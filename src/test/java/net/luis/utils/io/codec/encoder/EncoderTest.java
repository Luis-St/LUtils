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

package net.luis.utils.io.codec.encoder;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.ResultingFunction;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.JsonElement;
import net.luis.utils.io.data.json.JsonPrimitive;
import net.luis.utils.util.Result;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link Encoder}.<br>
 *
 * @author Luis-St
 */
class EncoderTest {
	
	@Test
	void encode() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Encoder<Integer> decoder = Codec.INTEGER;
		
		assertThrows(NullPointerException.class, () -> decoder.encode(null, 1));
		assertThrows(IllegalStateException.class, () -> decoder.encode(typeProvider, null));
		assertEquals(new JsonPrimitive(1), decoder.encode(typeProvider, 1));
		
		assertThrows(NullPointerException.class, () -> decoder.encode(null, typeProvider.empty(), 1));
		assertThrows(NullPointerException.class, () -> decoder.encode(typeProvider, null, 1));
		assertThrows(IllegalStateException.class, () -> decoder.encode(typeProvider, typeProvider.empty(), null));
		assertEquals(new JsonPrimitive(1), decoder.encode(typeProvider, typeProvider.empty(), 1));
	}
	
	@Test
	void encodeStart() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Encoder<Integer>  decoder = Codec.INTEGER;
		
		assertThrows(NullPointerException.class, () -> decoder.encodeStart(null, typeProvider.empty(), 1));
		assertThrows(NullPointerException.class, () -> decoder.encodeStart(typeProvider, null, 1));
		
		Result<JsonElement> nullResult = decoder.encodeStart(typeProvider, typeProvider.empty(), null);
		assertTrue(nullResult.isError());
		
		Result<JsonElement> result = decoder.encodeStart(typeProvider, typeProvider.empty(), 1);
		assertTrue(result.isSuccess());
		assertInstanceOf(JsonPrimitive.class, result.orThrow());
		assertEquals(new JsonPrimitive(1), result.orThrow());
	}
	
	@Test
	void mapEncoder() {
		assertThrows(NullPointerException.class, () -> Codec.INTEGER.mapEncoder(null));
		assertNotNull(Codec.INTEGER.mapEncoder(ResultingFunction.direct(Double::intValue)));
	}
}
