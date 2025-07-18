/*
 * LUtils
 * Copyright (C) 2025 Luis Staudt
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

package net.luis.utils.io.data.xml;

import net.luis.utils.util.Version;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Represents an xml declaration.<br>
 *
 * @author Luis-St
 *
 * @param version The version of the xml document
 * @param encoding The encoding of the xml document
 * @param standalone Whether the xml document is standalone or not
 */
public record XmlDeclaration(@NotNull Version version, @NotNull Charset encoding, boolean standalone) {
	
	/**
	 * Constructs a new xml declaration with the given version, encoding and standalone flag.<br>
	 *
	 * @param version The version of the xml document
	 * @param encoding The encoding of the xml document
	 * @param standalone Whether the xml document is standalone or not
	 * @throws NullPointerException If the version or encoding is null
	 * @throws IllegalArgumentException If the version is invalid
	 */
	public XmlDeclaration {
		Objects.requireNonNull(version, "Version must not be null");
		Objects.requireNonNull(encoding, "Charset must not be null");
		if (version.getMajor() <= 0) {
			throw new IllegalArgumentException("Major version must be greater than 0, but was " + version.getMajor());
		}
		if (version.getMinor() >= 10) {
			throw new IllegalArgumentException("Minor version must be less than 10, but was " + version.getMinor());
		}
		if (version.getMinor() < 0) {
			throw new IllegalArgumentException("Minor version must not be negative, but was " + version.getMinor());
		}
		if (version.getPatch() != 0) {
			throw new IllegalArgumentException("Version must not have a patch number, but found " + version.getPatch());
		}
		if (version.getBuild() != 0) {
			throw new IllegalArgumentException("Version must not have a build number, but found " + version.getBuild());
		}
		if (!version.getSuffix().isEmpty()) {
			throw new IllegalArgumentException("Version must not have a suffix, but found " + version.getSuffix());
		}
		if (version.getSuffixVersion() != 0) {
			throw new IllegalArgumentException("Version must not have a suffix version, but found " + version.getSuffixVersion());
		}
	}
	
	/**
	 * Constructs a new xml declaration with the given version and encoding.<br>
	 * The encoding and standalone flag are set to UTF-8 and false respectively.<br>
	 *
	 * @param version The version of the xml document
	 * @throws NullPointerException If the version is null
	 * @throws IllegalArgumentException If the version is invalid
	 */
	public XmlDeclaration(@NotNull Version version) {
		this(version, StandardCharsets.UTF_8, false);
	}
	
	/**
	 * Constructs a new xml declaration with the given version and encoding.<br>
	 * The standalone flag is set to false.<br>
	 *
	 * @param version The version of the xml document
	 * @param encoding The encoding of the xml document
	 * @throws NullPointerException If the version or encoding is null
	 * @throws IllegalArgumentException If the version is invalid
	 */
	public XmlDeclaration(@NotNull Version version, @NotNull Charset encoding) {
		this(version, encoding, false);
	}
	
	//region Object overrides
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof XmlDeclaration that)) return false;
		
		if (this.standalone != that.standalone) return false;
		if (!this.version.equals(that.version)) return false;
		return this.encoding.equals(that.encoding);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.version, this.encoding, this.standalone);
	}
	
	@Override
	public String toString() {
		return "<?xml version=\"" + this.version.toString().substring(1) + "\" encoding=\"" + this.encoding.name() + "\" standalone=\"" + this.standalone + "\"?>";
	}
	//endregion
}
