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

package net.luis.utils.io.token.rule.actions.core;

import net.luis.utils.io.token.TokenPosition;
import net.luis.utils.io.token.definition.TokenDefinition;
import net.luis.utils.io.token.tokens.*;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link TokenConverter}.<br>
 *
 * @author Luis-St
 */
class TokenConverterTest {
	
	private static @NotNull Token createToken(@NotNull String value) {
		return SimpleToken.createUnpositioned(word -> word.equals(value), value);
	}
	
	private static @NotNull Token createPositionedToken(@NotNull String value, int line, int charPos) {
		TokenPosition start = new TokenPosition(line, 5, charPos);
		TokenPosition end = new TokenPosition(line, 5 + value.length() - 1, charPos + value.length() - 1);
		return new SimpleToken(word -> word.equals(value), value, start, end);
	}
	
	@Test
	void identityConverterReturnsUnmodifiedToken() {
		TokenConverter identity = token -> token;
		Token token = createToken("test");
		Token result = identity.convert(token);
		
		assertSame(token, result);
		assertEquals("test", result.value());
	}
	
	@Test
	void identityConverterWithDifferentTokenTypes() {
		TokenConverter identity = token -> token;
		
		Token simple = createToken("simple");
		Token positioned = createPositionedToken("positioned", 1, 20);
		AnnotatedToken annotated = AnnotatedToken.of(createToken("annotated"), "type", "test");
		IndexedToken indexed = new IndexedToken(createToken("indexed"), 3);
		
		assertSame(simple, identity.convert(simple));
		assertSame(positioned, identity.convert(positioned));
		assertSame(annotated, identity.convert(annotated));
		assertSame(indexed, identity.convert(indexed));
	}
	
	@Test
	void uppercaseConverterModifiesTokenValue() {
		TokenConverter uppercase = token -> createToken(token.value().toUpperCase());
		Token token = createToken("hello");
		Token result = uppercase.convert(token);
		
		assertEquals("HELLO", result.value());
		assertNotSame(token, result);
	}
	
	@Test
	void lowercaseConverterModifiesTokenValue() {
		TokenConverter lowercase = token -> createToken(token.value().toLowerCase());
		Token token = createToken("WORLD");
		Token result = lowercase.convert(token);
		
		assertEquals("world", result.value());
		assertNotSame(token, result);
	}
	
	@Test
	void prefixConverterAddsPrefix() {
		TokenConverter prefix = token -> createToken("PREFIX_" + token.value());
		Token token = createToken("test");
		Token result = prefix.convert(token);
		
		assertEquals("PREFIX_test", result.value());
	}
	
	@Test
	void suffixConverterAddsSuffix() {
		TokenConverter suffix = token -> createToken(token.value() + "_SUFFIX");
		Token token = createToken("base");
		Token result = suffix.convert(token);
		
		assertEquals("base_SUFFIX", result.value());
	}
	
	@Test
	void reverseConverterReversesTokenValue() {
		TokenConverter reverse = token -> createToken(new StringBuilder(token.value()).reverse().toString());
		Token token = createToken("hello");
		Token result = reverse.convert(token);
		
		assertEquals("olleh", result.value());
	}
	
	@Test
	void trimConverterTrimsWhitespace() {
		TokenConverter trim = token -> createToken(token.value().trim());
		Token token = createToken("  spaced  ");
		Token result = trim.convert(token);
		
		assertEquals("spaced", result.value());
	}
	
	@Test
	void replaceConverterReplacesSubstrings() {
		TokenConverter replace = token -> createToken(token.value().replace("old", "new"));
		Token token = createToken("oldValue");
		Token result = replace.convert(token);
		
		assertEquals("newValue", result.value());
	}
	
