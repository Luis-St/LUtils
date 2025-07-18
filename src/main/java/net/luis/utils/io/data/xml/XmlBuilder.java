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

package net.luis.utils.io.data.xml;

import net.luis.utils.util.Version;
import org.jetbrains.annotations.*;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * A fluent builder for creating complex XML structures with ease.<br>
 * This builder provides a chainable API for constructing XML documents, containers, values, and elements
 * with support for attributes, nested structures, and various XML configurations.<br>
 *
 * <p><b>Basic Usage:</b></p>
 * <pre>{@code
 * XmlElement element = XmlBuilder.create()
 *     .element("test")
 *     .attribute("id", "1")
 *     .build();
 * }</pre>
 *
 * <p><b>Complex Structures:</b></p>
 * <pre>{@code
 * XmlContainer root = XmlBuilder.create()
 *     .container("library")
 *         .attribute("name", "My Library")
 *         .container("books")
 *             .value("book", "Java Programming")
 *                 .attribute("id", "1")
 *                 .attribute("author", "John Doe")
 *             .value("book", "XML Processing")
 *                 .attribute("id", "2")
 *                 .attribute("author", "Jane Smith")
 *         .end()
 *         .value("count", 2)
 *     .buildContainer();
 * }</pre>
 *
 * <p><b>Full XML Documents:</b></p>
 * <pre>{@code
 * String xml = XmlBuilder.create()
 *     .container("root")
 *         .value("message", "Hello World")
 *     .toXml();
 * }</pre>
 *
 * @author Luis-St
 */
public final class XmlBuilder {
	
	/**
	 * The XML configuration used for building and formatting.<br>
	 */
	private final XmlConfig config;
	/**
	 * Stack to manage nested container contexts.<br>
	 */
	private final Deque<BuilderContext> contextStack = new ArrayDeque<>();
	/**
	 * The current element being built.<br>
	 */
	private @UnknownNullability XmlElement currentElement;
	
	/**
	 * Private constructor for creating a new XML builder with the specified configuration.<br>
	 *
	 * @param config The XML configuration to use
	 */
	private XmlBuilder(@NotNull XmlConfig config) {
		this.config = Objects.requireNonNull(config, "Config must not be null");
	}
	
	/**
	 * Creates a new XML builder with the default configuration.<br>
	 *
	 * @return A new XML builder instance
	 */
	public static @NotNull XmlBuilder create() {
		return new XmlBuilder(XmlConfig.DEFAULT);
	}
	
	/**
	 * Creates a new XML builder with the specified configuration.<br>
	 *
	 * @param config The XML configuration to use
	 * @return A new XML builder instance
	 * @throws NullPointerException If the config is null
	 */
	public static @NotNull XmlBuilder create(@NotNull XmlConfig config) {
		return new XmlBuilder(config);
	}
	
	/**
	 * Creates a new XML builder and immediately starts building a container with the specified name.<br>
	 *
	 * @param containerName The name of the root container
	 * @return A new XML builder instance with the container started
	 * @throws NullPointerException If the container name is null
	 */
	public static @NotNull XmlBuilder createContainer(@NotNull String containerName) {
		return new XmlBuilder(XmlConfig.DEFAULT).container(containerName);
	}
	
	/**
	 * Creates a new XML builder with the specified configuration and immediately starts building a container.<br>
	 *
	 * @param containerName The name of the root container
	 * @param config The XML configuration to use
	 * @return A new XML builder instance with the container started
	 * @throws NullPointerException If the container name or config is null
	 */
	public static @NotNull XmlBuilder createContainer(@NotNull String containerName, @NotNull XmlConfig config) {
		return new XmlBuilder(config).container(containerName);
	}
	
	/**
	 * Starts building a self-closing XML element with the specified name.<br>
	 * This element will have no content and will be rendered as {@code <name/>}.<br>
	 *
	 * @param name The name of the element
	 * @return This builder instance for method chaining
	 * @throws NullPointerException If the name is null
	 * @throws IllegalStateException If another element is currently being built
	 */
	public @NotNull XmlBuilder element(@NotNull String name) {
		Objects.requireNonNull(name, "Name must not be null");
		this.ensureNoCurrentElement();
		this.currentElement = new XmlElement(name);
		return this;
	}
	
	/**
	 * Starts building a container XML element with the specified name.<br>
	 * Container elements can contain child elements and are rendered with opening and closing tags.<br>
	 * Use {@link #end()} to finish building this container and return to the parent context.<br>
	 *
	 * @param name The name of the container
	 * @return This builder instance for method chaining
	 * @throws NullPointerException If the name is null
	 * @throws IllegalStateException If another element is currently being built
	 */
	public @NotNull XmlBuilder container(@NotNull String name) {
		Objects.requireNonNull(name, "Name must not be null");
		this.ensureNoCurrentElement();
		XmlContainer container = new XmlContainer(name);
		this.contextStack.push(new BuilderContext(container));
		this.currentElement = container; // Set as current element so attributes can be added
		return this;
	}
	
