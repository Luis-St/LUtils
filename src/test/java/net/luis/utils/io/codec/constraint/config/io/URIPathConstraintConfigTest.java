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
import net.luis.utils.io.codec.constraint.config.validator.ConstraintViolateException;
import net.luis.utils.io.codec.constraint.util.Unit;
import net.luis.utils.util.Pair;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link URIPathConstraintConfig}.<br>
 *
 * @author Luis-St
 */
class URIPathConstraintConfigTest {
	
	@Test
	void constructor() {
		assertDoesNotThrow(() -> URIPathConstraintConfig.UNCONSTRAINED);
	}
	
	@Test
	void constructorNullChecks() {
		assertThrows(NullPointerException.class, () -> new URIPathConstraintConfig(
			null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
		assertThrows(NullPointerException.class, () -> new URIPathConstraintConfig(
			Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
		assertThrows(NullPointerException.class, () -> new URIPathConstraintConfig(
			Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
		assertThrows(NullPointerException.class, () -> new URIPathConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
		assertThrows(NullPointerException.class, () -> new URIPathConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
		assertThrows(NullPointerException.class, () -> new URIPathConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
		assertThrows(NullPointerException.class, () -> new URIPathConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null,
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
		assertThrows(NullPointerException.class, () -> new URIPathConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
		assertThrows(NullPointerException.class, () -> new URIPathConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
		assertThrows(NullPointerException.class, () -> new URIPathConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
		assertThrows(NullPointerException.class, () -> new URIPathConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
		assertThrows(NullPointerException.class, () -> new URIPathConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty()
		));
		assertThrows(NullPointerException.class, () -> new URIPathConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty()
		));
		assertThrows(NullPointerException.class, () -> new URIPathConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty()
		));
		assertThrows(NullPointerException.class, () -> new URIPathConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null
		));
	}
	
	@Test
	void constructorEmptyInSet() {
		assertThrows(IllegalArgumentException.class, () -> new URIPathConstraintConfig(
			Optional.empty(), Optional.of(Pair.of(Set.of(), false)), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructorEmptyAncestorOfSet() {
		assertThrows(IllegalArgumentException.class, () -> new URIPathConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(Set.of()), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructorEmptyDescendantOfSet() {
		assertThrows(IllegalArgumentException.class, () -> new URIPathConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(Set.of()), Optional.empty()
		));
	}
	
	@Test
	void constructorAbsoluteRelativeMutuallyExclusive() {
		assertThrows(IllegalArgumentException.class, () -> new URIPathConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(Unit.INSTANCE), Optional.of(Unit.INSTANCE), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructorWithoutExtensionAndExtensionMutuallyExclusive() {
		assertThrows(IllegalArgumentException.class, () -> new URIPathConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(Unit.INSTANCE), Optional.of(StringConstraintConfig.UNCONSTRAINED), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void unconstrained() {
		URIPathConstraintConfig config = URIPathConstraintConfig.UNCONSTRAINED;
		assertNotNull(config);
		assertTrue(config.equalTo().isEmpty());
		assertTrue(config.in().isEmpty());
		assertTrue(config.length().isEmpty());
		assertTrue(config.depth().isEmpty());
		assertTrue(config.absolute().isEmpty());
		assertTrue(config.relative().isEmpty());
		assertTrue(config.normalized().isEmpty());
		assertTrue(config.path().isEmpty());
		assertTrue(config.segment().isEmpty());
		assertTrue(config.file().isEmpty());
		assertTrue(config.withoutExtension().isEmpty());
		assertTrue(config.extension().isEmpty());
		assertTrue(config.ancestorOf().isEmpty());
		assertTrue(config.descendantOf().isEmpty());
		assertTrue(config.custom().isEmpty());
	}
	
	@Test
	void isUnconstrainedWithUnconstrained() {
		assertTrue(URIPathConstraintConfig.UNCONSTRAINED.isUnconstrained());
	}
	
	@Test
	void isUnconstrainedWithConstraint() {
		URIPathConstraintConfig config = URIPathConstraintConfig.UNCONSTRAINED.withAbsolute();
		assertFalse(config.isUnconstrained());
	}
	
	@Test
	void withEqualTo() {
		URIPathConstraintConfig config = URIPathConstraintConfig.UNCONSTRAINED.withEqualTo("/home/user");
		
		assertTrue(config.equalTo().isPresent());
		assertEquals("/home/user", config.equalTo().get().getFirst());
		assertFalse(config.equalTo().get().getSecond());
	}
	
	@Test
	void withEqualToNull() {
		assertThrows(NullPointerException.class, () -> URIPathConstraintConfig.UNCONSTRAINED.withEqualTo(null));
	}
	
	@Test
	void withNotEqualTo() {
		URIPathConstraintConfig config = URIPathConstraintConfig.UNCONSTRAINED.withNotEqualTo("/tmp");
		
		assertTrue(config.equalTo().isPresent());
		assertEquals("/tmp", config.equalTo().get().getFirst());
		assertTrue(config.equalTo().get().getSecond());
	}
	
	@Test
	void withIn() {
		URIPathConstraintConfig config = URIPathConstraintConfig.UNCONSTRAINED.withIn(List.of("/home", "/var"));
		
		assertTrue(config.in().isPresent());
		assertEquals(2, config.in().get().getFirst().size());
		assertFalse(config.in().get().getSecond());
	}
	
	@Test
	void withInNull() {
		assertThrows(NullPointerException.class, () -> URIPathConstraintConfig.UNCONSTRAINED.withIn(null));
	}
	
	@Test
	void withNotIn() {
		URIPathConstraintConfig config = URIPathConstraintConfig.UNCONSTRAINED.withNotIn(List.of("/tmp"));
		
		assertTrue(config.in().isPresent());
		assertTrue(config.in().get().getSecond());
	}
	
	@Test
	void withLength() {
		LengthConstraintConfig lengthConfig = LengthConstraintConfig.UNCONSTRAINED.withMinLength(1).withMaxLength(100);
		URIPathConstraintConfig config = URIPathConstraintConfig.UNCONSTRAINED.withLength(lengthConfig);
		
		assertTrue(config.length().isPresent());
		assertEquals(lengthConfig, config.length().get());
	}
	
	@Test
	void withLengthNull() {
		assertThrows(NullPointerException.class, () -> URIPathConstraintConfig.UNCONSTRAINED.withLength(null));
	}
	
	@Test
	void withDepth() {
		DepthConstraintConfig depthConfig = DepthConstraintConfig.UNCONSTRAINED.withMinDepth(1).withMaxDepth(10);
		URIPathConstraintConfig config = URIPathConstraintConfig.UNCONSTRAINED.withDepth(depthConfig);
		
		assertTrue(config.depth().isPresent());
		assertEquals(depthConfig, config.depth().get());
	}
	
	@Test
	void withDepthNull() {
		assertThrows(NullPointerException.class, () -> URIPathConstraintConfig.UNCONSTRAINED.withDepth(null));
	}
	
	@Test
	void withAbsolute() {
		URIPathConstraintConfig config = URIPathConstraintConfig.UNCONSTRAINED.withAbsolute();
		
		assertTrue(config.absolute().isPresent());
		assertTrue(config.relative().isEmpty());
	}
	
	@Test
	void withRelative() {
		URIPathConstraintConfig config = URIPathConstraintConfig.UNCONSTRAINED.withRelative();
		
		assertTrue(config.relative().isPresent());
		assertTrue(config.absolute().isEmpty());
	}
	
	@Test
	void withNormalized() {
		URIPathConstraintConfig config = URIPathConstraintConfig.UNCONSTRAINED.withNormalized();
		
		assertTrue(config.normalized().isPresent());
	}
	
	@Test
	void withPath() {
		StringConstraintConfig stringConfig = StringConstraintConfig.UNCONSTRAINED.withStartsWith("/home");
		URIPathConstraintConfig config = URIPathConstraintConfig.UNCONSTRAINED.withPath(stringConfig);
		
		assertTrue(config.path().isPresent());
		assertEquals(stringConfig, config.path().get());
	}
	
	@Test
	void withPathNull() {
		assertThrows(NullPointerException.class, () -> URIPathConstraintConfig.UNCONSTRAINED.withPath(null));
	}
	
	@Test
	void withSegment() {
		StringConstraintConfig stringConfig = StringConstraintConfig.UNCONSTRAINED.withNotBlank();
		URIPathConstraintConfig config = URIPathConstraintConfig.UNCONSTRAINED.withSegment(stringConfig);
		
		assertTrue(config.segment().isPresent());
		assertEquals(stringConfig, config.segment().get());
	}
	
	@Test
	void withSegmentNull() {
		assertThrows(NullPointerException.class, () -> URIPathConstraintConfig.UNCONSTRAINED.withSegment(null));
	}
	
	@Test
	void withFile() {
		StringConstraintConfig stringConfig = StringConstraintConfig.UNCONSTRAINED.withEndsWith(".txt");
		URIPathConstraintConfig config = URIPathConstraintConfig.UNCONSTRAINED.withFile(stringConfig);
		
		assertTrue(config.file().isPresent());
		assertEquals(stringConfig, config.file().get());
	}
	
	@Test
	void withFileNull() {
		assertThrows(NullPointerException.class, () -> URIPathConstraintConfig.UNCONSTRAINED.withFile(null));
	}
	
	@Test
	void withWithoutExtension() {
		URIPathConstraintConfig config = URIPathConstraintConfig.UNCONSTRAINED.withWithoutExtension();
		
		assertTrue(config.withoutExtension().isPresent());
		assertTrue(config.extension().isEmpty());
	}
	
	@Test
	void withExtension() {
		StringConstraintConfig stringConfig = StringConstraintConfig.UNCONSTRAINED.withIn(List.of("txt", "md"));
		URIPathConstraintConfig config = URIPathConstraintConfig.UNCONSTRAINED.withExtension(stringConfig);
		
		assertTrue(config.extension().isPresent());
		assertTrue(config.withoutExtension().isEmpty());
	}
	
	@Test
	void withExtensionNull() {
		assertThrows(NullPointerException.class, () -> URIPathConstraintConfig.UNCONSTRAINED.withExtension(null));
	}
	
	@Test
	void withAncestorOf() {
		URIPathConstraintConfig config = URIPathConstraintConfig.UNCONSTRAINED.withAncestorOf(List.of("/home/user/file.txt"));
		
		assertTrue(config.ancestorOf().isPresent());
		assertEquals(1, config.ancestorOf().get().size());
	}
	
	@Test
	void withAncestorOfNull() {
		assertThrows(NullPointerException.class, () -> URIPathConstraintConfig.UNCONSTRAINED.withAncestorOf(null));
	}
	
	@Test
	void withDescendantOf() {
		URIPathConstraintConfig config = URIPathConstraintConfig.UNCONSTRAINED.withDescendantOf(List.of("/home"));
		
		assertTrue(config.descendantOf().isPresent());
		assertEquals(1, config.descendantOf().get().size());
	}
	
	@Test
	void withDescendantOfNull() {
		assertThrows(NullPointerException.class, () -> URIPathConstraintConfig.UNCONSTRAINED.withDescendantOf(null));
	}
	
	@Test
	void withCustom() {
		URIPathConstraintConfig config = URIPathConstraintConfig.UNCONSTRAINED.withCustom(value -> {});
		
		assertTrue(config.custom().isPresent());
	}
	
	@Test
	void withCustomNull() {
		assertThrows(NullPointerException.class, () -> URIPathConstraintConfig.UNCONSTRAINED.withCustom(null));
	}
	
	@Test
	void validateUnconstrained() {
		URIPathConstraintConfig config = URIPathConstraintConfig.UNCONSTRAINED;
		
		assertDoesNotThrow(() -> config.validate("/home/user"));
		assertDoesNotThrow(() -> config.validate("relative/path"));
		assertDoesNotThrow(() -> config.validate("file.txt"));
	}
	
	@Test
	void validateWithNull() {
		assertThrows(NullPointerException.class, () -> URIPathConstraintConfig.UNCONSTRAINED.validate(null));
	}
	
	@Test
	void validateEqualTo() {
		URIPathConstraintConfig config = URIPathConstraintConfig.UNCONSTRAINED.withEqualTo("/home/user");
		
		assertDoesNotThrow(() -> config.validate("/home/user"));
		assertThrows(ConstraintViolateException.class, () -> config.validate("/home/other"));
	}
	
	@Test
	void validateNotEqualTo() {
		URIPathConstraintConfig config = URIPathConstraintConfig.UNCONSTRAINED.withNotEqualTo("/home/user");
		
		assertDoesNotThrow(() -> config.validate("/home/other"));
		assertThrows(ConstraintViolateException.class, () -> config.validate("/home/user"));
	}
	
	@Test
	void validateIn() {
		URIPathConstraintConfig config = URIPathConstraintConfig.UNCONSTRAINED.withIn(List.of("/home", "/var"));
		
		assertDoesNotThrow(() -> config.validate("/home"));
		assertDoesNotThrow(() -> config.validate("/var"));
		assertThrows(ConstraintViolateException.class, () -> config.validate("/tmp"));
	}
	
	@Test
	void validateNotIn() {
		URIPathConstraintConfig config = URIPathConstraintConfig.UNCONSTRAINED.withNotIn(List.of("/tmp", "/var"));
		
		assertDoesNotThrow(() -> config.validate("/home"));
		assertThrows(ConstraintViolateException.class, () -> config.validate("/tmp"));
		assertThrows(ConstraintViolateException.class, () -> config.validate("/var"));
	}
	
	@Test
	void validateLength() {
		LengthConstraintConfig lengthConfig = LengthConstraintConfig.UNCONSTRAINED.withMinLength(5).withMaxLength(20);
		URIPathConstraintConfig config = URIPathConstraintConfig.UNCONSTRAINED.withLength(lengthConfig);
		
		assertDoesNotThrow(() -> config.validate("/home/user"));
		assertThrows(ConstraintViolateException.class, () -> config.validate("/a"));
		assertThrows(ConstraintViolateException.class, () -> config.validate("/home/user/very/long/path/name"));
	}
	
	@Test
	void validateDepth() {
		DepthConstraintConfig depthConfig = DepthConstraintConfig.UNCONSTRAINED.withMinDepth(2).withMaxDepth(4);
		URIPathConstraintConfig config = URIPathConstraintConfig.UNCONSTRAINED.withDepth(depthConfig);
		
		assertDoesNotThrow(() -> config.validate("/a/b"));
		assertDoesNotThrow(() -> config.validate("/a/b/c"));
		assertDoesNotThrow(() -> config.validate("/a/b/c/d"));
		assertThrows(ConstraintViolateException.class, () -> config.validate("/a"));
		assertThrows(ConstraintViolateException.class, () -> config.validate("/a/b/c/d/e"));
	}
	
	@Test
	void validateAbsolute() {
		URIPathConstraintConfig config = URIPathConstraintConfig.UNCONSTRAINED.withAbsolute();
		
		assertDoesNotThrow(() -> config.validate("/home/user"));
		assertDoesNotThrow(() -> config.validate("/"));
		assertThrows(ConstraintViolateException.class, () -> config.validate("relative/path"));
		assertThrows(ConstraintViolateException.class, () -> config.validate("file.txt"));
	}
	
	@Test
	void validateRelative() {
		URIPathConstraintConfig config = URIPathConstraintConfig.UNCONSTRAINED.withRelative();
		
		assertDoesNotThrow(() -> config.validate("relative/path"));
		assertDoesNotThrow(() -> config.validate("file.txt"));
		assertThrows(ConstraintViolateException.class, () -> config.validate("/home/user"));
		assertThrows(ConstraintViolateException.class, () -> config.validate("/"));
	}
	
	@Test
	void validateNormalized() {
		URIPathConstraintConfig config = URIPathConstraintConfig.UNCONSTRAINED.withNormalized();
		
		assertDoesNotThrow(() -> config.validate("/home/user"));
		assertDoesNotThrow(() -> config.validate("relative/path"));
		assertThrows(ConstraintViolateException.class, () -> config.validate("/home/./user"));
		assertThrows(ConstraintViolateException.class, () -> config.validate("/home/../other"));
		assertThrows(ConstraintViolateException.class, () -> config.validate("./file.txt"));
		assertThrows(ConstraintViolateException.class, () -> config.validate("../parent/file.txt"));
	}
	
	@Test
	void validatePath() {
		StringConstraintConfig stringConfig = StringConstraintConfig.UNCONSTRAINED.withStartsWith("/home");
		URIPathConstraintConfig config = URIPathConstraintConfig.UNCONSTRAINED.withPath(stringConfig);
		
		assertDoesNotThrow(() -> config.validate("/home/user"));
		assertDoesNotThrow(() -> config.validate("/home"));
		assertThrows(ConstraintViolateException.class, () -> config.validate("/var/log"));
	}
	
	@Test
	void validateSegment() {
		StringConstraintConfig stringConfig = StringConstraintConfig.UNCONSTRAINED.withNotBlank();
		URIPathConstraintConfig config = URIPathConstraintConfig.UNCONSTRAINED.withSegment(stringConfig);
		
		assertDoesNotThrow(() -> config.validate("/home/user"));
		assertDoesNotThrow(() -> config.validate("file.txt"));
	}
	
	@Test
	void validateFile() {
		StringConstraintConfig stringConfig = StringConstraintConfig.UNCONSTRAINED.withEndsWith(".txt");
		URIPathConstraintConfig config = URIPathConstraintConfig.UNCONSTRAINED.withFile(stringConfig);
		
		assertDoesNotThrow(() -> config.validate("/home/user/file.txt"));
		assertDoesNotThrow(() -> config.validate("document.txt"));
		assertThrows(ConstraintViolateException.class, () -> config.validate("/home/user/file.md"));
	}
	
	@Test
	void validateWithoutExtension() {
		URIPathConstraintConfig config = URIPathConstraintConfig.UNCONSTRAINED.withWithoutExtension();
		
		assertDoesNotThrow(() -> config.validate("/home/user/file"));
		assertDoesNotThrow(() -> config.validate("directory"));
		assertThrows(ConstraintViolateException.class, () -> config.validate("/home/user/file.txt"));
	}
	
	@Test
	void validateExtension() {
		StringConstraintConfig stringConfig = StringConstraintConfig.UNCONSTRAINED.withIn(List.of("txt", "md"));
		URIPathConstraintConfig config = URIPathConstraintConfig.UNCONSTRAINED.withExtension(stringConfig);
		
		assertDoesNotThrow(() -> config.validate("/home/user/file.txt"));
		assertDoesNotThrow(() -> config.validate("/home/user/file.md"));
		assertThrows(ConstraintViolateException.class, () -> config.validate("/home/user/file.xml"));
	}
	
	@Test
	void validateAncestorOf() {
		URIPathConstraintConfig config = URIPathConstraintConfig.UNCONSTRAINED.withAncestorOf(List.of("/home/user/documents/file.txt"));
		
		assertDoesNotThrow(() -> config.validate("/home"));
		assertDoesNotThrow(() -> config.validate("/home/user"));
		assertDoesNotThrow(() -> config.validate("/home/user/documents"));
		assertThrows(ConstraintViolateException.class, () -> config.validate("/var"));
		assertThrows(ConstraintViolateException.class, () -> config.validate("/home/other"));
	}
	
	@Test
	void validateDescendantOf() {
		URIPathConstraintConfig config = URIPathConstraintConfig.UNCONSTRAINED.withDescendantOf(List.of("/home/user"));
		
		assertDoesNotThrow(() -> config.validate("/home/user/documents"));
		assertDoesNotThrow(() -> config.validate("/home/user/documents/file.txt"));
		assertDoesNotThrow(() -> config.validate("/home/user"));
		assertThrows(ConstraintViolateException.class, () -> config.validate("/home"));
		assertThrows(ConstraintViolateException.class, () -> config.validate("/var"));
	}
	
	@Test
	void validateCustom() {
		URIPathConstraintConfig config = URIPathConstraintConfig.UNCONSTRAINED.withCustom(value ->
			{
				if (!value.contains("allowed")) throw new ConstraintViolateException("Path must contain 'allowed'");
			}
		);
		
		assertDoesNotThrow(() -> config.validate("/home/allowed/file.txt"));
		assertThrows(ConstraintViolateException.class, () -> config.validate("/home/forbidden/file.txt"));
	}
}
