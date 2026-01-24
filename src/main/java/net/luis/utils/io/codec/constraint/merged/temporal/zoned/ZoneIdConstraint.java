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

package net.luis.utils.io.codec.constraint.merged.temporal.zoned;

import net.luis.utils.io.codec.constraint.core.*;
import net.luis.utils.io.codec.constraint.builder.StringConstraintBuilder;
import net.luis.utils.io.codec.constraint.config.temporal.zoned.ZoneIdConstraintConfig;
import org.jspecify.annotations.NonNull;

import java.time.ZoneId;
import java.util.Collection;
import java.util.Objects;
import java.util.function.UnaryOperator;

/**
 * Constraint interface for {@link ZoneId} that provides time zone validation operations.<br>
 * <p>
 *     This interface extends {@link BaseConstraint} with methods for constraining time zones
 *     based on their type (region-based, offset-based), normalization, and availability.<br>
 *     It allows validation of zone identifiers according to various criteria.
 * </p>
 *
 * @author Luis-St
 *
 * @param <C> The return type of the constraint method (for fluent method chaining)
 */
@FunctionalInterface
public interface ZoneIdConstraint<C> extends ApplicableConstraint<ZoneIdConstraintConfig, C>, BaseConstraint<ZoneId, C> {
	
	@Override
	@NonNull C apply(@NonNull UnaryOperator<ZoneIdConstraintConfig> configModifier);
	
	@Override
	default @NonNull C equalTo(@NonNull ZoneId value) {
		return this.apply(config -> config.withEqualTo(value));
	}
	
	@Override
	default @NonNull C notEqualTo(@NonNull ZoneId value) {
		return this.apply(config -> config.withNotEqualTo(value));
	}
	
	@Override
	default @NonNull C in(@NonNull Collection<ZoneId> values) {
		return this.apply(config -> config.withIn(values));
	}
	
	@Override
	default @NonNull C notIn(@NonNull Collection<ZoneId> values) {
		return this.apply(config -> config.withNotIn(values));
	}
	
	@Override
	default @NonNull C custom(@NonNull Constraint<ZoneId> constraint) {
		return this.apply(config -> config.withCustom(constraint));
	}
	
	/**
	 * Applies a normalized zone constraint to the type.<br>
	 * <p>
	 *     The returned type will validate that zone IDs are in their normalized form.
	 * </p>
	 *
	 * @return A new type with the applied normalized constraint
	 */
	default @NonNull C normalized() {
		return this.apply(ZoneIdConstraintConfig::withNormalized);
	}
	
	/**
	 * Applies a region-based zone constraint to the type.<br>
	 * <p>
	 *     The returned type will validate that zone IDs are region-based (e.g., "America/New_York", "Europe/London")
	 *     rather than offset-based.
	 * </p>
	 *
	 * @return A new type with the applied region-based constraint
	 * @see #offsetBased()
	 */
	default @NonNull C regionBased() {
		return this.apply(ZoneIdConstraintConfig::withRegionBased);
	}
	
	/**
	 * Applies an offset-based zone constraint to the type.<br>
	 * <p>
	 *     The returned type will validate that zone IDs are offset-based (e.g., "+02:00", "-05:00")
	 *     rather than region-based.
	 * </p>
	 *
	 * @return A new type with the applied offset-based constraint
	 * @see #regionBased()
	 * @see #fixedOffset()
	 */
	default @NonNull C offsetBased() {
		return this.apply(ZoneIdConstraintConfig::withOffsetBased);
	}
	
	/**
	 * Applies a fixed offset zone constraint to the type.<br>
	 * <p>
	 *     The returned type will validate that zone IDs represent fixed offsets that do not observe daylight saving time.
	 * </p>
	 *
	 * @return A new type with the applied fixed offset constraint
	 * @see #offsetBased()
	 */
	default @NonNull C fixedOffset() {
		return this.apply(ZoneIdConstraintConfig::withFixedOffset);
	}
	
	/**
	 * Applies a UTC zone constraint to the type.<br>
	 * <p>
	 *     The returned type will validate that zone IDs represent UTC (Coordinated Universal Time).
	 * </p>
	 *
	 * @return A new type with the applied UTC constraint
	 */
	default @NonNull C utc() {
		return this.apply(ZoneIdConstraintConfig::withUtc);
	}
	
	/**
	 * Applies a system default zone constraint to the type.<br>
	 * <p>
	 *     The returned type will validate that zone IDs match the system's default time zone.
	 * </p>
	 *
	 * @return A new type with the applied system default constraint
	 */
	default @NonNull C systemDefault() {
		return this.apply(ZoneIdConstraintConfig::withSystemDefault);
	}
	
	/**
	 * Applies an available zone constraint to the type.<br>
	 * <p>
	 *     The returned type will validate that zone IDs are among the available zone IDs known to the system.
	 * </p>
	 *
	 * @return A new type with the applied available constraint
	 */
	default @NonNull C available() {
		return this.apply(ZoneIdConstraintConfig::withAvailable);
	}
	
	/**
	 * Applies a region constraint to the zone using a builder.<br>
	 * <p>
	 *     The returned type will validate that the region part of region-based zone IDs matches
	 *     the constraints defined by the builder.
	 * </p>
	 *
	 * @param builder A function that configures the region constraint using a string constraint builder
	 * @return A new type with the applied region constraint
	 * @throws NullPointerException If the builder is null
	 */
	default @NonNull C region(@NonNull UnaryOperator<StringConstraintBuilder> builder) {
		Objects.requireNonNull(builder, "Builder must not be null");
		
		StringConstraintBuilder stringBuilder = new StringConstraintBuilder();
		builder.apply(stringBuilder);
		return this.apply(config -> config.withRegion(stringBuilder.build()));
	}
}
