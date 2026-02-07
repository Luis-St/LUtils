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

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link CharacterCodec} with constraints.<br>
 *
 * @author Luis-St
 */
class ConstrainedCharacterCodecTest {
	
	@Test
	void encodeWithValidConstrainedValue() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = Codecs.CHARACTER.letter();
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), 'a');
		assertEquals(new JsonPrimitive("a"), result);
	}
	
	@Test
	void decodeWithValidConstrainedValue() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = Codecs.CHARACTER.letter();
		
		Character result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("a"));
		assertEquals('a', result);
	}
	
	@Test
	void encodeKeyWithValidConstrainedValue() throws EncoderException {
		Codec<Character> codec = Codecs.CHARACTER.letter();
		
		String result = codec.encodeKey('a');
		assertEquals("a", result);
	}
	
	@Test
	void decodeKeyWithValidConstrainedValue() throws DecoderException {
		Codec<Character> codec = Codecs.CHARACTER.letter();
		
		Character result = codec.decodeKey("a");
		assertEquals('a', result);
	}
	
	@Test
	void encodeEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = Codecs.CHARACTER.equalTo('x');
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), 'x'));
	}
	
	@Test
	void decodeEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = Codecs.CHARACTER.equalTo('x');
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("x")));
	}
	
	@Test
	void encodeKeyEqualToConstraintSuccess() {
		Codec<Character> codec = Codecs.CHARACTER.equalTo('x');
		
		assertDoesNotThrow(() -> codec.encodeKey('x'));
	}
	
	@Test
	void decodeKeyEqualToConstraintSuccess() {
		Codec<Character> codec = Codecs.CHARACTER.equalTo('x');
		
		assertDoesNotThrow(() -> codec.decodeKey("x"));
	}
	
	@Test
	void encodeEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = Codecs.CHARACTER.equalTo('x');
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), 'y'));
	}
	
	@Test
	void decodeEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = Codecs.CHARACTER.equalTo('x');
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("y")));
	}
	
	@Test
	void encodeKeyEqualToConstraintViolation() {
		Codec<Character> codec = Codecs.CHARACTER.equalTo('x');
		
		assertThrows(EncoderException.class, () -> codec.encodeKey('y'));
	}
	
	@Test
	void decodeKeyEqualToConstraintViolation() {
		Codec<Character> codec = Codecs.CHARACTER.equalTo('x');
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("y"));
	}
	
	@Test
	void encodeNotEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = Codecs.CHARACTER.notEqualTo('x');
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), 'a'));
	}
	
	@Test
	void decodeNotEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = Codecs.CHARACTER.notEqualTo('x');
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("a")));
	}
	
	@Test
	void encodeKeyNotEqualToConstraintSuccess() {
		Codec<Character> codec = Codecs.CHARACTER.notEqualTo('x');
		
		assertDoesNotThrow(() -> codec.encodeKey('a'));
	}
	
	@Test
	void decodeKeyNotEqualToConstraintSuccess() {
		Codec<Character> codec = Codecs.CHARACTER.notEqualTo('x');
		
		assertDoesNotThrow(() -> codec.decodeKey("a"));
	}
	
	@Test
	void encodeNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = Codecs.CHARACTER.notEqualTo('x');
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), 'x'));
	}
	
	@Test
	void decodeNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = Codecs.CHARACTER.notEqualTo('x');
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("x")));
	}
	
	@Test
	void encodeKeyNotEqualToConstraintViolation() {
		Codec<Character> codec = Codecs.CHARACTER.notEqualTo('x');
		
		assertThrows(EncoderException.class, () -> codec.encodeKey('x'));
	}
	
	@Test
	void decodeKeyNotEqualToConstraintViolation() {
		Codec<Character> codec = Codecs.CHARACTER.notEqualTo('x');
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("x"));
	}
	
	@Test
	void encodeInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = Codecs.CHARACTER.in(Set.of('a', 'b', 'c'));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), 'b'));
	}
	
	@Test
	void decodeInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = Codecs.CHARACTER.in(Set.of('a', 'b', 'c'));
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("b")));
	}
	
	@Test
	void encodeKeyInConstraintSuccess() {
		Codec<Character> codec = Codecs.CHARACTER.in(Set.of('a', 'b', 'c'));
		
		assertDoesNotThrow(() -> codec.encodeKey('b'));
	}
	
	@Test
	void decodeKeyInConstraintSuccess() {
		Codec<Character> codec = Codecs.CHARACTER.in(Set.of('a', 'b', 'c'));
		
		assertDoesNotThrow(() -> codec.decodeKey("b"));
	}
	
	@Test
	void encodeInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = Codecs.CHARACTER.in(Set.of('a', 'b', 'c'));
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), 'z'));
	}
	
	@Test
	void decodeInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = Codecs.CHARACTER.in(Set.of('a', 'b', 'c'));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("z")));
	}
	
	@Test
	void encodeKeyInConstraintViolation() {
		Codec<Character> codec = Codecs.CHARACTER.in(Set.of('a', 'b', 'c'));
		
		assertThrows(EncoderException.class, () -> codec.encodeKey('z'));
	}
	
	@Test
	void decodeKeyInConstraintViolation() {
		Codec<Character> codec = Codecs.CHARACTER.in(Set.of('a', 'b', 'c'));
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("z"));
	}
	
	@Test
	void encodeNotInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = Codecs.CHARACTER.notIn(Set.of('x', 'y', 'z'));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), 'a'));
	}
	
	@Test
	void decodeNotInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = Codecs.CHARACTER.notIn(Set.of('x', 'y', 'z'));
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("a")));
	}
	
	@Test
	void encodeKeyNotInConstraintSuccess() {
		Codec<Character> codec = Codecs.CHARACTER.notIn(Set.of('x', 'y', 'z'));
		
		assertDoesNotThrow(() -> codec.encodeKey('a'));
	}
	
	@Test
	void decodeKeyNotInConstraintSuccess() {
		Codec<Character> codec = Codecs.CHARACTER.notIn(Set.of('x', 'y', 'z'));
		
		assertDoesNotThrow(() -> codec.decodeKey("a"));
	}
	
	@Test
	void encodeNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = Codecs.CHARACTER.notIn(Set.of('x', 'y', 'z'));
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), 'x'));
	}
	
	@Test
	void decodeNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = Codecs.CHARACTER.notIn(Set.of('x', 'y', 'z'));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("x")));
	}
	
	@Test
	void encodeKeyNotInConstraintViolation() {
		Codec<Character> codec = Codecs.CHARACTER.notIn(Set.of('x', 'y', 'z'));
		
		assertThrows(EncoderException.class, () -> codec.encodeKey('x'));
	}
	
	@Test
	void decodeKeyNotInConstraintViolation() {
		Codec<Character> codec = Codecs.CHARACTER.notIn(Set.of('x', 'y', 'z'));
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("x"));
	}
	
	@Test
	void encodeLetterConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = Codecs.CHARACTER.letter();
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), 'a'));
	}
	
	@Test
	void decodeLetterConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = Codecs.CHARACTER.letter();
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("Z")));
	}
	
	@Test
	void encodeKeyLetterConstraintSuccess() {
		Codec<Character> codec = Codecs.CHARACTER.letter();
		
		assertDoesNotThrow(() -> codec.encodeKey('b'));
	}
	
	@Test
	void decodeKeyLetterConstraintSuccess() {
		Codec<Character> codec = Codecs.CHARACTER.letter();
		
		assertDoesNotThrow(() -> codec.decodeKey("c"));
	}
	
	@Test
	void encodeLetterConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = Codecs.CHARACTER.letter();
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), '5'));
	}
	
	@Test
	void decodeLetterConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = Codecs.CHARACTER.letter();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("9")));
	}
	
	@Test
	void encodeKeyLetterConstraintViolation() {
		Codec<Character> codec = Codecs.CHARACTER.letter();
		
		assertThrows(EncoderException.class, () -> codec.encodeKey('3'));
	}
	
	@Test
	void decodeKeyLetterConstraintViolation() {
		Codec<Character> codec = Codecs.CHARACTER.letter();
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("@"));
	}
	
	@Test
	void encodeDigitConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = Codecs.CHARACTER.digit();
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), '5'));
	}
	
	@Test
	void decodeDigitConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = Codecs.CHARACTER.digit();
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("0")));
	}
	
	@Test
	void encodeKeyDigitConstraintSuccess() {
		Codec<Character> codec = Codecs.CHARACTER.digit();
		
		assertDoesNotThrow(() -> codec.encodeKey('9'));
	}
	
	@Test
	void decodeKeyDigitConstraintSuccess() {
		Codec<Character> codec = Codecs.CHARACTER.digit();
		
		assertDoesNotThrow(() -> codec.decodeKey("7"));
	}
	
	@Test
	void encodeDigitConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = Codecs.CHARACTER.digit();
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), 'a'));
	}
	
	@Test
	void decodeDigitConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = Codecs.CHARACTER.digit();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("z")));
	}
	
	@Test
	void encodeKeyDigitConstraintViolation() {
		Codec<Character> codec = Codecs.CHARACTER.digit();
		
		assertThrows(EncoderException.class, () -> codec.encodeKey('X'));
	}
	
	@Test
	void decodeKeyDigitConstraintViolation() {
		Codec<Character> codec = Codecs.CHARACTER.digit();
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("!"));
	}
	
	@Test
	void encodeAlphanumericConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = Codecs.CHARACTER.alphanumeric();
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), 'a'));
	}
	
	@Test
	void decodeAlphanumericConstraintSuccessWithDigit() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = Codecs.CHARACTER.alphanumeric();
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("5")));
	}
	
	@Test
	void encodeKeyAlphanumericConstraintSuccess() {
		Codec<Character> codec = Codecs.CHARACTER.alphanumeric();
		
		assertDoesNotThrow(() -> codec.encodeKey('Z'));
	}
	
	@Test
	void decodeKeyAlphanumericConstraintSuccess() {
		Codec<Character> codec = Codecs.CHARACTER.alphanumeric();
		
		assertDoesNotThrow(() -> codec.decodeKey("9"));
	}
	
	@Test
	void encodeAlphanumericConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = Codecs.CHARACTER.alphanumeric();
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), '@'));
	}
	
	@Test
	void decodeAlphanumericConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = Codecs.CHARACTER.alphanumeric();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("#")));
	}
	
	@Test
	void encodeKeyAlphanumericConstraintViolation() {
		Codec<Character> codec = Codecs.CHARACTER.alphanumeric();
		
		assertThrows(EncoderException.class, () -> codec.encodeKey('!'));
	}
	
	@Test
	void decodeKeyAlphanumericConstraintViolation() {
		Codec<Character> codec = Codecs.CHARACTER.alphanumeric();
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("$"));
	}
	
	@Test
	void encodeWhitespaceConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = Codecs.CHARACTER.whitespace();
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), ' '));
	}
	
	@Test
	void decodeWhitespaceConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = Codecs.CHARACTER.whitespace();
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("\t")));
	}
	
	@Test
	void encodeKeyWhitespaceConstraintSuccess() {
		Codec<Character> codec = Codecs.CHARACTER.whitespace();
		
		assertDoesNotThrow(() -> codec.encodeKey('\n'));
	}
	
	@Test
	void decodeKeyWhitespaceConstraintSuccess() {
		Codec<Character> codec = Codecs.CHARACTER.whitespace();
		
		assertDoesNotThrow(() -> codec.decodeKey(" "));
	}
	
	@Test
	void encodeWhitespaceConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = Codecs.CHARACTER.whitespace();
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), 'a'));
	}
	
	@Test
	void decodeWhitespaceConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = Codecs.CHARACTER.whitespace();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("5")));
	}
	
	@Test
	void encodeKeyWhitespaceConstraintViolation() {
		Codec<Character> codec = Codecs.CHARACTER.whitespace();
		
		assertThrows(EncoderException.class, () -> codec.encodeKey('X'));
	}
	
	@Test
	void decodeKeyWhitespaceConstraintViolation() {
		Codec<Character> codec = Codecs.CHARACTER.whitespace();
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("@"));
	}
	
	@Test
	void encodeUpperCaseConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = Codecs.CHARACTER.upperCase();
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), 'A'));
	}
	
	@Test
	void decodeUpperCaseConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = Codecs.CHARACTER.upperCase();
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("Z")));
	}
	
	@Test
	void encodeKeyUpperCaseConstraintSuccess() {
		Codec<Character> codec = Codecs.CHARACTER.upperCase();
		
		assertDoesNotThrow(() -> codec.encodeKey('M'));
	}
	
	@Test
	void decodeKeyUpperCaseConstraintSuccess() {
		Codec<Character> codec = Codecs.CHARACTER.upperCase();
		
		assertDoesNotThrow(() -> codec.decodeKey("Q"));
	}
	
	@Test
	void encodeUpperCaseConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = Codecs.CHARACTER.upperCase();
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), 'a'));
	}
	
	@Test
	void decodeUpperCaseConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = Codecs.CHARACTER.upperCase();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("z")));
	}
	
	@Test
	void encodeKeyUpperCaseConstraintViolation() {
		Codec<Character> codec = Codecs.CHARACTER.upperCase();
		
		assertThrows(EncoderException.class, () -> codec.encodeKey('m'));
	}
	
	@Test
	void decodeKeyUpperCaseConstraintViolation() {
		Codec<Character> codec = Codecs.CHARACTER.upperCase();
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("q"));
	}
	
	@Test
	void encodeLowerCaseConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = Codecs.CHARACTER.lowerCase();
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), 'a'));
	}
	
	@Test
	void decodeLowerCaseConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = Codecs.CHARACTER.lowerCase();
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("z")));
	}
	
	@Test
	void encodeKeyLowerCaseConstraintSuccess() {
		Codec<Character> codec = Codecs.CHARACTER.lowerCase();
		
		assertDoesNotThrow(() -> codec.encodeKey('m'));
	}
	
	@Test
	void decodeKeyLowerCaseConstraintSuccess() {
		Codec<Character> codec = Codecs.CHARACTER.lowerCase();
		
		assertDoesNotThrow(() -> codec.decodeKey("q"));
	}
	
	@Test
	void encodeLowerCaseConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = Codecs.CHARACTER.lowerCase();
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), 'A'));
	}
	
	@Test
	void decodeLowerCaseConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = Codecs.CHARACTER.lowerCase();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("Z")));
	}
	
	@Test
	void encodeKeyLowerCaseConstraintViolation() {
		Codec<Character> codec = Codecs.CHARACTER.lowerCase();
		
		assertThrows(EncoderException.class, () -> codec.encodeKey('M'));
	}
	
	@Test
	void decodeKeyLowerCaseConstraintViolation() {
		Codec<Character> codec = Codecs.CHARACTER.lowerCase();
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("Q"));
	}
	
	@Test
	void encodeAsciiConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = Codecs.CHARACTER.ascii();
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), 'a'));
	}
	
	@Test
	void decodeAsciiConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = Codecs.CHARACTER.ascii();
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("!")));
	}
	
	@Test
	void encodeKeyAsciiConstraintSuccess() {
		Codec<Character> codec = Codecs.CHARACTER.ascii();
		
		assertDoesNotThrow(() -> codec.encodeKey('~'));
	}
	
	@Test
	void decodeKeyAsciiConstraintSuccess() {
		Codec<Character> codec = Codecs.CHARACTER.ascii();
		
		assertDoesNotThrow(() -> codec.decodeKey("5"));
	}
	
	@Test
	void encodeAsciiConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = Codecs.CHARACTER.ascii();
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), '\u00E9'));
	}
	
	@Test
	void decodeAsciiConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = Codecs.CHARACTER.ascii();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("\u00FC")));
	}
	
	@Test
	void encodeKeyAsciiConstraintViolation() {
		Codec<Character> codec = Codecs.CHARACTER.ascii();
		
		assertThrows(EncoderException.class, () -> codec.encodeKey('\u00F1'));
	}
	
	@Test
	void decodeKeyAsciiConstraintViolation() {
		Codec<Character> codec = Codecs.CHARACTER.ascii();
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("\u00E4"));
	}
	
	@Test
	void encodeLatin1ConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = Codecs.CHARACTER.latin1();
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), '\u00E9'));
	}
	
	@Test
	void decodeLatin1ConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = Codecs.CHARACTER.latin1();
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("\u00FC")));
	}
	
	@Test
	void encodeKeyLatin1ConstraintSuccess() {
		Codec<Character> codec = Codecs.CHARACTER.latin1();
		
		assertDoesNotThrow(() -> codec.encodeKey('\u00F1'));
	}
	
	@Test
	void decodeKeyLatin1ConstraintSuccess() {
		Codec<Character> codec = Codecs.CHARACTER.latin1();
		
		assertDoesNotThrow(() -> codec.decodeKey("\u00E4"));
	}
	
	@Test
	void encodeLatin1ConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = Codecs.CHARACTER.latin1();
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), '\u4E16'));
	}
	
	@Test
	void decodeLatin1ConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = Codecs.CHARACTER.latin1();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("\u754C")));
	}
	
	@Test
	void encodeKeyLatin1ConstraintViolation() {
		Codec<Character> codec = Codecs.CHARACTER.latin1();
		
		assertThrows(EncoderException.class, () -> codec.encodeKey('\u3042'));
	}
	
	@Test
	void decodeKeyLatin1ConstraintViolation() {
		Codec<Character> codec = Codecs.CHARACTER.latin1();
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("\u0410"));
	}
	
	@Test
	void encodeGreaterThanConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = Codecs.CHARACTER.greaterThan('m');
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), 'z'));
	}
	
	@Test
	void decodeGreaterThanConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = Codecs.CHARACTER.greaterThan('m');
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("n")));
	}
	
	@Test
	void encodeKeyGreaterThanConstraintSuccess() {
		Codec<Character> codec = Codecs.CHARACTER.greaterThan('m');
		
		assertDoesNotThrow(() -> codec.encodeKey('x'));
	}
	
	@Test
	void decodeKeyGreaterThanConstraintSuccess() {
		Codec<Character> codec = Codecs.CHARACTER.greaterThan('m');
		
		assertDoesNotThrow(() -> codec.decodeKey("p"));
	}
	
	@Test
	void encodeGreaterThanConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = Codecs.CHARACTER.greaterThan('m');
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), 'a'));
	}
	
	@Test
	void decodeGreaterThanConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = Codecs.CHARACTER.greaterThan('m');
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("m")));
	}
	
	@Test
	void encodeKeyGreaterThanConstraintViolation() {
		Codec<Character> codec = Codecs.CHARACTER.greaterThan('m');
		
		assertThrows(EncoderException.class, () -> codec.encodeKey('b'));
	}
	
	@Test
	void decodeKeyGreaterThanConstraintViolation() {
		Codec<Character> codec = Codecs.CHARACTER.greaterThan('m');
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("k"));
	}
	
	@Test
	void encodeLessThanConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = Codecs.CHARACTER.lessThan('m');
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), 'a'));
	}
	
	@Test
	void decodeLessThanConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = Codecs.CHARACTER.lessThan('m');
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("l")));
	}
	
	@Test
	void encodeKeyLessThanConstraintSuccess() {
		Codec<Character> codec = Codecs.CHARACTER.lessThan('m');
		
		assertDoesNotThrow(() -> codec.encodeKey('b'));
	}
	
	@Test
	void decodeKeyLessThanConstraintSuccess() {
		Codec<Character> codec = Codecs.CHARACTER.lessThan('m');
		
		assertDoesNotThrow(() -> codec.decodeKey("k"));
	}
	
	@Test
	void encodeLessThanConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = Codecs.CHARACTER.lessThan('m');
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), 'z'));
	}
	
	@Test
	void decodeLessThanConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = Codecs.CHARACTER.lessThan('m');
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("m")));
	}
	
	@Test
	void encodeKeyLessThanConstraintViolation() {
		Codec<Character> codec = Codecs.CHARACTER.lessThan('m');
		
		assertThrows(EncoderException.class, () -> codec.encodeKey('x'));
	}
	
	@Test
	void decodeKeyLessThanConstraintViolation() {
		Codec<Character> codec = Codecs.CHARACTER.lessThan('m');
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("n"));
	}
	
	@Test
	void encodeBetweenOrEqualConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = Codecs.CHARACTER.betweenOrEqual('a', 'z');
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), 'm'));
	}
	
	@Test
	void decodeBetweenOrEqualConstraintSuccessBoundary() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = Codecs.CHARACTER.betweenOrEqual('a', 'z');
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("a")));
	}
	
	@Test
	void encodeKeyBetweenOrEqualConstraintSuccess() {
		Codec<Character> codec = Codecs.CHARACTER.betweenOrEqual('a', 'z');
		
		assertDoesNotThrow(() -> codec.encodeKey('z'));
	}
	
	@Test
	void decodeKeyBetweenOrEqualConstraintSuccess() {
		Codec<Character> codec = Codecs.CHARACTER.betweenOrEqual('a', 'z');
		
		assertDoesNotThrow(() -> codec.decodeKey("k"));
	}
	
	@Test
	void encodeBetweenOrEqualConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = Codecs.CHARACTER.betweenOrEqual('a', 'z');
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), 'A'));
	}
	
	@Test
	void decodeBetweenOrEqualConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = Codecs.CHARACTER.betweenOrEqual('a', 'z');
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("5")));
	}
	
	@Test
	void encodeKeyBetweenOrEqualConstraintViolation() {
		Codec<Character> codec = Codecs.CHARACTER.betweenOrEqual('a', 'z');
		
		assertThrows(EncoderException.class, () -> codec.encodeKey('0'));
	}
	
	@Test
	void decodeKeyBetweenOrEqualConstraintViolation() {
		Codec<Character> codec = Codecs.CHARACTER.betweenOrEqual('a', 'z');
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("!"));
	}
	
	@Test
	void encodePunctuationConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = Codecs.CHARACTER.punctuation();
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), '.'));
	}
	
	@Test
	void decodePunctuationConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = Codecs.CHARACTER.punctuation();
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(",")));
	}
	
	@Test
	void encodeKeyPunctuationConstraintSuccess() {
		Codec<Character> codec = Codecs.CHARACTER.punctuation();
		
		assertDoesNotThrow(() -> codec.encodeKey('!'));
	}
	
	@Test
	void decodeKeyPunctuationConstraintSuccess() {
		Codec<Character> codec = Codecs.CHARACTER.punctuation();
		
		assertDoesNotThrow(() -> codec.decodeKey("?"));
	}
	
	@Test
	void encodePunctuationConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = Codecs.CHARACTER.punctuation();
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), 'a'));
	}
	
	@Test
	void decodePunctuationConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = Codecs.CHARACTER.punctuation();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("5")));
	}
	
	@Test
	void encodeKeyPunctuationConstraintViolation() {
		Codec<Character> codec = Codecs.CHARACTER.punctuation();
		
		assertThrows(EncoderException.class, () -> codec.encodeKey('X'));
	}
	
	@Test
	void decodeKeyPunctuationConstraintViolation() {
		Codec<Character> codec = Codecs.CHARACTER.punctuation();
		
		assertThrows(DecoderException.class, () -> codec.decodeKey(" "));
	}
	
	@Test
	void encodeSymbolConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = Codecs.CHARACTER.symbol();
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), '$'));
	}
	
	@Test
	void decodeSymbolConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = Codecs.CHARACTER.symbol();
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("+")));
	}
	
	@Test
	void encodeKeySymbolConstraintSuccess() {
		Codec<Character> codec = Codecs.CHARACTER.symbol();
		
		assertDoesNotThrow(() -> codec.encodeKey('='));
	}
	
	@Test
	void decodeKeySymbolConstraintSuccess() {
		Codec<Character> codec = Codecs.CHARACTER.symbol();
		
		assertDoesNotThrow(() -> codec.decodeKey("~"));
	}
	
	@Test
	void encodeSymbolConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = Codecs.CHARACTER.symbol();
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), 'a'));
	}
	
	@Test
	void decodeSymbolConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = Codecs.CHARACTER.symbol();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("5")));
	}
	
	@Test
	void encodeKeySymbolConstraintViolation() {
		Codec<Character> codec = Codecs.CHARACTER.symbol();
		
		assertThrows(EncoderException.class, () -> codec.encodeKey('X'));
	}
	
	@Test
	void decodeKeySymbolConstraintViolation() {
		Codec<Character> codec = Codecs.CHARACTER.symbol();
		
		assertThrows(DecoderException.class, () -> codec.decodeKey(" "));
	}
	
	@Test
	void encodeControlConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = Codecs.CHARACTER.control();
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), '\0'));
	}
	
	@Test
	void decodeControlConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = Codecs.CHARACTER.control();
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("\u0007")));
	}
	
	@Test
	void encodeKeyControlConstraintSuccess() {
		Codec<Character> codec = Codecs.CHARACTER.control();
		
		assertDoesNotThrow(() -> codec.encodeKey('\u001B'));
	}
	
	@Test
	void decodeKeyControlConstraintSuccess() {
		Codec<Character> codec = Codecs.CHARACTER.control();
		
		assertDoesNotThrow(() -> codec.decodeKey("\u0003"));
	}
	
	@Test
	void encodeControlConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = Codecs.CHARACTER.control();
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), 'a'));
	}
	
	@Test
	void decodeControlConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = Codecs.CHARACTER.control();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("5")));
	}
	
	@Test
	void encodeKeyControlConstraintViolation() {
		Codec<Character> codec = Codecs.CHARACTER.control();
		
		assertThrows(EncoderException.class, () -> codec.encodeKey('X'));
	}
	
	@Test
	void decodeKeyControlConstraintViolation() {
		Codec<Character> codec = Codecs.CHARACTER.control();
		
		assertThrows(DecoderException.class, () -> codec.decodeKey(" "));
	}
}
