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

import net.luis.utils.io.data.json.JsonElement;
import net.luis.utils.io.data.json.JsonPrimitive;
import net.luis.utils.io.data.xml.XmlElement;
import net.luis.utils.io.database.exception.SqlClientException;
import net.luis.utils.io.database.type.parameter.*;
import net.luis.utils.io.network.address.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.*;
import java.time.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlTypes}.<br>
 *
 * @author Luis-St
 */
@SuppressWarnings("unchecked")
class SqlTypesTest {
	
	@Test
	void enumNameWithNullClass() {
		assertThrows(NullPointerException.class, () -> SqlTypes.enumName((Class<TestEnum>) null));
	}
	
	@Test
	void enumNameWithNonEnumClass() {
		assertThrows(IllegalArgumentException.class, () -> SqlTypes.enumName((Class<TestEnum>) (Class<?>) String.class));
	}
	
	@Test
	void enumOrdinalWithNullClass() {
		assertThrows(NullPointerException.class, () -> SqlTypes.enumOrdinal((Class<TestEnum>) null));
	}
	
	@Test
	void enumOrdinalWithNonEnumClass() {
		assertThrows(IllegalArgumentException.class, () -> SqlTypes.enumOrdinal((Class<TestEnum>) (Class<?>) String.class));
	}
	
	@Test
	void enumOrdinalWithNegativeOrdinalThrows() {
		MappedSqlType<Integer, TestEnum> type = (MappedSqlType<Integer, TestEnum>) SqlTypes.enumOrdinal(TestEnum.class);
		assertThrows(SqlClientException.class, () -> type.fromSourceToTarget().apply(-1));
	}
	
	@Test
	void enumOrdinalWithOrdinalAboveRangeThrows() {
		MappedSqlType<Integer, TestEnum> type = (MappedSqlType<Integer, TestEnum>) SqlTypes.enumOrdinal(TestEnum.class);
		assertThrows(SqlClientException.class, () -> type.fromSourceToTarget().apply(TestEnum.values().length));
	}
	
	@Test
	void characterFromEmptyStringThrows() {
		MappedSqlType<String, Character> type = (MappedSqlType<String, Character>) SqlTypes.CHARACTER;
		assertThrows(SqlClientException.class, () -> type.fromSourceToTarget().apply(""));
	}
	
	@Test
	void enumNameWithUnknownNameThrows() {
		MappedSqlType<String, TestEnum> type = (MappedSqlType<String, TestEnum>) SqlTypes.enumName(TestEnum.class);
		assertThrows(IllegalArgumentException.class, () -> type.fromSourceToTarget().apply("UNKNOWN"));
	}
	
	@Test
	void uuidFromInvalidStringThrows() {
		MappedSqlType<String, UUID> type = (MappedSqlType<String, UUID>) SqlTypes.UUID;
		assertThrows(IllegalArgumentException.class, () -> type.fromSourceToTarget().apply("not-a-uuid"));
	}
	
