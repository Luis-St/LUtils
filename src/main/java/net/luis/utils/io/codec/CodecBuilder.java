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

import net.luis.utils.io.codec.group.function.*;
import net.luis.utils.io.codec.group.grouper.*;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * A utility class that provides methods to create codec groupers that group multiple codecs into a single codec.<br>
 *
 * @author Luis-St
 */
@SuppressWarnings("DuplicatedCode")
public final class CodecBuilder {
	
	/**
	 * Private constructor to prevent instantiation.<br>
	 * This is a static helper class.<br>
	 */
	private CodecBuilder() {}
	
	/**
	 * Creates a new codec grouper that groups the provided codec into a single codec.<br>
	 * The resulting codec can be created by calling the {@code create} method of the returned grouper.<br>
	 * The {@code create} requires a grouping function as input that constructs the resulting object from the provided components.<br>
	 * @param codec1 The first codec
	 * @return The created codec grouper
	 * @param <CI1> The type of the first component
	 * @param <O> The type of the resulting object the created codec will produce
	 * @throws NullPointerException If any of the provided codecs is null
	 * @see CodecGrouper1
	 * @see CodecGroupingFunction1
	 */
	public static <CI1, O> @NotNull CodecGrouper1<CI1, O> group(
		@NotNull ConfiguredCodec<CI1, O> codec1
	) {
		Objects.requireNonNull(codec1, "Configured codec #1 must not be null");
		return new CodecGrouper1<>(codec1);
	}
	
	/**
	 * Creates a new codec grouper that groups the provided codecs into a single codec.<br>
	 * The resulting codec can be created by calling the {@code create} method of the returned grouper.<br>
	 * The {@code create} requires a grouping function as input that constructs the resulting object from the provided components.<br>
	 * @param codec1 The first codec
	 * @param codec2 The second codec
	 * @return The created codec grouper
	 * @param <CI1> The type of the first component
	 * @param <CI2> The type of the second component
	 * @param <O> The type of the resulting object the created codec will produce
	 * @throws NullPointerException If any of the provided codecs is null
	 * @see CodecGrouper2
	 * @see CodecGroupingFunction2
	 */
	public static <CI1, CI2, O> @NotNull CodecGrouper2<CI1, CI2, O> group(
		@NotNull ConfiguredCodec<CI1, O> codec1,
		@NotNull ConfiguredCodec<CI2, O> codec2
	) {
		Objects.requireNonNull(codec1, "Configured codec #1 must not be null");
		Objects.requireNonNull(codec2, "Configured codec #2 must not be null");
		return new CodecGrouper2<>(codec1, codec2);
	}
	
	/**
	 * Creates a new codec grouper that groups the provided codecs into a single codec.<br>
	 * The resulting codec can be created by calling the {@code create} method of the returned grouper.<br>
	 * The {@code create} requires a grouping function as input that constructs the resulting object from the provided components.<br>
	 * @param codec1 The first codec
	 * @param codec2 The second codec
	 * @param codec3 The third codec
	 * @return The created codec grouper
	 * @param <CI1> The type of the first component
	 * @param <CI2> The type of the second component
	 * @param <CI3> The type of the third component
	 * @param <O> The type of the resulting object the created codec will produce
	 * @throws NullPointerException If any of the provided codecs is null
	 * @see CodecGrouper3
	 * @see CodecGroupingFunction3
	 */
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
	
	/**
	 * Creates a new codec grouper that groups the provided codecs into a single codec.<br>
	 * The resulting codec can be created by calling the {@code create} method of the returned grouper.<br>
	 * The {@code create} requires a grouping function as input that constructs the resulting object from the provided components.<br>
	 * @param codec1 The first codec
	 * @param codec2 The second codec
	 * @param codec3 The third codec
	 * @param codec4 The fourth codec
	 * @return The created codec grouper
	 * @param <CI1> The type of the first component
	 * @param <CI2> The type of the second component
	 * @param <CI3> The type of the third component
	 * @param <CI4> The type of the fourth component
	 * @param <O> The type of the resulting object the created codec will produce
	 * @throws NullPointerException If any of the provided codecs is null
	 * @see CodecGrouper4
	 * @see CodecGroupingFunction4
	 */
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
	
