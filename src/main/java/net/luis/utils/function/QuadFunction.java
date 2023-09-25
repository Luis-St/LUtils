package net.luis.utils.function;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.util.Objects;
import java.util.function.Function;

/**
 *
 * @author Luis-St
 *
 */

@FunctionalInterface
public interface QuadFunction<T, U, V, W, R> {
	
	@UnknownNullability R apply(@UnknownNullability T t, @UnknownNullability U u, @UnknownNullability V v, @UnknownNullability W w);
	
	default <S> @NotNull QuadFunction<T, U, V, W, S> andThen(@NotNull Function<? super R, ? extends S> after) {
		return (T t, U u, V v, W w) -> Objects.requireNonNull(after, "'After' function must not be null").apply(this.apply(t, u, v, w));
	}
}
