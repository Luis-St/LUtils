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

import net.luis.utils.io.data.toon.exception.ToonTypeException;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link ToonNull}.<br>
 *
 * @author Luis-St
 */
class ToonNullTest {
	
	@Test
	void singletonInstance() {
		assertSame(ToonNull.INSTANCE, ToonNull.INSTANCE);
		
		ToonNull null1 = ToonNull.INSTANCE;
		ToonNull null2 = ToonNull.INSTANCE;
		assertSame(null1, null2);
	}
	
	@Test
	void tomlElementTypeChecks() {
		ToonNull toonNull = ToonNull.INSTANCE;
		
		assertTrue(toonNull.isToonNull());
		assertFalse(toonNull.isToonValue());
		assertFalse(toonNull.isToonArray());
		assertFalse(toonNull.isToonObject());
		assertFalse(toonNull.isToonBoolean());
		assertFalse(toonNull.isToonNumber());
		assertFalse(toonNull.isToonByte());
		assertFalse(toonNull.isToonShort());
		assertFalse(toonNull.isToonInteger());
		assertFalse(toonNull.isToonLong());
		assertFalse(toonNull.isToonFloat());
		assertFalse(toonNull.isToonDouble());
		assertFalse(toonNull.isToonString());
	}
	
	@Test
	void tomlElementConversions() {
		ToonNull toonNull = ToonNull.INSTANCE;
		
		assertThrows(ToonTypeException.class, toonNull::getAsToonValue);
		assertThrows(ToonTypeException.class, toonNull::getAsToonArray);
		assertThrows(ToonTypeException.class, toonNull::getAsToonObject);
		assertThrows(ToonTypeException.class, toonNull::getAsBoolean);
		assertThrows(ToonTypeException.class, toonNull::getAsNumber);
		assertThrows(ToonTypeException.class, toonNull::getAsByte);
		assertThrows(ToonTypeException.class, toonNull::getAsShort);
		assertThrows(ToonTypeException.class, toonNull::getAsInteger);
		assertThrows(ToonTypeException.class, toonNull::getAsLong);
		assertThrows(ToonTypeException.class, toonNull::getAsFloat);
		assertThrows(ToonTypeException.class, toonNull::getAsDouble);
		assertThrows(ToonTypeException.class, toonNull::getAsString);
	}
	
	@Test
	void toStringDefault() {
		assertEquals("null", ToonNull.INSTANCE.toString());
	}
	
	@Test
	void toStringWithConfig() {
		assertEquals("null", ToonNull.INSTANCE.toString(ToonConfig.DEFAULT));
		
		ToonConfig customConfig = new ToonConfig(
			false, 4, ToonConfig.Delimiter.TAB, ToonConfig.KeyFolding.SAFE,
			10, ToonConfig.PathExpansion.SAFE, StandardCharsets.UTF_16
		);
		assertEquals("null", ToonNull.INSTANCE.toString(customConfig));
		
		assertThrows(NullPointerException.class, () -> ToonNull.INSTANCE.toString(null));
	}
}
