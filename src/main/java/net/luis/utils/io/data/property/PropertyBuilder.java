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

package net.luis.utils.io.data.property;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.*;

/**
 * A fluent builder for constructing complex property structures.<br>
 * This builder provides a convenient way to create nested property objects and arrays
 * using a chainable API that maintains proper structure and type safety.<br>
 * <p>
 *     The builder supports creating both property objects and arrays, with automatic
 *     context tracking to ensure operations are valid for the current structure type.
 *     It provides type-specific methods for adding common data types and supports
 *     arbitrary nesting depth.
 * </p>
 *
 * <p>
 *     Usage Examples:
 * </p>
 * <pre>{@code
 * // Simple object
 * PropertyObject config = PropertyBuilder.object()
 *     .add("app.name", "MyApp")
 *     .add("app.port", 8080)
 *     .add("app.debug", true)
 *     .buildObject();
 *
 * // Complex nested structure
 * PropertyObject complex = PropertyBuilder.object()
 *     .add("database.host", "localhost")
 *     .add("database.port", 5432)
 *     .startArray("database.pools")
 *         .add("pool1")
 *         .add("pool2")
 *     .endArray()
 *     .buildObject();
 * }</pre>
 *
 * @author Luis-St
 */
public final class PropertyBuilder {
	
	/**
	 * Stack to track nested contexts for proper structure building.
	 */
	private final Deque<ContextFrame> contextStack = new ArrayDeque<>();
	/**
	 * The root element being built.
	 */
	private final PropertyElement root;
	
	/**
	 * Private constructor to enforce factory method usage.
	 *
	 * @param initialContext The initial context type
	 * @param initialElement The initial property element
	 */
	private PropertyBuilder(@NonNull BuilderContext initialContext, @NonNull PropertyElement initialElement) {
		this.root = initialElement;
		this.contextStack.push(new ContextFrame(initialContext, initialElement));
	}
	
	/**
	 * Creates a new builder for constructing a property object.<br>
	 * The builder will start in object context, allowing you to add key-value pairs.
	 *
	 * @return A new PropertyBuilder configured for object construction
	 */
	public static @NonNull PropertyBuilder object() {
		return new PropertyBuilder(BuilderContext.OBJECT, new PropertyObject());
	}
	
	/**
	 * Creates a new builder for constructing a property array.<br>
	 * The builder will start in array context, allowing you to add elements.
	 *
	 * @return A new PropertyBuilder configured for array construction
	 */
	public static @NonNull PropertyBuilder array() {
		return new PropertyBuilder(BuilderContext.ARRAY, new PropertyArray());
	}
	
	/**
	 * Gets the current context frame from the stack.
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
	 * Ensures the current context is an object context.
	 *
	 * @throws IllegalStateException If the current context is not an object
	 */
	private void ensureObjectContext() {
		ContextFrame current = this.getCurrentContext();
		if (current.type != BuilderContext.OBJECT) {
			throw new IllegalStateException("Current context is not an object. Use add(value) for arrays.");
		}
	}
	
	/**
	 * Ensures the current context is an array context.
	 *
	 * @throws IllegalStateException If the current context is not an array
	 */
	private void ensureArrayContext() {
		ContextFrame current = this.getCurrentContext();
		if (current.type != BuilderContext.ARRAY) {
			throw new IllegalStateException("Current context is not an array. Use add(key, value) for objects.");
		}
	}
	
	/**
	 * Adds a property element with the specified key to the current object.<br>
	 * If the element is null, it will be converted to PropertyNull.
	 *
	 * @param key The key for the element
	 * @param element The property element to add
	 * @return This builder for method chaining
	 * @throws IllegalStateException If the current context is not an object
	 * @throws NullPointerException If the key is null
	 */
	public @NonNull PropertyBuilder add(@NonNull String key, @Nullable PropertyElement element) {
		this.ensureObjectContext();
		Objects.requireNonNull(key, "Key must not be null");
		
		PropertyObject object = (PropertyObject) this.getCurrentContext().element;
		object.add(key, element);
		return this;
	}
	
	/**
	 * Adds a string value with the specified key to the current object.<br>
	 * If the value is null, it will be converted to PropertyNull.
	 *
	 * @param key The key for the value
	 * @param value The string value to add
	 * @return This builder for method chaining
	 * @throws IllegalStateException If the current context is not an object
	 * @throws NullPointerException If the key is null
	 */
	public @NonNull PropertyBuilder add(@NonNull String key, @Nullable String value) {
		return this.add(key, value == null ? null : new PropertyValue(value));
	}
	
