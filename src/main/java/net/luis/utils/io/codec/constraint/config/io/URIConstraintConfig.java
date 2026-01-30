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

package net.luis.utils.io.codec.constraint.config.io;

import net.luis.utils.io.codec.constraint.config.ConstraintConfig;
import net.luis.utils.io.codec.constraint.config.StringConstraintConfig;
import net.luis.utils.io.codec.constraint.config.matcher.ConstraintMatchers;
import net.luis.utils.io.codec.constraint.config.matcher.IOMatchers;
import net.luis.utils.io.codec.constraint.core.Constraint;
import net.luis.utils.io.codec.constraint.util.Unit;
import net.luis.utils.util.Pair;
import net.luis.utils.util.result.Result;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.net.URI;
import java.util.*;

/**
 * Configuration record for uri constraints.<br>
 * <p>
 *     This record stores the constraint values for uri codecs.<br>
 *     It includes base constraints and uri specific constraints for components
 *     (scheme, host, port, path, query, fragment) and structure (absolute, relative, opaque, hierarchical).
 * </p>
 *
 * @author Luis-St
 *
 * @param equalTo The equality constraint as a pair of (value, negated) where negated=false means equalTo and negated=true means notEqualTo
 * @param in The inclusion constraint as a pair of (values, negated) where negated=false means in and negated=true means notIn
 * @param scheme The string constraint config for scheme validation
 * @param host The host constraint config for host validation
 * @param withoutUserInfo If present, requires URIs to have no user info component
 * @param userInfo The string constraint config for user info validation
 * @param withoutPort If present, requires URIs to have no port component
 * @param port The port constraint config for port validation
 * @param withoutPath If present, requires URIs to have no path component
 * @param path The path constraint config for path validation
 * @param withoutQuery If present, requires URIs to have no query component
 * @param query The query constraint config for query validation
 * @param withoutFragment If present, requires URIs to have no fragment component
 * @param fragment The string constraint config for fragment validation
 * @param absolute If present, requires URIs to be absolute
 * @param relative If present, requires URIs to be relative
 * @param opaque If present, requires URIs to be opaque
 * @param hierarchical If present, requires URIs to be hierarchical
 * @param custom A custom constraint implementation
 */
