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

package net.luis.utils.io.codec.decoder;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.util.Result;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link KeyableDecoder}.<br>
 *
 * @author Luis-St
 */
class KeyableDecoderTest {
	
	@Test
	void of() {
		assertThrows(NullPointerException.class, () -> KeyableDecoder.of(null, Integer::valueOf));
		assertThrows(NullPointerException.class, () -> KeyableDecoder.of(Codec.INTEGER, null));
		assertNotNull(KeyableDecoder.of(Codec.INTEGER, Integer::valueOf));
	}
	
	@Test
	void decodeKey() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		KeyableDecoder<Integer> decoder = Codec.INTEGER;
		
		assertThrows(NullPointerException.class, () -> decoder.decodeKey(null, "1"));
		assertThrows(NullPointerException.class, () -> decoder.decodeKey(typeProvider, null));
		
		Result<Integer> invalidResult = decoder.decodeKey(typeProvider, "invalid");
		assertTrue(invalidResult.isError());
		
		Result<Integer> result = decoder.decodeKey(typeProvider, "1");
		assertTrue(result.isSuccess());
		assertEquals(1, result.orThrow());
	}
}
