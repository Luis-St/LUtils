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

package net.luis.utils.io.data.yaml;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link YamlConfig}.<br>
 *
 * @author Luis-St
 */
class YamlConfigTest {
	
	@Test
	void defaultConfiguration() {
		YamlConfig config = YamlConfig.DEFAULT;
		
		assertTrue(config.strict());
		assertTrue(config.prettyPrint());
		assertEquals("  ", config.indent());
		assertTrue(config.useBlockStyle());
		assertFalse(config.useDocumentMarkers());
		assertEquals(YamlConfig.NullStyle.NULL, config.nullStyle());
		assertTrue(config.resolveAnchors());
		assertFalse(config.allowDuplicateKeys());
		assertEquals(StandardCharsets.UTF_8, config.charset());
	}
	
	@Test
	void preserveAnchorsConfiguration() {
		YamlConfig config = YamlConfig.PRESERVE_ANCHORS;
		
		assertTrue(config.strict());
		assertTrue(config.prettyPrint());
		assertEquals("  ", config.indent());
		assertTrue(config.useBlockStyle());
		assertFalse(config.useDocumentMarkers());
		assertEquals(YamlConfig.NullStyle.NULL, config.nullStyle());
		assertFalse(config.resolveAnchors());
		assertFalse(config.allowDuplicateKeys());
		assertEquals(StandardCharsets.UTF_8, config.charset());
	}
	
	@Test
	void constructorWithValidParameters() {
		YamlConfig config = new YamlConfig(
			false, false, "\t", false, true,
			YamlConfig.NullStyle.TILDE, false, true, StandardCharsets.UTF_16
		);
		
		assertFalse(config.strict());
		assertFalse(config.prettyPrint());
		assertEquals("\t", config.indent());
		assertFalse(config.useBlockStyle());
		assertTrue(config.useDocumentMarkers());
		assertEquals(YamlConfig.NullStyle.TILDE, config.nullStyle());
		assertFalse(config.resolveAnchors());
		assertTrue(config.allowDuplicateKeys());
		assertEquals(StandardCharsets.UTF_16, config.charset());
	}
	
	@Test
	void constructorWithNullIndent() {
		assertThrows(NullPointerException.class, () -> new YamlConfig(
			true, true, null, true, false,
			YamlConfig.NullStyle.NULL, true, false, StandardCharsets.UTF_8
		));
	}
	
	@Test
	void constructorWithNullNullStyle() {
		assertThrows(NullPointerException.class, () -> new YamlConfig(
			true, true, "  ", true, false,
			null, true, false, StandardCharsets.UTF_8
		));
	}
	
	@Test
	void constructorWithNullCharset() {
		assertThrows(NullPointerException.class, () -> new YamlConfig(
			true, true, "  ", true, false,
			YamlConfig.NullStyle.NULL, true, false, null
		));
	}
	
	@Test
	void differentIndentStrings() {
		YamlConfig twoSpaces = new YamlConfig(true, true, "  ", true, false, YamlConfig.NullStyle.NULL, true, false, StandardCharsets.UTF_8);
		YamlConfig fourSpaces = new YamlConfig(true, true, "    ", true, false, YamlConfig.NullStyle.NULL, true, false, StandardCharsets.UTF_8);
		YamlConfig tab = new YamlConfig(true, true, "\t", true, false, YamlConfig.NullStyle.NULL, true, false, StandardCharsets.UTF_8);
		YamlConfig empty = new YamlConfig(true, true, "", true, false, YamlConfig.NullStyle.NULL, true, false, StandardCharsets.UTF_8);
		
		assertEquals("  ", twoSpaces.indent());
		assertEquals("    ", fourSpaces.indent());
		assertEquals("\t", tab.indent());
		assertEquals("", empty.indent());
	}
	
	@Test
	void differentCharsets() {
		YamlConfig utf8 = new YamlConfig(true, true, "  ", true, false, YamlConfig.NullStyle.NULL, true, false, StandardCharsets.UTF_8);
		YamlConfig utf16 = new YamlConfig(true, true, "  ", true, false, YamlConfig.NullStyle.NULL, true, false, StandardCharsets.UTF_16);
		YamlConfig iso = new YamlConfig(true, true, "  ", true, false, YamlConfig.NullStyle.NULL, true, false, StandardCharsets.ISO_8859_1);
		YamlConfig ascii = new YamlConfig(true, true, "  ", true, false, YamlConfig.NullStyle.NULL, true, false, StandardCharsets.US_ASCII);
		
		assertEquals(StandardCharsets.UTF_8, utf8.charset());
		assertEquals(StandardCharsets.UTF_16, utf16.charset());
		assertEquals(StandardCharsets.ISO_8859_1, iso.charset());
		assertEquals(StandardCharsets.US_ASCII, ascii.charset());
	}
	
	@Test
	void nullStyleEnumValues() {
		YamlConfig.NullStyle[] values = YamlConfig.NullStyle.values();
		assertEquals(3, values.length);
		assertEquals(YamlConfig.NullStyle.NULL, YamlConfig.NullStyle.valueOf("NULL"));
		assertEquals(YamlConfig.NullStyle.TILDE, YamlConfig.NullStyle.valueOf("TILDE"));
		assertEquals(YamlConfig.NullStyle.EMPTY, YamlConfig.NullStyle.valueOf("EMPTY"));
	}
	
