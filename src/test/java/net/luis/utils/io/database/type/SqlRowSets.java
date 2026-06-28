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

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.sql.rowset.*;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Builds single-column {@link CachedRowSet} fixtures positioned on their single row for the
 * {@code SqlType} read tests (a real {@link ResultSet}, no mocks).<br>
 *
 * @author Luis-St
 */
final class SqlRowSets {
	
	private SqlRowSets() {}
	
	/**
	 * Creates a {@link CachedRowSet} with one row holding a single column of the given JDBC type
	 * and value (or SQL NULL when {@code value} is {@code null}), with the cursor on that row.<br>
	 *
	 * @param jdbcType The JDBC {@link java.sql.Types} constant of the column
	 * @param value The column value, or {@code null} for a SQL NULL column
	 * @return A populated, positioned cached row set
	 */
	static @NonNull CachedRowSet singleColumn(int jdbcType, @Nullable Object value) {
		try {
			RowSetMetaDataImpl metaData = new RowSetMetaDataImpl();
			metaData.setColumnCount(1);
			metaData.setColumnType(1, jdbcType);
			metaData.setColumnName(1, "value");
			
			CachedRowSet rowSet = RowSetProvider.newFactory().createCachedRowSet();
			rowSet.setMetaData(metaData);
			rowSet.moveToInsertRow();
			if (value == null) {
				rowSet.updateNull(1);
			} else {
				rowSet.updateObject(1, value);
			}
			rowSet.insertRow();
			rowSet.moveToCurrentRow();
			rowSet.beforeFirst();
			rowSet.next();
			return rowSet;
		} catch (SQLException e) {
			throw new IllegalStateException("Failed to build cached row set fixture", e);
		}
	}
}
