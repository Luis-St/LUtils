package net.luis.utils.io.codec.constraint_new;

import org.jspecify.annotations.NonNull;

import java.util.function.UnaryOperator;

/**
 * Functional interface for constraints where the configuration can be modified via a function.<br>
 *
 * @author Luis-St
 *
 * @param <T> The type of the constraint configuration
 * @param <C> The return type of the apply method (for fluent method chaining)
 */
@FunctionalInterface
public interface ApplicableConstraint<T, C> {
	
	/**
	 * Applies a modification to the constraint configuration and returns a new constraint instance with the updated configuration.<br>
	 *
	 * @param configModifier The function that modifies the constraint configuration
	 * @return A new constraint instance with the updated configuration
	 * @throws NullPointerException If the config modifier is null
	 */
	@NonNull C apply(@NonNull UnaryOperator<T> configModifier);
}
