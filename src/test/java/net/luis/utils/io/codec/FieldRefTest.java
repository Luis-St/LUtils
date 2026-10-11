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

import net.luis.utils.io.codec.encoder.EncoderException;
import net.luis.utils.io.codec.provider.BinaryTypeProvider;
import net.luis.utils.io.data.binary.BinaryElement;
import net.luis.utils.io.data.binary.BinaryStruct;
import org.junit.jupiter.api.Test;

import java.util.*;

import static net.luis.utils.io.codec.Codecs.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link FieldRef}.<br>
 *
 * @author Luis-St
 */
class FieldRefTest {
	
	@Test
	void constructWithNameAliasesAndIndex() {
		FieldRef field = new FieldRef("field", Set.of("alias"), 2);
		
		assertEquals("field", field.name());
		assertEquals(Set.of("alias"), field.aliases());
		assertEquals(2, field.index());
		assertTrue(field.isIndexed());
	}
	
	@Test
	void constructWithNameOnly() {
		FieldRef field = new FieldRef("field");
		
		assertEquals("field", field.name());
		assertTrue(field.aliases().isEmpty());
		assertEquals(FieldRef.NO_INDEX, field.index());
		assertFalse(field.isIndexed());
	}
	
	@Test
	void constructWithNameAndAliases() {
		FieldRef field = new FieldRef("field", Set.of("a", "b"));
		
		assertEquals(Set.of("a", "b"), field.aliases());
		assertEquals(-1, field.index());
		assertFalse(field.isIndexed());
	}
	
	@Test
	void constructWithNullName() {
		assertThrows(NullPointerException.class, () -> new FieldRef(null, Set.of(), 0));
	}
	
	@Test
	void constructWithNullNameInShortConstructors() {
		assertThrows(NullPointerException.class, () -> new FieldRef(null));
		assertThrows(NullPointerException.class, () -> new FieldRef(null, Set.of()));
	}
	
	@Test
	void constructWithNullAliases() {
		assertThrows(NullPointerException.class, () -> new FieldRef("field", null, 0));
		assertThrows(NullPointerException.class, () -> new FieldRef("field", null));
	}
	
