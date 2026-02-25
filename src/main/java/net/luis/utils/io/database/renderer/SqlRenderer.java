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

package net.luis.utils.io.database.renderer;

import net.luis.utils.io.database.dialect.SqlDialect;
import org.jspecify.annotations.NonNull;

/**
 * Interface responsible for rendering SQL syntax elements in a dialect-specific way.<br>
 * <p>
 *     Renderers handle the syntactic differences between SQL dialects such as
 *     identifier quoting, boolean literals, limit/offset syntax, etc.<br>
 * </p>
 *
 * @author Luis-St
 */
public interface SqlRenderer {

	/**
	 * Returns the dialect associated with this renderer.<br>
	 * @return The SQL dialect
	 */
	@NonNull SqlDialect getDialect();

	/**
	 * Quotes the given identifier for use in SQL statements.<br>
	 *
	 * @param identifier The identifier to quote
	 * @return The quoted identifier
	 */
	@NonNull String quoteIdentifier(@NonNull String identifier);

	/**
	 * Returns the SQL function for the current timestamp.<br>
	 * @return The now function syntax
	 */
	@NonNull String nowFunction();

	/**
	 * Returns the SQL function for generating a UUID.<br>
	 * @return The UUID function syntax
	 */
	@NonNull String uuidFunction();

	/**
	 * Returns the auto-increment syntax for this dialect.<br>
	 * @return The auto-increment syntax
	 */
	@NonNull String autoIncrementSyntax();

	/**
	 * Returns the suffix appended to {@code CREATE TABLE} statements.<br>
	 * @return The create table suffix
	 */
	@NonNull String createTableSuffix();

	/**
	 * Returns whether the dialect supports {@code IF NOT EXISTS} in DDL statements.<br>
	 * @return Whether {@code IF NOT EXISTS} is supported
	 */
	boolean supportsIfNotExists();

	/**
	 * Returns the limit and offset syntax for this dialect.<br>
	 *
	 * @param limit The maximum number of rows
	 * @param offset The number of rows to skip
	 * @return The limit/offset SQL syntax
	 */
	@NonNull String limitOffsetSyntax(int limit, int offset);

	/**
	 * Returns the upsert syntax for this dialect.<br>
	 * @return The upsert SQL syntax
	 */
	@NonNull String upsertSyntax();

	/**
	 * Returns the {@code RETURNING} clause syntax for this dialect.<br>
	 *
	 * @param columns The columns to return
	 * @return The returning SQL syntax
	 */
	@NonNull String returningSyntax(@NonNull String columns);

	/**
	 * Returns the boolean literal for the given value.<br>
	 *
	 * @param value The boolean value
	 * @return The boolean literal
	 */
	@NonNull String booleanLiteral(boolean value);

	/**
	 * Returns the string concatenation operator for this dialect.<br>
	 * @return The concatenation operator
	 */
	@NonNull String stringConcatOperator();

	/**
	 * Returns the parameter placeholder for the given index.<br>
	 * <p>
	 *     The {@code index} parameter is 1-based and used by dialects that support
	 *     positional parameter syntax (e.g., PostgreSQL's {@code $1}, {@code $2}).<br>
	 *     Dialects using the JDBC standard {@code ?} placeholder may ignore the index.<br>
	 * </p>
	 *
	 * @param index The 1-based parameter index
	 * @return The parameter placeholder (e.g., {@code ?} for ANSI SQL, {@code $1} for PostgreSQL)
	 */
	@NonNull String parameterPlaceholder(int index);
}
