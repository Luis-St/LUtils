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

package net.luis.utils.io.codec.types.primitive;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.Codecs;
import net.luis.utils.io.codec.decoder.DecoderException;
import net.luis.utils.io.codec.encoder.EncoderException;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.JsonElement;
import net.luis.utils.io.data.json.JsonPrimitive;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link StringCodec} with constraints.<br>
 *
 * @author Luis-St
 */
class ConstrainedStringCodecTest {
	
	@Test
	void encodeWithValidConstrainedValue() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codecs.STRING.length(builder -> builder.minLength(1));
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), "hello");
		assertEquals(new JsonPrimitive("hello"), result);
	}
	
	@Test
	void decodeWithValidConstrainedValue() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codecs.STRING.length(builder -> builder.minLength(1));
		
		String result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("hello"));
		assertEquals("hello", result);
	}
	
	@Test
	void encodeKeyWithValidConstrainedValue() throws EncoderException {
		Codec<String> codec = Codecs.STRING.length(builder -> builder.minLength(1));
		
		String result = codec.encodeKey("hello");
		assertEquals("hello", result);
	}
	
	@Test
	void decodeKeyWithValidConstrainedValue() throws DecoderException {
		Codec<String> codec = Codecs.STRING.length(builder -> builder.minLength(1));
		
		String result = codec.decodeKey("hello");
		assertEquals("hello", result);
	}
	
	@Test
	void toStringWithConstraints() {
		Codec<String> codec = Codecs.STRING.length(builder -> builder.minLength(1));
		assertTrue(codec.toString().contains("Constrained"));
	}
	
	@Test
	void encodeEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codecs.STRING.equalTo("expected");
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), "expected"));
	}
	
	@Test
	void decodeEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codecs.STRING.equalTo("expected");
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("expected")));
	}
	
	@Test
	void encodeKeyEqualToConstraintSuccess() {
		Codec<String> codec = Codecs.STRING.equalTo("expected");
		
		assertDoesNotThrow(() -> codec.encodeKey("expected"));
	}
	
	@Test
	void decodeKeyEqualToConstraintSuccess() {
		Codec<String> codec = Codecs.STRING.equalTo("expected");
		
		assertDoesNotThrow(() -> codec.decodeKey("expected"));
	}
	
	@Test
	void encodeEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codecs.STRING.equalTo("expected");
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), "different"));
	}
	
	@Test
	void decodeEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codecs.STRING.equalTo("expected");
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("different")));
	}
	
	@Test
	void encodeKeyEqualToConstraintViolation() {
		Codec<String> codec = Codecs.STRING.equalTo("expected");
		
		assertThrows(EncoderException.class, () -> codec.encodeKey("different"));
	}
	
	@Test
	void decodeKeyEqualToConstraintViolation() {
		Codec<String> codec = Codecs.STRING.equalTo("expected");
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("different"));
	}
	
	@Test
	void encodeNotEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codecs.STRING.notEqualTo("forbidden");
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), "allowed"));
	}
	
	@Test
	void decodeNotEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codecs.STRING.notEqualTo("forbidden");
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("allowed")));
	}
	
	@Test
	void encodeKeyNotEqualToConstraintSuccess() {
		Codec<String> codec = Codecs.STRING.notEqualTo("forbidden");
		
		assertDoesNotThrow(() -> codec.encodeKey("allowed"));
	}
	
	@Test
	void decodeKeyNotEqualToConstraintSuccess() {
		Codec<String> codec = Codecs.STRING.notEqualTo("forbidden");
		
		assertDoesNotThrow(() -> codec.decodeKey("allowed"));
	}
	
	@Test
	void encodeNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codecs.STRING.notEqualTo("forbidden");
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), "forbidden"));
	}
	
	@Test
	void decodeNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codecs.STRING.notEqualTo("forbidden");
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("forbidden")));
	}
	
	@Test
	void encodeKeyNotEqualToConstraintViolation() {
		Codec<String> codec = Codecs.STRING.notEqualTo("forbidden");
		
		assertThrows(EncoderException.class, () -> codec.encodeKey("forbidden"));
	}
	
	@Test
	void decodeKeyNotEqualToConstraintViolation() {
		Codec<String> codec = Codecs.STRING.notEqualTo("forbidden");
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("forbidden"));
	}
	
	@Test
	void encodeInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codecs.STRING.in(Set.of("apple", "banana", "cherry"));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), "banana"));
	}
	
	@Test
	void decodeInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codecs.STRING.in(Set.of("apple", "banana", "cherry"));
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("banana")));
	}
	
	@Test
	void encodeKeyInConstraintSuccess() {
		Codec<String> codec = Codecs.STRING.in(Set.of("apple", "banana", "cherry"));
		
		assertDoesNotThrow(() -> codec.encodeKey("banana"));
	}
	
	@Test
	void decodeKeyInConstraintSuccess() {
		Codec<String> codec = Codecs.STRING.in(Set.of("apple", "banana", "cherry"));
		
		assertDoesNotThrow(() -> codec.decodeKey("banana"));
	}
	
	@Test
	void encodeInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codecs.STRING.in(Set.of("apple", "banana", "cherry"));
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), "mango"));
	}
	
	@Test
	void decodeInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codecs.STRING.in(Set.of("apple", "banana", "cherry"));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("mango")));
	}
	
	@Test
	void encodeKeyInConstraintViolation() {
		Codec<String> codec = Codecs.STRING.in(Set.of("apple", "banana", "cherry"));
		
		assertThrows(EncoderException.class, () -> codec.encodeKey("mango"));
	}
	
	@Test
	void decodeKeyInConstraintViolation() {
		Codec<String> codec = Codecs.STRING.in(Set.of("apple", "banana", "cherry"));
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("mango"));
	}
	
	@Test
	void encodeNotInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codecs.STRING.notIn(Set.of("forbidden1", "forbidden2"));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), "allowed"));
	}
	
	@Test
	void decodeNotInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codecs.STRING.notIn(Set.of("forbidden1", "forbidden2"));
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("allowed")));
	}
	
	@Test
	void encodeKeyNotInConstraintSuccess() {
		Codec<String> codec = Codecs.STRING.notIn(Set.of("forbidden1", "forbidden2"));
		
		assertDoesNotThrow(() -> codec.encodeKey("allowed"));
	}
	
	@Test
	void decodeKeyNotInConstraintSuccess() {
		Codec<String> codec = Codecs.STRING.notIn(Set.of("forbidden1", "forbidden2"));
		
		assertDoesNotThrow(() -> codec.decodeKey("allowed"));
	}
	
	@Test
	void encodeNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codecs.STRING.notIn(Set.of("forbidden1", "forbidden2"));
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), "forbidden1"));
	}
	
	@Test
	void decodeNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codecs.STRING.notIn(Set.of("forbidden1", "forbidden2"));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("forbidden1")));
	}
	
	@Test
	void encodeKeyNotInConstraintViolation() {
		Codec<String> codec = Codecs.STRING.notIn(Set.of("forbidden1", "forbidden2"));
		
		assertThrows(EncoderException.class, () -> codec.encodeKey("forbidden1"));
	}
	
	@Test
	void decodeKeyNotInConstraintViolation() {
		Codec<String> codec = Codecs.STRING.notIn(Set.of("forbidden1", "forbidden2"));
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("forbidden1"));
	}
	
	@Test
	void encodeMinLengthConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codecs.STRING.length(builder -> builder.minLength(3));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), "hello"));
	}
	
	@Test
	void decodeMinLengthConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codecs.STRING.length(builder -> builder.minLength(3));
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("hello")));
	}
	
	@Test
	void encodeKeyMinLengthConstraintSuccess() {
		Codec<String> codec = Codecs.STRING.length(builder -> builder.minLength(3));
		
		assertDoesNotThrow(() -> codec.encodeKey("hello"));
	}
	
	@Test
	void decodeKeyMinLengthConstraintSuccess() {
		Codec<String> codec = Codecs.STRING.length(builder -> builder.minLength(3));
		
		assertDoesNotThrow(() -> codec.decodeKey("hello"));
	}
	
	@Test
	void encodeMinLengthConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codecs.STRING.length(builder -> builder.minLength(5));
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), "ab"));
	}
	
	@Test
	void decodeMinLengthConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codecs.STRING.length(builder -> builder.minLength(5));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("ab")));
	}
	
	@Test
	void encodeKeyMinLengthConstraintViolation() {
		Codec<String> codec = Codecs.STRING.length(builder -> builder.minLength(5));
		
		assertThrows(EncoderException.class, () -> codec.encodeKey("ab"));
	}
	
	@Test
	void decodeKeyMinLengthConstraintViolation() {
		Codec<String> codec = Codecs.STRING.length(builder -> builder.minLength(5));
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("ab"));
	}
	
	@Test
	void encodeMaxLengthConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codecs.STRING.length(builder -> builder.maxLength(10));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), "hello"));
	}
	
	@Test
	void decodeMaxLengthConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codecs.STRING.length(builder -> builder.maxLength(10));
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("hello")));
	}
	
	@Test
	void encodeKeyMaxLengthConstraintSuccess() {
		Codec<String> codec = Codecs.STRING.length(builder -> builder.maxLength(10));
		
		assertDoesNotThrow(() -> codec.encodeKey("hello"));
	}
	
	@Test
	void decodeKeyMaxLengthConstraintSuccess() {
		Codec<String> codec = Codecs.STRING.length(builder -> builder.maxLength(10));
		
		assertDoesNotThrow(() -> codec.decodeKey("hello"));
	}
	
	@Test
	void encodeMaxLengthConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codecs.STRING.length(builder -> builder.maxLength(3));
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), "hello"));
	}
	
	@Test
	void decodeMaxLengthConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codecs.STRING.length(builder -> builder.maxLength(3));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("hello")));
	}
	
	@Test
	void encodeKeyMaxLengthConstraintViolation() {
		Codec<String> codec = Codecs.STRING.length(builder -> builder.maxLength(3));
		
		assertThrows(EncoderException.class, () -> codec.encodeKey("hello"));
	}
	
	@Test
	void decodeKeyMaxLengthConstraintViolation() {
		Codec<String> codec = Codecs.STRING.length(builder -> builder.maxLength(3));
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("hello"));
	}
	
	@Test
	void encodeExactLengthConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codecs.STRING.length(builder -> builder.exactLength(5));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), "hello"));
	}
	
	@Test
	void decodeExactLengthConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codecs.STRING.length(builder -> builder.exactLength(5));
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("hello")));
	}
	
	@Test
	void encodeKeyExactLengthConstraintSuccess() {
		Codec<String> codec = Codecs.STRING.length(builder -> builder.exactLength(5));
		
		assertDoesNotThrow(() -> codec.encodeKey("hello"));
	}
	
	@Test
	void decodeKeyExactLengthConstraintSuccess() {
		Codec<String> codec = Codecs.STRING.length(builder -> builder.exactLength(5));
		
		assertDoesNotThrow(() -> codec.decodeKey("hello"));
	}
	
	@Test
	void encodeExactLengthConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codecs.STRING.length(builder -> builder.exactLength(5));
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), "hi"));
	}
	
	@Test
	void decodeExactLengthConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codecs.STRING.length(builder -> builder.exactLength(5));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("hi")));
	}
	
	@Test
	void encodeKeyExactLengthConstraintViolation() {
		Codec<String> codec = Codecs.STRING.length(builder -> builder.exactLength(5));
		
		assertThrows(EncoderException.class, () -> codec.encodeKey("hi"));
	}
	
	@Test
	void decodeKeyExactLengthConstraintViolation() {
		Codec<String> codec = Codecs.STRING.length(builder -> builder.exactLength(5));
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("hi"));
	}
	
	@Test
	void encodeStartsWithConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codecs.STRING.startsWith("pre_");
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), "pre_value"));
	}
	
	@Test
	void decodeStartsWithConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codecs.STRING.startsWith("pre_");
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("pre_value")));
	}
	
	@Test
	void encodeKeyStartsWithConstraintSuccess() {
		Codec<String> codec = Codecs.STRING.startsWith("pre_");
		
		assertDoesNotThrow(() -> codec.encodeKey("pre_value"));
	}
	
	@Test
	void decodeKeyStartsWithConstraintSuccess() {
		Codec<String> codec = Codecs.STRING.startsWith("pre_");
		
		assertDoesNotThrow(() -> codec.decodeKey("pre_value"));
	}
	
	@Test
	void encodeStartsWithConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codecs.STRING.startsWith("pre_");
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), "value"));
	}
	
	@Test
	void decodeStartsWithConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codecs.STRING.startsWith("pre_");
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("value")));
	}
	
	@Test
	void encodeKeyStartsWithConstraintViolation() {
		Codec<String> codec = Codecs.STRING.startsWith("pre_");
		
		assertThrows(EncoderException.class, () -> codec.encodeKey("value"));
	}
	
	@Test
	void decodeKeyStartsWithConstraintViolation() {
		Codec<String> codec = Codecs.STRING.startsWith("pre_");
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("value"));
	}
	
	@Test
	void encodeNotStartsWithConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codecs.STRING.notStartsWith("bad_");
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), "good_value"));
	}
	
	@Test
	void decodeNotStartsWithConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codecs.STRING.notStartsWith("bad_");
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("good_value")));
	}
	
	@Test
	void encodeKeyNotStartsWithConstraintSuccess() {
		Codec<String> codec = Codecs.STRING.notStartsWith("bad_");
		
		assertDoesNotThrow(() -> codec.encodeKey("good_value"));
	}
	
	@Test
	void decodeKeyNotStartsWithConstraintSuccess() {
		Codec<String> codec = Codecs.STRING.notStartsWith("bad_");
		
		assertDoesNotThrow(() -> codec.decodeKey("good_value"));
	}
	
	@Test
	void encodeNotStartsWithConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codecs.STRING.notStartsWith("bad_");
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), "bad_value"));
	}
	
	@Test
	void decodeNotStartsWithConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codecs.STRING.notStartsWith("bad_");
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("bad_value")));
	}
	
	@Test
	void encodeKeyNotStartsWithConstraintViolation() {
		Codec<String> codec = Codecs.STRING.notStartsWith("bad_");
		
		assertThrows(EncoderException.class, () -> codec.encodeKey("bad_value"));
	}
	
	@Test
	void decodeKeyNotStartsWithConstraintViolation() {
		Codec<String> codec = Codecs.STRING.notStartsWith("bad_");
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("bad_value"));
	}
	
	@Test
	void encodeEndsWithConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codecs.STRING.endsWith(".txt");
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), "file.txt"));
	}
	
	@Test
	void decodeEndsWithConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codecs.STRING.endsWith(".txt");
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("file.txt")));
	}
	
	@Test
	void encodeKeyEndsWithConstraintSuccess() {
		Codec<String> codec = Codecs.STRING.endsWith(".txt");
		
		assertDoesNotThrow(() -> codec.encodeKey("file.txt"));
	}
	
	@Test
	void decodeKeyEndsWithConstraintSuccess() {
		Codec<String> codec = Codecs.STRING.endsWith(".txt");
		
		assertDoesNotThrow(() -> codec.decodeKey("file.txt"));
	}
	
	@Test
	void encodeEndsWithConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codecs.STRING.endsWith(".txt");
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), "file.pdf"));
	}
	
	@Test
	void decodeEndsWithConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codecs.STRING.endsWith(".txt");
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("file.pdf")));
	}
	
	@Test
	void encodeKeyEndsWithConstraintViolation() {
		Codec<String> codec = Codecs.STRING.endsWith(".txt");
		
		assertThrows(EncoderException.class, () -> codec.encodeKey("file.pdf"));
	}
	
	@Test
	void decodeKeyEndsWithConstraintViolation() {
		Codec<String> codec = Codecs.STRING.endsWith(".txt");
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("file.pdf"));
	}
	
	@Test
	void encodeNotEndsWithConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codecs.STRING.notEndsWith(".tmp");
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), "file.txt"));
	}
	
	@Test
	void decodeNotEndsWithConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codecs.STRING.notEndsWith(".tmp");
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("file.txt")));
	}
	
	@Test
	void encodeKeyNotEndsWithConstraintSuccess() {
		Codec<String> codec = Codecs.STRING.notEndsWith(".tmp");
		
		assertDoesNotThrow(() -> codec.encodeKey("file.txt"));
	}
	
	@Test
	void decodeKeyNotEndsWithConstraintSuccess() {
		Codec<String> codec = Codecs.STRING.notEndsWith(".tmp");
		
		assertDoesNotThrow(() -> codec.decodeKey("file.txt"));
	}
	
	@Test
	void encodeNotEndsWithConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codecs.STRING.notEndsWith(".tmp");
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), "file.tmp"));
	}
	
	@Test
	void decodeNotEndsWithConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codecs.STRING.notEndsWith(".tmp");
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("file.tmp")));
	}
	
	@Test
	void encodeKeyNotEndsWithConstraintViolation() {
		Codec<String> codec = Codecs.STRING.notEndsWith(".tmp");
		
		assertThrows(EncoderException.class, () -> codec.encodeKey("file.tmp"));
	}
	
	@Test
	void decodeKeyNotEndsWithConstraintViolation() {
		Codec<String> codec = Codecs.STRING.notEndsWith(".tmp");
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("file.tmp"));
	}
	
	@Test
	void encodeContainsConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codecs.STRING.contains("test");
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), "this_test_works"));
	}
	
	@Test
	void decodeContainsConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codecs.STRING.contains("test");
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("this_test_works")));
	}
	
	@Test
	void encodeKeyContainsConstraintSuccess() {
		Codec<String> codec = Codecs.STRING.contains("test");
		
		assertDoesNotThrow(() -> codec.encodeKey("this_test_works"));
	}
	
	@Test
	void decodeKeyContainsConstraintSuccess() {
		Codec<String> codec = Codecs.STRING.contains("test");
		
		assertDoesNotThrow(() -> codec.decodeKey("this_test_works"));
	}
	
	@Test
	void encodeContainsConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codecs.STRING.contains("test");
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), "no_match_here"));
	}
	
	@Test
	void decodeContainsConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codecs.STRING.contains("test");
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("no_match_here")));
	}
	
	@Test
	void encodeKeyContainsConstraintViolation() {
		Codec<String> codec = Codecs.STRING.contains("test");
		
		assertThrows(EncoderException.class, () -> codec.encodeKey("no_match_here"));
	}
	
	@Test
	void decodeKeyContainsConstraintViolation() {
		Codec<String> codec = Codecs.STRING.contains("test");
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("no_match_here"));
	}
	
	@Test
	void encodeNotContainsConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codecs.STRING.notContains("forbidden");
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), "allowed_text"));
	}
	
	@Test
	void decodeNotContainsConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codecs.STRING.notContains("forbidden");
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("allowed_text")));
	}
	
	@Test
	void encodeKeyNotContainsConstraintSuccess() {
		Codec<String> codec = Codecs.STRING.notContains("forbidden");
		
		assertDoesNotThrow(() -> codec.encodeKey("allowed_text"));
	}
	
	@Test
	void decodeKeyNotContainsConstraintSuccess() {
		Codec<String> codec = Codecs.STRING.notContains("forbidden");
		
		assertDoesNotThrow(() -> codec.decodeKey("allowed_text"));
	}
	
	@Test
	void encodeNotContainsConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codecs.STRING.notContains("forbidden");
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), "this_is_forbidden_text"));
	}
	
	@Test
	void decodeNotContainsConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codecs.STRING.notContains("forbidden");
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("this_is_forbidden_text")));
	}
	
	@Test
	void encodeKeyNotContainsConstraintViolation() {
		Codec<String> codec = Codecs.STRING.notContains("forbidden");
		
		assertThrows(EncoderException.class, () -> codec.encodeKey("this_is_forbidden_text"));
	}
	
	@Test
	void decodeKeyNotContainsConstraintViolation() {
		Codec<String> codec = Codecs.STRING.notContains("forbidden");
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("this_is_forbidden_text"));
	}
	
	@Test
	void encodeMatchesConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codecs.STRING.matches("^[a-z]+$");
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), "hello"));
	}
	
	@Test
	void decodeMatchesConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codecs.STRING.matches("^[a-z]+$");
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("hello")));
	}
	
	@Test
	void encodeKeyMatchesConstraintSuccess() {
		Codec<String> codec = Codecs.STRING.matches("^[a-z]+$");
		
		assertDoesNotThrow(() -> codec.encodeKey("hello"));
	}
	
	@Test
	void decodeKeyMatchesConstraintSuccess() {
		Codec<String> codec = Codecs.STRING.matches("^[a-z]+$");
		
		assertDoesNotThrow(() -> codec.decodeKey("hello"));
	}
	
	@Test
	void encodeMatchesConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codecs.STRING.matches("^[a-z]+$");
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), "Hello123"));
	}
	
	@Test
	void decodeMatchesConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codecs.STRING.matches("^[a-z]+$");
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("Hello123")));
	}
	
	@Test
	void encodeKeyMatchesConstraintViolation() {
		Codec<String> codec = Codecs.STRING.matches("^[a-z]+$");
		
		assertThrows(EncoderException.class, () -> codec.encodeKey("Hello123"));
	}
	
	@Test
	void decodeKeyMatchesConstraintViolation() {
		Codec<String> codec = Codecs.STRING.matches("^[a-z]+$");
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("Hello123"));
	}
	
	@Test
	void encodeNotMatchesConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codecs.STRING.notMatches("^[0-9]+$");
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), "hello"));
	}
	
	@Test
	void decodeNotMatchesConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codecs.STRING.notMatches("^[0-9]+$");
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("hello")));
	}
	
	@Test
	void encodeKeyNotMatchesConstraintSuccess() {
		Codec<String> codec = Codecs.STRING.notMatches("^[0-9]+$");
		
		assertDoesNotThrow(() -> codec.encodeKey("hello"));
	}
	
	@Test
	void decodeKeyNotMatchesConstraintSuccess() {
		Codec<String> codec = Codecs.STRING.notMatches("^[0-9]+$");
		
		assertDoesNotThrow(() -> codec.decodeKey("hello"));
	}
	
	@Test
	void encodeNotMatchesConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codecs.STRING.notMatches("^[0-9]+$");
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), "12345"));
	}
	
	@Test
	void decodeNotMatchesConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codecs.STRING.notMatches("^[0-9]+$");
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("12345")));
	}
	
	@Test
	void encodeKeyNotMatchesConstraintViolation() {
		Codec<String> codec = Codecs.STRING.notMatches("^[0-9]+$");
		
		assertThrows(EncoderException.class, () -> codec.encodeKey("12345"));
	}
	
	@Test
	void decodeKeyNotMatchesConstraintViolation() {
		Codec<String> codec = Codecs.STRING.notMatches("^[0-9]+$");
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("12345"));
	}
	
	@Test
	void encodeTrimmedConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codecs.STRING.trimmed();
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), "hello"));
	}
	
	@Test
	void decodeTrimmedConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codecs.STRING.trimmed();
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("hello")));
	}
	
	@Test
	void encodeKeyTrimmedConstraintSuccess() {
		Codec<String> codec = Codecs.STRING.trimmed();
		
		assertDoesNotThrow(() -> codec.encodeKey("hello"));
	}
	
	@Test
	void decodeKeyTrimmedConstraintSuccess() {
		Codec<String> codec = Codecs.STRING.trimmed();
		
		assertDoesNotThrow(() -> codec.decodeKey("hello"));
	}
	
	@Test
	void encodeTrimmedConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codecs.STRING.trimmed();
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), "  hello  "));
	}
	
	@Test
	void decodeTrimmedConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codecs.STRING.trimmed();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("  hello  ")));
	}
	
	@Test
	void encodeKeyTrimmedConstraintViolation() {
		Codec<String> codec = Codecs.STRING.trimmed();
		
		assertThrows(EncoderException.class, () -> codec.encodeKey("  hello  "));
	}
	
	@Test
	void decodeKeyTrimmedConstraintViolation() {
		Codec<String> codec = Codecs.STRING.trimmed();
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("  hello  "));
	}
	
	@Test
	void encodeNotBlankConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codecs.STRING.notBlank();
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), "hello"));
	}
	
	@Test
	void decodeNotBlankConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codecs.STRING.notBlank();
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("hello")));
	}
	
	@Test
	void encodeKeyNotBlankConstraintSuccess() {
		Codec<String> codec = Codecs.STRING.notBlank();
		
		assertDoesNotThrow(() -> codec.encodeKey("hello"));
	}
	
	@Test
	void decodeKeyNotBlankConstraintSuccess() {
		Codec<String> codec = Codecs.STRING.notBlank();
		
		assertDoesNotThrow(() -> codec.decodeKey("hello"));
	}
	
	@Test
	void encodeNotBlankConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codecs.STRING.notBlank();
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), "   "));
	}
	
	@Test
	void decodeNotBlankConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codecs.STRING.notBlank();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("   ")));
	}
	
	@Test
	void encodeKeyNotBlankConstraintViolation() {
		Codec<String> codec = Codecs.STRING.notBlank();
		
		assertThrows(EncoderException.class, () -> codec.encodeKey("   "));
	}
	
	@Test
	void decodeKeyNotBlankConstraintViolation() {
		Codec<String> codec = Codecs.STRING.notBlank();
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("   "));
	}
	
	@Test
	void encodeUpperCaseConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codecs.STRING.upperCase();
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), "HELLO"));
	}
	
	@Test
	void decodeUpperCaseConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codecs.STRING.upperCase();
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("HELLO")));
	}
	
	@Test
	void encodeKeyUpperCaseConstraintSuccess() {
		Codec<String> codec = Codecs.STRING.upperCase();
		
		assertDoesNotThrow(() -> codec.encodeKey("HELLO"));
	}
	
	@Test
	void decodeKeyUpperCaseConstraintSuccess() {
		Codec<String> codec = Codecs.STRING.upperCase();
		
		assertDoesNotThrow(() -> codec.decodeKey("HELLO"));
	}
	
	@Test
	void encodeUpperCaseConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codecs.STRING.upperCase();
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), "Hello"));
	}
	
	@Test
	void decodeUpperCaseConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codecs.STRING.upperCase();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("Hello")));
	}
	
	@Test
	void encodeKeyUpperCaseConstraintViolation() {
		Codec<String> codec = Codecs.STRING.upperCase();
		
		assertThrows(EncoderException.class, () -> codec.encodeKey("Hello"));
	}
	
	@Test
	void decodeKeyUpperCaseConstraintViolation() {
		Codec<String> codec = Codecs.STRING.upperCase();
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("Hello"));
	}
	
	@Test
	void encodeLowerCaseConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codecs.STRING.lowerCase();
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), "hello"));
	}
	
	@Test
	void decodeLowerCaseConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codecs.STRING.lowerCase();
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("hello")));
	}
	
	@Test
	void encodeKeyLowerCaseConstraintSuccess() {
		Codec<String> codec = Codecs.STRING.lowerCase();
		
		assertDoesNotThrow(() -> codec.encodeKey("hello"));
	}
	
	@Test
	void decodeKeyLowerCaseConstraintSuccess() {
		Codec<String> codec = Codecs.STRING.lowerCase();
		
		assertDoesNotThrow(() -> codec.decodeKey("hello"));
	}
	
	@Test
	void encodeLowerCaseConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codecs.STRING.lowerCase();
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), "Hello"));
	}
	
	@Test
	void decodeLowerCaseConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codecs.STRING.lowerCase();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("Hello")));
	}
	
	@Test
	void encodeKeyLowerCaseConstraintViolation() {
		Codec<String> codec = Codecs.STRING.lowerCase();
		
		assertThrows(EncoderException.class, () -> codec.encodeKey("Hello"));
	}
	
	@Test
	void decodeKeyLowerCaseConstraintViolation() {
		Codec<String> codec = Codecs.STRING.lowerCase();
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("Hello"));
	}
	
	@Test
	void encodeNumericConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codecs.STRING.numeric();
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), "12345"));
	}
	
	@Test
	void decodeNumericConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codecs.STRING.numeric();
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("12345")));
	}
	
	@Test
	void encodeKeyNumericConstraintSuccess() {
		Codec<String> codec = Codecs.STRING.numeric();
		
		assertDoesNotThrow(() -> codec.encodeKey("12345"));
	}
	
	@Test
	void decodeKeyNumericConstraintSuccess() {
		Codec<String> codec = Codecs.STRING.numeric();
		
		assertDoesNotThrow(() -> codec.decodeKey("12345"));
	}
	
	@Test
	void encodeNumericConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codecs.STRING.numeric();
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), "hello"));
	}
	
	@Test
	void decodeNumericConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codecs.STRING.numeric();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("hello")));
	}
	
	@Test
	void encodeKeyNumericConstraintViolation() {
		Codec<String> codec = Codecs.STRING.numeric();
		
		assertThrows(EncoderException.class, () -> codec.encodeKey("hello"));
	}
	
	@Test
	void decodeKeyNumericConstraintViolation() {
		Codec<String> codec = Codecs.STRING.numeric();
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("hello"));
	}
	
	@Test
	void encodeAlphabeticConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codecs.STRING.alphabetic();
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), "hello"));
	}
	
	@Test
	void decodeAlphabeticConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codecs.STRING.alphabetic();
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("hello")));
	}
	
	@Test
	void encodeKeyAlphabeticConstraintSuccess() {
		Codec<String> codec = Codecs.STRING.alphabetic();
		
		assertDoesNotThrow(() -> codec.encodeKey("hello"));
	}
	
	@Test
	void decodeKeyAlphabeticConstraintSuccess() {
		Codec<String> codec = Codecs.STRING.alphabetic();
		
		assertDoesNotThrow(() -> codec.decodeKey("hello"));
	}
	
	@Test
	void encodeAlphabeticConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codecs.STRING.alphabetic();
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), "hello123"));
	}
	
	@Test
	void decodeAlphabeticConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codecs.STRING.alphabetic();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("hello123")));
	}
	
	@Test
	void encodeKeyAlphabeticConstraintViolation() {
		Codec<String> codec = Codecs.STRING.alphabetic();
		
		assertThrows(EncoderException.class, () -> codec.encodeKey("hello123"));
	}
	
	@Test
	void decodeKeyAlphabeticConstraintViolation() {
		Codec<String> codec = Codecs.STRING.alphabetic();
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("hello123"));
	}
	
	@Test
	void encodeAlphanumericConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codecs.STRING.alphanumeric();
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), "hello123"));
	}
	
	@Test
	void decodeAlphanumericConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codecs.STRING.alphanumeric();
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("hello123")));
	}
	
	@Test
	void encodeKeyAlphanumericConstraintSuccess() {
		Codec<String> codec = Codecs.STRING.alphanumeric();
		
		assertDoesNotThrow(() -> codec.encodeKey("hello123"));
	}
	
	@Test
	void decodeKeyAlphanumericConstraintSuccess() {
		Codec<String> codec = Codecs.STRING.alphanumeric();
		
		assertDoesNotThrow(() -> codec.decodeKey("hello123"));
	}
	
	@Test
	void encodeAlphanumericConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codecs.STRING.alphanumeric();
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), "hello@123"));
	}
	
	@Test
	void decodeAlphanumericConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codecs.STRING.alphanumeric();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("hello@123")));
	}
	
	@Test
	void encodeKeyAlphanumericConstraintViolation() {
		Codec<String> codec = Codecs.STRING.alphanumeric();
		
		assertThrows(EncoderException.class, () -> codec.encodeKey("hello@123"));
	}
	
	@Test
	void decodeKeyAlphanumericConstraintViolation() {
		Codec<String> codec = Codecs.STRING.alphanumeric();
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("hello@123"));
	}
	
	@Test
	void encodeAsciiConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codecs.STRING.ascii();
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), "Hello World!"));
	}
	
	@Test
	void decodeAsciiConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codecs.STRING.ascii();
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("Hello World!")));
	}
	
	@Test
	void encodeKeyAsciiConstraintSuccess() {
		Codec<String> codec = Codecs.STRING.ascii();
		
		assertDoesNotThrow(() -> codec.encodeKey("Hello"));
	}
	
	@Test
	void decodeKeyAsciiConstraintSuccess() {
		Codec<String> codec = Codecs.STRING.ascii();
		
		assertDoesNotThrow(() -> codec.decodeKey("Hello"));
	}
	
	@Test
	void encodeAsciiConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codecs.STRING.ascii();
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), "Hello \u4E16\u754C"));
	}
	
	@Test
	void decodeAsciiConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codecs.STRING.ascii();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("Hello \u4E16\u754C")));
	}
	
	@Test
	void encodeKeyAsciiConstraintViolation() {
		Codec<String> codec = Codecs.STRING.ascii();
		
		assertThrows(EncoderException.class, () -> codec.encodeKey("Hello\u00E9"));
	}
	
	@Test
	void decodeKeyAsciiConstraintViolation() {
		Codec<String> codec = Codecs.STRING.ascii();
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("Hello\u00E9"));
	}
	
	@Test
	void encodeEqualToIgnoreCaseConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codecs.STRING.equalToIgnoreCase("hello");
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), "HELLO"));
	}
	
	@Test
	void decodeEqualToIgnoreCaseConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codecs.STRING.equalToIgnoreCase("hello");
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("HeLLo")));
	}
	
	@Test
	void encodeKeyEqualToIgnoreCaseConstraintSuccess() {
		Codec<String> codec = Codecs.STRING.equalToIgnoreCase("hello");
		
		assertDoesNotThrow(() -> codec.encodeKey("Hello"));
	}
	
	@Test
	void decodeKeyEqualToIgnoreCaseConstraintSuccess() {
		Codec<String> codec = Codecs.STRING.equalToIgnoreCase("hello");
		
		assertDoesNotThrow(() -> codec.decodeKey("hElLo"));
	}
	
	@Test
	void encodeEqualToIgnoreCaseConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codecs.STRING.equalToIgnoreCase("hello");
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), "world"));
	}
	
	@Test
	void decodeEqualToIgnoreCaseConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codecs.STRING.equalToIgnoreCase("hello");
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("world")));
	}
	
	@Test
	void encodeKeyEqualToIgnoreCaseConstraintViolation() {
		Codec<String> codec = Codecs.STRING.equalToIgnoreCase("hello");
		
		assertThrows(EncoderException.class, () -> codec.encodeKey("world"));
	}
	
	@Test
	void decodeKeyEqualToIgnoreCaseConstraintViolation() {
		Codec<String> codec = Codecs.STRING.equalToIgnoreCase("hello");
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("world"));
	}
	
	@Test
	void encodeStartsWithAnyConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codecs.STRING.startsWithAny(List.of("http://", "https://", "ftp://"));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), "https://example.com"));
	}
	
	@Test
	void decodeStartsWithAnyConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codecs.STRING.startsWithAny(List.of("http://", "https://", "ftp://"));
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("http://example.com")));
	}
	
	@Test
	void encodeKeyStartsWithAnyConstraintSuccess() {
		Codec<String> codec = Codecs.STRING.startsWithAny(List.of("http://", "https://", "ftp://"));
		
		assertDoesNotThrow(() -> codec.encodeKey("ftp://server.com"));
	}
	
	@Test
	void decodeKeyStartsWithAnyConstraintSuccess() {
		Codec<String> codec = Codecs.STRING.startsWithAny(List.of("http://", "https://", "ftp://"));
		
		assertDoesNotThrow(() -> codec.decodeKey("https://secure.com"));
	}
	
	@Test
	void encodeStartsWithAnyConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codecs.STRING.startsWithAny(List.of("http://", "https://", "ftp://"));
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), "ssh://example.com"));
	}
	
	@Test
	void decodeStartsWithAnyConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codecs.STRING.startsWithAny(List.of("http://", "https://", "ftp://"));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("file://local")));
	}
	
	@Test
	void encodeKeyStartsWithAnyConstraintViolation() {
		Codec<String> codec = Codecs.STRING.startsWithAny(List.of("http://", "https://", "ftp://"));
		
		assertThrows(EncoderException.class, () -> codec.encodeKey("mailto:test@test.com"));
	}
	
	@Test
	void decodeKeyStartsWithAnyConstraintViolation() {
		Codec<String> codec = Codecs.STRING.startsWithAny(List.of("http://", "https://", "ftp://"));
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("ws://websocket.com"));
	}
	
	@Test
	void encodeEndsWithAnyConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codecs.STRING.endsWithAny(List.of(".txt", ".pdf", ".doc"));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), "document.pdf"));
	}
	
	@Test
	void decodeEndsWithAnyConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codecs.STRING.endsWithAny(List.of(".txt", ".pdf", ".doc"));
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("file.txt")));
	}
	
	@Test
	void encodeKeyEndsWithAnyConstraintSuccess() {
		Codec<String> codec = Codecs.STRING.endsWithAny(List.of(".txt", ".pdf", ".doc"));
		
		assertDoesNotThrow(() -> codec.encodeKey("report.doc"));
	}
	
	@Test
	void decodeKeyEndsWithAnyConstraintSuccess() {
		Codec<String> codec = Codecs.STRING.endsWithAny(List.of(".txt", ".pdf", ".doc"));
		
		assertDoesNotThrow(() -> codec.decodeKey("readme.txt"));
	}
	
	@Test
	void encodeEndsWithAnyConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codecs.STRING.endsWithAny(List.of(".txt", ".pdf", ".doc"));
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), "image.png"));
	}
	
	@Test
	void decodeEndsWithAnyConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codecs.STRING.endsWithAny(List.of(".txt", ".pdf", ".doc"));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("image.jpg")));
	}
	
	@Test
	void encodeKeyEndsWithAnyConstraintViolation() {
		Codec<String> codec = Codecs.STRING.endsWithAny(List.of(".txt", ".pdf", ".doc"));
		
		assertThrows(EncoderException.class, () -> codec.encodeKey("video.mp4"));
	}
	
	@Test
	void decodeKeyEndsWithAnyConstraintViolation() {
		Codec<String> codec = Codecs.STRING.endsWithAny(List.of(".txt", ".pdf", ".doc"));
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("archive.zip"));
	}
	
	@Test
	void encodeContainsAllConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codecs.STRING.containsAll(List.of("foo", "bar"));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), "foo_bar_baz"));
	}
	
	@Test
	void decodeContainsAllConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codecs.STRING.containsAll(List.of("foo", "bar"));
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("bar_test_foo")));
	}
	
	@Test
	void encodeKeyContainsAllConstraintSuccess() {
		Codec<String> codec = Codecs.STRING.containsAll(List.of("foo", "bar"));
		
		assertDoesNotThrow(() -> codec.encodeKey("foobar"));
	}
	
	@Test
	void decodeKeyContainsAllConstraintSuccess() {
		Codec<String> codec = Codecs.STRING.containsAll(List.of("foo", "bar"));
		
		assertDoesNotThrow(() -> codec.decodeKey("barfoo"));
	}
	
	@Test
	void encodeContainsAllConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codecs.STRING.containsAll(List.of("foo", "bar"));
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), "foo_only"));
	}
	
	@Test
	void decodeContainsAllConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codecs.STRING.containsAll(List.of("foo", "bar"));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("bar_only")));
	}
	
	@Test
	void encodeKeyContainsAllConstraintViolation() {
		Codec<String> codec = Codecs.STRING.containsAll(List.of("foo", "bar"));
		
		assertThrows(EncoderException.class, () -> codec.encodeKey("neither"));
	}
	
	@Test
	void decodeKeyContainsAllConstraintViolation() {
		Codec<String> codec = Codecs.STRING.containsAll(List.of("foo", "bar"));
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("something_else"));
	}
}
