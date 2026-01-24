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

import net.luis.utils.io.codec.constraint.builder.*;
import net.luis.utils.io.codec.constraint.core.BaseConstraint;
import org.jspecify.annotations.NonNull;

import java.util.function.UnaryOperator;

/**
 * Constraint interface for URI types that provides comprehensive URI validation operations.<br>
 * <p>
 *     This interface extends {@link BaseConstraint} with methods for constraining URIs
 *     based on their components (scheme, host, port, path, query, fragment) and structure (absolute, relative, opaque, hierarchical).<br>
 *     It allows fine-grained validation of URI structure according to RFC 3986.
 * </p>
 *
 * @author Luis-St
 *
 * @param <T> The type of the constraint configuration
 * @param <C> The return type of the constraint method (for fluent method chaining)
 */
public interface URIConstraint<T, C> extends BaseConstraint<T, C> {
	
	/**
	 * Applies a scheme constraint to the URI using a builder.<br>
	 * <p>
	 *     The returned type will validate that the URI scheme matches the constraints defined by the builder.
	 * </p>
	 *
	 * @param builder A function that configures the scheme constraint using a string constraint builder
	 * @return A new type with the applied scheme constraint
	 * @throws NullPointerException If the builder is null
	 */
	@NonNull C scheme(@NonNull UnaryOperator<StringConstraintBuilder> builder);
	
	/**
	 * Applies a host constraint to the URI using a builder.<br>
	 * <p>
	 *     The returned type will validate that the URI host matches the constraints defined by the builder.
	 * </p>
	 *
	 * @param builder A function that configures the host constraint using a host constraint builder
	 * @return A new type with the applied host constraint
	 * @throws NullPointerException If the builder is null
	 */
	@NonNull C host(@NonNull UnaryOperator<HostConstraintBuilder> builder);
	
	/**
	 * Applies a constraint requiring the URI to have no user info component.<br>
	 * <p>
	 *     The returned type will validate that URIs do not contain a user info component (user:password@).
	 * </p>
	 *
	 * @return A new type with the applied no-user-info constraint
	 * @see #userInfo(UnaryOperator)
	 */
	@NonNull C withoutUserInfo();
	
	/**
	 * Applies a user info constraint to the URI using a builder.<br>
	 * <p>
	 *     The returned type will validate that the URI user info matches the constraints defined by the builder.
	 * </p>
	 *
	 * @param builder A function that configures the user info constraint using a string constraint builder
	 * @return A new type with the applied user info constraint
	 * @throws NullPointerException If the builder is null
	 * @see #withoutUserInfo()
	 */
	@NonNull C userInfo(@NonNull UnaryOperator<StringConstraintBuilder> builder);
	
	/**
	 * Applies a constraint requiring the URI to have no port component.<br>
	 * <p>
	 *     The returned type will validate that URIs do not specify a port number.
	 * </p>
	 *
	 * @return A new type with the applied no-port constraint
	 * @see #port(UnaryOperator)
	 */
	@NonNull C withoutPort();
	
	/**
	 * Applies a port constraint to the URI using a builder.<br>
	 * <p>
	 *     The returned type will validate that the URI port matches the constraints defined by the builder.
	 * </p>
	 *
	 * @param builder A function that configures the port constraint using a port constraint builder
	 * @return A new type with the applied port constraint
	 * @throws NullPointerException If the builder is null
	 * @see #withoutPort()
	 */
	@NonNull C port(@NonNull UnaryOperator<PortConstraintBuilder> builder);
	
	/**
	 * Applies a constraint requiring the URI to have no path component.<br>
	 * <p>
	 *     The returned type will validate that URIs do not contain a path component.
	 * </p>
	 *
	 * @return A new type with the applied no-path constraint
	 * @see #path(UnaryOperator)
	 */
	@NonNull C withoutPath();
	
	/**
	 * Applies a path constraint to the URI using a builder.<br>
	 * <p>
	 *     The returned type will validate that the URI path matches the constraints defined by the builder.
	 * </p>
	 *
	 * @param builder A function that configures the path constraint using a path constraint builder
	 * @return A new type with the applied path constraint
	 * @throws NullPointerException If the builder is null
	 * @see #withoutPath()
	 */
	@NonNull C path(@NonNull UnaryOperator<PathConstraintBuilder> builder);
	
	/**
	 * Applies a constraint requiring the URI to have no query component.<br>
	 * <p>
	 *     The returned type will validate that URIs do not contain a query string.
	 * </p>
	 *
	 * @return A new type with the applied no-query constraint
	 * @see #query(UnaryOperator)
	 */
	@NonNull C withoutQuery();
	
	/**
	 * Applies a query constraint to the URI using a builder.<br>
	 * <p>
	 *     The returned type will validate that the URI query matches the constraints defined by the builder.
	 * </p>
	 *
	 * @param builder A function that configures the query constraint using a query constraint builder
	 * @return A new type with the applied query constraint
	 * @throws NullPointerException If the builder is null
	 * @see #withoutQuery()
	 */
	@NonNull C query(@NonNull UnaryOperator<QueryConstraintBuilder> builder);
	
	/**
	 * Applies a constraint requiring the URI to have no fragment component.<br>
	 * <p>
	 *     The returned type will validate that URIs do not contain a fragment identifier.
	 * </p>
	 *
	 * @return A new type with the applied no-fragment constraint
	 * @see #fragment(UnaryOperator)
	 */
	@NonNull C withoutFragment();
	
	/**
	 * Applies a fragment constraint to the URI using a builder.<br>
	 * <p>
	 *     The returned type will validate that the URI fragment matches the constraints defined by the builder.
	 * </p>
	 *
	 * @param builder A function that configures the fragment constraint using a string constraint builder
	 * @return A new type with the applied fragment constraint
	 * @throws NullPointerException If the builder is null
	 * @see #withoutFragment()
	 */
	@NonNull C fragment(@NonNull UnaryOperator<StringConstraintBuilder> builder);
	
	/**
	 * Applies an absolute URI constraint to the type.<br>
	 * <p>
	 *     The returned type will validate that URIs are absolute (have a scheme component).
	 * </p>
	 *
	 * @return A new type with the applied absolute constraint
	 * @see #relative()
	 */
	@NonNull C absolute();
	
	/**
	 * Applies a relative URI constraint to the type.<br>
	 * <p>
	 *     The returned type will validate that URIs are relative (do not have a scheme component).
	 * </p>
	 *
	 * @return A new type with the applied relative constraint
	 * @see #absolute()
	 */
	@NonNull C relative();
	
	/**
	 * Applies an opaque URI constraint to the type.<br>
	 * <p>
	 *     The returned type will validate that URIs are opaque (absolute URIs whose scheme-specific part does not begin with a slash).
	 * </p>
	 *
	 * @return A new type with the applied opaque constraint
	 * @see #hierarchical()
	 */
	@NonNull C opaque();
	
	/**
	 * Applies a hierarchical URI constraint to the type.<br>
	 * <p>
	 *     The returned type will validate that URIs are hierarchical (either relative or absolute URIs whose scheme-specific part begins with a slash).
	 * </p>
	 *
	 * @return A new type with the applied hierarchical constraint
	 * @see #opaque()
	 */
	@NonNull C hierarchical();
}
