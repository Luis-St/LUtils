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

package net.luis.utils.io.token.stream;

import net.luis.utils.io.token.tokens.Token;
import org.jetbrains.annotations.*;

import java.util.List;
import java.util.Objects;

/**
 * Interface for a stream of tokens.<br>
 * A token stream is a sequence of tokens that can be read one at a time.<br>
 * The token stream maintains a current index that points to the next token to be read.<br>
 *
 * @author Luis-St
 *
 * @see MutableTokenStream
 * @see ImmutableTokenStream
 */
public interface TokenStream {
	
	/**
	 * An empty immutable token stream.<br>
	 * This stream contains no tokens and has a current index of 0.<br>
	 */
	TokenStream EMPTY = new ImmutableTokenStream(List.of(), 0);
	
	/**
	 * Creates a new mutable token stream with the given tokens and a current index of 0.<br>
	 *
	 * @param tokens The list of tokens in the stream
	 * @return A new mutable token stream
	 * @throws NullPointerException If the list of tokens is null
	 * @see #createMutable(List, int)
	 */
	static @NotNull TokenStream createMutable(@NotNull List<Token> tokens) {
		return createMutable(tokens, 0);
	}
	
	/**
	 * Creates a new mutable token stream with the given tokens and current index.<br>
	 *
	 * @param tokens The list of tokens in the stream
	 * @param currentIndex The current index of the stream
	 * @return A new mutable token stream
	 * @throws NullPointerException If the list of tokens is null
	 * @throws IndexOutOfBoundsException If the current index is negative
	 * @see MutableTokenStream
	 * @see MutableTokenStream#MutableTokenStream(List, int)
	 */
	static @NotNull TokenStream createMutable(@NotNull List<Token> tokens, int currentIndex) {
		return new MutableTokenStream(tokens, currentIndex);
	}
	
	/**
	 * Creates a new immutable token stream with the given tokens and a current index of 0.<br>
	 *
	 * @param tokens The list of tokens in the stream
	 * @return A new immutable token stream
	 * @throws NullPointerException If the list of tokens is null
	 * @see #createImmutable(List, int)
	 */
	static @NotNull TokenStream createImmutable(@NotNull List<Token> tokens) {
		return createImmutable(tokens, 0);
	}
	
	/**
	 * Creates a new immutable token stream with the given tokens and current index.<br>
	 *
	 * @param tokens The list of tokens in the stream
	 * @param currentIndex The current index of the stream
	 * @return A new immutable token stream
	 * @throws NullPointerException If the list of tokens is null
	 * @throws IndexOutOfBoundsException If the current index is negative
	 * @see ImmutableTokenStream
	 * @see ImmutableTokenStream#ImmutableTokenStream(List, int)
	 */
	static @NotNull TokenStream createImmutable(@NotNull List<Token> tokens, int currentIndex) {
		return new ImmutableTokenStream(tokens, currentIndex);
	}
	
	/**
	 * Gets an unmodifiable list of all tokens in the stream.<br>
	 * This list is a snapshot of the tokens at the time of the call and will not reflect any changes made to the stream after the call.<br>
	 *
	 * @return An unmodifiable list of all tokens in the stream
	 */
	@NotNull @Unmodifiable
	List<Token> getAllTokens();
	
	/**
	 * Checks if the token stream is empty.<br>
	 * A token stream is considered empty if it contains no tokens.<br>
	 *
	 * @return True if the token stream is empty, false otherwise
	 */
	default boolean isEmpty() {
		return this.getAllTokens().isEmpty();
	}
	
	/**
	 * Gets the number of tokens in the stream.<br>
	 *
	 * @return The number of tokens in the stream
	 */
	default int size() {
		return this.getAllTokens().size();
	}
	
	/**
	 * Gets the current index of the token stream.<br>
	 * The current index points to the token that will be returned by the call to {@link #getCurrentToken()}.<br>
	 * The index is zero-based.<br>
	 *
	 * @return The current index of the token stream
	 */
	int getCurrentIndex();
	
	/**
	 * Moves the current index by the given offset.<br>
	 * A positive offset moves the index forward, while a negative offset moves it backward.<br>
	 * If the resulting index is negative, it will be set to zero.<br>
	 * If the resulting index is greater than the size of the token stream, it will be set to the size of the token stream.<br>
	 *
	 * @param offset The offset to move the current index by
	 * @see #advanceTo(int)
	 * @throws UnsupportedOperationException If the token stream is immutable
	 */
	default void moveBy(int offset) {
		this.advanceTo(this.getCurrentIndex() + offset);
	}
	
	/**
	 * Resets the current index to zero.<br>
	 *
	 * @see #advanceTo(int)
	 * @throws UnsupportedOperationException If the token stream is immutable
	 */
	default void reset() {
		this.advanceTo(0);
	}
	
	/**
	 * Advances the current index by one and returns the new index.<br>
	 * This is equivalent to calling {@link #readToken()} and then {@link #getCurrentIndex()}.<br>
	 *
	 * @return The new current index after advancing
	 * @see #readToken()
	 * @throws EndOfTokenStreamException If there are no more tokens to read
	 * @throws UnsupportedOperationException If the token stream is immutable
	 */
	default int advance() {
		this.readToken();
		return this.getCurrentIndex();
	}
	
