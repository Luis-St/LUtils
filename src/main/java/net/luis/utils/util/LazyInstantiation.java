package net.luis.utils.util;

import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.NotNull;
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
	
	public LazyInstantiation(@NotNull T value) {
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
	
	public void set(@NotNull T value) {
		if (this.instantiated) {
			throw new ConcurrentModificationException("Cannot change a final object");
		} else {
			this.object.setValue(value);
			this.instantiated = true;
		}
	}
	
	@Override
	public @NotNull String toString() {
		if (!this.instantiated) {
			return "null";
		}
		return Objects.requireNonNull(this.get()).toString();
	}
	
	@Override
	public boolean equals(@Nullable Object o) {
		if (this == o) return true;
		if (!(o instanceof LazyInstantiation<?> that)) return false;
		
		if (this.instantiated != that.instantiated) return false;
		return this.object.equals(that.object);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.object, this.instantiated);
	}
	
}
