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

package net.luis.utils.io.codec.decoder;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.ResultMappingFunction;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.JsonPrimitive;
import net.luis.utils.util.Result;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link Decoder}.<br>
 *
 * @author Luis-St
 */
class DecoderTest {
	
	@Test
	void decode() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Decoder<Integer>  decoder = Codec.INTEGER;
		
		assertThrows(NullPointerException.class, () -> decoder.decode(null, new JsonPrimitive(1)));
		assertThrows(IllegalStateException.class, () -> decoder.decode(typeProvider, null));
		assertEquals(1, decoder.decode(typeProvider, new JsonPrimitive(1)));
	}
	
	@Test
	void decodeStart() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Decoder<Integer>  decoder = Codec.INTEGER;
		
		assertThrows(NullPointerException.class, () -> decoder.decodeStart(null, new JsonPrimitive(1)));
		
		Result<Integer> nullResult = decoder.decodeStart(typeProvider, null);
		assertTrue(nullResult.isError());
		
		Result<Integer> result = decoder.decodeStart(typeProvider, new JsonPrimitive(1));
		assertTrue(result.isSuccess());
		assertEquals(1, result.orThrow());
	}
	
	@Test
	void mapDecoder() {
		assertThrows(NullPointerException.class, () -> Codec.INTEGER.mapDecoder(null));
		assertNotNull(Codec.INTEGER.mapDecoder(ResultMappingFunction.direct(Integer::doubleValue)));
	}
}
