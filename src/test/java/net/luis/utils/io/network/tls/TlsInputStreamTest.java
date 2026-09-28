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

import net.luis.utils.crypto.algorithm.AeadAlgorithm;
import net.luis.utils.crypto.util.CryptoBytes;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link TlsInputStream}.<br>
 *
 * @author Luis-St
 */
class TlsInputStreamTest {
	
	private static final byte[] DATA = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
	
	private static @NonNull TlsInputStream input(byte @NonNull [] @NonNull ... parts) {
		return new TlsInputStream(new ByteArrayInputStream(CryptoBytes.concat(parts)));
	}
	
	private static byte @NonNull [] record(@NonNull TlsContentType type, byte @NonNull ... body) {
		return CryptoBytes.concat(TlsRecord.header(type, body.length), body);
	}
	
	private static byte @NonNull [] alertRecord(@NonNull TlsAlertLevel level, @NonNull TlsAlertDescription description) {
		return record(TlsContentType.ALERT, new TlsAlert(level, description).encode());
	}
	
	private static byte @NonNull [] closeNotify() {
		return record(TlsContentType.ALERT, TlsAlert.CLOSE_NOTIFY.encode());
	}
	
	private static byte @NonNull [] protectedRecord(@NonNull TlsRecordCipher cipher, long sequence, @NonNull TlsContentType type, byte @NonNull ... data) {
		byte[] body = cipher.encrypt(sequence, type, data);
		return CryptoBytes.concat(TlsRecord.header(cipher.outerContentType(type), body.length), body);
	}
	
	private static @NonNull TlsRecordCipher cipher(@NonNull TlsVersion version, @NonNull AeadAlgorithm algorithm) {
		return new TlsAeadRecordCipher(version, algorithm, new byte[algorithm.keyLength()], new byte[TlsAeadRecordCipher.ivLength(version, algorithm)]);
	}
	
	private static byte @NonNull [] filled(int length) {
		byte[] data = new byte[length];
		Arrays.fill(data, (byte) 1);
		return data;
	}
	
	@Test
	void constructWithInputStream() throws TlsException {
		TestInputStream transport = new TestInputStream(record(TlsContentType.HANDSHAKE, DATA));
		TlsInputStream in = new TlsInputStream(transport);
		assertEquals(0, in.available());
		assertFalse(in.isCloseNotifyReceived());
		assertEquals(5 + DATA.length, transport.remaining());
	}
	
	@Test
	void constructWithNullInputStream() {
		assertThrows(NullPointerException.class, () -> new TlsInputStream(null));
	}
	
	@Test
	void changeCipherSpecWithNullCipher() {
		TlsInputStream in = input();
		assertThrows(NullPointerException.class, () -> in.changeCipherSpec(null));
	}
	
	@Test
	void setRecordHandlerWithNullHandler() {
		TlsInputStream in = input();
		assertThrows(NullPointerException.class, () -> in.setRecordHandler(null));
	}
	
	@Test
	void readWithNullTarget() {
		TlsInputStream in = input(record(TlsContentType.APPLICATION_DATA, DATA));
		assertThrows(NullPointerException.class, () -> in.read(null, 0, 1));
	}
	
	@Test
	void readWithInvalidBounds() throws TlsException {
		TlsInputStream in = input(record(TlsContentType.APPLICATION_DATA, (byte) 1));
		assertThrows(IndexOutOfBoundsException.class, () -> in.read(new byte[4], -1, 1));
		assertThrows(IndexOutOfBoundsException.class, () -> in.read(new byte[4], 0, 5));
		assertThrows(IndexOutOfBoundsException.class, () -> in.read(new byte[4], 3, 2));
		assertEquals(1, in.read());
	}
	
	@Test
	void readRecordFromFailingTransport() {
		TlsInputStream in = new TlsInputStream(new TestInputStream(true, false, record(TlsContentType.HANDSHAKE, DATA)));
		TlsException exception = assertThrows(TlsException.class, in::readRecord);
		assertEquals("Failed to read from the transport", exception.getMessage());
		assertEquals("read failed", exception.getCause().getMessage());
	}
	
	@Test
	void closeFailingTransport() {
		TlsInputStream in = new TlsInputStream(new TestInputStream(false, true));
		TlsException exception = assertThrows(TlsException.class, in::close);
		assertEquals("Failed to close the transport", exception.getMessage());
		assertEquals("close failed", exception.getCause().getMessage());
	}
	
