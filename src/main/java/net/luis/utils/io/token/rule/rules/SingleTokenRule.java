package net.luis.utils.io.token.rule.rules;

import net.luis.utils.io.token.rule.Match;
import net.luis.utils.io.token.rule.Rule;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 *
 * @author Luis-St
 *
 */

public record SingleTokenRule(@NotNull String token) implements Rule {
	
	public SingleTokenRule {
		Objects.requireNonNull(token, "Token must not be null");
	}
	
	@Override
	public @Nullable Match match(@NotNull List<String> tokens, int startIndex) {
		Objects.requireNonNull(tokens, "Tokens must not be null");
		if (startIndex >= tokens.size()) {
			return null;
		}
		
		if (tokens.get(startIndex).equals(this.token)) {
			List<String> matchedTokens = Collections.singletonList(tokens.get(startIndex));
			return new Match(startIndex, startIndex + 1, matchedTokens, this);
		}
		return null;
	}
}
