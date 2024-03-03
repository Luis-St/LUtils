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

package net.luis.utils.lang;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link EnumConstant}.
 *
 * @author Luis-St
 */
class EnumConstantTest {
	
	@Test
	void constructor() {
		assertThrows(NullPointerException.class, () -> new EnumConstant<>(null, 0, "value"));
		assertThrows(NullPointerException.class, () -> new EnumConstant<>("name", 0, null));
		assertThrows(IllegalArgumentException.class, () -> new EnumConstant<>("name", -1, "value"));
		assertDoesNotThrow(() -> new EnumConstant<>("name", 0, "value"));
	}
	
	@Test
	void name() {
		assertDoesNotThrow(() -> new EnumConstant<>("name", 0, "value").name());
		assertEquals("name", new EnumConstant<>("name", 0, "value").name());
		assertNotEquals("name", new EnumConstant<>("name2", 0, "value").name());
	}
	
	@Test
	void ordinal() {
		assertDoesNotThrow(() -> new EnumConstant<>("name", 0, "value").ordinal());
		assertEquals(0, new EnumConstant<>("name", 0, "value").ordinal());
		assertNotEquals(0, new EnumConstant<>("name", 1, "value").ordinal());
	}
	
	@Test
	void value() {
		assertDoesNotThrow(() -> new EnumConstant<>("name", 0, "value").value());
		assertEquals("value", new EnumConstant<>("name", 0, "value").value());
		assertNotEquals("value", new EnumConstant<>("name", 0, "value2").value());
	}
}