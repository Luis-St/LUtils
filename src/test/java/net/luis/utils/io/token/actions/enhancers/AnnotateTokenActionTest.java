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

package net.luis.utils.io.token.actions.enhancers;

import net.luis.utils.io.token.TokenRuleMatch;
import net.luis.utils.io.token.context.TokenActionContext;
import net.luis.utils.io.token.rules.AlwaysMatchTokenRule;
import net.luis.utils.io.token.stream.TokenStream;
import net.luis.utils.io.token.tokens.*;
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
	
	private static @NonNull Token createToken(@NonNull String value) {
		return SimpleToken.createUnpositioned(value);
	}
	
	@Test
	void constructorWithValidMetadata() {
		Map<String, Object> metadata = Map.of("key", "value");
		
		AnnotateTokenAction action = new AnnotateTokenAction(metadata);
		
		assertEquals(metadata, action.metadata());
	}
	
	@Test
	void constructorWithNullMetadata() {
		assertThrows(NullPointerException.class, () -> new AnnotateTokenAction(null));
	}
	
	@Test
	void constructorWithEmptyMetadata() {
		Map<String, Object> metadata = Map.of();
		
		AnnotateTokenAction action = new AnnotateTokenAction(metadata);
		
		assertTrue(action.metadata().isEmpty());
	}
	
	@Test
	void constructorCopiesMetadata() {
		Map<String, Object> metadata = new HashMap<>();
		metadata.put("key", "value");
		
		AnnotateTokenAction action = new AnnotateTokenAction(metadata);
		metadata.put("new", "added");
		
		assertEquals(1, action.metadata().size());
		assertEquals("value", action.metadata().get("key"));
		assertFalse(action.metadata().containsKey("new"));
	}
	
	@Test
	void applyWithNullMatch() {
		Map<String, Object> metadata = Map.of("key", "value");
		AnnotateTokenAction action = new AnnotateTokenAction(metadata);
		TokenActionContext context = new TokenActionContext(TokenStream.createImmutable(List.of()));
		
		assertThrows(NullPointerException.class, () -> action.apply(null, context));
	}
	
	@Test
	void applyWithNullContext() {
		Map<String, Object> metadata = Map.of("key", "value");
		AnnotateTokenAction action = new AnnotateTokenAction(metadata);
		
		List<Token> tokens = List.of(createToken("test"));
		TokenRuleMatch match = new TokenRuleMatch(0, 1, tokens, AlwaysMatchTokenRule.INSTANCE);
		
		assertThrows(NullPointerException.class, () -> action.apply(match, null));
	}
	
	@Test
	void applyWithSimpleTokens() {
		Map<String, Object> metadata = Map.of("type", "test", "level", 1);
		AnnotateTokenAction action = new AnnotateTokenAction(metadata);
		
		List<Token> tokens = List.of(createToken("hello"), createToken("world"));
		TokenRuleMatch match = new TokenRuleMatch(0, 2, tokens, AlwaysMatchTokenRule.INSTANCE);
		TokenActionContext context = new TokenActionContext(TokenStream.createImmutable(tokens));
		
		List<Token> result = action.apply(match, context);
		assertEquals(2, result.size());
		
		AnnotatedToken annotated1 = assertInstanceOf(AnnotatedToken.class, result.get(0));
		AnnotatedToken annotated2 = assertInstanceOf(AnnotatedToken.class, result.get(1));
		assertEquals("hello", annotated1.value());
		assertEquals("world", annotated2.value());
		assertEquals("test", annotated1.getMetadata("type"));
		assertEquals(1, annotated1.getMetadata("level"));
		assertEquals("test", annotated2.getMetadata("type"));
		assertEquals(1, annotated2.getMetadata("level"));
	}
	
	@Test
	void applyWithAlreadyAnnotatedTokens() {
		Map<String, Object> existingMetadata = Map.of("existing", "old");
		AnnotatedToken annotatedToken = new AnnotatedToken(createToken("test"), existingMetadata);
		
		Map<String, Object> newMetadata = Map.of("new", "value", "existing", "updated");
		AnnotateTokenAction action = new AnnotateTokenAction(newMetadata);
		
		List<Token> tokens = List.of(annotatedToken);
		TokenRuleMatch match = new TokenRuleMatch(0, 1, tokens, AlwaysMatchTokenRule.INSTANCE);
		TokenActionContext context = new TokenActionContext(TokenStream.createImmutable(tokens));
		
		List<Token> result = action.apply(match, context);
		assertEquals(1, result.size());
		
		AnnotatedToken merged = assertInstanceOf(AnnotatedToken.class, result.getFirst());
		assertEquals("test", merged.value());
		assertEquals("updated", merged.getMetadata("existing"));
		assertEquals("value", merged.getMetadata("new"));
	}
	
	@Test
	void applyWithEmptyTokens() {
		Map<String, Object> metadata = Map.of("key", "value");
		AnnotateTokenAction action = new AnnotateTokenAction(metadata);
		
		List<Token> tokens = List.of();
		TokenRuleMatch match = new TokenRuleMatch(0, 0, tokens, AlwaysMatchTokenRule.INSTANCE);
		TokenActionContext context = new TokenActionContext(TokenStream.createImmutable(List.of(createToken("dummy"))));
		
		List<Token> result = action.apply(match, context);
		
		assertTrue(result.isEmpty());
	}
	
	@Test
	void applyReturnsImmutableList() {
		Map<String, Object> metadata = Map.of("key", "value");
		AnnotateTokenAction action = new AnnotateTokenAction(metadata);
		
		List<Token> tokens = List.of(createToken("test"));
		TokenRuleMatch match = new TokenRuleMatch(0, 1, tokens, AlwaysMatchTokenRule.INSTANCE);
		TokenActionContext context = new TokenActionContext(TokenStream.createImmutable(tokens));
		
		List<Token> result = action.apply(match, context);
		
		assertThrows(UnsupportedOperationException.class, () -> result.add(createToken("new")));
	}
	
	@Test
	void applyWithComplexMetadata() {
		Map<String, Object> metadata = Map.of(
			"string", "value",
			"integer", 42,
			"boolean", true,
			"list", List.of("a", "b", "c")
		);
		AnnotateTokenAction action = new AnnotateTokenAction(metadata);
		
		List<Token> tokens = List.of(createToken("test"));
		TokenRuleMatch match = new TokenRuleMatch(0, 1, tokens, AlwaysMatchTokenRule.INSTANCE);
		TokenActionContext context = new TokenActionContext(TokenStream.createImmutable(tokens));
		
		List<Token> result = action.apply(match, context);
		AnnotatedToken annotated = (AnnotatedToken) result.getFirst();
		
		assertEquals("value", annotated.getMetadata("string"));
		assertEquals(42, annotated.getMetadata("integer"));
		assertEquals(true, annotated.getMetadata("boolean"));
		assertEquals(List.of("a", "b", "c"), annotated.getMetadata("list"));
	}
	
	@Test
	void toStringTest() {
		Map<String, Object> metadata = Map.of("key", "value");
		AnnotateTokenAction action = new AnnotateTokenAction(metadata);
		
		String result = action.toString();
		
		assertNotNull(result);
		assertTrue(result.contains("AnnotateTokenAction"));
		assertTrue(result.contains("metadata"));
	}
	
	@Test
	void equalsAndHashCodeTest() {
		Map<String, Object> metadata1 = Map.of("key", "value");
		Map<String, Object> metadata2 = Map.of("key", "value");
		Map<String, Object> metadata3 = Map.of("other", "value");
		
		AnnotateTokenAction action1 = new AnnotateTokenAction(metadata1);
		AnnotateTokenAction action2 = new AnnotateTokenAction(metadata2);
		AnnotateTokenAction action3 = new AnnotateTokenAction(metadata3);
		
		assertEquals(action1, action2);
		assertNotEquals(action1, action3);
		assertEquals(action1.hashCode(), action2.hashCode());
	}
}
