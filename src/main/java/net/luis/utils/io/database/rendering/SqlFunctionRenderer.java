package net.luis.utils.io.database.rendering;

import net.luis.utils.io.database.function.SqlDefaultFunctionType;
import net.luis.utils.io.database.function.SqlFunctionType;
import org.jspecify.annotations.NonNull;

import java.util.List;

/**
 *
 * @author Luis-St
 *
 */

public interface SqlFunctionRenderer {
	
	boolean canRender(@NonNull SqlFunctionType type);
	
	@NonNull SqlRendered render(@NonNull SqlFunctionType type, @NonNull List<SqlRendered> arguments);
}
