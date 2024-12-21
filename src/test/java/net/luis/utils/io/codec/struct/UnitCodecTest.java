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
import net.luis.utils.io.data.json.*;
import net.luis.utils.util.Result;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link UnitCodec}.<br>
 *
 * @author Luis-St
 */
class UnitCodecTest {
	
	@Test
	void constructor() {
		assertThrows(NullPointerException.class, () -> new UnitCodec<>(null));
	}
	
	@Test
	void encodeStart() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new UnitCodec<>(() -> 1);
		
		assertThrows(NullPointerException.class, () -> codec.encodeStart(typeProvider, null, 1));
		assertDoesNotThrow(() -> codec.encodeStart(null, typeProvider.empty(), 1));
		
		Result<JsonElement> nullResult = assertDoesNotThrow(() -> codec.encodeStart(typeProvider, typeProvider.empty(), null));
		assertTrue(nullResult.isSuccess());
		assertEquals(JsonNull.INSTANCE, nullResult.orThrow());
		
		Result<JsonElement> result = assertDoesNotThrow(() -> codec.encodeStart(typeProvider, typeProvider.empty(), 1));
		assertTrue(result.isSuccess());
		assertEquals(JsonNull.INSTANCE, result.orThrow());
	}
	
	@Test
	void decodeStart() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new UnitCodec<>(() -> 1);
		
		assertDoesNotThrow(() -> codec.decodeStart(null, JsonNull.INSTANCE));
		
		Result<Integer> nullResult = assertDoesNotThrow(() -> codec.decodeStart(typeProvider, null));
		assertTrue(nullResult.isSuccess());
		assertEquals(1, nullResult.orThrow());
		
		Result<Integer> result = assertDoesNotThrow(() -> codec.decodeStart(typeProvider, JsonNull.INSTANCE));
		assertTrue(result.isSuccess());
		assertEquals(1, result.orThrow());
		
		Result<Integer> errorResult = assertDoesNotThrow(() -> codec.decodeStart(typeProvider, new JsonPrimitive(100)));
		assertTrue(errorResult.isSuccess());
		assertEquals(1, result.orThrow());
	}
}
