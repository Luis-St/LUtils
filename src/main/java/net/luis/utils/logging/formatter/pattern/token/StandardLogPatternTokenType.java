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
import net.luis.utils.logging.formatter.LogTokenFormatter;
import net.luis.utils.logging.formatter.pattern.token.config.LogPatternTokenConfig;
import net.luis.utils.logging.formatter.pattern.token.config.base.LogPatternBooleanValueConfig;
import net.luis.utils.logging.formatter.pattern.token.config.base.LogPatternStringValueConfig;
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
	
	PLAIN_TEXT("PlainText", Set.of(), (_, _, content) -> content),
	LOGGER_NAME("LoggerName", Set.of("logger", "name", "loggerName", "ln"), true, false, LogPatternLoggerNameConfig.CODEC, LogTokenFormatter.configBased()),
	LEVEL("Level", Set.of("level", "lvl", "l"), true, false, LogPatternLevelConfig.CODEC),
	MARKER("Marker", Set.of("marker", "mk"), true, false, LogPatternMarkerConfig.CODEC),
	THREAD_NAME("ThreadName", Set.of("threadName", "tn"), true, false, LogPatternStringValueConfig.CODEC),
	THREAD_ID("ThreadId", Set.of("threadId", "ti"), (event, _, _) -> {
		return String.valueOf(event.threadInfo().id());
	}),
	THREAD_GROUP("ThreadGroup", Set.of("threadGroup", "tg"), true, false, LogPatternStringValueConfig.CODEC),
	THREAD_PRIORITY("ThreadPriority", Set.of("threadPriority", "tp")),
	THREAD_VIRTUAL("ThreadVirtual", Set.of("threadVirtual", "tv"), true, false, LogPatternBooleanValueConfig.CODEC),
	THREAD_DAEMON("ThreadDaemon", Set.of("threadDaemon", "td"), true, false, LogPatternBooleanValueConfig.CODEC),
	THREAD_STATE("ThreadState", Set.of("threadState", "ts"), true, false, LogPatternStringValueConfig.CODEC),
	TIMESTAMP("Timestamp", Set.of("timestamp", "time", "date", "t"), true, false, LogPatternTimestampConfig.CODEC),
	CONTEXT_MAP("ContextMap", Set.of("context", "ctx"), true, false, LogPatternContextMapConfig.CODEC),
	CONTEXT_ITEM("ContextItem", Set.of("context:", "ctx:"), true, false, LogPatternContextItemConfig.CODEC) {
		@Override
		public boolean isIdentifier(@Nullable String identifier) {
			if (identifier == null) {
				return false;
			}
			
			for (String tokenIdentifier : this.getIdentifiers()) {
				if (identifier.startsWith(tokenIdentifier)) {
					return true;
				}
			}
			return super.isIdentifier(identifier);
		}
	},
	EXCEPTION("Exception", Set.of("exception", "ex", "e"), true, false, LogPatternExceptionConfig.CODEC),
	MESSAGE("Message", Set.of("message", "msg", "m"), true, false, LogPatternMessageConfig.CODEC),
	SEQUENCE_NUMBER("SequenceNumber", Set.of("sequenceNumber", "sequence", "sqn"), (event, _, _) -> {
		return String.valueOf(event.sequenceNumber());
	}),
	NEWLINE("Newline", Set.of("newline", "nl"), true, false, LogPatternNewlineConfig.CODEC);
	
	private final String name;
	private final Set<String> identifiers;
	private final boolean supportsConfiguration;
	private final boolean requiresConfiguration;
	private final Optional<Codec<? extends LogPatternTokenConfig>> codec;
	
	StandardLogPatternTokenType(
		@NonNull String name, @NonNull Set<String> identifiers, @NonNull LogTokenFormatter<LogPatternTokenConfig> formatter
	) {
		this(name, identifiers, false, false, null, formatter);
	}
	
	<T extends LogPatternTokenConfig> StandardLogPatternTokenType(
		@NonNull String name, @NonNull Set<String> identifiers, boolean supportsConfiguration, boolean requiresConfiguration, @Nullable Codec<T> codec, @NonNull LogTokenFormatter<T> formatter
	) {
		this.name = Objects.requireNonNull(name, "Name must not be null");
		this.identifiers = Set.copyOf(Objects.requireNonNull(identifiers, "Identifiers must not be null"));
		this.supportsConfiguration = supportsConfiguration;
		this.requiresConfiguration = requiresConfiguration;
		this.codec = Optional.ofNullable(codec);
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
		return null;
	}
}
