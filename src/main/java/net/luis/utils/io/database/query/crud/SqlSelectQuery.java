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

import net.luis.utils.io.database.*;
import net.luis.utils.io.database.column.SqlColumn;
import net.luis.utils.io.database.condition.SqlCondition;
import net.luis.utils.io.database.condition.SqlOrderable;
import net.luis.utils.io.database.dialect.SqlDialect;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.mapping.SqlMapper;
import net.luis.utils.io.database.query.*;
import net.luis.utils.io.database.rendering.SqlRendered;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 *
 * @author Luis-St
 *
 */

public class SqlSelectQuery<T> implements SqlJoinableQuery<T> {
	
	public @NonNull SqlSelectQuery<T> forUpdate() {
		return null;
	}
	
	public @NonNull SqlSelectQuery<T> skipLocked() {
		return null;
	}
	
	public @NonNull SqlSelectQuery<T> forShare() {
		return null;
	}
	
	public @NonNull SqlSelectQuery<T> noWait() throws SqlException {
		return null;
	}
	
	@Override
	public @NonNull SqlSelectQuery<T> innerJoin(@NonNull SqlTable<?> table, @NonNull SqlCondition on) {
		return null;
	}
	
	@Override
	public @NonNull SqlSelectQuery<T> leftJoin(@NonNull SqlTable<?> table, @NonNull SqlCondition on) {
		return null;
	}
	
	@Override
	public @NonNull SqlSelectQuery<T> rightJoin(@NonNull SqlTable<?> table, @NonNull SqlCondition on) {
		return null;
	}
	
	@Override
	public @NonNull SqlSelectQuery<T> fullJoin(@NonNull SqlTable<?> table, @NonNull SqlCondition on) {
		return null;
	}
	
	@Override
	public @NonNull SqlSelectQuery<T> crossJoin(@NonNull SqlTable<?> table) {
		return null;
	}
	
	public @NonNull SqlSelectQuery<T> lateralJoin(@NonNull SqlSelectQuery<?> subquery, @NonNull SqlAlias alias) {
		return null;
	}
	
	public @NonNull SqlSelectQuery<T> where(@NonNull SqlCondition condition) {
		return null;
	}
	
	public @NonNull SqlSelectQuery<T> whereExists(@NonNull SqlSelectQuery<?> subquery) {
		return null;
	}
	
	public @NonNull SqlSelectQuery<T> groupBy(SqlColumn<?> @NonNull ... columns) {
		return null;
	}
	
	public @NonNull SqlSelectQuery<T> having(@NonNull SqlCondition condition) {
		return null;
	}
	
	public @NonNull SqlSelectQuery<T> orderBy(SqlOrderable @NonNull ... orderables) {
		return null;
	}
	
	public @NonNull SqlSelectQuery<T> limit(int limit) {
		return null;
	}
	
	public @NonNull SqlSelectQuery<T> offset(long offset) {
		return null;
	}
	
	public @NonNull SqlSelectQuery<T> distinct() {
		return null;
	}
	
	public @NonNull SqlSelectQuery<T> union(@NonNull SqlSelectQuery<T> other) {
		return null;
	}
	
	public @NonNull SqlSelectQuery<T> unionAll(@NonNull SqlSelectQuery<T> other) {
		return null;
	}
	
	public @NonNull SqlSelectQuery<T> intersect(@NonNull SqlSelectQuery<T> other) {
		return null;
	}
	
	public @NonNull SqlSelectQuery<T> except(@NonNull SqlSelectQuery<T> other) {
		return null;
	}
	
	public <R> @NonNull SqlSelectQuery<R> projectInto(@NonNull Class<R> type) {
		return null;
	}
	
	public <R> @NonNull SqlSelectQuery<R> projectInto(@NonNull Class<R> type, @NonNull SqlMapper mapper) {
		return null;
	}
	
	public @NonNull SqlCommonTableExpression asCommonExpression(@NonNull SqlAlias alias, boolean recursive) {
		return null;
	}
	
	public @NonNull List<T> fetch() throws SqlException {
		return null;
	}
	
	public @NonNull Optional<T> fetchFirst() throws SqlException {
		return null;
	}
	
	public @NonNull T fetchOne() throws SqlException {
		return null;
	}
	
	public @Nullable T fetchOneOrNull() throws SqlException {
		return null;
	}
	
	public long count() throws SqlException {
		return 0;
	}
	
	public boolean exists() throws SqlException {
		return false;
	}
	
	public @NonNull Stream<T> stream() throws SqlException {
		return null;
	}
	
	public @NonNull SqlPage<T> fetchPage(int page, int pageSize) throws SqlException {
		return null;
	}
	
	@Override
	public @NonNull SqlRendered toSql(@NonNull SqlDialect dialect) {
		return null;
	}
}
