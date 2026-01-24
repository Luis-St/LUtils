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

package net.luis.utils.io.codec.constraint.config.matcher;

import net.luis.utils.io.codec.constraint.config.*;
import net.luis.utils.io.codec.constraint.config.io.*;
import net.luis.utils.io.codec.constraint.util.*;
import net.luis.utils.io.network.address.*;
import net.luis.utils.io.network.address.exception.IpParseException;
import net.luis.utils.io.network.address.ipv4.Ipv4Address;
import net.luis.utils.io.network.address.ipv4.Ipv4Network;
import net.luis.utils.io.network.address.ipv6.Ipv6Address;
import net.luis.utils.io.network.address.ipv6.Ipv6Network;
import net.luis.utils.util.Pair;
import net.luis.utils.util.result.Result;
import org.apache.commons.lang3.ArrayUtils;
import org.jspecify.annotations.NonNull;

import java.io.File;
import java.io.IOException;
import java.net.*;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Utility class providing static methods for network-specific constraint validation patterns.<br>
 * <p>
 *     This class contains domain-specific matchers for network-related types that don't fit
 *     in the generic {@link ConstraintMatchers} class due to their specialized semantics
 *     for ports, hosts, paths, queries, and URIs.
 * </p>
 *
 * @author Luis-St
 */
@SuppressWarnings("OptionalContainsCollection")
public final class IOMatchers {
	
	/**
	 * Private constructor to prevent instantiation.<br>
	 */
	private IOMatchers() {}
	
	/**
	 * Validates a port value against a port range constraint.<br>
	 *
	 * @param value The port value to validate
	 * @param inRange The port range constraint as a pair of ((min, max), negated)
	 * @return A successful result if the constraint is satisfied or not present
	 * @throws NullPointerException If any parameter is null
	 */
	public static @NonNull Result<Void> matchPortRange(int value, @NonNull Optional<Pair<Pair<Integer, Integer>, Boolean>> inRange) {
		Objects.requireNonNull(inRange, "Port in range constraint must not be null");
		if (inRange.isEmpty()) {
			return Result.success();
		}
		
		int min = inRange.get().getFirst().getFirst();
		int max = inRange.get().getFirst().getSecond();
		boolean negated = inRange.get().getSecond();
		boolean inRangeCheck = value >= min && value <= max;
		
		if (!negated && !inRangeCheck) {
			return Result.error("Port " + value + " must be in range [" + min + ", " + max + "]");
		} else if (negated && inRangeCheck) {
			return Result.error("Port " + value + " must not be in range [" + min + ", " + max + "]");
		}
		return Result.success();
	}
	
	/**
	 * Validates a port value against a port type constraint.<br>
	 *
	 * @param port The port value to validate
	 * @param type The port type enum constraint config
	 * @return A successful result if the constraint is satisfied or not present
	 * @throws NullPointerException If any parameter is null
	 */
	public static @NonNull Result<Void> matchPortType(int port, @NonNull Optional<EnumConstraintConfig<PortRange>> type) {
		Objects.requireNonNull(type, "Port type constraint must not be null");
		if (type.isEmpty()) {
			return Result.success();
		}
		
		PortRange portRange = PortRange.fromPort(port);
		Result<Void> result = type.get().matches(portRange);
		if (result.isError()) {
			return Result.error("Port type constraint failed: " + result.errorOrThrow());
		}
		return Result.success();
	}
	
	/**
	 * Validates a host value against an IP version constraint.<br>
	 *
	 * @param value The host value to validate
	 * @param ipVersion The IP version enum constraint config
	 * @return A successful result if the constraint is satisfied or not present
	 * @throws NullPointerException If any parameter is null
	 */
	public static @NonNull Result<Void> matchIpVersion(@NonNull String value, @NonNull Optional<EnumConstraintConfig<IpVersion>> ipVersion) {
		Objects.requireNonNull(value, "Ip address must not be null");
		Objects.requireNonNull(ipVersion, "Ip version constraint must not be null");
		if (ipVersion.isEmpty()) {
			return Result.success();
		}
		
		IpVersion detectedVersion;
		if (isValidIpv4(value)) {
			detectedVersion = IpVersion.IPV4;
		} else if (isValidIpv6(value)) {
			detectedVersion = IpVersion.IPV6;
		} else {
			return Result.error("Host '" + value + "' is not a valid IP address");
		}
		
		Result<Void> result = ipVersion.get().matches(detectedVersion);
		if (result.isError()) {
			return Result.error("IP version constraint failed: " + result.errorOrThrow());
		}
		return Result.success();
	}
	
	/**
	 * Validates a host value against an IP address type constraint.<br>
	 *
	 * @param value The host value to validate
	 * @param ipType The IP address type enum constraint config
	 * @return A successful result if the constraint is satisfied or not present
	 * @throws NullPointerException If any parameter is null
	 */
	public static @NonNull Result<Void> matchIpType(@NonNull String value, @NonNull Optional<EnumConstraintConfig<IpAddressType>> ipType) {
		Objects.requireNonNull(value, "Ip address must not be null");
		Objects.requireNonNull(ipType, "Ip type constraint must not be null");
		if (ipType.isEmpty()) {
			return Result.success();
		}
		
		try {
			IpAddress<?> address = IpAddresses.parse(value);
			return ipType.get().matches(getIpAddressType(address));
		} catch (IpParseException e) {
			return Result.error("Host '" + value + "' is not a valid ip address: " + e.getMessage());
		}
	}
	
