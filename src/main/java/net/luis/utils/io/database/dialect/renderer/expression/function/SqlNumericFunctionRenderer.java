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

import net.luis.utils.io.database.dialect.SqlDialect;
import net.luis.utils.io.database.dialect.renderer.SqlRenderingHelper;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.exception.client.dialect.SqlDialectUnknownConstructException;
import net.luis.utils.io.database.expression.SqlExpression;
import net.luis.utils.io.database.expression.SqlValueExpression;
import net.luis.utils.io.database.function.functions.SqlNumericFunction;
import net.luis.utils.io.database.function.functions.numeric.*;
import net.luis.utils.io.database.function.functions.numeric.bitwise.*;
import net.luis.utils.io.database.function.functions.numeric.trigonometric.*;
import net.luis.utils.io.database.rendering.SqlRendered;
import net.luis.utils.io.database.rendering.SqlRenderer;
import net.luis.utils.io.database.type.SqlTypes;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

/**
 * Renderer for numeric sql functions into dialect-specific sql.<br>
 * Dispatches the given {@link SqlNumericFunction} to the matching render method based on its concrete type<br>
 * and produces the resulting {@link SqlRendered} using the configured {@link SqlDialect}.<br>
 *
 * @author Luis-St
 */

public class SqlNumericFunctionRenderer {
	
	/**
	 * The sql dialect used to render the functions.
	 */
	protected final SqlDialect dialect;
	
	/**
	 * Constructs a new numeric sql function renderer for the given dialect.<br>
	 *
	 * @param dialect The sql dialect used to render the functions
	 * @throws NullPointerException If the dialect is null
	 */
	public SqlNumericFunctionRenderer(@NonNull SqlDialect dialect) {
		this.dialect = Objects.requireNonNull(dialect, "Sql dialect must not be null");
	}
	
	/**
	 * Renders the given numeric sql function into dialect-specific sql.<br>
	 * The function is dispatched to the matching render method based on its concrete type.<br>
	 *
	 * @param function The numeric function to render
	 * @return The rendered sql
	 * @throws NullPointerException If the function is null
	 * @throws SqlException If rendering fails
	 */
	public @NonNull SqlRendered render(@NonNull SqlNumericFunction<?> function) throws SqlException {
		return switch (function) {
			case SqlBitwiseAndFunction<?> func -> this.renderBitwiseAnd(func);
			case SqlBitwiseNotFunction<?> func -> this.renderBitwiseNot(func);
			case SqlBitwiseOrFunction<?> func -> this.renderBitwiseOr(func);
			case SqlBitwiseXorFunction<?> func -> this.renderBitwiseXor(func);
			
			case SqlAcosFunction func -> this.renderAcos(func);
			case SqlAsinFunction func -> this.renderAsin(func);
			case SqlAtan2Function func -> this.renderAtan2(func);
			case SqlAtanFunction func -> this.renderAtan(func);
			case SqlCosFunction func -> this.renderCos(func);
			case SqlSinFunction func -> this.renderSin(func);
			case SqlTanFunction func -> this.renderTan(func);
			
			case SqlAbsFunction<?> func -> this.renderAbs(func);
			case SqlCeilFunction<?> func -> this.renderCeil(func);
			case SqlDegreesFunction func -> this.renderDegrees(func);
			case SqlExpFunction func -> this.renderExp(func);
			case SqlFloorFunction<?> func -> this.renderFloor(func);
			case SqlLogFunction func -> this.renderLog(func);
			case SqlModFunction<?> func -> this.renderMod(func);
			case SqlNegateFunction<?> func -> this.renderNegate(func);
			case SqlNumericAddFunction<?> func -> this.renderAdd(func);
			case SqlNumericDivideFunction<?> func -> this.renderDivide(func);
			case SqlNumericMultiplyFunction<?> func -> this.renderMultiply(func);
			case SqlNumericSubtractFunction<?> func -> this.renderSubtract(func);
			case SqlPiFunction func -> this.renderPi(func);
			case SqlPowFunction<?> func -> this.renderPow(func);
			case SqlRadiansFunction func -> this.renderRadians(func);
			case SqlRandomFunction func -> this.renderRandom(func);
			case SqlRoundFunction<?> func -> this.renderRound(func);
			case SqlSignFunction func -> this.renderSign(func);
			case SqlSqrtFunction func -> this.renderSqrt(func);
			case SqlNumericTruncateFunction<?> func -> this.renderTruncate(func);
			
			case null -> throw new NullPointerException("Sql numeric function must not be null");
			default -> throw new SqlDialectUnknownConstructException("Unknown sql numeric function type: " + function.getClass().getName() + " in dialect " + this.dialect.name());
		};
	}
	
