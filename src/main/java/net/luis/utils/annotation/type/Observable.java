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

package net.luis.utils.annotation.type;

import org.jspecify.annotations.NonNull;

import java.lang.annotation.*;

/**
 * Types that are annotated with this annotation are observable.<br>
 * This means that there are methods that can be called to add and remove listeners.<br>
 * The annotated type might be associated with an event system.<br>
 *
 * @author Luis-St
 */
@Inherited
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Observable {
	
	/**
	 * Returns the type of the observer that is used by this observable.<br>
	 * The type might be used to automatically register and unregister listeners.<br>
	 * If the type is {@code Object.class}, no type is specified.<br>
	 *
	 * @return The type of the observer
	 */
	@NonNull Class<?> value() default Object.class;
	
	/**
	 * Returns the methods that are used to add remove listeners.<br>
	 * The methods which are returned by this method must be public and non-static.<br>
	 * They must have exactly one parameter of the type that is returned by {@link #value()}.<br>
	 * The methods can include wildcard characters or regular expressions.<br>
	 *
	 * @return The methods that are used to add and remove listeners
	 */
	@NonNull String[] methods() default {};
}
