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
import net.luis.utils.crypto.key.Secret;
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
import java.security.SecureRandom;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

/**
 * Test class for {@link Sealed}.<br>
 *
 * @author Luis-St
 */
class SealedTest {
	
	private static final CryptoSuite SUITE = CryptoSuite.HYBRID_V1;
	private static final byte[] PLAINTEXT = "the quick brown fox".getBytes(StandardCharsets.UTF_8);
	private static final byte[] AAD = "header".getBytes(StandardCharsets.UTF_8);
	
	private static KeyPair recipient;
	private static KeyPair otherRecipient;
	private static KeyPair sender;
	private static KeyPair ed25519;
	
	@BeforeAll
	static void setUp() {
		Providers.installBouncyCastle();
		recipient = Kems.generateKeyPair(SUITE.kem());
		otherRecipient = Kems.generateKeyPair(SUITE.kem());
		sender = Signatures.generateKeyPair(SUITE.signature());
		ed25519 = Signatures.generateKeyPair(SignatureAlgorithm.ED25519);
	}
	
	private static byte[] sealed() {
		return Sealed.seal(recipient.getPublic(), PLAINTEXT);
	}
	
	private static SecureRandom seeded() throws Exception {
		SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
		random.setSeed(new byte[] { 1, 2, 3, 4 });
		return random;
	}
	
	private static byte[] flip(byte[] source, int index) {
		byte[] copy = source.clone();
		copy[index] ^= 1;
		return copy;
	}
	
	@Test
	void constructorIsPrivate() throws Exception {
		Constructor<?>[] constructors = Sealed.class.getDeclaredConstructors();
		assertEquals(1, constructors.length);
		assertTrue(Modifier.isPrivate(constructors[0].getModifiers()));
		assertTrue(Modifier.isFinal(Sealed.class.getModifiers()));
		
		Constructor<Sealed> constructor = Sealed.class.getDeclaredConstructor();
		constructor.setAccessible(true);
		assertNotNull(constructor.newInstance());
	}
	
	@Test
	void constructHead() {
		UUID messageId = UUID.randomUUID();
		byte[] commitment = new byte[32];
		Sealed.Head head = new Sealed.Head(CryptoSuite.HYBRID_V1, messageId, commitment);
		
		assertSame(CryptoSuite.HYBRID_V1, head.suite());
		assertSame(messageId, head.messageId());
		assertSame(commitment, head.commitment());
	}
	
	@Test
	void constructHeadWithNullSuite() {
		assertEquals("Suite must not be null", assertThrows(NullPointerException.class, () -> new Sealed.Head(null, UUID.randomUUID(), new byte[32])).getMessage());
	}
	
	@Test
	void constructHeadWithNullMessageId() {
		assertEquals("Message id must not be null", assertThrows(NullPointerException.class, () -> new Sealed.Head(SUITE, null, new byte[32])).getMessage());
	}
	
	@Test
	void constructHeadWithNullCommitment() {
		assertEquals("Commitment must not be null", assertThrows(NullPointerException.class, () -> new Sealed.Head(SUITE, UUID.randomUUID(), null)).getMessage());
	}
	
	@Test
	void constructHeadWithAllNull() {
		assertEquals("Suite must not be null", assertThrows(NullPointerException.class, () -> new Sealed.Head(null, null, null)).getMessage());
	}
	
	@Test
	void sealWithNullRecipient() {
		assertEquals("Recipient must not be null", assertThrows(NullPointerException.class, () -> Sealed.seal(null, PLAINTEXT)).getMessage());
		assertEquals("Recipient must not be null", assertThrows(NullPointerException.class, () -> Sealed.seal(SUITE, null, PLAINTEXT, AAD)).getMessage());
		assertEquals("Recipient must not be null", assertThrows(NullPointerException.class, () -> Sealed.seal(CryptoRandom.instance(), SUITE, null, PLAINTEXT, AAD)).getMessage());
	}
	
	@Test
	void sealWithNullPlaintext() {
		assertEquals("Plaintext must not be null", assertThrows(NullPointerException.class, () -> Sealed.seal(recipient.getPublic(), null)).getMessage());
	}
	
	@Test
	void sealWithNullSuite() {
		assertEquals("Suite must not be null", assertThrows(NullPointerException.class, () -> Sealed.seal(null, recipient.getPublic(), PLAINTEXT, AAD)).getMessage());
	}
	
	@Test
	void sealWithNullRandom() {
		assertEquals("Random must not be null", assertThrows(NullPointerException.class, () -> Sealed.seal(null, SUITE, recipient.getPublic(), PLAINTEXT, AAD)).getMessage());
	}
	
	@Test
	void sealWithAllNull() {
		assertEquals("Random must not be null", assertThrows(NullPointerException.class, () -> Sealed.seal(null, null, null, null, null)).getMessage());
	}
	
	@Test
	void sealWithMismatchedRecipientKey() {
		assertThrows(ClassCastException.class, () -> Sealed.seal(SUITE, ed25519.getPublic(), PLAINTEXT, null));
	}
	
