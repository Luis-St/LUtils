/*
 * LUtils
 * Copyright (C) 2024 Luis Staudt
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

package net.luis.utils.io.data.properties;

import net.luis.utils.io.data.util.DefaultValueGetter;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 *
 * @author Luis-St
 *
 */

public class Property implements DefaultValueGetter {
	
	private final String key;
	private final String value;
	
	private Property(@NotNull String key, @NotNull String value) {
		this.key = Objects.requireNonNull(key, "Key must not be null");
		this.value = Objects.requireNonNull(value, "Value must not be null");
	}
	
	public static @NotNull Property of(@NotNull String key, @NotNull String value) {
		return new Property(key, value);
	}
	
	public final @NotNull String getKey() {
		return this.key;
	}
	
	public final @NotNull String getRawValue() {
		return this.value;
	}
	
	@Override
	public final @NotNull String getString() {
		return this.value;
	}
	
	public boolean isPartOfGroup(@Nullable String group) {
		if (StringUtils.isEmpty(group)) {
			return true;
		}
		if (group.isBlank()) {
			throw new IllegalArgumentException("Group must not be blank");
		}
		if (group.startsWith(".")) {
			throw new IllegalArgumentException("Group must not start with a dot");
		}
		if (!group.endsWith(".")) {
			group += ".";
		}
		return this.key.startsWith(group);
	}
	
	//region Object overrides
	@Override
	public boolean equals(Object object) {
		if (this == object) return true;
		if (!(object instanceof Property property)) return false;
		
		if (!this.key.equals(property.key)) return false;
		return this.value.equals(property.value);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.key, this.value);
	}
	
	@Override
	public String toString() {
		return this.key + "=" + this.value;
	}
	
	public @NotNull String toString(@NotNull PropertyConfig config) {
		return this.key + config.separator() + this.value;
	}
	//endregion
}
