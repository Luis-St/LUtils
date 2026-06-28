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

package net.luis.utils.io.token.lexer.stream;

import net.luis.utils.io.token.TokenPosition;
import org.jspecify.annotations.NonNull;

/**
 * Interface for a stream of characters.<br>
 * A character stream is a sequence of characters that can be read one at a time.<br>
 * The character stream maintains a current index that points to the next character to be read.<br>
 * <p>
 *     The character stream is the lexer-side analogue of the parser-side token stream.<br>
 *     It tracks the source position (line and character in line) of the current index so that
 *     produced tokens can be positioned correctly.
 * </p>
 *
 * @author Luis-St
 *
 * @see MutableCharStream
 * @see ImmutableCharStream
 */
public interface CharStream {
	
	/**
	 * An empty immutable character stream.<br>
	 * This stream contains no characters and has a current index of 0.<br>
	 */
	CharStream EMPTY = new ImmutableCharStream("", 0);
	
	/**
	 * Creates a new mutable character stream with the given input and a current index of 0.<br>
	 *
	 * @param input The input string of the stream
	 * @return A new mutable character stream
	 * @throws NullPointerException If the input is null
	 * @see #createMutable(String, int)
	 */
	static @NonNull CharStream createMutable(@NonNull String input) {
		return createMutable(input, 0);
	}
	
	/**
	 * Creates a new mutable character stream with the given input and current index.<br>
	 *
	 * @param input The input string of the stream
	 * @param currentIndex The current index of the stream
	 * @return A new mutable character stream
	 * @throws NullPointerException If the input is null
	 * @throws IndexOutOfBoundsException If the current index is negative
	 * @see MutableCharStream
	 */
	static @NonNull CharStream createMutable(@NonNull String input, int currentIndex) {
		return new MutableCharStream(input, currentIndex);
	}
	
	/**
	 * Creates a new immutable character stream with the given input and a current index of 0.<br>
	 *
	 * @param input The input string of the stream
	 * @return A new immutable character stream
	 * @throws NullPointerException If the input is null
	 * @see #createImmutable(String, int)
	 */
	static @NonNull CharStream createImmutable(@NonNull String input) {
		return createImmutable(input, 0);
	}
	
	/**
	 * Creates a new immutable character stream with the given input and current index.<br>
	 *
	 * @param input The input string of the stream
	 * @param currentIndex The current index of the stream
	 * @return A new immutable character stream
	 * @throws NullPointerException If the input is null
	 * @throws IndexOutOfBoundsException If the current index is negative
	 * @see ImmutableCharStream
	 */
	static @NonNull CharStream createImmutable(@NonNull String input, int currentIndex) {
		return new ImmutableCharStream(input, currentIndex);
	}
	
	/**
	 * Gets the full input string of the stream.<br>
	 * @return The input string
	 */
	@NonNull String getInput();
	
	/**
	 * Gets the length of the input string of the stream.<br>
	 * @return The length of the input
	 */
	default int getLength() {
		return this.getInput().length();
	}
	
	/**
	 * Gets the current index of the character stream.<br>
	 * The current index points to the character that will be returned by the call to {@link #getCurrentChar()}.<br>
	 * The index is zero-based.<br>
	 *
	 * @return The current index of the character stream
	 */
	int getCurrentIndex();
	
	/**
	 * Checks if there are more characters to read in the stream.<br>
	 *
	 * @return True if there are more characters to read, false otherwise
	 */
	default boolean hasMore() {
		return this.getCurrentIndex() < this.getLength();
	}
	
	/**
	 * Gets the current character in the stream without advancing the index.<br>
	 *
	 * @return The current character in the stream
	 * @throws EndOfCharStreamException If the index is at or beyond the end of the stream
	 */
	char getCurrentChar();
	
	/**
	 * Gets the current character in the stream without advancing the index.<br>
	 * This is an alias for {@link #getCurrentChar()}.<br>
	 *
	 * @return The current character in the stream
	 * @throws EndOfCharStreamException If the index is at or beyond the end of the stream
	 * @see #getCurrentChar()
	 */
	default char peek() {
		return this.getCurrentChar();
	}
	
	/**
	 * Reads the current character and advances the index by one.<br>
	 *
	 * @return The current character in the stream
	 * @throws EndOfCharStreamException If there are no more characters to read
	 * @throws UnsupportedOperationException If the character stream is immutable
	 */
	char readChar();
	
	/**
	 * Advances the current index to the given index.<br>
	 * If the given index is negative, the current index will be set to zero.<br>
	 * If the given index is greater than the length of the input, the current index will be set to the length of the input.<br>
	 *
	 * @param index The index to advance the current index to
	 * @throws UnsupportedOperationException If the character stream is immutable
	 */
	void advanceTo(int index);
	
	/**
	 * Moves the current index by the given offset.<br>
	 * A positive offset moves the index forward, while a negative offset moves it backward.<br>
	 *
	 * @param offset The offset to move the current index by
	 * @throws UnsupportedOperationException If the character stream is immutable
	 * @see #advanceTo(int)
	 */
	default void advanceBy(int offset) {
		this.advanceTo(this.getCurrentIndex() + offset);
	}
	
	/**
	 * Creates a copy of the character stream with the current index set to the given index.<br>
	 * The returned character stream will be of the same type (mutable or immutable) as the original character stream and will contain the same input.<br>
	 * If the index is out of bounds, it will be clamped to the valid range (0 to length).<br>
	 *
	 * @param index The index to set the current index to in the copied character stream
	 * @return A copy of the character stream with the current index set to the given index
	 */
	@NonNull CharStream copyWithIndex(int index);
	
	/**
	 * Creates a copy of the character stream with the current index offset by the given amount.<br>
	 * The returned character stream will be of the same type (mutable or immutable) as the original character stream and will contain the same input.<br>
	 * If the resulting index is out of bounds, it will be clamped to the valid range (0 to length).<br>
	 *
	 * @param offset The offset to apply to the current index in the copied character stream
	 * @return A copy of the character stream with the current index offset by the given amount
	 * @see #copyWithIndex(int)
	 */
	default @NonNull CharStream copyWithOffset(int offset) {
		return this.copyWithIndex(this.getCurrentIndex() + offset);
	}
	
	/**
	 * Returns a subsequence of the input string between the given start and end index.<br>
	 *
	 * @param start The start index of the subsequence (inclusive)
	 * @param end The end index of the subsequence (exclusive)
	 * @return The subsequence of the input string
	 * @throws IndexOutOfBoundsException If the start or end index is out of bounds
	 */
	default @NonNull CharSequence subSequence(int start, int end) {
		return this.getInput().subSequence(start, end);
	}
	
	/**
	 * Gets the source position of the current index in the input string.<br>
	 * The line and character in line numbers are zero-based and computed from the newline characters in the input.<br>
	 *
	 * @return The token position of the current index
	 */
	default @NonNull TokenPosition currentPosition() {
		String input = this.getInput();
		int index = Math.min(this.getCurrentIndex(), input.length());
		
		int line = 0;
		int lastNewLine = -1;
		for (int i = 0; i < index; i++) {
			if (input.charAt(i) == '\n') {
				line++;
				lastNewLine = i;
			}
		}
		return new TokenPosition(line, index - (lastNewLine + 1), index);
	}
}
