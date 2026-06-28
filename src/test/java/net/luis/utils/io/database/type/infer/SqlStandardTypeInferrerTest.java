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

package net.luis.utils.io.database.type.infer;

import net.luis.utils.io.data.json.JsonPrimitive;
import net.luis.utils.io.data.xml.XmlElement;
import net.luis.utils.io.database.exception.client.SqlTypeNotFoundException;
import net.luis.utils.io.database.type.SqlType;
import net.luis.utils.io.database.type.SqlTypes;
import net.luis.utils.io.database.type.parameter.SqlParameter;
import net.luis.utils.io.network.address.IpAddresses;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Clob;
import java.sql.NClob;
import java.time.*;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlStandardTypeInferrer}.<br>
 *
 * @author Luis-St
 */
class SqlStandardTypeInferrerTest {
	
	private static final SqlTypeInferrer INFERRER = SqlTypeInferrer.standard();
	
	@Test
	void inferTypeWithNullValue() throws Exception {
		assertThrows(NullPointerException.class, () -> INFERRER.inferType(null));
	}
	
	@Test
	void inferTypeWithUnsupportedTypeThrows() throws Exception {
		assertThrows(SqlTypeNotFoundException.class, () -> INFERRER.inferType(new Object()));
	}
	
	@Test
	void inferBooleanType() throws Exception {
		assertEquals(SqlTypes.BOOLEAN, INFERRER.inferType(true));
	}
	
	@Test
	void inferByteType() throws Exception {
		assertEquals(SqlTypes.BYTE, INFERRER.inferType((byte) 1));
	}
	
	@Test
	void inferShortType() throws Exception {
		assertEquals(SqlTypes.SHORT, INFERRER.inferType((short) 1));
	}
	
	@Test
	void inferIntegerType() throws Exception {
		assertEquals(SqlTypes.INTEGER, INFERRER.inferType(1));
	}
	
	@Test
	void inferLongType() throws Exception {
		assertEquals(SqlTypes.LONG, INFERRER.inferType(1L));
	}
	
	@Test
	void inferBigIntegerType() throws Exception {
		assertEquals(SqlTypes.BIG_INTEGER.configure(SqlParameter.precision(38, 0)), INFERRER.inferType(BigInteger.valueOf(5)));
	}
	
	@Test
	void inferFloatType() throws Exception {
		assertEquals(SqlTypes.REAL, INFERRER.inferType(1.0F));
	}
	
	@Test
	void inferDoubleType() throws Exception {
		assertEquals(SqlTypes.DOUBLE, INFERRER.inferType(1.0));
	}
	
	@Test
	void inferBigDecimalType() throws Exception {
		assertEquals(SqlTypes.DECIMAL.configure(SqlParameter.precision(38, 18)), INFERRER.inferType(BigDecimal.valueOf(5)));
	}
	
	@Test
	void inferCharacterType() throws Exception {
		assertEquals(SqlTypes.CHARACTER, INFERRER.inferType('c'));
	}
	
	@Test
	void inferStringType() throws Exception {
		assertEquals(SqlTypes.STRING.configure(SqlParameter.length(255)), INFERRER.inferType("text"));
	}
	
	@Test
	void inferNClobType() throws Exception {
		assertEquals(SqlTypes.NCLOB, INFERRER.inferType(new FakeNClob()));
	}
	
	@Test
	void inferClobType() throws Exception {
		assertEquals(SqlTypes.CLOB, INFERRER.inferType(new FakeClob()));
	}
	
	@Test
	void inferByteArrayType() throws Exception {
		assertEquals(SqlTypes.BYTES.configure(SqlParameter.length(255)), INFERRER.inferType(new byte[] { 1, 2, 3 }));
	}
	
	@Test
	void inferUuidType() throws Exception {
		assertEquals(SqlTypes.UUID, INFERRER.inferType(UUID.randomUUID()));
	}
	
	@Test
	void inferLocalDateType() throws Exception {
		assertEquals(SqlTypes.LOCAL_DATE, INFERRER.inferType(LocalDate.now()));
	}
	
