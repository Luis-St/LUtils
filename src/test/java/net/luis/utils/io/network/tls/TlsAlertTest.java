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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link TlsAlert}.<br>
 *
 * @author Luis-St
 */
class TlsAlertTest {
	
	@Test
	void constructWithLevelAndDescription() {
		TlsAlert alert = new TlsAlert(TlsAlertLevel.WARNING, TlsAlertDescription.USER_CANCELED);
		assertEquals(TlsAlertLevel.WARNING, alert.level());
		assertEquals(TlsAlertDescription.USER_CANCELED, alert.description());
	}
	
	@Test
	void closeNotifyConstant() {
		assertEquals(TlsAlertLevel.WARNING, TlsAlert.CLOSE_NOTIFY.level());
		assertEquals(TlsAlertDescription.CLOSE_NOTIFY, TlsAlert.CLOSE_NOTIFY.description());
	}
	
	@Test
	void constructWithNullLevel() {
		assertThrows(NullPointerException.class, () -> new TlsAlert(null, TlsAlertDescription.CLOSE_NOTIFY));
	}
	
	@Test
	void constructWithNullDescription() {
		assertThrows(NullPointerException.class, () -> new TlsAlert(TlsAlertLevel.FATAL, null));
	}
	
	@Test
	void fatalWithNullDescription() {
		assertThrows(NullPointerException.class, () -> TlsAlert.fatal(null));
	}
	
	@Test
	void warningWithNullDescription() {
		assertThrows(NullPointerException.class, () -> TlsAlert.warning(null));
	}
	
	@Test
	void decodeWithNullBody() {
		assertThrows(NullPointerException.class, () -> TlsAlert.decode(null));
	}
	
	@Test
	void decodeWithTooShortBody() {
		assertTrue(TlsAlert.decode(new byte[0]).isEmpty());
		assertTrue(TlsAlert.decode(new byte[] { 2 }).isEmpty());
	}
	
	@Test
	void decodeWithTooLongBody() {
		assertTrue(TlsAlert.decode(new byte[] { 2, 20, 0 }).isEmpty());
	}
	
	@Test
	void decodeWithUnknownLevel() {
		assertTrue(TlsAlert.decode(new byte[] { 3, 20 }).isEmpty());
	}
	
	@Test
	void decodeWithUnknownDescription() {
		assertTrue(TlsAlert.decode(new byte[] { 2, 21 }).isEmpty());
	}
	
	@Test
	void decodeWithValidBody() {
		assertEquals(Optional.of(new TlsAlert(TlsAlertLevel.FATAL, TlsAlertDescription.BAD_RECORD_MAC)), TlsAlert.decode(new byte[] { 2, 20 }));
	}
	
	@Test
	void isTerminalForFatalAlert() {
		assertTrue(TlsAlert.fatal(TlsAlertDescription.HANDSHAKE_FAILURE).isTerminal());
	}
	
	@Test
	void isTerminalForWarningCloseNotify() {
		assertTrue(TlsAlert.CLOSE_NOTIFY.isTerminal());
	}
	
	@Test
	void isTerminalForWarningOtherDescription() {
		assertFalse(TlsAlert.warning(TlsAlertDescription.USER_CANCELED).isTerminal());
	}
	
	@Test
	void fatalCreatesFatalAlert() {
		assertEquals(new TlsAlert(TlsAlertLevel.FATAL, TlsAlertDescription.DECODE_ERROR), TlsAlert.fatal(TlsAlertDescription.DECODE_ERROR));
	}
	
	@Test
	void warningCreatesWarningAlert() {
		assertEquals(new TlsAlert(TlsAlertLevel.WARNING, TlsAlertDescription.CERTIFICATE_EXPIRED), TlsAlert.warning(TlsAlertDescription.CERTIFICATE_EXPIRED));
	}
	
	@Test
	void encodeWritesLevelAndDescriptionCode() {
		assertArrayEquals(new byte[] { 2, 20 }, TlsAlert.fatal(TlsAlertDescription.BAD_RECORD_MAC).encode());
		assertArrayEquals(new byte[] { 1, 0 }, TlsAlert.CLOSE_NOTIFY.encode());
	}
	
	@Test
	void isTerminalForFatalCloseNotify() {
		assertTrue(TlsAlert.fatal(TlsAlertDescription.CLOSE_NOTIFY).isTerminal());
	}
	
	@Test
	void encodeDecodeRoundTripForAllCombinations() {
		int combinations = 0;
		for (TlsAlertLevel level : TlsAlertLevel.values()) {
			for (TlsAlertDescription description : TlsAlertDescription.values()) {
				TlsAlert alert = new TlsAlert(level, description);
				assertEquals(Optional.of(alert), TlsAlert.decode(alert.encode()));
				combinations++;
			}
		}
		assertEquals(54, combinations);
	}
	
	@Test
	void decodeMasksHighByteValues() {
		assertTrue(TlsAlert.decode(new byte[] { (byte) 0x82, 20 }).isEmpty());
		assertTrue(TlsAlert.decode(new byte[] { 2, (byte) 0x94 }).isEmpty());
	}
	
	@Test
	void decodeDoesNotDependOnArrayIdentity() {
		byte[] body = { 2, 20 };
		TlsAlert alert = TlsAlert.decode(body).orElseThrow();
		body[0] = 1;
		body[1] = 0;
		assertEquals(TlsAlert.fatal(TlsAlertDescription.BAD_RECORD_MAC), alert);
	}
}
