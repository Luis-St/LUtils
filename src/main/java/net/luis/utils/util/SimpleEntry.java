package net.luis.utils.util;

import java.util.ConcurrentModificationException;
import java.util.Map;
import java.util.Objects;

/**
 *
 * @author Luis-st
 *
 */

public class SimpleEntry<K, V> implements Map.Entry<K, V> {
	
	private final K key;
	protected V value;
	
	public SimpleEntry(K key, V value) {
		this.key = key;
		this.value = value;
	}
	
	@Override
	public K getKey() {
		return this.key;
	}
	
	@Override
	public V getValue() {
		return this.value;
	}
	
	@Override
	public V setValue(V value) {
		throw new ConcurrentModificationException("Value of the entry cannot be set to " + value + ", because the entry is muted");
	}
	
	@Override
	public String toString() {
		return ToString.toString(this);
	}
	
	@Override
	public boolean equals(Object object) {
		if (object instanceof SimpleEntry<?, ?> entry) {
			if (!this.key.equals(entry.getKey())) {
				return false;
			} else {
				return Objects.equals(this.value, entry.getValue());
			}
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.key, this.value);
	}
	
}
