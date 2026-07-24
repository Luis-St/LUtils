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

package net.luis.utils.grammar.parser.action;

import net.luis.utils.grammar.parser.TokenRuleMatch;
import net.luis.utils.grammar.parser.action.core.GroupingMode;
import net.luis.utils.grammar.parser.action.enhancers.AnnotateTokenAction;
import net.luis.utils.grammar.parser.action.enhancers.IndexTokenAction;
import net.luis.utils.grammar.parser.action.filters.*;
import net.luis.utils.grammar.parser.action.transformers.*;
import net.luis.utils.grammar.parser.context.TokenActionContext;
import net.luis.utils.grammar.parser.rule.AlwaysMatchTokenRule;
import net.luis.utils.grammar.parser.stream.TokenStream;
import net.luis.utils.grammar.token.SimpleToken;
import net.luis.utils.grammar.token.Token;
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
	
	private static Token token(String value) {
		return SimpleToken.createUnpositioned(value);
	}
	
	@Test
	void groupingWithNullModeThrows() {
		assertThrows(NullPointerException.class, () -> TokenActions.grouping(null));
	}
	
	@Test
	void groupingWithLabelAndNullModeThrows() {
		assertThrows(NullPointerException.class, () -> TokenActions.grouping("label", null));
	}
	
	@Test
	void groupingWithNullLabelThrows() {
		assertThrows(NullPointerException.class, () -> TokenActions.grouping(null, GroupingMode.MATCHED));
	}
	
	@Test
	void annotateWithNullMetadataThrows() {
		assertThrows(NullPointerException.class, () -> TokenActions.annotate(null));
	}
	
	@Test
	void indexWithNegativeStartIndexThrows() {
		assertThrows(IllegalArgumentException.class, () -> TokenActions.index(-1));
	}
	
	@Test
	void extractWithNullFilterThrows() {
		assertThrows(NullPointerException.class, () -> TokenActions.extract(null, _ -> {}));
	}
	
	@Test
	void extractWithNullExtractorThrows() {
		assertThrows(NullPointerException.class, () -> TokenActions.extract(_ -> true, null));
	}
	
	@Test
	void filterWithNullPredicateThrows() {
		assertThrows(NullPointerException.class, () -> TokenActions.filter(null));
	}
	
	@Test
	void skipWithNullPredicateThrows() {
		assertThrows(NullPointerException.class, () -> TokenActions.skip(null));
	}
	
	@Test
	void convertWithNullConverterThrows() {
		assertThrows(NullPointerException.class, () -> TokenActions.convert(null));
	}
	
	@Test
	void splitWithNullStringPatternThrows() {
		assertThrows(NullPointerException.class, () -> TokenActions.split((String) null));
	}
	
	@Test
	void splitWithNullPatternThrows() {
		assertThrows(NullPointerException.class, () -> TokenActions.split((Pattern) null));
	}
	
	@Test
	void transformWithNullTransformerThrows() {
		assertThrows(NullPointerException.class, () -> TokenActions.transform(null));
	}
	
	@Test
	void wrapWithNullPrefixThrows() {
		assertThrows(NullPointerException.class, () -> TokenActions.wrap(null, token("suffix")));
	}
	
	@Test
	void wrapWithNullSuffixThrows() {
		assertThrows(NullPointerException.class, () -> TokenActions.wrap(token("prefix"), null));
	}
	
	@Test
	void identityCreatesIdentityAction() {
		TokenAction action = TokenActions.identity();
		assertNotNull(action);
	}
	
	@Test
	void groupingWithModeOnlyCreatesUnlabeledAction() {
		TokenAction result = TokenActions.grouping(GroupingMode.MATCHED);
		assertInstanceOf(GroupingTokenAction.class, result);
		GroupingTokenAction casted = (GroupingTokenAction) result;
		assertEquals("", casted.label());
		assertEquals(GroupingMode.MATCHED, casted.mode());
	}
	
	@Test
	void groupingWithLabelAndModeCreatesLabeledAction() {
		TokenAction result = TokenActions.grouping("label", GroupingMode.ALL);
		assertInstanceOf(GroupingTokenAction.class, result);
		GroupingTokenAction casted = (GroupingTokenAction) result;
		assertEquals("label", casted.label());
		assertEquals(GroupingMode.ALL, casted.mode());
	}
	
	@Test
	void annotateCreatesAnnotateAction() {
		TokenAction result = TokenActions.annotate(Map.of("key", "value"));
		assertInstanceOf(AnnotateTokenAction.class, result);
	}
	
	@Test
	void indexCreatesIndexActionWithDefaultStart() {
		TokenAction result = TokenActions.index();
		assertInstanceOf(IndexTokenAction.class, result);
	}
	
	@Test
	void indexWithStartIndexCreatesIndexAction() {
		TokenAction result = TokenActions.index(5);
		assertInstanceOf(IndexTokenAction.class, result);
	}
	
	@Test
	void extractCreatesExtractAction() {
		TokenAction result = TokenActions.extract(_ -> true, _ -> {});
		assertInstanceOf(ExtractTokenAction.class, result);
	}
	
	@Test
	void filterCreatesFilterAction() {
		TokenAction result = TokenActions.filter(_ -> true);
		assertInstanceOf(FilterTokenAction.class, result);
	}
	
	@Test
	void skipCreatesSkipAction() {
		TokenAction result = TokenActions.skip(_ -> true);
		assertInstanceOf(SkipTokenAction.class, result);
	}
	
	@Test
	void convertCreatesConvertAction() {
		net.luis.utils.grammar.parser.action.core.TokenConverter converter = t -> t;
		TokenAction result = TokenActions.convert(converter);
		assertInstanceOf(ConvertTokenAction.class, result);
		assertSame(converter, ((ConvertTokenAction) result).converter());
	}
	
	@Test
	void splitWithStringPatternCreatesSplitAction() {
		TokenAction result = TokenActions.split(",");
		assertInstanceOf(SplitTokenAction.class, result);
	}
	
	@Test
	void splitWithPatternCreatesSplitAction() {
		Pattern pattern = Pattern.compile(",");
		TokenAction result = TokenActions.split(pattern);
		assertInstanceOf(SplitTokenAction.class, result);
		assertEquals(pattern, ((SplitTokenAction) result).splitPattern());
	}
	
	@Test
	void transformCreatesTransformAction() {
		net.luis.utils.grammar.parser.action.core.TokenTransformer transformer = tokens -> tokens;
		TokenAction result = TokenActions.transform(transformer);
		assertInstanceOf(TransformTokenAction.class, result);
		assertSame(transformer, ((TransformTokenAction) result).transformer());
	}
	
	@Test
	void wrapCreatesWrapAction() {
		Token prefix = token("prefix");
		Token suffix = token("suffix");
		TokenAction result = TokenActions.wrap(prefix, suffix);
		assertInstanceOf(WrapTokenAction.class, result);
		WrapTokenAction casted = (WrapTokenAction) result;
		assertEquals(prefix, casted.prefixToken());
		assertEquals(suffix, casted.suffixToken());
	}
	
	@Test
	void annotateForwardsMetadataToAction() {
		TokenAction result = TokenActions.annotate(Map.of("key", "value"));
		assertEquals(Map.of("key", "value"), ((AnnotateTokenAction) result).metadata());
	}
	
	@Test
	void indexDefaultStartIndexIsZero() {
		assertEquals(0, ((IndexTokenAction) TokenActions.index()).startIndex());
	}
	
	@Test
	void indexWithStartIndexForwardsValueToAction() {
		TokenAction result = TokenActions.index(5);
		assertEquals(5, ((IndexTokenAction) result).startIndex());
	}
	
	@Test
	void extractForwardsFilterAndExtractorToAction() {
		Predicate<Token> filter = _ -> true;
		Consumer<Token> extractor = _ -> {};
		TokenAction result = TokenActions.extract(filter, extractor);
		assertSame(filter, ((ExtractTokenAction) result).filter());
		assertSame(extractor, ((ExtractTokenAction) result).extractor());
	}
	
	@Test
	void filterForwardsPredicateToAction() {
		Predicate<Token> filter = _ -> true;
		TokenAction result = TokenActions.filter(filter);
		assertSame(filter, ((FilterTokenAction) result).filter());
	}
	
	@Test
	void skipForwardsPredicateToAction() {
		Predicate<Token> filter = _ -> true;
		TokenAction result = TokenActions.skip(filter);
		assertSame(filter, ((SkipTokenAction) result).filter());
	}
	
	@Test
	void splitWithStringPatternForwardsCompiledPatternToAction() {
		TokenAction result = TokenActions.split(",");
		assertEquals(",", ((SplitTokenAction) result).splitPattern().pattern());
	}
	
	@Test
	void createdActionsApplySuccessfullyToAMatch() {
		Token tokenA = token("a");
		Token tokenB = token("b");
		List<Token> matchedTokens = List.of(tokenA, tokenB);
		TokenRuleMatch match = new TokenRuleMatch(0, 2, matchedTokens, AlwaysMatchTokenRule.INSTANCE);
		TokenActionContext ctx = new TokenActionContext(TokenStream.createImmutable(matchedTokens));
		
		List<Token> identityResult = TokenActions.identity().apply(match, ctx);
		assertEquals(matchedTokens, identityResult);
		
		List<Token> groupingResult = TokenActions.grouping(GroupingMode.MATCHED).apply(match, ctx);
		assertEquals(1, groupingResult.size());
		
		List<Token> convertResult = TokenActions.convert(t -> t).apply(match, ctx);
		assertEquals(matchedTokens, convertResult);
		
		List<Token> transformResult = TokenActions.transform(tokens -> tokens).apply(match, ctx);
		assertEquals(matchedTokens, transformResult);
		
		Token prefix = token("prefix");
		Token suffix = token("suffix");
		List<Token> wrapResult = TokenActions.wrap(prefix, suffix).apply(match, ctx);
		assertEquals(matchedTokens.size() + 2, wrapResult.size());
	}
}
