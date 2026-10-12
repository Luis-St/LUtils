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

package net.luis.utils.logging.formatter.pattern;

import com.google.common.collect.*;
import net.luis.utils.io.reader.ScopedStringReader;
import net.luis.utils.io.reader.StringReader;
import net.luis.utils.logging.formatter.pattern.token.*;
import net.luis.utils.logging.formatter.pattern.token.config.LogPatternTokenConfig;
import net.luis.utils.logging.formatter.pattern.token.config.util.Embeddable;
import net.luis.utils.logging.formatter.pattern.token.config.util.Embedded;
import net.luis.utils.math.NumberType;
import net.luis.utils.math.Radix;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;

/**
 *
 * @author Luis-St
 *
 */

public class LogPatternLayoutParser {
	
	private static final Logger LOGGER = LogManager.getLogger(LogPatternLayoutParser.class);
	private static final List<?> EMPTY_UNMODIFIABLE_LIST = Collections.unmodifiableList(Lists.newArrayList());
	private static final Set<?> EMPTY_UNMODIFIABLE_SET = Collections.unmodifiableSet(Sets.newHashSet());
	private static final Map<?, ?> EMPTY_UNMODIFIABLE_MAP = Collections.unmodifiableMap(Maps.newHashMap());
	private static final Function<ScopedStringReader, Object> CONFIG_VALUE_PARSER = r -> parseConfigItem(r.readRemaining());
	private static final ScopedValue<RecordComponent> RECORD_COMPONENT = ScopedValue.newInstance();
	private static final Radix[] RADIX_PARSE_ORDER = { Radix.BINARY, Radix.HEXADECIMAL, Radix.OCTAL };
	private static final char TOKEN_START_CHAR = '$';
	private static final char EMBEDDED_NAMESPACE_SEPARATOR = '.';
	
	private final Set<LogPatternTokenType> tokenTypes;
	
	public LogPatternLayoutParser(@NonNull Set<LogPatternTokenType> tokenTypes) {
		this.tokenTypes = Set.copyOf(Objects.requireNonNull(tokenTypes, "Token types must not be null"));
	}
	
	private static boolean isTokenIdentifierEndChar(char c) {
		if (c == '{') {
			return true;
		}
		
		char lower = Character.toLowerCase(c);
		return (lower < 'a' || lower > 'z') && (lower < '0' || lower > '9') && lower != '_';
	}
	
	private static @NonNull Object parseConfigItem(@NonNull String value) {
		Objects.requireNonNull(value, "Value must not be null");
		
		if ("true".equalsIgnoreCase(value)) {
			return true;
		} else if ("false".equalsIgnoreCase(value)) {
			return false;
		}
		
		char first = value.charAt(0);
		char last = value.charAt(value.length() - 1);
		if (first == '[' && last == ']') {
			return new ScopedStringReader(value).readList(CONFIG_VALUE_PARSER);
		} else if (first == '{' && last == '}') {
			return new ScopedStringReader(value).readMap(ScopedStringReader::readRemaining, CONFIG_VALUE_PARSER);
		} else if (first == '(' && last == ')') {
			return new ScopedStringReader(value).readSet(CONFIG_VALUE_PARSER);
		}
		
		Radix radix = Arrays.stream(RADIX_PARSE_ORDER).filter(r -> value.startsWith(r.getPrefix())).findFirst().orElse(Radix.DECIMAL);
		for (NumberType numberType : NumberType.values()) {
			if (numberType.canConvertTo(value, radix)) {
				return numberType.parseNumber(value, radix);
			}
		}
		return value;
	}
	
	private static @NonNull Object construct(@NonNull Class<?> recordClass, @NonNull Function<RecordComponent, Object> valueResolver) {
		Objects.requireNonNull(recordClass, "Record class must not be null");
		Objects.requireNonNull(valueResolver, "Value resolver must not be null");
		if (!recordClass.isRecord()) {
			throw new IllegalArgumentException("Class " + recordClass.getName() + " is not a record");
		}
		
		RecordComponent[] components = recordClass.getRecordComponents();
		Class<?>[] parameterTypes = new Class<?>[components.length];
		Object[] arguments = new Object[components.length];
		
		for (int i = 0; i < components.length; i++) {
			parameterTypes[i] = components[i].getType();
			arguments[i] = valueResolver.apply(components[i]);
		}
		
		try {
			Constructor<?> constructor = recordClass.getDeclaredConstructor(parameterTypes);
			constructor.setAccessible(true);
			return constructor.newInstance(arguments);
		} catch (InvocationTargetException e) {
			throw new IllegalStateException("Canonical constructor of " + recordClass.getName() + " threw", e.getCause());
		} catch (ReflectiveOperationException e) {
			throw new IllegalStateException("Failed to construct " + recordClass.getName(), e);
		}
	}
	
