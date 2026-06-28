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

package net.luis.utils.io.database.migration;

import net.luis.utils.io.database.condition.SqlCondition;
import net.luis.utils.io.database.index.SqlIndex;
import net.luis.utils.io.database.index.SqlIndexMethod;
import net.luis.utils.io.database.table.SqlColumn;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Objects;

/**
 * Fluent builder used to define an index during a migration.<br>
 * It collects the columns and options of the index and builds a {@link SqlIndex}.<br>
 *
 * @author Luis-St
 */

public class SqlMigrationIndexBuilder {
	
	/**
	 * The name of the index.
	 */
	private final String name;
	/**
	 * The columns covered by the index.
	 */
	private List<SqlColumn<?, ?>> columns = List.of();
	/**
	 * Whether the index is unique.
	 */
	private boolean unique;
	/**
	 * The index method used by the index.
	 */
	private SqlIndexMethod method = SqlIndexMethod.BTREE;
	/**
	 * The where condition of a partial index, or {@code null} if none.
	 */
	private SqlCondition whereCondition;
	
	/**
	 * Constructs a new sql migration index builder with the given name.<br>
	 *
	 * @param name The name of the index
	 * @throws NullPointerException If the name is null
	 */
	SqlMigrationIndexBuilder(@NonNull String name) {
		this.name = Objects.requireNonNull(name, "Sql index name must not be null");
	}
	
	/**
	 * Sets the columns covered by the index.<br>
	 *
	 * @param columns The columns to index
	 * @return This builder
	 * @throws NullPointerException If the columns array is null
	 * @throws IllegalArgumentException If no columns are given
	 */
	public @NonNull SqlMigrationIndexBuilder columns(SqlColumn<?, ?> @NonNull ... columns) {
		Objects.requireNonNull(columns, "Sql index columns must not be null");
		if (columns.length == 0) {
			throw new IllegalArgumentException("Index must have at least one column");
		}
		
		this.columns = List.of(columns);
		return this;
	}
	
	/**
	 * Declares the index as unique.<br>
	 * @return This builder
	 */
	public @NonNull SqlMigrationIndexBuilder unique() {
		this.unique = true;
		return this;
	}
	
	/**
	 * Sets the index method used by the index.<br>
	 *
	 * @param method The index method
	 * @return This builder
	 * @throws NullPointerException If the method is null
	 */
	public @NonNull SqlMigrationIndexBuilder method(@NonNull SqlIndexMethod method) {
		this.method = Objects.requireNonNull(method, "Sql index method must not be null");
		return this;
	}
	
	/**
	 * Sets the where condition of the index, making it a partial index.<br>
	 *
	 * @param condition The where condition
	 * @return This builder
	 * @throws NullPointerException If the condition is null
	 */
	public @NonNull SqlMigrationIndexBuilder where(@NonNull SqlCondition condition) {
		this.whereCondition = Objects.requireNonNull(condition, "Sql where condition must not be null");
		return this;
	}
	
	/**
	 * Builds the index described by this builder.<br>
	 *
	 * @return The built index
	 * @throws IllegalStateException If no columns have been set
	 */
	@NonNull SqlIndex build() {
		if (this.columns.isEmpty()) {
			throw new IllegalStateException("Index must have at least one column");
		}
		return new SqlIndex(this.name, this.columns, this.unique, this.whereCondition, this.method);
	}
}
