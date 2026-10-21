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

package net.luis.utils.io.network.connection;

import net.luis.utils.io.network.connection.exception.FrameTooLargeException;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link Framing}.<br>
 *
 * @author Luis-St
 */
class FramingTest {
	
	private static byte[] bytes(String value) {
		return value.getBytes(StandardCharsets.UTF_8);
	}
	
	private static ByteArrayInputStream input(String value) {
		return new ByteArrayInputStream(bytes(value));
	}
	
	private static ByteArrayInputStream input(byte... value) {
		return new ByteArrayInputStream(value);
	}
	
	@Test
	void noneReturnsNoneFraming() {
		Framing framing = Framing.none();
		
		assertInstanceOf(Framing.None.class, framing);
		assertEquals(new Framing.None(), framing);
	}
	
	@Test
	void lengthPrefixedReturnsLengthPrefixedFraming() {
		assertInstanceOf(Framing.LengthPrefixed.class, Framing.lengthPrefixed());
	}
	
	@Test
	void linesReturnsLinesFraming() {
		assertInstanceOf(Framing.Lines.class, Framing.lines());
	}
	
	@Test
	void delimitedReturnsDelimitedFraming() {
		Framing.Delimited framing = Framing.delimited((byte) 0);
		
		assertInstanceOf(Framing.Delimited.class, framing);
		assertArrayEquals(new byte[] { 0 }, framing.delimiter());
	}
	
	@Test
	void constructDelimitedWithNullDelimiter() {
		assertThrows(NullPointerException.class, () -> new Framing.Delimited(null));
		assertThrows(NullPointerException.class, () -> Framing.delimited((byte[]) null));
	}
	
	@Test
	void constructDelimitedWithEmptyDelimiter() {
		assertThrows(IllegalArgumentException.class, () -> new Framing.Delimited(new byte[0]));
		assertThrows(IllegalArgumentException.class, Framing::delimited);
	}
	
	@Test
	void constructDelimitedCopiesDelimiter() {
		byte[] source = { '\r', '\n' };
		Framing.Delimited framing = new Framing.Delimited(source);
		
		source[0] = 'x';
		
		assertArrayEquals(new byte[] { '\r', '\n' }, framing.delimiter());
	}
	
	@Test
	void noneWriteWithNullArguments() {
		Framing framing = Framing.none();
		
		assertThrows(NullPointerException.class, () -> framing.write(null, new byte[1]));
		assertThrows(NullPointerException.class, () -> framing.write(new ByteArrayOutputStream(), null));
	}
	
	@Test
	void noneReadWithNullStream() {
		assertThrows(NullPointerException.class, () -> Framing.none().read(null, 10));
	}
	
	@Test
	void noneReadWithInvalidMaxBytes() {
		Framing framing = Framing.none();
		
		assertThrows(IllegalArgumentException.class, () -> framing.read(input("abc"), 0));
		assertThrows(IllegalArgumentException.class, () -> framing.read(input("abc"), -1));
	}
	
	@Test
	void lengthPrefixedWriteWithNullArguments() {
		Framing framing = Framing.lengthPrefixed();
		
		assertThrows(NullPointerException.class, () -> framing.write(null, new byte[1]));
		assertThrows(NullPointerException.class, () -> framing.write(new ByteArrayOutputStream(), null));
	}
	
	@Test
	void lengthPrefixedReadWithNullStream() {
		assertThrows(NullPointerException.class, () -> Framing.lengthPrefixed().read(null, 10));
	}
	
	@Test
	void lengthPrefixedReadWithInvalidMaxBytes() {
		assertThrows(IllegalArgumentException.class, () -> Framing.lengthPrefixed().read(input((byte) 0, (byte) 0, (byte) 0, (byte) 0), 0));
	}
	
	@Test
	void lengthPrefixedReadTruncatedHeader() {
		Framing framing = Framing.lengthPrefixed();
		
		assertThrows(EOFException.class, () -> framing.read(input((byte) 0), 10));
		assertThrows(EOFException.class, () -> framing.read(input((byte) 0, (byte) 0), 10));
		assertThrows(EOFException.class, () -> framing.read(input((byte) 0, (byte) 0, (byte) 0), 10));
	}
	
