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

package net.luis.utils.io.data.ini;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.*;

/**
 * A fluent builder for constructing ini documents.<br>
 * Provides a chainable API for building ini structures programmatically.<br>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * IniDocument doc = IniBuilder.document()
 *     .addGlobal("version", "1.0")
 *     .startSection("database")
 *         .add("host", "localhost")
 *         .add("port", 5432)
 *     .endSection()
 *     .startSection("logging")
 *         .add("level", "INFO")
 *         .add("enabled", true)
 *     .endSection()
 *     .build();
 * }</pre>
 *
 * @author Luis-St
 */
public final class IniBuilder {
	
	/**
	 * The context stack for tracking nested sections.<br>
	 */
	private final Deque<BuilderContext> contextStack = new ArrayDeque<>();
	
	/**
	 * The root document being built.<br>
	 */
	private final IniDocument document;
	
	/**
	 * Constructs a new ini builder.<br>
	 */
	private IniBuilder() {
		this.document = new IniDocument();
	}
	
	/**
	 * Creates a new ini builder for building an ini document.<br>
	 *
	 * @return A new ini builder
	 */
	public static @NonNull IniBuilder document() {
		return new IniBuilder();
	}
	
	/**
	 * Adds a global property with a string value.<br>
	 * Global properties appear before any section in the ini file.<br>
	 *
	 * @param key The property key
	 * @param value The string value (null becomes IniNull)
	 * @return This builder for chaining
	 * @throws NullPointerException If the key is null
	 * @throws IllegalStateException If currently inside a section
	 */
	public @NonNull IniBuilder addGlobal(@NonNull String key, @Nullable String value) {
		Objects.requireNonNull(key, "Global property key must not be null");
		this.ensureNotInSection();
		
		this.document.addGlobal(key, value);
		return this;
	}
	
	/**
	 * Adds a global property with a boolean value.<br>
	 *
	 * @param key The property key
	 * @param value The boolean value
	 * @return This builder for chaining
	 * @throws NullPointerException If the key is null
	 * @throws IllegalStateException If currently inside a section
	 */
	public @NonNull IniBuilder addGlobal(@NonNull String key, boolean value) {
		Objects.requireNonNull(key, "Global property key must not be null");
		this.ensureNotInSection();
		
		this.document.addGlobal(key, value);
		return this;
	}
	
	/**
	 * Adds a global property with a number value.<br>
	 *
	 * @param key The property key
	 * @param value The number value (null becomes IniNull)
	 * @return This builder for chaining
	 * @throws NullPointerException If the key is null
	 * @throws IllegalStateException If currently inside a section
	 */
	public @NonNull IniBuilder addGlobal(@NonNull String key, @Nullable Number value) {
		Objects.requireNonNull(key, "Global property key must not be null");
		this.ensureNotInSection();
		
		this.document.addGlobal(key, value);
		return this;
	}
	
	/**
	 * Adds a global property with an ini element value.<br>
	 *
	 * @param key The property key
	 * @param element The ini element value (null becomes IniNull)
	 * @return This builder for chaining
	 * @throws NullPointerException If the key is null
	 * @throws IllegalStateException If currently inside a section
	 */
	public @NonNull IniBuilder addGlobal(@NonNull String key, @Nullable IniElement element) {
		Objects.requireNonNull(key, "Global property key must not be null");
		this.ensureNotInSection();
		
		this.document.addGlobal(key, element);
		return this;
	}
	
	/**
	 * Conditionally adds a global property with a string value.<br>
	 *
	 * @param condition The condition to check
	 * @param key The property key
	 * @param value The string value
	 * @return This builder for chaining
	 * @throws NullPointerException If the key is null
	 * @throws IllegalStateException If currently inside a section
	 */
	public @NonNull IniBuilder addGlobalIf(boolean condition, @NonNull String key, @Nullable String value) {
		if (condition) {
			this.addGlobal(key, value);
		}
		return this;
	}
	
	/**
	 * Starts a new section with the given name.<br>
	 * All subsequent add() calls will add properties to this section.<br>
	 *
	 * @param name The section name
	 * @return This builder for chaining
	 * @throws NullPointerException If the name is null
	 * @throws IllegalArgumentException If the name is blank
	 */
	public @NonNull IniBuilder startSection(@NonNull String name) {
		Objects.requireNonNull(name, "Section name must not be null");
		if (name.isBlank()) {
			throw new IllegalArgumentException("Section name must not be blank");
		}
		
		IniSection section = this.document.getOrCreateSection(name);
		this.contextStack.push(new BuilderContext(name, section));
		return this;
	}
	
	/**
	 * Ends the current section and returns to the document level.<br>
	 *
	 * @return This builder for chaining
	 * @throws IllegalStateException If not currently inside a section
	 */
	public @NonNull IniBuilder endSection() {
		if (this.contextStack.isEmpty()) {
			throw new IllegalStateException("Not inside a section");
		}
		this.contextStack.pop();
		return this;
	}
	
	/**
	 * Adds a property with a string value to the current section.<br>
	 *
	 * @param key The property key
	 * @param value The string value (null becomes IniNull)
	 * @return This builder for chaining
	 * @throws NullPointerException If the key is null
	 * @throws IllegalStateException If not currently inside a section
	 */
	public @NonNull IniBuilder add(@NonNull String key, @Nullable String value) {
		Objects.requireNonNull(key, "Property key must not be null");
		
		this.getCurrentSection().add(key, value);
		return this;
	}
	
