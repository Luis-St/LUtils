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
import net.luis.utils.io.database.expression.SqlExpression;
import net.luis.utils.io.database.expression.SqlValueExpression;
import net.luis.utils.io.database.function.functions.SqlNumericFunction;
import net.luis.utils.io.database.function.functions.numeric.*;
import net.luis.utils.io.database.function.functions.numeric.bitwise.*;
import net.luis.utils.io.database.function.functions.numeric.trigonometric.*;
import net.luis.utils.io.database.rendering.SqlRendered;
import net.luis.utils.io.database.table.SqlColumn;
import net.luis.utils.io.database.table.SqlTable;
import net.luis.utils.io.database.type.SqlType;
import net.luis.utils.io.database.type.SqlTypes;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlNumericFunctionRenderer}.<br>
 *
 * @author Luis-St
 */
class SqlNumericFunctionRendererTest {
	
	private static final SqlNumericFunctionRenderer RENDERER = new SqlNumericFunctionRenderer(SqlDialects.DEFAULT);
	
	private static SqlValueExpression<Integer> value() throws SqlException {
		return new SqlValueExpression<>(5);
	}
	
	@Test
	void constructWithDialect() {
		assertNotNull(new SqlNumericFunctionRenderer(SqlDialects.DEFAULT));
	}
	
	@Test
	void constructWithNullDialect() {
		assertThrows(NullPointerException.class, () -> new SqlNumericFunctionRenderer(null));
	}
	
	@Test
	void renderNullFunction() {
		assertThrows(NullPointerException.class, () -> RENDERER.render(null));
	}
	
	@Test
	void renderUnknownNumericType() {
		SqlNumericFunction<Object> unknown = new UnknownNumericFunction();
		assertThrows(SqlDialectUnknownConstructException.class, () -> RENDERER.render(unknown));
	}
	
