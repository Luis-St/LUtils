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

package net.luis.utils.io.database.query;

import net.luis.utils.io.database.condition.SqlExpression;
import net.luis.utils.io.database.query.crud.*;
import net.luis.utils.io.database.table.SqlColumn;
import net.luis.utils.io.database.query.row.*;
import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.function.Function;

/**
 *
 * @author Luis-St
 *
 */

public class SqlQueryProvider<T> {
	
	public @NonNull SqlSelectQuery<T> select() {
		return null;
	}
	
	public <T1> @NonNull SqlSelectQuery<T1> select(@NonNull SqlExpression<T1> e1) {
		return null;
	}
	
	public <T1, T2> @NonNull SqlSelectQuery<SqlRow2<T1, T2>> select(@NonNull SqlExpression<T1> e1, @NonNull SqlExpression<T2> e2) {
		return null;
	}
	
	public <T1, T2, T3> @NonNull SqlSelectQuery<SqlRow3<T1, T2, T3>> select(@NonNull SqlExpression<T1> e1, @NonNull SqlExpression<T2> e2, @NonNull SqlExpression<T3> e3) {
		return null;
	}
	
	public <T1, T2, T3, T4> @NonNull SqlSelectQuery<SqlRow4<T1, T2, T3, T4>> select(
		@NonNull SqlExpression<T1> e1, @NonNull SqlExpression<T2> e2,
		@NonNull SqlExpression<T3> e3, @NonNull SqlExpression<T4> e4
	) {
		return null;
	}
	
	public <T1, T2, T3, T4, T5> @NonNull SqlSelectQuery<SqlRow5<T1, T2, T3, T4, T5>> select(
		@NonNull SqlExpression<T1> e1, @NonNull SqlExpression<T2> e2,
		@NonNull SqlExpression<T3> e3, @NonNull SqlExpression<T4> e4,
		@NonNull SqlExpression<T5> e5
	) {
		return null;
	}
	
	public <T1, T2, T3, T4, T5, T6> @NonNull SqlSelectQuery<SqlRow6<T1, T2, T3, T4, T5, T6>> select(
		@NonNull SqlExpression<T1> e1, @NonNull SqlExpression<T2> e2,
		@NonNull SqlExpression<T3> e3, @NonNull SqlExpression<T4> e4,
		@NonNull SqlExpression<T5> e5, @NonNull SqlExpression<T6> e6
	) {
		return null;
	}
	
	public <T1, T2, T3, T4, T5, T6, T7> @NonNull SqlSelectQuery<SqlRow7<T1, T2, T3, T4, T5, T6, T7>> select(
		@NonNull SqlExpression<T1> e1, @NonNull SqlExpression<T2> e2,
		@NonNull SqlExpression<T3> e3, @NonNull SqlExpression<T4> e4,
		@NonNull SqlExpression<T5> e5, @NonNull SqlExpression<T6> e6,
		@NonNull SqlExpression<T7> e7
	) {
		return null;
	}
	
	public <T1, T2, T3, T4, T5, T6, T7, T8> @NonNull SqlSelectQuery<SqlRow8<T1, T2, T3, T4, T5, T6, T7, T8>> select(
		@NonNull SqlExpression<T1> e1, @NonNull SqlExpression<T2> e2,
		@NonNull SqlExpression<T3> e3, @NonNull SqlExpression<T4> e4,
		@NonNull SqlExpression<T5> e5, @NonNull SqlExpression<T6> e6,
		@NonNull SqlExpression<T7> e7, @NonNull SqlExpression<T8> e8
	) {
		return null;
	}
	
	public <T1, T2, T3, T4, T5, T6, T7, T8, T9> @NonNull SqlSelectQuery<SqlRow9<T1, T2, T3, T4, T5, T6, T7, T8, T9>> select(
		@NonNull SqlExpression<T1> e1, @NonNull SqlExpression<T2> e2,
		@NonNull SqlExpression<T3> e3, @NonNull SqlExpression<T4> e4,
		@NonNull SqlExpression<T5> e5, @NonNull SqlExpression<T6> e6,
		@NonNull SqlExpression<T7> e7, @NonNull SqlExpression<T8> e8,
		@NonNull SqlExpression<T9> e9
	)  {
		return null;
	}
	
	public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> @NonNull SqlSelectQuery<SqlRow10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>> select(
		@NonNull SqlExpression<T1> e1, @NonNull SqlExpression<T2> e2,
		@NonNull SqlExpression<T3> e3, @NonNull SqlExpression<T4> e4,
		@NonNull SqlExpression<T5> e5, @NonNull SqlExpression<T6> e6,
		@NonNull SqlExpression<T7> e7, @NonNull SqlExpression<T8> e8,
		@NonNull SqlExpression<T9> e9, @NonNull SqlExpression<T10> e10
	) {
		return null;
	}
	
	public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> @NonNull SqlSelectQuery<SqlRow11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> select(
		@NonNull SqlExpression<T1> e1, @NonNull SqlExpression<T2> e2,
		@NonNull SqlExpression<T3> e3, @NonNull SqlExpression<T4> e4,
		@NonNull SqlExpression<T5> e5, @NonNull SqlExpression<T6> e6,
		@NonNull SqlExpression<T7> e7, @NonNull SqlExpression<T8> e8,
		@NonNull SqlExpression<T9> e9, @NonNull SqlExpression<T10> e10,
		@NonNull SqlExpression<T11> e11
	) {
		return null;
	}
	
