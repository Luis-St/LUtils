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
 *
 * @author Luis-St
 */
public enum SqlColumnType {
	
	VARCHAR,
	TEXT,
	CHAR,
	BOOLEAN,
	SMALLINT,
	INTEGER,
	BIGINT,
	REAL,
	DOUBLE,
	DECIMAL,
	UUID,
	DATE,
	TIME,
	TIMESTAMP,
	TIMESTAMP_TZ,
	BLOB,
	BYTEA,
	JSON,
	JSONB,
	ARRAY,
	XML
}
