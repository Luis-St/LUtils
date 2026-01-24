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

package net.luis.utils.io.codec.constraint.config.temporal;

import net.luis.utils.io.codec.constraint.core.Constraint;
import net.luis.utils.io.codec.constraint.config.ConstraintConfig;
import net.luis.utils.io.codec.constraint.config.matcher.ConstraintMatchers;
import net.luis.utils.util.Pair;
import net.luis.utils.util.result.Result;
import org.jetbrains.annotations.NotNull;
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
) implements ConstraintConfig<Instant> {
	
	/**
	 * An unconstrained Instant configuration with no constraints applied.<br>
	 */
	public static final InstantConstraintConfig UNCONSTRAINED = new InstantConstraintConfig(
		Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
	);
	
	/**
	 * Constructs a new instant constraint config with the specified parameters.<br>
	 *
	 * @param equalTo The Instant equality constraint as a pair of (value, negated) where negated=false means equalTo and negated=true means notEqualTo
	 * @param in The Instant set constraint as a pair of (values, negated) where negated=false means in and negated=true means notIn
	 * @param after The "after" temporal constraint as a pair of (value, inclusive)
	 * @param before The "before" temporal constraint as a pair of (value, inclusive)
	 * @param withinLast A Duration specifying how far back from now values must fall
	 * @param withinNext A Duration specifying how far ahead from now values must fall
	 * @param custom A custom constraint implementation
	 * @throws NullPointerException If any of the optional fields is null
	 * @throws IllegalArgumentException If the 'in' set is empty when present
	 * @throws IllegalArgumentException If withinLast duration is not positive when present
	 * @throws IllegalArgumentException If withinNext duration is not positive when present
	 */
	public InstantConstraintConfig {
		Objects.requireNonNull(equalTo, "Optional for 'equal to' constraint must not be null");
		Objects.requireNonNull(in, "Optional for 'in' constraint must not be null");
		Objects.requireNonNull(after, "Optional for 'after' constraint must not be null");
		Objects.requireNonNull(before, "Optional for 'before' constraint must not be null");
		Objects.requireNonNull(withinLast, "Optional for 'within last' constraint must not be null");
		Objects.requireNonNull(withinNext, "Optional for 'within next' constraint must not be null");
		Objects.requireNonNull(custom, "Optional for 'custom' constraint must not be null");
		
		if (in.isPresent() && in.get().getFirst().isEmpty()) {
			throw new IllegalArgumentException("In set must not be empty when present");
		}
		
		if (withinLast.isPresent() && (withinLast.get().isNegative() || withinLast.get().isZero())) {
			throw new IllegalArgumentException("Within last duration must be positive when present, but got " + withinLast.get());
		}
		
		if (withinNext.isPresent() && (withinNext.get().isNegative() || withinNext.get().isZero())) {
			throw new IllegalArgumentException("Within next duration must be positive when present, but got " + withinNext.get());
		}
	}
	
	//region With methods
	
	/**
	 * Creates a new config with the specified equal-to constraint.<br>
	 *
	 * @param value The exact Instant that should be matched
	 * @return A new config with the constraint applied
	 */
	public @NonNull InstantConstraintConfig withEqualTo(@NonNull Instant value) {
		Objects.requireNonNull(value, "Value for 'equal to' constraint must not be null");
		return new InstantConstraintConfig(Optional.of(Pair.of(value, false)), this.in, this.after, this.before, this.withinLast, this.withinNext, this.custom);
	}
	
	/**
	 * Creates a new config with the specified not-equal-to constraint.<br>
	 *
	 * @param value The Instant that should be excluded
	 * @return A new config with the constraint applied
	 */
	public @NonNull InstantConstraintConfig withNotEqualTo(@NonNull Instant value) {
		Objects.requireNonNull(value, "Value for 'not equal to' constraint must not be null");
		return new InstantConstraintConfig(Optional.of(Pair.of(value, true)), this.in, this.after, this.before, this.withinLast, this.withinNext, this.custom);
	}
	
	/**
	 * Creates a new config with the specified inclusion constraint.<br>
	 *
	 * @param values The collection of Instants that are allowed
	 * @return A new config with the constraint applied
	 */
	public @NonNull InstantConstraintConfig withIn(@NonNull Collection<Instant> values) {
		Objects.requireNonNull(values, "Values for 'in' constraint must not be null");
		return new InstantConstraintConfig(this.equalTo, Optional.of(Pair.of(Set.copyOf(values), false)), this.after, this.before, this.withinLast, this.withinNext, this.custom);
	}
	
	/**
	 * Creates a new config with the specified exclusion constraint.<br>
	 *
	 * @param values The collection of Instants that are not allowed
	 * @return A new config with the constraint applied
	 */
	public @NonNull InstantConstraintConfig withNotIn(@NonNull Collection<Instant> values) {
		Objects.requireNonNull(values, "Values for 'not in' constraint must not be null");
		return new InstantConstraintConfig(this.equalTo, Optional.of(Pair.of(Set.copyOf(values), true)), this.after, this.before, this.withinLast, this.withinNext, this.custom);
	}
	
	/**
	 * Creates a new config with the specified after constraint (exclusive).<br>
	 *
	 * @param value The threshold Instant (exclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull InstantConstraintConfig withAfter(@NonNull Instant value) {
		Objects.requireNonNull(value, "Value for 'after' constraint must not be null");
		return new InstantConstraintConfig(this.equalTo, this.in, Optional.of(Pair.of(value, false)), this.before, this.withinLast, this.withinNext, this.custom);
	}
	
	/**
	 * Creates a new config with the specified after-or-equal constraint (inclusive).<br>
	 *
	 * @param value The threshold Instant (inclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull InstantConstraintConfig withAfterOrEqual(@NonNull Instant value) {
		Objects.requireNonNull(value, "Value for 'after or equal' constraint must not be null");
		return new InstantConstraintConfig(this.equalTo, this.in, Optional.of(Pair.of(value, true)), this.before, this.withinLast, this.withinNext, this.custom);
	}
	
	/**
	 * Creates a new config with the specified before constraint (exclusive).<br>
	 *
	 * @param value The threshold Instant (exclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull InstantConstraintConfig withBefore(@NonNull Instant value) {
		Objects.requireNonNull(value, "Value for 'before' constraint must not be null");
		return new InstantConstraintConfig(this.equalTo, this.in, this.after, Optional.of(Pair.of(value, false)), this.withinLast, this.withinNext, this.custom);
	}
	
	/**
	 * Creates a new config with the specified before-or-equal constraint (inclusive).<br>
	 *
	 * @param value The threshold Instant (inclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull InstantConstraintConfig withBeforeOrEqual(@NonNull Instant value) {
		Objects.requireNonNull(value, "Value for 'before or equal' constraint must not be null");
		return new InstantConstraintConfig(this.equalTo, this.in, this.after, Optional.of(Pair.of(value, true)), this.withinLast, this.withinNext, this.custom);
	}
	
	/**
	 * Creates a new config with the specified between constraint (exclusive on both bounds).<br>
	 *
	 * @param after The minimum Instant (exclusive)
	 * @param before The maximum Instant (exclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull InstantConstraintConfig withBetween(@NonNull Instant after, @NonNull Instant before) {
		Objects.requireNonNull(after, "After value for 'between' constraint must not be null");
		Objects.requireNonNull(before, "Before value for 'between' constraint must not be null");
		return new InstantConstraintConfig(this.equalTo, this.in, Optional.of(Pair.of(after, false)), Optional.of(Pair.of(before, false)), this.withinLast, this.withinNext, this.custom);
	}
	
	/**
	 * Creates a new config with the specified between constraint (inclusive on both bounds).<br>
	 *
	 * @param after The minimum Instant (inclusive)
	 * @param before The maximum Instant (inclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull InstantConstraintConfig withBetweenOrEqual(@NonNull Instant after, @NonNull Instant before) {
		Objects.requireNonNull(after, "After value for 'between or equal' constraint must not be null");
		Objects.requireNonNull(before, "Before value for 'between or equal' constraint must not be null");
		return new InstantConstraintConfig(this.equalTo, this.in, Optional.of(Pair.of(after, true)), Optional.of(Pair.of(before, true)), this.withinLast, this.withinNext, this.custom);
	}
	
	/**
	 * Creates a new config with the specified within-last constraint.<br>
	 *
	 * @param duration The duration backwards from now
	 * @return A new config with the constraint applied
	 */
	public @NonNull InstantConstraintConfig withWithinLast(@NonNull Duration duration) {
		Objects.requireNonNull(duration, "Duration for 'within last' constraint must not be null");
		return new InstantConstraintConfig(this.equalTo, this.in, this.after, this.before, Optional.of(duration), this.withinNext, this.custom);
	}
	
	/**
	 * Creates a new config with the specified within-next constraint.<br>
	 *
	 * @param duration The duration forwards from now
	 * @return A new config with the constraint applied
	 */
	public @NonNull InstantConstraintConfig withWithinNext(@NonNull Duration duration) {
		Objects.requireNonNull(duration, "Duration for 'within next' constraint must not be null");
		return new InstantConstraintConfig(this.equalTo, this.in, this.after, this.before, this.withinLast, Optional.of(duration), this.custom);
	}
	
	/**
	 * Creates a new config with the specified custom constraint.<br>
	 *
	 * @param constraint The custom constraint implementation
	 * @return A new config with the constraint applied
	 */
	public @NonNull InstantConstraintConfig withCustom(@NonNull Constraint<Instant> constraint) {
		Objects.requireNonNull(constraint, "Custom constraint must not be null");
		return new InstantConstraintConfig(this.equalTo, this.in, this.after, this.before, this.withinLast, this.withinNext, Optional.of(constraint));
	}
	//endregion
	
	@Override
	public @NotNull Result<Void> matches(@NonNull Instant value) {
		Objects.requireNonNull(value, "Value must not be null");
		
		return ConstraintMatchers.allOf(
			() -> ConstraintMatchers.matchEqualTo(value, this.equalTo),
			() -> ConstraintMatchers.matchIn(value, this.in),
			() -> ConstraintMatchers.matchRange(value, this.after, this.before),
			() -> ConstraintMatchers.matchWithinLast(value, this.withinLast, Instant::now, Instant::minus, "Instant"),
			() -> ConstraintMatchers.matchWithinNext(value, this.withinNext, Instant::now, Instant::plus, "Instant"),
			() -> ConstraintMatchers.matchCustom(value, this.custom)
		);
	}
}
