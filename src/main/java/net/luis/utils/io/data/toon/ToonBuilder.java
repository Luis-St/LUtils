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
 * A fluent builder for constructing toon objects and arrays.<br>
 * Provides a chainable API for building toon structures programmatically.<br>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * ToonObject object = ToonBuilder.object()
 *     .add("title", "toon Example")
 *     .startObject("database")
 *         .add("host", "localhost")
 *         .add("port", 5432)
 *         .add("enabled", true)
 *     .endObject()
 *     .buildObject();
 *
 * ToonArray array = ToonBuilder.array()
 *     .add("first")
 *     .add(42)
 *     .add(true)
 *     .buildArray();
 * }</pre>
 *
 * @author Luis-St
 */
public final class ToonBuilder {
	
	/**
	 * Stack to track nested contexts for proper structure building.
	 */
	private final Deque<ContextFrame> contextStack = new ArrayDeque<>();
	/**
	 * The root element being built.
	 */
	private final ToonElement root;
	
	/**
	 * Private constructor to enforce factory method usage.<br>
	 *
	 * @param initialContext The initial context type
	 * @param initialElement The initial toon element
	 * @throws NullPointerException If the initial context or element is null
	 */
	private ToonBuilder(@NonNull BuilderContext initialContext, @NonNull ToonElement initialElement) {
		Objects.requireNonNull(initialContext, "Initial context must not be null");
		Objects.requireNonNull(initialElement, "Initial element must not be null");
		
		this.root = initialElement;
		this.contextStack.push(new ContextFrame(initialContext, initialElement));
	}
	
	/**
	 * Creates a new toon builder for building a toon object.<br>
	 * The builder will start in object context, allowing you to add key-value pairs.<br>
	 *
	 * @return A new toon builder configured for object construction
	 */
	public static @NonNull ToonBuilder object() {
		return new ToonBuilder(BuilderContext.OBJECT, new ToonObject());
	}
	
	/**
	 * Creates a new toon builder for building a toon array.<br>
	 * The builder will start in array context, allowing you to add elements.<br>
	 *
	 * @return A new toon builder configured for array construction
	 */
	public static @NonNull ToonBuilder array() {
		return new ToonBuilder(BuilderContext.ARRAY, new ToonArray());
	}
	
	/**
	 * Gets the current context frame from the stack.<br>
	 *
	 * @return The current context frame
	 * @throws IllegalStateException If the context stack is empty
	 */
	private @NonNull ContextFrame getCurrentContext() {
		if (this.contextStack.isEmpty()) {
			throw new IllegalStateException("No active building context");
		}
		return this.contextStack.peek();
	}
	
	/**
	 * Ensures the current context is an object context.<br>
	 * @throws IllegalStateException If the current context is not an object
	 */
	private void ensureObjectContext() {
		ContextFrame current = this.getCurrentContext();
		
		if (current.type != BuilderContext.OBJECT) {
			throw new IllegalStateException("Current context is not an object. Use add(value) for arrays.");
		}
	}
	
	/**
	 * Ensures the current context is an array context.<br>
	 * @throws IllegalStateException If the current context is not an array
	 */
	private void ensureArrayContext() {
		ContextFrame current = this.getCurrentContext();
		
		if (current.type != BuilderContext.ARRAY) {
			throw new IllegalStateException("Current context is not an array. Use add(key, value) for objects.");
		}
	}
	
	/**
	 * Adds a property with a toon element value to the current object.<br>
	 *
	 * @param key The property key
	 * @param element The toon element value (null becomes toon null)
	 * @return This builder for chaining
	 * @throws NullPointerException If the key is null
	 * @throws IllegalStateException If the current context is not an object
	 */
	public @NonNull ToonBuilder add(@NonNull String key, @Nullable ToonElement element) {
		this.ensureObjectContext();
		Objects.requireNonNull(key, "Property key must not be null");
		
		ToonObject object = (ToonObject) this.getCurrentContext().element;
		object.add(key, element);
		return this;
	}
	
