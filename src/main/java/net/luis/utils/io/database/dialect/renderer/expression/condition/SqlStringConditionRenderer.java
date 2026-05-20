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

import net.luis.utils.io.database.condition.conditions.SqlStringCondition;
import net.luis.utils.io.database.condition.conditions.string.*;
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

public class SqlStringConditionRenderer {
	
	protected final SqlDialect dialect;
	
	public SqlStringConditionRenderer(@NonNull SqlDialect dialect) {
		this.dialect = Objects.requireNonNull(dialect, "Sql dialect must not be null");
	}
	
	public @NonNull SqlRendered render(@NonNull SqlStringCondition condition) throws SqlException {
		return switch (condition) {
			case SqlContainsCondition cond -> this.renderContains(cond);
			case SqlEndsWithCondition cond -> this.renderEndsWith(cond);
			case SqlEqualsIgnoreCaseCondition cond -> this.renderEqualsIgnoreCase(cond);
			case SqlLikeCondition cond -> this.renderLike(cond);
			case SqlStartsWithCondition cond -> this.renderStartsWith(cond);
			
			case null -> throw new NullPointerException("Sql string condition must not be null");
			default -> throw new SqlDialectUnsupportedRenderingException("Unknown sql string condition type: " + condition.getClass().getName() + " in dialect " + this.dialect.name());
		};
	}
	
	protected @NonNull SqlRendered renderConcatExpression(@NonNull SqlRenderer renderer, @NonNull SqlRendered left, @NonNull SqlRendered right) throws SqlException {
		Objects.requireNonNull(renderer, "Sql renderer must not be null");
		Objects.requireNonNull(left, "Left sql rendered must not be null");
		Objects.requireNonNull(right, "Right sql rendered must not be null");
		
		return renderer.rendered(left).literal("||").rendered(right).toSql();
	}
	
	protected @NonNull SqlRendered renderContains(@NonNull SqlContainsCondition condition) throws SqlException {
		Objects.requireNonNull(condition, "Sql condition must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.rendered(condition.value().toSql(this.dialect)).like();
		return this.renderConcatExpression(renderer, this.renderConcatExpression(SqlRenderer.empty(), SqlRendered.of("'%'"), condition.substring().toSql(this.dialect)), SqlRendered.of("'%'"));
	}
	
	protected @NonNull SqlRendered renderEndsWith(@NonNull SqlEndsWithCondition condition) throws SqlException {
		Objects.requireNonNull(condition, "Sql condition must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.rendered(condition.value().toSql(this.dialect)).like();
		return this.renderConcatExpression(renderer, SqlRendered.of("'%'"), condition.suffix().toSql(this.dialect));
	}
	
	protected @NonNull SqlRendered renderEqualsIgnoreCase(@NonNull SqlEqualsIgnoreCaseCondition condition) throws SqlException {
		Objects.requireNonNull(condition, "Sql condition must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.literal("UPPER").openingBracket().rendered(condition.first().toSql(this.dialect)).closingBracket();
		renderer.literal("=");
		renderer.literal("UPPER").openingBracket().rendered(condition.second().toSql(this.dialect)).closingBracket();
		return renderer.toSql();
	}
	
	protected @NonNull SqlRendered renderLike(@NonNull SqlLikeCondition condition) throws SqlException {
		Objects.requireNonNull(condition, "Sql condition must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.rendered(condition.value().toSql(this.dialect)).like().rendered(condition.pattern().toSql(this.dialect));
		return renderer.toSql();
	}
	
	protected @NonNull SqlRendered renderStartsWith(@NonNull SqlStartsWithCondition condition) throws SqlException {
		Objects.requireNonNull(condition, "Sql condition must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.rendered(condition.value().toSql(this.dialect)).like();
		return this.renderConcatExpression(renderer, condition.prefix().toSql(this.dialect), SqlRendered.of("'%'"));
	}
}
