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

package net.luis.utils.io.codec.types.struct;

import net.luis.utils.io.codec.*;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.*;
import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link RecursiveCodec}.<br>
 *
 * @author Luis-St
 */
class RecursiveCodecTest {
	
	@Test
	void recursiveCodecWithNullFactory() {
		assertThrows(NullPointerException.class, () -> new RecursiveCodec<>(null));
	}
	
	@Test
	void encodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LinkedListNode> codec = Codecs.recursive(self ->
			CodecBuilder.of(
				Codecs.INTEGER.fieldOf("value", LinkedListNode::value),
				self.nullable().fieldOf("next", LinkedListNode::next)
			).create(LinkedListNode::new)
		);
		
		LinkedListNode node = new LinkedListNode(1, null);
		assertThrows(NullPointerException.class, () -> codec.encodeStart(null, typeProvider.empty(), node));
		assertThrows(NullPointerException.class, () -> codec.encodeStart(typeProvider, null, node));
	}
	
	@Test
	void decodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LinkedListNode> codec = Codecs.recursive(self ->
			CodecBuilder.of(
				Codecs.INTEGER.fieldOf("value", LinkedListNode::value),
				self.nullable().fieldOf("next", LinkedListNode::next)
			).create(LinkedListNode::new)
		);
		
		JsonObject obj = new JsonObject();
		obj.add("value", 1);
		assertThrows(NullPointerException.class, () -> codec.decodeStart(null, typeProvider.empty(), obj));
		assertThrows(NullPointerException.class, () -> codec.decodeStart(typeProvider, null, obj));
	}
	
	@Test
	void encodeLinkedListWithSingleNode() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LinkedListNode> codec = Codecs.recursive(self ->
			CodecBuilder.of(
				Codecs.INTEGER.fieldOf("value", LinkedListNode::value),
				self.nullable().fieldOf("next", LinkedListNode::next)
			).create(LinkedListNode::new)
		);
		
		LinkedListNode node = new LinkedListNode(42, null);
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), node);
		
		assertTrue(result.isSuccess());
		JsonElement element = result.resultOrThrow();
		assertInstanceOf(JsonObject.class, element);
		
		JsonObject obj = element.getAsJsonObject();
		assertEquals(42, obj.get("value").getAsJsonPrimitive().getAsInteger());
		assertTrue(obj.get("next").isJsonNull());
	}
	
	@Test
	void encodeLinkedListWithMultipleNodes() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LinkedListNode> codec = Codecs.recursive(self ->
			CodecBuilder.of(
				Codecs.INTEGER.fieldOf("value", LinkedListNode::value),
				self.nullable().fieldOf("next", LinkedListNode::next)
			).create(LinkedListNode::new)
		);
		
		LinkedListNode node = new LinkedListNode(1, new LinkedListNode(2, new LinkedListNode(3, null)));
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), node);
		
		assertTrue(result.isSuccess());
		JsonElement element = result.resultOrThrow();
		assertInstanceOf(JsonObject.class, element);
		
		JsonObject obj = element.getAsJsonObject();
		assertEquals(1, obj.get("value").getAsJsonPrimitive().getAsInteger());
		
		JsonObject nextObj = obj.get("next").getAsJsonObject();
		assertEquals(2, nextObj.get("value").getAsJsonPrimitive().getAsInteger());
		
		JsonObject nextNextObj = nextObj.get("next").getAsJsonObject();
		assertEquals(3, nextNextObj.get("value").getAsJsonPrimitive().getAsInteger());
		assertTrue(nextNextObj.get("next").isJsonNull());
	}
	
	@Test
	void decodeLinkedListWithSingleNode() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LinkedListNode> codec = Codecs.recursive(self ->
			CodecBuilder.of(
				Codecs.INTEGER.fieldOf("value", LinkedListNode::value),
				self.nullable().fieldOf("next", LinkedListNode::next)
			).create(LinkedListNode::new)
		);
		
		JsonObject obj = new JsonObject();
		obj.add("value", 42);
		obj.add("next", JsonNull.INSTANCE);
		
		Result<LinkedListNode> result = codec.decodeStart(typeProvider, typeProvider.empty(), obj);
		assertTrue(result.isSuccess());
		
		LinkedListNode node = result.resultOrThrow();
		assertEquals(42, node.value());
		assertNull(node.next());
	}
	
	@Test
	void decodeLinkedListWithMultipleNodes() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LinkedListNode> codec = Codecs.recursive(self ->
			CodecBuilder.of(
				Codecs.INTEGER.fieldOf("value", LinkedListNode::value),
				self.nullable().fieldOf("next", LinkedListNode::next)
			).create(LinkedListNode::new)
		);
		
		JsonObject nextNextObj = new JsonObject();
		nextNextObj.add("value", 3);
		nextNextObj.add("next", JsonNull.INSTANCE);
		
		JsonObject nextObj = new JsonObject();
		nextObj.add("value", 2);
		nextObj.add("next", nextNextObj);
		
		JsonObject obj = new JsonObject();
		obj.add("value", 1);
		obj.add("next", nextObj);
		
		Result<LinkedListNode> result = codec.decodeStart(typeProvider, typeProvider.empty(), obj);
		assertTrue(result.isSuccess());
		
		LinkedListNode node = result.resultOrThrow();
		assertEquals(1, node.value());
		assertNotNull(node.next());
		assertEquals(2, node.next().value());
		assertNotNull(node.next().next());
		assertEquals(3, node.next().next().value());
		assertNull(node.next().next().next());
	}
	
	@Test
	void roundTripLinkedList() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LinkedListNode> codec = Codecs.recursive(self ->
			CodecBuilder.of(
				Codecs.INTEGER.fieldOf("value", LinkedListNode::value),
				self.nullable().fieldOf("next", LinkedListNode::next)
			).create(LinkedListNode::new)
		);
		
		LinkedListNode original = new LinkedListNode(1, new LinkedListNode(2, new LinkedListNode(3, null)));
		
		Result<JsonElement> encodeResult = codec.encodeStart(typeProvider, typeProvider.empty(), original);
		assertTrue(encodeResult.isSuccess());
		
		Result<LinkedListNode> decodeResult = codec.decodeStart(typeProvider, typeProvider.empty(), encodeResult.resultOrThrow());
		assertTrue(decodeResult.isSuccess());
		
		LinkedListNode decoded = decodeResult.resultOrThrow();
		assertEquals(original, decoded);
	}
	
	@Test
	void encodeBinaryTreeWithSingleNode() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<TreeNode> codec = Codecs.recursive(self ->
			CodecBuilder.of(
				Codecs.INTEGER.fieldOf("value", TreeNode::value),
				self.nullable().fieldOf("left", TreeNode::left),
				self.nullable().fieldOf("right", TreeNode::right)
			).create(TreeNode::new)
		);
		
		TreeNode node = new TreeNode(42, null, null);
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), node);
		
		assertTrue(result.isSuccess());
		JsonElement element = result.resultOrThrow();
		assertInstanceOf(JsonObject.class, element);
		
		JsonObject obj = element.getAsJsonObject();
		assertEquals(42, obj.get("value").getAsJsonPrimitive().getAsInteger());
		assertTrue(obj.get("left").isJsonNull());
		assertTrue(obj.get("right").isJsonNull());
	}
	
	@Test
	void encodeBinaryTreeWithMultipleNodes() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<TreeNode> codec = Codecs.recursive(self ->
			CodecBuilder.of(
				Codecs.INTEGER.fieldOf("value", TreeNode::value),
				self.nullable().fieldOf("left", TreeNode::left),
				self.nullable().fieldOf("right", TreeNode::right)
			).create(TreeNode::new)
		);
		
		TreeNode tree = new TreeNode(
			10,
			new TreeNode(5, null, null),
			new TreeNode(15, null, null)
		);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), tree);
		assertTrue(result.isSuccess());
		
		JsonElement element = result.resultOrThrow();
		assertInstanceOf(JsonObject.class, element);
		
		JsonObject obj = element.getAsJsonObject();
		assertEquals(10, obj.get("value").getAsJsonPrimitive().getAsInteger());
		
		JsonObject leftObj = obj.get("left").getAsJsonObject();
		assertEquals(5, leftObj.get("value").getAsJsonPrimitive().getAsInteger());
		
		JsonObject rightObj = obj.get("right").getAsJsonObject();
		assertEquals(15, rightObj.get("value").getAsJsonPrimitive().getAsInteger());
	}
	
	@Test
	void roundTripBinaryTree() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<TreeNode> codec = Codecs.recursive(self ->
			CodecBuilder.of(
				Codecs.INTEGER.fieldOf("value", TreeNode::value),
				self.nullable().fieldOf("left", TreeNode::left),
				self.nullable().fieldOf("right", TreeNode::right)
			).create(TreeNode::new)
		);
		
		TreeNode original = new TreeNode(
			10,
			new TreeNode(5, new TreeNode(3, null, null), new TreeNode(7, null, null)),
			new TreeNode(15, new TreeNode(12, null, null), new TreeNode(20, null, null))
		);
		
		Result<JsonElement> encodeResult = codec.encodeStart(typeProvider, typeProvider.empty(), original);
		assertTrue(encodeResult.isSuccess());
		
		Result<TreeNode> decodeResult = codec.decodeStart(typeProvider, typeProvider.empty(), encodeResult.resultOrThrow());
		assertTrue(decodeResult.isSuccess());
		
		TreeNode decoded = decodeResult.resultOrThrow();
		assertEquals(original, decoded);
	}
	
	@Test
	void toStringRepresentation() {
		Codec<LinkedListNode> codec = Codecs.recursive(self ->
			CodecBuilder.of(
				Codecs.INTEGER.fieldOf("value", LinkedListNode::value),
				self.nullable().fieldOf("next", LinkedListNode::next)
			).create(LinkedListNode::new)
		);
		
		String toString = codec.toString();
		assertTrue(toString.startsWith("RecursiveCodec["));
		assertTrue(toString.endsWith("]"));
	}
	
	/**
	 * A simple recursive data structure representing a linked list node.<br>
	 */
	record LinkedListNode(int value, LinkedListNode next) {}
	
	/**
	 * A simple recursive data structure representing a binary tree node.<br>
	 */
	record TreeNode(int value, TreeNode left, TreeNode right) {}
}