	@Test
	void unsealWithNullRecipient() {
		assertEquals("Recipient must not be null", assertThrows(NullPointerException.class, () -> Sealed.unseal(null, sealed())).getMessage());
	}
	
	@Test
	void unsealWithNullSealed() {
		assertEquals("Sealed artifact must not be null", assertThrows(NullPointerException.class, () -> Sealed.unseal(recipient.getPrivate(), null)).getMessage());
	}
	
	@Test
	void unsealWithTooShortArtifact() {
		for (int length : new int[] { 0, 1, Sealed.HEADER_LENGTH - 1 }) {
			MalformedDataException exception = assertThrows(MalformedDataException.class, () -> Sealed.unseal(recipient.getPrivate(), new byte[length]));
			assertEquals("Sealed blob too short to contain a header", exception.getMessage());
		}
	}
	
	@Test
	void unsealWithBadMagic() {
		byte[] artifact = sealed();
		for (int i = 0; i < Sealed.MAGIC.length; i++) {
			int index = i;
			MalformedDataException exception = assertThrows(MalformedDataException.class, () -> Sealed.unseal(recipient.getPrivate(), flip(artifact, index)), "index " + index);
			assertEquals("Not a sealed blob (bad magic)", exception.getMessage());
		}
	}
	
	@Test
	void unsealWithUnsupportedVersion() {
		byte[] artifact = sealed();
		for (byte version : new byte[] { 0, 2, (byte) 0xFF }) {
			byte[] corrupted = artifact.clone();
			corrupted[Sealed.MAGIC.length] = version;
			
			MalformedDataException exception = assertThrows(MalformedDataException.class, () -> Sealed.unseal(recipient.getPrivate(), corrupted));
			assertTrue(exception.getMessage().contains("Unsupported sealed format version"));
			assertTrue(exception.getMessage().contains(String.valueOf(version)));
		}
	}
	
	@Test
	void unsealWithUnknownSuiteId() {
		byte[] artifact = sealed();
		System.arraycopy(CryptoBytes.of((short) 999), 0, artifact, Sealed.MAGIC.length + 1, 2);
		
		MalformedDataException exception = assertThrows(MalformedDataException.class, () -> Sealed.unseal(recipient.getPrivate(), artifact));
		assertTrue(exception.getMessage().contains("999"));
		assertTrue(exception.getMessage().contains("newer version"));
	}
	
	@Test
	void unsealWithTruncatedBody() {
		byte[] artifact = sealed();
		int minimum = Sealed.HEADER_LENGTH + SUITE.kem().encapsulationLength() + SUITE.aead().nonceLength() + SUITE.aead().tagLength();
		
		for (int length : new int[] { Sealed.HEADER_LENGTH, minimum - 1 }) {
			MalformedDataException exception = assertThrows(MalformedDataException.class, () -> Sealed.unseal(recipient.getPrivate(), Arrays.copyOf(artifact, length)));
			assertTrue(exception.getMessage().contains("Sealed blob too short: " + length));
			assertTrue(exception.getMessage().contains(String.valueOf(minimum)));
		}
	}
	
	@Test
	void unsealWithWrongKey() {
		AuthenticationException exception = assertThrows(AuthenticationException.class, () -> Sealed.unseal(otherRecipient.getPrivate(), sealed()));
		assertEquals("Key commitment mismatch - the header does not belong to this key", exception.getMessage());
	}
	
	@Test
	void unsealWithTamperedCommitment() {
		int offset = Sealed.MAGIC.length + 1 + Short.BYTES + 16;
		byte[] artifact = sealed();
		
		for (int index : new int[] { offset, offset + Sealed.COMMITMENT_LENGTH - 1 }) {
			AuthenticationException exception = assertThrows(AuthenticationException.class, () -> Sealed.unseal(recipient.getPrivate(), flip(artifact, index)));
			assertTrue(exception.getMessage().contains("Key commitment mismatch"));
		}
	}
	
	@Test
	void unsealWithTamperedCiphertext() {
		byte[] artifact = sealed();
		AuthenticationException exception = assertThrows(AuthenticationException.class, () -> Sealed.unseal(recipient.getPrivate(), flip(artifact, artifact.length - 1)));
		assertFalse(exception.getMessage().contains("Key commitment mismatch"));
	}
	
	@Test
	void unsealWithTamperedSuiteId() {
		byte[] artifact = sealed();
		System.arraycopy(CryptoBytes.of(CryptoSuite.CLASSICAL_V1.id()), 0, artifact, Sealed.MAGIC.length + 1, 2);
		
		assertNotEquals(SUITE.kem().encapsulationLength(), CryptoSuite.CLASSICAL_V1.kem().encapsulationLength());
		assertThrows(AuthenticationException.class, () -> Sealed.unseal(recipient.getPrivate(), artifact));
	}
	
	@Test
	void unsealWithTamperedMessageId() {
		int offset = Sealed.MAGIC.length + 1 + Short.BYTES;
		byte[] artifact = sealed();
		
		for (int index : new int[] { offset, offset + 15 }) {
			assertThrows(AuthenticationException.class, () -> Sealed.unseal(recipient.getPrivate(), flip(artifact, index)), "index " + index);
		}
	}
	
