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

package net.luis.utils.io.token.actions;

import net.luis.utils.io.token.actions.core.*;
import net.luis.utils.io.token.actions.enhancers.AnnotateTokenAction;
import net.luis.utils.io.token.actions.enhancers.IndexTokenAction;
import net.luis.utils.io.token.actions.filters.*;
import net.luis.utils.io.token.actions.transformers.*;
import net.luis.utils.io.token.tokens.Token;
import org.intellij.lang.annotations.Language;
import org.jspecify.annotations.NonNull;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * A utility class for creating token actions.<br>
 *
 * @author Luis-St
 */
public final class TokenActions {
	
	/**
	 * Private constructor to prevent instantiation.<br>
	 * This is a static helper class.<br>
	 */
	private TokenActions() {}
	
	/**
	 * Creates a token action that does nothing.<br>
	 * This action returns the matched tokens as they are.<br>
	 * The resulting list is an immutable copy of the original list.<br>
	 *
	 * @return The identity token action
	 * @apiNote This is the preferred way to access the {@link TokenAction#identity()} method
	 * @see TokenAction#identity()
	 */
	public static @NonNull TokenAction identity() {
		return TokenAction.identity();
	}
	
	/**
	 * Creates a token action that groups the tokens into a single token group.<br>
	 * The grouping behavior depends on the specified {@link GroupingMode}.<br>
	 *
	 * @param mode The grouping mode to use
	 * @return The created grouping token action
	 * @throws NullPointerException If the mode is null
	 * @see GroupingTokenAction
	 * @see GroupingMode
	 */
	public static @NonNull TokenAction grouping(@NonNull GroupingMode mode) {
		return new GroupingTokenAction(mode);
	}
	
	/**
	 * Creates a token action that annotates tokens with the given metadata.<br>
	 * Each token is wrapped in an {@link net.luis.utils.io.token.tokens.AnnotatedToken} with the provided metadata.<br>
	 * If a token is already annotated, the metadata is merged (new metadata takes precedence).<br>
	 *
	 * @param metadata The metadata to add to tokens
	 * @return The created annotate token action
	 * @throws NullPointerException If the metadata map is null
	 * @see AnnotateTokenAction
	 */
	public static @NonNull TokenAction annotate(@NonNull Map<String, Object> metadata) {
		return new AnnotateTokenAction(metadata);
	}
	
	/**
	 * Creates a token action that adds index information to tokens.<br>
	 * Each token is wrapped in an {@link net.luis.utils.io.token.tokens.IndexedToken} with its position in the matched tokens list.<br>
	 * The index starts from 0 for the first token in the match.<br>
	 *
	 * @return The created index token action with starting index of 0
	 * @see IndexTokenAction
	 */
	public static @NonNull TokenAction index() {
		return new IndexTokenAction();
	}
	
	/**
	 * Creates a token action that adds index information to tokens.<br>
	 * Each token is wrapped in an {@link net.luis.utils.io.token.tokens.IndexedToken} with its position in the matched tokens list.<br>
	 * The index starts from the specified starting index for the first token in the match.<br>
	 *
	 * @param startIndex The starting index for the first token
	 * @return The created index token action
	 * @throws IllegalArgumentException If the starting index is negative
	 * @see IndexTokenAction
	 */
	public static @NonNull TokenAction index(int startIndex) {
		return new IndexTokenAction(startIndex);
	}
	
	/**
	 * Creates a token action that extracts (removes) tokens based on a predicate and processes them with a consumer.<br>
	 * Tokens that match the predicate are removed from the result and passed to the consumer for processing.<br>
	 * This is similar to {@link #skip(Predicate)} but allows for additional processing of the extracted tokens.<br>
	 *
	 * @param filter The predicate to identify tokens to extract
	 * @param extractor The consumer to process extracted tokens
	 * @return The created extract token action
	 * @throws NullPointerException If the predicate or consumer is null
	 * @see ExtractTokenAction
	 */
	public static @NonNull TokenAction extract(@NonNull Predicate<Token> filter, @NonNull Consumer<Token> extractor) {
		return new ExtractTokenAction(filter, extractor);
	}
	
