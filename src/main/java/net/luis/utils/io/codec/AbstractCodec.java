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

package net.luis.utils.io.codec;

/**
 * Abstract non-sealed implementation of the {@link Codec} interface.<br>
 * Used by all default codecs in the {@link net.luis.utils.io.codec.types} package.<br>
 *
 * @author Luis-St
 *
 * @param <C> The type of the codec
 */
public abstract non-sealed class AbstractCodec<C> implements Codec<C> {
	
	/**
	 * Constructs a new abstract codec.<br>
	 */
	protected AbstractCodec() {}
}
