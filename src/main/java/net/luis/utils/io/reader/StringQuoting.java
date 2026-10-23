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

import com.google.common.collect.Sets;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.*;

/**
 * A record to define which quotes a {@link QuotedStringReader} handles.<br>
 * Each quote is identified by its opening character, so the opening characters of the quotes must be unique.<br>
 *
 * @author Luis-St
 *
 * @param quotes The handled quotes
 */
public record StringQuoting(@NonNull List<StringQuote> quotes) {
	
	/**
	 * Constant quoting which handles single and double quotes.<br>
	 */
	public static final StringQuoting DEFAULT = of(StringQuote.SINGLE_QUOTE, StringQuote.DOUBLE_QUOTE);
	/**
	 * Constant quoting which handles only single quotes.<br>
	 */
	public static final StringQuoting SINGLE_ONLY = of(StringQuote.SINGLE_QUOTE);
	/**
	 * Constant quoting which handles only double quotes.<br>
	 */
	public static final StringQuoting DOUBLE_ONLY = of(StringQuote.DOUBLE_QUOTE);
	/**
	 * Constant quoting which handles no quotes.<br>
	 */
	public static final StringQuoting NONE = of();
	
	/**
	 * Constructs a new quoting with the given quotes.<br>
	 *
	 * @param quotes The handled quotes
	 * @throws NullPointerException If the list of quotes or any quote is null
	 * @throws IllegalArgumentException If two quotes have the same opening character
	 */
	public StringQuoting {
		quotes = List.copyOf(Objects.requireNonNull(quotes, "Quotes must not be null"));
		
		Set<Character> openings = Sets.newHashSet();
		for (StringQuote quote : quotes) {
			if (!openings.add(quote.open())) {
				throw new IllegalArgumentException("Quotes must have unique opening characters, but found '" + quote.open() + "' multiple times");
			}
		}
	}
	
	/**
	 * Creates a new quoting with the given quotes.<br>
	 *
	 * @param quotes The handled quotes
	 * @return The created quoting
	 * @throws NullPointerException If the quotes array or any quote is null
	 * @throws IllegalArgumentException If two quotes have the same opening character
	 */
	public static @NonNull StringQuoting of(StringQuote @NonNull ... quotes) {
		Objects.requireNonNull(quotes, "Quotes must not be null");
		return new StringQuoting(Arrays.asList(quotes));
	}
	
	/**
	 * Returns the quote which is opened by the given character.<br>
	 *
	 * @param c The character
	 * @return The quote which is opened by the character, or null if the character does not open a quote
	 */
	public @Nullable StringQuote getQuote(char c) {
		for (StringQuote quote : this.quotes) {
			if (quote.open() == c) {
				return quote;
			}
		}
		return null;
	}
	
	/**
	 * Checks whether the given character opens a quote.<br>
	 *
	 * @param c The character
	 * @return True if the character opens a quote, otherwise false
	 */
	public boolean isOpening(char c) {
		return this.getQuote(c) != null;
	}
}
