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

package net.luis.utils.io.token.rule.rules;

import net.luis.utils.io.token.TokenStream;
import net.luis.utils.io.token.rule.TokenRuleMatch;
import net.luis.utils.io.token.tokens.SimpleToken;
import net.luis.utils.io.token.tokens.Token;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link NeverMatchTokenRule}.<br>
 *
 * @author Luis-St
 */
class NeverMatchTokenRuleTest {
	
	private static @NotNull Token createToken(@NotNull String value) {
		return SimpleToken.createUnpositioned(word -> word.equals(value), value);
	}
	
	@Test
	void singletonInstanceExists() {
		assertNotNull(NeverMatchTokenRule.INSTANCE);
	}
	
	@Test
	void singletonInstanceIsConsistent() {
		NeverMatchTokenRule instance1 = NeverMatchTokenRule.INSTANCE;
		NeverMatchTokenRule instance2 = NeverMatchTokenRule.INSTANCE;
		
		assertSame(instance1, instance2);
	}
	
	@Test
	void matchWithNullTokenStream() {
		NeverMatchTokenRule rule = NeverMatchTokenRule.INSTANCE;
		
		assertThrows(NullPointerException.class, () -> rule.match(null));
	}
	
	@Test
	void matchWithEmptyTokenList() {
		NeverMatchTokenRule rule = NeverMatchTokenRule.INSTANCE;
		
		assertNull(rule.match(new TokenStream(Collections.emptyList())));
	}
	
	@Test
	void matchWithIndexOutOfBounds() {
		NeverMatchTokenRule rule = NeverMatchTokenRule.INSTANCE;
		List<Token> tokens = List.of(createToken("test"));
		
		assertThrows(IndexOutOfBoundsException.class, () -> rule.match(new TokenStream(tokens, 5)));
		assertThrows(IndexOutOfBoundsException.class, () -> rule.match(new TokenStream(tokens, -1)));
	}
	
	@Test
	void matchWithSingleToken() {
		NeverMatchTokenRule rule = NeverMatchTokenRule.INSTANCE;
		Token token = createToken("test");
		List<Token> tokens = List.of(token);
		
		assertNull(rule.match(new TokenStream(tokens, 0)));
	}
	
	@Test
	void matchWithMultipleTokens() {
		NeverMatchTokenRule rule = NeverMatchTokenRule.INSTANCE;
		Token first = createToken("first");
		Token second = createToken("second");
		Token third = createToken("third");
		List<Token> tokens = List.of(first, second, third);
		
		assertNull(rule.match(new TokenStream(tokens, 0)));
		assertNull(rule.match(new TokenStream(tokens, 1)));
		assertNull(rule.match(new TokenStream(tokens, 2)));
	}
	
	@Test
	void matchWithDifferentTokenTypes() {
		NeverMatchTokenRule rule = NeverMatchTokenRule.INSTANCE;
		Token text = createToken("text");
		Token number = createToken("123");
		Token symbol = createToken("!");
		Token empty = createToken("");
		List<Token> tokens = List.of(text, number, symbol, empty);
		
		for (int i = 0; i < tokens.size(); i++) {
			assertNull(rule.match(new TokenStream(tokens, i)));
		}
	}
	
	@Test
	void matchNeverReturnsMatch() {
		NeverMatchTokenRule rule = NeverMatchTokenRule.INSTANCE;
		Token token1 = createToken("anything");
		Token token2 = createToken("something");
		Token token3 = createToken("nothing");
		List<Token> tokens = List.of(token1, token2, token3);
		
		for (int i = 0; i < tokens.size(); i++) {
			TokenRuleMatch match = rule.match(new TokenStream(tokens, i));
			assertNull(match);
		}
	}
	
	@Test
	void matchWithLargeTokenList() {
		NeverMatchTokenRule rule = NeverMatchTokenRule.INSTANCE;
		List<Token> largeList = IntStream.range(0, 1000).mapToObj(i -> createToken("token" + i)).toList();
		
		for (int i = 0; i < largeList.size(); i += 100) {
			assertNull(rule.match(new TokenStream(largeList, i)));
		}
		
		assertNull(rule.match(new TokenStream(largeList, 0)));
		assertNull(rule.match(new TokenStream(largeList, 500)));
		assertNull(rule.match(new TokenStream(largeList, 999)));
	}
	
	@Test
	void matchWithSpecialCharacterTokens() {
		NeverMatchTokenRule rule = NeverMatchTokenRule.INSTANCE;
		Token space = createToken(" ");
		Token tab = createToken("\t");
		Token newline = createToken("\n");
		Token backslash = createToken("\\");
		Token quote = createToken("\"");
		List<Token> tokens = List.of(space, tab, newline, backslash, quote);
		
		for (int i = 0; i < tokens.size(); i++) {
			assertNull(rule.match(new TokenStream(tokens, i)));
		}
	}
	
