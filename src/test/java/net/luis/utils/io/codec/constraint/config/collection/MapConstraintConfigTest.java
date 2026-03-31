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

package net.luis.utils.io.codec.constraint.config.collection;

import net.luis.utils.io.codec.constraint.config.SizeConstraintConfig;
import net.luis.utils.io.codec.constraint.config.validator.ConstraintViolateException;
import net.luis.utils.util.Pair;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link MapConstraintConfig}.<br>
 *
 * @author Luis-St
 */
class MapConstraintConfigTest {
	
	@Test
	void constructWithNullEqualTo() {
		assertThrows(NullPointerException.class, () -> new MapConstraintConfig<String, Integer>(
			null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullIn() {
		assertThrows(NullPointerException.class, () -> new MapConstraintConfig<String, Integer>(
			Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullSize() {
		assertThrows(NullPointerException.class, () -> new MapConstraintConfig<String, Integer>(
			Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullRequiredKeys() {
		assertThrows(NullPointerException.class, () -> new MapConstraintConfig<String, Integer>(
			Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullForbiddenKeys() {
		assertThrows(NullPointerException.class, () -> new MapConstraintConfig<String, Integer>(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullAllowedKeys() {
		assertThrows(NullPointerException.class, () -> new MapConstraintConfig<String, Integer>(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null,
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullNonNullKeys() {
		assertThrows(NullPointerException.class, () -> new MapConstraintConfig<String, Integer>(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			null, Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullUniqueValues() {
		assertThrows(NullPointerException.class, () -> new MapConstraintConfig<String, Integer>(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), null, Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullNonNullValues() {
		assertThrows(NullPointerException.class, () -> new MapConstraintConfig<String, Integer>(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), null, Optional.empty()
		));
	}
	
	@Test
	void constructWithNullCustom() {
		assertThrows(NullPointerException.class, () -> new MapConstraintConfig<String, Integer>(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), null
		));
	}
	
	@Test
	void constructWithEmptyInSet() {
		assertThrows(IllegalArgumentException.class, () -> new MapConstraintConfig<String, Integer>(
			Optional.empty(), Optional.of(Pair.of(Set.of(), false)), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNegativeMin() {
		assertThrows(IllegalArgumentException.class, () -> MapConstraintConfig.<String, Integer>unconstrained().withMinSize(-1));
	}
	
	@Test
	void constructWithNegativeMax() {
		assertThrows(IllegalArgumentException.class, () -> MapConstraintConfig.<String, Integer>unconstrained().withMaxSize(-1));
	}
	
	@Test
	void constructWithMinGreaterThanMax() {
		assertThrows(IllegalArgumentException.class, () -> MapConstraintConfig.<String, Integer>unconstrained().withSizeBetween(10, 5));
	}
	
	@Test
	void constructWithEmptyRequiredKeys() {
		assertThrows(IllegalArgumentException.class, () -> new MapConstraintConfig<String, Integer>(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(Set.of()), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithEmptyForbiddenKeys() {
		assertThrows(IllegalArgumentException.class, () -> new MapConstraintConfig<String, Integer>(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(Set.of()),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithEmptyAllowedKeys() {
		assertThrows(IllegalArgumentException.class, () -> new MapConstraintConfig<String, Integer>(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.of(Set.of()), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithOverlappingRequiredAndForbiddenKeys() {
		assertThrows(IllegalArgumentException.class, () -> new MapConstraintConfig<String, Integer>(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(Set.of("a")), Optional.of(Set.of("a")),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithRequiredKeysNotSubsetOfAllowed() {
		assertThrows(IllegalArgumentException.class, () -> new MapConstraintConfig<String, Integer>(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(Set.of("a", "b")), Optional.empty(),
			Optional.of(Set.of("a")), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithOverlappingForbiddenAndAllowedKeys() {
		assertThrows(IllegalArgumentException.class, () -> new MapConstraintConfig<String, Integer>(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(Set.of("a")),
			Optional.of(Set.of("a", "b")), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void unconstrained() {
		MapConstraintConfig<String, Integer> config = MapConstraintConfig.unconstrained();
		assertNotNull(config);
		assertTrue(config.equalTo().isEmpty());
		assertTrue(config.in().isEmpty());
		assertTrue(config.size().isEmpty());
		assertTrue(config.requiredKeys().isEmpty());
		assertTrue(config.forbiddenKeys().isEmpty());
		assertTrue(config.allowedKeys().isEmpty());
		assertTrue(config.nonNullKeys().isEmpty());
		assertTrue(config.uniqueValues().isEmpty());
		assertTrue(config.nonNullValues().isEmpty());
		assertTrue(config.custom().isEmpty());
		assertDoesNotThrow(() -> config.validate(Map.of("a", 1)));
	}
	
	@Test
	void isUnconstrainedWithUnconstrained() {
		assertTrue(MapConstraintConfig.<String, Integer>unconstrained().isUnconstrained());
	}
	
	@Test
	void isUnconstrainedWithConstraint() {
		MapConstraintConfig<String, Integer> config = MapConstraintConfig.<String, Integer>unconstrained().withMinSize(1);
		assertFalse(config.isUnconstrained());
	}
	
	@Test
	void withEqualTo() {
		Map<String, Integer> map = Map.of("a", 1);
		MapConstraintConfig<String, Integer> config = MapConstraintConfig.<String, Integer>unconstrained().withEqualTo(map);
		assertTrue(config.equalTo().isPresent());
		assertEquals(map, config.equalTo().get().getFirst());
		assertFalse(config.equalTo().get().getSecond());
	}
	
	@Test
	void withEqualToNull() {
		assertThrows(NullPointerException.class, () -> MapConstraintConfig.<String, Integer>unconstrained().withEqualTo(null));
	}
	
	@Test
	void withNotEqualTo() {
		Map<String, Integer> map = Map.of("a", 1);
		MapConstraintConfig<String, Integer> config = MapConstraintConfig.<String, Integer>unconstrained().withNotEqualTo(map);
		assertTrue(config.equalTo().isPresent());
		assertEquals(map, config.equalTo().get().getFirst());
		assertTrue(config.equalTo().get().getSecond());
	}
	
	@Test
	void withNotEqualToNull() {
		assertThrows(NullPointerException.class, () -> MapConstraintConfig.<String, Integer>unconstrained().withNotEqualTo(null));
	}
	
	@Test
	void withIn() {
		List<Map<String, Integer>> maps = List.of(Map.of("a", 1), Map.of("b", 2));
		MapConstraintConfig<String, Integer> config = MapConstraintConfig.<String, Integer>unconstrained().withIn(maps);
		assertTrue(config.in().isPresent());
		assertFalse(config.in().get().getSecond());
	}
	
	@Test
	void withInNull() {
		assertThrows(NullPointerException.class, () -> MapConstraintConfig.<String, Integer>unconstrained().withIn(null));
	}
	
	@Test
	void withNotIn() {
		List<Map<String, Integer>> maps = List.of(Map.of("a", 1), Map.of("b", 2));
		MapConstraintConfig<String, Integer> config = MapConstraintConfig.<String, Integer>unconstrained().withNotIn(maps);
		assertTrue(config.in().isPresent());
		assertTrue(config.in().get().getSecond());
	}
	
	@Test
	void withNotInNull() {
		assertThrows(NullPointerException.class, () -> MapConstraintConfig.<String, Integer>unconstrained().withNotIn(null));
	}
	
	@Test
	void withMinSize() {
		MapConstraintConfig<String, Integer> config = MapConstraintConfig.<String, Integer>unconstrained().withMinSize(5);
		assertTrue(config.size().isPresent());
		assertTrue(config.size().get().min().isPresent());
		assertEquals(5, config.size().get().min().get().getFirst());
		assertTrue(config.size().get().min().get().getSecond());
	}
	
	@Test
	void withMaxSize() {
		MapConstraintConfig<String, Integer> config = MapConstraintConfig.<String, Integer>unconstrained().withMaxSize(10);
		assertTrue(config.size().isPresent());
		assertTrue(config.size().get().max().isPresent());
		assertEquals(10, config.size().get().max().get().getFirst());
		assertTrue(config.size().get().max().get().getSecond());
	}
	
	@Test
	void withExactSize() {
		MapConstraintConfig<String, Integer> config = MapConstraintConfig.<String, Integer>unconstrained().withExactSize(7);
		assertTrue(config.size().isPresent());
		assertTrue(config.size().get().min().isPresent());
		assertTrue(config.size().get().max().isPresent());
		assertEquals(7, config.size().get().min().get().getFirst());
		assertEquals(7, config.size().get().max().get().getFirst());
	}
	
	@Test
	void withSizeBetween() {
		MapConstraintConfig<String, Integer> config = MapConstraintConfig.<String, Integer>unconstrained().withSizeBetween(3, 8);
		assertTrue(config.size().isPresent());
		assertTrue(config.size().get().min().isPresent());
		assertTrue(config.size().get().max().isPresent());
		assertEquals(3, config.size().get().min().get().getFirst());
		assertEquals(8, config.size().get().max().get().getFirst());
	}
	
	@Test
	void withRequiredKey() {
		MapConstraintConfig<String, Integer> config = MapConstraintConfig.<String, Integer>unconstrained().withRequiredKey("key");
		assertTrue(config.requiredKeys().isPresent());
		assertTrue(config.requiredKeys().get().contains("key"));
	}
	
	@Test
	void withRequiredKeyNull() {
		assertThrows(NullPointerException.class, () -> MapConstraintConfig.<String, Integer>unconstrained().withRequiredKey(null));
	}
	
	@Test
	void withRequiredKeys() {
		MapConstraintConfig<String, Integer> config = MapConstraintConfig.<String, Integer>unconstrained().withRequiredKeys(List.of("a", "b"));
		assertTrue(config.requiredKeys().isPresent());
		assertEquals(Set.of("a", "b"), config.requiredKeys().get());
	}
	
	@Test
	void withRequiredKeysNull() {
		assertThrows(NullPointerException.class, () -> MapConstraintConfig.<String, Integer>unconstrained().withRequiredKeys(null));
	}
	
	@Test
	void withForbiddenKey() {
		MapConstraintConfig<String, Integer> config = MapConstraintConfig.<String, Integer>unconstrained().withForbiddenKey("key");
		assertTrue(config.forbiddenKeys().isPresent());
		assertTrue(config.forbiddenKeys().get().contains("key"));
	}
	
	@Test
	void withForbiddenKeyNull() {
		assertThrows(NullPointerException.class, () -> MapConstraintConfig.<String, Integer>unconstrained().withForbiddenKey(null));
	}
	
	@Test
	void withForbiddenKeys() {
		MapConstraintConfig<String, Integer> config = MapConstraintConfig.<String, Integer>unconstrained().withForbiddenKeys(List.of("x", "y"));
		assertTrue(config.forbiddenKeys().isPresent());
		assertEquals(Set.of("x", "y"), config.forbiddenKeys().get());
	}
	
	@Test
	void withForbiddenKeysNull() {
		assertThrows(NullPointerException.class, () -> MapConstraintConfig.<String, Integer>unconstrained().withForbiddenKeys(null));
	}
	
	@Test
	void withAllowedKey() {
		MapConstraintConfig<String, Integer> config = MapConstraintConfig.<String, Integer>unconstrained().withAllowedKey("key");
		assertTrue(config.allowedKeys().isPresent());
		assertTrue(config.allowedKeys().get().contains("key"));
	}
	
	@Test
	void withAllowedKeyNull() {
		assertThrows(NullPointerException.class, () -> MapConstraintConfig.<String, Integer>unconstrained().withAllowedKey(null));
	}
	
	@Test
	void withAllowedKeys() {
		MapConstraintConfig<String, Integer> config = MapConstraintConfig.<String, Integer>unconstrained().withAllowedKeys(List.of("a", "b"));
		assertTrue(config.allowedKeys().isPresent());
		assertEquals(Set.of("a", "b"), config.allowedKeys().get());
	}
	
	@Test
	void withAllowedKeysNull() {
		assertThrows(NullPointerException.class, () -> MapConstraintConfig.<String, Integer>unconstrained().withAllowedKeys(null));
	}
	
	@Test
	void withNonNullKeys() {
		MapConstraintConfig<String, Integer> config = MapConstraintConfig.<String, Integer>unconstrained().withNonNullKeys();
		assertTrue(config.nonNullKeys().isPresent());
	}
	
	@Test
	void withUniqueValues() {
		MapConstraintConfig<String, Integer> config = MapConstraintConfig.<String, Integer>unconstrained().withUniqueValues();
		assertTrue(config.uniqueValues().isPresent());
	}
	
	@Test
	void withNonNullValues() {
		MapConstraintConfig<String, Integer> config = MapConstraintConfig.<String, Integer>unconstrained().withNonNullValues();
		assertTrue(config.nonNullValues().isPresent());
	}
	
	@Test
	void withCustom() {
		MapConstraintConfig<String, Integer> config = MapConstraintConfig.<String, Integer>unconstrained().withCustom(m -> {
			if (m.isEmpty()) throw new ConstraintViolateException("Map must not be empty");
		});
		assertTrue(config.custom().isPresent());
	}
	
	@Test
	void withCustomNull() {
		assertThrows(NullPointerException.class, () -> MapConstraintConfig.<String, Integer>unconstrained().withCustom(null));
	}
	
	@Test
	void validateWithEqualTo() {
		Map<String, Integer> map = Map.of("a", 1);
		MapConstraintConfig<String, Integer> config = MapConstraintConfig.<String, Integer>unconstrained().withEqualTo(map);
		assertDoesNotThrow(() -> config.validate(Map.of("a", 1)));
		assertThrows(ConstraintViolateException.class, () -> config.validate(Map.of("b", 2)));
	}
	
	@Test
	void validateWithNotEqualTo() {
		Map<String, Integer> map = Map.of("a", 1);
		MapConstraintConfig<String, Integer> config = MapConstraintConfig.<String, Integer>unconstrained().withNotEqualTo(map);
		assertDoesNotThrow(() -> config.validate(Map.of("b", 2)));
		assertThrows(ConstraintViolateException.class, () -> config.validate(Map.of("a", 1)));
	}
	
	@Test
	void validateWithIn() {
		List<Map<String, Integer>> maps = List.of(Map.of("a", 1), Map.of("b", 2));
		MapConstraintConfig<String, Integer> config = MapConstraintConfig.<String, Integer>unconstrained().withIn(maps);
		assertDoesNotThrow(() -> config.validate(Map.of("a", 1)));
		assertDoesNotThrow(() -> config.validate(Map.of("b", 2)));
		assertThrows(ConstraintViolateException.class, () -> config.validate(Map.of("c", 3)));
	}
	
	@Test
	void validateWithNotIn() {
		List<Map<String, Integer>> maps = List.of(Map.of("a", 1), Map.of("b", 2));
		MapConstraintConfig<String, Integer> config = MapConstraintConfig.<String, Integer>unconstrained().withNotIn(maps);
		assertDoesNotThrow(() -> config.validate(Map.of("c", 3)));
		assertThrows(ConstraintViolateException.class, () -> config.validate(Map.of("a", 1)));
	}
	
	@Test
	void validateWithMinSize() {
		MapConstraintConfig<String, Integer> config = MapConstraintConfig.<String, Integer>unconstrained().withMinSize(2);
		assertDoesNotThrow(() -> config.validate(Map.of("a", 1, "b", 2)));
		assertThrows(ConstraintViolateException.class, () -> config.validate(Map.of("a", 1)));
	}
	
	@Test
	void validateWithMaxSize() {
		MapConstraintConfig<String, Integer> config = MapConstraintConfig.<String, Integer>unconstrained().withMaxSize(2);
		assertDoesNotThrow(() -> config.validate(Map.of("a", 1, "b", 2)));
		assertThrows(ConstraintViolateException.class, () -> config.validate(Map.of("a", 1, "b", 2, "c", 3)));
	}
	
	@Test
	void validateWithExactSize() {
		MapConstraintConfig<String, Integer> config = MapConstraintConfig.<String, Integer>unconstrained().withExactSize(2);
		assertDoesNotThrow(() -> config.validate(Map.of("a", 1, "b", 2)));
		assertThrows(ConstraintViolateException.class, () -> config.validate(Map.of("a", 1)));
		assertThrows(ConstraintViolateException.class, () -> config.validate(Map.of("a", 1, "b", 2, "c", 3)));
	}
	
	@Test
	void validateWithRequiredKeys() {
		MapConstraintConfig<String, Integer> config = MapConstraintConfig.<String, Integer>unconstrained().withRequiredKeys(List.of("a", "b"));
		assertDoesNotThrow(() -> config.validate(Map.of("a", 1, "b", 2)));
		assertDoesNotThrow(() -> config.validate(Map.of("a", 1, "b", 2, "c", 3)));
		assertThrows(ConstraintViolateException.class, () -> config.validate(Map.of("a", 1)));
	}
	
	@Test
	void validateWithForbiddenKeys() {
		MapConstraintConfig<String, Integer> config = MapConstraintConfig.<String, Integer>unconstrained().withForbiddenKeys(List.of("x", "y"));
		assertDoesNotThrow(() -> config.validate(Map.of("a", 1, "b", 2)));
		assertThrows(ConstraintViolateException.class, () -> config.validate(Map.of("a", 1, "x", 2)));
	}
	
	@Test
	void validateWithAllowedKeys() {
		MapConstraintConfig<String, Integer> config = MapConstraintConfig.<String, Integer>unconstrained().withAllowedKeys(List.of("a", "b"));
		assertDoesNotThrow(() -> config.validate(Map.of("a", 1)));
		assertDoesNotThrow(() -> config.validate(Map.of("a", 1, "b", 2)));
		assertThrows(ConstraintViolateException.class, () -> config.validate(Map.of("a", 1, "c", 3)));
	}
	
	@Test
	void validateWithUniqueValues() {
		MapConstraintConfig<String, Integer> config = MapConstraintConfig.<String, Integer>unconstrained().withUniqueValues();
		assertDoesNotThrow(() -> config.validate(Map.of("a", 1, "b", 2)));
		Map<String, Integer> mapWithDuplicates = new HashMap<>();
		mapWithDuplicates.put("a", 1);
		mapWithDuplicates.put("b", 1);
		assertThrows(ConstraintViolateException.class, () -> config.validate(mapWithDuplicates));
	}
	
	@Test
	void validateWithMultipleConstraints() {
		MapConstraintConfig<String, Integer> config = MapConstraintConfig.<String, Integer>unconstrained()
			.withMinSize(1)
			.withMaxSize(3)
			.withRequiredKey("id")
			.withForbiddenKey("secret");
		
		assertDoesNotThrow(() -> config.validate(Map.of("id", 1, "name", 2)));
		assertThrows(ConstraintViolateException.class, () -> config.validate(Map.of("name", 1)));
		assertThrows(ConstraintViolateException.class, () -> config.validate(Map.of("id", 1, "secret", 2)));
	}
	
	@Test
	void validateWithNullValue() {
		MapConstraintConfig<String, Integer> config = MapConstraintConfig.unconstrained();
		assertThrows(NullPointerException.class, () -> config.validate(null));
	}
	
	@Test
	void withSize() {
		SizeConstraintConfig sizeConfig = SizeConstraintConfig.UNCONSTRAINED.withMinSize(2).withMaxSize(10);
		MapConstraintConfig<String, Integer> config = MapConstraintConfig.<String, Integer>unconstrained().withSize(sizeConfig);
		assertTrue(config.size().isPresent());
		assertEquals(sizeConfig, config.size().get());
		assertTrue(config.size().get().min().isPresent());
		assertTrue(config.size().get().max().isPresent());
		assertEquals(2, config.size().get().min().get().getFirst());
		assertEquals(10, config.size().get().max().get().getFirst());
	}
	
	@Test
	void withSizeNull() {
		assertThrows(NullPointerException.class, () -> MapConstraintConfig.<String, Integer>unconstrained().withSize(null));
	}
	
	@Test
	void validateWithSize() {
		SizeConstraintConfig sizeConfig = SizeConstraintConfig.UNCONSTRAINED.withMinSize(1).withMaxSize(3);
		MapConstraintConfig<String, Integer> config = MapConstraintConfig.<String, Integer>unconstrained().withSize(sizeConfig);
		assertDoesNotThrow(() -> config.validate(Map.of("a", 1)));
		assertDoesNotThrow(() -> config.validate(Map.of("a", 1, "b", 2, "c", 3)));
		assertThrows(ConstraintViolateException.class, () -> config.validate(Map.of()));
		assertThrows(ConstraintViolateException.class, () -> config.validate(Map.of("a", 1, "b", 2, "c", 3, "d", 4)));
	}
}
