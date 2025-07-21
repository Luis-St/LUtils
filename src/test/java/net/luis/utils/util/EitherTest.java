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

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link Either}.<br>
 *
 * @author Luis-St
 */
class EitherTest {
	
	@Test
	void leftWithValidValues() {
		assertNotNull(Either.left("left"));
		assertNotNull(Either.left(42));
		assertNotNull(Either.left(null));
	}
	
	@Test
	void rightWithValidValues() {
		assertNotNull(Either.right("right"));
		assertNotNull(Either.right(42));
		assertNotNull(Either.right(null));
	}
	
	@Test
	void isLeftWithLeftEither() {
		assertTrue(Either.left("left").isLeft());
		assertTrue(Either.left(42).isLeft());
		assertTrue(Either.left(null).isLeft());
	}
	
	@Test
	void isLeftWithRightEither() {
		assertFalse(Either.right("right").isLeft());
		assertFalse(Either.right(42).isLeft());
		assertFalse(Either.right(null).isLeft());
	}
	
	@Test
	void isRightWithRightEither() {
		assertTrue(Either.right("right").isRight());
		assertTrue(Either.right(42).isRight());
		assertTrue(Either.right(null).isRight());
	}
	
	@Test
	void isRightWithLeftEither() {
		assertFalse(Either.left("left").isRight());
		assertFalse(Either.left(42).isRight());
		assertFalse(Either.left(null).isRight());
	}
	
	@Test
	void ifLeftWithLeftEither() {
		AtomicBoolean executed = new AtomicBoolean(false);
		Either.left("left").ifLeft(s -> {
			assertEquals("left", s);
			executed.set(true);
		});
		assertTrue(executed.get());
	}
	
	@Test
	void ifLeftWithRightEither() {
		AtomicBoolean executed = new AtomicBoolean(false);
		Either.right("right").ifLeft(s -> executed.set(true));
		assertFalse(executed.get());
	}
	
	@Test
	void ifLeftWithNullAction() {
		assertThrows(NullPointerException.class, () -> Either.left("left").ifLeft(null));
	}
	
	@Test
	void ifRightWithRightEither() {
		AtomicBoolean executed = new AtomicBoolean(false);
		Either.right("right").ifRight(s -> {
			assertEquals("right", s);
			executed.set(true);
		});
		assertTrue(executed.get());
	}
	
	@Test
	void ifRightWithLeftEither() {
		AtomicBoolean executed = new AtomicBoolean(false);
		Either.left("left").ifRight(s -> executed.set(true));
		assertFalse(executed.get());
	}
	
	@Test
	void ifRightWithNullAction() {
		assertThrows(NullPointerException.class, () -> Either.right("right").ifRight(null));
	}
	
	@Test
	void leftWithLeftEither() {
		assertEquals(Optional.of("left"), Either.left("left").left());
		assertEquals(Optional.of(42), Either.left(42).left());
		assertEquals(Optional.empty(), Either.left(null).left());
	}
	
	@Test
	void leftWithRightEither() {
		assertEquals(Optional.empty(), Either.right("right").left());
		assertEquals(Optional.empty(), Either.right(42).left());
		assertEquals(Optional.empty(), Either.right(null).left());
	}
	
	@Test
	void rightWithRightEither() {
		assertEquals(Optional.of("right"), Either.right("right").right());
		assertEquals(Optional.of(42), Either.right(42).right());
		assertEquals(Optional.empty(), Either.right(null).right());
	}
	
	@Test
	void rightWithLeftEither() {
		assertEquals(Optional.empty(), Either.left("left").right());
		assertEquals(Optional.empty(), Either.left(42).right());
		assertEquals(Optional.empty(), Either.left(null).right());
	}
	
	@Test
	void leftOrThrowWithLeftEither() {
		assertEquals("left", Either.left("left").leftOrThrow());
		assertEquals(Integer.valueOf(42), Either.left(42).leftOrThrow());
		assertNull(Either.left(null).leftOrThrow());
	}
	
	@Test
	void leftOrThrowWithRightEither() {
		assertThrows(IllegalStateException.class, () -> Either.right("right").leftOrThrow());
		assertThrows(IllegalStateException.class, () -> Either.right(42).leftOrThrow());
		assertThrows(IllegalStateException.class, () -> Either.right(null).leftOrThrow());
	}
	
	@Test
	void rightOrThrowWithRightEither() {
		assertEquals("right", Either.right("right").rightOrThrow());
		assertEquals(Integer.valueOf(42), Either.right(42).rightOrThrow());
		assertNull(Either.right(null).rightOrThrow());
	}
	
