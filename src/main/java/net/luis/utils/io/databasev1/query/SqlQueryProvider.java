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

package net.luis.utils.io.databasev1.query;

import net.luis.utils.io.databasev1.SqlDatabase;
import net.luis.utils.io.databasev1.exception.SqlException;
import net.luis.utils.io.databasev1.function.SqlExpression;
import net.luis.utils.io.databasev1.function.scalar.SqlAgg;
import net.luis.utils.io.databasev1.index.SqlIndexDefinition;
import net.luis.utils.io.databasev1.index.SqlIndexInfo;
import net.luis.utils.io.databasev1.query.row.*;
import net.luis.utils.io.databasev1.table.SqlColumn;
import net.luis.utils.io.databasev1.table.SqlTable;
import net.luis.utils.io.databasev1.transaction.SqlTransaction;
import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

/**
 * Interface providing methods to create SQL queries and DDL operations for a specific entity type.<br>
 * Obtained via {@link SqlDatabase#from(SqlTable)} or {@link SqlTransaction#from(SqlTable)}.<br>
 *
 * @author Luis-St
 */
public interface SqlQueryProvider<T> {
	
	/**
	 * Returns a scoped view of this query provider where automatic version checks are suppressed.<br>
	 * Entity-level update and delete on the returned view will not add automatic {@code WHERE version = ?} and {@code SET version = version + 1} clauses.<br>
	 *
	 * @return A query provider with version checking disabled
	 */
	@NonNull SqlQueryProvider<T> skipVersionCheck();
	
	/**
	 * Creates this table in the database.<br>
	 *
	 * @throws SqlException If a database access error occurs or the table already exists
	 */
	void create() throws SqlException;
	
	/**
	 * Creates this table in the database if it does not exist.<br>
	 *
	 * @throws SqlException If a database access error occurs
	 */
	void createIfNotExists() throws SqlException;
	
	/**
	 * Drops this table from the database.<br>
	 *
	 * @throws SqlException If a database access error occurs or the table does not exist
	 */
	void drop() throws SqlException;
	
	/**
	 * Drops this table from the database if it exists.<br>
	 *
	 * @throws SqlException If a database access error occurs
	 */
	void dropIfExists() throws SqlException;
	
	/**
	 * Truncates (removes all rows from) this table.<br>
	 *
	 * @throws SqlException If a database access error occurs
	 */
	void truncate() throws SqlException;
	
	/**
	 * Checks if this table exists in the database.<br>
	 *
	 * @return True if the table exists, false otherwise
	 * @throws SqlException If a database access error occurs
	 */
	boolean exists() throws SqlException;
	
	/**
	 * Creates an index on the specified columns.<br>
	 *
	 * @param name The index name
	 * @param columns The columns to index
	 * @throws SqlException If a database access error occurs
	 */
	void createIndex(@NonNull String name, SqlColumn<?> @NonNull ... columns) throws SqlException;
	
	/**
	 * Creates an index using the specified definition.<br>
	 *
	 * @param definition The index definition
	 * @throws SqlException If a database access error occurs
	 */
	void createIndex(@NonNull SqlIndexDefinition definition) throws SqlException;
	
	/**
	 * Drops an index by name.<br>
	 *
	 * @param name The index name to drop
	 * @throws SqlException If a database access error occurs
	 */
	void dropIndex(@NonNull String name) throws SqlException;
	
	/**
	 * Lists all indexes on this table.<br>
	 *
	 * @return A list of index information
	 * @throws SqlException If a database access error occurs
	 */
	@NonNull List<SqlIndexInfo> listIndexes() throws SqlException;
	
	/**
	 * Creates a select query for all columns of this table.<br>
	 * @return A select query returning full entities
	 */
	@NonNull SqlSelectQuery<T> select();
	
	/**
	 * Creates a typed select query for a single column.<br>
	 *
	 * @param e1 The expression to select
	 * @param <T1> The type of the column
	 * @return A select query returning values of the column type
	 */
	<T1> @NonNull SqlSelectProjectionQuery<T1> select(@NonNull SqlExpression<T1> e1);
	
