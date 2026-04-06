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

import net.luis.utils.io.database.exception.dialect.SqlDialectUnsupportedFeatureException;
import net.luis.utils.io.database.rendering.SimpleSqlRendered;
import net.luis.utils.io.database.rendering.SqlRendered;
import net.luis.utils.io.database.table.SqlColumn;
import org.jspecify.annotations.NonNull;

import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * @author Luis-St
 *
 */

public class MariaDbDialect extends MySqlDialect {
	
	private static final Set<SqlFeature> SUPPORTED_FEATURES = Set.of(
		SqlFeature.CTE,
		SqlFeature.RECURSIVE_CTE,
		SqlFeature.RETURNING,
		SqlFeature.WINDOW_FUNCTIONS,
		SqlFeature.FOR_UPDATE,
		SqlFeature.FOR_SHARE,
		SqlFeature.SKIP_LOCKED,
		SqlFeature.NO_WAIT
	);
	
	@Override
	public @NonNull String name() {
		return "MariaDB";
	}
	
	@Override
	public boolean isFeatureSupported(@NonNull SqlFeature feature) {
		Objects.requireNonNull(feature, "Feature must not be null");
		return SUPPORTED_FEATURES.contains(feature);
	}
	
	@Override
	public @NonNull SqlRendered renderReturning(@NonNull List<SqlColumn<?, ?>> columns) throws SqlDialectUnsupportedFeatureException {
		Objects.requireNonNull(columns, "Columns must not be null");
		if (columns.isEmpty()) {
			return SimpleSqlRendered.of("RETURNING *");
		}
		
		String cols = columns.stream().map(c -> this.quoteIdentifier(c.getName())).collect(Collectors.joining(", "));
		return SimpleSqlRendered.of("RETURNING " + cols);
	}
}
