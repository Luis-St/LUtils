/*
 * LUtils
 * Copyright (C) 2024 Luis Staudt
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

package net.luis.utils.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link Chance}.<br>
 *
 * @author Luis-St
 */
class ChanceTest {
	
	@Test
	void of() {
		assertNotNull(Chance.of(1.0));
		assertDoesNotThrow(() -> Chance.of(1.0));
	}
	
	@Test
	void isTrue() {
		assertTrue(Chance.of(1.0).isTrue());
		assertFalse(Chance.of(0.9).isTrue());
	}
	
	@Test
	void isFalse() {
		assertTrue(Chance.of(0.0).isFalse());
		assertFalse(Chance.of(0.1).isFalse());
	}
	
	@Test
	void result() {
		assertTrue(Chance.of(1.0).result());
		assertFalse(Chance.of(0.0).result());
	}
}