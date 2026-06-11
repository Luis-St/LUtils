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

import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.expression.SqlValueExpression;
import net.luis.utils.io.database.function.functions.temporal.*;
import net.luis.utils.io.database.type.SqlTypes;
import net.luis.utils.io.database.util.SqlTemporalPart;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link MySqlTemporalFunctionRenderer}.<br>
 *
 * @author Luis-St
 */
class MySqlTemporalFunctionRendererTest {
	
	private static final MySqlTemporalFunctionRenderer RENDERER = new MySqlTemporalFunctionRenderer(SqlDialects.MYSQL);
	
	@Test
	void renderFromEpochNullFunction() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderFromEpoch(null));
	}
	
	@Test
	void renderMakeDateNullFunction() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderMakeDate(null));
	}
	
	@Test
	void renderMakeTimeNullFunction() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderMakeTime(null));
	}
	
	@Test
	void renderToEpochNullFunction() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderToEpoch(null));
	}
	
	@Test
	void renderTemporalAddNullFunction() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderTemporalAdd(null));
	}
	
	@Test
	void renderTemporalSubtractNullFunction() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderTemporalSubtract(null));
	}
	
	@Test
	void renderIntervalNullDuration() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderInterval(null, "DAY"));
	}
	
	@Test
	void renderIntervalNullPart() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderInterval(new SqlValueExpression<>(5), null));
	}
	
	@Test
	void renderFromEpochUsesFromUnixtime() throws SqlException {
		SqlFromEpochFunction<?> function = new SqlFromEpochFunction<>(new SqlValueExpression<>(5), SqlTypes.LOCAL_DATE);
		assertTrue(RENDERER.renderFromEpoch(function).sql().contains("FROM_UNIXTIME("));
	}
	
	@Test
	void renderToEpochUsesUnixTimestamp() throws SqlException {
		SqlToEpochFunction<?> function = new SqlToEpochFunction<>(new SqlValueExpression<>(5), SqlTypes.LONG);
		assertTrue(RENDERER.renderToEpoch(function).sql().contains("UNIX_TIMESTAMP("));
	}
	
	@Test
	void renderMakeDateUsesStrToDate() throws SqlException {
		SqlMakeDateFunction<?> function = new SqlMakeDateFunction<>(new SqlValueExpression<>(2026), new SqlValueExpression<>(6), new SqlValueExpression<>(7), SqlTypes.LOCAL_DATE);
		String sql = RENDERER.renderMakeDate(function).sql();
		assertTrue(sql.contains("STR_TO_DATE("));
		assertTrue(sql.contains("CONCAT("));
		assertTrue(sql.contains("'%Y-%m-%d'"));
	}
	
	@Test
	void renderMakeTimeUsesMaketime() throws SqlException {
		SqlMakeTimeFunction<?> function = new SqlMakeTimeFunction<>(new SqlValueExpression<>(12), new SqlValueExpression<>(30), new SqlValueExpression<>(0), SqlTypes.LOCAL_DATE);
		assertTrue(RENDERER.renderMakeTime(function).sql().contains("MAKETIME("));
	}
	
	@Test
	void renderTemporalAddUsesDateAdd() throws SqlException {
		SqlTemporalAddFunction<?> function = new SqlTemporalAddFunction<>(new SqlValueExpression<>(5), SqlTemporalPart.DAY, new SqlValueExpression<>(3), SqlTypes.LOCAL_DATE);
		String sql = RENDERER.renderTemporalAdd(function).sql();
		assertTrue(sql.contains("DATE_ADD("));
		assertTrue(sql.contains("INTERVAL"));
		assertTrue(sql.contains("DAY"));
	}
	
	@Test
	void renderTemporalSubtractUsesDateSub() throws SqlException {
		SqlTemporalSubtractFunction<?> function = new SqlTemporalSubtractFunction<>(new SqlValueExpression<>(5), SqlTemporalPart.DAY, new SqlValueExpression<>(3), SqlTypes.LOCAL_DATE);
		String sql = RENDERER.renderTemporalSubtract(function).sql();
		assertTrue(sql.contains("DATE_SUB("));
		assertTrue(sql.contains("INTERVAL"));
		assertTrue(sql.contains("DAY"));
	}
	
	@Test
	void renderIntervalProducesIntervalClause() throws SqlException {
		String sql = RENDERER.renderInterval(new SqlValueExpression<>(5), "DAY").sql();
		assertTrue(sql.contains("INTERVAL"));
		assertTrue(sql.contains("DAY"));
	}
}
