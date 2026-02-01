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

package net.luis.utils.io.codec.constraint.config.temporal.zoned;

import net.luis.utils.io.codec.constraint.config.ConstraintConfig;
import net.luis.utils.io.codec.constraint.config.StringConstraintConfig;
import net.luis.utils.io.codec.constraint.config.validator.ConstraintValidators;
import net.luis.utils.io.codec.constraint.core.Constraint;
import net.luis.utils.io.codec.constraint.util.Unit;
import net.luis.utils.util.Pair;
import net.luis.utils.util.result.Result;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.*;

/**
 * Configuration record for zone id type constraints.<br>
 * <p>
 *     This record stores the constraint values for zone id codecs.<br>
 *     It includes base constraints and zone-specific flags for type validation.
 * </p>
 * <p>
 *     The equalTo field uses {@link Pair} where the first value is the zone id and
 *     the second value indicates negation (false=equalTo, true=notEqualTo).
 * </p>
 * <p>
 *     The in field uses {@link Pair} where the first value is the set of ZoneIds and
 *     the second value indicates negation (false=in, true=notIn).
 * </p>
 *
 * @author Luis-St
 *
 * @param equalTo The zone id equality constraint as a pair of (value, negated) where negated=false means equalTo and negated=true means notEqualTo
 * @param in The zone id set constraint as a pair of (values, negated) where negated=false means in and negated=true means notIn
 * @param normalized If present, requires the zone to be in normalized form
 * @param regionBased If present, requires the zone to be region-based
 * @param offsetBased If present, requires the zone to be offset-based
 * @param fixedOffset If present, requires the zone to be a fixed offset
 * @param utc If present, requires the zone to be UTC
 * @param systemDefault If present, requires the zone to be the system default
 * @param available If present, requires the zone to be in the available zone list
 * @param region A nested config for region string constraints
 * @param custom A custom constraint implementation
 */
