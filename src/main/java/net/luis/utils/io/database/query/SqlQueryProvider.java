package net.luis.utils.io.database.query;

import net.luis.utils.io.database.condition.SqlExpression;
import net.luis.utils.io.database.query.crud.*;
import net.luis.utils.io.database.table.SqlColumn;
import net.luis.utils.io.databasev1.query.SqlSelectProjectionQuery;
import net.luis.utils.io.databasev1.query.row.*;
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
	
	public <T1> @NonNull SqlSelectProjectionQuery<T1> select(@NonNull SqlExpression<T1> e1) {
		return null;
	}
	
	public <T1, T2> @NonNull SqlSelectProjectionQuery<SqlRow2<T1, T2>> select(@NonNull SqlExpression<T1> e1, @NonNull SqlExpression<T2> e2) {
		return null;
	}
	
	public <T1, T2, T3> @NonNull SqlSelectProjectionQuery<SqlRow3<T1, T2, T3>> select(@NonNull SqlExpression<T1> e1, @NonNull SqlExpression<T2> e2, @NonNull SqlExpression<T3> e3) {
		return null;
	}
	
	public <T1, T2, T3, T4> @NonNull SqlSelectProjectionQuery<SqlRow4<T1, T2, T3, T4>> select(
		@NonNull SqlExpression<T1> e1, @NonNull SqlExpression<T2> e2,
		@NonNull SqlExpression<T3> e3, @NonNull SqlExpression<T4> e4
	) {
		return null;
	}
	
	public <T1, T2, T3, T4, T5> @NonNull SqlSelectProjectionQuery<SqlRow5<T1, T2, T3, T4, T5>> select(
		@NonNull SqlExpression<T1> e1, @NonNull SqlExpression<T2> e2,
		@NonNull SqlExpression<T3> e3, @NonNull SqlExpression<T4> e4,
		@NonNull SqlExpression<T5> e5
	) {
		return null;
	}
	
	public <T1, T2, T3, T4, T5, T6> @NonNull SqlSelectProjectionQuery<SqlRow6<T1, T2, T3, T4, T5, T6>> select(
		@NonNull SqlExpression<T1> e1, @NonNull SqlExpression<T2> e2,
		@NonNull SqlExpression<T3> e3, @NonNull SqlExpression<T4> e4,
		@NonNull SqlExpression<T5> e5, @NonNull SqlExpression<T6> e6
	) {
		return null;
	}
	
	public <T1, T2, T3, T4, T5, T6, T7> @NonNull SqlSelectProjectionQuery<SqlRow7<T1, T2, T3, T4, T5, T6, T7>> select(
		@NonNull SqlExpression<T1> e1, @NonNull SqlExpression<T2> e2,
		@NonNull SqlExpression<T3> e3, @NonNull SqlExpression<T4> e4,
		@NonNull SqlExpression<T5> e5, @NonNull SqlExpression<T6> e6,
		@NonNull SqlExpression<T7> e7
	) {
		return null;
	}
	
	public <T1, T2, T3, T4, T5, T6, T7, T8> @NonNull SqlSelectProjectionQuery<SqlRow8<T1, T2, T3, T4, T5, T6, T7, T8>> select(
		@NonNull SqlExpression<T1> e1, @NonNull SqlExpression<T2> e2,
		@NonNull SqlExpression<T3> e3, @NonNull SqlExpression<T4> e4,
		@NonNull SqlExpression<T5> e5, @NonNull SqlExpression<T6> e6,
		@NonNull SqlExpression<T7> e7, @NonNull SqlExpression<T8> e8
	) {
		return null;
	}
	
	public <T1, T2, T3, T4, T5, T6, T7, T8, T9> @NonNull SqlSelectProjectionQuery<SqlRow9<T1, T2, T3, T4, T5, T6, T7, T8, T9>> select(
		@NonNull SqlExpression<T1> e1, @NonNull SqlExpression<T2> e2,
		@NonNull SqlExpression<T3> e3, @NonNull SqlExpression<T4> e4,
		@NonNull SqlExpression<T5> e5, @NonNull SqlExpression<T6> e6,
		@NonNull SqlExpression<T7> e7, @NonNull SqlExpression<T8> e8,
		@NonNull SqlExpression<T9> e9
	)  {
		return null;
	}
	
	public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> @NonNull SqlSelectProjectionQuery<SqlRow10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>> select(
		@NonNull SqlExpression<T1> e1, @NonNull SqlExpression<T2> e2,
		@NonNull SqlExpression<T3> e3, @NonNull SqlExpression<T4> e4,
		@NonNull SqlExpression<T5> e5, @NonNull SqlExpression<T6> e6,
		@NonNull SqlExpression<T7> e7, @NonNull SqlExpression<T8> e8,
		@NonNull SqlExpression<T9> e9, @NonNull SqlExpression<T10> e10
	) {
		return null;
	}
	
	public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> @NonNull SqlSelectProjectionQuery<SqlRow11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> select(
		@NonNull SqlExpression<T1> e1, @NonNull SqlExpression<T2> e2,
		@NonNull SqlExpression<T3> e3, @NonNull SqlExpression<T4> e4,
		@NonNull SqlExpression<T5> e5, @NonNull SqlExpression<T6> e6,
		@NonNull SqlExpression<T7> e7, @NonNull SqlExpression<T8> e8,
		@NonNull SqlExpression<T9> e9, @NonNull SqlExpression<T10> e10,
		@NonNull SqlExpression<T11> e11
	) {
		return null;
	}
	
	public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> @NonNull SqlSelectProjectionQuery<SqlRow12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>> select(
		@NonNull SqlExpression<T1> e1, @NonNull SqlExpression<T2> e2,
		@NonNull SqlExpression<T3> e3, @NonNull SqlExpression<T4> e4,
		@NonNull SqlExpression<T5> e5, @NonNull SqlExpression<T6> e6,
		@NonNull SqlExpression<T7> e7, @NonNull SqlExpression<T8> e8,
		@NonNull SqlExpression<T9> e9, @NonNull SqlExpression<T10> e10,
		@NonNull SqlExpression<T11> e11, @NonNull SqlExpression<T12> e12
	) {
		return null;
	}
	
	public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> @NonNull SqlSelectProjectionQuery<SqlRow13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>> select(
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
	
	public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> @NonNull SqlSelectProjectionQuery<SqlRow14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14>> select(
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
	
	public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> @NonNull SqlSelectProjectionQuery<SqlRow15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15>> select(
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
	
	public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> @NonNull SqlSelectProjectionQuery<SqlRow16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16>> select(
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
	
	public @NonNull SqlSelectProjectionQuery<?> select(SqlExpression<?> @NonNull ... expressions) {
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
