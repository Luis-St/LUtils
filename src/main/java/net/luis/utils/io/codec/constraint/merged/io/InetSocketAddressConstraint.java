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

import net.luis.utils.io.codec.constraint.config.io.*;
import net.luis.utils.io.codec.constraint.core.*;
import org.jspecify.annotations.NonNull;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Objects;
import java.util.function.UnaryOperator;

/**
 * Constraint interface for InetSocketAddress types that provides comprehensive socket address validation operations.<br>
 * <p>
 *     This interface extends {@link ApplicableConstraint} and {@link BaseConstraint} with methods for constraining InetSocketAddresses
 *     based on their address component, port component, and resolution state.<br>
 *     It allows fine-grained validation of socket addresses for network-related codecs.
 * </p>
 *
 * @author Luis-St
 *
 * @param <C> The return type of the constraint method (for fluent method chaining)
 */
@FunctionalInterface
public interface InetSocketAddressConstraint<C> extends ApplicableConstraint<InetSocketAddressConstraintConfig, C>, BaseConstraint<InetSocketAddress, C> {
	
	@Override
	@NonNull C apply(@NonNull UnaryOperator<InetSocketAddressConstraintConfig> configModifier);
	
	@Override
	default @NonNull C equalTo(@NonNull InetSocketAddress value) {
		return this.apply(config -> config.withEqualTo(value));
	}
	
	@Override
	default @NonNull C notEqualTo(@NonNull InetSocketAddress value) {
		return this.apply(config -> config.withNotEqualTo(value));
	}
	
	@Override
	default @NonNull C in(@NonNull Collection<InetSocketAddress> values) {
		return this.apply(config -> config.withIn(values));
	}
	
	@Override
	default @NonNull C notIn(@NonNull Collection<InetSocketAddress> values) {
		return this.apply(config -> config.withNotIn(values));
	}
	
	@Override
	default @NonNull C custom(@NonNull Constraint<InetSocketAddress> constraint) {
		return this.apply(config -> config.withCustom(constraint));
	}
	
	/**
	 * Applies an address constraint to the InetSocketAddress.<br>
	 * <p>
	 *     The returned type will validate that the InetSocketAddress's address component matches
	 *     the constraints defined by the config.
	 * </p>
	 *
	 * @param config The InetAddressConstraintConfig for address validation
	 * @return A new type with the applied address constraint
	 * @throws NullPointerException If the config is null
	 */
	default @NonNull C address(@NonNull InetAddressConstraintConfig config) {
		Objects.requireNonNull(config, "Config must not be null");
		return this.apply(c -> c.withAddress(config));
	}
	
	/**
	 * Applies a port constraint to the InetSocketAddress.<br>
	 * <p>
	 *     The returned type will validate that the InetSocketAddress's port component matches
	 *     the constraints defined by the config.
	 * </p>
	 *
	 * @param config The PortConstraintConfig for port validation
	 * @return A new type with the applied port constraint
	 * @throws NullPointerException If the config is null
	 */
	default @NonNull C port(@NonNull PortConstraintConfig config) {
		Objects.requireNonNull(config, "Config must not be null");
		return this.apply(c -> c.withPort(config));
	}
	
	/**
	 * Applies a resolved constraint to the InetSocketAddress.<br>
	 * <p>
	 *     The returned type will validate that the InetSocketAddress is resolved
	 *     (i.e., has a resolved InetAddress).
	 * </p>
	 *
	 * @return A new type with the applied resolved constraint
	 */
	default @NonNull C resolved() {
		return this.apply(InetSocketAddressConstraintConfig::withResolved);
	}
	
	/**
	 * Applies an unresolved constraint to the InetSocketAddress.<br>
	 * <p>
	 *     The returned type will validate that the InetSocketAddress is unresolved
	 *     (i.e., does not have a resolved InetAddress).
	 * </p>
	 *
	 * @return A new type with the applied unresolved constraint
	 */
	default @NonNull C unresolved() {
		return this.apply(InetSocketAddressConstraintConfig::withUnresolved);
	}
}
