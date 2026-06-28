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
import net.luis.utils.io.database.expression.SqlValueExpression;
import net.luis.utils.io.database.function.functions.SqlStringFunction;
import net.luis.utils.io.database.function.functions.string.*;
import net.luis.utils.io.database.rendering.SqlRendered;
import net.luis.utils.io.database.type.SqlType;
import net.luis.utils.io.database.type.SqlTypes;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlStringFunctionRenderer}.<br>
 *
 * @author Luis-St
 */
class SqlStringFunctionRendererTest {
	
	private static final SqlStringFunctionRenderer RENDERER = new SqlStringFunctionRenderer(SqlDialects.DEFAULT);
	
	private static SqlValueExpression<String> value(String value) throws SqlException {
		return new SqlValueExpression<>(value);
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
		assertNotNull(new SqlStringFunctionRenderer(SqlDialects.DEFAULT));
	}
	
	@Test
	void constructWithNullDialect() {
		assertThrows(NullPointerException.class, () -> new SqlStringFunctionRenderer(null));
	}
	
	@Test
	void renderNullFunction() {
		assertThrows(NullPointerException.class, () -> RENDERER.render(null));
	}
	
	@Test
	void renderUnknownStringType() {
		SqlStringFunction<Object> unknown = new UnknownStringFunction();
		assertThrows(SqlDialectUnknownConstructException.class, () -> RENDERER.render(unknown));
	}
	
