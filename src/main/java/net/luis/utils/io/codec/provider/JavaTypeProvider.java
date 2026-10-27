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

package net.luis.utils.io.codec.provider;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.AtomicDouble;
import net.luis.utils.exception.InvalidStringException;
import net.luis.utils.io.reader.ScopedStringReader;
import net.luis.utils.io.reader.ValueStringReader;
import net.luis.utils.math.NumberType;
import net.luis.utils.util.result.Result;
import org.jetbrains.annotations.UnknownNullability;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * Type provider that uses standard Java types.<br>
 *
 * @author Luis-St
 */
public final class JavaTypeProvider implements TypeProvider<Object> {
	
	/**
	 * An empty java element instance.<br>
	 * Used for internal purposes only.<br>
	 * The java element has no string representation.<br>
	 */
	private static final Object EMPTY_ELEMENT = new Object() {
		@Override
		public String toString() {
			return "Empty java element has no string representation";
		}
	};
	/**
	 * Pattern matching a string which represents a list, the content may span multiple lines.<br>
	 */
	private static final Pattern LIST_PATTERN = Pattern.compile("^\\s*\\[.*]\\s*$", Pattern.DOTALL);
	/**
	 * Pattern matching a string which represents a map, the content may span multiple lines.<br>
	 */
	private static final Pattern MAP_PATTERN = Pattern.compile("^\\s*\\{.*}\\s*$", Pattern.DOTALL);
	/**
	 * Strict java type provider.<br>
	 * This provider will throw an exception if the type is not valid.<br>
	 */
	public static final JavaTypeProvider STRICT = new JavaTypeProvider(ParseConfig.STRICT);
	/**
	 * Default java type provider.<br>
	 * This provider will try to cast or parse incorrect types as far this is lossless possible.<br>
	 */
	public static final JavaTypeProvider DEFAULT = new JavaTypeProvider(ParseConfig.DEFAULT);
	/**
	 * Lenient java type provider.<br>
	 * This provider will try to cast or parse incorrect types as long as this will not throw an exception.<br>
	 */
	public static final JavaTypeProvider LENIENT = new JavaTypeProvider(ParseConfig.LENIENT);
	/**
	 * Type configuration used by the java type provider.<br>
	 */
	private final ParseConfig config;
	
	/**
	 * Constructs a new java type provider with the given type config.
	 * Private constructor to prevent instantiation.<br>
	 *
	 * @param config The type configuration used by the type provider
	 */
	private JavaTypeProvider(@NonNull ParseConfig config) {
		this.config = Objects.requireNonNull(config, "Config must not be null");
	}
	
	@Override
	public @NonNull Object empty() {
		return EMPTY_ELEMENT;
	}
	
	@Override
	@SuppressWarnings({ "ReturnOfNull", "DataFlowIssue" })
	public <X extends Exception> @NonNull Object createNull(@NonNull Function<String, X> exceptionConstructor) {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		return null;
	}
	
	@Override
	public <X extends Exception> @NonNull Object createBoolean(boolean value, @NonNull Function<String, X> exceptionConstructor) {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		return value;
	}
	
	@Override
	public <X extends Exception> @NonNull Object createByte(byte value, @NonNull Function<String, X> exceptionConstructor) {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		return value;
	}
	
	@Override
	public <X extends Exception> @NonNull Object createShort(short value, @NonNull Function<String, X> exceptionConstructor) {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		return value;
	}
	
	@Override
	public <X extends Exception> @NonNull Object createInteger(int value, @NonNull Function<String, X> exceptionConstructor) {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		return value;
	}
	
	@Override
	public <X extends Exception> @NonNull Object createLong(long value, @NonNull Function<String, X> exceptionConstructor) {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		return value;
	}
	
	@Override
	public <X extends Exception> @NonNull Object createFloat(float value, @NonNull Function<String, X> exceptionConstructor) {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		return value;
	}
	
	@Override
	public <X extends Exception> @NonNull Object createDouble(double value, @NonNull Function<String, X> exceptionConstructor) {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		return value;
	}
	
	@Override
	public <X extends Exception> @NonNull Object createString(@Nullable String value, @NonNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		if (value == null) {
			throw exceptionConstructor.apply("Value 'null' is not a valid string");
		}
		
		return value;
	}
	
	@Override
	public <X extends Exception> @NonNull Object createList(@Nullable List<?> values, @NonNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		if (values == null) {
			throw exceptionConstructor.apply("Value 'null' is not a valid list");
		}
		
		return values;
	}
	
