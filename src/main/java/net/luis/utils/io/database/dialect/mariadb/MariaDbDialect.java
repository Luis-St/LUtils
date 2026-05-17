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

package net.luis.utils.io.database.dialect.mariadb;

import net.luis.utils.io.database.dialect.SqlFeature;
import net.luis.utils.io.database.dialect.mysql.MySqlDialect;
import net.luis.utils.io.database.dialect.base.SqlRenderingHelper;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.rendering.SqlRendered;
import net.luis.utils.io.database.rendering.SqlRenderer;
import net.luis.utils.io.database.table.SqlColumn;
import org.jspecify.annotations.NonNull;

import java.util.*;

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
		Objects.requireNonNull(feature, "Sql feature must not be null");
		return SUPPORTED_FEATURES.contains(feature);
	}
	
	@Override
	@SuppressWarnings("DuplicatedCode")
	public @NonNull SqlRendered renderReturning(@NonNull List<SqlColumn<?, ?>> columns) throws SqlException {
		Objects.requireNonNull(columns, "Sql columns must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.returning();
		if (columns.isEmpty()) {
			renderer.literal("*");
		} else {
			SqlRenderingHelper.renderList(renderer, columns, (r, column) -> r.literal(this.quoteIdentifier(column.getName())));
		}
		return renderer.toSql();
	}
}
