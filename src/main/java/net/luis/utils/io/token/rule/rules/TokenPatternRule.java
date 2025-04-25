package net.luis.utils.io.token.rule.rules;

import net.luis.utils.io.token.rule.Match;
import net.luis.utils.io.token.rule.Rule;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Luis-St
 *
 */

public record TokenPatternRule(@NotNull Pattern pattern) implements Rule {
	
	public TokenPatternRule(@NotNull String regex) {
		this(Pattern.compile(Objects.requireNonNull(regex, "Regex must not be null")));
	}
	
	public TokenPatternRule {
		Objects.requireNonNull(pattern, "Pattern must not be null");
	}
	
	@Override
	public @Nullable Match match(@NotNull List<String> tokens, int startIndex) {
		Objects.requireNonNull(tokens, "Tokens must not be null");
		if (startIndex >= tokens.size()) {
			return null;
		}
		
		Matcher matcher = this.pattern.matcher(tokens.get(startIndex));
		if (matcher.matches()) {
			List<String> matchedTokens = Collections.singletonList(tokens.get(startIndex));
			return new Match(startIndex, startIndex + 1, matchedTokens, this);
		}
		return null;
	}
}
