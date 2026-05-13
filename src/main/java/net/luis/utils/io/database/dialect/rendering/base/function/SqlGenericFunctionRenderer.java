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

package net.luis.utils.io.database.dialect.rendering.base.function;

import net.luis.utils.io.database.dialect.SqlDialect;
import net.luis.utils.io.database.dialect.rendering.base.SqlRenderingHelper;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.exception.dialect.SqlDialectUnsupportedRenderingException;
import net.luis.utils.io.database.expression.SqlExpression;
import net.luis.utils.io.database.function.SqlFunction;
import net.luis.utils.io.database.function.functions.generic.*;
import net.luis.utils.io.database.rendering.SqlRendered;
import net.luis.utils.io.database.rendering.SqlRenderer;
import net.luis.utils.io.database.util.SqlCaseWhenBranch;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

/**
 *
 * @author Luis-St
 *
 */

public class SqlGenericFunctionRenderer {
	
	protected final SqlDialect dialect;
	
	public SqlGenericFunctionRenderer(@NonNull SqlDialect dialect) {
		this.dialect = Objects.requireNonNull(dialect, "Sql dialect must not be null");
	}
	
	public @NonNull SqlRendered render(@NonNull SqlFunction<?> function) throws SqlException {
		return switch (function) {
			case SqlCaseWhenFunction<?> func -> this.renderCaseWhen(func);
			case SqlCoalesceFunction<?> func -> this.renderCoalesce(func);
			case SqlGreatestFunction<?> func -> this.renderGreatest(func);
			case SqlLeastFunction<?> func -> this.renderLeast(func);
			case SqlNullIfFunction<?> func -> this.renderNullIf(func);
			case SqlUnsafeFunction<?> func -> this.renderUnsafe(func);
			
			case null -> throw new NullPointerException("Sql function must not be null");
			default -> throw new SqlDialectUnsupportedRenderingException("Unknown sql function type: " + function.getClass().getName() + " in dialect " + this.dialect.name());
		};
	}
	
	protected @NonNull SqlRendered renderCaseWhen(@NonNull SqlCaseWhenFunction<?> function) throws SqlException {
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.case_();
		for (SqlCaseWhenBranch<?> branch : function.branches()) {
			renderer.when().rendered(branch.condition().toSql(this.dialect));
			renderer.then().rendered(branch.expression().toSql(this.dialect));
		}
		SqlExpression<?> elseValue = function.elseValue();
		if (elseValue != null) {
			renderer.else_().rendered(elseValue.toSql(this.dialect));
		}
		renderer.end();
		return renderer.toSql();
	}
	
	protected @NonNull SqlRendered renderCoalesce(@NonNull SqlCoalesceFunction<?> function) throws SqlException {
		return SqlRenderingHelper.renderFunctionCallWithList(this.dialect, "COALESCE", function.expressions());
	}
	
	protected @NonNull SqlRendered renderGreatest(@NonNull SqlGreatestFunction<?> function) throws SqlException {
		return SqlRenderingHelper.renderFunctionCallWithList(this.dialect, "GREATEST", function.expressions());
	}
	
	protected @NonNull SqlRendered renderLeast(@NonNull SqlLeastFunction<?> function) throws SqlException {
		return SqlRenderingHelper.renderFunctionCallWithList(this.dialect, "LEAST", function.expressions());
	}
	
	protected @NonNull SqlRendered renderNullIf(@NonNull SqlNullIfFunction<?> function) throws SqlException {
		return SqlRenderingHelper.renderFunctionCall(this.dialect, "NULLIF", function.expression(), function.compareValue());
	}
	
	protected @NonNull SqlRendered renderUnsafe(@NonNull SqlUnsafeFunction<?> function) throws SqlException {
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.literal(function.expression());
		SqlRendered args = SqlRenderingHelper.renderFunctionCallWithList(this.dialect, "", function.arguments());
		renderer.rendered(args);
		return renderer.toSql();
	}
}