	@Test
	void scalarConstantsHaveExpectedDescriptors() {
		assertEquals(Types.BOOLEAN, SqlTypes.BOOLEAN.jdbcType());
		assertEquals(Boolean.class, SqlTypes.BOOLEAN.javaType());
		assertEquals(Types.TINYINT, SqlTypes.BYTE.jdbcType());
		assertEquals(Byte.class, SqlTypes.BYTE.javaType());
		assertEquals(Types.SMALLINT, SqlTypes.SHORT.jdbcType());
		assertEquals(Short.class, SqlTypes.SHORT.javaType());
		assertEquals(Types.INTEGER, SqlTypes.INTEGER.jdbcType());
		assertEquals(Integer.class, SqlTypes.INTEGER.javaType());
		assertEquals(Types.BIGINT, SqlTypes.LONG.jdbcType());
		assertEquals(Long.class, SqlTypes.LONG.javaType());
		assertEquals(Types.REAL, SqlTypes.REAL.jdbcType());
		assertEquals(Float.class, SqlTypes.REAL.javaType());
		assertEquals(Types.FLOAT, SqlTypes.FLOAT.jdbcType());
		assertEquals(Double.class, SqlTypes.FLOAT.javaType());
		assertEquals(Types.DOUBLE, SqlTypes.DOUBLE.jdbcType());
		assertEquals(Double.class, SqlTypes.DOUBLE.javaType());
		assertEquals(Types.LONGVARCHAR, SqlTypes.TEXT.jdbcType());
		assertEquals(String.class, SqlTypes.TEXT.javaType());
		assertEquals(Types.LONGNVARCHAR, SqlTypes.UNICODE_TEXT.jdbcType());
		assertEquals(String.class, SqlTypes.UNICODE_TEXT.javaType());
		assertEquals(Types.CLOB, SqlTypes.CLOB.jdbcType());
		assertEquals(Clob.class, SqlTypes.CLOB.javaType());
		assertEquals(Types.NCLOB, SqlTypes.NCLOB.jdbcType());
		assertEquals(NClob.class, SqlTypes.NCLOB.javaType());
		assertEquals(Types.LONGVARBINARY, SqlTypes.LARGE_BYTES.jdbcType());
		assertEquals(byte[].class, SqlTypes.LARGE_BYTES.javaType());
		assertEquals(Types.BLOB, SqlTypes.BLOB.jdbcType());
		assertEquals(Blob.class, SqlTypes.BLOB.javaType());
		assertEquals(Types.DATE, SqlTypes.LOCAL_DATE.jdbcType());
		assertEquals(LocalDate.class, SqlTypes.LOCAL_DATE.javaType());
	}
	
	@Test
	void parameterizableConstantsHaveExpectedParameterType() {
		assertEquals(SqlPrecisionParameter.class, SqlTypes.NUMERIC.parameterType());
		assertEquals(SqlPrecisionParameter.class, SqlTypes.DECIMAL.parameterType());
		assertEquals(SqlLengthParameter.class, SqlTypes.FIXED_STRING.parameterType());
		assertEquals(SqlLengthParameter.class, SqlTypes.STRING.parameterType());
		assertEquals(SqlLengthParameter.class, SqlTypes.UNICODE_FIXED_STRING.parameterType());
		assertEquals(SqlLengthParameter.class, SqlTypes.UNICODE_STRING.parameterType());
		assertEquals(SqlLengthParameter.class, SqlTypes.FIXED_BYTES.parameterType());
		assertEquals(SqlLengthParameter.class, SqlTypes.BYTES.parameterType());
		assertEquals(SqlFractionalParameter.class, SqlTypes.LOCAL_TIME.parameterType());
		assertEquals(SqlFractionalParameter.class, SqlTypes.LOCAL_DATE_TIME.parameterType());
		assertEquals(SqlFractionalParameter.class, SqlTypes.OFFSET_TIME.parameterType());
		assertEquals(SqlFractionalParameter.class, SqlTypes.OFFSET_DATE_TIME.parameterType());
	}
	
	@Test
	void mappedConstantsHaveExpectedJavaType() {
		assertEquals(BigInteger.class, ((MappedParameterizableSqlType<?, ?, ?>) SqlTypes.BIG_INTEGER).javaType());
		assertEquals(Character.class, SqlTypes.CHARACTER.javaType());
		assertEquals(UUID.class, SqlTypes.UUID.javaType());
		assertEquals(JsonElement.class, SqlTypes.JSON.javaType());
		assertEquals(XmlElement.class, SqlTypes.XML.javaType());
		assertEquals(ZonedDateTime.class, ((MappedParameterizableSqlType<?, ?, ?>) SqlTypes.ZONED_DATE_TIME).javaType());
		assertEquals(Instant.class, ((MappedParameterizableSqlType<?, ?, ?>) SqlTypes.INSTANT).javaType());
		assertEquals(Year.class, SqlTypes.YEAR.javaType());
		assertEquals(Month.class, SqlTypes.MONTH.javaType());
		assertEquals(DayOfWeek.class, SqlTypes.DAY_OF_WEEK.javaType());
		assertEquals(Duration.class, SqlTypes.DURATION.javaType());
		assertEquals(IpAddress.class, SqlTypes.IP_ADDRESS.javaType());
		assertEquals(IpNetwork.class, SqlTypes.IP_NETWORK.javaType());
	}
	
