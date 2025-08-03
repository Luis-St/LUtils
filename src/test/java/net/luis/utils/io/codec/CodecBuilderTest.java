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

package net.luis.utils.io.codec;

import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.*;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static net.luis.utils.io.codec.Codecs.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link CodecBuilder}.<br>
 *
 * @author Luis-St
 */
class CodecBuilderTest {
	
	@Test
	void autoMapCodecWithNullClass() {
		assertThrows(NullPointerException.class, () -> CodecBuilder.autoMapCodec(null));
	}
	
	@Test
	void autoMapCodecWithValidRecord() {
		record SimpleRecord(String name, int age) {}
		
		Codec<SimpleRecord> codec = CodecBuilder.autoMapCodec(SimpleRecord.class);
		assertNotNull(codec);
		
		SimpleRecord record = new SimpleRecord("Test", 25);
		JsonElement encoded = codec.encode(JsonTypeProvider.INSTANCE, record);
		
		assertInstanceOf(JsonObject.class, encoded);
		JsonObject jsonObject = (JsonObject) encoded;
		assertEquals("Test", jsonObject.get("name").getAsJsonPrimitive().getAsString());
		assertEquals(25, jsonObject.get("age").getAsJsonPrimitive().getAsInteger());
		
		SimpleRecord decoded = codec.decode(JsonTypeProvider.INSTANCE, encoded);
		assertEquals(record, decoded);
	}
	
	@Test
	void autoMapCodecWithEnum() {
		enum TestEnum {FIRST, SECOND, THIRD}
		
		Codec<TestEnum> codec = CodecBuilder.autoMapCodec(TestEnum.class);
		assertNotNull(codec);
		
		JsonElement encoded = codec.encode(JsonTypeProvider.INSTANCE, TestEnum.FIRST);
		assertEquals(new JsonPrimitive("FIRST"), encoded);
		
		TestEnum decoded = codec.decode(JsonTypeProvider.INSTANCE, encoded);
		assertEquals(TestEnum.FIRST, decoded);
	}
	
	@Test
	void groupWithSingleCodec() {
		ConfiguredCodec<String, TestObject> nameCodec = STRING.configure("name", TestObject::name);
		
		assertThrows(NullPointerException.class, () -> CodecBuilder.group((ConfiguredCodec<String, TestObject>) null));
		
		assertNotNull(CodecBuilder.group(nameCodec));
		
		Codec<TestObject> codec = CodecBuilder.group(nameCodec).create(TestObject::new);
		assertNotNull(codec);
		
		TestObject obj = new TestObject("John");
		JsonElement encoded = codec.encode(JsonTypeProvider.INSTANCE, obj);
		TestObject decoded = codec.decode(JsonTypeProvider.INSTANCE, encoded);
		assertEquals(obj.name(), decoded.name());
	}
	
