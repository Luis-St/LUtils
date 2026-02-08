/*
 * LUtils
 * Copyright (C) 2026 Luis Staudt
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
import net.luis.utils.io.codec.decoder.DecoderException;
import net.luis.utils.io.codec.encoder.EncoderException;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.JsonElement;
import net.luis.utils.io.data.json.JsonPrimitive;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link PathCodec}.<br>
 *
 * @author Luis-St
 */
class PathCodecTest {
	
	@Test
	void encodeNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Path> codec = new PathCodec();
		Path path = Path.of("/tmp/test");
		
		assertThrows(NullPointerException.class, () -> codec.encode(null, typeProvider.empty(), path));
		assertThrows(NullPointerException.class, () -> codec.encode(typeProvider, null, path));
	}
	
	@Test
	void encodeWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Path> codec = new PathCodec();
		
		EncoderException exception = assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), null));
		assertTrue(exception.getMessage().contains("Unable to encode null as path"));
	}
	
	@Test
	void encodeWithValidPath() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Path> codec = new PathCodec();
		Path path = Path.of("/tmp/test");
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), path);
		assertEquals(new JsonPrimitive(File.separator + "tmp" + File.separator + "test"), result);
	}
	
	@Test
	void encodeWithRelativePath() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Path> codec = new PathCodec();
		Path path = Path.of("relative/path");
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), path);
		assertEquals(new JsonPrimitive("relative" + File.separator + "path"), result);
	}
	
	@Test
	void encodeWithCurrentDirectory() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Path> codec = new PathCodec();
		Path path = Path.of(".");
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), path);
		assertEquals(new JsonPrimitive("."), result);
	}
	
	@Test
	void decodeNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Path> codec = new PathCodec();
		
		assertThrows(NullPointerException.class, () -> codec.decode(null, typeProvider.empty(), new JsonPrimitive("/tmp/test")));
	}
	
	@Test
	void decodeWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Path> codec = new PathCodec();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), null));
		assertTrue(exception.getMessage().contains("Unable to decode null as path"));
	}
	
	@Test
	void decodeWithValidString() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Path> codec = new PathCodec();
		
		Path result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("/tmp/test"));
		assertEquals(Path.of("/tmp/test"), result);
	}
	
	@Test
	void decodeWithRelativePath() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Path> codec = new PathCodec();
		
		Path result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("relative/path"));
		assertEquals(Path.of("relative/path"), result);
	}
	
	@Test
	void decodeWithCurrentDirectory() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Path> codec = new PathCodec();
		
		Path result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("."));
		assertEquals(Path.of("."), result);
	}
	
	@Test
	void decodeWithNonString() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Path> codec = new PathCodec();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(42)));
	}
	
	@Test
	void decodeWithEmptyString() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Path> codec = new PathCodec();
		
		Path result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(""));
		assertEquals(Path.of(""), result);
	}
	
	@Test
	void toStringRepresentation() {
		PathCodec codec = new PathCodec();
		assertEquals("PathCodec", codec.toString());
	}
}