	@Override
	public <X extends Exception> @NonNull Object createMap(@NonNull Function<String, X> exceptionConstructor) {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		return Maps.newLinkedHashMap();
	}
	
	@Override
	public <X extends Exception> @NonNull Object createMap(@Nullable Map<String, ?> values, @NonNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		if (values == null) {
			throw exceptionConstructor.apply("Value 'null' is not a valid map");
		}
		
		return values;
	}
	
	@Override
	public <X extends Exception> boolean isEmpty(@Nullable Object type, @NonNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		if (type == null) {
			throw exceptionConstructor.apply("Value 'null' is not an empty object");
		}
		
		return type == EMPTY_ELEMENT;
	}
	
	@Override
	public <X extends Exception> boolean isNull(@Nullable Object type, @NonNull Function<String, X> exceptionConstructor) {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		return type == null;
	}
	
	@Override
	public <X extends Exception> @NonNull Boolean getBoolean(@Nullable Object type, @NonNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		if (type == null) {
			if (this.config.isNullStrict()) {
				throw exceptionConstructor.apply("Value 'null' is not a boolean");
			}
			return false;
		}
		
		if (type instanceof Boolean booleanValue) {
			return booleanValue;
		}
		
		if (this.config.isDefaultLenient()) {
			if (type instanceof String stringValue) {
				if ("true".equalsIgnoreCase(stringValue) || "1".equals(stringValue)) {
					return true;
				}
				if ("false".equalsIgnoreCase(stringValue) || "0".equals(stringValue)) {
					return false;
				}
			}
			
			if (type instanceof Number numberValue) {
				Optional<BigInteger> exact = this.tryConvertToBigInteger(numberValue);
				
				if (exact.isPresent()) {
					if (exact.get().equals(BigInteger.ONE)) {
						return true;
					}
					if (exact.get().equals(BigInteger.ZERO)) {
						return false;
					}
				}
			}
		}
		throw exceptionConstructor.apply("Object '" + type + "' is not a boolean");
	}
	
	@Override
	public <X extends Exception> @NonNull Byte getByte(@Nullable Object type, @NonNull Function<String, X> exceptionConstructor) throws X {
		return this.getNumber(type, NumberType.BYTE, Number::byteValue, exceptionConstructor);
	}
	
	@Override
	public <X extends Exception> @NonNull Short getShort(@Nullable Object type, @NonNull Function<String, X> exceptionConstructor) throws X {
		return this.getNumber(type, NumberType.SHORT, Number::shortValue, exceptionConstructor);
	}
	
	@Override
	public <X extends Exception> @NonNull Integer getInteger(@Nullable Object type, @NonNull Function<String, X> exceptionConstructor) throws X {
		return this.getNumber(type, NumberType.INTEGER, Number::intValue, exceptionConstructor);
	}
	
	@Override
	public <X extends Exception> @NonNull Long getLong(@Nullable Object type, @NonNull Function<String, X> exceptionConstructor) throws X {
		return this.getNumber(type, NumberType.LONG, Number::longValue, exceptionConstructor);
	}
	
	@Override
	public <X extends Exception> @NonNull Float getFloat(@Nullable Object type, @NonNull Function<String, X> exceptionConstructor) throws X {
		return this.getNumber(type, NumberType.FLOAT, Number::floatValue, exceptionConstructor);
	}
	
	@Override
	public <X extends Exception> @NonNull Double getDouble(@Nullable Object type, @NonNull Function<String, X> exceptionConstructor) throws X {
		return this.getNumber(type, NumberType.DOUBLE, Number::doubleValue, exceptionConstructor);
	}
	
