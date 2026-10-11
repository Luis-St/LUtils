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
import org.bouncycastle.asn1.*;
import org.bouncycastle.asn1.misc.MiscObjectIdentifiers;
import org.bouncycastle.asn1.misc.ScryptParams;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.*;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.bouncycastle.crypto.params.KeyParameter;
import org.junit.jupiter.api.Test;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link PrivateKeyEncryption}.<br>
 *
 * @author Luis-St
 */
class PrivateKeyEncryptionTest {
	
	private static final char[] OPENSSL_PASSWORD = "password".toCharArray();
	private static final byte[] OPENSSL_KEY = hex("302e020100300506032b65700422042004dc2ee21f02f733d32df0ad7168ced94bc13863b72cf2135db2e0ef742d3ff2");
	private static final byte[] OPENSSL_AES256_SHA256 = hex(
		"3081a3305f06092a864886f70d01050d3052303106092a864886f70d01050c302404107456960ba435c894471ac030b0d5471302020800300c06082a864886f70d02090500301d060960864801650304012a0410004e5987bfa319b0f51f90f24d7973e0044069d68e7af1c6427f9f88f96be5df774069e53f1385ef1bfc2e326f3a89aeac6076ed3085b6462c5d4c509c04cc49989c3ed8ce3328c43f9b61679940a43fa015"
	);
	private static final byte[] OPENSSL_AES128_DEFAULT_PRF = hex(
		"308195305106092a864886f70d01050d3044302306092a864886f70d01050c30160410aae18f7314d7263105b15fe9e3881bfa02020800301d060960864801650304010204103ea2dd7da654278576a5b21831d7b9d70440c526fc72c4bf201a5f8f5ba354e67d14ce9bed57bc1dbf5789a34045bc3b047f481d4db3fc750b1c31f447a9afaf9d4e8a6efa2b9e68b6a7895b28f085ecea97"
	);
	private static final byte[] OPENSSL_SCRYPT = hex(
		"30819b305706092a864886f70d01050d304a302906092b06010401da47040b301c041016d742492ba1724e70248c97cbe05f8b02020400020108020101301d060960864801650304012a0410aeafe11167eed482e4c51ff97c738d1b044006fba8e851d547433deeb4f2044dfe6a4d10b7d0f05def3e83437bc1c05925b388765f0309226157b64589e79e9555b655f74d2d52916859edcff513abd9fd43"
	);
	private static final byte[] OPENSSL_PBES1 = hex(
		"3058301c060a2a864886f70d010c0103300e0408160b01824e967d590202080004387cef92a572e37ba88e07c5fb78ee89b0de0a390f35d6657843a2240ae12aacd3ef59332d783b21fb005cff7a7ace163e3f2410d6679f6a99"
	);
	private static final byte[] SALT = hex("000102030405060708090a0b0c0d0e0f");
	private static final byte[] IV = hex("f0f1f2f3f4f5f6f7f8f9fafbfcfdfeff");
	private static final int CRAFTED_ITERATIONS = 1000;
	private static final AlgorithmIdentifier HMAC_SHA256 = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_hmacWithSHA256, DERNull.INSTANCE);
	private static final byte[] KEY = "a private key that is not really one".getBytes(StandardCharsets.US_ASCII);
	
	private static byte[] hex(String value) {
		return HexFormat.of().parseHex(value);
	}
	
	private static KeyDerivationFunc pbkdf2(ASN1Encodable parameters) {
		return new KeyDerivationFunc(PKCSObjectIdentifiers.id_PBKDF2, parameters);
	}
	
	private static byte[] info(KeyDerivationFunc function, ASN1ObjectIdentifier cipher, ASN1Encodable cipherParameters, byte[] data) throws Exception {
		PBES2Parameters parameters = new PBES2Parameters(function, new EncryptionScheme(cipher, cipherParameters));
		return new EncryptedPrivateKeyInfo(new AlgorithmIdentifier(PKCSObjectIdentifiers.id_PBES2, parameters), data).getEncoded(ASN1Encoding.DER);
	}
	
	private static byte[] crafted(String jcaName, ASN1ObjectIdentifier prf, ASN1ObjectIdentifier cipherOid, int keyLength, char[] password, byte[] plaintext) throws Exception {
		byte[] key = SecretKeyFactory.getInstance(jcaName).generateSecret(new PBEKeySpec(password, SALT, CRAFTED_ITERATIONS, keyLength * 8)).getEncoded();
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"), new IvParameterSpec(IV));
		PBKDF2Params parameters = new PBKDF2Params(SALT, CRAFTED_ITERATIONS, keyLength, new AlgorithmIdentifier(prf, DERNull.INSTANCE));
		return info(pbkdf2(parameters), cipherOid, new DEROctetString(IV), cipher.doFinal(plaintext));
	}
	
	@Test
	void constructorIsPrivate() throws Exception {
		Constructor<?>[] constructors = PrivateKeyEncryption.class.getDeclaredConstructors();
		assertEquals(1, constructors.length);
		assertTrue(Modifier.isPrivate(constructors[0].getModifiers()));
		assertTrue(Modifier.isFinal(PrivateKeyEncryption.class.getModifiers()));
		
		Constructor<PrivateKeyEncryption> constructor = PrivateKeyEncryption.class.getDeclaredConstructor();
		constructor.setAccessible(true);
		assertNotNull(constructor.newInstance());
	}
	
	@Test
	void encryptWithNullKey() {
		assertEquals("Encoded key must not be null", assertThrows(NullPointerException.class, () -> PrivateKeyEncryption.encrypt(null, OPENSSL_PASSWORD)).getMessage());
	}
	
	@Test
	void encryptWithNullPassword() {
		assertEquals("Password must not be null", assertThrows(NullPointerException.class, () -> PrivateKeyEncryption.encrypt(KEY, null)).getMessage());
	}
	
	@Test
	void decryptWithNullData() {
		assertEquals("Encrypted key must not be null", assertThrows(NullPointerException.class, () -> PrivateKeyEncryption.decrypt(null, OPENSSL_PASSWORD)).getMessage());
	}
	
	@Test
	void decryptWithNullPassword() {
		assertEquals("Password must not be null", assertThrows(NullPointerException.class, () -> PrivateKeyEncryption.decrypt(OPENSSL_AES256_SHA256, null)).getMessage());
	}
	
	@Test
	void decryptWithGarbageData() {
		assertThrows(MalformedDataException.class, () -> PrivateKeyEncryption.decrypt(new byte[] { 1, 2, 3, 4, 5 }, OPENSSL_PASSWORD));
		assertThrows(MalformedDataException.class, () -> PrivateKeyEncryption.decrypt(OPENSSL_KEY, OPENSSL_PASSWORD));
	}
	
	@Test
	void decryptWithEmptyData() {
		assertThrows(MalformedDataException.class, () -> PrivateKeyEncryption.decrypt(new byte[0], OPENSSL_PASSWORD));
	}
	
	@Test
	void decryptWithLegacyPbes1Scheme() {
		UnsupportedAlgorithmException exception = assertThrows(UnsupportedAlgorithmException.class, () -> PrivateKeyEncryption.decrypt(OPENSSL_PBES1, OPENSSL_PASSWORD));
		assertTrue(exception.getMessage().contains(PKCSObjectIdentifiers.pbeWithSHAAnd3_KeyTripleDES_CBC.getId()));
		assertTrue(exception.getMessage().contains("only PBES2 is supported"));
	}
	
	@Test
	void decryptWithMalformedPbes2Parameters() throws Exception {
		byte[] der = new EncryptedPrivateKeyInfo(new AlgorithmIdentifier(PKCSObjectIdentifiers.id_PBES2, DERNull.INSTANCE), new byte[16]).getEncoded(ASN1Encoding.DER);
		assertThrows(MalformedDataException.class, () -> PrivateKeyEncryption.decrypt(der, OPENSSL_PASSWORD));
	}
	
	@Test
	void decryptWithMalformedIv() throws Exception {
		byte[] der = info(pbkdf2(new PBKDF2Params(SALT, CRAFTED_ITERATIONS, HMAC_SHA256)), NISTObjectIdentifiers.id_aes256_CBC, DERNull.INSTANCE, new byte[16]);
		assertThrows(MalformedDataException.class, () -> PrivateKeyEncryption.decrypt(der, OPENSSL_PASSWORD));
	}
	
	@Test
	void decryptWithUnsupportedCipher() throws Exception {
		byte[] der = info(pbkdf2(new PBKDF2Params(SALT, CRAFTED_ITERATIONS, HMAC_SHA256)), PKCSObjectIdentifiers.des_EDE3_CBC, new DEROctetString(new byte[8]), new byte[16]);
		UnsupportedAlgorithmException exception = assertThrows(UnsupportedAlgorithmException.class, () -> PrivateKeyEncryption.decrypt(der, OPENSSL_PASSWORD));
		assertTrue(exception.getMessage().contains(PKCSObjectIdentifiers.des_EDE3_CBC.getId()));
		assertTrue(exception.getMessage().contains("only AES-CBC is supported"));
	}
	
	@Test
	void decryptWithUnsupportedPrf() throws Exception {
		AlgorithmIdentifier prf = new AlgorithmIdentifier(new ASN1ObjectIdentifier("1.2.840.113549.2.12"), DERNull.INSTANCE);
		byte[] der = info(pbkdf2(new PBKDF2Params(SALT, CRAFTED_ITERATIONS, prf)), NISTObjectIdentifiers.id_aes256_CBC, new DEROctetString(IV), new byte[16]);
		UnsupportedAlgorithmException exception = assertThrows(UnsupportedAlgorithmException.class, () -> PrivateKeyEncryption.decrypt(der, OPENSSL_PASSWORD));
		assertTrue(exception.getMessage().contains("1.2.840.113549.2.12"));
	}
	
	@Test
	void decryptWithMismatchedKeyLength() throws Exception {
		byte[] der = info(pbkdf2(new PBKDF2Params(SALT, CRAFTED_ITERATIONS, 16, HMAC_SHA256)), NISTObjectIdentifiers.id_aes256_CBC, new DEROctetString(IV), new byte[16]);
		MalformedDataException exception = assertThrows(MalformedDataException.class, () -> PrivateKeyEncryption.decrypt(der, OPENSSL_PASSWORD));
		assertEquals("PBKDF2 key length does not match the cipher", exception.getMessage());
	}
	
	@Test
	void decryptWithIterationCountBeyondInt() throws Exception {
		DERSequence parameters = new DERSequence(new ASN1Encodable[] { new DEROctetString(SALT), new ASN1Integer(BigInteger.ONE.shiftLeft(40)), HMAC_SHA256 });
		byte[] der = info(pbkdf2(parameters), NISTObjectIdentifiers.id_aes256_CBC, new DEROctetString(IV), new byte[16]);
		MalformedDataException exception = assertThrows(MalformedDataException.class, () -> PrivateKeyEncryption.decrypt(der, OPENSSL_PASSWORD));
		assertInstanceOf(ArithmeticException.class, exception.getCause());
	}
	
	@Test
	void decryptWithZeroIterationCount() throws Exception {
		DERSequence parameters = new DERSequence(new ASN1Encodable[] { new DEROctetString(SALT), new ASN1Integer(0), HMAC_SHA256 });
		byte[] der = info(pbkdf2(parameters), NISTObjectIdentifiers.id_aes256_CBC, new DEROctetString(IV), new byte[16]);
		MalformedDataException exception = assertThrows(MalformedDataException.class, () -> PrivateKeyEncryption.decrypt(der, OPENSSL_PASSWORD));
		assertInstanceOf(IllegalArgumentException.class, exception.getCause());
	}
	
	@Test
	void decryptWithInvalidScryptCost() throws Exception {
		KeyDerivationFunc function = new KeyDerivationFunc(MiscObjectIdentifiers.id_scrypt, new ScryptParams(SALT, 1000, 8, 1));
		byte[] der = info(function, NISTObjectIdentifiers.id_aes256_CBC, new DEROctetString(IV), new byte[16]);
		MalformedDataException exception = assertThrows(MalformedDataException.class, () -> PrivateKeyEncryption.decrypt(der, OPENSSL_PASSWORD));
		assertInstanceOf(IllegalArgumentException.class, exception.getCause());
	}
	
	@Test
	void decryptWithUnsupportedKeyDerivationFunction() throws Exception {
		KeyDerivationFunc function = new KeyDerivationFunc(new ASN1ObjectIdentifier("1.2.3.4"), DERNull.INSTANCE);
		byte[] der = info(function, NISTObjectIdentifiers.id_aes256_CBC, new DEROctetString(IV), new byte[16]);
		UnsupportedAlgorithmException exception = assertThrows(UnsupportedAlgorithmException.class, () -> PrivateKeyEncryption.decrypt(der, OPENSSL_PASSWORD));
		assertEquals("Key derivation function 1.2.3.4 is not supported", exception.getMessage());
	}
	
	@Test
	void decryptWithWrongPassword() {
		AuthenticationException exception = assertThrows(AuthenticationException.class, () -> PrivateKeyEncryption.decrypt(OPENSSL_AES256_SHA256, "wrong".toCharArray()));
		assertEquals("Cannot decrypt private key, the password is wrong or the data is corrupted", exception.getMessage());
	}
	
	@Test
	void decryptWithTruncatedCiphertext() throws Exception {
		byte[] der = info(pbkdf2(new PBKDF2Params(SALT, CRAFTED_ITERATIONS, HMAC_SHA256)), NISTObjectIdentifiers.id_aes256_CBC, new DEROctetString(IV), new byte[5]);
		CryptoException exception = assertThrows(CryptoException.class, () -> PrivateKeyEncryption.decrypt(der, OPENSSL_PASSWORD));
		assertEquals(CryptoException.class, exception.getClass());
		assertEquals("Cannot decrypt private key", exception.getMessage());
	}
	
	@Test
	void decryptOpenSslAes256WithPbkdf2Sha256() {
		try (Secret secret = PrivateKeyEncryption.decrypt(OPENSSL_AES256_SHA256, OPENSSL_PASSWORD)) {
			assertArrayEquals(OPENSSL_KEY, secret.material());
		}
	}
	
	@Test
	void decryptOpenSslAes128WithDefaultPrf() {
		try (Secret secret = PrivateKeyEncryption.decrypt(OPENSSL_AES128_DEFAULT_PRF, OPENSSL_PASSWORD)) {
			assertArrayEquals(OPENSSL_KEY, secret.material());
		}
	}
	
	@Test
	void decryptOpenSslScrypt() {
		try (Secret secret = PrivateKeyEncryption.decrypt(OPENSSL_SCRYPT, OPENSSL_PASSWORD)) {
			assertArrayEquals(OPENSSL_KEY, secret.material());
		}
	}
	
	@Test
	void encryptWritesOpenSslCompatibleStructure() {
		EncryptedPrivateKeyInfo info = EncryptedPrivateKeyInfo.getInstance(PrivateKeyEncryption.encrypt(OPENSSL_KEY, OPENSSL_PASSWORD));
		assertEquals(PKCSObjectIdentifiers.id_PBES2, info.getEncryptionAlgorithm().getAlgorithm());
		PBES2Parameters parameters = PBES2Parameters.getInstance(info.getEncryptionAlgorithm().getParameters());
		assertEquals(PKCSObjectIdentifiers.id_PBKDF2, parameters.getKeyDerivationFunc().getAlgorithm());
		
		PBKDF2Params kdf = PBKDF2Params.getInstance(parameters.getKeyDerivationFunc().getParameters());
		assertEquals(16, kdf.getSalt().length);
		assertEquals(BigInteger.valueOf(600_000), kdf.getIterationCount());
		assertEquals(BigInteger.valueOf(32), kdf.getKeyLength());
		assertEquals(PKCSObjectIdentifiers.id_hmacWithSHA256, kdf.getPrf().getAlgorithm());
		
		assertEquals(NISTObjectIdentifiers.id_aes256_CBC, parameters.getEncryptionScheme().getAlgorithm());
		assertEquals(16, ASN1OctetString.getInstance(parameters.getEncryptionScheme().getParameters()).getOctets().length);
		assertEquals(OPENSSL_KEY.length + 16, info.getEncryptedData().length);
	}
	
	@Test
	void encryptDecryptRoundTrip() {
		byte[] der = PrivateKeyEncryption.encrypt(KEY, OPENSSL_PASSWORD);
		try (Secret secret = PrivateKeyEncryption.decrypt(der, OPENSSL_PASSWORD)) {
			assertArrayEquals(KEY, secret.material());
		}
	}
	
	@Test
	void encryptUsesRandomSaltAndIv() {
		byte[] first = PrivateKeyEncryption.encrypt(KEY, OPENSSL_PASSWORD);
		byte[] second = PrivateKeyEncryption.encrypt(KEY, OPENSSL_PASSWORD);
		assertFalse(Arrays.equals(first, second));
		try (Secret firstSecret = PrivateKeyEncryption.decrypt(first, OPENSSL_PASSWORD); Secret secondSecret = PrivateKeyEncryption.decrypt(second, OPENSSL_PASSWORD)) {
			assertArrayEquals(KEY, firstSecret.material());
			assertArrayEquals(KEY, secondSecret.material());
		}
	}
	
	@Test
	void encryptDecryptEmptyKey() {
		byte[] der = PrivateKeyEncryption.encrypt(new byte[0], OPENSSL_PASSWORD);
		assertEquals(16, EncryptedPrivateKeyInfo.getInstance(der).getEncryptedData().length);
		try (Secret secret = PrivateKeyEncryption.decrypt(der, OPENSSL_PASSWORD)) {
			assertEquals(0, secret.length());
		}
	}
	
	@Test
	void decryptAes192WithPbkdf2Sha512() throws Exception {
		byte[] der = crafted("PBKDF2WithHmacSHA512", PKCSObjectIdentifiers.id_hmacWithSHA512, NISTObjectIdentifiers.id_aes192_CBC, 24, OPENSSL_PASSWORD, KEY);
		try (Secret secret = PrivateKeyEncryption.decrypt(der, OPENSSL_PASSWORD)) {
			assertArrayEquals(KEY, secret.material());
		}
	}
	
	@Test
	void encryptDecryptNonAsciiPassword() throws Exception {
		char[] password = "pässwörd€".toCharArray();
		EncryptedPrivateKeyInfo info = EncryptedPrivateKeyInfo.getInstance(PrivateKeyEncryption.encrypt(KEY, password));
		PBES2Parameters parameters = PBES2Parameters.getInstance(info.getEncryptionAlgorithm().getParameters());
		PBKDF2Params kdf = PBKDF2Params.getInstance(parameters.getKeyDerivationFunc().getParameters());
		byte[] iv = ASN1OctetString.getInstance(parameters.getEncryptionScheme().getParameters()).getOctets();
		
		PKCS5S2ParametersGenerator generator = new PKCS5S2ParametersGenerator(new SHA256Digest());
		generator.init(new String(password).getBytes(StandardCharsets.UTF_8), kdf.getSalt(), kdf.getIterationCount().intValueExact());
		byte[] key = ((KeyParameter) generator.generateDerivedParameters(256)).getKey();
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"), new IvParameterSpec(iv));
		assertArrayEquals(KEY, cipher.doFinal(info.getEncryptedData()));
		
		byte[] fixed = crafted("PBKDF2WithHmacSHA256", PKCSObjectIdentifiers.id_hmacWithSHA256, NISTObjectIdentifiers.id_aes256_CBC, 32, password, KEY);
		try (Secret secret = PrivateKeyEncryption.decrypt(fixed, password)) {
			assertArrayEquals(KEY, secret.material());
		}
		assertThrows(AuthenticationException.class, () -> PrivateKeyEncryption.decrypt(fixed, "password".toCharArray()));
	}
	
	@Test
	void decryptEveryPbkdf2PrfAndAesKeyLength() throws Exception {
		Map<String, ASN1ObjectIdentifier> prfs = new LinkedHashMap<>();
		prfs.put("PBKDF2WithHmacSHA1", PKCSObjectIdentifiers.id_hmacWithSHA1);
		prfs.put("PBKDF2WithHmacSHA224", PKCSObjectIdentifiers.id_hmacWithSHA224);
		prfs.put("PBKDF2WithHmacSHA256", PKCSObjectIdentifiers.id_hmacWithSHA256);
		prfs.put("PBKDF2WithHmacSHA384", PKCSObjectIdentifiers.id_hmacWithSHA384);
		prfs.put("PBKDF2WithHmacSHA512", PKCSObjectIdentifiers.id_hmacWithSHA512);
		Map<ASN1ObjectIdentifier, Integer> ciphers = Map.of(NISTObjectIdentifiers.id_aes128_CBC, 16, NISTObjectIdentifiers.id_aes192_CBC, 24, NISTObjectIdentifiers.id_aes256_CBC, 32);
		
		for (Map.Entry<String, ASN1ObjectIdentifier> prf : prfs.entrySet()) {
			for (Map.Entry<ASN1ObjectIdentifier, Integer> cipher : ciphers.entrySet()) {
				byte[] der = crafted(prf.getKey(), prf.getValue(), cipher.getKey(), cipher.getValue(), OPENSSL_PASSWORD, KEY);
				try (Secret secret = PrivateKeyEncryption.decrypt(der, OPENSSL_PASSWORD)) {
					assertArrayEquals(KEY, secret.material(), prf.getKey() + " with " + cipher.getKey());
				}
			}
		}
	}
}