	/**
	 * Starts building a value XML element with the specified name and boolean value.<br>
	 *
	 * @param name The name of the value element
	 * @param value The boolean value
	 * @return This builder instance for method chaining
	 * @throws NullPointerException If the name is null
	 * @throws IllegalStateException If another element is currently being built
	 */
	public @NotNull XmlBuilder value(@NotNull String name, boolean value) {
		Objects.requireNonNull(name, "Name must not be null");
		this.ensureNoCurrentElement();
		this.currentElement = new XmlValue(name, value);
		return this;
	}
	
	/**
	 * Starts building a value XML element with the specified name and number value.<br>
	 *
	 * @param name The name of the value element
	 * @param value The number value
	 * @return This builder instance for method chaining
	 * @throws NullPointerException If the name is null
	 * @throws IllegalStateException If another element is currently being built
	 */
	public @NotNull XmlBuilder value(@NotNull String name, @Nullable Number value) {
		Objects.requireNonNull(name, "Name must not be null");
		this.ensureNoCurrentElement();
		this.currentElement = new XmlValue(name, value);
		return this;
	}
	
	/**
	 * Starts building a value XML element with the specified name and string value.<br>
	 *
	 * @param name The name of the value element
	 * @param value The string value
	 * @return This builder instance for method chaining
	 * @throws NullPointerException If the name is null
	 * @throws IllegalStateException If another element is currently being built
	 */
	public @NotNull XmlBuilder value(@NotNull String name, @Nullable String value) {
		Objects.requireNonNull(name, "Name must not be null");
		this.ensureNoCurrentElement();
		this.currentElement = new XmlValue(name, value);
		return this;
	}
	
	/**
	 * Adds an attribute with the specified name and string value to the current element.<br>
	 *
	 * @param name The name of the attribute
	 * @param value The string value of the attribute
	 * @return This builder instance for method chaining
	 * @throws NullPointerException If the name is null
	 * @throws IllegalStateException If no element is currently being built
	 */
	public @NotNull XmlBuilder attribute(@NotNull String name, @Nullable String value) {
		Objects.requireNonNull(name, "Name must not be null");
		this.ensureCurrentElement();
		this.currentElement.addAttribute(name, value);
		return this;
	}
	
	/**
	 * Adds an attribute with the specified name and boolean value to the current element.<br>
	 *
	 * @param name The name of the attribute
	 * @param value The boolean value of the attribute
	 * @return This builder instance for method chaining
	 * @throws NullPointerException If the name is null
	 * @throws IllegalStateException If no element is currently being built
	 */
	public @NotNull XmlBuilder attribute(@NotNull String name, boolean value) {
		Objects.requireNonNull(name, "Name must not be null");
		this.ensureCurrentElement();
		this.currentElement.addAttribute(name, value);
		return this;
	}
	
	/**
	 * Adds an attribute with the specified name and number value to the current element.<br>
	 *
	 * @param name The name of the attribute
	 * @param value The number value of the attribute
	 * @return This builder instance for method chaining
	 * @throws NullPointerException If the name is null
	 * @throws IllegalStateException If no element is currently being built
	 */
	public @NotNull XmlBuilder attribute(@NotNull String name, @Nullable Number value) {
		Objects.requireNonNull(name, "Name must not be null");
		this.ensureCurrentElement();
		this.currentElement.addAttribute(name, value);
		return this;
	}
	
	/**
	 * Adds multiple attributes to the current element using a configurator function.<br>
	 * This is useful for adding many attributes in a clean, readable way.<br>
	 *
	 * <pre>{@code
	 * builder.element("test")
	 *     .attributes(attrs -> {
	 *         attrs.attribute("id", "1");
	 *         attrs.attribute("name", "test");
	 *         attrs.attribute("active", true);
	 *     });
	 * }</pre>
	 *
	 * @param configurator A function that receives this builder to configure attributes
	 * @return This builder instance for method chaining
	 * @throws NullPointerException If the configurator is null
	 * @throws IllegalStateException If no element is currently being built
	 */
	public @NotNull XmlBuilder attributes(@NotNull Consumer<XmlBuilder> configurator) {
		Objects.requireNonNull(configurator, "Configurator must not be null");
		this.ensureCurrentElement();
		configurator.accept(this);
		return this;
	}
	
