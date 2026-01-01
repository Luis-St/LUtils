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

import org.jspecify.annotations.NonNull;

import java.lang.annotation.*;

/**
 * An annotation that provides generic type information for fields or record components.<br>
 * This annotation is used by the codec auto-mapping system to determine the correct codec
 * for generic types like {@code List<T>}, {@code Map<K, V>}, or {@code Optional<T>}.<br>
 * <p>
 *     Java's type erasure removes generic type information at runtime, so this annotation allows
 *     explicitly specifying the generic parameters that should be used when creating codecs.<br>
 *     Example usage:
 * </p>
 * <pre><code>
 *     &#64;GenericInfo({String.class, Integer.class}) private Map&lt;String, Integer&gt; map;
 *     &#64;GenericInfo({ List.class, List.class, String.class, Integer.class }) private Either&lt;List&lt;String&gt;, List&lt;Integer&gt;&gt;
 * </code></pre>
 *
 * @see CodecAutoMapping
 *
 * @author Luis-St
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.RECORD_COMPONENT, ElementType.FIELD })
public @interface GenericInfo {
	
	/**
	 * Specifies the generic type parameters for the annotated element.<br>
	 * The order of the classes should match the order of the generic parameters.<br>
	 * For nested generic types, all type parameters should be listed in order.<br>
	 *
	 * @return An array of classes representing the generic type parameters
	 */
	Class<?> @NonNull [] value() default {};
}
