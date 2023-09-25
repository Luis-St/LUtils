package net.luis.utils.util;

import net.luis.utils.exception.AlreadyInitialisedException;
import net.luis.utils.exception.NotInitialisedException;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ConcurrentModificationException;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 *
 * @author Luis-St
 *
 */

// TODO: Rename to LazyInitialisation
public class LazyInstantiation<T> {
	
	private final MutableObject<T> object;
	private Consumer<T> action;
	private CompletableFuture<T> future;
	private boolean initialised;
	
	public LazyInstantiation() {
		this.object = new MutableObject<>();
	}
	
	public LazyInstantiation(@Nullable T value) {
		this.object = new MutableObject<>();
		this.object.setValue(value);
		this.initialised = true;
	}
	
	public @NotNull T get() {
		if (this.initialised) {
			return this.object.getValue();
		} else {
			throw new NotInitialisedException("The object has not been initialised yet");
		}
	}
	
	public void set(@Nullable T value) {
		if (this.initialised) {
			throw new AlreadyInitialisedException("The object has already been initialised");
		} else {
			this.object.setValue(value);
			this.initialised = true;
			Utils.executeIfNotNull(this.action, (action) -> {
				action.accept(value);
				this.future.complete(value);
			});
		}
	}
	
	public boolean isInstantiated() {
		return this.initialised;
	}
	
	public @NotNull CompletableFuture<T> whenInstantiated(@NotNull Consumer<@Nullable T> action) {
		this.action = Objects.requireNonNull(action, "Initialisation action must not be null");
		return this.future = new CompletableFuture<>();
	}
	
	//region Object overrides
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof LazyInstantiation<?> that)) return false;
		
		if (this.initialised != that.initialised) return false;
		return this.object.equals(that.object);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.object);
	}
	
	@Override
	public String toString() {
		if (!this.initialised) {
			return "null";
		}
		return Objects.requireNonNull(this.get()).toString();
	}
	//endregion
}
