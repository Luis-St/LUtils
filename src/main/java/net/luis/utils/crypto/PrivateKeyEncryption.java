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

import net.luis.utils.crypto.exception.*;
import net.luis.utils.crypto.key.Secret;
import net.luis.utils.crypto.util.CryptoBytes;
import net.luis.utils.crypto.util.CryptoRandom;
import org.bouncycastle.asn1.*;
import org.bouncycastle.asn1.misc.MiscObjectIdentifiers;
import org.bouncycastle.asn1.misc.ScryptParams;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.*;
import org.bouncycastle.asn1.pkcs.EncryptedPrivateKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.generators.SCrypt;
import org.jspecify.annotations.NonNull;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Objects;

/**
 * Password based encryption of private keys in the PKCS#8 {@code EncryptedPrivateKeyInfo} format.<br>
 * <p>
 *     Keys are written with PBES2, deriving an AES-256-CBC key through PBKDF2 with HMAC-SHA256 at 600000 iterations.<br>
 *     This is the format {@code openssl pkcs8 -topk8 -v2 aes-256-cbc} writes, so a key written here is read by OpenSSL and the other way round.
 * </p>
 * <p>
 *     Reading also accepts the other PBES2 combinations OpenSSL writes, which are PBKDF2 with any HMAC-SHA1 or HMAC-SHA2 function or scrypt, together with AES-128, AES-192 or AES-256 in CBC mode.<br>
 *     The legacy PBES1 schemes are rejected.<br>
 *     They rest on DES and RC2.
 * </p>
 * <p>
 *     The password is converted to bytes as UTF-8, which matches OpenSSL on any system with a UTF-8 locale.<br>
 *     The JDK's own PBE ciphers are not used, because they reject a password with characters outside printable ASCII.
 * </p>
 * <p>
 *     CBC carries no authentication tag.<br>
 *     A wrong password is detected through the padding almost always, and the rare wrong password that happens to produce valid padding yields garbage that fails when it is decoded as a key.
 * </p>
 *
 * @author Luis-St
 */
final class PrivateKeyEncryption {
	
	/**
	 * The PBKDF2 iteration count keys are written with, the OWASP recommendation for HMAC-SHA256.<br>
	 */
	private static final int ITERATIONS = 600_000;
	/**
	 * The length of the salt keys are written with in bytes.<br>
	 */
	private static final int SALT_LENGTH = 16;
	/**
	 * The length of an AES-CBC initialization vector in bytes.<br>
	 */
	private static final int IV_LENGTH = 16;
	/**
	 * The length of an AES-256 key in bytes.<br>
	 */
	private static final int KEY_LENGTH = 32;
	/**
	 * The JCA names of the PBKDF2 functions by the object identifier of their pseudorandom function.<br>
	 */
	private static final Map<ASN1ObjectIdentifier, String> PBKDF2_BY_PRF = Map.of(
		PKCSObjectIdentifiers.id_hmacWithSHA1, "PBKDF2WithHmacSHA1",
		PKCSObjectIdentifiers.id_hmacWithSHA224, "PBKDF2WithHmacSHA224",
		PKCSObjectIdentifiers.id_hmacWithSHA256, "PBKDF2WithHmacSHA256",
		PKCSObjectIdentifiers.id_hmacWithSHA384, "PBKDF2WithHmacSHA384",
		PKCSObjectIdentifiers.id_hmacWithSHA512, "PBKDF2WithHmacSHA512"
	);
	/**
	 * The AES key lengths in bytes by the object identifier of their CBC mode.<br>
	 */
	private static final Map<ASN1ObjectIdentifier, Integer> AES_KEY_LENGTHS = Map.of(
		NISTObjectIdentifiers.id_aes128_CBC, 16,
		NISTObjectIdentifiers.id_aes192_CBC, 24,
		NISTObjectIdentifiers.id_aes256_CBC, 32
	);
	
	/**
	 * Private constructor to prevent instantiation.<br>
	 * This is a static helper class.<br>
	 */
	private PrivateKeyEncryption() {}
	
