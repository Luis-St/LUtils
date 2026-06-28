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

package net.luis.utils.io.database.type;

import net.luis.utils.function.throwable.ThrowableFunction;
import net.luis.utils.io.database.exception.SqlClientException;
import net.luis.utils.io.database.exception.database.statement.SqlStatementBindException;
import net.luis.utils.io.database.type.parameter.SqlLengthParameter;
import net.luis.utils.io.database.type.parameter.SqlParameter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link MappedParameterizableSqlType}.<br>
 *
 * @author Luis-St
 */
class MappedParameterizableSqlTypeTest {
	
	private static final ParameterizableSqlType<String, SqlLengthParameter> SOURCE = SqlTypes.STRING;
	private static final ThrowableFunction<Integer, String, SqlStatementBindException> TO_SOURCE = value -> value == null ? null : String.valueOf(value);
	private static final ThrowableFunction<String, Integer, SqlClientException> TO_TARGET = Integer::parseInt;
	private static final ThrowableFunction<Integer, String, SqlStatementBindException> TO_SOURCE_OTHER = value -> value == null ? null : Integer.toString(value);
	private static final ThrowableFunction<String, Integer, SqlClientException> TO_TARGET_OTHER = value -> Integer.valueOf(value);
	private static final ThrowableFunction<Long, String, SqlStatementBindException> TO_SOURCE_LONG = value -> value == null ? null : String.valueOf(value);
	private static final ThrowableFunction<String, Long, SqlClientException> TO_TARGET_LONG = Long::parseLong;
	
	@Test
	void constructWithAllArguments() {
		MappedParameterizableSqlType<String, Integer, SqlLengthParameter> mapped = new MappedParameterizableSqlType<>(SOURCE, Integer.class, TO_SOURCE, TO_TARGET);
		assertEquals(SOURCE, mapped.sourceType());
		assertEquals(Integer.class, mapped.javaType());
		assertSame(TO_SOURCE, mapped.fromTargetToSource());
		assertSame(TO_TARGET, mapped.fromSourceToTarget());
	}
	
	@Test
	void constructWithNullSourceType() {
		assertThrows(NullPointerException.class, () -> new MappedParameterizableSqlType<>(null, Integer.class, TO_SOURCE, TO_TARGET));
	}
	
	@Test
	void constructWithNullJavaType() {
		assertThrows(NullPointerException.class, () -> new MappedParameterizableSqlType<>(SOURCE, null, TO_SOURCE, TO_TARGET));
	}
	
	@Test
	void constructWithNullFromTargetToSource() {
		assertThrows(NullPointerException.class, () -> new MappedParameterizableSqlType<>(SOURCE, Integer.class, null, TO_TARGET));
	}
	
	@Test
	void constructWithNullFromSourceToTarget() {
		assertThrows(NullPointerException.class, () -> new MappedParameterizableSqlType<>(SOURCE, Integer.class, TO_SOURCE, null));
	}
	
	@Test
	void parameterTypeDelegatesToSource() {
		MappedParameterizableSqlType<String, Integer, SqlLengthParameter> mapped = new MappedParameterizableSqlType<>(SOURCE, Integer.class, TO_SOURCE, TO_TARGET);
		assertEquals(SOURCE.parameterType(), mapped.parameterType());
	}
	
	@Test
	void configureProducesMappedSqlType() {
		MappedParameterizableSqlType<String, Integer, SqlLengthParameter> mapped = new MappedParameterizableSqlType<>(SOURCE, Integer.class, TO_SOURCE, TO_TARGET);
		SqlType<Integer> configured = mapped.configure(SqlParameter.length(10));
		assertInstanceOf(MappedSqlType.class, configured);
		assertEquals(Integer.class, configured.javaType());
	}
	
	@Test
	void equalsSameFields() {
		assertEquals(new MappedParameterizableSqlType<>(SOURCE, Integer.class, TO_SOURCE, TO_TARGET), new MappedParameterizableSqlType<>(SOURCE, Integer.class, TO_SOURCE, TO_TARGET));
	}
	
	@Test
	void equalsDifferentSourceType() {
		assertNotEquals(new MappedParameterizableSqlType<>(SOURCE, Integer.class, TO_SOURCE, TO_TARGET), new MappedParameterizableSqlType<>(SqlTypes.UNICODE_STRING, Integer.class, TO_SOURCE, TO_TARGET));
	}
	
	@Test
	void equalsDifferentJavaType() {
		assertNotEquals(new MappedParameterizableSqlType<>(SOURCE, Integer.class, TO_SOURCE, TO_TARGET), new MappedParameterizableSqlType<>(SOURCE, Long.class, TO_SOURCE_LONG, TO_TARGET_LONG));
	}
	
	@Test
	void equalsDifferentFromTargetToSource() {
		assertNotEquals(new MappedParameterizableSqlType<>(SOURCE, Integer.class, TO_SOURCE, TO_TARGET), new MappedParameterizableSqlType<>(SOURCE, Integer.class, TO_SOURCE_OTHER, TO_TARGET));
	}
	
	@Test
	void equalsDifferentFromSourceToTarget() {
		assertNotEquals(new MappedParameterizableSqlType<>(SOURCE, Integer.class, TO_SOURCE, TO_TARGET), new MappedParameterizableSqlType<>(SOURCE, Integer.class, TO_SOURCE, TO_TARGET_OTHER));
	}
	
	@Test
	void equalsWithNull() {
		assertNotEquals(null, new MappedParameterizableSqlType<>(SOURCE, Integer.class, TO_SOURCE, TO_TARGET));
	}
	
	@Test
	void equalsWithDifferentType() {
		assertNotEquals("string", new MappedParameterizableSqlType<>(SOURCE, Integer.class, TO_SOURCE, TO_TARGET));
	}
	
	@Test
	void gettersReturnConfiguredFunctions() {
		MappedParameterizableSqlType<String, Integer, SqlLengthParameter> mapped = new MappedParameterizableSqlType<>(SOURCE, Integer.class, TO_SOURCE, TO_TARGET);
		assertSame(TO_SOURCE, mapped.fromTargetToSource());
		assertSame(TO_TARGET, mapped.fromSourceToTarget());
	}
	
	@Test
	void hashCodeConsistentForEqualTypes() {
		assertEquals(new MappedParameterizableSqlType<>(SOURCE, Integer.class, TO_SOURCE, TO_TARGET).hashCode(), new MappedParameterizableSqlType<>(SOURCE, Integer.class, TO_SOURCE, TO_TARGET).hashCode());
	}
	
	@Test
	void toStringContainsSourceAndJavaType() {
		String string = new MappedParameterizableSqlType<>(SOURCE, Integer.class, TO_SOURCE, TO_TARGET).toString();
		assertTrue(string.contains("sourceType="));
		assertTrue(string.contains("javaType="));
	}
	
	@Test
	void configureConversionRoundTrips() throws Exception {
		MappedParameterizableSqlType<String, Integer, SqlLengthParameter> mapped = new MappedParameterizableSqlType<>(SOURCE, Integer.class, TO_SOURCE, TO_TARGET);
		SqlType<Integer> configured = mapped.configure(SqlParameter.length(10));
		assertInstanceOf(MappedSqlType.class, configured);
		@SuppressWarnings("unchecked")
		MappedSqlType<String, Integer> result = (MappedSqlType<String, Integer>) configured;
		
		String source = result.fromTargetToSource().apply(42);
		assertEquals("42", source);
		assertEquals(42, result.fromSourceToTarget().apply(source));
	}
}
