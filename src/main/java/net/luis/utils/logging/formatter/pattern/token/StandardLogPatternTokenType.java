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

package net.luis.utils.logging.formatter.pattern.token;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.logging.event.LogEvent;
import net.luis.utils.logging.formatter.LogTokenFormatHelper;
import net.luis.utils.logging.formatter.LogTokenFormatter;
import net.luis.utils.logging.formatter.pattern.token.config.LogPatternTokenConfig;
import net.luis.utils.logging.formatter.pattern.token.config.base.*;
import net.luis.utils.logging.formatter.pattern.token.config.core.*;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.*;

/**
 *
 * @author Luis-St
 *
 */

public enum StandardLogPatternTokenType implements LogPatternTokenType {
	
	// ToDo:
	//  - LogTokenFormateHelper#configBased fails for no configuration
	//  - Implement Marker first
	
	PLAIN_TEXT("PlainText", Set.of(), (_, _, content) -> content),
	LOGGER_NAME("LoggerName", Set.of("logger", "name", "loggerName", "ln"), LogPatternLoggerNameConfig.CODEC, LogTokenFormatHelper.configBased()),
	LEVEL("Level", Set.of("level", "lvl", "l"), LogPatternLevelConfig.CODEC, LogTokenFormatHelper.configBased()),
	MARKER("Marker", Set.of("marker", "mk"), LogPatternMarkerConfig.CODEC, null),
	THREAD_NAME("ThreadName", Set.of("threadName", "tn"), LogPatternStringValueConfig.CODEC, LogTokenFormatHelper.stringValue(event -> event.threadInfo().name(), "")),
	THREAD_ID("ThreadId", Set.of("threadId", "ti"), LogPatternLongValueConfig.CODEC, LogTokenFormatHelper.longValue(event -> event.threadInfo().id())),
	THREAD_GROUP("ThreadGroup", Set.of("threadGroup", "tg"), LogPatternStringValueConfig.CODEC, LogTokenFormatHelper.stringValue(event -> event.threadInfo().group(), "DefaultThreadGroup")),
	THREAD_PRIORITY("ThreadPriority", Set.of("threadPriority", "tp"), LogPatternLongValueConfig.CODEC, LogTokenFormatHelper.longValue(event -> (long) event.threadInfo().priority())),
	THREAD_VIRTUAL("ThreadVirtual", Set.of("threadVirtual", "tv"), LogPatternBooleanValueConfig.CODEC, LogTokenFormatHelper.booleanValue(event -> event.threadInfo().isVirtual())),
	THREAD_DAEMON("ThreadDaemon", Set.of("threadDaemon", "td"), LogPatternBooleanValueConfig.CODEC, LogTokenFormatHelper.booleanValue(event -> event.threadInfo().isDaemon())),
	THREAD_STATE("ThreadState", Set.of("threadState", "ts"), LogPatternStringValueConfig.CODEC, LogTokenFormatHelper.stringValue(event -> event.threadInfo().state(), "")),
	TIMESTAMP("Timestamp", Set.of("timestamp", "time", "date", "t"), LogPatternTimestampConfig.CODEC, LogTokenFormatHelper.configBased()),
	CONTEXT_MAP("ContextMap", Set.of("context", "ctx"), LogPatternContextMapConfig.CODEC, LogTokenFormatHelper.configBased()),
	CONTEXT_ITEM("ContextItem", Set.of("context:", "ctx:"), LogPatternContextItemConfig.CODEC, LogTokenFormatHelper.configBased()) {
		@Override
		public boolean isIdentifier(@Nullable String identifier) {
			if (identifier == null) {
				return false;
			}
			
			for (String tokenIdentifier : this.getIdentifiers()) {
				if (identifier.regionMatches(true, 0, tokenIdentifier, 0, tokenIdentifier.length())) {
					return true;
				}
			}
			return super.isIdentifier(identifier);
		}
	},
	EXCEPTION("Exception", Set.of("exception", "ex", "e"), LogPatternExceptionConfig.CODEC, LogTokenFormatHelper.configBased()),
	MESSAGE("Message", Set.of("message", "msg", "m"), LogPatternMessageConfig.CODEC, LogTokenFormatHelper.configBased()),
	SEQUENCE_NUMBER("SequenceNumber", Set.of("sequenceNumber", "sequence", "sqn"), LogPatternLongValueConfig.CODEC, LogTokenFormatHelper.longValue(LogEvent::sequenceNumber)),
	NEWLINE("Newline", Set.of("newline", "nl"), LogPatternNewlineConfig.CODEC, LogTokenFormatHelper.configBased());
	
	private final String name;
	private final Set<String> identifiers;
	private final boolean supportsConfiguration;
	private final boolean requiresConfiguration;
	private final Optional<Codec<? extends LogPatternTokenConfig>> codec;
	private final LogTokenFormatter<? extends LogPatternTokenConfig> formatter;
	
	StandardLogPatternTokenType(
		@NonNull String name, @NonNull Set<String> identifiers, @NonNull LogTokenFormatter<LogPatternTokenConfig> formatter
	) {
		this.name = Objects.requireNonNull(name, "Name must not be null");
		this.identifiers = Set.copyOf(Objects.requireNonNull(identifiers, "Identifiers must not be null"));
		this.supportsConfiguration = false;
		this.requiresConfiguration = false;
		this.codec = Optional.empty();
		this.formatter = Objects.requireNonNull(formatter, "Formatter must not be null");
	}
	
	<T extends LogPatternTokenConfig> StandardLogPatternTokenType(
		@NonNull String name, @NonNull Set<String> identifiers, @NonNull Codec<T> codec, @NonNull LogTokenFormatter<T> formatter
	) {
		this.name = Objects.requireNonNull(name, "Name must not be null");
		this.identifiers = Set.copyOf(Objects.requireNonNull(identifiers, "Identifiers must not be null"));
		this.supportsConfiguration = true;
		this.requiresConfiguration = false;
		this.codec = Optional.of(Objects.requireNonNull(codec, "Codec must not be null"));
		this.formatter = Objects.requireNonNull(formatter, "Formatter must not be null");
	}
	
	@Override
	public @NonNull String getName() {
		return this.name;
	}
	
	@Override
	public @NonNull @Unmodifiable Set<String> getIdentifiers() {
		return this.identifiers;
	}
	
	@Override
	public boolean supportsConfiguration() {
		return this.supportsConfiguration;
	}
	
	@Override
	public boolean requiresConfiguration() {
		return this.requiresConfiguration;
	}
	
	@Override
	public @NonNull Optional<Codec<? extends LogPatternTokenConfig>> codec() {
		return this.codec;
	}
	
	@Override
	public @NonNull LogTokenFormatter<? extends LogPatternTokenConfig> formatter() {
		return this.formatter;
	}
}
