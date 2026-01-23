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

package net.luis.utils.io.codec.types.primitive;

import net.luis.utils.io.codec.AbstractCodec;
import net.luis.utils.io.codec.constraint.merged.CharacterConstraint;
import net.luis.utils.io.codec.constraint.config.CharacterConstraintConfig;
import net.luis.utils.io.codec.provider.TypeProvider;
import net.luis.utils.util.result.Result;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.function.UnaryOperator;

/**
 * Internal codec implementation for characters.<br>
 *
 * @author Luis-St
 */
public class CharacterCodec extends AbstractCodec<Character, CharacterConstraintConfig> implements CharacterConstraint<CharacterCodec> {
	
	/**
	 * Constructs a new character codec.<br>
	 */
	public CharacterCodec() {}
	
	/**
	 * Constructs a new character codec with the given configuration.<br>
	 *
	 * @param config The constraint configuration
	 * @throws NullPointerException If the config is null
	 */
	private CharacterCodec(@NonNull CharacterConstraintConfig config) {
		super(config);
	}
	
	@Override
	public @NonNull CharacterCodec apply(@NonNull UnaryOperator<CharacterConstraintConfig> configModifier) {
		Objects.requireNonNull(configModifier, "Config modifier must not be null");
		
		return new CharacterCodec(
			configModifier.apply(this.getConstraintConfig().orElse(CharacterConstraintConfig.UNCONSTRAINED))
		);
	}
	
	@Override
	public <R> @NonNull Result<R> encodeStart(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable Character value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		if (value == null) {
			return Result.error("Unable to encode null as character using '" + this + "'");
		}
		
		Result<Void> constraintResult = this.checkConstraints(value);
		if (constraintResult.isError()) {
			return Result.error(constraintResult.errorOrThrow());
		}
		return provider.createString(String.valueOf(value));
	}
	
	@Override
	public @NonNull Result<String> encodeKey(@NonNull Character key) {
		Objects.requireNonNull(key, "Key must not be null");
		
		Result<Void> constraintResult = this.checkConstraints(key);
		if (constraintResult.isError()) {
			return Result.error(constraintResult.errorOrThrow());
		}
		return Result.success(String.valueOf(key));
	}
	
	@Override
	public <R> @NonNull Result<Character> decodeStart(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable R value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			return Result.error("Unable to decode null value as character using '" + this + "'");
		}
		
		Result<String> result = provider.getString(value);
		if (result.isError()) {
			return Result.error("Unable to decode value as character from a string value using '" + this + "': " + result.errorOrThrow());
		}
		
		String str = result.resultOrThrow();
		if (str.length() != 1) {
			return Result.error("Unable to decode value '" + str + "' as character using '" + this + "': String must have exactly one character to decode as character");
		}
		
		char ch = str.charAt(0);
		Result<Void> constraintResult = this.checkConstraints(ch);
		if (constraintResult.isError()) {
			return Result.error(constraintResult.errorOrThrow());
		}
		return Result.success(ch);
	}
	
	@Override
	public @NonNull Result<Character> decodeKey(@NonNull String key) {
		Objects.requireNonNull(key, "Key must not be null");
		
		if (key.length() != 1) {
			return Result.error("Unable to decode key '" + key + "' as character using '" + this + "': Key must have exactly one character to decode as character");
		}
		
		char ch = key.charAt(0);
		Result<Void> constraintResult = this.checkConstraints(ch);
		if (constraintResult.isError()) {
			return Result.error(constraintResult.errorOrThrow());
		}
		return Result.success(ch);
	}
	
	@Override
	public String toString() {
		return "CharacterCodec";
	}
}
