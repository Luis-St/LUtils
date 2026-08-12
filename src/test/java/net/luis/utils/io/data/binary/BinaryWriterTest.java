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

package net.luis.utils.io.data.binary;

import net.luis.utils.io.data.OutputProvider;
import net.luis.utils.io.data.binary.exception.BinaryTypeException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link BinaryWriter}.<br>
 *
 * @author Luis-St
 */
class BinaryWriterTest {
	
	private static final BinaryConfig HEADER_CONFIG = new BinaryConfig(true, 64, 65536, 1048576, StandardCharsets.UTF_8);
	private static final Path TEST_FILE = Path.of("test-binary-writer.bin");
	
	@AfterAll
	static void cleanUp() throws Exception {
		Files.deleteIfExists(TEST_FILE);
	}
	
	private static byte[] write(BinaryElement element) {
		return BinaryWriter.toByteArray(element);
	}
	
	private static OutputStream failingStream() {
		return new OutputStream() {
			
			@Override
			public void write(int b) throws IOException {
				throw new IOException("failing stream");
			}
			
			@Override
			public void write(byte @org.jspecify.annotations.NonNull [] b, int off, int len) throws IOException {
				throw new IOException("failing stream");
			}
			
			@Override
			public void flush() throws IOException {
				throw new IOException("failing stream");
			}
		};
	}
	
	@Test
	void constructWithOutputProvider() {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		BinaryWriter writer = new BinaryWriter(new OutputProvider(stream));
		
		assertNotNull(writer);
		writer.writeBinary(new BinaryPrimitive(1));
		
		assertArrayEquals(write(new BinaryPrimitive(1)), stream.toByteArray());
	}
	
	@Test
	void constructWithOutputProviderAndConfig() {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		BinaryWriter writer = new BinaryWriter(new OutputProvider(stream), HEADER_CONFIG);
		writer.writeBinary(new BinaryPrimitive(1));
		
		byte[] data = stream.toByteArray();
		assertEquals((byte) 0x4C, data[0]);
		assertEquals((byte) 0x42, data[1]);
		assertEquals((byte) 0x01, data[2]);
	}
	
	@Test
	void constructWithNullOutput() {
		assertThrows(NullPointerException.class, () -> new BinaryWriter(null));
	}
	
	@Test
	void constructWithNullOutputAndConfig() {
		assertThrows(NullPointerException.class, () -> new BinaryWriter(null, BinaryConfig.DEFAULT));
	}
	
	@Test
	void constructWithNullConfig() {
		OutputProvider output = new OutputProvider(new ByteArrayOutputStream());
		
		assertThrows(NullPointerException.class, () -> new BinaryWriter(output, null));
	}
	
	@Test
	void constructWithNullOutputAndNullConfig() {
		NullPointerException exception = assertThrows(NullPointerException.class, () -> new BinaryWriter(null, null));
		
		assertEquals("Binary config must not be null", exception.getMessage());
	}
	
	@Test
	void writeBinaryWithNullElement() {
		try (BinaryWriter writer = new BinaryWriter(new OutputProvider(new ByteArrayOutputStream()))) {
			assertThrows(NullPointerException.class, () -> writer.writeBinary(null));
		} catch (IOException e) {
			fail(e);
		}
	}
	
	@Test
	void writeBinaryWithAbsentElement() {
		try (BinaryWriter writer = new BinaryWriter(new OutputProvider(new ByteArrayOutputStream()))) {
			BinaryTypeException exception = assertThrows(BinaryTypeException.class, () -> writer.writeBinary(BinaryAbsent.INSTANCE));
			assertTrue(exception.getMessage().contains("only be written as a field of a struct"));
		} catch (IOException e) {
			fail(e);
		}
	}
	
	@Test
	void toByteArrayWithNullElement() {
		assertThrows(NullPointerException.class, () -> BinaryWriter.toByteArray(null));
		assertThrows(NullPointerException.class, () -> BinaryWriter.toByteArray(null, BinaryConfig.DEFAULT));
	}
	
