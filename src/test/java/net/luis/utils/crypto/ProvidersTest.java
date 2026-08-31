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

import net.luis.utils.crypto.algorithm.*;
import net.luis.utils.crypto.exception.UnsupportedAlgorithmException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.Property;
import org.junit.jupiter.api.*;

import javax.crypto.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.security.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

/**
 * Test class for {@link Providers}.<br>
 *
 * @author Luis-St
 */
class ProvidersTest {
	
	private static final NativeSignatureAlgorithm FAKE_SIGNATURE = new NativeSignatureAlgorithm("fake", "NoSuchSig", "NoSuchKey", null, 1, false, false);
	private static final NativeSignatureAlgorithm FAKE_SIGNATURE_KEY = new NativeSignatureAlgorithm("fake-key", "Ed25519", "NoSuchKey", null, 1, false, false);
	private static final NativeKemAlgorithm FAKE_KEM = new NativeKemAlgorithm("fake", "NoSuchKem", "NoSuchKey", 1, 1, 1);
	private static final DhKemAlgorithm FAKE_DH_KEM = new DhKemAlgorithm("NoSuchCurve", 32, 32);
	
	private final List<String> messages = new ArrayList<>();
	private CapturingAppender appender;
	
	@BeforeAll
	static void installProvider() {
		Providers.installBouncyCastle();
	}
	
	private static CryptoSuite suite(AeadAlgorithm aead, KemAlgorithm kem, SignatureAlgorithm signature) {
		return new CryptoSuite((short) 0, "test-suite", aead, kem, signature, KdfAlgorithm.HKDF_SHA_256, HashAlgorithm.SHA_256, false);
	}
	
	private static boolean canCreate(Creator creator) {
		try {
			return creator.create() != null;
		} catch (GeneralSecurityException e) {
			return false;
		}
	}
	
	@BeforeEach
	void attachAppender() {
		this.messages.clear();
		this.appender = new CapturingAppender(this.messages);
		this.appender.start();
		Configurator.setLevel(Providers.class.getName(), Level.INFO);
		((Logger) org.apache.logging.log4j.LogManager.getLogger(Providers.class)).addAppender(this.appender);
	}
	
	@AfterEach
	void detachAppender() {
		((Logger) org.apache.logging.log4j.LogManager.getLogger(Providers.class)).removeAppender(this.appender);
		this.appender.stop();
	}
	
	private String captured() {
		assertEquals(1, this.messages.size());
		return this.messages.getFirst();
	}
	
	@Test
	void constructorIsPrivate() throws Exception {
		Constructor<?>[] constructors = Providers.class.getDeclaredConstructors();
		assertEquals(1, constructors.length);
		assertTrue(Modifier.isPrivate(constructors[0].getModifiers()));
		assertTrue(Modifier.isFinal(Providers.class.getModifiers()));
		
		Constructor<Providers> constructor = Providers.class.getDeclaredConstructor();
		constructor.setAccessible(true);
		assertNotNull(constructor.newInstance());
	}
	
	@Test
	void supportsHashWithNullAlgorithm() {
		assertThrows(NullPointerException.class, () -> Providers.supports((HashAlgorithm) null));
	}
	
	@Test
	void supportsMacWithNullAlgorithm() {
		assertThrows(NullPointerException.class, () -> Providers.supports((MacAlgorithm) null));
	}
	
	@Test
	void supportsAeadWithNullAlgorithm() {
		assertThrows(NullPointerException.class, () -> Providers.supports((AeadAlgorithm) null));
	}
	
	@Test
	void supportsKemWithNullAlgorithm() {
		assertThrows(NullPointerException.class, () -> Providers.supports((KemAlgorithm) null));
	}
	
	@Test
	void supportsSignatureWithNullAlgorithm() {
		assertThrows(NullPointerException.class, () -> Providers.supports((SignatureAlgorithm) null));
	}
	
	@Test
	void supportsKdfWithNullAlgorithm() {
		assertThrows(NullPointerException.class, () -> Providers.supports((KdfAlgorithm) null));
	}
	
