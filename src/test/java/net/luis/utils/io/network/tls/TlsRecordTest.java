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

package net.luis.utils.io.network.tls;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link TlsRecord}.<br>
 *
 * @author Luis-St
 */
class TlsRecordTest {
	
	@Test
	void constructWithTypeAndData() {
		TlsRecord record = new TlsRecord(TlsContentType.HANDSHAKE, new byte[] { 1, 2, 3 });
		assertEquals(TlsContentType.HANDSHAKE, record.type());
		assertArrayEquals(new byte[] { 1, 2, 3 }, record.data());
		assertEquals(3, record.length());
	}
	
	@Test
	void constructWithEmptyData() {
		assertEquals(0, new TlsRecord(TlsContentType.APPLICATION_DATA, new byte[0]).length());
	}
	
	@Test
	void constructWithNullType() {
		assertThrows(NullPointerException.class, () -> new TlsRecord(null, new byte[0]));
	}
	
	@Test
	void constructWithNullData() {
		assertThrows(NullPointerException.class, () -> new TlsRecord(TlsContentType.HANDSHAKE, null));
	}
	
	@Test
	void headerWithNullType() {
		assertThrows(NullPointerException.class, () -> TlsRecord.header(null, 0));
	}
	
	@Test
	void constantsHaveExpectedValues() {
		assertEquals(5, TlsRecord.HEADER_LENGTH);
		assertEquals(16384, TlsRecord.MAX_PLAINTEXT_LENGTH);
	}
	
	@Test
	void constructDoesNotCopyData() {
		byte[] data = { 1, 2, 3 };
		TlsRecord record = new TlsRecord(TlsContentType.HANDSHAKE, data);
		assertSame(data, record.data());
		data[0] = 9;
		assertEquals(9, record.data()[0]);
	}
	
	@Test
	void headerForZeroLength() {
		assertArrayEquals(new byte[] { 21, 3, 3, 0, 0 }, TlsRecord.header(TlsContentType.ALERT, 0));
	}
	
	@Test
	void headerWithLengthAboveOneByte() {
		byte[] header = TlsRecord.header(TlsContentType.APPLICATION_DATA, 0x1234);
		assertArrayEquals(new byte[] { 23, 3, 3, 0x12, 0x34 }, header);
		assertEquals(TlsRecord.HEADER_LENGTH, header.length);
	}
	
	@Test
	void toStringShowsTypeAndLength() {
		assertEquals("HANDSHAKE[3]", new TlsRecord(TlsContentType.HANDSHAKE, new byte[3]).toString());
	}
	
	@Test
	void headerAlwaysWritesTls12VersionField() {
		for (TlsContentType type : TlsContentType.values()) {
			byte[] header = TlsRecord.header(type, 1);
			assertEquals((byte) type.code(), header[0]);
			assertEquals(3, header[1]);
			assertEquals(3, header[2]);
		}
	}
	
	@Test
	void headerWithMaximumUnsignedShortLength() {
		byte[] header = TlsRecord.header(TlsContentType.APPLICATION_DATA, 0xFFFF);
		assertEquals((byte) 0xFF, header[3]);
		assertEquals((byte) 0xFF, header[4]);
	}
	
	@Test
	void headerTruncatesLengthAboveShortRange() {
		byte[] header = TlsRecord.header(TlsContentType.APPLICATION_DATA, 0x10001);
		assertEquals(0, header[3]);
		assertEquals(1, header[4]);
	}
	
	@Test
	void equalsComparesDataByReference() {
		byte[] data = { 1, 2, 3 };
		TlsRecord first = new TlsRecord(TlsContentType.HANDSHAKE, data);
		TlsRecord copy = new TlsRecord(TlsContentType.HANDSHAKE, data.clone());
		TlsRecord shared = new TlsRecord(TlsContentType.HANDSHAKE, data);
		assertNotEquals(first, copy);
		assertEquals(first, shared);
		assertEquals(first.hashCode(), shared.hashCode());
	}
}
