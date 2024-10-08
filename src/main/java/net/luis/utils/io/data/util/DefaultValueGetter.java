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

package net.luis.utils.io.data.util;

import net.luis.utils.util.ValueParser;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 *
 * @author Luis-St
 *
 */

public interface DefaultValueGetter extends ValueGetter {
	
	default boolean getBoolean(boolean defaultValue) {
		try {
			return this.getBoolean();
		} catch (Exception e) {
			return defaultValue;
		}
	}
	
	default @NotNull Number getNumber(@NotNull Number defaultValue) {
		try {
			return this.getNumber();
		} catch (Exception e) {
			return Objects.requireNonNull(defaultValue, "Default value must not be null");
		}
	}
	
	default byte getByte(byte defaultValue) {
		try {
			return this.getByte();
		} catch (Exception e) {
			return defaultValue;
		}
	}
	
	default short getShort(short defaultValue) {
		try {
			return this.getShort();
		} catch (Exception e) {
			return defaultValue;
		}
	}
	
	default int getInteger(int defaultValue) {
		try {
			return this.getInteger();
		} catch (Exception e) {
			return defaultValue;
		}
	}
	
	default long getLong(long defaultValue) {
		try {
			return this.getLong();
		} catch (Exception e) {
			return defaultValue;
		}
	}
	
	default float getFloat(float defaultValue) {
		try {
			return this.getFloat();
		} catch (Exception e) {
			return defaultValue;
		}
	}
	
	default double getDouble(double defaultValue) {
		try {
			return this.getDouble();
		} catch (Exception e) {
			return defaultValue;
		}
	}
	
	default <T> @NotNull T get(@NotNull ValueParser<String, T> parser, @NotNull T defaultValue) {
		Objects.requireNonNull(parser, "Parser must not be null");
		try {
			return this.get(parser);
		} catch (Exception e) {
			return Objects.requireNonNull(defaultValue, "Default value must not be null");
		}
	}
}