	/**
	 * Adds a property with a string value to the current object.<br>
	 *
	 * @param key The property key
	 * @param value The string value (null becomes toon null)
	 * @return This builder for chaining
	 * @throws NullPointerException If the key is null
	 * @throws IllegalStateException If the current context is not an object
	 */
	public @NonNull ToonBuilder add(@NonNull String key, @Nullable String value) {
		this.ensureObjectContext();
		Objects.requireNonNull(key, "Property key must not be null");
		
		ToonObject object = (ToonObject) this.getCurrentContext().element;
		object.add(key, value);
		return this;
	}
	
	/**
	 * Adds a property with a boolean value to the current object.<br>
	 *
	 * @param key The property key
	 * @param value The boolean value
	 * @return This builder for chaining
	 * @throws NullPointerException If the key is null
	 * @throws IllegalStateException If the current context is not an object
	 */
	public @NonNull ToonBuilder add(@NonNull String key, boolean value) {
		this.ensureObjectContext();
		Objects.requireNonNull(key, "Property key must not be null");
		
		ToonObject object = (ToonObject) this.getCurrentContext().element;
		object.add(key, value);
		return this;
	}
	
	/**
	 * Adds a property with a number value to the current object.<br>
	 *
	 * @param key The property key
	 * @param value The number value (null becomes toon null)
	 * @return This builder for chaining
	 * @throws NullPointerException If the key is null
	 * @throws IllegalStateException If the current context is not an object
	 */
	public @NonNull ToonBuilder add(@NonNull String key, @Nullable Number value) {
		this.ensureObjectContext();
		Objects.requireNonNull(key, "Property key must not be null");
		
		ToonObject object = (ToonObject) this.getCurrentContext().element;
		object.add(key, value);
		return this;
	}
	
	/**
	 * Conditionally adds a property with a string value to the current object.<br>
	 *
	 * @param condition The condition to check
	 * @param key The property key
	 * @param value The string value
	 * @return This builder for chaining
	 * @throws NullPointerException If the key is null
	 * @throws IllegalStateException If the current context is not an object
	 */
	public @NonNull ToonBuilder addIf(boolean condition, @NonNull String key, @Nullable String value) {
		if (condition) {
			this.add(key, value);
		}
		return this;
	}
	
	/**
	 * Conditionally adds a property with a boolean value to the current object.<br>
	 *
	 * @param condition The condition to check
	 * @param key The property key
	 * @param value The boolean value
	 * @return This builder for chaining
	 * @throws NullPointerException If the key is null
	 * @throws IllegalStateException If the current context is not an object
	 */
	public @NonNull ToonBuilder addIf(boolean condition, @NonNull String key, boolean value) {
		if (condition) {
			this.add(key, value);
		}
		return this;
	}
	
	/**
	 * Conditionally adds a property with a number value to the current object.<br>
	 *
	 * @param condition The condition to check
	 * @param key The property key
	 * @param value The number value
	 * @return This builder for chaining
	 * @throws NullPointerException If the key is null
	 * @throws IllegalStateException If the current context is not an object
	 */
	public @NonNull ToonBuilder addIf(boolean condition, @NonNull String key, @Nullable Number value) {
		if (condition) {
			this.add(key, value);
		}
		return this;
	}
	
	/**
	 * Adds a toon element to the current array.<br>
	 * If the element is null, it will be converted to toon null.<br>
	 *
	 * @param element The toon element to add
	 * @return This builder for method chaining
	 * @throws IllegalStateException If the current context is not an array
	 */
	public @NonNull ToonBuilder add(@Nullable ToonElement element) {
		this.ensureArrayContext();
		
		ToonArray array = (ToonArray) this.getCurrentContext().element;
		array.add(element);
		return this;
	}
	
	/**
	 * Adds a string value to the current array.<br>
	 * If the value is null, it will be converted to toon null.<br>
	 *
	 * @param value The string value to add
	 * @return This builder for method chaining
	 * @throws IllegalStateException If the current context is not an array
	 */
	public @NonNull ToonBuilder add(@Nullable String value) {
		this.ensureArrayContext();
		
		ToonArray array = (ToonArray) this.getCurrentContext().element;
		array.add(value);
		return this;
	}
	
