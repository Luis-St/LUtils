package net.luis.utils.util;

import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Luis-St
 *
 */

class EitherTest {
	
	@Test
	void mapBoth() {
		Either<String, String> leftEither = Either.left("10");
		assertEquals(leftEither.mapBoth(Integer::parseInt, Integer::parseInt), Either.left(10));
		
		Either<String, String> rightEither = Either.right("10");
		assertEquals(rightEither.mapBoth(Integer::parseInt, Integer::parseInt), Either.right(10));
	}
	
	@Test
	void map() {
		Either<String, String> leftEither = Either.left("10");
		assertEquals(Integer.valueOf(10), leftEither.map(Integer::parseInt, Integer::parseInt));
		
		Either<String, String> rightEither = Either.right("10");
		assertEquals(Integer.valueOf(10), rightEither.map(Integer::parseInt, Integer::parseInt));
	}
	
	@Test
	void isLeft() {
		assertTrue(Either.left("left").isLeft());
		assertFalse(Either.right("right").isLeft());
	}
	
	@Test
	void isRight() {
		assertTrue(Either.right("right").isRight());
		assertFalse(Either.left("left").isRight());
	}
	
	@Test
	void left() {
		assertTrue(Either.left("left").left().isPresent());
		assertTrue(Either.right("right").left().isEmpty());
	}
	
	@Test
	void right() {
		assertTrue(Either.right("right").right().isPresent());
		assertTrue(Either.left("left").right().isEmpty());
	}
	
	@Test
	void mapLeft() {
		Either<String, String> either = Either.left(" left ");
		assertEquals(either.mapLeft(String::trim), Either.left("left"));
	}
	
	@Test
	void mapRight() {
		Either<String, String> either = Either.right(" right ");
		assertEquals(either.mapRight(String::trim), Either.right("right"));
	}
	
	@Test
	void leftOrThrow() {
		assertDoesNotThrow(() -> Either.left("left").leftOrThrow());
		assertEquals("left", Either.left("left").leftOrThrow());
		assertThrows(NoSuchElementException.class, () -> Either.right("right").leftOrThrow());
	}
	
	@Test
	void rightOrThrow() {
		assertDoesNotThrow(() -> Either.right("right").rightOrThrow());
		assertEquals("right", Either.right("right").rightOrThrow());
		assertThrows(NoSuchElementException.class, () -> Either.left("left").rightOrThrow());
	}
	
	@Test
	void swap() {
		Either<String, String> leftEither = Either.<String, String>left("left").swap();
		assertFalse(leftEither.isLeft());
		assertTrue(leftEither.isRight());
		assertThrows(NoSuchElementException.class, leftEither::leftOrThrow);
		assertDoesNotThrow(leftEither::rightOrThrow);
		assertEquals("left", leftEither.rightOrThrow());
		
		Either<String, String> rightEither = Either.<String, String>right("right").swap();
		assertTrue(rightEither.isLeft());
		assertFalse(rightEither.isRight());
		assertDoesNotThrow(rightEither::leftOrThrow);
		assertEquals("right", rightEither.leftOrThrow());
		assertThrows(NoSuchElementException.class, rightEither::rightOrThrow);
	}
}