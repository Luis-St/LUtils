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
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.exception.client.dialect.SqlDialectUnknownConstructException;
import net.luis.utils.io.database.exception.client.dialect.SqlDialectUnsupportedRenderingException;
import net.luis.utils.io.database.expression.SqlExpression;
import net.luis.utils.io.database.function.functions.SqlTemporalFunction;
import net.luis.utils.io.database.function.functions.temporal.*;
import net.luis.utils.io.database.rendering.SqlRendered;
import net.luis.utils.io.database.rendering.SqlRenderer;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

/**
 * Renderer for temporal sql functions into dialect-specific sql.<br>
 * Dispatches the given {@link SqlTemporalFunction} to the matching render method based on its concrete type<br>
 * and produces the resulting {@link SqlRendered} using the configured {@link SqlDialect}.<br>
 *
 * @author Luis-St
 */

public class SqlTemporalFunctionRenderer {
	
	/**
	 * The sql dialect used to render the functions.
	 */
	protected final SqlDialect dialect;
	
	/**
	 * Constructs a new temporal sql function renderer for the given dialect.<br>
	 *
	 * @param dialect The sql dialect used to render the functions
	 * @throws NullPointerException If the dialect is null
	 */
	public SqlTemporalFunctionRenderer(@NonNull SqlDialect dialect) {
		this.dialect = Objects.requireNonNull(dialect, "Sql dialect must not be null");
	}
	
	/**
	 * Renders the given temporal sql function into dialect-specific sql.<br>
	 * The function is dispatched to the matching render method based on its concrete type.<br>
	 *
	 * @param function The temporal function to render
	 * @return The rendered sql
	 * @throws NullPointerException If the function is null
	 * @throws SqlException If rendering fails
	 */
	public @NonNull SqlRendered render(@NonNull SqlTemporalFunction<?> function) throws SqlException {
		return switch (function) {
			case SqlTemporalAddFunction<?> func -> this.renderTemporalAdd(func);
			case SqlCurrentDateFunction<?> _ -> this.renderCurrentDate();
			case SqlCurrentTimeFunction<?> _ -> this.renderCurrentTime();
			case SqlCurrentTimestampFunction<?> _ -> this.renderCurrentTimestamp();
			case SqlExtractFunction<?> func -> this.renderExtract(func);
			case SqlFromEpochFunction<?> func -> this.renderFromEpoch(func);
			case SqlMakeDateFunction<?> func -> this.renderMakeDate(func);
			case SqlMakeTimeFunction<?> func -> this.renderMakeTime(func);
			case SqlNowFunction<?> _ -> this.renderNow();
			case SqlTemporalSubtractFunction<?> func -> this.renderTemporalSubtract(func);
			case SqlToDateFunction<?> func -> this.renderToDate(func);
			case SqlToTimeFunction<?> func -> this.renderToTime(func);
			case SqlToEpochFunction<?> func -> this.renderToEpoch(func);
			case SqlTemporalTruncateFunction<?> func -> this.renderTemporalTruncate(func);
			
			case null -> throw new NullPointerException("Sql temporal function must not be null");
			default -> throw new SqlDialectUnknownConstructException("Unknown sql temporal function type: " + function.getClass().getName() + " in dialect " + this.dialect.name());
		};
	}
	
	/**
	 * Renders the given temporal-add function into dialect-specific sql.<br>
	 * The first summand is rendered, followed by a {@code +} operator and the second summand rendered as an interval of the function part.<br>
	 *
	 * @param function The temporal-add function to render
	 * @return The rendered sql
	 * @throws NullPointerException If the function is null
	 * @throws SqlException If rendering fails
	 */
	protected @NonNull SqlRendered renderTemporalAdd(@NonNull SqlTemporalAddFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.rendered(function.firstSummand().toSql(this.dialect));
		renderer.literal("+");
		renderer.rendered(this.renderInterval(function.secondSummand(), function.part().name()));
		return renderer.toSql();
	}
	
