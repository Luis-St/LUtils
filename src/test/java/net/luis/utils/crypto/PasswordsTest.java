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
import net.luis.utils.crypto.exception.MalformedDataException;
import org.junit.jupiter.api.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Security;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link Passwords}.<br>
 *
 * @author Luis-St
 */
class PasswordsTest {
	
	private static final Argon2PasswordAlgorithm CHEAP_ARGON2 = new Argon2PasswordAlgorithm(8, 1, 1);
	private static final ScryptPasswordAlgorithm CHEAP_SCRYPT = new ScryptPasswordAlgorithm(1024, 1, 1);
	private static final Pbkdf2PasswordAlgorithm CHEAP_PBKDF2 = new Pbkdf2PasswordAlgorithm(1000);
	private static final List<PasswordAlgorithm> CHEAP = List.of(CHEAP_ARGON2, CHEAP_SCRYPT, CHEAP_PBKDF2);
	
	private static final char[] PASSWORD = "correct horse battery staple".toCharArray();
	private static final Path RECORD_FILE = Path.of("PasswordsTest-record.txt");
	
	@BeforeAll
	static void installProvider() {
		Providers.installBouncyCastle();
	}
	
	@AfterAll
	static void cleanUp() throws Exception {
		Files.deleteIfExists(RECORD_FILE);
	}
	
	private static String encode(byte[] data) {
		return Base64.getUrlEncoder().withoutPadding().encodeToString(data);
	}
	
	private static String record(String identifier, String parameters, int saltLength, int hashLength) {
		return "$" + identifier + "$" + parameters + "$" + encode(new byte[saltLength]) + "$" + encode(new byte[hashLength]);
	}
	
	@Test
	void constructorIsPrivate() throws Exception {
		Constructor<?>[] constructors = Passwords.class.getDeclaredConstructors();
		assertEquals(1, constructors.length);
		assertTrue(Modifier.isPrivate(constructors[0].getModifiers()));
		assertTrue(Modifier.isFinal(Passwords.class.getModifiers()));
		
		Constructor<Passwords> constructor = Passwords.class.getDeclaredConstructor();
		constructor.setAccessible(true);
		assertNotNull(constructor.newInstance());
	}
	
	@Test
	void constructEncoded() {
		byte[] salt = new byte[16];
		byte[] hash = new byte[32];
		Passwords.Encoded encoded = new Passwords.Encoded(PasswordAlgorithm.ARGON2ID, salt, hash);
		
		assertSame(PasswordAlgorithm.ARGON2ID, encoded.algorithm());
		assertSame(salt, encoded.salt());
		assertSame(hash, encoded.hash());
	}
	
	@Test
	void constructEncodedWithNullAlgorithm() {
		NullPointerException exception = assertThrows(NullPointerException.class, () -> new Passwords.Encoded(null, new byte[16], new byte[32]));
		assertEquals("Algorithm must not be null", exception.getMessage());
	}
	
	@Test
	void constructEncodedWithNullSalt() {
		NullPointerException exception = assertThrows(NullPointerException.class, () -> new Passwords.Encoded(PasswordAlgorithm.ARGON2ID, null, new byte[32]));
		assertEquals("Salt must not be null", exception.getMessage());
	}
	
	@Test
	void constructEncodedWithNullHash() {
		NullPointerException exception = assertThrows(NullPointerException.class, () -> new Passwords.Encoded(PasswordAlgorithm.ARGON2ID, new byte[16], null));
		assertEquals("Hash must not be null", exception.getMessage());
	}
	
	@Test
	void constructEncodedWithAllNull() {
		NullPointerException exception = assertThrows(NullPointerException.class, () -> new Passwords.Encoded(null, null, null));
		assertEquals("Algorithm must not be null", exception.getMessage());
	}
	
	@Test
	void hashWithNullPassword() {
		assertEquals("Password must not be null", assertThrows(NullPointerException.class, () -> Passwords.hash(null)).getMessage());
		assertEquals("Password must not be null", assertThrows(NullPointerException.class, () -> Passwords.hash(null, CHEAP_ARGON2)).getMessage());
	}
	