	/**
	 * Renders the given bitwise-and function into dialect-specific sql.<br>
	 * The function is rendered as an infix {@code &} operation between both operands.<br>
	 *
	 * @param function The bitwise-and function to render
	 * @return The rendered sql
	 * @throws NullPointerException If the function is null
	 * @throws SqlException If rendering fails
	 */
	protected @NonNull SqlRendered renderBitwiseAnd(@NonNull SqlBitwiseAndFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		return SqlRenderingHelper.renderInfix(this.dialect, function.firstOperand(), "&", function.secondOperand());
	}
	
	/**
	 * Renders the given bitwise-not function into dialect-specific sql.<br>
	 * The function is rendered as a prefix {@code ~} operation applied to the bracketed expression.<br>
	 *
	 * @param function The bitwise-not function to render
	 * @return The rendered sql
	 * @throws NullPointerException If the function is null
	 * @throws SqlException If rendering fails
	 */
	protected @NonNull SqlRendered renderBitwiseNot(@NonNull SqlBitwiseNotFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		return SqlRenderer.empty().literal("~").openingBracket().rendered(function.expression().toSql(this.dialect)).closingBracket().toSql();
	}
	
	/**
	 * Renders the given bitwise-or function into dialect-specific sql.<br>
	 * The function is rendered as an infix {@code |} operation between both operands.<br>
	 *
	 * @param function The bitwise-or function to render
	 * @return The rendered sql
	 * @throws NullPointerException If the function is null
	 * @throws SqlException If rendering fails
	 */
	protected @NonNull SqlRendered renderBitwiseOr(@NonNull SqlBitwiseOrFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		return SqlRenderingHelper.renderInfix(this.dialect, function.firstOperand(), "|", function.secondOperand());
	}
	
	/**
	 * Renders the given bitwise-xor function into dialect-specific sql.<br>
	 * The function is rendered as an infix {@code ^} operation between both operands.<br>
	 *
	 * @param function The bitwise-xor function to render
	 * @return The rendered sql
	 * @throws NullPointerException If the function is null
	 * @throws SqlException If rendering fails
	 */
	protected @NonNull SqlRendered renderBitwiseXor(@NonNull SqlBitwiseXorFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		return SqlRenderingHelper.renderInfix(this.dialect, function.firstOperand(), "^", function.secondOperand());
	}
	
	/**
	 * Renders the given arc-cosine function into dialect-specific sql.<br>
	 * The function is rendered as an {@code ACOS} call over the expression.<br>
	 *
	 * @param function The arc-cosine function to render
	 * @return The rendered sql
	 * @throws NullPointerException If the function is null
	 * @throws SqlException If rendering fails
	 */
	protected @NonNull SqlRendered renderAcos(@NonNull SqlAcosFunction function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		return SqlRenderingHelper.renderFunctionCall(this.dialect, "ACOS", function.expression());
	}
	
	/**
	 * Renders the given arc-sine function into dialect-specific sql.<br>
	 * The function is rendered as an {@code ASIN} call over the expression.<br>
	 *
	 * @param function The arc-sine function to render
	 * @return The rendered sql
	 * @throws NullPointerException If the function is null
	 * @throws SqlException If rendering fails
	 */
	protected @NonNull SqlRendered renderAsin(@NonNull SqlAsinFunction function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		return SqlRenderingHelper.renderFunctionCall(this.dialect, "ASIN", function.expression());
	}
	
	/**
	 * Renders the given two-argument arc-tangent function into dialect-specific sql.<br>
	 * The function is rendered as an {@code ATAN2} call with the {@code y} and {@code x} operands.<br>
	 *
	 * @param function The two-argument arc-tangent function to render
	 * @return The rendered sql
	 * @throws NullPointerException If the function is null
	 * @throws SqlException If rendering fails
	 */
	protected @NonNull SqlRendered renderAtan2(@NonNull SqlAtan2Function function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		return SqlRenderingHelper.renderFunctionCall(this.dialect, "ATAN2", function.y(), function.x());
	}
	
