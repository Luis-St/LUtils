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

package net.luis.utils.io.database.query.crud;

import net.luis.utils.io.database.audit.SqlAuditUserProvider;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.exception.client.SqlStatementBuilderException;
import net.luis.utils.io.database.query.util.SqlColumnValue;
import net.luis.utils.io.database.table.SqlColumn;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;

/**
 * A compile-time checked, two-column insert builder that is not driven by an entity.<br>
 * Every row added through {@link #row} is checked against the declared columns' types at compile time.<br>
 * The builder is immutable, every {@link #row} call returns a new instance leaving this builder unchanged.<br>
 *
 * @see SqlInsertColumnsBuilder
 *
 * @author Luis-St
 *
 * @param <E> The type of the entity mapped from a row of the table
 * @param <T1> The type of the first column
 * @param <T2> The type of the second column
 */
public final class SqlInsertColumns2<E, T1, T2> {
	
	/**
	 * The underlying untyped column-value insert builder.
	 */
	private final SqlInsertColumnsBuilder<E> builder;
	/**
	 * The first column of every row.
	 */
	private final SqlColumn<E, T1> column1;
	/**
	 * The second column of every row.
	 */
	private final SqlColumn<E, T2> column2;
	
	/**
	 * Constructs a new two-column insert builder.<br>
	 *
	 * @param builder The underlying untyped column-value insert builder
	 * @param column1 The first column of every row
	 * @param column2 The second column of every row
	 * @throws NullPointerException If the builder or a column is null
	 */
	SqlInsertColumns2(@NonNull SqlInsertColumnsBuilder<E> builder, @NonNull SqlColumn<E, T1> column1, @NonNull SqlColumn<E, T2> column2) {
		this.builder = Objects.requireNonNull(builder, "Sql insert columns builder must not be null");
		this.column1 = Objects.requireNonNull(column1, "Sql column must not be null");
		this.column2 = Objects.requireNonNull(column2, "Sql column must not be null");
	}
	
	/**
	 * Creates a copy of this builder using the given audit user provider.<br>
	 *
	 * @param auditUserProvider The provider that resolves the audit user, or {@code null} for no audit user
	 * @return A new builder using the given audit user provider
	 */
	public @NonNull SqlInsertColumns2<E, T1, T2> withAuditUser(@Nullable SqlAuditUserProvider auditUserProvider) {
		return new SqlInsertColumns2<>(this.builder.withAuditUser(auditUserProvider), this.column1, this.column2);
	}
	
	/**
	 * Creates a copy of this builder with a row of the given values appended.<br>
	 *
	 * @param value1 The value of the first column
	 * @param value2 The value of the second column
	 * @return A new builder with the additional row
	 * @throws SqlStatementBuilderException If a previously added row specified different columns, which cannot happen for this arity
	 */
	public @NonNull SqlInsertColumns2<E, T1, T2> row(@NonNull T1 value1, @NonNull T2 value2) throws SqlStatementBuilderException {
		SqlInsertColumnsBuilder<E> next = this.builder.row(List.of(
			SqlColumnValue.of(this.column1, value1),
			SqlColumnValue.of(this.column2, value2)
		));
		return new SqlInsertColumns2<>(next, this.column1, this.column2);
	}
	
	/**
	 * Executes this insert query and returns the number of affected rows.<br>
	 *
	 * @return The number of affected rows
	 * @throws SqlException If an error occurs while executing the query
	 */
	public int execute() throws SqlException {
		return this.builder.execute();
	}
	
	/**
	 * Executes this insert query and returns the generated keys of the inserted rows.<br>
	 *
	 * @return The list of generated keys
	 * @throws SqlException If an error occurs while executing the query
	 */
	public @NonNull List<Long> executeReturningKeys() throws SqlException {
		return this.builder.executeReturningKeys();
	}
	
	/**
	 * Executes this insert query and returns the inserted rows mapped back into entities.<br>
	 *
	 * @return The list of inserted entities
	 * @throws SqlException If an error occurs while executing the query
	 */
	public @NonNull List<E> returning() throws SqlException {
		return this.builder.returning();
	}
}
