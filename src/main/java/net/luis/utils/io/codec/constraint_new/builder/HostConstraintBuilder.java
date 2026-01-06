package net.luis.utils.io.codec.constraint_new.builder;

import net.luis.utils.io.codec.constraint_new.core.IpAddressType;
import net.luis.utils.io.codec.constraint_new.network.HostConstraint;
import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

/**
 * Builder class for constructing host-based constraints.<br>
 * <p>
 *     This builder implements {@link HostConstraint} to provide a fluent API for building
 *     constraints on network hosts including IP address format, subnet membership, and domain validation.<br>
 *     It is typically used as a parameter to constraint builder methods that accept host constraints.
 * </p>
 *
 * @author Luis-St
 *
 */

public class HostConstraintBuilder implements HostConstraint<String, HostConstraintBuilder> {
	
	@Override
	public @NonNull HostConstraintBuilder equalTo(@NonNull String value) {
		return null;
	}
	
	@Override
	public @NonNull HostConstraintBuilder notEqualTo(@NonNull String value) {
		return null;
	}
	
	@Override
	public @NonNull HostConstraintBuilder in(@NonNull Collection<String> values) {
		return null;
	}
	
	@Override
	public @NonNull HostConstraintBuilder notIn(@NonNull Collection<String> values) {
		return null;
	}
	
	@Override
	public @NonNull HostConstraintBuilder ipv4() {
		return null;
	}
	
	@Override
	public @NonNull HostConstraintBuilder ipv6() {
		return null;
	}
	
	@Override
	public @NonNull HostConstraintBuilder ip(@NonNull UnaryOperator<StringConstraintBuilder> builder) {
		return null;
	}
	
	@Override
	public @NonNull HostConstraintBuilder ipType(@NonNull UnaryOperator<EnumConstraintBuilder<IpAddressType>> builder) {
		return null;
	}
	
	@Override
	public @NonNull HostConstraintBuilder inAnySubnet(@NonNull Collection<String> cidrs) {
		return null;
	}
	
	@Override
	public @NonNull HostConstraintBuilder notInAnySubnet(@NonNull Collection<String> cidrs) {
		return null;
	}
	
	@Override
	public @NonNull HostConstraintBuilder domain(@NonNull UnaryOperator<StringConstraintBuilder> builder) {
		return null;
	}
	
	@Override
	public @NonNull HostConstraintBuilder rootDomain() {
		return null;
	}
	
	@Override
	public @NonNull HostConstraintBuilder subDomain() {
		return null;
	}
	
	@Override
	public @NonNull HostConstraintBuilder startsWith(@NonNull String prefix) {
		return null;
	}
	
	@Override
	public @NonNull HostConstraintBuilder notStartsWith(@NonNull String prefix) {
		return null;
	}
	
	@Override
	public @NonNull HostConstraintBuilder startsWithAny(@NonNull Collection<String> prefixes) {
		return null;
	}
	
	@Override
	public @NonNull HostConstraintBuilder startsWithNone(@NonNull Collection<String> prefixes) {
		return null;
	}
	
	@Override
	public @NonNull HostConstraintBuilder contains(@NonNull String substring) {
		return null;
	}
	
	@Override
	public @NonNull HostConstraintBuilder notContains(@NonNull String substring) {
		return null;
	}
	
	@Override
	public @NonNull HostConstraintBuilder containsAny(@NonNull Collection<String> substrings) {
		return null;
	}
	
	@Override
	public @NonNull HostConstraintBuilder containsNone(@NonNull Collection<String> substrings) {
		return null;
	}
	
	@Override
	public @NonNull HostConstraintBuilder endsWith(@NonNull String suffix) {
		return null;
	}
	
	@Override
	public @NonNull HostConstraintBuilder notEndsWith(@NonNull String suffix) {
		return null;
	}
	
	@Override
	public @NonNull HostConstraintBuilder endsWithAny(@NonNull Collection<String> suffixes) {
		return null;
	}
	
	@Override
	public @NonNull HostConstraintBuilder endsWithNone(@NonNull Collection<String> suffixes) {
		return null;
	}
	
	@Override
	public @NonNull HostConstraintBuilder matches(@NonNull String regex) {
		return null;
	}
	
	@Override
	public @NonNull HostConstraintBuilder notMatches(@NonNull String regex) {
		return null;
	}
	
	@Override
	public @NonNull HostConstraintBuilder matches(@NonNull Pattern pattern) {
		return null;
	}
	
	@Override
	public @NonNull HostConstraintBuilder notMatches(@NonNull Pattern pattern) {
		return null;
	}
	
	@Override
	public @NonNull HostConstraintBuilder minLength(int minLength) {
		return null;
	}
	
	@Override
	public @NonNull HostConstraintBuilder maxLength(int maxLength) {
		return null;
	}
	
	@Override
	public @NonNull HostConstraintBuilder exactLength(int exactLength) {
		return null;
	}
	
	@Override
	public @NonNull HostConstraintBuilder lengthBetween(int minLength, int maxLength) {
		return null;
	}
}
