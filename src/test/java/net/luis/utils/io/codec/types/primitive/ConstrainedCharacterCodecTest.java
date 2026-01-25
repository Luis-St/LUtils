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
import net.luis.utils.io.codec.constraint.config.CharacterConstraintConfig;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.JsonElement;
import net.luis.utils.io.data.json.JsonPrimitive;
import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link CharacterCodec} with constraints.<br>
 *
 * @author Luis-St
 */
class ConstrainedCharacterCodecTest {
	
	@Test
	void encodeStartWithValidConstrainedValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = new CharacterCodec().apply(CharacterConstraintConfig::withLetter);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 'a');
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("a"), result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithValidConstrainedValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = new CharacterCodec().apply(CharacterConstraintConfig::withLetter);
		
		Result<Character> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("a"));
		assertTrue(result.isSuccess());
		assertEquals('a', result.resultOrThrow());
	}
	
	@Test
	void encodeKeyWithValidConstrainedValue() {
		Codec<Character> codec = new CharacterCodec().apply(CharacterConstraintConfig::withLetter);
		
		Result<String> result = codec.encodeKey('a');
		assertTrue(result.isSuccess());
		assertEquals("a", result.resultOrThrow());
	}
	
	@Test
	void decodeKeyWithValidConstrainedValue() {
		Codec<Character> codec = new CharacterCodec().apply(CharacterConstraintConfig::withLetter);
		
		Result<Character> result = codec.decodeKey("a");
		assertTrue(result.isSuccess());
		assertEquals('a', result.resultOrThrow());
	}
	
	@Test
	void encodeStartEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = new CharacterCodec().apply(config -> config.withEqualTo('x'));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 'x');
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = new CharacterCodec().apply(config -> config.withEqualTo('x'));
		
		Result<Character> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("x"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyEqualToConstraintSuccess() {
		Codec<Character> codec = new CharacterCodec().apply(config -> config.withEqualTo('x'));
		
		Result<String> result = codec.encodeKey('x');
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyEqualToConstraintSuccess() {
		Codec<Character> codec = new CharacterCodec().apply(config -> config.withEqualTo('x'));
		
		Result<Character> result = codec.decodeKey("x");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = new CharacterCodec().apply(config -> config.withEqualTo('x'));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 'y');
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = new CharacterCodec().apply(config -> config.withEqualTo('x'));
		
		Result<Character> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("y"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyEqualToConstraintViolation() {
		Codec<Character> codec = new CharacterCodec().apply(config -> config.withEqualTo('x'));
		
		Result<String> result = codec.encodeKey('y');
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyEqualToConstraintViolation() {
		Codec<Character> codec = new CharacterCodec().apply(config -> config.withEqualTo('x'));
		
		Result<Character> result = codec.decodeKey("y");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNotEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = new CharacterCodec().apply(config -> config.withNotEqualTo('x'));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 'a');
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartNotEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = new CharacterCodec().apply(config -> config.withNotEqualTo('x'));
		
		Result<Character> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("a"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyNotEqualToConstraintSuccess() {
		Codec<Character> codec = new CharacterCodec().apply(config -> config.withNotEqualTo('x'));
		
		Result<String> result = codec.encodeKey('a');
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyNotEqualToConstraintSuccess() {
		Codec<Character> codec = new CharacterCodec().apply(config -> config.withNotEqualTo('x'));
		
		Result<Character> result = codec.decodeKey("a");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = new CharacterCodec().apply(config -> config.withNotEqualTo('x'));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 'x');
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = new CharacterCodec().apply(config -> config.withNotEqualTo('x'));
		
		Result<Character> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("x"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyNotEqualToConstraintViolation() {
		Codec<Character> codec = new CharacterCodec().apply(config -> config.withNotEqualTo('x'));
		
		Result<String> result = codec.encodeKey('x');
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyNotEqualToConstraintViolation() {
		Codec<Character> codec = new CharacterCodec().apply(config -> config.withNotEqualTo('x'));
		
		Result<Character> result = codec.decodeKey("x");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = new CharacterCodec().apply(config -> config.withIn(Set.of('a', 'b', 'c')));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 'b');
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = new CharacterCodec().apply(config -> config.withIn(Set.of('a', 'b', 'c')));
		
		Result<Character> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("b"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyInConstraintSuccess() {
		Codec<Character> codec = new CharacterCodec().apply(config -> config.withIn(Set.of('a', 'b', 'c')));
		
		Result<String> result = codec.encodeKey('b');
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyInConstraintSuccess() {
		Codec<Character> codec = new CharacterCodec().apply(config -> config.withIn(Set.of('a', 'b', 'c')));
		
		Result<Character> result = codec.decodeKey("b");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = new CharacterCodec().apply(config -> config.withIn(Set.of('a', 'b', 'c')));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 'z');
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = new CharacterCodec().apply(config -> config.withIn(Set.of('a', 'b', 'c')));
		
		Result<Character> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("z"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyInConstraintViolation() {
		Codec<Character> codec = new CharacterCodec().apply(config -> config.withIn(Set.of('a', 'b', 'c')));
		
		Result<String> result = codec.encodeKey('z');
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyInConstraintViolation() {
		Codec<Character> codec = new CharacterCodec().apply(config -> config.withIn(Set.of('a', 'b', 'c')));
		
		Result<Character> result = codec.decodeKey("z");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNotInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = new CharacterCodec().apply(config -> config.withNotIn(Set.of('x', 'y', 'z')));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 'a');
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartNotInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = new CharacterCodec().apply(config -> config.withNotIn(Set.of('x', 'y', 'z')));
		
		Result<Character> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("a"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyNotInConstraintSuccess() {
		Codec<Character> codec = new CharacterCodec().apply(config -> config.withNotIn(Set.of('x', 'y', 'z')));
		
		Result<String> result = codec.encodeKey('a');
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyNotInConstraintSuccess() {
		Codec<Character> codec = new CharacterCodec().apply(config -> config.withNotIn(Set.of('x', 'y', 'z')));
		
		Result<Character> result = codec.decodeKey("a");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = new CharacterCodec().apply(config -> config.withNotIn(Set.of('x', 'y', 'z')));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 'x');
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = new CharacterCodec().apply(config -> config.withNotIn(Set.of('x', 'y', 'z')));
		
		Result<Character> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("x"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyNotInConstraintViolation() {
		Codec<Character> codec = new CharacterCodec().apply(config -> config.withNotIn(Set.of('x', 'y', 'z')));
		
		Result<String> result = codec.encodeKey('x');
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyNotInConstraintViolation() {
		Codec<Character> codec = new CharacterCodec().apply(config -> config.withNotIn(Set.of('x', 'y', 'z')));
		
		Result<Character> result = codec.decodeKey("x");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartLetterConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = new CharacterCodec().apply(CharacterConstraintConfig::withLetter);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 'a');
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartLetterConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = new CharacterCodec().apply(CharacterConstraintConfig::withLetter);
		
		Result<Character> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("Z"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyLetterConstraintSuccess() {
		Codec<Character> codec = new CharacterCodec().apply(CharacterConstraintConfig::withLetter);
		
		Result<String> result = codec.encodeKey('b');
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyLetterConstraintSuccess() {
		Codec<Character> codec = new CharacterCodec().apply(CharacterConstraintConfig::withLetter);
		
		Result<Character> result = codec.decodeKey("c");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartLetterConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = new CharacterCodec().apply(CharacterConstraintConfig::withLetter);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), '5');
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartLetterConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = new CharacterCodec().apply(CharacterConstraintConfig::withLetter);
		
		Result<Character> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("9"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyLetterConstraintViolation() {
		Codec<Character> codec = new CharacterCodec().apply(CharacterConstraintConfig::withLetter);
		
		Result<String> result = codec.encodeKey('3');
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyLetterConstraintViolation() {
		Codec<Character> codec = new CharacterCodec().apply(CharacterConstraintConfig::withLetter);
		
		Result<Character> result = codec.decodeKey("@");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartDigitConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = new CharacterCodec().apply(CharacterConstraintConfig::withDigit);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), '5');
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartDigitConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = new CharacterCodec().apply(CharacterConstraintConfig::withDigit);
		
		Result<Character> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("0"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyDigitConstraintSuccess() {
		Codec<Character> codec = new CharacterCodec().apply(CharacterConstraintConfig::withDigit);
		
		Result<String> result = codec.encodeKey('9');
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyDigitConstraintSuccess() {
		Codec<Character> codec = new CharacterCodec().apply(CharacterConstraintConfig::withDigit);
		
		Result<Character> result = codec.decodeKey("7");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartDigitConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = new CharacterCodec().apply(CharacterConstraintConfig::withDigit);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 'a');
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartDigitConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = new CharacterCodec().apply(CharacterConstraintConfig::withDigit);
		
		Result<Character> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("z"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyDigitConstraintViolation() {
		Codec<Character> codec = new CharacterCodec().apply(CharacterConstraintConfig::withDigit);
		
		Result<String> result = codec.encodeKey('X');
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyDigitConstraintViolation() {
		Codec<Character> codec = new CharacterCodec().apply(CharacterConstraintConfig::withDigit);
		
		Result<Character> result = codec.decodeKey("!");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartAlphanumericConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = new CharacterCodec().apply(CharacterConstraintConfig::withAlphanumeric);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 'a');
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartAlphanumericConstraintSuccessWithDigit() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = new CharacterCodec().apply(CharacterConstraintConfig::withAlphanumeric);
		
		Result<Character> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("5"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyAlphanumericConstraintSuccess() {
		Codec<Character> codec = new CharacterCodec().apply(CharacterConstraintConfig::withAlphanumeric);
		
		Result<String> result = codec.encodeKey('Z');
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyAlphanumericConstraintSuccess() {
		Codec<Character> codec = new CharacterCodec().apply(CharacterConstraintConfig::withAlphanumeric);
		
		Result<Character> result = codec.decodeKey("9");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartAlphanumericConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = new CharacterCodec().apply(CharacterConstraintConfig::withAlphanumeric);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), '@');
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartAlphanumericConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = new CharacterCodec().apply(CharacterConstraintConfig::withAlphanumeric);
		
		Result<Character> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("#"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyAlphanumericConstraintViolation() {
		Codec<Character> codec = new CharacterCodec().apply(CharacterConstraintConfig::withAlphanumeric);
		
		Result<String> result = codec.encodeKey('!');
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyAlphanumericConstraintViolation() {
		Codec<Character> codec = new CharacterCodec().apply(CharacterConstraintConfig::withAlphanumeric);
		
		Result<Character> result = codec.decodeKey("$");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartWhitespaceConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = new CharacterCodec().apply(CharacterConstraintConfig::withWhitespace);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), ' ');
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartWhitespaceConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = new CharacterCodec().apply(CharacterConstraintConfig::withWhitespace);
		
		Result<Character> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("\t"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyWhitespaceConstraintSuccess() {
		Codec<Character> codec = new CharacterCodec().apply(CharacterConstraintConfig::withWhitespace);
		
		Result<String> result = codec.encodeKey('\n');
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyWhitespaceConstraintSuccess() {
		Codec<Character> codec = new CharacterCodec().apply(CharacterConstraintConfig::withWhitespace);
		
		Result<Character> result = codec.decodeKey(" ");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartWhitespaceConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = new CharacterCodec().apply(CharacterConstraintConfig::withWhitespace);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 'a');
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartWhitespaceConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = new CharacterCodec().apply(CharacterConstraintConfig::withWhitespace);
		
		Result<Character> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("5"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyWhitespaceConstraintViolation() {
		Codec<Character> codec = new CharacterCodec().apply(CharacterConstraintConfig::withWhitespace);
		
		Result<String> result = codec.encodeKey('X');
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyWhitespaceConstraintViolation() {
		Codec<Character> codec = new CharacterCodec().apply(CharacterConstraintConfig::withWhitespace);
		
		Result<Character> result = codec.decodeKey("@");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartUpperCaseConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = new CharacterCodec().apply(CharacterConstraintConfig::withUpperCase);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 'A');
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartUpperCaseConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = new CharacterCodec().apply(CharacterConstraintConfig::withUpperCase);
		
		Result<Character> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("Z"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyUpperCaseConstraintSuccess() {
		Codec<Character> codec = new CharacterCodec().apply(CharacterConstraintConfig::withUpperCase);
		
		Result<String> result = codec.encodeKey('M');
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyUpperCaseConstraintSuccess() {
		Codec<Character> codec = new CharacterCodec().apply(CharacterConstraintConfig::withUpperCase);
		
		Result<Character> result = codec.decodeKey("Q");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartUpperCaseConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = new CharacterCodec().apply(CharacterConstraintConfig::withUpperCase);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 'a');
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartUpperCaseConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = new CharacterCodec().apply(CharacterConstraintConfig::withUpperCase);
		
		Result<Character> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("z"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyUpperCaseConstraintViolation() {
		Codec<Character> codec = new CharacterCodec().apply(CharacterConstraintConfig::withUpperCase);
		
		Result<String> result = codec.encodeKey('m');
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyUpperCaseConstraintViolation() {
		Codec<Character> codec = new CharacterCodec().apply(CharacterConstraintConfig::withUpperCase);
		
		Result<Character> result = codec.decodeKey("q");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartLowerCaseConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = new CharacterCodec().apply(CharacterConstraintConfig::withLowerCase);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 'a');
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartLowerCaseConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = new CharacterCodec().apply(CharacterConstraintConfig::withLowerCase);
		
		Result<Character> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("z"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyLowerCaseConstraintSuccess() {
		Codec<Character> codec = new CharacterCodec().apply(CharacterConstraintConfig::withLowerCase);
		
		Result<String> result = codec.encodeKey('m');
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyLowerCaseConstraintSuccess() {
		Codec<Character> codec = new CharacterCodec().apply(CharacterConstraintConfig::withLowerCase);
		
		Result<Character> result = codec.decodeKey("q");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartLowerCaseConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = new CharacterCodec().apply(CharacterConstraintConfig::withLowerCase);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 'A');
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartLowerCaseConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = new CharacterCodec().apply(CharacterConstraintConfig::withLowerCase);
		
		Result<Character> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("Z"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyLowerCaseConstraintViolation() {
		Codec<Character> codec = new CharacterCodec().apply(CharacterConstraintConfig::withLowerCase);
		
		Result<String> result = codec.encodeKey('M');
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyLowerCaseConstraintViolation() {
		Codec<Character> codec = new CharacterCodec().apply(CharacterConstraintConfig::withLowerCase);
		
		Result<Character> result = codec.decodeKey("Q");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartAsciiConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = new CharacterCodec().apply(CharacterConstraintConfig::withAscii);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 'a');
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartAsciiConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = new CharacterCodec().apply(CharacterConstraintConfig::withAscii);
		
		Result<Character> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("!"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyAsciiConstraintSuccess() {
		Codec<Character> codec = new CharacterCodec().apply(CharacterConstraintConfig::withAscii);
		
		Result<String> result = codec.encodeKey('~');
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyAsciiConstraintSuccess() {
		Codec<Character> codec = new CharacterCodec().apply(CharacterConstraintConfig::withAscii);
		
		Result<Character> result = codec.decodeKey("5");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartAsciiConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = new CharacterCodec().apply(CharacterConstraintConfig::withAscii);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), '\u00E9');
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartAsciiConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = new CharacterCodec().apply(CharacterConstraintConfig::withAscii);
		
		Result<Character> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("\u00FC"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyAsciiConstraintViolation() {
		Codec<Character> codec = new CharacterCodec().apply(CharacterConstraintConfig::withAscii);
		
		Result<String> result = codec.encodeKey('\u00F1');
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyAsciiConstraintViolation() {
		Codec<Character> codec = new CharacterCodec().apply(CharacterConstraintConfig::withAscii);
		
		Result<Character> result = codec.decodeKey("\u00E4");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartLatin1ConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = new CharacterCodec().apply(CharacterConstraintConfig::withLatin1);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), '\u00E9');
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartLatin1ConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = new CharacterCodec().apply(CharacterConstraintConfig::withLatin1);
		
		Result<Character> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("\u00FC"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyLatin1ConstraintSuccess() {
		Codec<Character> codec = new CharacterCodec().apply(CharacterConstraintConfig::withLatin1);
		
		Result<String> result = codec.encodeKey('\u00F1');
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyLatin1ConstraintSuccess() {
		Codec<Character> codec = new CharacterCodec().apply(CharacterConstraintConfig::withLatin1);
		
		Result<Character> result = codec.decodeKey("\u00E4");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartLatin1ConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = new CharacterCodec().apply(CharacterConstraintConfig::withLatin1);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), '\u4E16');
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartLatin1ConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = new CharacterCodec().apply(CharacterConstraintConfig::withLatin1);
		
		Result<Character> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("\u754C"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyLatin1ConstraintViolation() {
		Codec<Character> codec = new CharacterCodec().apply(CharacterConstraintConfig::withLatin1);
		
		Result<String> result = codec.encodeKey('\u3042');
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyLatin1ConstraintViolation() {
		Codec<Character> codec = new CharacterCodec().apply(CharacterConstraintConfig::withLatin1);
		
		Result<Character> result = codec.decodeKey("\u0410");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartGreaterThanConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = new CharacterCodec().apply(config -> config.withGreaterThan('m'));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 'z');
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartGreaterThanConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = new CharacterCodec().apply(config -> config.withGreaterThan('m'));
		
		Result<Character> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("n"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyGreaterThanConstraintSuccess() {
		Codec<Character> codec = new CharacterCodec().apply(config -> config.withGreaterThan('m'));
		
		Result<String> result = codec.encodeKey('x');
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyGreaterThanConstraintSuccess() {
		Codec<Character> codec = new CharacterCodec().apply(config -> config.withGreaterThan('m'));
		
		Result<Character> result = codec.decodeKey("p");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartGreaterThanConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = new CharacterCodec().apply(config -> config.withGreaterThan('m'));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 'a');
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartGreaterThanConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = new CharacterCodec().apply(config -> config.withGreaterThan('m'));
		
		Result<Character> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("m"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyGreaterThanConstraintViolation() {
		Codec<Character> codec = new CharacterCodec().apply(config -> config.withGreaterThan('m'));
		
		Result<String> result = codec.encodeKey('b');
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyGreaterThanConstraintViolation() {
		Codec<Character> codec = new CharacterCodec().apply(config -> config.withGreaterThan('m'));
		
		Result<Character> result = codec.decodeKey("k");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartLessThanConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = new CharacterCodec().apply(config -> config.withLessThan('m'));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 'a');
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartLessThanConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = new CharacterCodec().apply(config -> config.withLessThan('m'));
		
		Result<Character> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("l"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyLessThanConstraintSuccess() {
		Codec<Character> codec = new CharacterCodec().apply(config -> config.withLessThan('m'));
		
		Result<String> result = codec.encodeKey('b');
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyLessThanConstraintSuccess() {
		Codec<Character> codec = new CharacterCodec().apply(config -> config.withLessThan('m'));
		
		Result<Character> result = codec.decodeKey("k");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartLessThanConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = new CharacterCodec().apply(config -> config.withLessThan('m'));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 'z');
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartLessThanConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = new CharacterCodec().apply(config -> config.withLessThan('m'));
		
		Result<Character> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("m"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyLessThanConstraintViolation() {
		Codec<Character> codec = new CharacterCodec().apply(config -> config.withLessThan('m'));
		
		Result<String> result = codec.encodeKey('x');
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyLessThanConstraintViolation() {
		Codec<Character> codec = new CharacterCodec().apply(config -> config.withLessThan('m'));
		
		Result<Character> result = codec.decodeKey("n");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartBetweenOrEqualConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = new CharacterCodec().apply(config -> config.withBetweenOrEqual('a', 'z'));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 'm');
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartBetweenOrEqualConstraintSuccessBoundary() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = new CharacterCodec().apply(config -> config.withBetweenOrEqual('a', 'z'));
		
		Result<Character> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("a"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyBetweenOrEqualConstraintSuccess() {
		Codec<Character> codec = new CharacterCodec().apply(config -> config.withBetweenOrEqual('a', 'z'));
		
		Result<String> result = codec.encodeKey('z');
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyBetweenOrEqualConstraintSuccess() {
		Codec<Character> codec = new CharacterCodec().apply(config -> config.withBetweenOrEqual('a', 'z'));
		
		Result<Character> result = codec.decodeKey("k");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartBetweenOrEqualConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = new CharacterCodec().apply(config -> config.withBetweenOrEqual('a', 'z'));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 'A');
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartBetweenOrEqualConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = new CharacterCodec().apply(config -> config.withBetweenOrEqual('a', 'z'));
		
		Result<Character> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("5"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyBetweenOrEqualConstraintViolation() {
		Codec<Character> codec = new CharacterCodec().apply(config -> config.withBetweenOrEqual('a', 'z'));
		
		Result<String> result = codec.encodeKey('0');
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyBetweenOrEqualConstraintViolation() {
		Codec<Character> codec = new CharacterCodec().apply(config -> config.withBetweenOrEqual('a', 'z'));
		
		Result<Character> result = codec.decodeKey("!");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartPunctuationConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = new CharacterCodec().apply(CharacterConstraintConfig::withPunctuation);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), '.');
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartPunctuationConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = new CharacterCodec().apply(CharacterConstraintConfig::withPunctuation);
		
		Result<Character> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(","));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyPunctuationConstraintSuccess() {
		Codec<Character> codec = new CharacterCodec().apply(CharacterConstraintConfig::withPunctuation);
		
		Result<String> result = codec.encodeKey('!');
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyPunctuationConstraintSuccess() {
		Codec<Character> codec = new CharacterCodec().apply(CharacterConstraintConfig::withPunctuation);
		
		Result<Character> result = codec.decodeKey("?");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartPunctuationConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = new CharacterCodec().apply(CharacterConstraintConfig::withPunctuation);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 'a');
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartPunctuationConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = new CharacterCodec().apply(CharacterConstraintConfig::withPunctuation);
		
		Result<Character> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("5"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyPunctuationConstraintViolation() {
		Codec<Character> codec = new CharacterCodec().apply(CharacterConstraintConfig::withPunctuation);
		
		Result<String> result = codec.encodeKey('X');
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyPunctuationConstraintViolation() {
		Codec<Character> codec = new CharacterCodec().apply(CharacterConstraintConfig::withPunctuation);
		
		Result<Character> result = codec.decodeKey(" ");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartSymbolConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = new CharacterCodec().apply(CharacterConstraintConfig::withSymbol);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), '$');
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartSymbolConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = new CharacterCodec().apply(CharacterConstraintConfig::withSymbol);
		
		Result<Character> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("+"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeySymbolConstraintSuccess() {
		Codec<Character> codec = new CharacterCodec().apply(CharacterConstraintConfig::withSymbol);
		
		Result<String> result = codec.encodeKey('=');
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeySymbolConstraintSuccess() {
		Codec<Character> codec = new CharacterCodec().apply(CharacterConstraintConfig::withSymbol);
		
		Result<Character> result = codec.decodeKey("~");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartSymbolConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = new CharacterCodec().apply(CharacterConstraintConfig::withSymbol);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 'a');
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartSymbolConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = new CharacterCodec().apply(CharacterConstraintConfig::withSymbol);
		
		Result<Character> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("5"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeySymbolConstraintViolation() {
		Codec<Character> codec = new CharacterCodec().apply(CharacterConstraintConfig::withSymbol);
		
		Result<String> result = codec.encodeKey('X');
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeySymbolConstraintViolation() {
		Codec<Character> codec = new CharacterCodec().apply(CharacterConstraintConfig::withSymbol);
		
		Result<Character> result = codec.decodeKey(" ");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartControlConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = new CharacterCodec().apply(CharacterConstraintConfig::withControl);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), '\0');
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartControlConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = new CharacterCodec().apply(CharacterConstraintConfig::withControl);
		
		Result<Character> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("\u0007"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyControlConstraintSuccess() {
		Codec<Character> codec = new CharacterCodec().apply(CharacterConstraintConfig::withControl);
		
		Result<String> result = codec.encodeKey('\u001B');
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyControlConstraintSuccess() {
		Codec<Character> codec = new CharacterCodec().apply(CharacterConstraintConfig::withControl);
		
		Result<Character> result = codec.decodeKey("\u0003");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartControlConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = new CharacterCodec().apply(CharacterConstraintConfig::withControl);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 'a');
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartControlConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = new CharacterCodec().apply(CharacterConstraintConfig::withControl);
		
		Result<Character> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("5"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyControlConstraintViolation() {
		Codec<Character> codec = new CharacterCodec().apply(CharacterConstraintConfig::withControl);
		
		Result<String> result = codec.encodeKey('X');
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyControlConstraintViolation() {
		Codec<Character> codec = new CharacterCodec().apply(CharacterConstraintConfig::withControl);
		
		Result<Character> result = codec.decodeKey(" ");
		assertTrue(result.isError());
	}
}
