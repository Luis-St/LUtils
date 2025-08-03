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

package net.luis.utils.io.token.rule.actions;

import net.luis.utils.io.token.rule.TokenRuleMatch;
import net.luis.utils.io.token.rule.rules.TokenRules;
import net.luis.utils.io.token.tokens.*;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link AnnotateTokenAction}.<br>
 *
 * @author Luis-St
 */
class AnnotateTokenActionTest {
	
	private static @NotNull Token createToken(@NotNull String value) {
		return SimpleToken.createUnpositioned(word -> word.equals(value), value);
	}
	
	@Test
	void constructorWithNullMetadata() {
		assertThrows(NullPointerException.class, () -> new AnnotateTokenAction(null));
	}
	
	@Test
	void constructorWithValidMetadata() {
		Map<String, Object> metadata = Map.of("key", "value");
		
		assertDoesNotThrow(() -> new AnnotateTokenAction(metadata));
	}
	
	@Test
	void constructorWithEmptyMetadata() {
		Map<String, Object> emptyMetadata = Map.of();
		
		assertDoesNotThrow(() -> new AnnotateTokenAction(emptyMetadata));
	}
	
	@Test
	void metadataReturnsCorrectValue() {
		Map<String, Object> metadata = Map.of("type", "test", "priority", 1);
		AnnotateTokenAction action = new AnnotateTokenAction(metadata);
		
		assertEquals(metadata, action.metadata());
	}
	
	@Test
	void metadataIsImmutable() {
		Map<String, Object> metadata = Map.of("key", "value");
		AnnotateTokenAction action = new AnnotateTokenAction(metadata);
		
		assertThrows(UnsupportedOperationException.class, () -> action.metadata().put("new", "value"));
	}
	
	@Test
	void applyWithNullMatch() {
		AnnotateTokenAction action = new AnnotateTokenAction(Map.of("key", "value"));
		
		assertThrows(NullPointerException.class, () -> action.apply(null));
	}
	