	@Test
	void inferLocalTimeType() throws Exception {
		assertEquals(SqlTypes.LOCAL_TIME.configure(SqlParameter.fractional(6)), INFERRER.inferType(LocalTime.now()));
	}
	
	@Test
	void inferLocalDateTimeType() throws Exception {
		assertEquals(SqlTypes.LOCAL_DATE_TIME.configure(SqlParameter.fractional(6)), INFERRER.inferType(LocalDateTime.now()));
	}
	
	@Test
	void inferOffsetTimeType() throws Exception {
		assertEquals(SqlTypes.OFFSET_TIME.configure(SqlParameter.fractional(6)), INFERRER.inferType(OffsetTime.now()));
	}
	
	@Test
	void inferOffsetDateTimeType() throws Exception {
		assertEquals(SqlTypes.OFFSET_DATE_TIME.configure(SqlParameter.fractional(6)), INFERRER.inferType(OffsetDateTime.now()));
	}
	
	@Test
	void inferZonedDateTimeType() throws Exception {
		assertEquals(SqlTypes.ZONED_DATE_TIME.configure(SqlParameter.fractional(6)), INFERRER.inferType(ZonedDateTime.now()));
	}
	
	@Test
	void inferInstantType() throws Exception {
		assertEquals(SqlTypes.INSTANT.configure(SqlParameter.fractional(6)), INFERRER.inferType(Instant.now()));
	}
	
	@Test
	void inferYearType() throws Exception {
		assertEquals(SqlTypes.YEAR, INFERRER.inferType(Year.now()));
	}
	
	@Test
	void inferMonthType() throws Exception {
		assertEquals(SqlTypes.MONTH, INFERRER.inferType(Month.JANUARY));
	}
	
	@Test
	void inferDayOfWeekType() throws Exception {
		assertEquals(SqlTypes.DAY_OF_WEEK, INFERRER.inferType(DayOfWeek.MONDAY));
	}
	
	@Test
	void inferDurationType() throws Exception {
		assertEquals(SqlTypes.DURATION, INFERRER.inferType(Duration.ofSeconds(1)));
	}
	
	@Test
	void inferJsonElementType() throws Exception {
		assertEquals(SqlTypes.JSON, INFERRER.inferType(new JsonPrimitive("value")));
	}
	
	@Test
	void inferXmlElementType() throws Exception {
		assertEquals(SqlTypes.XML, INFERRER.inferType(new XmlElement("root")));
	}
	
	@Test
	void inferIpAddressType() throws Exception {
		assertEquals(SqlTypes.IP_ADDRESS, INFERRER.inferType(IpAddresses.parse("127.0.0.1")));
	}
	
	@Test
	void inferIpNetworkType() throws Exception {
		assertEquals(SqlTypes.IP_NETWORK, INFERRER.inferType(IpAddresses.parseNetwork("127.0.0.0/8")));
	}
	
	@Test
	void inferEnumType() throws Exception {
		SqlType<TestEnum> type = INFERRER.inferType(TestEnum.ALPHA);
		assertEquals(SqlTypes.enumName(TestEnum.class), type);
		assertEquals(TestEnum.class, type.javaType());
	}
	
	@Test
	void inferTypeGenericMethodReturnsTypedResult() throws Exception {
		SqlType<Integer> type = INFERRER.inferType(5);
		assertEquals(Integer.class, type.javaType());
	}
	
	private enum TestEnum {
		ALPHA, BETA
	}
	
	private static class FakeClob implements Clob {
		
		@Override
		public long length() {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public String getSubString(long pos, int length) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public Reader getCharacterStream() {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public InputStream getAsciiStream() {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public long position(String searchstr, long start) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public long position(Clob searchstr, long start) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public int setString(long pos, String str) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public int setString(long pos, String str, int offset, int len) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public OutputStream setAsciiStream(long pos) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public Writer setCharacterStream(long pos) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public void truncate(long len) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public void free() {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public Reader getCharacterStream(long pos, long length) {
			throw new UnsupportedOperationException();
		}
	}
	
	private static class FakeNClob extends FakeClob implements NClob {}
}
