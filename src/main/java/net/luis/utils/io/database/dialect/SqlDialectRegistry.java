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

import org.jspecify.annotations.NonNull;

/**
 * Registry for auto-detecting SQL dialects from JDBC URLs.<br>
 *
 * @author Luis-St
 */
public class SqlDialectRegistry {
	
	/**
	 * Sql dialect for PostgreSQL databases.<br>
	 */
	public static final PostgresDialect POSTGRES = new PostgresDialect();
	/**
	 * Sql dialect for MySQL databases.<br>
	 */
	public static final MysqlDialect MYSQL = new MysqlDialect();
	/**
	 * Sql dialect for MariaDB databases.<br>
	 */
	public static final MariaDialect MARIA = new MariaDialect();
	/**
	 * Sql dialect for SQLite databases.<br>
	 */
	public static final SqliteDialect SQLITE = new SqliteDialect();
	/**
	 * Default SQL dialect for unknown databases.<br>
	 */
	public static final SqlDefaultDialect DEFAULT = new SqlDefaultDialect();
	
	/**
	 * Detects the SQL dialect from the given JDBC URL prefix.<br>
	 *
	 * @param jdbcUrl The JDBC URL to detect the dialect from
	 * @return The detected dialect, or the default dialect if no match is found
	 */
	public static @NonNull SqlDialect<?, ?> detect(@NonNull String jdbcUrl) {
		if (jdbcUrl.startsWith("jdbc:postgresql:")) {
			return POSTGRES;
		} else if (jdbcUrl.startsWith("jdbc:mysql:")) {
			return MYSQL;
		} else if (jdbcUrl.startsWith("jdbc:mariadb:")) {
			return MARIA;
		} else if (jdbcUrl.startsWith("jdbc:sqlite:")) {
			return SQLITE;
		}
		return DEFAULT;
	}
}
