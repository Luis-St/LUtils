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

import net.luis.utils.io.codec.group.grouper.*;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 *
 * @author Luis-St
 *
 */

@SuppressWarnings("DuplicatedCode")
public class CodecBuilder {
	
	public static <CI1, O> @NotNull CodecGrouper1<CI1, O> group(
		@NotNull ConfiguredCodec<CI1, O> codec1
	) {
		Objects.requireNonNull(codec1, "Configured codec #1 must not be null");
		return new CodecGrouper1<>(codec1);
	}
	
	public static <CI1, CI2, O> @NotNull CodecGrouper2<CI1, CI2, O> group(
		@NotNull ConfiguredCodec<CI1, O> codec1,
		@NotNull ConfiguredCodec<CI2, O> codec2
	) {
		Objects.requireNonNull(codec1, "Configured codec #1 must not be null");
		Objects.requireNonNull(codec2, "Configured codec #2 must not be null");
		return new CodecGrouper2<>(codec1, codec2);
	}
	
	public static <CI1, CI2, CI3, O> @NotNull CodecGrouper3<CI1, CI2, CI3, O> group(
		@NotNull ConfiguredCodec<CI1, O> codec1,
		@NotNull ConfiguredCodec<CI2, O> codec2,
		@NotNull ConfiguredCodec<CI3, O> codec3
	) {
		Objects.requireNonNull(codec1, "Configured codec #1 must not be null");
		Objects.requireNonNull(codec2, "Configured codec #2 must not be null");
		Objects.requireNonNull(codec3, "Configured codec #3 must not be null");
		return new CodecGrouper3<>(codec1, codec2, codec3);
	}
	
	public static <CI1, CI2, CI3, CI4, O> @NotNull CodecGrouper4<CI1, CI2, CI3, CI4, O> group(
		@NotNull ConfiguredCodec<CI1, O> codec1,
		@NotNull ConfiguredCodec<CI2, O> codec2,
		@NotNull ConfiguredCodec<CI3, O> codec3,
		@NotNull ConfiguredCodec<CI4, O> codec4
	) {
		//region Parameter validation
		Objects.requireNonNull(codec1, "Configured codec #1 must not be null");
		Objects.requireNonNull(codec2, "Configured codec #2 must not be null");
		Objects.requireNonNull(codec3, "Configured codec #3 must not be null");
		Objects.requireNonNull(codec4, "Configured codec #4 must not be null");
		//endregion
		return new CodecGrouper4<>(codec1, codec2, codec3, codec4);
	}
	
	public static <CI1, CI2, CI3, CI4, CI5, O> @NotNull CodecGrouper5<CI1, CI2, CI3, CI4, CI5, O> group(
		@NotNull ConfiguredCodec<CI1, O> codec1,
		@NotNull ConfiguredCodec<CI2, O> codec2,
		@NotNull ConfiguredCodec<CI3, O> codec3,
		@NotNull ConfiguredCodec<CI4, O> codec4,
		@NotNull ConfiguredCodec<CI5, O> codec5
	) {
		//region Parameter validation
		Objects.requireNonNull(codec1, "Configured codec #1 must not be null");
		Objects.requireNonNull(codec2, "Configured codec #2 must not be null");
		Objects.requireNonNull(codec3, "Configured codec #3 must not be null");
		Objects.requireNonNull(codec4, "Configured codec #4 must not be null");
		Objects.requireNonNull(codec5, "Configured codec #5 must not be null");
		//endregion
		return new CodecGrouper5<>(codec1, codec2, codec3, codec4, codec5);
	}
	
	public static <CI1, CI2, CI3, CI4, CI5, CI6, O> @NotNull CodecGrouper6<CI1, CI2, CI3, CI4, CI5, CI6, O> group(
		@NotNull ConfiguredCodec<CI1, O> codec1,
		@NotNull ConfiguredCodec<CI2, O> codec2,
		@NotNull ConfiguredCodec<CI3, O> codec3,
		@NotNull ConfiguredCodec<CI4, O> codec4,
		@NotNull ConfiguredCodec<CI5, O> codec5,
		@NotNull ConfiguredCodec<CI6, O> codec6
	) {
		//region Parameter validation
		Objects.requireNonNull(codec1, "Configured codec #1 must not be null");
		Objects.requireNonNull(codec2, "Configured codec #2 must not be null");
		Objects.requireNonNull(codec3, "Configured codec #3 must not be null");
		Objects.requireNonNull(codec4, "Configured codec #4 must not be null");
		Objects.requireNonNull(codec5, "Configured codec #5 must not be null");
		Objects.requireNonNull(codec6, "Configured codec #6 must not be null");
		//endregion
		return new CodecGrouper6<>(codec1, codec2, codec3, codec4, codec5, codec6);
	}
	
