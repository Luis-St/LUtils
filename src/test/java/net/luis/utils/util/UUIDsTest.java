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

package net.luis.utils.util;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link UUIDs}.<br>
 *
 * @author Luis-St
 */
class UUIDsTest {
	
	private static final byte[] BIG_ENDIAN_BYTES = {
		0x00, 0x11, 0x22, 0x33, 0x44, 0x55, 0x66, 0x77,
		(byte) 0x88, (byte) 0x99, (byte) 0xAA, (byte) 0xBB, (byte) 0xCC, (byte) 0xDD, (byte) 0xEE, (byte) 0xFF
	};
	
	@Test
	void v2WithNegativeLocalDomain() {
		assertThrows(IllegalArgumentException.class, () -> UUIDs.v2(-1, 0));
		assertThrows(IllegalArgumentException.class, () -> UUIDs.v2(Integer.MIN_VALUE, 0));
	}
	
	@Test
	void v2WithTooLargeLocalDomain() {
		assertThrows(IllegalArgumentException.class, () -> UUIDs.v2(256, 0));
		assertThrows(IllegalArgumentException.class, () -> UUIDs.v2(Integer.MAX_VALUE, 0));
	}
	
	@Test
	void v3WithNullNamespace() {
		assertThrows(NullPointerException.class, () -> UUIDs.v3(null, "name"));
	}
	
	@Test
	void v3WithNullName() {
		assertThrows(NullPointerException.class, () -> UUIDs.v3(UUIDs.NAMESPACE_DNS, (String) null));
	}
	
	@Test
	void v3WithNullNamespaceAndByteName() {
		assertThrows(NullPointerException.class, () -> UUIDs.v3(null, new byte[0]));
	}
	
	@Test
	void v3WithNullByteName() {
		assertThrows(NullPointerException.class, () -> UUIDs.v3(UUIDs.NAMESPACE_DNS, (byte[]) null));
	}
	
	@Test
	void v5WithNullNamespace() {
		assertThrows(NullPointerException.class, () -> UUIDs.v5(null, "name"));
	}
	
	@Test
	void v5WithNullName() {
		assertThrows(NullPointerException.class, () -> UUIDs.v5(UUIDs.NAMESPACE_DNS, (String) null));
	}
	
	@Test
	void v5WithNullNamespaceAndByteName() {
		assertThrows(NullPointerException.class, () -> UUIDs.v5(null, new byte[0]));
	}
	
	@Test
	void v5WithNullByteName() {
		assertThrows(NullPointerException.class, () -> UUIDs.v5(UUIDs.NAMESPACE_DNS, (byte[]) null));
	}
	
	@Test
	void v8WithNullByteArray() {
		assertThrows(NullPointerException.class, () -> UUIDs.v8(null));
	}
	
	@Test
	void v8WithTooShortByteArray() {
		assertThrows(IllegalArgumentException.class, () -> UUIDs.v8(new byte[0]));
		assertThrows(IllegalArgumentException.class, () -> UUIDs.v8(new byte[15]));
	}
	
	@Test
	void v8WithTooLongByteArray() {
		assertThrows(IllegalArgumentException.class, () -> UUIDs.v8(new byte[17]));
	}
	
	@Test
	void unixMillisWithNullUuid() {
		assertThrows(NullPointerException.class, () -> UUIDs.unixMillis(null));
	}
	
	@Test
	void unixMillisWithNonTimeBasedVersion() {
		assertThrows(IllegalArgumentException.class, () -> UUIDs.unixMillis(UUIDs.v3(UUIDs.NAMESPACE_DNS, "a")));
		assertThrows(IllegalArgumentException.class, () -> UUIDs.unixMillis(UUIDs.v4()));
		assertThrows(IllegalArgumentException.class, () -> UUIDs.unixMillis(UUIDs.v5(UUIDs.NAMESPACE_DNS, "a")));
		assertThrows(IllegalArgumentException.class, () -> UUIDs.unixMillis(UUIDs.v8(0L, 0L)));
	}
	
