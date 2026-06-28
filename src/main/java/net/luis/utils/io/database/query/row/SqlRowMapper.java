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

package net.luis.utils.io.database.query.row;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.luis.utils.function.throwable.ThrowableFunction;
import net.luis.utils.io.database.dialect.SqlDialect;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.exception.database.SqlResultMappingException;
import net.luis.utils.io.database.expression.SqlAliasedExpression;
import net.luis.utils.io.database.expression.SqlExpression;
import net.luis.utils.io.database.table.SqlAliasedColumn;
import net.luis.utils.io.database.table.SqlColumn;
import net.luis.utils.io.database.type.SqlType;
import org.apache.commons.lang3.ArrayUtils;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.*;
import java.sql.ResultSet;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Factory for row mappers that turn a jdbc {@link ResultSet} row into a typed result object.<br>
 * <p>
 *     Depending on the requested target type, the produced mapper materializes one of three shapes:<br>
 * </p>
 * <ul>
 *     <li>A dynamic proxy backed by the positional {@link SqlRow2}..{@code SqlRow16} accessor interfaces, where accessors like {@code first()} bind to columns by position</li>
 *     <li>A record, whose canonical constructor is invoked with the column values matched by component name or, as a fallback, by position</li>
 *     <li>An arbitrary interface, whose zero-argument methods are matched to selected expressions by name and served through a dynamic proxy</li>
 * </ul>
 * The column values are read using the {@link SqlType} of the selected expressions and the active {@link SqlDialect}.<br>
 *
 * @see SqlRowInvocationHandler
 *
 * @author Luis-St
 */
@SuppressWarnings("unchecked")
public final class SqlRowMapper {
	
	/**
	 * Maps the ordinal accessor names of the positional row interfaces to their zero-based column index.<br>
	 * For example {@code first} maps to {@code 0} and {@code sixteenth} maps to {@code 15}.<br>
	 */
	private static final Map<String, Integer> ORDINAL_NAMES = Map.ofEntries(
		Map.entry("first", 0),
		Map.entry("second", 1),
		Map.entry("third", 2),
		Map.entry("fourth", 3),
		Map.entry("fifth", 4),
		Map.entry("sixth", 5),
		Map.entry("seventh", 6),
		Map.entry("eighth", 7),
		Map.entry("ninth", 8),
		Map.entry("tenth", 9),
		Map.entry("eleventh", 10),
		Map.entry("twelfth", 11),
		Map.entry("thirteenth", 12),
		Map.entry("fourteenth", 13),
		Map.entry("fifteenth", 14),
		Map.entry("sixteenth", 15)
	);
	
	/**
	 * Private constructor to prevent instantiation.<br>
	 * This is a utility class that should not be instantiated.<br>
	 */
	private SqlRowMapper() {}
	
	/**
	 * Resolves the column name that the given expression contributes to a result row.<br>
	 * <p>
	 *     The name is derived from the kind of expression:<br>
	 * </p>
	 * <ul>
	 *     <li>For a {@link SqlColumn} the column name is used</li>
	 *     <li>For a {@link SqlAliasedExpression} the alias is used</li>
	 *     <li>For a {@link SqlAliasedColumn} the underlying column name is used</li>
	 *     <li>For any other expression the simple class name is used as a fallback</li>
	 * </ul>
	 *
	 * @param expression The expression to resolve the name of
	 * @return The resolved column name of the expression
	 * @throws NullPointerException If the expression is null
	 */
	private static @NonNull String resolveExpressionName(@NonNull SqlExpression<?> expression) {
		return switch (expression) {
			case SqlColumn<?, ?> column -> column.name();
			case SqlAliasedExpression<?> aliased -> aliased.alias().get();
			case SqlAliasedColumn<?, ?> aliasColumn -> aliasColumn.column().name();
			
			case null -> throw new NullPointerException("Sql expression must not be null");
			default -> expression.getClass().getSimpleName();
		};
	}
	
