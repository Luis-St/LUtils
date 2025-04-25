package net.luis.utils.io.token.rule;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

/**
 *
 * @author Luis-St
 *
 */

public record Match(
	int startIndex,
	int endIndex, // exclusive
	@NotNull List<String> matchedTokens,
	@NotNull Rule matchingRule
) {
	
	public Match {
		Objects.requireNonNull(matchedTokens, "Matched tokens must not be null");
		Objects.requireNonNull(matchingRule, "Matching rule must not be null");
	}
}
