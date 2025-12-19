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

import net.luis.utils.io.codec.function.CodecBuilderFunction;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.*;
import net.luis.utils.util.result.Result;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Function;

import static net.luis.utils.io.codec.Codecs.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link CodecCreator}.<br>
 *
 * @author Luis-St
 */
class CodecCreatorTest {
	
	@Test
	void constructor() {
		assertThrows(NullPointerException.class, () -> new CodecCreator<>(null));
		assertDoesNotThrow(() -> new CodecCreator<>(List.of()));
	}
	
	@Test
	void createWithNullFunction() {
		CodecCreator<TestObject, TestFunction> creator = new CodecCreator<>(List.of(
			STRING.fieldOf("name", TestObject::name),
			INTEGER.fieldOf("value", TestObject::value)
		));
		
		assertThrows(NullPointerException.class, () -> creator.create(null));
	}
	
	@Test
	void createSuccess() {
		CodecCreator<TestObject, TestFunction> creator = new CodecCreator<>(List.of(
			STRING.fieldOf("name", TestObject::name),
			INTEGER.fieldOf("value", TestObject::value)
		));
		TestFunction function = new TestFunction();
		
		Codec<TestObject> codec = creator.create(function);
		assertNotNull(codec);
		assertInstanceOf(CodecGroup.class, codec);
	}
	
	@Test
	void createWithEmptyCodecs() {
		CodecCreator<TestObject, TestFunction> creator = new CodecCreator<>(List.of());
		TestFunction function = new TestFunction();
		
		Codec<TestObject> codec = creator.create(function);
		assertNotNull(codec);
		assertInstanceOf(CodecGroup.class, codec);
	}
	
	@Test
	void createWithDifferentFunctionTypes() {
		CodecCreator<TestObject2, TestFunction2> creator = new CodecCreator<>(List.of(
			STRING.fieldOf("text", TestObject2::text),
			DOUBLE.fieldOf("number", TestObject2::number),
			BOOLEAN.fieldOf("flag", TestObject2::flag)
		));
		TestFunction2 function = new TestFunction2();
		
		Codec<TestObject2> codec = creator.create(function);
		assertNotNull(codec);
		assertInstanceOf(CodecGroup.class, codec);
	}
	
