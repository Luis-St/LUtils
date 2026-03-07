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

package net.luis.utils.io.databasev1.function.scalar;

/**
 * Enum representing a date part for SQL date functions.<br>
 *
 * @author Luis-St
 */
public enum DatePart {
	
	/**
	 * The date part to extract from a date or time value.<br>
	 */
	YEAR,
	/**
	 * The month part to extract from a date or time value.<br>
	 */
	MONTH,
	/**
	 * The day part to extract from a date or time value.<br>
	 */
	DAY,
	/**
	 * The hour part to extract from a date or time value.<br>
	 */
	HOUR,
	/**
	 * The minute part to extract from a date or time value.<br>
	 */
	MINUTE,
	/**
	 * The second part to extract from a date or time value.<br>
	 */
	SECOND,
	/**
	 * The quarter part to extract from a date or time value.<br>
	 */
	QUARTER,
	/**
	 * The week part to extract from a date or time value.<br>
	 */
	WEEK,
	/**
	 * The day of the week part to extract from a date or time value.<br>
	 */
	DAY_OF_WEEK,
	/**
	 * The day of the year part to extract from a date or time value.<br>
	 */
	DAY_OF_YEAR
}
