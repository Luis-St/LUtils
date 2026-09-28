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
import net.luis.utils.crypto.util.CryptoRandom;
import net.luis.utils.util.UUIDs;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.crypto.SecretKey;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.*;

/**
 * Public-key encryption of a message, to one recipient or to several.<br>
 * <p>
 *     Nothing here is an exchange.<br>
 *     A message is a value, sealing produces bytes and unsealing consumes them, and the sender never has to meet the recipient.<br>
 *     Interactive key agreement is {@link Kems}.
 * </p>
 * <p>
 *     Both shapes share a construction.<br>
 *     A key encapsulation produces a shared secret, one derivation turns that into a cipher key plus a key commitment,<br>
 *     and everything in front of the ciphertext is fed to the cipher as associated data so no field can be rewritten or downgraded without breaking the tag.
 * </p>
 * <p>
 *     The single-recipient format is:
 * </p>
 * <pre>{@code
 *  0..3    magic "LUC1"
 *  4       version (0x01)
 *  5..6    suite id (big endian short)
 *  7..22   message id (UUIDv7, 16 bytes)
 *  23..54  key commitment (32 bytes)
 *  55..    KEM encapsulation
 *  ..      AEAD nonce
 *  ..      ciphertext and tag
 * }</pre>
 * <p>
 *     The multi-recipient format wraps one content key once per recipient, which costs a slot per recipient instead of a full copy of the ciphertext:
 * </p>
 * <pre>{@code
 *  header:  magic "LUCM" | version (0x01) | suite id (2) | message id (16) | recipient count (2)
 *  slot:    key id (16) | commitment (32) | encapsulation (suite) | wrapped length (2) | wrapped
 *  body:    nonce | ciphertext and tag
 * }</pre>
 * <p>
 *     The magic differs per layout on purpose, so a reader can never mistake one for the other.<br>
 *     {@link CryptoOutputStream} writes a third layout under "LUCS" for the same reason.
 * </p>
 * <p>
 *     Each recipient slot carries its own key commitment.<br>
 *     A reader matches the key id, checks the commitment, and only then attempts the unwrap, so a wrong slot is rejected before any cipher runs.<br>
 *     This is the construction the commitment exists for, since it is the one place where a reader legitimately tries a key that may not fit.
 * </p>
 * <p>
 *     {@link #seal(PublicKey, byte[])} alone is anonymous encryption, so anyone holding the recipient's public key can produce a message that opens correctly.<br>
 *     Use {@link #sealSigned} when the recipient has to know who wrote it.
 * </p>
 * <p>
 *     Example:
 * </p>
 * <pre>{@code
 * KeyPair recipient = Kems.generateKeyPair(CryptoSuite.current().kem());
 *
 * // One recipient, anonymous
 * byte[] message = CryptoMessages.seal(recipient.getPublic(), plaintext);
 * byte[] opened = CryptoMessages.unseal(recipient.getPrivate(), message);
 *
 * // One recipient, authenticated
 * byte[] signed = CryptoMessages.sealSigned(CryptoSuite.current(), recipient.getPublic(), sender.getPrivate(), plaintext);
 * byte[] verified = CryptoMessages.unsealVerified(recipient.getPrivate(), sender.getPublic(), signed);
 *
 * // Several recipients, each opening the same message with their own key pair
 * byte[] shared = CryptoMessages.seal(CryptoSuite.current(), List.of(alice.getPublic(), bob.getPublic()), plaintext, null);
 * byte[] read = CryptoMessages.unseal(bob.getPublic(), bob.getPrivate(), shared, null);
 *
 * // The header is readable without any key, whichever layout it is
 * CryptoSuite suite = CryptoMessages.suiteOf(shared);
 * List<KeyId> slots = CryptoMessages.recipientsOf(shared);
 * }</pre>
 *
 * @see CryptoSuite
 * @see CryptoOutputStream
 * @see Kems
 *
 * @author Luis-St
 */
public final class CryptoMessages {
	
