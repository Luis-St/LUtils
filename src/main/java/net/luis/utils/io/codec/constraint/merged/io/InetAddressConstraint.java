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

import net.luis.utils.io.codec.constraint.config.EnumConstraintConfig;
import net.luis.utils.io.codec.constraint.config.io.InetAddressConstraintConfig;
import net.luis.utils.io.codec.constraint.core.*;
import net.luis.utils.io.codec.constraint.util.IpAddressType;
import net.luis.utils.io.codec.constraint.util.IpVersion;
import org.jspecify.annotations.NonNull;

import java.net.InetAddress;
import java.util.*;
import java.util.function.UnaryOperator;

/**
 * Constraint interface for InetAddress types that provides comprehensive IP address validation operations.<br>
 * <p>
 *     This interface extends {@link ApplicableConstraint} and {@link BaseConstraint} with methods for constraining InetAddresses
 *     based on IP version, IP address type, and subnet membership.<br>
 *     It allows fine-grained validation of IP addresses for network-related codecs.
 * </p>
 *
 * @author Luis-St
 *
 * @param <C> The return type of the constraint method (for fluent method chaining)
 */
@FunctionalInterface
public interface InetAddressConstraint<C> extends ApplicableConstraint<InetAddressConstraintConfig, C>, BaseConstraint<InetAddress, C> {
	
	@Override
	@NonNull C apply(@NonNull UnaryOperator<InetAddressConstraintConfig> configModifier);
	
	@Override
	default @NonNull C equalTo(@NonNull InetAddress value) {
		return this.apply(config -> config.withEqualTo(value));
	}
	
	@Override
	default @NonNull C notEqualTo(@NonNull InetAddress value) {
		return this.apply(config -> config.withNotEqualTo(value));
	}
	
	@Override
	default @NonNull C in(@NonNull Collection<InetAddress> values) {
		return this.apply(config -> config.withIn(values));
	}
	
	@Override
	default @NonNull C notIn(@NonNull Collection<InetAddress> values) {
		return this.apply(config -> config.withNotIn(values));
	}
	
	@Override
	default @NonNull C custom(@NonNull Constraint<InetAddress> constraint) {
		return this.apply(config -> config.withCustom(constraint));
	}
	
	/**
	 * Applies an IP version constraint to the InetAddress.<br>
	 * <p>
	 *     The returned type will validate that the InetAddress matches the IP version constraints defined by the config.
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
	 * Applies an IPv4 constraint to the InetAddress.<br>
	 * <p>
	 *     The returned type will validate that the InetAddress is an IPv4 address.
	 * </p>
	 *
	 * @return A new type with the applied IPv4 constraint
	 */
	default @NonNull C ipv4() {
		return this.ipVersion(EnumConstraintConfig.<IpVersion>unconstrained().withEqualTo(IpVersion.IPV4));
	}
	
	/**
	 * Applies an IPv6 constraint to the InetAddress.<br>
	 * <p>
	 *     The returned type will validate that the InetAddress is an IPv6 address.
	 * </p>
	 *
	 * @return A new type with the applied IPv6 constraint
	 */
	default @NonNull C ipv6() {
		return this.ipVersion(EnumConstraintConfig.<IpVersion>unconstrained().withEqualTo(IpVersion.IPV6));
	}
	
	/**
	 * Applies an IP address type constraint to the InetAddress.<br>
	 * <p>
	 *     The returned type will validate that the InetAddress matches the IP address type constraints defined by the config.
	 * </p>
	 *
	 * @param config The enum constraint config for IP address type validation
	 * @return A new type with the applied IP address type constraint
	 * @throws NullPointerException If the config is null
	 */
	default @NonNull C ipType(@NonNull EnumConstraintConfig<IpAddressType> config) {
		Objects.requireNonNull(config, "Config must not be null");
		return this.apply(c -> c.withIpType(config));
	}
	
	/**
	 * Applies a public address constraint to the InetAddress.<br>
	 * <p>
	 *     The returned type will validate that the InetAddress is a public (globally routable) address.
	 * </p>
	 *
	 * @return A new type with the applied public address constraint
	 */
	default @NonNull C publicAddress() {
		return this.ipType(EnumConstraintConfig.<IpAddressType>unconstrained().withEqualTo(IpAddressType.PUBLIC));
	}
	
