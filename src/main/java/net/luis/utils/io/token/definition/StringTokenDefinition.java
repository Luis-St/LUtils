package net.luis.utils.io.token.definition;

import net.luis.utils.io.token.TokenCategory;
import org.jetbrains.annotations.NotNull;

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
	
	@Override
	public boolean matches(@NotNull String word) {
		return this.equalsIgnoreCase ? word.equalsIgnoreCase(this.token) : word.equals(this.token);
	}
}
