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

import net.luis.utils.util.Result;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Function;

/**
 *
 * @author Luis-St
 *
 */

@FunctionalInterface
public interface ResultFunction<T, R> extends Function<Result<T>, Result<R>> {
	
	static <T, R> @NotNull ResultFunction<T, R> direct(@NotNull Function<T, R> function) {
		Objects.requireNonNull(function, "Function must not be null");
		return result -> result.map(function);
	}
	
	@Override
	@NotNull Result<R> apply(@NotNull Result<T> input);
}