	@Test
	void toByteArrayWithNullConfig() {
		assertThrows(NullPointerException.class, () -> BinaryWriter.toByteArray(new BinaryPrimitive(1), null));
	}
	
	@Test
	void toByteArrayWithAbsentElement() {
		assertThrows(BinaryTypeException.class, () -> BinaryWriter.toByteArray(BinaryAbsent.INSTANCE));
	}
	
	@Test
	void writeBinaryWithInconsistentElement() {
		assertThrows(BinaryTypeException.class, () -> BinaryWriter.toByteArray(() -> BinaryType.STRING));
		assertThrows(BinaryTypeException.class, () -> BinaryWriter.toByteArray(() -> BinaryType.LIST));
		assertThrows(BinaryTypeException.class, () -> BinaryWriter.toByteArray(() -> BinaryType.STRUCT));
		assertThrows(BinaryTypeException.class, () -> BinaryWriter.toByteArray(() -> BinaryType.MAP));
	}
	
	@Test
	void writeBinaryToClosedStreamThrows() {
		BinaryWriter writer = new BinaryWriter(new OutputProvider(failingStream()));
		
		assertThrows(UncheckedIOException.class, () -> writer.writeBinary(new BinaryPrimitive(1)));
	}
	
	@Test
	void toByteArrayWrapsCloseFailure() {
		assertThrows(UncheckedIOException.class, () -> {
			try (BinaryWriter writer = new BinaryWriter(new OutputProvider(failingStream()))) {
				writer.writeBinary(new BinaryPrimitive(1));
			}
		});
	}
	
	@Test
	void writeBinaryWithHeaderEnabled() {
		byte[] data = BinaryWriter.toByteArray(new BinaryPrimitive((byte) 7), HEADER_CONFIG);
		
		assertArrayEquals(new byte[] { 0x4C, 0x42, 0x01, 0x04, 0x07 }, data);
	}
	
	@Test
	void writeBinaryWithHeaderDisabled() {
		byte[] data = write(new BinaryPrimitive((byte) 7));
		
		assertEquals((byte) 0x04, data[0]);
	}
	
	@Test
	void writeNullElement() {
		assertArrayEquals(new byte[] { 0x00 }, write(BinaryNull.INSTANCE));
	}
	
	@Test
	void writeAbsentElementInsideStruct() {
		assertArrayEquals(new byte[] { 0x0C, 0x01, 0x01 }, write(new BinaryStruct(1)));
	}
	
	@Test
	void writeBooleanTrue() {
		assertArrayEquals(new byte[] { 0x03 }, write(new BinaryPrimitive(true)));
	}
	
	@Test
	void writeBooleanFalse() {
		assertArrayEquals(new byte[] { 0x02 }, write(new BinaryPrimitive(false)));
	}
	
	@Test
	void writeByteValue() {
		assertArrayEquals(new byte[] { 0x04, 0x2A }, write(new BinaryPrimitive((byte) 42)));
		assertArrayEquals(new byte[] { 0x04, (byte) 0x80 }, write(new BinaryPrimitive(Byte.MIN_VALUE)));
	}
	
	@Test
	void writeShortValue() {
		assertArrayEquals(new byte[] { 0x05, 0x00 }, write(new BinaryPrimitive((short) 0)));
		assertArrayEquals(new byte[] { 0x05, 0x02 }, write(new BinaryPrimitive((short) 1)));
		assertArrayEquals(new byte[] { 0x05, 0x01 }, write(new BinaryPrimitive((short) -1)));
		
		byte[] minValue = write(new BinaryPrimitive(Short.MIN_VALUE));
		assertEquals((byte) 0x05, minValue[0]);
		assertEquals(Short.MIN_VALUE, BinaryReader.fromByteArray(minValue).getAsShort());
	}
	