	@Test
	void constructWithEmptyName() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new FieldRef("", Set.of(), 0));
		
		assertTrue(exception.getMessage().contains("must not be empty"));
	}
	
	@Test
	void constructWithEmptyNameInShortConstructors() {
		assertThrows(IllegalArgumentException.class, () -> new FieldRef(""));
		assertThrows(IllegalArgumentException.class, () -> new FieldRef("", Set.of()));
	}
	
	@Test
	void constructWithIndexBelowNoIndex() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new FieldRef("field", Set.of(), -2));
		assertTrue(exception.getMessage().contains("-1"));
		assertTrue(exception.getMessage().contains("-2"));
		
		assertThrows(IllegalArgumentException.class, () -> new FieldRef("field", Set.of(), Integer.MIN_VALUE));
	}
	
	@Test
	void constructWithNullAliasElement() {
		Set<String> aliases = new HashSet<>();
		aliases.add(null);
		
		assertThrows(NullPointerException.class, () -> new FieldRef("field", aliases, 0));
	}
	
	@Test
	void withIndexBelowNoIndex() {
		FieldRef field = new FieldRef("field");
		
		assertThrows(IllegalArgumentException.class, () -> field.withIndex(-2));
	}
	
	@Test
	void constructWithNoIndexBoundary() {
		FieldRef field = assertDoesNotThrow(() -> new FieldRef("field", Set.of(), -1));
		
		assertEquals(-1, field.index());
		assertFalse(field.isIndexed());
	}
	
	@Test
	void constructWithZeroIndex() {
		FieldRef field = new FieldRef("field", Set.of(), 0);
		
		assertEquals(0, field.index());
		assertTrue(field.isIndexed());
	}
	
	@Test
	void constructWithNonEmptyName() {
		assertDoesNotThrow(() -> new FieldRef("a", Set.of(), 0));
	}
	
	@Test
	void isIndexedWithAssignedIndex() {
		assertTrue(new FieldRef("a", Set.of(), 3).isIndexed());
	}
	
	@Test
	void isIndexedWithoutIndex() {
		assertFalse(new FieldRef("a").isIndexed());
	}
	
	@Test
	void constructWithEmptyAliases() {
		FieldRef field = assertDoesNotThrow(() -> new FieldRef("field", Set.of(), 0));
		
		assertTrue(field.aliases().isEmpty());
	}
	
	@Test
	void constructWithMultipleAliases() {
		FieldRef field = new FieldRef("field", Set.of("a", "b", "c"), 0);
		
		assertEquals(Set.of("a", "b", "c"), field.aliases());
	}
	
	@Test
	void withIndexAssignsIndex() {
		FieldRef field = new FieldRef("field", Set.of("alias"));
		FieldRef indexed = field.withIndex(4);
		
		assertEquals(4, indexed.index());
		assertEquals("field", indexed.name());
		assertEquals(Set.of("alias"), indexed.aliases());
	}
	
	@Test
	void withIndexReturnsNewInstance() {
		FieldRef field = new FieldRef("field");
		
		assertNotSame(field, field.withIndex(1));
		assertEquals(FieldRef.NO_INDEX, field.index());
	}
	
	@Test
	void withIndexResetsToNoIndex() {
		FieldRef field = new FieldRef("field", Set.of(), 3);
		
		assertFalse(field.withIndex(FieldRef.NO_INDEX).isIndexed());
	}
	
	@Test
	void withIndexOverwritesExistingIndex() {
		FieldRef field = new FieldRef("field", Set.of("alias"), 1);
		FieldRef updated = field.withIndex(5);
		
		assertEquals(5, updated.index());
		assertEquals("field", updated.name());
		assertEquals(Set.of("alias"), updated.aliases());
	}
	
	@Test
	void aliasesAreUnmodifiable() {
		FieldRef field = new FieldRef("field", Set.of("a"));
		
		assertThrows(UnsupportedOperationException.class, () -> field.aliases().add("x"));
	}
	
	@Test
	void aliasesAreDefensivelyCopied() {
		Set<String> source = new HashSet<>(Set.of("a"));
		FieldRef field = new FieldRef("field", source);
		
		source.add("b");
		
		assertEquals(Set.of("a"), field.aliases());
	}
	
	@Test
	void noIndexConstantValue() {
		assertEquals(-1, FieldRef.NO_INDEX);
	}
	
	@Test
	void equalsAndHashCode() {
		FieldRef field = new FieldRef("field", Set.of("alias"), 1);
		FieldRef same = new FieldRef("field", Set.of("alias"), 1);
		
		assertEquals(field, same);
		assertEquals(field.hashCode(), same.hashCode());
		
		assertNotEquals(field, new FieldRef("other", Set.of("alias"), 1));
		assertNotEquals(field, new FieldRef("field", Set.of("different"), 1));
		assertNotEquals(field, new FieldRef("field", Set.of("alias"), 2));
		assertNotEquals(new FieldRef("a"), new FieldRef("a").withIndex(0));
	}
	
	@Test
	void toStringRepresentation() {
		String result = new FieldRef("field", Set.of("alias"), 2).toString();
		
		assertTrue(result.contains("field"));
		assertTrue(result.contains("alias"));
		assertTrue(result.contains("2"));
	}
	
	@Test
	void withIndexChainedRepeatedly() {
		FieldRef field = new FieldRef("field");
		
		assertEquals(0, field.withIndex(1).withIndex(2).withIndex(0).index());
		assertEquals(FieldRef.NO_INDEX, field.index());
	}
	
	@Test
	void fieldRefUsedByCodecGroupIndexing() throws Exception {
		List<FieldCodec<?, TestObject>> codecs = List.of(
			STRING.fieldOf("name", TestObject::name),
			INTEGER.fieldOf("value", TestObject::value),
			BOOLEAN.fieldOf("flag", TestObject::flag)
		);
		CodecGroup<TestObject> group = new CodecGroup<>(codecs, components -> new TestObject((String) components.getFirst(), (Integer) components.get(1), (Boolean) components.get(2)));
		
		BinaryTypeProvider provider = BinaryTypeProvider.INSTANCE;
		BinaryElement encoded = group.encode(provider, provider.empty(), new TestObject("test", 42, true));
		
		BinaryStruct struct = encoded.getAsBinaryStruct();
		assertEquals(3, struct.size());
		assertEquals("test", struct.getAsString(0));
		assertEquals(42, struct.getAsInteger(1));
		assertTrue(struct.getAsBoolean(2));
	}
	
	@Test
	void indexedAndUnindexedRefsBehaveDifferentlyInBinaryProvider() {
		BinaryTypeProvider provider = BinaryTypeProvider.INSTANCE;
		BinaryStruct struct = new BinaryStruct(2);
		
		FieldRef unindexed = new FieldRef("name");
		EncoderException exception = assertThrows(EncoderException.class, () -> provider.setField(struct, unindexed, new net.luis.utils.io.data.binary.BinaryPrimitive(1), EncoderException::new));
		assertTrue(exception.getMessage().contains("is not indexed"));
		
		FieldRef indexed = unindexed.withIndex(0);
		assertDoesNotThrow(() -> provider.setField(struct, indexed, new net.luis.utils.io.data.binary.BinaryPrimitive(1), EncoderException::new));
		assertEquals(1, struct.getAsInteger(0));
	}
	
	private record TestObject(String name, int value, boolean flag) {}
}