	@Test
	void unsealWithTamperedEncapsulation() {
		byte[] artifact = sealed();
		assertThrows(AuthenticationException.class, () -> Sealed.unseal(recipient.getPrivate(), flip(artifact, Sealed.HEADER_LENGTH)));
	}
	
	@Test
	void unsealWithTamperedNonce() {
		byte[] artifact = sealed();
		int offset = Sealed.HEADER_LENGTH + SUITE.kem().encapsulationLength();
		
		AuthenticationException exception = assertThrows(AuthenticationException.class, () -> Sealed.unseal(recipient.getPrivate(), flip(artifact, offset)));
		assertFalse(exception.getMessage().contains("Key commitment mismatch"));
	}
	
	@Test
	void unsealWithWrongAssociatedData() {
		byte[] artifact = Sealed.seal(SUITE, recipient.getPublic(), PLAINTEXT, "header-a".getBytes(StandardCharsets.UTF_8));
		
		assertThrows(AuthenticationException.class, () -> Sealed.unseal(recipient.getPrivate(), artifact, "header-b".getBytes(StandardCharsets.UTF_8)));
		assertThrows(AuthenticationException.class, () -> Sealed.unseal(recipient.getPrivate(), artifact, null));
	}
	
	@Test
	void sealSignedWithNullSuite() {
		assertEquals("Suite must not be null", assertThrows(NullPointerException.class, () -> Sealed.sealSigned(null, recipient.getPublic(), sender.getPrivate(), PLAINTEXT)).getMessage());
	}
	
	@Test
	void sealSignedWithNullSender() {
		assertEquals("Sender must not be null", assertThrows(NullPointerException.class, () -> Sealed.sealSigned(SUITE, recipient.getPublic(), null, PLAINTEXT)).getMessage());
	}
	
	@Test
	void sealSignedWithNullPlaintext() {
		assertEquals("Plaintext must not be null", assertThrows(NullPointerException.class, () -> Sealed.sealSigned(SUITE, recipient.getPublic(), sender.getPrivate(), null)).getMessage());
	}
	
	@Test
	void sealSignedWithNullRecipient() {
		assertEquals("Recipient must not be null", assertThrows(NullPointerException.class, () -> Sealed.sealSigned(SUITE, null, sender.getPrivate(), PLAINTEXT)).getMessage());
	}
	
	@Test
	void unsealVerifiedWithNullRecipient() {
		assertEquals("Recipient must not be null", assertThrows(NullPointerException.class, () -> Sealed.unsealVerified(null, sender.getPublic(), sealed())).getMessage());
	}
	
	@Test
	void unsealVerifiedWithNullSender() {
		assertEquals("Sender must not be null", assertThrows(NullPointerException.class, () -> Sealed.unsealVerified(recipient.getPrivate(), null, sealed())).getMessage());
	}
	
	@Test
	void unsealVerifiedWithNullSealed() {
		assertEquals("Sealed artifact must not be null", assertThrows(NullPointerException.class, () -> Sealed.unsealVerified(recipient.getPrivate(), sender.getPublic(), null)).getMessage());
	}
	
	@Test
	void unsealVerifiedWithWrongSender() {
		byte[] artifact = Sealed.sealSigned(SUITE, recipient.getPublic(), sender.getPrivate(), PLAINTEXT);
		KeyPair other = Signatures.generateKeyPair(SUITE.signature());
		
		AuthenticationException exception = assertThrows(AuthenticationException.class, () -> Sealed.unsealVerified(recipient.getPrivate(), other.getPublic(), artifact));
		assertTrue(exception.getMessage().contains(SUITE.signature().name()));
	}
	
	@Test
	void unsealVerifiedOnUnsignedArtifact() {
		byte[] artifact = Sealed.seal(recipient.getPublic(), new byte[] { 1, 2 });
		MalformedDataException exception = assertThrows(MalformedDataException.class, () -> Sealed.unsealVerified(recipient.getPrivate(), sender.getPublic(), artifact));
		assertEquals("Signed payload is too short to carry a signature length", exception.getMessage());
	}
	
	@Test
	void unsealVerifiedWithShortPayload() {
		for (int length : new int[] { 0, 1, 2, 3 }) {
			byte[] artifact = Sealed.seal(recipient.getPublic(), new byte[length]);
			MalformedDataException exception = assertThrows(MalformedDataException.class, () -> Sealed.unsealVerified(recipient.getPrivate(), sender.getPublic(), artifact));
			assertEquals("Signed payload is too short to carry a signature length", exception.getMessage());
		}
		
		byte[] fourBytes = Sealed.seal(recipient.getPublic(), new byte[4]);
		assertThrows(MalformedDataException.class, () -> Sealed.unsealVerified(recipient.getPrivate(), sender.getPublic(), fourBytes));
	}
	
	@Test
	void unsealVerifiedWithNegativeSignatureLength() {
		byte[] artifact = Sealed.seal(recipient.getPublic(), CryptoBytes.concat(CryptoBytes.of(-1), new byte[10]));
		MalformedDataException exception = assertThrows(MalformedDataException.class, () -> Sealed.unsealVerified(recipient.getPrivate(), sender.getPublic(), artifact));
		assertTrue(exception.getMessage().contains("-1"));
	}
	
