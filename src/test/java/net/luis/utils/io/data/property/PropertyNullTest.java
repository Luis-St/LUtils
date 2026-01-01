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

package net.luis.utils.io.data.property;

import net.luis.utils.io.data.property.exception.PropertyTypeException;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link PropertyNull}.<br>
 *
 * @author Luis-St
 */
class PropertyNullTest {
	
	@Test
	void singletonInstance() {
		assertSame(PropertyNull.INSTANCE, PropertyNull.INSTANCE);
		
		PropertyNull null1 = PropertyNull.INSTANCE;
		PropertyNull null2 = PropertyNull.INSTANCE;
		assertSame(null1, null2);
	}
	
	@Test
	void propertyElementTypeChecks() {
		PropertyNull propertyNull = PropertyNull.INSTANCE;
		
		assertTrue(propertyNull.isPropertyNull());
		assertFalse(propertyNull.isPropertyObject());
		assertFalse(propertyNull.isPropertyArray());
		assertFalse(propertyNull.isPropertyValue());
	}
	
	@Test
	void propertyElementConversions() {
		PropertyNull propertyNull = PropertyNull.INSTANCE;
		
		assertThrows(PropertyTypeException.class, propertyNull::getAsPropertyObject);
		assertThrows(PropertyTypeException.class, propertyNull::getAsPropertyArray);
		assertThrows(PropertyTypeException.class, propertyNull::getAsPropertyValue);
	}
	
	@Test
	void propertyElementConversionExceptionMessages() {
		PropertyNull propertyNull = PropertyNull.INSTANCE;
		
		PropertyTypeException objectException = assertThrows(PropertyTypeException.class, propertyNull::getAsPropertyObject);
		assertTrue(objectException.getMessage().contains("property object"));
		assertTrue(objectException.getMessage().contains("property null"));
		
		PropertyTypeException arrayException = assertThrows(PropertyTypeException.class, propertyNull::getAsPropertyArray);
		assertTrue(arrayException.getMessage().contains("property array"));
		assertTrue(arrayException.getMessage().contains("property null"));
		
		PropertyTypeException valueException = assertThrows(PropertyTypeException.class, propertyNull::getAsPropertyValue);
		assertTrue(valueException.getMessage().contains("property value"));
		assertTrue(valueException.getMessage().contains("property null"));
	}
	
	@Test
	void toStringWithDefaultConfig() {
		assertEquals("", PropertyNull.INSTANCE.toString());
	}
	
	@Test
	void toStringWithNullStyles() {
		PropertyConfig emptyConfig = new PropertyConfig(
			'=', 1, Set.of('#'),
			Pattern.compile(".*"), Pattern.compile(".*"),
			false, StandardCharsets.UTF_8,
			false, "",
			'[', ']', ',',
			true, false,
			PropertyConfig.NullStyle.EMPTY,
			false, 2,
			':', ":-",
			null
		);
		assertEquals("", PropertyNull.INSTANCE.toString(emptyConfig));
		
		PropertyConfig nullStringConfig = new PropertyConfig(
			'=', 1, Set.of('#'),
			Pattern.compile(".*"), Pattern.compile(".*"),
			false, StandardCharsets.UTF_8,
			false, "",
			'[', ']', ',',
			true, false,
			PropertyConfig.NullStyle.NULL_STRING,
			false, 2,
			':', ":-",
			null
		);
		assertEquals("null", PropertyNull.INSTANCE.toString(nullStringConfig));
		
		PropertyConfig tildeConfig = new PropertyConfig(
			'=', 1, Set.of('#'),
			Pattern.compile(".*"), Pattern.compile(".*"),
			false, StandardCharsets.UTF_8,
			false, "",
			'[', ']', ',',
			true, false,
			PropertyConfig.NullStyle.TILDE,
			false, 2,
			':', ":-",
			null
		);
		assertEquals("~", PropertyNull.INSTANCE.toString(tildeConfig));
	}
	
	@Test
	void toStringWithNullConfig() {
		assertThrows(NullPointerException.class, () -> PropertyNull.INSTANCE.toString(null));
	}
	
	@Test
	void consistentBehaviorAcrossMultipleCalls() {
		for (int i = 0; i < 100; i++) {
			assertTrue(PropertyNull.INSTANCE.isPropertyNull());
			assertFalse(PropertyNull.INSTANCE.isPropertyObject());
			assertEquals("", PropertyNull.INSTANCE.toString());
			assertSame(PropertyNull.INSTANCE, PropertyNull.INSTANCE);
		}
	}
	
	@Test
	void interactionWithOtherPropertyElements() {
		PropertyArray array = new PropertyArray();
		array.add(PropertyNull.INSTANCE);
		
		PropertyObject object = new PropertyObject();
		object.add("nullValue", PropertyNull.INSTANCE);
		
		assertTrue(array.contains(PropertyNull.INSTANCE));
		assertEquals(PropertyNull.INSTANCE, array.get(0));
		
		assertTrue(object.containsValue(PropertyNull.INSTANCE));
		assertEquals(PropertyNull.INSTANCE, object.get("nullValue"));
	}
	
	@Test
	void primitiveTypeChecks() {
		PropertyNull propertyNull = PropertyNull.INSTANCE;
		
		assertFalse(propertyNull.isBoolean());
		assertFalse(propertyNull.isNumber());
		assertFalse(propertyNull.isByte());
		assertFalse(propertyNull.isShort());
		assertFalse(propertyNull.isInteger());
		assertFalse(propertyNull.isLong());
		assertFalse(propertyNull.isFloat());
		assertFalse(propertyNull.isDouble());
		assertFalse(propertyNull.isString());
	}
	
	@Test
	void primitiveGettersThrowException() {
		PropertyNull propertyNull = PropertyNull.INSTANCE;
		
		assertThrows(PropertyTypeException.class, propertyNull::getAsBoolean);
		assertThrows(PropertyTypeException.class, propertyNull::getAsNumber);
		assertThrows(PropertyTypeException.class, propertyNull::getAsByte);
		assertThrows(PropertyTypeException.class, propertyNull::getAsShort);
		assertThrows(PropertyTypeException.class, propertyNull::getAsInteger);
		assertThrows(PropertyTypeException.class, propertyNull::getAsLong);
		assertThrows(PropertyTypeException.class, propertyNull::getAsFloat);
		assertThrows(PropertyTypeException.class, propertyNull::getAsDouble);
		assertThrows(PropertyTypeException.class, propertyNull::getAsString);
	}
}
