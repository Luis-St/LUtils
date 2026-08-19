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

import net.luis.utils.crypto.exception.AuthenticationException;
import net.luis.utils.crypto.exception.MalformedDataException;
import net.luis.utils.crypto.key.KeyId;
import net.luis.utils.crypto.key.Secret;
import net.luis.utils.crypto.util.CryptoBytes;
import net.luis.utils.util.UUIDs;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.crypto.SecretKey;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.*;

/**
 * Public-key encryption of one message to several recipients.<br>
 * <p>
 *     One content key encrypts the body, and that key is wrapped once per recipient.<br>
 *     The alternative, encrypting the whole message once per recipient, costs a full copy of the ciphertext each time.
 * </p>
 * <p>
 *     The wire format is:
 * </p>
 * <pre>{@code
 *  header:  magic "LUCM" | version (0x01) | suite id (2) | message id (16) | recipient count (2)
 *  slot:    key id (16) | commitment (32) | encapsulation (suite) | wrapped length (2) | wrapped
 *  body:    nonce | ciphertext and tag
 * }</pre>
 * <p>
 *     The magic differs from the one {@link Sealed} writes, because the layout differs: a reader must not be able to mistake one for the other.
 * </p>
 * <p>
 *     Each slot carries its own key commitment.<br>
 *     A reader matches the key id, checks the commitment, and only then attempts the unwrap, so a wrong slot is rejected before any cipher runs.<br>
 *     This is the construction the commitment exists for: it is the one place where a reader legitimately tries a key that may not fit.
 * </p>
 * <p>
 *     The whole header and slot region is authenticated as associated data of the body, so no slot can be added, removed or rewritten.
 * </p>
 * <p>
 *     Example:
 * </p>
 * <pre>{@code
 * List<PublicKey> recipients = List.of(alice.getPublic(), bob.getPublic());
 * byte[] sealed = SealedForMany.seal(CryptoSuite.current(), recipients, plaintext, null);
 *
 * // Every recipient opens the same artifact with their own key pair
 * byte[] opened = SealedForMany.unseal(bob.getPublic(), bob.getPrivate(), sealed, null);
 *
 * // The recipient set is inspectable, which also makes it public
 * List<KeyId> slots = SealedForMany.recipientsOf(sealed);
 * }</pre>
 *
 * @see Sealed
 *
 * @author Luis-St
 */
public final class SealedForMany {
	
	/**
	 * The magic value every multi-recipient artifact starts with.<br>
	 */
	private static final byte[] MAGIC = "LUCM".getBytes(StandardCharsets.US_ASCII);
	/**
	 * The version of the multi-recipient wire format.<br>
	 */
	private static final byte VERSION = 1;
	/**
	 * The length of the fixed header in bytes.<br>
	 */
	private static final int HEADER_LENGTH = MAGIC.length + 1 + Short.BYTES + 16 + Short.BYTES;
	/**
	 * The largest number of recipients a single artifact may carry.<br>
	 */
	private static final int MAX_RECIPIENTS = Short.MAX_VALUE;
	
	/**
	 * Private constructor to prevent instantiation.<br>
	 * This is a static helper class.<br>
	 */
	private SealedForMany() {}
	
	/**
	 * Encrypts one message to every given recipient.<br>
	 *
	 * @param suite The suite to encrypt with
	 * @param recipients The public keys to encrypt to
	 * @param plaintext The message to encrypt
	 * @param associatedData Extra data to authenticate but not encrypt, may be null
	 * @return The sealed artifact
	 * @throws NullPointerException If the suite, the recipients or the plaintext is null
	 * @throws IllegalArgumentException If there is no recipient, or more than the format can carry
	 */
	public static byte @NonNull [] seal(@NonNull CryptoSuite suite, @NonNull List<PublicKey> recipients, byte @NonNull [] plaintext, byte @Nullable [] associatedData) {
		Objects.requireNonNull(suite, "Suite must not be null");
		Objects.requireNonNull(recipients, "Recipients must not be null");
		Objects.requireNonNull(plaintext, "Plaintext must not be null");
		if (recipients.isEmpty()) {
			throw new IllegalArgumentException("At least one recipient is required");
		}
		if (recipients.size() > MAX_RECIPIENTS) {
			throw new IllegalArgumentException("At most " + MAX_RECIPIENTS + " recipients are supported, got " + recipients.size());
		}
		
		SecretKey contentKey = Aead.generateKey(suite.aead());
		UUID messageId = UUIDs.v7();
		byte[] header = header(suite, messageId, (short) recipients.size());
		
		ByteArrayOutputStream slots = new ByteArrayOutputStream();
		for (PublicKey recipient : recipients) {
			try (Kems.Encapsulation encapsulated = Kems.encapsulate(suite.kem(), recipient); Secret material = Sealed.deriveMaterial(suite, encapsulated.sharedSecret(), messageId, encapsulated.encapsulation())) {
				SecretKey wrappingKey = Aead.key(suite.aead(), CryptoBytes.slice(material.material(), 0, suite.aead().keyLength()));
				byte[] commitment = CryptoBytes.slice(material.material(), suite.aead().keyLength(), Sealed.COMMITMENT_LENGTH);
				byte[] wrapped = Aead.encrypt(suite.aead(), wrappingKey, contentKey.getEncoded(), header);
				
				slots.writeBytes(KeyId.of(recipient).toBytes());
				slots.writeBytes(commitment);
				slots.writeBytes(encapsulated.encapsulation());
				slots.writeBytes(CryptoBytes.of((short) wrapped.length));
				slots.writeBytes(wrapped);
			}
		}
		
		byte[] slotBytes = slots.toByteArray();
		byte[] aad = aad(header, slotBytes, associatedData);
		return CryptoBytes.concat(header, slotBytes, Aead.encrypt(suite.aead(), contentKey, plaintext, aad));
	}
	
