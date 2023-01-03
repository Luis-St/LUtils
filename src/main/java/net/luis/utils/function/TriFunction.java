package net.luis.utils.function;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Function;

/**
 *
 * @author Luis-st
 *
 */

@FunctionalInterface
public interface TriFunction<T, U, V, R> {
	
	R apply(T t, U u, V v);
	
	default <S> @NotNull TriFunction<T, U, V, S> andThen(Function<? super R, ? extends S> after) {
		Objects.requireNonNull(after);
		return (T t, U u, V v) -> {
			return after.apply(this.apply(t, u, v));
		};
	}
	
}