public record ZoneIdConstraintConfig(
	@NonNull Optional<Pair<ZoneId, Boolean>> equalTo,
	@NonNull Optional<Pair<Set<ZoneId>, Boolean>> in,
	@NonNull Optional<Unit> normalized,
	@NonNull Optional<Unit> regionBased,
	@NonNull Optional<Unit> offsetBased,
	@NonNull Optional<Unit> fixedOffset,
	@NonNull Optional<Unit> utc,
	@NonNull Optional<Unit> systemDefault,
	@NonNull Optional<Unit> available,
	@NonNull Optional<StringConstraintConfig> region,
	@NonNull Optional<Constraint<ZoneId>> custom
) implements ConstraintConfig<ZoneId> {
	
	/**
	 * An unconstrained zone id configuration with no constraints applied.<br>
	 */
	public static final ZoneIdConstraintConfig UNCONSTRAINED = new ZoneIdConstraintConfig(
		Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
	);
	
	/**
	 * Constructs a new zone id constraint config with the specified parameters.<br>
	 *
	 * @param equalTo The zone id equality constraint as a pair of (value, negated) where negated=false means equalTo and negated=true means notEqualTo
	 * @param in The zone id set constraint as a pair of (values, negated) where negated=false means in and negated=true means notIn
	 * @param normalized If present, requires the zone to be in normalized form
	 * @param regionBased If present, requires the zone to be region-based
	 * @param offsetBased If present, requires the zone to be offset-based
	 * @param fixedOffset If present, requires the zone to be a fixed offset
	 * @param utc If present, requires the zone to be UTC
	 * @param systemDefault If present, requires the zone to be the system default
	 * @param available If present, requires the zone to be in the available zone list
	 * @param region A nested config for region string constraints
	 * @param custom A custom constraint implementation
	 * @throws NullPointerException If any of the optional fields is null
	 * @throws IllegalArgumentException If the 'in' set is empty when present
	 * @throws IllegalArgumentException If regionBased and offsetBased constraints are both present
	 */
	public ZoneIdConstraintConfig {
		Objects.requireNonNull(equalTo, "Optional for 'equal to' constraint must not be null");
		Objects.requireNonNull(in, "Optional for 'in' constraint must not be null");
		Objects.requireNonNull(normalized, "Optional for 'normalized' constraint must not be null");
		Objects.requireNonNull(regionBased, "Optional for 'region based' constraint must not be null");
		Objects.requireNonNull(offsetBased, "Optional for 'offset based' constraint must not be null");
		Objects.requireNonNull(fixedOffset, "Optional for 'fixed offset' constraint must not be null");
		Objects.requireNonNull(utc, "Optional for 'utc' constraint must not be null");
		Objects.requireNonNull(systemDefault, "Optional for 'system default' constraint must not be null");
		Objects.requireNonNull(available, "Optional for 'available' constraint must not be null");
		Objects.requireNonNull(region, "Optional for 'region' constraint must not be null");
		Objects.requireNonNull(custom, "Optional for 'custom' constraint must not be null");
		
		if (in.isPresent() && in.get().getFirst().isEmpty()) {
			throw new IllegalArgumentException("In set must not be empty when present");
		}
		
		if (regionBased.isPresent() && offsetBased.isPresent()) {
			throw new IllegalArgumentException("Region based and offset based constraints are mutually exclusive");
		}
	}
	
	//region With methods
	
	/**
	 * Creates a new config with the specified equal-to constraint.<br>
	 *
	 * @param value The exact zone id that should be matched
	 * @throws NullPointerException If the value is null
	 * @return A new config with the constraint applied
	 */
	public @NonNull ZoneIdConstraintConfig withEqualTo(@NonNull ZoneId value) {
		Objects.requireNonNull(value, "Value for 'equal to' constraint must not be null");
		return new ZoneIdConstraintConfig(Optional.of(Pair.of(value, false)), this.in, this.normalized, this.regionBased, this.offsetBased, this.fixedOffset, this.utc, this.systemDefault, this.available, this.region, this.custom);
	}
	
	/**
	 * Creates a new config with the specified not-equal-to constraint.<br>
	 *
	 * @param value The zone id that should be excluded
	 * @throws NullPointerException If the value is null
	 * @return A new config with the constraint applied
	 */
	public @NonNull ZoneIdConstraintConfig withNotEqualTo(@NonNull ZoneId value) {
		Objects.requireNonNull(value, "Value for 'not equal to' constraint must not be null");
		return new ZoneIdConstraintConfig(Optional.of(Pair.of(value, true)), this.in, this.normalized, this.regionBased, this.offsetBased, this.fixedOffset, this.utc, this.systemDefault, this.available, this.region, this.custom);
	}
	
	/**
	 * Creates a new config with the specified inclusion constraint.<br>
	 *
	 * @param values The collection of ZoneIds that are allowed
	 * @throws NullPointerException If the values is null
	 * @return A new config with the constraint applied
	 */
	public @NonNull ZoneIdConstraintConfig withIn(@NonNull Collection<ZoneId> values) {
		Objects.requireNonNull(values, "Values for 'in' constraint must not be null");
		return new ZoneIdConstraintConfig(this.equalTo, Optional.of(Pair.of(Set.copyOf(values), false)), this.normalized, this.regionBased, this.offsetBased, this.fixedOffset, this.utc, this.systemDefault, this.available, this.region, this.custom);
	}
	
	/**
	 * Creates a new config with the specified exclusion constraint.<br>
	 *
	 * @param values The collection of ZoneIds that are not allowed
	 * @throws NullPointerException If the values is null
	 * @return A new config with the constraint applied
	 */
	public @NonNull ZoneIdConstraintConfig withNotIn(@NonNull Collection<ZoneId> values) {
		Objects.requireNonNull(values, "Values for 'not in' constraint must not be null");
		return new ZoneIdConstraintConfig(this.equalTo, Optional.of(Pair.of(Set.copyOf(values), true)), this.normalized, this.regionBased, this.offsetBased, this.fixedOffset, this.utc, this.systemDefault, this.available, this.region, this.custom);
	}
	
	/**
	 * Creates a new config with the normalized constraint enabled.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull ZoneIdConstraintConfig withNormalized() {
		return new ZoneIdConstraintConfig(this.equalTo, this.in, Optional.of(Unit.INSTANCE), this.regionBased, this.offsetBased, this.fixedOffset, this.utc, this.systemDefault, this.available, this.region, this.custom);
	}
	
	/**
	 * Creates a new config with the region-based constraint enabled.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull ZoneIdConstraintConfig withRegionBased() {
		return new ZoneIdConstraintConfig(this.equalTo, this.in, this.normalized, Optional.of(Unit.INSTANCE), this.offsetBased, this.fixedOffset, this.utc, this.systemDefault, this.available, this.region, this.custom);
	}
	
	/**
	 * Creates a new config with the offset-based constraint enabled.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull ZoneIdConstraintConfig withOffsetBased() {
		return new ZoneIdConstraintConfig(this.equalTo, this.in, this.normalized, this.regionBased, Optional.of(Unit.INSTANCE), this.fixedOffset, this.utc, this.systemDefault, this.available, this.region, this.custom);
	}
	
	/**
	 * Creates a new config with the fixed offset constraint enabled.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull ZoneIdConstraintConfig withFixedOffset() {
		return new ZoneIdConstraintConfig(this.equalTo, this.in, this.normalized, this.regionBased, this.offsetBased, Optional.of(Unit.INSTANCE), this.utc, this.systemDefault, this.available, this.region, this.custom);
	}
	
	/**
	 * Creates a new config with the UTC constraint enabled.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull ZoneIdConstraintConfig withUtc() {
		return new ZoneIdConstraintConfig(this.equalTo, this.in, this.normalized, this.regionBased, this.offsetBased, this.fixedOffset, Optional.of(Unit.INSTANCE), this.systemDefault, this.available, this.region, this.custom);
	}
	
	/**
	 * Creates a new config with the system default constraint enabled.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull ZoneIdConstraintConfig withSystemDefault() {
		return new ZoneIdConstraintConfig(this.equalTo, this.in, this.normalized, this.regionBased, this.offsetBased, this.fixedOffset, this.utc, Optional.of(Unit.INSTANCE), this.available, this.region, this.custom);
	}
	
	/**
	 * Creates a new config with the available constraint enabled.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull ZoneIdConstraintConfig withAvailable() {
		return new ZoneIdConstraintConfig(this.equalTo, this.in, this.normalized, this.regionBased, this.offsetBased, this.fixedOffset, this.utc, this.systemDefault, Optional.of(Unit.INSTANCE), this.region, this.custom);
	}
	
	/**
	 * Creates a new config with the specified region constraint.<br>
	 *
	 * @param regionConfig The string constraint config for region validation
	 * @throws NullPointerException If the regionConfig is null
	 * @return A new config with the constraint applied
	 */
	public @NonNull ZoneIdConstraintConfig withRegion(@NonNull StringConstraintConfig regionConfig) {
		Objects.requireNonNull(regionConfig, "Config for 'region' constraint must not be null");
		return new ZoneIdConstraintConfig(this.equalTo, this.in, this.normalized, this.regionBased, this.offsetBased, this.fixedOffset, this.utc, this.systemDefault, this.available, Optional.of(regionConfig), this.custom);
	}
	
	/**
	 * Creates a new config with the specified custom constraint.<br>
	 *
	 * @param constraint The custom constraint implementation
	 * @throws NullPointerException If the constraint is null
	 * @return A new config with the constraint applied
	 */
	public @NonNull ZoneIdConstraintConfig withCustom(@NonNull Constraint<ZoneId> constraint) {
		Objects.requireNonNull(constraint, "Custom constraint must not be null");
		return new ZoneIdConstraintConfig(this.equalTo, this.in, this.normalized, this.regionBased, this.offsetBased, this.fixedOffset, this.utc, this.systemDefault, this.available, this.region, Optional.of(constraint));
	}
	//endregion
	
	@Override
	public void validate(@NonNull ZoneId value) {
		Objects.requireNonNull(value, "Value must not be null");
		ConstraintValidators.validateAll(
			() -> ConstraintValidators.validateEqualTo(value, this.equalTo),
			() -> ConstraintValidators.validateIn(value, this.in),
			() -> ConstraintValidators.validateFlag(value, this.normalized, v -> v.equals(v.normalized()), "Zone id '" + value + "' must be in normalized form"),
			() -> ConstraintValidators.validateFlag(value, this.regionBased, v -> !(v instanceof ZoneOffset), "Zone id '" + value + "' must be region-based"),
			() -> ConstraintValidators.validateFlag(value, this.offsetBased, ZoneOffset.class::isInstance, "Zone id '" + value + "' must be offset-based"),
			() -> ConstraintValidators.validateFlag(value, this.fixedOffset, ZoneOffset.class::isInstance, "Zone id '" + value + "' must be a fixed offset"),
			() -> ConstraintValidators.validateFlag(value, this.utc, v -> v.normalized().equals(ZoneOffset.UTC), "Zone id '" + value + "' must be UTC"),
			() -> ConstraintValidators.validateFlag(value, this.systemDefault, v -> v.equals(ZoneId.systemDefault()), "Zone id '" + value + "' must be the system default"),
			() -> ConstraintValidators.validateFlag(value, this.available, v -> ZoneId.getAvailableZoneIds().contains(v.getId()), "Zone id '" + value + "' must be in the available zone list"),
			() -> ConstraintValidators.validateNestedConfig(value.getId(), this.region, "Region"),
			() -> ConstraintValidators.validateCustom(value, this.custom)
		);
	}
}
