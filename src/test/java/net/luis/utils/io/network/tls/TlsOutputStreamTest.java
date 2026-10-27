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

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link TlsOutputStream}.<br>
 *
 * @author Luis-St
 */
class TlsOutputStreamTest {
	
	private static final byte[] DATA = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
	
	private static @NonNull TlsRecordCipher cipher(@NonNull TlsVersion version, @NonNull AeadAlgorithm algorithm) {
		return new TlsAeadRecordCipher(version, algorithm, new byte[algorithm.keyLength()], new byte[TlsAeadRecordCipher.ivLength(version, algorithm)]);
	}
	
	private static byte @NonNull [] pattern(int length) {
		byte[] data = new byte[length];
		for (int i = 0; i < length; i++) {
			data[i] = (byte) i;
		}
		return data;
	}
	
	private static @NonNull List<TlsRecord> parse(byte @NonNull [] wire) {
		List<TlsRecord> records = new ArrayList<>();
		int position = 0;
		while (position < wire.length) {
			TlsContentType type = TlsContentType.byCode(wire[position] & 0xFF).orElseThrow();
			int length = ((wire[position + 3] & 0xFF) << 8) | (wire[position + 4] & 0xFF);
			records.add(new TlsRecord(type, Arrays.copyOfRange(wire, position + 5, position + 5 + length)));
			position += 5 + length;
		}
		return records;
	}
	
	@Test
	void constructWithOutputStream() {
		TestOutputStream transport = new TestOutputStream();
		assertDoesNotThrow(() -> new TlsOutputStream(transport));
		assertEquals(0, transport.toByteArray().length);
	}
	
	@Test
	void constructWithNullOutputStream() {
		assertThrows(NullPointerException.class, () -> new TlsOutputStream(null));
	}
	
	@Test
	void changeCipherSpecWithNullCipher() {
		TlsOutputStream stream = new TlsOutputStream(new TestOutputStream());
		assertThrows(NullPointerException.class, () -> stream.changeCipherSpec(null));
	}
	
	@Test
	void writeWithNullBuffer() {
		TlsOutputStream stream = new TlsOutputStream(new TestOutputStream());
		assertThrows(NullPointerException.class, () -> stream.write(null, 0, 0));
	}
	
	@Test
	void writeWithInvalidBounds() {
		TestOutputStream transport = new TestOutputStream();
		TlsOutputStream stream = new TlsOutputStream(transport);
		assertThrows(IndexOutOfBoundsException.class, () -> stream.write(new byte[4], -1, 1));
		assertThrows(IndexOutOfBoundsException.class, () -> stream.write(new byte[4], 0, 5));
		assertThrows(IndexOutOfBoundsException.class, () -> stream.write(new byte[4], 3, 2));
		assertThrows(IndexOutOfBoundsException.class, () -> stream.write(new byte[4], 0, -1));
		assertEquals(0, transport.toByteArray().length);
	}
	
	@Test
	void writeRecordWithNullType() {
		TlsOutputStream stream = new TlsOutputStream(new TestOutputStream());
		assertThrows(NullPointerException.class, () -> stream.writeRecord(null, DATA));
	}
	
	@Test
	void writeRecordWithNullData() {
		TlsOutputStream stream = new TlsOutputStream(new TestOutputStream());
		assertThrows(NullPointerException.class, () -> stream.writeRecord(TlsContentType.HANDSHAKE, null));
	}
	
	@Test
	void writeAlertWithNullAlert() {
		TlsOutputStream stream = new TlsOutputStream(new TestOutputStream());
		assertThrows(NullPointerException.class, () -> stream.writeAlert(null));
	}
	
	@Test
	void writeToFailingTransport() {
		TlsOutputStream stream = new TlsOutputStream(new TestOutputStream(true, false, false));
		TlsException exception = assertThrows(TlsException.class, () -> stream.write(1));
		assertInstanceOf(IOException.class, exception.getCause());
		assertEquals("write failed", exception.getCause().getMessage());
	}
	
	@Test
	void flushFailingTransport() {
		TlsOutputStream stream = new TlsOutputStream(new TestOutputStream(false, true, false));
		TlsException exception = assertThrows(TlsException.class, stream::flush);
		assertInstanceOf(IOException.class, exception.getCause());
		assertEquals("flush failed", exception.getCause().getMessage());
	}
	