	private static @NonNull Object parseConfigParameter(@NonNull RecordComponent component, @NonNull Map<String, Object> configItems, @NonNull String tokenIdentifier, int readerIndex) {
		Class<?> type = component.getType();
		String name = component.getName();
		
		if (component.isAnnotationPresent(Embedded.class)) {
			String baseMessage = "Configuration item '" + name + "' for token '" + tokenIdentifier + "' at position " + (readerIndex - tokenIdentifier.length()) + " is marked as embedded, ";
			if (type != Optional.class) {
				throw new LogPatternException(baseMessage + "but is not of type Optional<T>");
			}
			
			Class<?> innerType = innerType(component);
			if (!innerType.isRecord()) {
				throw new LogPatternException(baseMessage + "but inner type " + innerType.getName() + " is not a record");
			}
			if (!innerType.isAnnotationPresent(Embeddable.class)) {
				throw new LogPatternException(baseMessage + "but inner type " + innerType.getName() + " is not marked as embeddable");
			}
			
			String namespace = component.getAnnotation(Embedded.class).namespace();
			Map<String, Object> embeddedConfigItems = Maps.newHashMap();
			
			if (configItems.containsKey(namespace)) {
				Object object = configItems.get(namespace);
				
				if (object instanceof Map<?, ?> map) {
					for (Map.Entry<?, ?> entry : map.entrySet()) {
						Object key = entry.getKey();
						
						if (key instanceof String strKey) {
							embeddedConfigItems.put(strKey, entry.getValue());
							continue;
						}
						
						LOGGER.warn("Key of embedded configuration item '{}' for token '{}' was parsed as {}, is now force converted to a string", name, tokenIdentifier, key.getClass().getName());
						embeddedConfigItems.put(key.toString(), entry.getValue());
					}
				} else {
					throw new LogPatternException("Configuration item '" + namespace + "' for embedded configuration at position " + (readerIndex - tokenIdentifier.length()) + " is " + object.getClass().getName() + ", expected was a map");
				}
			}
			
			for (Map.Entry<String, Object> entry : configItems.entrySet()) {
				String key = entry.getKey();
				
				if (key.startsWith(namespace + EMBEDDED_NAMESPACE_SEPARATOR)) {
					String subKey = key.substring(namespace.length() + 1);
					embeddedConfigItems.put(subKey, entry.getValue());
				}
			}
			
			try {
				return Optional.of(construct(innerType, embeddedComponent -> parseConfigParameter(embeddedComponent, embeddedConfigItems, tokenIdentifier, readerIndex)));
			} catch (LogPatternException e) {
				throw new LogPatternException("Failed to construct embedded configuration item '" + name + "' for token '" + tokenIdentifier + "' at position " + (readerIndex - tokenIdentifier.length()), e);
			}
		}
		
		Object value = configItems.get(name);
		if (value == null) {
			if (type == Optional.class) {
				return Optional.empty();
			} else if (type == OptionalInt.class) {
				return OptionalInt.empty();
			} else if (type == OptionalLong.class) {
				return OptionalLong.empty();
			} else if (type == OptionalDouble.class) {
				return OptionalDouble.empty();
			} else if (type == List.class) {
				return Collections.emptyList();
			} else if (type == Set.class) {
				return Collections.emptySet();
			} else if (type == Map.class) {
				return Collections.emptyMap();
			} else {
				throw new LogPatternException("Missing required configuration item '" + name + "' for token '" + tokenIdentifier + "' at position " + (readerIndex - tokenIdentifier.length()) + " and no default value configured for type " + type.getName());
			}
		}
		
		if (!type.isInstance(value)) {
			Object casted = ScopedValue.where(RECORD_COMPONENT, component).call(() -> tryCast(type, value));
			if (type.isInstance(casted)) {
				return casted;
			}
			throw new LogPatternException("Configuration item '" + name + "' for token '" + tokenIdentifier + "' at position " + (readerIndex - tokenIdentifier.length()) + " is of type " + value.getClass().getName() + ", expected " + type.getName());
		}
		return value;
	}
	