	@Test
	void unixMillisWithNilAndMaxUuid() {
		assertEquals(0, UUIDs.NIL.version());
		assertEquals(15, UUIDs.MAX.version());
		assertThrows(IllegalArgumentException.class, () -> UUIDs.unixMillis(UUIDs.NIL));
		assertThrows(IllegalArgumentException.class, () -> UUIDs.unixMillis(UUIDs.MAX));
	}
	
	@Test
	void toBytesWithNullUuid() {
		assertThrows(NullPointerException.class, () -> UUIDs.toBytes(null));
	}
	
	@Test
	void fromBytesWithNullByteArray() {
		assertThrows(NullPointerException.class, () -> UUIDs.fromBytes(null));
	}
	
	@Test
	void fromBytesWithTooShortByteArray() {
		assertThrows(IllegalArgumentException.class, () -> UUIDs.fromBytes(new byte[0]));
		assertThrows(IllegalArgumentException.class, () -> UUIDs.fromBytes(new byte[15]));
	}
	
	@Test
	void fromBytesWithTooLongByteArray() {
		assertThrows(IllegalArgumentException.class, () -> UUIDs.fromBytes(new byte[17]));
	}
	
	@Test
	void unsignedComparatorWithNullFirst() {
		assertThrows(NullPointerException.class, () -> UUIDs.UNSIGNED.compare(null, UUIDs.NIL));
	}
	
	@Test
	void unsignedComparatorWithNullSecond() {
		assertThrows(NullPointerException.class, () -> UUIDs.UNSIGNED.compare(UUIDs.NIL, null));
	}
	
	@Test
	void timeOrderedComparatorWithNullUuid() {
		UUID uuid = UUIDs.v7();
		assertThrows(NullPointerException.class, () -> UUIDs.TIME_ORDERED.compare(null, uuid));
		assertThrows(NullPointerException.class, () -> UUIDs.TIME_ORDERED.compare(uuid, null));
	}
	
	@Test
	void timeOrderedComparatorWithNonTimeBasedVersion() {
		UUID first = UUIDs.v4();
		UUID second = UUIDs.v4();
		assertThrows(IllegalArgumentException.class, () -> UUIDs.TIME_ORDERED.compare(first, second));
	}
	
	@Test
	void v2WithBoundaryLocalDomains() {
		UUID low = assertDoesNotThrow(() -> UUIDs.v2(0, 1));
		UUID high = assertDoesNotThrow(() -> UUIDs.v2(255, 1));
		assertEquals(0, (low.getLeastSignificantBits() >>> 48) & 0xFF);
		assertEquals(255, (high.getLeastSignificantBits() >>> 48) & 0xFF);
		assertEquals(2, low.version());
		assertEquals(2, high.version());
	}
	
	@Test
	void v8WithExactByteArrayLength() {
		UUID uuid = assertDoesNotThrow(() -> UUIDs.v8(new byte[16]));
		assertEquals(8, uuid.version());
		assertEquals(2, uuid.variant());
	}
	
	@Test
	void fromBytesWithExactByteArrayLength() {
		UUID uuid = assertDoesNotThrow(() -> UUIDs.fromBytes(new byte[16]));
		assertEquals(UUIDs.NIL, uuid);
		assertEquals(0, uuid.version());
	}
	
	@Test
	void unixMillisFromVersionOne() {
		long now = System.currentTimeMillis();
		long timestamp = UUIDs.unixMillis(UUIDs.v1());
		assertTrue(Math.abs(timestamp - now) < 1000L, "expected " + timestamp + " within 1000ms of " + now);
	}
	
	@Test
	void unixMillisFromVersionTwo() {
		long now = System.currentTimeMillis();
		long timestamp = UUIDs.unixMillis(UUIDs.v2(0, 0));
		assertTrue(timestamp <= now, "expected " + timestamp + " to not exceed " + now);
		assertTrue(now - timestamp < 429496L, "expected " + timestamp + " within the v2 granularity of " + now);
	}
	
	@Test
	void unixMillisFromVersionTwoIgnoresLocalIdentifier() {
		long first = UUIDs.unixMillis(UUIDs.v2(0, 0));
		long second = UUIDs.unixMillis(UUIDs.v2(0, -1));
		long third = UUIDs.unixMillis(UUIDs.v2(0, Integer.MAX_VALUE));
		assertEquals(first, second);
		assertEquals(first, third);
	}
	
