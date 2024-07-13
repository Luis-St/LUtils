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

package net.luis.utils.crypto.key;

import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static java.math.BigInteger.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link KeyPair}.<br>
 *
 * @author Luis-St
 */
class KeyPairTest {
	
	@Test
	void constructor() {
		assertThrows(NullPointerException.class, () -> new KeyPair(null, null));
		assertThrows(NullPointerException.class, () -> new KeyPair(new PublicKey(ONE, ONE), null));
		assertThrows(NullPointerException.class, () -> new KeyPair(null, new PrivateKey(ONE, ONE)));
		assertThrows(IllegalArgumentException.class, () -> new KeyPair(new PublicKey(ONE, ONE), new PrivateKey(ONE, TWO)));
	}
	
	@Test
	void getPublicKey() {
		KeyPair keyPair = new KeyPair(new PublicKey(ONE, ONE), new PrivateKey(ONE, ONE));
		assertEquals(ONE, keyPair.getPublicKey().getKey());
		assertEquals(ONE, keyPair.getPublicKey().getModulus());
	}
	
	@Test
	void getPrivateKey() {
		KeyPair keyPair = new KeyPair(new PublicKey(ONE, ONE), new PrivateKey(ONE, ONE));
		assertEquals(ONE, keyPair.getPrivateKey().getKey());
		assertEquals(ONE, keyPair.getPrivateKey().getModulus());
	}
}