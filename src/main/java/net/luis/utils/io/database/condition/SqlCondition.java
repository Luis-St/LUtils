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

import com.google.common.collect.Lists;
import net.luis.utils.io.database.dialect.SqlDialect;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.rendering.SqlRenderable;
import net.luis.utils.io.database.rendering.SqlRendered;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Objects;

/**
 *
 * @author Luis-St
 *
 */

public interface SqlCondition extends SqlRenderable {
	
	static @NonNull SqlCondition always() {
		return new SqlAlwaysCondition();
	}
	
	static @NonNull SqlCondition never() {
		return new SqlNeverCondition();
	}
	
	@SuppressWarnings("DuplicatedCode")
	static @NonNull SqlCondition allOf(@NonNull SqlCondition first, @NonNull SqlCondition second, SqlCondition @NonNull ... others) {
		Objects.requireNonNull(first, "First sql condition must not be null");
		Objects.requireNonNull(second, "Second sql condition must not be null");
		Objects.requireNonNull(others, "Other sql conditions must not be null");
		
		List<SqlCondition> conditions = Lists.newArrayListWithCapacity(2 + others.length);
		conditions.add(first);
		conditions.add(second);
		for (SqlCondition condition : others) {
			Objects.requireNonNull(condition, "Other sql condition must not be null");
			conditions.add(condition);
		}
		return new SqlAllOfCondition(conditions);
	}
	
	static @NonNull SqlCondition allOf(@NonNull List<SqlCondition> conditions) {
		if (conditions.size() == 1) {
			return conditions.getFirst();
		}
		return new SqlAllOfCondition(conditions);
	}
	
	@SuppressWarnings("DuplicatedCode")
	static @NonNull SqlCondition anyOf(@NonNull SqlCondition first, @NonNull SqlCondition second, SqlCondition @NonNull ... others) {
		Objects.requireNonNull(first, "First sql condition must not be null");
		Objects.requireNonNull(second, "Second sql condition must not be null");
		Objects.requireNonNull(others, "Other sql conditions must not be null");
		
		List<SqlCondition> conditions = Lists.newArrayListWithCapacity(2 + others.length);
		conditions.add(first);
		conditions.add(second);
		for (SqlCondition condition : others) {
			Objects.requireNonNull(condition, "Other sql condition must not be null");
			conditions.add(condition);
		}
		return new SqlAnyOfCondition(conditions);
	}
	
	static @NonNull SqlCondition anyOf(@NonNull List<SqlCondition> conditions) {
		if (conditions.size() == 1) {
			return conditions.getFirst();
		}
		return new SqlAnyOfCondition(conditions);
	}
	
	default @NonNull SqlCondition and(@NonNull SqlCondition other) {
		return allOf(this, other);
	}
	
	default @NonNull SqlCondition or(@NonNull SqlCondition other) {
		return anyOf(this, other);
	}
	
	default @NonNull SqlCondition not() {
		return new SqlNegatedCondition(this);
	}
	
	@Override
	default @NonNull SqlRendered toSql(@NonNull SqlDialect dialect) throws SqlException {
		Objects.requireNonNull(dialect, "Sql dialect must not be null");
		return dialect.renderCondition(this);
	}
}
