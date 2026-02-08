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

package net.luis.utils.io.codec.constraint.builder;

import net.luis.utils.io.codec.constraint.config.io.URIPathConstraintConfig;
import net.luis.utils.io.codec.constraint.core.Constraint;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link URIPathConstraintBuilder}.<br>
 *
 * @author Luis-St
 */
class URIPathConstraintBuilderTest {
	
	@Test
	void constructEmpty() {
		URIPathConstraintBuilder builder = new URIPathConstraintBuilder();
		URIPathConstraintConfig config = builder.build();
		
		assertNotNull(config);
		assertEquals(URIPathConstraintConfig.UNCONSTRAINED, config);
	}
	
	@Test
	void constructWithInitialConfig() {
		URIPathConstraintConfig initialConfig = URIPathConstraintConfig.UNCONSTRAINED.withAbsolute();
		URIPathConstraintBuilder builder = new URIPathConstraintBuilder(initialConfig);
		URIPathConstraintConfig config = builder.build();
		
		assertNotNull(config);
		assertEquals(initialConfig, config);
		assertTrue(config.absolute().isPresent());
	}
	
	@Test
	void constructWithNullInitialConfig() {
		assertThrows(NullPointerException.class, () -> new URIPathConstraintBuilder(null));
	}
	
	@Test
	void equalToReturnsBuilder() {
		URIPathConstraintBuilder builder = new URIPathConstraintBuilder();
		assertSame(builder, builder.equalTo("/home/user"));
		assertTrue(builder.build().equalTo().isPresent());
	}
	
