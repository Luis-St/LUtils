/*
 * LUtils
 * Copyright (C) 2024 Luis Staudt
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

package net.luis.utils.util;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class represents a version number.<br>
 * The version number is represented by the following simplified pattern:<br>
 * <pre>{@code
 * [v]major.minor[.patch][(r.-)build][(-)suffix][(+)suffixVersion]
 * }</pre>
 * The version is composed of the following parts:
 * <ul>
 *     <li>major: The major version number</li>
 *     <li>minor: The minor version number</li>
 *     <li>patch: The patch or fix version number</li>
 *     <li>build: The current build number</li>
 *     <li>suffix: The suffix or pre-release identifier (e.g. alpha, beta, rc, ...)</li>
 *     <li>suffixVersion: The version of the suffix or pre-release identifier</li>
 * </ul>
 * <p>
 *     Everything placed in square brackets is optional.<br>
 *     Characters in parentheses are separators, if there are multiple separators, one of them can be used.<br>
 * </p>
 *
 * @author Luis-St
 */
public class Version implements Comparable<Version> {
	
	/**
	 * The pattern used to parse a version number.<br>
	 * <ul>
	 *     <li>Group 1: The major version number</li>
	 *     <li>Group 2: The minor version number</li>
	 *     <li>Group 3: The patch or fix version number</li>
	 *     <li>Group 6: The build number</li>
	 *     <li>Group 8: The suffix or pre-release identifier</li>
	 *     <li>Group 10: The version of the suffix or pre-release identifier</li>
	 * </ul>
	 * The pattern is case-sensitive and allows the version number to be prefixed with a 'v'.<br>
	 * <p>
	 *     The major and minor version numbers are required.<br>
	 *     The patch version is optional.<br>
	 *     They must be separated by a dot.<br>
	 * </p>
	 * <p>
	 *     The build number is optional.<br>
	 *     It must be separated by a dot, a hyphen, or an 'r' (release).<br>
	 * </p>
	 * <p>
	 *     The suffix and suffix-version are optional.<br>
	 *     If the suffix is present, it must be separated by a hyphen.<br>
	 *     If the suffix-version is present, it must be separated by a plus sign.<br>
	 * </p>
	 */
	private static final Pattern VERSION_PATTERN = Pattern.compile("^v?(\\d+)\\.(\\d+)(\\.(\\d+))?(([.r-])(\\d+))?(-([a-z]+))?(\\+(\\d{3}))?$");
	/**
	 * Constant for the system property 'version.default.prefixed'.<br>
	 * <p>
	 *     If the system property is set to 'true', the version number will be prefixed with a 'v'<br>
	 *     when it is converted to a string via {@link #toString()}.<br>
	 *     The system property does not affect {@link #toString(boolean)}.<br>
	 * </p>
	 * <p>
	 *     The default value is 'true'.<br>
	 * </p>
	 */
	private static final String VERSION_DEFAULT_PREFIXED = "version.default.prefixed";
	
	/**
	 * A constant value for a version number of 0.0.0.<br>
	 * This is the default version number.<br>
	 */
	public static final Version ZERO = new Version(0, 0, 0, AppendingVersion.EMPTY, "", AppendingVersion.EMPTY);
	
	/**
	 * The major version number.<br>
	 */
	private final int major;
	/**
	 * The minor version number.<br>
	 */
	private final int minor;
	/**
	 * The patch or fix version number.<br>
	 */
	private final int patch;
	/**
	 * The appendable build version number.<br>
	 */
	private final AppendingVersion buildVersion;
	/**
	 * The suffix or pre-release identifier.<br>
	 */
	private final String suffix;
	/**
	 * The appendable suffix version number.<br>
	 */
	private final AppendingVersion suffixVersion;
	