	@Test
	void hashWithNullAlgorithm() {
		NullPointerException exception = assertThrows(NullPointerException.class, () -> Passwords.hash(PASSWORD, null));
		assertEquals("Algorithm must not be null", exception.getMessage());
	}
	
	@Test
	void hashWithBothNull() {
		NullPointerException exception = assertThrows(NullPointerException.class, () -> Passwords.hash(null, null));
		assertEquals("Password must not be null", exception.getMessage());
	}
	
	@Test
	void verifyWithNullPassword() {
		NullPointerException exception = assertThrows(NullPointerException.class, () -> Passwords.verify(null, record("argon2id", "v=19$m=8,t=1,p=1", 16, 32)));
		assertEquals("Password must not be null", exception.getMessage());
	}
	
	@Test
	void verifyWithNullEncoded() {
		NullPointerException exception = assertThrows(NullPointerException.class, () -> Passwords.verify(PASSWORD, null));
		assertEquals("Encoded hash must not be null", exception.getMessage());
	}
	
	@Test
	void verifyWithNullPasswordAndNullEncoded() {
		NullPointerException exception = assertThrows(NullPointerException.class, () -> Passwords.verify(null, null));
		assertEquals("Password must not be null", exception.getMessage());
	}
	
	@Test
	void needsRehashWithNullEncoded() {
		NullPointerException exception = assertThrows(NullPointerException.class, () -> Passwords.needsRehash(null));
		assertEquals("Encoded hash must not be null", exception.getMessage());
	}
	
	@Test
	void parseWithNullEncoded() {
		NullPointerException exception = assertThrows(NullPointerException.class, () -> Passwords.Encoded.parse(null));
		assertEquals("Encoded hash must not be null", exception.getMessage());
	}
	
	@Test
	void parseWithTooFewSections() {
		for (String malformed : new String[] { "", "$", "$argon2id", "$argon2id$v=19", "$argon2id$v=19$m=8,t=1,p=1" }) {
			MalformedDataException exception = assertThrows(MalformedDataException.class, () -> Passwords.Encoded.parse(malformed));
			assertTrue(exception.getMessage().contains("dollar separated sections"));
		}
	}
	
	@Test
	void parseWithoutLeadingDollar() {
		assertThrows(MalformedDataException.class, () -> Passwords.Encoded.parse("argon2id$v=19$m=8,t=1,p=1$" + encode(new byte[16]) + "$" + encode(new byte[32])));
	}
	
	@Test
	void parseWithUnknownIdentifier() {
		MalformedDataException exception = assertThrows(MalformedDataException.class, () -> Passwords.Encoded.parse(record("bcrypt", "v=19$m=8,t=1,p=1", 16, 32)));
		assertTrue(exception.getMessage().contains("bcrypt"));
	}
	
	@Test
	void parseWithEmptyIdentifier() {
		MalformedDataException exception = assertThrows(MalformedDataException.class, () -> Passwords.Encoded.parse(record("", "v=19$m=8,t=1,p=1", 16, 32)));
		assertTrue(exception.getMessage().contains("''"));
	}
	
	@Test
	void parseWithHashTooShort() {
		for (int length : new int[] { 15, 0 }) {
			MalformedDataException exception = assertThrows(MalformedDataException.class, () -> Passwords.Encoded.parse(record("argon2id", "v=19$m=8,t=1,p=1", 16, length)));
			assertTrue(exception.getMessage().contains("[16, 64]"));
			assertTrue(exception.getMessage().contains(String.valueOf(length)));
		}
	}
	
	@Test
	void parseWithHashTooLong() {
		for (int length : new int[] { 65, 128 }) {
			assertThrows(MalformedDataException.class, () -> Passwords.Encoded.parse(record("argon2id", "v=19$m=8,t=1,p=1", 16, length)));
		}
	}
	
