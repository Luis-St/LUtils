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

package net.luis.utils.io.reader;

import net.luis.utils.exception.ConcurrentInteractionException;
import net.luis.utils.exception.InvalidStringException;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.io.Reader;
import java.io.UncheckedIOException;
import java.util.Objects;

/**
 * A string reader which handles quoted parts.<br>
 * <p>
 *     The handled quotes are defined by the {@link StringQuoting} of the reader.<br>
 *     Methods which read until a terminator ignore terminators inside quoted parts.<br>
 *     Inside a quoted part, only the closing character of the opening quote ends the part.
 * </p>
 *
 * @author Luis-St
 */
public class QuotedStringReader extends PositionedStringReader {
	
	/**
	 * The quotes handled by this reader.<br>
	 */
	private final StringQuoting quoting;
	
	/**
	 * Constructs a new quoted string reader with the given string.<br>
	 * The reader uses the {@link #DEFAULT_ESCAPE_CHARACTER default escape character} and the {@link StringQuoting#DEFAULT default quoting}.<br>
	 *
	 * @param string The string to read from
	 * @throws NullPointerException If the string is null
	 */
	public QuotedStringReader(@NonNull String string) {
		this(string, DEFAULT_ESCAPE_CHARACTER, StringQuoting.DEFAULT);
	}
	
	/**
	 * Constructs a new quoted string reader with the given string, escape character and quoting.<br>
	 *
	 * @param string The string to read from
	 * @param escapeCharacter The escape character, or null to disable escaping
	 * @param quoting The quotes handled by the reader
	 * @throws NullPointerException If the string or quoting is null
	 */
	public QuotedStringReader(@NonNull String string, @Nullable Character escapeCharacter, @NonNull StringQuoting quoting) {
		super(string, escapeCharacter);
		this.quoting = Objects.requireNonNull(quoting, "Quoting must not be null");
	}
	
	/**
	 * Constructs a new quoted string reader from the given reader.<br>
	 * The reader is closed after reading the string.<br>
	 * The reader uses the {@link #DEFAULT_ESCAPE_CHARACTER default escape character} and the {@link StringQuoting#DEFAULT default quoting}.<br>
	 *
	 * @param reader The reader to read from
	 * @throws NullPointerException If the reader is null
	 * @throws UncheckedIOException If an I/O error occurs while reading the string
	 */
	public QuotedStringReader(@NonNull Reader reader) {
		this(reader, DEFAULT_ESCAPE_CHARACTER, StringQuoting.DEFAULT);
	}
	
	/**
	 * Constructs a new quoted string reader from the given reader with the given escape character and quoting.<br>
	 * The reader is closed after reading the string.<br>
	 *
	 * @param reader The reader to read from
	 * @param escapeCharacter The escape character, or null to disable escaping
	 * @param quoting The quotes handled by the reader
	 * @throws NullPointerException If the reader or quoting is null
	 * @throws UncheckedIOException If an I/O error occurs while reading the string
	 */
	public QuotedStringReader(@NonNull Reader reader, @Nullable Character escapeCharacter, @NonNull StringQuoting quoting) {
		super(reader, escapeCharacter);
		this.quoting = Objects.requireNonNull(quoting, "Quoting must not be null");
	}
	
	/**
	 * Returns the quotes handled by this reader.<br>
	 * @return The quoting
	 */
	public @NonNull StringQuoting getQuoting() {
		return this.quoting;
	}
	
	/**
	 * Checks whether the given character opens a quote handled by this reader.<br>
	 *
	 * @param c The character to check
	 * @return True if the character opens a handled quote, otherwise false
	 */
	protected boolean isQuote(char c) {
		return this.quoting.isOpening(c);
	}
	
	@Override
	protected @NonNull NestingTracker createTracker() {
		return new QuoteTracker(this.quoting);
	}
	
