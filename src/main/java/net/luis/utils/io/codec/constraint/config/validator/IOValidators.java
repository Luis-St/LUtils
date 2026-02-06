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

package net.luis.utils.io.codec.constraint.config.validator;

import net.luis.utils.io.codec.constraint.config.*;
import net.luis.utils.io.codec.constraint.config.io.*;
import net.luis.utils.io.codec.constraint.util.*;
import net.luis.utils.io.network.address.*;
import net.luis.utils.io.network.address.exception.IpParseException;
import net.luis.utils.io.network.address.ipv4.Ipv4Address;
import net.luis.utils.io.network.address.ipv4.Ipv4Network;
import net.luis.utils.io.network.address.ipv6.Ipv6Address;
import net.luis.utils.io.network.address.ipv6.Ipv6Network;
import net.luis.utils.io.network.address.mac.MacAddress;
import net.luis.utils.util.Pair;
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
 *     in the generic {@link ConstraintValidators} class due to their specialized semantics
 *     for ports, hosts, paths, queries, and URIs.
 * </p>
 *
 * @author Luis-St
 */
@SuppressWarnings("OptionalContainsCollection")
public final class IOValidators {
	
	/**
	 * Private constructor to prevent instantiation.<br>
	 */
	private IOValidators() {}
	
	/**
	 * Validates a port value against a port range constraint.<br>
	 *
	 * @param value The port value to validate
	 * @param inRange The port range constraint as a pair of ((min, max), negated)
	 * @throws NullPointerException If any parameter is null
	 * @throws ConstraintViolateException If the constraint is not satisfied
	 */
	public static void validatePortRange(int value, @NonNull Optional<Pair<Pair<Integer, Integer>, Boolean>> inRange) {
		Objects.requireNonNull(inRange, "Port in range constraint must not be null");
		if (inRange.isEmpty()) {
			return;
		}
		
		int min = inRange.get().getFirst().getFirst();
		int max = inRange.get().getFirst().getSecond();
		boolean negated = inRange.get().getSecond();
		boolean inRangeCheck = value >= min && value <= max;
		
		if (!negated && !inRangeCheck) {
			throw new ConstraintViolateException("Port " + value + " must be in range [" + min + ", " + max + "]");
		} else if (negated && inRangeCheck) {
			throw new ConstraintViolateException("Port " + value + " must not be in range [" + min + ", " + max + "]");
		}
	}
	
	/**
	 * Validates a port value against a port type constraint.<br>
	 *
	 * @param port The port value to validate
	 * @param type The port type enum constraint config
	 * @throws NullPointerException If any parameter is null
	 * @throws ConstraintViolateException If the constraint is not satisfied
	 */
	public static void validatePortType(int port, @NonNull Optional<EnumConstraintConfig<PortRange>> type) {
		Objects.requireNonNull(type, "Port type constraint must not be null");
		if (type.isEmpty()) {
			return;
		}
		
		PortRange portRange = PortRange.fromPort(port);
		try {
			type.get().validate(portRange);
		} catch (ConstraintViolateException e) {
			throw new ConstraintViolateException("Port type constraint failed: " + e.getMessage(), e);
		}
	}
	
	/**
	 * Validates a host value against an IP version constraint.<br>
	 *
	 * @param value The host value to validate
	 * @param ipVersion The IP version enum constraint config
	 * @throws NullPointerException If any parameter is null
	 * @throws ConstraintViolateException If the constraint is not satisfied
	 */
	public static void validateIpVersion(@NonNull String value, @NonNull Optional<EnumConstraintConfig<IpVersion>> ipVersion) {
		Objects.requireNonNull(value, "Ip address must not be null");
		Objects.requireNonNull(ipVersion, "Ip version constraint must not be null");
		if (ipVersion.isEmpty()) {
			return;
		}
		
		IpVersion detectedVersion;
		if (IpAddresses.isValidIpv4(value)) {
			detectedVersion = IpVersion.IPV4;
		} else if (IpAddresses.isValidIpv6(value)) {
			detectedVersion = IpVersion.IPV6;
		} else {
			throw new ConstraintViolateException("Host '" + value + "' is not a valid Ip address");
		}
		
		try {
			ipVersion.get().validate(detectedVersion);
		} catch (ConstraintViolateException e) {
			throw new ConstraintViolateException("Ip version constraint failed: " + e.getMessage(), e);
		}
	}
	