	/**
	 * Adds a pre-built XML element to the current container.<br>
	 * This allows mixing builder-created elements with manually created ones.<br>
	 *
	 * @param element The element to add
	 * @return This builder instance for method chaining
	 * @throws NullPointerException If the element is null
	 * @throws IllegalStateException If not currently building a container or if another element is being built
	 */
	public @NotNull XmlBuilder add(@NotNull XmlElement element) {
		Objects.requireNonNull(element, "Element must not be null");
		this.ensureNoCurrentElement();
		this.ensureInContainer();
		this.getCurrentContainer().add(element);
		return this;
	}
	
	/**
	 * Adds a child element using a configurator function.<br>
	 * This creates a new builder context for the child element.<br>
	 *
	 * <pre>{@code
	 * builder.container("parent")
	 *     .child(child -> child
	 *         .value("name", "John")
	 *         .attribute("id", "1")
	 *     );
	 * }</pre>
	 *
	 * @param configurator A function that receives a new builder to configure the child element
	 * @return This builder instance for method chaining
	 * @throws NullPointerException If the configurator is null
	 * @throws IllegalStateException If not currently building a container or if another element is being built
	 */
	public @NotNull XmlBuilder child(@NotNull Consumer<XmlBuilder> configurator) {
		Objects.requireNonNull(configurator, "Configurator must not be null");
		this.ensureNoCurrentElement();
		this.ensureInContainer();
		
		XmlBuilder childBuilder = new XmlBuilder(this.config);
		configurator.accept(childBuilder);
		
		if (childBuilder.currentElement != null) {
			this.getCurrentContainer().add(childBuilder.currentElement);
		}
		
		return this;
	}
	
	/**
	 * Finishes building the current container and returns to the parent context.<br>
	 * If this is the root container, it becomes available for building.<br>
	 *
	 * @return This builder instance for method chaining
	 * @throws IllegalStateException If not currently building a container
	 */
	public @NotNull XmlBuilder end() {
		if (this.contextStack.isEmpty()) {
			throw new IllegalStateException("No container context to end");
		}
		
		BuilderContext context = this.contextStack.peek();
		
		// Complete any current element before ending the container
		if (this.currentElement != null && this.currentElement != context.container) {
			// Current element is a child element, add it to the container
			context.container.add(this.currentElement);
		}
		// If currentElement is the container itself, we just clear it
		this.currentElement = null;
		
		// Pop the completed container
		this.contextStack.pop();
		
		if (this.contextStack.isEmpty()) {
			// No more parent containers, set the completed container as currentElement
			this.currentElement = context.container;
		} else {
			// Add the completed container to its parent
			this.getCurrentContainer().add(context.container);
			// currentElement remains null
		}
		
		return this;
	}
	
	/**
	 * Builds and returns the current XML element.<br>
	 * This method completes the building process and returns the constructed element.<br>
	 *
	 * @return The built XML element
	 * @throws IllegalStateException If no element has been built or if there are unclosed containers
	 */
	public @NotNull XmlElement build() {
		if (!this.contextStack.isEmpty()) {
			throw new IllegalStateException("Unclosed container contexts. Call end() to close all containers.");
		}
		if (this.currentElement == null) {
			throw new IllegalStateException("No element to build. Start building an element first.");
		}
		XmlElement result = this.currentElement;
		this.currentElement = null; // Clear after building to prevent reuse
		return result;
	}
	
	/**
	 * Builds and returns the current XML element as a container.<br>
	 * This is a convenience method that ensures the built element is a container.<br>
	 *
	 * @return The built XML container
	 * @throws IllegalStateException If no element has been built, if there are unclosed containers, or if the element is not a container
	 */
	public @NotNull XmlContainer buildContainer() {
		XmlElement element = this.build();
		if (!(element instanceof XmlContainer)) {
			throw new IllegalStateException("Built element is not a container: " + element.getClass().getSimpleName());
		}
		return (XmlContainer) element;
	}
	
	/**
	 * Builds and returns the current XML element as a value.<br>
	 * This is a convenience method that ensures the built element is a value.<br>
	 *
	 * @return The built XML value
	 * @throws IllegalStateException If no element has been built, if there are unclosed containers, or if the element is not a value
	 */
	public @NotNull XmlValue buildValue() {
		XmlElement element = this.build();
		if (!(element instanceof XmlValue)) {
			throw new IllegalStateException("Built element is not a value: " + element.getClass().getSimpleName());
		}
		return (XmlValue) element;
	}
	
	/**
	 * Builds the XML element and converts it to an XML string representation.<br>
	 * This method creates a complete XML document with declaration and the built element.<br>
	 *
	 * @return The XML string representation
	 * @throws IllegalStateException If no element has been built or if there are unclosed containers
	 */
	public @NotNull String toXml() {
		return this.toXml(new XmlDeclaration(Version.of(1, 0), this.config.charset()));
	}
	
