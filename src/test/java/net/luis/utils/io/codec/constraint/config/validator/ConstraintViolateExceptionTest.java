package net.luis.utils.io.codec.constraint.config.validator;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link ConstraintViolateException}.<br>
 *
 * @author Luis-St
 */
class ConstraintViolateExceptionTest {
	
	@Test
	void defaultConstructor() {
		ConstraintViolateException exception = new ConstraintViolateException();
		
		assertNull(exception.getMessage());
		assertNull(exception.getCause());
	}
	
	@Test
	void constructorWithMessage() {
		String message = "Type provider operation failed";
		ConstraintViolateException exception = new ConstraintViolateException(message);
		
		assertEquals(message, exception.getMessage());
		assertNull(exception.getCause());
	}
	
	@Test
	void constructorWithNullMessage() {
		ConstraintViolateException exception = new ConstraintViolateException((String) null);
		
		assertNull(exception.getMessage());
		assertNull(exception.getCause());
	}
	
	@Test
	void constructorWithEmptyMessage() {
		String message = "";
		ConstraintViolateException exception = new ConstraintViolateException(message);
		
		assertEquals(message, exception.getMessage());
		assertNull(exception.getCause());
	}
	
	@Test
	void constructorWithCause() {
		RuntimeException cause = new RuntimeException("Root cause");
		ConstraintViolateException exception = new ConstraintViolateException(cause);
		
		assertEquals("java.lang.RuntimeException: Root cause", exception.getMessage());
		assertSame(cause, exception.getCause());
	}
	
	@Test
	void constructorWithNullCause() {
		ConstraintViolateException exception = new ConstraintViolateException((Throwable) null);
		
		assertNull(exception.getMessage());
		assertNull(exception.getCause());
	}
	
	@Test
	void constructorWithMessageAndCause() {
		String message = "Type provider operation failed";
		RuntimeException cause = new RuntimeException("Root cause");
		ConstraintViolateException exception = new ConstraintViolateException(message, cause);
		
		assertEquals(message, exception.getMessage());
		assertSame(cause, exception.getCause());
	}
	
	@Test
	void constructorWithNullMessageAndCause() {
		RuntimeException cause = new RuntimeException("Root cause");
		ConstraintViolateException exception = new ConstraintViolateException(null, cause);
		
		assertNull(exception.getMessage());
		assertSame(cause, exception.getCause());
	}
	
	@Test
	void constructorWithMessageAndNullCause() {
		String message = "Type provider operation failed";
		ConstraintViolateException exception = new ConstraintViolateException(message, null);
		
		assertEquals(message, exception.getMessage());
		assertNull(exception.getCause());
	}
	
	@Test
	void constructorWithNullMessageAndNullCause() {
		ConstraintViolateException exception = new ConstraintViolateException(null, null);
		
		assertNull(exception.getMessage());
		assertNull(exception.getCause());
	}
	
	@Test
	void isRuntimeException() {
		ConstraintViolateException exception = new ConstraintViolateException("test");
		
		assertInstanceOf(RuntimeException.class, exception);
	}
	
	@Test
	void canBeThrown() {
		assertThrows(ConstraintViolateException.class, () -> {
			throw new ConstraintViolateException("test exception");
		});
	}
	
	@Test
	void preservesStackTrace() {
		try {
			throw new ConstraintViolateException("test");
		} catch (ConstraintViolateException e) {
			assertNotNull(e.getStackTrace());
			assertTrue(e.getStackTrace().length > 0);
			assertEquals("preservesStackTrace", e.getStackTrace()[0].getMethodName());
		}
	}
	
	@Test
	void causedByChain() {
		Exception rootCause = new Exception("Root");
		RuntimeException intermediateCause = new RuntimeException("Intermediate", rootCause);
		ConstraintViolateException exception = new ConstraintViolateException("Final", intermediateCause);
		
		assertEquals("Final", exception.getMessage());
		assertSame(intermediateCause, exception.getCause());
		assertSame(rootCause, exception.getCause().getCause());
	}
	
	@Test
	void withComplexMessage() {
		String complexMessage = "Failed to create integer value from 'abc': NumberFormatException";
		ConstraintViolateException exception = new ConstraintViolateException(complexMessage);
		
		assertEquals(complexMessage, exception.getMessage());
	}
	
	@Test
	void withSpecialCharactersInMessage() {
		String specialMessage = "Type provider failed: 特殊字符 & symbols! @#$%^&*()";
		ConstraintViolateException exception = new ConstraintViolateException(specialMessage);
		
		assertEquals(specialMessage, exception.getMessage());
	}
}