	/**
	 * Validates a host value against an IP address type constraint.<br>
	 *
	 * @param value The host value to validate
	 * @param ipType The IP address type enum constraint config
	 * @throws NullPointerException If any parameter is null
	 * @throws ConstraintViolateException If the constraint is not satisfied
	 */
	public static void validateIpType(@NonNull String value, @NonNull Optional<EnumConstraintConfig<IpAddressType>> ipType) {
		Objects.requireNonNull(value, "Ip address must not be null");
		Objects.requireNonNull(ipType, "Ip type constraint must not be null");
		if (ipType.isEmpty()) {
			return;
		}
		
		try {
			IpAddress<?> address = IpAddresses.parse(value);
			ipType.get().validate(getIpAddressType(address));
		} catch (IpParseException e) {
			throw new ConstraintViolateException("Host '" + value + "' is not a valid ip address: " + e.getMessage());
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
	 * @throws NullPointerException If any parameter is null
	 * @throws ConstraintViolateException If the constraint is not satisfied
	 */
	public static void validateInAnySubnet(@NonNull String value, @NonNull Optional<Pair<Set<String>, Boolean>> inAnySubnet) {
		Objects.requireNonNull(value, "Ip address must not be null");
		Objects.requireNonNull(inAnySubnet, "In any subnet constraint must not be null");
		if (inAnySubnet.isEmpty()) {
			return;
		}
		
		Set<IpNetwork<?, ?>> subnets = new HashSet<>();
		for (String cidr : inAnySubnet.get().getFirst()) {
			try {
				subnets.add(IpAddresses.parseNetwork(cidr));
			} catch (IpParseException e) {
				throw new ConstraintViolateException("Invalid CIDR '" + cidr + "' in subnet constraint: " + e.getMessage());
			}
		}
		
		try {
			IpAddress<?> address = IpAddresses.parse(value);
			
			switch (address) {
				case Ipv4Address ipv4 -> validateIpv4SubnetMembership(ipv4, subnets, inAnySubnet.get().getSecond());
				case Ipv6Address ipv6 -> validateIpv6SubnetMembership(ipv6, subnets, inAnySubnet.get().getSecond());
			}
		} catch (IpParseException e) {
			throw new ConstraintViolateException("Host '" + value + "' is not a valid ip address: " + e.getMessage());
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
	 * @throws NullPointerException If any parameter is null
	 * @throws ConstraintViolateException If the membership condition is not satisfied
	 */
	private static void validateIpv4SubnetMembership(@NonNull Ipv4Address ipv4, @NonNull Set<IpNetwork<?, ?>> subnets, boolean negated) {
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
			throw new ConstraintViolateException("IPv4 address '" + ipv4 + "' must be member of at least one specified subnet: " + validSubnets);
		} else if (negated && !memberOfNone) {
			throw new ConstraintViolateException("IPv4 address '" + ipv4 + "' must not be member of any specified subnet: " + validSubnets);
		}
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
	 * @throws NullPointerException If any parameter is null
	 * @throws ConstraintViolateException If the membership condition is not satisfied
	 */
	private static void validateIpv6SubnetMembership(@NonNull Ipv6Address ipv6, @NonNull Set<IpNetwork<?, ?>> subnets, boolean negated) {
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
			throw new ConstraintViolateException("IPv6 address '" + ipv6 + "' must be member of at least one specified subnet: " + validSubnets);
		} else if (negated && !memberOfNone) {
			throw new ConstraintViolateException("IPv6 address '" + ipv6 + "' must not be member of any specified subnet: " + validSubnets);
		}
	}
	
	/**
	 * Validates that a host value is a root domain (has exactly one dot).<br>
	 *
	 * @param value The host value to validate
	 * @param rootDomain The root domain constraint flag
	 * @throws NullPointerException If any parameter is null
	 * @throws ConstraintViolateException If the constraint is not satisfied
	 */
	public static void validateRootDomain(@NonNull String value, @NonNull Optional<Unit> rootDomain) {
		Objects.requireNonNull(value, "Domain must not be null");
		Objects.requireNonNull(rootDomain, "Root domain constraint must not be null");
		if (rootDomain.isEmpty()) {
			return;
		}
		
		long dotCount = value.chars().filter(c -> c == '.').count();
		if (dotCount != 1) {
			throw new ConstraintViolateException("Host '" + value + "' must be a root domain");
		}
	}
	
	/**
	 * Validates that a host value is a subdomain (has more than one dot).<br>
	 *
	 * @param value The host value to validate
	 * @param subDomain The subdomain constraint flag
	 * @throws NullPointerException If any parameter is null
	 * @throws ConstraintViolateException If the constraint is not satisfied
	 */
	public static void validateSubDomain(@NonNull String value, @NonNull Optional<Unit> subDomain) {
		Objects.requireNonNull(value, "Domain must not be null");
		Objects.requireNonNull(subDomain, "Sub domain constraint must not be null");
		if (subDomain.isEmpty()) {
			return;
		}
		
		long dotCount = value.chars().filter(c -> c == '.').count();
		if (dotCount < 2) {
			throw new ConstraintViolateException("Host '" + value + "' must be a subdomain");
		}
	}
	
	/**
	 * Validates that a path is canonical.<br>
	 *
	 * @param value The path to validate
	 * @param canonical The canonical constraint flag
	 * @throws NullPointerException If any parameter is null
	 * @throws ConstraintViolateException If the constraint is not satisfied
	 */
	public static void validatePathCanonical(@NonNull Path value, @NonNull Optional<Unit> canonical) {
		Objects.requireNonNull(value, "Path must not be null");
		Objects.requireNonNull(canonical, "Path canonical constraint must not be null");
		if (canonical.isEmpty()) {
			return;
		}
		
		try {
			File file = value.toFile();
			String canonicalPath = file.getCanonicalPath();
			if (!value.toString().equals(canonicalPath)) {
				throw new ConstraintViolateException("Path '" + value + "' must be canonical");
			}
		} catch (IOException e) {
			throw new ConstraintViolateException("Failed to get canonical path for '" + value + "': " + e.getMessage());
		}
	}
	
	/**
	 * Validates a path string against a string constraint config.<br>
	 *
	 * @param value The path to validate
	 * @param pathConfig The string constraint config for path validation
	 * @throws NullPointerException If any parameter is null
	 * @throws ConstraintViolateException If the constraint is not satisfied
	 */
	public static void validatePathStringConfig(@NonNull Path value, @NonNull Optional<StringConstraintConfig> pathConfig) {
		Objects.requireNonNull(value, "Path must not be null");
		Objects.requireNonNull(pathConfig, "Path config constraint must not be null");
		if (pathConfig.isEmpty()) {
			return;
		}
		
		try {
			pathConfig.get().validate(value.toString());
		} catch (ConstraintViolateException e) {
			throw new ConstraintViolateException("Path constraint failed: " + e.getMessage(), e);
		}
	}
	
	/**
	 * Validates a path's root component against a string constraint config.<br>
	 *
	 * @param value The path to validate
	 * @param rootConfig The string constraint config for root component validation
	 * @throws NullPointerException If any parameter is null
	 * @throws ConstraintViolateException If the constraint is not satisfied
	 */
	public static void validatePathRootConfig(@NonNull Path value, @NonNull Optional<StringConstraintConfig> rootConfig) {
		Objects.requireNonNull(value, "Path must not be null");
		Objects.requireNonNull(rootConfig, "Path root config constraint must not be null");
		if (rootConfig.isEmpty()) {
			return;
		}
		
		Path root = value.getRoot();
		if (root == null) {
			throw new ConstraintViolateException("Path '" + value + "' has no root component");
		}
		
		try {
			rootConfig.get().validate(root.toString());
		} catch (ConstraintViolateException e) {
			throw new ConstraintViolateException("Root constraint failed: " + e.getMessage(), e);
		}
	}
	
	/**
	 * Validates a path's parent directory against a string constraint config.<br>
	 *
	 * @param value The path to validate
	 * @param parentConfig The string constraint config for parent directory validation
	 * @throws NullPointerException If any parameter is null
	 * @throws ConstraintViolateException If the constraint is not satisfied
	 */
	public static void validatePathParentConfig(@NonNull Path value, @NonNull Optional<StringConstraintConfig> parentConfig) {
		Objects.requireNonNull(value, "Path must not be null");
		Objects.requireNonNull(parentConfig, "Path parent config constraint must not be null");
		if (parentConfig.isEmpty()) {
			return;
		}
		
		Path parent = value.getParent();
		if (parent == null) {
			throw new ConstraintViolateException("Path '" + value + "' has no parent component");
		}
		
		try {
			parentConfig.get().validate(parent.toString());
		} catch (ConstraintViolateException e) {
			throw new ConstraintViolateException("Parent constraint failed: " + e.getMessage(), e);
		}
	}
	
	/**
	 * Validates each segment of a path against a string constraint config.<br>
	 *
	 * @param value The path to validate
	 * @param segmentConfig The string constraint config for segment validation
	 * @throws NullPointerException If any parameter is null
	 * @throws ConstraintViolateException If the constraint is not satisfied
	 */
	public static void validatePathSegmentConfig(@NonNull Path value, @NonNull Optional<StringConstraintConfig> segmentConfig) {
		Objects.requireNonNull(value, "Path must not be null");
		Objects.requireNonNull(segmentConfig, "Path segment config constraint must not be null");
		if (segmentConfig.isEmpty()) {
			return;
		}
		
		for (int i = 0; i < value.getNameCount(); i++) {
			try {
				segmentConfig.get().validate(value.getName(i).toString());
			} catch (ConstraintViolateException e) {
				throw new ConstraintViolateException("Segment '" + value.getName(i) + "' constraint failed: " + e.getMessage(), e);
			}
		}
	}
	
	/**
	 * Validates a path's file name against a string constraint config.<br>
	 *
	 * @param value The path to validate
	 * @param fileConfig The string constraint config for file name validation
	 * @throws NullPointerException If any parameter is null
	 * @throws ConstraintViolateException If the constraint is not satisfied
	 */
	public static void validatePathFileNameConfig(@NonNull Path value, @NonNull Optional<StringConstraintConfig> fileConfig) {
		Objects.requireNonNull(value, "Path must not be null");
		Objects.requireNonNull(fileConfig, "Path file name config constraint must not be null");
		if (fileConfig.isEmpty()) {
			return;
		}
		
		Path fileName = value.getFileName();
		if (fileName == null) {
			throw new ConstraintViolateException("Path '" + value + "' has no file name");
		}
		
		try {
			fileConfig.get().validate(fileName.toString());
		} catch (ConstraintViolateException e) {
			throw new ConstraintViolateException("File name constraint failed: " + e.getMessage(), e);
		}
	}
	
	/**
	 * Validates a path's file extension against a string constraint config.<br>
	 *
	 * @param value The path to validate
	 * @param extensionConfig The string constraint config for extension validation
	 * @throws NullPointerException If any parameter is null
	 * @throws ConstraintViolateException If the constraint is not satisfied
	 */
	public static void validatePathExtensionConfig(@NonNull Path value, @NonNull Optional<StringConstraintConfig> extensionConfig) {
		Objects.requireNonNull(value, "Path must not be null");
		Objects.requireNonNull(extensionConfig, "Path extension config constraint must not be null");
		if (extensionConfig.isEmpty()) {
			return;
		}
		
		Path fileName = value.getFileName();
		if (fileName == null) {
			throw new ConstraintViolateException("Path '" + value + "' has no file name for extension check");
		}
		
		String fileNameStr = fileName.toString();
		int dotIndex = fileNameStr.lastIndexOf('.');
		if (dotIndex < 0) {
			throw new ConstraintViolateException("Path '" + value + "' has no extension");
		}
		
		String extension = fileNameStr.substring(dotIndex + 1);
		
		try {
			extensionConfig.get().validate(extension);
		} catch (ConstraintViolateException e) {
			throw new ConstraintViolateException("Extension constraint failed: " + e.getMessage(), e);
		}
	}
	
	/**
	 * Validates that a path has no file extension.<br>
	 *
	 * @param value The path to validate
	 * @param withoutExtension The without extension constraint flag
	 * @throws NullPointerException If any parameter is null
	 * @throws ConstraintViolateException If the constraint is not satisfied
	 */
	public static void validatePathWithoutExtension(@NonNull Path value, @NonNull Optional<Unit> withoutExtension) {
		Objects.requireNonNull(value, "Path must not be null");
		Objects.requireNonNull(withoutExtension, "Path without extension constraint must not be null");
		if (withoutExtension.isEmpty()) {
			return;
		}
		
		Path fileName = value.getFileName();
		if (fileName != null && fileName.toString().contains(".")) {
			throw new ConstraintViolateException("Path '" + value + "' must not have an extension");
		}
	}
	
	/**
	 * Validates that a path is an ancestor of all specified paths.<br>
	 *
	 * @param value The path to validate
	 * @param ancestorOf The set of paths that the value must be an ancestor of
	 * @throws NullPointerException If any parameter is null
	 * @throws ConstraintViolateException If the constraint is not satisfied
	 */
	public static void validatePathAncestorOf(@NonNull Path value, @NonNull Optional<Set<String>> ancestorOf) {
		Objects.requireNonNull(value, "Path must not be null");
		Objects.requireNonNull(ancestorOf, "path ancestor of constraint must not be null");
		if (ancestorOf.isEmpty()) {
			return;
		}
		
		for (String pathStr : ancestorOf.get()) {
			Path targetPath = Path.of(pathStr);
			if (!targetPath.startsWith(value)) {
				throw new ConstraintViolateException("Path '" + value + "' must be ancestor of '" + pathStr + "'");
			}
		}
	}
	
	/**
	 * Validates that a path is a descendant of all specified paths.<br>
	 *
	 * @param value The path to validate
	 * @param descendantOf The set of paths that the value must be a descendant of
	 * @throws NullPointerException If any parameter is null
	 * @throws ConstraintViolateException If the constraint is not satisfied
	 */
	public static void validatePathDescendantOf(@NonNull Path value, @NonNull Optional<Set<String>> descendantOf) {
		Objects.requireNonNull(value, "Path must not be null");
		Objects.requireNonNull(descendantOf, "Path descendant of constraint must not be null");
		if (descendantOf.isEmpty()) {
			return;
		}
		
		for (String pathStr : descendantOf.get()) {
			Path targetPath = Path.of(pathStr);
			if (!value.startsWith(targetPath)) {
				throw new ConstraintViolateException("Path '" + value + "' must be descendant of '" + pathStr + "'");
			}
		}
	}
	
	/**
	 * Validates query parameter values against key-specific string constraint configs.<br>
	 *
	 * @param value The query parameters to validate
	 * @param valueConstraints A map of key to string constraint config
	 * @throws NullPointerException If any parameter is null
	 * @throws ConstraintViolateException If the constraint is not satisfied
	 */
	public static void validateQueryValueConstraints(@NonNull Map<String, List<String>> value, @NonNull Optional<Map<String, StringConstraintConfig>> valueConstraints) {
		Objects.requireNonNull(value, "Query parameter map must not be null");
		Objects.requireNonNull(valueConstraints, "Query value constraints must not be null");
		if (valueConstraints.isEmpty()) {
			return;
		}
		
		for (Map.Entry<String, StringConstraintConfig> entry : valueConstraints.get().entrySet()) {
			String key = entry.getKey();
			StringConstraintConfig config = entry.getValue();
			
			if (value.containsKey(key)) {
				for (String v : value.get(key)) {
					try {
						config.validate(v);
					} catch (ConstraintViolateException e) {
						throw new ConstraintViolateException("Value constraint for key '" + key + "' failed: " + e.getMessage(), e);
					}
				}
			}
		}
	}
	
	/**
	 * Validates query parameter values against pattern-matched string constraint configs.<br>
	 *
	 * @param value The query parameters to validate
	 * @param patternValueConstraints A map of pattern to string constraint config
	 * @throws NullPointerException If any parameter is null
	 * @throws ConstraintViolateException If the constraint is not satisfied
	 */
	public static void validateQueryPatternValueConstraints(@NonNull Map<String, List<String>> value, @NonNull Optional<Map<Pattern, StringConstraintConfig>> patternValueConstraints) {
		Objects.requireNonNull(value, "Query parameter map must not be null");
		Objects.requireNonNull(patternValueConstraints, "Query pattern value constraints must not be null");
		if (patternValueConstraints.isEmpty()) {
			return;
		}
		
		for (Map.Entry<Pattern, StringConstraintConfig> entry : patternValueConstraints.get().entrySet()) {
			Pattern pattern = entry.getKey();
			StringConstraintConfig config = entry.getValue();
			
			for (Map.Entry<String, List<String>> kvEntry : value.entrySet()) {
				if (pattern.matcher(kvEntry.getKey()).matches()) {
					for (String v : kvEntry.getValue()) {
						try {
							config.validate(v);
						} catch (ConstraintViolateException e) {
							throw new ConstraintViolateException("Pattern value constraint for key '" + kvEntry.getKey() + "' failed: " + e.getMessage(), e);
						}
					}
				}
			}
		}
	}
	
	/**
	 * Validates that all query parameters have exactly one value.<br>
	 *
	 * @param value The query parameters to validate
	 * @param singleValued The single-valued constraint flag
	 * @throws NullPointerException If any parameter is null
	 * @throws ConstraintViolateException If the constraint is not satisfied
	 */
	public static void validateQuerySingleValued(@NonNull Map<String, List<String>> value, @NonNull Optional<Unit> singleValued) {
		Objects.requireNonNull(value, "Query parameter map must not be null");
		Objects.requireNonNull(singleValued, "Query single valued constraint must not be null");
		if (singleValued.isEmpty()) {
			return;
		}
		
		for (Map.Entry<String, List<String>> entry : value.entrySet()) {
			if (entry.getValue().size() != 1) {
				throw new ConstraintViolateException("Query parameter '" + entry.getKey() + "' must have exactly one value");
			}
		}
	}
	
	/**
	 * Validates that all query parameter values are unique across all keys.<br>
	 *
	 * @param value The query parameters to validate
	 * @param uniqueValues The unique values constraint flag
	 * @throws NullPointerException If any parameter is null
	 * @throws ConstraintViolateException If the constraint is not satisfied
	 */
	public static void validateQueryUniqueValues(@NonNull Map<String, List<String>> value, @NonNull Optional<Unit> uniqueValues) {
		Objects.requireNonNull(value, "Query parameter map must not be null");
		Objects.requireNonNull(uniqueValues, "Query unique values constraint must not be null");
		if (uniqueValues.isEmpty()) {
			return;
		}
		
		Set<String> allValues = new HashSet<>();
		for (List<String> values : value.values()) {
			for (String val : values) {
				if (!allValues.add(val)) {
					throw new ConstraintViolateException("Query values must be unique");
				}
			}
		}
	}
	
	/**
	 * Validates query parameter multi-value sizes against key-specific size constraint configs.<br>
	 *
	 * @param value The query parameters to validate
	 * @param multiValuedConstraints A map of key to size constraint config
	 * @throws NullPointerException If any parameter is null
	 * @throws ConstraintViolateException If the constraint is not satisfied
	 */
	public static void validateQueryMultiValuedConstraints(@NonNull Map<String, List<String>> value, @NonNull Optional<Map<String, SizeConstraintConfig>> multiValuedConstraints) {
		Objects.requireNonNull(value, "Query parameter map must not be null");
		Objects.requireNonNull(multiValuedConstraints, "Query multi valued constraints must not be null");
		if (multiValuedConstraints.isEmpty()) {
			return;
		}
		
		for (Map.Entry<String, SizeConstraintConfig> entry : multiValuedConstraints.get().entrySet()) {
			String key = entry.getKey();
			SizeConstraintConfig config = entry.getValue();
			
			if (value.containsKey(key)) {
				try {
					config.validate(value.get(key).size());
				} catch (ConstraintViolateException e) {
					throw new ConstraintViolateException("Multi-valued constraint for key '" + key + "' failed: " + e.getMessage(), e);
				}
			}
		}
	}
	
	/**
	 * Validates a URI's scheme against a string constraint config.<br>
	 *
	 * @param value The URI to validate
	 * @param schemeConfig The string constraint config for scheme validation
	 * @throws NullPointerException If any parameter is null
	 * @throws ConstraintViolateException If the constraint is not satisfied
	 */
	public static void validateUriSchemeConfig(@NonNull URI value, @NonNull Optional<StringConstraintConfig> schemeConfig) {
		Objects.requireNonNull(value, "Uri must not be null");
		Objects.requireNonNull(schemeConfig, "Uri scheme config constraint must not be null");
		if (schemeConfig.isEmpty()) {
			return;
		}
		
		String scheme = value.getScheme();
		if (scheme == null) {
			throw new ConstraintViolateException("URI '" + value + "' has no scheme");
		}
		
		try {
			schemeConfig.get().validate(scheme);
		} catch (ConstraintViolateException e) {
			throw new ConstraintViolateException("Scheme constraint failed: " + e.getMessage(), e);
		}
	}
	
	/**
	 * Validates a URI's host against a host constraint config.<br>
	 *
	 * @param value The URI to validate
	 * @param hostConfig The host constraint config for host validation
	 * @throws NullPointerException If any parameter is null
	 * @throws ConstraintViolateException If the constraint is not satisfied
	 */
	public static void validateUriHostConfig(@NonNull URI value, @NonNull Optional<HostConstraintConfig> hostConfig) {
		Objects.requireNonNull(value, "Uri must not be null");
		Objects.requireNonNull(hostConfig, "Uri host config constraint must not be null");
		if (hostConfig.isEmpty()) {
			return;
		}
		
		String host = value.getHost();
		if (host == null) {
			throw new ConstraintViolateException("URI '" + value + "' has no host");
		}
		
		try {
			hostConfig.get().validate(host);
		} catch (ConstraintViolateException e) {
			throw new ConstraintViolateException("Host constraint failed: " + e.getMessage(), e);
		}
	}
	
	/**
	 * Validates a URI's user info against a string constraint config.<br>
	 *
	 * @param value The URI to validate
	 * @param userInfoConfig The string constraint config for user info validation
	 * @throws NullPointerException If any parameter is null
	 * @throws ConstraintViolateException If the constraint is not satisfied
	 */
	public static void validateUriUserInfoConfig(@NonNull URI value, @NonNull Optional<StringConstraintConfig> userInfoConfig) {
		Objects.requireNonNull(value, "Uri must not be null");
		Objects.requireNonNull(userInfoConfig, "Uri user info config constraint must not be null");
		if (userInfoConfig.isEmpty()) {
			return;
		}
		
		String userInfo = value.getUserInfo();
		if (userInfo == null) {
			throw new ConstraintViolateException("URI '" + value + "' has no user info");
		}
		
		try {
			userInfoConfig.get().validate(userInfo);
		} catch (ConstraintViolateException e) {
			throw new ConstraintViolateException("User info constraint failed: " + e.getMessage(), e);
		}
	}
	
	/**
	 * Validates a URI's port against a port constraint config.<br>
	 *
	 * @param value The URI to validate
	 * @param portConfig The port constraint config for port validation
	 * @throws NullPointerException If any parameter is null
	 * @throws ConstraintViolateException If the constraint is not satisfied
	 */
	public static void validateUriPortConfig(@NonNull URI value, @NonNull Optional<PortConstraintConfig> portConfig) {
		Objects.requireNonNull(value, "Uri must not be null");
		Objects.requireNonNull(portConfig, "Uri port config constraint must not be null");
		if (portConfig.isEmpty()) {
			return;
		}
		
		int port = value.getPort();
		if (port == -1) {
			throw new ConstraintViolateException("URI '" + value + "' has no port");
		}
		
		try {
			portConfig.get().validate(port);
		} catch (ConstraintViolateException e) {
			throw new ConstraintViolateException("Port constraint failed: " + e.getMessage(), e);
		}
	}
	
	/**
	 * Validates a URI's path against a URI path constraint config.<br>
	 *
	 * @param value The URI to validate
	 * @param pathConfig The URI path constraint config for path validation
	 * @throws NullPointerException If any parameter is null
	 * @throws ConstraintViolateException If the constraint is not satisfied
	 */
	public static void validateUriPathConfig(@NonNull URI value, @NonNull Optional<URIPathConstraintConfig> pathConfig) {
		Objects.requireNonNull(value, "Uri must not be null");
		Objects.requireNonNull(pathConfig, "Uri path config constraint must not be null");
		if (pathConfig.isEmpty()) {
			return;
		}
		
		String pathStr = value.getPath();
		if (pathStr == null || pathStr.isEmpty()) {
			throw new ConstraintViolateException("URI '" + value + "' has no path");
		}
		
		try {
			pathConfig.get().validate(pathStr);
		} catch (ConstraintViolateException e) {
			throw new ConstraintViolateException("Path constraint failed: " + e.getMessage(), e);
		}
	}
	
	/**
	 * Validates a URI's query against a query constraint config.<br>
	 *
	 * @param value The URI to validate
	 * @param queryConfig The query constraint config for query validation
	 * @throws NullPointerException If any parameter is null
	 * @throws ConstraintViolateException If the constraint is not satisfied
	 */
	public static void validateUriQueryConfig(@NonNull URI value, @NonNull Optional<QueryConstraintConfig> queryConfig) {
		Objects.requireNonNull(value, "Uri must not be null");
		Objects.requireNonNull(queryConfig, "Uri query config constraint must not be null");
		if (queryConfig.isEmpty()) {
			return;
		}
		
		String queryStr = value.getQuery();
		if (queryStr == null) {
			throw new ConstraintViolateException("URI '" + value + "' has no query");
		}
		
		Map<String, List<String>> queryParams = parseQuery(queryStr);
		try {
			queryConfig.get().validate(queryParams);
		} catch (ConstraintViolateException e) {
			throw new ConstraintViolateException("Query constraint failed: " + e.getMessage(), e);
		}
	}
	
	/**
	 * Validates a URI's fragment against a string constraint config.<br>
	 *
	 * @param value The URI to validate
	 * @param fragmentConfig The string constraint config for fragment validation
	 * @throws NullPointerException If any parameter is null
	 * @throws ConstraintViolateException If the constraint is not satisfied
	 */
	public static void validateUriFragmentConfig(@NonNull URI value, @NonNull Optional<StringConstraintConfig> fragmentConfig) {
		Objects.requireNonNull(value, "Uri must not be null");
		Objects.requireNonNull(fragmentConfig, "Uri fragment config constraint must not be null");
		if (fragmentConfig.isEmpty()) {
			return;
		}
		
		String fragment = value.getFragment();
		if (fragment == null) {
			throw new ConstraintViolateException("URI '" + value + "' has no fragment");
		}
		
		try {
			fragmentConfig.get().validate(fragment);
		} catch (ConstraintViolateException e) {
			throw new ConstraintViolateException("Fragment constraint failed: " + e.getMessage(), e);
		}
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
	private static @NonNull String normalizeUriPath(@NonNull String pathStr) {
		Objects.requireNonNull(pathStr, "URI path string must not be null");
		
		List<String> result = new ArrayList<>();
		for (String segment : getUriPathSegments(pathStr)) {
			if ("..".equals(segment)) {
				if (!result.isEmpty()) {
					result.removeLast();
				}
			} else if (!".".equals(segment)) {
				result.add(segment);
			}
		}
		
		String normalized = String.join("/", result);
		return pathStr.startsWith("/") ? "/" + normalized : normalized;
	}
	
	/**
	 * Validates a URI path string against a string constraint config.<br>
	 *
	 * @param value The path to validate
	 * @param pathConfig The string constraint config for path validation
	 * @throws NullPointerException If any parameter is null
	 * @throws ConstraintViolateException If the constraint is not satisfied
	 */
	public static void validateUriPathStringConfig(@NonNull String value, @NonNull Optional<StringConstraintConfig> pathConfig) {
		Objects.requireNonNull(value, "URI path must not be null");
		Objects.requireNonNull(pathConfig, "URI path config constraint must not be null");
		if (pathConfig.isEmpty()) {
			return;
		}
		
		try {
			pathConfig.get().validate(value);
		} catch (ConstraintViolateException e) {
			throw new ConstraintViolateException("Path constraint failed: " + e.getMessage(), e);
		}
	}
	
	/**
	 * Validates each segment of a URI path against a string constraint config.<br>
	 *
	 * @param value The path to validate
	 * @param segmentConfig The string constraint config for segment validation
	 * @throws NullPointerException If any parameter is null
	 * @throws ConstraintViolateException If the constraint is not satisfied
	 */
	public static void validateUriPathSegmentConfig(@NonNull String value, @NonNull Optional<StringConstraintConfig> segmentConfig) {
		Objects.requireNonNull(value, "URI path must not be null");
		Objects.requireNonNull(segmentConfig, "URI path segment config constraint must not be null");
		if (segmentConfig.isEmpty()) {
			return;
		}
		
		String[] segments = getUriPathSegments(value);
		for (String segment : segments) {
			try {
				segmentConfig.get().validate(segment);
			} catch (ConstraintViolateException e) {
				throw new ConstraintViolateException("Segment '" + segment + "' constraint failed: " + e.getMessage(), e);
			}
		}
	}
	
	/**
	 * Validates a URI path's file name against a string constraint config.<br>
	 *
	 * @param value The path to validate
	 * @param fileConfig The string constraint config for file name validation
	 * @throws NullPointerException If any parameter is null
	 * @throws ConstraintViolateException If the constraint is not satisfied
	 */
	public static void validateUriPathFileNameConfig(@NonNull String value, @NonNull Optional<StringConstraintConfig> fileConfig) {
		Objects.requireNonNull(value, "URI path must not be null");
		Objects.requireNonNull(fileConfig, "URI path file name config constraint must not be null");
		if (fileConfig.isEmpty()) {
			return;
		}
		
		Optional<String> fileName = getUriPathFileName(value);
		if (fileName.isEmpty()) {
			throw new ConstraintViolateException("URI path '" + value + "' has no file name");
		}
		
		try {
			fileConfig.get().validate(fileName.get());
		} catch (ConstraintViolateException e) {
			throw new ConstraintViolateException("File name constraint failed: " + e.getMessage(), e);
		}
	}
	
	/**
	 * Validates that a URI path has no file extension.<br>
	 *
	 * @param value The path to validate
	 * @param withoutExtension The without extension constraint flag
	 * @throws NullPointerException If any parameter is null
	 * @throws ConstraintViolateException If the constraint is not satisfied
	 */
	public static void validateUriPathWithoutExtension(@NonNull String value, @NonNull Optional<Unit> withoutExtension) {
		Objects.requireNonNull(value, "URI path must not be null");
		Objects.requireNonNull(withoutExtension, "URI path without extension constraint must not be null");
		if (withoutExtension.isEmpty()) {
			return;
		}
		
		Optional<String> fileName = getUriPathFileName(value);
		if (fileName.isPresent() && fileName.get().contains(".")) {
			throw new ConstraintViolateException("URI path '" + value + "' must not have an extension");
		}
	}
	
	/**
	 * Validates a URI path's file extension against a string constraint config.<br>
	 *
	 * @param value The path to validate
	 * @param extensionConfig The string constraint config for extension validation
	 * @throws NullPointerException If any parameter is null
	 * @throws ConstraintViolateException If the constraint is not satisfied
	 */
	public static void validateUriPathExtensionConfig(@NonNull String value, @NonNull Optional<StringConstraintConfig> extensionConfig) {
		Objects.requireNonNull(value, "URI path must not be null");
		Objects.requireNonNull(extensionConfig, "URI path extension config constraint must not be null");
		if (extensionConfig.isEmpty()) {
			return;
		}
		
		Optional<String> fileName = getUriPathFileName(value);
		if (fileName.isEmpty()) {
			throw new ConstraintViolateException("URI path '" + value + "' has no file name for extension check");
		}
		
		Optional<String> extension = getUriPathExtension(fileName.get());
		if (extension.isEmpty()) {
			throw new ConstraintViolateException("URI path '" + value + "' has no extension");
		}
		
		try {
			extensionConfig.get().validate(extension.get());
		} catch (ConstraintViolateException e) {
			throw new ConstraintViolateException("Extension constraint failed: " + e.getMessage(), e);
		}
	}
	
	/**
	 * Gets the extension of a file name.<br>
	 *
	 * @param fileName The file name
	 * @return The extension without the dot, or empty if no extension
	 * @throws NullPointerException If the file name is null
	 */
	private static @NonNull Optional<String> getUriPathExtension(@NonNull String fileName) {
		Objects.requireNonNull(fileName, "File name must not be null");
		
		int dotIndex = fileName.lastIndexOf('.');
		if (dotIndex < 0 || dotIndex == fileName.length() - 1) {
			return Optional.empty();
		}
		return Optional.of(fileName.substring(dotIndex + 1));
	}
	
	/**
	 * Validates that a URI path is an ancestor of all specified paths.<br>
	 *
	 * @param value The path to validate
	 * @param ancestorOf The set of paths that the value must be an ancestor of
	 * @throws NullPointerException If any parameter is null
	 * @throws ConstraintViolateException If the constraint is not satisfied
	 */
	public static void validateUriPathAncestorOf(@NonNull String value, @NonNull Optional<Set<String>> ancestorOf) {
		Objects.requireNonNull(value, "URI path must not be null");
		Objects.requireNonNull(ancestorOf, "URI path ancestor of constraint must not be null");
		if (ancestorOf.isEmpty()) {
			return;
		}
		
		String normalizedValue = normalizeUriPath(value);
		for (String pathStr : ancestorOf.get()) {
			String normalizedTarget = normalizeUriPath(pathStr);
			if (!normalizedTarget.startsWith(normalizedValue)) {
				throw new ConstraintViolateException("URI path '" + value + "' must be ancestor of '" + pathStr + "'");
			}
		}
	}
	
	/**
	 * Validates that a URI path is a descendant of all specified paths.<br>
	 *
	 * @param value The path to validate
	 * @param descendantOf The set of paths that the value must be a descendant of
	 * @throws NullPointerException If any parameter is null
	 * @throws ConstraintViolateException If the constraint is not satisfied
	 */
	public static void validateUriPathDescendantOf(@NonNull String value, @NonNull Optional<Set<String>> descendantOf) {
		Objects.requireNonNull(value, "URI path must not be null");
		Objects.requireNonNull(descendantOf, "URI path descendant of constraint must not be null");
		if (descendantOf.isEmpty()) {
			return;
		}
		
		String normalizedValue = normalizeUriPath(value);
		for (String pathStr : descendantOf.get()) {
			String normalizedTarget = normalizeUriPath(pathStr);
			if (!normalizedValue.startsWith(normalizedTarget)) {
				throw new ConstraintViolateException("URI path '" + value + "' must be descendant of '" + pathStr + "'");
			}
		}
	}
	
	/**
	 * Validates an InetAddress against an IP version constraint.<br>
	 *
	 * @param value The InetAddress to validate
	 * @param ipVersion The IP version enum constraint config
	 * @throws NullPointerException If any parameter is null
	 * @throws ConstraintViolateException If the constraint is not satisfied
	 */
	public static void validateInetAddressIpVersion(@NonNull InetAddress value, @NonNull Optional<EnumConstraintConfig<IpVersion>> ipVersion) {
		Objects.requireNonNull(value, "InetAddress must not be null");
		Objects.requireNonNull(ipVersion, "IP version constraint must not be null");
		if (ipVersion.isEmpty()) {
			return;
		}
		
		validateIpVersion(value.getHostAddress(), ipVersion);
	}
	
	/**
	 * Validates an InetAddress against an IP address type constraint.<br>
	 *
	 * @param value The InetAddress to validate
	 * @param ipType The IP address type enum constraint config
	 * @throws NullPointerException If any parameter is null
	 * @throws ConstraintViolateException If the constraint is not satisfied
	 */
	public static void validateInetAddressIpType(@NonNull InetAddress value, @NonNull Optional<EnumConstraintConfig<IpAddressType>> ipType) {
		Objects.requireNonNull(value, "InetAddress must not be null");
		Objects.requireNonNull(ipType, "IP type constraint must not be null");
		if (ipType.isEmpty()) {
			return;
		}
		
		validateIpType(value.getHostAddress(), ipType);
	}
	
	/**
	 * Validates an InetAddress against a subnet membership constraint.<br>
	 *
	 * @param value The InetAddress to validate
	 * @param inAnySubnet The subnet constraint as a pair of (CIDRs, negated)
	 * @throws NullPointerException If any parameter is null
	 * @throws ConstraintViolateException If the constraint is not satisfied
	 */
	public static void validateInetAddressInAnySubnet(@NonNull InetAddress value, @NonNull Optional<Pair<Set<String>, Boolean>> inAnySubnet) {
		Objects.requireNonNull(value, "InetAddress must not be null");
		Objects.requireNonNull(inAnySubnet, "In any subnet constraint must not be null");
		if (inAnySubnet.isEmpty()) {
			return;
		}
		
		validateInAnySubnet(value.getHostAddress(), inAnySubnet);
	}
	
	/**
	 * Validates an InetSocketAddress's address component against an InetAddressConstraintConfig.<br>
	 *
	 * @param value The InetSocketAddress to validate
	 * @param addressConfig The InetAddressConstraintConfig for address validation
	 * @throws NullPointerException If any parameter is null
	 * @throws ConstraintViolateException If the constraint is not satisfied
	 */
	public static void validateInetSocketAddressAddress(@NonNull InetSocketAddress value, @NonNull Optional<InetAddressConstraintConfig> addressConfig) {
		Objects.requireNonNull(value, "InetSocketAddress must not be null");
		Objects.requireNonNull(addressConfig, "Address config constraint must not be null");
		if (addressConfig.isEmpty()) {
			return;
		}
		
		InetAddress address = value.getAddress();
		if (address == null) {
			throw new ConstraintViolateException("Internet socker address '" + value + "' has no resolved address");
		}
		
		try {
			addressConfig.get().validate(address);
		} catch (ConstraintViolateException e) {
			throw new ConstraintViolateException("Address constraint failed: " + e.getMessage(), e);
		}
	}
	
	/**
	 * Validates an InetSocketAddress's port component against a PortConstraintConfig.<br>
	 *
	 * @param value The InetSocketAddress to validate
	 * @param portConfig The PortConstraintConfig for port validation
	 * @throws NullPointerException If any parameter is null
	 * @throws ConstraintViolateException If the constraint is not satisfied
	 */
	public static void validateInetSocketAddressPort(@NonNull InetSocketAddress value, @NonNull Optional<PortConstraintConfig> portConfig) {
		Objects.requireNonNull(value, "InetSocketAddress must not be null");
		Objects.requireNonNull(portConfig, "Port config constraint must not be null");
		if (portConfig.isEmpty()) {
			return;
		}
		
		try {
			portConfig.get().validate(value.getPort());
		} catch (ConstraintViolateException e) {
			throw new ConstraintViolateException("Port constraint failed: " + e.getMessage(), e);
		}
	}
	
	/**
	 * Validates an IpNetwork against an IP version constraint.<br>
	 *
	 * @param value The IpNetwork to validate
	 * @param ipVersion The enum constraint config for IP version
	 * @throws NullPointerException If any parameter is null
	 * @throws ConstraintViolateException If the constraint is not satisfied
	 */
	public static void validateIpNetworkIpVersion(@NonNull IpNetwork<?, ?> value, @NonNull Optional<EnumConstraintConfig<IpVersion>> ipVersion) {
		Objects.requireNonNull(value, "IpNetwork must not be null");
		Objects.requireNonNull(ipVersion, "IP version constraint must not be null");
		if (ipVersion.isEmpty()) {
			return;
		}
		
		IpVersion version = switch (value.networkAddress()) {
			case Ipv4Address _ -> IpVersion.IPV4;
			case Ipv6Address _ -> IpVersion.IPV6;
			default -> null;
		};
		if (version == null) {
			throw new ConstraintViolateException("Unable to determine IP version for network: " + value);
		}
		
		try {
			ipVersion.get().validate(version);
		} catch (ConstraintViolateException e) {
			throw new ConstraintViolateException("IP version constraint failed: " + e.getMessage(), e);
		}
	}
	
	/**
	 * Validates a MacAddress against a broadcast constraint.<br>
	 *
	 * @param value The MacAddress to validate
	 * @param broadcast The broadcast constraint as a pair of (Unit, negated)
	 * @throws NullPointerException If any parameter is null
	 * @throws ConstraintViolateException If the constraint is not satisfied
	 */
	public static void validateMacAddressBroadcast(@NonNull MacAddress value, @NonNull Optional<Pair<Unit, Boolean>> broadcast) {
		Objects.requireNonNull(value, "MacAddress must not be null");
		Objects.requireNonNull(broadcast, "Broadcast constraint must not be null");
		if (broadcast.isEmpty()) {
			return;
		}
		
		boolean isBroadcast = value.isBroadcast();
		boolean negated = broadcast.get().getSecond();
		
		if (negated && isBroadcast) {
			throw new ConstraintViolateException("MAC address must not be broadcast, but was: " + value);
		} else if (!negated && !isBroadcast) {
			throw new ConstraintViolateException("MAC address must be broadcast, but was: " + value);
		}
	}
}
