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

package net.luis.utils.io.database.query.util;

import net.luis.utils.io.database.query.SqlSetOperation;
import net.luis.utils.io.database.query.crud.SqlSelectQuery;
import org.junit.jupiter.api.Test;

import static net.luis.utils.io.database.SqlTestFixtures.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlSetOperationEntry}.<br>
 *
 * @author Luis-St
 */
class SqlSetOperationEntryTest {
	
	@Test
	void constructWithValidArguments() {
		SqlSelectQuery<Object> query = sampleSelect();
		SqlSetOperationEntry<Object> entry = new SqlSetOperationEntry<>(SqlSetOperation.UNION, query);
		assertEquals(SqlSetOperation.UNION, entry.operation());
		assertSame(query, entry.query());
	}
	
	@Test
	void constructWithNullOperation() {
		assertThrows(NullPointerException.class, () -> new SqlSetOperationEntry<>(null, sampleSelect()));
	}
	
	@Test
	void constructWithNullQuery() {
		assertThrows(NullPointerException.class, () -> new SqlSetOperationEntry<>(SqlSetOperation.UNION, null));
	}
	
	@Test
	void equalsAndHashCodeForSameComponents() {
		SqlSelectQuery<Object> query = sampleSelect();
		SqlSetOperationEntry<Object> first = new SqlSetOperationEntry<>(SqlSetOperation.UNION, query);
		SqlSetOperationEntry<Object> second = new SqlSetOperationEntry<>(SqlSetOperation.UNION, query);
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
	}
}
