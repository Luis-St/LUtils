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

package net.luis.utils.io.database.renderer;

import net.luis.utils.io.database.dialect.SqlDialect;
import org.jspecify.annotations.NonNull;

/**
 * Default SQL renderer providing ANSI SQL syntax.<br>
 *
 * @author Luis-St
 */
public class SqlDefaultRenderer implements SqlRenderer {
	
	private final SqlDialect dialect;
	
	/**
	 * Constructs a new default SQL renderer for the given dialect.<br>
	 *
	 * @param dialect The dialect associated with this renderer
	 */
	public SqlDefaultRenderer(@NonNull SqlDialect dialect) {
		this.dialect = dialect;
	}
	
	@Override
	public @NonNull SqlDialect getDialect() {
		return this.dialect;
	}
	
	@Override
	public @NonNull String quoteIdentifier(@NonNull String identifier) {
		return "\"" + identifier + "\"";
	}
	
	@Override
	public @NonNull String nowFunction() {
		return "CURRENT_TIMESTAMP";
	}
	
	@Override
	public @NonNull String uuidFunction() {
		return "GEN_RANDOM_UUID()";
	}
	
	@Override
	public @NonNull String autoIncrementSyntax() {
		return "GENERATED ALWAYS AS IDENTITY";
	}
	
	@Override
	public @NonNull String createTableSuffix() {
		return "";
	}
	
	@Override
	public boolean supportsIfNotExists() {
		return true;
	}
	
	@Override
	public @NonNull String limitOffsetSyntax(int limit, int offset) {
		if (offset > 0) {
			return "LIMIT " + limit + " OFFSET " + offset;
		}
		return "LIMIT " + limit;
	}
	
	@Override
	public @NonNull String upsertSyntax() {
		return "ON CONFLICT";
	}
	
	@Override
	public @NonNull String returningSyntax(@NonNull String columns) {
		return "RETURNING " + columns;
	}
	
	@Override
	public @NonNull String booleanLiteral(boolean value) {
		return value ? "TRUE" : "FALSE";
	}
	
	@Override
	public @NonNull String stringConcatOperator() {
		return "||";
	}
	
	@Override
	public @NonNull String parameterPlaceholder(int index) {
		return "?";
	}
}