	@Test
	void unixMillisFromVersionSix() {
		long now = System.currentTimeMillis();
		long timestamp = UUIDs.unixMillis(UUIDs.v6());
		assertTrue(Math.abs(timestamp - now) < 1000L, "expected " + timestamp + " within 1000ms of " + now);
	}
	
	@Test
	void unixMillisFromVersionSeven() {
		long now = System.currentTimeMillis();
		long timestamp = UUIDs.unixMillis(UUIDs.v7());
		assertTrue(Math.abs(timestamp - now) < 1000L, "expected " + timestamp + " within 1000ms of " + now);
	}
	
	@Test
	void unsignedComparatorOrdersByMostSignificantBits() {
		UUID first = new UUID(1L, 0L);
		UUID second = new UUID(2L, 0L);
		assertTrue(UUIDs.UNSIGNED.compare(first, second) < 0);
		assertTrue(UUIDs.UNSIGNED.compare(second, first) > 0);
	}
	
	@Test
	void unsignedComparatorOrdersByLeastSignificantBits() {
		UUID first = new UUID(1L, 1L);
		UUID second = new UUID(1L, 2L);
		assertTrue(UUIDs.UNSIGNED.compare(first, second) < 0);
		assertTrue(UUIDs.UNSIGNED.compare(second, first) > 0);
	}
	
	@Test
	void unsignedComparatorWithEqualUuids() {
		UUID first = new UUID(1L, 1L);
		UUID second = new UUID(1L, 1L);
		assertEquals(0, UUIDs.UNSIGNED.compare(first, second));
		assertEquals(0, UUIDs.UNSIGNED.compare(first, first));
	}
	
	@Test
	void unsignedComparatorTreatsBitsAsUnsigned() {
		UUID first = new UUID(-1L, 0L);
		UUID second = new UUID(1L, 0L);
		assertTrue(UUIDs.UNSIGNED.compare(first, second) > 0);
		assertTrue(first.compareTo(second) < 0);
	}
	
	@Test
	void unsignedComparatorTreatsLeastSignificantBitsAsUnsigned() {
		UUID first = new UUID(1L, -1L);
		UUID second = new UUID(1L, 1L);
		assertTrue(UUIDs.UNSIGNED.compare(first, second) > 0);
		assertTrue(UUIDs.UNSIGNED.compare(second, first) < 0);
	}
	
	@Test
	void unixMillisMatchesExactTimestampBits() {
		assertEquals(1700000000000L, UUIDs.unixMillis(UUID.fromString("04afc000-833b-11ee-8000-000000000000")));
		assertEquals(1700000000000L, UUIDs.unixMillis(UUID.fromString("1ee833b0-4afc-6000-8000-000000000000")));
		assertEquals(1700000000000L, UUIDs.unixMillis(UUID.fromString("018bcfe5-6800-7000-8000-000000000000")));
		assertEquals(1699999992137L, UUIDs.unixMillis(UUID.fromString("deadbeef-833b-21ee-8000-000000000000")));
	}
	
	@Test
	void unixMillisWithMaximumVersionSevenTimestamp() {
		UUID uuid = UUID.fromString("ffffffff-ffff-7000-8000-000000000000");
		assertTrue(uuid.getMostSignificantBits() < 0, "expected the most significant bit to be set");
		assertEquals(281474976710655L, UUIDs.unixMillis(uuid));
	}
	
	@Test
	void timeOrderedComparatorOrdersByTimestamp() throws InterruptedException {
		UUID first = UUIDs.v7();
		Thread.sleep(5L);
		UUID second = UUIDs.v7();
		assertTrue(UUIDs.TIME_ORDERED.compare(first, second) < 0);
		assertTrue(UUIDs.TIME_ORDERED.compare(second, first) > 0);
	}
	
