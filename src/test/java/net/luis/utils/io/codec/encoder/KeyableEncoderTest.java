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

package net.luis.utils.io.codec.encoder;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.util.Result;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link KeyableEncoder}.<br>
 *
 * @author Luis-St
 */
class KeyableEncoderTest {
	
	@Test
	void of() {
		assertThrows(NullPointerException.class, () -> KeyableEncoder.of(null, String::valueOf));
		assertThrows(NullPointerException.class, () -> KeyableEncoder.of(Codec.INTEGER, null));
		assertNotNull(KeyableEncoder.of(Codec.INTEGER, String::valueOf));
	}
	
	@Test
	void encodeKey() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		KeyableEncoder<Integer> decoder = Codec.INTEGER;
		
		assertThrows(NullPointerException.class, () -> decoder.encodeKey(null, 1));
		assertThrows(NullPointerException.class, () -> decoder.encodeKey(typeProvider, null));
		
		Result<String> result = decoder.encodeKey(typeProvider, 1);
		assertTrue(result.isSuccess());
		assertEquals("1", result.orThrow());
	}
}
