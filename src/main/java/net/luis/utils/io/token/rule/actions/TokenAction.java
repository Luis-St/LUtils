package net.luis.utils.io.token.rule.actions;

import net.luis.utils.io.token.rule.Match;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

/**
 *
 * @author Luis-St
 *
 */

@FunctionalInterface
public interface TokenAction {
	
	@NotNull @Unmodifiable
	List<String> apply(@NotNull Match match);
}
