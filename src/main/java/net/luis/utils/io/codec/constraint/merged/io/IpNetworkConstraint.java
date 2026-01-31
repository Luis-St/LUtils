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

import net.luis.utils.io.codec.constraint.builder.LengthConstraintBuilder;
import net.luis.utils.io.codec.constraint.builder.StringConstraintBuilder;
import net.luis.utils.io.codec.constraint.config.EnumConstraintConfig;
import net.luis.utils.io.codec.constraint.config.io.IpNetworkConstraintConfig;
import net.luis.utils.io.codec.constraint.core.*;
import net.luis.utils.io.codec.constraint.util.IpVersion;
import net.luis.utils.io.network.address.IpNetwork;
import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.Objects;
import java.util.function.UnaryOperator;

/**
 * Constraint interface for IpNetwork types that provides comprehensive IP network validation operations.<br>
 * <p>
 *     This interface extends {@link ApplicableConstraint} and {@link BaseConstraint} with methods for constraining IpNetworks
 *     based on IP version, prefix length, and canonical form.<br>
 *     It allows fine-grained validation of IP networks for network-related codecs.
 * </p>
 *
 * @author Luis-St
 *
 * @param <C> The return type of the constraint method (for fluent method chaining)
 */
@FunctionalInterface
public interface IpNetworkConstraint<C> extends ApplicableConstraint<IpNetworkConstraintConfig, C>, BaseConstraint<IpNetwork<?, ?>, C> {
	
	@Override
	@NonNull C apply(@NonNull UnaryOperator<IpNetworkConstraintConfig> configModifier);
	
	@Override
	default @NonNull C equalTo(@NonNull IpNetwork<?, ?> value) {
		return this.apply(config -> config.withEqualTo(value));
	}
	
	@Override
	default @NonNull C notEqualTo(@NonNull IpNetwork<?, ?> value) {
		return this.apply(config -> config.withNotEqualTo(value));
	}
	
	@Override
	default @NonNull C in(@NonNull Collection<IpNetwork<?, ?>> values) {
		return this.apply(config -> config.withIn(values));
	}
	
	@Override
	default @NonNull C notIn(@NonNull Collection<IpNetwork<?, ?>> values) {
		return this.apply(config -> config.withNotIn(values));
	}
	
	@Override
	default @NonNull C custom(@NonNull Constraint<IpNetwork<?, ?>> constraint) {
		return this.apply(config -> config.withCustom(constraint));
	}
	
	/**
	 * Applies an IP version constraint to the IpNetwork.<br>
	 * <p>
	 *     The returned type will validate that the IpNetwork matches the IP version constraints defined by the config.
	 * </p>
	 *
	 * @param config The enum constraint config for IP version validation
	 * @return A new type with the applied IP version constraint
	 * @throws NullPointerException If the config is null
	 */
	default @NonNull C ipVersion(@NonNull EnumConstraintConfig<IpVersion> config) {
		Objects.requireNonNull(config, "Config must not be null");
		return this.apply(c -> c.withIpVersion(config));
	}
	
	/**
	 * Applies an IPv4 constraint to the IpNetwork.<br>
	 * <p>
	 *     The returned type will validate that the IpNetwork is an IPv4 network.
	 * </p>
	 *
	 * @return A new type with the applied IPv4 constraint
	 */
	default @NonNull C ipv4() {
		return this.ipVersion(EnumConstraintConfig.<IpVersion>unconstrained().withEqualTo(IpVersion.IPV4));
	}
	
	/**
	 * Applies an IPv6 constraint to the IpNetwork.<br>
	 * <p>
	 *     The returned type will validate that the IpNetwork is an IPv6 network.
	 * </p>
	 *
	 * @return A new type with the applied IPv6 constraint
	 */
	default @NonNull C ipv6() {
		return this.ipVersion(EnumConstraintConfig.<IpVersion>unconstrained().withEqualTo(IpVersion.IPV6));
	}
	
	/**
	 * Applies a prefix length constraint to the IpNetwork using a builder.<br>
	 * <p>
	 *     The returned type will validate that the IpNetwork's prefix length matches the constraints
	 *     defined by the builder configuration.<br>
	 *     Example usage: {@code .prefixLength(b -> b.minLength(8).maxLength(24))}
	 * </p>
	 *
	 * @param builder The builder function to configure prefix length constraints
	 * @return A new type with the applied prefix length constraint
	 * @throws NullPointerException If the builder is null
	 */
	default @NonNull C prefixLength(@NonNull UnaryOperator<LengthConstraintBuilder> builder) {
		Objects.requireNonNull(builder, "Builder must not be null");
		LengthConstraintBuilder lengthBuilder = new LengthConstraintBuilder();
		builder.apply(lengthBuilder);
		return this.apply(config -> config.withPrefixLength(lengthBuilder.build()));
	}
	
	/**
	 * Applies a canonical form constraint to the IpNetwork.<br>
	 * <p>
	 *     The returned type will validate that the IpNetwork is in canonical form (no host bits set).
	 * </p>
	 *
	 * @return A new type with the applied canonical constraint
	 */
	default @NonNull C canonical() {
		return this.apply(IpNetworkConstraintConfig::withCanonical);
	}
	
	/**
	 * Applies string constraints to the IP network CIDR notation string representation using a builder.<br>
	 * <p>
	 *     The returned type will validate that the CIDR notation string representation of the IpNetwork matches
	 *     the constraints defined by the builder configuration.<br>
	 *     Example usage: {@code .string(b -> b.startsWith("192.168.").endsWith("/24"))}
	 * </p>
	 *
	 * @param builder The builder function to configure string constraints
	 * @return A new type with the applied string constraints
	 * @throws NullPointerException If the builder is null
	 */
	default @NonNull C string(@NonNull UnaryOperator<StringConstraintBuilder> builder) {
		Objects.requireNonNull(builder, "Builder must not be null");
		
		StringConstraintBuilder stringBuilder = new StringConstraintBuilder();
		builder.apply(stringBuilder);
		return this.apply(config -> config.withStringConstraint(stringBuilder.build()));
	}
}
