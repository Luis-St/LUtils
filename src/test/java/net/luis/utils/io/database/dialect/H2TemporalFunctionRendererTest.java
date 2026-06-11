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
 * Test class for {@link H2TemporalFunctionRenderer}.<br>
 *
 * @author Luis-St
 */
class H2TemporalFunctionRendererTest {
	
	private static final H2TemporalFunctionRenderer RENDERER = new H2TemporalFunctionRenderer(SqlDialects.H2);
	
	@Test
	void constructWithDialect() {
		assertNotNull(new H2TemporalFunctionRenderer(SqlDialects.H2));
	}
	
	@Test
	void renderFromEpochNullFunction() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderFromEpoch(null));
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
	void renderFromEpochProducesDateAdd() throws SqlException {
		SqlFromEpochFunction<?> function = new SqlFromEpochFunction<>(new SqlValueExpression<>(5), SqlTypes.LOCAL_DATE);
		String sql = RENDERER.renderFromEpoch(function).sql();
		assertTrue(sql.contains("DATEADD"));
		assertTrue(sql.contains("'SECOND'"));
		assertTrue(sql.contains("TIMESTAMP '1970-01-01 00:00:00'"));
	}
	
	@Test
	void renderTemporalAddProducesDateAdd() throws SqlException {
		SqlTemporalAddFunction<?> function = new SqlTemporalAddFunction<>(new SqlValueExpression<>(5), SqlTemporalPart.DAY, new SqlValueExpression<>(3), SqlTypes.LOCAL_DATE);
		String sql = RENDERER.renderTemporalAdd(function).sql();
		assertTrue(sql.contains("DATEADD"));
		assertTrue(sql.contains("'DAY'"));
	}
	
	@Test
	void renderTemporalSubtractNegatesSubtrahend() throws SqlException {
		SqlTemporalSubtractFunction<?> function = new SqlTemporalSubtractFunction<>(new SqlValueExpression<>(5), SqlTemporalPart.DAY, new SqlValueExpression<>(3), SqlTypes.LOCAL_DATE);
		String sql = RENDERER.renderTemporalSubtract(function).sql();
		assertTrue(sql.contains("DATEADD"));
		assertTrue(sql.contains("'DAY'"));
		assertTrue(sql.contains("-"));
	}
	
	@Test
	void renderIntervalReturnsDurationOnly() throws SqlException {
		SqlExpression<Integer> duration = new SqlValueExpression<>(5);
		String expected = duration.toSql(SqlDialects.H2).sql();
		String sql = RENDERER.renderInterval(duration, "DAY").sql();
		assertEquals(expected, sql);
		assertFalse(sql.contains("INTERVAL"));
		assertFalse(sql.contains("DAY"));
	}
}
