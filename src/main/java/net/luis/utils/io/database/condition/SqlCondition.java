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

import net.luis.utils.io.database.SqlRenderable;
import org.jspecify.annotations.NonNull;

/**
 * Interface representing a SQL condition.<br>
 *
 * @author Luis-St
 */
public interface SqlCondition extends SqlRenderable {
	
	/**
	 * Creates a combined condition using {@code AND}.<br>
	 * Generates SQL: {@code cond1 AND cond2 AND ...}.<br>
	 *
	 * @param conditions The conditions to combine
	 * @return The combined condition
	 */
	static @NonNull SqlCondition and(SqlCondition @NonNull ... conditions) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Creates a combined condition using {@code OR}.<br>
	 * Generates SQL: {@code cond1 OR cond2 OR ...}.<br>
	 *
	 * @param conditions The conditions to combine
	 * @return The combined condition
	 */
	static @NonNull SqlCondition or(SqlCondition @NonNull ... conditions) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Combines this condition with the given conditions using {@code AND}.<br>
	 * Generates SQL: {@code this AND first AND ...}.<br>
	 *
	 * @param first The first condition to combine with
	 * @param rest The remaining conditions to combine with
	 * @return The combined condition
	 */
	@NonNull SqlCondition and(@NonNull SqlCondition first, SqlCondition @NonNull ... rest);
	
	/**
	 * Combines this condition with the given conditions using {@code OR}.<br>
	 * Generates SQL: {@code this OR first OR ...}.<br>
	 *
	 * @param first The first condition to combine with
	 * @param rest The remaining conditions to combine with
	 * @return The combined condition
	 */
	@NonNull SqlCondition or(@NonNull SqlCondition first, SqlCondition @NonNull ... rest);
	
	/**
	 * Negates this condition.<br>
	 * Generates SQL: {@code NOT (condition)}.<br>
	 *
	 * @return The negated condition
	 */
	@NonNull SqlCondition not();
}