	@Test
	void writeWithZeroLength() throws IOException {
		TestOutputStream transport = new TestOutputStream();
		new TlsOutputStream(transport).write(DATA, 2, 0);
		assertEquals(0, transport.toByteArray().length);
	}
	
	@Test
	void writeWithPositiveLength() throws IOException {
		TestOutputStream transport = new TestOutputStream();
		new TlsOutputStream(transport).write(new byte[] { 9, 1, 2, 3, 9 }, 1, 3);
		assertArrayEquals(new byte[] { 23, 3, 3, 0, 3, 1, 2, 3 }, transport.toByteArray());
	}
	
	@Test
	void writeRecordEmptyHandshake() {
		TestOutputStream transport = new TestOutputStream();
		TlsOutputStream stream = new TlsOutputStream(transport);
		assertThrows(IllegalArgumentException.class, () -> stream.writeRecord(TlsContentType.HANDSHAKE, new byte[0]));
		assertEquals(0, transport.toByteArray().length);
	}
	
	@Test
	void writeRecordEmptyApplicationData() throws IOException {
		TestOutputStream transport = new TestOutputStream();
		new TlsOutputStream(transport).writeRecord(TlsContentType.APPLICATION_DATA, new byte[0]);
		assertArrayEquals(new byte[] { 23, 3, 3, 0, 0 }, transport.toByteArray());
	}
	
	@Test
	void writeRecordNonEmptyHandshake() throws IOException {
		TestOutputStream transport = new TestOutputStream();
		new TlsOutputStream(transport).writeRecord(TlsContentType.HANDSHAKE, new byte[] { 5, 6 });
		assertArrayEquals(new byte[] { 22, 3, 3, 0, 2, 5, 6 }, transport.toByteArray());
	}
	
	@Test
	void writeAlertTerminalClosesOutbound() throws IOException {
		TestOutputStream transport = new TestOutputStream();
		TlsOutputStream stream = new TlsOutputStream(transport);
		stream.writeAlert(TlsAlert.fatal(TlsAlertDescription.HANDSHAKE_FAILURE));
		
		assertArrayEquals(new byte[] { 21, 3, 3, 0, 2, 2, 40 }, transport.toByteArray());
		TlsException exception = assertThrows(TlsException.class, () -> stream.write(1));
		assertEquals("A terminal alert has already been sent", exception.getMessage());
	}
	
	@Test
	void writeAlertNonTerminalKeepsOutboundOpen() throws IOException {
		TestOutputStream transport = new TestOutputStream();
		TlsOutputStream stream = new TlsOutputStream(transport);
		stream.writeAlert(TlsAlert.warning(TlsAlertDescription.USER_CANCELED));
		
		assertDoesNotThrow(() -> stream.write(1));
		List<TlsRecord> records = parse(transport.toByteArray());
		assertEquals(2, records.size());
		assertEquals(TlsContentType.ALERT, records.get(0).type());
		assertEquals(TlsContentType.APPLICATION_DATA, records.get(1).type());
	}
	
	@Test
	void writeAfterClose() throws IOException {
		TlsOutputStream stream = new TlsOutputStream(new TestOutputStream());
		stream.close();
		TlsException exception = assertThrows(TlsException.class, () -> stream.write(1));
		assertEquals("Stream is closed", exception.getMessage());
	}
	
	@Test
	void writeAfterCloseNotify() throws IOException {
		TlsOutputStream stream = new TlsOutputStream(new TestOutputStream());
		stream.writeAlert(TlsAlert.CLOSE_NOTIFY);
		TlsException exception = assertThrows(TlsException.class, () -> stream.writeRecord(TlsContentType.HANDSHAKE, DATA));
		assertEquals("A terminal alert has already been sent", exception.getMessage());
	}
	
	@Test
	void writeExactlyMaxPlaintextLength() throws IOException {
		TestOutputStream transport = new TestOutputStream();
		new TlsOutputStream(transport).write(new byte[16384]);
		
		assertEquals(5 + 16384, transport.toByteArray().length);
		List<TlsRecord> records = parse(transport.toByteArray());
		assertEquals(1, records.size());
		assertEquals(16384, records.getFirst().length());
	}
	
