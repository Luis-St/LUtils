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

import net.luis.utils.io.network.Endpoint;
import net.luis.utils.io.network.IpEndpoint;
import net.luis.utils.io.network.connection.NetworkClient;
import net.luis.utils.io.network.connection.exception.NetworkConnectionException;
import net.luis.utils.io.network.connection.ssl.SslClient;
import net.luis.utils.io.network.connection.tcp.TcpClient;
import net.luis.utils.io.network.connection.tcp.TcpClientConfig;
import net.luis.utils.io.network.file.exception.*;
import org.jspecify.annotations.NonNull;

import java.time.Instant;
import java.util.Arrays;
import java.util.Objects;

/**
 *
 * @author Luis-St
 *
 */

public class FtpClient implements AutoCloseable {
	
	// Will not be implemented:
	// LANG, FEAT, OPTS, HELP (not needed), SITE (dangerous), NLST (replaced by MLSD/MLST), ADAT (only support plain)
	// CCC (use plain from start or create a new connection), MIC (redundant), CONF (redundant), ENC (redundant),
	// PORT (deprecated, does not support IPv6), PASV (deprecated, does not support IPv6), SMNT (not needed)
	// ALLO (not needed)
	
	private static final String CRLF = "\r\n";
	
	private NetworkClient<byte[]> client;
	
	public FtpClient() {
		this.client = new TcpClient(TcpClientConfig.builder().framing(false).build());
	}
	
	private @NonNull FtpStatusCode parseStatusCode(@NonNull String response) throws FtpException {
		return null;
	}
	
	private @NonNull FtpStatusCode sendCommand(@NonNull String command) throws FtpException {
		Objects.requireNonNull(command, "Command must not be null");
		if (command.isBlank()) {
			throw new IllegalArgumentException("Command must not be blank");
		}
		if (!this.client.isActive()) {
			throw new FtpException("Not connected to a ftp server");
		}
		
		String response = "";
		FtpStatusCode status = this.parseStatusCode(response);
		if (status.is5xx()) {
			throw new FtpStatusException(status);
		}
		return status;
	}
	
	public void connect(@NonNull IpEndpoint endpoint) throws FtpException {
		Objects.requireNonNull(endpoint, "Endpoint must not be null");
		if (this.client.isActive()) {
			throw new FtpException("Already connected to a ftp server");
		}
		
		try {
			switch (this.client) {
				case TcpClient tcp -> tcp.connect(endpoint);
				case SslClient ssl -> ssl.connect(endpoint);
				default -> throw new FtpException("Unsupported client type");
			}
		} catch (NetworkConnectionException e) {
			throw new FtpException("Failed to connect to " + endpoint, e);
		}
	}
	
	public @NonNull FtpStatusCode authenticate(@NonNull FtpAuthenticationMethod method) throws FtpException {
		Objects.requireNonNull(method, "Authentication method must not be null");
		return this.sendCommand("AUTH " + method.name());
	}
	
	public @NonNull FtpStatusCode host(@NonNull String hostname) throws FtpException {
		Objects.requireNonNull(hostname, "Hostname must not be null");
		if (hostname.isBlank()) {
			throw new IllegalArgumentException("Hostname must not be blank");
		}
		
		return this.sendCommand("HOST " + hostname);
	}
	
	public @NonNull FtpStatusCode user(@NonNull String username) throws FtpException {
		Objects.requireNonNull(username, "Username must not be null");
		if (username.isBlank()) {
			throw new IllegalArgumentException("Username must not be blank");
		}
		
		return this.sendCommand("USER " + username);
	}
	
	public @NonNull FtpStatusCode password(char @NonNull [] password) throws FtpException {
		Objects.requireNonNull(password, "Password must not be null");
		if (password.length == 0) {
			throw new IllegalArgumentException("Password must not be empty");
		}
		
		FtpStatusCode status = this.sendCommand("PASS " + new String(password));
		Arrays.fill(password, '\0');
		return status;
	}
	
