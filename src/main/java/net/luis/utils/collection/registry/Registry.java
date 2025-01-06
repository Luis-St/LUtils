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

package net.luis.utils.collection.registry;

import com.google.common.collect.Maps;
import net.luis.utils.collection.registry.key.KeyGenerator;
import net.luis.utils.collection.registry.key.RegistryKey;
import net.luis.utils.exception.ModificationException;
import net.luis.utils.exception.NoSuchItemException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;

/**
 * A registry is a collection of items that can be accessed by a unique id.<br>
 * The unique id is a {@link UUID} and is generated when an item is registered.<br>
 * If the registry is freezable, no modifications are allowed after it has been frozen.<br>
 *
 * @author Luis-St
 *
 * @param <K> The type of the registry key
 * @param <I> The type of the items in the registry
 */
public class Registry<K extends RegistryKey<?>, I> implements Iterable<I> {
	
	/**
	 * The registry mapTo that contains the items and their unique ids.<br>
	 */
	private final Map<K, I> registry = Maps.newHashMap();
	/**
	 * The key generator of the registry.<br>
	 */
	private final KeyGenerator<K> keyGenerator;
	/**
	 * Whether the registry is freezable or not.<br>
	 */
	private final boolean freezable;
	/**
	 * Whether the registry is frozen or not.<br>
	 */
	private boolean frozen;
	
	/**
	 * Constructs a new registry.<br>
	 * @param keyGenerator The key generator of the registry
	 * @param freezable Whether the registry is freezable or not
	 * @throws NullPointerException If the key generator is null
	 */
	protected Registry(@NotNull KeyGenerator<K> keyGenerator, boolean freezable) {
		this.keyGenerator = Objects.requireNonNull(keyGenerator, "Key generator must not be null");
		this.freezable = freezable;
	}
	
	//region Static factory methods
	
	/**
	 * Creates a new unfreezable registry with the given key generator.<br>
	 * @param keyGenerator The key generator of the registry
	 * @return The created registry
	 * @param <K> The type of the registry key
	 * @param <I> The type of the items in the registry
	 * @throws NullPointerException If the key generator is null
	 */
	public static <K extends RegistryKey<?>, I> @NotNull Registry<K, I> of(@NotNull KeyGenerator<K> keyGenerator) {
		return new Registry<>(keyGenerator, false);
	}
	
	/**
	 * Creates a registry from a list of items.<br>
	 * The items will be registered in the order they are in the list.<br>
	 * The registry will be frozen after all items have been registered.<br>
	 * @param keyGenerator The key generator of the registry
	 * @param items The items to register
	 * @return The created frozen registry containing the items
	 * @param <K> The type of the registry key
	 * @param <I> The type of the items
	 */
	public static <K extends RegistryKey<?>, I> @NotNull Registry<K, I> of(@NotNull KeyGenerator<K> keyGenerator, @NotNull List<I> items) {
		Registry<K, I> registry = freezable(keyGenerator);
		Objects.requireNonNull(items, "Items must not be null").forEach(registry::register);
		return registry.freeze();
	}
	
	/**
	 * Creates a new freezable registry with the given key generator.<br>
	 * @param keyGenerator The key generator of the registry
	 * @return The created registry
	 * @param <K> The type of the registry key
	 * @param <I> The type of the items in the registry
	 * @throws NullPointerException If the key generator is null
	 */
	public static <K extends RegistryKey<?>, I> @NotNull Registry<K, I> freezable(@NotNull KeyGenerator<K> keyGenerator) {
		return new Registry<>(keyGenerator, true);
	}
	//endregion
	
	/**
	 * Checks whether the registry is frozen or not.<br>
	 * @throws ModificationException If the registry is frozen
	 */
	private void checkFrozen() {
		if (this.frozen) {
			throw new ModificationException("Registry is frozen, no modifications are allowed");
		}
	}
	
	/**
	 * Registers an item in the registry.<br>
	 * @param item The item to register
	 * @return The generated key of the item
	 * @throws ModificationException If the registry is frozen
	 * @throws NullPointerException If the generated key or the item is null
	 */
	public @NotNull K register(@NotNull I item) {
		this.checkFrozen();
		K key = Objects.requireNonNull(this.keyGenerator.generateKey(), "Generated key must not be null");
		this.registry.put(key, Objects.requireNonNull(item, "Item must not be null"));
		return key;
	}
	
