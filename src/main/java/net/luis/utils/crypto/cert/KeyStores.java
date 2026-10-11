/*
 * LUtils
 * Copyright (C) 2026 Luis Staudt
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package net.luis.utils.crypto.cert;

import net.luis.utils.crypto.exception.*;
import org.jspecify.annotations.NonNull;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.*;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;

/**
 * PKCS#12 key stores, the file format servers and the JDK keep a private key and its certificate chain in.<br>
 * <p>
 *     The stores are written with the defaults of the JDK, which protect the key with PBES2 and AES-256 and the whole file with an HMAC-SHA256 integrity check.<br>
 *     OpenSSL 1.1.1 and later read these files.
 * </p>
 * <p>
 *     Example:
 * </p>
 * <pre>{@code
 * KeyStore store = KeyStores.pkcs12(serverKey, List.of(server, ca.certificate()), password);
 * KeyStores.save(store, Path.of("server.p12"), password);
 *
 * KeyStore loaded = KeyStores.load(Path.of("server.p12"), password);
 * Optional<KeyStore.PrivateKeyEntry> entry = KeyStores.entry(loaded, KeyStores.DEFAULT_ALIAS, password);
 * }</pre>
 *
 * @author Luis-St
 */
public final class KeyStores {
	
	/**
	 * The type of the key stores this class creates and reads.<br>
	 */
	private static final String PKCS12 = "PKCS12";
	/**
	 * The alias the overloads without an alias store the key under.<br>
	 */
	public static final String DEFAULT_ALIAS = "key";
	
	/**
	 * Private constructor to prevent instantiation.<br>
	 * This is a static helper class.<br>
	 */
	private KeyStores() {}
	
	/**
	 * Creates an in-memory PKCS#12 key store holding the given key and chain under {@link #DEFAULT_ALIAS}.<br>
	 *
	 * @param key The private key to store
	 * @param chain The certificate chain of the key, leaf first
	 * @param password The password protecting the key
	 * @return The created key store
	 * @throws NullPointerException If any parameter is null, or the chain holds null
	 * @throws IllegalArgumentException If the chain is empty
	 * @throws CryptoException If the key cannot be stored
	 */
	public static @NonNull KeyStore pkcs12(@NonNull PrivateKey key, @NonNull List<X509Certificate> chain, char @NonNull [] password) {
		return pkcs12(DEFAULT_ALIAS, key, chain, password);
	}
	
	/**
	 * Creates an in-memory PKCS#12 key store holding the given key and chain under the given alias.<br>
	 * The chain must start with the certificate of the key, the key store does not check that it does.<br>
	 *
	 * @param alias The alias to store the key under
	 * @param key The private key to store
	 * @param chain The certificate chain of the key, leaf first
	 * @param password The password protecting the key
	 * @return The created key store
	 * @throws NullPointerException If any parameter is null, or the chain holds null
	 * @throws IllegalArgumentException If the chain is empty
	 * @throws CryptoException If the key cannot be stored
	 */
	public static @NonNull KeyStore pkcs12(@NonNull String alias, @NonNull PrivateKey key, @NonNull List<X509Certificate> chain, char @NonNull [] password) {
		Objects.requireNonNull(alias, "Alias must not be null");
		Objects.requireNonNull(key, "Key must not be null");
		Objects.requireNonNull(chain, "Chain must not be null");
		Objects.requireNonNull(password, "Password must not be null");
		List<X509Certificate> certificates = List.copyOf(chain);
		if (certificates.isEmpty()) {
			throw new IllegalArgumentException("Chain must not be empty");
		}
		
		KeyStore store = empty();
		try {
			store.setKeyEntry(alias, key, password, certificates.toArray(X509Certificate[]::new));
			return store;
		} catch (KeyStoreException e) {
			throw new CryptoException("Cannot store a " + key.getAlgorithm() + " key", e);
		}
	}
	
