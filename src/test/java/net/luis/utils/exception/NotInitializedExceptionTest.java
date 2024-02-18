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

package net.luis.utils.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link NotInitializedException}.<br>
 *
 * @author Luis-St
 */
class NotInitializedExceptionTest {
	
	@Test
	void constructor() {
		assertDoesNotThrow(() -> new NotInitializedException((String) null));
		assertDoesNotThrow(() -> new NotInitializedException((Throwable) null));
		assertDoesNotThrow(() -> new NotInitializedException(null, null));
	}
}