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

package net.luis.utils.math;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Represents a type of number that can be parsed from a string.<br>
 * This enum contains the java default number types and their respective parsers.<br>
 *
 * @author Luis-St
 */
public enum NumberType {
	
	/**
	 * Represents a {@link Byte} number type.<br>
	 */
	BYTE(Byte.class, Byte.SIZE, Byte.MIN_VALUE, Byte.MAX_VALUE, false, 'b', Set.of(Radix.values()), wrapIntegerParser(Byte::parseByte)),
	/**
	 * Represents a {@link Short} number type.<br>
	 */
	SHORT(Short.class, Short.SIZE, Short.MIN_VALUE, Short.MAX_VALUE, false, 's', Set.of(Radix.values()), wrapIntegerParser(Short::parseShort)),
	/**
	 * Represents a {@link Integer} number type.<br>
	 */
	INTEGER(Integer.class, Integer.SIZE, Integer.MIN_VALUE, Integer.MAX_VALUE, false, 'i', Set.of(Radix.values()), wrapIntegerParser(Integer::parseInt)),
	/**
	 * Represents a {@link Long} number type.<br>
	 */
	LONG(Long.class, Long.SIZE, Long.MIN_VALUE, Long.MAX_VALUE, false, 'l', Set.of(Radix.values()), wrapIntegerParser(Long::parseLong)),
	/**
	 * Represents a {@link BigInteger} number type.<br>
	 */
	BIG_INTEGER(BigInteger.class, -1, null, null, false, '\0', Set.of(Radix.values()), wrapIntegerParser(BigInteger::new)),
	/**
	 * Represents a {@link Float} number type.<br>
	 */
	FLOAT(Float.class, Float.SIZE, -Float.MAX_VALUE, Float.MAX_VALUE, true, 'f', Set.of(Radix.DECIMAL, Radix.HEXADECIMAL), wrapFloatingPointParser(Float::parseFloat)),
	/**
	 * Represents a {@link Double} number type.<br>
	 */
	DOUBLE(Double.class, Double.SIZE, -Double.MAX_VALUE, Double.MAX_VALUE, true, 'd', Set.of(Radix.DECIMAL, Radix.HEXADECIMAL), wrapFloatingPointParser(Double::parseDouble)),
	/**
	 * Represents a {@link BigDecimal} number type.<br>
	 */
	BIG_DECIMAL(BigDecimal.class, -1, null, null, true, '\0', Set.of(Radix.DECIMAL, Radix.HEXADECIMAL), createBigDecimalParser());
	
	/**
	 * The java number class that this type represents.<br>
	 */
	private final Class<? extends Number> numberClass;
	/**
	 * The bit size of the number type.<br>
	 * -1 if the number type is infinite (big integer or big decimal).<br>
	 */
	private final int bitSize;
	/**
	 * The minimum value of the number type.<br>
	 * Null if the number type is infinite (big integer or big decimal).<br>
	 */
	private final @Nullable Number minValue;
	/**
	 * The maximum value of the number type.<br>
	 * Null if the number type is infinite (big integer or big decimal).<br>
	 */
	private final @Nullable Number maxValue;
	/**
	 * Flag indicating if the number type is a floating point number.<br>
	 */
	private final boolean floatingPoint;
	/**
	 * The suffix of the number type.<br>
	 * If the number does not have a suffix by default in java, it will be the first character of the number type name.<br>
	 * {@code '\0'} if the number type does not have a suffix.<br>
	 */
	private final char suffix;
	/**
	 * The radices supported by the number type.<br>
	 */
	private final Set<Radix> supportedRadices;
	/**
	 * The parser function that converts a string to a number of this type based on the radix.<br>
	 */
	private final BiFunction<String, Radix, ? extends Number> parser;
	