	/**
	 * Creates a typed select query for two columns.<br>
	 *
	 * @param e1 The first expression
	 * @param e2 The second expression
	 * @param <T1> The type of the first column
	 * @param <T2> The type of the second column
	 * @return A select query returning {@link SqlRow2} tuples
	 */
	<T1, T2> @NonNull SqlSelectProjectionQuery<SqlRow2<T1, T2>> select(@NonNull SqlExpression<T1> e1, @NonNull SqlExpression<T2> e2);
	
	/**
	 * Creates a typed select query for three columns.<br>
	 *
	 * @param e1 The first expression
	 * @param e2 The second expression
	 * @param e3 The third expression
	 * @param <T1> The type of the first column
	 * @param <T2> The type of the second column
	 * @param <T3> The type of the third column
	 * @return A select query returning {@link SqlRow3} tuples
	 */
	<T1, T2, T3> @NonNull SqlSelectProjectionQuery<SqlRow3<T1, T2, T3>> select(@NonNull SqlExpression<T1> e1, @NonNull SqlExpression<T2> e2, @NonNull SqlExpression<T3> e3);
	
	/**
	 * Creates a typed select query for four columns.<br>
	 *
	 * @param e1 The first expression
	 * @param e2 The second expression
	 * @param e3 The third expression
	 * @param e4 The fourth expression
	 * @param <T1> The type of the first column
	 * @param <T2> The type of the second column
	 * @param <T3> The type of the third column
	 * @param <T4> The type of the fourth column
	 * @return A select query returning {@link SqlRow4} tuples
	 */
	<T1, T2, T3, T4> @NonNull SqlSelectProjectionQuery<SqlRow4<T1, T2, T3, T4>> select(@NonNull SqlExpression<T1> e1, @NonNull SqlExpression<T2> e2, @NonNull SqlExpression<T3> e3, @NonNull SqlExpression<T4> e4);
	
	/**
	 * Creates a typed select query for five columns.<br>
	 *
	 * @param e1 The first expression
	 * @param e2 The second expression
	 * @param e3 The third expression
	 * @param e4 The fourth expression
	 * @param e5 The fifth expression
	 * @param <T1> The type of the first column
	 * @param <T2> The type of the second column
	 * @param <T3> The type of the third column
	 * @param <T4> The type of the fourth column
	 * @param <T5> The type of the fifth column
	 * @return A select query returning {@link SqlRow5} tuples
	 */
	<T1, T2, T3, T4, T5> @NonNull SqlSelectProjectionQuery<SqlRow5<T1, T2, T3, T4, T5>> select(@NonNull SqlExpression<T1> e1, @NonNull SqlExpression<T2> e2, @NonNull SqlExpression<T3> e3, @NonNull SqlExpression<T4> e4, @NonNull SqlExpression<T5> e5);
	
	/**
	 * Creates a typed select query for six columns.<br>
	 *
	 * @param e1 The first expression
	 * @param e2 The second expression
	 * @param e3 The third expression
	 * @param e4 The fourth expression
	 * @param e5 The fifth expression
	 * @param e6 The sixth expression
	 * @param <T1> The type of the first column
	 * @param <T2> The type of the second column
	 * @param <T3> The type of the third column
	 * @param <T4> The type of the fourth column
	 * @param <T5> The type of the fifth column
	 * @param <T6> The type of the sixth column
	 * @return A select query returning {@link SqlRow6} tuples
	 */
	<T1, T2, T3, T4, T5, T6> @NonNull SqlSelectProjectionQuery<SqlRow6<T1, T2, T3, T4, T5, T6>> select(@NonNull SqlExpression<T1> e1, @NonNull SqlExpression<T2> e2, @NonNull SqlExpression<T3> e3, @NonNull SqlExpression<T4> e4, @NonNull SqlExpression<T5> e5, @NonNull SqlExpression<T6> e6);
	
