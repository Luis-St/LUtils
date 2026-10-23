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

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link TlsAlertException}.<br>
 *
 * @author Luis-St
 */
class TlsAlertExceptionTest {
	
	private static final TlsAlert FATAL_BAD_RECORD_MAC = TlsAlert.fatal(TlsAlertDescription.BAD_RECORD_MAC);
	
	@Test
	void constructWithMessage() {
		TlsAlertException exception = new TlsAlertException(FATAL_BAD_RECORD_MAC, true, "detail");
		assertEquals(FATAL_BAD_RECORD_MAC, exception.alert());
		assertTrue(exception.isLocal());
		assertEquals(FATAL_BAD_RECORD_MAC + ": detail", exception.getMessage());
		assertNull(exception.getCause());
	}
	
	@Test
	void constructRemoteWithMessage() {
		TlsAlertException exception = new TlsAlertException(FATAL_BAD_RECORD_MAC, false, "detail");
		assertFalse(exception.isLocal());
		assertEquals(FATAL_BAD_RECORD_MAC, exception.alert());
	}
	
	@Test
	void constructWithNullAlert() {
		assertThrows(NullPointerException.class, () -> new TlsAlertException(null, true, "detail"));
	}
	
	@Test
	void localWithNullDescription() {
		assertThrows(NullPointerException.class, () -> TlsAlertException.local(null, "detail"));
	}
	
	@Test
	void remoteWithNullAlert() {
		assertThrows(NullPointerException.class, () -> TlsAlertException.remote(null));
	}
	
	@Test
	void constructWithNullMessage() {
		TlsAlertException exception = new TlsAlertException(FATAL_BAD_RECORD_MAC, true, null);
		assertEquals(FATAL_BAD_RECORD_MAC.toString(), exception.getMessage());
		assertFalse(exception.getMessage().contains(":"));
	}
	
	@Test
	void constructWithNonNullMessage() {
		TlsAlertException exception = new TlsAlertException(FATAL_BAD_RECORD_MAC, true, "detail");
		assertTrue(exception.getMessage().endsWith(": detail"));
	}
	
	@Test
	void localWithNullMessage() {
		TlsAlertException exception = TlsAlertException.local(TlsAlertDescription.DECODE_ERROR, null);
		assertEquals(TlsAlert.fatal(TlsAlertDescription.DECODE_ERROR).toString(), exception.getMessage());
	}
	
	@Test
	void localCreatesFatalLocalAlert() {
		TlsAlertException exception = TlsAlertException.local(TlsAlertDescription.RECORD_OVERFLOW, "too long");
		assertEquals(TlsAlertLevel.FATAL, exception.alert().level());
		assertEquals(TlsAlertDescription.RECORD_OVERFLOW, exception.alert().description());
		assertTrue(exception.isLocal());
		assertTrue(exception.getMessage().endsWith("too long"));
	}
	
	@Test
	void remoteKeepsAlertAndIsNotLocal() {
		TlsAlert alert = TlsAlert.warning(TlsAlertDescription.USER_CANCELED);
		TlsAlertException exception = TlsAlertException.remote(alert);
		assertSame(alert, exception.alert());
		assertEquals(TlsAlertLevel.WARNING, exception.alert().level());
		assertFalse(exception.isLocal());
		assertTrue(exception.getMessage().endsWith("Alert received from peer"));
	}
	
	@Test
	void constructWithEmptyMessage() {
		TlsAlertException exception = new TlsAlertException(FATAL_BAD_RECORD_MAC, true, "");
		assertEquals(FATAL_BAD_RECORD_MAC + ": ", exception.getMessage());
	}
	
	@Test
	void catchAsTlsExceptionAndIOException() {
		TlsAlertException local = TlsAlertException.local(TlsAlertDescription.INTERNAL_ERROR, "detail");
		TlsAlertException remote = TlsAlertException.remote(FATAL_BAD_RECORD_MAC);
		assertInstanceOf(TlsException.class, local);
		assertInstanceOf(IOException.class, local);
		assertInstanceOf(TlsException.class, remote);
		assertInstanceOf(IOException.class, remote);
	}
	
	@Test
	void localForEveryDescription() {
		for (TlsAlertDescription description : TlsAlertDescription.values()) {
			TlsAlertException exception = TlsAlertException.local(description, null);
			assertEquals(TlsAlertLevel.FATAL, exception.alert().level());
			assertEquals(description, exception.alert().description());
			assertTrue(exception.isLocal());
		}
	}
}
