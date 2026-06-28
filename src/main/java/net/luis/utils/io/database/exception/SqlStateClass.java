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

package net.luis.utils.io.database.exception;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

/**
 * The standard {@code SQLState} classes as defined by the SQL standard (and common vendor extensions).<br>
 * <p>
 *     A {@code SQLState} value is a five character string whose first two characters denote the class,<br>
 *     while the remaining three characters denote the subclass.
 * </p>
 * <p>
 *     This enum models the leading two character class,<br>
 *     allowing callers to categorize a failure without matching on vendor specific full state strings.
 * </p>
 *
 * @author Luis-St
 */
public enum SqlStateClass {
	
	/**
	 * Connection exception ({@code 08}).<br>
	 */
	CONNECTION("08"),
	/**
	 * Feature not supported ({@code 0A}).<br>
	 */
	FEATURE_NOT_SUPPORTED("0A"),
	/**
	 * Cardinality violation ({@code 21}).<br>
	 */
	CARDINALITY_VIOLATION("21"),
	/**
	 * Data exception ({@code 22}).<br>
	 */
	DATA("22"),
	/**
	 * Integrity constraint violation ({@code 23}).<br>
	 */
	INTEGRITY_CONSTRAINT("23"),
	/**
	 * Invalid transaction state ({@code 25}).<br>
	 */
	INVALID_TRANSACTION_STATE("25"),
	/**
	 * Invalid authorization specification ({@code 28}).<br>
	 */
	AUTHORIZATION("28"),
	/**
	 * Savepoint exception ({@code 3B}).<br>
	 */
	SAVEPOINT("3B"),
	/**
	 * Transaction rollback ({@code 40}).<br>
	 */
	TRANSACTION_ROLLBACK("40"),
	/**
	 * Syntax error or access rule violation ({@code 42}).<br>
	 */
	SYNTAX_OR_ACCESS("42"),
	/**
	 * Insufficient resources ({@code 53}).<br>
	 */
	INSUFFICIENT_RESOURCES("53"),
	/**
	 * Operator intervention ({@code 57}).<br>
	 */
	OPERATOR_INTERVENTION("57"),
	/**
	 * System error ({@code 58}).<br>
	 */
	SYSTEM_ERROR("58");
	
	/**
	 * The two character {@code SQLState} class code this constant represents.<br>
	 */
	private final String code;
	
	/**
	 * Constructs a new sql state class with the given two character code.<br>
	 * @param code The two character class code
	 */
	SqlStateClass(String code) {
		this.code = code;
	}
	
	/**
	 * Parses the class of the given {@code SQLState} string.<br>
	 * Only the leading two characters are considered, the subclass is ignored.<br>
	 *
	 * @param sqlState The {@code SQLState} string to parse, may be null
	 * @return An optional holding the matching class, or an empty optional if the state is null, shorter than two characters or does not match any known class
	 */
	public static @NonNull Optional<SqlStateClass> fromState(@Nullable String sqlState) {
		if (sqlState == null || sqlState.length() < 2) {
			return Optional.empty();
		}
		
		String prefix = sqlState.substring(0, 2);
		for (SqlStateClass value : values()) {
			if (value.code.equalsIgnoreCase(prefix)) {
				return Optional.of(value);
			}
		}
		return Optional.empty();
	}
	
	/**
	 * Returns the two character {@code SQLState} class code of this constant.<br>
	 * @return The class code
	 */
	public @NonNull String getCode() {
		return this.code;
	}
}
