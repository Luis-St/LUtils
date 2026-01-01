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

package net.luis.utils.io.data.toml;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.*;
import java.util.*;

/**
 * A fluent builder for constructing TOML tables.<br>
 * Provides a chainable API for building TOML structures programmatically.<br>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * TomlTable table = TomlBuilder.table()
 *     .add("title", "TOML Example")
 *     .startTable("database")
 *         .add("host", "localhost")
 *         .add("port", 5432)
 *         .add("enabled", true)
 *     .endTable()
 *     .startTable("server")
 *         .add("host", "0.0.0.0")
 *         .add("ports", TomlBuilder.array().add(8080).add(8443).build())
 *     .endTable()
 *     .build();
 * }</pre>
 *
 * @author Luis-St
 */
public final class TomlBuilder {
	
	/**
	 * The context stack for tracking nested tables.<br>
	 */
	private final Deque<BuilderContext> contextStack = new ArrayDeque<>();
	
	/**
	 * The root table being built.<br>
	 */
	private final TomlTable rootTable;
	
	/**
	 * Constructs a new TOML builder.<br>
	 */
	private TomlBuilder() {
		this.rootTable = new TomlTable();
	}
	
	/**
	 * Creates a new TOML builder for building a TOML table.<br>
	 *
	 * @return A new TOML builder
	 */
	public static @NonNull TomlBuilder table() {
		return new TomlBuilder();
	}
	
	/**
	 * Creates a new array builder for building a TOML array.<br>
	 *
	 * @return A new array builder
	 */
	public static @NonNull ArrayBuilder array() {
		return new ArrayBuilder();
	}
	
	/**
	 * Creates a new inline table builder.<br>
	 *
	 * @return A new inline table builder
	 */
	public static @NonNull InlineTableBuilder inlineTable() {
		return new InlineTableBuilder();
	}
	
	/**
	 * Returns the current table being built.<br>
	 *
	 * @return The current table
	 */
	private @NonNull TomlTable getCurrentTable() {
		if (this.contextStack.isEmpty()) {
			return this.rootTable;
		}
		return this.contextStack.peek().table();
	}
	