	@Test
	void writeAboveMaxPlaintextLength() throws IOException {
		TestOutputStream transport = new TestOutputStream();
		byte[] data = pattern(16385);
		new TlsOutputStream(transport).write(data);
		
		List<TlsRecord> records = parse(transport.toByteArray());
		assertEquals(2, records.size());
		assertEquals(16384, records.get(0).length());
		assertEquals(1, records.get(1).length());
		assertArrayEquals(data, CryptoBytes.concat(records.get(0).data(), records.get(1).data()));
	}
	
	@Test
	void closeSendsCloseNotify() throws IOException {
		TestOutputStream transport = new TestOutputStream();
		new TlsOutputStream(transport).close();
		assertArrayEquals(new byte[] { 21, 3, 3, 0, 2, 1, 0 }, transport.toByteArray());
		assertEquals(1, transport.flushes);
		assertEquals(1, transport.closes);
	}
	
	@Test
	void closeAfterTerminalAlert() throws IOException {
		TestOutputStream transport = new TestOutputStream();
		TlsOutputStream stream = new TlsOutputStream(transport);
		stream.writeAlert(TlsAlert.fatal(TlsAlertDescription.INTERNAL_ERROR));
		stream.close();
		
		assertArrayEquals(new byte[] { 21, 3, 3, 0, 2, 2, 80 }, transport.toByteArray());
		assertEquals(1, transport.closes);
	}
	
	@Test
	void closeTwice() throws IOException {
		TestOutputStream transport = new TestOutputStream();
		TlsOutputStream stream = new TlsOutputStream(transport);
		stream.close();
		stream.close();
		
		assertEquals(7, transport.toByteArray().length);
		assertEquals(1, transport.closes);
	}
	
	@Test
	void closeWithFailingWrite() {
		TestOutputStream transport = new TestOutputStream(true, false, false);
		TlsOutputStream stream = new TlsOutputStream(transport);
		
		TlsException exception = assertThrows(TlsException.class, stream::close);
		assertInstanceOf(IOException.class, exception.getCause());
		assertEquals(1, transport.closes);
		TlsException afterClose = assertThrows(TlsException.class, () -> stream.write(1));
		assertEquals("Stream is closed", afterClose.getMessage());
	}
	
	@Test
	void closeWithFailingTransportClose() {
		TestOutputStream transport = new TestOutputStream(false, false, true);
		TlsOutputStream stream = new TlsOutputStream(transport);
		
		TlsException exception = assertThrows(TlsException.class, stream::close);
		assertEquals("Failed to close the transport", exception.getMessage());
		assertEquals("close failed", exception.getCause().getMessage());
		assertArrayEquals(new byte[] { 21, 3, 3, 0, 2, 1, 0 }, transport.toByteArray());
	}
	
	@Test
	void writeSingleByte() throws IOException {
		TestOutputStream transport = new TestOutputStream();
		new TlsOutputStream(transport).write(0x41);
		assertArrayEquals(new byte[] { 23, 3, 3, 0, 1, 0x41 }, transport.toByteArray());
	}
	
	@Test
	void writeSingleByteTruncatesValue() throws IOException {
		TestOutputStream transport = new TestOutputStream();
		new TlsOutputStream(transport).write(0x1FF);
		assertArrayEquals(new byte[] { 23, 3, 3, 0, 1, (byte) 0xFF }, transport.toByteArray());
	}
	
	@Test
	void writeArrayUsesFullBuffer() throws IOException {
		TestOutputStream transport = new TestOutputStream();
		new TlsOutputStream(transport).write(new byte[] { 1, 2, 3 });
		
		List<TlsRecord> records = parse(transport.toByteArray());
		assertEquals(1, records.size());
		assertArrayEquals(new byte[] { 1, 2, 3 }, records.getFirst().data());
	}
	
	@Test
	void writeChangeCipherSpecWritesSingleByteRecord() throws IOException {
		TestOutputStream transport = new TestOutputStream();
		new TlsOutputStream(transport).writeChangeCipherSpec();
		assertArrayEquals(new byte[] { 20, 3, 3, 0, 1, 1 }, transport.toByteArray());
		assertEquals(1, transport.flushes);
	}
	
