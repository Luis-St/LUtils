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
import net.luis.utils.io.database.exception.dialect.SqlDialectUnsupportedRenderingException;
import net.luis.utils.io.database.expression.SqlExpression;
import net.luis.utils.io.database.function.functions.SqlTemporalFunction;
import net.luis.utils.io.database.function.functions.temporal.*;
import net.luis.utils.io.database.rendering.SqlRendered;
import net.luis.utils.io.database.rendering.SqlRenderer;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

/**
 *
 * @author Luis-St
 *
 */

public class SqlTemporalFunctionRenderer {
	
	protected final SqlDialect dialect;
	
	public SqlTemporalFunctionRenderer(@NonNull SqlDialect dialect) {
		this.dialect = Objects.requireNonNull(dialect, "Sql dialect must not be null");
	}
	
	public @NonNull SqlRendered render(@NonNull SqlTemporalFunction<?> function) throws SqlException {
		return switch (function) {
			case SqlTemporalAddFunction<?> func -> this.renderTemporalAdd(func);
			case SqlCurrentDateFunction<?> func -> this.renderCurrentDate(func);
			case SqlCurrentTimeFunction<?> func -> this.renderCurrentTime(func);
			case SqlCurrentTimestampFunction<?> func -> this.renderCurrentTimestamp(func);
			case SqlExtractFunction<?> func -> this.renderExtract(func);
			case SqlFromEpochFunction<?> func -> this.renderFromEpoch(func);
			case SqlMakeDateFunction<?> func -> this.renderMakeDate(func);
			case SqlMakeTimeFunction<?> func -> this.renderMakeTime(func);
			case SqlNowFunction<?> func -> this.renderNow(func);
			case SqlTemporalSubtractFunction<?> func -> this.renderTemporalSubtract(func);
			case SqlToDateFunction<?> func -> this.renderToDate(func);
			case SqlToTimeFunction<?> func -> this.renderToTime(func);
			case SqlToEpochFunction<?> func -> this.renderToEpoch(func);
			case SqlTemporalTruncateFunction<?> func -> this.renderTemporalTruncate(func);
			
			case null -> throw new NullPointerException("Sql temporal function must not be null");
			default -> throw new SqlDialectUnsupportedRenderingException("Unknown sql temporal function type: " + function.getClass().getName() + " in dialect " + this.dialect.name());
		};
	}
	
	protected @NonNull SqlRendered renderTemporalAdd(@NonNull SqlTemporalAddFunction<?> function) throws SqlException {
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.rendered(function.firstSummand().toSql(this.dialect));
		renderer.literal("+");
		renderer.rendered(this.renderInterval(function.secondSummand(), function.part().name()));
		return renderer.toSql();
	}
	
	protected @NonNull SqlRendered renderCurrentDate(@NonNull SqlCurrentDateFunction<?> function) throws SqlException {
		return SqlRendered.of("CURRENT_DATE");
	}
	
	protected @NonNull SqlRendered renderCurrentTime(@NonNull SqlCurrentTimeFunction<?> function) throws SqlException {
		return SqlRendered.of("CURRENT_TIME");
	}
	
	protected @NonNull SqlRendered renderCurrentTimestamp(@NonNull SqlCurrentTimestampFunction<?> function) throws SqlException {
		return SqlRendered.of("CURRENT_TIMESTAMP");
	}
	
	protected @NonNull SqlRendered renderExtract(@NonNull SqlExtractFunction<?> function) throws SqlException {
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.literal("EXTRACT").openingBracket().keyword(function.part().name()).from().rendered(function.expression().toSql(this.dialect)).closingBracket();
		return renderer.toSql();
	}
	
	protected @NonNull SqlRendered renderFromEpoch(@NonNull SqlFromEpochFunction<?> function) throws SqlException {
		throw new SqlDialectUnsupportedRenderingException("FROM_EPOCH is not supported by dialect " + this.dialect.name() + ", requires dialect-specific override");
	}
	
	protected @NonNull SqlRendered renderMakeDate(@NonNull SqlMakeDateFunction<?> function) throws SqlException {
		throw new SqlDialectUnsupportedRenderingException("MAKE_DATE is not supported by dialect " + this.dialect.name() + ", requires dialect-specific override");
	}
	
	protected @NonNull SqlRendered renderMakeTime(@NonNull SqlMakeTimeFunction<?> function) throws SqlException {
		throw new SqlDialectUnsupportedRenderingException("MAKE_TIME is not supported by dialect " + this.dialect.name() + ", requires dialect-specific override");
	}
	
	protected @NonNull SqlRendered renderNow(@NonNull SqlNowFunction<?> function) throws SqlException {
		return SqlRendered.of("CURRENT_TIMESTAMP");
	}
	
	protected @NonNull SqlRendered renderTemporalSubtract(@NonNull SqlTemporalSubtractFunction<?> function) throws SqlException {
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.rendered(function.minuend().toSql(this.dialect));
		renderer.literal("-");
		renderer.rendered(this.renderInterval(function.subtrahend(), function.part().name()));
		return renderer.toSql();
	}
	
	protected @NonNull SqlRendered renderToDate(@NonNull SqlToDateFunction<?> function) throws SqlException {
		SqlRendered inner = function.expression().toSql(this.dialect);
		return SqlRenderer.empty().cast().openingBracket().rendered(inner).as().literal("DATE").closingBracket().toSql();
	}
	
	protected @NonNull SqlRendered renderToTime(@NonNull SqlToTimeFunction<?> function) throws SqlException {
		SqlRendered inner = function.expression().toSql(this.dialect);
		return SqlRenderer.empty().cast().openingBracket().rendered(inner).as().literal("TIME").closingBracket().toSql();
	}
	
	protected @NonNull SqlRendered renderToEpoch(@NonNull SqlToEpochFunction<?> function) throws SqlException {
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.literal("EXTRACT").openingBracket().keyword("EPOCH").from().rendered(function.expression().toSql(this.dialect)).closingBracket();
		return renderer.toSql();
	}
	
	protected @NonNull SqlRendered renderTemporalTruncate(@NonNull SqlTemporalTruncateFunction<?> function) throws SqlException {
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.literal("DATE_TRUNC").openingBracket().literal("'" + function.part().name() + "'").comma().rendered(function.expression().toSql(this.dialect)).closingBracket();
		return renderer.toSql();
	}
	
	public @NonNull SqlRendered renderInterval(@NonNull SqlExpression<?> duration, @NonNull String part) throws SqlException {
		Objects.requireNonNull(duration, "Duration expression must not be null");
		Objects.requireNonNull(part, "Temporal part must not be null");
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.rendered(duration.toSql(this.dialect)).literal("* INTERVAL '1'").literal(part);
		return renderer.toSql();
	}
}
