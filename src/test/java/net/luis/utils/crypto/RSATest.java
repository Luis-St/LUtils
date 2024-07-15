/*
 * LUtils
 * Copyright (C) 2024 Luis Staudt
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

import net.luis.utils.crypto.key.KeyPair;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link RSA}.<br>
 *
 * @author Luis-St
 */
class RSATest {
	
	private static final BigInteger DEFAULT_PUBLIC = BigInteger.valueOf(65537);
	private static final BigInteger CUSTOM_PUBLIC = BigInteger.valueOf(17);
	
	@Test
	void createFastKeyPair() {
		assertDoesNotThrow(() -> RSA.createFastKeyPair());
		assertNotNull(RSA.createFastKeyPair());
		assertEquals(1024, RSA.createFastKeyPair().getPrivateKey().getKey().bitLength(), 16);
		assertEquals(BigInteger.valueOf(65537), RSA.createFastKeyPair().getPublicKey().getKey());
		
		assertThrows(NullPointerException.class, () -> RSA.createFastKeyPair(null));
		assertThrows(ArithmeticException.class, () -> RSA.createFastKeyPair(BigInteger.valueOf(16)));
		assertDoesNotThrow(() -> RSA.createFastKeyPair(CUSTOM_PUBLIC));
		assertNotNull(RSA.createFastKeyPair(CUSTOM_PUBLIC));
		assertEquals(1024, RSA.createFastKeyPair(CUSTOM_PUBLIC).getPrivateKey().getKey().bitLength(), 16);
		assertEquals(CUSTOM_PUBLIC, RSA.createFastKeyPair(CUSTOM_PUBLIC).getPublicKey().getKey());
	}
	
	@Test
	void createSecureKeyPair() {
		assertDoesNotThrow(() -> RSA.createSecureKeyPair());
		assertNotNull(RSA.createSecureKeyPair());
		assertEquals(4096, RSA.createSecureKeyPair().getPrivateKey().getKey().bitLength(), 16);
		assertEquals(DEFAULT_PUBLIC, RSA.createSecureKeyPair().getPublicKey().getKey());
		
		assertThrows(NullPointerException.class, () -> RSA.createSecureKeyPair(null));
		assertThrows(ArithmeticException.class, () -> RSA.createSecureKeyPair(BigInteger.valueOf(16)));
		assertDoesNotThrow(() -> RSA.createSecureKeyPair(CUSTOM_PUBLIC));
		assertNotNull(RSA.createSecureKeyPair(CUSTOM_PUBLIC));
		assertEquals(4096, RSA.createSecureKeyPair(CUSTOM_PUBLIC).getPrivateKey().getKey().bitLength(), 16);
		assertEquals(CUSTOM_PUBLIC, RSA.createSecureKeyPair(CUSTOM_PUBLIC).getPublicKey().getKey());
	}
	
	@Test
	void createKeyPair() {
		assertThrows(IllegalArgumentException.class, () -> RSA.createKeyPair(7));
		assertThrows(IllegalArgumentException.class, () -> RSA.createKeyPair(1));
		assertDoesNotThrow(() -> RSA.createKeyPair(2048));
		assertNotNull(RSA.createKeyPair(2048));
		assertEquals(2048, RSA.createKeyPair(2048).getPrivateKey().getKey().bitLength(), 16);
		assertEquals(DEFAULT_PUBLIC, RSA.createKeyPair(2048).getPublicKey().getKey());
		
		assertThrows(NullPointerException.class, () -> RSA.createKeyPair(2048, null));
		assertThrows(IllegalArgumentException.class, () -> RSA.createKeyPair(7, CUSTOM_PUBLIC));
		assertThrows(IllegalArgumentException.class, () -> RSA.createKeyPair(1, CUSTOM_PUBLIC));
		assertThrows(ArithmeticException.class, () -> RSA.createKeyPair(2048, BigInteger.valueOf(16)));
		assertDoesNotThrow(() -> RSA.createKeyPair(2048, CUSTOM_PUBLIC));
		assertNotNull(RSA.createKeyPair(2048, CUSTOM_PUBLIC));
		assertEquals(2048, RSA.createKeyPair(2048, CUSTOM_PUBLIC).getPrivateKey().getKey().bitLength(), 16);
		assertEquals(CUSTOM_PUBLIC, RSA.createKeyPair(2048, CUSTOM_PUBLIC).getPublicKey().getKey());
	}
	
	@Test
	void encrypt() {
		KeyPair keyPair = RSA.createKeyPair(4);
		assertThrows(NullPointerException.class, () -> RSA.encrypt((String) null, keyPair.getPublicKey()));
		assertThrows(NullPointerException.class, () -> RSA.encrypt("", null));
		assertArrayEquals(new byte[] { 0 }, RSA.encrypt("", keyPair.getPublicKey()));
		
		assertThrows(NullPointerException.class, () -> RSA.encrypt((byte[]) null, keyPair.getPublicKey()));
		assertThrows(NullPointerException.class, () -> RSA.encrypt(ArrayUtils.EMPTY_BYTE_ARRAY, null));
		assertArrayEquals(new byte[] { 0 }, RSA.encrypt(ArrayUtils.EMPTY_BYTE_ARRAY, keyPair.getPublicKey()));
		
		assertThrows(NullPointerException.class, () -> RSA.encrypt((BigInteger) null, keyPair.getPublicKey()));
		assertThrows(NullPointerException.class, () -> RSA.encrypt(BigInteger.ZERO, null));
		assertArrayEquals(new byte[] { 0 }, RSA.encrypt(BigInteger.ZERO, keyPair.getPublicKey()));
	}
	
	@Test
	void decrypt() {
		KeyPair keyPair = RSA.createKeyPair(4);
		assertThrows(NullPointerException.class, () -> RSA.decrypt((byte[]) null, keyPair.getPrivateKey()));
		assertThrows(NullPointerException.class, () -> RSA.decrypt(ArrayUtils.EMPTY_BYTE_ARRAY, null));
		assertArrayEquals(new byte[] { 0 }, RSA.decrypt(ArrayUtils.EMPTY_BYTE_ARRAY, keyPair.getPrivateKey()));
		
		assertThrows(NullPointerException.class, () -> RSA.decrypt((BigInteger) null, keyPair.getPrivateKey()));
		assertThrows(NullPointerException.class, () -> RSA.decrypt(BigInteger.ZERO, null));
		assertArrayEquals(new byte[] { 0 }, RSA.decrypt(BigInteger.ZERO, keyPair.getPrivateKey()));
	}
	
	@Test
	void crypt() {
		KeyPair keyPair = RSA.createKeyPair(4);
		String str = "Hello World!";
		byte[] bytes = { 0, 4, 8, 16, 32, 64 };
		BigInteger bigInteger = BigInteger.valueOf(1234567890);
		
		assertArrayEquals(str.getBytes(), RSA.decrypt(RSA.encrypt(str, keyPair.getPublicKey()), keyPair.getPrivateKey()));
		assertArrayEquals(bytes, RSA.decrypt(RSA.encrypt(bytes, keyPair.getPublicKey()), keyPair.getPrivateKey()));
		assertEquals(bigInteger, new BigInteger(RSA.decrypt(RSA.encrypt(bigInteger, keyPair.getPublicKey()), keyPair.getPrivateKey())));
	}
}