	/**
	 * Gets the of IP address type for a given IP address.<br>
	 * <p>
	 *     This method analyzes the properties of the provided IP address and returns its type.<br>
	 *     The classifications include PRIVATE, LOOPBACK, LINK_LOCAL, MULTICAST, BROADCAST, UNSPECIFIED, and PUBLIC:
	 * </p>
	 * <ul>
	 *     <li>
	 *         <b>PRIVATE</b>:<br>
	 *         Indicates that the IP address is reserved for use within private networks.<br>
	 *         Checked using {@link IpAddress#isPrivate()}.
	 *     </li>
	 *     <li>
	 *         <b>LOOPBACK</b>:<br>
	 *         Indicates that the IP address is used for communication within the same host.<br>
	 *         Checked using {@link IpAddress#isLoopback()}.
	 *     </li>
	 *     <li>
	 *         <b>LINK_LOCAL</b>:<br>
	 *         Indicates that the IP address is used for communication within a local network segment without a router.<br>
	 *         Checked using {@link IpAddress#isLinkLocal()}.
	 *     </li>
	 *     <li>
	 *         <b>MULTICAST</b>:<br>
	 *         Indicates that the IP address is used for one-to-many communication.<br>
	 *         Checked using {@link IpAddress#isMulticast()}.
	 *     </li>
	 *     <li>
	 *         <b>BROADCAST</b>:<br>
	 *         Indicates that the IP address is used for one-to-all communication within a network.<br>
	 *         Checked specifically for IPv4 addresses using {@link Ipv4Address#isBroadcast()}.
	 *     </li>
	 *     <li>
	 *         <b>UNSPECIFIED</b>:<br>
	 *         Indicates that the IP address is unspecified (all zeros).<br>
	 *         Checked using {@link IpAddress#isUnspecified()}.
	 *     </li>
	 *     <li>
	 *         <b>PUBLIC</b>:<br>
	 *         Indicates that the IP address is a globally routable address.<br>
	 *         This type is assigned if none of the other types apply and the address is not unspecified.
	 *     </li>
	 * </ul>
	 *
	 * @param address The IP address to analyze
	 * @return An ip address type representing the classification of the IP address
	 * @throws NullPointerException If the address is null
	 */
	private static @NonNull IpAddressType getIpAddressType(@NonNull IpAddress<?> address) {
		Objects.requireNonNull(address, "Ip address must not be null");
		
		if (address.isPrivate()) {
			return IpAddressType.PRIVATE;
		} else if (address.isLoopback()) {
			return IpAddressType.LOOPBACK;
		} else if (address.isLinkLocal()) {
			return IpAddressType.LINK_LOCAL;
		} else if (address.isMulticast()) {
			return IpAddressType.MULTICAST;
		} else if (address instanceof Ipv4Address ipv4 && ipv4.isBroadcast()) {
			return IpAddressType.BROADCAST;
		} else if (address.isUnspecified()) {
			return IpAddressType.UNSPECIFIED;
		} else if (address.isDocumentation()) {
			return IpAddressType.DOCUMENTATION;
		} else {
			return IpAddressType.PUBLIC;
		}
	}
	
	/**
	 * Validates a host value against a subnet membership constraint.<br>
	 *
	 * @param value The host value to validate
	 * @param inAnySubnet The subnet constraint as a pair of (CIDRs, negated)
	 * @return A successful result if the constraint is satisfied or not present
	 * @throws NullPointerException If any parameter is null
	 */
	public static @NonNull Result<Void> matchInAnySubnet(@NonNull String value, @NonNull Optional<Pair<Set<String>, Boolean>> inAnySubnet) {
		Objects.requireNonNull(value, "Ip address must not be null");
		Objects.requireNonNull(inAnySubnet, "In any subnet constraint must not be null");
		if (inAnySubnet.isEmpty()) {
			return Result.success();
		}
		
		Set<IpNetwork<?, ?>> subnets = new HashSet<>();
		for (String cidr : inAnySubnet.get().getFirst()) {
			try {
				subnets.add(IpAddresses.parseNetwork(cidr));
			} catch (IpParseException e) {
				return Result.error("Invalid CIDR '" + cidr + "' in subnet constraint: " + e.getMessage());
			}
		}
		
		try {
			IpAddress<?> address = IpAddresses.parse(value);
			
			return switch (address) {
				case Ipv4Address ipv4 -> checkIpv4SubnetMembership(ipv4, subnets, inAnySubnet.get().getSecond());
				case Ipv6Address ipv6 -> checkIpv6SubnetMembership(ipv6, subnets, inAnySubnet.get().getSecond());
			};
		} catch (IpParseException e) {
			return Result.error("Host '" + value + "' is not a valid ip address: " + e.getMessage());
		}
	}
	
	/**
	 * Checks IPv4 subnet membership.<br>
	 * <p>
	 *     This method checks if the given IPv4 address is a member of any or none of the specified subnets, based on the negation flag.<br>
	 *     All subnets that are not IPv4 networks are ignored in the check:
	 * </p>
	 * <p>
	 *     If the negation flag is {@code false}, the method ensures that the address is a member of at least one subnet.<br>
	 *     If the negation flag is {@code true}, the method ensures that the address is not a member of any subnet.
	 * </p>
	 * @param ipv4 The IPv4 address to check
	 * @param subnets The set of subnets to check against
	 * @param negated The negation flag indicating the membership condition
	 * @return A successful result if the membership condition is satisfied, otherwise an error result with a descriptive message
	 * @throws NullPointerException If any parameter is null
	 */
	private static @NonNull Result<Void> checkIpv4SubnetMembership(@NonNull Ipv4Address ipv4, @NonNull Set<IpNetwork<?, ?>> subnets, boolean negated) {
		Objects.requireNonNull(ipv4, "Ipv4 address must not be null");
		Objects.requireNonNull(subnets, "Subnet set must not be null");
		
		boolean memberOfAny = false;
		boolean memberOfNone = true;
		List<Ipv4Network> validSubnets = new ArrayList<>();
		for (IpNetwork<?, ?> subnet : subnets) {
			if (!(subnet instanceof Ipv4Network ipv4Network)) {
				continue;
			}
			validSubnets.add(ipv4Network);
			
			if (ipv4Network.contains(ipv4)) {
				memberOfAny = true;
				memberOfNone = false;
			}
		}
		
		if (!negated && !memberOfAny) {
			return Result.error("IPv4 address '" + ipv4 + "' must be member of at least one specified subnet: " + validSubnets);
		} else if (negated && !memberOfNone) {
			return Result.error("IPv4 address '" + ipv4 + "' must not be member of any specified subnet: " + validSubnets);
		}
		return Result.success();
	}
	
	/**
	 * Checks IPv6 subnet membership.<br>
	 * <p>
	 *     This method checks if the given IPv6 address is a member of any or none of the specified subnets, based on the negation flag.<br>
	 *     All subnets that are not IPv6 networks are ignored in the check:
	 * </p>
	 * <p>
	 *     If the negation flag is {@code false}, the method ensures that the address is a member of at least one subnet.<br>
	 *     If the negation flag is {@code true}, the method ensures that the address is not a member of any subnet.
	 * </p>
	 *
	 * @param ipv6 The IPv6 address to check
	 * @param subnets The set of subnets to check against
	 * @param negated The negation flag indicating the membership condition
	 * @return A successful result if the membership condition is satisfied, otherwise an error result with a descriptive message
	 * @throws NullPointerException If any parameter is null
	 */
	private static @NonNull Result<Void> checkIpv6SubnetMembership(@NonNull Ipv6Address ipv6, @NonNull Set<IpNetwork<?, ?>> subnets, boolean negated) {
		Objects.requireNonNull(ipv6, "Ipv6 address must not be null");
		Objects.requireNonNull(subnets, "Subnet set must not be null");
		
		boolean memberOfAny = false;
		boolean memberOfNone = true;
		List<Ipv6Network> validSubnets = new ArrayList<>();
		for (IpNetwork<?, ?> subnet : subnets) {
			if (!(subnet instanceof Ipv6Network ipv6Network)) {
				continue;
			}
			validSubnets.add(ipv6Network);
			
			if (ipv6Network.contains(ipv6)) {
				memberOfAny = true;
				memberOfNone = false;
			}
		}
		
		if (!negated && !memberOfAny) {
			return Result.error("IPv6 address '" + ipv6 + "' must be member of at least one specified subnet: " + validSubnets);
		} else if (negated && !memberOfNone) {
			return Result.error("IPv6 address '" + ipv6 + "' must not be member of any specified subnet: " + validSubnets);
		}
		return Result.success();
	}
	
