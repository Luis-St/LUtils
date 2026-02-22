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

package net.luis.utils.io.database.query.row;

import org.jspecify.annotations.NonNull;

/**
 * A record representing a SQL row with 14 columns.<br>
 *
 * @author Luis-St
 *
 * @param first The value of the first column
 * @param second The value of the second column
 * @param third The value of the third column
 * @param fourth The value of the fourth column
 * @param fifth The value of the fifth column
 * @param sixth The value of the sixth column
 * @param seventh The value of the seventh column
 * @param eighth The value of the eighth column
 * @param ninth The value of the ninth column
 * @param tenth The value of the tenth column
 * @param eleventh The value of the eleventh column
 * @param twelfth The value of the twelfth column
 * @param thirteenth The value of the thirteenth column
 * @param fourteenth The value of the fourteenth column
 * @param <T1> The type of the first column
 * @param <T2> The type of the second column
 * @param <T3> The type of the third column
 * @param <T4> The type of the fourth column
 * @param <T5> The type of the fifth column
 * @param <T6> The type of the sixth column
 * @param <T7> The type of the seventh column
 * @param <T8> The type of the eighth column
 * @param <T9> The type of the ninth column
 * @param <T10> The type of the tenth column
 * @param <T11> The type of the eleventh column
 * @param <T12> The type of the twelfth column
 * @param <T13> The type of the thirteenth column
 * @param <T14> The type of the fourteenth column
 */
public record SqlRow14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14>(
	@NonNull T1 first,
	@NonNull T2 second,
	@NonNull T3 third,
	@NonNull T4 fourth,
	@NonNull T5 fifth,
	@NonNull T6 sixth,
	@NonNull T7 seventh,
	@NonNull T8 eighth,
	@NonNull T9 ninth,
	@NonNull T10 tenth,
	@NonNull T11 eleventh,
	@NonNull T12 twelfth,
	@NonNull T13 thirteenth,
	@NonNull T14 fourteenth
) {}