	/**
	 * Constructs a new version with the given version numbers.<br>
	 * The version numbers will be clamped to a minimum of 0.<br>
	 * @param major The major version number
	 * @param minor The minor version number
	 * @param patch The patch or fix version number
	 * @param buildVersion The appendable build version number, containing the separator and the version number
	 * @param suffix The suffix or pre-release identifier
	 * @param suffixVersion The appendable suffix version number, containing the separator and the version number
	 * @throws NullPointerException If the build or suffix version is null
	 */
	private Version(int major, int minor, int patch, @NotNull AppendingVersion buildVersion, @Nullable String suffix, @NotNull AppendingVersion suffixVersion) {
		this.major = Math.max(0, major);
		this.minor = Math.max(0, minor);
		this.patch = Math.max(0, patch);
		this.buildVersion = Objects.requireNonNull(buildVersion, "Build version must not be null");
		this.suffix = StringUtils.strip(StringUtils.stripToEmpty(suffix), "-");
		this.suffixVersion = Objects.requireNonNull(suffixVersion, "Suffix version must not be null");
	}
	
	/**
	 * Creates a new version with the given version numbers.<br>
	 * The version numbers will be clamped to a minimum of 0.<br>
	 * <p>
	 *     If the version numbers are all 0 or less, {@link #ZERO} will be returned.<br>
	 * </p>
	 * @param major The major version number
	 * @param minor The minor version number
	 * @return The created version
	 * @see Version.Builder
	 */
	public static @NotNull Version of(int major, int minor) {
		if (0 >= major && 0 >= minor) {
			return ZERO;
		}
		return new Version.Builder(major, minor).build();
	}
	
	/**
	 * Creates a new version with the given version numbers.<br>
	 * The version numbers will be clamped to a minimum of 0.<br>
	 * <p>
	 *     If the version numbers are all 0 or less, {@link #ZERO} will be returned.<br>
	 * </p>
	 * @param major The major version number
	 * @param minor The minor version number
	 * @param patch The patch or fix version number
	 * @return The created version
	 * @see Version.Builder
	 */
	public static @NotNull Version of(int major, int minor, int patch) {
		if (0 >= major && 0 >= minor && 0 >= patch) {
			return ZERO;
		}
		return new Version.Builder(major, minor, patch).build();
	}
	
	/**
	 * Creates a new version builder with the given version numbers.<br>
	 * The version numbers will be clamped to a minimum of 0.<br>
	 * @param major The major version number
	 * @param minor The minor version number
	 * @return The created version builder
	 * @see Version.Builder
	 */
	public static Version.@NotNull Builder builder(int major, int minor) {
		return new Version.Builder(major, minor);
	}
	
	/**
	 * Creates a new version builder with the given version numbers.<br>
	 * The version numbers will be clamped to a minimum of 0.<br>
	 * @param major The major version number
	 * @param minor The minor version number
	 * @param patch The patch or fix version number
	 * @return The created version builder
	 * @see Version.Builder
	 */
	public static Version.@NotNull Builder builder(int major, int minor, int patch) {
		return new Version.Builder(major, minor, patch);
	}
	
