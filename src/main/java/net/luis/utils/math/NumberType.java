/*
 * LUtils
 * Copyright (C) 2024 Luis Staudt
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

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import net.luis.utils.util.Utils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 *
 * @author Luis-St
 *
 */

public enum NumberType {
	
	BYTE(Byte.class, Byte.SIZE, Byte.MIN_VALUE, Byte.MAX_VALUE, false, 'b', Set.of(Radix.values()), wrapIntegerParser(Byte::parseByte)),
	SHORT(Short.class, Short.SIZE, Short.MIN_VALUE, Short.MAX_VALUE, false, 's', Set.of(Radix.values()), wrapIntegerParser(Short::parseShort)),
	INTEGER(Integer.class, Integer.SIZE, Integer.MIN_VALUE, Integer.MAX_VALUE, false, 'i', Set.of(Radix.values()), wrapIntegerParser(Integer::parseInt)),
	LONG(Long.class, Long.SIZE, Long.MIN_VALUE, Long.MAX_VALUE, false, 'l', Set.of(Radix.values()), wrapIntegerParser(Long::parseLong)),
	BIG_INTEGER(BigInteger.class, -1, null, null, false, '\0', Set.of(Radix.values()), wrapIntegerParser(BigInteger::new)),
	FLOAT(Float.class, Float.SIZE, Float.MIN_VALUE, Float.MAX_VALUE, true, 'f', Set.of(Radix.DECIMAL, Radix.HEXADECIMAL), wrapFloatingPointParser(Float::parseFloat)),
	DOUBLE(Double.class, Double.SIZE, Double.MIN_VALUE, Double.MAX_VALUE, true, 'd', Set.of(Radix.DECIMAL, Radix.HEXADECIMAL), wrapFloatingPointParser(Double::parseDouble)),
	BIG_DECIMAL(BigDecimal.class, -1, null, null, true, '\0', Set.of(Radix.DECIMAL, Radix.HEXADECIMAL), createBigDecimalParser());
	
	private static final Table<NumberType, Radix, Integer> MAX_DIGITS = Utils.make(HashBasedTable.create(), table -> {
		table.put(BYTE, Radix.BINARY, 8);
		table.put(BYTE, Radix.DECIMAL, 3);
		table.put(BYTE, Radix.OCTAL, 3);
		table.put(BYTE, Radix.HEXADECIMAL, 2);
		
		table.put(SHORT, Radix.BINARY, 16);
		table.put(SHORT, Radix.DECIMAL, 5);
		table.put(SHORT, Radix.OCTAL, 5);
		table.put(SHORT, Radix.HEXADECIMAL, 4);
		
		table.put(INTEGER, Radix.BINARY, 32);
		table.put(INTEGER, Radix.DECIMAL, 10);
		table.put(INTEGER, Radix.OCTAL, 11);
		table.put(INTEGER, Radix.HEXADECIMAL, 8);
		
		table.put(LONG, Radix.BINARY, 64);
		table.put(LONG, Radix.DECIMAL, 19);
		table.put(LONG, Radix.OCTAL, 21);
		table.put(LONG, Radix.HEXADECIMAL, 16);
	});
	
	private final Class<? extends Number> numberClass;
	private final int bitSize;
	private final @Nullable Number minValue;
	private final @Nullable Number maxValue;
	private final boolean floatingPoint;
	private final char suffix;
	private final Set<Radix> supportedRadices;
	private final BiFunction<String, Radix, ? extends Number> parser;
	
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
	private static @NotNull BiFunction<String, Radix, ? extends Number> wrapIntegerParser(@NotNull BiFunction<String, Integer, ? extends Number> parser) {
		return (value, radix) -> parser.apply(value, radix.getRadix());
	}
	
	private static @NotNull BiFunction<String, Radix, ? extends Number> wrapFloatingPointParser(@NotNull Function<String, ? extends Number> parser) {
		return (value, radix) -> {
			String unsignedValue = value;
			char sign = unsignedValue.charAt(0);
			if (sign == '+' || sign == '-') {
				unsignedValue = unsignedValue.substring(1);
			}
			return parser.apply(sign + radix.getPrefix() + unsignedValue);
		};
	}
	
	private static @NotNull BiFunction<String, Radix, ? extends Number> createBigDecimalParser() {
		return (value, radix) -> {
			if (radix == Radix.DECIMAL) {
				return new BigDecimal(value);
			}
			return Mth.parseHexToBigDecimal(value);
		};
	}
	//endregion
	
	public static @Nullable NumberType getBySuffix(char suffix) {
		if (suffix == '\0') {
			return null;
		}
		for (NumberType numberType : NumberType.values()) {
			if (numberType.suffix == suffix) {
				return numberType;
			}
		}
		return null;
	}
	
