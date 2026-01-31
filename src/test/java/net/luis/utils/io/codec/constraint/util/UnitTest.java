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

package net.luis.utils.io.codec.constraint.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link Unit}.<br>
 *
 * @author Luis-St
 */
class UnitTest {
	
	@Test
	void instanceIsNotNull() {
		assertNotNull(Unit.INSTANCE);
	}
	
	@Test
	void instanceIsSingleton() {
		Unit first = Unit.INSTANCE;
		Unit second = Unit.INSTANCE;
		assertSame(first, second);
		assertEquals(first, second);
	}
	
	@Test
	void toStringReturnsUnit() {
		assertEquals("Unit", Unit.INSTANCE.toString());
	}
}
