package net.luis.utils.event;

/**
 *
 * @author Luis-St
 *
 */

@FunctionalInterface
public interface EventListener<T extends EventType<E>, E extends Event> {
	
	void call(T type, E event);
}
