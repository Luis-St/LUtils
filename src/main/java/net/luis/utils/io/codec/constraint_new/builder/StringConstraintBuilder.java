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

package net.luis.utils.io.codec.constraint_new.builder;

import net.luis.utils.io.codec.constraint_new.StringConstraint;
import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.regex.Pattern;

/**
 * Builder class for constructing string-based constraints.<br>
 * <p>
 *     This builder implements {@link StringConstraint} to provide a fluent API for building
 *     constraints on string values including pattern matching, length, and content validation.<br>
 *     It is typically used as a parameter to constraint builder methods that accept string constraints.
 * </p>
 *
 * @author Luis-St
 *
 */

public class StringConstraintBuilder implements StringConstraint<String, StringConstraintBuilder> {
	
	@Override
	public @NonNull StringConstraintBuilder equalTo(@NonNull String value) {
		return null;
	}
	
	@Override
	public @NonNull StringConstraintBuilder notEqualTo(@NonNull String value) {
		return null;
	}
	
	@Override
	public @NonNull StringConstraintBuilder in(@NonNull Collection<String> values) {
		return null;
	}
	
	@Override
	public @NonNull StringConstraintBuilder notIn(@NonNull Collection<String> values) {
		return null;
	}
	
	@Override
	public @NonNull StringConstraintBuilder equalToIgnoreCase(@NonNull String value) {
		return null;
	}
	
	@Override
	public @NonNull StringConstraintBuilder notEqualToIgnoreCase(@NonNull String value) {
		return null;
	}
	
	@Override
	public @NonNull StringConstraintBuilder inIgnoreCase(@NonNull Collection<String> values) {
		return null;
	}
	
	@Override
	public @NonNull StringConstraintBuilder notInIgnoreCase(@NonNull Collection<String> values) {
		return null;
	}
	
	@Override
	public @NonNull StringConstraintBuilder lowerCase() {
		return null;
	}
	
	@Override
	public @NonNull StringConstraintBuilder upperCase() {
		return null;
	}
	
	@Override
	public @NonNull StringConstraintBuilder minLength(int minLength) {
		return null;
	}
	
	@Override
	public @NonNull StringConstraintBuilder maxLength(int maxLength) {
		return null;
	}
	
	@Override
	public @NonNull StringConstraintBuilder exactLength(int exactLength) {
		return null;
	}
	
	@Override
	public @NonNull StringConstraintBuilder lengthBetween(int minLength, int maxLength) {
		return null;
	}
	
	@Override
	public @NonNull StringConstraintBuilder startsWith(@NonNull String prefix) {
		return null;
	}
	
	@Override
	public @NonNull StringConstraintBuilder notStartsWith(@NonNull String prefix) {
		return null;
	}
	
	@Override
	public @NonNull StringConstraintBuilder startsWithAny(@NonNull Collection<String> prefixes) {
		return null;
	}
	
	@Override
	public @NonNull StringConstraintBuilder startsWithNone(@NonNull Collection<String> prefixes) {
		return null;
	}
	
	@Override
	public @NonNull StringConstraintBuilder contains(@NonNull String substring) {
		return null;
	}
	
	@Override
	public @NonNull StringConstraintBuilder notContains(@NonNull String substring) {
		return null;
	}
	
	@Override
	public @NonNull StringConstraintBuilder containsAny(@NonNull Collection<String> substrings) {
		return null;
	}
	
	@Override
	public @NonNull StringConstraintBuilder containsNone(@NonNull Collection<String> substrings) {
		return null;
	}
	
	@Override
	public @NonNull StringConstraintBuilder containsAll(@NonNull Collection<String> substrings) {
		return null;
	}
	
	@Override
	public @NonNull StringConstraintBuilder containsOnly(@NonNull Collection<String> substrings) {
		return null;
	}
	
	@Override
	public @NonNull StringConstraintBuilder endsWith(@NonNull String suffix) {
		return null;
	}
	
	@Override
	public @NonNull StringConstraintBuilder notEndsWith(@NonNull String suffix) {
		return null;
	}
	
	@Override
	public @NonNull StringConstraintBuilder endsWithAny(@NonNull Collection<String> suffixes) {
		return null;
	}
	
	@Override
	public @NonNull StringConstraintBuilder endsWithNone(@NonNull Collection<String> suffixes) {
		return null;
	}
	
	@Override
	public @NonNull StringConstraintBuilder matches(@NonNull String regex) {
		return null;
	}
	
	@Override
	public @NonNull StringConstraintBuilder notMatches(@NonNull String regex) {
		return null;
	}
	
	@Override
	public @NonNull StringConstraintBuilder matches(@NonNull Pattern pattern) {
		return null;
	}
	
	@Override
	public @NonNull StringConstraintBuilder notMatches(@NonNull Pattern pattern) {
		return null;
	}
	
	@Override
	public @NonNull StringConstraintBuilder trimmed() {
		return null;
	}
	
	@Override
	public @NonNull StringConstraintBuilder blank() {
		return null;
	}
	
	@Override
	public @NonNull StringConstraintBuilder notBlank() {
		return null;
	}
	
	@Override
	public @NonNull StringConstraintBuilder numeric() {
		return null;
	}
	
	@Override
	public @NonNull StringConstraintBuilder alphabetic() {
		return null;
	}
	
	@Override
	public @NonNull StringConstraintBuilder alphanumeric() {
		return null;
	}
	
	@Override
	public @NonNull StringConstraintBuilder ascii() {
		return null;
	}
	
	@Override
	public @NonNull StringConstraintBuilder latin1() {
		return null;
	}
}
