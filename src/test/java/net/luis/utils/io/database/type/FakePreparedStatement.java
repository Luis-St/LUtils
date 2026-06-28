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

import java.sql.PreparedStatement;

/**
 * A non-functional {@link PreparedStatement} placeholder for tests that need a non-null
 * statement reference but never actually invoke any of its methods (e.g. argument-guard tests
 * that throw before touching the statement).<br>
 * Every method throws {@link UnsupportedOperationException}.<br>
 *
 * @author Luis-St
 */
class FakePreparedStatement implements PreparedStatement {
	
	@Override
	public <T> T unwrap(java.lang.Class<T> p0) throws java.sql.SQLException {throw new UnsupportedOperationException();}
	
	@Override
	public boolean execute() throws java.sql.SQLException {throw new UnsupportedOperationException();}
	
	@Override
	public boolean execute(java.lang.String p0) throws java.sql.SQLException {throw new UnsupportedOperationException();}
	
	@Override
	public boolean execute(java.lang.String p0, int p1) throws java.sql.SQLException {throw new UnsupportedOperationException();}
	
	@Override
	public boolean execute(java.lang.String p0, int[] p1) throws java.sql.SQLException {throw new UnsupportedOperationException();}
	
	@Override
	public boolean execute(java.lang.String p0, java.lang.String[] p1) throws java.sql.SQLException {throw new UnsupportedOperationException();}
	
	@Override
	public boolean getMoreResults() throws java.sql.SQLException {throw new UnsupportedOperationException();}
	
	@Override
	public boolean getMoreResults(int p0) throws java.sql.SQLException {throw new UnsupportedOperationException();}
	
	@Override
	public boolean isCloseOnCompletion() throws java.sql.SQLException {throw new UnsupportedOperationException();}
	
	@Override
	public boolean isClosed() throws java.sql.SQLException {throw new UnsupportedOperationException();}
	
	@Override
	public boolean isPoolable() throws java.sql.SQLException {throw new UnsupportedOperationException();}
	
	@Override
	public void setPoolable(boolean p0) throws java.sql.SQLException {throw new UnsupportedOperationException();}
	
	@Override
	public boolean isSimpleIdentifier(java.lang.String p0) throws java.sql.SQLException {throw new UnsupportedOperationException();}
	
	@Override
	public boolean isWrapperFor(java.lang.Class<?> p0) throws java.sql.SQLException {throw new UnsupportedOperationException();}
	
	@Override
	public int executeUpdate() throws java.sql.SQLException {throw new UnsupportedOperationException();}
	
	@Override
	public int executeUpdate(java.lang.String p0) throws java.sql.SQLException {throw new UnsupportedOperationException();}
	
	@Override
	public int executeUpdate(java.lang.String p0, int p1) throws java.sql.SQLException {throw new UnsupportedOperationException();}
	
	@Override
	public int executeUpdate(java.lang.String p0, int[] p1) throws java.sql.SQLException {throw new UnsupportedOperationException();}
	
	@Override
	public int executeUpdate(java.lang.String p0, java.lang.String[] p1) throws java.sql.SQLException {throw new UnsupportedOperationException();}
	
	@Override
	public int getFetchDirection() throws java.sql.SQLException {throw new UnsupportedOperationException();}
	
	@Override
	public void setFetchDirection(int p0) throws java.sql.SQLException {throw new UnsupportedOperationException();}
	
	@Override
	public int getFetchSize() throws java.sql.SQLException {throw new UnsupportedOperationException();}
	
	@Override
	public void setFetchSize(int p0) throws java.sql.SQLException {throw new UnsupportedOperationException();}
	
	@Override
	public int getMaxFieldSize() throws java.sql.SQLException {throw new UnsupportedOperationException();}
	
	@Override
	public void setMaxFieldSize(int p0) throws java.sql.SQLException {throw new UnsupportedOperationException();}
	
	@Override
	public int getMaxRows() throws java.sql.SQLException {throw new UnsupportedOperationException();}
	
	@Override
	public void setMaxRows(int p0) throws java.sql.SQLException {throw new UnsupportedOperationException();}
	