	/**
	 * Adds a boolean value to the current array.<br>
	 *
	 * @param value The boolean value to add
	 * @return This builder for method chaining
	 * @throws IllegalStateException If the current context is not an array
	 */
	public @NonNull ToonBuilder add(boolean value) {
		this.ensureArrayContext();
		
		ToonArray array = (ToonArray) this.getCurrentContext().element;
		array.add(value);
		return this;
	}
	
	/**
	 * Adds a number value to the current array.<br>
	 * If the value is null, it will be converted to toon null.<br>
	 *
	 * @param value The number value to add
	 * @return This builder for method chaining
	 * @throws IllegalStateException If the current context is not an array
	 */
	public @NonNull ToonBuilder add(@Nullable Number value) {
		this.ensureArrayContext();
		
		ToonArray array = (ToonArray) this.getCurrentContext().element;
		array.add(value);
		return this;
	}
	
	/**
	 * Adds multiple string values to the current array at once.<br>
	 *
	 * @param values The values to add to the array
	 * @return This builder for method chaining
	 * @throws IllegalStateException If the current context is not an array
	 * @throws NullPointerException If the values array is null
	 */
	public @NonNull ToonBuilder addAll(@NonNull String... values) {
		Objects.requireNonNull(values, "Values array must not be null");
		
		for (String value : values) {
			this.add(value);
		}
		return this;
	}
	
	/**
	 * Adds multiple number values to the current array at once.<br>
	 *
	 * @param values The number values to add to the array
	 * @return This builder for method chaining
	 * @throws IllegalStateException If the current context is not an array
	 * @throws NullPointerException If the values array is null
	 */
	public @NonNull ToonBuilder addAll(@NonNull Number... values) {
		Objects.requireNonNull(values, "Values array must not be null");
		
		for (Number value : values) {
			this.add(value);
		}
		return this;
	}
	
	/**
	 * Adds multiple boolean values to the current array at once.<br>
	 *
	 * @param values The boolean values to add to the array
	 * @return This builder for method chaining
	 * @throws IllegalStateException If the current context is not an array
	 * @throws NullPointerException If the values array is null
	 */
	public @NonNull ToonBuilder addAll(boolean... values) {
		Objects.requireNonNull(values, "Values array must not be null");
		
		for (boolean value : values) {
			this.add(value);
		}
		return this;
	}
	
	/**
	 * Conditionally adds a string value to the current array only if the condition is true.<br>
	 *
	 * @param condition The condition to evaluate
	 * @param value The value to add if condition is true
	 * @return This builder for method chaining
	 * @throws IllegalStateException If the current context is not an array
	 */
	public @NonNull ToonBuilder addIf(boolean condition, @Nullable String value) {
		if (condition) {
			this.add(value);
		}
		return this;
	}
	
	/**
	 * Conditionally adds a boolean value to the current array only if the condition is true.<br>
	 *
	 * @param condition The condition to evaluate
	 * @param value The boolean value to add if condition is true
	 * @return This builder for method chaining
	 * @throws IllegalStateException If the current context is not an array
	 */
	public @NonNull ToonBuilder addIf(boolean condition, boolean value) {
		if (condition) {
			this.add(value);
		}
		return this;
	}
	
	/**
	 * Conditionally adds a number value to the current array only if the condition is true.<br>
	 *
	 * @param condition The condition to evaluate
	 * @param value The number value to add if condition is true
	 * @return This builder for method chaining
	 * @throws IllegalStateException If the current context is not an array
	 */
	public @NonNull ToonBuilder addIf(boolean condition, @Nullable Number value) {
		if (condition) {
			this.add(value);
		}
		return this;
	}
	//endregion
	
	//region Nesting methods
	
