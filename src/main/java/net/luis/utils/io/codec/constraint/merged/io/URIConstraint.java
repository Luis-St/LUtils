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
import net.luis.utils.io.codec.constraint.config.io.URIConstraintConfig;
import net.luis.utils.io.codec.constraint.core.*;
import org.jspecify.annotations.NonNull;

import java.net.URI;
import java.util.Collection;
import java.util.Objects;
import java.util.function.UnaryOperator;

/**
 * Constraint interface for URI types that provides comprehensive URI validation operations.<br>
 * <p>
 *     This interface extends {@link ApplicableConstraint} and {@link BaseConstraint} with methods for constraining URIs
 *     based on their components (scheme, host, port, path, query, fragment) and structure (absolute, relative, opaque, hierarchical).<br>
 *     It allows fine-grained validation of URI structure according to RFC 3986.
 * </p>
 *
 * @author Luis-St
 *
 * @param <C> The return type of the constraint method (for fluent method chaining)
 */
@FunctionalInterface
public interface URIConstraint<C> extends ApplicableConstraint<URIConstraintConfig, C>, BaseConstraint<URI, C> {
	
	@Override
	@NonNull C apply(@NonNull UnaryOperator<URIConstraintConfig> configModifier);
	
	@Override
	default @NonNull C equalTo(@NonNull URI value) {
		return this.apply(config -> config.withEqualTo(value));
	}
	
	@Override
	default @NonNull C notEqualTo(@NonNull URI value) {
		return this.apply(config -> config.withNotEqualTo(value));
	}
	
	@Override
	default @NonNull C in(@NonNull Collection<URI> values) {
		return this.apply(config -> config.withIn(values));
	}
	
	@Override
	default @NonNull C notIn(@NonNull Collection<URI> values) {
		return this.apply(config -> config.withNotIn(values));
	}
	
	@Override
	default @NonNull C custom(@NonNull Constraint<URI> constraint) {
		return this.apply(config -> config.withCustom(constraint));
	}
	
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
	default @NonNull C scheme(@NonNull UnaryOperator<StringConstraintBuilder> builder) {
		Objects.requireNonNull(builder, "Builder must not be null");
		
		StringConstraintBuilder stringBuilder = new StringConstraintBuilder();
		builder.apply(stringBuilder);
		return this.apply(config -> config.withScheme(stringBuilder.build()));
	}
	
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
	default @NonNull C host(@NonNull UnaryOperator<HostConstraintBuilder> builder) {
		Objects.requireNonNull(builder, "Builder must not be null");
		
		HostConstraintBuilder hostBuilder = new HostConstraintBuilder();
		builder.apply(hostBuilder);
		return this.apply(config -> config.withHost(hostBuilder.build()));
	}
	
	/**
	 * Applies a constraint requiring the URI to have no user info component.<br>
	 * <p>
	 *     The returned type will validate that URIs do not contain a user info component (user:password@).
	 * </p>
	 *
	 * @return A new type with the applied no-user-info constraint
	 * @see #userInfo(UnaryOperator)
	 */
	default @NonNull C withoutUserInfo() {
		return this.apply(URIConstraintConfig::withWithoutUserInfo);
	}
	
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
	default @NonNull C userInfo(@NonNull UnaryOperator<StringConstraintBuilder> builder) {
		Objects.requireNonNull(builder, "Builder must not be null");
		
		StringConstraintBuilder stringBuilder = new StringConstraintBuilder();
		builder.apply(stringBuilder);
		return this.apply(config -> config.withUserInfo(stringBuilder.build()));
	}
	
	/**
	 * Applies a constraint requiring the URI to have no port component.<br>
	 * <p>
	 *     The returned type will validate that URIs do not specify a port number.
	 * </p>
	 *
	 * @return A new type with the applied no-port constraint
	 * @see #port(UnaryOperator)
	 */
	default @NonNull C withoutPort() {
		return this.apply(URIConstraintConfig::withWithoutPort);
	}
	
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
	default @NonNull C port(@NonNull UnaryOperator<PortConstraintBuilder> builder) {
		Objects.requireNonNull(builder, "Builder must not be null");
		
		PortConstraintBuilder portBuilder = new PortConstraintBuilder();
		builder.apply(portBuilder);
		return this.apply(config -> config.withPort(portBuilder.build()));
	}
	
