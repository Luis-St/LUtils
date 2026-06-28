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
import net.luis.utils.io.database.function.SqlFunction;
import net.luis.utils.io.database.function.functions.generic.*;
import net.luis.utils.io.database.rendering.SqlRendered;
import net.luis.utils.io.database.rendering.SqlRenderer;
import net.luis.utils.io.database.util.SqlCaseWhenBranch;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

/**
 * Renderer for generic sql functions into dialect-specific sql.<br>
 * Dispatches the given {@link SqlFunction} to the matching render method based on its concrete type<br>
 * and produces the resulting {@link SqlRendered} using the configured {@link SqlDialect}.<br>
 *
 * @author Luis-St
 */

public class SqlGenericFunctionRenderer {
	
	/**
	 * The sql dialect used to render the functions.
	 */
	protected final SqlDialect dialect;
	
	/**
	 * Constructs a new generic sql function renderer for the given dialect.<br>
	 *
	 * @param dialect The sql dialect used to render the functions
	 * @throws NullPointerException If the dialect is null
	 */
	public SqlGenericFunctionRenderer(@NonNull SqlDialect dialect) {
		this.dialect = Objects.requireNonNull(dialect, "Sql dialect must not be null");
	}
	
	/**
	 * Renders the given generic sql function into dialect-specific sql.<br>
	 * The function is dispatched to the matching render method based on its concrete type.<br>
	 *
	 * @param function The sql function to render
	 * @return The rendered sql
	 * @throws NullPointerException If the function is null
	 * @throws SqlException If rendering fails
	 */
	public @NonNull SqlRendered render(@NonNull SqlFunction<?> function) throws SqlException {
		return switch (function) {
			case SqlCaseWhenFunction<?> func -> this.renderCaseWhen(func);
			case SqlCoalesceFunction<?> func -> this.renderCoalesce(func);
			case SqlGreatestFunction<?> func -> this.renderGreatest(func);
			case SqlLeastFunction<?> func -> this.renderLeast(func);
			case SqlNullIfFunction<?> func -> this.renderNullIf(func);
			case SqlUnsafeFunction<?> func -> this.renderUnsafe(func);
			
			case null -> throw new NullPointerException("Sql function must not be null");
			default -> throw new SqlDialectUnknownConstructException("Unknown sql function type: " + function.getClass().getName() + " in dialect " + this.dialect.name());
		};
	}
	
	/**
	 * Renders the given case-when function into dialect-specific sql.<br>
	 * Each branch is rendered as a {@code WHEN ... THEN ...} pair, followed by an optional {@code ELSE} value.<br>
	 *
	 * @param function The case-when function to render
	 * @return The rendered sql
	 * @throws NullPointerException If the function is null
	 * @throws SqlException If rendering fails
	 */
	protected @NonNull SqlRendered renderCaseWhen(@NonNull SqlCaseWhenFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
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
	
	/**
	 * Renders the given coalesce function into dialect-specific sql.<br>
	 * The function is rendered as a {@code COALESCE} call with the comma-separated list of expressions.<br>
	 *
	 * @param function The coalesce function to render
	 * @return The rendered sql
	 * @throws NullPointerException If the function is null
	 * @throws SqlException If rendering fails
	 */
	protected @NonNull SqlRendered renderCoalesce(@NonNull SqlCoalesceFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		return SqlRenderingHelper.renderFunctionCallWithList(this.dialect, "COALESCE", function.expressions());
	}
	
	/**
	 * Renders the given greatest function into dialect-specific sql.<br>
	 * The function is rendered as a {@code GREATEST} call with the comma-separated list of expressions.<br>
	 *
	 * @param function The greatest function to render
	 * @return The rendered sql
	 * @throws NullPointerException If the function is null
	 * @throws SqlException If rendering fails
	 */
	protected @NonNull SqlRendered renderGreatest(@NonNull SqlGreatestFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		return SqlRenderingHelper.renderFunctionCallWithList(this.dialect, "GREATEST", function.expressions());
	}
	
	/**
	 * Renders the given least function into dialect-specific sql.<br>
	 * The function is rendered as a {@code LEAST} call with the comma-separated list of expressions.<br>
	 *
	 * @param function The least function to render
	 * @return The rendered sql
	 * @throws NullPointerException If the function is null
	 * @throws SqlException If rendering fails
	 */
	protected @NonNull SqlRendered renderLeast(@NonNull SqlLeastFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		return SqlRenderingHelper.renderFunctionCallWithList(this.dialect, "LEAST", function.expressions());
	}
	
	/**
	 * Renders the given null-if function into dialect-specific sql.<br>
	 * The function is rendered as a {@code NULLIF} call with the expression and the compare value.<br>
	 *
	 * @param function The null-if function to render
	 * @return The rendered sql
	 * @throws NullPointerException If the function is null
	 * @throws SqlException If rendering fails
	 */
	protected @NonNull SqlRendered renderNullIf(@NonNull SqlNullIfFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		return SqlRenderingHelper.renderFunctionCall(this.dialect, "NULLIF", function.expression(), function.compareValue());
	}
	
	/**
	 * Renders the given unsafe function into dialect-specific sql.<br>
	 * The raw function expression is rendered as a literal, followed by its argument list.<br>
	 *
	 * @param function The unsafe function to render
	 * @return The rendered sql
	 * @throws NullPointerException If the function is null
	 * @throws SqlException If rendering fails
	 */
	protected @NonNull SqlRendered renderUnsafe(@NonNull SqlUnsafeFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.literal(function.expression());
		SqlRendered args = SqlRenderingHelper.renderFunctionCallWithList(this.dialect, "", function.arguments());
		renderer.rendered(args);
		return renderer.toSql();
	}
}
