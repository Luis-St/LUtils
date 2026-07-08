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

package net.luis.utils.io.network.mail;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SmtpAuth}.<br>
 *
 * @author Luis-St
 */
class SmtpAuthTest {
	
	@Test
	void constructNone() {
		SmtpAuth.None none = new SmtpAuth.None();
		
		assertNotNull(none);
		assertInstanceOf(SmtpAuth.class, none);
	}
	
	@Test
	void constructPlain() {
		SmtpAuth.Plain plain = new SmtpAuth.Plain("user", "pw".toCharArray());
		
		assertNotNull(plain);
		assertEquals("user", plain.username());
		assertArrayEquals(new char[] { 'p', 'w' }, plain.password());
	}
	
	@Test
	void constructLogin() {
		SmtpAuth.Login login = new SmtpAuth.Login("user", "pw".toCharArray());
		
		assertNotNull(login);
		assertEquals("user", login.username());
		assertArrayEquals(new char[] { 'p', 'w' }, login.password());
	}
	
	@Test
	void constructOAuth() {
		SmtpAuth.OAuth oauth = new SmtpAuth.OAuth("user", "tok".toCharArray());
		
		assertNotNull(oauth);
		assertEquals("user", oauth.username());
		assertArrayEquals(new char[] { 't', 'o', 'k' }, oauth.token());
	}
	
	@Test
	void constructPlainWithNullUsername() {
		assertThrows(NullPointerException.class, () -> new SmtpAuth.Plain(null, "pw".toCharArray()));
	}
	
	@Test
	void constructPlainWithNullPassword() {
		assertThrows(NullPointerException.class, () -> new SmtpAuth.Plain("user", null));
	}
	
	@Test
	void constructLoginWithNullUsername() {
		assertThrows(NullPointerException.class, () -> new SmtpAuth.Login(null, "pw".toCharArray()));
	}
	
	@Test
	void constructLoginWithNullPassword() {
		assertThrows(NullPointerException.class, () -> new SmtpAuth.Login("user", null));
	}
	
	@Test
	void constructOAuthWithNullUsername() {
		assertThrows(NullPointerException.class, () -> new SmtpAuth.OAuth(null, "tok".toCharArray()));
	}
	
	@Test
	void constructOAuthWithNullToken() {
		assertThrows(NullPointerException.class, () -> new SmtpAuth.OAuth("user", null));
	}
	
	@Test
	void constructPlainWithEmptyUsername() {
		assertThrows(IllegalArgumentException.class, () -> new SmtpAuth.Plain("", "pw".toCharArray()));
	}
	
	@Test
	void constructPlainWithEmptyPassword() {
		assertThrows(IllegalArgumentException.class, () -> new SmtpAuth.Plain("user", new char[0]));
	}
	
	@Test
	void constructLoginWithEmptyUsername() {
		assertThrows(IllegalArgumentException.class, () -> new SmtpAuth.Login("", "pw".toCharArray()));
	}
	
	@Test
	void constructLoginWithEmptyPassword() {
		assertThrows(IllegalArgumentException.class, () -> new SmtpAuth.Login("user", new char[0]));
	}
	
	@Test
	void constructOAuthWithEmptyUsername() {
		assertThrows(IllegalArgumentException.class, () -> new SmtpAuth.OAuth("", "tok".toCharArray()));
	}
	
	@Test
	void constructOAuthWithEmptyToken() {
		assertThrows(IllegalArgumentException.class, () -> new SmtpAuth.OAuth("user", new char[0]));
	}
	
	@Test
	void mechanismNone() {
		assertEquals("", new SmtpAuth.None().mechanism());
	}
	
	@Test
	void mechanismPlain() {
		assertEquals("PLAIN", new SmtpAuth.Plain("user", "pw".toCharArray()).mechanism());
	}
	
	@Test
	void mechanismLogin() {
		assertEquals("LOGIN", new SmtpAuth.Login("user", "pw".toCharArray()).mechanism());
	}
	
	@Test
	void mechanismOAuth() {
		assertEquals("XOAUTH2", new SmtpAuth.OAuth("user", "tok".toCharArray()).mechanism());
	}
	
	@Test
	void constructPlainWithSingleCharValues() {
		SmtpAuth.Plain plain = new SmtpAuth.Plain("u", new char[] { 'p' });
		
		assertEquals("u", plain.username());
		assertArrayEquals(new char[] { 'p' }, plain.password());
	}
	
	@Test
	void toStringNoneDefault() {
		assertEquals("None[]", new SmtpAuth.None().toString());
	}
	