	@Test
	void writeAlertFlushes() throws IOException {
		TestOutputStream transport = new TestOutputStream();
		new TlsOutputStream(transport).writeAlert(TlsAlert.warning(TlsAlertDescription.USER_CANCELED));
		assertEquals(1, transport.flushes);
	}
	
	@Test
	void flushDelegates() throws IOException {
		TestOutputStream transport = new TestOutputStream();
		new TlsOutputStream(transport).flush();
		assertEquals(1, transport.flushes);
	}
	
	@Test
	void writeManyFragments() throws IOException {
		TestOutputStream transport = new TestOutputStream();
		byte[] data = pattern(3 * 16384 + 5);
		new TlsOutputStream(transport).write(data);
		
		List<TlsRecord> records = parse(transport.toByteArray());
		assertEquals(4, records.size());
		assertEquals(16384, records.get(0).length());
		assertEquals(16384, records.get(1).length());
		assertEquals(16384, records.get(2).length());
		assertEquals(5, records.get(3).length());
		assertArrayEquals(data, CryptoBytes.concat(records.stream().map(TlsRecord::data).toArray(byte[][]::new)));
	}
	
	@Test
	void writeWithOffsetAcrossFragments() throws IOException {
		TestOutputStream transport = new TestOutputStream();
		byte[] buffer = pattern(16384 + 20);
		new TlsOutputStream(transport).write(buffer, 10, 16384 + 5);
		
		List<TlsRecord> records = parse(transport.toByteArray());
		assertEquals(2, records.size());
		assertArrayEquals(Arrays.copyOfRange(buffer, 10, 16384 + 15), CryptoBytes.concat(records.get(0).data(), records.get(1).data()));
	}
	
	@Test
	void writeWithTls13CipherHidesContentType() throws IOException {
		TestOutputStream transport = new TestOutputStream();
		TlsOutputStream stream = new TlsOutputStream(transport);
		stream.changeCipherSpec(cipher(TlsVersion.TLS_1_3, AeadAlgorithm.AES_128_GCM));
		stream.writeRecord(TlsContentType.HANDSHAKE, DATA);
		
		TlsRecord written = parse(transport.toByteArray()).getFirst();
		assertEquals(TlsContentType.APPLICATION_DATA, written.type());
		assertEquals(DATA.length + 17, written.length());
		TlsRecord decrypted = cipher(TlsVersion.TLS_1_3, AeadAlgorithm.AES_128_GCM).decrypt(0, written.type(), written.data());
		assertEquals(TlsContentType.HANDSHAKE, decrypted.type());
		assertArrayEquals(DATA, decrypted.data());
	}
	
	@Test
	void sequenceIncrementsPerRecord() throws IOException {
		TestOutputStream transport = new TestOutputStream();
		TlsOutputStream stream = new TlsOutputStream(transport);
		stream.changeCipherSpec(cipher(TlsVersion.TLS_1_2, AeadAlgorithm.CHACHA20_POLY1305));
		stream.write(DATA);
		stream.write(DATA);
		
		List<TlsRecord> records = parse(transport.toByteArray());
		TlsRecordCipher receiver = cipher(TlsVersion.TLS_1_2, AeadAlgorithm.CHACHA20_POLY1305);
		assertArrayEquals(DATA, receiver.decrypt(0, TlsContentType.APPLICATION_DATA, records.get(0).data()).data());
		assertArrayEquals(DATA, receiver.decrypt(1, TlsContentType.APPLICATION_DATA, records.get(1).data()).data());
		assertThrows(TlsAlertException.class, () -> receiver.decrypt(0, TlsContentType.APPLICATION_DATA, records.get(1).data()));
	}
	
	@Test
	void changeCipherSpecResetsSequence() throws IOException {
		TestOutputStream transport = new TestOutputStream();
		TlsOutputStream stream = new TlsOutputStream(transport);
		stream.changeCipherSpec(cipher(TlsVersion.TLS_1_3, AeadAlgorithm.AES_128_GCM));
		stream.write(DATA);
		stream.write(DATA);
		stream.changeCipherSpec(cipher(TlsVersion.TLS_1_3, AeadAlgorithm.AES_128_GCM));
		stream.write(DATA);
		
		TlsRecord third = parse(transport.toByteArray()).get(2);
		TlsRecord decrypted = cipher(TlsVersion.TLS_1_3, AeadAlgorithm.AES_128_GCM).decrypt(0, TlsContentType.APPLICATION_DATA, third.data());
		assertArrayEquals(DATA, decrypted.data());
	}
	