	@Test
	void supportsPasswordWithNullAlgorithm() {
		assertThrows(NullPointerException.class, () -> Providers.supports((PasswordAlgorithm) null));
	}
	
	@Test
	void isAvailableWithNullService() {
		NullPointerException exception = assertThrows(NullPointerException.class, () -> Providers.isAvailable(null, "SHA-256"));
		assertEquals("Service must not be null", exception.getMessage());
	}
	
	@Test
	void isAvailableWithNullAlgorithm() {
		NullPointerException exception = assertThrows(NullPointerException.class, () -> Providers.isAvailable("MessageDigest", null));
		assertEquals("Algorithm must not be null", exception.getMessage());
	}
	
	@Test
	void isAvailableWithBothNull() {
		NullPointerException exception = assertThrows(NullPointerException.class, () -> Providers.isAvailable(null, null));
		assertEquals("Service must not be null", exception.getMessage());
	}
	
	@Test
	void preferredWithNullService() {
		NullPointerException exception = assertThrows(NullPointerException.class, () -> Providers.preferred(null, "SHA-256"));
		assertEquals("Service must not be null", exception.getMessage());
	}
	
	@Test
	void preferredWithNullAlgorithm() {
		NullPointerException exception = assertThrows(NullPointerException.class, () -> Providers.preferred("MessageDigest", null));
		assertEquals("Algorithm must not be null", exception.getMessage());
	}
	
	@Test
	void requireWithNullSuite() {
		NullPointerException exception = assertThrows(NullPointerException.class, () -> Providers.require(null));
		assertEquals("Suite must not be null", exception.getMessage());
	}
	
	@Test
	void logProvidersWithNullSuite() {
		NullPointerException exception = assertThrows(NullPointerException.class, () -> Providers.logProviders(null));
		assertEquals("Suite must not be null", exception.getMessage());
	}
	
	@Test
	void kemWithNullAlgorithm() {
		NullPointerException exception = assertThrows(NullPointerException.class, () -> Providers.kem(null));
		assertEquals("Algorithm must not be null", exception.getMessage());
	}
	
	@Test
	void kemWithUnknownAlgorithm() {
		UnsupportedAlgorithmException exception = assertThrows(UnsupportedAlgorithmException.class, () -> Providers.kem("NOT-A-KEM"));
		assertTrue(exception.getMessage().contains("NOT-A-KEM"));
		assertInstanceOf(NoSuchAlgorithmException.class, exception.getCause());
	}
	
	@Test
	void requireWithUnservableSuite() {
		CryptoSuite unservable = suite(AeadAlgorithm.AES_256_GCM, KemAlgorithm.X25519, FAKE_SIGNATURE);
		UnsupportedAlgorithmException exception = assertThrows(UnsupportedAlgorithmException.class, () -> Providers.require(unservable));
		
		assertTrue(exception.getMessage().contains("test-suite"));
		assertTrue(exception.getCause().getMessage().contains("Signature fake"));
		assertTrue(exception.getCause().getMessage().contains("JDK " + Runtime.version().feature()));
		assertTrue(exception.getCause().getMessage().contains("BouncyCastle "));
	}
	
	@Test
	void requireListsEveryMissingAlgorithm() {
		CryptoSuite unservable = suite(AeadAlgorithm.AES_256_GCM, FAKE_KEM, FAKE_SIGNATURE);
		UnsupportedAlgorithmException exception = assertThrows(UnsupportedAlgorithmException.class, () -> Providers.require(unservable));
		String message = exception.getCause().getMessage();
		
		assertTrue(message.contains("KEM       fake"));
		assertTrue(message.contains("Signature fake"));
		assertTrue(message.contains("\n  "));
	}
	
	@Test
	void isBouncyCastleAvailableAfterInstallation() {
		assertTrue(Providers.isBouncyCastleAvailable());
		assertNotNull(Security.getProvider("BC"));
		assertSame(Providers.bouncyCastle(), Security.getProvider("BC"));
	}
	