	@Test
	void unsealVerifiedWithOversizedSignatureLength() {
		for (int declared : new int[] { Integer.MAX_VALUE, 100 }) {
			byte[] artifact = Sealed.seal(recipient.getPublic(), CryptoBytes.concat(CryptoBytes.of(declared), new byte[10]));
			MalformedDataException exception = assertThrows(MalformedDataException.class, () -> Sealed.unsealVerified(recipient.getPrivate(), sender.getPublic(), artifact));
			assertTrue(exception.getMessage().contains(String.valueOf(declared)));
		}
	}
	
	@Test
	void suiteOfWithNullSealed() {
		assertThrows(NullPointerException.class, () -> Sealed.suiteOf(null));
	}
	
	@Test
	void messageIdOfWithNullSealed() {
		assertThrows(NullPointerException.class, () -> Sealed.messageIdOf(null));
	}
	
	@Test
	void suiteOfWithMalformedArtifact() {
		byte[] artifact = sealed();
		System.arraycopy(CryptoBytes.of((short) 999), 0, artifact, Sealed.MAGIC.length + 1, 2);
		
		assertThrows(MalformedDataException.class, () -> Sealed.suiteOf(new byte[10]));
		assertThrows(MalformedDataException.class, () -> Sealed.suiteOf(flip(sealed(), 0)));
		assertThrows(MalformedDataException.class, () -> Sealed.suiteOf(artifact));
	}
	
	@Test
	void headerWithNullSuite() {
		assertEquals("Suite must not be null", assertThrows(NullPointerException.class, () -> Sealed.header(null, UUID.randomUUID(), new byte[32])).getMessage());
	}
	
	@Test
	void headerWithNullMessageId() {
		assertEquals("Message id must not be null", assertThrows(NullPointerException.class, () -> Sealed.header(SUITE, null, new byte[32])).getMessage());
	}
	
	@Test
	void headerWithNullCommitment() {
		assertEquals("Commitment must not be null", assertThrows(NullPointerException.class, () -> Sealed.header(SUITE, UUID.randomUUID(), null)).getMessage());
	}
	
	@Test
	void deriveMaterialWithNullSuite() {
		try (Secret secret = Secret.random(32)) {
			assertEquals("Suite must not be null", assertThrows(NullPointerException.class, () -> Sealed.deriveMaterial(null, secret, UUID.randomUUID(), new byte[32])).getMessage());
		}
	}
	
	@Test
	void deriveMaterialWithNullSharedSecret() {
		assertEquals("Shared secret must not be null", assertThrows(NullPointerException.class, () -> Sealed.deriveMaterial(SUITE, null, UUID.randomUUID(), new byte[32])).getMessage());
	}
	
	@Test
	void deriveMaterialWithNullMessageId() {
		try (Secret secret = Secret.random(32)) {
			assertEquals("Message id must not be null", assertThrows(NullPointerException.class, () -> Sealed.deriveMaterial(SUITE, secret, null, new byte[32])).getMessage());
		}
	}
	
	@Test
	void deriveMaterialWithNullEncapsulation() {
		try (Secret secret = Secret.random(32)) {
			assertEquals("Encapsulation must not be null", assertThrows(NullPointerException.class, () -> Sealed.deriveMaterial(SUITE, secret, UUID.randomUUID(), null)).getMessage());
		}
	}
	
	@Test
	void deriveMaterialWithClosedSecret() {
		Secret secret = Secret.random(32);
		secret.close();
		assertThrows(IllegalStateException.class, () -> Sealed.deriveMaterial(SUITE, secret, UUID.randomUUID(), new byte[32]));
	}
	
	@Test
	void parseWithNullSealed() {
		assertEquals("Sealed artifact must not be null", assertThrows(NullPointerException.class, () -> Sealed.Head.parse(null)).getMessage());
	}
	
	@Test
	void sealAndUnsealRoundTrip() {
		assertArrayEquals(PLAINTEXT, Sealed.unseal(recipient.getPrivate(), sealed()));
	}
	
	@Test
	void parseWithMinimumHeaderLength() {
		byte[] header = Sealed.header(SUITE, UUID.randomUUID(), CryptoRandom.bytes(32));
		assertEquals(Sealed.HEADER_LENGTH, header.length);
		
		Sealed.Head head = assertDoesNotThrow(() -> Sealed.Head.parse(header));
		assertSame(SUITE, head.suite());
		assertEquals(32, head.commitment().length);
	}
	
	@Test
	void parseWithCorrectMagic() {
		assertDoesNotThrow(() -> Sealed.Head.parse(sealed()));
		assertArrayEquals("LUC1".getBytes(StandardCharsets.US_ASCII), Arrays.copyOf(sealed(), 4));
	}
	
	@Test
	void parseWithCorrectVersion() {
		assertEquals(1, sealed()[Sealed.MAGIC.length]);
		assertEquals(1, Sealed.VERSION);
	}
	
