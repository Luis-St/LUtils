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
import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.encoder.EncoderException;
import net.luis.utils.io.codec.provider.JavaTypeProvider;
import net.luis.utils.io.reader.*;
import net.luis.utils.logging.formatter.pattern.token.*;
import net.luis.utils.logging.formatter.pattern.token.config.LogPatternTokenConfig;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 *
 * @author Luis-St
 *
 */

public class LogPatternLayoutParser {
	
	private static final Function<ScopedStringReader, Object> CONFIG_VALUE_PARSER = reader -> {
		Object value = reader.readObject();
		reader.skipWhitespaces();
		
		if (reader.canRead()) {
			throw new LogPatternException("Unexpected trailing input '" + reader.readRemaining() + "' in configuration value");
		}
		return value;
	};
	private static final char TOKEN_START_CHAR = '$';
	private static final String EMBEDDED_CONFIG_SEPARATOR = ".";
	
	private final Set<LogPatternTokenType> tokenTypes;
	
	public LogPatternLayoutParser(@NonNull Set<LogPatternTokenType> tokenTypes) {
		this.tokenTypes = Set.copyOf(Objects.requireNonNull(tokenTypes, "Token types must not be null"));
	}
	
	//region Static helpers
	private static boolean isTokenIdentifierEndChar(char c) {
		if (c == '{') {
			return true;
		}
		
		char lower = Character.toLowerCase(c);
		return (lower < 'a' || lower > 'z') && (lower < '0' || lower > '9') && lower != '_';
	}
	
	private static @NonNull Map<String, Object> nestConfigItems(@NonNull Map<String, Object> configItems) {
		Objects.requireNonNull(configItems, "Config items must not be null");
		
		Map<String, Object> nestedItems = Maps.newLinkedHashMap();
		Set<Map<String, Object>> createdMaps = Sets.newIdentityHashSet();
		for (Map.Entry<String, Object> entry : configItems.entrySet()) {
			putConfigItem(nestedItems, createdMaps, "", entry.getKey(), entry.getValue());
		}
		return nestedItems;
	}
	