	/**
	 * The domain separation label of the key derivation.<br>
	 */
	private static final byte[] KEY_LABEL = "lutils-seal-v1".getBytes(StandardCharsets.UTF_8);
	/**
	 * The largest number of recipients a single message may carry.<br>
	 */
	private static final int MAX_RECIPIENTS = Short.MAX_VALUE;
	/**
	 * The magic value every single-recipient message starts with.<br>
	 */
	static final byte[] MAGIC = "LUC1".getBytes(StandardCharsets.US_ASCII);
	/**
	 * The magic value every multi-recipient message starts with.<br>
	 */
	static final byte[] MANY_MAGIC = "LUCM".getBytes(StandardCharsets.US_ASCII);
	/**
	 * The length of the fixed header of the multi-recipient layout.<br>
	 */
	private static final int MANY_HEADER_LENGTH = MANY_MAGIC.length + 1 + Short.BYTES + 16 + Short.BYTES;
	/**
	 * The magic value every chunked stream starts with.<br>
	 */
	static final byte[] STREAM_MAGIC = "LUCS".getBytes(StandardCharsets.US_ASCII);
	/**
	 * The version of the wire format, shared by every layout.<br>
	 */
	static final byte VERSION = 1;
	/**
	 * The number of plaintext bytes a chunked stream puts in one chunk.<br>
	 */
	static final int CHUNK_SIZE = 64 * 1024;
	/**
	 * The kind byte marking a chunk that is followed by more chunks.<br>
	 */
	static final byte CHUNK_MORE = 0x00;
	/**
	 * The kind byte marking the last chunk of a stream.<br>
	 */
	static final byte CHUNK_FINAL = 0x01;
	/**
	 * The length of the key commitment in bytes.<br>
	 */
	static final int COMMITMENT_LENGTH = 32;
	/**
	 * The length of the fixed header of the layouts that carry a commitment.<br>
	 */
	static final int HEADER_LENGTH = MAGIC.length + 1 + Short.BYTES + 16 + COMMITMENT_LENGTH;
	
	/**
	 * Private constructor to prevent instantiation.<br>
	 * This is a static helper class.<br>
	 */
	private CryptoMessages() {}
	
	/**
	 * Encrypts to the given recipient using the current suite.<br>
	 * <p>
	 *     This provides confidentiality and integrity, but not sender authentication, so anyone holding the recipient's public key can produce a message that unseals correctly.<br>
	 *     Use {@link #sealSigned} when the recipient must know who wrote it.
	 * </p>
	 *
	 * @param recipient The public key to encrypt to
	 * @param plaintext The message to encrypt
	 * @return The sealed message
	 * @throws NullPointerException If the recipient or the plaintext is null
	 */
	public static byte @NonNull [] seal(@NonNull PublicKey recipient, byte @NonNull [] plaintext) {
		return seal(CryptoSuite.current(), recipient, plaintext, null);
	}
	
	/**
	 * Encrypts to the given recipient using the given suite.<br>
	 *
	 * @param suite The suite to encrypt with
	 * @param recipient The public key to encrypt to
	 * @param plaintext The message to encrypt
	 * @param associatedData Extra data to authenticate but not encrypt, may be null
	 * @return The sealed message
	 * @throws NullPointerException If the suite, the recipient or the plaintext is null
	 */
	public static byte @NonNull [] seal(@NonNull CryptoSuite suite, @NonNull PublicKey recipient, byte @NonNull [] plaintext, byte @Nullable [] associatedData) {
		return seal(CryptoRandom.instance(), suite, recipient, plaintext, associatedData);
	}
	
	/**
	 * Encrypts to the given recipient, drawing all randomness from the given source.<br>
	 * This overload exists so the wire format can be tested against known answers with a fixed source.<br>
	 *
	 * @param random The source to draw the nonce from
	 * @param suite The suite to encrypt with
	 * @param recipient The public key to encrypt to
	 * @param plaintext The message to encrypt
	 * @param associatedData Extra data to authenticate but not encrypt, may be null
	 * @return The sealed message
	 * @throws NullPointerException If the random source, the suite, the recipient or the plaintext is null
	 */
	public static byte @NonNull [] seal(@NonNull SecureRandom random, @NonNull CryptoSuite suite, @NonNull PublicKey recipient, byte @NonNull [] plaintext, byte @Nullable [] associatedData) {
		Objects.requireNonNull(random, "Random must not be null");
		Objects.requireNonNull(suite, "Suite must not be null");
		Objects.requireNonNull(recipient, "Recipient must not be null");
		Objects.requireNonNull(plaintext, "Plaintext must not be null");
		
		try (Kems.Encapsulation encapsulated = Kems.encapsulate(suite.kem(), recipient)) {
			UUID messageId = UUIDs.v7();
			
			try (Secret material = deriveMaterial(suite, encapsulated.sharedSecret(), messageId, encapsulated.encapsulation())) {
				SecretKey key = Aeads.key(suite.aead(), CryptoBytes.slice(material.material(), 0, suite.aead().keyLength()));
				byte[] commitment = CryptoBytes.slice(material.material(), suite.aead().keyLength(), COMMITMENT_LENGTH);
				
				byte[] prologue = CryptoBytes.concat(header(MAGIC, suite, messageId, commitment), encapsulated.encapsulation());
				byte[] nonce = CryptoRandom.bytes(random, suite.aead().nonceLength());
				byte[] ciphertext = Aeads.encrypt(suite.aead(), key, nonce, plaintext, aad(prologue, nonce, associatedData));
				return CryptoBytes.concat(prologue, nonce, ciphertext);
			}
		}
	}
	
