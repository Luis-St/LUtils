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

package net.luis.utils.io.codec.types.time.zoned;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.JsonElement;
import net.luis.utils.io.data.json.JsonPrimitive;
import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link ZoneOffsetCodec}.<br>
 *
 * @author Luis-St
 */
class ZoneOffsetCodecTest {
	
	@Test
	void encodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneOffset> codec = new ZoneOffsetCodec();
		ZoneOffset offset = ZoneOffset.of("+02:00");
		
		assertThrows(NullPointerException.class, () -> codec.encodeStart(null, typeProvider.empty(), offset));
		assertThrows(NullPointerException.class, () -> codec.encodeStart(typeProvider, null, offset));
	}
	
	@Test
	void encodeStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneOffset> codec = new ZoneOffsetCodec();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), null);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to encode null as zone offset"));
	}
	
	@Test
	void encodeStartWithPositiveOffset() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneOffset> codec = new ZoneOffsetCodec();
		ZoneOffset offset = ZoneOffset.of("+02:00");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), offset);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("+02:00"), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithNegativeOffset() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneOffset> codec = new ZoneOffsetCodec();
		ZoneOffset offset = ZoneOffset.of("-05:00");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), offset);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("-05:00"), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithUTC() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneOffset> codec = new ZoneOffsetCodec();
		ZoneOffset offset = ZoneOffset.UTC;
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), offset);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("Z"), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithMaxOffset() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneOffset> codec = new ZoneOffsetCodec();
		ZoneOffset offset = ZoneOffset.MAX;
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), offset);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("+18:00"), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithMinOffset() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneOffset> codec = new ZoneOffsetCodec();
		ZoneOffset offset = ZoneOffset.MIN;
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), offset);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("-18:00"), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithOffsetWithMinutes() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneOffset> codec = new ZoneOffsetCodec();
		ZoneOffset offset = ZoneOffset.of("+05:30");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), offset);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("+05:30"), result.resultOrThrow());
	}
	
	@Test
	void encodeKeyNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneOffset> codec = new ZoneOffsetCodec();
		
		assertThrows(NullPointerException.class, () -> codec.encodeKey(null));
	}
	
	@Test
	void encodeKeyWithOffset() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneOffset> codec = new ZoneOffsetCodec();
		ZoneOffset offset = ZoneOffset.of("+02:00");
		
		Result<String> result = codec.encodeKey(offset);
		assertTrue(result.isSuccess());
		assertEquals("+02:00", result.resultOrThrow());
	}
	
	@Test
	void encodeKeyWithUTC() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneOffset> codec = new ZoneOffsetCodec();
		ZoneOffset offset = ZoneOffset.UTC;
		
		Result<String> result = codec.encodeKey(offset);
		assertTrue(result.isSuccess());
		assertEquals("Z", result.resultOrThrow());
	}
	
	@Test
	void decodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneOffset> codec = new ZoneOffsetCodec();
		
		assertThrows(NullPointerException.class, () -> codec.decodeStart(null, typeProvider.empty(), new JsonPrimitive("+02:00")));
	}
	
	@Test
	void decodeStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneOffset> codec = new ZoneOffsetCodec();
		
		Result<ZoneOffset> result = codec.decodeStart(typeProvider, typeProvider.empty(), null);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode null value as zone offset"));
	}
	
	@Test
	void decodeStartWithValidPositiveOffset() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneOffset> codec = new ZoneOffsetCodec();
		
		Result<ZoneOffset> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("+02:00"));
		assertTrue(result.isSuccess());
		assertEquals(ZoneOffset.of("+02:00"), result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithValidNegativeOffset() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneOffset> codec = new ZoneOffsetCodec();
		
		Result<ZoneOffset> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("-05:00"));
		assertTrue(result.isSuccess());
		assertEquals(ZoneOffset.of("-05:00"), result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithUTC() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneOffset> codec = new ZoneOffsetCodec();
		
		Result<ZoneOffset> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("Z"));
		assertTrue(result.isSuccess());
		assertEquals(ZoneOffset.UTC, result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithOffsetWithMinutes() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneOffset> codec = new ZoneOffsetCodec();
		
		Result<ZoneOffset> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("+05:30"));
		assertTrue(result.isSuccess());
		assertEquals(ZoneOffset.of("+05:30"), result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithInvalidOffset() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneOffset> codec = new ZoneOffsetCodec();
		
		Result<ZoneOffset> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("invalid"));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode zone offset"));
	}
	
	@Test
	void decodeStartWithOutOfRangeOffset() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneOffset> codec = new ZoneOffsetCodec();
		
		Result<ZoneOffset> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("+20:00"));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode zone offset"));
	}
	
	@Test
	void decodeStartWithNonString() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneOffset> codec = new ZoneOffsetCodec();
		
		Result<ZoneOffset> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(42));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneOffset> codec = new ZoneOffsetCodec();
		
		assertThrows(NullPointerException.class, () -> codec.decodeKey(null));
	}
	
	@Test
	void decodeKeyWithValidOffset() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneOffset> codec = new ZoneOffsetCodec();
		
		Result<ZoneOffset> result = codec.decodeKey("+02:00");
		assertTrue(result.isSuccess());
		assertEquals(ZoneOffset.of("+02:00"), result.resultOrThrow());
	}
	
	@Test
	void decodeKeyWithUTC() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneOffset> codec = new ZoneOffsetCodec();
		
		Result<ZoneOffset> result = codec.decodeKey("Z");
		assertTrue(result.isSuccess());
		assertEquals(ZoneOffset.UTC, result.resultOrThrow());
	}
	
	@Test
	void decodeKeyWithInvalidOffset() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneOffset> codec = new ZoneOffsetCodec();
		
		Result<ZoneOffset> result = codec.decodeKey("invalid");
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode key 'invalid' as zone offset"));
	}
	
	@Test
	void toStringRepresentation() {
		ZoneOffsetCodec codec = new ZoneOffsetCodec();
		assertEquals("ZoneOffsetCodec", codec.toString());
	}
}
