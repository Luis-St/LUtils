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
 * Fluent builder used to configure the options of a column definition during a migration.<br>
 * It collects flags and constraints that are turned into {@link SqlColumnOptions}.<br>
 *
 * @author Luis-St
 *
 * @param <C> The value type of the column
 */

public class SqlMigrationColumnBuilder<C> {
	
	/**
	 * Whether the column is declared as not null.
	 */
	private boolean notNull;
	/**
	 * Whether the column is declared as unique.
	 */
	private boolean unique;
	/**
	 * Whether the column is declared as auto-incrementing.
	 */
	private boolean autoIncrement;
	/**
	 * The default value of the column, or empty if none is set.
	 */
	private Optional<Object> defaultValue = Optional.empty();
	/**
	 * The table referenced by this column, or {@code null} if none.
	 */
	private SqlTable<?> referencesTable;
	/**
	 * The check condition of the column, or {@code null} if none.
	 */
	private SqlCondition check;
	
	/**
	 * Constructs a new sql migration column builder.<br>
	 */
	SqlMigrationColumnBuilder() {}
	
	/**
	 * Declares the column as not null.<br>
	 * @return This builder
	 */
	public @NonNull SqlMigrationColumnBuilder<C> notNull() {
		this.notNull = true;
		return this;
	}
	
	/**
	 * Declares the column as unique.<br>
	 * @return This builder
	 */
	public @NonNull SqlMigrationColumnBuilder<C> unique() {
		this.unique = true;
		return this;
	}
	
	/**
	 * Declares the column as auto-incrementing.<br>
	 * @return This builder
	 */
	public @NonNull SqlMigrationColumnBuilder<C> autoIncrement() {
		this.autoIncrement = true;
		return this;
	}
	
	/**
	 * Sets the default value of the column.<br>
	 *
	 * @param value The default value
	 * @return This builder
	 * @throws NullPointerException If the value is null
	 */
	public @NonNull SqlMigrationColumnBuilder<C> defaultValue(@NonNull C value) {
		Objects.requireNonNull(value, "Sql default value must not be null");
		
		this.defaultValue = Optional.of(value);
		return this;
	}
	
	/**
	 * Declares the column as referencing the given table.<br>
	 *
	 * @param table The referenced table
	 * @return This builder
	 * @throws NullPointerException If the table is null
	 */
	public @NonNull SqlMigrationColumnBuilder<C> references(@NonNull SqlTable<?> table) {
		this.referencesTable = Objects.requireNonNull(table, "Sql referenced table must not be null");
		return this;
	}
	
	/**
	 * Sets the check condition of the column.<br>
	 *
	 * @param condition The check condition
	 * @return This builder
	 * @throws NullPointerException If the condition is null
	 */
	public @NonNull SqlMigrationColumnBuilder<C> check(@NonNull SqlCondition condition) {
		this.check = Objects.requireNonNull(condition, "Sql check condition must not be null");
		return this;
	}
	
	/**
	 * Builds the column options described by this builder.<br>
	 * @return The built column options
	 */
	SqlColumnOptions build() {
		return new SqlColumnOptions(this.notNull, this.unique, this.autoIncrement, this.defaultValue, this.referencesTable, this.check);
	}
}