	/**
	 * Constructs a new number type with the given parameters.<br>
	 *
	 * @param numberClass The java number class that this type represents
	 * @param bitSize The bit size of the number type
	 * @param minValue The minimum value of the number type
	 * @param maxValue The maximum value of the number type
	 * @param floatingPoint Flag indicating if the number type is a floating point number
	 * @param suffix The suffix of the number type
	 * @param supportedRadices The radices supported by the number type
	 * @param radixParser The parser function that converts a string to a number of this type based on the radix
	 * @throws IllegalArgumentException If the minimum and maximum values are not both null or not null
	 * @throws NullPointerException If any of the parameters are null
	 */
	NumberType(@NotNull Class<? extends Number> numberClass, int bitSize, @Nullable Number minValue, @Nullable Number maxValue, boolean floatingPoint, char suffix, @NotNull Set<Radix> supportedRadices, @NotNull BiFunction<String, Radix, ? extends Number> radixParser) {
		this.numberClass = Objects.requireNonNull(numberClass, "Number class must not be null");
		this.bitSize = bitSize;
		this.minValue = minValue;
		this.maxValue = maxValue;
		if (this.minValue == null ^ this.maxValue == null) {
			throw new IllegalArgumentException("Both min and max value must be null or not null");
		}
		this.floatingPoint = floatingPoint;
		this.suffix = suffix;
		this.supportedRadices = Objects.requireNonNull(supportedRadices, "Supported radices must not be null");
		this.parser = Objects.requireNonNull(radixParser, "Radix parser must not be null");
	}
	
	//region Parser helper methods
	
	/**
	 * Wraps the integer parser function to a number parser function.<br>
	 * The wrapped parser simply translates the radix to an integer.<br>
	 *
	 * @param parser The integer parser function
	 * @return The wrapped parser function
	 * @throws NullPointerException If the parser is null
	 */
	private static @NotNull BiFunction<String, Radix, ? extends Number> wrapIntegerParser(@NotNull BiFunction<String, Integer, ? extends Number> parser) {
		Objects.requireNonNull(parser, "Parser must not be null");
		return (value, radix) -> parser.apply(value, radix.getRadix());
	}
	
	/**
	 * Wraps the floating point parser function to a number parser function.<br>
	 * The wrapped parser handles the special cases of floating point numbers.<br>
	 *
	 * @param parser The floating point parser function
	 * @return The wrapped parser function
	 * @throws NullPointerException If the parser is null
	 */
	private static @NotNull BiFunction<String, Radix, ? extends Number> wrapFloatingPointParser(@NotNull Function<String, ? extends Number> parser) {
		Objects.requireNonNull(parser, "Parser must not be null");
		return (value, radix) -> parser.apply(insertRadixPrefix(value, radix));
	}
	
	/**
	 * Creates a new big decimal parser function.<br>
	 * This parser function will handle the special cases of big decimals.<br>
	 *
	 * @return The big decimal parser function
	 * @see Mth#parseHexToBigDecimal(String)
	 */
	private static @NotNull BiFunction<String, Radix, ? extends Number> createBigDecimalParser() {
		return (value, radix) -> {
			if (radix == Radix.DECIMAL) {
				return new BigDecimal(value);
			}
			return Mth.parseHexToBigDecimal(insertRadixPrefix(value, radix));
		};
	}
	//endregion
	
	//region Static helper methods
	
	/**
	 * Inserts the radix prefix to the given value if it is not already present.<br>
	 * If the value is signed, the radix prefix will be inserted after the sign.<br>
	 * This method should only be used for floating point numbers.<br>
	 *
	 * @param value The value to insert the radix prefix to
	 * @param radix The radix to insert
	 * @return The value with the radix prefix inserted
	 * @throws NullPointerException If the value or radix is null
	 */
	private static @NotNull String insertRadixPrefix(@NotNull String value, @NotNull Radix radix) {
		Objects.requireNonNull(value, "Value must not be null");
		Objects.requireNonNull(radix, "Radix must not be null");
		char sign = value.charAt(0);
		if (sign == '+' || sign == '-') {
			String unsignedValue = value.substring(1);
			if (unsignedValue.startsWith(radix.getPrefix())) {
				return sign + unsignedValue;
			}
			return sign + radix.getPrefix() + unsignedValue;
		}
		if (!value.startsWith(radix.getPrefix())) {
			return radix.getPrefix() + value;
		}
		return value;
	}
	