	@Test
	void rightOrThrowWithLeftEither() {
		assertThrows(IllegalStateException.class, () -> Either.left("left").rightOrThrow());
		assertThrows(IllegalStateException.class, () -> Either.left(42).rightOrThrow());
		assertThrows(IllegalStateException.class, () -> Either.left(null).rightOrThrow());
	}
	
	@Test
	void swapWithLeftEither() {
		Either<String, String> original = Either.left("left");
		Either<String, String> swapped = original.swap();
		
		assertFalse(swapped.isLeft());
		assertTrue(swapped.isRight());
		assertEquals("left", swapped.rightOrThrow());
		assertThrows(IllegalStateException.class, swapped::leftOrThrow);
	}
	
	@Test
	void swapWithRightEither() {
		Either<String, String> original = Either.right("right");
		Either<String, String> swapped = original.swap();
		
		assertTrue(swapped.isLeft());
		assertFalse(swapped.isRight());
		assertEquals("right", swapped.leftOrThrow());
		assertThrows(IllegalStateException.class, swapped::rightOrThrow);
	}
	
	@Test
	void mapBothWithLeftEither() {
		Either<String, String> leftEither = Either.left("10");
		Either<Integer, Integer> result = leftEither.mapBoth(Integer::parseInt, Integer::parseInt);
		
		assertTrue(result.isLeft());
		assertEquals(Integer.valueOf(10), result.leftOrThrow());
	}
	
	@Test
	void mapBothWithRightEither() {
		Either<String, String> rightEither = Either.right("20");
		Either<Integer, Integer> result = rightEither.mapBoth(Integer::parseInt, Integer::parseInt);
		
		assertTrue(result.isRight());
		assertEquals(Integer.valueOf(20), result.rightOrThrow());
	}
	
	@Test
	void mapBothWithNullMappers() {
		Either<String, String> leftEither = Either.left("10");
		Either<String, String> rightEither = Either.right("20");
		
		assertThrows(NullPointerException.class, () -> leftEither.mapBoth(null, Integer::parseInt));
		assertThrows(NullPointerException.class, () -> rightEither.mapBoth(Integer::parseInt, null));
	}
	
	@Test
	void mapToWithLeftEither() {
		Either<String, String> leftEither = Either.left("10");
		Integer result = leftEither.mapTo(Integer::parseInt, Integer::parseInt);
		
		assertEquals(Integer.valueOf(10), result);
	}
	
	@Test
	void mapToWithRightEither() {
		Either<String, String> rightEither = Either.right("20");
		Integer result = rightEither.mapTo(Integer::parseInt, Integer::parseInt);
		
		assertEquals(Integer.valueOf(20), result);
	}
	
	@Test
	void mapToWithNullMappers() {
		Either<String, String> leftEither = Either.left("10");
		Either<String, String> rightEither = Either.right("20");
		
		assertThrows(NullPointerException.class, () -> leftEither.mapTo(null, Integer::parseInt));
		assertThrows(NullPointerException.class, () -> rightEither.mapTo(Integer::parseInt, null));
	}
	
	@Test
	void mapLeftWithLeftEither() {
		Either<String, String> either = Either.left(" left ");
		Either<String, String> result = either.mapLeft(String::trim);
		
		assertTrue(result.isLeft());
		assertEquals("left", result.leftOrThrow());
	}
	
	@Test
	void mapLeftWithRightEither() {
		Either<String, String> either = Either.right(" right ");
		Either<String, String> result = either.mapLeft(String::trim);
		
		assertTrue(result.isRight());
		assertEquals(" right ", result.rightOrThrow());
	}
	
	@Test
	void mapLeftWithNullMapper() {
		Either<String, String> either = Either.left("left");
		assertThrows(NullPointerException.class, () -> either.mapLeft(null));
	}
	
	@Test
	void mapRightWithRightEither() {
		Either<String, String> either = Either.right(" right ");
		Either<String, String> result = either.mapRight(String::trim);
		
		assertTrue(result.isRight());
		assertEquals("right", result.rightOrThrow());
	}
	
	@Test
	void mapRightWithLeftEither() {
		Either<String, String> either = Either.left(" left ");
		Either<String, String> result = either.mapRight(String::trim);
		
		assertTrue(result.isLeft());
		assertEquals(" left ", result.leftOrThrow());
	}
	
	@Test
	void mapRightWithNullMapper() {
		Either<String, String> either = Either.right("right");
		assertThrows(NullPointerException.class, () -> either.mapRight(null));
	}
	
	@Test
	void hashCodeConsistency() {
		Either<String, String> left1 = Either.left("same");
		Either<String, String> left2 = Either.left("same");
		Either<String, String> right1 = Either.right("same");
		Either<String, String> right2 = Either.right("same");
		
		assertEquals(left1.hashCode(), left2.hashCode());
		assertEquals(right1.hashCode(), right2.hashCode());
	}
}
