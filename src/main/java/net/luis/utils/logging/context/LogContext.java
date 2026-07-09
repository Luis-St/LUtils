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

package net.luis.utils.logging.context;

import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Luis-St
 *
 */

public interface LogContext extends Iterable<String> {
	
	@NonNull LogContext with(@NonNull String key, @Nullable Object value);
	
	@NonNull
	@Unmodifiable
	List<String> getKeys();
	
	@Override
	default @NonNull @Unmodifiable Iterator<String> iterator() {
		return this.getKeys().iterator();
	}
}