	/**
	 * Encrypts one message to every given recipient.<br>
	 * <p>
	 *     One content key encrypts the body, and that key is wrapped once per recipient.<br>
	 *     The alternative, encrypting the whole message once per recipient, costs a full copy of the ciphertext each time.
	 * </p>
	 *
	 * @param suite The suite to encrypt with
	 * @param recipients The public keys to encrypt to
	 * @param plaintext The message to encrypt
	 * @param associatedData Extra data to authenticate but not encrypt, may be null
	 * @return The sealed message
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
		
		SecretKey contentKey = Aeads.generateKey(suite.aead());
		UUID messageId = UUIDs.v7();
		byte[] header = manyHeader(suite, messageId, (short) recipients.size());
		
		ByteArrayOutputStream slots = new ByteArrayOutputStream();
		for (PublicKey recipient : recipients) {
			try (Kems.Encapsulation encapsulated = Kems.encapsulate(suite.kem(), recipient); Secret material = deriveMaterial(suite, encapsulated.sharedSecret(), messageId, encapsulated.encapsulation())) {
				SecretKey wrappingKey = Aeads.key(suite.aead(), CryptoBytes.slice(material.material(), 0, suite.aead().keyLength()));
				byte[] commitment = CryptoBytes.slice(material.material(), suite.aead().keyLength(), COMMITMENT_LENGTH);
				byte[] wrapped = Aeads.encrypt(suite.aead(), wrappingKey, contentKey.getEncoded(), header);
				
				slots.writeBytes(KeyId.of(recipient).toBytes());
				slots.writeBytes(commitment);
				slots.writeBytes(encapsulated.encapsulation());
				slots.writeBytes(CryptoBytes.of((short) wrapped.length));
				slots.writeBytes(wrapped);
			}
		}
		
		byte[] slotBytes = slots.toByteArray();
		byte[] aad = manyAad(header, slotBytes, associatedData);
		return CryptoBytes.concat(header, slotBytes, Aeads.encrypt(suite.aead(), contentKey, plaintext, aad));
	}
	
	/**
	 * Decrypts a single-recipient message.<br>
	 *
	 * @param recipient The private key to decrypt with
	 * @param sealed The sealed message
	 * @return The recovered message
	 * @throws NullPointerException If the recipient or the sealed message is null
	 * @throws MalformedDataException If the message is not readable
	 * @throws AuthenticationException If the message does not authenticate under this key
	 */
	public static byte @NonNull [] unseal(@NonNull PrivateKey recipient, byte @NonNull [] sealed) {
		return unseal(recipient, sealed, null);
	}
	
	/**
	 * Decrypts a single-recipient message.<br>
	 *
	 * @param recipient The private key to decrypt with
	 * @param sealed The sealed message
	 * @param associatedData The extra data that was authenticated when sealing, may be null
	 * @return The recovered message
	 * @throws NullPointerException If the recipient or the sealed message is null
	 * @throws MalformedDataException If the message is not readable
	 * @throws AuthenticationException If the message does not authenticate under this key
	 */
	public static byte @NonNull [] unseal(@NonNull PrivateKey recipient, byte @NonNull [] sealed, byte @Nullable [] associatedData) {
		Objects.requireNonNull(recipient, "Recipient must not be null");
		Objects.requireNonNull(sealed, "Sealed artifact must not be null");
		
		return unseal(recipient, Head.parse(sealed), sealed, associatedData);
	}
	
