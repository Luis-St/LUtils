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

package net.luis.utils.io.database.condition.conditions.comparison;

import net.luis.utils.io.database.SqlConnectionSource;
import net.luis.utils.io.database.SqlTestFixtures;
import net.luis.utils.io.database.dialect.SqlDialects;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.expression.SqlExpression;
import net.luis.utils.io.database.query.crud.SqlSelectQuery;
import net.luis.utils.io.database.table.SqlTable;
import net.luis.utils.io.database.type.SqlTypes;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlInQueryCondition}.<br>
 *
 * @author Luis-St
 */
class SqlInQueryConditionTest {
	
	private static SqlSelectQuery<Object> selectQuery() {
		SqlTable<Object> table = SqlTable.create(Object.class, "users");
		table.column("id", SqlTypes.INTEGER, object -> 0);
		SqlConnectionSource source = () -> null;
		return new SqlSelectQuery<>(table, SqlDialects.DEFAULT, source, Duration.ofSeconds(30), resultSet -> null);
	}
	
	@Test
	void constructWithValueAndQuery() {
		SqlExpression<?> value = SqlTestFixtures.integerExpression();
		SqlSelectQuery<Object> query = selectQuery();
		SqlInQueryCondition condition = new SqlInQueryCondition(value, query);
		assertSame(value, condition.value());
		assertSame(query, condition.query());
	}
	
	@Test
	void constructWithNullValue() {
		assertThrows(NullPointerException.class, () -> new SqlInQueryCondition(null, selectQuery()));
	}
	
	@Test
	void constructWithNullQuery() {
		assertThrows(NullPointerException.class, () -> new SqlInQueryCondition(SqlTestFixtures.integerExpression(), null));
	}
	
	@Test
	void toSqlWithNullDialect() {
		SqlInQueryCondition condition = new SqlInQueryCondition(SqlTestFixtures.integerExpression(), selectQuery());
		assertThrows(NullPointerException.class, () -> condition.toSql(null));
	}
	
	@Test
	void toSqlRendersInSubquery() throws SqlException {
		SqlInQueryCondition condition = new SqlInQueryCondition(SqlTestFixtures.integerExpression(), selectQuery());
		String sql = condition.toSql(SqlTestFixtures.DIALECT).sql();
		assertTrue(sql.contains("IN("));
		assertTrue(sql.contains("SELECT"));
	}
	
	@Test
	void equalsAndHashCode() {
		SqlSelectQuery<Object> query = selectQuery();
		SqlInQueryCondition first = new SqlInQueryCondition(SqlTestFixtures.integerExpression(), query);
		SqlInQueryCondition second = new SqlInQueryCondition(SqlTestFixtures.integerExpression(), query);
		SqlInQueryCondition different = new SqlInQueryCondition(SqlTestFixtures.stringExpression(), query);
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
		assertNotEquals(first, different);
	}
}
