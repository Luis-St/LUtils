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

import net.luis.utils.io.database.Sql;
import net.luis.utils.io.database.condition.conditions.SqlTemporalCondition;
import net.luis.utils.io.database.condition.conditions.temporal.*;
import net.luis.utils.io.database.dialect.SqlDialect;
import net.luis.utils.io.database.dialect.renderer.SqlRenderingHelper;
import net.luis.utils.io.database.dialect.renderer.expression.function.SqlTemporalFunctionRenderer;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.exception.client.dialect.SqlDialectUnknownConstructException;
import net.luis.utils.io.database.expression.SqlExpression;
import net.luis.utils.io.database.expression.SqlValueExpression;
import net.luis.utils.io.database.function.functions.temporal.*;
import net.luis.utils.io.database.rendering.SqlRendered;
import net.luis.utils.io.database.rendering.SqlRenderer;
import net.luis.utils.io.database.type.SqlType;
import net.luis.utils.io.database.type.SqlTypes;
import net.luis.utils.io.database.type.parameter.SqlParameter;
import net.luis.utils.io.database.util.SqlTemporalPart;
import org.jspecify.annotations.NonNull;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Renderer for temporal sql conditions into dialect-specific sql.<br>
 * Handles conditions such as before, after and within a duration relative to the current timestamp.<br>
 *
 * @author Luis-St
 */

@SuppressWarnings("unchecked")
public class SqlTemporalConditionRenderer {
	
	/**
	 * The renderer used to render temporal arithmetic functions.
	 */
	private final SqlTemporalFunctionRenderer temporalRenderer;
	/**
	 * The sql dialect used to render the conditions.
	 */
	protected final SqlDialect dialect;
	
	/**
	 * Constructs a new sql temporal condition renderer for the given dialect and temporal function renderer.<br>
	 *
	 * @param dialect The sql dialect used to render the conditions
	 * @param temporalRenderer The renderer used to render temporal arithmetic functions
	 * @throws NullPointerException If the dialect or temporal renderer is null
	 */
	public SqlTemporalConditionRenderer(@NonNull SqlDialect dialect, @NonNull SqlTemporalFunctionRenderer temporalRenderer) {
		this.dialect = Objects.requireNonNull(dialect, "Sql dialect must not be null");
		this.temporalRenderer = Objects.requireNonNull(temporalRenderer, "Sql temporal function renderer must not be null");
	}
	
	/**
	 * Renders the given temporal condition into dialect-specific sql.<br>
	 * The condition is dispatched to the matching render method based on its concrete type.<br>
	 *
	 * @param condition The temporal condition to render
	 * @return The rendered sql
	 * @throws NullPointerException If the condition is null
	 * @throws SqlException If rendering fails
	 */
	public @NonNull SqlRendered render(@NonNull SqlTemporalCondition condition) throws SqlException {
		return switch (condition) {
			case SqlAfterCondition cond -> this.renderAfter(cond);
			case SqlBeforeCondition cond -> this.renderBefore(cond);
			case SqlWithinLastCondition cond -> this.renderWithinLast(cond);
			case SqlWithinNextCondition cond -> this.renderWithinNext(cond);
			
			case null -> throw new NullPointerException("Sql temporal condition must not be null");
			default -> throw new SqlDialectUnknownConstructException("Unknown sql temporal condition type: " + condition.getClass().getName() + " in dialect " + this.dialect.name());
		};
	}
	
	/**
	 * Renders the given after condition into dialect-specific sql.<br>
	 * Produces a {@code value > earlierBound} expression.<br>
	 *
	 * @param condition The after condition to render
	 * @return The rendered sql
	 * @throws NullPointerException If the condition is null
	 * @throws SqlException If rendering fails
	 */
	protected @NonNull SqlRendered renderAfter(@NonNull SqlAfterCondition condition) throws SqlException {
		Objects.requireNonNull(condition, "Sql condition must not be null");
		
		return SqlRenderingHelper.renderInfix(this.dialect, condition.value(), ">", condition.earlierBound());
	}
	
