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

record StringTokenDefinition(@NotNull String token, boolean equalsIgnoreCase, @NotNull Optional<TokenCategory> category) implements TokenDefinition {
	
	StringTokenDefinition(@NotNull String token, boolean equalsIgnoreCase) {
		this(token, equalsIgnoreCase, Optional.empty());
	}
	
	StringTokenDefinition {
		Objects.requireNonNull(token, "Token must not be null");
		Objects.requireNonNull(category, "Category must not be null");
	}
	
	@Override
	public boolean matches(@NotNull String word) {
		Objects.requireNonNull(word, "Word must not be null");
		return this.equalsIgnoreCase ? word.equalsIgnoreCase(this.token) : word.equals(this.token);
	}
}
