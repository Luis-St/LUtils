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
import net.luis.utils.io.database.query.row.*;
import net.luis.utils.io.database.table.SqlColumn;
import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.function.Function;

/**
 *
 * @author Luis-St
 *
 */

public class SqlQueryProvider<E> {
	
	public @NonNull SqlSelectQuery<E> select() {
		return null;
	}
	
	public <E1> @NonNull SqlSelectQuery<E1> select(@NonNull SqlExpression<E1> e1) {
		return null;
	}
	
	public <E1, E2> @NonNull SqlSelectQuery<SqlRow2<E1, E2>> select(@NonNull SqlExpression<E1> e1, @NonNull SqlExpression<E2> e2) {
		return null;
	}
	
	public <E1, E2, E3> @NonNull SqlSelectQuery<SqlRow3<E1, E2, E3>> select(@NonNull SqlExpression<E1> e1, @NonNull SqlExpression<E2> e2, @NonNull SqlExpression<E3> e3) {
		return null;
	}
	
	public <E1, E2, E3, E4> @NonNull SqlSelectQuery<SqlRow4<E1, E2, E3, E4>> select(
		@NonNull SqlExpression<E1> e1, @NonNull SqlExpression<E2> e2,
		@NonNull SqlExpression<E3> e3, @NonNull SqlExpression<E4> e4
	) {
		return null;
	}
	
	public <E1, E2, E3, E4, E5> @NonNull SqlSelectQuery<SqlRow5<E1, E2, E3, E4, E5>> select(
		@NonNull SqlExpression<E1> e1, @NonNull SqlExpression<E2> e2,
		@NonNull SqlExpression<E3> e3, @NonNull SqlExpression<E4> e4,
		@NonNull SqlExpression<E5> e5
	) {
		return null;
	}
	
	public <E1, E2, E3, E4, E5, E6> @NonNull SqlSelectQuery<SqlRow6<E1, E2, E3, E4, E5, E6>> select(
		@NonNull SqlExpression<E1> e1, @NonNull SqlExpression<E2> e2,
		@NonNull SqlExpression<E3> e3, @NonNull SqlExpression<E4> e4,
		@NonNull SqlExpression<E5> e5, @NonNull SqlExpression<E6> e6
	) {
		return null;
	}
	
	public <E1, E2, E3, E4, E5, E6, E7> @NonNull SqlSelectQuery<SqlRow7<E1, E2, E3, E4, E5, E6, E7>> select(
		@NonNull SqlExpression<E1> e1, @NonNull SqlExpression<E2> e2,
		@NonNull SqlExpression<E3> e3, @NonNull SqlExpression<E4> e4,
		@NonNull SqlExpression<E5> e5, @NonNull SqlExpression<E6> e6,
		@NonNull SqlExpression<E7> e7
	) {
		return null;
	}
	
	public <E1, E2, E3, E4, E5, E6, E7, E8> @NonNull SqlSelectQuery<SqlRow8<E1, E2, E3, E4, E5, E6, E7, E8>> select(
		@NonNull SqlExpression<E1> e1, @NonNull SqlExpression<E2> e2,
		@NonNull SqlExpression<E3> e3, @NonNull SqlExpression<E4> e4,
		@NonNull SqlExpression<E5> e5, @NonNull SqlExpression<E6> e6,
		@NonNull SqlExpression<E7> e7, @NonNull SqlExpression<E8> e8
	) {
		return null;
	}
	
	public <E1, E2, E3, E4, E5, E6, E7, E8, E9> @NonNull SqlSelectQuery<SqlRow9<E1, E2, E3, E4, E5, E6, E7, E8, E9>> select(
		@NonNull SqlExpression<E1> e1, @NonNull SqlExpression<E2> e2,
		@NonNull SqlExpression<E3> e3, @NonNull SqlExpression<E4> e4,
		@NonNull SqlExpression<E5> e5, @NonNull SqlExpression<E6> e6,
		@NonNull SqlExpression<E7> e7, @NonNull SqlExpression<E8> e8,
		@NonNull SqlExpression<E9> e9
	) {
		return null;
	}
	
	public <E1, E2, E3, E4, E5, E6, E7, E8, E9, E10> @NonNull SqlSelectQuery<SqlRow10<E1, E2, E3, E4, E5, E6, E7, E8, E9, E10>> select(
		@NonNull SqlExpression<E1> e1, @NonNull SqlExpression<E2> e2,
		@NonNull SqlExpression<E3> e3, @NonNull SqlExpression<E4> e4,
		@NonNull SqlExpression<E5> e5, @NonNull SqlExpression<E6> e6,
		@NonNull SqlExpression<E7> e7, @NonNull SqlExpression<E8> e8,
		@NonNull SqlExpression<E9> e9, @NonNull SqlExpression<E10> e10
	) {
		return null;
	}
	
	public <E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11> @NonNull SqlSelectQuery<SqlRow11<E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11>> select(
		@NonNull SqlExpression<E1> e1, @NonNull SqlExpression<E2> e2,
		@NonNull SqlExpression<E3> e3, @NonNull SqlExpression<E4> e4,
		@NonNull SqlExpression<E5> e5, @NonNull SqlExpression<E6> e6,
		@NonNull SqlExpression<E7> e7, @NonNull SqlExpression<E8> e8,
		@NonNull SqlExpression<E9> e9, @NonNull SqlExpression<E10> e10,
		@NonNull SqlExpression<E11> e11
	) {
		return null;
	}
	
