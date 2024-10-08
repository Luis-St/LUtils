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
import net.luis.utils.io.reader.StringReader;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 *
 * @author Luis-St
 *
 */

public interface ValueGetter {
	
	@NotNull String getString();
	
	default boolean getBoolean() {
		return Boolean.parseBoolean(this.getString());
	}
	
	default @NotNull Number getNumber() {
		return new StringReader(this.getString()).readNumber();
	}
	
	default byte getByte() {
		return this.getNumber().byteValue();
	}
	
	default short getShort() {
		return this.getNumber().shortValue();
	}
	
	default int getInteger() {
		return this.getNumber().intValue();
	}
	
	default long getLong() {
		return this.getNumber().longValue();
	}
	
	default float getFloat() {
		return this.getNumber().floatValue();
	}
	
	default double getDouble() {
		return this.getNumber().doubleValue();
	}
	
	default <T> @NotNull T get(@NotNull ValueParser<String, T> parser) {
		Objects.requireNonNull(parser, "Parser must not be null");
		return parser.parse(this.getString());
	}
}