	@Test
	void writeIntegerValue() {
		assertArrayEquals(new byte[] { 0x06, 0x00 }, write(new BinaryPrimitive(0)));
		assertArrayEquals(new byte[] { 0x06, 0x01 }, write(new BinaryPrimitive(-1)));
		
		for (int value : new int[] { Integer.MAX_VALUE, Integer.MIN_VALUE }) {
			byte[] data = write(new BinaryPrimitive(value));
			assertEquals((byte) 0x06, data[0]);
			assertEquals(value, BinaryReader.fromByteArray(data).getAsInteger());
		}
	}
	
	@Test
	void writeLongValue() {
		assertArrayEquals(new byte[] { 0x07, 0x00 }, write(new BinaryPrimitive(0L)));
		assertArrayEquals(new byte[] { 0x07, 0x01 }, write(new BinaryPrimitive(-1L)));
		
		for (long value : new long[] { Long.MAX_VALUE, Long.MIN_VALUE }) {
			byte[] data = write(new BinaryPrimitive(value));
			assertEquals((byte) 0x07, data[0]);
			assertEquals(value, BinaryReader.fromByteArray(data).getAsLong());
		}
	}
	
	@Test
	void writeFloatValue() {
		byte[] data = write(new BinaryPrimitive(1.5F));
		int bits = Float.floatToIntBits(1.5F);
		
		assertArrayEquals(new byte[] {
			0x08,
			(byte) (bits >>> 24),
			(byte) (bits >>> 16),
			(byte) (bits >>> 8),
			(byte) bits
		}, data);
	}
	
	@Test
	void writeDoubleValue() {
		byte[] data = write(new BinaryPrimitive(1.5));
		long bits = Double.doubleToLongBits(1.5);
		
		assertEquals(9, data.length);
		assertEquals((byte) 0x09, data[0]);
		for (int i = 0; i < 8; i++) {
			assertEquals((byte) (bits >>> (56 - 8 * i)), data[i + 1]);
		}
	}
	
	@Test
	void writeStringValue() {
		byte[] data = write(new BinaryPrimitive("abc"));
		
		assertArrayEquals(new byte[] { 0x0A, 0x03, 'a', 'b', 'c' }, data);
	}
	
	@Test
	void writeEmptyString() {
		assertArrayEquals(new byte[] { 0x0A, 0x00 }, write(new BinaryPrimitive("")));
	}
	
	@Test
	void writeEmptyList() {
		assertArrayEquals(new byte[] { 0x0B, 0x00 }, write(new BinaryArray()));
	}
	
	@Test
	void writeListWithElements() {
		BinaryArray array = new BinaryArray(new BinaryPrimitive((byte) 1), new BinaryPrimitive((byte) 2));
		
		assertArrayEquals(new byte[] { 0x0B, 0x02, 0x04, 0x01, 0x04, 0x02 }, write(array));
	}
	
	@Test
	void writeEmptyStruct() {
		assertArrayEquals(new byte[] { 0x0C, 0x00 }, write(new BinaryStruct(0)));
	}
	
	@Test
	void writeStructWithFields() {
		BinaryStruct struct = new BinaryStruct(2);
		struct.set(0, "alpha", (byte) 1);
		struct.set(1, "beta", (byte) 2);
		
		byte[] data = write(struct);
		
		assertArrayEquals(new byte[] { 0x0C, 0x02, 0x04, 0x01, 0x04, 0x02 }, data);
		assertFalse(new String(data, StandardCharsets.UTF_8).contains("alpha"));
		assertFalse(new String(data, StandardCharsets.UTF_8).contains("beta"));
	}
	
	@Test
	void writeEmptyMap() {
		assertArrayEquals(new byte[] { 0x0D, 0x00 }, write(new BinaryMap()));
	}
	