	/**
	 * Creates a token action that filters tokens based on a predicate.<br>
	 * Only tokens that match the predicate are kept in the result.<br>
	 *
	 * @param filter The predicate to filter tokens
	 * @return The created filter token action
	 * @throws NullPointerException If the predicate is null
	 * @see FilterTokenAction
	 */
	public static @NonNull TokenAction filter(@NonNull Predicate<Token> filter) {
		return new FilterTokenAction(filter);
	}
	
	/**
	 * Creates a token action that skips (removes) tokens based on a predicate.<br>
	 * Tokens that match the predicate are removed from the result.<br>
	 * This is the inverse of {@link #filter(Predicate)}.<br>
	 *
	 * @param filter The predicate to identify tokens to skip
	 * @return The created skip token action
	 * @throws NullPointerException If the predicate is null
	 * @see SkipTokenAction
	 */
	public static @NonNull TokenAction skip(@NonNull Predicate<Token> filter) {
		return new SkipTokenAction(filter);
	}
	
	/**
	 * Creates a token action that converts tokens using a converter.<br>
	 * The converter is applied to each token, allowing full control over token transformation.<br>
	 *
	 * @param converter The converter to apply to tokens
	 * @return The created convert token action
	 * @throws NullPointerException If the converter is null
	 * @see ConvertTokenAction
	 * @see TokenConverter
	 */
	public static @NonNull TokenAction convert(@NonNull TokenConverter converter) {
		return new ConvertTokenAction(converter);
	}
	
	/**
	 * Creates a token action that splits tokens based on a regular expression pattern string.<br>
	 * Each token's value is split using the pattern, creating multiple tokens from each original token.<br>
	 * Empty splits are ignored. If the original token is positioned, the split tokens will have their positions calculated accordingly.<br>
	 *
	 * @param splitPattern The pattern string to use for splitting token values
	 * @return The created split token action
	 * @throws NullPointerException If the split pattern is null
	 * @see SplitTokenAction
	 * @see #split(Pattern)
	 */
	public static @NonNull TokenAction split(@Language("RegExp") @NonNull String splitPattern) {
		return new SplitTokenAction(splitPattern);
	}
	
	/**
	 * Creates a token action that splits tokens based on a regular expression pattern.<br>
	 * Each token's value is split using the pattern, creating multiple tokens from each original token.<br>
	 * Empty splits are ignored. If the original token is positioned, the split tokens will have their positions calculated accordingly.<br>
	 *
	 * @param splitPattern The pattern to use for splitting token values
	 * @return The created split token action
	 * @throws NullPointerException If the split pattern is null
	 * @see SplitTokenAction
	 */
	public static @NonNull TokenAction split(@NonNull Pattern splitPattern) {
		return new SplitTokenAction(splitPattern);
	}
	
	/**
	 * Creates a token action that transforms the tokens using a transformer.<br>
	 * The transformer is applied to the tokens, and the result is returned as an immutable list of tokens.<br>
	 *
	 * @param transformer The transformer to apply to tokens
	 * @return The created transform token action
	 * @throws NullPointerException If the transformer is null
	 * @see TransformTokenAction
	 * @see TokenTransformer
	 */
	public static @NonNull TokenAction transform(@NonNull TokenTransformer transformer) {
		return new TransformTokenAction(transformer);
	}
	
	/**
	 * Creates a token action that wraps the tokens with a prefix and suffix token.<br>
	 * The prefix and suffix tokens are added to the beginning and end of the matched tokens, respectively.<br>
	 *
	 * @param prefixToken The prefix token
	 * @param suffixToken The suffix token
	 * @return The created wrap token action
	 * @throws NullPointerException If the prefix or suffix token is null
	 * @see WrapTokenAction
	 */
	public static @NonNull TokenAction wrap(@NonNull Token prefixToken, @NonNull Token suffixToken) {
		return new WrapTokenAction(prefixToken, suffixToken);
	}
}
