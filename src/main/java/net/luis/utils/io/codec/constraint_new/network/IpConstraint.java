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
import net.luis.utils.io.codec.constraint_new.core.IpAddressType;
import net.luis.utils.io.codec.constraint_new.core.IpVersion;
import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.Collections;
import java.util.function.UnaryOperator;

/**
 * Constraint interface for IP address validation operations.<br>
 * <p>
 *     This interface extends {@link CharSequenceConstraint} with methods for constraining IP addresses
 *     based on format (IPv4, IPv6), type (public, private, loopback, etc.), and subnet membership.
 * </p>
 *
 * @author Luis-St
 *
 * @param <T> The type of the constraint configuration
 * @param <C> The return type of the constraint method (for fluent method chaining)
 */
public interface IpConstraint<T, C> extends CharSequenceConstraint<T, C> {
	
	/**
	 * Applies an IPv4 constraint to the IP address.<br>
	 * <p>
	 *     The returned type will validate that values are valid IPv4 addresses.
	 * </p>
	 *
	 * @return A new type with the applied IPv4 constraint
	 * @see #ipv6()
	 * @see #ipVersion(UnaryOperator)
	 */
	default @NonNull C ipv4() {
		return this.ipVersion(builder -> builder.equalTo(IpVersion.IPV4));
	}
	
	/**
	 * Applies an IPv6 constraint to the IP address.<br>
	 * <p>
	 *     The returned type will validate that values are valid IPv6 addresses.
	 * </p>
	 *
	 * @return A new type with the applied IPv6 constraint
	 * @see #ipv4()
	 * @see #ipVersion(UnaryOperator)
	 */
	default @NonNull C ipv6() {
		return this.ipVersion(builder -> builder.equalTo(IpVersion.IPV6));
	}
	
	/**
	 * Applies an IP version constraint using a builder.<br>
	 * <p>
	 *     The returned type will validate that IP addresses belong to the versions defined by the builder (IPv4, IPv6, or both).
	 * </p>
	 *
	 * @param builder A function that configures the IP version constraint using an enum constraint builder
	 * @return A new type with the applied IP version constraint
	 * @throws NullPointerException If the builder is null
	 * @see IpVersion
	 * @see #ipv4()
	 * @see #ipv6()
	 */
	@NonNull C ipVersion(@NonNull UnaryOperator<EnumConstraintBuilder<IpVersion>> builder);
	
	/**
	 * Applies a public IP type constraint.<br>
	 * <p>
	 *     The returned type will validate that values are public IP addresses.<br>
	 *     Public IP addresses are routable on the internet and not reserved for private networks.
	 * </p>
	 *
	 * @return A new type with the applied public IP type constraint
	 * @see #privateIp()
	 * @see #loopbackIp()
	 * @see #linkLocalIp()
	 * @see #ipType(UnaryOperator)
	 */
	default @NonNull C publicIp() {
		return this.ipType(builder -> builder.equalTo(IpAddressType.PUBLIC));
	}
	
	/**
	 * Applies a private IP type constraint.<br>
	 * <p>
	 *     The returned type will validate that values are private IP addresses.<br>
	 *     Private IP addresses are reserved for use within private networks and are not routable on the internet.
	 * </p>
	 *
	 * @return A new type with the applied private IP type constraint
	 * @see #publicIp()
	 * @see #loopbackIp()
	 * @see #linkLocalIp()
	 * @see #ipType(UnaryOperator)
	 */
	default @NonNull C privateIp() {
		return this.ipType(builder -> builder.equalTo(IpAddressType.PRIVATE));
	}
	
	/**
	 * Applies a loopback IP type constraint.<br>
	 * <p>
	 *     The returned type will validate that values are loopback IP addresses.<br>
	 *     Loopback IP addresses are used for internal testing and communication within the host machine.
	 * </p>
	 *
	 * @return A new type with the applied loopback IP type constraint
	 * @see #publicIp()
	 * @see #privateIp()
	 * @see #linkLocalIp()
	 * @see #ipType(UnaryOperator)
	 */
	default @NonNull C loopbackIp() {
		return this.ipType(builder -> builder.equalTo(IpAddressType.LOOPBACK));
	}
	
	/**
	 * Applies a link-local IP type constraint.<br>
	 * <p>
	 *     The returned type will validate that values are link-local IP addresses.<br>
	 *     Link-local IP addresses are used for communication within a local network segment.
	 * </p>
	 *
	 * @return A new type with the applied link-local IP type constraint
	 * @see #publicIp()
	 * @see #privateIp()
	 * @see #loopbackIp()
	 * @see #ipType(UnaryOperator)
	 */
	default @NonNull C linkLocalIp() {
		return this.ipType(builder -> builder.equalTo(IpAddressType.LINK_LOCAL));
	}
	
	/**
	 * Applies an IP type constraint using a builder.<br>
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
	 * Applies a subnet membership constraint.<br>
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
	 * Applies a negative subnet membership constraint.<br>
	 * <p>
	 *     The returned type will validate that IP addresses are not within the specified CIDR subnet.<br>
	 *     This is a convenience method equivalent to {@code notInAnySubnet(Collections.singletonList(cidr))}.
	 * </p>
	 *
	 * @param cidr The CIDR notation subnet to exclude (e.g., "10.0.0.0/8")
	 * @return A new type with the applied negative subnet constraint
	 * @throws NullPointerException If the CIDR is null
	 * @see #inNoSubnet(Collection)
	 * @see #inSubnet(String)
	 */
	default @NonNull C notInSubnet(@NonNull String cidr) {
		return this.inNoSubnet(Collections.singletonList(cidr));
	}
	
	/**
	 * Applies a multi-subnet membership constraint.<br>
	 * <p>
	 *     The returned type will validate that IP addresses are within any of the specified CIDR subnets.
	 * </p>
	 *
	 * @param cidrs The collection of CIDR notation subnets
	 * @return A new type with the applied multi-subnet constraint
	 * @throws NullPointerException If the collection is null
	 * @see #inSubnet(String)
	 * @see #inNoSubnet(Collection)
	 */
	@NonNull C inAnySubnet(@NonNull Collection<String> cidrs);
	
	/**
	 * Applies a negative multi-subnet membership constraint.<br>
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
	@NonNull C inNoSubnet(@NonNull Collection<String> cidrs);
}
