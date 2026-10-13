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

import net.luis.utils.crypto.algorithm.KemAlgorithm;
import net.luis.utils.crypto.key.Secret;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.security.KeyPair;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link KemEncapsulation}.<br>
 *
 * @author Luis-St
 */
class KemEncapsulationTest {
	
	private static KeyPair x25519;
	
	@BeforeAll
	static void setUp() {
		Providers.installBouncyCastle();
		x25519 = Kems.generateKeyPair(KemAlgorithm.X25519);
	}
	
	@Test
	void constructEncapsulation() {
		KemEncapsulation encapsulation = new KemEncapsulation(new byte[] { 1, 2 }, Secret.copyOf(new byte[32]));
		assertArrayEquals(new byte[] { 1, 2 }, encapsulation.encapsulation());
		assertEquals(32, encapsulation.sharedSecret().length());
	}
	
	@Test
	void constructEncapsulationWithNullEncapsulation() {
		assertEquals("Encapsulation must not be null", assertThrows(NullPointerException.class, () -> new KemEncapsulation(null, Secret.copyOf(new byte[32]))).getMessage());
	}
	
	@Test
	void constructEncapsulationWithNullSharedSecret() {
		assertEquals("Shared secret must not be null", assertThrows(NullPointerException.class, () -> new KemEncapsulation(new byte[1], null)).getMessage());
	}
	
	@Test
	void constructEncapsulationWithBothNull() {
		assertEquals("Encapsulation must not be null", assertThrows(NullPointerException.class, () -> new KemEncapsulation(null, null)).getMessage());
	}
	
	@Test
	void encapsulationCloseWipesSecret() {
		KemEncapsulation sent = Kems.encapsulate(KemAlgorithm.X25519, x25519.getPublic());
		byte[] encapsulation = sent.encapsulation().clone();
		
		sent.close();
		assertThrows(IllegalStateException.class, () -> sent.sharedSecret().material());
		assertEquals(32, sent.sharedSecret().length());
		assertArrayEquals(encapsulation, sent.encapsulation());
	}
	
	@Test
	void encapsulationIsNotWipedOnClose() {
		KemEncapsulation sent = Kems.encapsulate(KemAlgorithm.X25519, x25519.getPublic());
		byte[] before = sent.encapsulation().clone();
		
		sent.close();
		assertArrayEquals(before, sent.encapsulation());
		assertFalse(Arrays.equals(new byte[before.length], sent.encapsulation()));
	}
	
	@Test
	void encapsulationCloseIsIdempotent() {
		KemEncapsulation sent = Kems.encapsulate(KemAlgorithm.X25519, x25519.getPublic());
		assertDoesNotThrow(() -> {
			sent.close();
			sent.close();
		});
	}
	
	@Test
	void encapsulationInTryWithResources() {
		byte[] captured;
		KemEncapsulation escaped;
		try (KemEncapsulation sent = Kems.encapsulate(KemAlgorithm.X25519, x25519.getPublic())) {
			escaped = sent;
			captured = sent.encapsulation();
			assertNotNull(sent.sharedSecret().material());
		}
		
		assertThrows(IllegalStateException.class, () -> escaped.sharedSecret().material());
		assertArrayEquals(captured, escaped.encapsulation());
	}
	
	@Test
	void secretsAreWipedOnClose() {
		KemEncapsulation sent = Kems.encapsulate(KemAlgorithm.X25519, x25519.getPublic());
		byte[] live = sent.sharedSecret().material();
		
		assertFalse(Arrays.equals(new byte[live.length], live));
		sent.close();
		assertArrayEquals(new byte[32], live);
	}
}