	/**
	 * Adds a boolean value with the specified key to the current object.
	 *
	 * @param key The key for the value
	 * @param value The boolean value to add
	 * @return This builder for method chaining
	 * @throws IllegalStateException If the current context is not an object
	 * @throws NullPointerException If the key is null
	 */
	public @NonNull PropertyBuilder add(@NonNull String key, boolean value) {
		return this.add(key, new PropertyValue(value));
	}
	
	/**
	 * Adds a number value with the specified key to the current object.<br>
	 * If the value is null, it will be converted to PropertyNull.
	 *
	 * @param key The key for the value
	 * @param value The number value to add
	 * @return This builder for method chaining
	 * @throws IllegalStateException If the current context is not an object
	 * @throws NullPointerException If the key is null
	 */
	public @NonNull PropertyBuilder add(@NonNull String key, @Nullable Number value) {
		return this.add(key, value == null ? null : new PropertyValue(value));
	}
	
	/**
	 * Adds a byte value with the specified key to the current object.
	 *
	 * @param key The key for the value
	 * @param value The byte value to add
	 * @return This builder for method chaining
	 * @throws IllegalStateException If the current context is not an object
	 * @throws NullPointerException If the key is null
	 */
	public @NonNull PropertyBuilder add(@NonNull String key, byte value) {
		return this.add(key, new PropertyValue(value));
	}
	
	/**
	 * Adds a short value with the specified key to the current object.
	 *
	 * @param key The key for the value
	 * @param value The short value to add
	 * @return This builder for method chaining
	 * @throws IllegalStateException If the current context is not an object
	 * @throws NullPointerException If the key is null
	 */
	public @NonNull PropertyBuilder add(@NonNull String key, short value) {
		return this.add(key, new PropertyValue(value));
	}
	
	/**
	 * Adds an int value with the specified key to the current object.
	 *
	 * @param key The key for the value
	 * @param value The int value to add
	 * @return This builder for method chaining
	 * @throws IllegalStateException If the current context is not an object
	 * @throws NullPointerException If the key is null
	 */
	public @NonNull PropertyBuilder add(@NonNull String key, int value) {
		return this.add(key, new PropertyValue(value));
	}
	
	/**
	 * Adds a long value with the specified key to the current object.
	 *
	 * @param key The key for the value
	 * @param value The long value to add
	 * @return This builder for method chaining
	 * @throws IllegalStateException If the current context is not an object
	 * @throws NullPointerException If the key is null
	 */
	public @NonNull PropertyBuilder add(@NonNull String key, long value) {
		return this.add(key, new PropertyValue(value));
	}
	
	/**
	 * Adds a float value with the specified key to the current object.
	 *
	 * @param key The key for the value
	 * @param value The float value to add
	 * @return This builder for method chaining
	 * @throws IllegalStateException If the current context is not an object
	 * @throws NullPointerException If the key is null
	 */
	public @NonNull PropertyBuilder add(@NonNull String key, float value) {
		return this.add(key, new PropertyValue(value));
	}
	
	/**
	 * Adds a double value with the specified key to the current object.
	 *
	 * @param key The key for the value
	 * @param value The double value to add
	 * @return This builder for method chaining
	 * @throws IllegalStateException If the current context is not an object
	 * @throws NullPointerException If the key is null
	 */
	public @NonNull PropertyBuilder add(@NonNull String key, double value) {
		return this.add(key, new PropertyValue(value));
	}
	
	/**
	 * Adds a nested property object with the specified key to the current object.<br>
	 * This method allows you to add pre-built PropertyBuilder results as nested objects.
	 *
	 * @param key The key for the nested object
	 * @param objectBuilder The builder containing the object to nest
	 * @return This builder for method chaining
	 * @throws IllegalStateException If the current context is not an object
	 * @throws NullPointerException If the key or objectBuilder is null
	 */
	public @NonNull PropertyBuilder addObject(@NonNull String key, @NonNull PropertyBuilder objectBuilder) {
		Objects.requireNonNull(objectBuilder, "Object builder must not be null");
		return this.add(key, objectBuilder.build());
	}
	
