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

package net.luis.utils.grammar.parser.rule;

import net.luis.utils.grammar.parser.TokenRuleMatch;
import net.luis.utils.grammar.parser.context.TokenRuleContext;
import net.luis.utils.grammar.parser.stream.TokenStream;
import net.luis.utils.grammar.token.SimpleToken;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link NeverMatchTokenRule}.<br>
 *
 * @author Luis-St
 */
class NeverMatchTokenRuleTest {
	
	@Test
	void instanceIsNonNullAndStable() {
		assertNotNull(NeverMatchTokenRule.INSTANCE);
		assertSame(NeverMatchTokenRule.INSTANCE, NeverMatchTokenRule.INSTANCE);
	}
	
	@Test
	void matchWithNullStream() {
		assertThrows(NullPointerException.class, () -> NeverMatchTokenRule.INSTANCE.match(null, TokenRuleContext.empty()));
	}
	
	@Test
	void matchWithNullContext() {
		assertThrows(NullPointerException.class, () -> NeverMatchTokenRule.INSTANCE.match(TokenStream.createMutable(List.of()), null));
	}
	
	@Test
	void matchWithEmptyStreamReturnsNull() {
		TokenStream stream = TokenStream.createMutable(List.of());
		
		assertNull(NeverMatchTokenRule.INSTANCE.match(stream, TokenRuleContext.empty()));
	}
	
	@Test
	void matchWithNonEmptyStreamReturnsNull() {
		TokenStream stream = TokenStream.createMutable(List.of(SimpleToken.createUnpositioned("x")));
		
		assertNull(NeverMatchTokenRule.INSTANCE.match(stream, TokenRuleContext.empty()));
		assertEquals(0, stream.getCurrentIndex());
	}
	
	@Test
	void notReturnsAlwaysMatchInstance() {
		assertSame(AlwaysMatchTokenRule.INSTANCE, NeverMatchTokenRule.INSTANCE.not());
	}
	
	@Test
	void notThenMatchAlwaysMatchesNonEmptyStream() {
		TokenStream stream = TokenStream.createMutable(List.of(SimpleToken.createUnpositioned("x")));
		
		TokenRuleMatch match = NeverMatchTokenRule.INSTANCE.not().match(stream, TokenRuleContext.empty());
		
		assertNotNull(match);
		assertEquals(1, stream.getCurrentIndex());
	}
}
