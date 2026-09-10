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
	
	PLAIN_TEXT("PlainText", Set.of()),
	LOGGER_NAME("LoggerName", Set.of("logger", "name", "loggerName", "ln"), true, false, LogPatternLoggerNameConfig.class),
	LEVEL("Level", Set.of("level", "lvl", "l"), true, false, LogPatternLevelConfig.class),
	MARKER("Marker", Set.of("marker", "m"), true, false, LogPatternMarkerConfig.class),
	THREAD_NAME("ThreadName", Set.of("threadName", "tn"), true, false, LogPatternStringValueConfig.class),
	THREAD_ID("ThreadId", Set.of("threadId", "ti")),
	THREAD_GROUP("ThreadGroup", Set.of("threadGroup", "tg"), true, false, LogPatternStringValueConfig.class),
	THREAD_PRIORITY("ThreadPriority", Set.of("threadPriority", "tp"), true, false, null),
	THREAD_VIRTUAL("ThreadVirtual", Set.of("threadVirtual", "tv"), true, false, LogPatternBooleanValueConfig.class),
	THREAD_DAEMON("ThreadDaemon", Set.of("threadDaemon", "td"), true, false, LogPatternBooleanValueConfig.class),
	THREAD_STATE("ThreadState", Set.of("threadSet", "ts"), true, false, LogPatternStringValueConfig.class),
	TIMESTAMP("Timestamp", Set.of("timestamp", "time", "date", "ts", "t"), true, false, LogPatternTimestampConfig.class),
	CONTEXT_MAP("ContextMap", Set.of("context", "ctx"), true, false, LogPatternContextMapConfig.class),
	CONTEXT_ITEM("ContextItem", Set.of(), true, false, LogPatternContextItemConfig.class),
	EXCEPTION("Exception", Set.of("exception", "ex", "e"), true, false, LogPatternExceptionConfig.class),
	MESSAGE("Message", Set.of("message", "msg", "m"), true, false, LogPatternMessageConfig.class),
	SEQUENCE_NUMBER("SequenceNumber", Set.of("sequenceNumber", "sequence", "sqn")),
	NEWLINE("Newline", Set.of("newline", "nl"), true, false, LogPatternNewlineConfig.class);
	
	private final String name;
	private final Set<String> identifiers;
	private final boolean supportsConfiguration;
	private final boolean requiresConfiguration;
	private final Optional<Class<?>> configurationType;
	
	StandardLogPatternTokenType(
		@NonNull String name, @NonNull Set<String> identifiers
	) {
		this(name, identifiers, false, false, null);
	}
	
	StandardLogPatternTokenType(
		@NonNull String name, @NonNull Set<String> identifiers, boolean supportsConfiguration, boolean requiresConfiguration, @Nullable Class<?> configurationType
	) {
		this.name = Objects.requireNonNull(name, "Name must not be null");
		this.identifiers = Set.copyOf(Objects.requireNonNull(identifiers, "Identifiers must not be null"));
		this.supportsConfiguration = supportsConfiguration;
		this.requiresConfiguration = requiresConfiguration;
		this.configurationType = Optional.ofNullable(configurationType);
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
	public @NonNull Optional<Class<?>> getConfigurationType() {
		return this.configurationType;
	}
}
