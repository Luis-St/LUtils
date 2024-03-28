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

import net.luis.utils.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Pattern;

import static org.apache.commons.lang3.StringUtils.*;

/**
 *
 * @author Luis-St
 *
 */

public class PatternKey implements Key {
	
	private static final Pattern VALIDATOR = Pattern.compile("^(?!.*(?:\\.-|\\.\\.|--))(?![.\\-])([a-z0-9.*-]+)(?<![.\\-])$");
	
	private final String rawKey;
	private final String[] keyParts;
	
	PatternKey(@NotNull String key) {
		Objects.requireNonNull(key, "Key must not be null");
		if (isBlank(key)) {
			throw new IllegalArgumentException("Key must not be blank");
		}
		this.rawKey = key;
		if (!this.rawKey.contains("*")) {
			throw new IllegalArgumentException("Pattern key must contain at least one wildcard");
		}
		this.keyParts = key.split("\\*");
	}
	
	static boolean isValid(@Nullable String key) {
		if (isBlank(key)) {
			return false;
		}
		if (!VALIDATOR.matcher(key).matches()) {
			return false;
		}
		return key.contains("*") && StringUtils.isSurroundedBy(key, '*', ".");
	}
	
	@Override
	public @NotNull KeyType<PatternKey> getType() {
		return KeyType.PATTERN;
	}
	
	@Override
	public @NotNull String getRawKey() {
		return this.rawKey;
	}
	
	@Override
	public @NotNull String getKey(Object @Nullable ... objects) {
		int wildcards = this.keyParts.length - 1;
		if (objects == null || wildcards > objects.length) {
			throw new IllegalArgumentException("Key '" + this.rawKey + "' requires " + wildcards + " wildcards, but only " + (objects == null ? 0 : objects.length) + " were provided");
		}
		if (objects.length > wildcards) {
			throw new IllegalArgumentException("Too many wildcards (" + objects.length + ") for key '" + this.rawKey + "' provided (expected " + wildcards + ")");
		}
		StringBuilder key = new StringBuilder();
		for (int i = 0; i < this.keyParts.length; i++) {
			key.append(this.keyParts[i]);
			if (i < objects.length) {
				key.append(objects[i]);
			}
		}
		return key.toString();
	}
	
	@Override
	public String @NotNull [] getKeyParts() {
		return this.keyParts;
	}
	
	@Override
	public boolean matches(@Nullable String key) {
		if (isBlank(key) || !VALIDATOR.matcher(key).matches() || key.contains("*")) {
			return false;
		}
		String[] instanceParts = this.rawKey.split("\\.");
		String[] keyParts = key.split("\\.");
		if (instanceParts.length != keyParts.length) {
			return false;
		}
		for (int i = 0; i < keyParts.length; i++) {
			if ("*".equals(instanceParts[i])) {
				continue;
			}
			if (!keyParts[i].equals(instanceParts[i])) {
				return false;
			}
		}
		return true;
	}
	
	//region Object overrides
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof PatternKey that)) return false;
		
		if (!this.rawKey.equals(that.rawKey)) return false;
		return Objects.deepEquals(this.keyParts, that.keyParts);
	}
	
	@Override
	public int hashCode() {
		int result = Objects.hash(this.rawKey);
		result = 31 * result + Arrays.hashCode(this.keyParts);
		return result;
	}
	
	@Override
	public String toString() {
		return this.rawKey;
	}
	//endregion
}