	/**
	 * Encrypts the given encoded private key under the given password.<br>
	 *
	 * @param encodedKey The encoded private key
	 * @param password The password to encrypt under
	 * @return The DER encoded {@code EncryptedPrivateKeyInfo}
	 * @throws NullPointerException If the encoded key or the password is null
	 * @throws CryptoException If the encryption fails
	 */
	static byte @NonNull [] encrypt(byte @NonNull [] encodedKey, char @NonNull [] password) {
		Objects.requireNonNull(encodedKey, "Encoded key must not be null");
		Objects.requireNonNull(password, "Password must not be null");
		
		byte[] salt = CryptoRandom.bytes(SALT_LENGTH);
		byte[] iv = CryptoRandom.bytes(IV_LENGTH);
		AlgorithmIdentifier prf = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_hmacWithSHA256, DERNull.INSTANCE);
		
		byte[] ciphertext;
		try (Secret key = pbkdf2(PBKDF2_BY_PRF.get(PKCSObjectIdentifiers.id_hmacWithSHA256), password, salt, ITERATIONS, KEY_LENGTH)) {
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, key.toKey("AES"), new IvParameterSpec(iv));
			ciphertext = cipher.doFinal(encodedKey);
		} catch (GeneralSecurityException e) {
			throw new CryptoException("Cannot encrypt private key", e);
		}
		
		PBES2Parameters parameters = new PBES2Parameters(
			new KeyDerivationFunc(PKCSObjectIdentifiers.id_PBKDF2, new PBKDF2Params(salt, ITERATIONS, KEY_LENGTH, prf)),
			new EncryptionScheme(NISTObjectIdentifiers.id_aes256_CBC, new DEROctetString(iv))
		);
		try {
			return new EncryptedPrivateKeyInfo(new AlgorithmIdentifier(PKCSObjectIdentifiers.id_PBES2, parameters), ciphertext).getEncoded(ASN1Encoding.DER);
		} catch (IOException e) {
			throw new CryptoException("Cannot encode encrypted private key", e);
		}
	}
	
	/**
	 * Decrypts the given {@code EncryptedPrivateKeyInfo} with the given password.<br>
	 *
	 * @param der The DER encoded {@code EncryptedPrivateKeyInfo}
	 * @param password The password to decrypt with
	 * @return The decrypted encoded private key
	 * @throws NullPointerException If the data or the password is null
	 * @throws MalformedDataException If the data is not an encrypted private key
	 * @throws UnsupportedAlgorithmException If the key is encrypted with a scheme other than PBES2 with AES-CBC
	 * @throws AuthenticationException If the password is wrong
	 */
	static @NonNull Secret decrypt(byte @NonNull [] der, char @NonNull [] password) {
		Objects.requireNonNull(der, "Encrypted key must not be null");
		Objects.requireNonNull(password, "Password must not be null");
		
		EncryptedPrivateKeyInfo info;
		PBES2Parameters parameters;
		byte[] iv;
		try {
			info = EncryptedPrivateKeyInfo.getInstance(der);
			if (!PKCSObjectIdentifiers.id_PBES2.equals(info.getEncryptionAlgorithm().getAlgorithm())) {
				throw new UnsupportedAlgorithmException("Private key is encrypted with " + info.getEncryptionAlgorithm().getAlgorithm() + ", only PBES2 is supported");
			}
			parameters = PBES2Parameters.getInstance(info.getEncryptionAlgorithm().getParameters());
			iv = ASN1OctetString.getInstance(parameters.getEncryptionScheme().getParameters()).getOctets();
		} catch (IllegalArgumentException | IllegalStateException e) {
			throw new MalformedDataException("Malformed encrypted private key", e);
		}
		
		ASN1ObjectIdentifier cipherOid = parameters.getEncryptionScheme().getAlgorithm();
		Integer keyLength = AES_KEY_LENGTHS.get(cipherOid);
		if (keyLength == null) {
			throw new UnsupportedAlgorithmException("Private key is encrypted with " + cipherOid + ", only AES-CBC is supported");
		}
		
		try (Secret key = deriveKey(parameters.getKeyDerivationFunc(), password, keyLength)) {
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, key.toKey("AES"), new IvParameterSpec(iv));
			return Secret.adopt(cipher.doFinal(info.getEncryptedData()));
		} catch (BadPaddingException e) {
			throw new AuthenticationException("Cannot decrypt private key, the password is wrong or the data is corrupted", e);
		} catch (GeneralSecurityException e) {
			throw new CryptoException("Cannot decrypt private key", e);
		}
	}
	
	/**
	 * Derives the content encryption key as the given key derivation function describes.<br>
	 *
	 * @param function The key derivation function of the PBES2 parameters
	 * @param password The password to derive from
	 * @param keyLength The length of the key to derive in bytes
	 * @return The derived key
	 * @throws NullPointerException If the function or the password is null
	 * @throws MalformedDataException If the function parameters are malformed
	 * @throws UnsupportedAlgorithmException If the function is neither PBKDF2 nor scrypt, or uses an unknown pseudorandom function
	 */
	private static @NonNull Secret deriveKey(@NonNull KeyDerivationFunc function, char @NonNull [] password, int keyLength) {
		Objects.requireNonNull(function, "Function must not be null");
		Objects.requireNonNull(password, "Password must not be null");
		
		ASN1ObjectIdentifier algorithm = function.getAlgorithm();
		try {
			if (PKCSObjectIdentifiers.id_PBKDF2.equals(algorithm)) {
				PBKDF2Params parameters = PBKDF2Params.getInstance(function.getParameters());
				String jcaName = PBKDF2_BY_PRF.get(parameters.getPrf().getAlgorithm());
				if (jcaName == null) {
					throw new UnsupportedAlgorithmException("PBKDF2 with " + parameters.getPrf().getAlgorithm() + " is not supported");
				}
				if (parameters.getKeyLength() != null && parameters.getKeyLength().intValueExact() != keyLength) {
					throw new MalformedDataException("PBKDF2 key length does not match the cipher");
				}
				return pbkdf2(jcaName, password, parameters.getSalt(), parameters.getIterationCount().intValueExact(), keyLength);
			}
			if (MiscObjectIdentifiers.id_scrypt.equals(algorithm)) {
				ScryptParams parameters = ScryptParams.getInstance(function.getParameters());
				byte[] passwordBytes = CryptoBytes.toBytes(password, StandardCharsets.UTF_8);
				try {
					return Secret.adopt(SCrypt.generate(
						passwordBytes, parameters.getSalt(), parameters.getCostParameter().intValueExact(), parameters.getBlockSize().intValueExact(), parameters.getParallelizationParameter().intValueExact(), keyLength
					));
				} finally {
					CryptoBytes.wipe(passwordBytes);
				}
			}
		} catch (ArithmeticException | IllegalArgumentException e) {
			throw new MalformedDataException("Malformed key derivation parameters", e);
		}
		throw new UnsupportedAlgorithmException("Key derivation function " + algorithm + " is not supported");
	}
	
	/**
	 * Derives a key through PBKDF2.<br>
	 *
	 * @param jcaName The JCA name of the PBKDF2 function
	 * @param password The password to derive from
	 * @param salt The salt
	 * @param iterations The iteration count
	 * @param keyLength The length of the key to derive in bytes
	 * @return The derived key
	 * @throws NullPointerException If the name, the password or the salt is null
	 * @throws UnsupportedAlgorithmException If no registered provider serves the function
	 * @throws CryptoException If the derivation fails
	 */
	private static @NonNull Secret pbkdf2(@NonNull String jcaName, char @NonNull [] password, byte @NonNull [] salt, int iterations, int keyLength) {
		Objects.requireNonNull(jcaName, "Jca name must not be null");
		Objects.requireNonNull(password, "Password must not be null");
		Objects.requireNonNull(salt, "Salt must not be null");
		
		PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, keyLength * 8);
		try {
			return Secret.adopt(SecretKeyFactory.getInstance(jcaName).generateSecret(spec).getEncoded());
		} catch (NoSuchAlgorithmException e) {
			throw UnsupportedAlgorithmException.forAlgorithm(jcaName, e);
		} catch (GeneralSecurityException e) {
			throw new CryptoException("Cannot derive key through " + jcaName, e);
		} finally {
			spec.clearPassword();
		}
	}
}
