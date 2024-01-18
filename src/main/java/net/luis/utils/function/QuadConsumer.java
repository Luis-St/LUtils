package net.luis.utils.function;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Functional interface that takes four arguments and consumes them.<br>
 * The {@link FunctionalInterface} method is {@link #accept(Object, Object, Object, Object)}.<br>
 *
 * @author Luis-St
 *
 * @param <T> The type of the first argument
 * @param <U> The type of the second argument
 * @param <V> The type of the third argument
 * @param <W> The type of the fourth argument
 */
@FunctionalInterface
public interface QuadConsumer<T, U, V, W> {
	
	/**
	 * Takes four arguments and consumes them.<br>
	 * @param t The first argument
	 * @param u The second argument
	 * @param v The third argument
	 * @param w The fourth argument
	 */
	void accept(T t, U u, V v, W w);
	
	/**
	 * Returns a composed consumer of same type that performs, in sequence,<br>
	 * this operation followed by the {@code after} operation.<br>
	 * @param operation The operation to perform after this operation
	 * @return A composed consumer that performs in sequence this operation followed by the {@code after} operation
	 * @throws NullPointerException If the {@code after} operation is null
	 */
	default @NotNull QuadConsumer<T, U, V, W> andThen(@NotNull QuadConsumer<? super T, ? super U, ? super V, ? super W> operation) {
		return (t, u, v, w) -> {
			this.accept(t, u, v, w);
			Objects.requireNonNull(operation, "Operation must not be null").accept(t, u, v, w);
		};
	}
}
