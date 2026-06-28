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

package net.luis.utils.io.database.dialect.renderer;

import net.luis.utils.io.database.condition.conditions.comparison.SqlEqualToCondition;
import net.luis.utils.io.database.dialect.SqlDialects;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.exception.client.dialect.SqlDialectUnsupportedRenderingException;
import net.luis.utils.io.database.expression.SqlValueExpression;
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
 * Test class for {@link SqlIndexRenderer}.<br>
 *
 * @author Luis-St
 */
class SqlIndexRendererTest {
	
	private static final SqlIndexRenderer RENDERER = new SqlIndexRenderer(SqlDialects.DEFAULT);
	
	private static SqlTable<Object> usersTable() {
		return SqlTable.create(Object.class, "users");
	}
	
	private static List<SqlColumn<?, ?>> single(SqlTable<Object> table) {
		return List.of(table.column("age", SqlTypes.INTEGER, object -> 0));
	}
	
	private static List<SqlColumn<?, ?>> multiple(SqlTable<Object> table) {
		return List.of(table.column("age", SqlTypes.INTEGER, object -> 0), table.column("score", SqlTypes.INTEGER, object -> 0));
	}
	
	@Test
	void constructWithDialect() {
		assertNotNull(new SqlIndexRenderer(SqlDialects.DEFAULT));
	}
	
	@Test
	void constructWithNullDialect() {
		assertThrows(NullPointerException.class, () -> new SqlIndexRenderer(null));
	}
	
	@Test
	void renderCreateIndexWithNullIndex() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderCreateIndex(null));
	}
	
	@Test
	void renderCreateIndexWithUnsupportedMethod() {
		SqlIndex index = new SqlIndex("idx", single(usersTable()), false, SqlIndexMethod.HASH);
		SqlDialectUnsupportedRenderingException exception = assertThrows(SqlDialectUnsupportedRenderingException.class, () -> RENDERER.renderCreateIndex(index));
		assertTrue(exception.getMessage().contains("Default"));
	}
	
	@Test
	void renderDropIndexWithNullIndexName() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderDropIndex(null, null));
	}
	
	@Test
	void renderStandardDropIndexOnTableWithNullTable() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderStandardDropIndexOnTable(null, "idx"));
	}
	
	@Test
	void renderStandardDropIndexOnTableWithNullIndexName() {
		SqlTable<Object> table = usersTable();
		assertThrows(NullPointerException.class, () -> RENDERER.renderStandardDropIndexOnTable(table, null));
	}
	
	@Test
	void renderRenameIndexWithNullFrom() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderRenameIndex(null, null, "new_idx"));
	}
	
	@Test
	void renderRenameIndexWithNullTo() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderRenameIndex(null, "old_idx", null));
	}
	
	@Test
	void renderCreateIndexNonUnique() throws SqlException {
		SqlIndex index = new SqlIndex("idx_age", single(usersTable()), false, SqlIndexMethod.BTREE);
		SqlRendered rendered = RENDERER.renderCreateIndex(index);
		assertTrue(rendered.sql().contains("CREATE INDEX"));
		assertTrue(rendered.sql().contains("\"idx_age\""));
		assertTrue(rendered.sql().contains("BTREE"));
		assertFalse(rendered.sql().contains("UNIQUE"));
	}
	
	@Test
	void renderCreateIndexUnique() throws SqlException {
		SqlIndex index = new SqlIndex("idx_age", single(usersTable()), true, SqlIndexMethod.BTREE);
		SqlRendered rendered = RENDERER.renderCreateIndex(index);
		assertTrue(rendered.sql().contains("CREATE UNIQUE INDEX"));
	}
	
	@Test
	void renderCreateIndexWithoutWhereCondition() throws SqlException {
		SqlIndex index = new SqlIndex("idx_age", single(usersTable()), false, SqlIndexMethod.BTREE);
		SqlRendered rendered = RENDERER.renderCreateIndex(index);
		assertFalse(rendered.sql().contains("WHERE"));
	}
	
	@Test
	void renderCreateIndexWithWhereCondition() throws SqlException {
		SqlEqualToCondition where = new SqlEqualToCondition(new SqlValueExpression<>(1), new SqlValueExpression<>(1));
		SqlIndex index = new SqlIndex("idx_age", single(usersTable()), false, where, SqlIndexMethod.BTREE);
		SqlRendered rendered = RENDERER.renderCreateIndex(index);
		assertTrue(rendered.sql().contains("WHERE"));
	}
	
	@Test
	void renderCreateIndexWithSingleColumn() throws SqlException {
		SqlIndex index = new SqlIndex("idx_age", single(usersTable()), false, SqlIndexMethod.BTREE);
		SqlRendered rendered = RENDERER.renderCreateIndex(index);
		assertTrue(rendered.sql().contains("ON \"users\""));
		assertTrue(rendered.sql().contains("\"age\""));
		assertEquals(0, rendered.sql().chars().filter(c -> c == ',').count());
	}
	
	@Test
	void renderCreateIndexWithMultipleColumns() throws SqlException {
		SqlIndex index = new SqlIndex("idx_age", multiple(usersTable()), false, SqlIndexMethod.BTREE);
		SqlRendered rendered = RENDERER.renderCreateIndex(index);
		assertTrue(rendered.sql().contains("ON \"users\""));
		assertTrue(rendered.sql().contains("\"age\""));
		assertTrue(rendered.sql().contains("\"score\""));
		assertEquals(1, rendered.sql().chars().filter(c -> c == ',').count());
	}
	
	@Test
	void renderDropIndexProducesDropStatement() throws SqlException {
		SqlRendered rendered = RENDERER.renderDropIndex(null, "idx_age");
		assertTrue(rendered.sql().contains("DROP INDEX"));
		assertTrue(rendered.sql().contains("\"idx_age\""));
		assertFalse(rendered.sql().contains(" ON "));
	}
	
	@Test
	void renderStandardDropIndexOnTableProducesOnClause() throws SqlException {
		SqlRendered rendered = RENDERER.renderStandardDropIndexOnTable(usersTable(), "idx_age");
		assertTrue(rendered.sql().contains("DROP INDEX"));
		assertTrue(rendered.sql().contains("\"idx_age\""));
		assertTrue(rendered.sql().contains("ON"));
		assertTrue(rendered.sql().contains("\"users\""));
	}
	
	@Test
	void renderRenameIndexProducesAlterStatement() throws SqlException {
		SqlRendered rendered = RENDERER.renderRenameIndex(null, "old_idx", "new_idx");
		assertTrue(rendered.sql().contains("ALTER INDEX"));
		assertTrue(rendered.sql().contains("\"old_idx\""));
		assertTrue(rendered.sql().contains("RENAME"));
		assertTrue(rendered.sql().contains("TO"));
		assertTrue(rendered.sql().contains("\"new_idx\""));
	}
	
	@Test
	void renderCreateIndexUniqueWithWhereAndMultipleColumns() throws SqlException {
		SqlEqualToCondition where = new SqlEqualToCondition(new SqlValueExpression<>(1), new SqlValueExpression<>(1));
		SqlIndex index = new SqlIndex("idx_age", multiple(usersTable()), true, where, SqlIndexMethod.BTREE);
		SqlRendered rendered = RENDERER.renderCreateIndex(index);
		assertTrue(rendered.sql().contains("CREATE UNIQUE INDEX"));
		assertEquals(1, rendered.sql().chars().filter(c -> c == ',').count());
		assertTrue(rendered.sql().contains("WHERE"));
	}
}
