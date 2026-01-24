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

import net.luis.utils.io.codec.constraint.config.io.PathConstraintConfig;
import net.luis.utils.io.codec.constraint.core.Constraint;
import net.luis.utils.io.codec.constraint.config.LengthConstraintConfig;
import net.luis.utils.io.codec.constraint.util.Platform;
import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link PathConstraintBuilder}.<br>
 *
 * @author Luis-St
 */
class PathConstraintBuilderTest {
	
	@Test
	void constructEmpty() {
		PathConstraintBuilder builder = new PathConstraintBuilder();
		PathConstraintConfig config = builder.build();
		
		assertNotNull(config);
		assertEquals(PathConstraintConfig.UNCONSTRAINED, config);
	}
	
	@Test
	void constructWithInitialConfig() {
		PathConstraintConfig initialConfig = PathConstraintConfig.UNCONSTRAINED.withLength(
			LengthConstraintConfig.UNCONSTRAINED.withMinLength(1)
		);
		PathConstraintBuilder builder = new PathConstraintBuilder(initialConfig);
		PathConstraintConfig config = builder.build();
		
		assertNotNull(config);
		assertEquals(initialConfig, config);
		assertTrue(config.length().isPresent());
	}
	
	@Test
	void constructWithNullInitialConfig() {
		assertThrows(NullPointerException.class, () -> new PathConstraintBuilder(null));
	}
	
	@Test
	void equalToReturnsBuilder() {
		PathConstraintBuilder builder = new PathConstraintBuilder();
		assertSame(builder, builder.equalTo("/home/user"));
		assertTrue(builder.build().equalTo().isPresent());
	}
	
