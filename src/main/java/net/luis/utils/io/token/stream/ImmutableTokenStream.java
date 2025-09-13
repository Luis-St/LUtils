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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;

/**
 * An immutable implementation of the {@link TokenStream} interface.<br>
 * This class represents a stream of tokens that cannot be modified after creation.<br>
 * The following operations are not supported and will throw an {@link UnsupportedOperationException} if called:<br>
 * <ul>
 *     <li>{@link #moveBy(int)}</li>
 *     <li>{@link #reset()}</li>
 *     <li>{@link #advance()}</li>
 *     <li>{@link #advanceTo(int)}</li>
 *     <li>{@link #advanceTo(TokenStream)}</li>
 *     <li>{@link #readToken()}</li>
 * </ul>
 *
 * @author Luis-St
 *
 * @see TokenStream
 * @see MutableTokenStream
 */
public class ImmutableTokenStream implements TokenStream {
	
	/**
	 * The list of tokens in the stream.<br>
	 * This list is immutable and cannot be modified after the stream is created.<br>
	 */
	private final List<Token> tokens;
	/**
	 * The current index of the stream.<br>
	 * Final to ensure immutability.<br>
	 */
	private final int currentIndex;
	
	/**
	 * Creates a new immutable token stream with the given list of tokens and current index.<br>
	 * The list of tokens is copied to ensure immutability.<br>
	 * The current index is updated to point to the next non-shadow token if the given index points to a shadow token.<br>
	 *
	 * @param tokens The list of tokens in the stream
	 * @param currentIndex The current index of the stream
	 * @throws NullPointerException If the list of tokens is null
	 * @throws IndexOutOfBoundsException If the current index is negative
	 */
	ImmutableTokenStream(@NotNull List<Token> tokens, int currentIndex) {
		Objects.requireNonNull(tokens, "Token list must not be null");
		
		this.tokens = List.copyOf(tokens);
		if (currentIndex < 0) {
			throw new IndexOutOfBoundsException("Current index must not be negative");
		}
		
		while (currentIndex < this.tokens.size() && this.tokens.get(currentIndex) instanceof ShadowToken) {
			currentIndex++;
		}
		this.currentIndex = currentIndex;
	}
	
	@Override
	public @NotNull @Unmodifiable List<Token> getAllTokens() {
		return this.tokens;
	}
	
	@Override
	public int getCurrentIndex() {
		return this.currentIndex;
	}
	
	@Override
	public void reset() {
		throw new UnsupportedOperationException("Immutable token stream cannot be modified");
	}
	
	@Override
	public void advanceTo(int index) {
		throw new UnsupportedOperationException("Immutable token stream cannot be modified");
	}
	
	@Override
	public @NotNull Token getCurrentToken() {
		if (!this.hasMoreTokens()) {
			throw new EndOfTokenStreamException("No more tokens available");
		}
		return this.tokens.get(this.currentIndex);
	}
	
	@Override
	public @NotNull Token readToken() {
		throw new UnsupportedOperationException("Immutable token stream cannot be modified");
	}
	
	@Override
	public @NotNull TokenStream copyWithIndex(int index) {
		return new ImmutableTokenStream(this.tokens, Mth.clamp(index, 0, this.tokens.size()));
	}
	
	@Override
	public @NotNull TokenStream reversed() {
		List<Token> reversedTokens = new ArrayList<>(this.tokens);
		Collections.reverse(reversedTokens);
		int newIndex = Mth.clamp(this.tokens.size() - 1 - this.currentIndex, 0, this.tokens.size());
		return new ImmutableTokenStream(reversedTokens, newIndex);
	}
	
	@Override
	public @NotNull TokenStream createLookaheadStream() {
		return new ImmutableTokenStream(this.tokens.subList(this.currentIndex, this.tokens.size()), 0);
	}
	
	@Override
	@SuppressWarnings("DuplicatedCode")
	public @NotNull TokenStream createLookbehindStream() {
		if (this.currentIndex == 0) {
			return new ImmutableTokenStream(Collections.emptyList(), 0);
		}
		
		List<Token> beforeTokens = Lists.newArrayList(this.tokens.subList(0, this.currentIndex));
		Collections.reverse(beforeTokens);
		return new ImmutableTokenStream(beforeTokens, 0);
	}
}
