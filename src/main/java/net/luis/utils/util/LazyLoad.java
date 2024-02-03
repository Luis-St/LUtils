package net.luis.utils.util;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * Represents a lazy-loaded value.<br>
 *
 * @author Luis-St
 *
 * @param <T> The type of the value
 */
public class LazyLoad<T> implements Supplier<T> {
	
	/**
	 * The supplier of the value to be loaded lazily.<br>
	 */
	private final Supplier<T> supplier;
	/**
	 * The cached value of the supplier.<br>
	 * The value is {@code null} if the supplier has not been called yet.<br>
	 */
	private T value;
	
	/**
	 * Constructs a new {@link LazyLoad} with the specified supplier.<br>
	 * @param supplier The supplier of the value to be loaded lazily
	 * @throws NullPointerException If the supplier is null
	 */
	public LazyLoad(@NotNull Supplier<T> supplier) {
		this.supplier = Objects.requireNonNull(supplier, "Supplied value must not be null");
	}
	
	/**
	 * Loads the value from the supplier if it has not been loaded yet.<br>
	 */
	public void load() {
		if (this.value == null) {
			this.value = this.supplier.get();
		}
	}
	
	/**
	 * Checks whether the value has been loaded yet or not.<br>
	 * @return True if the value has been loaded, false otherwise
	 */
	public boolean isLoaded() {
		return this.value != null;
	}
	
	/**
	 * Gets the cached value.<br>
	 * If the value has not been loaded yet, it will be loaded first.<br>
	 * @return The value
	 * @see #load()
	 */
	@Override
	public T get() {
		this.load();
		return this.value;
	}
	
	//region Object overrides
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof LazyLoad<?> lazyLoad)) return false;
		
		return Objects.equals(this.value, lazyLoad.value);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.supplier);
	}
	
	@Override
	public String toString() {
		return "LazyLoad{value=" + this.value + "}";
	}
	//endregion
}
