package net.luis.utils.util;

import org.apache.commons.lang3.mutable.MutableObject;

import java.util.ConcurrentModificationException;
import java.util.Objects;

/**
 *
 * @author Luis-St
 *
 */

public class LazyInstantiation<T> {
	
	private final MutableObject<T> object;
	private boolean instantiated = false;
	
	public LazyInstantiation() {
		this.object = new MutableObject<>();
	}
	
	public LazyInstantiation(T value) {
		this.object = new MutableObject<>();
		this.object.setValue(value);
		this.instantiated = true;
	}
	
	public T get() {
		if (!this.instantiated) {
			throw new NullPointerException("The object has not been instantiated yet");
		} else {
			return this.object.getValue();
		}
	}
	
	public void set(T value) {
		if (this.instantiated) {
			throw new ConcurrentModificationException("Cannot change a final object");
		} else {
			this.object.setValue(value);
			this.instantiated = true;
		}
	}
	
	//region Object overrides
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof LazyInstantiation<?> that)) return false;
		
		if (this.instantiated != that.instantiated) return false;
		return this.object.equals(that.object);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.object, this.instantiated);
	}
	
	@Override
	public String toString() {
		if (!this.instantiated) {
			return "null";
		}
		return Objects.requireNonNull(this.get()).toString();
	}
	//endregion
}
