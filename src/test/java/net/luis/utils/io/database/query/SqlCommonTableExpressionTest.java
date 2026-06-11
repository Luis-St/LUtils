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

import net.luis.utils.io.database.query.crud.SqlSelectQuery;
import org.junit.jupiter.api.Test;

import static net.luis.utils.io.database.SqlTestFixtures.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlCommonTableExpression}.<br>
 *
 * @author Luis-St
 */
class SqlCommonTableExpressionTest {
	
	@Test
	void constructWithValidArguments() {
		SqlAlias alias = SqlAlias.of("cte");
		SqlSelectQuery<Object> query = sampleSelect();
		SqlCommonTableExpression cte = new SqlCommonTableExpression(alias, query, false);
		assertSame(alias, cte.alias());
		assertSame(query, cte.query());
		assertFalse(cte.recursive());
	}
	
	@Test
	void constructWithNullAlias() {
		assertThrows(NullPointerException.class, () -> new SqlCommonTableExpression(null, sampleSelect(), false));
	}
	
	@Test
	void constructWithNullQuery() {
		assertThrows(NullPointerException.class, () -> new SqlCommonTableExpression(SqlAlias.of("cte"), null, false));
	}
	
	@Test
	void constructRecursiveTrue() {
		SqlCommonTableExpression cte = new SqlCommonTableExpression(SqlAlias.of("cte"), sampleSelect(), true);
		assertTrue(cte.recursive());
	}
	
	@Test
	void constructRecursiveFalse() {
		SqlCommonTableExpression cte = new SqlCommonTableExpression(SqlAlias.of("cte"), sampleSelect(), false);
		assertFalse(cte.recursive());
	}
	
	@Test
	void equalsAndHashCodeForSameComponents() {
		SqlAlias alias = SqlAlias.of("cte");
		SqlSelectQuery<Object> query = sampleSelect();
		SqlCommonTableExpression first = new SqlCommonTableExpression(alias, query, true);
		SqlCommonTableExpression second = new SqlCommonTableExpression(alias, query, true);
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
	}
}
