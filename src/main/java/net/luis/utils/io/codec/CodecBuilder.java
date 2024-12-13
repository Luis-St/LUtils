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
	
	public <CI1, CI2, CI3, CI4, CI5> @NotNull CodecGrouper5<CI1, CI2, CI3, CI4, CI5, T> group(
		@NotNull ConfigurableCodec<CI1, T> codec1,
		@NotNull ConfigurableCodec<CI2, T> codec2,
		@NotNull ConfigurableCodec<CI3, T> codec3,
		@NotNull ConfigurableCodec<CI4, T> codec4,
		@NotNull ConfigurableCodec<CI5, T> codec5
	) {
		return new CodecGrouper5<>(codec1, codec2, codec3, codec4, codec5);
	}
	
	public <CI1, CI2, CI3, CI4, CI5, CI6> @NotNull CodecGrouper6<CI1, CI2, CI3, CI4, CI5, CI6, T> group(
		@NotNull ConfigurableCodec<CI1, T> codec1,
		@NotNull ConfigurableCodec<CI2, T> codec2,
		@NotNull ConfigurableCodec<CI3, T> codec3,
		@NotNull ConfigurableCodec<CI4, T> codec4,
		@NotNull ConfigurableCodec<CI5, T> codec5,
		@NotNull ConfigurableCodec<CI6, T> codec6
	) {
		return new CodecGrouper6<>(codec1, codec2, codec3, codec4, codec5, codec6);
	}
	
	public <CI1, CI2, CI3, CI4, CI5, CI6, CI7> @NotNull CodecGrouper7<CI1, CI2, CI3, CI4, CI5, CI6, CI7, T> group(
		@NotNull ConfigurableCodec<CI1, T> codec1,
		@NotNull ConfigurableCodec<CI2, T> codec2,
		@NotNull ConfigurableCodec<CI3, T> codec3,
		@NotNull ConfigurableCodec<CI4, T> codec4,
		@NotNull ConfigurableCodec<CI5, T> codec5,
		@NotNull ConfigurableCodec<CI6, T> codec6,
		@NotNull ConfigurableCodec<CI7, T> codec7
	) {
		return new CodecGrouper7<>(codec1, codec2, codec3, codec4, codec5, codec6, codec7);
	}
	
	public <CI1, CI2, CI3, CI4, CI5, CI6, CI7, CI8> @NotNull CodecGrouper8<CI1, CI2, CI3, CI4, CI5, CI6, CI7, CI8, T> group(
		@NotNull ConfigurableCodec<CI1, T> codec1,
		@NotNull ConfigurableCodec<CI2, T> codec2,
		@NotNull ConfigurableCodec<CI3, T> codec3,
		@NotNull ConfigurableCodec<CI4, T> codec4,
		@NotNull ConfigurableCodec<CI5, T> codec5,
		@NotNull ConfigurableCodec<CI6, T> codec6,
		@NotNull ConfigurableCodec<CI7, T> codec7,
		@NotNull ConfigurableCodec<CI8, T> codec8
	) {
		return new CodecGrouper8<>(codec1, codec2, codec3, codec4, codec5, codec6, codec7, codec8);
	}
	
	public <CI1, CI2, CI3, CI4, CI5, CI6, CI7, CI8, CI9> @NotNull CodecGrouper9<CI1, CI2, CI3, CI4, CI5, CI6, CI7, CI8, CI9, T> group(
		@NotNull ConfigurableCodec<CI1, T> codec1,
		@NotNull ConfigurableCodec<CI2, T> codec2,
		@NotNull ConfigurableCodec<CI3, T> codec3,
		@NotNull ConfigurableCodec<CI4, T> codec4,
		@NotNull ConfigurableCodec<CI5, T> codec5,
		@NotNull ConfigurableCodec<CI6, T> codec6,
		@NotNull ConfigurableCodec<CI7, T> codec7,
		@NotNull ConfigurableCodec<CI8, T> codec8,
		@NotNull ConfigurableCodec<CI9, T> codec9
	) {
		return new CodecGrouper9<>(codec1, codec2, codec3, codec4, codec5, codec6, codec7, codec8, codec9);
	}
	
	public <CI1, CI2, CI3, CI4, CI5, CI6, CI7, CI8, CI9, CI10> @NotNull CodecGrouper10<CI1, CI2, CI3, CI4, CI5, CI6, CI7, CI8, CI9, CI10, T> group(
		@NotNull ConfigurableCodec<CI1, T> codec1,
		@NotNull ConfigurableCodec<CI2, T> codec2,
		@NotNull ConfigurableCodec<CI3, T> codec3,
		@NotNull ConfigurableCodec<CI4, T> codec4,
		@NotNull ConfigurableCodec<CI5, T> codec5,
		@NotNull ConfigurableCodec<CI6, T> codec6,
		@NotNull ConfigurableCodec<CI7, T> codec7,
		@NotNull ConfigurableCodec<CI8, T> codec8,
		@NotNull ConfigurableCodec<CI9, T> codec9,
		@NotNull ConfigurableCodec<CI10, T> codec10
	) {
		return new CodecGrouper10<>(codec1, codec2, codec3, codec4, codec5, codec6, codec7, codec8, codec9, codec10);
	}
	
	public <CI1, CI2, CI3, CI4, CI5, CI6, CI7, CI8, CI9, CI10, CI11> @NotNull CodecGrouper11<CI1, CI2, CI3, CI4, CI5, CI6, CI7, CI8, CI9, CI10, CI11, T> group(
		@NotNull ConfigurableCodec<CI1, T> codec1,
		@NotNull ConfigurableCodec<CI2, T> codec2,
		@NotNull ConfigurableCodec<CI3, T> codec3,
		@NotNull ConfigurableCodec<CI4, T> codec4,
		@NotNull ConfigurableCodec<CI5, T> codec5,
		@NotNull ConfigurableCodec<CI6, T> codec6,
		@NotNull ConfigurableCodec<CI7, T> codec7,
		@NotNull ConfigurableCodec<CI8, T> codec8,
		@NotNull ConfigurableCodec<CI9, T> codec9,
		@NotNull ConfigurableCodec<CI10, T> codec10,
		@NotNull ConfigurableCodec<CI11, T> codec11
	) {
		return new CodecGrouper11<>(codec1, codec2, codec3, codec4, codec5, codec6, codec7, codec8, codec9, codec10, codec11);
	}
	
	public <CI1, CI2, CI3, CI4, CI5, CI6, CI7, CI8, CI9, CI10, CI11, CI12> @NotNull CodecGrouper12<CI1, CI2, CI3, CI4, CI5, CI6, CI7, CI8, CI9, CI10, CI11, CI12, T> group(
		@NotNull ConfigurableCodec<CI1, T> codec1,
		@NotNull ConfigurableCodec<CI2, T> codec2,
		@NotNull ConfigurableCodec<CI3, T> codec3,
		@NotNull ConfigurableCodec<CI4, T> codec4,
		@NotNull ConfigurableCodec<CI5, T> codec5,
		@NotNull ConfigurableCodec<CI6, T> codec6,
		@NotNull ConfigurableCodec<CI7, T> codec7,
		@NotNull ConfigurableCodec<CI8, T> codec8,
		@NotNull ConfigurableCodec<CI9, T> codec9,
		@NotNull ConfigurableCodec<CI10, T> codec10,
		@NotNull ConfigurableCodec<CI11, T> codec11,
		@NotNull ConfigurableCodec<CI12, T> codec12
	) {
		return new CodecGrouper12<>(codec1, codec2, codec3, codec4, codec5, codec6, codec7, codec8, codec9, codec10, codec11, codec12);
	}
	
	public <CI1, CI2, CI3, CI4, CI5, CI6, CI7, CI8, CI9, CI10, CI11, CI12, CI13> @NotNull CodecGrouper13<CI1, CI2, CI3, CI4, CI5, CI6, CI7, CI8, CI9, CI10, CI11, CI12, CI13, T> group(
		@NotNull ConfigurableCodec<CI1, T> codec1,
		@NotNull ConfigurableCodec<CI2, T> codec2,
		@NotNull ConfigurableCodec<CI3, T> codec3,
		@NotNull ConfigurableCodec<CI4, T> codec4,
		@NotNull ConfigurableCodec<CI5, T> codec5,
		@NotNull ConfigurableCodec<CI6, T> codec6,
		@NotNull ConfigurableCodec<CI7, T> codec7,
		@NotNull ConfigurableCodec<CI8, T> codec8,
		@NotNull ConfigurableCodec<CI9, T> codec9,
		@NotNull ConfigurableCodec<CI10, T> codec10,
		@NotNull ConfigurableCodec<CI11, T> codec11,
		@NotNull ConfigurableCodec<CI12, T> codec12,
		@NotNull ConfigurableCodec<CI13, T> codec13
	) {
		return new CodecGrouper13<>(codec1, codec2, codec3, codec4, codec5, codec6, codec7, codec8, codec9, codec10, codec11, codec12, codec13);
	}
	
	public <CI1, CI2, CI3, CI4, CI5, CI6, CI7, CI8, CI9, CI10, CI11, CI12, CI13, CI14> @NotNull CodecGrouper14<CI1, CI2, CI3, CI4, CI5, CI6, CI7, CI8, CI9, CI10, CI11, CI12, CI13, CI14, T> group(
		@NotNull ConfigurableCodec<CI1, T> codec1,
		@NotNull ConfigurableCodec<CI2, T> codec2,
		@NotNull ConfigurableCodec<CI3, T> codec3,
		@NotNull ConfigurableCodec<CI4, T> codec4,
		@NotNull ConfigurableCodec<CI5, T> codec5,
		@NotNull ConfigurableCodec<CI6, T> codec6,
		@NotNull ConfigurableCodec<CI7, T> codec7,
		@NotNull ConfigurableCodec<CI8, T> codec8,
		@NotNull ConfigurableCodec<CI9, T> codec9,
		@NotNull ConfigurableCodec<CI10, T> codec10,
		@NotNull ConfigurableCodec<CI11, T> codec11,
		@NotNull ConfigurableCodec<CI12, T> codec12,
		@NotNull ConfigurableCodec<CI13, T> codec13,
		@NotNull ConfigurableCodec<CI14, T> codec14
	) {
		return new CodecGrouper14<>(codec1, codec2, codec3, codec4, codec5, codec6, codec7, codec8, codec9, codec10, codec11, codec12, codec13, codec14);
	}
	
	public <CI1, CI2, CI3, CI4, CI5, CI6, CI7, CI8, CI9, CI10, CI11, CI12, CI13, CI14, CI15> @NotNull CodecGrouper15<CI1, CI2, CI3, CI4, CI5, CI6, CI7, CI8, CI9, CI10, CI11, CI12, CI13, CI14, CI15, T> group(
		@NotNull ConfigurableCodec<CI1, T> codec1,
		@NotNull ConfigurableCodec<CI2, T> codec2,
		@NotNull ConfigurableCodec<CI3, T> codec3,
		@NotNull ConfigurableCodec<CI4, T> codec4,
		@NotNull ConfigurableCodec<CI5, T> codec5,
		@NotNull ConfigurableCodec<CI6, T> codec6,
		@NotNull ConfigurableCodec<CI7, T> codec7,
		@NotNull ConfigurableCodec<CI8, T> codec8,
		@NotNull ConfigurableCodec<CI9, T> codec9,
		@NotNull ConfigurableCodec<CI10, T> codec10,
		@NotNull ConfigurableCodec<CI11, T> codec11,
		@NotNull ConfigurableCodec<CI12, T> codec12,
		@NotNull ConfigurableCodec<CI13, T> codec13,
		@NotNull ConfigurableCodec<CI14, T> codec14,
		@NotNull ConfigurableCodec<CI15, T> codec15
	) {
		return new CodecGrouper15<>(codec1, codec2, codec3, codec4, codec5, codec6, codec7, codec8, codec9, codec10, codec11, codec12, codec13, codec14, codec15);
	}
	
	public <CI1, CI2, CI3, CI4, CI5, CI6, CI7, CI8, CI9, CI10, CI11, CI12, CI13, CI14, CI15, CI16> @NotNull CodecGrouper16<CI1, CI2, CI3, CI4, CI5, CI6, CI7, CI8, CI9, CI10, CI11, CI12, CI13, CI14, CI15, CI16, T> group(
		@NotNull ConfigurableCodec<CI1, T> codec1,
		@NotNull ConfigurableCodec<CI2, T> codec2,
		@NotNull ConfigurableCodec<CI3, T> codec3,
		@NotNull ConfigurableCodec<CI4, T> codec4,
		@NotNull ConfigurableCodec<CI5, T> codec5,
		@NotNull ConfigurableCodec<CI6, T> codec6,
		@NotNull ConfigurableCodec<CI7, T> codec7,
		@NotNull ConfigurableCodec<CI8, T> codec8,
		@NotNull ConfigurableCodec<CI9, T> codec9,
		@NotNull ConfigurableCodec<CI10, T> codec10,
		@NotNull ConfigurableCodec<CI11, T> codec11,
		@NotNull ConfigurableCodec<CI12, T> codec12,
		@NotNull ConfigurableCodec<CI13, T> codec13,
		@NotNull ConfigurableCodec<CI14, T> codec14,
		@NotNull ConfigurableCodec<CI15, T> codec15,
		@NotNull ConfigurableCodec<CI16, T> codec16
	) {
		return new CodecGrouper16<>(codec1, codec2, codec3, codec4, codec5, codec6, codec7, codec8, codec9, codec10, codec11, codec12, codec13, codec14, codec15, codec16);
	}
}