	/**
	 * Parses a version from a string.<br>
	 * <p>
	 *     If the given string to parse is null, empty, blank,<br>
	 *     or does not match the pattern ({@link #VERSION_PATTERN}), {@link #ZERO} will be returned.<br>
	 * </p>
	 * <p>
	 *     The following examples are valid base version strings:<br>
	 * </p>
	 * <ul>
	 *     <li>1.0</li>
	 *     <li>v1.0</li>
	 *     <li>1.0.0</li>
	 *     <li>v1.0.0</li>
	 * </ul>
	 * <p>
	 *     The major and minor version numbers are required and must be separated by a dot.<br>
	 *     The patch version is optional and must be separated by a dot.<br>
	 *     The build number is optional and can be separated by a dot, a hyphen, or an 'r' (release).<br>
	 *     Examples for valid build version strings are:<br>
	 * </p>
	 * <ul>
	 *     <li>1.0.0.0</li>
	 *     <li>1.0.0-0</li>
	 *     <li>1.0.0r0</li>
	 * </ul>
	 * <p>
	 *     <strong>Note</strong>: If the patch version is not present, the build number must not be separated by a dot<br>
	 *     otherwise it will be considered as the patch version.<br>
	 * </p>
	 * <p>
	 *     The suffix and suffix-version are optional.<br>
	 *     If the suffix is present, it must be separated by a hyphen.<br>
	 *     If the suffix-version is present, it must be separated by a plus sign.<br>
	 *     Examples for valid suffix and suffix-version strings are:<br>
	 * </p>
	 * <ul>
	 *     <li>1.0.0-alpha</li>
	 *     <li>1.0.0+001</li>
	 *     <li>1.0.0-alpha+001</li>
	 *     <li>1.0.0-0+001</li>
	 *     <li>1.0.0r0-alpha+001</li>
	 * </ul>
	 * @param version The version string to parse
	 * @return The parsed version or {@link #ZERO}
	 */
	public static @NotNull Version parse(@Nullable String version) {
		if (StringUtils.isBlank(version)) {
			return ZERO;
		}
		Matcher matcher = VERSION_PATTERN.matcher(version);
		if (!matcher.matches()) {
			return ZERO;
		}
		int major = Integer.parseInt(matcher.group(1));
		int minor = Integer.parseInt(matcher.group(2));
		int patch = 0;
		if (matcher.group(4) != null) {
			patch = Integer.parseInt(matcher.group(4));
		}
		Version.Builder builder = builder(major, minor, patch);
		if (matcher.group(11) != null) {
			builder = builder.withSuffixVersion(Integer.parseInt(matcher.group(11)));
		}
		if (StringUtils.isEmpty(matcher.group(5)) && StringUtils.isEmpty(matcher.group(9))) {
			return builder.build();
		}
		if (matcher.group(5) != null) {
			int build = Integer.parseInt(matcher.group(7));
			char separator = matcher.group(6).charAt(0);
			if (matcher.group(9) != null) {
				return builder.withBuild(separator, build).withSuffix(matcher.group(9)).build();
			}
			return builder.withBuild(separator, build).build();
		}
		if (matcher.group(9) != null) {
			return builder.withSuffix(matcher.group(9)).build();
		}
		return ZERO;
	}
	
	/**
	 * Returns the major version number as an integer.<br>
	 * @return The major version
	 */
	public int getMajor() {
		return this.major;
	}
	
	/**
	 * Returns the minor version number as an integer.<br>
	 * @return The minor version
	 */
	public int getMinor() {
		return this.minor;
	}
	
	/**
	 * Returns the patch or fix version number as an integer.<br>
	 * @return The patch or fix version or {@code 0} if not present
	 */
	public int getPatch() {
		return this.patch;
	}
	
	/**
	 * Returns the build version number as an integer.<br>
	 * @return The build version or {@code 0} if not present
	 */
	public int getBuild() {
		return Math.max(0, this.buildVersion.version());
	}
	
	/**
	 * Returns the suffix or pre-release identifier.<br>
	 * @return The suffix or pre-release identifier or an empty string if not present
	 */
	public @NotNull String getSuffix() {
		return this.suffix;
	}
	
	/**
	 * Gets the index of the suffix.<br>
	 * The index is used to compare the suffix with other suffixes.<br>
	 * The index is as follows:<br>
	 * <ul>
	 *     <li>0: No or unknown suffix</li>
	 *     <li>1: Alpha</li>
	 *     <li>2: Beta</li>
	 *     <li>3: Release candidate</li>
	 *     <li>4: Release</li>
	 *     <li>5: Final</li>
	 * </ul>
	 * @return The index of the suffix
	 */
	public int getSuffixIndex() {
		return switch (this.suffix) {
			case "alpha" -> 1;
			case "beta" -> 2;
			case "rc", "release-candidate" -> 3;
			case "release" -> 4;
			case "final" -> 5;
			case null, default -> 0;
		};
	}
	
	/**
	 * Returns the suffix version number as an integer.<br>
	 * @return The suffix version or {@code 0} if not present
	 */
	public int getSuffixVersion() {
		return Math.max(0, this.suffixVersion.version());
	}
	
