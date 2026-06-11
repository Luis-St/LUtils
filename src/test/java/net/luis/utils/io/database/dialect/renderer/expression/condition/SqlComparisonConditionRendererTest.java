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

package net.luis.utils.io.database.dialect.renderer.expression.condition;

import net.luis.utils.io.database.SqlConnectionSource;
import net.luis.utils.io.database.condition.conditions.SqlComparisonCondition;
import net.luis.utils.io.database.condition.conditions.comparison.*;
import net.luis.utils.io.database.dialect.SqlDialects;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.exception.client.dialect.SqlDialectUnknownConstructException;
import net.luis.utils.io.database.expression.SqlExpression;
import net.luis.utils.io.database.expression.SqlValueExpression;
import net.luis.utils.io.database.query.crud.SqlSelectQuery;
import net.luis.utils.io.database.rendering.SqlRendered;
import net.luis.utils.io.database.table.SqlTable;
import net.luis.utils.io.database.type.SqlTypes;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlComparisonConditionRenderer}.<br>
 *
 * @author Luis-St
 */
class SqlComparisonConditionRendererTest {
	
	private static final SqlComparisonConditionRenderer RENDERER = new SqlComparisonConditionRenderer(SqlDialects.DEFAULT);
	
	private static SqlSelectQuery<Object> selectQuery() {
		SqlTable<Object> table = SqlTable.create(Object.class, "users");
		table.column("id", SqlTypes.INTEGER, object -> 0);
		SqlConnectionSource source = () -> null;
		return new SqlSelectQuery<>(table, SqlDialects.DEFAULT, source, Duration.ofSeconds(30), resultSet -> null);
	}
	
	private static List<SqlExpression<?>> options(int count) throws SqlException {
		List<SqlExpression<?>> options = new ArrayList<>(count);
		for (int i = 0; i < count; i++) {
			options.add(new SqlValueExpression<>(i));
		}
		return options;
	}
	
	private static int countOccurrences(String text, String needle) {
		int count = 0;
		int index = 0;
		while ((index = text.indexOf(needle, index)) != -1) {
			count++;
			index += needle.length();
		}
		return count;
	}
	
	@Test
	void constructWithDialect() {
		assertNotNull(new SqlComparisonConditionRenderer(SqlDialects.DEFAULT));
	}
	
	@Test
	void constructWithNullDialect() {
		assertThrows(NullPointerException.class, () -> new SqlComparisonConditionRenderer(null));
	}
	
	@Test
	void renderNullCondition() {
		assertThrows(NullPointerException.class, () -> RENDERER.render(null));
	}
	
	@Test
	void renderUnknownComparisonType() {
		SqlComparisonCondition unknown = new SqlComparisonCondition() {};
		assertThrows(SqlDialectUnknownConstructException.class, () -> RENDERER.render(unknown));
	}
	
