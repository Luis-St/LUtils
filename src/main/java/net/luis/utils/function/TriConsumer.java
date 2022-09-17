package net.luis.utils.function;

import java.util.Objects;

/**
 *
 * @author Luis-st
 *
 */

@FunctionalInterface
public interface TriConsumer<T, U, V> {
	
	void accept(T t, U u, V v);
	
	default TriConsumer<T, U, V> andThen(TriConsumer<? super T, ? super U, ? super V> consumer) {
		Objects.requireNonNull(consumer);
		return (t, u, v) -> {
			this.accept(t, u, v);
			consumer.accept(t, u, v);
		};
	}
	
}