	/**
	 * Opens the slot belonging to the given recipient.<br>
	 * <p>
	 *     The recipient's own public key is required as well as the private key: it identifies which slot to open, which avoids a full decapsulation attempt against every foreign slot.<br>
	 *     Only slots whose key id matches are tried at all, and each of those is checked against its commitment before anything is unwrapped.
	 * </p>
	 *
	 * @param ownKey The recipient's own public key, used to find their slot
	 * @param recipient The recipient's private key
	 * @param sealed The sealed artifact
	 * @param associatedData The extra data that was authenticated when sealing, may be null
	 * @return The recovered message
	 * @throws NullPointerException If the own key, the recipient or the sealed artifact is null
	 * @throws MalformedDataException If the artifact is not readable
	 * @throws AuthenticationException If no slot of the artifact belongs to this key
	 */
	public static byte @NonNull [] unseal(@NonNull PublicKey ownKey, @NonNull PrivateKey recipient, byte @NonNull [] sealed, byte @Nullable [] associatedData) {
		Objects.requireNonNull(ownKey, "Own key must not be null");
		Objects.requireNonNull(recipient, "Recipient must not be null");
		Objects.requireNonNull(sealed, "Sealed artifact must not be null");
		
		Parsed parsed = Parsed.of(sealed);
		CryptoSuite suite = parsed.suite();
		KeyId own = KeyId.of(ownKey);
		
		for (Slot slot : parsed.slots()) {
			if (!slot.keyId().equals(own)) {
				continue;
			}
			
			Optional<byte[]> contentKey = unwrap(suite, recipient, parsed, slot);
			if (contentKey.isPresent()) {
				byte[] raw = contentKey.get();
				
				try {
					return Aead.decrypt(suite.aead(), Aead.key(suite.aead(), raw), parsed.body(), aad(parsed.header(), parsed.slotBytes(), associatedData));
				} finally {
					CryptoBytes.wipe(raw);
				}
			}
		}
		throw new AuthenticationException("No recipient slot matches this key");
	}
	
	/**
	 * Lists which recipients an artifact was sealed for, without opening it.<br>
	 * <p>
	 *     This makes the recipient set inspectable for tooling, and equally makes it public.<br>
	 *     If the set of recipients is itself sensitive, seal each recipient a separate artifact instead.
	 * </p>
	 *
	 * @param sealed The sealed artifact
	 * @return The key ids of every recipient, in the order they appear
	 * @throws NullPointerException If the sealed artifact is null
	 * @throws MalformedDataException If the artifact is not readable
	 */
	public static @NonNull @Unmodifiable List<KeyId> recipientsOf(byte @NonNull [] sealed) {
		Objects.requireNonNull(sealed, "Sealed artifact must not be null");
		return Parsed.of(sealed).slots().stream().map(Slot::keyId).toList();
	}
	
	/**
	 * Reads the suite of an artifact without opening it.<br>
	 *
	 * @param sealed The sealed artifact
	 * @return The suite the artifact was written with
	 * @throws NullPointerException If the sealed artifact is null
	 * @throws MalformedDataException If the artifact is not readable
	 */
	public static @NonNull CryptoSuite suiteOf(byte @NonNull [] sealed) {
		Objects.requireNonNull(sealed, "Sealed artifact must not be null");
		return Parsed.of(sealed).suite();
	}
	
