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

package net.luis.utils.io.data.yaml;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.*;

/**
 * A fluent builder for constructing complex yaml structures.<br>
 * This builder provides a convenient way to create nested yaml mappings and sequences
 * using a chainable API that maintains proper structure and type safety.<br>
 * <p>
 *     The builder supports creating both yaml mappings and sequences, with automatic
 *     context tracking to ensure operations are valid for the current structure type.
 *     It provides type-specific methods for adding common data types and supports
 *     arbitrary nesting depth.
 * </p>
 * <p>
 *     The builder also supports yaml anchors and aliases for creating reusable
 *     yaml structures.
 * </p>
 *
 * <p>
 *     Usage Examples:
 * </p>
 * <pre>{@code
 * // Simple mapping
 * YamlMapping user = YamlBuilder.mapping()
 *     .add("name", "John Doe")
 *     .add("age", 30)
 *     .add("active", true)
 *     .buildMapping();
 *
 * // With anchors and aliases
 * YamlMapping config = YamlBuilder.mapping()
 *     .addWithAnchor("defaults", defaultSettings, "default_settings")
 *     .startMapping("production")
 *         .add("host", "prod.example.com")
 *         .addAlias("settings", "default_settings")
 *     .endMapping()
 *     .buildMapping();
 * }</pre>
 *
 * @author Luis-St
 */
public final class YamlBuilder {
	
	/**
	 * Stack to track nested contexts for proper structure building.
	 */
	private final Deque<ContextFrame> contextStack = new ArrayDeque<>();
	/**
	 * The root element being built.
	 */
	private final YamlElement root;
	
	/**
	 * Private constructor to enforce factory method usage.
	 *
	 * @param initialContext The initial context type
	 * @param initialElement The initial yaml element
	 */
	private YamlBuilder(@NonNull BuilderContext initialContext, @NonNull YamlElement initialElement) {
		this.root = initialElement;
		this.contextStack.push(new ContextFrame(initialContext, initialElement));
	}
	
	//region Factory methods
	
	/**
	 * Creates a new builder for constructing a yaml mapping.<br>
	 * The builder will start in mapping context, allowing you to add key-value pairs.
	 *
	 * @return A new YamlBuilder configured for mapping construction
	 */
	public static @NonNull YamlBuilder mapping() {
		return new YamlBuilder(BuilderContext.MAPPING, new YamlMapping());
	}
	
	/**
	 * Creates a new builder for constructing a yaml sequence.<br>
	 * The builder will start in sequence context, allowing you to add elements.
	 *
	 * @return A new YamlBuilder configured for sequence construction
	 */
	public static @NonNull YamlBuilder sequence() {
		return new YamlBuilder(BuilderContext.SEQUENCE, new YamlSequence());
	}
	//endregion
	
	//region Context management
	
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
	 * Ensures the current context is a mapping context.
	 *
	 * @throws IllegalStateException If the current context is not a mapping
	 */
	private void ensureMappingContext() {
		ContextFrame current = this.getCurrentContext();
		if (current.type != BuilderContext.MAPPING) {
			throw new IllegalStateException("Current context is not a mapping. Use add(value) for sequences.");
		}
	}
	
	/**
	 * Ensures the current context is a sequence context.
	 *
	 * @throws IllegalStateException If the current context is not a sequence
	 */
	private void ensureSequenceContext() {
		ContextFrame current = this.getCurrentContext();
		if (current.type != BuilderContext.SEQUENCE) {
			throw new IllegalStateException("Current context is not a sequence. Use add(key, value) for mappings.");
		}
	}
	//endregion
	
	//region Mapping add methods
	
	/**
	 * Adds a yaml element with the specified key to the current mapping.<br>
	 * If the element is null, it will be converted to YamlNull.
	 *
	 * @param key The key for the element
	 * @param element The yaml element to add
	 * @return This builder for method chaining
	 * @throws IllegalStateException If the current context is not a mapping
	 * @throws NullPointerException If the key is null
	 */
	public @NonNull YamlBuilder add(@NonNull String key, @Nullable YamlElement element) {
		this.ensureMappingContext();
		Objects.requireNonNull(key, "Key must not be null");
		
		YamlMapping mapping = (YamlMapping) this.getCurrentContext().element;
		mapping.add(key, element);
		return this;
	}
	
