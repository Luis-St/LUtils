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
import net.luis.utils.io.database.migration.operation.SqlColumnOptions;
import net.luis.utils.io.database.table.SqlTable;
import org.jspecify.annotations.NonNull;

import java.util.Objects;
import java.util.Optional;

/**
 *
 * @author Luis-St
 *
 */

public class SqlMigrationColumnBuilder<C> {
	
	private boolean notNull;
	private boolean unique;
	private boolean autoIncrement;
	private Optional<Object> defaultValue = Optional.empty();
	private SqlTable<?> referencesTable;
	private SqlCondition check;
	
	SqlMigrationColumnBuilder() {}
	
	public @NonNull SqlMigrationColumnBuilder<C> notNull() {
		this.notNull = true;
		return this;
	}
	
	public @NonNull SqlMigrationColumnBuilder<C> unique() {
		this.unique = true;
		return this;
	}
	
	public @NonNull SqlMigrationColumnBuilder<C> autoIncrement() {
		this.autoIncrement = true;
		return this;
	}
	
	public @NonNull SqlMigrationColumnBuilder<C> defaultValue(@NonNull C value) {
		Objects.requireNonNull(value, "Sql default value must not be null");
		
		this.defaultValue = Optional.of(value);
		return this;
	}
	
	public @NonNull SqlMigrationColumnBuilder<C> references(@NonNull SqlTable<?> table) {
		this.referencesTable = Objects.requireNonNull(table, "Sql referenced table must not be null");
		return this;
	}
	
	public @NonNull SqlMigrationColumnBuilder<C> check(@NonNull SqlCondition condition) {
		this.check = Objects.requireNonNull(condition, "Sql check condition must not be null");
		return this;
	}
	
	SqlColumnOptions build() {
		return new SqlColumnOptions(this.notNull, this.unique, this.autoIncrement, this.defaultValue, this.referencesTable, this.check);
	}
}
