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

package net.luis.utils.io.codec.constraint_new.network;

import net.luis.utils.io.codec.constraint_new.CharSequenceConstraint;
import net.luis.utils.io.codec.constraint_new.builder.LengthConstraintBuilder;
import org.jspecify.annotations.NonNull;

import java.util.function.UnaryOperator;

/**
 * Constraint interface for domain name validation operations.<br>
 * <p>
 *     This interface extends {@link CharSequenceConstraint} with methods for constraining domain names
 *     based on their structure (root domain vs subdomain).
 * </p>
 *
 * @author Luis-St
 *
 * @param <T> The type of the constraint configuration
 * @param <C> The return type of the constraint method (for fluent method chaining)
 */
public interface DomainConstraint<T, C> extends CharSequenceConstraint<T, C> {
	
	/**
	 * Applies a root domain constraint.<br>
	 * <p>
	 *     The returned type will validate that values are root domains (e.g., "example.com" rather than "sub.example.com").<br>
	 *     A root domain has exactly one dot separating the domain name and the top-level domain.
	 * </p>
	 *
	 * @return A new type with the applied root domain constraint
	 * @see #subDomain()
	 */
	@NonNull C rootDomain();
	
	/**
	 * Applies a subdomain constraint.<br>
	 * <p>
	 *     The returned type will validate that values are subdomains (e.g., "sub.example.com" rather than "example.com").<br>
	 *     A subdomain has two or more dots separating domain components.
	 * </p>
	 *
	 * @return A new type with the applied subdomain constraint
	 * @see #rootDomain()
	 */
	@NonNull C subDomain();

	/**
	 * Applies length constraints to the domain name using a builder.<br>
	 * <p>
	 *     This method provides a fluent API for configuring length constraints on domain names.<br>
	 *     The builder allows setting minimum length, maximum length, exact length, or length ranges.
	 * </p>
	 *
	 * @param builder The builder function to configure length constraints
	 * @return A new type with the applied length constraints
	 * @throws NullPointerException If the builder is null
	 * @see LengthConstraintBuilder
	 */
	@NonNull C length(@NonNull UnaryOperator<LengthConstraintBuilder> builder);
}