	public static <CI1, CI2, CI3, CI4, CI5, CI6, CI7, O> @NotNull CodecGrouper7<CI1, CI2, CI3, CI4, CI5, CI6, CI7, O> group(
		@NotNull ConfiguredCodec<CI1, O> codec1,
		@NotNull ConfiguredCodec<CI2, O> codec2,
		@NotNull ConfiguredCodec<CI3, O> codec3,
		@NotNull ConfiguredCodec<CI4, O> codec4,
		@NotNull ConfiguredCodec<CI5, O> codec5,
		@NotNull ConfiguredCodec<CI6, O> codec6,
		@NotNull ConfiguredCodec<CI7, O> codec7
	) {
		//region Parameter validation
		Objects.requireNonNull(codec1, "Configured codec #1 must not be null");
		Objects.requireNonNull(codec2, "Configured codec #2 must not be null");
		Objects.requireNonNull(codec3, "Configured codec #3 must not be null");
		Objects.requireNonNull(codec4, "Configured codec #4 must not be null");
		Objects.requireNonNull(codec5, "Configured codec #5 must not be null");
		Objects.requireNonNull(codec6, "Configured codec #6 must not be null");
		Objects.requireNonNull(codec7, "Configured codec #7 must not be null");
		//endregion
		return new CodecGrouper7<>(codec1, codec2, codec3, codec4, codec5, codec6, codec7);
	}
	
	public static <CI1, CI2, CI3, CI4, CI5, CI6, CI7, CI8, O> @NotNull CodecGrouper8<CI1, CI2, CI3, CI4, CI5, CI6, CI7, CI8, O> group(
		@NotNull ConfiguredCodec<CI1, O> codec1,
		@NotNull ConfiguredCodec<CI2, O> codec2,
		@NotNull ConfiguredCodec<CI3, O> codec3,
		@NotNull ConfiguredCodec<CI4, O> codec4,
		@NotNull ConfiguredCodec<CI5, O> codec5,
		@NotNull ConfiguredCodec<CI6, O> codec6,
		@NotNull ConfiguredCodec<CI7, O> codec7,
		@NotNull ConfiguredCodec<CI8, O> codec8
	) {
		//region Parameter validation
		Objects.requireNonNull(codec1, "Configured codec #1 must not be null");
		Objects.requireNonNull(codec2, "Configured codec #2 must not be null");
		Objects.requireNonNull(codec3, "Configured codec #3 must not be null");
		Objects.requireNonNull(codec4, "Configured codec #4 must not be null");
		Objects.requireNonNull(codec5, "Configured codec #5 must not be null");
		Objects.requireNonNull(codec6, "Configured codec #6 must not be null");
		Objects.requireNonNull(codec7, "Configured codec #7 must not be null");
		Objects.requireNonNull(codec8, "Configured codec #8 must not be null");
		//endregion
		return new CodecGrouper8<>(codec1, codec2, codec3, codec4, codec5, codec6, codec7, codec8);
	}
	
	public static <CI1, CI2, CI3, CI4, CI5, CI6, CI7, CI8, CI9, O> @NotNull CodecGrouper9<CI1, CI2, CI3, CI4, CI5, CI6, CI7, CI8, CI9, O> group(
		@NotNull ConfiguredCodec<CI1, O> codec1,
		@NotNull ConfiguredCodec<CI2, O> codec2,
		@NotNull ConfiguredCodec<CI3, O> codec3,
		@NotNull ConfiguredCodec<CI4, O> codec4,
		@NotNull ConfiguredCodec<CI5, O> codec5,
		@NotNull ConfiguredCodec<CI6, O> codec6,
		@NotNull ConfiguredCodec<CI7, O> codec7,
		@NotNull ConfiguredCodec<CI8, O> codec8,
		@NotNull ConfiguredCodec<CI9, O> codec9
	) {
		//region Parameter validation
		Objects.requireNonNull(codec1, "Configured codec #1 must not be null");
		Objects.requireNonNull(codec2, "Configured codec #2 must not be null");
		Objects.requireNonNull(codec3, "Configured codec #3 must not be null");
		Objects.requireNonNull(codec4, "Configured codec #4 must not be null");
		Objects.requireNonNull(codec5, "Configured codec #5 must not be null");
		Objects.requireNonNull(codec6, "Configured codec #6 must not be null");
		Objects.requireNonNull(codec7, "Configured codec #7 must not be null");
		Objects.requireNonNull(codec8, "Configured codec #8 must not be null");
		Objects.requireNonNull(codec9, "Configured codec #9 must not be null");
		//endregion
		return new CodecGrouper9<>(codec1, codec2, codec3, codec4, codec5, codec6, codec7, codec8, codec9);
	}
	