	@Test
	void lengthPrefixedReadNegativeLength() {
		IOException exception = assertThrows(IOException.class, () -> Framing.lengthPrefixed().read(input((byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF), 10));
		
		assertFalse(exception instanceof EOFException);
		assertFalse(exception instanceof FrameTooLargeException);
	}
	
	@Test
	void lengthPrefixedReadLengthAboveMaxBytes() {
		byte[] frame = new byte[4 + 11];
		frame[3] = 11;
		ByteArrayInputStream stream = new ByteArrayInputStream(frame);
		
		assertThrows(FrameTooLargeException.class, () -> Framing.lengthPrefixed().read(stream, 10));
		assertEquals(11, stream.available());
	}
	
	@Test
	void lengthPrefixedReadTruncatedPayload() {
		byte[] frame = new byte[4 + 4];
		frame[3] = 10;
		
		assertThrows(EOFException.class, () -> Framing.lengthPrefixed().read(new ByteArrayInputStream(frame), 100));
	}
	
	@Test
	void delimitedWriteWithNullArguments() {
		Framing framing = Framing.delimited((byte) '\n');
		
		assertThrows(NullPointerException.class, () -> framing.write(null, new byte[1]));
		assertThrows(NullPointerException.class, () -> framing.write(new ByteArrayOutputStream(), null));
	}
	
	@Test
	void delimitedReadWithNullStream() {
		assertThrows(NullPointerException.class, () -> Framing.delimited((byte) '\n').read(null, 10));
	}
	
	@Test
	void delimitedReadWithInvalidMaxBytes() {
		assertThrows(IllegalArgumentException.class, () -> Framing.delimited((byte) '\n').read(input("a\n"), 0));
	}
	
	@Test
	void delimitedReadEndsInsideMessage() {
		assertThrows(EOFException.class, () -> Framing.delimited((byte) '\n').read(input("abc"), 10));
	}
	
	@Test
	void delimitedReadMessageExceedsMaxBytes() {
		assertThrows(FrameTooLargeException.class, () -> Framing.delimited((byte) '\n').read(input("abcd\n"), 3));
	}
	
	@Test
	void linesWriteWithNullArguments() {
		Framing framing = Framing.lines();
		
		assertThrows(NullPointerException.class, () -> framing.write(null, new byte[1]));
		assertThrows(NullPointerException.class, () -> framing.write(new ByteArrayOutputStream(), null));
	}
	
	@Test
	void linesReadWithNullStream() {
		assertThrows(NullPointerException.class, () -> Framing.lines().read(null, 10));
	}
	
	@Test
	void noneReadEmptyStreamReturnsNull() throws Exception {
		assertNull(Framing.none().read(input(), 10));
	}
	
	@Test
	void noneReadReturnsAvailableBytes() throws Exception {
		assertArrayEquals(bytes("hello"), Framing.none().read(input("hello"), 100));
	}
	
	@Test
	void noneReadLimitsToMaxBytes() throws Exception {
		Framing framing = Framing.none();
		ByteArrayInputStream stream = input("0123456789");
		
		assertArrayEquals(bytes("0123"), framing.read(stream, 4));
		assertArrayEquals(bytes("4567"), framing.read(stream, 4));
		assertArrayEquals(bytes("89"), framing.read(stream, 4));
		assertNull(framing.read(stream, 4));
	}
	
	@Test
	void noneReadWithMaxBytesOne() throws Exception {
		Framing framing = Framing.none();
		ByteArrayInputStream stream = input("ab");
		
		assertArrayEquals(bytes("a"), framing.read(stream, 1));
		assertArrayEquals(bytes("b"), framing.read(stream, 1));
		assertNull(framing.read(stream, 1));
	}
	
	@Test
	void noneReadShortReadIsTrimmed() throws Exception {
		byte[] data = Framing.none().read(new LyingInputStream(bytes("abcde"), 3), 100);
		
		assertArrayEquals(bytes("abcde"), data);
	}
	
	@Test
	void noneReadNegativeAvailableIsClamped() throws Exception {
		LyingInputStream stream = new LyingInputStream(bytes("abc"), -1);
		
		assertArrayEquals(bytes("a"), Framing.none().read(stream, 100));
	}
	
	@Test
	void lengthPrefixedReadEmptyStreamReturnsNull() throws Exception {
		assertNull(Framing.lengthPrefixed().read(input(), 10));
	}
	
	@Test
	void lengthPrefixedReadLengthEqualToMaxBytes() throws Exception {
		byte[] frame = { 0, 0, 0, 3, 'a', 'b', 'c' };
		
		assertArrayEquals(bytes("abc"), Framing.lengthPrefixed().read(new ByteArrayInputStream(frame), 3));
	}
	
	@Test
	void lengthPrefixedReadZeroLengthPayload() throws Exception {
		byte[] data = Framing.lengthPrefixed().read(input((byte) 0, (byte) 0, (byte) 0, (byte) 0), 10);
		
		assertNotNull(data);
		assertEquals(0, data.length);
	}
	
	@Test
	void delimitedReadEmptyStreamReturnsNull() throws Exception {
		assertNull(Framing.delimited((byte) '\n').read(input(), 10));
	}
	
	@Test
	void delimitedReadMessageAtMaxBytes() throws Exception {
		assertArrayEquals(bytes("abc"), Framing.delimited((byte) '\n').read(input("abc\n"), 3));
	}
	
	@Test
	void delimitedReadEmptyMessage() throws Exception {
		byte[] data = Framing.delimited((byte) '\n').read(input("\n"), 10);
		
		assertNotNull(data);
		assertEquals(0, data.length);
	}
	
	@Test
	void delimitedReadPartialDelimiterInsideMessage() throws Exception {
		assertArrayEquals(bytes("a\rb"), Framing.delimited((byte) '\r', (byte) '\n').read(input("a\rb\r\n"), 10));
	}
	
	@Test
	void delimitedReadGrowsBuffer() throws Exception {
		byte[] message = new byte[1000];
		Arrays.fill(message, (byte) 'x');
		byte[] stream = Arrays.copyOf(message, 1001);
		stream[1000] = '\n';
		
		assertArrayEquals(message, Framing.delimited((byte) '\n').read(new ByteArrayInputStream(stream), 2000));
	}
	
	@Test
	void delimitedReadWithMaxIntegerMaxBytes() throws Exception {
		assertArrayEquals(bytes("short"), Framing.delimited((byte) '\r', (byte) '\n').read(input("short\r\n"), Integer.MAX_VALUE));
	}
	
	@Test
	void linesReadStripsCarriageReturn() throws Exception {
		assertArrayEquals(bytes("abc"), Framing.lines().read(input("abc\r\n"), 10));
	}
	
	@Test
	void linesReadBareLineFeed() throws Exception {
		assertArrayEquals(bytes("abc"), Framing.lines().read(input("abc\n"), 10));
	}
	
	@Test
	void linesReadEmptyLine() throws Exception {
		assertArrayEquals(new byte[0], Framing.lines().read(input("\n"), 10));
	}
	
	@Test
	void linesReadOnlyCarriageReturn() throws Exception {
		assertArrayEquals(new byte[0], Framing.lines().read(input("\r\n"), 10));
	}
	
	@Test
	void linesReadKeepsInnerCarriageReturn() throws Exception {
		assertArrayEquals(bytes("a\rb"), Framing.lines().read(input("a\rb\n"), 10));
	}
	
	@Test
	void linesReadEmptyStreamReturnsNull() throws Exception {
		assertNull(Framing.lines().read(input(), 10));
	}
	
	@Test
	void noneWriteWritesRawBytes() throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		Framing.none().write(out, bytes("raw"));
		
		assertArrayEquals(bytes("raw"), out.toByteArray());
	}
	