	/**
	 * Opens the slot of a multi-recipient message that belongs to the given recipient.<br>
	 * <p>
	 *     The recipient's own public key is required as well as the private key, since it identifies which slot to open, which avoids a full decapsulation attempt against every foreign slot.<br>
	 *     Only slots whose key id matches are tried at all, and each of those is checked against its commitment before anything is unwrapped.
	 * </p>
	 *
	 * @param ownKey The recipient's own public key, used to find their slot
	 * @param recipient The recipient's private key
	 * @param sealed The sealed message
	 * @param associatedData The extra data that was authenticated when sealing, may be null
	 * @return The recovered message
	 * @throws NullPointerException If the own key, the recipient or the sealed message is null
	 * @throws MalformedDataException If the message is not readable
	 * @throws AuthenticationException If no slot of the message belongs to this key
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
					return Aeads.decrypt(suite.aead(), Aeads.key(suite.aead(), raw), parsed.body(), manyAad(parsed.header(), parsed.slotBytes(), associatedData));
				} finally {
					CryptoBytes.wipe(raw);
				}
			}
		}
		throw new AuthenticationException("No recipient slot matches this key");
	}
	
	/**
	 * Signs the plaintext with the sender's key, then seals plaintext and signature together to one recipient.<br>
	 * <p>
	 *     The signature is inside the encryption, so it does not reveal the sender to anyone who cannot already decrypt the message.
	 * </p>
	 *
	 * @param suite The suite to sign and encrypt with
	 * @param recipient The public key to encrypt to
	 * @param sender The private key to sign with
	 * @param plaintext The message to sign and encrypt
	 * @return The sealed message
	 * @throws NullPointerException If the suite, the recipient, the sender or the plaintext is null
	 */
	public static byte @NonNull [] sealSigned(@NonNull CryptoSuite suite, @NonNull PublicKey recipient, @NonNull PrivateKey sender, byte @NonNull [] plaintext) {
		Objects.requireNonNull(suite, "Suite must not be null");
		Objects.requireNonNull(sender, "Sender must not be null");
		Objects.requireNonNull(plaintext, "Plaintext must not be null");
		
		byte[] signature = Signatures.sign(suite.signature(), sender, plaintext);
		return seal(suite, recipient, CryptoBytes.concat(CryptoBytes.of(signature.length), signature, plaintext), null);
	}
	
	/**
	 * Unseals a single-recipient message and verifies the sender's signature.<br>
	 *
	 * @param recipient The private key to decrypt with
	 * @param sender The public key the signature must verify against
	 * @param sealed The sealed message
	 * @return The recovered message
	 * @throws NullPointerException If the recipient, the sender or the sealed message is null
	 * @throws MalformedDataException If the message is not readable
	 * @throws AuthenticationException If the message does not decrypt, or the signature does not match the sender
	 */
	public static byte @NonNull [] unsealVerified(@NonNull PrivateKey recipient, @NonNull PublicKey sender, byte @NonNull [] sealed) {
		Objects.requireNonNull(recipient, "Recipient must not be null");
		Objects.requireNonNull(sender, "Sender must not be null");
		Objects.requireNonNull(sealed, "Sealed artifact must not be null");
		
		Head head = Head.parse(sealed);
		byte[] opened = unseal(recipient, head, sealed, null);
		ByteBuffer buffer = ByteBuffer.wrap(opened);
		int signatureLength;
		try {
			signatureLength = buffer.getInt();
		} catch (RuntimeException e) {
			throw new MalformedDataException("Signed payload is too short to carry a signature length", e);
		}
		
		if (signatureLength < 0 || signatureLength > buffer.remaining()) {
			throw new MalformedDataException("Signature length " + signatureLength + " does not fit into the remaining " + buffer.remaining() + " bytes");
		}
		
		byte[] signature = new byte[signatureLength];
		buffer.get(signature);
		byte[] plaintext = new byte[buffer.remaining()];
		buffer.get(plaintext);
		
		Signatures.require(head.suite().signature(), sender, plaintext, signature);
		return plaintext;
	}
	
	/**
	 * Reads the suite of a message without decrypting it.<br>
	 * <p>
	 *     Every layout this library writes is accepted, which is what the differing magic values are for.<br>
	 *     Useful for deciding whether to re-encrypt under a newer suite.
	 * </p>
	 *
	 * @param sealed The sealed message
	 * @return The suite the message was written with
	 * @throws NullPointerException If the sealed message is null
	 * @throws MalformedDataException If the header is not readable
	 */
	public static @NonNull CryptoSuite suiteOf(byte @NonNull [] sealed) {
		Objects.requireNonNull(sealed, "Sealed artifact must not be null");
		return startsWith(sealed, MANY_MAGIC) ? Parsed.of(sealed).suite() : Head.parse(sealed, magicOf(sealed)).suite();
	}
	
	/**
	 * Reads the time-ordered message id of a message without decrypting it.<br>
	 * <p>
	 *     Every layout this library writes is accepted.<br>
	 *     The creation time is recoverable from the id through {@link UUIDs#unixMillis(UUID)}.
	 * </p>
	 *
	 * @param sealed The sealed message
	 * @return The message id of the message
	 * @throws NullPointerException If the sealed message is null
	 * @throws MalformedDataException If the header is not readable
	 */
	public static @NonNull UUID messageIdOf(byte @NonNull [] sealed) {
		Objects.requireNonNull(sealed, "Sealed artifact must not be null");
		return startsWith(sealed, MANY_MAGIC) ? Parsed.of(sealed).messageId() : Head.parse(sealed, magicOf(sealed)).messageId();
	}
	
