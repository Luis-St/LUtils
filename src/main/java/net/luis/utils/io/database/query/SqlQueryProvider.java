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

import net.luis.utils.function.throwable.ThrowableFunction;
import net.luis.utils.io.database.SqlConnectionSource;
import net.luis.utils.io.database.SqlSession;
import net.luis.utils.io.database.audit.*;
import net.luis.utils.io.database.dialect.SqlDialect;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.exception.database.SqlResultMappingException;
import net.luis.utils.io.database.expression.SqlExpression;
import net.luis.utils.io.database.query.crud.*;
import net.luis.utils.io.database.query.row.*;
import net.luis.utils.io.database.table.SqlColumn;
import net.luis.utils.io.database.table.SqlTable;
import net.luis.utils.io.database.type.SqlType;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InaccessibleObjectException;
import java.sql.ResultSet;
import java.time.Duration;
import java.util.*;

/**
 * Entry point for building sql queries against a single {@link SqlTable table}.<br>
 * Provides factory methods for the supported crud operations, each of which returns a fluent query object
 * that can be further configured and executed:<br>
 * <ul>
 *     <li>{@link #select()} and its overloads for read queries</li>
 *     <li>{@link #insert(Object)} and its overloads for insert queries</li>
 *     <li>{@link #update()} for update queries</li>
 *     <li>{@link #delete()} for delete queries</li>
 * </ul>
 * The provider also builds the row mappers that convert a {@link ResultSet} into entities of type {@code E},
 * including the audit tracking wiring when the table is audited and a {@link SqlSession session} is present.<br>
 *
 * @see SqlSelectQuery
 * @see SqlInsertQuery
 * @see SqlUpdateQuery
 * @see SqlDeleteQuery
 *
 * @author Luis-St
 *
 * @param <E> The entity type managed by the underlying table
 */
@SuppressWarnings("unchecked")
public class SqlQueryProvider<E> {
	
	/**
	 * The table the queries are built for.
	 */
	private final SqlTable<E> table;
	/**
	 * The dialect used to render and execute the queries.
	 */
	private final SqlDialect dialect;
	/**
	 * The connection source used to obtain database connections.
	 */
	private final SqlConnectionSource connectionSource;
	/**
	 * The timeout applied to the built queries.
	 */
	private final Duration queryTimeout;
	/**
	 * The row mapper that maps a result set row to a full entity of type {@code E}.
	 */
	private final ThrowableFunction<ResultSet, E, SqlException> entityRowMapper;
	/**
	 * The row mapper used for select queries, which wraps the {@link #entityRowMapper} with audit tracking when applicable.
	 */
	private final ThrowableFunction<ResultSet, E, SqlException> selectRowMapper;
	/**
	 * The provider of the current audit user, or {@code null} if auditing is not configured.
	 */
	private final SqlAuditUserProvider auditUserProvider;
	
	/**
	 * Constructs a new query provider without auditing and session tracking.<br>
	 *
	 * @param table The table the queries are built for
	 * @param dialect The dialect used to render and execute the queries
	 * @param connectionSource The connection source used to obtain database connections
	 * @param queryTimeout The timeout applied to the built queries
	 * @throws NullPointerException If any of the arguments is null
	 */
	public SqlQueryProvider(@NonNull SqlTable<E> table, @NonNull SqlDialect dialect, @NonNull SqlConnectionSource connectionSource, @NonNull Duration queryTimeout) {
		this(table, dialect, connectionSource, queryTimeout, null, null);
	}
	
	/**
	 * Constructs a new query provider with optional auditing and session tracking.<br>
	 * If the table is audited and a session is given, select queries additionally read the audit metadata
	 * from the result set and track the loaded entities in the session.<br>
	 *
	 * @param table The table the queries are built for
	 * @param dialect The dialect used to render and execute the queries
	 * @param connectionSource The connection source used to obtain database connections
	 * @param queryTimeout The timeout applied to the built queries
	 * @param auditUserProvider The provider of the current audit user, or {@code null} if auditing is not configured
	 * @param session The session used to track loaded entities, or {@code null} if no tracking is required
	 * @throws NullPointerException If the table, dialect, connection source or query timeout is null
	 */
	public SqlQueryProvider(
		@NonNull SqlTable<E> table,
		@NonNull SqlDialect dialect,
		@NonNull SqlConnectionSource connectionSource,
		@NonNull Duration queryTimeout,
		@Nullable SqlAuditUserProvider auditUserProvider,
		@Nullable SqlSession session
	) {
		this.table = Objects.requireNonNull(table, "Sql table must not be null");
		this.dialect = Objects.requireNonNull(dialect, "Sql dialect must not be null");
		this.connectionSource = Objects.requireNonNull(connectionSource, "Sql connection source must not be null");
		this.queryTimeout = Objects.requireNonNull(queryTimeout, "Query timeout must not be null");
		this.auditUserProvider = auditUserProvider;
		this.entityRowMapper = createRowMapper(table, dialect);
		this.selectRowMapper = createSelectRowMapper(table, this.entityRowMapper, session, dialect);
	}
	
	//region Row mapper creation
	
	/**
	 * Creates the row mapper used for select queries.<br>
	 * If the session is {@code null} or the table is not audited, the given entity row mapper is returned unchanged.<br>
	 * Otherwise the returned mapper additionally reads the audit metadata from the result set and tracks the loaded entity in the session.<br>
	 *
	 * @param table The table the queries are built for
	 * @param entityRowMapper The mapper that maps a result set row to a full entity
	 * @param session The session used to track loaded entities, or {@code null} if no tracking is required
	 * @param dialect The dialect used to read the audit metadata
	 * @param <E> The entity type
	 * @return The select row mapper
	 * @throws NullPointerException If the table, entity row mapper or dialect is null
	 */
	private static <E> @NonNull ThrowableFunction<ResultSet, E, SqlException> createSelectRowMapper(
		@NonNull SqlTable<E> table,
		@NonNull ThrowableFunction<ResultSet, E, SqlException> entityRowMapper,
		@Nullable SqlSession session,
		@NonNull SqlDialect dialect
	) {
		Objects.requireNonNull(table, "Sql table must not be null");
		Objects.requireNonNull(entityRowMapper, "Entity row mapper must not be null");
		Objects.requireNonNull(dialect, "Sql dialect must not be null");
		if (session == null || !table.isAudited()) {
			return entityRowMapper;
		}
		
		int entityColumnCount = table.columns().size();
		SqlAuditConfig config = table.auditConfig().orElseThrow();
		return resultSet -> {
			E entity = entityRowMapper.apply(resultSet);
			SqlAuditMetadata metadata = SqlAuditMetadata.readFrom(dialect, resultSet, entityColumnCount + 1, config);
			session.track(table, entity, metadata);
			return entity;
		};
	}
	