	/**
	 * Adds a property with a string value.<br>
	 *
	 * @param key The property key
	 * @param value The string value (null becomes TomlNull)
	 * @return This builder for chaining
	 * @throws NullPointerException If the key is null
	 */
	public @NonNull TomlBuilder add(@NonNull String key, @Nullable String value) {
		Objects.requireNonNull(key, "Property key must not be null");
		
		this.getCurrentTable().add(key, value);
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
	public @NonNull TomlBuilder add(@NonNull String key, boolean value) {
		Objects.requireNonNull(key, "Property key must not be null");
		
		this.getCurrentTable().add(key, value);
		return this;
	}
	
	/**
	 * Adds a property with a number value.<br>
	 *
	 * @param key The property key
	 * @param value The number value (null becomes TomlNull)
	 * @return This builder for chaining
	 * @throws NullPointerException If the key is null
	 */
	public @NonNull TomlBuilder add(@NonNull String key, @Nullable Number value) {
		Objects.requireNonNull(key, "Property key must not be null");
		
		this.getCurrentTable().add(key, value);
		return this;
	}
	
	/**
	 * Adds a property with a local date value.<br>
	 *
	 * @param key The property key
	 * @param value The local date value (null becomes TomlNull)
	 * @return This builder for chaining
	 * @throws NullPointerException If the key is null
	 */
	public @NonNull TomlBuilder add(@NonNull String key, @Nullable LocalDate value) {
		Objects.requireNonNull(key, "Property key must not be null");
		
		this.getCurrentTable().add(key, value);
		return this;
	}
	
	/**
	 * Adds a property with a local time value.<br>
	 *
	 * @param key The property key
	 * @param value The local time value (null becomes TomlNull)
	 * @return This builder for chaining
	 * @throws NullPointerException If the key is null
	 */
	public @NonNull TomlBuilder add(@NonNull String key, @Nullable LocalTime value) {
		Objects.requireNonNull(key, "Property key must not be null");
		
		this.getCurrentTable().add(key, value);
		return this;
	}
	
	/**
	 * Adds a property with a local date-time value.<br>
	 *
	 * @param key The property key
	 * @param value The local date-time value (null becomes TomlNull)
	 * @return This builder for chaining
	 * @throws NullPointerException If the key is null
	 */
	public @NonNull TomlBuilder add(@NonNull String key, @Nullable LocalDateTime value) {
		Objects.requireNonNull(key, "Property key must not be null");
		
		this.getCurrentTable().add(key, value);
		return this;
	}
	
	/**
	 * Adds a property with an offset date-time value.<br>
	 *
	 * @param key The property key
	 * @param value The offset date-time value (null becomes TomlNull)
	 * @return This builder for chaining
	 * @throws NullPointerException If the key is null
	 */
	public @NonNull TomlBuilder add(@NonNull String key, @Nullable OffsetDateTime value) {
		Objects.requireNonNull(key, "Property key must not be null");
		
		this.getCurrentTable().add(key, value);
		return this;
	}
	
	/**
	 * Adds a property with a TOML element value.<br>
	 *
	 * @param key The property key
	 * @param element The TOML element value (null becomes TomlNull)
	 * @return This builder for chaining
	 * @throws NullPointerException If the key is null
	 */
	public @NonNull TomlBuilder add(@NonNull String key, @Nullable TomlElement element) {
		Objects.requireNonNull(key, "Property key must not be null");
		
		this.getCurrentTable().add(key, element);
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
	public @NonNull TomlBuilder addIf(boolean condition, @NonNull String key, @Nullable String value) {
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
	public @NonNull TomlBuilder addIf(boolean condition, @NonNull String key, boolean value) {
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
	public @NonNull TomlBuilder addIf(boolean condition, @NonNull String key, @Nullable Number value) {
		if (condition) {
			this.add(key, value);
		}
		return this;
	}
	
	/**
	 * Starts a new nested table with the given name.<br>
	 * All subsequent add() calls will add properties to this table.<br>
	 *
	 * @param name The table name
	 * @return This builder for chaining
	 * @throws NullPointerException If the name is null
	 * @throws IllegalArgumentException If the name is blank
	 */
	public @NonNull TomlBuilder startTable(@NonNull String name) {
		Objects.requireNonNull(name, "Table name must not be null");
		if (name.isBlank()) {
			throw new IllegalArgumentException("Table name must not be blank");
		}
		
		TomlTable parentTable = this.getCurrentTable();
		TomlElement existing = parentTable.get(name);
		TomlTable newTable;
		
		if (existing instanceof TomlTable table) {
			newTable = table;
		} else {
			newTable = new TomlTable();
			parentTable.add(name, newTable);
		}
		
		this.contextStack.push(new BuilderContext(name, newTable));
		return this;
	}
	
	/**
	 * Ends the current nested table and returns to the parent table.<br>
	 *
	 * @return This builder for chaining
	 * @throws IllegalStateException If not currently inside a nested table
	 */
	public @NonNull TomlBuilder endTable() {
		if (this.contextStack.isEmpty()) {
			throw new IllegalStateException("Not inside a nested table");
		}
		
		this.contextStack.pop();
		return this;
	}
	
	/**
	 * Starts an inline table with the given name.<br>
	 * Inline tables are formatted on a single line: {key = "value"}.<br>
	 *
	 * @param name The table name
	 * @return This builder for chaining
	 * @throws NullPointerException If the name is null
	 * @throws IllegalArgumentException If the name is blank
	 */
	public @NonNull TomlBuilder startInlineTable(@NonNull String name) {
		Objects.requireNonNull(name, "Table name must not be null");
		if (name.isBlank()) {
			throw new IllegalArgumentException("Table name must not be blank");
		}
		
		TomlTable inlineTable = new TomlTable();
		inlineTable.setInline(true);
		
		this.getCurrentTable().add(name, inlineTable);
		this.contextStack.push(new BuilderContext(name, inlineTable));
		return this;
	}
	
	/**
	 * Ends the current inline table and returns to the parent table.<br>
	 *
	 * @return This builder for chaining
	 * @throws IllegalStateException If not currently inside a table
	 */
	public @NonNull TomlBuilder endInlineTable() {
		return this.endTable();
	}
	
	/**
	 * Adds an array of tables.<br>
	 * Each table in the array will be formatted with [[array.name]] notation.<br>
	 *
	 * @param name The array name
	 * @param tables The tables to add
	 * @return This builder for chaining
	 * @throws NullPointerException If the name or tables are null
	 */
	public @NonNull TomlBuilder addArrayOfTables(@NonNull String name, @NonNull TomlTable... tables) {
		Objects.requireNonNull(name, "Array name must not be null");
		Objects.requireNonNull(tables, "Tables must not be null");
		
		TomlArray array = new TomlArray();
		array.setArrayOfTables(true);
		for (TomlTable table : tables) {
			array.add(table);
		}
		
		this.getCurrentTable().add(name, array);
		return this;
	}
	
	/**
	 * Adds an array of tables from a list.<br>
	 *
	 * @param name The array name
	 * @param tables The tables to add
	 * @return This builder for chaining
	 * @throws NullPointerException If the name or tables are null
	 */
	public @NonNull TomlBuilder addArrayOfTables(@NonNull String name, @NonNull List<TomlTable> tables) {
		Objects.requireNonNull(name, "Array name must not be null");
		Objects.requireNonNull(tables, "Tables must not be null");
		
		TomlArray array = new TomlArray();
		array.setArrayOfTables(true);
		for (TomlTable table : tables) {
			array.add(table);
		}
		
		this.getCurrentTable().add(name, array);
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
	 * Checks if currently inside a nested table.<br>
	 *
	 * @return True if inside a nested table, false otherwise
	 */
	public boolean isNested() {
		return !this.contextStack.isEmpty();
	}
	
	/**
	 * Checks if at the root level (not inside any nested table).<br>
	 *
	 * @return True if at root level, false otherwise
	 */
	public boolean isAtRoot() {
		return this.contextStack.isEmpty();
	}
	
	/**
	 * Builds and returns the TOML table.<br>
	 * Automatically closes any open nested tables.<br>
	 *
	 * @return The built TOML table
	 */
	public @NonNull TomlTable build() {
		this.contextStack.clear();
		return this.rootTable;
	}
	
	@Override
	public String toString() {
		return this.toString(TomlConfig.DEFAULT);
	}
	
	/**
	 * Returns a string representation of the table being built.<br>
	 *
	 * @param config The TOML config to use for formatting
	 * @return The string representation
	 */
	public @NonNull String toString(@NonNull TomlConfig config) {
		return this.rootTable.toString(config);
	}
	
	/**
	 * Represents the current builder context.<br>
	 *
	 * @param name The table name
	 * @param table The table being built
	 */
	private record BuilderContext(@NonNull String name, @NonNull TomlTable table) {}
	
	//region Array builder
	
	/**
	 * A builder for constructing TOML arrays.<br>
	 */
	public static final class ArrayBuilder {
		
		/**
		 * The array being built.<br>
		 */
		private final TomlArray array = new TomlArray();
		
		/**
		 * Constructs a new array builder.<br>
		 */
		private ArrayBuilder() {}
		
		/**
		 * Adds a string value to the array.<br>
		 *
		 * @param value The string value (null becomes TomlNull)
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
		 * @param value The number value (null becomes TomlNull)
		 * @return This builder for chaining
		 */
		public @NonNull ArrayBuilder add(@Nullable Number value) {
			this.array.add(value);
			return this;
		}
		
		/**
		 * Adds a local date value to the array.<br>
		 *
		 * @param value The local date value (null becomes TomlNull)
		 * @return This builder for chaining
		 */
		public @NonNull ArrayBuilder add(@Nullable LocalDate value) {
			this.array.add(value);
			return this;
		}
		
		/**
		 * Adds a local time value to the array.<br>
		 *
		 * @param value The local time value (null becomes TomlNull)
		 * @return This builder for chaining
		 */
		public @NonNull ArrayBuilder add(@Nullable LocalTime value) {
			this.array.add(value);
			return this;
		}
		
		/**
		 * Adds a local date-time value to the array.<br>
		 *
		 * @param value The local date-time value (null becomes TomlNull)
		 * @return This builder for chaining
		 */
		public @NonNull ArrayBuilder add(@Nullable LocalDateTime value) {
			this.array.add(value);
			return this;
		}
		
		/**
		 * Adds an offset date-time value to the array.<br>
		 *
		 * @param value The offset date-time value (null becomes TomlNull)
		 * @return This builder for chaining
		 */
		public @NonNull ArrayBuilder add(@Nullable OffsetDateTime value) {
			this.array.add(value);
			return this;
		}
		
		/**
		 * Adds a TOML element to the array.<br>
		 *
		 * @param element The element (null becomes TomlNull)
		 * @return This builder for chaining
		 */
		public @NonNull ArrayBuilder add(@Nullable TomlElement element) {
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
		public @NonNull ArrayBuilder addAll(@NonNull TomlArray other) {
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
		 * Marks this array as an array of tables.<br>
		 *
		 * @return This builder for chaining
		 */
		public @NonNull ArrayBuilder asArrayOfTables() {
			this.array.setArrayOfTables(true);
			return this;
		}
		
		/**
		 * Builds and returns the TOML array.<br>
		 *
		 * @return The built TOML array
		 */
		public @NonNull TomlArray build() {
			return this.array;
		}
	}
	//endregion
	
	//region Inline table builder
	
	/**
	 * A builder for constructing inline TOML tables.<br>
	 */
	public static final class InlineTableBuilder {
		
		/**
		 * The table being built.<br>
		 */
		private final TomlTable table = new TomlTable();
		
		/**
		 * Constructs a new inline table builder.<br>
		 */
		private InlineTableBuilder() {
			this.table.setInline(true);
		}
		
		/**
		 * Adds a property with a string value.<br>
		 *
		 * @param key The property key
		 * @param value The string value (null becomes TomlNull)
		 * @return This builder for chaining
		 * @throws NullPointerException If the key is null
		 */
		public @NonNull InlineTableBuilder add(@NonNull String key, @Nullable String value) {
			this.table.add(key, value);
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
		public @NonNull InlineTableBuilder add(@NonNull String key, boolean value) {
			this.table.add(key, value);
			return this;
		}
		
		/**
		 * Adds a property with a number value.<br>
		 *
		 * @param key The property key
		 * @param value The number value (null becomes TomlNull)
		 * @return This builder for chaining
		 * @throws NullPointerException If the key is null
		 */
		public @NonNull InlineTableBuilder add(@NonNull String key, @Nullable Number value) {
			this.table.add(key, value);
			return this;
		}
		
		/**
		 * Adds a property with a TOML element value.<br>
		 *
		 * @param key The property key
		 * @param element The element (null becomes TomlNull)
		 * @return This builder for chaining
		 * @throws NullPointerException If the key is null
		 */
		public @NonNull InlineTableBuilder add(@NonNull String key, @Nullable TomlElement element) {
			this.table.add(key, element);
			return this;
		}
		
		/**
		 * Builds and returns the inline TOML table.<br>
		 *
		 * @return The built inline TOML table
		 */
		public @NonNull TomlTable build() {
			return this.table;
		}
	}
	//endregion
}