	/**
	 * Renders the current-date function into dialect-specific sql.<br>
	 * The function is rendered as the {@code CURRENT_DATE} keyword.<br>
	 *
	 * @return The rendered sql
	 * @throws SqlException If rendering fails
	 */
	protected @NonNull SqlRendered renderCurrentDate() throws SqlException {
		return SqlRendered.of("CURRENT_DATE");
	}
	
	/**
	 * Renders the current-time function into dialect-specific sql.<br>
	 * The function is rendered as the {@code CURRENT_TIME} keyword.<br>
	 *
	 * @return The rendered sql
	 * @throws SqlException If rendering fails
	 */
	protected @NonNull SqlRendered renderCurrentTime() throws SqlException {
		return SqlRendered.of("CURRENT_TIME");
	}
	
	/**
	 * Renders the current-timestamp function into dialect-specific sql.<br>
	 * The function is rendered as the {@code CURRENT_TIMESTAMP} keyword.<br>
	 *
	 * @return The rendered sql
	 * @throws SqlException If rendering fails
	 */
	protected @NonNull SqlRendered renderCurrentTimestamp() throws SqlException {
		return SqlRendered.of("CURRENT_TIMESTAMP");
	}
	
	/**
	 * Renders the given extract function into dialect-specific sql.<br>
	 * The function is rendered as an {@code EXTRACT(part FROM expression)} call.<br>
	 *
	 * @param function The extract function to render
	 * @return The rendered sql
	 * @throws NullPointerException If the function is null
	 * @throws SqlException If rendering fails
	 */
	protected @NonNull SqlRendered renderExtract(@NonNull SqlExtractFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.literal("EXTRACT").openingBracket().keyword(function.part().name()).from().rendered(function.expression().toSql(this.dialect)).closingBracket();
		return renderer.toSql();
	}
	
	/**
	 * Renders the given from-epoch function into dialect-specific sql.<br>
	 * This base implementation does not support the function and requires a dialect-specific override.<br>
	 *
	 * @param function The from-epoch function to render
	 * @return The rendered sql
	 * @throws SqlException If the dialect does not support rendering this function
	 */
	protected @NonNull SqlRendered renderFromEpoch(@NonNull SqlFromEpochFunction<?> function) throws SqlException {
		throw new SqlDialectUnsupportedRenderingException("FROM_EPOCH is not supported by dialect " + this.dialect.name() + ", requires dialect-specific override");
	}
	
	/**
	 * Renders the given make-date function into dialect-specific sql.<br>
	 * This base implementation does not support the function and requires a dialect-specific override.<br>
	 *
	 * @param function The make-date function to render
	 * @return The rendered sql
	 * @throws SqlException If the dialect does not support rendering this function
	 */
	protected @NonNull SqlRendered renderMakeDate(@NonNull SqlMakeDateFunction<?> function) throws SqlException {
		throw new SqlDialectUnsupportedRenderingException("MAKE_DATE is not supported by dialect " + this.dialect.name() + ", requires dialect-specific override");
	}
	
	/**
	 * Renders the given make-time function into dialect-specific sql.<br>
	 * This base implementation does not support the function and requires a dialect-specific override.<br>
	 *
	 * @param function The make-time function to render
	 * @return The rendered sql
	 * @throws SqlException If the dialect does not support rendering this function
	 */
	protected @NonNull SqlRendered renderMakeTime(@NonNull SqlMakeTimeFunction<?> function) throws SqlException {
		throw new SqlDialectUnsupportedRenderingException("MAKE_TIME is not supported by dialect " + this.dialect.name() + ", requires dialect-specific override");
	}
	
	/**
	 * Renders the now function into dialect-specific sql.<br>
	 * The function is rendered as the {@code CURRENT_TIMESTAMP} keyword.<br>
	 *
	 * @return The rendered sql
	 * @throws SqlException If rendering fails
	 */
	protected @NonNull SqlRendered renderNow() throws SqlException {
		return SqlRendered.of("CURRENT_TIMESTAMP");
	}
	