	/**
	 * Creates the entity row mapper for the given table.<br>
	 * The mapper resolves the column values in column index order and invokes the matching constructor of the entity type.<br>
	 *
	 * @param table The table whose entities are mapped
	 * @param dialect The dialect used to read the column values
	 * @param <E> The entity type
	 * @return The entity row mapper
	 * @throws NullPointerException If the table or dialect is null
	 * @throws IllegalStateException If no matching constructor can be found or the matching constructor cannot be accessed
	 */
	private static <E> @NonNull ThrowableFunction<ResultSet, E, SqlException> createRowMapper(@NonNull SqlTable<E> table, @NonNull SqlDialect dialect) {
		Objects.requireNonNull(table, "Sql table must not be null");
		Objects.requireNonNull(dialect, "Sql dialect must not be null");
		
		List<SqlColumn<E, ?>> sorted = table.columns().stream().sorted(Comparator.comparingInt(SqlColumn::index)).toList();
		Constructor<E> constructor = findMatchingConstructor(table.type(), sorted);
		
		return resultSet -> {
			Object[] args = new Object[sorted.size()];
			for (int i = 0; i < sorted.size(); i++) {
				args[i] = SqlType.getValue(sorted.get(i).type(), dialect, resultSet, i + 1);
			}
			
			try {
				return constructor.newInstance(args);
			} catch (Exception e) {
				throw new SqlResultMappingException("Failed to construct entity " + table.type().getSimpleName() + " from result set", e, table.type());
			}
		};
	}
	
	/**
	 * Checks whether a value of the source type can be assigned to the target type, taking primitive boxing and unboxing into account.<br>
	 *
	 * @param target The type to assign to
	 * @param source The type to assign from
	 * @return {@code true} if the source type is assignable to the target type, {@code false} otherwise
	 * @throws NullPointerException If the target or source class is null
	 */
	private static boolean isAssignableWithUnboxing(@NonNull Class<?> target, @NonNull Class<?> source) {
		Objects.requireNonNull(target, "Target class must not be null");
		Objects.requireNonNull(source, "Source class must not be null");
		
		if (target.isAssignableFrom(source)) {
			return true;
		}
		if (target.isPrimitive() && source.isPrimitive()) {
			return target == source;
		}
		if (target.isPrimitive() && isPrimitiveAssignableTo(target, source)) {
			return true;
		}
		return source.isPrimitive() && isPrimitiveAssignableTo(source, target);
	}
	
	/**
	 * Checks whether the given primitive type and the given other type represent the same type via boxing.<br>
	 *
	 * @param primitive The primitive type to check
	 * @param other The type that is expected to be the boxed counterpart
	 * @return {@code true} if the other type is the wrapper type of the given primitive type, {@code false} otherwise
	 * @throws NullPointerException If the primitive or other class is null
	 * @throws IllegalArgumentException If the first class is not a primitive type
	 */
	private static boolean isPrimitiveAssignableTo(@NonNull Class<?> primitive, @NonNull Class<?> other) {
		Objects.requireNonNull(primitive, "Primitive class must not be null");
		Objects.requireNonNull(other, "Other class must not be null");
		if (!primitive.isPrimitive()) {
			throw new IllegalArgumentException("First class must be primitive");
		}
		
		return primitive == boolean.class && other == Boolean.class
			|| primitive == byte.class && other == Byte.class
			|| primitive == char.class && other == Character.class
			|| primitive == short.class && other == Short.class
			|| primitive == int.class && other == Integer.class
			|| primitive == long.class && other == Long.class
			|| primitive == float.class && other == Float.class
			|| primitive == double.class && other == Double.class;
	}
	
	/**
	 * Finds the declared constructor of the entity type whose parameters match the given columns.<br>
	 * A constructor matches if its parameter count equals the column count and every parameter type is assignable
	 * from the corresponding column java type, taking boxing and unboxing into account.<br>
	 * The matching constructor is made accessible before it is returned.<br>
	 *
	 * @param type The entity type whose constructor is searched
	 * @param sortedColumns The columns sorted by their index
	 * @param <E> The entity type
	 * @return The matching constructor
	 * @throws NullPointerException If the type or sorted columns is null
	 * @throws IllegalStateException If the matching constructor cannot be accessed or no matching constructor is found
	 */
	@SuppressWarnings("unchecked")
	private static <E> @NonNull Constructor<E> findMatchingConstructor(@NonNull Class<E> type, @NonNull List<SqlColumn<E, ?>> sortedColumns) {
		Objects.requireNonNull(type, "Entity type must not be null");
		Objects.requireNonNull(sortedColumns, "Sorted columns must not be null");
		
		Constructor<?>[] constructors = type.getDeclaredConstructors();
		
		for (Constructor<?> constructor : constructors) {
			if (constructor.getParameterCount() == sortedColumns.size()) {
				boolean matches = true;
				Class<?>[] paramTypes = constructor.getParameterTypes();
				
				for (int i = 0; i < paramTypes.length; i++) {
					Class<?> columnType = sortedColumns.get(i).type().javaType();
					if (!isAssignableWithUnboxing(paramTypes[i], columnType)) {
						matches = false;
						break;
					}
				}
				
				if (matches) {
					try {
						constructor.setAccessible(true);
					} catch (InaccessibleObjectException e) {
						throw new IllegalStateException("Cannot access matching constructor on " + type.getSimpleName() + ", if it lives in a named module, open its package to LUtils (e.g. 'opens your.package;' in module-info.java)", e);
					}
					return (Constructor<E>) constructor;
				}
			}
		}
		throw new IllegalStateException("No matching constructor found on " + type.getSimpleName() + " for " + sortedColumns.size() + " table columns");
	}
	//endregion
	