	@Test
	void toStringPlainRedactsPassword() {
		String string = new SmtpAuth.Plain("user", "secret".toCharArray()).toString();
		
		assertEquals("Plain[username=user, password=***]", string);
		assertFalse(string.contains("secret"));
	}
	
	@Test
	void toStringLoginRedactsPassword() {
		String string = new SmtpAuth.Login("user", "secret".toCharArray()).toString();
		
		assertEquals("Login[username=user, password=***]", string);
		assertFalse(string.contains("secret"));
	}
	
	@Test
	void toStringOAuthRedactsToken() {
		String string = new SmtpAuth.OAuth("user", "bearer-token".toCharArray()).toString();
		
		assertEquals("OAuth[username=user, token=***]", string);
		assertFalse(string.contains("bearer-token"));
	}
	
	@Test
	void plainAccessorsReturnStoredReference() {
		char[] pw = "pw".toCharArray();
		SmtpAuth.Plain plain = new SmtpAuth.Plain("user", pw);
		
		assertSame(pw, plain.password());
		assertEquals("user", plain.username());
	}
	
	@Test
	void oauthTokenAccessorReturnsStoredReference() {
		char[] tok = "tok".toCharArray();
		SmtpAuth.OAuth oauth = new SmtpAuth.OAuth("user", tok);
		
		assertSame(tok, oauth.token());
	}
	
	@Test
	void mechanismPolymorphicViaInterface() {
		List<SmtpAuth> auths = List.of(
			new SmtpAuth.None(),
			new SmtpAuth.Plain("user", "pw".toCharArray()),
			new SmtpAuth.Login("user", "pw".toCharArray()),
			new SmtpAuth.OAuth("user", "tok".toCharArray())
		);
		
		assertEquals("", auths.get(0).mechanism());
		assertEquals("PLAIN", auths.get(1).mechanism());
		assertEquals("LOGIN", auths.get(2).mechanism());
		assertEquals("XOAUTH2", auths.get(3).mechanism());
	}
	
	@Test
	void plainEqualsUsesArrayIdentityForPassword() {
		SmtpAuth.Plain a = new SmtpAuth.Plain("user", "pw".toCharArray());
		SmtpAuth.Plain b = new SmtpAuth.Plain("user", "pw".toCharArray());
		
		assertNotEquals(a, b);
	}
	
	@Test
	void plainEqualsWithSharedArrayAndSameUsername() {
		char[] pw = "pw".toCharArray();
		SmtpAuth.Plain a = new SmtpAuth.Plain("user", pw);
		SmtpAuth.Plain b = new SmtpAuth.Plain("user", pw);
		
		assertEquals(a, b);
		assertEquals(a.hashCode(), b.hashCode());
	}
	
	@Test
	void noneInstancesAreEqual() {
		SmtpAuth.None a = new SmtpAuth.None();
		SmtpAuth.None b = new SmtpAuth.None();
		
		assertEquals(a, b);
		assertEquals(a.hashCode(), b.hashCode());
	}
	
	@Test
	void secretMutationVisibleThroughAccessor() {
		char[] pw = "pw".toCharArray();
		SmtpAuth.Plain plain = new SmtpAuth.Plain("user", pw);
		
		pw[0] = 'X';
		assertEquals('X', plain.password()[0]);
		assertSame(pw, plain.password());
	}
	
	@Test
	void loginEqualsAndHashCodeUseArrayIdentityForPassword() {
		SmtpAuth.Login a = new SmtpAuth.Login("user", "pw".toCharArray());
		SmtpAuth.Login b = new SmtpAuth.Login("user", "pw".toCharArray());
		assertNotEquals(a, b);
		
		char[] pw = "pw".toCharArray();
		SmtpAuth.Login c = new SmtpAuth.Login("user", pw);
		SmtpAuth.Login d = new SmtpAuth.Login("user", pw);
		assertEquals(c, d);
		assertEquals(c.hashCode(), d.hashCode());
	}
	
	@Test
	void oauthEqualsAndHashCodeUseArrayIdentityForToken() {
		SmtpAuth.OAuth a = new SmtpAuth.OAuth("user", "tok".toCharArray());
		SmtpAuth.OAuth b = new SmtpAuth.OAuth("user", "tok".toCharArray());
		assertNotEquals(a, b);
		
		char[] tok = "tok".toCharArray();
		SmtpAuth.OAuth c = new SmtpAuth.OAuth("user", tok);
		SmtpAuth.OAuth d = new SmtpAuth.OAuth("user", tok);
		assertEquals(c, d);
		assertEquals(c.hashCode(), d.hashCode());
	}
}
