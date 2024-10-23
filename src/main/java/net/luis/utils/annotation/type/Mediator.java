/*
 * LUtils
 * Copyright (C) 2024 Luis Staudt
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

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.*;

/**
 * Types that are annotated with this annotation are considered to be mediators<br>
 * between two or more types. They are used to facilitate communication.<br>
 * The mediator handles a many-to-many relationship between those types.<br>
 *
 * @author Luis-St
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Mediator {
	
	/**
	 * Returns the types that are mediated by this class.<br>
	 * @return The types
	 */
	@NotNull Class<?>[] value() default {};
}
