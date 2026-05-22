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
import net.luis.utils.function.throwable.ThrowableFunction;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.expression.SqlAliasedExpression;
import net.luis.utils.io.database.expression.SqlExpression;
import net.luis.utils.io.database.table.SqlAliasedColumn;
import net.luis.utils.io.database.table.SqlColumn;
import net.luis.utils.io.database.type.SqlType;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.*;
import java.sql.ResultSet;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author Luis-St
 *
 */

public final class SqlRowMapper {
	
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
	
	private SqlRowMapper() {}
	
	private static @NonNull String resolveExpressionName(@NonNull SqlExpression<?> expression) {
		return switch (expression) {
			case SqlColumn<?, ?> column -> column.name();
			case SqlAliasedExpression<?> aliased -> aliased.alias().get();
			case SqlAliasedColumn<?, ?> aliasColumn -> aliasColumn.column().name();
			
			case null -> throw new NullPointerException("Sql expression must not be null");
			default -> expression.getClass().getSimpleName();
		};
	}
	
	public static <R> @NonNull ThrowableFunction<ResultSet, R, SqlException> forExpressions(@NonNull Class<R> rowType, @NonNull List<SqlExpression<?>> expressions) {
		Objects.requireNonNull(rowType, "Row type must not be null");
		Objects.requireNonNull(expressions, "Sql expressions must not be null");
		
		List<SqlType<?>> types = new ArrayList<>(expressions.size());
		for (SqlExpression<?> expression : expressions) {
			types.add(expression.type());
		}
		return createProxyMapper(rowType, types);
	}
	
	public static <R> @NonNull ThrowableFunction<ResultSet, R, SqlException> forProjection(@NonNull Class<R> targetType, @NonNull List<SqlExpression<?>> expressions) {
		Objects.requireNonNull(targetType, "Target type must not be null");
		Objects.requireNonNull(expressions, "Sql expressions must not be null");
		
		List<String> expressionNames = Lists.newArrayList();
		for (SqlExpression<?> expression : expressions) {
			expressionNames.add(resolveExpressionName(expression));
		}
		
		if (targetType.isRecord()) {
			return forRecordProjection(targetType, expressions, expressionNames);
		}
		if (targetType.isInterface()) {
			return forInterfaceProjection(targetType, expressions, expressionNames);
		}
		throw new IllegalArgumentException("Sql projection target must be an interface or a record, got: " + targetType.getName());
	}
	
	private static <R> @NonNull ThrowableFunction<ResultSet, R, SqlException> forRecordProjection(@NonNull Class<R> recordType, @NonNull List<SqlExpression<?>> expressions, @NonNull List<String> expressionNames) {
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
		}
		
		return resultSet -> {
			Object[] args = new Object[components.length];
			for (int i = 0; i < args.length; i++) {
				args[i] = types[i].get(resultSet, mapping[i] + 1);
			}
			
			try {
				return constructor.newInstance(args);
			} catch (Exception e) {
				throw new SqlException("Failed to construct record " + recordType.getSimpleName() + " from result set", e);
			}
		};
	}
	
	@SuppressWarnings("unchecked")
	private static <R> @NonNull ThrowableFunction<ResultSet, R, SqlException> forInterfaceProjection(@NonNull Class<R> interfaceType, @NonNull List<SqlExpression<?>> expressions, @NonNull List<String> expressionNames) {
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
		
		Map<String, Integer> methodIndexMap = new HashMap<>();
		for (int i = 0; i < methodNames.length; i++) {
			methodIndexMap.put(methodNames[i], i);
		}
		
		return resultSet -> {
			Object[] values = new Object[methods.length];
			for (int i = 0; i < values.length; i++) {
				values[i] = types[i].get(resultSet, mapping[i] + 1);
			}
			
			return (R) Proxy.newProxyInstance(
				interfaceType.getClassLoader(),
				new Class<?>[] { interfaceType },
				new SqlRowInvocationHandler(values, methodIndexMap)
			);
		};
	}
	
	@SuppressWarnings("unchecked")
	private static <R> @NonNull ThrowableFunction<ResultSet, R, SqlException> createProxyMapper(@NonNull Class<R> rowType, @NonNull List<SqlType<?>> types) {
		Objects.requireNonNull(rowType, "Row type must not be null");
		Objects.requireNonNull(types, "Sql types must not be null");
		
		Method[] methods = Arrays.stream(rowType.getMethods()).filter(m -> m.getParameterCount() == 0 && !m.isDefault()).toArray(Method[]::new);
		
		Arrays.sort(methods, Comparator.comparingInt(m -> {
			Integer index = ORDINAL_NAMES.get(m.getName());
			return index != null ? index : Integer.MAX_VALUE;
		}));
		
		Map<String, Integer> methodIndexMap = new HashMap<>();
		for (int i = 0; i < methods.length; i++) {
			methodIndexMap.put(methods[i].getName(), i);
		}
		
		return resultSet -> {
			Object[] values = new Object[types.size()];
			for (int i = 0; i < values.length; i++) {
				values[i] = types.get(i).get(resultSet, i + 1);
			}
			
			return (R) Proxy.newProxyInstance(
				rowType.getClassLoader(),
				new Class<?>[] { rowType },
				new SqlRowInvocationHandler(values, methodIndexMap)
			);
		};
	}
	
	private record SqlRowInvocationHandler(
		@NonNull Object[] values,
		@NonNull Map<String, Integer> methodIndexMap
	) implements InvocationHandler {
		
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
