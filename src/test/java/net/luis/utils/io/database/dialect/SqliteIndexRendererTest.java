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

package net.luis.utils.io.database.dialect;

import net.luis.utils.io.database.Sql;
import net.luis.utils.io.database.SqlTestFixtures;
import net.luis.utils.io.database.condition.SqlCondition;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.exception.client.dialect.SqlDialectFeatureException;
import net.luis.utils.io.database.index.SqlIndex;
import net.luis.utils.io.database.index.SqlIndexMethod;
import net.luis.utils.io.database.rendering.SqlRendered;
import net.luis.utils.io.database.table.SqlColumn;
import net.luis.utils.io.database.table.SqlTable;
import net.luis.utils.io.database.type.SqlTypes;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqliteIndexRenderer}.<br>
 *
 * @author Luis-St
 */
class SqliteIndexRendererTest {
	
	private static final SqliteIndexRenderer RENDERER = new SqliteIndexRenderer(SqlDialects.SQLITE);
	
	@Test
	void renderRenameIndexAlwaysUnsupported() {
		assertThrows(SqlDialectFeatureException.class, () -> RENDERER.renderRenameIndex(SqlTestFixtures.sampleTable(), "old", "new"));
	}
	
	@Test
	void renderCreateIndexWithWhereConditionRendersLiteral() throws SqlException {
		SqlTable<Object> table = SqlTable.create(Object.class, "users");
		SqlColumn<Object, String> kind = table.column("kind", SqlTypes.TEXT, object -> "");
		SqlIndex index = new SqlIndex("idx_kind", List.of(kind), false, Sql.equalTo(kind, "A"), SqlIndexMethod.BTREE);
		SqlRendered rendered = RENDERER.renderCreateIndex(index);
		assertTrue(rendered.sql().contains("WHERE"));
		assertTrue(rendered.sql().contains("'A'"));
		assertFalse(rendered.sql().contains("?"));
		assertTrue(rendered.parameters().isEmpty());
	}
	
	@Test
	void renderCreateIndexWithoutWhereConditionOmitsWhere() throws SqlException {
		SqlIndex index = new SqlIndex("idx_kind", SqlTestFixtures.columns(), false, SqlIndexMethod.BTREE);
		SqlRendered rendered = RENDERER.renderCreateIndex(index);
		assertFalse(rendered.sql().contains("WHERE"));
		assertTrue(rendered.parameters().isEmpty());
	}
	
	@Test
	void renderCreateIndexWithNumericWhereConditionRendersLiteral() throws SqlException {
		SqlTable<Object> table = SqlTable.create(Object.class, "users");
		SqlColumn<Object, Integer> count = table.column("count", SqlTypes.INTEGER, object -> 0);
		SqlIndex index = new SqlIndex("idx_count", List.of(count), false, Sql.greaterThan(count, 10), SqlIndexMethod.BTREE);
		SqlRendered rendered = RENDERER.renderCreateIndex(index);
		assertTrue(rendered.sql().contains("> 10"));
		assertTrue(rendered.parameters().isEmpty());
	}
	
	@Test
	void renderCreateIndexWithStringWhereConditionEscapesLiteral() throws SqlException {
		SqlTable<Object> table = SqlTable.create(Object.class, "users");
		SqlColumn<Object, String> kind = table.column("kind", SqlTypes.TEXT, object -> "");
		SqlIndex index = new SqlIndex("idx_kind", List.of(kind), false, Sql.equalTo(kind, "O'Brien"), SqlIndexMethod.BTREE);
		SqlRendered rendered = RENDERER.renderCreateIndex(index);
		assertTrue(rendered.sql().contains("'O''Brien'"));
		assertTrue(rendered.parameters().isEmpty());
	}
	
	@Test
	void renderCreateIndexUniqueWithWhereConditionRendersLiteral() throws SqlException {
		SqlTable<Object> table = SqlTable.create(Object.class, "users");
		SqlColumn<Object, String> kind = table.column("kind", SqlTypes.TEXT, object -> "");
		SqlIndex index = new SqlIndex("idx_kind", List.of(kind), true, Sql.equalTo(kind, "A"), SqlIndexMethod.BTREE);
		SqlRendered rendered = RENDERER.renderCreateIndex(index);
		assertTrue(rendered.sql().contains("UNIQUE"));
		assertTrue(rendered.sql().contains("'A'"));
		assertTrue(rendered.parameters().isEmpty());
	}
	
	@Test
	void renderCreateIndexWithCombinedWhereConditionRendersAllLiterals() throws SqlException {
		SqlTable<Object> table = SqlTable.create(Object.class, "users");
		SqlColumn<Object, String> kind = table.column("kind", SqlTypes.TEXT, object -> "");
		SqlColumn<Object, Integer> count = table.column("count", SqlTypes.INTEGER, object -> 0);
		SqlIndex index = new SqlIndex("idx_kind", List.of(kind), false, SqlCondition.allOf(Sql.equalTo(kind, "A"), Sql.greaterThan(count, 0)), SqlIndexMethod.BTREE);
		SqlRendered rendered = RENDERER.renderCreateIndex(index);
		assertTrue(rendered.sql().contains("'A'"));
		assertTrue(rendered.sql().contains("> 0"));
		assertTrue(rendered.parameters().isEmpty());
	}
}
