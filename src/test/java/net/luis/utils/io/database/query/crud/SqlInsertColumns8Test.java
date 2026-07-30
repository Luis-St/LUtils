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


package net.luis.utils.io.database.query.crud;

import net.luis.utils.io.database.SqlConnectionSource;
import net.luis.utils.io.database.audit.SqlAuditUserProvider;
import net.luis.utils.io.database.dialect.SqlDialects;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.exception.client.SqlStatementBuilderException;
import net.luis.utils.io.database.query.util.SqlColumnValue;
import net.luis.utils.io.database.table.SqlColumn;
import net.luis.utils.io.database.table.SqlTable;
import org.junit.jupiter.api.Test;

import java.util.*;

import static net.luis.utils.io.database.SqlTestFixtures.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlInsertColumns8}.<br>
 *
 * @author Luis-St
 */
class SqlInsertColumns8Test {
	
	private static SqlTable<Object> table() {
		return SqlTable.create(Object.class, "test_table");
	}
	
	private static SqlInsertColumnsBuilder<Object> emptyBuilder(SqlTable<Object> table) {
		return new SqlInsertColumnsBuilder<>(table, DIALECT, SOURCE, TIMEOUT, resultSet -> null, null, List.of());
	}
	
	@Test
	void constructWithValidBuilderAndColumns() {
		SqlTable<Object> table = table();
		SqlColumn<Object, Integer> column1 = table.column("col1", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column2 = table.column("col2", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column3 = table.column("col3", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column4 = table.column("col4", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column5 = table.column("col5", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column6 = table.column("col6", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column7 = table.column("col7", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column8 = table.column("col8", INTEGER_TYPE, o -> 0);
		SqlInsertColumns8<Object, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer> wrapper = new SqlInsertColumns8<>(emptyBuilder(table), column1, column2, column3, column4, column5, column6, column7, column8);
		assertNotNull(wrapper);
	}
	
	@Test
	void constructWithNullBuilder() {
		SqlTable<Object> table = table();
		SqlColumn<Object, Integer> column1 = table.column("col1", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column2 = table.column("col2", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column3 = table.column("col3", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column4 = table.column("col4", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column5 = table.column("col5", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column6 = table.column("col6", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column7 = table.column("col7", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column8 = table.column("col8", INTEGER_TYPE, o -> 0);
		assertThrows(NullPointerException.class, () -> new SqlInsertColumns8<>(null, column1, column2, column3, column4, column5, column6, column7, column8));
	}
	
	@Test
	void constructWithNullColumn1() {
		SqlTable<Object> table = table();
		SqlColumn<Object, Integer> column1 = table.column("col1", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column2 = table.column("col2", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column3 = table.column("col3", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column4 = table.column("col4", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column5 = table.column("col5", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column6 = table.column("col6", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column7 = table.column("col7", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column8 = table.column("col8", INTEGER_TYPE, o -> 0);
		assertThrows(NullPointerException.class, () -> new SqlInsertColumns8<>(emptyBuilder(table), null, column2, column3, column4, column5, column6, column7, column8));
	}
	
	@Test
	void constructWithNullColumn2() {
		SqlTable<Object> table = table();
		SqlColumn<Object, Integer> column1 = table.column("col1", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column2 = table.column("col2", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column3 = table.column("col3", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column4 = table.column("col4", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column5 = table.column("col5", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column6 = table.column("col6", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column7 = table.column("col7", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column8 = table.column("col8", INTEGER_TYPE, o -> 0);
		assertThrows(NullPointerException.class, () -> new SqlInsertColumns8<>(emptyBuilder(table), column1, null, column3, column4, column5, column6, column7, column8));
	}
	
	@Test
	void constructWithNullColumn3() {
		SqlTable<Object> table = table();
		SqlColumn<Object, Integer> column1 = table.column("col1", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column2 = table.column("col2", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column3 = table.column("col3", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column4 = table.column("col4", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column5 = table.column("col5", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column6 = table.column("col6", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column7 = table.column("col7", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column8 = table.column("col8", INTEGER_TYPE, o -> 0);
		assertThrows(NullPointerException.class, () -> new SqlInsertColumns8<>(emptyBuilder(table), column1, column2, null, column4, column5, column6, column7, column8));
	}
	
	@Test
	void constructWithNullColumn4() {
		SqlTable<Object> table = table();
		SqlColumn<Object, Integer> column1 = table.column("col1", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column2 = table.column("col2", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column3 = table.column("col3", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column4 = table.column("col4", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column5 = table.column("col5", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column6 = table.column("col6", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column7 = table.column("col7", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column8 = table.column("col8", INTEGER_TYPE, o -> 0);
		assertThrows(NullPointerException.class, () -> new SqlInsertColumns8<>(emptyBuilder(table), column1, column2, column3, null, column5, column6, column7, column8));
	}
	
	@Test
	void constructWithNullColumn5() {
		SqlTable<Object> table = table();
		SqlColumn<Object, Integer> column1 = table.column("col1", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column2 = table.column("col2", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column3 = table.column("col3", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column4 = table.column("col4", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column5 = table.column("col5", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column6 = table.column("col6", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column7 = table.column("col7", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column8 = table.column("col8", INTEGER_TYPE, o -> 0);
		assertThrows(NullPointerException.class, () -> new SqlInsertColumns8<>(emptyBuilder(table), column1, column2, column3, column4, null, column6, column7, column8));
	}
	
	@Test
	void constructWithNullColumn6() {
		SqlTable<Object> table = table();
		SqlColumn<Object, Integer> column1 = table.column("col1", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column2 = table.column("col2", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column3 = table.column("col3", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column4 = table.column("col4", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column5 = table.column("col5", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column6 = table.column("col6", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column7 = table.column("col7", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column8 = table.column("col8", INTEGER_TYPE, o -> 0);
		assertThrows(NullPointerException.class, () -> new SqlInsertColumns8<>(emptyBuilder(table), column1, column2, column3, column4, column5, null, column7, column8));
	}
	
	@Test
	void constructWithNullColumn7() {
		SqlTable<Object> table = table();
		SqlColumn<Object, Integer> column1 = table.column("col1", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column2 = table.column("col2", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column3 = table.column("col3", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column4 = table.column("col4", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column5 = table.column("col5", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column6 = table.column("col6", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column7 = table.column("col7", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column8 = table.column("col8", INTEGER_TYPE, o -> 0);
		assertThrows(NullPointerException.class, () -> new SqlInsertColumns8<>(emptyBuilder(table), column1, column2, column3, column4, column5, column6, null, column8));
	}
	
	@Test
	void constructWithNullColumn8() {
		SqlTable<Object> table = table();
		SqlColumn<Object, Integer> column1 = table.column("col1", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column2 = table.column("col2", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column3 = table.column("col3", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column4 = table.column("col4", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column5 = table.column("col5", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column6 = table.column("col6", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column7 = table.column("col7", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column8 = table.column("col8", INTEGER_TYPE, o -> 0);
		assertThrows(NullPointerException.class, () -> new SqlInsertColumns8<>(emptyBuilder(table), column1, column2, column3, column4, column5, column6, column7, null));
	}
	
	@Test
	void rowWithNullValue1Throws() {
		SqlTable<Object> table = table();
		SqlColumn<Object, Integer> column1 = table.column("col1", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column2 = table.column("col2", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column3 = table.column("col3", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column4 = table.column("col4", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column5 = table.column("col5", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column6 = table.column("col6", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column7 = table.column("col7", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column8 = table.column("col8", INTEGER_TYPE, o -> 0);
		SqlInsertColumns8<Object, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer> wrapper = new SqlInsertColumns8<>(emptyBuilder(table), column1, column2, column3, column4, column5, column6, column7, column8);
		assertThrows(NullPointerException.class, () -> wrapper.row(null, 1, 1, 1, 1, 1, 1, 1));
	}
	
	@Test
	void rowWithNullValue2Throws() {
		SqlTable<Object> table = table();
		SqlColumn<Object, Integer> column1 = table.column("col1", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column2 = table.column("col2", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column3 = table.column("col3", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column4 = table.column("col4", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column5 = table.column("col5", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column6 = table.column("col6", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column7 = table.column("col7", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column8 = table.column("col8", INTEGER_TYPE, o -> 0);
		SqlInsertColumns8<Object, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer> wrapper = new SqlInsertColumns8<>(emptyBuilder(table), column1, column2, column3, column4, column5, column6, column7, column8);
		assertThrows(NullPointerException.class, () -> wrapper.row(1, null, 1, 1, 1, 1, 1, 1));
	}
	
	@Test
	void rowWithNullValue3Throws() {
		SqlTable<Object> table = table();
		SqlColumn<Object, Integer> column1 = table.column("col1", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column2 = table.column("col2", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column3 = table.column("col3", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column4 = table.column("col4", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column5 = table.column("col5", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column6 = table.column("col6", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column7 = table.column("col7", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column8 = table.column("col8", INTEGER_TYPE, o -> 0);
		SqlInsertColumns8<Object, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer> wrapper = new SqlInsertColumns8<>(emptyBuilder(table), column1, column2, column3, column4, column5, column6, column7, column8);
		assertThrows(NullPointerException.class, () -> wrapper.row(1, 1, null, 1, 1, 1, 1, 1));
	}
	
	@Test
	void rowWithNullValue4Throws() {
		SqlTable<Object> table = table();
		SqlColumn<Object, Integer> column1 = table.column("col1", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column2 = table.column("col2", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column3 = table.column("col3", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column4 = table.column("col4", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column5 = table.column("col5", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column6 = table.column("col6", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column7 = table.column("col7", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column8 = table.column("col8", INTEGER_TYPE, o -> 0);
		SqlInsertColumns8<Object, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer> wrapper = new SqlInsertColumns8<>(emptyBuilder(table), column1, column2, column3, column4, column5, column6, column7, column8);
		assertThrows(NullPointerException.class, () -> wrapper.row(1, 1, 1, null, 1, 1, 1, 1));
	}
	
	@Test
	void rowWithNullValue5Throws() {
		SqlTable<Object> table = table();
		SqlColumn<Object, Integer> column1 = table.column("col1", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column2 = table.column("col2", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column3 = table.column("col3", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column4 = table.column("col4", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column5 = table.column("col5", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column6 = table.column("col6", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column7 = table.column("col7", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column8 = table.column("col8", INTEGER_TYPE, o -> 0);
		SqlInsertColumns8<Object, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer> wrapper = new SqlInsertColumns8<>(emptyBuilder(table), column1, column2, column3, column4, column5, column6, column7, column8);
		assertThrows(NullPointerException.class, () -> wrapper.row(1, 1, 1, 1, null, 1, 1, 1));
	}
	
	@Test
	void rowWithNullValue6Throws() {
		SqlTable<Object> table = table();
		SqlColumn<Object, Integer> column1 = table.column("col1", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column2 = table.column("col2", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column3 = table.column("col3", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column4 = table.column("col4", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column5 = table.column("col5", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column6 = table.column("col6", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column7 = table.column("col7", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column8 = table.column("col8", INTEGER_TYPE, o -> 0);
		SqlInsertColumns8<Object, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer> wrapper = new SqlInsertColumns8<>(emptyBuilder(table), column1, column2, column3, column4, column5, column6, column7, column8);
		assertThrows(NullPointerException.class, () -> wrapper.row(1, 1, 1, 1, 1, null, 1, 1));
	}
	
	@Test
	void rowWithNullValue7Throws() {
		SqlTable<Object> table = table();
		SqlColumn<Object, Integer> column1 = table.column("col1", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column2 = table.column("col2", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column3 = table.column("col3", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column4 = table.column("col4", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column5 = table.column("col5", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column6 = table.column("col6", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column7 = table.column("col7", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column8 = table.column("col8", INTEGER_TYPE, o -> 0);
		SqlInsertColumns8<Object, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer> wrapper = new SqlInsertColumns8<>(emptyBuilder(table), column1, column2, column3, column4, column5, column6, column7, column8);
		assertThrows(NullPointerException.class, () -> wrapper.row(1, 1, 1, 1, 1, 1, null, 1));
	}
	
	@Test
	void rowWithNullValue8Throws() {
		SqlTable<Object> table = table();
		SqlColumn<Object, Integer> column1 = table.column("col1", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column2 = table.column("col2", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column3 = table.column("col3", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column4 = table.column("col4", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column5 = table.column("col5", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column6 = table.column("col6", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column7 = table.column("col7", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column8 = table.column("col8", INTEGER_TYPE, o -> 0);
		SqlInsertColumns8<Object, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer> wrapper = new SqlInsertColumns8<>(emptyBuilder(table), column1, column2, column3, column4, column5, column6, column7, column8);
		assertThrows(NullPointerException.class, () -> wrapper.row(1, 1, 1, 1, 1, 1, 1, null));
	}
	
	@Test
	void rowOnPrebuiltMismatchedBuilderThrows() throws SqlException {
		SqlTable<Object> table = table();
		SqlColumn<Object, Integer> other = table.column("other", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column1 = table.column("col1", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column2 = table.column("col2", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column3 = table.column("col3", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column4 = table.column("col4", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column5 = table.column("col5", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column6 = table.column("col6", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column7 = table.column("col7", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column8 = table.column("col8", INTEGER_TYPE, o -> 0);
		SqlInsertColumnsBuilder<Object> prebuilt = emptyBuilder(table).row(List.of(SqlColumnValue.of(other, 1)));
		SqlInsertColumns8<Object, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer> wrapper = new SqlInsertColumns8<>(prebuilt, column1, column2, column3, column4, column5, column6, column7, column8);
		assertThrows(SqlStatementBuilderException.class, () -> wrapper.row(1, 1, 1, 1, 1, 1, 1, 1));
	}
	
	@Test
	void withAuditUserReturnsNewInstanceWithProvider() {
		SqlTable<Object> table = table();
		SqlColumn<Object, Integer> column1 = table.column("col1", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column2 = table.column("col2", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column3 = table.column("col3", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column4 = table.column("col4", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column5 = table.column("col5", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column6 = table.column("col6", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column7 = table.column("col7", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column8 = table.column("col8", INTEGER_TYPE, o -> 0);
		SqlInsertColumns8<Object, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer> wrapper = new SqlInsertColumns8<>(emptyBuilder(table), column1, column2, column3, column4, column5, column6, column7, column8);
		SqlAuditUserProvider provider = () -> Optional.of("alice");
		SqlInsertColumns8<Object, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer> withProvider = wrapper.withAuditUser(provider);
		assertNotNull(withProvider);
		assertNotSame(wrapper, withProvider);
	}
	
	@Test
	void withAuditUserWithNullClearsProvider() {
		SqlTable<Object> table = table();
		SqlColumn<Object, Integer> column1 = table.column("col1", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column2 = table.column("col2", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column3 = table.column("col3", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column4 = table.column("col4", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column5 = table.column("col5", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column6 = table.column("col6", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column7 = table.column("col7", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column8 = table.column("col8", INTEGER_TYPE, o -> 0);
		SqlInsertColumns8<Object, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer> wrapper = new SqlInsertColumns8<>(emptyBuilder(table), column1, column2, column3, column4, column5, column6, column7, column8);
		assertDoesNotThrow(() -> wrapper.withAuditUser(null));
	}
	
	@Test
	void rowAppendsRowAndReturnsNewInstance() throws SqlException {
		SqlTable<Object> table = table();
		SqlColumn<Object, Integer> column1 = table.column("col1", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column2 = table.column("col2", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column3 = table.column("col3", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column4 = table.column("col4", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column5 = table.column("col5", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column6 = table.column("col6", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column7 = table.column("col7", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column8 = table.column("col8", INTEGER_TYPE, o -> 0);
		SqlInsertColumns8<Object, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer> wrapper = new SqlInsertColumns8<>(emptyBuilder(table), column1, column2, column3, column4, column5, column6, column7, column8);
		SqlInsertColumns8<Object, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer> withRow = wrapper.row(1, 1, 1, 1, 1, 1, 1, 1);
		assertNotNull(withRow);
		assertNotSame(wrapper, withRow);
	}
	
	@Test
	void executeDelegatesToBuilder() throws SqlException {
		SqlTable<Object> table = table();
		SqlColumn<Object, Integer> column1 = table.column("col1", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column2 = table.column("col2", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column3 = table.column("col3", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column4 = table.column("col4", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column5 = table.column("col5", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column6 = table.column("col6", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column7 = table.column("col7", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column8 = table.column("col8", INTEGER_TYPE, o -> 0);
		RecordingDataSource source = recordingDataSource().rowsAffected(1);
		SqlInsertColumnsBuilder<Object> builder = new SqlInsertColumnsBuilder<>(table, DIALECT, SqlConnectionSource.pooled(source), TIMEOUT, resultSet -> null, null, List.of());
		SqlInsertColumns8<Object, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer> wrapper = new SqlInsertColumns8<>(builder, column1, column2, column3, column4, column5, column6, column7, column8).row(1, 1, 1, 1, 1, 1, 1, 1);
		int affected = wrapper.execute();
		assertEquals(1, affected);
	}
	
	@Test
	void executeReturningKeysDelegatesToBuilder() throws SqlException {
		SqlTable<Object> table = table();
		SqlColumn<Object, Integer> column1 = table.column("col1", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column2 = table.column("col2", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column3 = table.column("col3", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column4 = table.column("col4", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column5 = table.column("col5", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column6 = table.column("col6", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column7 = table.column("col7", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column8 = table.column("col8", INTEGER_TYPE, o -> 0);
		RecordingDataSource source = recordingDataSource();
		source.enqueueResultSet(generatedKeysResultSet(42L));
		SqlInsertColumnsBuilder<Object> builder = new SqlInsertColumnsBuilder<>(table, DIALECT, SqlConnectionSource.pooled(source), TIMEOUT, resultSet -> null, null, List.of());
		SqlInsertColumns8<Object, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer> wrapper = new SqlInsertColumns8<>(builder, column1, column2, column3, column4, column5, column6, column7, column8).row(1, 1, 1, 1, 1, 1, 1, 1);
		assertEquals(List.of(42L), wrapper.executeReturningKeys());
	}
	
	@Test
	void returningDelegatesToBuilder() throws SqlException {
		SqlTable<Object> table = table();
		SqlColumn<Object, Integer> column1 = table.column("col1", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column2 = table.column("col2", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column3 = table.column("col3", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column4 = table.column("col4", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column5 = table.column("col5", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column6 = table.column("col6", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column7 = table.column("col7", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column8 = table.column("col8", INTEGER_TYPE, o -> 0);
		RecordingDataSource source = recordingDataSource();
		source.enqueueResultSet(labeledResultSet(List.of(Map.of())));
		SqlInsertColumnsBuilder<Object> builder = new SqlInsertColumnsBuilder<>(table, SqlDialects.SQLITE, SqlConnectionSource.pooled(source), TIMEOUT, resultSet -> new Object(), null, List.of());
		SqlInsertColumns8<Object, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer> wrapper = new SqlInsertColumns8<>(builder, column1, column2, column3, column4, column5, column6, column7, column8).row(1, 1, 1, 1, 1, 1, 1, 1);
		assertEquals(1, wrapper.returning().size());
	}
	
	@Test
	void rowCalledMultipleTimesAccumulatesRowsImmutably() throws SqlException {
		SqlTable<Object> table = table();
		SqlColumn<Object, Integer> column1 = table.column("col1", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column2 = table.column("col2", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column3 = table.column("col3", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column4 = table.column("col4", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column5 = table.column("col5", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column6 = table.column("col6", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column7 = table.column("col7", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column8 = table.column("col8", INTEGER_TYPE, o -> 0);
		RecordingDataSource source = recordingDataSource().rowsAffected(1);
		SqlInsertColumnsBuilder<Object> builder = new SqlInsertColumnsBuilder<>(table, DIALECT, SqlConnectionSource.pooled(source), TIMEOUT, resultSet -> null, null, List.of());
		SqlInsertColumns8<Object, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer> w0 = new SqlInsertColumns8<>(builder, column1, column2, column3, column4, column5, column6, column7, column8);
		SqlInsertColumns8<Object, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer> w1 = w0.row(1, 1, 1, 1, 1, 1, 1, 1);
		SqlInsertColumns8<Object, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer> w2 = w1.row(2, 2, 2, 2, 2, 2, 2, 2);
		
		assertThrows(SqlStatementBuilderException.class, w0::execute);
		
		w1.execute();
		w2.execute();
		
		List<String> executed = source.executedSql();
		assertEquals(2, executed.size());
		assertEquals(8, executed.get(0).chars().filter(c -> c == '?').count());
		assertEquals(16, executed.get(1).chars().filter(c -> c == '?').count());
	}
	
	@Test
	void withAuditUserThenRowPreservesAuditUserAcrossRowAppend() throws SqlException {
		SqlTable<Object> table = auditedTable();
		SqlColumn<Object, Integer> column1 = table.column("col1", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column2 = table.column("col2", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column3 = table.column("col3", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column4 = table.column("col4", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column5 = table.column("col5", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column6 = table.column("col6", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column7 = table.column("col7", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, Integer> column8 = table.column("col8", INTEGER_TYPE, o -> 0);
		SqlAuditUserProvider provider = () -> Optional.of("alice");
		
		RecordingDataSource withProviderSource = recordingDataSource().rowsAffected(1);
		SqlInsertColumnsBuilder<Object> withProviderBuilder = new SqlInsertColumnsBuilder<>(table, DIALECT, SqlConnectionSource.pooled(withProviderSource), TIMEOUT, resultSet -> null, null, List.of());
		new SqlInsertColumns8<>(withProviderBuilder, column1, column2, column3, column4, column5, column6, column7, column8).withAuditUser(provider).row(1, 1, 1, 1, 1, 1, 1, 1).execute();
		
		RecordingDataSource withoutProviderSource = recordingDataSource().rowsAffected(1);
		SqlInsertColumnsBuilder<Object> withoutProviderBuilder = new SqlInsertColumnsBuilder<>(table, DIALECT, SqlConnectionSource.pooled(withoutProviderSource), TIMEOUT, resultSet -> null, null, List.of());
		new SqlInsertColumns8<>(withoutProviderBuilder, column1, column2, column3, column4, column5, column6, column7, column8).row(1, 1, 1, 1, 1, 1, 1, 1).execute();
		
		assertNotEquals(withProviderSource.executedSql().getFirst(), withoutProviderSource.executedSql().getFirst());
	}
}
