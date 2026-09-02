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
import net.luis.utils.crypto.key.Secret;
import net.luis.utils.crypto.util.CryptoBytes;
import net.luis.utils.crypto.util.CryptoRandom;
import net.luis.utils.util.UUIDs;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.crypto.SecretKey;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Objects;
import java.util.UUID;

/**
 * Public-key encryption of a single message.<br>
 * <p>
 *     The wire format is self-describing:
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
 *     Everything before the ciphertext is fed to the cipher as associated data, so the suite id, the message id,<br>
 *     the commitment and the encapsulation are all cryptographically bound.<br>
 *     An attacker cannot downgrade the suite field without breaking the tag.
 * </p>
 * <p>
 *     {@link #seal} alone is anonymous encryption: anyone holding the recipient's public key can produce a blob that opens correctly.<br>
 *     Use {@link #sealSigned} when the recipient has to know who wrote it.
 * </p>
 * <p>
 *     Example:
 * </p>
 * <pre>{@code
 * KeyPair recipient = Kems.generateKeyPair(CryptoSuite.current().kem());
 *
 * // Anonymous, anyone holding the public key can produce this
 * byte[] sealed = Sealed.seal(recipient.getPublic(), plaintext);
 * byte[] opened = Sealed.unseal(recipient.getPrivate(), sealed);
 *
 * // Authenticated, the recipient learns who wrote it
 * KeyPair sender = Signatures.generateKeyPair(CryptoSuite.current().signature());
 * byte[] signed = Sealed.sealSigned(CryptoSuite.current(), recipient.getPublic(), sender.getPrivate(), plaintext);
 * byte[] verified = Sealed.unsealVerified(recipient.getPrivate(), sender.getPublic(), signed);
 *
 * // Readable without the private key, useful for deciding whether to re-encrypt
 * CryptoSuite used = Sealed.suiteOf(sealed);
 * }</pre>
 *
 * @see CryptoSuite
 * @see SealedStream
 * @see SealedForMany
 *
 * @author Luis-St
 */
public final class Sealed {
	
	/**
	 * The domain separation label of the key derivation.<br>
	 */
	private static final byte[] KEY_LABEL = "lutils-seal-v1".getBytes(StandardCharsets.UTF_8);
	/**
	 * The magic value every sealed artifact starts with.<br>
	 */
	static final byte[] MAGIC = "LUC1".getBytes(StandardCharsets.US_ASCII);
	/**
	 * The version of the sealed wire format.<br>
	 */
	static final byte VERSION = 1;
	/**
	 * The length of the key commitment in bytes.<br>
	 */
	static final int COMMITMENT_LENGTH = 32;
	/**
	 * The length of the fixed header in bytes.<br>
	 */
	static final int HEADER_LENGTH = MAGIC.length + 1 + Short.BYTES + 16 + COMMITMENT_LENGTH;
	
	/**
	 * Private constructor to prevent instantiation.<br>
	 * This is a static helper class.<br>
	 */
	private Sealed() {}
	
