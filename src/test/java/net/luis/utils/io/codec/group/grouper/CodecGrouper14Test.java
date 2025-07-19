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
import net.luis.utils.io.codec.group.function.CodecGroupingFunction14;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class CodecGrouper14Test {
	
	record TestRecord(int field1, int field2, int field3, int field4, int field5, int field6, int field7, int field8, int field9, int field10, int field11, int field12, int field13, int field14) {}
	
	private static @NotNull ConfiguredCodec<Integer, TestRecord> createConfiguredCodec(@NotNull String name, @NotNull Function<TestRecord, Integer> getter) {
		return Codec.INTEGER.configure(name, getter);
	}
	
	@Test
	void constructorWithValidCodecs() {
		ConfiguredCodec<Integer, TestRecord> codec1 = createConfiguredCodec("test1", TestRecord::field1);
		ConfiguredCodec<Integer, TestRecord> codec2 = createConfiguredCodec("test2", TestRecord::field2);
		ConfiguredCodec<Integer, TestRecord> codec3 = createConfiguredCodec("test3", TestRecord::field3);
		ConfiguredCodec<Integer, TestRecord> codec4 = createConfiguredCodec("test4", TestRecord::field4);
		ConfiguredCodec<Integer, TestRecord> codec5 = createConfiguredCodec("test5", TestRecord::field5);
		ConfiguredCodec<Integer, TestRecord> codec6 = createConfiguredCodec("test6", TestRecord::field6);
		ConfiguredCodec<Integer, TestRecord> codec7 = createConfiguredCodec("test7", TestRecord::field7);
		ConfiguredCodec<Integer, TestRecord> codec8 = createConfiguredCodec("test8", TestRecord::field8);
		ConfiguredCodec<Integer, TestRecord> codec9 = createConfiguredCodec("test9", TestRecord::field9);
		ConfiguredCodec<Integer, TestRecord> codec10 = createConfiguredCodec("test10", TestRecord::field10);
		ConfiguredCodec<Integer, TestRecord> codec11 = createConfiguredCodec("test11", TestRecord::field11);
		ConfiguredCodec<Integer, TestRecord> codec12 = createConfiguredCodec("test12", TestRecord::field12);
		ConfiguredCodec<Integer, TestRecord> codec13 = createConfiguredCodec("test13", TestRecord::field13);
		ConfiguredCodec<Integer, TestRecord> codec14 = createConfiguredCodec("test14", TestRecord::field14);
		
		CodecGrouper14<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, TestRecord> grouper = new CodecGrouper14<>(
			codec1, codec2, codec3, codec4, codec5, codec6, codec7, codec8, codec9, codec10, codec11, codec12, codec13, codec14
		);
		
		assertEquals(codec1, grouper.codec1());
		assertEquals(codec2, grouper.codec2());
		assertEquals(codec3, grouper.codec3());
		assertEquals(codec4, grouper.codec4());
		assertEquals(codec5, grouper.codec5());
		assertEquals(codec6, grouper.codec6());
		assertEquals(codec7, grouper.codec7());
		assertEquals(codec8, grouper.codec8());
		assertEquals(codec9, grouper.codec9());
		assertEquals(codec10, grouper.codec10());
		assertEquals(codec11, grouper.codec11());
		assertEquals(codec12, grouper.codec12());
		assertEquals(codec13, grouper.codec13());
		assertEquals(codec14, grouper.codec14());
	}
	
	@Test
	void constructorWithNullCodecsThrowsException() {
		ConfiguredCodec<Integer, TestRecord> codec = createConfiguredCodec("test", TestRecord::field1);
		
		assertThrows(NullPointerException.class, () -> new CodecGrouper14<>(null, codec, codec, codec, codec, codec, codec, codec, codec, codec, codec, codec, codec, codec));
		assertThrows(NullPointerException.class, () -> new CodecGrouper14<>(codec, codec, codec, codec, codec, codec, codec, codec, codec, codec, codec, codec, codec, null));
	}
	
	@Test
	void createWithValidFunction() {
		ConfiguredCodec<Integer, TestRecord> codec = createConfiguredCodec("test", TestRecord::field1);
		CodecGrouper14<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, TestRecord> grouper = new CodecGrouper14<>(
			codec, codec, codec, codec, codec, codec, codec, codec, codec, codec, codec, codec, codec, codec
		);
		
		Codec<TestRecord> result = grouper.create(TestRecord::new);
		
		assertNotNull(result);
		assertTrue(result.toString().contains("Codec["));
	}
	
	@Test
	void createWithNullFunctionThrowsException() {
		ConfiguredCodec<Integer, TestRecord> codec = createConfiguredCodec("test", TestRecord::field1);
		CodecGrouper14<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, TestRecord> grouper = new CodecGrouper14<>(
			codec, codec, codec, codec, codec, codec, codec, codec, codec, codec, codec, codec, codec, codec
		);
		
		assertThrows(NullPointerException.class, () -> grouper.create(null));
	}
	
	@Test
	void equalsWithSameCodecs() {
		ConfiguredCodec<Integer, TestRecord> codec = createConfiguredCodec("test", TestRecord::field1);
		CodecGrouper14<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, TestRecord> grouper1 = new CodecGrouper14<>(
			codec, codec, codec, codec, codec, codec, codec, codec, codec, codec, codec, codec, codec, codec
		);
		CodecGrouper14<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, TestRecord> grouper2 = new CodecGrouper14<>(
			codec, codec, codec, codec, codec, codec, codec, codec, codec, codec, codec, codec, codec, codec
		);
		
		assertEquals(grouper1, grouper2);
	}
	
	@Test
	void equalsWithDifferentCodecs() {
		ConfiguredCodec<Integer, TestRecord> codec1 = createConfiguredCodec("test1", TestRecord::field1);
		ConfiguredCodec<Integer, TestRecord> codec2 = createConfiguredCodec("test2", TestRecord::field2);
		CodecGrouper14<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, TestRecord> grouper1 = new CodecGrouper14<>(
			codec1, codec1, codec1, codec1, codec1, codec1, codec1, codec1, codec1, codec1, codec1, codec1, codec1, codec1
		);
		CodecGrouper14<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, TestRecord> grouper2 = new CodecGrouper14<>(
			codec1, codec1, codec1, codec1, codec1, codec1, codec1, codec1, codec1, codec1, codec1, codec1, codec1, codec2
		);
		
		assertNotEquals(grouper1, grouper2);
	}
	
	@Test
	void hashCodeConsistency() {
		ConfiguredCodec<Integer, TestRecord> codec = createConfiguredCodec("test", TestRecord::field1);
		CodecGrouper14<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, TestRecord> grouper1 = new CodecGrouper14<>(
			codec, codec, codec, codec, codec, codec, codec, codec, codec, codec, codec, codec, codec, codec
		);
		CodecGrouper14<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, TestRecord> grouper2 = new CodecGrouper14<>(
			codec, codec, codec, codec, codec, codec, codec, codec, codec, codec, codec, codec, codec, codec
		);
		
		assertEquals(grouper1.hashCode(), grouper2.hashCode());
	}
	
	@Test
	void toStringContainsCodecInfo() {
		ConfiguredCodec<Integer, TestRecord> codec1 = createConfiguredCodec("test1", TestRecord::field1);
		ConfiguredCodec<Integer, TestRecord> codec14 = createConfiguredCodec("test14", TestRecord::field14);
		ConfiguredCodec<Integer, TestRecord> codec = createConfiguredCodec("test", TestRecord::field2);
		CodecGrouper14<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, TestRecord> grouper = new CodecGrouper14<>(
			codec1, codec, codec, codec, codec, codec, codec, codec, codec, codec, codec, codec, codec, codec14
		);
		
		String result = grouper.toString();
		
		assertTrue(result.contains("Codec["));
		assertTrue(result.contains("test1"));
		assertTrue(result.contains("test14"));
	}
}
