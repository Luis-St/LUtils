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

package net.luis.utils.io.codec.constraint_new.config;

import net.luis.utils.io.codec.constraint_new.Constraint;
import net.luis.utils.io.codec.constraint_new.UUIDConstraint;
import net.luis.utils.io.codec.constraint_new.core.UUIDVariant;
import net.luis.utils.io.codec.constraint_new.core.Unit;
import net.luis.utils.util.Pair;
import net.luis.utils.util.result.Result;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.*;

import static net.luis.utils.io.codec.constraint_new.config.ConstraintMatchers.*;

/**
 * Configuration record for uuid constraints.<br>
 * <p>
 *     This record stores the constraint values for {@link UUIDConstraint}.<br>
 *     It includes base constraints, version constraints, variant constraints, and special uuid validations.
 * </p>
 *
 * @author Luis-St
 *
 * @param equalTo The uuid equality constraint as a pair of (value, negated) where negated=false means equalTo and negated=true means notEqualTo
 * @param in The uuid set constraint as a pair of (values, negated) where negated=false means in and negated=true means notIn
 * @param version The uuid version number to constrain to
 * @param variant The uuid variant to constrain to
 * @param nil If present, requires the uuid to be the nil uuid
 * @param notNil If present, requires the uuid to not be the nil uuid
 * @param max If present, requires the uuid to be the max uuid
 * @param custom A custom constraint implementation
 */
