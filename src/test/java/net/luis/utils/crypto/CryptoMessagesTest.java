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

import net.luis.utils.crypto.exception.*;
import net.luis.utils.crypto.key.KeyId;
import net.luis.utils.crypto.key.Secret;
import net.luis.utils.crypto.util.CryptoBytes;
import net.luis.utils.crypto.util.CryptoRandom;
import net.luis.utils.util.UUIDs;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link CryptoMessages}.<br>
 *
 * @author Luis-St
 */
class CryptoMessagesTest {
	
	private static final int MANY_HEADER_LENGTH = 4 + 1 + Short.BYTES + 16 + Short.BYTES;
	private static final byte[] DATA = "the quick brown fox".getBytes(StandardCharsets.UTF_8);
	private static final byte[] AAD = "associated".getBytes(StandardCharsets.UTF_8);
	
	private static final Map<CryptoSuite, KeyPair> RECIPIENTS = new LinkedHashMap<>();
	private static final Map<CryptoSuite, KeyPair> OTHERS = new LinkedHashMap<>();
	private static final Map<CryptoSuite, KeyPair> SENDERS = new LinkedHashMap<>();
	
	@BeforeAll
	static void setUp() {
		Providers.installBouncyCastle();
		for (CryptoSuite suite : CryptoSuite.values()) {
			RECIPIENTS.put(suite, Kems.generateKeyPair(suite.kem()));
			OTHERS.put(suite, Kems.generateKeyPair(suite.kem()));
			SENDERS.put(suite, Signatures.generateKeyPair(suite.signature()));
		}
	}
	
	private static CryptoSuite suite() {
		return CryptoSuite.current();
	}
	
	private static KeyPair recipient() {
		return RECIPIENTS.get(suite());
	}
	
	private static KeyPair other() {
		return OTHERS.get(suite());
	}
	
	private static KeyPair sender() {
		return SENDERS.get(suite());
	}
	
	private static int slotLength(CryptoSuite suite) {
		return 16 + CryptoMessages.COMMITMENT_LENGTH + suite.kem().encapsulationLength() + Short.BYTES + suite.aead().nonceLength() + suite.aead().keyLength() + suite.aead().tagLength();
	}
	
	private static int minimumSlotLength(CryptoSuite suite) {
		return 16 + CryptoMessages.COMMITMENT_LENGTH + suite.kem().encapsulationLength() + Short.BYTES;
	}
	
	private static int wrappedLengthOffset(CryptoSuite suite) {
		return MANY_HEADER_LENGTH + 16 + CryptoMessages.COMMITMENT_LENGTH + suite.kem().encapsulationLength();
	}
	
	private static byte[] streamArtifact() throws Exception {
		ByteArrayOutputStream target = new ByteArrayOutputStream();
		try (CryptoOutputStream sealed = new CryptoOutputStream(recipient().getPublic(), target)) {
			sealed.write(DATA);
		}
		return target.toByteArray();
	}
	
