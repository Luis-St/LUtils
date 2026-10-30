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
import org.jetbrains.annotations.Unmodifiable;
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

public class LogPatternParser {
	
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
	
	public LogPatternParser(@NonNull Set<LogPatternTokenType> tokenTypes) {
		this.tokenTypes = validateTokenTypes(tokenTypes);
	}
	
	//region Static helpers
	private static @NonNull @Unmodifiable Set<LogPatternTokenType> validateTokenTypes(@Nullable Set<LogPatternTokenType> tokenTypes) {
		Objects.requireNonNull(tokenTypes, "Token types must not be null");
		
		Set<String> identifiers = Sets.newHashSet();
		for (LogPatternTokenType tokenType : tokenTypes) {
			Set<String> intersection = Sets.intersection(identifiers, tokenType.getIdentifiers());
			if (!intersection.isEmpty()) {
				throw new LogPatternException("Log pattern token " + tokenType.getName() + " shares " + intersection.size() + " identifiers with at least one other token type: " + String.join(", ", intersection));
			}
			identifiers.addAll(tokenType.getIdentifiers());
			
			if (tokenType.supportsConfiguration() && tokenType.codec().isEmpty()) {
				throw new LogPatternException("Log pattern token " + tokenType.getName() + " supports configuration, but no codec to parse the configuration was provided");
			}
			if (tokenType.requiresConfiguration() && !tokenType.supportsConfiguration()) {
				throw new LogPatternException("Log pattern token " + tokenType.getName() + " requires configuration, but does not support configuration");
			}
		}
		return Set.copyOf(tokenTypes);
	}
	
	private static boolean isTokenIdentifierEndChar(char c) {
		if (c == '{') {
			return true;
		}
		
		char lower = Character.toLowerCase(c);
		return (lower < 'a' || lower > 'z') && (lower < '0' || lower > '9') && lower != '_' &&  lower != ':';
	}
	
	private static @NonNull Map<String, Object> nestConfigItems(@NonNull Map<String, Object> configItems) {
		Objects.requireNonNull(configItems, "Config items must not be null");
		
		Map<String, Object> nestedItems = Maps.newLinkedHashMap();
		Set<Map<String, Object>> createdMaps = Sets.newIdentityHashSet();
		for (Map.Entry<String, Object> entry : configItems.entrySet()) {
			putConfigItem(nestedItems, createdMaps, "", entry.getKey(), entry.getValue(), true);
		}
		return nestedItems;
	}
	
	private static void putConfigItem(@NonNull Map<String, Object> current, @NonNull Set<Map<String, Object>> createdMaps, @NonNull String prefix, @NonNull String key, @Nullable Object value, boolean splitKey) {
		Objects.requireNonNull(current, "Current config items must not be null");
		Objects.requireNonNull(createdMaps, "Created maps must not be null");
		Objects.requireNonNull(prefix, "Prefix must not be null");
		Objects.requireNonNull(key, "Config key must not be null");
		
		String fullKey = prefix.isEmpty() ? key : prefix + EMBEDDED_CONFIG_SEPARATOR + key;
		String[] parts = splitKey ? key.split(Pattern.quote(EMBEDDED_CONFIG_SEPARATOR), -1) : new String[] { key };
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
				
				putConfigItem(current, createdMaps, path, nestedKey, entry.getValue(), false);
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
	
	public @NonNull LogPattern parse(@NonNull String pattern) {
		Objects.requireNonNull(pattern, "Pattern must not be null");
		if (pattern.isEmpty()) {
			return new LogPattern(pattern, Collections.emptyList());
		}
		
		List<LogPatternToken> tokens = Lists.newArrayList();
		QuotedStringReader reader = new QuotedStringReader(pattern, null, StringQuoting.NONE);
		while (reader.canRead()) {
			String str = reader.readUntil(TOKEN_START_CHAR);
			if (!str.isEmpty()) {
				tokens.add(new LogPatternToken(str, StandardLogPatternTokenType.PLAIN_TEXT));
			}
			if (!reader.canRead()) {
				return new LogPattern(pattern, tokens);
			}
			
			StringPosition tokenPosition = reader.getPosition();
			reader.skip();
			
			if (reader.peek() == TOKEN_START_CHAR) {
				reader.skip();
				tokens.add(new LogPatternToken("$", StandardLogPatternTokenType.PLAIN_TEXT));
				continue;
			}
			
			String tokenIdentifier = reader.readUntil(LogPatternParser::isTokenIdentifierEndChar).toLowerCase();
			LogPatternTokenType tokenType = this.parseTokenType(tokenIdentifier);
			if (tokenType == null) {
				throw new LogPatternException("Unknown token identifier '" + tokenIdentifier + "' at " + tokenPosition);
			}
			
			if (!reader.canRead() || reader.peek() != '{') {
				if (tokenType.requiresConfiguration()) {
					throw new LogPatternException("Token '" + tokenIdentifier + "' at " + tokenPosition + " requires configuration, but none was provided");
				}
				
				tokens.add(new LogPatternToken(tokenIdentifier, tokenType));
				continue;
			}
			
			StringPosition configPosition = reader.getPosition();
			Optional<Codec<? extends LogPatternTokenConfig>> codec = tokenType.codec();
			if (!tokenType.supportsConfiguration()) {
				throw new LogPatternException("Token '" + tokenIdentifier + "' at " + configPosition + " does not support configuration, but configuration was provided");
			}
			if (codec.isEmpty()) {
				throw new LogPatternException("Token '" + tokenIdentifier + "' at " + configPosition + " does not need a configuration, but configuration was provided");
			}
			
			int start = reader.getIndex();
			try (ScopedStringReader scopedReader = reader.createSubReader(s -> new ScopedStringReader(s, ScopedStringReader.DuplicationStrategy.THROW))) {
				Map<String, Object> configItems = nestConfigItems(scopedReader.readMap(StringReader::readRemaining, CONFIG_VALUE_PARSER));
				LogPatternTokenConfig config = codec.get().decode(JavaTypeProvider.DEFAULT, configItems);
				validateConfigKeys(codec.get(), config, configItems);
				tokens.add(new LogPatternToken("$" + tokenIdentifier + scopedReader.getString().substring(start, scopedReader.getIndex()), tokenType, Optional.of(config)));
			} catch (Exception e) {
				if (e instanceof LogPatternException ex) {
					throw ex;
				}
				throw new LogPatternException("Failed to parse config for token '" + tokenIdentifier + "' at " + configPosition + " skipping token", e);
			}
		}
		return new LogPattern(pattern, tokens);
	}
	
	private @Nullable LogPatternTokenType parseTokenType(@NonNull String tokenIdentifier) {
		Objects.requireNonNull(tokenIdentifier,  "Token identifier must not be null");
		
		for (LogPatternTokenType tokenType : this.tokenTypes) {
			if (tokenType.isIdentifier(tokenIdentifier)) {
				return tokenType;
			}
		}
		return null;
	}
}
