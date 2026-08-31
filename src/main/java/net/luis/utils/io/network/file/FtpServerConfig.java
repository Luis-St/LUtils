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

package net.luis.utils.io.network.file;

import net.luis.utils.io.network.connection.executor.ClientExecutorStrategy;
import org.jspecify.annotations.NonNull;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 *
 * @author Luis-St
 *
 */

public record FtpServerConfig(
	int maxConnections,
	@NonNull ClientExecutorStrategy executorStrategy,
	@NonNull Charset charset
) {
	
	public static final FtpServerConfig DEFAULT = new FtpServerConfig(50, ClientExecutorStrategy.virtualThreads(), StandardCharsets.UTF_8);
	
	public FtpServerConfig {
		Objects.requireNonNull(executorStrategy, "Executor strategy must not be null");
		
		if (maxConnections < 1) {
			throw new IllegalArgumentException("Max connections must be at least 1: " + maxConnections);
		}
	}
}