	/**
	 * Adds a string value with the specified key to the current mapping.<br>
	 * If the value is null, it will be converted to YamlNull.
	 *
	 * @param key The key for the value
	 * @param value The string value to add
	 * @return This builder for method chaining
	 * @throws IllegalStateException If the current context is not a mapping
	 * @throws NullPointerException If the key is null
	 */
	public @NonNull YamlBuilder add(@NonNull String key, @Nullable String value) {
		return this.add(key, value == null ? null : new YamlScalar(value));
	}
	
	/**
	 * Adds a boolean value with the specified key to the current mapping.
	 *
	 * @param key The key for the value
	 * @param value The boolean value to add
	 * @return This builder for method chaining
	 * @throws IllegalStateException If the current context is not a mapping
	 * @throws NullPointerException If the key is null
	 */
	public @NonNull YamlBuilder add(@NonNull String key, boolean value) {
		return this.add(key, new YamlScalar(value));
	}
	
	/**
	 * Adds a number value with the specified key to the current mapping.<br>
	 * If the value is null, it will be converted to YamlNull.
	 *
	 * @param key The key for the value
	 * @param value The number value to add
	 * @return This builder for method chaining
	 * @throws IllegalStateException If the current context is not a mapping
	 * @throws NullPointerException If the key is null
	 */
	public @NonNull YamlBuilder add(@NonNull String key, @Nullable Number value) {
		return this.add(key, value == null ? null : new YamlScalar(value));
	}
	
	/**
	 * Adds a nested yaml mapping with the specified key to the current mapping.<br>
	 * This method allows you to add pre-built YamlBuilder results as nested mappings.
	 *
	 * @param key The key for the nested mapping
	 * @param mappingBuilder The builder containing the mapping to nest
	 * @return This builder for method chaining
	 * @throws IllegalStateException If the current context is not a mapping
	 * @throws NullPointerException If the key or mappingBuilder is null
	 */
	public @NonNull YamlBuilder addMapping(@NonNull String key, @NonNull YamlBuilder mappingBuilder) {
		Objects.requireNonNull(mappingBuilder, "Mapping builder must not be null");
		return this.add(key, mappingBuilder.build());
	}
	
	/**
	 * Adds a nested yaml sequence with the specified key to the current mapping.<br>
	 * This method allows you to add pre-built YamlBuilder results as nested sequences.
	 *
	 * @param key The key for the nested sequence
	 * @param sequenceBuilder The builder containing the sequence to nest
	 * @return This builder for method chaining
	 * @throws IllegalStateException If the current context is not a mapping
	 * @throws NullPointerException If the key or sequenceBuilder is null
	 */
	public @NonNull YamlBuilder addSequence(@NonNull String key, @NonNull YamlBuilder sequenceBuilder) {
		Objects.requireNonNull(sequenceBuilder, "Sequence builder must not be null");
		return this.add(key, sequenceBuilder.build());
	}
	//endregion
	
	//region Mapping anchor methods
	
	/**
	 * Adds an element with an anchor to the current mapping.<br>
	 *
	 * @param key The mapping key
	 * @param element The element to anchor
	 * @param anchorName The anchor name for later reference
	 * @return This builder for method chaining
	 * @throws IllegalStateException If the current context is not a mapping
	 * @throws NullPointerException If key, element, or anchorName is null
	 */
	public @NonNull YamlBuilder addWithAnchor(@NonNull String key, @NonNull YamlElement element, @NonNull String anchorName) {
		Objects.requireNonNull(element, "Element must not be null");
		Objects.requireNonNull(anchorName, "Anchor name must not be null");
		return this.add(key, new YamlAnchor(anchorName, element));
	}
	
	/**
	 * Adds a string value with an anchor to the current mapping.<br>
	 *
	 * @param key The mapping key
	 * @param value The string value to anchor
	 * @param anchorName The anchor name for later reference
	 * @return This builder for method chaining
	 * @throws IllegalStateException If the current context is not a mapping
	 * @throws NullPointerException If key or anchorName is null
	 */
	public @NonNull YamlBuilder addWithAnchor(@NonNull String key, @Nullable String value, @NonNull String anchorName) {
		YamlElement element = value == null ? YamlNull.INSTANCE : new YamlScalar(value);
		return this.addWithAnchor(key, element, anchorName);
	}
	
