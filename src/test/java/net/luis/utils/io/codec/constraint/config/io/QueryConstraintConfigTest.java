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

import net.luis.utils.io.codec.constraint.config.SizeConstraintConfig;
import net.luis.utils.io.codec.constraint.config.StringConstraintConfig;
import net.luis.utils.io.codec.constraint.config.validator.ConstraintViolateException;
import net.luis.utils.util.Pair;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link QueryConstraintConfig}.<br>
 *
 * @author Luis-St
 */
class QueryConstraintConfigTest {
	
	@Test
	void constructor() {
		assertDoesNotThrow(() -> QueryConstraintConfig.UNCONSTRAINED);
		assertDoesNotThrow(() -> new QueryConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructorNullChecks() {
		assertThrows(NullPointerException.class, () -> new QueryConstraintConfig(
			null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
		assertThrows(NullPointerException.class, () -> new QueryConstraintConfig(
			Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
		assertThrows(NullPointerException.class, () -> new QueryConstraintConfig(
			Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
		assertThrows(NullPointerException.class, () -> new QueryConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
		assertThrows(NullPointerException.class, () -> new QueryConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
		assertThrows(NullPointerException.class, () -> new QueryConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
		assertThrows(NullPointerException.class, () -> new QueryConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null,
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
		assertThrows(NullPointerException.class, () -> new QueryConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
		assertThrows(NullPointerException.class, () -> new QueryConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
		assertThrows(NullPointerException.class, () -> new QueryConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
		assertThrows(NullPointerException.class, () -> new QueryConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty()
		));
		assertThrows(NullPointerException.class, () -> new QueryConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty()
		));
		assertThrows(NullPointerException.class, () -> new QueryConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty()
		));
		assertThrows(NullPointerException.class, () -> new QueryConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null
		));
	}
	
	@Test
	void constructorEmptyInSet() {
		assertThrows(IllegalArgumentException.class, () -> new QueryConstraintConfig(
			Optional.empty(), Optional.of(Pair.of(Set.of(), false)), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructorEmptyRequiredKeysSet() {
		assertThrows(IllegalArgumentException.class, () -> new QueryConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(Set.of()), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructorEmptyForbiddenKeysSet() {
		assertThrows(IllegalArgumentException.class, () -> new QueryConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(Set.of()), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructorEmptyAllowedKeysSet() {
		assertThrows(IllegalArgumentException.class, () -> new QueryConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(Set.of()), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void unconstrained() {
		QueryConstraintConfig config = QueryConstraintConfig.UNCONSTRAINED;
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
		assertTrue(config.valueConstraints().isEmpty());
		assertTrue(config.patternValueConstraints().isEmpty());
		assertTrue(config.singleValued().isEmpty());
		assertTrue(config.multiValuedConstraints().isEmpty());
		assertTrue(config.custom().isEmpty());
	}
	
	@Test
	void isUnconstrainedWithUnconstrained() {
		assertTrue(QueryConstraintConfig.UNCONSTRAINED.isUnconstrained());
	}
	
	@Test
	void isUnconstrainedWithConstraint() {
		QueryConstraintConfig config = QueryConstraintConfig.UNCONSTRAINED.withSize(SizeConstraintConfig.UNCONSTRAINED.withMinSize(1));
		assertFalse(config.isUnconstrained());
	}
	
	@Test
	void withEqualTo() {
		Map<String, List<String>> value = Map.of("key", List.of("value"));
		QueryConstraintConfig config = QueryConstraintConfig.UNCONSTRAINED.withEqualTo(value);
		
		assertTrue(config.equalTo().isPresent());
		assertEquals(value, config.equalTo().get().getFirst());
		assertFalse(config.equalTo().get().getSecond());
	}
	
	@Test
	void withEqualToNull() {
		assertThrows(NullPointerException.class, () -> QueryConstraintConfig.UNCONSTRAINED.withEqualTo(null));
	}
	
	@Test
	void withNotEqualTo() {
		Map<String, List<String>> value = Map.of("key", List.of("value"));
		QueryConstraintConfig config = QueryConstraintConfig.UNCONSTRAINED.withNotEqualTo(value);
		
		assertTrue(config.equalTo().isPresent());
		assertEquals(value, config.equalTo().get().getFirst());
		assertTrue(config.equalTo().get().getSecond());
	}
	
	@Test
	void withNotEqualToNull() {
		assertThrows(NullPointerException.class, () -> QueryConstraintConfig.UNCONSTRAINED.withNotEqualTo(null));
	}
	
	@Test
	void withIn() {
		Map<String, List<String>> value1 = Map.of("key1", List.of("value1"));
		Map<String, List<String>> value2 = Map.of("key2", List.of("value2"));
		QueryConstraintConfig config = QueryConstraintConfig.UNCONSTRAINED.withIn(List.of(value1, value2));
		
		assertTrue(config.in().isPresent());
		assertEquals(2, config.in().get().getFirst().size());
		assertFalse(config.in().get().getSecond());
	}
	
	@Test
	void withInNull() {
		assertThrows(NullPointerException.class, () -> QueryConstraintConfig.UNCONSTRAINED.withIn(null));
	}
	
	@Test
	void withNotIn() {
		Map<String, List<String>> value = Map.of("key", List.of("value"));
		QueryConstraintConfig config = QueryConstraintConfig.UNCONSTRAINED.withNotIn(List.of(value));
		
		assertTrue(config.in().isPresent());
		assertTrue(config.in().get().getSecond());
	}
	
	@Test
	void withNotInNull() {
		assertThrows(NullPointerException.class, () -> QueryConstraintConfig.UNCONSTRAINED.withNotIn(null));
	}
	
	@Test
	void withSize() {
		SizeConstraintConfig sizeConfig = SizeConstraintConfig.UNCONSTRAINED.withMinSize(2).withMaxSize(10);
		QueryConstraintConfig config = QueryConstraintConfig.UNCONSTRAINED.withSize(sizeConfig);
		
		assertTrue(config.size().isPresent());
		assertEquals(sizeConfig, config.size().get());
	}
	
	@Test
	void withSizeNull() {
		assertThrows(NullPointerException.class, () -> QueryConstraintConfig.UNCONSTRAINED.withSize(null));
	}
	
	@Test
	void withRequiredKeys() {
		QueryConstraintConfig config = QueryConstraintConfig.UNCONSTRAINED.withRequiredKeys(List.of("name", "age"));
		
		assertTrue(config.requiredKeys().isPresent());
		assertEquals(2, config.requiredKeys().get().size());
		assertTrue(config.requiredKeys().get().contains("name"));
		assertTrue(config.requiredKeys().get().contains("age"));
	}
	
	@Test
	void withRequiredKeysNull() {
		assertThrows(NullPointerException.class, () -> QueryConstraintConfig.UNCONSTRAINED.withRequiredKeys(null));
	}
	
	@Test
	void withForbiddenKeys() {
		QueryConstraintConfig config = QueryConstraintConfig.UNCONSTRAINED.withForbiddenKeys(List.of("password", "secret"));
		
		assertTrue(config.forbiddenKeys().isPresent());
		assertEquals(2, config.forbiddenKeys().get().size());
		assertTrue(config.forbiddenKeys().get().contains("password"));
	}
	
	@Test
	void withForbiddenKeysNull() {
		assertThrows(NullPointerException.class, () -> QueryConstraintConfig.UNCONSTRAINED.withForbiddenKeys(null));
	}
	
	@Test
	void withAllowedKeys() {
		QueryConstraintConfig config = QueryConstraintConfig.UNCONSTRAINED.withAllowedKeys(List.of("name", "email"));
		
		assertTrue(config.allowedKeys().isPresent());
		assertEquals(2, config.allowedKeys().get().size());
	}
	
	@Test
	void withAllowedKeysNull() {
		assertThrows(NullPointerException.class, () -> QueryConstraintConfig.UNCONSTRAINED.withAllowedKeys(null));
	}
	
	@Test
	void withNonNullKeys() {
		QueryConstraintConfig config = QueryConstraintConfig.UNCONSTRAINED.withNonNullKeys();
		
		assertTrue(config.nonNullKeys().isPresent());
	}
	
	@Test
	void withUniqueValues() {
		QueryConstraintConfig config = QueryConstraintConfig.UNCONSTRAINED.withUniqueValues();
		
		assertTrue(config.uniqueValues().isPresent());
	}
	
	@Test
	void withNonNullValues() {
		QueryConstraintConfig config = QueryConstraintConfig.UNCONSTRAINED.withNonNullValues();
		
		assertTrue(config.nonNullValues().isPresent());
	}
	
	@Test
	void withValue() {
		StringConstraintConfig stringConfig = StringConstraintConfig.UNCONSTRAINED.withNotBlank();
		QueryConstraintConfig config = QueryConstraintConfig.UNCONSTRAINED.withValue("name", stringConfig);
		
		assertTrue(config.valueConstraints().isPresent());
		assertTrue(config.valueConstraints().get().containsKey("name"));
	}
	
	@Test
	void withValueNullKey() {
		assertThrows(NullPointerException.class, () -> QueryConstraintConfig.UNCONSTRAINED.withValue(null, StringConstraintConfig.UNCONSTRAINED));
	}
	
	@Test
	void withValueNullConfig() {
		assertThrows(NullPointerException.class, () -> QueryConstraintConfig.UNCONSTRAINED.withValue("name", null));
	}
	
	@Test
	void withValuesString() {
		StringConstraintConfig stringConfig = StringConstraintConfig.UNCONSTRAINED.withNotBlank();
		QueryConstraintConfig config = QueryConstraintConfig.UNCONSTRAINED.withValues("^id_.*$", stringConfig);
		
		assertTrue(config.patternValueConstraints().isPresent());
		assertEquals(1, config.patternValueConstraints().get().size());
	}
	
	@Test
	void withValuesStringNull() {
		assertThrows(NullPointerException.class, () -> QueryConstraintConfig.UNCONSTRAINED.withValues((String) null, StringConstraintConfig.UNCONSTRAINED));
	}
	
	@Test
	void withValuesPattern() {
		Pattern pattern = Pattern.compile("^id_.*$");
		StringConstraintConfig stringConfig = StringConstraintConfig.UNCONSTRAINED.withNotBlank();
		QueryConstraintConfig config = QueryConstraintConfig.UNCONSTRAINED.withValues(pattern, stringConfig);
		
		assertTrue(config.patternValueConstraints().isPresent());
		assertTrue(config.patternValueConstraints().get().containsKey(pattern));
	}
	
	@Test
	void withValuesPatternNull() {
		assertThrows(NullPointerException.class, () -> QueryConstraintConfig.UNCONSTRAINED.withValues((Pattern) null, StringConstraintConfig.UNCONSTRAINED));
	}
	
	@Test
	void withValuesConfigNull() {
		assertThrows(NullPointerException.class, () -> QueryConstraintConfig.UNCONSTRAINED.withValues(Pattern.compile(".*"), null));
	}
	
	@Test
	void withSingleValued() {
		QueryConstraintConfig config = QueryConstraintConfig.UNCONSTRAINED.withSingleValued();
		
		assertTrue(config.singleValued().isPresent());
	}
	
	@Test
	void withMultiValued() {
		SizeConstraintConfig sizeConfig = SizeConstraintConfig.UNCONSTRAINED.withMinSize(2);
		QueryConstraintConfig config = QueryConstraintConfig.UNCONSTRAINED.withMultiValued("tags", sizeConfig);
		
		assertTrue(config.multiValuedConstraints().isPresent());
		assertTrue(config.multiValuedConstraints().get().containsKey("tags"));
	}
	
	@Test
	void withMultiValuedNullKey() {
		assertThrows(NullPointerException.class, () -> QueryConstraintConfig.UNCONSTRAINED.withMultiValued(null, SizeConstraintConfig.UNCONSTRAINED));
	}
	
	@Test
	void withMultiValuedNullConfig() {
		assertThrows(NullPointerException.class, () -> QueryConstraintConfig.UNCONSTRAINED.withMultiValued("tags", null));
	}
	
	@Test
	void withCustom() {
		QueryConstraintConfig config = QueryConstraintConfig.UNCONSTRAINED.withCustom(value -> {});
		
		assertTrue(config.custom().isPresent());
	}
	
	@Test
	void withCustomNull() {
		assertThrows(NullPointerException.class, () -> QueryConstraintConfig.UNCONSTRAINED.withCustom(null));
	}
	
	@Test
	void validateUnconstrained() {
		QueryConstraintConfig config = QueryConstraintConfig.UNCONSTRAINED;
		
		assertDoesNotThrow(() -> config.validate(Map.of()));
		assertDoesNotThrow(() -> config.validate(Map.of("key", List.of("value"))));
		assertDoesNotThrow(() -> config.validate(Map.of("a", List.of("1"), "b", List.of("2"))));
	}
	
	@Test
	void validateWithNull() {
		assertThrows(NullPointerException.class, () -> QueryConstraintConfig.UNCONSTRAINED.validate(null));
	}
	
	@Test
	void validateEqualTo() {
		Map<String, List<String>> expected = Map.of("key", List.of("value"));
		QueryConstraintConfig config = QueryConstraintConfig.UNCONSTRAINED.withEqualTo(expected);
		
		assertDoesNotThrow(() -> config.validate(Map.of("key", List.of("value"))));
		assertThrows(ConstraintViolateException.class, () -> config.validate(Map.of("key", List.of("other"))));
		assertThrows(ConstraintViolateException.class, () -> config.validate(Map.of("other", List.of("value"))));
	}
	
	@Test
	void validateNotEqualTo() {
		Map<String, List<String>> excluded = Map.of("key", List.of("value"));
		QueryConstraintConfig config = QueryConstraintConfig.UNCONSTRAINED.withNotEqualTo(excluded);
		
		assertDoesNotThrow(() -> config.validate(Map.of("key", List.of("other"))));
		assertDoesNotThrow(() -> config.validate(Map.of("other", List.of("value"))));
		assertThrows(ConstraintViolateException.class, () -> config.validate(Map.of("key", List.of("value"))));
	}
	
	@Test
	void validateIn() {
		Map<String, List<String>> value1 = Map.of("key", List.of("a"));
		Map<String, List<String>> value2 = Map.of("key", List.of("b"));
		QueryConstraintConfig config = QueryConstraintConfig.UNCONSTRAINED.withIn(List.of(value1, value2));
		
		assertDoesNotThrow(() -> config.validate(Map.of("key", List.of("a"))));
		assertDoesNotThrow(() -> config.validate(Map.of("key", List.of("b"))));
		assertThrows(ConstraintViolateException.class, () -> config.validate(Map.of("key", List.of("c"))));
	}
	
	@Test
	void validateNotIn() {
		Map<String, List<String>> excluded = Map.of("key", List.of("blocked"));
		QueryConstraintConfig config = QueryConstraintConfig.UNCONSTRAINED.withNotIn(List.of(excluded));
		
		assertDoesNotThrow(() -> config.validate(Map.of("key", List.of("allowed"))));
		assertThrows(ConstraintViolateException.class, () -> config.validate(Map.of("key", List.of("blocked"))));
	}
	
	@Test
	void validateMinSize() {
		QueryConstraintConfig config = QueryConstraintConfig.UNCONSTRAINED.withSize(SizeConstraintConfig.UNCONSTRAINED.withMinSize(2));
		
		assertDoesNotThrow(() -> config.validate(Map.of("a", List.of("1"), "b", List.of("2"))));
		assertDoesNotThrow(() -> config.validate(Map.of("a", List.of("1"), "b", List.of("2"), "c", List.of("3"))));
		assertThrows(ConstraintViolateException.class, () -> config.validate(Map.of("a", List.of("1"))));
	}
	
	@Test
	void validateMaxSize() {
		QueryConstraintConfig config = QueryConstraintConfig.UNCONSTRAINED.withSize(SizeConstraintConfig.UNCONSTRAINED.withMaxSize(2));
		
		assertDoesNotThrow(() -> config.validate(Map.of()));
		assertDoesNotThrow(() -> config.validate(Map.of("a", List.of("1"))));
		assertDoesNotThrow(() -> config.validate(Map.of("a", List.of("1"), "b", List.of("2"))));
		assertThrows(ConstraintViolateException.class, () -> config.validate(Map.of("a", List.of("1"), "b", List.of("2"), "c", List.of("3"))));
	}
	
	@Test
	void validateRequiredKeys() {
		QueryConstraintConfig config = QueryConstraintConfig.UNCONSTRAINED.withRequiredKeys(List.of("name"));
		
		assertDoesNotThrow(() -> config.validate(Map.of("name", List.of("value"))));
		assertDoesNotThrow(() -> config.validate(Map.of("name", List.of("value"), "extra", List.of("data"))));
		assertThrows(ConstraintViolateException.class, () -> config.validate(Map.of("other", List.of("value"))));
	}
	
	@Test
	void validateForbiddenKeys() {
		QueryConstraintConfig config = QueryConstraintConfig.UNCONSTRAINED.withForbiddenKeys(List.of("secret"));
		
		assertDoesNotThrow(() -> config.validate(Map.of("name", List.of("value"))));
		assertThrows(ConstraintViolateException.class, () -> config.validate(Map.of("secret", List.of("value"))));
	}
	
	@Test
	void validateAllowedKeys() {
		QueryConstraintConfig config = QueryConstraintConfig.UNCONSTRAINED.withAllowedKeys(List.of("name", "email"));
		
		assertDoesNotThrow(() -> config.validate(Map.of("name", List.of("value"))));
		assertDoesNotThrow(() -> config.validate(Map.of("name", List.of("a"), "email", List.of("b"))));
		assertThrows(ConstraintViolateException.class, () -> config.validate(Map.of("other", List.of("value"))));
	}
}