	/**
	 * Renders the given temporal-subtract function into dialect-specific sql.<br>
	 * The minuend is rendered, followed by a {@code -} operator and the subtrahend rendered as an interval of the function part.<br>
	 *
	 * @param function The temporal-subtract function to render
	 * @return The rendered sql
	 * @throws NullPointerException If the function is null
	 * @throws SqlException If rendering fails
	 */
	protected @NonNull SqlRendered renderTemporalSubtract(@NonNull SqlTemporalSubtractFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.rendered(function.minuend().toSql(this.dialect));
		renderer.literal("-");
		renderer.rendered(this.renderInterval(function.subtrahend(), function.part().name()));
		return renderer.toSql();
	}
	
	/**
	 * Renders the given to-date function into dialect-specific sql.<br>
	 * The function is rendered as a {@code CAST(expression AS DATE)} call.<br>
	 *
	 * @param function The to-date function to render
	 * @return The rendered sql
	 * @throws NullPointerException If the function is null
	 * @throws SqlException If rendering fails
	 */
	protected @NonNull SqlRendered renderToDate(@NonNull SqlToDateFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		SqlRendered inner = function.expression().toSql(this.dialect);
		return SqlRenderer.empty().cast().openingBracket().rendered(inner).as().literal("DATE").closingBracket().toSql();
	}
	
	/**
	 * Renders the given to-time function into dialect-specific sql.<br>
	 * The function is rendered as a {@code CAST(expression AS TIME)} call.<br>
	 *
	 * @param function The to-time function to render
	 * @return The rendered sql
	 * @throws NullPointerException If the function is null
	 * @throws SqlException If rendering fails
	 */
	protected @NonNull SqlRendered renderToTime(@NonNull SqlToTimeFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		SqlRendered inner = function.expression().toSql(this.dialect);
		return SqlRenderer.empty().cast().openingBracket().rendered(inner).as().literal("TIME").closingBracket().toSql();
	}
	
	/**
	 * Renders the given to-epoch function into dialect-specific sql.<br>
	 * The function is rendered as an {@code EXTRACT(EPOCH FROM expression)} call.<br>
	 *
	 * @param function The to-epoch function to render
	 * @return The rendered sql
	 * @throws NullPointerException If the function is null
	 * @throws SqlException If rendering fails
	 */
	protected @NonNull SqlRendered renderToEpoch(@NonNull SqlToEpochFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.literal("EXTRACT").openingBracket().keyword("EPOCH").from().rendered(function.expression().toSql(this.dialect)).closingBracket();
		return renderer.toSql();
	}
	
	/**
	 * Renders the given temporal-truncate function into dialect-specific sql.<br>
	 * The function is rendered as a {@code DATE_TRUNC('part', expression)} call.<br>
	 *
	 * @param function The temporal-truncate function to render
	 * @return The rendered sql
	 * @throws NullPointerException If the function is null
	 * @throws SqlException If rendering fails
	 */
	protected @NonNull SqlRendered renderTemporalTruncate(@NonNull SqlTemporalTruncateFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.literal("DATE_TRUNC").openingBracket().literal("'" + function.part().name() + "'").comma().rendered(function.expression().toSql(this.dialect)).closingBracket();
		return renderer.toSql();
	}
	
	/**
	 * Renders the given duration as an interval expression for the given temporal part.<br>
	 * The duration is rendered and multiplied by a single-unit {@code INTERVAL} of the given part.<br>
	 *
	 * @param duration The duration expression to render
	 * @param part The temporal part of the interval
	 * @return The rendered sql
	 * @throws NullPointerException If the duration or the part is null
	 * @throws SqlException If rendering fails
	 */
	public @NonNull SqlRendered renderInterval(@NonNull SqlExpression<?> duration, @NonNull String part) throws SqlException {
		Objects.requireNonNull(duration, "Sql duration expression must not be null");
		Objects.requireNonNull(part, "Temporal part must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.rendered(duration.toSql(this.dialect)).literal("* INTERVAL '1'").literal(part);
		return renderer.toSql();
	}
}
