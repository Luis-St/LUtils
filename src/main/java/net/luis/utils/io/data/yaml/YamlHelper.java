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

package net.luis.utils.io.data.yaml;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 *
 * @author Luis-St
 *
 */

final class YamlHelper {
	
	private static final Pattern COLON_WHITESPACE_PATTERN = Pattern.compile(":\\s");
	private static final Pattern WHITESPACE_PATTERN = Pattern.compile("\\s");
	static final String[] SPECIAL_CHARACTERS = new String[] { "*", "&", "!", ">", "|", "#", "@", "%", "[", "{", "'", "\"" };
	
	private YamlHelper() {}
	
	static void validateYamlKey(@NotNull String key) {
		Objects.requireNonNull(key, "Key must not be null");
		if (key.isBlank()) {
			throw new IllegalArgumentException("Key must not be empty or blank");
		}
		if (StringUtils.startsWithAny(key, SPECIAL_CHARACTERS)) {
			throw new IllegalArgumentException("Key must not start with a special character (*, &, !, >, |, #, @, %, [, {, ' or \")");
		}
		if (COLON_WHITESPACE_PATTERN.matcher(key).find()) {
			throw new IllegalArgumentException("Key must not contain a colon followed by a space");
		}
	}
	
	static void validateYamlAnchor(@NotNull String anchor) {
		Objects.requireNonNull(anchor, "Anchor must not be null");
		if (anchor.isBlank()) {
			throw new IllegalArgumentException("Anchor must not be empty or blank");
		}
		if (StringUtils.containsAny(anchor, "[", "]", "{", "}")) {
			throw new IllegalArgumentException("Anchor must not start with a special character ([, ], {, })");
		}
		if (WHITESPACE_PATTERN.matcher(anchor).find()) {
			throw new IllegalArgumentException("Anchor must not contain a whitespace");
		}
	}
}
