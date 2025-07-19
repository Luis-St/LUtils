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

package net.luis.utils.io.codec.group.grouper;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.ConfiguredCodec;
import net.luis.utils.io.codec.group.function.CodecGroupingFunction3;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class CodecGrouper3Test {
	
	record TestRecord(int field1, int field2, int field3) {}
	
	private static @NotNull ConfiguredCodec<Integer, TestRecord> createConfiguredCodec(@NotNull String name, @NotNull Function<TestRecord, Integer> getter) {
		return Codec.INTEGER.configure(name, getter);
	}
	
	@Test
	void constructorWithValidCodecs() {
		ConfiguredCodec<Integer, TestRecord> codec1 = createConfiguredCodec("test1", TestRecord::field1);
		ConfiguredCodec<Integer, TestRecord> codec2 = createConfiguredCodec("test2", TestRecord::field2);
		ConfiguredCodec<Integer, TestRecord> codec3 = createConfiguredCodec("test3", TestRecord::field3);
		
		CodecGrouper3<Integer, Integer, Integer, TestRecord> grouper = new CodecGrouper3<>(codec1, codec2, codec3);
		
		assertEquals(codec1, grouper.codec1());
		assertEquals(codec2, grouper.codec2());
		assertEquals(codec3, grouper.codec3());
	}
	
	@Test
	void constructorWithNullFirstCodecThrowsException() {
		ConfiguredCodec<Integer, TestRecord> codec2 = createConfiguredCodec("test2", TestRecord::field2);
		ConfiguredCodec<Integer, TestRecord> codec3 = createConfiguredCodec("test3", TestRecord::field3);
		
		assertThrows(NullPointerException.class, () -> new CodecGrouper3<>(null, codec2, codec3));
	}
	
	@Test
	void constructorWithNullSecondCodecThrowsException() {
		ConfiguredCodec<Integer, TestRecord> codec1 = createConfiguredCodec("test1", TestRecord::field1);
		ConfiguredCodec<Integer, TestRecord> codec3 = createConfiguredCodec("test3", TestRecord::field3);
		
		assertThrows(NullPointerException.class, () -> new CodecGrouper3<>(codec1, null, codec3));
	}
	
	@Test
	void constructorWithNullThirdCodecThrowsException() {
		ConfiguredCodec<Integer, TestRecord> codec1 = createConfiguredCodec("test1", TestRecord::field1);
		ConfiguredCodec<Integer, TestRecord> codec2 = createConfiguredCodec("test2", TestRecord::field2);
		
		assertThrows(NullPointerException.class, () -> new CodecGrouper3<>(codec1, codec2, null));
	}
	
	@Test
	void createWithValidFunction() {
		ConfiguredCodec<Integer, TestRecord> codec1 = createConfiguredCodec("test1", TestRecord::field1);
		ConfiguredCodec<Integer, TestRecord> codec2 = createConfiguredCodec("test2", TestRecord::field2);
		ConfiguredCodec<Integer, TestRecord> codec3 = createConfiguredCodec("test3", TestRecord::field3);
		CodecGrouper3<Integer, Integer, Integer, TestRecord> grouper = new CodecGrouper3<>(codec1, codec2, codec3);
		
		Codec<TestRecord> result = grouper.create(TestRecord::new);
		
		assertNotNull(result);
		assertTrue(result.toString().contains("Codec["));
	}
	
	@Test
	void createWithNullFunctionThrowsException() {
		ConfiguredCodec<Integer, TestRecord> codec1 = createConfiguredCodec("test1", TestRecord::field1);
		ConfiguredCodec<Integer, TestRecord> codec2 = createConfiguredCodec("test2", TestRecord::field2);
		ConfiguredCodec<Integer, TestRecord> codec3 = createConfiguredCodec("test3", TestRecord::field3);
		CodecGrouper3<Integer, Integer, Integer, TestRecord> grouper = new CodecGrouper3<>(codec1, codec2, codec3);
		
		assertThrows(NullPointerException.class, () -> grouper.create(null));
	}
	
	@Test
	void equalsSameInstance() {
		ConfiguredCodec<Integer, TestRecord> codec1 = createConfiguredCodec("test1", TestRecord::field1);
		ConfiguredCodec<Integer, TestRecord> codec2 = createConfiguredCodec("test2", TestRecord::field2);
		ConfiguredCodec<Integer, TestRecord> codec3 = createConfiguredCodec("test3", TestRecord::field3);
		CodecGrouper3<Integer, Integer, Integer, TestRecord> grouper = new CodecGrouper3<>(codec1, codec2, codec3);
		
		assertEquals(grouper, grouper);
	}
	
	@Test
	void equalsWithSameCodecs() {
		ConfiguredCodec<Integer, TestRecord> codec1 = createConfiguredCodec("test1", TestRecord::field1);
		ConfiguredCodec<Integer, TestRecord> codec2 = createConfiguredCodec("test2", TestRecord::field2);
		ConfiguredCodec<Integer, TestRecord> codec3 = createConfiguredCodec("test3", TestRecord::field3);
		CodecGrouper3<Integer, Integer, Integer, TestRecord> grouper1 = new CodecGrouper3<>(codec1, codec2, codec3);
		CodecGrouper3<Integer, Integer, Integer, TestRecord> grouper2 = new CodecGrouper3<>(codec1, codec2, codec3);
		
		assertEquals(grouper1, grouper2);
	}
	
	@Test
	void equalsWithDifferentThirdCodec() {
		ConfiguredCodec<Integer, TestRecord> codec1 = createConfiguredCodec("test1", TestRecord::field1);
		ConfiguredCodec<Integer, TestRecord> codec2 = createConfiguredCodec("test2", TestRecord::field2);
		ConfiguredCodec<Integer, TestRecord> codec3a = createConfiguredCodec("test3a", TestRecord::field3);
		ConfiguredCodec<Integer, TestRecord> codec3b = createConfiguredCodec("test3b", TestRecord::field3);
		CodecGrouper3<Integer, Integer, Integer, TestRecord> grouper1 = new CodecGrouper3<>(codec1, codec2, codec3a);
		CodecGrouper3<Integer, Integer, Integer, TestRecord> grouper2 = new CodecGrouper3<>(codec1, codec2, codec3b);
		
		assertNotEquals(grouper1, grouper2);
	}
	
	@Test
	void hashCodeConsistency() {
		ConfiguredCodec<Integer, TestRecord> codec1 = createConfiguredCodec("test1", TestRecord::field1);
		ConfiguredCodec<Integer, TestRecord> codec2 = createConfiguredCodec("test2", TestRecord::field2);
		ConfiguredCodec<Integer, TestRecord> codec3 = createConfiguredCodec("test3", TestRecord::field3);
		CodecGrouper3<Integer, Integer, Integer, TestRecord> grouper1 = new CodecGrouper3<>(codec1, codec2, codec3);
		CodecGrouper3<Integer, Integer, Integer, TestRecord> grouper2 = new CodecGrouper3<>(codec1, codec2, codec3);
		
		assertEquals(grouper1.hashCode(), grouper2.hashCode());
	}
	
	@Test
	void toStringContainsCodecInfo() {
		ConfiguredCodec<Integer, TestRecord> codec1 = createConfiguredCodec("test1", TestRecord::field1);
		ConfiguredCodec<Integer, TestRecord> codec2 = createConfiguredCodec("test2", TestRecord::field2);
		ConfiguredCodec<Integer, TestRecord> codec3 = createConfiguredCodec("test3", TestRecord::field3);
		CodecGrouper3<Integer, Integer, Integer, TestRecord> grouper = new CodecGrouper3<>(codec1, codec2, codec3);
		
		String result = grouper.toString();
		
		assertTrue(result.contains("Codec["));
		assertTrue(result.contains("test1"));
		assertTrue(result.contains("test2"));
		assertTrue(result.contains("test3"));
	}
}
