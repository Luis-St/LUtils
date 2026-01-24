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

import net.luis.utils.io.codec.constraint.builder.StringConstraintBuilder;
import net.luis.utils.io.codec.constraint.config.io.MacAddressConstraintConfig;
import net.luis.utils.io.codec.constraint.core.*;
import net.luis.utils.io.network.address.mac.MacAddress;
import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.Objects;
import java.util.function.UnaryOperator;

/**
 * Constraint interface for MacAddress types that provides comprehensive MAC address validation operations.<br>
 * <p>
 *     This interface extends {@link ApplicableConstraint} and {@link BaseConstraint} with methods for constraining MacAddresses
 *     based on address type (unicast/multicast), administration type (universal/local), and broadcast status.<br>
 *     It allows fine-grained validation of MAC addresses for network-related codecs.
 * </p>
 *
 * @author Luis-St
 *
 * @param <C> The return type of the constraint method (for fluent method chaining)
 */
@FunctionalInterface
public interface MacAddressConstraint<C> extends ApplicableConstraint<MacAddressConstraintConfig, C>, BaseConstraint<MacAddress, C> {
	
	@Override
	@NonNull C apply(@NonNull UnaryOperator<MacAddressConstraintConfig> configModifier);
	
	@Override
	default @NonNull C equalTo(@NonNull MacAddress value) {
		return this.apply(config -> config.withEqualTo(value));
	}
	
	@Override
	default @NonNull C notEqualTo(@NonNull MacAddress value) {
		return this.apply(config -> config.withNotEqualTo(value));
	}
	
	@Override
	default @NonNull C in(@NonNull Collection<MacAddress> values) {
		return this.apply(config -> config.withIn(values));
	}
	
	@Override
	default @NonNull C notIn(@NonNull Collection<MacAddress> values) {
		return this.apply(config -> config.withNotIn(values));
	}
	
	@Override
	default @NonNull C custom(@NonNull Constraint<MacAddress> constraint) {
		return this.apply(config -> config.withCustom(constraint));
	}
	
	/**
	 * Applies a unicast constraint to the MacAddress.<br>
	 * <p>
	 *     The returned type will validate that the MacAddress is a unicast address (I/G bit = 0).
	 * </p>
	 *
	 * @return A new type with the applied unicast constraint
	 */
	default @NonNull C unicast() {
		return this.apply(MacAddressConstraintConfig::withUnicast);
	}
	
	/**
	 * Applies a multicast constraint to the MacAddress.<br>
	 * <p>
	 *     The returned type will validate that the MacAddress is a multicast address (I/G bit = 1).
	 * </p>
	 *
	 * @return A new type with the applied multicast constraint
	 */
	default @NonNull C multicast() {
		return this.apply(MacAddressConstraintConfig::withMulticast);
	}
	
	/**
	 * Applies a universally administered constraint to the MacAddress.<br>
	 * <p>
	 *     The returned type will validate that the MacAddress is universally administered (U/L bit = 0).
	 * </p>
	 *
	 * @return A new type with the applied universal constraint
	 */
	default @NonNull C universal() {
		return this.apply(MacAddressConstraintConfig::withUniversal);
	}
	
	/**
	 * Applies a locally administered constraint to the MacAddress.<br>
	 * <p>
	 *     The returned type will validate that the MacAddress is locally administered (U/L bit = 1).
	 * </p>
	 *
	 * @return A new type with the applied local constraint
	 */
	default @NonNull C local() {
		return this.apply(MacAddressConstraintConfig::withLocal);
	}
	
	/**
	 * Applies a broadcast constraint to the MacAddress.<br>
	 * <p>
	 *     The returned type will validate that the MacAddress is the broadcast address (FF:FF:FF:FF:FF:FF).
	 * </p>
	 *
	 * @return A new type with the applied broadcast constraint
	 */
	default @NonNull C broadcast() {
		return this.apply(MacAddressConstraintConfig::withBroadcast);
	}
	
	/**
	 * Applies a not broadcast constraint to the MacAddress.<br>
	 * <p>
	 *     The returned type will validate that the MacAddress is not the broadcast address.
	 * </p>
	 *
	 * @return A new type with the applied not broadcast constraint
	 */
	default @NonNull C notBroadcast() {
		return this.apply(MacAddressConstraintConfig::withNotBroadcast);
	}
	
	/**
	 * Applies string constraints to the MAC address colon-separated string representation using a builder.<br>
	 * <p>
	 *     The returned type will validate that the colon-separated string representation of the MacAddress matches
	 *     the constraints defined by the builder configuration.<br>
	 *     Example usage: {@code .string(b -> b.startsWith("00:11:").matches("([0-9A-F]{2}:){5}[0-9A-F]{2}"))}
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