	/**
	 * Lists which recipients a multi-recipient message was sealed for, without opening it.<br>
	 * <p>
	 *     This makes the recipient set inspectable for tooling, and equally makes it public.<br>
	 *     If the set of recipients is itself sensitive, seal each recipient a separate message instead.
	 * </p>
	 *
	 * @param sealed The sealed message
	 * @return The key ids of every recipient, in the order they appear
	 * @throws NullPointerException If the sealed message is null
	 * @throws MalformedDataException If the message is not readable
	 */
	public static @NonNull @Unmodifiable List<KeyId> recipientsOf(byte @NonNull [] sealed) {
		Objects.requireNonNull(sealed, "Sealed artifact must not be null");
		return Parsed.of(sealed).slots().stream().map(Slot::keyId).toList();
	}
	
	/**
	 * Decrypts a single-recipient message whose header has already been parsed.<br>
	 *
	 * @param recipient The private key to decrypt with
	 * @param head The parsed header
	 * @param sealed The sealed message
	 * @param associatedData The extra data that was authenticated when sealing, may be null
	 * @return The recovered message
	 * @throws NullPointerException If the recipient, the head or the sealed message is null
	 * @throws MalformedDataException If the message is not readable
	 * @throws AuthenticationException If the message does not authenticate under this key
	 */
	private static byte @NonNull [] unseal(@NonNull PrivateKey recipient, @NonNull Head head, byte @NonNull [] sealed, byte @Nullable [] associatedData) {
		Objects.requireNonNull(recipient, "Recipient must not be null");
		Objects.requireNonNull(head, "Head must not be null");
		Objects.requireNonNull(sealed, "Sealed artifact must not be null");
		
		CryptoSuite suite = head.suite();
		int encapsulationLength = suite.kem().encapsulationLength();
		int nonceLength = suite.aead().nonceLength();
		int minimum = HEADER_LENGTH + encapsulationLength + nonceLength + suite.aead().tagLength();
		if (sealed.length < minimum) {
			throw new MalformedDataException("Sealed blob too short: " + sealed.length + " < " + minimum);
		}
		
		byte[] prologue = CryptoBytes.slice(sealed, 0, HEADER_LENGTH + encapsulationLength);
		byte[] encapsulation = CryptoBytes.slice(sealed, HEADER_LENGTH, encapsulationLength);
		byte[] nonce = CryptoBytes.slice(sealed, prologue.length, nonceLength);
		int bodyOffset = prologue.length + nonceLength;
		byte[] ciphertext = CryptoBytes.slice(sealed, bodyOffset, sealed.length - bodyOffset);
		
		try (Secret shared = Kems.decapsulate(suite.kem(), recipient, encapsulation); Secret material = deriveMaterial(suite, shared, head.messageId(), encapsulation)) {
			SecretKey key = Aeads.key(suite.aead(), CryptoBytes.slice(material.material(), 0, suite.aead().keyLength()));
			byte[] commitment = CryptoBytes.slice(material.material(), suite.aead().keyLength(), COMMITMENT_LENGTH);
			
			if (!CryptoBytes.equalsConstantTime(commitment, head.commitment())) {
				throw new AuthenticationException("Key commitment mismatch - the header does not belong to this key");
			}
			return Aeads.decrypt(suite.aead(), key, nonce, ciphertext, aad(prologue, nonce, associatedData));
		}
	}
	
	/**
	 * Attempts to recover the content key from the given recipient slot.<br>
	 * <p>
	 *     The commitment is checked first, so a slot that does not belong to this key is rejected without running a cipher.<br>
	 *     The unwrap itself still goes through the non-throwing decryption, because a key id collision would get this far legitimately.
	 * </p>
	 *
	 * @param suite The suite the message was written with
	 * @param recipient The recipient's private key
	 * @param parsed The parsed message
	 * @param slot The slot to attempt
	 * @return The recovered content key, or empty if this slot does not belong to the key
	 * @throws NullPointerException If the suite, the recipient, the parsed message or the slot is null
	 */
	private static @NonNull Optional<byte[]> unwrap(@NonNull CryptoSuite suite, @NonNull PrivateKey recipient, @NonNull Parsed parsed, @NonNull Slot slot) {
		Objects.requireNonNull(suite, "Suite must not be null");
		Objects.requireNonNull(recipient, "Recipient must not be null");
		Objects.requireNonNull(parsed, "Parsed artifact must not be null");
		Objects.requireNonNull(slot, "Slot must not be null");
		
		try (Secret shared = Kems.decapsulate(suite.kem(), recipient, slot.encapsulation()); Secret material = deriveMaterial(suite, shared, parsed.messageId(), slot.encapsulation())) {
			byte[] commitment = CryptoBytes.slice(material.material(), suite.aead().keyLength(), COMMITMENT_LENGTH);
			if (!CryptoBytes.equalsConstantTime(commitment, slot.commitment())) {
				return Optional.empty();
			}
			
			SecretKey wrappingKey = Aeads.key(suite.aead(), CryptoBytes.slice(material.material(), 0, suite.aead().keyLength()));
			int nonceLength = suite.aead().nonceLength();
			if (slot.wrapped().length < nonceLength + suite.aead().tagLength()) {
				throw new MalformedDataException("Wrapped content key too short: " + slot.wrapped().length);
			}
			
			byte[] nonce = CryptoBytes.slice(slot.wrapped(), 0, nonceLength);
			byte[] body = CryptoBytes.slice(slot.wrapped(), nonceLength, slot.wrapped().length - nonceLength);
			return Aeads.tryDecrypt(suite.aead(), wrappingKey, nonce, body, parsed.header());
		}
	}
	
