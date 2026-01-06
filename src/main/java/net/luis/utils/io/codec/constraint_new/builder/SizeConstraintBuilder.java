package net.luis.utils.io.codec.constraint_new.builder;

import net.luis.utils.io.codec.constraint_new.SizeConstraint;
import org.jspecify.annotations.NonNull;

import java.util.Collection;

/**
 * Builder class for constructing size-based constraints.<br>
 * <p>
 *     This builder implements {@link SizeConstraint} to provide a fluent API for building
 *     constraints on collection sizes.<br>
 *     It is typically used as a parameter to constraint builder methods that accept size constraints.
 * </p>
 *
 * @author Luis-St
 *
 */

public class SizeConstraintBuilder implements SizeConstraint<Integer, SizeConstraintBuilder> {
	
	@Override
	public @NonNull SizeConstraintBuilder equalTo(@NonNull Integer value) {
		return null;
	}
	
	@Override
	public @NonNull SizeConstraintBuilder notEqualTo(@NonNull Integer value) {
		return null;
	}
	
	@Override
	public @NonNull SizeConstraintBuilder in(@NonNull Collection<Integer> values) {
		return null;
	}
	
	@Override
	public @NonNull SizeConstraintBuilder notIn(@NonNull Collection<Integer> values) {
		return null;
	}
	
	@Override
	public @NonNull SizeConstraintBuilder minSize(int minSize) {
		return null;
	}
	
	@Override
	public @NonNull SizeConstraintBuilder maxSize(int maxSize) {
		return null;
	}
	
	@Override
	public @NonNull SizeConstraintBuilder exactSize(int exactSize) {
		return null;
	}
	
	@Override
	public @NonNull SizeConstraintBuilder sizeBetween(int minSize, int maxSize) {
		return null;
	}
}
