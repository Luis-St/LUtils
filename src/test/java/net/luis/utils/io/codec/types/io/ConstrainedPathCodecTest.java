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
import net.luis.utils.io.codec.Codecs;
import net.luis.utils.io.codec.decoder.DecoderException;
import net.luis.utils.io.codec.encoder.EncoderException;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.JsonElement;
import net.luis.utils.io.data.json.JsonPrimitive;
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
	void encodeWithValidConstrainedValue() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Path expected = Path.of("home", "user", "file.txt");
		Codec<Path> codec = Codecs.PATH.equalTo(expected);
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), expected);
		assertEquals(new JsonPrimitive("home" + File.separator + "user" + File.separator + "file.txt"), result);
	}
	
	@Test
	void decodeWithValidConstrainedValue() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Path expected = Path.of("/home/user/file.txt");
		Codec<Path> codec = Codecs.PATH.equalTo(expected);
		
		Path result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("/home/user/file.txt"));
		assertEquals(expected, result);
	}
	
	@Test
	void toStringWithConstraints() {
		Path expected = Path.of("/home/user/file.txt");
		Codec<Path> codec = Codecs.PATH.equalTo(expected);
		assertTrue(codec.toString().contains("Constrained"));
	}
	
	@Test
	void encodeEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Path expected = Path.of("/home/user/file.txt");
		Codec<Path> codec = Codecs.PATH.equalTo(expected);
		Path different = Path.of("/tmp/other.txt");
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), different));
	}
	
	@Test
	void decodeEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Path expected = Path.of("/home/user/file.txt");
		Codec<Path> codec = Codecs.PATH.equalTo(expected);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("/tmp/other.txt")));
	}
	
	@Test
	void encodeNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Path excluded = Path.of("/home/user/file.txt");
		Codec<Path> codec = Codecs.PATH.notEqualTo(excluded);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), excluded));
	}
	
	@Test
	void decodeNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Path excluded = Path.of("/home/user/file.txt");
		Codec<Path> codec = Codecs.PATH.notEqualTo(excluded);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("/home/user/file.txt")));
	}
	
	@Test
	void encodeInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		List<Path> allowed = List.of(Path.of("/home/user/file1.txt"), Path.of("/home/user/file2.txt"));
		Codec<Path> codec = Codecs.PATH.in(allowed);
		Path notAllowed = Path.of("/tmp/other.txt");
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), notAllowed));
	}
	
	@Test
	void decodeInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		List<Path> allowed = List.of(Path.of("/home/user/file1.txt"), Path.of("/home/user/file2.txt"));
		Codec<Path> codec = Codecs.PATH.in(allowed);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("/tmp/other.txt")));
	}
	
	@Test
	void encodeNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		List<Path> excluded = List.of(Path.of("/home/user/file1.txt"), Path.of("/home/user/file2.txt"));
		Codec<Path> codec = Codecs.PATH.notIn(excluded);
		Path excludedValue = Path.of("/home/user/file1.txt");
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), excludedValue));
	}
	
	@Test
	void decodeNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		List<Path> excluded = List.of(Path.of("/home/user/file1.txt"), Path.of("/home/user/file2.txt"));
		Codec<Path> codec = Codecs.PATH.notIn(excluded);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("/home/user/file1.txt")));
	}
	
	@Test
	void encodeLengthConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Path> codec = Codecs.PATH.length(builder -> builder.maxLength(10));
		Path longPath = Path.of("/home/user/very/long/path/to/file.txt");
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), longPath));
	}
	
	@Test
	void decodeLengthConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Path> codec = Codecs.PATH.length(builder -> builder.maxLength(10));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("/home/user/very/long/path/to/file.txt")));
	}
	
	@Test
	void encodeLengthSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Path> codec = Codecs.PATH.length(builder -> builder.maxLength(50));
		Path shortPath = Path.of("/home/user/file.txt");
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), shortPath));
	}
	
	@Test
	void encodeDepthConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Path> codec = Codecs.PATH.depth(builder -> builder.maxDepth(2));
		Path deepPath = Path.of("home/user/documents/files/data.txt");
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), deepPath));
	}
	
	@Test
	void decodeDepthConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Path> codec = Codecs.PATH.depth(builder -> builder.maxDepth(2));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("home/user/documents/files/data.txt")));
	}
	
	@Test
	void encodeDepthSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Path> codec = Codecs.PATH.depth(builder -> builder.maxDepth(3));
		Path shallowPath = Path.of("home/user/file.txt");
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), shallowPath));
	}
	
	@Test
	void encodeAbsoluteConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Path> codec = Codecs.PATH.absolute();
		Path relativePath = Path.of("home/user/file.txt");
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), relativePath));
	}
	
	@Test
	void decodeAbsoluteConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Path> codec = Codecs.PATH.absolute();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("home/user/file.txt")));
	}
	
	@Test
	void encodeAbsoluteSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Path> codec = Codecs.PATH.absolute();
		Path absolutePath = Path.of("").toAbsolutePath().resolve("file.txt");
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), absolutePath));
	}
	
	@Test
	void encodeRelativeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Path> codec = Codecs.PATH.relative();
		Path absolutePath = Path.of("").toAbsolutePath().resolve("file.txt");
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), absolutePath));
	}
	
	@Test
	void decodeRelativeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Path> codec = Codecs.PATH.relative();
		String absolutePathString = Path.of("").toAbsolutePath().resolve("file.txt").toString();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(absolutePathString)));
	}
	
	@Test
	void encodeRelativeSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Path> codec = Codecs.PATH.relative();
		Path relativePath = Path.of("home/user/file.txt");
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), relativePath));
	}
	
	@Test
	void encodeNormalizedConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Path> codec = Codecs.PATH.normalized();
		Path nonNormalizedPath = Path.of("/home/user/../user/./file.txt");
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), nonNormalizedPath));
	}
	
	@Test
	void decodeNormalizedConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Path> codec = Codecs.PATH.normalized();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("/home/user/../user/./file.txt")));
	}
	
	@Test
	void encodeNormalizedSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Path> codec = Codecs.PATH.normalized();
		Path normalizedPath = Path.of("/home/user/file.txt");
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), normalizedPath));
	}
	
	@Test
	void encodePathStringConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Path> codec = Codecs.PATH.path(builder -> builder.startsWith("/home"));
		Path path = Path.of("/tmp/file.txt");
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), path));
	}
	
	@Test
	void decodePathStringConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Path> codec = Codecs.PATH.path(builder -> builder.startsWith("/home"));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("/tmp/file.txt")));
	}
	
	@Test
	void encodeFileNameConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Path> codec = Codecs.PATH.file(builder -> builder.endsWith(".txt"));
		Path path = Path.of("/home/user/file.json");
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), path));
	}
	
	@Test
	void decodeFileNameConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Path> codec = Codecs.PATH.file(builder -> builder.endsWith(".txt"));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("/home/user/file.json")));
	}
	
	@Test
	void encodeFileNameSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Path> codec = Codecs.PATH.file(builder -> builder.endsWith(".txt"));
		Path path = Path.of("/home/user/file.txt");
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), path));
	}
	
	@Test
	void encodeExtensionConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Path> codec = Codecs.PATH.extension(builder -> builder.equalTo("txt"));
		Path path = Path.of("/home/user/file.json");
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), path));
	}
	
	@Test
	void decodeExtensionConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Path> codec = Codecs.PATH.extension(builder -> builder.equalTo("txt"));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("/home/user/file.json")));
	}
	
	@Test
	void encodeExtensionSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Path> codec = Codecs.PATH.extension(builder -> builder.equalTo("txt"));
		Path path = Path.of("/home/user/file.txt");
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), path));
	}
	
	@Test
	void encodeWithoutExtensionConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Path> codec = Codecs.PATH.withoutExtension();
		Path pathWithExtension = Path.of("/home/user/file.txt");
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), pathWithExtension));
	}
	
	@Test
	void decodeWithoutExtensionConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Path> codec = Codecs.PATH.withoutExtension();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("/home/user/file.txt")));
	}
	
	@Test
	void encodeWithoutExtensionSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Path> codec = Codecs.PATH.withoutExtension();
		Path pathWithoutExtension = Path.of("/home/user/file");
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), pathWithoutExtension));
	}
	
	@Test
	void encodeDescendantOfConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Path> codec = Codecs.PATH.descendantOf(Set.of("/home/user"));
		Path path = Path.of("/tmp/file.txt");
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), path));
	}
	
	@Test
	void decodeDescendantOfConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Path> codec = Codecs.PATH.descendantOf(Set.of("/home/user"));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("/tmp/file.txt")));
	}
	
	@Test
	void encodeDescendantOfSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Path> codec = Codecs.PATH.descendantOf(Set.of("/home/user"));
		Path path = Path.of("/home/user/documents/file.txt");
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), path));
	}
	
	@Test
	void encodeAncestorOfConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Path> codec = Codecs.PATH.ancestorOf(Set.of("/home/user/documents/file.txt"));
		Path path = Path.of("/tmp");
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), path));
	}
	
	@Test
	void decodeAncestorOfConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Path> codec = Codecs.PATH.ancestorOf(Set.of("/home/user/documents/file.txt"));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("/tmp")));
	}
	
	@Test
	void encodeAncestorOfSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Path> codec = Codecs.PATH.ancestorOf(Set.of("/home/user/documents/file.txt"));
		Path path = Path.of("/home/user");
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), path));
	}
	
	@Test
	void encodeCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Path> codec = Codecs.PATH.custom(value -> {
			throw new net.luis.utils.io.codec.constraint.config.validator.ConstraintViolateException("Custom validation failed");
		});
		Path path = Path.of("/home/user/file.txt");
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), path));
	}
	
	@Test
	void decodeCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Path> codec = Codecs.PATH.custom(value -> {
			throw new net.luis.utils.io.codec.constraint.config.validator.ConstraintViolateException("Custom validation failed");
		});
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("/home/user/file.txt")));
	}
	
	@Test
	void encodeCustomConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Path> codec = Codecs.PATH.custom(value -> {});
		Path path = Path.of("/home/user/file.txt");
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), path));
	}
}