	@Test
	void enumNameRoundTrips() throws Exception {
		MappedSqlType<String, TestEnum> type = (MappedSqlType<String, TestEnum>) SqlTypes.enumName(TestEnum.class);
		assertEquals("A", type.fromTargetToSource().apply(TestEnum.A));
		assertEquals(TestEnum.A, type.fromSourceToTarget().apply("A"));
		assertEquals(TestEnum.class, type.javaType());
	}
	
	@Test
	void enumOrdinalRoundTrips() throws Exception {
		MappedSqlType<Integer, TestEnum> type = (MappedSqlType<Integer, TestEnum>) SqlTypes.enumOrdinal(TestEnum.class);
		assertEquals(TestEnum.A.ordinal(), type.fromTargetToSource().apply(TestEnum.A));
		assertEquals(TestEnum.values()[0], type.fromSourceToTarget().apply(0));
	}
	
	@Test
	void enumOrdinalBoundaryLastConstant() throws Exception {
		MappedSqlType<Integer, TestEnum> type = (MappedSqlType<Integer, TestEnum>) SqlTypes.enumOrdinal(TestEnum.class);
		assertEquals(TestEnum.C, type.fromSourceToTarget().apply(TestEnum.values().length - 1));
	}
	
	@Test
	void characterFromNonEmptyStringReturnsFirstChar() throws Exception {
		MappedSqlType<String, Character> type = (MappedSqlType<String, Character>) SqlTypes.CHARACTER;
		assertEquals('a', type.fromSourceToTarget().apply("abc"));
	}
	
	@Test
	void characterFromTargetToSourceConvertsChar() throws Exception {
		MappedSqlType<String, Character> type = (MappedSqlType<String, Character>) SqlTypes.CHARACTER;
		assertEquals("x", type.fromTargetToSource().apply('x'));
	}
	
	@Test
	void nullableWrapperReturnsNullForNullInput() throws Exception {
		MappedSqlType<String, Character> type = (MappedSqlType<String, Character>) SqlTypes.CHARACTER;
		assertNull(type.fromTargetToSource().apply(null));
	}
	
	@Test
	void readXmlPrependsMissingDeclaration() throws Exception {
		MappedSqlType<String, XmlElement> type = (MappedSqlType<String, XmlElement>) SqlTypes.XML;
		XmlElement element = type.fromSourceToTarget().apply("<root/>");
		assertEquals("root", element.getName());
	}
	
	@Test
	void readXmlKeepsExistingDeclaration() throws Exception {
		MappedSqlType<String, XmlElement> type = (MappedSqlType<String, XmlElement>) SqlTypes.XML;
		XmlElement element = type.fromSourceToTarget().apply("<?xml version=\"1.0\" encoding=\"UTF-8\"?><root/>");
		assertEquals("root", element.getName());
	}
	
	@Test
	void jsonRoundTrips() throws Exception {
		MappedSqlType<String, JsonElement> type = (MappedSqlType<String, JsonElement>) SqlTypes.JSON;
		JsonElement element = new JsonPrimitive("test");
		String encoded = type.fromTargetToSource().apply(element);
		assertEquals(element, type.fromSourceToTarget().apply(encoded));
	}
	
	@Test
	void uuidRoundTrips() throws Exception {
		MappedSqlType<String, UUID> type = (MappedSqlType<String, UUID>) SqlTypes.UUID;
		UUID value = UUID.randomUUID();
		String encoded = type.fromTargetToSource().apply(value);
		assertEquals(value, type.fromSourceToTarget().apply(encoded));
	}
	
