package net.luis.utils.function;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.util.Objects;

/**
 *
 * @author Luis-St
 *
 */

@FunctionalInterface
public interface QuadConsumer<T, U, V, W> {
	
	void accept(@UnknownNullability T t, @UnknownNullability U u, @UnknownNullability V v, @UnknownNullability W w);
	
	default @NotNull QuadConsumer<T, U, V, W> andThen(@NotNull QuadConsumer<? super T, ? super U, ? super V, ? super W> consumer) {
		return (t, u, v, w) -> {
			this.accept(t, u, v, w);
			Objects.requireNonNull(consumer, "'Then' action must not be null").accept(t, u, v, w);
		};
	}
}