	private static void putConfigItem(@NonNull Map<String, Object> current, @NonNull Set<Map<String, Object>> createdMaps, @NonNull String prefix, @NonNull String key, @Nullable Object value) {
		Objects.requireNonNull(current, "Current config items must not be null");
		Objects.requireNonNull(createdMaps, "Created maps must not be null");
		Objects.requireNonNull(prefix, "Prefix must not be null");
		Objects.requireNonNull(key, "Config key must not be null");
		
		String fullKey = prefix.isEmpty() ? key : prefix + EMBEDDED_CONFIG_SEPARATOR + key;
		String[] parts = key.split(Pattern.quote(EMBEDDED_CONFIG_SEPARATOR), -1);
		String path = prefix;
		for (int i = 0; i < parts.length; i++) {
			String part = parts[i].strip();
			if (part.isEmpty()) {
				throw new LogPatternException("Config key '" + fullKey + "' not valid, it contains an empty part");
			}
			
			path = path.isEmpty() ? part : path + EMBEDDED_CONFIG_SEPARATOR + part;
			if (i < parts.length - 1 || value instanceof Map<?, ?>) {
				current = getOrCreateNestedItems(current, createdMaps, part, path, fullKey);
			} else if (current.containsKey(part)) {
				throw new LogPatternException("Config key '" + fullKey + "' conflicts with another config key that already defines '" + path + "'");
			} else {
				current.put(part, value);
			}
		}
		
		if (value instanceof Map<?, ?> map) {
			for (Map.Entry<?, ?> entry : map.entrySet()) {
				if (!(entry.getKey() instanceof String nestedKey)) {
					throw new LogPatternException("Config key '" + entry.getKey() + "' in config item '" + fullKey + "' not valid, it must be a string");
				}
				
				putConfigItem(current, createdMaps, path, nestedKey, entry.getValue());
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private static @NonNull Map<String, Object> getOrCreateNestedItems(@NonNull Map<String, Object> current, @NonNull Set<Map<String, Object>> createdMaps, @NonNull String part, @NonNull String path, @NonNull String fullKey) {
		Objects.requireNonNull(current, "Current config items must not be null");
		Objects.requireNonNull(createdMaps, "Created maps must not be null");
		Objects.requireNonNull(part, "Key part must not be null");
		Objects.requireNonNull(path, "Path must not be null");
		Objects.requireNonNull(fullKey, "Config key must not be null");
		
		Object existing = current.get(part);
		if (existing == null && !current.containsKey(part)) {
			Map<String, Object> created = Maps.newLinkedHashMap();
			createdMaps.add(created);
			current.put(part, created);
			return created;
		} else if (existing instanceof Map<?, ?> map && createdMaps.contains(map)) {
			return (Map<String, Object>) map;
		}
		throw new LogPatternException("Config key '" + fullKey + "' conflicts with config key '" + path + "' which already defines a value");
	}
	
	@SuppressWarnings("unchecked")
	private static <C extends LogPatternTokenConfig> void validateConfigKeys(@NonNull Codec<C> codec, @NonNull LogPatternTokenConfig config, @NonNull Map<String, Object> configItems) throws EncoderException {
		Objects.requireNonNull(codec, "Codec must not be null");
		Objects.requireNonNull(config, "Config must not be null");
		Objects.requireNonNull(configItems, "Config items must not be null");
		
		Object encoded = codec.encode(JavaTypeProvider.DEFAULT, JavaTypeProvider.DEFAULT.empty(), (C) config);
		validateConfigKeys(configItems, encoded instanceof Map<?, ?> map ? map : Map.of(), "");
	}
	
	@SuppressWarnings("unchecked")
	private static void validateConfigKeys(@NonNull Map<String, Object> configItems, @NonNull Map<?, ?> encodedItems, @NonNull String prefix) {
		Objects.requireNonNull(configItems, "Config items must not be null");
		Objects.requireNonNull(encodedItems, "Encoded config items must not be null");
		Objects.requireNonNull(prefix, "Prefix must not be null");
		
		for (Map.Entry<String, Object> entry : configItems.entrySet()) {
			String fullKey = prefix.isEmpty() ? entry.getKey() : prefix + EMBEDDED_CONFIG_SEPARATOR + entry.getKey();
			if (!encodedItems.containsKey(entry.getKey())) {
				throw new LogPatternException("Config key '" + fullKey + "' is unknown or its value '" + entry.getValue() + "' is not valid");
			}
			
			if (entry.getValue() instanceof Map<?, ?> nestedItems && encodedItems.get(entry.getKey()) instanceof Map<?, ?> nestedEncodedItems) {
				validateConfigKeys((Map<String, Object>) nestedItems, nestedEncodedItems, fullKey);
			}
		}
	}
	//endregion
	
	public @NonNull LogPatternLayout parse(@NonNull String pattern) {
		Objects.requireNonNull(pattern, "Pattern must not be null");
		if (pattern.isEmpty()) {
			return new LogPatternLayout(pattern, Collections.emptyList());
		}
		
		List<LogPatternToken> tokens = Lists.newArrayList();
		ScopedStringReader reader = new ScopedStringReader(pattern, StringReadingOption.IGNORE_ESCAPES, StringReadingOption.IGNORE_SINGLE_QUOTES, StringReadingOption.IGNORE_DOUBLE_QUOTES);
		while (reader.canRead()) {
			String str = reader.readUntil(TOKEN_START_CHAR);
			if (!str.isEmpty()) {
				tokens.add(new LogPatternToken(str, StandardLogPatternTokenType.PLAIN_TEXT));
			}
			if (!reader.canRead()) {
				return new LogPatternLayout(pattern, tokens);
			}
			
			reader.skip();
			
			int position = reader.getIndex();
			String tokenIdentifier = reader.readUntil(LogPatternLayoutParser::isTokenIdentifierEndChar);
			LogPatternTokenType tokenType = this.parseTokenType(tokenIdentifier);
			if (tokenType == null) {
				throw new LogPatternException("Unknown token identifier '" + tokenIdentifier + "' at position " + position);
			}
			
			if (!reader.canRead() || reader.peek() != '{') {
				if (tokenType.requiresConfiguration()) {
					throw new LogPatternException("Token '" + tokenIdentifier + "' at position " + position + " requires configuration, but none was provided");
				}
				
				tokens.add(new LogPatternToken(tokenIdentifier, tokenType));
				continue;
			}
			
			Optional<Codec<? extends LogPatternTokenConfig>> codec = tokenType.codec();
			if (!tokenType.supportsConfiguration()) {
				throw new LogPatternException("Token '" + tokenIdentifier + "' at position " + position + " does not support configuration, but configuration was provided");
			}
			if (codec.isEmpty()) {
				throw new LogPatternException("Token '" + tokenIdentifier + "' at position " + position + " does not need a configuration, but configuration was provided");
			}
			
			try {
				Map<String, Object> configItems = nestConfigItems(reader.readMap(StringReader::readRemaining, CONFIG_VALUE_PARSER));
				LogPatternTokenConfig config = codec.get().decode(JavaTypeProvider.DEFAULT, configItems);
				validateConfigKeys(codec.get(), config, configItems);
				tokens.add(new LogPatternToken(tokenIdentifier, tokenType, Optional.of(config)));
			} catch (Exception e) {
				throw new LogPatternException("Failed to parse config for token '" + tokenIdentifier + "' at position " + position + " skipping token", e);
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