	@Test
	void installBouncyCastleWhenAlreadyInstalled() {
		long before = Arrays.stream(Security.getProviders()).filter(provider -> "BC".equals(provider.getName())).count();
		assertTrue(Providers.installBouncyCastle());
		assertTrue(Providers.installBouncyCastle());
		
		long after = Arrays.stream(Security.getProviders()).filter(provider -> "BC".equals(provider.getName())).count();
		assertEquals(1, before);
		assertEquals(before, after);
	}
	
	@Test
	void installBouncyCastleWhenOnPath() {
		assumeTrue(Providers.isBouncyCastleOnPath());
		assertTrue(Providers.installBouncyCastle());
		assertTrue(Providers.isBouncyCastleAvailable());
	}
	
	@Test
	void isBouncyCastleOnPathIsConsistentWithInstall() {
		if (Providers.isBouncyCastleOnPath()) {
			assertTrue(Providers.installBouncyCastle());
		} else {
			assertEquals(Providers.isBouncyCastleAvailable(), Providers.installBouncyCastle());
		}
	}
	
	@Test
	void isAvailableWithServedAlgorithm() {
		assertTrue(Providers.isAvailable("MessageDigest", "SHA-256"));
	}
	
	@Test
	void isAvailableWithUnservedAlgorithm() {
		assertFalse(Providers.isAvailable("MessageDigest", "NOT-A-HASH"));
		assertFalse(Providers.isAvailable("NotAService", "SHA-256"));
	}
	
	@Test
	void isAvailableWithEmptyStrings() {
		assertThrows(InvalidParameterException.class, () -> Providers.isAvailable("", ""));
		assertThrows(InvalidParameterException.class, () -> Providers.isAvailable("MessageDigest", ""));
		assertThrows(InvalidParameterException.class, () -> Providers.preferred("MessageDigest", ""));
	}
	
	@Test
	void preferredWithServedAlgorithm() {
		Optional<Provider> provider = Providers.preferred("MessageDigest", "SHA-256");
		assertTrue(provider.isPresent());
		assertNotNull(provider.orElseThrow().getService("MessageDigest", "SHA-256"));
	}
	
	@Test
	void preferredWithUnservedAlgorithm() {
		assertTrue(Providers.preferred("MessageDigest", "NOT-A-HASH").isEmpty());
		assertTrue(Providers.preferred("NotAService", "SHA-256").isEmpty());
	}
	
	@Test
	void supportsAeadServedByRegistry() {
		assertTrue(Providers.isAvailable("Cipher", AeadAlgorithm.AES_256_GCM.jcaName()));
		assertTrue(Providers.supports(AeadAlgorithm.AES_256_GCM));
		assertTrue(Providers.supports(AeadAlgorithm.CHACHA20_POLY1305));
	}
	
	@Test
	void supportsAeadServedThroughBouncyCastleFallback() {
		for (AeadAlgorithm algorithm : new AeadAlgorithm[] { AeadAlgorithm.AES_256_GCM_SIV, AeadAlgorithm.XCHACHA20_POLY1305 }) {
			assertTrue(algorithm.requiresBouncyCastle());
			assertTrue(Providers.supports(algorithm));
			assertEquals(Providers.isAvailable("Cipher", algorithm.jcaName()) || Providers.isBouncyCastleAvailable(), Providers.supports(algorithm));
		}
	}
	
	@Test
	void supportsNativeKem() {
		for (KemAlgorithm algorithm : List.of(KemAlgorithm.ML_KEM_512, KemAlgorithm.ML_KEM_768, KemAlgorithm.ML_KEM_1024)) {
			NativeKemAlgorithm native0 = assertInstanceOf(NativeKemAlgorithm.class, algorithm);
			assertEquals(Providers.isAvailable("KEM", native0.jcaName()) && Providers.isAvailable("KeyPairGenerator", native0.keyJcaName()), Providers.supports(algorithm));
		}
	}
	
	@Test
	void supportsDhKem() {
		for (KemAlgorithm algorithm : List.of(KemAlgorithm.X25519, KemAlgorithm.X448)) {
			DhKemAlgorithm dh = assertInstanceOf(DhKemAlgorithm.class, algorithm);
			assertTrue(Providers.isAvailable("KEM", dh.jcaName()));
			assertTrue(Providers.isAvailable("KeyPairGenerator", dh.keyJcaName()));
			assertTrue(Providers.supports(algorithm));
		}
	}
	
