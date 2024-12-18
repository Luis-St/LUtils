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

package net.luis.utils.io.codec.group.grouper;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.ConfigurableCodec;
import net.luis.utils.io.codec.group.function.CodecGroupingFunction14;
import net.luis.utils.io.codec.provider.TypeProvider;
import net.luis.utils.util.Result;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;

public record CodecGrouper14<CI1, CI2, CI3, CI4, CI5, CI6, CI7, CI8, CI9, CI10, CI11, CI12, CI13, CI14, O>(
	@NotNull ConfigurableCodec<CI1, O> codec1,
	@NotNull ConfigurableCodec<CI2, O> codec2,
	@NotNull ConfigurableCodec<CI3, O> codec3,
	@NotNull ConfigurableCodec<CI4, O> codec4,
	@NotNull ConfigurableCodec<CI5, O> codec5,
	@NotNull ConfigurableCodec<CI6, O> codec6,
	@NotNull ConfigurableCodec<CI7, O> codec7,
	@NotNull ConfigurableCodec<CI8, O> codec8,
	@NotNull ConfigurableCodec<CI9, O> codec9,
	@NotNull ConfigurableCodec<CI10, O> codec10,
	@NotNull ConfigurableCodec<CI11, O> codec11,
	@NotNull ConfigurableCodec<CI12, O> codec12,
	@NotNull ConfigurableCodec<CI13, O> codec13,
	@NotNull ConfigurableCodec<CI14, O> codec14
) {
	
	public CodecGrouper14 {
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
	}
	
	@SuppressWarnings({ "DuplicatedCode", "UnqualifiedFieldAccess" })
	public @NotNull Codec<O> create(@NotNull CodecGroupingFunction14<CI1, CI2, CI3, CI4, CI5, CI6, CI7, CI8, CI9, CI10, CI11, CI12, CI13, CI14, O> function) {
		Objects.requireNonNull(function, "Codec grouping function must not be null");
		return new Codec<>() {
			
			@Override
			public <R> @NotNull Result<R> encodeStart(@NotNull TypeProvider<R> provider, @NotNull R current, @Nullable O value) {
				Objects.requireNonNull(provider, "Type provider must not be null");
				Objects.requireNonNull(current, "Current value must not be null");
				if (value == null) {
					return Result.error("Unable to encode null value with '" + this);
				}
				Result<R> mergedMap = provider.merge(current, provider.createMap());
				if (mergedMap.isError()) {
					return Result.error("Unable to encode '" + value + "' with '" + this + "': " + mergedMap.errorOrThrow());
				}
				R map = mergedMap.orThrow();
				Result<R> encoded1 = codec1.encodeNamedStart(provider, map, value);
				if (encoded1.isError()) {
					return Result.error("Unable to encode component of '" + value + "' with '" + codec1 + "': " + encoded1.errorOrThrow());
				}
				Result<R> encoded2 = codec2.encodeNamedStart(provider, map, value);
				if (encoded2.isError()) {
					return Result.error("Unable to encode component of '" + value + "' with '" + codec2 + "': " + encoded2.errorOrThrow());
				}
				Result<R> encoded3 = codec3.encodeNamedStart(provider, map, value);
				if (encoded3.isError()) {
					return Result.error("Unable to encode component of '" + value + "' with '" + codec3 + "': " + encoded3.errorOrThrow());
				}
				Result<R> encoded4 = codec4.encodeNamedStart(provider, map, value);
				if (encoded4.isError()) {
					return Result.error("Unable to encode component of '" + value + "' with '" + codec4 + "': " + encoded4.errorOrThrow());
				}
				Result<R> encoded5 = codec5.encodeNamedStart(provider, map, value);
				if (encoded5.isError()) {
					return Result.error("Unable to encode component of '" + value + "' with '" + codec5 + "': " + encoded5.errorOrThrow());
				}
				Result<R> encoded6 = codec6.encodeNamedStart(provider, map, value);
				if (encoded6.isError()) {
					return Result.error("Unable to encode component of '" + value + "' with '" + codec6 + "': " + encoded6.errorOrThrow());
				}
				Result<R> encoded7 = codec7.encodeNamedStart(provider, map, value);
				if (encoded7.isError()) {
					return Result.error("Unable to encode component of '" + value + "' with '" + codec7 + "': " + encoded7.errorOrThrow());
				}
				Result<R> encoded8 = codec8.encodeNamedStart(provider, map, value);
				if (encoded8.isError()) {
					return Result.error("Unable to encode component of '" + value + "' with '" + codec8 + "': " + encoded8.errorOrThrow());
				}
				Result<R> encoded9 = codec9.encodeNamedStart(provider, map, value);
				if (encoded9.isError()) {
					return Result.error("Unable to encode component of '" + value + "' with '" + codec9 + "': " + encoded9.errorOrThrow());
				}
				Result<R> encoded10 = codec10.encodeNamedStart(provider, map, value);
				if (encoded10.isError()) {
					return Result.error("Unable to encode component of '" + value + "' with '" + codec10 + "': " + encoded10.errorOrThrow());
				}
				Result<R> encoded11 = codec11.encodeNamedStart(provider, map, value);
				if (encoded11.isError()) {
					return Result.error("Unable to encode component of '" + value + "' with '" + codec11 + "': " + encoded11.errorOrThrow());
				}
				Result<R> encoded12 = codec12.encodeNamedStart(provider, map, value);
				if (encoded12.isError()) {
					return Result.error("Unable to encode component of '" + value + "' with '" + codec12 + "': " + encoded12.errorOrThrow());
				}
				Result<R> encoded13 = codec13.encodeNamedStart(provider, map, value);
				if (encoded13.isError()) {
					return Result.error("Unable to encode component of '" + value + "' with '" + codec13 + "': " + encoded13.errorOrThrow());
				}
				Result<R> encoded14 = codec14.encodeNamedStart(provider, map, value);
				if (encoded14.isError()) {
					return Result.error("Unable to encode component of '" + value + "' with '" + codec14 + "': " + encoded14.errorOrThrow());
				}
				return Result.success(map);
			}
			
			@Override
			public @NotNull <R> Result<O> decodeStart(@NotNull TypeProvider<R> provider, @Nullable R value) {
				Objects.requireNonNull(provider, "Type provider must not be null");
				if (value == null) {
					return Result.error("Unable to decode null value using '" + this);
				}
				Result<Map<String, R>> decodedMap = provider.getMap(value);
				if (decodedMap.isError()) {
					return Result.error("Unable to decode '" + value + "' using '" + this + "': " + decodedMap.errorOrThrow());
				}
				Result<CI1> decoded1 = codec1.decodeNamedStart(provider, value);
				if (decoded1.isError()) {
					return Result.error("Unable to decode component of '" + value + "' using '" + codec1 + "': " + decoded1.errorOrThrow());
				}
				Result<CI2> decoded2 = codec2.decodeNamedStart(provider, value);
				if (decoded2.isError()) {
					return Result.error("Unable to decode component of '" + value + "' using '" + codec2 + "': " + decoded2.errorOrThrow());
				}
				Result<CI3> decoded3 = codec3.decodeNamedStart(provider, value);
				if (decoded3.isError()) {
					return Result.error("Unable to decode component of '" + value + "' using '" + codec3 + "': " + decoded3.errorOrThrow());
				}
				Result<CI4> decoded4 = codec4.decodeNamedStart(provider, value);
				if (decoded4.isError()) {
					return Result.error("Unable to decode component of '" + value + "' using '" + codec4 + "': " + decoded4.errorOrThrow());
				}
				Result<CI5> decoded5 = codec5.decodeNamedStart(provider, value);
				if (decoded5.isError()) {
					return Result.error("Unable to decode component of '" + value + "' using '" + codec5 + "': " + decoded5.errorOrThrow());
				}
				Result<CI6> decoded6 = codec6.decodeNamedStart(provider, value);
				if (decoded6.isError()) {
					return Result.error("Unable to decode component of '" + value + "' using '" + codec6 + "': " + decoded6.errorOrThrow());
				}
				Result<CI7> decoded7 = codec7.decodeNamedStart(provider, value);
				if (decoded7.isError()) {
					return Result.error("Unable to decode component of '" + value + "' using '" + codec7 + "': " + decoded7.errorOrThrow());
				}
				Result<CI8> decoded8 = codec8.decodeNamedStart(provider, value);
				if (decoded8.isError()) {
					return Result.error("Unable to decode component of '" + value + "' using '" + codec8 + "': " + decoded8.errorOrThrow());
				}
				Result<CI9> decoded9 = codec9.decodeNamedStart(provider, value);
				if (decoded9.isError()) {
					return Result.error("Unable to decode component of '" + value + "' using '" + codec9 + "': " + decoded9.errorOrThrow());
				}
				Result<CI10> decoded10 = codec10.decodeNamedStart(provider, value);
				if (decoded10.isError()) {
					return Result.error("Unable to decode component of '" + value + "' using '" + codec10 + "': " + decoded10.errorOrThrow());
				}
				Result<CI11> decoded11 = codec11.decodeNamedStart(provider, value);
				if (decoded11.isError()) {
					return Result.error("Unable to decode component of '" + value + "' using '" + codec11 + "': " + decoded11.errorOrThrow());
				}
				Result<CI12> decoded12 = codec12.decodeNamedStart(provider, value);
				if (decoded12.isError()) {
					return Result.error("Unable to decode component of '" + value + "' using '" + codec12 + "': " + decoded12.errorOrThrow());
				}
				Result<CI13> decoded13 = codec13.decodeNamedStart(provider, value);
				if (decoded13.isError()) {
					return Result.error("Unable to decode component of '" + value + "' using '" + codec13 + "': " + decoded13.errorOrThrow());
				}
				Result<CI14> decoded14 = codec14.decodeNamedStart(provider, value);
				if (decoded14.isError()) {
					return Result.error("Unable to decode component of '" + value + "' using '" + codec14 + "': " + decoded14.errorOrThrow());
				}
				return Result.success(function.create(
					decoded1.orThrow(), decoded2.orThrow(), decoded3.orThrow(), decoded4.orThrow(), decoded5.orThrow(), decoded6.orThrow(), decoded7.orThrow(), decoded8.orThrow(),
					decoded9.orThrow(), decoded10.orThrow(), decoded11.orThrow(), decoded12.orThrow(), decoded13.orThrow(), decoded14.orThrow()
				));
			}
			
			@Override
			public String toString() {
				return "Codec[" + codec1 + ", " + codec2 + ", " + codec3 + ", " + codec4 + ", " + codec5 + ", " + codec6 + ", " + codec7 + ", " + codec8 + ", "
					+ codec9 + ", " + codec10 + ", " + codec11 + ", " + codec12 + ", " + codec13 + ", " + codec14 + ", " + "]";
			}
		};
	}
}
