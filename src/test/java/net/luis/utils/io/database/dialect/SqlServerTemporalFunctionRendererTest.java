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
import net.luis.utils.io.database.expression.SqlExpression;
import net.luis.utils.io.database.expression.SqlValueExpression;
import net.luis.utils.io.database.function.functions.temporal.*;
import net.luis.utils.io.database.type.SqlTypes;
import net.luis.utils.io.database.util.SqlTemporalPart;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlServerTemporalFunctionRenderer}.<br>
 *
 * @author Luis-St
 */
class SqlServerTemporalFunctionRendererTest {
	
	private static final SqlServerTemporalFunctionRenderer RENDERER = new SqlServerTemporalFunctionRenderer(SqlDialects.SQL_SERVER);
	
	@Test
	void renderFromEpochNullFunction() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderFromEpoch(null));
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
	void renderTemporalTruncateNullFunction() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderTemporalTruncate(null));
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
	void renderFromEpochUsesDateAddSecond() throws SqlException {
		SqlFromEpochFunction<?> function = new SqlFromEpochFunction<>(new SqlValueExpression<>(5), SqlTypes.LOCAL_DATE);
		String sql = RENDERER.renderFromEpoch(function).sql();
		assertTrue(sql.contains("DATEADD("));
		assertTrue(sql.contains("SECOND"));
		assertTrue(sql.contains("'1970-01-01'"));
	}
	
	@Test
	void renderToEpochUsesDateDiffSecond() throws SqlException {
		SqlToEpochFunction<?> function = new SqlToEpochFunction<>(new SqlValueExpression<>(5), SqlTypes.LONG);
		String sql = RENDERER.renderToEpoch(function).sql();
		assertTrue(sql.contains("DATEDIFF_BIG("));
		assertTrue(sql.contains("SECOND"));
		assertTrue(sql.contains("'1970-01-01'"));
	}
	
	@Test
	void renderTemporalAddUsesDateAdd() throws SqlException {
		SqlTemporalAddFunction<?> function = new SqlTemporalAddFunction<>(new SqlValueExpression<>(5), SqlTemporalPart.DAY, new SqlValueExpression<>(3), SqlTypes.LOCAL_DATE);
		String sql = RENDERER.renderTemporalAdd(function).sql();
		assertTrue(sql.contains("DATEADD("));
		assertTrue(sql.contains("DAY"));
	}
	
	@Test
	void renderTemporalSubtractNegatesSubtrahend() throws SqlException {
		SqlTemporalSubtractFunction<?> function = new SqlTemporalSubtractFunction<>(new SqlValueExpression<>(5), SqlTemporalPart.DAY, new SqlValueExpression<>(3), SqlTypes.LOCAL_DATE);
		String sql = RENDERER.renderTemporalSubtract(function).sql();
		assertTrue(sql.contains("DATEADD("));
		assertTrue(sql.contains("-"));
	}
	
	@Test
	void renderNowUsesGetdate() throws SqlException {
		assertEquals("GETDATE()", RENDERER.renderNow().sql());
	}
	
	@Test
	void renderCurrentTimestampUsesGetdate() throws SqlException {
		assertEquals("GETDATE()", RENDERER.renderCurrentTimestamp().sql());
	}
	
	@Test
	void renderTemporalTruncateUsesDatetrunc() throws SqlException {
		SqlTemporalTruncateFunction<?> function = new SqlTemporalTruncateFunction<>(new SqlValueExpression<>(5), SqlTemporalPart.DAY, SqlTypes.LOCAL_DATE);
		String sql = RENDERER.renderTemporalTruncate(function).sql();
		assertTrue(sql.contains("DATETRUNC("));
		assertTrue(sql.contains("DAY"));
	}
	
	@Test
	void renderIntervalReturnsDurationOnly() throws SqlException {
		SqlExpression<Integer> duration = new SqlValueExpression<>(5);
		String expected = duration.toSql(SqlDialects.SQL_SERVER).sql();
		String sql = RENDERER.renderInterval(duration, "DAY").sql();
		assertEquals(expected, sql);
		assertFalse(sql.contains("DAY"));
	}
}