	@Test
	void writeMapWithEntries() {
		BinaryMap map = new BinaryMap();
		map.add("a", (byte) 1);
		map.add("b", (byte) 2);
		
		byte[] data = write(map);
		
		assertEquals((byte) 0x0D, data[0]);
		assertEquals((byte) 0x02, data[1]);
		assertEquals(10, data.length);
		
		byte[] aFirst = { 0x0D, 0x02, 0x01, 'a', 0x04, 0x01, 0x01, 'b', 0x04, 0x02 };
		byte[] bFirst = { 0x0D, 0x02, 0x01, 'b', 0x04, 0x02, 0x01, 'a', 0x04, 0x01 };
		assertTrue(Arrays.equals(aFirst, data) || Arrays.equals(bFirst, data), "Unexpected encoding: " + Arrays.toString(data));
		
		assertEquals(map, BinaryReader.fromByteArray(data));
	}
	
	@Test
	void writeVarLongSingleByteValues() {
		assertArrayEquals(new byte[] { 0x06, 0x00 }, write(new BinaryPrimitive(0)));
		
		byte[] data = write(new BinaryPrimitive(63));
		assertEquals(2, data.length);
		assertEquals(126, data[1] & 0xFF);
	}
	
	@Test
	void writeVarLongMultiByteValues() {
		int[] values = { 64, 8192, 1 << 27 };
		int[] expectedLengths = { 2, 3, 5 };
		
		for (int i = 0; i < values.length; i++) {
			byte[] data = write(new BinaryPrimitive(values[i]));
			byte[] payload = Arrays.copyOfRange(data, 1, data.length);
			
			assertEquals(expectedLengths[i], payload.length, "Unexpected payload length for " + values[i]);
			for (int j = 0; j < payload.length - 1; j++) {
				assertNotEquals(0, payload[j] & 0x80);
			}
			assertEquals(0, payload[payload.length - 1] & 0x80);
		}
	}
	
	@Test
	void writeVarLongMaximumLength() {
		byte[] data = write(new BinaryPrimitive(Long.MIN_VALUE));
		
		assertEquals(11, data.length);
		assertEquals(Long.MIN_VALUE, BinaryReader.fromByteArray(data).getAsLong());
	}
	
	@Test
	void toByteArrayWithDefaultConfig() {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		try (BinaryWriter writer = new BinaryWriter(new OutputProvider(stream))) {
			writer.writeBinary(new BinaryPrimitive(1));
		} catch (IOException e) {
			fail(e);
		}
		
		assertArrayEquals(stream.toByteArray(), BinaryWriter.toByteArray(new BinaryPrimitive(1)));
	}
	
	@Test
	void toByteArrayWithCustomConfig() {
		byte[] data = BinaryWriter.toByteArray(new BinaryPrimitive(1), HEADER_CONFIG);
		
		assertEquals((byte) 0x4C, data[0]);
		assertEquals((byte) 0x42, data[1]);
		assertEquals((byte) 0x01, data[2]);
	}
	
	@Test
	void writeStringWithCustomCharset() {
		BinaryConfig latin = new BinaryConfig(false, 64, 65536, 1048576, StandardCharsets.ISO_8859_1);
		
		byte[] utf8 = write(new BinaryPrimitive("ä"));
		byte[] latin1 = BinaryWriter.toByteArray(new BinaryPrimitive("ä"), latin);
		
		assertEquals(2, utf8[1]);
		assertEquals(1, latin1[1]);
		assertArrayEquals("ä".getBytes(StandardCharsets.UTF_8), Arrays.copyOfRange(utf8, 2, utf8.length));
		assertArrayEquals("ä".getBytes(StandardCharsets.ISO_8859_1), Arrays.copyOfRange(latin1, 2, latin1.length));
	}
	
	@Test
	void writeStringWithMultiByteCharacters() {
		byte[] data = write(new BinaryPrimitive("日本語"));
		
		assertEquals(9, data[1]);
		assertEquals(3, "日本語".length());
	}
	
	@Test
	void closeWriterReleasesStream() {
		BinaryWriter writer = new BinaryWriter(new OutputProvider(new ByteArrayOutputStream()));
		
		assertDoesNotThrow(writer::close);
	}
	
	@Test
	void closeWriterTwice() {
		BinaryWriter writer = new BinaryWriter(new OutputProvider(new ByteArrayOutputStream()));
		
		assertDoesNotThrow(writer::close);
		assertDoesNotThrow(writer::close);
	}
	