	/**
	 * Encrypts to the given recipient using the current suite.<br>
	 * <p>
	 *     This provides confidentiality and integrity, but not sender authentication: anyone holding the recipient's public key can produce a blob that unseals correctly.<br>
	 *     Use {@link #sealSigned} when the recipient must know who wrote it.
	 * </p>
	 *
	 * @param recipient The public key to encrypt to
	 * @param plaintext The message to encrypt
	 * @return The sealed artifact
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
	 * @return The sealed artifact
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
	 * @return The sealed artifact
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
				SecretKey key = Aead.key(suite.aead(), CryptoBytes.slice(material.material(), 0, suite.aead().keyLength()));
				byte[] commitment = CryptoBytes.slice(material.material(), suite.aead().keyLength(), COMMITMENT_LENGTH);
				
				byte[] prologue = CryptoBytes.concat(header(suite, messageId, commitment), encapsulated.encapsulation());
				byte[] nonce = CryptoRandom.bytes(random, suite.aead().nonceLength());
				byte[] ciphertext = Aead.encrypt(suite.aead(), key, nonce, plaintext, aad(prologue, nonce, associatedData));
				return CryptoBytes.concat(prologue, nonce, ciphertext);
			}
		}
	}
	
	/**
	 * Decrypts a sealed artifact.<br>
	 *
	 * @param recipient The private key to decrypt with
	 * @param sealed The sealed artifact
	 * @return The recovered message
	 * @throws NullPointerException If the recipient or the sealed artifact is null
	 * @throws MalformedDataException If the artifact is not readable
	 * @throws AuthenticationException If the artifact does not authenticate under this key
	 */
	public static byte @NonNull [] unseal(@NonNull PrivateKey recipient, byte @NonNull [] sealed) {
		return unseal(recipient, sealed, null);
	}
	
	/**
	 * Decrypts a sealed artifact.<br>
	 *
	 * @param recipient The private key to decrypt with
	 * @param sealed The sealed artifact
	 * @param associatedData The extra data that was authenticated when sealing, may be null
	 * @return The recovered message
	 * @throws NullPointerException If the recipient or the sealed artifact is null
	 * @throws MalformedDataException If the artifact is not readable
	 * @throws AuthenticationException If the artifact does not authenticate under this key
	 */
	public static byte @NonNull [] unseal(@NonNull PrivateKey recipient, byte @NonNull [] sealed, byte @Nullable [] associatedData) {
		Objects.requireNonNull(recipient, "Recipient must not be null");
		Objects.requireNonNull(sealed, "Sealed artifact must not be null");
		
		return unseal(recipient, Head.parse(sealed), sealed, associatedData);
	}
	
	/**
	 * Decrypts a sealed artifact whose header has already been parsed.<br>
	 *
	 * @param recipient The private key to decrypt with
	 * @param head The parsed header
	 * @param sealed The sealed artifact
	 * @param associatedData The extra data that was authenticated when sealing, may be null
	 * @return The recovered message
	 * @throws NullPointerException If the recipient, the head or the sealed artifact is null
	 * @throws MalformedDataException If the artifact is not readable
	 * @throws AuthenticationException If the artifact does not authenticate under this key
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
			SecretKey key = Aead.key(suite.aead(), CryptoBytes.slice(material.material(), 0, suite.aead().keyLength()));
			byte[] commitment = CryptoBytes.slice(material.material(), suite.aead().keyLength(), COMMITMENT_LENGTH);
			
			if (!CryptoBytes.equalsConstantTime(commitment, head.commitment())) {
				throw new AuthenticationException("Key commitment mismatch - the header does not belong to this key");
			}
			return Aead.decrypt(suite.aead(), key, nonce, ciphertext, aad(prologue, nonce, associatedData));
		}
	}
	
	/**
	 * Signs the plaintext with the sender's key, then seals plaintext and signature together.<br>
	 * <p>
	 *     The signature is inside the encryption, so it does not reveal the sender to anyone who cannot already decrypt the artifact.
	 * </p>
	 *
	 * @param suite The suite to sign and encrypt with
	 * @param recipient The public key to encrypt to
	 * @param sender The private key to sign with
	 * @param plaintext The message to sign and encrypt
	 * @return The sealed artifact
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
	 * Unseals an artifact and verifies the sender's signature.<br>
	 *
	 * @param recipient The private key to decrypt with
	 * @param sender The public key the signature must verify against
	 * @param sealed The sealed artifact
	 * @return The recovered message
	 * @throws NullPointerException If the recipient, the sender or the sealed artifact is null
	 * @throws MalformedDataException If the artifact is not readable
	 * @throws AuthenticationException If the blob does not decrypt, or the signature does not match the sender
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
	 * Reads the suite of an artifact without decrypting it.<br>
	 * Useful for deciding whether to re-encrypt under a newer suite.<br>
	 *
	 * @param sealed The sealed artifact
	 * @return The suite the artifact was written with
	 * @throws NullPointerException If the sealed artifact is null
	 * @throws MalformedDataException If the header is not readable
	 */
	public static @NonNull CryptoSuite suiteOf(byte @NonNull [] sealed) {
		return Head.parse(sealed).suite();
	}
	
