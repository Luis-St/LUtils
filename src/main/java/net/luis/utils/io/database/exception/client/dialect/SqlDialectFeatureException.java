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

package net.luis.utils.io.database.exception.client.dialect;

import net.luis.utils.io.database.dialect.SqlDialect;
import net.luis.utils.io.database.dialect.SqlFeature;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

/**
 * Thrown when a requested sql feature is not supported by the active dialect.<br>
 *
 * @author Luis-St
 */
public class SqlDialectFeatureException extends SqlDialectException {
	
	/**
	 * Constructs a new dialect feature exception for the given unsupported feature and dialect.<br>
	 *
	 * @param feature The unsupported sql feature
	 * @param dialect The dialect that does not support the feature
	 * @throws NullPointerException If the feature or dialect is null
	 */
	public SqlDialectFeatureException(@NonNull SqlFeature feature, @NonNull SqlDialect dialect) {
		Objects.requireNonNull(feature, "Sql feature must not be null");
		Objects.requireNonNull(dialect, "Sql dialect must not be null");
		
		super("Sql feature '" + feature + "' is not supported by dialect '" + dialect.name() + "'");
	}
}
