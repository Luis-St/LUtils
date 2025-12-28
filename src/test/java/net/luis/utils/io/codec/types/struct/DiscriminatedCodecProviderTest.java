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

package net.luis.utils.io.codec.types.struct;

import net.luis.utils.io.codec.Codec;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.function.Function;

import static net.luis.utils.io.codec.Codecs.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link DiscriminatedCodecProvider}.<br>
 *
 * @author Luis-St
 */
class DiscriminatedCodecProviderTest {
	
	@Test
	void createWithFunctionNullChecks() {
		Function<String, Codec<? extends String>> function = type -> STRING;
		
		assertThrows(NullPointerException.class, () -> DiscriminatedCodecProvider.create(null, function));
		assertThrows(NullPointerException.class, () -> DiscriminatedCodecProvider.create(String.class, (Function<String, Codec<? extends String>>) null));
	}
	
	@Test
	void createWithFunctionValid() {
		Function<String, Codec<? extends Number>> function = type -> switch (type) {
			case "int" -> INTEGER;
			case "long" -> LONG;
			case "double" -> DOUBLE;
			default -> throw new IllegalArgumentException("Unknown type: " + type);
		};
		
		DiscriminatedCodecProvider<Number, String> provider = DiscriminatedCodecProvider.create(Number.class, function);
		
		assertNotNull(provider);
		assertEquals(Number.class, provider.getCodecType());
	}
	
	@Test
	void createWithFunctionReturnsCorrectCodecs() {
		Function<String, Codec<? extends Number>> function = type -> switch (type) {
			case "int" -> INTEGER;
			case "long" -> LONG;
			case "double" -> DOUBLE;
			default -> throw new IllegalArgumentException("Unknown type: " + type);
		};
		
		DiscriminatedCodecProvider<Number, String> provider = DiscriminatedCodecProvider.create(Number.class, function);
		
		assertEquals(INTEGER, provider.getCodec("int"));
		assertEquals(LONG, provider.getCodec("long"));
		assertEquals(DOUBLE, provider.getCodec("double"));
	}
	
	@Test
	void createWithFunctionThrowsForUnknownDiscriminator() {
		Function<String, Codec<? extends Number>> function = type -> switch (type) {
			case "int" -> INTEGER;
			case "long" -> LONG;
			default -> throw new IllegalArgumentException("Unknown type: " + type);
		};
		
		DiscriminatedCodecProvider<Number, String> provider = DiscriminatedCodecProvider.create(Number.class, function);
		
		assertThrows(IllegalArgumentException.class, () -> provider.getCodec("unknown"));
	}
	
	@Test
	void createWithMapNullChecks() {
		Map<String, Codec<? extends String>> map = Map.of("a", STRING);
		
		assertThrows(NullPointerException.class, () -> DiscriminatedCodecProvider.create(null, map));
		assertThrows(NullPointerException.class, () -> DiscriminatedCodecProvider.create(String.class, (Map<String, Codec<? extends String>>) null));
	}
	
	@Test
	void createWithMapValid() {
		Map<String, Codec<? extends Number>> map = Map.of(
			"int", INTEGER,
			"long", LONG,
			"double", DOUBLE
		);
		
		DiscriminatedCodecProvider<Number, String> provider = DiscriminatedCodecProvider.create(Number.class, map);
		
		assertNotNull(provider);
		assertEquals(Number.class, provider.getCodecType());
	}
	
	@Test
	void createWithMapReturnsCorrectCodecs() {
		Map<String, Codec<? extends Number>> map = Map.of(
			"int", INTEGER,
			"long", LONG,
			"double", DOUBLE
		);
		
		DiscriminatedCodecProvider<Number, String> provider = DiscriminatedCodecProvider.create(Number.class, map);
		
		assertEquals(INTEGER, provider.getCodec("int"));
		assertEquals(LONG, provider.getCodec("long"));
		assertEquals(DOUBLE, provider.getCodec("double"));
	}
	
	@Test
	void createWithMapReturnsNullForUnknownDiscriminator() {
		Map<String, Codec<? extends Number>> map = Map.of(
			"int", INTEGER,
			"long", LONG
		);
		
		DiscriminatedCodecProvider<Number, String> provider = DiscriminatedCodecProvider.create(Number.class, map);
		
		assertNull(provider.getCodec("unknown"));
	}
	
	@Test
	void createWithEmptyMap() {
		Map<String, Codec<? extends String>> emptyMap = Map.of();
		
		DiscriminatedCodecProvider<String, String> provider = DiscriminatedCodecProvider.create(String.class, emptyMap);
		
		assertNotNull(provider);
		assertEquals(String.class, provider.getCodecType());
		assertNull(provider.getCodec("any"));
	}
	
	@Test
	void createWithSingleEntryMap() {
		Map<String, Codec<? extends String>> singleEntryMap = Map.of("string", STRING);
		
		DiscriminatedCodecProvider<String, String> provider = DiscriminatedCodecProvider.create(String.class, singleEntryMap);
		
		assertNotNull(provider);
		assertEquals(String.class, provider.getCodecType());
		assertEquals(STRING, provider.getCodec("string"));
		assertNull(provider.getCodec("other"));
	}
	
	@Test
	void createWithComplexTypes() {
		// Test with different codec types
		Map<Integer, Codec<? extends CharSequence>> map = Map.of(
			1, STRING,
			2, STRING
		);
		
		DiscriminatedCodecProvider<CharSequence, Integer> provider = DiscriminatedCodecProvider.create(CharSequence.class, map);
		
		assertNotNull(provider);
		assertEquals(CharSequence.class, provider.getCodecType());
		assertEquals(STRING, provider.getCodec(1));
		assertEquals(STRING, provider.getCodec(2));
		assertNull(provider.getCodec(3));
	}
	
	@Test
	void functionBasedProviderWithNullReturnValue() {
		Function<String, Codec<? extends String>> function = type -> null;
		
		DiscriminatedCodecProvider<String, String> provider = DiscriminatedCodecProvider.create(String.class, function);
		
		assertNotNull(provider);
		assertNull(provider.getCodec("any"));
	}
}
