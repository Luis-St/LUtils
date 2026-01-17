package net.luis.utils.io.codec.constraint_new;

import net.luis.utils.io.codec.constraint_new.config.EnumConstraintConfig;
import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.function.UnaryOperator;

/**
 * Base interface for all enum constraint types that provides fundamental equality and membership operations.<br>
 *
 * @author Luis-St
 *
 * @param <T> The type of the constraint configuration
 * @param <C> The return type of the constraint method (for fluent method chaining)
 */
public interface EnumConstraint<T extends Enum<T>, C> extends ApplicableConstraint<EnumConstraintConfig<T>, C>, BaseConstraint<T, C> {
	
	@Override
	@NonNull C apply(@NonNull UnaryOperator<EnumConstraintConfig<T>> configModifier);
	
	@Override
	default @NonNull C equalTo(@NonNull T value) {
		return this.apply(config -> config.withEqualTo(value));
	}
	
	@Override
	default @NonNull C notEqualTo(@NonNull T value) {
		return this.apply(config -> config.withNotEqualTo(value));
	}
	
	@Override
	default @NonNull C in(@NonNull Collection<T> values) {
		return this.apply(config -> config.withIn(values));
	}
	
	@Override
	default @NonNull C notIn(@NonNull Collection<T> values) {
		return this.apply(config -> config.withNotIn(values));
	}
	
	@Override
	default @NonNull C custom(@NonNull Constraint<T> constraint) {
		return this.apply(config -> config.withCustom(constraint));
	}
}
