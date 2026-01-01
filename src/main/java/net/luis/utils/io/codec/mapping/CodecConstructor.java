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

package net.luis.utils.io.codec.mapping;

import java.lang.annotation.*;

/**
 * An annotation that marks a constructor to be used by the codec auto-mapping system.<br>
 * When a class has multiple constructors, one can be annotated with this annotation to indicate<br>
 * that it should be used for creating instances during codec decoding.<br>
 * <p>
 *     The annotated constructor must have parameters that match the class's codec components in both number and type compatibility.
 * </p>
 * <p>
 *     If a class has only one constructor, this annotation is optional.
 * </p>
 *
 * @see CodecAutoMapping
 *
 * @author Luis-St
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.CONSTRUCTOR)
public @interface CodecConstructor {}