	@Test
	void renderConcatNullDirect() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderConcat(null));
	}
	
	@Test
	void renderHexNullDirect() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderHex(null));
	}
	
	@Test
	void renderLeftNullDirect() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderLeft(null));
	}
	
	@Test
	void renderLeftPadNullDirect() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderLeftPad(null));
	}
	
	@Test
	void renderLeftTrimNullDirect() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderLeftTrim(null));
	}
	
	@Test
	void renderLengthNullDirect() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderLength(null));
	}
	
	@Test
	void renderLowerNullDirect() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderLower(null));
	}
	
	@Test
	void renderPositionNullDirect() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderPosition(null));
	}
	
	@Test
	void renderReplaceNullDirect() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderReplace(null));
	}
	
	@Test
	void renderRightNullDirect() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderRight(null));
	}
	
	@Test
	void renderRightPadNullDirect() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderRightPad(null));
	}
	
	@Test
	void renderRightTrimNullDirect() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderRightTrim(null));
	}
	
	@Test
	void renderSubstringNullDirect() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderSubstring(null));
	}
	
	@Test
	void renderTrimCharsNullDirect() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderTrimChars(null));
	}
	
	@Test
	void renderTrimNullDirect() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderTrim(null));
	}
	
	@Test
	void renderUnhexNullDirect() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderUnhex(null));
	}
	
	@Test
	void renderUpperNullDirect() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderUpper(null));
	}
	
	@Test
	void renderConcatSimple() throws SqlException {
		SqlConcatFunction<String> function = new SqlConcatFunction<>(Arrays.asList(value("a"), value("b")), Optional.empty(), false, false);
		SqlRendered rendered = RENDERER.render(function);
		assertTrue(rendered.sql().contains("||"));
		assertFalse(rendered.sql().contains("STRING_AGG"));
	}
	
	@Test
	void renderConcatSimpleWithSeparator() throws SqlException {
		SqlConcatFunction<String> function = new SqlConcatFunction<>(Arrays.asList(value("a"), value("b")), Optional.of(", "), false, false);
		SqlRendered rendered = RENDERER.render(function);
		assertTrue(rendered.sql().contains("||"));
		assertEquals(3, rendered.parameters().size());
	}
	
	@Test
	void renderConcatSingleValueNoSeparator() throws SqlException {
		SqlConcatFunction<String> function = new SqlConcatFunction<>(List.of(value("a")), Optional.empty(), false, false);
		SqlRendered rendered = RENDERER.render(function);
		assertFalse(rendered.sql().contains("||"));
	}
	
	@Test
	void renderConcatDistinct() throws SqlException {
		SqlConcatFunction<String> function = new SqlConcatFunction<>(Arrays.asList(value("a"), value("b")), Optional.empty(), true, false);
		SqlRendered rendered = RENDERER.render(function);
		assertTrue(rendered.sql().contains("STRING_AGG("));
		assertTrue(rendered.sql().contains("DISTINCT"));
	}
	
	@Test
	void renderConcatOrdered() throws SqlException {
		SqlConcatFunction<String> function = new SqlConcatFunction<>(Arrays.asList(value("a"), value("b")), Optional.empty(), false, true);
		SqlRendered rendered = RENDERER.render(function);
		assertTrue(rendered.sql().contains("STRING_AGG("));
		assertTrue(rendered.sql().contains("ORDER BY"));
	}
	
	@Test
	void renderHexFunction() throws SqlException {
		assertTrue(RENDERER.render(new SqlHexFunction(value("a"), SqlTypes.TEXT)).sql().contains("HEX("));
	}
	
	@Test
	void renderLeftFunction() throws SqlException {
		assertTrue(RENDERER.render(new SqlLeftFunction<>(value("a"), new SqlValueExpression<>(2))).sql().contains("LEFT("));
	}
	
	@Test
	void renderLeftPadFunction() throws SqlException {
		assertTrue(RENDERER.render(new SqlLeftPadFunction<>(value("a"), new SqlValueExpression<>(5), value("x"))).sql().contains("LPAD("));
	}
	
	@Test
	void renderLeftTrimFunction() throws SqlException {
		assertTrue(RENDERER.render(new SqlLeftTrimFunction<>(value("a"))).sql().contains("LTRIM("));
	}
	
	@Test
	void renderLengthFunction() throws SqlException {
		assertTrue(RENDERER.render(new SqlLengthFunction<>(value("a"), SqlTypes.INTEGER)).sql().contains("LENGTH("));
	}
	
	@Test
	void renderLowerFunction() throws SqlException {
		assertTrue(RENDERER.render(new SqlLowerFunction<>(value("a"))).sql().contains("LOWER("));
	}
	
	@Test
	void renderPositionFunction() throws SqlException {
		SqlRendered rendered = RENDERER.render(new SqlPositionFunction<>(value("a"), value("abc"), SqlTypes.INTEGER));
		assertTrue(rendered.sql().contains("POSITION("));
		assertTrue(rendered.sql().contains("IN"));
	}
	
	@Test
	void renderReplaceFunction() throws SqlException {
		assertTrue(RENDERER.render(new SqlReplaceFunction<>(value("a"), value("b"), value("c"))).sql().contains("REPLACE("));
	}
	
	@Test
	void renderRightFunction() throws SqlException {
		assertTrue(RENDERER.render(new SqlRightFunction<>(value("a"), new SqlValueExpression<>(2))).sql().contains("RIGHT("));
	}
	
	@Test
	void renderRightPadFunction() throws SqlException {
		assertTrue(RENDERER.render(new SqlRightPadFunction<>(value("a"), new SqlValueExpression<>(5), value("x"))).sql().contains("RPAD("));
	}
	
	@Test
	void renderRightTrimFunction() throws SqlException {
		assertTrue(RENDERER.render(new SqlRightTrimFunction<>(value("a"))).sql().contains("RTRIM("));
	}
	
	@Test
	void renderSubstringWithLength() throws SqlException {
		SqlRendered rendered = RENDERER.render(new SqlSubstringFunction<>(value("abc"), new SqlValueExpression<>(1), new SqlValueExpression<>(2)));
		assertTrue(rendered.sql().contains("SUBSTRING("));
		assertTrue(rendered.sql().contains("FROM"));
		assertTrue(rendered.sql().contains("FOR"));
	}
	
	@Test
	void renderSubstringWithoutLength() throws SqlException {
		SqlRendered rendered = RENDERER.render(new SqlSubstringFunction<>(value("abc"), new SqlValueExpression<>(1), null));
		assertTrue(rendered.sql().contains("SUBSTRING("));
		assertTrue(rendered.sql().contains("FROM"));
		assertFalse(rendered.sql().contains("FOR"));
	}
	
	@Test
	void renderTrimCharsFunction() throws SqlException {
		SqlRendered rendered = RENDERER.render(new SqlTrimCharsFunction<>(value("abc"), value("a")));
		assertTrue(rendered.sql().contains("TRIM("));
		assertTrue(rendered.sql().contains("FROM"));
	}
	
	@Test
	void renderTrimFunction() throws SqlException {
		assertTrue(RENDERER.render(new SqlTrimFunction<>(value("a"))).sql().contains("TRIM("));
	}
	
	@Test
	void renderUnhexFunction() throws SqlException {
		assertTrue(RENDERER.render(new SqlUnhexFunction<>(value("a"), SqlTypes.TEXT)).sql().contains("UNHEX("));
	}
	
	@Test
	void renderUpperFunction() throws SqlException {
		assertTrue(RENDERER.render(new SqlUpperFunction<>(value("a"))).sql().contains("UPPER("));
	}
	
	@Test
	void renderConcatThreeValues() throws SqlException {
		SqlConcatFunction<String> function = new SqlConcatFunction<>(Arrays.asList(value("a"), value("b"), value("c")), Optional.empty(), false, false);
		SqlRendered rendered = RENDERER.render(function);
		assertEquals(2, countOccurrences(rendered.sql(), "||"));
	}
	
	@Test
	void renderConcatDistinctOrderedWithSeparator() throws SqlException {
		SqlConcatFunction<String> function = new SqlConcatFunction<>(Arrays.asList(value("a"), value("b")), Optional.of(", "), true, true);
		SqlRendered rendered = RENDERER.render(function);
		assertTrue(rendered.sql().contains("STRING_AGG("));
		assertTrue(rendered.sql().contains("DISTINCT"));
		assertTrue(rendered.sql().contains("ORDER BY"));
	}
	
	private static final class UnknownStringFunction implements SqlStringFunction<Object> {
		
		@Override
		public @NonNull SqlType<Object> type() {
			throw new UnsupportedOperationException();
		}
	}
}
