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

import java.lang.annotation.*;

/**
 * Types that are annotated with this annotation are considered as Facades.<br>
 * Facades are used to provide a simple interface to a complex subsystem.<br>
 * <p>
 *     Facades can also be used to extend multiple classes.<br>
 *     Therefor, the facade hides an instance of each class that should be extended<br>
 *     and provides the methods of the extended classes.<br>
 * </p>
 * <p>
 *     The logic of the methods provided by the facade may be different<br>
 *     from the logic from the standalone classes.<br>
 *     The facade can also keep methods internal or provide additional methods.<br>
 * </p>
 * @author Luis-St
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Facade {}