	public static <CI1, CI2, CI3, CI4, CI5, CI6, CI7, CI8, CI9, CI10, O> @NotNull CodecGrouper10<CI1, CI2, CI3, CI4, CI5, CI6, CI7, CI8, CI9, CI10, O> group(
		@NotNull ConfiguredCodec<CI1, O> codec1,
		@NotNull ConfiguredCodec<CI2, O> codec2,
		@NotNull ConfiguredCodec<CI3, O> codec3,
		@NotNull ConfiguredCodec<CI4, O> codec4,
		@NotNull ConfiguredCodec<CI5, O> codec5,
		@NotNull ConfiguredCodec<CI6, O> codec6,
		@NotNull ConfiguredCodec<CI7, O> codec7,
		@NotNull ConfiguredCodec<CI8, O> codec8,
		@NotNull ConfiguredCodec<CI9, O> codec9,
		@NotNull ConfiguredCodec<CI10, O> codec10
	) {
		//region Parameter validation
		Objects.requireNonNull(codec1, "Configured codec #1 must not be null");
		Objects.requireNonNull(codec2, "Configured codec #2 must not be null");
		Objects.requireNonNull(codec3, "Configured codec #3 must not be null");
		Objects.requireNonNull(codec4, "Configured codec #4 must not be null");
		Objects.requireNonNull(codec5, "Configured codec #5 must not be null");
		Objects.requireNonNull(codec6, "Configured codec #6 must not be null");
		Objects.requireNonNull(codec7, "Configured codec #7 must not be null");
		Objects.requireNonNull(codec8, "Configured codec #8 must not be null");
		Objects.requireNonNull(codec9, "Configured codec #9 must not be null");
		Objects.requireNonNull(codec10, "Configured codec #10 must not be null");
		//endregion
		return new CodecGrouper10<>(codec1, codec2, codec3, codec4, codec5, codec6, codec7, codec8, codec9, codec10);
	}
	
	public static <CI1, CI2, CI3, CI4, CI5, CI6, CI7, CI8, CI9, CI10, CI11, O> @NotNull CodecGrouper11<CI1, CI2, CI3, CI4, CI5, CI6, CI7, CI8, CI9, CI10, CI11, O> group(
		@NotNull ConfiguredCodec<CI1, O> codec1,
		@NotNull ConfiguredCodec<CI2, O> codec2,
		@NotNull ConfiguredCodec<CI3, O> codec3,
		@NotNull ConfiguredCodec<CI4, O> codec4,
		@NotNull ConfiguredCodec<CI5, O> codec5,
		@NotNull ConfiguredCodec<CI6, O> codec6,
		@NotNull ConfiguredCodec<CI7, O> codec7,
		@NotNull ConfiguredCodec<CI8, O> codec8,
		@NotNull ConfiguredCodec<CI9, O> codec9,
		@NotNull ConfiguredCodec<CI10, O> codec10,
		@NotNull ConfiguredCodec<CI11, O> codec11
	) {
		//region Parameter validation
		Objects.requireNonNull(codec1, "Configured codec #1 must not be null");
		Objects.requireNonNull(codec2, "Configured codec #2 must not be null");
		Objects.requireNonNull(codec3, "Configured codec #3 must not be null");
		Objects.requireNonNull(codec4, "Configured codec #4 must not be null");
		Objects.requireNonNull(codec5, "Configured codec #5 must not be null");
		Objects.requireNonNull(codec6, "Configured codec #6 must not be null");
		Objects.requireNonNull(codec7, "Configured codec #7 must not be null");
		Objects.requireNonNull(codec8, "Configured codec #8 must not be null");
		Objects.requireNonNull(codec9, "Configured codec #9 must not be null");
		Objects.requireNonNull(codec10, "Configured codec #10 must not be null");
		Objects.requireNonNull(codec11, "Configured codec #11 must not be null");
		//endregion
		return new CodecGrouper11<>(codec1, codec2, codec3, codec4, codec5, codec6, codec7, codec8, codec9, codec10, codec11);
	}
	
