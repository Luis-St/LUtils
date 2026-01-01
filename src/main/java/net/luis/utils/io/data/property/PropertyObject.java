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

package net.luis.utils.io.data.property;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.luis.utils.io.data.property.exception.NoSuchPropertyException;
import net.luis.utils.io.data.property.exception.PropertyTypeException;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.function.BiConsumer;

/**
 * Represents a property object.<br>
 * A property object is an ordered set of name/value pairs.<br>
 * A name is a string and a value can be any property element, including property null.<br>
 *
 * @author Luis-St
 */
public class PropertyObject implements PropertyElement {
	
	/**
	 * The internal map of elements.<br>
	 * The order of the elements is preserved.<br>
	 */
	private final Map<String, PropertyElement> elements = Maps.newLinkedHashMap();
	
	/**
	 * Constructs an empty property object.<br>
	 */
	public PropertyObject() {}
	
	/**
	 * Constructs a property object with the given elements.<br>
	 *
	 * @param elements The map of elements to add
	 * @throws NullPointerException If the given elements are null
	 */
	public PropertyObject(@NonNull Map<String, ? extends PropertyElement> elements) {
		this.elements.putAll(Objects.requireNonNull(elements, "Property elements must not be null"));
	}
	
	/**
	 * Checks if the given key belongs to the specified group (prefix).<br>
	 * Uses dot-notation: "app.database" is part of group "app".<br>
	 *
	 * @param key The key to check
	 * @param group The group prefix to check against
	 * @return True if the key belongs to the group, false otherwise
	 */
	private static boolean isKeyInGroup(@NonNull String key, @Nullable String group) {
		if (StringUtils.isEmpty(group)) {
			return true;
		}
		String prefix = group.endsWith(".") ? group : group + ".";
		return key.startsWith(prefix);
	}
	
	/**
	 * Removes the group prefix from the given key.<br>
	 *
	 * @param key The key to process
	 * @param group The group prefix to remove
	 * @return The key with the group prefix removed
	 */
	private static @NonNull String removeGroupPrefix(@NonNull String key, @Nullable String group) {
		if (StringUtils.isEmpty(group)) {
			return key;
		}
		String prefix = group.endsWith(".") ? group : group + ".";
		if (key.startsWith(prefix)) {
			return key.substring(prefix.length());
		}
		return key;
	}
	
	/**
	 * Converts a property element to a plain Java value for the nested map.<br>
	 */
	private static @Nullable Object elementToValue(@Nullable PropertyElement element) {
		if (element == null || element.isPropertyNull()) {
			return null;
		} else if (element instanceof PropertyValue value) {
			if (value.isBoolean()) {
				return value.getAsBoolean();
			} else if (value.isNumber()) {
				return value.getAsNumber();
			} else {
				return value.getAsString();
			}
		} else if (element instanceof PropertyArray array) {
			List<Object> list = new ArrayList<>();
			for (PropertyElement e : array) {
				list.add(elementToValue(e));
			}
			return list;
		} else if (element instanceof PropertyObject object) {
			return object.toNestedMap();
		}
		return element.toString();
	}
	
	/**
	 * Creates a PropertyObject from a nested Map structure.<br>
	 * Flattens to dot-notation keys.<br>
	 *
	 * @param map The nested map to convert
	 * @return A new PropertyObject with flattened keys
	 * @throws NullPointerException If the map is null
	 */
	public static @NonNull PropertyObject fromNestedMap(@NonNull Map<String, Object> map) {
		Objects.requireNonNull(map, "Map must not be null");
		PropertyObject result = new PropertyObject();
		flattenMap("", map, result);
		return result;
	}
	
