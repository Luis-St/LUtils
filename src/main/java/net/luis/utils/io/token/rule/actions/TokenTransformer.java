package net.luis.utils.io.token.rule.actions;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

/**
 *
 * @author Luis-St
 *
 */

@FunctionalInterface
public interface TokenTransformer {
	
	@NotNull @Unmodifiable
	List<String> transform(@NotNull List<String> tokens);
}