	public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> @NonNull SqlSelectQuery<SqlRow12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>> select(
		@NonNull SqlExpression<T1> e1, @NonNull SqlExpression<T2> e2,
		@NonNull SqlExpression<T3> e3, @NonNull SqlExpression<T4> e4,
		@NonNull SqlExpression<T5> e5, @NonNull SqlExpression<T6> e6,
		@NonNull SqlExpression<T7> e7, @NonNull SqlExpression<T8> e8,
		@NonNull SqlExpression<T9> e9, @NonNull SqlExpression<T10> e10,
		@NonNull SqlExpression<T11> e11, @NonNull SqlExpression<T12> e12
	) {
		return null;
	}
	
	public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> @NonNull SqlSelectQuery<SqlRow13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>> select(
		@NonNull SqlExpression<T1> e1, @NonNull SqlExpression<T2> e2,
		@NonNull SqlExpression<T3> e3, @NonNull SqlExpression<T4> e4,
		@NonNull SqlExpression<T5> e5, @NonNull SqlExpression<T6> e6,
		@NonNull SqlExpression<T7> e7, @NonNull SqlExpression<T8> e8,
		@NonNull SqlExpression<T9> e9, @NonNull SqlExpression<T10> e10,
		@NonNull SqlExpression<T11> e11, @NonNull SqlExpression<T12> e12,
		@NonNull SqlExpression<T13> e13
	) {
		return null;
	}
	
	public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> @NonNull SqlSelectQuery<SqlRow14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14>> select(
		@NonNull SqlExpression<T1> e1, @NonNull SqlExpression<T2> e2,
		@NonNull SqlExpression<T3> e3, @NonNull SqlExpression<T4> e4,
		@NonNull SqlExpression<T5> e5, @NonNull SqlExpression<T6> e6,
		@NonNull SqlExpression<T7> e7, @NonNull SqlExpression<T8> e8,
		@NonNull SqlExpression<T9> e9, @NonNull SqlExpression<T10> e10,
		@NonNull SqlExpression<T11> e11, @NonNull SqlExpression<T12> e12,
		@NonNull SqlExpression<T13> e13, @NonNull SqlExpression<T14> e14
	) {
		return null;
	}
	
	public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> @NonNull SqlSelectQuery<SqlRow15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15>> select(
		@NonNull SqlExpression<T1> e1, @NonNull SqlExpression<T2> e2,
		@NonNull SqlExpression<T3> e3, @NonNull SqlExpression<T4> e4,
		@NonNull SqlExpression<T5> e5, @NonNull SqlExpression<T6> e6,
		@NonNull SqlExpression<T7> e7, @NonNull SqlExpression<T8> e8,
		@NonNull SqlExpression<T9> e9, @NonNull SqlExpression<T10> e10,
		@NonNull SqlExpression<T11> e11, @NonNull SqlExpression<T12> e12,
		@NonNull SqlExpression<T13> e13, @NonNull SqlExpression<T14> e14,
		@NonNull SqlExpression<T15> e15
	) {
		return null;
	}
	
	public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> @NonNull SqlSelectQuery<SqlRow16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16>> select(
		@NonNull SqlExpression<T1> e1, @NonNull SqlExpression<T2> e2,
		@NonNull SqlExpression<T3> e3, @NonNull SqlExpression<T4> e4,
		@NonNull SqlExpression<T5> e5, @NonNull SqlExpression<T6> e6,
		@NonNull SqlExpression<T7> e7, @NonNull SqlExpression<T8> e8,
		@NonNull SqlExpression<T9> e9, @NonNull SqlExpression<T10> e10,
		@NonNull SqlExpression<T11> e11, @NonNull SqlExpression<T12> e12,
		@NonNull SqlExpression<T13> e13, @NonNull SqlExpression<T14> e14,
		@NonNull SqlExpression<T15> e15, @NonNull SqlExpression<T16> e16
	) {
		return null;
	}
	
	public @NonNull SqlSelectQuery<?> select(SqlExpression<?> @NonNull ... expressions) {
		return null;
	}
	
	public @NonNull SqlSelectQuery<?> subquery(SqlExpression<?> @NonNull ... expressions) {
		return null;
	}
	
	public @NonNull SqlInsertQuery<T> insert(@NonNull T entity) {
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public @NonNull SqlInsertQuery<T> insert(T @NonNull ... entities) {
		return null;
	}
	
	public @NonNull SqlInsertQuery<T> insert(@NonNull Collection<T> entities) {
		return null;
	}
	
	public @NonNull SqlInsertQuery<T> insert(@NonNull Collection<T> entities, int batchSize) {
		return null;
	}
	
	public @NonNull SqlInsertQuery<T> upsert(@NonNull T entity, @NonNull SqlColumn<?> conflictColumn, @NonNull Function<T, T> onConflict) {
		return null;
	}
	
	public @NonNull SqlInsertQuery<T> insertOrIgnore(@NonNull T entity, SqlColumn<?> @NonNull ... conflictColumns) {
		return null;
	}
	
	public @NonNull SqlInsertQuery<T> insertFromSelect(@NonNull SqlSelectQuery<?> query) {
		return null;
	}
	
	public @NonNull SqlUpdateQuery<T> update() {
		return null;
	}
	
	public @NonNull SqlDeleteQuery<T> delete() {
		return null;
	}
}
