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

package net.luis.utils.io.database.dialect.renderer.expression.function;

import net.luis.utils.io.database.dialect.SqlDialects;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.exception.client.dialect.SqlDialectUnknownConstructException;
import net.luis.utils.io.database.exception.client.dialect.SqlDialectUnsupportedRenderingException;
import net.luis.utils.io.database.expression.SqlValueExpression;
import net.luis.utils.io.database.function.functions.SqlTemporalFunction;
import net.luis.utils.io.database.function.functions.temporal.*;
import net.luis.utils.io.database.rendering.SqlRendered;
import net.luis.utils.io.database.type.SqlType;
import net.luis.utils.io.database.type.SqlTypes;
import net.luis.utils.io.database.util.SqlTemporalPart;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlTemporalFunctionRenderer}.<br>
 *
 * @author Luis-St
 */
class SqlTemporalFunctionRendererTest {
	
	private static final SqlTemporalFunctionRenderer RENDERER = new SqlTemporalFunctionRenderer(SqlDialects.DEFAULT);
	
	@Test
	void constructWithDialect() {
		assertNotNull(new SqlTemporalFunctionRenderer(SqlDialects.DEFAULT));
	}
	
	@Test
	void constructWithNullDialect() {
		assertThrows(NullPointerException.class, () -> new SqlTemporalFunctionRenderer(null));
	}
	
	@Test
	void renderNullFunction() {
		assertThrows(NullPointerException.class, () -> RENDERER.render(null));
	}
	
	@Test
	void renderUnknownTemporalType() {
		SqlTemporalFunction<Object> unknown = new UnknownTemporalFunction();
		assertThrows(SqlDialectUnknownConstructException.class, () -> RENDERER.render(unknown));
	}
	
	@Test
	void renderFromEpochUnsupported() throws SqlException {
		SqlFromEpochFunction<?> function = new SqlFromEpochFunction<>(new SqlValueExpression<>(5), SqlTypes.LOCAL_DATE);
		assertThrows(SqlDialectUnsupportedRenderingException.class, () -> RENDERER.render(function));
	}
	
	@Test
	void renderMakeDateUnsupported() throws SqlException {
		SqlMakeDateFunction<?> function = new SqlMakeDateFunction<>(new SqlValueExpression<>(2026), new SqlValueExpression<>(6), new SqlValueExpression<>(7), SqlTypes.LOCAL_DATE);
		assertThrows(SqlDialectUnsupportedRenderingException.class, () -> RENDERER.render(function));
	}
	
	@Test
	void renderMakeTimeUnsupported() throws SqlException {
		SqlMakeTimeFunction<?> function = new SqlMakeTimeFunction<>(new SqlValueExpression<>(12), new SqlValueExpression<>(30), new SqlValueExpression<>(0), SqlTypes.LOCAL_DATE);
		assertThrows(SqlDialectUnsupportedRenderingException.class, () -> RENDERER.render(function));
	}
	
