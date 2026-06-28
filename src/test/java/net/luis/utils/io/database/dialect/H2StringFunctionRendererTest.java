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

import net.luis.utils.io.database.SqlTestFixtures;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.expression.SqlExpression;
import net.luis.utils.io.database.function.functions.string.*;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link H2StringFunctionRenderer}.<br>
 *
 * @author Luis-St
 */
class H2StringFunctionRendererTest {
	
	private static final H2StringFunctionRenderer RENDERER = new H2StringFunctionRenderer(SqlDialects.H2);
	
	@Test
	void renderHexNullFunction() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderHex(null));
	}
	
	@Test
	void renderUnhexNullFunction() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderUnhex(null));
	}
	
	@Test
	void renderConcatNullFunction() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderConcat(null));
	}
	
	@Test
	void renderHexProducesRawToHex() throws SqlException {
		String sql = RENDERER.renderHex(new SqlHexFunction(SqlTestFixtures.stringExpression())).sql();
		assertTrue(sql.contains("UPPER("));
		assertTrue(sql.contains("RAWTOHEX("));
		assertTrue(sql.contains("VARBINARY"));
	}
	
	@Test
	void renderUnhexProducesHexToRaw() throws SqlException {
		SqlUnhexFunction<String> function = new SqlUnhexFunction<>(SqlTestFixtures.stringExpression(), SqlTestFixtures.STRING_TYPE);
		String sql = RENDERER.renderUnhex(function).sql();
		assertTrue(sql.contains("HEXTORAW("));
		assertTrue(sql.contains("REGEXP_REPLACE("));
		assertTrue(sql.contains("'00$1'"));
		assertTrue(sql.contains("VARBINARY"));
	}
	
	@Test
	void renderConcatPlainDelegatesToSuper() throws SqlException {
		List<SqlExpression<String>> expressions = List.of(SqlTestFixtures.stringExpression(), SqlTestFixtures.stringExpression());
		String sql = RENDERER.renderConcat(new SqlConcatFunction<>(expressions, Optional.empty(), false, false)).sql();
		assertFalse(sql.contains("GROUP_CONCAT"));
	}
	
	@Test
	void renderConcatDistinctUsesGroupConcat() throws SqlException {
		List<SqlExpression<String>> expressions = List.of(SqlTestFixtures.stringExpression(), SqlTestFixtures.stringExpression());
		String sql = RENDERER.renderConcat(new SqlConcatFunction<>(expressions, Optional.empty(), true, false)).sql();
		assertTrue(sql.contains("GROUP_CONCAT("));
		assertTrue(sql.contains("DISTINCT"));
	}
	
	@Test
	void renderConcatOrderedUsesGroupConcat() throws SqlException {
		List<SqlExpression<String>> expressions = List.of(SqlTestFixtures.stringExpression(), SqlTestFixtures.stringExpression());
		String sql = RENDERER.renderConcat(new SqlConcatFunction<>(expressions, Optional.empty(), false, true)).sql();
		assertTrue(sql.contains("GROUP_CONCAT("));
		assertTrue(sql.contains("ORDER BY"));
	}
	
	@Test
	void renderConcatWithSeparatorEmitsSeparator() throws SqlException {
		List<SqlExpression<String>> expressions = List.of(SqlTestFixtures.stringExpression(), SqlTestFixtures.stringExpression());
		String sql = RENDERER.renderConcat(new SqlConcatFunction<>(expressions, Optional.of(", "), false, true)).sql();
		assertTrue(sql.contains("SEPARATOR"));
	}
}
