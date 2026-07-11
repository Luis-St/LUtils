package net.luis.utils.logging;

/**
 *
 * @author Luis-St
 *
 */



// try (LogScope scope = logger.beginScope()) {} // Binds the buffered logger to this scope
@FunctionalInterface
public interface LogScope extends AutoCloseable {
	
	@Override
	void close();
}
