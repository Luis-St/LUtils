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

package net.luis.utils.io.database.dialect;

/**
 *
 * @author Luis-St
 *
 */

public final class SqlDialects {
	
	public static final SqlDialect H2 = new H2Dialect();
	public static final SqlDialect MARIA_DB = new MariaDbDialect();
	public static final SqlDialect MYSQL = new MySqlDialect();
	public static final SqlDialect POSTGRESQL = new PostgreSqlDialect();
	public static final SqlDialect SQLITE = new SqliteDialect();
	public static final SqlDialect SQL_SERVER = new SqlServerDialect();
	
	private SqlDialects() {}
	
}
