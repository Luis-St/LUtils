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

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link BinaryConfig}.<br>
 *
 * @author Luis-St
 */
class BinaryConfigTest {
	
	@Test
	void constructWithValidValues() {
		BinaryConfig config = new BinaryConfig(true, 8, 16, 32, StandardCharsets.UTF_8);
		
		assertTrue(config.writeHeader());
		assertEquals(8, config.maxDepth());
		assertEquals(16, config.maxCollectionSize());
		assertEquals(32, config.maxStringLength());
		assertEquals(StandardCharsets.UTF_8, config.charset());
	}
	
	@Test
	void constructWithNullCharset() {
		assertThrows(NullPointerException.class, () -> new BinaryConfig(false, 1, 1, 1, null));
	}
	
	@Test
	void constructWithZeroMaxDepth() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new BinaryConfig(false, 0, 1, 1, StandardCharsets.UTF_8));
		
		assertTrue(exception.getMessage().contains("Max depth"));
		assertTrue(exception.getMessage().contains("0"));
	}
	
	@Test
	void constructWithNegativeMaxDepth() {
		assertThrows(IllegalArgumentException.class, () -> new BinaryConfig(false, -1, 1, 1, StandardCharsets.UTF_8));
	}
	
	@Test
	void constructWithZeroMaxCollectionSize() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new BinaryConfig(false, 1, 0, 1, StandardCharsets.UTF_8));
		
		assertTrue(exception.getMessage().contains("Max collection size"));
	}
	
	@Test
	void constructWithNegativeMaxCollectionSize() {
		assertThrows(IllegalArgumentException.class, () -> new BinaryConfig(false, 1, -5, 1, StandardCharsets.UTF_8));
	}
	
	@Test
	void constructWithZeroMaxStringLength() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new BinaryConfig(false, 1, 1, 0, StandardCharsets.UTF_8));
		
		assertTrue(exception.getMessage().contains("Max string length"));
	}
	
	@Test
	void constructWithNegativeMaxStringLength() {
		assertThrows(IllegalArgumentException.class, () -> new BinaryConfig(false, 1, 1, Integer.MIN_VALUE, StandardCharsets.UTF_8));
	}
	
	@Test
	void constructWithMinimalValidLimits() {
		BinaryConfig config = assertDoesNotThrow(() -> new BinaryConfig(false, 1, 1, 1, StandardCharsets.UTF_8));
		
		assertEquals(1, config.maxDepth());
		assertEquals(1, config.maxCollectionSize());
		assertEquals(1, config.maxStringLength());
	}
	
	@Test
	void constructWithHeaderEnabled() {
		assertTrue(new BinaryConfig(true, 1, 1, 1, StandardCharsets.UTF_8).writeHeader());
	}
	
	@Test
	void constructWithHeaderDisabled() {
		assertFalse(new BinaryConfig(false, 1, 1, 1, StandardCharsets.UTF_8).writeHeader());
	}
	
	@Test
	void constructWithMaximumLimits() {
		assertDoesNotThrow(() -> new BinaryConfig(false, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, StandardCharsets.UTF_8));
	}
	
	@Test
	void defaultConfigValues() {
		BinaryConfig config = BinaryConfig.DEFAULT;
		
		assertFalse(config.writeHeader());
		assertEquals(64, config.maxDepth());
		assertEquals(65536, config.maxCollectionSize());
		assertEquals(1048576, config.maxStringLength());
		assertEquals(StandardCharsets.UTF_8, config.charset());
	}
	
	@Test
	void magicAndVersionConstants() {
		assertEquals(0x4C42, BinaryConfig.MAGIC);
		assertEquals(1, BinaryConfig.VERSION);
	}
	
	@Test
	void constructWithNonUtf8Charset() {
		BinaryConfig config = new BinaryConfig(false, 1, 1, 1, StandardCharsets.ISO_8859_1);
		
		assertEquals(StandardCharsets.ISO_8859_1, config.charset());
	}
	
	@Test
	void equalsAndHashCode() {
		BinaryConfig config = new BinaryConfig(true, 8, 16, 32, StandardCharsets.UTF_8);
		BinaryConfig same = new BinaryConfig(true, 8, 16, 32, StandardCharsets.UTF_8);
		
		assertEquals(config, same);
		assertEquals(config.hashCode(), same.hashCode());
		
		assertNotEquals(config, new BinaryConfig(false, 8, 16, 32, StandardCharsets.UTF_8));
		assertNotEquals(config, new BinaryConfig(true, 9, 16, 32, StandardCharsets.UTF_8));
		assertNotEquals(config, new BinaryConfig(true, 8, 17, 32, StandardCharsets.UTF_8));
		assertNotEquals(config, new BinaryConfig(true, 8, 16, 33, StandardCharsets.UTF_8));
		assertNotEquals(config, new BinaryConfig(true, 8, 16, 32, StandardCharsets.ISO_8859_1));
	}
	
	@Test
	void toStringRepresentation() {
		String result = new BinaryConfig(true, 8, 16, 32, StandardCharsets.UTF_8).toString();
		
		assertTrue(result.contains("writeHeader"));
		assertTrue(result.contains("maxDepth"));
		assertTrue(result.contains("8"));
		assertTrue(result.contains("16"));
		assertTrue(result.contains("32"));
	}
	
	@Test
	void configDrivesReaderStringLimit() {
		byte[] data = BinaryWriter.toByteArray(new BinaryPrimitive("abcdef"));
		
		BinaryConfig restricted = new BinaryConfig(false, 64, 65536, 1, StandardCharsets.UTF_8);
		assertThrows(BinarySyntaxException.class, () -> BinaryReader.fromByteArray(data, restricted));
		
		BinaryConfig allowed = new BinaryConfig(false, 64, 65536, 6, StandardCharsets.UTF_8);
		assertEquals("abcdef", BinaryReader.fromByteArray(data, allowed).getAsString());
	}
	
	@Test
	void configDrivesReaderCollectionAndDepthLimits() {
		BinaryArray array = new BinaryArray();
		array.add(1);
		array.add(2);
		array.add(3);
		byte[] arrayData = BinaryWriter.toByteArray(array);
		
		BinaryConfig smallCollection = new BinaryConfig(false, 64, 2, 1048576, StandardCharsets.UTF_8);
		assertThrows(BinarySyntaxException.class, () -> BinaryReader.fromByteArray(arrayData, smallCollection));
		assertDoesNotThrow(() -> BinaryReader.fromByteArray(arrayData));
		
		BinaryArray nested = new BinaryArray(new BinaryArray(new BinaryArray()));
		byte[] nestedData = BinaryWriter.toByteArray(nested);
		
		BinaryConfig shallow = new BinaryConfig(false, 1, 65536, 1048576, StandardCharsets.UTF_8);
		assertThrows(BinarySyntaxException.class, () -> BinaryReader.fromByteArray(nestedData, shallow));
		assertDoesNotThrow(() -> BinaryReader.fromByteArray(nestedData));
	}
	
	@Test
	void headerRoundTripWithCustomCharset() {
		BinaryConfig config = new BinaryConfig(true, 64, 65536, 1048576, StandardCharsets.ISO_8859_1);
		
		byte[] data = BinaryWriter.toByteArray(new BinaryPrimitive("ä"), config);
		
		assertEquals((byte) 0x4C, data[0]);
		assertEquals((byte) 0x42, data[1]);
		assertEquals((byte) 0x01, data[2]);
		assertEquals("ä", BinaryReader.fromByteArray(data, config).getAsString());
	}
}
