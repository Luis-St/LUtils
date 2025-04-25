package net.luis.utils.io.token.rule.actions;

import com.google.common.collect.Lists;
import net.luis.utils.io.token.rule.Match;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;

/**
 *
 * @author Luis-St
 *
 */

public record WrapTokenAction(@NotNull String prefix, @NotNull String suffix) implements TokenAction {
	
	public WrapTokenAction {
		Objects.requireNonNull(prefix, "Prefix must not be null");
		Objects.requireNonNull(suffix, "Suffix must not be null");
	}
	
	@Override
	public @NotNull @Unmodifiable List<String> apply(@NotNull Match match) {
		Objects.requireNonNull(match, "Match must not be null");
		
		List<String> tokens = match.matchedTokens();
		if (tokens.isEmpty()) {
			return Collections.emptyList();
		}
		
		List<String> result = Lists.newArrayListWithExpectedSize(tokens.size());
		result.add(this.prefix + tokens.getFirst());
		
		for (int i = 1; i < tokens.size() - 1; i++) {
			result.add(tokens.get(i));
		}
		
		if (tokens.size() > 1) {
			result.add(tokens.getLast() + this.suffix);
		} else {
			String firstToken = result.removeFirst();
			result.add(firstToken + this.suffix);
		}
		return result;
	}
}
