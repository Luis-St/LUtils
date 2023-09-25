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
public interface TriFunction<T, U, V, R> {
	
	@UnknownNullability R apply(@UnknownNullability T t, @UnknownNullability U u, @UnknownNullability V v);
	
	default <S> @NotNull TriFunction<T, U, V, S> andThen(@NotNull Function<? super R, ? extends S> after) {
		return (T t, U u, V v) -> Objects.requireNonNull(after, "'After' function must not be null").apply(this.apply(t, u, v));
	}
}
