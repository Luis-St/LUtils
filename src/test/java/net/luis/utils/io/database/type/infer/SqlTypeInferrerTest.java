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

package net.luis.utils.io.database.type.infer;

import net.luis.utils.io.database.SqlTestFixtures;
import net.luis.utils.io.database.exception.client.SqlTypeNotFoundException;
import net.luis.utils.io.database.type.SqlType;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlTypeInferrer}.<br>
 *
 * @author Luis-St
 */
class SqlTypeInferrerTest {
	
	@Test
	void ofWithNullLookupThrows() throws Exception {
		assertThrows(NullPointerException.class, () -> SqlTypeInferrer.of(null));
	}
	
	@Test
	void standardReturnsNonNullInferrer() throws Exception {
		SqlTypeInferrer inferrer = SqlTypeInferrer.standard();
		assertNotNull(inferrer);
		assertInstanceOf(SqlStandardTypeInferrer.class, inferrer);
	}
	
	@Test
	void standardReturnsSingletonInstance() throws Exception {
		assertSame(SqlTypeInferrer.standard(), SqlTypeInferrer.standard());
	}
	
	@Test
	void ofCreatesLookupInferrer() throws Exception {
		Map<Class<?>, SqlType<?>> lookup = Map.of(String.class, SqlTestFixtures.STRING_TYPE);
		SqlTypeInferrer inferrer = SqlTypeInferrer.of(lookup);
		assertNotNull(inferrer);
		assertInstanceOf(SqlLookupTypeInferrer.class, inferrer);
		assertSame(SqlTestFixtures.STRING_TYPE, inferrer.inferType("x"));
	}
	
	@Test
	void ofWithEmptyMapCreatesInferrer() throws Exception {
		SqlTypeInferrer inferrer = SqlTypeInferrer.of(Map.of());
		assertNotNull(inferrer);
		assertThrows(SqlTypeNotFoundException.class, () -> inferrer.inferType("x"));
	}
}
