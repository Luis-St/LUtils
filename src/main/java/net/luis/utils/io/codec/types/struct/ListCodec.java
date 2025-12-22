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
import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.constraint.SizeConstraint;
import net.luis.utils.io.codec.constraint.config.SizeConstraintConfig;
import net.luis.utils.io.codec.provider.TypeProvider;
import net.luis.utils.util.result.Result;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A codec for encoding and decoding lists of elements.<br>
 * This codec uses another codec to encode and decode the elements of the list.<br>
 *
 * @author Luis-St
 *
 * @param <C> The type of elements in the list
 */
public class ListCodec<C> extends AbstractCodec<List<C>, SizeConstraintConfig> implements SizeConstraint<List<C>, ListCodec<C>> {
	
	/**
	 * The codec used to encode and decode the elements of the list.<br>
	 */
	private final Codec<C> codec;
	
	/**
	 * Constructs a new list codec using the given codec for the elements.<br>
	 *
	 * @param codec The codec for the elements
	 * @throws NullPointerException If the codec is null
	 */
	public ListCodec(@NonNull Codec<C> codec) {
		this.codec = Objects.requireNonNull(codec, "Codec must not be null");
	}
	
	/**
	 * Constructs a new list codec using the given codec for the elements and the given size constraint configuration.<br>
	 *
	 * @param codec The codec for the elements
	 * @param constraintConfig The size constraint configuration
	 * @throws NullPointerException If the codec is null
	 */
	public ListCodec(@NonNull Codec<C> codec, @NonNull SizeConstraintConfig constraintConfig) {
		this.codec = Objects.requireNonNull(codec, "Codec must not be null");
		super(constraintConfig);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public @NonNull Class<List<C>> getType() {
		return (Class<List<C>>) (Class<?>) List.class;
	}
	
	@Override
	public @NonNull ListCodec<C> applyConstraint(@NonNull SizeConstraintConfig config) {
		return new ListCodec<>(this.codec, config);
	}
	
	@Override
	protected @NonNull Result<Void> checkConstraints(@NonNull List<C> value) {
		Objects.requireNonNull(value, "Value must not be null");
		
		Result<Void> constraintResult = this.getConstraintConfig().map(config -> config.matches(value.size())).orElseGet(Result::success);
		if (constraintResult.isError()) {
			return Result.error("List " + value + " does not meet constraints: " + constraintResult.errorOrThrow());
		}
		
		return Result.success();
	}
	
	@Override
	@SuppressWarnings("DuplicatedCode")
	public <R> @NonNull Result<R> encodeStart(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable List<C> value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			return Result.error("Unable to encode null value as list using '" + this + "'");
		}
		
		Result<Void> constraintResult = this.checkConstraints(value);
		if (constraintResult.isError()) {
			return Result.error("Unable to encode list using '" + this + "': " + constraintResult.errorOrThrow());
		}
		
		List<R> elements = new ArrayList<>();
		List<String> errors = new ArrayList<>();
		for (int i = 0; i < value.size(); i++) {
			Result<R> result = this.codec.encodeStart(provider, provider.empty(), value.get(i));
			
			if (result.hasValue()) {
				R encodedValue = result.resultOrThrow();
				if (provider.getEmpty(encodedValue).isError()) {
					elements.add(encodedValue);
				}
			}
			if (result.hasError()) {
				errors.add("Index " + i + ": " + result.errorOrThrow());
			}
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
	public <R> @NonNull Result<List<C>> decodeStart(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable R value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			return Result.error("Unable to decode null value as list using'" + this + "'");
		}
		
		Result<List<R>> decoded = provider.getList(value);
		if (decoded.isError()) {
			return Result.error("Unable to decode list using '" + this + "': " + decoded.errorOrThrow());
		}
		List<Result<C>> results = decoded.resultOrThrow().stream().map(element -> this.codec.decodeStart(provider, value, element)).toList();
		
		List<C> elements = new ArrayList<>();
		List<String> errors = new ArrayList<>();
		for (int i = 0; i < results.size(); i++) {
			Result<C> result = results.get(i);
			if (result.hasValue()) {
				elements.add(result.resultOrThrow());
			}
			if (result.hasError()) {
				errors.add("Index " + i + ": " + result.errorOrThrow());
			}
		}
		
		if (elements.isEmpty() && !errors.isEmpty()) {
			return Result.error("Unable to decode any elements of the list using '" + this + "': " + errors.stream().collect(Collectors.joining("\n - ", "", "\n")));
		}
		Result<Void> constraintResult = this.checkConstraints(elements);
		if (constraintResult.isError()) {
			return Result.error("Unable to decode list using '" + this + "': " + constraintResult.errorOrThrow());
		}
		
		if (errors.isEmpty()) {
			return Result.success(elements);
		}
		return Result.partial(elements, "Decoded " + elements.size() + " of " + results.size() + " elements successfully:", errors);
	}
	
	//region Object overrides
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof ListCodec<?> listCodec)) return false;
		
		return this.codec.equals(listCodec.codec);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.codec);
	}
	
	@Override
	public String toString() {
		return this.getConstraintConfig().map(sizeConstraintConfig -> {
			return "ConstrainedListCodec[" + this.codec + ",constraints=" + sizeConstraintConfig + "]";
		}).orElse("ListCodec[" + this.codec + "]");
	}
	//endregion
}
