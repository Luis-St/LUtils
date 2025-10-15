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

package net.luis.utils.io.codec.internal.io;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.*;
import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link FileCodec}.<br>
 *
 * @author Luis-St
 */
class FileCodecTest {

	@Test
	void encodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<File> codec = new FileCodec();
		File file = new File("/tmp/test");

		assertThrows(NullPointerException.class, () -> codec.encodeStart(null, typeProvider.empty(), file));
		assertThrows(NullPointerException.class, () -> codec.encodeStart(typeProvider, null, file));
	}

	@Test
	void encodeStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<File> codec = new FileCodec();

		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), null);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to encode null as file"));
	}

	@Test
	void encodeStartWithValidFile() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<File> codec = new FileCodec();
		File file = new File("/tmp/test");

		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), file);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("/tmp/test"), result.resultOrThrow());
	}

	@Test
	void encodeStartWithRelativeFile() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<File> codec = new FileCodec();
		File file = new File("relative/path");

		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), file);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("relative/path"), result.resultOrThrow());
	}

	@Test
	void encodeStartWithCurrentDirectory() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<File> codec = new FileCodec();
		File file = new File(".");

		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), file);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("."), result.resultOrThrow());
	}

	@Test
	void decodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<File> codec = new FileCodec();

		assertThrows(NullPointerException.class, () -> codec.decodeStart(null, new JsonPrimitive("/tmp/test")));
	}

	@Test
	void decodeStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<File> codec = new FileCodec();

		Result<File> result = codec.decodeStart(typeProvider, null);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode null value as file"));
	}

	@Test
	void decodeStartWithValidString() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<File> codec = new FileCodec();

		Result<File> result = codec.decodeStart(typeProvider, new JsonPrimitive("/tmp/test"));
		assertTrue(result.isSuccess());
		assertEquals(new File("/tmp/test"), result.resultOrThrow());
	}

	@Test
	void decodeStartWithRelativePath() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<File> codec = new FileCodec();

		Result<File> result = codec.decodeStart(typeProvider, new JsonPrimitive("relative/path"));
		assertTrue(result.isSuccess());
		assertEquals(new File("relative/path"), result.resultOrThrow());
	}

	@Test
	void decodeStartWithCurrentDirectory() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<File> codec = new FileCodec();

		Result<File> result = codec.decodeStart(typeProvider, new JsonPrimitive("."));
		assertTrue(result.isSuccess());
		assertEquals(new File("."), result.resultOrThrow());
	}

	@Test
	void decodeStartWithNonString() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<File> codec = new FileCodec();

		Result<File> result = codec.decodeStart(typeProvider, new JsonPrimitive(42));
		assertTrue(result.isError());
	}

	@Test
	void decodeStartWithEmptyString() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<File> codec = new FileCodec();

		Result<File> result = codec.decodeStart(typeProvider, new JsonPrimitive(""));
		assertTrue(result.isSuccess());
		assertEquals(new File(""), result.resultOrThrow());
	}

	@Test
	void toStringRepresentation() {
		FileCodec codec = new FileCodec();
		assertEquals("FileCodec", codec.toString());
	}
}
