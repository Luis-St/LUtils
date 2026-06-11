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
import net.luis.utils.io.database.type.SqlTypes;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlLookupTypeInferrer}.<br>
 *
 * @author Luis-St
 */
class SqlLookupTypeInferrerTest {
	
	@Test
	void constructWithValidLookup() throws Exception {
		Map<Class<?>, SqlType<?>> lookup = Map.of(String.class, SqlTestFixtures.STRING_TYPE);
		SqlLookupTypeInferrer inferrer = new SqlLookupTypeInferrer(lookup);
		assertSame(SqlTestFixtures.STRING_TYPE, inferrer.inferType("value"));
	}
	
	@Test
	void constructWithNullLookup() throws Exception {
		assertThrows(NullPointerException.class, () -> new SqlLookupTypeInferrer(null));
	}
	
	@Test
	void inferTypeWithNullValue() throws Exception {
		SqlLookupTypeInferrer inferrer = new SqlLookupTypeInferrer(Map.of(String.class, SqlTestFixtures.STRING_TYPE));
		assertThrows(NullPointerException.class, () -> inferrer.inferType(null));
	}
	
	@Test
	void inferTypeWithUnknownTypeThrows() throws Exception {
		Map<Class<?>, SqlType<?>> lookup = Map.of(Integer.class, SqlTestFixtures.INTEGER_TYPE);
		SqlLookupTypeInferrer inferrer = new SqlLookupTypeInferrer(lookup);
		assertThrows(SqlTypeNotFoundException.class, () -> inferrer.inferType("value"));
	}
	
	@Test
	void inferTypeWithEmptyLookupThrows() throws Exception {
		SqlLookupTypeInferrer inferrer = new SqlLookupTypeInferrer(Map.of());
		assertThrows(SqlTypeNotFoundException.class, () -> inferrer.inferType("value"));
	}
	
	@Test
	void inferTypeWithExactMatch() throws Exception {
		Map<Class<?>, SqlType<?>> lookup = Map.of(String.class, SqlTestFixtures.STRING_TYPE);
		SqlLookupTypeInferrer inferrer = new SqlLookupTypeInferrer(lookup);
		assertSame(SqlTestFixtures.STRING_TYPE, inferrer.inferType("value"));
	}
	
	@Test
	void inferTypeWithAssignableSuperType() throws Exception {
		Map<Class<?>, SqlType<?>> lookup = Map.of(CharSequence.class, SqlTestFixtures.STRING_TYPE);
		SqlLookupTypeInferrer inferrer = new SqlLookupTypeInferrer(lookup);
		assertSame(SqlTestFixtures.STRING_TYPE, inferrer.inferType("value"));
	}
	
	@Test
	void inferTypeExactMatchPreferredOverAssignable() throws Exception {
		Map<Class<?>, SqlType<?>> lookup = Map.of(Number.class, SqlTypes.LONG, Integer.class, SqlTypes.INTEGER);
		SqlLookupTypeInferrer inferrer = new SqlLookupTypeInferrer(lookup);
		assertSame(SqlTypes.INTEGER, inferrer.inferType(5));
	}
	
	@Test
	void inferTypeReturnsRegisteredTypeInstance() throws Exception {
		Map<Class<?>, SqlType<?>> lookup = Map.of(String.class, SqlTestFixtures.STRING_TYPE);
		SqlLookupTypeInferrer inferrer = new SqlLookupTypeInferrer(lookup);
		assertSame(SqlTestFixtures.STRING_TYPE, inferrer.inferType("value"));
	}
	
	@Test
	void constructorCopiesLookupDefensively() throws Exception {
		Map<Class<?>, SqlType<?>> source = new HashMap<>();
		source.put(String.class, SqlTestFixtures.STRING_TYPE);
		SqlLookupTypeInferrer inferrer = new SqlLookupTypeInferrer(source);
		
		source.put(Integer.class, SqlTestFixtures.INTEGER_TYPE);
		source.remove(String.class);
		
		assertSame(SqlTestFixtures.STRING_TYPE, inferrer.inferType("value"));
		assertThrows(SqlTypeNotFoundException.class, () -> inferrer.inferType(5));
	}
	
	@Test
	void inferTypeAssignableMatchFollowsInsertionOrder() throws Exception {
		Map<Class<?>, SqlType<?>> lookup = new LinkedHashMap<>();
		lookup.put(Number.class, SqlTypes.LONG);
		lookup.put(Comparable.class, SqlTypes.INTEGER);
		SqlLookupTypeInferrer inferrer = new SqlLookupTypeInferrer(lookup);
		assertSame(SqlTypes.LONG, inferrer.inferType(5));
	}
}
