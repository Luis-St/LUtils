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

/**
 *
 * @author Luis-St
 *
 */

public enum SqlDefaultConditionType implements SqlConditionType {
	
	// Comparison
	BETWEEN,
	EQUAL_TO,
	GREATER_THAN,
	GREATER_THAN_OR_EQUAL_TO,
	IN_LIST,
	IN_SUBQUERY,
	IS_DISTINCT_FROM,
	IS_NULL,
	LESS_THAN,
	LESS_THAN_OR_EQUAL_TO,
	
	// Numeric
	IS_NEGATIVE,
	IS_POSITIVE,
	IS_ZERO,
	MOD_EQUALS,
	
	// String
	CONTAINS,
	ENDS_WITH,
	EQUALS_IGNORE_CASE,
	LIKE,
	STARTS_WITH,
	
	// Temporal
	AFTER,
	BEFORE,
	WITHIN_LAST,
	WITHIN_NEXT,
}
