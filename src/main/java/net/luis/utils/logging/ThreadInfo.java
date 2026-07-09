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

package net.luis.utils.logging;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 *
 * @author Luis-St
 *
 */

public record ThreadInfo(
	@NonNull String name,
	long id,
	@Nullable ThreadGroup group,
	int priority,
	boolean isDaemon,
	@NonNull String state
) {
	
	public ThreadInfo {}
}