	@Test
	void nullStyleConfigurations() {
		YamlConfig nullConfig = new YamlConfig(true, true, "  ", true, false, YamlConfig.NullStyle.NULL, true, false, StandardCharsets.UTF_8);
		YamlConfig tildeConfig = new YamlConfig(true, true, "  ", true, false, YamlConfig.NullStyle.TILDE, true, false, StandardCharsets.UTF_8);
		YamlConfig emptyConfig = new YamlConfig(true, true, "  ", true, false, YamlConfig.NullStyle.EMPTY, true, false, StandardCharsets.UTF_8);
		
		assertEquals(YamlConfig.NullStyle.NULL, nullConfig.nullStyle());
		assertEquals(YamlConfig.NullStyle.TILDE, tildeConfig.nullStyle());
		assertEquals(YamlConfig.NullStyle.EMPTY, emptyConfig.nullStyle());
	}
	
	@Test
	void strictModeConfigurations() {
		YamlConfig strict = new YamlConfig(true, true, "  ", true, false, YamlConfig.NullStyle.NULL, true, false, StandardCharsets.UTF_8);
		YamlConfig nonStrict = new YamlConfig(false, true, "  ", true, false, YamlConfig.NullStyle.NULL, true, false, StandardCharsets.UTF_8);
		
		assertTrue(strict.strict());
		assertFalse(nonStrict.strict());
	}
	
	@Test
	void blockStyleConfigurations() {
		YamlConfig block = new YamlConfig(true, true, "  ", true, false, YamlConfig.NullStyle.NULL, true, false, StandardCharsets.UTF_8);
		YamlConfig flow = new YamlConfig(true, true, "  ", false, false, YamlConfig.NullStyle.NULL, true, false, StandardCharsets.UTF_8);
		
		assertTrue(block.useBlockStyle());
		assertFalse(flow.useBlockStyle());
	}
	
	@Test
	void documentMarkersConfigurations() {
		YamlConfig withMarkers = new YamlConfig(true, true, "  ", true, true, YamlConfig.NullStyle.NULL, true, false, StandardCharsets.UTF_8);
		YamlConfig withoutMarkers = new YamlConfig(true, true, "  ", true, false, YamlConfig.NullStyle.NULL, true, false, StandardCharsets.UTF_8);
		
		assertTrue(withMarkers.useDocumentMarkers());
		assertFalse(withoutMarkers.useDocumentMarkers());
	}
	
	@Test
	void resolveAnchorsConfigurations() {
		YamlConfig resolve = new YamlConfig(true, true, "  ", true, false, YamlConfig.NullStyle.NULL, true, false, StandardCharsets.UTF_8);
		YamlConfig preserve = new YamlConfig(true, true, "  ", true, false, YamlConfig.NullStyle.NULL, false, false, StandardCharsets.UTF_8);
		
		assertTrue(resolve.resolveAnchors());
		assertFalse(preserve.resolveAnchors());
	}
	
	@Test
	void allowDuplicateKeysConfigurations() {
		YamlConfig noDuplicates = new YamlConfig(true, true, "  ", true, false, YamlConfig.NullStyle.NULL, true, false, StandardCharsets.UTF_8);
		YamlConfig allowDuplicates = new YamlConfig(true, true, "  ", true, false, YamlConfig.NullStyle.NULL, true, true, StandardCharsets.UTF_8);
		
		assertFalse(noDuplicates.allowDuplicateKeys());
		assertTrue(allowDuplicates.allowDuplicateKeys());
	}
	
	@Test
	void equalsAndHashCode() {
		YamlConfig config1 = new YamlConfig(true, true, "  ", true, false, YamlConfig.NullStyle.NULL, true, false, StandardCharsets.UTF_8);
		YamlConfig config2 = new YamlConfig(true, true, "  ", true, false, YamlConfig.NullStyle.NULL, true, false, StandardCharsets.UTF_8);
		YamlConfig config3 = new YamlConfig(false, true, "  ", true, false, YamlConfig.NullStyle.NULL, true, false, StandardCharsets.UTF_8);
		
		assertEquals(config1, config2);
		assertEquals(config1.hashCode(), config2.hashCode());
		assertNotEquals(config1, config3);
		
		assertEquals(config1, config1);
		assertNotEquals(config1, null);
		assertNotEquals(config1, "not a config");
	}
	
	@Test
	void toStringContainsRelevantInfo() {
		YamlConfig config = YamlConfig.DEFAULT;
		String str = config.toString();
		
		assertTrue(str.contains("strict"));
		assertTrue(str.contains("prettyPrint"));
		assertTrue(str.contains("indent"));
		assertTrue(str.contains("useBlockStyle"));
		assertTrue(str.contains("useDocumentMarkers"));
		assertTrue(str.contains("nullStyle"));
		assertTrue(str.contains("resolveAnchors"));
		assertTrue(str.contains("allowDuplicateKeys"));
		assertTrue(str.contains("charset"));
	}
	
	@Test
	void recordCopyFunctionality() {
		YamlConfig original = YamlConfig.DEFAULT;
		
		YamlConfig modified = new YamlConfig(
			false,
			original.prettyPrint(),
			original.indent(),
			original.useBlockStyle(),
			original.useDocumentMarkers(),
			original.nullStyle(),
			original.resolveAnchors(),
			original.allowDuplicateKeys(),
			original.charset()
		);
		
		assertTrue(original.strict());
		assertFalse(modified.strict());
		assertEquals(original.prettyPrint(), modified.prettyPrint());
	}
}
