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

package net.luis.utils.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link Either}.<br>
 *
 * @author Luis-St
 */
class EitherTest {
	
	@Test
	void left() {
		assertNotNull(Either.left("left"));
		assertDoesNotThrow(() -> Either.left(null));
	}
	
	@Test
	void right() {
		assertNotNull(Either.right("right"));
		assertDoesNotThrow(() -> Either.right(null));
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
	void ifLeft() {
		Either.left("left").ifLeft(s -> assertEquals("left", s));
		Either.right("right").ifLeft(s -> fail("Should not be called"));
	}
	
	@Test
	void ifRight() {
		Either.right("right").ifRight(s -> assertEquals("right", s));
		Either.left("left").ifRight(s -> fail("Should not be called"));
	}
	
	@Test
	void leftOrThrow() {
		assertDoesNotThrow(() -> Either.left("left").leftOrThrow());
		assertEquals("left", Either.left("left").leftOrThrow());
		assertNull(Either.left(null).leftOrThrow());
		assertThrows(IllegalStateException.class, Either.right("right")::leftOrThrow);
	}
	
	@Test
	void rightOrThrow() {
		assertDoesNotThrow(() -> Either.right("right").rightOrThrow());
		assertEquals("right", Either.right("right").rightOrThrow());
		assertNull(Either.right(null).rightOrThrow());
		assertThrows(IllegalStateException.class, Either.left("left")::rightOrThrow);
	}
	
	@Test
	void swap() {
		Either<String, String> leftEither = Either.<String, String>left("left").swap();
		assertFalse(leftEither.isLeft());
		assertTrue(leftEither.isRight());
		assertThrows(IllegalStateException.class, leftEither::leftOrThrow);
		assertDoesNotThrow(leftEither::rightOrThrow);
		assertEquals("left", leftEither.rightOrThrow());
		
		Either<String, String> rightEither = Either.<String, String>right("right").swap();
		assertTrue(rightEither.isLeft());
		assertFalse(rightEither.isRight());
		assertDoesNotThrow(rightEither::leftOrThrow);
		assertEquals("right", rightEither.leftOrThrow());
		assertThrows(IllegalStateException.class, rightEither::rightOrThrow);
	}
	
	@Test
	void mapBoth() {
		Either<String, String> leftEither = Either.left("10");
		assertThrows(NullPointerException.class, () -> leftEither.mapBoth(null, null));
		assertThrows(NullPointerException.class, () -> leftEither.mapBoth(null, Integer::parseInt));
		assertDoesNotThrow(() -> leftEither.mapBoth(Integer::parseInt, null));
		assertDoesNotThrow(() -> leftEither.mapBoth(Integer::parseInt, Integer::parseInt));
		assertEquals(leftEither.mapBoth(Integer::parseInt, Integer::parseInt), Either.left(10));
		
		Either<String, String> rightEither = Either.right("10");
		assertThrows(NullPointerException.class, () -> rightEither.mapBoth(null, null));
		assertThrows(NullPointerException.class, () -> rightEither.mapBoth(Integer::parseInt, null));
		assertDoesNotThrow(() -> rightEither.mapBoth(null, Integer::parseInt));
		assertDoesNotThrow(() -> rightEither.mapBoth(Integer::parseInt, Integer::parseInt));
		assertEquals(rightEither.mapBoth(Integer::parseInt, Integer::parseInt), Either.right(10));
	}
	
	@Test
	void mapTo() {
		Either<String, String> leftEither = Either.left("10");
		assertThrows(NullPointerException.class, () -> leftEither.mapTo(null, null));
		assertThrows(NullPointerException.class, () -> leftEither.mapTo(null, Integer::parseInt));
		assertDoesNotThrow(() -> leftEither.mapTo(Integer::parseInt, null));
		assertDoesNotThrow(() -> leftEither.mapTo(Integer::parseInt, Integer::parseInt));
		assertEquals(Integer.valueOf(10), leftEither.mapTo(Integer::parseInt, Integer::parseInt));
		
		Either<String, String> rightEither = Either.right("10");
		assertThrows(NullPointerException.class, () -> rightEither.mapTo(null, null));
		assertThrows(NullPointerException.class, () -> rightEither.mapTo(Integer::parseInt, null));
		assertDoesNotThrow(() -> rightEither.mapTo(null, Integer::parseInt));
		assertDoesNotThrow(() -> rightEither.mapTo(Integer::parseInt, Integer::parseInt));
		assertEquals(Integer.valueOf(10), rightEither.mapTo(Integer::parseInt, Integer::parseInt));
	}
	
	@Test
	void mapLeft() {
		Either<String, String> either = Either.left(" left ");
		assertThrows(NullPointerException.class, () -> either.mapLeft(null));
		assertDoesNotThrow(() -> either.mapLeft(String::trim));
		assertEquals(either.mapLeft(String::trim), Either.left("left"));
	}
	
	@Test
	void mapRight() {
		Either<String, String> either = Either.right(" right ");
		assertThrows(NullPointerException.class, () -> either.mapRight(null));
		assertDoesNotThrow(() -> either.mapRight(String::trim));
		assertEquals(either.mapRight(String::trim), Either.right("right"));
	}
}