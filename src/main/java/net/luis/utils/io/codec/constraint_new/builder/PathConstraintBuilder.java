package net.luis.utils.io.codec.constraint_new.builder;

import net.luis.utils.io.codec.constraint_new.core.Platform;
import net.luis.utils.io.codec.constraint_new.network.PathConstraint;
import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.function.UnaryOperator;

/**
 * Builder class for constructing path-based constraints.<br>
 * <p>
 *     This builder implements {@link PathConstraint} to provide a fluent API for building
 *     constraints on file paths including structure, components, and platform compatibility.<br>
 *     It is typically used as a parameter to constraint builder methods that accept path constraints.
 * </p>
 *
 * @author Luis-St
 *
 */

public class PathConstraintBuilder implements PathConstraint<String, PathConstraintBuilder> {
	
	@Override
	public @NonNull PathConstraintBuilder equalTo(@NonNull String value) {
		return null;
	}
	
	@Override
	public @NonNull PathConstraintBuilder notEqualTo(@NonNull String value) {
		return null;
	}
	
	@Override
	public @NonNull PathConstraintBuilder in(@NonNull Collection<String> values) {
		return null;
	}
	
	@Override
	public @NonNull PathConstraintBuilder notIn(@NonNull Collection<String> values) {
		return null;
	}
	
	@Override
	public @NonNull PathConstraintBuilder minLength(int minLength) {
		return null;
	}
	
	@Override
	public @NonNull PathConstraintBuilder maxLength(int maxLength) {
		return null;
	}
	
	@Override
	public @NonNull PathConstraintBuilder exactLength(int exactLength) {
		return null;
	}
	
	@Override
	public @NonNull PathConstraintBuilder lengthBetween(int minLength, int maxLength) {
		return null;
	}
	
	@Override
	public @NonNull PathConstraintBuilder absolute() {
		return null;
	}
	
	@Override
	public @NonNull PathConstraintBuilder relative() {
		return null;
	}
	
	@Override
	public @NonNull PathConstraintBuilder normalized() {
		return null;
	}
	
	@Override
	public @NonNull PathConstraintBuilder canonical() {
		return null;
	}
	
	@Override
	public @NonNull PathConstraintBuilder depth(@NonNull UnaryOperator<NumericConstraintBuilder> builder) {
		return null;
	}
	
	@Override
	public @NonNull PathConstraintBuilder path(@NonNull UnaryOperator<StringConstraintBuilder> builder) {
		return null;
	}
	
	@Override
	public @NonNull PathConstraintBuilder root(@NonNull UnaryOperator<StringConstraintBuilder> builder) {
		return null;
	}
	
	@Override
	public @NonNull PathConstraintBuilder parent(@NonNull UnaryOperator<StringConstraintBuilder> builder) {
		return null;
	}
	
	@Override
	public @NonNull PathConstraintBuilder segment(@NonNull UnaryOperator<StringConstraintBuilder> builder) {
		return null;
	}
	
	@Override
	public @NonNull PathConstraintBuilder file(@NonNull UnaryOperator<StringConstraintBuilder> builder) {
		return null;
	}
	
	@Override
	public @NonNull PathConstraintBuilder withoutExtension() {
		return null;
	}
	
	@Override
	public @NonNull PathConstraintBuilder extension(@NonNull UnaryOperator<StringConstraintBuilder> builder) {
		return null;
	}
	
	@Override
	public @NonNull PathConstraintBuilder ancestorOf(@NonNull String path) {
		return null;
	}
	
	@Override
	public @NonNull PathConstraintBuilder ancestorOf(@NonNull Collection<String> paths) {
		return null;
	}
	
	@Override
	public @NonNull PathConstraintBuilder descendantOf(@NonNull String path) {
		return null;
	}
	
	@Override
	public @NonNull PathConstraintBuilder descendantOf(@NonNull Collection<String> paths) {
		return null;
	}
	
	@Override
	public @NonNull PathConstraintBuilder validFor(@NonNull Platform platform) {
		return null;
	}
	
	@Override
	public @NonNull PathConstraintBuilder portable() {
		return null;
	}
	
	@Override
	public @NonNull PathConstraintBuilder separator(@NonNull Platform platform) {
		return null;
	}
}
