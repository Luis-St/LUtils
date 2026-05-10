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

package net.luis.utils.io.codec.types.struct;

import net.luis.utils.io.codec.*;
import net.luis.utils.io.codec.decoder.DecoderException;
import net.luis.utils.io.codec.encoder.EncoderException;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.*;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static net.luis.utils.io.codec.Codecs.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link FlatDiscriminatedCodec}.<br>
 *
 * @author Luis-St
 */
class FlatDiscriminatedCodecTest {
	
	private sealed interface Shape permits Circle, Rectangle {
		
		String type();
	}
	
	private record Circle(String type, double radius) implements Shape {}
	
	private record Rectangle(String type, double width, double height) implements Shape {}
	
	private static final Codec<Circle> CIRCLE_CODEC = CodecBuilder.of(
		STRING.fieldOf("type", Circle::type),
		DOUBLE.fieldOf("radius", Circle::radius)
	).create(Circle::new);
	
	private static final Codec<Rectangle> RECTANGLE_CODEC = CodecBuilder.of(
		STRING.fieldOf("type", Rectangle::type),
		DOUBLE.fieldOf("width", Rectangle::width),
		DOUBLE.fieldOf("height", Rectangle::height)
	).create(Rectangle::new);
	
	private static final DiscriminatedCodecProvider<Shape, String> SHAPE_PROVIDER = DiscriminatedCodecProvider.create(Shape.class, type -> switch (type) {
		case "circle" -> CIRCLE_CODEC;
		case "rectangle" -> RECTANGLE_CODEC;
		default -> throw new IllegalArgumentException("Unknown type: " + type);
	});
	
	private static final FlatDiscriminatedCodec<Shape, String> SHAPE_CODEC = new FlatDiscriminatedCodec<>("type", STRING, SHAPE_PROVIDER, Shape::type);
	
	@Test
	void constructor() {
		assertThrows(NullPointerException.class, () -> new FlatDiscriminatedCodec<>(null, STRING, SHAPE_PROVIDER, Shape::type));
		assertThrows(NullPointerException.class, () -> new FlatDiscriminatedCodec<>("type", null, SHAPE_PROVIDER, Shape::type));
		assertThrows(NullPointerException.class, () -> new FlatDiscriminatedCodec<>("type", STRING, null, Shape::type));
		assertThrows(NullPointerException.class, () -> new FlatDiscriminatedCodec<>("type", STRING, SHAPE_PROVIDER, null));
		assertDoesNotThrow(() -> new FlatDiscriminatedCodec<>("type", STRING, SHAPE_PROVIDER, Shape::type));
	}
	
