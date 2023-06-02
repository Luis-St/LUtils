package net.luis.utils.function;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 *
 * @author Luis-st
 *
 */

@FunctionalInterface
public interface QuadConsumer<T, U, V, W> {
	
	void accept(T t, U u, V v, W w);
	
	default @NotNull QuadConsumer<T, U, V, W> andThen(QuadConsumer<? super T, ? super U, ? super V, ? super W> consumer) {
		Objects.requireNonNull(consumer);
		return (t, u, v, w) -> {
			this.accept(t, u, v, w);
			consumer.accept(t, u, v, w);
		};
	}
	
}
