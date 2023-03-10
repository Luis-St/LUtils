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
	
	R apply(@NotNull T t, @NotNull U u, @NotNull V v, @NotNull W w);
	
	default <S> @NotNull QuadFunction<T, U, V, W, S> andThen(@NotNull Function<? super R, ? extends S> after) {
		return (T t, U u, V v, W w) -> Objects.requireNonNull(after).apply(this.apply(t, u, v, w));
	}
	
}