	@Test
	void splitFragmentsUseConsecutiveSequences() throws IOException {
		TestOutputStream transport = new TestOutputStream();
		TlsOutputStream stream = new TlsOutputStream(transport);
		stream.changeCipherSpec(cipher(TlsVersion.TLS_1_3, AeadAlgorithm.AES_128_GCM));
		stream.write(pattern(16385));
		
		List<TlsRecord> records = parse(transport.toByteArray());
		TlsRecordCipher receiver = cipher(TlsVersion.TLS_1_3, AeadAlgorithm.AES_128_GCM);
		assertEquals(16384, receiver.decrypt(0, TlsContentType.APPLICATION_DATA, records.get(0).data()).length());
		assertEquals(1, receiver.decrypt(1, TlsContentType.APPLICATION_DATA, records.get(1).data()).length());
	}
	
	@Test
	void closeWithTls13CipherSendsEncryptedCloseNotify() throws IOException {
		TestOutputStream transport = new TestOutputStream();
		TlsOutputStream stream = new TlsOutputStream(transport);
		stream.changeCipherSpec(cipher(TlsVersion.TLS_1_3, AeadAlgorithm.AES_128_GCM));
		stream.close();
		
		TlsRecord written = parse(transport.toByteArray()).getFirst();
		assertEquals(TlsContentType.APPLICATION_DATA, written.type());
		TlsRecord decrypted = cipher(TlsVersion.TLS_1_3, AeadAlgorithm.AES_128_GCM).decrypt(0, written.type(), written.data());
		assertEquals(TlsContentType.ALERT, decrypted.type());
		assertEquals(Optional.of(TlsAlert.CLOSE_NOTIFY), TlsAlert.decode(decrypted.data()));
	}
	
	@Test
	void roundTripWithTlsInputStream() throws IOException {
		TestOutputStream transport = new TestOutputStream();
		TlsOutputStream out = new TlsOutputStream(transport);
		out.changeCipherSpec(cipher(TlsVersion.TLS_1_3, AeadAlgorithm.CHACHA20_POLY1305));
		out.writeRecord(TlsContentType.HANDSHAKE, DATA);
		out.write(DATA);
		out.close();
		
		TlsInputStream in = new TlsInputStream(new ByteArrayInputStream(transport.toByteArray()));
		in.changeCipherSpec(cipher(TlsVersion.TLS_1_3, AeadAlgorithm.CHACHA20_POLY1305));
		TlsRecord handshake = in.readRecord().orElseThrow();
		assertEquals(TlsContentType.HANDSHAKE, handshake.type());
		assertArrayEquals(DATA, handshake.data());
		assertArrayEquals(DATA, in.readAllBytes());
		assertTrue(in.isCloseNotifyReceived());
	}
	
	private static final class TestOutputStream extends OutputStream {
		
		private final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		private final boolean failWrite;
		private final boolean failFlush;
		private final boolean failClose;
		private int flushes;
		private int closes;
		
		private TestOutputStream() {
			this(false, false, false);
		}
		
		private TestOutputStream(boolean failWrite, boolean failFlush, boolean failClose) {
			this.failWrite = failWrite;
			this.failFlush = failFlush;
			this.failClose = failClose;
		}
		
		@Override
		public void write(int value) throws IOException {
			if (this.failWrite) {
				throw new IOException("write failed");
			}
			this.bytes.write(value);
		}
		
		@Override
		public void write(byte @NonNull [] buffer, int offset, int length) throws IOException {
			if (this.failWrite) {
				throw new IOException("write failed");
			}
			this.bytes.write(buffer, offset, length);
		}
		
		@Override
		public void flush() throws IOException {
			if (this.failFlush) {
				throw new IOException("flush failed");
			}
			this.flushes++;
		}
		
		@Override
		public void close() throws IOException {
			this.closes++;
			if (this.failClose) {
				throw new IOException("close failed");
			}
		}
		
		private byte @NonNull [] toByteArray() {
			return this.bytes.toByteArray();
		}
	}
}
