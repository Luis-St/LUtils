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

import net.luis.utils.io.database.function.window.frame.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlWindowFrame}.<br>
 *
 * @author Luis-St
 */
class SqlWindowFrameTest {
	
	@Test
	void rowsWithNullStart() {
		assertThrows(NullPointerException.class, () -> SqlWindowFrame.rows(null, SqlFrameBound.currentRow()));
	}
	
	@Test
	void rowsWithNullEnd() {
		assertThrows(NullPointerException.class, () -> SqlWindowFrame.rows(SqlFrameBound.unboundedPreceding(), null));
	}
	
	@Test
	void rangeWithNullStart() {
		assertThrows(NullPointerException.class, () -> SqlWindowFrame.range(null, SqlFrameBound.currentRow()));
	}
	
	@Test
	void rangeWithNullEnd() {
		assertThrows(NullPointerException.class, () -> SqlWindowFrame.range(SqlFrameBound.unboundedPreceding(), null));
	}
	
	@Test
	void groupsWithNullStart() {
		assertThrows(NullPointerException.class, () -> SqlWindowFrame.groups(null, SqlFrameBound.currentRow()));
	}
	
	@Test
	void groupsWithNullEnd() {
		assertThrows(NullPointerException.class, () -> SqlWindowFrame.groups(SqlFrameBound.unboundedPreceding(), null));
	}
	
	@Test
	void rowsCreatesRowsWindowFrame() {
		SqlFrameBound start = SqlFrameBound.unboundedPreceding();
		SqlFrameBound end = SqlFrameBound.currentRow();
		SqlWindowFrame frame = SqlWindowFrame.rows(start, end);
		assertInstanceOf(RowsWindowFrame.class, frame);
		assertSame(start, frame.start());
		assertSame(end, frame.end());
	}
	
	@Test
	void rangeCreatesRangeWindowFrame() {
		SqlFrameBound start = SqlFrameBound.unboundedPreceding();
		SqlFrameBound end = SqlFrameBound.currentRow();
		SqlWindowFrame frame = SqlWindowFrame.range(start, end);
		assertInstanceOf(RangeWindowFrame.class, frame);
		assertSame(start, frame.start());
		assertSame(end, frame.end());
	}
	
	@Test
	void groupsCreatesGroupsWindowFrame() {
		SqlFrameBound start = SqlFrameBound.unboundedPreceding();
		SqlFrameBound end = SqlFrameBound.currentRow();
		SqlWindowFrame frame = SqlWindowFrame.groups(start, end);
		assertInstanceOf(GroupsWindowFrame.class, frame);
		assertSame(start, frame.start());
		assertSame(end, frame.end());
	}
}
