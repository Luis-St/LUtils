package net.luis.utils.util;

import net.luis.utils.exception.AlreadyInitializedException;
import net.luis.utils.exception.NotInitializedException;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * A class representing a lazy initialization of an object.<br>
 *
 * @author Luis-St
 *
 * @param <T> The type of the object to initialize
 */
public class LazyInitialization<T> {
	
	/**
	 * The object to be initialized.<br>
	 */
	private final MutableObject<T> object;
	/**
	 * The action to perform when the object is initialized.<br>
	 */
	private final Consumer<T> action;
	/**
	 * Whether the object has been initialized or not.<br>
	 */
	private boolean initialised;
	
	/**
	 * Constructs a new {@link LazyInitialization} with no value and no initialization action.<br>
	 */
	public LazyInitialization() {
		this(new MutableObject<>(), (v) -> {});
	}
	
	/**
	 * Constructs an already initialized {@link LazyInitialization} with the given value.<br>
	 * @param value The value to initialize the object with
	 */
	public LazyInitialization(@Nullable T value) {
		this(new MutableObject<>(value), (v) -> {});
	}
	
	/**
	 * Constructs a new {@link LazyInitialization} with the given initialization action.<br>
	 * @param action The action to perform when the object is initialized
	 * @throws NullPointerException If the action is null
	 */
	public LazyInitialization(@NotNull Consumer<T> action) {
		this(new MutableObject<>(), action);
	}
	
	/**
	 * Constructs an already initialized {@link LazyInitialization} with the given value and initialization action.<br>
	 * @param value The value to initialize the object with
	 * @param action The action to perform when the object is initialized
	 * @throws NullPointerException If the action is null
	 */
	public LazyInitialization(@Nullable T value, @NotNull Consumer<T> action) {
		this(new MutableObject<>(value), action);
	}
	
	/**
	 * Constructs a new {@link LazyInitialization} with the given mutable object and initialization action.<br>
	 * @param mutable The internal mutable object to hold the value
	 * @param action The action to perform when the object is initialized
	 * @throws NullPointerException If the mutable or action is null
	 * @see #LazyInitialization(Object)
	 * @see #LazyInitialization(Consumer)
	 * @see #LazyInitialization(Object, Consumer)
	 */
	private LazyInitialization(@NotNull MutableObject<T> mutable, @NotNull Consumer<T> action) {
		this.object = Objects.requireNonNull(mutable, "Object must not be null");
		this.action = Objects.requireNonNull(action, "Action must not be null");
		this.initialised = this.object.getValue() != null;
	}
	
	/**
	 * Gets the value of the object if it has been initialized.<br>
	 * @return The value of the object
	 * @throws NotInitializedException If the object has not been initialized yet
	 */
	public @Nullable T get() {
		if (this.initialised) {
			return this.object.getValue();
		} else {
			throw new NotInitializedException("The object has not been initialized yet");
		}
	}
	
	/**
	 * Sets the value of the object if it has not been initialized yet.<br>
	 * Performs the action set in the constructor if the object is initialized.<br>
	 * @param value The value to initialize the object with
	 * @throws AlreadyInitializedException If the object has already been initialized
	 */
	public void set(@Nullable T value) {
		if (this.initialised) {
			throw new AlreadyInitializedException("The object has already been initialized");
		} else {
			this.object.setValue(value);
			this.initialised = true;
			Utils.executeIfNotNull(this.action, (action) -> action.accept(value));
		}
	}
	
	/**
	 * Checks whether the object has been initialized or not.<br>
	 * @return True if the object has been initialized, false otherwise
	 */
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
