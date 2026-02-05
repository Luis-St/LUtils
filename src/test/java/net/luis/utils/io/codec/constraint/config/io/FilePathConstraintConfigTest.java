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

package net.luis.utils.io.codec.constraint.config.io;

import net.luis.utils.io.codec.constraint.config.*;
import net.luis.utils.io.codec.constraint.util.Platform;
import net.luis.utils.io.codec.constraint.util.Unit;
import net.luis.utils.util.Pair;
import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link FilePathConstraintConfig}.<br>
 *
 * @author Luis-St
 */
class FilePathConstraintConfigTest {
	
	@Test
	void constructor() {
		assertDoesNotThrow(() -> FilePathConstraintConfig.UNCONSTRAINED);
	}
	
	@Test
	void constructorNullChecks() {
		assertThrows(NullPointerException.class, () -> new FilePathConstraintConfig(
			null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructorEmptyInSet() {
		assertThrows(IllegalArgumentException.class, () -> new FilePathConstraintConfig(
			Optional.empty(), Optional.of(Pair.of(Set.of(), false)), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructorAbsoluteRelativeMutuallyExclusive() {
		assertThrows(IllegalArgumentException.class, () -> new FilePathConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(Unit.INSTANCE), Optional.of(Unit.INSTANCE), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructorWithoutExtensionAndExtensionMutuallyExclusive() {
		assertThrows(IllegalArgumentException.class, () -> new FilePathConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(Unit.INSTANCE), Optional.of(StringConstraintConfig.UNCONSTRAINED), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void unconstrained() {
		FilePathConstraintConfig config = FilePathConstraintConfig.UNCONSTRAINED;
		assertNotNull(config);
		assertTrue(config.equalTo().isEmpty());
		assertTrue(config.in().isEmpty());
		assertTrue(config.length().isEmpty());
		assertTrue(config.depth().isEmpty());
		assertTrue(config.absolute().isEmpty());
		assertTrue(config.relative().isEmpty());
		assertTrue(config.custom().isEmpty());
	}

	@Test
	void isUnconstrainedWithUnconstrained() {
		assertTrue(FilePathConstraintConfig.UNCONSTRAINED.isUnconstrained());
	}

	@Test
	void isUnconstrainedWithConstraint() {
		FilePathConstraintConfig config = FilePathConstraintConfig.UNCONSTRAINED.withAbsolute();
		assertFalse(config.isUnconstrained());
	}

	@Test
	void withEqualTo() {
		FilePathConstraintConfig config = FilePathConstraintConfig.UNCONSTRAINED.withEqualTo(Path.of("/home/user"));
		
		assertTrue(config.equalTo().isPresent());
		assertEquals(Path.of("/home/user"), config.equalTo().get().getFirst());
		assertFalse(config.equalTo().get().getSecond());
	}
	
	@Test
	void withEqualToNull() {
		assertThrows(NullPointerException.class, () -> FilePathConstraintConfig.UNCONSTRAINED.withEqualTo(null));
	}
	
	@Test
	void withNotEqualTo() {
		FilePathConstraintConfig config = FilePathConstraintConfig.UNCONSTRAINED.withNotEqualTo(Path.of("/tmp"));
		
		assertTrue(config.equalTo().isPresent());
		assertTrue(config.equalTo().get().getSecond());
	}
	
	@Test
	void withIn() {
		FilePathConstraintConfig config = FilePathConstraintConfig.UNCONSTRAINED.withIn(List.of(Path.of("/home"), Path.of("/var")));
		
		assertTrue(config.in().isPresent());
		assertEquals(2, config.in().get().getFirst().size());
		assertFalse(config.in().get().getSecond());
	}
	
	@Test
	void withNotIn() {
		FilePathConstraintConfig config = FilePathConstraintConfig.UNCONSTRAINED.withNotIn(List.of(Path.of("/tmp")));
		
		assertTrue(config.in().isPresent());
		assertTrue(config.in().get().getSecond());
	}
	
	@Test
	void withLength() {
		LengthConstraintConfig lengthConfig = LengthConstraintConfig.UNCONSTRAINED.withMinLength(1).withMaxLength(100);
		FilePathConstraintConfig config = FilePathConstraintConfig.UNCONSTRAINED.withLength(lengthConfig);
		
		assertTrue(config.length().isPresent());
		assertEquals(lengthConfig, config.length().get());
	}
	
	@Test
	void withLengthNull() {
		assertThrows(NullPointerException.class, () -> FilePathConstraintConfig.UNCONSTRAINED.withLength(null));
	}
	
	@Test
	void withDepth() {
		DepthConstraintConfig depthConfig = DepthConstraintConfig.UNCONSTRAINED.withMinDepth(1).withMaxDepth(10);
		FilePathConstraintConfig config = FilePathConstraintConfig.UNCONSTRAINED.withDepth(depthConfig);
		
		assertTrue(config.depth().isPresent());
		assertEquals(depthConfig, config.depth().get());
	}
	
	@Test
	void withDepthNull() {
		assertThrows(NullPointerException.class, () -> FilePathConstraintConfig.UNCONSTRAINED.withDepth(null));
	}
	
	@Test
	void withAbsolute() {
		FilePathConstraintConfig config = FilePathConstraintConfig.UNCONSTRAINED.withAbsolute();
		
		assertTrue(config.absolute().isPresent());
		assertTrue(config.relative().isEmpty());
	}
	
	@Test
	void withRelative() {
		FilePathConstraintConfig config = FilePathConstraintConfig.UNCONSTRAINED.withRelative();
		
		assertTrue(config.relative().isPresent());
		assertTrue(config.absolute().isEmpty());
	}
	
	@Test
	void withNormalized() {
		FilePathConstraintConfig config = FilePathConstraintConfig.UNCONSTRAINED.withNormalized();
		
		assertTrue(config.normalized().isPresent());
	}
	
	@Test
	void withCanonical() {
		FilePathConstraintConfig config = FilePathConstraintConfig.UNCONSTRAINED.withCanonical();
		
		assertTrue(config.canonical().isPresent());
	}
	
	@Test
	void withPath() {
		StringConstraintConfig stringConfig = StringConstraintConfig.UNCONSTRAINED.withStartsWith("/home");
		FilePathConstraintConfig config = FilePathConstraintConfig.UNCONSTRAINED.withPath(stringConfig);
		
		assertTrue(config.path().isPresent());
	}
	
	@Test
	void withPathNull() {
		assertThrows(NullPointerException.class, () -> FilePathConstraintConfig.UNCONSTRAINED.withPath(null));
	}
	
	@Test
	void withRoot() {
		StringConstraintConfig stringConfig = StringConstraintConfig.UNCONSTRAINED.withEqualTo("/");
		FilePathConstraintConfig config = FilePathConstraintConfig.UNCONSTRAINED.withRoot(stringConfig);
		
		assertTrue(config.root().isPresent());
	}
	
	@Test
	void withParent() {
		StringConstraintConfig stringConfig = StringConstraintConfig.UNCONSTRAINED.withContains("home");
		FilePathConstraintConfig config = FilePathConstraintConfig.UNCONSTRAINED.withParent(stringConfig);
		
		assertTrue(config.parent().isPresent());
	}
	
	@Test
	void withSegment() {
		StringConstraintConfig stringConfig = StringConstraintConfig.UNCONSTRAINED.withNotBlank();
		FilePathConstraintConfig config = FilePathConstraintConfig.UNCONSTRAINED.withSegment(stringConfig);
		
		assertTrue(config.segment().isPresent());
	}
	
	@Test
	void withFile() {
		StringConstraintConfig stringConfig = StringConstraintConfig.UNCONSTRAINED.withEndsWith(".txt");
		FilePathConstraintConfig config = FilePathConstraintConfig.UNCONSTRAINED.withFile(stringConfig);
		
		assertTrue(config.file().isPresent());
	}
	
	@Test
	void withWithoutExtension() {
		FilePathConstraintConfig config = FilePathConstraintConfig.UNCONSTRAINED.withWithoutExtension();
		
		assertTrue(config.withoutExtension().isPresent());
		assertTrue(config.extension().isEmpty());
	}
	
	@Test
	void withExtension() {
		StringConstraintConfig stringConfig = StringConstraintConfig.UNCONSTRAINED.withIn(List.of("txt", "md"));
		FilePathConstraintConfig config = FilePathConstraintConfig.UNCONSTRAINED.withExtension(stringConfig);
		
		assertTrue(config.extension().isPresent());
		assertTrue(config.withoutExtension().isEmpty());
	}
	
	@Test
	void withAncestorOf() {
		FilePathConstraintConfig config = FilePathConstraintConfig.UNCONSTRAINED.withAncestorOf(List.of("/home/user/file.txt"));
		
		assertTrue(config.ancestorOf().isPresent());
		assertEquals(1, config.ancestorOf().get().size());
	}
	
	@Test
	void withDescendantOf() {
		FilePathConstraintConfig config = FilePathConstraintConfig.UNCONSTRAINED.withDescendantOf(List.of("/home"));
		
		assertTrue(config.descendantOf().isPresent());
		assertEquals(1, config.descendantOf().get().size());
	}
	
	@Test
	void withValidFor() {
		FilePathConstraintConfig config = FilePathConstraintConfig.UNCONSTRAINED.withValidFor(Platform.LINUX);
		
		assertTrue(config.validFor().isPresent());
		assertEquals(Platform.LINUX, config.validFor().get());
	}
	
	@Test
	void withValidForNull() {
		assertThrows(NullPointerException.class, () -> FilePathConstraintConfig.UNCONSTRAINED.withValidFor(null));
	}
	
	@Test
	void withPortable() {
		FilePathConstraintConfig config = FilePathConstraintConfig.UNCONSTRAINED.withPortable();
		
		assertTrue(config.portable().isPresent());
	}
	
	@Test
	void withSeparator() {
		FilePathConstraintConfig config = FilePathConstraintConfig.UNCONSTRAINED.withSeparator(Platform.WINDOWS);
		
		assertTrue(config.separator().isPresent());
		assertEquals(Platform.WINDOWS, config.separator().get());
	}
	
	@Test
	void withCustom() {
		FilePathConstraintConfig config = FilePathConstraintConfig.UNCONSTRAINED.withCustom(value -> Result.success());
		
		assertTrue(config.custom().isPresent());
	}
	
	@Test
	void withCustomNull() {
		assertThrows(NullPointerException.class, () -> FilePathConstraintConfig.UNCONSTRAINED.withCustom(null));
	}
	
	@Test
	void matchesUnconstrained() {
		FilePathConstraintConfig config = FilePathConstraintConfig.UNCONSTRAINED;
		
		assertTrue(config.matches(Path.of("/home/user")).isSuccess());
		assertTrue(config.matches(Path.of("relative/path")).isSuccess());
		assertTrue(config.matches(Path.of("file.txt")).isSuccess());
	}
	
	@Test
	void matchesWithNull() {
		assertThrows(NullPointerException.class, () -> FilePathConstraintConfig.UNCONSTRAINED.matches(null));
	}
	
	@Test
	void matchesEqualTo() {
		FilePathConstraintConfig config = FilePathConstraintConfig.UNCONSTRAINED.withEqualTo(Path.of("/home/user"));
		
		assertTrue(config.matches(Path.of("/home/user")).isSuccess());
		assertTrue(config.matches(Path.of("/home/other")).isError());
	}
	
	@Test
	void matchesIn() {
		FilePathConstraintConfig config = FilePathConstraintConfig.UNCONSTRAINED.withIn(List.of(Path.of("/home"), Path.of("/var")));
		
		assertTrue(config.matches(Path.of("/home")).isSuccess());
		assertTrue(config.matches(Path.of("/var")).isSuccess());
		assertTrue(config.matches(Path.of("/tmp")).isError());
	}
	
	@Test
	void matchesLength() {
		LengthConstraintConfig lengthConfig = LengthConstraintConfig.UNCONSTRAINED.withMinLength(5).withMaxLength(20);
		FilePathConstraintConfig config = FilePathConstraintConfig.UNCONSTRAINED.withLength(lengthConfig);
		
		assertTrue(config.matches(Path.of("/home/user")).isSuccess());
		assertTrue(config.matches(Path.of("/a")).isError());
	}
	
	@Test
	void matchesDepth() {
		DepthConstraintConfig depthConfig = DepthConstraintConfig.UNCONSTRAINED.withMinDepth(2).withMaxDepth(4);
		FilePathConstraintConfig config = FilePathConstraintConfig.UNCONSTRAINED.withDepth(depthConfig);
		
		assertTrue(config.matches(Path.of("a/b")).isSuccess());
		assertTrue(config.matches(Path.of("a/b/c")).isSuccess());
		assertTrue(config.matches(Path.of("a")).isError());
		assertTrue(config.matches(Path.of("a/b/c/d/e")).isError());
	}
}
