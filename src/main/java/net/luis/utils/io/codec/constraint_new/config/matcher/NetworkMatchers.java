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

package net.luis.utils.io.codec.constraint_new.config.matcher;

import net.luis.utils.io.codec.constraint_new.config.*;
import net.luis.utils.io.codec.constraint_new.config.network.*;
import net.luis.utils.io.codec.constraint_new.core.*;
import net.luis.utils.util.Pair;
import net.luis.utils.util.result.Result;
import org.jspecify.annotations.NonNull;

import java.io.File;
import java.io.IOException;
import java.net.URI;
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
public final class NetworkMatchers {
	
	/**
	 * Private constructor to prevent instantiation.<br>
	 */
	private NetworkMatchers() {}
	
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
		
		// ToDo: Implement IP address type detection and matching
		return Result.success();
	}
	
	/**
	 * Validates a host value against a subnet membership constraint.<br>
	 * <p>
	 *     Note: This is a placeholder implementation that always succeeds.
	 *     A full implementation would parse CIDR notation and check if the IP is within the subnet.
	 * </p>
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
		
		// ToDo: Implement subnet membership check based on CIDR notation
		return Result.success();
	}
	
	/**
	 * Validates a host value against a domain string constraint config.<br>
	 *
	 * @param value The host value to validate
	 * @param domain The domain string constraint config
	 * @return A successful result if the constraint is satisfied or not present
	 * @throws NullPointerException If any parameter is null
	 */
	public static @NonNull Result<Void> matchDomainConfig(@NonNull String value, @NonNull Optional<StringConstraintConfig> domain) {
		Objects.requireNonNull(value, "Domain must not be null");
		Objects.requireNonNull(domain, "Domain constraint must not be null");
		if (domain.isEmpty()) {
			return Result.success();
		}
		
		Result<Void> result = domain.get().matches(value);
		if (result.isError()) {
			return Result.error("Domain constraint failed: " + result.errorOrThrow());
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
	 * Validates a URI's path against a path constraint config.<br>
	 *
	 * @param value The URI to validate
	 * @param pathConfig The path constraint config for path validation
	 * @return A successful result if the constraint is satisfied or not present
	 * @throws NullPointerException If any parameter is null
	 */
	public static @NonNull Result<Void> matchUriPathConfig(@NonNull URI value, @NonNull Optional<PathConstraintConfig> pathConfig) {
		Objects.requireNonNull(value, "Uri must not be null");
		Objects.requireNonNull(pathConfig, "Uri path config constraint must not be null");
		if (pathConfig.isEmpty()) {
			return Result.success();
		}
		
		String pathStr = value.getPath();
		if (pathStr == null || pathStr.isEmpty()) {
			return Result.error("URI '" + value + "' has no path");
		}
		
		Result<Void> result = pathConfig.get().matches(Path.of(pathStr));
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
}
