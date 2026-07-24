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

package net.luis.utils.grammar.parser.action.enhancers;

import net.luis.utils.grammar.parser.TokenRuleMatch;
import net.luis.utils.grammar.parser.context.TokenActionContext;
import net.luis.utils.grammar.parser.rule.TokenRules;
import net.luis.utils.grammar.parser.stream.TokenStream;
import net.luis.utils.grammar.token.*;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link AnnotateTokenAction}.<br>
 *
 * @author Luis-St
 */
class AnnotateTokenActionTest {
	
	private static final TokenActionContext CONTEXT = new TokenActionContext(TokenStream.createImmutable(List.of()));
	
	private static @NonNull TokenRuleMatch matchOf(Token... tokens) {
		List<Token> list = List.of(tokens);
		return new TokenRuleMatch(0, list.size(), list, TokenRules.alwaysMatch());
	}
	
	private static @NonNull SimpleToken plainToken(String value) {
		return new SimpleToken(value, TokenPosition.UNPOSITIONED);
	}
	
	@Test
	void constructWithValidMetadata() {
		AnnotateTokenAction action = new AnnotateTokenAction(Map.of("key", "value"));
		assertEquals(Map.of("key", "value"), action.metadata());
	}
	
	@Test
	void constructWithNullMetadata() {
		assertThrows(NullPointerException.class, () -> new AnnotateTokenAction(null));
	}
	
	@Test
	void applyWithNullMatchThrows() {
		AnnotateTokenAction action = new AnnotateTokenAction(Map.of("key", "value"));
		assertThrows(NullPointerException.class, () -> action.apply(null, CONTEXT));
	}
	
	@Test
	void applyWithNullContextThrows() {
		AnnotateTokenAction action = new AnnotateTokenAction(Map.of("key", "value"));
		TokenRuleMatch match = matchOf(plainToken("a"));
		assertThrows(NullPointerException.class, () -> action.apply(match, null));
	}
	
	@Test
	void constructWithMetadataContainingNullValueThrows() {
		Map<String, Object> mapWithNullValue = new HashMap<>();
		mapWithNullValue.put("key", null);
		assertThrows(NullPointerException.class, () -> new AnnotateTokenAction(mapWithNullValue));
	}
	
	@Test
	void constructWithMetadataContainingNullKeyThrows() {
		Map<String, Object> mapWithNullKey = new HashMap<>();
		mapWithNullKey.put(null, "value");
		assertThrows(NullPointerException.class, () -> new AnnotateTokenAction(mapWithNullKey));
	}
	
	@Test
	void applyWithEmptyMatchedTokensReturnsEmptyList() {
		AnnotateTokenAction action = new AnnotateTokenAction(Map.of("key", "value"));
		TokenRuleMatch match = matchOf();
		
		List<Token> result = action.apply(match, CONTEXT);
		
		assertTrue(result.isEmpty());
	}
	
	@Test
	void applyWithPlainTokenWrapsInAnnotatedToken() {
		AnnotateTokenAction action = new AnnotateTokenAction(Map.of("k", "v"));
		SimpleToken plain = plainToken("value");
		TokenRuleMatch match = matchOf(plain);
		
		List<Token> result = action.apply(match, CONTEXT);
		
		assertEquals(1, result.size());
		assertInstanceOf(AnnotatedToken.class, result.get(0));
		AnnotatedToken annotated = (AnnotatedToken) result.get(0);
		assertEquals(Map.of("k", "v"), annotated.metadata());
		assertSame(plain, annotated.token());
	}
	
	@Test
	void applyWithAlreadyAnnotatedTokenMergesMetadata() {
		AnnotateTokenAction action = new AnnotateTokenAction(Map.of("shared", "new", "added", "value"));
		AnnotatedToken existing = new AnnotatedToken(plainToken("value"), Map.of("existing", "old", "shared", "old"));
		TokenRuleMatch match = matchOf(existing);
		
		List<Token> result = action.apply(match, CONTEXT);
		
		AnnotatedToken merged = (AnnotatedToken) result.get(0);
		assertEquals(3, merged.metadata().size());
		assertEquals("old", merged.metadata().get("existing"));
		assertEquals("new", merged.metadata().get("shared"));
		assertEquals("value", merged.metadata().get("added"));
	}
	
	@Test
	void applyWithMultiplePlainTokensAnnotatesEach() {
		AnnotateTokenAction action = new AnnotateTokenAction(Map.of("k", "v"));
		SimpleToken first = plainToken("a");
		SimpleToken second = plainToken("b");
		SimpleToken third = plainToken("c");
		TokenRuleMatch match = matchOf(first, second, third);
		
		List<Token> result = action.apply(match, CONTEXT);
		
		assertEquals(3, result.size());
		assertSame(first, ((AnnotatedToken) result.get(0)).token());
		assertSame(second, ((AnnotatedToken) result.get(1)).token());
		assertSame(third, ((AnnotatedToken) result.get(2)).token());
	}
	
	@Test
	void applyWithEmptyMetadataStillWrapsTokens() {
		AnnotateTokenAction action = new AnnotateTokenAction(Map.of());
		TokenRuleMatch match = matchOf(plainToken("value"));
		
		List<Token> result = action.apply(match, CONTEXT);
		
		AnnotatedToken annotated = (AnnotatedToken) result.get(0);
		assertTrue(annotated.metadata().isEmpty());
	}
	
	@Test
	void constructDefensivelyCopiesMutableMetadata() {
		HashMap<String, Object> source = new HashMap<>(Map.of("key", "value"));
		AnnotateTokenAction action = new AnnotateTokenAction(source);
		
		source.put("extra", "mutated");
		
		assertEquals(Map.of("key", "value"), action.metadata());
	}
	
	@Test
	void metadataAccessorReturnsUnmodifiableMap() {
		AnnotateTokenAction action = new AnnotateTokenAction(Map.of("key", "value"));
		assertThrows(UnsupportedOperationException.class, () -> action.metadata().put("other", "value"));
	}
	
	@Test
	void applyWithMixOfPlainAndAnnotatedTokens() {
		AnnotateTokenAction action = new AnnotateTokenAction(Map.of("tag", "x"));
		SimpleToken firstPlain = plainToken("a");
		AnnotatedToken annotatedMiddle = new AnnotatedToken(plainToken("b"), Map.of("own", "y"));
		SimpleToken lastPlain = plainToken("c");
		TokenRuleMatch match = matchOf(firstPlain, annotatedMiddle, lastPlain);
		
		List<Token> result = action.apply(match, CONTEXT);
		
		AnnotatedToken first = (AnnotatedToken) result.get(0);
		AnnotatedToken middle = (AnnotatedToken) result.get(1);
		AnnotatedToken last = (AnnotatedToken) result.get(2);
		assertEquals(Map.of("tag", "x"), first.metadata());
		assertEquals(Map.of("own", "y", "tag", "x"), middle.metadata());
		assertEquals(Map.of("tag", "x"), last.metadata());
	}
	
	@Test
	void applyReturnsUnmodifiableList() {
		AnnotateTokenAction action = new AnnotateTokenAction(Map.of("key", "value"));
		TokenRuleMatch match = matchOf(plainToken("value"));
		
		List<Token> result = action.apply(match, CONTEXT);
		
		assertThrows(UnsupportedOperationException.class, () -> result.add(plainToken("other")));
	}
}