	/**
	 * Builds the fixed header of a layout that carries a key commitment.<br>
	 * The magic is a parameter because the single-recipient layout and the chunked stream layout share this header and differ only in it.<br>
	 *
	 * @param magic The magic of the layout being written
	 * @param suite The suite the message is written with
	 * @param messageId The message id of the message
	 * @param commitment The key commitment of the message
	 * @return The built header
	 * @throws NullPointerException If the magic, the suite, the message id or the commitment is null
	 */
	static byte @NonNull [] header(byte @NonNull [] magic, @NonNull CryptoSuite suite, @NonNull UUID messageId, byte @NonNull [] commitment) {
		Objects.requireNonNull(magic, "Magic must not be null");
		Objects.requireNonNull(suite, "Suite must not be null");
		Objects.requireNonNull(messageId, "Message id must not be null");
		Objects.requireNonNull(commitment, "Commitment must not be null");
		
		return CryptoBytes.concat(magic, new byte[] { VERSION }, CryptoBytes.of(suite.id()), UUIDs.toBytes(messageId), commitment);
	}
	
	/**
	 * Builds the fixed header of the multi-recipient layout.<br>
	 *
	 * @param suite The suite the message is written with
	 * @param messageId The message id of the message
	 * @param count The number of recipients
	 * @return The built header
	 * @throws NullPointerException If the suite or the message id is null
	 */
	private static byte @NonNull [] manyHeader(@NonNull CryptoSuite suite, @NonNull UUID messageId, short count) {
		Objects.requireNonNull(suite, "Suite must not be null");
		Objects.requireNonNull(messageId, "Message id must not be null");
		
		return CryptoBytes.concat(MANY_MAGIC, new byte[] { VERSION }, CryptoBytes.of(suite.id()), UUIDs.toBytes(messageId), CryptoBytes.of(count));
	}
	
	/**
	 * Derives the cipher key and the key commitment from a shared secret.<br>
	 * <p>
	 *     One derivation produces both, so the first bytes key the cipher and the trailing 32 are the commitment.<br>
	 *     This is sound because the output bytes of the key derivation are independent of one another.<br>
	 *     Do not "optimise" it into two derive calls sharing one context, which would produce the same bytes twice.
	 * </p>
	 * <p>
	 *     The suite id is bound into the context as well as into the associated data.<br>
	 *     The associated data already makes a downgrade detectable.<br>
	 *     Binding it here additionally makes the key itself suite-specific, which costs nothing.
	 * </p>
	 *
	 * @param suite The suite being used
	 * @param sharedSecret The shared secret to derive from
	 * @param messageId The message id to bind
	 * @param encapsulation The encapsulation to bind
	 * @return The derived material, cipher key followed by commitment
	 * @throws NullPointerException If the suite, the shared secret, the message id or the encapsulation is null
	 */
	static @NonNull Secret deriveMaterial(@NonNull CryptoSuite suite, @NonNull Secret sharedSecret, @NonNull UUID messageId, byte @NonNull [] encapsulation) {
		Objects.requireNonNull(suite, "Suite must not be null");
		Objects.requireNonNull(sharedSecret, "Shared secret must not be null");
		Objects.requireNonNull(messageId, "Message id must not be null");
		Objects.requireNonNull(encapsulation, "Encapsulation must not be null");
		
		byte[] context = CryptoBytes.concat(KEY_LABEL, CryptoBytes.of(suite.id()), UUIDs.toBytes(messageId), encapsulation);
		return Kdfs.derive(suite.kdf(), sharedSecret, null, context, suite.aead().keyLength() + COMMITMENT_LENGTH);
	}
	
