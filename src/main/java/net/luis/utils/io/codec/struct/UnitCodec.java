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

package net.luis.utils.io.codec.struct;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.provider.TypeProvider;
import net.luis.utils.util.Result;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Supplier;

/**
 *
 * @author Luis-St
 *
 */

public class UnitCodec<C> implements Codec<C> {
	
	private final Supplier<C> supplier;
	
	public UnitCodec(@NotNull Supplier<C> supplier) {
		this.supplier = Objects.requireNonNull(supplier, "Unit supplier must not be null");
	}
	
	@Override
	public <R> @NotNull Result<R> encodeStart(@Nullable TypeProvider<R> provider, @NotNull R current, @Nullable C value) {
		Objects.requireNonNull(current, "Current value must not be null");
		return Result.success(current);
	}
	
	@Override
	public <R> @NotNull Result<C> decodeStart(@Nullable TypeProvider<R> provider, @Nullable R value) {
		return Result.success(this.supplier.get());
	}
	
	//region Object overrides
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof UnitCodec<?> unitCodec)) return false;
		
		return this.supplier.equals(unitCodec.supplier);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.supplier);
	}
	
	@Override
	public String toString() {
		return "unit codec";
	}
	//endregion
}