	@Test
	void unsealWithMinimumValidLength() {
		byte[] artifact = Sealed.seal(recipient.getPublic(), new byte[0]);
		int minimum = Sealed.HEADER_LENGTH + SUITE.kem().encapsulationLength() + SUITE.aead().nonceLength() + SUITE.aead().tagLength();
		
		assertEquals(minimum, artifact.length);
		assertEquals(0, assertDoesNotThrow(() -> Sealed.unseal(recipient.getPrivate(), artifact)).length);
	}
	
	@Test
	void unsealWithMatchingCommitment() {
		assertDoesNotThrow(() -> Sealed.unseal(recipient.getPrivate(), sealed()));
	}
	
	@Test
	void aadWithNullAssociatedData() {
		byte[] artifact = Sealed.seal(SUITE, recipient.getPublic(), PLAINTEXT, null);
		assertArrayEquals(PLAINTEXT, Sealed.unseal(recipient.getPrivate(), artifact, null));
		assertArrayEquals(PLAINTEXT, Sealed.unseal(recipient.getPrivate(), artifact, new byte[0]));
	}
	
	@Test
	void aadWithEmptyAssociatedData() {
		byte[] artifact = Sealed.seal(SUITE, recipient.getPublic(), PLAINTEXT, new byte[0]);
		assertArrayEquals(PLAINTEXT, Sealed.unseal(recipient.getPrivate(), artifact, null));
		assertArrayEquals(PLAINTEXT, Sealed.unseal(recipient.getPrivate(), artifact, new byte[0]));
	}
	
	@Test
	void aadWithNonEmptyAssociatedData() {
		byte[] artifact = Sealed.seal(SUITE, recipient.getPublic(), PLAINTEXT, AAD);
		assertArrayEquals(PLAINTEXT, Sealed.unseal(recipient.getPrivate(), artifact, AAD));
		assertThrows(AuthenticationException.class, () -> Sealed.unseal(recipient.getPrivate(), artifact, null));
	}
	
	@Test
	void unsealVerifiedWithValidSignature() {
		byte[] artifact = Sealed.sealSigned(SUITE, recipient.getPublic(), sender.getPrivate(), PLAINTEXT);
		assertArrayEquals(PLAINTEXT, Sealed.unsealVerified(recipient.getPrivate(), sender.getPublic(), artifact));
	}
	
	@Test
	void unsealVerifiedWithZeroLengthSignature() {
		byte[] artifact = Sealed.seal(recipient.getPublic(), CryptoBytes.concat(CryptoBytes.of(0), PLAINTEXT));
		MalformedDataException exception = assertThrows(MalformedDataException.class, () -> Sealed.unsealVerified(recipient.getPrivate(), sender.getPublic(), artifact));
		assertEquals("Malformed hybrid signature", exception.getMessage());
	}
	
	@Test
	void unsealVerifiedWithSignatureFillingThePayload() {
		byte[] artifact = Sealed.seal(recipient.getPublic(), CryptoBytes.concat(CryptoBytes.of(10), new byte[10]));
		assertThrows(AuthenticationException.class, () -> Sealed.unsealVerified(recipient.getPrivate(), sender.getPublic(), artifact));
	}
	
	@Test
	void sealedArtifactLayout() {
		byte[] artifact = sealed();
		
		assertArrayEquals("LUC1".getBytes(StandardCharsets.US_ASCII), Arrays.copyOf(artifact, 4));
		assertEquals(1, artifact[4]);
		assertEquals(SUITE.id(), ByteBuffer.wrap(artifact, 5, 2).getShort());
		assertNotNull(UUIDs.fromBytes(CryptoBytes.slice(artifact, 7, 16)));
		assertEquals(55 + SUITE.kem().encapsulationLength() + SUITE.aead().nonceLength() + PLAINTEXT.length + SUITE.aead().tagLength(), artifact.length);
	}
	
	@Test
	void headerLengthConstant() {
		assertEquals(55, Sealed.HEADER_LENGTH);
		assertEquals(32, Sealed.COMMITMENT_LENGTH);
		assertEquals(4, Sealed.MAGIC.length);
		assertEquals(1, Sealed.VERSION);
	}
	
	@Test
	void sealWithEmptyPlaintext() {
		byte[] artifact = assertDoesNotThrow(() -> Sealed.seal(recipient.getPublic(), new byte[0]));
		
		assertEquals(Sealed.HEADER_LENGTH + SUITE.kem().encapsulationLength() + SUITE.aead().nonceLength() + SUITE.aead().tagLength(), artifact.length);
		assertEquals(0, Sealed.unseal(recipient.getPrivate(), artifact).length);
	}
	
	@Test
	void sealProducesDifferentArtifactsPerCall() {
		byte[] first = sealed();
		byte[] second = sealed();
		
		assertFalse(Arrays.equals(first, second));
		assertNotEquals(Sealed.messageIdOf(first), Sealed.messageIdOf(second));
		assertArrayEquals(PLAINTEXT, Sealed.unseal(recipient.getPrivate(), first));
		assertArrayEquals(PLAINTEXT, Sealed.unseal(recipient.getPrivate(), second));
	}
	
