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

package net.luis.utils.io.database.exception;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlStateClass}.<br>
 *
 * @author Luis-St
 */
class SqlStateClassTest {

	@Test
	void fromStateWithKnownPrefixes() {
		assertEquals(Optional.of(SqlStateClass.CONNECTION), SqlStateClass.fromState("08006"));
		assertEquals(Optional.of(SqlStateClass.DATA), SqlStateClass.fromState("22003"));
		assertEquals(Optional.of(SqlStateClass.INTEGRITY_CONSTRAINT), SqlStateClass.fromState("23505"));
		assertEquals(Optional.of(SqlStateClass.AUTHORIZATION), SqlStateClass.fromState("28000"));
		assertEquals(Optional.of(SqlStateClass.TRANSACTION_ROLLBACK), SqlStateClass.fromState("40001"));
		assertEquals(Optional.of(SqlStateClass.SYNTAX_OR_ACCESS), SqlStateClass.fromState("42601"));
	}

	@Test
	void fromStateUsesOnlyLeadingTwoCharacters() {
		assertEquals(Optional.of(SqlStateClass.INTEGRITY_CONSTRAINT), SqlStateClass.fromState("23"));
		assertEquals(Optional.of(SqlStateClass.SYNTAX_OR_ACCESS), SqlStateClass.fromState("42P01"));
	}

	@Test
	void fromStateIsCaseInsensitive() {
		assertEquals(Optional.of(SqlStateClass.FEATURE_NOT_SUPPORTED), SqlStateClass.fromState("0a000"));
	}

	@Test
	void fromStateWithNullOrMalformed() {
		assertEquals(Optional.empty(), SqlStateClass.fromState(null));
		assertEquals(Optional.empty(), SqlStateClass.fromState(""));
		assertEquals(Optional.empty(), SqlStateClass.fromState("0"));
		assertEquals(Optional.empty(), SqlStateClass.fromState("99999"));
	}

	@Test
	void getCode() {
		assertEquals("08", SqlStateClass.CONNECTION.getCode());
		assertEquals("23", SqlStateClass.INTEGRITY_CONSTRAINT.getCode());
		assertEquals("42", SqlStateClass.SYNTAX_OR_ACCESS.getCode());
	}
}
