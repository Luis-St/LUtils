package net.luis.utils.logging.formatter;

import net.luis.utils.logging.event.LogEvent;
import net.luis.utils.logging.formatter.pattern.token.config.LogPatternTokenConfig;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 *
 * @author Luis-St
 *
 */

@FunctionalInterface
public interface LogTokenFormatter<T extends LogPatternTokenConfig> {
	
	static <T extends LogPatternTokenConfig> LogTokenFormatter<T> configBased() {
		return new LogTokenFormatter<>() {
			@Override
			public @NonNull String formate(@NonNull LogEvent event, @Nullable LogPatternTokenConfig config, @NonNull String content) {
				if (config == null) {
					throw new IllegalArgumentException("Unable to formate log event based on the configuration, no configuration was provided"); // ToDo: better exception
				}
				return config.formate(event, content);
			}
		};
	}
	
	@NonNull String formate(@NonNull LogEvent event, @Nullable T config, @NonNull String content);
}
