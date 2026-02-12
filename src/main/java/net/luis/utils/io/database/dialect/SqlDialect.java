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
import net.luis.utils.io.database.dialect.postgres.PostgresDialect;
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
	public static final MysqlDialect MYSQL = new MysqlDialect();
	public static final MariaDialect MARIA = new MariaDialect();
	public static final SqliteDialect SQLITE = new SqliteDialect();
	public static final H2Dialect H2 = new H2Dialect();
	public static final SqlDefaultDialect DEFAULT = new SqlDefaultDialect();
	
	/**
	 * Returns the identifier of this dialect.<br>
	 * @return The dialect identifier
	 */
	public @NonNull String getId() {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Returns the display name of this dialect.<br>
	 * @return The dialect name
	 */
	public @NonNull String getName() {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Returns the features supported by this dialect.<br>
	 * @return The dialect features
	 */
	public @NonNull SqlDialectFeatures getFeatures() {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Quotes the given identifier for use in SQL statements.<br>
	 * @param identifier The identifier to quote
	 * @return The quoted identifier
	 */
	public @NonNull String quoteIdentifier(@NonNull String identifier) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Returns the SQL function for the current timestamp.<br>
	 * @return The now function syntax
	 */
	public @NonNull String nowFunction() {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Returns the SQL function for generating a UUID.<br>
	 * @return The UUID function syntax
	 */
	public @NonNull String uuidFunction() {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Maps a column type to the dialect-specific SQL type name.<br>
	 *
	 * @param type The column type to map
	 * @return The SQL type name
	 */
	public @NonNull String mapColumnType(@NonNull SqlColumnType type) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Maps a Java type to the corresponding SQL column type.<br>
	 *
	 * @param javaType The Java type to map
	 * @return The SQL column type
	 */
	public @NonNull SqlColumnType mapJavaType(@NonNull Class<?> javaType) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Returns the auto-increment syntax for this dialect.<br>
	 * @return The auto-increment syntax
	 */
	public @NonNull String autoIncrementSyntax() {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Returns the suffix appended to {@code CREATE TABLE} statements.<br>
	 * @return The create table suffix
	 */
	public @NonNull String createTableSuffix() {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Returns whether the dialect supports {@code IF NOT EXISTS} in DDL statements.<br>
	 * @return Whether {@code IF NOT EXISTS} is supported
	 */
	public boolean supportsIfNotExists() {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Returns the limit and offset syntax for this dialect.<br>
	 *
	 * @param limit The maximum number of rows
	 * @param offset The number of rows to skip
	 * @return The limit/offset SQL syntax
	 */
	public @NonNull String limitOffsetSyntax(int limit, int offset) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Returns the upsert syntax for this dialect.<br>
	 * @return The upsert SQL syntax
	 */
	public @NonNull String upsertSyntax() {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Returns the {@code RETURNING} clause syntax for this dialect.<br>
	 *
	 * @param columns The columns to return
	 * @return The returning SQL syntax
	 */
	public @NonNull String returningSyntax(@NonNull String columns) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Returns the boolean literal for the given value.<br>
	 *
	 * @param value The boolean value
	 * @return The boolean literal
	 */
	public @NonNull String booleanLiteral(boolean value) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Returns the string concatenation operator for this dialect.<br>
	 * @return The concatenation operator
	 */
	public @NonNull String stringConcatOperator() {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Returns the parameter placeholder for the given index.<br>
	 *
	 * @param index The parameter index
	 * @return The parameter placeholder
	 */
	public @NonNull String parameterPlaceholder(int index) {
		throw new UnsupportedOperationException();
	}
}
