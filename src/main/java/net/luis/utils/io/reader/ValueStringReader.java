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
import net.luis.utils.math.NumberType;
import net.luis.utils.math.Radix;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.io.Reader;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * A string reader which reads single values from a string.<br>
 * The reader can read the following types of values:<br>
 * <ul>
 *     <li>Booleans</li>
 *     <li>Numbers (byte, short, integer, long, float, double)</li>
 *     <li>Big numbers (BigInteger, BigDecimal)</li>
 *     <li>Objects, which are the best matching value of the types above or a string</li>
 * </ul>
 * <p>
 *     Numbers can be quoted with any quote handled by the reader.
 * </p>
 *
 * @author Luis-St
 */
public class ValueStringReader extends QuotedStringReader {
	
	/**
	 * Constructs a new value string reader with the given string.<br>
	 * The reader uses the {@link #DEFAULT_ESCAPE_CHARACTER default escape character} and the {@link StringQuoting#DEFAULT default quoting}.<br>
	 *
	 * @param string The string to read from
	 * @throws NullPointerException If the string is null
	 */
	public ValueStringReader(@NonNull String string) {
		super(string);
	}
	
	/**
	 * Constructs a new value string reader with the given string, escape character, and quoting.<br>
	 *
	 * @param string The string to read from
	 * @param escapeCharacter The escape character, or null to disable escaping
	 * @param quoting The quotes handled by the reader
	 * @throws NullPointerException If the string or quoting is null
	 */
	public ValueStringReader(@NonNull String string, @Nullable Character escapeCharacter, @NonNull StringQuoting quoting) {
		super(string, escapeCharacter, quoting);
	}
	
	/**
	 * Constructs a new value string reader from the given reader.<br>
	 * The reader is closed after reading the string.<br>
	 * The reader uses the {@link #DEFAULT_ESCAPE_CHARACTER default escape character} and the {@link StringQuoting#DEFAULT default quoting}.<br>
	 *
	 * @param reader The reader to read from
	 * @throws NullPointerException If the reader is null
	 * @throws UncheckedIOException If an I/O error occurs while reading the string
	 */
	public ValueStringReader(@NonNull Reader reader) {
		super(reader);
	}
	
	/**
	 * Constructs a new value string reader from the given reader with the given escape character and quoting.<br>
	 * The reader is closed after reading the string.<br>
	 *
	 * @param reader The reader to read from
	 * @param escapeCharacter The escape character, or null to disable escaping
	 * @param quoting The quotes handled by the reader
	 * @throws NullPointerException If the reader or quoting is null
	 * @throws UncheckedIOException If an I/O error occurs while reading the string
	 */
	public ValueStringReader(@NonNull Reader reader, @Nullable Character escapeCharacter, @NonNull StringQuoting quoting) {
		super(reader, escapeCharacter, quoting);
	}
	
	/**
	 * Creates the reader which reads a quoted number.<br>
	 * The created reader uses the same escape character and quoting as this reader.<br>
	 *
	 * @param string The string to read from
	 * @return The created reader
	 * @throws NullPointerException If the string is null
	 */
	private @NonNull ValueStringReader createNumberReader(@NonNull String string) {
		return new ValueStringReader(string, this.getEscapeCharacter(), this.getQuoting());
	}
	
	/**
	 * Reads a boolean.<br>
	 * The boolean is read as an unquoted string and then parsed case-insensitive.<br>
	 *
	 * @return The boolean value which was read
	 * @throws ConcurrentInteractionException If a child reader is active
	 * @throws IllegalStateException If this reader is a closed sub reader
	 * @throws StringIndexOutOfBoundsException If there are no more characters to read
	 * @throws InvalidStringException If the read value is not a boolean
	 */
	public boolean readBoolean() {
		this.ensureUsable();
		if (!this.canRead()) {
			throw new StringIndexOutOfBoundsException(this.withLocation("Expected a boolean value but found nothing"));
		}
		
		char c = Character.toLowerCase(this.peek());
		String value;
		if (c == 't') {
			value = this.readExpected("true", false);
		} else if (c == 'f') {
			value = this.readExpected("false", false);
		} else {
			throw new InvalidStringException(this.withLocation("Expected a start character of 'true' or 'false' but found: '" + c + "'"));
		}
		
		if ("true".equalsIgnoreCase(value)) {
			return true;
		} else if ("false".equalsIgnoreCase(value)) {
			return false;
		}
		throw new InvalidStringException(this.withLocation("Expected a boolean value but found: '" + value + "'"));
	}
	