	private static List<KeyPair> pairs(int count) {
		List<KeyPair> pairs = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			pairs.add(Kems.generateKeyPair(suite().kem()));
		}
		return pairs;
	}
	
	private static byte[] material(CryptoSuite suite, Secret shared, UUID messageId, byte[] encapsulation) {
		try (Secret material = CryptoMessages.deriveMaterial(suite, shared, messageId, encapsulation)) {
			return material.material().clone();
		}
	}
	
	private static boolean contains(byte[] haystack, byte[] needle) {
		for (int offset = 0; offset + needle.length <= haystack.length; offset++) {
			if (Arrays.equals(haystack, offset, offset + needle.length, needle, 0, needle.length)) {
				return true;
			}
		}
		return false;
	}
	
	@Test
	void constructorIsPrivate() throws Exception {
		Constructor<?>[] constructors = CryptoMessages.class.getDeclaredConstructors();
		assertEquals(1, constructors.length);
		assertTrue(Modifier.isPrivate(constructors[0].getModifiers()));
		assertTrue(Modifier.isFinal(CryptoMessages.class.getModifiers()));
		
		Constructor<CryptoMessages> constructor = CryptoMessages.class.getDeclaredConstructor();
		constructor.setAccessible(true);
		assertNotNull(constructor.newInstance());
	}
	
	@Test
	void sealWithNullRecipient() {
		assertThrows(NullPointerException.class, () -> CryptoMessages.seal(null, DATA));
	}
	
	@Test
	void sealWithNullPlaintext() {
		assertThrows(NullPointerException.class, () -> CryptoMessages.seal(recipient().getPublic(), null));
	}
	
	@Test
	void sealWithNullSuite() {
		assertThrows(NullPointerException.class, () -> CryptoMessages.seal(null, recipient().getPublic(), DATA, null));
	}
	
	@Test
	void sealWithNullRandom() {
		assertThrows(NullPointerException.class, () -> CryptoMessages.seal(null, suite(), recipient().getPublic(), DATA, null));
	}
	
	@Test
	void sealWithNullRecipientList() {
		assertThrows(NullPointerException.class, () -> CryptoMessages.seal(suite(), (List<PublicKey>) null, DATA, null));
	}
	
	@Test
	void sealManyWithNullSuite() {
		assertThrows(NullPointerException.class, () -> CryptoMessages.seal(null, List.of(recipient().getPublic()), DATA, null));
	}
	
	@Test
	void sealManyWithNullPlaintext() {
		assertThrows(NullPointerException.class, () -> CryptoMessages.seal(suite(), List.of(recipient().getPublic()), null, null));
	}
	
	@Test
	void sealWithEmptyRecipientList() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> CryptoMessages.seal(suite(), List.of(), DATA, null));
		assertTrue(exception.getMessage().contains("At least one recipient"));
	}
	
	@Test
	void sealWithTooManyRecipients() {
		List<PublicKey> recipients = new AbstractList<>() {
			
			@Override
			public PublicKey get(int index) {
				return recipient().getPublic();
			}
			
			@Override
			public int size() {
				return Short.MAX_VALUE + 1;
			}
		};
		
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> CryptoMessages.seal(suite(), recipients, DATA, null));
		assertTrue(exception.getMessage().contains(String.valueOf(Short.MAX_VALUE)));
		assertTrue(exception.getMessage().contains(String.valueOf(Short.MAX_VALUE + 1)));
	}
	
	@Test
	void sealWithNullRecipientInList() {
		List<PublicKey> recipients = Arrays.asList(recipient().getPublic(), null);
		assertThrows(NullPointerException.class, () -> CryptoMessages.seal(suite(), recipients, DATA, null));
	}
	
	@Test
	void unsealWithNullRecipient() {
		byte[] sealed = CryptoMessages.seal(recipient().getPublic(), DATA);
		assertThrows(NullPointerException.class, () -> CryptoMessages.unseal(null, sealed));
	}
	
	@Test
	void unsealWithNullSealed() {
		assertThrows(NullPointerException.class, () -> CryptoMessages.unseal(recipient().getPrivate(), null));
	}
	
	@Test
	void unsealManyWithNullOwnKey() {
		byte[] sealed = CryptoMessages.seal(suite(), List.of(recipient().getPublic()), DATA, null);
		assertThrows(NullPointerException.class, () -> CryptoMessages.unseal(null, recipient().getPrivate(), sealed, null));
	}
	
	@Test
	void unsealManyWithNullRecipient() {
		byte[] sealed = CryptoMessages.seal(suite(), List.of(recipient().getPublic()), DATA, null);
		assertThrows(NullPointerException.class, () -> CryptoMessages.unseal(recipient().getPublic(), null, sealed, null));
	}
	
	@Test
	void unsealManyWithNullSealed() {
		assertThrows(NullPointerException.class, () -> CryptoMessages.unseal(recipient().getPublic(), recipient().getPrivate(), null, null));
	}
	
	@Test
	void unsealWithShortBlob() {
		byte[] sealed = new byte[CryptoMessages.HEADER_LENGTH - 1];
		MalformedDataException exception = assertThrows(MalformedDataException.class, () -> CryptoMessages.unseal(recipient().getPrivate(), sealed));
		assertTrue(exception.getMessage().contains("too short"));
	}
	
	@Test
	void unsealWithBadMagic() {
		byte[] sealed = CryptoMessages.seal(recipient().getPublic(), DATA);
		sealed[0] = 'X';
		
		MalformedDataException exception = assertThrows(MalformedDataException.class, () -> CryptoMessages.unseal(recipient().getPrivate(), sealed));
		assertTrue(exception.getMessage().contains("magic"));
	}
	
	@Test
	void unsealWithUnsupportedVersion() {
		byte[] sealed = CryptoMessages.seal(recipient().getPublic(), DATA);
		sealed[4] = 9;
		
		MalformedDataException exception = assertThrows(MalformedDataException.class, () -> CryptoMessages.unseal(recipient().getPrivate(), sealed));
		assertTrue(exception.getMessage().contains("9"));
	}
	
	@Test
	void unsealWithUnknownSuiteId() {
		byte[] sealed = CryptoMessages.seal(recipient().getPublic(), DATA);
		sealed[5] = 0x7F;
		sealed[6] = (byte) 0xFF;
		
		assertThrows(MalformedDataException.class, () -> CryptoMessages.unseal(recipient().getPrivate(), sealed));
	}
	
	@Test
	void unsealWithBlobBelowMinimumLength() {
		byte[] sealed = Arrays.copyOf(CryptoMessages.seal(recipient().getPublic(), DATA), CryptoMessages.HEADER_LENGTH + 4);
		MalformedDataException exception = assertThrows(MalformedDataException.class, () -> CryptoMessages.unseal(recipient().getPrivate(), sealed));
		assertTrue(exception.getMessage().contains("Sealed blob too short"));
	}
	
	@Test
	void unsealWithWrongKey() {
		byte[] sealed = CryptoMessages.seal(recipient().getPublic(), DATA);
		AuthenticationException exception = assertThrows(AuthenticationException.class, () -> CryptoMessages.unseal(other().getPrivate(), sealed));
		assertTrue(exception.getMessage().contains("commitment"));
	}
	
	@Test
	void unsealWithTamperedCiphertext() {
		byte[] sealed = CryptoMessages.seal(recipient().getPublic(), DATA);
		sealed[sealed.length - 1] ^= 0x01;
		
		assertThrows(AuthenticationException.class, () -> CryptoMessages.unseal(recipient().getPrivate(), sealed));
	}
	
	@Test
	void unsealWithTamperedNonce() {
		byte[] sealed = CryptoMessages.seal(recipient().getPublic(), DATA);
		sealed[CryptoMessages.HEADER_LENGTH + suite().kem().encapsulationLength()] ^= 0x01;
		
		assertThrows(AuthenticationException.class, () -> CryptoMessages.unseal(recipient().getPrivate(), sealed));
	}
	
	@Test
	void unsealWithTamperedEncapsulation() {
		byte[] sealed = CryptoMessages.seal(recipient().getPublic(), DATA);
		sealed[CryptoMessages.HEADER_LENGTH] ^= 0x01;
		
		assertThrows(CryptoException.class, () -> CryptoMessages.unseal(recipient().getPrivate(), sealed));
	}
	
	@Test
	void unsealWithWrongAssociatedData() {
		byte[] sealed = CryptoMessages.seal(suite(), recipient().getPublic(), DATA, AAD);
		assertThrows(AuthenticationException.class, () -> CryptoMessages.unseal(recipient().getPrivate(), sealed, "other".getBytes(StandardCharsets.UTF_8)));
	}
	
	@Test
	void unsealMultiRecipientBlobWithSingleOverload() {
		byte[] sealed = CryptoMessages.seal(suite(), List.of(recipient().getPublic()), DATA, null);
		assertThrows(MalformedDataException.class, () -> CryptoMessages.unseal(recipient().getPrivate(), sealed));
	}
	
	@Test
	void unsealStreamArtifactWithSingleOverload() throws Exception {
		byte[] sealed = streamArtifact();
		assertThrows(MalformedDataException.class, () -> CryptoMessages.unseal(recipient().getPrivate(), sealed));
	}
	
	@Test
	void unsealManyWithForeignKey() {
		byte[] sealed = CryptoMessages.seal(suite(), List.of(recipient().getPublic()), DATA, null);
		AuthenticationException exception = assertThrows(AuthenticationException.class, () -> CryptoMessages.unseal(other().getPublic(), other().getPrivate(), sealed, null));
		assertEquals("No recipient slot matches this key", exception.getMessage());
	}
	
	@Test
	void unsealManyWithMismatchedKeyPair() {
		byte[] sealed = CryptoMessages.seal(suite(), List.of(recipient().getPublic()), DATA, null);
		assertThrows(AuthenticationException.class, () -> CryptoMessages.unseal(recipient().getPublic(), other().getPrivate(), sealed, null));
	}
	
	@Test
	void unsealManyWithSingleRecipientBlob() {
		byte[] sealed = CryptoMessages.seal(recipient().getPublic(), DATA);
		assertThrows(MalformedDataException.class, () -> CryptoMessages.unseal(recipient().getPublic(), recipient().getPrivate(), sealed, null));
	}
	
	@Test
	void unsealManyWithShortBlob() {
		byte[] sealed = new byte[MANY_HEADER_LENGTH - 1];
		assertThrows(MalformedDataException.class, () -> CryptoMessages.unseal(recipient().getPublic(), recipient().getPrivate(), sealed, null));
	}
	
	@Test
	void unsealManyWithBadVersion() {
		byte[] sealed = CryptoMessages.seal(suite(), List.of(recipient().getPublic()), DATA, null);
		sealed[4] = 9;
		
		assertThrows(MalformedDataException.class, () -> CryptoMessages.unseal(recipient().getPublic(), recipient().getPrivate(), sealed, null));
	}
	
	@Test
	void unsealManyWithUnknownSuiteId() {
		byte[] sealed = CryptoMessages.seal(suite(), List.of(recipient().getPublic()), DATA, null);
		sealed[5] = 0x7F;
		sealed[6] = (byte) 0xFF;
		
		assertThrows(MalformedDataException.class, () -> CryptoMessages.unseal(recipient().getPublic(), recipient().getPrivate(), sealed, null));
	}
	
	@Test
	void unsealManyWithImplausibleRecipientCount() {
		byte[] sealed = CryptoMessages.seal(suite(), List.of(recipient().getPublic()), DATA, null);
		ByteBuffer.wrap(sealed, MANY_HEADER_LENGTH - Short.BYTES, Short.BYTES).putShort((short) 0x7FFF);
		
		MalformedDataException exception = assertThrows(MalformedDataException.class, () -> CryptoMessages.unseal(recipient().getPublic(), recipient().getPrivate(), sealed, null));
		assertTrue(exception.getMessage().contains("32767"));
	}
	
	@Test
	void unsealManyWithTruncatedSlot() {
		byte[] sealed = CryptoMessages.seal(suite(), List.of(recipient().getPublic(), other().getPublic()), DATA, null);
		byte[] truncated = Arrays.copyOf(sealed, MANY_HEADER_LENGTH + 2 * minimumSlotLength(suite()));
		
		MalformedDataException exception = assertThrows(MalformedDataException.class, () -> CryptoMessages.unseal(recipient().getPublic(), recipient().getPrivate(), truncated, null));
		assertTrue(exception.getMessage().contains("Truncated recipient slot 1"));
	}
	
	@Test
	void unsealManyWithTruncatedWrappedKey() {
		byte[] sealed = CryptoMessages.seal(suite(), List.of(recipient().getPublic()), DATA, null);
		byte[] truncated = Arrays.copyOf(sealed, wrappedLengthOffset(suite()) + Short.BYTES + 4);
		
		MalformedDataException exception = assertThrows(MalformedDataException.class, () -> CryptoMessages.unseal(recipient().getPublic(), recipient().getPrivate(), truncated, null));
		assertTrue(exception.getMessage().contains("Truncated wrapped content key"));
	}
	
	@Test
	void unsealManyWithoutBody() {
		byte[] sealed = CryptoMessages.seal(suite(), List.of(recipient().getPublic()), DATA, null);
		byte[] truncated = Arrays.copyOf(sealed, MANY_HEADER_LENGTH + slotLength(suite()));
		
		MalformedDataException exception = assertThrows(MalformedDataException.class, () -> CryptoMessages.unseal(recipient().getPublic(), recipient().getPrivate(), truncated, null));
		assertTrue(exception.getMessage().contains("no readable body"));
	}
	
	@Test
	void unsealManyWithTamperedSlotRegion() {
		byte[] sealed = CryptoMessages.seal(suite(), List.of(other().getPublic(), recipient().getPublic()), DATA, null);
		sealed[MANY_HEADER_LENGTH + 16 + 4] ^= 0x01;
		
		assertThrows(CryptoException.class, () -> CryptoMessages.unseal(recipient().getPublic(), recipient().getPrivate(), sealed, null));
	}
	
	@Test
	void unsealManyWithTamperedOwnSlot() {
		byte[] sealed = CryptoMessages.seal(suite(), List.of(recipient().getPublic()), DATA, null);
		sealed[wrappedLengthOffset(suite()) + Short.BYTES + 5] ^= 0x01;
		
		AuthenticationException exception = assertThrows(AuthenticationException.class, () -> CryptoMessages.unseal(recipient().getPublic(), recipient().getPrivate(), sealed, null));
		assertEquals("No recipient slot matches this key", exception.getMessage());
	}
	
	@Test
	void unsealManyWithShortenedWrappedLength() {
		byte[] sealed = CryptoMessages.seal(suite(), List.of(recipient().getPublic()), DATA, null);
		ByteBuffer.wrap(sealed, wrappedLengthOffset(suite()), Short.BYTES).putShort((short) 5);
		
		MalformedDataException exception = assertThrows(MalformedDataException.class, () -> CryptoMessages.unseal(recipient().getPublic(), recipient().getPrivate(), sealed, null));
		assertTrue(exception.getMessage().contains("Wrapped content key too short"));
	}
	
	@Test
	void sealSignedWithNullSuite() {
		assertThrows(NullPointerException.class, () -> CryptoMessages.sealSigned(null, recipient().getPublic(), sender().getPrivate(), DATA));
	}
	
	@Test
	void sealSignedWithNullSender() {
		assertThrows(NullPointerException.class, () -> CryptoMessages.sealSigned(suite(), recipient().getPublic(), null, DATA));
	}
	
	@Test
	void sealSignedWithNullPlaintext() {
		assertThrows(NullPointerException.class, () -> CryptoMessages.sealSigned(suite(), recipient().getPublic(), sender().getPrivate(), null));
	}
	
	@Test
	void sealSignedWithNullRecipient() {
		assertThrows(NullPointerException.class, () -> CryptoMessages.sealSigned(suite(), null, sender().getPrivate(), DATA));
	}
	
	@Test
	void unsealVerifiedWithNullRecipient() {
		byte[] sealed = CryptoMessages.sealSigned(suite(), recipient().getPublic(), sender().getPrivate(), DATA);
		assertThrows(NullPointerException.class, () -> CryptoMessages.unsealVerified(null, sender().getPublic(), sealed));
	}
	
	@Test
	void unsealVerifiedWithNullSender() {
		byte[] sealed = CryptoMessages.sealSigned(suite(), recipient().getPublic(), sender().getPrivate(), DATA);
		assertThrows(NullPointerException.class, () -> CryptoMessages.unsealVerified(recipient().getPrivate(), null, sealed));
	}
	
	@Test
	void unsealVerifiedWithNullSealed() {
		assertThrows(NullPointerException.class, () -> CryptoMessages.unsealVerified(recipient().getPrivate(), sender().getPublic(), null));
	}
	
	@Test
	void unsealVerifiedWithWrongSender() {
		byte[] sealed = CryptoMessages.sealSigned(suite(), recipient().getPublic(), sender().getPrivate(), DATA);
		PublicKey foreign = Signatures.generateKeyPair(suite().signature()).getPublic();
		
		assertThrows(AuthenticationException.class, () -> CryptoMessages.unsealVerified(recipient().getPrivate(), foreign, sealed));
	}
	
	@Test
	void unsealVerifiedWithUnsignedPayload() {
		byte[] sealed = CryptoMessages.seal(recipient().getPublic(), new byte[] { 1, 2, 3, 4 });
		MalformedDataException exception = assertThrows(MalformedDataException.class, () -> CryptoMessages.unsealVerified(recipient().getPrivate(), sender().getPublic(), sealed));
		assertTrue(exception.getMessage().contains("does not fit"));
	}
	
	@Test
	void unsealVerifiedWithPayloadShorterThanLengthPrefix() {
		byte[] sealed = CryptoMessages.seal(recipient().getPublic(), new byte[] { 1, 2 });
		MalformedDataException exception = assertThrows(MalformedDataException.class, () -> CryptoMessages.unsealVerified(recipient().getPrivate(), sender().getPublic(), sealed));
		assertTrue(exception.getMessage().contains("too short to carry a signature length"));
	}
	
	@Test
	void unsealVerifiedWithNegativeSignatureLength() {
		byte[] payload = CryptoBytes.concat(new byte[] { -1, -1, -1, -1 }, DATA);
		byte[] sealed = CryptoMessages.seal(recipient().getPublic(), payload);
		
		assertThrows(MalformedDataException.class, () -> CryptoMessages.unsealVerified(recipient().getPrivate(), sender().getPublic(), sealed));
	}
	
	@Test
	void unsealVerifiedWithOversizedSignatureLength() {
		byte[] payload = CryptoBytes.concat(CryptoBytes.of(500), DATA);
		byte[] sealed = CryptoMessages.seal(recipient().getPublic(), payload);
		
		MalformedDataException exception = assertThrows(MalformedDataException.class, () -> CryptoMessages.unsealVerified(recipient().getPrivate(), sender().getPublic(), sealed));
		assertTrue(exception.getMessage().contains("500"));
		assertTrue(exception.getMessage().contains(String.valueOf(DATA.length)));
	}
	
	@Test
	void unsealVerifiedWithZeroSignatureLength() {
		CryptoSuite classical = CryptoSuite.CLASSICAL_V1;
		byte[] payload = CryptoBytes.concat(CryptoBytes.of(0), DATA);
		byte[] sealed = CryptoMessages.seal(classical, RECIPIENTS.get(classical).getPublic(), payload, null);
		
		assertThrows(AuthenticationException.class, () -> CryptoMessages.unsealVerified(RECIPIENTS.get(classical).getPrivate(), SENDERS.get(classical).getPublic(), sealed));
	}
	
	@Test
	void unsealVerifiedWithTamperedSignature() {
		byte[] sealed = CryptoMessages.sealSigned(suite(), recipient().getPublic(), sender().getPrivate(), DATA);
		byte[] opened = CryptoMessages.unseal(recipient().getPrivate(), sealed);
		opened[8] ^= 0x01;
		byte[] resealed = CryptoMessages.seal(recipient().getPublic(), opened);
		
		assertThrows(AuthenticationException.class, () -> CryptoMessages.unsealVerified(recipient().getPrivate(), sender().getPublic(), resealed));
	}
	
	@Test
	void suiteOfWithNullSealed() {
		assertThrows(NullPointerException.class, () -> CryptoMessages.suiteOf(null));
	}
	
	@Test
	void messageIdOfWithNullSealed() {
		assertThrows(NullPointerException.class, () -> CryptoMessages.messageIdOf(null));
	}
	
	@Test
	void recipientsOfWithNullSealed() {
		assertThrows(NullPointerException.class, () -> CryptoMessages.recipientsOf(null));
	}
	
	@Test
	void suiteOfGarbage() {
		byte[] garbage = new byte[CryptoMessages.HEADER_LENGTH];
		Arrays.fill(garbage, (byte) 0x42);
		assertThrows(MalformedDataException.class, () -> CryptoMessages.suiteOf(garbage));
	}
	
	@Test
	void messageIdOfGarbage() {
		byte[] garbage = new byte[CryptoMessages.HEADER_LENGTH];
		Arrays.fill(garbage, (byte) 0x42);
		assertThrows(MalformedDataException.class, () -> CryptoMessages.messageIdOf(garbage));
	}
	
	@Test
	void suiteOfWithBlobShorterThanMagic() {
		assertThrows(MalformedDataException.class, () -> CryptoMessages.suiteOf(new byte[0]));
		assertThrows(MalformedDataException.class, () -> CryptoMessages.suiteOf(new byte[3]));
		assertThrows(MalformedDataException.class, () -> CryptoMessages.messageIdOf(new byte[0]));
		assertThrows(MalformedDataException.class, () -> CryptoMessages.messageIdOf(new byte[3]));
	}
	
	@Test
	void recipientsOfSingleRecipientBlob() {
		byte[] sealed = CryptoMessages.seal(recipient().getPublic(), DATA);
		assertThrows(MalformedDataException.class, () -> CryptoMessages.recipientsOf(sealed));
	}
	
	@Test
	void headerWithNullMagic() {
		assertThrows(NullPointerException.class, () -> CryptoMessages.header(null, suite(), UUIDs.v7(), new byte[CryptoMessages.COMMITMENT_LENGTH]));
	}
	
	@Test
	void headerWithNullSuite() {
		assertThrows(NullPointerException.class, () -> CryptoMessages.header(CryptoMessages.MAGIC, null, UUIDs.v7(), new byte[CryptoMessages.COMMITMENT_LENGTH]));
	}
	
	@Test
	void headerWithNullMessageId() {
		assertThrows(NullPointerException.class, () -> CryptoMessages.header(CryptoMessages.MAGIC, suite(), null, new byte[CryptoMessages.COMMITMENT_LENGTH]));
	}
	
	@Test
	void headerWithNullCommitment() {
		assertThrows(NullPointerException.class, () -> CryptoMessages.header(CryptoMessages.MAGIC, suite(), UUIDs.v7(), null));
	}
	
	@Test
	void deriveMaterialWithNullSuite() {
		try (Secret shared = Secret.random(32)) {
			assertThrows(NullPointerException.class, () -> CryptoMessages.deriveMaterial(null, shared, UUIDs.v7(), new byte[4]));
		}
	}
	
	@Test
	void deriveMaterialWithNullSharedSecret() {
		assertThrows(NullPointerException.class, () -> CryptoMessages.deriveMaterial(suite(), null, UUIDs.v7(), new byte[4]));
	}
	
	@Test
	void deriveMaterialWithNullMessageId() {
		try (Secret shared = Secret.random(32)) {
			assertThrows(NullPointerException.class, () -> CryptoMessages.deriveMaterial(suite(), shared, null, new byte[4]));
		}
	}
	
	@Test
	void deriveMaterialWithNullEncapsulation() {
		try (Secret shared = Secret.random(32)) {
			assertThrows(NullPointerException.class, () -> CryptoMessages.deriveMaterial(suite(), shared, UUIDs.v7(), null));
		}
	}
	
	@Test
	void deriveMaterialWithClosedSecret() {
		Secret shared = Secret.random(32);
		shared.close();
		
		assertThrows(IllegalStateException.class, () -> CryptoMessages.deriveMaterial(suite(), shared, UUIDs.v7(), new byte[4]));
	}
	
	@Test
	void headParseWithNullSealed() {
		assertThrows(NullPointerException.class, () -> CryptoMessages.Head.parse(null, CryptoMessages.MAGIC));
	}
	
	@Test
	void headParseWithNullMagic() {
		byte[] sealed = CryptoMessages.seal(recipient().getPublic(), DATA);
		assertThrows(NullPointerException.class, () -> CryptoMessages.Head.parse(sealed, null));
	}
	
	@Test
	void headConstructWithNullSuite() {
		assertThrows(NullPointerException.class, () -> new CryptoMessages.Head(null, UUIDs.v7(), new byte[CryptoMessages.COMMITMENT_LENGTH]));
	}
	
	@Test
	void headConstructWithNullMessageId() {
		assertThrows(NullPointerException.class, () -> new CryptoMessages.Head(suite(), null, new byte[CryptoMessages.COMMITMENT_LENGTH]));
	}
	
	@Test
	void headConstructWithNullCommitment() {
		assertThrows(NullPointerException.class, () -> new CryptoMessages.Head(suite(), UUIDs.v7(), null));
	}
	
	@Test
	void sealAndUnsealRoundTrip() {
		byte[] sealed = CryptoMessages.seal(recipient().getPublic(), DATA);
		assertArrayEquals(DATA, CryptoMessages.unseal(recipient().getPrivate(), sealed));
		assertArrayEquals(DATA, CryptoMessages.unseal(recipient().getPrivate(), sealed, null));
	}
	
	@Test
	void sealWithNullAssociatedData() {
		byte[] sealed = CryptoMessages.seal(suite(), recipient().getPublic(), DATA, null);
		assertArrayEquals(DATA, CryptoMessages.unseal(recipient().getPrivate(), sealed, new byte[0]));
	}
	
	@Test
	void sealWithEmptyAssociatedData() {
		byte[] sealed = CryptoMessages.seal(suite(), recipient().getPublic(), DATA, new byte[0]);
		assertArrayEquals(DATA, CryptoMessages.unseal(recipient().getPrivate(), sealed, null));
	}
	
	@Test
	void sealWithAssociatedData() {
		byte[] sealed = CryptoMessages.seal(suite(), recipient().getPublic(), DATA, AAD);
		assertArrayEquals(DATA, CryptoMessages.unseal(recipient().getPrivate(), sealed, AAD));
		assertThrows(AuthenticationException.class, () -> CryptoMessages.unseal(recipient().getPrivate(), sealed, "different".getBytes(StandardCharsets.UTF_8)));
	}
	
	@Test
	void sealWithEmptyPlaintext() {
		byte[] sealed = CryptoMessages.seal(recipient().getPublic(), new byte[0]);
		int expected = CryptoMessages.HEADER_LENGTH + suite().kem().encapsulationLength() + suite().aead().nonceLength() + suite().aead().tagLength();
		
		assertEquals(expected, sealed.length);
		assertArrayEquals(new byte[0], CryptoMessages.unseal(recipient().getPrivate(), sealed));
	}
	
	@Test
	void sealForSingleRecipientList() {
		byte[] sealed = CryptoMessages.seal(suite(), List.of(recipient().getPublic()), DATA, null);
		
		assertArrayEquals(DATA, CryptoMessages.unseal(recipient().getPublic(), recipient().getPrivate(), sealed, null));
		assertEquals(1, CryptoMessages.recipientsOf(sealed).size());
	}
	
	@Test
	void sealForTwoRecipients() {
		byte[] sealed = CryptoMessages.seal(suite(), List.of(recipient().getPublic(), other().getPublic()), DATA, null);
		
		assertArrayEquals(DATA, CryptoMessages.unseal(recipient().getPublic(), recipient().getPrivate(), sealed, null));
		assertArrayEquals(DATA, CryptoMessages.unseal(other().getPublic(), other().getPrivate(), sealed, null));
	}
	
	@Test
	void sealForManyRecipients() {
		List<KeyPair> pairs = pairs(8);
		byte[] sealed = CryptoMessages.seal(suite(), pairs.stream().map(KeyPair::getPublic).toList(), DATA, null);
		KeyPair last = pairs.getLast();
		
		assertArrayEquals(DATA, CryptoMessages.unseal(last.getPublic(), last.getPrivate(), sealed, null));
	}
	
	@Test
	void unsealManyWithDuplicateRecipient() {
		byte[] sealed = CryptoMessages.seal(suite(), List.of(recipient().getPublic(), recipient().getPublic()), DATA, null);
		
		assertEquals(2, CryptoMessages.recipientsOf(sealed).size());
		assertArrayEquals(DATA, CryptoMessages.unseal(recipient().getPublic(), recipient().getPrivate(), sealed, null));
	}
	
	@Test
	void sealManyWithNullAssociatedData() {
		byte[] sealed = CryptoMessages.seal(suite(), List.of(recipient().getPublic()), DATA, null);
		assertArrayEquals(DATA, CryptoMessages.unseal(recipient().getPublic(), recipient().getPrivate(), sealed, new byte[0]));
	}
	
	@Test
	void sealManyWithAssociatedData() {
		byte[] sealed = CryptoMessages.seal(suite(), List.of(recipient().getPublic()), DATA, AAD);
		
		assertArrayEquals(DATA, CryptoMessages.unseal(recipient().getPublic(), recipient().getPrivate(), sealed, AAD));
		assertThrows(AuthenticationException.class, () -> CryptoMessages.unseal(recipient().getPublic(), recipient().getPrivate(), sealed, "different".getBytes(StandardCharsets.UTF_8)));
	}
	
	@Test
	void recipientsOfWithZeroRecipientCount() {
		byte[] sealed = CryptoMessages.seal(suite(), List.of(recipient().getPublic()), DATA, null);
		ByteBuffer.wrap(sealed, MANY_HEADER_LENGTH - Short.BYTES, Short.BYTES).putShort((short) 0);
		
		assertEquals(List.of(), CryptoMessages.recipientsOf(sealed));
		AuthenticationException exception = assertThrows(AuthenticationException.class, () -> CryptoMessages.unseal(recipient().getPublic(), recipient().getPrivate(), sealed, null));
		assertEquals("No recipient slot matches this key", exception.getMessage());
	}
	
	@Test
	void sealSignedAndUnsealVerifiedRoundTrip() {
		byte[] sealed = CryptoMessages.sealSigned(suite(), recipient().getPublic(), sender().getPrivate(), DATA);
		assertArrayEquals(DATA, CryptoMessages.unsealVerified(recipient().getPrivate(), sender().getPublic(), sealed));
	}
	
	@Test
	void unsealVerifiedWithEmptyPlaintext() {
		byte[] sealed = CryptoMessages.sealSigned(suite(), recipient().getPublic(), sender().getPrivate(), new byte[0]);
		assertArrayEquals(new byte[0], CryptoMessages.unsealVerified(recipient().getPrivate(), sender().getPublic(), sealed));
	}
	
	@Test
	void suiteOfSingleRecipientBlob() {
		assertEquals(suite(), CryptoMessages.suiteOf(CryptoMessages.seal(recipient().getPublic(), DATA)));
	}
	
	@Test
	void suiteOfMultiRecipientBlob() {
		assertEquals(suite(), CryptoMessages.suiteOf(CryptoMessages.seal(suite(), List.of(recipient().getPublic()), DATA, null)));
	}
	
	@Test
	void suiteOfStreamArtifact() throws Exception {
		assertEquals(suite(), CryptoMessages.suiteOf(streamArtifact()));
	}
	
	@Test
	void messageIdOfSingleRecipientBlob() {
		long before = System.currentTimeMillis();
		UUID messageId = CryptoMessages.messageIdOf(CryptoMessages.seal(recipient().getPublic(), DATA));
		
		assertEquals(7, messageId.version());
		assertTrue(UUIDs.unixMillis(messageId) >= before - 1000);
	}
	
	@Test
	void messageIdOfMultiRecipientBlob() {
		UUID messageId = CryptoMessages.messageIdOf(CryptoMessages.seal(suite(), List.of(recipient().getPublic()), DATA, null));
		assertEquals(7, messageId.version());
	}
	
	@Test
	void messageIdOfStreamArtifact() throws Exception {
		assertEquals(7, CryptoMessages.messageIdOf(streamArtifact()).version());
	}
	
	@Test
	void recipientsOfMultiRecipientBlob() {
		List<PublicKey> keys = List.of(recipient().getPublic(), other().getPublic());
		byte[] sealed = CryptoMessages.seal(suite(), keys, DATA, null);
		
		assertEquals(keys.stream().map(KeyId::of).toList(), CryptoMessages.recipientsOf(sealed));
	}
	
	@Test
	void headParseWithStreamMagic() throws Exception {
		byte[] artifact = streamArtifact();
		assertEquals(suite(), CryptoMessages.Head.parse(artifact, CryptoMessages.STREAM_MAGIC).suite());
		assertThrows(MalformedDataException.class, () -> CryptoMessages.Head.parse(artifact, CryptoMessages.MAGIC));
	}
	
	@Test
	void headerLayout() {
		UUID messageId = UUIDs.v7();
		byte[] commitment = CryptoRandom.bytes(CryptoMessages.COMMITMENT_LENGTH);
		byte[] header = CryptoMessages.header(CryptoMessages.MAGIC, suite(), messageId, commitment);
		
		assertEquals(CryptoMessages.HEADER_LENGTH, header.length);
		assertArrayEquals(CryptoMessages.MAGIC, Arrays.copyOf(header, 4));
		assertEquals(CryptoMessages.VERSION, header[4]);
		assertEquals(suite().id(), ByteBuffer.wrap(header, 5, Short.BYTES).getShort());
		assertEquals(messageId, UUIDs.fromBytes(Arrays.copyOfRange(header, 7, 23)));
		assertArrayEquals(commitment, Arrays.copyOfRange(header, 23, CryptoMessages.HEADER_LENGTH));
	}
	
	@Test
	void deriveMaterialLength() {
		for (CryptoSuite suite : CryptoSuite.values()) {
			try (Secret shared = Secret.random(32); Secret material = CryptoMessages.deriveMaterial(suite, shared, UUIDs.v7(), new byte[8])) {
				assertEquals(suite.aead().keyLength() + CryptoMessages.COMMITMENT_LENGTH, material.material().length);
			}
		}
	}
	
	@Test
	void deriveMaterialIsDeterministic() {
		UUID messageId = UUIDs.v7();
		byte[] encapsulation = CryptoRandom.bytes(8);
		try (Secret shared = Secret.random(32)) {
			byte[] first = material(suite(), shared, messageId, encapsulation);
			
			assertArrayEquals(first, material(suite(), shared, messageId, encapsulation));
			assertFalse(Arrays.equals(first, material(suite(), shared, UUIDs.v7(), encapsulation)));
			assertFalse(Arrays.equals(first, material(suite(), shared, messageId, CryptoRandom.bytes(8))));
			assertFalse(Arrays.equals(first, material(CryptoSuite.POST_QUANTUM_V1, shared, messageId, encapsulation)));
		}
	}
	
	@Test
	void sealProducesMagicAndVersion() {
		byte[] sealed = CryptoMessages.seal(recipient().getPublic(), DATA);
		
		assertArrayEquals("LUC1".getBytes(StandardCharsets.US_ASCII), Arrays.copyOf(sealed, 4));
		assertEquals(CryptoMessages.VERSION, sealed[4]);
		assertEquals(suite(), CryptoSuite.byId(ByteBuffer.wrap(sealed, 5, Short.BYTES).getShort()));
	}
	
	@Test
	void sealManyProducesMagicAndVersion() {
		byte[] sealed = CryptoMessages.seal(suite(), List.of(recipient().getPublic()), DATA, null);
		
		assertArrayEquals("LUCM".getBytes(StandardCharsets.US_ASCII), Arrays.copyOf(sealed, 4));
		assertEquals(CryptoMessages.VERSION, sealed[4]);
	}
	
	@Test
	void sealArtifactLengthFollowsSuite() {
		for (CryptoSuite suite : CryptoSuite.values()) {
			byte[] sealed = CryptoMessages.seal(suite, RECIPIENTS.get(suite).getPublic(), DATA, null);
			int expected = CryptoMessages.HEADER_LENGTH + suite.kem().encapsulationLength() + suite.aead().nonceLength() + DATA.length + suite.aead().tagLength();
			assertEquals(expected, sealed.length);
		}
	}
	
	@Test
	void sealUsesGivenRandomForNonce() {
		byte[] sealed = CryptoMessages.seal(new FixedRandom(), suite(), recipient().getPublic(), DATA, null);
		int start = CryptoMessages.HEADER_LENGTH + suite().kem().encapsulationLength();
		byte[] expected = new byte[suite().aead().nonceLength()];
		Arrays.fill(expected, (byte) 7);
		
		assertArrayEquals(expected, Arrays.copyOfRange(sealed, start, start + expected.length));
	}
	
	@Test
	void sealTwiceProducesDifferentArtifacts() {
		byte[] first = CryptoMessages.seal(recipient().getPublic(), DATA);
		byte[] second = CryptoMessages.seal(recipient().getPublic(), DATA);
		
		assertFalse(Arrays.equals(first, second));
		assertArrayEquals(CryptoMessages.unseal(recipient().getPrivate(), first), CryptoMessages.unseal(recipient().getPrivate(), second));
	}
	
	@Test
	void sealForEverySuite() {
		for (CryptoSuite suite : CryptoSuite.values()) {
			byte[] sealed = CryptoMessages.seal(suite, RECIPIENTS.get(suite).getPublic(), DATA, null);
			assertArrayEquals(DATA, CryptoMessages.unseal(RECIPIENTS.get(suite).getPrivate(), sealed));
			assertEquals(suite, CryptoMessages.suiteOf(sealed));
		}
	}
	
	@Test
	void sealManyForEverySuite() {
		for (CryptoSuite suite : CryptoSuite.values()) {
			List<PublicKey> keys = List.of(RECIPIENTS.get(suite).getPublic(), OTHERS.get(suite).getPublic());
			byte[] sealed = CryptoMessages.seal(suite, keys, DATA, null);
			
			assertArrayEquals(DATA, CryptoMessages.unseal(RECIPIENTS.get(suite).getPublic(), RECIPIENTS.get(suite).getPrivate(), sealed, null));
			assertArrayEquals(DATA, CryptoMessages.unseal(OTHERS.get(suite).getPublic(), OTHERS.get(suite).getPrivate(), sealed, null));
		}
	}
	
	@Test
	void sealLargePlaintext() {
		byte[] plaintext = CryptoRandom.bytes(2 * 1024 * 1024);
		byte[] sealed = CryptoMessages.seal(recipient().getPublic(), plaintext);
		
		assertArrayEquals(plaintext, CryptoMessages.unseal(recipient().getPrivate(), sealed));
	}
	
	@Test
	void unsealIsRepeatable() {
		byte[] sealed = CryptoMessages.seal(recipient().getPublic(), DATA);
		byte[] copy = sealed.clone();
		
		assertArrayEquals(DATA, CryptoMessages.unseal(recipient().getPrivate(), sealed));
		assertArrayEquals(DATA, CryptoMessages.unseal(recipient().getPrivate(), sealed));
		assertArrayEquals(copy, sealed);
	}
	
	@Test
	void sealDoesNotMutateInput() {
		byte[] plaintext = DATA.clone();
		byte[] associatedData = AAD.clone();
		CryptoMessages.seal(suite(), recipient().getPublic(), plaintext, associatedData);
		
		assertArrayEquals(DATA, plaintext);
		assertArrayEquals(AAD, associatedData);
	}
	
	@Test
	void multiRecipientCostsOneSlotPerRecipient() {
		byte[] plaintext = CryptoRandom.bytes(1024 * 1024);
		byte[] one = CryptoMessages.seal(suite(), List.of(recipient().getPublic()), plaintext, null);
		List<PublicKey> four = pairs(4).stream().map(KeyPair::getPublic).toList();
		byte[] many = CryptoMessages.seal(suite(), four, plaintext, null);
		
		assertEquals(3 * slotLength(suite()), many.length - one.length);
		assertTrue(many.length - one.length < plaintext.length);
	}
	
	@Test
	void multiRecipientPreservesRecipientOrder() {
		List<PublicKey> keys = pairs(8).stream().map(KeyPair::getPublic).toList();
		byte[] sealed = CryptoMessages.seal(suite(), keys, DATA, null);
		
		assertEquals(keys.stream().map(KeyId::of).toList(), CryptoMessages.recipientsOf(sealed));
	}
	
	@Test
	void signedMessageHidesSenderFromHeader() {
		byte[] sealed = CryptoMessages.sealSigned(suite(), recipient().getPublic(), sender().getPrivate(), DATA);
		byte[] senderKey = sender().getPublic().getEncoded();
		
		assertFalse(contains(sealed, senderKey));
		byte[] opened = CryptoMessages.unseal(recipient().getPrivate(), sealed);
		int signatureLength = ByteBuffer.wrap(opened).getInt();
		assertArrayEquals(DATA, Arrays.copyOfRange(opened, Integer.BYTES + signatureLength, opened.length));
	}
	
	@Test
	void unsealVerifiedRejectsSignatureOverOtherPlaintext() {
		byte[] signature = Signatures.sign(suite().signature(), sender().getPrivate(), DATA);
		byte[] payload = CryptoBytes.concat(CryptoBytes.of(signature.length), signature, "another message".getBytes(StandardCharsets.UTF_8));
		byte[] sealed = CryptoMessages.seal(recipient().getPublic(), payload);
		
		assertThrows(AuthenticationException.class, () -> CryptoMessages.unsealVerified(recipient().getPrivate(), sender().getPublic(), sealed));
	}
	
	@Test
	void sealSignedForEverySuite() {
		for (CryptoSuite suite : CryptoSuite.values()) {
			byte[] sealed = CryptoMessages.sealSigned(suite, RECIPIENTS.get(suite).getPublic(), SENDERS.get(suite).getPrivate(), DATA);
			assertArrayEquals(DATA, CryptoMessages.unsealVerified(RECIPIENTS.get(suite).getPrivate(), SENDERS.get(suite).getPublic(), sealed));
		}
	}
	
	@Test
	void headerAccessorsWorkWithoutAnyKey() {
		KeyPair unknown = Kems.generateKeyPair(suite().kem());
		byte[] single = CryptoMessages.seal(unknown.getPublic(), DATA);
		byte[] many = CryptoMessages.seal(suite(), List.of(unknown.getPublic()), DATA, null);
		
		assertEquals(suite(), CryptoMessages.suiteOf(single));
		assertEquals(7, CryptoMessages.messageIdOf(many).version());
		assertEquals(List.of(KeyId.of(unknown.getPublic())), CryptoMessages.recipientsOf(many));
	}
	
	@Test
	void messageIdsAreTimeOrdered() {
		long first = UUIDs.unixMillis(CryptoMessages.messageIdOf(CryptoMessages.seal(recipient().getPublic(), DATA)));
		long second = UUIDs.unixMillis(CryptoMessages.messageIdOf(CryptoMessages.seal(recipient().getPublic(), DATA)));
		long third = UUIDs.unixMillis(CryptoMessages.messageIdOf(CryptoMessages.seal(recipient().getPublic(), DATA)));
		
		assertTrue(first <= second);
		assertTrue(second <= third);
	}
	
	@Test
	void deriveMaterialFeedsBothKeyAndCommitment() {
		UUID messageId = UUIDs.v7();
		byte[] artifact;
		try (Kems.Encapsulation encapsulated = Kems.encapsulate(suite().kem(), recipient().getPublic());
			 Secret material = CryptoMessages.deriveMaterial(suite(), encapsulated.sharedSecret(), messageId, encapsulated.encapsulation())) {
			int keyLength = suite().aead().keyLength();
			SecretKey key = Aeads.key(suite().aead(), Arrays.copyOf(material.material(), keyLength));
			byte[] commitment = Arrays.copyOfRange(material.material(), keyLength, keyLength + CryptoMessages.COMMITMENT_LENGTH);
			
			byte[] prologue = CryptoBytes.concat(CryptoMessages.header(CryptoMessages.MAGIC, suite(), messageId, commitment), encapsulated.encapsulation());
			byte[] nonce = CryptoRandom.bytes(suite().aead().nonceLength());
			byte[] ciphertext = Aeads.encrypt(suite().aead(), key, nonce, DATA, CryptoBytes.concat(prologue, nonce));
			artifact = CryptoBytes.concat(prologue, nonce, ciphertext);
		}
		
		assertArrayEquals(DATA, CryptoMessages.unseal(recipient().getPrivate(), artifact));
	}
	
	@Test
	void commitmentDetectsWrongKeyBeforeDecryption() {
		byte[] sealed = CryptoMessages.seal(suite(), List.of(other().getPublic()), DATA, null);
		System.arraycopy(KeyId.of(recipient().getPublic()).toBytes(), 0, sealed, MANY_HEADER_LENGTH, 16);
		
		AuthenticationException exception = assertThrows(AuthenticationException.class, () -> CryptoMessages.unseal(recipient().getPublic(), recipient().getPrivate(), sealed, null));
		assertEquals("No recipient slot matches this key", exception.getMessage());
	}
	
	@Test
	void crossLayoutArtifactsAreNeverConfused() throws Exception {
		byte[] single = CryptoMessages.seal(recipient().getPublic(), DATA);
		byte[] many = CryptoMessages.seal(suite(), List.of(recipient().getPublic()), DATA, null);
		byte[] stream = streamArtifact();
		
		assertArrayEquals(DATA, CryptoMessages.unseal(recipient().getPrivate(), single));
		assertThrows(MalformedDataException.class, () -> CryptoMessages.unseal(recipient().getPrivate(), many));
		assertThrows(MalformedDataException.class, () -> CryptoMessages.unseal(recipient().getPrivate(), stream));
		
		assertArrayEquals(DATA, CryptoMessages.unseal(recipient().getPublic(), recipient().getPrivate(), many, null));
		assertThrows(MalformedDataException.class, () -> CryptoMessages.unseal(recipient().getPublic(), recipient().getPrivate(), single, null));
		assertThrows(MalformedDataException.class, () -> CryptoMessages.unseal(recipient().getPublic(), recipient().getPrivate(), stream, null));
		
		for (byte[] artifact : List.of(single, many, stream)) {
			assertEquals(suite(), CryptoMessages.suiteOf(artifact));
			assertEquals(7, CryptoMessages.messageIdOf(artifact).version());
		}
	}
	
	@Test
	void sealedMessageSurvivesBase64Transport() {
		byte[] sealed = CryptoMessages.seal(recipient().getPublic(), DATA);
		String encoded = Base64.getEncoder().encodeToString(sealed);
		
		assertArrayEquals(DATA, CryptoMessages.unseal(recipient().getPrivate(), Base64.getDecoder().decode(encoded)));
	}
	
	@Test
	void multiRecipientWithOneMillionByteBody() {
		byte[] plaintext = CryptoRandom.bytes(1_000_000);
		List<KeyPair> pairs = pairs(3);
		byte[] sealed = CryptoMessages.seal(suite(), pairs.stream().map(KeyPair::getPublic).toList(), plaintext, null);
		
		for (KeyPair pair : pairs) {
			assertArrayEquals(plaintext, CryptoMessages.unseal(pair.getPublic(), pair.getPrivate(), sealed, null));
		}
	}
	
	@Test
	void unsealManySkipsForeignSlotsBeforeMatching() {
		List<KeyPair> pairs = pairs(8);
		byte[] sealed = CryptoMessages.seal(suite(), pairs.stream().map(KeyPair::getPublic).toList(), DATA, null);
		KeyPair last = pairs.getLast();
		KeyPair unrelated = Kems.generateKeyPair(suite().kem());
		
		assertArrayEquals(DATA, CryptoMessages.unseal(last.getPublic(), last.getPrivate(), sealed, null));
		assertThrows(AuthenticationException.class, () -> CryptoMessages.unseal(unrelated.getPublic(), unrelated.getPrivate(), sealed, null));
	}
	
	private static final class FixedRandom extends SecureRandom {
		
		@Override
		public void nextBytes(byte[] bytes) {
			Arrays.fill(bytes, (byte) 7);
		}
	}
}
