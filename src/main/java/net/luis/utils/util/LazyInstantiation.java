package net.luis.utils.util;

import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.Nullable;

import java.util.ConcurrentModificationException;
import java.util.Objects;

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
	
	public LazyInstantiation(T value) {
		this.object = new MutableObject<>();
		this.object.setValue(value);
		this.instantiated = true;
	}
	
	public @Nullable T get() {
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
		return Objects.requireNonNull(this.get()).toString();
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