	/**
	 * Validates that a host value is a root domain (has exactly one dot).<br>
	 *
	 * @param value The host value to validate
	 * @param rootDomain The root domain constraint flag
	 * @return A successful result if the constraint is satisfied or not present
	 * @throws NullPointerException If any parameter is null
	 */
	public static @NonNull Result<Void> matchRootDomain(@NonNull String value, @NonNull Optional<Unit> rootDomain) {
		Objects.requireNonNull(value, "Domain must not be null");
		Objects.requireNonNull(rootDomain, "Root domain constraint must not be null");
		if (rootDomain.isEmpty()) {
			return Result.success();
		}
		
		long dotCount = value.chars().filter(c -> c == '.').count();
		if (dotCount != 1) {
			return Result.error("Host '" + value + "' must be a root domain");
		}
		return Result.success();
	}
	
	/**
	 * Validates that a host value is a subdomain (has more than one dot).<br>
	 *
	 * @param value The host value to validate
	 * @param subDomain The subdomain constraint flag
	 * @return A successful result if the constraint is satisfied or not present
	 * @throws NullPointerException If any parameter is null
	 */
	public static @NonNull Result<Void> matchSubDomain(@NonNull String value, @NonNull Optional<Unit> subDomain) {
		Objects.requireNonNull(value, "Domain must not be null");
		Objects.requireNonNull(subDomain, "Sub domain constraint must not be null");
		if (subDomain.isEmpty()) {
			return Result.success();
		}
		
		long dotCount = value.chars().filter(c -> c == '.').count();
		if (dotCount < 2) {
			return Result.error("Host '" + value + "' must be a subdomain");
		}
		return Result.success();
	}
	
