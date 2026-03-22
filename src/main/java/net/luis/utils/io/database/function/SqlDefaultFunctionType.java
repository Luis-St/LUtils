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

package net.luis.utils.io.database.function;

/**
 *
 * @author Luis-St
 *
 */

public enum SqlDefaultFunctionType implements SqlFunctionType {
	
	// SqlFunctions
	EQUAL_TO,
	IN_LIST,
	IN_SUBQUERY,
	IS_DISTINCT_FROM,
	IS_NULL,
	COUNT,
	COUNT_DISTINCT,
	CAST,
	COALESCE,
	NULLIF,
	CASE_WHEN,
	UNSAFE,
	ROW_NUMBER,
	RANK,
	DENSE_RANK,
	TILE_BUCKET,
	LAG,
	LEAD,
	PERCENT_RANK,
	CUMULATIVE_DISTRIBUTION,
	FIRST_VALUE,
	LAST_VALUE,
	VALUE_AT,
	
	// SqlOrderableFunctions
	GREATER_THAN,
	GREATER_THAN_OR_EQUAL_TO,
	LESS_THAN,
	LESS_THAN_OR_EQUAL_TO,
	BETWEEN,
	MIN,
	MAX,
	GREATEST,
	LEAST,
	
	// SqlNumericFunctions
	IS_POSITIVE,
	IS_NEGATIVE,
	IS_ZERO,
	MOD_EQUALS,
	RANDOM,
	PI,
	NEGATE,
	SUM,
	AVERAGE,
	ABS,
	ROUND,
	CEIL,
	FLOOR,
	TRUNCATE,
	MOD,
	POW,
	SQRT,
	SIGN,
	EXP,
	LOG2,
	LN,
	LOG10,
	SIN,
	COS,
	TAN,
	ASIN,
	ACOS,
	ATAN,
	ATAN2,
	RADIANS,
	DEGREES,
	BITWISE_AND,
	BITWISE_OR,
	BITWISE_XOR,
	BITWISE_NOT,
	
	// SqlStringFunctions
	STARTS_WITH,
	CONTAINS,
	ENDS_WITH,
	LIKE,
	EQUALS_IGNORE_CASE,
	LOWER,
	UPPER,
	TRIM,
	LEFT_TRIM,
	RIGHT_TRIM,
	TRIM_CHARS,
	LENGTH,
	SUBSTRING,
	CONCAT,
	CONCAT_WITH_SEPARATOR,
	CONCAT_DISTINCT_WITH_SEPARATOR,
	CONCAT_ORDERED_WITH_SEPARATOR,
	REPLACE,
	POSITION,
	LEFT,
	RIGHT,
	LEFT_PAD,
	RIGHT_PAD,
	HEX,
	UNHEX,
	
	// SqlTemporalFunctions
	WITHIN_LAST,
	WITHIN_NEXT,
	BEFORE,
	AFTER,
	NOW,
	CURRENT_DATE,
	CURRENT_TIME,
	CURRENT_TIMESTAMP,
	FROM_EPOCH,
	MAKE_DATE,
	MAKE_TIME,
	EXTRACT,
	TRUNCATE_TEMPORAL,
	ADD_TEMPORAL,
	SUBTRACT_TEMPORAL,
	TO_EPOCH,
	TO_DATE,
	TO_TIME,
}
