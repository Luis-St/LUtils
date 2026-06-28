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
import net.luis.utils.io.database.function.functions.numeric.*;
import net.luis.utils.io.database.function.functions.numeric.bitwise.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link H2NumericFunctionRenderer}.<br>
 *
 * @author Luis-St
 */
class H2NumericFunctionRendererTest {
	
	private static final H2NumericFunctionRenderer RENDERER = new H2NumericFunctionRenderer(SqlDialects.H2);
	
	@Test
	void renderCeilNullFunction() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderCeil(null));
	}
	
	@Test
	void renderFloorNullFunction() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderFloor(null));
	}
	
	@Test
	void renderModNullFunction() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderMod(null));
	}
	
	@Test
	void renderRoundNullFunction() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderRound(null));
	}
	
	@Test
	void renderTruncateNullFunction() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderTruncate(null));
	}
	
	@Test
	void renderBitwiseAndNullFunction() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderBitwiseAnd(null));
	}
	
	@Test
	void renderBitwiseOrNullFunction() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderBitwiseOr(null));
	}
	
	@Test
	void renderBitwiseXorNullFunction() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderBitwiseXor(null));
	}
	
	@Test
	void renderBitwiseNotNullFunction() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderBitwiseNot(null));
	}
	
	@Test
	void renderRandomProducesRandCall() throws SqlException {
		assertEquals("RAND()", RENDERER.renderRandom(new SqlRandomFunction()).sql());
	}
	
	@Test
	void renderCeilProducesCeilCall() throws SqlException {
		String sql = RENDERER.renderCeil(new SqlCeilFunction<>(new SqlValueExpression<>(5))).sql();
		assertTrue(sql.contains("CEIL("));
		assertTrue(sql.contains("CAST("));
	}
	
	@Test
	void renderFloorProducesFloorCall() throws SqlException {
		String sql = RENDERER.renderFloor(new SqlFloorFunction<>(new SqlValueExpression<>(5))).sql();
		assertTrue(sql.contains("FLOOR("));
		assertTrue(sql.contains("CAST("));
	}
	
	@Test
	void renderModProducesModCall() throws SqlException {
		SqlModFunction<Integer> function = new SqlModFunction<>(new SqlValueExpression<>(7), new SqlValueExpression<>(3));
		String sql = RENDERER.renderMod(function).sql();
		assertTrue(sql.contains("MOD("));
		assertTrue(sql.contains("CAST("));
		assertTrue(sql.contains(","));
	}
	
	@Test
	void renderRoundWithoutPrecision() throws SqlException {
		SqlRoundFunction<Integer> function = new SqlRoundFunction<>(new SqlValueExpression<>(5), null);
		String sql = RENDERER.renderRound(function).sql();
		assertTrue(sql.contains("ROUND("));
		assertTrue(sql.contains("CAST("));
		assertFalse(sql.contains(","));
	}
	
	@Test
	void renderRoundWithPrecision() throws SqlException {
		SqlRoundFunction<Integer> function = new SqlRoundFunction<>(new SqlValueExpression<>(5), new SqlValueExpression<>(2));
		String sql = RENDERER.renderRound(function).sql();
		assertTrue(sql.contains("ROUND("));
		assertTrue(sql.contains("CAST("));
		assertTrue(sql.contains(","));
	}
	
	@Test
	void renderTruncateProducesTruncateCall() throws SqlException {
		String sql = RENDERER.renderTruncate(new SqlNumericTruncateFunction<>(new SqlValueExpression<>(5))).sql();
		assertTrue(sql.contains("TRUNCATE("));
		assertTrue(sql.contains("CAST("));
		assertTrue(sql.contains("0"));
	}
	
	@Test
	void renderBitwiseAndProducesBitand() throws SqlException {
		SqlBitwiseAndFunction<Integer> function = new SqlBitwiseAndFunction<>(new SqlValueExpression<>(5), new SqlValueExpression<>(3));
		String sql = RENDERER.renderBitwiseAnd(function).sql();
		assertTrue(sql.contains("BITAND("));
		assertTrue(sql.contains(","));
	}
	
	@Test
	void renderBitwiseOrProducesBitor() throws SqlException {
		SqlBitwiseOrFunction<Integer> function = new SqlBitwiseOrFunction<>(new SqlValueExpression<>(5), new SqlValueExpression<>(3));
		String sql = RENDERER.renderBitwiseOr(function).sql();
		assertTrue(sql.contains("BITOR("));
		assertTrue(sql.contains(","));
	}
	
	@Test
	void renderBitwiseXorProducesBitxor() throws SqlException {
		SqlBitwiseXorFunction<Integer> function = new SqlBitwiseXorFunction<>(new SqlValueExpression<>(5), new SqlValueExpression<>(3));
		String sql = RENDERER.renderBitwiseXor(function).sql();
		assertTrue(sql.contains("BITXOR("));
		assertTrue(sql.contains(","));
	}
	
	@Test
	void renderBitwiseNotProducesBitnot() throws SqlException {
		String sql = RENDERER.renderBitwiseNot(new SqlBitwiseNotFunction<>(new SqlValueExpression<>(5))).sql();
		assertTrue(sql.contains("BITNOT("));
		assertFalse(sql.contains(","));
	}
}