	@Test
	void renderTemporalAddNullDirect() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderTemporalAdd(null));
	}
	
	@Test
	void renderExtractNullDirect() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderExtract(null));
	}
	
	@Test
	void renderTemporalSubtractNullDirect() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderTemporalSubtract(null));
	}
	
	@Test
	void renderToDateNullDirect() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderToDate(null));
	}
	
	@Test
	void renderToTimeNullDirect() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderToTime(null));
	}
	
	@Test
	void renderToEpochNullDirect() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderToEpoch(null));
	}
	
	@Test
	void renderTemporalTruncateNullDirect() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderTemporalTruncate(null));
	}
	
	@Test
	void renderIntervalWithNullDuration() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderInterval(null, "SECOND"));
	}
	
	@Test
	void renderIntervalWithNullPart() throws SqlException {
		assertThrows(NullPointerException.class, () -> RENDERER.renderInterval(new SqlValueExpression<>(5), null));
	}
	
	@Test
	void renderTemporalAddFunction() throws SqlException {
		SqlTemporalAddFunction<?> function = new SqlTemporalAddFunction<>(new SqlValueExpression<>(5), SqlTemporalPart.DAY, new SqlValueExpression<>(3), SqlTypes.LOCAL_DATE);
		SqlRendered rendered = RENDERER.render(function);
		assertTrue(rendered.sql().contains("+"));
		assertTrue(rendered.sql().contains("INTERVAL"));
	}
	
	@Test
	void renderCurrentDateFunction() throws SqlException {
		assertEquals("CURRENT_DATE", RENDERER.render(new SqlCurrentDateFunction<>(SqlTypes.LOCAL_DATE)).sql());
	}
	
	@Test
	void renderCurrentTimeFunction() throws SqlException {
		assertEquals("CURRENT_TIME", RENDERER.render(new SqlCurrentTimeFunction<>(SqlTypes.LOCAL_DATE)).sql());
	}
	
	@Test
	void renderCurrentTimestampFunction() throws SqlException {
		assertEquals("CURRENT_TIMESTAMP", RENDERER.render(new SqlCurrentTimestampFunction<>(SqlTypes.LOCAL_DATE)).sql());
	}
	
	@Test
	void renderExtractFunction() throws SqlException {
		SqlExtractFunction<?> function = new SqlExtractFunction<>(new SqlValueExpression<>(5), SqlTemporalPart.HOUR, SqlTypes.INTEGER);
		SqlRendered rendered = RENDERER.render(function);
		assertTrue(rendered.sql().contains("EXTRACT("));
		assertTrue(rendered.sql().contains("FROM"));
	}
	
	@Test
	void renderNowFunction() throws SqlException {
		assertEquals("CURRENT_TIMESTAMP", RENDERER.render(new SqlNowFunction<>(SqlTypes.LOCAL_DATE)).sql());
	}
	
	@Test
	void renderTemporalSubtractFunction() throws SqlException {
		SqlTemporalSubtractFunction<?> function = new SqlTemporalSubtractFunction<>(new SqlValueExpression<>(5), SqlTemporalPart.DAY, new SqlValueExpression<>(3), SqlTypes.LOCAL_DATE);
		SqlRendered rendered = RENDERER.render(function);
		assertTrue(rendered.sql().contains("-"));
		assertTrue(rendered.sql().contains("INTERVAL"));
	}
	
	@Test
	void renderToDateFunction() throws SqlException {
		SqlRendered rendered = RENDERER.render(new SqlToDateFunction<>(new SqlValueExpression<>("2026-06-07"), SqlTypes.LOCAL_DATE));
		assertTrue(rendered.sql().contains("CAST("));
		assertTrue(rendered.sql().contains("AS DATE"));
	}
	
	@Test
	void renderToTimeFunction() throws SqlException {
		SqlRendered rendered = RENDERER.render(new SqlToTimeFunction<>(new SqlValueExpression<>("12:30:00"), SqlTypes.LOCAL_DATE));
		assertTrue(rendered.sql().contains("CAST("));
		assertTrue(rendered.sql().contains("AS TIME"));
	}
	
	@Test
	void renderToEpochFunction() throws SqlException {
		SqlRendered rendered = RENDERER.render(new SqlToEpochFunction<>(new SqlValueExpression<>(5), SqlTypes.LONG));
		assertTrue(rendered.sql().contains("EXTRACT("));
		assertTrue(rendered.sql().contains("EPOCH"));
	}
	
	@Test
	void renderTemporalTruncateFunction() throws SqlException {
		SqlTemporalTruncateFunction<?> function = new SqlTemporalTruncateFunction<>(new SqlValueExpression<>(5), SqlTemporalPart.DAY, SqlTypes.LOCAL_DATE);
		SqlRendered rendered = RENDERER.render(function);
		assertTrue(rendered.sql().contains("DATE_TRUNC("));
		assertTrue(rendered.sql().contains("'DAY'"));
	}
	
	@Test
	void renderIntervalDirectly() throws SqlException {
		SqlRendered rendered = RENDERER.renderInterval(new SqlValueExpression<>(30), "SECOND");
		assertTrue(rendered.sql().contains("* INTERVAL '1'"));
		assertTrue(rendered.sql().contains("SECOND"));
	}
	
	@Test
	void renderTemporalAddUsesPartName() throws SqlException {
		SqlTemporalAddFunction<?> function = new SqlTemporalAddFunction<>(new SqlValueExpression<>(5), SqlTemporalPart.DAY, new SqlValueExpression<>(3), SqlTypes.LOCAL_DATE);
		SqlRendered rendered = RENDERER.render(function);
		assertTrue(rendered.sql().contains("DAY"));
	}
	
	private static final class UnknownTemporalFunction implements SqlTemporalFunction<Object> {
		
		@Override
		public @NonNull SqlType<Object> type() {
			throw new UnsupportedOperationException();
		}
	}
}