	@Test
	void readRecordAfterClose() throws TlsException {
		TlsInputStream in = input(record(TlsContentType.HANDSHAKE, DATA));
		in.close();
		TlsException exception = assertThrows(TlsException.class, in::readRecord);
		assertEquals("Stream is closed", exception.getMessage());
	}
	
	@Test
	void readAfterClose() throws TlsException {
		TlsInputStream in = input(record(TlsContentType.APPLICATION_DATA, DATA));
		in.close();
		assertThrows(TlsException.class, in::read);
		assertThrows(TlsException.class, () -> in.read(new byte[1], 0, 1));
	}
	
	@Test
	void availableAfterClose() throws TlsException {
		TlsInputStream in = input();
		in.close();
		assertThrows(TlsException.class, in::available);
	}
	
	@Test
	void readRecordAfterEarlierFailure() {
		TlsInputStream in = input(new byte[] { 99, 3, 3, 0, 0 });
		TlsAlertException first = assertThrows(TlsAlertException.class, in::readRecord);
		TlsException second = assertThrows(TlsException.class, in::readRecord);
		assertFalse(second instanceof TlsAlertException);
		assertSame(first, second.getCause());
	}
	
	@Test
	void readRecordReturnsHandshakeRecord() throws TlsException {
		TlsRecord record = input(record(TlsContentType.HANDSHAKE, DATA)).readRecord().orElseThrow();
		assertEquals(TlsContentType.HANDSHAKE, record.type());
		assertArrayEquals(DATA, record.data());
	}
	
	@Test
	void transportEndsWithoutCloseNotifyRequired() {
		TlsInputStream in = input();
		TlsException exception = assertThrows(TlsException.class, in::readRecord);
		assertFalse(exception instanceof TlsAlertException);
		assertEquals("Transport ended without a close_notify alert", exception.getMessage());
	}
	
	@Test
	void transportEndsWithoutCloseNotifyNotRequired() throws TlsException {
		TlsInputStream in = input();
		in.setRequireCloseNotify(false);
		assertTrue(in.readRecord().isEmpty());
		assertEquals(-1, in.read());
		assertFalse(in.isCloseNotifyReceived());
	}
	
	@Test
	void transportEndsInsideHeader() {
		TlsInputStream in = input(new byte[] { 22, 3, 3 });
		TlsException exception = assertThrows(TlsException.class, in::readRecord);
		assertEquals("Transport ended inside a record header", exception.getMessage());
	}
	
	@Test
	void recordWithUnknownContentType() {
		TlsInputStream in = input(new byte[] { 99, 3, 3, 0, 0 });
		TlsAlertException exception = assertThrows(TlsAlertException.class, in::readRecord);
		assertEquals(TlsAlertDescription.UNEXPECTED_MESSAGE, exception.alert().description());
		assertTrue(exception.isLocal());
	}
	
	@Test
	void recordLongerThanNullCipherLimit() {
		TestInputStream transport = new TestInputStream(TlsRecord.header(TlsContentType.HANDSHAKE, 16385), new byte[16385]);
		TlsInputStream in = new TlsInputStream(transport);
		TlsAlertException exception = assertThrows(TlsAlertException.class, in::readRecord);
		assertEquals(TlsAlertDescription.RECORD_OVERFLOW, exception.alert().description());
		assertEquals(16385, transport.remaining());
	}
	
	@Test
	void recordAtNullCipherLimit() throws TlsException {
		TlsRecord record = input(record(TlsContentType.HANDSHAKE, new byte[16384])).readRecord().orElseThrow();
		assertEquals(16384, record.length());
	}
	
	@Test
	void recordLongerThanTls13CipherLimit() {
		TlsInputStream in = input(TlsRecord.header(TlsContentType.APPLICATION_DATA, 16641), new byte[16641]);
		in.changeCipherSpec(cipher(TlsVersion.TLS_1_3, AeadAlgorithm.AES_128_GCM));
		TlsAlertException exception = assertThrows(TlsAlertException.class, in::readRecord);
		assertEquals(TlsAlertDescription.RECORD_OVERFLOW, exception.alert().description());
	}
	
	@Test
	void transportEndsInsideBody() {
		TlsInputStream in = input(TlsRecord.header(TlsContentType.HANDSHAKE, 10), new byte[4]);
		TlsException exception = assertThrows(TlsException.class, in::readRecord);
		assertEquals("Transport ended inside a record body", exception.getMessage());
	}
	
