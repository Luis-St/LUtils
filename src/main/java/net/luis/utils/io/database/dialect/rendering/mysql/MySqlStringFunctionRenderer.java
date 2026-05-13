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

package net.luis.utils.io.database.dialect.rendering.mysql;

import net.luis.utils.io.database.dialect.SqlDialect;
import net.luis.utils.io.database.dialect.rendering.base.function.SqlStringFunctionRenderer;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.expression.SqlExpression;
import net.luis.utils.io.database.function.functions.string.SqlConcatFunction;
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

public class MySqlStringFunctionRenderer extends SqlStringFunctionRenderer {
	
	public MySqlStringFunctionRenderer(@NonNull SqlDialect dialect) {
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
			renderer.literal("GROUP_CONCAT").openingBracket();
			if (distinct) {
				renderer.distinct();
			}
			for (int i = 0; i < values.size(); i++) {
				if (i > 0) {
					renderer.comma();
				}
				renderer.rendered(values.get(i).toSql(this.dialect));
			}
			if (ordered && !values.isEmpty()) {
				renderer.orderBy().rendered(values.getFirst().toSql(this.dialect));
			}
			separator.ifPresent(s -> renderer.literal("SEPARATOR").parameter(DEFAULT_STRING_TYPE, s));
			renderer.closingBracket();
		} else if (separator.isPresent()) {
			renderer.literal("CONCAT_WS").openingBracket();
			renderer.parameter(DEFAULT_STRING_TYPE, separator.get());
			for (SqlExpression<? extends CharSequence> value : values) {
				renderer.comma().rendered(value.toSql(this.dialect));
			}
			renderer.closingBracket();
		} else {
			renderer.literal("CONCAT").openingBracket();
			for (int i = 0; i < values.size(); i++) {
				if (i > 0) {
					renderer.comma();
				}
				renderer.rendered(values.get(i).toSql(this.dialect));
			}
			renderer.closingBracket();
		}
		return renderer.toSql();
	}
}
