package net.luis.utils.util;

import org.apache.commons.lang3.mutable.MutableObject;

import java.util.ConcurrentModificationException;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 *
 * @author Luis-St
 *
 */

public class LazyInstantiation<T> {
	
	private final MutableObject<T> object;
	private Consumer<T> action;
	private CompletableFuture<T> future;
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
			Utils.executeIfNotNull(this.action, (action) -> {
				action.accept(value);
				this.future.complete(value);
			});
		}
	}
	
	public boolean isInstantiated() {
		return this.instantiated;
	}
	
	public CompletableFuture<T> whenInstantiated(Consumer<T> action) {
		this.action = Objects.requireNonNull(action, "Instantiation action must not be null");
		return this.future = new CompletableFuture<>();
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
