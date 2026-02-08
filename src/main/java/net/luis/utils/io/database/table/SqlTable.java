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

import net.luis.utils.io.database.dialect.SqlDialect;
import net.luis.utils.io.database.index.SqlIndexDefinition;
import net.luis.utils.io.database.index.SqlIndexInfo;
import net.luis.utils.io.database.query.*;
import net.luis.utils.io.database.sequence.SqlSequenceDefinition;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.function.Function;

/**
 * Interface representing a SQL table.<br>
 *
 * @author Luis-St
 *
 * @param <T> The type of the entity
 */
public interface SqlTable<T> {
	
	static <T> @NonNull SqlTable<T> of(@NonNull String name, @NonNull Class<T> type) {
		throw new UnsupportedOperationException();
	}
	
	<TT, CC> @NonNull TT dialect(@NonNull SqlDialect<TT, CC> dialect);
	
	<C> @NonNull SqlColumn<C> column(@NonNull String name, @NonNull Class<C> type);
	
	<C, R> @NonNull SqlForeignColumn<C, R> foreignColumn(@NonNull String name, @NonNull Class<C> type, @NonNull SqlTable<R> referencedTable);
	
	@NonNull SqlSelectQuery<T> select();
	
	@NonNull SqlSelectProjectionQuery<T> select(SqlColumn<?> @NonNull ... columns);
	
	@NonNull SqlInsertQuery<T> insert();
	
	@NonNull T insert(@NonNull T entity);
	
	@SuppressWarnings("unchecked")
	@NonNull List<T> insert(T @NonNull ... entities);
	
	@NonNull T upsert(@NonNull T entity, @NonNull SqlColumn<?> conflictColumn, @NonNull Function<T, T> onConflict);
	
	void insertOrIgnore(@NonNull T entity, SqlColumn<?> @NonNull ... conflictColumns);
	
	@NonNull SqlUpdateQuery<T> update();
	
	void update(@NonNull T entity);
	
	@NonNull SqlDeleteQuery<T> delete();
	
	void delete(@NonNull T entity);
	
	@NonNull SqlSelectQuery<?> subquery(SqlColumn<?> @NonNull ... columns);
	
	void createIndex(@NonNull String name, SqlColumn<?> @NonNull ... columns);
	
	void createIndex(@NonNull SqlIndexDefinition definition);
	
	void dropIndex(@NonNull String name);
	
	@NonNull List<SqlIndexInfo> listIndexes();
	
	void createSequence(@NonNull SqlSequenceDefinition definition);
	
	long nextSequenceValue(@NonNull String name);
	
	@NonNull String generateCreateSql();
	
	@NonNull String generateDropSql();
	
	void create();
	
	void createIfNotExists();
	
	void drop();
	
	void dropIfExists();
	
	void truncate();
	
	boolean exists();
}
