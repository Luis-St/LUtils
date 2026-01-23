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

package net.luis.utils.io.codec.constraint.merged.io;

import net.luis.utils.io.codec.constraint.merged.collection.MapConstraint;
import net.luis.utils.io.codec.constraint_new.builder.SizeConstraintBuilder;
import net.luis.utils.io.codec.constraint_new.builder.StringConstraintBuilder;
import net.luis.utils.io.codec.constraint.config.collection.MapConstraintConfig;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

/**
 * Constraint interface for URI query parameter types that provides query validation operations.<br>
 * <p>
 *     This interface extends {@link MapConstraint} with methods for constraining query parameters
 *     based on value content, single/multi-value requirements, and pattern matching.<br>
 *     It allows validation of URI query strings as key-value pairs where values can be multi-valued.
 * </p>
 *
 * @author Luis-St
 *
 * @param <C> The return type of the constraint method (for fluent method chaining)
 */
public interface QueryConstraint<C> extends MapConstraint<String, List<String>, C> {
	
	// ToDo: Fix
	
	@Override
	@NonNull C apply(@NonNull UnaryOperator<MapConstraintConfig<String, List<String>>> configModifier);
	
	/**
	 * Applies a value constraint to a specific query parameter key using a builder.<br>
	 * <p>
	 *     The returned type will validate that the value(s) for the specified key match
	 *     the constraints defined by the builder.
	 * </p>
	 *
	 * @param key The query parameter key to constrain
	 * @param builder A function that configures the value constraint using a string constraint builder
	 * @return A new type with the applied value constraint
	 * @throws NullPointerException If the key or builder is null
	 * @see #values(String, UnaryOperator)
	 */
	@NonNull C value(@NonNull String key, @NonNull UnaryOperator<StringConstraintBuilder> builder);
	
	/**
	 * Applies a value constraint to query parameter keys matching a regex pattern using a builder.<br>
	 * <p>
	 *     The returned type will validate that values for all keys matching the regex pattern
	 *     satisfy the constraints defined by the builder.
	 * </p>
	 *
	 * @param regex The regular expression to match query parameter keys
	 * @param builder A function that configures the value constraint using a string constraint builder
	 * @return A new type with the applied values constraint
	 * @throws NullPointerException If the regex or builder is null
	 * @see #values(Pattern, UnaryOperator)
	 * @see #value(String, UnaryOperator)
	 */
	@NonNull C values(@NonNull String regex, @NonNull UnaryOperator<StringConstraintBuilder> builder);
	
	/**
	 * Applies a value constraint to query parameter keys matching a compiled pattern using a builder.<br>
	 * <p>
	 *     The returned type will validate that values for all keys matching the pattern
	 *     satisfy the constraints defined by the builder.<br>
	 *     This method is more efficient than {@link #values(String, UnaryOperator)} when the same pattern is used multiple times.
	 * </p>
	 *
	 * @param pattern The compiled pattern to match query parameter keys
	 * @param builder A function that configures the value constraint using a string constraint builder
	 * @return A new type with the applied values constraint
	 * @throws NullPointerException If the pattern or builder is null
	 * @see #values(String, UnaryOperator)
	 */
	@NonNull C values(@NonNull Pattern pattern, @NonNull UnaryOperator<StringConstraintBuilder> builder);
	
	/**
	 * Applies a single-valued constraint to the query parameters.<br>
	 * <p>
	 *     The returned type will validate that all query parameter keys have exactly one value.
	 * </p>
	 *
	 * @return A new type with the applied single-valued constraint
	 * @see #multiValued(String, UnaryOperator)
	 */
	@NonNull C singleValued();
	
	/**
	 * Applies a multi-valued constraint to a specific query parameter key using a builder.<br>
	 * <p>
	 *     The returned type will validate that the specified key has multiple values matching
	 *     the size constraints defined by the builder.
	 * </p>
	 *
	 * @param key The query parameter key to constrain
	 * @param builder A function that configures the size constraint using a size constraint builder
	 * @return A new type with the applied multi-valued constraint
	 * @throws NullPointerException If the key or builder is null
	 * @see #singleValued()
	 */
	@NonNull C multiValued(@NonNull String key, @NonNull UnaryOperator<SizeConstraintBuilder> builder);
}
