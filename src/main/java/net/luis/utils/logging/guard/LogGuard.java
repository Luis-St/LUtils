package net.luis.utils.logging.guard;

import net.luis.utils.logging.LogLevel;
import net.luis.utils.logging.marker.LogMarker;
import org.jspecify.annotations.NonNull;

/**
 *
 * @author Luis-St
 *
 */

@FunctionalInterface
public interface LogGuard {
	
	boolean shouldLog(@NonNull String logger, @NonNull LogLevel level, @NonNull LogMarker marker);
}