	/**
	 * Creates a new codec grouper that groups the provided codecs into a single codec.<br>
	 * The resulting codec can be created by calling the {@code create} method of the returned grouper.<br>
	 * The {@code create} requires a grouping function as input that constructs the resulting object from the provided components.<br>
	 * @param codec1 The first codec
	 * @param codec2 The second codec
	 * @param codec3 The third codec
	 * @param codec4 The fourth codec
	 * @param codec5 The fifth codec
	 * @return The created codec grouper
	 * @param <CI1> The type of the first component
	 * @param <CI2> The type of the second component
	 * @param <CI3> The type of the third component
	 * @param <CI4> The type of the fourth component
	 * @param <CI5> The type of the fifth component
	 * @param <O> The type of the resulting object the created codec will produce
	 * @throws NullPointerException If any of the provided codecs is null
	 * @see CodecGrouper5
	 * @see CodecGroupingFunction5
	 */
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
	
	/**
	 * Creates a new codec grouper that groups the provided codecs into a single codec.<br>
	 * The resulting codec can be created by calling the {@code create} method of the returned grouper.<br>
	 * The {@code create} requires a grouping function as input that constructs the resulting object from the provided components.<br>
	 * @param codec1 The first codec
	 * @param codec2 The second codec
	 * @param codec3 The third codec
	 * @param codec4 The fourth codec
	 * @param codec5 The fifth codec
	 * @param codec6 The sixth codec
	 * @return The created codec grouper
	 * @param <CI1> The type of the first component
	 * @param <CI2> The type of the second component
	 * @param <CI3> The type of the third component
	 * @param <CI4> The type of the fourth component
	 * @param <CI5> The type of the fifth component
	 * @param <CI6> The type of the sixth component
	 * @param <O> The type of the resulting object the created codec will produce
	 * @throws NullPointerException If any of the provided codecs is null
	 * @see CodecGrouper6
	 * @see CodecGroupingFunction6
	 */
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
	
	/**
	 * Creates a new codec grouper that groups the provided codecs into a single codec.<br>
	 * The resulting codec can be created by calling the {@code create} method of the returned grouper.<br>
	 * The {@code create} requires a grouping function as input that constructs the resulting object from the provided components.<br>
	 * @param codec1 The first codec
	 * @param codec2 The second codec
	 * @param codec3 The third codec
	 * @param codec4 The fourth codec
	 * @param codec5 The fifth codec
	 * @param codec6 The sixth codec
	 * @param codec7 The seventh codec
	 * @return The created codec grouper
	 * @param <CI1> The type of the first component
	 * @param <CI2> The type of the second component
	 * @param <CI3> The type of the third component
	 * @param <CI4> The type of the fourth component
	 * @param <CI5> The type of the fifth component
	 * @param <CI6> The type of the sixth component
	 * @param <CI7> The type of the seventh component
	 * @param <O> The type of the resulting object the created codec will produce
	 * @throws NullPointerException If any of the provided codecs is null
	 * @see CodecGrouper7
	 * @see CodecGroupingFunction7
	 */
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
	
	/**
	 * Creates a new codec grouper that groups the provided codecs into a single codec.<br>
	 * The resulting codec can be created by calling the {@code create} method of the returned grouper.<br>
	 * The {@code create} requires a grouping function as input that constructs the resulting object from the provided components.<br>
	 * @param codec1 The first codec
	 * @param codec2 The second codec
	 * @param codec3 The third codec
	 * @param codec4 The fourth codec
	 * @param codec5 The fifth codec
	 * @param codec6 The sixth codec
	 * @param codec7 The seventh codec
	 * @param codec8 The eighth codec
	 * @return The created codec grouper
	 * @param <CI1> The type of the first component
	 * @param <CI2> The type of the second component
	 * @param <CI3> The type of the third component
	 * @param <CI4> The type of the fourth component
	 * @param <CI5> The type of the fifth component
	 * @param <CI6> The type of the sixth component
	 * @param <CI7> The type of the seventh component
	 * @param <CI8> The type of the eighth component
	 * @param <O> The type of the resulting object the created codec will produce
	 * @throws NullPointerException If any of the provided codecs is null
	 * @see CodecGrouper8
	 * @see CodecGroupingFunction8
	 */
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
	
