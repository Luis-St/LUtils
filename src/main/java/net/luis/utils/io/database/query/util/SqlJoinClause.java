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
 * Represents a single join clause of a sql query.<br>
 * A join clause either joins a {@link SqlTable table} on a condition or a lateral {@link SqlSelectQuery subquery}.<br>
 * The {@link SqlJoinType join type} determines which rows of the joined tables are included in the result.<br>
 *
 * @see SqlJoinType
 *
 * @author Luis-St
 */
public class SqlJoinClause implements SqlRenderable {
	
	/**
	 * The type of the join.
	 */
	private final SqlJoinType type;
	/**
	 * The table to join, or {@code null} if a lateral subquery is joined.
	 */
	private final @Nullable SqlTable<?> table;
	/**
	 * The condition to join on, or {@code null} for a cross join or a lateral subquery.
	 */
	private final @Nullable SqlCondition on;
	/**
	 * The lateral subquery to join, or {@code null} if a table is joined.
	 */
	private final @Nullable SqlSelectQuery<?> lateralSubquery;
	/**
	 * The alias of the lateral subquery, or {@code null} if a table is joined.
	 */
	private final @Nullable SqlAlias lateralAlias;
	
	/**
	 * Constructs a new sql join clause that joins the given table.<br>
	 * The condition is ignored and set to {@code null} if the type is {@link SqlJoinType#CROSS}.<br>
	 *
	 * @param type The type of the join
	 * @param table The table to join
	 * @param on The condition to join on, may be null for a cross join
	 * @throws NullPointerException If the type or the table is null, or if the condition is null and the type is not {@link SqlJoinType#CROSS}
	 */
	public SqlJoinClause(@NonNull SqlJoinType type, @NonNull SqlTable<?> table, @Nullable SqlCondition on) {
		this.type = Objects.requireNonNull(type, "Sql join type must not be null");
		this.table = Objects.requireNonNull(table, "Sql join table must not be null");
		this.on = type == SqlJoinType.CROSS ? null : Objects.requireNonNull(on, "Sql join condition must not be null");
		this.lateralSubquery = null;
		this.lateralAlias = null;
	}
	
	/**
	 * Constructs a new sql join clause that cross joins the given lateral subquery.<br>
	 *
	 * @param lateralSubquery The lateral subquery to join
	 * @param lateralAlias The alias of the lateral subquery
	 * @throws NullPointerException If the lateral subquery or the lateral alias is null
	 */
	public SqlJoinClause(@NonNull SqlSelectQuery<?> lateralSubquery, @NonNull SqlAlias lateralAlias) {
		this(SqlJoinType.CROSS, lateralSubquery, lateralAlias);
	}
	
	/**
	 * Constructs a new sql join clause that joins the given lateral subquery.<br>
	 *
	 * @param type The type of the join
	 * @param lateralSubquery The lateral subquery to join
	 * @param lateralAlias The alias of the lateral subquery
	 * @throws NullPointerException If the type, the lateral subquery or the lateral alias is null
	 */
	public SqlJoinClause(@NonNull SqlJoinType type, @NonNull SqlSelectQuery<?> lateralSubquery, @NonNull SqlAlias lateralAlias) {
		this.type = Objects.requireNonNull(type, "Sql join type must not be null");
		this.table = null;
		this.on = null;
		this.lateralSubquery = Objects.requireNonNull(lateralSubquery, "Sql lateral subquery must not be null");
		this.lateralAlias = Objects.requireNonNull(lateralAlias, "Sql lateral alias must not be null");
	}
	
	/**
	 * Returns the type of this join.<br>
	 * @return The join type
	 */
	public @NonNull SqlJoinType type() {
		return this.type;
	}
	
	/**
	 * Returns the table that is joined.<br>
	 * @return The joined table, or {@code null} if a lateral subquery is joined
	 */
	public @Nullable SqlTable<?> table() {
		return this.table;
	}
	
	/**
	 * Returns the condition this join is joined on.<br>
	 * @return The join condition, or {@code null} for a cross join or a lateral subquery
	 */
	public @Nullable SqlCondition on() {
		return this.on;
	}
	
	/**
	 * Checks whether this join joins a lateral subquery.<br>
	 * @return {@code true} if a lateral subquery is joined, {@code false} if a table is joined
	 */
	public boolean isLateral() {
		return this.lateralSubquery != null;
	}
	
	/**
	 * Returns the lateral subquery that is joined.<br>
	 * @return The lateral subquery, or {@code null} if a table is joined
	 */
	public @Nullable SqlSelectQuery<?> lateralSubquery() {
		return this.lateralSubquery;
	}
	
	/**
	 * Returns the alias of the joined lateral subquery.<br>
	 * @return The lateral alias, or {@code null} if a table is joined
	 */
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
			renderer.literal(dialect.quoteIdentifier(this.table.name()));
			if (this.on != null) {
				renderer.on().rendered(dialect.renderCondition(this.on));
			}
		}
		return renderer.toSql();
	}
}