	/**
	 * Adds a nested property array with the specified key to the current object.<br>
	 * This method allows you to add pre-built PropertyBuilder results as nested arrays.
	 *
	 * @param key The key for the nested array
	 * @param arrayBuilder The builder containing the array to nest
	 * @return This builder for method chaining
	 * @throws IllegalStateException If the current context is not an object
	 * @throws NullPointerException If the key or arrayBuilder is null
	 */
	public @NonNull PropertyBuilder addArray(@NonNull String key, @NonNull PropertyBuilder arrayBuilder) {
		Objects.requireNonNull(arrayBuilder, "Array builder must not be null");
		return this.add(key, arrayBuilder.build());
	}
	
	/**
	 * Adds a property element to the current array.<br>
	 * If the element is null, it will be converted to PropertyNull.
	 *
	 * @param element The property element to add
	 * @return This builder for method chaining
	 * @throws IllegalStateException If the current context is not an array
	 */
	public @NonNull PropertyBuilder add(@Nullable PropertyElement element) {
		this.ensureArrayContext();
		
		PropertyArray array = (PropertyArray) this.getCurrentContext().element;
		array.add(element);
		return this;
	}
	
	/**
	 * Adds a string value to the current array.<br>
	 * If the value is null, it will be converted to PropertyNull.
	 *
	 * @param value The string value to add
	 * @return This builder for method chaining
	 * @throws IllegalStateException If the current context is not an array
	 */
	public @NonNull PropertyBuilder add(@Nullable String value) {
		return this.add(value == null ? null : new PropertyValue(value));
	}
	
	/**
	 * Adds a boolean value to the current array.
	 *
	 * @param value The boolean value to add
	 * @return This builder for method chaining
	 * @throws IllegalStateException If the current context is not an array
	 */
	public @NonNull PropertyBuilder add(boolean value) {
		return this.add(new PropertyValue(value));
	}
	
	/**
	 * Adds a number value to the current array.<br>
	 * If the value is null, it will be converted to PropertyNull.
	 *
	 * @param value The number value to add
	 * @return This builder for method chaining
	 * @throws IllegalStateException If the current context is not an array
	 */
	public @NonNull PropertyBuilder add(@Nullable Number value) {
		return this.add(value == null ? null : new PropertyValue(value));
	}
	
	/**
	 * Adds a byte value to the current array.
	 *
	 * @param value The byte value to add
	 * @return This builder for method chaining
	 * @throws IllegalStateException If the current context is not an array
	 */
	public @NonNull PropertyBuilder add(byte value) {
		return this.add(new PropertyValue(value));
	}
	
	/**
	 * Adds a short value to the current array.
	 *
	 * @param value The short value to add
	 * @return This builder for method chaining
	 * @throws IllegalStateException If the current context is not an array
	 */
	public @NonNull PropertyBuilder add(short value) {
		return this.add(new PropertyValue(value));
	}
	
	/**
	 * Adds an int value to the current array.
	 *
	 * @param value The int value to add
	 * @return This builder for method chaining
	 * @throws IllegalStateException If the current context is not an array
	 */
	public @NonNull PropertyBuilder add(int value) {
		return this.add(new PropertyValue(value));
	}
	
	/**
	 * Adds a long value to the current array.
	 *
	 * @param value The long value to add
	 * @return This builder for method chaining
	 * @throws IllegalStateException If the current context is not an array
	 */
	public @NonNull PropertyBuilder add(long value) {
		return this.add(new PropertyValue(value));
	}
	
	/**
	 * Adds a float value to the current array.
	 *
	 * @param value The float value to add
	 * @return This builder for method chaining
	 * @throws IllegalStateException If the current context is not an array
	 */
	public @NonNull PropertyBuilder add(float value) {
		return this.add(new PropertyValue(value));
	}
	
	/**
	 * Adds a double value to the current array.
	 *
	 * @param value The double value to add
	 * @return This builder for method chaining
	 * @throws IllegalStateException If the current context is not an array
	 */
	public @NonNull PropertyBuilder add(double value) {
		return this.add(new PropertyValue(value));
	}
	
	/**
	 * Adds a nested property object to the current array.<br>
	 * This method allows you to add pre-built PropertyBuilder results as nested objects.
	 *
	 * @param objectBuilder The builder containing the object to add
	 * @return This builder for method chaining
	 * @throws IllegalStateException If the current context is not an array
	 * @throws NullPointerException If the objectBuilder is null
	 */
	public @NonNull PropertyBuilder addObject(@NonNull PropertyBuilder objectBuilder) {
		Objects.requireNonNull(objectBuilder, "Object builder must not be null");
		return this.add(objectBuilder.build());
	}
	
