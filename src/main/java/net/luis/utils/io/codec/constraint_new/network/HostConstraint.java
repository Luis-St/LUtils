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

package net.luis.utils.io.codec.constraint_new.network;

import net.luis.utils.io.codec.constraint_new.CharSequenceConstraint;
import net.luis.utils.io.codec.constraint_new.builder.EnumConstraintBuilder;
import net.luis.utils.io.codec.constraint_new.builder.StringConstraintBuilder;
import net.luis.utils.io.codec.constraint_new.core.IpAddressType;
import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.Collections;
import java.util.function.UnaryOperator;

/**
 * Constraint interface for host types that provides host validation operations.<br>
 * <p>
 *     This interface extends {@link CharSequenceConstraint} with methods for constraining hosts
 *     based on IP address format, IP type, subnet membership, and domain characteristics.<br>
 *     It supports validation of both IP addresses (IPv4, IPv6) and domain names.
 * </p>
 *
 * @author Luis-St
 *
 * @param <T> The type of the constraint configuration
 * @param <C> The return type of the constraint method (for fluent method chaining)
 */
public interface HostConstraint<T, C> extends CharSequenceConstraint<T, C> {
	
	/**
	 * Applies an IPv4 constraint to the host.<br>
	 * <p>
	 *     The returned type will validate that hosts are valid IPv4 addresses.
	 * </p>
	 *
	 * @return A new type with the applied IPv4 constraint
	 * @see #ipv6()
	 * @see #ip()
	 */
	@NonNull C ipv4();
	
	/**
	 * Applies an IPv6 constraint to the host.<br>
	 * <p>
	 *     The returned type will validate that hosts are valid IPv6 addresses.
	 * </p>
	 *
	 * @return A new type with the applied IPv6 constraint
	 * @see #ipv4()
	 * @see #ip()
	 */
	@NonNull C ipv6();
	
	/**
	 * Applies an IP address constraint to the host.<br>
	 * <p>
	 *     The returned type will validate that hosts are valid IP addresses (either IPv4 or IPv6).<br>
	 *     This is a convenience method equivalent to {@code ip(UnaryOperator.identity())}.
	 * </p>
	 *
	 * @return A new type with the applied IP constraint
	 * @see #ip(UnaryOperator)
	 * @see #ipv4()
	 * @see #ipv6()
	 */
	default @NonNull C ip() {
		return this.ip(UnaryOperator.identity());
	}
	
	/**
	 * Applies an IP address constraint to the host using a builder.<br>
	 * <p>
	 *     The returned type will validate that hosts are valid IP addresses matching
	 *     the additional string constraints defined by the builder.
	 * </p>
	 *
	 * @param builder A function that configures additional constraints on the IP address string
	 * @return A new type with the applied IP constraint
	 * @throws NullPointerException If the builder is null
	 * @see #ip()
	 */
	@NonNull C ip(@NonNull UnaryOperator<StringConstraintBuilder> builder);
	
	/**
	 * Applies an IP type constraint to the host using a builder.<br>
	 * <p>
	 *     The returned type will validate that IP addresses belong to the types
	 *     defined by the builder (public, private, loopback, link-local, multicast, broadcast, or unspecified).
	 * </p>
	 *
	 * @param builder A function that configures the IP type constraint using an enum constraint builder
	 * @return A new type with the applied IP type constraint
	 * @throws NullPointerException If the builder is null
	 * @see IpAddressType
	 */
	@NonNull C ipType(@NonNull UnaryOperator<EnumConstraintBuilder<IpAddressType>> builder);
	
