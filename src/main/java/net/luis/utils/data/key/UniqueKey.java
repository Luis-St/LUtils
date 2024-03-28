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

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 *
 * @author Luis-St
 *
 */

public class UniqueKey implements Key {
	
	private static final Pattern VALIDATOR = Pattern.compile("^([0-9a-f]{8})-([0-9a-f]{4})-([0-9a-f]{4})-([0-9a-f]{4})-([0-9a-f]{12})$");
	
	private final String key;
	
	UniqueKey(@NotNull String key) {
		Objects.requireNonNull(key, "Key must not be null");
		if (StringUtils.isBlank(key)) {
			throw new IllegalArgumentException("Key must not be blank");
		}
		this.key = key;
	}
	
	static boolean isValid(@Nullable String key) {
		if (StringUtils.isBlank(key)) {
			return false;
		}
		return VALIDATOR.matcher(key).matches();
	}
	
	@Override
	public @NotNull KeyType<UniqueKey> getType() {
		return KeyType.UNIQUE;
	}
	
	@Override
	public @NotNull String getRawKey() {
		return this.key;
	}
	
	@Override
	public String @NotNull [] getKeyParts() {
		return new String[] { this.key };
	}
	
	//region Object overrides
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof UniqueKey uniqueKey)) return false;
		
		return this.key.equals(uniqueKey.key);
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
