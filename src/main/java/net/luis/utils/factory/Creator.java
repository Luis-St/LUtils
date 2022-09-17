package net.luis.utils.factory;

import org.jetbrains.annotations.NotNull;

/**
 *
 * @author Luis-st
 *
 */

@FunctionalInterface
public interface Creator<T> {
	
	@NotNull
	T create();
	
}
