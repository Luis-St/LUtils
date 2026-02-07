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
import net.luis.utils.util.Pair;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.*;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link IOValidators}.<br>
 *
 * @author Luis-St
 */
class IOValidatorsTest {
	
	@Test
	void validatePortRangeWithEmptyOptional() {
		assertDoesNotThrow(() -> IOValidators.validatePortRange(8080, Optional.empty()));
	}
	
	@Test
	void validatePortRangeWithInRange() {
		assertDoesNotThrow(() -> IOValidators.validatePortRange(8080, Optional.of(Pair.of(Pair.of(8000, 9000), false))));
	}
	
	@Test
	void validatePortRangeWithOutOfRange() {
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> IOValidators.validatePortRange(7000, Optional.of(Pair.of(Pair.of(8000, 9000), false))));
		assertTrue(exception.getMessage().contains("must be in range"));
	}
	
	@Test
	void validatePortRangeWithNegated() {
		assertDoesNotThrow(() -> IOValidators.validatePortRange(7000, Optional.of(Pair.of(Pair.of(8000, 9000), true))));
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> IOValidators.validatePortRange(8080, Optional.of(Pair.of(Pair.of(8000, 9000), true))));
		assertTrue(exception.getMessage().contains("must not be in range"));
	}
	
	@Test
	void validatePortRangeWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOValidators.validatePortRange(8080, null));
	}
	
	@Test
	void validatePortTypeWithEmptyOptional() {
		assertDoesNotThrow(() -> IOValidators.validatePortType(80, Optional.empty()));
	}
	
	@Test
	void validatePortTypeWithSystemPort() {
		EnumConstraintConfig<PortRange> config = EnumConstraintConfig.<PortRange>unconstrained().withEqualTo(PortRange.SYSTEM);
		assertDoesNotThrow(() -> IOValidators.validatePortType(80, Optional.of(config)));
	}
	
	@Test
	void validatePortTypeWithRegisteredPort() {
		EnumConstraintConfig<PortRange> config = EnumConstraintConfig.<PortRange>unconstrained().withEqualTo(PortRange.REGISTERED);
		assertDoesNotThrow(() -> IOValidators.validatePortType(8080, Optional.of(config)));
	}
	
	@Test
	void validatePortTypeWithDynamicPort() {
		EnumConstraintConfig<PortRange> config = EnumConstraintConfig.<PortRange>unconstrained().withEqualTo(PortRange.DYNAMIC);
		assertDoesNotThrow(() -> IOValidators.validatePortType(50000, Optional.of(config)));
	}
	
	@Test
	void validatePortTypeWithMismatch() {
		EnumConstraintConfig<PortRange> config = EnumConstraintConfig.<PortRange>unconstrained().withEqualTo(PortRange.SYSTEM);
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> IOValidators.validatePortType(8080, Optional.of(config)));
		assertTrue(exception.getMessage().contains("Port type constraint failed"));
	}
	
	@Test
	void validatePortTypeWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOValidators.validatePortType(80, null));
	}
	
	@Test
	void validateIpVersionWithEmptyOptional() {
		assertDoesNotThrow(() -> IOValidators.validateIpVersion("192.168.1.1", Optional.empty()));
	}
	
	@Test
	void validateIpVersionWithIpv4() {
		EnumConstraintConfig<IpVersion> config = EnumConstraintConfig.<IpVersion>unconstrained().withEqualTo(IpVersion.IPV4);
		assertDoesNotThrow(() -> IOValidators.validateIpVersion("192.168.1.1", Optional.of(config)));
	}
	
	@Test
	void validateIpVersionWithIpv6() {
		EnumConstraintConfig<IpVersion> config = EnumConstraintConfig.<IpVersion>unconstrained().withEqualTo(IpVersion.IPV6);
		assertDoesNotThrow(() -> IOValidators.validateIpVersion("2001:0db8::1", Optional.of(config)));
	}
	
	@Test
	void validateIpVersionWithMismatch() {
		EnumConstraintConfig<IpVersion> config = EnumConstraintConfig.<IpVersion>unconstrained().withEqualTo(IpVersion.IPV6);
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> IOValidators.validateIpVersion("192.168.1.1", Optional.of(config)));
		assertTrue(exception.getMessage().contains("IP version constraint failed") || exception.getMessage().contains("Ip version constraint failed"));
	}
	
	@Test
	void validateIpVersionWithInvalidIp() {
		EnumConstraintConfig<IpVersion> config = EnumConstraintConfig.<IpVersion>unconstrained().withEqualTo(IpVersion.IPV4);
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> IOValidators.validateIpVersion("not.an.ip", Optional.of(config)));
		assertTrue(exception.getMessage().contains("not a valid"));
	}
	
	@Test
	void validateIpVersionWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOValidators.validateIpVersion(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> IOValidators.validateIpVersion("192.168.1.1", null));
	}
	
	@Test
	void validateIpTypeWithEmptyOptional() {
		assertDoesNotThrow(() -> IOValidators.validateIpType("192.168.1.1", Optional.empty()));
	}
	
	@Test
	void validateIpTypeWithPublicIpv4() {
		EnumConstraintConfig<IpAddressType> config = EnumConstraintConfig.<IpAddressType>unconstrained().withEqualTo(IpAddressType.PUBLIC);
		assertDoesNotThrow(() -> IOValidators.validateIpType("8.8.8.8", Optional.of(config)));
	}
	
	@Test
	void validateIpTypeWithPrivateIpv4() {
		EnumConstraintConfig<IpAddressType> config = EnumConstraintConfig.<IpAddressType>unconstrained().withEqualTo(IpAddressType.PRIVATE);
		assertDoesNotThrow(() -> IOValidators.validateIpType("192.168.1.1", Optional.of(config)));
	}
	
	@Test
	void validateIpTypeWithLoopbackIpv4() {
		EnumConstraintConfig<IpAddressType> config = EnumConstraintConfig.<IpAddressType>unconstrained().withEqualTo(IpAddressType.LOOPBACK);
		assertDoesNotThrow(() -> IOValidators.validateIpType("127.0.0.1", Optional.of(config)));
	}
	
	@Test
	void validateIpTypeWithLinkLocalIpv4() {
		EnumConstraintConfig<IpAddressType> config = EnumConstraintConfig.<IpAddressType>unconstrained().withEqualTo(IpAddressType.LINK_LOCAL);
		assertDoesNotThrow(() -> IOValidators.validateIpType("169.254.1.1", Optional.of(config)));
	}
	
	@Test
	void validateIpTypeWithMulticastIpv4() {
		EnumConstraintConfig<IpAddressType> config = EnumConstraintConfig.<IpAddressType>unconstrained().withEqualTo(IpAddressType.MULTICAST);
		assertDoesNotThrow(() -> IOValidators.validateIpType("224.0.0.1", Optional.of(config)));
	}
	
	@Test
	void validateIpTypeWithBroadcastIpv4() {
		EnumConstraintConfig<IpAddressType> config = EnumConstraintConfig.<IpAddressType>unconstrained().withEqualTo(IpAddressType.BROADCAST);
		assertDoesNotThrow(() -> IOValidators.validateIpType("255.255.255.255", Optional.of(config)));
	}
	
	@Test
	void validateIpTypeWithDocumentationIpv4() {
		EnumConstraintConfig<IpAddressType> config = EnumConstraintConfig.<IpAddressType>unconstrained().withEqualTo(IpAddressType.DOCUMENTATION);
		assertDoesNotThrow(() -> IOValidators.validateIpType("192.0.2.1", Optional.of(config)));
	}
	
	@Test
	void validateIpTypeWithUnspecifiedIpv4() {
		EnumConstraintConfig<IpAddressType> config = EnumConstraintConfig.<IpAddressType>unconstrained().withEqualTo(IpAddressType.UNSPECIFIED);
		assertDoesNotThrow(() -> IOValidators.validateIpType("0.0.0.0", Optional.of(config)));
	}
	
	@Test
	void validateIpTypeWithLoopbackIpv6() {
		EnumConstraintConfig<IpAddressType> config = EnumConstraintConfig.<IpAddressType>unconstrained().withEqualTo(IpAddressType.LOOPBACK);
		assertDoesNotThrow(() -> IOValidators.validateIpType("::1", Optional.of(config)));
	}
	
	@Test
	void validateIpTypeWithDocumentationIpv6() {
		EnumConstraintConfig<IpAddressType> config = EnumConstraintConfig.<IpAddressType>unconstrained().withEqualTo(IpAddressType.DOCUMENTATION);
		assertDoesNotThrow(() -> IOValidators.validateIpType("2001:db8::1", Optional.of(config)));
	}
	
	@Test
	void validateIpTypeWithMismatch() {
		EnumConstraintConfig<IpAddressType> config = EnumConstraintConfig.<IpAddressType>unconstrained().withEqualTo(IpAddressType.PUBLIC);
		assertThrows(ConstraintViolateException.class, () -> IOValidators.validateIpType("192.168.1.1", Optional.of(config)));
	}
	
	@Test
	void validateIpTypeWithInvalidIp() {
		EnumConstraintConfig<IpAddressType> config = EnumConstraintConfig.<IpAddressType>unconstrained().withEqualTo(IpAddressType.PUBLIC);
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> IOValidators.validateIpType("invalid", Optional.of(config)));
		assertTrue(exception.getMessage().contains("not a valid ip address"));
	}
	
	@Test
	void validateIpTypeWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOValidators.validateIpType(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> IOValidators.validateIpType("192.168.1.1", null));
	}
	
	@Test
	void validateInAnySubnetWithEmptyOptional() {
		assertDoesNotThrow(() -> IOValidators.validateInAnySubnet("192.168.1.1", Optional.empty()));
	}
	
	@Test
	void validateInAnySubnetWithIpv4InSubnet() {
		assertDoesNotThrow(() -> IOValidators.validateInAnySubnet("192.168.1.50", Optional.of(Pair.of(Set.of("192.168.1.0/24"), false))));
	}
	
	@Test
	void validateInAnySubnetWithIpv4NotInSubnet() {
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> IOValidators.validateInAnySubnet("10.0.0.1", Optional.of(Pair.of(Set.of("192.168.1.0/24"), false))));
		assertTrue(exception.getMessage().contains("must be member of at least one specified subnet"));
	}
	
	@Test
	void validateInAnySubnetWithIpv6InSubnet() {
		assertDoesNotThrow(() -> IOValidators.validateInAnySubnet("2001:db8::1", Optional.of(Pair.of(Set.of("2001:db8::/32"), false))));
	}
	
	@Test
	void validateInAnySubnetWithIpv6NotInSubnet() {
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> IOValidators.validateInAnySubnet("2001:db9::1", Optional.of(Pair.of(Set.of("2001:db8::/32"), false))));
		assertTrue(exception.getMessage().contains("must be member of at least one specified subnet"));
	}
	
	@Test
	void validateInAnySubnetWithMultipleSubnets() {
		assertDoesNotThrow(() -> IOValidators.validateInAnySubnet("10.0.0.1", Optional.of(Pair.of(Set.of("192.168.0.0/16", "10.0.0.0/8"), false))));
	}
	
	@Test
	void validateInAnySubnetWithNegatedNotMember() {
		assertDoesNotThrow(() -> IOValidators.validateInAnySubnet("8.8.8.8", Optional.of(Pair.of(Set.of("192.168.0.0/16"), true))));
	}
	
	@Test
	void validateInAnySubnetWithNegatedMember() {
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> IOValidators.validateInAnySubnet("192.168.1.1", Optional.of(Pair.of(Set.of("192.168.0.0/16"), true))));
		assertTrue(exception.getMessage().contains("must not be member of any specified subnet"));
	}
	
	@Test
	void validateInAnySubnetWithInvalidCidr() {
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> IOValidators.validateInAnySubnet("192.168.1.1", Optional.of(Pair.of(Set.of("invalid"), false))));
		assertTrue(exception.getMessage().contains("Invalid CIDR"));
	}
	
	@Test
	void validateInAnySubnetWithInvalidIp() {
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> IOValidators.validateInAnySubnet("invalid", Optional.of(Pair.of(Set.of("192.168.0.0/16"), false))));
		assertTrue(exception.getMessage().contains("not a valid ip address"));
	}
	
	@Test
	void validateInAnySubnetWithMixedSubnets() {
		assertDoesNotThrow(() -> IOValidators.validateInAnySubnet("192.168.1.1", Optional.of(Pair.of(Set.of("192.168.0.0/16", "2001:db8::/32"), false))));
	}
	
	@Test
	void validateInAnySubnetWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOValidators.validateInAnySubnet(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> IOValidators.validateInAnySubnet("192.168.1.1", null));
	}
	
	@Test
	void validateRootDomainWithEmptyOptional() {
		assertDoesNotThrow(() -> IOValidators.validateRootDomain("example.com", Optional.empty()));
	}
	
	@Test
	void validateRootDomainWithValidRootDomain() {
		assertDoesNotThrow(() -> IOValidators.validateRootDomain("example.com", Optional.of(Unit.INSTANCE)));
	}
	
	@Test
	void validateRootDomainWithSubDomain() {
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> IOValidators.validateRootDomain("sub.example.com", Optional.of(Unit.INSTANCE)));
		assertTrue(exception.getMessage().contains("must be a root domain"));
	}
	
	@Test
	void validateRootDomainWithNoDot() {
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> IOValidators.validateRootDomain("localhost", Optional.of(Unit.INSTANCE)));
		assertTrue(exception.getMessage().contains("must be a root domain"));
	}
	
	@Test
	void validateRootDomainWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOValidators.validateRootDomain(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> IOValidators.validateRootDomain("example.com", null));
	}
	
	@Test
	void validateSubDomainWithEmptyOptional() {
		assertDoesNotThrow(() -> IOValidators.validateSubDomain("sub.example.com", Optional.empty()));
	}
	
	@Test
	void validateSubDomainWithValidSubDomain() {
		assertDoesNotThrow(() -> IOValidators.validateSubDomain("sub.example.com", Optional.of(Unit.INSTANCE)));
	}
	
	@Test
	void validateSubDomainWithRootDomain() {
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> IOValidators.validateSubDomain("example.com", Optional.of(Unit.INSTANCE)));
		assertTrue(exception.getMessage().contains("must be a subdomain"));
	}
	
	@Test
	void validateSubDomainWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOValidators.validateSubDomain(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> IOValidators.validateSubDomain("sub.example.com", null));
	}
	
	@Test
	void validatePathCanonicalWithEmptyOptional() {
		assertDoesNotThrow(() -> IOValidators.validatePathCanonical(Path.of("/some/path"), Optional.empty()));
	}
	
	@Test
	void validatePathCanonicalWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOValidators.validatePathCanonical(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> IOValidators.validatePathCanonical(Path.of("/some/path"), null));
	}
	
	@Test
	void validatePathStringConfigWithEmptyOptional() {
		assertDoesNotThrow(() -> IOValidators.validatePathStringConfig(Path.of("/some/path"), Optional.empty()));
	}
	
	@Test
	void matchPathStringConfigWithValidate() {
		String separator = File.separator;
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withStartsWith(separator);
		assertDoesNotThrow(() -> IOValidators.validatePathStringConfig(Path.of(separator + "some" + separator + "path"), Optional.of(config)));
	}
	
	@Test
	void matchPathStringConfigWithNoValidate() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withStartsWith("/usr");
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> IOValidators.validatePathStringConfig(Path.of("/home/user"), Optional.of(config)));
		assertTrue(exception.getMessage().contains("Path constraint failed"));
	}
	
	@Test
	void validatePathStringConfigWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOValidators.validatePathStringConfig(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> IOValidators.validatePathStringConfig(Path.of("/some/path"), null));
	}
	
	@Test
	void validatePathRootConfigWithEmptyOptional() {
		assertDoesNotThrow(() -> IOValidators.validatePathRootConfig(Path.of("/some/path"), Optional.empty()));
	}
	
	@Test
	void validatePathRootConfigWithNoRoot() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withEqualTo("/");
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> IOValidators.validatePathRootConfig(Path.of("relative/path"), Optional.of(config)));
		assertTrue(exception.getMessage().contains("has no root component"));
	}
	
	@Test
	void validatePathRootConfigWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOValidators.validatePathRootConfig(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> IOValidators.validatePathRootConfig(Path.of("/some/path"), null));
	}
	
	@Test
	void validatePathParentConfigWithEmptyOptional() {
		assertDoesNotThrow(() -> IOValidators.validatePathParentConfig(Path.of("/some/path"), Optional.empty()));
	}
	
	@Test
	void validatePathParentConfigWithNoParent() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withContains("some");
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> IOValidators.validatePathParentConfig(Path.of("file"), Optional.of(config)));
		assertTrue(exception.getMessage().contains("has no parent component"));
	}
	
	@Test
	void validatePathParentConfigWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOValidators.validatePathParentConfig(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> IOValidators.validatePathParentConfig(Path.of("/some/path"), null));
	}
	
	@Test
	void validatePathSegmentConfigWithEmptyOptional() {
		assertDoesNotThrow(() -> IOValidators.validatePathSegmentConfig(Path.of("/some/path"), Optional.empty()));
	}
	
	@Test
	void matchPathSegmentConfigWithValidate() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withAlphabetic();
		assertDoesNotThrow(() -> IOValidators.validatePathSegmentConfig(Path.of("some/path"), Optional.of(config)));
	}
	
	@Test
	void matchPathSegmentConfigWithNoValidate() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withNumeric();
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> IOValidators.validatePathSegmentConfig(Path.of("some/path"), Optional.of(config)));
		assertTrue(exception.getMessage().contains("Segment"));
	}
	
	@Test
	void validatePathSegmentConfigWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOValidators.validatePathSegmentConfig(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> IOValidators.validatePathSegmentConfig(Path.of("/some/path"), null));
	}
	
	@Test
	void validatePathFileNameConfigWithEmptyOptional() {
		assertDoesNotThrow(() -> IOValidators.validatePathFileNameConfig(Path.of("/some/file.txt"), Optional.empty()));
	}
	
	@Test
	void matchPathFileNameConfigWithValidate() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withEndsWith(".txt");
		assertDoesNotThrow(() -> IOValidators.validatePathFileNameConfig(Path.of("/some/file.txt"), Optional.of(config)));
	}
	
	@Test
	void matchPathFileNameConfigWithNoValidate() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withEndsWith(".java");
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> IOValidators.validatePathFileNameConfig(Path.of("/some/file.txt"), Optional.of(config)));
		assertTrue(exception.getMessage().contains("File name constraint failed") || exception.getMessage().contains("File constraint failed"));
	}
	
	@Test
	void validatePathFileNameConfigWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOValidators.validatePathFileNameConfig(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> IOValidators.validatePathFileNameConfig(Path.of("/some/file.txt"), null));
	}
	
	@Test
	void validatePathExtensionConfigWithEmptyOptional() {
		assertDoesNotThrow(() -> IOValidators.validatePathExtensionConfig(Path.of("/some/file.txt"), Optional.empty()));
	}
	
	@Test
	void matchPathExtensionConfigWithValidate() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withEqualTo("txt");
		assertDoesNotThrow(() -> IOValidators.validatePathExtensionConfig(Path.of("/some/file.txt"), Optional.of(config)));
	}
	
	@Test
	void validatePathExtensionConfigWithNoExtension() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withEqualTo("txt");
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> IOValidators.validatePathExtensionConfig(Path.of("/some/file"), Optional.of(config)));
		assertTrue(exception.getMessage().contains("has no extension"));
	}
	
	@Test
	void validatePathExtensionConfigWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOValidators.validatePathExtensionConfig(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> IOValidators.validatePathExtensionConfig(Path.of("/some/file.txt"), null));
	}
	
	@Test
	void validatePathWithoutExtensionWithEmptyOptional() {
		assertDoesNotThrow(() -> IOValidators.validatePathWithoutExtension(Path.of("/some/file.txt"), Optional.empty()));
	}
	
	@Test
	void validatePathWithoutExtensionWithNoExtension() {
		assertDoesNotThrow(() -> IOValidators.validatePathWithoutExtension(Path.of("/some/file"), Optional.of(Unit.INSTANCE)));
	}
	
	@Test
	void validatePathWithoutExtensionWithExtension() {
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> IOValidators.validatePathWithoutExtension(Path.of("/some/file.txt"), Optional.of(Unit.INSTANCE)));
		assertTrue(exception.getMessage().contains("must not have an extension"));
	}
	
	@Test
	void validatePathWithoutExtensionWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOValidators.validatePathWithoutExtension(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> IOValidators.validatePathWithoutExtension(Path.of("/some/file"), null));
	}
	
	@Test
	void validatePathAncestorOfWithEmptyOptional() {
		assertDoesNotThrow(() -> IOValidators.validatePathAncestorOf(Path.of("/some"), Optional.empty()));
	}
	
	@Test
	void validatePathAncestorOfWithValid() {
		assertDoesNotThrow(() -> IOValidators.validatePathAncestorOf(Path.of("/some"), Optional.of(Set.of("/some/path", "/some/other"))));
	}
	
	@Test
	void validatePathAncestorOfWithInvalid() {
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> IOValidators.validatePathAncestorOf(Path.of("/other"), Optional.of(Set.of("/some/path"))));
		assertTrue(exception.getMessage().contains("must be ancestor of"));
	}
	
	@Test
	void validatePathAncestorOfWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOValidators.validatePathAncestorOf(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> IOValidators.validatePathAncestorOf(Path.of("/some"), null));
	}
	
	@Test
	void validatePathDescendantOfWithEmptyOptional() {
		assertDoesNotThrow(() -> IOValidators.validatePathDescendantOf(Path.of("/some/path/file"), Optional.empty()));
	}
	
	@Test
	void validatePathDescendantOfWithValid() {
		assertDoesNotThrow(() -> IOValidators.validatePathDescendantOf(Path.of("/some/path/file"), Optional.of(Set.of("/some", "/some/path"))));
	}
	
	@Test
	void validatePathDescendantOfWithInvalid() {
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> IOValidators.validatePathDescendantOf(Path.of("/other/path"), Optional.of(Set.of("/some"))));
		assertTrue(exception.getMessage().contains("must be descendant of"));
	}
	
	@Test
	void validatePathDescendantOfWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOValidators.validatePathDescendantOf(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> IOValidators.validatePathDescendantOf(Path.of("/some/path"), null));
	}
	
	@Test
	void validateQueryValueConstraintsWithEmptyOptional() {
		Map<String, List<String>> query = Map.of("key", List.of("value"));
		assertDoesNotThrow(() -> IOValidators.validateQueryValueConstraints(query, Optional.empty()));
	}
	
	@Test
	void matchQueryValueConstraintsWithValidate() {
		Map<String, List<String>> query = Map.of("key", List.of("value"));
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withEqualTo("value");
		assertDoesNotThrow(() -> IOValidators.validateQueryValueConstraints(query, Optional.of(Map.of("key", config))));
	}
	
	@Test
	void matchQueryValueConstraintsWithNoValidate() {
		Map<String, List<String>> query = Map.of("key", List.of("wrong"));
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withEqualTo("value");
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> IOValidators.validateQueryValueConstraints(query, Optional.of(Map.of("key", config))));
		assertTrue(exception.getMessage().contains("Value constraint for key"));
	}
	
	@Test
	void validateQueryValueConstraintsWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOValidators.validateQueryValueConstraints(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> IOValidators.validateQueryValueConstraints(Map.of(), null));
	}
	
	@Test
	void validateQueryPatternValueConstraintsWithEmptyOptional() {
		Map<String, List<String>> query = Map.of("key1", List.of("value"));
		assertDoesNotThrow(() -> IOValidators.validateQueryPatternValueConstraints(query, Optional.empty()));
	}
	
	@Test
	void matchQueryPatternValueConstraintsWithValidate() {
		Map<String, List<String>> query = Map.of("key1", List.of("value"));
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withAlphabetic();
		assertDoesNotThrow(() -> IOValidators.validateQueryPatternValueConstraints(query, Optional.of(Map.of(Pattern.compile("key\\d+"), config))));
	}
	
	@Test
	void matchQueryPatternValueConstraintsWithNoValidate() {
		Map<String, List<String>> query = Map.of("key1", List.of("123"));
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withAlphabetic();
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> IOValidators.validateQueryPatternValueConstraints(query, Optional.of(Map.of(Pattern.compile("key\\d+"), config))));
		assertTrue(exception.getMessage().contains("Pattern value constraint"));
	}
	
	@Test
	void validateQueryPatternValueConstraintsWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOValidators.validateQueryPatternValueConstraints(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> IOValidators.validateQueryPatternValueConstraints(Map.of(), null));
	}
	
	@Test
	void validateQuerySingleValuedWithEmptyOptional() {
		Map<String, List<String>> query = Map.of("key", List.of("value1", "value2"));
		assertDoesNotThrow(() -> IOValidators.validateQuerySingleValued(query, Optional.empty()));
	}
	
	@Test
	void validateQuerySingleValuedWithSingleValues() {
		Map<String, List<String>> query = Map.of("key1", List.of("value1"), "key2", List.of("value2"));
		assertDoesNotThrow(() -> IOValidators.validateQuerySingleValued(query, Optional.of(Unit.INSTANCE)));
	}
	
	@Test
	void validateQuerySingleValuedWithMultipleValues() {
		Map<String, List<String>> query = Map.of("key", List.of("value1", "value2"));
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> IOValidators.validateQuerySingleValued(query, Optional.of(Unit.INSTANCE)));
		assertTrue(exception.getMessage().contains("must have exactly one value"));
	}
	
	@Test
	void validateQuerySingleValuedWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOValidators.validateQuerySingleValued(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> IOValidators.validateQuerySingleValued(Map.of(), null));
	}
	
	@Test
	void validateQueryUniqueValuesWithEmptyOptional() {
		Map<String, List<String>> query = Map.of("key1", List.of("value"), "key2", List.of("value"));
		assertDoesNotThrow(() -> IOValidators.validateQueryUniqueValues(query, Optional.empty()));
	}
	
	@Test
	void validateQueryUniqueValuesWithUnique() {
		Map<String, List<String>> query = Map.of("key1", List.of("value1"), "key2", List.of("value2"));
		assertDoesNotThrow(() -> IOValidators.validateQueryUniqueValues(query, Optional.of(Unit.INSTANCE)));
	}
	
	@Test
	void validateQueryUniqueValuesWithDuplicates() {
		Map<String, List<String>> query = Map.of("key1", List.of("value", "other"), "key2", List.of("value"));
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> IOValidators.validateQueryUniqueValues(query, Optional.of(Unit.INSTANCE)));
		assertTrue(exception.getMessage().contains("must be unique"));
	}
	
	@Test
	void validateQueryUniqueValuesWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOValidators.validateQueryUniqueValues(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> IOValidators.validateQueryUniqueValues(Map.of(), null));
	}
	
	@Test
	void validateQueryMultiValuedConstraintsWithEmptyOptional() {
		Map<String, List<String>> query = Map.of("key", List.of("v1", "v2", "v3"));
		assertDoesNotThrow(() -> IOValidators.validateQueryMultiValuedConstraints(query, Optional.empty()));
	}
	
	@Test
	void matchQueryMultiValuedConstraintsWithValidate() {
		Map<String, List<String>> query = Map.of("key", List.of("v1", "v2"));
		SizeConstraintConfig config = SizeConstraintConfig.UNCONSTRAINED.withSizeBetween(1, 3);
		assertDoesNotThrow(() -> IOValidators.validateQueryMultiValuedConstraints(query, Optional.of(Map.of("key", config))));
	}
	
	@Test
	void matchQueryMultiValuedConstraintsWithNoValidate() {
		Map<String, List<String>> query = Map.of("key", List.of("v1", "v2", "v3", "v4", "v5"));
		SizeConstraintConfig config = SizeConstraintConfig.UNCONSTRAINED.withMaxSize(3);
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> IOValidators.validateQueryMultiValuedConstraints(query, Optional.of(Map.of("key", config))));
		assertTrue(exception.getMessage().contains("Multi-valued constraint"));
	}
	
	@Test
	void validateQueryMultiValuedConstraintsWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOValidators.validateQueryMultiValuedConstraints(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> IOValidators.validateQueryMultiValuedConstraints(Map.of(), null));
	}
	
	@Test
	void validateUriSchemeConfigWithEmptyOptional() {
		URI uri = URI.create("https://example.com");
		assertDoesNotThrow(() -> IOValidators.validateUriSchemeConfig(uri, Optional.empty()));
	}
	
	@Test
	void matchUriSchemeConfigWithValidate() {
		URI uri = URI.create("https://example.com");
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withEqualTo("https");
		assertDoesNotThrow(() -> IOValidators.validateUriSchemeConfig(uri, Optional.of(config)));
	}
	
	@Test
	void validateUriSchemeConfigWithNoScheme() {
		URI uri = URI.create("//example.com");
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withEqualTo("https");
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> IOValidators.validateUriSchemeConfig(uri, Optional.of(config)));
		assertTrue(exception.getMessage().contains("has no scheme"));
	}
	
	@Test
	void validateUriSchemeConfigWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOValidators.validateUriSchemeConfig(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> IOValidators.validateUriSchemeConfig(URI.create("https://example.com"), null));
	}
	
	@Test
	void validateUriHostConfigWithEmptyOptional() {
		URI uri = URI.create("https://example.com");
		assertDoesNotThrow(() -> IOValidators.validateUriHostConfig(uri, Optional.empty()));
	}
	
	@Test
	void matchUriHostConfigWithValidate() {
		URI uri = URI.create("https://example.com");
		HostConstraintConfig config = HostConstraintConfig.UNCONSTRAINED.withEqualTo("example.com");
		assertDoesNotThrow(() -> IOValidators.validateUriHostConfig(uri, Optional.of(config)));
	}
	
	@Test
	void matchUriHostConfigWithNoValidate() {
		URI uri = URI.create("https://example.com");
		HostConstraintConfig config = HostConstraintConfig.UNCONSTRAINED.withEqualTo("other.com");
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> IOValidators.validateUriHostConfig(uri, Optional.of(config)));
		assertTrue(exception.getMessage().contains("Host constraint failed"));
	}
	
	@Test
	void validateUriHostConfigWithNoHost() {
		URI uri = URI.create("file:/path/to/file");
		HostConstraintConfig config = HostConstraintConfig.UNCONSTRAINED.withEqualTo("example.com");
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> IOValidators.validateUriHostConfig(uri, Optional.of(config)));
		assertTrue(exception.getMessage().contains("has no host"));
	}
	
	@Test
	void validateUriHostConfigWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOValidators.validateUriHostConfig(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> IOValidators.validateUriHostConfig(URI.create("https://example.com"), null));
	}
	
	@Test
	void validateUriUserInfoConfigWithEmptyOptional() {
		URI uri = URI.create("https://user:pass@example.com");
		assertDoesNotThrow(() -> IOValidators.validateUriUserInfoConfig(uri, Optional.empty()));
	}
	
	@Test
	void matchUriUserInfoConfigWithValidate() {
		URI uri = URI.create("https://user:pass@example.com");
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withContains("user");
		assertDoesNotThrow(() -> IOValidators.validateUriUserInfoConfig(uri, Optional.of(config)));
	}
	
	@Test
	void validateUriUserInfoConfigWithNoUserInfo() {
		URI uri = URI.create("https://example.com");
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withContains("user");
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> IOValidators.validateUriUserInfoConfig(uri, Optional.of(config)));
		assertTrue(exception.getMessage().contains("has no user info"));
	}
	
	@Test
	void validateUriUserInfoConfigWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOValidators.validateUriUserInfoConfig(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> IOValidators.validateUriUserInfoConfig(URI.create("https://example.com"), null));
	}
	
	@Test
	void validateUriPortConfigWithEmptyOptional() {
		URI uri = URI.create("https://example.com:8080");
		assertDoesNotThrow(() -> IOValidators.validateUriPortConfig(uri, Optional.empty()));
	}
	
	@Test
	void matchUriPortConfigWithValidate() {
		URI uri = URI.create("https://example.com:8080");
		PortConstraintConfig config = PortConstraintConfig.UNCONSTRAINED.withEqualTo(8080);
		assertDoesNotThrow(() -> IOValidators.validateUriPortConfig(uri, Optional.of(config)));
	}
	
	@Test
	void matchUriPortConfigWithNoValidate() {
		URI uri = URI.create("https://example.com:8080");
		PortConstraintConfig config = PortConstraintConfig.UNCONSTRAINED.withEqualTo(443);
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> IOValidators.validateUriPortConfig(uri, Optional.of(config)));
		assertTrue(exception.getMessage().contains("Port constraint failed"));
	}
	
	@Test
	void validateUriPortConfigWithNoPort() {
		URI uri = URI.create("https://example.com");
		PortConstraintConfig config = PortConstraintConfig.UNCONSTRAINED.withEqualTo(8080);
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> IOValidators.validateUriPortConfig(uri, Optional.of(config)));
		assertTrue(exception.getMessage().contains("has no port"));
	}
	
	@Test
	void validateUriPortConfigWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOValidators.validateUriPortConfig(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> IOValidators.validateUriPortConfig(URI.create("https://example.com:8080"), null));
	}
	
	@Test
	void validateUriPathConfigWithEmptyOptional() {
		URI uri = URI.create("https://example.com/path/to/resource");
		assertDoesNotThrow(() -> IOValidators.validateUriPathConfig(uri, Optional.empty()));
	}
	
	@Test
	void matchUriPathConfigWithValidate() {
		URI uri = URI.create("https://example.com/path/to/resource");
		URIPathConstraintConfig config = URIPathConstraintConfig.UNCONSTRAINED.withDepth(
			DepthConstraintConfig.UNCONSTRAINED.withMinDepth(2)
		);
		assertDoesNotThrow(() -> IOValidators.validateUriPathConfig(uri, Optional.of(config)));
	}
	
	@Test
	void matchUriPathConfigWithNoValidate() {
		URI uri = URI.create("https://example.com/path");
		URIPathConstraintConfig config = URIPathConstraintConfig.UNCONSTRAINED.withDepth(
			DepthConstraintConfig.UNCONSTRAINED.withMinDepth(3)
		);
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> IOValidators.validateUriPathConfig(uri, Optional.of(config)));
		assertTrue(exception.getMessage().contains("Path constraint failed"));
	}
	
	@Test
	void validateUriPathConfigWithNoPath() {
		URI uri = URI.create("https://example.com");
		URIPathConstraintConfig config = URIPathConstraintConfig.UNCONSTRAINED.withDepth(
			DepthConstraintConfig.UNCONSTRAINED.withMinDepth(1)
		);
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> IOValidators.validateUriPathConfig(uri, Optional.of(config)));
		assertTrue(exception.getMessage().contains("has no path"));
	}
	
	@Test
	void validateUriPathConfigWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOValidators.validateUriPathConfig(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> IOValidators.validateUriPathConfig(URI.create("https://example.com/path"), null));
	}
	
	@Test
	void validateUriQueryConfigWithEmptyOptional() {
		URI uri = URI.create("https://example.com?key=value");
		assertDoesNotThrow(() -> IOValidators.validateUriQueryConfig(uri, Optional.empty()));
	}
	
	@Test
	void matchUriQueryConfigWithValidate() {
		URI uri = URI.create("https://example.com?key=value");
		QueryConstraintConfig config = QueryConstraintConfig.UNCONSTRAINED.withRequiredKeys(List.of("key"));
		assertDoesNotThrow(() -> IOValidators.validateUriQueryConfig(uri, Optional.of(config)));
	}
	
	@Test
	void matchUriQueryConfigWithNoValidate() {
		URI uri = URI.create("https://example.com?key=value");
		QueryConstraintConfig config = QueryConstraintConfig.UNCONSTRAINED.withRequiredKeys(List.of("missing"));
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> IOValidators.validateUriQueryConfig(uri, Optional.of(config)));
		assertTrue(exception.getMessage().contains("Query constraint failed"));
	}
	
	@Test
	void validateUriQueryConfigWithNoQuery() {
		URI uri = URI.create("https://example.com");
		QueryConstraintConfig config = QueryConstraintConfig.UNCONSTRAINED.withSize(SizeConstraintConfig.UNCONSTRAINED.withMinSize(1));
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> IOValidators.validateUriQueryConfig(uri, Optional.of(config)));
		assertTrue(exception.getMessage().contains("has no query"));
	}
	
	@Test
	void validateUriQueryConfigWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOValidators.validateUriQueryConfig(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> IOValidators.validateUriQueryConfig(URI.create("https://example.com?key=value"), null));
	}
	
	@Test
	void validateUriFragmentConfigWithEmptyOptional() {
		URI uri = URI.create("https://example.com#section");
		assertDoesNotThrow(() -> IOValidators.validateUriFragmentConfig(uri, Optional.empty()));
	}
	
	@Test
	void matchUriFragmentConfigWithValidate() {
		URI uri = URI.create("https://example.com#section");
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withEqualTo("section");
		assertDoesNotThrow(() -> IOValidators.validateUriFragmentConfig(uri, Optional.of(config)));
	}
	
	@Test
	void validateUriFragmentConfigWithNoFragment() {
		URI uri = URI.create("https://example.com");
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withEqualTo("section");
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> IOValidators.validateUriFragmentConfig(uri, Optional.of(config)));
		assertTrue(exception.getMessage().contains("has no fragment"));
	}
	
	@Test
	void validateUriFragmentConfigWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOValidators.validateUriFragmentConfig(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> IOValidators.validateUriFragmentConfig(URI.create("https://example.com"), null));
	}
	
	@Test
	void parseQueryWithEmptyString() {
		Map<String, List<String>> result = IOValidators.parseQuery("");
		assertTrue(result.isEmpty());
	}
	
	@Test
	void parseQueryWithSingleParam() {
		Map<String, List<String>> result = IOValidators.parseQuery("key=value");
		assertEquals(1, result.size());
		assertEquals(List.of("value"), result.get("key"));
	}
	
	@Test
	void parseQueryWithMultipleParams() {
		Map<String, List<String>> result = IOValidators.parseQuery("key1=value1&key2=value2");
		assertEquals(2, result.size());
		assertEquals(List.of("value1"), result.get("key1"));
		assertEquals(List.of("value2"), result.get("key2"));
	}
	
	@Test
	void parseQueryWithRepeatedKeys() {
		Map<String, List<String>> result = IOValidators.parseQuery("key=value1&key=value2");
		assertEquals(1, result.size());
		assertEquals(List.of("value1", "value2"), result.get("key"));
	}
	
	@Test
	void parseQueryWithEmptyValue() {
		Map<String, List<String>> result = IOValidators.parseQuery("key=");
		assertEquals(1, result.size());
		assertEquals(List.of(""), result.get("key"));
	}
	
	@Test
	void parseQueryWithNoValue() {
		Map<String, List<String>> result = IOValidators.parseQuery("key");
		assertEquals(1, result.size());
		assertEquals(List.of(""), result.get("key"));
	}
	
	@Test
	void parseQueryWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOValidators.parseQuery(null));
	}
	
	// URI Path matcher tests
	
	@Test
	void validateUriPathStringConfigWithEmptyOptional() {
		assertDoesNotThrow(() -> IOValidators.validateUriPathStringConfig("/some/path", Optional.empty()));
	}
	
	@Test
	void matchUriPathStringConfigWithValidate() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withStartsWith("/");
		assertDoesNotThrow(() -> IOValidators.validateUriPathStringConfig("/some/path", Optional.of(config)));
	}
	
	@Test
	void matchUriPathStringConfigWithNoValidate() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withStartsWith("/usr");
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> IOValidators.validateUriPathStringConfig("/home/user", Optional.of(config)));
		assertTrue(exception.getMessage().contains("Path constraint failed"));
	}
	
	@Test
	void validateUriPathStringConfigWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOValidators.validateUriPathStringConfig(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> IOValidators.validateUriPathStringConfig("/some/path", null));
	}
	
	@Test
	void validateUriPathSegmentConfigWithEmptyOptional() {
		assertDoesNotThrow(() -> IOValidators.validateUriPathSegmentConfig("/some/path", Optional.empty()));
	}
	
	@Test
	void matchUriPathSegmentConfigWithValidate() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withAlphabetic();
		assertDoesNotThrow(() -> IOValidators.validateUriPathSegmentConfig("/some/path", Optional.of(config)));
	}
	
	@Test
	void matchUriPathSegmentConfigWithNoValidate() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withNumeric();
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> IOValidators.validateUriPathSegmentConfig("/some/path", Optional.of(config)));
		assertTrue(exception.getMessage().contains("Segment"));
	}
	
	@Test
	void validateUriPathSegmentConfigWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOValidators.validateUriPathSegmentConfig(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> IOValidators.validateUriPathSegmentConfig("/some/path", null));
	}
	
	@Test
	void validateUriPathFileNameConfigWithEmptyOptional() {
		assertDoesNotThrow(() -> IOValidators.validateUriPathFileNameConfig("/some/file.txt", Optional.empty()));
	}
	
	@Test
	void matchUriPathFileNameConfigWithValidate() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withEndsWith(".txt");
		assertDoesNotThrow(() -> IOValidators.validateUriPathFileNameConfig("/some/file.txt", Optional.of(config)));
	}
	
	@Test
	void matchUriPathFileNameConfigWithNoValidate() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withEndsWith(".java");
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> IOValidators.validateUriPathFileNameConfig("/some/file.txt", Optional.of(config)));
		assertTrue(exception.getMessage().contains("File") && exception.getMessage().contains("constraint failed"));
	}
	
	@Test
	void validateUriPathFileNameConfigWithNoFileName() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withEqualTo("test");
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> IOValidators.validateUriPathFileNameConfig("", Optional.of(config)));
		assertTrue(exception.getMessage().contains("has no file name"));
	}
	
	@Test
	void validateUriPathFileNameConfigWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOValidators.validateUriPathFileNameConfig(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> IOValidators.validateUriPathFileNameConfig("/some/file.txt", null));
	}
	
	@Test
	void validateUriPathWithoutExtensionWithEmptyOptional() {
		assertDoesNotThrow(() -> IOValidators.validateUriPathWithoutExtension("/some/file.txt", Optional.empty()));
	}
	
	@Test
	void validateUriPathWithoutExtensionWithNoExtension() {
		assertDoesNotThrow(() -> IOValidators.validateUriPathWithoutExtension("/some/file", Optional.of(Unit.INSTANCE)));
	}
	
	@Test
	void validateUriPathWithoutExtensionWithExtension() {
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> IOValidators.validateUriPathWithoutExtension("/some/file.txt", Optional.of(Unit.INSTANCE)));
		assertTrue(exception.getMessage().contains("must not have an extension"));
	}
	
	@Test
	void validateUriPathWithoutExtensionWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOValidators.validateUriPathWithoutExtension(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> IOValidators.validateUriPathWithoutExtension("/some/file", null));
	}
	
	@Test
	void validateUriPathExtensionConfigWithEmptyOptional() {
		assertDoesNotThrow(() -> IOValidators.validateUriPathExtensionConfig("/some/file.txt", Optional.empty()));
	}
	
	@Test
	void matchUriPathExtensionConfigWithValidate() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withEqualTo("txt");
		assertDoesNotThrow(() -> IOValidators.validateUriPathExtensionConfig("/some/file.txt", Optional.of(config)));
	}
	
	@Test
	void validateUriPathExtensionConfigWithNoExtension() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withEqualTo("txt");
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> IOValidators.validateUriPathExtensionConfig("/some/file", Optional.of(config)));
		assertTrue(exception.getMessage().contains("has no extension"));
	}
	
	@Test
	void validateUriPathExtensionConfigWithNoFileName() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withEqualTo("txt");
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> IOValidators.validateUriPathExtensionConfig("", Optional.of(config)));
		assertTrue(exception.getMessage().contains("has no file name"));
	}
	
	@Test
	void validateUriPathExtensionConfigWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOValidators.validateUriPathExtensionConfig(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> IOValidators.validateUriPathExtensionConfig("/some/file.txt", null));
	}
	
	@Test
	void validateUriPathAncestorOfWithEmptyOptional() {
		assertDoesNotThrow(() -> IOValidators.validateUriPathAncestorOf("/some", Optional.empty()));
	}
	
	@Test
	void validateUriPathAncestorOfWithValid() {
		assertDoesNotThrow(() -> IOValidators.validateUriPathAncestorOf("/some", Optional.of(Set.of("/some/path", "/some/other"))));
	}
	
	@Test
	void validateUriPathAncestorOfWithInvalid() {
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> IOValidators.validateUriPathAncestorOf("/other", Optional.of(Set.of("/some/path"))));
		assertTrue(exception.getMessage().contains("must be ancestor of"));
	}
	
	@Test
	void validateUriPathAncestorOfWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOValidators.validateUriPathAncestorOf(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> IOValidators.validateUriPathAncestorOf("/some", null));
	}
	
	@Test
	void validateUriPathDescendantOfWithEmptyOptional() {
		assertDoesNotThrow(() -> IOValidators.validateUriPathDescendantOf("/some/path/file", Optional.empty()));
	}
	
	@Test
	void validateUriPathDescendantOfWithValid() {
		assertDoesNotThrow(() -> IOValidators.validateUriPathDescendantOf("/some/path/file", Optional.of(Set.of("/some", "/some/path"))));
	}
	
	@Test
	void validateUriPathDescendantOfWithInvalid() {
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> IOValidators.validateUriPathDescendantOf("/other/path", Optional.of(Set.of("/some"))));
		assertTrue(exception.getMessage().contains("must be descendant of"));
	}
	
	@Test
	void validateUriPathDescendantOfWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOValidators.validateUriPathDescendantOf(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> IOValidators.validateUriPathDescendantOf("/some/path", null));
	}
	
	@Test
	void validateInetAddressIpVersionWithEmptyOptional() throws UnknownHostException {
		InetAddress address = InetAddress.getByName("192.168.1.1");
		assertDoesNotThrow(() -> IOValidators.validateInetAddressIpVersion(address, Optional.empty()));
	}
	
	@Test
	void validateInetAddressIpVersionWithIpv4() throws UnknownHostException {
		InetAddress address = InetAddress.getByName("192.168.1.1");
		EnumConstraintConfig<IpVersion> config = EnumConstraintConfig.<IpVersion>unconstrained().withEqualTo(IpVersion.IPV4);
		assertDoesNotThrow(() -> IOValidators.validateInetAddressIpVersion(address, Optional.of(config)));
	}
	
	@Test
	void validateInetAddressIpVersionWithIpv6() throws UnknownHostException {
		InetAddress address = InetAddress.getByName("::1");
		EnumConstraintConfig<IpVersion> config = EnumConstraintConfig.<IpVersion>unconstrained().withEqualTo(IpVersion.IPV6);
		assertDoesNotThrow(() -> IOValidators.validateInetAddressIpVersion(address, Optional.of(config)));
	}
	
	@Test
	void validateInetAddressIpVersionWithMismatch() throws UnknownHostException {
		InetAddress address = InetAddress.getByName("192.168.1.1");
		EnumConstraintConfig<IpVersion> config = EnumConstraintConfig.<IpVersion>unconstrained().withEqualTo(IpVersion.IPV6);
		assertThrows(ConstraintViolateException.class, () -> IOValidators.validateInetAddressIpVersion(address, Optional.of(config)));
	}
	
	@Test
	void validateInetAddressIpVersionWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOValidators.validateInetAddressIpVersion(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> IOValidators.validateInetAddressIpVersion(InetAddress.getByName("192.168.1.1"), null));
	}
	
	@Test
	void validateInetAddressIpTypeWithEmptyOptional() throws UnknownHostException {
		InetAddress address = InetAddress.getByName("192.168.1.1");
		assertDoesNotThrow(() -> IOValidators.validateInetAddressIpType(address, Optional.empty()));
	}
	
	@Test
	void validateInetAddressIpTypeWithPrivate() throws UnknownHostException {
		InetAddress address = InetAddress.getByName("192.168.1.1");
		EnumConstraintConfig<IpAddressType> config = EnumConstraintConfig.<IpAddressType>unconstrained().withEqualTo(IpAddressType.PRIVATE);
		assertDoesNotThrow(() -> IOValidators.validateInetAddressIpType(address, Optional.of(config)));
	}
	
	@Test
	void validateInetAddressIpTypeWithLoopback() throws UnknownHostException {
		InetAddress address = InetAddress.getByName("127.0.0.1");
		EnumConstraintConfig<IpAddressType> config = EnumConstraintConfig.<IpAddressType>unconstrained().withEqualTo(IpAddressType.LOOPBACK);
		assertDoesNotThrow(() -> IOValidators.validateInetAddressIpType(address, Optional.of(config)));
	}
	
	@Test
	void validateInetAddressIpTypeWithLoopbackIpv6() throws UnknownHostException {
		InetAddress address = InetAddress.getByName("::1");
		EnumConstraintConfig<IpAddressType> config = EnumConstraintConfig.<IpAddressType>unconstrained().withEqualTo(IpAddressType.LOOPBACK);
		assertDoesNotThrow(() -> IOValidators.validateInetAddressIpType(address, Optional.of(config)));
	}
	
	@Test
	void validateInetAddressIpTypeWithMismatch() throws UnknownHostException {
		InetAddress address = InetAddress.getByName("192.168.1.1");
		EnumConstraintConfig<IpAddressType> config = EnumConstraintConfig.<IpAddressType>unconstrained().withEqualTo(IpAddressType.LOOPBACK);
		assertThrows(ConstraintViolateException.class, () -> IOValidators.validateInetAddressIpType(address, Optional.of(config)));
	}
	
	@Test
	void validateInetAddressIpTypeWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOValidators.validateInetAddressIpType(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> IOValidators.validateInetAddressIpType(InetAddress.getByName("192.168.1.1"), null));
	}
	
	@Test
	void validateInetAddressInAnySubnetWithEmptyOptional() throws UnknownHostException {
		InetAddress address = InetAddress.getByName("192.168.1.1");
		assertDoesNotThrow(() -> IOValidators.validateInetAddressInAnySubnet(address, Optional.empty()));
	}
	
	@Test
	void matchInetAddressInAnySubnetWithValidate() throws UnknownHostException {
		InetAddress address = InetAddress.getByName("192.168.1.50");
		assertDoesNotThrow(() -> IOValidators.validateInetAddressInAnySubnet(address, Optional.of(Pair.of(Set.of("192.168.1.0/24"), false))));
	}
	
	@Test
	void matchInetAddressInAnySubnetWithNoValidate() throws UnknownHostException {
		InetAddress address = InetAddress.getByName("10.0.0.1");
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> IOValidators.validateInetAddressInAnySubnet(address, Optional.of(Pair.of(Set.of("192.168.1.0/24"), false))));
		assertTrue(exception.getMessage().contains("must be member of at least one specified subnet"));
	}
	
	@Test
	void validateInetAddressInAnySubnetWithNegated() throws UnknownHostException {
		InetAddress address = InetAddress.getByName("10.0.0.1");
		assertDoesNotThrow(() -> IOValidators.validateInetAddressInAnySubnet(address, Optional.of(Pair.of(Set.of("192.168.0.0/16"), true))));
	}
	
	@Test
	void validateInetAddressInAnySubnetWithNegatedMember() throws UnknownHostException {
		InetAddress address = InetAddress.getByName("192.168.1.1");
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> IOValidators.validateInetAddressInAnySubnet(address, Optional.of(Pair.of(Set.of("192.168.0.0/16"), true))));
		assertTrue(exception.getMessage().contains("must not be member of any specified subnet"));
	}
	
	@Test
	void validateInetAddressInAnySubnetWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOValidators.validateInetAddressInAnySubnet(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> IOValidators.validateInetAddressInAnySubnet(InetAddress.getByName("192.168.1.1"), null));
	}
	
	@Test
	void validateInetSocketAddressAddressWithEmptyOptional() throws UnknownHostException {
		InetSocketAddress socketAddress = new InetSocketAddress(InetAddress.getByName("192.168.1.1"), 8080);
		assertDoesNotThrow(() -> IOValidators.validateInetSocketAddressAddress(socketAddress, Optional.empty()));
	}
	
	@Test
	void matchInetSocketAddressAddressWithValidate() throws UnknownHostException {
		InetSocketAddress socketAddress = new InetSocketAddress(InetAddress.getByName("192.168.1.1"), 8080);
		InetAddressConstraintConfig config = InetAddressConstraintConfig.UNCONSTRAINED.withIpType(
			EnumConstraintConfig.<IpAddressType>unconstrained().withEqualTo(IpAddressType.PRIVATE)
		);
		assertDoesNotThrow(() -> IOValidators.validateInetSocketAddressAddress(socketAddress, Optional.of(config)));
	}
	
	@Test
	void matchInetSocketAddressAddressWithNoValidate() throws UnknownHostException {
		InetSocketAddress socketAddress = new InetSocketAddress(InetAddress.getByName("127.0.0.1"), 8080);
		InetAddressConstraintConfig config = InetAddressConstraintConfig.UNCONSTRAINED.withIpType(
			EnumConstraintConfig.<IpAddressType>unconstrained().withEqualTo(IpAddressType.PRIVATE)
		);
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> IOValidators.validateInetSocketAddressAddress(socketAddress, Optional.of(config)));
		assertTrue(exception.getMessage().contains("Address constraint failed"));
	}
	
	@Test
	void validateInetSocketAddressAddressWithUnresolved() {
		InetSocketAddress socketAddress = InetSocketAddress.createUnresolved("nonexistent.invalid", 8080);
		InetAddressConstraintConfig config = InetAddressConstraintConfig.UNCONSTRAINED;
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> IOValidators.validateInetSocketAddressAddress(socketAddress, Optional.of(config)));
		assertTrue(exception.getMessage().contains("has no resolved address"));
	}
	
	@Test
	void validateInetSocketAddressAddressWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOValidators.validateInetSocketAddressAddress(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> IOValidators.validateInetSocketAddressAddress(
			new InetSocketAddress(InetAddress.getByName("192.168.1.1"), 8080), null
		));
	}
	
	@Test
	void validateInetSocketAddressPortWithEmptyOptional() throws UnknownHostException {
		InetSocketAddress socketAddress = new InetSocketAddress(InetAddress.getByName("192.168.1.1"), 8080);
		assertDoesNotThrow(() -> IOValidators.validateInetSocketAddressPort(socketAddress, Optional.empty()));
	}
	
	@Test
	void matchInetSocketAddressPortWithValidate() throws UnknownHostException {
		InetSocketAddress socketAddress = new InetSocketAddress(InetAddress.getByName("192.168.1.1"), 8080);
		PortConstraintConfig config = PortConstraintConfig.UNCONSTRAINED.withInRange(8000, 9000);
		assertDoesNotThrow(() -> IOValidators.validateInetSocketAddressPort(socketAddress, Optional.of(config)));
	}
	
	@Test
	void matchInetSocketAddressPortWithNoValidate() throws UnknownHostException {
		InetSocketAddress socketAddress = new InetSocketAddress(InetAddress.getByName("192.168.1.1"), 80);
		PortConstraintConfig config = PortConstraintConfig.UNCONSTRAINED.withInRange(8000, 9000);
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> IOValidators.validateInetSocketAddressPort(socketAddress, Optional.of(config)));
		assertTrue(exception.getMessage().contains("Port constraint failed"));
	}
	
	@Test
	void matchInetSocketAddressPortWithExactValidate() throws UnknownHostException {
		InetSocketAddress socketAddress = new InetSocketAddress(InetAddress.getByName("192.168.1.1"), 443);
		PortConstraintConfig config = PortConstraintConfig.UNCONSTRAINED.withEqualTo(443);
		assertDoesNotThrow(() -> IOValidators.validateInetSocketAddressPort(socketAddress, Optional.of(config)));
	}
	
	@Test
	void validateInetSocketAddressPortWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOValidators.validateInetSocketAddressPort(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> IOValidators.validateInetSocketAddressPort(
			new InetSocketAddress(InetAddress.getByName("192.168.1.1"), 8080), null
		));
	}
	
	@Test
	void calculateUriPathDepthWithEmptyPath() {
		assertEquals(0, IOValidators.calculateUriPathDepth(""));
	}
	
	@Test
	void calculateUriPathDepthWithRootOnly() {
		assertEquals(0, IOValidators.calculateUriPathDepth("/"));
	}
	
	@Test
	void calculateUriPathDepthWithSegments() {
		assertEquals(3, IOValidators.calculateUriPathDepth("/some/path/file"));
	}
	
	@Test
	void calculateUriPathDepthWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOValidators.calculateUriPathDepth(null));
	}
	
	@Test
	void getUriPathSegmentsWithEmptyPath() {
		assertArrayEquals(new String[0], IOValidators.getUriPathSegments(""));
	}
	
	@Test
	void getUriPathSegmentsWithSegments() {
		assertArrayEquals(new String[] { "some", "path", "file" }, IOValidators.getUriPathSegments("/some/path/file"));
	}
	
	@Test
	void getUriPathSegmentsWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOValidators.getUriPathSegments(null));
	}
	
	@Test
	void getUriPathFileNameWithEmptyPath() {
		assertTrue(IOValidators.getUriPathFileName("").isEmpty());
	}
	
	@Test
	void getUriPathFileNameWithSegments() {
		assertEquals(Optional.of("file.txt"), IOValidators.getUriPathFileName("/some/path/file.txt"));
	}
	
	@Test
	void getUriPathFileNameWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOValidators.getUriPathFileName(null));
	}
	
	@Test
	void isUriPathNormalizedWithNormalizedPath() {
		assertTrue(IOValidators.isUriPathNormalized("/some/path/file"));
	}
	
	@Test
	void isUriPathNormalizedWithDotSegment() {
		assertFalse(IOValidators.isUriPathNormalized("/some/./path"));
	}
	
	@Test
	void isUriPathNormalizedWithDotDotSegment() {
		assertFalse(IOValidators.isUriPathNormalized("/some/../path"));
	}
	
	@Test
	void isUriPathNormalizedWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOValidators.isUriPathNormalized(null));
	}
}