	@Test
	void conditionalConverterAppliesDifferentRules() {
		TokenConverter conditional = token -> {
			String value = token.value();
			if (value.matches("\\d+")) {
				return createToken("NUMBER_" + value);
			} else if (value.matches("[a-zA-Z]+")) {
				return createToken("WORD_" + value.toUpperCase());
			} else {
				return createToken("OTHER_" + value);
			}
		};
		
		Token number = createToken("123");
		Token word = createToken("hello");
		Token symbol = createToken("@#$");
		
		assertEquals("NUMBER_123", conditional.convert(number).value());
		assertEquals("WORD_HELLO", conditional.convert(word).value());
		assertEquals("OTHER_@#$", conditional.convert(symbol).value());
	}
	
	@Test
	void converterWithEmptyStringToken() {
		TokenConverter converter = token -> createToken("converted_" + token.value());
		Token emptyToken = createToken("");
		Token result = converter.convert(emptyToken);
		
		assertEquals("converted_", result.value());
	}
	
	@Test
	void converterWithSpecialCharacters() {
		TokenConverter escaper = token -> createToken(token.value().replace("\n", "\\n").replace("\t", "\\t"));
		Token newlineToken = createToken("line1\nline2");
		Token tabToken = createToken("col1\tcol2");
		
		assertEquals("line1\\nline2", escaper.convert(newlineToken).value());
		assertEquals("col1\\tcol2", escaper.convert(tabToken).value());
	}
	
	@Test
	void converterWithUnicodeCharacters() {
		TokenConverter converter = token -> createToken("🚀" + token.value() + "🚀");
		Token token = createToken("test");
		Token result = converter.convert(token);
		
		assertEquals("🚀test🚀", result.value());
	}
	
	@Test
	void lengthBasedConverter() {
		TokenConverter lengthBased = token -> {
			int length = token.value().length();
			return createToken("[" + length + "]" + token.value());
		};
		
		Token short1 = createToken("hi");
		Token long1 = createToken("hello");
		
		assertEquals("[2]hi", lengthBased.convert(short1).value());
		assertEquals("[5]hello", lengthBased.convert(long1).value());
	}
	
	@Test
	void converterThatPreservesOriginalToken() {
		TokenConverter wrapper = token -> AnnotatedToken.of(token, "converted", true);
		Token original = createToken("original");
		Token result = wrapper.convert(original);
		
		assertInstanceOf(AnnotatedToken.class, result);
		AnnotatedToken annotated = (AnnotatedToken) result;
		assertEquals(original, annotated.token());
		assertEquals(true, annotated.getMetadata("converted"));
	}
	
	@Test
	void converterThatCreatesIndexedToken() {
		TokenConverter indexer = token -> new IndexedToken(token, token.value().length());
		Token token = createToken("test");
		Token result = indexer.convert(token);
		
		assertInstanceOf(IndexedToken.class, result);
		IndexedToken indexed = (IndexedToken) result;
		assertEquals(token, indexed.token());
		assertEquals(4, indexed.index());
	}
	
	@Test
	void chainedConverters() {
		TokenConverter first = token -> createToken(token.value().toUpperCase());
		TokenConverter second = token -> createToken("PREFIX_" + token.value());
		TokenConverter chained = token -> second.convert(first.convert(token));
		
		Token token = createToken("test");
		Token result = chained.convert(token);
		
		assertEquals("PREFIX_TEST", result.value());
	}
	
	@Test
	void converterWithComplexLogic() {
		TokenConverter complex = token -> {
			String value = token.value();
			if (value.isEmpty()) {
				return createToken("<EMPTY>");
			} else if (value.length() == 1) {
				return createToken("CHAR_" + value.toUpperCase());
			} else if (value.matches("\\d+")) {
				return createToken("NUM_" + value);
			} else {
				return createToken("STR_" + value.toLowerCase());
			}
		};
		
		Token empty = createToken("");
		Token singleChar = createToken("a");
		Token number = createToken("123");
		Token text = createToken("Hello");
		
		assertEquals("<EMPTY>", complex.convert(empty).value());
		assertEquals("CHAR_A", complex.convert(singleChar).value());
		assertEquals("NUM_123", complex.convert(number).value());
		assertEquals("STR_hello", complex.convert(text).value());
	}
	
