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
 *
 * @author Luis-St
 *
 */

public class SqlNumericFunctionRenderer {
	
	protected final SqlDialect dialect;
	
	public SqlNumericFunctionRenderer(@NonNull SqlDialect dialect) {
		this.dialect = Objects.requireNonNull(dialect, "Sql dialect must not be null");
	}
	
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
	
	protected @NonNull SqlRendered renderBitwiseAnd(@NonNull SqlBitwiseAndFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		return SqlRenderingHelper.renderInfix(this.dialect, function.firstOperand(), "&", function.secondOperand());
	}
	
	protected @NonNull SqlRendered renderBitwiseNot(@NonNull SqlBitwiseNotFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		return SqlRenderer.empty().literal("~").openingBracket().rendered(function.expression().toSql(this.dialect)).closingBracket().toSql();
	}
	
	protected @NonNull SqlRendered renderBitwiseOr(@NonNull SqlBitwiseOrFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		return SqlRenderingHelper.renderInfix(this.dialect, function.firstOperand(), "|", function.secondOperand());
	}
	
	protected @NonNull SqlRendered renderBitwiseXor(@NonNull SqlBitwiseXorFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		return SqlRenderingHelper.renderInfix(this.dialect, function.firstOperand(), "^", function.secondOperand());
	}
	
	protected @NonNull SqlRendered renderAcos(@NonNull SqlAcosFunction function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		return SqlRenderingHelper.renderFunctionCall(this.dialect, "ACOS", function.expression());
	}
	
	protected @NonNull SqlRendered renderAsin(@NonNull SqlAsinFunction function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		return SqlRenderingHelper.renderFunctionCall(this.dialect, "ASIN", function.expression());
	}
	
	protected @NonNull SqlRendered renderAtan2(@NonNull SqlAtan2Function function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		return SqlRenderingHelper.renderFunctionCall(this.dialect, "ATAN2", function.y(), function.x());
	}
	
	protected @NonNull SqlRendered renderAtan(@NonNull SqlAtanFunction function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		return SqlRenderingHelper.renderFunctionCall(this.dialect, "ATAN", function.expression());
	}
	
	protected @NonNull SqlRendered renderCos(@NonNull SqlCosFunction function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		return SqlRenderingHelper.renderFunctionCall(this.dialect, "COS", function.expression());
	}
	
	protected @NonNull SqlRendered renderSin(@NonNull SqlSinFunction function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		return SqlRenderingHelper.renderFunctionCall(this.dialect, "SIN", function.expression());
	}
	
	protected @NonNull SqlRendered renderTan(@NonNull SqlTanFunction function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		return SqlRenderingHelper.renderFunctionCall(this.dialect, "TAN", function.expression());
	}
	
	protected @NonNull SqlRendered renderAbs(@NonNull SqlAbsFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		return SqlRenderingHelper.renderFunctionCall(this.dialect, "ABS", function.expression());
	}
	
	protected @NonNull SqlRendered renderCeil(@NonNull SqlCeilFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		return SqlRenderingHelper.renderFunctionCall(this.dialect, "CEIL", function.expression());
	}
	
	protected @NonNull SqlRendered renderDegrees(@NonNull SqlDegreesFunction function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		return SqlRenderingHelper.renderFunctionCall(this.dialect, "DEGREES", function.expression());
	}
	
	protected @NonNull SqlRendered renderExp(@NonNull SqlExpFunction function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		return SqlRenderingHelper.renderFunctionCall(this.dialect, "EXP", function.expression());
	}
	
	protected @NonNull SqlRendered renderFloor(@NonNull SqlFloorFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		return SqlRenderingHelper.renderFunctionCall(this.dialect, "FLOOR", function.expression());
	}
	
	protected @NonNull SqlRendered renderLog(@NonNull SqlLogFunction function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		SqlExpression<? extends Number> base = function.base();
		if (base != null) {
			return SqlRenderingHelper.renderFunctionCall(this.dialect, "LOG", base, function.expression());
		}
		return SqlRenderingHelper.renderFunctionCall(this.dialect, "LN", function.expression());
	}
	
	protected @NonNull SqlRendered renderMod(@NonNull SqlModFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		return SqlRenderingHelper.renderFunctionCall(this.dialect, "MOD", function.expression(), function.divisor());
	}
	
	protected @NonNull SqlRendered renderNegate(@NonNull SqlNegateFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		return SqlRenderer.empty().literal("-").openingBracket().rendered(function.expression().toSql(this.dialect)).closingBracket().toSql();
	}
	
	protected @NonNull SqlRendered renderAdd(@NonNull SqlNumericAddFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		return SqlRenderingHelper.renderInfix(this.dialect, function.expression(), "+", function.addend());
	}
	
	protected @NonNull SqlRendered renderDivide(@NonNull SqlNumericDivideFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		return SqlRenderingHelper.renderInfix(this.dialect, function.expression(), "/", function.divisor());
	}
	
	protected @NonNull SqlRendered renderMultiply(@NonNull SqlNumericMultiplyFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		return SqlRenderingHelper.renderInfix(this.dialect, function.expression(), "*", function.multiplier());
	}
	
	protected @NonNull SqlRendered renderSubtract(@NonNull SqlNumericSubtractFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		return SqlRenderingHelper.renderInfix(this.dialect, function.expression(), "-", function.subtrahend());
	}
	
	protected @NonNull SqlRendered renderPi(@NonNull SqlPiFunction function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		return SqlRendered.of("PI()");
	}
	
	protected @NonNull SqlRendered renderPow(@NonNull SqlPowFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		return SqlRenderingHelper.renderFunctionCall(this.dialect, "POWER", function.expression(), function.exponent());
	}
	
	protected @NonNull SqlRendered renderRadians(@NonNull SqlRadiansFunction function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		return SqlRenderingHelper.renderFunctionCall(this.dialect, "RADIANS", function.expression());
	}
	
	protected @NonNull SqlRendered renderRandom(@NonNull SqlRandomFunction function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		return SqlRendered.of("RANDOM()");
	}
	
	protected @NonNull SqlRendered renderRound(@NonNull SqlRoundFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		SqlExpression<? extends Number> precision = function.precision();
		if (precision != null) {
			return SqlRenderingHelper.renderFunctionCall(this.dialect, "ROUND", function.expression(), precision);
		}
		return SqlRenderingHelper.renderFunctionCall(this.dialect, "ROUND", function.expression());
	}
	
	protected @NonNull SqlRendered renderSign(@NonNull SqlSignFunction function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		return SqlRenderingHelper.renderFunctionCall(this.dialect, "SIGN", function.expression());
	}
	
	protected @NonNull SqlRendered renderSqrt(@NonNull SqlSqrtFunction function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		return SqlRenderingHelper.renderFunctionCall(this.dialect, "SQRT", function.expression());
	}
	
	protected @NonNull SqlRendered renderTruncate(@NonNull SqlNumericTruncateFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		SqlExpression<Integer> zero = new SqlValueExpression<>(0, SqlTypes.INTEGER);
		return SqlRenderingHelper.renderFunctionCall(this.dialect, "TRUNCATE", function.expression(), zero);
	}
}
