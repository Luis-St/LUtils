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

package net.luis.utils.io.database.query.crud;

import net.luis.utils.io.database.condition.SqlCondition;
import net.luis.utils.io.database.dialect.SqlDialect;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.expression.SqlExpression;
import net.luis.utils.io.database.query.SqlJoinableQuery;
import net.luis.utils.io.database.rendering.SqlRendered;
import net.luis.utils.io.database.table.SqlColumn;
import net.luis.utils.io.database.table.SqlTable;
import org.jspecify.annotations.NonNull;

import java.util.List;

/**
 *
 * @author Luis-St
 *
 */

public class SqlUpdateQuery<E> implements SqlJoinableQuery<E> {
	
	@Override
	public @NonNull SqlUpdateQuery<E> innerJoin(@NonNull SqlTable<?> table, @NonNull SqlCondition on) {
		return null;
	}
	
	@Override
	public @NonNull SqlUpdateQuery<E> leftJoin(@NonNull SqlTable<?> table, @NonNull SqlCondition on) {
		return null;
	}
	
	@Override
	public @NonNull SqlUpdateQuery<E> rightJoin(@NonNull SqlTable<?> table, @NonNull SqlCondition on) {
		return null;
	}
	
	@Override
	public @NonNull SqlUpdateQuery<E> fullJoin(@NonNull SqlTable<?> table, @NonNull SqlCondition on) {
		return null;
	}
	
	@Override
	public @NonNull SqlUpdateQuery<E> crossJoin(@NonNull SqlTable<?> table) {
		return null;
	}
	
	public <V> @NonNull SqlUpdateQuery<E> set(@NonNull SqlColumn<E, V> column, @NonNull V value) {
		return null;
	}
	
	public <V> @NonNull SqlUpdateQuery<E> set(@NonNull SqlColumn<E, V> column, @NonNull SqlExpression<V> expression) {
		return null;
	}
	
	public <V extends Number> @NonNull SqlUpdateQuery<E> increment(@NonNull SqlColumn<E, V> column, @NonNull V incrementBy) {
		return null;
	}
	
	public <V extends Number> @NonNull SqlUpdateQuery<E> increment(@NonNull SqlColumn<E, V> column, @NonNull SqlExpression<V> incrementByExpression) {
		return null;
	}
	
	public <V extends Number> @NonNull SqlUpdateQuery<E> decrement(@NonNull SqlColumn<E, V> column, @NonNull V decrementBy) {
		return null;
	}
	
	public <V extends Number> @NonNull SqlUpdateQuery<E> decrement(@NonNull SqlColumn<E, V> column, @NonNull SqlExpression<V> decrementByExpression) {
		return null;
	}
	
	public @NonNull SqlUpdateQuery<E> where(@NonNull SqlCondition condition) {
		return null;
	}
	
	public @NonNull SqlUpdateQuery<E> batchSize(int batchSize) {
		return null;
	}
	
	public int execute() throws SqlException {
		return 0;
	}
	
	public @NonNull List<E> returning() throws SqlException {
		return null;
	}
	
	@Override
	public @NonNull SqlRendered toSql(@NonNull SqlDialect dialect) {
		return null;
	}
}
