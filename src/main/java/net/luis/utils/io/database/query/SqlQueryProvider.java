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
import net.luis.utils.io.database.dialect.SqlDialect;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.exception.database.SqlResultMappingException;
import net.luis.utils.io.database.expression.SqlExpression;
import net.luis.utils.io.database.query.crud.*;
import net.luis.utils.io.database.query.row.*;
import net.luis.utils.io.database.table.SqlColumn;
import net.luis.utils.io.database.table.SqlTable;
import org.jspecify.annotations.NonNull;

import java.lang.reflect.Constructor;
import java.sql.ResultSet;
import java.time.Duration;
import java.util.*;

/**
 *
 * @author Luis-St
 *
 */

@SuppressWarnings("unchecked")
public class SqlQueryProvider<E> {
	
	private final SqlTable<E> table;
	private final SqlDialect dialect;
	private final SqlConnectionSource connectionSource;
	private final Duration queryTimeout;
	private final ThrowableFunction<ResultSet, E, SqlException> entityRowMapper;
	
	public SqlQueryProvider(@NonNull SqlTable<E> table, @NonNull SqlDialect dialect, @NonNull SqlConnectionSource connectionSource, @NonNull Duration queryTimeout) {
		this.table = Objects.requireNonNull(table, "Sql table must not be null");
		this.dialect = Objects.requireNonNull(dialect, "Sql dialect must not be null");
		this.connectionSource = Objects.requireNonNull(connectionSource, "Sql connection source must not be null");
		this.queryTimeout = Objects.requireNonNull(queryTimeout, "Query timeout must not be null");
		this.entityRowMapper = createRowMapper(table);
	}
	
	//region Row mapper creation
	
