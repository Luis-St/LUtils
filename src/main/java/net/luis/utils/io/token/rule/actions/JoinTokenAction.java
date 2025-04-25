package net.luis.utils.io.token.rule.actions;

import net.luis.utils.io.token.rule.Match;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;

/**
 *
 * @author Luis-St
 *
 */

public record JoinTokenAction(@NotNull String delimiter) implements TokenAction {
	
	public JoinTokenAction {
		Objects.requireNonNull(delimiter, "Delimiter must not be null");
	}
	
	@Override
	public @NotNull @Unmodifiable List<String> apply(@NotNull Match match) {
		Objects.requireNonNull(match, "Match must not be null");
		
		String joined = String.join(this.delimiter, match.matchedTokens());
		return Collections.singletonList(joined);
	}
}
