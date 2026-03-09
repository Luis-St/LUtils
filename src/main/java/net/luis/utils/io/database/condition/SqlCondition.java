package net.luis.utils.io.database.condition;

import net.luis.utils.io.database.rendering.SqlRenderable;
import org.jspecify.annotations.NonNull;

/**
 *
 * @author Luis-St
 *
 */

public interface SqlCondition extends SqlRenderable {
	
	static @NonNull SqlCondition always() {
		return null;
	}
	
	static @NonNull SqlCondition never() {
		return null;
	}
	
	static @NonNull SqlCondition allOf(@NonNull SqlCondition first, @NonNull SqlCondition second, SqlCondition @NonNull ... others) {
		return null;
	}
	
	static @NonNull SqlCondition anyOf(@NonNull SqlCondition first, @NonNull SqlCondition second, SqlCondition @NonNull ... others) {
		return null;
	}
	
	default @NonNull SqlCondition and(@NonNull SqlCondition other) {
		return allOf(this, other);
	}
	
	default @NonNull SqlCondition or(@NonNull SqlCondition other) {
		return anyOf(this, other);
	}
	
	@NonNull SqlCondition not();
}