	/**
	 * Registers the item returned by the register function in the registry.<br>
	 * @param function The function that returns the item to register
	 * @return The generated key of the item
	 * @throws ModificationException If the registry is frozen
	 * @throws NullPointerException If the generated key or the register function is null
	 */
	public @NotNull K register(@NotNull Function<K, I> function) {
		this.checkFrozen();
		K key = Objects.requireNonNull(this.keyGenerator.generateKey(), "Generated key must not be null");
		this.registry.put(key, Objects.requireNonNull(function, "Register function must not be null").apply(key));
		return key;
	}
	
	/**
	 * Removes the item with the given key from the registry.<br>
	 * @param key The key of the item to remove
	 * @return True if an item was removed, false otherwise
	 * @throws ModificationException If the registry is frozen
	 */
	public boolean remove(@Nullable K key) {
		this.checkFrozen();
		return this.registry.remove(key) != null;
	}
	
	/**
	 * Gets the item with the given key from the registry.<br>
	 * @param key The key of the item to get
	 * @return The item with the given key or null if no item was found
	 * @throws NullPointerException If the key is null
	 */
	public @Nullable I get(@NotNull K key) {
		return this.registry.get(Objects.requireNonNull(key, "Key must not be null"));
	}
	
	/**
	 * Gets the item with the given key from the registry.<br>
	 * @param key The key of the item to get
	 * @return The item with the given key
	 * @throws NullPointerException If the key is null
	 * @throws NoSuchItemException If no item with the given key was found
	 */
	public @NotNull I getOrThrow(@NotNull K key) {
		I item = this.get(key);
		if (item == null) {
			throw new NoSuchItemException("No item with key '" + key + "' found");
		}
		return item;
	}
	
	/**
	 * Gets the key of the given item from the registry.<br>
	 * @param item The item to get the key of
	 * @return The key of the given item
	 */
	public @Nullable K getKey(@Nullable I item) {
		return this.registry.entrySet().stream().filter(entry -> entry.getValue().equals(item)).map(Map.Entry::getKey).findFirst().orElse(null);
	}
	
	/**
	 * Checks whether the registry contains an item with the given key.<br>
	 * @param key The key to check
	 * @return True if the registry contains an item with the given key, false otherwise
	 */
	public boolean contains(@Nullable K key) {
		return this.registry.containsKey(key);
	}
	
	/**
	 * Checks whether the registry contains the given item.<br>
	 * @param item The item to check
	 * @return True if the registry contains the given item, false otherwise
	 */
	public boolean contains(@Nullable I item) {
		return this.registry.containsValue(item);
	}
	
	/**
	 * Gets the keys of all items in the registry.<br>
	 * @return A set of all keys in the registry
	 */
	public @NotNull Set<K> getKeys() {
		return this.registry.keySet();
	}
	
	/**
	 * Gets all items in the registry.<br>
	 * @return A collection of all items in the registry
	 */
	public @NotNull Collection<I> getItems() {
		return this.registry.values();
	}
	
	/**
	 * @return An iterator over all items in the registry
	 */
	@Override
	public @NotNull Iterator<I> iterator() {
		return this.getItems().iterator();
	}
	
	/**
	 * Checks whether the registry is empty or not.<br>
	 * @return True if the registry is empty, false otherwise
	 */
	public boolean isEmpty() {
		return this.registry.isEmpty();
	}
	
	/**
	 * Returns the number of items in the registry.<br>
	 * @return The size of the registry
	 */
	public int size() {
		return this.registry.size();
	}
	
	/**
	 * Clears the registry if it is not frozen.<br>
	 * @throws ModificationException If the registry is frozen
	 */
	public void clear() {
		this.checkFrozen();
		this.registry.clear();
	}
	
	/**
	 * Freezes the registry if it is freezable.<br>
	 * @return The registry
	 */
	public @NotNull Registry<K, I> freeze() {
		if (this.freezable) {
			this.frozen = true;
		}
		return this;
	}
	
	/**
	 * Checks whether the registry is frozen or not.<br>
	 * @return True if the registry is frozen, false otherwise
	 */
	public boolean isFrozen() {
		return this.frozen;
	}
	
	//region Object overrides
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Registry<?, ?> registry)) return false;
		
		if (this.freezable != registry.freezable) return false;
		if (this.frozen != registry.frozen) return false;
		return this.registry.equals(registry.registry);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.registry, this.freezable);
	}
	
	@Override
	public String toString() {
		return (this.frozen ? "Frozen " : "") + "Registry" + this.registry;
	}
	//endregion
}
