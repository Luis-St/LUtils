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

public record SequenceRule(@NotNull List<Rule> rules) implements Rule {
	
	public SequenceRule {
		Objects.requireNonNull(rules, "Rules must not be null");
		if (rules.isEmpty()) {
			throw new IllegalArgumentException("Rules must not be empty");
		}
		rules = List.copyOf(rules);
		
		for (Rule rule : rules) {
			Objects.requireNonNull(rule, "Rules must not contain a null element");
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
		for (Rule rule : this.rules) {
			Match match = rule.match(tokens, currentIndex);
			if (match == null) {
				return null;
			}
			
			matchedTokens.addAll(match.matchedTokens());
			currentIndex = match.endIndex();
		}
		return new Match(startIndex, currentIndex, matchedTokens, this);
	}
}