	@Test
	void supportsDhKemAsksForTheKemService() {
		DhKemAlgorithm x25519 = assertInstanceOf(DhKemAlgorithm.class, KemAlgorithm.X25519);
		DhKemAlgorithm x448 = assertInstanceOf(DhKemAlgorithm.class, KemAlgorithm.X448);
		
		assertEquals("DHKEM", x25519.jcaName());
		assertEquals(x25519.jcaName(), x448.jcaName());
		assertEquals(Providers.isAvailable("KEM", "DHKEM"), Providers.supports(KemAlgorithm.X25519));
	}
	
	@Test
	void supportsDhKemFalseWhenComponentMissing() {
		assertEquals("DHKEM", FAKE_DH_KEM.jcaName());
		assertTrue(Providers.isAvailable("KEM", FAKE_DH_KEM.jcaName()));
		assertFalse(Providers.isAvailable("KeyPairGenerator", FAKE_DH_KEM.keyJcaName()));
		assertFalse(Providers.supports(FAKE_DH_KEM));
	}
	
	@Test
	void supportsHybridKem() {
		for (KemAlgorithm algorithm : List.of(KemAlgorithm.X25519_ML_KEM_768, KemAlgorithm.X448_ML_KEM_1024)) {
			HybridKemAlgorithm hybrid = assertInstanceOf(HybridKemAlgorithm.class, algorithm);
			assertEquals(Providers.supports(hybrid.classical()) && Providers.supports(hybrid.postQuantum()), Providers.supports(algorithm));
		}
	}
	
	@Test
	void supportsHybridKemFalseWhenComponentMissing() {
		DhKemAlgorithm classical = assertInstanceOf(DhKemAlgorithm.class, KemAlgorithm.X25519);
		NativeKemAlgorithm postQuantum = assertInstanceOf(NativeKemAlgorithm.class, KemAlgorithm.ML_KEM_768);
		
		assertFalse(Providers.supports(new HybridKemAlgorithm(classical, FAKE_KEM)));
		assertFalse(Providers.supports(new HybridKemAlgorithm(FAKE_DH_KEM, postQuantum)));
	}
	
	@Test
	void supportsNativeSignature() {
		for (SignatureAlgorithm algorithm : List.of(SignatureAlgorithm.ED25519, SignatureAlgorithm.ED448, SignatureAlgorithm.ECDSA_P256_SHA_256, SignatureAlgorithm.ECDSA_P384_SHA_384, SignatureAlgorithm.ECDSA_P521_SHA_512)) {
			assertTrue(Providers.supports(algorithm), algorithm.name());
		}
	}
	
	@Test
	void supportsHybridSignature() {
		for (SignatureAlgorithm algorithm : SignatureAlgorithm.VALUES) {
			if (algorithm instanceof HybridSignatureAlgorithm hybrid) {
				assertEquals(Providers.supports(hybrid.classical()) && Providers.supports(hybrid.postQuantum()), Providers.supports(algorithm), hybrid.name());
			}
		}
	}
	
	@Test
	void supportsSignatureFalseWhenComponentMissing() {
		assertFalse(Providers.supports(FAKE_SIGNATURE));
		assertFalse(Providers.supports(FAKE_SIGNATURE_KEY));
		assertTrue(Providers.isAvailable("Signature", FAKE_SIGNATURE_KEY.jcaName()));
	}
	
	@Test
	void supportsPasswordRequiringBouncyCastle() {
		for (PasswordAlgorithm algorithm : List.of(PasswordAlgorithm.ARGON2ID, PasswordAlgorithm.SCRYPT)) {
			assertTrue(algorithm.requiresBouncyCastle());
			assertNotNull(Providers.bouncyCastle().getService("SecretKeyFactory", algorithm.jcaName()));
			assertTrue(Providers.supports(algorithm));
		}
	}
	
