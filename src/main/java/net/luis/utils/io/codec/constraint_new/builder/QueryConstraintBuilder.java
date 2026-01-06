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

import net.luis.utils.io.codec.constraint_new.Constraint;
import net.luis.utils.io.codec.constraint_new.network.QueryConstraint;
import org.jspecify.annotations.NonNull;

import java.util.*;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

/**
 * Builder class for constructing query parameter constraints.<br>
 * <p>
 *     This builder implements {@link QueryConstraint} to provide a fluent API for building
 *     constraints on URI query parameters including value validation and single/multi-value requirements.<br>
 *     It is typically used as a parameter to constraint builder methods that accept query constraints.
 * </p>
 *
 * @author Luis-St
 */
public class QueryConstraintBuilder implements QueryConstraint<QueryConstraintBuilder> {
	
	@Override
	public @NonNull QueryConstraintBuilder equalTo(@NonNull Map<String, List<String>> value) {
		return null;
	}
	
	@Override
	public @NonNull QueryConstraintBuilder notEqualTo(@NonNull Map<String, List<String>> value) {
		return null;
	}
	
	@Override
	public @NonNull QueryConstraintBuilder in(@NonNull Collection<Map<String, List<String>>> values) {
		return null;
	}
	
	@Override
	public @NonNull QueryConstraintBuilder notIn(@NonNull Collection<Map<String, List<String>>> values) {
		return null;
	}
	
	@Override
	public @NonNull QueryConstraintBuilder custom(@NonNull Constraint<Map<String, List<String>>> constraint) {
		return null;
	}
	
	@Override
	public @NonNull QueryConstraintBuilder minSize(int minSize) {
		return null;
	}
	
	@Override
	public @NonNull QueryConstraintBuilder maxSize(int maxSize) {
		return null;
	}
	
	@Override
	public @NonNull QueryConstraintBuilder exactSize(int exactSize) {
		return null;
	}
	
	@Override
	public @NonNull QueryConstraintBuilder sizeBetween(int minSize, int maxSize) {
		return null;
	}
	
	@Override
	public @NonNull QueryConstraintBuilder requiredKey(@NonNull String key) {
		return null;
	}
	
	@Override
	public @NonNull QueryConstraintBuilder requiredKeys(@NonNull Collection<String> keys) {
		return null;
	}
	
	@Override
	public @NonNull QueryConstraintBuilder forbiddenKey(@NonNull String key) {
		return null;
	}
	
	@Override
	public @NonNull QueryConstraintBuilder forbiddenKeys(@NonNull Collection<String> keys) {
		return null;
	}
	
	@Override
	public @NonNull QueryConstraintBuilder allowedKey(@NonNull String key) {
		return null;
	}
	
	@Override
	public @NonNull QueryConstraintBuilder allowedKeys(@NonNull Collection<String> keys) {
		return null;
	}
	
	@Override
	public @NonNull QueryConstraintBuilder nonNullKeys() {
		return null;
	}
	
	@Override
	public @NonNull QueryConstraintBuilder uniqueValues() {
		return null;
	}
	
	@Override
	public @NonNull QueryConstraintBuilder nonNullValues() {
		return null;
	}
	
	@Override
	public @NonNull QueryConstraintBuilder value(@NonNull String key, @NonNull UnaryOperator<StringConstraintBuilder> builder) {
		return null;
	}
	
	@Override
	public @NonNull QueryConstraintBuilder values(@NonNull String regex, @NonNull UnaryOperator<StringConstraintBuilder> builder) {
		return null;
	}
	
	@Override
	public @NonNull QueryConstraintBuilder values(@NonNull Pattern pattern, @NonNull UnaryOperator<StringConstraintBuilder> builder) {
		return null;
	}
	
	@Override
	public @NonNull QueryConstraintBuilder singleValued() {
		return null;
	}
	
	@Override
	public @NonNull QueryConstraintBuilder multiValued(@NonNull String key, @NonNull UnaryOperator<SizeConstraintBuilder> builder) {
		return null;
	}
}