	//region Select query
	
	/**
	 * Ensures that none of the given expressions is null.<br>
	 *
	 * @param expressions The expressions to check
	 * @throws NullPointerException If any of the expressions is null
	 */
	private void ensureNotNull(SqlExpression<?> @NonNull ... expressions) {
		for (SqlExpression<?> expression : expressions) {
			Objects.requireNonNull(expression, "Sql expression must not be null");
		}
	}
	
	/**
	 * Creates a select query that projects the given expressions onto a row type.<br>
	 * The values of the expressions are mapped into an instance of the given row type using a {@link SqlRowMapper}.<br>
	 *
	 * @param rowType The row type the selected expressions are mapped to
	 * @param expressions The expressions to select
	 * @param <R> The row type produced by the query
	 * @return A select query producing rows of the given type
	 * @throws NullPointerException If the row type, the expression array or any of the expressions is null
	 */
	private <R> @NonNull SqlSelectQuery<R> selectWithExpressions(@NonNull Class<R> rowType, SqlExpression<?> @NonNull ... expressions) {
		Objects.requireNonNull(rowType, "Row type must not be null");
		Objects.requireNonNull(expressions, "Sql expressions must not be null");
		this.ensureNotNull(expressions);
		
		List<SqlExpression<?>> expressionList = List.of(expressions);
		return new SqlSelectQuery<>(this.table, this.dialect, this.connectionSource, this.queryTimeout, SqlRowMapper.forExpressions(rowType, expressionList, this.dialect), expressionList);
	}
	
	/**
	 * Creates a select query that returns full entities of type {@code E}.<br>
	 * The rows are mapped using the select row mapper, which includes audit tracking when applicable.<br>
	 * @return A select query producing entities of type {@code E}
	 */
	public @NonNull SqlSelectQuery<E> select() {
		return new SqlSelectQuery<>(this.table, this.dialect, this.connectionSource, this.queryTimeout, this.selectRowMapper);
	}
	
	/**
	 * Creates a select query that returns the value of the given single expression.<br>
	 *
	 * @param e1 The expression to select
	 * @param <E1> The result type of the expression
	 * @return A select query producing the value of the expression
	 * @throws NullPointerException If the expression is null
	 */
	public <E1> @NonNull SqlSelectQuery<E1> select(@NonNull SqlExpression<E1> e1) {
		Objects.requireNonNull(e1, "Sql expression must not be null");
		
		ThrowableFunction<ResultSet, E1, SqlException> mapper = rs -> {
			return SqlType.getValue(e1.type(), this.dialect, rs, 1);
		};
		return new SqlSelectQuery<>(this.table, this.dialect, this.connectionSource, this.queryTimeout, mapper, List.of(e1));
	}
	
	/**
	 * Creates a select query that returns the values of the given two expressions as a {@link SqlRow2 row}.<br>
	 *
	 * @param e1 The first expression to select
	 * @param e2 The second expression to select
	 * @param <E1> The result type of the first expression
	 * @param <E2> The result type of the second expression
	 * @return A select query producing rows of the selected values
	 * @throws NullPointerException If any of the expressions is null
	 */
	public <E1, E2> @NonNull SqlSelectQuery<SqlRow2<E1, E2>> select(@NonNull SqlExpression<E1> e1, @NonNull SqlExpression<E2> e2) {
		return this.selectWithExpressions((Class<SqlRow2<E1, E2>>) (Class<?>) SqlRow2.class, e1, e2);
	}
	
	/**
	 * Creates a select query that returns the values of the given three expressions as a {@link SqlRow3 row}.<br>
	 *
	 * @param e1 The first expression to select
	 * @param e2 The second expression to select
	 * @param e3 The third expression to select
	 * @param <E1> The result type of the first expression
	 * @param <E2> The result type of the second expression
	 * @param <E3> The result type of the third expression
	 * @return A select query producing rows of the selected values
	 * @throws NullPointerException If any of the expressions is null
	 */
	public <E1, E2, E3> @NonNull SqlSelectQuery<SqlRow3<E1, E2, E3>> select(@NonNull SqlExpression<E1> e1, @NonNull SqlExpression<E2> e2, @NonNull SqlExpression<E3> e3) {
		return this.selectWithExpressions((Class<SqlRow3<E1, E2, E3>>) (Class<?>) SqlRow3.class, e1, e2, e3);
	}
	
	/**
	 * Creates a select query that returns the values of the given four expressions as a {@link SqlRow4 row}.<br>
	 *
	 * @param e1 The first expression to select
	 * @param e2 The second expression to select
	 * @param e3 The third expression to select
	 * @param e4 The fourth expression to select
	 * @param <E1> The result type of the first expression
	 * @param <E2> The result type of the second expression
	 * @param <E3> The result type of the third expression
	 * @param <E4> The result type of the fourth expression
	 * @return A select query producing rows of the selected values
	 * @throws NullPointerException If any of the expressions is null
	 */
	public <E1, E2, E3, E4> @NonNull SqlSelectQuery<SqlRow4<E1, E2, E3, E4>> select(
		@NonNull SqlExpression<E1> e1,
		@NonNull SqlExpression<E2> e2,
		@NonNull SqlExpression<E3> e3,
		@NonNull SqlExpression<E4> e4
	) {
		return this.selectWithExpressions((Class<SqlRow4<E1, E2, E3, E4>>) (Class<?>) SqlRow4.class, e1, e2, e3, e4);
	}
	