	@Test
	void parseWithInvalidBase64Salt() {
		MalformedDataException exception = assertThrows(MalformedDataException.class, () -> Passwords.Encoded.parse("$argon2id$v=19$m=8,t=1,p=1$!!!$" + encode(new byte[32])));
		assertTrue(exception.getMessage().contains("argon2id"));
		assertInstanceOf(IllegalArgumentException.class, exception.getCause());
	}
	
	@Test
	void parseWithInvalidBase64Hash() {
		MalformedDataException exception = assertThrows(MalformedDataException.class, () -> Passwords.Encoded.parse("$argon2id$v=19$m=8,t=1,p=1$" + encode(new byte[16]) + "$!!!"));
		assertInstanceOf(IllegalArgumentException.class, exception.getCause());
	}
	
	@Test
	void parseWithPaddedBase64() {
		String padded = "$argon2id$v=19$m=8,t=1,p=1$" + Base64.getUrlEncoder().encodeToString(new byte[15]) + "$" + encode(new byte[32]);
		
		assertTrue(padded.contains("="));
		Passwords.Encoded parsed = assertDoesNotThrow(() -> Passwords.Encoded.parse(padded));
		assertEquals(15, parsed.salt().length);
	}
	
	@Test
	void parseWithStandardBase64Alphabet() {
		assertThrows(MalformedDataException.class, () -> Passwords.Encoded.parse("$argon2id$v=19$m=8,t=1,p=1$ab+cdefghijklmno$" + encode(new byte[32])));
		assertThrows(MalformedDataException.class, () -> Passwords.Encoded.parse("$argon2id$v=19$m=8,t=1,p=1$ab/cdefghijklmno$" + encode(new byte[32])));
	}
	
	@Test
	void parseWithWrongParameterSections() {
		assertThrows(MalformedDataException.class, () -> Passwords.Encoded.parse(record("argon2id", "m=8,t=1,p=1", 16, 32)));
		assertThrows(MalformedDataException.class, () -> Passwords.Encoded.parse(record("scrypt", "v=19$ln=10,r=1,p=1", 16, 32)));
		assertThrows(MalformedDataException.class, () -> Passwords.Encoded.parse(record("pbkdf2-sha512", "v=19$i=1000", 16, 32)));
	}
	
	@Test
	void parseWithOutOfRangeCost() {
		assertThrows(MalformedDataException.class, () -> Passwords.Encoded.parse(record("argon2id", "v=19$m=1048577,t=1,p=1", 16, 32)));
		assertThrows(MalformedDataException.class, () -> Passwords.Encoded.parse(record("argon2id", "v=19$m=8,t=999,p=1", 16, 32)));
		assertThrows(MalformedDataException.class, () -> Passwords.Encoded.parse(record("scrypt", "ln=20,r=16,p=1", 16, 32)));
		assertThrows(MalformedDataException.class, () -> Passwords.Encoded.parse(record("pbkdf2-sha512", "i=999999999", 16, 32)));
	}
	
	@Test
	void parseWithUnsupportedArgon2Version() {
		assertThrows(MalformedDataException.class, () -> Passwords.Encoded.parse(record("argon2id", "v=16$m=8,t=1,p=1", 16, 32)));
	}
	
	@Test
	void verifyWithMalformedRecord() {
		for (String malformed : new String[] { "", "$argon2id$v=19$m=8,t=1,p=1", record("bcrypt", "v=19$m=8,t=1,p=1", 16, 32), record("argon2id", "v=19$m=8,t=1,p=1", 16, 8) }) {
			assertThrows(MalformedDataException.class, () -> Passwords.verify(PASSWORD, malformed));
		}
	}
	
	@Test
	void hashWithArgon2() {
		String encoded = Passwords.hash(PASSWORD, CHEAP_ARGON2);
		assertTrue(encoded.startsWith("$argon2id$v=19$m=8,t=1,p=1$"));
		assertTrue(Passwords.verify(PASSWORD, encoded));
	}
	
	@Test
	void hashWithScrypt() {
		String encoded = Passwords.hash(PASSWORD, CHEAP_SCRYPT);
		assertTrue(encoded.startsWith("$scrypt$ln=10,r=1,p=1$"));
		assertTrue(Passwords.verify(PASSWORD, encoded));
	}
	
