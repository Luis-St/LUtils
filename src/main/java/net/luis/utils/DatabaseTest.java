package net.luis.utils;

import net.luis.utils.io.database.audit.SqlAuditContext;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

/**
 *
 * @author Luis-St
 *
 */

public class DatabaseTest {
	
	void main() {
		SqlDatabase database = null;
		
	}
	
	interface SqlTransaction {
		
		void commit();
		
		void rollback();
		
		<Q> @NonNull SqlQueryProvider<Q> withIn(@NonNull SqlTable<Q> table);
	}
	
	interface SqlDatabase {
		
		@NonNull SqlAuditContext getAuditContext();
		
		void setAuditContext(@NonNull SqlAuditContext ctx);
		
		<Q> @NonNull SqlQueryProvider<Q> from(@NonNull SqlTable<Q> table);
		
		@NonNull SqlTransaction beginTransaction();
		
		@NonNull SqlTransaction beginTransaction(@NonNull SqlAuditContext ctx);
	}
	
	interface SqlQuery<Q> {}
	
	interface SqlSelectQuery<Q> extends SqlQuery<Q> {
		
		@NonNull SqlSelectQuery<CompletableFuture<Q>> async();
		
		@NonNull SqlSelectQuery<Q> where(String condition);
		
		@NonNull SqlSelectQuery<Q> groupBy(String... columns);
		
		@NonNull SqlSelectQuery<Q> orderBy(String column, boolean ascending);
		
		@NonNull Q fetch();
	}
	
	interface SqlInsertQuery<Q> extends SqlQuery<Q> {}
	
	interface SqlUpdateQuery<Q> extends SqlQuery<Q> {}
	
	interface SqlDeleteQuery<Q> extends SqlQuery<Q> {}
	
	interface SqlQueryProvider<Q> {
		
		@NonNull SqlSelectQuery<Q> select();
		
		<T1, T2> @NonNull SqlSelectQuery<SqlRow2<T1, T2>> select(
			@NonNull SqlColumn<T1> column1, @NonNull SqlColumn<T2> column2
		);
		
		<T1, T2, T3> @NonNull SqlSelectQuery<SqlRow3<T1, T2, T3>> select(
			@NonNull SqlColumn<T1> column1, @NonNull SqlColumn<T2> column2, @NonNull SqlColumn<T3> column3
		);
		
		<T1, T2, T3, T4> @NonNull SqlSelectQuery<SqlRow4<T1, T2, T3, T4>> select(
			@NonNull SqlColumn<T1> column1, @NonNull SqlColumn<T2> column2, @NonNull SqlColumn<T3> column3, @NonNull SqlColumn<T4> column4
		);
		
		@NonNull SqlInsertQuery<Q> insert();
		
		@NonNull SqlUpdateQuery<Q> update();
		
		@NonNull SqlDeleteQuery<Q> delete();
	}
	
	interface SqlTable<Q> {}
	
	interface SqlColumn<T> {}
	
	interface SqlRow2<T1, T2> {}
	
	interface SqlRow3<T1, T2, T3> {}
	
	interface SqlRow4<T1, T2, T3, T4> {}
}