	/**
	 * Creates a select query that returns the values of the given five expressions as a {@link SqlRow5 row}.<br>
	 *
	 * @param e1 The first expression to select
	 * @param e2 The second expression to select
	 * @param e3 The third expression to select
	 * @param e4 The fourth expression to select
	 * @param e5 The fifth expression to select
	 * @param <E1> The result type of the first expression
	 * @param <E2> The result type of the second expression
	 * @param <E3> The result type of the third expression
	 * @param <E4> The result type of the fourth expression
	 * @param <E5> The result type of the fifth expression
	 * @return A select query producing rows of the selected values
	 * @throws NullPointerException If any of the expressions is null
	 */
	public <E1, E2, E3, E4, E5> @NonNull SqlSelectQuery<SqlRow5<E1, E2, E3, E4, E5>> select(
		@NonNull SqlExpression<E1> e1,
		@NonNull SqlExpression<E2> e2,
		@NonNull SqlExpression<E3> e3,
		@NonNull SqlExpression<E4> e4,
		@NonNull SqlExpression<E5> e5
	) {
		return this.selectWithExpressions((Class<SqlRow5<E1, E2, E3, E4, E5>>) (Class<?>) SqlRow5.class, e1, e2, e3, e4, e5);
	}
	
	/**
	 * Creates a select query that returns the values of the given six expressions as a {@link SqlRow6 row}.<br>
	 *
	 * @param e1 The first expression to select
	 * @param e2 The second expression to select
	 * @param e3 The third expression to select
	 * @param e4 The fourth expression to select
	 * @param e5 The fifth expression to select
	 * @param e6 The sixth expression to select
	 * @param <E1> The result type of the first expression
	 * @param <E2> The result type of the second expression
	 * @param <E3> The result type of the third expression
	 * @param <E4> The result type of the fourth expression
	 * @param <E5> The result type of the fifth expression
	 * @param <E6> The result type of the sixth expression
	 * @return A select query producing rows of the selected values
	 * @throws NullPointerException If any of the expressions is null
	 */
	public <E1, E2, E3, E4, E5, E6> @NonNull SqlSelectQuery<SqlRow6<E1, E2, E3, E4, E5, E6>> select(
		@NonNull SqlExpression<E1> e1,
		@NonNull SqlExpression<E2> e2,
		@NonNull SqlExpression<E3> e3,
		@NonNull SqlExpression<E4> e4,
		@NonNull SqlExpression<E5> e5,
		@NonNull SqlExpression<E6> e6
	) {
		return this.selectWithExpressions((Class<SqlRow6<E1, E2, E3, E4, E5, E6>>) (Class<?>) SqlRow6.class, e1, e2, e3, e4, e5, e6);
	}
	
	/**
	 * Creates a select query that returns the values of the given seven expressions as a {@link SqlRow7 row}.<br>
	 *
	 * @param e1 The first expression to select
	 * @param e2 The second expression to select
	 * @param e3 The third expression to select
	 * @param e4 The fourth expression to select
	 * @param e5 The fifth expression to select
	 * @param e6 The sixth expression to select
	 * @param e7 The seventh expression to select
	 * @param <E1> The result type of the first expression
	 * @param <E2> The result type of the second expression
	 * @param <E3> The result type of the third expression
	 * @param <E4> The result type of the fourth expression
	 * @param <E5> The result type of the fifth expression
	 * @param <E6> The result type of the sixth expression
	 * @param <E7> The result type of the seventh expression
	 * @return A select query producing rows of the selected values
	 * @throws NullPointerException If any of the expressions is null
	 */
	public <E1, E2, E3, E4, E5, E6, E7> @NonNull SqlSelectQuery<SqlRow7<E1, E2, E3, E4, E5, E6, E7>> select(
		@NonNull SqlExpression<E1> e1,
		@NonNull SqlExpression<E2> e2,
		@NonNull SqlExpression<E3> e3,
		@NonNull SqlExpression<E4> e4,
		@NonNull SqlExpression<E5> e5,
		@NonNull SqlExpression<E6> e6,
		@NonNull SqlExpression<E7> e7
	) {
		return this.selectWithExpressions((Class<SqlRow7<E1, E2, E3, E4, E5, E6, E7>>) (Class<?>) SqlRow7.class, e1, e2, e3, e4, e5, e6, e7);
	}
	
	/**
	 * Creates a select query that returns the values of the given eight expressions as a {@link SqlRow8 row}.<br>
	 *
	 * @param e1 The first expression to select
	 * @param e2 The second expression to select
	 * @param e3 The third expression to select
	 * @param e4 The fourth expression to select
	 * @param e5 The fifth expression to select
	 * @param e6 The sixth expression to select
	 * @param e7 The seventh expression to select
	 * @param e8 The eighth expression to select
	 * @param <E1> The result type of the first expression
	 * @param <E2> The result type of the second expression
	 * @param <E3> The result type of the third expression
	 * @param <E4> The result type of the fourth expression
	 * @param <E5> The result type of the fifth expression
	 * @param <E6> The result type of the sixth expression
	 * @param <E7> The result type of the seventh expression
	 * @param <E8> The result type of the eighth expression
	 * @return A select query producing rows of the selected values
	 * @throws NullPointerException If any of the expressions is null
	 */
	public <E1, E2, E3, E4, E5, E6, E7, E8> @NonNull SqlSelectQuery<SqlRow8<E1, E2, E3, E4, E5, E6, E7, E8>> select(
		@NonNull SqlExpression<E1> e1,
		@NonNull SqlExpression<E2> e2,
		@NonNull SqlExpression<E3> e3,
		@NonNull SqlExpression<E4> e4,
		@NonNull SqlExpression<E5> e5,
		@NonNull SqlExpression<E6> e6,
		@NonNull SqlExpression<E7> e7,
		@NonNull SqlExpression<E8> e8
	) {
		return this.selectWithExpressions((Class<SqlRow8<E1, E2, E3, E4, E5, E6, E7, E8>>) (Class<?>) SqlRow8.class, e1, e2, e3, e4, e5, e6, e7, e8);
	}
	
