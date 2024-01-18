package net.luis.utils.function;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Function;

/**
 * Functional interface that takes three arguments and returns a value.<br>
 * The {@link FunctionalInterface} method is {@link #apply(Object, Object, Object)}.<br>
 *
 * @author Luis-St
 *
 * @param <T> The type of the first argument
 * @param <U> The type of the second argument
 * @param <V> The type of the third argument
 */
@FunctionalInterface
public interface TriFunction<T, U, V, R> {
	
	/**
	 * Applies the arguments to the function and returns a value.<br>
	 * @param t The first argument
	 * @param u The second argument
	 * @param v The third argument
	 * @return The result
	 */
	R apply(T t, U u, V v);
	
	/**
	 * Returns a composed function that first applies this function to its input,<br>
	 * and then applies the {@code after} function to the result.<br>
	 * @param after The function to apply after this function is applied
	 * @return The composed function
	 * @param <S> The type of the output of the {@code after} function, and of the composed function
	 * @throws NullPointerException If the {@code after} function is null
	 */
	default <S> @NotNull TriFunction<T, U, V, S> andThen(@NotNull Function<? super R, ? extends S> after) {
		return (T t, U u, V v) -> Objects.requireNonNull(after, "'After' function must not be null").apply(this.apply(t, u, v));
	}
}
