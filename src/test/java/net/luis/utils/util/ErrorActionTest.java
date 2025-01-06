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

package net.luis.utils.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link ErrorAction}.<br>
 *
 * @author Luis-St
 */
class ErrorActionTest {
	
	@Test
	void handleIgnore() {
		assertDoesNotThrow(() -> ErrorAction.IGNORE.handle(null));
		assertDoesNotThrow(() -> ErrorAction.IGNORE.handle(new RuntimeException()));
	}
	
	@Test
	void handleThrow() {
		assertThrows(NullPointerException.class, () -> ErrorAction.THROW.handle(null));
		assertThrows(RuntimeException.class, () -> ErrorAction.THROW.handle(new RuntimeException()));
	}
}