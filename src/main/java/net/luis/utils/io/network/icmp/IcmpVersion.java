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

import net.luis.utils.io.network.address.IpAddress;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

/**
 * The ICMP versions an {@link IcmpSocket} can speak.<br>
 * The version decides the address family of the socket and the message types of an echo exchange.<br>
 *
 * @see IcmpSocket
 *
 * @author Luis-St
 */
public enum IcmpVersion {
	
	/**
	 * ICMP for IPv4 as defined by RFC 792.<br>
	 */
	ICMP_V4(4, 8, 0),
	/**
	 * ICMPv6 for IPv6 as defined by RFC 4443.<br>
	 */
	ICMP_V6(6, 128, 129);
	
	/**
	 * The version of the IP protocol this ICMP version belongs to.<br>
	 */
	private final int ipVersion;
	/**
	 * The message type of an echo request.<br>
	 */
	private final int echoRequestType;
	/**
	 * The message type of an echo reply.<br>
	 */
	private final int echoReplyType;
	
	/**
	 * Constructs a new ICMP version constant.<br>
	 *
	 * @param ipVersion The version of the IP protocol
	 * @param echoRequestType The message type of an echo request
	 * @param echoReplyType The message type of an echo reply
	 */
	IcmpVersion(int ipVersion, int echoRequestType, int echoReplyType) {
		this.ipVersion = ipVersion;
		this.echoRequestType = echoRequestType;
		this.echoReplyType = echoReplyType;
	}
	
	/**
	 * Returns the ICMP version matching the IP version of the given address.<br>
	 *
	 * @param address The address
	 * @return The ICMP version
	 * @throws NullPointerException If the address is null
	 */
	public static @NonNull IcmpVersion of(@NonNull IpAddress<?> address) {
		Objects.requireNonNull(address, "Address must not be null");
		return address.version() == 4 ? ICMP_V4 : ICMP_V6;
	}
	
	/**
	 * Returns the version of the IP protocol this ICMP version belongs to.<br>
	 * @return Either 4 or 6
	 */
	public int ipVersion() {
		return this.ipVersion;
	}
	
	/**
	 * Returns the message type of an echo request.<br>
	 * @return The echo request type
	 */
	public int echoRequestType() {
		return this.echoRequestType;
	}
	
	/**
	 * Returns the message type of an echo reply.<br>
	 * @return The echo reply type
	 */
	public int echoReplyType() {
		return this.echoReplyType;
	}
	
	/**
	 * Checks whether the given address belongs to this version.<br>
	 *
	 * @param address The address to check
	 * @return True if the IP version of the address matches this version
	 * @throws NullPointerException If the address is null
	 */
	public boolean supports(@NonNull IpAddress<?> address) {
		Objects.requireNonNull(address, "Address must not be null");
		return address.version() == this.ipVersion;
	}
}
