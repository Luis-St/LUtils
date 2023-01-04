package net.luis.utils.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


/**
 *
 * @author Luis-st
 *
 */

class ChanceTest {
	
	@Test
	void of() {
		assertNotNull(Chance.of(1.0));
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