package net.luis.utils.logging.formatter;

import net.luis.utils.logging.event.*;
import net.luis.utils.logging.formatter.pattern.LogPattern;
import net.luis.utils.logging.formatter.pattern.token.LogPatternToken;
import net.luis.utils.logging.formatter.pattern.token.StandardLogPatternTokenType;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

/**
 *
 * @author Luis-St
 *
 */

public class LogPatternFormatter implements LogFormatter {
	
	private final LogPattern pattern;
	
	protected LogPatternFormatter(@NonNull LogPattern pattern) {
		this.pattern = Objects.requireNonNull(pattern, "Log pattern must not be null");
	}
	
	@Override
	public @NonNull FormattedLogEvent format(@NonNull LogEvent event) {
		Objects.requireNonNull(event, "Log event must not be null");
		
		StringBuilder formatted = new StringBuilder();
		for (LogPatternToken token : this.pattern.tokens()) {
			if (token.type() == StandardLogPatternTokenType.PLAIN_TEXT) {
				formatted.append(token.content());
			}
			
			// LogEvent, LogPatternToken(content, type, config)
		}
		return new FormattedLogEventRecord(event, formatted.toString());
	}
}
