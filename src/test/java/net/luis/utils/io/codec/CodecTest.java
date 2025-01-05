/*
 * LUtils
 * Copyright (C) 2024 Luis Staudt
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
import net.luis.utils.io.codec.struct.*;
import net.luis.utils.io.data.json.*;
import net.luis.utils.util.Result;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link Codec}.<br>
 *
 * @author Luis-St
 */
class CodecTest {
	
	@Test
	void constants() {
	}
	
	@Test
	void from() {
		assertThrows(NullPointerException.class, () -> Codec.<Integer, String>from(null, (Function<String, Integer>) Integer::valueOf, ResultMappingFunction.direct(String::valueOf), "IntegerToStringCodec"));
		assertThrows(NullPointerException.class, () -> Codec.from(Codec.INTEGER, (Function<String, Integer>) null, ResultMappingFunction.direct(String::valueOf), "IntegerToStringCodec"));
		assertThrows(NullPointerException.class, () -> Codec.<Integer, String>from(Codec.INTEGER, (Function<String, Integer>) Integer::valueOf, null, "IntegerToStringCodec"));
		assertThrows(NullPointerException.class, () -> Codec.<Integer, String>from(Codec.INTEGER, (Function<String, Integer>) Integer::valueOf, ResultMappingFunction.direct(String::valueOf), null));
		assertNotNull(Codec.<Integer, String>from(Codec.INTEGER, (Function<String, Integer>) Integer::valueOf, ResultMappingFunction.direct(String::valueOf), "IntegerToStringCodec"));
		
		assertThrows(NullPointerException.class, () -> Codec.<Integer, String>from(null, ResultingFunction.throwable(Integer::valueOf), ResultMappingFunction.direct(String::valueOf), "IntegerToStringCodec"));
		assertThrows(NullPointerException.class, () -> Codec.from(Codec.INTEGER, (ResultingFunction<String, Integer>) null, ResultMappingFunction.direct(String::valueOf), "IntegerToStringCodec"));
		assertThrows(NullPointerException.class, () -> Codec.<Integer, String>from(Codec.INTEGER, ResultingFunction.throwable(Integer::valueOf), null, "IntegerToStringCodec"));
		assertThrows(NullPointerException.class, () -> Codec.<Integer, String>from(Codec.INTEGER, ResultingFunction.throwable(Integer::valueOf), ResultMappingFunction.direct(String::valueOf), null));
		assertNotNull(Codec.<Integer, String>from(Codec.INTEGER, (Function<String, Integer>) Integer::valueOf, ResultMappingFunction.direct(String::valueOf), "IntegerToStringCodec"));
	}
	
	@Test
	void of() {
		assertThrows(NullPointerException.class, () -> Codec.of(null, Codec.INTEGER, "IntegerCodec"));
		assertThrows(NullPointerException.class, () -> Codec.of(Codec.INTEGER, null, "IntegerCodec"));
		assertThrows(NullPointerException.class, () -> Codec.of(Codec.INTEGER, Codec.INTEGER, null));
		assertNotNull(Codec.of(Codec.INTEGER, Codec.INTEGER, "IntegerCodec"));
	}
	
	@Test
	void keyableStatic() {
		assertThrows(NullPointerException.class, () -> Codec.keyable((Codec<Integer>) null, Integer::valueOf));
		assertThrows(NullPointerException.class, () -> Codec.keyable(Codec.INTEGER, null));
		assertNotNull(Codec.keyable(Codec.INTEGER, Integer::valueOf));
		assertInstanceOf(KeyableCodec.class, Codec.keyable(Codec.INTEGER, Integer::valueOf));
		
		assertThrows(NullPointerException.class, () -> Codec.keyable((Codec<Integer>) null, String::valueOf, Integer::valueOf));
		assertThrows(NullPointerException.class, () -> Codec.keyable(Codec.INTEGER, null, Integer::valueOf));
		assertThrows(NullPointerException.class, () -> Codec.keyable(Codec.INTEGER, String::valueOf, null));
		assertNotNull(Codec.keyable(Codec.INTEGER, String::valueOf, Integer::valueOf));
		assertInstanceOf(KeyableCodec.class, Codec.keyable(Codec.INTEGER, String::valueOf, Integer::valueOf));
		
		assertThrows(NullPointerException.class, () -> Codec.keyable(null, Codec.INTEGER, Codec.INTEGER));
		assertThrows(NullPointerException.class, () -> Codec.keyable(Codec.INTEGER, null, Codec.INTEGER));
		assertThrows(NullPointerException.class, () -> Codec.keyable(Codec.INTEGER, Codec.INTEGER, null));
		assertNotNull(Codec.keyable(Codec.INTEGER, Codec.INTEGER, Codec.INTEGER));
		assertInstanceOf(KeyableCodec.class, Codec.keyable(Codec.INTEGER, Codec.INTEGER, Codec.INTEGER));
	}
	