public record URIConstraintConfig(
	@NonNull Optional<Pair<URI, Boolean>> equalTo,
	@NonNull Optional<Pair<Set<URI>, Boolean>> in,
	@NonNull Optional<StringConstraintConfig> scheme,
	@NonNull Optional<HostConstraintConfig> host,
	@NonNull Optional<Unit> withoutUserInfo,
	@NonNull Optional<StringConstraintConfig> userInfo,
	@NonNull Optional<Unit> withoutPort,
	@NonNull Optional<PortConstraintConfig> port,
	@NonNull Optional<Unit> withoutPath,
	@NonNull Optional<URIPathConstraintConfig> path,
	@NonNull Optional<Unit> withoutQuery,
	@NonNull Optional<QueryConstraintConfig> query,
	@NonNull Optional<Unit> withoutFragment,
	@NonNull Optional<StringConstraintConfig> fragment,
	@NonNull Optional<Unit> absolute,
	@NonNull Optional<Unit> relative,
	@NonNull Optional<Unit> opaque,
	@NonNull Optional<Unit> hierarchical,
	@NonNull Optional<Constraint<URI>> custom
) implements ConstraintConfig<URI> {
	
	/**
	 * An unconstrained uri configuration with no constraints applied.<br>
	 */
	public static final URIConstraintConfig UNCONSTRAINED = new URIConstraintConfig(
		Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
		Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
	);
	
	/**
	 * Constructs a new uri constraint config with the specified parameters.<br>
	 *
	 * @param equalTo The equality constraint as a pair of (value, negated) where negated=false means equalTo and negated=true means notEqualTo
	 * @param in The inclusion constraint as a pair of (values, negated) where negated=false means in and negated=true means notIn
	 * @param scheme The string constraint config for scheme validation
	 * @param host The host constraint config for host validation
	 * @param withoutUserInfo If present, requires URIs to have no user info component
	 * @param userInfo The string constraint config for user info validation
	 * @param withoutPort If present, requires URIs to have no port component
	 * @param port The port constraint config for port validation
	 * @param withoutPath If present, requires URIs to have no path component
	 * @param path The path constraint config for path validation
	 * @param withoutQuery If present, requires URIs to have no query component
	 * @param query The query constraint config for query validation
	 * @param withoutFragment If present, requires URIs to have no fragment component
	 * @param fragment The string constraint config for fragment validation
	 * @param absolute If present, requires URIs to be absolute
	 * @param relative If present, requires URIs to be relative
	 * @param opaque If present, requires URIs to be opaque
	 * @param hierarchical If present, requires URIs to be hierarchical
	 * @param custom A custom constraint implementation
	 * @throws NullPointerException If any of the optional fields is null
	 * @throws IllegalArgumentException If the in constraint set is empty when present
	 * @throws IllegalArgumentException If both absolute and relative constraints are present
	 * @throws IllegalArgumentException If both opaque and hierarchical constraints are present
	 * @throws IllegalArgumentException If both without user info and user info constraints are present
	 * @throws IllegalArgumentException If both without port and port constraints are present
	 * @throws IllegalArgumentException If both without path and path constraints are present
	 * @throws IllegalArgumentException If both without query and query constraints are present
	 * @throws IllegalArgumentException If both without fragment and fragment constraints are present
	 */
	public URIConstraintConfig {
		Objects.requireNonNull(equalTo, "Optional for 'equal to' constraint must not be null");
		Objects.requireNonNull(in, "Optional for 'in' constraint must not be null");
		Objects.requireNonNull(scheme, "Optional for 'scheme' constraint must not be null");
		Objects.requireNonNull(host, "Optional for 'host' constraint must not be null");
		Objects.requireNonNull(withoutUserInfo, "Optional for 'without user info' constraint must not be null");
		Objects.requireNonNull(userInfo, "Optional for 'user info' constraint must not be null");
		Objects.requireNonNull(withoutPort, "Optional for 'without port' constraint must not be null");
		Objects.requireNonNull(port, "Optional for 'port' constraint must not be null");
		Objects.requireNonNull(withoutPath, "Optional for 'without path' constraint must not be null");
		Objects.requireNonNull(path, "Optional for 'path' constraint must not be null");
		Objects.requireNonNull(withoutQuery, "Optional for 'without query' constraint must not be null");
		Objects.requireNonNull(query, "Optional for 'query' constraint must not be null");
		Objects.requireNonNull(withoutFragment, "Optional for 'without fragment' constraint must not be null");
		Objects.requireNonNull(fragment, "Optional for 'fragment' constraint must not be null");
		Objects.requireNonNull(absolute, "Optional for 'absolute' constraint must not be null");
		Objects.requireNonNull(relative, "Optional for 'relative' constraint must not be null");
		Objects.requireNonNull(opaque, "Optional for 'opaque' constraint must not be null");
		Objects.requireNonNull(hierarchical, "Optional for 'hierarchical' constraint must not be null");
		Objects.requireNonNull(custom, "Optional for 'custom' constraint must not be null");
		
		if (in.isPresent() && in.get().getFirst().isEmpty()) {
			throw new IllegalArgumentException("In constraint set must not be empty when present");
		}
		
		if (absolute.isPresent() && relative.isPresent()) {
			throw new IllegalArgumentException("Both absolute and relative constraints cannot be present at the same time");
		}
		
		if (opaque.isPresent() && hierarchical.isPresent()) {
			throw new IllegalArgumentException("Both opaque and hierarchical constraints cannot be present at the same time");
		}
		
		if (withoutUserInfo.isPresent() && userInfo.isPresent()) {
			throw new IllegalArgumentException("Both without user info and user info constraints cannot be present at the same time");
		}
		
		if (withoutPort.isPresent() && port.isPresent()) {
			throw new IllegalArgumentException("Both without port and port constraints cannot be present at the same time");
		}
		
		if (withoutPath.isPresent() && path.isPresent()) {
			throw new IllegalArgumentException("Both without path and path constraints cannot be present at the same time");
		}
		
		if (withoutQuery.isPresent() && query.isPresent()) {
			throw new IllegalArgumentException("Both without query and query constraints cannot be present at the same time");
		}
		
		if (withoutFragment.isPresent() && fragment.isPresent()) {
			throw new IllegalArgumentException("Both without fragment and fragment constraints cannot be present at the same time");
		}
	}
	
	//region With methods
	
	/**
	 * Creates a new config with the specified equal-to constraint.<br>
	 *
	 * @param value The exact uri that should be matched
	 * @throws NullPointerException If the value is null
	 * @return A new config with the constraint applied
	 */
	public @NonNull URIConstraintConfig withEqualTo(@NonNull URI value) {
		Objects.requireNonNull(value, "Value for 'equal to' constraint must not be null");
		return new URIConstraintConfig(Optional.of(Pair.of(value, false)), this.in, this.scheme, this.host, this.withoutUserInfo, this.userInfo, this.withoutPort, this.port, this.withoutPath, this.path, this.withoutQuery, this.query, this.withoutFragment, this.fragment, this.absolute, this.relative, this.opaque, this.hierarchical, this.custom);
	}
	
	/**
	 * Creates a new config with the specified not-equal-to constraint.<br>
	 *
	 * @param value The uri that should be excluded
	 * @throws NullPointerException If the value is null
	 * @return A new config with the constraint applied
	 */
	public @NonNull URIConstraintConfig withNotEqualTo(@NonNull URI value) {
		Objects.requireNonNull(value, "Value for 'not equal to' constraint must not be null");
		return new URIConstraintConfig(Optional.of(Pair.of(value, true)), this.in, this.scheme, this.host, this.withoutUserInfo, this.userInfo, this.withoutPort, this.port, this.withoutPath, this.path, this.withoutQuery, this.query, this.withoutFragment, this.fragment, this.absolute, this.relative, this.opaque, this.hierarchical, this.custom);
	}
	
	/**
	 * Creates a new config with the specified inclusion constraint.<br>
	 *
	 * @param values The collection of URIs that are allowed
	 * @throws NullPointerException If the values collection is null
	 * @return A new config with the constraint applied
	 */
	public @NonNull URIConstraintConfig withIn(@NonNull Collection<URI> values) {
		Objects.requireNonNull(values, "Values for 'in' constraint must not be null");
		return new URIConstraintConfig(this.equalTo, Optional.of(Pair.of(Set.copyOf(values), false)), this.scheme, this.host, this.withoutUserInfo, this.userInfo, this.withoutPort, this.port, this.withoutPath, this.path, this.withoutQuery, this.query, this.withoutFragment, this.fragment, this.absolute, this.relative, this.opaque, this.hierarchical, this.custom);
	}
	
	/**
	 * Creates a new config with the specified exclusion constraint.<br>
	 *
	 * @param values The collection of URIs that are not allowed
	 * @throws NullPointerException If the values collection is null
	 * @return A new config with the constraint applied
	 */
	public @NonNull URIConstraintConfig withNotIn(@NonNull Collection<URI> values) {
		Objects.requireNonNull(values, "Values for 'not in' constraint must not be null");
		return new URIConstraintConfig(this.equalTo, Optional.of(Pair.of(Set.copyOf(values), true)), this.scheme, this.host, this.withoutUserInfo, this.userInfo, this.withoutPort, this.port, this.withoutPath, this.path, this.withoutQuery, this.query, this.withoutFragment, this.fragment, this.absolute, this.relative, this.opaque, this.hierarchical, this.custom);
	}
	
	/**
	 * Creates a new config with the specified scheme constraint.<br>
	 *
	 * @param config The string constraint config for scheme validation
	 * @throws NullPointerException If the config is null
	 * @return A new config with the constraint applied
	 */
	public @NonNull URIConstraintConfig withScheme(@NonNull StringConstraintConfig config) {
		Objects.requireNonNull(config, "Config for 'scheme' constraint must not be null");
		return new URIConstraintConfig(this.equalTo, this.in, Optional.of(config), this.host, this.withoutUserInfo, this.userInfo, this.withoutPort, this.port, this.withoutPath, this.path, this.withoutQuery, this.query, this.withoutFragment, this.fragment, this.absolute, this.relative, this.opaque, this.hierarchical, this.custom);
	}
	
	/**
	 * Creates a new config with the specified host constraint.<br>
	 *
	 * @param config The host constraint config for host validation
	 * @throws NullPointerException If the config is null
	 * @return A new config with the constraint applied
	 */
	public @NonNull URIConstraintConfig withHost(@NonNull HostConstraintConfig config) {
		Objects.requireNonNull(config, "Config for 'host' constraint must not be null");
		return new URIConstraintConfig(this.equalTo, this.in, this.scheme, Optional.of(config), this.withoutUserInfo, this.userInfo, this.withoutPort, this.port, this.withoutPath, this.path, this.withoutQuery, this.query, this.withoutFragment, this.fragment, this.absolute, this.relative, this.opaque, this.hierarchical, this.custom);
	}
	
	/**
	 * Creates a new config with the no-user-info constraint.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull URIConstraintConfig withWithoutUserInfo() {
		return new URIConstraintConfig(this.equalTo, this.in, this.scheme, this.host, Optional.of(Unit.INSTANCE), this.userInfo, this.withoutPort, this.port, this.withoutPath, this.path, this.withoutQuery, this.query, this.withoutFragment, this.fragment, this.absolute, this.relative, this.opaque, this.hierarchical, this.custom);
	}
	
	/**
	 * Creates a new config with the specified user info constraint.<br>
	 *
	 * @param config The string constraint config for user info validation
	 * @throws NullPointerException If the config is null
	 * @return A new config with the constraint applied
	 */
	public @NonNull URIConstraintConfig withUserInfo(@NonNull StringConstraintConfig config) {
		Objects.requireNonNull(config, "Config for 'user info' constraint must not be null");
		return new URIConstraintConfig(this.equalTo, this.in, this.scheme, this.host, this.withoutUserInfo, Optional.of(config), this.withoutPort, this.port, this.withoutPath, this.path, this.withoutQuery, this.query, this.withoutFragment, this.fragment, this.absolute, this.relative, this.opaque, this.hierarchical, this.custom);
	}
	
	/**
	 * Creates a new config with the no-port constraint.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull URIConstraintConfig withWithoutPort() {
		return new URIConstraintConfig(this.equalTo, this.in, this.scheme, this.host, this.withoutUserInfo, this.userInfo, Optional.of(Unit.INSTANCE), this.port, this.withoutPath, this.path, this.withoutQuery, this.query, this.withoutFragment, this.fragment, this.absolute, this.relative, this.opaque, this.hierarchical, this.custom);
	}
	
	/**
	 * Creates a new config with the specified port constraint.<br>
	 *
	 * @param config The port constraint config for port validation
	 * @throws NullPointerException If the config is null
	 * @return A new config with the constraint applied
	 */
	public @NonNull URIConstraintConfig withPort(@NonNull PortConstraintConfig config) {
		Objects.requireNonNull(config, "Config for 'port' constraint must not be null");
		return new URIConstraintConfig(this.equalTo, this.in, this.scheme, this.host, this.withoutUserInfo, this.userInfo, this.withoutPort, Optional.of(config), this.withoutPath, this.path, this.withoutQuery, this.query, this.withoutFragment, this.fragment, this.absolute, this.relative, this.opaque, this.hierarchical, this.custom);
	}
	
	/**
	 * Creates a new config with the no-path constraint.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull URIConstraintConfig withWithoutPath() {
		return new URIConstraintConfig(this.equalTo, this.in, this.scheme, this.host, this.withoutUserInfo, this.userInfo, this.withoutPort, this.port, Optional.of(Unit.INSTANCE), this.path, this.withoutQuery, this.query, this.withoutFragment, this.fragment, this.absolute, this.relative, this.opaque, this.hierarchical, this.custom);
	}
	
	/**
	 * Creates a new config with the specified path constraint.<br>
	 *
	 * @param config The URI path constraint config for path validation
	 * @throws NullPointerException If the config is null
	 * @return A new config with the constraint applied
	 */
	public @NonNull URIConstraintConfig withPath(@NonNull URIPathConstraintConfig config) {
		Objects.requireNonNull(config, "Config for 'path' constraint must not be null");
		return new URIConstraintConfig(this.equalTo, this.in, this.scheme, this.host, this.withoutUserInfo, this.userInfo, this.withoutPort, this.port, this.withoutPath, Optional.of(config), this.withoutQuery, this.query, this.withoutFragment, this.fragment, this.absolute, this.relative, this.opaque, this.hierarchical, this.custom);
	}
	
	/**
	 * Creates a new config with the no-query constraint.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull URIConstraintConfig withWithoutQuery() {
		return new URIConstraintConfig(this.equalTo, this.in, this.scheme, this.host, this.withoutUserInfo, this.userInfo, this.withoutPort, this.port, this.withoutPath, this.path, Optional.of(Unit.INSTANCE), this.query, this.withoutFragment, this.fragment, this.absolute, this.relative, this.opaque, this.hierarchical, this.custom);
	}
	
	/**
	 * Creates a new config with the specified query constraint.<br>
	 *
	 * @param config The query constraint config for query validation
	 * @throws NullPointerException If the config is null
	 * @return A new config with the constraint applied
	 */
	public @NonNull URIConstraintConfig withQuery(@NonNull QueryConstraintConfig config) {
		Objects.requireNonNull(config, "Config for 'query' constraint must not be null");
		return new URIConstraintConfig(this.equalTo, this.in, this.scheme, this.host, this.withoutUserInfo, this.userInfo, this.withoutPort, this.port, this.withoutPath, this.path, this.withoutQuery, Optional.of(config), this.withoutFragment, this.fragment, this.absolute, this.relative, this.opaque, this.hierarchical, this.custom);
	}
	
	/**
	 * Creates a new config with the no-fragment constraint.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull URIConstraintConfig withWithoutFragment() {
		return new URIConstraintConfig(this.equalTo, this.in, this.scheme, this.host, this.withoutUserInfo, this.userInfo, this.withoutPort, this.port, this.withoutPath, this.path, this.withoutQuery, this.query, Optional.of(Unit.INSTANCE), this.fragment, this.absolute, this.relative, this.opaque, this.hierarchical, this.custom);
	}
	
	/**
	 * Creates a new config with the specified fragment constraint.<br>
	 *
	 * @param config The string constraint config for fragment validation
	 * @throws NullPointerException If the config is null
	 * @return A new config with the constraint applied
	 */
	public @NonNull URIConstraintConfig withFragment(@NonNull StringConstraintConfig config) {
		Objects.requireNonNull(config, "Config for 'fragment' constraint must not be null");
		return new URIConstraintConfig(this.equalTo, this.in, this.scheme, this.host, this.withoutUserInfo, this.userInfo, this.withoutPort, this.port, this.withoutPath, this.path, this.withoutQuery, this.query, this.withoutFragment, Optional.of(config), this.absolute, this.relative, this.opaque, this.hierarchical, this.custom);
	}
	
	/**
	 * Creates a new config with the absolute uri constraint.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull URIConstraintConfig withAbsolute() {
		return new URIConstraintConfig(this.equalTo, this.in, this.scheme, this.host, this.withoutUserInfo, this.userInfo, this.withoutPort, this.port, this.withoutPath, this.path, this.withoutQuery, this.query, this.withoutFragment, this.fragment, Optional.of(Unit.INSTANCE), this.relative, this.opaque, this.hierarchical, this.custom);
	}
	
	/**
	 * Creates a new config with the relative uri constraint.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull URIConstraintConfig withRelative() {
		return new URIConstraintConfig(this.equalTo, this.in, this.scheme, this.host, this.withoutUserInfo, this.userInfo, this.withoutPort, this.port, this.withoutPath, this.path, this.withoutQuery, this.query, this.withoutFragment, this.fragment, this.absolute, Optional.of(Unit.INSTANCE), this.opaque, this.hierarchical, this.custom);
	}
	
	/**
	 * Creates a new config with the opaque uri constraint.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull URIConstraintConfig withOpaque() {
		return new URIConstraintConfig(this.equalTo, this.in, this.scheme, this.host, this.withoutUserInfo, this.userInfo, this.withoutPort, this.port, this.withoutPath, this.path, this.withoutQuery, this.query, this.withoutFragment, this.fragment, this.absolute, this.relative, Optional.of(Unit.INSTANCE), this.hierarchical, this.custom);
	}
	
	/**
	 * Creates a new config with the hierarchical uri constraint.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull URIConstraintConfig withHierarchical() {
		return new URIConstraintConfig(this.equalTo, this.in, this.scheme, this.host, this.withoutUserInfo, this.userInfo, this.withoutPort, this.port, this.withoutPath, this.path, this.withoutQuery, this.query, this.withoutFragment, this.fragment, this.absolute, this.relative, this.opaque, Optional.of(Unit.INSTANCE), this.custom);
	}
	
	/**
	 * Creates a new config with the specified custom constraint.<br>
	 *
	 * @param constraint The custom constraint implementation
	 * @throws NullPointerException If the constraint is null
	 * @return A new config with the constraint applied
	 */
	public @NonNull URIConstraintConfig withCustom(@NonNull Constraint<URI> constraint) {
		Objects.requireNonNull(constraint, "Custom constraint must not be null");
		return new URIConstraintConfig(this.equalTo, this.in, this.scheme, this.host, this.withoutUserInfo, this.userInfo, this.withoutPort, this.port, this.withoutPath, this.path, this.withoutQuery, this.query, this.withoutFragment, this.fragment, this.absolute, this.relative, this.opaque, this.hierarchical, Optional.of(constraint));
	}
	//endregion
	
	@Override
	public @NotNull Result<Void> matches(@NonNull URI value) {
		Objects.requireNonNull(value, "Value must not be null");
		
		return ConstraintMatchers.allOf(
			() -> ConstraintMatchers.matchEqualTo(value, this.equalTo),
			() -> ConstraintMatchers.matchIn(value, this.in),
			() -> IOMatchers.matchUriSchemeConfig(value, this.scheme),
			() -> IOMatchers.matchUriHostConfig(value, this.host),
			() -> ConstraintMatchers.matchFlag(value, this.withoutUserInfo, uri -> uri.getUserInfo() == null, "URI '" + value + "' must not have user info"),
			() -> IOMatchers.matchUriUserInfoConfig(value, this.userInfo),
			() -> ConstraintMatchers.matchFlag(value, this.withoutPort, uri -> uri.getPort() == -1, "URI '" + value + "' must not have a port"),
			() -> IOMatchers.matchUriPortConfig(value, this.port),
			() -> ConstraintMatchers.matchFlag(value, this.withoutPath, uri -> uri.getPath() == null || uri.getPath().isEmpty(), "URI '" + value + "' must not have a path"),
			() -> IOMatchers.matchUriPathConfig(value, this.path),
			() -> ConstraintMatchers.matchFlag(value, this.withoutQuery, uri -> uri.getQuery() == null, "URI '" + value + "' must not have a query"),
			() -> IOMatchers.matchUriQueryConfig(value, this.query),
			() -> ConstraintMatchers.matchFlag(value, this.withoutFragment, uri -> uri.getFragment() == null, "URI '" + value + "' must not have a fragment"),
			() -> IOMatchers.matchUriFragmentConfig(value, this.fragment),
			() -> ConstraintMatchers.matchFlag(value, this.absolute, URI::isAbsolute, "URI '" + value + "' must be absolute"),
			() -> ConstraintMatchers.matchFlag(value, this.relative, uri -> !uri.isAbsolute(), "URI '" + value + "' must be relative"),
			() -> ConstraintMatchers.matchFlag(value, this.opaque, URI::isOpaque, "URI '" + value + "' must be opaque"),
			() -> ConstraintMatchers.matchFlag(value, this.hierarchical, uri -> !uri.isOpaque(), "URI '" + value + "' must be hierarchical"),
			() -> ConstraintMatchers.matchCustom(value, this.custom)
		);
	}
}
