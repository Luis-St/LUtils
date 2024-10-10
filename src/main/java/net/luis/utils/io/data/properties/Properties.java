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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.*;

import java.util.*;

/**
 *
 * @author Luis-St
 *
 */

public class Properties {
	
	private final Map<String, Property> properties = Maps.newLinkedHashMap();
	
	public Properties() {}
	
	public Properties(@NotNull List<Property> properties) {
		Objects.requireNonNull(properties, "Properties must not be null");
		properties.forEach(property -> this.properties.put(property.getKey(), property));
	}
	
	public Properties(@NotNull Map<String, Property> properties) {
		Objects.requireNonNull(properties, "Properties must not be null");
		this.properties.putAll(properties);
	}
	
	//region Static helper methods
	private static @NotNull Property copyPropertyAndRemoveGroup(@NotNull Property property, @Nullable String group) {
		if (!property.isPartOfGroup(group)) {
			throw new IllegalArgumentException("Property is not part of group");
	private static @NotNull Property copyPropertyAndRemoveGroup(@NotNull Property property, @Nullable String subgroup) {
		if (!property.isPartOfGroup(subgroup)) {
			throw new IllegalArgumentException("Property '" + property.getKey() + "' is not part of subgroup '" + subgroup + "'");
		}
		if (StringUtils.isEmpty(subgroup)) {
			return Property.of(property.getKey(), property.getRawValue());
		}
		String key = property.getKey();
		if (key.startsWith(group.endsWith(".") ? group : group + ".")) {
			key = key.substring(group.length() + 1);
		if (key.startsWith(subgroup.endsWith(".") ? subgroup : subgroup + ".")) {
			key = key.substring(subgroup.length() + 1);
		}
		return Property.of(key, property.getRawValue());
	}
	//endregion
	
	public @NotNull @Unmodifiable Collection<Property> getProperties() {
		return Collections.unmodifiableCollection(this.properties.values());
	}
	
	public boolean hasProperty(@NotNull String key) {
		Objects.requireNonNull(key, "Key must not be null");
		return this.properties.containsKey(key);
	}
	
	public @UnknownNullability Property getProperty(@NotNull String key) {
	public @Nullable Property getProperty(@NotNull String key) {
		Objects.requireNonNull(key, "Key must not be null");
		return this.properties.get(key);
	}
	
	public @NotNull Properties getGroupedProperties(@Nullable String group) {
	public @NotNull Properties getSubProperties(@Nullable String subgroup) {
		List<Property> properties = Lists.newArrayList();
		this.properties.values().forEach(property -> {
			if (property.isPartOfGroup(group)) {
				properties.add(copyPropertyAndRemoveGroup(property, group));
			if (property.isPartOfGroup(subgroup)) {
			}
		});
		return new Properties(properties);
	}
	
	@SuppressWarnings("unchecked")
	public @NotNull Map<String, Object> getGroupedMap() {
		Map<String, Object> map = Maps.newLinkedHashMap();
		this.properties.forEach((key, property) -> {
			String[] keyParts = key.split("\\.");
			Map<String, Object> currentMap = map;
			for (int i = 0; i < keyParts.length - 1; i++) {
				currentMap = (Map<String, Object>) currentMap.computeIfAbsent(keyParts[i], k -> Maps.newLinkedHashMap());
			}
			currentMap.put(keyParts[keyParts.length - 1], property.getString());
		});
		return map;
	}
	
	//region Object overrides
	@Override
	public boolean equals(Object object) {
		if (this == object) return true;
		if (!(object instanceof Properties that)) return false;
		
		return this.properties.equals(that.properties);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.properties);
	}
	
	@Override
	public String toString() {
		return this.properties.toString();
	}
	
	public @NotNull String toString(@NotNull PropertyConfig config) {
		StringBuilder builder = new StringBuilder();
		this.properties.forEach((key, property) -> {
			builder.append(property.toString(config)).append(System.lineSeparator());
		});
		return builder.toString();
	}
	//endregion
}
