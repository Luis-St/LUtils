package net.luis.utils.event;

import net.luis.utils.collection.Registry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 *
 * @author Luis-St
 *
 */

public class EventDispatcher {
	
	private final Map<EventType<?>, Registry<EventListener<?>>> listeners = new HashMap<>();
	
	public <E extends Event> @NotNull UUID register(@NotNull EventType<E> type, @NotNull EventListener<E> listener) {
		Objects.requireNonNull(type, "Type must not be null");
		Objects.requireNonNull(listener, "Listener must not be null");
		return this.listeners.computeIfAbsent(type, k -> Registry.of()).register(listener);
	}
	
	public <E extends Event> boolean remove(@Nullable EventType<E> type, @Nullable UUID uniqueId) {
		return this.listeners.getOrDefault(type, Registry.of()).remove(uniqueId);
	}
	
	public <E extends Event> void removeAll(@Nullable EventType<E> type) {
		this.listeners.getOrDefault(type, Registry.of()).clear();
	}
	
	@SuppressWarnings("unchecked")
	public <E extends Event> void dispatch(@NotNull EventType<E> type, @NotNull E event) {
		Objects.requireNonNull(type, "Type must not be null");
		Objects.requireNonNull(event, "Event must not be null");
		this.listeners.getOrDefault(type, Registry.of()).forEach(listener -> ((EventListener<E>) listener).call(event));
	}
	
	//region Object overrides
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof EventDispatcher that)) return false;
		
		return this.listeners.equals(that.listeners);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.listeners);
	}
	
	@Override
	public String toString() {
		return "EventDispatcher";
	}
	//endregion
}