	/**
	 * Creates a typed select query for seven columns.<br>
	 *
	 * @param e1 The first expression
	 * @param e2 The second expression
	 * @param e3 The third expression
	 * @param e4 The fourth expression
	 * @param e5 The fifth expression
	 * @param e6 The sixth expression
	 * @param e7 The seventh expression
	 * @param <T1> The type of the first column
	 * @param <T2> The type of the second column
	 * @param <T3> The type of the third column
	 * @param <T4> The type of the fourth column
	 * @param <T5> The type of the fifth column
	 * @param <T6> The type of the sixth column
	 * @param <T7> The type of the seventh column
	 * @return A select query returning {@link SqlRow7} tuples
	 */
	<T1, T2, T3, T4, T5, T6, T7> @NonNull SqlSelectProjectionQuery<SqlRow7<T1, T2, T3, T4, T5, T6, T7>> select(@NonNull SqlExpression<T1> e1, @NonNull SqlExpression<T2> e2, @NonNull SqlExpression<T3> e3, @NonNull SqlExpression<T4> e4, @NonNull SqlExpression<T5> e5, @NonNull SqlExpression<T6> e6, @NonNull SqlExpression<T7> e7);
	
	/**
	 * Creates a typed select query for eight columns.<br>
	 *
	 * @param e1 The first expression
	 * @param e2 The second expression
	 * @param e3 The third expression
	 * @param e4 The fourth expression
	 * @param e5 The fifth expression
	 * @param e6 The sixth expression
	 * @param e7 The seventh expression
	 * @param e8 The eighth expression
	 * @param <T1> The type of the first column
	 * @param <T2> The type of the second column
	 * @param <T3> The type of the third column
	 * @param <T4> The type of the fourth column
	 * @param <T5> The type of the fifth column
	 * @param <T6> The type of the sixth column
	 * @param <T7> The type of the seventh column
	 * @param <T8> The type of the eighth column
	 * @return A select query returning {@link SqlRow8} tuples
	 */
	<T1, T2, T3, T4, T5, T6, T7, T8> @NonNull SqlSelectProjectionQuery<SqlRow8<T1, T2, T3, T4, T5, T6, T7, T8>> select(@NonNull SqlExpression<T1> e1, @NonNull SqlExpression<T2> e2, @NonNull SqlExpression<T3> e3, @NonNull SqlExpression<T4> e4, @NonNull SqlExpression<T5> e5, @NonNull SqlExpression<T6> e6, @NonNull SqlExpression<T7> e7, @NonNull SqlExpression<T8> e8);
	
	/**
	 * Creates a typed select query for nine columns.<br>
	 *
	 * @param e1 The first expression
	 * @param e2 The second expression
	 * @param e3 The third expression
	 * @param e4 The fourth expression
	 * @param e5 The fifth expression
	 * @param e6 The sixth expression
	 * @param e7 The seventh expression
	 * @param e8 The eighth expression
	 * @param e9 The ninth expression
	 * @param <T1> The type of the first column
	 * @param <T2> The type of the second column
	 * @param <T3> The type of the third column
	 * @param <T4> The type of the fourth column
	 * @param <T5> The type of the fifth column
	 * @param <T6> The type of the sixth column
	 * @param <T7> The type of the seventh column
	 * @param <T8> The type of the eighth column
	 * @param <T9> The type of the ninth column
	 * @return A select query returning {@link SqlRow9} tuples
	 */
	<T1, T2, T3, T4, T5, T6, T7, T8, T9> @NonNull SqlSelectProjectionQuery<SqlRow9<T1, T2, T3, T4, T5, T6, T7, T8, T9>> select(@NonNull SqlExpression<T1> e1, @NonNull SqlExpression<T2> e2, @NonNull SqlExpression<T3> e3, @NonNull SqlExpression<T4> e4, @NonNull SqlExpression<T5> e5, @NonNull SqlExpression<T6> e6, @NonNull SqlExpression<T7> e7, @NonNull SqlExpression<T8> e8, @NonNull SqlExpression<T9> e9);
	
