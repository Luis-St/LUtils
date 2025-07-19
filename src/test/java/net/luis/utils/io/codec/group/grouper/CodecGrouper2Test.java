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
import net.luis.utils.io.codec.group.function.CodecGroupingFunction2;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class CodecGrouper2Test {
	
	record TestRecord(int field1, int field2) {}
	
	private static @NotNull ConfiguredCodec<Integer, TestRecord> createConfiguredCodec(@NotNull String name, @NotNull Function<TestRecord, Integer> getter) {
		return Codec.INTEGER.configure(name, getter);
	}
	
	@Test
	void constructorWithValidCodecs() {
		ConfiguredCodec<Integer, TestRecord> codec1 = createConfiguredCodec("test1", TestRecord::field1);
		ConfiguredCodec<Integer, TestRecord> codec2 = createConfiguredCodec("test2", TestRecord::field2);
		
		CodecGrouper2<Integer, Integer, TestRecord> grouper = new CodecGrouper2<>(codec1, codec2);
		
		assertEquals(codec1, grouper.codec1());
		assertEquals(codec2, grouper.codec2());
	}
	
	@Test
	void constructorWithNullFirstCodecThrowsException() {
		ConfiguredCodec<Integer, TestRecord> codec2 = createConfiguredCodec("test2", TestRecord::field2);
		
		assertThrows(NullPointerException.class, () -> new CodecGrouper2<>(null, codec2));
	}
	
	@Test
	void constructorWithNullSecondCodecThrowsException() {
		ConfiguredCodec<Integer, TestRecord> codec1 = createConfiguredCodec("test1", TestRecord::field1);
		
		assertThrows(NullPointerException.class, () -> new CodecGrouper2<>(codec1, null));
	}
	
	@Test
	void constructorWithBothNullCodecsThrowsException() {
		assertThrows(NullPointerException.class, () -> new CodecGrouper2<>(null, null));
	}
	
	@Test
	void createWithValidFunction() {
		ConfiguredCodec<Integer, TestRecord> codec1 = createConfiguredCodec("test1", TestRecord::field1);
		ConfiguredCodec<Integer, TestRecord> codec2 = createConfiguredCodec("test2", TestRecord::field2);
		CodecGrouper2<Integer, Integer, TestRecord> grouper = new CodecGrouper2<>(codec1, codec2);
		
		Codec<TestRecord> result = grouper.create(TestRecord::new);
		
		assertNotNull(result);
		assertTrue(result.toString().contains("Codec["));
	}
	
	@Test
	void createWithNullFunctionThrowsException() {
		ConfiguredCodec<Integer, TestRecord> codec1 = createConfiguredCodec("test1", TestRecord::field1);
		ConfiguredCodec<Integer, TestRecord> codec2 = createConfiguredCodec("test2", TestRecord::field2);
		CodecGrouper2<Integer, Integer, TestRecord> grouper = new CodecGrouper2<>(codec1, codec2);
		
		assertThrows(NullPointerException.class, () -> grouper.create(null));
	}
	
	@Test
	void equalsSameInstance() {
		ConfiguredCodec<Integer, TestRecord> codec1 = createConfiguredCodec("test1", TestRecord::field1);
		ConfiguredCodec<Integer, TestRecord> codec2 = createConfiguredCodec("test2", TestRecord::field2);
		CodecGrouper2<Integer, Integer, TestRecord> grouper = new CodecGrouper2<>(codec1, codec2);
		
		assertEquals(grouper, grouper);
	}
	
	@Test
	void equalsWithSameCodecs() {
		ConfiguredCodec<Integer, TestRecord> codec1 = createConfiguredCodec("test1", TestRecord::field1);
		ConfiguredCodec<Integer, TestRecord> codec2 = createConfiguredCodec("test2", TestRecord::field2);
		CodecGrouper2<Integer, Integer, TestRecord> grouper1 = new CodecGrouper2<>(codec1, codec2);
		CodecGrouper2<Integer, Integer, TestRecord> grouper2 = new CodecGrouper2<>(codec1, codec2);
		
		assertEquals(grouper1, grouper2);
	}
	
	@Test
	void equalsWithDifferentFirstCodec() {
		ConfiguredCodec<Integer, TestRecord> codec1a = createConfiguredCodec("test1a", TestRecord::field1);
		ConfiguredCodec<Integer, TestRecord> codec1b = createConfiguredCodec("test1b", TestRecord::field1);
		ConfiguredCodec<Integer, TestRecord> codec2 = createConfiguredCodec("test2", TestRecord::field2);
		CodecGrouper2<Integer, Integer, TestRecord> grouper1 = new CodecGrouper2<>(codec1a, codec2);
		CodecGrouper2<Integer, Integer, TestRecord> grouper2 = new CodecGrouper2<>(codec1b, codec2);
		
		assertNotEquals(grouper1, grouper2);
	}
	
	@Test
	void equalsWithDifferentSecondCodec() {
		ConfiguredCodec<Integer, TestRecord> codec1 = createConfiguredCodec("test1", TestRecord::field1);
		ConfiguredCodec<Integer, TestRecord> codec2a = createConfiguredCodec("test2a", TestRecord::field2);
		ConfiguredCodec<Integer, TestRecord> codec2b = createConfiguredCodec("test2b", TestRecord::field2);
		CodecGrouper2<Integer, Integer, TestRecord> grouper1 = new CodecGrouper2<>(codec1, codec2a);
		CodecGrouper2<Integer, Integer, TestRecord> grouper2 = new CodecGrouper2<>(codec1, codec2b);
		
		assertNotEquals(grouper1, grouper2);
	}
	
	@Test
	void hashCodeConsistency() {
		ConfiguredCodec<Integer, TestRecord> codec1 = createConfiguredCodec("test1", TestRecord::field1);
		ConfiguredCodec<Integer, TestRecord> codec2 = createConfiguredCodec("test2", TestRecord::field2);
		CodecGrouper2<Integer, Integer, TestRecord> grouper1 = new CodecGrouper2<>(codec1, codec2);
		CodecGrouper2<Integer, Integer, TestRecord> grouper2 = new CodecGrouper2<>(codec1, codec2);
		
		assertEquals(grouper1.hashCode(), grouper2.hashCode());
	}
	
	@Test
	void toStringContainsCodecInfo() {
		ConfiguredCodec<Integer, TestRecord> codec1 = createConfiguredCodec("test1", TestRecord::field1);
		ConfiguredCodec<Integer, TestRecord> codec2 = createConfiguredCodec("test2", TestRecord::field2);
		CodecGrouper2<Integer, Integer, TestRecord> grouper = new CodecGrouper2<>(codec1, codec2);
		
		String result = grouper.toString();
		
		assertTrue(result.contains("CodecGrouper2"));
		assertTrue(result.contains("test1"));
		assertTrue(result.contains("test2"));
	}
}
