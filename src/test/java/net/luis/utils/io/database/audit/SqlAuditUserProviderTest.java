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

package net.luis.utils.io.database.audit;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlAuditUserProvider}.<br>
 *
 * @author Luis-St
 */
class SqlAuditUserProviderTest {
	
	@Test
	void ofWithNullUser() {
		assertThrows(NullPointerException.class, () -> SqlAuditUserProvider.of(null));
	}
	
	@Test
	void emptyProviderReturnsEmpty() {
		assertTrue(SqlAuditUserProvider.empty().get().isEmpty());
	}
	
	@Test
	void ofProviderReturnsPresentUser() {
		assertEquals(Optional.of("alice"), SqlAuditUserProvider.of("alice").get());
	}
	
	@Test
	void customLambdaImplementationWorks() {
		SqlAuditUserProvider provider = () -> Optional.of("bob");
		assertEquals("bob", provider.get().orElseThrow());
	}
	
	@Test
	void ofProviderIsRepeatable() {
		SqlAuditUserProvider provider = SqlAuditUserProvider.of("alice");
		assertEquals(Optional.of("alice"), provider.get());
		assertEquals(Optional.of("alice"), provider.get());
	}
	
	@Test
	void ofWithVariousUserStrings() {
		assertEquals(Optional.of(""), SqlAuditUserProvider.of("").get());
		assertEquals(Optional.of("  "), SqlAuditUserProvider.of("  ").get());
		assertEquals(Optional.of("ünïcödé"), SqlAuditUserProvider.of("ünïcödé").get());
	}
}
