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

package net.luis.utils.crypto;

import net.luis.utils.crypto.exception.CryptoException;
import net.luis.utils.crypto.exception.MalformedDataException;
import net.luis.utils.io.reader.StringReader;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NonNull;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.*;
import java.security.Key;
import java.security.PrivateKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.*;

/**
 * The PEM container format.<br>
 * <p>
 *     Written by hand rather than against the JDK preview encoder, so this module does not need preview features enabled.
 * </p>
 * <p>
 *     Decoding validates that the BEGIN and END labels match and returns the label,<br>
 *     so a caller that asked for a certificate cannot silently be handed a private key.
 * </p>
 * <p>
 *     Example:
 * </p>
 * <pre>{@code
 * String pem = Pem.encode(pair.getPublic());
 *
 * // The expected label is checked, so a private key never arrives where a public one belongs
 * Pem.Document document = Pem.decode(pem, Pem.PUBLIC_KEY);
 * PublicKey key = CryptoKeys.publicKey(KemAlgorithm.X25519_ML_KEM_768, document.der());
 *
 * // Created with owner only permissions wherever the file system supports them
 * Pem.write(Path.of("private.pem"), pair.getPrivate());
 * }</pre>
 *
 * @author Luis-St
 */
public final class Pem {
	
	/**
	 * The number of base64 characters per line.<br>
	 */
	private static final int LINE_LENGTH = 64;
	/**
	 * The marker opening a document.<br>
	 */
	private static final String BEGIN = "-----BEGIN ";
	/**
	 * The marker closing a document.<br>
	 */
	private static final String END = "-----END ";
	/**
	 * The marker terminating a label.<br>
	 */
	private static final String DASHES = "-----";
	/**
	 * The label of a public key document.<br>
	 */
	public static final String PUBLIC_KEY = "PUBLIC KEY";
	/**
	 * The label of a private key document.<br>
	 */
	public static final String PRIVATE_KEY = "PRIVATE KEY";
	/**
	 * The label of a certificate document.<br>
	 */
	public static final String CERTIFICATE = "CERTIFICATE";
	
	/**
	 * Private constructor to prevent instantiation.<br>
	 * This is a static helper class.<br>
	 */
	private Pem() {}
	
	/**
	 * Encodes the given body under the given label.<br>
	 *
	 * @param label The label to write
	 * @param der The body to encode
	 * @return The encoded document
	 * @throws NullPointerException If the label or the body is null
	 */
	public static @NonNull String encode(@NonNull String label, byte @NonNull [] der) {
		Objects.requireNonNull(label, "Label must not be null");
		Objects.requireNonNull(der, "Der must not be null");
		
		String body = Base64.getEncoder().encodeToString(der);
		StringBuilder builder = new StringBuilder(BEGIN).append(label).append(DASHES).append('\n');
		for (int offset = 0; offset < body.length(); offset += LINE_LENGTH) {
			builder.append(body, offset, Math.min(offset + LINE_LENGTH, body.length())).append('\n');
		}
		return builder.append(END).append(label).append(DASHES).append('\n').toString();
	}
	
	/**
	 * Encodes the given key under the label matching its kind.<br>
	 *
	 * @param key The key to encode
	 * @return The encoded document
	 * @throws NullPointerException If the key is null
	 */
	public static @NonNull String encode(@NonNull Key key) {
		Objects.requireNonNull(key, "Key must not be null");
		return encode(key instanceof PrivateKey ? PRIVATE_KEY : PUBLIC_KEY, key.getEncoded());
	}
	
	/**
	 * Encodes the given certificate.<br>
	 *
	 * @param certificate The certificate to encode
	 * @return The encoded document
	 * @throws NullPointerException If the certificate is null
	 * @throws CryptoException If the certificate cannot be encoded
	 */
	public static @NonNull String encode(@NonNull X509Certificate certificate) {
		Objects.requireNonNull(certificate, "Certificate must not be null");
		
		try {
			return encode(CERTIFICATE, certificate.getEncoded());
		} catch (CertificateEncodingException e) {
			throw new CryptoException("Cannot encode certificate", e);
		}
	}
	
	/**
	 * Decodes the first document in the given text.<br>
	 *
	 * @param pem The text to decode
	 * @return The first decoded document
	 * @throws NullPointerException If the text is null
	 * @throws MalformedDataException If the text holds no document, or a malformed one
	 */
	public static @NonNull Document decode(@NonNull String pem) {
		List<Document> documents = decodeAll(pem);
		if (documents.isEmpty()) {
			throw new MalformedDataException("Not a PEM document");
		}
		return documents.getFirst();
	}
	
