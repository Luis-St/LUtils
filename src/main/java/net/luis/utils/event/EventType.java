package net.luis.utils.event;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 *
 * @author Luis-St
 *
 */

public record EventType<T extends Event>(@NotNull String name) {
	
	public EventType {
		Objects.requireNonNull(name, "Name must not be null");
	}
	
	//region Object overrides
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof EventType<?> eventType)) return false;
		
		return this.name.equals(eventType.name);
	}
	
	@Override
	public String toString() {
		return "EventType{name='" + this.name + '\'' + "}";
	}
	//endregion
}
