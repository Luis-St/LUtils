package net.luis.utils.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A mutable implementation of the {@link java.util.Map.Entry} interface.<br>
 *
 * @see SimpleEntry
 *
 * @author Luis-St
 *
 * @param <K> The type of the key
 * @param <V> The type of the value
 */
public class MutableEntry<K, V> extends SimpleEntry<K, V> {
	
	/**
	 * Constructs a new {@link MutableEntry} with the specified key and value.<br>
	 * @param key The key of the entry
	 * @param value The value of the entry
	 * @throws NullPointerException If the key is null
	 */
	public MutableEntry(@NotNull K key, @Nullable V value) {
		super(key, value);
	}
	
	/**
	 * Sets the value of this entry to the specified value.<br>
	 * @param value The new value of this entry
	 * @return The old value of this entry
	 */
	@Override
	public @Nullable V setValue(@Nullable V value) {
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
