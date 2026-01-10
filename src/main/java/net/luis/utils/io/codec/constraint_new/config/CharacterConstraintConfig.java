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

package net.luis.utils.io.codec.constraint_new.config;

import net.luis.utils.io.codec.constraint_new.CharacterConstraint;
import net.luis.utils.io.codec.constraint_new.Constraint;
import net.luis.utils.io.codec.constraint_new.core.Unit;
import net.luis.utils.util.Pair;
import net.luis.utils.util.result.Result;
import org.jspecify.annotations.NonNull;

import java.util.*;

/**
 * Configuration record for character constraints.<br>
 * <p>
 *     This record stores the constraint values for {@link CharacterConstraint}.<br>
 *     It includes base constraints, comparable constraints, and character classification flags.
 * </p>
 *
 * @author Luis-St
 *
 * @param equalTo The character equality constraint as a pair of (value, negated) where negated=false means equalTo and negated=true means notEqualTo
 * @param in The character set constraint as a pair of (values, negated) where negated=false means in and negated=true means notIn
 * @param min The minimum character constraint as a pair of (value, inclusive)
 * @param max The maximum character constraint as a pair of (value, inclusive)
 * @param letter If present, requires the character to be a letter
 * @param digit If present, requires the character to be a digit
 * @param alphanumeric If present, requires the character to be alphanumeric
 * @param whitespace If present, requires the character to be whitespace
 * @param punctuation If present, requires the character to be punctuation
 * @param symbol If present, requires the character to be a symbol
 * @param control If present, requires the character to be a control character
 * @param upperCase If present, requires the character to be upper case
 * @param lowerCase If present, requires the character to be lower case
 * @param ascii If present, requires the character to be an ASCII character (0-127)
 * @param latin1 If present, requires the character to be a Latin-1 character (0-255)
 * @param custom A custom constraint implementation
 */
