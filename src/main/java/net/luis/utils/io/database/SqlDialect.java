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

import net.luis.utils.io.database.dialect.*;
import net.luis.utils.io.database.dialect.mysql.SqlMysqlDialect;
import net.luis.utils.io.database.dialect.postgres.SqlPostgresDialect;
import net.luis.utils.io.database.dialect.timescale.SqlTimescaleDialect;
import net.luis.utils.io.database.table.SqlColumn;
import net.luis.utils.io.database.table.SqlTable;
import org.jspecify.annotations.NonNull;

/**
 * Abstract class representing the supported SQL dialects.<br>
 *
 * @author Luis-St
 *
 * @param <T> The type returned by {@link SqlTable#dialect(SqlDialect)}
 * @param <C> The type returned by {@link SqlColumn#dialect(SqlDialect)}
 */
public abstract class SqlDialect<T, C> {
	
	public static final SqlPostgresDialect POSTGRES = new SqlPostgresDialect();
	public static final SqlTimescaleDialect TIMESCALE = new SqlTimescaleDialect();
	public static final SqlMysqlDialect MYSQL = new SqlMysqlDialect();
	public static final SqlSqliteDialect SQLITE = new SqlSqliteDialect();
	public static final SqlH2Dialect H2 = new SqlH2Dialect();
	public static final SqlDefaultDialect DEFAULT = new SqlDefaultDialect();
	
	public @NonNull String getId() {
		throw new UnsupportedOperationException();
	}
	
	public @NonNull String getName() {
		throw new UnsupportedOperationException();
	}
	
	public @NonNull SqlDialectFeatures getFeatures() {
		throw new UnsupportedOperationException();
	}
	
	public @NonNull String quoteIdentifier(@NonNull String identifier) {
		throw new UnsupportedOperationException();
	}
	
	public @NonNull String nowFunction() {
		throw new UnsupportedOperationException();
	}
	
	public @NonNull String uuidFunction() {
		throw new UnsupportedOperationException();
	}
}
