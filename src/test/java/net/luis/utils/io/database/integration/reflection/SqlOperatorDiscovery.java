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

package net.luis.utils.io.database.integration.reflection;

import net.luis.utils.io.database.condition.SqlCondition;
import net.luis.utils.io.database.function.SqlFunction;
import net.luis.utils.util.unsafe.classpath.ClassPathUtils;
import org.jspecify.annotations.NonNull;

import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Stream;

/**
 * Reflectively discovers every concrete condition / function leaf class under the database operator
 * packages, so the coverage guard can prove that {@link SqlOperatorRegistry} represents all of
 * them.<br>
 * <p>
 *     A {@code record} that implements {@link SqlCondition} or {@link SqlFunction} and lives under
 *     one of the two scanned roots is a leaf. {@code SqlUnsafeFunction} is excluded: it is a raw-SQL
 *     escape hatch (it takes an arbitrary function-name string rather than a fixed operator) and is
 *     not part of the rendered operator surface.
 * </p>
 *
 * @author Luis-St
 */
public final class SqlOperatorDiscovery {
	
	private static final String CONDITION_ROOT = "net.luis.utils.io.database.condition.conditions";
	private static final String FUNCTION_ROOT = "net.luis.utils.io.database.function.functions";
	private static final Set<String> EXCLUDED = Set.of("SqlUnsafeFunction");
	
	private static volatile Map<String, Class<?>> leaves;
	
	private SqlOperatorDiscovery() {}
	
	public static @NonNull Collection<Class<?>> discoverLeaves() {
		return leaves().values();
	}
	
	public static @NonNull Class<?> leafBySimpleName(@NonNull String simpleName) {
		Class<?> leaf = leaves().get(simpleName);
		if (leaf == null) {
			throw new IllegalStateException("No discovered operator leaf named '" + simpleName + "' - it was excluded, renamed or removed");
		}
		return leaf;
	}
	
	private static @NonNull Map<String, Class<?>> leaves() {
		Map<String, Class<?>> local = leaves;
		if (local == null) {
			synchronized (SqlOperatorDiscovery.class) {
				local = leaves;
				if (local == null) {
					local = scan();
					leaves = local;
				}
			}
		}
		return local;
	}
	
	private static @NonNull Map<String, Class<?>> scan() {
		Map<String, Class<?>> discovered = new TreeMap<>();
		Stream.concat(ClassPathUtils.getClasses(CONDITION_ROOT).stream(), ClassPathUtils.getClasses(FUNCTION_ROOT).stream())
			.filter(SqlOperatorDiscovery::isLeaf)
			.forEach(clazz -> discovered.put(clazz.getSimpleName(), clazz));
		return discovered;
	}
	
	private static boolean isLeaf(@NonNull Class<?> clazz) {
		if (!clazz.isRecord() || Modifier.isAbstract(clazz.getModifiers())) {
			return false;
		}
		if (EXCLUDED.contains(clazz.getSimpleName())) {
			return false;
		}
		return SqlCondition.class.isAssignableFrom(clazz) || SqlFunction.class.isAssignableFrom(clazz);
	}
}
