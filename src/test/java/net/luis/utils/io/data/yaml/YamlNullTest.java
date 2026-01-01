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
 * Test class for {@link YamlNull}.<br>
 *
 * @author Luis-St
 */
class YamlNullTest {
	
	@Test
	void singletonInstance() {
		assertSame(YamlNull.INSTANCE, YamlNull.INSTANCE);
		
		YamlNull null1 = YamlNull.INSTANCE;
		YamlNull null2 = YamlNull.INSTANCE;
		assertSame(null1, null2);
	}
	
	@Test
	void yamlElementTypeChecks() {
		YamlNull yamlNull = YamlNull.INSTANCE;
		
		assertTrue(yamlNull.isYamlNull());
		assertFalse(yamlNull.isYamlMapping());
		assertFalse(yamlNull.isYamlSequence());
		assertFalse(yamlNull.isYamlScalar());
		assertFalse(yamlNull.isYamlAnchor());
		assertFalse(yamlNull.isYamlAlias());
	}
	
	@Test
	void yamlElementConversions() {
		YamlNull yamlNull = YamlNull.INSTANCE;
		
		assertThrows(YamlTypeException.class, yamlNull::getAsYamlMapping);
		assertThrows(YamlTypeException.class, yamlNull::getAsYamlSequence);
		assertThrows(YamlTypeException.class, yamlNull::getAsYamlScalar);
		assertThrows(YamlTypeException.class, yamlNull::getAsYamlAnchor);
		assertThrows(YamlTypeException.class, yamlNull::getAsYamlAlias);
	}
	
	@Test
	void yamlElementConversionExceptionMessages() {
		YamlNull yamlNull = YamlNull.INSTANCE;
		
		YamlTypeException mappingException = assertThrows(YamlTypeException.class, yamlNull::getAsYamlMapping);
		assertTrue(mappingException.getMessage().contains("yaml mapping"));
		assertTrue(mappingException.getMessage().contains("yaml null"));
		
		YamlTypeException sequenceException = assertThrows(YamlTypeException.class, yamlNull::getAsYamlSequence);
		assertTrue(sequenceException.getMessage().contains("yaml sequence"));
		assertTrue(sequenceException.getMessage().contains("yaml null"));
		
		YamlTypeException scalarException = assertThrows(YamlTypeException.class, yamlNull::getAsYamlScalar);
		assertTrue(scalarException.getMessage().contains("yaml scalar"));
		assertTrue(scalarException.getMessage().contains("yaml null"));
		
		YamlTypeException anchorException = assertThrows(YamlTypeException.class, yamlNull::getAsYamlAnchor);
		assertTrue(anchorException.getMessage().contains("yaml anchor"));
		assertTrue(anchorException.getMessage().contains("yaml null"));
		
		YamlTypeException aliasException = assertThrows(YamlTypeException.class, yamlNull::getAsYamlAlias);
		assertTrue(aliasException.getMessage().contains("yaml alias"));
		assertTrue(aliasException.getMessage().contains("yaml null"));
	}
	
	@Test
	void unwrap() {
		YamlNull yamlNull = YamlNull.INSTANCE;
		assertSame(yamlNull, yamlNull.unwrap());
	}
	
	@Test
	void toStringWithDefaultConfig() {
		assertEquals("null", YamlNull.INSTANCE.toString());
	}
	
	@Test
	void toStringWithNullStyleNull() {
		YamlConfig config = new YamlConfig(true, true, "  ", true, false, YamlConfig.NullStyle.NULL, true, false, StandardCharsets.UTF_8);
		assertEquals("null", YamlNull.INSTANCE.toString(config));
	}
	
	@Test
	void toStringWithNullStyleTilde() {
		YamlConfig config = new YamlConfig(true, true, "  ", true, false, YamlConfig.NullStyle.TILDE, true, false, StandardCharsets.UTF_8);
		assertEquals("~", YamlNull.INSTANCE.toString(config));
	}
	
	@Test
	void toStringWithNullStyleEmpty() {
		YamlConfig config = new YamlConfig(true, true, "  ", true, false, YamlConfig.NullStyle.EMPTY, true, false, StandardCharsets.UTF_8);
		assertEquals("", YamlNull.INSTANCE.toString(config));
	}
	
	@Test
	void toStringWithNullConfig() {
		assertEquals("null", YamlNull.INSTANCE.toString(null));
	}
	
	@Test
	void toStringWithVariousConfigs() {
		YamlConfig config1 = new YamlConfig(false, false, "\t", false, true, YamlConfig.NullStyle.NULL, false, true, StandardCharsets.UTF_16);
		YamlConfig config2 = new YamlConfig(true, true, "    ", true, true, YamlConfig.NullStyle.TILDE, true, false, StandardCharsets.UTF_8);
		
		assertEquals("null", YamlNull.INSTANCE.toString(config1));
		assertEquals("~", YamlNull.INSTANCE.toString(config2));
	}
	
	@Test
	void equalsAndHashCode() {
		YamlNull null1 = YamlNull.INSTANCE;
		YamlNull null2 = YamlNull.INSTANCE;
		
		assertEquals(null1, null2);
		assertEquals(null1.hashCode(), null2.hashCode());
		
		assertEquals(null1, null1);
		
		assertNotEquals(null1, null);
		assertNotEquals(null1, "null");
		assertNotEquals(null1, new YamlScalar("null"));
		assertNotEquals(null1, new YamlMapping());
		assertNotEquals(null1, new YamlSequence());
	}
	
	@Test
	void consistentBehaviorAcrossMultipleCalls() {
		for (int i = 0; i < 100; i++) {
			assertTrue(YamlNull.INSTANCE.isYamlNull());
			assertFalse(YamlNull.INSTANCE.isYamlMapping());
			assertEquals("null", YamlNull.INSTANCE.toString());
			assertSame(YamlNull.INSTANCE, YamlNull.INSTANCE);
		}
	}
	
	@Test
	void interactionWithOtherYamlElements() {
		YamlSequence sequence = new YamlSequence();
		sequence.add(YamlNull.INSTANCE);
		
		YamlMapping mapping = new YamlMapping();
		mapping.add("nullValue", YamlNull.INSTANCE);
		
		assertTrue(sequence.contains(YamlNull.INSTANCE));
		assertEquals(YamlNull.INSTANCE, sequence.get(0));
		
		assertTrue(mapping.containsValue(YamlNull.INSTANCE));
		assertEquals(YamlNull.INSTANCE, mapping.get("nullValue"));
	}
	
	@Test
	void nullStyleEnumValues() {
		YamlConfig.NullStyle[] values = YamlConfig.NullStyle.values();
		assertEquals(3, values.length);
		assertEquals(YamlConfig.NullStyle.NULL, YamlConfig.NullStyle.valueOf("NULL"));
		assertEquals(YamlConfig.NullStyle.TILDE, YamlConfig.NullStyle.valueOf("TILDE"));
		assertEquals(YamlConfig.NullStyle.EMPTY, YamlConfig.NullStyle.valueOf("EMPTY"));
	}
}
