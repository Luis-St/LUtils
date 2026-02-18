/*
 * LUtils
 * Copyright (C) 2026 Luis Staudt
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

package net.luis.utils.io.data.toon;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.*;

/**
 * A fluent builder for constructing toon objects.<br>
 * Provides a chainable API for building toon structures programmatically.<br>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * ToonObject object = ToonBuilder.table()
 *     .add("title", "toon Example")
 *     .startObject("database")
 *         .add("host", "localhost")
 *         .add("port", 5432)
 *         .add("enabled", true)
 *     .endObject()
 *     .build();
 * }</pre>
 *
 * @author Luis-St
 */
public final class ToonBuilder {
	
	/**
	 * The context stack for tracking nested objects.<br>
	 */
	private final Deque<BuilderContext> contextStack = new ArrayDeque<>();
	
	/**
	 * The root object being built.<br>
	 */
	private final ToonObject rootObject;
	
	/**
	 * Constructs a new toon builder.<br>
	 */
	private ToonBuilder() {
		this.rootObject = new ToonObject();
	}
	
	/**
	 * Creates a new toon builder for building a toon object.<br>
	 * @return A new toon builder
	 */
	public static @NonNull ToonBuilder table() {
		return new ToonBuilder();
	}
	
	/**
	 * Creates a new array builder for building a toon array.<br>
	 * @return A new array builder
	 */
	public static @NonNull ArrayBuilder array() {
		return new ArrayBuilder();
	}
	
	/**
	 * Returns the current object being built.<br>
	 * @return The current object
	 */
	private @NonNull ToonObject getCurrentObject() {
		if (this.contextStack.isEmpty()) {
			return this.rootObject;
		}
		return this.contextStack.peek().object();
	}
	
	/**
	 * Adds a property with a string value.<br>
	 *
	 * @param key The property key
	 * @param value The string value (null becomes toon null)
	 * @return This builder for chaining
	 * @throws NullPointerException If the key is null
	 */
	public @NonNull ToonBuilder add(@NonNull String key, @Nullable String value) {
		Objects.requireNonNull(key, "Property key must not be null");
		
		this.getCurrentObject().add(key, value);
		return this;
	}
	
	/**
	 * Adds a property with a boolean value.<br>
	 *
	 * @param key The property key
	 * @param value The boolean value
	 * @return This builder for chaining
	 * @throws NullPointerException If the key is null
	 */
	public @NonNull ToonBuilder add(@NonNull String key, boolean value) {
		Objects.requireNonNull(key, "Property key must not be null");
		
		this.getCurrentObject().add(key, value);
		return this;
	}
	
	/**
	 * Adds a property with a number value.<br>
	 *
	 * @param key The property key
	 * @param value The number value (null becomes toon null)
	 * @return This builder for chaining
	 * @throws NullPointerException If the key is null
	 */
	public @NonNull ToonBuilder add(@NonNull String key, @Nullable Number value) {
		Objects.requireNonNull(key, "Property key must not be null");
		
		this.getCurrentObject().add(key, value);
		return this;
	}
	
	/**
	 * Adds a property with a toon element value.<br>
	 *
	 * @param key The property key
	 * @param element The toon element value (null becomes toon null)
	 * @return This builder for chaining
	 * @throws NullPointerException If the key is null
	 */
	public @NonNull ToonBuilder add(@NonNull String key, @Nullable ToonElement element) {
		Objects.requireNonNull(key, "Property key must not be null");
		
		this.getCurrentObject().add(key, element);
		return this;
	}
	
	/**
	 * Conditionally adds a property with a string value.<br>
	 *
	 * @param condition The condition to check
	 * @param key The property key
	 * @param value The string value
	 * @return This builder for chaining
	 * @throws NullPointerException If the key is null
	 */
	public @NonNull ToonBuilder addIf(boolean condition, @NonNull String key, @Nullable String value) {
		if (condition) {
			this.add(key, value);
		}
		return this;
	}
	
	/**
	 * Conditionally adds a property with a boolean value.<br>
	 *
	 * @param condition The condition to check
	 * @param key The property key
	 * @param value The boolean value
	 * @return This builder for chaining
	 * @throws NullPointerException If the key is null
	 */
	public @NonNull ToonBuilder addIf(boolean condition, @NonNull String key, boolean value) {
		if (condition) {
			this.add(key, value);
		}
		return this;
	}
	
	/**
	 * Conditionally adds a property with a number value.<br>
	 *
	 * @param condition The condition to check
	 * @param key The property key
	 * @param value The number value
	 * @return This builder for chaining
	 * @throws NullPointerException If the key is null
	 */
	public @NonNull ToonBuilder addIf(boolean condition, @NonNull String key, @Nullable Number value) {
		if (condition) {
			this.add(key, value);
		}
		return this;
	}
	