public record CharacterConstraintConfig(
	@NonNull Optional<Pair<Character, Boolean>> equalTo,
	@NonNull Optional<Pair<Set<Character>, Boolean>> in,
	@NonNull Optional<Pair<Character, Boolean>> min,
	@NonNull Optional<Pair<Character, Boolean>> max,
	@NonNull Optional<Unit> letter,
	@NonNull Optional<Unit> digit,
	@NonNull Optional<Unit> alphanumeric,
	@NonNull Optional<Unit> whitespace,
	@NonNull Optional<Unit> punctuation,
	@NonNull Optional<Unit> symbol,
	@NonNull Optional<Unit> control,
	@NonNull Optional<Unit> upperCase,
	@NonNull Optional<Unit> lowerCase,
	@NonNull Optional<Unit> ascii,
	@NonNull Optional<Unit> latin1,
	@NonNull Optional<Constraint<Character>> custom
) implements ConstraintConfig<Character> {
	
	/**
	 * Set of punctuation character types used for classification.<br>
	 */
	private static final Set<Byte> PUNCTUATION_TYPES = Set.of(
		Character.CONNECTOR_PUNCTUATION,
		Character.DASH_PUNCTUATION,
		Character.START_PUNCTUATION,
		Character.END_PUNCTUATION,
		Character.INITIAL_QUOTE_PUNCTUATION,
		Character.FINAL_QUOTE_PUNCTUATION,
		Character.OTHER_PUNCTUATION
	);
	
	/**
	 * Set of symbol character types used for classification.<br>
	 */
	private static final Set<Byte> SYMBOL_TYPES = Set.of(
		Character.MATH_SYMBOL,
		Character.CURRENCY_SYMBOL,
		Character.MODIFIER_SYMBOL,
		Character.OTHER_SYMBOL
	);
	
	/**
	 * An unconstrained character configuration with no constraints applied.<br>
	 */
	public static final CharacterConstraintConfig UNCONSTRAINED = new CharacterConstraintConfig(
		Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
		Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
	);
	
	/**
	 * Constructs a new character constraint config with the specified parameters.<br>
	 *
	 * @param equalTo The character equality constraint as a pair of (value, negated) where negated=false means equalTo and negated=true means notEqualTo
	 * @param in The character set constraint as a pair of (values, negated) where negated=false means in and negated=true means notIn
	 * @param min The minimum character constraint as a pair of (value, inclusive)
	 * @param max The maximum character constraint as a pair of (value, inclusive)
	 * @param letter If present, requires the character to be a letter
	 * @param digit If present, requires the character to be a digit
	 * @param alphanumeric If present, requires the character to be alphanumeric
	 * @param whitespace If present, requires the character to be whitespace
	 * @param punctuation If present, requires the character to be punctuation
	 * @param symbol If present, requires the character to be a symbol
	 * @param control If present, requires the character to be a control character
	 * @param upperCase If present, requires the character to be upper case
	 * @param lowerCase If present, requires the character to be lower case
	 * @param ascii If present, requires the character to be an ASCII character (0-127)
	 * @param latin1 If present, requires the character to be a Latin-1 character (0-255)
	 * @param custom A custom constraint implementation
	 * @throws NullPointerException If any optional field is null
	 * @throws IllegalArgumentException If the 'in' constraint set is empty when present
	 * @throws IllegalArgumentException If min is greater than max when both are present
	 * @throws IllegalArgumentException If min equals max with at least one exclusive bound when both are present
	 * @throws IllegalArgumentException If both 'upperCase' and 'lowerCase' constraints are present
	 */
	public CharacterConstraintConfig {
		Objects.requireNonNull(equalTo, "Optional for 'equal to' constraint must not be null");
		Objects.requireNonNull(in, "Optional for 'in' constraint must not be null");
		Objects.requireNonNull(min, "Optional for 'min' constraint must not be null");
		Objects.requireNonNull(max, "Optional for 'max' constraint must not be null");
		Objects.requireNonNull(letter, "Optional for 'letter' constraint must not be null");
		Objects.requireNonNull(digit, "Optional for 'digit' constraint must not be null");
		Objects.requireNonNull(alphanumeric, "Optional for 'alphanumeric' constraint must not be null");
		Objects.requireNonNull(whitespace, "Optional for 'whitespace' constraint must not be null");
		Objects.requireNonNull(punctuation, "Optional for 'punctuation' constraint must not be null");
		Objects.requireNonNull(symbol, "Optional for 'symbol' constraint must not be null");
		Objects.requireNonNull(control, "Optional for 'control' constraint must not be null");
		Objects.requireNonNull(upperCase, "Optional for 'upper case' constraint must not be null");
		Objects.requireNonNull(lowerCase, "Optional for 'lower case' constraint must not be null");
		Objects.requireNonNull(ascii, "Optional for 'ascii' constraint must not be null");
		Objects.requireNonNull(latin1, "Optional for 'latin1' constraint must not be null");
		Objects.requireNonNull(custom, "Optional for 'custom' constraint must not be null");
		
		if (in.isPresent() && in.get().getFirst().isEmpty()) {
			throw new IllegalArgumentException("In constraint set must not be empty when present");
		}
		
		if (min.isPresent() && max.isPresent()) {
			if (min.get().getFirst().compareTo(max.get().getFirst()) > 0) {
				throw new IllegalArgumentException("Min must be less than or equal to max when both are present, but got " + min.get().getFirst() + " > " + max.get().getFirst());
			}
			if (min.get().getFirst().compareTo(max.get().getFirst()) == 0 && (!min.get().getSecond() || !max.get().getSecond())) {
				throw new IllegalArgumentException("Min and max are equal but at least one bound is exclusive when both are present");
			}
		}
		
		if (upperCase.isPresent() && lowerCase.isPresent()) {
			throw new IllegalArgumentException("Upper case and lower case are mutually exclusive");
		}
	}
	
	//region With methods
	
	/**
	 * Creates a new config with the specified equal-to constraint.<br>
	 *
	 * @param value The exact character value that should be matched
	 * @return A new config with the constraint applied
	 */
	public @NonNull CharacterConstraintConfig withEqualTo(char value) {
		return new CharacterConstraintConfig(Optional.of(Pair.of(value, false)), this.in, this.min, this.max, this.letter, this.digit, this.alphanumeric, this.whitespace, this.punctuation, this.symbol, this.control, this.upperCase, this.lowerCase, this.ascii, this.latin1, this.custom);
	}
	
	/**
	 * Creates a new config with the specified not-equal-to constraint.<br>
	 *
	 * @param value The character value that should be excluded
	 * @return A new config with the constraint applied
	 */
	public @NonNull CharacterConstraintConfig withNotEqualTo(char value) {
		return new CharacterConstraintConfig(Optional.of(Pair.of(value, true)), this.in, this.min, this.max, this.letter, this.digit, this.alphanumeric, this.whitespace, this.punctuation, this.symbol, this.control, this.upperCase, this.lowerCase, this.ascii, this.latin1, this.custom);
	}
	
	/**
	 * Creates a new config with the specified inclusion constraint.<br>
	 *
	 * @param values The collection of characters that are allowed
	 * @return A new config with the constraint applied
	 */
	public @NonNull CharacterConstraintConfig withIn(@NonNull Collection<Character> values) {
		Objects.requireNonNull(values, "Values for 'in' constraint must not be null");
		return new CharacterConstraintConfig(this.equalTo, Optional.of(Pair.of(Set.copyOf(values), false)), this.min, this.max, this.letter, this.digit, this.alphanumeric, this.whitespace, this.punctuation, this.symbol, this.control, this.upperCase, this.lowerCase, this.ascii, this.latin1, this.custom);
	}
	
	/**
	 * Creates a new config with the specified exclusion constraint.<br>
	 *
	 * @param values The collection of characters that are not allowed
	 * @return A new config with the constraint applied
	 */
	public @NonNull CharacterConstraintConfig withNotIn(@NonNull Collection<Character> values) {
		Objects.requireNonNull(values, "Values for 'not in' constraint must not be null");
		return new CharacterConstraintConfig(this.equalTo, Optional.of(Pair.of(Set.copyOf(values), true)), this.min, this.max, this.letter, this.digit, this.alphanumeric, this.whitespace, this.punctuation, this.symbol, this.control, this.upperCase, this.lowerCase, this.ascii, this.latin1, this.custom);
	}
	
	/**
	 * Creates a new config with the specified greater-than constraint (exclusive).<br>
	 *
	 * @param value The threshold value (exclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull CharacterConstraintConfig withGreaterThan(char value) {
		return new CharacterConstraintConfig(this.equalTo, this.in, Optional.of(Pair.of(value, false)), this.max, this.letter, this.digit, this.alphanumeric, this.whitespace, this.punctuation, this.symbol, this.control, this.upperCase, this.lowerCase, this.ascii, this.latin1, this.custom);
	}
	
	/**
	 * Creates a new config with the specified greater-than-or-equal constraint (inclusive).<br>
	 *
	 * @param value The threshold value (inclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull CharacterConstraintConfig withGreaterThanOrEqual(char value) {
		return new CharacterConstraintConfig(this.equalTo, this.in, Optional.of(Pair.of(value, true)), this.max, this.letter, this.digit, this.alphanumeric, this.whitespace, this.punctuation, this.symbol, this.control, this.upperCase, this.lowerCase, this.ascii, this.latin1, this.custom);
	}
	
	/**
	 * Creates a new config with the specified less-than constraint (exclusive).<br>
	 *
	 * @param value The threshold value (exclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull CharacterConstraintConfig withLessThan(char value) {
		return new CharacterConstraintConfig(this.equalTo, this.in, this.min, Optional.of(Pair.of(value, false)), this.letter, this.digit, this.alphanumeric, this.whitespace, this.punctuation, this.symbol, this.control, this.upperCase, this.lowerCase, this.ascii, this.latin1, this.custom);
	}
	
	/**
	 * Creates a new config with the specified less-than-or-equal constraint (inclusive).<br>
	 *
	 * @param value The threshold value (inclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull CharacterConstraintConfig withLessThanOrEqual(char value) {
		return new CharacterConstraintConfig(this.equalTo, this.in, this.min, Optional.of(Pair.of(value, true)), this.letter, this.digit, this.alphanumeric, this.whitespace, this.punctuation, this.symbol, this.control, this.upperCase, this.lowerCase, this.ascii, this.latin1, this.custom);
	}
	
	/**
	 * Creates a new config with the specified between constraint (exclusive on both bounds).<br>
	 *
	 * @param min The minimum value (exclusive)
	 * @param max The maximum value (exclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull CharacterConstraintConfig withBetween(char min, char max) {
		return new CharacterConstraintConfig(this.equalTo, this.in, Optional.of(Pair.of(min, false)), Optional.of(Pair.of(max, false)), this.letter, this.digit, this.alphanumeric, this.whitespace, this.punctuation, this.symbol, this.control, this.upperCase, this.lowerCase, this.ascii, this.latin1, this.custom);
	}
	
	/**
	 * Creates a new config with the specified between constraint (inclusive on both bounds).<br>
	 *
	 * @param min The minimum value (inclusive)
	 * @param max The maximum value (inclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull CharacterConstraintConfig withBetweenOrEqual(char min, char max) {
		return new CharacterConstraintConfig(this.equalTo, this.in, Optional.of(Pair.of(min, true)), Optional.of(Pair.of(max, true)), this.letter, this.digit, this.alphanumeric, this.whitespace, this.punctuation, this.symbol, this.control, this.upperCase, this.lowerCase, this.ascii, this.latin1, this.custom);
	}
	
	/**
	 * Creates a new config with the letter constraint enabled.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull CharacterConstraintConfig withLetter() {
		return new CharacterConstraintConfig(this.equalTo, this.in, this.min, this.max, Optional.of(Unit.INSTANCE), this.digit, this.alphanumeric, this.whitespace, this.punctuation, this.symbol, this.control, this.upperCase, this.lowerCase, this.ascii, this.latin1, this.custom);
	}
	
	/**
	 * Creates a new config with the digit constraint enabled.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull CharacterConstraintConfig withDigit() {
		return new CharacterConstraintConfig(this.equalTo, this.in, this.min, this.max, this.letter, Optional.of(Unit.INSTANCE), this.alphanumeric, this.whitespace, this.punctuation, this.symbol, this.control, this.upperCase, this.lowerCase, this.ascii, this.latin1, this.custom);
	}
	
	/**
	 * Creates a new config with the alphanumeric constraint enabled.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull CharacterConstraintConfig withAlphanumeric() {
		return new CharacterConstraintConfig(this.equalTo, this.in, this.min, this.max, this.letter, this.digit, Optional.of(Unit.INSTANCE), this.whitespace, this.punctuation, this.symbol, this.control, this.upperCase, this.lowerCase, this.ascii, this.latin1, this.custom);
	}
	
	/**
	 * Creates a new config with the whitespace constraint enabled.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull CharacterConstraintConfig withWhitespace() {
		return new CharacterConstraintConfig(this.equalTo, this.in, this.min, this.max, this.letter, this.digit, this.alphanumeric, Optional.of(Unit.INSTANCE), this.punctuation, this.symbol, this.control, this.upperCase, this.lowerCase, this.ascii, this.latin1, this.custom);
	}
	
	/**
	 * Creates a new config with the punctuation constraint enabled.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull CharacterConstraintConfig withPunctuation() {
		return new CharacterConstraintConfig(this.equalTo, this.in, this.min, this.max, this.letter, this.digit, this.alphanumeric, this.whitespace, Optional.of(Unit.INSTANCE), this.symbol, this.control, this.upperCase, this.lowerCase, this.ascii, this.latin1, this.custom);
	}
	
	/**
	 * Creates a new config with the symbol constraint enabled.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull CharacterConstraintConfig withSymbol() {
		return new CharacterConstraintConfig(this.equalTo, this.in, this.min, this.max, this.letter, this.digit, this.alphanumeric, this.whitespace, this.punctuation, Optional.of(Unit.INSTANCE), this.control, this.upperCase, this.lowerCase, this.ascii, this.latin1, this.custom);
	}
	
	/**
	 * Creates a new config with the control constraint enabled.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull CharacterConstraintConfig withControl() {
		return new CharacterConstraintConfig(this.equalTo, this.in, this.min, this.max, this.letter, this.digit, this.alphanumeric, this.whitespace, this.punctuation, this.symbol, Optional.of(Unit.INSTANCE), this.upperCase, this.lowerCase, this.ascii, this.latin1, this.custom);
	}
	
	/**
	 * Creates a new config with the upper case constraint enabled.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull CharacterConstraintConfig withUpperCase() {
		return new CharacterConstraintConfig(this.equalTo, this.in, this.min, this.max, this.letter, this.digit, this.alphanumeric, this.whitespace, this.punctuation, this.symbol, this.control, Optional.of(Unit.INSTANCE), this.lowerCase, this.ascii, this.latin1, this.custom);
	}
	
	/**
	 * Creates a new config with the lower case constraint enabled.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull CharacterConstraintConfig withLowerCase() {
		return new CharacterConstraintConfig(this.equalTo, this.in, this.min, this.max, this.letter, this.digit, this.alphanumeric, this.whitespace, this.punctuation, this.symbol, this.control, this.upperCase, Optional.of(Unit.INSTANCE), this.ascii, this.latin1, this.custom);
	}
	
	/**
	 * Creates a new config with the ASCII constraint enabled.<br>
	 * <p>
	 *     Requires the character to be in the ASCII range (0-127).
	 * </p>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull CharacterConstraintConfig withAscii() {
		return new CharacterConstraintConfig(this.equalTo, this.in, this.min, this.max, this.letter, this.digit, this.alphanumeric, this.whitespace, this.punctuation, this.symbol, this.control, this.upperCase, this.lowerCase, Optional.of(Unit.INSTANCE), this.latin1, this.custom);
	}
	
	/**
	 * Creates a new config with the Latin-1 constraint enabled.<br>
	 * <p>
	 *     Requires the character to be in the Latin-1 range (0-255).
	 * </p>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull CharacterConstraintConfig withLatin1() {
		return new CharacterConstraintConfig(this.equalTo, this.in, this.min, this.max, this.letter, this.digit, this.alphanumeric, this.whitespace, this.punctuation, this.symbol, this.control, this.upperCase, this.lowerCase, this.ascii, Optional.of(Unit.INSTANCE), this.custom);
	}
	
	/**
	 * Creates a new config with the specified custom constraint.<br>
	 *
	 * @param constraint The custom constraint implementation
	 * @return A new config with the constraint applied
	 */
	public @NonNull CharacterConstraintConfig withCustom(@NonNull Constraint<Character> constraint) {
		Objects.requireNonNull(constraint, "Custom constraint must not be null");
		return new CharacterConstraintConfig(this.equalTo, this.in, this.min, this.max, this.letter, this.digit, this.alphanumeric, this.whitespace, this.punctuation, this.symbol, this.control, this.upperCase, this.lowerCase, this.ascii, this.latin1, Optional.of(constraint));
	}
	//endregion
	
	@Override
	public @NonNull Result<Void> matches(@NonNull Character value) {
		Objects.requireNonNull(value, "Value must not be null");
		
		return ConstraintMatchers.allOf(
			() -> ConstraintMatchers.matchEqualTo(value, this.equalTo),
			() -> ConstraintMatchers.matchIn(value, this.in),
			() -> ConstraintMatchers.matchRange(value, this.min, this.max),
			() -> ConstraintMatchers.matchFlag(value, this.letter, Character::isLetter, "Character '" + value + "' must be a letter"),
			() -> ConstraintMatchers.matchFlag(value, this.digit, Character::isDigit, "Character" + value + "' must be a digit"),
			() -> ConstraintMatchers.matchFlag(value, this.alphanumeric, Character::isLetterOrDigit, "Character '" + value + "' must be alphanumeric"),
			() -> ConstraintMatchers.matchFlag(value, this.whitespace, Character::isWhitespace, "Character '" + value + "' must be whitespace"),
			() -> ConstraintMatchers.matchFlag(value, this.punctuation, c -> PUNCTUATION_TYPES.contains((byte) Character.getType(c)), "Character '" + value + "' must be punctuation"),
			() -> ConstraintMatchers.matchFlag(value, this.symbol, c -> SYMBOL_TYPES.contains((byte) Character.getType(c)), "Character '" + value + "' must be a symbol"),
			() -> ConstraintMatchers.matchFlag(value, this.control, Character::isISOControl, "Character '" + value + "' must be a control character"),
			() -> ConstraintMatchers.matchFlag(value, this.upperCase, Character::isUpperCase, "Character '" + value + "' must be upper case"),
			() -> ConstraintMatchers.matchFlag(value, this.lowerCase, Character::isLowerCase, "Character '" + value + "' must be lower case"),
			() -> ConstraintMatchers.matchFlag(value, this.ascii, c -> c <= 127, "Character '" + value + "' must be an ASCII character (0-127)"),
			() -> ConstraintMatchers.matchFlag(value, this.latin1, c -> c <= 255, "Character '" + value + "' must be a Latin-1 character (0-255)"),
			() -> ConstraintMatchers.matchCustom(value, this.custom)
		);
	}
}
