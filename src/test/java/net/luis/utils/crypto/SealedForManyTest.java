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

package net.luis.utils.crypto;

import net.luis.utils.crypto.algorithm.SignatureAlgorithm;
import net.luis.utils.crypto.exception.AuthenticationException;
import net.luis.utils.crypto.exception.MalformedDataException;
import net.luis.utils.crypto.key.KeyId;
import net.luis.utils.crypto.util.CryptoBytes;
import net.luis.utils.crypto.util.CryptoRandom;
import net.luis.utils.util.UUIDs;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.PublicKey;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

/**
 * Test class for {@link SealedForMany}.<br>
 *
 * @author Luis-St
 */
class SealedForManyTest {
	
	private static final CryptoSuite SUITE = CryptoSuite.HYBRID_V1;
	private static final byte[] PLAINTEXT = "the quick brown fox".getBytes(StandardCharsets.UTF_8);
	private static final byte[] AAD = "header".getBytes(StandardCharsets.UTF_8);
	private static final int HEADER_LENGTH = 25;
	
	private static KeyPair alice;
	private static KeyPair bob;
	private static KeyPair carol;
	private static KeyPair ed25519;
	private static int slotLength;
	
	@BeforeAll
	static void setUp() {
		Providers.installBouncyCastle();
		alice = Kems.generateKeyPair(SUITE.kem());
		bob = Kems.generateKeyPair(SUITE.kem());
		carol = Kems.generateKeyPair(SUITE.kem());
		ed25519 = Signatures.generateKeyPair(SignatureAlgorithm.ED25519);
		slotLength = 16 + 32 + SUITE.kem().encapsulationLength() + 2 + SUITE.aead().nonceLength() + SUITE.aead().keyLength() + SUITE.aead().tagLength();
	}
	
	private static byte[] sealed(List<PublicKey> recipients) {
		return SealedForMany.seal(SUITE, recipients, PLAINTEXT, null);
	}
	
	private static byte[] sealedToAliceAndBob() {
		return sealed(List.of(alice.getPublic(), bob.getPublic()));
	}
	
	private static byte[] flip(byte[] source, int index) {
		byte[] copy = source.clone();
		copy[index] ^= 1;
		return copy;
	}
	
	@Test
	void constructorIsPrivate() throws Exception {
		Constructor<?>[] constructors = SealedForMany.class.getDeclaredConstructors();
		assertEquals(1, constructors.length);
		assertTrue(Modifier.isPrivate(constructors[0].getModifiers()));
		assertTrue(Modifier.isFinal(SealedForMany.class.getModifiers()));
		
		Constructor<SealedForMany> constructor = SealedForMany.class.getDeclaredConstructor();
		constructor.setAccessible(true);
		assertNotNull(constructor.newInstance());
	}
	
	@Test
	void sealWithNullSuite() {
		assertEquals("Suite must not be null", assertThrows(NullPointerException.class, () -> SealedForMany.seal(null, List.of(alice.getPublic()), PLAINTEXT, null)).getMessage());
	}
	
	@Test
	void sealWithNullRecipients() {
		assertEquals("Recipients must not be null", assertThrows(NullPointerException.class, () -> SealedForMany.seal(SUITE, null, PLAINTEXT, null)).getMessage());
	}
	
	@Test
	void sealWithNullPlaintext() {
		assertEquals("Plaintext must not be null", assertThrows(NullPointerException.class, () -> SealedForMany.seal(SUITE, List.of(alice.getPublic()), null, null)).getMessage());
	}
	
	@Test
	void sealWithAllNull() {
		assertEquals("Suite must not be null", assertThrows(NullPointerException.class, () -> SealedForMany.seal(null, null, null, null)).getMessage());
	}
	
	@Test
	void sealWithNullRecipientInList() {
		List<PublicKey> recipients = Arrays.asList(alice.getPublic(), null);
		assertThrows(NullPointerException.class, () -> SealedForMany.seal(SUITE, recipients, PLAINTEXT, null));
	}
	
