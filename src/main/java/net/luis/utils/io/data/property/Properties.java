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

package net.luis.utils.io.data.property;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.*;

import java.util.*;

/**
 * Represents a collection of properties.<br>
 * This class is immutable and is used for reading properties only.<br>
 *
 * @author Luis-St
 */
public class Properties {
	
	/**
	 * The internal map of properties.<br>
	 * For performance reasons, the properties are stored with their keys as the map key.<br>
	 */
	private final Map<String, Property> properties = Maps.newLinkedHashMap();
	
	/**
	 * Constructs a new properties instance from a list of properties.<br>
	 * @param properties The list of properties to construct the instance from
	 * @throws NullPointerException If the list of properties is null
	 */
	public Properties(@NotNull List<Property> properties) {
		Objects.requireNonNull(properties, "Properties must not be null");
		properties.forEach(property -> this.properties.put(property.getKey(), property));
	}
	
	/**
	 * Constructs a new properties instance from a map of properties
	 * @param properties The map of properties to construct the instance from
	 * @throws NullPointerException If the map of properties is null
	 */
	public Properties(@NotNull Map<String, Property> properties) {
		Objects.requireNonNull(properties, "Properties must not be null");
		this.properties.putAll(properties);
	}
	
	//region Static helper methods
	
	/**
	 * Copies a property and removes the specified subgroup from the key.<br>
	 * If the subgroup is null or empty, the property is copied without any changes.<br>
	 * @param property The property to copy
	 * @param subgroup The subgroup to remove from the key
	 * @return The copied property with the subgroup removed from the key
	 * @throws NullPointerException If the property is null
	 * @throws IllegalArgumentException If the property is not part of the specified subgroup
	 */
	private static @NotNull Property copyPropertyAndRemoveGroup(@NotNull Property property, @Nullable String subgroup) {
		Objects.requireNonNull(property, "Property must not be null");
		if (!property.isPartOfGroup(subgroup)) {
			throw new IllegalArgumentException("Property '" + property.getKey() + "' is not part of subgroup '" + subgroup + "'");
		}
		if (StringUtils.isEmpty(subgroup)) {
			return Property.of(property.getKey(), property.getRawValue());
		}
		String key = property.getKey();
		if (key.startsWith(subgroup.endsWith(".") ? subgroup : subgroup + ".")) {
			key = key.substring(subgroup.length() + 1);
		}
		return Property.of(key, property.getRawValue());
	}
	//endregion
	
	/**
	 * Returns the number of properties in this instance.<br>
	 * @return The number of properties
	 */
	public int size() {
		return this.properties.size();
	}
	
	/**
	 * Returns an unmodifiable collection of all properties in this instance.<br>
	 * @return A collection of all properties
	 */
	public @NotNull @Unmodifiable Collection<Property> getProperties() {
		return Collections.unmodifiableCollection(this.properties.values());
	}
	
	/**
	 * Checks if this instance contains a property with the specified key.<br>
	 * @param key The key to check for
	 * @return True if a property with the specified key exists, otherwise false
	 * @throws NullPointerException If the key is null
	 */
	public boolean hasProperty(@NotNull String key) {
		Objects.requireNonNull(key, "Key must not be null");
		return this.properties.containsKey(key);
	}
	
	/**
	 * Returns the property with the specified key or null if no such property exists.<br>
	 * @param key The key of the property to return
	 * @return The property with the specified key or null
	 * @throws NullPointerException If the key is null
	 */
	public @Nullable Property getProperty(@NotNull String key) {
		Objects.requireNonNull(key, "Key must not be null");
		return this.properties.get(key);
	}
	
	/**
	 * Returns all properties that are part of the specified subgroup.<br>
	 * If the subgroup is null or empty, all properties are returned.<br>
	 * The properties are copied and the subgroup is removed from the key.<br>
	 * @param subgroup The subgroup to get the properties for
	 * @return A new properties instance containing all properties that are part of the specified subgroup
	 */
	public @NotNull Properties getPropertiesOfSubgroup(@Nullable String subgroup) {
		List<Property> properties = Lists.newArrayList();
		this.properties.values().forEach(property -> {
			if (property.isPartOfGroup(subgroup)) {
				properties.add(copyPropertyAndRemoveGroup(property, subgroup));
			}
		});
		return new Properties(properties);
	}
	
	/**
	 * Returns a map of all properties grouped by their subgroups.<br>
	 * The map returned can either contain nested maps or simple key-value pairs.<br>
	 * @return A map of all properties grouped by their subgroups
	 */
	@SuppressWarnings("unchecked")
	public @NotNull Map<String, Object> getGroupedMap() {
		Map<String, Object> map = Maps.newLinkedHashMap();
		this.properties.forEach((key, property) -> {
			String[] keyParts = key.split("\\.");
			Map<String, Object> currentMap = map;
			for (int i = 0; i < keyParts.length - 1; i++) {
				currentMap = (Map<String, Object>) currentMap.computeIfAbsent(keyParts[i], k -> Maps.newLinkedHashMap());
			}
			currentMap.put(keyParts[keyParts.length - 1], property.getAsString());
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
	//endregion
}