	/**
	 * Creates a new codec grouper that groups the provided codecs into a single codec.<br>
	 * The resulting codec can be created by calling the {@code create} method of the returned grouper.<br>
	 * The {@code create} requires a grouping function as input that constructs the resulting object from the provided components.<br>
	 * @param codec1 The first codec
	 * @param codec2 The second codec
	 * @param codec3 The third codec
	 * @param codec4 The fourth codec
	 * @param codec5 The fifth codec
	 * @param codec6 The sixth codec
	 * @param codec7 The seventh codec
	 * @param codec8 The eighth codec
	 * @param codec9 The ninth codec
	 * @return The created codec grouper
	 * @param <CI1> The type of the first component
	 * @param <CI2> The type of the second component
	 * @param <CI3> The type of the third component
	 * @param <CI4> The type of the fourth component
	 * @param <CI5> The type of the fifth component
	 * @param <CI6> The type of the sixth component
	 * @param <CI7> The type of the seventh component
	 * @param <CI8> The type of the eighth component
	 * @param <CI9> The type of the ninth component
	 * @param <O> The type of the resulting object the created codec will produce
	 * @throws NullPointerException If any of the provided codecs is null
	 * @see CodecGrouper9
	 * @see CodecGroupingFunction9
	 */
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
	
	/**
	 * Creates a new codec grouper that groups the provided codecs into a single codec.<br>
	 * The resulting codec can be created by calling the {@code create} method of the returned grouper.<br>
	 * The {@code create} requires a grouping function as input that constructs the resulting object from the provided components.<br>
	 * @param codec1 The first codec
	 * @param codec2 The second codec
	 * @param codec3 The third codec
	 * @param codec4 The fourth codec
	 * @param codec5 The fifth codec
	 * @param codec6 The sixth codec
	 * @param codec7 The seventh codec
	 * @param codec8 The eighth codec
	 * @param codec9 The ninth codec
	 * @param codec10 The tenth codec
	 * @return The created codec grouper
	 * @param <CI1> The type of the first component
	 * @param <CI2> The type of the second component
	 * @param <CI3> The type of the third component
	 * @param <CI4> The type of the fourth component
	 * @param <CI5> The type of the fifth component
	 * @param <CI6> The type of the sixth component
	 * @param <CI7> The type of the seventh component
	 * @param <CI8> The type of the eighth component
	 * @param <CI9> The type of the ninth component
	 * @param <CI10> The type of the tenth component
	 * @param <O> The type of the resulting object the created codec will produce
	 * @throws NullPointerException If any of the provided codecs is null
	 * @see CodecGrouper10
	 * @see CodecGroupingFunction10
	 */
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
	
	/**
	 * Creates a new codec grouper that groups the provided codecs into a single codec.<br>
	 * The resulting codec can be created by calling the {@code create} method of the returned grouper.<br>
	 * The {@code create} requires a grouping function as input that constructs the resulting object from the provided components.<br>
	 * @param codec1 The first codec
	 * @param codec2 The second codec
	 * @param codec3 The third codec
	 * @param codec4 The fourth codec
	 * @param codec5 The fifth codec
	 * @param codec6 The sixth codec
	 * @param codec7 The seventh codec
	 * @param codec8 The eighth codec
	 * @param codec9 The ninth codec
	 * @param codec10 The tenth codec
	 * @param codec11 The eleventh codec
	 * @return The created codec grouper
	 * @param <CI1> The type of the first component
	 * @param <CI2> The type of the second component
	 * @param <CI3> The type of the third component
	 * @param <CI4> The type of the fourth component
	 * @param <CI5> The type of the fifth component
	 * @param <CI6> The type of the sixth component
	 * @param <CI7> The type of the seventh component
	 * @param <CI8> The type of the eighth component
	 * @param <CI9> The type of the ninth component
	 * @param <CI10> The type of the tenth component
	 * @param <CI11> The type of the eleventh component
	 * @param <O> The type of the resulting object the created codec will produce
	 * @throws NullPointerException If any of the provided codecs is null
	 * @see CodecGrouper11
	 * @see CodecGroupingFunction11
	 */
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
	
