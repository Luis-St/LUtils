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

package net.luis.utils.io.database.query.util;

import net.luis.utils.io.database.condition.SqlCondition;
import net.luis.utils.io.database.dialect.SqlDialect;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.query.SqlAlias;
import net.luis.utils.io.database.query.crud.SqlSelectQuery;
import net.luis.utils.io.database.rendering.*;
import net.luis.utils.io.database.table.SqlTable;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

/**
 *
 * @author Luis-St
 *
 */

public class SqlJoinClause implements SqlRenderable {
	
	private final SqlJoinType type;
	private final @Nullable SqlTable<?> table;
	private final @Nullable SqlCondition on;
	private final @Nullable SqlSelectQuery<?> lateralSubquery;
	private final @Nullable SqlAlias lateralAlias;
	
	public SqlJoinClause(@NonNull SqlJoinType type, @NonNull SqlTable<?> table, @Nullable SqlCondition on) {
		this.type = Objects.requireNonNull(type, "Sql join type must not be null");
		this.table = Objects.requireNonNull(table, "Sql join table must not be null");
		this.on = type == SqlJoinType.CROSS ? null : Objects.requireNonNull(on, "Sql join condition must not be null");
		this.lateralSubquery = null;
		this.lateralAlias = null;
	}
	
	public SqlJoinClause(@NonNull SqlSelectQuery<?> lateralSubquery, @NonNull SqlAlias lateralAlias) {
		this(SqlJoinType.CROSS, lateralSubquery, lateralAlias);
	}
	
	public SqlJoinClause(@NonNull SqlJoinType type, @NonNull SqlSelectQuery<?> lateralSubquery, @NonNull SqlAlias lateralAlias) {
		this.type = Objects.requireNonNull(type, "Sql join type must not be null");
		this.table = null;
		this.on = null;
		this.lateralSubquery = Objects.requireNonNull(lateralSubquery, "Sql lateral subquery must not be null");
		this.lateralAlias = Objects.requireNonNull(lateralAlias, "Sql lateral alias must not be null");
	}
	
	public @NonNull SqlJoinType type() {
		return this.type;
	}
	
	public @Nullable SqlTable<?> table() {
		return this.table;
	}
	
	public @Nullable SqlCondition on() {
		return this.on;
	}
	
	public boolean isLateral() {
		return this.lateralSubquery != null;
	}
	
	public @Nullable SqlSelectQuery<?> lateralSubquery() {
		return this.lateralSubquery;
	}
	
	public @Nullable SqlAlias lateralAlias() {
		return this.lateralAlias;
	}
	
	@Override
	public @NonNull SqlRendered toSql(@NonNull SqlDialect dialect) throws SqlException {
		Objects.requireNonNull(dialect, "Sql dialect must not be null");
		SqlRenderer renderer = SqlRenderer.empty();
		
		switch (this.type) {
			case INNER -> renderer.inner().join();
			case LEFT -> renderer.left().join();
			case RIGHT -> renderer.right().join();
			case FULL -> renderer.full().outer().join();
			case CROSS -> renderer.cross().join();
		}
		
		if (this.lateralSubquery != null && this.lateralAlias != null) {
			renderer.lateral().openingBracket().rendered(this.lateralSubquery.toSql(dialect)).closingBracket().literal("AS").literal(dialect.quoteIdentifier(this.lateralAlias.get()));
		} else if (this.table != null) {
			renderer.literal(dialect.quoteIdentifier(this.table.getName()));
			if (this.on != null) {
				renderer.on().rendered(dialect.renderCondition(this.on));
			}
		}
		return renderer.toSql();
	}
}
