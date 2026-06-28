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
 * Represents a boolean condition that can be rendered into a sql predicate.<br>
 * Conditions can be combined using {@link #and(SqlCondition)}, {@link #or(SqlCondition)} and negated using {@link #not()}.<br>
 *
 * @author Luis-St
 */
public interface SqlCondition extends SqlRenderable {
	
	/**
	 * Creates a condition that always evaluates to {@code true}.<br>
	 * @return A condition that is always true
	 */
	static @NonNull SqlCondition always() {
		return new SqlAlwaysCondition();
	}
	
	/**
	 * Creates a condition that always evaluates to {@code false}.<br>
	 * @return A condition that is always false
	 */
	static @NonNull SqlCondition never() {
		return new SqlNeverCondition();
	}
	
	/**
	 * Creates a condition that is the logical conjunction of all the given conditions.<br>
	 *
	 * @param first The first condition
	 * @param second The second condition
	 * @param others The additional conditions
	 * @return A condition that is true only if all the given conditions are true
	 * @throws NullPointerException If any of the conditions is null
	 */
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
	
	/**
	 * Creates a condition that is the logical conjunction of all the given conditions.<br>
	 * If the list contains a single condition, that condition is returned directly.<br>
	 *
	 * @param conditions The conditions to combine
	 * @return A condition that is true only if all the given conditions are true
	 * @throws NullPointerException If the conditions list is null
	 * @throws IllegalArgumentException If the conditions list is empty or contains null conditions
	 */
	static @NonNull SqlCondition allOf(@NonNull List<SqlCondition> conditions) {
		if (conditions.size() == 1) {
			return conditions.getFirst();
		}
		return new SqlAllOfCondition(conditions);
	}
	
	/**
	 * Creates a condition that is the logical disjunction of all the given conditions.<br>
	 *
	 * @param first The first condition
	 * @param second The second condition
	 * @param others The additional conditions
	 * @return A condition that is true if any of the given conditions is true
	 * @throws NullPointerException If any of the conditions is null
	 */
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
	
	/**
	 * Creates a condition that is the logical disjunction of all the given conditions.<br>
	 * If the list contains a single condition, that condition is returned directly.<br>
	 *
	 * @param conditions The conditions to combine
	 * @return A condition that is true if any of the given conditions is true
	 * @throws NullPointerException If the conditions list is null
	 * @throws IllegalArgumentException If the conditions list is empty or contains null conditions
	 */
	static @NonNull SqlCondition anyOf(@NonNull List<SqlCondition> conditions) {
		if (conditions.size() == 1) {
			return conditions.getFirst();
		}
		return new SqlAnyOfCondition(conditions);
	}
	
	/**
	 * Combines this condition with the given condition using a logical conjunction.<br>
	 *
	 * @param other The condition to combine with this condition
	 * @return A condition that is true only if both conditions are true
	 * @throws NullPointerException If the other condition is null
	 */
	default @NonNull SqlCondition and(@NonNull SqlCondition other) {
		return allOf(this, other);
	}
	
	/**
	 * Combines this condition with the given condition using a logical disjunction.<br>
	 *
	 * @param other The condition to combine with this condition
	 * @return A condition that is true if either condition is true
	 * @throws NullPointerException If the other condition is null
	 */
	default @NonNull SqlCondition or(@NonNull SqlCondition other) {
		return anyOf(this, other);
	}
	
	/**
	 * Negates this condition.<br>
	 * @return A condition that is true if this condition is false
	 */
	default @NonNull SqlCondition not() {
		return new SqlNegatedCondition(this);
	}
	
	@Override
	default @NonNull SqlRendered toSql(@NonNull SqlDialect dialect) throws SqlException {
		Objects.requireNonNull(dialect, "Sql dialect must not be null");
		return dialect.renderCondition(this);
	}
}