	public @NotNull Class<? extends Number> getNumberClass() {
		return this.numberClass;
	}
	
	public int getBitSize() {
		return this.bitSize;
	}
	
	public @Nullable Number getMinValue() {
		return this.minValue;
	}
	
	public @Nullable Number getMaxValue() {
		return this.maxValue;
	}
	
	public boolean isInfinite() {
		return this.minValue == null && this.maxValue == null && this.bitSize == -1;
	}
	
	public boolean isInRange(@NotNull Number number) {
		Objects.requireNonNull(number, "Number must not be null");
		if (this.minValue == null || this.maxValue == null) {
			return true;
		}
		return this.minValue.doubleValue() <= number.doubleValue() && number.doubleValue() <= this.maxValue.doubleValue();
	}
	
	public boolean isFloatingPoint() {
		return this.floatingPoint;
	}
	
	public char getSuffix() {
		return this.suffix;
	}
	
	public @NotNull @Unmodifiable Set<Radix> getSupportedRadices() {
		return Set.copyOf(this.supportedRadices);
	}
	
	public boolean isSupportedRadix(@Nullable Radix radix) {
		if (radix == null) {
			return false;
		}
		return this.supportedRadices.contains(radix);
	}
	
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
			return false;
		}
		if (this.bitSize > targetType.bitSize) {
			return false;
		}
		if (targetType.isFloatingPoint())  {
			return this.bitSize <= targetType.bitSize;
		}
		return this.minValue.doubleValue() >= targetType.minValue.doubleValue() && this.maxValue.doubleValue() <= targetType.maxValue.doubleValue();
	}
	
	public boolean canBeSafelyCastTo(@NotNull String value, @NotNull Radix radix) {
		Objects.requireNonNull(value, "Value must not be null");
		Objects.requireNonNull(radix, "Radix must not be null");
		if (!this.isSupportedRadix(radix)) {
			return false;
		} else if (this.isInfinite()) {
			return true;
		} else if (this.isFloatingPoint()) {
			return this.canBeCastToFloatingPoint(value, radix);
		}
		if (value.contains(".")) {
			return false;
		}
		Integer maxDigits = MAX_DIGITS.get(this, radix);
		if (maxDigits == null) {
			return false;
		}
		return maxDigits >= value.chars().filter(Character::isDigit).count();
	}
	
	// Decimal:
	//   float f = 39 digits;
	//   float f = 0.9e-45f;
 	//   float f = 0.9e+38f;
	//   double d = 309 digits;
	//   double d = 0.9e-323;
	//   double d = 0.9e+308;
	// Hexadecimal:
	//   float f = 0x1.0p-149f;
	//   float f = 0x1.0p+127f;
	//   double d = 0x1.0p-1074;
 	//   double d = 0x1.0p+1023;
	private boolean canBeCastToFloatingPoint(@NotNull String value, @NotNull Radix radix) {
		return radix == Radix.HEXADECIMAL ? this.canBeCastToHexadecimalFloatingPoint(value) : this.canBeCastToDecimalFloatingPoint(value);
	}
	
	private boolean canBeCastToDecimalFloatingPoint(@NotNull String value) {
		if (StringUtils.containsIgnoreCase(value, "p")) {
			return false;
		}
		long preDecimal = StringUtils.substringBefore(value, ".").chars().filter(Character::isDigit).count();
		
		if (StringUtils.containsIgnoreCase(value, "e")) {
			long postDecimal = StringUtils.substringBetween(value, ".", "e").chars().filter(Character::isDigit).count();
			int exponent = Integer.parseInt(StringUtils.substringAfter(value, "e"));
			return true;
		}
		long postDecimal = StringUtils.substringAfter(value, ".").chars().filter(Character::isDigit).count();
		return switch (this) {
			case FLOAT -> 39 >= preDecimal + postDecimal;
			case DOUBLE -> 309 >= preDecimal + postDecimal;
			default -> false;
		};
	}
	
	private boolean canBeCastToHexadecimalFloatingPoint(@NotNull String value) {
		if (StringUtils.containsIgnoreCase(value, "e") || !StringUtils.containsIgnoreCase(value, "p")) {
			return false;
		}
		
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Number> @NotNull T parseNumber(@NotNull String value, @NotNull Radix radix) {
		Objects.requireNonNull(value, "Value must not be null");
		Objects.requireNonNull(radix, "Radix must not be null");
		if (!this.isSupportedRadix(radix)) {
			throw new IllegalArgumentException("Radix " + radix + " is not supported by " + this);
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
