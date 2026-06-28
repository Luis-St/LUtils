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

package net.luis.utils.io.database.exception.client;

import net.luis.utils.io.database.exception.SqlClientException;
import org.jspecify.annotations.Nullable;

/**
 * Thrown when a referenced table or column cannot be found in the introspected schema.<br>
 *
 * @author Luis-St
 */
public class SqlSchemaObjectNotFoundException extends SqlClientException {
	
	/**
	 * Constructs a new {@code SqlSchemaObjectNotFoundException} with the given detail message.<br>
	 * @param message The detail message, may be null
	 */
	public SqlSchemaObjectNotFoundException(@Nullable String message) {
		super(message);
	}
	
	/**
	 * Constructs a new {@code SqlSchemaObjectNotFoundException} with the given detail message and cause.<br>
	 *
	 * @param message The detail message, may be null
	 * @param cause The cause, may be null
	 */
	public SqlSchemaObjectNotFoundException(@Nullable String message, @Nullable Throwable cause) {
		super(message, cause);
	}
}
