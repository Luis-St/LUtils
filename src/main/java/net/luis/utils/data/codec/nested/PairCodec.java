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
import net.luis.utils.util.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 *
 * @author Luis-St
 *
 */

public class PairCodec<F, S> implements NestedCodec<Pair<F, S>> {
	
	private final Codec<F> firstCodec;
	private final Codec<S> secondCodec;
	
	public PairCodec(@NotNull Codec<F> firstCodec, @NotNull Codec<S> secondCodec) {
		this.firstCodec = Objects.requireNonNull(firstCodec, "First codec must not be null");
		this.secondCodec = Objects.requireNonNull(secondCodec, "Second codec must not be null");
	}
	
	@Override
	public <X> @NotNull ParserCache<X> isValid(@Nullable String value) {
		return null;
	}
	
	@Override
	public @NotNull <X> Pair<F, S> decode(@NotNull ParserCache<X> cache) {
		return null;
	}
	
	@Override
	public @NotNull String encode(@NotNull Pair<F, S> value) {
		StringBuilder builder = new StringBuilder("(");
		builder.append(this.firstCodec.encode(value.getFirst()));
		builder.append(", ");
		builder.append(this.secondCodec.encode(value.getSecond()));
		return builder.append(")").toString();
	}
	
	@Override
	public @NotNull Pair<F, S> getDefaultValue() {
		return Pair.of(this.firstCodec.getDefaultValue(), this.secondCodec.getDefaultValue());
	}
	
	@Override
	public boolean isNested() {
		return true;
	}
}
