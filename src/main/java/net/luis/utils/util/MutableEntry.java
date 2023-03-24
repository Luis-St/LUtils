package net.luis.utils.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 *
 * @author Luis-St
 *
 */

public class MutableEntry<K, V> extends SimpleEntry<K, V> {
	
	public MutableEntry(@NotNull K key, @Nullable V value) {
		super(key, value);
	}
	
	@Override
	public @Nullable V setValue(@Nullable V value) {
		V oldValue = this.value;
		this.value = value;
		return oldValue;
	}
	
	@Override
	public @NotNull String toString() {
		return ToString.toString(this);
	}
	
}