	/**
	 * Applies a subnet membership constraint to the host.<br>
	 * <p>
	 *     The returned type will validate that IP addresses are within the specified CIDR subnet.<br>
	 *     This is a convenience method equivalent to {@code inAnySubnet(Collections.singletonList(cidr))}.
	 * </p>
	 *
	 * @param cidr The CIDR notation subnet (e.g., "192.168.1.0/24")
	 * @return A new type with the applied subnet constraint
	 * @throws NullPointerException If the CIDR is null
	 * @see #inAnySubnet(Collection)
	 * @see #notInSubnet(String)
	 */
	default @NonNull C inSubnet(@NonNull String cidr) {
		return this.inAnySubnet(Collections.singletonList(cidr));
	}
	
	/**
	 * Applies a negative subnet membership constraint to the host.<br>
	 * <p>
	 *     The returned type will validate that IP addresses are not within the specified CIDR subnet.<br>
	 *     This is a convenience method equivalent to {@code notInAnySubnet(Collections.singletonList(cidr))}.
	 * </p>
	 *
	 * @param cidr The CIDR notation subnet to exclude (e.g., "10.0.0.0/8")
	 * @return A new type with the applied negative subnet constraint
	 * @throws NullPointerException If the CIDR is null
	 * @see #notInAnySubnet(Collection)
	 * @see #inSubnet(String)
	 */
	default @NonNull C notInSubnet(@NonNull String cidr) {
		return this.notInAnySubnet(Collections.singletonList(cidr));
	}
	
	/**
	 * Applies a multi-subnet membership constraint to the host.<br>
	 * <p>
	 *     The returned type will validate that IP addresses are within any of the specified CIDR subnets.
	 * </p>
	 *
	 * @param cidrs The collection of CIDR notation subnets
	 * @return A new type with the applied multi-subnet constraint
	 * @throws NullPointerException If the collection is null
	 * @see #inSubnet(String)
	 * @see #notInAnySubnet(Collection)
	 */
	@NonNull C inAnySubnet(@NonNull Collection<String> cidrs);
	
	/**
	 * Applies a negative multi-subnet membership constraint to the host.<br>
	 * <p>
	 *     The returned type will validate that IP addresses are not within any of the specified CIDR subnets.
	 * </p>
	 *
	 * @param cidrs The collection of CIDR notation subnets to exclude
	 * @return A new type with the applied negative multi-subnet constraint
	 * @throws NullPointerException If the collection is null
	 * @see #notInSubnet(String)
	 * @see #inAnySubnet(Collection)
	 */
	@NonNull C notInAnySubnet(@NonNull Collection<String> cidrs);
	
	/**
	 * Applies a domain constraint to the host.<br>
	 * <p>
	 *     The returned type will validate that hosts are valid domain names.<br>
	 *     This is a convenience method equivalent to {@code domain(UnaryOperator.identity())}.
	 * </p>
	 *
	 * @return A new type with the applied domain constraint
	 * @see #domain(UnaryOperator)
	 */
	default @NonNull C domain() {
		return this.domain(UnaryOperator.identity());
	}
	
	/**
	 * Applies a domain constraint to the host using a builder.<br>
	 * <p>
	 *     The returned type will validate that hosts are valid domain names matching
	 *     the additional string constraints defined by the builder.
	 * </p>
	 *
	 * @param builder A function that configures additional constraints on the domain string
	 * @return A new type with the applied domain constraint
	 * @throws NullPointerException If the builder is null
	 * @see #domain()
	 */
	@NonNull C domain(@NonNull UnaryOperator<StringConstraintBuilder> builder);
	
	/**
	 * Applies a root domain constraint to the host.<br>
	 * <p>
	 *     The returned type will validate that hosts are root domains (e.g., "example.com" rather than "sub.example.com").
	 * </p>
	 *
	 * @return A new type with the applied root domain constraint
	 * @see #subDomain()
	 */
	@NonNull C rootDomain();
	
	/**
	 * Applies a subdomain constraint to the host.<br>
	 * <p>
	 *     The returned type will validate that hosts are subdomains (e.g., "sub.example.com" rather than "example.com").
	 * </p>
	 *
	 * @return A new type with the applied subdomain constraint
	 * @see #rootDomain()
	 */
	@NonNull C subDomain();
}
