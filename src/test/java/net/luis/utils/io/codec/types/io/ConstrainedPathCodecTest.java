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
import net.luis.utils.io.codec.constraint.config.*;
import net.luis.utils.io.codec.constraint.config.io.FilePathConstraintConfig;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.JsonElement;
import net.luis.utils.io.data.json.JsonPrimitive;
import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link PathCodec} with constraints.<br>
 *
 * @author Luis-St
 */
class ConstrainedPathCodecTest {
	
	@Test
	void encodeStartWithValidConstrainedValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Path expected = Path.of("home", "user", "file.txt");
		Codec<Path> codec = new PathCodec().apply(config -> config.withEqualTo(expected));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), expected);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("home" + File.separator + "user" + File.separator + "file.txt"), result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithValidConstrainedValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Path expected = Path.of("/home/user/file.txt");
		Codec<Path> codec = new PathCodec().apply(config -> config.withEqualTo(expected));
		
		Result<Path> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("/home/user/file.txt"));
		assertTrue(result.isSuccess());
		assertEquals(expected, result.resultOrThrow());
	}
	
	@Test
	void toStringWithConstraints() {
		Path expected = Path.of("/home/user/file.txt");
		Codec<Path> codec = new PathCodec().apply(config -> config.withEqualTo(expected));
		assertTrue(codec.toString().contains("Constrained"));
	}
	
	@Test
	void encodeStartEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Path expected = Path.of("/home/user/file.txt");
		Codec<Path> codec = new PathCodec().apply(config -> config.withEqualTo(expected));
		Path different = Path.of("/tmp/other.txt");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), different);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Path expected = Path.of("/home/user/file.txt");
		Codec<Path> codec = new PathCodec().apply(config -> config.withEqualTo(expected));
		
		Result<Path> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("/tmp/other.txt"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Path excluded = Path.of("/home/user/file.txt");
		Codec<Path> codec = new PathCodec().apply(config -> config.withNotEqualTo(excluded));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), excluded);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Path excluded = Path.of("/home/user/file.txt");
		Codec<Path> codec = new PathCodec().apply(config -> config.withNotEqualTo(excluded));
		
		Result<Path> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("/home/user/file.txt"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		List<Path> allowed = List.of(Path.of("/home/user/file1.txt"), Path.of("/home/user/file2.txt"));
		Codec<Path> codec = new PathCodec().apply(config -> config.withIn(allowed));
		Path notAllowed = Path.of("/tmp/other.txt");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), notAllowed);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		List<Path> allowed = List.of(Path.of("/home/user/file1.txt"), Path.of("/home/user/file2.txt"));
		Codec<Path> codec = new PathCodec().apply(config -> config.withIn(allowed));
		
		Result<Path> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("/tmp/other.txt"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		List<Path> excluded = List.of(Path.of("/home/user/file1.txt"), Path.of("/home/user/file2.txt"));
		Codec<Path> codec = new PathCodec().apply(config -> config.withNotIn(excluded));
		Path excludedValue = Path.of("/home/user/file1.txt");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), excludedValue);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		List<Path> excluded = List.of(Path.of("/home/user/file1.txt"), Path.of("/home/user/file2.txt"));
		Codec<Path> codec = new PathCodec().apply(config -> config.withNotIn(excluded));
		
		Result<Path> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("/home/user/file1.txt"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartLengthConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Path> codec = new PathCodec().apply(config -> config.withLength(LengthConstraintConfig.UNCONSTRAINED.withMaxLength(10)));
		Path longPath = Path.of("/home/user/very/long/path/to/file.txt");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), longPath);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartLengthConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Path> codec = new PathCodec().apply(config -> config.withLength(LengthConstraintConfig.UNCONSTRAINED.withMaxLength(10)));
		
		Result<Path> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("/home/user/very/long/path/to/file.txt"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartLengthSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Path> codec = new PathCodec().apply(config -> config.withLength(LengthConstraintConfig.UNCONSTRAINED.withMaxLength(50)));
		Path shortPath = Path.of("/home/user/file.txt");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), shortPath);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartDepthConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Path> codec = new PathCodec().apply(config -> config.withDepth(DepthConstraintConfig.UNCONSTRAINED.withMaxDepth(2)));
		Path deepPath = Path.of("home/user/documents/files/data.txt");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), deepPath);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartDepthConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Path> codec = new PathCodec().apply(config -> config.withDepth(DepthConstraintConfig.UNCONSTRAINED.withMaxDepth(2)));
		
		Result<Path> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("home/user/documents/files/data.txt"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartDepthSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Path> codec = new PathCodec().apply(config -> config.withDepth(DepthConstraintConfig.UNCONSTRAINED.withMaxDepth(3)));
		Path shallowPath = Path.of("home/user/file.txt");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), shallowPath);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartAbsoluteConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Path> codec = new PathCodec().apply(FilePathConstraintConfig::withAbsolute);
		Path relativePath = Path.of("home/user/file.txt");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), relativePath);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartAbsoluteConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Path> codec = new PathCodec().apply(FilePathConstraintConfig::withAbsolute);
		
		Result<Path> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("home/user/file.txt"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartAbsoluteSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Path> codec = new PathCodec().apply(FilePathConstraintConfig::withAbsolute);
		Path absolutePath = Path.of("").toAbsolutePath().resolve("file.txt");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), absolutePath);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartRelativeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Path> codec = new PathCodec().apply(FilePathConstraintConfig::withRelative);
		Path absolutePath = Path.of("").toAbsolutePath().resolve("file.txt");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), absolutePath);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartRelativeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Path> codec = new PathCodec().apply(FilePathConstraintConfig::withRelative);
		String absolutePathString = Path.of("").toAbsolutePath().resolve("file.txt").toString();
		
		Result<Path> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(absolutePathString));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartRelativeSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Path> codec = new PathCodec().apply(FilePathConstraintConfig::withRelative);
		Path relativePath = Path.of("home/user/file.txt");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), relativePath);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartNormalizedConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Path> codec = new PathCodec().apply(FilePathConstraintConfig::withNormalized);
		Path nonNormalizedPath = Path.of("/home/user/../user/./file.txt");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), nonNormalizedPath);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartNormalizedConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Path> codec = new PathCodec().apply(FilePathConstraintConfig::withNormalized);
		
		Result<Path> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("/home/user/../user/./file.txt"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNormalizedSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Path> codec = new PathCodec().apply(FilePathConstraintConfig::withNormalized);
		Path normalizedPath = Path.of("/home/user/file.txt");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), normalizedPath);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartPathStringConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Path> codec = new PathCodec().apply(config -> config.withPath(StringConstraintConfig.UNCONSTRAINED.withStartsWith("/home")));
		Path path = Path.of("/tmp/file.txt");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), path);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartPathStringConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Path> codec = new PathCodec().apply(config -> config.withPath(StringConstraintConfig.UNCONSTRAINED.withStartsWith("/home")));
		
		Result<Path> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("/tmp/file.txt"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartFileNameConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Path> codec = new PathCodec().apply(config -> config.withFile(StringConstraintConfig.UNCONSTRAINED.withEndsWith(".txt")));
		Path path = Path.of("/home/user/file.json");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), path);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartFileNameConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Path> codec = new PathCodec().apply(config -> config.withFile(StringConstraintConfig.UNCONSTRAINED.withEndsWith(".txt")));
		
		Result<Path> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("/home/user/file.json"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartFileNameSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Path> codec = new PathCodec().apply(config -> config.withFile(StringConstraintConfig.UNCONSTRAINED.withEndsWith(".txt")));
		Path path = Path.of("/home/user/file.txt");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), path);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartExtensionConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Path> codec = new PathCodec().apply(config -> config.withExtension(StringConstraintConfig.UNCONSTRAINED.withEqualTo("txt")));
		Path path = Path.of("/home/user/file.json");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), path);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartExtensionConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Path> codec = new PathCodec().apply(config -> config.withExtension(StringConstraintConfig.UNCONSTRAINED.withEqualTo("txt")));
		
		Result<Path> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("/home/user/file.json"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartExtensionSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Path> codec = new PathCodec().apply(config -> config.withExtension(StringConstraintConfig.UNCONSTRAINED.withEqualTo("txt")));
		Path path = Path.of("/home/user/file.txt");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), path);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartWithoutExtensionConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Path> codec = new PathCodec().apply(FilePathConstraintConfig::withWithoutExtension);
		Path pathWithExtension = Path.of("/home/user/file.txt");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), pathWithExtension);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartWithoutExtensionConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Path> codec = new PathCodec().apply(FilePathConstraintConfig::withWithoutExtension);
		
		Result<Path> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("/home/user/file.txt"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartWithoutExtensionSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Path> codec = new PathCodec().apply(FilePathConstraintConfig::withWithoutExtension);
		Path pathWithoutExtension = Path.of("/home/user/file");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), pathWithoutExtension);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartDescendantOfConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Path> codec = new PathCodec().apply(config -> config.withDescendantOf(Set.of("/home/user")));
		Path path = Path.of("/tmp/file.txt");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), path);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartDescendantOfConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Path> codec = new PathCodec().apply(config -> config.withDescendantOf(Set.of("/home/user")));
		
		Result<Path> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("/tmp/file.txt"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartDescendantOfSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Path> codec = new PathCodec().apply(config -> config.withDescendantOf(Set.of("/home/user")));
		Path path = Path.of("/home/user/documents/file.txt");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), path);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartAncestorOfConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Path> codec = new PathCodec().apply(config -> config.withAncestorOf(Set.of("/home/user/documents/file.txt")));
		Path path = Path.of("/tmp");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), path);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartAncestorOfConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Path> codec = new PathCodec().apply(config -> config.withAncestorOf(Set.of("/home/user/documents/file.txt")));
		
		Result<Path> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("/tmp"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartAncestorOfSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Path> codec = new PathCodec().apply(config -> config.withAncestorOf(Set.of("/home/user/documents/file.txt")));
		Path path = Path.of("/home/user");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), path);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Path> codec = new PathCodec().apply(config -> config.withCustom(value -> Result.error("Custom validation failed")));
		Path path = Path.of("/home/user/file.txt");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), path);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Path> codec = new PathCodec().apply(config -> config.withCustom(value -> Result.error("Custom validation failed")));
		
		Result<Path> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("/home/user/file.txt"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartCustomConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Path> codec = new PathCodec().apply(config -> config.withCustom(value -> Result.success()));
		Path path = Path.of("/home/user/file.txt");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), path);
		assertTrue(result.isSuccess());
	}
}
