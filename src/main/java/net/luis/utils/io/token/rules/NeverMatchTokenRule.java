/*
 * LUtils
 * Copyright (C) 2026 Luis Staudt
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package net.luis.utils.io.token.rules;

import net.luis.utils.io.token.TokenRuleMatch;
import net.luis.utils.io.token.context.TokenRuleContext;
import net.luis.utils.io.token.stream.TokenStream;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

/**
 * A token rule that never matches.<br>
 * This class is implemented as a singleton and can be accessed via {@link TokenRules#neverMatch()} or {@link #INSTANCE}.<br>
 *
 * @author Luis-St
 */
public final class NeverMatchTokenRule implements TokenRule {
	
	/**
	 * The singleton instance of this class.<br>
	 */
	public static final NeverMatchTokenRule INSTANCE = new NeverMatchTokenRule();
	
	/**
	 * Private constructor to prevent instantiation.<br>
	 */
	private NeverMatchTokenRule() {}
	
	@Override
	public @Nullable TokenRuleMatch match(@NonNull TokenStream stream, @NonNull TokenRuleContext ctx) {
		Objects.requireNonNull(stream, "Token stream must not be null");
		Objects.requireNonNull(ctx, "Token rule context must not be null");
		return null;
	}
	
	@Override
	public @NonNull TokenRule not() {
		return AlwaysMatchTokenRule.INSTANCE;
	}
}
