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

import net.luis.utils.math.Mth;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

/**
 * A mutable implementation of the {@link CharStream} interface.<br>
 * This class represents a stream of characters whose current index can be modified after creation.<br>
 * All operations defined in the {@link CharStream} interface are supported.<br>
 *
 * @author Luis-St
 *
 * @see CharStream
 * @see ImmutableCharStream
 */
public class MutableCharStream implements CharStream {
	
	/**
	 * The input string of the stream.
	 */
	private final String input;
	/**
	 * The current index of the stream.
	 */
	private int currentIndex;
	
	/**
	 * Creates a new mutable character stream with the given input and current index.<br>
	 *
	 * @param input The input string of the stream
	 * @param currentIndex The current index of the stream
	 * @throws NullPointerException If the input is null
	 * @throws IndexOutOfBoundsException If the current index is negative
	 */
	MutableCharStream(@NonNull String input, int currentIndex) {
		this.input = Objects.requireNonNull(input, "Input must not be null");
		if (currentIndex < 0) {
			throw new IndexOutOfBoundsException("Current index must not be negative");
		}
		this.currentIndex = Mth.clamp(currentIndex, 0, this.input.length());
	}
	
	@Override
	public @NonNull String getInput() {
		return this.input;
	}
	
	@Override
	public int getCurrentIndex() {
		return this.currentIndex;
	}
	
	@Override
	public char getCurrentChar() {
		if (!this.hasMore()) {
			throw new EndOfCharStreamException("No character available at the current position");
		}
		return this.input.charAt(this.currentIndex);
	}
	
	@Override
	public char readChar() {
		if (!this.hasMore()) {
			throw new EndOfCharStreamException("No character available in the stream");
		}
		char c = this.input.charAt(this.currentIndex);
		this.currentIndex++;
		return c;
	}
	
	@Override
	public void advanceTo(int index) {
		this.currentIndex = Mth.clamp(index, 0, this.input.length());
	}
	
	@Override
	public @NonNull CharStream copyWithIndex(int index) {
		return new MutableCharStream(this.input, Mth.clamp(index, 0, this.input.length()));
	}
}
