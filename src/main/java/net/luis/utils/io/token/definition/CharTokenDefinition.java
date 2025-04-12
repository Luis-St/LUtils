package net.luis.utils.io.token.definition;

import net.luis.utils.io.token.TokenCategory;
import org.jetbrains.annotations.NotNull;

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
	
	@Override
	public boolean matches(@NotNull String word) {
		return word.length() == 1 && word.charAt(0) == this.token;
	}
}
