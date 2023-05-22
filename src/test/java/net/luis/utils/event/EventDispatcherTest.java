package net.luis.utils.event;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Luis-St
 *
 */

class EventDispatcherTest {
	
	private static final EventType<ExampleEvent> TYPE = new EventType<>("example");
	
	@Test
	void register() {
		assertDoesNotThrow(() -> new EventDispatcher().register(TYPE, (type, event) -> {}));
	}
	
	@Test
	void remove() {
		EventDispatcher dispatcher = new EventDispatcher();
		UUID uniqueId = dispatcher.register(TYPE, (type, event) -> {});
		assertTrue(dispatcher.remove(TYPE, uniqueId));
	}
	
	@Test
	void dispatch() {
		EventDispatcher dispatcher = new EventDispatcher();
		assertDoesNotThrow(() -> dispatcher.dispatch(TYPE, new ExampleEvent()));
		dispatcher.register(TYPE, (type, event) -> {
			assertEquals(TYPE, type);
			assertEquals(ExampleEvent.class, event.getClass());
		});
		dispatcher.register(TYPE, (type, event) -> {
			assertEquals(TYPE, type);
			assertEquals(ExampleEvent.class, event.getClass());
		});
		dispatcher.dispatch(TYPE, new ExampleEvent());
	}
	
	static class ExampleEvent implements Event { }
}