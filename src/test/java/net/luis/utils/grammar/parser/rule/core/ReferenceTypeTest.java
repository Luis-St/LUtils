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
 * Test class for {@link ReferenceType}.<br>
 *
 * @author Luis-St
 */
class ReferenceTypeTest {
	
	@Test
	void valueOfWithUnknownNameThrows() {
		assertThrows(IllegalArgumentException.class, () -> ReferenceType.valueOf("UNKNOWN"));
	}
	
	@Test
	void valueOfWithNullNameThrows() {
		assertThrows(NullPointerException.class, () -> ReferenceType.valueOf(null));
	}
	
	@Test
	void valuesContainsAllConstantsInDeclarationOrder() {
		ReferenceType[] values = ReferenceType.values();
		assertEquals(3, values.length);
		assertEquals(ReferenceType.RULE, values[0]);
		assertEquals(ReferenceType.TOKENS, values[1]);
		assertEquals(ReferenceType.DYNAMIC, values[2]);
	}
	
	@Test
	void valueOfRuleReturnsRuleConstant() {
		assertEquals(ReferenceType.RULE, ReferenceType.valueOf("RULE"));
	}
	
	@Test
	void valueOfTokensReturnsTokensConstant() {
		assertEquals(ReferenceType.TOKENS, ReferenceType.valueOf("TOKENS"));
	}
	
	@Test
	void valueOfDynamicReturnsDynamicConstant() {
		assertEquals(ReferenceType.DYNAMIC, ReferenceType.valueOf("DYNAMIC"));
	}
	
	@Test
	void ordinalsReflectDeclarationOrder() {
		assertEquals(0, ReferenceType.RULE.ordinal());
		assertEquals(1, ReferenceType.TOKENS.ordinal());
		assertEquals(2, ReferenceType.DYNAMIC.ordinal());
	}
	
	@Test
	void nameReturnsDeclaredConstantName() {
		assertEquals("RULE", ReferenceType.RULE.name());
		assertEquals("TOKENS", ReferenceType.TOKENS.name());
		assertEquals("DYNAMIC", ReferenceType.DYNAMIC.name());
	}
}
