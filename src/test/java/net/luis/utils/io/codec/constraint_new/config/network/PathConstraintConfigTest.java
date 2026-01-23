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

package net.luis.utils.io.codec.constraint_new.config.network;

import net.luis.utils.io.codec.constraint_new.config.StringConstraintConfig;
import net.luis.utils.io.codec.constraint_new.core.Platform;
import net.luis.utils.io.codec.constraint_new.core.Unit;
import net.luis.utils.util.Pair;
import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link PathConstraintConfig}.<br>
 *
 * @author Luis-St
 */
class PathConstraintConfigTest {
	
	@Test
	void constructor() {
		assertDoesNotThrow(() -> PathConstraintConfig.UNCONSTRAINED);
	}
	
	@Test
	void constructorNullChecks() {
		assertThrows(NullPointerException.class, () -> new PathConstraintConfig(
			null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructorEmptyInSet() {
		assertThrows(IllegalArgumentException.class, () -> new PathConstraintConfig(
			Optional.empty(), Optional.of(Pair.of(Set.of(), false)), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructorMinMaxLengthValidation() {
		assertThrows(IllegalArgumentException.class, () -> new PathConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.of(Pair.of(100, true)), Optional.of(Pair.of(50, true)), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructorMinMaxDepthValidation() {
		assertThrows(IllegalArgumentException.class, () -> new PathConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(Pair.of(10, true)), Optional.of(Pair.of(5, true)), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructorAbsoluteRelativeMutuallyExclusive() {
		assertThrows(IllegalArgumentException.class, () -> new PathConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(Unit.INSTANCE), Optional.of(Unit.INSTANCE), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructorWithoutExtensionAndExtensionMutuallyExclusive() {
		assertThrows(IllegalArgumentException.class, () -> new PathConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(Unit.INSTANCE), Optional.of(StringConstraintConfig.UNCONSTRAINED), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void unconstrained() {
		PathConstraintConfig config = PathConstraintConfig.UNCONSTRAINED;
		assertNotNull(config);
		assertTrue(config.equalTo().isEmpty());
		assertTrue(config.in().isEmpty());
		assertTrue(config.absolute().isEmpty());
		assertTrue(config.relative().isEmpty());
		assertTrue(config.custom().isEmpty());
	}
	
	@Test
	void withEqualTo() {
		PathConstraintConfig config = PathConstraintConfig.UNCONSTRAINED.withEqualTo(Path.of("/home/user"));
		
		assertTrue(config.equalTo().isPresent());
		assertEquals(Path.of("/home/user"), config.equalTo().get().getFirst());
		assertFalse(config.equalTo().get().getSecond());
	}
	
	@Test
	void withEqualToNull() {
		assertThrows(NullPointerException.class, () -> PathConstraintConfig.UNCONSTRAINED.withEqualTo(null));
	}
	
	@Test
	void withNotEqualTo() {
		PathConstraintConfig config = PathConstraintConfig.UNCONSTRAINED.withNotEqualTo(Path.of("/tmp"));
		
		assertTrue(config.equalTo().isPresent());
		assertTrue(config.equalTo().get().getSecond());
	}
	
	@Test
	void withIn() {
		PathConstraintConfig config = PathConstraintConfig.UNCONSTRAINED.withIn(List.of(Path.of("/home"), Path.of("/var")));
		
		assertTrue(config.in().isPresent());
		assertEquals(2, config.in().get().getFirst().size());
		assertFalse(config.in().get().getSecond());
	}
	
	@Test
	void withNotIn() {
		PathConstraintConfig config = PathConstraintConfig.UNCONSTRAINED.withNotIn(List.of(Path.of("/tmp")));
		
		assertTrue(config.in().isPresent());
		assertTrue(config.in().get().getSecond());
	}
	
	@Test
	void withMinLength() {
		PathConstraintConfig config = PathConstraintConfig.UNCONSTRAINED.withMinLength(5);
		
		assertTrue(config.minLength().isPresent());
		assertEquals(5, config.minLength().get().getFirst());
	}
	
	@Test
	void withMaxLength() {
		PathConstraintConfig config = PathConstraintConfig.UNCONSTRAINED.withMaxLength(255);
		
		assertTrue(config.maxLength().isPresent());
		assertEquals(255, config.maxLength().get().getFirst());
	}
	
	@Test
	void withExactLength() {
		PathConstraintConfig config = PathConstraintConfig.UNCONSTRAINED.withExactLength(10);
		
		assertTrue(config.minLength().isPresent());
		assertTrue(config.maxLength().isPresent());
		assertEquals(10, config.minLength().get().getFirst());
		assertEquals(10, config.maxLength().get().getFirst());
	}
	
	@Test
	void withLengthBetween() {
		PathConstraintConfig config = PathConstraintConfig.UNCONSTRAINED.withLengthBetween(5, 100);
		
		assertTrue(config.minLength().isPresent());
		assertTrue(config.maxLength().isPresent());
		assertEquals(5, config.minLength().get().getFirst());
		assertEquals(100, config.maxLength().get().getFirst());
	}
	
	@Test
	void withMinDepth() {
		PathConstraintConfig config = PathConstraintConfig.UNCONSTRAINED.withMinDepth(2);
		
		assertTrue(config.minDepth().isPresent());
		assertEquals(2, config.minDepth().get().getFirst());
	}
	
	@Test
	void withMaxDepth() {
		PathConstraintConfig config = PathConstraintConfig.UNCONSTRAINED.withMaxDepth(10);
		
		assertTrue(config.maxDepth().isPresent());
		assertEquals(10, config.maxDepth().get().getFirst());
	}
	
	@Test
	void withExactDepth() {
		PathConstraintConfig config = PathConstraintConfig.UNCONSTRAINED.withExactDepth(3);
		
		assertTrue(config.minDepth().isPresent());
		assertTrue(config.maxDepth().isPresent());
		assertEquals(3, config.minDepth().get().getFirst());
		assertEquals(3, config.maxDepth().get().getFirst());
	}
	
	@Test
	void withDepthBetween() {
		PathConstraintConfig config = PathConstraintConfig.UNCONSTRAINED.withDepthBetween(1, 5);
		
		assertTrue(config.minDepth().isPresent());
		assertTrue(config.maxDepth().isPresent());
		assertEquals(1, config.minDepth().get().getFirst());
		assertEquals(5, config.maxDepth().get().getFirst());
	}
	
	@Test
	void withAbsolute() {
		PathConstraintConfig config = PathConstraintConfig.UNCONSTRAINED.withAbsolute();
		
		assertTrue(config.absolute().isPresent());
		assertTrue(config.relative().isEmpty());
	}
	
	@Test
	void withRelative() {
		PathConstraintConfig config = PathConstraintConfig.UNCONSTRAINED.withRelative();
		
		assertTrue(config.relative().isPresent());
		assertTrue(config.absolute().isEmpty());
	}
	
	@Test
	void withNormalized() {
		PathConstraintConfig config = PathConstraintConfig.UNCONSTRAINED.withNormalized();
		
		assertTrue(config.normalized().isPresent());
	}
	
	@Test
	void withCanonical() {
		PathConstraintConfig config = PathConstraintConfig.UNCONSTRAINED.withCanonical();
		
		assertTrue(config.canonical().isPresent());
	}
	
	@Test
	void withPath() {
		StringConstraintConfig stringConfig = StringConstraintConfig.UNCONSTRAINED.withStartsWith("/home");
		PathConstraintConfig config = PathConstraintConfig.UNCONSTRAINED.withPath(stringConfig);
		
		assertTrue(config.path().isPresent());
	}
	
	@Test
	void withPathNull() {
		assertThrows(NullPointerException.class, () -> PathConstraintConfig.UNCONSTRAINED.withPath(null));
	}
	
	@Test
	void withRoot() {
		StringConstraintConfig stringConfig = StringConstraintConfig.UNCONSTRAINED.withEqualTo("/");
		PathConstraintConfig config = PathConstraintConfig.UNCONSTRAINED.withRoot(stringConfig);
		
		assertTrue(config.root().isPresent());
	}
	
	@Test
	void withParent() {
		StringConstraintConfig stringConfig = StringConstraintConfig.UNCONSTRAINED.withContains("home");
		PathConstraintConfig config = PathConstraintConfig.UNCONSTRAINED.withParent(stringConfig);
		
		assertTrue(config.parent().isPresent());
	}
	
	@Test
	void withSegment() {
		StringConstraintConfig stringConfig = StringConstraintConfig.UNCONSTRAINED.withNotBlank();
		PathConstraintConfig config = PathConstraintConfig.UNCONSTRAINED.withSegment(stringConfig);
		
		assertTrue(config.segment().isPresent());
	}
	
	@Test
	void withFile() {
		StringConstraintConfig stringConfig = StringConstraintConfig.UNCONSTRAINED.withEndsWith(".txt");
		PathConstraintConfig config = PathConstraintConfig.UNCONSTRAINED.withFile(stringConfig);
		
		assertTrue(config.file().isPresent());
	}
	
	@Test
	void withWithoutExtension() {
		PathConstraintConfig config = PathConstraintConfig.UNCONSTRAINED.withWithoutExtension();
		
		assertTrue(config.withoutExtension().isPresent());
		assertTrue(config.extension().isEmpty());
	}
	
	@Test
	void withExtension() {
		StringConstraintConfig stringConfig = StringConstraintConfig.UNCONSTRAINED.withIn(List.of("txt", "md"));
		PathConstraintConfig config = PathConstraintConfig.UNCONSTRAINED.withExtension(stringConfig);
		
		assertTrue(config.extension().isPresent());
		assertTrue(config.withoutExtension().isEmpty());
	}
	
	@Test
	void withAncestorOf() {
		PathConstraintConfig config = PathConstraintConfig.UNCONSTRAINED.withAncestorOf(List.of("/home/user/file.txt"));
		
		assertTrue(config.ancestorOf().isPresent());
		assertEquals(1, config.ancestorOf().get().size());
	}
	
	@Test
	void withDescendantOf() {
		PathConstraintConfig config = PathConstraintConfig.UNCONSTRAINED.withDescendantOf(List.of("/home"));
		
		assertTrue(config.descendantOf().isPresent());
		assertEquals(1, config.descendantOf().get().size());
	}
	
	@Test
	void withValidFor() {
		PathConstraintConfig config = PathConstraintConfig.UNCONSTRAINED.withValidFor(Platform.LINUX);
		
		assertTrue(config.validFor().isPresent());
		assertEquals(Platform.LINUX, config.validFor().get());
	}
	
	@Test
	void withValidForNull() {
		assertThrows(NullPointerException.class, () -> PathConstraintConfig.UNCONSTRAINED.withValidFor(null));
	}
	
	@Test
	void withPortable() {
		PathConstraintConfig config = PathConstraintConfig.UNCONSTRAINED.withPortable();
		
		assertTrue(config.portable().isPresent());
	}
	
	@Test
	void withSeparator() {
		PathConstraintConfig config = PathConstraintConfig.UNCONSTRAINED.withSeparator(Platform.WINDOWS);
		
		assertTrue(config.separator().isPresent());
		assertEquals(Platform.WINDOWS, config.separator().get());
	}
	
	@Test
	void withCustom() {
		PathConstraintConfig config = PathConstraintConfig.UNCONSTRAINED.withCustom(value -> Result.success());
		
		assertTrue(config.custom().isPresent());
	}
	
	@Test
	void withCustomNull() {
		assertThrows(NullPointerException.class, () -> PathConstraintConfig.UNCONSTRAINED.withCustom(null));
	}
	
	@Test
	void matchesUnconstrained() {
		PathConstraintConfig config = PathConstraintConfig.UNCONSTRAINED;
		
		assertTrue(config.matches(Path.of("/home/user")).isSuccess());
		assertTrue(config.matches(Path.of("relative/path")).isSuccess());
		assertTrue(config.matches(Path.of("file.txt")).isSuccess());
	}
	
	@Test
	void matchesWithNull() {
		assertThrows(NullPointerException.class, () -> PathConstraintConfig.UNCONSTRAINED.matches(null));
	}
	
	@Test
	void matchesEqualTo() {
		PathConstraintConfig config = PathConstraintConfig.UNCONSTRAINED.withEqualTo(Path.of("/home/user"));
		
		assertTrue(config.matches(Path.of("/home/user")).isSuccess());
		assertTrue(config.matches(Path.of("/home/other")).isError());
	}
	
	@Test
	void matchesIn() {
		PathConstraintConfig config = PathConstraintConfig.UNCONSTRAINED.withIn(List.of(Path.of("/home"), Path.of("/var")));
		
		assertTrue(config.matches(Path.of("/home")).isSuccess());
		assertTrue(config.matches(Path.of("/var")).isSuccess());
		assertTrue(config.matches(Path.of("/tmp")).isError());
	}
}
