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
 *
 * @author Luis-St
 *
 */

@SuppressWarnings("unchecked")
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
	
	private static @NonNull Constructor<?> resolveProxyConstructor(@NonNull Class<?> rowType) {
		try {
			// Create one throwaway proxy to obtain (and cache) the proxy class once per mapper, then reuse its constructor per row.
			// This avoids the deprecated Proxy.getProxyClass while still skipping the per-row proxy-class cache lookup of Proxy.newProxyInstance.
			Object sample = Proxy.newProxyInstance(rowType.getClassLoader(), new Class<?>[] { rowType }, new SqlRowInvocationHandler(ArrayUtils.EMPTY_OBJECT_ARRAY, Map.of()));
			Constructor<?> proxyConstructor = sample.getClass().getConstructor(InvocationHandler.class);
			proxyConstructor.setAccessible(true);
			return proxyConstructor;
		} catch (NoSuchMethodException e) {
			throw new IllegalStateException("Cannot resolve proxy constructor for " + rowType.getSimpleName(), e);
		}
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
