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

import net.luis.utils.io.database.SqlTestFixtures;
import net.luis.utils.io.database.condition.conditions.comparison.SqlEqualToCondition;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.expression.SqlValueExpression;
import net.luis.utils.io.database.index.SqlIndex;
import net.luis.utils.io.database.index.SqlIndexMethod;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link MySqlIndexRenderer}.<br>
 *
 * @author Luis-St
 */
class MySqlIndexRendererTest {
	
	private static final MySqlIndexRenderer RENDERER = new MySqlIndexRenderer(SqlDialects.MYSQL);
	
	@Test
	void renderCreateIndexNullIndex() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderCreateIndex(null));
	}
	
	@Test
	void renderDropIndexNullOwningTable() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderDropIndex(null, "idx"));
	}
	
	@Test
	void renderRenameIndexNullTable() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderRenameIndex(null, "old", "new"));
	}
	
	@Test
	void renderRenameIndexNullFrom() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderRenameIndex(SqlTestFixtures.sampleTable(), null, "new"));
	}
	
	@Test
	void renderRenameIndexNullTo() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderRenameIndex(SqlTestFixtures.sampleTable(), "old", null));
	}
	
	@Test
	void renderCreateIndexNonUnique() throws SqlException {
		SqlIndex index = new SqlIndex("idx", SqlTestFixtures.columns(), false, SqlIndexMethod.BTREE);
		String sql = RENDERER.renderCreateIndex(index).sql();
		assertTrue(sql.contains("CREATE INDEX"));
		assertTrue(sql.contains("`idx`"));
		assertTrue(sql.contains("`test_table`"));
		assertTrue(sql.contains("USING"));
		assertFalse(sql.contains("UNIQUE"));
		assertFalse(sql.contains("WHERE"));
	}
	
	@Test
	void renderCreateIndexUnique() throws SqlException {
		SqlIndex index = new SqlIndex("idx", SqlTestFixtures.columns(), true, SqlIndexMethod.BTREE);
		String sql = RENDERER.renderCreateIndex(index).sql();
		assertTrue(sql.contains("UNIQUE"));
	}
	
	@Test
	void renderCreateIndexWithWhereCondition() throws SqlException {
		SqlEqualToCondition condition = new SqlEqualToCondition(new SqlValueExpression<>(1), new SqlValueExpression<>(1));
		SqlIndex index = new SqlIndex("idx", SqlTestFixtures.columns(), false, condition, SqlIndexMethod.BTREE);
		String sql = RENDERER.renderCreateIndex(index).sql();
		assertTrue(sql.contains("WHERE"));
	}
	
	@Test
	void renderCreateIndexEmitsUsingMethod() throws SqlException {
		SqlIndex index = new SqlIndex("idx", SqlTestFixtures.columns(), false, SqlIndexMethod.BTREE);
		String sql = RENDERER.renderCreateIndex(index).sql();
		assertTrue(sql.contains("USING"));
		assertTrue(sql.contains(SqlDialects.MYSQL.getIndexMethodName(SqlIndexMethod.BTREE)));
	}
	
	@Test
	void renderDropIndexProducesDropOnTable() throws SqlException {
		String sql = RENDERER.renderDropIndex(SqlTestFixtures.sampleTable(), "idx").sql();
		assertTrue(sql.contains("DROP INDEX"));
		assertTrue(sql.contains("`test_table`"));
	}
	
	@Test
	void renderRenameIndexProducesAlterRename() throws SqlException {
		String sql = RENDERER.renderRenameIndex(SqlTestFixtures.sampleTable(), "old", "new").sql();
		assertTrue(sql.contains("ALTER TABLE"));
		assertTrue(sql.contains("RENAME"));
		assertTrue(sql.contains("INDEX"));
		assertTrue(sql.contains("`old`"));
		assertTrue(sql.contains("`new`"));
	}
}