	@Test
	void converterThatModifiesPositions() {
		TokenConverter positionModifier = token -> {
			if (token.startPosition().isPositioned()) {
				TokenPosition newStart = new TokenPosition(
					token.startPosition().line() + 1,
					token.startPosition().characterInLine(),
					token.startPosition().character()
				);
				TokenPosition newEnd = new TokenPosition(
					token.endPosition().line() + 1,
					token.endPosition().characterInLine(),
					token.endPosition().character()
				);
				return new SimpleToken(token.definition(), token.value(), newStart, newEnd);
			}
			return token;
		};
		
		Token positioned = createPositionedToken("test", 0, 10);
		Token result = positionModifier.convert(positioned);
		
		assertEquals(1, result.startPosition().line());
		assertEquals(1, result.endPosition().line());
		assertEquals(5, result.startPosition().characterInLine());
		assertEquals(10, result.startPosition().character());
	}
	
	@Test
	void converterThatChangesDefinition() {
		TokenConverter definitionChanger = token -> {
			return SimpleToken.createUnpositioned(
				word -> word.startsWith("CONVERTED_"),
				"CONVERTED_" + token.value()
			);
		};
		
		Token token = createToken("test");
		Token result = definitionChanger.convert(token);
		
		assertEquals("CONVERTED_test", result.value());
		assertNotEquals(token.definition(), result.definition());
	}
	
	@Test
	void converterWithRegexReplacement() {
		TokenConverter regexReplacer = token -> createToken(token.value().replaceAll("\\d+", "NUM"));
		Token token = createToken("test123data456");
		Token result = regexReplacer.convert(token);
		
		assertEquals("testNUMdataNUM", result.value());
	}
	
	@Test
	void converterWithSpecialReplacements() {
		TokenConverter specialReplacer = token -> createToken(
			token.value()
				.replace("&", "&amp;")
				.replace("<", "&lt;")
				.replace(">", "&gt;")
		);
		
		Token token = createToken("a<b>c&d");
		Token result = specialReplacer.convert(token);
		
		assertEquals("a&lt;b&gt;c&amp;d", result.value());
	}
	
	@Test
	void converterThatHandlesAnnotatedTokens() {
		TokenConverter annotationPreserver = token -> {
			if (token instanceof AnnotatedToken annotated) {
				Token convertedBase = createToken(annotated.value().toUpperCase());
				return new AnnotatedToken(convertedBase, annotated.metadata());
			}
			return createToken(token.value().toUpperCase());
		};
		
		Token simple = createToken("simple");
		AnnotatedToken annotated = AnnotatedToken.of(createToken("annotated"), "type", "test");
		
		Token simpleResult = annotationPreserver.convert(simple);
		Token annotatedResult = annotationPreserver.convert(annotated);
		
		assertEquals("SIMPLE", simpleResult.value());
		assertInstanceOf(AnnotatedToken.class, annotatedResult);
		
		AnnotatedToken annotatedResultTyped = (AnnotatedToken) annotatedResult;
		assertEquals("ANNOTATED", annotatedResultTyped.value());
		assertEquals("test", annotatedResultTyped.getMetadata("type"));
	}
	
	@Test
	void converterThatHandlesIndexedTokens() {
		TokenConverter indexPreserver = token -> {
			if (token instanceof IndexedToken indexed) {
				Token convertedBase = createToken(indexed.value().toLowerCase());
				return new IndexedToken(convertedBase, indexed.index());
			}
			return createToken(token.value().toLowerCase());
		};
		
		Token simple = createToken("SIMPLE");
		IndexedToken indexed = new IndexedToken(createToken("INDEXED"), 5);
		
		Token simpleResult = indexPreserver.convert(simple);
		Token indexedResult = indexPreserver.convert(indexed);
		
		assertEquals("simple", simpleResult.value());
		assertInstanceOf(IndexedToken.class, indexedResult);
		
		IndexedToken indexedResultTyped = (IndexedToken) indexedResult;
		assertEquals("indexed", indexedResultTyped.value());
		assertEquals(5, indexedResultTyped.index());
	}
	
