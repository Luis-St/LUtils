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

package net.luis.utils.io.database.query;

import org.junit.jupiter.api.Test;

import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlAlias}.<br>
 *
 * @author Luis-St
 */
class SqlAliasTest {
	
	@Test
	void ofCreatesAliasWithValue() {
		assertEquals("u", SqlAlias.of("u").get());
	}
	
	@Test
	void ofWithNullAlias() {
		assertThrows(NullPointerException.class, () -> SqlAlias.of(null));
	}
	
	@Test
	void ofWithBlankAlias() {
		assertThrows(IllegalArgumentException.class, () -> SqlAlias.of("   "));
	}
	
	@Test
	void ofWithEmptyAlias() {
		assertThrows(IllegalArgumentException.class, () -> SqlAlias.of(""));
	}
	
	@Test
	void getReturnsAlias() {
		assertEquals("name", SqlAlias.of("name").get());
	}
	
	@Test
	void equalsWithSameAliasValue() {
		assertEquals(SqlAlias.of("a"), SqlAlias.of("a"));
	}
	
	@Test
	void equalsWithDifferentAliasValue() {
		assertNotEquals(SqlAlias.of("a"), SqlAlias.of("b"));
	}
	
	@Test
	void equalsWithNonAliasObject() {
		assertNotEquals("a", SqlAlias.of("a"));
	}
	
	@Test
	void equalsWithNull() {
		assertNotEquals(null, SqlAlias.of("a"));
	}
	
	@Test
	void hashCodeConsistentWithEquals() {
		assertEquals(SqlAlias.of("a").hashCode(), SqlAlias.of("a").hashCode());
	}
	
	@Test
	void toStringReturnsAlias() {
		assertEquals("table_alias", SqlAlias.of("table_alias").toString());
	}
	
	@Test
	void implementsSupplierContract() {
		Supplier<String> supplier = SqlAlias.of("x");
		assertEquals("x", supplier.get());
	}
}
