package net.luis.utils.io.token.actions.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link GroupingMode}.<br>
 *
 * @author Luis-St
 */
class GroupingModeTest {
	
	@Test
	void toStringTest() {
		assertEquals("ALL", GroupingMode.ALL.toString());
		assertEquals("MATCHED", GroupingMode.MATCHED.toString());
	}
}
