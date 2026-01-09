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

package net.luis.utils.io.codec.constraint_new.config.temporal;

import net.luis.utils.io.codec.constraint_new.Constraint;
import net.luis.utils.util.Pair;
import org.jspecify.annotations.NonNull;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

/**
 * Configuration record for Instant type constraints.<br>
 * <p>
 *     This record stores the constraint values for Instant codecs.<br>
 *     It includes base constraints, temporal comparable constraints, and temporal span constraints.
 * </p>
 * <p>
 *     The after and before fields use {@link Pair} where the first value is the bound
 *     and the second value indicates whether the bound is inclusive (true) or exclusive (false).
 * </p>
 * <p>
 *     The equalTo field uses {@link Pair} where the first value is the Instant and
 *     the second value indicates negation (false=equalTo, true=notEqualTo).
 * </p>
 * <p>
 *     The in field uses {@link Pair} where the first value is the set of Instants and
 *     the second value indicates negation (false=in, true=notIn).
 * </p>
 *
 * @author Luis-St
 *
 * @param equalTo The Instant equality constraint as a pair of (value, negated) where negated=false means equalTo and negated=true means notEqualTo
 * @param in The Instant set constraint as a pair of (values, negated) where negated=false means in and negated=true means notIn
 * @param after The "after" temporal constraint as a pair of (value, inclusive)
 * @param before The "before" temporal constraint as a pair of (value, inclusive)
 * @param withinLast A Duration specifying how far back from now values must fall
 * @param withinNext A Duration specifying how far ahead from now values must fall
 * @param custom A custom constraint implementation
 */