	@Test
	void renderBetweenNullDirect() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderBetween(null));
	}
	
	@Test
	void renderEqualToNullDirect() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderEqualTo(null));
	}
	
	@Test
	void renderGreaterThanNullDirect() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderGreaterThan(null));
	}
	
	@Test
	void renderInListNullDirect() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderInList(null));
	}
	
	@Test
	void renderInListGroupWithNullValue() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderInListGroup(null, List.of(new SqlValueExpression<>(1))));
	}
	
	@Test
	void renderInListGroupWithNullOptions() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderInListGroup(new SqlValueExpression<>(1), null));
	}
	
	@Test
	void renderInQueryNullDirect() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderInQuery(null));
	}
	
	@Test
	void renderIsDistinctFromNullDirect() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderIsDistinctFrom(null));
	}
	
	@Test
	void renderIsNullNullDirect() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderIsNull(null));
	}
	
	@Test
	void renderLessThanNullDirect() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderLessThan(null));
	}
	
	@Test
	void renderBetweenCondition() throws SqlException {
		SqlBetweenCondition condition = new SqlBetweenCondition(new SqlValueExpression<>(5), new SqlValueExpression<>(1), new SqlValueExpression<>(9));
		SqlRendered rendered = RENDERER.render(condition);
		assertTrue(rendered.sql().contains("BETWEEN"));
		assertTrue(rendered.sql().contains("AND"));
	}
	
	@Test
	void renderEqualToCondition() throws SqlException {
		SqlEqualToCondition condition = new SqlEqualToCondition(new SqlValueExpression<>(5), new SqlValueExpression<>(5));
		SqlRendered rendered = RENDERER.render(condition);
		assertTrue(rendered.sql().contains("="));
	}
	
	@Test
	void renderGreaterThanStrict() throws SqlException {
		SqlGreaterThanCondition condition = new SqlGreaterThanCondition(new SqlValueExpression<>(5), new SqlValueExpression<>(1), false);
		SqlRendered rendered = RENDERER.render(condition);
		assertTrue(rendered.sql().contains(">"));
		assertFalse(rendered.sql().contains(">="));
	}
	
	@Test
	void renderGreaterThanOrEqual() throws SqlException {
		SqlGreaterThanCondition condition = new SqlGreaterThanCondition(new SqlValueExpression<>(5), new SqlValueExpression<>(1), true);
		SqlRendered rendered = RENDERER.render(condition);
		assertTrue(rendered.sql().contains(">="));
	}
	
	@Test
	void renderLessThanStrict() throws SqlException {
		SqlLessThanCondition condition = new SqlLessThanCondition(new SqlValueExpression<>(5), new SqlValueExpression<>(9), false);
		SqlRendered rendered = RENDERER.render(condition);
		assertTrue(rendered.sql().contains("<"));
		assertFalse(rendered.sql().contains("<="));
	}
	
	@Test
	void renderLessThanOrEqual() throws SqlException {
		SqlLessThanCondition condition = new SqlLessThanCondition(new SqlValueExpression<>(5), new SqlValueExpression<>(9), true);
		SqlRendered rendered = RENDERER.render(condition);
		assertTrue(rendered.sql().contains("<="));
	}
	
	@Test
	void renderInListSmallGroup() throws SqlException {
		SqlInListCondition condition = new SqlInListCondition(new SqlValueExpression<>(5), options(3));
		SqlRendered rendered = RENDERER.render(condition);
		assertTrue(rendered.sql().contains("IN("));
		assertFalse(rendered.sql().contains(" OR "));
	}
	
	@Test
	void renderInListLargeBatched() throws SqlException {
		SqlInListCondition condition = new SqlInListCondition(new SqlValueExpression<>(5), options(1500));
		SqlRendered rendered = RENDERER.render(condition);
		assertTrue(rendered.sql().startsWith("("));
		assertTrue(rendered.sql().contains(" OR "));
		assertEquals(1, countOccurrences(rendered.sql(), " OR "));
	}
	
	@Test
	void renderInListBoundaryExactlyMax() throws SqlException {
		SqlInListCondition condition = new SqlInListCondition(new SqlValueExpression<>(5), options(1000));
		SqlRendered rendered = RENDERER.render(condition);
		assertTrue(rendered.sql().contains("IN("));
		assertFalse(rendered.sql().contains(" OR "));
	}
	
	@Test
	void renderInQueryCondition() throws SqlException {
		SqlInQueryCondition condition = new SqlInQueryCondition(new SqlValueExpression<>(5), selectQuery());
		SqlRendered rendered = RENDERER.render(condition);
		assertTrue(rendered.sql().contains("IN("));
		assertTrue(rendered.sql().contains("SELECT"));
	}
	
	@Test
	void renderIsDistinctFromCondition() throws SqlException {
		SqlIsDistinctFromCondition condition = new SqlIsDistinctFromCondition(new SqlValueExpression<>(5), new SqlValueExpression<>(9));
		SqlRendered rendered = RENDERER.render(condition);
		assertTrue(rendered.sql().contains("IS DISTINCT FROM"));
	}
	
	@Test
	void renderIsNullCondition() throws SqlException {
		SqlIsNullCondition condition = new SqlIsNullCondition(new SqlValueExpression<>(5));
		SqlRendered rendered = RENDERER.render(condition);
		assertTrue(rendered.sql().contains("IS NULL"));
	}
	
	@Test
	void renderInListSingleOption() throws SqlException {
		SqlInListCondition condition = new SqlInListCondition(new SqlValueExpression<>(5), options(1));
		SqlRendered rendered = RENDERER.render(condition);
		assertTrue(rendered.sql().contains("IN("));
		assertFalse(rendered.sql().contains(" OR "));
	}
	
	@Test
	void renderInListMultiBatchThreeGroups() throws SqlException {
		SqlInListCondition condition = new SqlInListCondition(new SqlValueExpression<>(5), options(2500));
		SqlRendered rendered = RENDERER.render(condition);
		assertTrue(rendered.sql().startsWith("("));
		assertEquals(2, countOccurrences(rendered.sql(), " OR "));
	}
}
