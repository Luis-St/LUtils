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

import net.luis.utils.io.database.condition.conditions.SqlComparisonCondition;
import net.luis.utils.io.database.condition.conditions.comparison.*;
import net.luis.utils.io.database.dialect.SqlDialect;
import net.luis.utils.io.database.dialect.renderer.SqlRenderingHelper;
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

public class SqlComparisonConditionRenderer {
	
	protected final SqlDialect dialect;
	
	public SqlComparisonConditionRenderer(@NonNull SqlDialect dialect) {
		this.dialect = Objects.requireNonNull(dialect, "Sql dialect must not be null");
	}
	
	public @NonNull SqlRendered render(@NonNull SqlComparisonCondition condition) throws SqlException {
		return switch (condition) {
			case SqlBetweenCondition cond -> this.renderBetween(cond);
			case SqlEqualToCondition cond -> this.renderEqualTo(cond);
			case SqlGreaterThanCondition cond -> this.renderGreaterThan(cond);
			case SqlInListCondition cond -> this.renderInList(cond);
			case SqlInQueryCondition cond -> this.renderInQuery(cond);
			case SqlIsDistinctFromCondition cond -> this.renderIsDistinctFrom(cond);
			case SqlIsNullCondition cond -> this.renderIsNull(cond);
			case SqlLessThanCondition cond -> this.renderLessThan(cond);
			
			case null -> throw new NullPointerException("Sql comparison condition must not be null");
			default -> throw new SqlDialectUnsupportedRenderingException("Unknown sql comparison condition type: " + condition.getClass().getName() + " in dialect " + this.dialect.name());
		};
	}
	
	protected @NonNull SqlRendered renderBetween(@NonNull SqlBetweenCondition condition) throws SqlException {
		Objects.requireNonNull(condition, "Sql condition must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.rendered(condition.value().toSql(this.dialect)).between().rendered(condition.lower().toSql(this.dialect)).and().rendered(condition.upper().toSql(this.dialect));
		return renderer.toSql();
	}
	
	protected @NonNull SqlRendered renderEqualTo(@NonNull SqlEqualToCondition condition) throws SqlException {
		Objects.requireNonNull(condition, "Sql condition must not be null");
		
		return SqlRenderingHelper.renderInfix(this.dialect, condition.first(), "=", condition.second());
	}
	
	protected @NonNull SqlRendered renderGreaterThan(@NonNull SqlGreaterThanCondition condition) throws SqlException {
		Objects.requireNonNull(condition, "Sql condition must not be null");
		
		return SqlRenderingHelper.renderInfix(this.dialect, condition.value(), condition.equalTo() ? ">=" : ">", condition.threshold());
	}
	
	protected @NonNull SqlRendered renderInList(@NonNull SqlInListCondition condition) throws SqlException {
		Objects.requireNonNull(condition, "Sql condition must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.rendered(condition.value().toSql(this.dialect)).in().openingBracket();
		SqlRenderingHelper.renderList(renderer, condition.options(), (r, item) -> r.rendered(item.toSql(this.dialect)));
		renderer.closingBracket();
		return renderer.toSql();
	}
	
	protected @NonNull SqlRendered renderInQuery(@NonNull SqlInQueryCondition condition) throws SqlException {
		Objects.requireNonNull(condition, "Sql condition must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.rendered(condition.value().toSql(this.dialect)).in().openingBracket().rendered(condition.query().toSql(this.dialect)).closingBracket();
		return renderer.toSql();
	}
	
	protected @NonNull SqlRendered renderIsDistinctFrom(@NonNull SqlIsDistinctFromCondition condition) throws SqlException {
		Objects.requireNonNull(condition, "Sql condition must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.rendered(condition.first().toSql(this.dialect)).is().keyword("DISTINCT").from().rendered(condition.second().toSql(this.dialect));
		return renderer.toSql();
	}
	
	protected @NonNull SqlRendered renderIsNull(@NonNull SqlIsNullCondition condition) throws SqlException {
		Objects.requireNonNull(condition, "Sql condition must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.rendered(condition.value().toSql(this.dialect)).is().null_();
		return renderer.toSql();
	}
	
	protected @NonNull SqlRendered renderLessThan(@NonNull SqlLessThanCondition condition) throws SqlException {
		Objects.requireNonNull(condition, "Sql condition must not be null");
		
		return SqlRenderingHelper.renderInfix(this.dialect, condition.value(), condition.equalTo() ? "<=" : "<", condition.threshold());
	}
}