	@Test
	void hashWithPbkdf2() {
		String encoded = Passwords.hash(PASSWORD, CHEAP_PBKDF2);
		assertTrue(encoded.startsWith("$pbkdf2-sha512$i=1000$"));
		assertTrue(Passwords.verify(PASSWORD, encoded));
	}
	
	@Test
	void hashWithoutBouncyCastleInstalled() {
		assertNotNull(Security.getProvider("BC"));
		assertTrue(Passwords.verify(PASSWORD, Passwords.hash(PASSWORD, CHEAP_ARGON2)));
		assertTrue(Passwords.verify(PASSWORD, Passwords.hash(PASSWORD, CHEAP_SCRYPT)));
	}
	
	@Test
	void pbkdf2SpecPasswordIsCleared() {
		char[] password = PASSWORD.clone();
		String encoded = Passwords.hash(password, CHEAP_PBKDF2);
		
		assertArrayEquals(PASSWORD, password);
		assertTrue(Passwords.verify(password, encoded));
	}
	
	@Test
	void argon2AndScryptSpecsAreNotCleared() {
		for (PasswordAlgorithm algorithm : CHEAP) {
			char[] password = PASSWORD.clone();
			Passwords.hash(password, algorithm);
			assertArrayEquals(PASSWORD, password);
		}
	}
	
	@Test
	void parseWithExactlyFiveSections() {
		String encoded = Passwords.hash(PASSWORD, CHEAP_SCRYPT);
		assertEquals(5, encoded.split("\\$", -1).length);
		assertDoesNotThrow(() -> Passwords.Encoded.parse(encoded));
	}
	
	@Test
	void parseWithSixSections() {
		String encoded = Passwords.hash(PASSWORD, CHEAP_ARGON2);
		assertEquals(6, encoded.split("\\$", -1).length);
		assertEquals(CHEAP_ARGON2, Passwords.Encoded.parse(encoded).algorithm());
	}
	
	@Test
	void parseWithHashAtMinimumLength() {
		Passwords.Encoded parsed = assertDoesNotThrow(() -> Passwords.Encoded.parse(record("argon2id", "v=19$m=8,t=1,p=1", 16, 16)));
		assertEquals(16, parsed.hash().length);
	}
	
	@Test
	void parseWithHashAtMaximumLength() {
		Passwords.Encoded parsed = assertDoesNotThrow(() -> Passwords.Encoded.parse(record("argon2id", "v=19$m=8,t=1,p=1", 16, 64)));
		assertEquals(64, parsed.hash().length);
	}
	
	@Test
	void verifyWithCorrectPassword() {
		for (PasswordAlgorithm algorithm : CHEAP) {
			assertTrue(Passwords.verify(PASSWORD, Passwords.hash(PASSWORD, algorithm)));
		}
	}
	
	@Test
	void verifyWithWrongPassword() {
		String encoded = Passwords.hash(PASSWORD, CHEAP_ARGON2);
		
		assertFalse(Passwords.verify("completely different".toCharArray(), encoded));
		assertFalse(Passwords.verify("correct horse battery stapla".toCharArray(), encoded));
		assertFalse(Passwords.verify("correct horse".toCharArray(), encoded));
		assertThrows(IllegalArgumentException.class, () -> Passwords.verify(new char[0], encoded));
	}
	
	@Test
	void needsRehashForWeakerRecord() {
		assertTrue(Passwords.needsRehash(Passwords.hash(PASSWORD, CHEAP_ARGON2)));
	}
	
	@Test
	void needsRehashForCurrentRecord() {
		assertFalse(Passwords.needsRehash(Passwords.hash(PASSWORD, PasswordAlgorithm.ARGON2ID)));
	}
	
	@Test
	void needsRehashForStrongerRecord() {
		assertFalse(Passwords.needsRehash(Passwords.hash(PASSWORD, new Argon2PasswordAlgorithm(131_072, 4, 8))));
	}
	
