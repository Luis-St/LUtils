package net.luis.utils;

import net.luis.utils.io.database.audit.SqlAuditContext;
import net.luis.utils.io.database.transaction.SqlSavepoint;
import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

/**
 *
 * @author Luis-St
 *
 */

public class DatabaseTest {
	
	/**
	 * Base class for all other SqlExceptions, extends Exception to force handling or declaration of these exceptions
	 */
	public static class SqlException extends Exception {
		
		public SqlException(String message) {
			super(message);
		}
		
		public SqlException(String message, Throwable cause) {
			super(message, cause);
		}
		
		public SqlException(Throwable cause) {
			super(cause);
		}
	}
	
	void main() {
		SqlDatabase db = SqlDatabase.of(new SqlDatabaseConfig() {});
		
		record Person(int id, String name) {}
		
		SqlTable<Person> personTable = SqlTable.of("person", Person.class);
		SqlColumn<Integer> idColumn = personTable.column("id", Integer.class);
		SqlColumn<String> nameColumn = personTable.column("name", String.class);
		
		db.from(personTable).insert(new Person(1, "Alice"), new Person(2, "Bob"));
		
		db.from(personTable).select(idColumn).where(null/*idColumn.equalTo(10)*/).fetch();
		db.from(personTable).select(idColumn).where(null/*SqlCondition.not(idColumn.equalTo(10))*/)/*.fetchPage(1, 20)*/;
		db.from(personTable).select(idColumn).where(null/*SqlCondition.or(idColumn.equalTo(10), idColumn.equalTo(20))*/)/*.fetchOne()*/;
		db.from(personTable).select(idColumn).where(null/*SqlCondition.and(idColumn.equalTo(10), nameColumn.equalTo("Test"))*/)/*.fetchOneOrNull()*/;
	}
	
	interface SqlTransaction {
		
		void commit();
		
		void rollback();
		
		<Q> @NonNull SqlQueryProvider<Q> withIn(@NonNull SqlTable<Q> table);
		
		@NonNull SqlSavepoint savepoint(@NonNull String name);
		
		void rollbackTo(@NonNull SqlSavepoint savepoint);
	}
	
	interface SqlDatabaseConfig {}
	
	interface SqlDatabase {
		
		static @NonNull SqlDatabase of(@NonNull SqlDatabaseConfig config) {
			throw new UnsupportedOperationException("Not implemented");
		}
		
		@NonNull SqlAuditContext getAuditContext();
		
		void setAuditContext(@NonNull SqlAuditContext ctx);
		
		<Q> @NonNull SqlQueryProvider<Q> from(@NonNull SqlTable<Q> table);
		
		@NonNull SqlTransaction beginTransaction();
		
		@NonNull SqlTransaction beginTransaction(@NonNull SqlAuditContext ctx /*, more options here, would it make sense to implement a SqlTransactionOption record?*/);
	}
	
	interface SqlQuery<Q> {}
	
	interface SqlSelectQuery<Q> extends SqlQuery<Q> {
		
		@NonNull SqlSelectQuery<CompletableFuture<Q>> async();
		
		@NonNull SqlSelectQuery<Q> where(String condition /*SqlCondition, ....*/);
		
		@NonNull SqlSelectQuery<Q> groupBy(SqlColumn<?> @NonNull ... columns);
		
		@NonNull SqlSelectQuery<Q> orderBy(@NonNull SqlColumn<?> column, boolean ascending);
		
		@NonNull Q fetch();
		
		// other fetch methods
	}
	
	interface SqlInsertQuery<Q> extends SqlQuery<Q> {}
	
	interface SqlUpdateQuery<Q> extends SqlQuery<Q> {}
	
	interface SqlDeleteQuery<Q> extends SqlQuery<Q> {}
	
	interface SqlQueryProvider<Q> {
		
		@NonNull SqlSelectQuery<Q> select();
		
		<T> @NonNull SqlSelectQuery<T> select(@NonNull SqlColumn<T> column);
		
		<T1, T2> @NonNull SqlSelectQuery<SqlRow2<T1, T2>> select(
			@NonNull SqlColumn<T1> column1, @NonNull SqlColumn<T2> column2
		);
		
		<T1, T2, T3> @NonNull SqlSelectQuery<SqlRow3<T1, T2, T3>> select(
			@NonNull SqlColumn<T1> column1, @NonNull SqlColumn<T2> column2, @NonNull SqlColumn<T3> column3
		);
		
		<T1, T2, T3, T4> @NonNull SqlSelectQuery<SqlRow4<T1, T2, T3, T4>> select(
			@NonNull SqlColumn<T1> column1, @NonNull SqlColumn<T2> column2, @NonNull SqlColumn<T3> column3, @NonNull SqlColumn<T4> column4
		);
		
		// more select methods for more columns (up to 16, generic varargs option for more columns)
		
		@NonNull SqlInsertQuery<Q> insert(@NonNull Q entity);
		
		@NonNull SqlInsertQuery<Q> insert(Q @NonNull ... entities);
		
		@NonNull SqlInsertQuery<Q> insert(@NonNull Collection<Q> entities, int batchSize);
		
		@NonNull SqlInsertQuery<Q> insertOrIgnore(@NonNull Q entity, SqlColumn<?> @NonNull ... conflictColumns);
		
		@NonNull SqlInsertQuery<Q> insertFromSelect(@NonNull SqlSelectQuery<Q> query);
		
		@NonNull SqlUpdateQuery<Q> update();
		
		@NonNull SqlDeleteQuery<Q> delete();
	}
	
	interface SqlTable<Q> {
		
		static <Q> @NonNull SqlTable<Q> of(@NonNull String name, @NonNull Class<Q> type) {
			throw new UnsupportedOperationException("Not implemented");
		}
		
		<C> @NonNull SqlColumn<C> column(@NonNull String name, @NonNull Class<C> type);
		
		<C, R> @NonNull SqlForeignColumn<C, R> foreignColumn(@NonNull String name, @NonNull Class<C> type, @NonNull SqlTable<R> referencedTable);
		
		<V> @NonNull SqlVersionColumn<V> versionColumn(@NonNull String name, @NonNull Class<V> type);
		
		<V> @NonNull SqlCreationColumn<V> creationColumn(@NonNull String name, @NonNull Class<V> type);
		
		<V> @NonNull SqlUpdateColumn<V> updateColumn(@NonNull String name, @NonNull Class<V> type);
	}
	
	interface SqlColumn<T> {}
	
	interface SqlForeignColumn<T, R> extends SqlColumn<T> {}
	
	interface SqlVersionColumn<T> extends SqlColumn<T> {}
	
	interface SqlCreationColumn<T> extends SqlColumn<T> {}
	
	interface SqlUpdateColumn<T> extends SqlColumn<T> {}
	
	interface SqlRow2<T1, T2> {}
	
	interface SqlRow3<T1, T2, T3> {}
	
	interface SqlRow4<T1, T2, T3, T4> {}
}
