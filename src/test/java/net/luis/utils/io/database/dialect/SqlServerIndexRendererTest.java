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
import net.luis.utils.io.database.exception.client.dialect.SqlDialectFeatureException;
import net.luis.utils.io.database.expression.SqlValueExpression;
import net.luis.utils.io.database.index.SqlIndex;
import net.luis.utils.io.database.index.SqlIndexMethod;
import net.luis.utils.io.database.table.SqlColumn;
import net.luis.utils.io.database.table.SqlTable;
import net.luis.utils.io.database.type.SqlTypes;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlServerIndexRenderer}.<br>
 *
 * @author Luis-St
 */
class SqlServerIndexRendererTest {
	
	private static final SqlServerIndexRenderer RENDERER = new SqlServerIndexRenderer(SqlDialects.SQL_SERVER);
	
	private static List<SqlColumn<?, ?>> columns() {
		SqlTable<Object> table = SqlTable.create(Object.class, "users");
		return List.of(table.column("age", SqlTypes.INTEGER, object -> 0));
	}
	
	@Test
	void renderCreateIndexNullIndex() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderCreateIndex(null));
	}
	
	@Test
	void renderDropIndexNullOwningTable() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderDropIndex(null, "idx"));
	}
	
	@Test
	void renderRenameIndexAlwaysUnsupported() {
		assertThrows(SqlDialectFeatureException.class, () -> RENDERER.renderRenameIndex(SqlTestFixtures.sampleTable(), "old", "new"));
	}
	
	@Test
	void renderCreateIndexNonUniqueBtree() throws SqlException {
		SqlIndex index = new SqlIndex("idx", columns(), false, SqlIndexMethod.BTREE);
		String sql = RENDERER.renderCreateIndex(index).sql();
		assertTrue(sql.contains("CREATE INDEX"));
		assertFalse(sql.contains("UNIQUE"));
		assertFalse(sql.contains("CLUSTERED"));
		assertFalse(sql.contains("WHERE"));
	}
	
	@Test
	void renderCreateIndexUnique() throws SqlException {
		SqlIndex index = new SqlIndex("idx", columns(), true, SqlIndexMethod.BTREE);
		assertTrue(RENDERER.renderCreateIndex(index).sql().contains("UNIQUE"));
	}
	
	@Test
	void renderCreateIndexClustered() throws SqlException {
		SqlIndex index = new SqlIndex("idx", columns(), false, SqlIndexMethod.CLUSTERED);
		assertTrue(RENDERER.renderCreateIndex(index).sql().contains("CLUSTERED"));
	}
	
	@Test
	void renderCreateIndexNonclustered() throws SqlException {
		SqlIndex index = new SqlIndex("idx", columns(), false, SqlIndexMethod.NONCLUSTERED);
		assertTrue(RENDERER.renderCreateIndex(index).sql().contains("NONCLUSTERED"));
	}
	
	@Test
	void renderCreateIndexWithWhereCondition() throws SqlException {
		SqlEqualToCondition where = new SqlEqualToCondition(new SqlValueExpression<>(1), new SqlValueExpression<>(1));
		SqlIndex index = new SqlIndex("idx", columns(), false, where, SqlIndexMethod.BTREE);
		assertTrue(RENDERER.renderCreateIndex(index).sql().contains("WHERE"));
	}
	
	@Test
	void renderDropIndexProducesDropOnTable() throws SqlException {
		String sql = RENDERER.renderDropIndex(SqlTestFixtures.sampleTable(), "idx").sql();
		assertTrue(sql.contains("DROP INDEX"));
		assertTrue(sql.contains("[test_table]"));
	}
}