	@Override
	public int getQueryTimeout() throws java.sql.SQLException {throw new UnsupportedOperationException();}
	
	@Override
	public void setQueryTimeout(int p0) throws java.sql.SQLException {throw new UnsupportedOperationException();}
	
	@Override
	public int getResultSetConcurrency() throws java.sql.SQLException {throw new UnsupportedOperationException();}
	
	@Override
	public int getResultSetHoldability() throws java.sql.SQLException {throw new UnsupportedOperationException();}
	
	@Override
	public int getResultSetType() throws java.sql.SQLException {throw new UnsupportedOperationException();}
	
	@Override
	public int getUpdateCount() throws java.sql.SQLException {throw new UnsupportedOperationException();}
	
	@Override
	public int[] executeBatch() throws java.sql.SQLException {throw new UnsupportedOperationException();}
	
	@Override
	public java.lang.String enquoteIdentifier(java.lang.String p0, boolean p1) throws java.sql.SQLException {throw new UnsupportedOperationException();}
	
	@Override
	public java.lang.String enquoteLiteral(java.lang.String p0) throws java.sql.SQLException {throw new UnsupportedOperationException();}
	
	@Override
	public java.lang.String enquoteNCharLiteral(java.lang.String p0) throws java.sql.SQLException {throw new UnsupportedOperationException();}
	
	@Override
	public java.sql.Connection getConnection() throws java.sql.SQLException {throw new UnsupportedOperationException();}
	
	@Override
	public java.sql.ParameterMetaData getParameterMetaData() throws java.sql.SQLException {throw new UnsupportedOperationException();}
	
	@Override
	public java.sql.ResultSet executeQuery() throws java.sql.SQLException {throw new UnsupportedOperationException();}
	
	@Override
	public java.sql.ResultSet executeQuery(java.lang.String p0) throws java.sql.SQLException {throw new UnsupportedOperationException();}
	
	@Override
	public java.sql.ResultSet getGeneratedKeys() throws java.sql.SQLException {throw new UnsupportedOperationException();}
	
	@Override
	public java.sql.ResultSet getResultSet() throws java.sql.SQLException {throw new UnsupportedOperationException();}
	
	@Override
	public java.sql.ResultSetMetaData getMetaData() throws java.sql.SQLException {throw new UnsupportedOperationException();}
	
	@Override
	public java.sql.SQLWarning getWarnings() throws java.sql.SQLException {throw new UnsupportedOperationException();}
	
	@Override
	public long executeLargeUpdate() throws java.sql.SQLException {throw new UnsupportedOperationException();}
	
	@Override
	public long executeLargeUpdate(java.lang.String p0) throws java.sql.SQLException {throw new UnsupportedOperationException();}
	
	@Override
	public long executeLargeUpdate(java.lang.String p0, int p1) throws java.sql.SQLException {throw new UnsupportedOperationException();}
	
	@Override
	public long executeLargeUpdate(java.lang.String p0, int[] p1) throws java.sql.SQLException {throw new UnsupportedOperationException();}
	
	@Override
	public long executeLargeUpdate(java.lang.String p0, java.lang.String[] p1) throws java.sql.SQLException {throw new UnsupportedOperationException();}
	
	@Override
	public long getLargeMaxRows() throws java.sql.SQLException {throw new UnsupportedOperationException();}
	
	@Override
	public void setLargeMaxRows(long p0) throws java.sql.SQLException {throw new UnsupportedOperationException();}
	
	@Override
	public long getLargeUpdateCount() throws java.sql.SQLException {throw new UnsupportedOperationException();}
	
	@Override
	public long[] executeLargeBatch() throws java.sql.SQLException {throw new UnsupportedOperationException();}
	
	@Override
	public void addBatch() throws java.sql.SQLException {throw new UnsupportedOperationException();}
	
	@Override
	public void addBatch(java.lang.String p0) throws java.sql.SQLException {throw new UnsupportedOperationException();}
	
	@Override
	public void cancel() throws java.sql.SQLException {throw new UnsupportedOperationException();}
	
	@Override
	public void clearBatch() throws java.sql.SQLException {throw new UnsupportedOperationException();}
	