	/**
	 * Reads the time-ordered message id of an artifact without decrypting it.<br>
	 * The creation time is recoverable from it through {@link UUIDs#unixMillis(UUID)}.<br>
	 *
	 * @param sealed The sealed artifact
	 * @return The message id of the artifact
	 * @throws NullPointerException If the sealed artifact is null
	 * @throws MalformedDataException If the header is not readable
	 */
	public static @NonNull UUID messageIdOf(byte @NonNull [] sealed) {
		return Head.parse(sealed).messageId();
	}
	
	/**
	 * Builds the fixed header of a sealed artifact.<br>
	 *
	 * @param suite The suite the artifact is written with
	 * @param messageId The message id of the artifact
	 * @param commitment The key commitment of the artifact
	 * @return The built header
	 * @throws NullPointerException If the suite, the message id or the commitment is null
	 */
	static byte @NonNull [] header(@NonNull CryptoSuite suite, @NonNull UUID messageId, byte @NonNull [] commitment) {
		Objects.requireNonNull(suite, "Suite must not be null");
		Objects.requireNonNull(messageId, "Message id must not be null");
		Objects.requireNonNull(commitment, "Commitment must not be null");
		
		return CryptoBytes.concat(MAGIC, new byte[] { VERSION }, CryptoBytes.of(suite.id()), UUIDs.toBytes(messageId), commitment);
	}
	
	/**
	 * Derives the cipher key and the key commitment from a shared secret.<br>
	 * <p>
	 *     One derivation produces both: the first bytes key the cipher, the trailing 32 are the commitment.<br>
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
		return Kdf.derive(suite.kdf(), sharedSecret.material(), null, context, suite.aead().keyLength() + COMMITMENT_LENGTH);
	}
	
	/**
	 * Builds the associated data of the body encryption.<br>
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
	 * The parsed fixed header of a sealed artifact.<br>
	 *
	 * @author Luis-St
	 *
	 * @param suite The suite the artifact was written with
	 * @param messageId The message id of the artifact
	 * @param commitment The key commitment of the artifact
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
		 * Parses the fixed header of a sealed artifact.<br>
		 *
		 * @param sealed The artifact to read the header from
		 * @return The parsed header
		 * @throws NullPointerException If the sealed artifact is null
		 * @throws MalformedDataException If the header is too short, has a bad magic, an unsupported version or an unknown suite
		 */
		static @NonNull Head parse(byte @NonNull [] sealed) {
			Objects.requireNonNull(sealed, "Sealed artifact must not be null");
			if (sealed.length < HEADER_LENGTH) {
				throw new MalformedDataException("Sealed blob too short to contain a header");
			}
			for (int i = 0; i < MAGIC.length; i++) {
				if (sealed[i] != MAGIC[i]) {
					throw new MalformedDataException("Not a sealed blob (bad magic)");
				}
			}
			if (sealed[MAGIC.length] != VERSION) {
				throw new MalformedDataException("Unsupported sealed format version " + sealed[MAGIC.length]);
			}
			
			int offset = MAGIC.length + 1;
			CryptoSuite suite = CryptoSuite.byId(ByteBuffer.wrap(sealed, offset, Short.BYTES).getShort());
			offset += Short.BYTES;
			UUID messageId = UUIDs.fromBytes(CryptoBytes.slice(sealed, offset, 16));
			offset += 16;
			return new Head(suite, messageId, CryptoBytes.slice(sealed, offset, COMMITMENT_LENGTH));
		}
	}
}
