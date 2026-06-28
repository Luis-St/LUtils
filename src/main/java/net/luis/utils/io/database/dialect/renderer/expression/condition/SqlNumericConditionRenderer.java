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

package net.luis.utils.io.database.dialect.renderer.expression.condition;

import net.luis.utils.io.database.condition.conditions.SqlNumericCondition;
import net.luis.utils.io.database.condition.conditions.numeric.*;
import net.luis.utils.io.database.dialect.SqlDialect;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.exception.client.dialect.SqlDialectUnknownConstructException;
import net.luis.utils.io.database.rendering.SqlRendered;
import net.luis.utils.io.database.rendering.SqlRenderer;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

/**
 * Renderer for numeric sql conditions into dialect-specific sql.<br>
 * Handles sign checks such as negative, positive and zero, as well as modulo conditions.<br>
 *
 * @author Luis-St
 */

public class SqlNumericConditionRenderer {
	
	/**
	 * The sql dialect used to render the conditions.
	 */
	protected final SqlDialect dialect;
	
	/**
	 * Constructs a new sql numeric condition renderer for the given dialect.<br>
	 *
	 * @param dialect The sql dialect used to render the conditions
	 * @throws NullPointerException If the dialect is null
	 */
	public SqlNumericConditionRenderer(@NonNull SqlDialect dialect) {
		this.dialect = Objects.requireNonNull(dialect, "Sql dialect must not be null");
	}
	
	/**
	 * Renders the given numeric condition into dialect-specific sql.<br>
	 * The condition is dispatched to the matching render method based on its concrete type.<br>
	 *
	 * @param condition The numeric condition to render
	 * @return The rendered sql
	 * @throws NullPointerException If the condition is null
	 * @throws SqlException If rendering fails
	 */
	public @NonNull SqlRendered render(@NonNull SqlNumericCondition condition) throws SqlException {
		return switch (condition) {
			case SqlIsNegativeCondition cond -> this.renderIsNegative(cond);
			case SqlIsPositiveCondition cond -> this.renderIsPositive(cond);
			case SqlIsZeroCondition cond -> this.renderIsZero(cond);
			case SqlModEqualsCondition cond -> this.renderModEquals(cond);
			
			case null -> throw new NullPointerException("Sql numeric condition must not be null");
			default -> throw new SqlDialectUnknownConstructException("Unknown sql numeric condition type: " + condition.getClass().getName() + " in dialect " + this.dialect.name());
		};
	}
	
	/**
	 * Renders the given is-negative condition into dialect-specific sql.<br>
	 * Produces a {@code value < 0} expression.<br>
	 *
	 * @param condition The is-negative condition to render
	 * @return The rendered sql
	 * @throws NullPointerException If the condition is null
	 * @throws SqlException If rendering fails
	 */
	protected @NonNull SqlRendered renderIsNegative(@NonNull SqlIsNegativeCondition condition) throws SqlException {
		Objects.requireNonNull(condition, "Sql condition must not be null");
		
		return SqlRenderer.empty().rendered(condition.value().toSql(this.dialect)).literal("<").literal("0").toSql();
	}
	
	/**
	 * Renders the given is-positive condition into dialect-specific sql.<br>
	 * Produces a {@code value > 0} expression.<br>
	 *
	 * @param condition The is-positive condition to render
	 * @return The rendered sql
	 * @throws NullPointerException If the condition is null
	 * @throws SqlException If rendering fails
	 */
	protected @NonNull SqlRendered renderIsPositive(@NonNull SqlIsPositiveCondition condition) throws SqlException {
		Objects.requireNonNull(condition, "Sql condition must not be null");
		
		return SqlRenderer.empty().rendered(condition.value().toSql(this.dialect)).literal(">").literal("0").toSql();
	}
	
	/**
	 * Renders the given is-zero condition into dialect-specific sql.<br>
	 * Produces a {@code value = 0} expression.<br>
	 *
	 * @param condition The is-zero condition to render
	 * @return The rendered sql
	 * @throws NullPointerException If the condition is null
	 * @throws SqlException If rendering fails
	 */
	protected @NonNull SqlRendered renderIsZero(@NonNull SqlIsZeroCondition condition) throws SqlException {
		Objects.requireNonNull(condition, "Sql condition must not be null");
		
		return SqlRenderer.empty().rendered(condition.value().toSql(this.dialect)).literal("=").literal("0").toSql();
	}
	
	/**
	 * Renders the given mod-equals condition into dialect-specific sql.<br>
	 * Produces a {@code MOD(value, divisor) = remainder} expression.<br>
	 *
	 * @param condition The mod-equals condition to render
	 * @return The rendered sql
	 * @throws NullPointerException If the condition is null
	 * @throws SqlException If rendering fails
	 */
	protected @NonNull SqlRendered renderModEquals(@NonNull SqlModEqualsCondition condition) throws SqlException {
		Objects.requireNonNull(condition, "Sql condition must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.literal("MOD").openingBracket().rendered(condition.value().toSql(this.dialect)).comma().rendered(condition.divisor().toSql(this.dialect)).closingBracket();
		renderer.literal("=").rendered(condition.remainder().toSql(this.dialect));
		return renderer.toSql();
	}
}