	@Test
	void timeOrderedComparatorFallsBackToUnsigned() {
		long msb = UUIDs.v7().getMostSignificantBits();
		UUID first = new UUID(msb, 0x8000000000000001L);
		UUID second = new UUID(msb, 0x8000000000000002L);
		assertEquals(UUIDs.unixMillis(first), UUIDs.unixMillis(second));
		assertTrue(UUIDs.TIME_ORDERED.compare(first, second) < 0);
		assertEquals(UUIDs.UNSIGNED.compare(first, second) < 0, UUIDs.TIME_ORDERED.compare(first, second) < 0);
	}
	
	@Test
	void v7IncrementsCounterWithinSameMillisecond() {
		List<UUID> created = new ArrayList<>();
		for (int i = 0; i < 1000; i++) {
			created.add(UUIDs.v7());
		}
		
		List<UUID> sorted = new ArrayList<>(created);
		sorted.sort(UUIDs.UNSIGNED);
		assertEquals(1000, new HashSet<>(created).size());
		assertEquals(created, sorted);
		assertTrue(new HashSet<>(created.stream().map(UUIDs::unixMillis).toList()).size() < 1000, "expected at least two uuids within the same millisecond");
	}
	
	@Test
	void v1CreatesTimeBasedUuid() {
		long now = System.currentTimeMillis();
		UUID uuid = UUIDs.v1();
		assertEquals(1, uuid.version());
		assertEquals(2, uuid.variant());
		assertTrue(Math.abs(UUIDs.unixMillis(uuid) - now) < 1000L);
	}
	
	@Test
	void v2CreatesDceSecurityUuid() {
		UUID uuid = UUIDs.v2(1, 4711);
		assertEquals(2, uuid.version());
		assertEquals(2, uuid.variant());
		assertEquals(4711L, uuid.getMostSignificantBits() >>> 32);
		assertEquals(1L, (uuid.getLeastSignificantBits() >>> 48) & 0xFF);
	}
	
	@Test
	void v3CreatesNameBasedUuid() {
		UUID uuid = UUIDs.v3(UUIDs.NAMESPACE_DNS, "example.com");
		assertEquals(3, uuid.version());
		assertEquals(2, uuid.variant());
	}
	
	@Test
	void v4CreatesRandomUuid() {
		UUID uuid = UUIDs.v4();
		assertEquals(4, uuid.version());
		assertEquals(2, uuid.variant());
	}
	
	@Test
	void v5CreatesNameBasedUuid() {
		UUID uuid = UUIDs.v5(UUIDs.NAMESPACE_DNS, "example.com");
		assertEquals(5, uuid.version());
		assertEquals(2, uuid.variant());
	}
	
	@Test
	void v6CreatesTimeBasedUuid() {
		long now = System.currentTimeMillis();
		UUID uuid = UUIDs.v6();
		assertEquals(6, uuid.version());
		assertEquals(2, uuid.variant());
		assertTrue(Math.abs(UUIDs.unixMillis(uuid) - now) < 1000L);
	}
	
	@Test
	void v7CreatesTimeBasedUuid() {
		long now = System.currentTimeMillis();
		UUID uuid = UUIDs.v7();
		assertEquals(7, uuid.version());
		assertEquals(2, uuid.variant());
		assertTrue(Math.abs(UUIDs.unixMillis(uuid) - now) < 1000L);
	}
	
	@Test
	void v8CreatesCustomUuid() {
		UUID zero = UUIDs.v8(0L, 0L);
		UUID ones = UUIDs.v8(-1L, -1L);
		assertEquals(8, zero.version());
		assertEquals(2, zero.variant());
		assertEquals(8, ones.version());
		assertEquals(2, ones.variant());
	}
	
	@Test
	void v8PreservesPayloadBits() {
		UUID uuid = UUIDs.v8(0x0123456789ABCDEFL, 0x0123456789ABCDEFL);
		assertEquals(0x0123456789AB8DEFL, uuid.getMostSignificantBits());
		assertEquals(0x8123456789ABCDEFL, uuid.getLeastSignificantBits());
		assertEquals(8, uuid.version());
		assertEquals(2, uuid.variant());
	}
	
