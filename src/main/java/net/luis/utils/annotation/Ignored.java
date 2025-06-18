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

package net.luis.utils.annotation;

import java.lang.annotation.*;

/**
 * Annotations to indicate that a parameter or local variable is ignored.
 * <p>
 *     Will be removed after the final release of java's unnamed variables.<br>
 * </p>
 *
 * @see Always
 * @see Maybe
 * @see Never
 *
 * @author Luis-St
 */
public final class Ignored {
	
	/**
	 * Private constructor to prevent instantiation.<br>
	 * This is a static helper class.<br>
	 */
	private Ignored() {}
	
	/**
	 * Indicates that the parameter or local variable is always ignored.<br>
	 * Passing a null value to a parameter or local variable that is annotated with this annotation<br>
	 * will be safe and will not cause any errors.<br>
	 *
	 * @author Luis-St
	 */
	@Inherited
	@Documented
	@Target({ ElementType.PARAMETER, ElementType.LOCAL_VARIABLE })
	@Retention(RetentionPolicy.CLASS)
	public @interface Always {}
	
	/**
	 * Indicates that the parameter or local variable is maybe ignored.<br>
	 * The reason for this could be that there is a rare case where the parameter or local variable<br>
	 * is used or that passing a null value to the parameter or local variable will cause an error.<br>
	 *
	 * @author Luis-St
	 */
	@Inherited
	@Documented
	@Target({ ElementType.PARAMETER, ElementType.LOCAL_VARIABLE })
	@Retention(RetentionPolicy.CLASS)
	public @interface Maybe {}
	
	/**
	 * Indicates that a parameter is never ignored.<br>
	 * This is the default behavior of any parameter in java.<br>
	 * <p>
	 *     The annotation is only used to reset the behavior of a parameter that is<br>
	 *     annotated with {@link Always} or {@link Maybe}.<br>
	 *     An example would be an overridden method where the superclass ignores a parameter.
	 * </p>
	 *
	 * @author Luis-St
	 */
	@Inherited
	@Documented
	@Target(ElementType.PARAMETER)
	@Retention(RetentionPolicy.CLASS)
	public @interface Never {}
}