	@Test
	void supportsPasswordServedByJdk() {
		assertFalse(PasswordAlgorithm.PBKDF2_HMAC_SHA_512.requiresBouncyCastle());
		assertTrue(Providers.supports(PasswordAlgorithm.PBKDF2_HMAC_SHA_512));
	}
	
	@Test
	void supportsHashForEveryConstant() {
		for (HashAlgorithm algorithm : HashAlgorithm.values()) {
			assertTrue(Providers.supports(algorithm), algorithm.jcaName());
		}
	}
	
	@Test
	void supportsMacForEveryConstant() {
		for (MacAlgorithm algorithm : MacAlgorithm.values()) {
			assertTrue(Providers.supports(algorithm), algorithm.jcaName());
		}
	}
	
	@Test
	void supportsKdfForEveryConstant() {
		for (KdfAlgorithm algorithm : KdfAlgorithm.values()) {
			assertEquals(Providers.isAvailable("KDF", algorithm.jcaName()), Providers.supports(algorithm));
			assertTrue(Providers.supports(algorithm), algorithm.jcaName());
		}
	}
	
	@Test
	void supportsKdfDoesNotDelegateToTheMac() {
		for (KdfAlgorithm algorithm : KdfAlgorithm.values()) {
			assertTrue(Providers.isAvailable("KDF", algorithm.jcaName()));
			assertNotEquals(algorithm.jcaName(), algorithm.mac().jcaName());
			assertEquals(Providers.isAvailable("KDF", algorithm.jcaName()), Providers.supports(algorithm));
		}
	}
	
	@Test
	void requireWithServableSuite() {
		assertDoesNotThrow(() -> Providers.require(CryptoSuite.current()));
	}
	
	@Test
	void logProvidersWithNativeAlgorithms() {
		Providers.logProviders(CryptoSuite.POST_QUANTUM_V1);
		String line = this.captured();
		
		NativeKemAlgorithm kem = assertInstanceOf(NativeKemAlgorithm.class, CryptoSuite.POST_QUANTUM_V1.kem());
		NativeSignatureAlgorithm signature = assertInstanceOf(NativeSignatureAlgorithm.class, CryptoSuite.POST_QUANTUM_V1.signature());
		assertTrue(line.contains("KEM=" + Providers.preferred("KEM", kem.jcaName()).orElseThrow().getName()));
		assertTrue(line.contains("Signature=" + Providers.preferred("Signature", signature.jcaName()).orElseThrow().getName()));
		assertFalse(line.contains("KEM=" + kem.name()));
	}
	
	@Test
	void logProvidersWithHybridAlgorithms() {
		Providers.logProviders(CryptoSuite.HYBRID_V1);
		String line = this.captured();
		
		assertTrue(line.contains("KEM=" + CryptoSuite.HYBRID_V1.kem().name()));
		assertTrue(line.contains("Signature=" + CryptoSuite.HYBRID_V1.signature().name()));
	}
	
	@Test
	void logProvidersWithDhKem() {
		Providers.logProviders(CryptoSuite.CLASSICAL_V1);
		String line = this.captured();
		
		assertTrue(line.contains("KEM=" + Providers.preferred("KEM", "DHKEM").orElseThrow().getName()));
		assertFalse(line.contains("KEM=X25519"));
	}
	
	@Test
	void logProvidersReportsTheKdfService() {
		Providers.logProviders(CryptoSuite.HYBRID_V1);
		String line = this.captured();
		
		assertTrue(line.contains("KDF=" + Providers.preferred("KDF", "HKDF-SHA256").orElseThrow().getName()));
		assertFalse(line.contains("MAC="));
	}
	
	@Test
	void logProvidersReportsEverySlot() {
		Providers.logProviders(CryptoSuite.HYBRID_V1);
		String line = this.captured();
		
		assertTrue(line.contains(CryptoSuite.HYBRID_V1.name()));
		int aead = line.indexOf("AEAD=");
		int kem = line.indexOf("KEM=");
		int signature = line.indexOf("Signature=");
		int kdf = line.indexOf("KDF=");
		int hash = line.indexOf("Hash=");
		assertTrue(aead >= 0 && aead < kem && kem < signature && signature < kdf && kdf < hash);
	}
	