	@Test
	void changeCipherSpecUnderTls13IsSkipped() throws TlsException {
		TlsRecordCipher cipher = cipher(TlsVersion.TLS_1_3, AeadAlgorithm.AES_128_GCM);
		TlsInputStream in = input(record(TlsContentType.CHANGE_CIPHER_SPEC, (byte) 1), protectedRecord(cipher, 0, TlsContentType.HANDSHAKE, DATA));
		in.changeCipherSpec(cipher);
		
		TlsRecord record = in.readRecord().orElseThrow();
		assertEquals(TlsContentType.HANDSHAKE, record.type());
		assertArrayEquals(DATA, record.data());
	}
	
	@Test
	void changeCipherSpecUnderTls13WithWrongLength() {
		TlsInputStream in = input(record(TlsContentType.CHANGE_CIPHER_SPEC, (byte) 1, (byte) 1));
		in.changeCipherSpec(cipher(TlsVersion.TLS_1_3, AeadAlgorithm.AES_128_GCM));
		TlsAlertException exception = assertThrows(TlsAlertException.class, in::readRecord);
		assertEquals(TlsAlertDescription.UNEXPECTED_MESSAGE, exception.alert().description());
	}
	
	@Test
	void changeCipherSpecUnderTls13WithWrongValue() {
		TlsInputStream in = input(record(TlsContentType.CHANGE_CIPHER_SPEC, (byte) 2));
		in.changeCipherSpec(cipher(TlsVersion.TLS_1_3, AeadAlgorithm.AES_128_GCM));
		TlsAlertException exception = assertThrows(TlsAlertException.class, in::readRecord);
		assertEquals(TlsAlertDescription.UNEXPECTED_MESSAGE, exception.alert().description());
	}
	
	@Test
	void changeCipherSpecWithoutTls13IsReturned() throws TlsException {
		TlsRecord record = input(record(TlsContentType.CHANGE_CIPHER_SPEC, (byte) 1)).readRecord().orElseThrow();
		assertEquals(TlsContentType.CHANGE_CIPHER_SPEC, record.type());
		assertArrayEquals(new byte[] { 1 }, record.data());
	}
	
	@Test
	void changeCipherSpecUnderTls12IsDecrypted() {
		TlsInputStream in = input(record(TlsContentType.CHANGE_CIPHER_SPEC, (byte) 1));
		in.changeCipherSpec(cipher(TlsVersion.TLS_1_2, AeadAlgorithm.CHACHA20_POLY1305));
		TlsAlertException exception = assertThrows(TlsAlertException.class, in::readRecord);
		assertEquals(TlsAlertDescription.BAD_RECORD_MAC, exception.alert().description());
	}
	
	@Test
	void recordFailingToAuthenticate() {
		TlsRecordCipher cipher = cipher(TlsVersion.TLS_1_3, AeadAlgorithm.AES_128_GCM);
		byte[] tampered = protectedRecord(cipher, 0, TlsContentType.HANDSHAKE, DATA);
		tampered[tampered.length - 1] ^= 1;
		TlsInputStream in = input(tampered);
		in.changeCipherSpec(cipher);
		
		TlsAlertException first = assertThrows(TlsAlertException.class, in::readRecord);
		assertEquals(TlsAlertDescription.BAD_RECORD_MAC, first.alert().description());
		TlsException second = assertThrows(TlsException.class, in::readRecord);
		assertSame(first, second.getCause());
	}
	
	@Test
	void emptyHandshakeRecord() {
		TlsInputStream in = input(record(TlsContentType.HANDSHAKE));
		TlsAlertException exception = assertThrows(TlsAlertException.class, in::readRecord);
		assertEquals(TlsAlertDescription.UNEXPECTED_MESSAGE, exception.alert().description());
	}
	
	@Test
	void emptyApplicationDataRecordIsReturned() throws TlsException {
		TlsRecord record = input(record(TlsContentType.APPLICATION_DATA)).readRecord().orElseThrow();
		assertEquals(TlsContentType.APPLICATION_DATA, record.type());
		assertEquals(0, record.length());
	}
	