	/**
	 * Checks if a string is a valid IPv4 address.<br>
	 *
	 * @param value The string to check
	 * @return true if the string is a valid IPv4 address
	 * @throws NullPointerException If any parameter is null
	 */
	private static boolean isValidIpv4(@NonNull String value) {
		Objects.requireNonNull(value, "Ip address must not be null");
		
		String[] parts = value.split("\\.");
		if (parts.length != 4) {
			return false;
		}
		
		for (String part : parts) {
			try {
				int num = Integer.parseInt(part);
				if (num < 0 || num > 255) {
					return false;
				}
			} catch (NumberFormatException e) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Checks if a string is a valid IPv6 address.<br>
	 *
	 * @param value The string to check
	 * @return true if the string is a valid IPv6 address
	 * @throws NullPointerException If any parameter is null
	 */
	private static boolean isValidIpv6(@NonNull String value) {
		Objects.requireNonNull(value, "Ip address must not be null");
		
		if (value.contains("::")) {
			String[] parts = value.split("::");
			if (parts.length > 2) {
				return false;
			}
		}
		
		String[] groups = value.split(":");
		if (groups.length > 8) {
			return false;
		}
		
		for (String group : groups) {
			if (group.isEmpty()) {
				continue;
			}
			
			if (group.length() > 4) {
				return false;
			}
			
			try {
				Integer.parseInt(group, 16);
			} catch (NumberFormatException e) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Validates that a path is canonical.<br>
	 *
	 * @param value The path to validate
	 * @param canonical The canonical constraint flag
	 * @return A successful result if the constraint is satisfied or not present
	 * @throws NullPointerException If any parameter is null
	 */
	public static @NonNull Result<Void> matchPathCanonical(@NonNull Path value, @NonNull Optional<Unit> canonical) {
		Objects.requireNonNull(value, "Path must not be null");
		Objects.requireNonNull(canonical, "Path canonical constraint must not be null");
		if (canonical.isEmpty()) {
			return Result.success();
		}
		
		try {
			File file = value.toFile();
			String canonicalPath = file.getCanonicalPath();
			if (!value.toString().equals(canonicalPath)) {
				return Result.error("Path '" + value + "' must be canonical");
			}
		} catch (IOException e) {
			return Result.error("Failed to get canonical path for '" + value + "': " + e.getMessage());
		}
		return Result.success();
	}
	
	/**
	 * Validates a path string against a string constraint config.<br>
	 *
	 * @param value The path to validate
	 * @param pathConfig The string constraint config for path validation
	 * @return A successful result if the constraint is satisfied or not present
	 * @throws NullPointerException If any parameter is null
	 */
	public static @NonNull Result<Void> matchPathStringConfig(@NonNull Path value, @NonNull Optional<StringConstraintConfig> pathConfig) {
		Objects.requireNonNull(value, "Path must not be null");
		Objects.requireNonNull(pathConfig, "Path config constraint must not be null");
		if (pathConfig.isEmpty()) {
			return Result.success();
		}
		
		Result<Void> result = pathConfig.get().matches(value.toString());
		if (result.isError()) {
			return Result.error("Path constraint failed: " + result.errorOrThrow());
		}
		return Result.success();
	}
	
	/**
	 * Validates a path's root component against a string constraint config.<br>
	 *
	 * @param value The path to validate
	 * @param rootConfig The string constraint config for root component validation
	 * @return A successful result if the constraint is satisfied or not present
	 * @throws NullPointerException If any parameter is null
	 */
	public static @NonNull Result<Void> matchPathRootConfig(@NonNull Path value, @NonNull Optional<StringConstraintConfig> rootConfig) {
		Objects.requireNonNull(value, "Path must not be null");
		Objects.requireNonNull(rootConfig, "Path root config constraint must not be null");
		if (rootConfig.isEmpty()) {
			return Result.success();
		}
		
		Path root = value.getRoot();
		if (root == null) {
			return Result.error("Path '" + value + "' has no root component");
		}
		
		Result<Void> result = rootConfig.get().matches(root.toString());
		if (result.isError()) {
			return Result.error("Root constraint failed: " + result.errorOrThrow());
		}
		return Result.success();
	}
	
	/**
	 * Validates a path's parent directory against a string constraint config.<br>
	 *
	 * @param value The path to validate
	 * @param parentConfig The string constraint config for parent directory validation
	 * @return A successful result if the constraint is satisfied or not present
	 * @throws NullPointerException If any parameter is null
	 */
	public static @NonNull Result<Void> matchPathParentConfig(@NonNull Path value, @NonNull Optional<StringConstraintConfig> parentConfig) {
		Objects.requireNonNull(value, "Path must not be null");
		Objects.requireNonNull(parentConfig, "Path parent config constraint must not be null");
		if (parentConfig.isEmpty()) {
			return Result.success();
		}
		
		Path parent = value.getParent();
		if (parent == null) {
			return Result.error("Path '" + value + "' has no parent component");
		}
		
		Result<Void> result = parentConfig.get().matches(parent.toString());
		if (result.isError()) {
			return Result.error("Parent constraint failed: " + result.errorOrThrow());
		}
		return Result.success();
	}
	
	/**
	 * Validates each segment of a path against a string constraint config.<br>
	 *
	 * @param value The path to validate
	 * @param segmentConfig The string constraint config for segment validation
	 * @return A successful result if the constraint is satisfied or not present
	 * @throws NullPointerException If any parameter is null
	 */
	public static @NonNull Result<Void> matchPathSegmentConfig(@NonNull Path value, @NonNull Optional<StringConstraintConfig> segmentConfig) {
		Objects.requireNonNull(value, "Path must not be null");
		Objects.requireNonNull(segmentConfig, "Path segment config constraint must not be null");
		if (segmentConfig.isEmpty()) {
			return Result.success();
		}
		
		for (int i = 0; i < value.getNameCount(); i++) {
			Result<Void> result = segmentConfig.get().matches(value.getName(i).toString());
			if (result.isError()) {
				return Result.error("Segment '" + value.getName(i) + "' constraint failed: " + result.errorOrThrow());
			}
		}
		return Result.success();
	}
	
	/**
	 * Validates a path's file name against a string constraint config.<br>
	 *
	 * @param value The path to validate
	 * @param fileConfig The string constraint config for file name validation
	 * @return A successful result if the constraint is satisfied or not present
	 * @throws NullPointerException If any parameter is null
	 */
	public static @NonNull Result<Void> matchPathFileNameConfig(@NonNull Path value, @NonNull Optional<StringConstraintConfig> fileConfig) {
		Objects.requireNonNull(value, "Path must not be null");
		Objects.requireNonNull(fileConfig, "Path file name config constraint must not be null");
		if (fileConfig.isEmpty()) {
			return Result.success();
		}
		
		Path fileName = value.getFileName();
		if (fileName == null) {
			return Result.error("Path '" + value + "' has no file name");
		}
		
		Result<Void> result = fileConfig.get().matches(fileName.toString());
		if (result.isError()) {
			return Result.error("File constraint failed: " + result.errorOrThrow());
		}
		return Result.success();
	}
	
	/**
	 * Validates a path's file extension against a string constraint config.<br>
	 *
	 * @param value The path to validate
	 * @param extensionConfig The string constraint config for extension validation
	 * @return A successful result if the constraint is satisfied or not present
	 * @throws NullPointerException If any parameter is null
	 */
	public static @NonNull Result<Void> matchPathExtensionConfig(@NonNull Path value, @NonNull Optional<StringConstraintConfig> extensionConfig) {
		Objects.requireNonNull(value, "Path must not be null");
		Objects.requireNonNull(extensionConfig, "Path extension config constraint must not be null");
		if (extensionConfig.isEmpty()) {
			return Result.success();
		}
		
		Path fileName = value.getFileName();
		if (fileName == null) {
			return Result.error("Path '" + value + "' has no file name for extension check");
		}
		
		String fileNameStr = fileName.toString();
		int dotIndex = fileNameStr.lastIndexOf('.');
		if (dotIndex < 0) {
			return Result.error("Path '" + value + "' has no extension");
		}
		
		String extension = fileNameStr.substring(dotIndex + 1);
		Result<Void> result = extensionConfig.get().matches(extension);
		if (result.isError()) {
			return Result.error("Extension constraint failed: " + result.errorOrThrow());
		}
		return Result.success();
	}
	
	/**
	 * Validates that a path has no file extension.<br>
	 *
	 * @param value The path to validate
	 * @param withoutExtension The without extension constraint flag
	 * @return A successful result if the constraint is satisfied or not present
	 * @throws NullPointerException If any parameter is null
	 */
	public static @NonNull Result<Void> matchPathWithoutExtension(@NonNull Path value, @NonNull Optional<Unit> withoutExtension) {
		Objects.requireNonNull(value, "Path must not be null");
		Objects.requireNonNull(withoutExtension, "Path without extension constraint must not be null");
		if (withoutExtension.isEmpty()) {
			return Result.success();
		}
		
		Path fileName = value.getFileName();
		if (fileName != null && fileName.toString().contains(".")) {
			return Result.error("Path '" + value + "' must not have an extension");
		}
		return Result.success();
	}
	
	/**
	 * Validates that a path is an ancestor of all specified paths.<br>
	 *
	 * @param value The path to validate
	 * @param ancestorOf The set of paths that the value must be an ancestor of
	 * @return A successful result if the constraint is satisfied or not present
	 * @throws NullPointerException If any parameter is null
	 */
	public static @NonNull Result<Void> matchPathAncestorOf(@NonNull Path value, @NonNull Optional<Set<String>> ancestorOf) {
		Objects.requireNonNull(value, "Path must not be null");
		Objects.requireNonNull(ancestorOf, "path ancestor of constraint must not be null");
		if (ancestorOf.isEmpty()) {
			return Result.success();
		}
		
		for (String pathStr : ancestorOf.get()) {
			Path targetPath = Path.of(pathStr);
			if (!targetPath.startsWith(value)) {
				return Result.error("Path '" + value + "' must be ancestor of '" + pathStr + "'");
			}
		}
		return Result.success();
	}
	
	/**
	 * Validates that a path is a descendant of all specified paths.<br>
	 *
	 * @param value The path to validate
	 * @param descendantOf The set of paths that the value must be a descendant of
	 * @return A successful result if the constraint is satisfied or not present
	 * @throws NullPointerException If any parameter is null
	 */
	public static @NonNull Result<Void> matchPathDescendantOf(@NonNull Path value, @NonNull Optional<Set<String>> descendantOf) {
		Objects.requireNonNull(value, "Path must not be null");
		Objects.requireNonNull(descendantOf, "Path descendant of constraint must not be null");
		if (descendantOf.isEmpty()) {
			return Result.success();
		}
		
		for (String pathStr : descendantOf.get()) {
			Path targetPath = Path.of(pathStr);
			if (!value.startsWith(targetPath)) {
				return Result.error("Path '" + value + "' must be descendant of '" + pathStr + "'");
			}
		}
		return Result.success();
	}
	
	/**
	 * Validates query parameter values against key-specific string constraint configs.<br>
	 *
	 * @param value The query parameters to validate
	 * @param valueConstraints A map of key to string constraint config
	 * @return A successful result if the constraint is satisfied or not present
	 * @throws NullPointerException If any parameter is null
	 */
	public static @NonNull Result<Void> matchQueryValueConstraints(@NonNull Map<String, List<String>> value, @NonNull Optional<Map<String, StringConstraintConfig>> valueConstraints) {
		Objects.requireNonNull(value, "Query parameter map must not be null");
		Objects.requireNonNull(valueConstraints, "Query value constraints must not be null");
		if (valueConstraints.isEmpty()) {
			return Result.success();
		}
		
		for (Map.Entry<String, StringConstraintConfig> entry : valueConstraints.get().entrySet()) {
			String key = entry.getKey();
			StringConstraintConfig config = entry.getValue();
			if (value.containsKey(key)) {
				for (String v : value.get(key)) {
					Result<Void> result = config.matches(v);
					if (result.isError()) {
						return Result.error("Value constraint for key '" + key + "' failed: " + result.errorOrThrow());
					}
				}
			}
		}
		return Result.success();
	}
	
	/**
	 * Validates query parameter values against pattern-matched string constraint configs.<br>
	 *
	 * @param value The query parameters to validate
	 * @param patternValueConstraints A map of pattern to string constraint config
	 * @return A successful result if the constraint is satisfied or not present
	 * @throws NullPointerException If any parameter is null
	 */
	public static @NonNull Result<Void> matchQueryPatternValueConstraints(@NonNull Map<String, List<String>> value, @NonNull Optional<Map<Pattern, StringConstraintConfig>> patternValueConstraints) {
		Objects.requireNonNull(value, "Query parameter map must not be null");
		Objects.requireNonNull(patternValueConstraints, "Query pattern value constraints must not be null");
		if (patternValueConstraints.isEmpty()) {
			return Result.success();
		}
		
		for (Map.Entry<Pattern, StringConstraintConfig> entry : patternValueConstraints.get().entrySet()) {
			Pattern pattern = entry.getKey();
			StringConstraintConfig config = entry.getValue();
			for (Map.Entry<String, List<String>> kvEntry : value.entrySet()) {
				if (pattern.matcher(kvEntry.getKey()).matches()) {
					for (String v : kvEntry.getValue()) {
						Result<Void> result = config.matches(v);
						if (result.isError()) {
							return Result.error("Pattern value constraint for key '" + kvEntry.getKey() + "' failed: " + result.errorOrThrow());
						}
					}
				}
			}
		}
		return Result.success();
	}
	
	/**
	 * Validates that all query parameters have exactly one value.<br>
	 *
	 * @param value The query parameters to validate
	 * @param singleValued The single-valued constraint flag
	 * @return A successful result if the constraint is satisfied or not present
	 * @throws NullPointerException If any parameter is null
	 */
	public static @NonNull Result<Void> matchQuerySingleValued(@NonNull Map<String, List<String>> value, @NonNull Optional<Unit> singleValued) {
		Objects.requireNonNull(value, "Query parameter map must not be null");
		Objects.requireNonNull(singleValued, "Query single valued constraint must not be null");
		if (singleValued.isEmpty()) {
			return Result.success();
		}
		
		for (Map.Entry<String, List<String>> entry : value.entrySet()) {
			if (entry.getValue().size() != 1) {
				return Result.error("Query parameter '" + entry.getKey() + "' must have exactly one value");
			}
		}
		return Result.success();
	}
	
	/**
	 * Validates that all query parameter values are unique across all keys.<br>
	 *
	 * @param value The query parameters to validate
	 * @param uniqueValues The unique values constraint flag
	 * @return A successful result if the constraint is satisfied or not present
	 * @throws NullPointerException If any parameter is null
	 */
	public static @NonNull Result<Void> matchQueryUniqueValues(@NonNull Map<String, List<String>> value, @NonNull Optional<Unit> uniqueValues) {
		Objects.requireNonNull(value, "Query parameter map must not be null");
		Objects.requireNonNull(uniqueValues, "Query unique values constraint must not be null");
		if (uniqueValues.isEmpty()) {
			return Result.success();
		}
		
		Set<String> allValues = new HashSet<>();
		for (List<String> values : value.values()) {
			for (String val : values) {
				if (!allValues.add(val)) {
					return Result.error("Query values must be unique");
				}
			}
		}
		return Result.success();
	}
	
	/**
	 * Validates query parameter multi-value sizes against key-specific size constraint configs.<br>
	 *
	 * @param value The query parameters to validate
	 * @param multiValuedConstraints A map of key to size constraint config
	 * @return A successful result if the constraint is satisfied or not present
	 * @throws NullPointerException If any parameter is null
	 */
	public static @NonNull Result<Void> matchQueryMultiValuedConstraints(@NonNull Map<String, List<String>> value, @NonNull Optional<Map<String, SizeConstraintConfig>> multiValuedConstraints) {
		Objects.requireNonNull(value, "Query parameter map must not be null");
		Objects.requireNonNull(multiValuedConstraints, "Query multi valued constraints must not be null");
		if (multiValuedConstraints.isEmpty()) {
			return Result.success();
		}
		
		for (Map.Entry<String, SizeConstraintConfig> entry : multiValuedConstraints.get().entrySet()) {
			String key = entry.getKey();
			SizeConstraintConfig config = entry.getValue();
			if (value.containsKey(key)) {
				Result<Void> result = config.matches(value.get(key).size());
				if (result.isError()) {
					return Result.error("Multi-valued constraint for key '" + key + "' failed: " + result.errorOrThrow());
				}
			}
		}
		return Result.success();
	}
	
	/**
	 * Validates a URI's scheme against a string constraint config.<br>
	 *
	 * @param value The URI to validate
	 * @param schemeConfig The string constraint config for scheme validation
	 * @return A successful result if the constraint is satisfied or not present
	 * @throws NullPointerException If any parameter is null
	 */
	public static @NonNull Result<Void> matchUriSchemeConfig(@NonNull URI value, @NonNull Optional<StringConstraintConfig> schemeConfig) {
		Objects.requireNonNull(value, "Uri must not be null");
		Objects.requireNonNull(schemeConfig, "Uri scheme config constraint must not be null");
		if (schemeConfig.isEmpty()) {
			return Result.success();
		}
		
		String scheme = value.getScheme();
		if (scheme == null) {
			return Result.error("URI '" + value + "' has no scheme");
		}
		
		Result<Void> result = schemeConfig.get().matches(scheme);
		if (result.isError()) {
			return Result.error("Scheme constraint failed: " + result.errorOrThrow());
		}
		return Result.success();
	}
	
	/**
	 * Validates a URI's host against a host constraint config.<br>
	 *
	 * @param value The URI to validate
	 * @param hostConfig The host constraint config for host validation
	 * @return A successful result if the constraint is satisfied or not present
	 * @throws NullPointerException If any parameter is null
	 */
	public static @NonNull Result<Void> matchUriHostConfig(@NonNull URI value, @NonNull Optional<HostConstraintConfig> hostConfig) {
		Objects.requireNonNull(value, "Uri must not be null");
		Objects.requireNonNull(hostConfig, "Uri host config constraint must not be null");
		if (hostConfig.isEmpty()) {
			return Result.success();
		}
		
		String host = value.getHost();
		if (host == null) {
			return Result.error("URI '" + value + "' has no host");
		}
		
		Result<Void> result = hostConfig.get().matches(host);
		if (result.isError()) {
			return Result.error("Host constraint failed: " + result.errorOrThrow());
		}
		return Result.success();
	}
	
	/**
	 * Validates a URI's user info against a string constraint config.<br>
	 *
	 * @param value The URI to validate
	 * @param userInfoConfig The string constraint config for user info validation
	 * @return A successful result if the constraint is satisfied or not present
	 * @throws NullPointerException If any parameter is null
	 */
	public static @NonNull Result<Void> matchUriUserInfoConfig(@NonNull URI value, @NonNull Optional<StringConstraintConfig> userInfoConfig) {
		Objects.requireNonNull(value, "Uri must not be null");
		Objects.requireNonNull(userInfoConfig, "Uri user info config constraint must not be null");
		if (userInfoConfig.isEmpty()) {
			return Result.success();
		}
		
		String userInfo = value.getUserInfo();
		if (userInfo == null) {
			return Result.error("URI '" + value + "' has no user info");
		}
		
		Result<Void> result = userInfoConfig.get().matches(userInfo);
		if (result.isError()) {
			return Result.error("User info constraint failed: " + result.errorOrThrow());
		}
		return Result.success();
	}
	
	/**
	 * Validates a URI's port against a port constraint config.<br>
	 *
	 * @param value The URI to validate
	 * @param portConfig The port constraint config for port validation
	 * @return A successful result if the constraint is satisfied or not present
	 * @throws NullPointerException If any parameter is null
	 */
	public static @NonNull Result<Void> matchUriPortConfig(@NonNull URI value, @NonNull Optional<PortConstraintConfig> portConfig) {
		Objects.requireNonNull(value, "Uri must not be null");
		Objects.requireNonNull(portConfig, "Uri port config constraint must not be null");
		if (portConfig.isEmpty()) {
			return Result.success();
		}
		
		int port = value.getPort();
		if (port == -1) {
			return Result.error("URI '" + value + "' has no port");
		}
		
		Result<Void> result = portConfig.get().matches(port);
		if (result.isError()) {
			return Result.error("Port constraint failed: " + result.errorOrThrow());
		}
		return Result.success();
	}
	
	/**
	 * Validates a URI's path against a URI path constraint config.<br>
	 *
	 * @param value The URI to validate
	 * @param pathConfig The URI path constraint config for path validation
	 * @return A successful result if the constraint is satisfied or not present
	 * @throws NullPointerException If any parameter is null
	 */
	public static @NonNull Result<Void> matchUriPathConfig(@NonNull URI value, @NonNull Optional<URIPathConstraintConfig> pathConfig) {
		Objects.requireNonNull(value, "Uri must not be null");
		Objects.requireNonNull(pathConfig, "Uri path config constraint must not be null");
		if (pathConfig.isEmpty()) {
			return Result.success();
		}
		
		String pathStr = value.getPath();
		if (pathStr == null || pathStr.isEmpty()) {
			return Result.error("URI '" + value + "' has no path");
		}
		
		Result<Void> result = pathConfig.get().matches(pathStr);
		if (result.isError()) {
			return Result.error("Path constraint failed: " + result.errorOrThrow());
		}
		return Result.success();
	}
	
	/**
	 * Validates a URI's query against a query constraint config.<br>
	 *
	 * @param value The URI to validate
	 * @param queryConfig The query constraint config for query validation
	 * @return A successful result if the constraint is satisfied or not present
	 * @throws NullPointerException If any parameter is null
	 */
	public static @NonNull Result<Void> matchUriQueryConfig(@NonNull URI value, @NonNull Optional<QueryConstraintConfig> queryConfig) {
		Objects.requireNonNull(value, "Uri must not be null");
		Objects.requireNonNull(queryConfig, "Uri query config constraint must not be null");
		if (queryConfig.isEmpty()) {
			return Result.success();
		}
		
		String queryStr = value.getQuery();
		if (queryStr == null) {
			return Result.error("URI '" + value + "' has no query");
		}
		
		Map<String, List<String>> queryParams = parseQuery(queryStr);
		Result<Void> result = queryConfig.get().matches(queryParams);
		if (result.isError()) {
			return Result.error("Query constraint failed: " + result.errorOrThrow());
		}
		return Result.success();
	}
	
	/**
	 * Validates a URI's fragment against a string constraint config.<br>
	 *
	 * @param value The URI to validate
	 * @param fragmentConfig The string constraint config for fragment validation
	 * @return A successful result if the constraint is satisfied or not present
	 * @throws NullPointerException If any parameter is null
	 */
	public static @NonNull Result<Void> matchUriFragmentConfig(@NonNull URI value, @NonNull Optional<StringConstraintConfig> fragmentConfig) {
		Objects.requireNonNull(value, "Uri must not be null");
		Objects.requireNonNull(fragmentConfig, "Uri fragment config constraint must not be null");
		if (fragmentConfig.isEmpty()) {
			return Result.success();
		}
		
		String fragment = value.getFragment();
		if (fragment == null) {
			return Result.error("URI '" + value + "' has no fragment");
		}
		
		Result<Void> result = fragmentConfig.get().matches(fragment);
		if (result.isError()) {
			return Result.error("Fragment constraint failed: " + result.errorOrThrow());
		}
		return Result.success();
	}
	
	/**
	 * Parses a query string into a map of key to list of values.<br>
	 *
	 * @param query The query string to parse
	 * @return A map of query parameter keys to their values
	 * @throws NullPointerException If any parameter is null
	 */
	public static @NonNull Map<String, List<String>> parseQuery(@NonNull String query) {
		Objects.requireNonNull(query, "Query string must not be null");
		Map<String, List<String>> params = new LinkedHashMap<>();
		if (query.isEmpty()) {
			return params;
		}
		
		for (String pair : query.split("&")) {
			String[] keyValue = pair.split("=", 2);
			String key = keyValue[0];
			String value = keyValue.length > 1 ? keyValue[1] : "";
			params.computeIfAbsent(key, _ -> new ArrayList<>()).add(value);
		}
		return params;
	}
	
	/**
	 * Calculates the depth of a URI path (number of segments).<br>
	 *
	 * @param pathStr The path string to analyze
	 * @return The number of path segments
	 * @throws NullPointerException If the path string is null
	 */
	public static int calculateUriPathDepth(@NonNull String pathStr) {
		Objects.requireNonNull(pathStr, "URI path string must not be null");
		if (pathStr.isEmpty()) {
			return 0;
		}
		String normalized = pathStr;
		if (normalized.startsWith("/")) {
			normalized = normalized.substring(1);
		}
		if (normalized.endsWith("/")) {
			normalized = normalized.substring(0, normalized.length() - 1);
		}
		if (normalized.isEmpty()) {
			return 0;
		}
		return normalized.split("/").length;
	}
	
	/**
	 * Gets the segments of a URI path.<br>
	 *
	 * @param pathStr The path string to split
	 * @return An array of path segments
	 * @throws NullPointerException If the path string is null
	 */
	public static @NonNull String[] getUriPathSegments(@NonNull String pathStr) {
		Objects.requireNonNull(pathStr, "URI path string must not be null");
		if (pathStr.isEmpty()) {
			return ArrayUtils.EMPTY_STRING_ARRAY;
		}
		
		String normalized = pathStr;
		if (normalized.startsWith("/")) {
			normalized = normalized.substring(1);
		}
		if (normalized.endsWith("/")) {
			normalized = normalized.substring(0, normalized.length() - 1);
		}
		if (normalized.isEmpty()) {
			return ArrayUtils.EMPTY_STRING_ARRAY;
		}
		return normalized.split("/");
	}
	
	/**
	 * Gets the file name (last segment) of a URI path.<br>
	 *
	 * @param pathStr The path string
	 * @return The file name, or empty if the path has no segments
	 * @throws NullPointerException If the path string is null
	 */
	public static @NonNull Optional<String> getUriPathFileName(@NonNull String pathStr) {
		Objects.requireNonNull(pathStr, "URI path string must not be null");
		
		String[] segments = getUriPathSegments(pathStr);
		if (segments.length == 0) {
			return Optional.empty();
		}
		return Optional.of(segments[segments.length - 1]);
	}
	
	/**
	 * Gets the extension of a file name.<br>
	 *
	 * @param fileName The file name
	 * @return The extension without the dot, or empty if no extension
	 * @throws NullPointerException If the file name is null
	 */
	public static @NonNull Optional<String> getUriPathExtension(@NonNull String fileName) {
		Objects.requireNonNull(fileName, "File name must not be null");
		
		int dotIndex = fileName.lastIndexOf('.');
		if (dotIndex < 0 || dotIndex == fileName.length() - 1) {
			return Optional.empty();
		}
		return Optional.of(fileName.substring(dotIndex + 1));
	}
	
	/**
	 * Checks if a URI path is normalized (contains no '.' or '..' segments).<br>
	 *
	 * @param pathStr The path string to check
	 * @return true if the path is normalized
	 * @throws NullPointerException If the path string is null
	 */
	public static boolean isUriPathNormalized(@NonNull String pathStr) {
		Objects.requireNonNull(pathStr, "URI path string must not be null");
		
		for (String segment : getUriPathSegments(pathStr)) {
			if (".".equals(segment) || "..".equals(segment)) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Normalizes a URI path by removing '.' and '..' segments.<br>
	 *
	 * @param pathStr The path string to normalize
	 * @return The normalized path
	 * @throws NullPointerException If the path string is null
	 */
	public static @NonNull String normalizeUriPath(@NonNull String pathStr) {
		Objects.requireNonNull(pathStr, "URI path string must not be null");
		
		List<String> result = new ArrayList<>();
		for (String segment : getUriPathSegments(pathStr)) {
			if (".".equals(segment)) {
				continue;
			} else if ("..".equals(segment)) {
				if (!result.isEmpty()) {
					result.removeLast();
				}
			} else {
				result.add(segment);
			}
		}
		
		String normalized = String.join("/", result);
		return pathStr.startsWith("/") ? "/" + normalized : normalized;
	}
	//endregion
	
	/**
	 * Validates a URI path string against a string constraint config.<br>
	 *
	 * @param value The path to validate
	 * @param pathConfig The string constraint config for path validation
	 * @return A successful result if the constraint is satisfied or not present
	 * @throws NullPointerException If any parameter is null
	 */
	public static @NonNull Result<Void> matchUriPathStringConfig(@NonNull String value, @NonNull Optional<StringConstraintConfig> pathConfig) {
		Objects.requireNonNull(value, "URI path must not be null");
		Objects.requireNonNull(pathConfig, "URI path config constraint must not be null");
		if (pathConfig.isEmpty()) {
			return Result.success();
		}
		
		Result<Void> result = pathConfig.get().matches(value);
		if (result.isError()) {
			return Result.error("Path constraint failed: " + result.errorOrThrow());
		}
		return Result.success();
	}
	
	/**
	 * Validates each segment of a URI path against a string constraint config.<br>
	 *
	 * @param value The path to validate
	 * @param segmentConfig The string constraint config for segment validation
	 * @return A successful result if the constraint is satisfied or not present
	 * @throws NullPointerException If any parameter is null
	 */
	public static @NonNull Result<Void> matchUriPathSegmentConfig(@NonNull String value, @NonNull Optional<StringConstraintConfig> segmentConfig) {
		Objects.requireNonNull(value, "URI path must not be null");
		Objects.requireNonNull(segmentConfig, "URI path segment config constraint must not be null");
		if (segmentConfig.isEmpty()) {
			return Result.success();
		}
		
		String[] segments = getUriPathSegments(value);
		for (String segment : segments) {
			Result<Void> result = segmentConfig.get().matches(segment);
			if (result.isError()) {
				return Result.error("Segment '" + segment + "' constraint failed: " + result.errorOrThrow());
			}
		}
		return Result.success();
	}
	
	/**
	 * Validates a URI path's file name against a string constraint config.<br>
	 *
	 * @param value The path to validate
	 * @param fileConfig The string constraint config for file name validation
	 * @return A successful result if the constraint is satisfied or not present
	 * @throws NullPointerException If any parameter is null
	 */
	public static @NonNull Result<Void> matchUriPathFileNameConfig(@NonNull String value, @NonNull Optional<StringConstraintConfig> fileConfig) {
		Objects.requireNonNull(value, "URI path must not be null");
		Objects.requireNonNull(fileConfig, "URI path file name config constraint must not be null");
		if (fileConfig.isEmpty()) {
			return Result.success();
		}
		
		Optional<String> fileName = getUriPathFileName(value);
		if (fileName.isEmpty()) {
			return Result.error("URI path '" + value + "' has no file name");
		}
		
		Result<Void> result = fileConfig.get().matches(fileName.get());
		if (result.isError()) {
			return Result.error("File constraint failed: " + result.errorOrThrow());
		}
		return Result.success();
	}
	
	/**
	 * Validates that a URI path has no file extension.<br>
	 *
	 * @param value The path to validate
	 * @param withoutExtension The without extension constraint flag
	 * @return A successful result if the constraint is satisfied or not present
	 * @throws NullPointerException If any parameter is null
	 */
	public static @NonNull Result<Void> matchUriPathWithoutExtension(@NonNull String value, @NonNull Optional<Unit> withoutExtension) {
		Objects.requireNonNull(value, "URI path must not be null");
		Objects.requireNonNull(withoutExtension, "URI path without extension constraint must not be null");
		if (withoutExtension.isEmpty()) {
			return Result.success();
		}
		
		Optional<String> fileName = getUriPathFileName(value);
		if (fileName.isPresent() && fileName.get().contains(".")) {
			return Result.error("URI path '" + value + "' must not have an extension");
		}
		return Result.success();
	}
	
	/**
	 * Validates a URI path's file extension against a string constraint config.<br>
	 *
	 * @param value The path to validate
	 * @param extensionConfig The string constraint config for extension validation
	 * @return A successful result if the constraint is satisfied or not present
	 * @throws NullPointerException If any parameter is null
	 */
	public static @NonNull Result<Void> matchUriPathExtensionConfig(@NonNull String value, @NonNull Optional<StringConstraintConfig> extensionConfig) {
		Objects.requireNonNull(value, "URI path must not be null");
		Objects.requireNonNull(extensionConfig, "URI path extension config constraint must not be null");
		if (extensionConfig.isEmpty()) {
			return Result.success();
		}
		
		Optional<String> fileName = getUriPathFileName(value);
		if (fileName.isEmpty()) {
			return Result.error("URI path '" + value + "' has no file name for extension check");
		}
		
		Optional<String> extension = getUriPathExtension(fileName.get());
		if (extension.isEmpty()) {
			return Result.error("URI path '" + value + "' has no extension");
		}
		
		Result<Void> result = extensionConfig.get().matches(extension.get());
		if (result.isError()) {
			return Result.error("Extension constraint failed: " + result.errorOrThrow());
		}
		return Result.success();
	}
	
	/**
	 * Validates that a URI path is an ancestor of all specified paths.<br>
	 *
	 * @param value The path to validate
	 * @param ancestorOf The set of paths that the value must be an ancestor of
	 * @return A successful result if the constraint is satisfied or not present
	 * @throws NullPointerException If any parameter is null
	 */
	public static @NonNull Result<Void> matchUriPathAncestorOf(@NonNull String value, @NonNull Optional<Set<String>> ancestorOf) {
		Objects.requireNonNull(value, "URI path must not be null");
		Objects.requireNonNull(ancestorOf, "URI path ancestor of constraint must not be null");
		if (ancestorOf.isEmpty()) {
			return Result.success();
		}
		
		String normalizedValue = normalizeUriPath(value);
		for (String pathStr : ancestorOf.get()) {
			String normalizedTarget = normalizeUriPath(pathStr);
			if (!normalizedTarget.startsWith(normalizedValue)) {
				return Result.error("URI path '" + value + "' must be ancestor of '" + pathStr + "'");
			}
		}
		return Result.success();
	}
	
	/**
	 * Validates that a URI path is a descendant of all specified paths.<br>
	 *
	 * @param value The path to validate
	 * @param descendantOf The set of paths that the value must be a descendant of
	 * @return A successful result if the constraint is satisfied or not present
	 * @throws NullPointerException If any parameter is null
	 */
	public static @NonNull Result<Void> matchUriPathDescendantOf(@NonNull String value, @NonNull Optional<Set<String>> descendantOf) {
		Objects.requireNonNull(value, "URI path must not be null");
		Objects.requireNonNull(descendantOf, "URI path descendant of constraint must not be null");
		if (descendantOf.isEmpty()) {
			return Result.success();
		}
		
		String normalizedValue = normalizeUriPath(value);
		for (String pathStr : descendantOf.get()) {
			String normalizedTarget = normalizeUriPath(pathStr);
			if (!normalizedValue.startsWith(normalizedTarget)) {
				return Result.error("URI path '" + value + "' must be descendant of '" + pathStr + "'");
			}
		}
		return Result.success();
	}
	
	/**
	 * Validates an InetAddress against an IP version constraint.<br>
	 *
	 * @param value The InetAddress to validate
	 * @param ipVersion The IP version enum constraint config
	 * @return A successful result if the constraint is satisfied or not present
	 * @throws NullPointerException If any parameter is null
	 */
	public static @NonNull Result<Void> matchInetAddressIpVersion(@NonNull InetAddress value, @NonNull Optional<EnumConstraintConfig<IpVersion>> ipVersion) {
		Objects.requireNonNull(value, "InetAddress must not be null");
		Objects.requireNonNull(ipVersion, "IP version constraint must not be null");
		if (ipVersion.isEmpty()) {
			return Result.success();
		}
		
		return matchIpVersion(value.getHostAddress(), ipVersion);
	}
	
	/**
	 * Validates an InetAddress against an IP address type constraint.<br>
	 *
	 * @param value The InetAddress to validate
	 * @param ipType The IP address type enum constraint config
	 * @return A successful result if the constraint is satisfied or not present
	 * @throws NullPointerException If any parameter is null
	 */
	public static @NonNull Result<Void> matchInetAddressIpType(@NonNull InetAddress value, @NonNull Optional<EnumConstraintConfig<IpAddressType>> ipType) {
		Objects.requireNonNull(value, "InetAddress must not be null");
		Objects.requireNonNull(ipType, "IP type constraint must not be null");
		if (ipType.isEmpty()) {
			return Result.success();
		}
		
		return matchIpType(value.getHostAddress(), ipType);
	}
	
	/**
	 * Validates an InetAddress against a subnet membership constraint.<br>
	 *
	 * @param value The InetAddress to validate
	 * @param inAnySubnet The subnet constraint as a pair of (CIDRs, negated)
	 * @return A successful result if the constraint is satisfied or not present
	 * @throws NullPointerException If any parameter is null
	 */
	public static @NonNull Result<Void> matchInetAddressInAnySubnet(@NonNull InetAddress value, @NonNull Optional<Pair<Set<String>, Boolean>> inAnySubnet) {
		Objects.requireNonNull(value, "InetAddress must not be null");
		Objects.requireNonNull(inAnySubnet, "In any subnet constraint must not be null");
		if (inAnySubnet.isEmpty()) {
			return Result.success();
		}
		
		return matchInAnySubnet(value.getHostAddress(), inAnySubnet);
	}
	
	/**
	 * Validates an InetSocketAddress's address component against an InetAddressConstraintConfig.<br>
	 *
	 * @param value The InetSocketAddress to validate
	 * @param addressConfig The InetAddressConstraintConfig for address validation
	 * @return A successful result if the constraint is satisfied or not present
	 * @throws NullPointerException If any parameter is null
	 */
	public static @NonNull Result<Void> matchInetSocketAddressAddress(@NonNull InetSocketAddress value, @NonNull Optional<InetAddressConstraintConfig> addressConfig) {
		Objects.requireNonNull(value, "InetSocketAddress must not be null");
		Objects.requireNonNull(addressConfig, "Address config constraint must not be null");
		if (addressConfig.isEmpty()) {
			return Result.success();
		}
		
		InetAddress address = value.getAddress();
		if (address == null) {
			return Result.error("InetSocketAddress '" + value + "' has no resolved address");
		}
		
		Result<Void> result = addressConfig.get().matches(address);
		if (result.isError()) {
			return Result.error("Address constraint failed: " + result.errorOrThrow());
		}
		return Result.success();
	}
	
	/**
	 * Validates an InetSocketAddress's port component against a PortConstraintConfig.<br>
	 *
	 * @param value The InetSocketAddress to validate
	 * @param portConfig The PortConstraintConfig for port validation
	 * @return A successful result if the constraint is satisfied or not present
	 * @throws NullPointerException If any parameter is null
	 */
	public static @NonNull Result<Void> matchInetSocketAddressPort(@NonNull InetSocketAddress value, @NonNull Optional<PortConstraintConfig> portConfig) {
		Objects.requireNonNull(value, "InetSocketAddress must not be null");
		Objects.requireNonNull(portConfig, "Port config constraint must not be null");
		if (portConfig.isEmpty()) {
			return Result.success();
		}
		
		Result<Void> result = portConfig.get().matches(value.getPort());
		if (result.isError()) {
			return Result.error("Port constraint failed: " + result.errorOrThrow());
		}
		return Result.success();
	}
}
