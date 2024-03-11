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
		assertDoesNotThrow(() -> Chance.of(1.0));
		assertNotNull(Chance.of(1.0));
		assertEquals(Chance.ZERO, Chance.of(0.0));
		assertEquals(Chance.ZERO, Chance.of(-100.0));
		assertEquals(Chance.ONE, Chance.of(1.0));
		assertEquals(Chance.ONE, Chance.of(100.0));
	}
	
	@Test
	void parse() {
		assertDoesNotThrow(() -> Chance.parse(null));
		assertEquals(Chance.ZERO, Chance.parse(null));
		assertEquals(Chance.ZERO, Chance.parse(""));
		assertEquals(Chance.ZERO, Chance.parse("0%"));
		assertEquals(Chance.ZERO, Chance.parse("0.0"));
		assertEquals(Chance.ZERO, Chance.parse(".0%"));
		assertEquals(Chance.ZERO, Chance.parse("0.0%"));
		assertEquals(Chance.of(0.05), Chance.parse("0.05%"));
		assertEquals(Chance.of(0.1), Chance.parse("0.1%"));
		assertEquals(Chance.of(0.5), Chance.parse("0.5%"));
		assertEquals(Chance.ONE, Chance.parse("1.0%"));
	}
	
	@Test
	void setSeed() {
		assertDoesNotThrow(() -> Chance.setSeed(-1));
		assertDoesNotThrow(() -> Chance.setSeed(1));
	}
	
	@Test
	void isTrue() {
		assertFalse(Chance.ZERO.isTrue());
		assertFalse(Chance.of(0.0).isTrue());
		assertFalse(Chance.of(0.9).isTrue());
		assertTrue(Chance.of(1.0).isTrue());
		assertTrue(Chance.of(100.0).isTrue());
		assertTrue(Chance.ONE.isTrue());
	}
	
	@Test
	void isFalse() {
		assertFalse(Chance.ONE.isFalse());
		assertFalse(Chance.of(1.0).isFalse());
		assertFalse(Chance.of(0.1).isFalse());
		assertTrue(Chance.of(0.0).isFalse());
		assertTrue(Chance.of(-100.0).isFalse());
		assertTrue(Chance.ZERO.isFalse());
	}
	
	@Test
	void result() {
		assertTrue(Chance.of(1.0).result());
		assertFalse(Chance.of(0.0).result());
	}
}