	/**
	 * Renders the given arc-tangent function into dialect-specific sql.<br>
	 * The function is rendered as an {@code ATAN} call over the expression.<br>
	 *
	 * @param function The arc-tangent function to render
	 * @return The rendered sql
	 * @throws NullPointerException If the function is null
	 * @throws SqlException If rendering fails
	 */
	protected @NonNull SqlRendered renderAtan(@NonNull SqlAtanFunction function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		return SqlRenderingHelper.renderFunctionCall(this.dialect, "ATAN", function.expression());
	}
	
	/**
	 * Renders the given cosine function into dialect-specific sql.<br>
	 * The function is rendered as a {@code COS} call over the expression.<br>
	 *
	 * @param function The cosine function to render
	 * @return The rendered sql
	 * @throws NullPointerException If the function is null
	 * @throws SqlException If rendering fails
	 */
	protected @NonNull SqlRendered renderCos(@NonNull SqlCosFunction function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		return SqlRenderingHelper.renderFunctionCall(this.dialect, "COS", function.expression());
	}
	
	/**
	 * Renders the given sine function into dialect-specific sql.<br>
	 * The function is rendered as a {@code SIN} call over the expression.<br>
	 *
	 * @param function The sine function to render
	 * @return The rendered sql
	 * @throws NullPointerException If the function is null
	 * @throws SqlException If rendering fails
	 */
	protected @NonNull SqlRendered renderSin(@NonNull SqlSinFunction function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		return SqlRenderingHelper.renderFunctionCall(this.dialect, "SIN", function.expression());
	}
	
	/**
	 * Renders the given tangent function into dialect-specific sql.<br>
	 * The function is rendered as a {@code TAN} call over the expression.<br>
	 *
	 * @param function The tangent function to render
	 * @return The rendered sql
	 * @throws NullPointerException If the function is null
	 * @throws SqlException If rendering fails
	 */
	protected @NonNull SqlRendered renderTan(@NonNull SqlTanFunction function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		return SqlRenderingHelper.renderFunctionCall(this.dialect, "TAN", function.expression());
	}
	
	/**
	 * Renders the given absolute-value function into dialect-specific sql.<br>
	 * The function is rendered as an {@code ABS} call over the expression.<br>
	 *
	 * @param function The absolute-value function to render
	 * @return The rendered sql
	 * @throws NullPointerException If the function is null
	 * @throws SqlException If rendering fails
	 */
	protected @NonNull SqlRendered renderAbs(@NonNull SqlAbsFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		return SqlRenderingHelper.renderFunctionCall(this.dialect, "ABS", function.expression());
	}
	
	/**
	 * Renders the given ceiling function into dialect-specific sql.<br>
	 * The function is rendered as a {@code CEIL} call over the expression.<br>
	 *
	 * @param function The ceiling function to render
	 * @return The rendered sql
	 * @throws NullPointerException If the function is null
	 * @throws SqlException If rendering fails
	 */
	protected @NonNull SqlRendered renderCeil(@NonNull SqlCeilFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		return SqlRenderingHelper.renderFunctionCall(this.dialect, "CEIL", function.expression());
	}
	
	/**
	 * Renders the given degrees function into dialect-specific sql.<br>
	 * The function is rendered as a {@code DEGREES} call over the expression.<br>
	 *
	 * @param function The degrees function to render
	 * @return The rendered sql
	 * @throws NullPointerException If the function is null
	 * @throws SqlException If rendering fails
	 */
	protected @NonNull SqlRendered renderDegrees(@NonNull SqlDegreesFunction function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		return SqlRenderingHelper.renderFunctionCall(this.dialect, "DEGREES", function.expression());
	}
	
	/**
	 * Renders the given exponential function into dialect-specific sql.<br>
	 * The function is rendered as an {@code EXP} call over the expression.<br>
	 *
	 * @param function The exponential function to render
	 * @return The rendered sql
	 * @throws NullPointerException If the function is null
	 * @throws SqlException If rendering fails
	 */
	protected @NonNull SqlRendered renderExp(@NonNull SqlExpFunction function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		return SqlRenderingHelper.renderFunctionCall(this.dialect, "EXP", function.expression());
	}
	
	/**
	 * Renders the given floor function into dialect-specific sql.<br>
	 * The function is rendered as a {@code FLOOR} call over the expression.<br>
	 *
	 * @param function The floor function to render
	 * @return The rendered sql
	 * @throws NullPointerException If the function is null
	 * @throws SqlException If rendering fails
	 */
	protected @NonNull SqlRendered renderFloor(@NonNull SqlFloorFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		return SqlRenderingHelper.renderFunctionCall(this.dialect, "FLOOR", function.expression());
	}
	
