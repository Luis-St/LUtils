package net.luis.utils.util;

/**
 *
 * @author Luis-St
 *
 */

public class MutableEntry<K, V> extends SimpleEntry<K, V> {
	
	public MutableEntry(K key, V value) {
		super(key, value);
	}
	
	@Override
	public V setValue(V value) {
		V oldValue = this.value;
		this.value = value;
		return oldValue;
	}
	
	//region Object overrides
	@Override
	public String toString() {
		return "MutableEntry{key=" + this.getKey() + ", value=" + this.value + "}";
	}
	//endregion
}