	private static <E> @NonNull ThrowableFunction<ResultSet, E, SqlException> createRowMapper(@NonNull SqlTable<E> table) {
		Objects.requireNonNull(table, "Sql table must not be null");
		
		List<SqlColumn<E, ?>> sorted = table.columns().stream().sorted(Comparator.comparingInt(SqlColumn::index)).toList();
		Constructor<E> constructor = findMatchingConstructor(table.type(), sorted);
		
		return resultSet -> {
			Object[] args = new Object[sorted.size()];
			for (int i = 0; i < sorted.size(); i++) {
				args[i] = sorted.get(i).type().get(resultSet, i + 1);
			}
			
			try {
				return constructor.newInstance(args);
			} catch (Exception e) {
				throw new SqlResultMappingException("Failed to construct entity " + table.type().getSimpleName() + " from result set", e, table.type());
			}
		};
	}
	
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
					constructor.setAccessible(true);
					return (Constructor<E>) constructor;
				}
			}
		}
		throw new IllegalStateException("No matching constructor found on " + type.getSimpleName() + " for " + sortedColumns.size() + " table columns");
	}
	//endregion
	
	private void ensureNotNull(SqlExpression<?> @NonNull ... expressions) {
		for (SqlExpression<?> expression : expressions) {
			Objects.requireNonNull(expression, "Sql expression must not be null");
		}
	}
	
	private <R> @NonNull SqlSelectQuery<R> selectWithExpressions(@NonNull Class<R> rowType, SqlExpression<?> @NonNull ... expressions) {
		Objects.requireNonNull(rowType, "Row type must not be null");
		Objects.requireNonNull(expressions, "Sql expressions must not be null");
		this.ensureNotNull(expressions);
		
		List<SqlExpression<?>> expressionList = List.of(expressions);
		return new SqlSelectQuery<>(this.table, this.dialect, this.connectionSource, this.queryTimeout, SqlRowMapper.forExpressions(rowType, expressionList), expressionList);
	}
	
	public @NonNull SqlSelectQuery<E> select() {
		return new SqlSelectQuery<>(this.table, this.dialect, this.connectionSource, this.queryTimeout, this.entityRowMapper);
	}
	
	public <E1> @NonNull SqlSelectQuery<E1> select(@NonNull SqlExpression<E1> e1) {
		Objects.requireNonNull(e1, "Sql expression must not be null");
		
		ThrowableFunction<ResultSet, E1, SqlException> mapper = rs -> {
			return e1.type().get(rs, 1);
		};
		return new SqlSelectQuery<>(this.table, this.dialect, this.connectionSource, this.queryTimeout, mapper, List.of(e1));
	}
	
	public <E1, E2> @NonNull SqlSelectQuery<SqlRow2<E1, E2>> select(@NonNull SqlExpression<E1> e1, @NonNull SqlExpression<E2> e2) {
		return this.selectWithExpressions((Class<SqlRow2<E1, E2>>) (Class<?>) SqlRow2.class, e1, e2);
	}
	
	public <E1, E2, E3> @NonNull SqlSelectQuery<SqlRow3<E1, E2, E3>> select(@NonNull SqlExpression<E1> e1, @NonNull SqlExpression<E2> e2, @NonNull SqlExpression<E3> e3) {
		return this.selectWithExpressions((Class<SqlRow3<E1, E2, E3>>) (Class<?>) SqlRow3.class, e1, e2, e3);
	}
	
	public <E1, E2, E3, E4> @NonNull SqlSelectQuery<SqlRow4<E1, E2, E3, E4>> select(
		@NonNull SqlExpression<E1> e1, @NonNull SqlExpression<E2> e2,
		@NonNull SqlExpression<E3> e3, @NonNull SqlExpression<E4> e4
	) {
		return this.selectWithExpressions((Class<SqlRow4<E1, E2, E3, E4>>) (Class<?>) SqlRow4.class, e1, e2, e3, e4);
	}
	
	public <E1, E2, E3, E4, E5> @NonNull SqlSelectQuery<SqlRow5<E1, E2, E3, E4, E5>> select(
		@NonNull SqlExpression<E1> e1, @NonNull SqlExpression<E2> e2,
		@NonNull SqlExpression<E3> e3, @NonNull SqlExpression<E4> e4,
		@NonNull SqlExpression<E5> e5
	) {
		return this.selectWithExpressions((Class<SqlRow5<E1, E2, E3, E4, E5>>) (Class<?>) SqlRow5.class, e1, e2, e3, e4, e5);
	}
	
	public <E1, E2, E3, E4, E5, E6> @NonNull SqlSelectQuery<SqlRow6<E1, E2, E3, E4, E5, E6>> select(
		@NonNull SqlExpression<E1> e1, @NonNull SqlExpression<E2> e2,
		@NonNull SqlExpression<E3> e3, @NonNull SqlExpression<E4> e4,
		@NonNull SqlExpression<E5> e5, @NonNull SqlExpression<E6> e6
	) {
		return this.selectWithExpressions((Class<SqlRow6<E1, E2, E3, E4, E5, E6>>) (Class<?>) SqlRow6.class, e1, e2, e3, e4, e5, e6);
	}
	
	public <E1, E2, E3, E4, E5, E6, E7> @NonNull SqlSelectQuery<SqlRow7<E1, E2, E3, E4, E5, E6, E7>> select(
		@NonNull SqlExpression<E1> e1, @NonNull SqlExpression<E2> e2,
		@NonNull SqlExpression<E3> e3, @NonNull SqlExpression<E4> e4,
		@NonNull SqlExpression<E5> e5, @NonNull SqlExpression<E6> e6,
		@NonNull SqlExpression<E7> e7
	) {
		return this.selectWithExpressions((Class<SqlRow7<E1, E2, E3, E4, E5, E6, E7>>) (Class<?>) SqlRow7.class, e1, e2, e3, e4, e5, e6, e7);
	}
	
	public <E1, E2, E3, E4, E5, E6, E7, E8> @NonNull SqlSelectQuery<SqlRow8<E1, E2, E3, E4, E5, E6, E7, E8>> select(
		@NonNull SqlExpression<E1> e1, @NonNull SqlExpression<E2> e2,
		@NonNull SqlExpression<E3> e3, @NonNull SqlExpression<E4> e4,
		@NonNull SqlExpression<E5> e5, @NonNull SqlExpression<E6> e6,
		@NonNull SqlExpression<E7> e7, @NonNull SqlExpression<E8> e8
	) {
		return this.selectWithExpressions((Class<SqlRow8<E1, E2, E3, E4, E5, E6, E7, E8>>) (Class<?>) SqlRow8.class, e1, e2, e3, e4, e5, e6, e7, e8);
	}
	
	public <E1, E2, E3, E4, E5, E6, E7, E8, E9> @NonNull SqlSelectQuery<SqlRow9<E1, E2, E3, E4, E5, E6, E7, E8, E9>> select(
		@NonNull SqlExpression<E1> e1, @NonNull SqlExpression<E2> e2,
		@NonNull SqlExpression<E3> e3, @NonNull SqlExpression<E4> e4,
		@NonNull SqlExpression<E5> e5, @NonNull SqlExpression<E6> e6,
		@NonNull SqlExpression<E7> e7, @NonNull SqlExpression<E8> e8,
		@NonNull SqlExpression<E9> e9
	) {
		return this.selectWithExpressions((Class<SqlRow9<E1, E2, E3, E4, E5, E6, E7, E8, E9>>) (Class<?>) SqlRow9.class, e1, e2, e3, e4, e5, e6, e7, e8, e9);
	}
	
	public <E1, E2, E3, E4, E5, E6, E7, E8, E9, E10> @NonNull SqlSelectQuery<SqlRow10<E1, E2, E3, E4, E5, E6, E7, E8, E9, E10>> select(
		@NonNull SqlExpression<E1> e1, @NonNull SqlExpression<E2> e2,
		@NonNull SqlExpression<E3> e3, @NonNull SqlExpression<E4> e4,
		@NonNull SqlExpression<E5> e5, @NonNull SqlExpression<E6> e6,
		@NonNull SqlExpression<E7> e7, @NonNull SqlExpression<E8> e8,
		@NonNull SqlExpression<E9> e9, @NonNull SqlExpression<E10> e10
	) {
		return this.selectWithExpressions((Class<SqlRow10<E1, E2, E3, E4, E5, E6, E7, E8, E9, E10>>) (Class<?>) SqlRow10.class, e1, e2, e3, e4, e5, e6, e7, e8, e9, e10);
	}
	
	public <E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11> @NonNull SqlSelectQuery<SqlRow11<E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11>> select(
		@NonNull SqlExpression<E1> e1, @NonNull SqlExpression<E2> e2,
		@NonNull SqlExpression<E3> e3, @NonNull SqlExpression<E4> e4,
		@NonNull SqlExpression<E5> e5, @NonNull SqlExpression<E6> e6,
		@NonNull SqlExpression<E7> e7, @NonNull SqlExpression<E8> e8,
		@NonNull SqlExpression<E9> e9, @NonNull SqlExpression<E10> e10,
		@NonNull SqlExpression<E11> e11
	) {
		return this.selectWithExpressions((Class<SqlRow11<E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11>>) (Class<?>) SqlRow11.class, e1, e2, e3, e4, e5, e6, e7, e8, e9, e10, e11);
	}
	
	public <E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11, E12> @NonNull SqlSelectQuery<SqlRow12<E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11, E12>> select(
		@NonNull SqlExpression<E1> e1, @NonNull SqlExpression<E2> e2,
		@NonNull SqlExpression<E3> e3, @NonNull SqlExpression<E4> e4,
		@NonNull SqlExpression<E5> e5, @NonNull SqlExpression<E6> e6,
		@NonNull SqlExpression<E7> e7, @NonNull SqlExpression<E8> e8,
		@NonNull SqlExpression<E9> e9, @NonNull SqlExpression<E10> e10,
		@NonNull SqlExpression<E11> e11, @NonNull SqlExpression<E12> e12
	) {
		return this.selectWithExpressions((Class<SqlRow12<E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11, E12>>) (Class<?>) SqlRow12.class, e1, e2, e3, e4, e5, e6, e7, e8, e9, e10, e11, e12);
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
		return this.selectWithExpressions((Class<SqlRow13<E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11, E12, E13>>) (Class<?>) SqlRow13.class, e1, e2, e3, e4, e5, e6, e7, e8, e9, e10, e11, e12, e13);
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
		return this.selectWithExpressions((Class<SqlRow14<E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11, E12, E13, E14>>) (Class<?>) SqlRow14.class, e1, e2, e3, e4, e5, e6, e7, e8, e9, e10, e11, e12, e13, e14);
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
		return this.selectWithExpressions((Class<SqlRow15<E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11, E12, E13, E14, E15>>) (Class<?>) SqlRow15.class, e1, e2, e3, e4, e5, e6, e7, e8, e9, e10, e11, e12, e13, e14, e15);
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
		return this.selectWithExpressions((Class<SqlRow16<E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11, E12, E13, E14, E15, E16>>) (Class<?>) SqlRow16.class, e1, e2, e3, e4, e5, e6, e7, e8, e9, e10, e11, e12, e13, e14, e15, e16);
	}
	
	public @NonNull SqlSelectQuery<?> select(SqlExpression<?> @NonNull ... expressions) {
		Objects.requireNonNull(expressions, "Sql expressions must not be null");
		
		ThrowableFunction<ResultSet, Object[], SqlException> mapper = rs -> {
			Object[] values = new Object[expressions.length];
			for (int i = 0; i < expressions.length; i++) {
				values[i] = expressions[i].type().get(rs, i + 1);
			}
			return values;
		};
		return new SqlSelectQuery<>(this.table, this.dialect, this.connectionSource, this.queryTimeout, mapper, List.of(expressions));
	}
	
	public @NonNull SqlSelectQuery<?> subquery(SqlExpression<?> @NonNull ... expressions) {
		return this.select(expressions);
	}
	
	public @NonNull SqlInsertQuery<E> insert(@NonNull E entity) throws SqlException {
		Objects.requireNonNull(entity, "Entity must not be null");
		
		return new SqlInsertQuery<>(this.table, this.dialect, this.connectionSource, this.queryTimeout, this.entityRowMapper, List.of(entity));
	}
	
	@SuppressWarnings("unchecked")
	public @NonNull SqlInsertQuery<E> insert(E @NonNull ... entities) throws SqlException {
		Objects.requireNonNull(entities, "Entity array must not be null");
		
		return new SqlInsertQuery<>(this.table, this.dialect, this.connectionSource, this.queryTimeout, this.entityRowMapper, List.of(entities));
	}
	
	public @NonNull SqlInsertQuery<E> insert(@NonNull Collection<E> entities) throws SqlException {
		Objects.requireNonNull(entities, "Entity collection must not be null");
		
		return new SqlInsertQuery<>(this.table, this.dialect, this.connectionSource, this.queryTimeout, this.entityRowMapper, List.copyOf(entities));
	}
	
	@SafeVarargs
	public final @NonNull SqlInsertQuery<E> insertOrIgnore(@NonNull E entity, SqlColumn<E, ?> @NonNull ... conflictColumns) throws SqlException {
		Objects.requireNonNull(entity, "Entity must not be null");
		Objects.requireNonNull(conflictColumns, "Sql conflict columns must not be null");
		
		return SqlInsertQuery.insertOrIgnore(this.table, this.dialect, this.connectionSource, this.queryTimeout, this.entityRowMapper, List.of(entity), List.of(conflictColumns));
	}
	
	public @NonNull SqlInsertQuery<E> insertFromSelect(@NonNull SqlSelectQuery<?> query) throws SqlException {
		Objects.requireNonNull(query, "Sql select query must not be null");
		
		return SqlInsertQuery.insertFromSelect(this.table, this.dialect, this.connectionSource, this.queryTimeout, this.entityRowMapper, query);
	}
	
	public @NonNull SqlUpdateQuery<E> update() {
		return new SqlUpdateQuery<>(this.table, this.dialect, this.connectionSource, this.queryTimeout, this.entityRowMapper);
	}
	
	public @NonNull SqlDeleteQuery<E> delete() {
		return new SqlDeleteQuery<>(this.table, this.dialect, this.connectionSource, this.queryTimeout, this.entityRowMapper);
	}
}
