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

import net.luis.utils.io.codec.decoder.KeyableDecoder;
import net.luis.utils.io.codec.encoder.KeyableEncoder;

/**
 * Combines the {@link Codec}, {@link KeyableEncoder} and {@link KeyableDecoder} interfaces.<br>
 * Into one interface that provides all methods to encode and decode a value or a key.<br>
 *
 * @author Luis-St
 *
 * @param <C> The type of the codec
 */
public interface KeyableCodec<C> extends Codec<C>, KeyableEncoder<C>, KeyableDecoder<C> {}
