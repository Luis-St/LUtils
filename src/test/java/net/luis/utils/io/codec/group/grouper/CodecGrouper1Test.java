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
import net.luis.utils.io.codec.group.function.CodecGroupingFunction1;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CodecGrouper1Test {
	
	record TestRecord(int field1) {}
	
	private static @NotNull ConfiguredCodec<Integer, TestRecord> createConfiguredCodec(@NotNull String name) {
		return Codec.INTEGER.configure(name, TestRecord::field1);
	}
	
	@Test
	void constructorWithValidCodec() {
		ConfiguredCodec<Integer, TestRecord> codec = createConfiguredCodec("test");
		
		CodecGrouper1<Integer, TestRecord> grouper = new CodecGrouper1<>(codec);
		
		assertEquals(codec, grouper.codec1());
	}
	
	@Test
	void constructorWithNullCodecThrowsException() {
		assertThrows(NullPointerException.class, () -> new CodecGrouper1<>(null));
	}
	
	@Test
	void createWithValidFunction() {
		ConfiguredCodec<Integer, TestRecord> codec = createConfiguredCodec("test");
		CodecGrouper1<Integer, TestRecord> grouper = new CodecGrouper1<>(codec);
		
		Codec<TestRecord> result = grouper.create(TestRecord::new);
		
		assertNotNull(result);
		assertTrue(result.toString().contains("Codec["));
	}
	
	@Test
	void createWithNullFunctionThrowsException() {
		ConfiguredCodec<Integer, TestRecord> codec = createConfiguredCodec("test");
		CodecGrouper1<Integer, TestRecord> grouper = new CodecGrouper1<>(codec);
		
		assertThrows(NullPointerException.class, () -> grouper.create(null));
	}
	
	@Test
	void equalsSameInstance() {
		ConfiguredCodec<Integer, TestRecord> codec = createConfiguredCodec("test");
		CodecGrouper1<Integer, TestRecord> grouper = new CodecGrouper1<>(codec);
		
		assertEquals(grouper, grouper);
	}
	
	@Test
	void equalsWithSameCodecs() {
		ConfiguredCodec<Integer, TestRecord> codec = createConfiguredCodec("test");
		CodecGrouper1<Integer, TestRecord> grouper1 = new CodecGrouper1<>(codec);
		CodecGrouper1<Integer, TestRecord> grouper2 = new CodecGrouper1<>(codec);
		
		assertEquals(grouper1, grouper2);
	}
	
	@Test
	void equalsWithDifferentCodecs() {
		ConfiguredCodec<Integer, TestRecord> codec1 = createConfiguredCodec("test1");
		ConfiguredCodec<Integer, TestRecord> codec2 = createConfiguredCodec("test2");
		CodecGrouper1<Integer, TestRecord> grouper1 = new CodecGrouper1<>(codec1);
		CodecGrouper1<Integer, TestRecord> grouper2 = new CodecGrouper1<>(codec2);
		
		assertNotEquals(grouper1, grouper2);
	}
	
	@Test
	void equalsWithNull() {
		ConfiguredCodec<Integer, TestRecord> codec = createConfiguredCodec("test");
		CodecGrouper1<Integer, TestRecord> grouper = new CodecGrouper1<>(codec);
		
		assertNotEquals(grouper, null);
	}
	
	@Test
	void equalsWithDifferentType() {
		ConfiguredCodec<Integer, TestRecord> codec = createConfiguredCodec("test");
		CodecGrouper1<Integer, TestRecord> grouper = new CodecGrouper1<>(codec);
		
		assertNotEquals(grouper, "not a grouper");
	}
	
	@Test
	void hashCodeConsistency() {
		ConfiguredCodec<Integer, TestRecord> codec = createConfiguredCodec("test");
		CodecGrouper1<Integer, TestRecord> grouper1 = new CodecGrouper1<>(codec);
		CodecGrouper1<Integer, TestRecord> grouper2 = new CodecGrouper1<>(codec);
		
		assertEquals(grouper1.hashCode(), grouper2.hashCode());
	}
	
	@Test
	void toStringContainsCodecInfo() {
		ConfiguredCodec<Integer, TestRecord> codec = createConfiguredCodec("test");
		CodecGrouper1<Integer, TestRecord> grouper = new CodecGrouper1<>(codec);
		
		String result = grouper.toString();
		
		assertTrue(result.contains("CodecGrouper1"));
		assertTrue(result.contains("test"));
	}
}