	/**
	 * Builds the XML element and converts it to an XML string representation with a custom declaration.<br>
	 *
	 * @param declaration The XML declaration to use
	 * @return The XML string representation
	 * @throws NullPointerException If the declaration is null
	 * @throws IllegalStateException If no element has been built or if there are unclosed containers
	 */
	public @NotNull String toXml(@NotNull XmlDeclaration declaration) {
		Objects.requireNonNull(declaration, "Declaration must not be null");
		XmlElement element = this.build();
		
		StringBuilder xml = new StringBuilder();
		xml.append(declaration);
		if (this.config.prettyPrint()) {
			xml.append(System.lineSeparator());
		}
		xml.append(element.toString(this.config));
		
		return xml.toString();
	}
	
	/**
	 * Builds the XML element and returns it along with the declaration as an XML document container.<br>
	 * This creates a document-like structure that can be used for further processing.<br>
	 *
	 * @return An XML document representation
	 * @throws IllegalStateException If no element has been built or if there are unclosed containers
	 */
	public @NotNull XmlDocument toDocument() {
		return this.toDocument(new XmlDeclaration(Version.of(1, 0), this.config.charset()));
	}
	
	/**
	 * Builds the XML element and returns it along with a custom declaration as an XML document container.<br>
	 *
	 * @param declaration The XML declaration to use
	 * @return An XML document representation
	 * @throws NullPointerException If the declaration is null
	 * @throws IllegalStateException If no element has been built or if there are unclosed containers
	 */
	public @NotNull XmlDocument toDocument(@NotNull XmlDeclaration declaration) {
		Objects.requireNonNull(declaration, "Declaration must not be null");
		return new XmlDocument(declaration, this.build());
	}
	
	/**
	 * Returns the current XML configuration used by this builder.<br>
	 *
	 * @return The XML configuration
	 */
	public @NotNull XmlConfig getConfig() {
		return this.config;
	}
	
	/**
	 * Checks if the builder is currently building a container.<br>
	 *
	 * @return True if currently building a container, false otherwise
	 */
	public boolean isInContainer() {
		return !this.contextStack.isEmpty();
	}
	
	/**
	 * Returns the depth of nested containers currently being built.<br>
	 *
	 * @return The nesting depth (0 means no containers)
	 */
	public int getContainerDepth() {
		return this.contextStack.size();
	}
	
	/**
	 * Ensures that no element is currently being built.<br>
	 * If an element is currently being built and we're in a container context,
	 * the current element will be automatically added to the container.<br>
	 *
	 * @throws IllegalStateException If an element is currently being built and we're not in a container context
	 */
	private void ensureNoCurrentElement() {
		if (this.currentElement != null) {
			if (!this.isInContainer()) {
				throw new IllegalStateException("Another element is currently being built. Call build() or end() first.");
			} else {
				// Check if currentElement is the same as the current container
				// If so, we're just transitioning from adding attributes to adding children
				if (this.currentElement == this.getCurrentContainer()) {
					this.currentElement = null; // Clear but don't add to itself
				} else {
					// Different element, add it to the container
					this.getCurrentContainer().add(this.currentElement);
					this.currentElement = null;
				}
			}
		}
	}
	
	/**
	 * Ensures that an element is currently being built.<br>
	 *
	 * @throws IllegalStateException If no element is currently being built
	 */
	private void ensureCurrentElement() {
		if (this.currentElement == null) {
			throw new IllegalStateException("No element is currently being built. Start building an element first.");
		}
	}
	
	/**
	 * Ensures that we are currently building a container.<br>
	 *
	 * @throws IllegalStateException If not currently building a container
	 */
	private void ensureInContainer() {
		if (this.contextStack.isEmpty()) {
			throw new IllegalStateException("Not currently building a container. Start a container first.");
		}
	}
	
	/**
	 * Gets the current container being built.<br>
	 *
	 * @return The current container
	 * @throws IllegalStateException If not currently building a container
	 */
	private @NotNull XmlContainer getCurrentContainer() {
		this.ensureInContainer();
		return this.contextStack.peek().container;
	}
	
	/**
	 * Internal class to track container building context.<br>
	 *
	 * @param container The container being built
	 */
	private record BuilderContext(@NotNull XmlContainer container) {
		
		/**
		 * Creates a new builder context for the specified container.<br>
		 *
		 * @param container The container being built
		 * @throws NullPointerException If the container is null
		 */
		private BuilderContext {
			Objects.requireNonNull(container, "Container must not be null");
		}
	}
}