	/**
	 * Creates a select query that returns the values of the given nine expressions as a {@link SqlRow9 row}.<br>
	 *
	 * @param e1 The first expression to select
	 * @param e2 The second expression to select
	 * @param e3 The third expression to select
	 * @param e4 The fourth expression to select
	 * @param e5 The fifth expression to select
	 * @param e6 The sixth expression to select
	 * @param e7 The seventh expression to select
	 * @param e8 The eighth expression to select
	 * @param e9 The ninth expression to select
	 * @param <E1> The result type of the first expression
	 * @param <E2> The result type of the second expression
	 * @param <E3> The result type of the third expression
	 * @param <E4> The result type of the fourth expression
	 * @param <E5> The result type of the fifth expression
	 * @param <E6> The result type of the sixth expression
	 * @param <E7> The result type of the seventh expression
	 * @param <E8> The result type of the eighth expression
	 * @param <E9> The result type of the ninth expression
	 * @return A select query producing rows of the selected values
	 * @throws NullPointerException If any of the expressions is null
	 */
	public <E1, E2, E3, E4, E5, E6, E7, E8, E9> @NonNull SqlSelectQuery<SqlRow9<E1, E2, E3, E4, E5, E6, E7, E8, E9>> select(
		@NonNull SqlExpression<E1> e1,
		@NonNull SqlExpression<E2> e2,
		@NonNull SqlExpression<E3> e3,
		@NonNull SqlExpression<E4> e4,
		@NonNull SqlExpression<E5> e5,
		@NonNull SqlExpression<E6> e6,
		@NonNull SqlExpression<E7> e7,
		@NonNull SqlExpression<E8> e8,
		@NonNull SqlExpression<E9> e9
	) {
		return this.selectWithExpressions((Class<SqlRow9<E1, E2, E3, E4, E5, E6, E7, E8, E9>>) (Class<?>) SqlRow9.class, e1, e2, e3, e4, e5, e6, e7, e8, e9);
	}
	
	/**
	 * Creates a select query that returns the values of the given ten expressions as a {@link SqlRow10 row}.<br>
	 *
	 * @param e1 The first expression to select
	 * @param e2 The second expression to select
	 * @param e3 The third expression to select
	 * @param e4 The fourth expression to select
	 * @param e5 The fifth expression to select
	 * @param e6 The sixth expression to select
	 * @param e7 The seventh expression to select
	 * @param e8 The eighth expression to select
	 * @param e9 The ninth expression to select
	 * @param e10 The tenth expression to select
	 * @param <E1> The result type of the first expression
	 * @param <E2> The result type of the second expression
	 * @param <E3> The result type of the third expression
	 * @param <E4> The result type of the fourth expression
	 * @param <E5> The result type of the fifth expression
	 * @param <E6> The result type of the sixth expression
	 * @param <E7> The result type of the seventh expression
	 * @param <E8> The result type of the eighth expression
	 * @param <E9> The result type of the ninth expression
	 * @param <E10> The result type of the tenth expression
	 * @return A select query producing rows of the selected values
	 * @throws NullPointerException If any of the expressions is null
	 */
	public <E1, E2, E3, E4, E5, E6, E7, E8, E9, E10> @NonNull SqlSelectQuery<SqlRow10<E1, E2, E3, E4, E5, E6, E7, E8, E9, E10>> select(
		@NonNull SqlExpression<E1> e1,
		@NonNull SqlExpression<E2> e2,
		@NonNull SqlExpression<E3> e3,
		@NonNull SqlExpression<E4> e4,
		@NonNull SqlExpression<E5> e5,
		@NonNull SqlExpression<E6> e6,
		@NonNull SqlExpression<E7> e7,
		@NonNull SqlExpression<E8> e8,
		@NonNull SqlExpression<E9> e9,
		@NonNull SqlExpression<E10> e10
	) {
		return this.selectWithExpressions((Class<SqlRow10<E1, E2, E3, E4, E5, E6, E7, E8, E9, E10>>) (Class<?>) SqlRow10.class, e1, e2, e3, e4, e5, e6, e7, e8, e9, e10);
	}
	
	/**
	 * Creates a select query that returns the values of the given eleven expressions as a {@link SqlRow11 row}.<br>
	 *
	 * @param e1 The first expression to select
	 * @param e2 The second expression to select
	 * @param e3 The third expression to select
	 * @param e4 The fourth expression to select
	 * @param e5 The fifth expression to select
	 * @param e6 The sixth expression to select
	 * @param e7 The seventh expression to select
	 * @param e8 The eighth expression to select
	 * @param e9 The ninth expression to select
	 * @param e10 The tenth expression to select
	 * @param e11 The eleventh expression to select
	 * @param <E1> The result type of the first expression
	 * @param <E2> The result type of the second expression
	 * @param <E3> The result type of the third expression
	 * @param <E4> The result type of the fourth expression
	 * @param <E5> The result type of the fifth expression
	 * @param <E6> The result type of the sixth expression
	 * @param <E7> The result type of the seventh expression
	 * @param <E8> The result type of the eighth expression
	 * @param <E9> The result type of the ninth expression
	 * @param <E10> The result type of the tenth expression
	 * @param <E11> The result type of the eleventh expression
	 * @return A select query producing rows of the selected values
	 * @throws NullPointerException If any of the expressions is null
	 */
	public <E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11> @NonNull SqlSelectQuery<SqlRow11<E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11>> select(
		@NonNull SqlExpression<E1> e1,
		@NonNull SqlExpression<E2> e2,
		@NonNull SqlExpression<E3> e3,
		@NonNull SqlExpression<E4> e4,
		@NonNull SqlExpression<E5> e5,
		@NonNull SqlExpression<E6> e6,
		@NonNull SqlExpression<E7> e7,
		@NonNull SqlExpression<E8> e8,
		@NonNull SqlExpression<E9> e9,
		@NonNull SqlExpression<E10> e10,
		@NonNull SqlExpression<E11> e11
	) {
		return this.selectWithExpressions((Class<SqlRow11<E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11>>) (Class<?>) SqlRow11.class, e1, e2, e3, e4, e5, e6, e7, e8, e9, e10, e11);
	}
	
