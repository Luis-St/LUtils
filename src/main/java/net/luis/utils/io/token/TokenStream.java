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

package net.luis.utils.io.token;

import com.google.common.collect.Lists;
import net.luis.utils.io.token.tokens.Token;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Represents a stream of tokens that allows sequential reading and manipulation.<br>
 * The token stream provides functionality for:<br>
 * <ul>
 *     <li>Sequential token consumption and reading</li>
 *     <li>Position tracking and management</li>
 *     <li>Lookahead and lookbehind operations</li>
 *     <li>Stream copying and resetting</li>
 *     <li>Stream reversal operations</li>
 * </ul>
 * <p>
 *     This implementation maintains an internal current index that tracks the position
 *     within the token stream. The stream can be navigated forwards through token
 *     consumption, or reset to the beginning. Additionally, it supports creating
 *     lookahead and lookbehind streams for non-destructive token inspection.
 * </p>
 * <p>
 *     The token stream is designed to be used in parsing scenarios where tokens
 *     need to be processed sequentially while maintaining the ability to backtrack
 *     or inspect upcoming tokens without modifying the main stream position.
 * </p>
 *
 * @author Luis-St
 */
public class TokenStream {
	
	/**
	 * The list of tokens in this stream.<br>
	 */
	private final List<Token> tokens;
	/**
	 * The current index position in the token stream.<br>
	 */
	private int currentIndex;
	
	/**
	 * Constructs a new token stream with the given tokens.<br>
	 * The current index is initialized to zero (beginning of the stream).<br>
	 *
	 * @param tokens The list of tokens to create the stream from
	 * @throws NullPointerException If the token list is null
	 */
	public TokenStream(@NotNull List<Token> tokens) {
		this(tokens, 0);
	}
	
	/**
	 * Constructs a new token stream with the given tokens and current index.<br>
	 * The token list is copied to prevent external modifications.<br>
	 *
	 * @param tokens The list of tokens to create the stream from
	 * @param currentIndex The initial position in the token stream
	 * @throws NullPointerException If the token list is null
	 * @throws IndexOutOfBoundsException If the current index is negative or beyond the bounds of the token list (unless the list is empty)
	 */
	public TokenStream(@NotNull List<Token> tokens, int currentIndex) {
		Objects.requireNonNull(tokens, "Token list must not be null");
		this.tokens = Lists.newArrayList(tokens);
		this.currentIndex = currentIndex;
		if (!(this.hasToken() || this.tokens.isEmpty()) || this.currentIndex < 0) {
			throw new IndexOutOfBoundsException("Current index is out of bounds: " + this.currentIndex);
		}
	}
	
	/**
	 * Checks whether this token stream is empty.<br>
	 * An empty stream contains no tokens.<br>
	 *
	 * @return True if the stream contains no tokens, otherwise false
	 */
	public boolean isEmpty() {
		return this.tokens.isEmpty();
	}
	
	/**
	 * Returns the total number of tokens in this stream.<br>
	 * This count includes all tokens regardless of the current position.<br>
	 *
	 * @return The total number of tokens in the stream
	 */
	public int size() {
		return this.tokens.size();
	}
	
	/**
	 * Returns the current index position in the token stream.<br>
	 * The index represents the position of the next token to be read.<br>
	 *
	 * @return The current index position
	 */
	public int getCurrentIndex() {
		return this.currentIndex;
	}
	
	/**
	 * Returns the token at the current index position without advancing the stream.<br>
	 * This operation is equivalent to peeking at the current token.<br>
	 *
	 * @return The token at the current position
	 * @throws IndexOutOfBoundsException If there is no token available at the current position ({@link #hasToken()} returns false)
	 */
	public @NotNull Token getCurrentToken() {
		return this.tokens.get(this.currentIndex);
	}
	
	/**
	 * Checks whether there is a token available at the current index position.<br>
	 * This method should be called before {@link #getCurrentToken()}, {@link #consumeToken()}, or {@link #readToken()}
	 * to ensure that a token is available.<br>
	 *
	 * @return True if a token is available at the current position, otherwise false
	 */
	public boolean hasToken() {
		return 0 <= this.currentIndex && this.currentIndex < this.tokens.size();
	}
	
	/**
	 * Consumes the current token by advancing the stream position by one.<br>
	 * The token itself is not returned, only the stream position is advanced.<br>
	 * Use this method when you need to skip over a token without processing its value.<br>
	 *
	 * @return The new current index after consuming the token
	 * @throws NoSuchElementException If there is no token available to consume ({@link #hasToken()} returns false)
	 * @see #readToken()
	 */
	public int consumeToken() {
		if (!this.hasToken()) {
			throw new NoSuchElementException("No token available in the stream");
		}
		
		this.currentIndex++;
		return this.currentIndex;
	}
	
