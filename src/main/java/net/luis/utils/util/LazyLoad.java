package net.luis.utils.util;

import java.util.Objects;
import java.util.function.Supplier;

/**
 *
 * @author Luis-St
 *
 */

public class LazyLoad<T> implements Supplier<T> {
	
	private final Supplier<T> supplier;
	private T value;
	
	public LazyLoad(Supplier<T> supplier) {
		this.supplier = supplier;
	}
	
	public void load() {
		if (this.value == null) {
			this.value = this.supplier.get();
		}
	}
	
	public boolean isLoaded() {
		return this.value != null;
	}
	
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
		return Objects.hash(this.value);
	}
	
	@Override
	public String toString() {
		return "LazyLoad{supplier=" + this.supplier + ", value=" + this.value + "}";
	}
	//endregion
}
