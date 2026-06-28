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

package net.luis.utils.io.database.migration;

import net.luis.utils.io.database.SqlTestFixtures;
import net.luis.utils.io.database.migration.operation.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlMigrationColumnAlter}.<br>
 *
 * @author Luis-St
 */
class SqlMigrationColumnAlterTest {
	
	@Test
	void constructEmpty() {
		assertTrue(new SqlMigrationColumnAlter<>().getAlterations().isEmpty());
	}
	
	@Test
	void setTypeWithNull() {
		assertThrows(NullPointerException.class, () -> new SqlMigrationColumnAlter<>().setType(null));
	}
	
	@Test
	void setDefaultWithNull() {
		assertThrows(NullPointerException.class, () -> new SqlMigrationColumnAlter<>().setDefault(null));
	}
	
	@Test
	void setTypeAddsTypeAlteration() {
		List<SqlColumnAlteration> alterations = new SqlMigrationColumnAlter<String>().setType(SqlTestFixtures.STRING_TYPE).getAlterations();
		assertEquals(1, alterations.size());
		SqlSetTypeAlteration alteration = assertInstanceOf(SqlSetTypeAlteration.class, alterations.getFirst());
		assertSame(SqlTestFixtures.STRING_TYPE, alteration.type());
	}
	
	@Test
	void setNullableAddsNullableAlteration() {
		List<SqlColumnAlteration> alterations = new SqlMigrationColumnAlter<>().setNullable(true).getAlterations();
		assertEquals(1, alterations.size());
		SqlSetNullableAlteration alteration = assertInstanceOf(SqlSetNullableAlteration.class, alterations.getFirst());
		assertTrue(alteration.nullable());
	}
	
	@Test
	void setDefaultAddsDefaultAlteration() {
		List<SqlColumnAlteration> alterations = new SqlMigrationColumnAlter<String>().setDefault("d").getAlterations();
		assertEquals(1, alterations.size());
		SqlSetDefaultAlteration alteration = assertInstanceOf(SqlSetDefaultAlteration.class, alterations.getFirst());
		assertEquals("d", alteration.value());
	}
	
	@Test
	void dropDefaultAddsDropDefaultAlteration() {
		List<SqlColumnAlteration> alterations = new SqlMigrationColumnAlter<>().dropDefault().getAlterations();
		assertEquals(1, alterations.size());
		assertInstanceOf(SqlDropDefaultAlteration.class, alterations.getFirst());
	}
	
	@Test
	void settersReturnSameInstance() {
		SqlMigrationColumnAlter<String> alter = new SqlMigrationColumnAlter<>();
		assertSame(alter, alter.setType(SqlTestFixtures.STRING_TYPE));
		assertSame(alter, alter.setNullable(true));
		assertSame(alter, alter.setDefault("d"));
		assertSame(alter, alter.dropDefault());
	}
	
	@Test
	void chainedAlterationsPreserveOrder() {
		List<SqlColumnAlteration> alterations = new SqlMigrationColumnAlter<String>().setType(SqlTestFixtures.STRING_TYPE).setNullable(false).setDefault("x").dropDefault().getAlterations();
		assertEquals(4, alterations.size());
		assertInstanceOf(SqlSetTypeAlteration.class, alterations.get(0));
		assertInstanceOf(SqlSetNullableAlteration.class, alterations.get(1));
		assertInstanceOf(SqlSetDefaultAlteration.class, alterations.get(2));
		assertInstanceOf(SqlDropDefaultAlteration.class, alterations.get(3));
	}
	
	@Test
	void getAlterationsReturnsImmutableCopy() {
		SqlMigrationColumnAlter<String> alter = new SqlMigrationColumnAlter<>();
		alter.setNullable(true);
		List<SqlColumnAlteration> first = alter.getAlterations();
		assertThrows(UnsupportedOperationException.class, () -> first.add(new SqlDropDefaultAlteration()));
		alter.setNullable(false);
		assertEquals(1, first.size());
		assertEquals(2, alter.getAlterations().size());
	}
}
