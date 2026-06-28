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

package net.luis.utils.io.database.type;

import net.luis.utils.function.throwable.ThrowableFunction;
import net.luis.utils.io.database.exception.SqlClientException;
import net.luis.utils.io.database.exception.database.statement.SqlStatementBindException;
import net.luis.utils.io.database.type.parameter.SqlLengthParameter;
import net.luis.utils.io.database.type.parameter.SqlParameter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link ParameterizableSqlType}.<br>
 *
 * @author Luis-St
 */
class ParameterizableSqlTypeTest {
	
	private static final ParameterizableSqlType<String, SqlLengthParameter> BASE = SqlTypes.STRING;
	private static final ThrowableFunction<Integer, String, SqlStatementBindException> FROM = value -> value == null ? null : String.valueOf(value);
	private static final ThrowableFunction<String, Integer, SqlClientException> TO = Integer::parseInt;
	
	@Test
	void mapWithNullTargetType() {
		assertThrows(NullPointerException.class, () -> BASE.map(null, FROM, TO));
	}
	
	@Test
	void mapWithNullFromTargetToSource() {
		assertThrows(NullPointerException.class, () -> BASE.map(Integer.class, null, TO));
	}
	
	@Test
	void mapWithNullFromSourceToTarget() {
		assertThrows(NullPointerException.class, () -> BASE.map(Integer.class, FROM, null));
	}
	
	@Test
	void mapProducesMappedParameterizableType() {
		ParameterizableSqlType<Integer, SqlLengthParameter> mapped = BASE.map(Integer.class, FROM, TO);
		assertNotNull(mapped);
		assertInstanceOf(MappedParameterizableSqlType.class, mapped);
		assertEquals(BASE.parameterType(), mapped.parameterType());
	}
	
	@Test
	void mapPreservesSourceTypeChain() {
		ParameterizableSqlType<Integer, SqlLengthParameter> mapped = BASE.map(Integer.class, FROM, TO);
		MappedParameterizableSqlType<?, ?, ?> result = assertInstanceOf(MappedParameterizableSqlType.class, mapped);
		assertEquals(BASE, result.sourceType());
		assertInstanceOf(MappedSqlType.class, mapped.configure(SqlParameter.length(10)));
	}
}
