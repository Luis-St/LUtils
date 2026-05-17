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

package net.luis.utils.io.database.dialect.sqlserver;

import net.luis.utils.io.database.dialect.SqlDialect;
import net.luis.utils.io.database.dialect.base.condition.SqlStringConditionRenderer;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.rendering.SqlRendered;
import net.luis.utils.io.database.rendering.SqlRenderer;
import org.jspecify.annotations.NonNull;

/**
 *
 * @author Luis-St
 *
 */

public class SqlServerStringConditionRenderer extends SqlStringConditionRenderer {
	
	public SqlServerStringConditionRenderer(@NonNull SqlDialect dialect) {
		super(dialect);
	}
	
	@Override
	protected @NonNull SqlRendered renderConcatExpression(@NonNull SqlRenderer renderer, @NonNull SqlRendered left, @NonNull SqlRendered right) throws SqlException {
		return renderer.rendered(left).literal("+").rendered(right).toSql();
	}
}