	/**
	 * Attempts to recover the content key from the given slot.<br>
	 * <p>
	 *     The commitment is checked first, so a slot that does not belong to this key is rejected without running a cipher.<br>
	 *     The unwrap itself still goes through the non-throwing decryption, because a key id collision would get this far legitimately.
	 * </p>
	 *
	 * @param suite The suite the artifact was written with
	 * @param recipient The recipient's private key
	 * @param parsed The parsed artifact
	 * @param slot The slot to attempt
	 * @return The recovered content key, or empty if this slot does not belong to the key
	 * @throws NullPointerException If the suite, the recipient, the parsed artifact or the slot is null
	 */
	private static @NonNull Optional<byte[]> unwrap(@NonNull CryptoSuite suite, @NonNull PrivateKey recipient, @NonNull Parsed parsed, @NonNull Slot slot) {
		Objects.requireNonNull(suite, "Suite must not be null");
		Objects.requireNonNull(recipient, "Recipient must not be null");
		Objects.requireNonNull(parsed, "Parsed artifact must not be null");
		Objects.requireNonNull(slot, "Slot must not be null");
		
		try (Secret shared = Kems.decapsulate(suite.kem(), recipient, slot.encapsulation()); Secret material = Sealed.deriveMaterial(suite, shared, parsed.messageId(), slot.encapsulation())) {
			byte[] commitment = CryptoBytes.slice(material.material(), suite.aead().keyLength(), Sealed.COMMITMENT_LENGTH);
			if (!CryptoBytes.equalsConstantTime(commitment, slot.commitment())) {
				return Optional.empty();
			}
			
			SecretKey wrappingKey = Aead.key(suite.aead(), CryptoBytes.slice(material.material(), 0, suite.aead().keyLength()));
			int nonceLength = suite.aead().nonceLength();
			if (slot.wrapped().length < nonceLength + suite.aead().tagLength()) {
				throw new MalformedDataException("Wrapped content key too short: " + slot.wrapped().length);
			}
			
			byte[] nonce = CryptoBytes.slice(slot.wrapped(), 0, nonceLength);
			byte[] body = CryptoBytes.slice(slot.wrapped(), nonceLength, slot.wrapped().length - nonceLength);
			return Aead.tryDecrypt(suite.aead(), wrappingKey, nonce, body, parsed.header());
		}
	}
	
	/**
	 * Builds the fixed header of a multi-recipient artifact.<br>
	 *
	 * @param suite The suite the artifact is written with
	 * @param messageId The message id of the artifact
	 * @param count The number of recipients
	 * @return The built header
	 * @throws NullPointerException If the suite or the message id is null
	 */
	private static byte @NonNull [] header(@NonNull CryptoSuite suite, @NonNull UUID messageId, short count) {
		Objects.requireNonNull(suite, "Suite must not be null");
		Objects.requireNonNull(messageId, "Message id must not be null");
		
		return CryptoBytes.concat(MAGIC, new byte[] { VERSION }, CryptoBytes.of(suite.id()), UUIDs.toBytes(messageId), CryptoBytes.of(count));
	}
	
	/**
	 * Builds the associated data of the body encryption.<br>
	 *
	 * @param header The fixed header
	 * @param slotBytes The whole slot region
	 * @param associatedData The caller's extra data, may be null
	 * @return The built associated data
	 * @throws NullPointerException If the header or the slot region is null
	 */
	private static byte @NonNull [] aad(byte @NonNull [] header, byte @NonNull [] slotBytes, byte @Nullable [] associatedData) {
		Objects.requireNonNull(header, "Header must not be null");
		Objects.requireNonNull(slotBytes, "Slot bytes must not be null");
		
		return associatedData == null || associatedData.length == 0 ? CryptoBytes.concat(header, slotBytes) : CryptoBytes.concat(header, slotBytes, associatedData);
	}
	
	/**
	 * One recipient's slot.<br>
	 *
	 * @author Luis-St
	 *
	 * @param keyId The key id of the recipient this slot belongs to
	 * @param commitment The key commitment of this slot's wrapping key
	 * @param encapsulation The encapsulation carrying this slot's wrapping key
	 * @param wrapped The wrapped content key, nonce first
	 */
	private record Slot(
		@NonNull KeyId keyId,
		byte @NonNull [] commitment,
		byte @NonNull [] encapsulation,
		byte @NonNull [] wrapped
	) {
		
		/**
		 * Constructs a new slot.<br>
		 * @throws NullPointerException If the key id, the commitment, the encapsulation or the wrapped content key is null
		 */
		private Slot {
			Objects.requireNonNull(keyId, "Key id must not be null");
			Objects.requireNonNull(commitment, "Commitment must not be null");
			Objects.requireNonNull(encapsulation, "Encapsulation must not be null");
			Objects.requireNonNull(wrapped, "Wrapped content key must not be null");
		}
	}
	
