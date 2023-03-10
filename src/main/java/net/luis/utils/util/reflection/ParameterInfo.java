package net.luis.utils.util.reflection;

import org.jetbrains.annotations.NotNull;

/**
 *
 * @author Luis-st
 *
 */

public record ParameterInfo(Object parameter, boolean hasName, String parameterName, boolean nullable) {
	
	public @NotNull Class<?> type() {
		return this.parameter.getClass();
	}
}
