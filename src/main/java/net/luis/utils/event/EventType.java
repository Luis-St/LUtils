package net.luis.utils.event;

/**
 *
 * @author Luis-St
 *
 */

public record EventType<T extends Event>(String name) {
	
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