	@Test
	void encodeNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		
		assertThrows(NullPointerException.class, () -> SHAPE_CODEC.encode(null, typeProvider.empty(), new Circle("circle", 5.0)));
		assertThrows(NullPointerException.class, () -> SHAPE_CODEC.encode(typeProvider, null, new Circle("circle", 5.0)));
	}
	
	@Test
	void encodeWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		
		EncoderException exception = assertThrows(EncoderException.class, () -> SHAPE_CODEC.encode(typeProvider, typeProvider.empty(), null));
		assertTrue(exception.getMessage().contains("Unable to encode null value as flat discriminated"));
	}
	
	@Test
	void encodeCircle() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		
		JsonElement result = SHAPE_CODEC.encode(typeProvider, typeProvider.empty(), new Circle("circle", 5.0));
		
		assertInstanceOf(JsonObject.class, result);
		JsonObject obj = (JsonObject) result;
		assertEquals(new JsonPrimitive("circle"), obj.get("type"));
		assertEquals(new JsonPrimitive(5.0), obj.get("radius"));
	}
	
	@Test
	void encodeRectangle() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		
		JsonElement result = SHAPE_CODEC.encode(typeProvider, typeProvider.empty(), new Rectangle("rectangle", 3.0, 4.0));
		
		assertInstanceOf(JsonObject.class, result);
		JsonObject obj = (JsonObject) result;
		assertEquals(new JsonPrimitive("rectangle"), obj.get("type"));
		assertEquals(new JsonPrimitive(3.0), obj.get("width"));
		assertEquals(new JsonPrimitive(4.0), obj.get("height"));
	}
	
	@Test
	void encodeWithInvalidDiscriminator() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		DiscriminatedCodecProvider<Shape, String> provider = DiscriminatedCodecProvider.create(Shape.class, Map.of("circle", CIRCLE_CODEC));
		FlatDiscriminatedCodec<Shape, String> codec = new FlatDiscriminatedCodec<>("type", STRING, provider, Shape::type);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), new Rectangle("rectangle", 3.0, 4.0)));
	}
	
	@Test
	void decodeNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		
		assertThrows(NullPointerException.class, () -> SHAPE_CODEC.decode(null, typeProvider.empty(), new JsonObject()));
		assertThrows(NullPointerException.class, () -> SHAPE_CODEC.decode(typeProvider, null, new JsonObject()));
	}
	
	@Test
	void decodeWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		
		DecoderException exception = assertThrows(DecoderException.class, () -> SHAPE_CODEC.decode(typeProvider, typeProvider.empty(), null));
		assertTrue(exception.getMessage().contains("Unable to decode null value as flat discriminated"));
	}
	
	@Test
	void decodeCircle() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		
		JsonObject input = new JsonObject();
		input.add("type", new JsonPrimitive("circle"));
		input.add("radius", new JsonPrimitive(5.0));
		
		Shape result = SHAPE_CODEC.decode(typeProvider, typeProvider.empty(), input);
		
		assertInstanceOf(Circle.class, result);
		Circle circle = (Circle) result;
		assertEquals("circle", circle.type());
		assertEquals(5.0, circle.radius());
	}
	
	@Test
	void decodeRectangle() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		
		JsonObject input = new JsonObject();
		input.add("type", new JsonPrimitive("rectangle"));
		input.add("width", new JsonPrimitive(3.0));
		input.add("height", new JsonPrimitive(4.0));
		
		Shape result = SHAPE_CODEC.decode(typeProvider, typeProvider.empty(), input);
		
		assertInstanceOf(Rectangle.class, result);
		Rectangle rectangle = (Rectangle) result;
		assertEquals("rectangle", rectangle.type());
		assertEquals(3.0, rectangle.width());
		assertEquals(4.0, rectangle.height());
	}
	
	@Test
	void decodeWithMissingDiscriminator() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		
		JsonObject input = new JsonObject();
		input.add("radius", new JsonPrimitive(5.0));
		
		DecoderException exception = assertThrows(DecoderException.class, () -> SHAPE_CODEC.decode(typeProvider, typeProvider.empty(), input));
		assertTrue(exception.getMessage().contains("Discriminator field 'type' not found"));
	}
	
	@Test
	void decodeWithInvalidDiscriminator() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		DiscriminatedCodecProvider<Shape, String> provider = DiscriminatedCodecProvider.create(Shape.class, Map.of("circle", CIRCLE_CODEC));
		FlatDiscriminatedCodec<Shape, String> codec = new FlatDiscriminatedCodec<>("type", STRING, provider, Shape::type);
		
		JsonObject input = new JsonObject();
		input.add("type", new JsonPrimitive("triangle"));
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), input));
		assertTrue(exception.getMessage().contains("No codec found for discriminator value 'triangle'"));
	}
	
	@Test
	void roundTripCircle() throws Exception {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		
		Circle original = new Circle("circle", 7.5);
		JsonElement encoded = SHAPE_CODEC.encode(typeProvider, typeProvider.empty(), original);
		Shape decoded = SHAPE_CODEC.decode(typeProvider, typeProvider.empty(), encoded);
		
		assertEquals(original, decoded);
	}
	
	@Test
	void roundTripRectangle() throws Exception {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		
		Rectangle original = new Rectangle("rectangle", 10.0, 20.0);
		JsonElement encoded = SHAPE_CODEC.encode(typeProvider, typeProvider.empty(), original);
		Shape decoded = SHAPE_CODEC.decode(typeProvider, typeProvider.empty(), encoded);
		
		assertEquals(original, decoded);
	}
	
	@Test
	void roundTripMultipleVariants() throws Exception {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		
		Shape[] shapes = {
			new Circle("circle", 1.0),
			new Rectangle("rectangle", 2.0, 3.0),
			new Circle("circle", 100.0),
			new Rectangle("rectangle", 0.5, 0.5)
		};
		
		for (Shape original : shapes) {
			JsonElement encoded = SHAPE_CODEC.encode(typeProvider, typeProvider.empty(), original);
			Shape decoded = SHAPE_CODEC.decode(typeProvider, typeProvider.empty(), encoded);
			assertEquals(original, decoded);
		}
	}
	
	@Test
	void integrationWithCodecsFactory() throws Exception {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		
		FlatDiscriminatedCodec<Shape, String> codec = Codecs.flatDiscriminatedBy("type", STRING, Shape::type, SHAPE_PROVIDER);
		
		Circle original = new Circle("circle", 3.14);
		JsonElement encoded = codec.encode(typeProvider, typeProvider.empty(), original);
		Shape decoded = codec.decode(typeProvider, typeProvider.empty(), encoded);
		
		assertEquals(original, decoded);
	}
	
	@Test
	void integrationWithMapBasedProvider() throws Exception {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		
		Map<String, Codec<? extends Shape>> codecMap = Map.of(
			"circle", CIRCLE_CODEC,
			"rectangle", RECTANGLE_CODEC
		);
		DiscriminatedCodecProvider<Shape, String> provider = DiscriminatedCodecProvider.create(Shape.class, codecMap);
		FlatDiscriminatedCodec<Shape, String> codec = new FlatDiscriminatedCodec<>("type", STRING, provider, Shape::type);
		
		Rectangle original = new Rectangle("rectangle", 5.0, 10.0);
		JsonElement encoded = codec.encode(typeProvider, typeProvider.empty(), original);
		Shape decoded = codec.decode(typeProvider, typeProvider.empty(), encoded);
		
		assertEquals(original, decoded);
	}
	
	@Test
	void equalsAndHashCode() {
		assertEquals(SHAPE_CODEC.hashCode(), SHAPE_CODEC.hashCode());
		assertEquals(SHAPE_CODEC, SHAPE_CODEC);
	}
	
	@Test
	void toStringRepresentation() {
		String result = SHAPE_CODEC.toString();
		assertTrue(result.startsWith("FlatDiscriminatedCodec["));
		assertTrue(result.endsWith("]"));
		assertTrue(result.contains("type"));
	}
}