	/**
	 * Returns the version as a builder for modifications.<br>
	 * @return The version as a builder
	 */
	public Version.@NotNull Builder asBuilder() {
		Version.Builder builder = new Version.Builder(this.major, this.minor, this.patch).withSuffix(this.suffix);
		if (this.suffixVersion.isNotEmpty()) {
			builder = builder.withSuffixVersion(this.suffixVersion.version());
		}
		if (this.buildVersion.isNotEmpty()) {
			builder = builder.withBuild(this.buildVersion.separator, this.buildVersion.version);
		}
		return builder;
	}
	
	/**
	 * Compares this version with the given version.<br>
	 * The comparison is based on the following order:<br>
	 * <ol>
	 *     <li>Major version number</li>
	 *     <li>Minor version number</li>
	 *     <li>Patch or fix version number</li>
	 *     <li>Build version number</li>
	 * </ol>
	 * <p>
	 *     If the suffixes are equal, the suffix version will be compared.<br>
	 *     If the suffixes are not equal, the suffixes will be compared<br>
	 *     in this case, the suffix version will not be considered.<br>
	 * </p>
	 * <p>
	 *     The suffixes are compared based on the following order:<br>
	 * </p>
	 * <ol>
	 *     <li>Alpha</li>
	 *     <li>Beta</li>
	 *     <li>Release candidate</li>
	 *     <li>Release</li>
	 *     <li>Final</li>
	 * </ol>
	 * <p>
	 *     The first comparison that is not equal will determine the result.<br>
	 * </p>
	 * @param o The version to compare with
	 * @return The comparison result, negative if this version is less than the given version,<br>
	 * positive if this version is greater than the given version, and zero if both versions are equal
	 */
	@Override
	public int compareTo(@NotNull Version o) {
		int major = Integer.compare(this.major, o.major);
		if (major != 0) {
			return major;
		}
		int minor = Integer.compare(this.minor, o.minor);
		if (minor != 0) {
			return minor;
		}
		int patch = Integer.compare(this.patch, o.patch);
		if (patch != 0) {
			return patch;
		}
		int build = this.buildVersion.compareTo(o.buildVersion);
		if (build != 0) {
			return build;
		}
		if (StringUtils.equals(this.suffix, o.suffix)) {
			return Integer.compare(this.getSuffixVersion(), o.getSuffixVersion());
		}
		if (StringUtils.isEmpty(this.suffix)) {
			return 1;
		}
		if (StringUtils.isEmpty(o.suffix)) {
			return -1;
		}
		return Integer.compare(this.getSuffixIndex(), o.getSuffixIndex());
	}
	