	/**
	 * Reads a number as a string of the given type.<br>
	 * The initial type is used to determine the type of the number,<br>
	 * it can be overridden by a larger number type (e.g., reading a byte as a long).<br>
	 * <p>
	 *     The number can be quoted or unquoted.<br>
	 *     The first character can be a minus or plus sign.<br>
	 *     The number can contain a single dot for float or double values.<br>
	 *     The number can contain a type suffix (single character, case-insensitive) at the end of the number:
	 * </p>
	 * <ul>
	 *     <li>byte: 'b'</li>
	 *     <li>short: 's'</li>
	 *     <li>integer: 'i'</li>
	 *     <li>long: 'l'</li>
	 *     <li>float: 'f'</li>
	 *     <li>double: 'd'</li>
	 * </ul>
	 * <p>
	 *     The number can be specified in:
	 * </p>
	 * <ul>
	 *     <li>binary by prefixing with '0b'</li>
	 *     <li>octal by prefixing with '0'</li>
	 *     <li>decimal by default</li>
	 *     <li>hexadecimal by prefixing with '0x'</li>
	 * </ul>
	 * <p>
	 *     The number can contain underscores to separate digits.<br>
	 *     Floating point numbers can contain an exponent with 'e' or 'p' for hexadecimal floating point numbers.<br>
	 *     Leading zeros of a floating point number do not make it octal, the number stays decimal.
	 * </p>
	 *
	 * @param initialType The initial number type to read as
	 * @return The number as a string which was read
	 * @throws ConcurrentInteractionException If a child reader is active
	 * @throws IllegalStateException If this reader is a closed sub reader
	 * @throws InvalidStringException If the read value is not a (valid) number
	 */
	private @Nullable ParsedNumber readNumberAsString(@Nullable NumberType initialType) {
		this.ensureUsable();
		if (!this.canRead()) {
			return null;
		}
		
		char next = this.peek();
		if (this.isQuote(next)) {
			int start = this.getIndex();
			this.readQuotedString();
			int end = this.getIndex();
			
			this.setIndex(start + 1);
			ParsedNumber number;
			try (ValueStringReader reader = this.createSubReader(end - start - 2, this::createNumberReader)) {
				number = reader.readNumberAsString(initialType);
			}
			this.setIndex(end);
			return number;
		}
		
		char sign = '+';
		StringBuilder builder = new StringBuilder();
		if (next == '-' || next == '+') {
			sign = this.read();
		}
		
		boolean exponent = false;
		Radix radix = Radix.DECIMAL;
		NumberType type = initialType == null ? NumberType.LONG : initialType; // Largest none infinite integer type
		while (this.canRead()) {
			char c = Character.toLowerCase(this.peek());
			if (c == '_') {
				if (builder.isEmpty()) {
					throw new InvalidStringException(this.withLocation("Underscores must not be at the beginning of a number"));
				}
				this.skip();
				continue;
			}
			
			if (Character.isDigit(c)) {
				if (builder.length() == 1 && builder.charAt(0) == '0' && radix == Radix.DECIMAL) {
					radix = Radix.OCTAL;
				}
			} else if (c == '.') {
				if (type.isFloatingPoint() && builder.toString().contains(".")) {
					throw new InvalidStringException(this.withLocation("Multiple dots in a number are not allowed: '" + sign + builder + c + "'"));
				}
				if (!type.isFloatingPoint()) {
					type = NumberType.DOUBLE; // Largest none infinite floating point type
				}
				if (radix == Radix.OCTAL) {
					radix = Radix.DECIMAL; // Leading zeros of a floating point number are decimal
				}
				if (!type.isSupportedRadix(radix)) {
					throw new InvalidStringException(this.withLocation("Try to read number of type '" + type + "' which does not support radix '" + radix + "': '" + sign + builder + c + "'"));
				}
			} else if (c == 'x') {
				if (radix != Radix.DECIMAL) {
					throw new InvalidStringException(this.withLocation("Found invalid 'x' in number of type '" + type + "' with radix '" + radix + "': '" + sign + builder + c + "'"));
				}
				if (builder.length() != 1 || builder.charAt(0) != '0') {
					throw new InvalidStringException(this.withLocation("Found invalid 'x' in number of type '" + type + "' with radix '" + radix + "': '" + sign + builder + c + "'"));
				}
				
				radix = Radix.HEXADECIMAL;
				builder.setLength(0);
				this.skip();
				continue;
			} else if (c == 'b' && radix != Radix.HEXADECIMAL) { // 'b' is a digit in hexadecimal numbers
				if (radix == Radix.DECIMAL && builder.length() == 1 && builder.charAt(0) == '0') {
					radix = Radix.BINARY;
					builder.setLength(0);
					this.skip();
					continue;
				}
				break; // 'b' is a valid suffix for byte
			} else if ((c == 'e' && radix != Radix.HEXADECIMAL) || c == 'p') { // 'e' is a digit in hexadecimal numbers, their exponent is 'p'
				if (exponent) {
					throw new InvalidStringException(this.withLocation("Found invalid '" + c + "' exponent in number of type '" + type + "' with radix '" + radix + "': '" + sign + builder + c + "'"));
				}
				if (!type.isFloatingPoint()) {
					type = NumberType.DOUBLE; // Largest none infinite floating point type
				}
				if (radix == Radix.OCTAL) {
					radix = Radix.DECIMAL; // Leading zeros of a floating point number are decimal
				}
				if ('e' == c && radix != Radix.DECIMAL) {
					throw new InvalidStringException(this.withLocation("Found invalid 'e' exponent in number of type '" + type + "' with radix '" + radix + "' expected 'decimal' radix: '" + sign + builder + c + "'"));
				}
				if ('p' == c && radix != Radix.HEXADECIMAL) {
					throw new InvalidStringException(this.withLocation("Found invalid 'p' exponent in number of type '" + type + "' with radix '" + radix + "' expected 'hexadecimal' radix: '" + sign + builder + c + "'"));
				}
				exponent = true;
			} else if (c == '+' || c == '-') {
				String message = "Found invalid '" + c + "' sign in number of type '" + type + "' with radix '" + radix + "' and missing exponent: '" + sign + builder + c + "'";
				if (!exponent) {
					throw new InvalidStringException(this.withLocation(message));
				}
				
				char last = Character.toLowerCase(builder.charAt(builder.length() - 1));
				if (last != 'e' && last != 'p') {
					throw new InvalidStringException(this.withLocation(message));
				}
			} else {
				if (radix != Radix.HEXADECIMAL) {
					break;
				}
				if (c < 'a' || 'f' < c) {
					break;
				}
			}
			builder.append(this.read());
		}
		
		if (exponent && "ep+-".indexOf(Character.toLowerCase(builder.charAt(builder.length() - 1))) >= 0) {
			throw new InvalidStringException(this.withLocation("Found exponent without digits in number of type '" + type + "': '" + sign + builder + "'"));
		}
		
		NumberType suffixType = this.canRead() ? NumberType.getBySuffix(Character.toLowerCase(this.peek())) : null;
		if (radix == Radix.OCTAL) {
			if (suffixType != null && suffixType.isFloatingPoint()) {
				radix = Radix.DECIMAL;
			} else if (builder.chars().anyMatch(digit -> digit == '8' || digit == '9')) {
				throw new InvalidStringException(this.withLocation("Found invalid digit in octal number, only the digits 0 to 7 are allowed: '" + sign + builder + "'"));
			}
		}
		
		if (this.canRead()) {
			char c = Character.toLowerCase(this.peek());
			NumberType newType = NumberType.getBySuffix(c);
			
			if (newType != null) {
				this.skip();
				
				if (!newType.isSupportedRadix(radix)) {
					throw new InvalidStringException(this.withLocation("Try to read number of type '" + newType + "' which does not support radix '" + radix + "': '" + sign + builder + c + "'"));
				}
				if (!newType.canConvertTo(sign + builder.toString(), radix)) {
					throw new InvalidStringException(this.withLocation("Unable to convert number of type '" + type + "' to type '" + newType + "': '" + sign + builder + c + "'"));
				}
				type = newType;
			}
		}
		return new ParsedNumber(sign, builder.toString(), type, radix);
	}
	