	/**
	 * Creates a typed select query for ten columns.<br>
	 *
	 * @param e1 The first expression
	 * @param e2 The second expression
	 * @param e3 The third expression
	 * @param e4 The fourth expression
	 * @param e5 The fifth expression
	 * @param e6 The sixth expression
	 * @param e7 The seventh expression
	 * @param e8 The eighth expression
	 * @param e9 The ninth expression
	 * @param e10 The tenth expression
	 * @param <T1> The type of the first column
	 * @param <T2> The type of the second column
	 * @param <T3> The type of the third column
	 * @param <T4> The type of the fourth column
	 * @param <T5> The type of the fifth column
	 * @param <T6> The type of the sixth column
	 * @param <T7> The type of the seventh column
	 * @param <T8> The type of the eighth column
	 * @param <T9> The type of the ninth column
	 * @param <T10> The type of the tenth column
	 * @return A select query returning {@link SqlRow10} tuples
	 */
	<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> @NonNull SqlSelectProjectionQuery<SqlRow10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>> select(@NonNull SqlExpression<T1> e1, @NonNull SqlExpression<T2> e2, @NonNull SqlExpression<T3> e3, @NonNull SqlExpression<T4> e4, @NonNull SqlExpression<T5> e5, @NonNull SqlExpression<T6> e6, @NonNull SqlExpression<T7> e7, @NonNull SqlExpression<T8> e8, @NonNull SqlExpression<T9> e9, @NonNull SqlExpression<T10> e10);
	
	/**
	 * Creates a typed select query for eleven columns.<br>
	 *
	 * @param e1 The first expression
	 * @param e2 The second expression
	 * @param e3 The third expression
	 * @param e4 The fourth expression
	 * @param e5 The fifth expression
	 * @param e6 The sixth expression
	 * @param e7 The seventh expression
	 * @param e8 The eighth expression
	 * @param e9 The ninth expression
	 * @param e10 The tenth expression
	 * @param e11 The eleventh expression
	 * @param <T1> The type of the first column
	 * @param <T2> The type of the second column
	 * @param <T3> The type of the third column
	 * @param <T4> The type of the fourth column
	 * @param <T5> The type of the fifth column
	 * @param <T6> The type of the sixth column
	 * @param <T7> The type of the seventh column
	 * @param <T8> The type of the eighth column
	 * @param <T9> The type of the ninth column
	 * @param <T10> The type of the tenth column
	 * @param <T11> The type of the eleventh column
	 * @return A select query returning {@link SqlRow11} tuples
	 */
	<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> @NonNull SqlSelectProjectionQuery<SqlRow11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> select(@NonNull SqlExpression<T1> e1, @NonNull SqlExpression<T2> e2, @NonNull SqlExpression<T3> e3, @NonNull SqlExpression<T4> e4, @NonNull SqlExpression<T5> e5, @NonNull SqlExpression<T6> e6, @NonNull SqlExpression<T7> e7, @NonNull SqlExpression<T8> e8, @NonNull SqlExpression<T9> e9, @NonNull SqlExpression<T10> e10, @NonNull SqlExpression<T11> e11);
	
	/**
	 * Creates a typed select query for twelve columns.<br>
	 *
	 * @param e1 The first expression
	 * @param e2 The second expression
	 * @param e3 The third expression
	 * @param e4 The fourth expression
	 * @param e5 The fifth expression
	 * @param e6 The sixth expression
	 * @param e7 The seventh expression
	 * @param e8 The eighth expression
	 * @param e9 The ninth expression
	 * @param e10 The tenth expression
	 * @param e11 The eleventh expression
	 * @param e12 The twelfth expression
	 * @param <T1> The type of the first column
	 * @param <T2> The type of the second column
	 * @param <T3> The type of the third column
	 * @param <T4> The type of the fourth column
	 * @param <T5> The type of the fifth column
	 * @param <T6> The type of the sixth column
	 * @param <T7> The type of the seventh column
	 * @param <T8> The type of the eighth column
	 * @param <T9> The type of the ninth column
	 * @param <T10> The type of the tenth column
	 * @param <T11> The type of the eleventh column
	 * @param <T12> The type of the twelfth column
	 * @return A select query returning {@link SqlRow12} tuples
	 */
	<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> @NonNull SqlSelectProjectionQuery<SqlRow12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>> select(@NonNull SqlExpression<T1> e1, @NonNull SqlExpression<T2> e2, @NonNull SqlExpression<T3> e3, @NonNull SqlExpression<T4> e4, @NonNull SqlExpression<T5> e5, @NonNull SqlExpression<T6> e6, @NonNull SqlExpression<T7> e7, @NonNull SqlExpression<T8> e8, @NonNull SqlExpression<T9> e9, @NonNull SqlExpression<T10> e10, @NonNull SqlExpression<T11> e11, @NonNull SqlExpression<T12> e12);
	