	@Test
	void applyWithEmptyTokenList() {
		AnnotateTokenAction action = new AnnotateTokenAction(Map.of("key", "value"));
		TokenRuleMatch match = new TokenRuleMatch(0, 0, Collections.emptyList(), TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertTrue(result.isEmpty());
		assertThrows(UnsupportedOperationException.class, () -> result.add(createToken("test")));
	}
	
	@Test
	void applyWithSingleToken() {
		Map<String, Object> metadata = Map.of("type", "identifier", "line", 5);
		AnnotateTokenAction action = new AnnotateTokenAction(metadata);
		Token token = createToken("test");
		List<Token> tokens = List.of(token);
		TokenRuleMatch match = new TokenRuleMatch(0, 1, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertEquals(1, result.size());
		
		AnnotatedToken annotated = assertInstanceOf(AnnotatedToken.class, result.getFirst());
		assertEquals(token, annotated.token());
		assertEquals(metadata, annotated.metadata());
		assertEquals("test", annotated.value());
	}
	
	@Test
	void applyWithMultipleTokens() {
		Map<String, Object> metadata = Map.of("category", "keyword");
		AnnotateTokenAction action = new AnnotateTokenAction(metadata);
		Token token1 = createToken("if");
		Token token2 = createToken("else");
		List<Token> tokens = List.of(token1, token2);
		TokenRuleMatch match = new TokenRuleMatch(0, 2, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertEquals(2, result.size());
		
		AnnotatedToken annotated1 = assertInstanceOf(AnnotatedToken.class, result.getFirst());
		assertEquals(token1, annotated1.token());
		assertEquals(metadata, annotated1.metadata());
		
		AnnotatedToken annotated2 = assertInstanceOf(AnnotatedToken.class, result.get(1));
		assertEquals(token2, annotated2.token());
		assertEquals(metadata, annotated2.metadata());
	}
	
	@Test
	void applyWithAlreadyAnnotatedToken() {
		Map<String, Object> existingMetadata = Map.of("existing", "value");
		Token baseToken = createToken("test");
		AnnotatedToken annotatedToken = new AnnotatedToken(baseToken, existingMetadata);
		
		Map<String, Object> newMetadata = Map.of("new", "data");
		AnnotateTokenAction action = new AnnotateTokenAction(newMetadata);
		List<Token> tokens = List.of(annotatedToken);
		TokenRuleMatch match = new TokenRuleMatch(0, 1, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		assertEquals(1, result.size());
		
		AnnotatedToken mergedAnnotated = assertInstanceOf(AnnotatedToken.class, result.getFirst());
		assertEquals(baseToken, mergedAnnotated.token());
		
		Map<String, Object> expectedMetadata = Map.of("existing", "value", "new", "data");
		assertEquals(expectedMetadata, mergedAnnotated.metadata());
	}
	
	@Test
	void applyWithMetadataOverwrite() {
		Map<String, Object> existingMetadata = Map.of("key", "oldValue", "other", "data");
		Token baseToken = createToken("test");
		AnnotatedToken annotatedToken = new AnnotatedToken(baseToken, existingMetadata);
		
		Map<String, Object> newMetadata = Map.of("key", "newValue");
		AnnotateTokenAction action = new AnnotateTokenAction(newMetadata);
		List<Token> tokens = List.of(annotatedToken);
		TokenRuleMatch match = new TokenRuleMatch(0, 1, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		AnnotatedToken mergedAnnotated = assertInstanceOf(AnnotatedToken.class, result.getFirst());
		Map<String, Object> expectedMetadata = Map.of("key", "newValue", "other", "data");
		assertEquals(expectedMetadata, mergedAnnotated.metadata());
	}
	
	@Test
	void applyWithMixedTokenTypes() {
		Map<String, Object> metadata = Map.of("processed", true);
		AnnotateTokenAction action = new AnnotateTokenAction(metadata);
		
		Token simpleToken = createToken("simple");
		AnnotatedToken existingAnnotated = AnnotatedToken.of(createToken("existing"), "type", "test");
		
		List<Token> tokens = List.of(simpleToken, existingAnnotated);
		TokenRuleMatch match = new TokenRuleMatch(0, 2, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		assertEquals(2, result.size());
		
		AnnotatedToken result1 = assertInstanceOf(AnnotatedToken.class, result.getFirst());
		assertEquals(simpleToken, result1.token());
		assertEquals(Map.of("processed", true), result1.metadata());
		
		AnnotatedToken result2 = (AnnotatedToken) result.get(1);
		assertEquals(existingAnnotated.token(), result2.token());
		Map<String, Object> expectedMerged = Map.of("type", "test", "processed", true);
		assertEquals(expectedMerged, result2.metadata());
	}
	
	@Test
	void applyWithComplexMetadata() {
		Map<String, Object> complexMetadata = Map.of(
			"string", "value",
			"number", 42,
			"boolean", true,
			"list", List.of("a", "b", "c"),
			"nested", Map.of("inner", "value")
		);
		
		AnnotateTokenAction action = new AnnotateTokenAction(complexMetadata);
		Token token = createToken("test");
		List<Token> tokens = List.of(token);
		TokenRuleMatch match = new TokenRuleMatch(0, 1, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		AnnotatedToken annotated = assertInstanceOf(AnnotatedToken.class, result.getFirst());
		assertEquals(complexMetadata, annotated.metadata());
		assertEquals("value", annotated.getMetadata("string"));
		assertEquals(42, annotated.getMetadata("number"));
		assertEquals(true, annotated.getMetadata("boolean"));
	}
	
	@Test
	void applyWithEmptyMetadata() {
		AnnotateTokenAction action = new AnnotateTokenAction(Map.of());
		Token token = createToken("test");
		List<Token> tokens = List.of(token);
		TokenRuleMatch match = new TokenRuleMatch(0, 1, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		AnnotatedToken annotated = assertInstanceOf(AnnotatedToken.class, result.getFirst());
		assertTrue(annotated.metadata().isEmpty());
	}
	
	@Test
	void applyPreservesOriginalTokenProperties() {
		Map<String, Object> metadata = Map.of("tag", "test");
		AnnotateTokenAction action = new AnnotateTokenAction(metadata);
		Token originalToken = createToken("original");
		List<Token> tokens = List.of(originalToken);
		TokenRuleMatch match = new TokenRuleMatch(0, 1, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		AnnotatedToken annotated = assertInstanceOf(AnnotatedToken.class, result.getFirst());
		assertEquals(originalToken.definition(), annotated.definition());
		assertEquals(originalToken.value(), annotated.value());
		assertEquals(originalToken.startPosition(), annotated.startPosition());
		assertEquals(originalToken.endPosition(), annotated.endPosition());
	}
	
	@Test
	void applyResultIsUnmodifiable() {
		AnnotateTokenAction action = new AnnotateTokenAction(Map.of("key", "value"));
		Token token = createToken("test");
		List<Token> tokens = List.of(token);
		TokenRuleMatch match = new TokenRuleMatch(0, 1, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertThrows(UnsupportedOperationException.class, () -> result.add(createToken("new")));
		assertThrows(UnsupportedOperationException.class, result::removeFirst);
		assertThrows(UnsupportedOperationException.class, result::clear);
	}
	
	@Test
	void equalActionsHaveSameHashCode() {
		Map<String, Object> metadata = Map.of("key", "value");
		AnnotateTokenAction action1 = new AnnotateTokenAction(metadata);
		AnnotateTokenAction action2 = new AnnotateTokenAction(metadata);
		
		assertEquals(action1.hashCode(), action2.hashCode());
	}
	
	@Test
	void toStringContainsMetadata() {
		Map<String, Object> metadata = Map.of("type", "test");
		AnnotateTokenAction action = new AnnotateTokenAction(metadata);
		String actionString = action.toString();
		
		assertTrue(actionString.contains("AnnotateTokenAction"));
		assertTrue(actionString.contains("metadata"));
	}
}
