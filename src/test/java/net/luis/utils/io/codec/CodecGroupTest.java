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

package net.luis.utils.io.codec;

import net.luis.utils.function.throwable.ThrowableFunction;
import net.luis.utils.io.codec.decoder.DecoderException;
import net.luis.utils.io.codec.encoder.EncoderException;
import net.luis.utils.io.codec.provider.BinaryTypeProvider;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.binary.*;
import net.luis.utils.io.data.json.*;
import net.luis.utils.util.result.Result;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.*;

import static net.luis.utils.io.codec.Codecs.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link CodecGroup}.<br>
 *
 * @author Luis-St
 */
class CodecGroupTest {
	
	private static @NonNull CodecGroup<TestObject> createTestCodecGroup() {
		List<FieldCodec<?, TestObject>> codecs = List.of(
			STRING.fieldOf("name", TestObject::name),
			INTEGER.fieldOf("value", TestObject::value)
		);
		return new CodecGroup<>(codecs, components -> new TestObject((String) components.getFirst(), (Integer) components.get(1)));
	}
	
	@Test
	void constructor() {
		assertThrows(NullPointerException.class, () -> new CodecGroup<>(null, components -> new TestObject("", 0)));
		assertThrows(NullPointerException.class, () -> new CodecGroup<>(Collections.singletonList(null), components -> new TestObject("", 0)));
		assertThrows(NullPointerException.class, () -> new CodecGroup<>(List.of(), null));
		assertDoesNotThrow(() -> new CodecGroup<>(List.of(), components -> new TestObject("", 0)));
	}
	
	@Test
	void encodeNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		CodecGroup<TestObject> codec = createTestCodecGroup();
		TestObject testObject = new TestObject("test", 42);
		
