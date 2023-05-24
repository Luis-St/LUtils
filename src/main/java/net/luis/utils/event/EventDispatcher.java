package net.luis.utils.event;

import net.luis.utils.collection.Registry;

import java.util.*;

/**
 *
 * @author Luis-St
 *
 */

public class EventDispatcher {
	
	private final Map<EventType<?>, Registry<EventListener<?>>> listeners = new HashMap<>();
	
	public <T extends EventType<E>, E extends Event> UUID register(T type, EventListener<E> listener) {
		return this.listeners.computeIfAbsent(type, k -> Registry.of()).register(listener);
	}
	
	public <T extends EventType<E>, E extends Event> boolean remove(T type, UUID uniqueId) {
		return this.listeners.get(type).remove(uniqueId);
	}
	
	public <T extends EventType<E>, E extends Event> void removeAll(T type) {
		this.listeners.get(type).clear();
	}
	
	@SuppressWarnings("unchecked")
	public <T extends EventType<E>, E extends Event> void dispatch(T type, E event) {
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
