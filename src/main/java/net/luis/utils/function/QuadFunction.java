package net.luis.utils.function;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Function;

/**
 *
 * @author Luis-St
 *
 */

@FunctionalInterface
public interface QuadFunction<T, U, V, W, R> {
	
	R apply(T t, U u, V v, W w);
	
	default <S> @NotNull QuadFunction<T, U, V, W, S> andThen(Function<? super R, ? extends S> after) {
		return (T t, U u, V v, W w) -> Objects.requireNonNull(after, "'After' function must not be null").apply(this.apply(t, u, v, w));
	}
}
