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

import net.luis.utils.io.data.yaml.exception.YamlTypeException;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link YamlAlias}.<br>
 *
 * @author Luis-St
 */
class YamlAliasTest {

	private static final YamlConfig DEFAULT_CONFIG = new YamlConfig(true, true, "  ", true, false, YamlConfig.NullStyle.NULL, true, false, StandardCharsets.UTF_8);

	@Test
	void constructorValid() {
		YamlAlias alias = new YamlAlias("myAlias");
		assertEquals("myAlias", alias.anchorName());
	}

	@Test
	void constructorWithValidNames() {
		assertDoesNotThrow(() -> new YamlAlias("simple"));
		assertDoesNotThrow(() -> new YamlAlias("with_underscore"));
		assertDoesNotThrow(() -> new YamlAlias("with-hyphen"));
		assertDoesNotThrow(() -> new YamlAlias("with123numbers"));
		assertDoesNotThrow(() -> new YamlAlias("MixedCase"));
		assertDoesNotThrow(() -> new YamlAlias("a"));
	}

	@Test
	void constructorWithNullName() {
		assertThrows(NullPointerException.class, () -> new YamlAlias(null));
	}

	@Test
	void constructorWithBlankName() {
		assertThrows(IllegalArgumentException.class, () -> new YamlAlias(""));
		assertThrows(IllegalArgumentException.class, () -> new YamlAlias("   "));
	}

	@Test
	void constructorWithInvalidNames() {
		assertThrows(IllegalArgumentException.class, () -> new YamlAlias("with space"));
		assertThrows(IllegalArgumentException.class, () -> new YamlAlias("with.dot"));
		assertThrows(IllegalArgumentException.class, () -> new YamlAlias("with:colon"));
		assertThrows(IllegalArgumentException.class, () -> new YamlAlias("with@symbol"));
		assertThrows(IllegalArgumentException.class, () -> new YamlAlias("with#hash"));
	}

	@Test
	void getAnchorName() {
		YamlAlias alias = new YamlAlias("testAlias");
		assertEquals("testAlias", alias.anchorName());
	}

	@Test
	void yamlElementTypeChecks() {
		YamlAlias alias = new YamlAlias("alias");

		assertFalse(alias.isYamlNull());
		assertFalse(alias.isYamlMapping());
		assertFalse(alias.isYamlSequence());
		assertFalse(alias.isYamlScalar());
		assertFalse(alias.isYamlAnchor());
		assertTrue(alias.isYamlAlias());
	}

	@Test
	void yamlElementConversions() {
		YamlAlias alias = new YamlAlias("alias");

		assertThrows(YamlTypeException.class, alias::getAsYamlMapping);
		assertThrows(YamlTypeException.class, alias::getAsYamlSequence);
		assertThrows(YamlTypeException.class, alias::getAsYamlScalar);
		assertThrows(YamlTypeException.class, alias::getAsYamlAnchor);
		assertSame(alias, alias.getAsYamlAlias());
	}

	@Test
	void unwrap() {
		YamlAlias alias = new YamlAlias("alias");
		// Alias doesn't unwrap to anything else
		assertSame(alias, alias.unwrap());
	}

	@Test
	void equalsAndHashCode() {
		YamlAlias alias1 = new YamlAlias("alias");
		YamlAlias alias2 = new YamlAlias("alias");
		YamlAlias alias3 = new YamlAlias("differentAlias");

		assertEquals(alias1, alias2);
		assertEquals(alias1.hashCode(), alias2.hashCode());

		assertNotEquals(alias1, alias3);

		assertEquals(alias1, alias1);
		assertNotEquals(alias1, null);
		assertNotEquals(alias1, "not an alias");
	}

	@Test
	void toStringWithDefaultConfig() {
		YamlAlias alias = new YamlAlias("myAlias");
		assertEquals("*myAlias", alias.toString());
	}

	@Test
	void toStringWithCustomConfig() {
		YamlAlias alias = new YamlAlias("testAlias");
		assertEquals("*testAlias", alias.toString(DEFAULT_CONFIG));
	}

	@Test
	void toStringFormat() {
		// Verify the * prefix is always present
		YamlAlias alias = new YamlAlias("anchorName");
		String result = alias.toString();

		assertTrue(result.startsWith("*"));
		assertEquals("*anchorName", result);
	}

	@Test
	void aliasWithDifferentNameFormats() {
		assertEquals("*simple", new YamlAlias("simple").toString());
		assertEquals("*with_underscore", new YamlAlias("with_underscore").toString());
		assertEquals("*with-hyphen", new YamlAlias("with-hyphen").toString());
		assertEquals("*with123", new YamlAlias("with123").toString());
		assertEquals("*CamelCase", new YamlAlias("CamelCase").toString());
	}

	@Test
	void aliasInteractionWithSequence() {
		YamlSequence sequence = new YamlSequence();
		YamlAlias alias = new YamlAlias("myAlias");
		sequence.add(alias);

		assertEquals(alias, sequence.get(0));
		assertTrue(sequence.contains(alias));
	}

	@Test
	void aliasInteractionWithMapping() {
		YamlMapping mapping = new YamlMapping();
		YamlAlias alias = new YamlAlias("myAlias");
		mapping.add("ref", alias);

		assertEquals(alias, mapping.get("ref"));
		assertTrue(mapping.containsValue(alias));
	}
}
