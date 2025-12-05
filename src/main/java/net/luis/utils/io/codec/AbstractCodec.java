/*
 * LUtils
 * Copyright (C) 2025 Luis Staudt
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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 *
 * @author Luis-St
 *
 * @param <C>
 * @param <T>
 */
public abstract non-sealed class AbstractCodec<C, T> implements Codec<C> {
	
	protected final @Nullable T config;
	
	protected AbstractCodec() {
		this.config = null;
	}
	
	protected AbstractCodec(@NotNull T config) {
		this.config = Objects.requireNonNull(config, "Config must not be null");
	}
	
}
