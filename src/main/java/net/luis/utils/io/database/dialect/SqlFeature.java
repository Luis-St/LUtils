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
 * Represents an optional sql feature that a {@link SqlDialect} may or may not support.<br>
 * Dialects declare their capabilities through {@link SqlDialect#isFeatureSupported(SqlFeature)}.<br>
 *
 * @author Luis-St
 */
public enum SqlFeature {
	
	/**
	 * Support for the returning clause on insert, update and delete statements.<br>
	 */
	RETURNING,
	/**
	 * Support for lateral joins.<br>
	 */
	LATERAL_JOIN,
	/**
	 * Support for common table expressions.<br>
	 */
	CTE,
	/**
	 * Support for recursive common table expressions.<br>
	 */
	RECURSIVE_CTE,
	/**
	 * Support for explicit nulls ordering in order by clauses.<br>
	 */
	NULLS_ORDERING,
	/**
	 * Support for database schemas.<br>
	 */
	SCHEMAS,
	/**
	 * Support for window functions.<br>
	 */
	WINDOW_FUNCTIONS,
	/**
	 * Support for the for update row locking clause.<br>
	 */
	FOR_UPDATE,
	/**
	 * Support for the for share row locking clause.<br>
	 */
	FOR_SHARE,
	/**
	 * Support for skipping already locked rows.<br>
	 */
	SKIP_LOCKED,
	/**
	 * Support for failing immediately instead of waiting for a lock.<br>
	 */
	NO_WAIT,
	/**
	 * Support for cascading truncate statements.<br>
	 */
	TRUNCATE_CASCADE,
	/**
	 * Support for the is distinct from comparison.<br>
	 */
	IS_DISTINCT_FROM,
	/**
	 * Support for an upsert suffix appended after the statement.<br>
	 */
	UPSERT_SUFFIX,
	/**
	 * Support for transactional data definition language statements.<br>
	 */
	TRANSACTIONAL_DDL,
	/**
	 * Support for row-level locking.<br>
	 */
	ROW_LOCKING,
	/**
	 * Support for upsert statements.<br>
	 */
	UPSERT,
	/**
	 * Support for insert-or-ignore statements.<br>
	 */
	INSERT_OR_IGNORE,
	/**
	 * Support for renaming indexes.<br>
	 */
	RENAME_INDEX,
	/**
	 * Support for altering existing columns.<br>
	 */
	ALTER_COLUMN,
	/**
	 * Support for adding constraints to existing tables.<br>
	 */
	ADD_CONSTRAINT,
	/**
	 * Support for dropping constraints from existing tables.<br>
	 */
	DROP_CONSTRAINT,
	/**
	 * Support for array column types.<br>
	 */
	ARRAY_TYPE,
	/**
	 * Support for rebuilding tables during migrations.<br>
	 */
	TABLE_REBUILD,
	/**
	 * Support for an offset without an accompanying limit.<br>
	 */
	OFFSET_WITHOUT_LIMIT,
	/**
	 * Support for data manipulation statements that join multiple tables.<br>
	 */
	JOINED_DML,
	/**
	 * Support for the returning clause on update statements.<br>
	 */
	UPDATE_RETURNING
}
