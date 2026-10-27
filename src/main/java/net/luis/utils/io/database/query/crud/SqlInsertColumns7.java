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
 * A compile-time checked, seventh-column insert builder that is not driven by an entity.<br>
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
 * @param <T3> The type of the third column
 * @param <T4> The type of the fourth column
 * @param <T5> The type of the fifth column
 * @param <T6> The type of the sixth column
 * @param <T7> The type of the seventh column
 */
public final class SqlInsertColumns7<E, T1, T2, T3, T4, T5, T6, T7> {
	
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
	 * The third column of every row.
	 */
	private final SqlColumn<E, T3> column3;
	/**
	 * The fourth column of every row.
	 */
	private final SqlColumn<E, T4> column4;
	/**
	 * The fifth column of every row.
	 */
	private final SqlColumn<E, T5> column5;
	/**
	 * The sixth column of every row.
	 */
	private final SqlColumn<E, T6> column6;
	/**
	 * The seventh column of every row.
	 */
	private final SqlColumn<E, T7> column7;
	
	/**
	 * Constructs a new seventh-column insert builder.<br>
	 *
	 * @param builder The underlying untyped column-value insert builder
	 * @param column1 The first column of every row
	 * @param column2 The second column of every row
	 * @param column3 The third column of every row
	 * @param column4 The fourth column of every row
	 * @param column5 The fifth column of every row
	 * @param column6 The sixth column of every row
	 * @param column7 The seventh column of every row
	 * @throws NullPointerException If the builder or a column is null
	 */
	SqlInsertColumns7(
		@NonNull SqlInsertColumnsBuilder<E> builder,
		@NonNull SqlColumn<E, T1> column1,
		@NonNull SqlColumn<E, T2> column2,
		@NonNull SqlColumn<E, T3> column3,
		@NonNull SqlColumn<E, T4> column4,
		@NonNull SqlColumn<E, T5> column5,
		@NonNull SqlColumn<E, T6> column6,
		@NonNull SqlColumn<E, T7> column7
	) {
		this.builder = Objects.requireNonNull(builder, "Sql insert columns builder must not be null");
		this.column1 = Objects.requireNonNull(column1, "Sql column must not be null");
		this.column2 = Objects.requireNonNull(column2, "Sql column must not be null");
		this.column3 = Objects.requireNonNull(column3, "Sql column must not be null");
		this.column4 = Objects.requireNonNull(column4, "Sql column must not be null");
		this.column5 = Objects.requireNonNull(column5, "Sql column must not be null");
		this.column6 = Objects.requireNonNull(column6, "Sql column must not be null");
		this.column7 = Objects.requireNonNull(column7, "Sql column must not be null");
	}
	
	/**
	 * Creates a copy of this builder using the given audit user provider.<br>
	 *
	 * @param auditUserProvider The provider that resolves the audit user, or {@code null} for no audit user
	 * @return A new builder using the given audit user provider
	 */
	public @NonNull SqlInsertColumns7<E, T1, T2, T3, T4, T5, T6, T7> withAuditUser(@Nullable SqlAuditUserProvider auditUserProvider) {
		return new SqlInsertColumns7<>(this.builder.withAuditUser(auditUserProvider), this.column1, this.column2, this.column3, this.column4, this.column5, this.column6, this.column7);
	}
	
	/**
	 * Creates a copy of this builder with a row of the given values appended.<br>
	 *
	 * @param value1 The value of the first column
	 * @param value2 The value of the second column
	 * @param value3 The value of the third column
	 * @param value4 The value of the fourth column
	 * @param value5 The value of the fifth column
	 * @param value6 The value of the sixth column
	 * @param value7 The value of the seventh column
	 * @return A new builder with the additional row
	 * @throws SqlStatementBuilderException If a previously added row specified different columns, which cannot happen for this arity
	 */
	public @NonNull SqlInsertColumns7<E, T1, T2, T3, T4, T5, T6, T7> row(@NonNull T1 value1, @NonNull T2 value2, @NonNull T3 value3, @NonNull T4 value4, @NonNull T5 value5, @NonNull T6 value6, @NonNull T7 value7) throws SqlStatementBuilderException {
		SqlInsertColumnsBuilder<E> next = this.builder.row(List.of(
			SqlColumnValue.of(this.column1, value1),
			SqlColumnValue.of(this.column2, value2),
			SqlColumnValue.of(this.column3, value3),
			SqlColumnValue.of(this.column4, value4),
			SqlColumnValue.of(this.column5, value5),
			SqlColumnValue.of(this.column6, value6),
			SqlColumnValue.of(this.column7, value7)
		));
		return new SqlInsertColumns7<>(next, this.column1, this.column2, this.column3, this.column4, this.column5, this.column6, this.column7);
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