	@Test
	void v3MatchesReferenceVector() {
		assertEquals("9073926b-929f-31c2-abc9-fad77ae3e8eb", UUIDs.v3(UUIDs.NAMESPACE_DNS, "example.com").toString());
	}
	
	@Test
	void v5MatchesReferenceVector() {
		assertEquals("cfbff0d1-9375-5685-968c-48ce8b15ae17", UUIDs.v5(UUIDs.NAMESPACE_DNS, "example.com").toString());
	}
	
	@Test
	void v3WithEmptyName() {
		UUID fromString = assertDoesNotThrow(() -> UUIDs.v3(UUIDs.NAMESPACE_DNS, ""));
		UUID fromBytes = assertDoesNotThrow(() -> UUIDs.v3(UUIDs.NAMESPACE_DNS, new byte[0]));
		assertEquals(3, fromString.version());
		assertEquals(fromString, fromBytes);
	}
	
	@Test
	void v5WithEmptyName() {
		UUID fromString = assertDoesNotThrow(() -> UUIDs.v5(UUIDs.NAMESPACE_DNS, ""));
		UUID fromBytes = assertDoesNotThrow(() -> UUIDs.v5(UUIDs.NAMESPACE_DNS, new byte[0]));
		assertEquals(5, fromString.version());
		assertEquals(fromString, fromBytes);
	}
	
	@Test
	void toBytesProducesBigEndianBytes() {
		byte[] data = UUIDs.toBytes(new UUID(0x0011223344556677L, 0x8899AABBCCDDEEFFL));
		assertEquals(16, data.length);
		assertArrayEquals(BIG_ENDIAN_BYTES, data);
		assertEquals(0x00, data[0]);
		assertEquals(0x77, data[7]);
		assertEquals((byte) 0x88, data[8]);
		assertEquals((byte) 0xFF, data[15]);
	}
	
	@Test
	void fromBytesReadsBigEndianBytes() {
		UUID uuid = UUIDs.fromBytes(BIG_ENDIAN_BYTES);
		assertEquals(0x0011223344556677L, uuid.getMostSignificantBits());
		assertEquals(0x8899AABBCCDDEEFFL, uuid.getLeastSignificantBits());
	}
	
	@Test
	void nilAndMaxConstants() {
		assertEquals(0L, UUIDs.NIL.getMostSignificantBits());
		assertEquals(0L, UUIDs.NIL.getLeastSignificantBits());
		assertEquals(0, UUIDs.NIL.version());
		assertEquals(-1L, UUIDs.MAX.getMostSignificantBits());
		assertEquals(-1L, UUIDs.MAX.getLeastSignificantBits());
		assertEquals(15, UUIDs.MAX.version());
	}
	
	@Test
	void namespaceConstantsMatchRfcValues() {
		assertEquals("6ba7b810-9dad-11d1-80b4-00c04fd430c8", UUIDs.NAMESPACE_DNS.toString());
		assertEquals("6ba7b811-9dad-11d1-80b4-00c04fd430c8", UUIDs.NAMESPACE_URL.toString());
		assertEquals("6ba7b812-9dad-11d1-80b4-00c04fd430c8", UUIDs.NAMESPACE_OID.toString());
		assertEquals("6ba7b814-9dad-11d1-80b4-00c04fd430c8", UUIDs.NAMESPACE_X500.toString());
		assertEquals(4, new HashSet<>(List.of(UUIDs.NAMESPACE_DNS, UUIDs.NAMESPACE_URL, UUIDs.NAMESPACE_OID, UUIDs.NAMESPACE_X500)).size());
	}
	
	@Test
	void v3AndV5DifferForSameInput() {
		UUID md5 = UUIDs.v3(UUIDs.NAMESPACE_DNS, "example.com");
		UUID sha1 = UUIDs.v5(UUIDs.NAMESPACE_DNS, "example.com");
		assertNotEquals(md5, sha1);
		assertEquals(3, md5.version());
		assertEquals(5, sha1.version());
	}
	
