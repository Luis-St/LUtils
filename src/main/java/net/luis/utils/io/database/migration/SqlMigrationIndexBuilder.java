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
 *
 * @author Luis-St
 *
 */

public class SqlMigrationIndexBuilder {
	
	private final String name;
	private List<SqlColumn<?, ?>> columns = List.of();
	private boolean unique;
	private SqlIndexMethod method = SqlIndexMethod.BTREE;
	private SqlCondition whereCondition;
	
	SqlMigrationIndexBuilder(@NonNull String name) {
		this.name = Objects.requireNonNull(name, "Sql index name must not be null");
	}
	
	public @NonNull SqlMigrationIndexBuilder columns(SqlColumn<?, ?> @NonNull ... columns) {
		Objects.requireNonNull(columns, "Sql index columns must not be null");
		if (columns.length == 0) {
			throw new IllegalArgumentException("Index must have at least one column");
		}
		
		this.columns = List.of(columns);
		return this;
	}
	
	public @NonNull SqlMigrationIndexBuilder unique() {
		this.unique = true;
		return this;
	}
	
	public @NonNull SqlMigrationIndexBuilder method(@NonNull SqlIndexMethod method) {
		this.method = Objects.requireNonNull(method, "Sql index method must not be null");
		return this;
	}
	
	public @NonNull SqlMigrationIndexBuilder where(@NonNull SqlCondition condition) {
		this.whereCondition = Objects.requireNonNull(condition, "Sql where condition must not be null");
		return this;
	}
	
	@NonNull SqlIndex build() {
		if (this.columns.isEmpty()) {
			throw new IllegalStateException("Index must have at least one column");
		}
		return new SqlIndex(this.name, this.columns, this.unique, this.whereCondition, this.method);
	}
}