	@Test
	void logProvidersWithUnservedAlgorithm() {
		assertDoesNotThrow(() -> Providers.logProviders(suite(AeadAlgorithm.AES_256_GCM, FAKE_KEM, FAKE_SIGNATURE)));
		String line = this.captured();
		
		assertTrue(line.contains("KEM=none"));
		assertTrue(line.contains("Signature=none"));
	}
	
	@Test
	void servingKemCoversEveryVariant() {
		DhKemAlgorithm classical = assertInstanceOf(DhKemAlgorithm.class, KemAlgorithm.X25519);
		NativeKemAlgorithm postQuantum = assertInstanceOf(NativeKemAlgorithm.class, KemAlgorithm.ML_KEM_768);
		
		for (KemAlgorithm kem : List.of(classical, postQuantum, new HybridKemAlgorithm(classical, postQuantum))) {
			this.messages.clear();
			Providers.logProviders(suite(AeadAlgorithm.AES_256_GCM, kem, SignatureAlgorithm.ED25519));
			String slot = this.captured().split("KEM=", 2)[1].split(",", 2)[0];
			
			assertFalse(slot.isBlank());
			assertEquals(kem instanceof HybridKemAlgorithm, slot.equals(kem.name()));
		}
	}
	
	@Test
	void kemWithServedAlgorithm() {
		assumeTrue(Providers.isAvailable("KEM", "ML-KEM"));
		assertNotNull(assertDoesNotThrow(() -> Providers.kem("ML-KEM")));
	}
	
	@Test
	void bouncyCastleReturnsSharedInstance() {
		assertSame(Providers.bouncyCastle(), Providers.bouncyCastle());
		assertEquals("BC", Providers.bouncyCastle().getName());
	}
	
	@Test
	void bouncyCastleInstanceIsTheInstalledOne() {
		Providers.installBouncyCastle();
		assertSame(Providers.bouncyCastle(), Security.getProvider("BC"));
	}
	
	@Test
	void supportsIsStableAcrossCalls() {
		assertEquals(Providers.supports(HashAlgorithm.SHA_256), Providers.supports(HashAlgorithm.SHA_256));
		assertEquals(Providers.supports(MacAlgorithm.HMAC_SHA_256), Providers.supports(MacAlgorithm.HMAC_SHA_256));
		assertEquals(Providers.supports(AeadAlgorithm.AES_256_GCM), Providers.supports(AeadAlgorithm.AES_256_GCM));
		assertEquals(Providers.supports(KemAlgorithm.X25519), Providers.supports(KemAlgorithm.X25519));
		assertEquals(Providers.supports(SignatureAlgorithm.ED25519), Providers.supports(SignatureAlgorithm.ED25519));
		assertEquals(Providers.supports(KdfAlgorithm.HKDF_SHA_256), Providers.supports(KdfAlgorithm.HKDF_SHA_256));
		assertEquals(Providers.supports(PasswordAlgorithm.ARGON2ID), Providers.supports(PasswordAlgorithm.ARGON2ID));
	}
	
	@Test
	void isAvailableAgreesWithPreferred() {
		String[][] pairs = { { "MessageDigest", "SHA-256" }, { "Mac", "HmacSHA512" }, { "KDF", "HKDF-SHA256" }, { "MessageDigest", "NOT-A-HASH" }, { "NotAService", "SHA-256" } };
		for (String[] pair : pairs) {
			assertEquals(Providers.isAvailable(pair[0], pair[1]), Providers.preferred(pair[0], pair[1]).isPresent());
		}
	}
	
	@Test
	void preferredReturnsFirstProvider() throws Exception {
		assertEquals(MessageDigest.getInstance("SHA-256").getProvider(), Providers.preferred("MessageDigest", "SHA-256").orElseThrow());
	}
	