	@Test
	void equalToWithNullValue() {
		URIPathConstraintBuilder builder = new URIPathConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.equalTo(null));
	}
	
	@Test
	void notEqualToReturnsBuilder() {
		URIPathConstraintBuilder builder = new URIPathConstraintBuilder();
		assertSame(builder, builder.notEqualTo("/tmp"));
		assertTrue(builder.build().equalTo().isPresent());
	}
	
	@Test
	void notEqualToWithNullValue() {
		URIPathConstraintBuilder builder = new URIPathConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.notEqualTo(null));
	}
	
	@Test
	void inReturnsBuilder() {
		URIPathConstraintBuilder builder = new URIPathConstraintBuilder();
		assertSame(builder, builder.in(List.of("/home", "/var", "/usr")));
		assertTrue(builder.build().in().isPresent());
	}
	
	@Test
	void inWithNullValues() {
		URIPathConstraintBuilder builder = new URIPathConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.in(null));
	}
	
	@Test
	void notInReturnsBuilder() {
		URIPathConstraintBuilder builder = new URIPathConstraintBuilder();
		assertSame(builder, builder.notIn(List.of("/tmp", "/var/tmp")));
		assertTrue(builder.build().in().isPresent());
	}
	
	@Test
	void notInWithNullValues() {
		URIPathConstraintBuilder builder = new URIPathConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.notIn(null));
	}
	
	@Test
	void customReturnsBuilder() {
		URIPathConstraintBuilder builder = new URIPathConstraintBuilder();
		Constraint<String> constraint = value -> {};
		assertSame(builder, builder.custom(constraint));
		assertTrue(builder.build().custom().isPresent());
	}
	
	@Test
	void customWithNullConstraintDoesNotThrowImmediately() {
		URIPathConstraintBuilder builder = new URIPathConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.custom(null));
	}
	
	@Test
	void lengthReturnsBuilder() {
		URIPathConstraintBuilder builder = new URIPathConstraintBuilder();
		assertSame(builder, builder.length(l -> l.minLength(3).maxLength(100)));
		
		URIPathConstraintConfig config = builder.build();
		assertTrue(config.length().isPresent());
		assertTrue(config.length().get().min().isPresent());
		assertTrue(config.length().get().max().isPresent());
		assertEquals(3, config.length().get().min().get().getFirst());
		assertEquals(100, config.length().get().max().get().getFirst());
	}
	
	@Test
	void lengthWithNullBuilder() {
		URIPathConstraintBuilder builder = new URIPathConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.length(null));
	}
	
	@Test
	void depthReturnsBuilder() {
		URIPathConstraintBuilder builder = new URIPathConstraintBuilder();
		assertSame(builder, builder.depth(d -> d.minDepth(1).maxDepth(5)));
		
		URIPathConstraintConfig config = builder.build();
		assertTrue(config.depth().isPresent());
		assertTrue(config.depth().get().min().isPresent());
		assertTrue(config.depth().get().max().isPresent());
		assertEquals(1, config.depth().get().min().get().getFirst());
		assertEquals(5, config.depth().get().max().get().getFirst());
	}
	
	@Test
	void depthWithNullBuilder() {
		URIPathConstraintBuilder builder = new URIPathConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.depth(null));
	}
	
	@Test
	void absoluteReturnsBuilder() {
		URIPathConstraintBuilder builder = new URIPathConstraintBuilder();
		assertSame(builder, builder.absolute());
		assertTrue(builder.build().absolute().isPresent());
	}
	
	@Test
	void relativeReturnsBuilder() {
		URIPathConstraintBuilder builder = new URIPathConstraintBuilder();
		assertSame(builder, builder.relative());
		assertTrue(builder.build().relative().isPresent());
	}
	
	@Test
	void normalizedReturnsBuilder() {
		URIPathConstraintBuilder builder = new URIPathConstraintBuilder();
		assertSame(builder, builder.normalized());
		assertTrue(builder.build().normalized().isPresent());
	}
	
	@Test
	void pathReturnsBuilder() {
		URIPathConstraintBuilder builder = new URIPathConstraintBuilder();
		assertSame(builder, builder.path(p -> p.startsWith("/home")));
		
		URIPathConstraintConfig config = builder.build();
		assertTrue(config.path().isPresent());
	}
	
	@Test
	void pathWithNullBuilder() {
		URIPathConstraintBuilder builder = new URIPathConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.path(null));
	}
	
	@Test
	void segmentReturnsBuilder() {
		URIPathConstraintBuilder builder = new URIPathConstraintBuilder();
		assertSame(builder, builder.segment(s -> s.notBlank()));
		
		URIPathConstraintConfig config = builder.build();
		assertTrue(config.segment().isPresent());
	}
	
	@Test
	void segmentWithNullBuilder() {
		URIPathConstraintBuilder builder = new URIPathConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.segment(null));
	}
	
	@Test
	void fileReturnsBuilder() {
		URIPathConstraintBuilder builder = new URIPathConstraintBuilder();
		assertSame(builder, builder.file(f -> f.endsWith(".txt")));
		
		URIPathConstraintConfig config = builder.build();
		assertTrue(config.file().isPresent());
	}
	
	@Test
	void fileWithNullBuilder() {
		URIPathConstraintBuilder builder = new URIPathConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.file(null));
	}
	
	@Test
	void withoutExtensionReturnsBuilder() {
		URIPathConstraintBuilder builder = new URIPathConstraintBuilder();
		assertSame(builder, builder.withoutExtension());
		assertTrue(builder.build().withoutExtension().isPresent());
	}
	
	@Test
	void extensionReturnsBuilder() {
		URIPathConstraintBuilder builder = new URIPathConstraintBuilder();
		assertSame(builder, builder.extension(e -> e.in(List.of("txt", "md", "xml"))));
		
		URIPathConstraintConfig config = builder.build();
		assertTrue(config.extension().isPresent());
	}
	
	@Test
	void extensionWithNullBuilder() {
		URIPathConstraintBuilder builder = new URIPathConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.extension(null));
	}
	
	@Test
	void ancestorOfSingleReturnsBuilder() {
		URIPathConstraintBuilder builder = new URIPathConstraintBuilder();
		assertSame(builder, builder.ancestorOf("/home/user/documents/file.txt"));
		
		URIPathConstraintConfig config = builder.build();
		assertTrue(config.ancestorOf().isPresent());
		assertEquals(1, config.ancestorOf().get().size());
	}
	
	@Test
	void ancestorOfSingleWithNullPath() {
		URIPathConstraintBuilder builder = new URIPathConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.ancestorOf((String) null));
	}
	
	@Test
	void ancestorOfCollectionReturnsBuilder() {
		URIPathConstraintBuilder builder = new URIPathConstraintBuilder();
		assertSame(builder, builder.ancestorOf(List.of("/home/user/file1.txt", "/home/user/file2.txt")));
		
		URIPathConstraintConfig config = builder.build();
		assertTrue(config.ancestorOf().isPresent());
		assertEquals(2, config.ancestorOf().get().size());
	}
	
	@Test
	void ancestorOfCollectionWithNullPaths() {
		URIPathConstraintBuilder builder = new URIPathConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.ancestorOf((List<String>) null));
	}
	
	@Test
	void descendantOfSingleReturnsBuilder() {
		URIPathConstraintBuilder builder = new URIPathConstraintBuilder();
		assertSame(builder, builder.descendantOf("/home/user"));
		
		URIPathConstraintConfig config = builder.build();
		assertTrue(config.descendantOf().isPresent());
		assertEquals(1, config.descendantOf().get().size());
	}
	
	@Test
	void descendantOfSingleWithNullPath() {
		URIPathConstraintBuilder builder = new URIPathConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.descendantOf((String) null));
	}
	
	@Test
	void descendantOfCollectionReturnsBuilder() {
		URIPathConstraintBuilder builder = new URIPathConstraintBuilder();
		assertSame(builder, builder.descendantOf(List.of("/home", "/var")));
		
		URIPathConstraintConfig config = builder.build();
		assertTrue(config.descendantOf().isPresent());
		assertEquals(2, config.descendantOf().get().size());
	}
	
	@Test
	void descendantOfCollectionWithNullPaths() {
		URIPathConstraintBuilder builder = new URIPathConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.descendantOf((List<String>) null));
	}
	
	@Test
	void buildReturnsCorrectConfig() {
		URIPathConstraintBuilder builder = new URIPathConstraintBuilder();
		builder.absolute().depth(d -> d.minDepth(2).maxDepth(5));
		
		URIPathConstraintConfig config = builder.build();
		
		assertNotNull(config);
		assertTrue(config.absolute().isPresent());
		assertTrue(config.depth().isPresent());
		assertTrue(config.depth().get().min().isPresent());
		assertTrue(config.depth().get().max().isPresent());
	}
	
	@Test
	void methodChainingWorks() {
		URIPathConstraintBuilder builder = new URIPathConstraintBuilder();
		
		URIPathConstraintConfig config = builder
			.absolute()
			.normalized()
			.depth(d -> d.minDepth(1).maxDepth(10))
			.path(p -> p.startsWith("/home"))
			.extension(e -> e.in(List.of("txt", "md")))
			.build();
		
		assertNotNull(config);
		assertTrue(config.absolute().isPresent());
		assertTrue(config.normalized().isPresent());
		assertTrue(config.depth().isPresent());
		assertTrue(config.depth().get().min().isPresent());
		assertTrue(config.depth().get().max().isPresent());
		assertTrue(config.path().isPresent());
		assertTrue(config.extension().isPresent());
	}
}