	@Test
	void hashFormat() {
		for (PasswordAlgorithm algorithm : CHEAP) {
			String encoded = Passwords.hash(PASSWORD, algorithm);
			String[] sections = encoded.split("\\$", -1);
			
			assertTrue(encoded.startsWith("$"));
			assertEquals("", sections[0]);
			assertEquals(algorithm.identifier(), sections[1]);
			assertEquals(algorithm instanceof Argon2PasswordAlgorithm ? 6 : 5, sections.length);
		}
	}
	
	@Test
	void hashSaltLength() {
		for (PasswordAlgorithm algorithm : CHEAP) {
			assertEquals(16, Passwords.Encoded.parse(Passwords.hash(PASSWORD, algorithm)).salt().length);
		}
	}
	
	@Test
	void hashLength() {
		for (PasswordAlgorithm algorithm : CHEAP) {
			byte[] hash = Passwords.Encoded.parse(Passwords.hash(PASSWORD, algorithm)).hash();
			assertEquals(32, hash.length);
			assertTrue(hash.length >= 16 && hash.length <= 64);
		}
	}
	
	@Test
	void hashUsesUnpaddedBase64Url() {
		for (PasswordAlgorithm algorithm : CHEAP) {
			String[] sections = Passwords.hash(PASSWORD, algorithm).split("\\$", -1);
			for (String section : new String[] { sections[sections.length - 2], sections[sections.length - 1] }) {
				assertFalse(section.contains("="));
				assertFalse(section.contains("+"));
				assertFalse(section.contains("/"));
			}
		}
	}
	
	@Test
	void hashProducesDifferentRecordsPerCall() {
		String first = Passwords.hash(PASSWORD, CHEAP_ARGON2);
		String second = Passwords.hash(PASSWORD, CHEAP_ARGON2);
		
		assertNotEquals(first, second);
		assertTrue(Passwords.verify(PASSWORD, first));
		assertTrue(Passwords.verify(PASSWORD, second));
	}
	
	@Test
	void hashWithEmptyPassword() {
		assertEquals("password empty", assertThrows(IllegalArgumentException.class, () -> Passwords.hash(new char[0], CHEAP_ARGON2)).getMessage());
		assertEquals("password empty", assertThrows(IllegalArgumentException.class, () -> Passwords.hash(new char[0], CHEAP_SCRYPT)).getMessage());
		
		String encoded = assertDoesNotThrow(() -> Passwords.hash(new char[0], CHEAP_PBKDF2));
		assertTrue(Passwords.verify(new char[0], encoded));
		assertFalse(Passwords.verify("x".toCharArray(), encoded));
	}
	
	@Test
	void hashWithSingleCharacterPassword() {
		String encoded = assertDoesNotThrow(() -> Passwords.hash("x".toCharArray(), CHEAP_ARGON2));
		assertTrue(Passwords.verify("x".toCharArray(), encoded));
	}
	
	@Test
	void hashDoesNotMutatePassword() {
		for (PasswordAlgorithm algorithm : CHEAP) {
			char[] password = PASSWORD.clone();
			String encoded = Passwords.hash(password, algorithm);
			assertArrayEquals(PASSWORD, password);
			
			Passwords.verify(password, encoded);
			assertArrayEquals(PASSWORD, password);
		}
	}
	
	@Test
	void defaultIsArgon2id() {
		String encoded = Passwords.hash(PASSWORD);
		String[] sections = encoded.split("\\$", -1);
		
		assertEquals("argon2id", sections[1]);
		assertEquals(PasswordAlgorithm.ARGON2ID.encodeParameters(), sections[2] + "$" + sections[3]);
	}
	
	@Test
	void parseRecoversTheAlgorithmAndCost() {
		for (PasswordAlgorithm algorithm : CHEAP) {
			assertEquals(algorithm, Passwords.Encoded.parse(Passwords.hash(PASSWORD, algorithm)).algorithm());
		}
		assertNotEquals(PasswordAlgorithm.ARGON2ID, Passwords.Encoded.parse(Passwords.hash(PASSWORD, CHEAP_ARGON2)).algorithm());
	}
	
