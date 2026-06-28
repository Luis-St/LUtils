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

package net.luis.utils.io.database.rendering;

import net.luis.utils.io.database.SqlTestFixtures;
import net.luis.utils.io.database.dialect.SqlDialect;
import net.luis.utils.io.database.exception.SqlException;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlRenderable}.<br>
 *
 * @author Luis-St
 */
class SqlRenderableTest {
	
	@Test
	void lambdaImplementsRenderable() throws SqlException {
		SqlRenderable renderable = dialect -> SqlRendered.of("SELECT 1");
		assertEquals("SELECT 1", renderable.toSql(SqlTestFixtures.DIALECT).sql());
	}
	
	@Test
	void toSqlReceivesDialectArgument() throws SqlException {
		AtomicReference<SqlDialect> captured = new AtomicReference<>();
		SqlRenderable renderable = dialect -> {
			captured.set(dialect);
			return SqlRendered.of("SELECT 1");
		};
		renderable.toSql(SqlTestFixtures.DIALECT);
		assertSame(SqlTestFixtures.DIALECT, captured.get());
	}
	
	@Test
	void toSqlPropagatesSqlException() {
		SqlRenderable renderable = dialect -> {
			throw new SqlException("boom");
		};
		assertThrows(SqlException.class, () -> renderable.toSql(SqlTestFixtures.DIALECT));
	}
}
