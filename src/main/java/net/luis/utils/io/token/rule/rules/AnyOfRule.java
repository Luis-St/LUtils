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

public record AnyOfRule(@NotNull Set<String> allowedTokens) implements Rule {
	
	public AnyOfRule {
		allowedTokens = Set.copyOf(Objects.requireNonNull(allowedTokens, "Allowed tokens must not be null"));
	}
	
	@Override
	public @Nullable Match match(@NotNull List<String> tokens, int startIndex) {
		Objects.requireNonNull(tokens, "Tokens must not be null");
		if (startIndex >= tokens.size()) {
			return null;
		}
		
		String token = tokens.get(startIndex);
		if (this.allowedTokens.contains(token)) {
			List<String> matchedTokens = Collections.singletonList(token);
			return new Match(startIndex, startIndex + 1, matchedTokens, this);
		}
		return null;
	}
}
