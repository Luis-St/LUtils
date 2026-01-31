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

package net.luis.utils.io.data.yaml;

import org.jspecify.annotations.NonNull;

import java.util.Objects;

/**
 * Represents a yaml alias that references an anchored element.<br>
 * When resolved, the alias points to the element defined by the corresponding anchor.<br>
 *
 * @author Luis-St
 * @param anchorName
The name of the anchor this alias references.<br>
 */
public record YamlAlias(String anchorName) implements YamlElement {
	
	/**
	 * Constructs a new yaml alias referencing the given anchor name.<br>
	 *
	 * @param anchorName The name of the anchor to reference (without the '*' prefix)
	 * @throws NullPointerException If the anchor name is null
	 * @throws IllegalArgumentException If the anchor name is blank or contains invalid characters
	 */
	public YamlAlias(@NonNull String anchorName) {
		this.anchorName = Objects.requireNonNull(anchorName, "Anchor name must not be null");
		
		if (anchorName.isBlank()) {
			throw new IllegalArgumentException("Anchor name must not be blank");
		}
		if (!YamlHelper.isValidAnchorName(anchorName)) {
			throw new IllegalArgumentException("Invalid anchor name: '" + anchorName + "'. Anchor names must contain only alphanumeric characters, underscores, and hyphens");
		}
	}
	
	/**
	 * Returns the name of the referenced anchor.<br>
	 * @return The anchor name without the '*' prefix
	 */
	@Override
	public @NonNull String anchorName() {
		return this.anchorName;
	}
	
	//region Object overrides
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof YamlAlias that)) return false;
		
		return this.anchorName.equals(that.anchorName);
	}
	
	@Override
	public @NonNull String toString() {
		return this.toString(YamlConfig.DEFAULT);
	}
	
	@Override
	public @NonNull String toString(@NonNull YamlConfig config) {
		return "*" + this.anchorName;
	}
	//endregion
}