	@Test
	void malformedAlertRecord() {
		for (byte[] body : new byte[][] { { 2 }, { 9, 9 } }) {
			TlsInputStream in = input(record(TlsContentType.ALERT, body));
			TlsAlertException exception = assertThrows(TlsAlertException.class, in::readRecord);
			assertEquals(TlsAlertDescription.DECODE_ERROR, exception.alert().description());
			assertTrue(exception.isLocal());
		}
	}
	
	@Test
	void closeNotifyEndsStream() throws TlsException {
		TlsInputStream in = input(closeNotify());
		assertTrue(in.readRecord().isEmpty());
		assertTrue(in.isCloseNotifyReceived());
	}
	
	@Test
	void readRecordAfterCloseNotify() throws TlsException {
		byte[] handshake = record(TlsContentType.HANDSHAKE, DATA);
		TestInputStream transport = new TestInputStream(closeNotify(), handshake);
		TlsInputStream in = new TlsInputStream(transport);
		assertTrue(in.readRecord().isEmpty());
		assertTrue(in.readRecord().isEmpty());
		assertEquals(handshake.length, transport.remaining());
	}
	
	@Test
	void fatalAlertUnderNullCipher() {
		TlsAlert alert = TlsAlert.fatal(TlsAlertDescription.HANDSHAKE_FAILURE);
		TlsInputStream in = input(record(TlsContentType.ALERT, alert.encode()));
		TlsAlertException exception = assertThrows(TlsAlertException.class, in::readRecord);
		assertFalse(exception.isLocal());
		assertEquals(alert, exception.alert());
	}
	
	@Test
	void warningAlertUnderNullCipherIsIgnored() throws TlsException {
		TlsInputStream in = input(alertRecord(TlsAlertLevel.WARNING, TlsAlertDescription.USER_CANCELED), record(TlsContentType.HANDSHAKE, DATA));
		TlsRecord record = in.readRecord().orElseThrow();
		assertEquals(TlsContentType.HANDSHAKE, record.type());
		assertArrayEquals(DATA, record.data());
	}
	
	@Test
	void warningAlertUnderTls12CipherIsIgnored() throws TlsException {
		TlsRecordCipher cipher = cipher(TlsVersion.TLS_1_2, AeadAlgorithm.CHACHA20_POLY1305);
		byte[] warning = protectedRecord(cipher, 0, TlsContentType.ALERT, TlsAlert.warning(TlsAlertDescription.CERTIFICATE_EXPIRED).encode());
		TlsInputStream in = input(warning, protectedRecord(cipher, 1, TlsContentType.HANDSHAKE, DATA));
		in.changeCipherSpec(cipher);
		
		TlsRecord record = in.readRecord().orElseThrow();
		assertEquals(TlsContentType.HANDSHAKE, record.type());
	}
	
	@Test
	void warningAlertUnderTls13IsFatal() {
		TlsRecordCipher cipher = cipher(TlsVersion.TLS_1_3, AeadAlgorithm.AES_128_GCM);
		TlsAlert alert = TlsAlert.warning(TlsAlertDescription.CERTIFICATE_EXPIRED);
		TlsInputStream in = input(protectedRecord(cipher, 0, TlsContentType.ALERT, alert.encode()));
		in.changeCipherSpec(cipher);
		
		TlsAlertException first = assertThrows(TlsAlertException.class, in::readRecord);
		assertFalse(first.isLocal());
		assertEquals(alert, first.alert());
		assertSame(first, assertThrows(TlsException.class, in::readRecord).getCause());
	}
	
	@Test
	void userCanceledWarningUnderTls13IsIgnored() throws TlsException {
		TlsRecordCipher cipher = cipher(TlsVersion.TLS_1_3, AeadAlgorithm.AES_128_GCM);
		byte[] warning = protectedRecord(cipher, 0, TlsContentType.ALERT, TlsAlert.warning(TlsAlertDescription.USER_CANCELED).encode());
		TlsInputStream in = input(warning, protectedRecord(cipher, 1, TlsContentType.HANDSHAKE, DATA));
		in.changeCipherSpec(cipher);
		
		TlsRecord record = in.readRecord().orElseThrow();
		assertEquals(TlsContentType.HANDSHAKE, record.type());
		assertArrayEquals(DATA, record.data());
	}
	
	@Test
	void readReturnsMinusOneAtEnd() throws TlsException {
		assertEquals(-1, input(closeNotify()).read());
	}
	
