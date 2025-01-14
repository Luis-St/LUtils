/*
 * LUtils
 * Copyright (C) 2025 Luis Staudt
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
 * Types that are annotated with this annotation will be instantiated only once.<br>
 * It might be possible to get the instance of the class by:
 * <ul>
 *     <li>Calling the static method {@code getInstance()}</li>
 *     <li>Accessing the public static field {@code INSTANCE}</li>
 * </ul>
 *
 * If an interface is annotated with this annotation,<br>
 * all classes that implement this interface will be instantiated only once.<br>
 *
 * @author Luis-St
 */
@Inherited
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Singleton {
	
	/**
	 * Returns the method to get the instance of the singleton class.<br>
	 * @return The method to get the singleton
	 */
	@NotNull Method value() default Method.UNDEFINED;
	
	/**
	 * The method to obtain the instance of the singleton class.
	 *
	 * @author Luis-St
	 */
	enum Method {
		/**
		 * The instance of the singleton class can be obtained by calling the static method {@code getInstance()}.<br>
		 */
		METHOD,
		/**
		 * The instance of the singleton class can be obtained by accessing the public static field {@code INSTANCE}.<br>
		 */
		FIELD,
		/**
		 * The instance of the singleton class can be obtained by custom method.<br>
		 */
		CUSTOM,
		/**
		 * The instance of the singleton class can be not obtained.<br>
		 */
		NOT_POSSIBLE,
		/**
		 * The method to obtain the instance of the singleton class is undefined.<br>
		 */
		UNDEFINED
	}
}
