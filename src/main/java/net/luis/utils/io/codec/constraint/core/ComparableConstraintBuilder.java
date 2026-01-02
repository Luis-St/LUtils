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

package net.luis.utils.io.codec.constraint.core;

import net.luis.utils.io.codec.constraint.config.temporal.core.FieldConstraintConfig;
import org.jspecify.annotations.NonNull;

/**
 * A builder for creating constraints on comparable field values.<br>
 * <p>
 *     This builder allows defining constraints for individual components of temporal types,
 *     such as hour, minute, second, millisecond, day of month, and year.<br>
 *     Constraints can be chained to create complex field validations.
 * </p>
 * <p>
 *     Example usage:
 * </p>
 * <pre>{@code
 * // Constrain hour to be between 9 and 17, but not 12 (lunch break)
 * codec.hour(c -> c.between(9, 17).notEqualTo(12))
 *
 * // Constrain millisecond to be exactly 500
 * codec.millisecond(c -> c.equalTo(500))
 *
 * // Constrain day of month to be in the first half
 * codec.dayOfMonth(c -> c.betweenOrEqual(1, 15))
 * }</pre>
 *
 * @author Luis-St
 */
public class ComparableConstraintBuilder {
	
	/**
	 * The current configuration being built.<br>
	 */
	private FieldConstraintConfig config;
	
	/**
	 * Constructs a new comparable constraint builder with no constraints.<br>
	 */
	public ComparableConstraintBuilder() {
		this.config = FieldConstraintConfig.UNCONSTRAINED;
	}
	
	/**
	 * Applies a "greater than" constraint (exclusive).<br>
	 * <p>
	 *     The field value must be strictly greater than the specified value.<br>
	 *     For example, {@code greaterThan(10)} requires values > 10.
	 * </p>
	 *
	 * @param value The minimum value (exclusive)
	 * @return This builder for method chaining
	 */
	public @NonNull ComparableConstraintBuilder greaterThan(int value) {
		this.config = this.config.withMin(value, false);
		return this;
	}
	
	/**
	 * Applies a "greater than or equal" constraint (inclusive).<br>
	 * <p>
	 *     The field value must be greater than or equal to the specified value.<br>
	 *     For example, {@code greaterThanOrEqual(10)} requires values >= 10.
	 * </p>
	 *
	 * @param value The minimum value (inclusive)
	 * @return This builder for method chaining
	 */
	public @NonNull ComparableConstraintBuilder greaterThanOrEqual(int value) {
		this.config = this.config.withMin(value, true);
		return this;
	}
	
	/**
	 * Applies a "less than" constraint (exclusive).<br>
	 * <p>
	 *     The field value must be strictly less than the specified value.<br>
	 *     For example, {@code lessThan(20)} requires values < 20.
	 * </p>
	 *
	 * @param value The maximum value (exclusive)
	 * @return This builder for method chaining
	 */
	public @NonNull ComparableConstraintBuilder lessThan(int value) {
		this.config = this.config.withMax(value, false);
		return this;
	}
	
	/**
	 * Applies a "less than or equal" constraint (inclusive).<br>
	 * <p>
	 *     The field value must be less than or equal to the specified value.<br>
	 *     For example, {@code lessThanOrEqual(20)} requires values <= 20.
	 * </p>
	 *
	 * @param value The maximum value (inclusive)
	 * @return This builder for method chaining
	 */
	public @NonNull ComparableConstraintBuilder lessThanOrEqual(int value) {
		this.config = this.config.withMax(value, true);
		return this;
	}
	
	/**
	 * Applies a range constraint (exclusive bounds).<br>
	 * <p>
	 *     The field value must be strictly within the specified range.<br>
	 *     For example, {@code between(9, 17)} requires 9 < value < 17.
	 * </p>
	 *
	 * @param min The minimum value (exclusive)
	 * @param max The maximum value (exclusive)
	 * @return This builder for method chaining
	 * @throws IllegalArgumentException If the min value is greater than the max value
	 */
	public @NonNull ComparableConstraintBuilder between(int min, int max) {
		this.config = this.config.withRange(min, max, false);
		return this;
	}
	
	/**
	 * Applies a range constraint (inclusive bounds).<br>
	 * <p>
	 *     The field value must be within the specified range, including boundaries.<br>
	 *     For example, {@code betweenOrEqual(9, 17)} requires 9 <= value <= 17.
	 * </p>
	 *
	 * @param min The minimum value (inclusive)
	 * @param max The maximum value (inclusive)
	 * @return This builder for method chaining
	 * @throws IllegalArgumentException If min > max
	 */
	public @NonNull ComparableConstraintBuilder betweenOrEqual(int min, int max) {
		this.config = this.config.withRange(min, max, true);
		return this;
	}
	
	/**
	 * Applies an equality constraint.<br>
	 * <p>
	 *     The field value must be exactly equal to the specified value.<br>
	 *     For example, {@code equalTo(12)} requires value == 12.
	 * </p>
	 *
	 * @param value The exact value required
	 * @return This builder for method chaining
	 */
	public @NonNull ComparableConstraintBuilder equalTo(int value) {
		this.config = this.config.withEquals(value, false);
		return this;
	}
	
	/**
	 * Applies a non-equality constraint.<br>
	 * <p>
	 *     The field value must not be equal to the specified value.<br>
	 *     For example, {@code notEqualTo(13)} requires value != 13.
	 * </p>
	 *
	 * @param value The value to exclude
	 * @return This builder for method chaining
	 */
	public @NonNull ComparableConstraintBuilder notEqualTo(int value) {
		this.config = this.config.withEquals(value, true);
		return this;
	}
	
	/**
	 * Builds and returns the field constraint configuration.<br>
	 *
	 * @return The built field constraint configuration
	 */
	public @NonNull FieldConstraintConfig build() {
		return this.config;
	}
}
