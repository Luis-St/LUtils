package net.luis.utils.util;

import java.util.ConcurrentModificationException;
import java.util.Objects;

import org.apache.commons.lang3.mutable.MutableObject;

/**
 *
 * @author Luis-st
 *
 */

public class LazyInstantiation<T> {
	
	private final MutableObject<T> object;
	private boolean instantiated = false;
	
	public LazyInstantiation() {
		this.object = new MutableObject<>();
	}
	
	public LazyInstantiation(T object) {
		this.object = new MutableObject<>();
		this.set(object);
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
	
	@Override
	public String toString() {
		if (!this.instantiated) {
			return "null";
		}
		return this.get().toString();
	}
	
	@Override
	public boolean equals(Object object) {
		return Equals.equals(this, object);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.object, this.instantiated);
	}
	
}
