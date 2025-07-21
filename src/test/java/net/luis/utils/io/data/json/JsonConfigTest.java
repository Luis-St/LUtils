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

package net.luis.utils.io.data.json;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link JsonConfig}.<br>
 *
 * @author Luis-St
 */
class JsonConfigTest {
	
	@Test
	void constructorValidatesParameters() {
		assertDoesNotThrow(() -> new JsonConfig(
			true, true, "\t", true, 10, true, 1, StandardCharsets.UTF_8
		));
		
		assertThrows(NullPointerException.class, () -> new JsonConfig(
			true, true, null, true, 10, true, 1, StandardCharsets.UTF_8
		));
		
		assertThrows(NullPointerException.class, () -> new JsonConfig(
			true, true, "\t", true, 10, true, 1, null
		));
		
		assertThrows(IllegalArgumentException.class, () -> new JsonConfig(
			true, true, "\t", true, 0, true, 1, StandardCharsets.UTF_8
		));
		
		assertThrows(IllegalArgumentException.class, () -> new JsonConfig(
			true, true, "\t", true, -1, true, 1, StandardCharsets.UTF_8
		));
		
		assertThrows(IllegalArgumentException.class, () -> new JsonConfig(
			true, true, "\t", true, 10, true, 0, StandardCharsets.UTF_8
		));
		
		assertThrows(IllegalArgumentException.class, () -> new JsonConfig(
			true, true, "\t", true, 10, true, -1, StandardCharsets.UTF_8
		));
		
		assertDoesNotThrow(() -> new JsonConfig(
			true, true, "\t", false, 0, false, 0, StandardCharsets.UTF_8
		));
		
		assertDoesNotThrow(() -> new JsonConfig(
			true, true, "\t", false, -1, false, -1, StandardCharsets.UTF_8
		));
	}
	
	@Test
	void defaultConfigurationValues() {
		JsonConfig config = JsonConfig.DEFAULT;
		
		assertTrue(config.strict());
		assertTrue(config.prettyPrint());
		assertEquals("\t", config.indent());
		assertTrue(config.simplifyArrays());
		assertEquals(10, config.maxArraySimplificationSize());
		assertTrue(config.simplifyObjects());
		assertEquals(1, config.maxObjectSimplificationSize());
		assertEquals(StandardCharsets.UTF_8, config.charset());
	}
	
	@Test
	void customConfigurationValues() {
		JsonConfig config = new JsonConfig(
			false, false, "    ", false, 5, false, 3, StandardCharsets.UTF_16
		);
		
		assertFalse(config.strict());
		assertFalse(config.prettyPrint());
		assertEquals("    ", config.indent());
		assertFalse(config.simplifyArrays());
		assertEquals(5, config.maxArraySimplificationSize());
		assertFalse(config.simplifyObjects());
		assertEquals(3, config.maxObjectSimplificationSize());
		assertEquals(StandardCharsets.UTF_16, config.charset());
	}
	
	@Test
	void allParameterCombinations() {
		for (boolean strict : new boolean[] { true, false }) {
			for (boolean prettyPrint : new boolean[] { true, false }) {
				for (boolean simplifyArrays : new boolean[] { true, false }) {
					for (boolean simplifyObjects : new boolean[] { true, false }) {
						assertDoesNotThrow(() -> new JsonConfig(
							strict, prettyPrint, "  ", simplifyArrays, 5, simplifyObjects, 2, StandardCharsets.UTF_8
						));
					}
				}
			}
		}
	}
	
	@Test
	void differentIndentStrings() {
		String[] indents = { "\t", "  ", "    ", "        ", "", "xyz" };
		
		for (String indent : indents) {
			JsonConfig config = new JsonConfig(
				true, true, indent, true, 10, true, 1, StandardCharsets.UTF_8
			);
			assertEquals(indent, config.indent());
		}
	}
	
	@Test
	void differentCharsets() {
		var charsets = new java.nio.charset.Charset[] {
			StandardCharsets.UTF_8,
			StandardCharsets.UTF_16,
			StandardCharsets.UTF_16BE,
			StandardCharsets.UTF_16LE,
			StandardCharsets.ISO_8859_1,
			StandardCharsets.US_ASCII
		};
		
		for (var charset : charsets) {
			JsonConfig config = new JsonConfig(
				true, true, "\t", true, 10, true, 1, charset
			);
			assertEquals(charset, config.charset());
		}
	}
	
	@Test
	void boundarySizeValues() {
		assertDoesNotThrow(() -> new JsonConfig(
			true, true, "\t", true, 1, true, 1, StandardCharsets.UTF_8
		));
		
		assertDoesNotThrow(() -> new JsonConfig(
			true, true, "\t", true, 1000, true, 1000, StandardCharsets.UTF_8
		));
		
		assertDoesNotThrow(() -> new JsonConfig(
			true, true, "\t", true, Integer.MAX_VALUE, true, Integer.MAX_VALUE, StandardCharsets.UTF_8
		));
	}
	
	@Test
	void recordEquality() {
		JsonConfig config1 = new JsonConfig(
			true, true, "\t", true, 10, true, 1, StandardCharsets.UTF_8
		);
		
		JsonConfig config2 = new JsonConfig(
			true, true, "\t", true, 10, true, 1, StandardCharsets.UTF_8
		);
		
		JsonConfig config3 = new JsonConfig(
			false, true, "\t", true, 10, true, 1, StandardCharsets.UTF_8
		);
		
		assertEquals(config1, config2);
		assertEquals(config1.hashCode(), config2.hashCode());
		assertNotEquals(config1, config3);
		
		assertEquals(JsonConfig.DEFAULT, new JsonConfig(
			true, true, "\t", true, 10, true, 1, StandardCharsets.UTF_8
		));
	}
	
	@Test
	void recordToString() {
		JsonConfig config = new JsonConfig(
			true, false, "  ", false, 5, true, 2, StandardCharsets.UTF_16
		);
		
		String toString = config.toString();
		assertTrue(toString.contains("strict=true"));
		assertTrue(toString.contains("prettyPrint=false"));
		assertTrue(toString.contains("simplifyArrays=false"));
		assertTrue(toString.contains("maxArraySimplificationSize=5"));
		assertTrue(toString.contains("simplifyObjects=true"));
		assertTrue(toString.contains("maxObjectSimplificationSize=2"));
		assertTrue(toString.contains("UTF-16"));
	}
}
