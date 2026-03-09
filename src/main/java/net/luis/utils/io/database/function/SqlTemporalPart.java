package net.luis.utils.io.database.function;

/**
 *
 * @author Luis-St
 *
 */

public enum SqlTemporalPart {
	
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
