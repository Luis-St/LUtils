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
import net.luis.utils.util.result.Result;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static net.luis.utils.io.codec.Codecs.*;
import static net.luis.utils.util.result.ResultingFunction.*;
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
		return new CodecGroup<>(codecs, direct(components -> new TestObject((String) components.getFirst(), (Integer) components.get(1))));
	}
	
	@Test
	void constructor() {
		assertThrows(NullPointerException.class, () -> new CodecGroup<>(null, direct(components -> new TestObject("", 0))));
		assertThrows(NullPointerException.class, () -> new CodecGroup<>(List.of(), null));
		assertDoesNotThrow(() -> new CodecGroup<>(List.of(), direct(components -> new TestObject("", 0))));
	}
	
	@Test
	void encodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		CodecGroup<TestObject> codec = createTestCodecGroup();
		TestObject testObject = new TestObject("test", 42);
		
		assertThrows(NullPointerException.class, () -> codec.encodeStart(null, typeProvider.empty(), testObject));
		assertThrows(NullPointerException.class, () -> codec.encodeStart(typeProvider, null, testObject));
	}
	
	@Test
	void encodeStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		CodecGroup<TestObject> codec = createTestCodecGroup();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), null);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to encode null value"));
	}
	
	@Test
	void encodeStartSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		CodecGroup<TestObject> codec = createTestCodecGroup();
		TestObject testObject = new TestObject("hello", 123);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), testObject);
		assertTrue(result.isSuccess());
		
		JsonElement element = result.resultOrThrow();
		assertTrue(element.isJsonObject());
		JsonObject obj = element.getAsJsonObject();
		assertEquals(new JsonPrimitive("hello"), obj.get("name"));
		assertEquals(new JsonPrimitive(123), obj.get("value"));
	}
	
	@Test
	void encodeStartWithDifferentTypes() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		List<FieldCodec<?, TestObject2>> codecs = List.of(
			STRING.fieldOf("text", TestObject2::text),
			DOUBLE.fieldOf("number", TestObject2::number),
			BOOLEAN.fieldOf("flag", TestObject2::flag)
		);
		CodecGroup<TestObject2> codec = new CodecGroup<>(codecs, direct(components -> new TestObject2((String) components.getFirst(), (Double) components.get(1), (Boolean) components.get(2))));
		
		TestObject2 testObject = new TestObject2("test", 3.14, true);
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), testObject);
		assertTrue(result.isSuccess());
		
		JsonElement element = result.resultOrThrow();
		assertTrue(element.isJsonObject());
		JsonObject obj = element.getAsJsonObject();
		assertEquals(new JsonPrimitive("test"), obj.get("text"));
		assertEquals(new JsonPrimitive(3.14), obj.get("number"));
		assertEquals(new JsonPrimitive(true), obj.get("flag"));
	}
	
	@Test
	void encodeStartWithEmptyCodecs() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		CodecGroup<TestObject> codec = new CodecGroup<>(List.of(), direct(components -> new TestObject("", 0)));
		TestObject testObject = new TestObject("test", 42);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), testObject);
		assertTrue(result.isSuccess());
		
		JsonElement element = result.resultOrThrow();
		assertTrue(element.isJsonObject());
		assertTrue(element.getAsJsonObject().isEmpty());
	}
	
	@Test
	void encodeStartWithNullCodec() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		List<FieldCodec<?, TestObject>> codecs = Arrays.asList(
			STRING.fieldOf("name", TestObject::name),
			null
		);
		CodecGroup<TestObject> codec = new CodecGroup<>(codecs, direct(components -> new TestObject("", 0)));
		TestObject testObject = new TestObject("test", 42);
		
		assertThrows(NullPointerException.class, () -> codec.encodeStart(typeProvider, typeProvider.empty(), testObject));
	}
	
	@Test
	void decodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		CodecGroup<TestObject> codec = createTestCodecGroup();
		
		JsonObject testObj = new JsonObject();
		assertThrows(NullPointerException.class, () -> codec.decodeStart(null, testObj, testObj));
	}
	
	@Test
	void decodeStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		CodecGroup<TestObject> codec = createTestCodecGroup();
		
		Result<TestObject> result = codec.decodeStart(typeProvider, typeProvider.empty(), null);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode null value"));
	}
	
	@Test
	void decodeStartWithNonObject() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		CodecGroup<TestObject> codec = createTestCodecGroup();
		
		JsonPrimitive notAnObject = new JsonPrimitive("not-an-object");
		Result<TestObject> result = codec.decodeStart(typeProvider, notAnObject, notAnObject);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode"));
	}
	
	@Test
	void decodeStartSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		CodecGroup<TestObject> codec = createTestCodecGroup();
		
		JsonObject jsonObj = new JsonObject();
		jsonObj.add("name", new JsonPrimitive("hello"));
		jsonObj.add("value", new JsonPrimitive(123));
		
		Result<TestObject> result = codec.decodeStart(typeProvider, jsonObj, jsonObj);
		assertTrue(result.isSuccess());
		
		TestObject obj = result.resultOrThrow();
		assertEquals("hello", obj.name);
		assertEquals(123, obj.value);
	}
	
	@Test
	void decodeStartWithDifferentTypes() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		List<FieldCodec<?, TestObject2>> codecs = List.of(
			STRING.fieldOf("text", TestObject2::text),
			DOUBLE.fieldOf("number", TestObject2::number),
			BOOLEAN.fieldOf("flag", TestObject2::flag)
		);
		CodecGroup<TestObject2> codec = new CodecGroup<>(codecs, direct(components -> new TestObject2((String) components.getFirst(), (Double) components.get(1), (Boolean) components.get(2))));
		
		JsonObject jsonObj = new JsonObject();
		jsonObj.add("text", new JsonPrimitive("test"));
		jsonObj.add("number", new JsonPrimitive(3.14));
		jsonObj.add("flag", new JsonPrimitive(true));
		
		Result<TestObject2> result = codec.decodeStart(typeProvider, jsonObj, jsonObj);
		assertTrue(result.isSuccess());
		
		TestObject2 obj = result.resultOrThrow();
		assertEquals("test", obj.text);
		assertEquals(3.14, obj.number);
		assertTrue(obj.flag);
	}
	
	@Test
	void decodeStartWithEmptyCodecs() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		CodecGroup<TestObject> codec = new CodecGroup<>(List.of(), direct(components -> new TestObject("default", 0)));
		
		Result<TestObject> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonObject());
		assertTrue(result.isSuccess());
		
		TestObject obj = result.resultOrThrow();
		assertEquals("default", obj.name);
		assertEquals(0, obj.value);
	}
	
	@Test
	void decodeStartWithNullCodec() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		List<FieldCodec<?, TestObject>> codecs = Arrays.asList(
			STRING.fieldOf("name", TestObject::name),
			null
		);
		CodecGroup<TestObject> codec = new CodecGroup<>(codecs, direct(components -> new TestObject("", 0)));
		
		JsonObject jsonObj = new JsonObject();
		jsonObj.add("name", new JsonPrimitive("test"));
		
		assertThrows(NullPointerException.class, () -> codec.decodeStart(typeProvider, jsonObj, jsonObj));
	}
	
	@Test
	void decodeStartWithFailingCodec() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		FieldCodec<String, TestObject> failingCodec = STRING.validate(s -> Result.error("Always fails")).fieldOf("name", TestObject::name);
		List<FieldCodec<?, TestObject>> codecs = List.of(failingCodec);
		CodecGroup<TestObject> codec = new CodecGroup<>(codecs, direct(components -> new TestObject("", 0)));
		
		JsonObject jsonObj = new JsonObject();
		jsonObj.add("name", new JsonPrimitive("test"));
		
		Result<TestObject> result = codec.decodeStart(typeProvider, jsonObj, jsonObj);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode component"));
	}
	
	@Test
	void decodeStartWithMissingField() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		CodecGroup<TestObject> codec = createTestCodecGroup();
		
		JsonObject jsonObj = new JsonObject();
		jsonObj.add("name", new JsonPrimitive("test"));
		
		Result<TestObject> result = codec.decodeStart(typeProvider, jsonObj, jsonObj);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode component"));
	}
	
	@Test
	void decodeStartWithFactoryException() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		List<FieldCodec<?, TestObject>> codecs = List.of(
			STRING.fieldOf("name", TestObject::name),
			INTEGER.fieldOf("value", TestObject::value)
		);
		Function<List<Object>, TestObject> throwingFactory = components -> {
			throw new RuntimeException("Factory error");
		};
		CodecGroup<TestObject> codec = new CodecGroup<>(codecs, direct(throwingFactory));
		
		JsonObject jsonObj = new JsonObject();
		jsonObj.add("name", new JsonPrimitive("test"));
		jsonObj.add("value", new JsonPrimitive(42));
		
		assertThrows(RuntimeException.class, () -> codec.decodeStart(typeProvider, jsonObj, jsonObj));
	}
	
	@Test
	void roundTripEncodeAndDecode() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		CodecGroup<TestObject> codec = createTestCodecGroup();
		TestObject original = new TestObject("roundtrip", 999);
		
		Result<JsonElement> encodeResult = codec.encodeStart(typeProvider, typeProvider.empty(), original);
		assertTrue(encodeResult.isSuccess());
		
		Result<TestObject> decodeResult = codec.decodeStart(typeProvider, encodeResult.resultOrThrow(), encodeResult.resultOrThrow());
		assertTrue(decodeResult.isSuccess());
		
		TestObject decoded = decodeResult.resultOrThrow();
		assertEquals(original.name, decoded.name);
		assertEquals(original.value, decoded.value);
	}
	
	@Test
	void roundTripWithComplexTypes() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		List<FieldCodec<?, TestObject2>> codecs = List.of(
			STRING.fieldOf("text", TestObject2::text),
			DOUBLE.fieldOf("number", TestObject2::number),
			BOOLEAN.fieldOf("flag", TestObject2::flag)
		);
		CodecGroup<TestObject2> codec = new CodecGroup<>(codecs, direct(components -> new TestObject2((String) components.getFirst(), (Double) components.get(1), (Boolean) components.get(2))));
		
		TestObject2 original = new TestObject2("complex", 2.71, false);
		
		Result<JsonElement> encodeResult = codec.encodeStart(typeProvider, typeProvider.empty(), original);
		assertTrue(encodeResult.isSuccess());
		
		Result<TestObject2> decodeResult = codec.decodeStart(typeProvider, encodeResult.resultOrThrow(), encodeResult.resultOrThrow());
		assertTrue(decodeResult.isSuccess());
		
		TestObject2 decoded = decodeResult.resultOrThrow();
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
		CodecGroup<TestObject> codec = new CodecGroup<>(List.of(), direct(components -> new TestObject("", 0)));
		String result = codec.toString();
		
		assertEquals("GroupCodec[]", result);
	}
	
	@Test
	void toStringWithSingleCodec() {
		List<FieldCodec<?, TestObject>> codecs = List.of(
			STRING.fieldOf("name", TestObject::name)
		);
		CodecGroup<TestObject> codec = new CodecGroup<>(codecs, direct(components -> new TestObject("", 0)));
		String result = codec.toString();
		
		assertTrue(result.startsWith("GroupCodec["));
		assertTrue(result.endsWith("]"));
	}
	
	private record TestObject(@NonNull String name, int value) {}
	
	private record TestObject2(@NonNull String text, double number, boolean flag) {}
}
