/*
 * LUtils
 * Copyright (C) 2026 Luis Staudt
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package net.luis.utils.logging.formatter.pattern;

import net.luis.utils.logging.formatter.pattern.util.*;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Config records for all pattern tokens.
 *
 * One record per token type. Binding happens via the RecordComponent-based
 * binder: option keys in the pattern are LITERALLY the record component names.
 * All defaulting and validation lives in the compact constructors, so the same
 * rules apply whether a config was parsed from a pattern string or constructed
 * programmatically through the builder API.
 *
 * @author Luis-St
 */
public final class LogTokenConfigs {
	
	private LogTokenConfigs() {}
	
	/* ---------------- universal layout options ---------------- */
	
	/**
	 * NOT a token config. These are reserved keys valid on EVERY token.
	 * The parser strips them from the option map before binding the token's
	 * own record; if any were present, the compiled FormatComponent is
	 * wrapped in a padding/truncation decorator.
	 *
	 * truncateFrom=START keeps the tail of the value, which is usually the
	 * informative end (logger names, file paths).
	 * Zero-padding a number: align=RIGHT, padChar=0.
	 */
	public record PaddingConfig(
		Integer minWidth,
		Integer maxWidth,
		Align align,
		Character padChar,
		TruncateFrom truncateFrom
	) {
		public PaddingConfig {
			if (align == null) align = Align.LEFT;
			if (padChar == null) padChar = ' ';
			if (truncateFrom == null) truncateFrom = TruncateFrom.END;
			if (minWidth != null && minWidth < 1)
				throw new PatternException("minWidth must be >= 1");
			if (maxWidth != null && maxWidth < 1)
				throw new PatternException("maxWidth must be >= 1");
			if (minWidth != null && maxWidth != null && maxWidth < minWidth)
				throw new PatternException("maxWidth must be >= minWidth");
		}
		
		public boolean isEmpty() {
			return minWidth == null && maxWidth == null;
		}
	}
	
	/* ---------------- parser-internal tokens ---------------- */
	
	/** The text between directives. Never user-configured, constructed by the parser. */
	public record LiteralConfig(String text) {
		public LiteralConfig {
			if (text == null) throw new PatternException("literal text must not be null");
		}
	}
	
	/** Deliberate default LF even on Windows; CRLF in log files is legacy pain. */
	public record NewlineConfig(LineEnding style) {
		public NewlineConfig {
			if (style == null) style = LineEnding.LF;
		}
	}
	
	/* ---------------- core tokens ---------------- */
	
	/**
	 * format is either a preset name or a DateTimeFormatter pattern,
	 * validated at config time so a typo fails at startup, not at first log.
	 */
	public record TimestampConfig(String format, ZoneId timezone, Locale locale) {
		
		public static final Set<String> PRESETS = Set.of(
			"ISO8601", "ISO8601_LOCAL", "UNIX_MILLIS", "UNIX_SECONDS", "RFC_2822", "RFC_1123"
		);
		
		public TimestampConfig {
			if (format == null) format = "ISO8601";
			if (timezone == null) timezone = ZoneOffset.UTC;
			if (locale == null) locale = Locale.ROOT;
			if (!PRESETS.contains(format)) {
				try {
					DateTimeFormatter.ofPattern(format);
				} catch (IllegalArgumentException e) {
					throw new PatternException(
						"invalid timestamp format '" + format + "': " + e.getMessage());
				}
			}
		}
	}
	
	/**
	 * length truncates the level name: length=1 gives I/W/E.
	 * Combine with universal minWidth=5 for the classic aligned column.
	 */
	public record LevelConfig(Casing casing, Integer length) {
		public LevelConfig {
			if (casing == null) casing = Casing.NONE;
			if (length != null && length < 1)
				throw new PatternException("length must be >= 1");
		}
	}
	
	/**
	 * Every field here is a commitment in the event schema: thread data must
	 * be captured on the caller thread at event creation - the consumer thread
	 * cannot ask afterwards. NAME is cheap; each further field adds event weight.
	 * STATE is deliberately absent: thread state at format time would be a lie.
	 */
	public record ThreadConfig(ThreadField field) {
		public ThreadConfig {
			if (field == null) field = ThreadField.NAME;
		}
	}
	
	public record MessageConfig(
		Casing casing,
		Integer maxLength,       // truncate message text
		String ellipsis,         // appended on truncation, counts toward maxLength; "" = none
		Boolean escapeNewlines,  // for one-event-one-line guarantees (log shippers)
		String emptyValue        // rendered when the message is null/blank
	) {
		public MessageConfig {
			if (casing == null) casing = Casing.NONE;
			if (ellipsis == null) ellipsis = "";
			if (escapeNewlines == null) escapeNewlines = false;
			if (emptyValue == null) emptyValue = "";
			if (maxLength != null && maxLength < 1)
				throw new PatternException("maxLength must be >= 1");
			if (maxLength != null && ellipsis.length() >= maxLength)
				throw new PatternException("ellipsis must be shorter than maxLength");
		}
	}
	
	/**
	 * depth=0 renders only the "ExceptionType: message" header, no frames.
	 * packageFilter hides frames whose class name starts with a listed prefix
	 * ("jdk.internal.", "org.springframework."), collapsed to "... N frames hidden".
	 */
	public record ExceptionConfig(
		Integer depth,              // max frames per throwable; null = unlimited
		Boolean elideCommonFrames,  // "... 23 common frames omitted" in cause chains
		Boolean causes,             // walk the cause chain at all
		Integer maxCauseDepth,      // cap on chain length; null = unlimited
		Boolean suppressed,         // include suppressed exceptions
		Boolean rootCauseFirst,     // invert the chain: root cause printed first
		List<String> packageFilter  // frame class-name prefixes to hide
	) {
		public ExceptionConfig {
			if (elideCommonFrames == null) elideCommonFrames = true;
			if (causes == null) causes = true;
			if (suppressed == null) suppressed = true;
			if (rootCauseFirst == null) rootCauseFirst = false;
			packageFilter = packageFilter == null ? List.of() : List.copyOf(packageFilter);
			if (depth != null && depth < 0)
				throw new PatternException("depth must be >= 0");
			if (maxCauseDepth != null && maxCauseDepth < 1)
				throw new PatternException("maxCauseDepth must be >= 1");
		}
	}
	
	/**
	 * Nothing token-specific to configure - zero-padding comes from the
	 * universal keys (align=RIGHT, padChar=0, minWidth=8). This is the
	 * empty-record pattern: unknown options still fail through the same
	 * binder path as every other token.
	 */
	public record SequenceNumberConfig() {}
	
	/* ---------------- placeholder ---------------- */
	
	/** Replace with your real config-time exception type. */
	public static final class PatternException extends IllegalArgumentException {
		public PatternException(String message) { super(message); }
	}
}

