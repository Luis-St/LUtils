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

import net.luis.utils.io.data.InputProvider;
import net.luis.utils.io.data.OutputProvider;
import net.luis.utils.io.data.binary.exception.BinarySyntaxException;
import org.junit.jupiter.api.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.zip.DeflaterOutputStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link BinaryReader}.<br>
 *
 * @author Luis-St
 */
class BinaryReaderTest {
	
	private static final BinaryConfig HEADER_CONFIG = new BinaryConfig(true, 64, 65536, 1048576, StandardCharsets.UTF_8);
	private static final BinaryConfig DEFLATE_CONFIG = new BinaryConfig(
		true, 64, 65536, 1048576, BinaryConfig.DEFAULT_MAX_DOCUMENT_SIZE, StandardCharsets.UTF_8, BinaryCompression.DEFLATE
	);
	private static final Path TEST_FILE = Path.of("test-binary-reader.bin");
	
	@AfterAll
	static void cleanUp() throws Exception {
		Files.deleteIfExists(TEST_FILE);
	}
	
	private static BinaryElement read(byte... data) {
		return BinaryReader.fromByteArray(data);
	}
	
	private static BinaryElement roundTrip(BinaryElement element) {
		return BinaryReader.fromByteArray(BinaryWriter.toByteArray(element));
	}
	
	private static byte[] deflate(byte[] data) {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		try (DeflaterOutputStream out = new DeflaterOutputStream(buffer)) {
			out.write(data);
		} catch (IOException e) {
			fail(e);
		}
		return buffer.toByteArray();
	}
	
	private static byte[] compressedDocument(byte[] payload) {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		buffer.writeBytes(new byte[] { 0x4C, 0x42, 0x01, BinaryConfig.FLAG_COMPRESSED });
		
		long remaining = payload.length;
		while ((remaining & ~0x7FL) != 0) {
			buffer.write((int) (remaining & 0x7F) | 0x80);
			remaining >>>= 7;
		}
		buffer.write((int) remaining);
		
		buffer.writeBytes(payload);
		return buffer.toByteArray();
	}
	
	private static BinaryArray booleans(boolean... values) {
		BinaryArray array = new BinaryArray();
		for (boolean value : values) {
			array.add(value);
		}
		return array;
	}
	
	@Test
	void constructWithInputProvider() {
		byte[] data = BinaryWriter.toByteArray(new BinaryPrimitive(1));
		
		try (BinaryReader reader = new BinaryReader(new InputProvider(data))) {
			assertEquals(new BinaryPrimitive(1), reader.readBinary());
		} catch (IOException e) {
			fail(e);
		}
	}
	
	@Test
	void constructWithInputProviderAndConfig() {
		byte[] data = BinaryWriter.toByteArray(new BinaryPrimitive(1), HEADER_CONFIG);
		
		try (BinaryReader reader = new BinaryReader(new InputProvider(data), HEADER_CONFIG)) {
			assertEquals(new BinaryPrimitive(1), reader.readBinary());
		} catch (IOException e) {
			fail(e);
		}
	}
	
	@Test
	void constructWithNullInput() {
		assertThrows(NullPointerException.class, () -> new BinaryReader(null));
	}
	
	@Test
	void constructWithNullInputAndConfig() {
		assertThrows(NullPointerException.class, () -> new BinaryReader(null, BinaryConfig.DEFAULT));
	}
	
	@Test
	void constructWithNullConfig() {
		InputProvider input = new InputProvider(new byte[] { 0x00 });
		
		assertThrows(NullPointerException.class, () -> new BinaryReader(input, null));
	}
	
	@Test
	void constructWithNullInputAndNullConfig() {
		NullPointerException exception = assertThrows(NullPointerException.class, () -> new BinaryReader(null, null));
		
		assertEquals("Binary config must not be null", exception.getMessage());
	}
	
	@Test
	void fromByteArrayWithNullData() {
		assertThrows(NullPointerException.class, () -> BinaryReader.fromByteArray(null));
		assertThrows(NullPointerException.class, () -> BinaryReader.fromByteArray(null, BinaryConfig.DEFAULT));
	}
	
	@Test
	void fromByteArrayWithNullConfig() {
		byte[] data = { 0x00 };
		
		assertThrows(NullPointerException.class, () -> BinaryReader.fromByteArray(data, null));
	}
	
	@Test
	void readBinaryWithEmptyInput() {
		BinarySyntaxException exception = assertThrows(BinarySyntaxException.class, () -> BinaryReader.fromByteArray(new byte[0]));
		
		assertTrue(exception.getMessage().contains("truncated"));
	}
	
	@Test
	void readBinaryWithUnknownTypeTag() {
		BinarySyntaxException exception = assertThrows(BinarySyntaxException.class, () -> read((byte) 0x7F));
		
		assertTrue(exception.getMessage().contains("0x7F"));
	}
	