	@Test
	void bigIntegerConfigureRoundTrips() throws Exception {
		MappedSqlType<BigDecimal, BigInteger> type = (MappedSqlType<BigDecimal, BigInteger>) SqlTypes.BIG_INTEGER.configure(SqlParameter.precision(38, 0));
		BigInteger value = BigInteger.valueOf(123456789);
		BigDecimal encoded = type.fromTargetToSource().apply(value);
		assertEquals(value, type.fromSourceToTarget().apply(encoded));
	}
	
	@Test
	void instantConfigureRoundTrips() throws Exception {
		MappedSqlType<OffsetDateTime, Instant> type = (MappedSqlType<OffsetDateTime, Instant>) SqlTypes.INSTANT.configure(SqlParameter.fractional(6));
		Instant value = Instant.parse("2020-01-15T10:15:30Z");
		OffsetDateTime encoded = type.fromTargetToSource().apply(value);
		assertEquals(value, type.fromSourceToTarget().apply(encoded));
	}
	
	@Test
	void zonedDateTimeConfigureRoundTrips() throws Exception {
		MappedSqlType<OffsetDateTime, ZonedDateTime> type = (MappedSqlType<OffsetDateTime, ZonedDateTime>) SqlTypes.ZONED_DATE_TIME.configure(SqlParameter.fractional(6));
		ZonedDateTime value = ZonedDateTime.parse("2020-01-15T10:15:30+01:00");
		OffsetDateTime encoded = type.fromTargetToSource().apply(value);
		assertEquals(value.toInstant(), type.fromSourceToTarget().apply(encoded).toInstant());
	}
	
	@Test
	void ipAddressRoundTrips() throws Exception {
		MappedSqlType<String, IpAddress<?>> type = (MappedSqlType<String, IpAddress<?>>) SqlTypes.IP_ADDRESS;
		IpAddress<?> value = IpAddresses.parse("127.0.0.1");
		String encoded = type.fromTargetToSource().apply(value);
		assertEquals(value, type.fromSourceToTarget().apply(encoded));
	}
	
	@Test
	void byIdentifierWithNullIdentifier() {
		assertThrows(NullPointerException.class, () -> SqlTypes.byIdentifier(null));
	}
	
	@Test
	void byIdentifierReturnsTypeWhenKnown() {
		Optional<SqlType<?>> resolved = SqlTypes.byIdentifier("uuid");
		assertTrue(resolved.isPresent());
		assertSame(SqlTypes.UUID, resolved.get());
	}
	
	@Test
	void byIdentifierReturnsEmptyWhenUnknown() {
		assertEquals(Optional.empty(), assertDoesNotThrow(() -> SqlTypes.byIdentifier("not_a_type")));
		assertEquals(Optional.empty(), assertDoesNotThrow(() -> SqlTypes.byIdentifier("")));
	}
	
	@Test
	void byIdentifierIsCaseSensitive() {
		assertEquals(Optional.empty(), SqlTypes.byIdentifier("UUID"));
		assertEquals(Optional.empty(), SqlTypes.byIdentifier("Json"));
	}
	
	@Test
	void byIdentifierResolvesEveryIdentifiedType() {
		assertSame(SqlTypes.CHARACTER, SqlTypes.byIdentifier("character").orElseThrow());
		assertSame(SqlTypes.YEAR, SqlTypes.byIdentifier("year").orElseThrow());
		assertSame(SqlTypes.MONTH, SqlTypes.byIdentifier("month").orElseThrow());
		assertSame(SqlTypes.DAY_OF_WEEK, SqlTypes.byIdentifier("day_of_week").orElseThrow());
		assertSame(SqlTypes.DURATION, SqlTypes.byIdentifier("duration").orElseThrow());
		assertSame(SqlTypes.UUID, SqlTypes.byIdentifier("uuid").orElseThrow());
		assertSame(SqlTypes.JSON, SqlTypes.byIdentifier("json").orElseThrow());
		assertSame(SqlTypes.XML, SqlTypes.byIdentifier("xml").orElseThrow());
		assertSame(SqlTypes.IP_ADDRESS, SqlTypes.byIdentifier("ip_address").orElseThrow());
		assertSame(SqlTypes.IP_NETWORK, SqlTypes.byIdentifier("ip_network").orElseThrow());
	}
	
