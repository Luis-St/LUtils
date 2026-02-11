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

import net.luis.utils.io.database.dialect.SqlDialect;
import net.luis.utils.io.database.transaction.SqlTransaction;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * Interface representing a SQL database connection.<br>
 *
 * @author Luis-St
 */
public interface SqlDatabase extends AutoCloseable {
	
	static @NonNull SqlDatabase create(@NonNull SqlDatabaseConfig config) {
		throw new UnsupportedOperationException();
	}
	
	static @NonNull SqlDatabase createAndSetDefault(@NonNull SqlDatabaseConfig config) {
		throw new UnsupportedOperationException();
	}
	
	static @NonNull SqlDatabase getDefault() {
		throw new UnsupportedOperationException();
	}
	
	static void setDefault(@NonNull SqlDatabase database) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	void close();
	
	@NonNull SqlDialect<?, ?> getDialect();
	
	boolean health();
	
	boolean ping();
	
	@NonNull SqlTransaction beginTransaction();
	
	@NonNull SqlTransaction beginTransaction(@NonNull SqlIsolationLevel isolationLevel);
	
	<T> T inTransaction(@NonNull Function<SqlTransaction, T> action);
	
	<T> @NonNull CompletableFuture<T> inTransactionAsync(@NonNull Function<SqlTransaction, T> action);
}
