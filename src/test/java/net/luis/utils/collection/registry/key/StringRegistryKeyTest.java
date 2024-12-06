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
 * Test class for {@link StringRegistryKey}.<br>
 *
 * @author Luis-St
 */
class StringRegistryKeyTest {
	
	@Test
	void of() {
		assertThrows(NullPointerException.class, () -> StringRegistryKey.of(null));
		assertDoesNotThrow(() -> StringRegistryKey.of("key"));
		assertNotNull(StringRegistryKey.of("key"));
		assertNotSame(StringRegistryKey.of("key"), StringRegistryKey.of("key"));
	}
	
	@Test
	void getKey() {
		StringRegistryKey key = StringRegistryKey.of("key");
		assertNotNull(key.getKey());
		assertEquals("key", key.getKey());
		assertNotEquals("test", key.getKey());
	}
	
	@Test
	void compareTo() {
		StringRegistryKey key1 = StringRegistryKey.of("key0");
		StringRegistryKey key2 = StringRegistryKey.of("key1");
		assertEquals(-1, key1.compareTo(key2));
		assertEquals(1, key2.compareTo(key1));
		assertEquals(0, key1.compareTo(key1));
	}
}
