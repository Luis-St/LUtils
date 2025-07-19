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
import net.luis.utils.io.codec.group.function.CodecGroupingFunction4;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class CodecGrouper4Test {
	
	record TestRecord(int field1, int field2, int field3, int field4) {}
	
	private static @NotNull ConfiguredCodec<Integer, TestRecord> createConfiguredCodec(@NotNull String name, @NotNull Function<TestRecord, Integer> getter) {
		return Codec.INTEGER.configure(name, getter);
	}
	
	@Test
	void constructorWithValidCodecs() {
		ConfiguredCodec<Integer, TestRecord> codec1 = createConfiguredCodec("test1", TestRecord::field1);
		ConfiguredCodec<Integer, TestRecord> codec2 = createConfiguredCodec("test2", TestRecord::field2);
		ConfiguredCodec<Integer, TestRecord> codec3 = createConfiguredCodec("test3", TestRecord::field3);
		ConfiguredCodec<Integer, TestRecord> codec4 = createConfiguredCodec("test4", TestRecord::field4);
		
		CodecGrouper4<Integer, Integer, Integer, Integer, TestRecord> grouper = new CodecGrouper4<>(codec1, codec2, codec3, codec4);
		
		assertEquals(codec1, grouper.codec1());
		assertEquals(codec2, grouper.codec2());
		assertEquals(codec3, grouper.codec3());
		assertEquals(codec4, grouper.codec4());
	}
	
	@Test
	void constructorWithNullCodecsThrowsException() {
		ConfiguredCodec<Integer, TestRecord> codec = createConfiguredCodec("test", TestRecord::field1);
		
		assertThrows(NullPointerException.class, () -> new CodecGrouper4<>(null, codec, codec, codec));
		assertThrows(NullPointerException.class, () -> new CodecGrouper4<>(codec, null, codec, codec));
		assertThrows(NullPointerException.class, () -> new CodecGrouper4<>(codec, codec, null, codec));
		assertThrows(NullPointerException.class, () -> new CodecGrouper4<>(codec, codec, codec, null));
	}
	
	@Test
	void createWithValidFunction() {
		ConfiguredCodec<Integer, TestRecord> codec1 = createConfiguredCodec("test1", TestRecord::field1);
		ConfiguredCodec<Integer, TestRecord> codec2 = createConfiguredCodec("test2", TestRecord::field2);
		ConfiguredCodec<Integer, TestRecord> codec3 = createConfiguredCodec("test3", TestRecord::field3);
		ConfiguredCodec<Integer, TestRecord> codec4 = createConfiguredCodec("test4", TestRecord::field4);
		CodecGrouper4<Integer, Integer, Integer, Integer, TestRecord> grouper = new CodecGrouper4<>(codec1, codec2, codec3, codec4);
		
		Codec<TestRecord> result = grouper.create(TestRecord::new);
		
		assertNotNull(result);
		assertTrue(result.toString().contains("Codec["));
	}
	
	@Test
	void createWithNullFunctionThrowsException() {
		ConfiguredCodec<Integer, TestRecord> codec1 = createConfiguredCodec("test1", TestRecord::field1);
		ConfiguredCodec<Integer, TestRecord> codec2 = createConfiguredCodec("test2", TestRecord::field2);
		ConfiguredCodec<Integer, TestRecord> codec3 = createConfiguredCodec("test3", TestRecord::field3);
		ConfiguredCodec<Integer, TestRecord> codec4 = createConfiguredCodec("test4", TestRecord::field4);
		CodecGrouper4<Integer, Integer, Integer, Integer, TestRecord> grouper = new CodecGrouper4<>(codec1, codec2, codec3, codec4);
		
		assertThrows(NullPointerException.class, () -> grouper.create(null));
	}
	
	@Test
	void equalsWithSameCodecs() {
		ConfiguredCodec<Integer, TestRecord> codec1 = createConfiguredCodec("test1", TestRecord::field1);
		ConfiguredCodec<Integer, TestRecord> codec2 = createConfiguredCodec("test2", TestRecord::field2);
		ConfiguredCodec<Integer, TestRecord> codec3 = createConfiguredCodec("test3", TestRecord::field3);
		ConfiguredCodec<Integer, TestRecord> codec4 = createConfiguredCodec("test4", TestRecord::field4);
		CodecGrouper4<Integer, Integer, Integer, Integer, TestRecord> grouper1 = new CodecGrouper4<>(codec1, codec2, codec3, codec4);
		CodecGrouper4<Integer, Integer, Integer, Integer, TestRecord> grouper2 = new CodecGrouper4<>(codec1, codec2, codec3, codec4);
		
		assertEquals(grouper1, grouper2);
	}
	
	@Test
	void equalsWithDifferentCodecs() {
		ConfiguredCodec<Integer, TestRecord> codec1 = createConfiguredCodec("test1", TestRecord::field1);
		ConfiguredCodec<Integer, TestRecord> codec2 = createConfiguredCodec("test2", TestRecord::field2);
		ConfiguredCodec<Integer, TestRecord> codec3 = createConfiguredCodec("test3", TestRecord::field3);
		ConfiguredCodec<Integer, TestRecord> codec4a = createConfiguredCodec("test4a", TestRecord::field4);
		ConfiguredCodec<Integer, TestRecord> codec4b = createConfiguredCodec("test4b", TestRecord::field4);
		CodecGrouper4<Integer, Integer, Integer, Integer, TestRecord> grouper1 = new CodecGrouper4<>(codec1, codec2, codec3, codec4a);
		CodecGrouper4<Integer, Integer, Integer, Integer, TestRecord> grouper2 = new CodecGrouper4<>(codec1, codec2, codec3, codec4b);
		
		assertNotEquals(grouper1, grouper2);
	}
	
	@Test
	void hashCodeConsistency() {
		ConfiguredCodec<Integer, TestRecord> codec1 = createConfiguredCodec("test1", TestRecord::field1);
		ConfiguredCodec<Integer, TestRecord> codec2 = createConfiguredCodec("test2", TestRecord::field2);
		ConfiguredCodec<Integer, TestRecord> codec3 = createConfiguredCodec("test3", TestRecord::field3);
		ConfiguredCodec<Integer, TestRecord> codec4 = createConfiguredCodec("test4", TestRecord::field4);
		CodecGrouper4<Integer, Integer, Integer, Integer, TestRecord> grouper1 = new CodecGrouper4<>(codec1, codec2, codec3, codec4);
		CodecGrouper4<Integer, Integer, Integer, Integer, TestRecord> grouper2 = new CodecGrouper4<>(codec1, codec2, codec3, codec4);
		
		assertEquals(grouper1.hashCode(), grouper2.hashCode());
	}
	
	@Test
	void toStringContainsAllCodecInfo() {
		ConfiguredCodec<Integer, TestRecord> codec1 = createConfiguredCodec("test1", TestRecord::field1);
		ConfiguredCodec<Integer, TestRecord> codec2 = createConfiguredCodec("test2", TestRecord::field2);
		ConfiguredCodec<Integer, TestRecord> codec3 = createConfiguredCodec("test3", TestRecord::field3);
		ConfiguredCodec<Integer, TestRecord> codec4 = createConfiguredCodec("test4", TestRecord::field4);
		CodecGrouper4<Integer, Integer, Integer, Integer, TestRecord> grouper = new CodecGrouper4<>(codec1, codec2, codec3, codec4);
		
		String result = grouper.toString();
		
		assertTrue(result.contains("Codec["));
		assertTrue(result.contains("test1"));
		assertTrue(result.contains("test2"));
		assertTrue(result.contains("test3"));
		assertTrue(result.contains("test4"));
	}
}
