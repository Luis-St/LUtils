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
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link PostgresSqlTemporalFunctionRenderer}.<br>
 *
 * @author Luis-St
 */
class PostgresSqlTemporalFunctionRendererTest {
	
	private static final PostgresSqlTemporalFunctionRenderer RENDERER = new PostgresSqlTemporalFunctionRenderer(SqlDialects.POSTGRESQL);
	
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
	void renderFromEpochUsesToTimestamp() throws SqlException {
		SqlFromEpochFunction<?> function = new SqlFromEpochFunction<>(new SqlValueExpression<>(5), SqlTypes.LOCAL_DATE);
		assertTrue(RENDERER.renderFromEpoch(function).sql().contains("TO_TIMESTAMP("));
	}
	
	@Test
	void renderMakeDateUsesMakeDate() throws SqlException {
		SqlMakeDateFunction<?> function = new SqlMakeDateFunction<>(new SqlValueExpression<>(2026), new SqlValueExpression<>(6), new SqlValueExpression<>(7), SqlTypes.LOCAL_DATE);
		assertTrue(RENDERER.renderMakeDate(function).sql().contains("MAKE_DATE("));
	}
	
	@Test
	void renderMakeTimeUsesMakeTime() throws SqlException {
		SqlMakeTimeFunction<?> function = new SqlMakeTimeFunction<>(new SqlValueExpression<>(12), new SqlValueExpression<>(30), new SqlValueExpression<>(0), SqlTypes.LOCAL_DATE);
		assertTrue(RENDERER.renderMakeTime(function).sql().contains("MAKE_TIME("));
	}
}
