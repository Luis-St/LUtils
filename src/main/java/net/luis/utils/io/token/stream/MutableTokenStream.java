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

import com.google.common.collect.Lists;
import net.luis.utils.io.token.tokens.ShadowToken;
import net.luis.utils.io.token.tokens.Token;
import net.luis.utils.math.Mth;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NonNull;

import java.util.*;

/**
 * A mutable implementation of the {@link TokenStream} interface.<br>
 * This class represents a stream of tokens that can be modified after creation.<br>
 * All operations defined in the {@link TokenStream} interface are supported.<br>
 *
 * @author Luis-St
 *
 * @see TokenStream
 * @see ImmutableTokenStream
 */
public class MutableTokenStream implements TokenStream {
	
	/**
	 * The list of tokens in the stream.<br>
	 */
	private final List<Token> tokens;
	/**
	 * The current index of the stream.<br>
	 */
	private int currentIndex;
	
	/**
	 * Creates a new mutable token stream with the given list of tokens and current index.<br>
	 * The list of tokens is copied to prevent external modification.<br>
	 *
	 * @param tokens The list of tokens in the stream
	 * @param currentIndex The current index of the stream
	 * @throws NullPointerException If the list of tokens is null
	 * @throws IndexOutOfBoundsException If the current index is negative
	 */
	MutableTokenStream(@NonNull List<Token> tokens, int currentIndex) {
		Objects.requireNonNull(tokens, "Token list must not be null");
		
		this.tokens = Lists.newArrayList(tokens);
		this.currentIndex = currentIndex;
		if (this.currentIndex < 0) {
			throw new IndexOutOfBoundsException("Current index must not be negative");
		}
		this.currentIndex = this.findNextNonShadowIndex();
	}
	
	/**
	 * Finds the index of the next non-shadow token starting from the current index.<br>
	 * If the current index points to a non-shadow token, it is returned.<br>
	 * If there are no non-shadow tokens after the current index, the size of the token list is returned.<br>
	 *
	 * @return The index of the next non-shadow token, or the size of the token list if none is found
	 */
	private int findNextNonShadowIndex() {
		int index = this.currentIndex;
		while (index < this.tokens.size() && this.tokens.get(index) instanceof ShadowToken) {
			index++;
		}
		return index;
	}
	
	@Override
	public @NonNull @Unmodifiable List<Token> getAllTokens() {
		return Collections.unmodifiableList(this.tokens);
	}
	
	@Override
	public int getCurrentIndex() {
		return this.currentIndex;
	}
	
	@Override
	public void advanceTo(int index) {
		this.currentIndex = Mth.clamp(index, 0, this.tokens.size());
		this.currentIndex = this.findNextNonShadowIndex();
	}
	
	/**
	 * Gets the current non-shadow token in the stream.<br>
	 *
	 * @return The current non-shadow token in the stream
	 * @throws EndOfTokenStreamException If there are no non-shadow tokens available at or beyond the current position
	 * @see #findNextNonShadowIndex()
	 */
	@Override
	public @NonNull Token getCurrentToken() {
		int nonShadowIndex = this.findNextNonShadowIndex();
		if (nonShadowIndex >= this.tokens.size()) {
			throw new EndOfTokenStreamException("No non-shadow token available at the current position");
		}
		return this.tokens.get(nonShadowIndex);
	}
	
	/**
	 * Checks if there are more non-shadow tokens to read in the stream.<br>
	 *
	 * @return True if there are more non-shadow tokens to read, false otherwise
	 * @see #findNextNonShadowIndex()
	 */
	@Override
	public boolean hasMoreTokens() {
		return this.findNextNonShadowIndex() < this.tokens.size();
	}
	
	/**
	 * Reads the current non-shadow token in the stream and advances the current index to the next non-shadow token.<br>
	 * It is guaranteed that if {@link #hasMoreTokens()} returns true, this method will not throw an exception.
	 * This is equivalent to calling {@link #getCurrentToken()} and then {@link #advance()}.<br>
	 *
	 * @return The current token in the stream
	 * @throws EndOfTokenStreamException If there are no more tokens to read
	 * @throws UnsupportedOperationException If the token stream is immutable
	 * @see #findNextNonShadowIndex()
	 */
	@Override
	public @NonNull Token readToken() {
		int nonShadowIndex = this.findNextNonShadowIndex();
		if (nonShadowIndex >= this.tokens.size()) {
			throw new EndOfTokenStreamException("No non-shadow token available in the stream");
		}
		
		Token token = this.tokens.get(nonShadowIndex);
		this.currentIndex = nonShadowIndex + 1;
		return token;
	}
	
	@Override
	public @NonNull TokenStream copyWithIndex(int index) {
		return new MutableTokenStream(this.tokens, Mth.clamp(index, 0, this.tokens.size()));
	}
	
	@Override
	public @NonNull TokenStream reversed() {
		List<Token> reversedTokens = new ArrayList<>(this.tokens);
		Collections.reverse(reversedTokens);
		int newIndex = Mth.clamp(this.tokens.size() - 1 - this.currentIndex, 0, this.tokens.size());
		return new MutableTokenStream(reversedTokens, newIndex);
	}
	
	@Override
	public @NonNull TokenStream createLookaheadStream() {
		return new MutableTokenStream(this.tokens.subList(this.currentIndex, this.tokens.size()), 0);
	}
	
	@Override
	@SuppressWarnings("DuplicatedCode")
	public @NonNull TokenStream createLookbehindStream() {
		if (this.currentIndex == 0) {
			return new MutableTokenStream(Collections.emptyList(), 0);
		}
		
		List<Token> beforeTokens = Lists.newArrayList(this.tokens.subList(0, this.currentIndex));
		Collections.reverse(beforeTokens);
		return new MutableTokenStream(beforeTokens, 0);
	}
}
