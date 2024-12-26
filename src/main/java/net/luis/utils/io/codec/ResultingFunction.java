/*
 * LUtils
 * Copyright (C) 2024 Luis Staudt
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

package net.luis.utils.io.codec;

import net.luis.utils.function.throwable.ThrowableFunction;
import net.luis.utils.util.Result;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.util.Objects;
import java.util.function.Function;

/**
 *
 * @author Luis-St
 *
 */

@FunctionalInterface
public interface ResultingFunction<T, R> extends Function<T, Result<R>> {
	
	static <T, R> @NotNull ResultingFunction<T, R> direct(@NotNull Function<T, R> function) {
		Objects.requireNonNull(function, "Function must not be null");
		return value -> Result.success(function.apply(value));
	}
	
	static <T, R> @NotNull ResultingFunction<T, R> throwable(@NotNull ThrowableFunction<T, R, ? extends Throwable> function) {
		Objects.requireNonNull(function, "Function must not be null");
		return value -> {
			try {
				return Result.success(function.apply(value));
			} catch (Throwable throwable) {
				return Result.error(throwable.getMessage());
			}
		};
	}
	
	@Override
	@NotNull Result<R> apply(@UnknownNullability T value);
}
