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

package net.luis.utils.io.database.dialect;

import net.luis.utils.io.database.dialect.maria.MariaDialect;
import net.luis.utils.io.database.dialect.mysql.MysqlDialect;
import net.luis.utils.io.database.dialect.oracle.OracleDialect;
import net.luis.utils.io.database.dialect.postgis.PostgisDialect;
import net.luis.utils.io.database.dialect.postgres.PostgresDialect;
import net.luis.utils.io.database.dialect.sqlserver.SqlServerDialect;
import net.luis.utils.io.database.dialect.timescale.TimescaleDialect;
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
	
	public static final PostgresDialect POSTGRES = new PostgresDialect();
	public static final TimescaleDialect TIMESCALE = new TimescaleDialect();
	public static final PostgisDialect POSTGIS = new PostgisDialect();
	public static final MysqlDialect MYSQL = new MysqlDialect();
	public static final MariaDialect MARIA = new MariaDialect();
	public static final SqlServerDialect SQL_SERVER = new SqlServerDialect();
	public static final OracleDialect ORACLE = new OracleDialect();
	public static final SqliteDialect SQLITE = new SqliteDialect();
	public static final H2Dialect H2 = new H2Dialect();
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
	
	public @NonNull String mapColumnType(@NonNull SqlColumnType type) {
		throw new UnsupportedOperationException();
	}
	
	public @NonNull SqlColumnType mapJavaType(@NonNull Class<?> javaType) {
		throw new UnsupportedOperationException();
	}
	
	public @NonNull String autoIncrementSyntax() {
		throw new UnsupportedOperationException();
	}
	
	public @NonNull String createTableSuffix() {
		throw new UnsupportedOperationException();
	}
	
	public boolean supportsIfNotExists() {
		throw new UnsupportedOperationException();
	}
	
	public @NonNull String limitOffsetSyntax(int limit, int offset) {
		throw new UnsupportedOperationException();
	}
	
	public @NonNull String upsertSyntax() {
		throw new UnsupportedOperationException();
	}
	
	public @NonNull String returningSyntax(@NonNull String columns) {
		throw new UnsupportedOperationException();
	}
	
	public @NonNull String booleanLiteral(boolean value) {
		throw new UnsupportedOperationException();
	}
	
	public @NonNull String stringConcatOperator() {
		throw new UnsupportedOperationException();
	}
	
	public @NonNull String parameterPlaceholder(int index) {
		throw new UnsupportedOperationException();
	}
}
