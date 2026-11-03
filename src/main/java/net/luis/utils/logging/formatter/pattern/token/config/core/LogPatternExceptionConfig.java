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

package net.luis.utils.logging.formatter.pattern.token.config.core;

import com.google.common.collect.Lists;
import net.luis.utils.io.codec.*;
import net.luis.utils.logging.event.LogEvent;
import net.luis.utils.logging.formatter.LogTokenFormatHelper;
import net.luis.utils.logging.formatter.pattern.LogPatternException;
import net.luis.utils.logging.formatter.pattern.token.config.LogPatternTokenConfig;
import net.luis.utils.logging.formatter.pattern.token.config.base.LogPatternDecorationConfig;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.*;

/**
 *
 * @author Luis-St
 *
 */

public record LogPatternExceptionConfig(
	@NonNull Optional<Integer> depth,
	@NonNull Optional<Boolean> omitCommonFrames,
	@NonNull Optional<Boolean> causes,
	@NonNull Optional<Integer> maxCauseDepth,
	@NonNull Optional<Boolean> suppressed,
	@NonNull Optional<Boolean> rootCauseFirst,
	@NonNull @Unmodifiable List<String> excludePackages,
	@NonNull Optional<LogPatternDecorationConfig> decoration
) implements LogPatternTokenConfig {
	
	public static final Codec<LogPatternExceptionConfig> CODEC = CodecBuilder.of(
		Codecs.INTEGER.optional().optionalFieldOf("depth", Optional.empty(), LogPatternExceptionConfig::depth),
		Codecs.BOOLEAN.optional().optionalFieldOf("omitCommonFrames", Optional.empty(), LogPatternExceptionConfig::omitCommonFrames),
		Codecs.BOOLEAN.optional().optionalFieldOf("causes", Optional.empty(), LogPatternExceptionConfig::causes),
		Codecs.INTEGER.optional().optionalFieldOf("maxCauseDepth", Optional.empty(), LogPatternExceptionConfig::maxCauseDepth),
		Codecs.BOOLEAN.optional().optionalFieldOf("suppressed", Optional.empty(), LogPatternExceptionConfig::suppressed),
		Codecs.BOOLEAN.optional().optionalFieldOf("rootCauseFirst", Optional.empty(), LogPatternExceptionConfig::rootCauseFirst),
		Codecs.STRING.list().optionalFieldOf("excludePackages", Lists.newArrayList(), LogPatternExceptionConfig::excludePackages),
		LogPatternDecorationConfig.CODEC.optional().optionalFieldOf("decoration", Optional.empty(), LogPatternExceptionConfig::decoration)
	).create(LogPatternExceptionConfig::new);
	
	public LogPatternExceptionConfig {
		Objects.requireNonNull(depth, "Depth must not be null");
		Objects.requireNonNull(omitCommonFrames, "Omit common frames must not be null");
		Objects.requireNonNull(causes, "Causes must not be null");
		Objects.requireNonNull(maxCauseDepth, "Max cause depth must not be null");
		Objects.requireNonNull(suppressed, "Suppressed must not be null");
		Objects.requireNonNull(rootCauseFirst, "Root cause first must not be null");
		Objects.requireNonNull(excludePackages, "Exclude packages must not be null");
		Objects.requireNonNull(decoration, "Decoration config must not be null");
		
		if (depth.isPresent() && depth.get() < 0) {
			throw new LogPatternException("Depth must not be negative");
		}
		if (maxCauseDepth.isPresent()) {
			if (maxCauseDepth.get() < 0) {
				throw new LogPatternException("Max cause depth must not be negative");
			}
			if (causes.isPresent() && !causes.get()) {
				throw new LogPatternException("Max cause depth can not be used if causes are disabled");
			}
		}
		if (rootCauseFirst.isPresent() && rootCauseFirst.get() && causes.isPresent() && !causes.get()) {
			throw new LogPatternException("Root cause first can not be used if causes are disabled");
		}
		if (excludePackages.stream().anyMatch(element -> element == null || element.isBlank())) {
			throw new LogPatternException("Exclude packages must not contain null or blank values");
		}
		
		excludePackages = Collections.unmodifiableList(excludePackages);
	}
	
	private static int countFramesInCommon(StackTraceElement @NonNull [] trace, StackTraceElement @Nullable [] enclosingTrace) {
		Objects.requireNonNull(trace, "Stack trace must not be null");
		if (enclosingTrace == null) {
			return 0;
		}
		
		int framesInCommon = 0;
		int traceIndex = trace.length - 1;
		int enclosingIndex = enclosingTrace.length - 1;
		while (traceIndex >= 0 && enclosingIndex >= 0 && trace[traceIndex].equals(enclosingTrace[enclosingIndex])) {
			framesInCommon++;
			traceIndex--;
			enclosingIndex--;
		}
		return framesInCommon;
	}
	
	@Override
	public @NonNull String format(@NonNull LogEvent event, @NonNull String plainTokenContent) {
		Objects.requireNonNull(event, "Log event must not be null");
		Objects.requireNonNull(plainTokenContent, "Plain token content must not be null");
		
		Throwable throwable = event.message().throwable();
		if (throwable == null) {
			return "";
		}
		
		List<String> lines = Lists.newArrayList();
		Set<Throwable> visited = Collections.newSetFromMap(new IdentityHashMap<>());
		if (this.rootCauseFirst.orElse(false)) {
			this.appendRootCauseFirst(lines, throwable, visited);
		} else {
			this.appendThrowable(lines, throwable, null, "", "", 0, visited);
		}
		return LogTokenFormatHelper.format(String.join(System.lineSeparator(), lines), Optional.empty(), Optional.empty(), this.decoration);
	}
	
	private boolean isCauseIncluded(int causeDepth) {
		return this.causes.orElse(true) && causeDepth < this.maxCauseDepth.orElse(Integer.MAX_VALUE);
	}
	
	private boolean isFrameExcluded(@NonNull StackTraceElement frame) {
		Objects.requireNonNull(frame, "Stack trace element must not be null");
		
		String className = frame.getClassName();
		for (String excludedPackage : this.excludePackages) {
			if (className.equals(excludedPackage) || className.startsWith(excludedPackage + ".")) {
				return true;
			}
		}
		return false;
	}
	
	private void appendRootCauseFirst(@NonNull List<String> lines, @NonNull Throwable throwable, @NonNull Set<Throwable> visited) {
		Objects.requireNonNull(lines, "Lines must not be null");
		Objects.requireNonNull(throwable, "Throwable must not be null");
		Objects.requireNonNull(visited, "Visited throwables must not be null");
		
		List<Throwable> chain = Lists.newArrayList();
		Throwable current = throwable;
		while (current != null && visited.add(current)) {
			chain.add(current);
			current = this.isCauseIncluded(chain.size() - 1) ? current.getCause() : null;
		}
		
		for (int i = chain.size() - 1; i >= 0; i--) {
			boolean root = i == chain.size() - 1;
			StackTraceElement[] enclosingTrace = root ? null : chain.get(i + 1).getStackTrace();
			this.appendSingleThrowable(lines, chain.get(i), enclosingTrace, "", root ? "" : "Wrapped by: ", visited);
		}
	}
	
	private void appendThrowable(
		@NonNull List<String> lines, @NonNull Throwable throwable, StackTraceElement @Nullable [] enclosingTrace,
		@NonNull String prefix, @NonNull String caption, int causeDepth, @NonNull Set<Throwable> visited
	) {
		Objects.requireNonNull(lines, "Lines must not be null");
		Objects.requireNonNull(throwable, "Throwable must not be null");
		Objects.requireNonNull(prefix, "Prefix must not be null");
		Objects.requireNonNull(caption, "Caption must not be null");
		Objects.requireNonNull(visited, "Visited throwables must not be null");
		
		if (!visited.add(throwable)) {
			lines.add(prefix + caption + "[CIRCULAR REFERENCE: " + throwable + "]");
			return;
		}
		
		StackTraceElement[] trace = this.appendSingleThrowable(lines, throwable, enclosingTrace, prefix, caption, visited);
		Throwable cause = throwable.getCause();
		if (cause != null && this.isCauseIncluded(causeDepth)) {
			this.appendThrowable(lines, cause, trace, prefix, "Caused by: ", causeDepth + 1, visited);
		}
	}
	
	private StackTraceElement @NonNull [] appendSingleThrowable(
		@NonNull List<String> lines, @NonNull Throwable throwable, StackTraceElement @Nullable [] enclosingTrace,
		@NonNull String prefix, @NonNull String caption, @NonNull Set<Throwable> visited
	) {
		Objects.requireNonNull(lines, "Lines must not be null");
		Objects.requireNonNull(throwable, "Throwable must not be null");
		Objects.requireNonNull(prefix, "Prefix must not be null");
		Objects.requireNonNull(caption, "Caption must not be null");
		Objects.requireNonNull(visited, "Visited throwables must not be null");
		
		lines.add(prefix + caption + throwable);
		
		StackTraceElement[] trace = throwable.getStackTrace();
		int framesInCommon = this.omitCommonFrames.orElse(true) ? countFramesInCommon(trace, enclosingTrace) : 0;
		int uniqueFrames = trace.length - framesInCommon;
		int maxFrames = this.depth.orElse(Integer.MAX_VALUE);
		
		int index = 0;
		int shownFrames = 0;
		int excludedFrames = 0;
		for (; index < uniqueFrames && shownFrames < maxFrames; index++) {
			if (this.isFrameExcluded(trace[index])) {
				excludedFrames++;
			} else {
				lines.add(prefix + "\tat " + trace[index]);
				shownFrames++;
			}
		}
		if (excludedFrames > 0) {
			lines.add(prefix + "\t... " + excludedFrames + " filtered");
		}
		if (index < trace.length) {
			lines.add(prefix + "\t... " + (trace.length - index) + " more");
		}
		
		if (this.suppressed.orElse(true)) {
			for (Throwable suppressed : throwable.getSuppressed()) {
				this.appendThrowable(lines, suppressed, trace, prefix + "\t", "Suppressed: ", 0, visited);
			}
		}
		return trace;
	}
}
