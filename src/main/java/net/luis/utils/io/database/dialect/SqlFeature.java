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
 *
 * @author Luis-St
 *
 */

public enum SqlFeature {
	
	RETURNING,
	LATERAL_JOIN,
	CTE,
	RECURSIVE_CTE,
	NULLS_ORDERING,
	SCHEMAS,
	WINDOW_FUNCTIONS,
	FOR_UPDATE,
	FOR_SHARE,
	SKIP_LOCKED,
	NO_WAIT,
	TRUNCATE_CASCADE,
	IS_DISTINCT_FROM,
	UPSERT_SUFFIX,
	TRANSACTIONAL_DDL,
	ROW_LOCKING,
	UPSERT,
	INSERT_OR_IGNORE,
	RENAME_INDEX,
	ALTER_COLUMN,
	ADD_CONSTRAINT,
	DROP_CONSTRAINT,
	ARRAY_TYPE,
	TABLE_REBUILD,
	OFFSET_WITHOUT_LIMIT,
	JOINED_DML,
	UPDATE_RETURNING
}
