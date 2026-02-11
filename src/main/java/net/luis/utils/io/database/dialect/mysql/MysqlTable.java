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

package net.luis.utils.io.database.dialect.mysql;

import net.luis.utils.io.database.table.SqlColumn;
import net.luis.utils.io.database.table.SqlTable;
import org.jspecify.annotations.NonNull;

/**
 * Interface representing a MySQL-specific table.<br>
 *
 * @author Luis-St
 *
 * @param <T> The type of the entity
 */
public interface MysqlTable<T> extends SqlTable<T> {
	
	/**
	 * Sets the storage engine for this table.<br>
	 * Generates SQL: {@code ENGINE=engine}.<br>
	 *
	 * @param engine The MySQL storage engine to use
	 */
	void setEngine(@NonNull MysqlEngine engine);
	
	/**
	 * Sets the default character set for this table.<br>
	 * Generates SQL: {@code DEFAULT CHARSET=charset}.<br>
	 *
	 * @param charset The character set name
	 */
	void setCharset(@NonNull String charset);
	
	/**
	 * Sets the collation for this table.<br>
	 * Generates SQL: {@code COLLATE=collation}.<br>
	 *
	 * @param collation The collation name
	 */
	void setCollation(@NonNull String collation);
	
	/**
	 * Sets the auto-increment starting value for this table.<br>
	 * Generates SQL: {@code AUTO_INCREMENT=value}.<br>
	 *
	 * @param value The auto-increment starting value
	 */
	void setAutoIncrement(long value);
	
	/**
	 * Sets a comment for this table.<br>
	 * Generates SQL: {@code COMMENT='comment'}.<br>
	 *
	 * @param comment The table comment
	 */
	void setComment(@NonNull String comment);
	
	/**
	 * Adds a fulltext index on the specified columns.<br>
	 * Generates SQL: {@code CREATE FULLTEXT INDEX name ON table(columns)}.<br>
	 *
	 * @param name The name of the fulltext index
	 * @param columns The columns to include in the index
	 */
	void addFullTextIndex(@NonNull String name, SqlColumn<?> @NonNull ... columns);
}
