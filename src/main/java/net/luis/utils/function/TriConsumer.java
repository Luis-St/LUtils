package net.luis.utils.function;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Functional interface that takes three arguments and consumes them.<br>
 * The {@link FunctionalInterface} method is {@link #accept(Object, Object, Object)}.<br>
 *
 * @author Luis-St
 *
 * @param <T> The type of the first argument
 * @param <U> The type of the second argument
 * @param <V> The type of the third argument
 */
@FunctionalInterface
public interface TriConsumer<T, U, V> {
	
	/**
	 * Takes three arguments and consumes them.<br>
	 * @param t The first argument
	 * @param u The second argument
	 * @param v The third argument
	 */
	void accept(T t, U u, V v);
	
	/**
	 * Returns a composed consumer of same type that performs, in sequence,<br>
	 * this operation followed by the {@code after} operation.<br>
	 * @param after The operation to perform after this operation
	 * @return A composed consumer that performs in sequence this operation followed by the {@code after} operation
	 * @throws NullPointerException If the {@code after} operation is null
	 */
	default @NotNull TriConsumer<T, U, V> andThen(@NotNull TriConsumer<? super T, ? super U, ? super V> after) {
		Objects.requireNonNull(after, "'After' function must not be null");
		return (t, u, v) -> {
			this.accept(t, u, v);
			after.accept(t, u, v);
		};
	}
}