	/**
	 * Adds a boolean value with an anchor to the current mapping.<br>
	 *
	 * @param key The mapping key
	 * @param value The boolean value to anchor
	 * @param anchorName The anchor name for later reference
	 * @return This builder for method chaining
	 * @throws IllegalStateException If the current context is not a mapping
	 * @throws NullPointerException If key or anchorName is null
	 */
	public @NonNull YamlBuilder addWithAnchor(@NonNull String key, boolean value, @NonNull String anchorName) {
		return this.addWithAnchor(key, new YamlScalar(value), anchorName);
	}
	
	/**
	 * Adds a number value with an anchor to the current mapping.<br>
	 *
	 * @param key The mapping key
	 * @param value The number value to anchor
	 * @param anchorName The anchor name for later reference
	 * @return This builder for method chaining
	 * @throws IllegalStateException If the current context is not a mapping
	 * @throws NullPointerException If key or anchorName is null
	 */
	public @NonNull YamlBuilder addWithAnchor(@NonNull String key, @Nullable Number value, @NonNull String anchorName) {
		YamlElement element = value == null ? YamlNull.INSTANCE : new YamlScalar(value);
		return this.addWithAnchor(key, element, anchorName);
	}
	
	/**
	 * Adds an alias (reference to an anchor) to the current mapping.<br>
	 *
	 * @param key The mapping key
	 * @param anchorName The name of the anchor to reference
	 * @return This builder for method chaining
	 * @throws IllegalStateException If the current context is not a mapping
	 * @throws NullPointerException If key or anchorName is null
	 */
	public @NonNull YamlBuilder addAlias(@NonNull String key, @NonNull String anchorName) {
		Objects.requireNonNull(anchorName, "Anchor name must not be null");
		return this.add(key, new YamlAlias(anchorName));
	}
	//endregion
	
	//region Sequence add methods
	
	/**
	 * Adds a yaml element to the current sequence.<br>
	 * If the element is null, it will be converted to YamlNull.
	 *
	 * @param element The yaml element to add
	 * @return This builder for method chaining
	 * @throws IllegalStateException If the current context is not a sequence
	 */
	public @NonNull YamlBuilder add(@Nullable YamlElement element) {
		this.ensureSequenceContext();
		
		YamlSequence sequence = (YamlSequence) this.getCurrentContext().element;
		sequence.add(element);
		return this;
	}
	
	/**
	 * Adds a string value to the current sequence.<br>
	 * If the value is null, it will be converted to YamlNull.
	 *
	 * @param value The string value to add
	 * @return This builder for method chaining
	 * @throws IllegalStateException If the current context is not a sequence
	 */
	public @NonNull YamlBuilder add(@Nullable String value) {
		return this.add(value == null ? null : new YamlScalar(value));
	}
	
	/**
	 * Adds a boolean value to the current sequence.
	 *
	 * @param value The boolean value to add
	 * @return This builder for method chaining
	 * @throws IllegalStateException If the current context is not a sequence
	 */
	public @NonNull YamlBuilder add(boolean value) {
		return this.add(new YamlScalar(value));
	}
	
	/**
	 * Adds a number value to the current sequence.<br>
	 * If the value is null, it will be converted to YamlNull.
	 *
	 * @param value The number value to add
	 * @return This builder for method chaining
	 * @throws IllegalStateException If the current context is not a sequence
	 */
	public @NonNull YamlBuilder add(@Nullable Number value) {
		return this.add(value == null ? null : new YamlScalar(value));
	}
	
	/**
	 * Adds a nested yaml mapping to the current sequence.<br>
	 * This method allows you to add pre-built YamlBuilder results as nested mappings.
	 *
	 * @param mappingBuilder The builder containing the mapping to add
	 * @return This builder for method chaining
	 * @throws IllegalStateException If the current context is not a sequence
	 * @throws NullPointerException If the mappingBuilder is null
	 */
	public @NonNull YamlBuilder addMapping(@NonNull YamlBuilder mappingBuilder) {
		Objects.requireNonNull(mappingBuilder, "Mapping builder must not be null");
		return this.add(mappingBuilder.build());
	}
	
	/**
	 * Adds a nested yaml sequence to the current sequence.<br>
	 * This method allows you to add pre-built YamlBuilder results as nested sequences.
	 *
	 * @param sequenceBuilder The builder containing the sequence to add
	 * @return This builder for method chaining
	 * @throws IllegalStateException If the current context is not a sequence
	 * @throws NullPointerException If the sequenceBuilder is null
	 */
	public @NonNull YamlBuilder addSequence(@NonNull YamlBuilder sequenceBuilder) {
		Objects.requireNonNull(sequenceBuilder, "Sequence builder must not be null");
		return this.add(sequenceBuilder.build());
	}
	
