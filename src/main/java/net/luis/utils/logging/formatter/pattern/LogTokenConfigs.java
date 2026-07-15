package net.luis.utils.logging.formatter.pattern;

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
	
	/* ---------------- shared enums ---------------- */
	
	public enum Casing { NONE, UPPER, LOWER }
	public enum Align { LEFT, RIGHT }
	public enum TruncateFrom { START, END }
	public enum LineEnding { LF, CRLF, SYSTEM }
	public enum ThreadField { NAME, ID, GROUP, PRIORITY, VIRTUAL }
	
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
		
		public static final Set<String> PRESETS =
			Set.of("ISO8601", "ISO8601_LOCAL", "UNIX_MILLIS", "UNIX_SECONDS");
		
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
	 * Exactly one abbreviation strategy may be set:
	 *   maxLength - abbreviate package segments until the name fits;
	 *               the class name itself is never abbreviated (Logback semantics).
	 *   segments  - keep only the last N dot-separated segments
	 *               (segments=1 renders just the class name).
	 * Neither set: full name. Logger names are bounded and interned, so the
	 * abbreviated form can be cached per logger at first use.
	 */
	public record LoggerNameConfig(Integer maxLength, Integer segments) {
		public LoggerNameConfig {
			if (maxLength != null && segments != null)
				throw new PatternException("maxLength and segments are mutually exclusive");
			if (maxLength != null && maxLength < 1)
				throw new PatternException("maxLength must be >= 1");
			if (segments != null && segments < 1)
				throw new PatternException("segments must be >= 1");
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
	 * key is REQUIRED - note that required-ness needs no binder mechanism,
	 * the compact constructor simply rejects its absence.
	 *
	 * prefix/suffix are emitted only when a value is actually rendered:
	 *   %Context{key=requestId,prefix='[req=',suffix=']'}
	 * prints nothing at all when requestId is absent.
	 *
	 * defaultValue semantics: null means "absent stays absent" (prefix/suffix
	 * suppressed); a non-null defaultValue is rendered like a real value,
	 * prefix/suffix included.
	 */
	public record ContextConfig(String key, String defaultValue, String prefix, String suffix) {
		public ContextConfig {
			if (key == null || key.isBlank())
				throw new PatternException("%Context requires option 'key'");
			if (prefix == null) prefix = "";
			if (suffix == null) suffix = "";
		}
	}
	
	/**
	 * include doubles as render order. Without include, keys are sorted:
	 * context maps have no stable iteration order, and nondeterministic
	 * field order across lines makes logs needlessly diff-hostile.
	 * prefix/suffix wrap the whole rendering, only when the map is non-empty.
	 */
	public record ContextMapConfig(
		List<String> include,
		List<String> exclude,
		String kvSeparator,      // default "="
		String entrySeparator,   // default ", "
		String prefix,
		String suffix
	) {
		public ContextMapConfig {
			if (include != null && exclude != null)
				throw new PatternException("include and exclude are mutually exclusive");
			if (include != null) include = List.copyOf(include);
			if (exclude != null) exclude = List.copyOf(exclude);
			if (kvSeparator == null) kvSeparator = "=";
			if (entrySeparator == null) entrySeparator = ", ";
			if (prefix == null) prefix = "";
			if (suffix == null) suffix = "";
		}
	}
	
	/* ---------------- fast-follow tokens ---------------- */
	
	/**
	 * Markers are optional per event - same render-only-when-present
	 * prefix/suffix treatment as %Context. path=true renders the full
	 * hierarchy joined by separator ("Background/Backup"); false renders
	 * the leaf name. Both forms are precomputable and cacheable on the
	 * marker itself, since markers are interned and bounded.
	 */
	public record MarkerConfig(Boolean path, String separator, String prefix, String suffix) {
		public MarkerConfig {
			if (path == null) path = false;
			if (separator == null) separator = "/";
			if (prefix == null) prefix = "";
			if (suffix == null) suffix = "";
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