	/**
	 * Removes the radix prefixes from the given value.<br>
	 * This method should only be used for integer numbers.<br>
	 *
	 * @param value The value to remove the radix prefixes from
	 * @param radices The radices to remove
	 * @return The value with the radix prefixes removed
	 * @throws NullPointerException If the value is null
	 */
	private static @NotNull String removeRadixPrefixes(@NotNull String value, Radix @NotNull ... radices) {
		Objects.requireNonNull(value, "Value must not be null");
		String result = value;
		for (Radix radix : radices) {
			result = StringUtils.replaceIgnoreCase(result, radix.getPrefix(), "");
		}
		return result;
	}
	
	/**
	 * Removes the type suffix from the given value if it is present.<br>
	 * If the radix is hexadecimal, the suffix must be outside the range of 'a' to 'f'.<br>
	 *
	 * @param value The value to remove the type suffix from
	 * @param type The number type to check the suffix against
	 * @param radix The radix to check the suffix against
	 * @return The value with the type suffix removed
	 * @throws NullPointerException If the value, type or radix is null
	 * @throws IllegalArgumentException If the suffix is present but does not match the type suffix of the number type
	 */
	private static @NotNull String removeTypeSuffix(@NotNull String value, @NotNull NumberType type, @NotNull Radix radix) {
		Objects.requireNonNull(value, "Value must not be null");
		Objects.requireNonNull(type, "Type must not be null");
		Objects.requireNonNull(radix, "Radix must not be null");
		char typeSuffix = Character.toLowerCase(value.charAt(value.length() - 1));
		if (Character.isAlphabetic(typeSuffix)) {
			if (radix == Radix.HEXADECIMAL) {
				if (typeSuffix < 'a' || 'f' < typeSuffix) {
					if (type.suffix != typeSuffix) {
						throw new IllegalArgumentException("Type suffix does not match, expected " + type.suffix + " but got " + typeSuffix);
					}
					return value.substring(0, value.length() - 1);
				}
			} else {
				if (type.suffix != typeSuffix) {
					throw new IllegalArgumentException("Type suffix does not match, expected " + type.suffix + " but got " + typeSuffix);
				}
				return value.substring(0, value.length() - 1);
			}
		}
		return value;
	}
	//endregion
	
	/**
	 * Gets the number type by the given suffix.<br>
	 * Will return null if the suffix is {@code '\0'}.<br>
	 *
	 * @param suffix The suffix of the number type
	 * @return The number type with the given suffix or null
	 */
	public static @Nullable NumberType getBySuffix(char suffix) {
		if (suffix == '\0') {
			return null;
		}
		for (NumberType numberType : values()) {
			if (numberType.suffix == suffix) {
				return numberType;
			}
		}
		return null;
	}
	
	/**
	 * Returns the java number class that this type represents.<br>
	 * @return The number class
	 */
	public @NotNull Class<? extends Number> getNumberClass() {
		return this.numberClass;
	}
	
	/**
	 * Returns the bit size of the number type.<br>
	 * @return The bit size or -1 if the number type is infinite (big integer or big decimal)
	 */
	public int getBitSize() {
		return this.bitSize;
	}
	
	/**
	 * Returns the minimum value of the number type.<br>
	 * @return The minimum value or null if the number type is infinite (big integer or big decimal)
	 */
	public @Nullable Number getMinValue() {
		return this.minValue;
	}
	
	/**
	 * Returns the maximum value of the number type.<br>
	 * @return The maximum value or null if the number type is infinite (big integer or big decimal)
	 */
	public @Nullable Number getMaxValue() {
		return this.maxValue;
	}
	
	/**
	 * Returns if the number type is infinite (big integer or big decimal).<br>
	 * @return True if the number type is infinite, false otherwise
	 */
	public boolean isInfinite() {
		return this.minValue == null && this.maxValue == null && this.bitSize == -1;
	}
	
