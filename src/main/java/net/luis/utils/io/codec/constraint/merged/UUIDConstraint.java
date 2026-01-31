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

package net.luis.utils.io.codec.constraint.merged;

import net.luis.utils.io.codec.constraint.builder.EnumConstraintBuilder;
import net.luis.utils.io.codec.constraint.builder.NumericConstraintBuilder;
import net.luis.utils.io.codec.constraint.config.UUIDConstraintConfig;
import net.luis.utils.io.codec.constraint.core.*;
import net.luis.utils.io.codec.constraint.util.UUIDVariant;
import org.jspecify.annotations.NonNull;

import java.util.*;
import java.util.function.UnaryOperator;

/**
 * Constraint interface for UUID types that provides UUID-specific validation operations.<br>
 * <p>
 *     This interface extends {@link BaseConstraint} with methods for constraining UUIDs based on
 *     version, variant, and special UUID values like nil and max.<br>
 *     It supports all standard UUID versions and variants as defined in RFC 4122.
 * </p>
 *
 * @author Luis-St
 *
 * @param <C> The return type of the constraint method (for fluent method chaining)
 */
@FunctionalInterface
public interface UUIDConstraint<C> extends ApplicableConstraint<UUIDConstraintConfig, C>, BaseConstraint<UUID, C> {
	
	@Override
	@NonNull C apply(@NonNull UnaryOperator<UUIDConstraintConfig> configModifier);
	
	@Override
	default @NonNull C equalTo(@NonNull UUID value) {
		return this.apply(config -> config.withEqualTo(value));
	}
	
	@Override
	default @NonNull C notEqualTo(@NonNull UUID value) {
		return this.apply(config -> config.withNotEqualTo(value));
	}
	
	@Override
	default @NonNull C in(@NonNull Collection<UUID> values) {
		return this.apply(config -> config.withIn(values));
	}
	
	@Override
	default @NonNull C notIn(@NonNull Collection<UUID> values) {
		return this.apply(config -> config.withNotIn(values));
	}
	
	@Override
	default @NonNull C custom(@NonNull Constraint<UUID> constraint) {
		return this.apply(config -> config.withCustom(constraint));
	}
	
	/**
	 * Applies a version 1 (time-based) constraint to the UUID.<br>
	 * <p>
	 *     The returned type will validate that UUIDs are version 1 (time-based).<br>
	 *     This is a convenience method equivalent to {@code version(1)}.
	 * </p>
	 *
	 * @return A new type with the applied version 1 constraint
	 * @see #version(int)
	 */
	default @NonNull C version1() {
		return this.version(1);
	}
	
	/**
	 * Applies a version 4 (random) constraint to the UUID.<br>
	 * <p>
	 *     The returned type will validate that UUIDs are version 4 (randomly generated).<br>
	 *     This is a convenience method equivalent to {@code version(4)}.
	 * </p>
	 *
	 * @return A new type with the applied version 4 constraint
	 * @see #version(int)
	 */
	default @NonNull C version4() {
		return this.version(4);
	}
	
	/**
	 * Applies a version 5 (name-based SHA-1) constraint to the UUID.<br>
	 * <p>
	 *     The returned type will validate that UUIDs are version 5 (name-based using SHA-1).<br>
	 *     This is a convenience method equivalent to {@code version(5)}.
	 * </p>
	 *
	 * @return A new type with the applied version 5 constraint
	 * @see #version(int)
	 */
	default @NonNull C version5() {
		return this.version(5);
	}
	
	/**
	 * Applies a version 7 (Unix epoch time-based) constraint to the UUID.<br>
	 * <p>
	 *     The returned type will validate that UUIDs are version 7 (Unix epoch time-based).<br>
	 *     This is a convenience method equivalent to {@code version(7)}.
	 * </p>
	 *
	 * @return A new type with the applied version 7 constraint
	 * @see #version(int)
	 */
	default @NonNull C version7() {
		return this.version(7);
	}
	
	/**
	 * Applies a specific version constraint to the UUID.<br>
	 * <p>
	 *     The returned type will validate that UUIDs have the specified version number.<br>
	 *     This is a convenience method equivalent to {@code version(builder -> builder.equalTo(version))}.
	 * </p>
	 *
	 * @param version The UUID version number to constrain to
	 * @return A new type with the applied version constraint
	 * @see #version(UnaryOperator)
	 */
	default @NonNull C version(int version) {
		return this.version(builder -> builder.equalTo(version));
	}
	
	/**
	 * Applies a version constraint to the UUID using a builder.<br>
	 * <p>
	 *     The returned type will validate that UUIDs have a version matching the constraints defined by the builder.<br>
	 *     This method allows for complex version constraints such as ranges or multiple allowed versions.
	 * </p>
	 *
	 * @param builder A function that configures the version constraint using a numeric constraint builder
	 * @return A new type with the applied version constraint
	 * @throws NullPointerException If the builder is null
	 * @see #version(int)
	 */
	default @NonNull C version(@NonNull UnaryOperator<NumericConstraintBuilder> builder) {
		Objects.requireNonNull(builder, "Builder must not be null");
		
		NumericConstraintBuilder numericBuilder = new NumericConstraintBuilder();
		builder.apply(numericBuilder);
		return this.apply(config -> config.withVersion(numericBuilder.build()));
	}
	
	/**
	 * Applies a variant constraint to the UUID using a builder.<br>
	 * <p>
	 *     The returned type will validate that UUIDs have a variant matching the constraints defined by the builder.<br>
	 *     UUID variants include NFC, RFC 4122, Microsoft, and reserved variants.
	 * </p>
	 *
	 * @param builder A function that configures the variant constraint using an enum constraint builder
	 * @return A new type with the applied variant constraint
	 * @throws NullPointerException If the builder is null
	 * @see UUIDVariant
	 */
	default @NonNull C variant(@NonNull UnaryOperator<EnumConstraintBuilder<UUIDVariant>> builder) {
		Objects.requireNonNull(builder, "Builder must not be null");
		
		EnumConstraintBuilder<UUIDVariant> enumBuilder = new EnumConstraintBuilder<>();
		builder.apply(enumBuilder);
		return this.apply(config -> config.withVariant(enumBuilder.build()));
	}
	
	/**
	 * Applies a nil UUID constraint to the type.<br>
	 * <p>
	 *     The returned type will validate that UUIDs are the nil UUID (all zeros: 00000000-0000-0000-0000-000000000000).
	 * </p>
	 *
	 * @return A new type with the applied nil constraint
	 * @see #notNil()
	 */
	default @NonNull C nil() {
		return this.apply(UUIDConstraintConfig::withNil);
	}
	
	/**
	 * Applies a non-nil UUID constraint to the type.<br>
	 * <p>
	 *     The returned type will validate that UUIDs are not the nil UUID.
	 * </p>
	 *
	 * @return A new type with the applied non-nil constraint
	 * @see #nil()
	 */
	default @NonNull C notNil() {
		return this.apply(UUIDConstraintConfig::withNotNil);
	}
	
	/**
	 * Applies a max UUID constraint to the type.<br>
	 * <p>
	 *     The returned type will validate that UUIDs are the max UUID (all ones: ffffffff-ffff-ffff-ffff-ffffffffffff).
	 * </p>
	 *
	 * @return A new type with the applied max constraint
	 */
	default @NonNull C max() {
		return this.apply(UUIDConstraintConfig::withMax);
	}
}
