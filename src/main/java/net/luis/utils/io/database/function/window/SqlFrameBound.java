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

package net.luis.utils.io.database.function.window;

import net.luis.utils.io.database.function.window.frame.bound.*;
import org.jspecify.annotations.NonNull;

/**
 *
 * @author Luis-St
 *
 */

public interface SqlFrameBound {
	
	static @NonNull SqlFrameBound unboundedPreceding() {
		return new UnboundedPrecedingFrameBound();
	}
	
	static @NonNull SqlFrameBound preceding(int offset) {
		return new PrecedingFrameBound(offset);
	}
	
	static @NonNull SqlFrameBound currentRow() {
		return new CurrentRowFrameBound();
	}
	
	static @NonNull SqlFrameBound following(int offset) {
		return new FollowingFrameBound(offset);
	}
	
	static @NonNull SqlFrameBound unboundedFollowing() {
		return new UnboundedFollowingFrameBound();
	}
}
