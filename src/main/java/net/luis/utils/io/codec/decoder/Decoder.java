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

package net.luis.utils.io.codec.decoder;

import net.luis.utils.io.codec.provider.TypeProvider;
import net.luis.utils.util.Result;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 *
 * @author Luis-St
 *
 */

public interface Decoder<C> {
	
	default <R> @NotNull C decode(@NotNull TypeProvider<R> provider, @Nullable R value) {
		return this.decodeStart(provider, value).orThrow();
	}
	
	<R> @NotNull Result<C> decodeStart(@NotNull TypeProvider<R> provider, @Nullable R value);
}