public record UUIDConstraintConfig(
	@NonNull Optional<Pair<UUID, Boolean>> equalTo,
	@NonNull Optional<Pair<Set<UUID>, Boolean>> in,
	@NonNull Optional<Integer> version,
	@NonNull Optional<UUIDVariant> variant,
	@NonNull Optional<Unit> nil,
	@NonNull Optional<Unit> notNil,
	@NonNull Optional<Unit> max,
	@NonNull Optional<Constraint<UUID>> custom
) implements ConstraintConfig<UUID> {
	
	private static final UUID NIL_UUID = new UUID(0L, 0L);
	private static final UUID MAX_UUID = new UUID(-1L, -1L);
	
	/**
	 * An unconstrained uuid configuration with no constraints applied.<br>
	 */
	public static final UUIDConstraintConfig UNCONSTRAINED = new UUIDConstraintConfig(
		Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
	);
	
	/**
	 * Constructs a new uuid constraint config with the specified parameters.<br>
	 *
	 * @param equalTo The uuid equality constraint as a pair of (value, negated) where negated=false means equalTo and negated=true means notEqualTo
	 * @param in The uuid set constraint as a pair of (values, negated) where negated=false means in and negated=true means notIn
	 * @param version The uuid version number to constrain to
	 * @param variant The uuid variant to constrain to
	 * @param nil If present, requires the uuid to be the nil uuid
	 * @param notNil If present, requires the uuid to not be the nil uuid
	 * @param max If present, requires the uuid to be the max uuid
	 * @param custom A custom constraint implementation
	 * @throws NullPointerException If any optional field is null
	 * @throws IllegalArgumentException If the 'in' constraint set is empty when present
	 * @throws IllegalArgumentException If 'version' is not between 0 and 5 when present
	 * @throws IllegalArgumentException If both 'nil' and 'notNil' constraints are present
	 * @throws IllegalArgumentException If both 'nil' and 'max' constraints are present
	 */
	public UUIDConstraintConfig {
		Objects.requireNonNull(equalTo, "Optional for 'equal to' constraint must not be null");
		Objects.requireNonNull(in, "Optional for 'in' constraint must not be null");
		Objects.requireNonNull(version, "Optional for 'version' constraint must not be null");
		Objects.requireNonNull(variant, "Optional for 'variant' constraint must not be null");
		Objects.requireNonNull(nil, "Optional for 'nil' constraint must not be null");
		Objects.requireNonNull(notNil, "Optional for 'not nil' constraint must not be null");
		Objects.requireNonNull(max, "Optional for 'max' constraint must not be null");
		Objects.requireNonNull(custom, "Optional for 'custom' constraint must not be null");
		
		if (in.isPresent() && in.get().getFirst().isEmpty()) {
			throw new IllegalArgumentException("In constraint set must not be empty when present");
		}
		
		if (version.isPresent() && (version.get() < 0 || version.get() > 5)) {
			throw new IllegalArgumentException("Version must be between 0 and 5 when present, but got " + version.get());
		}
		
		if (nil.isPresent() && notNil.isPresent()) {
			throw new IllegalArgumentException("Nil and not nil are mutually exclusive");
		}
		
		if (nil.isPresent() && max.isPresent()) {
			throw new IllegalArgumentException("Nil and max are mutually exclusive");
		}
	}
	
	/**
	 * Creates a new config with the specified equal-to constraint.<br>
	 *
	 * @param value The exact uuid that should be matched
	 * @return A new config with the constraint applied
	 */
	public @NonNull UUIDConstraintConfig withEqualTo(@NonNull UUID value) {
		Objects.requireNonNull(value, "Value for 'equal to' constraint must not be null");
		return new UUIDConstraintConfig(Optional.of(Pair.of(value, false)), this.in, this.version, this.variant, this.nil, this.notNil, this.max, this.custom);
	}
	
	/**
	 * Creates a new config with the specified not-equal-to constraint.<br>
	 *
	 * @param value The uuid that should be excluded
	 * @return A new config with the constraint applied
	 */
	public @NonNull UUIDConstraintConfig withNotEqualTo(@NonNull UUID value) {
		Objects.requireNonNull(value, "Value for 'not equal to' constraint must not be null");
		return new UUIDConstraintConfig(Optional.of(Pair.of(value, true)), this.in, this.version, this.variant, this.nil, this.notNil, this.max, this.custom);
	}
	
	/**
	 * Creates a new config with the specified inclusion constraint.<br>
	 *
	 * @param values The collection of UUIDs that are allowed
	 * @return A new config with the constraint applied
	 */
	public @NonNull UUIDConstraintConfig withIn(@NonNull Collection<UUID> values) {
		Objects.requireNonNull(values, "Values for 'in' constraint must not be null");
		return new UUIDConstraintConfig(this.equalTo, Optional.of(Pair.of(Set.copyOf(values), false)), this.version, this.variant, this.nil, this.notNil, this.max, this.custom);
	}
	
	/**
	 * Creates a new config with the specified exclusion constraint.<br>
	 *
	 * @param values The collection of UUIDs that are not allowed
	 * @return A new config with the constraint applied
	 */
	public @NonNull UUIDConstraintConfig withNotIn(@NonNull Collection<UUID> values) {
		Objects.requireNonNull(values, "Values for 'not in' constraint must not be null");
		return new UUIDConstraintConfig(this.equalTo, Optional.of(Pair.of(Set.copyOf(values), true)), this.version, this.variant, this.nil, this.notNil, this.max, this.custom);
	}
	
	/**
	 * Creates a new config with the specified version constraint.<br>
	 *
	 * @param version The uuid version number to constrain to
	 * @return A new config with the constraint applied
	 */
	public @NonNull UUIDConstraintConfig withVersion(int version) {
		return new UUIDConstraintConfig(this.equalTo, this.in, Optional.of(version), this.variant, this.nil, this.notNil, this.max, this.custom);
	}
	
	/**
	 * Creates a new config with the specified variant constraint.<br>
	 *
	 * @param variant The uuid variant to constrain to
	 * @return A new config with the constraint applied
	 */
	public @NonNull UUIDConstraintConfig withVariant(@NonNull UUIDVariant variant) {
		Objects.requireNonNull(variant, "Variant for 'variant' constraint must not be null");
		return new UUIDConstraintConfig(this.equalTo, this.in, this.version, Optional.of(variant), this.nil, this.notNil, this.max, this.custom);
	}
	
	/**
	 * Creates a new config with the nil uuid constraint enabled.<br>
	 * <p>
	 *     The nil uuid is a special uuid with all bits set to zero (00000000-0000-0000-0000-000000000000).
	 * </p>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull UUIDConstraintConfig withNil() {
		return new UUIDConstraintConfig(this.equalTo, this.in, this.version, this.variant, Optional.of(Unit.INSTANCE), this.notNil, this.max, this.custom);
	}
	
	/**
	 * Creates a new config with the not-nil uuid constraint enabled.<br>
	 * <p>
	 *     Requires the uuid to not be the nil uuid (00000000-0000-0000-0000-000000000000).
	 * </p>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull UUIDConstraintConfig withNotNil() {
		return new UUIDConstraintConfig(this.equalTo, this.in, this.version, this.variant, this.nil, Optional.of(Unit.INSTANCE), this.max, this.custom);
	}
	
	/**
	 * Creates a new config with the max uuid constraint enabled.<br>
	 * <p>
	 *     The max uuid is a special uuid with all bits set to one (ffffffff-ffff-ffff-ffff-ffffffffffff).
	 * </p>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull UUIDConstraintConfig withMax() {
		return new UUIDConstraintConfig(this.equalTo, this.in, this.version, this.variant, this.nil, this.notNil, Optional.of(Unit.INSTANCE), this.custom);
	}
	
	/**
	 * Creates a new config with the specified custom constraint.<br>
	 *
	 * @param constraint The custom constraint implementation
	 * @return A new config with the constraint applied
	 */
	public @NonNull UUIDConstraintConfig withCustom(@NonNull Constraint<UUID> constraint) {
		Objects.requireNonNull(constraint, "Custom constraint must not be null");
		return new UUIDConstraintConfig(this.equalTo, this.in, this.version, this.variant, this.nil, this.notNil, this.max, Optional.of(constraint));
	}
	
	@Override
	public @NotNull Result<Void> matches(@NonNull UUID value) {
		Objects.requireNonNull(value, "Value must not be null");
		return allOf(
			() -> matchEqualTo(value, this.equalTo),
			() -> matchIn(value, this.in),
			() -> this.matchVersion(value, this.version),
			() -> this.matchVariant(value, this.variant),
			() -> matchFlag(value, this.nil, u -> u.equals(NIL_UUID), "UUID '" + value + "' must be the nil UUID"),
			() -> matchFlag(value, this.notNil, u -> !u.equals(NIL_UUID), "UUID '" + value + "' must not be the nil UUID"),
			() -> matchFlag(value, this.max, u -> u.equals(MAX_UUID), "UUID '" + value + "' must be the max UUID"),
			() -> matchCustom(value, this.custom)
		);
	}
	
	private @NonNull Result<Void> matchVersion(@NonNull UUID value, @NonNull Optional<Integer> version) {
		if (version.isEmpty()) {
			return Result.success();
		}
		int expected = version.get();
		int actual = value.version();
		if (actual != expected) {
			return Result.error("UUID '" + value + "' must have version " + expected + " but has version " + actual);
		}
		return Result.success();
	}
	
	private @NonNull Result<Void> matchVariant(@NonNull UUID value, @NonNull Optional<UUIDVariant> variant) {
		if (variant.isEmpty()) {
			return Result.success();
		}
		UUIDVariant expected = variant.get();
		int actualVariant = value.variant();
		UUIDVariant actual = switch (actualVariant) {
			case 0 -> UUIDVariant.NFC;
			case 2 -> UUIDVariant.RFC_4122;
			case 6 -> UUIDVariant.MICROSOFT;
			case 7 -> UUIDVariant.RESERVED;
			default -> null;
		};
		if (actual != expected) {
			return Result.error("UUID '" + value + "' must have variant " + expected + " but has variant " + actual);
		}
		return Result.success();
	}
}