	@Test
	void readReturnsUnsignedByte() throws TlsException {
		TlsInputStream in = input(record(TlsContentType.APPLICATION_DATA, (byte) 0xFF, (byte) 1));
		assertEquals(255, in.read());
		assertEquals(1, in.read());
	}
	
	@Test
	void readArrayWithZeroLength() throws TlsException {
		TlsInputStream in = input();
		assertEquals(0, in.read(new byte[1], 0, 0));
	}
	
	@Test
	void readArrayReturnsMinusOneAtEnd() throws TlsException {
		assertEquals(-1, input(closeNotify()).read(new byte[4], 0, 4));
	}
	
	@Test
	void readArrayLimitedByLength() throws TlsException {
		TlsInputStream in = input(record(TlsContentType.APPLICATION_DATA, DATA));
		byte[] target = new byte[8];
		assertEquals(4, in.read(target, 2, 4));
		assertArrayEquals(new byte[] { 1, 2, 3, 4 }, Arrays.copyOfRange(target, 2, 6));
		assertEquals(6, in.available());
	}
	
	@Test
	void readArrayLimitedByBuffer() throws TlsException {
		TlsInputStream in = input(record(TlsContentType.APPLICATION_DATA, (byte) 1, (byte) 2, (byte) 3), record(TlsContentType.APPLICATION_DATA, (byte) 4, (byte) 5, (byte) 6));
		byte[] target = new byte[10];
		assertEquals(3, in.read(target, 0, 10));
		assertArrayEquals(new byte[] { 1, 2, 3 }, Arrays.copyOf(target, 3));
		assertEquals(3, in.read(target, 0, 10));
		assertArrayEquals(new byte[] { 4, 5, 6 }, Arrays.copyOf(target, 3));
	}
	
	@Test
	void applicationDataSkipsEmptyRecords() throws TlsException {
		TlsInputStream in = input(record(TlsContentType.APPLICATION_DATA), record(TlsContentType.APPLICATION_DATA, (byte) 7));
		assertEquals(7, in.read());
	}
	
	@Test
	void nonApplicationDataWithDefaultHandler() {
		TlsInputStream in = input(record(TlsContentType.HANDSHAKE, DATA));
		TlsAlertException exception = assertThrows(TlsAlertException.class, in::read);
		assertEquals(TlsAlertDescription.UNEXPECTED_MESSAGE, exception.alert().description());
	}
	
	@Test
	void nonApplicationDataWithCustomHandler() throws TlsException {
		List<TlsRecord> handled = new ArrayList<>();
		TlsInputStream in = input(record(TlsContentType.HANDSHAKE, DATA), record(TlsContentType.APPLICATION_DATA, (byte) 7));
		in.setRecordHandler(handled::add);
		
		assertEquals(7, in.read());
		assertEquals(1, handled.size());
		assertEquals(TlsContentType.HANDSHAKE, handled.getFirst().type());
	}
	
	@Test
	void availableReturnsRemainingBytes() throws TlsException {
		TlsInputStream in = input(record(TlsContentType.APPLICATION_DATA, (byte) 1, (byte) 2, (byte) 3, (byte) 4, (byte) 5));
		assertEquals(0, in.available());
		in.read();
		assertEquals(4, in.available());
		in.read(new byte[4], 0, 4);
		assertEquals(0, in.available());
	}
	
	@Test
	void closeClosesTransportOnce() throws TlsException {
		TestInputStream transport = new TestInputStream();
		TlsInputStream in = new TlsInputStream(transport);
		in.close();
		in.close();
		assertEquals(1, transport.closes);
	}
	
	@Test
	void closeDiscardsBufferedData() throws TlsException {
		TlsInputStream in = input(record(TlsContentType.APPLICATION_DATA, (byte) 1, (byte) 2, (byte) 3, (byte) 4, (byte) 5));
		in.read();
		in.close();
		assertThrows(TlsException.class, in::available);
	}
	
	@Test
	void readAllBytesAcrossRecords() throws IOException {
		TlsInputStream in = input(record(TlsContentType.APPLICATION_DATA, (byte) 1), record(TlsContentType.APPLICATION_DATA, (byte) 2, (byte) 3), record(TlsContentType.APPLICATION_DATA, (byte) 4), closeNotify());
		assertArrayEquals(new byte[] { 1, 2, 3, 4 }, in.readAllBytes());
	}
	
