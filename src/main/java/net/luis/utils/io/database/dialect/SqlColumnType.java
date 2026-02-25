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

/**
 * Enum representing abstract SQL column types that each dialect maps to concrete SQL strings.<br>
 * <p>
 *     These types are database-agnostic.<br>
 *     Dialect-specific types (e.g., {@code BYTEA}, {@code JSONB}) are produced by the dialect's {@link SqlDialect#mapColumnType(SqlColumnType)} method.
 * </p>
 *
 * @author Luis-St
 */
public enum SqlColumnType {
	
	/**
	 * Represents a variable-length string type. The actual SQL type (e.g., {@code VARCHAR}, {@code NVARCHAR}) is determined by the dialect.<br>
	 */
	VARCHAR,
	/**
	 * Represents a fixed-length string type. The actual SQL type (e.g., {@code CHAR}, {@code NCHAR}) is determined by the dialect.<br>
	 */
	TEXT,
	/**
	 * Represents a single character type. The actual SQL type (e.g., {@code CHAR}, {@code NCHAR}) is determined by the dialect.<br>
	 */
	CHAR,
	/**
	 * Represents a boolean type. The actual SQL type (e.g., {@code BOOLEAN}, {@code BIT}) is determined by the dialect.<br>
	 */
	BOOLEAN,
	/**
	 * Represents a small integer type. The actual SQL type (e.g., {@code SMALLINT}, {@code TINYINT}) is determined by the dialect.<br>
	 */
	SMALLINT,
	/**
	 * Represents a standard integer type. The actual SQL type (e.g., {@code INTEGER}, {@code INT}) is determined by the dialect.<br>
	 */
	INTEGER,
	/**
	 * Represents a large integer type. The actual SQL type (e.g., {@code BIGINT}, {@code LONG}) is determined by the dialect.<br>
	 */
	BIGINT,
	/**
	 * Represents a floating-point number type. The actual SQL type (e.g., {@code REAL}, {@code FLOAT}) is determined by the dialect.<br>
	 */
	REAL,
	/**
	 * Represents a double-precision floating-point number type. The actual SQL type (e.g., {@code DOUBLE}, {@code FLOAT}) is determined by the dialect.<br>
	 */
	DOUBLE,
	/**
	 * Represents a decimal number type. The actual SQL type (e.g., {@code DECIMAL}, {@code NUMERIC}) is determined by the dialect.<br>
	 */
	DECIMAL,
	/**
	 * Represents a universally unique identifier type. The actual SQL type (e.g., {@code UUID}, {@code UNIQUEIDENTIFIER}) is determined by the dialect.<br>
	 */
	UUID,
	/**
	 * Represents a date type. The actual SQL type (e.g., {@code DATE}, {@code DATETIME}) is determined by the dialect.<br>
	 */
	DATE,
	/**
	 * Represents a time type. The actual SQL type (e.g., {@code TIME}, {@code TIMESTAMP}) is determined by the dialect.<br>
	 */
	TIME,
	/**
	 * Represents a timestamp type. The actual SQL type (e.g., {@code TIMESTAMP}, {@code DATETIME}) is determined by the dialect.<br>
	 */
	TIMESTAMP,
	/**
	 * Represents a timestamp with time zone type. The actual SQL type (e.g., {@code TIMESTAMP WITH TIME ZONE}, {@code DATETIMEOFFSET}) is determined by the dialect.<br>
	 */
	TIMESTAMP_TZ,
	/**
	 * Represents a binary large object type. The actual SQL type (e.g., {@code BLOB}, {@code BYTEA}) is determined by the dialect.<br>
	 */
	BLOB,
	/**
	 * Represents a binary data type. The actual SQL type (e.g., {@code BINARY}, {@code VARBINARY}) is determined by the dialect.<br>
	 */
	BINARY,
	/**
	 * Represents a JSON type. The actual SQL type (e.g., {@code JSON}, {@code JSONB}) is determined by the dialect.<br>
	 */
	JSON,
	/**
	 * Represents an array type. The actual SQL type (e.g., {@code ARRAY}, {@code JSON}) is determined by the dialect.<br>
	 */
	ARRAY,
	/**
	 * Represents an XML type. The actual SQL type (e.g., {@code XML}, {@code CLOB}) is determined by the dialect.<br>
	 */
	XML
}
