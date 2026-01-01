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
 * An annotation that marks a field to be included in the codec auto-mapping process.<br>
 * Fields marked with this annotation will be encoded and decoded by the codec auto-mapping system.<br>
 * <p>
 *     When a class has any fields annotated with this annotation, only those fields will be included in the codec,
 *     ignoring any non-annotated fields.
 * </p>
 * <p>
 *     If no fields are annotated with this annotation, the codec auto-mapping system will include all non-static,
 *     final fields that are not marked as transient.
 * </p>
 *
 * @see CodecAutoMapping
 *
 * @author Luis-St
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface CodecField {}
