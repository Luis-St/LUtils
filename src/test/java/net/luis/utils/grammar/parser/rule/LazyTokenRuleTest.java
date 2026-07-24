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

import net.luis.utils.exception.NotInitializedException;
import net.luis.utils.grammar.parser.TokenRuleMatch;
import net.luis.utils.grammar.parser.context.TokenRuleContext;
import net.luis.utils.grammar.parser.stream.TokenStream;
import net.luis.utils.grammar.token.SimpleToken;
import net.luis.utils.grammar.token.Token;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link LazyTokenRule}.<br>
 *
 * @author Luis-St
 */
class LazyTokenRuleTest {
	
	private static TokenStream oneTokenStream() {
		Token token = SimpleToken.createUnpositioned("token");
		return TokenStream.createMutable(List.of(token));
	}
	
	@Test
	void constructUninitializedLazyRule() {
		LazyTokenRule lazyTokenRule = new LazyTokenRule();
		assertThrows(NotInitializedException.class, lazyTokenRule::get);
	}
	
	@Test
	void getBeforeInitializationThrows() {
		LazyTokenRule lazyTokenRule = new LazyTokenRule();
		assertThrows(NotInitializedException.class, lazyTokenRule::get);
	}
	
	@Test
	void setWithNullRule() {
		LazyTokenRule lazyTokenRule = new LazyTokenRule();
		assertThrows(NullPointerException.class, () -> lazyTokenRule.set(null));
	}
	
	@Test
	void matchWithNullStream() {
		LazyTokenRule lazyTokenRule = new LazyTokenRule();
		assertThrows(NullPointerException.class, () -> lazyTokenRule.match(null, TokenRuleContext.empty()));
	}
	
	@Test
	void matchWithNullContext() {
		LazyTokenRule lazyTokenRule = new LazyTokenRule();
		TokenStream stream = oneTokenStream();
		assertThrows(NullPointerException.class, () -> lazyTokenRule.match(stream, null));
	}
	
	@Test
	void matchBeforeInitializationReturnsNull() {
		LazyTokenRule lazyTokenRule = new LazyTokenRule();
		TokenStream stream = oneTokenStream();
		
		assertNull(lazyTokenRule.match(stream, TokenRuleContext.empty()));
		assertEquals(0, stream.getCurrentIndex());
	}
	
	@Test
	void matchAfterInitializationDelegatesToSetRule() {
		Token token = SimpleToken.createUnpositioned("token");
		LazyTokenRule lazyTokenRule = new LazyTokenRule();
		lazyTokenRule.set(AlwaysMatchTokenRule.INSTANCE);
		TokenRuleContext ctx = TokenRuleContext.empty();
		
		TokenRuleMatch actual = lazyTokenRule.match(TokenStream.createMutable(List.of(token)), ctx);
		TokenRuleMatch expected = AlwaysMatchTokenRule.INSTANCE.match(TokenStream.createMutable(List.of(token)), ctx);
		
		assertNotNull(actual);
		assertEquals(expected, actual);
	}
	
	@Test
	void matchAfterInitializationWithNonMatchingRule() {
		LazyTokenRule lazyTokenRule = new LazyTokenRule();
		lazyTokenRule.set(NeverMatchTokenRule.INSTANCE);
		
		assertNull(lazyTokenRule.match(oneTokenStream(), TokenRuleContext.empty()));
	}
	
	@Test
	void setOverwritesPreviouslySetRule() {
		LazyTokenRule lazyTokenRule = new LazyTokenRule();
		lazyTokenRule.set(NeverMatchTokenRule.INSTANCE);
		lazyTokenRule.set(AlwaysMatchTokenRule.INSTANCE);
		
		assertSame(AlwaysMatchTokenRule.INSTANCE, lazyTokenRule.get());
		assertNotNull(lazyTokenRule.match(oneTokenStream(), TokenRuleContext.empty()));
	}
	
	@Test
	void getIsMemoizedAcrossCalls() {
		LazyTokenRule lazyTokenRule = new LazyTokenRule();
		lazyTokenRule.set(AlwaysMatchTokenRule.INSTANCE);
		
		TokenRule first = lazyTokenRule.get();
		TokenRule second = lazyTokenRule.get();
		
		assertSame(first, second);
	}
	
	@Test
	void lazyTokenRuleGetterReturnsSupplier() {
		LazyTokenRule lazyTokenRule = new LazyTokenRule();
		lazyTokenRule.set(AlwaysMatchTokenRule.INSTANCE);
		
		assertSame(lazyTokenRule.get(), lazyTokenRule.lazyTokenRule().get());
	}
	
	@Test
	void notPreventsDoubleNegationNesting() {
		LazyTokenRule lazyTokenRule = new LazyTokenRule();
		lazyTokenRule.set(AlwaysMatchTokenRule.INSTANCE);
		
		assertSame(lazyTokenRule, lazyTokenRule.not().not());
	}
	
	@Test
	void notOnUninitializedRulePropagatesNotInitializedViaMatch() {
		LazyTokenRule lazyTokenRule = new LazyTokenRule();
		TokenRule negated = lazyTokenRule.not();
		
		assertNull(negated.match(oneTokenStream(), TokenRuleContext.empty()));
	}
	
	@Test
	void notOnInitializedRuleMatchesNegatedBehavior() {
		LazyTokenRule lazyTokenRule = new LazyTokenRule();
		lazyTokenRule.set(AlwaysMatchTokenRule.INSTANCE);
		TokenRule negated = lazyTokenRule.not();
		
		assertNull(negated.match(oneTokenStream(), TokenRuleContext.empty()));
	}
	
	@Test
	void equalsHashCodeAndToStringBehavior() {
		LazyTokenRule uninitialized = new LazyTokenRule();
		LazyTokenRule initialized = new LazyTokenRule();
		initialized.set(AlwaysMatchTokenRule.INSTANCE);
		
		assertNotEquals("not a lazy token rule", uninitialized);
		assertEquals(uninitialized, uninitialized);
		assertEquals(0, uninitialized.hashCode());
		assertEquals(0, initialized.hashCode());
		assertTrue(initialized.toString().contains("LazyTokenRule[lazyTokenRule="));
	}
}
