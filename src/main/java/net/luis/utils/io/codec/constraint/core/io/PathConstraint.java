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

package net.luis.utils.io.codec.constraint.core.io;

import net.luis.utils.io.codec.constraint.builder.*;
import net.luis.utils.io.codec.constraint.core.BaseConstraint;
import net.luis.utils.io.codec.constraint.merged.io.FilePathConstraint;
import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.function.UnaryOperator;

/**
 * Common constraint interface for path types that provides path validation operations.<br>
 * <p>
 *     This interface defines the 14 common constraint methods shared between
 *     {@link URIPathConstraint URIPathConstraint} and
 *     {@link FilePathConstraint FilePathConstraint}.<br>
 *     It provides methods for constraining paths based on their structure, components, and relationships.
 * </p>
 *
 * @author Luis-St
 *
 * @param <T> The value type for base constraint methods
 * @param <C> The return type of the constraint method (for fluent method chaining)
 */
public interface PathConstraint<T, C> extends BaseConstraint<T, C> {
	
	/**
	 * Applies length constraints to the path using a builder.<br>
	 * <p>
	 *     The returned type will validate that the path string length matches the constraints defined by the builder.
	 * </p>
	 *
	 * @param builder A function that configures the length constraint using a length constraint builder
	 * @return A new type with the applied length constraints
	 * @throws NullPointerException If the builder is null
	 */
	@NonNull C length(@NonNull UnaryOperator<LengthConstraintBuilder> builder);
	
	/**
	 * Applies depth constraints to the path using a builder.<br>
	 * <p>
	 *     The returned type will validate that the path depth (number of segments) matches the constraints defined by the builder.
	 * </p>
	 *
	 * @param builder A function that configures the depth constraint using a depth constraint builder
	 * @return A new type with the applied depth constraints
	 * @throws NullPointerException If the builder is null
	 */
	@NonNull C depth(@NonNull UnaryOperator<DepthConstraintBuilder> builder);
	
	/**
	 * Applies an absolute path constraint to the type.<br>
	 * <p>
	 *     The returned type will validate that paths are absolute.
	 * </p>
	 *
	 * @return A new type with the applied absolute constraint
	 * @see #relative()
	 */
	@NonNull C absolute();
	
	/**
	 * Applies a relative path constraint to the type.<br>
	 * <p>
	 *     The returned type will validate that paths are relative.
	 * </p>
	 *
	 * @return A new type with the applied relative constraint
	 * @see #absolute()
	 */
	@NonNull C relative();
	
	/**
	 * Applies a normalized path constraint to the type.<br>
	 * <p>
	 *     The returned type will validate that paths are normalized (contain no redundant elements like "." or "..").
	 * </p>
	 *
	 * @return A new type with the applied normalized constraint
	 */
	@NonNull C normalized();
	
	/**
	 * Applies a path string constraint using a builder.<br>
	 * <p>
	 *     The returned type will validate that the entire path string matches the constraints defined by the builder.
	 * </p>
	 *
	 * @param builder A function that configures the path string constraint using a string constraint builder
	 * @return A new type with the applied path constraint
	 * @throws NullPointerException If the builder is null
	 */
	@NonNull C path(@NonNull UnaryOperator<StringConstraintBuilder> builder);
	
	/**
	 * Applies a segment constraint to the path using a builder.<br>
	 * <p>
	 *     The returned type will validate that each path segment matches the constraints defined by the builder.
	 * </p>
	 *
	 * @param builder A function that configures the segment constraint using a string constraint builder
	 * @return A new type with the applied segment constraint
	 * @throws NullPointerException If the builder is null
	 */
	@NonNull C segment(@NonNull UnaryOperator<StringConstraintBuilder> builder);
	
	/**
	 * Applies a file name constraint to the path using a builder.<br>
	 * <p>
	 *     The returned type will validate that the file name (last segment) of the path matches the constraints defined by the builder.
	 * </p>
	 *
	 * @param builder A function that configures the file name constraint using a string constraint builder
	 * @return A new type with the applied file constraint
	 * @throws NullPointerException If the builder is null
	 * @see #extension(UnaryOperator)
	 */
	@NonNull C file(@NonNull UnaryOperator<StringConstraintBuilder> builder);
	
	/**
	 * Applies a constraint requiring the path to have no file extension.<br>
	 * <p>
	 *     The returned type will validate that paths do not have a file extension.
	 * </p>
	 *
	 * @return A new type with the applied no-extension constraint
	 * @see #extension(UnaryOperator)
	 */
	@NonNull C withoutExtension();
	
	/**
	 * Applies a file extension constraint to the path using a builder.<br>
	 * <p>
	 *     The returned type will validate that the file extension matches the constraints defined by the builder.
	 * </p>
	 *
	 * @param builder A function that configures the extension constraint using a string constraint builder
	 * @return A new type with the applied extension constraint
	 * @throws NullPointerException If the builder is null
	 * @see #withoutExtension()
	 */
	@NonNull C extension(@NonNull UnaryOperator<StringConstraintBuilder> builder);
	
	/**
	 * Applies an ancestor-of constraint to the path.<br>
	 * <p>
	 *     The returned type will validate that paths are ancestors of the specified path.
	 * </p>
	 *
	 * @param path The path that constrained paths must be ancestors of
	 * @return A new type with the applied ancestor-of constraint
	 * @throws NullPointerException If the path is null
	 * @see #ancestorOf(Collection)
	 * @see #descendantOf(String)
	 */
	@NonNull C ancestorOf(@NonNull String path);
	
	/**
	 * Applies a multi-ancestor-of constraint to the path.<br>
	 * <p>
	 *     The returned type will validate that paths are ancestors of any of the specified paths.
	 * </p>
	 *
	 * @param paths The collection of paths that constrained paths must be ancestors of
	 * @return A new type with the applied multi-ancestor-of constraint
	 * @throws NullPointerException If the collection is null
	 * @see #ancestorOf(String)
	 * @see #descendantOf(Collection)
	 */
	@NonNull C ancestorOf(@NonNull Collection<String> paths);
	
	/**
	 * Applies a descendant-of constraint to the path.<br>
	 * <p>
	 *     The returned type will validate that paths are descendants of the specified path.
	 * </p>
	 *
	 * @param path The path that constrained paths must be descendants of
	 * @return A new type with the applied descendant-of constraint
	 * @throws NullPointerException If the path is null
	 * @see #descendantOf(Collection)
	 * @see #ancestorOf(String)
	 */
	@NonNull C descendantOf(@NonNull String path);
	
	/**
	 * Applies a multi-descendant-of constraint to the path.<br>
	 * <p>
	 *     The returned type will validate that paths are descendants of any of the specified paths.
	 * </p>
	 *
	 * @param paths The collection of paths that constrained paths must be descendants of
	 * @return A new type with the applied multi-descendant-of constraint
	 * @throws NullPointerException If the collection is null
	 * @see #descendantOf(String)
	 * @see #ancestorOf(Collection)
	 */
	@NonNull C descendantOf(@NonNull Collection<String> paths);
}
