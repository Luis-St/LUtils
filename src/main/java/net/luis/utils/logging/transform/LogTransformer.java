package net.luis.utils.logging.transform;

import net.luis.utils.logging.LogEvent;
import org.jspecify.annotations.NonNull;

/**
 *
 * @author Luis-St
 *
 */

@FunctionalInterface
public interface LogTransformer {
	
	@NonNull LogEvent format(@NonNull LogEvent event);
}