	/**
	 * Applies a constraint requiring the URI to have no path component.<br>
	 * <p>
	 *     The returned type will validate that URIs do not contain a path component.
	 * </p>
	 *
	 * @return A new type with the applied no-path constraint
	 * @see #path(UnaryOperator)
	 */
	default @NonNull C withoutPath() {
		return this.apply(URIConstraintConfig::withWithoutPath);
	}
	
	/**
	 * Applies a path constraint to the URI using a builder.<br>
	 * <p>
	 *     The returned type will validate that the URI path matches the constraints defined by the builder.
	 * </p>
	 *
	 * @param builder A function that configures the path constraint using a URI path constraint builder
	 * @return A new type with the applied path constraint
	 * @throws NullPointerException If the builder is null
	 * @see #withoutPath()
	 */
	default @NonNull C path(@NonNull UnaryOperator<URIPathConstraintBuilder> builder) {
		Objects.requireNonNull(builder, "Builder must not be null");
		
		URIPathConstraintBuilder pathBuilder = new URIPathConstraintBuilder();
		builder.apply(pathBuilder);
		return this.apply(config -> config.withPath(pathBuilder.build()));
	}
	
	/**
	 * Applies a constraint requiring the URI to have no query component.<br>
	 * <p>
	 *     The returned type will validate that URIs do not contain a query string.
	 * </p>
	 *
	 * @return A new type with the applied no-query constraint
	 * @see #query(UnaryOperator)
	 */
	default @NonNull C withoutQuery() {
		return this.apply(URIConstraintConfig::withWithoutQuery);
	}
	
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
	default @NonNull C query(@NonNull UnaryOperator<QueryConstraintBuilder> builder) {
		Objects.requireNonNull(builder, "Builder must not be null");
		
		QueryConstraintBuilder queryBuilder = new QueryConstraintBuilder();
		builder.apply(queryBuilder);
		return this.apply(config -> config.withQuery(queryBuilder.build()));
	}
	
	/**
	 * Applies a constraint requiring the URI to have no fragment component.<br>
	 * <p>
	 *     The returned type will validate that URIs do not contain a fragment identifier.
	 * </p>
	 *
	 * @return A new type with the applied no-fragment constraint
	 * @see #fragment(UnaryOperator)
	 */
	default @NonNull C withoutFragment() {
		return this.apply(URIConstraintConfig::withWithoutFragment);
	}
	
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
	default @NonNull C fragment(@NonNull UnaryOperator<StringConstraintBuilder> builder) {
		Objects.requireNonNull(builder, "Builder must not be null");
		
		StringConstraintBuilder stringBuilder = new StringConstraintBuilder();
		builder.apply(stringBuilder);
		return this.apply(config -> config.withFragment(stringBuilder.build()));
	}
	
	/**
	 * Applies an absolute URI constraint to the type.<br>
	 * <p>
	 *     The returned type will validate that URIs are absolute (have a scheme component).
	 * </p>
	 *
	 * @return A new type with the applied absolute constraint
	 * @see #relative()
	 */
	default @NonNull C absolute() {
		return this.apply(URIConstraintConfig::withAbsolute);
	}
	
	/**
	 * Applies a relative URI constraint to the type.<br>
	 * <p>
	 *     The returned type will validate that URIs are relative (do not have a scheme component).
	 * </p>
	 *
	 * @return A new type with the applied relative constraint
	 * @see #absolute()
	 */
	default @NonNull C relative() {
		return this.apply(URIConstraintConfig::withRelative);
	}
	
	/**
	 * Applies an opaque URI constraint to the type.<br>
	 * <p>
	 *     The returned type will validate that URIs are opaque (absolute URIs whose scheme-specific part does not begin with a slash).
	 * </p>
	 *
	 * @return A new type with the applied opaque constraint
	 * @see #hierarchical()
	 */
	default @NonNull C opaque() {
		return this.apply(URIConstraintConfig::withOpaque);
	}
	
	/**
	 * Applies a hierarchical URI constraint to the type.<br>
	 * <p>
	 *     The returned type will validate that URIs are hierarchical (either relative or absolute URIs whose scheme-specific part begins with a slash).
	 * </p>
	 *
	 * @return A new type with the applied hierarchical constraint
	 * @see #opaque()
	 */
	default @NonNull C hierarchical() {
		return this.apply(URIConstraintConfig::withHierarchical);
	}
}
