package net.luis.utils.io.token.rule.rules.reference;

import net.luis.utils.io.token.TokenStream;
import net.luis.utils.io.token.rule.TokenRuleMatch;
import net.luis.utils.io.token.rule.rules.TokenRule;
import net.luis.utils.io.token.rule.rules.TokenRuleContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Token rule that captures the tokens matched by another token rule and stores them in the context under a specified key.<br>
 * The captured tokens can be retrieved later using the key.<br>
 *
 * @author Luis-St
 *
 * @param tokenRule The token rule to capture tokens from
 * @param key The key under which to store the captured tokens in the context
 */
public record CaptureTokenRule(
	@NotNull String key,
	@NotNull TokenRule tokenRule
) implements TokenRule {
	
	/**
	 * Creates a new capture token rule using the given token rule and key.<br>
	 *
	 * @param tokenRule The token rule to capture tokens from
	 * @param key The key under which to store the captured tokens in the context
	 * @throws NullPointerException If the token rule or key is null
	 * @throws IllegalArgumentException If the key is empty
	 */
	public CaptureTokenRule {
		Objects.requireNonNull(key, "Key must not be null");
		Objects.requireNonNull(tokenRule, "Token rule must not be null");
		
		if (key.isEmpty()) {
			throw new IllegalArgumentException("Key must not be empty");
		}
	}
	
	@Override
	public @Nullable TokenRuleMatch match(@NotNull TokenStream stream, @NotNull TokenRuleContext ctx) {
		Objects.requireNonNull(stream, "Token stream must not be null");
		Objects.requireNonNull(ctx, "Token rule context must not be null");
		
		TokenRuleMatch match = this.tokenRule.match(stream, ctx);
		if (match != null) {
			ctx.captureTokens(this.key, match.matchedTokens());
			return match;
		}
		return null;
	}
	
	@Override
	public @NotNull TokenRule not() {
		return new CaptureTokenRule(this.key, this.tokenRule.not());
	}
}