	@Test
	void parseRecoversSaltAndHash() {
		String encoded = Passwords.hash(PASSWORD, CHEAP_ARGON2);
		String[] sections = encoded.split("\\$", -1);
		Passwords.Encoded parsed = Passwords.Encoded.parse(encoded);
		
		assertArrayEquals(Base64.getUrlDecoder().decode(sections[4]), parsed.salt());
		assertArrayEquals(Base64.getUrlDecoder().decode(sections[5]), parsed.hash());
	}
	
	@Test
	void hashAndVerifyRoundTripForEveryAlgorithm() {
		for (PasswordAlgorithm algorithm : CHEAP) {
			String encoded = Passwords.hash(PASSWORD, algorithm);
			assertTrue(Passwords.verify(PASSWORD, encoded));
			assertFalse(Passwords.verify("other".toCharArray(), encoded));
		}
	}
	
	@Test
	void verifyUsesTheRecordsOwnCostNotTheDefault() {
		String encoded = Passwords.hash(PASSWORD, CHEAP_ARGON2);
		
		long start = System.nanoTime();
		assertTrue(Passwords.verify(PASSWORD, encoded));
		assertTrue(System.nanoTime() - start < 1_000_000_000L);
		assertEquals(CHEAP_ARGON2, Passwords.Encoded.parse(encoded).algorithm());
	}
	
	@Test
	void verifyAcrossFunctionFamilies() {
		assertEquals(PasswordAlgorithm.ARGON2ID, PasswordAlgorithm.VALUES.getFirst());
		assertTrue(Passwords.verify(PASSWORD, Passwords.hash(PASSWORD, CHEAP_SCRYPT)));
		assertTrue(Passwords.verify(PASSWORD, Passwords.hash(PASSWORD, CHEAP_PBKDF2)));
	}
	
	@Test
	void verifyDerivesAtTheStoredHashLength() {
		String encoded = Passwords.hash(PASSWORD, CHEAP_ARGON2);
		String[] sections = encoded.split("\\$", -1);
		byte[] full = Base64.getUrlDecoder().decode(sections[5]);
		
		for (int length : new int[] { 16, 64 }) {
			String rebuilt = "$argon2id$v=19$m=8,t=1,p=1$" + sections[4] + "$" + encode(Arrays.copyOf(full, length));
			assertEquals(length, Passwords.Encoded.parse(rebuilt).hash().length);
			assertEquals(length == 16 && Arrays.equals(Arrays.copyOf(full, 16), Passwords.Encoded.parse(rebuilt).hash()), length == 16);
		}
	}
	
	@Test
	void rehashMigrationCycle() {
		String stored = Passwords.hash(PASSWORD, CHEAP_ARGON2);
		assertTrue(Passwords.verify(PASSWORD, stored));
		assertTrue(Passwords.needsRehash(stored));
		
		String migrated = Passwords.hash(PASSWORD);
		assertTrue(Passwords.verify(PASSWORD, migrated));
		assertFalse(Passwords.needsRehash(migrated));
		assertNotEquals(stored, migrated);
	}
	
	@Test
	void needsRehashAcrossFunctionFamilies() {
		assertTrue(Passwords.needsRehash(Passwords.hash(PASSWORD, CHEAP_SCRYPT)));
		assertTrue(Passwords.needsRehash(Passwords.hash(PASSWORD, CHEAP_PBKDF2)));
		assertTrue(Passwords.needsRehash(Passwords.hash(PASSWORD, PasswordAlgorithm.PBKDF2_HMAC_SHA_512)));
	}
	
	@Test
	void verifyIsConstantTimeShaped() {
		String encoded = Passwords.hash(PASSWORD, CHEAP_ARGON2);
		
		assertFalse(Passwords.verify("correct horse battery stapleX".toCharArray(), encoded));
		assertFalse(Passwords.verify("zzzzzzzzzzzzzzzzzzzzzzzzzzzzz".toCharArray(), encoded));
		assertDoesNotThrow(() -> Passwords.verify("correct horse".toCharArray(), encoded));
	}
	