	public static <CI1, CI2, CI3, CI4, CI5, CI6, CI7, CI8, CI9, CI10, CI11, CI12, O> @NotNull CodecGrouper12<CI1, CI2, CI3, CI4, CI5, CI6, CI7, CI8, CI9, CI10, CI11, CI12, O> group(
		@NotNull ConfiguredCodec<CI1, O> codec1,
		@NotNull ConfiguredCodec<CI2, O> codec2,
		@NotNull ConfiguredCodec<CI3, O> codec3,
		@NotNull ConfiguredCodec<CI4, O> codec4,
		@NotNull ConfiguredCodec<CI5, O> codec5,
		@NotNull ConfiguredCodec<CI6, O> codec6,
		@NotNull ConfiguredCodec<CI7, O> codec7,
		@NotNull ConfiguredCodec<CI8, O> codec8,
		@NotNull ConfiguredCodec<CI9, O> codec9,
		@NotNull ConfiguredCodec<CI10, O> codec10,
		@NotNull ConfiguredCodec<CI11, O> codec11,
		@NotNull ConfiguredCodec<CI12, O> codec12
	) {
		//region Parameter validation
		Objects.requireNonNull(codec1, "Configured codec #1 must not be null");
		Objects.requireNonNull(codec2, "Configured codec #2 must not be null");
		Objects.requireNonNull(codec3, "Configured codec #3 must not be null");
		Objects.requireNonNull(codec4, "Configured codec #4 must not be null");
		Objects.requireNonNull(codec5, "Configured codec #5 must not be null");
		Objects.requireNonNull(codec6, "Configured codec #6 must not be null");
		Objects.requireNonNull(codec7, "Configured codec #7 must not be null");
		Objects.requireNonNull(codec8, "Configured codec #8 must not be null");
		Objects.requireNonNull(codec9, "Configured codec #9 must not be null");
		Objects.requireNonNull(codec10, "Configured codec #10 must not be null");
		Objects.requireNonNull(codec11, "Configured codec #11 must not be null");
		Objects.requireNonNull(codec12, "Configured codec #12 must not be null");
		//endregion
		return new CodecGrouper12<>(codec1, codec2, codec3, codec4, codec5, codec6, codec7, codec8, codec9, codec10, codec11, codec12);
	}
	
	public static <CI1, CI2, CI3, CI4, CI5, CI6, CI7, CI8, CI9, CI10, CI11, CI12, CI13, O> @NotNull CodecGrouper13<CI1, CI2, CI3, CI4, CI5, CI6, CI7, CI8, CI9, CI10, CI11, CI12, CI13, O> group(
		@NotNull ConfiguredCodec<CI1, O> codec1,
		@NotNull ConfiguredCodec<CI2, O> codec2,
		@NotNull ConfiguredCodec<CI3, O> codec3,
		@NotNull ConfiguredCodec<CI4, O> codec4,
		@NotNull ConfiguredCodec<CI5, O> codec5,
		@NotNull ConfiguredCodec<CI6, O> codec6,
		@NotNull ConfiguredCodec<CI7, O> codec7,
		@NotNull ConfiguredCodec<CI8, O> codec8,
		@NotNull ConfiguredCodec<CI9, O> codec9,
		@NotNull ConfiguredCodec<CI10, O> codec10,
		@NotNull ConfiguredCodec<CI11, O> codec11,
		@NotNull ConfiguredCodec<CI12, O> codec12,
		@NotNull ConfiguredCodec<CI13, O> codec13
	) {
		//region Parameter validation
		Objects.requireNonNull(codec1, "Configured codec #1 must not be null");
		Objects.requireNonNull(codec2, "Configured codec #2 must not be null");
		Objects.requireNonNull(codec3, "Configured codec #3 must not be null");
		Objects.requireNonNull(codec4, "Configured codec #4 must not be null");
		Objects.requireNonNull(codec5, "Configured codec #5 must not be null");
		Objects.requireNonNull(codec6, "Configured codec #6 must not be null");
		Objects.requireNonNull(codec7, "Configured codec #7 must not be null");
		Objects.requireNonNull(codec8, "Configured codec #8 must not be null");
		Objects.requireNonNull(codec9, "Configured codec #9 must not be null");
		Objects.requireNonNull(codec10, "Configured codec #10 must not be null");
		Objects.requireNonNull(codec11, "Configured codec #11 must not be null");
		Objects.requireNonNull(codec12, "Configured codec #12 must not be null");
		Objects.requireNonNull(codec13, "Configured codec #13 must not be null");
		//endregion
		return new CodecGrouper13<>(codec1, codec2, codec3, codec4, codec5, codec6, codec7, codec8, codec9, codec10, codec11, codec12, codec13);
	}
	
