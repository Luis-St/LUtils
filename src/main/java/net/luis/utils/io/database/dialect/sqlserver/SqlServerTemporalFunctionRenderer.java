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
import net.luis.utils.io.database.dialect.base.function.SqlTemporalFunctionRenderer;
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

public class SqlServerTemporalFunctionRenderer extends SqlTemporalFunctionRenderer {
	
	public SqlServerTemporalFunctionRenderer(@NonNull SqlDialect dialect) {
		super(dialect);
	}
	
	@Override
	protected @NonNull SqlRendered renderFromEpoch(@NonNull SqlFromEpochFunction<?> function) throws SqlException {
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.literal("DATEADD").openingBracket();
		renderer.literal("SECOND").comma().rendered(function.expression().toSql(this.dialect));
		renderer.comma().literal("'1970-01-01'");
		renderer.closingBracket();
		return renderer.toSql();
	}
	
	@Override
	protected @NonNull SqlRendered renderToEpoch(@NonNull SqlToEpochFunction<?> function) throws SqlException {
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.literal("DATEDIFF").openingBracket();
		renderer.literal("SECOND").comma().literal("'1970-01-01'").comma();
		renderer.rendered(function.expression().toSql(this.dialect));
		renderer.closingBracket();
		return renderer.toSql();
	}
	
	@Override
	protected @NonNull SqlRendered renderTemporalAdd(@NonNull SqlTemporalAddFunction<?> function) throws SqlException {
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.literal("DATEADD").openingBracket();
		renderer.literal(function.part().name()).comma();
		renderer.rendered(function.secondSummand().toSql(this.dialect)).comma();
		renderer.rendered(function.firstSummand().toSql(this.dialect));
		renderer.closingBracket();
		return renderer.toSql();
	}
	
	@Override
	protected @NonNull SqlRendered renderTemporalSubtract(@NonNull SqlTemporalSubtractFunction<?> function) throws SqlException {
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.literal("DATEADD").openingBracket();
		renderer.literal(function.part().name()).comma();
		renderer.literal("-").openingBracket().rendered(function.subtrahend().toSql(this.dialect)).closingBracket().comma();
		renderer.rendered(function.minuend().toSql(this.dialect));
		renderer.closingBracket();
		return renderer.toSql();
	}
	
	@Override
	protected @NonNull SqlRendered renderNow(@NonNull SqlNowFunction<?> function) throws SqlException {
		return SqlRendered.of("GETDATE()");
	}
	
	@Override
	protected @NonNull SqlRendered renderCurrentTimestamp(@NonNull SqlCurrentTimestampFunction<?> function) throws SqlException {
		return SqlRendered.of("GETDATE()");
	}
	
	@Override
	public @NonNull SqlRendered renderInterval(@NonNull SqlExpression<?> duration, @NonNull String part) throws SqlException {
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.rendered(duration.toSql(this.dialect));
		return renderer.toSql();
	}
	
	@Override
	protected @NonNull SqlRendered renderTemporalTruncate(@NonNull SqlTemporalTruncateFunction<?> function) throws SqlException {
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.literal("DATETRUNC").openingBracket();
		renderer.literal(function.part().name()).comma();
		renderer.rendered(function.expression().toSql(this.dialect));
		renderer.closingBracket();
		return renderer.toSql();
	}
}