	/**
	 * Adds multiple string values to the current sequence at once.<br>
	 *
	 * @param values The values to add to the sequence
	 * @return This builder for method chaining
	 * @throws IllegalStateException If the current context is not a sequence
	 * @throws NullPointerException If the values array is null
	 */
	public @NonNull YamlBuilder addAll(@NonNull String... values) {
		Objects.requireNonNull(values, "Values array must not be null");
		for (String value : values) {
			this.add(value);
		}
		return this;
	}
	
	/**
	 * Adds multiple number values to the current sequence at once.<br>
	 *
	 * @param values The number values to add to the sequence
	 * @return This builder for method chaining
	 * @throws IllegalStateException If the current context is not a sequence
	 * @throws NullPointerException If the values array is null
	 */
	public @NonNull YamlBuilder addAll(@NonNull Number... values) {
		Objects.requireNonNull(values, "Values array must not be null");
		for (Number value : values) {
			this.add(value);
		}
		return this;
	}
	//endregion
	
	//region Sequence anchor methods
	
	/**
	 * Adds an element with an anchor to the current sequence.<br>
	 *
	 * @param element The element to anchor
	 * @param anchorName The anchor name for later reference
	 * @return This builder for method chaining
	 * @throws IllegalStateException If the current context is not a sequence
	 * @throws NullPointerException If element or anchorName is null
	 */
	public @NonNull YamlBuilder addWithAnchor(@NonNull YamlElement element, @NonNull String anchorName) {
		Objects.requireNonNull(element, "Element must not be null");
		Objects.requireNonNull(anchorName, "Anchor name must not be null");
		return this.add(new YamlAnchor(anchorName, element));
	}
	
	/**
	 * Adds a string value with an anchor to the current sequence.<br>
	 *
	 * @param value The string value to anchor
	 * @param anchorName The anchor name for later reference
	 * @return This builder for method chaining
	 * @throws IllegalStateException If the current context is not a sequence
	 * @throws NullPointerException If anchorName is null
	 */
	public @NonNull YamlBuilder addWithAnchor(@Nullable String value, @NonNull String anchorName) {
		YamlElement element = value == null ? YamlNull.INSTANCE : new YamlScalar(value);
		return this.addWithAnchor(element, anchorName);
	}
	
	/**
	 * Adds an alias (reference to an anchor) to the current sequence.<br>
	 *
	 * @param anchorName The name of the anchor to reference
	 * @return This builder for method chaining
	 * @throws IllegalStateException If the current context is not a sequence
	 * @throws NullPointerException If anchorName is null
	 */
	public @NonNull YamlBuilder addAlias(@NonNull String anchorName) {
		Objects.requireNonNull(anchorName, "Anchor name must not be null");
		return this.add(new YamlAlias(anchorName));
	}
	//endregion
	
	//region Nesting - start nested mapping
	
	/**
	 * Starts building a nested mapping with the specified key in the current mapping.<br>
	 * Use {@link #endMapping()} to return to the parent context.
	 *
	 * @param key The key for the nested mapping
	 * @return This builder for method chaining, now in mapping context
	 * @throws IllegalStateException If the current context is not a mapping
	 * @throws NullPointerException If the key is null
	 */
	public @NonNull YamlBuilder startMapping(@NonNull String key) {
		this.ensureMappingContext();
		Objects.requireNonNull(key, "Key must not be null");
		
		YamlMapping nestedMapping = new YamlMapping();
		YamlMapping currentMapping = (YamlMapping) this.getCurrentContext().element;
		currentMapping.add(key, nestedMapping);
		
		this.contextStack.push(new ContextFrame(BuilderContext.MAPPING, nestedMapping));
		return this;
	}
	
