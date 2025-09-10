package net.luis.utils.io.token.rule.rules.reference;

import net.luis.utils.io.token.TokenStream;
import net.luis.utils.io.token.rule.TokenRuleMatch;
import net.luis.utils.io.token.rule.rules.*;
import net.luis.utils.io.token.tokens.Token;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

/**
 * Token rule that references another token rule or a list of tokens by a specified key.<br>
 * The referenced rule or tokens are looked up in the context using the key.<br>
 * The passed reference type determines whether a rule or a list of tokens is referenced.<br>
 *
 * @author Luis-St
 *
 * @param key The key used to look up the referenced rule or tokens in the context
 * @param type The type of reference (rule or tokens)
 */
public record ReferenceTokenRule(
	@NotNull String key,
	@NotNull ReferenceType type
) implements TokenRule {
	
	/**
	 * Creates a new reference token rule using the given key and reference type.<br>
	 *
	 * @param key The key used to look up the referenced rule or tokens in the context
	 * @param type The type of reference (rule or tokens)
	 * @throws NullPointerException If the key or reference type is null
	 * @throws IllegalArgumentException If the key is empty
	 */
	public ReferenceTokenRule {
		Objects.requireNonNull(key, "Key must not be null");
		Objects.requireNonNull(type, "Reference type must not be null");
		
		if (key.isEmpty()) {
			throw new IllegalArgumentException("Key must not be empty");
		}
	}
	
	@Override
	public @Nullable TokenRuleMatch match(@NotNull TokenStream stream, @NotNull TokenRuleContext ctx) {
		Objects.requireNonNull(stream, "Token stream must not be null");
		Objects.requireNonNull(ctx, "Token rule context must not be null");
		
		TokenStream workingStream = stream.copyWithCurrentIndex();
		TokenRuleMatch match = switch (this.type) {
			case RULE -> this.matchReferencedRule(workingStream, ctx);
			case TOKENS -> this.matchReferencedTokens(workingStream, ctx);
		};
		
		if (match != null) {
			stream.advanceTo(workingStream);
			return match;
		}
		return null;
	}
	
	/**
	 * Matches the referenced rule from the context using the specified key.<br>
	 * If the rule is found, it attempts to match it against the provided token stream and context.<br>
	 * If the match is successful, it returns the resulting match, otherwise it returns null.<br>
	 *
	 * @param stream The token stream to match against
	 * @param ctx The token rule context containing the referenced rules
	 * @return The resulting token rule match if successful, otherwise null
	 * @throws NullPointerException If the token stream or context is null
	 */
	private @Nullable TokenRuleMatch matchReferencedRule(@NotNull TokenStream stream, @NotNull TokenRuleContext ctx) {
		Objects.requireNonNull(stream, "Token stream must not be null");
		Objects.requireNonNull(ctx, "Token rule context must not be null");
		
		TokenRule tokenRule = ctx.getRuleReference(this.key);
		if (tokenRule != null) {
			return tokenRule.match(stream, ctx);
		}
		return null;
	}
	
	/**
	 * Matches the referenced tokens from the context using the specified key.<br>
	 * If the tokens are found, it creates a sequence rule from them and attempts to match it against the provided token stream and context.<br>
	 * If the match is successful, it returns the resulting match, otherwise it returns null.<br>
	 *
	 * @param stream The token stream to match against
	 * @param ctx The token rule context containing the referenced tokens
	 * @return The resulting token rule match if successful, otherwise null
	 * @throws NullPointerException If the token stream or context is null
	 */
	private @Nullable TokenRuleMatch matchReferencedTokens(@NotNull TokenStream stream, @NotNull TokenRuleContext ctx) {
		Objects.requireNonNull(stream, "Token stream must not be null");
		Objects.requireNonNull(ctx, "Token rule context must not be null");
		if (!stream.hasToken()) {
			return null;
		}
		
		List<Token> tokens = ctx.getCapturedTokens(this.key);
		if (tokens != null) {
			TokenRule rule = TokenRules.sequence(
				tokens.stream().map(token -> TokenRules.value(token.value(), false)).toList()
			);
			return rule.match(stream, ctx);
		}
		return null;
	}
}
