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

package net.luis.utils.io.database.dialect.sqlserver;

import net.luis.utils.io.database.dialect.SqlDialect;
import net.luis.utils.io.database.dialect.base.SqlRenderingHelper;
import net.luis.utils.io.database.dialect.base.function.SqlStringFunctionRenderer;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.expression.SqlExpression;
import net.luis.utils.io.database.function.functions.string.SqlConcatFunction;
import net.luis.utils.io.database.function.functions.string.SqlLengthFunction;
import net.luis.utils.io.database.rendering.SqlRendered;
import net.luis.utils.io.database.rendering.SqlRenderer;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Optional;

/**
 *
 * @author Luis-St
 *
 */

public class SqlServerStringFunctionRenderer extends SqlStringFunctionRenderer {
	
	public SqlServerStringFunctionRenderer(@NonNull SqlDialect dialect) {
		super(dialect);
	}
	
	@Override
	@SuppressWarnings("DuplicatedCode")
	protected @NonNull SqlRendered renderConcat(@NonNull SqlConcatFunction<?> function) throws SqlException {
		List<? extends SqlExpression<? extends CharSequence>> values = function.expressions();
		Optional<String> separator = function.separator();
		boolean distinct = function.distinct();
		boolean ordered = function.ordered();
		
		SqlRenderer renderer = SqlRenderer.empty();
		if (distinct || ordered) {
			renderer.literal("STRING_AGG").openingBracket();
			SqlExpression<? extends CharSequence> first = values.isEmpty() ? null : values.getFirst();
			if (first != null) {
				renderer.rendered(first.toSql(this.dialect));
			}
			renderer.comma().parameter(DEFAULT_STRING_TYPE, separator.orElse(""));
			renderer.closingBracket();
			if (ordered && first != null) {
				renderer.literal("WITHIN GROUP").openingBracket().orderBy().rendered(first.toSql(this.dialect)).closingBracket();
			}
		} else {
			for (int i = 0; i < values.size(); i++) {
				if (i > 0) {
					renderer.literal("+");
					separator.ifPresent(s -> renderer.parameter(DEFAULT_STRING_TYPE, s).literal("+"));
				}
				renderer.rendered(values.get(i).toSql(this.dialect));
			}
		}
		return renderer.toSql();
	}
	
	@Override
	protected @NonNull SqlRendered renderLength(@NonNull SqlLengthFunction<?> function) throws SqlException {
		return SqlRenderingHelper.renderFunctionCall(this.dialect, "LEN", function.expression());
	}
}
