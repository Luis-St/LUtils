package net.luis.utils.event;

import org.jetbrains.annotations.NotNull;

/**
 *
 * @author Luis-St
 *
 */

@FunctionalInterface
public interface EventListener<E extends Event> {
	
	void call(@NotNull E event);
}