	public static <CI1, CI2, CI3, CI4, CI5, CI6, CI7, CI8, CI9, CI10, CI11, CI12, CI13, CI14, O> @NotNull CodecGrouper14<CI1, CI2, CI3, CI4, CI5, CI6, CI7, CI8, CI9, CI10, CI11, CI12, CI13, CI14, O> group(
		@NotNull ConfiguredCodec<CI1, O> codec1,
		@NotNull ConfiguredCodec<CI2, O> codec2,
		@NotNull ConfiguredCodec<CI3, O> codec3,
		@NotNull ConfiguredCodec<CI4, O> codec4,
		@NotNull ConfiguredCodec<CI5, O> codec5,
		@NotNull ConfiguredCodec<CI6, O> codec6,
		@NotNull ConfiguredCodec<CI7, O> codec7,
		@NotNull ConfiguredCodec<CI8, O> codec8,
		@NotNull ConfiguredCodec<CI9, O> codec9,
		@NotNull ConfiguredCodec<CI10, O> codec10,
		@NotNull ConfiguredCodec<CI11, O> codec11,
		@NotNull ConfiguredCodec<CI12, O> codec12,
		@NotNull ConfiguredCodec<CI13, O> codec13,
		@NotNull ConfiguredCodec<CI14, O> codec14
	) {
		//region Parameter validation
		Objects.requireNonNull(codec1, "Configured codec #1 must not be null");
		Objects.requireNonNull(codec2, "Configured codec #2 must not be null");
		Objects.requireNonNull(codec3, "Configured codec #3 must not be null");
		Objects.requireNonNull(codec4, "Configured codec #4 must not be null");
		Objects.requireNonNull(codec5, "Configured codec #5 must not be null");
		Objects.requireNonNull(codec6, "Configured codec #6 must not be null");
		Objects.requireNonNull(codec7, "Configured codec #7 must not be null");
		Objects.requireNonNull(codec8, "Configured codec #8 must not be null");
		Objects.requireNonNull(codec9, "Configured codec #9 must not be null");
		Objects.requireNonNull(codec10, "Configured codec #10 must not be null");
		Objects.requireNonNull(codec11, "Configured codec #11 must not be null");
		Objects.requireNonNull(codec12, "Configured codec #12 must not be null");
		Objects.requireNonNull(codec13, "Configured codec #13 must not be null");
		Objects.requireNonNull(codec14, "Configured codec #14 must not be null");
		//endregion
		return new CodecGrouper14<>(codec1, codec2, codec3, codec4, codec5, codec6, codec7, codec8, codec9, codec10, codec11, codec12, codec13, codec14);
	}
	