public record InstantConstraintConfig(
	@NonNull Optional<Pair<Instant, Boolean>> equalTo,
	@NonNull Optional<Pair<Set<Instant>, Boolean>> in,
	@NonNull Optional<Pair<Instant, Boolean>> after,
	@NonNull Optional<Pair<Instant, Boolean>> before,
	@NonNull Optional<Duration> withinLast,
	@NonNull Optional<Duration> withinNext,
	@NonNull Optional<Constraint<Instant>> custom
) {
	
	/**
	 * An unconstrained Instant configuration with no constraints applied.<br>
	 */
	public static final InstantConstraintConfig UNCONSTRAINED = new InstantConstraintConfig(
		Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
	);
	
	/**
	 * Creates a new config with the specified equal-to constraint.<br>
	 *
	 * @param value The exact Instant that should be matched
	 * @return A new config with the constraint applied
	 */
	public @NonNull InstantConstraintConfig withEqualTo(@NonNull Instant value) {
		return new InstantConstraintConfig(Optional.of(Pair.of(Objects.requireNonNull(value), false)), this.in, this.after, this.before, this.withinLast, this.withinNext, this.custom);
	}
	
	/**
	 * Creates a new config with the specified not-equal-to constraint.<br>
	 *
	 * @param value The Instant that should be excluded
	 * @return A new config with the constraint applied
	 */
	public @NonNull InstantConstraintConfig withNotEqualTo(@NonNull Instant value) {
		return new InstantConstraintConfig(Optional.of(Pair.of(Objects.requireNonNull(value), true)), this.in, this.after, this.before, this.withinLast, this.withinNext, this.custom);
	}
	
	/**
	 * Creates a new config with the specified inclusion constraint.<br>
	 *
	 * @param values The collection of Instants that are allowed
	 * @return A new config with the constraint applied
	 */
	public @NonNull InstantConstraintConfig withIn(@NonNull Collection<Instant> values) {
		return new InstantConstraintConfig(this.equalTo, Optional.of(Pair.of(Set.copyOf(values), false)), this.after, this.before, this.withinLast, this.withinNext, this.custom);
	}
	
	/**
	 * Creates a new config with the specified exclusion constraint.<br>
	 *
	 * @param values The collection of Instants that are not allowed
	 * @return A new config with the constraint applied
	 */
	public @NonNull InstantConstraintConfig withNotIn(@NonNull Collection<Instant> values) {
		return new InstantConstraintConfig(this.equalTo, Optional.of(Pair.of(Set.copyOf(values), true)), this.after, this.before, this.withinLast, this.withinNext, this.custom);
	}
	
	/**
	 * Creates a new config with the specified after constraint (exclusive).<br>
	 *
	 * @param value The threshold Instant (exclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull InstantConstraintConfig withAfter(@NonNull Instant value) {
		return new InstantConstraintConfig(this.equalTo, this.in, Optional.of(Pair.of(Objects.requireNonNull(value), false)), this.before, this.withinLast, this.withinNext, this.custom);
	}
	
	/**
	 * Creates a new config with the specified after-or-equal constraint (inclusive).<br>
	 *
	 * @param value The threshold Instant (inclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull InstantConstraintConfig withAfterOrEqual(@NonNull Instant value) {
		return new InstantConstraintConfig(this.equalTo, this.in, Optional.of(Pair.of(Objects.requireNonNull(value), true)), this.before, this.withinLast, this.withinNext, this.custom);
	}
	
	/**
	 * Creates a new config with the specified before constraint (exclusive).<br>
	 *
	 * @param value The threshold Instant (exclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull InstantConstraintConfig withBefore(@NonNull Instant value) {
		return new InstantConstraintConfig(this.equalTo, this.in, this.after, Optional.of(Pair.of(Objects.requireNonNull(value), false)), this.withinLast, this.withinNext, this.custom);
	}
	
	/**
	 * Creates a new config with the specified before-or-equal constraint (inclusive).<br>
	 *
	 * @param value The threshold Instant (inclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull InstantConstraintConfig withBeforeOrEqual(@NonNull Instant value) {
		return new InstantConstraintConfig(this.equalTo, this.in, this.after, Optional.of(Pair.of(Objects.requireNonNull(value), true)), this.withinLast, this.withinNext, this.custom);
	}
	
	/**
	 * Creates a new config with the specified between constraint (exclusive on both bounds).<br>
	 *
	 * @param after The minimum Instant (exclusive)
	 * @param before The maximum Instant (exclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull InstantConstraintConfig withBetween(@NonNull Instant after, @NonNull Instant before) {
		return new InstantConstraintConfig(this.equalTo, this.in, Optional.of(Pair.of(Objects.requireNonNull(after), false)), Optional.of(Pair.of(Objects.requireNonNull(before), false)), this.withinLast, this.withinNext, this.custom);
	}
	
	/**
	 * Creates a new config with the specified between constraint (inclusive on both bounds).<br>
	 *
	 * @param after The minimum Instant (inclusive)
	 * @param before The maximum Instant (inclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull InstantConstraintConfig withBetweenOrEqual(@NonNull Instant after, @NonNull Instant before) {
		return new InstantConstraintConfig(this.equalTo, this.in, Optional.of(Pair.of(Objects.requireNonNull(after), true)), Optional.of(Pair.of(Objects.requireNonNull(before), true)), this.withinLast, this.withinNext, this.custom);
	}
	
	/**
	 * Creates a new config with the specified within-last constraint.<br>
	 *
	 * @param duration The duration backwards from now
	 * @return A new config with the constraint applied
	 */
	public @NonNull InstantConstraintConfig withWithinLast(@NonNull Duration duration) {
		return new InstantConstraintConfig(this.equalTo, this.in, this.after, this.before, Optional.of(Objects.requireNonNull(duration)), this.withinNext, this.custom);
	}
	
	/**
	 * Creates a new config with the specified within-next constraint.<br>
	 *
	 * @param duration The duration forwards from now
	 * @return A new config with the constraint applied
	 */
	public @NonNull InstantConstraintConfig withWithinNext(@NonNull Duration duration) {
		return new InstantConstraintConfig(this.equalTo, this.in, this.after, this.before, this.withinLast, Optional.of(Objects.requireNonNull(duration)), this.custom);
	}
	
	/**
	 * Creates a new config with the specified custom constraint.<br>
	 *
	 * @param constraint The custom constraint implementation
	 * @return A new config with the constraint applied
	 */
	public @NonNull InstantConstraintConfig withCustom(@NonNull Constraint<Instant> constraint) {
		return new InstantConstraintConfig(this.equalTo, this.in, this.after, this.before, this.withinLast, this.withinNext, Optional.of(Objects.requireNonNull(constraint)));
	}
}