	@Test
	void nameBasedUuidsAreDeterministic() {
		assertEquals(UUIDs.v3(UUIDs.NAMESPACE_DNS, "a"), UUIDs.v3(UUIDs.NAMESPACE_DNS, "a"));
		assertEquals(UUIDs.v5(UUIDs.NAMESPACE_DNS, "a"), UUIDs.v5(UUIDs.NAMESPACE_DNS, "a"));
		assertNotEquals(UUIDs.v3(UUIDs.NAMESPACE_DNS, "a"), UUIDs.v3(UUIDs.NAMESPACE_URL, "a"));
		assertNotEquals(UUIDs.v5(UUIDs.NAMESPACE_DNS, "a"), UUIDs.v5(UUIDs.NAMESPACE_URL, "a"));
		assertNotEquals(UUIDs.v3(UUIDs.NAMESPACE_DNS, "a"), UUIDs.v3(UUIDs.NAMESPACE_DNS, "b"));
		assertNotEquals(UUIDs.v5(UUIDs.NAMESPACE_DNS, "a"), UUIDs.v5(UUIDs.NAMESPACE_DNS, "b"));
	}
	
	@Test
	void nameBasedStringAndByteOverloadsAgree() {
		String name = "näme-äöü";
		byte[] encoded = name.getBytes(StandardCharsets.UTF_8);
		assertEquals(UUIDs.v3(UUIDs.NAMESPACE_DNS, name), UUIDs.v3(UUIDs.NAMESPACE_DNS, encoded));
		assertEquals(UUIDs.v5(UUIDs.NAMESPACE_DNS, name), UUIDs.v5(UUIDs.NAMESPACE_DNS, encoded));
		assertNotEquals(UUIDs.v3(UUIDs.NAMESPACE_DNS, name), UUIDs.v3(UUIDs.NAMESPACE_DNS, name.getBytes(StandardCharsets.ISO_8859_1)));
	}
	
	@Test
	void byteArrayRoundTripConsistency() {
		List<UUID> uuids = List.of(
			UUIDs.v1(), UUIDs.v2(0, 1), UUIDs.v3(UUIDs.NAMESPACE_DNS, "a"), UUIDs.v4(),
			UUIDs.v5(UUIDs.NAMESPACE_DNS, "a"), UUIDs.v6(), UUIDs.v7(), UUIDs.v8(1L, 2L)
		);
		for (UUID uuid : uuids) {
			assertEquals(uuid, UUIDs.fromBytes(UUIDs.toBytes(uuid)));
		}
		assertEquals(UUIDs.NIL, UUIDs.fromBytes(UUIDs.toBytes(UUIDs.NIL)));
		assertEquals(UUIDs.MAX, UUIDs.fromBytes(UUIDs.toBytes(UUIDs.MAX)));
	}
	
	@Test
	void toBytesReturnsIndependentArray() {
		UUID uuid = UUIDs.v4();
		byte[] first = UUIDs.toBytes(uuid);
		byte[] second = UUIDs.toBytes(uuid);
		assertNotSame(first, second);
		assertArrayEquals(first, second);
		
		first[0] = (byte) ~first[0];
		assertFalse(Arrays.equals(first, second));
		assertArrayEquals(second, UUIDs.toBytes(uuid));
	}
	
	@Test
	void fromBytesDiffersFromV8ForSameBytes() {
		byte[] data = new byte[16];
		UUID plain = UUIDs.fromBytes(data);
		UUID versioned = UUIDs.v8(data);
		assertEquals(UUIDs.NIL, plain);
		assertNotEquals(plain, versioned);
		assertEquals(8, versioned.version());
		assertEquals(0, plain.version());
	}
	
	@Test
	void v6UuidsSortByCreationTime() {
		List<UUID> created = new ArrayList<>();
		for (int i = 0; i < 500; i++) {
			created.add(UUIDs.v6());
		}
		
		List<UUID> shuffled = new ArrayList<>(created);
		Collections.shuffle(shuffled, new Random(42L));
		shuffled.sort(UUIDs.UNSIGNED);
		assertEquals(500, new HashSet<>(created).size());
		assertEquals(created, shuffled);
	}
	