	/**
	 * Reads a quoted string.<br>
	 * A quoted string is a string enclosed in one of the quotes handled by this reader.<br>
	 * The quotes are read but not included in the result.<br>
	 * <p>
	 *     If escaping is enabled, an escaped quote does not end the quoted string.<br>
	 *     The escape characters are kept in the result.<br>
	 *     Only the closing character of the opening quote ends the string, other quotes are read as normal characters.
	 * </p>
	 *
	 * @return The quoted string which was read
	 * @throws ConcurrentInteractionException If a child reader is active
	 * @throws IllegalStateException If this reader is a closed sub reader
	 * @throws StringIndexOutOfBoundsException If there are no more characters to read
	 * @throws InvalidStringException If the next read character is not a handled quote or the quoted string is not closed
	 */
	public @NonNull String readQuotedString() {
		this.ensureUsable();
		if (!this.canRead()) {
			throw new StringIndexOutOfBoundsException(this.withLocation("Expected a quoted string but found nothing"));
		}
		
		char next = this.peek();
		StringQuote quote = this.quoting.getQuote(next);
		if (quote == null) {
			throw new InvalidStringException(this.withLocation("Expected a quote as next character, but found: '" + next + "'"));
		}
		
		this.skip();
		StringBuilder builder = new StringBuilder();
		boolean escaped = false;
		while (this.canRead()) {
			char c = this.read();
			if (escaped) {
				escaped = false;
			} else if (this.isEscapeCharacter(c)) {
				escaped = true;
			} else if (c == quote.close()) {
				return builder.toString();
			}
			builder.append(c);
		}
		throw new InvalidStringException(this.withLocation("Expected a closing quote '" + quote.close() + "' but reached the end of the string: '" + next + builder + "'"));
	}
	
	/**
	 * Reads a string.<br>
	 * <p>
	 *     If the next character is a handled quote, {@link #readQuotedString()} is called<br>
	 *     otherwise {@link #readUnquotedString()} is called.
	 * </p>
	 *
	 * @return The string which was read
	 * @throws ConcurrentInteractionException If a child reader is active
	 * @throws IllegalStateException If this reader is a closed sub reader
	 * @throws StringIndexOutOfBoundsException If there are no more characters to read
	 * @throws InvalidStringException If the string is quoted and the quoted string is not closed
	 * @see #readQuotedString()
	 * @see #readUnquotedString()
	 */
	public @NonNull String readString() {
		this.ensureUsable();
		if (!this.canRead()) {
			throw new StringIndexOutOfBoundsException(this.withLocation("Expected a string value but found nothing"));
		}
		
		if (this.isQuote(this.peek())) {
			return this.readQuotedString();
		}
		return this.readUnquotedString();
	}
	
	//region Object overrides
	@Override
	public boolean equals(Object o) {
		if (!super.equals(o)) return false;
		QuotedStringReader that = (QuotedStringReader) o;
		
		return this.quoting.equals(that.quoting);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), this.quoting);
	}
	//endregion
	
	/**
	 * A tracker for quoted parts of a string.<br>
	 * A quoted part is opened by the opening character of a quote and closed by the closing character of the same quote.<br>
	 *
	 * @author Luis-St
	 */
	protected static class QuoteTracker implements NestingTracker {
		
		/**
		 * The quotes which are tracked.<br>
		 */
		private final StringQuoting quoting;
		/**
		 * The quote of the currently open quoted part, or null if no quoted part is open.<br>
		 */
		private @Nullable StringQuote openQuote;
		
		/**
		 * Constructs a new quote tracker for the given quotes.<br>
		 *
		 * @param quoting The quotes to track
		 * @throws NullPointerException If the quoting is null
		 */
		protected QuoteTracker(@NonNull StringQuoting quoting) {
			this.quoting = Objects.requireNonNull(quoting, "Quoting must not be null");
		}
		
		/**
		 * Checks whether a quoted part is currently open.<br>
		 * @return True if a quoted part is open, otherwise false
		 */
		protected boolean isInQuote() {
			return this.openQuote != null;
		}
		
		/**
		 * Checks whether the given character opens a quoted part.<br>
		 * A character can only open a quoted part if no quoted part is open.<br>
		 *
		 * @param c The character to check
		 * @return True if the character opens a quoted part, otherwise false
		 */
		protected boolean opensQuote(char c) {
			return this.openQuote == null && this.quoting.isOpening(c);
		}
		
		@Override
		public boolean isNested() {
			return this.isInQuote();
		}
		
		@Override
		public void accept(char c) {
			if (this.openQuote == null) {
				this.openQuote = this.quoting.getQuote(c);
			} else if (c == this.openQuote.close()) {
				this.openQuote = null;
			}
		}
	}
}
