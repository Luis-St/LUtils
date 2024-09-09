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
 * This annotation is used to mark a class as a mock object.<br>
 * Mock objects are used to simulate real objects in tests.<br>
 * They are used to test the behavior of other objects.<br>
 * <p>
 *     Mock objects should be only used in tests.<br>
 *     The mock object must not be tested itself,<br>
 *     it is assumed that the mock object works correctly.<br>
 * </p>
 * <p>
 *     A mock object must always be a non abstract class.<br>
 *     The class must be private or package-private.<br>
 *     In the case of package-private the class must also be final.<br>
 * </p>
 * <p>
 *     A mocked class must also be prefixed with "Mocked" or<br>
 *     suffixed with "Mock" to indicate that it is a mock object.<br>
 * </p>
 *
 * @author Luis-St
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MockObject {
	
	/**
	 * @return The class that is mocked by this mock object.<br>
	 */
	@NotNull Class<?> value();
}
