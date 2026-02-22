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
 * Interface representing the features supported by a SQL dialect.<br>
 *
 * @author Luis-St
 */
public interface SqlDialectFeatures {

	/**
	 * Returns whether the dialect supports the {@code RETURNING} clause.<br>
	 * @return Whether {@code RETURNING} is supported
	 */
	boolean supportsReturning();

	/**
	 * Returns whether the dialect supports {@code SKIP LOCKED}.<br>
	 * @return Whether {@code SKIP LOCKED} is supported
	 */
	boolean supportsSkipLocked();

	/**
	 * Returns whether the dialect supports array column types.<br>
	 * @return Whether arrays are supported
	 */
	boolean supportsArrays();

	/**
	 * Returns whether the dialect supports the {@code JSONB} data type.<br>
	 * @return Whether {@code JSONB} is supported
	 */
	boolean supportsJsonb();

	/**
	 * Returns whether the dialect supports partial indexes.<br>
	 * @return Whether partial indexes are supported
	 */
	boolean supportsPartialIndexes();

	/**
	 * Returns whether the dialect supports sequences.<br>
	 * @return Whether sequences are supported
	 */
	boolean supportsSequences();

	/**
	 * Returns whether the dialect supports Common Table Expressions (CTEs).<br>
	 * @return Whether CTEs are supported
	 */
	boolean supportsCte();

	/**
	 * Returns whether the dialect supports window functions.<br>
	 * @return Whether window functions are supported
	 */
	boolean supportsWindowFunctions();

	/**
	 * Returns whether the dialect supports a native boolean type.<br>
	 * @return Whether the boolean type is supported
	 */
	boolean supportsBooleanType();

	/**
	 * Returns whether the dialect supports upsert operations.<br>
	 * @return Whether upsert is supported
	 */
	boolean supportsUpsert();

	/**
	 * Returns whether the dialect supports database schemas.<br>
	 * @return Whether schemas are supported
	 */
	boolean supportsSchemas();

	/**
	 * Returns whether the dialect supports generated columns.<br>
	 * @return Whether generated columns are supported
	 */
	boolean supportsGeneratedColumns();

	/**
	 * Returns whether the dialect supports the {@code MERGE} statement.<br>
	 * @return Whether {@code MERGE} is supported
	 */
	boolean supportsMerge();

	/**
	 * Returns whether the dialect supports {@code LATERAL} joins.<br>
	 * @return Whether {@code LATERAL} is supported
	 */
	boolean supportsLateral();

	/**
	 * Returns whether the dialect supports JSON operations.<br>
	 * @return Whether JSON is supported
	 */
	boolean supportsJson();

	/**
	 * Returns whether the dialect supports {@code TRUNCATE ... CASCADE}.<br>
	 * @return Whether truncate cascade is supported
	 */
	boolean supportsTruncateCascade();

	/**
	 * Returns whether the dialect supports table inheritance.<br>
	 * @return Whether table inheritance is supported
	 */
	boolean supportsTableInheritance();

	/**
	 * Returns whether the dialect supports identity columns.<br>
	 * @return Whether identity columns are supported
	 */
	boolean supportsIdentityColumns();
}