	@Test
	void createdCodecEncodesCorrectly() {
		CodecCreator<TestObject, TestFunction> creator = new CodecCreator<>(List.of(
			STRING.fieldOf("name", TestObject::name),
			INTEGER.fieldOf("value", TestObject::value)
		));
		TestFunction function = new TestFunction();
		Codec<TestObject> codec = creator.create(function);
		
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		TestObject testObject = new TestObject("test", 42);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), testObject);
		assertTrue(result.isSuccess());
		
		JsonElement element = result.resultOrThrow();
		assertTrue(element.isJsonObject());
		JsonObject obj = element.getAsJsonObject();
		assertEquals(new JsonPrimitive("test"), obj.get("name"));
		assertEquals(new JsonPrimitive(42), obj.get("value"));
	}
	
	@Test
	void createdCodecDecodesCorrectly() {
		CodecCreator<TestObject, TestFunction> creator = new CodecCreator<>(List.of(
			STRING.fieldOf("name", TestObject::name),
			INTEGER.fieldOf("value", TestObject::value)
		));
		TestFunction function = new TestFunction();
		Codec<TestObject> codec = creator.create(function);
		
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		JsonObject jsonObj = new JsonObject();
		jsonObj.add("name", new JsonPrimitive("decoded"));
		jsonObj.add("value", new JsonPrimitive(123));
		
		Result<TestObject> result = codec.decodeStart(typeProvider, jsonObj, jsonObj);
		assertTrue(result.isSuccess());
		
		TestObject obj = result.resultOrThrow();
		assertEquals("decoded", obj.name);
		assertEquals(123, obj.value);
	}
	
	@Test
	void createdCodecRoundTrip() {
		CodecCreator<TestObject, TestFunction> creator = new CodecCreator<>(List.of(
			STRING.fieldOf("name", TestObject::name),
			INTEGER.fieldOf("value", TestObject::value)
		));
		TestFunction function = new TestFunction();
		Codec<TestObject> codec = creator.create(function);
		
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
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
	void createdCodecWithComplexTypes() {
		CodecCreator<TestObject2, TestFunction2> creator = new CodecCreator<>(List.of(
			STRING.fieldOf("text", TestObject2::text),
			DOUBLE.fieldOf("number", TestObject2::number),
			BOOLEAN.fieldOf("flag", TestObject2::flag)
		));
		TestFunction2 function = new TestFunction2();
		Codec<TestObject2> codec = creator.create(function);
		
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		TestObject2 original = new TestObject2("complex", 3.14159, true);
		
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
	void createdCodecWithEmptyCodecs() {
		CodecCreator<TestObject, TestFunction> creator = new CodecCreator<>(List.of());
		TestFunction function = new TestFunction();
		Codec<TestObject> codec = creator.create(function);
		
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		TestObject testObject = new TestObject("test", 42);
		
		Result<JsonElement> encodeResult = codec.encodeStart(typeProvider, typeProvider.empty(), testObject);
		assertTrue(encodeResult.isSuccess());
		
		JsonObject emptyJson = new JsonObject();
		Result<TestObject> decodeResult = codec.decodeStart(typeProvider, emptyJson, emptyJson);
		assertTrue(decodeResult.isError());
		assertTrue(decodeResult.errorOrThrow().contains("Unable to create object with function"));
	}
	
	@Test
	void createdCodecWithFailingFunction() {
		CodecCreator<TestObject, FailingTestFunction> creator = new CodecCreator<>(List.of(
			STRING.fieldOf("name", TestObject::name),
			INTEGER.fieldOf("value", TestObject::value)
		));
		FailingTestFunction function = new FailingTestFunction();
		Codec<TestObject> codec = creator.create(function);
		
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		JsonObject jsonObj = new JsonObject();
		jsonObj.add("name", new JsonPrimitive("test"));
		jsonObj.add("value", new JsonPrimitive(42));
		
		Result<TestObject> result = codec.decodeStart(typeProvider, jsonObj, jsonObj);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to create object with function"));
	}
	
	@Test
	void createdCodecWithFunctionReturningNull() {
		CodecCreator<TestObject, NullReturningTestFunction> creator = new CodecCreator<>(List.of(
			STRING.fieldOf("name", TestObject::name),
			INTEGER.fieldOf("value", TestObject::value)
		));
		NullReturningTestFunction function = new NullReturningTestFunction();
		Codec<TestObject> codec = creator.create(function);
		
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		JsonObject jsonObj = new JsonObject();
		jsonObj.add("name", new JsonPrimitive("test"));
		jsonObj.add("value", new JsonPrimitive(42));
		
		Result<TestObject> result = codec.decodeStart(typeProvider, jsonObj, jsonObj);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to create object with function"));
	}
	
	@Test
	void createdCodecWithSingleParameter() {
		CodecCreator<String, SingleParamFunction> creator = new CodecCreator<>(List.of(
			STRING.fieldOf("value", Function.identity())
		));
		SingleParamFunction function = new SingleParamFunction();
		Codec<String> codec = creator.create(function);
		
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		JsonObject jsonObj = new JsonObject();
		jsonObj.add("value", new JsonPrimitive("single"));
		
		Result<String> result = codec.decodeStart(typeProvider, jsonObj, jsonObj);
		assertTrue(result.isSuccess());
		assertEquals("SINGLE", result.resultOrThrow());
	}
	
	@Test
	void createdCodecWithManyParameters() {
		CodecCreator<TestObject3, ManyParamFunction> creator = new CodecCreator<>(List.of(
			STRING.fieldOf("a", TestObject3::a),
			STRING.fieldOf("b", TestObject3::b),
			STRING.fieldOf("c", TestObject3::c),
			STRING.fieldOf("d", TestObject3::d),
			STRING.fieldOf("e", TestObject3::e)
		));
		ManyParamFunction function = new ManyParamFunction();
		Codec<TestObject3> codec = creator.create(function);
		
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		JsonObject jsonObj = new JsonObject();
		jsonObj.add("a", new JsonPrimitive("1"));
		jsonObj.add("b", new JsonPrimitive("2"));
		jsonObj.add("c", new JsonPrimitive("3"));
		jsonObj.add("d", new JsonPrimitive("4"));
		jsonObj.add("e", new JsonPrimitive("5"));
		
		Result<TestObject3> result = codec.decodeStart(typeProvider, jsonObj, jsonObj);
		assertTrue(result.isSuccess());
		
		TestObject3 obj = result.resultOrThrow();
		assertEquals("1", obj.a);
		assertEquals("2", obj.b);
		assertEquals("3", obj.c);
		assertEquals("4", obj.d);
		assertEquals("5", obj.e);
	}
	
	private record TestObject(@NotNull String name, int value) {}
	
	private record TestObject2(@NotNull String text, double number, boolean flag) {}
	
	private record TestObject3(@NotNull String a, @NotNull String b, @NotNull String c, @NotNull String d, @NotNull String e) {}
	
	private static class TestFunction implements CodecBuilderFunction {
		
		public @NotNull TestObject create(@NotNull Object name, @NotNull Object value) {
			return new TestObject((String) name, (Integer) value);
		}
	}
	
	private static class TestFunction2 implements CodecBuilderFunction {
		
		public @NotNull TestObject2 create(@NotNull Object text, @NotNull Object number, @NotNull Object flag) {
			return new TestObject2((String) text, (Double) number, (Boolean) flag);
		}
	}
	
	private static class FailingTestFunction implements CodecBuilderFunction {
		
		public @Nullable TestObject create(@NotNull Object name, @NotNull Object value) {
			return null;
		}
	}
	
	private static class NullReturningTestFunction implements CodecBuilderFunction {
		
		public @Nullable TestObject create(@NotNull Object name, @NotNull Object value) {
			return null;
		}
	}
	
	private static class SingleParamFunction implements CodecBuilderFunction {
		
		public @NotNull String create(@NotNull Object value) {
			return ((String) value).toUpperCase();
		}
	}
	
	private static class ManyParamFunction implements CodecBuilderFunction {
		
		public @NotNull TestObject3 create(@NotNull Object a, @NotNull Object b, @NotNull Object c, @NotNull Object d, @NotNull Object e) {
			return new TestObject3((String) a, (String) b, (String) c, (String) d, (String) e);
		}
	}
}