	/**
	 * Creates a row mapper that materializes each result row as a dynamic proxy of the given row type.<br>
	 * <p>
	 *     The row type is typically one of the positional {@link SqlRow2}..{@code SqlRow16} interfaces.<br>
	 *     Ordinal accessors such as {@code first()} bind to columns by position, while any other
	 *     zero-argument accessor binds to the selected expression with a matching name.
	 * </p>
	 *
	 * @param rowType The row interface to map each result row to
	 * @param expressions The selected expressions in the order they appear in the result set
	 * @param dialect The sql dialect used to read the column values
	 * @param <R> The type of the mapped row
	 * @return A mapper that maps a result set row to an instance of the row type
	 * @throws NullPointerException If the row type, expressions or dialect is null
	 * @throws IllegalArgumentException If an accessor of the row type matches neither an ordinal position nor a selected expression
	 */
	public static <R> @NonNull ThrowableFunction<ResultSet, R, SqlException> forExpressions(@NonNull Class<R> rowType, @NonNull List<SqlExpression<?>> expressions, @NonNull SqlDialect dialect) {
		Objects.requireNonNull(rowType, "Row type must not be null");
		Objects.requireNonNull(expressions, "Sql expressions must not be null");
		Objects.requireNonNull(dialect, "Sql dialect must not be null");
		
		List<SqlType<?>> types = Lists.newArrayListWithCapacity(expressions.size());
		List<String> expressionNames = Lists.newArrayListWithCapacity(expressions.size());
		for (SqlExpression<?> expression : expressions) {
			types.add(expression.type());
			expressionNames.add(resolveExpressionName(expression));
		}
		return createProxyMapper(rowType, types, expressionNames, dialect);
	}
	
	/**
	 * Creates a row mapper that projects each result row into the given record or interface target type.<br>
	 * <p>
	 *     Record targets are mapped through their canonical constructor, interface targets through a
	 *     dynamic proxy; in both cases columns are matched to the target by name.
	 * </p>
	 *
	 * @param targetType The record or interface type to project each result row into
	 * @param expressions The selected expressions in the order they appear in the result set
	 * @param dialect The sql dialect used to read the column values
	 * @param <R> The type of the projected row
	 * @return A mapper that maps a result set row to an instance of the target type
	 * @throws NullPointerException If the target type, expressions or dialect is null
	 * @throws IllegalArgumentException If the target type is neither a record nor an interface, or if a component or method has no matching expression
	 */
	public static <R> @NonNull ThrowableFunction<ResultSet, R, SqlException> forProjection(@NonNull Class<R> targetType, @NonNull List<SqlExpression<?>> expressions, @NonNull SqlDialect dialect) {
		Objects.requireNonNull(targetType, "Target type must not be null");
		Objects.requireNonNull(expressions, "Sql expressions must not be null");
		Objects.requireNonNull(dialect, "Sql dialect must not be null");
		
		List<String> expressionNames = Lists.newArrayList();
		for (SqlExpression<?> expression : expressions) {
			expressionNames.add(resolveExpressionName(expression));
		}
		
		if (targetType.isRecord()) {
			return forRecordProjection(dialect, targetType, expressions, expressionNames);
		}
		if (targetType.isInterface()) {
			return forInterfaceProjection(dialect, targetType, expressions, expressionNames);
		}
		throw new IllegalArgumentException("Sql projection target must be an interface or a record, got: " + targetType.getName());
	}
	
