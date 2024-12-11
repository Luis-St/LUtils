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

import net.luis.utils.io.codec.group.grouper.*;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 *
 * @author Luis-St
 *
 */

public class CodecBuilder<T> {
	
	CodecBuilder() {}
	
	public static <T> @NotNull Codec<T> create(@NotNull Function<CodecBuilder<T>, Codec<T>> function) {
		return function.apply(new CodecBuilder<>());
	}
	
	public <CI1> @NotNull CodecGrouper1<CI1, T> group(
		@NotNull ConfigurableCodec<CI1, T> codec1
	) {
		return new CodecGrouper1<>(codec1);
	}
	
	public <CI1, CI2> @NotNull CodecGrouper2<CI1, CI2, T> group(
		@NotNull ConfigurableCodec<CI1, T> codec1,
		@NotNull ConfigurableCodec<CI2, T> codec2
	) {
		return new CodecGrouper2<>(codec1, codec2);
	}
	
	public <CI1, CI2, CI3> @NotNull CodecGrouper3<CI1, CI2, CI3, T> group(
		@NotNull ConfigurableCodec<CI1, T> codec1,
		@NotNull ConfigurableCodec<CI2, T> codec2,
		@NotNull ConfigurableCodec<CI3, T> codec3
	) {
		return new CodecGrouper3<>(codec1, codec2, codec3);
	}
	
	public <CI1, CI2, CI3, CI4> @NotNull CodecGrouper4<CI1, CI2, CI3, CI4, T> group(
		@NotNull ConfigurableCodec<CI1, T> codec1,
		@NotNull ConfigurableCodec<CI2, T> codec2,
		@NotNull ConfigurableCodec<CI3, T> codec3,
		@NotNull ConfigurableCodec<CI4, T> codec4
	) {
		return new CodecGrouper4<>(codec1, codec2, codec3, codec4);
	}
}