	@Test
	void writerUsableInTryWithResources() {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		
		assertDoesNotThrow(() -> {
			try (BinaryWriter writer = new BinaryWriter(new OutputProvider(stream))) {
				writer.writeBinary(new BinaryPrimitive("value"));
			}
		});
		
		assertEquals("value", BinaryReader.fromByteArray(stream.toByteArray()).getAsString());
	}
	
	@Test
	void writeMultipleElementsToSameWriter() {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		try (BinaryWriter writer = new BinaryWriter(new OutputProvider(stream))) {
			writer.writeBinary(new BinaryPrimitive((byte) 1));
			writer.writeBinary(new BinaryPrimitive((byte) 2));
		} catch (IOException e) {
			fail(e);
		}
		
		assertArrayEquals(new byte[] { 0x04, 0x01, 0x04, 0x02 }, stream.toByteArray());
		assertEquals((byte) 1, BinaryReader.fromByteArray(stream.toByteArray()).getAsByte());
	}
	
	@Test
	void writeNestedContainers() {
		BinaryStruct struct = new BinaryStruct(1);
		struct.set(0, (byte) 7);
		BinaryArray array = new BinaryArray(struct);
		BinaryMap map = new BinaryMap();
		map.add("a", array);
		
		byte[] data = write(map);
		
		assertArrayEquals(new byte[] { 0x0D, 0x01, 0x01, 'a', 0x0B, 0x01, 0x0C, 0x01, 0x04, 0x07 }, data);
		assertEquals(map, BinaryReader.fromByteArray(data));
	}
	
	@Test
	void writeDeeplyNestedStructure() {
		BinaryArray current = new BinaryArray();
		for (int i = 0; i < 20; i++) {
			current = new BinaryArray(current);
		}
		BinaryArray nested = current;
		
		byte[] data = assertDoesNotThrow(() -> write(nested));
		
		assertEquals(nested, BinaryReader.fromByteArray(data));
	}
	
	@Test
	void writeStructWithMixedPresentAndAbsentFields() {
		BinaryStruct struct = new BinaryStruct(4);
		struct.set(0, (byte) 1);
		struct.set(2, (byte) 2);
		
		assertArrayEquals(new byte[] { 0x0C, 0x04, 0x04, 0x01, 0x01, 0x04, 0x02, 0x01 }, write(struct));
	}
	
	@Test
	void writeAllPrimitiveTypesInOneList() {
		BinaryArray array = new BinaryArray(
			BinaryNull.INSTANCE,
			new BinaryPrimitive(true),
			new BinaryPrimitive((byte) 1),
			new BinaryPrimitive((short) 2),
			new BinaryPrimitive(3),
			new BinaryPrimitive(4L),
			new BinaryPrimitive(5.0F),
			new BinaryPrimitive(6.0),
			new BinaryPrimitive("text")
		);
		
		byte[] data = write(array);
		
		assertEquals((byte) 0x0B, data[0]);
		assertEquals((byte) 0x09, data[1]);
		assertEquals(array, BinaryReader.fromByteArray(data));
	}
	
	@Test
	void writerAndReaderRoundTripWithHeader() {
		BinaryMap original = new BinaryMap();
		original.add("a", 1);
		original.add("b", "text");
		
		byte[] data = BinaryWriter.toByteArray(original, HEADER_CONFIG);
		
		assertEquals(original, BinaryReader.fromByteArray(data, HEADER_CONFIG));
	}
	
	@Test
	void writeToFileAndReadBack() throws Exception {
		BinaryMap original = new BinaryMap();
		original.add("value", 42);
		
		try (BinaryWriter writer = new BinaryWriter(new OutputProvider(TEST_FILE))) {
			writer.writeBinary(original);
		}
		
		byte[] data = Files.readAllBytes(TEST_FILE);
		assertEquals(original, BinaryReader.fromByteArray(data));
	}
}