	/**
	 * Creates a row mapper that projects each result row into the given record type.<br>
	 * <p>
	 *     Each record component is matched to a selected expression by name; if no expression name
	 *     matches, the component is bound positionally by its declaration index.<br>
	 *     The mapper invokes the canonical constructor of the record with the resolved column values.
	 * </p>
	 *
	 * @param dialect The sql dialect used to read the column values
	 * @param recordType The record type to project each result row into
	 * @param expressions The selected expressions in the order they appear in the result set
	 * @param expressionNames The resolved names of the selected expressions
	 * @param <R> The type of the projected record
	 * @return A mapper that maps a result set row to an instance of the record type
	 * @throws NullPointerException If any of the arguments is null
	 * @throws IllegalArgumentException If the type is not a record or a component has no matching expression
	 * @throws IllegalStateException If the canonical constructor cannot be found or accessed
	 */
	private static <R> @NonNull ThrowableFunction<ResultSet, R, SqlException> forRecordProjection(
		@NonNull SqlDialect dialect,
		@NonNull Class<R> recordType,
		@NonNull List<SqlExpression<?>> expressions,
		@NonNull List<String> expressionNames
	) {
		Objects.requireNonNull(dialect, "Sql dialect must not be null");
		Objects.requireNonNull(recordType, "Record type must not be null");
		Objects.requireNonNull(expressions, "Sql expressions must not be null");
		Objects.requireNonNull(expressionNames, "Sql expression names must not be null");
		
		RecordComponent[] components = recordType.getRecordComponents();
		if (components == null) {
			throw new IllegalArgumentException("Not a record type: " + recordType.getName());
		}
		
		int[] mapping = new int[components.length];
		SqlType<?>[] types = new SqlType<?>[components.length];
		
		for (int c = 0; c < components.length; c++) {
			String componentName = components[c].getName();
			int expressionIndex = expressionNames.indexOf(componentName);
			if (expressionIndex < 0) {
				expressionIndex = c;
				if (expressionIndex >= expressions.size()) {
					throw new IllegalArgumentException("Cannot project into record " + recordType.getSimpleName() + ": component '" + componentName + "' has no matching expression");
				}
			}
			mapping[c] = expressionIndex;
			types[c] = expressions.get(expressionIndex).type();
		}
		
		Constructor<R> constructor;
		try {
			Class<?>[] paramTypes = Arrays.stream(components).map(RecordComponent::getType).toArray(Class[]::new);
			constructor = recordType.getDeclaredConstructor(paramTypes);
			constructor.setAccessible(true);
		} catch (NoSuchMethodException e) {
			throw new IllegalStateException("Cannot find canonical constructor for record " + recordType.getSimpleName(), e);
		} catch (InaccessibleObjectException e) {
			throw new IllegalStateException("Cannot access canonical constructor for record " + recordType.getSimpleName() + ", if it lives in a named module, open its package to LUtils (e.g. 'opens your.package;' in module-info.java)", e);
		}
		
		return resultSet -> {
			Object[] args = new Object[components.length];
			for (int i = 0; i < args.length; i++) {
				args[i] = SqlType.getValue(types[i], dialect, resultSet, mapping[i] + 1);
			}
			
			try {
				return constructor.newInstance(args);
			} catch (Exception e) {
				throw new SqlResultMappingException("Failed to construct record " + recordType.getSimpleName() + " from result set", e, recordType);
			}
		};
	}
	
	/**
	 * Creates a row mapper that projects each result row into the given interface type.<br>
	 * <p>
	 *     Every zero-argument, non-default method of the interface is matched to a selected expression
	 *     by name.<br>
	 *     The resolved column values are served through a dynamic proxy backed by a
	 *     {@link SqlRowInvocationHandler}.
	 * </p>
	 *
	 * @param dialect The sql dialect used to read the column values
	 * @param interfaceType The interface type to project each result row into
	 * @param expressions The selected expressions in the order they appear in the result set
	 * @param expressionNames The resolved names of the selected expressions
	 * @param <R> The type of the projected interface
	 * @return A mapper that maps a result set row to a proxy instance of the interface type
	 * @throws NullPointerException If any of the arguments is null
	 * @throws IllegalArgumentException If a method of the interface has no matching expression
	 * @throws IllegalStateException If the proxy constructor cannot be resolved or accessed
	 */
	private static <R> @NonNull ThrowableFunction<ResultSet, R, SqlException> forInterfaceProjection(
		@NonNull SqlDialect dialect,
		@NonNull Class<R> interfaceType,
		@NonNull List<SqlExpression<?>> expressions,
		@NonNull List<String> expressionNames
	) {
		Objects.requireNonNull(dialect, "Sql dialect must not be null");
		Objects.requireNonNull(interfaceType, "Interface type must not be null");
		Objects.requireNonNull(expressions, "Sql expressions must not be null");
		Objects.requireNonNull(expressionNames, "Sql expression names must not be null");
		
		Method[] methods = Arrays.stream(interfaceType.getMethods()).filter(m -> m.getParameterCount() == 0 && !m.isDefault()).toArray(Method[]::new);
		
		int[] mapping = new int[methods.length];
		String[] methodNames = new String[methods.length];
		SqlType<?>[] types = new SqlType<?>[methods.length];
		
		for (int m = 0; m < methods.length; m++) {
			String methodName = methods[m].getName();
			methodNames[m] = methodName;
			
			int expressionIndex = expressionNames.indexOf(methodName);
			if (expressionIndex < 0) {
				throw new IllegalArgumentException("Cannot project into interface " + interfaceType.getSimpleName() + ": method '" + methodName + "()' has no matching expression");
			}
			
			mapping[m] = expressionIndex;
			types[m] = expressions.get(expressionIndex).type();
		}
		
		Map<String, Integer> methodIndexMap = Maps.newHashMap();
		for (int i = 0; i < methodNames.length; i++) {
			methodIndexMap.put(methodNames[i], i);
		}
		
		Constructor<?> proxyConstructor = resolveProxyConstructor(interfaceType);
		
		return resultSet -> {
			Object[] values = new Object[methods.length];
			for (int i = 0; i < values.length; i++) {
				values[i] = SqlType.getValue(types[i], dialect, resultSet, mapping[i] + 1);
			}
			
			try {
				return (R) proxyConstructor.newInstance((InvocationHandler) new SqlRowInvocationHandler(values, methodIndexMap));
			} catch (ReflectiveOperationException e) {
				throw new SqlResultMappingException("Failed to construct proxy for " + interfaceType.getSimpleName() + " from result set", e, interfaceType);
			}
		};
	}
	
