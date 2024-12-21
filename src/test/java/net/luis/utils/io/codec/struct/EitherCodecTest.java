/*
 * LUtils
 * Copyright (C) 2024 Luis Staudt
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

package net.luis.utils.io.codec.struct;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.JsonElement;
import net.luis.utils.io.data.json.JsonPrimitive;
import net.luis.utils.util.Either;
import net.luis.utils.util.Result;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link EitherCodec}.<br>
 *
 * @author Luis-St
 */
class EitherCodecTest {
	
	@Test
	void constructor() {
		assertThrows(NullPointerException.class, () -> new EitherCodec<>(null, Codec.BOOLEAN));
		assertThrows(NullPointerException.class, () -> new EitherCodec<>(Codec.INTEGER, null));
	}
	
	@Test
	void encodeStart() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Either<Integer, Boolean>> codec = new EitherCodec<>(Codec.INTEGER, Codec.BOOLEAN);
		Either<Integer, Boolean> left = Either.left(1);
		Either<Integer, Boolean> right = Either.right(true);
		
		assertThrows(NullPointerException.class, () -> codec.encodeStart(null, typeProvider.empty(), left));
		assertThrows(NullPointerException.class, () -> codec.encodeStart(typeProvider, null, left));
		
		assertTrue(assertDoesNotThrow(() -> codec.encodeStart(typeProvider, typeProvider.empty(), null)).isError());
		Result<JsonElement> leftResult = assertDoesNotThrow(() -> codec.encodeStart(typeProvider, typeProvider.empty(), left));
		assertTrue(leftResult.isSuccess());
		assertEquals(new JsonPrimitive(1), leftResult.orThrow());
		
		Result<JsonElement> rightResult = assertDoesNotThrow(() -> codec.encodeStart(typeProvider, typeProvider.empty(), right));
		assertTrue(rightResult.isSuccess());
		assertEquals(new JsonPrimitive(true), rightResult.orThrow());
	}
	
	@Test
	void decodeStart() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Either<Integer, Boolean>> codec = new EitherCodec<>(Codec.INTEGER, Codec.BOOLEAN);
		
		assertThrows(NullPointerException.class, () -> codec.decodeStart(null, new JsonPrimitive(1)));
		assertTrue(assertDoesNotThrow(() -> codec.decodeStart(typeProvider, null)).isError());
		assertTrue(assertDoesNotThrow(() -> codec.decodeStart(typeProvider, new JsonPrimitive("test"))).isError());
		
		Result<Either<Integer, Boolean>> leftResult = assertDoesNotThrow(() -> codec.decodeStart(typeProvider, new JsonPrimitive(1)));
		assertTrue(leftResult.isSuccess());
		assertEquals(Either.left(1), leftResult.orThrow());
		
		Result<Either<Integer, Boolean>> rightResult = assertDoesNotThrow(() -> codec.decodeStart(typeProvider, new JsonPrimitive(true)));
		assertTrue(rightResult.isSuccess());
		assertEquals(Either.right(true), rightResult.orThrow());
	}
}
