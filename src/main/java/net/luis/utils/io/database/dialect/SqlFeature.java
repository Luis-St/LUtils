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
 * Enum representing SQL dialect features that may or may not be supported by a specific dialect.<br>
 *
 * @author Luis-St
 */
public enum SqlFeature {

	/**
	 * The {@code RETURNING} clause for retrieving affected rows from mutation queries.<br>
	 */
	RETURNING,

	/**
	 * The {@code SKIP LOCKED} clause for skipping locked rows in queries.<br>
	 */
	SKIP_LOCKED,

	/**
	 * Array column types.<br>
	 */
	ARRAYS,

	/**
	 * The {@code JSONB} binary JSON data type.<br>
	 */
	JSONB,

	/**
	 * Partial indexes with a {@code WHERE} clause.<br>
	 */
	PARTIAL_INDEXES,

	/**
	 * Database sequences for generating unique values.<br>
	 */
	SEQUENCES,

	/**
	 * Common Table Expressions (CTEs) using {@code WITH} clauses.<br>
	 */
	CTE,

	/**
	 * Window functions for computing values across rows.<br>
	 */
	WINDOW_FUNCTIONS,

	/**
	 * A native boolean data type.<br>
	 */
	BOOLEAN_TYPE,

	/**
	 * Upsert operations ({@code INSERT ... ON CONFLICT} or equivalent).<br>
	 */
	UPSERT,

	/**
	 * Database schemas for organizing tables into namespaces.<br>
	 */
	SCHEMAS,

	/**
	 * Generated columns computed from other columns.<br>
	 */
	GENERATED_COLUMNS,

	/**
	 * The {@code MERGE} statement for conditional insert/update/delete.<br>
	 */
	MERGE,

	/**
	 * {@code LATERAL} joins for correlated subqueries in the {@code FROM} clause.<br>
	 */
	LATERAL,

	/**
	 * JSON operations and functions.<br>
	 */
	JSON,

	/**
	 * {@code TRUNCATE ... CASCADE} for truncating tables with foreign key references.<br>
	 */
	TRUNCATE_CASCADE,

	/**
	 * Table inheritance for creating child tables that inherit from parent tables.<br>
	 */
	TABLE_INHERITANCE,

	/**
	 * Identity columns for auto-generated values.<br>
	 */
	IDENTITY_COLUMNS
}
