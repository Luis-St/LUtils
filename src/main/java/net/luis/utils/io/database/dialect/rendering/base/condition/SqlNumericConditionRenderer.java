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

package net.luis.utils.io.database.dialect.rendering.base.condition;

import net.luis.utils.io.database.condition.conditions.SqlNumericCondition;
import net.luis.utils.io.database.condition.conditions.numeric.*;
import net.luis.utils.io.database.dialect.SqlDialect;
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

public class SqlNumericConditionRenderer {
	
	protected final SqlDialect dialect;
	
	public SqlNumericConditionRenderer(@NonNull SqlDialect dialect) {
		this.dialect = Objects.requireNonNull(dialect, "Sql dialect must not be null");
	}
	
	public @NonNull SqlRendered render(@NonNull SqlNumericCondition condition) throws SqlException {
		return switch (condition) {
			case SqlIsNegativeCondition cond -> this.renderIsNegative(cond);
			case SqlIsPositiveCondition cond -> this.renderIsPositive(cond);
			case SqlIsZeroCondition cond -> this.renderIsZero(cond);
			case SqlModEqualsCondition cond -> this.renderModEquals(cond);
			
			case null -> throw new NullPointerException("Sql numeric condition must not be null");
			default -> throw new SqlDialectUnsupportedRenderingException("Unknown sql numeric condition type: " + condition.getClass().getName() + " in dialect " + this.dialect.name());
		};
	}
	
	protected @NonNull SqlRendered renderIsNegative(@NonNull SqlIsNegativeCondition condition) throws SqlException {
		return SqlRenderer.empty().rendered(condition.value().toSql(this.dialect)).literal("<").literal("0").toSql();
	}
	
	protected @NonNull SqlRendered renderIsPositive(@NonNull SqlIsPositiveCondition condition) throws SqlException {
		return SqlRenderer.empty().rendered(condition.value().toSql(this.dialect)).literal(">").literal("0").toSql();
	}
	
	protected @NonNull SqlRendered renderIsZero(@NonNull SqlIsZeroCondition condition) throws SqlException {
		return SqlRenderer.empty().rendered(condition.value().toSql(this.dialect)).literal("=").literal("0").toSql();
	}
	
	protected @NonNull SqlRendered renderModEquals(@NonNull SqlModEqualsCondition condition) throws SqlException {
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.literal("MOD").openingBracket().rendered(condition.value().toSql(this.dialect)).comma().rendered(condition.divisor().toSql(this.dialect)).closingBracket();
		renderer.literal("=").rendered(condition.remainder().toSql(this.dialect));
		return renderer.toSql();
	}
}