	/**
	 * Starts building a nested object with the specified key in the current object.<br>
	 * Use {@link #endObject()} to return to the parent context.<br>
	 *
	 * @param key The key for the nested object
	 * @return This builder for method chaining, now in object context
	 * @throws IllegalStateException If the current context is not an object
	 * @throws NullPointerException If the key is null
	 */
	public @NonNull ToonBuilder startObject(@NonNull String key) {
		this.ensureObjectContext();
		Objects.requireNonNull(key, "Key must not be null");
		
		ToonObject nestedObject = new ToonObject();
		ToonObject currentObject = (ToonObject) this.getCurrentContext().element;
		currentObject.add(key, nestedObject);
		
		this.contextStack.push(new ContextFrame(BuilderContext.OBJECT, nestedObject));
		return this;
	}
	
	/**
	 * Starts building a nested object in the current array.<br>
	 * Use {@link #endObject()} to return to the parent context.<br>
	 *
	 * @return This builder for method chaining, now in object context
	 * @throws IllegalStateException If the current context is not an array
	 */
	public @NonNull ToonBuilder startObject() {
		this.ensureArrayContext();
		
		ToonObject nestedObject = new ToonObject();
		ToonArray currentArray = (ToonArray) this.getCurrentContext().element;
		currentArray.add(nestedObject);
		
		this.contextStack.push(new ContextFrame(BuilderContext.OBJECT, nestedObject));
		return this;
	}
	
	/**
	 * Ends the current object building context and returns to the parent context.<br>
	 *
	 * @return This builder for method chaining, now in the parent context
	 * @throws IllegalStateException If the current context is not an object or if no parent context exists
	 */
	public @NonNull ToonBuilder endObject() {
		ContextFrame current = this.getCurrentContext();
		if (current.type != BuilderContext.OBJECT) {
			throw new IllegalStateException("Current context is not an object");
		}
		
		if (this.contextStack.size() == 1) {
			throw new IllegalStateException("Cannot end root object context. Use build() instead.");
		}
		
		this.contextStack.pop();
		return this;
	}
	
	/**
	 * Starts building a nested array with the specified key in the current object.<br>
	 * Use {@link #endArray()} to return to the parent context.<br>
	 *
	 * @param key The key for the nested array
	 * @return This builder for method chaining, now in array context
	 * @throws IllegalStateException If the current context is not an object
	 * @throws NullPointerException If the key is null
	 */
	public @NonNull ToonBuilder startArray(@NonNull String key) {
		this.ensureObjectContext();
		Objects.requireNonNull(key, "Key must not be null");
		
		ToonArray nestedArray = new ToonArray();
		ToonObject currentObject = (ToonObject) this.getCurrentContext().element;
		currentObject.add(key, nestedArray);
		
		this.contextStack.push(new ContextFrame(BuilderContext.ARRAY, nestedArray));
		return this;
	}
	
	/**
	 * Starts building a nested array in the current array.<br>
	 * Use {@link #endArray()} to return to the parent context.<br>
	 *
	 * @return This builder for method chaining, now in array context
	 * @throws IllegalStateException If the current context is not an array
	 */
	public @NonNull ToonBuilder startArray() {
		this.ensureArrayContext();
		
		ToonArray nestedArray = new ToonArray();
		ToonArray currentArray = (ToonArray) this.getCurrentContext().element;
		currentArray.add(nestedArray);
		
		this.contextStack.push(new ContextFrame(BuilderContext.ARRAY, nestedArray));
		return this;
	}
	
	/**
	 * Ends the current array building context and returns to the parent context.<br>
	 *
	 * @return This builder for method chaining, now in the parent context
	 * @throws IllegalStateException If the current context is not an array or if no parent context exists
	 */
	public @NonNull ToonBuilder endArray() {
		ContextFrame current = this.getCurrentContext();
		if (current.type != BuilderContext.ARRAY) {
			throw new IllegalStateException("Current context is not an array");
		}
		
		if (this.contextStack.size() == 1) {
			throw new IllegalStateException("Cannot end root array context. Use build() instead.");
		}
		
		this.contextStack.pop();
		return this;
	}
	//endregion
	
	//region Build methods
	