	@Test
	void readBinaryWithInvalidMagicNumber() {
		byte[] data = BinaryWriter.toByteArray(new BinaryPrimitive(1));
		
		BinarySyntaxException exception = assertThrows(BinarySyntaxException.class, () -> BinaryReader.fromByteArray(data, HEADER_CONFIG));
		assertTrue(exception.getMessage().contains("magic"));
	}
	
	@Test
	void readBinaryWithUnsupportedVersion() {
		byte[] data = { 0x4C, 0x42, 0x02, 0x00 };
		
		BinarySyntaxException exception = assertThrows(BinarySyntaxException.class, () -> BinaryReader.fromByteArray(data, HEADER_CONFIG));
		assertTrue(exception.getMessage().contains("version"));
	}
	
	@Test
	void readBinaryWithTruncatedHeader() {
		byte[] data = { 0x4C };
		
		BinarySyntaxException exception = assertThrows(BinarySyntaxException.class, () -> BinaryReader.fromByteArray(data, HEADER_CONFIG));
		assertTrue(exception.getMessage().contains("truncated"));
	}
	
	@Test
	void readBinaryWithTruncatedPayload() {
		assertTrue(assertThrows(BinarySyntaxException.class, () -> read((byte) 0x09, (byte) 0x00, (byte) 0x00, (byte) 0x00)).getMessage().contains("truncated"));
		assertTrue(assertThrows(BinarySyntaxException.class, () -> read((byte) 0x0A, (byte) 0x05, (byte) 'a')).getMessage().contains("truncated"));
	}
	
	@Test
	void readBinaryWithTruncatedCollection() {
		BinarySyntaxException exception = assertThrows(BinarySyntaxException.class, () -> read((byte) 0x0B, (byte) 0x03, (byte) 0x00));
		
		assertTrue(exception.getMessage().contains("truncated"));
	}
	
	@Test
	void readBinaryExceedingMaxDepth() {
		BinaryArray nested = new BinaryArray(new BinaryArray(new BinaryArray()));
		byte[] data = BinaryWriter.toByteArray(nested);
		BinaryConfig shallow = new BinaryConfig(false, 1, 65536, 1048576, StandardCharsets.UTF_8);
		
		BinarySyntaxException exception = assertThrows(BinarySyntaxException.class, () -> BinaryReader.fromByteArray(data, shallow));
		assertTrue(exception.getMessage().contains("maximum depth"));
	}
	
	@Test
	void readBinaryExceedingMaxCollectionSize() {
		BinaryArray array = new BinaryArray(new BinaryPrimitive(1), new BinaryPrimitive(2), new BinaryPrimitive(3));
		byte[] data = BinaryWriter.toByteArray(array);
		BinaryConfig small = new BinaryConfig(false, 64, 2, 1048576, StandardCharsets.UTF_8);
		
		BinarySyntaxException exception = assertThrows(BinarySyntaxException.class, () -> BinaryReader.fromByteArray(data, small));
		assertTrue(exception.getMessage().contains("list"));
	}
	