	/**
	 * Creates a typed select query for thirteen columns.<br>
	 *
	 * @param e1 The first expression
	 * @param e2 The second expression
	 * @param e3 The third expression
	 * @param e4 The fourth expression
	 * @param e5 The fifth expression
	 * @param e6 The sixth expression
	 * @param e7 The seventh expression
	 * @param e8 The eighth expression
	 * @param e9 The ninth expression
	 * @param e10 The tenth expression
	 * @param e11 The eleventh expression
	 * @param e12 The twelfth expression
	 * @param e13 The thirteenth expression
	 * @param <T1> The type of the first column
	 * @param <T2> The type of the second column
	 * @param <T3> The type of the third column
	 * @param <T4> The type of the fourth column
	 * @param <T5> The type of the fifth column
	 * @param <T6> The type of the sixth column
	 * @param <T7> The type of the seventh column
	 * @param <T8> The type of the eighth column
	 * @param <T9> The type of the ninth column
	 * @param <T10> The type of the tenth column
	 * @param <T11> The type of the eleventh column
	 * @param <T12> The type of the twelfth column
	 * @param <T13> The type of the thirteenth column
	 * @return A select query returning {@link SqlRow13} tuples
	 */
	<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> @NonNull SqlSelectProjectionQuery<SqlRow13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>> select(@NonNull SqlExpression<T1> e1, @NonNull SqlExpression<T2> e2, @NonNull SqlExpression<T3> e3, @NonNull SqlExpression<T4> e4, @NonNull SqlExpression<T5> e5, @NonNull SqlExpression<T6> e6, @NonNull SqlExpression<T7> e7, @NonNull SqlExpression<T8> e8, @NonNull SqlExpression<T9> e9, @NonNull SqlExpression<T10> e10, @NonNull SqlExpression<T11> e11, @NonNull SqlExpression<T12> e12, @NonNull SqlExpression<T13> e13);
	
	/**
	 * Creates a typed select query for fourteen columns.<br>
	 *
	 * @param e1 The first expression
	 * @param e2 The second expression
	 * @param e3 The third expression
	 * @param e4 The fourth expression
	 * @param e5 The fifth expression
	 * @param e6 The sixth expression
	 * @param e7 The seventh expression
	 * @param e8 The eighth expression
	 * @param e9 The ninth expression
	 * @param e10 The tenth expression
	 * @param e11 The eleventh expression
	 * @param e12 The twelfth expression
	 * @param e13 The thirteenth expression
	 * @param e14 The fourteenth expression
	 * @param <T1> The type of the first column
	 * @param <T2> The type of the second column
	 * @param <T3> The type of the third column
	 * @param <T4> The type of the fourth column
	 * @param <T5> The type of the fifth column
	 * @param <T6> The type of the sixth column
	 * @param <T7> The type of the seventh column
	 * @param <T8> The type of the eighth column
	 * @param <T9> The type of the ninth column
	 * @param <T10> The type of the tenth column
	 * @param <T11> The type of the eleventh column
	 * @param <T12> The type of the twelfth column
	 * @param <T13> The type of the thirteenth column
	 * @param <T14> The type of the fourteenth column
	 * @return A select query returning {@link SqlRow14} tuples
	 */
	<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> @NonNull SqlSelectProjectionQuery<SqlRow14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14>> select(@NonNull SqlExpression<T1> e1, @NonNull SqlExpression<T2> e2, @NonNull SqlExpression<T3> e3, @NonNull SqlExpression<T4> e4, @NonNull SqlExpression<T5> e5, @NonNull SqlExpression<T6> e6, @NonNull SqlExpression<T7> e7, @NonNull SqlExpression<T8> e8, @NonNull SqlExpression<T9> e9, @NonNull SqlExpression<T10> e10, @NonNull SqlExpression<T11> e11, @NonNull SqlExpression<T12> e12, @NonNull SqlExpression<T13> e13, @NonNull SqlExpression<T14> e14);
	
