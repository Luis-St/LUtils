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

package net.luis.utils.io.data.config;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.*;

/**
 * Annotation for record components of an io configuration.<br>
 * The annotated record component is used for reading only.<br>
 * <p>
 *     The value of the annotation defines dependencies to other record components.<br>
 *     If the value is empty, the record component is not dependent on any other record component.<br>
 *     The dependencies must also be annotated with {@link ReadOnly}.
 * </p>
 * <p>
 *     The dependencies must be boolean expressions and can be combined with the following operators:<br>
 *     {@code key1 == 10} - The record component is dependent on key1 only if key1 is equal to 10.<br>
 *     {@code key1 && key2} - The record component is dependent on key1 and key2.
 * </p>
 * <p>
 *     The dependencies are not checked or used, they are only for documentation purposes.
 * </p>
 *
 * @author Luis-St
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.RECORD_COMPONENT)
public @interface ReadOnly {
	
	/**
	 * The dependencies of the annotated record component.<br>
	 * Each dependency must be a boolean expression.<br>
	 * @return The dependencies
	 */
	String @NotNull [] value() default {};
}
