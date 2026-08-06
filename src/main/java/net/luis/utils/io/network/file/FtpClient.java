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
import net.luis.utils.io.network.file.exception.FtpException;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.time.Instant;

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
	
	
	
	
	
	public void connect(@NonNull IpEndpoint endpoint) throws FtpException {
	
	}
	
	public void authenticate(@NonNull FtpAuthenticationMethod method) throws FtpException {
		// AUTH
	}
	
	public void host(@NonNull String hostname) throws FtpException {
		// HOST
	}
	
	public void password(char @NonNull [] password) throws FtpException {
		// PASS
	}
	
	public void account(@NonNull String account) throws FtpException {
		// ACCT
	}
	
	public void login(@NonNull String username, char @NonNull [] password) throws FtpException {
		// USER
		// PASS
	}
	
	public void login(@NonNull String username, char @NonNull [] password, @NonNull String account) throws FtpException {
		// USER
		// PASS
		// ACCT
	}
	
	public void dataChannelProtection(@NonNull FtpDataChannelProtection protection) throws FtpException {
		// PBSZ 0
		// PROT
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
	
	public @NotNull Object stats(@NonNull String path) throws FtpException {
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