	/**
	 * Helper method to recursively flatten a nested map.<br>
	 */
	@SuppressWarnings("unchecked")
	private static void flattenMap(@NonNull String prefix, @NonNull Map<String, Object> map, @NonNull PropertyObject result) {
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			String key = prefix.isEmpty() ? entry.getKey() : prefix + "." + entry.getKey();
			Object value = entry.getValue();
			
			if (value instanceof Map) {
				flattenMap(key, (Map<String, Object>) value, result);
			} else if (value instanceof List) {
				PropertyArray array = new PropertyArray();
				for (Object item : (List<?>) value) {
					array.add(valueToElement(item));
				}
				result.add(key, array);
			} else {
				result.add(key, valueToElement(value));
			}
		}
	}
	
	/**
	 * Converts a plain Java value to a property element.<br>
	 */
	@SuppressWarnings("unchecked")
	private static @NonNull PropertyElement valueToElement(@Nullable Object value) {
		if (value == null) {
			return PropertyNull.INSTANCE;
		} else if (value instanceof Boolean bool) {
			return new PropertyValue(bool);
		} else if (value instanceof Number number) {
			return new PropertyValue(number);
		} else if (value instanceof String string) {
			return new PropertyValue(string);
		} else if (value instanceof Map) {
			return fromNestedMap((Map<String, Object>) value);
		} else if (value instanceof List) {
			PropertyArray array = new PropertyArray();
			for (Object item : (List<?>) value) {
				array.add(valueToElement(item));
			}
			return array;
		}
		return new PropertyValue(value.toString());
	}
	
	/**
	 * Helper method to recursively flatten property elements.<br>
	 */
	private static void flattenElement(@NonNull String prefix, @NonNull PropertyObject object, @NonNull PropertyObject result) {
		for (Map.Entry<String, PropertyElement> entry : object.elements.entrySet()) {
			String key = prefix.isEmpty() ? entry.getKey() : prefix + "." + entry.getKey();
			PropertyElement value = entry.getValue();
			
			if (value instanceof PropertyObject nested) {
				flattenElement(key, nested, result);
			} else {
				result.add(key, value);
			}
		}
	}
	
	/**
	 * Helper method to insert a value into a nested property structure.<br>
	 */
	private static void insertNested(@NonNull PropertyObject current, String @NonNull [] keyParts, int index, @NonNull PropertyElement value) {
		if (index == keyParts.length - 1) {
			current.add(keyParts[index], value);
		} else {
			PropertyElement existing = current.get(keyParts[index]);
			PropertyObject nested;
			if (existing instanceof PropertyObject obj) {
				nested = obj;
			} else {
				nested = new PropertyObject();
				current.add(keyParts[index], nested);
			}
			insertNested(nested, keyParts, index + 1, value);
		}
	}
	
	/**
	 * Returns the number of elements in this property object.<br>
	 * @return The size of this property object
	 */
	public int size() {
		return this.elements.size();
	}
	
	/**
	 * Checks if this property object is empty.<br>
	 * @return True if this property object is empty, false otherwise
	 */
	public boolean isEmpty() {
		return this.elements.isEmpty();
	}
	
	/**
	 * Checks if this property object contains the given key.<br>
	 *
	 * @param key The key to check
	 * @return True if this property object contains the given key, false otherwise
	 */
	public boolean containsKey(@Nullable String key) {
		return this.elements.containsKey(key);
	}
	
	/**
	 * Checks if this property object contains the given element.<br>
	 *
	 * @param element The element to check
	 * @return True if this property object contains the given element, false otherwise
	 */
	public boolean containsValue(@Nullable PropertyElement element) {
		return this.elements.containsValue(element);
	}
	
	/**
	 * Returns the set of keys in this property object.<br>
	 * @return The keys of this property object
	 */
	public @NonNull Set<String> keySet() {
		return this.elements.keySet();
	}
	
	/**
	 * Returns the collection of values in this property object.<br>
	 * @return The values of this property object
	 */
	public @NonNull @Unmodifiable Collection<PropertyElement> elements() {
		return Collections.unmodifiableCollection(this.elements.values());
	}
	
	/**
	 * Returns the set of entries in this property object.<br>
	 * @return The entries of this property object
	 */
	public @NonNull Set<Map.Entry<String, PropertyElement>> entrySet() {
		return this.elements.entrySet();
	}
	
	/**
	 * Iterates over the entries of this property object and applies the given action to each entry.<br>
	 *
	 * @param action The action to apply to each entry
	 * @throws NullPointerException If the given action is null
	 */
	public void forEach(@NonNull BiConsumer<? super String, ? super PropertyElement> action) {
		this.elements.forEach(Objects.requireNonNull(action, "Action must not be null"));
	}
	
	/**
	 * Adds the given element with the given key to this property object.<br>
	 * If the element is null, it will be replaced with property null.<br>
	 * If the key is already present, the element will be replaced.<br>
	 *
	 * @param key The key to add
	 * @param element The element to add
	 * @return The previous element associated with the key, or null if the key was not present
	 * @throws NullPointerException If the given key is null
	 */
	public @Nullable PropertyElement add(@NonNull String key, @Nullable PropertyElement element) {
		Objects.requireNonNull(key, "Key must not be null");
		return this.elements.put(key, element == null ? PropertyNull.INSTANCE : element);
	}
	
	/**
	 * Adds the given string with the given key to this property object.<br>
	 * If the string is null, it will be replaced with property null.<br>
	 * The string value will be converted to a property value.<br>
	 *
	 * @param key The key to add
	 * @param value The string value to add
	 * @return The previous element associated with the key, or null if the key was not present
	 * @throws NullPointerException If the given key is null
	 * @see #add(String, PropertyElement)
	 */
	public @Nullable PropertyElement add(@NonNull String key, @Nullable String value) {
		return this.add(key, value == null ? null : new PropertyValue(value));
	}
	
	/**
	 * Adds the given boolean with the given key to this property object.<br>
	 * The boolean will be converted to a property value.<br>
	 *
	 * @param key The key to add
	 * @param value The boolean value to add
	 * @return The previous element associated with the key, or null if the key was not present
	 * @throws NullPointerException If the given key is null
	 * @see #add(String, PropertyElement)
	 */
	public @Nullable PropertyElement add(@NonNull String key, boolean value) {
		return this.add(key, new PropertyValue(value));
	}
	
	/**
	 * Adds the given number with the given key to this property object.<br>
	 * If the number is null, it will be replaced with property null.<br>
	 * The number value will be converted to a property value.<br>
	 *
	 * @param key The key to add
	 * @param value The number value to add
	 * @return The previous element associated with the key, or null if the key was not present
	 * @throws NullPointerException If the given key is null
	 * @see #add(String, PropertyElement)
	 */
	public @Nullable PropertyElement add(@NonNull String key, @Nullable Number value) {
		return this.add(key, value == null ? null : new PropertyValue(value));
	}
	
	/**
	 * Adds the given byte with the given key to this property object.<br>
	 * The byte will be converted to a property value.<br>
	 *
	 * @param key The key to add
	 * @param value The byte value to add
	 * @return The previous element associated with the key, or null if the key was not present
	 * @throws NullPointerException If the given key is null
	 * @see #add(String, PropertyElement)
	 */
	public @Nullable PropertyElement add(@NonNull String key, byte value) {
		return this.add(key, new PropertyValue(value));
	}
	
	/**
	 * Adds the given short with the given key to this property object.<br>
	 * The short will be converted to a property value.<br>
	 *
	 * @param key The key to add
	 * @param value The short value to add
	 * @return The previous element associated with the key, or null if the key was not present
	 * @throws NullPointerException If the given key is null
	 * @see #add(String, PropertyElement)
	 */
	public @Nullable PropertyElement add(@NonNull String key, short value) {
		return this.add(key, new PropertyValue(value));
	}
	
	/**
	 * Adds the given int with the given key to this property object.<br>
	 * The int will be converted to a property value.<br>
	 *
	 * @param key The key to add
	 * @param value The int value to add
	 * @return The previous element associated with the key, or null if the key was not present
	 * @throws NullPointerException If the given key is null
	 * @see #add(String, PropertyElement)
	 */
	public @Nullable PropertyElement add(@NonNull String key, int value) {
		return this.add(key, new PropertyValue(value));
	}
	
	/**
	 * Adds the given long with the given key to this property object.<br>
	 * The long will be converted to a property value.<br>
	 *
	 * @param key The key to add
	 * @param value The long value to add
	 * @return The previous element associated with the key, or null if the key was not present
	 * @throws NullPointerException If the given key is null
	 * @see #add(String, PropertyElement)
	 */
	public @Nullable PropertyElement add(@NonNull String key, long value) {
		return this.add(key, new PropertyValue(value));
	}
	
	/**
	 * Adds the given float with the given key to this property object.<br>
	 * The float will be converted to a property value.<br>
	 *
	 * @param key The key to add
	 * @param value The float value to add
	 * @return The previous element associated with the key, or null if the key was not present
	 * @throws NullPointerException If the given key is null
	 * @see #add(String, PropertyElement)
	 */
	public @Nullable PropertyElement add(@NonNull String key, float value) {
		return this.add(key, new PropertyValue(value));
	}
	
	/**
	 * Adds the given double with the given key to this property object.<br>
	 * The double will be converted to a property value.<br>
	 *
	 * @param key The key to add
	 * @param value The double value to add
	 * @return The previous element associated with the key, or null if the key was not present
	 * @throws NullPointerException If the given key is null
	 * @see #add(String, PropertyElement)
	 */
	public @Nullable PropertyElement add(@NonNull String key, double value) {
		return this.add(key, new PropertyValue(value));
	}
	
	/**
	 * Adds all elements from the given property object to this property object.<br>
	 *
	 * @param object The property object of elements to add
	 * @throws NullPointerException If the given property object is null
	 */
	public void addAll(@NonNull PropertyObject object) {
		this.elements.putAll(Objects.requireNonNull(object, "Property object must not be null").elements);
	}
	
	/**
	 * Adds all elements from the given map to this property object.<br>
	 *
	 * @param elements The map of elements to add
	 * @throws NullPointerException If the given elements are null
	 */
	public void addAll(@NonNull Map<String, ? extends PropertyElement> elements) {
		this.elements.putAll(Objects.requireNonNull(elements, "Property elements must not be null"));
	}
	
	/**
	 * Removes the element with the given key from this property object.<br>
	 *
	 * @param key The key to remove
	 * @return The element associated with the key, or null if the key was not present
	 */
	public @Nullable PropertyElement remove(@Nullable String key) {
		return this.elements.remove(key);
	}
	
	/**
	 * Removes all element pairs from this property object.<br>
	 */
	public void clear() {
		this.elements.clear();
	}
	
	/**
	 * Replaces the element with the given key in this property object with the new given element.<br>
	 * If the given element is null, it will be replaced with property null.<br>
	 *
	 * @param key The key to replace
	 * @param newElement The new element to replace with
	 * @return The previous element associated with the key, or null if the key was not present
	 * @throws NullPointerException If the given key is null
	 */
	public @Nullable PropertyElement replace(@NonNull String key, @Nullable PropertyElement newElement) {
		Objects.requireNonNull(key, "Key must not be null");
		return this.elements.replace(key, newElement == null ? PropertyNull.INSTANCE : newElement);
	}
	
	/**
	 * Replaces the given element with the given key in this property object with the new given element.<br>
	 *
	 * @param key The key to replace
	 * @param oldElement The old element to replace
	 * @param newElement The new element to replace with
	 * @return True if the element was replaced, false otherwise
	 * @throws NullPointerException If the given key or old element is null
	 */
	public boolean replace(@NonNull String key, @NonNull PropertyElement oldElement, @Nullable PropertyElement newElement) {
		Objects.requireNonNull(key, "Key must not be null");
		Objects.requireNonNull(oldElement, "Old value must not be null");
		
		return this.elements.replace(key, oldElement, newElement == null ? PropertyNull.INSTANCE : newElement);
	}
	
	/**
	 * Gets the element with the given key from this property object.<br>
	 *
	 * @param key The key to get
	 * @return The element associated with the key, or null if the key was not present
	 * @throws NullPointerException If the given key is null
	 */
	public @Nullable PropertyElement get(@NonNull String key) {
		Objects.requireNonNull(key, "Key must not be null");
		return this.elements.get(key);
	}
	
	/**
	 * Gets the element with the given key from this property object as a property object.<br>
	 *
	 * @param key The key to get
	 * @return The element associated with the key as a property object
	 * @throws NullPointerException If the given key is null
	 * @throws NoSuchPropertyException If no element was found for the given key
	 * @throws PropertyTypeException If the element is not a property object
	 * @see #get(String)
	 */
	public @NonNull PropertyObject getAsPropertyObject(@NonNull String key) {
		PropertyElement element = this.get(key);
		
		if (element == null) {
			throw new NoSuchPropertyException("Expected property object for key '" + key + "', but found none");
		}
		if (element instanceof PropertyObject object) {
			return object;
		}
		
		return element.getAsPropertyObject();
	}
	
	/**
	 * Gets the element with the given key from this property object as a property array.<br>
	 *
	 * @param key The key to get
	 * @return The element associated with the key as a property array
	 * @throws NullPointerException If the given key is null
	 * @throws NoSuchPropertyException If no element was found for the given key
	 * @throws PropertyTypeException If the element is not a property array
	 * @see #get(String)
	 */
	public @NonNull PropertyArray getAsPropertyArray(@NonNull String key) {
		PropertyElement element = this.get(key);
		
		if (element == null) {
			throw new NoSuchPropertyException("Expected property array for key '" + key + "', but found none");
		}
		if (element instanceof PropertyArray array) {
			return array;
		}
		
		return element.getAsPropertyArray();
	}
	
	/**
	 * Gets the element with the given key from this property object as a property value.<br>
	 *
	 * @param key The key to get
	 * @return The element associated with the key as a property value
	 * @throws NullPointerException If the given key is null
	 * @throws NoSuchPropertyException If no element was found for the given key
	 * @throws PropertyTypeException If the element is not a property value
	 * @see #get(String)
	 */
	public @NonNull PropertyValue getPropertyValue(@NonNull String key) {
		PropertyElement element = this.get(key);
		if (element == null) {
			throw new NoSuchPropertyException("Expected property value for key '" + key + "', but found none");
		}
		if (element instanceof PropertyValue value) {
			return value;
		}
		return element.getAsPropertyValue();
	}
	
	/**
	 * Gets the element with the given key from this property object as a string.<br>
	 * The element will be converted to a property value and then to a string.<br>
	 *
	 * @param key The key to get
	 * @return The element associated with the key as a string
	 * @throws NullPointerException If the given key is null
	 * @throws NoSuchPropertyException If no element was found for the given key
	 * @throws PropertyTypeException If the element is not a string
	 * @see #getAsPropertyValue()
	 */
	public @NonNull String getAsString(@NonNull String key) {
		return this.getPropertyValue(key).getAsString();
	}
	
	//region Group/Namespace support
	
	/**
	 * Gets the element with the given key from this property object as a boolean.<br>
	 * The element will be converted to a property value and then to a boolean.<br>
	 *
	 * @param key The key to get
	 * @return The element associated with the key as a boolean
	 * @throws NullPointerException If the given key is null
	 * @throws NoSuchPropertyException If no element was found for the given key
	 * @throws PropertyTypeException If the element is not a boolean
	 * @see #getAsPropertyValue()
	 */
	public boolean getAsBoolean(@NonNull String key) {
		return this.getPropertyValue(key).getAsBoolean();
	}
	
	/**
	 * Gets the element with the given key from this property object as a number.<br>
	 * The element will be converted to a property value and then to a number.<br>
	 *
	 * @param key The key to get
	 * @return The element associated with the key as a number
	 * @throws NullPointerException If the given key is null
	 * @throws NoSuchPropertyException If no element was found for the given key
	 * @throws PropertyTypeException If the element is not a number
	 * @see #getAsPropertyValue()
	 */
	public @NonNull Number getAsNumber(@NonNull String key) {
		return this.getPropertyValue(key).getAsNumber();
	}
	
	/**
	 * Gets the element with the given key from this property object as a byte.<br>
	 * The element will be converted to a property value and then to a byte.<br>
	 *
	 * @param key The key to get
	 * @return The element associated with the key as a byte
	 * @throws NullPointerException If the given key is null
	 * @throws NoSuchPropertyException If no element was found for the given key
	 * @throws PropertyTypeException If the element is not a byte
	 * @see #getAsPropertyValue()
	 */
	public byte getAsByte(@NonNull String key) {
		return this.getPropertyValue(key).getAsByte();
	}
	
	/**
	 * Gets the element with the given key from this property object as a short.<br>
	 * The element will be converted to a property value and then to a short.<br>
	 *
	 * @param key The key to get
	 * @return The element associated with the key as a short
	 * @throws NullPointerException If the given key is null
	 * @throws NoSuchPropertyException If no element was found for the given key
	 * @throws PropertyTypeException If the element is not a short
	 * @see #getAsPropertyValue()
	 */
	public short getAsShort(@NonNull String key) {
		return this.getPropertyValue(key).getAsShort();
	}
	
	/**
	 * Gets the element with the given key from this property object as an integer.<br>
	 * The element will be converted to a property value and then to an integer.<br>
	 *
	 * @param key The key to get
	 * @return The element associated with the key as an integer
	 * @throws NullPointerException If the given key is null
	 * @throws NoSuchPropertyException If no element was found for the given key
	 * @throws PropertyTypeException If the element is not an integer
	 * @see #getAsPropertyValue()
	 */
	public int getAsInteger(@NonNull String key) {
		return this.getPropertyValue(key).getAsInteger();
	}
	
	/**
	 * Gets the element with the given key from this property object as a long.<br>
	 * The element will be converted to a property value and then to a long.<br>
	 *
	 * @param key The key to get
	 * @return The element associated with the key as a long
	 * @throws NullPointerException If the given key is null
	 * @throws NoSuchPropertyException If no element was found for the given key
	 * @throws PropertyTypeException If the element is not a long
	 * @see #getAsPropertyValue()
	 */
	public long getAsLong(@NonNull String key) {
		return this.getPropertyValue(key).getAsLong();
	}
	
	/**
	 * Gets the element with the given key from this property object as a float.<br>
	 * The element will be converted to a property value and then to a float.<br>
	 *
	 * @param key The key to get
	 * @return The element associated with the key as a float
	 * @throws NullPointerException If the given key is null
	 * @throws NoSuchPropertyException If no element was found for the given key
	 * @throws PropertyTypeException If the element is not a float
	 * @see #getAsPropertyValue()
	 */
	public float getAsFloat(@NonNull String key) {
		return this.getPropertyValue(key).getAsFloat();
	}
	
	/**
	 * Gets the element with the given key from this property object as a double.<br>
	 * The element will be converted to a property value and then to a double.<br>
	 *
	 * @param key The key to get
	 * @return The element associated with the key as a double
	 * @throws NullPointerException If the given key is null
	 * @throws NoSuchPropertyException If no element was found for the given key
	 * @throws PropertyTypeException If the element is not a double
	 * @see #getAsPropertyValue()
	 */
	public double getAsDouble(@NonNull String key) {
		return this.getPropertyValue(key).getAsDouble();
	}
	
	/**
	 * Checks if any key in this object belongs to the given group (prefix).<br>
	 * Uses dot-notation: "app.database" is part of group "app".<br>
	 *
	 * @param group The group prefix to check
	 * @return True if any key belongs to the group, false otherwise
	 */
	public boolean hasGroup(@NonNull String group) {
		Objects.requireNonNull(group, "Group must not be null");
		for (String key : this.elements.keySet()) {
			if (isKeyInGroup(key, group)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Returns a new PropertyObject containing only elements whose keys
	 * start with the given prefix. Keys have the prefix removed.<br>
	 * <p>
	 *     Example: getSubgroup("app") on {"app.name": "X", "app.port": 8080, "other": "Y"}
	 *     returns {"name": "X", "port": 8080}
	 * </p>
	 *
	 * @param prefix The group prefix to filter by (null or empty returns a copy of all elements)
	 * @return A new PropertyObject containing the filtered elements
	 */
	public @NonNull PropertyObject getSubgroup(@Nullable String prefix) {
		PropertyObject result = new PropertyObject();
		for (Map.Entry<String, PropertyElement> entry : this.elements.entrySet()) {
			String key = entry.getKey();
			if (isKeyInGroup(key, prefix)) {
				String newKey = removeGroupPrefix(key, prefix);
				result.add(newKey, entry.getValue());
			}
		}
		return result;
	}
	
	/**
	 * Returns all direct child group names at the current level.<br>
	 * <p>
	 *     Example: on {"app.db.host": "x", "app.db.port": 1, "app.cache.enabled": true}
	 *     getChildGroups("app") returns ["db", "cache"]
	 * </p>
	 *
	 * @param prefix The prefix to search under (null or empty for root level)
	 * @return A set of direct child group names
	 */
	public @NonNull Set<String> getChildGroups(@Nullable String prefix) {
		Set<String> groups = Sets.newLinkedHashSet();
		String normalizedPrefix = StringUtils.isEmpty(prefix) ? "" : (prefix.endsWith(".") ? prefix : prefix + ".");
		
		for (String key : this.elements.keySet()) {
			if (!key.startsWith(normalizedPrefix)) {
				continue;
			}
			String remainder = key.substring(normalizedPrefix.length());
			int dotIndex = remainder.indexOf('.');
			if (dotIndex > 0) {
				groups.add(remainder.substring(0, dotIndex));
			}
		}
		return groups;
	}
	
	/**
	 * Converts this flat property object to a nested Map structure.<br>
	 * Uses dot-notation to create hierarchy.<br>
	 * <p>
	 *     Example: {"a.b.c": 1, "a.b.d": 2} becomes {a: {b: {c: 1, d: 2}}}
	 * </p>
	 *
	 * @return A nested map representation of this property object
	 */
	@SuppressWarnings("unchecked")
	public @NonNull Map<String, Object> toNestedMap() {
		Map<String, Object> result = Maps.newLinkedHashMap();
		for (Map.Entry<String, PropertyElement> entry : this.elements.entrySet()) {
			String[] keyParts = entry.getKey().split("\\.");
			Map<String, Object> currentMap = result;
			for (int i = 0; i < keyParts.length - 1; i++) {
				currentMap = (Map<String, Object>) currentMap.computeIfAbsent(keyParts[i], k -> Maps.newLinkedHashMap());
			}
			currentMap.put(keyParts[keyParts.length - 1], elementToValue(entry.getValue()));
		}
		return result;
	}
	
	/**
	 * Flattens a hierarchical PropertyObject (with nested PropertyObjects)
	 * to a flat structure with dot-notation keys.<br>
	 *
	 * @return A new flattened PropertyObject
	 */
	public @NonNull PropertyObject flatten() {
		PropertyObject result = new PropertyObject();
		flattenElement("", this, result);
		return result;
	}
	
	/**
	 * Expands flat dot-notation keys into nested PropertyObjects.<br>
	 * <p>
	 *     Example: {"a.b": 1} becomes {"a": PropertyObject{"b": 1}}
	 * </p>
	 *
	 * @return A new PropertyObject with nested structure
	 */
	public @NonNull PropertyObject expand() {
		PropertyObject result = new PropertyObject();
		for (Map.Entry<String, PropertyElement> entry : this.elements.entrySet()) {
			String[] keyParts = entry.getKey().split("\\.");
			insertNested(result, keyParts, 0, entry.getValue());
		}
		return result;
	}
	//endregion
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof PropertyObject that)) return false;
		
		return this.elements.equals(that.elements);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.elements);
	}
	
	@Override
	public String toString() {
		return this.toString(PropertyConfig.DEFAULT);
	}
	
	@Override
	public @NonNull String toString(@NonNull PropertyConfig config) {
		Objects.requireNonNull(config, "Config must not be null");
		StringBuilder builder = new StringBuilder();
		String alignment = " ".repeat(config.alignment());
		
		boolean first = true;
		for (Map.Entry<String, PropertyElement> entry : this.elements.entrySet()) {
			if (!first) {
				builder.append(System.lineSeparator());
			}
			first = false;
			
			String key = entry.getKey();
			PropertyElement value = entry.getValue();
			
			if (value instanceof PropertyArray array) {
				builder.append(key).append(alignment).append(config.separator()).append(alignment);
				builder.append(array.toString(config));
			} else {
				builder.append(key).append(alignment).append(config.separator()).append(alignment);
				builder.append(value.toString(config));
			}
		}
		
		return builder.toString();
	}
}