	/**
	 * Builds the associated data of a single-recipient body encryption.<br>
	 *
	 * @param prologue The header and encapsulation
	 * @param nonce The nonce of the body encryption
	 * @param associatedData The caller's extra data, may be null
	 * @return The built associated data
	 * @throws NullPointerException If the prologue or the nonce is null
	 */
	private static byte @NonNull [] aad(byte @NonNull [] prologue, byte @NonNull [] nonce, byte @Nullable [] associatedData) {
		Objects.requireNonNull(prologue, "Prologue must not be null");
		Objects.requireNonNull(nonce, "Nonce must not be null");
		
		return associatedData == null || associatedData.length == 0 ? CryptoBytes.concat(prologue, nonce) : CryptoBytes.concat(prologue, nonce, associatedData);
	}
	
	/**
	 * Builds the associated data of a multi-recipient body encryption.<br>
	 * The whole header and slot region is bound, so no slot can be added, removed or rewritten.<br>
	 *
	 * @param header The fixed header
	 * @param slotBytes The whole slot region
	 * @param associatedData The caller's extra data, may be null
	 * @return The built associated data
	 * @throws NullPointerException If the header or the slot region is null
	 */
	private static byte @NonNull [] manyAad(byte @NonNull [] header, byte @NonNull [] slotBytes, byte @Nullable [] associatedData) {
		Objects.requireNonNull(header, "Header must not be null");
		Objects.requireNonNull(slotBytes, "Slot bytes must not be null");
		
		return associatedData == null || associatedData.length == 0 ? CryptoBytes.concat(header, slotBytes) : CryptoBytes.concat(header, slotBytes, associatedData);
	}
	
