package net.luis.utils.util;

import java.util.ConcurrentModificationException;

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
			throw new NullPointerException("Object has not been yet instantiated");
		} else {
			return this.object.getValue();
		}
	}

	public void set(T value) {
		if (this.instantiated) {
			throw new ConcurrentModificationException("Can not modify a final object");
		} else {
			this.object.setValue(value);
			this.instantiated = true;
		}
	}
	
	@Override
	public boolean equals(Object object) {
		if (object instanceof LazyInstantiation<?> lazyInstantiation) {
			if (this.instantiated != lazyInstantiation.instantiated) {
				return false;
			} else  {
				return this.object.equals(lazyInstantiation.object);
			}
		}
		return false;
	}
	
	@Override
	public String toString() {
		if (!this.instantiated) {
			return "null";
		}
		return this.get().toString();
	}
	
}
