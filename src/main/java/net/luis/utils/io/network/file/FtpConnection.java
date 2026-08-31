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

import net.luis.utils.io.network.connection.exception.NetworkConnectionException;
import net.luis.utils.io.network.connection.tcp.TcpConnection;
import net.luis.utils.io.network.file.exception.FtpException;
import org.jspecify.annotations.NonNull;

import java.io.*;
import java.util.Objects;

/**
 *
 * @author Luis-St
 *
 */

public class FtpConnection {
	
	private final TcpConnection connection;
	private final BufferedInputStream inputStream;
	private final BufferedOutputStream outputStream;
	
	public FtpConnection(@NonNull TcpConnection connection) throws FtpException {
		this.connection = Objects.requireNonNull(connection, "Internal tcp connection must not be null");
		
		try {
			this.inputStream = new BufferedInputStream(connection.getInputStream());
			this.outputStream = new BufferedOutputStream(connection.getOutputStream());
		} catch (NetworkConnectionException e) {
			throw new FtpException("Failed to create input/output streams for the connection", e);
		}
	}
}
