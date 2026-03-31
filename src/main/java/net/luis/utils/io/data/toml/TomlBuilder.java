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

package net.luis.utils.io.data.toml;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.*;
import java.util.*;

/**
 * A fluent builder for constructing toml tables and arrays.<br>
 * Provides a chainable API for building toml structures programmatically.<br>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * TomlTable table = TomlBuilder.table()
 *     .add("title", "toml Example")
 *     .startTable("database")
 *         .add("host", "localhost")
 *         .add("port", 5432)
 *         .add("enabled", true)
 *     .endTable()
 *     .startArray("ports")
 *         .add(8080)
 *         .add(8443)
 *     .endArray()
 *     .buildTable();
 *
 * TomlArray array = TomlBuilder.array()
 *     .add("first")
 *     .add(42)
 *     .add(true)
 *     .buildArray();
 * }</pre>
 *
 * @author Luis-St
 */
public final class TomlBuilder {
	
	/**
	 * Stack to track nested contexts for proper structure building.<br>
	 */
	private final Deque<ContextFrame> contextStack = new ArrayDeque<>();
	/**
	 * The root element being built.<br>
	 */
	private final TomlElement root;
	
	/**
	 * Private constructor to enforce factory method usage.<br>
	 *
	 * @param initialContext The initial context type
	 * @param initialElement The initial toml element
	 * @throws NullPointerException If the initial context or element is null
	 */
	private TomlBuilder(@NonNull BuilderContext initialContext, @NonNull TomlElement initialElement) {
		Objects.requireNonNull(initialContext, "Initial context must not be null");
		Objects.requireNonNull(initialElement, "Initial element must not be null");
		
		this.root = initialElement;
		this.contextStack.push(new ContextFrame(initialContext, initialElement));
	}
	
	/**
	 * Creates a new toml builder for building a toml table.<br>
	 * The builder will start in table context, allowing you to add key-value pairs.<br>
	 *
	 * @return A new toml builder configured for table construction
	 */
	public static @NonNull TomlBuilder table() {
		return new TomlBuilder(BuilderContext.TABLE, new TomlTable());
	}
	
