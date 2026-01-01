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

package net.luis.utils.io.token.rules;

import net.luis.utils.io.token.TokenPosition;
import net.luis.utils.io.token.TokenRuleMatch;
import net.luis.utils.io.token.context.TokenRuleContext;
import net.luis.utils.io.token.stream.TokenStream;
import net.luis.utils.io.token.tokens.*;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link NeverMatchTokenRule}.<br>
 *
 * @author Luis-St
 */
class NeverMatchTokenRuleTest {
	
	private static @NonNull Token createToken(@NonNull String value) {
		return SimpleToken.createUnpositioned(value);
	}
	
	@Test
	void instanceIsSingleton() {
		NeverMatchTokenRule instance1 = NeverMatchTokenRule.INSTANCE;
		NeverMatchTokenRule instance2 = NeverMatchTokenRule.INSTANCE;
		
		assertSame(instance1, instance2);
	}
	
	@Test
	void matchWithNullStream() {
		NeverMatchTokenRule rule = NeverMatchTokenRule.INSTANCE;
		TokenRuleContext context = TokenRuleContext.empty();
		
		assertThrows(NullPointerException.class, () -> rule.match(null, context));
	}
	
	@Test
	void matchWithNullContext() {
		NeverMatchTokenRule rule = NeverMatchTokenRule.INSTANCE;
		TokenStream stream = TokenStream.createMutable(List.of(createToken("test")));
		
		assertThrows(NullPointerException.class, () -> rule.match(stream, null));
	}
	
	@Test
	void matchWithNoTokensAvailable() {
		NeverMatchTokenRule rule = NeverMatchTokenRule.INSTANCE;
		TokenStream stream = TokenStream.createMutable(List.of());
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch match = rule.match(stream, context);
		
		assertNull(match);
	}
	
	@Test
	void matchWithTokensAvailable() {
		NeverMatchTokenRule rule = NeverMatchTokenRule.INSTANCE;
		TokenStream stream = TokenStream.createMutable(List.of(createToken("test")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch match = rule.match(stream, context);
		
		assertNull(match);
	}
	
	@Test
	void matchWithMultipleTokens() {
		NeverMatchTokenRule rule = NeverMatchTokenRule.INSTANCE;
		TokenStream stream = TokenStream.createMutable(List.of(createToken("first"), createToken("second")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch match = rule.match(stream, context);
		
		assertNull(match);
	}
	
	@Test
	void matchStreamPositionUnchanged() {
		NeverMatchTokenRule rule = NeverMatchTokenRule.INSTANCE;
		TokenStream stream = TokenStream.createMutable(List.of(createToken("first"), createToken("second")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		int initialIndex = stream.getCurrentIndex();
		TokenRuleMatch match = rule.match(stream, context);
		
		assertNull(match);
		assertEquals(initialIndex, stream.getCurrentIndex());
		assertEquals("first", stream.getCurrentToken().value());
	}
	
	@Test
	void matchWithSpecialCharacters() {
		NeverMatchTokenRule rule = NeverMatchTokenRule.INSTANCE;
		TokenStream stream = TokenStream.createMutable(List.of(createToken("!@#$%^&*()")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch match = rule.match(stream, context);
		
		assertNull(match);
		assertEquals(0, stream.getCurrentIndex());
	}
	
	@Test
	void matchWithEmptyStringToken() {
		NeverMatchTokenRule rule = NeverMatchTokenRule.INSTANCE;
		TokenStream stream = TokenStream.createMutable(List.of(createToken("")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch match = rule.match(stream, context);
		
		assertNull(match);
		assertEquals(0, stream.getCurrentIndex());
	}
	
	@Test
	void matchWithWhitespaceToken() {
		NeverMatchTokenRule rule = NeverMatchTokenRule.INSTANCE;
		TokenStream stream = TokenStream.createMutable(List.of(createToken("   ")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch match = rule.match(stream, context);
		
		assertNull(match);
		assertEquals(0, stream.getCurrentIndex());
	}
	
	@Test
	void matchWithNumericTokens() {
		NeverMatchTokenRule rule = NeverMatchTokenRule.INSTANCE;
		TokenStream stream = TokenStream.createMutable(List.of(createToken("123"), createToken("456")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch match = rule.match(stream, context);
		
		assertNull(match);
		assertEquals(0, stream.getCurrentIndex());
	}
	
	@Test
	void matchWithPositionedToken() {
		Token positionedToken = new SimpleToken("positioned", new TokenPosition(5, 10, 125));
		NeverMatchTokenRule rule = NeverMatchTokenRule.INSTANCE;
		TokenStream stream = TokenStream.createMutable(List.of(positionedToken));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch match = rule.match(stream, context);
		
		assertNull(match);
		assertEquals(0, stream.getCurrentIndex());
	}
	
	@Test
	void matchWithShadowTokens() {
		List<Token> tokens = List.of(
			new ShadowToken(createToken("shadow")),
			createToken("visible")
		);
		NeverMatchTokenRule rule = NeverMatchTokenRule.INSTANCE;
		TokenStream stream = TokenStream.createMutable(tokens);
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch match = rule.match(stream, context);
		
		assertNull(match);
	}
	
	@Test
	void matchWithOnlyShadowTokens() {
		List<Token> tokens = List.of(
			new ShadowToken(createToken("shadow1")),
			new ShadowToken(createToken("shadow2"))
		);
		NeverMatchTokenRule rule = NeverMatchTokenRule.INSTANCE;
		TokenStream stream = TokenStream.createMutable(tokens);
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch match = rule.match(stream, context);
		
		assertNull(match);
	}
	
	@Test
	void matchAtMiddleOfStream() {
		NeverMatchTokenRule rule = NeverMatchTokenRule.INSTANCE;
		TokenStream stream = TokenStream.createMutable(List.of(createToken("first"), createToken("second"), createToken("third")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		stream.advance();
		int midIndex = stream.getCurrentIndex();
		
		TokenRuleMatch match = rule.match(stream, context);
		
		assertNull(match);
		assertEquals(midIndex, stream.getCurrentIndex());
		assertEquals("second", stream.getCurrentToken().value());
	}
	
	@Test
	void matchWithLongTokenValue() {
		String longValue = "a".repeat(1000);
		NeverMatchTokenRule rule = NeverMatchTokenRule.INSTANCE;
		TokenStream stream = TokenStream.createMutable(List.of(createToken(longValue)));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch match = rule.match(stream, context);
		
		assertNull(match);
		assertEquals(0, stream.getCurrentIndex());
	}
	
	@Test
	void not() {
		NeverMatchTokenRule rule = NeverMatchTokenRule.INSTANCE;
		
		TokenRule negated = rule.not();
		
		assertEquals(AlwaysMatchTokenRule.INSTANCE, negated);
	}
}