	/**
	 * Checks if the given number is in the range of the number type.<br>
	 * If the number type is infinite, this will always return true.<br>
	 *
	 * @param number The number to check
	 * @return True if the number is in the range, false otherwise
	 * @throws NullPointerException If the number is null
	 */
	public boolean isInRange(@NotNull Number number) {
		Objects.requireNonNull(number, "Number must not be null");
		if (this.minValue == null || this.maxValue == null) {
			return true;
		}
		return this.minValue.doubleValue() <= number.doubleValue() && number.doubleValue() <= this.maxValue.doubleValue();
	}
	
	/**
	 * Returns if the number type is a floating point number.<br>
	 * @return True if the number type is a floating point number, false otherwise
	 */
	public boolean isFloatingPoint() {
		return this.floatingPoint;
	}
	
	/**
	 * Returns the suffix of the number type.<br>
	 * @return The suffix
	 */
	public char getSuffix() {
		return this.suffix;
	}
	
	/**
	 * Returns the radices supported by the number type.<br>
	 * @return The supported radices
	 */
	public @NotNull @Unmodifiable Set<Radix> getSupportedRadices() {
		return Set.copyOf(this.supportedRadices);
	}
	
	/**
	 * Checks if the number type supports the given radix.<br>
	 * @param radix The radix to check
	 * @return True if the number type supports the radix, false otherwise
	 */
	public boolean isSupportedRadix(@Nullable Radix radix) {
		if (radix == null) {
			return false;
		}
		return this.supportedRadices.contains(radix);
	}
	
	/**
	 * Checks if the number type can be converted to the given target type.<br>
	 * The following rules apply:<br>
	 * <p>
	 *     If the number type is...
	 * </p>
	 * <ul>
	 *     <li>the same as the target type, it can be converted</li>
	 *     <li>a floating point number and the target type is not, it cannot be converted</li>
	 *     <li>infinite, the target type must also be infinite</li>
	 *     <li>not infinite but the target type is, the number type can be converted<br></li>
	 *     <li>the bit size is greater than the target type, it cannot be converted</li>
	 *     <li>the minimum value is less than the target type, it cannot be converted</li>
	 *     <li>the maximum value is greater than the target type, it cannot be converted</li>
	 * </ul>
	 *
	 * @param targetType The target type to check
	 * @return True if the number type can be converted to the target type, false otherwise
	 * @throws NullPointerException If the target type is null
	 */
	public boolean canConvertTo(@NotNull NumberType targetType) {
		Objects.requireNonNull(targetType, "Target type must not be null");
		if (this == targetType) {
			return true;
		}
		if (this.isFloatingPoint() && !targetType.isFloatingPoint()) {
			return false;
		}
		if (this.isInfinite()) {
			return targetType.isInfinite();
		}
		if (targetType.isInfinite()) {
			return true;
		}
		if (this.bitSize > targetType.bitSize) {
			return false;
		}
		if (targetType.isFloatingPoint()) {
			return true;
		}
		return this.minValue.doubleValue() >= targetType.minValue.doubleValue() && this.maxValue.doubleValue() <= targetType.maxValue.doubleValue();
	}
	