	/**
	 * Applies a private address constraint to the InetAddress.<br>
	 * <p>
	 *     The returned type will validate that the InetAddress is a private (non-routable) address.
	 * </p>
	 *
	 * @return A new type with the applied private address constraint
	 */
	default @NonNull C privateAddress() {
		return this.ipType(EnumConstraintConfig.<IpAddressType>unconstrained().withEqualTo(IpAddressType.PRIVATE));
	}
	
	/**
	 * Applies a loopback address constraint to the InetAddress.<br>
	 * <p>
	 *     The returned type will validate that the InetAddress is a loopback address (127.0.0.1 or ::1).
	 * </p>
	 *
	 * @return A new type with the applied loopback constraint
	 */
	default @NonNull C loopback() {
		return this.ipType(EnumConstraintConfig.<IpAddressType>unconstrained().withEqualTo(IpAddressType.LOOPBACK));
	}
	
	/**
	 * Applies a link-local address constraint to the InetAddress.<br>
	 * <p>
	 *     The returned type will validate that the InetAddress is a link-local address.
	 * </p>
	 *
	 * @return A new type with the applied link-local constraint
	 */
	default @NonNull C linkLocal() {
		return this.ipType(EnumConstraintConfig.<IpAddressType>unconstrained().withEqualTo(IpAddressType.LINK_LOCAL));
	}
	
	/**
	 * Applies a multicast address constraint to the InetAddress.<br>
	 * <p>
	 *     The returned type will validate that the InetAddress is a multicast address.
	 * </p>
	 *
	 * @return A new type with the applied multicast constraint
	 */
	default @NonNull C multicast() {
		return this.ipType(EnumConstraintConfig.<IpAddressType>unconstrained().withEqualTo(IpAddressType.MULTICAST));
	}
	
	/**
	 * Applies a subnet membership constraint to the InetAddress.<br>
	 * <p>
	 *     The returned type will validate that the InetAddress is a member of at least one of the specified subnets.
	 * </p>
	 *
	 * @param cidrs The CIDR notation strings for allowed subnets
	 * @return A new type with the applied subnet membership constraint
	 * @throws NullPointerException If the cidrs array is null
	 */
	default @NonNull C inAnySubnet(String @NonNull ... cidrs) {
		Objects.requireNonNull(cidrs, "CIDRs must not be null");
		return this.inAnySubnet(List.of(cidrs));
	}
	
	/**
	 * Applies a subnet membership constraint to the InetAddress.<br>
	 * <p>
	 *     The returned type will validate that the InetAddress is a member of at least one of the specified subnets.
	 * </p>
	 *
	 * @param cidrs The collection of CIDR notation strings for allowed subnets
	 * @return A new type with the applied subnet membership constraint
	 * @throws NullPointerException If the cidrs collection is null
	 */
	default @NonNull C inAnySubnet(@NonNull Collection<String> cidrs) {
		Objects.requireNonNull(cidrs, "CIDRs must not be null");
		return this.apply(config -> config.withInAnySubnet(cidrs));
	}
	
	/**
	 * Applies an excluded subnet constraint to the InetAddress.<br>
	 * <p>
	 *     The returned type will validate that the InetAddress is not a member of any of the specified subnets.
	 * </p>
	 *
	 * @param cidrs The CIDR notation strings for excluded subnets
	 * @return A new type with the applied excluded subnet constraint
	 * @throws NullPointerException If the cidrs array is null
	 */
	default @NonNull C notInAnySubnet(String @NonNull ... cidrs) {
		Objects.requireNonNull(cidrs, "CIDRs must not be null");
		return this.notInAnySubnet(List.of(cidrs));
	}
	
	/**
	 * Applies an excluded subnet constraint to the InetAddress.<br>
	 * <p>
	 *     The returned type will validate that the InetAddress is not a member of any of the specified subnets.
	 * </p>
	 *
	 * @param cidrs The collection of CIDR notation strings for excluded subnets
	 * @return A new type with the applied excluded subnet constraint
	 * @throws NullPointerException If the cidrs collection is null
	 */
	default @NonNull C notInAnySubnet(@NonNull Collection<String> cidrs) {
		Objects.requireNonNull(cidrs, "CIDRs must not be null");
		return this.apply(config -> config.withNotInAnySubnet(cidrs));
	}
}
