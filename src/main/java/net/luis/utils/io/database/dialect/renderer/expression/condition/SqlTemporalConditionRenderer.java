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

import net.luis.utils.io.database.condition.conditions.SqlTemporalCondition;
import net.luis.utils.io.database.condition.conditions.temporal.*;
import net.luis.utils.io.database.dialect.SqlDialect;
import net.luis.utils.io.database.dialect.renderer.SqlRenderingHelper;
import net.luis.utils.io.database.dialect.renderer.expression.function.SqlTemporalFunctionRenderer;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.exception.dialect.SqlDialectUnsupportedRenderingException;
import net.luis.utils.io.database.rendering.SqlRendered;
import net.luis.utils.io.database.rendering.SqlRenderer;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

/**
 *
 * @author Luis-St
 *
 */

public class SqlTemporalConditionRenderer {
	
	private final SqlTemporalFunctionRenderer temporalRenderer;
	protected final SqlDialect dialect;
	
	public SqlTemporalConditionRenderer(@NonNull SqlDialect dialect, @NonNull SqlTemporalFunctionRenderer temporalRenderer) {
		this.dialect = Objects.requireNonNull(dialect, "Sql dialect must not be null");
		this.temporalRenderer = Objects.requireNonNull(temporalRenderer, "Sql temporal function renderer must not be null");
	}
	
	public @NonNull SqlRendered render(@NonNull SqlTemporalCondition condition) throws SqlException {
		return switch (condition) {
			case SqlAfterCondition cond -> this.renderAfter(cond);
			case SqlBeforeCondition cond -> this.renderBefore(cond);
			case SqlWithinLastCondition cond -> this.renderWithinLast(cond);
			case SqlWithinNextCondition cond -> this.renderWithinNext(cond);
			
			case null -> throw new NullPointerException("Sql temporal condition must not be null");
			default -> throw new SqlDialectUnsupportedRenderingException("Unknown sql temporal condition type: " + condition.getClass().getName() + " in dialect " + this.dialect.name());
		};
	}
	
	protected @NonNull SqlRendered renderAfter(@NonNull SqlAfterCondition condition) throws SqlException {
		Objects.requireNonNull(condition, "Sql condition must not be null");
		
		return SqlRenderingHelper.renderInfix(this.dialect, condition.value(), ">", condition.earlierBound());
	}
	
	protected @NonNull SqlRendered renderBefore(@NonNull SqlBeforeCondition condition) throws SqlException {
		Objects.requireNonNull(condition, "Sql condition must not be null");
		
		return SqlRenderingHelper.renderInfix(this.dialect, condition.value(), "<", condition.laterBound());
	}
	
	protected @NonNull SqlRendered renderWithinLast(@NonNull SqlWithinLastCondition condition) throws SqlException {
		Objects.requireNonNull(condition, "Sql condition must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.rendered(condition.value().toSql(this.dialect));
		renderer.literal(">=").openingBracket().keyword("CURRENT_TIMESTAMP").literal("-");
		renderer.rendered(this.temporalRenderer.renderInterval(condition.duration(), "SECOND"));
		renderer.closingBracket();
		return renderer.toSql();
	}
	
	protected @NonNull SqlRendered renderWithinNext(@NonNull SqlWithinNextCondition condition) throws SqlException {
		Objects.requireNonNull(condition, "Sql condition must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.rendered(condition.value().toSql(this.dialect));
		renderer.literal("<=").openingBracket().keyword("CURRENT_TIMESTAMP").literal("+");
		renderer.rendered(this.temporalRenderer.renderInterval(condition.duration(), "SECOND"));
		renderer.closingBracket();
		return renderer.toSql();
	}
}
