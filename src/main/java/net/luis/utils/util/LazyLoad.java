/*
 * LUtils
 * Copyright (C) 2025 Luis Staudt
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package net.luis.utils.util;

import org.jspecify.annotations.NonNull;

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
	 * Constructs a new lazy load with the specified supplier.<br>
	 *
	 * @param supplier The supplier of the value to be loaded lazily
	 * @throws NullPointerException If the supplier is null
	 */
	public LazyLoad(@NonNull Supplier<T> supplier) {
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
	 *
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
		return "lazy (" + this.value + ")";
	}
	//endregion
}
