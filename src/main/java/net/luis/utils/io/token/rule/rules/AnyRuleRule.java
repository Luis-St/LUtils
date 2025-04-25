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

public record AnyRuleRule(@NotNull Set<Rule> rules) implements Rule {
	
	public AnyRuleRule {
		rules = Set.copyOf(Objects.requireNonNull(rules, "Rules must not be null"));
		for (Rule rule : rules) {
			Objects.requireNonNull(rule, "Rules must not contain a null element");
		}
	}
	
	@Override
	public @Nullable Match match(@NotNull List<String> tokens, int startIndex) {
		Objects.requireNonNull(tokens, "Tokens must not be null");
		
		for (Rule rule : this.rules) {
			Match match = rule.match(tokens, startIndex);
			if (match != null) {
				return match;
			}
		}
		return null;
	}
}