	/**
	 * Creates a new toml builder for building a toml array.<br>
	 * The builder will start in array context, allowing you to add elements.<br>
	 *
	 * @return A new toml builder configured for array construction
	 */
	public static @NonNull TomlBuilder array() {
		return new TomlBuilder(BuilderContext.ARRAY, new TomlArray());
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
	 * Ensures the current context is a table context.<br>
	 * @throws IllegalStateException If the current context is not a table
	 */
	private void ensureTableContext() {
		ContextFrame current = this.getCurrentContext();
		
		if (current.type != BuilderContext.TABLE) {
			throw new IllegalStateException("Current context is not a table. Use add(value) for arrays.");
		}
	}
	
	/**
	 * Ensures the current context is an array context.<br>
	 * @throws IllegalStateException If the current context is not an array
	 */
	private void ensureArrayContext() {
		ContextFrame current = this.getCurrentContext();
		
		if (current.type != BuilderContext.ARRAY) {
			throw new IllegalStateException("Current context is not an array. Use add(key, value) for tables.");
		}
	}
	
	/**
	 * Adds a property with a string value to the current table.<br>
	 *
	 * @param key The property key
	 * @param value The string value (null becomes toml null)
	 * @return This builder for chaining
	 * @throws NullPointerException If the key is null
	 * @throws IllegalStateException If the current context is not a table
	 */
	public @NonNull TomlBuilder add(@NonNull String key, @Nullable String value) {
		this.ensureTableContext();
		Objects.requireNonNull(key, "Property key must not be null");
		
		TomlTable table = (TomlTable) this.getCurrentContext().element;
		table.add(key, value);
		return this;
	}
	
	/**
	 * Adds a property with a boolean value to the current table.<br>
	 *
	 * @param key The property key
	 * @param value The boolean value
	 * @return This builder for chaining
	 * @throws NullPointerException If the key is null
	 * @throws IllegalStateException If the current context is not a table
	 */
	public @NonNull TomlBuilder add(@NonNull String key, boolean value) {
		this.ensureTableContext();
		Objects.requireNonNull(key, "Property key must not be null");
		
		TomlTable table = (TomlTable) this.getCurrentContext().element;
		table.add(key, value);
		return this;
	}
	
	/**
	 * Adds a property with a number value to the current table.<br>
	 *
	 * @param key The property key
	 * @param value The number value (null becomes toml null)
	 * @return This builder for chaining
	 * @throws NullPointerException If the key is null
	 * @throws IllegalStateException If the current context is not a table
	 */
	public @NonNull TomlBuilder add(@NonNull String key, @Nullable Number value) {
		this.ensureTableContext();
		Objects.requireNonNull(key, "Property key must not be null");
		
		TomlTable table = (TomlTable) this.getCurrentContext().element;
		table.add(key, value);
		return this;
	}
	
	/**
	 * Adds a property with a local date value to the current table.<br>
	 *
	 * @param key The property key
	 * @param value The local date value (null becomes toml null)
	 * @return This builder for chaining
	 * @throws NullPointerException If the key is null
	 * @throws IllegalStateException If the current context is not a table
	 */
	public @NonNull TomlBuilder add(@NonNull String key, @Nullable LocalDate value) {
		this.ensureTableContext();
		Objects.requireNonNull(key, "Property key must not be null");
		
		TomlTable table = (TomlTable) this.getCurrentContext().element;
		table.add(key, value);
		return this;
	}
	
	/**
	 * Adds a property with a local time value to the current table.<br>
	 *
	 * @param key The property key
	 * @param value The local time value (null becomes toml null)
	 * @return This builder for chaining
	 * @throws NullPointerException If the key is null
	 * @throws IllegalStateException If the current context is not a table
	 */
	public @NonNull TomlBuilder add(@NonNull String key, @Nullable LocalTime value) {
		this.ensureTableContext();
		Objects.requireNonNull(key, "Property key must not be null");
		
		TomlTable table = (TomlTable) this.getCurrentContext().element;
		table.add(key, value);
		return this;
	}
	
	/**
	 * Adds a property with a local date-time value to the current table.<br>
	 *
	 * @param key The property key
	 * @param value The local date-time value (null becomes toml null)
	 * @return This builder for chaining
	 * @throws NullPointerException If the key is null
	 * @throws IllegalStateException If the current context is not a table
	 */
	public @NonNull TomlBuilder add(@NonNull String key, @Nullable LocalDateTime value) {
		this.ensureTableContext();
		Objects.requireNonNull(key, "Property key must not be null");
		
		TomlTable table = (TomlTable) this.getCurrentContext().element;
		table.add(key, value);
		return this;
	}
	
	/**
	 * Adds a property with an offset date-time value to the current table.<br>
	 *
	 * @param key The property key
	 * @param value The offset date-time value (null becomes toml null)
	 * @return This builder for chaining
	 * @throws NullPointerException If the key is null
	 * @throws IllegalStateException If the current context is not a table
	 */
	public @NonNull TomlBuilder add(@NonNull String key, @Nullable OffsetDateTime value) {
		this.ensureTableContext();
		Objects.requireNonNull(key, "Property key must not be null");
		
		TomlTable table = (TomlTable) this.getCurrentContext().element;
		table.add(key, value);
		return this;
	}
	
	/**
	 * Adds a property with a toml element value to the current table.<br>
	 *
	 * @param key The property key
	 * @param element The toml element value (null becomes toml null)
	 * @return This builder for chaining
	 * @throws NullPointerException If the key is null
	 * @throws IllegalStateException If the current context is not a table
	 */
	public @NonNull TomlBuilder add(@NonNull String key, @Nullable TomlElement element) {
		this.ensureTableContext();
		Objects.requireNonNull(key, "Property key must not be null");
		
		TomlTable table = (TomlTable) this.getCurrentContext().element;
		table.add(key, element);
		return this;
	}
	
	/**
	 * Conditionally adds a property with a string value to the current table.<br>
	 *
	 * @param condition The condition to check
	 * @param key The property key
	 * @param value The string value
	 * @return This builder for chaining
	 * @throws NullPointerException If the key is null
	 * @throws IllegalStateException If the current context is not a table
	 */
	public @NonNull TomlBuilder addIf(boolean condition, @NonNull String key, @Nullable String value) {
		if (condition) {
			this.add(key, value);
		}
		return this;
	}
	
	/**
	 * Conditionally adds a property with a boolean value to the current table.<br>
	 *
	 * @param condition The condition to check
	 * @param key The property key
	 * @param value The boolean value
	 * @return This builder for chaining
	 * @throws NullPointerException If the key is null
	 * @throws IllegalStateException If the current context is not a table
	 */
	public @NonNull TomlBuilder addIf(boolean condition, @NonNull String key, boolean value) {
		if (condition) {
			this.add(key, value);
		}
		return this;
	}
	
	/**
	 * Conditionally adds a property with a number value to the current table.<br>
	 *
	 * @param condition The condition to check
	 * @param key The property key
	 * @param value The number value
	 * @return This builder for chaining
	 * @throws NullPointerException If the key is null
	 * @throws IllegalStateException If the current context is not a table
	 */
	public @NonNull TomlBuilder addIf(boolean condition, @NonNull String key, @Nullable Number value) {
		if (condition) {
			this.add(key, value);
		}
		return this;
	}
	
	/**
	 * Adds an array of tables.<br>
	 * Each table in the array will be formatted with [[array.name]] notation.<br>
	 *
	 * @param name The array name
	 * @param tables The tables to add
	 * @return This builder for chaining
	 * @throws NullPointerException If the name or tables are null
	 * @throws IllegalStateException If the current context is not a table
	 */
	public @NonNull TomlBuilder addArrayOfTables(@NonNull String name, TomlTable @NonNull ... tables) {
		Objects.requireNonNull(name, "Array name must not be null");
		Objects.requireNonNull(tables, "Table array must not be null");
		
		return this.addArrayOfTables(name, Arrays.asList(tables));
	}
	
	/**
	 * Adds an array of tables from a list.<br>
	 *
	 * @param name The array name
	 * @param tables The tables to add
	 * @return This builder for chaining
	 * @throws NullPointerException If the name or tables are null
	 * @throws IllegalStateException If the current context is not a table
	 */
	public @NonNull TomlBuilder addArrayOfTables(@NonNull String name, @NonNull List<TomlTable> tables) {
		this.ensureTableContext();
		Objects.requireNonNull(name, "Array name must not be null");
		Objects.requireNonNull(tables, "Table list must not be null");
		
		TomlArray array = new TomlArray();
		array.setArrayOfTables(true);
		for (TomlTable table : tables) {
			array.add(table);
		}
		
		TomlTable currentTable = (TomlTable) this.getCurrentContext().element;
		currentTable.add(name, array);
		return this;
	}
	
	/**
	 * Adds a toml element to the current array.<br>
	 * If the element is null, it will be converted to toml null.<br>
	 *
	 * @param element The toml element to add
	 * @return This builder for method chaining
	 * @throws IllegalStateException If the current context is not an array
	 */
	public @NonNull TomlBuilder add(@Nullable TomlElement element) {
		this.ensureArrayContext();
		
		TomlArray array = (TomlArray) this.getCurrentContext().element;
		array.add(element);
		return this;
	}
	
	/**
	 * Adds a string value to the current array.<br>
	 * If the value is null, it will be converted to toml null.<br>
	 *
	 * @param value The string value to add
	 * @return This builder for method chaining
	 * @throws IllegalStateException If the current context is not an array
	 */
	public @NonNull TomlBuilder add(@Nullable String value) {
		this.ensureArrayContext();
		
		TomlArray array = (TomlArray) this.getCurrentContext().element;
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
	public @NonNull TomlBuilder add(boolean value) {
		this.ensureArrayContext();
		
		TomlArray array = (TomlArray) this.getCurrentContext().element;
		array.add(value);
		return this;
	}
	
	/**
	 * Adds a number value to the current array.<br>
	 * If the value is null, it will be converted to toml null.<br>
	 *
	 * @param value The number value to add
	 * @return This builder for method chaining
	 * @throws IllegalStateException If the current context is not an array
	 */
	public @NonNull TomlBuilder add(@Nullable Number value) {
		this.ensureArrayContext();
		
		TomlArray array = (TomlArray) this.getCurrentContext().element;
		array.add(value);
		return this;
	}
	
	/**
	 * Adds a local date value to the current array.<br>
	 * If the value is null, it will be converted to toml null.<br>
	 *
	 * @param value The local date value to add
	 * @return This builder for method chaining
	 * @throws IllegalStateException If the current context is not an array
	 */
	public @NonNull TomlBuilder add(@Nullable LocalDate value) {
		this.ensureArrayContext();
		
		TomlArray array = (TomlArray) this.getCurrentContext().element;
		array.add(value);
		return this;
	}
	
	/**
	 * Adds a local time value to the current array.<br>
	 * If the value is null, it will be converted to toml null.<br>
	 *
	 * @param value The local time value to add
	 * @return This builder for method chaining
	 * @throws IllegalStateException If the current context is not an array
	 */
	public @NonNull TomlBuilder add(@Nullable LocalTime value) {
		this.ensureArrayContext();
		
		TomlArray array = (TomlArray) this.getCurrentContext().element;
		array.add(value);
		return this;
	}
	
	/**
	 * Adds a local date-time value to the current array.<br>
	 * If the value is null, it will be converted to toml null.<br>
	 *
	 * @param value The local date-time value to add
	 * @return This builder for method chaining
	 * @throws IllegalStateException If the current context is not an array
	 */
	public @NonNull TomlBuilder add(@Nullable LocalDateTime value) {
		this.ensureArrayContext();
		
		TomlArray array = (TomlArray) this.getCurrentContext().element;
		array.add(value);
		return this;
	}
	
	/**
	 * Adds an offset date-time value to the current array.<br>
	 * If the value is null, it will be converted to toml null.<br>
	 *
	 * @param value The offset date-time value to add
	 * @return This builder for method chaining
	 * @throws IllegalStateException If the current context is not an array
	 */
	public @NonNull TomlBuilder add(@Nullable OffsetDateTime value) {
		this.ensureArrayContext();
		
		TomlArray array = (TomlArray) this.getCurrentContext().element;
		array.add(value);
		return this;
	}
	
	/**
	 * Adds all elements from another array to the current array.<br>
	 *
	 * @param other The array to add elements from
	 * @return This builder for chaining
	 * @throws NullPointerException If the other array is null
	 * @throws IllegalStateException If the current context is not an array
	 */
	public @NonNull TomlBuilder addAll(@NonNull TomlArray other) {
		this.ensureArrayContext();
		Objects.requireNonNull(other, "Array must not be null");
		
		TomlArray array = (TomlArray) this.getCurrentContext().element;
		array.addAll(other);
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
	public @NonNull TomlBuilder addAll(@NonNull String... values) {
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
	public @NonNull TomlBuilder addAll(@NonNull Number... values) {
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
	public @NonNull TomlBuilder addAll(boolean... values) {
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
	public @NonNull TomlBuilder addIf(boolean condition, @Nullable String value) {
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
	public @NonNull TomlBuilder addIf(boolean condition, boolean value) {
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
	public @NonNull TomlBuilder addIf(boolean condition, @Nullable Number value) {
		if (condition) {
			this.add(value);
		}
		return this;
	}
	
	/**
	 * Marks the current array as an array of tables.<br>
	 *
	 * @return This builder for chaining
	 * @throws IllegalStateException If the current context is not an array
	 */
	public @NonNull TomlBuilder asArrayOfTables() {
		this.ensureArrayContext();
		
		TomlArray array = (TomlArray) this.getCurrentContext().element;
		array.setArrayOfTables(true);
		return this;
	}
	
	/**
	 * Starts a new nested table with the given name in the current table.<br>
	 * All subsequent add() calls will add properties to this table.<br>
	 *
	 * @param name The table name
	 * @return This builder for chaining
	 * @throws NullPointerException If the name is null
	 * @throws IllegalArgumentException If the name is blank
	 * @throws IllegalStateException If the current context is not a table
	 */
	public @NonNull TomlBuilder startTable(@NonNull String name) {
		this.ensureTableContext();
		Objects.requireNonNull(name, "Table name must not be null");
		if (name.isBlank()) {
			throw new IllegalArgumentException("Table name must not be blank");
		}
		
		TomlTable parentTable = (TomlTable) this.getCurrentContext().element;
		TomlElement existing = parentTable.get(name);
		TomlTable newTable;
		
		if (existing instanceof TomlTable table) {
			newTable = table;
		} else {
			newTable = new TomlTable();
			parentTable.add(name, newTable);
		}
		
		this.contextStack.push(new ContextFrame(BuilderContext.TABLE, newTable));
		return this;
	}
	
	/**
	 * Starts building an anonymous table in the current array.<br>
	 * Use {@link #endTable()} to return to the parent context.<br>
	 *
	 * @return This builder for chaining, now in table context
	 * @throws IllegalStateException If the current context is not an array
	 */
	public @NonNull TomlBuilder startTable() {
		this.ensureArrayContext();
		
		TomlTable newTable = new TomlTable();
		TomlArray currentArray = (TomlArray) this.getCurrentContext().element;
		currentArray.add(newTable);
		
		this.contextStack.push(new ContextFrame(BuilderContext.TABLE, newTable));
		return this;
	}
	
	/**
	 * Ends the current nested table and returns to the parent context.<br>
	 *
	 * @return This builder for chaining
	 * @throws IllegalStateException If the current context is not a table or if at root level
	 */
	public @NonNull TomlBuilder endTable() {
		ContextFrame current = this.getCurrentContext();
		if (current.type != BuilderContext.TABLE) {
			throw new IllegalStateException("Current context is not a table");
		}
		
		if (this.contextStack.size() == 1) {
			throw new IllegalStateException("Cannot end root table context. Use build() instead.");
		}
		
		this.contextStack.pop();
		return this;
	}
	
	/**
	 * Starts an inline table with the given name in the current table.<br>
	 * Inline tables are formatted on a single line: {key = "value"}.<br>
	 *
	 * @param name The table name
	 * @return This builder for chaining
	 * @throws NullPointerException If the name is null
	 * @throws IllegalArgumentException If the name is blank
	 * @throws IllegalStateException If the current context is not a table
	 */
	public @NonNull TomlBuilder startInlineTable(@NonNull String name) {
		this.ensureTableContext();
		Objects.requireNonNull(name, "Table name must not be null");
		if (name.isBlank()) {
			throw new IllegalArgumentException("Table name must not be blank");
		}
		
		TomlTable inlineTable = new TomlTable();
		inlineTable.setInline(true);
		
		TomlTable parentTable = (TomlTable) this.getCurrentContext().element;
		parentTable.add(name, inlineTable);
		this.contextStack.push(new ContextFrame(BuilderContext.TABLE, inlineTable));
		return this;
	}
	
	/**
	 * Ends the current inline table and returns to the parent context.<br>
	 *
	 * @return This builder for chaining
	 * @throws IllegalStateException If the current context is not a table or if at root level
	 */
	public @NonNull TomlBuilder endInlineTable() {
		return this.endTable();
	}
	
	/**
	 * Starts a new named array in the current table.<br>
	 * Use {@link #endArray()} to return to the parent context.<br>
	 *
	 * @param name The array name
	 * @return This builder for chaining, now in array context
	 * @throws NullPointerException If the name is null
	 * @throws IllegalArgumentException If the name is blank
	 * @throws IllegalStateException If the current context is not a table
	 */
	public @NonNull TomlBuilder startArray(@NonNull String name) {
		this.ensureTableContext();
		Objects.requireNonNull(name, "Array name must not be null");
		if (name.isBlank()) {
			throw new IllegalArgumentException("Array name must not be blank");
		}
		
		TomlArray nestedArray = new TomlArray();
		TomlTable parentTable = (TomlTable) this.getCurrentContext().element;
		parentTable.add(name, nestedArray);
		
		this.contextStack.push(new ContextFrame(BuilderContext.ARRAY, nestedArray));
		return this;
	}
	
	/**
	 * Starts a nested array in the current array.<br>
	 * Use {@link #endArray()} to return to the parent context.<br>
	 *
	 * @return This builder for chaining, now in array context
	 * @throws IllegalStateException If the current context is not an array
	 */
	public @NonNull TomlBuilder startArray() {
		this.ensureArrayContext();
		
		TomlArray nestedArray = new TomlArray();
		TomlArray currentArray = (TomlArray) this.getCurrentContext().element;
		currentArray.add(nestedArray);
		
		this.contextStack.push(new ContextFrame(BuilderContext.ARRAY, nestedArray));
		return this;
	}
	
	/**
	 * Ends the current array context and returns to the parent context.<br>
	 *
	 * @return This builder for chaining
	 * @throws IllegalStateException If the current context is not an array or if at root level
	 */
	public @NonNull TomlBuilder endArray() {
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
	
	/**
	 * Starts a new named array of tables in the current table.<br>
	 * The array will be marked as an array of tables for [[name]] notation.<br>
	 * Use {@link #startTable()} to add tables to the array, then {@link #endArrayOfTables()} when done.<br>
	 *
	 * @param name The array name
	 * @return This builder for chaining, now in array context
	 * @throws NullPointerException If the name is null
	 * @throws IllegalArgumentException If the name is blank
	 * @throws IllegalStateException If the current context is not a table
	 */
	public @NonNull TomlBuilder startArrayOfTables(@NonNull String name) {
		this.ensureTableContext();
		Objects.requireNonNull(name, "Array name must not be null");
		if (name.isBlank()) {
			throw new IllegalArgumentException("Array name must not be blank");
		}
		
		TomlArray array = new TomlArray();
		array.setArrayOfTables(true);
		
		TomlTable parentTable = (TomlTable) this.getCurrentContext().element;
		parentTable.add(name, array);
		
		this.contextStack.push(new ContextFrame(BuilderContext.ARRAY, array));
		return this;
	}
	
	/**
	 * Ends the current array of tables context and returns to the parent context.<br>
	 *
	 * @return This builder for chaining
	 * @throws IllegalStateException If the current context is not an array or if at root level
	 */
	public @NonNull TomlBuilder endArrayOfTables() {
		return this.endArray();
	}
	
	/**
	 * Builds and returns the constructed toml element.<br>
	 *
	 * @return The constructed toml element
	 * @throws IllegalStateException If there are unclosed nested contexts
	 */
	public @NonNull TomlElement build() {
		if (this.contextStack.size() != 1) {
			throw new IllegalStateException("Cannot build with unclosed nested contexts. Current nesting depth: " + (this.contextStack.size() - 1));
		}
		return this.root;
	}
	
	/**
	 * Builds and returns the constructed toml element as a toml table.<br>
	 * This is a convenience method that casts the result to toml table.<br>
	 *
	 * @return The constructed toml table
	 * @throws IllegalStateException If there are unclosed nested contexts or if the root is not a table
	 */
	public @NonNull TomlTable buildTable() {
		TomlElement element = this.build();
		if (!(element instanceof TomlTable table)) {
			throw new IllegalStateException("Root element is not a toml table");
		}
		
		TomlTable result = new TomlTable();
		result.setInline(table.isInline());
		result.addAll(table);
		return result;
	}
	
	/**
	 * Builds and returns the constructed toml element as a toml array.<br>
	 * This is a convenience method that casts the result to toml array.<br>
	 *
	 * @return The constructed toml array
	 * @throws IllegalStateException If there are unclosed nested contexts or if the root is not an array
	 */
	public @NonNull TomlArray buildArray() {
		TomlElement element = this.build();
		if (!(element instanceof TomlArray array)) {
			throw new IllegalStateException("Root element is not a toml array");
		}
		
		TomlArray result = new TomlArray();
		result.setArrayOfTables(array.isArrayOfTables());
		result.addAll(array);
		return result;
	}
	
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
	 * Checks if the builder is currently in a table context.<br>
	 * @return True if the current context is a table, false otherwise
	 */
	public boolean isInTableContext() {
		return !this.contextStack.isEmpty() && this.getCurrentContext().type == BuilderContext.TABLE;
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
	
	@Override
	public String toString() {
		return this.root.toString();
	}
	
	/**
	 * Returns a string representation of the element being built.<br>
	 *
	 * @param config The toml config to use for formatting
	 * @return The string representation
	 */
	public @NonNull String toString(@NonNull TomlConfig config) {
		return this.root.toString(config);
	}
	
	/**
	 * Enumeration of builder contexts to track whether we're building a table or array.<br>
	 *
	 * @author Luis-St
	 */
	private enum BuilderContext {
		/**
		 * Context for building a toml table.<br>
		 */
		TABLE,
		/**
		 * Context for building a toml array.<br>
		 */
		ARRAY
	}
	
	/**
	 * Internal class to track nesting state.<br>
	 *
	 * @author Luis-St
	 *
	 * @param type The type of context (table or array)
	 * @param element The toml element associated with this context
	 */
	private record ContextFrame(@NonNull BuilderContext type, @NonNull TomlElement element) {
		
		/**
		 * Constructs a new context frame with the specified type and element.<br>
		 *
		 * @param type The type of context (table or array)
		 * @param element The toml element associated with this context
		 * @throws NullPointerException If the type or element is null
		 */
		private ContextFrame {
			Objects.requireNonNull(type, "Context type must not be null");
			Objects.requireNonNull(element, "Context element must not be null");
		}
	}
}
