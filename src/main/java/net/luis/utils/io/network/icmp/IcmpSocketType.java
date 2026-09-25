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

package net.luis.utils.io.network.icmp;

/**
 * The kinds of operating system sockets an {@link IcmpSocket} can be backed by.<br>
 *
 * @see IcmpSocketConfig
 *
 * @author Luis-St
 */
public enum IcmpSocketType {
	
	/**
	 * An unprivileged ICMP datagram socket.<br>
	 * <p>
	 *     It needs no special rights on macOS.<br>
	 *     On Linux the group of the process must be inside the range of {@code net.ipv4.ping_group_range}.<br>
	 *     Linux also replaces the identifier of outgoing echo requests with its own and only delivers the matching replies,<br>
	 *     so {@link IcmpSocket#identifier()} reports the identifier the kernel chose.
	 * </p>
	 */
	DATAGRAM,
	/**
	 * A raw ICMP socket, which receives every ICMP message arriving at the host.<br>
	 * It needs root rights or the {@code CAP_NET_RAW} capability.<br>
	 */
	RAW
}
