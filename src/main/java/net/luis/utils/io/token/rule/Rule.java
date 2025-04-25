package net.luis.utils.io.token.rule;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 *
 * @author Luis-St
 *
 */

@FunctionalInterface
public interface Rule {
	
	@Nullable Match match(@NotNull List<String> tokens, int startIndex);
}