	/**
	 * Adds a nested property array to the current array.<br>
	 * This method allows you to add pre-built PropertyBuilder results as nested arrays.
	 *
	 * @param arrayBuilder The builder containing the array to add
	 * @return This builder for method chaining
	 * @throws IllegalStateException If the current context is not an array
	 * @throws NullPointerException If the arrayBuilder is null
	 */
	public @NonNull PropertyBuilder addArray(@NonNull PropertyBuilder arrayBuilder) {
		Objects.requireNonNull(arrayBuilder, "Array builder must not be null");
		return this.add(arrayBuilder.build());
	}
	
	/**
	 * Adds multiple string values to the current array at once.<br>
	 * This is a convenience method for adding several elements in one call.
	 *
	 * @param values The values to add to the array
	 * @return This builder for method chaining
	 * @throws IllegalStateException If the current context is not an array
	 * @throws NullPointerException If the values array is null
	 */
	public @NonNull PropertyBuilder addAll(@NonNull String... values) {
		Objects.requireNonNull(values, "Values array must not be null");
		
		for (String value : values) {
			this.add(value);
		}
		return this;
	}
	
	/**
	 * Adds multiple number values to the current array at once.<br>
	 * This is a convenience method for adding several numeric elements in one call.
	 *
	 * @param values The number values to add to the array
	 * @return This builder for method chaining
	 * @throws IllegalStateException If the current context is not an array
	 * @throws NullPointerException If the values array is null
	 */
	public @NonNull PropertyBuilder addAll(@NonNull Number... values) {
		Objects.requireNonNull(values, "Values array must not be null");
		
		for (Number value : values) {
			this.add(value);
		}
		return this;
	}
	
	/**
	 * Adds multiple boolean values to the current array at once.<br>
	 * This is a convenience method for adding several boolean elements in one call.
	 *
	 * @param values The boolean values to add to the array
	 * @return This builder for method chaining
	 * @throws IllegalStateException If the current context is not an array
	 * @throws NullPointerException If the values array is null
	 */
	public @NonNull PropertyBuilder addAll(boolean... values) {
		Objects.requireNonNull(values, "Values array must not be null");
		
		for (boolean value : values) {
			this.add(value);
		}
		return this;
	}
	
	/**
	 * Starts building a nested object with the specified key in the current object.<br>
	 * This method pushes a new object context onto the stack, allowing you to build
	 * a nested object structure. Use {@link #endObject()} to return to the parent context.
	 *
	 * @param key The key for the nested object
	 * @return This builder for method chaining, now in object context
	 * @throws IllegalStateException If the current context is not an object
	 * @throws NullPointerException If the key is null
	 */
	public @NonNull PropertyBuilder startObject(@NonNull String key) {
		this.ensureObjectContext();
		Objects.requireNonNull(key, "Key must not be null");
		
		PropertyObject nestedObject = new PropertyObject();
		PropertyObject currentObject = (PropertyObject) this.getCurrentContext().element;
		currentObject.add(key, nestedObject);
		
		this.contextStack.push(new ContextFrame(BuilderContext.OBJECT, nestedObject));
		return this;
	}
	
	/**
	 * Starts building a nested object in the current array.<br>
	 * This method pushes a new object context onto the stack, allowing you to build
	 * a nested object structure. Use {@link #endObject()} to return to the parent context.
	 *
	 * @return This builder for method chaining, now in object context
	 * @throws IllegalStateException If the current context is not an array
	 */
	public @NonNull PropertyBuilder startObject() {
		this.ensureArrayContext();
		
		PropertyObject nestedObject = new PropertyObject();
		PropertyArray currentArray = (PropertyArray) this.getCurrentContext().element;
		currentArray.add(nestedObject);
		
		this.contextStack.push(new ContextFrame(BuilderContext.OBJECT, nestedObject));
		return this;
	}
	
