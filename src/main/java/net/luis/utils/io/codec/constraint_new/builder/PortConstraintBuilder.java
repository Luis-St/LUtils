package net.luis.utils.io.codec.constraint_new.builder;

import net.luis.utils.io.codec.constraint_new.core.PortRange;
import net.luis.utils.io.codec.constraint_new.network.PortConstraint;
import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.function.UnaryOperator;

/**
 * Builder class for constructing port-based constraints.<br>
 * <p>
 *     This builder implements {@link PortConstraint} to provide a fluent API for building
 *     constraints on network port numbers including range and port type validation.<br>
 *     It is typically used as a parameter to constraint builder methods that accept port constraints.
 * </p>
 *
 * @author Luis-St
 *
 */

public class PortConstraintBuilder implements PortConstraint<Integer, PortConstraintBuilder> {
	
	@Override
	public @NonNull PortConstraintBuilder equalTo(@NonNull Integer value) {
		return null;
	}
	
	@Override
	public @NonNull PortConstraintBuilder notEqualTo(@NonNull Integer value) {
		return null;
	}
	
	@Override
	public @NonNull PortConstraintBuilder in(@NonNull Collection<Integer> values) {
		return null;
	}
	
	@Override
	public @NonNull PortConstraintBuilder notIn(@NonNull Collection<Integer> values) {
		return null;
	}
	
	@Override
	public @NonNull PortConstraintBuilder inRange(int min, int max) {
		return null;
	}
	
	@Override
	public @NonNull PortConstraintBuilder notInRange(int min, int max) {
		return null;
	}
	
	@Override
	public @NonNull PortConstraintBuilder type(@NonNull UnaryOperator<EnumConstraintBuilder<PortRange>> builder) {
		return null;
	}
}