	/**
	 * Adds a property with a boolean value to the current section.<br>
	 *
	 * @param key The property key
	 * @param value The boolean value
	 * @return This builder for chaining
	 * @throws NullPointerException If the key is null
	 * @throws IllegalStateException If not currently inside a section
	 */
	public @NonNull IniBuilder add(@NonNull String key, boolean value) {
		Objects.requireNonNull(key, "Property key must not be null");
		
		this.getCurrentSection().add(key, value);
		return this;
	}
	
	/**
	 * Adds a property with a number value to the current section.<br>
	 *
	 * @param key The property key
	 * @param value The number value (null becomes IniNull)
	 * @return This builder for chaining
	 * @throws NullPointerException If the key is null
	 * @throws IllegalStateException If not currently inside a section
	 */
	public @NonNull IniBuilder add(@NonNull String key, @Nullable Number value) {
		Objects.requireNonNull(key, "Property key must not be null");
		
		this.getCurrentSection().add(key, value);
		return this;
	}
	
	/**
	 * Adds a property with an ini element value to the current section.<br>
	 *
	 * @param key The property key
	 * @param element The ini element value (null becomes IniNull)
	 * @return This builder for chaining
	 * @throws NullPointerException If the key is null
	 * @throws IllegalStateException If not currently inside a section
	 */
	public @NonNull IniBuilder add(@NonNull String key, @Nullable IniElement element) {
		Objects.requireNonNull(key, "Property key must not be null");
		
		this.getCurrentSection().add(key, element);
		return this;
	}
	
	/**
	 * Conditionally adds a property with a string value to the current section.<br>
	 *
	 * @param condition The condition to check
	 * @param key The property key
	 * @param value The string value
	 * @return This builder for chaining
	 * @throws NullPointerException If the key is null
	 * @throws IllegalStateException If not currently inside a section
	 */
	public @NonNull IniBuilder addIf(boolean condition, @NonNull String key, @Nullable String value) {
		if (condition) {
			this.add(key, value);
		}
		return this;
	}
	
	/**
	 * Conditionally adds a property with a boolean value to the current section.<br>
	 *
	 * @param condition The condition to check
	 * @param key The property key
	 * @param value The boolean value
	 * @return This builder for chaining
	 * @throws NullPointerException If the key is null
	 * @throws IllegalStateException If not currently inside a section
	 */
	public @NonNull IniBuilder addIf(boolean condition, @NonNull String key, boolean value) {
		if (condition) {
			this.add(key, value);
		}
		return this;
	}
	
	/**
	 * Conditionally adds a property with a number value to the current section.<br>
	 *
	 * @param condition The condition to check
	 * @param key The property key
	 * @param value The number value
	 * @return This builder for chaining
	 * @throws NullPointerException If the key is null
	 * @throws IllegalStateException If not currently inside a section
	 */
	public @NonNull IniBuilder addIf(boolean condition, @NonNull String key, @Nullable Number value) {
		if (condition) {
			this.add(key, value);
		}
		return this;
	}
	
	/**
	 * Adds a complete section to the document.<br>
	 *
	 * @param section The section to add
	 * @return This builder for chaining
	 * @throws NullPointerException If the section is null
	 * @throws IllegalStateException If currently inside a section
	 */
	public @NonNull IniBuilder addSection(@NonNull IniSection section) {
		Objects.requireNonNull(section, "Section must not be null");
		this.ensureNotInSection();
		
		this.document.addSection(section);
		return this;
	}
	
	/**
	 * Returns the current section being built.<br>
	 *
	 * @return The current section
	 * @throws IllegalStateException If not currently inside a section
	 */
	private @NonNull IniSection getCurrentSection() {
		if (this.contextStack.isEmpty()) {
			throw new IllegalStateException("Not inside a section. Use startSection() first or addGlobal() for global properties.");
		}
		return this.contextStack.peek().section();
	}
	
	/**
	 * Ensures the builder is not currently inside a section.<br>
	 *
	 * @throws IllegalStateException If currently inside a section
	 */
	private void ensureNotInSection() {
		if (!this.contextStack.isEmpty()) {
			throw new IllegalStateException("Cannot add global property while inside section '" + this.contextStack.peek().name() + "'. Use endSection() first.");
		}
	}
	
	/**
	 * Returns the current nesting depth.<br>
	 *
	 * @return The nesting depth (0 = document level, 1 = inside section)
	 */
	public int getNestingDepth() {
		return this.contextStack.size();
	}
	
	/**
	 * Checks if currently inside a section.<br>
	 *
	 * @return True if inside a section, false otherwise
	 */
	public boolean isInSection() {
		return !this.contextStack.isEmpty();
	}
	
	/**
	 * Checks if at the document level (not inside any section).<br>
	 *
	 * @return True if at document level, false otherwise
	 */
	public boolean isAtDocumentLevel() {
		return this.contextStack.isEmpty();
	}
	
	/**
	 * Builds and returns the ini document.<br>
	 * Automatically closes any open sections.<br>
	 *
	 * @return The built ini document
	 */
	public @NonNull IniDocument build() {
		this.contextStack.clear();
		return this.document;
	}
	
	//region Object overrides
	@Override
	public String toString() {
		return this.toString(IniConfig.DEFAULT);
	}
	
	/**
	 * Returns a string representation of the document being built.<br>
	 *
	 * @param config The ini config to use for formatting
	 * @return The string representation
	 */
	public @NonNull String toString(@NonNull IniConfig config) {
		return this.document.toString(config);
	}
	//endregion
	
	/**
	 * Represents the current builder context.<br>
	 *
	 * @param name The section name
	 * @param section The section being built
	 */
	private record BuilderContext(@NonNull String name, @NonNull IniSection section) {}
}