	/**
	 * Decodes the first document in the given text and checks its label.<br>
	 *
	 * @param pem The text to decode
	 * @param expectedLabel The label the document must carry
	 * @return The first decoded document
	 * @throws NullPointerException If the text or the expected label is null
	 * @throws MalformedDataException If the text holds no document, a malformed one, or one with another label
	 */
	public static @NonNull Document decode(@NonNull String pem, @NonNull String expectedLabel) {
		Objects.requireNonNull(expectedLabel, "Expected label must not be null");
		
		Document document = decode(pem);
		if (!document.label().equals(expectedLabel)) {
			throw new MalformedDataException("Expected a '" + expectedLabel + "' PEM document, got '" + document.label() + "'");
		}
		return document;
	}
	
	/**
	 * Decodes every document in the given text, in order.<br>
	 * Text outside the markers is ignored, which is what lets a certificate chain be read as it is.<br>
	 *
	 * @param pem The text to decode
	 * @return Every decoded document, in order
	 * @throws NullPointerException If the text is null
	 * @throws MalformedDataException If a document is malformed or its BEGIN and END labels differ
	 */
	public static @NonNull @Unmodifiable List<Document> decodeAll(@NonNull String pem) {
		Objects.requireNonNull(pem, "Pem must not be null");
		
		StringReader reader = new StringReader(pem);
		List<Document> documents = new ArrayList<>();
		while (reader.canRead()) {
			reader.readUntil(BEGIN, true);
			if (!reader.canRead()) {
				break;
			}
			
			try {
				String label = reader.readUntil(DASHES, true);
				String body = reader.readUntil(END, true);
				reader.readExpected(label + DASHES, true);
				documents.add(new Document(label, Base64.getMimeDecoder().decode(body.replaceAll("\\s", ""))));
			} catch (MalformedDataException e) {
				throw e;
			} catch (RuntimeException e) {
				throw new MalformedDataException("Malformed PEM document at index " + reader.getIndex(), e);
			}
		}
		return List.copyOf(documents);
	}
	
	/**
	 * Writes the given key to the given file, readable only by its owner.<br>
	 * <p>
	 *     A new file is created with the restrictive permissions already in place, so there is no window in which the key is world readable.<br>
	 *     An existing file keeps whatever permissions it had until they are tightened after to write, so prefer writing to a path that does not exist yet.
	 * </p>
	 *
	 * @param file The file to write to
	 * @param key The key to write
	 * @throws NullPointerException If the file or the key is null
	 * @throws UncheckedIOException If writing fails
	 */
	public static void write(@NonNull Path file, @NonNull Key key) {
		Objects.requireNonNull(file, "File must not be null");
		Objects.requireNonNull(key, "Key must not be null");
		
		try {
			boolean posix = Files.getFileStore(file.toAbsolutePath().getParent()).supportsFileAttributeView(PosixFileAttributeView.class);
			if (posix && Files.notExists(file)) {
				FileAttribute<?> attribute = PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString("rw-------"));
				Files.createFile(file, attribute);
			}
			Files.writeString(file, encode(key), StandardCharsets.US_ASCII);
			if (posix) {
				Files.setPosixFilePermissions(file, PosixFilePermissions.fromString("rw-------"));
			}
		} catch (IOException e) {
			throw new UncheckedIOException("Failed to write the pem file: " + file, e);
		}
	}
	
	/**
	 * Reads the first document from the given file.<br>
	 *
	 * @param file The file to read
	 * @return The first decoded document
	 * @throws NullPointerException If the file is null
	 * @throws MalformedDataException If the file holds no document, or a malformed one
	 * @throws UncheckedIOException If reading fails
	 */
	public static @NonNull Document read(@NonNull Path file) {
		Objects.requireNonNull(file, "File must not be null");
		
		try {
			return decode(Files.readString(file, StandardCharsets.US_ASCII));
		} catch (IOException e) {
			throw new UncheckedIOException("Failed to read the pem file: " + file, e);
		}
	}
	
	/**
	 * A decoded PEM document: its label and its DER body.<br>
	 *
	 * @author Luis-St
	 *
	 * @param label The label between the BEGIN and END markers
	 * @param der The decoded body
	 */
	public record Document(
		@NonNull String label,
		byte @NonNull [] der
	) {
		
		/**
		 * Constructs a new document.<br>
		 * @throws NullPointerException If the label or the body is null
		 */
		public Document {
			Objects.requireNonNull(label, "Label must not be null");
			Objects.requireNonNull(der, "Der must not be null");
		}
	}
}