	@Test
	void renderBitwiseAndNullDirect() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderBitwiseAnd(null));
	}
	
	@Test
	void renderBitwiseNotNullDirect() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderBitwiseNot(null));
	}
	
	@Test
	void renderBitwiseOrNullDirect() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderBitwiseOr(null));
	}
	
	@Test
	void renderBitwiseXorNullDirect() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderBitwiseXor(null));
	}
	
	@Test
	void renderAcosNullDirect() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderAcos(null));
	}
	
	@Test
	void renderAsinNullDirect() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderAsin(null));
	}
	
	@Test
	void renderAtan2NullDirect() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderAtan2(null));
	}
	
	@Test
	void renderAtanNullDirect() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderAtan(null));
	}
	
	@Test
	void renderCosNullDirect() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderCos(null));
	}
	
	@Test
	void renderSinNullDirect() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderSin(null));
	}
	
	@Test
	void renderTanNullDirect() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderTan(null));
	}
	
	@Test
	void renderAbsNullDirect() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderAbs(null));
	}
	
	@Test
	void renderCeilNullDirect() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderCeil(null));
	}
	
	@Test
	void renderDegreesNullDirect() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderDegrees(null));
	}
	
	@Test
	void renderExpNullDirect() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderExp(null));
	}
	
	@Test
	void renderFloorNullDirect() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderFloor(null));
	}
	
	@Test
	void renderLogNullDirect() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderLog(null));
	}
	
	@Test
	void renderModNullDirect() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderMod(null));
	}
	
	@Test
	void renderNegateNullDirect() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderNegate(null));
	}
	
	@Test
	void renderAddNullDirect() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderAdd(null));
	}
	
	@Test
	void renderDivideNullDirect() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderDivide(null));
	}
	
	@Test
	void renderMultiplyNullDirect() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderMultiply(null));
	}
	
	@Test
	void renderSubtractNullDirect() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderSubtract(null));
	}
	
	@Test
	void renderPiNullDirect() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderPi(null));
	}
	
	@Test
	void renderPowNullDirect() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderPow(null));
	}
	
	@Test
	void renderRadiansNullDirect() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderRadians(null));
	}
	
	@Test
	void renderRandomNullDirect() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderRandom(null));
	}
	
	@Test
	void renderRoundNullDirect() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderRound(null));
	}
	
	@Test
	void renderSignNullDirect() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderSign(null));
	}
	
	@Test
	void renderSqrtNullDirect() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderSqrt(null));
	}
	
	@Test
	void renderTruncateNullDirect() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderTruncate(null));
	}
	
	@Test
	void renderBitwiseAndFunction() throws SqlException {
		SqlRendered rendered = RENDERER.render(new SqlBitwiseAndFunction<>(value(), value(), SqlTypes.INTEGER));
		assertTrue(rendered.sql().contains("&"));
	}
	
	@Test
	void renderBitwiseNotFunction() throws SqlException {
		SqlRendered rendered = RENDERER.render(new SqlBitwiseNotFunction<>(value(), SqlTypes.INTEGER));
		assertTrue(rendered.sql().contains("~("));
	}
	
	@Test
	void renderBitwiseOrFunction() throws SqlException {
		SqlRendered rendered = RENDERER.render(new SqlBitwiseOrFunction<>(value(), value(), SqlTypes.INTEGER));
		assertTrue(rendered.sql().contains("|"));
	}
	
	@Test
	void renderBitwiseXorFunction() throws SqlException {
		SqlRendered rendered = RENDERER.render(new SqlBitwiseXorFunction<>(value(), value(), SqlTypes.INTEGER));
		assertTrue(rendered.sql().contains("^"));
	}
	
	@Test
	void renderAcosFunction() throws SqlException {
		assertTrue(RENDERER.render(new SqlAcosFunction(value())).sql().contains("ACOS("));
	}
	
	@Test
	void renderAsinFunction() throws SqlException {
		assertTrue(RENDERER.render(new SqlAsinFunction(value())).sql().contains("ASIN("));
	}
	
	@Test
	void renderAtan2Function() throws SqlException {
		assertTrue(RENDERER.render(new SqlAtan2Function(value(), value())).sql().contains("ATAN2("));
	}
	
	@Test
	void renderAtanFunction() throws SqlException {
		assertTrue(RENDERER.render(new SqlAtanFunction(value())).sql().contains("ATAN("));
	}
	
	@Test
	void renderCosFunction() throws SqlException {
		assertTrue(RENDERER.render(new SqlCosFunction(value())).sql().contains("COS("));
	}
	
	@Test
	void renderSinFunction() throws SqlException {
		assertTrue(RENDERER.render(new SqlSinFunction(value())).sql().contains("SIN("));
	}
	
	@Test
	void renderTanFunction() throws SqlException {
		assertTrue(RENDERER.render(new SqlTanFunction(value())).sql().contains("TAN("));
	}
	
	@Test
	void renderAbsFunction() throws SqlException {
		assertTrue(RENDERER.render(new SqlAbsFunction<>(value())).sql().contains("ABS("));
	}
	
	@Test
	void renderCeilFunction() throws SqlException {
		assertTrue(RENDERER.render(new SqlCeilFunction<>(value())).sql().contains("CEIL("));
	}
	
	@Test
	void renderDegreesFunction() throws SqlException {
		assertTrue(RENDERER.render(new SqlDegreesFunction(value())).sql().contains("DEGREES("));
	}
	
	@Test
	void renderExpFunction() throws SqlException {
		assertTrue(RENDERER.render(new SqlExpFunction(value())).sql().contains("EXP("));
	}
	
	@Test
	void renderFloorFunction() throws SqlException {
		assertTrue(RENDERER.render(new SqlFloorFunction<>(value())).sql().contains("FLOOR("));
	}
	
	@Test
	void renderLogWithBase() throws SqlException {
		SqlRendered rendered = RENDERER.render(new SqlLogFunction(value(), value()));
		assertTrue(rendered.sql().contains("LOG("));
		assertEquals(2, rendered.parameters().size());
	}
	
	@Test
	void renderLogWithoutBase() throws SqlException {
		SqlRendered rendered = RENDERER.render(new SqlLogFunction(value(), null));
		assertTrue(rendered.sql().contains("LN("));
	}
	
	@Test
	void renderModFunction() throws SqlException {
		assertTrue(RENDERER.render(new SqlModFunction<>(value(), value())).sql().contains("MOD("));
	}
	
	@Test
	void renderNegateFunction() throws SqlException {
		assertTrue(RENDERER.render(new SqlNegateFunction<>(value())).sql().contains("-("));
	}
	
	@Test
	void renderAddFunction() throws SqlException {
		assertTrue(RENDERER.render(new SqlNumericAddFunction<>(value(), value())).sql().contains("+"));
	}
	
	@Test
	void renderDivideFunction() throws SqlException {
		assertTrue(RENDERER.render(new SqlNumericDivideFunction<>(value(), value())).sql().contains("/"));
	}
	
	@Test
	void renderMultiplyFunction() throws SqlException {
		assertTrue(RENDERER.render(new SqlNumericMultiplyFunction<>(value(), value())).sql().contains("*"));
	}
	
	@Test
	void renderSubtractFunction() throws SqlException {
		assertTrue(RENDERER.render(new SqlNumericSubtractFunction<>(value(), value())).sql().contains("-"));
	}
	
	@Test
	void renderPiFunction() throws SqlException {
		assertEquals("PI()", RENDERER.render(new SqlPiFunction()).sql());
	}
	
	@Test
	void renderPowFunction() throws SqlException {
		assertTrue(RENDERER.render(new SqlPowFunction<>(value(), value())).sql().contains("POWER("));
	}
	
	@Test
	void renderRadiansFunction() throws SqlException {
		assertTrue(RENDERER.render(new SqlRadiansFunction(value())).sql().contains("RADIANS("));
	}
	
	@Test
	void renderRandomFunction() throws SqlException {
		assertEquals("RANDOM()", RENDERER.render(new SqlRandomFunction()).sql());
	}
	
	@Test
	void renderRoundWithPrecision() throws SqlException {
		SqlRendered rendered = RENDERER.render(new SqlRoundFunction<>(value(), value()));
		assertTrue(rendered.sql().contains("ROUND("));
		assertEquals(2, rendered.parameters().size());
	}
	
	@Test
	void renderRoundWithoutPrecision() throws SqlException {
		SqlRendered rendered = RENDERER.render(new SqlRoundFunction<>(value(), null));
		assertTrue(rendered.sql().contains("ROUND("));
		assertEquals(1, rendered.parameters().size());
	}
	
	@Test
	void renderSignFunction() throws SqlException {
		assertTrue(RENDERER.render(new SqlSignFunction(value())).sql().contains("SIGN("));
	}
	
	@Test
	void renderSqrtFunction() throws SqlException {
		assertTrue(RENDERER.render(new SqlSqrtFunction(value())).sql().contains("SQRT("));
	}
	
	@Test
	void renderTruncateFunction() throws SqlException {
		SqlRendered rendered = RENDERER.render(new SqlNumericTruncateFunction<>(value()));
		assertTrue(rendered.sql().contains("TRUNCATE("));
		assertEquals(2, rendered.parameters().size());
	}
	
	@Test
	void renderAddWithColumnOperands() throws SqlException {
		SqlTable<Object> table = SqlTable.create(Object.class, "stats");
		SqlColumn<Object, Integer> left = table.column("a", SqlTypes.INTEGER, object -> 0);
		SqlColumn<Object, Integer> right = table.column("b", SqlTypes.INTEGER, object -> 0);
		
		SqlRendered rendered = RENDERER.render(new SqlNumericAddFunction<>(left, right));
		assertTrue(rendered.sql().contains("+"));
		assertTrue(rendered.sql().contains("\"a\""));
		assertTrue(rendered.sql().contains("\"b\""));
	}
	
	@Test
	void renderModWithLiteralDivisor() throws SqlException {
		SqlTable<Object> table = SqlTable.create(Object.class, "stats");
		SqlColumn<Object, Integer> column = table.column("amount", SqlTypes.INTEGER, object -> 0);
		
		SqlRendered rendered = RENDERER.render(new SqlModFunction<>(column, new SqlValueExpression<>(3)));
		assertTrue(rendered.sql().contains("MOD("));
		assertTrue(rendered.sql().contains("\"amount\""));
	}
	
	@Test
	void renderNestedArithmetic() throws SqlException {
		SqlExpression<Integer> multiply = new SqlNumericMultiplyFunction<>(value(), value());
		SqlRendered rendered = RENDERER.render(new SqlNumericAddFunction<>(multiply, value()));
		assertTrue(rendered.sql().contains("+"));
		assertTrue(rendered.sql().contains("*"));
	}
	
	private static final class UnknownNumericFunction implements SqlNumericFunction<Object> {
		
		@Override
		public @NonNull SqlType<Object> type() {
			throw new UnsupportedOperationException();
		}
	}
}
