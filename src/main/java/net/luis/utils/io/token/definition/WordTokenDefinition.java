package net.luis.utils.io.token.definition;

import net.luis.utils.io.token.TokenCategory;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;

/**
 *
 * @author Luis-St
 *
 */

final class WordTokenDefinition implements TokenDefinition {
	
	@Override
	public @NotNull Optional<TokenCategory> category() {
		return Optional.empty();
	}
	
	@Override
	public boolean matches(@NotNull String word) {
		Objects.requireNonNull(word, "Word must not be null");
		return word.length() > 1;
	}
	
	@Override
	public String toString() {
		return "WORD";
	}
}
