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

package net.luis.utils.io.database.function.window;

import net.luis.utils.io.database.function.window.frame.bound.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlFrameBound}.<br>
 *
 * @author Luis-St
 */
class SqlFrameBoundTest {
	
	@Test
	void precedingWithNegativeOffset() {
		assertThrows(IllegalArgumentException.class, () -> SqlFrameBound.preceding(-1));
	}
	
	@Test
	void followingWithNegativeOffset() {
		assertThrows(IllegalArgumentException.class, () -> SqlFrameBound.following(-1));
	}
	
	@Test
	void unboundedPrecedingCreatesType() {
		assertInstanceOf(UnboundedPrecedingFrameBound.class, SqlFrameBound.unboundedPreceding());
	}
	
	@Test
	void precedingCreatesType() {
		SqlFrameBound bound = SqlFrameBound.preceding(3);
		assertInstanceOf(PrecedingFrameBound.class, bound);
		assertEquals(3, ((PrecedingFrameBound) bound).offset());
	}
	
	@Test
	void currentRowCreatesType() {
		assertInstanceOf(CurrentRowFrameBound.class, SqlFrameBound.currentRow());
	}
	
	@Test
	void followingCreatesType() {
		SqlFrameBound bound = SqlFrameBound.following(2);
		assertInstanceOf(FollowingFrameBound.class, bound);
		assertEquals(2, ((FollowingFrameBound) bound).offset());
	}
	
	@Test
	void unboundedFollowingCreatesType() {
		assertInstanceOf(UnboundedFollowingFrameBound.class, SqlFrameBound.unboundedFollowing());
	}
	
	@Test
	void precedingWithZeroOffset() {
		SqlFrameBound bound = SqlFrameBound.preceding(0);
		assertEquals(0, ((PrecedingFrameBound) bound).offset());
	}
	
	@Test
	void followingWithZeroOffset() {
		SqlFrameBound bound = SqlFrameBound.following(0);
		assertEquals(0, ((FollowingFrameBound) bound).offset());
	}
}