	@Test
	void readNBytesAcrossRecords() throws IOException {
		TlsInputStream in = input(record(TlsContentType.APPLICATION_DATA, (byte) 1, (byte) 2), record(TlsContentType.APPLICATION_DATA, (byte) 3, (byte) 4));
		assertArrayEquals(new byte[] { 1, 2, 3 }, in.readNBytes(3));
		assertEquals(1, in.available());
	}
	
	@Test
	void readRecordReturnsRecordsInOrder() throws TlsException {
		TlsInputStream in = input(record(TlsContentType.HANDSHAKE, (byte) 1), record(TlsContentType.APPLICATION_DATA, (byte) 2), record(TlsContentType.CHANGE_CIPHER_SPEC, (byte) 1));
		TlsRecord first = in.readRecord().orElseThrow();
		TlsRecord second = in.readRecord().orElseThrow();
		TlsRecord third = in.readRecord().orElseThrow();
		assertEquals(TlsContentType.HANDSHAKE, first.type());
		assertArrayEquals(new byte[] { 1 }, first.data());
		assertEquals(TlsContentType.APPLICATION_DATA, second.type());
		assertArrayEquals(new byte[] { 2 }, second.data());
		assertEquals(TlsContentType.CHANGE_CIPHER_SPEC, third.type());
	}
	
	@Test
	void isCloseNotifyReceivedFalseInitially() {
		assertFalse(input().isCloseNotifyReceived());
	}
	
	@Test
	void closeWithoutReading() {
		TestInputStream transport = new TestInputStream();
		TlsInputStream in = new TlsInputStream(transport);
		assertDoesNotThrow(in::close);
		assertEquals(1, transport.closes);
	}
	
	@Test
	void readProtectedStreamWrittenByTlsOutputStream() throws IOException {
		byte[] large = new byte[16385];
		Arrays.fill(large, (byte) 3);
		for (TlsVersion version : TlsVersion.values()) {
			for (AeadAlgorithm algorithm : new AeadAlgorithm[] { AeadAlgorithm.AES_128_GCM, AeadAlgorithm.CHACHA20_POLY1305 }) {
				ByteArrayOutputStream wire = new ByteArrayOutputStream();
				TlsOutputStream out = new TlsOutputStream(wire);
				out.changeCipherSpec(cipher(version, algorithm));
				out.writeRecord(TlsContentType.HANDSHAKE, DATA);
				out.write(large);
				out.close();
				
				TlsInputStream in = new TlsInputStream(new ByteArrayInputStream(wire.toByteArray()));
				in.changeCipherSpec(cipher(version, algorithm));
				assertEquals(TlsContentType.HANDSHAKE, in.readRecord().orElseThrow().type());
				assertArrayEquals(large, in.readAllBytes());
				assertTrue(in.isCloseNotifyReceived());
			}
		}
	}
	
	@Test
	void changeCipherSpecResetsSequence() throws TlsException {
		TlsRecordCipher first = cipher(TlsVersion.TLS_1_3, AeadAlgorithm.AES_128_GCM);
		TlsRecordCipher second = new TlsAeadRecordCipher(TlsVersion.TLS_1_3, AeadAlgorithm.AES_128_GCM, filled(16), new byte[12]);
		TlsInputStream in = input(protectedRecord(first, 0, TlsContentType.APPLICATION_DATA, (byte) 1), protectedRecord(first, 1, TlsContentType.APPLICATION_DATA, (byte) 2), protectedRecord(second, 0, TlsContentType.APPLICATION_DATA, (byte) 3));
		in.changeCipherSpec(first);
		
		assertEquals(1, in.read());
		assertEquals(2, in.read());
		in.changeCipherSpec(second);
		assertEquals(3, in.read());
	}
	
	@Test
	void recordHandlerInstallsNewCipher() throws TlsException {
		TlsRecordCipher first = cipher(TlsVersion.TLS_1_3, AeadAlgorithm.AES_128_GCM);
		TlsRecordCipher second = new TlsAeadRecordCipher(TlsVersion.TLS_1_3, AeadAlgorithm.AES_128_GCM, filled(16), new byte[12]);
		TlsInputStream in = input(protectedRecord(first, 0, TlsContentType.HANDSHAKE, DATA), protectedRecord(second, 0, TlsContentType.APPLICATION_DATA, (byte) 42));
		in.changeCipherSpec(first);
		in.setRecordHandler(record -> in.changeCipherSpec(second));
		
		assertEquals(42, in.read());
	}
	