	private static @Nullable Object tryCast(@NonNull Class<?> type, @NonNull Object value) {
		Objects.requireNonNull(type, "Type must not be null");
		Objects.requireNonNull(value, "Value must not be null");
		if (type.isInstance(value)) {
			return value;
		}
		
		if (type.isEnum()) {
			if (value instanceof String str) {
				return Arrays.stream(type.getEnumConstants())
					.map(Enum.class::cast)
					.filter(constant -> constant.name().equals(str.toUpperCase()))
					.findFirst()
					.orElse(null);
			}
			return tryCast(String.class, value);
		} else if (type == String.class) {
			return value.toString();
		} else if (Number.class.isAssignableFrom(type) && value instanceof Number number) {
			NumberType targetType = NumberType.getByClass(type);
			NumberType sourceType = NumberType.getByClass(number.getClass());
			if (targetType == null || sourceType == null) {
				return null;
			}
			
			if (sourceType.isFloatingPoint()) {
				BigDecimal bigDecimal = toBigDecimal(number).stripTrailingZeros();
				if (bigDecimal.scale() <= 0) {
					for (NumberType numberType : NumberType.values()) {
						if (numberType.isFloatingPoint() || !numberType.isInRange(bigDecimal)) {
							continue;
						}
						number = switch (numberType) {
							case BYTE -> bigDecimal.byteValueExact();
							case SHORT -> bigDecimal.shortValueExact();
							case INTEGER -> bigDecimal.intValueExact();
							case LONG -> bigDecimal.longValueExact();
							default -> throw new AssertionError("Unhandled number type: " + numberType);
						};
						sourceType = numberType;
						break;
					}
				}
			}
			
			if (sourceType.canConvertTo(targetType) || targetType.isInRange(number)) {
				try {
					return convertNumber(number, targetType);
				} catch (ClassCastException e) {
					LOGGER.info("Failed to cast number {} ({}) to type {} ({}): {}", number, sourceType, type.getName(), targetType, e.getMessage());
				}
			}
			return null;
		} else if (type == Optional.class) {
			Object casted = tryCast(innerType(RECORD_COMPONENT.get()), value);
			if (casted == null) {
				return null;
			}
			return Optional.of(casted);
		} else if (type == OptionalInt.class) {
			Object casted = tryCast(Integer.class, value);
			if (casted == null) {
				return null;
			}
			return OptionalInt.of((Integer) casted);
		} else if (type == OptionalLong.class) {
			Object casted = tryCast(Long.class, value);
			if (casted == null) {
				return null;
			}
			return OptionalLong.of((Long) casted);
		} else if (type == OptionalDouble.class) {
			Object casted = tryCast(Double.class, value);
			if (casted == null) {
				return null;
			}
			return OptionalDouble.of((Double) casted);
		} else if (List.class.isAssignableFrom(type) && value instanceof List<?> list) {
			if (EMPTY_UNMODIFIABLE_LIST.getClass().isAssignableFrom(type)) {
				return Collections.unmodifiableList(list);
			} else if (LinkedList.class.isAssignableFrom(type)) {
				return Lists.newLinkedList(list);
			}
			return Lists.newArrayList(list);
		} else if (Set.class.isAssignableFrom(type) && value instanceof Set<?> set) {
			if (EMPTY_UNMODIFIABLE_SET.getClass().isAssignableFrom(type)) {
				return Collections.unmodifiableSet(set);
			} else if (LinkedHashSet.class.isAssignableFrom(type)) {
				return Sets.newLinkedHashSet(set);
			}
			return Sets.newHashSet(set);
		} else if (Map.class.isAssignableFrom(type) && value instanceof Map<?, ?> map) {
			if (EMPTY_UNMODIFIABLE_MAP.getClass().isAssignableFrom(type)) {
				return Collections.unmodifiableMap(map);
			} else if (LinkedHashMap.class.isAssignableFrom(type)) {
				return Maps.newLinkedHashMap(map);
			}
			return Maps.newHashMap(map);
		}
		return null;
	}
	
	private static @NonNull BigDecimal toBigDecimal(@NonNull Number number) {
		Objects.requireNonNull(number, "Number must not be null");
		
		return switch (number) {
			case Float f -> BigDecimal.valueOf(f);
			case Double d -> BigDecimal.valueOf(d);
			case BigDecimal bd -> bd;
			default -> BigDecimal.valueOf(number.doubleValue());
		};
	}
	
