package net.luis.utils.io.codec.constraint_new.builder;

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
 *
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
