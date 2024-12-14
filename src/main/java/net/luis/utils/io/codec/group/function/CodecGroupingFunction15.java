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

package net.luis.utils.io.codec.group.function;

import org.jetbrains.annotations.NotNull;

/**
 *
 * @author Luis-St
 *
 */

@FunctionalInterface
public interface CodecGroupingFunction15<CI1, CI2, CI3, CI4, CI5, CI6, CI7, CI8, CI9, CI10, CI11, CI12, CI13, CI14, CI15, R> {
	
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
		@NotNull CI14 input14,
		@NotNull CI15 input15
	);
}
