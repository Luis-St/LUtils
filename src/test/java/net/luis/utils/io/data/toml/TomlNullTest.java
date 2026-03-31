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

package net.luis.utils.io.data.toml;

import net.luis.utils.io.data.toml.exception.TomlTypeException;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link TomlNull}.<br>
 *
 * @author Luis-St
 */
class TomlNullTest {
	
	private static final TomlConfig CUSTOM_CONFIG = new TomlConfig(
		true, true, "\t",
		false, 3, false, 10, false, 80, false,
		TomlConfig.DateTimeStyle.RFC_3339, false, StandardCharsets.UTF_8
	);
	
	@Test
	void singletonInstance() {
		assertSame(TomlNull.INSTANCE, TomlNull.INSTANCE);
		
		TomlNull null1 = TomlNull.INSTANCE;
		TomlNull null2 = TomlNull.INSTANCE;
		assertSame(null1, null2);
	}
	
	@Test
	void tomlElementTypeChecks() {
		TomlNull tomlNull = TomlNull.INSTANCE;
		
		assertTrue(tomlNull.isTomlNull());
		assertFalse(tomlNull.isTomlValue());
		assertFalse(tomlNull.isTomlArray());
		assertFalse(tomlNull.isTomlTable());
	}
	
	@Test
	void tomlElementConversions() {
		TomlNull tomlNull = TomlNull.INSTANCE;
		
		assertThrows(TomlTypeException.class, tomlNull::getAsTomlValue);
		assertThrows(TomlTypeException.class, tomlNull::getAsTomlArray);
		assertThrows(TomlTypeException.class, tomlNull::getAsTomlTable);
	}
	
	@Test
	void tomlElementConversionExceptionMessages() {
		TomlNull tomlNull = TomlNull.INSTANCE;
		
		TomlTypeException valueException = assertThrows(TomlTypeException.class, tomlNull::getAsTomlValue);
		assertTrue(valueException.getMessage().contains("toml value"));
		assertTrue(valueException.getMessage().contains("toml null"));
		
		TomlTypeException arrayException = assertThrows(TomlTypeException.class, tomlNull::getAsTomlArray);
		assertTrue(arrayException.getMessage().contains("toml array"));
		assertTrue(arrayException.getMessage().contains("toml null"));
		
		TomlTypeException tableException = assertThrows(TomlTypeException.class, tomlNull::getAsTomlTable);
		assertTrue(tableException.getMessage().contains("toml table"));
		assertTrue(tableException.getMessage().contains("toml null"));
	}
	
	@Test
	void primitiveTypeChecks() {
		TomlNull tomlNull = TomlNull.INSTANCE;
		
		assertFalse(tomlNull.isTomlBoolean());
		assertFalse(tomlNull.isTomlNumber());
		assertFalse(tomlNull.isTomlByte());
		assertFalse(tomlNull.isTomlShort());
		assertFalse(tomlNull.isTomlInteger());
		assertFalse(tomlNull.isTomlLong());
		assertFalse(tomlNull.isTomlFloat());
		assertFalse(tomlNull.isTomlDouble());
		assertFalse(tomlNull.isTomlString());
	}
	
	@Test
	void dateTimeTypeChecks() {
		TomlNull tomlNull = TomlNull.INSTANCE;
		
		assertFalse(tomlNull.isTomlLocalDate());
		assertFalse(tomlNull.isTomlLocalTime());
		assertFalse(tomlNull.isTomlLocalDateTime());
		assertFalse(tomlNull.isTomlOffsetDateTime());
		assertFalse(tomlNull.isTomlDateTime());
	}
	
	@Test
	void primitiveTypeConversions() {
		TomlNull tomlNull = TomlNull.INSTANCE;
		
		assertThrows(TomlTypeException.class, tomlNull::getAsBoolean);
		assertThrows(TomlTypeException.class, tomlNull::getAsNumber);
		assertThrows(TomlTypeException.class, tomlNull::getAsByte);
		assertThrows(TomlTypeException.class, tomlNull::getAsShort);
		assertThrows(TomlTypeException.class, tomlNull::getAsInteger);
		assertThrows(TomlTypeException.class, tomlNull::getAsLong);
		assertThrows(TomlTypeException.class, tomlNull::getAsFloat);
		assertThrows(TomlTypeException.class, tomlNull::getAsDouble);
		assertThrows(TomlTypeException.class, tomlNull::getAsString);
	}
	
	@Test
	void dateTimeTypeConversions() {
		TomlNull tomlNull = TomlNull.INSTANCE;
		
		assertThrows(TomlTypeException.class, tomlNull::getAsLocalDate);
		assertThrows(TomlTypeException.class, tomlNull::getAsLocalTime);
		assertThrows(TomlTypeException.class, tomlNull::getAsLocalDateTime);
		assertThrows(TomlTypeException.class, tomlNull::getAsOffsetDateTime);
	}
	
	@Test
	void toStringWithDefaultConfig() {
		assertEquals("", TomlNull.INSTANCE.toString());
	}
	
	@Test
	void toStringWithCustomConfigs() {
		assertEquals("", TomlNull.INSTANCE.toString(CUSTOM_CONFIG));
		assertEquals("", TomlNull.INSTANCE.toString(TomlConfig.DEFAULT));
		assertEquals("", TomlNull.INSTANCE.toString(TomlConfig.COMPACT));
	}
	
	@Test
	void toStringWithNullConfig() {
		assertThrows(NullPointerException.class, () -> TomlNull.INSTANCE.toString(null));
	}
	
	@Test
	void equalsAndHashCode() {
		TomlNull null1 = TomlNull.INSTANCE;
		TomlNull null2 = TomlNull.INSTANCE;
		
		assertEquals(null1, null2);
		assertEquals(null1.hashCode(), null2.hashCode());
		
		assertEquals(null1, null1);
		
		assertNotEquals(null1, null);
		assertNotEquals(null1, "null");
		assertNotEquals(null1, new TomlValue("null"));
		assertNotEquals(null1, new TomlTable());
		assertNotEquals(null1, new TomlArray());
	}
	
	@Test
	void consistentBehaviorAcrossMultipleCalls() {
		for (int i = 0; i < 100; i++) {
			assertTrue(TomlNull.INSTANCE.isTomlNull());
			assertFalse(TomlNull.INSTANCE.isTomlValue());
			assertEquals("", TomlNull.INSTANCE.toString());
			assertSame(TomlNull.INSTANCE, TomlNull.INSTANCE);
		}
	}
	
	@Test
	void interactionWithOtherTomlElements() {
		TomlArray array = new TomlArray();
		array.add(TomlNull.INSTANCE);
		
		TomlTable table = new TomlTable();
		table.add("nullValue", TomlNull.INSTANCE);
		
		assertTrue(array.contains(TomlNull.INSTANCE));
		assertEquals(TomlNull.INSTANCE, array.get(0));
		assertTrue(array.get(0).isTomlNull());
		
		assertTrue(table.containsValue(TomlNull.INSTANCE));
		assertEquals(TomlNull.INSTANCE, table.get("nullValue"));
		assertTrue(table.get("nullValue").isTomlNull());
	}
}
