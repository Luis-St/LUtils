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

package net.luis.utils.io.database;

import net.luis.utils.function.throwable.ThrowableFunction;
import net.luis.utils.function.throwable.ThrowableSupplier;
import net.luis.utils.io.database.dialect.SqlDialect;
import net.luis.utils.io.database.exception.*;
import net.luis.utils.io.database.query.SqlQueryProvider;
import net.luis.utils.io.database.table.SqlTable;
import net.luis.utils.io.database.table.SqlTableProvider;
import net.luis.utils.io.database.transaction.SqlIsolationLevel;
import net.luis.utils.io.database.transaction.SqlTransaction;
import net.luis.utils.io.databasev1.transaction.SqlPropagation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;
import org.jspecify.annotations.NonNull;

import javax.sql.DataSource;
import java.time.Duration;
import java.util.Objects;

/**
 *
 * @author Luis-St
 *
 */

public class SqlDatabase implements SqlProvider, AutoCloseable {
	
	private final DataSource dataSource;
	
	SqlDatabase(@NonNull DataSource dataSource) {
		this.dataSource = Objects.requireNonNull(dataSource, "Data source must not be null");
	}
	
	public static @NonNull SqlDatabaseBuilder builder(@NonNull DataSource dataSource) {
		return new SqlDatabaseBuilder(dataSource);
	}
	
	public @NotNull SqlDialect getDialect() {
		return null;
	}
	
	public boolean health() {
		return false;
	}
	
	public boolean ping() {
		return false;
	}
	
	public void connect() throws SqlConnectionException {
	
	}
	
	@Override
	public void createSchema(@NotNull String name) throws SqlException {
	
	}
	
	@Override
	public void createSchemaIfNotExists(@NotNull String name) throws SqlException {
	
	}
	
	@Override
	public boolean existsSchema(@NotNull String name) throws SqlException {
		return false;
	}
	
	@Override
	public void dropSchema(@NotNull String name, boolean cascade) throws SqlException {
	
	}
	
	@Override
	public @NonNull <T> SqlTableProvider<T> table(@NonNull SqlTable<T> table) {
		return null;
	}
	
	@Override
	public @NonNull <T> SqlQueryProvider<T> from(@NonNull SqlTable<T> table) {
		return null;
	}
	
	public @NonNull SqlTransaction beginTransaction() {
		return null;
	}
	
	public @NonNull SqlTransaction beginTransaction(@NonNull Duration timeout, @NonNull SqlIsolationLevel isolationLevel, @NonNull SqlPropagation propagationBehavior) {
		return null;
	}
	
	public @NonNull SqlTransaction beginReadOnlyTransaction() {
		return null;
	}
	
	public @NonNull SqlTransaction beginReadOnlyTransaction(@NonNull Duration timeout, @NonNull SqlIsolationLevel isolationLevel, @NonNull SqlPropagation propagationBehavior) {
		return null;
	}
	
	public <T> @UnknownNullability T inTransaction(@NonNull ThrowableFunction<SqlTransaction, T, SqlException> action) throws SqlException {
		try (SqlTransaction tx = this.beginTransaction()) {
			return this.inTransaction(tx, action);
		}
	}
	
	public <T> @UnknownNullability T inTransaction(@NonNull SqlTransaction transaction, @NonNull ThrowableSupplier<T, SqlException> action) throws SqlException {
		// Should be used when the transaction is provided from in the same scope and can be accessed in the lambda expression
		
		Objects.requireNonNull(action, "Transaction action must not be null");
		return this.inTransaction(transaction, _ -> action.get());
	}
	
	public <T> @UnknownNullability T inTransaction(@NonNull SqlTransaction transaction, @NonNull ThrowableFunction<SqlTransaction, T, SqlException> action) throws SqlException {
		// Should be used when the transaction can not be accessed in the lambda expression, like where a method is passed: this::execute -> T execute(@NonNull SqlTransaction tx)
		
		try (SqlTransaction tx = this.beginTransaction()) {
			try {
				T result = action.apply(tx);
				tx.commit();
				return result;
			} catch (Exception e) {
				try {
					tx.rollback();
				} catch (SqlTransactionException rollbackEx) {
					e.addSuppressed(rollbackEx);
				}
				
				if (e instanceof SqlException sqlEx) {
					throw sqlEx;
				}
				
				if (e instanceof RuntimeException rte) {
					throw rte;
				}
				throw new SqlException("Transaction failed", e);
			}
		}
	}
	
	@Override
	public void close() {
	
	}
}
 