	@Test
	void enumOrdinal() {
		assertThrows(NullPointerException.class, () -> Codec.enumOrdinal((Class<TestEnum>) null));
		
		Codec<TestEnum> codec = Codec.enumOrdinal(TestEnum.class);
		assertInstanceOf(KeyableCodec.class, codec);
		
		JsonElement encoded = codec.encode(JsonTypeProvider.INSTANCE, TestEnum.ONE);
		assertInstanceOf(JsonPrimitive.class, encoded);
		assertEquals(0, encoded.getAsJsonPrimitive().getAsIntegerStrict());
		
		TestEnum decoded = codec.decode(JsonTypeProvider.INSTANCE, encoded);
		assertEquals(TestEnum.ONE, decoded);
	}
	
	@Test
	void enumName() {
		assertThrows(NullPointerException.class, () -> Codec.enumName((Class<TestEnum>) null));
		
		Codec<TestEnum> codec = Codec.enumName(TestEnum.class);
		assertInstanceOf(KeyableCodec.class, codec);
		
		JsonElement encoded = codec.encode(JsonTypeProvider.INSTANCE, TestEnum.ONE);
		assertInstanceOf(JsonPrimitive.class, encoded);
		assertEquals("ONE", encoded.getAsJsonPrimitive().getAsString());
		
		TestEnum decoded = codec.decode(JsonTypeProvider.INSTANCE, encoded);
		assertEquals(TestEnum.ONE, decoded);
	}
	
	@Test
	void friendlyEnumName() {
		Function<TestEnum, String> toFriendly = e -> e.name().toLowerCase();
		Function<String, TestEnum> fromFriendly = s -> TestEnum.valueOf(s.toUpperCase());
		
		assertThrows(NullPointerException.class, () -> Codec.friendlyEnumName(null, fromFriendly));
		assertThrows(NullPointerException.class, () -> Codec.friendlyEnumName(toFriendly, null));
		
		Codec<TestEnum> codec = Codec.friendlyEnumName(toFriendly, fromFriendly);
		assertInstanceOf(KeyableCodec.class, codec);
		
		JsonElement encoded = codec.encode(JsonTypeProvider.INSTANCE, TestEnum.ONE);
		assertInstanceOf(JsonPrimitive.class, encoded);
		assertEquals("one", encoded.getAsJsonPrimitive().getAsString());
		
		TestEnum decoded = codec.decode(JsonTypeProvider.INSTANCE, encoded);
		assertEquals(TestEnum.ONE, decoded);
	}
	
	@Test
	void dynamicEnum() {
		assertThrows(NullPointerException.class, () -> Codec.dynamicEnum((Class<TestEnum>) null));
		
		Codec<TestEnum> codec = Codec.dynamicEnum(TestEnum.class);
		assertInstanceOf(KeyableCodec.class, codec);
		
		JsonElement encoded = codec.encode(JsonTypeProvider.INSTANCE, TestEnum.ONE);
		assertInstanceOf(JsonPrimitive.class, encoded);
		assertEquals("ONE", encoded.getAsJsonPrimitive().getAsString());
		
		TestEnum decodedName = codec.decode(JsonTypeProvider.INSTANCE, encoded);
		assertEquals(TestEnum.ONE, decodedName);
		
		TestEnum decodedOrdinal = codec.decode(JsonTypeProvider.INSTANCE, new JsonPrimitive(0));
		assertEquals(TestEnum.ONE, decodedOrdinal);
	}
	
	@Test
	void string() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		assertThrows(IllegalArgumentException.class, () -> Codec.string(-1));
		
		Codec<String> limitedCodec = Codec.string(2);
		assertTrue(limitedCodec.encodeStart(typeProvider, typeProvider.empty(), "1").isSuccess());
		assertTrue(limitedCodec.encodeStart(typeProvider, typeProvider.empty(), "12").isSuccess());
		assertTrue(limitedCodec.encodeStart(typeProvider, typeProvider.empty(), "123").isError());
		
