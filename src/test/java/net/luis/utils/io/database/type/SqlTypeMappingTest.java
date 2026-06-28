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

package net.luis.utils.io.database.type;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlTypeMapping}.<br>
 *
 * @author Luis-St
 */
class SqlTypeMappingTest {
	
	private static final SqlValueBinder BINDER = (statement, index, value) -> {};
	private static final SqlValueReader READER = (resultSet, index) -> null;
	
	@Test
	void constructWithAllFields() {
		SqlTypeMapping mapping = new SqlTypeMapping("VARCHAR", BINDER, READER);
		assertEquals("VARCHAR", mapping.nativeTypeName());
		assertSame(BINDER, mapping.binder());
		assertSame(READER, mapping.reader());
	}
	
	@Test
	void constructWithNameOnly() {
		SqlTypeMapping mapping = new SqlTypeMapping("VARCHAR");
		assertEquals("VARCHAR", mapping.nativeTypeName());
		assertNull(mapping.binder());
		assertNull(mapping.reader());
	}
	
	@Test
	void constructWithNullName() {
		assertThrows(NullPointerException.class, () -> new SqlTypeMapping(null, BINDER, READER));
	}
	
	@Test
	void constructWithBlankName() {
		assertThrows(IllegalArgumentException.class, () -> new SqlTypeMapping("", null, null));
	}
	
	@Test
	void constructWithWhitespaceName() {
		assertThrows(IllegalArgumentException.class, () -> new SqlTypeMapping("   ", null, null));
	}
	
	@Test
	void constructNameOnlyWithNullName() {
		assertThrows(NullPointerException.class, () -> new SqlTypeMapping(null));
	}
	
	@Test
	void constructWithNullBinderAndReaderAllowed() {
		SqlTypeMapping mapping = new SqlTypeMapping("INT", null, null);
		assertNull(mapping.binder());
		assertNull(mapping.reader());
	}
	
	@Test
	void equalsSameValues() {
		SqlTypeMapping first = new SqlTypeMapping("VARCHAR", BINDER, READER);
		SqlTypeMapping second = new SqlTypeMapping("VARCHAR", BINDER, READER);
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
	}
	
	@Test
	void equalsDifferentName() {
		assertNotEquals(new SqlTypeMapping("VARCHAR"), new SqlTypeMapping("TEXT"));
	}
	
	@Test
	void accessorsReturnConfiguredValues() {
		SqlTypeMapping mapping = new SqlTypeMapping("NUMERIC", BINDER, READER);
		assertEquals("NUMERIC", mapping.nativeTypeName());
		assertSame(BINDER, mapping.binder());
		assertSame(READER, mapping.reader());
	}
	
	@Test
	void toStringContainsName() {
		assertTrue(new SqlTypeMapping("VARCHAR").toString().contains("VARCHAR"));
	}
}