	/**
	 * Reads and returns the current token, then advances the stream position by one.<br>
	 * This is the primary method for sequential token processing, combining
	 * token retrieval with stream advancement in a single operation.<br>
	 *
	 * @return The token at the current position before advancing
	 * @throws NoSuchElementException If there is no token available to read ({@link #hasToken()} returns false)
	 * @see #consumeToken()
	 * @see #getCurrentToken()
	 */
	public @NotNull Token readToken() {
		if (!this.hasToken()) {
			throw new NoSuchElementException("No next token available in the stream");
		}
		
		return this.tokens.get(this.currentIndex++);
	}
	
	/**
	 * Resets the stream position to the beginning (index 0).<br>
	 * After calling this method, the next token read will be the first token in the stream.<br>
	 * This operation does not modify the tokens themselves, only the current position.<br>
	 */
	public void reset() {
		this.currentIndex = 0;
	}
	
	/**
	 * Creates a lookahead stream containing all tokens from the current position onwards.<br>
	 * The lookahead stream starts at index 0 and contains all remaining tokens in the original stream.
	 * This allows for non-destructive inspection of upcoming tokens without affecting the current stream position.<br>
	 * <p>
	 *     If the current stream is at the end (no more tokens available),
	 *     the returned stream will be empty.
	 * </p>
	 *
	 * @return A new token stream containing the remaining tokens, starting from index 0
	 * @see #lookbehindStream()
	 */
	public @NotNull TokenStream lookaheadStream() {
		return new TokenStream(this.tokens.subList(this.currentIndex, this.tokens.size()));
	}
	
	/**
	 * Creates a lookbehind stream containing all tokens before the current position in reverse order.<br>
	 * The lookbehind stream allows inspection of previously processed tokens.
	 * The tokens are ordered in reverse, so the most recently processed token appears first.<br>
	 * <p>
	 *     If the current stream is at the beginning (index 0), the returned stream will be empty.
	 * </p>
	 *
	 * @return A new token stream containing the previous tokens in reverse order, starting from index 0
	 * @see #lookaheadStream()
	 */
	public @NotNull TokenStream lookbehindStream() {
		if (this.currentIndex == 0) {
			return new TokenStream(Collections.emptyList());
		}
		
		List<Token> beforeTokens = Lists.newArrayList(this.tokens.subList(0, this.currentIndex));
		Collections.reverse(beforeTokens);
		return new TokenStream(beforeTokens);
	}
	
	/**
	 * Reverses the order of all tokens in this stream and adjusts the current index accordingly.<br>
	 * After reversal, the current index is recalculated to point to the same relative position
	 * in the reversed stream. This operation modifies the current stream in place.<br>
	 * <p>
	 *     For example, if the stream has 5 tokens {@code [0, 1, 2, 3, 4]} and the current index is 2,
	 *     after reversal the tokens become {@code [4, 3, 2, 1, 0]} and the current index becomes 2
	 *     (pointing to token {@code 2}, maintaining the same relative position).
	 * </p>
	 */
	public void reverse() {
		Collections.reverse(this.tokens);
		this.currentIndex = Math.max(0, this.tokens.size() - 1 - this.currentIndex);
	}
	
	/**
	 * Creates a copy of this token stream with the specified current index.<br>
	 * The returned stream contains the same tokens but with an independent position tracker.
	 * Changes to the copy's position will not affect the original stream.<br>
	 *
	 * @param currentIndex The index position for the copied stream
	 * @return A new token stream that is a copy of this stream with the specified index
	 * @throws IndexOutOfBoundsException If the specified index is out of bounds for the token list
	 * @see #copyFromZero()
	 * @see #copyWithCurrentIndex()
	 */
	public @NotNull TokenStream copy(int currentIndex) {
		return new TokenStream(Lists.newArrayList(this.tokens), currentIndex);
	}
	
	/**
	 * Creates a copy of this token stream with the current index reset to zero.<br>
	 * This is equivalent to calling {@link #copy(int)} with an index of 0.<br>
	 * The copied stream will start from the beginning regardless of the original stream's position.<br>
	 *
	 * @return A new token stream that is a copy of this stream starting from the beginning
	 * @see #copy(int)
	 * @see #copyWithCurrentIndex()
	 */
	public @NotNull TokenStream copyFromZero() {
		return this.copy(0);
	}
	
	/**
	 * Creates a copy of this token stream preserving the current index position.<br>
	 * This is equivalent to calling {@link #copy(int)} with the current index value.<br>
	 * The copied stream will have the same position as the original stream.<br>
	 *
	 * @return A new token stream that is a copy of this stream with the same current index
	 * @see #copy(int)
	 * @see #copyFromZero()
	 */
	public @NotNull TokenStream copyWithCurrentIndex() {
		return this.copy(this.currentIndex);
	}
}
