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

package net.luis.utils.io.database.function.window.frame;

import net.luis.utils.io.database.function.window.SqlFrameBound;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link RangeWindowFrame}.<br>
 *
 * @author Luis-St
 */
class RangeWindowFrameTest {
	
	@Test
	void constructWithStartAndEnd() {
		SqlFrameBound start = SqlFrameBound.unboundedPreceding();
		SqlFrameBound end = SqlFrameBound.currentRow();
		RangeWindowFrame frame = new RangeWindowFrame(start, end);
		assertSame(start, frame.start());
		assertSame(end, frame.end());
	}
	
	@Test
	void constructWithNullStart() {
		assertThrows(NullPointerException.class, () -> new RangeWindowFrame(null, SqlFrameBound.currentRow()));
	}
	
	@Test
	void constructWithNullEnd() {
		assertThrows(NullPointerException.class, () -> new RangeWindowFrame(SqlFrameBound.unboundedPreceding(), null));
	}
	
	@Test
	void recordEqualityByComponents() {
		RangeWindowFrame first = new RangeWindowFrame(SqlFrameBound.unboundedPreceding(), SqlFrameBound.currentRow());
		RangeWindowFrame second = new RangeWindowFrame(SqlFrameBound.unboundedPreceding(), SqlFrameBound.currentRow());
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
	}
}
