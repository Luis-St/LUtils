package net.luis.utils.util;

import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Luis-st
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
		assertEquals(leftEither.map(Integer::parseInt, Integer::parseInt), Integer.valueOf(10));
		
		Either<String, String> rightEither = Either.right("10");
		assertEquals(rightEither.map(Integer::parseInt, Integer::parseInt), Integer.valueOf(10));
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
		assertEquals(Either.left("left").leftOrThrow(), "left");
		assertThrows(NoSuchElementException.class, () -> Either.right("right").leftOrThrow());
	}
	
	@Test
	void rightOrThrow() {
		assertDoesNotThrow(() -> Either.right("right").rightOrThrow());
		assertEquals(Either.right("right").rightOrThrow(), "right");
		assertThrows(NoSuchElementException.class, () -> Either.left("left").rightOrThrow());
	}
	
	@Test
	void swap() {
		Either<String, String> leftEither = Either.<String, String>left("left").swap();
		assertFalse(leftEither.isLeft());
		assertTrue(leftEither.isRight());
		assertThrows(NoSuchElementException.class, leftEither::leftOrThrow);
		assertDoesNotThrow(leftEither::rightOrThrow);
		assertEquals(leftEither.rightOrThrow(), "left");
		
		Either<String, String> rightEither = Either.<String, String>right("right").swap();
		assertTrue(rightEither.isLeft());
		assertFalse(rightEither.isRight());
		assertDoesNotThrow(rightEither::leftOrThrow);
		assertEquals(rightEither.leftOrThrow(), "right");
		assertThrows(NoSuchElementException.class, rightEither::rightOrThrow);
	}
}