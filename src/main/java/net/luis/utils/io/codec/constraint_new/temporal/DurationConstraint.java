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

package net.luis.utils.io.codec.constraint_new.temporal;

import net.luis.utils.io.codec.constraint_new.SignedConstraint;

/**
 * Constraint interface for Duration types that provides time-based duration validation operations.<br>
 * <p>
 *     This interface extends {@link SignedConstraint} and {@link TimeFieldConstraint} to provide
 *     comprehensive duration validation capabilities.<br>
 *     It inherits sign-based constraints (positive, negative, zero) and time field constraints
 *     (hour, minute, second, millisecond, nanosecond) for validating duration values.
 * </p>
 *
 * @author Luis-St
 *
 * @param <T> The type of the constraint configuration
 * @param <C> The return type of the constraint method (for fluent method chaining)
 */
public interface DurationConstraint<T, C> extends SignedConstraint<T, C>, TimeFieldConstraint<T, C>, TemporalSpanConstraint<T, C> {}
