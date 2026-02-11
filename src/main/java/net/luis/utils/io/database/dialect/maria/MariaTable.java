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

package net.luis.utils.io.database.dialect.maria;

import net.luis.utils.io.database.dialect.mysql.MysqlTable;
import net.luis.utils.io.database.sequence.SqlSequenceDefinition;
import org.jspecify.annotations.NonNull;

/**
 * Interface representing a MariaDB-specific table.<br>
 * Extends MySQL table with MariaDB additions.<br>
 *
 * @author Luis-St
 *
 * @param <T> The type of the entity
 */
public interface MariaTable<T> extends MysqlTable<T> {
	
	/**
	 * Sets whether this table uses system-versioned (temporal) rows.<br>
	 * Generates SQL: {@code ALTER TABLE ... ADD SYSTEM VERSIONING} or {@code ALTER TABLE ... DROP SYSTEM VERSIONING}.<br>
	 * <p>
	 *     System-versioned tables automatically track the history of row changes,<br>
	 *     allowing temporal queries to retrieve past states of the data.
	 * </p>
	 *
	 * @param versioned Whether the table should be system-versioned
	 */
	void setSystemVersioned(boolean versioned);
	
	@Override
	void createSequence(@NonNull SqlSequenceDefinition definition);
}
