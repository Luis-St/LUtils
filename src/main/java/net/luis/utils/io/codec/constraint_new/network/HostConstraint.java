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

import net.luis.utils.io.codec.constraint_new.BaseConstraint;
import net.luis.utils.io.codec.constraint_new.builder.DomainConstraintBuilder;
import net.luis.utils.io.codec.constraint_new.builder.IpConstraintBuilder;
import org.jspecify.annotations.NonNull;

import java.util.function.UnaryOperator;

/**
 * Constraint interface for host types that provides host validation operations.<br>
 * <p>
 *     This interface extends {@link BaseConstraint} with methods for constraining hosts
 *     as either IP addresses or domain names. The constraints are mutually exclusive -
 *     a host can be validated as an IP address or a domain, but not both simultaneously.
 * </p>
 * <p>
 *     Use {@link #ip(UnaryOperator)} to constrain the host as an IP address with
 *     IPv4/IPv6 format validation, type classification, and subnet membership checks.
 * </p>
 * <p>
 *     Use {@link #domain(UnaryOperator)} to constrain the host as a domain name with
 *     root domain/subdomain validation and string pattern matching.
 * </p>
 *
 * @author Luis-St
 *
 * @param <T> The type of the constraint configuration
 * @param <C> The return type of the constraint method (for fluent method chaining)
 */
public interface HostConstraint<T, C> extends BaseConstraint<T, C> {
	
	/**
	 * Applies an IP address constraint to the host using a builder.<br>
	 * <p>
	 *     The returned type will validate that hosts are valid IP addresses matching
	 *     the constraints defined by the builder (IPv4/IPv6 format, type, subnet, etc.).<br>
	 *     This constraint is mutually exclusive with {@link #domain(UnaryOperator)}.
	 * </p>
	 *
	 * @param builder A function that configures constraints on the IP address
	 * @return A new type with the applied IP constraint
	 * @throws NullPointerException If the builder is null
	 * @see IpConstraint
	 * @see #domain(UnaryOperator)
	 */
	@NonNull C ip(@NonNull UnaryOperator<IpConstraintBuilder> builder);
	
	/**
	 * Applies a domain name constraint to the host using a builder.<br>
	 * <p>
	 *     The returned type will validate that hosts are valid domain names matching
	 *     the constraints defined by the builder (root domain, subdomain, patterns, etc.).<br>
	 *     This constraint is mutually exclusive with {@link #ip(UnaryOperator)}.
	 * </p>
	 *
	 * @param builder A function that configures constraints on the domain name
	 * @return A new type with the applied domain constraint
	 * @throws NullPointerException If the builder is null
	 * @see DomainConstraint
	 * @see #ip(UnaryOperator)
	 */
	@NonNull C domain(@NonNull UnaryOperator<DomainConstraintBuilder> builder);
}
