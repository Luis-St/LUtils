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

package net.luis.utils.io.codec.function;

import net.luis.utils.io.codec.Codec;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a codec grouping function that accepts fourteen arguments and produces a result.<br>
 * Manly used for decoding objects using a {@link Codec}.<br>
 *
 * @author Luis-St
 *
 * @param <CI1> The type of the first input
 * @param <CI2> The type of the second input
 * @param <CI3> The type of the third input
 * @param <CI4> The type of the fourth input
 * @param <CI5> The type of the fifth input
 * @param <CI6> The type of the sixth input
 * @param <CI7> The type of the seventh input
 * @param <CI8> The type of the eighth input
 * @param <CI9> The type of the ninth input
 * @param <CI10> The type of the tenth input
 * @param <CI11> The type of the eleventh input
 * @param <CI12> The type of the twelfth input
 * @param <CI13> The type of the thirteenth input
 * @param <CI14> The type of the fourteenth input
 * @param <R> The result type
 */
@FunctionalInterface
public interface CodecGroupingFunction14<CI1, CI2, CI3, CI4, CI5, CI6, CI7, CI8, CI9, CI10, CI11, CI12, CI13, CI14, R> extends CodecGroupingFunction {
	
	/**
	 * Constructs a new object using the given inputs.<br>
	 *
	 * @param input1 The first input
	 * @param input2 The second input
	 * @param input3 The third input
	 * @param input4 The fourth input
	 * @param input5 The fifth input
	 * @param input6 The sixth input
	 * @param input7 The seventh input
	 * @param input8 The eighth input
	 * @param input9 The ninth input
	 * @param input10 The tenth input
	 * @param input11 The eleventh input
	 * @param input12 The twelfth input
	 * @param input13 The thirteenth input
	 * @param input14 The fourteenth input
	 * @return The constructed object
	 */
	@NotNull R create(
		@NotNull CI1 input1,
		@NotNull CI2 input2,
		@NotNull CI3 input3,
		@NotNull CI4 input4,
		@NotNull CI5 input5,
		@NotNull CI6 input6,
		@NotNull CI7 input7,
		@NotNull CI8 input8,
		@NotNull CI9 input9,
		@NotNull CI10 input10,
		@NotNull CI11 input11,
		@NotNull CI12 input12,
		@NotNull CI13 input13,
		@NotNull CI14 input14
	);
}
