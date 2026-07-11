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

import com.google.common.collect.Maps;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;

/**
 *
 * @author Luis-St
 *
 */

public class ImmutableLogContext implements LogContext {
	
	private final Map<String, Object> context;
	
	public ImmutableLogContext() {
		this.context = Map.of();
	}
	
	public ImmutableLogContext(@NonNull Map<String, Object> context) {
		this.context = Map.copyOf(context);
	}
	
	@Override
	public @NonNull LogContext with(@NonNull String key, @Nullable Object value) {
		Map<String, Object> newContext = Maps.newHashMap(this.context);
		newContext.put(key, value);
		return new ImmutableLogContext(newContext);
	}
	
	@Override
	public @NonNull @Unmodifiable List<String> getKeys() {
		return List.copyOf(this.context.keySet());
	}
}