	/**
	 * Creates a new codec grouper that groups the provided codecs into a single codec.<br>
	 * The resulting codec can be created by calling the {@code create} method of the returned grouper.<br>
	 * The {@code create} requires a grouping function as input that constructs the resulting object from the provided components.<br>
	 * @param codec1 The first codec
	 * @param codec2 The second codec
	 * @param codec3 The third codec
	 * @param codec4 The fourth codec
	 * @param codec5 The fifth codec
	 * @param codec6 The sixth codec
	 * @param codec7 The seventh codec
	 * @param codec8 The eighth codec
	 * @param codec9 The ninth codec
	 * @param codec10 The tenth codec
	 * @param codec11 The eleventh codec
	 * @param codec12 The twelfth codec
	 * @return The created codec grouper
	 * @param <CI1> The type of the first component
	 * @param <CI2> The type of the second component
	 * @param <CI3> The type of the third component
	 * @param <CI4> The type of the fourth component
	 * @param <CI5> The type of the fifth component
	 * @param <CI6> The type of the sixth component
	 * @param <CI7> The type of the seventh component
	 * @param <CI8> The type of the eighth component
	 * @param <CI9> The type of the ninth component
	 * @param <CI10> The type of the tenth component
	 * @param <CI11> The type of the eleventh component
	 * @param <CI12> The type of the twelfth component
	 * @param <O> The type of the resulting object the created codec will produce
	 * @throws NullPointerException If any of the provided codecs is null
	 * @see CodecGrouper12
	 * @see CodecGroupingFunction12
	 */
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
	
	/**
	 * Creates a new codec grouper that groups the provided codecs into a single codec.<br>
	 * The resulting codec can be created by calling the {@code create} method of the returned grouper.<br>
	 * The {@code create} requires a grouping function as input that constructs the resulting object from the provided components.<br>
	 * @param codec1 The first codec
	 * @param codec2 The second codec
	 * @param codec3 The third codec
	 * @param codec4 The fourth codec
	 * @param codec5 The fifth codec
	 * @param codec6 The sixth codec
	 * @param codec7 The seventh codec
	 * @param codec8 The eighth codec
	 * @param codec9 The ninth codec
	 * @param codec10 The tenth codec
	 * @param codec11 The eleventh codec
	 * @param codec12 The twelfth codec
	 * @param codec13 The thirteenth codec
	 * @return The created codec grouper
	 * @param <CI1> The type of the first component
	 * @param <CI2> The type of the second component
	 * @param <CI3> The type of the third component
	 * @param <CI4> The type of the fourth component
	 * @param <CI5> The type of the fifth component
	 * @param <CI6> The type of the sixth component
	 * @param <CI7> The type of the seventh component
	 * @param <CI8> The type of the eighth component
	 * @param <CI9> The type of the ninth component
	 * @param <CI10> The type of the tenth component
	 * @param <CI11> The type of the eleventh component
	 * @param <CI12> The type of the twelfth component
	 * @param <CI13> The type of the thirteenth component
	 * @param <O> The type of the resulting object the created codec will produce
	 * @throws NullPointerException If any of the provided codecs is null
	 * @see CodecGrouper13
	 * @see CodecGroupingFunction13
	 */
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
	
	/**
	 * Creates a new codec grouper that groups the provided codecs into a single codec.<br>
	 * The resulting codec can be created by calling the {@code create} method of the returned grouper.<br>
	 * The {@code create} requires a grouping function as input that constructs the resulting object from the provided components.<br>
	 * @param codec1 The first codec
	 * @param codec2 The second codec
	 * @param codec3 The third codec
	 * @param codec4 The fourth codec
	 * @param codec5 The fifth codec
	 * @param codec6 The sixth codec
	 * @param codec7 The seventh codec
	 * @param codec8 The eighth codec
	 * @param codec9 The ninth codec
	 * @param codec10 The tenth codec
	 * @param codec11 The eleventh codec
	 * @param codec12 The twelfth codec
	 * @param codec13 The thirteenth codec
	 * @param codec14 The fourteenth codec
	 * @return The created codec grouper
	 * @param <CI1> The type of the first component
	 * @param <CI2> The type of the second component
	 * @param <CI3> The type of the third component
	 * @param <CI4> The type of the fourth component
	 * @param <CI5> The type of the fifth component
	 * @param <CI6> The type of the sixth component
	 * @param <CI7> The type of the seventh component
	 * @param <CI8> The type of the eighth component
	 * @param <CI9> The type of the ninth component
	 * @param <CI10> The type of the tenth component
	 * @param <CI11> The type of the eleventh component
	 * @param <CI12> The type of the twelfth component
	 * @param <CI13> The type of the thirteenth component
	 * @param <CI14> The type of the fourteenth component
	 * @param <O> The type of the resulting object the created codec will produce
	 * @throws NullPointerException If any of the provided codecs is null
	 * @see CodecGrouper14
	 * @see CodecGroupingFunction14
	 */
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
	
