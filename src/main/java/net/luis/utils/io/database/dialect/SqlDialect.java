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

import net.luis.utils.io.database.renderer.SqlRenderer;
import org.jspecify.annotations.NonNull;

/**
 * Abstract class representing a SQL dialect.<br>
 * <p>
 *     Dialects define the type mappings and feature support for a specific database engine.<br>
 *     Rendering of SQL syntax is delegated to {@link SqlRenderer} via {@link #createRenderer()}.<br>
 * </p>
 *
 * @author Luis-St
 */
public abstract class SqlDialect {

	/**
	 * Returns the identifier of this dialect.<br>
	 * @return The dialect identifier
	 */
	public @NonNull String getId() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Returns the display name of this dialect.<br>
	 * @return The dialect name
	 */
	public @NonNull String getName() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Returns the features supported by this dialect.<br>
	 * @return The dialect features
	 */
	public @NonNull SqlDialectFeatures getFeatures() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Maps a column type to the dialect-specific SQL type name.<br>
	 *
	 * @param type The column type to map
	 * @return The SQL type name
	 */
	public @NonNull String mapColumnType(@NonNull SqlColumnType type) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Maps a Java type to the corresponding SQL column type.<br>
	 *
	 * @param javaType The Java type to map
	 * @return The SQL column type
	 */
	public @NonNull SqlColumnType mapJavaType(@NonNull Class<?> javaType) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Creates a new renderer for this dialect.<br>
	 * @return A renderer that produces SQL syntax for this dialect
	 */
	public @NonNull SqlRenderer createRenderer() {
		throw new UnsupportedOperationException();
	}
}