	/**
	 * Reads a number.<br>
	 * If the number is specified with a type suffix, the number is parsed as the specified type.<br>
	 * By default, the number is parsed as a double if it contains a dot otherwise as a long.<br>
	 *
	 * @return The number value which was read
	 * @throws ConcurrentInteractionException If a child reader is active
	 * @throws IllegalStateException If this reader is a closed sub reader
	 * @throws StringIndexOutOfBoundsException If there are no more characters to read
	 * @throws InvalidStringException If the read value is not a (valid) number
	 * @see #readNumberAsString(NumberType)
	 */
	public @NonNull Number readNumber() {
		this.ensureUsable();
		if (!this.canRead()) {
			throw new StringIndexOutOfBoundsException(this.withLocation("Expected a number but found nothing"));
		}
		
		ParsedNumber number = this.readNumberAsString(null);
		if (number == null || number.value().isEmpty()) {
			throw new InvalidStringException(this.withLocation("Expected a number but found nothing"));
		}
		return number.type().parseNumber(number.getSignedValue(), number.radix());
	}
	
	/**
	 * Reads a number of the given type.<br>
	 *
	 * @param type The type of the number to read
	 * @param name The name of the value used in error messages
	 * @return The number value which was read
	 * @param <T> The type of the number
	 * @throws NullPointerException If the type or name is null
	 * @throws ConcurrentInteractionException If a child reader is active
	 * @throws IllegalStateException If this reader is a closed sub reader
	 * @throws StringIndexOutOfBoundsException If there are no more characters to read
	 * @throws InvalidStringException If the read value is not a (valid) number of the given type
	 * @see #readNumberAsString(NumberType)
	 */
	private <T extends Number> @NonNull T readNumberOfType(@NonNull NumberType type, @NonNull String name) {
		Objects.requireNonNull(type, "Number type must not be null");
		Objects.requireNonNull(name, "Name must not be null");
		this.ensureUsable();
		
		String description = ("aeiou".indexOf(name.charAt(0)) >= 0 ? "an " : "a ") + name;
		if (!this.canRead()) {
			throw new StringIndexOutOfBoundsException(this.withLocation("Expected " + description + " but found nothing"));
		}
		
		ParsedNumber number = this.readNumberAsString(type);
		if (number == null || number.value().isEmpty()) {
			throw new InvalidStringException(this.withLocation("Expected " + description + " but found nothing"));
		}
		
		if (number.type() != type) {
			throw new InvalidStringException(this.withLocation("Expected " + description + " but found: '" + number + "'"));
		}
		
		try {
			return type.parseNumber(number.getSignedValue(), number.radix());
		} catch (NumberFormatException e) {
			throw new InvalidStringException(this.withLocation("Unable to parse " + name + ": '" + number + "'"), e);
		}
	}
	
