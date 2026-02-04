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

package net.luis.utils.io.database.table;

import net.luis.utils.io.database.query.*;
import org.jspecify.annotations.NonNull;

/**
 * Interface representing a SQL table.<br>
 *
 * @param <T> The type of the entity
 * @author Luis-St
 */
public interface SqlTable<T> {

	<C> @NonNull SqlColumn<C> column(@NonNull String name, @NonNull Class<C> type);

	<C, R> @NonNull SqlForeignColumn<C, R> foreignColumn(@NonNull String name, @NonNull Class<C> type, @NonNull SqlTable<R> referencedTable);

	@NonNull SqlSelectQuery<T> select();

	@NonNull SqlInsertQuery<T> insert();

	@NonNull SqlUpdateQuery<T> update();

	@NonNull SqlDeleteQuery<T> delete();

	@NonNull SqlJoinBuilder<T> join(@NonNull SqlTable<?> other);

	@NonNull SqlJoinBuilder<T> leftJoin(@NonNull SqlTable<?> other);

	// DDL methods
	void create();

	void createIfNotExists();

	void drop();

	void dropIfExists();

	void truncate();

	boolean exists();
}
