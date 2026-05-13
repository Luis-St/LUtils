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

package net.luis.utils.io.database.dialect.rendering.sqlite;

import net.luis.utils.io.database.dialect.SqlDialect;
import net.luis.utils.io.database.dialect.rendering.base.function.SqlTemporalFunctionRenderer;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.expression.SqlExpression;
import net.luis.utils.io.database.function.functions.temporal.*;
import net.luis.utils.io.database.rendering.SqlRendered;
import net.luis.utils.io.database.rendering.SqlRenderer;
import org.jspecify.annotations.NonNull;

/**
 *
 * @author Luis-St
 *
 */

public class SqliteTemporalFunctionRenderer extends SqlTemporalFunctionRenderer {
	
	public SqliteTemporalFunctionRenderer(@NonNull SqlDialect dialect) {
		super(dialect);
	}
	
	private static @NonNull String toSqliteModifier(@NonNull String part) {
		return switch (part.toUpperCase()) {
			case "YEAR" -> "years";
			case "MONTH" -> "months";
			case "DAY" -> "days";
			case "HOUR" -> "hours";
			case "MINUTE" -> "minutes";
			case "SECOND" -> "seconds";
			default -> part.toLowerCase() + "s";
		};
	}
	
	@Override
	protected @NonNull SqlRendered renderFromEpoch(@NonNull SqlFromEpochFunction<?> function) throws SqlException {
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.literal("datetime").openingBracket();
		renderer.rendered(function.expression().toSql(this.dialect)).comma().literal("'unixepoch'");
		renderer.closingBracket();
		return renderer.toSql();
	}
	
	@Override
	protected @NonNull SqlRendered renderToEpoch(@NonNull SqlToEpochFunction<?> function) throws SqlException {
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.literal("strftime").openingBracket();
		renderer.literal("'%s'").comma().rendered(function.expression().toSql(this.dialect));
		renderer.closingBracket();
		return renderer.toSql();
	}
	
	@Override
	protected @NonNull SqlRendered renderNow(@NonNull SqlNowFunction<?> function) throws SqlException {
		return SqlRendered.of("datetime('now')");
	}
	
	@Override
	protected @NonNull SqlRendered renderCurrentDate(@NonNull SqlCurrentDateFunction<?> function) throws SqlException {
		return SqlRendered.of("date('now')");
	}
	
	@Override
	protected @NonNull SqlRendered renderCurrentTime(@NonNull SqlCurrentTimeFunction<?> function) throws SqlException {
		return SqlRendered.of("time('now')");
	}
	
	@Override
	protected @NonNull SqlRendered renderCurrentTimestamp(@NonNull SqlCurrentTimestampFunction<?> function) throws SqlException {
		return SqlRendered.of("datetime('now')");
	}
	
	@Override
	protected @NonNull SqlRendered renderTemporalAdd(@NonNull SqlTemporalAddFunction<?> function) throws SqlException {
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.literal("datetime").openingBracket();
		renderer.rendered(function.firstSummand().toSql(this.dialect)).comma();
		renderer.literal("'+' ||").rendered(function.secondSummand().toSql(this.dialect)).literal("|| ' " + toSqliteModifier(function.part().name()) + "'");
		renderer.closingBracket();
		return renderer.toSql();
	}
	
	@Override
	protected @NonNull SqlRendered renderTemporalSubtract(@NonNull SqlTemporalSubtractFunction<?> function) throws SqlException {
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.literal("datetime").openingBracket();
		renderer.rendered(function.minuend().toSql(this.dialect)).comma();
		renderer.literal("'-' ||").rendered(function.subtrahend().toSql(this.dialect)).literal("|| ' " + toSqliteModifier(function.part().name()) + "'");
		renderer.closingBracket();
		return renderer.toSql();
	}
	
	@Override
	protected @NonNull SqlRendered renderToDate(@NonNull SqlToDateFunction<?> function) throws SqlException {
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.literal("date").openingBracket().rendered(function.expression().toSql(this.dialect)).closingBracket();
		return renderer.toSql();
	}
	
	@Override
	protected @NonNull SqlRendered renderToTime(@NonNull SqlToTimeFunction<?> function) throws SqlException {
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.literal("time").openingBracket().rendered(function.expression().toSql(this.dialect)).closingBracket();
		return renderer.toSql();
	}
	
	@Override
	public @NonNull SqlRendered renderInterval(@NonNull SqlExpression<?> duration, @NonNull String part) throws SqlException {
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.rendered(duration.toSql(this.dialect)).literal("|| ' " + toSqliteModifier(part) + "'");
		return renderer.toSql();
	}
}