	@Test
	void identifiedConstantsCarryTheirIdentifier() {
		Map<String, SqlType<?>> expected = Map.of(
			"character", SqlTypes.CHARACTER, "year", SqlTypes.YEAR, "month", SqlTypes.MONTH,
			"day_of_week", SqlTypes.DAY_OF_WEEK, "duration", SqlTypes.DURATION, "uuid", SqlTypes.UUID,
			"json", SqlTypes.JSON, "xml", SqlTypes.XML, "ip_address", SqlTypes.IP_ADDRESS, "ip_network", SqlTypes.IP_NETWORK
		);
		for (Map.Entry<String, SqlType<?>> entry : expected.entrySet()) {
			MappedSqlType<?, ?> mapped = assertInstanceOf(MappedSqlType.class, entry.getValue());
			assertEquals(entry.getKey(), mapped.identifier());
		}
	}
	
	@Test
	void unidentifiedMappedConstantsHaveNoIdentifier() {
		assertEquals(Optional.empty(), SqlTypes.byIdentifier("instant"));
		assertEquals(Optional.empty(), SqlTypes.byIdentifier("zoned_date_time"));
	}
	
	@Test
	void byIdentifierRoundTripsEveryIdentifiedConstant() {
		List<SqlType<?>> constants = List.of(
			SqlTypes.CHARACTER, SqlTypes.YEAR, SqlTypes.MONTH, SqlTypes.DAY_OF_WEEK, SqlTypes.DURATION,
			SqlTypes.UUID, SqlTypes.JSON, SqlTypes.XML, SqlTypes.IP_ADDRESS, SqlTypes.IP_NETWORK
		);
		for (SqlType<?> constant : constants) {
			String identifier = assertInstanceOf(MappedSqlType.class, constant).identifier();
			assertNotNull(identifier, "Constant without identifier: " + constant);
			assertSame(constant, SqlTypes.byIdentifier(identifier).orElseThrow(), "Identifier not registered: " + identifier);
		}
	}
	
	@Test
	void identifiedConstantsHaveUniqueIdentifiers() {
		List<SqlType<?>> constants = List.of(
			SqlTypes.CHARACTER, SqlTypes.YEAR, SqlTypes.MONTH, SqlTypes.DAY_OF_WEEK, SqlTypes.DURATION,
			SqlTypes.UUID, SqlTypes.JSON, SqlTypes.XML, SqlTypes.IP_ADDRESS, SqlTypes.IP_NETWORK
		);
		Set<String> identifiers = new HashSet<>();
		for (SqlType<?> constant : constants) {
			identifiers.add(assertInstanceOf(MappedSqlType.class, constant).identifier());
		}
		assertEquals(constants.size(), identifiers.size());
	}
	
	@Test
	void identifiedConstantsPreserveTheirBaseType() {
		assertEquals(SqlTypes.FIXED_STRING.configure(SqlParameter.length(36)), SqlTypes.UUID.baseType());
		assertEquals(SqlTypes.FIXED_STRING.configure(SqlParameter.length(1)), SqlTypes.CHARACTER.baseType());
		assertSame(SqlTypes.TEXT, SqlTypes.JSON.baseType());
		assertSame(SqlTypes.TEXT, SqlTypes.XML.baseType());
		assertEquals(SqlTypes.STRING.configure(SqlParameter.length(64)), SqlTypes.IP_ADDRESS.baseType());
		assertEquals(SqlTypes.STRING.configure(SqlParameter.length(64)), SqlTypes.IP_NETWORK.baseType());
		assertSame(SqlTypes.INTEGER, SqlTypes.YEAR.baseType());
		assertSame(SqlTypes.INTEGER, SqlTypes.MONTH.baseType());
		assertSame(SqlTypes.INTEGER, SqlTypes.DAY_OF_WEEK.baseType());
		assertSame(SqlTypes.LONG, SqlTypes.DURATION.baseType());
	}
	
	private enum TestEnum {
		A, B, C
	}
}
