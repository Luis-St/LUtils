package net.luis.utils.io.token.rule.actions;

import net.luis.utils.io.token.rule.Match;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Objects;

/**
 *
 * @author Luis-St
 *
 */

public record TransformTokenAction(@NotNull TokenTransformer transformer) implements TokenAction {
	
	public TransformTokenAction {
		Objects.requireNonNull(transformer, "Transformer must not be null");
	}
	
	@Override
	public @NotNull @Unmodifiable List<String> apply(@NotNull Match match) {
		Objects.requireNonNull(match, "Match must not be null");
		return this.transformer.transform(match.matchedTokens());
	}
}
