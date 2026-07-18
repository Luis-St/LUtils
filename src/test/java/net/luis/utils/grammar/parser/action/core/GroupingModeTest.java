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

package net.luis.utils.grammar.parser.action.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link GroupingMode}.<br>
 *
 * @author Luis-St
 */
class GroupingModeTest {
	
	@Test
	void valueOfWithUnknownNameThrows() {
		assertThrows(IllegalArgumentException.class, () -> GroupingMode.valueOf("UNKNOWN"));
	}
	
	@Test
	void valueOfWithNullNameThrows() {
		assertThrows(NullPointerException.class, () -> GroupingMode.valueOf(null));
	}
	
	@Test
	void valuesContainsBothConstantsInDeclarationOrder() {
		GroupingMode[] values = GroupingMode.values();
		assertEquals(2, values.length);
		assertEquals(GroupingMode.MATCHED, values[0]);
		assertEquals(GroupingMode.ALL, values[1]);
	}
	
	@Test
	void valueOfMatchedReturnsMatchedConstant() {
		assertEquals(GroupingMode.MATCHED, GroupingMode.valueOf("MATCHED"));
	}
	
	@Test
	void valueOfAllReturnsAllConstant() {
		assertEquals(GroupingMode.ALL, GroupingMode.valueOf("ALL"));
	}
	
	@Test
	void ordinalsReflectDeclarationOrder() {
		assertEquals(0, GroupingMode.MATCHED.ordinal());
		assertEquals(1, GroupingMode.ALL.ordinal());
	}
	
	@Test
	void nameReturnsDeclaredConstantName() {
		assertEquals("MATCHED", GroupingMode.MATCHED.name());
		assertEquals("ALL", GroupingMode.ALL.name());
	}
}