	/**
	 * Creates a typed select query for fifteen columns.<br>
	 *
	 * @param e1 The first expression
	 * @param e2 The second expression
	 * @param e3 The third expression
	 * @param e4 The fourth expression
	 * @param e5 The fifth expression
	 * @param e6 The sixth expression
	 * @param e7 The seventh expression
	 * @param e8 The eighth expression
	 * @param e9 The ninth expression
	 * @param e10 The tenth expression
	 * @param e11 The eleventh expression
	 * @param e12 The twelfth expression
	 * @param e13 The thirteenth expression
	 * @param e14 The fourteenth expression
	 * @param e15 The fifteenth expression
	 * @param <T1> The type of the first column
	 * @param <T2> The type of the second column
	 * @param <T3> The type of the third column
	 * @param <T4> The type of the fourth column
	 * @param <T5> The type of the fifth column
	 * @param <T6> The type of the sixth column
	 * @param <T7> The type of the seventh column
	 * @param <T8> The type of the eighth column
	 * @param <T9> The type of the ninth column
	 * @param <T10> The type of the tenth column
	 * @param <T11> The type of the eleventh column
	 * @param <T12> The type of the twelfth column
	 * @param <T13> The type of the thirteenth column
	 * @param <T14> The type of the fourteenth column
	 * @param <T15> The type of the fifteenth column
	 * @return A select query returning {@link SqlRow15} tuples
	 */
	<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> @NonNull SqlSelectProjectionQuery<SqlRow15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15>> select(@NonNull SqlExpression<T1> e1, @NonNull SqlExpression<T2> e2, @NonNull SqlExpression<T3> e3, @NonNull SqlExpression<T4> e4, @NonNull SqlExpression<T5> e5, @NonNull SqlExpression<T6> e6, @NonNull SqlExpression<T7> e7, @NonNull SqlExpression<T8> e8, @NonNull SqlExpression<T9> e9, @NonNull SqlExpression<T10> e10, @NonNull SqlExpression<T11> e11, @NonNull SqlExpression<T12> e12, @NonNull SqlExpression<T13> e13, @NonNull SqlExpression<T14> e14, @NonNull SqlExpression<T15> e15);
	
	/**
	 * Creates a typed select query for sixteen columns.<br>
	 *
	 * @param e1 The first expression
	 * @param e2 The second expression
	 * @param e3 The third expression
	 * @param e4 The fourth expression
	 * @param e5 The fifth expression
	 * @param e6 The sixth expression
	 * @param e7 The seventh expression
	 * @param e8 The eighth expression
	 * @param e9 The ninth expression
	 * @param e10 The tenth expression
	 * @param e11 The eleventh expression
	 * @param e12 The twelfth expression
	 * @param e13 The thirteenth expression
	 * @param e14 The fourteenth expression
	 * @param e15 The fifteenth expression
	 * @param e16 The sixteenth expression
	 * @param <T1> The type of the first column
	 * @param <T2> The type of the second column
	 * @param <T3> The type of the third column
	 * @param <T4> The type of the fourth column
	 * @param <T5> The type of the fifth column
	 * @param <T6> The type of the sixth column
	 * @param <T7> The type of the seventh column
	 * @param <T8> The type of the eighth column
	 * @param <T9> The type of the ninth column
	 * @param <T10> The type of the tenth column
	 * @param <T11> The type of the eleventh column
	 * @param <T12> The type of the twelfth column
	 * @param <T13> The type of the thirteenth column
	 * @param <T14> The type of the fourteenth column
	 * @param <T15> The type of the fifteenth column
	 * @param <T16> The type of the sixteenth column
	 * @return A select query returning {@link SqlRow16} tuples
	 */
	<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> @NonNull SqlSelectProjectionQuery<SqlRow16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16>> select(@NonNull SqlExpression<T1> e1, @NonNull SqlExpression<T2> e2, @NonNull SqlExpression<T3> e3, @NonNull SqlExpression<T4> e4, @NonNull SqlExpression<T5> e5, @NonNull SqlExpression<T6> e6, @NonNull SqlExpression<T7> e7, @NonNull SqlExpression<T8> e8, @NonNull SqlExpression<T9> e9, @NonNull SqlExpression<T10> e10, @NonNull SqlExpression<T11> e11, @NonNull SqlExpression<T12> e12, @NonNull SqlExpression<T13> e13, @NonNull SqlExpression<T14> e14, @NonNull SqlExpression<T15> e15, @NonNull SqlExpression<T16> e16);
	
