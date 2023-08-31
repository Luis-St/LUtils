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
	
	public <E extends Event> UUID register(EventType<E> type, EventListener<E> listener) {
		return this.listeners.computeIfAbsent(type, k -> Registry.of()).register(listener);
	}
	
	public <E extends Event> boolean remove(EventType<E> type, UUID uniqueId) {
		return this.listeners.get(type).remove(uniqueId);
	}
	
	public <E extends Event> void removeAll(EventType<E> type) {
		this.listeners.get(type).clear();
	}
	
	@SuppressWarnings("unchecked")
	public <E extends Event> void dispatch(EventType<E> type, E event) {
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
