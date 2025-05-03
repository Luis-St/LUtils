package net.luis.utils.io.token.rule.rules;

import net.luis.utils.io.token.rule.TokenRuleMatch;
import net.luis.utils.io.token.tokens.Token;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

/**
 *
 * @author Luis-St
 *
 */

public record OptionalTokenRule(
	@NotNull TokenRule tokenRule
) implements TokenRule {
	
	@Override
	public @Nullable TokenRuleMatch match(@NotNull List<Token> tokens, int startIndex) {
		Objects.requireNonNull(tokens, "Tokens must not be null");
		if (startIndex >= tokens.size()) {
			return null;
		}
		
		TokenRuleMatch match = this.tokenRule.match(tokens, startIndex);
		if (match != null) {
			return match;
		}
		return TokenRuleMatch.empty(startIndex);
	}
}