	//region Object overrides
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Version version)) return false;
		
		if (this.major != version.major) return false;
		if (this.minor != version.minor) return false;
		if (this.patch != version.patch) return false;
		if (!this.buildVersion.equals(version.buildVersion)) return false;
		if (!this.suffix.equals(version.suffix)) return false;
		return this.suffixVersion.equals(version.suffixVersion);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.major, this.minor, this.patch, this.buildVersion, this.suffix, this.suffixVersion);
	}
	
	@Override
	public String toString() {
		return this.toString(Boolean.parseBoolean(System.getProperty(VERSION_DEFAULT_PREFIXED, "true")));
	}
	
	/**
	 * Returns the version as a string.<br>
	 * The version will be formatted as follows:<br>
	 * <pre>{@code
	 * <major>.<minor>.<patch>[(r.-)<build>][-<suffix>][+<suffixVersion>]
	 * }</pre>
	 * If the patch version is 0, it will be omitted.<br>
	 * @param prefixed If the version should be prefixed with a 'v'
	 * @return The version as a string
	 * @see #toString(boolean, boolean)
	 */
	public @NotNull String toString(boolean prefixed) {
		return this.toString(prefixed, true);
	}
	
	/**
	 * Returns the version as a string.<br>
	 * The version will be formatted as follows:<br>
	 * <pre>{@code
	 * <major>.<minor>.<patch>[(r.-)<build>][-<suffix>][+<suffixVersion>]
	 * }</pre>
	 * If the patch version is 0 and {@code omitZeros} is true, it will be omitted.<br>
	 * In this case, the version will be formatted as follows:<br>
	 * <pre>{@code
	 * <major>.<minor>[(r.-)<build>][-<suffix>][+<suffixVersion>]
	 * }</pre>
	 * @param prefixed If the version should be prefixed with a 'v'
	 * @param omitZeros If the patch version should be omitted if it is 0
	 * @return The version as a string
	 */
	public @NotNull String toString(boolean prefixed, boolean omitZeros) {
		StringBuilder builder = new StringBuilder(prefixed ? "v" : "");
		builder.append(this.major).append('.').append(this.minor);
		if (!omitZeros || this.patch != 0) {
			builder.append('.').append(this.patch);
		}
		if (this.buildVersion.isNotEmpty()) {
			builder.append(this.buildVersion);
		}
		if (!this.suffix.isEmpty()) {
			builder.append('-').append(this.suffix);
		}
		if (this.suffixVersion.isNotEmpty()) {
			String version = String.valueOf(this.suffixVersion.version());
			builder.append(this.suffixVersion.separator()).append("0".repeat(3 - version.length())).append(version);
		}
		return builder.toString();
	}
	//endregion
	
	//region Builder
	
	/**
	 * This class represents a builder for a version number.<br>
	 *
	 * @author Luis-St
	 */
	public static class Builder {
		
		/**
		 * The major version number.<br>
		 */
		private int major;
		/**
		 * The minor version number.<br>
		 */
		private int minor;
		/**
		 * The patch or fix version number.<br>
		 */
		private int patch;
		/**
		 * The appendable build version number.<br>
		 */
		private AppendingVersion build = AppendingVersion.EMPTY;
		/**
		 * The suffix or pre-release identifier.<br>
		 */
		private String suffix = "";
		/**
		 * The appendable suffix version number.<br>
		 */
		private AppendingVersion suffixVersion = AppendingVersion.EMPTY;
		
		/**
		 * Constructs a new version builder with the given version numbers.<br>
		 * The version numbers will be clamped to a minimum of 0.<br>
		 * @param major The major version number
		 * @param minor The minor version number
		 */
		private Builder(int major, int minor) {
			this.major = Math.max(0, major);
			this.minor = Math.max(0, minor);
		}
		
		/**
		 * Constructs a new version builder with the given version numbers.<br>
		 * The version numbers will be clamped to a minimum of 0.<br>
		 * @param major The major version number
		 * @param minor The minor version number
		 * @param patch The patch or fix version number
		 */
		private Builder(int major, int minor, int patch) {
			this.major = Math.max(0, major);
			this.minor = Math.max(0, minor);
			this.patch = Math.max(0, patch);
		}
		
		/**
		 * Sets the major version number.<br>
		 * The major version number will be clamped to a minimum of 0.<br>
		 * @param major The major version number
		 * @return This builder
		 */
		public @NotNull Builder withMajor(int major) {
			this.major = Math.max(0, major);
			return this;
		}
		
		/**
		 * Sets the minor version number.<br>
		 * The minor version number will be clamped to a minimum of 0.<br>
		 * @param minor The minor version number
		 * @return This builder
		 */
		public @NotNull Builder withMinor(int minor) {
			this.minor = Math.max(0, minor);
			return this;
		}
		
		/**
		 * Sets the patch or fix version number.<br>
		 * The patch or fix version number will be clamped to a minimum of 0.<br>
		 * @param patch The patch or fix version number
		 * @return This builder
		 */
		public @NotNull Builder withPatch(int patch) {
			this.patch = Math.max(0, patch);
			return this;
		}
		
		/**
		 * Sets the build version number and the separator.<br>
		 * The build version number will be clamped to a minimum of 0.<br>
		 * @param separator The separator character (must be '.', '-', or 'r')
		 * @param version The build version number
		 * @return This builder
		 * @throws IllegalArgumentException If the separator is not '.', '-', or 'r'
		 */
		public @NotNull Builder withBuild(char separator, int version) {
			if (separator != '.' && separator != '-' && separator != 'r') {
				throw new IllegalArgumentException("Invalid build version separator: '" + separator + "' (expected '.', '-', or 'r')");
			}
			this.build = new AppendingVersion(separator, Math.max(0, version));
			return this;
		}
		
		/**
		 * Sets the suffix or pre-release identifier.<br>
		 * Removes leading and trailing whitespace from the suffix.<br>
		 * Recommended suffixes are 'alpha', 'beta', 'rc', 'release-candidate', 'release', and 'final'.<br>
		 * @param suffix The suffix or pre-release identifier
		 * @return This builder
		 */
		public @NotNull Builder withSuffix(@Nullable String suffix) {
			this.suffix = StringUtils.stripToEmpty(suffix);
			return this;
		}
		
		/**
		 * Sets the suffix version number.<br>
		 * The suffix version number will be clamped to a minimum of 0.<br>
		 * @param version The suffix version number
		 * @return This builder
		 */
		public @NotNull Builder withSuffixVersion(int version) {
			this.suffixVersion = new AppendingVersion('+', Math.max(0, version));
			return this;
		}
		
		/**
		 * Builds the version with the given version numbers.<br>
		 * @return The created version
		 */
		public @NotNull Version build() {
			return new Version(this.major, this.minor, this.patch, this.build, this.suffix, this.suffixVersion);
		}
		
		//region Object overrides
		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (!(o instanceof Builder builder)) return false;
			
			if (this.major != builder.major) return false;
			if (this.minor != builder.minor) return false;
			if (this.patch != builder.patch) return false;
			if (!this.build.equals(builder.build)) return false;
			if (!this.suffix.equals(builder.suffix)) return false;
			return this.suffixVersion.equals(builder.suffixVersion);
		}
		
		@Override
		@SuppressWarnings("NonFinalFieldReferencedInHashCode")
		public int hashCode() {
			return Objects.hash(this.major, this.minor, this.patch, this.build, this.suffix, this.suffixVersion);
		}
		//endregion
	}
	//endregion
	
	//region Internal
	
	/**
	 * This record represents an appendable version number.<br>
	 * The record is used for the build and suffix version numbers.<br>
	 *
	 * @author Luis-St
	 *
	 * @param separator The separator character
	 * @param version The version number
	 */
	private record AppendingVersion(char separator, int version) implements Comparable<AppendingVersion> {
		
		/**
		 * An empty appendable version number.<br>
		 * The separator is {@code '\0'} and the version is {@code -1}.<br>
		 * This is the default value for an empty version number.<br>
		 */
		private static final AppendingVersion EMPTY = new AppendingVersion('\0', -1);
		
		/**
		 * Checks if the version number is not empty.<br>
		 * The version number is not empty if the separator is not {@code '\0'} and the version is not {@code -1}.<br>
		 * @return True if the version number is not empty, otherwise false
		 */
		public boolean isNotEmpty() {
			return this.separator != '\0' && this.version != -1;
		}
		
		/**
		 * Compares this appendable version number with the given appendable version number.<br>
		 * The comparison is based on the version number, the separator is not considered.<br>
		 * @param o The appendable version to compare with
		 * @return The comparison result
		 */
		@Override
		public int compareTo(@NotNull Version.AppendingVersion o) {
			if (this.version == -1 && o.version == -1) {
				return 0;
			}
			if (this.version == -1) {
				return -1;
			}
			if (o.version == -1) {
				return 1;
			}
			return Integer.compare(this.version, o.version);
		}
		
		//region Object overrides
		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (!(o instanceof AppendingVersion that)) return false;
			
			if (this.separator != that.separator) return false;
			return this.version == that.version;
		}
		
		@Override
		public int hashCode() {
			return Objects.hash(this.separator, this.version);
		}
		
		@Override
		public String toString() {
			return this.separator + String.valueOf(this.version);
		}
		//endregion
	}
	//endregion
}