	@Test
	void converterWithNumericOperations() {
		TokenConverter numericConverter = token -> {
			String value = token.value();
			if (value.matches("\\d+")) {
				int number = Integer.parseInt(value);
				return createToken(String.valueOf(number * 2));
			}
			return token;
		};
		
		Token number = createToken("42");
		Token text = createToken("hello");
		
		assertEquals("84", numericConverter.convert(number).value());
		assertEquals("hello", numericConverter.convert(text).value());
	}
	
	@Test
	void converterWithStringManipulation() {
		TokenConverter stringManipulator = token -> {
			String value = token.value();
			if (value.length() > 5) {
				return createToken(value.substring(0, 5) + "...");
			} else if (value.length() < 3) {
				return createToken(value + "_PADDED");
			}
			return token;
		};
		
		Token longToken = createToken("verylongstring");
		Token shortToken = createToken("hi");
		Token mediumToken = createToken("medium");
		
		assertEquals("veryl...", stringManipulator.convert(longToken).value());
		assertEquals("hi_PADDED", stringManipulator.convert(shortToken).value());
		assertEquals("mediu...", stringManipulator.convert(mediumToken).value());
	}
	
	@Test
	void converterWithCaseBasedLogic() {
		TokenConverter caseBased = token -> {
			String value = token.value();
			if (value.equals(value.toUpperCase())) {
				return createToken(value.toLowerCase());
			} else if (value.equals(value.toLowerCase())) {
				return createToken(value.toUpperCase());
			} else {
				return createToken(value.toUpperCase());
			}
		};
		
		Token uppercase = createToken("UPPER");
		Token lowercase = createToken("lower");
		Token mixed = createToken("MiXeD");
		
		assertEquals("upper", caseBased.convert(uppercase).value());
		assertEquals("LOWER", caseBased.convert(lowercase).value());
		assertEquals("MIXED", caseBased.convert(mixed).value());
	}
	
	@Test
	void converterWithCharacterCounting() {
		TokenConverter charCounter = token -> {
			String value = token.value();
			long vowelCount = value.toLowerCase().chars()
				.filter(c -> "aeiou".indexOf(c) >= 0)
				.count();
			return createToken(value + "[" + vowelCount + "v]");
		};
		
		Token token = createToken("hello");
		Token result = charCounter.convert(token);
		
		assertEquals("hello[2v]", result.value());
	}
	
	@Test
	void converterWithHashCodeBasedModification() {
		TokenConverter hashBased = token -> {
			int hash = Math.abs(token.value().hashCode() % 100);
			return createToken(token.value() + "_" + hash);
		};
		
		Token token = createToken("test");
		Token result = hashBased.convert(token);
		
		assertTrue(result.value().startsWith("test_"));
		assertTrue(result.value().matches("test_\\d+"));
	}
	
	@Test
	void converterWithComplexTransformation() {
		TokenConverter complex = token -> {
			String value = token.value();
			StringBuilder transformed = new StringBuilder();
			
			for (char c : value.toCharArray()) {
				if (Character.isLetter(c)) {
					transformed.append(Character.toUpperCase(c));
				} else if (Character.isDigit(c)) {
					transformed.append('[').append(c).append(']');
				} else {
					transformed.append('_');
				}
			}
			
			return createToken(transformed.toString());
		};
		
		Token token = createToken("abc123!@#");
		Token result = complex.convert(token);
		
		assertEquals("ABC[1][2][3]___", result.value());
	}
	
