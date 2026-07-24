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

package net.luis.utils.grammar.parser.rule.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link LookMatchMode}.<br>
 *
 * @author Luis-St
 */
class LookMatchModeTest {
	
	@Test
	void valueOfWithUnknownNameThrows() {
		assertThrows(IllegalArgumentException.class, () -> LookMatchMode.valueOf("UNKNOWN"));
	}
	
	@Test
	void valueOfWithNullNameThrows() {
		assertThrows(NullPointerException.class, () -> LookMatchMode.valueOf(null));
	}
	
	@Test
	void positiveShouldMatchTrueWhenRuleMatches() {
		assertTrue(LookMatchMode.POSITIVE.shouldMatch(true));
	}
	
	@Test
	void positiveShouldMatchFalseWhenRuleDoesNotMatch() {
		assertFalse(LookMatchMode.POSITIVE.shouldMatch(false));
	}
	
	@Test
	void negativeShouldMatchTrueWhenRuleDoesNotMatch() {
		assertTrue(LookMatchMode.NEGATIVE.shouldMatch(false));
	}
	
	@Test
	void negativeShouldMatchFalseWhenRuleMatches() {
		assertFalse(LookMatchMode.NEGATIVE.shouldMatch(true));
	}
	
	@Test
	void valuesContainsBothConstantsInDeclarationOrder() {
		LookMatchMode[] values = LookMatchMode.values();
		assertEquals(2, values.length);
		assertEquals(LookMatchMode.POSITIVE, values[0]);
		assertEquals(LookMatchMode.NEGATIVE, values[1]);
	}
	
	@Test
	void valueOfPositiveReturnsPositiveConstant() {
		assertEquals(LookMatchMode.POSITIVE, LookMatchMode.valueOf("POSITIVE"));
	}
	
	@Test
	void valueOfNegativeReturnsNegativeConstant() {
		assertEquals(LookMatchMode.NEGATIVE, LookMatchMode.valueOf("NEGATIVE"));
	}
	
	@Test
	void ordinalsReflectDeclarationOrder() {
		assertEquals(0, LookMatchMode.POSITIVE.ordinal());
		assertEquals(1, LookMatchMode.NEGATIVE.ordinal());
	}
	
	@Test
	void nameReturnsDeclaredConstantName() {
		assertEquals("POSITIVE", LookMatchMode.POSITIVE.name());
		assertEquals("NEGATIVE", LookMatchMode.NEGATIVE.name());
	}
}