	/**
	 * Creates a select query that returns the values of the given twelve expressions as a {@link SqlRow12 row}.<br>
	 *
	 * @param e1 The first expression to select
	 * @param e2 The second expression to select
	 * @param e3 The third expression to select
	 * @param e4 The fourth expression to select
	 * @param e5 The fifth expression to select
	 * @param e6 The sixth expression to select
	 * @param e7 The seventh expression to select
	 * @param e8 The eighth expression to select
	 * @param e9 The ninth expression to select
	 * @param e10 The tenth expression to select
	 * @param e11 The eleventh expression to select
	 * @param e12 The twelfth expression to select
	 * @param <E1> The result type of the first expression
	 * @param <E2> The result type of the second expression
	 * @param <E3> The result type of the third expression
	 * @param <E4> The result type of the fourth expression
	 * @param <E5> The result type of the fifth expression
	 * @param <E6> The result type of the sixth expression
	 * @param <E7> The result type of the seventh expression
	 * @param <E8> The result type of the eighth expression
	 * @param <E9> The result type of the ninth expression
	 * @param <E10> The result type of the tenth expression
	 * @param <E11> The result type of the eleventh expression
	 * @param <E12> The result type of the twelfth expression
	 * @return A select query producing rows of the selected values
	 * @throws NullPointerException If any of the expressions is null
	 */
	public <E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11, E12> @NonNull SqlSelectQuery<SqlRow12<E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11, E12>> select(
		@NonNull SqlExpression<E1> e1,
		@NonNull SqlExpression<E2> e2,
		@NonNull SqlExpression<E3> e3,
		@NonNull SqlExpression<E4> e4,
		@NonNull SqlExpression<E5> e5,
		@NonNull SqlExpression<E6> e6,
		@NonNull SqlExpression<E7> e7,
		@NonNull SqlExpression<E8> e8,
		@NonNull SqlExpression<E9> e9,
		@NonNull SqlExpression<E10> e10,
		@NonNull SqlExpression<E11> e11,
		@NonNull SqlExpression<E12> e12
	) {
		return this.selectWithExpressions((Class<SqlRow12<E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11, E12>>) (Class<?>) SqlRow12.class, e1, e2, e3, e4, e5, e6, e7, e8, e9, e10, e11, e12);
	}
	
	/**
	 * Creates a select query that returns the values of the given thirteen expressions as a {@link SqlRow13 row}.<br>
	 *
	 * @param e1 The first expression to select
	 * @param e2 The second expression to select
	 * @param e3 The third expression to select
	 * @param e4 The fourth expression to select
	 * @param e5 The fifth expression to select
	 * @param e6 The sixth expression to select
	 * @param e7 The seventh expression to select
	 * @param e8 The eighth expression to select
	 * @param e9 The ninth expression to select
	 * @param e10 The tenth expression to select
	 * @param e11 The eleventh expression to select
	 * @param e12 The twelfth expression to select
	 * @param e13 The thirteenth expression to select
	 * @param <E1> The result type of the first expression
	 * @param <E2> The result type of the second expression
	 * @param <E3> The result type of the third expression
	 * @param <E4> The result type of the fourth expression
	 * @param <E5> The result type of the fifth expression
	 * @param <E6> The result type of the sixth expression
	 * @param <E7> The result type of the seventh expression
	 * @param <E8> The result type of the eighth expression
	 * @param <E9> The result type of the ninth expression
	 * @param <E10> The result type of the tenth expression
	 * @param <E11> The result type of the eleventh expression
	 * @param <E12> The result type of the twelfth expression
	 * @param <E13> The result type of the thirteenth expression
	 * @return A select query producing rows of the selected values
	 * @throws NullPointerException If any of the expressions is null
	 */
	public <E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11, E12, E13> @NonNull SqlSelectQuery<SqlRow13<E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11, E12, E13>> select(
		@NonNull SqlExpression<E1> e1,
		@NonNull SqlExpression<E2> e2,
		@NonNull SqlExpression<E3> e3,
		@NonNull SqlExpression<E4> e4,
		@NonNull SqlExpression<E5> e5,
		@NonNull SqlExpression<E6> e6,
		@NonNull SqlExpression<E7> e7,
		@NonNull SqlExpression<E8> e8,
		@NonNull SqlExpression<E9> e9,
		@NonNull SqlExpression<E10> e10,
		@NonNull SqlExpression<E11> e11,
		@NonNull SqlExpression<E12> e12,
		@NonNull SqlExpression<E13> e13
	) {
		return this.selectWithExpressions((Class<SqlRow13<E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11, E12, E13>>) (Class<?>) SqlRow13.class, e1, e2, e3, e4, e5, e6, e7, e8, e9, e10, e11, e12, e13);
	}
	
	/**
	 * Creates a select query that returns the values of the given fourteen expressions as a {@link SqlRow14 row}.<br>
	 *
	 * @param e1 The first expression to select
	 * @param e2 The second expression to select
	 * @param e3 The third expression to select
	 * @param e4 The fourth expression to select
	 * @param e5 The fifth expression to select
	 * @param e6 The sixth expression to select
	 * @param e7 The seventh expression to select
	 * @param e8 The eighth expression to select
	 * @param e9 The ninth expression to select
	 * @param e10 The tenth expression to select
	 * @param e11 The eleventh expression to select
	 * @param e12 The twelfth expression to select
	 * @param e13 The thirteenth expression to select
	 * @param e14 The fourteenth expression to select
	 * @param <E1> The result type of the first expression
	 * @param <E2> The result type of the second expression
	 * @param <E3> The result type of the third expression
	 * @param <E4> The result type of the fourth expression
	 * @param <E5> The result type of the fifth expression
	 * @param <E6> The result type of the sixth expression
	 * @param <E7> The result type of the seventh expression
	 * @param <E8> The result type of the eighth expression
	 * @param <E9> The result type of the ninth expression
	 * @param <E10> The result type of the tenth expression
	 * @param <E11> The result type of the eleventh expression
	 * @param <E12> The result type of the twelfth expression
	 * @param <E13> The result type of the thirteenth expression
	 * @param <E14> The result type of the fourteenth expression
	 * @return A select query producing rows of the selected values
	 * @throws NullPointerException If any of the expressions is null
	 */
	public <E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11, E12, E13, E14> @NonNull SqlSelectQuery<SqlRow14<E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11, E12, E13, E14>> select(
		@NonNull SqlExpression<E1> e1,
		@NonNull SqlExpression<E2> e2,
		@NonNull SqlExpression<E3> e3,
		@NonNull SqlExpression<E4> e4,
		@NonNull SqlExpression<E5> e5,
		@NonNull SqlExpression<E6> e6,
		@NonNull SqlExpression<E7> e7,
		@NonNull SqlExpression<E8> e8,
		@NonNull SqlExpression<E9> e9,
		@NonNull SqlExpression<E10> e10,
		@NonNull SqlExpression<E11> e11,
		@NonNull SqlExpression<E12> e12,
		@NonNull SqlExpression<E13> e13,
		@NonNull SqlExpression<E14> e14
	) {
		return this.selectWithExpressions((Class<SqlRow14<E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11, E12, E13, E14>>) (Class<?>) SqlRow14.class, e1, e2, e3, e4, e5, e6, e7, e8, e9, e10, e11, e12, e13, e14);
	}
	
