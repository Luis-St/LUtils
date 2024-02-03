package net.luis.utils.util;

import net.luis.utils.exception.AlreadyInitializedException;
import net.luis.utils.exception.NotInitializedException;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 *
 * @author Luis-St
 *
 */

	
	private final MutableObject<T> object;
	private final Consumer<T> action;
	private boolean initialised;
	
	public LazyInitialization() {
		this(new MutableObject<>(), (v) -> {});
	}
	
	public LazyInitialization(@Nullable T value) {
		this(new MutableObject<>(value), (v) -> {});
	}
	
	public LazyInitialization(@NotNull Consumer<T> action) {
		this(new MutableObject<>(), action);
	}
	
	public LazyInitialization(@Nullable T value, @NotNull Consumer<T> action) {
		this(new MutableObject<>(value), action);
	}
	
	private LazyInitialization(@NotNull MutableObject<T> mutable, @NotNull Consumer<T> action) {
		this.object = Objects.requireNonNull(mutable, "Object must not be null");
		this.action = Objects.requireNonNull(action, "Action must not be null");
		this.initialised = this.object.getValue() != null;
	}
	
	public @Nullable T get() {
		if (this.initialised) {
			return this.object.getValue();
		} else {
			throw new NotInitializedException("The object has not been initialized yet");
		}
	}
	
	public void set(@Nullable T value) {
		if (this.initialised) {
			throw new AlreadyInitializedException("The object has already been initialized");
		} else {
			this.object.setValue(value);
			this.initialised = true;
			Utils.executeIfNotNull(this.action, (action) -> action.accept(value));
		}
	}
	
	public boolean isInstantiated() {
		return this.initialised;
	}
	
	//region Object overrides
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof LazyInitialization<?> that)) return false;
		
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
