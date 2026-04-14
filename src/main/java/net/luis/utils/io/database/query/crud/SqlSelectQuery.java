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

import net.luis.utils.io.database.SqlPage;
import net.luis.utils.io.database.condition.SqlCondition;
import net.luis.utils.io.database.dialect.SqlDialect;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.expression.SqlOrderable;
import net.luis.utils.io.database.query.*;
import net.luis.utils.io.database.rendering.SqlRendered;
import net.luis.utils.io.database.table.SqlColumn;
import net.luis.utils.io.database.table.SqlTable;
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

public class SqlSelectQuery<E> implements SqlJoinableQuery<E> {
	
	public @NonNull SqlSelectQuery<E> forUpdate() {
		return null;
	}
	
	public @NonNull SqlSelectQuery<E> skipLocked() {
		return null;
	}
	
	public @NonNull SqlSelectQuery<E> forShare() {
		return null;
	}
	
	public @NonNull SqlSelectQuery<E> noWait() throws SqlException {
		return null;
	}
	
	@Override
	public @NonNull SqlSelectQuery<E> innerJoin(@NonNull SqlTable<?> table, @NonNull SqlCondition on) {
		return null;
	}
	
	@Override
	public @NonNull SqlSelectQuery<E> leftJoin(@NonNull SqlTable<?> table, @NonNull SqlCondition on) {
		return null;
	}
	
	@Override
	public @NonNull SqlSelectQuery<E> rightJoin(@NonNull SqlTable<?> table, @NonNull SqlCondition on) {
		return null;
	}
	
	@Override
	public @NonNull SqlSelectQuery<E> fullJoin(@NonNull SqlTable<?> table, @NonNull SqlCondition on) {
		return null;
	}
	
	@Override
	public @NonNull SqlSelectQuery<E> crossJoin(@NonNull SqlTable<?> table) {
		return null;
	}
	
	public @NonNull SqlSelectQuery<E> lateralJoin(@NonNull SqlSelectQuery<?> subquery, @NonNull SqlAlias alias) {
		return null;
	}
	
	public @NonNull SqlSelectQuery<E> where(@NonNull SqlCondition condition) {
		return null;
	}
	
	public @NonNull SqlSelectQuery<E> whereExists(@NonNull SqlSelectQuery<?> subquery) {
		return null;
	}
	
	@SafeVarargs
	public final @NonNull SqlSelectQuery<E> groupBy(SqlColumn<E, ?> @NonNull ... columns) {
		return null;
	}
	
	public @NonNull SqlSelectQuery<E> having(@NonNull SqlCondition condition) {
		return null;
	}
	
	public @NonNull SqlSelectQuery<E> orderBy(SqlOrderable<?> @NonNull ... orderables) {
		return null;
	}
	
	public @NonNull SqlSelectQuery<E> limit(int limit) {
		return null;
	}
	
	public @NonNull SqlSelectQuery<E> offset(long offset) {
		return null;
	}
	
	public @NonNull SqlSelectQuery<E> distinct() {
		return null;
	}
	
	public @NonNull SqlSelectQuery<E> union(@NonNull SqlSelectQuery<E> other) {
		return null;
	}
	
	public @NonNull SqlSelectQuery<E> unionAll(@NonNull SqlSelectQuery<E> other) {
		return null;
	}
	
	public @NonNull SqlSelectQuery<E> intersect(@NonNull SqlSelectQuery<E> other) {
		return null;
	}
	
	public @NonNull SqlSelectQuery<E> except(@NonNull SqlSelectQuery<E> other) {
		return null;
	}
	
	public <R> @NonNull SqlSelectQuery<R> projectInto(@NonNull Class<R> type) {
		return null;
	}
	
	public @NonNull SqlCommonTableExpression asCommonExpression(@NonNull SqlAlias alias, boolean recursive) {
		return null;
	}
	
	public @NonNull List<E> fetch() throws SqlException {
		return null;
	}
	
	public @NonNull Optional<E> fetchFirst() throws SqlException {
		return null;
	}
	
	public @NonNull E fetchOne() throws SqlException {
		return null;
	}
	
	public @Nullable E fetchOneOrNull() throws SqlException {
		return null;
	}
	
	public long count() throws SqlException {
		return 0;
	}
	
	public boolean exists() throws SqlException {
		return false;
	}
	
	public @NonNull Stream<E> stream() throws SqlException {
		return null;
	}
	
	public @NonNull SqlPage<E> fetchPage(int page, int pageSize) throws SqlException {
		return null;
	}
	
	@Override
	public @NonNull SqlRendered toSql(@NonNull SqlDialect dialect) throws SqlException {
		return null;
	}
}