	@Override
	public void clearParameters() throws java.sql.SQLException {throw new UnsupportedOperationException();}
	
	@Override
	public void clearWarnings() throws java.sql.SQLException {throw new UnsupportedOperationException();}
	
	@Override
	public void close() throws java.sql.SQLException {throw new UnsupportedOperationException();}
	
	@Override
	public void closeOnCompletion() throws java.sql.SQLException {throw new UnsupportedOperationException();}
	
	@Override
	public void setArray(int p0, java.sql.Array p1) throws java.sql.SQLException {throw new UnsupportedOperationException();}
	
	@Override
	public void setAsciiStream(int p0, java.io.InputStream p1) throws java.sql.SQLException {throw new UnsupportedOperationException();}
	
	@Override
	public void setAsciiStream(int p0, java.io.InputStream p1, int p2) throws java.sql.SQLException {throw new UnsupportedOperationException();}
	
	@Override
	public void setAsciiStream(int p0, java.io.InputStream p1, long p2) throws java.sql.SQLException {throw new UnsupportedOperationException();}
	
	@Override
	public void setBigDecimal(int p0, java.math.BigDecimal p1) throws java.sql.SQLException {throw new UnsupportedOperationException();}
	
	@Override
	public void setBinaryStream(int p0, java.io.InputStream p1) throws java.sql.SQLException {throw new UnsupportedOperationException();}
	
	@Override
	public void setBinaryStream(int p0, java.io.InputStream p1, int p2) throws java.sql.SQLException {throw new UnsupportedOperationException();}
	
	@Override
	public void setBinaryStream(int p0, java.io.InputStream p1, long p2) throws java.sql.SQLException {throw new UnsupportedOperationException();}
	
	@Override
	public void setBlob(int p0, java.io.InputStream p1) throws java.sql.SQLException {throw new UnsupportedOperationException();}
	
	@Override
	public void setBlob(int p0, java.io.InputStream p1, long p2) throws java.sql.SQLException {throw new UnsupportedOperationException();}
	
	@Override
	public void setBlob(int p0, java.sql.Blob p1) throws java.sql.SQLException {throw new UnsupportedOperationException();}
	
	@Override
	public void setBoolean(int p0, boolean p1) throws java.sql.SQLException {throw new UnsupportedOperationException();}
	
	@Override
	public void setByte(int p0, byte p1) throws java.sql.SQLException {throw new UnsupportedOperationException();}
	
	@Override
	public void setBytes(int p0, byte[] p1) throws java.sql.SQLException {throw new UnsupportedOperationException();}
	
	@Override
	public void setCharacterStream(int p0, java.io.Reader p1) throws java.sql.SQLException {throw new UnsupportedOperationException();}
	
	@Override
	public void setCharacterStream(int p0, java.io.Reader p1, int p2) throws java.sql.SQLException {throw new UnsupportedOperationException();}
	
	@Override
	public void setCharacterStream(int p0, java.io.Reader p1, long p2) throws java.sql.SQLException {throw new UnsupportedOperationException();}
	
	@Override
	public void setClob(int p0, java.io.Reader p1) throws java.sql.SQLException {throw new UnsupportedOperationException();}
	
	@Override
	public void setClob(int p0, java.io.Reader p1, long p2) throws java.sql.SQLException {throw new UnsupportedOperationException();}
	
	@Override
	public void setClob(int p0, java.sql.Clob p1) throws java.sql.SQLException {throw new UnsupportedOperationException();}
	
	@Override
	public void setCursorName(java.lang.String p0) throws java.sql.SQLException {throw new UnsupportedOperationException();}
	
	@Override
	public void setDate(int p0, java.sql.Date p1) throws java.sql.SQLException {throw new UnsupportedOperationException();}
	
	@Override
	public void setDate(int p0, java.sql.Date p1, java.util.Calendar p2) throws java.sql.SQLException {throw new UnsupportedOperationException();}
	
	@Override
	public void setDouble(int p0, double p1) throws java.sql.SQLException {throw new UnsupportedOperationException();}
	
