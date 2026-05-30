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
import net.luis.utils.io.database.function.functions.SqlStringFunction;
import net.luis.utils.io.database.function.functions.string.*;
import net.luis.utils.io.database.rendering.SqlRendered;
import net.luis.utils.io.database.rendering.SqlRenderer;
import net.luis.utils.io.database.type.SqlType;
import net.luis.utils.io.database.type.SqlTypes;
import net.luis.utils.io.database.type.parameter.SqlParameter;
import org.jspecify.annotations.NonNull;

import java.util.*;

/**
 *
 * @author Luis-St
 *
 */

public class SqlStringFunctionRenderer {
	
	protected static final SqlType<String> DEFAULT_STRING_TYPE = SqlTypes.STRING.configure(SqlParameter.length(255));
	
	protected final SqlDialect dialect;
	
	public SqlStringFunctionRenderer(@NonNull SqlDialect dialect) {
		this.dialect = Objects.requireNonNull(dialect, "Sql dialect must not be null");
	}
	
	public @NonNull SqlRendered render(@NonNull SqlStringFunction<?> function) throws SqlException {
		return switch (function) {
			case SqlConcatFunction<?> func -> this.renderConcat(func);
			case SqlHexFunction func -> this.renderHex(func);
			case SqlLeftFunction<?> func -> this.renderLeft(func);
			case SqlLeftPadFunction<?> func -> this.renderLeftPad(func);
			case SqlLeftTrimFunction<?> func -> this.renderLeftTrim(func);
			case SqlLengthFunction<?> func -> this.renderLength(func);
			case SqlLowerFunction<?> func -> this.renderLower(func);
			case SqlPositionFunction<?> func -> this.renderPosition(func);
			case SqlReplaceFunction<?> func -> this.renderReplace(func);
			case SqlRightFunction<?> func -> this.renderRight(func);
			case SqlRightPadFunction<?> func -> this.renderRightPad(func);
			case SqlRightTrimFunction<?> func -> this.renderRightTrim(func);
			case SqlSubstringFunction<?> func -> this.renderSubstring(func);
			case SqlTrimCharsFunction<?> func -> this.renderTrimChars(func);
			case SqlTrimFunction<?> func -> this.renderTrim(func);
			case SqlUnhexFunction<?> func -> this.renderUnhex(func);
			case SqlUpperFunction<?> func -> this.renderUpper(func);
			
			case null -> throw new NullPointerException("Sql string function must not be null");
			default -> throw new SqlDialectUnknownConstructException("Unknown sql string function type: " + function.getClass().getName() + " in dialect " + this.dialect.name());
		};
	}
	
	@SuppressWarnings("DuplicatedCode")
	protected @NonNull SqlRendered renderConcat(@NonNull SqlConcatFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		List<? extends SqlExpression<? extends CharSequence>> values = function.expressions();
		Optional<String> separator = function.separator();
		boolean distinct = function.distinct();
		boolean ordered = function.ordered();
		
		SqlRenderer renderer = SqlRenderer.empty();
		if (distinct || ordered) {
			renderer.literal("STRING_AGG").openingBracket();
			if (distinct) {
				renderer.distinct();
			}
			SqlExpression<? extends CharSequence> first = values.isEmpty() ? null : values.getFirst();
			if (first != null) {
				renderer.rendered(first.toSql(this.dialect));
			}
			renderer.comma().parameter(DEFAULT_STRING_TYPE, separator.orElse(""));
			if (ordered && first != null) {
				renderer.orderBy().rendered(first.toSql(this.dialect));
			}
			renderer.closingBracket();
		} else {
			for (int i = 0; i < values.size(); i++) {
				if (i > 0) {
					renderer.literal("||");
					separator.ifPresent(s -> renderer.parameter(DEFAULT_STRING_TYPE, s).literal("||"));
				}
				renderer.rendered(values.get(i).toSql(this.dialect));
			}
		}
		return renderer.toSql();
	}
	
	protected @NonNull SqlRendered renderHex(@NonNull SqlHexFunction function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		return SqlRenderingHelper.renderFunctionCall(this.dialect, "HEX", function.expression());
	}
	
	protected @NonNull SqlRendered renderLeft(@NonNull SqlLeftFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		return SqlRenderingHelper.renderFunctionCall(this.dialect, "LEFT", function.expression(), function.length());
	}
	
	protected @NonNull SqlRendered renderLeftPad(@NonNull SqlLeftPadFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		return SqlRenderingHelper.renderFunctionCall(this.dialect, "LPAD", function.expression(), function.length(), function.fill());
	}
	
	protected @NonNull SqlRendered renderLeftTrim(@NonNull SqlLeftTrimFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		return SqlRenderingHelper.renderFunctionCall(this.dialect, "LTRIM", function.expression());
	}
	
	protected @NonNull SqlRendered renderLength(@NonNull SqlLengthFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		return SqlRenderingHelper.renderFunctionCall(this.dialect, "LENGTH", function.expression());
	}
	
	protected @NonNull SqlRendered renderLower(@NonNull SqlLowerFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		return SqlRenderingHelper.renderFunctionCall(this.dialect, "LOWER", function.expression());
	}
	
	protected @NonNull SqlRendered renderPosition(@NonNull SqlPositionFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.literal("POSITION").openingBracket();
		renderer.rendered(function.substring().toSql(this.dialect)).in().rendered(function.expression().toSql(this.dialect));
		renderer.closingBracket();
		return renderer.toSql();
	}
	
	protected @NonNull SqlRendered renderReplace(@NonNull SqlReplaceFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		return SqlRenderingHelper.renderFunctionCall(this.dialect, "REPLACE", function.expression(), function.search(), function.replacement());
	}
	
	protected @NonNull SqlRendered renderRight(@NonNull SqlRightFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		return SqlRenderingHelper.renderFunctionCall(this.dialect, "RIGHT", function.expression(), function.length());
	}
	
	protected @NonNull SqlRendered renderRightPad(@NonNull SqlRightPadFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		return SqlRenderingHelper.renderFunctionCall(this.dialect, "RPAD", function.expression(), function.length(), function.fill());
	}
	
	protected @NonNull SqlRendered renderRightTrim(@NonNull SqlRightTrimFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		return SqlRenderingHelper.renderFunctionCall(this.dialect, "RTRIM", function.expression());
	}
	
	protected @NonNull SqlRendered renderSubstring(@NonNull SqlSubstringFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.literal("SUBSTRING").openingBracket().rendered(function.expression().toSql(this.dialect)).from().rendered(function.start().toSql(this.dialect));
		SqlExpression<? extends Number> length = function.length();
		if (length != null) {
			renderer.for_().rendered(length.toSql(this.dialect));
		}
		renderer.closingBracket();
		return renderer.toSql();
	}
	
	protected @NonNull SqlRendered renderTrimChars(@NonNull SqlTrimCharsFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.literal("TRIM").openingBracket().rendered(function.characters().toSql(this.dialect)).from().rendered(function.expression().toSql(this.dialect)).closingBracket();
		return renderer.toSql();
	}
	
	protected @NonNull SqlRendered renderTrim(@NonNull SqlTrimFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		return SqlRenderingHelper.renderFunctionCall(this.dialect, "TRIM", function.expression());
	}
	
	protected @NonNull SqlRendered renderUnhex(@NonNull SqlUnhexFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		return SqlRenderingHelper.renderFunctionCall(this.dialect, "UNHEX", function.expression());
	}
	
	protected @NonNull SqlRendered renderUpper(@NonNull SqlUpperFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		return SqlRenderingHelper.renderFunctionCall(this.dialect, "UPPER", function.expression());
	}
}