	/**
	 * Creates a new codec grouper that groups the provided codecs into a single codec.<br>
	 * The resulting codec can be created by calling the {@code create} method of the returned grouper.<br>
	 * The {@code create} requires a grouping function as input that constructs the resulting object from the provided components.<br>
	 * @param codec1 The first codec
	 * @param codec2 The second codec
	 * @param codec3 The third codec
	 * @param codec4 The fourth codec
	 * @param codec5 The fifth codec
	 * @param codec6 The sixth codec
	 * @param codec7 The seventh codec
	 * @param codec8 The eighth codec
	 * @param codec9 The ninth codec
	 * @param codec10 The tenth codec
	 * @param codec11 The eleventh codec
	 * @param codec12 The twelfth codec
	 * @param codec13 The thirteenth codec
	 * @param codec14 The fourteenth codec
	 * @param codec15 The fifteenth codec
	 * @return The created codec grouper
	 * @param <CI1> The type of the first component
	 * @param <CI2> The type of the second component
	 * @param <CI3> The type of the third component
	 * @param <CI4> The type of the fourth component
	 * @param <CI5> The type of the fifth component
	 * @param <CI6> The type of the sixth component
	 * @param <CI7> The type of the seventh component
	 * @param <CI8> The type of the eighth component
	 * @param <CI9> The type of the ninth component
	 * @param <CI10> The type of the tenth component
	 * @param <CI11> The type of the eleventh component
	 * @param <CI12> The type of the twelfth component
	 * @param <CI13> The type of the thirteenth component
	 * @param <CI14> The type of the fourteenth component
	 * @param <CI15> The type of the fifteenth component
	 * @param <O> The type of the resulting object the created codec will produce
	 * @throws NullPointerException If any of the provided codecs is null
	 * @see CodecGrouper15
	 * @see CodecGroupingFunction15
	 */
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
	
	/**
	 * Creates a new codec grouper that groups the provided codecs into a single codec.<br>
	 * The resulting codec can be created by calling the {@code create} method of the returned grouper.<br>
	 * The {@code create} requires a grouping function as input that constructs the resulting object from the provided components.<br>
	 * @param codec1 The first codec
	 * @param codec2 The second codec
	 * @param codec3 The third codec
	 * @param codec4 The fourth codec
	 * @param codec5 The fifth codec
	 * @param codec6 The sixth codec
	 * @param codec7 The seventh codec
	 * @param codec8 The eighth codec
	 * @param codec9 The ninth codec
	 * @param codec10 The tenth codec
	 * @param codec11 The eleventh codec
	 * @param codec12 The twelfth codec
	 * @param codec13 The thirteenth codec
	 * @param codec14 The fourteenth codec
	 * @param codec15 The fifteenth codec
	 * @param codec16 The sixteenth codec
	 * @return The created codec grouper
	 * @param <CI1> The type of the first component
	 * @param <CI2> The type of the second component
	 * @param <CI3> The type of the third component
	 * @param <CI4> The type of the fourth component
	 * @param <CI5> The type of the fifth component
	 * @param <CI6> The type of the sixth component
	 * @param <CI7> The type of the seventh component
	 * @param <CI8> The type of the eighth component
	 * @param <CI9> The type of the ninth component
	 * @param <CI10> The type of the tenth component
	 * @param <CI11> The type of the eleventh component
	 * @param <CI12> The type of the twelfth component
	 * @param <CI13> The type of the thirteenth component
	 * @param <CI14> The type of the fourteenth component
	 * @param <CI15> The type of the fifteenth component
	 * @param <CI16> The type of the sixteenth component
	 * @param <O> The type of the resulting object the created codec will produce
	 * @throws NullPointerException If any of the provided codecs is null
	 * @see CodecGrouper16
	 * @see CodecGroupingFunction16
	 */
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
