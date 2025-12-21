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

package net.luis.utils.io.token.actions;

import net.luis.utils.io.token.actions.core.*;
import net.luis.utils.io.token.actions.enhancers.AnnotateTokenAction;
import net.luis.utils.io.token.actions.enhancers.IndexTokenAction;
import net.luis.utils.io.token.actions.filters.*;
import net.luis.utils.io.token.actions.transformers.*;
import net.luis.utils.io.token.tokens.SimpleToken;
import net.luis.utils.io.token.tokens.Token;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link TokenActions}.<br>
 *
 * @author Luis-St
 */
class TokenActionsTest {
	
	private static @NonNull Token createToken(@NonNull String value) {
		return SimpleToken.createUnpositioned(value);
	}
	
	private static @NonNull Predicate<Token> createTestPredicate() {
		return token -> "test".equals(token.value());
	}
	
	private static @NonNull Consumer<Token> createTestConsumer() {
		return token -> {};
	}
	
	@Test
	void identity() {
		TokenAction result = TokenActions.identity();
		
		assertEquals(TokenAction.identity().getClass(), result.getClass());
	}
	
	@Test
	void groupingWithNullMode() {
		assertThrows(NullPointerException.class, () -> TokenActions.grouping(null));
	}
	
	@Test
	void groupingWithMatchedMode() {
		TokenAction result = TokenActions.grouping(GroupingMode.MATCHED);
		
		assertInstanceOf(GroupingTokenAction.class, result);
		assertEquals(GroupingMode.MATCHED, ((GroupingTokenAction) result).mode());
	}
	
	@Test
	void groupingWithAllMode() {
		TokenAction result = TokenActions.grouping(GroupingMode.ALL);
		
		assertInstanceOf(GroupingTokenAction.class, result);
		assertEquals(GroupingMode.ALL, ((GroupingTokenAction) result).mode());
	}
	
	@Test
	void annotateWithNullMetadata() {
		assertThrows(NullPointerException.class, () -> TokenActions.annotate(null));
	}
	
	@Test
	void annotate() {
		Map<String, Object> metadata = Map.of("key1", "value1", "key2", 42);
		
		TokenAction result = TokenActions.annotate(metadata);
		
		AnnotateTokenAction annotateAction = assertInstanceOf(AnnotateTokenAction.class, result);
		assertEquals(metadata, annotateAction.metadata());
	}
	
	@Test
	void indexDefault() {
		TokenAction result = TokenActions.index();
		
		IndexTokenAction indexAction = assertInstanceOf(IndexTokenAction.class, result);
		assertEquals(0, indexAction.startIndex());
	}
	
	@Test
	void indexWithNegativeStartIndex() {
		assertThrows(IllegalArgumentException.class, () -> TokenActions.index(-1));
	}
	
	@Test
	void indexWithStartIndex() {
		TokenAction result = TokenActions.index(5);
		
		IndexTokenAction indexAction = assertInstanceOf(IndexTokenAction.class, result);
		assertEquals(5, indexAction.startIndex());
	}
	
	@Test
	void extractWithNullFilter() {
		Consumer<Token> extractor = createTestConsumer();
		assertThrows(NullPointerException.class, () -> TokenActions.extract(null, extractor));
	}
	
	@Test
	void extractWithNullExtractor() {
		Predicate<Token> filter = createTestPredicate();
		assertThrows(NullPointerException.class, () -> TokenActions.extract(filter, null));
	}
	
	@Test
	void extract() {
		Predicate<Token> filter = createTestPredicate();
		Consumer<Token> extractor = createTestConsumer();
		
		TokenAction result = TokenActions.extract(filter, extractor);
		
		ExtractTokenAction extractAction = assertInstanceOf(ExtractTokenAction.class, result);
		assertEquals(filter, extractAction.filter());
		assertEquals(extractor, extractAction.extractor());
	}
	
	@Test
	void filterWithNullFilter() {
		assertThrows(NullPointerException.class, () -> TokenActions.filter(null));
	}
	
	@Test
	void filter() {
		Predicate<Token> filter = createTestPredicate();
		
		TokenAction result = TokenActions.filter(filter);
		
		FilterTokenAction filterAction = assertInstanceOf(FilterTokenAction.class, result);
		assertEquals(filter, filterAction.filter());
	}
	
	@Test
	void skipWithNullFilter() {
		assertThrows(NullPointerException.class, () -> TokenActions.skip(null));
	}
	
	@Test
	void skip() {
		Predicate<Token> filter = createTestPredicate();
		
		TokenAction result = TokenActions.skip(filter);
		
		SkipTokenAction skipAction = assertInstanceOf(SkipTokenAction.class, result);
		assertEquals(filter, skipAction.filter());
	}
	
	@Test
	void convertWithNullConverter() {
		assertThrows(NullPointerException.class, () -> TokenActions.convert(null));
	}
	
	@Test
	void convert() {
		TokenConverter converter = token -> createToken(token.value().toUpperCase());
		
		TokenAction result = TokenActions.convert(converter);
		
		ConvertTokenAction convertAction = assertInstanceOf(ConvertTokenAction.class, result);
		assertEquals(converter, convertAction.converter());
	}
	
	@Test
	void splitWithNullStringPattern() {
		assertThrows(NullPointerException.class, () -> TokenActions.split((String) null));
	}
	
	@Test
	void splitWithStringPattern() {
		String pattern = "\\s+";
		
		TokenAction result = TokenActions.split(pattern);
		
		SplitTokenAction splitAction = assertInstanceOf(SplitTokenAction.class, result);
		assertEquals(pattern, splitAction.splitPattern().pattern());
	}
	
	@Test
	void splitWithNullPattern() {
		assertThrows(NullPointerException.class, () -> TokenActions.split((Pattern) null));
	}
	
	@Test
	void splitWithPattern() {
		Pattern pattern = Pattern.compile("\\d+");
		
		TokenAction result = TokenActions.split(pattern);
		
		SplitTokenAction splitAction = assertInstanceOf(SplitTokenAction.class, result);
		assertEquals(pattern, splitAction.splitPattern());
	}
	
	@Test
	void transformWithNullTransformer() {
		assertThrows(NullPointerException.class, () -> TokenActions.transform(null));
	}
	
	@Test
	void transform() {
		TokenTransformer transformer = List::copyOf;
		
		TokenAction result = TokenActions.transform(transformer);
		
		TransformTokenAction transformAction = assertInstanceOf(TransformTokenAction.class, result);
		assertEquals(transformer, transformAction.transformer());
	}
	
	@Test
	void wrapWithNullPrefixToken() {
		Token suffixToken = createToken(")");
		assertThrows(NullPointerException.class, () -> TokenActions.wrap(null, suffixToken));
	}
	
	@Test
	void wrapWithNullSuffixToken() {
		Token prefixToken = createToken("(");
		assertThrows(NullPointerException.class, () -> TokenActions.wrap(prefixToken, null));
	}
	
	@Test
	void wrap() {
		Token prefixToken = createToken("(");
		Token suffixToken = createToken(")");
		
		TokenAction result = TokenActions.wrap(prefixToken, suffixToken);
		
		WrapTokenAction wrapAction = assertInstanceOf(WrapTokenAction.class, result);
		assertEquals(prefixToken, wrapAction.prefixToken());
		assertEquals(suffixToken, wrapAction.suffixToken());
	}
}
