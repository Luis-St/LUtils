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

package net.luis.utils.io.codec.provider;

import net.luis.utils.util.Result;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 *
 * @author Luis-St
 *
 */

public interface TypeProvider<T> {
	
	@NotNull T empty();
	
	@NotNull Result<T> createBoolean(boolean value);
	
	@NotNull Result<T> createByte(byte value);
	
	@NotNull Result<T> createShort(short value);
	
	@NotNull Result<T> createInteger(int value);
	
	@NotNull Result<T> createLong(long value);
	
	@NotNull Result<T> createFloat(float value);
	
	@NotNull Result<T> createDouble(double value);
	
	@NotNull Result<T> createString(@NotNull String value);
	
	@NotNull Result<T> createList(@NotNull List<? extends T> values);
	
	@NotNull Result<T> createMap();
	
	@NotNull Result<T> createMap(@NotNull Map<String, ? extends T> values);
	
	@NotNull Result<T> getEmpty(@NotNull T type);
	
	@NotNull Result<Boolean> getBoolean(@NotNull T type);
	
	@NotNull Result<Byte> getByte(@NotNull T type);
	
	@NotNull Result<Short> getShort(@NotNull T type);
	
	@NotNull Result<Integer> getInteger(@NotNull T type);
	
	@NotNull Result<Long> getLong(@NotNull T type);
	
	@NotNull Result<Float> getFloat(@NotNull T type);
	
	@NotNull Result<Double> getDouble(@NotNull T type);
	
	@NotNull Result<String> getString(@NotNull T type);
	
	@NotNull Result<List<T>> getList(@NotNull T type);
	
	@NotNull Result<Map<String, T>> getMap(@NotNull T type);
	
	@NotNull Result<Boolean> has(@NotNull T type, @NotNull String key);
	
	@NotNull Result<T> get(@NotNull T type, @NotNull String key);
	
	@NotNull Result<T> set(@NotNull T type, @NotNull String key, @NotNull T value);
	
	default @NotNull Result<T> set(@NotNull T type, @NotNull String key, @NotNull Result<T> value) {
		Objects.requireNonNull(type, "Type must not be null");
		Objects.requireNonNull(key, "Key must not be null");
		Objects.requireNonNull(value, "Value must not be null");
		if (value.isSuccess()) {
			return this.set(type, key, value.orThrow());
		}
		return Result.error("Unable to set value for key '" + key + "' in '" + type + "': " + value.errorOrThrow());
	}
	
	@NotNull Result<T> merge(@NotNull T current, @NotNull T value);
	
	default @NotNull Result<T> merge(@NotNull T current, @NotNull Result<T> value) {
		Objects.requireNonNull(current, "Current value must not be null");
		Objects.requireNonNull(value, "Value must not be null");
		if (value.isSuccess()) {
			return this.merge(current, value.orThrow());
		}
		return Result.error("Unable to merge '" + current + "' with 'value': " + value.errorOrThrow());
	}
}