	@Test
	void readBinaryWithNegativeCollectionSize() {
		byte[] data = {
			0x0B,
			(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
			(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, 0x01
		};
		
		assertThrows(BinarySyntaxException.class, () -> BinaryReader.fromByteArray(data));
	}
	
	@Test
	void readBinaryExceedingMaxStringLength() {
		byte[] data = BinaryWriter.toByteArray(new BinaryPrimitive("abcdef"));
		BinaryConfig small = new BinaryConfig(false, 64, 65536, 1, StandardCharsets.UTF_8);
		
		BinarySyntaxException exception = assertThrows(BinarySyntaxException.class, () -> BinaryReader.fromByteArray(data, small));
		assertTrue(exception.getMessage().contains("maximum string length"));
	}
	
	@Test
	void readBinaryWithNegativeStringLength() {
		byte[] data = {
			0x0A,
			(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
			(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, 0x01
		};
		
		assertThrows(BinarySyntaxException.class, () -> BinaryReader.fromByteArray(data));
	}
	
	@Test
	void readBinaryWithOverlongVarInt() {
		byte[] data = new byte[12];
		data[0] = 0x06;
		for (int i = 1; i < data.length; i++) {
			data[i] = (byte) 0x80;
		}
		
		BinarySyntaxException exception = assertThrows(BinarySyntaxException.class, () -> BinaryReader.fromByteArray(data));
		assertTrue(exception.getMessage().contains("ten bytes"));
	}
	
	@Test
	void readBinaryWrapsIoException() {
		InputStream stream = new InputStream() {
			
			@Override
			public int read() throws IOException {
				throw new IOException("failing stream");
			}
			
			@Override
			public int read(byte @org.jspecify.annotations.NonNull [] b, int off, int len) throws IOException {
				throw new IOException("failing stream");
			}
		};
		
		BinaryReader reader = new BinaryReader(new InputProvider(stream));
		
		assertThrows(UncheckedIOException.class, reader::readBinary);
	}
	
	@Test
	void readStructSizeExceedingMaxCollectionSize() {
		byte[] data = BinaryWriter.toByteArray(new BinaryStruct(3));
		BinaryConfig small = new BinaryConfig(false, 64, 2, 1048576, StandardCharsets.UTF_8);
		
		BinarySyntaxException exception = assertThrows(BinarySyntaxException.class, () -> BinaryReader.fromByteArray(data, small));
		assertTrue(exception.getMessage().contains("struct"));
	}
	
	@Test
	void readMapSizeExceedingMaxCollectionSize() {
		BinaryMap map = new BinaryMap();
		map.add("a", 1);
		map.add("b", 2);
		map.add("c", 3);
		byte[] data = BinaryWriter.toByteArray(map);
		BinaryConfig small = new BinaryConfig(false, 64, 2, 1048576, StandardCharsets.UTF_8);
		
		BinarySyntaxException exception = assertThrows(BinarySyntaxException.class, () -> BinaryReader.fromByteArray(data, small));
		assertTrue(exception.getMessage().contains("map"));
	}
	
	@Test
	void readWithUnknownHeaderFlags() {
		byte[] data = { 0x4C, 0x42, 0x01, 0x02, 0x04, 0x07 };
		
		BinarySyntaxException exception = assertThrows(BinarySyntaxException.class, () -> BinaryReader.fromByteArray(data, HEADER_CONFIG));
		assertTrue(exception.getMessage().contains("flags"));
	}
	
	@Test
	void readCompressedDocumentWithInvalidData() {
		byte[] data = compressedDocument(new byte[] { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF });
		
		BinarySyntaxException exception = assertThrows(BinarySyntaxException.class, () -> BinaryReader.fromByteArray(data, HEADER_CONFIG));
		assertTrue(exception.getMessage().contains("decompress"));
	}
	
	@Test
	void readCompressedDocumentWithTruncatedData() {
		byte[] compressed = deflate(BinaryWriter.toByteArray(new BinaryPrimitive("a value which compresses")));
		byte[] truncated = Arrays.copyOfRange(compressed, 0, compressed.length - 4);
		byte[] data = compressedDocument(truncated);
		
		BinarySyntaxException exception = assertThrows(BinarySyntaxException.class, () -> BinaryReader.fromByteArray(data, HEADER_CONFIG));
		assertTrue(exception.getMessage().contains("truncated"));
	}
	
	@Test
	@Timeout(5)
	void readCompressedDocumentWithExcessiveLength() {
		byte[] data = { 0x4C, 0x42, 0x01, BinaryConfig.FLAG_COMPRESSED, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, 0x7F };
		
		BinarySyntaxException exception = assertThrows(BinarySyntaxException.class, () -> BinaryReader.fromByteArray(data, HEADER_CONFIG));
		assertTrue(exception.getMessage().contains("maximum document size"));
	}
	
	@Test
	@Timeout(5)
	void readCompressedDocumentWithNegativeLength() {
		byte[] data = {
			0x4C, 0x42, 0x01, BinaryConfig.FLAG_COMPRESSED,
			(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
			(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, 0x01
		};
		
		BinarySyntaxException exception = assertThrows(BinarySyntaxException.class, () -> BinaryReader.fromByteArray(data, HEADER_CONFIG));
		assertTrue(exception.getMessage().contains("maximum document size"));
	}
	
	@Test
	void readCompressedDocumentExceedingMaxDocumentSizeWhenInflated() {
		BinaryArray array = new BinaryArray();
		for (int i = 0; i < 512; i++) {
			array.add("compressible");
		}
		byte[] data = BinaryWriter.toByteArray(array, BinaryConfig.COMPRESSED);
		BinaryConfig restricted = new BinaryConfig(true, 64, 65536, 1048576, 1024, StandardCharsets.UTF_8, BinaryCompression.NONE);
		
		BinarySyntaxException exception = assertThrows(BinarySyntaxException.class, () -> BinaryReader.fromByteArray(data, restricted));
		assertTrue(exception.getMessage().contains("maximum document size"));
	}
	
	@Test
	void readTypedListWithTypeWithoutPayload() {
		BinarySyntaxException exception = assertThrows(BinarySyntaxException.class, () -> read((byte) 0x10, (byte) 0x00, (byte) 0x01));
		
		assertTrue(exception.getMessage().contains("no payload"));
	}
	
	@Test
	void readTypedListWithAbsentOrBooleanElementType() {
		for (byte id : new byte[] { 0x01, 0x02, 0x03 }) {
			BinarySyntaxException exception = assertThrows(BinarySyntaxException.class, () -> read((byte) 0x10, id, (byte) 0x01));
			
			assertTrue(exception.getMessage().contains("no payload"), "Unexpected message for id " + id);
		}
	}
	
	@Test
	void readTypedListWithNonPrimitiveType() {
		BinarySyntaxException exception = assertThrows(BinarySyntaxException.class, () -> read((byte) 0x10, (byte) 0x0C, (byte) 0x01));
		
		assertTrue(exception.getMessage().contains("not a primitive"));
	}
	
	@Test
	void readTypedListWithListOrMapElementType() {
		for (byte id : new byte[] { 0x0B, 0x0D, 0x0E }) {
			BinarySyntaxException exception = assertThrows(BinarySyntaxException.class, () -> read((byte) 0x10, id, (byte) 0x01));
			
			assertTrue(exception.getMessage().contains("not a primitive"), "Unexpected message for id " + id);
		}
	}
	
	@Test
	void readTypedListWithUnknownElementType() {
		BinarySyntaxException exception = assertThrows(BinarySyntaxException.class, () -> read((byte) 0x10, (byte) 0x7F, (byte) 0x01));
		
		assertTrue(exception.getMessage().contains("0x7F"));
	}
	
	@Test
	void readCompactedListExceedingMaxCollectionSize() {
		BinaryConfig small = new BinaryConfig(false, 64, 2, 1048576, StandardCharsets.UTF_8);
		
		assertThrows(BinarySyntaxException.class, () -> BinaryReader.fromByteArray(new byte[] { 0x0E, 0x64 }, small));
		assertThrows(BinarySyntaxException.class, () -> BinaryReader.fromByteArray(new byte[] { 0x0F, 0x64 }, small));
	}
	
	@Test
	void readCompactedListWithTruncatedPayload() {
		assertTrue(assertThrows(BinarySyntaxException.class, () -> read((byte) 0x0E, (byte) 0x04, (byte) 0x01)).getMessage().contains("truncated"));
		assertTrue(assertThrows(BinarySyntaxException.class, () -> read((byte) 0x0F, (byte) 0x10, (byte) 0x00)).getMessage().contains("truncated"));
	}
	
	@Test
	void readBinaryWithHeaderEnabled() {
		byte[] data = BinaryWriter.toByteArray(new BinaryPrimitive("value"), HEADER_CONFIG);
		
		assertEquals("value", BinaryReader.fromByteArray(data, HEADER_CONFIG).getAsString());
	}
	
	@Test
	void readBinaryWithHeaderDisabled() {
		assertEquals("value", BinaryReader.fromByteArray(BinaryWriter.toByteArray(new BinaryPrimitive("value"))).getAsString());
	}
	
	@Test
	void readNullElement() {
		assertSame(BinaryNull.INSTANCE, read((byte) 0x00));
	}
	
	@Test
	void readAbsentElement() {
		assertSame(BinaryAbsent.INSTANCE, read((byte) 0x01));
	}
	
	@Test
	void readBooleanTrue() {
		assertTrue(read((byte) 0x03).getAsBoolean());
	}
	
	@Test
	void readBooleanFalse() {
		assertFalse(read((byte) 0x02).getAsBoolean());
	}
	
	@Test
	void readByteValue() {
		assertEquals((byte) 42, read((byte) 0x04, (byte) 42).getAsByte());
		assertEquals(Byte.MIN_VALUE, roundTrip(new BinaryPrimitive(Byte.MIN_VALUE)).getAsByte());
		assertEquals(Byte.MAX_VALUE, roundTrip(new BinaryPrimitive(Byte.MAX_VALUE)).getAsByte());
	}
	
	@Test
	void readShortValue() {
		for (short value : new short[] { 0, 1, -1, Short.MIN_VALUE, Short.MAX_VALUE }) {
			BinaryElement decoded = roundTrip(new BinaryPrimitive(value));
			assertEquals(value, decoded.getAsShort());
			assertEquals(BinaryType.SHORT, decoded.getType());
		}
	}
	
	@Test
	void readIntegerValue() {
		for (int value : new int[] { 0, -1, Integer.MIN_VALUE, Integer.MAX_VALUE }) {
			BinaryElement decoded = roundTrip(new BinaryPrimitive(value));
			assertEquals(value, decoded.getAsInteger());
			assertEquals(BinaryType.INTEGER, decoded.getType());
		}
	}
	
	@Test
	void readLongValue() {
		for (long value : new long[] { 0L, -1L, Long.MIN_VALUE, Long.MAX_VALUE }) {
			BinaryElement decoded = roundTrip(new BinaryPrimitive(value));
			assertEquals(value, decoded.getAsLong());
			assertEquals(BinaryType.LONG, decoded.getType());
		}
	}
	
	@Test
	void readFloatValue() {
		assertEquals(1.5F, roundTrip(new BinaryPrimitive(1.5F)).getAsFloat());
		assertTrue(Float.isNaN(roundTrip(new BinaryPrimitive(Float.NaN)).getAsFloat()));
		assertEquals(Float.POSITIVE_INFINITY, roundTrip(new BinaryPrimitive(Float.POSITIVE_INFINITY)).getAsFloat());
		assertEquals(Float.NEGATIVE_INFINITY, roundTrip(new BinaryPrimitive(Float.NEGATIVE_INFINITY)).getAsFloat());
	}
	
	@Test
	void readDoubleValue() {
		assertEquals(1.5, roundTrip(new BinaryPrimitive(1.5)).getAsDouble());
		assertTrue(Double.isNaN(roundTrip(new BinaryPrimitive(Double.NaN)).getAsDouble()));
		assertEquals(Double.POSITIVE_INFINITY, roundTrip(new BinaryPrimitive(Double.POSITIVE_INFINITY)).getAsDouble());
		assertEquals(Double.NEGATIVE_INFINITY, roundTrip(new BinaryPrimitive(Double.NEGATIVE_INFINITY)).getAsDouble());
		assertEquals(Double.doubleToRawLongBits(-0.0), Double.doubleToRawLongBits(roundTrip(new BinaryPrimitive(-0.0)).getAsDouble()));
	}
	
	@Test
	void readStringValue() {
		assertEquals("abc", read((byte) 0x0A, (byte) 0x03, (byte) 'a', (byte) 'b', (byte) 'c').getAsString());
	}
	
	@Test
	void readEmptyString() {
		BinaryElement element = read((byte) 0x0A, (byte) 0x00);
		
		assertEquals("", element.getAsString());
		assertEquals(BinaryType.STRING, element.getType());
	}
	
	@Test
	void readEmptyList() {
		BinaryElement element = read((byte) 0x0B, (byte) 0x00);
		
		assertInstanceOf(BinaryArray.class, element);
		assertTrue(element.getAsBinaryArray().isEmpty());
	}
	
	@Test
	void readListWithElements() {
		BinaryArray original = new BinaryArray(new BinaryPrimitive(1), new BinaryArray(new BinaryPrimitive(2)));
		
		BinaryArray decoded = roundTrip(original).getAsBinaryArray();
		
		assertEquals(2, decoded.size());
		assertEquals(1, decoded.getAsInteger(0));
		assertEquals(2, decoded.getAsBinaryArray(1).getAsInteger(0));
	}
	
	@Test
	void readEmptyStruct() {
		BinaryElement element = read((byte) 0x0C, (byte) 0x00);
		
		assertInstanceOf(BinaryStruct.class, element);
		assertTrue(element.getAsBinaryStruct().isEmpty());
	}
	
	@Test
	void readStructWithFields() {
		BinaryStruct original = new BinaryStruct(2);
		original.set(0, "first", 1);
		original.set(1, "second", 2);
		
		BinaryStruct decoded = roundTrip(original).getAsBinaryStruct();
		
		assertEquals(2, decoded.size());
		assertEquals(1, decoded.getAsInteger(0));
		assertEquals(2, decoded.getAsInteger(1));
		assertFalse(decoded.hasNames());
	}
	
	@Test
	void readEmptyMap() {
		BinaryElement element = read((byte) 0x0D, (byte) 0x00);
		
		assertInstanceOf(BinaryMap.class, element);
		assertTrue(element.getAsBinaryMap().isEmpty());
	}
	
	@Test
	void readMapWithEntries() {
		BinaryMap original = new BinaryMap();
		original.add("a", 1);
		original.add("b", "text");
		
		BinaryMap decoded = roundTrip(original).getAsBinaryMap();
		
		assertEquals(2, decoded.size());
		assertEquals(1, decoded.getAsInteger("a"));
		assertEquals("text", decoded.getAsString("b"));
	}
	
	@Test
	void readCompactedListsYieldsPlainElements() {
		BinaryArray bytes = new BinaryArray(new BinaryPrimitive((byte) 1), new BinaryPrimitive((byte) 2));
		BinaryArray flags = booleans(true, false, true);
		BinaryArray integers = new BinaryArray(new BinaryPrimitive(1000), new BinaryPrimitive(2000));
		BinaryArray strings = new BinaryArray(new BinaryPrimitive("a"), new BinaryPrimitive("bc"));
		
		assertEquals(bytes, roundTrip(bytes));
		assertEquals(flags, roundTrip(flags));
		assertEquals(integers, roundTrip(integers));
		assertEquals(strings, roundTrip(strings));
		assertEquals(BinaryType.BYTE, roundTrip(bytes).getAsBinaryArray().get(0).getType());
	}
	
	@Test
	void readBooleanListWithPartialByte() {
		BinaryArray original = booleans(true, false, false, true, true, false, true, false, true, true, false);
		
		BinaryArray decoded = roundTrip(original).getAsBinaryArray();
		
		assertEquals(11, decoded.size());
		assertEquals(original, decoded);
	}
	
	@Test
	void readBooleanListWithFullBytes() {
		BinaryArray original = booleans(
			true, false, false, true, true, false, true, false,
			false, true, true, false, false, false, true, true
		);
		
		BinaryArray decoded = roundTrip(original).getAsBinaryArray();
		
		assertEquals(16, decoded.size());
		assertEquals(original, decoded);
	}
	
	@Test
	void readCompactedListsWithZeroSize() {
		BinaryArray empty = new BinaryArray();
		
		assertEquals(empty, read((byte) 0x0E, (byte) 0x00));
		assertEquals(empty, read((byte) 0x0F, (byte) 0x00));
		assertEquals(empty, read((byte) 0x10, (byte) 0x06, (byte) 0x00));
		assertEquals(read((byte) 0x0B, (byte) 0x00), read((byte) 0x0E, (byte) 0x00));
	}
	
	@Test
	void readCompressedDocument() {
		BinaryArray original = new BinaryArray();
		for (int i = 0; i < 512; i++) {
			original.add("repeated");
		}
		
		byte[] data = BinaryWriter.toByteArray(original, BinaryConfig.COMPRESSED);
		
		assertEquals(BinaryConfig.FLAG_COMPRESSED, data[3]);
		assertTrue(data.length < BinaryWriter.toByteArray(original, HEADER_CONFIG).length);
		assertEquals(original, BinaryReader.fromByteArray(data, BinaryConfig.COMPRESSED));
	}
	
	@Test
	void readUncompressedDocumentWithCompressedConfig() {
		BinaryPrimitive original = new BinaryPrimitive("value");
		
		byte[] data = BinaryWriter.toByteArray(original, HEADER_CONFIG);
		
		assertEquals((byte) 0x00, data[3]);
		assertEquals(original, BinaryReader.fromByteArray(data, BinaryConfig.COMPRESSED));
	}
	
	@Test
	void readCompressedDocumentWithUncompressedConfig() {
		BinaryPrimitive original = new BinaryPrimitive("value");
		
		byte[] data = BinaryWriter.toByteArray(original, DEFLATE_CONFIG);
		
		assertEquals(BinaryConfig.FLAG_COMPRESSED, data[3]);
		assertEquals(original, BinaryReader.fromByteArray(data, HEADER_CONFIG));
		assertEquals(original, BinaryReader.fromByteArray(data, BinaryConfig.COMPRESSED));
	}
	
	@Test
	void readTypedListOfEachPrimitiveType() {
		BinaryArray shorts = new BinaryArray(new BinaryPrimitive((short) 1000), new BinaryPrimitive((short) -1000));
		BinaryArray integers = new BinaryArray(new BinaryPrimitive(1000), new BinaryPrimitive(-1000));
		BinaryArray longs = new BinaryArray(new BinaryPrimitive(1000L), new BinaryPrimitive(-1000L));
		BinaryArray floats = new BinaryArray(new BinaryPrimitive(1.5F), new BinaryPrimitive(-2.5F));
		BinaryArray doubles = new BinaryArray(new BinaryPrimitive(1.5), new BinaryPrimitive(-2.5));
		BinaryArray strings = new BinaryArray(new BinaryPrimitive("a"), new BinaryPrimitive("bc"));
		
		for (BinaryArray original : new BinaryArray[] { shorts, integers, longs, floats, doubles, strings }) {
			assertEquals(original, roundTrip(original), "Unexpected result for " + original);
		}
	}
	
	@Test
	void readTypedListOfBytes() {
		BinaryElement typed = read((byte) 0x10, (byte) 0x04, (byte) 0x02, (byte) 0x01, (byte) 0x02);
		
		assertEquals(read((byte) 0x0E, (byte) 0x02, (byte) 0x01, (byte) 0x02), typed);
		assertEquals(2, typed.getAsBinaryArray().size());
		assertEquals((byte) 1, typed.getAsBinaryArray().getAsByte(0));
		assertEquals((byte) 2, typed.getAsBinaryArray().getAsByte(1));
	}
	
	@Test
	void readBinaryAtExactlyMaxDepth() {
		BinaryArray nested = new BinaryArray(new BinaryArray());
		byte[] data = BinaryWriter.toByteArray(nested);
		BinaryConfig config = new BinaryConfig(false, 2, 65536, 1048576, StandardCharsets.UTF_8);
		
		assertDoesNotThrow(() -> BinaryReader.fromByteArray(data, config));
		assertEquals(nested, BinaryReader.fromByteArray(data, config));
	}
	
	@Test
	void readCollectionAtExactlyMaxSize() {
		BinaryArray array = new BinaryArray(new BinaryPrimitive(1), new BinaryPrimitive(2));
		byte[] data = BinaryWriter.toByteArray(array);
		BinaryConfig config = new BinaryConfig(false, 64, 2, 1048576, StandardCharsets.UTF_8);
		
		assertEquals(array, BinaryReader.fromByteArray(data, config));
	}
	
	@Test
	void readStringAtExactlyMaxLength() {
		byte[] data = BinaryWriter.toByteArray(new BinaryPrimitive("abc"));
		BinaryConfig config = new BinaryConfig(false, 64, 65536, 3, StandardCharsets.UTF_8);
		
		assertEquals("abc", BinaryReader.fromByteArray(data, config).getAsString());
	}
	
	@Test
	void readVarLongSingleByte() {
		byte[] data = BinaryWriter.toByteArray(new BinaryPrimitive(63));
		
		assertEquals(2, data.length);
		assertEquals(63, BinaryReader.fromByteArray(data).getAsInteger());
	}
	
	@Test
	void readVarLongMultiByte() {
		for (int value : new int[] { 64, 8192, 1 << 27 }) {
			assertEquals(value, roundTrip(new BinaryPrimitive(value)).getAsInteger());
		}
	}
	
	@Test
	void readVarLongMaximumLength() {
		assertEquals(Long.MIN_VALUE, roundTrip(new BinaryPrimitive(Long.MIN_VALUE)).getAsLong());
	}
	
	@Test
	void fromByteArrayWithDefaultConfig() {
		BinaryPrimitive original = new BinaryPrimitive("value");
		
		assertEquals(original, BinaryReader.fromByteArray(BinaryWriter.toByteArray(original)));
	}
	
	@Test
	void fromByteArrayWithCustomConfig() {
		BinaryPrimitive original = new BinaryPrimitive("value");
		byte[] data = BinaryWriter.toByteArray(original, HEADER_CONFIG);
		
		assertEquals(original, BinaryReader.fromByteArray(data, HEADER_CONFIG));
	}
	
	@Test
	void readStringWithCustomCharset() {
		BinaryConfig latin = new BinaryConfig(false, 64, 65536, 1048576, StandardCharsets.ISO_8859_1);
		byte[] data = BinaryWriter.toByteArray(new BinaryPrimitive("ä"), latin);
		
		assertEquals("ä", BinaryReader.fromByteArray(data, latin).getAsString());
		assertNotEquals("ä", BinaryReader.fromByteArray(data).getAsString());
	}
	
	@Test
	void readStringWithMultiByteCharacters() {
		assertEquals("日本語", roundTrip(new BinaryPrimitive("日本語")).getAsString());
	}
	
	@Test
	void readIgnoresTrailingBytes() {
		byte[] first = BinaryWriter.toByteArray(new BinaryPrimitive((byte) 1));
		byte[] second = BinaryWriter.toByteArray(new BinaryPrimitive((byte) 2));
		byte[] combined = new byte[first.length + second.length];
		System.arraycopy(first, 0, combined, 0, first.length);
		System.arraycopy(second, 0, combined, first.length, second.length);
		
		assertEquals((byte) 1, BinaryReader.fromByteArray(combined).getAsByte());
	}
	
	@Test
	void readMultipleElementsSequentially() {
		byte[] combined = { 0x04, 0x01, 0x04, 0x02 };
		
		try (BinaryReader reader = new BinaryReader(new InputProvider(combined))) {
			assertEquals((byte) 1, reader.readBinary().getAsByte());
			assertEquals((byte) 2, reader.readBinary().getAsByte());
		} catch (IOException e) {
			fail(e);
		}
	}
	
	@Test
	void closeReaderReleasesStream() {
		BinaryReader reader = new BinaryReader(new InputProvider(new byte[] { 0x00 }));
		
		assertDoesNotThrow(reader::close);
	}
	
	@Test
	void closeReaderTwice() {
		BinaryReader reader = new BinaryReader(new InputProvider(new byte[] { 0x00 }));
		
		assertDoesNotThrow(reader::close);
		assertDoesNotThrow(reader::close);
	}
	
	@Test
	void readerUsableInTryWithResources() {
		byte[] data = BinaryWriter.toByteArray(new BinaryPrimitive("value"));
		
		assertDoesNotThrow(() -> {
			try (BinaryReader reader = new BinaryReader(new InputProvider(data))) {
				assertEquals("value", reader.readBinary().getAsString());
			}
		});
	}
	
	@Test
	void readNestedContainers() {
		BinaryStruct struct = new BinaryStruct(1);
		struct.set(0, 7);
		BinaryArray array = new BinaryArray(struct);
		BinaryMap original = new BinaryMap();
		original.add("a", array);
		
		assertEquals(original, roundTrip(original));
	}
	
	@Test
	void readDeeplyNestedStructure() {
		BinaryArray current = new BinaryArray();
		for (int i = 0; i < 20; i++) {
			current = new BinaryArray(current);
		}
		BinaryArray nested = current;
		
		assertEquals(nested, roundTrip(nested));
	}
	
	@Test
	void readStructWithMixedPresentAndAbsentFields() {
		BinaryStruct original = new BinaryStruct(4);
		original.set(0, 1);
		original.set(2, 3);
		
		BinaryStruct decoded = roundTrip(original).getAsBinaryStruct();
		
		assertTrue(decoded.has(0));
		assertFalse(decoded.has(1));
		assertTrue(decoded.has(2));
		assertFalse(decoded.has(3));
		assertEquals(2, decoded.presentFields());
	}
	
	@Test
	void readAllPrimitiveTypesFromOneList() {
		BinaryArray original = new BinaryArray(
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
		
		BinaryArray decoded = roundTrip(original).getAsBinaryArray();
		
		assertEquals(BinaryType.NULL, decoded.get(0).getType());
		assertEquals(BinaryType.BOOLEAN, decoded.get(1).getType());
		assertEquals(BinaryType.BYTE, decoded.get(2).getType());
		assertEquals(BinaryType.SHORT, decoded.get(3).getType());
		assertEquals(BinaryType.INTEGER, decoded.get(4).getType());
		assertEquals(BinaryType.LONG, decoded.get(5).getType());
		assertEquals(BinaryType.FLOAT, decoded.get(6).getType());
		assertEquals(BinaryType.DOUBLE, decoded.get(7).getType());
		assertEquals(BinaryType.STRING, decoded.get(8).getType());
		assertEquals(original, decoded);
	}
	
	@Test
	void roundTripAllTypesWithHeader() {
		BinaryArray original = new BinaryArray(
			BinaryNull.INSTANCE,
			new BinaryPrimitive(true),
			new BinaryPrimitive((byte) 1),
			new BinaryPrimitive("text")
		);
		
		byte[] data = BinaryWriter.toByteArray(original, HEADER_CONFIG);
		
		assertEquals(original, BinaryReader.fromByteArray(data, HEADER_CONFIG));
	}
	
	@Test
	void readFromFile() throws Exception {
		BinaryMap original = new BinaryMap();
		original.add("value", 42);
		
		try (BinaryWriter writer = new BinaryWriter(new OutputProvider(TEST_FILE))) {
			writer.writeBinary(original);
		}
		
		try (BinaryReader reader = new BinaryReader(new InputProvider(TEST_FILE))) {
			assertEquals(original, reader.readBinary());
		}
	}
	
	@Test
	void readCompressedDocumentFollowedByMoreData() {
		BinaryPrimitive original = new BinaryPrimitive("value");
		byte[] document = BinaryWriter.toByteArray(original, DEFLATE_CONFIG);
		byte[] combined = Arrays.copyOf(document, document.length + 2);
		combined[document.length] = 0x7F;
		combined[document.length + 1] = 0x7F;
		
		try (BinaryReader reader = new BinaryReader(new InputProvider(combined), DEFLATE_CONFIG)) {
			assertEquals(original, reader.readBinary());
		} catch (IOException e) {
			fail(e);
		}
		
		assertEquals(original, BinaryReader.fromByteArray(combined, DEFLATE_CONFIG));
	}
	
	@Test
	void readCompressedDocumentWithNestedCompactedLists() {
		BinaryStruct original = new BinaryStruct(3);
		original.set(0, new BinaryArray(new BinaryPrimitive((byte) 1), new BinaryPrimitive((byte) 2)));
		original.set(1, booleans(true, false, true, true, false, true, true, false, true));
		original.set(2, new BinaryArray(new BinaryPrimitive(1000), new BinaryPrimitive(2000)));
		
		byte[] data = BinaryWriter.toByteArray(original, BinaryConfig.COMPRESSED);
		
		assertEquals(original, BinaryReader.fromByteArray(data, BinaryConfig.COMPRESSED));
	}
	
	@Test
	void readCompressedDocumentLargerThanInflateBuffer() {
		BinaryArray original = new BinaryArray();
		for (int i = 0; i < 2048; i++) {
			original.add("a value which is repeated many times");
		}
		
		byte[] data = BinaryWriter.toByteArray(original, BinaryConfig.COMPRESSED);
		
		assertEquals(BinaryConfig.FLAG_COMPRESSED, data[3]);
		assertEquals(original, BinaryReader.fromByteArray(data, BinaryConfig.COMPRESSED));
	}
	
	@Test
	@Timeout(5)
	void malformedInputNeverAllocatesUnbounded() {
		byte[] data = { 0x0B, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0x7F };
		
		assertThrows(BinarySyntaxException.class, () -> BinaryReader.fromByteArray(data));
	}
}
