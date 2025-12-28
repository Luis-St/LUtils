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

package net.luis.utils.io.codec.types.struct.collection;

import net.luis.utils.io.codec.AbstractCodec;
import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.constraint.SizeConstraint;
import net.luis.utils.io.codec.constraint.config.SizeConstraintConfig;
import net.luis.utils.io.codec.provider.TypeProvider;
import net.luis.utils.util.result.Result;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.function.UnaryOperator;

/**
 * Internal codec implementation for sets.<br>
 * Uses a list representation internally and converts to/from a set.<br>
 *
 * @author Luis-St
 *
 * @param <E> The element type of the set
 */
public class SetCodec<E> extends AbstractCodec<Set<E>, SizeConstraintConfig> implements SizeConstraint<Set<E>, SetCodec<E>> {
	
	/**
	 * The codec used to encode and decode set elements.<br>
	 */
	private final Codec<E> codec;
	
	/**
	 * Constructs a new set codec.<br>
	 *
	 * @param codec The codec for set elements
	 * @throws NullPointerException If the element codec is null
	 */
	public SetCodec(@NonNull Codec<E> codec) {
		this.codec = Objects.requireNonNull(codec, "Element codec must not be null");
	}
	
	/**
	 * Constructs a new set codec using the given codec for the elements and the given size constraint configuration.<br>
	 *
	 * @param codec The codec for the elements
	 * @param constraintConfig The size constraint configuration
	 * @throws NullPointerException If the codec is null
	 */
	public SetCodec(@NonNull Codec<E> codec, @NonNull SizeConstraintConfig constraintConfig) {
		this.codec = Objects.requireNonNull(codec, "Element codec must not be null");
		super(constraintConfig);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public @NonNull Class<Set<E>> getType() {
		return (Class<Set<E>>) (Class<?>) Set.class;
	}
	
	@Override
	public @NonNull SetCodec<E> applyConstraint(@NonNull UnaryOperator<SizeConstraintConfig> configModifier) {
		Objects.requireNonNull(configModifier, "Config modifier must not be null");
		
		return new SetCodec<>(this.codec, configModifier.apply(
			this.getConstraintConfig().orElse(SizeConstraintConfig.UNCONSTRAINED)
		));
	}
	
	@Override
	protected @NonNull Result<Void> checkConstraints(@NonNull Set<E> value) {
		Objects.requireNonNull(value, "Value must not be null");
		
		Result<Void> constraintResult = this.getConstraintConfig().map(config -> config.matches(value.size())).orElseGet(Result::success);
		if (constraintResult.isError()) {
			return Result.error("Set " + value + " does not meet constraints: " + constraintResult.errorOrThrow());
		}
		return Result.success();
	}
	
	@Override
	@SuppressWarnings("DuplicatedCode")
	public <R> @NonNull Result<R> encodeStart(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable Set<E> value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			return Result.error("Unable to encode null value as set using '" + this + "'");
		}
		
		Result<Void> constraintResult = this.checkConstraints(value);
		if (constraintResult.isError()) {
			return Result.error("Unable to encode set using '" + this + "': " + constraintResult.errorOrThrow());
		}
		
		List<R> elements = new ArrayList<>();
		List<String> errors = new ArrayList<>();
		int i = 0;
		for (E element : value) {
			Result<R> result = this.codec.encodeStart(provider, provider.empty(), element);
			
			if (result.hasValue()) {
				R encodedValue = result.resultOrThrow();
				if (provider.getEmpty(encodedValue).isError()) {
					elements.add(encodedValue);
				}
			}
			if (result.hasError()) {
				errors.add("Index " + i + ": " + result.errorOrThrow());
			}
			i++;
		}
		
		Result<R> merged = provider.merge(current, provider.createList(elements));
		if (merged.isError()) {
			return Result.error(merged.errorOrThrow());
		}
		if (errors.isEmpty()) {
			return merged;
		}
		return Result.partial(merged.resultOrThrow(), "Encoded " + elements.size() + " of " + value.size() + " elements successfully:", errors);
	}
	
	@Override
	@SuppressWarnings("DuplicatedCode")
	public <R> @NonNull Result<Set<E>> decodeStart(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable R value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			return Result.error("Unable to decode null value as set using '" + this + "'");
		}
		
		Result<List<R>> decoded = provider.getList(value);
		if (decoded.isError()) {
			return Result.error("Unable to decode set using '" + this + "': " + decoded.errorOrThrow());
		}
		List<Result<E>> results = decoded.resultOrThrow().stream().map(element -> this.codec.decodeStart(provider, value, element)).toList();
		
		Set<E> elements = new LinkedHashSet<>();
		List<String> errors = new ArrayList<>();
		for (int i = 0; i < results.size(); i++) {
			Result<E> result = results.get(i);
			if (result.hasValue()) {
				elements.add(result.resultOrThrow());
			}
			if (result.hasError()) {
				errors.add("Index " + i + ": " + result.errorOrThrow());
			}
		}
		
		if (elements.isEmpty() && !errors.isEmpty()) {
			return Result.error("Unable to decode any elements of the set using '" + this + "': " + String.join("\n - ", errors));
		}
		Result<Void> constraintResult = this.checkConstraints(elements);
		if (constraintResult.isError()) {
			return Result.error("Unable to decode set using '" + this + "': " + constraintResult.errorOrThrow());
		}
		
		if (errors.isEmpty()) {
			return Result.success(elements);
		}
		return Result.partial(elements, "Decoded " + elements.size() + " of " + results.size() + " elements successfully:", errors);
	}
	
	//region Object overrides
	@Override
	public boolean equals(Object object) {
		if (!(object instanceof SetCodec<?> that)) return false;
		
		return this.codec.equals(that.codec);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.codec);
	}
	
	@Override
	public String toString() {
		return this.getConstraintConfig().map(sizeConstraintConfig -> {
			return "ConstrainedSetCodec[" + this.codec + ",constraints=" + sizeConstraintConfig + "]";
		}).orElse("SetCodec[" + this.codec + "]");
	}
	//endregion
}
