package net.luis.utils.collection;

import com.google.common.collect.Maps;
import net.luis.utils.exception.ModificationException;
import net.luis.utils.exception.NoSuchItemException;
import net.luis.utils.util.Utils;
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
 * @param <T> The type of the items in the registry.
 */
public class Registry<T> implements Iterable<T> {
	
	/**
	 * The registry map that contains the items and their unique ids.
	 */
	private final Map<UUID, T> registry = Maps.newHashMap();
	/**
	 * Whether the registry is freezable or not.
	 */
	private final boolean freezable;
	/**
	 * Whether the registry is frozen or not.
	 */
	private boolean frozen;
	
	/**
	 * Constructs a new registry.<br>
	 * @param freezable Whether the registry is freezable or not.
	 */
	private Registry(boolean freezable) {
		this.freezable = freezable;
	}
	
	//region Static factory methods
	
	/**
	 * Creates a new unfreezable registry.<br>
	 * @return A new unfreezable registry.
	 * @param <T> The type of the items in the registry.
	 */
	public static <T> @NotNull Registry<T> of() {
		return new Registry<>(false);
	}
	
	/**
	 * Creates a new freezable registry.<br>
	 * @return A new freezable registry.
	 * @param <T> The type of the items in the registry.
	 */
	public static <T> @NotNull Registry<T> freezable() {
		return new Registry<>(true);
	}
	
	/**
	 * Creates a registry from a list of items.<br>
	 * The items will be registered in the order they are in the list.<br>
	 * The registry will be frozen after all items have been registered.<br>
	 * @param items The items to register.
	 * @return The frozen registry with the items.
	 * @param <T> The type of the items.
	 */
	public static <T> @NotNull Registry<T> of(@NotNull List<T> items) {
		Registry<T> registry = Registry.freezable();
		Objects.requireNonNull(items, "Items must not be null").forEach(registry::register);
		return registry.freeze();
	}
	//endregion
	
	/**
	 * Checks whether the registry is frozen or not.<br>
	 * If the registry is frozen, a {@link ModificationException} will be thrown.
	 */
	private void checkFrozen() {
		if (this.frozen) {
			throw new ModificationException("Registry is frozen, no modifications are allowed");
		}
	}
	
	/**
	 * Registers an item in the registry.<br>
	 * @param item The item to register.
	 * @return The unique id of the item.
	 * @throws ModificationException If the registry is frozen.
	 * @throws NullPointerException If the item is null.
	 */
	public @NotNull UUID register(@NotNull T item) {
		this.checkFrozen();
		UUID uniqueId = UUID.randomUUID();
		this.registry.put(uniqueId, Objects.requireNonNull(item, "Item must not be null"));
		return uniqueId;
	}
	
	/**
	 * Registers the item returned by the register function in the registry.<br>
	 * @param function The function that returns the item to register.
	 * @return The unique id of the item.
	 * @throws ModificationException If the registry is frozen.
	 * @throws NullPointerException If the register function is null.
	 */
	public @NotNull UUID register(@NotNull Function<UUID, T> function) {
		this.checkFrozen();
		UUID uniqueId = UUID.randomUUID();
		this.registry.put(uniqueId, Objects.requireNonNull(function, "Register function must not be null").apply(uniqueId));
		return uniqueId;
	}
	
	/**
	 * Removes the item with the given unique id from the registry.<br>
	 * @param uniqueId The unique id of the item to remove.
	 * @return True if an item was removed, false otherwise.
	 * @throws ModificationException If the registry is frozen.
	 */
	public boolean remove(@Nullable UUID uniqueId) {
		this.checkFrozen();
		return this.registry.remove(uniqueId) != null;
	}
	
	/**
	 * Gets the item with the given unique id from the registry.<br>
	 * @param uniqueId The unique id of the item to get.
	 * @return The item with the given unique id or null if no item was found.
	 * @throws NullPointerException If the unique id is null.
	 */
	public @Nullable T get(@NotNull UUID uniqueId) {
		return this.registry.get(Objects.requireNonNull(uniqueId, "Unique id must not be null"));
	}
	
	/**
	 * Gets the item with the given unique id from the registry.<br>
	 * @param uniqueId The unique id of the item to get.
	 * @return The item with the given unique id.
	 * @throws NullPointerException If the unique id is null.
	 * @throws NoSuchItemException If no item with the given unique id was found.
	 */
	public @NotNull T getOrThrow(@NotNull UUID uniqueId) {
		T item = this.get(uniqueId);
		if (item == null) {
			throw new NoSuchItemException("No item with unique id '" + uniqueId + "' found");
		}
		return item;
	}
	
	/**
	 * Gets the unique id of the given item from the registry.<br>
	 * @param item The item to get the unique id of.
	 * @return The unique id of the given item or {@link Utils#EMPTY_UUID} if no item was found.
	 */
	public @NotNull UUID getUniqueId(@Nullable T item) {
		return this.registry.entrySet().stream().filter(entry -> entry.getValue().equals(item)).map(Map.Entry::getKey).findFirst().orElse(Utils.EMPTY_UUID);
	}
	
	/**
	 * Checks whether the registry contains an item with the given unique id.<br>
	 * @param uniqueId The unique id to check.
	 * @return True if the registry contains an item with the given unique id, false otherwise.
	 */
	public boolean contains(@Nullable UUID uniqueId) {
		return this.registry.containsKey(uniqueId);
	}
	
	/**
	 * Checks whether the registry contains the given item.<br>
	 * @param item The item to check.
	 * @return True if the registry contains the given item, false otherwise.
	 */
	public boolean contains(@Nullable T item) {
		return this.registry.containsValue(item);
	}
	
	/**
	 * Gets the unique ids of all items in the registry.<br>
	 * @return A set of all unique ids in the registry.
	 */
	public @NotNull Set<UUID> getKeys() {
		return this.registry.keySet();
	}
	
	/**
	 * Gets all items in the registry.<br>
	 * @return A collection of all items in the registry.
	 */
	public @NotNull Collection<T> getItems() {
		return this.registry.values();
	}
	
	/**
	 * @return An iterator over all items in the registry.
	 */
	@Override
	public @NotNull Iterator<T> iterator() {
		return this.getItems().iterator();
	}
	
	/**
	 * Checks whether the registry is empty or not.<br>
	 * @return True if the registry is empty, false otherwise.
	 */
	public boolean isEmpty() {
		return this.registry.isEmpty();
	}
	
	/**
	 * @return The size of the registry.
	 */
	public int size() {
		return this.registry.size();
	}
	
	/**
	 * Clears the registry if it is not frozen.<br>
	 * @throws ModificationException If the registry is frozen.
	 */
	public void clear() {
		this.checkFrozen();
		this.registry.clear();
	}
	
	/**
	 * Freezes the registry if it is freezable.<br>
	 * @return The registry.
	 */
	public @NotNull Registry<T> freeze() {
		if (this.freezable) {
			this.frozen = true;
		}
		return this;
	}
	
	/**
	 * Checks whether the registry is frozen or not.<br>
	 * @return True if the registry is frozen, false otherwise.
	 */
	public boolean isFrozen() {
		return this.frozen;
	}
	
	//region Object overrides
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Registry<?> registry1)) return false;
		
		if (this.freezable != registry1.freezable) return false;
		if (this.frozen != registry1.frozen) return false;
		return this.registry.equals(registry1.registry);
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