	/**
	 * Creates a select query that returns the values of the given fifteen expressions as a {@link SqlRow15 row}.<br>
	 *
	 * @param e1 The first expression to select
	 * @param e2 The second expression to select
	 * @param e3 The third expression to select
	 * @param e4 The fourth expression to select
	 * @param e5 The fifth expression to select
	 * @param e6 The sixth expression to select
	 * @param e7 The seventh expression to select
	 * @param e8 The eighth expression to select
	 * @param e9 The ninth expression to select
	 * @param e10 The tenth expression to select
	 * @param e11 The eleventh expression to select
	 * @param e12 The twelfth expression to select
	 * @param e13 The thirteenth expression to select
	 * @param e14 The fourteenth expression to select
	 * @param e15 The fifteenth expression to select
	 * @param <E1> The result type of the first expression
	 * @param <E2> The result type of the second expression
	 * @param <E3> The result type of the third expression
	 * @param <E4> The result type of the fourth expression
	 * @param <E5> The result type of the fifth expression
	 * @param <E6> The result type of the sixth expression
	 * @param <E7> The result type of the seventh expression
	 * @param <E8> The result type of the eighth expression
	 * @param <E9> The result type of the ninth expression
	 * @param <E10> The result type of the tenth expression
	 * @param <E11> The result type of the eleventh expression
	 * @param <E12> The result type of the twelfth expression
	 * @param <E13> The result type of the thirteenth expression
	 * @param <E14> The result type of the fourteenth expression
	 * @param <E15> The result type of the fifteenth expression
	 * @return A select query producing rows of the selected values
	 * @throws NullPointerException If any of the expressions is null
	 */
	public <E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11, E12, E13, E14, E15> @NonNull SqlSelectQuery<SqlRow15<E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11, E12, E13, E14, E15>> select(
		@NonNull SqlExpression<E1> e1,
		@NonNull SqlExpression<E2> e2,
		@NonNull SqlExpression<E3> e3,
		@NonNull SqlExpression<E4> e4,
		@NonNull SqlExpression<E5> e5,
		@NonNull SqlExpression<E6> e6,
		@NonNull SqlExpression<E7> e7,
		@NonNull SqlExpression<E8> e8,
		@NonNull SqlExpression<E9> e9,
		@NonNull SqlExpression<E10> e10,
		@NonNull SqlExpression<E11> e11,
		@NonNull SqlExpression<E12> e12,
		@NonNull SqlExpression<E13> e13,
		@NonNull SqlExpression<E14> e14,
		@NonNull SqlExpression<E15> e15
	) {
		return this.selectWithExpressions((Class<SqlRow15<E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11, E12, E13, E14, E15>>) (Class<?>) SqlRow15.class, e1, e2, e3, e4, e5, e6, e7, e8, e9, e10, e11, e12, e13, e14, e15);
	}
	
	/**
	 * Creates a select query that returns the values of the given sixteen expressions as a {@link SqlRow16 row}.<br>
	 *
	 * @param e1 The first expression to select
	 * @param e2 The second expression to select
	 * @param e3 The third expression to select
	 * @param e4 The fourth expression to select
	 * @param e5 The fifth expression to select
	 * @param e6 The sixth expression to select
	 * @param e7 The seventh expression to select
	 * @param e8 The eighth expression to select
	 * @param e9 The ninth expression to select
	 * @param e10 The tenth expression to select
	 * @param e11 The eleventh expression to select
	 * @param e12 The twelfth expression to select
	 * @param e13 The thirteenth expression to select
	 * @param e14 The fourteenth expression to select
	 * @param e15 The fifteenth expression to select
	 * @param e16 The sixteenth expression to select
	 * @param <E1> The result type of the first expression
	 * @param <E2> The result type of the second expression
	 * @param <E3> The result type of the third expression
	 * @param <E4> The result type of the fourth expression
	 * @param <E5> The result type of the fifth expression
	 * @param <E6> The result type of the sixth expression
	 * @param <E7> The result type of the seventh expression
	 * @param <E8> The result type of the eighth expression
	 * @param <E9> The result type of the ninth expression
	 * @param <E10> The result type of the tenth expression
	 * @param <E11> The result type of the eleventh expression
	 * @param <E12> The result type of the twelfth expression
	 * @param <E13> The result type of the thirteenth expression
	 * @param <E14> The result type of the fourteenth expression
	 * @param <E15> The result type of the fifteenth expression
	 * @param <E16> The result type of the sixteenth expression
	 * @return A select query producing rows of the selected values
	 * @throws NullPointerException If any of the expressions is null
	 */
	public <E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11, E12, E13, E14, E15, E16> @NonNull SqlSelectQuery<SqlRow16<E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11, E12, E13, E14, E15, E16>> select(
		@NonNull SqlExpression<E1> e1,
		@NonNull SqlExpression<E2> e2,
		@NonNull SqlExpression<E3> e3,
		@NonNull SqlExpression<E4> e4,
		@NonNull SqlExpression<E5> e5,
		@NonNull SqlExpression<E6> e6,
		@NonNull SqlExpression<E7> e7,
		@NonNull SqlExpression<E8> e8,
		@NonNull SqlExpression<E9> e9,
		@NonNull SqlExpression<E10> e10,
		@NonNull SqlExpression<E11> e11,
		@NonNull SqlExpression<E12> e12,
		@NonNull SqlExpression<E13> e13,
		@NonNull SqlExpression<E14> e14,
		@NonNull SqlExpression<E15> e15,
		@NonNull SqlExpression<E16> e16
	) {
		return this.selectWithExpressions((Class<SqlRow16<E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11, E12, E13, E14, E15, E16>>) (Class<?>) SqlRow16.class, e1, e2, e3, e4, e5, e6, e7, e8, e9, e10, e11, e12, e13, e14, e15, e16);
	}
	
