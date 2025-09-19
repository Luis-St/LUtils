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

package net.luis.utils.io.token.rules;

import net.luis.utils.exception.NotInitializedException;
import net.luis.utils.io.token.TokenRuleMatch;
import net.luis.utils.io.token.context.TokenRuleContext;
import net.luis.utils.io.token.stream.TokenStream;
import net.luis.utils.io.token.tokens.SimpleToken;
import net.luis.utils.io.token.tokens.Token;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

class LazyTokenRuleTest {
	
	private static @NotNull Token createToken(@NotNull String value) {
		return SimpleToken.createUnpositioned(value);
	}
	
	private static @NotNull TokenRule createRule(@NotNull String value) {
		return new TokenRule() {
			@Override
			public @Nullable TokenRuleMatch match(@NotNull TokenStream stream, @NotNull TokenRuleContext ctx) {
				Objects.requireNonNull(stream, "Token stream must not be null");
				Objects.requireNonNull(ctx, "Token rule context must not be null");
				if (!stream.hasMoreTokens()) {
					return null;
				}
				
				int startIndex = stream.getCurrentIndex();
				Token token = stream.getCurrentToken();
				if (token.value().equals(value)) {
					return new TokenRuleMatch(startIndex, stream.advance(), List.of(token), this);
				}
				return null;
			}
		};
	}
	
	@Test
	void constructorDefault() {
		LazyTokenRule rule = new LazyTokenRule();
		
		assertNotNull(rule);
		assertNotNull(rule.lazyTokenRule());
	}
	
	@Test
	void setWithNullRule() {
		LazyTokenRule rule = new LazyTokenRule();
		
		assertThrows(NullPointerException.class, () -> rule.set(null));
	}
	
	@Test
	void setWithValidRule() {
		LazyTokenRule rule = new LazyTokenRule();
		TokenRule targetRule = createRule("test");
		
		rule.set(targetRule);
		
		assertEquals(targetRule, rule.get());
	}
	
	@Test
	void setOverwritesPreviousRule() {
		LazyTokenRule rule = new LazyTokenRule();
		TokenRule rule1 = createRule("first");
		TokenRule rule2 = createRule("second");
		
		rule.set(rule1);
		rule.set(rule2);
		
		assertEquals(rule2, rule.get());
	}
	
	@Test
	void getWithoutInitialization() {
		LazyTokenRule rule = new LazyTokenRule();
		
		assertThrows(NotInitializedException.class, rule::get);
	}
	
	@Test
	void lazyTokenRuleSupplier() {
		LazyTokenRule rule = new LazyTokenRule();
		
		Supplier<TokenRule> supplier = rule.lazyTokenRule();
		
		assertNotNull(supplier);
		assertThrows(NotInitializedException.class, supplier::get);
	}
	
	@Test
	void lazyTokenRuleSupplierAfterSet() {
		LazyTokenRule rule = new LazyTokenRule();
		TokenRule targetRule = createRule("test");
		rule.set(targetRule);
		
		Supplier<TokenRule> supplier = rule.lazyTokenRule();
		
		assertEquals(targetRule, supplier.get());
	}
	
	@Test
	void matchWithNullStream() {
		LazyTokenRule rule = new LazyTokenRule();
		TokenRuleContext context = TokenRuleContext.empty();
		
		assertThrows(NullPointerException.class, () -> rule.match(null, context));
	}
	
	@Test
	void matchWithNullContext() {
		LazyTokenRule rule = new LazyTokenRule();
		TokenStream stream = TokenStream.createMutable(List.of(createToken("test")));
		
		assertThrows(NullPointerException.class, () -> rule.match(stream, null));
	}
	
	@Test
	void matchWithoutInitialization() {
		LazyTokenRule rule = new LazyTokenRule();
		TokenStream stream = TokenStream.createMutable(List.of(createToken("test")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNull(result);
	}
	
	@Test
	void matchWithInitializedRule() {
		LazyTokenRule rule = new LazyTokenRule();
		TokenRule targetRule = createRule("test");
		rule.set(targetRule);
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("test")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals("test", result.matchedTokens().get(0).value());
	}
	
	@Test
	void matchWithInitializedRuleNoMatch() {
		LazyTokenRule rule = new LazyTokenRule();
		TokenRule targetRule = createRule("expected");
		rule.set(targetRule);
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("actual")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNull(result);
	}
	
	@Test
	void not() {
		LazyTokenRule rule = new LazyTokenRule();
		
		TokenRule negated = rule.not();
		
		assertNotNull(negated);
		assertTrue(negated instanceof LazyTokenRule);
		assertNotEquals(rule, negated);
	}
	
	@Test
	void notWithDoubleNegation() {
		LazyTokenRule rule = new LazyTokenRule();
		
		TokenRule doubleNegated = rule.not().not();
		
		assertEquals(rule, doubleNegated);
	}
	
	@Test
	void notWithInitializedRule() {
		LazyTokenRule rule = new LazyTokenRule();
		TokenRule targetRule = createRule("test");
		rule.set(targetRule);
		
		TokenRule negated = rule.not();
		
		assertNotNull(negated);
		assertTrue(negated instanceof LazyTokenRule);
	}
	
	@Test
	void equalsWithSameInstance() {
		LazyTokenRule rule = new LazyTokenRule();
		
		assertEquals(rule, rule);
	}
	
	@Test
	void equalsWithDifferentInstances() {
		LazyTokenRule rule1 = new LazyTokenRule();
		LazyTokenRule rule2 = new LazyTokenRule();
		
		assertNotEquals(rule1, rule2);
	}
	
	@Test
	void equalsWithNull() {
		LazyTokenRule rule = new LazyTokenRule();
		
		assertNotEquals(rule, null);
	}
	
	@Test
	void equalsWithDifferentType() {
		LazyTokenRule rule = new LazyTokenRule();
		
		assertNotEquals(rule, "string");
	}
	
	@Test
	void hashCodeConsistency() {
		LazyTokenRule rule = new LazyTokenRule();
		
		int hashCode1 = rule.hashCode();
		int hashCode2 = rule.hashCode();
		
		assertEquals(hashCode1, hashCode2);
	}
	
	@Test
	void hashCodeAlwaysZero() {
		LazyTokenRule rule1 = new LazyTokenRule();
		LazyTokenRule rule2 = new LazyTokenRule();
		
		assertEquals(0, rule1.hashCode());
		assertEquals(0, rule2.hashCode());
	}
	
	@Test
	void toStringTest() {
		LazyTokenRule rule = new LazyTokenRule();
		
		String result = rule.toString();
		
		assertTrue(result.contains("LazyTokenRule"));
		assertTrue(result.contains("lazyTokenRule"));
	}
}
