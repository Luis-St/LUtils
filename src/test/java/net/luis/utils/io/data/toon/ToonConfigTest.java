/*
 * LUtils
 * Copyright (C) 2026 Luis Staudt
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

package net.luis.utils.io.data.toon;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link ToonConfig}.<br>
 *
 * @author Luis-St
 */
class ToonConfigTest {
	
	@Test
	void defaultValues() {
		ToonConfig config = ToonConfig.DEFAULT;
		
		assertTrue(config.strict());
		assertEquals(2, config.indent());
		assertEquals(ToonConfig.Delimiter.COMMA, config.delimiter());
		assertEquals(ToonConfig.KeyFolding.OFF, config.keyFolding());
		assertEquals(Integer.MAX_VALUE, config.flattenDepth());
		assertEquals(ToonConfig.PathExpansion.OFF, config.expandPaths());
		assertEquals(StandardCharsets.UTF_8, config.charset());
	}
	
	@Test
	void compactValues() {
		ToonConfig config = ToonConfig.COMPACT;
		
		assertTrue(config.strict());
		assertEquals(0, config.indent());
		assertEquals(ToonConfig.Delimiter.COMMA, config.delimiter());
		assertEquals(ToonConfig.KeyFolding.OFF, config.keyFolding());
		assertEquals(Integer.MAX_VALUE, config.flattenDepth());
		assertEquals(ToonConfig.PathExpansion.OFF, config.expandPaths());
		assertEquals(StandardCharsets.UTF_8, config.charset());
	}
	
	@Test
	void constructorWithNullDelimiter() {
		assertThrows(NullPointerException.class, () -> new ToonConfig(
			true, 2, null, ToonConfig.KeyFolding.OFF,
			Integer.MAX_VALUE, ToonConfig.PathExpansion.OFF, StandardCharsets.UTF_8
		));
	}
	
	@Test
	void constructorWithNullKeyFolding() {
		assertThrows(NullPointerException.class, () -> new ToonConfig(
			true, 2, ToonConfig.Delimiter.COMMA, null,
			Integer.MAX_VALUE, ToonConfig.PathExpansion.OFF, StandardCharsets.UTF_8
		));
	}
	
	@Test
	void constructorWithNullExpandPaths() {
		assertThrows(NullPointerException.class, () -> new ToonConfig(
			true, 2, ToonConfig.Delimiter.COMMA, ToonConfig.KeyFolding.OFF,
			Integer.MAX_VALUE, null, StandardCharsets.UTF_8
		));
	}
	
	@Test
	void constructorWithNullCharset() {
		assertThrows(NullPointerException.class, () -> new ToonConfig(
			true, 2, ToonConfig.Delimiter.COMMA, ToonConfig.KeyFolding.OFF,
			Integer.MAX_VALUE, ToonConfig.PathExpansion.OFF, null
		));
	}
	
	@Test
	void constructorWithNegativeIndent() {
		assertThrows(IllegalArgumentException.class, () -> new ToonConfig(
			true, -1, ToonConfig.Delimiter.COMMA, ToonConfig.KeyFolding.OFF,
			Integer.MAX_VALUE, ToonConfig.PathExpansion.OFF, StandardCharsets.UTF_8
		));
	}
	
	@Test
	void delimiterValues() {
		assertEquals(',', ToonConfig.Delimiter.COMMA.getChar());
		assertEquals('\t', ToonConfig.Delimiter.TAB.getChar());
		assertEquals('|', ToonConfig.Delimiter.PIPE.getChar());
	}
	
	@Test
	void keyFoldingValues() {
		assertNotNull(ToonConfig.KeyFolding.OFF);
		assertNotNull(ToonConfig.KeyFolding.SAFE);
		assertEquals(ToonConfig.KeyFolding.OFF, ToonConfig.KeyFolding.valueOf("OFF"));
		assertEquals(ToonConfig.KeyFolding.SAFE, ToonConfig.KeyFolding.valueOf("SAFE"));
	}
	
	@Test
	void pathExpansionValues() {
		assertNotNull(ToonConfig.PathExpansion.OFF);
		assertNotNull(ToonConfig.PathExpansion.SAFE);
		assertEquals(ToonConfig.PathExpansion.OFF, ToonConfig.PathExpansion.valueOf("OFF"));
		assertEquals(ToonConfig.PathExpansion.SAFE, ToonConfig.PathExpansion.valueOf("SAFE"));
	}
}