	@Test
	void supportsAgreesWithActualInstantiation() {
		for (HashAlgorithm algorithm : HashAlgorithm.values()) {
			assertEquals(Providers.supports(algorithm), canCreate(() -> MessageDigest.getInstance(algorithm.jcaName())));
		}
		for (MacAlgorithm algorithm : MacAlgorithm.values()) {
			assertEquals(Providers.supports(algorithm), canCreate(() -> Mac.getInstance(algorithm.jcaName())));
		}
		for (KdfAlgorithm algorithm : KdfAlgorithm.values()) {
			assertEquals(Providers.supports(algorithm), canCreate(() -> KDF.getInstance(algorithm.jcaName())));
		}
		for (PasswordAlgorithm algorithm : PasswordAlgorithm.VALUES) {
			assertEquals(Providers.supports(algorithm), canCreate(() -> algorithm.requiresBouncyCastle() ? SecretKeyFactory.getInstance(algorithm.jcaName(), Providers.bouncyCastle()) : SecretKeyFactory.getInstance(algorithm.jcaName())));
		}
		for (KemAlgorithm algorithm : KemAlgorithm.VALUES) {
			if (algorithm instanceof HybridKemAlgorithm) {
				continue;
			}
			assertEquals(Providers.supports(algorithm), canCreate(() -> KEM.getInstance(algorithm instanceof NativeKemAlgorithm n ? n.jcaName() : "DHKEM")) && Providers.isAvailable("KeyPairGenerator", algorithm instanceof NativeKemAlgorithm n ? n.keyJcaName() : ((DhKemAlgorithm) algorithm).keyJcaName()));
		}
		for (SignatureAlgorithm algorithm : SignatureAlgorithm.VALUES) {
			if (algorithm instanceof NativeSignatureAlgorithm n) {
				assertEquals(Providers.supports(algorithm), canCreate(() -> Signature.getInstance(n.jcaName())) && Providers.isAvailable("KeyPairGenerator", n.keyJcaName()), n.name());
			}
		}
	}
	
	@Test
	void supportsNeverThrowsForAnyConstant() {
		for (HashAlgorithm algorithm : HashAlgorithm.values()) {
			assertDoesNotThrow(() -> Providers.supports(algorithm));
		}
		for (MacAlgorithm algorithm : MacAlgorithm.values()) {
			assertDoesNotThrow(() -> Providers.supports(algorithm));
		}
		for (AeadAlgorithm algorithm : AeadAlgorithm.values()) {
			assertDoesNotThrow(() -> Providers.supports(algorithm));
		}
		for (KdfAlgorithm algorithm : KdfAlgorithm.values()) {
			assertDoesNotThrow(() -> Providers.supports(algorithm));
		}
		for (KemAlgorithm algorithm : KemAlgorithm.VALUES) {
			assertDoesNotThrow(() -> Providers.supports(algorithm));
		}
		for (SignatureAlgorithm algorithm : SignatureAlgorithm.VALUES) {
			assertDoesNotThrow(() -> Providers.supports(algorithm));
		}
		for (PasswordAlgorithm algorithm : PasswordAlgorithm.VALUES) {
			assertDoesNotThrow(() -> Providers.supports(algorithm));
		}
	}
	
	@Test
	void installBouncyCastleIsIdempotent() {
		Provider first = Security.getProvider("BC");
		for (int i = 0; i < 3; i++) {
			assertTrue(Providers.installBouncyCastle());
			assertEquals(1, Arrays.stream(Security.getProviders()).filter(provider -> "BC".equals(provider.getName())).count());
			assertSame(first, Security.getProvider("BC"));
		}
	}
	
	@Test
	void requireAndSupportsAgree() {
		DhKemAlgorithm classical = assertInstanceOf(DhKemAlgorithm.class, KemAlgorithm.X25519);
		List<CryptoSuite> suites = List.of(
			CryptoSuite.current(),
			suite(AeadAlgorithm.AES_256_GCM, FAKE_KEM, SignatureAlgorithm.ED25519),
			suite(AeadAlgorithm.AES_256_GCM, classical, FAKE_SIGNATURE),
			suite(AeadAlgorithm.AES_256_GCM, FAKE_KEM, FAKE_SIGNATURE)
		);
		
		for (CryptoSuite candidate : suites) {
			boolean servable = Providers.supports(candidate.aead()) && Providers.supports(candidate.kem()) && Providers.supports(candidate.signature()) && Providers.supports(candidate.kdf()) && Providers.supports(candidate.hash());
			if (servable) {
				assertDoesNotThrow(() -> Providers.require(candidate));
			} else {
				assertThrows(UnsupportedAlgorithmException.class, () -> Providers.require(candidate));
			}
		}
	}
	
