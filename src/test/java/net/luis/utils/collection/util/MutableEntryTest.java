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

package net.luis.utils.collection.util;

import net.luis.utils.collection.util.MutableEntry;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link MutableEntry}.<br>
 *
 * @author Luis-St
 */
class MutableEntryTest {
	
	@Test
	void constructor() {
		assertThrows(NullPointerException.class, () -> new MutableEntry<>(null, "value"));
		assertDoesNotThrow(() -> new MutableEntry<>("key", "value"));
		assertDoesNotThrow(() -> new MutableEntry<>("key", null));
	}
	
	@Test
	void setValue() {
		MutableEntry<String, String> entry = new MutableEntry<>("key", "value0");
		assertEquals("value0", entry.getValue());
		assertDoesNotThrow(() -> entry.setValue("value1"));
		assertEquals("value1", entry.getValue());
	}
}