	public static <CI1, CI2, CI3, CI4, CI5, CI6, CI7, CI8, CI9, CI10, CI11, CI12, CI13, CI14, CI15, O> @NotNull CodecGrouper15<CI1, CI2, CI3, CI4, CI5, CI6, CI7, CI8, CI9, CI10, CI11, CI12, CI13, CI14, CI15, O> group(
		@NotNull ConfiguredCodec<CI1, O> codec1,
		@NotNull ConfiguredCodec<CI2, O> codec2,
		@NotNull ConfiguredCodec<CI3, O> codec3,
		@NotNull ConfiguredCodec<CI4, O> codec4,
		@NotNull ConfiguredCodec<CI5, O> codec5,
		@NotNull ConfiguredCodec<CI6, O> codec6,
		@NotNull ConfiguredCodec<CI7, O> codec7,
		@NotNull ConfiguredCodec<CI8, O> codec8,
		@NotNull ConfiguredCodec<CI9, O> codec9,
		@NotNull ConfiguredCodec<CI10, O> codec10,
		@NotNull ConfiguredCodec<CI11, O> codec11,
		@NotNull ConfiguredCodec<CI12, O> codec12,
		@NotNull ConfiguredCodec<CI13, O> codec13,
		@NotNull ConfiguredCodec<CI14, O> codec14,
		@NotNull ConfiguredCodec<CI15, O> codec15
	) {
		//region Parameter validation
		Objects.requireNonNull(codec1, "Configured codec #1 must not be null");
		Objects.requireNonNull(codec2, "Configured codec #2 must not be null");
		Objects.requireNonNull(codec3, "Configured codec #3 must not be null");
		Objects.requireNonNull(codec4, "Configured codec #4 must not be null");
		Objects.requireNonNull(codec5, "Configured codec #5 must not be null");
		Objects.requireNonNull(codec6, "Configured codec #6 must not be null");
		Objects.requireNonNull(codec7, "Configured codec #7 must not be null");
		Objects.requireNonNull(codec8, "Configured codec #8 must not be null");
		Objects.requireNonNull(codec9, "Configured codec #9 must not be null");
		Objects.requireNonNull(codec10, "Configured codec #10 must not be null");
		Objects.requireNonNull(codec11, "Configured codec #11 must not be null");
		Objects.requireNonNull(codec12, "Configured codec #12 must not be null");
		Objects.requireNonNull(codec13, "Configured codec #13 must not be null");
		Objects.requireNonNull(codec14, "Configured codec #14 must not be null");
		Objects.requireNonNull(codec15, "Configured codec #15 must not be null");
		//endregion
		return new CodecGrouper15<>(codec1, codec2, codec3, codec4, codec5, codec6, codec7, codec8, codec9, codec10, codec11, codec12, codec13, codec14, codec15);
	}
	
	public static <CI1, CI2, CI3, CI4, CI5, CI6, CI7, CI8, CI9, CI10, CI11, CI12, CI13, CI14, CI15, CI16, O> @NotNull CodecGrouper16<CI1, CI2, CI3, CI4, CI5, CI6, CI7, CI8, CI9, CI10, CI11, CI12, CI13, CI14, CI15, CI16, O> group(
		@NotNull ConfiguredCodec<CI1, O> codec1,
		@NotNull ConfiguredCodec<CI2, O> codec2,
		@NotNull ConfiguredCodec<CI3, O> codec3,
		@NotNull ConfiguredCodec<CI4, O> codec4,
		@NotNull ConfiguredCodec<CI5, O> codec5,
		@NotNull ConfiguredCodec<CI6, O> codec6,
		@NotNull ConfiguredCodec<CI7, O> codec7,
		@NotNull ConfiguredCodec<CI8, O> codec8,
		@NotNull ConfiguredCodec<CI9, O> codec9,
		@NotNull ConfiguredCodec<CI10, O> codec10,
		@NotNull ConfiguredCodec<CI11, O> codec11,
		@NotNull ConfiguredCodec<CI12, O> codec12,
		@NotNull ConfiguredCodec<CI13, O> codec13,
		@NotNull ConfiguredCodec<CI14, O> codec14,
		@NotNull ConfiguredCodec<CI15, O> codec15,
		@NotNull ConfiguredCodec<CI16, O> codec16
	) {
		//region Parameter validation
		Objects.requireNonNull(codec1, "Configured codec #1 must not be null");
		Objects.requireNonNull(codec2, "Configured codec #2 must not be null");
		Objects.requireNonNull(codec3, "Configured codec #3 must not be null");
		Objects.requireNonNull(codec4, "Configured codec #4 must not be null");
		Objects.requireNonNull(codec5, "Configured codec #5 must not be null");
		Objects.requireNonNull(codec6, "Configured codec #6 must not be null");
		Objects.requireNonNull(codec7, "Configured codec #7 must not be null");
		Objects.requireNonNull(codec8, "Configured codec #8 must not be null");
		Objects.requireNonNull(codec9, "Configured codec #9 must not be null");
		Objects.requireNonNull(codec10, "Configured codec #10 must not be null");
		Objects.requireNonNull(codec11, "Configured codec #11 must not be null");
		Objects.requireNonNull(codec12, "Configured codec #12 must not be null");
		Objects.requireNonNull(codec13, "Configured codec #13 must not be null");
		Objects.requireNonNull(codec14, "Configured codec #14 must not be null");
		Objects.requireNonNull(codec15, "Configured codec #15 must not be null");
		Objects.requireNonNull(codec16, "Configured codec #16 must not be null");
		//endregion
		return new CodecGrouper16<>(codec1, codec2, codec3, codec4, codec5, codec6, codec7, codec8, codec9, codec10, codec11, codec12, codec13, codec14, codec15, codec16);
	}
}