	/**
	 * Reads a byte.<br>
	 * The number can be suffixed with a {@code b} (case-insensitive) to indicate that it is a byte.<br>
	 *
	 * @return The byte value which was read
	 * @throws ConcurrentInteractionException If a child reader is active
	 * @throws IllegalStateException If this reader is a closed sub reader
	 * @throws StringIndexOutOfBoundsException If there are no more characters to read
	 * @throws InvalidStringException If the read value is not a (valid) byte
	 * @see #readNumberAsString(NumberType)
	 */
	public byte readByte() {
		return this.readNumberOfType(NumberType.BYTE, "byte value");
	}
	
	/**
	 * Reads a short.<br>
	 * The number can be suffixed with an {@code s} (case-insensitive) to indicate that it is a short.<br>
	 *
	 * @return The short value which was read
	 * @throws ConcurrentInteractionException If a child reader is active
	 * @throws IllegalStateException If this reader is a closed sub reader
	 * @throws StringIndexOutOfBoundsException If there are no more characters to read
	 * @throws InvalidStringException If the read value is not a (valid) short
	 * @see #readNumberAsString(NumberType)
	 */
	public short readShort() {
		return this.readNumberOfType(NumberType.SHORT, "short value");
	}
	
	/**
	 * Reads an integer.<br>
	 * The number can be suffixed with an {@code i} (case-insensitive) to indicate that it is an integer.<br>
	 *
	 * @return The integer value which was read
	 * @throws ConcurrentInteractionException If a child reader is active
	 * @throws IllegalStateException If this reader is a closed sub reader
	 * @throws StringIndexOutOfBoundsException If there are no more characters to read
	 * @throws InvalidStringException If the read value is not a (valid) integer
	 * @see #readNumberAsString(NumberType)
	 */
	public int readInt() {
		return this.readNumberOfType(NumberType.INTEGER, "integer value");
	}
	