	@Override
	public <X extends Exception> @NonNull String getString(@Nullable Object type, @NonNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		if (type == null) {
			if (this.config.isNullStrict()) {
				throw exceptionConstructor.apply("Value 'null' is not a string");
			}
			return "";
		}
		
		if (type instanceof String stringValue) {
			return stringValue;
		}
		
		if (this.config.isDefaultLenient() && (type instanceof Number || type instanceof Boolean || type instanceof Character)) {
			return type.toString();
		}
		throw exceptionConstructor.apply("Object '" + type + "' is not a string");
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public <X extends Exception> @NonNull List<Object> getList(@Nullable Object type, @NonNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		return switch (type) {
			case null -> {
				if (this.config.isNullStrict()) {
					throw exceptionConstructor.apply("Value 'null' is not a list");
				}
				yield Lists.newArrayList();
			}
			case List<?> list -> (List<Object>) list;
			case String str when this.config.isDefaultLenient() && LIST_PATTERN.matcher(str).matches() -> {
				try {
					yield new ScopedStringReader(str).readList(ScopedStringReader::readObject);
				} catch (Exception e) {
					throw exceptionConstructor.apply("Provided string '" + str + "' could not be parsed as list: " + e.getMessage());
				}
			}
			default -> throw exceptionConstructor.apply("Object '" + type + "' is not a list");
		};
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public <X extends Exception> @NonNull Map<String, Object> getMap(@Nullable Object type, @NonNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		return switch (type) {
			case null -> {
				if (this.config.isNullStrict()) {
					throw exceptionConstructor.apply("Value 'null' is not a map");
				}
				yield Maps.newLinkedHashMap();
			}
			case Map<?, ?> map -> (Map<String, Object>) map;
			case String str when this.config.isDefaultLenient() && MAP_PATTERN.matcher(str).matches() -> {
				try {
					yield new ScopedStringReader(str).readMap(ScopedStringReader::readString, ScopedStringReader::readObject);
				} catch (Exception e) {
					throw exceptionConstructor.apply("Provided string '" + str + "' could not be parsed as map: " + e.getMessage());
				}
			}
			default -> throw exceptionConstructor.apply("Object '" + type + "' is not a map");
		};
	}
	
	@Override
	@SuppressWarnings("DuplicatedCode")
	public <X extends Exception> boolean has(@Nullable Object type, @Nullable String key, @NonNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		if (type == null) {
			if (this.config.isNullStrict()) {
				throw exceptionConstructor.apply("Value 'null' is not a map");
			}
			return false;
		}
		if (key == null) {
			if (this.config.isNullStrict()) {
				throw exceptionConstructor.apply("Value 'null' is not a valid key");
			}
			return false;
		}
		
		if (type instanceof Map<?, ?> map) {
			return map.containsKey(key);
		}
		if (this.config.isDefaultLenient() && type instanceof String str && MAP_PATTERN.matcher(str).matches()) {
			return this.getMap(str, exceptionConstructor).containsKey(key);
		}
		throw exceptionConstructor.apply("Object '" + type + "' is not a map");
	}
	
	@Override
	@SuppressWarnings("DuplicatedCode")
	public <X extends Exception> @NonNull Object get(@Nullable Object type, @Nullable String key, @NonNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		if (type == null) {
			throw exceptionConstructor.apply("Value 'null' is not a map");
		}
		if (key == null) {
			throw exceptionConstructor.apply("Value 'null' is not a valid key");
		}
		
		if (type instanceof Map<?, ?> map) {
			return map.get(key);
		}
		if (this.config.isDefaultLenient() && type instanceof String str && MAP_PATTERN.matcher(str).matches()) {
			return this.getMap(str, exceptionConstructor).get(key);
		}
		throw exceptionConstructor.apply("Object '" + type + "' is not a map");
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public <X extends Exception> void set(@Nullable Object type, @Nullable String key, @Nullable Object value, @NonNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		if (type == null) {
			if (this.config.isNullStrict()) {
				throw exceptionConstructor.apply("Value 'null' is not a map");
			}
			return;
		}
		if (key == null) {
			if (this.config.isNullStrict()) {
				throw exceptionConstructor.apply("Value 'null' is not a valid key");
			}
			return;
		}
		
		if (type instanceof Map<?, ?> map) {
			((Map<String, Object>) map).put(key, value);
		} else {
			throw exceptionConstructor.apply("Object '" + type + "' is not a map");
		}
	}
	
	@Override
	public <X extends Exception> @UnknownNullability Object merge(@Nullable Object current, @Nullable Object value, @NonNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		if (current == null) {
			return value;
		}
		if (value == null) {
			return current;
		}
		
		if (current == EMPTY_ELEMENT) {
			return value;
		}
		
		if (current instanceof List<?> currentList && value instanceof List<?> valueList) {
			List<Object> newList = Lists.newArrayList();
			newList.addAll(currentList);
			newList.addAll(valueList);
			return newList;
		}
		
		if (current instanceof Map<?, ?> currentMap && value instanceof Map<?, ?> valueMap) {
			Map<Object, Object> newMap = Maps.newLinkedHashMap();
			newMap.putAll(currentMap);
			newMap.putAll(valueMap);
			return newMap;
		}
		throw exceptionConstructor.apply("Unable to merge '" + current + "' with '" + value + "'");
	}
	
	//region Helper methods
	
	/**
	 * Gets the given value as a number of the given number type.<br>
	 * If the value is null, the default value {@code 0} is returned unless the null parse mode is strict.<br>
	 * If the value is already of the given number type, it is returned as is.<br>
	 * In lenient number parse modes, strings are parsed and other numbers are converted.<br>
	 * With {@link NumberParseMode#CAST_IN_RANGE} the conversion must be lossless, with {@link NumberParseMode#CAST_ALWAYS} it is always performed.<br>
	 *
	 * @param type The value to get as a number
	 * @param numberType The target number type
	 * @param converter The function converting a number to the target type, e.g. {@code Number::byteValue}
	 * @param exceptionConstructor A function to create an exception if the value could not be converted
	 * @param <T> The target number type
	 * @param <X> The type of the exception to throw
	 * @return The value as a number of the given type
	 * @throws NullPointerException If the number type, the converter, or the exception constructor is null
	 * @throws X If the value is null in strict null parse mode, is not a valid number or could not be converted
	 */
	private <T extends Number, X extends Exception> @NonNull T getNumber(@Nullable Object type, @NonNull NumberType numberType, @NonNull Function<Number, T> converter, @NonNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(numberType, "Number type must not be null");
		Objects.requireNonNull(converter, "Converter must not be null");
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		Class<?> numberClass = numberType.getNumberClass();
		String typeSpecific = (numberType == NumberType.INTEGER ? " an " : " a ") + numberClass.getSimpleName().toLowerCase();
		if (type == null) {
			if (this.config.isNullStrict()) {
				throw exceptionConstructor.apply("Value 'null' is not" + typeSpecific);
			}
			return converter.apply(0);
		}
		
		if (numberClass.isInstance(type)) {
			return converter.apply((Number) type);
		}
		
		if (this.config.isNumberLenient()) {
			if (type instanceof String stringValue) {
				type = this.parseNumber(stringValue, exceptionConstructor);
			}
			if (type instanceof Number numberValue) {
				if (this.config.numberParseMode == NumberParseMode.CAST_ALWAYS) {
					return converter.apply(numberValue);
				} else if (this.config.numberParseMode == NumberParseMode.CAST_IN_RANGE) {
					Result<Void> result = this.canConvertTo(numberValue, numberType);
					
					if (result.isSuccess()) {
						return converter.apply(numberValue);
					}
					throw exceptionConstructor.apply(result.errorOrThrow());
				}
			}
		}
		throw exceptionConstructor.apply("Object '" + type + "' is not" + typeSpecific);
	}
	
	/**
	 * Checks if the given value can be converted to the target number type without losing information.<br>
	 * The conversion is allowed if the source type can be converted by the standard Java rules (see {@link NumberType#canConvertTo(NumberType)}),<br>
	 * or if the value is an integer that fits into an integral type convertible to the target, e.g. {@code 3.0} to {@code int} or {@code 100L} to {@code byte}.<br>
	 * An integer value is only converted to {@code float} or {@code double} if it is exactly representable, e.g. {@code Long.MAX_VALUE} to {@code double} is rejected.<br>
	 *
	 * @param value The value to check
	 * @param target The target number type
	 * @return A success result if the value can be converted, otherwise an error result with the reason
	 * @throws NullPointerException If the value or target is null
	 */
	private @NonNull Result<Void> canConvertTo(@NonNull Number value, @NonNull NumberType target) {
		Objects.requireNonNull(value, "Value must not be null");
		Objects.requireNonNull(target, "Target number type must not be null");
		
		NumberType source = NumberType.getByClass(value.getClass());
		if (source == null) {
			return Result.error("Value '" + value + "' is not a known number");
		}
		
		if (source == target) {
			return Result.success();
		}
		
		String errorMessage = "Value '" + value + "' could not be converted to '" + target.getNumberClass().getName().toLowerCase() + "'";
		boolean floatingPointTarget = target == NumberType.FLOAT || target == NumberType.DOUBLE;
		if (source.canConvertTo(target) && (source.isFloatingPoint() || !floatingPointTarget)) {
			return Result.success();
		}
		
		Optional<BigInteger> optional = this.tryConvertToBigInteger(value);
		if (optional.isEmpty()) {
			return Result.error(errorMessage);
		}
		
		BigInteger bigInteger = optional.get();
		if (floatingPointTarget) {
			double converted = target == NumberType.FLOAT ? bigInteger.floatValue() : bigInteger.doubleValue();
			if (Double.isInfinite(converted) || new BigDecimal(converted).compareTo(new BigDecimal(bigInteger)) != 0) {
				return Result.error(errorMessage);
			}
			return Result.success();
		}
		
		for (NumberType type : NumberType.values()) {
			if (!type.isFloatingPoint() && type.isInRange(bigInteger) && type.canConvertTo(target)) {
				return Result.success();
			}
		}
		return Result.error(errorMessage);
	}
	
	/**
	 * Tries to convert the given value to a {@link BigInteger}.<br>
	 * If the value is not a number or is not a finite number, this method will return {@link  Optional#empty()}.<br>
	 *
	 * @param value The number value to convert
	 * @return The converted {@link BigInteger} or {@link Optional#empty()} if the conversion failed
	 * @throws NullPointerException If the given value is null
	 */
	private @NonNull Optional<BigInteger> tryConvertToBigInteger(@NonNull Number value) {
		Objects.requireNonNull(value, "Value must not be null");
		Function<BigDecimal, Optional<BigInteger>> exact = (bd) -> {
			try {
				return Optional.of(bd.toBigIntegerExact());
			} catch (Exception _) {
				return Optional.empty();
			}
		};
		
		return switch (value) {
			case Byte b -> Optional.of(BigInteger.valueOf(b));
			case Short s -> Optional.of(BigInteger.valueOf(s));
			case Integer i -> Optional.of(BigInteger.valueOf(i));
			case AtomicInteger ai -> Optional.of(BigInteger.valueOf(ai.get()));
			case Long l -> Optional.of(BigInteger.valueOf(l));
			case AtomicLong al -> Optional.of(BigInteger.valueOf(al.get()));
			case BigInteger bi -> Optional.of(bi);
			case Float f when f.isInfinite() || f.isNaN() -> Optional.empty();
			case Double d when d.isInfinite() || d.isNaN() -> Optional.empty();
			case Float f -> exact.apply(new BigDecimal(f));
			case Double d -> exact.apply(new BigDecimal(d));
			case AtomicDouble ad -> this.tryConvertToBigInteger(ad.doubleValue());
			case BigDecimal bd -> exact.apply(bd);
			default -> {
				try {
					yield exact.apply(new BigDecimal(value.toString()));
				} catch (Exception _) {
					yield Optional.empty();
				}
			}
		};
	}
	
	/**
	 * Parses the given string as a number.<br>
	 * Leading and trailing whitespaces are ignored.<br>
	 * The number type is determined by {@link ValueStringReader#readNumber()}, so a type suffix is respected.<br>
	 * The number must consume the whole string, trailing characters like in {@code "42abc"} are rejected.<br>
	 *
	 * @param str The string to parse
	 * @param exceptionConstructor A function to create an exception if the string is not a valid number
	 * @param <X> The type of the exception to throw
	 * @return The parsed number
	 * @throws NullPointerException If the string or the exception constructor is null
	 * @throws X If the string is empty, is not a valid number, or contains trailing characters
	 */
	private <X extends Exception> @NonNull Number parseNumber(@NonNull String str, @NonNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(str, "String must not be null");
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		String stripped = str.strip();
		if (stripped.isEmpty()) {
			throw exceptionConstructor.apply("Value '" + str + "' is not a number, expected a number but found nothing");
		}
		
		ValueStringReader reader = new ValueStringReader(stripped);
		Number number;
		try {
			number = reader.readNumber();
		} catch (StringIndexOutOfBoundsException | InvalidStringException | IllegalArgumentException e) {
			throw exceptionConstructor.apply("Value '" + str + "' is not a valid number: " + e.getMessage());
		}
		
		if (reader.canRead()) {
			throw exceptionConstructor.apply("Value '" + str + "' is not a number, unexpected trailing characters '" + reader.readRemaining() + "'");
		}
		return number;
	}
	//endregion
	
	//region Configuration
	
	/**
	 * The parse mode used by the java type provider when parsing any other type than null values and numbers.<br>
	 *
	 * @author Luis-St
	 */
	private enum DefaultParseMode {
		
		/**
		 * When the type provider encounters a value that it could not cast, it will throw an exception.<br>
		 */
		STRICT,
		/**
		 * When the type provider encounters a value that it could not cast, it will try to parse it.<br>
		 */
		LENIENT
	}
	
	/**
	 * The parse mode used by the java type provider when encountering null values.<br>
	 *
	 * @author Luis-St
	 */
	private enum NullParseMode {
		
		/**
		 * When the type provider encounters a null value, it will throw an exception.<br>
		 */
		THROW_EXCEPTION,
		/**
		 * When the type provider encounters a null value, it will return the default value of the type (e.g., 0 for integers, false for booleans, ...).<br>
		 */
		RETURN_DEFAULT
	}
	
	/**
	 * The parse mode used by the java type provider when encountering numbers.<br>
	 *
	 * @author Luis-St
	 */
	private enum NumberParseMode {
		
		/**
		 * When the type provider encounters a number that is not the exact type, it will throw an exception.<br>
		 */
		ONLY_MATCHING_TYPE,
		/**
		 * When the type provider encounters a number not in the range of the type, it will throw an exception.<br>
		 */
		CAST_IN_RANGE,
		/**
		 * When the type provider encounters a number it will try to cast it to the type.<br>
		 * Only if the cast fails with an exception, the type provider will throw an exception.<br>
		 */
		CAST_ALWAYS
	}
	
	/**
	 * Parse configuration for the {@link JavaTypeProvider}.<br>
	 *
	 * @author Luis-St
	 *
	 * @param defaultParseMode The default parse mode used by the type provider when parsing any other type than null values and numbers
	 * @param nullParseMode The parse mode used by the type provider when encountering null values
	 * @param numberParseMode The parse mode used by the type provider when encountering numbers
	 */
	private record ParseConfig(@NonNull DefaultParseMode defaultParseMode, @NonNull NullParseMode nullParseMode, @NonNull NumberParseMode numberParseMode) {
		
		/**
		 * Strict parse configuration for the java type provider.<br>
		 * Used by {@link JavaTypeProvider#STRICT}.<br>
		 */
		public static final ParseConfig STRICT = new ParseConfig(DefaultParseMode.STRICT, NullParseMode.THROW_EXCEPTION, NumberParseMode.ONLY_MATCHING_TYPE);
		/**
		 * Default parse configuration for the java type provider.<br>
		 * Used by {@link JavaTypeProvider#DEFAULT}.<br>
		 */
		public static final ParseConfig DEFAULT = new ParseConfig(DefaultParseMode.LENIENT, NullParseMode.RETURN_DEFAULT, NumberParseMode.CAST_IN_RANGE);
		/**
		 * Lenient parse configuration for the java type provider.<br>
		 * Used by {@link JavaTypeProvider#LENIENT}.<br>
		 */
		public static final ParseConfig LENIENT = new ParseConfig(DefaultParseMode.LENIENT, NullParseMode.RETURN_DEFAULT, NumberParseMode.CAST_ALWAYS);
		
		/**
		 * Constructs a new parse configuration for the java type provider.<br>
		 *
		 * @param defaultParseMode The default parse mode used by the type provider when parsing any other type than null values and numbers
		 * @param nullParseMode The parse mode used by the type provider when encountering null values
		 * @param numberParseMode The parse mode used by the type provider when encountering numbers
		 * @throws NullPointerException If any of the parameters is null
		 */
		private ParseConfig {
			Objects.requireNonNull(defaultParseMode, "Default parse mode must not be null");
			Objects.requireNonNull(nullParseMode, "Null parse mode must not be null");
			Objects.requireNonNull(numberParseMode, "Number parse mode must not be null");
		}
		
		/**
		 * Checks if default parsing is lenient.<br>
		 * @return True if default parsing is lenient, false otherwise
		 */
		public boolean isDefaultLenient() {
			return this.defaultParseMode == DefaultParseMode.LENIENT;
		}
		
		/**
		 * Checks if null parsing is strict.<br>
		 * @return True if null parsing is strict, false otherwise
		 */
		public boolean isNullStrict() {
			return this.nullParseMode == NullParseMode.THROW_EXCEPTION;
		}
		
		/**
		 * Checks if number parsing is lenient.<br>
		 * @return True if number parsing is lenient, false otherwise
		 */
		public boolean isNumberLenient() {
			return this.numberParseMode != NumberParseMode.ONLY_MATCHING_TYPE;
		}
	}
	//endregion
}
