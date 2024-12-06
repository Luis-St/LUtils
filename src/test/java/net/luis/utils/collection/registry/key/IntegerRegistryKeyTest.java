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

package net.luis.utils.collection.registry.key;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link IntegerRegistryKey}.<br>
 *
 * @author Luis-St
 */
class IntegerRegistryKeyTest {
	
	@Test
	void of() {
		assertDoesNotThrow(() -> IntegerRegistryKey.of(0));
		assertNotNull(IntegerRegistryKey.of(0));
		assertNotSame(IntegerRegistryKey.of(0), IntegerRegistryKey.of(0));
	}
	
	@Test
	void getKey() {
		IntegerRegistryKey key = IntegerRegistryKey.of(0);
		assertEquals(0, key.getKey());
		assertNotEquals(1, key.getKey());
	}
	
	@Test
	void compareTo() {
		IntegerRegistryKey key1 = IntegerRegistryKey.of(0);
		IntegerRegistryKey key2 = IntegerRegistryKey.of(1);
		assertEquals(-1, key1.compareTo(key2));
		assertEquals(1, key2.compareTo(key1));
		assertEquals(0, key1.compareTo(key1));
	}
}