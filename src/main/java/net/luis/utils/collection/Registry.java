package net.luis.utils.collection;

import com.google.common.collect.Maps;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 *
 * @author Luis-St
 *
 */

public class Registry<T> implements Iterable<T> {
	
	private final Map<UUID, T> registry = Maps.newHashMap();
	private final boolean freezable;
	private boolean frozen = false;
	
	private Registry(boolean freezable) {
		this.freezable = freezable;
	}
	
	public static <T> @NotNull Registry<T> of() {
		return new Registry<>(false);
	}
	
	public static <T> @NotNull Registry<T> freezable() {
		return new Registry<>(true);
	}
	
	public static <T> @NotNull Registry<T> of(List<T> items) {
		Registry<T> registry = Registry.freezable();
		Objects.requireNonNull(items, "Items must not be null").forEach(registry::register);
		registry.freeze();
		return registry;
	}
	
	public @NotNull UUID register(T item) {
		if (this.frozen) {
			throw new UnsupportedOperationException("Registry is frozen");
		}
		UUID uniqueId = UUID.randomUUID();
		this.registry.put(uniqueId, Objects.requireNonNull(item, "Item must not be null"));
		return uniqueId;
	}
	
	public boolean remove(UUID uniqueId) {
		if (this.frozen) {
			throw new UnsupportedOperationException("Registry is frozen");
		}
		return this.registry.remove(Objects.requireNonNull(uniqueId, "Unique id must not be null")) != null;
	}
	
	public T get(UUID uniqueId) {
		return this.registry.get(Objects.requireNonNull(uniqueId, "Unique id must not be null"));
	}
	
	public UUID getUniqueId(T item) {
		return this.registry.entrySet().stream().filter(entry -> entry.getValue().equals(Objects.requireNonNull(item, "Item must not be null"))).map(Map.Entry::getKey).findFirst().orElse(null);
	}
	
	public boolean contains(UUID uniqueId) {
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
		if (this.frozen) {
			throw new UnsupportedOperationException("Registry is frozen");
		}
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
		return Objects.hash(this.registry, this.freezable, this.frozen);
	}
	
	@Override
	public String toString() {
		return "Registry{registry=" + this.registry + ", frozen=" + this.frozen + "}";
	}
	//endregion
}