		assertThrows(NullPointerException.class, () -> codec.encode(null, typeProvider.empty(), testObject));
		assertThrows(NullPointerException.class, () -> codec.encode(typeProvider, null, testObject));
	}
	
	@Test
	void encodeWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		CodecGroup<TestObject> codec = createTestCodecGroup();
		
		EncoderException exception = assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), null));
		assertTrue(exception.getMessage().contains("Unable to encode null value"));
	}
	
	@Test
	void encodeSuccess() throws Exception {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		CodecGroup<TestObject> codec = createTestCodecGroup();
		TestObject testObject = new TestObject("hello", 123);
		
		JsonElement element = codec.encode(typeProvider, typeProvider.empty(), testObject);
		assertTrue(element.isJsonObject());
		JsonObject obj = element.getAsJsonObject();
		assertEquals(new JsonPrimitive("hello"), obj.get("name"));
		assertEquals(new JsonPrimitive(123), obj.get("value"));
	}
	
	@Test
	void encodeWithDifferentTypes() throws Exception {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		List<FieldCodec<?, TestObject2>> codecs = List.of(
			STRING.fieldOf("text", TestObject2::text),
			DOUBLE.fieldOf("number", TestObject2::number),
			BOOLEAN.fieldOf("flag", TestObject2::flag)
		);
		CodecGroup<TestObject2> codec = new CodecGroup<>(codecs, components -> new TestObject2((String) components.getFirst(), (Double) components.get(1), (Boolean) components.get(2)));
		
		TestObject2 testObject = new TestObject2("test", 3.14, true);
		JsonElement element = codec.encode(typeProvider, typeProvider.empty(), testObject);
		assertTrue(element.isJsonObject());
		JsonObject obj = element.getAsJsonObject();
		assertEquals(new JsonPrimitive("test"), obj.get("text"));
		assertEquals(new JsonPrimitive(3.14), obj.get("number"));
		assertEquals(new JsonPrimitive(true), obj.get("flag"));
	}
	
	@Test
	void encodeWithEmptyCodecs() throws Exception {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		CodecGroup<TestObject> codec = new CodecGroup<>(List.of(), components -> new TestObject("", 0));
		TestObject testObject = new TestObject("test", 42);
		
		JsonElement element = codec.encode(typeProvider, typeProvider.empty(), testObject);
		assertTrue(element.isJsonObject());
		assertTrue(element.getAsJsonObject().isEmpty());
	}
	
	@Test
	void decodeNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		CodecGroup<TestObject> codec = createTestCodecGroup();
		
		JsonObject testObj = new JsonObject();
		assertThrows(NullPointerException.class, () -> codec.decode(null, testObj, testObj));
	}
	
	@Test
	void decodeWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		CodecGroup<TestObject> codec = createTestCodecGroup();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), null));
		assertTrue(exception.getMessage().contains("Unable to decode null value"));
	}
	
	@Test
	void decodeWithNonObject() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		CodecGroup<TestObject> codec = createTestCodecGroup();
		
		JsonPrimitive notAnObject = new JsonPrimitive("not-an-object");
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decode(typeProvider, notAnObject, notAnObject));
		assertTrue(exception.getMessage().contains("Json element"));
	}
	
	@Test
	void decodeSuccess() throws Exception {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		CodecGroup<TestObject> codec = createTestCodecGroup();
		
		JsonObject jsonObj = new JsonObject();
		jsonObj.add("name", new JsonPrimitive("hello"));
		jsonObj.add("value", new JsonPrimitive(123));
		
		TestObject obj = codec.decode(typeProvider, jsonObj, jsonObj);
		assertEquals("hello", obj.name);
		assertEquals(123, obj.value);
	}
	
	@Test
	void decodeWithDifferentTypes() throws Exception {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		List<FieldCodec<?, TestObject2>> codecs = List.of(
			STRING.fieldOf("text", TestObject2::text),
			DOUBLE.fieldOf("number", TestObject2::number),
			BOOLEAN.fieldOf("flag", TestObject2::flag)
		);
		CodecGroup<TestObject2> codec = new CodecGroup<>(codecs, components -> new TestObject2((String) components.getFirst(), (Double) components.get(1), (Boolean) components.get(2)));
		
		JsonObject jsonObj = new JsonObject();
		jsonObj.add("text", new JsonPrimitive("test"));
		jsonObj.add("number", new JsonPrimitive(3.14));
		jsonObj.add("flag", new JsonPrimitive(true));
		
		TestObject2 obj = codec.decode(typeProvider, jsonObj, jsonObj);
		assertEquals("test", obj.text);
		assertEquals(3.14, obj.number);
		assertTrue(obj.flag);
	}
	
	@Test
	void decodeWithEmptyCodecs() throws Exception {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		CodecGroup<TestObject> codec = new CodecGroup<>(List.of(), components -> new TestObject("default", 0));
		
		TestObject obj = codec.decode(typeProvider, typeProvider.empty(), new JsonObject());
		assertEquals("default", obj.name);
		assertEquals(0, obj.value);
	}
	
	@Test
	void decodeWithFailingCodec() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		FieldCodec<String, TestObject> failingCodec = STRING.validate(s -> Result.error("Always fails")).fieldOf("name", TestObject::name);
		List<FieldCodec<?, TestObject>> codecs = List.of(failingCodec);
		CodecGroup<TestObject> codec = new CodecGroup<>(codecs, components -> new TestObject("", 0));
		
		JsonObject jsonObj = new JsonObject();
		jsonObj.add("name", new JsonPrimitive("test"));
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decode(typeProvider, jsonObj, jsonObj));
		assertTrue(exception.getMessage().contains("Unable to decode component"));
	}
	
	@Test
	void decodeWithMissingField() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		CodecGroup<TestObject> codec = createTestCodecGroup();
		
		JsonObject jsonObj = new JsonObject();
		jsonObj.add("name", new JsonPrimitive("test"));
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decode(typeProvider, jsonObj, jsonObj));
		assertTrue(exception.getMessage().contains("Unable to decode component"));
	}
	
	@Test
	void decodeWithFactoryException() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		List<FieldCodec<?, TestObject>> codecs = List.of(
			STRING.fieldOf("name", TestObject::name),
			INTEGER.fieldOf("value", TestObject::value)
		);
		CodecGroup<TestObject> codec = new CodecGroup<>(codecs, components -> {
			throw new DecoderException("Factory error");
		});
		
		JsonObject jsonObj = new JsonObject();
		jsonObj.add("name", new JsonPrimitive("test"));
		jsonObj.add("value", new JsonPrimitive(42));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, jsonObj, jsonObj));
	}
	
	@Test
	void roundTripEncodeAndDecode() throws Exception {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		CodecGroup<TestObject> codec = createTestCodecGroup();
		TestObject original = new TestObject("roundtrip", 999);
		
		JsonElement encoded = codec.encode(typeProvider, typeProvider.empty(), original);
		TestObject decoded = codec.decode(typeProvider, encoded, encoded);
		
		assertEquals(original.name, decoded.name);
		assertEquals(original.value, decoded.value);
	}
	
	@Test
	void roundTripWithComplexTypes() throws Exception {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		List<FieldCodec<?, TestObject2>> codecs = List.of(
			STRING.fieldOf("text", TestObject2::text),
			DOUBLE.fieldOf("number", TestObject2::number),
			BOOLEAN.fieldOf("flag", TestObject2::flag)
		);
		CodecGroup<TestObject2> codec = new CodecGroup<>(codecs, components -> new TestObject2((String) components.getFirst(), (Double) components.get(1), (Boolean) components.get(2)));
		
		TestObject2 original = new TestObject2("complex", 2.71, false);
		
		JsonElement encoded = codec.encode(typeProvider, typeProvider.empty(), original);
		TestObject2 decoded = codec.decode(typeProvider, encoded, encoded);
		
		assertEquals(original.text, decoded.text);
		assertEquals(original.number, decoded.number);
		assertEquals(original.flag, decoded.flag);
	}
	
	@Test
	void toStringRepresentation() {
		CodecGroup<TestObject> codec = createTestCodecGroup();
		String result = codec.toString();
		
		assertTrue(result.startsWith("GroupCodec["));
		assertTrue(result.endsWith("]"));
		assertTrue(result.contains("name"));
		assertTrue(result.contains("value"));
	}
	
	@Test
	void toStringWithEmptyCodecs() {
		CodecGroup<TestObject> codec = new CodecGroup<>(List.of(), components -> new TestObject("", 0));
		String result = codec.toString();
		
		assertEquals("GroupCodec[]", result);
	}
	
	@Test
	void toStringWithSingleCodec() {
		List<FieldCodec<?, TestObject>> codecs = List.of(
			STRING.fieldOf("name", TestObject::name)
		);
		CodecGroup<TestObject> codec = new CodecGroup<>(codecs, components -> new TestObject("", 0));
		String result = codec.toString();
		
		assertTrue(result.startsWith("GroupCodec["));
		assertTrue(result.endsWith("]"));
	}
	
	@Test
	void constructAssignsIndicesToCodecs() throws Exception {
		BinaryTypeProvider typeProvider = BinaryTypeProvider.INSTANCE;
		CodecGroup<TestObject> codec = createTestCodecGroup();
		
		BinaryElement element = codec.encode(typeProvider, typeProvider.empty(), new TestObject("hello", 123));
		
		BinaryStruct struct = element.getAsBinaryStruct();
		assertEquals(2, struct.size());
		assertEquals("hello", struct.getAsString(0));
		assertEquals(123, struct.getAsInteger(1));
	}
	
	@Test
	void constructWithEmptyCodecsSkipsIndexing() throws Exception {
		BinaryTypeProvider typeProvider = BinaryTypeProvider.INSTANCE;
		CodecGroup<TestObject> codec = assertDoesNotThrow(() -> new CodecGroup<TestObject>(List.of(), components -> new TestObject("", 0)));
		
		BinaryElement element = codec.encode(typeProvider, typeProvider.empty(), new TestObject("test", 42));
		assertEquals(0, element.getAsBinaryStruct().size());
	}
	
	@Test
	void constructDoesNotMutateSourceCodecs() {
		BinaryTypeProvider typeProvider = BinaryTypeProvider.INSTANCE;
		FieldCodec<String, TestObject> original = STRING.fieldOf("name", TestObject::name);
		
		new CodecGroup<>(List.of(original), components -> new TestObject((String) components.getFirst(), 0));
		
		BinaryStruct struct = new BinaryStruct(1);
		EncoderException exception = assertThrows(EncoderException.class, () -> original.encode(typeProvider, struct, new TestObject("test", 0)));
		assertTrue(exception.getMessage().contains("not indexed"));
	}
	
	@Test
	void constructCopiesCodecList() throws Exception {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		List<FieldCodec<?, TestObject>> codecs = new ArrayList<>();
		codecs.add(STRING.fieldOf("name", TestObject::name));
		codecs.add(INTEGER.fieldOf("value", TestObject::value));
		
		CodecGroup<TestObject> codec = new CodecGroup<>(codecs, components -> new TestObject((String) components.getFirst(), (Integer) components.get(1)));
		codecs.clear();
		
		JsonObject obj = codec.encode(typeProvider, typeProvider.empty(), new TestObject("hello", 123)).getAsJsonObject();
		assertEquals(new JsonPrimitive("hello"), obj.get("name"));
		assertEquals(new JsonPrimitive(123), obj.get("value"));
	}
	
	@Test
	void constructWithNullCodecAtSecondPosition() {
		List<FieldCodec<?, TestObject>> codecs = Arrays.asList(STRING.fieldOf("name", TestObject::name), null);
		
		NullPointerException exception = assertThrows(NullPointerException.class, () -> new CodecGroup<>(codecs, components -> new TestObject("", 0)));
		assertTrue(exception.getMessage().contains("component 1"));
	}
	
	@Test
	void encodeWithNonStructCurrentValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		CodecGroup<TestObject> codec = createTestCodecGroup();
		JsonPrimitive current = new JsonPrimitive("not-an-object");
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, current, new TestObject("test", 42)));
	}
	
	@Test
	void decodeWithNonStructOnBinaryProvider() {
		BinaryTypeProvider typeProvider = BinaryTypeProvider.INSTANCE;
		CodecGroup<TestObject> codec = createTestCodecGroup();
		BinaryArray array = new BinaryArray();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decode(typeProvider, array, array));
		assertTrue(exception.getMessage().contains("not a binary struct"));
	}
	
	@Test
	void decodeWithNullValueOnBinaryProvider() {
		BinaryTypeProvider typeProvider = BinaryTypeProvider.INSTANCE;
		CodecGroup<TestObject> codec = createTestCodecGroup();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), null));
		assertTrue(exception.getMessage().contains("Unable to decode null value"));
	}
	
	@Test
	void encodeWithFailingCodec() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		FieldCodec<String, TestObject> failingCodec = STRING.validate(s -> Result.error("Always fails")).fieldOf("name", TestObject::name);
		CodecGroup<TestObject> codec = new CodecGroup<>(List.of(failingCodec), components -> new TestObject("", 0));
		
		TestObject testObject = new TestObject("test", 42);
		EncoderException exception = assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), testObject));
		
		assertTrue(exception.getMessage().startsWith("Unable to encode component of '"));
		assertTrue(exception.getMessage().contains(testObject.toString()));
		assertInstanceOf(EncoderException.class, exception.getCause());
	}
	
	@Test
	void encodeCreatesStructSizedToCodecCount() throws Exception {
		BinaryTypeProvider typeProvider = BinaryTypeProvider.INSTANCE;
		List<FieldCodec<?, TestObject2>> codecs = List.of(
			STRING.fieldOf("text", TestObject2::text),
			DOUBLE.fieldOf("number", TestObject2::number),
			BOOLEAN.fieldOf("flag", TestObject2::flag)
		);
		CodecGroup<TestObject2> codec = new CodecGroup<>(codecs, components -> new TestObject2((String) components.getFirst(), (Double) components.get(1), (Boolean) components.get(2)));
		
		BinaryElement element = codec.encode(typeProvider, typeProvider.empty(), new TestObject2("test", 3.14, true));
		assertEquals(3, element.getAsBinaryStruct().size());
	}
	
	@Test
	void encodeWithEmptyCodecsCreatesEmptyStruct() throws Exception {
		CodecGroup<TestObject> codec = new CodecGroup<>(List.of(), components -> new TestObject("", 0));
		TestObject testObject = new TestObject("test", 42);
		
		BinaryTypeProvider binaryProvider = BinaryTypeProvider.INSTANCE;
		assertEquals(0, codec.encode(binaryProvider, binaryProvider.empty(), testObject).getAsBinaryStruct().size());
		
		JsonTypeProvider jsonProvider = JsonTypeProvider.INSTANCE;
		assertTrue(codec.encode(jsonProvider, jsonProvider.empty(), testObject).getAsJsonObject().isEmpty());
	}
	
	@Test
	void encodeMergesIntoExistingStruct() throws Exception {
		BinaryTypeProvider typeProvider = BinaryTypeProvider.INSTANCE;
		CodecGroup<TestObject> codec = createTestCodecGroup();
		
		BinaryStruct current = new BinaryStruct(3);
		current.set(2, "extra", 7);
		
		BinaryStruct result = codec.encode(typeProvider, current, new TestObject("hello", 123)).getAsBinaryStruct();
		
		assertEquals(3, result.size());
		assertEquals("hello", result.getAsString(0));
		assertEquals(123, result.getAsInteger(1));
		assertEquals(7, result.getAsInteger(2));
	}
	
	@Test
	void decodeValidatesStructOnMapProvider() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		
		JsonObject jsonObj = new JsonObject();
		jsonObj.add("name", new JsonPrimitive("hello"));
		jsonObj.add("value", new JsonPrimitive(123));
		
		assertDoesNotThrow(() -> typeProvider.validateStruct(jsonObj, DecoderException::new));
		assertDoesNotThrow(() -> createTestCodecGroup().decode(typeProvider, jsonObj, jsonObj));
	}
	
	@Test
	void decodeValidatesStructOnBinaryProvider() throws Exception {
		BinaryTypeProvider typeProvider = BinaryTypeProvider.INSTANCE;
		CodecGroup<TestObject> codec = createTestCodecGroup();
		
		BinaryElement encoded = codec.encode(typeProvider, typeProvider.empty(), new TestObject("hello", 123));
		assertDoesNotThrow(() -> typeProvider.validateStruct(encoded, DecoderException::new));
		
		BinaryMap map = new BinaryMap();
		map.add("name", "hello");
		map.add("value", 123);
		assertDoesNotThrow(() -> typeProvider.validateStruct(map, DecoderException::new));
	}
	
	@Test
	void encodeAndDecodeWithBinaryProvider() throws Exception {
		BinaryTypeProvider typeProvider = BinaryTypeProvider.INSTANCE;
		CodecGroup<TestObject> codec = createTestCodecGroup();
		TestObject original = new TestObject("hello", 123);
		
		BinaryElement encoded = codec.encode(typeProvider, typeProvider.empty(), original);
		TestObject decoded = codec.decode(typeProvider, encoded, encoded);
		
		assertEquals(original, decoded);
	}
	
	@Test
	void encodeWithSingleCodecOnBinaryProvider() throws Exception {
		BinaryTypeProvider typeProvider = BinaryTypeProvider.INSTANCE;
		CodecGroup<TestObject> codec = new CodecGroup<>(List.of(STRING.fieldOf("name", TestObject::name)), components -> new TestObject((String) components.getFirst(), 0));
		
		BinaryStruct struct = codec.encode(typeProvider, typeProvider.empty(), new TestObject("hello", 0)).getAsBinaryStruct();
		
		assertEquals(1, struct.size());
		assertEquals("hello", struct.getAsString(0));
	}
	
	@Test
	void decodeWithEmptyCodecsOnBinaryProvider() throws Exception {
		BinaryTypeProvider typeProvider = BinaryTypeProvider.INSTANCE;
		CodecGroup<TestObject> codec = new CodecGroup<>(List.of(), components -> new TestObject("default", 0));
		
		BinaryStruct struct = new BinaryStruct(0);
		TestObject decoded = codec.decode(typeProvider, struct, struct);
		
		assertEquals("default", decoded.name);
		assertEquals(0, decoded.value);
	}
	
	@Test
	void equalsAndHashCode() {
		ThrowableFunction<List<Object>, TestObject, DecoderException> factory = components -> new TestObject((String) components.getFirst(), (Integer) components.get(1));
		
		FieldCodec<String, TestObject> nameCodec = STRING.fieldOf("name", TestObject::name);
		FieldCodec<Integer, TestObject> valueCodec = INTEGER.fieldOf("value", TestObject::value);
		
		List<FieldCodec<?, TestObject>> codecs = List.of(nameCodec, valueCodec);
		List<FieldCodec<?, TestObject>> sameCodecs = List.of(nameCodec, valueCodec);
		List<FieldCodec<?, TestObject>> otherCodecs = List.of(
			STRING.fieldOf("other", TestObject::name)
		);
		
		CodecGroup<TestObject> codec = new CodecGroup<>(codecs, factory);
		CodecGroup<TestObject> same = new CodecGroup<>(sameCodecs, factory);
		CodecGroup<TestObject> differentCodecs = new CodecGroup<>(otherCodecs, factory);
		CodecGroup<TestObject> differentFactory = new CodecGroup<>(codecs, components -> new TestObject("", 0));
		
		assertEquals(codec, same);
		assertEquals(codec.hashCode(), same.hashCode());
		
		assertNotEquals(codec, differentCodecs);
		assertNotEquals(codec, differentFactory);
		assertNotEquals(null, codec);
		assertNotEquals(codec, "text");
	}
	
	@Test
	void roundTripThroughBinaryWriterAndReader() throws Exception {
		BinaryTypeProvider typeProvider = BinaryTypeProvider.INSTANCE;
		List<FieldCodec<?, TestObject2>> codecs = List.of(
			STRING.fieldOf("text", TestObject2::text),
			DOUBLE.fieldOf("number", TestObject2::number),
			BOOLEAN.fieldOf("flag", TestObject2::flag)
		);
		CodecGroup<TestObject2> codec = new CodecGroup<>(codecs, components -> new TestObject2((String) components.getFirst(), (Double) components.get(1), (Boolean) components.get(2)));
		
		TestObject2 original = new TestObject2("complex", 2.71, false);
		
		byte[] data = BinaryWriter.toByteArray(codec.encode(typeProvider, typeProvider.empty(), original));
		assertFalse(new String(data, StandardCharsets.UTF_8).contains("number"));
		
		BinaryElement read = BinaryReader.fromByteArray(data);
		assertEquals(original, codec.decode(typeProvider, read, read));
	}
	
	@Test
	void roundTripWithNestedGroups() throws Exception {
		CodecGroup<TestObject> inner = createTestCodecGroup();
		List<FieldCodec<?, TestNested>> codecs = List.of(
			inner.fieldOf("inner", TestNested::inner),
			INTEGER.fieldOf("id", TestNested::id)
		);
		CodecGroup<TestNested> codec = new CodecGroup<>(codecs, components -> new TestNested((TestObject) components.getFirst(), (Integer) components.get(1)));
		
		TestNested original = new TestNested(new TestObject("hello", 123), 7);
		
		JsonTypeProvider jsonProvider = JsonTypeProvider.INSTANCE;
		JsonElement jsonEncoded = codec.encode(jsonProvider, jsonProvider.empty(), original);
		assertEquals(original, codec.decode(jsonProvider, jsonEncoded, jsonEncoded));
		
		BinaryTypeProvider binaryProvider = BinaryTypeProvider.INSTANCE;
		BinaryElement binaryEncoded = codec.encode(binaryProvider, binaryProvider.empty(), original);
		assertEquals(original, codec.decode(binaryProvider, binaryEncoded, binaryEncoded));
	}
	
	@Test
	void decodeByPositionIgnoresFieldOrderOnBinary() throws Exception {
		BinaryTypeProvider typeProvider = BinaryTypeProvider.INSTANCE;
		CodecGroup<TestObject> codec = createTestCodecGroup();
		
		BinaryStruct struct = new BinaryStruct(2);
		struct.set(0, "hello");
		struct.set(1, 123);
		
		assertFalse(struct.hasNames());
		assertEquals(new TestObject("hello", 123), codec.decode(typeProvider, struct, struct));
	}
	
	@Test
	void groupsAreInterchangeableAcrossProviders() throws Exception {
		CodecGroup<TestObject> codec = createTestCodecGroup();
		TestObject original = new TestObject("hello", 123);
		
		JsonTypeProvider jsonProvider = JsonTypeProvider.INSTANCE;
		JsonElement jsonEncoded = codec.encode(jsonProvider, jsonProvider.empty(), original);
		assertEquals(original, codec.decode(jsonProvider, jsonEncoded, jsonEncoded));
		
		BinaryTypeProvider binaryProvider = BinaryTypeProvider.INSTANCE;
		BinaryElement binaryEncoded = codec.encode(binaryProvider, binaryProvider.empty(), original);
		assertEquals(original, codec.decode(binaryProvider, binaryEncoded, binaryEncoded));
	}
	
	private record TestObject(@NonNull String name, int value) {}
	
	private record TestObject2(@NonNull String text, double number, boolean flag) {}
	
	private record TestNested(@NonNull TestObject inner, int id) {}
}
