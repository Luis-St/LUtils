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

package net.luis.utils.io.data.ini;

import net.luis.utils.io.data.ini.exception.IniTypeException;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link IniNull}.<br>
 *
 * @author Luis-St
 */
class IniNullTest {
	
	private static final IniConfig EMPTY_STYLE_CONFIG = new IniConfig(
		true, true, "\t", Set.of(';', '#'), '=', 0,
		false, false, false,
		Pattern.compile("^[a-zA-Z0-9._-]+$"), Pattern.compile("^[a-zA-Z0-9._-]+$"),
		IniConfig.NullStyle.EMPTY, StandardCharsets.UTF_8
	);
	
	private static final IniConfig NULL_STRING_STYLE_CONFIG = new IniConfig(
		true, true, "\t", Set.of(';', '#'), '=', 0,
		false, false, false,
		Pattern.compile("^[a-zA-Z0-9._-]+$"), Pattern.compile("^[a-zA-Z0-9._-]+$"),
		IniConfig.NullStyle.NULL_STRING, StandardCharsets.UTF_8
	);
	
	private static final IniConfig SKIP_STYLE_CONFIG = new IniConfig(
		true, true, "\t", Set.of(';', '#'), '=', 0,
		false, false, false,
		Pattern.compile("^[a-zA-Z0-9._-]+$"), Pattern.compile("^[a-zA-Z0-9._-]+$"),
		IniConfig.NullStyle.SKIP, StandardCharsets.UTF_8
	);
	
	@Test
	void singletonInstance() {
		assertSame(IniNull.INSTANCE, IniNull.INSTANCE);
		
		IniNull null1 = IniNull.INSTANCE;
		IniNull null2 = IniNull.INSTANCE;
		assertSame(null1, null2);
	}
	
	@Test
	void iniElementTypeChecks() {
		IniNull iniNull = IniNull.INSTANCE;
		
		assertTrue(iniNull.isIniNull());
		assertFalse(iniNull.isIniValue());
		assertFalse(iniNull.isIniSection());
		assertFalse(iniNull.isIniDocument());
	}
	
	@Test
	void iniElementConversions() {
		IniNull iniNull = IniNull.INSTANCE;
		
		assertThrows(IniTypeException.class, iniNull::getAsIniValue);
		assertThrows(IniTypeException.class, iniNull::getAsIniSection);
		assertThrows(IniTypeException.class, iniNull::getAsIniDocument);
	}
	
	@Test
	void iniElementConversionExceptionMessages() {
		IniNull iniNull = IniNull.INSTANCE;
		
		IniTypeException valueException = assertThrows(IniTypeException.class, iniNull::getAsIniValue);
		assertTrue(valueException.getMessage().contains("ini value"));
		assertTrue(valueException.getMessage().contains("ini null"));
		
		IniTypeException sectionException = assertThrows(IniTypeException.class, iniNull::getAsIniSection);
		assertTrue(sectionException.getMessage().contains("ini section"));
		assertTrue(sectionException.getMessage().contains("ini null"));
		
		IniTypeException documentException = assertThrows(IniTypeException.class, iniNull::getAsIniDocument);
		assertTrue(documentException.getMessage().contains("ini document"));
		assertTrue(documentException.getMessage().contains("ini null"));
	}
	
	@Test
	void primitiveTypeChecks() {
		IniNull iniNull = IniNull.INSTANCE;
		
		assertFalse(iniNull.isBoolean());
		assertFalse(iniNull.isNumber());
		assertFalse(iniNull.isByte());
		assertFalse(iniNull.isShort());
		assertFalse(iniNull.isInteger());
		assertFalse(iniNull.isLong());
		assertFalse(iniNull.isFloat());
		assertFalse(iniNull.isDouble());
		assertFalse(iniNull.isString());
	}
	
	@Test
	void primitiveTypeConversions() {
		IniNull iniNull = IniNull.INSTANCE;
		
		assertThrows(IniTypeException.class, iniNull::getAsBoolean);
		assertThrows(IniTypeException.class, iniNull::getAsNumber);
		assertThrows(IniTypeException.class, iniNull::getAsByte);
		assertThrows(IniTypeException.class, iniNull::getAsShort);
		assertThrows(IniTypeException.class, iniNull::getAsInteger);
		assertThrows(IniTypeException.class, iniNull::getAsLong);
		assertThrows(IniTypeException.class, iniNull::getAsFloat);
		assertThrows(IniTypeException.class, iniNull::getAsDouble);
		assertThrows(IniTypeException.class, iniNull::getAsString);
	}
	
	@Test
	void toStringWithDefaultConfig() {
		assertEquals("", IniNull.INSTANCE.toString());
	}
	
	@Test
	void toStringWithEmptyStyle() {
		assertEquals("", IniNull.INSTANCE.toString(EMPTY_STYLE_CONFIG));
	}
	
	@Test
	void toStringWithNullStringStyle() {
		assertEquals("null", IniNull.INSTANCE.toString(NULL_STRING_STYLE_CONFIG));
	}
	
	@Test
	void toStringWithSkipStyle() {
		assertEquals("", IniNull.INSTANCE.toString(SKIP_STYLE_CONFIG));
	}
	
	@Test
	void toStringWithNullConfig() {
		assertThrows(NullPointerException.class, () -> IniNull.INSTANCE.toString(null));
	}
	
	@Test
	void equalsAndHashCode() {
		IniNull null1 = IniNull.INSTANCE;
		IniNull null2 = IniNull.INSTANCE;
		
		assertEquals(null1, null2);
		assertEquals(null1.hashCode(), null2.hashCode());
		
		assertEquals(null1, null1);
		
		assertNotEquals(null1, null);
		assertNotEquals(null1, "null");
		assertNotEquals(null1, new IniValue("null"));
	}
	
	@Test
	void consistentBehaviorAcrossMultipleCalls() {
		for (int i = 0; i < 100; i++) {
			assertTrue(IniNull.INSTANCE.isIniNull());
			assertFalse(IniNull.INSTANCE.isIniValue());
			assertEquals("", IniNull.INSTANCE.toString());
			assertSame(IniNull.INSTANCE, IniNull.INSTANCE);
		}
	}
	
	@Test
	void interactionWithOtherIniElements() {
		IniSection section = new IniSection("test");
		section.add("nullValue", IniNull.INSTANCE);
		
		assertTrue(section.containsValue(IniNull.INSTANCE));
		assertEquals(IniNull.INSTANCE, section.get("nullValue"));
		assertTrue(section.get("nullValue").isIniNull());
	}
}