	@Test
	void equalToWithNullValue() {
		PathConstraintBuilder builder = new PathConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.equalTo(null));
	}
	
	@Test
	void notEqualToReturnsBuilder() {
		PathConstraintBuilder builder = new PathConstraintBuilder();
		assertSame(builder, builder.notEqualTo("/home/user"));
		assertTrue(builder.build().equalTo().isPresent());
	}
	
	@Test
	void notEqualToWithNullValue() {
		PathConstraintBuilder builder = new PathConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.notEqualTo(null));
	}
	
	@Test
	void inReturnsBuilder() {
		PathConstraintBuilder builder = new PathConstraintBuilder();
		assertSame(builder, builder.in(List.of("/home", "/tmp")));
		assertTrue(builder.build().in().isPresent());
	}
	
	@Test
	void inWithNullValues() {
		PathConstraintBuilder builder = new PathConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.in(null));
	}
	
	@Test
	void notInReturnsBuilder() {
		PathConstraintBuilder builder = new PathConstraintBuilder();
		assertSame(builder, builder.notIn(List.of("/home", "/tmp")));
		assertTrue(builder.build().in().isPresent());
	}
	
	@Test
	void notInWithNullValues() {
		PathConstraintBuilder builder = new PathConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.notIn(null));
	}
	
	@Test
	void customReturnsBuilder() {
		PathConstraintBuilder builder = new PathConstraintBuilder();
		Constraint<String> constraint = value -> Result.success(null);
		assertSame(builder, builder.custom(constraint));
		assertTrue(builder.build().custom().isPresent());
	}
	
	@Test
	void customWithNullConstraintDoesNotThrowImmediately() {
		PathConstraintBuilder builder = new PathConstraintBuilder();
		// The builder wraps the constraint in a lambda, so null is not validated immediately
		// NPE would occur when the constraint is actually used during matching
		assertDoesNotThrow(() -> builder.custom(null));
	}
	
	@Test
	void lengthReturnsBuilder() {
		PathConstraintBuilder builder = new PathConstraintBuilder();
		assertSame(builder, builder.length(l -> l.minLength(1).maxLength(100)));
		assertTrue(builder.build().length().isPresent());
	}
	
	@Test
	void lengthWithNullBuilder() {
		PathConstraintBuilder builder = new PathConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.length(null));
	}
	
	@Test
	void depthReturnsBuilder() {
		PathConstraintBuilder builder = new PathConstraintBuilder();
		assertSame(builder, builder.depth(d -> d.minDepth(1).maxDepth(10)));
		assertTrue(builder.build().depth().isPresent());
	}
	
	@Test
	void depthWithNullBuilder() {
		PathConstraintBuilder builder = new PathConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.depth(null));
	}
	
	@Test
	void absoluteReturnsBuilder() {
		PathConstraintBuilder builder = new PathConstraintBuilder();
		assertSame(builder, builder.absolute());
		assertTrue(builder.build().absolute().isPresent());
	}
	
	@Test
	void relativeReturnsBuilder() {
		PathConstraintBuilder builder = new PathConstraintBuilder();
		assertSame(builder, builder.relative());
		assertTrue(builder.build().relative().isPresent());
	}
	
	@Test
	void normalizedReturnsBuilder() {
		PathConstraintBuilder builder = new PathConstraintBuilder();
		assertSame(builder, builder.normalized());
		assertTrue(builder.build().normalized().isPresent());
	}
	
	@Test
	void canonicalReturnsBuilder() {
		PathConstraintBuilder builder = new PathConstraintBuilder();
		assertSame(builder, builder.canonical());
		assertTrue(builder.build().canonical().isPresent());
	}
	
	@Test
	void pathReturnsBuilder() {
		PathConstraintBuilder builder = new PathConstraintBuilder();
		assertSame(builder, builder.path(b -> b.notBlank()));
		assertTrue(builder.build().path().isPresent());
	}
	
	@Test
	void pathWithNullBuilder() {
		PathConstraintBuilder builder = new PathConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.path(null));
	}
	
	@Test
	void rootReturnsBuilder() {
		PathConstraintBuilder builder = new PathConstraintBuilder();
		assertSame(builder, builder.root(b -> b.equalTo("/")));
		assertTrue(builder.build().root().isPresent());
	}
	
	@Test
	void rootWithNullBuilder() {
		PathConstraintBuilder builder = new PathConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.root(null));
	}
	
	@Test
	void parentReturnsBuilder() {
		PathConstraintBuilder builder = new PathConstraintBuilder();
		assertSame(builder, builder.parent(b -> b.notBlank()));
		assertTrue(builder.build().parent().isPresent());
	}
	
	@Test
	void parentWithNullBuilder() {
		PathConstraintBuilder builder = new PathConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.parent(null));
	}
	
	@Test
	void segmentReturnsBuilder() {
		PathConstraintBuilder builder = new PathConstraintBuilder();
		assertSame(builder, builder.segment(b -> b.notBlank()));
		assertTrue(builder.build().segment().isPresent());
	}
	
	@Test
	void segmentWithNullBuilder() {
		PathConstraintBuilder builder = new PathConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.segment(null));
	}
	
	@Test
	void fileReturnsBuilder() {
		PathConstraintBuilder builder = new PathConstraintBuilder();
		assertSame(builder, builder.file(b -> b.notBlank()));
		assertTrue(builder.build().file().isPresent());
	}
	
	@Test
	void fileWithNullBuilder() {
		PathConstraintBuilder builder = new PathConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.file(null));
	}
	
	@Test
	void withoutExtensionReturnsBuilder() {
		PathConstraintBuilder builder = new PathConstraintBuilder();
		assertSame(builder, builder.withoutExtension());
		assertTrue(builder.build().withoutExtension().isPresent());
	}
	
	@Test
	void extensionReturnsBuilder() {
		PathConstraintBuilder builder = new PathConstraintBuilder();
		assertSame(builder, builder.extension(b -> b.equalTo("txt")));
		assertTrue(builder.build().extension().isPresent());
	}
	
	@Test
	void extensionWithNullBuilder() {
		PathConstraintBuilder builder = new PathConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.extension(null));
	}
	
	@Test
	void ancestorOfSingleReturnsBuilder() {
		PathConstraintBuilder builder = new PathConstraintBuilder();
		assertSame(builder, builder.ancestorOf("/home/user/file.txt"));
		assertTrue(builder.build().ancestorOf().isPresent());
	}
	
	@Test
	void ancestorOfSingleWithNullPath() {
		PathConstraintBuilder builder = new PathConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.ancestorOf((String) null));
	}
	
	@Test
	void ancestorOfCollectionReturnsBuilder() {
		PathConstraintBuilder builder = new PathConstraintBuilder();
		assertSame(builder, builder.ancestorOf(List.of("/home/user/file.txt", "/tmp/test.txt")));
		assertTrue(builder.build().ancestorOf().isPresent());
	}
	
	@Test
	void ancestorOfCollectionWithNullPaths() {
		PathConstraintBuilder builder = new PathConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.ancestorOf((List<String>) null));
	}
	
	@Test
	void descendantOfSingleReturnsBuilder() {
		PathConstraintBuilder builder = new PathConstraintBuilder();
		assertSame(builder, builder.descendantOf("/home"));
		assertTrue(builder.build().descendantOf().isPresent());
	}
	
	@Test
	void descendantOfSingleWithNullPath() {
		PathConstraintBuilder builder = new PathConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.descendantOf((String) null));
	}
	
	@Test
	void descendantOfCollectionReturnsBuilder() {
		PathConstraintBuilder builder = new PathConstraintBuilder();
		assertSame(builder, builder.descendantOf(List.of("/home", "/tmp")));
		assertTrue(builder.build().descendantOf().isPresent());
	}
	
	@Test
	void descendantOfCollectionWithNullPaths() {
		PathConstraintBuilder builder = new PathConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.descendantOf((List<String>) null));
	}
	
	@Test
	void validForReturnsBuilder() {
		PathConstraintBuilder builder = new PathConstraintBuilder();
		assertSame(builder, builder.validFor(Platform.LINUX));
		assertTrue(builder.build().validFor().isPresent());
	}
	
	@Test
	void validForWithNullPlatform() {
		PathConstraintBuilder builder = new PathConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.validFor(null));
	}
	
	@Test
	void portableReturnsBuilder() {
		PathConstraintBuilder builder = new PathConstraintBuilder();
		assertSame(builder, builder.portable());
		assertTrue(builder.build().portable().isPresent());
	}
	
	@Test
	void separatorReturnsBuilder() {
		PathConstraintBuilder builder = new PathConstraintBuilder();
		assertSame(builder, builder.separator(Platform.LINUX));
		assertTrue(builder.build().separator().isPresent());
	}
	
	@Test
	void separatorWithNullPlatform() {
		PathConstraintBuilder builder = new PathConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.separator(null));
	}
	
	@Test
	void buildReturnsCorrectConfig() {
		PathConstraintBuilder builder = new PathConstraintBuilder();
		builder.length(l -> l.minLength(1).maxLength(4096)).absolute();
		
		PathConstraintConfig config = builder.build();
		
		assertNotNull(config);
		assertTrue(config.length().isPresent());
		assertTrue(config.absolute().isPresent());
	}
	
	@Test
	void methodChainingWorks() {
		PathConstraintBuilder builder = new PathConstraintBuilder();
		
		PathConstraintConfig config = builder
			.length(l -> l.minLength(1).maxLength(4096))
			.depth(d -> d.minDepth(1).maxDepth(10))
			.absolute()
			.normalized()
			.extension(b -> b.in(List.of("txt", "json")))
			.build();
		
		assertNotNull(config);
		assertTrue(config.length().isPresent());
		assertTrue(config.depth().isPresent());
		assertTrue(config.absolute().isPresent());
		assertTrue(config.normalized().isPresent());
		assertTrue(config.extension().isPresent());
	}
}
