package net.luis.utils.io.database.rendering;

import net.luis.utils.io.database.dialect.SqlDialect;
import org.jspecify.annotations.NonNull;

import java.util.List;

/**
 *
 * @author Luis-St
 *
 */

@FunctionalInterface
public interface SqlRenderable {
	
	@NonNull SqlRendered toSql(@NonNull SqlDialect dialect);
	
	default @NonNull List<Object> getParameters() {
		return List.of();
	}
}
