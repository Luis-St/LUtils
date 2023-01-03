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
public interface QuadFunction<T, U, V, W, R> {
	
	R apply(T t, U u, V v, W w);
	
	default <S> @NotNull QuadFunction<T, U, V, W, S> andThen(Function<? super R, ? extends S> after) {
		Objects.requireNonNull(after);
		return (T t, U u, V v, W w) -> {
			return after.apply(this.apply(t, u, v, w));
		};
	}
	
}
