package net.luis.utils.function;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 *
 * @author Luis-st
 *
 */

@FunctionalInterface
public interface TriConsumer<T, U, V> {
	
	void accept(@NotNull T t, @NotNull U u, @NotNull V v);
	
	default @NotNull TriConsumer<T, U, V> andThen(@NotNull TriConsumer<? super T, ? super U, ? super V> consumer) {
		return (t, u, v) -> {
			this.accept(t, u, v);
			Objects.requireNonNull(consumer).accept(t, u, v);
		};
	}
	
}
