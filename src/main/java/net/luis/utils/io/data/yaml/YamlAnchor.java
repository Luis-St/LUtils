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

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Represents a yaml anchor that marks an element for reuse.<br>
 * The anchor wraps another element and assigns it a name that can be referenced via aliases.<br>
 *
 * @author Luis-St
 */
public class YamlAnchor implements YamlElement {

	/**
	 * The name of this anchor.<br>
	 */
	private final String name;
	/**
	 * The element wrapped by this anchor.<br>
	 */
	private final YamlElement element;

	/**
	 * Constructs a new yaml anchor with the given name and element.<br>
	 *
	 * @param name The anchor name (without the '&amp;' prefix)
	 * @param element The element to anchor
	 * @throws NullPointerException If the name or element is null
	 * @throws IllegalArgumentException If the name is blank or the element is a YamlAlias
	 */
	public YamlAnchor(@NotNull String name, @NotNull YamlElement element) {
		this.name = Objects.requireNonNull(name, "Anchor name must not be null");
		this.element = Objects.requireNonNull(element, "Anchored element must not be null");
		if (name.isBlank()) {
			throw new IllegalArgumentException("Anchor name must not be blank");
		}
		if (!isValidAnchorName(name)) {
			throw new IllegalArgumentException("Invalid anchor name: '" + name + "'. Anchor names must contain only alphanumeric characters, underscores, and hyphens");
		}
		if (element instanceof YamlAlias) {
			throw new IllegalArgumentException("Cannot anchor an alias");
		}
	}

	/**
	 * Checks if the given name is a valid anchor name.<br>
	 * Valid anchor names contain only alphanumeric characters, underscores, and hyphens.<br>
	 *
	 * @param name The name to check
	 * @return True if the name is valid, false otherwise
	 */
	private static boolean isValidAnchorName(@NotNull String name) {
		for (int i = 0; i < name.length(); i++) {
			char c = name.charAt(i);
			if (!Character.isLetterOrDigit(c) && c != '_' && c != '-') {
				return false;
			}
		}
		return true;
	}

	/**
	 * Returns the anchor name.<br>
	 * @return The anchor name without the '&amp;' prefix
	 */
	public @NotNull String getName() {
		return this.name;
	}

	/**
	 * Returns the anchored element.<br>
	 * @return The wrapped element
	 */
	public @NotNull YamlElement getElement() {
		return this.element;
	}

	/**
	 * Unwraps the anchor and returns the underlying element.<br>
	 * If the element is also an anchor, it recursively unwraps.<br>
	 * @return The innermost non-anchor element
	 */
	@Override
	public @NotNull YamlElement unwrap() {
		if (this.element instanceof YamlAnchor anchor) {
			return anchor.unwrap();
		}
		return this.element;
	}

	//region Object overrides
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof YamlAnchor that)) return false;

		return this.name.equals(that.name) && this.element.equals(that.element);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.name, this.element);
	}

	@Override
	public String toString() {
		return this.toString(YamlConfig.DEFAULT);
	}

	@Override
	public @NotNull String toString(@NotNull YamlConfig config) {
		Objects.requireNonNull(config, "Config must not be null");
		String elementStr = this.element.toString(config);

		// For block style collections, the anchor goes before the element
		if (this.element instanceof YamlMapping || this.element instanceof YamlSequence) {
			if (config.useBlockStyle() && !elementStr.startsWith("{") && !elementStr.startsWith("[")) {
				// Block style: &anchor\n  element content
				return "&" + this.name + System.lineSeparator() + elementStr;
			}
		}

		// For scalars and flow style, anchor is inline
		return "&" + this.name + " " + elementStr;
	}
	//endregion
}
