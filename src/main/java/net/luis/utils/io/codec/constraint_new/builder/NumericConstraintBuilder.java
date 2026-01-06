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

package net.luis.utils.io.codec.constraint_new.builder;

import net.luis.utils.io.codec.constraint_new.ComparableConstraint;
import net.luis.utils.io.codec.constraint_new.Constraint;
import org.jspecify.annotations.NonNull;

import java.util.Collection;

/**
 * Builder class for constructing numeric constraints.<br>
 * <p>
 *     This builder implements {@link ComparableConstraint} to provide a fluent API for building
 *     integer-based numeric constraints.<br>
 *     It is typically used as a parameter to constraint builder methods that accept numeric constraints.
 * </p>
 *
 * @author Luis-St
 *
 */

public class NumericConstraintBuilder implements ComparableConstraint<Integer, NumericConstraintBuilder> {
	
	@Override
	public @NonNull NumericConstraintBuilder equalTo(@NonNull Integer value) {
		return null;
	}
	
	@Override
	public @NonNull NumericConstraintBuilder notEqualTo(@NonNull Integer value) {
		return null;
	}
	
	@Override
	public @NonNull NumericConstraintBuilder in(@NonNull Collection<Integer> values) {
		return null;
	}
	
	@Override
	public @NonNull NumericConstraintBuilder notIn(@NonNull Collection<Integer> values) {
		return null;
	}
	
	@Override
	public @NonNull NumericConstraintBuilder custom(@NonNull Constraint<Integer> constraint) {
		return null;
	}
	
	@Override
	public @NonNull NumericConstraintBuilder greaterThan(@NonNull Integer value) {
		return null;
	}
	
	@Override
	public @NonNull NumericConstraintBuilder greaterThanOrEqual(@NonNull Integer value) {
		return null;
	}
	
	@Override
	public @NonNull NumericConstraintBuilder lessThan(@NonNull Integer value) {
		return null;
	}
	
	@Override
	public @NonNull NumericConstraintBuilder lessThanOrEqual(@NonNull Integer value) {
		return null;
	}
	
	@Override
	public @NonNull NumericConstraintBuilder between(@NonNull Integer min, @NonNull Integer max) {
		return null;
	}
	
	@Override
	public @NonNull NumericConstraintBuilder betweenOrEqual(@NonNull Integer min, @NonNull Integer max) {
		return null;
	}
}