		assertTrue(limitedCodec.decodeStart(typeProvider, new JsonPrimitive("1")).isSuccess());
		assertTrue(limitedCodec.decodeStart(typeProvider, new JsonPrimitive("12")).isSuccess());
		assertTrue(limitedCodec.decodeStart(typeProvider, new JsonPrimitive("123")).isError());
		
		Codec<String> boundedCodec = Codec.string(2, 2);
		assertTrue(boundedCodec.encodeStart(typeProvider, typeProvider.empty(), "1").isError());
		assertTrue(boundedCodec.encodeStart(typeProvider, typeProvider.empty(), "123").isError());
		assertTrue(boundedCodec.encodeStart(typeProvider, typeProvider.empty(), "12").isSuccess());
		
		assertTrue(boundedCodec.decodeStart(typeProvider, new JsonPrimitive("1")).isError());
		assertTrue(boundedCodec.decodeStart(typeProvider, new JsonPrimitive("123")).isError());
		assertTrue(boundedCodec.decodeStart(typeProvider, new JsonPrimitive("12")).isSuccess());
	}
	
	@Test
	void noneEmptyString() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codec.noneEmptyString();
		
		assertTrue(codec.encodeStart(typeProvider, typeProvider.empty(), "").isError());
		assertTrue(codec.encodeStart(typeProvider, typeProvider.empty(), "1").isSuccess());
		
		assertTrue(codec.decodeStart(typeProvider, new JsonPrimitive("")).isError());
		assertTrue(codec.decodeStart(typeProvider, new JsonPrimitive("1")).isSuccess());
	}
	
	@Test
	void unit() { // See also: UnitCodecTest
		assertThrows(NullPointerException.class, () -> Codec.unit((Object) null));
		assertInstanceOf(UnitCodec.class, Codec.unit("unit"));
		
		assertThrows(NullPointerException.class, () -> Codec.unit((Supplier<?>) null));
		assertInstanceOf(UnitCodec.class, Codec.unit(() -> "unit"));
	}
	
	@Test
	void optionalStatic() { // See also: OptionalCodecTest
		assertThrows(NullPointerException.class, () -> Codec.optional((Codec<Integer>) null));
		assertInstanceOf(OptionalCodec.class, Codec.optional(Codec.INTEGER));
	}
	
	@Test
	void listStatic() { // See also: ListCodecTest
		assertThrows(NullPointerException.class, () -> Codec.list((Codec<Integer>) null));
		assertInstanceOf(ListCodec.class, Codec.list(Codec.INTEGER));
		
		assertThrows(NullPointerException.class, () -> Codec.list((Codec<Integer>) null, 2));
		assertThrows(IllegalArgumentException.class, () -> Codec.list(Codec.INTEGER, -1));
		assertInstanceOf(ListCodec.class, Codec.list(Codec.INTEGER, 2));
		
		assertThrows(NullPointerException.class, () -> Codec.list((Codec<Integer>) null, 2, 2));
		assertThrows(IllegalArgumentException.class, () -> Codec.list(Codec.INTEGER, -1, 2));
		assertThrows(IllegalArgumentException.class, () -> Codec.list(Codec.INTEGER, 2, -1));
		assertInstanceOf(ListCodec.class, Codec.list(Codec.INTEGER, 2, 2));
	}
	
	@Test
	void noneEmptyListStatic() { // See also: ListCodecTest
		assertThrows(NullPointerException.class, () -> Codec.noneEmptyList((Codec<Integer>) null));
		assertInstanceOf(ListCodec.class, Codec.noneEmptyList(Codec.INTEGER));
	}
	
	@Test
	void streamStatic() {
		assertThrows(NullPointerException.class, () -> Codec.stream((Codec<Integer>) null));
		
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Stream<Integer>> codec = Codec.stream(Codec.INTEGER);
		JsonArray json = new JsonArray(List.of(new JsonPrimitive(1), new JsonPrimitive(2), new JsonPrimitive(3)));
		
		Result<JsonElement> encoded = codec.encodeStart(typeProvider, typeProvider.empty(), Stream.of(1, 2, 3));
		assertTrue(encoded.isSuccess());
		assertInstanceOf(JsonArray.class, encoded.orThrow());
		assertEquals(json, encoded.orThrow());
		
		Result<Stream<Integer>> decoded = codec.decodeStart(typeProvider, json);
		assertTrue(decoded.isSuccess());
		assertEquals(List.of(1, 2, 3), decoded.orThrow().toList());
	}
	
	@Test
	void mapStatic() { // See also: MapCodecTest
		assertThrows(NullPointerException.class, () -> Codec.map((Codec<Integer>) null));
		assertInstanceOf(MapCodec.class, Codec.map(Codec.INTEGER));
		
		assertThrows(NullPointerException.class, () -> Codec.map((KeyableCodec<Integer>) null, Codec.BOOLEAN));
		assertThrows(NullPointerException.class, () -> Codec.map(Codec.INTEGER, (Codec<Boolean>) null));
		assertInstanceOf(MapCodec.class, Codec.map(Codec.INTEGER, Codec.BOOLEAN));
		
		assertThrows(NullPointerException.class, () -> Codec.map((KeyableCodec<Integer>) null, Codec.BOOLEAN, 2));
		assertThrows(NullPointerException.class, () -> Codec.map(Codec.INTEGER, (Codec<Boolean>) null, 2));
		assertThrows(IllegalArgumentException.class, () -> Codec.map(Codec.INTEGER, Codec.BOOLEAN, -1));
		assertInstanceOf(MapCodec.class, Codec.map(Codec.INTEGER, Codec.BOOLEAN, 2));
		
		assertThrows(NullPointerException.class, () -> Codec.map((KeyableCodec<Integer>) null, Codec.BOOLEAN, 2, 2));
		assertThrows(NullPointerException.class, () -> Codec.map(Codec.INTEGER, (Codec<Boolean>) null, 2, 2));
		assertThrows(IllegalArgumentException.class, () -> Codec.map(Codec.INTEGER, Codec.BOOLEAN, -1, 2));
		assertThrows(IllegalArgumentException.class, () -> Codec.map(Codec.INTEGER, Codec.BOOLEAN, 2, -1));
		assertInstanceOf(MapCodec.class, Codec.map(Codec.INTEGER, Codec.BOOLEAN, 2, 2));
	}
	
	@Test
	void noneEmptyMap() { // See also: MapCodecTest
		assertThrows(NullPointerException.class, () -> Codec.noneEmptyMap((KeyableCodec<Integer>) null, Codec.BOOLEAN));
		assertThrows(NullPointerException.class, () -> Codec.noneEmptyMap(Codec.INTEGER, (Codec<Boolean>) null));
		assertInstanceOf(MapCodec.class, Codec.noneEmptyMap(Codec.INTEGER, Codec.BOOLEAN));
	}
	
	@Test
	void either() { // See also: EitherCodecTest
		assertThrows(NullPointerException.class, () -> Codec.either((Codec<Integer>) null, Codec.BOOLEAN));
		assertThrows(NullPointerException.class, () -> Codec.either(Codec.INTEGER, (Codec<Boolean>) null));
		assertInstanceOf(EitherCodec.class, Codec.either(Codec.INTEGER, Codec.BOOLEAN));
	}
	
	@Test
	void withAlternativeStatic() {
		Codec<Integer> alternative = Codec.DOUBLE.xmap(Integer::doubleValue, Double::intValue);
		assertThrows(NullPointerException.class, () -> Codec.withAlternative((Codec<Integer>) null, alternative));
		assertThrows(NullPointerException.class, () -> Codec.withAlternative(Codec.INTEGER, null));
		
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = Codec.withAlternative(Codec.INTEGER, alternative);
		
		Result<JsonElement> encoded = codec.encodeStart(typeProvider, typeProvider.empty(), 1);
		assertTrue(encoded.isSuccess());
		assertEquals(1, encoded.orThrow().getAsJsonPrimitive().getAsIntegerStrict());
		
		Result<Integer> decoded = codec.decodeStart(typeProvider, new JsonPrimitive(1.0));
		assertTrue(decoded.isSuccess());
		assertEquals(1, decoded.orThrow());
	}
	
	@Test
	void stringResolver() {
		assertThrows(NullPointerException.class, () -> Codec.stringResolver(null, Integer::valueOf));
		assertThrows(NullPointerException.class, () -> Codec.stringResolver(String::valueOf, null));
		
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = Codec.stringResolver(String::valueOf, Integer::valueOf);
		
		Result<JsonElement> encoded = codec.encodeStart(typeProvider, typeProvider.empty(), 1);
		assertTrue(encoded.isSuccess());
		assertEquals("1", encoded.orThrow().getAsJsonPrimitive().getAsString());
		
		assertTrue(codec.decodeStart(typeProvider, new JsonPrimitive("test")).isError());
		Result<Integer> decoded = codec.decodeStart(typeProvider, new JsonPrimitive("1"));
		assertTrue(decoded.isSuccess());
		assertEquals(1, decoded.orThrow());
	}
	
	@Test
	void keyableInstance() {
		assertThrows(NullPointerException.class, () -> Codec.BOOLEAN.keyable((Function<Boolean, String>) null, Boolean::valueOf));
		assertThrows(NullPointerException.class, () -> Codec.BOOLEAN.keyable(String::valueOf, (Function<String, Boolean>) null));
		assertInstanceOf(KeyableCodec.class, Codec.BOOLEAN.keyable(String::valueOf, Boolean::valueOf));
	}
	
	@Test
	void optionalInstance() {
		assertInstanceOf(OptionalCodec.class, Codec.INTEGER.optional());
	}
	
	@Test
	void listInstance() {
		assertInstanceOf(ListCodec.class, Codec.INTEGER.list());
		
		assertThrows(IllegalArgumentException.class, () -> Codec.INTEGER.list(-1));
		assertInstanceOf(ListCodec.class, Codec.INTEGER.list(2));
		
		assertThrows(IllegalArgumentException.class, () -> Codec.INTEGER.list(-1, 2));
		assertThrows(IllegalArgumentException.class, () -> Codec.INTEGER.list(2, -1));
		assertInstanceOf(ListCodec.class, Codec.INTEGER.list(2, 2));
	}
	
	@Test
	void noneEmptyListInstance() {
		assertInstanceOf(ListCodec.class, Codec.INTEGER.noneEmptyList());
	}
	
	@Test
	void streamInstance() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Stream<Integer>> codec = Codec.INTEGER.stream();
		JsonArray json = new JsonArray(List.of(new JsonPrimitive(1), new JsonPrimitive(2), new JsonPrimitive(3)));
		
		Result<JsonElement> encoded = codec.encodeStart(typeProvider, typeProvider.empty(), Stream.of(1, 2, 3));
		assertTrue(encoded.isSuccess());
		assertInstanceOf(JsonArray.class, encoded.orThrow());
		assertEquals(json, encoded.orThrow());
		
		Result<Stream<Integer>> decoded = codec.decodeStart(typeProvider, json);
		assertTrue(decoded.isSuccess());
		assertEquals(List.of(1, 2, 3), decoded.orThrow().toList());
	}
	
	@Test
	void withAlternativeInstance() {
		Codec<Integer> alternative = Codec.DOUBLE.xmap(Integer::doubleValue, Double::intValue);
		assertThrows(NullPointerException.class, () -> Codec.INTEGER.withAlternative(null));
		
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = Codec.INTEGER.withAlternative(alternative);
		
		Result<JsonElement> encoded = codec.encodeStart(typeProvider, typeProvider.empty(), 1);
		assertTrue(encoded.isSuccess());
		assertEquals(1, encoded.orThrow().getAsJsonPrimitive().getAsIntegerStrict());
		
		Result<Integer> decoded = codec.decodeStart(typeProvider, new JsonPrimitive(1.0));
		assertTrue(decoded.isSuccess());
		assertEquals(1, decoded.orThrow());
	}
	
	@Test
	void xmap() {
		assertThrows(NullPointerException.class, () -> Codec.STRING.xmap(null, Integer::valueOf));
		assertThrows(NullPointerException.class, () -> Codec.STRING.xmap(String::valueOf, (Function<String, Integer>) null));
		
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = Codec.STRING.xmap(String::valueOf, Integer::valueOf);
		
		Result<JsonElement> encoded = codec.encodeStart(typeProvider, typeProvider.empty(), 1);
		assertTrue(encoded.isSuccess());
		assertEquals("1", encoded.orThrow().getAsJsonPrimitive().getAsString());
		
		Result<Integer> decoded = codec.decodeStart(typeProvider, new JsonPrimitive("1"));
		assertTrue(decoded.isSuccess());
		assertEquals(1, decoded.orThrow());
		
		assertThrows(NumberFormatException.class, () -> codec.decodeStart(typeProvider, new JsonPrimitive("test")));
	}
	
	@Test
	void flatMap() {
		assertThrows(NullPointerException.class, () -> Codec.STRING.flatMap(null, ResultMappingFunction.throwable(Integer::valueOf)));
		assertThrows(NullPointerException.class, () -> Codec.STRING.flatMap(String::valueOf, (ResultMappingFunction<String, Integer>) null));
		
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = Codec.STRING.flatMap(String::valueOf, ResultMappingFunction.throwable(Integer::valueOf));
		
		Result<JsonElement> encoded = codec.encodeStart(typeProvider, typeProvider.empty(), 1);
		assertTrue(encoded.isSuccess());
		assertEquals("1", encoded.orThrow().getAsJsonPrimitive().getAsString());
		
		Result<Integer> decoded = codec.decodeStart(typeProvider, new JsonPrimitive("1"));
		assertTrue(decoded.isSuccess());
		assertEquals(1, decoded.orThrow());
		
		assertTrue(codec.decodeStart(typeProvider, new JsonPrimitive("test")).isError());
	}
	
	@Test
	void map() {
		assertThrows(NullPointerException.class, () -> Codec.STRING.map(null, ResultMappingFunction.throwable(Integer::valueOf)));
		assertThrows(NullPointerException.class, () -> Codec.STRING.map(ResultingFunction.direct(String::valueOf), (ResultMappingFunction<String, Integer>) null));
		
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = Codec.STRING.map(ResultingFunction.direct(String::valueOf), ResultMappingFunction.throwable(Integer::valueOf));
		
		Result<JsonElement> encoded = codec.encodeStart(typeProvider, typeProvider.empty(), 1);
		assertTrue(encoded.isSuccess());
		assertEquals("1", encoded.orThrow().getAsJsonPrimitive().getAsString());
		
		Result<Integer> decoded = codec.decodeStart(typeProvider, new JsonPrimitive("1"));
		assertTrue(decoded.isSuccess());
		assertEquals(1, decoded.orThrow());
		
		assertTrue(codec.decodeStart(typeProvider, new JsonPrimitive("test")).isError());
	}
	
	@Test
	void validate() {
		assertThrows(NullPointerException.class, () -> Codec.STRING.validate(null));
		
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codec.STRING.validate(s -> {
			if (s.length() > 2) {
				return Result.error("String is too long");
			}
			return Result.success(s);
		});
		
		Result<JsonElement> encoded = codec.encodeStart(typeProvider, typeProvider.empty(), "1");
		assertTrue(encoded.isSuccess());
		assertEquals("1", encoded.orThrow().getAsJsonPrimitive().getAsString());
		
		Result<String> decoded = codec.decodeStart(typeProvider, new JsonPrimitive("1"));
		assertTrue(decoded.isSuccess());
		assertEquals("1", decoded.orThrow());
		
		assertTrue(codec.decodeStart(typeProvider, new JsonPrimitive("123")).isError());
	}
	
	@Test
	void orElse() {
		assertDoesNotThrow(() -> Codec.STRING.orElse(null));
		
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codec.STRING.orElse("default");
		
		Result<JsonElement> encoded = codec.encodeStart(typeProvider, typeProvider.empty(), "1");
		assertTrue(encoded.isSuccess());
		assertEquals("1", encoded.orThrow().getAsJsonPrimitive().getAsString());
		
		Result<String> decoded = codec.decodeStart(typeProvider, new JsonPrimitive("1"));
		assertTrue(decoded.isSuccess());
		assertEquals("1", decoded.orThrow());
		
		Result<String> decodedDefault = codec.decodeStart(typeProvider, typeProvider.empty());
		assertTrue(decodedDefault.isSuccess());
		assertEquals("default", decodedDefault.orThrow());
	}
	
	@Test
	void orElseGet() {
		assertThrows(NullPointerException.class, () -> Codec.STRING.orElseGet(null));
		
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codec.STRING.orElseGet(() -> "default");
		
		Result<JsonElement> encoded = codec.encodeStart(typeProvider, typeProvider.empty(), "1");
		assertTrue(encoded.isSuccess());
		assertEquals("1", encoded.orThrow().getAsJsonPrimitive().getAsString());
		
		Result<String> decoded = codec.decodeStart(typeProvider, new JsonPrimitive("1"));
		assertTrue(decoded.isSuccess());
		assertEquals("1", decoded.orThrow());
		
		Result<String> decodedDefault = codec.decodeStart(typeProvider, typeProvider.empty());
		assertTrue(decodedDefault.isSuccess());
		assertEquals("default", decodedDefault.orThrow());
	}
	
	@Test
	void bind() {
		assertThrows(NullPointerException.class, () -> Codec.STRING.bind(null));
		assertNotNull(Codec.STRING.bind(new CodecBuilder<>()));
	}
	
	//region Internal
	private enum TestEnum {
		ONE, TWO, THREE
	}
	//endregion
}
