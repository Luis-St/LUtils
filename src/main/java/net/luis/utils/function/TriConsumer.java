package net.luis.utils.function;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 *
 * @author Luis-St
 *
 */

@FunctionalInterface
public interface TriConsumer<T, U, V> {
	
	void accept(T t, U u, V v);
	
	default @NotNull TriConsumer<T, U, V> andThen(@NotNull TriConsumer<? super T, ? super U, ? super V> consumer) {
		return (t, u, v) -> {
			this.accept(t, u, v);
			Objects.requireNonNull(consumer).accept(t, u, v);
		};
	}
	
}
