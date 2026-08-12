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

package net.luis.utils.io.data.binary;

import net.luis.utils.io.data.binary.exception.BinarySyntaxException;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link BinaryType}.<br>
 *
 * @author Luis-St
 */
class BinaryTypeTest {
	
	@Test
	void constructAllTypesHaveIdAndName() {
		Set<Byte> ids = new HashSet<>();
		for (BinaryType type : BinaryType.values()) {
			assertNotNull(type.getName());
			assertFalse(type.getName().isEmpty());
			assertNotEquals(BinaryType.BOOLEAN_TRUE_ID, type.getId());
			assertTrue(ids.add(type.getId()), "Duplicate id for " + type);
		}
	}
	
	@Test
	void fromIdWithUnknownId() {
		BinarySyntaxException exception = assertThrows(BinarySyntaxException.class, () -> BinaryType.fromId((byte) 0x7F));
		
		assertTrue(exception.getMessage().contains("0x7F"));
	}
	
	@Test
	void fromIdWithNegativeId() {
		BinarySyntaxException exception = assertThrows(BinarySyntaxException.class, () -> BinaryType.fromId((byte) 0xFF));
		
		assertTrue(exception.getMessage().contains("0xFF"));
	}
	
	@Test
	void valueOfWithUnknownName() {
		assertThrows(IllegalArgumentException.class, () -> BinaryType.valueOf("UNKNOWN"));
	}
	
	@Test
	void fromIdWithBooleanTrueId() {
		assertEquals(BinaryType.BOOLEAN, BinaryType.fromId((byte) 0x03));
	}
	
	@Test
	void fromIdWithBooleanFalseId() {
		assertEquals(BinaryType.BOOLEAN, BinaryType.fromId((byte) 0x02));
	}
	
	@Test
	void fromIdWithNullId() {
		assertEquals(BinaryType.NULL, BinaryType.fromId((byte) 0x00));
	}
	
	@Test
	void fromIdWithMapId() {
		assertEquals(BinaryType.MAP, BinaryType.fromId((byte) 0x0D));
	}
	
	@Test
	void getIdOfEachType() {
		assertEquals((byte) 0x00, BinaryType.NULL.getId());
		assertEquals((byte) 0x01, BinaryType.ABSENT.getId());
		assertEquals((byte) 0x02, BinaryType.BOOLEAN.getId());
		assertEquals((byte) 0x04, BinaryType.BYTE.getId());
		assertEquals((byte) 0x05, BinaryType.SHORT.getId());
		assertEquals((byte) 0x06, BinaryType.INTEGER.getId());
		assertEquals((byte) 0x07, BinaryType.LONG.getId());
		assertEquals((byte) 0x08, BinaryType.FLOAT.getId());
		assertEquals((byte) 0x09, BinaryType.DOUBLE.getId());
		assertEquals((byte) 0x0A, BinaryType.STRING.getId());
		assertEquals((byte) 0x0B, BinaryType.LIST.getId());
		assertEquals((byte) 0x0C, BinaryType.STRUCT.getId());
		assertEquals((byte) 0x0D, BinaryType.MAP.getId());
	}
	
	@Test
	void getNameOfEachType() {
		assertEquals("binary null", BinaryType.NULL.getName());
		assertEquals("binary absent", BinaryType.ABSENT.getName());
		assertEquals("binary boolean", BinaryType.BOOLEAN.getName());
		assertEquals("binary byte", BinaryType.BYTE.getName());
		assertEquals("binary short", BinaryType.SHORT.getName());
		assertEquals("binary integer", BinaryType.INTEGER.getName());
		assertEquals("binary long", BinaryType.LONG.getName());
		assertEquals("binary float", BinaryType.FLOAT.getName());
		assertEquals("binary double", BinaryType.DOUBLE.getName());
		assertEquals("binary string", BinaryType.STRING.getName());
		assertEquals("binary list", BinaryType.LIST.getName());
		assertEquals("binary struct", BinaryType.STRUCT.getName());
		assertEquals("binary map", BinaryType.MAP.getName());
	}
	
	@Test
	void toStringReturnsName() {
		assertEquals("binary integer", BinaryType.INTEGER.toString());
		
		for (BinaryType type : BinaryType.values()) {
			assertEquals(type.getName(), type.toString());
		}
	}
	
	@Test
	void booleanTrueIdIsNotAnOwnType() {
		assertEquals(0x03, BinaryType.BOOLEAN_TRUE_ID);
		
		for (BinaryType type : BinaryType.values()) {
			assertNotEquals(0x03, type.getId());
		}
	}
	
	@Test
	void typeCountIsStable() {
		assertEquals(13, BinaryType.values().length);
	}
	
	@Test
	void fromIdRoundTripForAllTypes() {
		for (BinaryType type : BinaryType.values()) {
			assertSame(type, BinaryType.fromId(type.getId()));
		}
	}
	
	@Test
	void fromIdRejectsAllUnusedIds() {
		Set<Byte> accepted = new HashSet<>();
		for (BinaryType type : BinaryType.values()) {
			accepted.add(type.getId());
		}
		accepted.add(BinaryType.BOOLEAN_TRUE_ID);
		
		for (int value = 0; value < 256; value++) {
			byte id = (byte) value;
			if (accepted.contains(id)) {
				assertDoesNotThrow(() -> BinaryType.fromId(id));
			} else {
				assertThrows(BinarySyntaxException.class, () -> BinaryType.fromId(id));
			}
		}
		assertEquals(14, accepted.size());
	}
}
