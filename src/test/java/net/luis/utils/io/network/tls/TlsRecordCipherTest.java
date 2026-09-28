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
 * Test class for {@link TlsRecordCipher}.<br>
 *
 * @author Luis-St
 */
class TlsRecordCipherTest {
	
	@Test
	void nullEncryptWithNullType() {
		assertThrows(NullPointerException.class, () -> TlsRecordCipher.NULL.encrypt(0, null, new byte[0]));
	}
	
	@Test
	void nullEncryptWithNullFragment() {
		assertThrows(NullPointerException.class, () -> TlsRecordCipher.NULL.encrypt(0, TlsContentType.HANDSHAKE, null));
	}
	
	@Test
	void nullDecryptWithNullType() {
		assertThrows(NullPointerException.class, () -> TlsRecordCipher.NULL.decrypt(0, null, new byte[0]));
	}
	
	@Test
	void nullDecryptWithNullBody() {
		assertThrows(NullPointerException.class, () -> TlsRecordCipher.NULL.decrypt(0, TlsContentType.HANDSHAKE, null));
	}
	
	@Test
	void outerContentTypeWithNullType() {
		assertThrows(NullPointerException.class, () -> TlsRecordCipher.NULL.outerContentType(null));
	}
	
	@Test
	void nullEncryptReturnsFragmentUnchanged() {
		byte[] data = { 1, 2, 3 };
		byte[] result = TlsRecordCipher.NULL.encrypt(0, TlsContentType.APPLICATION_DATA, data);
		assertSame(data, result);
		assertArrayEquals(new byte[] { 1, 2, 3 }, result);
	}
	
	@Test
	void nullDecryptReturnsRecordWithBody() throws TlsAlertException {
		byte[] body = { 4, 5 };
		TlsRecord record = TlsRecordCipher.NULL.decrypt(0, TlsContentType.HANDSHAKE, body);
		assertEquals(TlsContentType.HANDSHAKE, record.type());
		assertSame(body, record.data());
	}
	
	@Test
	void nullVersionIsEmpty() {
		assertTrue(TlsRecordCipher.NULL.version().isEmpty());
	}
	
	@Test
	void nullMaxBodyLengthIsMaxPlaintextLength() {
		assertEquals(16384, TlsRecordCipher.NULL.maxBodyLength());
	}
	
	@Test
	void nullToString() {
		assertEquals("NULL", TlsRecordCipher.NULL.toString());
	}
	
	@Test
	void nullOuterContentTypeKeepsEveryType() {
		for (TlsContentType type : TlsContentType.values()) {
			assertEquals(type, TlsRecordCipher.NULL.outerContentType(type));
		}
	}
	
	@Test
	void nullIgnoresSequenceNumber() throws TlsAlertException {
		byte[] data = { 1, 2, 3 };
		for (long sequence : new long[] { 0, -1, Long.MAX_VALUE }) {
			assertSame(data, TlsRecordCipher.NULL.encrypt(sequence, TlsContentType.HANDSHAKE, data));
			TlsRecord record = TlsRecordCipher.NULL.decrypt(sequence, TlsContentType.HANDSHAKE, data);
			assertEquals(TlsContentType.HANDSHAKE, record.type());
			assertSame(data, record.data());
		}
	}
	
	@Test
	void nullEncryptDecryptRoundTrip() throws TlsAlertException {
		byte[] data = { 2, 40 };
		TlsRecord record = TlsRecordCipher.NULL.decrypt(5, TlsContentType.ALERT, TlsRecordCipher.NULL.encrypt(5, TlsContentType.ALERT, data));
		assertEquals(TlsContentType.ALERT, record.type());
		assertArrayEquals(data, record.data());
	}
}
