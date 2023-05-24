package net.luis.utils.event;

/**
 *
 * @author Luis-St
 *
 */

@FunctionalInterface
public interface EventListener<E extends Event> {
	
	void call(E event);
}
