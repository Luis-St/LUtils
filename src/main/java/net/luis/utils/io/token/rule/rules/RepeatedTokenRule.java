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

public record RepeatedTokenRule(
	@NotNull Rule tokenRule,
	int minOccurrences,
	int maxOccurrences // -1 means unlimited
) implements Rule {
	
	public RepeatedTokenRule(@NotNull Rule tokenRule, int occurrences) {
		this(tokenRule, occurrences, occurrences);
	}
	
	public RepeatedTokenRule {
		Objects.requireNonNull(tokenRule, "Token rule must not be null");
		if (minOccurrences < 0) {
			throw new IllegalArgumentException("Min occurrences must not be negative");
		}
		if (maxOccurrences != -1 && maxOccurrences < minOccurrences) {
			throw new IllegalArgumentException("Max occurrences must not be less than minOccurrences");
		}
	}
	
	@Override
	public @Nullable Match match(@NotNull List<String> tokens, int startIndex) {
		Objects.requireNonNull(tokens, "Tokens must not be null");
		if (startIndex >= tokens.size()) {
			return null;
		}
		
		int currentIndex = startIndex;
		List<String> matchedTokens = new ArrayList<>();
		int occurrences = 0;
		while (currentIndex < tokens.size() && (this.maxOccurrences == -1 || occurrences < this.maxOccurrences)) {
			Match match = this.tokenRule.match(tokens, currentIndex);
			if (match == null) {
				break;
			}
			
			matchedTokens.addAll(match.matchedTokens());
			currentIndex = match.endIndex();
			occurrences++;
		}
		
		if (occurrences >= this.minOccurrences) {
			return new Match(startIndex, currentIndex, matchedTokens, this);
		}
		return null;
	}
}