	/**
	 * Starts building a nested mapping with an anchor in the current mapping.<br>
	 * Use {@link #endMapping()} to return to the parent context.
	 *
	 * @param key The key for the nested mapping
	 * @param anchorName The anchor name for later reference
	 * @return This builder for method chaining, now in mapping context
	 * @throws IllegalStateException If the current context is not a mapping
	 * @throws NullPointerException If key or anchorName is null
	 */
	public @NonNull YamlBuilder startMappingWithAnchor(@NonNull String key, @NonNull String anchorName) {
		this.ensureMappingContext();
		Objects.requireNonNull(key, "Key must not be null");
		Objects.requireNonNull(anchorName, "Anchor name must not be null");
		
		YamlMapping nestedMapping = new YamlMapping();
		YamlMapping currentMapping = (YamlMapping) this.getCurrentContext().element;
		currentMapping.add(key, new YamlAnchor(anchorName, nestedMapping));
		
		this.contextStack.push(new ContextFrame(BuilderContext.MAPPING, nestedMapping));
		return this;
	}
	
	/**
	 * Starts building a nested mapping in the current sequence.<br>
	 * Use {@link #endMapping()} to return to the parent context.
	 *
	 * @return This builder for method chaining, now in mapping context
	 * @throws IllegalStateException If the current context is not a sequence
	 */
	public @NonNull YamlBuilder startMapping() {
		this.ensureSequenceContext();
		
		YamlMapping nestedMapping = new YamlMapping();
		YamlSequence currentSequence = (YamlSequence) this.getCurrentContext().element;
		currentSequence.add(nestedMapping);
		
		this.contextStack.push(new ContextFrame(BuilderContext.MAPPING, nestedMapping));
		return this;
	}
	
	/**
	 * Starts building a nested mapping with an anchor in the current sequence.<br>
	 * Use {@link #endMapping()} to return to the parent context.
	 *
	 * @param anchorName The anchor name for later reference
	 * @return This builder for method chaining, now in mapping context
	 * @throws IllegalStateException If the current context is not a sequence
	 * @throws NullPointerException If anchorName is null
	 */
	public @NonNull YamlBuilder startMappingWithAnchor(@NonNull String anchorName) {
		this.ensureSequenceContext();
		Objects.requireNonNull(anchorName, "Anchor name must not be null");
		
		YamlMapping nestedMapping = new YamlMapping();
		YamlSequence currentSequence = (YamlSequence) this.getCurrentContext().element;
		currentSequence.add(new YamlAnchor(anchorName, nestedMapping));
		
		this.contextStack.push(new ContextFrame(BuilderContext.MAPPING, nestedMapping));
		return this;
	}
	
	/**
	 * Ends the current mapping building context and returns to the parent context.<br>
	 *
	 * @return This builder for method chaining, now in the parent context
	 * @throws IllegalStateException If the current context is not a mapping or if no parent context exists
	 */
	public @NonNull YamlBuilder endMapping() {
		ContextFrame current = this.getCurrentContext();
		if (current.type != BuilderContext.MAPPING) {
			throw new IllegalStateException("Current context is not a mapping");
		}
		
		if (this.contextStack.size() == 1) {
			throw new IllegalStateException("Cannot end root mapping context. Use build() instead.");
		}
		
		this.contextStack.pop();
		return this;
	}
	//endregion
	
	//region Nesting - start nested sequence
	
	/**
	 * Starts building a nested sequence with the specified key in the current mapping.<br>
	 * Use {@link #endSequence()} to return to the parent context.
	 *
	 * @param key The key for the nested sequence
	 * @return This builder for method chaining, now in sequence context
	 * @throws IllegalStateException If the current context is not a mapping
	 * @throws NullPointerException If the key is null
	 */
	public @NonNull YamlBuilder startSequence(@NonNull String key) {
		this.ensureMappingContext();
		Objects.requireNonNull(key, "Key must not be null");
		
		YamlSequence nestedSequence = new YamlSequence();
		YamlMapping currentMapping = (YamlMapping) this.getCurrentContext().element;
		currentMapping.add(key, nestedSequence);
		
		this.contextStack.push(new ContextFrame(BuilderContext.SEQUENCE, nestedSequence));
		return this;
	}
	
	/**
	 * Starts building a nested sequence with an anchor in the current mapping.<br>
	 * Use {@link #endSequence()} to return to the parent context.
	 *
	 * @param key The key for the nested sequence
	 * @param anchorName The anchor name for later reference
	 * @return This builder for method chaining, now in sequence context
	 * @throws IllegalStateException If the current context is not a mapping
	 * @throws NullPointerException If key or anchorName is null
	 */
	public @NonNull YamlBuilder startSequenceWithAnchor(@NonNull String key, @NonNull String anchorName) {
		this.ensureMappingContext();
		Objects.requireNonNull(key, "Key must not be null");
		Objects.requireNonNull(anchorName, "Anchor name must not be null");
		
		YamlSequence nestedSequence = new YamlSequence();
		YamlMapping currentMapping = (YamlMapping) this.getCurrentContext().element;
		currentMapping.add(key, new YamlAnchor(anchorName, nestedSequence));
		
		this.contextStack.push(new ContextFrame(BuilderContext.SEQUENCE, nestedSequence));
		return this;
	}
	