	/**
	 * Returns whether the given message starts with the given magic.<br>
	 *
	 * @param sealed The sealed message to inspect
	 * @param magic The magic to look for
	 * @return True if the message starts with the magic
	 * @throws NullPointerException If the sealed message or the magic is null
	 */
	private static boolean startsWith(byte @NonNull [] sealed, byte @NonNull [] magic) {
		Objects.requireNonNull(sealed, "Sealed artifact must not be null");
		Objects.requireNonNull(magic, "Magic must not be null");
		if (sealed.length < magic.length) {
			return false;
		}
		
		for (int i = 0; i < magic.length; i++) {
			if (sealed[i] != magic[i]) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Picks which commitment-carrying magic the given message claims.<br>
	 * A message that claims neither is reported against the single-recipient magic, which is the one a caller of the header accessors is most likely to have meant.<br>
	 *
	 * @param sealed The sealed message to inspect
	 * @return The magic the message claims
	 * @throws NullPointerException If the sealed message is null
	 */
	private static byte @NonNull [] magicOf(byte @NonNull [] sealed) {
		Objects.requireNonNull(sealed, "Sealed artifact must not be null");
		return startsWith(sealed, STREAM_MAGIC) ? STREAM_MAGIC : MAGIC;
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
	 * The parsed fixed header of a layout that carries a key commitment.<br>
	 *
	 * @author Luis-St
	 *
	 * @param suite The suite the message was written with
	 * @param messageId The message id of the message
	 * @param commitment The key commitment of the message
	 */
	record Head(
		@NonNull CryptoSuite suite,
		@NonNull UUID messageId,
		byte @NonNull [] commitment
	) {
		
		/**
		 * Constructs a new parsed header.<br>
		 * @throws NullPointerException If the suite, the message id or the commitment is null
		 */
		Head {
			Objects.requireNonNull(suite, "Suite must not be null");
			Objects.requireNonNull(messageId, "Message id must not be null");
			Objects.requireNonNull(commitment, "Commitment must not be null");
		}
		
		/**
		 * Parses the fixed header of a single-recipient message.<br>
		 *
		 * @param sealed The message to read the header from
		 * @return The parsed header
		 * @throws NullPointerException If the sealed message is null
		 * @throws MalformedDataException If the header is too short, has a bad magic, an unsupported version or an unknown suite
		 */
		static @NonNull Head parse(byte @NonNull [] sealed) {
			return parse(sealed, MAGIC);
		}
		
		/**
		 * Parses the fixed header of a layout that carries a key commitment.<br>
		 * The expected magic is a parameter because the single-recipient layout and the chunked stream layout share this header and differ only in it.<br>
		 *
		 * @param sealed The message to read the header from
		 * @param magic The magic the message must start with
		 * @return The parsed header
		 * @throws NullPointerException If the sealed message or the magic is null
		 * @throws MalformedDataException If the header is too short, has a bad magic, an unsupported version or an unknown suite
		 */
		static @NonNull Head parse(byte @NonNull [] sealed, byte @NonNull [] magic) {
			Objects.requireNonNull(sealed, "Sealed artifact must not be null");
			Objects.requireNonNull(magic, "Magic must not be null");
			if (sealed.length < HEADER_LENGTH) {
				throw new MalformedDataException("Sealed blob too short to contain a header");
			}
			if (!startsWith(sealed, magic)) {
				throw new MalformedDataException("Not a sealed blob (bad magic)");
			}
			if (sealed[magic.length] != VERSION) {
				throw new MalformedDataException("Unsupported sealed format version " + sealed[magic.length]);
			}
			
			int offset = magic.length + 1;
			CryptoSuite suite = CryptoSuite.byId(ByteBuffer.wrap(sealed, offset, Short.BYTES).getShort());
			offset += Short.BYTES;
			UUID messageId = UUIDs.fromBytes(CryptoBytes.slice(sealed, offset, 16));
			offset += 16;
			return new Head(suite, messageId, CryptoBytes.slice(sealed, offset, COMMITMENT_LENGTH));
		}
	}
	
	/**
	 * A parsed multi-recipient message.<br>
	 *
	 * @author Luis-St
	 *
	 * @param suite The suite the message was written with
	 * @param messageId The message id of the message
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
		 * Constructs a new parsed message.<br>
		 * @throws NullPointerException If the suite, the message id, the header, the slot region, the slots or the body is null
		 */
		private Parsed {
			Objects.requireNonNull(suite, "Suite must not be null");
			Objects.requireNonNull(messageId, "Message id must not be null");
			Objects.requireNonNull(header, "Header must not be null");
			Objects.requireNonNull(slotBytes, "Slot bytes must not be null");
			Objects.requireNonNull(slots, "Slots must not be null");
			Objects.requireNonNull(body, "Body must not be null");
		}
		
		/**
		 * Parses a multi-recipient message.<br>
		 * <p>
		 *     Every slot is parsed, including the ones that will not be opened, because the whole slot region is an input to the body's authentication and has to be reproduced exactly.
		 * </p>
		 *
		 * @param sealed The message to parse
		 * @return The parsed message
		 * @throws NullPointerException If the sealed message is null
		 * @throws MalformedDataException If the message is too short, has a bad magic, an unsupported version, an unknown suite or a malformed slot
		 */
		private static @NonNull Parsed of(byte @NonNull [] sealed) {
			Objects.requireNonNull(sealed, "Sealed artifact must not be null");
			
			if (sealed.length < MANY_HEADER_LENGTH) {
				throw new MalformedDataException("Multi-recipient blob too short to contain a header");
			}
			if (!startsWith(sealed, MANY_MAGIC)) {
				throw new MalformedDataException("Not a multi-recipient sealed blob (bad magic)");
			}
			if (sealed[MANY_MAGIC.length] != VERSION) {
				throw new MalformedDataException("Unsupported multi-recipient format version " + sealed[MANY_MAGIC.length]);
			}
			
			int offset = MANY_MAGIC.length + 1;
			CryptoSuite suite = CryptoSuite.byId(ByteBuffer.wrap(sealed, offset, Short.BYTES).getShort());
			offset += Short.BYTES;
			UUID messageId = UUIDs.fromBytes(CryptoBytes.slice(sealed, offset, 16));
			offset += 16;
			int count = ByteBuffer.wrap(sealed, offset, Short.BYTES).getShort() & 0xFFFF;
			offset += Short.BYTES;
			
			byte[] header = CryptoBytes.slice(sealed, 0, MANY_HEADER_LENGTH);
			int encapsulationLength = suite.kem().encapsulationLength();
			int minimumSlot = 16 + COMMITMENT_LENGTH + encapsulationLength + Short.BYTES;
			if (count > (sealed.length - MANY_HEADER_LENGTH) / minimumSlot) {
				throw new MalformedDataException("Recipient count " + count + " does not fit into the remaining " + (sealed.length - MANY_HEADER_LENGTH) + " bytes");
			}
			
			int slotStart = offset;
			List<Slot> slots = new ArrayList<>(count);
			for (int i = 0; i < count; i++) {
				if (sealed.length - offset < minimumSlot) {
					throw new MalformedDataException("Truncated recipient slot " + i);
				}
				
				KeyId keyId = KeyId.fromBytes(CryptoBytes.slice(sealed, offset, 16));
				offset += 16;
				byte[] commitment = CryptoBytes.slice(sealed, offset, COMMITMENT_LENGTH);
				offset += COMMITMENT_LENGTH;
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
