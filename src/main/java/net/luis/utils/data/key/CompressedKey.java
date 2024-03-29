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

package net.luis.utils.data.key;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static net.luis.utils.lang.StringUtils.*;
import static org.apache.commons.lang3.StringUtils.*;

/**
 *
 * @author Luis-St
 *
 */

public class CompressedKey implements Key {
	
	private static final Pattern VALIDATOR = Pattern.compile("^(?!.*(?:\\.-|\\.\\.|--))(?![.\\[\\]-])([a-z0-9.,;|\\[\\]-]+)(?<![.\\[\\]-])$");
	private static final Pattern COMPRESSED_VALIDATOR = Pattern.compile("^\\[(\\s*[a-z0-9-]+\\s*[,;|])*(\\s*[a-z0-9-]+\\s*)]$");
	
	private final String key;
	private final String[] base;
	private final String[] compressed;
	
	CompressedKey(@NotNull String key) {
		Objects.requireNonNull(key, "Key must not be null");
		if (StringUtils.isBlank(key)) {
			throw new IllegalArgumentException("Key must not be blank");
		}
		this.key = key;
		if (!this.key.contains("[") && !this.key.contains("]")) {
			throw new IllegalArgumentException("Compressed key must contain at least one compressed part");
		}
		this.base = this.key.replaceAll("\\[.*?]", "").split("\\.\\.");
		this.compressed = extract(this.key, "\\[.*?]");
		if (Stream.of(this.compressed).anyMatch(CompressedKey::containsEmptyValue)) {
			throw new IllegalArgumentException("Compressed parts must not contain empty values");
		}
	}
	
	static boolean isValid(@Nullable String key) {
		if (isBlank(key)) {
			return false;
		}
		if (!VALIDATOR.matcher(key).matches()) {
			return false;
		}
		if (containsNone(key, '[', ']') || !matchingBalanced(key, "[", "]")) {
			return false;
		}
		if (!isBeforeAllOccurrence(key, ']', ".") || !isAfterAllOccurrence(key, '[', ".")) {
			return false;
		}
		return isBaseValid(key) && isCompressedValid(key);
	}
	
	//region Additional validation
	private static boolean isBaseValid(@NotNull String key) {
		Objects.requireNonNull(key, "Key must not be null");
		return NamespaceKey.isValid(key.replaceAll("\\[.*?]\\.", ""));
	}
	
	private static boolean isCompressedValid(@NotNull String key) {
		Objects.requireNonNull(key, "Key must not be null");
		String[] brackets = key.split("\\[.*?]");
		return Arrays.stream(brackets).filter(part -> matchesPattern(COMPRESSED_VALIDATOR, part) && !containsEmptyValue(part)).count() == brackets.length;
	}
	
	private static boolean containsEmptyValue(@NotNull String compressed) {
		Objects.requireNonNull(compressed, "Compressed part must not be null");
		return Stream.of(strip(compressed, "[]").split("[,|;]")).noneMatch(String::isBlank);
	}
	//endregion
	
	@Override
	public @NotNull KeyType<CompressedKey> getType() {
		return KeyType.COMPRESSED;
	}
	
	@Override
	public @NotNull String getRawKey() {
		return this.key;
	}
	
	@Override
	public String @NotNull [] getKeyParts() {
		return this.key.split("\\.");
	}
	
	@Override
	public @NotNull String getKey(Object @Nullable ... objects) {
		if (objects == null || this.compressed.length > objects.length) {
			throw new IllegalArgumentException("Key '" + this.key + "' requires " + this.compressed.length + " compressed values, but only " + ArrayUtils.getLength(objects) + " were provided");
		}
		StringBuilder key = new StringBuilder();
		for (int i = 0; i < this.base.length; i++) {
			key.append(this.base[i]);
			String compressed = objects[i].toString();
			if (!this.isValidCompressed(i, compressed)) {
				throw new IllegalArgumentException("Compressed value '" + compressed + "' is not valid for key '" + this.key + "'");
			}
			key.append(".").append(compressed).append(".");
		}
		return key.toString();
	}
	
	private boolean isValidCompressed(int index, @NotNull String compressed) {
		Objects.requireNonNull(compressed, "Compressed part must not be null");
		String[] possible = strip(this.compressed[index], "[]").split("[,|;]");
		return Stream.of(possible).map(String::strip).anyMatch(compressed::equals);
	}
	
	@Override
	public boolean matches(@Nullable String key) {
		if (key == null || !NamespaceKey.isValid(key)) {
			return false;
		}
		String current = key;
		for (int i = 0; i < this.base.length; i++) {
			String base = this.base[i];
			if (!current.startsWith(base)) {
				return false;
			}
			current = current.substring(base.length() + (i != this.base.length - 1 ? 1 : 0));
			if (i < this.compressed.length) {
				int matching = this.getMatchingCompressed(strip(this.compressed[i], "[]").split("[,|;]"), current);
				if (matching == -1) {
					return false;
				}
				current = current.substring(matching);
			}
		}
		return true;
	}
	
	private int getMatchingCompressed(String @NotNull [] compressed, @NotNull String current) {
		Objects.requireNonNull(compressed, "Compressed parts must not be null");
		Objects.requireNonNull(current, "Current key must not be null");
		for (String part : compressed) {
			if (current.startsWith(part.strip()) && !part.isBlank()) {
				return part.length() + 1;
			}
		}
		return -1;
	}
	
	//region Object overrides
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof CompressedKey that)) return false;
		
		return this.key.equals(that.key);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.key);
	}
	
	@Override
	public String toString() {
		return this.key;
	}
	//endregion
}
