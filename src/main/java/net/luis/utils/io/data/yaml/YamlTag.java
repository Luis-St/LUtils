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
 *
 * @author Luis-St
 *
 */

public enum YamlTag {
	
	NULL("null", YamlNull.INSTANCE),
	BOOL("bool", new YamlScalar(false)),
	INT("int", new YamlScalar(0)),
	FLOAT("float", new YamlScalar(0.0)),
	STR("str", new YamlScalar("")),
	BINARY("binary", new YamlScalar("")),
	TIMESTAMP("timestamp", new YamlScalar("")),
	SEQUENCE("seq", new YamlSequence()),
	MAPPING("map", new YamlMapping());
	
	private final String tag;
	private final YamlNode defaultValue;
	
	YamlTag(@NotNull String tag, @NotNull YamlNode defaultValue) {
		this.tag = Objects.requireNonNull(tag, "Tag must not be null");
		this.defaultValue = Objects.requireNonNull(defaultValue, "Default value must not be null");
	}
	
	public static @NotNull YamlTag fromString(@NotNull String tag) {
		Objects.requireNonNull(tag, "Tag must not be null");
		for (YamlTag yamlTag : values()) {
			if (yamlTag.tag.equalsIgnoreCase(tag)) {
				return yamlTag;
			}
		}
		throw new IllegalArgumentException("Unknown tag: " + tag);
	}
	
	public @NotNull String getTag() {
		return this.tag;
	}
	
	public @NotNull YamlNode getDefaultValue() {
		return this.defaultValue;
	}
	
	//region Object overrides
	@Override
	public String toString() {
		return this.tag;
	}
	//endregion
}