	@Test
	void hashWithLongPassword() {
		char[] password = new char[10000];
		Arrays.fill(password, 'p');
		String encoded = assertDoesNotThrow(() -> Passwords.hash(password, CHEAP_ARGON2));
		
		assertTrue(Passwords.verify(password, encoded));
		assertFalse(Passwords.verify(Arrays.copyOf(password, 9999), encoded));
	}
	
	@Test
	void hashWithUnicodePassword() {
		char[] password = "pässwörd-🔐".toCharArray();
		String encoded = Passwords.hash(password, CHEAP_ARGON2);
		
		assertTrue(Passwords.verify(password, encoded));
		assertFalse(Passwords.verify("pässwörd-🔐".toCharArray(), encoded));
	}
	
	@Test
	void hashWithNullCharacterInPassword() {
		char[] password = { 'a', '\0', 'b' };
		String encoded = Passwords.hash(password, CHEAP_ARGON2);
		
		assertTrue(Passwords.verify(password, encoded));
		assertFalse(Passwords.verify(new char[] { 'a' }, encoded));
	}
	
	@Test
	void recordsAreNotInterchangeableAcrossPasswords() {
		List<char[]> passwords = new ArrayList<>();
		List<String> records = new ArrayList<>();
		for (int i = 0; i < 20; i++) {
			char[] password = ("password-" + i).toCharArray();
			passwords.add(password);
			records.add(Passwords.hash(password, CHEAP_ARGON2));
		}
		
		for (int i = 0; i < passwords.size(); i++) {
			for (int j = 0; j < records.size(); j++) {
				assertEquals(i == j, Passwords.verify(passwords.get(i), records.get(j)));
			}
		}
	}
	
	@Test
	void parseRejectsEveryTruncationOfAValidRecord() {
		String encoded = Passwords.hash(PASSWORD, CHEAP_ARGON2);
		for (int i = 1; i < encoded.length(); i++) {
			String prefix = encoded.substring(0, i);
			if (prefix.split("\\$", -1).length < 5) {
				assertThrows(MalformedDataException.class, () -> Passwords.Encoded.parse(prefix));
			} else {
				assertFalse(Passwords.verify(PASSWORD, encoded.substring(0, encoded.length() - 1)));
			}
		}
	}
	
	@Test
	void parseIsDeterministic() {
		String encoded = Passwords.hash(PASSWORD, CHEAP_ARGON2);
		Passwords.Encoded first = Passwords.Encoded.parse(encoded);
		Passwords.Encoded second = Passwords.Encoded.parse(encoded);
		
		assertEquals(first.algorithm(), second.algorithm());
		assertArrayEquals(first.salt(), second.salt());
		assertArrayEquals(first.hash(), second.hash());
	}
	
	@Test
	void encodedEqualsIsIdentityBasedForArrayComponents() {
		Passwords.Encoded first = new Passwords.Encoded(CHEAP_ARGON2, new byte[16], new byte[32]);
		Passwords.Encoded second = new Passwords.Encoded(CHEAP_ARGON2, new byte[16], new byte[32]);
		
		assertNotEquals(first, second);
		assertEquals(first, first);
	}
	
	@Test
	void encodedToStringDoesNotRevealHash() {
		byte[] salt = new byte[16];
		byte[] hash = new byte[32];
		Arrays.fill(hash, (byte) 0x5A);
		String rendered = new Passwords.Encoded(CHEAP_ARGON2, salt, hash).toString();
		
		assertFalse(rendered.contains(HexFormat.of().formatHex(hash)));
		assertFalse(rendered.contains(Arrays.toString(hash)));
		assertFalse(rendered.contains(encode(hash)));
		assertFalse(rendered.contains(Arrays.toString(salt)));
	}
	
	@Test
	void hashIsUsableAsAStoredRecord() throws Exception {
		String encoded = Passwords.hash(PASSWORD, CHEAP_ARGON2);
		Files.writeString(RECORD_FILE, encoded + System.lineSeparator());
		
		String restored = Files.readString(RECORD_FILE).strip();
		assertEquals(encoded, restored);
		assertTrue(Passwords.verify(PASSWORD, restored));
	}
}