	@Test
	void matchWithUnicodeTokens() {
		NeverMatchTokenRule rule = NeverMatchTokenRule.INSTANCE;
		Token unicode1 = createToken("hëllö");
		Token unicode2 = createToken("测试");
		Token emoji = createToken("😀");
		List<Token> tokens = List.of(unicode1, unicode2, emoji);
		
		for (int i = 0; i < tokens.size(); i++) {
			assertNull(rule.match(new TokenStream(tokens, i)));
		}
	}
	
	@Test
	void matchWithEmptyValueTokens() {
		NeverMatchTokenRule rule = NeverMatchTokenRule.INSTANCE;
		Token emptyToken = createToken("");
		List<Token> tokens = List.of(emptyToken);
		
		assertNull(rule.match(new TokenStream(tokens, 0)));
	}
	
	@Test
	void matchWithVeryLongTokens() {
		NeverMatchTokenRule rule = NeverMatchTokenRule.INSTANCE;
		Token longToken = createToken("a".repeat(10000));
		List<Token> tokens = List.of(longToken);
		
		assertNull(rule.match(new TokenStream(tokens, 0)));
	}
	
	@Test
	void matchResultsAreConsistent() {
		NeverMatchTokenRule rule = NeverMatchTokenRule.INSTANCE;
		Token token = createToken("consistent");
		List<Token> tokens = List.of(token);
		
		TokenRuleMatch match1 = rule.match(new TokenStream(tokens, 0));
		TokenRuleMatch match2 = rule.match(new TokenStream(tokens, 0));
		TokenRuleMatch match3 = rule.match(new TokenStream(tokens, 0));
		
		assertNull(match1);
		assertNull(match2);
		assertNull(match3);
	}
	
	@Test
	void matchWithSameTokenStreamMultipleTimes() {
		NeverMatchTokenRule rule = NeverMatchTokenRule.INSTANCE;
		Token token = createToken("test");
		List<Token> tokens = List.of(token);
		TokenStream stream = new TokenStream(tokens, 0);
		
		assertNull(rule.match(stream));
		assertNull(rule.match(stream));
		assertNull(rule.match(stream));
	}
	
	@Test
	void matchBehaviorIsIndependentOfTokenContent() {
		NeverMatchTokenRule rule = NeverMatchTokenRule.INSTANCE;
		
		String[] testValues = {
			"match", "test", "hello", "world",
			"123", "456", "0",
			"!", "@", "#", "$", "%",
			"true", "false", "null",
			"a", "A", "z", "Z",
			" ", "\t", "\n", "\r",
			"", "very long token value that spans multiple words"
		};
		
		for (String value : testValues) {
			Token token = createToken(value);
			List<Token> tokens = List.of(token);
			assertNull(rule.match(new TokenStream(tokens, 0)));
		}
	}
	
	@Test
	void matchWithAllValidIndicesInList() {
		NeverMatchTokenRule rule = NeverMatchTokenRule.INSTANCE;
		Token token1 = createToken("first");
		Token token2 = createToken("second");
		Token token3 = createToken("third");
		Token token4 = createToken("fourth");
		Token token5 = createToken("fifth");
		List<Token> tokens = List.of(token1, token2, token3, token4, token5);
		
		for (int i = 0; i < tokens.size(); i++) {
			assertNull(rule.match(new TokenStream(tokens, i)));
		}
	}
	
	@Test
	void matchDoesNotModifyTokenStream() {
		NeverMatchTokenRule rule = NeverMatchTokenRule.INSTANCE;
		Token token = createToken("test");
		List<Token> tokens = List.of(token);
		TokenStream stream = new TokenStream(tokens, 0);
		
		int initialIndex = stream.getCurrentIndex();
		boolean initialHasToken = stream.hasToken();
		
		rule.match(stream);
		
		assertEquals(initialIndex, stream.getCurrentIndex());
		assertEquals(initialHasToken, stream.hasToken());
	}
	
	@Test
	void equalInstancesHaveSameHashCode() {
		assertEquals(NeverMatchTokenRule.INSTANCE.hashCode(), NeverMatchTokenRule.INSTANCE.hashCode());
	}
	
	@Test
	void toStringContainsRuleInfo() {
		String ruleString = NeverMatchTokenRule.INSTANCE.toString();
		
		assertTrue(ruleString.contains("NeverMatchTokenRule"));
	}
	
	@Test
	void instanceIsThreadSafe() {
		NeverMatchTokenRule rule = NeverMatchTokenRule.INSTANCE;
		Token token = createToken("thread-test");
		List<Token> tokens = List.of(token);
		
		List<Thread> threads = IntStream.range(0, 10).mapToObj(i -> new Thread(() -> {
			for (int j = 0; j < 100; j++) {
				assertNull(rule.match(new TokenStream(tokens, 0)));
			}
		})).toList();
		
		threads.forEach(Thread::start);
		
		threads.forEach(thread -> {
			try {
				thread.join();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				fail("Thread was interrupted");
			}
		});
	}
}
