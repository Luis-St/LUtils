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
	
	void accept(@NotNull T t, @NotNull U u, @NotNull V v, @NotNull W w);
	
	default @NotNull QuadConsumer<T, U, V, W> andThen(@NotNull QuadConsumer<? super T, ? super U, ? super V, ? super W> consumer) {
		return (t, u, v, w) -> {
			this.accept(t, u, v, w);
			Objects.requireNonNull(consumer).accept(t, u, v, w);
		};
	}
	
}