	/**
	 * Reads a long.<br>
	 * The number can be suffixed with an {@code l} (case-insensitive) to indicate that it is a long.<br>
	 *
	 * @return The long value which was read
	 * @throws ConcurrentInteractionException If a child reader is active
	 * @throws IllegalStateException If this reader is a closed sub reader
	 * @throws StringIndexOutOfBoundsException If there are no more characters to read
	 * @throws InvalidStringException If the read value is not a (valid) long
	 * @see #readNumberAsString(NumberType)
	 */
	public long readLong() {
		return this.readNumberOfType(NumberType.LONG, "long value");
	}
	
	/**
	 * Reads a float.<br>
	 * The number can be suffixed with an {@code f} (case-insensitive) to indicate that it is a float.<br>
	 *
	 * @return The float value which was read
	 * @throws ConcurrentInteractionException If a child reader is active
	 * @throws IllegalStateException If this reader is a closed sub reader
	 * @throws StringIndexOutOfBoundsException If there are no more characters to read
	 * @throws InvalidStringException If the read value is not a (valid) float
	 * @see #readNumberAsString(NumberType)
	 */
	public float readFloat() {
		return this.readNumberOfType(NumberType.FLOAT, "float value");
	}
	
	/**
	 * Reads a double.<br>
	 * The number can be suffixed with a {@code d} (case-insensitive) to indicate that it is a double.<br>
	 *
	 * @return The double value which was read
	 * @throws ConcurrentInteractionException If a child reader is active
	 * @throws IllegalStateException If this reader is a closed sub reader
	 * @throws StringIndexOutOfBoundsException If there are no more characters to read
	 * @throws InvalidStringException If the read value is not a (valid) double
	 * @see #readNumberAsString(NumberType)
	 */
	public double readDouble() {
		return this.readNumberOfType(NumberType.DOUBLE, "double value");
	}
	
	/**
	 * Reads a big integer.<br>
	 *
	 * @return The big integer value which was read
	 * @throws ConcurrentInteractionException If a child reader is active
	 * @throws IllegalStateException If this reader is a closed sub reader
	 * @throws StringIndexOutOfBoundsException If there are no more characters to read
	 * @throws InvalidStringException If the read value is not a (valid) big integer
	 * @see #readNumberAsString(NumberType)
	 */
	public @NonNull BigInteger readBigInteger() {
		return this.readNumberOfType(NumberType.BIG_INTEGER, "big integer value");
	}
	
	/**
	 * Reads a big decimal.<br>
	 *
	 * @return The big decimal value which was read
	 * @throws ConcurrentInteractionException If a child reader is active
	 * @throws IllegalStateException If this reader is a closed sub reader
	 * @throws StringIndexOutOfBoundsException If there are no more characters to read
	 * @throws InvalidStringException If the read value is not a (valid) big decimal
	 * @see #readNumberAsString(NumberType)
	 */
	public @NonNull BigDecimal readBigDecimal() {
		return this.readNumberOfType(NumberType.BIG_DECIMAL, "big decimal value");
	}
	
