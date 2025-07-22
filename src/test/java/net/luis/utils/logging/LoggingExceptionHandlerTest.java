/*
 * LUtils
 * Copyright (C) 2025 Luis Staudt
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package net.luis.utils.logging;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link LoggingExceptionHandler}.<br>
 *
 * @author Luis-St
 */
class LoggingExceptionHandlerTest {
	
	@Test
	void constructor() {
		assertDoesNotThrow(LoggingExceptionHandler::new);
		
		LoggingExceptionHandler handler = new LoggingExceptionHandler();
		assertNotNull(handler);
		assertInstanceOf(Thread.UncaughtExceptionHandler.class, handler);
	}
	
	@Test
	void uncaughtExceptionWithValidInputs() {
		LoggingExceptionHandler handler = new LoggingExceptionHandler();
		Thread thread = new Thread(() -> {});
		thread.setName("TestThread");
		
		RuntimeException exception = new RuntimeException("Test exception");
		
		assertDoesNotThrow(() -> handler.uncaughtException(thread, exception));
	}
	
	@Test
	void uncaughtExceptionWithNullThrowable() {
		LoggingExceptionHandler handler = new LoggingExceptionHandler();
		Thread thread = new Thread(() -> {});
		thread.setName("TestThread");
		
		assertDoesNotThrow(() -> handler.uncaughtException(thread, null));
	}
	
	@Test
	void uncaughtExceptionWithAnonymousThread() {
		LoggingExceptionHandler handler = new LoggingExceptionHandler();
		Thread thread = new Thread(() -> {});
		
		RuntimeException exception = new RuntimeException("Test exception");
		
		assertDoesNotThrow(() -> handler.uncaughtException(thread, exception));
	}
	
	@Test
	void uncaughtExceptionWithCurrentThread() {
		LoggingExceptionHandler handler = new LoggingExceptionHandler();
		Thread currentThread = Thread.currentThread();
		
		RuntimeException exception = new RuntimeException("Test exception");
		
		assertDoesNotThrow(() -> handler.uncaughtException(currentThread, exception));
	}
	
	@Test
	void uncaughtExceptionWithDifferentExceptionTypes() {
		LoggingExceptionHandler handler = new LoggingExceptionHandler();
		Thread thread = new Thread(() -> {});
		thread.setName("TestThread");
		
		Throwable[] exceptions = {
			new RuntimeException("Runtime exception"),
			new IllegalArgumentException("Illegal argument"),
			new NullPointerException("Null pointer"),
			new OutOfMemoryError("Out of memory"),
			new StackOverflowError("Stack overflow"),
			new Exception("Generic exception"),
			new Throwable("Generic throwable")
		};
		
		for (Throwable exception : exceptions) {
			assertDoesNotThrow(() -> handler.uncaughtException(thread, exception));
		}
	}
	
	@Test
	void uncaughtExceptionWithExceptionWithCause() {
		LoggingExceptionHandler handler = new LoggingExceptionHandler();
		Thread thread = new Thread(() -> {});
		thread.setName("TestThread");
		
		RuntimeException cause = new RuntimeException("Root cause");
		RuntimeException exception = new RuntimeException("Wrapper exception", cause);
		
		assertDoesNotThrow(() -> handler.uncaughtException(thread, exception));
	}
	
	@Test
	void uncaughtExceptionWithLongExceptionMessage() {
		LoggingExceptionHandler handler = new LoggingExceptionHandler();
		Thread thread = new Thread(() -> {});
		thread.setName("TestThread");
		
		StringBuilder longMessage = new StringBuilder();
		for (int i = 0; i < 1000; i++) {
			longMessage.append("This is a very long exception message. ");
		}
		
		RuntimeException exception = new RuntimeException(longMessage.toString());
		
		assertDoesNotThrow(() -> handler.uncaughtException(thread, exception));
	}
	
	@Test
	void uncaughtExceptionWithSpecialCharactersInThreadName() {
		LoggingExceptionHandler handler = new LoggingExceptionHandler();
		Thread thread = new Thread(() -> {});
		thread.setName("Test-Thread_123!@#$%^&*()");
		
		RuntimeException exception = new RuntimeException("Test exception");
		
		assertDoesNotThrow(() -> handler.uncaughtException(thread, exception));
	}
	
	@Test
	void uncaughtExceptionWithEmptyThreadName() {
		LoggingExceptionHandler handler = new LoggingExceptionHandler();
		Thread thread = new Thread(() -> {});
		thread.setName("");
		
		RuntimeException exception = new RuntimeException("Test exception");
		
		assertDoesNotThrow(() -> handler.uncaughtException(thread, exception));
	}
	
	@Test
	void uncaughtExceptionWithExceptionWithoutMessage() {
		LoggingExceptionHandler handler = new LoggingExceptionHandler();
		Thread thread = new Thread(() -> {});
		thread.setName("TestThread");
		
		RuntimeException exception = new RuntimeException();
		
		assertDoesNotThrow(() -> handler.uncaughtException(thread, exception));
	}
	
	@Test
	void multipleHandlerInstances() {
		LoggingExceptionHandler handler1 = new LoggingExceptionHandler();
		LoggingExceptionHandler handler2 = new LoggingExceptionHandler();
		
		assertNotSame(handler1, handler2);
		
		Thread thread = new Thread(() -> {});
		thread.setName("TestThread");
		RuntimeException exception = new RuntimeException("Test exception");
		
		assertDoesNotThrow(() -> handler1.uncaughtException(thread, exception));
		assertDoesNotThrow(() -> handler2.uncaughtException(thread, exception));
	}
}