	/**
	 * A parsed multi-recipient artifact.<br>
	 *
	 * @author Luis-St
	 *
	 * @param suite The suite the artifact was written with
	 * @param messageId The message id of the artifact
	 * @param header The fixed header, as it has to be fed back as associated data
	 * @param slotBytes The whole slot region, as it has to be fed back as associated data
	 * @param slots The parsed slots
	 * @param body The encrypted body, nonce first
	 */
	private record Parsed(
		@NonNull CryptoSuite suite,
		@NonNull UUID messageId,
		byte @NonNull [] header,
		byte @NonNull [] slotBytes,
		@NonNull List<Slot> slots,
		byte @NonNull [] body
	) {
		
		/**
		 * Parses a multi-recipient artifact.<br>
		 * <p>
		 *     Every slot is parsed, including the ones that will not be opened, because the whole slot region is an input to the body's authentication and has to be reproduced exactly.
		 * </p>
		 *
		 * @param sealed The artifact to parse
		 * @return The parsed artifact
		 * @throws NullPointerException If the sealed artifact is null
		 * @throws MalformedDataException If the artifact is too short, has a bad magic, an unsupported version, an unknown suite or a malformed slot
		 */
		private static @NonNull Parsed of(byte @NonNull [] sealed) {
			Objects.requireNonNull(sealed, "Sealed artifact must not be null");
			
			if (sealed.length < HEADER_LENGTH) {
				throw new MalformedDataException("Multi-recipient blob too short to contain a header");
			}
			for (int i = 0; i < MAGIC.length; i++) {
				if (sealed[i] != MAGIC[i]) {
					throw new MalformedDataException("Not a multi-recipient sealed blob (bad magic)");
				}
			}
			if (sealed[MAGIC.length] != VERSION) {
				throw new MalformedDataException("Unsupported multi-recipient format version " + sealed[MAGIC.length]);
			}
			
			int offset = MAGIC.length + 1;
			CryptoSuite suite = CryptoSuite.byId(ByteBuffer.wrap(sealed, offset, Short.BYTES).getShort());
			offset += Short.BYTES;
			UUID messageId = UUIDs.fromBytes(CryptoBytes.slice(sealed, offset, 16));
			offset += 16;
			int count = ByteBuffer.wrap(sealed, offset, Short.BYTES).getShort() & 0xFFFF;
			offset += Short.BYTES;
			
			byte[] header = CryptoBytes.slice(sealed, 0, HEADER_LENGTH);
			int encapsulationLength = suite.kem().encapsulationLength();
			int minimumSlot = 16 + Sealed.COMMITMENT_LENGTH + encapsulationLength + Short.BYTES;
			if (count > (sealed.length - HEADER_LENGTH) / minimumSlot) {
				throw new MalformedDataException("Recipient count " + count + " does not fit into the remaining " + (sealed.length - HEADER_LENGTH) + " bytes");
			}
			
			int slotStart = offset;
			List<Slot> slots = new ArrayList<>(count);
			for (int i = 0; i < count; i++) {
				if (sealed.length - offset < minimumSlot) {
					throw new MalformedDataException("Truncated recipient slot " + i);
				}
				
				KeyId keyId = KeyId.fromBytes(CryptoBytes.slice(sealed, offset, 16));
				offset += 16;
				byte[] commitment = CryptoBytes.slice(sealed, offset, Sealed.COMMITMENT_LENGTH);
				offset += Sealed.COMMITMENT_LENGTH;
				byte[] encapsulation = CryptoBytes.slice(sealed, offset, encapsulationLength);
				offset += encapsulationLength;
				int wrappedLength = ByteBuffer.wrap(sealed, offset, Short.BYTES).getShort() & 0xFFFF;
				offset += Short.BYTES;
				
				if (sealed.length - offset < wrappedLength) {
					throw new MalformedDataException("Truncated wrapped content key in recipient slot " + i);
				}
				
				byte[] wrapped = CryptoBytes.slice(sealed, offset, wrappedLength);
				offset += wrappedLength;
				slots.add(new Slot(keyId, commitment, encapsulation, wrapped));
			}
			
			int minimumBody = suite.aead().nonceLength() + suite.aead().tagLength();
			if (sealed.length - offset < minimumBody) {
				throw new MalformedDataException("Multi-recipient blob has no readable body");
			}
			
			byte[] slotBytes = CryptoBytes.slice(sealed, slotStart, offset - slotStart);
			byte[] body = CryptoBytes.slice(sealed, offset, sealed.length - offset);
			return new Parsed(suite, messageId, header, slotBytes, List.copyOf(slots), body);
		}
	}
}