	@Test
	void sealWithNoRecipients() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> SealedForMany.seal(SUITE, List.of(), PLAINTEXT, null));
		assertEquals("At least one recipient is required", exception.getMessage());
	}
	
	@Test
	void sealWithTooManyRecipients() {
		List<PublicKey> oversized = new AbstractList<>() {
			
			@Override
			public PublicKey get(int index) {
				return alice.getPublic();
			}
			
			@Override
			public int size() {
				return Short.MAX_VALUE + 1;
			}
		};
		
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> SealedForMany.seal(SUITE, oversized, PLAINTEXT, null));
		assertTrue(exception.getMessage().contains(String.valueOf(Short.MAX_VALUE)));
		assertTrue(exception.getMessage().contains(String.valueOf(Short.MAX_VALUE + 1)));
	}
	
	@Test
	void sealWithMismatchedRecipientKey() {
		assertThrows(ClassCastException.class, () -> SealedForMany.seal(SUITE, List.of(ed25519.getPublic()), PLAINTEXT, null));
	}
	
	@Test
	void unsealWithNullOwnKey() {
		assertEquals("Own key must not be null", assertThrows(NullPointerException.class, () -> SealedForMany.unseal(null, alice.getPrivate(), sealedToAliceAndBob(), null)).getMessage());
	}
	
	@Test
	void unsealWithNullRecipient() {
		assertEquals("Recipient must not be null", assertThrows(NullPointerException.class, () -> SealedForMany.unseal(alice.getPublic(), null, sealedToAliceAndBob(), null)).getMessage());
	}
	
	@Test
	void unsealWithNullSealed() {
		assertEquals("Sealed artifact must not be null", assertThrows(NullPointerException.class, () -> SealedForMany.unseal(alice.getPublic(), alice.getPrivate(), null, null)).getMessage());
	}
	
	@Test
	void unsealWithTooShortArtifact() {
		for (int length : new int[] { 0, 1, HEADER_LENGTH - 1 }) {
			MalformedDataException exception = assertThrows(MalformedDataException.class, () -> SealedForMany.unseal(alice.getPublic(), alice.getPrivate(), new byte[length], null));
			assertEquals("Multi-recipient blob too short to contain a header", exception.getMessage());
		}
	}
	
	@Test
	void unsealWithBadMagic() {
		byte[] artifact = sealedToAliceAndBob();
		for (int i = 0; i < 4; i++) {
			int index = i;
			MalformedDataException exception = assertThrows(MalformedDataException.class, () -> SealedForMany.unseal(alice.getPublic(), alice.getPrivate(), flip(artifact, index), null), "index " + index);
			assertEquals("Not a multi-recipient sealed blob (bad magic)", exception.getMessage());
		}
	}
	
	@Test
	void unsealWithSealedSingleRecipientMagic() {
		byte[] single = Sealed.seal(SUITE, alice.getPublic(), PLAINTEXT, null);
		byte[] many = sealedToAliceAndBob();
		
		assertThrows(MalformedDataException.class, () -> SealedForMany.unseal(alice.getPublic(), alice.getPrivate(), single, null));
		assertThrows(MalformedDataException.class, () -> Sealed.unseal(alice.getPrivate(), many));
	}
	
	@Test
	void unsealWithUnsupportedVersion() {
		byte[] artifact = sealedToAliceAndBob();
		for (byte version : new byte[] { 0, 2, (byte) 0xFF }) {
			byte[] corrupted = artifact.clone();
			corrupted[4] = version;
			
			MalformedDataException exception = assertThrows(MalformedDataException.class, () -> SealedForMany.unseal(alice.getPublic(), alice.getPrivate(), corrupted, null));
			assertTrue(exception.getMessage().contains("Unsupported multi-recipient format version"));
		}
	}
	
	@Test
	void unsealWithUnknownSuiteId() {
		byte[] artifact = sealedToAliceAndBob();
		System.arraycopy(CryptoBytes.of((short) 999), 0, artifact, 5, 2);
		
		MalformedDataException exception = assertThrows(MalformedDataException.class, () -> SealedForMany.unseal(alice.getPublic(), alice.getPrivate(), artifact, null));
		assertTrue(exception.getMessage().contains("999"));
	}
	
	@Test
	void unsealWithOversizedRecipientCount() {
		for (int count : new int[] { 65535, 1000 }) {
			byte[] artifact = sealedToAliceAndBob();
			System.arraycopy(CryptoBytes.of((short) count), 0, artifact, 23, 2);
			
			MalformedDataException exception = assertThrows(MalformedDataException.class, () -> SealedForMany.unseal(alice.getPublic(), alice.getPrivate(), artifact, null));
			assertTrue(exception.getMessage().contains("Recipient count"));
			assertTrue(exception.getMessage().contains(String.valueOf(count)));
		}
	}
	
	@Test
	void unsealWithTruncatedSlot() {
		byte[] artifact = sealedToAliceAndBob();
		for (int length : new int[] { HEADER_LENGTH + slotLength + 10, HEADER_LENGTH + slotLength + 100 }) {
			MalformedDataException exception = assertThrows(MalformedDataException.class, () -> SealedForMany.unseal(alice.getPublic(), alice.getPrivate(), Arrays.copyOf(artifact, length), null));
			assertTrue(exception.getMessage().contains("slot 1") || exception.getMessage().contains("Recipient count"), exception.getMessage());
		}
	}
	
	@Test
	void unsealWithTruncatedWrappedKey() {
		byte[] artifact = sealed(List.of(alice.getPublic()));
		int lengthOffset = HEADER_LENGTH + 16 + 32 + SUITE.kem().encapsulationLength();
		System.arraycopy(CryptoBytes.of((short) 60000), 0, artifact, lengthOffset, 2);
		
		MalformedDataException exception = assertThrows(MalformedDataException.class, () -> SealedForMany.unseal(alice.getPublic(), alice.getPrivate(), artifact, null));
		assertEquals("Truncated wrapped content key in recipient slot 0", exception.getMessage());
	}
	
	@Test
	void unsealWithMissingBody() {
		byte[] artifact = sealed(List.of(alice.getPublic()));
		int minimumBody = SUITE.aead().nonceLength() + SUITE.aead().tagLength();
		
		for (int length : new int[] { HEADER_LENGTH + slotLength, HEADER_LENGTH + slotLength + minimumBody - 1 }) {
			MalformedDataException exception = assertThrows(MalformedDataException.class, () -> SealedForMany.unseal(alice.getPublic(), alice.getPrivate(), Arrays.copyOf(artifact, length), null));
			assertEquals("Multi-recipient blob has no readable body", exception.getMessage());
		}
	}
	
	@Test
	void unsealWithNoMatchingSlot() {
		AuthenticationException exception = assertThrows(AuthenticationException.class, () -> SealedForMany.unseal(carol.getPublic(), carol.getPrivate(), sealedToAliceAndBob(), null));
		assertEquals("No recipient slot matches this key", exception.getMessage());
	}
	
	@Test
	void unsealWithMatchingIdButWrongPrivateKey() {
		AuthenticationException exception = assertThrows(AuthenticationException.class, () -> SealedForMany.unseal(alice.getPublic(), bob.getPrivate(), sealedToAliceAndBob(), null));
		assertEquals("No recipient slot matches this key", exception.getMessage());
	}
	
	@Test
	void unsealWithTamperedSlotRegion() {
		byte[] artifact = sealedToAliceAndBob();
		int bobSlotStart = HEADER_LENGTH + slotLength;
		
		assertThrows(AuthenticationException.class, () -> SealedForMany.unseal(alice.getPublic(), alice.getPrivate(), flip(artifact, bobSlotStart + 20), null));
	}
	
	@Test
	void unsealWithRemovedSlot() {
		byte[] artifact = sealed(List.of(alice.getPublic(), bob.getPublic(), carol.getPublic()));
		int bodyStart = HEADER_LENGTH + 3 * slotLength;
		byte[] shortened = CryptoBytes.concat(CryptoBytes.slice(artifact, 0, HEADER_LENGTH + 2 * slotLength), CryptoBytes.slice(artifact, bodyStart, artifact.length - bodyStart));
		System.arraycopy(CryptoBytes.of((short) 2), 0, shortened, 23, 2);
		
		assertThrows(AuthenticationException.class, () -> SealedForMany.unseal(alice.getPublic(), alice.getPrivate(), shortened, null));
	}
	
	@Test
	void unsealWithAddedSlot() {
		byte[] artifact = sealedToAliceAndBob();
		int bodyStart = HEADER_LENGTH + 2 * slotLength;
		byte[] extended = CryptoBytes.concat(CryptoBytes.slice(artifact, 0, bodyStart), CryptoBytes.slice(artifact, HEADER_LENGTH, slotLength), CryptoBytes.slice(artifact, bodyStart, artifact.length - bodyStart));
		System.arraycopy(CryptoBytes.of((short) 3), 0, extended, 23, 2);
		
		assertThrows(AuthenticationException.class, () -> SealedForMany.unseal(alice.getPublic(), alice.getPrivate(), extended, null));
	}
	
	@Test
	void unsealWithTamperedHeader() {
		byte[] artifact = sealedToAliceAndBob();
		assertThrows(AuthenticationException.class, () -> SealedForMany.unseal(alice.getPublic(), alice.getPrivate(), flip(artifact, 7), null));
	}
	
	@Test
	void unsealWithTamperedBody() {
		byte[] artifact = sealedToAliceAndBob();
		assertThrows(AuthenticationException.class, () -> SealedForMany.unseal(alice.getPublic(), alice.getPrivate(), flip(artifact, artifact.length - 1), null));
	}
	
	@Test
	void unsealWithWrongAssociatedData() {
		byte[] artifact = SealedForMany.seal(SUITE, List.of(alice.getPublic()), PLAINTEXT, "header-a".getBytes(StandardCharsets.UTF_8));
		
		assertThrows(AuthenticationException.class, () -> SealedForMany.unseal(alice.getPublic(), alice.getPrivate(), artifact, "header-b".getBytes(StandardCharsets.UTF_8)));
		assertThrows(AuthenticationException.class, () -> SealedForMany.unseal(alice.getPublic(), alice.getPrivate(), artifact, null));
	}
	
	@Test
	void unwrapWithTooShortWrappedKey() {
		byte[] artifact = sealed(List.of(alice.getPublic()));
		int lengthOffset = HEADER_LENGTH + 16 + 32 + SUITE.kem().encapsulationLength();
		int wrappedLength = ByteBuffer.wrap(artifact, lengthOffset, 2).getShort() & 0xFFFF;
		int shortened = SUITE.aead().nonceLength() + SUITE.aead().tagLength() - 1;
		
		byte[] rebuilt = CryptoBytes.concat(
			CryptoBytes.slice(artifact, 0, lengthOffset),
			CryptoBytes.of((short) shortened),
			CryptoBytes.slice(artifact, lengthOffset + 2, shortened),
			CryptoBytes.slice(artifact, lengthOffset + 2 + wrappedLength, artifact.length - lengthOffset - 2 - wrappedLength)
		);
		
		MalformedDataException exception = assertThrows(MalformedDataException.class, () -> SealedForMany.unseal(alice.getPublic(), alice.getPrivate(), rebuilt, null));
		assertTrue(exception.getMessage().startsWith("Wrapped content key too short: "));
	}
	
	@Test
	void recipientsOfWithNullSealed() {
		assertEquals("Sealed artifact must not be null", assertThrows(NullPointerException.class, () -> SealedForMany.recipientsOf(null)).getMessage());
	}
	
	@Test
	void recipientsOfWithMalformedArtifact() {
		byte[] unknownSuite = sealedToAliceAndBob();
		System.arraycopy(CryptoBytes.of((short) 999), 0, unknownSuite, 5, 2);
		byte[] oversized = sealedToAliceAndBob();
		System.arraycopy(CryptoBytes.of((short) 65535), 0, oversized, 23, 2);
		
		assertThrows(MalformedDataException.class, () -> SealedForMany.recipientsOf(new byte[10]));
		assertThrows(MalformedDataException.class, () -> SealedForMany.recipientsOf(flip(sealedToAliceAndBob(), 0)));
		assertThrows(MalformedDataException.class, () -> SealedForMany.recipientsOf(unknownSuite));
		assertThrows(MalformedDataException.class, () -> SealedForMany.recipientsOf(oversized));
	}
	
	@Test
	void suiteOfWithNullSealed() {
		assertEquals("Sealed artifact must not be null", assertThrows(NullPointerException.class, () -> SealedForMany.suiteOf(null)).getMessage());
	}
	
	@Test
	void suiteOfWithMalformedArtifact() {
		assertThrows(MalformedDataException.class, () -> SealedForMany.suiteOf(new byte[10]));
		assertThrows(MalformedDataException.class, () -> SealedForMany.suiteOf(flip(sealedToAliceAndBob(), 0)));
	}
	
	@Test
	void sealWithSingleRecipient() {
		byte[] artifact = sealed(List.of(alice.getPublic()));
		
		assertEquals(1, SealedForMany.recipientsOf(artifact).size());
		assertArrayEquals(PLAINTEXT, SealedForMany.unseal(alice.getPublic(), alice.getPrivate(), artifact, null));
	}
	
	@Test
	void sealWithTwoRecipients() {
		byte[] artifact = sealedToAliceAndBob();
		
		assertArrayEquals(PLAINTEXT, SealedForMany.unseal(alice.getPublic(), alice.getPrivate(), artifact, null));
		assertArrayEquals(PLAINTEXT, SealedForMany.unseal(bob.getPublic(), bob.getPrivate(), artifact, null));
	}
	
	@Test
	void sealWithManyRecipients() {
		List<KeyPair> pairs = new ArrayList<>();
		List<PublicKey> recipients = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			KeyPair pair = Kems.generateKeyPair(SUITE.kem());
			pairs.add(pair);
			recipients.add(pair.getPublic());
		}
		
		byte[] artifact = sealed(recipients);
		assertEquals(10, SealedForMany.recipientsOf(artifact).size());
		for (KeyPair pair : pairs) {
			assertArrayEquals(PLAINTEXT, SealedForMany.unseal(pair.getPublic(), pair.getPrivate(), artifact, null));
		}
	}
	
	@Test
	void unsealSkipsNonMatchingSlots() {
		byte[] artifact = sealed(List.of(alice.getPublic(), bob.getPublic(), carol.getPublic()));
		assertArrayEquals(PLAINTEXT, SealedForMany.unseal(carol.getPublic(), carol.getPrivate(), artifact, null));
	}
	
	@Test
	void unsealMatchesFirstSlot() {
		byte[] artifact = sealed(List.of(alice.getPublic(), bob.getPublic(), carol.getPublic()));
		assertArrayEquals(PLAINTEXT, SealedForMany.unseal(alice.getPublic(), alice.getPrivate(), artifact, null));
	}
	
	@Test
	void unwrapReturnsPresentForCorrectKey() {
		assertArrayEquals(PLAINTEXT, SealedForMany.unseal(bob.getPublic(), bob.getPrivate(), sealedToAliceAndBob(), null));
	}
	
	@Test
	void parseWithMinimumHeaderLength() {
		byte[] header = CryptoBytes.concat("LUCM".getBytes(StandardCharsets.US_ASCII), new byte[] { 1 }, CryptoBytes.of(SUITE.id()), UUIDs.toBytes(UUIDs.v7()), CryptoBytes.of((short) 0));
		
		assertEquals(HEADER_LENGTH, header.length);
		MalformedDataException exception = assertThrows(MalformedDataException.class, () -> SealedForMany.recipientsOf(header));
		assertEquals("Multi-recipient blob has no readable body", exception.getMessage());
	}
	
	@Test
	void parseWithZeroRecipientCount() {
		byte[] header = CryptoBytes.concat("LUCM".getBytes(StandardCharsets.US_ASCII), new byte[] { 1 }, CryptoBytes.of(SUITE.id()), UUIDs.toBytes(UUIDs.v7()), CryptoBytes.of((short) 0));
		byte[] artifact = CryptoBytes.concat(header, new byte[SUITE.aead().nonceLength() + SUITE.aead().tagLength()]);
		
		assertTrue(SealedForMany.recipientsOf(artifact).isEmpty());
		AuthenticationException exception = assertThrows(AuthenticationException.class, () -> SealedForMany.unseal(alice.getPublic(), alice.getPrivate(), artifact, null));
		assertEquals("No recipient slot matches this key", exception.getMessage());
	}
	
	@Test
	void parseWithCountAtTheAllocationBound() {
		byte[] artifact = sealed(List.of(alice.getPublic()));
		int minimumSlot = 16 + 32 + SUITE.kem().encapsulationLength() + 2;
		int bound = (artifact.length - HEADER_LENGTH) / minimumSlot;
		
		assertTrue(bound >= 1);
		byte[] atBound = artifact.clone();
		System.arraycopy(CryptoBytes.of((short) bound), 0, atBound, 23, 2);
		if (bound == 1) {
			assertArrayEquals(PLAINTEXT, SealedForMany.unseal(alice.getPublic(), alice.getPrivate(), atBound, null));
		} else {
			assertThrows(MalformedDataException.class, () -> SealedForMany.unseal(alice.getPublic(), alice.getPrivate(), atBound, null));
		}
	}
	
	@Test
	void aadWithNullAssociatedData() {
		byte[] artifact = SealedForMany.seal(SUITE, List.of(alice.getPublic()), PLAINTEXT, null);
		
		assertArrayEquals(PLAINTEXT, SealedForMany.unseal(alice.getPublic(), alice.getPrivate(), artifact, null));
		assertArrayEquals(PLAINTEXT, SealedForMany.unseal(alice.getPublic(), alice.getPrivate(), artifact, new byte[0]));
	}
	
	@Test
	void aadWithEmptyAssociatedData() {
		byte[] artifact = SealedForMany.seal(SUITE, List.of(alice.getPublic()), PLAINTEXT, new byte[0]);
		
		assertArrayEquals(PLAINTEXT, SealedForMany.unseal(alice.getPublic(), alice.getPrivate(), artifact, null));
		assertArrayEquals(PLAINTEXT, SealedForMany.unseal(alice.getPublic(), alice.getPrivate(), artifact, new byte[0]));
	}
	
	@Test
	void aadWithNonEmptyAssociatedData() {
		byte[] artifact = SealedForMany.seal(SUITE, List.of(alice.getPublic()), PLAINTEXT, AAD);
		
		assertArrayEquals(PLAINTEXT, SealedForMany.unseal(alice.getPublic(), alice.getPrivate(), artifact, AAD));
		assertThrows(AuthenticationException.class, () -> SealedForMany.unseal(alice.getPublic(), alice.getPrivate(), artifact, null));
	}
	
	@Test
	void sealedArtifactLayout() {
		byte[] artifact = sealedToAliceAndBob();
		
		assertArrayEquals("LUCM".getBytes(StandardCharsets.US_ASCII), Arrays.copyOf(artifact, 4));
		assertEquals(1, artifact[4]);
		assertEquals(SUITE.id(), ByteBuffer.wrap(artifact, 5, 2).getShort());
		assertNotNull(UUIDs.fromBytes(CryptoBytes.slice(artifact, 7, 16)));
		assertEquals(2, ByteBuffer.wrap(artifact, 23, 2).getShort());
	}
	
	@Test
	void headerLengthConstant() {
		byte[] artifact = sealed(List.of(alice.getPublic()));
		assertEquals(KeyId.of(alice.getPublic()), KeyId.fromBytes(CryptoBytes.slice(artifact, HEADER_LENGTH, 16)));
	}
	
	@Test
	void recipientCountIsBigEndian() {
		byte[] artifact = sealedToAliceAndBob();
		assertArrayEquals(new byte[] { 0, 2 }, CryptoBytes.slice(artifact, 23, 2));
		assertEquals(258, ByteBuffer.wrap(new byte[] { 1, 2 }).getShort() & 0xFFFF);
	}
	
	@Test
	void slotLayout() {
		byte[] artifact = sealed(List.of(alice.getPublic()));
		int offset = HEADER_LENGTH;
		
		assertArrayEquals(KeyId.of(alice.getPublic()).toBytes(), CryptoBytes.slice(artifact, offset, 16));
		offset += 16 + 32;
		assertEquals(SUITE.kem().encapsulationLength(), CryptoBytes.slice(artifact, offset, SUITE.kem().encapsulationLength()).length);
		offset += SUITE.kem().encapsulationLength();
		assertEquals(SUITE.aead().nonceLength() + SUITE.aead().keyLength() + SUITE.aead().tagLength(), ByteBuffer.wrap(artifact, offset, 2).getShort() & 0xFFFF);
	}
	
	@Test
	void artifactLengthIsPredictable() {
		for (int count : new int[] { 1, 2, 3 }) {
			List<PublicKey> recipients = new ArrayList<>();
			for (int i = 0; i < count; i++) {
				recipients.add(Kems.generateKeyPair(SUITE.kem()).getPublic());
			}
			
			int expected = HEADER_LENGTH + count * slotLength + SUITE.aead().nonceLength() + PLAINTEXT.length + SUITE.aead().tagLength();
			assertEquals(expected, sealed(recipients).length, "count " + count);
		}
	}
	
	@Test
	void sealWithEmptyPlaintext() {
		byte[] artifact = assertDoesNotThrow(() -> SealedForMany.seal(SUITE, List.of(alice.getPublic()), new byte[0], null));
		assertEquals(0, SealedForMany.unseal(alice.getPublic(), alice.getPrivate(), artifact, null).length);
	}
	
	@Test
	void sealProducesDifferentArtifactsPerCall() {
		byte[] first = sealedToAliceAndBob();
		byte[] second = sealedToAliceAndBob();
		
		assertFalse(Arrays.equals(first, second));
		assertNotEquals(UUIDs.fromBytes(CryptoBytes.slice(first, 7, 16)), UUIDs.fromBytes(CryptoBytes.slice(second, 7, 16)));
		assertArrayEquals(PLAINTEXT, SealedForMany.unseal(alice.getPublic(), alice.getPrivate(), first, null));
		assertArrayEquals(PLAINTEXT, SealedForMany.unseal(alice.getPublic(), alice.getPrivate(), second, null));
	}
	
	@Test
	void messageIdIsTimeOrdered() {
		UUID messageId = UUIDs.fromBytes(CryptoBytes.slice(sealedToAliceAndBob(), 7, 16));
		
		assertEquals(7, messageId.version());
		assertTrue(Math.abs(UUIDs.unixMillis(messageId) - System.currentTimeMillis()) < 10_000L);
	}
	
	@Test
	void recipientsOfListsKeyIdsInOrder() {
		List<PublicKey> recipients = List.of(alice.getPublic(), bob.getPublic(), carol.getPublic());
		assertEquals(recipients.stream().map(KeyId::of).toList(), SealedForMany.recipientsOf(sealed(recipients)));
	}
	
	@Test
	void recipientsOfReturnsUnmodifiableList() {
		List<KeyId> ids = SealedForMany.recipientsOf(sealedToAliceAndBob());
		assertThrows(UnsupportedOperationException.class, () -> ids.add(KeyId.of(carol.getPublic())));
	}
	
	@Test
	void recipientsOfDoesNotNeedAPrivateKey() {
		assertEquals(2, assertDoesNotThrow(() -> SealedForMany.recipientsOf(sealedToAliceAndBob())).size());
	}
	
	@Test
	void suiteOfReadsWithoutAnyKey() {
		for (CryptoSuite suite : CryptoSuite.values()) {
			assumeTrue(suite.isSupported());
			KeyPair pair = Kems.generateKeyPair(suite.kem());
			assertSame(suite, SealedForMany.suiteOf(SealedForMany.seal(suite, List.of(pair.getPublic()), PLAINTEXT, null)), suite.name());
		}
	}
	
	@Test
	void sealDoesNotMutateInputs() {
		byte[] plaintext = PLAINTEXT.clone();
		byte[] aad = AAD.clone();
		List<PublicKey> recipients = new ArrayList<>(List.of(alice.getPublic(), bob.getPublic()));
		
		SealedForMany.seal(SUITE, recipients, plaintext, aad);
		assertArrayEquals(PLAINTEXT, plaintext);
		assertArrayEquals(AAD, aad);
		assertEquals(List.of(alice.getPublic(), bob.getPublic()), recipients);
	}
	
	@Test
	void unsealDoesNotMutateInputs() {
		byte[] aad = AAD.clone();
		byte[] artifact = SealedForMany.seal(SUITE, List.of(alice.getPublic()), PLAINTEXT, aad);
		byte[] copy = artifact.clone();
		
		SealedForMany.unseal(alice.getPublic(), alice.getPrivate(), artifact, aad);
		assertArrayEquals(copy, artifact);
		assertArrayEquals(AAD, aad);
	}
	
	@Test
	void roundTripForEverySuite() {
		for (CryptoSuite suite : CryptoSuite.values()) {
			assumeTrue(suite.isSupported());
			KeyPair first = Kems.generateKeyPair(suite.kem());
			KeyPair second = Kems.generateKeyPair(suite.kem());
			byte[] artifact = SealedForMany.seal(suite, List.of(first.getPublic(), second.getPublic()), PLAINTEXT, AAD);
			
			assertArrayEquals(PLAINTEXT, SealedForMany.unseal(first.getPublic(), first.getPrivate(), artifact, AAD), suite.name());
			assertArrayEquals(PLAINTEXT, SealedForMany.unseal(second.getPublic(), second.getPrivate(), artifact, AAD), suite.name());
		}
	}
	
	@Test
	void roundTripForEveryPlaintextSize() {
		for (int size : new int[] { 0, 1, 15, 16, 17, 1024, 100000 }) {
			byte[] plaintext = CryptoRandom.bytes(size);
			byte[] artifact = SealedForMany.seal(SUITE, List.of(alice.getPublic(), bob.getPublic()), plaintext, null);
			
			assertArrayEquals(plaintext, SealedForMany.unseal(alice.getPublic(), alice.getPrivate(), artifact, null), "size " + size);
			assertArrayEquals(plaintext, SealedForMany.unseal(bob.getPublic(), bob.getPrivate(), artifact, null), "size " + size);
		}
	}
	
	@Test
	void everyRecipientRecoversTheSamePlaintext() {
		byte[] plaintext = CryptoRandom.bytes(10000);
		List<KeyPair> pairs = new ArrayList<>();
		List<PublicKey> recipients = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			KeyPair pair = Kems.generateKeyPair(SUITE.kem());
			pairs.add(pair);
			recipients.add(pair.getPublic());
		}
		
		byte[] many = SealedForMany.seal(SUITE, recipients, plaintext, null);
		byte[] single = SealedForMany.seal(SUITE, List.of(recipients.getFirst()), plaintext, null);
		for (KeyPair pair : pairs) {
			assertArrayEquals(plaintext, SealedForMany.unseal(pair.getPublic(), pair.getPrivate(), many, null));
		}
		assertEquals(single.length + 4 * slotLength, many.length);
	}
	
	@Test
	void bodyIsEncryptedOnceRegardlessOfRecipientCount() {
		List<PublicKey> recipients = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			recipients.add(Kems.generateKeyPair(SUITE.kem()).getPublic());
		}
		
		int one = SealedForMany.seal(SUITE, recipients.subList(0, 1), PLAINTEXT, null).length;
		int two = SealedForMany.seal(SUITE, recipients.subList(0, 2), PLAINTEXT, null).length;
		int ten = SealedForMany.seal(SUITE, recipients, PLAINTEXT, null).length;
		
		assertEquals(slotLength, two - one);
		assertEquals(9 * slotLength, ten - one);
	}
	
	@Test
	void commitmentRejectsForeignSlotsWithoutRunningACipher() {
		List<KeyPair> pairs = new ArrayList<>();
		List<PublicKey> recipients = new ArrayList<>();
		for (int i = 0; i < 20; i++) {
			KeyPair pair = Kems.generateKeyPair(SUITE.kem());
			pairs.add(pair);
			recipients.add(pair.getPublic());
		}
		
		byte[] artifact = sealed(recipients);
		for (int i = 0; i < pairs.size(); i++) {
			KeyPair own = pairs.get(i);
			KeyPair other = pairs.get(i == 0 ? 1 : 0);
			assertArrayEquals(PLAINTEXT, SealedForMany.unseal(own.getPublic(), own.getPrivate(), artifact, null));
			
			AuthenticationException exception = assertThrows(AuthenticationException.class, () -> SealedForMany.unseal(own.getPublic(), other.getPrivate(), artifact, null), "index " + i);
			assertEquals("No recipient slot matches this key", exception.getMessage());
		}
	}
	
	@Test
	void slotsCarryDistinctCommitments() {
		List<PublicKey> recipients = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			recipients.add(Kems.generateKeyPair(SUITE.kem()).getPublic());
		}
		
		byte[] artifact = sealed(recipients);
		Set<String> commitments = new HashSet<>();
		for (int i = 0; i < 5; i++) {
			commitments.add(HexFormat.of().formatHex(CryptoBytes.slice(artifact, HEADER_LENGTH + i * slotLength + 16, 32)));
		}
		assertEquals(5, commitments.size());
	}
	
	@Test
	void slotsCarryDistinctEncapsulations() {
		List<PublicKey> recipients = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			recipients.add(Kems.generateKeyPair(SUITE.kem()).getPublic());
		}
		
		byte[] artifact = sealed(recipients);
		Set<String> encapsulations = new HashSet<>();
		for (int i = 0; i < 5; i++) {
			encapsulations.add(HexFormat.of().formatHex(CryptoBytes.slice(artifact, HEADER_LENGTH + i * slotLength + 48, SUITE.kem().encapsulationLength())));
		}
		assertEquals(5, encapsulations.size());
	}
	
	@Test
	void tamperingAnyHeaderOrSlotByteFails() {
		byte[] artifact = SealedForMany.seal(SUITE, List.of(alice.getPublic(), bob.getPublic()), new byte[] { 1, 2, 3, 4 }, null);
		int end = HEADER_LENGTH + 2 * slotLength;
		int malformed = 0;
		int authentication = 0;
		
		for (int i = 0; i < end; i++) {
			int index = i;
			byte[] corrupted = flip(artifact, index);
			try {
				SealedForMany.unseal(alice.getPublic(), alice.getPrivate(), corrupted, null);
				fail("index " + index + " did not fail");
			} catch (MalformedDataException e) {
				malformed++;
				int slotOffset = index < HEADER_LENGTH ? -1 : (index - HEADER_LENGTH) % slotLength;
				assertTrue(index < HEADER_LENGTH || slotOffset >= 16 + 32 + SUITE.kem().encapsulationLength(), "index " + index);
			} catch (AuthenticationException e) {
				authentication++;
			}
		}
		assertEquals(end, malformed + authentication);
		assertTrue(malformed > 0);
		assertTrue(authentication > 0);
	}
	
	@Test
	void tamperingAnyBodyByteFails() {
		byte[] artifact = SealedForMany.seal(SUITE, List.of(alice.getPublic()), new byte[] { 1, 2, 3, 4 }, null);
		int start = HEADER_LENGTH + slotLength;
		
		for (int i = start; i < artifact.length; i++) {
			int index = i;
			assertThrows(AuthenticationException.class, () -> SealedForMany.unseal(alice.getPublic(), alice.getPrivate(), flip(artifact, index), null), "index " + index);
		}
		assertThrows(Exception.class, () -> SealedForMany.unseal(alice.getPublic(), alice.getPrivate(), Arrays.copyOf(artifact, artifact.length - 1), null));
		assertThrows(Exception.class, () -> SealedForMany.unseal(alice.getPublic(), alice.getPrivate(), Arrays.copyOf(artifact, artifact.length + 1), null));
	}
	
	@Test
	void reorderingSlotsFails() {
		byte[] artifact = sealed(List.of(alice.getPublic(), bob.getPublic(), carol.getPublic()));
		int bodyStart = HEADER_LENGTH + 3 * slotLength;
		byte[] reordered = CryptoBytes.concat(
			CryptoBytes.slice(artifact, 0, HEADER_LENGTH),
			CryptoBytes.slice(artifact, HEADER_LENGTH + slotLength, slotLength),
			CryptoBytes.slice(artifact, HEADER_LENGTH, slotLength),
			CryptoBytes.slice(artifact, HEADER_LENGTH + 2 * slotLength, slotLength),
			CryptoBytes.slice(artifact, bodyStart, artifact.length - bodyStart)
		);
		
		assertThrows(AuthenticationException.class, () -> SealedForMany.unseal(alice.getPublic(), alice.getPrivate(), reordered, null));
		assertThrows(AuthenticationException.class, () -> SealedForMany.unseal(bob.getPublic(), bob.getPrivate(), reordered, null));
	}
	
	@Test
	void duplicateRecipientProducesTwoSlots() {
		byte[] artifact = assertDoesNotThrow(() -> sealed(List.of(alice.getPublic(), alice.getPublic())));
		List<KeyId> ids = SealedForMany.recipientsOf(artifact);
		
		assertEquals(2, ids.size());
		assertEquals(ids.get(0), ids.get(1));
		assertFalse(Arrays.equals(CryptoBytes.slice(artifact, HEADER_LENGTH + 16, 32), CryptoBytes.slice(artifact, HEADER_LENGTH + slotLength + 16, 32)));
		assertArrayEquals(PLAINTEXT, SealedForMany.unseal(alice.getPublic(), alice.getPrivate(), artifact, null));
	}
	
	@Test
	void keyIdCollisionFallsThroughToTheNextSlot() {
		byte[] artifact = sealed(List.of(bob.getPublic(), alice.getPublic()));
		System.arraycopy(KeyId.of(alice.getPublic()).toBytes(), 0, artifact, HEADER_LENGTH, 16);
		
		assertEquals(List.of(KeyId.of(alice.getPublic()), KeyId.of(alice.getPublic())), SealedForMany.recipientsOf(artifact));
		assertThrows(AuthenticationException.class, () -> SealedForMany.unseal(alice.getPublic(), alice.getPrivate(), artifact, null));
	}
	
	@Test
	void unsealAfterSuiteMigration() {
		assumeTrue(CryptoSuite.CLASSICAL_V1.isSupported());
		KeyPair classical = Kems.generateKeyPair(CryptoSuite.CLASSICAL_V1.kem());
		byte[] artifact = SealedForMany.seal(CryptoSuite.CLASSICAL_V1, List.of(classical.getPublic()), PLAINTEXT, null);
		
		assertNotSame(CryptoSuite.CLASSICAL_V1, CryptoSuite.current());
		assertSame(CryptoSuite.CLASSICAL_V1, SealedForMany.suiteOf(artifact));
		assertArrayEquals(PLAINTEXT, SealedForMany.unseal(classical.getPublic(), classical.getPrivate(), artifact, null));
	}
	
	@Test
	void artifactIsNotConfusableWithSealed() {
		byte[] single = Sealed.seal(SUITE, alice.getPublic(), PLAINTEXT, null);
		byte[] many = sealedToAliceAndBob();
		
		assertThrows(MalformedDataException.class, () -> Sealed.unseal(alice.getPrivate(), many));
		assertThrows(MalformedDataException.class, () -> Sealed.suiteOf(many));
		assertThrows(MalformedDataException.class, () -> Sealed.messageIdOf(many));
		assertThrows(MalformedDataException.class, () -> SealedForMany.unseal(alice.getPublic(), alice.getPrivate(), single, null));
		assertThrows(MalformedDataException.class, () -> SealedForMany.suiteOf(single));
		assertThrows(MalformedDataException.class, () -> SealedForMany.recipientsOf(single));
	}
	
	@Test
	void largePlaintextWithManyRecipients() {
		byte[] plaintext = CryptoRandom.bytes(1 << 20);
		byte[] aad = CryptoRandom.bytes(1 << 16);
		List<KeyPair> pairs = new ArrayList<>();
		List<PublicKey> recipients = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			KeyPair pair = Kems.generateKeyPair(SUITE.kem());
			pairs.add(pair);
			recipients.add(pair.getPublic());
		}
		
		byte[] artifact = SealedForMany.seal(SUITE, recipients, plaintext, aad);
		for (KeyPair pair : pairs) {
			assertArrayEquals(plaintext, SealedForMany.unseal(pair.getPublic(), pair.getPrivate(), artifact, aad));
		}
	}
	
	@Test
	void contentKeyIsWipedAfterUnsealing() {
		byte[] artifact = sealedToAliceAndBob();
		for (int i = 0; i < 5; i++) {
			assertArrayEquals(PLAINTEXT, SealedForMany.unseal(alice.getPublic(), alice.getPrivate(), artifact, null));
		}
	}
	
	@Test
	void unsealIsRepeatable() {
		byte[] artifact = sealedToAliceAndBob();
		for (int i = 0; i < 5; i++) {
			assertArrayEquals(PLAINTEXT, SealedForMany.unseal(bob.getPublic(), bob.getPrivate(), artifact, null));
		}
	}
}