	/**
	 * Starts building a nested sequence in the current sequence.<br>
	 * Use {@link #endSequence()} to return to the parent context.
	 *
	 * @return This builder for method chaining, now in sequence context
	 * @throws IllegalStateException If the current context is not a sequence
	 */
	public @NonNull YamlBuilder startSequence() {
		this.ensureSequenceContext();
		
		YamlSequence nestedSequence = new YamlSequence();
		YamlSequence currentSequence = (YamlSequence) this.getCurrentContext().element;
		currentSequence.add(nestedSequence);
		
		this.contextStack.push(new ContextFrame(BuilderContext.SEQUENCE, nestedSequence));
		return this;
	}
	
	/**
	 * Starts building a nested sequence with an anchor in the current sequence.<br>
	 * Use {@link #endSequence()} to return to the parent context.
	 *
	 * @param anchorName The anchor name for later reference
	 * @return This builder for method chaining, now in sequence context
	 * @throws IllegalStateException If the current context is not a sequence
	 * @throws NullPointerException If anchorName is null
	 */
	public @NonNull YamlBuilder startSequenceWithAnchor(@NonNull String anchorName) {
		this.ensureSequenceContext();
		Objects.requireNonNull(anchorName, "Anchor name must not be null");
		
		YamlSequence nestedSequence = new YamlSequence();
		YamlSequence currentSequence = (YamlSequence) this.getCurrentContext().element;
		currentSequence.add(new YamlAnchor(anchorName, nestedSequence));
		
		this.contextStack.push(new ContextFrame(BuilderContext.SEQUENCE, nestedSequence));
		return this;
	}
	
	/**
	 * Ends the current sequence building context and returns to the parent context.<br>
	 *
	 * @return This builder for method chaining, now in the parent context
	 * @throws IllegalStateException If the current context is not a sequence or if no parent context exists
	 */
	public @NonNull YamlBuilder endSequence() {
		ContextFrame current = this.getCurrentContext();
		if (current.type != BuilderContext.SEQUENCE) {
			throw new IllegalStateException("Current context is not a sequence");
		}
		
		if (this.contextStack.size() == 1) {
			throw new IllegalStateException("Cannot end root sequence context. Use build() instead.");
		}
		
		this.contextStack.pop();
		return this;
	}
	//endregion
	
	//region Conditional adding
	
	/**
	 * Conditionally adds a key-value pair to the current mapping only if the condition is true.<br>
	 *
	 * @param condition The condition to evaluate
	 * @param key The key for the value
	 * @param value The value to add if condition is true
	 * @return This builder for method chaining
	 * @throws IllegalStateException If the current context is not a mapping
	 */
	public @NonNull YamlBuilder addIf(boolean condition, @NonNull String key, @Nullable String value) {
		if (condition) {
			this.add(key, value);
		}
		return this;
	}
	
	/**
	 * Conditionally adds a key-value pair to the current mapping only if the condition is true.<br>
	 *
	 * @param condition The condition to evaluate
	 * @param key The key for the value
	 * @param value The value to add if condition is true
	 * @return This builder for method chaining
	 * @throws IllegalStateException If the current context is not a mapping
	 */
	public @NonNull YamlBuilder addIf(boolean condition, @NonNull String key, boolean value) {
		if (condition) {
			this.add(key, value);
		}
		return this;
	}
	
	/**
	 * Conditionally adds a key-value pair to the current mapping only if the condition is true.<br>
	 *
	 * @param condition The condition to evaluate
	 * @param key The key for the value
	 * @param value The value to add if condition is true
	 * @return This builder for method chaining
	 * @throws IllegalStateException If the current context is not a mapping
	 */
	public @NonNull YamlBuilder addIf(boolean condition, @NonNull String key, @Nullable Number value) {
		if (condition) {
			this.add(key, value);
		}
		return this;
	}
	