	/**
	 * Loads a key store from the given file.<br>
	 * The type is detected from the content, so a JKS file is read as well as a PKCS#12 file.<br>
	 *
	 * @param file The file to load
	 * @param password The password of the key store
	 * @return The loaded key store
	 * @throws NullPointerException If the file or the password is null
	 * @throws AuthenticationException If the password is wrong
	 * @throws MalformedDataException If the file is not a key store
	 * @throws UncheckedIOException If reading fails
	 */
	public static @NonNull KeyStore load(@NonNull Path file, char @NonNull [] password) {
		Objects.requireNonNull(file, "File must not be null");
		Objects.requireNonNull(password, "Password must not be null");
		
		try {
			return KeyStore.getInstance(file.toFile(), password);
		} catch (IOException e) {
			if (e.getCause() instanceof UnrecoverableKeyException) {
				throw new AuthenticationException("Cannot open key store " + file + ", the password is wrong", e);
			}
			throw new UncheckedIOException("Failed to read the key store: " + file, e);
		} catch (KeyStoreException | CertificateException | NoSuchAlgorithmException e) {
			throw new MalformedDataException("Cannot read key store " + file, e);
		}
	}
	
	/**
	 * Saves the given key store to the given file, readable only by its owner.<br>
	 * The permissions are handled as the private key writer of {@link net.luis.utils.crypto.Pems} handles them.<br>
	 *
	 * @param store The key store to save
	 * @param file The file to write to
	 * @param password The password protecting the file
	 * @throws NullPointerException If any parameter is null
	 * @throws CryptoException If the key store cannot be written
	 * @throws UncheckedIOException If writing fails
	 */
	public static void save(@NonNull KeyStore store, @NonNull Path file, char @NonNull [] password) {
		Objects.requireNonNull(store, "Key store must not be null");
		Objects.requireNonNull(file, "File must not be null");
		Objects.requireNonNull(password, "Password must not be null");
		
		try {
			boolean posix = Files.getFileStore(file.toAbsolutePath().getParent()).supportsFileAttributeView(PosixFileAttributeView.class);
			if (posix && Files.notExists(file)) {
				FileAttribute<?> attribute = PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString("rw-------"));
				Files.createFile(file, attribute);
			}
			
			try (OutputStream output = Files.newOutputStream(file)) {
				store.store(output, password);
			}
			
			if (posix) {
				Files.setPosixFilePermissions(file, PosixFilePermissions.fromString("rw-------"));
			}
		} catch (IOException e) {
			throw new UncheckedIOException("Failed to write the key store: " + file, e);
		} catch (GeneralSecurityException e) {
			throw new CryptoException("Cannot write key store " + file, e);
		}
	}
	
	/**
	 * Returns the private key entry stored under the given alias.<br>
	 * <p>
	 *     An alias that does not exist or holds something other than a private key yields an empty result.<br>
	 *     A wrong password is not treated as absence, it throws.
	 * </p>
	 *
	 * @param store The key store to read, which must be loaded
	 * @param alias The alias to read
	 * @param password The password protecting the entry
	 * @return The private key entry, or empty if the alias holds none
	 * @throws NullPointerException If any parameter is null
	 * @throws AuthenticationException If the password is wrong
	 * @throws CryptoException If the key store is not loaded, or the entry cannot be read
	 */
	public static @NonNull Optional<KeyStore.PrivateKeyEntry> entry(@NonNull KeyStore store, @NonNull String alias, char @NonNull [] password) {
		Objects.requireNonNull(store, "Key store must not be null");
		Objects.requireNonNull(alias, "Alias must not be null");
		Objects.requireNonNull(password, "Password must not be null");
		
		try {
			if (!store.entryInstanceOf(alias, KeyStore.PrivateKeyEntry.class)) {
				return Optional.empty();
			}
			return Optional.of((KeyStore.PrivateKeyEntry) store.getEntry(alias, new KeyStore.PasswordProtection(password)));
		} catch (UnrecoverableEntryException e) {
			throw new AuthenticationException("Cannot read entry " + alias + ", the password is wrong", e);
		} catch (KeyStoreException | NoSuchAlgorithmException e) {
			throw new CryptoException("Cannot read entry " + alias, e);
		}
	}
	
	/**
	 * Creates an empty, loaded PKCS#12 key store.<br>
	 *
	 * @return The created key store
	 * @throws CryptoException If the runtime provides no PKCS#12 key store
	 */
	private static @NonNull KeyStore empty() {
		try {
			KeyStore store = KeyStore.getInstance(PKCS12);
			store.load(null, null);
			return store;
		} catch (GeneralSecurityException | IOException e) {
			throw new CryptoException("Cannot create a PKCS#12 key store", e);
		}
	}
}
