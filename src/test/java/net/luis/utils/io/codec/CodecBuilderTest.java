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
import org.jspecify.annotations.NonNull;
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
		assertThrows(NullPointerException.class, () -> CodecBuilder.of((Class<? extends Object>) null));
	}
	
	@Test
	void autoMapCodecWithValidRecord() {
		record SimpleRecord(String name, int age) {}
		
		Codec<SimpleRecord> codec = CodecBuilder.of(SimpleRecord.class);
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
		
		Codec<TestEnum> codec = CodecBuilder.of(TestEnum.class);
		assertNotNull(codec);
		
		JsonElement encoded = codec.encode(JsonTypeProvider.INSTANCE, TestEnum.FIRST);
		assertEquals(new JsonPrimitive("FIRST"), encoded);
		
		TestEnum decoded = codec.decode(JsonTypeProvider.INSTANCE, encoded);
		assertEquals(TestEnum.FIRST, decoded);
	}
	
	@Test
	void ofWithSingleCodec() {
		FieldCodec<String, TestObject> nameCodec = STRING.fieldOf("name", TestObject::name);
		
		assertThrows(NullPointerException.class, () -> CodecBuilder.of((FieldCodec<String, TestObject>) null));
		
		assertNotNull(CodecBuilder.of(nameCodec));
		
		Codec<TestObject> codec = CodecBuilder.of(nameCodec).create(TestObject::new);
		assertNotNull(codec);
		
		TestObject obj = new TestObject("John");
		JsonElement encoded = codec.encode(JsonTypeProvider.INSTANCE, obj);
		TestObject decoded = codec.decode(JsonTypeProvider.INSTANCE, encoded);
		assertEquals(obj.name(), decoded.name());
	}
	
	@Test
	void ofWithTwoCodecs() {
		FieldCodec<String, TestObjectWithAge> nameCodec = STRING.fieldOf("name", TestObjectWithAge::name);
		FieldCodec<Integer, TestObjectWithAge> ageCodec = INTEGER.fieldOf("age", TestObjectWithAge::age);
		
		assertThrows(NullPointerException.class, () -> CodecBuilder.of(null, ageCodec));
		assertThrows(NullPointerException.class, () -> CodecBuilder.of(nameCodec, null));
		
		assertNotNull(CodecBuilder.of(nameCodec, ageCodec));
		
		Codec<TestObjectWithAge> codec = CodecBuilder.of(nameCodec, ageCodec).create(TestObjectWithAge::new);
		assertNotNull(codec);
		
		TestObjectWithAge obj = new TestObjectWithAge("John", 25);
		JsonElement encoded = codec.encode(JsonTypeProvider.INSTANCE, obj);
		TestObjectWithAge decoded = codec.decode(JsonTypeProvider.INSTANCE, encoded);
		assertEquals(obj.name(), decoded.name());
		assertEquals(obj.age(), decoded.age());
	}
	
	@Test
	void ofWithThreeCodecs() {
		FieldCodec<String, TestObjectWithAgeAndHeight> nameCodec = STRING.fieldOf("name", TestObjectWithAgeAndHeight::name);
		FieldCodec<Integer, TestObjectWithAgeAndHeight> ageCodec = INTEGER.fieldOf("age", TestObjectWithAgeAndHeight::age);
		FieldCodec<Double, TestObjectWithAgeAndHeight> heightCodec = DOUBLE.fieldOf("height", TestObjectWithAgeAndHeight::height);
		
		assertThrows(NullPointerException.class, () -> CodecBuilder.of(null, ageCodec, heightCodec));
		assertThrows(NullPointerException.class, () -> CodecBuilder.of(nameCodec, null, heightCodec));
		assertThrows(NullPointerException.class, () -> CodecBuilder.of(nameCodec, ageCodec, null));
		
		assertNotNull(CodecBuilder.of(nameCodec, ageCodec, heightCodec));
		
		Codec<TestObjectWithAgeAndHeight> codec = CodecBuilder.of(nameCodec, ageCodec, heightCodec)
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
	void ofWithOptionalField() {
		FieldCodec<Optional<Integer>, TestObjectWithOptional> ageCodec = INTEGER.optional().fieldOf("age", TestObjectWithOptional::age);
		
		Codec<TestObjectWithOptional> codec = CodecBuilder.of(ageCodec).create(TestObjectWithOptional::new);
		
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
	void ofSupportsUpToSixteenCodecs() {
		FieldCodec<String, MultiFieldObject> codec1 = STRING.fieldOf("field1", obj -> "field1");
		FieldCodec<String, MultiFieldObject> codec2 = STRING.fieldOf("field2", obj -> "field2");
		FieldCodec<String, MultiFieldObject> codec3 = STRING.fieldOf("field3", obj -> "field3");
		FieldCodec<String, MultiFieldObject> codec4 = STRING.fieldOf("field4", obj -> "field4");
		FieldCodec<String, MultiFieldObject> codec5 = STRING.fieldOf("field5", obj -> "field5");
		FieldCodec<String, MultiFieldObject> codec6 = STRING.fieldOf("field6", obj -> "field6");
		FieldCodec<String, MultiFieldObject> codec7 = STRING.fieldOf("field7", obj -> "field7");
		FieldCodec<String, MultiFieldObject> codec8 = STRING.fieldOf("field8", obj -> "field8");
		FieldCodec<String, MultiFieldObject> codec9 = STRING.fieldOf("field9", obj -> "field9");
		FieldCodec<String, MultiFieldObject> codec10 = STRING.fieldOf("field10", obj -> "field10");
		FieldCodec<String, MultiFieldObject> codec11 = STRING.fieldOf("field11", obj -> "field11");
		FieldCodec<String, MultiFieldObject> codec12 = STRING.fieldOf("field12", obj -> "field12");
		FieldCodec<String, MultiFieldObject> codec13 = STRING.fieldOf("field13", obj -> "field13");
		FieldCodec<String, MultiFieldObject> codec14 = STRING.fieldOf("field14", obj -> "field14");
		FieldCodec<String, MultiFieldObject> codec15 = STRING.fieldOf("field15", obj -> "field15");
		FieldCodec<String, MultiFieldObject> codec16 = STRING.fieldOf("field16", obj -> "field16");
		
		assertNotNull(CodecBuilder.of(
			codec1, codec2, codec3, codec4, codec5, codec6, codec7, codec8,
			codec9, codec10, codec11, codec12, codec13, codec14, codec15, codec16
		));
		
		Codec<MultiFieldObject> codec = CodecBuilder.of(
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
	private record TestObject(@NonNull String name) {}
	
	private record TestObjectWithAge(@NonNull String name, int age) {}
	
	private record TestObjectWithAgeAndHeight(@NonNull String name, int age, double height) {}
	
	private record TestObjectWithOptional(@NonNull Optional<Integer> age) {}
	
	private static class MultiFieldObject {}
	//endregion
}