	/**
	 * Ends the current object building context and returns to the parent context.<br>
	 * This method pops the current context from the stack, allowing you to continue
	 * building the parent structure.
	 *
	 * @return This builder for method chaining, now in the parent context
	 * @throws IllegalStateException If the current context is not an object or if no parent context exists
	 */
	public @NonNull PropertyBuilder endObject() {
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
	 * This method pushes a new array context onto the stack, allowing you to build
	 * a nested array structure. Use {@link #endArray()} to return to the parent context.
	 *
	 * @param key The key for the nested array
	 * @return This builder for method chaining, now in array context
	 * @throws IllegalStateException If the current context is not an object
	 * @throws NullPointerException If the key is null
	 */
	public @NonNull PropertyBuilder startArray(@NonNull String key) {
		this.ensureObjectContext();
		Objects.requireNonNull(key, "Key must not be null");
		
		PropertyArray nestedArray = new PropertyArray();
		PropertyObject currentObject = (PropertyObject) this.getCurrentContext().element;
		currentObject.add(key, nestedArray);
		
		this.contextStack.push(new ContextFrame(BuilderContext.ARRAY, nestedArray));
		return this;
	}
	
	/**
	 * Starts building a nested array in the current array.<br>
	 * This method pushes a new array context onto the stack, allowing you to build
	 * a nested array structure. Use {@link #endArray()} to return to the parent context.
	 *
	 * @return This builder for method chaining, now in array context
	 * @throws IllegalStateException If the current context is not an array
	 */
	public @NonNull PropertyBuilder startArray() {
		this.ensureArrayContext();
		
		PropertyArray nestedArray = new PropertyArray();
		PropertyArray currentArray = (PropertyArray) this.getCurrentContext().element;
		currentArray.add(nestedArray);
		
		this.contextStack.push(new ContextFrame(BuilderContext.ARRAY, nestedArray));
		return this;
	}
	
	/**
	 * Ends the current array building context and returns to the parent context.<br>
	 * This method pops the current context from the stack, allowing you to continue
	 * building the parent structure.
	 *
	 * @return This builder for method chaining, now in the parent context
	 * @throws IllegalStateException If the current context is not an array or if no parent context exists
	 */
	public @NonNull PropertyBuilder endArray() {
		ContextFrame current = this.getCurrentContext();
		if (current.type != BuilderContext.ARRAY) {
			throw new IllegalStateException("Current context is not an array");
		}
		
		this.contextStack.pop();
		if (this.contextStack.isEmpty()) {
			throw new IllegalStateException("Cannot end root array context. Use build() instead.");
		}
		return this;
	}
	
	/**
	 * Conditionally adds a key-value pair to the current object only if the condition is true.<br>
	 * This is useful for building objects with optional fields based on runtime conditions.
	 *
	 * @param condition The condition to evaluate
	 * @param key The key for the value
	 * @param value The value to add if condition is true
	 * @return This builder for method chaining
	 * @throws IllegalStateException If the current context is not an object
	 * @throws NullPointerException If the key is null and condition is true
	 */
	public @NonNull PropertyBuilder addIf(boolean condition, @NonNull String key, @Nullable String value) {
		if (condition) {
			this.add(key, value);
		}
		return this;
	}
	
	/**
	 * Conditionally adds a key-value pair to the current object only if the condition is true.<br>
	 * This is useful for building objects with optional fields based on runtime conditions.
	 *
	 * @param condition The condition to evaluate
	 * @param key The key for the value
	 * @param value The value to add if condition is true
	 * @return This builder for method chaining
	 * @throws IllegalStateException If the current context is not an object
	 * @throws NullPointerException If the key is null and condition is true
	 */
	public @NonNull PropertyBuilder addIf(boolean condition, @NonNull String key, boolean value) {
		if (condition) {
			this.add(key, value);
		}
		return this;
	}
	
	/**
	 * Conditionally adds a key-value pair to the current object only if the condition is true.<br>
	 * This is useful for building objects with optional fields based on runtime conditions.
	 *
	 * @param condition The condition to evaluate
	 * @param key The key for the value
	 * @param value The value to add if condition is true
	 * @return This builder for method chaining
	 * @throws IllegalStateException If the current context is not an object
	 * @throws NullPointerException If the key is null and condition is true
	 */
	public @NonNull PropertyBuilder addIf(boolean condition, @NonNull String key, @Nullable Number value) {
		if (condition) {
			this.add(key, value);
		}
		return this;
	}
	
	/**
	 * Conditionally adds a value to the current array only if the condition is true.<br>
	 * This is useful for building arrays with optional elements based on runtime conditions.
	 *
	 * @param condition The condition to evaluate
	 * @param value The value to add if condition is true
	 * @return This builder for method chaining
	 * @throws IllegalStateException If the current context is not an array
	 */
	public @NonNull PropertyBuilder addIf(boolean condition, @Nullable String value) {
		if (condition) {
			this.add(value);
		}
		return this;
	}
	
	/**
	 * Builds and returns the constructed property element.<br>
	 * This method should be called when you've finished building your property structure.
	 * The builder can still be used after calling this method.
	 *
	 * @return The constructed property element
	 * @throws IllegalStateException If there are unclosed nested contexts
	 */
	public @NonNull PropertyElement build() {
		if (this.contextStack.size() != 1) {
			throw new IllegalStateException("Cannot build with unclosed nested contexts. Current nesting depth: " + (this.contextStack.size() - 1));
		}
		return this.root;
	}
	
	/**
	 * Builds and returns the constructed property element as a PropertyObject.<br>
	 * This is a convenience method that casts the result to PropertyObject.
	 *
	 * @return The constructed property object
	 * @throws IllegalStateException If there are unclosed nested contexts or if the root is not an object
	 */
	public @NonNull PropertyObject buildObject() {
		PropertyElement element = this.build();
		if (!(element instanceof PropertyObject object)) {
			throw new IllegalStateException("Root element is not a PropertyObject");
		}
		
		PropertyObject result = new PropertyObject();
		result.addAll(object);
		return result;
	}
	
	/**
	 * Builds and returns the constructed property element as a PropertyArray.<br>
	 * This is a convenience method that casts the result to PropertyArray.
	 *
	 * @return The constructed property array
	 * @throws IllegalStateException If there are unclosed nested contexts or if the root is not an array
	 */
	public @NonNull PropertyArray buildArray() {
		PropertyElement element = this.build();
		if (!(element instanceof PropertyArray array)) {
			throw new IllegalStateException("Root element is not a PropertyArray");
		}
		
		PropertyArray result = new PropertyArray();
		result.addAll(array);
		return result;
	}
	
	/**
	 * Returns the current nesting depth of the builder.<br>
	 * A depth of 1 means we're at the root level, 2 means one level nested, etc.
	 *
	 * @return The current nesting depth
	 */
	public int getNestingDepth() {
		return this.contextStack.size();
	}
	
	/**
	 * Checks if the builder is currently in an object context.
	 *
	 * @return True if the current context is an object, false otherwise
	 */
	public boolean isInObjectContext() {
		return !this.contextStack.isEmpty() && this.getCurrentContext().type == BuilderContext.OBJECT;
	}
	
	/**
	 * Checks if the builder is currently in an array context.
	 *
	 * @return True if the current context is an array, false otherwise
	 */
	public boolean isInArrayContext() {
		return !this.contextStack.isEmpty() && this.getCurrentContext().type == BuilderContext.ARRAY;
	}
	
	/**
	 * Checks if the builder is at the root level (no nested contexts).
	 *
	 * @return True if at root level, false if there are nested contexts
	 */
	public boolean isAtRootLevel() {
		return this.contextStack.size() == 1;
	}
	
	/**
	 * Returns a string representation of the current property structure being built.<br>
	 * This method uses the default property configuration for formatting.
	 *
	 * @return A string representation of the current property structure
	 */
	@Override
	public String toString() {
		return this.root.toString();
	}
	
	/**
	 * Returns a string representation of the current property structure using the specified configuration.
	 *
	 * @param config The property configuration to use for formatting
	 * @return A string representation of the current property structure
	 * @throws NullPointerException If the config is null
	 */
	public @NonNull String toString(@NonNull PropertyConfig config) {
		Objects.requireNonNull(config, "Config must not be null");
		return this.root.toString(config);
	}
	
	/**
	 * Enumeration of builder contexts to track whether we're building an object or array.
	 */
	private enum BuilderContext {
		/**
		 * Context for building a property object.<br>
		 */
		OBJECT,
		/**
		 * Context for building a property array.<br>
		 */
		ARRAY
	}
	
	/**
	 * Internal class to track nesting state.<br>
	 *
	 * @param type The type of context (object or array)
	 * @param element The property element associated with this context
	 */
	private record ContextFrame(@NonNull BuilderContext type, @NonNull PropertyElement element) {
		
		/**
		 * Constructs a new context frame with the specified type and element.<br>
		 *
		 * @param type The type of context (object or array)
		 * @param element The property element associated with this context
		 * @throws NullPointerException If the type or element is null
		 */
		private ContextFrame {
			Objects.requireNonNull(type, "Context type must not be null");
			Objects.requireNonNull(element, "Context element must not be null");
		}
	}
}