	/**
	 * Builds and returns the constructed toon element.<br>
	 *
	 * @return The constructed toon element
	 * @throws IllegalStateException If there are unclosed nested contexts
	 */
	public @NonNull ToonElement build() {
		if (this.contextStack.size() != 1) {
			throw new IllegalStateException("Cannot build with unclosed nested contexts. Current nesting depth: " + (this.contextStack.size() - 1));
		}
		return this.root;
	}
	
	/**
	 * Builds and returns the constructed toon element as a toon object.<br>
	 * This is a convenience method that casts the result to toon object.<br>
	 *
	 * @return The constructed toon object
	 * @throws IllegalStateException If there are unclosed nested contexts or if the root is not an object
	 */
	public @NonNull ToonObject buildObject() {
		ToonElement element = this.build();
		if (!(element instanceof ToonObject object)) {
			throw new IllegalStateException("Root element is not a toon object");
		}
		
		ToonObject result = new ToonObject();
		result.addAll(object);
		return result;
	}
	
	/**
	 * Builds and returns the constructed toon element as a toon array.<br>
	 * This is a convenience method that casts the result to toon array.<br>
	 *
	 * @return The constructed toon array
	 * @throws IllegalStateException If there are unclosed nested contexts or if the root is not an array
	 */
	public @NonNull ToonArray buildArray() {
		ToonElement element = this.build();
		if (!(element instanceof ToonArray array)) {
			throw new IllegalStateException("Root element is not a toon array");
		}
		
		ToonArray result = new ToonArray();
		result.addAll(array);
		return result;
	}
	//endregion
	
	//region State query methods
	
	/**
	 * Returns the current nesting depth of the builder.<br>
	 * A depth of 0 means we're at the root level, 1 means one level nested, etc.<br>
	 *
	 * @return The current nesting depth
	 */
	public int getNestingDepth() {
		return this.contextStack.size() - 1;
	}
	
	/**
	 * Checks if the builder is currently in an object context.<br>
	 * @return True if the current context is an object, false otherwise
	 */
	public boolean isInObjectContext() {
		return !this.contextStack.isEmpty() && this.getCurrentContext().type == BuilderContext.OBJECT;
	}
	
	/**
	 * Checks if the builder is currently in an array context.<br>
	 * @return True if the current context is an array, false otherwise
	 */
	public boolean isInArrayContext() {
		return !this.contextStack.isEmpty() && this.getCurrentContext().type == BuilderContext.ARRAY;
	}
	
	/**
	 * Checks if the builder is at the root level (no nested contexts).<br>
	 * @return True if at root level, false if there are nested contexts
	 */
	public boolean isAtRootLevel() {
		return this.contextStack.size() == 1;
	}
	//endregion
	
	//region Object overrides
	
	@Override
	public String toString() {
		return this.root.toString();
	}
	
	/**
	 * Returns a string representation of the element being built.<br>
	 *
	 * @param config The toon config to use for formatting
	 * @return The string representation
	 */
	public @NonNull String toString(@NonNull ToonConfig config) {
		return this.root.toString(config);
	}
	//endregion
	
	/**
	 * Enumeration of builder contexts to track whether we're building an object or array.<br>
	 *
	 * @author Luis-St
	 */
	private enum BuilderContext {
		/**
		 * Context for building a toon object.<br>
		 */
		OBJECT,
		/**
		 * Context for building a toon array.<br>
		 */
		ARRAY
	}
	
	/**
	 * Internal class to track nesting state.<br>
	 *
	 * @author Luis-St
	 *
	 * @param type The type of context (object or array)
	 * @param element The toon element associated with this context
	 */
	private record ContextFrame(@NonNull BuilderContext type, @NonNull ToonElement element) {
		
		/**
		 * Constructs a new context frame with the specified type and element.<br>
		 *
		 * @param type The type of context (object or array)
		 * @param element The toon element associated with this context
		 * @throws NullPointerException If the type or element is null
		 */
		private ContextFrame {
			Objects.requireNonNull(type, "Context type must not be null");
			Objects.requireNonNull(element, "Context element must not be null");
		}
	}
}