	/**
	 * Creates a row mapper that serves the given column types and names through a dynamic proxy of the row type.<br>
	 * <p>
	 *     For each zero-argument, non-default accessor of the row type a column is resolved: ordinal
	 *     accessors such as {@code first()} bind to their fixed positional column, while all other
	 *     accessors bind to the selected expression whose name matches the accessor name.<br>
	 *     The resolved column values are served through a {@link SqlRowInvocationHandler}.
	 * </p>
	 *
	 * @param rowType The row interface to map each result row to
	 * @param types The sql types of the selected columns in result set order
	 * @param expressionNames The resolved names of the selected expressions
	 * @param dialect The sql dialect used to read the column values
	 * @param <R> The type of the mapped row
	 * @return A mapper that maps a result set row to a proxy instance of the row type
	 * @throws NullPointerException If any of the arguments is null
	 * @throws IllegalArgumentException If an ordinal accessor exceeds the column count or a non-ordinal accessor matches no expression
	 * @throws IllegalStateException If the proxy constructor cannot be resolved or accessed
	 */
	private static <R> @NonNull ThrowableFunction<ResultSet, R, SqlException> createProxyMapper(@NonNull Class<R> rowType, @NonNull List<SqlType<?>> types, @NonNull List<String> expressionNames, @NonNull SqlDialect dialect) {
		Objects.requireNonNull(rowType, "Row type must not be null");
		Objects.requireNonNull(types, "Sql types must not be null");
		Objects.requireNonNull(expressionNames, "Sql expression names must not be null");
		Objects.requireNonNull(dialect, "Sql dialect must not be null");
		
		Method[] methods = Arrays.stream(rowType.getMethods()).filter(m -> m.getParameterCount() == 0 && !m.isDefault()).toArray(Method[]::new);
		
		int[] mapping = new int[methods.length];
		Map<String, Integer> methodIndexMap = Maps.newHashMap();
		
		for (int m = 0; m < methods.length; m++) {
			String name = methods[m].getName();
			
			int column;
			Integer ordinal = ORDINAL_NAMES.get(name);
			if (ordinal != null) {
				// Ordinal accessors (SqlRowN) always bind positionally, so a column aliased "first" cannot hijack first()
				if (ordinal >= types.size()) {
					throw new IllegalArgumentException("Ordinal accessor '" + name + "()' on " + rowType.getSimpleName() + " maps to column index " + ordinal + " but only " + types.size() + " columns were selected");
				}
				column = ordinal;
			} else {
				column = expressionNames.indexOf(name);
				if (column < 0) {
					throw new IllegalArgumentException("Accessor '" + name + "()' on " + rowType.getSimpleName() + " matches no selected expression and is not an ordinal accessor, use a record or forProjection(...) for named columns");
				}
			}
			
			mapping[m] = column;
			methodIndexMap.put(name, m);
		}
		
		Constructor<?> proxyConstructor = resolveProxyConstructor(rowType);
		return resultSet -> {
			Object[] values = new Object[methods.length];
			for (int i = 0; i < values.length; i++) {
				values[i] = SqlType.getValue(types.get(mapping[i]), dialect, resultSet, mapping[i] + 1);
			}
			
			try {
				return (R) proxyConstructor.newInstance((InvocationHandler) new SqlRowInvocationHandler(values, methodIndexMap));
			} catch (ReflectiveOperationException e) {
				throw new SqlResultMappingException("Failed to construct proxy for " + rowType.getSimpleName() + " from result set", e, rowType);
			}
		};
	}
	