	/**
	 * Creates a select query for the specified expressions (columns, aggregates, functions).<br>
	 * Supports columns, aliased columns via {@link SqlColumn#as(String)}, and SQL expressions like aggregates ({@link SqlAgg}).<br>
	 *
	 * @param expressions The expressions to select
	 * @return A projection query returning the selected values
	 */
	@NonNull SqlSelectProjectionQuery<?> select(SqlExpression<?> @NonNull ... expressions);
	
	/**
	 * Creates a subquery selecting the specified expressions.<br>
	 * Subqueries can be used in {@code IN} conditions or {@code EXISTS} checks.<br>
	 *
	 * @param expressions The expressions to select
	 * @return A select query for use as a subquery
	 */
	@NonNull SqlSelectQuery<?> subquery(SqlExpression<?> @NonNull ... expressions);
	
	/**
	 * Adds an entity to be inserted.<br>
	 * Generates SQL: {@code INSERT INTO table (...) VALUES (...)}.<br>
	 *
	 * @param entity The entity to insert
	 * @return This insert query
	 */
	@NonNull SqlInsertQuery<T> insert(@NonNull T entity);
	
	/**
	 * Adds multiple entities to be inserted.<br>
	 * Generates SQL: {@code INSERT INTO table (...) VALUES (...), (...)}.<br>
	 *
	 * @param entities The entities to insert
	 * @return This insert query
	 */
	@SuppressWarnings("unchecked")
	@NonNull SqlInsertQuery<T> insert(T @NonNull ... entities);
	
	/**
	 * Adds multiple entities to be inserted.<br>
	 * Generates SQL: {@code INSERT INTO table (...) VALUES (...), (...)}.<br>
	 *
	 * @param entities The entities to insert
	 * @return This insert query
	 */
	@NonNull SqlInsertQuery<T> insert(@NonNull Collection<T> entities);
	
	/**
	 * Adds multiple entities to be inserted in batches.<br>
	 * Generates SQL: {@code INSERT INTO table (...) VALUES (...), (...)} for each batch of entities.<br>
	 *
	 * @param entities The entities to insert
	 * @param batchSize The size of each batch
	 * @return This insert query
	 */
	@NonNull SqlInsertQuery<T> insert(@NonNull Collection<T> entities, int batchSize);
	
	/**
	 * Adds an entity to be upserted.<br>
	 * Generates SQL: {@code INSERT INTO table (...) VALUES (...) ON CONFLICT (column) DO UPDATE SET ...}.<br>
	 *
	 * @param entity The entity to upsert
	 * @param conflictColumn The column that may cause a conflict
	 * @param onConflict The function to apply on conflict
	 * @return This insert query
	 */
	@NonNull SqlInsertQuery<T> upsert(@NonNull T entity, @NonNull SqlColumn<?> conflictColumn, @NonNull Function<T, T> onConflict);
	
	/**
	 * Adds an entity to be inserted, ignoring conflicts.<br>
	 * Generates SQL: {@code INSERT OR IGNORE INTO table (...) VALUES (...)}.<br>
	 *
	 * @param entity The entity to insert
	 * @param conflictColumns The columns that may cause a conflict
	 * @return This insert query
	 */
	@NonNull SqlInsertQuery<T> insertOrIgnore(@NonNull T entity, SqlColumn<?> @NonNull ... conflictColumns);
	
	/**
	 * Inserts data from a select query.<br>
	 * Generates SQL: {@code INSERT INTO table (...) SELECT ...}.<br>
	 *
	 * @param query The select query to insert from
	 * @return This insert query
	 */
	@NonNull SqlInsertQuery<T> insertFromSelect(@NonNull SqlSelectQuery<?> query);
	
	/**
	 * Creates an update query builder for this table.<br>
	 * @return An update query builder
	 */
	@NonNull SqlUpdateQuery<T> update();
	
	/**
	 * Creates a delete query builder for this table.<br>
	 * @return A delete query builder
	 */
	@NonNull SqlDeleteQuery<T> delete();
}