	/**
	 * Starts a new nested object with the given name.<br>
	 * All subsequent add() calls will add properties to this object.<br>
	 *
	 * @param name The object name
	 * @return This builder for chaining
	 * @throws NullPointerException If the name is null
	 * @throws IllegalArgumentException If the name is blank
	 */
	public @NonNull ToonBuilder startObject(@NonNull String name) {
		Objects.requireNonNull(name, "Object name must not be null");
		if (name.isBlank()) {
			throw new IllegalArgumentException("Object name must not be blank");
		}
		
		ToonObject parentObject = this.getCurrentObject();
		ToonElement existing = parentObject.get(name);
		ToonObject newObject;
		
		if (existing instanceof ToonObject object) {
			newObject = object;
		} else {
			newObject = new ToonObject();
			parentObject.add(name, newObject);
		}
		
		this.contextStack.push(new BuilderContext(name, newObject));
		return this;
	}
	
	/**
	 * Ends the current nested object and returns to the parent object.<br>
	 *
	 * @return This builder for chaining
	 * @throws IllegalStateException If not currently inside a nested object
	 */
	public @NonNull ToonBuilder endObject() {
		if (this.contextStack.isEmpty()) {
			throw new IllegalStateException("Not inside a nested object");
		}
		
		this.contextStack.pop();
		return this;
	}
	
	/**
	 * Returns the current nesting depth.<br>
	 *
	 * @return The nesting depth (0 = root level)
	 */
	public int getNestingDepth() {
		return this.contextStack.size();
	}
	
	/**
	 * Checks if currently inside a nested object.<br>
	 *
	 * @return True if inside a nested object, false otherwise
	 */
	public boolean isNested() {
		return !this.contextStack.isEmpty();
	}
	
	/**
	 * Checks if at the root level (not inside any nested object).<br>
	 *
	 * @return True if at root level, false otherwise
	 */
	public boolean isAtRoot() {
		return this.contextStack.isEmpty();
	}
	
	/**
	 * Builds and returns the toon object.<br>
	 * Automatically closes any open nested objects.<br>
	 *
	 * @return The built toon object
	 */
	public @NonNull ToonObject build() {
		this.contextStack.clear();
		return this.rootObject;
	}
	
	@Override
	public String toString() {
		return this.toString(ToonConfig.DEFAULT);
	}
	
	/**
	 * Returns a string representation of the object being built.<br>
	 *
	 * @param config The toon config to use for formatting
	 * @return The string representation
	 */
	public @NonNull String toString(@NonNull ToonConfig config) {
		return this.rootObject.toString(config);
	}
	
	/**
	 * Represents the current builder context.<br>
	 *
	 * @author Luis-St
	 *
	 * @param name The object name
	 * @param object The object being built
	 */
	private record BuilderContext(@NonNull String name, @NonNull ToonObject object) {
		
		/**
		 * Constructs a new builder context.<br>
		 *
		 * @param name The object name
		 * @param object The object being built
		 * @throws NullPointerException If the name or object are null
		 */
		private BuilderContext {
			Objects.requireNonNull(name, "Object name must not be null");
			Objects.requireNonNull(object, "Object must not be null");
		}
	}
	
	//region Array builder
	
	/**
	 * A builder for constructing toon arrays.<br>
	 *
	 * @author Luis-St
	 */
	public static final class ArrayBuilder {
		
		/**
		 * The array being built.<br>
		 */
		private final ToonArray array = new ToonArray();
		
		/**
		 * Constructs a new array builder.<br>
		 */
		private ArrayBuilder() {}
		
		/**
		 * Adds a string value to the array.<br>
		 *
		 * @param value The string value (null becomes toon null)
		 * @return This builder for chaining
		 */
		public @NonNull ArrayBuilder add(@Nullable String value) {
			this.array.add(value);
			return this;
		}
		
		/**
		 * Adds a boolean value to the array.<br>
		 *
		 * @param value The boolean value
		 * @return This builder for chaining
		 */
		public @NonNull ArrayBuilder add(boolean value) {
			this.array.add(value);
			return this;
		}
		
		/**
		 * Adds a number value to the array.<br>
		 *
		 * @param value The number value (null becomes toon null)
		 * @return This builder for chaining
		 */
		public @NonNull ArrayBuilder add(@Nullable Number value) {
			this.array.add(value);
			return this;
		}
		
		/**
		 * Adds a toon element to the array.<br>
		 *
		 * @param element The element (null becomes toon null)
		 * @return This builder for chaining
		 */
		public @NonNull ArrayBuilder add(@Nullable ToonElement element) {
			this.array.add(element);
			return this;
		}
		
		/**
		 * Adds all elements from another array.<br>
		 *
		 * @param other The array to add elements from
		 * @return This builder for chaining
		 * @throws NullPointerException If the other array is null
		 */
		public @NonNull ArrayBuilder addAll(@NonNull ToonArray other) {
			this.array.addAll(other);
			return this;
		}
		
		/**
		 * Conditionally adds a value.<br>
		 *
		 * @param condition The condition to check
		 * @param value The value to add
		 * @return This builder for chaining
		 */
		public @NonNull ArrayBuilder addIf(boolean condition, @Nullable String value) {
			if (condition) {
				this.add(value);
			}
			return this;
		}
		
		/**
		 * Builds and returns the toon array.<br>
		 *
		 * @return The built toon array
		 */
		public @NonNull ToonArray build() {
			return this.array;
		}
	}
	//endregion
}