	/**
	 * Creates a select query that returns the values of the given expressions as an object array.<br>
	 * This overload is used when more than sixteen expressions are selected or when the number of expressions is only known at runtime.<br>
	 * Each result row is mapped to an {@code Object[]} holding the values in the order of the given expressions.<br>
	 *
	 * @param expressions The expressions to select
	 * @return A select query producing object arrays of the selected values
	 * @throws NullPointerException If the expression array is null
	 */
	public @NonNull SqlSelectQuery<?> select(SqlExpression<?> @NonNull ... expressions) {
		Objects.requireNonNull(expressions, "Sql expressions must not be null");
		
		ThrowableFunction<ResultSet, Object[], SqlException> mapper = rs -> {
			Object[] values = new Object[expressions.length];
			for (int i = 0; i < expressions.length; i++) {
				values[i] = SqlType.getValue(expressions[i].type(), this.dialect, rs, i + 1);
			}
			return values;
		};
		return new SqlSelectQuery<>(this.table, this.dialect, this.connectionSource, this.queryTimeout, mapper, List.of(expressions));
	}
	//endregion
	
	/**
	 * Creates a select query that can be used as a subquery for the given expressions.<br>
	 * This is an alias for {@link #select(SqlExpression[])} that improves readability when the resulting query is nested into another query.<br>
	 *
	 * @param expressions The expressions to select
	 * @return A select query usable as a subquery
	 * @throws NullPointerException If the expression array is null
	 */
	public @NonNull SqlSelectQuery<?> subquery(SqlExpression<?> @NonNull ... expressions) {
		return this.select(expressions);
	}
	
	/**
	 * Creates an insert query for the given single entity.<br>
	 *
	 * @param entity The entity to insert
	 * @return An insert query for the entity
	 * @throws NullPointerException If the entity is null
	 * @throws SqlException If the insert statement could not be built
	 */
	public @NonNull SqlInsertQuery<E> insert(@NonNull E entity) throws SqlException {
		Objects.requireNonNull(entity, "Entity must not be null");
		
		return new SqlInsertQuery<>(this.table, this.dialect, this.connectionSource, this.queryTimeout, this.entityRowMapper, List.of(entity), this.auditUserProvider);
	}
	
	/**
	 * Creates an insert query for the given entities.<br>
	 *
	 * @param entities The entities to insert
	 * @return An insert query for the entities
	 * @throws NullPointerException If the entity array is null
	 * @throws SqlException If the insert statement could not be built
	 */
	public @NonNull SqlInsertQuery<E> insert(E @NonNull ... entities) throws SqlException {
		Objects.requireNonNull(entities, "Entity array must not be null");
		
		return new SqlInsertQuery<>(this.table, this.dialect, this.connectionSource, this.queryTimeout, this.entityRowMapper, List.of(entities), this.auditUserProvider);
	}
	
	/**
	 * Creates an insert query for the given collection of entities.<br>
	 *
	 * @param entities The entities to insert
	 * @return An insert query for the entities
	 * @throws NullPointerException If the entity collection is null
	 * @throws SqlException If the insert statement could not be built
	 */
	public @NonNull SqlInsertQuery<E> insert(@NonNull Collection<E> entities) throws SqlException {
		Objects.requireNonNull(entities, "Entity collection must not be null");
		
		return new SqlInsertQuery<>(this.table, this.dialect, this.connectionSource, this.queryTimeout, this.entityRowMapper, List.copyOf(entities), this.auditUserProvider);
	}
	
	/**
	 * Creates an insert query for the given entity that ignores rows conflicting on the given columns.<br>
	 * If a row with the same values in the conflict columns already exists, the insert is skipped instead of failing.<br>
	 *
	 * @param entity The entity to insert
	 * @param conflictColumns The columns that define a conflict
	 * @return An insert-or-ignore query for the entity
	 * @throws NullPointerException If the entity or the conflict column array is null
	 * @throws SqlException If the insert statement could not be built
	 */
	@SafeVarargs
	public final @NonNull SqlInsertQuery<E> insert(@NonNull E entity, SqlColumn<E, ?> @NonNull ... conflictColumns) throws SqlException {
		Objects.requireNonNull(entity, "Entity must not be null");
		Objects.requireNonNull(conflictColumns, "Sql conflict columns must not be null");
		
		return SqlInsertQuery.insertOrIgnore(this.table, this.dialect, this.connectionSource, this.queryTimeout, this.entityRowMapper, List.of(entity), List.of(conflictColumns));
	}
	
	/**
	 * Creates an insert query that inserts the rows produced by the given select query.<br>
	 *
	 * @param query The select query whose rows are inserted
	 * @return An insert-from-select query
	 * @throws NullPointerException If the select query is null
	 * @throws SqlException If the insert statement could not be built
	 */
	public @NonNull SqlInsertQuery<E> insert(@NonNull SqlSelectQuery<?> query) throws SqlException {
		Objects.requireNonNull(query, "Sql select query must not be null");
		
		return SqlInsertQuery.insertFromSelect(this.table, this.dialect, this.connectionSource, this.queryTimeout, this.entityRowMapper, query);
	}
	
	/**
	 * Creates an update query for the table.<br>
	 * The columns to update and the condition restricting the affected rows are configured on the returned query.<br>
	 * @return An update query for the table
	 */
	public @NonNull SqlUpdateQuery<E> update() {
		return new SqlUpdateQuery<>(this.table, this.dialect, this.connectionSource, this.queryTimeout, this.entityRowMapper);
	}
	
	/**
	 * Creates a delete query for the table.<br>
	 * The condition restricting the deleted rows is configured on the returned query.<br>
	 * @return A delete query for the table
	 */
	public @NonNull SqlDeleteQuery<E> delete() {
		return new SqlDeleteQuery<>(this.table, this.dialect, this.connectionSource, this.queryTimeout, this.entityRowMapper);
	}
}