	/**
	 * Resolves the dynamic proxy constructor for the given row type.<br>
	 * The constructor accepts a single {@link InvocationHandler} and is made accessible for direct instantiation.<br>
	 *
	 * @param rowType The interface type to resolve the proxy constructor for
	 * @return The accessible proxy constructor taking an invocation handler
	 * @throws NullPointerException If the row type is null
	 * @throws IllegalStateException If the proxy constructor cannot be resolved or accessed
	 */
	private static @NonNull Constructor<?> resolveProxyConstructor(@NonNull Class<?> rowType) {
		try {
			Object sample = Proxy.newProxyInstance(rowType.getClassLoader(), new Class<?>[] { rowType }, new SqlRowInvocationHandler(ArrayUtils.EMPTY_OBJECT_ARRAY, Map.of()));
			Constructor<?> proxyConstructor = sample.getClass().getConstructor(InvocationHandler.class);
			proxyConstructor.setAccessible(true);
			return proxyConstructor;
		} catch (NoSuchMethodException e) {
			throw new IllegalStateException("Cannot resolve proxy constructor for " + rowType.getSimpleName(), e);
		} catch (InaccessibleObjectException e) {
			throw new IllegalStateException("Cannot access proxy constructor for " + rowType.getSimpleName() +
				"; if it lives in a named module, open its package to LUtils (e.g. 'opens your.package;' in module-info.java)", e);
		}
	}
	
	/**
	 * Invocation handler that backs the dynamic row proxies created by this mapper.<br>
	 * <p>
	 *     Accessor calls are answered from the precomputed column values, looking up the value index
	 *     for the invoked method by name.<br>
	 *     The {@code equals}, {@code hashCode} and {@code toString} methods are handled directly:
	 *     equality is identity based, the hash code is derived from the values array and the string
	 *     representation lists the values.
	 * </p>
	 *
	 * @author Luis-St
	 *
	 * @param values The column values of a single result row, indexed by accessor position
	 * @param methodIndexMap The mapping from accessor name to its index in the values array
	 */
	private record SqlRowInvocationHandler(
		@NonNull Object[] values,
		@NonNull Map<String, Integer> methodIndexMap
	) implements InvocationHandler {
		
		/**
		 * Constructs a new invocation handler for the given row values and accessor index mapping.<br>
		 *
		 * @throws NullPointerException If the values array or the method index map is null
		 * @throws IllegalArgumentException If the method index map has more entries than the values array has elements
		 */
		private SqlRowInvocationHandler {
			Objects.requireNonNull(values, "Values array must not be null");
			Objects.requireNonNull(methodIndexMap, "Method index map must not be null");
			
			if (methodIndexMap.size() > values.length) {
				throw new IllegalArgumentException("Method index map size cannot be greater than values array length");
			}
		}
		
		@Override
		public @NonNull Object invoke(@NonNull Object proxy, @NonNull Method method, Object @Nullable [] args) {
			Objects.requireNonNull(proxy, "Proxy instance must not be null");
			Objects.requireNonNull(method, "Method must not be null");
			
			String name = method.getName();
			if ("equals".equals(name) && method.getParameterCount() == 1) {
				Objects.requireNonNull(args, "Arguments array must not be null");
				
				return proxy == args[0];
			}
			if ("hashCode".equals(name) && method.getParameterCount() == 0) {
				return Arrays.hashCode(this.values);
			}
			if ("toString".equals(name) && method.getParameterCount() == 0) {
				return "SqlRow" + Arrays.toString(this.values);
			}
			
			Integer index = this.methodIndexMap.get(name);
			if (index != null && index < this.values.length) {
				return this.values[index];
			}
			throw new UnsupportedOperationException("Unknown method: " + name + "(" + (args != null ? Stream.of(args).map(arg -> arg != null ? arg.getClass().getSimpleName() : "null").collect(Collectors.joining(", ")) : "") + ")");
		}
	}
}
