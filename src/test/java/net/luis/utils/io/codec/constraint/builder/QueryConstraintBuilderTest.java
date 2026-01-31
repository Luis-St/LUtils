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

import net.luis.utils.io.codec.constraint.core.Constraint;
import net.luis.utils.io.codec.constraint.config.SizeConstraintConfig;
import net.luis.utils.io.codec.constraint.config.io.QueryConstraintConfig;
import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link QueryConstraintBuilder}.<br>
 *
 * @author Luis-St
 */
class QueryConstraintBuilderTest {
	
	@Test
	void constructEmpty() {
		QueryConstraintBuilder builder = new QueryConstraintBuilder();
		QueryConstraintConfig config = builder.build();
		
		assertNotNull(config);
		assertEquals(QueryConstraintConfig.UNCONSTRAINED, config);
	}
	
	@Test
	void constructWithInitialConfig() {
		QueryConstraintConfig initialConfig = QueryConstraintConfig.UNCONSTRAINED.withSize(SizeConstraintConfig.UNCONSTRAINED.withMinSize(1));
		QueryConstraintBuilder builder = new QueryConstraintBuilder(initialConfig);
		QueryConstraintConfig config = builder.build();
		
		assertNotNull(config);
		assertEquals(initialConfig, config);
		assertTrue(config.size().isPresent());
	}
	
	@Test
	void constructWithNullInitialConfig() {
		assertThrows(NullPointerException.class, () -> new QueryConstraintBuilder(null));
	}
	
	@Test
	void equalToReturnsBuilder() {
		QueryConstraintBuilder builder = new QueryConstraintBuilder();
		Map<String, List<String>> value = Map.of("key", List.of("value"));
		assertSame(builder, builder.equalTo(value));
		assertTrue(builder.build().equalTo().isPresent());
	}
	
