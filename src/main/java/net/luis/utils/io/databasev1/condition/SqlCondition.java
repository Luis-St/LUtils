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

package net.luis.utils.io.databasev1.condition;

import net.luis.utils.io.databasev1.SqlRenderable;
import org.jspecify.annotations.NonNull;

/**
 * Interface representing a SQL condition.<br>
 *
 * @author Luis-St
 */
public interface SqlCondition extends SqlRenderable {
	
	/**
	 * Returns a condition that is always true.<br>
	 * Acts as the identity element for {@code AND} operations.<br>
	 * Generates SQL: {@code TRUE} or dialect equivalent.<br>
	 *
	 * @return A condition that always evaluates to true
	 */
	static @NonNull SqlCondition always() {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Returns a condition that is always false.<br>
	 * Acts as the identity element for {@code OR} operations.<br>
	 * Generates SQL: {@code FALSE} or dialect equivalent.<br>
	 *
	 * @return A condition that always evaluates to false
	 */
	static @NonNull SqlCondition none() {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Creates a combined condition using {@code AND}.<br>
	 * Generates SQL: {@code cond1 AND cond2 AND ...}.<br>
	 *
	 * @param first The first condition
	 * @param rest The remaining conditions to combine
	 * @return The combined condition
	 */
	static @NonNull SqlCondition allOf(@NonNull SqlCondition first, SqlCondition @NonNull ... rest) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Creates a combined condition using {@code OR}.<br>
	 * Generates SQL: {@code cond1 OR cond2 OR ...}.<br>
	 *
	 * @param first The first condition
	 * @param rest The remaining conditions to combine
	 * @return The combined condition
	 */
	static @NonNull SqlCondition anyOf(@NonNull SqlCondition first, SqlCondition @NonNull ... rest) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Combines this condition with the given conditions using {@code AND}.<br>
	 * Generates SQL: {@code this AND other1 AND other2 AND ...}.<br>
	 *
	 * @param others The conditions to combine with
	 * @return The combined condition
	 */
	@NonNull SqlCondition and(SqlCondition @NonNull ... others);
	
	/**
	 * Combines this condition with the given conditions using {@code OR}.<br>
	 * Generates SQL: {@code this OR other1 OR other2 OR ...}.<br>
	 *
	 * @param others The conditions to combine with
	 * @return The combined condition
	 */
	@NonNull SqlCondition or(SqlCondition @NonNull ... others);
	
	/**
	 * Negates this condition.<br>
	 * Generates SQL: {@code NOT (condition)}.<br>
	 *
	 * @return The negated condition
	 */
	@NonNull SqlCondition not();
}
