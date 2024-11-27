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

package net.luis.utils.io.data.properties;

import net.luis.utils.annotation.type.MockObject;
import net.luis.utils.io.data.OutputProvider;
import net.luis.utils.util.ErrorAction;
import net.luis.utils.util.ValueConverter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link PropertyWriter}.<br>
 *
 * @author Luis-St
 */
class PropertyWriterTest {
	
	private static final ValueConverter<String, String> CONVERTER = new ValueConverter<String, String>() {
		@Override
		public @NotNull String convert(@Nullable String value) {
			return String.valueOf(value);
		}
		
		@Override
		public @NotNull String parse(@Nullable String value) {
			return String.valueOf(value);
		}
	};
	private static final PropertyConfig DEFAULT_CONFIG = PropertyConfig.DEFAULT;
	private static final PropertyConfig CUSTOM_CONFIG = new PropertyConfig(':', 0, Set.of(';'), Pattern.compile("^[a-z._]+$"), Pattern.compile("^[ a-zA-Z0-9._-]*$"), true, StandardCharsets.UTF_8, ErrorAction.IGNORE);
	private static final Properties TEST_PROPERTIES = new Properties(List.of(Property.of("key", "value")));
	
	@Test
	void constructor() {
		OutputProvider provider = new OutputProvider(OutputStream.nullOutputStream());
		
		assertThrows(NullPointerException.class, () -> new PropertyWriter(null));
		assertDoesNotThrow(() -> new PropertyWriter(provider));
		
		assertThrows(NullPointerException.class, () -> new PropertyWriter(null, PropertyConfig.DEFAULT));
		assertThrows(NullPointerException.class, () -> new PropertyWriter(provider, null));
		assertDoesNotThrow(() -> new PropertyWriter(provider, PropertyConfig.DEFAULT));
	}
	
	@Test
	void writePropertyDefaultConfig() {
		StringOutputStream stream = new StringOutputStream();
		PropertyWriter writer = new PropertyWriter(new OutputProvider(stream), DEFAULT_CONFIG);
		
		assertThrows(NullPointerException.class, () -> writer.writeProperty(null, "value"));
		assertThrows(NullPointerException.class, () -> writer.writeProperty("key", null));
		assertDoesNotThrow(() -> writer.writeProperty("key", "value"));
		assertEquals("key = value" + System.lineSeparator(), stream.toString());
		stream.reset();
		
		assertThrows(NullPointerException.class, () -> writer.writeProperty(null, "value", CONVERTER));
		assertThrows(NullPointerException.class, () -> writer.writeProperty("key", null, CONVERTER));
		assertDoesNotThrow(() -> writer.writeProperty("key", "value", CONVERTER));
		assertEquals("key = value" + System.lineSeparator(), stream.toString());
		stream.reset();
		
		assertThrows(NullPointerException.class, () -> writer.writeProperty((Properties) null));
		assertDoesNotThrow(() -> writer.writeProperty(TEST_PROPERTIES));
		assertEquals("key = value" + System.lineSeparator(), stream.toString());
		stream.reset();
		
		assertThrows(NullPointerException.class, () -> writer.writeProperty((Property) null));
		assertDoesNotThrow(() -> writer.writeProperty(Property.of("key", "value")));
		assertEquals("key = value" + System.lineSeparator(), stream.toString());
		stream.reset();
	}
	
	@Test
	void writePropertyCustomConfig() {
		StringOutputStream stream = new StringOutputStream();
		PropertyWriter writer = new PropertyWriter(new OutputProvider(stream), CUSTOM_CONFIG);
		
		assertThrows(NullPointerException.class, () -> writer.writeProperty(null, "value"));
		assertThrows(NullPointerException.class, () -> writer.writeProperty("key", null));
		assertDoesNotThrow(() -> writer.writeProperty("key", "value"));
		assertEquals("key:value" + System.lineSeparator(), stream.toString());
		stream.reset();
		
		assertThrows(NullPointerException.class, () -> writer.writeProperty(null, "value", CONVERTER));
		assertThrows(NullPointerException.class, () -> writer.writeProperty("key", null, CONVERTER));
		assertDoesNotThrow(() -> writer.writeProperty("key", "value", CONVERTER));
		assertEquals("key:value" + System.lineSeparator(), stream.toString());
		stream.reset();
		
		assertThrows(NullPointerException.class, () -> writer.writeProperty((Properties) null));
		assertDoesNotThrow(() -> writer.writeProperty(TEST_PROPERTIES));
		assertEquals("key:value" + System.lineSeparator(), stream.toString());
		stream.reset();
		
		assertThrows(NullPointerException.class, () -> writer.writeProperty((Property) null));
		assertDoesNotThrow(() -> writer.writeProperty(Property.of("key", "value")));
		assertEquals("key:value" + System.lineSeparator(), stream.toString());
		stream.reset();
	}
	
	@Test
	void close() {
		assertDoesNotThrow(() -> new PropertyWriter(new OutputProvider(OutputStream.nullOutputStream())).close());
	}
	
	//region Internal classes
	@MockObject(OutputStream.class)
	private static class StringOutputStream extends OutputStream {
		
		private final StringBuilder builder = new StringBuilder();
		
		@Override
		public void write(int b) {
			this.builder.append((char) b);
		}
		
		public void reset() {
			this.builder.setLength(0);
		}
		
		@Override
		public String toString() {
			return this.builder.toString();
		}
	}
	//endregion
}
