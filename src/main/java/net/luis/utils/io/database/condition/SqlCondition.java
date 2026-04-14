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

package net.luis.utils.io.database.condition;

import net.luis.utils.io.database.dialect.SqlDialect;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.rendering.SqlRenderable;
import net.luis.utils.io.database.rendering.SqlRendered;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

/**
 *
 * @author Luis-St
 *
 */

@FunctionalInterface
public interface SqlCondition extends SqlRenderable {
	
	static @NonNull SqlCondition always() {
		return null;
	}
	
	static @NonNull SqlCondition never() {
		return null;
	}
	
	static @NonNull SqlCondition allOf(@NonNull SqlCondition first, @NonNull SqlCondition second, SqlCondition @NonNull ... others) {
		return null;
	}
	
	static @NonNull SqlCondition anyOf(@NonNull SqlCondition first, @NonNull SqlCondition second, SqlCondition @NonNull ... others) {
		return null;
	}
	
	default @NonNull SqlCondition and(@NonNull SqlCondition other) {
		return allOf(this, other);
	}
	
	default @NonNull SqlCondition or(@NonNull SqlCondition other) {
		return anyOf(this, other);
	}
	
	@NonNull SqlCondition not();
	
	@Override
	default @NonNull SqlRendered toSql(@NonNull SqlDialect dialect) throws SqlException {
		Objects.requireNonNull(dialect, "Sql dialect must not be null");
		return dialect.renderCondition(this);
	}
}
