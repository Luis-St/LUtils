package net.luis.utils.util.reflection;

/**
 *
 * @author Luis-st
 *
 */

public record ParameterInfo(Object parameter, boolean hasName, String parameterName, boolean nullable) {
	
	public Class<?> type() {
		return this.parameter.getClass();
	}
}
