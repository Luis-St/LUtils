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

package net.luis.utils.io.codec.types.array;

import net.luis.utils.io.codec.*;
import net.luis.utils.io.codec.constraint.config.collection.PrimitiveArrayConstraintConfig;
import net.luis.utils.io.codec.constraint.merged.collection.PrimitiveArrayConstraint;
import net.luis.utils.io.codec.provider.TypeProvider;
import net.luis.utils.util.result.Result;
import org.apache.commons.lang3.ArrayUtils;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.function.UnaryOperator;

/**
 * Internal codec implementation for double arrays.<br>
 * Uses a list of doubles as an internal representation.<br>
 *
 * @author Luis-St
 */
public class DoubleArrayCodec extends AbstractCodec<double[], PrimitiveArrayConstraintConfig<double[]>> implements PrimitiveArrayConstraint<double[], DoubleArrayCodec> {
	
	/**
	 * The internal codec that handles the conversion between a list of doubles and the array representation.<br>
	 */
	private final Codec<List<Double>> internalCodec = Codecs.DOUBLE.list();
	
	/**
	 * Constructs a new double array codec.<br>
	 */
	public DoubleArrayCodec() {}
	
	/**
	 * Constructs a new double array codec with the specified length constraint configuration.<br>
	 *
	 * @param config The constraint configuration
	 * @throws NullPointerException If the constraint config is null
	 */
	private DoubleArrayCodec(@NonNull PrimitiveArrayConstraintConfig<double[]> config) {
		super(config);
	}
	
	@Override
	public @NonNull DoubleArrayCodec apply(@NonNull UnaryOperator<PrimitiveArrayConstraintConfig<double[]>> configModifier) {
		Objects.requireNonNull(configModifier, "Config modifier must not be null");
		
		return new DoubleArrayCodec(
			configModifier.apply(this.getConstraintConfig().orElse(PrimitiveArrayConstraintConfig.doubleArray()))
		);
	}
	
	@Override
	public <R> @NonNull Result<R> encodeStart(@NonNull TypeProvider<R> provider, @NonNull R current, double @Nullable [] value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			return Result.error("Unable to encode null as double array using '" + this + "'");
		}
		
		Result<Void> constraintResult = this.checkConstraints(value);
		if (constraintResult.isError()) {
			return Result.error(constraintResult.errorOrThrow());
		}
		return this.internalCodec.encodeStart(provider, current, Arrays.asList(ArrayUtils.toObject(value)));
	}
	
	@Override
	public <R> @NonNull Result<double[]> decodeStart(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable R value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			return Result.error("Unable to decode null value as double array using '" + this + "'");
		}
		
		Result<List<Double>> result = this.internalCodec.decodeStart(provider, current, value);
		if (result.isError()) {
			return Result.error(result.errorOrThrow());
		}
		
		List<Double> list = result.resultOrThrow();
		double[] array = ArrayUtils.toPrimitive(list.toArray(Double[]::new));
		
		Result<Void> constraintResult = this.checkConstraints(array);
		if (constraintResult.isError()) {
			return Result.error(constraintResult.errorOrThrow());
		}
		return Result.success(array);
	}
	
	@Override
	public String toString() {
		return this.getConstraintConfig().map(config -> {
			return "ConstrainedDoubleArrayCodec[constraints=" + config + "]";
		}).orElse("DoubleArrayCodec");
	}
}