	/**
	 * Advances the current index to the given index.<br>
	 * If the given index is negative, the current index will be set to zero.<br>
	 * If the given index is greater than the size of the token stream, the current index will be set to the size of the token stream.<br>
	 *
	 * @param index The index to advance the current index to
	 * @throws UnsupportedOperationException If the token stream is immutable
	 */
	void advanceTo(int index);
	
	/**
	 * Advances the current index to the current index of another token stream.<br>
	 *
	 * @param other The other token stream to advance to
	 * @throws NullPointerException If the other token stream is null
	 * @throws UnsupportedOperationException If the token stream is immutable
	 * @see #advanceTo(int)
	 */
	default void advanceTo(@NotNull TokenStream other) {
		Objects.requireNonNull(other, "Other token stream must not be null");
		this.advanceTo(other.getCurrentIndex());
	}
	
	/**
	 * Gets the current token in the stream without advancing the index.<br>
	 *
	 * @return The current token in the stream
	 * @throws EndOfTokenStreamException If the index is at or beyond the end of the stream
	 */
	@NotNull Token getCurrentToken();
	
	/**
	 * Checks if there are more tokens to read in the stream.<br>
	 *
	 * @return True if there are more tokens to read, false otherwise
	 * @apiNote The implementation differs between mutable and immutable token streams
	 */
	default boolean hasMoreTokens() {
		return this.getCurrentIndex() < this.size();
	}
	
	/**
	 * Reads the current token and advances the index by one.<br>
	 * It is guaranteed that if {@link #hasMoreTokens()} returns true, this method will not throw an exception.
	 * This is equivalent to calling {@link #getCurrentToken()} and then {@link #advance()}.<br>
	 *
	 * @return The current token in the stream
	 * @throws EndOfTokenStreamException If there are no more tokens to read
	 * @throws UnsupportedOperationException If the token stream is immutable
	 */
	@NotNull Token readToken();
	
	/**
	 * Creates a copy of the token stream with the current index set to zero.<br>
	 * The returned token stream will be of the same type (mutable or immutable) as the original token stream and will contain the same tokens.<br>
	 *
	 * @return A copy of the token stream with the current index set to zero
	 */
	default @NotNull TokenStream copyFromZero() {
		return this.copyWithIndex(0);
	}
	
	/**
	 * Creates a copy of the token stream with the current index set to the given index.<br>
	 * The returned token stream will be of the same type (mutable or immutable) as the original token stream and will contain the same tokens.<br>
	 * If the index is out of bounds, it will be clamped to the valid range (0 to size).<br>
	 *
	 * @param index The index to set the current index to in the copied token stream
	 * @return A copy of the token stream with the current index set to the given index
	 */
	@NotNull TokenStream copyWithIndex(int index);
	
	/**
	 * Creates a copy of the token stream with the current index offset by the given amount.<br>
	 * The returned token stream will be of the same type (mutable or immutable) as the original token stream and will contain the same tokens.<br>
	 * If the resulting index is out of bounds, it will be clamped to the valid range (0 to size).<br>
	 *
	 * @param offset The offset to apply to the current index in the copied token stream
	 * @return A copy of the token stream with the current index offset by the given amount
	 * @see #copyWithIndex(int)
	 */
	default @NotNull TokenStream copyWithOffset(int offset) {
		return this.copyWithIndex(this.getCurrentIndex() + offset);
	}
	
	/**
	 * Creates a copy of the token stream with the tokens in reverse order and the current index adjusted accordingly.<br>
	 * The returned token stream will be of the same type (mutable or immutable) as the original token stream.<br>
	 * The current index in the reversed token stream will point to the token that corresponds to the original current index.<br>
	 * <p>
	 *     For example, if the stream has 5 tokens {@code [0, 1, 2, 3, 4]} and the current index is 2,
	 *     after reversal the tokens become {@code [4, 3, 2, 1, 0]} and the current index becomes 2
	 *     (pointing to token {@code 2}, maintaining the same relative position).
	 * </p>
	 *
	 * @return A copy of the token stream with the tokens in reverse order
	 */
	@NotNull TokenStream reversed();
	
	/**
	 * Creates a lookahead stream containing all tokens from the current position onwards.<br>
	 * The lookahead stream starts at index 0 and contains all remaining tokens in the original stream.<br>
	 * The returned token stream will be of the same type (mutable or immutable) as the original token stream.<br>
	 * <p>
	 *     If the current stream is at the end (no more non-shadow tokens available),
	 *     the returned stream will be empty or contain only shadow tokens.
	 * </p>
	 *
	 * @return A new token stream containing the remaining tokens, starting from index 0
	 */
	@NotNull TokenStream createLookaheadStream();
	
	/**
	 * Creates a lookbehind stream containing all tokens before the current position in reverse order.<br>
	 * The tokens are ordered in reverse, so the most recently processed token appears first.<br>
	 * The returned token stream will be of the same type (mutable or immutable) as the original token stream.<br>
	 * <p>
	 *     If the current stream is at the beginning (index 0), the returned stream will be empty.
	 * </p>
	 *
	 * @return A new token stream containing the previous tokens in reverse order, starting from index 0
	 */
	@NotNull TokenStream createLookbehindStream();
}