	/**
	 * Renders the given before condition into dialect-specific sql.<br>
	 * Produces a {@code value < laterBound} expression.<br>
	 *
	 * @param condition The before condition to render
	 * @return The rendered sql
	 * @throws NullPointerException If the condition is null
	 * @throws SqlException If rendering fails
	 */
	protected @NonNull SqlRendered renderBefore(@NonNull SqlBeforeCondition condition) throws SqlException {
		Objects.requireNonNull(condition, "Sql condition must not be null");
		
		return SqlRenderingHelper.renderInfix(this.dialect, condition.value(), "<", condition.laterBound());
	}
	
	/**
	 * Renders the given within-last condition into dialect-specific sql.<br>
	 * Produces an expression that checks whether the value lies between the current timestamp minus the duration and the current timestamp.<br>
	 *
	 * @param condition The within-last condition to render
	 * @return The rendered sql
	 * @throws NullPointerException If the condition is null
	 * @throws SqlException If rendering fails
	 */
	protected @NonNull SqlRendered renderWithinLast(@NonNull SqlWithinLastCondition condition) throws SqlException {
		Objects.requireNonNull(condition, "Sql condition must not be null");
		
		SqlType<LocalDateTime> timestampType = SqlTypes.LOCAL_DATE_TIME.configure(SqlParameter.fractional(0));
		SqlExpression<LocalDateTime> now = new SqlCurrentTimestampFunction<>(timestampType);
		SqlRendered lowerBound = this.temporalRenderer.render(new SqlTemporalSubtractFunction<>(now, SqlTemporalPart.SECOND, this.durationInSeconds(condition.duration()), timestampType));
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.openingBracket();
		renderer.rendered(condition.value().toSql(this.dialect)).literal(">=").openingBracket().rendered(lowerBound).closingBracket();
		renderer.literal("AND");
		renderer.rendered(condition.value().toSql(this.dialect)).literal("<=").openingBracket().rendered(now.toSql(this.dialect)).closingBracket();
		renderer.closingBracket();
		return renderer.toSql();
	}
	
	/**
	 * Renders the given within-next condition into dialect-specific sql.<br>
	 * Produces an expression that checks whether the value lies between the current timestamp and the current timestamp plus the duration.<br>
	 *
	 * @param condition The within-next condition to render
	 * @return The rendered sql
	 * @throws NullPointerException If the condition is null
	 * @throws SqlException If rendering fails
	 */
	protected @NonNull SqlRendered renderWithinNext(@NonNull SqlWithinNextCondition condition) throws SqlException {
		Objects.requireNonNull(condition, "Sql condition must not be null");
		
		SqlType<LocalDateTime> timestampType = SqlTypes.LOCAL_DATE_TIME.configure(SqlParameter.fractional(0));
		SqlExpression<LocalDateTime> now = new SqlCurrentTimestampFunction<>(timestampType);
		SqlRendered upperBound = this.temporalRenderer.render(new SqlTemporalAddFunction<>(now, SqlTemporalPart.SECOND, this.durationInSeconds(condition.duration()), timestampType));
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.openingBracket();
		renderer.rendered(condition.value().toSql(this.dialect)).literal(">=").openingBracket().rendered(now.toSql(this.dialect)).closingBracket();
		renderer.literal("AND");
		renderer.rendered(condition.value().toSql(this.dialect)).literal("<=").openingBracket().rendered(upperBound).closingBracket();
		renderer.closingBracket();
		return renderer.toSql();
	}
	
	/**
	 * Converts the given duration expression into an expression yielding the duration in seconds.<br>
	 * A constant {@link Duration} value is converted directly to its total seconds, otherwise the nanosecond expression is divided by one billion.<br>
	 *
	 * @param duration The duration expression to convert
	 * @return An expression yielding the duration in seconds
	 * @throws NullPointerException If the duration expression is null
	 */
	private @NonNull SqlExpression<? extends Number> durationInSeconds(@NonNull SqlExpression<?> duration) {
		Objects.requireNonNull(duration, "Sql duration expression must not be null");
		
		if (duration instanceof SqlValueExpression<?> value && value.value() instanceof Duration constant) {
			return new SqlValueExpression<>(constant.toSeconds(), SqlTypes.LONG);
		}
		SqlExpression<Long> nanoseconds = (SqlExpression<Long>) duration;
		return Sql.divide(nanoseconds, new SqlValueExpression<>(1_000_000_000L, SqlTypes.LONG));
	}
}