	@Test
	void messageIdIsTimeOrdered() {
		UUID first = Sealed.messageIdOf(sealed());
		UUID second = Sealed.messageIdOf(sealed());
		
		assertEquals(7, first.version());
		assertTrue(Math.abs(UUIDs.unixMillis(first) - System.currentTimeMillis()) < 10_000L);
		assertTrue(UUIDs.unixMillis(second) >= UUIDs.unixMillis(first));
	}
	
	@Test
	void suiteOfReadsWithoutPrivateKey() {
		for (CryptoSuite suite : CryptoSuite.values()) {
			assumeTrue(suite.isSupported());
			KeyPair pair = Kems.generateKeyPair(suite.kem());
			assertSame(suite, Sealed.suiteOf(Sealed.seal(suite, pair.getPublic(), PLAINTEXT, null)), suite.name());
		}
	}
	
	@Test
	void messageIdOfMatchesTheSealedHeader() {
		byte[] artifact = sealed();
		assertEquals(UUIDs.fromBytes(CryptoBytes.slice(artifact, 7, 16)), Sealed.messageIdOf(artifact));
	}
	
	@Test
	void headerLayout() {
		UUID messageId = UUID.randomUUID();
		byte[] commitment = CryptoRandom.bytes(32);
		byte[] header = Sealed.header(CryptoSuite.HYBRID_V1, messageId, commitment);
		
		assertEquals(55, header.length);
		assertArrayEquals("LUC1".getBytes(StandardCharsets.US_ASCII), Arrays.copyOf(header, 4));
		assertEquals(1, header[4]);
		assertEquals(CryptoSuite.HYBRID_V1.id(), ByteBuffer.wrap(header, 5, 2).getShort());
		assertArrayEquals(UUIDs.toBytes(messageId), CryptoBytes.slice(header, 7, 16));
		assertArrayEquals(commitment, CryptoBytes.slice(header, 23, 32));
	}
	
	@Test
	void headerIsBigEndianForSuiteId() {
		byte[] header = Sealed.header(CryptoSuite.HYBRID_V1, UUID.randomUUID(), new byte[32]);
		
		assertEquals((short) 2, CryptoSuite.HYBRID_V1.id());
		assertArrayEquals(new byte[] { 0, 2 }, CryptoBytes.slice(header, 5, 2));
	}
	
	@Test
	void deriveMaterialLength() {
		for (CryptoSuite suite : CryptoSuite.values()) {
			try (Secret shared = Secret.random(32); Secret material = Sealed.deriveMaterial(suite, shared, UUID.randomUUID(), new byte[32])) {
				assertEquals(suite.aead().keyLength() + Sealed.COMMITMENT_LENGTH, material.length(), suite.name());
				assertEquals(64, material.length());
			}
		}
	}
	
	@Test
	void deriveMaterialIsDeterministic() {
		UUID messageId = UUID.randomUUID();
		byte[] encapsulation = CryptoRandom.bytes(32);
		
		try (Secret shared = Secret.random(32)) {
			try (Secret first = Sealed.deriveMaterial(SUITE, shared, messageId, encapsulation); Secret second = Sealed.deriveMaterial(SUITE, shared, messageId, encapsulation)) {
				assertArrayEquals(first.material(), second.material());
			}
		}
	}
	
	@Test
	void sealDoesNotMutateInputs() {
		byte[] plaintext = PLAINTEXT.clone();
		byte[] aad = AAD.clone();
		
		Sealed.seal(SUITE, recipient.getPublic(), plaintext, aad);
		assertArrayEquals(PLAINTEXT, plaintext);
		assertArrayEquals(AAD, aad);
	}
	
	@Test
	void unsealDoesNotMutateInputs() {
		byte[] aad = AAD.clone();
		byte[] artifact = Sealed.seal(SUITE, recipient.getPublic(), PLAINTEXT, aad);
		byte[] copy = artifact.clone();
		
		Sealed.unseal(recipient.getPrivate(), artifact, aad);
		assertArrayEquals(copy, artifact);
		assertArrayEquals(AAD, aad);
	}
	
	@Test
	void roundTripForEverySuite() {
		for (CryptoSuite suite : CryptoSuite.values()) {
			assumeTrue(suite.isSupported());
			KeyPair pair = Kems.generateKeyPair(suite.kem());
			assertArrayEquals(PLAINTEXT, Sealed.unseal(pair.getPrivate(), Sealed.seal(suite, pair.getPublic(), PLAINTEXT, AAD), AAD), suite.name());
		}
	}
	
	@Test
	void roundTripForEveryPlaintextSize() {
		for (int size : new int[] { 0, 1, 15, 16, 17, 1024, 100000 }) {
			byte[] plaintext = CryptoRandom.bytes(size);
			assertArrayEquals(plaintext, Sealed.unseal(recipient.getPrivate(), Sealed.seal(recipient.getPublic(), plaintext)), "size " + size);
		}
	}
	