	/**
	 * Reads the next value as the best matching object.<br>
	 * The type of the value is determined in the following order:<br>
	 * <ol>
	 *     <li>
	 *         <b>Quoted values</b>:<br>
	 *         If the next character is a handled quote, the value is read as quoted string.<br>
	 *         If the value is enclosed in single quotes and consists of exactly one character, a {@link Character} is returned,<br>
	 *         otherwise a {@link String} is returned (see {@link #readQuotedString()}).
	 *     </li>
	 *     <li>
	 *         <b>Booleans</b>:<br>
	 *         If the next characters are {@code true} or {@code false} (case-insensitive)<br>
	 *         and are not followed by another word character, a {@link Boolean} is returned.
	 *     </li>
	 *     <li>
	 *         <b>Numbers</b>:<br>
	 *         If the next character is a digit, a sign, or a dot and the value can be read as a number<br>
	 *         which is not followed by another word character, a {@link Number} is returned (see {@link #readNumber()}).<br>
	 *         Numbers with a type suffix are returned as the specified type.<br>
	 *         Numbers without a type suffix are returned as {@link Long} or {@link Double}.<br>
	 *         If an integer number without suffix exceeds the range of a long, a {@link BigInteger} is returned.<br>
	 *         If a decimal floating point number without suffix exceeds the range of a double, a {@link BigDecimal} is returned.
	 *     </li>
	 *     <li>
	 *         <b>Unquoted strings</b>:<br>
	 *         If none of the above matches, the value is read as unquoted string (see {@link #readUnquotedString()}).
	 *     </li>
	 * </ol>
	 * <p>
	 *     A word character is a letter, a digit, an underscore, or a dot.<br>
	 *     If reading a boolean or number fails, the reader index is restored before the next type is tried.<br>
	 *     This method does not modify the marked index of the reader.
	 * </p>
	 *
	 * @return The object which was read
	 * @throws ConcurrentInteractionException If a child reader is active
	 * @throws IllegalStateException If this reader is a closed sub reader
	 * @throws StringIndexOutOfBoundsException If there are no more characters to read
	 * @see #readQuotedString()
	 * @see #readBoolean()
	 * @see #readNumber()
	 * @see #readUnquotedString()
	 */
	public @NonNull Object readObject() {
		this.ensureUsable();
		if (!this.canRead()) {
			throw new StringIndexOutOfBoundsException(this.withLocation("Expected a value but found nothing"));
		}
		
		char next = this.peek();
		if (this.isQuote(next)) {
			String value = this.readQuotedString();
			if (next == '\'' && value.length() == 1) {
				return value.charAt(0);
			}
			
			return value;
		}
		
		Predicate<Integer> isBoundary = i -> {
			if (i >= this.getEndIndex()) {
				return true;
			}
			
			char c = this.getString().charAt(i);
			return !Character.isLetterOrDigit(c) && c != '_' && c != '.';
		};
		
		int start = this.getIndex();
		char lower = Character.toLowerCase(next);
		if (lower == 't' || lower == 'f') {
			String word = Boolean.toString(lower == 't');
			
			if (word.equalsIgnoreCase(this.peek(word.length())) && isBoundary.test(start + word.length())) {
				this.skip(word.length());
				return lower == 't';
			}
		}
		
		if (Character.isDigit(next) || next == '+' || next == '-' || next == '.') {
			try {
				ParsedNumber number = this.readNumberAsString(null);
				
				if (number != null && !number.value().isEmpty() && isBoundary.test(this.getIndex())) {
					char last = Character.toLowerCase(this.getString().charAt(this.getIndex() - 1));
					boolean explicitSuffix = NumberType.getBySuffix(last) != null && (number.radix() != Radix.HEXADECIMAL || Character.digit(last, 16) == -1);
					
					try {
						Number value = number.type().parseNumber(number.getSignedValue(), number.radix());
						if (!explicitSuffix && value instanceof Double d && d.isInfinite() && number.radix() == Radix.DECIMAL) {
							return NumberType.BIG_DECIMAL.parseNumber(number.getSignedValue(), number.radix());
						}
						return value;
					} catch (NumberFormatException e) {
						if (explicitSuffix || number.type() != NumberType.LONG) {
							throw e;
						}
						return NumberType.BIG_INTEGER.parseNumber(number.getSignedValue(), number.radix());
					}
				}
			} catch (InvalidStringException | NumberFormatException _) {}
			
			this.setIndex(start);
		}
		
		return this.readUnquotedString();
	}
	
	//region Internal
	
	/**
	 * Internal to represent a parsed number.<br>
	 *
	 * @param sign The sign of the number
	 * @param value The value of the number
	 * @param type The type of the number
	 * @param radix The radix of the number
	 */
	private record ParsedNumber(char sign, @NonNull String value, @NonNull NumberType type, @NonNull Radix radix) {
		
		/**
		 * Constructs a new parsed number.<br>
		 *
		 * @param sign The sign of the number
		 * @param value The value of the number
		 * @param type The type of the number
		 * @param radix The radix of the number
		 * @throws NullPointerException If the value, type, or radix is null
		 */
		private ParsedNumber {
			Objects.requireNonNull(value, "Value must not be null");
			Objects.requireNonNull(type, "Type must not be null");
			Objects.requireNonNull(radix, "Radix must not be null");
		}
		
		/**
		 * Returns the signed value of the number.<br>
		 * The value is prefixed with the sign.<br>
		 *
		 * @return The signed value
		 */
		public @NonNull String getSignedValue() {
			return this.sign + this.value;
		}
		
		//region Object overrides
		@Override
		public @NonNull String toString() {
			return this.sign + this.value + " of type " + this.type + " with radix " + this.radix;
		}
		//endregion
	}
	//endregion
}