	@Test
	void equalToWithNullValue() {
		QueryConstraintBuilder builder = new QueryConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.equalTo(null));
	}
	
	@Test
	void notEqualToReturnsBuilder() {
		QueryConstraintBuilder builder = new QueryConstraintBuilder();
		Map<String, List<String>> value = Map.of("key", List.of("value"));
		assertSame(builder, builder.notEqualTo(value));
		assertTrue(builder.build().equalTo().isPresent());
	}
	
	@Test
	void notEqualToWithNullValue() {
		QueryConstraintBuilder builder = new QueryConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.notEqualTo(null));
	}
	
	@Test
	void inReturnsBuilder() {
		QueryConstraintBuilder builder = new QueryConstraintBuilder();
		List<Map<String, List<String>>> values = List.of(
			Map.of("key1", List.of("value1")),
			Map.of("key2", List.of("value2"))
		);
		assertSame(builder, builder.in(values));
		assertTrue(builder.build().in().isPresent());
	}
	
	@Test
	void inWithNullValues() {
		QueryConstraintBuilder builder = new QueryConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.in(null));
	}
	
	@Test
	void notInReturnsBuilder() {
		QueryConstraintBuilder builder = new QueryConstraintBuilder();
		List<Map<String, List<String>>> values = List.of(
			Map.of("key1", List.of("value1")),
			Map.of("key2", List.of("value2"))
		);
		assertSame(builder, builder.notIn(values));
		assertTrue(builder.build().in().isPresent());
	}
	
	@Test
	void notInWithNullValues() {
		QueryConstraintBuilder builder = new QueryConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.notIn(null));
	}
	
	@Test
	void customReturnsBuilder() {
		QueryConstraintBuilder builder = new QueryConstraintBuilder();
		Constraint<Map<String, List<String>>> constraint = value -> Result.success(null);
		assertSame(builder, builder.custom(constraint));
		assertTrue(builder.build().custom().isPresent());
	}
	
	@Test
	void customWithNullConstraint() {
		QueryConstraintBuilder builder = new QueryConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.custom(null));
	}
	
	@Test
	void sizeReturnsBuilder() {
		QueryConstraintBuilder builder = new QueryConstraintBuilder();
		assertSame(builder, builder.size(b -> b.minSize(1).maxSize(10)));
		
		QueryConstraintConfig config = builder.build();
		assertTrue(config.size().isPresent());
	}
	
	@Test
	void sizeWithNullBuilder() {
		QueryConstraintBuilder builder = new QueryConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.size(null));
	}
	
	@Test
	void requiredKeyReturnsBuilder() {
		QueryConstraintBuilder builder = new QueryConstraintBuilder();
		assertSame(builder, builder.requiredKey("id"));
		assertTrue(builder.build().requiredKeys().isPresent());
	}
	
	@Test
	void requiredKeyWithNullKey() {
		QueryConstraintBuilder builder = new QueryConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.requiredKey(null));
	}
	
	@Test
	void requiredKeysReturnsBuilder() {
		QueryConstraintBuilder builder = new QueryConstraintBuilder();
		assertSame(builder, builder.requiredKeys(List.of("id", "name")));
		assertTrue(builder.build().requiredKeys().isPresent());
	}
	
	@Test
	void requiredKeysWithNullKeys() {
		QueryConstraintBuilder builder = new QueryConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.requiredKeys(null));
	}
	
	@Test
	void forbiddenKeyReturnsBuilder() {
		QueryConstraintBuilder builder = new QueryConstraintBuilder();
		assertSame(builder, builder.forbiddenKey("password"));
		assertTrue(builder.build().forbiddenKeys().isPresent());
	}
	
	@Test
	void forbiddenKeyWithNullKey() {
		QueryConstraintBuilder builder = new QueryConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.forbiddenKey(null));
	}
	
	@Test
	void forbiddenKeysReturnsBuilder() {
		QueryConstraintBuilder builder = new QueryConstraintBuilder();
		assertSame(builder, builder.forbiddenKeys(List.of("password", "secret")));
		assertTrue(builder.build().forbiddenKeys().isPresent());
	}
	
	@Test
	void forbiddenKeysWithNullKeys() {
		QueryConstraintBuilder builder = new QueryConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.forbiddenKeys(null));
	}
	
	@Test
	void allowedKeyReturnsBuilder() {
		QueryConstraintBuilder builder = new QueryConstraintBuilder();
		assertSame(builder, builder.allowedKey("id"));
		assertTrue(builder.build().allowedKeys().isPresent());
	}
	
	@Test
	void allowedKeyWithNullKey() {
		QueryConstraintBuilder builder = new QueryConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.allowedKey(null));
	}
	
	@Test
	void allowedKeysReturnsBuilder() {
		QueryConstraintBuilder builder = new QueryConstraintBuilder();
		assertSame(builder, builder.allowedKeys(List.of("id", "name", "page")));
		assertTrue(builder.build().allowedKeys().isPresent());
	}
	
	@Test
	void allowedKeysWithNullKeys() {
		QueryConstraintBuilder builder = new QueryConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.allowedKeys(null));
	}
	
	@Test
	void nonNullKeysReturnsBuilder() {
		QueryConstraintBuilder builder = new QueryConstraintBuilder();
		assertSame(builder, builder.nonNullKeys());
		assertTrue(builder.build().nonNullKeys().isPresent());
	}
	
	@Test
	void uniqueValuesReturnsBuilder() {
		QueryConstraintBuilder builder = new QueryConstraintBuilder();
		assertSame(builder, builder.uniqueValues());
		assertTrue(builder.build().uniqueValues().isPresent());
	}
	
	@Test
	void nonNullValuesReturnsBuilder() {
		QueryConstraintBuilder builder = new QueryConstraintBuilder();
		assertSame(builder, builder.nonNullValues());
		assertTrue(builder.build().nonNullValues().isPresent());
	}
	
	@Test
	void valueReturnsBuilder() {
		QueryConstraintBuilder builder = new QueryConstraintBuilder();
		assertSame(builder, builder.value("id", b -> b.numeric()));
		assertTrue(builder.build().valueConstraints().isPresent());
	}
	
	@Test
	void valueWithNullBuilder() {
		QueryConstraintBuilder builder = new QueryConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.value("id", null));
	}
	
	@Test
	void valuesStringReturnsBuilder() {
		QueryConstraintBuilder builder = new QueryConstraintBuilder();
		assertSame(builder, builder.values("id.*", b -> b.numeric()));
		assertTrue(builder.build().patternValueConstraints().isPresent());
	}
	
	@Test
	void valuesStringWithNullBuilder() {
		QueryConstraintBuilder builder = new QueryConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.values("id.*", null));
	}
	
	@Test
	void valuesPatternReturnsBuilder() {
		QueryConstraintBuilder builder = new QueryConstraintBuilder();
		assertSame(builder, builder.values(Pattern.compile("id.*"), b -> b.numeric()));
		assertTrue(builder.build().patternValueConstraints().isPresent());
	}
	
	@Test
	void valuesPatternWithNullBuilder() {
		QueryConstraintBuilder builder = new QueryConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.values(Pattern.compile("id.*"), null));
	}
	
	@Test
	void singleValuedReturnsBuilder() {
		QueryConstraintBuilder builder = new QueryConstraintBuilder();
		assertSame(builder, builder.singleValued());
		assertTrue(builder.build().singleValued().isPresent());
	}
	
	@Test
	void multiValuedReturnsBuilder() {
		QueryConstraintBuilder builder = new QueryConstraintBuilder();
		assertSame(builder, builder.multiValued("tags", b -> b.minSize(1)));
		assertTrue(builder.build().multiValuedConstraints().isPresent());
	}
	
	@Test
	void multiValuedWithNullBuilder() {
		QueryConstraintBuilder builder = new QueryConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.multiValued("tags", null));
	}
	
	@Test
	void buildReturnsCorrectConfig() {
		QueryConstraintBuilder builder = new QueryConstraintBuilder();
		builder.size(b -> b.minSize(1).maxSize(10)).requiredKey("id");
		
		QueryConstraintConfig config = builder.build();
		
		assertNotNull(config);
		assertTrue(config.size().isPresent());
		assertTrue(config.requiredKeys().isPresent());
	}
	
	@Test
	void methodChainingWorks() {
		QueryConstraintBuilder builder = new QueryConstraintBuilder();
		
		QueryConstraintConfig config = builder
			.size(b -> b.minSize(1).maxSize(10))
			.requiredKeys(List.of("id", "name"))
			.forbiddenKey("password")
			.nonNullValues()
			.value("id", b -> b.numeric())
			.build();
		
		assertNotNull(config);
		assertTrue(config.size().isPresent());
		assertTrue(config.requiredKeys().isPresent());
		assertTrue(config.forbiddenKeys().isPresent());
		assertTrue(config.nonNullValues().isPresent());
		assertTrue(config.valueConstraints().isPresent());
	}
}