	private static @NonNull Number convertNumber(@NonNull Number number, @NonNull NumberType targetType) {
		Objects.requireNonNull(number, "Number must not be null");
		Objects.requireNonNull(targetType, "Target type must not be null");
		
		return switch (targetType) {
			case BYTE -> number.byteValue();
			case SHORT -> number.shortValue();
			case INTEGER -> number.intValue();
			case LONG -> number.longValue();
			case FLOAT -> number.floatValue();
			case DOUBLE -> number.doubleValue();
			default -> throw new IllegalArgumentException("Unrecognized number type: " + targetType);
		};
	}
	
	private static @NonNull Class<?> innerType(@NonNull RecordComponent component) {
		Objects.requireNonNull(component, "Record component must not be null");
		
		if (!(component.getGenericType() instanceof ParameterizedType parameterized)) {
			throw new IllegalArgumentException("Component '" + component.getName() + "' does not provide a parameterized type");
		}
		return rawClass(parameterized.getActualTypeArguments()[0]);
	}
	
	private static @NonNull Class<?> rawClass(@NonNull Type type) {
		Objects.requireNonNull(type, "Type must not be null");
		
		return switch (type) {
			case Class<?> clazz -> clazz;
			case ParameterizedType parameterized -> (Class<?>) parameterized.getRawType();
			case GenericArrayType array -> rawClass(array.getGenericComponentType()).arrayType();
			case WildcardType wildcard -> rawClass(wildcard.getUpperBounds()[0]);
			case TypeVariable<?> variable -> rawClass(variable.getBounds()[0]);
			default -> throw new IllegalArgumentException("Unsupported type: " + type);
		};
	}
	
	public @NonNull LogPatternLayout parse(@NonNull String pattern) {
		Objects.requireNonNull(pattern, "Pattern must not be null");
		if (pattern.isEmpty()) {
			return new LogPatternLayout(pattern, Collections.emptyList());
		}
		
		List<LogPatternToken> tokens = Lists.newLinkedList();
		ScopedStringReader reader = new ScopedStringReader(pattern);
		while (reader.canRead()) {
			String str = reader.readUntil(TOKEN_START_CHAR);
			if (!str.isEmpty()) {
				tokens.add(new LogPatternToken(str, StandardLogPatternTokenType.PLAIN_TEXT));
			}
			if (!reader.canRead()) {
				return new LogPatternLayout(pattern, tokens);
			}
			
			reader.skip();
			String tokenIdentifier = reader.readUntil(LogPatternLayoutParser::isTokenIdentifierEndChar);
			LogPatternTokenType tokenType = this.parseTokenType(tokenIdentifier);
			if (tokenType == null) {
				throw new LogPatternException("Unknown token identifier '" + tokenIdentifier + "' at position " + (reader.getIndex() - tokenIdentifier.length()));
			}
			
			if (reader.canRead() && reader.peek() != '{') {
				if (tokenType.requiresConfiguration()) {
					throw new LogPatternException("Token '" + tokenIdentifier + "' at position " + (reader.getIndex() - tokenIdentifier.length()) + " requires configuration, but none was provided");
				}
				
				tokens.add(new LogPatternToken(tokenIdentifier, tokenType));
				continue;
			}
			
			Optional<Class<?>> configurationType = tokenType.getConfigurationType();
			if (!tokenType.supportsConfiguration()) {
				throw new LogPatternException("Token '" + tokenIdentifier + "' at position " + (reader.getIndex() - tokenIdentifier.length()) + " does not support configuration, but configuration was provided");
			}
			if (configurationType.isEmpty()) {
				throw new LogPatternException("Token '" + tokenIdentifier + "' at position " + (reader.getIndex() - tokenIdentifier.length()) + " does not have a configuration type, but configuration was provided");
			}
			
			try {
				Map<String, Object> configItems = reader.readMap(StringReader::readRemaining, CONFIG_VALUE_PARSER);
				
				Object config = construct(configurationType.orElseThrow(), component -> parseConfigParameter(component, configItems, tokenIdentifier, reader.getIndex()));
				tokens.add(new LogPatternToken(tokenIdentifier, tokenType, Optional.of((LogPatternTokenConfig) config)));
			} catch (Exception e) {
				LOGGER.warn("Failed to parse config for token '{}' at position {} skipping token", tokenIdentifier, reader.getIndex(), e);
			}
		}
		return new LogPatternLayout(pattern, tokens);
	}
	
	private @Nullable LogPatternTokenType parseTokenType(@NonNull String tokenIdentifier) {
		for (LogPatternTokenType tokenType : this.tokenTypes) {
			if (tokenType.isIdentifier(tokenIdentifier)) {
				return tokenType;
			}
		}
		return null;
	}
}