	public @NonNull FtpStatusCode account(@NonNull String account) throws FtpException {
		Objects.requireNonNull(account, "Account must not be null");
		if (account.isBlank()) {
			throw new IllegalArgumentException("Account must not be blank");
		}
		
		return this.sendCommand("ACCT " + account);
	}
	
	public @NonNull FtpStatusCode login(@NonNull String username, char @NonNull [] password) throws FtpException {
		FtpStatusCode status = this.user(username);
		
		if (status == FtpStatusCode.USER_NAME_OKAY) {
			return this.password(password);
		}
		return status;
	}
	
	public @NonNull FtpStatusCode login(@NonNull String username, char @NonNull [] password, @NonNull String account) throws FtpException {
		FtpStatusCode status = this.login(username, password);
		
		if (status == FtpStatusCode.NEED_ACCOUNT_FOR_LOGIN) {
			return this.account(account);
		}
		return status;
	}
	
	public void dataChannelProtection(@NonNull FtpDataChannelProtection protection) throws FtpException {
		Objects.requireNonNull(protection, "Data channel protection must not be null");
		
		this.sendCommand("PBSZ 0");
		this.sendCommand("PROT " + protection.getName());
	}
	
	public void transferTyp(@NonNull FtpTransferType type) throws FtpException {
		// TYPE
	}
	
	public void transferstructure(@NonNull FtpTransferStructure structure) throws FtpException {
		// STRU
	}
	
	public void transferMode(@NonNull FtpTransferMode mode) throws FtpException {
		// MODE
	}
	
	public void passive() throws FtpException {
		// Client get a port back where the data connection should be opened
		// EPSV
	}
	
	public void port(@NonNull Endpoint endpoint) throws FtpException {
		// Client tells the server where to connect to for the data connection
		// EPRT
	}
	
	public void reinitialize() throws FtpException {
		// REIN
	}
	
	public void system() throws FtpException {
		// SYST
	}
	
	public void stats() throws FtpException {
		// STAT
	}
	
	public void changeWorkingDirectory(@NonNull String path) throws FtpException {
		// CWD
	}
	
	public void moveToParentDirectory() throws FtpException {
		// CDUP
	}
	
	public void uploadFile(@NonNull String path, byte @NonNull [] data) throws FtpException {
		// STOR
	}
	
	public void uploadFileSafe(@NonNull String path, byte @NonNull [] data) throws FtpException {
		// STOU
	}
	
	public byte @NonNull [] downloadFile(@NonNull String path) throws FtpException {
		return null; // RETR
	}
	
	public void appendFile(@NonNull String path, byte @NonNull [] data) throws FtpException {
		// APPE
	}
	
	public void reset(long offset) throws FtpException {
		// REST
	}
	
	public void renameFile(@NonNull String from, @NonNull String to) throws FtpException {
		// RNFR
		// RNTO
	}
	
	public void abort() throws FtpException {
		// ABOR
	}
	
	public void deleteFile(@NonNull String path) throws FtpException {
		// DELE
	}
	
	public void makeDirectory(@NonNull String path) throws FtpException {
		// MKD
	}
	
	public void removeDirectory(@NonNull String path) throws FtpException {
		// RMD
	}
	
	public void printWorkingDirectory() throws FtpException {
		// PWD
	}
	
	public void listFriendly(@NonNull String path) throws FtpException {
		// LIST
	}
	
	public void listDirectory(@NonNull String path) throws FtpException {
		// MLSD
	}
	
	public void list(@NonNull String path) throws FtpException {
		// MLST
	}
	
	public @NonNull Object stats(@NonNull String path) throws FtpException {
		// STAT
		return null;
	}
	
	public @NonNull Instant getModificationTime(@NonNull String path) throws FtpException {
		return null; // MDTM
	}
	
	public long getSize(@NonNull String path) throws FtpException {
		return 0; // SIZE
	}
	
	public void ping() throws FtpException {
		// NOOP
	}
	
	public void quit() throws FtpException {
		// QUIT
	}
	
	@Override
	public void close() throws FtpException {
	
	}
}
