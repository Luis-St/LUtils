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
import net.luis.utils.io.database.dialect.rendering.base.SqlRenderingHelper;
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

public class MySqlTemporalFunctionRenderer extends SqlTemporalFunctionRenderer {
	
	public MySqlTemporalFunctionRenderer(@NonNull SqlDialect dialect) {
		super(dialect);
	}
	
	@Override
	protected @NonNull SqlRendered renderFromEpoch(@NonNull SqlFromEpochFunction<?> function) throws SqlException {
		return SqlRenderingHelper.renderFunctionCall(this.dialect, "FROM_UNIXTIME", function.expression());
	}
	
	@Override
	protected @NonNull SqlRendered renderMakeDate(@NonNull SqlMakeDateFunction<?> function) throws SqlException {
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.literal("STR_TO_DATE").openingBracket();
		renderer.literal("CONCAT").openingBracket();
		renderer.rendered(function.year().toSql(this.dialect)).comma().literal("'-'").comma();
		renderer.rendered(function.month().toSql(this.dialect)).comma().literal("'-'").comma();
		renderer.rendered(function.day().toSql(this.dialect));
		renderer.closingBracket().comma().literal("'%Y-%m-%d'").closingBracket();
		return renderer.toSql();
	}
	
	@Override
	protected @NonNull SqlRendered renderMakeTime(@NonNull SqlMakeTimeFunction<?> function) throws SqlException {
		return SqlRenderingHelper.renderFunctionCall(this.dialect, "MAKETIME", function.hour(), function.minute(), function.second());
	}
	
	@Override
	protected @NonNull SqlRendered renderToEpoch(@NonNull SqlToEpochFunction<?> function) throws SqlException {
		return SqlRenderingHelper.renderFunctionCall(this.dialect, "UNIX_TIMESTAMP", function.expression());
	}
	
	@Override
	protected @NonNull SqlRendered renderTemporalAdd(@NonNull SqlTemporalAddFunction<?> function) throws SqlException {
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.literal("DATE_ADD").openingBracket();
		renderer.rendered(function.firstSummand().toSql(this.dialect)).comma();
		renderer.literal("INTERVAL").rendered(function.secondSummand().toSql(this.dialect)).literal(function.part().name());
		renderer.closingBracket();
		return renderer.toSql();
	}
	
	@Override
	protected @NonNull SqlRendered renderTemporalSubtract(@NonNull SqlTemporalSubtractFunction<?> function) throws SqlException {
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.literal("DATE_SUB").openingBracket();
		renderer.rendered(function.minuend().toSql(this.dialect)).comma();
		renderer.literal("INTERVAL").rendered(function.subtrahend().toSql(this.dialect)).literal(function.part().name());
		renderer.closingBracket();
		return renderer.toSql();
	}
	
	@Override
	public @NonNull SqlRendered renderInterval(@NonNull SqlExpression<?> duration, @NonNull String part) throws SqlException {
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.literal("INTERVAL").rendered(duration.toSql(this.dialect)).literal(part);
		return renderer.toSql();
	}
}