	@Test
	void lengthPrefixedWriteProducesBigEndianHeader() throws Exception {
		byte[] data = new byte[258];
		Arrays.fill(data, (byte) 7);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		Framing.lengthPrefixed().write(out, data);
		byte[] written = out.toByteArray();
		
		assertEquals(262, written.length);
		assertArrayEquals(new byte[] { 0, 0, 1, 2 }, Arrays.copyOf(written, 4));
		assertArrayEquals(data, Arrays.copyOfRange(written, 4, 262));
	}
	
	@Test
	void lengthPrefixedWriteEmptyData() throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		Framing.lengthPrefixed().write(out, new byte[0]);
		
		assertArrayEquals(new byte[] { 0, 0, 0, 0 }, out.toByteArray());
	}
	
	@Test
	void delimitedWriteAppendsDelimiter() throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		Framing.delimited((byte) 0, (byte) 1).write(out, bytes("ab"));
		
		assertArrayEquals(new byte[] { 'a', 'b', 0, 1 }, out.toByteArray());
	}
	
	@Test
	void linesWriteAppendsCrLf() throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		Framing.lines().write(out, bytes("line"));
		
		assertArrayEquals(bytes("line\r\n"), out.toByteArray());
	}
	
	@Test
	void delimitedReturnsCopyOfDelimiter() {
		Framing.Delimited framing = Framing.delimited((byte) '\r', (byte) '\n');
		
		byte[] first = framing.delimiter();
		first[0] = 'x';
		
		assertArrayEquals(new byte[] { '\r', '\n' }, framing.delimiter());
	}
	
	@Test
	void delimitedEqualsAndHashCode() {
		Framing.Delimited first = new Framing.Delimited(new byte[] { '\r', '\n' });
		Framing.Delimited second = new Framing.Delimited(new byte[] { '\r', '\n' });
		Framing.Delimited other = new Framing.Delimited(new byte[] { '\n' });
		
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
		assertNotEquals(first, other);
	}
	
	@Test
	void delimitedToStringContainsBytes() {
		assertEquals("Delimited[delimiter=[13, 10]]", Framing.delimited((byte) '\r', (byte) '\n').toString());
	}
	
	@Test
	void delimitedEqualsSameInstanceAndOtherTypes() {
		Framing.Delimited framing = Framing.delimited((byte) '\n');
		
		assertEquals(framing, framing);
		assertNotEquals(null, framing);
		assertNotEquals(framing, Framing.lines());
		assertNotEquals(framing, new byte[] { '\n' });
	}
	
	@Test
	void lengthPrefixedRoundTripMultipleMessages() throws Exception {
		Framing framing = Framing.lengthPrefixed();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		framing.write(out, bytes("first"));
		framing.write(out, new byte[0]);
		framing.write(out, bytes("third"));
		
		ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
		
		assertArrayEquals(bytes("first"), framing.read(in, 100));
		assertArrayEquals(new byte[0], framing.read(in, 100));
		assertArrayEquals(bytes("third"), framing.read(in, 100));
		assertNull(framing.read(in, 100));
	}
	
	@Test
	void lengthPrefixedReadFragmentedStream() throws Exception {
		byte[] frame = { 0, 0, 0, 5, 'h', 'e', 'l', 'l', 'o' };
		
		assertArrayEquals(bytes("hello"), Framing.lengthPrefixed().read(new OneByteInputStream(frame), 100));
	}
	
	@Test
	void lengthPrefixedRoundTripLargeBinaryPayload() throws Exception {
		byte[] data = new byte[64 * 1024];
		new Random(42).nextBytes(data);
		data[0] = 0;
		data[data.length - 1] = 0;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		Framing.lengthPrefixed().write(out, data);
		
		assertArrayEquals(data, Framing.lengthPrefixed().read(new ByteArrayInputStream(out.toByteArray()), data.length));
	}
	
	@Test
	void delimitedRoundTripMultipleMessages() throws Exception {
		Framing framing = Framing.delimited((byte) '|');
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		framing.write(out, bytes("one"));
		framing.write(out, bytes("two"));
		framing.write(out, bytes("three"));
		
		ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
		
		assertArrayEquals(bytes("one"), framing.read(in, 100));
		assertArrayEquals(bytes("two"), framing.read(in, 100));
		assertArrayEquals(bytes("three"), framing.read(in, 100));
		assertNull(framing.read(in, 100));
	}
	
	@Test
	void linesRoundTripMixedLineEndings() throws Exception {
		Framing framing = Framing.lines();
		ByteArrayInputStream in = input("a\r\nb\nc\r\n");
		
		assertArrayEquals(bytes("a"), framing.read(in, 10));
		assertArrayEquals(bytes("b"), framing.read(in, 10));
		assertArrayEquals(bytes("c"), framing.read(in, 10));
		assertNull(framing.read(in, 10));
	}
	
	@Test
	void writeIsSingleWriteCall() throws Exception {
		for (Framing framing : new Framing[] { Framing.lengthPrefixed(), Framing.delimited((byte) '\n'), Framing.lines() }) {
			CountingOutputStream out = new CountingOutputStream();
			
			framing.write(out, bytes("message"));
			
			assertEquals(1, out.writes, framing.toString());
			assertEquals(1, out.flushes, framing.toString());
		}
	}
	
	private static final class OneByteInputStream extends InputStream {
		
		private final ByteArrayInputStream delegate;
		
		private OneByteInputStream(byte[] data) {
			this.delegate = new ByteArrayInputStream(data);
		}
		
		@Override
		public int read() {
			return this.delegate.read();
		}
		
		@Override
		public int read(byte[] buffer, int offset, int length) {
			if (length == 0) {
				return 0;
			}
			return this.delegate.read(buffer, offset, 1);
		}
	}
	
	private static final class LyingInputStream extends InputStream {
		
		private final ByteArrayInputStream delegate;
		private final int availableOffset;
		
		private LyingInputStream(byte[] data, int availableOffset) {
			this.delegate = new ByteArrayInputStream(data);
			this.availableOffset = availableOffset;
		}
		
		@Override
		public int read() {
			return this.delegate.read();
		}
		
		@Override
		public int read(byte[] buffer, int offset, int length) {
			return this.delegate.read(buffer, offset, length);
		}
		
		@Override
		public int available() {
			if (this.availableOffset < 0) {
				return this.availableOffset;
			}
			return this.delegate.available() + this.availableOffset;
		}
	}
	
	private static final class CountingOutputStream extends OutputStream {
		
		private int writes;
		private int flushes;
		
		@Override
		public void write(int value) {
			this.writes++;
		}
		
		@Override
		public void write(byte[] buffer, int offset, int length) {
			this.writes++;
		}
		
		@Override
		public void flush() {
			this.flushes++;
		}
	}
}
