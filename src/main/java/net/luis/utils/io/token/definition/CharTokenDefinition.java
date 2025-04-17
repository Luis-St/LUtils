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

record CharTokenDefinition(char token, @NotNull Optional<TokenCategory> category) implements TokenDefinition {
	
	CharTokenDefinition(char token) {
		this(token, Optional.empty());
	}
	
	CharTokenDefinition {
		Objects.requireNonNull(category, "Category must not be null");
	}
	
	@Override
	public boolean matches(@NotNull String word) {
		Objects.requireNonNull(word, "Word must not be null");
		return word.length() == 1 && word.charAt(0) == this.token;
	}
}
