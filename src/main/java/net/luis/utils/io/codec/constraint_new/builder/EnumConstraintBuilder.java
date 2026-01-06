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

import net.luis.utils.io.codec.constraint_new.BaseConstraint;
import net.luis.utils.io.codec.constraint_new.Constraint;
import org.jspecify.annotations.NonNull;

import java.util.Collection;

/**
 * Builder class for constructing enum-based constraints.<br>
 * <p>
 *     This builder implements {@link BaseConstraint} to provide a fluent API for building
 *     constraints on enumeration values.<br>
 *     It is typically used as a parameter to constraint builder methods that accept enum constraints.
 * </p>
 *
 * @author Luis-St
 *
 * @param <T> The enum type being constrained
 */
public class EnumConstraintBuilder<T extends Enum<T>> implements BaseConstraint<T, EnumConstraintBuilder<T>> {
	
	@Override
	public @NonNull EnumConstraintBuilder<T> equalTo(@NonNull T value) {
		return null;
	}
	
	@Override
	public @NonNull EnumConstraintBuilder<T> notEqualTo(@NonNull T value) {
		return null;
	}
	
	@Override
	public @NonNull EnumConstraintBuilder<T> in(@NonNull Collection<T> values) {
		return null;
	}
	
	@Override
	public @NonNull EnumConstraintBuilder<T> notIn(@NonNull Collection<T> values) {
		return null;
	}
	
	@Override
	public @NonNull EnumConstraintBuilder<T> custom(@NonNull Constraint<T> constraint) {
		return null;
	}
}
