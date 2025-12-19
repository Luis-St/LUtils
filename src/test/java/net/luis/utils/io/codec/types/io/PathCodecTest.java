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

package net.luis.utils.io.codec.types.io;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.JsonElement;
import net.luis.utils.io.data.json.JsonPrimitive;
import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link PathCodec}.<br>
 *
 * @author Luis-St
 */
class PathCodecTest {
	
	@Test
	void encodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Path> codec = new PathCodec();
		Path path = Path.of("/tmp/test");
		
		assertThrows(NullPointerException.class, () -> codec.encodeStart(null, typeProvider.empty(), path));
		assertThrows(NullPointerException.class, () -> codec.encodeStart(typeProvider, null, path));
	}
	
	@Test
	void encodeStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Path> codec = new PathCodec();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), null);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to encode null as path"));
	}
	
	@Test
	void encodeStartWithValidPath() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Path> codec = new PathCodec();
		Path path = Path.of("/tmp/test");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), path);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("/tmp/test"), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithRelativePath() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Path> codec = new PathCodec();
		Path path = Path.of("relative/path");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), path);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("relative/path"), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithCurrentDirectory() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Path> codec = new PathCodec();
		Path path = Path.of(".");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), path);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("."), result.resultOrThrow());
	}
	
	@Test
	void decodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Path> codec = new PathCodec();
		
		assertThrows(NullPointerException.class, () -> codec.decodeStart(null, typeProvider.empty(), new JsonPrimitive("/tmp/test")));
	}
	
	@Test
	void decodeStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Path> codec = new PathCodec();
		
		Result<Path> result = codec.decodeStart(typeProvider, typeProvider.empty(), null);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode null value as path"));
	}
	
	@Test
	void decodeStartWithValidString() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Path> codec = new PathCodec();
		
		Result<Path> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("/tmp/test"));
		assertTrue(result.isSuccess());
		assertEquals(Path.of("/tmp/test"), result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithRelativePath() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Path> codec = new PathCodec();
		
		Result<Path> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("relative/path"));
		assertTrue(result.isSuccess());
		assertEquals(Path.of("relative/path"), result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithCurrentDirectory() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Path> codec = new PathCodec();
		
		Result<Path> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("."));
		assertTrue(result.isSuccess());
		assertEquals(Path.of("."), result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithNonString() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Path> codec = new PathCodec();
		
		Result<Path> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(42));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartWithEmptyString() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Path> codec = new PathCodec();
		
		Result<Path> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(""));
		assertTrue(result.isSuccess());
		assertEquals(Path.of(""), result.resultOrThrow());
	}
	
	@Test
	void toStringRepresentation() {
		PathCodec codec = new PathCodec();
		assertEquals("PathCodec", codec.toString());
	}
}
