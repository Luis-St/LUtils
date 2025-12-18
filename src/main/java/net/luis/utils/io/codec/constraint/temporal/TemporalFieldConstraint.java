package net.luis.utils.io.codec.constraint.temporal;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.constraint.ComparableConstraint;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.time.temporal.ChronoField;
import java.time.temporal.Temporal;
import java.util.Objects;
import java.util.Set;

/**
 *
 * @author Luis-St
 *
 */

public interface TemporalFieldConstraint<T extends Temporal & Comparable<T>, C extends Codec<T>> extends ComparableConstraint<Integer, C> {
	
	static <T extends Temporal & Comparable<T>, C extends Codec<T>> @NonNull TemporalFieldConstraint<T, C> of(@NotNull TemporalConstraint<T, C> parent, @NotNull ChronoField field) {
		Objects.requireNonNull(parent, "Parent temporal constraint must not be null");
		Objects.requireNonNull(field, "Chrono field must not be null");
		
		return new TemporalFieldConstraint<>() {
			@Override
			public @NotNull TemporalConstraint<T, C> parent() {
				return parent;
			}
			
			@Override
			public @NotNull ChronoField field() {
				return field;
			}
		};
	}
	
	@NotNull TemporalConstraint<T, C> parent();
	
	@NotNull ChronoField field();
	
	@Override
	default @NonNull C greaterThan(@NonNull Integer value) {
		return null;
	}
	
	@Override
	default @NonNull C greaterThanOrEqual(@NonNull Integer value) {
		return null;
	}
	
	@Override
	default @NonNull C lessThan(@NonNull Integer value) {
		return null;
	}
	
	@Override
	default @NonNull C lessThanOrEqual(@NonNull Integer value) {
		return null;
	}
	
	@Override
	default @NonNull C between(@NonNull Integer min, @NonNull Integer max) {
		return null;
	}
	
	@Override
	default @NonNull C betweenExclusive(@NonNull Integer min, @NonNull Integer max) {
		return null;
	}
	
	@Override
	default @NonNull C equalTo(@NonNull Integer value) {
		return null;
	}
	
	@Override
	default @NonNull C notEqualTo(@NonNull Integer value) {
		return null;
	}
	
	@Override
	default @NonNull C in(@NotNull Set<Integer> values) {
		return null;
	}
	
	@Override
	default @NonNull C in(Integer @NotNull ... values) {
		return null;
	}
	
	@Override
	default @NonNull C notIn(@NotNull Set<Integer> values) {
		return null;
	}
	
	@Override
	default @NonNull C notIn(Integer @NotNull ... values) {
		return null;
	}
}
