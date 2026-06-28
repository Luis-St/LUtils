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
import net.luis.utils.io.database.function.functions.string.SqlConcatFunction;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link MySqlStringFunctionRenderer}.<br>
 *
 * @author Luis-St
 */
class MySqlStringFunctionRendererTest {
	
	private static final MySqlStringFunctionRenderer RENDERER = new MySqlStringFunctionRenderer(SqlDialects.MYSQL);
	
	private static SqlValueExpression<String> value(String value) throws SqlException {
		return new SqlValueExpression<>(value);
	}
	
	@Test
	void renderConcatNullFunction() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderConcat(null));
	}
	
	@Test
	void renderConcatPlain() throws SqlException {
		SqlConcatFunction<String> function = new SqlConcatFunction<>(Arrays.asList(value("a"), value("b")), Optional.empty(), false, false);
		String sql = RENDERER.renderConcat(function).sql();
		assertTrue(sql.contains("CONCAT("));
		assertTrue(sql.contains(","));
		assertFalse(sql.contains("CONCAT_WS"));
		assertFalse(sql.contains("GROUP_CONCAT"));
	}
	
	@Test
	void renderConcatWithSeparator() throws SqlException {
		SqlConcatFunction<String> function = new SqlConcatFunction<>(Arrays.asList(value("a"), value("b")), Optional.of(", "), false, false);
		String sql = RENDERER.renderConcat(function).sql();
		assertTrue(sql.contains("CONCAT_WS("));
	}
	
	@Test
	void renderConcatDistinctUsesGroupConcat() throws SqlException {
		SqlConcatFunction<String> function = new SqlConcatFunction<>(Arrays.asList(value("a"), value("b")), Optional.empty(), true, false);
		String sql = RENDERER.renderConcat(function).sql();
		assertTrue(sql.contains("GROUP_CONCAT("));
		assertTrue(sql.contains("DISTINCT"));
	}
	
	@Test
	void renderConcatOrderedAddsOrderBy() throws SqlException {
		SqlConcatFunction<String> function = new SqlConcatFunction<>(Arrays.asList(value("a"), value("b")), Optional.empty(), false, true);
		String sql = RENDERER.renderConcat(function).sql();
		assertTrue(sql.contains("GROUP_CONCAT("));
		assertTrue(sql.contains("ORDER BY"));
	}
	
	@Test
	void renderConcatGroupConcatWithSeparator() throws SqlException {
		SqlConcatFunction<String> function = new SqlConcatFunction<>(Arrays.asList(value("a"), value("b")), Optional.of(", "), true, false);
		String sql = RENDERER.renderConcat(function).sql();
		assertTrue(sql.contains("GROUP_CONCAT("));
		assertTrue(sql.contains("SEPARATOR"));
	}
}
