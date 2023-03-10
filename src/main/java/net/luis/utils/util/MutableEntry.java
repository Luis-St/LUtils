package net.luis.utils.util;

import org.jetbrains.annotations.Nullable;

/**
 *
 * @author Luis-st
 *
 */

public class MutableEntry<K, V> extends SimpleEntry<K, V> {
	
	public MutableEntry(K key, V value) {
		super(key, value);
	}
	
	@Override
	public @Nullable V setValue(@Nullable V value) {
		V oldValue = this.value;
		this.value = value;
		return oldValue;
	}
	
	@Override
	public String toString() {
		return ToString.toString(this);
	}
	
}