	/**
	 * Renders the given logarithm function into dialect-specific sql.<br>
	 * The function is rendered as a {@code LOG} call with the base and expression if a base is set,<br>
	 * otherwise as a natural logarithm {@code LN} call over the expression.<br>
	 *
	 * @param function The logarithm function to render
	 * @return The rendered sql
	 * @throws NullPointerException If the function is null
	 * @throws SqlException If rendering fails
	 */
	protected @NonNull SqlRendered renderLog(@NonNull SqlLogFunction function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		SqlExpression<? extends Number> base = function.base();
		if (base != null) {
			return SqlRenderingHelper.renderFunctionCall(this.dialect, "LOG", base, function.expression());
		}
		return SqlRenderingHelper.renderFunctionCall(this.dialect, "LN", function.expression());
	}
	
	/**
	 * Renders the given modulo function into dialect-specific sql.<br>
	 * The function is rendered as a {@code MOD} call with the expression and the divisor.<br>
	 *
	 * @param function The modulo function to render
	 * @return The rendered sql
	 * @throws NullPointerException If the function is null
	 * @throws SqlException If rendering fails
	 */
	protected @NonNull SqlRendered renderMod(@NonNull SqlModFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		return SqlRenderingHelper.renderFunctionCall(this.dialect, "MOD", function.expression(), function.divisor());
	}
	
	/**
	 * Renders the given negate function into dialect-specific sql.<br>
	 * The function is rendered as a prefix {@code -} operation applied to the bracketed expression.<br>
	 *
	 * @param function The negate function to render
	 * @return The rendered sql
	 * @throws NullPointerException If the function is null
	 * @throws SqlException If rendering fails
	 */
	protected @NonNull SqlRendered renderNegate(@NonNull SqlNegateFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		return SqlRenderer.empty().literal("-").openingBracket().rendered(function.expression().toSql(this.dialect)).closingBracket().toSql();
	}
	
	/**
	 * Renders the given addition function into dialect-specific sql.<br>
	 * The function is rendered as an infix {@code +} operation between the expression and the addend.<br>
	 *
	 * @param function The addition function to render
	 * @return The rendered sql
	 * @throws NullPointerException If the function is null
	 * @throws SqlException If rendering fails
	 */
	protected @NonNull SqlRendered renderAdd(@NonNull SqlNumericAddFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		return SqlRenderingHelper.renderInfix(this.dialect, function.expression(), "+", function.addend());
	}
	
	/**
	 * Renders the given division function into dialect-specific sql.<br>
	 * The function is rendered as an infix {@code /} operation between the expression and the divisor.<br>
	 *
	 * @param function The division function to render
	 * @return The rendered sql
	 * @throws NullPointerException If the function is null
	 * @throws SqlException If rendering fails
	 */
	protected @NonNull SqlRendered renderDivide(@NonNull SqlNumericDivideFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		return SqlRenderingHelper.renderInfix(this.dialect, function.expression(), "/", function.divisor());
	}
	
	/**
	 * Renders the given multiplication function into dialect-specific sql.<br>
	 * The function is rendered as an infix {@code *} operation between the expression and the multiplier.<br>
	 *
	 * @param function The multiplication function to render
	 * @return The rendered sql
	 * @throws NullPointerException If the function is null
	 * @throws SqlException If rendering fails
	 */
	protected @NonNull SqlRendered renderMultiply(@NonNull SqlNumericMultiplyFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		return SqlRenderingHelper.renderInfix(this.dialect, function.expression(), "*", function.multiplier());
	}
	
	/**
	 * Renders the given subtraction function into dialect-specific sql.<br>
	 * The function is rendered as an infix {@code -} operation between the expression and the subtrahend.<br>
	 *
	 * @param function The subtraction function to render
	 * @return The rendered sql
	 * @throws NullPointerException If the function is null
	 * @throws SqlException If rendering fails
	 */
	protected @NonNull SqlRendered renderSubtract(@NonNull SqlNumericSubtractFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		return SqlRenderingHelper.renderInfix(this.dialect, function.expression(), "-", function.subtrahend());
	}
	