	@Test
	void requireMessageNamesTheRuntime() {
		UnsupportedAlgorithmException exception = assertThrows(UnsupportedAlgorithmException.class, () -> Providers.require(suite(AeadAlgorithm.AES_256_GCM, FAKE_KEM, SignatureAlgorithm.ED25519)));
		String message = exception.getCause().getMessage();
		
		assertTrue(message.contains("JDK " + Runtime.version().feature()));
		assertTrue(message.contains(Providers.isBouncyCastleAvailable() ? "BouncyCastle present" : "BouncyCastle absent"));
	}
	
	@Test
	void providersIsUsableWithoutTouchingBouncyCastle() throws Exception {
		assertDoesNotThrow(Providers::isBouncyCastleAvailable);
		assertDoesNotThrow(Providers::isBouncyCastleOnPath);
		assertDoesNotThrow(() -> Providers.isAvailable("MessageDigest", "SHA-256"));
		assertDoesNotThrow(() -> Providers.preferred("MessageDigest", "SHA-256"));
		assertTrue(Providers.supports(HashAlgorithm.SHA_256));
		
		Class<?> holder = Class.forName("net.luis.utils.crypto.Providers$BouncyCastle");
		assertTrue(Modifier.isPrivate(holder.getModifiers()));
		assertTrue(Modifier.isStatic(holder.getModifiers()));
		assertTrue(Modifier.isFinal(holder.getModifiers()));
		assertTrue(Arrays.stream(Providers.class.getDeclaredFields()).noneMatch(field -> field.getType() == Provider.class));
	}
	
	@Test
	void supportsHybridMatchesComponentConjunction() {
		for (KemAlgorithm algorithm : KemAlgorithm.VALUES) {
			if (algorithm instanceof HybridKemAlgorithm hybrid) {
				assertEquals(Providers.supports(hybrid.classical()) && Providers.supports(hybrid.postQuantum()), Providers.supports(algorithm), hybrid.name());
			}
		}
		for (SignatureAlgorithm algorithm : SignatureAlgorithm.VALUES) {
			if (algorithm instanceof HybridSignatureAlgorithm hybrid) {
				assertEquals(Providers.supports(hybrid.classical()) && Providers.supports(hybrid.postQuantum()), Providers.supports(algorithm), hybrid.name());
			}
		}
	}
	
	@Test
	void passwordSupportIsIndependentOfInstallation() {
		boolean argon2 = Providers.supports(PasswordAlgorithm.ARGON2ID);
		boolean scrypt = Providers.supports(PasswordAlgorithm.SCRYPT);
		
		Providers.installBouncyCastle();
		assertEquals(argon2, Providers.supports(PasswordAlgorithm.ARGON2ID));
		assertEquals(scrypt, Providers.supports(PasswordAlgorithm.SCRYPT));
		assertTrue(argon2 && scrypt);
	}
	
	@Test
	void suiteRoundTripThroughRequireAndLog() {
		assertTrue(Providers.installBouncyCastle());
		assertDoesNotThrow(() -> Providers.require(CryptoSuite.current()));
		assertDoesNotThrow(() -> Providers.logProviders(CryptoSuite.current()));
	}
	
	@FunctionalInterface
	private interface Creator {
		
		Object create() throws GeneralSecurityException;
	}
	
	private static final class CapturingAppender extends AbstractAppender {
		
		private final List<String> messages;
		
		private CapturingAppender(List<String> messages) {
			super("ProvidersTestCapture", null, null, true, Property.EMPTY_ARRAY);
			this.messages = messages;
		}
		
		@Override
		public void append(LogEvent event) {
			if (event.getLevel().isMoreSpecificThan(Level.INFO)) {
				this.messages.add(event.getMessage().getFormattedMessage());
			}
		}
	}
}