	@Test
	void converterWithTokenWrapping() {
		TokenConverter wrapper = token -> {
			AnnotatedToken annotated = AnnotatedToken.of(token, "original", token.value());
			return new IndexedToken(annotated, token.value().length());
		};
		
		Token token = createToken("test");
		Token result = wrapper.convert(token);
		
		assertInstanceOf(IndexedToken.class, result);
		IndexedToken indexed = (IndexedToken) result;
		assertEquals(4, indexed.index());
		
		assertInstanceOf(AnnotatedToken.class, indexed.token());
		AnnotatedToken annotated = (AnnotatedToken) indexed.token();
		assertEquals("test", annotated.getMetadata("original"));
	}
	
	@Test
	void converterConsistency() {
		TokenConverter converter = token -> createToken(token.value().toUpperCase());
		Token token = createToken("consistent");
		
		Token result1 = converter.convert(token);
		Token result2 = converter.convert(token);
		
		assertEquals(result1.value(), result2.value());
		assertEquals("CONSISTENT", result1.value());
		assertEquals("CONSISTENT", result2.value());
	}
	
	@Test
	void converterWithPositionPreservation() {
		TokenConverter positionPreserver = token -> {
			if (token.startPosition().isPositioned()) {
				return new SimpleToken(
					TokenDefinition.of("test", true),
					token.value().toUpperCase(),
					token.startPosition(),
					token.endPosition()
				);
			}
			return createToken(token.value().toUpperCase());
		};
		
		Token positioned = createPositionedToken("test", 2, 25);
		Token unpositioned = createToken("test");
		
		Token positionedResult = positionPreserver.convert(positioned);
		Token unpositionedResult = positionPreserver.convert(unpositioned);
		
		assertEquals("TEST", positionedResult.value());
		assertEquals(2, positionedResult.startPosition().line());
		assertEquals(5, positionedResult.startPosition().characterInLine());
		assertEquals(25, positionedResult.startPosition().character());
		
		assertEquals("TEST", unpositionedResult.value());
		assertEquals(TokenPosition.UNPOSITIONED, unpositionedResult.startPosition());
	}
	
	@Test
	void converterWithMultipleApplications() {
		TokenConverter incrementer = token -> {
			if (token.value().matches("\\d+")) {
				int value = Integer.parseInt(token.value());
				return createToken(String.valueOf(value + 1));
			}
			return token;
		};
		
		Token number = createToken("5");
		Token result1 = incrementer.convert(number);
		Token result2 = incrementer.convert(result1);
		Token result3 = incrementer.convert(result2);
		
		assertEquals("6", result1.value());
		assertEquals("7", result2.value());
		assertEquals("8", result3.value());
	}
	
	@Test
	void converterWithErrorHandling() {
		TokenConverter safeConverter = token -> {
			try {
				String value = token.value();
				if (value.isEmpty()) {
					return createToken("<EMPTY>");
				}
				return createToken(value.substring(1));
			} catch (Exception e) {
				return createToken("<ERROR>");
			}
		};
		
		Token empty = createToken("");
		Token normal = createToken("normal");
		
		assertEquals("<EMPTY>", safeConverter.convert(empty).value());
		assertEquals("ormal", safeConverter.convert(normal).value());
	}
	
	@Test
	void converterFunctionalInterfaceProperty() {
		// Test that TokenConverter is indeed a functional interface
		TokenConverter lambda = token -> createToken(token.value() + "_lambda");
		Token token = createToken("test");
		Token result = lambda.convert(token);
		
		assertEquals("test_lambda", result.value());
	}
	
	@Test
	void converterMethodReference() {
		// Test using method reference
		TokenConverter methodRef = this::helperConverter;
		Token token = createToken("input");
		Token result = methodRef.convert(token);
		
		assertEquals("HELPER_INPUT", result.value());
	}
	
	private @NotNull Token helperConverter(@NotNull Token token) {
		return createToken("HELPER_" + token.value().toUpperCase());
	}
}
