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

package net.luis.utils.io.data.yaml;

import net.luis.utils.io.data.yaml.exception.YamlTypeException;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link YamlAnchor}.<br>
 *
 * @author Luis-St
 */
class YamlAnchorTest {
	
	private static final YamlConfig BLOCK_CONFIG = new YamlConfig(true, true, "  ", true, false, YamlConfig.NullStyle.NULL, true, false, StandardCharsets.UTF_8);
	private static final YamlConfig FLOW_CONFIG = new YamlConfig(true, true, "  ", false, false, YamlConfig.NullStyle.NULL, true, false, StandardCharsets.UTF_8);
	
	@Test
	void constructorValid() {
		YamlScalar element = new YamlScalar("value");
		YamlAnchor anchor = new YamlAnchor("myAnchor", element);
		
		assertEquals("myAnchor", anchor.getName());
		assertEquals(element, anchor.getElement());
	}
	
	@Test
	void constructorWithValidNames() {
		assertDoesNotThrow(() -> new YamlAnchor("simple", new YamlScalar("value")));
		assertDoesNotThrow(() -> new YamlAnchor("with_underscore", new YamlScalar("value")));
		assertDoesNotThrow(() -> new YamlAnchor("with-hyphen", new YamlScalar("value")));
		assertDoesNotThrow(() -> new YamlAnchor("with123numbers", new YamlScalar("value")));
		assertDoesNotThrow(() -> new YamlAnchor("MixedCase", new YamlScalar("value")));
		assertDoesNotThrow(() -> new YamlAnchor("a", new YamlScalar("value")));
	}
	
	@Test
	void constructorWithNullGetName() {
		assertThrows(NullPointerException.class, () -> new YamlAnchor(null, new YamlScalar("value")));
	}
	
	@Test
	void constructorWithNullGetElement() {
		assertThrows(NullPointerException.class, () -> new YamlAnchor("anchor", null));
	}
	
	@Test
	void constructorWithBlankGetName() {
		assertThrows(IllegalArgumentException.class, () -> new YamlAnchor("", new YamlScalar("value")));
		assertThrows(IllegalArgumentException.class, () -> new YamlAnchor("   ", new YamlScalar("value")));
	}
	
	@Test
	void constructorWithInvalidNames() {
		assertThrows(IllegalArgumentException.class, () -> new YamlAnchor("with space", new YamlScalar("value")));
		assertThrows(IllegalArgumentException.class, () -> new YamlAnchor("with.dot", new YamlScalar("value")));
		assertThrows(IllegalArgumentException.class, () -> new YamlAnchor("with:colon", new YamlScalar("value")));
		assertThrows(IllegalArgumentException.class, () -> new YamlAnchor("with@symbol", new YamlScalar("value")));
		assertThrows(IllegalArgumentException.class, () -> new YamlAnchor("with#hash", new YamlScalar("value")));
	}
	
	@Test
	void constructorWithAlias() {
		YamlAlias alias = new YamlAlias("someAlias");
		assertThrows(IllegalArgumentException.class, () -> new YamlAnchor("anchor", alias));
	}
	
	@Test
	void getGetName() {
		YamlAnchor anchor = new YamlAnchor("testName", new YamlScalar("value"));
		assertEquals("testName", anchor.getName());
	}
	
	@Test
	void getGetElement() {
		YamlScalar scalar = new YamlScalar("value");
		YamlMapping mapping = new YamlMapping();
		YamlSequence sequence = new YamlSequence();
		
		assertEquals(scalar, new YamlAnchor("a", scalar).getElement());
		assertEquals(mapping, new YamlAnchor("b", mapping).getElement());
		assertEquals(sequence, new YamlAnchor("c", sequence).getElement());
	}
	
	@Test
	void unwrapSimple() {
		YamlScalar scalar = new YamlScalar("value");
		YamlAnchor anchor = new YamlAnchor("anchor", scalar);
		
		assertEquals(scalar, anchor.unwrap());
	}
	
	@Test
	void unwrapNested() {
		YamlScalar innerScalar = new YamlScalar("innerValue");
		YamlAnchor innerAnchor = new YamlAnchor("inner", innerScalar);
		YamlAnchor outerAnchor = new YamlAnchor("outer", innerAnchor);
		
		assertEquals(innerScalar, outerAnchor.unwrap());
	}
	
	@Test
	void unwrapDeeplyNested() {
		YamlScalar scalar = new YamlScalar("deep");
		YamlAnchor level1 = new YamlAnchor("l1", scalar);
		YamlAnchor level2 = new YamlAnchor("l2", level1);
		YamlAnchor level3 = new YamlAnchor("l3", level2);
		
		assertEquals(scalar, level3.unwrap());
	}
	
	@Test
	void yamlGetElementTypeChecks() {
		YamlAnchor anchor = new YamlAnchor("anchor", new YamlScalar("value"));
		
		assertFalse(anchor.isYamlNull());
		assertFalse(anchor.isYamlMapping());
		assertFalse(anchor.isYamlSequence());
		assertFalse(anchor.isYamlScalar());
		assertTrue(anchor.isYamlAnchor());
		assertFalse(anchor.isYamlAlias());
	}
	
