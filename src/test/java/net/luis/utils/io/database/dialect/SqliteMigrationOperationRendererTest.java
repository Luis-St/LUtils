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

import net.luis.utils.io.database.SqlReferentialAction;
import net.luis.utils.io.database.SqlTestFixtures;
import net.luis.utils.io.database.exception.client.dialect.SqlDialectFeatureException;
import net.luis.utils.io.database.exception.client.dialect.SqlDialectUnsupportedRenderingException;
import net.luis.utils.io.database.table.SqlColumn;
import net.luis.utils.io.database.table.SqlTable;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqliteMigrationOperationRenderer}.<br>
 *
 * @author Luis-St
 */
class SqliteMigrationOperationRendererTest {
	
	private static final SqliteMigrationOperationRenderer RENDERER = new SqliteMigrationOperationRenderer(SqlDialects.SQLITE);
	
	private static SqlTable<Object> table() {
		return SqlTestFixtures.sampleTable();
	}
	
	private static List<SqlColumn<?, ?>> columns() {
		return List.of(SqlTestFixtures.integerColumn());
	}
	
	@Test
	void renderAddUniqueConstraintUnsupported() {
		assertThrows(SqlDialectUnsupportedRenderingException.class, () -> RENDERER.renderAddUniqueConstraint(table(), "uc", columns()));
	}
	
	@Test
	void renderAddForeignKeyUnsupported() {
		assertThrows(SqlDialectUnsupportedRenderingException.class, () -> RENDERER.renderAddForeignKey(table(), "fk", columns(), table(), columns(), SqlReferentialAction.NO_ACTION, SqlReferentialAction.NO_ACTION));
	}
	
	@Test
	void renderAddCheckConstraintUnsupported() {
		assertThrows(SqlDialectUnsupportedRenderingException.class, () -> RENDERER.renderAddCheckConstraint(table(), "ck", SqlTestFixtures.alwaysCondition()));
	}
	
	@Test
	void renderAddCompositePrimaryKeyUnsupported() {
		assertThrows(SqlDialectUnsupportedRenderingException.class, () -> RENDERER.renderAddCompositePrimaryKey(table(), "pk", columns()));
	}
	
	@Test
	void renderDropConstraintUnsupported() {
		assertThrows(SqlDialectFeatureException.class, () -> RENDERER.renderDropConstraint(table(), "c"));
	}
}