	@Test
	void tamperingAnyHeaderByteFails() {
		byte[] artifact = Sealed.seal(recipient.getPublic(), new byte[] { 1, 2, 3, 4 });
		int end = Sealed.HEADER_LENGTH + SUITE.kem().encapsulationLength() + SUITE.aead().nonceLength();
		int malformed = 0;
		int authentication = 0;
		
		for (int index = 0; index < end; index++) {
			byte[] corrupted = flip(artifact, index);
			try {
				Sealed.unseal(recipient.getPrivate(), corrupted);
				fail("index " + index + " did not fail");
			} catch (MalformedDataException e) {
				malformed++;
				assertTrue(index < 6, "index " + index);
			} catch (AuthenticationException e) {
				authentication++;
				assertTrue(index >= 6, "index " + index);
			}
		}
		assertEquals(end, malformed + authentication);
		assertEquals(6, malformed);
	}
	
	@Test
	void tamperingAnyCiphertextByteFails() {
		byte[] artifact = Sealed.seal(recipient.getPublic(), new byte[] { 1, 2, 3, 4 });
		int start = Sealed.HEADER_LENGTH + SUITE.kem().encapsulationLength() + SUITE.aead().nonceLength();
		
		for (int i = start; i < artifact.length; i++) {
			int index = i;
			assertThrows(AuthenticationException.class, () -> Sealed.unseal(recipient.getPrivate(), flip(artifact, index)), "index " + index);
		}
		assertThrows(Exception.class, () -> Sealed.unseal(recipient.getPrivate(), Arrays.copyOf(artifact, artifact.length - 1)));
		assertThrows(Exception.class, () -> Sealed.unseal(recipient.getPrivate(), Arrays.copyOf(artifact, artifact.length + 1)));
	}
	
	@Test
	void keyCommitmentPreventsTrialDecryptionConfusion() {
		byte[] artifact = sealed();
		int successes = 0;
		for (int i = 0; i < 20; i++) {
			KeyPair candidate = Kems.generateKeyPair(SUITE.kem());
			AuthenticationException exception = assertThrows(AuthenticationException.class, () -> Sealed.unseal(candidate.getPrivate(), artifact));
			assertEquals("Key commitment mismatch - the header does not belong to this key", exception.getMessage());
		}
		assertArrayEquals(PLAINTEXT, Sealed.unseal(recipient.getPrivate(), artifact));
		assertEquals(0, successes);
	}
	
	@Test
	void commitmentDiffersPerRecipient() {
		byte[] first = Sealed.seal(recipient.getPublic(), PLAINTEXT);
		byte[] second = Sealed.seal(otherRecipient.getPublic(), PLAINTEXT);
		
		assertFalse(Arrays.equals(CryptoBytes.slice(first, 23, 32), CryptoBytes.slice(second, 23, 32)));
	}
	
	@Test
	void commitmentDiffersPerMessage() {
		byte[] first = sealed();
		byte[] second = sealed();
		assertFalse(Arrays.equals(CryptoBytes.slice(first, 23, 32), CryptoBytes.slice(second, 23, 32)));
	}
	
	@Test
	void keyAndCommitmentAreIndependent() {
		UUID messageId = UUID.randomUUID();
		byte[] encapsulation = CryptoRandom.bytes(32);
		
		try (Secret shared = Secret.random(32); Secret material = Sealed.deriveMaterial(SUITE, shared, messageId, encapsulation)) {
			byte[] key = CryptoBytes.slice(material.material(), 0, SUITE.aead().keyLength());
			byte[] commitment = CryptoBytes.slice(material.material(), SUITE.aead().keyLength(), 32);
			
			assertFalse(Arrays.equals(key, commitment));
			assertEquals(32, key.length);
			assertEquals(32, commitment.length);
		}
	}
	
	@Test
	void sealSignedAndUnsealVerifiedRoundTrip() {
		byte[] artifact = Sealed.sealSigned(SUITE, recipient.getPublic(), sender.getPrivate(), PLAINTEXT);
		
		assertArrayEquals(PLAINTEXT, Sealed.unsealVerified(recipient.getPrivate(), sender.getPublic(), artifact));
		assertSame(SUITE, Sealed.suiteOf(artifact));
		assertArrayEquals("LUC1".getBytes(StandardCharsets.US_ASCII), Arrays.copyOf(artifact, 4));
	}
	
	@Test
	void signatureIsInsideTheEncryption() {
		byte[] artifact = Sealed.sealSigned(SUITE, recipient.getPublic(), sender.getPrivate(), PLAINTEXT);
		byte[] envelope = Sealed.unseal(recipient.getPrivate(), artifact);
		
		int signatureLength = ByteBuffer.wrap(envelope, 0, 4).getInt();
		byte[] signature = CryptoBytes.slice(envelope, 4, signatureLength);
		assertArrayEquals(PLAINTEXT, CryptoBytes.slice(envelope, 4 + signatureLength, envelope.length - 4 - signatureLength));
		assertFalse(HexFormat.of().formatHex(artifact).contains(HexFormat.of().formatHex(signature)));
	}
	
	@Test
	void unsealVerifiedRejectsSwappedPlaintext() {
		byte[] artifact = Sealed.sealSigned(SUITE, recipient.getPublic(), sender.getPrivate(), PLAINTEXT);
		byte[] envelope = Sealed.unseal(recipient.getPrivate(), artifact);
		envelope[envelope.length - 1] ^= 1;
		
		byte[] resealed = Sealed.seal(recipient.getPublic(), envelope);
		assertThrows(AuthenticationException.class, () -> Sealed.unsealVerified(recipient.getPrivate(), sender.getPublic(), resealed));
	}
	