	@Test
	void yamlGetElementConversions() {
		YamlAnchor anchor = new YamlAnchor("anchor", new YamlScalar("value"));
		
		assertSame(anchor, anchor.getAsYamlAnchor());
		assertThrows(YamlTypeException.class, anchor::getAsYamlAlias);
	}
	
	@Test
	void getAsYamlMappingFromAnchor() {
		YamlMapping mapping = new YamlMapping();
		YamlAnchor anchor = new YamlAnchor("anchor", mapping);
		
		assertEquals(mapping, anchor.getAsYamlMapping());
	}
	
	@Test
	void getAsYamlSequenceFromAnchor() {
		YamlSequence sequence = new YamlSequence();
		YamlAnchor anchor = new YamlAnchor("anchor", sequence);
		
		assertEquals(sequence, anchor.getAsYamlSequence());
	}
	
	@Test
	void getAsYamlScalarFromAnchor() {
		YamlScalar scalar = new YamlScalar("value");
		YamlAnchor anchor = new YamlAnchor("anchor", scalar);
		
		assertEquals(scalar, anchor.getAsYamlScalar());
	}
	
	@Test
	void equalsAndHashCode() {
		YamlScalar scalar = new YamlScalar("value");
		YamlAnchor anchor1 = new YamlAnchor("anchor", scalar);
		YamlAnchor anchor2 = new YamlAnchor("anchor", new YamlScalar("value"));
		YamlAnchor anchor3 = new YamlAnchor("differentName", scalar);
		YamlAnchor anchor4 = new YamlAnchor("anchor", new YamlScalar("differentValue"));
		
		assertEquals(anchor1, anchor2);
		assertEquals(anchor1.hashCode(), anchor2.hashCode());
		
		assertNotEquals(anchor1, anchor3);
		assertNotEquals(anchor1, anchor4);
		
		assertEquals(anchor1, anchor1);
		assertNotEquals(anchor1, null);
		assertNotEquals(anchor1, "not an anchor");
	}
	
	@Test
	void toStringWithScalar() {
		YamlAnchor anchor = new YamlAnchor("myAnchor", new YamlScalar("value"));
		
		assertEquals("&myAnchor value", anchor.toString());
		assertEquals("&myAnchor value", anchor.toString(FLOW_CONFIG));
		assertEquals("&myAnchor value", anchor.toString(BLOCK_CONFIG));
	}
	
	@Test
	void toStringWithMapping() {
		YamlMapping mapping = new YamlMapping();
		mapping.add("key", "value");
		YamlAnchor anchor = new YamlAnchor("mapAnchor", mapping);
		
		String flowResult = anchor.toString(FLOW_CONFIG);
		assertEquals("&mapAnchor {key: value}", flowResult);
		
		String blockResult = anchor.toString(BLOCK_CONFIG);
		assertTrue(blockResult.startsWith("&mapAnchor"));
		assertTrue(blockResult.contains("key: value"));
	}
	
	@Test
	void toStringWithSequence() {
		YamlSequence sequence = new YamlSequence();
		sequence.add("a");
		sequence.add("b");
		YamlAnchor anchor = new YamlAnchor("seqAnchor", sequence);
		
		String flowResult = anchor.toString(FLOW_CONFIG);
		assertEquals("&seqAnchor [a, b]", flowResult);
		
		String blockResult = anchor.toString(BLOCK_CONFIG);
		assertTrue(blockResult.startsWith("&seqAnchor"));
	}
	
	@Test
	void toStringWithNullConfig() {
		YamlAnchor anchor = new YamlAnchor("anchor", new YamlScalar("value"));
		assertThrows(NullPointerException.class, () -> anchor.toString(null));
	}
	
	@Test
	void toStringWithEmptyMapping() {
		YamlAnchor anchor = new YamlAnchor("anchor", new YamlMapping());
		assertTrue(anchor.toString().contains("&anchor"));
		assertTrue(anchor.toString().contains("{}"));
	}
	
	@Test
	void toStringWithEmptySequence() {
		YamlAnchor anchor = new YamlAnchor("anchor", new YamlSequence());
		assertTrue(anchor.toString().contains("&anchor"));
		assertTrue(anchor.toString().contains("[]"));
	}
	
	@Test
	void anchorWithDifferentGetElementTypes() {
		YamlAnchor scalarAnchor = new YamlAnchor("scalar", new YamlScalar(42));
		YamlAnchor boolAnchor = new YamlAnchor("bool", new YamlScalar(true));
		YamlAnchor nullAnchor = new YamlAnchor("null", YamlNull.INSTANCE);
		
		assertEquals(new YamlScalar(42), scalarAnchor.getElement());
		assertEquals(new YamlScalar(true), boolAnchor.getElement());
		assertEquals(YamlNull.INSTANCE, nullAnchor.getElement());
	}
}