	@Test
	void handlerFailureDoesNotPoisonStream() throws TlsException {
		AtomicInteger calls = new AtomicInteger(0);
		TlsInputStream in = input(record(TlsContentType.HANDSHAKE, DATA), record(TlsContentType.APPLICATION_DATA, (byte) 7));
		in.setRecordHandler(record -> {
			if (calls.getAndIncrement() == 0) {
				throw new TlsException("handler failed");
			}
		});
		
		TlsException exception = assertThrows(TlsException.class, in::read);
		assertEquals("handler failed", exception.getMessage());
		assertEquals(7, in.read());
		assertEquals(1, calls.get());
	}
	
	@Test
	void failedReadPoisonsFurtherReads() throws TlsException {
		TlsRecordCipher cipher = cipher(TlsVersion.TLS_1_3, AeadAlgorithm.AES_128_GCM);
		byte[] tampered = protectedRecord(cipher, 1, TlsContentType.APPLICATION_DATA, (byte) 2);
		tampered[tampered.length - 1] ^= 1;
		TlsInputStream in = input(protectedRecord(cipher, 0, TlsContentType.APPLICATION_DATA, (byte) 1), tampered);
		in.changeCipherSpec(cipher);
		
		assertEquals(1, in.read());
		TlsAlertException first = assertThrows(TlsAlertException.class, in::read);
		assertEquals(TlsAlertDescription.BAD_RECORD_MAC, first.alert().description());
		assertSame(first, assertThrows(TlsException.class, in::read).getCause());
		assertSame(first, assertThrows(TlsException.class, in::read).getCause());
	}
	
	@Test
	void sequenceNotAdvancedBySkippedChangeCipherSpecs() throws TlsException {
		TlsRecordCipher cipher = cipher(TlsVersion.TLS_1_3, AeadAlgorithm.AES_128_GCM);
		byte[] changeCipherSpec = record(TlsContentType.CHANGE_CIPHER_SPEC, (byte) 1);
		TlsInputStream in = input(protectedRecord(cipher, 0, TlsContentType.HANDSHAKE, (byte) 1), changeCipherSpec, changeCipherSpec, protectedRecord(cipher, 1, TlsContentType.HANDSHAKE, (byte) 2));
		in.changeCipherSpec(cipher);
		
		assertArrayEquals(new byte[] { 1 }, in.readRecord().orElseThrow().data());
		assertArrayEquals(new byte[] { 2 }, in.readRecord().orElseThrow().data());
	}
	
	@Test
	void ignoredWarningsConsumeSequenceNumbers() throws TlsException {
		TlsRecordCipher cipher = cipher(TlsVersion.TLS_1_2, AeadAlgorithm.CHACHA20_POLY1305);
		byte[] warning = protectedRecord(cipher, 0, TlsContentType.ALERT, TlsAlert.warning(TlsAlertDescription.USER_CANCELED).encode());
		TlsInputStream in = input(warning, protectedRecord(cipher, 1, TlsContentType.HANDSHAKE, DATA));
		in.changeCipherSpec(cipher);
		
		TlsRecord record = in.readRecord().orElseThrow();
		assertEquals(TlsContentType.HANDSHAKE, record.type());
		assertArrayEquals(DATA, record.data());
	}
	
	private static final class TestInputStream extends InputStream {
		
		private final ByteArrayInputStream bytes;
		private final boolean failRead;
		private final boolean failClose;
		private int closes;
		
		private TestInputStream(byte @NonNull [] @NonNull ... parts) {
			this(false, false, parts);
		}
		
		private TestInputStream(boolean failRead, boolean failClose, byte @NonNull [] @NonNull ... parts) {
			this.bytes = new ByteArrayInputStream(CryptoBytes.concat(parts));
			this.failRead = failRead;
			this.failClose = failClose;
		}
		
		@Override
		public int read() throws IOException {
			if (this.failRead) {
				throw new IOException("read failed");
			}
			return this.bytes.read();
		}
		
		@Override
		public int read(byte @NonNull [] target, int offset, int length) throws IOException {
			if (this.failRead) {
				throw new IOException("read failed");
			}
			return this.bytes.read(target, offset, length);
		}
		
		@Override
		public void close() throws IOException {
			this.closes++;
			if (this.failClose) {
				throw new IOException("close failed");
			}
		}
		
		private int remaining() {
			return this.bytes.available();
		}
	}
}