	public <E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11, E12> @NonNull SqlSelectQuery<SqlRow12<E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11, E12>> select(
		@NonNull SqlExpression<E1> e1, @NonNull SqlExpression<E2> e2,
		@NonNull SqlExpression<E3> e3, @NonNull SqlExpression<E4> e4,
		@NonNull SqlExpression<E5> e5, @NonNull SqlExpression<E6> e6,
		@NonNull SqlExpression<E7> e7, @NonNull SqlExpression<E8> e8,
		@NonNull SqlExpression<E9> e9, @NonNull SqlExpression<E10> e10,
		@NonNull SqlExpression<E11> e11, @NonNull SqlExpression<E12> e12
	) {
		return null;
	}
	
	public <E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11, E12, E13> @NonNull SqlSelectQuery<SqlRow13<E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11, E12, E13>> select(
		@NonNull SqlExpression<E1> e1, @NonNull SqlExpression<E2> e2,
		@NonNull SqlExpression<E3> e3, @NonNull SqlExpression<E4> e4,
		@NonNull SqlExpression<E5> e5, @NonNull SqlExpression<E6> e6,
		@NonNull SqlExpression<E7> e7, @NonNull SqlExpression<E8> e8,
		@NonNull SqlExpression<E9> e9, @NonNull SqlExpression<E10> e10,
		@NonNull SqlExpression<E11> e11, @NonNull SqlExpression<E12> e12,
		@NonNull SqlExpression<E13> e13
	) {
		return null;
	}
	
	public <E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11, E12, E13, E14> @NonNull SqlSelectQuery<SqlRow14<E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11, E12, E13, E14>> select(
		@NonNull SqlExpression<E1> e1, @NonNull SqlExpression<E2> e2,
		@NonNull SqlExpression<E3> e3, @NonNull SqlExpression<E4> e4,
		@NonNull SqlExpression<E5> e5, @NonNull SqlExpression<E6> e6,
		@NonNull SqlExpression<E7> e7, @NonNull SqlExpression<E8> e8,
		@NonNull SqlExpression<E9> e9, @NonNull SqlExpression<E10> e10,
		@NonNull SqlExpression<E11> e11, @NonNull SqlExpression<E12> e12,
		@NonNull SqlExpression<E13> e13, @NonNull SqlExpression<E14> e14
	) {
		return null;
	}
	
	public <E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11, E12, E13, E14, E15> @NonNull SqlSelectQuery<SqlRow15<E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11, E12, E13, E14, E15>> select(
		@NonNull SqlExpression<E1> e1, @NonNull SqlExpression<E2> e2,
		@NonNull SqlExpression<E3> e3, @NonNull SqlExpression<E4> e4,
		@NonNull SqlExpression<E5> e5, @NonNull SqlExpression<E6> e6,
		@NonNull SqlExpression<E7> e7, @NonNull SqlExpression<E8> e8,
		@NonNull SqlExpression<E9> e9, @NonNull SqlExpression<E10> e10,
		@NonNull SqlExpression<E11> e11, @NonNull SqlExpression<E12> e12,
		@NonNull SqlExpression<E13> e13, @NonNull SqlExpression<E14> e14,
		@NonNull SqlExpression<E15> e15
	) {
		return null;
	}
	
	public <E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11, E12, E13, E14, E15, E16> @NonNull SqlSelectQuery<SqlRow16<E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11, E12, E13, E14, E15, E16>> select(
		@NonNull SqlExpression<E1> e1, @NonNull SqlExpression<E2> e2,
		@NonNull SqlExpression<E3> e3, @NonNull SqlExpression<E4> e4,
		@NonNull SqlExpression<E5> e5, @NonNull SqlExpression<E6> e6,
		@NonNull SqlExpression<E7> e7, @NonNull SqlExpression<E8> e8,
		@NonNull SqlExpression<E9> e9, @NonNull SqlExpression<E10> e10,
		@NonNull SqlExpression<E11> e11, @NonNull SqlExpression<E12> e12,
		@NonNull SqlExpression<E13> e13, @NonNull SqlExpression<E14> e14,
		@NonNull SqlExpression<E15> e15, @NonNull SqlExpression<E16> e16
	) {
		return null;
	}
	
	public @NonNull SqlSelectQuery<?> select(SqlExpression<?> @NonNull ... expressions) {
		return null;
	}
	
	public @NonNull SqlSelectQuery<?> subquery(SqlExpression<?> @NonNull ... expressions) {
		return null;
	}
	
	public @NonNull SqlInsertQuery<E> insert(@NonNull E entity) {
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public @NonNull SqlInsertQuery<E> insert(E @NonNull ... entities) {
		return null;
	}
	
	public @NonNull SqlInsertQuery<E> insert(@NonNull Collection<E> entities) {
		return null;
	}
	
	public @NonNull SqlInsertQuery<E> insert(@NonNull Collection<E> entities, int batchSize) {
		return null;
	}
	
	public @NonNull SqlInsertQuery<E> upsert(@NonNull E entity, @NonNull SqlColumn<E, ?> conflictColumn, @NonNull Function<E, E> onConflict) {
		return null;
	}
	
	@SafeVarargs
	public final @NonNull SqlInsertQuery<E> insertOrIgnore(@NonNull E entity, SqlColumn<E, ?> @NonNull ... conflictColumns) {
		return null;
	}
	
	public @NonNull SqlInsertQuery<E> insertFromSelect(@NonNull SqlSelectQuery<?> query) {
		return null;
	}
	
	public @NonNull SqlUpdateQuery<E> update() {
		return null;
	}
	
	public @NonNull SqlDeleteQuery<E> delete() {
		return null;
	}
}
