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

import net.luis.utils.io.codec.provider.TypeProvider;
import net.luis.utils.util.Result;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Function;

/**
 *
 * @author Luis-St
 *
 */

public class ConfiguredCodec<C, O> {
	
	private final Codec<C> codec;
	private final Function<O, C> getter;
	
	ConfiguredCodec(@NotNull Codec<C> codec, @NotNull Function<O, C> getter) {
		this.codec = Objects.requireNonNull(codec, "Codec must not be null");
		this.getter = Objects.requireNonNull(getter, "Getter must not be null");
	}
	
	public <R> @NotNull Result<R> encodeStart(@NotNull TypeProvider<R> provider, @NotNull R current, @Nullable O object) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (object == null) {
			return Result.error("Unable to encode component because the component can not be retrieved from a null object");
		}
		return this.codec.encodeStart(provider, current, this.getter.apply(object));
	}
	
	public <R> @NotNull Result<C> decodeStart(@NotNull TypeProvider<R> provider, @Nullable R value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		return this.codec.decodeStart(provider, value);
	}
	
	//region Object overrides
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof ConfiguredCodec<?, ?> that)) return false;
		
		if (!this.codec.equals(that.codec)) return false;
		return Objects.equals(this.getter, that.getter);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.codec);
	}
	
	@Override
	public String toString() {
		return "ConfigurableCodec[" + this.codec + "]";
	}
	//endregion
}