	/**
	 * Checks if the number type can be converted to the given target type.<br>
	 * The following rules apply:<br>
	 * <ul>
	 *     <li>If the value is null, empty or blank, it cannot be converted</li>
	 *     <li>If the radix is not supported by the number type, it cannot be converted</li>
	 *     <li>If the value has a suffix, it must match the number type suffix</li>
	 *     <li>The parser must not throw an exception</li>
	 * </ul>
	 *
	 * @param value The value to check
	 * @param radix The radix to check
	 * @return True if the number type can be converted to the target type, false otherwise
	 * @throws NullPointerException If the radix is null
	 */
	public boolean canConvertTo(@Nullable String value, @NotNull Radix radix) {
		Objects.requireNonNull(radix, "Radix must not be null");
		if (StringUtils.isBlank(value)) {
			return false;
		}
		if (!this.isSupportedRadix(radix)) {
			return false;
		}
		try {
			value = removeTypeSuffix(value, this, radix);
		} catch (IllegalArgumentException e) {
			return false;
		}
		if (!this.isFloatingPoint()) {
			value = removeRadixPrefixes(value, Radix.BINARY, Radix.HEXADECIMAL);
		}
		try {
			this.parser.apply(value, radix);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * Parses the given value to a number of this type based on the radix.<br>
	 * This method parses the value in soft mode and will handle invalid cases gracefully.<br>
	 * The following rules apply:<br>
	 * <ul>
	 *     <li>If the value is null or empty, it will be parsed as 0</li>
	 *     <li>If the radix is null, then the radix will be determined based on the value</li>
	 *     <li>If the value contains a type suffix, it will be removed</li>
	 *     <li>If the value contains a radix prefix, it will be removed</li>
	 * </ul>
	 *
	 * @param value The value to parse
	 * @param radix The radix to parse the value with
	 * @return The parsed number
	 * @param <T> The number type (auto-casted)
	 * @throws IllegalArgumentException If the radix is not supported, or the type suffix does not match the number type suffix
	 * @throws NumberFormatException If the value cannot be parsed
	 * @see #parseNumberStrict(String, Radix)
	 */
	public <T extends Number> @NotNull T parseNumber(@Nullable String value, @Nullable Radix radix) {
		if (radix == null) {
			if (StringUtils.isBlank(value)) {
				return this.parseNumberStrict("0", Radix.DECIMAL);
			}
			if (StringUtils.containsIgnoreCase(value, Radix.HEXADECIMAL.getPrefix())) {
				radix = Radix.HEXADECIMAL;
			} else if (StringUtils.containsIgnoreCase(value, Radix.BINARY.getPrefix())) {
				radix = Radix.BINARY;
			} else {
				radix = Radix.DECIMAL;
			}
		}
		if (!this.isSupportedRadix(radix)) {
			throw new IllegalArgumentException("Radix " + radix + " is not supported by " + this);
		}
		if (StringUtils.isBlank(value)) {
			return this.parseNumberStrict("0", radix);
		}
		return this.parseNumberStrict(removeRadixPrefixes(removeTypeSuffix(value, this, radix), Radix.BINARY, Radix.HEXADECIMAL), radix);
	}
	
	/**
	 * Parses the given value to a number of this type based on the radix.<br>
	 * This method parses the value strictly and will throw an exception if the value is invalid.<br>
	 *
	 * @param value The value to parse
	 * @param radix The radix to parse the value with
	 * @return The parsed number
	 * @param <T> The number type (auto-casted)
	 * @throws NullPointerException If the value or radix is null
	 * @throws IllegalArgumentException If the value is empty or blank or the radix is not supported
	 * @throws NumberFormatException If the value contains an unexpected type suffix or cannot be parsed
	 * @see #parseNumber(String, Radix)
	 */
	@SuppressWarnings("unchecked")
	public <T extends Number> @NotNull T parseNumberStrict(@NotNull String value, @NotNull Radix radix) {
		Objects.requireNonNull(value, "Value must not be null");
		Objects.requireNonNull(radix, "Radix must not be null");
		if (value.isBlank()) {
			throw new IllegalArgumentException("Value must not be empty or blank");
		}
		if (!this.isSupportedRadix(radix)) {
			throw new IllegalArgumentException("Radix " + radix + " is not supported by " + this);
		}
		char typeSuffix = Character.toLowerCase(value.charAt(value.length() - 1));
		if (Character.isAlphabetic(typeSuffix)) {
			if (radix == Radix.HEXADECIMAL) {
				if (typeSuffix < 'a' || 'f' < typeSuffix) {
					throw new NumberFormatException("Found unexpected type suffix " + typeSuffix + " in hexadecimal number '" + value + "'");
				}
			} else {
				throw new NumberFormatException("Found unexpected type suffix " + typeSuffix + " in number '" + value + "'");
			}
		}
		return (T) this.parser.apply(value, radix);
	}
	
	//region Object overrides
	@Override
	public String toString() {
		return this.name().toLowerCase().replaceAll("_", " ");
	}
	//endregion
}
