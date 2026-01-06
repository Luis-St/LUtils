package net.luis.utils.io.codec.constraint_new;

import net.luis.utils.util.result.Result;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

/**
 * Constraint interface that defines a validation method for a given value of type {@code T}.<br>
 * This interface can be implemented to create various constraints that can be applied to values during encoding or decoding processes.<br>
 *
 * @author Luis-St
 *
 * @param <T> The type of the value to be validated
 */
@FunctionalInterface
public interface Constraint<T> {
	
	/**
	 * Validates the given value against the constraint.<br>
	 * <p>
	 *     If the value satisfies the constraint, a successful Result is returned.<br>
	 *     If the value does not satisfy the constraint, a failed Result with an appropriate error message is returned.
	 * </p>
	 *
	 * @param value The value to be validated
	 * @return A result indicating success or failure of the validation
	 * @throws NullPointerException If the value is null
	 */
	@NotNull Result<Void> validate(@NonNull T value);
}
