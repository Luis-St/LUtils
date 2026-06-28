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
 * Test class for {@link SqliteTemporalFunctionRenderer}.<br>
 *
 * @author Luis-St
 */
class SqliteTemporalFunctionRendererTest {
	
	private static final SqliteTemporalFunctionRenderer RENDERER = new SqliteTemporalFunctionRenderer(SqlDialects.SQLITE);
	
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
	void renderToDateNullFunction() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderToDate(null));
	}
	
	@Test
	void renderToTimeNullFunction() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderToTime(null));
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
	void renderIntervalKnownPartMapsToPlural() throws SqlException {
		assertTrue(RENDERER.renderInterval(new SqlValueExpression<>(5), "DAY").sql().contains("|| ' days'"));
		assertTrue(RENDERER.renderInterval(new SqlValueExpression<>(5), "MONTH").sql().contains("|| ' months'"));
		assertTrue(RENDERER.renderInterval(new SqlValueExpression<>(5), "SECOND").sql().contains("|| ' seconds'"));
	}
	
	@Test
	void renderIntervalUnknownPartLowercasesAndAddsS() throws SqlException {
		assertTrue(RENDERER.renderInterval(new SqlValueExpression<>(5), "WEEK").sql().contains("|| ' weeks'"));
	}
	
	@Test
	void renderFromEpochUsesUnixepoch() throws SqlException {
		SqlFromEpochFunction<?> function = new SqlFromEpochFunction<>(new SqlValueExpression<>(5), SqlTypes.LOCAL_DATE);
		String sql = RENDERER.renderFromEpoch(function).sql();
		assertTrue(sql.contains("datetime("));
		assertTrue(sql.contains("'unixepoch'"));
	}
	
	@Test
	void renderToEpochUsesStrftime() throws SqlException {
		SqlToEpochFunction<?> function = new SqlToEpochFunction<>(new SqlValueExpression<>(5), SqlTypes.LONG);
		String sql = RENDERER.renderToEpoch(function).sql();
		assertTrue(sql.contains("strftime("));
		assertTrue(sql.contains("'%s'"));
	}
	
	@Test
	void renderNowProducesDatetimeNow() throws SqlException {
		assertEquals("datetime('now')", RENDERER.renderNow().sql());
	}
	
	@Test
	void renderCurrentDateProducesDateNow() throws SqlException {
		assertEquals("date('now')", RENDERER.renderCurrentDate().sql());
	}
	
	@Test
	void renderCurrentTimeProducesTimeNow() throws SqlException {
		assertEquals("time('now')", RENDERER.renderCurrentTime().sql());
	}
	
	@Test
	void renderCurrentTimestampProducesDatetimeNow() throws SqlException {
		assertEquals("datetime('now')", RENDERER.renderCurrentTimestamp().sql());
	}
	
	@Test
	void renderTemporalAddBuildsModifierConcat() throws SqlException {
		SqlTemporalAddFunction<?> function = new SqlTemporalAddFunction<>(new SqlValueExpression<>(5), SqlTemporalPart.DAY, new SqlValueExpression<>(3), SqlTypes.LOCAL_DATE);
		String sql = RENDERER.renderTemporalAdd(function).sql();
		assertTrue(sql.contains("datetime("));
		assertTrue(sql.contains("'+' ||"));
		assertTrue(sql.contains("|| ' days'"));
	}
	
	@Test
	void renderTemporalSubtractBuildsModifierConcat() throws SqlException {
		SqlTemporalSubtractFunction<?> function = new SqlTemporalSubtractFunction<>(new SqlValueExpression<>(5), SqlTemporalPart.DAY, new SqlValueExpression<>(3), SqlTypes.LOCAL_DATE);
		String sql = RENDERER.renderTemporalSubtract(function).sql();
		assertTrue(sql.contains("'-' ||"));
		assertTrue(sql.contains("|| ' days'"));
	}
	
	@Test
	void renderToDateUsesDate() throws SqlException {
		SqlToDateFunction<?> function = new SqlToDateFunction<>(new SqlValueExpression<>("2026-06-07"), SqlTypes.LOCAL_DATE);
		assertTrue(RENDERER.renderToDate(function).sql().contains("date("));
	}
	
	@Test
	void renderToTimeUsesTime() throws SqlException {
		SqlToTimeFunction<?> function = new SqlToTimeFunction<>(new SqlValueExpression<>("12:30:00"), SqlTypes.LOCAL_DATE);
		assertTrue(RENDERER.renderToTime(function).sql().contains("time("));
	}
}
