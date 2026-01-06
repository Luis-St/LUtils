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

import net.luis.utils.io.codec.constraint_new.Constraint;
import net.luis.utils.io.codec.constraint_new.SizeConstraint;
import org.jspecify.annotations.NonNull;

import java.util.Collection;

/**
 * Builder class for constructing size-based constraints.<br>
 * <p>
 *     This builder implements {@link SizeConstraint} to provide a fluent API for building
 *     constraints on collection sizes.<br>
 *     It is typically used as a parameter to constraint builder methods that accept size constraints.
 * </p>
 *
 * @author Luis-St
 */
public class SizeConstraintBuilder implements SizeConstraint<Integer, SizeConstraintBuilder> {
	
	@Override
	public @NonNull SizeConstraintBuilder equalTo(@NonNull Integer value) {
		return null;
	}
	
	@Override
	public @NonNull SizeConstraintBuilder notEqualTo(@NonNull Integer value) {
		return null;
	}
	
	@Override
	public @NonNull SizeConstraintBuilder in(@NonNull Collection<Integer> values) {
		return null;
	}
	
	@Override
	public @NonNull SizeConstraintBuilder notIn(@NonNull Collection<Integer> values) {
		return null;
	}
	
	@Override
	public @NonNull SizeConstraintBuilder custom(@NonNull Constraint<Integer> constraint) {
		return null;
	}
	
	@Override
	public @NonNull SizeConstraintBuilder minSize(int minSize) {
		return null;
	}
	
	@Override
	public @NonNull SizeConstraintBuilder maxSize(int maxSize) {
		return null;
	}
	
	@Override
	public @NonNull SizeConstraintBuilder exactSize(int exactSize) {
		return null;
	}
	
	@Override
	public @NonNull SizeConstraintBuilder sizeBetween(int minSize, int maxSize) {
		return null;
	}
}
