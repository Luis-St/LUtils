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

package net.luis.utils.grammar.parser.action.filters;

import net.luis.utils.grammar.parser.TokenRuleMatch;
import net.luis.utils.grammar.parser.context.TokenActionContext;
import net.luis.utils.grammar.parser.rule.AlwaysMatchTokenRule;
import net.luis.utils.grammar.parser.stream.TokenStream;
import net.luis.utils.grammar.token.SimpleToken;
import net.luis.utils.grammar.token.Token;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link ExtractTokenAction}.<br>
 *
 * @author Luis-St
 */
class ExtractTokenActionTest {
	
	private static final TokenActionContext CONTEXT = new TokenActionContext(TokenStream.EMPTY);
	
	private static TokenRuleMatch matchOf(Token... tokens) {
		return new TokenRuleMatch(0, tokens.length, List.of(tokens), AlwaysMatchTokenRule.INSTANCE);
	}
	
	@Test
	void constructWithValidFilterAndExtractor() {
		Predicate<Token> filter = t -> true;
		Consumer<Token> extractor = t -> {};
		
		ExtractTokenAction action = new ExtractTokenAction(filter, extractor);
		
		assertNotNull(action.filter());
		assertNotNull(action.extractor());
		assertSame(filter, action.filter());
		assertSame(extractor, action.extractor());
	}
	
	@Test
	void constructWithNullFilter() {
		assertThrows(NullPointerException.class, () -> new ExtractTokenAction(null, t -> {}));
	}
	
	@Test
	void constructWithNullExtractor() {
		assertThrows(NullPointerException.class, () -> new ExtractTokenAction(t -> true, null));
	}
	
	@Test
	void applyWithNullMatchThrows() {
		ExtractTokenAction action = new ExtractTokenAction(t -> true, t -> {});
		
		assertThrows(NullPointerException.class, () -> action.apply(null, CONTEXT));
	}
	
	@Test
	void applyWithNullContextThrows() {
		ExtractTokenAction action = new ExtractTokenAction(t -> true, t -> {});
		TokenRuleMatch match = matchOf();
		
		assertThrows(NullPointerException.class, () -> action.apply(match, null));
	}
	
	@Test
	void applyWithEmptyMatchedTokensReturnsEmptyListAndNeverExtracts() {
		AtomicInteger invocations = new AtomicInteger(0);
		ExtractTokenAction action = new ExtractTokenAction(t -> true, t -> invocations.incrementAndGet());
		TokenRuleMatch match = matchOf();
		
		List<Token> result = action.apply(match, CONTEXT);
		
		assertTrue(result.isEmpty());
		assertEquals(0, invocations.get());
	}
	
	@Test
	void applyExtractsMatchingTokenAndInvokesConsumer() {
		Token token = SimpleToken.createUnpositioned("a");
		List<Token> extracted = new ArrayList<>();
		ExtractTokenAction action = new ExtractTokenAction(t -> true, extracted::add);
		TokenRuleMatch match = matchOf(token);
		
		List<Token> result = action.apply(match, CONTEXT);
		
		assertTrue(result.isEmpty());
		assertEquals(List.of(token), extracted);
	}
	
	@Test
	void applyKeepsNonMatchingTokenAndSkipsConsumer() {
		Token token = SimpleToken.createUnpositioned("a");
		AtomicInteger invocations = new AtomicInteger(0);
		ExtractTokenAction action = new ExtractTokenAction(t -> false, t -> invocations.incrementAndGet());
		TokenRuleMatch match = matchOf(token);
		
		List<Token> result = action.apply(match, CONTEXT);
		
		assertEquals(List.of(token), result);
		assertEquals(0, invocations.get());
	}
	
	@Test
	void applyWithMultipleTokensPartitionsByFilter() {
		Token a = SimpleToken.createUnpositioned("a");
		Token b = SimpleToken.createUnpositioned("b");
		Token c = SimpleToken.createUnpositioned("c");
		List<Token> extracted = new ArrayList<>();
		ExtractTokenAction action = new ExtractTokenAction(t -> t == b, extracted::add);
		TokenRuleMatch match = matchOf(a, b, c);
		
		List<Token> result = action.apply(match, CONTEXT);
		
		assertEquals(List.of(a, c), result);
		assertEquals(List.of(b), extracted);
	}
	
	@Test
	void applyReturnsUnmodifiableList() {
		Token token = SimpleToken.createUnpositioned("a");
		ExtractTokenAction action = new ExtractTokenAction(t -> false, t -> {});
		TokenRuleMatch match = matchOf(token);
		
		List<Token> result = action.apply(match, CONTEXT);
		
		assertThrows(UnsupportedOperationException.class, () -> result.add(token));
	}
	
	@Test
	void applyWithAllTokensMatchingExtractsEveryToken() {
		Token a = SimpleToken.createUnpositioned("a");
		Token b = SimpleToken.createUnpositioned("b");
		Token c = SimpleToken.createUnpositioned("c");
		List<Token> extracted = new ArrayList<>();
		ExtractTokenAction action = new ExtractTokenAction(t -> true, extracted::add);
		TokenRuleMatch match = matchOf(a, b, c);
		
		List<Token> result = action.apply(match, CONTEXT);
		
		assertTrue(result.isEmpty());
		assertEquals(List.of(a, b, c), extracted);
	}
	
	@Test
	void applyWithStatefulExtractorAccumulatesAcrossCalls() {
		Token a = SimpleToken.createUnpositioned("a");
		Token b = SimpleToken.createUnpositioned("b");
		Token c = SimpleToken.createUnpositioned("c");
		Token d = SimpleToken.createUnpositioned("d");
		List<Token> extracted = new ArrayList<>();
		ExtractTokenAction action = new ExtractTokenAction(t -> t == a || t == d, extracted::add);
		
		List<Token> firstResult = action.apply(matchOf(a, b), CONTEXT);
		List<Token> secondResult = action.apply(matchOf(c, d), CONTEXT);
		
		assertEquals(List.of(b), firstResult);
		assertEquals(List.of(c), secondResult);
		assertEquals(List.of(a, d), extracted);
	}
	
	@Test
	void applyWithNoTokensMatchingKeepsAllAndNeverExtracts() {
		Token a = SimpleToken.createUnpositioned("a");
		Token b = SimpleToken.createUnpositioned("b");
		Token c = SimpleToken.createUnpositioned("c");
		AtomicInteger invocations = new AtomicInteger(0);
		ExtractTokenAction action = new ExtractTokenAction(t -> false, t -> invocations.incrementAndGet());
		TokenRuleMatch match = matchOf(a, b, c);
		
		List<Token> result = action.apply(match, CONTEXT);
		
		assertEquals(List.of(a, b, c), result);
		assertEquals(0, invocations.get());
	}
}