	/**
	 * Conditionally adds an alias to the current mapping only if the condition is true.<br>
	 *
	 * @param condition The condition to evaluate
	 * @param key The key for the alias
	 * @param anchorName The anchor name to reference if condition is true
	 * @return This builder for method chaining
	 * @throws IllegalStateException If the current context is not a mapping
	 */
	public @NonNull YamlBuilder addAliasIf(boolean condition, @NonNull String key, @NonNull String anchorName) {
		if (condition) {
			this.addAlias(key, anchorName);
		}
		return this;
	}
	//endregion
	
	//region Build methods
	
	/**
	 * Builds and returns the constructed yaml element.<br>
	 *
	 * @return The constructed yaml element
	 * @throws IllegalStateException If there are unclosed nested contexts
	 */
	public @NonNull YamlElement build() {
		if (this.contextStack.size() != 1) {
			throw new IllegalStateException("Cannot build with unclosed nested contexts. Current nesting depth: " + (this.contextStack.size() - 1));
		}
		return this.root;
	}
	
	/**
	 * Builds and returns the constructed yaml element as a YamlMapping.<br>
	 *
	 * @return The constructed yaml mapping
	 * @throws IllegalStateException If there are unclosed nested contexts or if the root is not a mapping
	 */
	public @NonNull YamlMapping buildMapping() {
		YamlElement element = this.build();
		if (!(element instanceof YamlMapping mapping)) {
			throw new IllegalStateException("Root element is not a YamlMapping");
		}
		YamlMapping result = new YamlMapping();
		result.addAll(mapping);
		return result;
	}
	
	/**
	 * Builds and returns the constructed yaml element as a YamlSequence.<br>
	 *
	 * @return The constructed yaml sequence
	 * @throws IllegalStateException If there are unclosed nested contexts or if the root is not a sequence
	 */
	public @NonNull YamlSequence buildSequence() {
		YamlElement element = this.build();
		if (!(element instanceof YamlSequence sequence)) {
			throw new IllegalStateException("Root element is not a YamlSequence");
		}
		YamlSequence result = new YamlSequence();
		result.addAll(sequence);
		return result;
	}
	//endregion
	
	//region Utility methods
	
	/**
	 * Returns the current nesting depth of the builder.<br>
	 *
	 * @return The current nesting depth
	 */
	public int getNestingDepth() {
		return this.contextStack.size();
	}
	
	/**
	 * Checks if the builder is currently in a mapping context.
	 *
	 * @return True if the current context is a mapping, false otherwise
	 */
	public boolean isInMappingContext() {
		return !this.contextStack.isEmpty() && this.getCurrentContext().type == BuilderContext.MAPPING;
	}
	
	/**
	 * Checks if the builder is currently in a sequence context.
	 *
	 * @return True if the current context is a sequence, false otherwise
	 */
	public boolean isInSequenceContext() {
		return !this.contextStack.isEmpty() && this.getCurrentContext().type == BuilderContext.SEQUENCE;
	}
	
	/**
	 * Checks if the builder is at the root level (no nested contexts).
	 *
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
	 * Returns a string representation of the current yaml structure using the specified configuration.
	 *
	 * @param config The yaml configuration to use for formatting
	 * @return A string representation of the current yaml structure
	 * @throws NullPointerException If the config is null
	 */
	public @NonNull String toString(@NonNull YamlConfig config) {
		Objects.requireNonNull(config, "Config must not be null");
		return this.root.toString(config);
	}
	//endregion
	
	/**
	 * Enumeration of builder contexts to track whether we're building a mapping or sequence.
	 */
	private enum BuilderContext {
		/**
		 * Context for building a yaml mapping.<br>
		 */
		MAPPING,
		/**
		 * Context for building a yaml sequence.<br>
		 */
		SEQUENCE
	}
	
	/**
	 * Internal class to track nesting state.<br>
	 *
	 * @param type The type of context (mapping or sequence)
	 * @param element The yaml element associated with this context
	 */
	private record ContextFrame(@NonNull BuilderContext type, @NonNull YamlElement element) {
		
		/**
		 * Constructs a new context frame with the specified type and element.<br>
		 *
		 * @param type The type of context (mapping or sequence)
		 * @param element The yaml element associated with this context
		 * @throws NullPointerException If the type or element is null
		 */
		private ContextFrame {
			Objects.requireNonNull(type, "Context type must not be null");
			Objects.requireNonNull(element, "Context element must not be null");
		}
	}
}