	@Test
	void unsealVerifiedUsesTheArtifactsSuiteForVerification() {
		assumeTrue(CryptoSuite.CLASSICAL_V1.isSupported());
		KeyPair classicalRecipient = Kems.generateKeyPair(CryptoSuite.CLASSICAL_V1.kem());
		KeyPair classicalSender = Signatures.generateKeyPair(CryptoSuite.CLASSICAL_V1.signature());
		byte[] artifact = Sealed.sealSigned(CryptoSuite.CLASSICAL_V1, classicalRecipient.getPublic(), classicalSender.getPrivate(), PLAINTEXT);
		
		assertNotSame(CryptoSuite.CLASSICAL_V1, CryptoSuite.current());
		assertSame(SignatureAlgorithm.ED25519, CryptoSuite.CLASSICAL_V1.signature());
		assertArrayEquals(PLAINTEXT, Sealed.unsealVerified(classicalRecipient.getPrivate(), classicalSender.getPublic(), artifact));
	}
	
	@Test
	void sealWithFixedRandomIsDeterministicExceptForTheMessageId() throws Exception {
		byte[] first = Sealed.seal(seeded(), SUITE, recipient.getPublic(), PLAINTEXT, AAD);
		byte[] second = Sealed.seal(seeded(), SUITE, recipient.getPublic(), PLAINTEXT, AAD);
		int nonceOffset = Sealed.HEADER_LENGTH + SUITE.kem().encapsulationLength();
		
		assertFalse(Arrays.equals(first, second));
		assertNotEquals(Sealed.messageIdOf(first), Sealed.messageIdOf(second));
		assertArrayEquals(CryptoBytes.slice(first, nonceOffset, SUITE.aead().nonceLength()), CryptoBytes.slice(second, nonceOffset, SUITE.aead().nonceLength()));
	}
	
	@Test
	void artifactsFromDifferentSuitesAreDistinguishable() {
		Map<CryptoSuite, Integer> lengths = new LinkedHashMap<>();
		for (CryptoSuite suite : CryptoSuite.values()) {
			assumeTrue(suite.isSupported());
			KeyPair pair = Kems.generateKeyPair(suite.kem());
			byte[] artifact = Sealed.seal(suite, pair.getPublic(), PLAINTEXT, null);
			
			assertSame(suite, Sealed.suiteOf(artifact));
			lengths.put(suite, artifact.length);
		}
		assertEquals(3, new HashSet<>(lengths.values()).size());
	}
	
	@Test
	void olderSuiteArtifactStaysReadable() {
		assumeTrue(CryptoSuite.CLASSICAL_V1.isSupported());
		KeyPair classical = Kems.generateKeyPair(CryptoSuite.CLASSICAL_V1.kem());
		byte[] artifact = Sealed.seal(CryptoSuite.CLASSICAL_V1, classical.getPublic(), PLAINTEXT, null);
		
		assertSame(CryptoSuite.CLASSICAL_V1, Sealed.suiteOf(artifact));
		assertNotSame(CryptoSuite.CLASSICAL_V1, CryptoSuite.current());
		assertArrayEquals(PLAINTEXT, Sealed.unseal(classical.getPrivate(), artifact));
	}
	
	@Test
	void secretsAreWipedAfterSealing() {
		for (int i = 0; i < 20; i++) {
			assertArrayEquals(PLAINTEXT, Sealed.unseal(recipient.getPrivate(), sealed()));
		}
		
		try (Secret shared = Secret.random(32)) {
			Secret material = Sealed.deriveMaterial(SUITE, shared, UUID.randomUUID(), new byte[32]);
			assertDoesNotThrow(material::material);
			material.close();
			assertThrows(IllegalStateException.class, material::material);
		}
	}
	
	@Test
	void headEqualsIsIdentityBasedForArrayComponent() {
		UUID messageId = UUID.randomUUID();
		byte[] commitment = new byte[32];
		Sealed.Head first = new Sealed.Head(SUITE, messageId, commitment);
		
		assertNotEquals(new Sealed.Head(SUITE, messageId, commitment.clone()), first);
		assertEquals(new Sealed.Head(SUITE, messageId, commitment), first);
	}
	
	@Test
	void headParseIsDeterministic() {
		byte[] artifact = sealed();
		Sealed.Head first = Sealed.Head.parse(artifact);
		Sealed.Head second = Sealed.Head.parse(artifact);
		
		assertSame(first.suite(), second.suite());
		assertEquals(first.messageId(), second.messageId());
		assertArrayEquals(first.commitment(), second.commitment());
	}
	
	@Test
	void largePlaintextRoundTrip() {
		byte[] plaintext = CryptoRandom.bytes(1 << 20);
		byte[] aad = CryptoRandom.bytes(1 << 16);
		assertArrayEquals(plaintext, Sealed.unseal(recipient.getPrivate(), Sealed.seal(SUITE, recipient.getPublic(), plaintext, aad), aad));
	}
}
