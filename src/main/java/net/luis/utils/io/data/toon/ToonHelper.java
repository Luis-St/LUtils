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

package net.luis.utils.io.data.toon;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.math.BigDecimal;
import java.util.*;

/**
 * Helper class for toon related operations.<br>
 *
 * @author Luis-St
 */
final class ToonHelper {
	
	/**
	 * Private constructor to prevent instantiation.<br>
	 * This is a static helper class.<br>
	 */
	private ToonHelper() {}
	
	/**
	 * Formats a key for toon output, quoting if necessary.<br>
	 * Keys matching the bare key pattern are left unquoted, others are quoted.<br>
	 *
	 * @param key The key to format
	 * @return The formatted key
	 * @throws NullPointerException If the given key is null
	 */
	static @NonNull String formatKey(@NonNull String key) {
		Objects.requireNonNull(key, "Key must not be null");
		if (isBareKey(key)) {
			return key;
		}
		return "\"" + escapeString(key) + "\"";
	}
	
	/**
	 * Checks if the given key is a valid bare key in TOON.<br>
	 * A bare key must match the pattern {@code ^[A-Za-z_][A-Za-z0-9_.]*$}.<br>
	 *
	 * @param key The key to check
	 * @return True if the key is a valid bare key, false otherwise
	 * @throws NullPointerException If the key is null
	 */
	@SuppressWarnings("DuplicatedCode")
	private static boolean isBareKey(@NonNull String key) {
		Objects.requireNonNull(key, "Key must not be null");
		if (key.isEmpty()) {
			return false;
		}
		
		char first = key.charAt(0);
		if (!((first >= 'A' && first <= 'Z') || (first >= 'a' && first <= 'z') || first == '_')) {
			return false;
		}
		
		for (int i = 1; i < key.length(); i++) {
			char c = key.charAt(i);
			if (!((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9') || c == '_' || c == '.')) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Escapes special characters in a string for toon quoted string output.<br>
	 * Escapes backslash, double quote, newline, carriage return, and tab.<br>
	 *
	 * @param str The string to escape
	 * @return The escaped string
	 * @throws NullPointerException If the given string is null
	 */
	static @NonNull String escapeString(@NonNull String str) {
		Objects.requireNonNull(str, "String must not be null");
		
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			
			switch (c) {
				case '\\' -> result.append("\\\\");
				case '"' -> result.append("\\\"");
				case '\n' -> result.append("\\n");
				case '\r' -> result.append("\\r");
				case '\t' -> result.append("\\t");
				default -> result.append(c);
			}
		}
		return result.toString();
	}
	
	/**
	 * Checks if a string value needs quoting in toon output.<br>
	 * A value must be quoted if it is empty, matches reserved keywords,<br>
	 * looks numeric, or contains characters that would be ambiguous.<br>
	 *
	 * @param value The string value to check
	 * @param delimiter The active delimiter character
	 * @return True if the value needs quoting, false otherwise
	 * @throws NullPointerException If the value is null
	 */
	static boolean needsQuoting(@NonNull String value, char delimiter) {
		Objects.requireNonNull(value, "Value must not be null");
		
		if (value.isEmpty()) {
			return true;
		}
		if ("true".equals(value) || "false".equals(value) || "null".equals(value)) {
			return true;
		}
		if (looksNumeric(value)) {
			return true;
		}
		if (value.startsWith("-") || value.startsWith(" ") || value.endsWith(" ")) {
			return true;
		}
		
		for (int i = 0; i < value.length(); i++) {
			char c = value.charAt(i);
			if (c == delimiter || c == ':' || c == '"' || c == '\\' || c == '[' || c == ']' || c == '{' || c == '}') {
				return true;
			}
			if (c < 0x20) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Checks if a string value looks like a numeric literal.<br>
	 *
	 * @param value The value to check
	 * @return True if the value looks numeric, false otherwise
	 * @throws NullPointerException If the value is null
	 */
	private static boolean looksNumeric(@NonNull String value) {
		Objects.requireNonNull(value, "Value must not be null");
		if (value.isEmpty()) {
			return false;
		}
		
		String trimmed = value;
		if (trimmed.startsWith("+") || trimmed.startsWith("-")) {
			trimmed = trimmed.substring(1);
		}
		if (trimmed.isEmpty()) {
			return false;
		}
		
		char first = trimmed.charAt(0);
		if (first >= '0' && first <= '9') {
			return true;
		}
		if (trimmed.startsWith(".") && trimmed.length() > 1) {
			char second = trimmed.charAt(1);
			return second >= '0' && second <= '9';
		}
		return false;
	}
	
	/**
	 * Formats a number in canonical toon form.<br>
	 * No exponent notation, no leading zeros, no trailing fractional zeros,<br>
	 * {@code -0} becomes {@code 0}, {@code NaN} and {@code Infinity} become {@code null}.<br>
	 *
	 * @param number The number to format
	 * @return The canonical string representation
	 * @throws NullPointerException If the number is null
	 */
	static @NonNull String formatNumber(@NonNull Number number) {
		Objects.requireNonNull(number, "Number must not be null");
		
		return switch (number) {
			case Double d -> {
				if (d.isNaN() || d.isInfinite()) {
					yield "null";
				}
				if (d == 0.0 && Double.doubleToRawLongBits(d) != 0L) {
					yield "0.0";
				}
				yield formatDecimal(BigDecimal.valueOf(d));
			}
			case Float f -> {
				if (f.isNaN() || f.isInfinite()) {
					yield "null";
				}
				if (f == 0.0f && Float.floatToRawIntBits(f) != 0) {
					yield "0.0";
				}
				yield formatDecimal(BigDecimal.valueOf(f.doubleValue()));
			}
			case BigDecimal bd -> formatDecimal(bd);
			default -> number.toString();
		};
	}
	
	/**
	 * Formats a BigDecimal in canonical form without exponent notation.<br>
	 *
	 * @param bd The BigDecimal to format
	 * @return The formatted string
	 * @throws NullPointerException If the BigDecimal is null
	 */
	private static @NonNull String formatDecimal(@NonNull BigDecimal bd) {
		Objects.requireNonNull(bd, "BigDecimal must not be null");
		
		bd = bd.stripTrailingZeros();
		if (bd.scale() <= 0) {
			String plain = bd.toPlainString();
			if (!plain.contains(".")) {
				return plain + ".0";
			}
			return plain;
		}
		return bd.toPlainString();
	}
	
	/**
	 * Checks if a toon array is eligible for tabular formatting.<br>
	 * A tabular array requires all elements to be objects with identical key sets<br>
	 * and all values to be primitives or null.<br>
	 *
	 * @param array The array to check
	 * @return True if the array is eligible for tabular formatting, false otherwise
	 * @throws NullPointerException If the array is null
	 */
	static boolean isTabularEligible(@NonNull ToonArray array) {
		Objects.requireNonNull(array, "Array must not be null");
		if (array.isEmpty()) {
			return false;
		}
		
		Set<String> referenceKeys = null;
		for (ToonElement element : array) {
			if (!(element instanceof ToonObject obj)) {
				return false;
			}
			if (obj.isEmpty()) {
				return false;
			}
			
			Set<String> keys = obj.keySet();
			if (referenceKeys == null) {
				referenceKeys = new LinkedHashSet<>(keys);
			} else if (!referenceKeys.equals(keys)) {
				return false;
			}
			
			for (Map.Entry<String, ToonElement> entry : obj) {
				ToonElement value = entry.getValue();
				if (!(value instanceof ToonValue) && !(value instanceof ToonNull)) {
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * Infers the type of an unquoted token value.<br>
	 * Returns a boolean for "true"/"false", null for "null",<br>
	 * a number for numeric strings, or the raw string otherwise.<br>
	 *
	 * @param token The token to infer the type of
	 * @return The inferred toon element
	 * @throws NullPointerException If the token is null
	 */
	static @NonNull ToonElement inferType(@NonNull String token) {
		Objects.requireNonNull(token, "Token must not be null");
		
		switch (token) {
			case "true" -> {
				return new ToonValue(true);
			}
			case "false" -> {
				return new ToonValue(false);
			}
			case "null" -> {
				return ToonNull.INSTANCE;
			}
		}
		
		Number number = tryParseNumber(token);
		if (number != null) {
			return new ToonValue(number);
		}
		return new ToonValue(token);
	}
	
	/**
	 * Attempts to parse a string as a number.<br>
	 *
	 * @param value The string to parse
	 * @return The parsed number, or null if not a valid number
	 * @throws NullPointerException If the value is null
	 */
	private static @Nullable Number tryParseNumber(@NonNull String value) {
		Objects.requireNonNull(value, "Value must not be null");
		if (value.isEmpty()) {
			return null;
		}
		
		try {
			if (value.contains(".") || value.contains("e") || value.contains("E")) {
				return Double.parseDouble(value);
			}
			return Long.parseLong(value);
		} catch (NumberFormatException _) {
			return null;
		}
	}
	
	/**
	 * Checks if a key segment is valid for key folding.<br>
	 * A foldable key must match the pattern {@code ^[A-Za-z_][A-Za-z0-9_]*$}.<br>
	 *
	 * @param key The key segment to check
	 * @return True if the key is valid for folding, false otherwise
	 * @throws NullPointerException If the key is null
	 */
	@SuppressWarnings("DuplicatedCode")
	static boolean canFoldKey(@NonNull String key) {
		Objects.requireNonNull(key, "Key must not be null");
		if (key.isEmpty()) {
			return false;
		}
		
		char first = key.charAt(0);
		if (!((first >= 'A' && first <= 'Z') || (first >= 'a' && first <= 'z') || first == '_')) {
			return false;
		}
		
		for (int i = 1; i < key.length(); i++) {
			char c = key.charAt(i);
			if (!((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9') || c == '_')) {
				return false;
			}
		}
		return true;
	}
}
