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

package net.luis.utils.data.codec.nested;

import net.luis.utils.data.codec.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

/**
 *
 * @author Luis-St
 *
 */

public class OptionalCodec<T> implements NestedCodec<Optional<T>> {
	
	private final Codec<T> innerCodec;
	
	public OptionalCodec(@NotNull Codec<T> innerCodec) {
		this.innerCodec = Objects.requireNonNull(innerCodec, "Inner codec must not be null");
	}
	
	@Override
	public <X> @NotNull ParserCache<X> isValid(@Nullable String value) {
		return null;
	}
	
	@Override
	public @NotNull <X> Optional<T> decode(@NotNull ParserCache<X> cache) {
		return Optional.empty();
	}
	
	@Override
	public @NotNull String encode(@NotNull Optional<T> optional) {
		Objects.requireNonNull(optional, "Optional value must not be null");
		return optional.map(this.innerCodec::encode).orElse("");
	}
	
	@Override
	public @NotNull Optional<T> getDefaultValue() {
		return Optional.empty();
	}
	
	@Override
	public boolean isNested() {
		return true;
	}
}