	@Test
	void groupWithTwoCodecs() {
		ConfiguredCodec<String, TestObjectWithAge> nameCodec = STRING.configure("name", TestObjectWithAge::name);
		ConfiguredCodec<Integer, TestObjectWithAge> ageCodec = INTEGER.configure("age", TestObjectWithAge::age);
		
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(null, ageCodec));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(nameCodec, null));
		
		assertNotNull(CodecBuilder.group(nameCodec, ageCodec));
		
		Codec<TestObjectWithAge> codec = CodecBuilder.group(nameCodec, ageCodec).create(TestObjectWithAge::new);
		assertNotNull(codec);
		
		TestObjectWithAge obj = new TestObjectWithAge("John", 25);
		JsonElement encoded = codec.encode(JsonTypeProvider.INSTANCE, obj);
		TestObjectWithAge decoded = codec.decode(JsonTypeProvider.INSTANCE, encoded);
		assertEquals(obj.name(), decoded.name());
		assertEquals(obj.age(), decoded.age());
	}
	
	@Test
	void groupWithThreeCodecs() {
		ConfiguredCodec<String, TestObjectWithAgeAndHeight> nameCodec = STRING.configure("name", TestObjectWithAgeAndHeight::name);
		ConfiguredCodec<Integer, TestObjectWithAgeAndHeight> ageCodec = INTEGER.configure("age", TestObjectWithAgeAndHeight::age);
		ConfiguredCodec<Double, TestObjectWithAgeAndHeight> heightCodec = DOUBLE.configure("height", TestObjectWithAgeAndHeight::height);
		
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(null, ageCodec, heightCodec));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(nameCodec, null, heightCodec));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(nameCodec, ageCodec, null));
		
		assertNotNull(CodecBuilder.group(nameCodec, ageCodec, heightCodec));
		
		Codec<TestObjectWithAgeAndHeight> codec = CodecBuilder.group(nameCodec, ageCodec, heightCodec)
			.create(TestObjectWithAgeAndHeight::new);
		assertNotNull(codec);
		
		TestObjectWithAgeAndHeight obj = new TestObjectWithAgeAndHeight("John", 25, 1.80);
		JsonElement encoded = codec.encode(JsonTypeProvider.INSTANCE, obj);
		TestObjectWithAgeAndHeight decoded = codec.decode(JsonTypeProvider.INSTANCE, encoded);
		assertEquals(obj.name(), decoded.name());
		assertEquals(obj.age(), decoded.age());
		assertEquals(obj.height(), decoded.height(), 0.001);
	}
	
	@Test
	void groupWithOptionalField() {
		ConfiguredCodec<Optional<Integer>, TestObjectWithOptional> ageCodec = INTEGER.optional().configure("age", TestObjectWithOptional::age);
		
		Codec<TestObjectWithOptional> codec = CodecBuilder.group(ageCodec).create(TestObjectWithOptional::new);
		
		TestObjectWithOptional objWithValue = new TestObjectWithOptional(Optional.of(25));
		JsonElement encodedWithValue = codec.encode(JsonTypeProvider.INSTANCE, objWithValue);
		TestObjectWithOptional decodedWithValue = codec.decode(JsonTypeProvider.INSTANCE, encodedWithValue);
		assertEquals(objWithValue.age(), decodedWithValue.age());
		
		TestObjectWithOptional objEmpty = new TestObjectWithOptional(Optional.empty());
		JsonElement encodedEmpty = codec.encode(JsonTypeProvider.INSTANCE, objEmpty);
		TestObjectWithOptional decodedEmpty = codec.decode(JsonTypeProvider.INSTANCE, encodedEmpty);
		assertEquals(objEmpty.age(), decodedEmpty.age());
	}
	
	@Test
	void groupSupportsUpToSixteenCodecs() {
		ConfiguredCodec<String, MultiFieldObject> codec1 = STRING.configure("field1", obj -> "field1");
		ConfiguredCodec<String, MultiFieldObject> codec2 = STRING.configure("field2", obj -> "field2");
		ConfiguredCodec<String, MultiFieldObject> codec3 = STRING.configure("field3", obj -> "field3");
		ConfiguredCodec<String, MultiFieldObject> codec4 = STRING.configure("field4", obj -> "field4");
		ConfiguredCodec<String, MultiFieldObject> codec5 = STRING.configure("field5", obj -> "field5");
		ConfiguredCodec<String, MultiFieldObject> codec6 = STRING.configure("field6", obj -> "field6");
		ConfiguredCodec<String, MultiFieldObject> codec7 = STRING.configure("field7", obj -> "field7");
		ConfiguredCodec<String, MultiFieldObject> codec8 = STRING.configure("field8", obj -> "field8");
		ConfiguredCodec<String, MultiFieldObject> codec9 = STRING.configure("field9", obj -> "field9");
		ConfiguredCodec<String, MultiFieldObject> codec10 = STRING.configure("field10", obj -> "field10");
		ConfiguredCodec<String, MultiFieldObject> codec11 = STRING.configure("field11", obj -> "field11");
		ConfiguredCodec<String, MultiFieldObject> codec12 = STRING.configure("field12", obj -> "field12");
		ConfiguredCodec<String, MultiFieldObject> codec13 = STRING.configure("field13", obj -> "field13");
		ConfiguredCodec<String, MultiFieldObject> codec14 = STRING.configure("field14", obj -> "field14");
		ConfiguredCodec<String, MultiFieldObject> codec15 = STRING.configure("field15", obj -> "field15");
		ConfiguredCodec<String, MultiFieldObject> codec16 = STRING.configure("field16", obj -> "field16");
		
		assertNotNull(CodecBuilder.group(
			codec1, codec2, codec3, codec4, codec5, codec6, codec7, codec8,
			codec9, codec10, codec11, codec12, codec13, codec14, codec15, codec16
		));
		
		Codec<MultiFieldObject> codec = CodecBuilder.group(
			codec1, codec2, codec3, codec4, codec5, codec6, codec7, codec8,
			codec9, codec10, codec11, codec12, codec13, codec14, codec15, codec16
		).create((f1, f2, f3, f4, f5, f6, f7, f8, f9, f10, f11, f12, f13, f14, f15, f16) -> new MultiFieldObject());
		
		assertNotNull(codec);
		
		MultiFieldObject obj = new MultiFieldObject();
		JsonElement encoded = codec.encode(JsonTypeProvider.INSTANCE, obj);
		assertInstanceOf(JsonObject.class, encoded);
		JsonObject jsonObject = (JsonObject) encoded;
		assertEquals(16, jsonObject.size());
		
		MultiFieldObject decoded = codec.decode(JsonTypeProvider.INSTANCE, encoded);
		assertNotNull(decoded);
	}
	
	//region Internal
	private record TestObject(@NotNull String name) {}
	
	private record TestObjectWithAge(@NotNull String name, int age) {}
	
	private record TestObjectWithAgeAndHeight(@NotNull String name, int age, double height) {}
	
	private record TestObjectWithOptional(@NotNull Optional<Integer> age) {}
	
	private static class MultiFieldObject {}
	//endregion
}