	@Override
	public void setEscapeProcessing(boolean p0) throws java.sql.SQLException {throw new UnsupportedOperationException();}
	
	@Override
	public void setFloat(int p0, float p1) throws java.sql.SQLException {throw new UnsupportedOperationException();}
	
	@Override
	public void setInt(int p0, int p1) throws java.sql.SQLException {throw new UnsupportedOperationException();}
	
	@Override
	public void setLong(int p0, long p1) throws java.sql.SQLException {throw new UnsupportedOperationException();}
	
	@Override
	public void setNCharacterStream(int p0, java.io.Reader p1) throws java.sql.SQLException {throw new UnsupportedOperationException();}
	
	@Override
	public void setNCharacterStream(int p0, java.io.Reader p1, long p2) throws java.sql.SQLException {throw new UnsupportedOperationException();}
	
	@Override
	public void setNClob(int p0, java.io.Reader p1) throws java.sql.SQLException {throw new UnsupportedOperationException();}
	
	@Override
	public void setNClob(int p0, java.io.Reader p1, long p2) throws java.sql.SQLException {throw new UnsupportedOperationException();}
	
	@Override
	public void setNClob(int p0, java.sql.NClob p1) throws java.sql.SQLException {throw new UnsupportedOperationException();}
	
	@Override
	public void setNString(int p0, java.lang.String p1) throws java.sql.SQLException {throw new UnsupportedOperationException();}
	
	@Override
	public void setNull(int p0, int p1) throws java.sql.SQLException {throw new UnsupportedOperationException();}
	
	@Override
	public void setNull(int p0, int p1, java.lang.String p2) throws java.sql.SQLException {throw new UnsupportedOperationException();}
	
	@Override
	public void setObject(int p0, java.lang.Object p1) throws java.sql.SQLException {throw new UnsupportedOperationException();}
	
	@Override
	public void setObject(int p0, java.lang.Object p1, int p2) throws java.sql.SQLException {throw new UnsupportedOperationException();}
	
	@Override
	public void setObject(int p0, java.lang.Object p1, int p2, int p3) throws java.sql.SQLException {throw new UnsupportedOperationException();}
	
	@Override
	public void setObject(int p0, java.lang.Object p1, java.sql.SQLType p2) throws java.sql.SQLException {throw new UnsupportedOperationException();}
	
	@Override
	public void setObject(int p0, java.lang.Object p1, java.sql.SQLType p2, int p3) throws java.sql.SQLException {throw new UnsupportedOperationException();}
	
	@Override
	public void setRef(int p0, java.sql.Ref p1) throws java.sql.SQLException {throw new UnsupportedOperationException();}
	
	@Override
	public void setRowId(int p0, java.sql.RowId p1) throws java.sql.SQLException {throw new UnsupportedOperationException();}
	
	@Override
	public void setSQLXML(int p0, java.sql.SQLXML p1) throws java.sql.SQLException {throw new UnsupportedOperationException();}
	
	@Override
	public void setShort(int p0, short p1) throws java.sql.SQLException {throw new UnsupportedOperationException();}
	
	@Override
	public void setString(int p0, java.lang.String p1) throws java.sql.SQLException {throw new UnsupportedOperationException();}
	
	@Override
	public void setTime(int p0, java.sql.Time p1) throws java.sql.SQLException {throw new UnsupportedOperationException();}
	
	@Override
	public void setTime(int p0, java.sql.Time p1, java.util.Calendar p2) throws java.sql.SQLException {throw new UnsupportedOperationException();}
	
	@Override
	public void setTimestamp(int p0, java.sql.Timestamp p1) throws java.sql.SQLException {throw new UnsupportedOperationException();}
	
	@Override
	public void setTimestamp(int p0, java.sql.Timestamp p1, java.util.Calendar p2) throws java.sql.SQLException {throw new UnsupportedOperationException();}
	
	@Override
	public void setURL(int p0, java.net.URL p1) throws java.sql.SQLException {throw new UnsupportedOperationException();}
	
	@Override
	public void setUnicodeStream(int p0, java.io.InputStream p1, int p2) throws java.sql.SQLException {throw new UnsupportedOperationException();}
}
