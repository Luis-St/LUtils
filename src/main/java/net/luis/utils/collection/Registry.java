package net.luis.utils.collection;

import com.google.common.collect.Maps;
import net.luis.utils.util.Utils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;

/**
 *
 * @author Luis-St
 *
 */

public class Registry<T> implements Iterable<T> {
	
	private final Map<UUID, T> registry = Maps.newHashMap();
	private final boolean freezable;
	private boolean frozen;
	
	private Registry(boolean freezable) {
		this.freezable = freezable;
	}
	
	public static <T> @NotNull Registry<T> of() {
		return new Registry<>(false);
	}
	
	public static <T> @NotNull Registry<T> freezable() {
		return new Registry<>(true);
	}
	
	public static <T> @NotNull Registry<T> of(@NotNull List<T> items) {
		Registry<T> registry = Registry.freezable();
		Objects.requireNonNull(items, "Items must not be null").forEach(registry::register);
		registry.freeze();
		return registry;
	}
	
	private void checkFrozen() {
		if (this.frozen) {
			throw new UnsupportedOperationException("Registry is frozen");
		}
	}
	
	public @NotNull UUID register(@NotNull T item) {
		this.checkFrozen();
		UUID uniqueId = UUID.randomUUID();
		this.registry.put(uniqueId, Objects.requireNonNull(item, "Item must not be null"));
		return uniqueId;
	}
	
	public @NotNull UUID register(@NotNull Function<UUID, T> function) {
		this.checkFrozen();
		UUID uniqueId = UUID.randomUUID();
		this.registry.put(uniqueId, Objects.requireNonNull(function, "Function must not be null").apply(uniqueId));
		return uniqueId;
	}
	
	public boolean remove(@Nullable UUID uniqueId) {
		this.checkFrozen();
		return this.registry.remove(uniqueId) != null;
	}
	
	public @Nullable T get(@NotNull UUID uniqueId) {
		return this.registry.get(Objects.requireNonNull(uniqueId, "Unique id must not be null"));
	}
	
	public @NotNull T getOrThrow(@NotNull UUID uniqueId) {
		T item = this.get(uniqueId);
		if (item == null) {
			throw new NoSuchElementException("No value present");
		}
		return item;
	}
	
	public @NotNull UUID getUniqueId(@Nullable T item) {
		return this.registry.entrySet().stream().filter(entry -> entry.getValue().equals(item)).map(Map.Entry::getKey).findFirst().orElse(Utils.EMPTY_UUID);
	}
	
	public boolean contains(@Nullable UUID uniqueId) {
		return this.registry.containsKey(uniqueId);
	}
	
	public @NotNull Set<UUID> getKeys() {
		return this.registry.keySet();
	}
	
	public @NotNull Collection<T> getItems() {
		return this.registry.values();
	}
	
	@Override
	public @NotNull Iterator<T> iterator() {
		return this.getItems().iterator();
	}
	
	public boolean isEmpty() {
		return this.registry.isEmpty();
	}
	
	public int size() {
		return this.registry.size();
	}
	
	public void clear() {
		this.checkFrozen();
		this.registry.clear();
	}
	
	public void freeze() {
		if (this.freezable) {
			this.frozen = true;
		}
	}
	
	public void unfreeze() {
		this.frozen = false;
	}
	
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