	@Test
	void v7UuidsSortByCreationTime() {
		List<UUID> created = new ArrayList<>();
		for (int i = 0; i < 500; i++) {
			created.add(UUIDs.v7());
		}
		
		List<UUID> shuffled = new ArrayList<>(created);
		Collections.shuffle(shuffled, new Random(42L));
		shuffled.sort(UUIDs.UNSIGNED);
		assertEquals(500, new HashSet<>(created).size());
		assertEquals(created, shuffled);
		
		List<UUID> timeOrdered = new ArrayList<>(created);
		Collections.shuffle(timeOrdered, new Random(7L));
		timeOrdered.sort(UUIDs.TIME_ORDERED);
		assertEquals(created, timeOrdered);
	}
	
	@Test
	void v7TimestampNeverAheadOfSystemClock() {
		List<UUID> created = new ArrayList<>();
		for (int i = 0; i < 50_000; i++) {
			created.add(UUIDs.v7());
		}
		long after = System.currentTimeMillis();
		
		assertEquals(50_000, new HashSet<>(created).size());
		for (UUID uuid : created) {
			assertTrue(UUIDs.unixMillis(uuid) <= after, "timestamp " + UUIDs.unixMillis(uuid) + " is ahead of the clock " + after);
		}
	}
	
	@Test
	void v1UuidsAreDistinctAndMonotonic() {
		List<UUID> created = new ArrayList<>();
		for (int i = 0; i < 500; i++) {
			created.add(UUIDs.v1());
		}
		
		assertEquals(500, new HashSet<>(created).size());
		for (int i = 1; i < created.size(); i++) {
			assertTrue(UUIDs.unixMillis(created.get(i)) >= UUIDs.unixMillis(created.get(i - 1)), "timestamp decreased at index " + i);
		}
	}
	
	@Test
	void v4UuidsAreDistinct() {
		Set<UUID> created = new HashSet<>();
		for (int i = 0; i < 1000; i++) {
			UUID uuid = UUIDs.v4();
			assertEquals(4, uuid.version());
			assertEquals(2, uuid.variant());
			created.add(uuid);
		}
		assertEquals(1000, created.size());
	}
	
	@Test
	void v2WithFullIdentifierRange() {
		int[] identifiers = { 0, 1, Integer.MAX_VALUE, -1 };
		for (int identifier : identifiers) {
			UUID uuid = UUIDs.v2(3, identifier);
			assertEquals(Integer.toUnsignedLong(identifier), uuid.getMostSignificantBits() >>> 32);
			assertEquals(2, uuid.version());
			assertEquals(3L, (uuid.getLeastSignificantBits() >>> 48) & 0xFF);
		}
	}
	
	@Test
	void timeBasedVersionsShareNodeAndClockSequence() {
		long first = UUIDs.v1().getLeastSignificantBits();
		long second = UUIDs.v1().getLeastSignificantBits();
		long sixth = UUIDs.v6().getLeastSignificantBits();
		assertEquals(first, second);
		assertEquals(first, sixth);
		assertNotEquals(0L, first & 0x0000_0100_0000_0000L);
	}
	
	@Test
	void v8ByteAndLongOverloadsAgree() {
		UUID fromBytes = UUIDs.v8(BIG_ENDIAN_BYTES);
		UUID fromLongs = UUIDs.v8(0x0011223344556677L, 0x8899AABBCCDDEEFFL);
		assertEquals(fromLongs, fromBytes);
		assertEquals(8, fromBytes.version());
		assertEquals(2, fromBytes.variant());
	}
	
	@Test
	void allVersionsCarryRfcVariant() {
		List<UUID> uuids = List.of(
			UUIDs.v1(), UUIDs.v2(0, 1), UUIDs.v3(UUIDs.NAMESPACE_DNS, "a"), UUIDs.v4(),
			UUIDs.v5(UUIDs.NAMESPACE_DNS, "a"), UUIDs.v6(), UUIDs.v7(), UUIDs.v8(1L, 2L)
		);
		for (int i = 0; i < uuids.size(); i++) {
			assertEquals(i + 1, uuids.get(i).version(), "wrong version at index " + i);
			assertEquals(2, uuids.get(i).variant(), "wrong variant at index " + i);
		}
	}
}
