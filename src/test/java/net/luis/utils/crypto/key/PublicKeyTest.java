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
 * Test class for {@link PublicKey}.<br>
 *
 * @author Luis-St
 */
class PublicKeyTest {
	
	@Test
	void constructor() {
		assertThrows(NullPointerException.class, () -> new PublicKey(null, null));
		assertThrows(NullPointerException.class, () -> new PublicKey(null, ONE));
		assertThrows(NullPointerException.class, () -> new PublicKey(ONE, null));
	}
	
	@Test
	void getKey() {
		PublicKey key = new PublicKey(ONE, ONE);
		assertNotNull(key.getKey());
		assertEquals(ONE, key.getKey());
	}
	
	@Test
	void getModulus() {
		PublicKey key = new PublicKey(ONE, ONE);
		assertNotNull(key.getModulus());
		assertEquals(ONE, key.getModulus());
	}
}