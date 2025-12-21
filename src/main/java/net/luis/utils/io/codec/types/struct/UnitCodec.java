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

package net.luis.utils.io.codec.types.struct;

import net.luis.utils.io.codec.AbstractCodec;
import net.luis.utils.io.codec.provider.TypeProvider;
import net.luis.utils.util.result.Result;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * A codec for encoding and decoding unit values.<br>
 * This codec does not encode or decode any value, it is used to represent a unit value.<br>
 * <p>
 *     The unit codec can be used to represent a value that is not encoded or decoded.<br>
 *     This is useful when a codec is required to encode or decode a value, but the value is not needed.
 * </p>
 * <p>
 *     The unit codec will always return the same value when encoding and decoding.<br>
 *     During encoding, the current value is returned.<br>
 *     During decoding, the unit value is returned which is created by a supplier.
 * </p>
 *
 * @author Luis-St
 *
 * @param <C> The type of the unit value
 */
public class UnitCodec<C> extends AbstractCodec<C, Object> {
	
	/**
	 * The supplier used to create the unit value.<br>
	 */
	private final Supplier<C> supplier;
	
	/**
	 * Constructs a new unit codec using the given supplier for the unit value.<br>
	 *
	 * @param supplier The supplier used to create the unit value
	 * @throws NullPointerException If the supplier is null
	 */
	public UnitCodec(@NonNull Supplier<C> supplier) {
		this.supplier = Objects.requireNonNull(supplier, "Unit supplier must not be null");
	}
	
	@Override
	public <R> @NonNull Result<R> encodeStart(@Nullable TypeProvider<R> provider, @NonNull R current, @Nullable C value) {
		Objects.requireNonNull(current, "Current value must not be null");
		return Result.success(current);
	}
	
	@Override
	public <R> @NonNull Result<C> decodeStart(@Nullable TypeProvider<R> provider, @Nullable R current, @Nullable R value) {
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
