/*
 * LUtils
 * Copyright (C) 2025 Luis Staudt
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

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
	 * Constructs a new lazy initialization with no value and no initialization action.<br>
	 */
	public LazyInitialization() {
		this(new MutableObject<>(), (v) -> {}, false);
	}
	
	/**
	 * Constructs an already initialized lazy initialization with the given value.<br>
	 * @param value The value to initialize the object with
	 */
	public LazyInitialization(@Nullable T value) {
		this(new MutableObject<>(value), (v) -> {}, true);
	}
	
	/**
	 * Constructs a new lazy initialization with the given initialization action.<br>
	 *
	 * @param action The action to perform when the object is initialized
	 * @throws NullPointerException If the action is null
	 */
	public LazyInitialization(@NotNull Consumer<T> action) {
		this(new MutableObject<>(), action, false);
	}
	
	/**
	 * Constructs an already initialized lazy initialization with the given value and initialization action.<br>
	 *
	 * @param value The value to initialize the object with
	 * @param action The action to perform when the object is initialized
	 * @throws NullPointerException If the action is null
	 */
	public LazyInitialization(@Nullable T value, @NotNull Consumer<T> action) {
		this(new MutableObject<>(value), action, true);
	}
	
	/**
	 * Constructs a new lazy initialization with the given mutable object and initialization action.<br>
	 *
	 * @param mutable The internal mutable object to hold the value
	 * @param action The action to perform when the object is initialized
	 * @param initialised Whether the object has been initialized or not
	 * @throws NullPointerException If the mutable or action is null
	 * @see #LazyInitialization(Object)
	 * @see #LazyInitialization(Consumer)
	 * @see #LazyInitialization(Object, Consumer)
	 */
	private LazyInitialization(@NotNull MutableObject<T> mutable, @NotNull Consumer<T> action, boolean initialised) {
		this.object = Objects.requireNonNull(mutable, "Object must not be null");
		this.action = Objects.requireNonNull(action, "Action must not be null");
		this.initialised = initialised;
	}
	
	/**
	 * Gets the value of the object if it has been initialized.<br>
	 *
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
	 *
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
			return "unknown";
		}
		return String.valueOf(this.get());
	}
	//endregion
}