	/**
	 * Renders the given pi function into dialect-specific sql.<br>
	 * The function is rendered as a constant {@code PI()} call.<br>
	 *
	 * @param function The pi function to render
	 * @return The rendered sql
	 * @throws NullPointerException If the function is null
	 * @throws SqlException If rendering fails
	 */
	protected @NonNull SqlRendered renderPi(@NonNull SqlPiFunction function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		return SqlRendered.of("PI()");
	}
	
	/**
	 * Renders the given power function into dialect-specific sql.<br>
	 * The function is rendered as a {@code POWER} call with the expression and the exponent.<br>
	 *
	 * @param function The power function to render
	 * @return The rendered sql
	 * @throws NullPointerException If the function is null
	 * @throws SqlException If rendering fails
	 */
	protected @NonNull SqlRendered renderPow(@NonNull SqlPowFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		return SqlRenderingHelper.renderFunctionCall(this.dialect, "POWER", function.expression(), function.exponent());
	}
	
	/**
	 * Renders the given radians function into dialect-specific sql.<br>
	 * The function is rendered as a {@code RADIANS} call over the expression.<br>
	 *
	 * @param function The radians function to render
	 * @return The rendered sql
	 * @throws NullPointerException If the function is null
	 * @throws SqlException If rendering fails
	 */
	protected @NonNull SqlRendered renderRadians(@NonNull SqlRadiansFunction function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		return SqlRenderingHelper.renderFunctionCall(this.dialect, "RADIANS", function.expression());
	}
	
	/**
	 * Renders the given random function into dialect-specific sql.<br>
	 * The function is rendered as a {@code RANDOM()} call.<br>
	 *
	 * @param function The random function to render
	 * @return The rendered sql
	 * @throws NullPointerException If the function is null
	 * @throws SqlException If rendering fails
	 */
	protected @NonNull SqlRendered renderRandom(@NonNull SqlRandomFunction function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		return SqlRendered.of("RANDOM()");
	}
	
	/**
	 * Renders the given round function into dialect-specific sql.<br>
	 * The function is rendered as a {@code ROUND} call with the expression and the precision if a precision is set,<br>
	 * otherwise as a {@code ROUND} call over the expression only.<br>
	 *
	 * @param function The round function to render
	 * @return The rendered sql
	 * @throws NullPointerException If the function is null
	 * @throws SqlException If rendering fails
	 */
	protected @NonNull SqlRendered renderRound(@NonNull SqlRoundFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		SqlExpression<? extends Number> precision = function.precision();
		if (precision != null) {
			return SqlRenderingHelper.renderFunctionCall(this.dialect, "ROUND", function.expression(), precision);
		}
		return SqlRenderingHelper.renderFunctionCall(this.dialect, "ROUND", function.expression());
	}
	
	/**
	 * Renders the given sign function into dialect-specific sql.<br>
	 * The function is rendered as a {@code SIGN} call over the expression.<br>
	 *
	 * @param function The sign function to render
	 * @return The rendered sql
	 * @throws NullPointerException If the function is null
	 * @throws SqlException If rendering fails
	 */
	protected @NonNull SqlRendered renderSign(@NonNull SqlSignFunction function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		return SqlRenderingHelper.renderFunctionCall(this.dialect, "SIGN", function.expression());
	}
	
	/**
	 * Renders the given square-root function into dialect-specific sql.<br>
	 * The function is rendered as a {@code SQRT} call over the expression.<br>
	 *
	 * @param function The square-root function to render
	 * @return The rendered sql
	 * @throws NullPointerException If the function is null
	 * @throws SqlException If rendering fails
	 */
	protected @NonNull SqlRendered renderSqrt(@NonNull SqlSqrtFunction function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		return SqlRenderingHelper.renderFunctionCall(this.dialect, "SQRT", function.expression());
	}
	
	/**
	 * Renders the given truncate function into dialect-specific sql.<br>
	 * The function is rendered as a {@code TRUNCATE} call with the expression and a fixed precision of zero.<br>
	 *
	 * @param function The truncate function to render
	 * @return The rendered sql
	 * @throws NullPointerException If the function is null
	 * @throws SqlException If rendering fails
	 */
	protected @NonNull SqlRendered renderTruncate(@NonNull SqlNumericTruncateFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		SqlExpression<Integer> zero = new SqlValueExpression<>(0, SqlTypes.INTEGER);
		return SqlRenderingHelper.renderFunctionCall(this.dialect, "TRUNCATE", function.expression(), zero);
	}
}
