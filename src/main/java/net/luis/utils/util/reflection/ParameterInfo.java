package net.luis.utils.util.reflection;

import org.jetbrains.annotations.NotNull;

/**
 *
 * @author Luis-st
 *
 */

public record ParameterInfo(@NotNull Object parameter, boolean hasName, @NotNull String parameterName, boolean nullable) {
	
	public @NotNull Class<?> type() {
		return this.parameter.getClass();
	}
}
