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
import net.luis.utils.util.result.Result;
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
		Result<Void> result = IOValidators.validatePortRange(8080, Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void validatePortRangeWithInRange() {
		Result<Void> result = IOValidators.validatePortRange(8080, Optional.of(Pair.of(Pair.of(8000, 9000), false)));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void validatePortRangeWithOutOfRange() {
		Result<Void> result = IOValidators.validatePortRange(7000, Optional.of(Pair.of(Pair.of(8000, 9000), false)));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("must be in range"));
	}
	
	@Test
	void validatePortRangeWithNegated() {
		Result<Void> result = IOValidators.validatePortRange(7000, Optional.of(Pair.of(Pair.of(8000, 9000), true)));
		assertTrue(result.isSuccess());
		Result<Void> negatedResult = IOValidators.validatePortRange(8080, Optional.of(Pair.of(Pair.of(8000, 9000), true)));
		assertTrue(negatedResult.isError());
		assertTrue(negatedResult.errorOrThrow().contains("must not be in range"));
	}
	
	@Test
	void validatePortRangeWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOValidators.validatePortRange(8080, null));
	}
	
	@Test
	void validatePortTypeWithEmptyOptional() {
		Result<Void> result = IOValidators.validatePortType(80, Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void validatePortTypeWithSystemPort() {
		EnumConstraintConfig<PortRange> config = EnumConstraintConfig.<PortRange>unconstrained().withEqualTo(PortRange.SYSTEM);
		Result<Void> result = IOValidators.validatePortType(80, Optional.of(config));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void validatePortTypeWithRegisteredPort() {
		EnumConstraintConfig<PortRange> config = EnumConstraintConfig.<PortRange>unconstrained().withEqualTo(PortRange.REGISTERED);
		Result<Void> result = IOValidators.validatePortType(8080, Optional.of(config));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void validatePortTypeWithDynamicPort() {
		EnumConstraintConfig<PortRange> config = EnumConstraintConfig.<PortRange>unconstrained().withEqualTo(PortRange.DYNAMIC);
		Result<Void> result = IOValidators.validatePortType(50000, Optional.of(config));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void validatePortTypeWithMismatch() {
		EnumConstraintConfig<PortRange> config = EnumConstraintConfig.<PortRange>unconstrained().withEqualTo(PortRange.SYSTEM);
		Result<Void> result = IOValidators.validatePortType(8080, Optional.of(config));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Port type constraint failed"));
	}
	
	@Test
	void validatePortTypeWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOValidators.validatePortType(80, null));
	}
	
	@Test
	void validateIpVersionWithEmptyOptional() {
		Result<Void> result = IOValidators.validateIpVersion("192.168.1.1", Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void validateIpVersionWithIpv4() {
		EnumConstraintConfig<IpVersion> config = EnumConstraintConfig.<IpVersion>unconstrained().withEqualTo(IpVersion.IPV4);
		Result<Void> result = IOValidators.validateIpVersion("192.168.1.1", Optional.of(config));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void validateIpVersionWithIpv6() {
		EnumConstraintConfig<IpVersion> config = EnumConstraintConfig.<IpVersion>unconstrained().withEqualTo(IpVersion.IPV6);
		Result<Void> result = IOValidators.validateIpVersion("2001:0db8::1", Optional.of(config));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void validateIpVersionWithMismatch() {
		EnumConstraintConfig<IpVersion> config = EnumConstraintConfig.<IpVersion>unconstrained().withEqualTo(IpVersion.IPV6);
		Result<Void> result = IOValidators.validateIpVersion("192.168.1.1", Optional.of(config));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("IP version constraint failed"));
	}
	
	@Test
	void validateIpVersionWithInvalidIp() {
		EnumConstraintConfig<IpVersion> config = EnumConstraintConfig.<IpVersion>unconstrained().withEqualTo(IpVersion.IPV4);
		Result<Void> result = IOValidators.validateIpVersion("not.an.ip", Optional.of(config));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("not a valid IP address"));
	}
	
	@Test
	void validateIpVersionWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOValidators.validateIpVersion(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> IOValidators.validateIpVersion("192.168.1.1", null));
	}
	
	@Test
	void validateIpTypeWithEmptyOptional() {
		Result<Void> result = IOValidators.validateIpType("192.168.1.1", Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void validateIpTypeWithPublicIpv4() {
		EnumConstraintConfig<IpAddressType> config = EnumConstraintConfig.<IpAddressType>unconstrained().withEqualTo(IpAddressType.PUBLIC);
		Result<Void> result = IOValidators.validateIpType("8.8.8.8", Optional.of(config));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void validateIpTypeWithPrivateIpv4() {
		EnumConstraintConfig<IpAddressType> config = EnumConstraintConfig.<IpAddressType>unconstrained().withEqualTo(IpAddressType.PRIVATE);
		Result<Void> result = IOValidators.validateIpType("192.168.1.1", Optional.of(config));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void validateIpTypeWithLoopbackIpv4() {
		EnumConstraintConfig<IpAddressType> config = EnumConstraintConfig.<IpAddressType>unconstrained().withEqualTo(IpAddressType.LOOPBACK);
		Result<Void> result = IOValidators.validateIpType("127.0.0.1", Optional.of(config));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void validateIpTypeWithLinkLocalIpv4() {
		EnumConstraintConfig<IpAddressType> config = EnumConstraintConfig.<IpAddressType>unconstrained().withEqualTo(IpAddressType.LINK_LOCAL);
		Result<Void> result = IOValidators.validateIpType("169.254.1.1", Optional.of(config));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void validateIpTypeWithMulticastIpv4() {
		EnumConstraintConfig<IpAddressType> config = EnumConstraintConfig.<IpAddressType>unconstrained().withEqualTo(IpAddressType.MULTICAST);
		Result<Void> result = IOValidators.validateIpType("224.0.0.1", Optional.of(config));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void validateIpTypeWithBroadcastIpv4() {
		EnumConstraintConfig<IpAddressType> config = EnumConstraintConfig.<IpAddressType>unconstrained().withEqualTo(IpAddressType.BROADCAST);
		Result<Void> result = IOValidators.validateIpType("255.255.255.255", Optional.of(config));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void validateIpTypeWithDocumentationIpv4() {
		EnumConstraintConfig<IpAddressType> config = EnumConstraintConfig.<IpAddressType>unconstrained().withEqualTo(IpAddressType.DOCUMENTATION);
		Result<Void> result = IOValidators.validateIpType("192.0.2.1", Optional.of(config));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void validateIpTypeWithUnspecifiedIpv4() {
		EnumConstraintConfig<IpAddressType> config = EnumConstraintConfig.<IpAddressType>unconstrained().withEqualTo(IpAddressType.UNSPECIFIED);
		Result<Void> result = IOValidators.validateIpType("0.0.0.0", Optional.of(config));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void validateIpTypeWithLoopbackIpv6() {
		EnumConstraintConfig<IpAddressType> config = EnumConstraintConfig.<IpAddressType>unconstrained().withEqualTo(IpAddressType.LOOPBACK);
		Result<Void> result = IOValidators.validateIpType("::1", Optional.of(config));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void validateIpTypeWithDocumentationIpv6() {
		EnumConstraintConfig<IpAddressType> config = EnumConstraintConfig.<IpAddressType>unconstrained().withEqualTo(IpAddressType.DOCUMENTATION);
		Result<Void> result = IOValidators.validateIpType("2001:db8::1", Optional.of(config));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void validateIpTypeWithMismatch() {
		EnumConstraintConfig<IpAddressType> config = EnumConstraintConfig.<IpAddressType>unconstrained().withEqualTo(IpAddressType.PUBLIC);
		Result<Void> result = IOValidators.validateIpType("192.168.1.1", Optional.of(config));
		assertTrue(result.isError());
	}
	
	@Test
	void validateIpTypeWithInvalidIp() {
		EnumConstraintConfig<IpAddressType> config = EnumConstraintConfig.<IpAddressType>unconstrained().withEqualTo(IpAddressType.PUBLIC);
		Result<Void> result = IOValidators.validateIpType("invalid", Optional.of(config));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("not a valid ip address"));
	}
	
	@Test
	void validateIpTypeWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOValidators.validateIpType(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> IOValidators.validateIpType("192.168.1.1", null));
	}
	
	@Test
	void validateInAnySubnetWithEmptyOptional() {
		Result<Void> result = IOValidators.validateInAnySubnet("192.168.1.1", Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void validateInAnySubnetWithIpv4InSubnet() {
		Result<Void> result = IOValidators.validateInAnySubnet("192.168.1.50", Optional.of(Pair.of(Set.of("192.168.1.0/24"), false)));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void validateInAnySubnetWithIpv4NotInSubnet() {
		Result<Void> result = IOValidators.validateInAnySubnet("10.0.0.1", Optional.of(Pair.of(Set.of("192.168.1.0/24"), false)));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("must be member of at least one specified subnet"));
	}
	
	@Test
	void validateInAnySubnetWithIpv6InSubnet() {
		Result<Void> result = IOValidators.validateInAnySubnet("2001:db8::1", Optional.of(Pair.of(Set.of("2001:db8::/32"), false)));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void validateInAnySubnetWithIpv6NotInSubnet() {
		Result<Void> result = IOValidators.validateInAnySubnet("2001:db9::1", Optional.of(Pair.of(Set.of("2001:db8::/32"), false)));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("must be member of at least one specified subnet"));
	}
	
	@Test
	void validateInAnySubnetWithMultipleSubnets() {
		Result<Void> result = IOValidators.validateInAnySubnet("10.0.0.1", Optional.of(Pair.of(Set.of("192.168.0.0/16", "10.0.0.0/8"), false)));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void validateInAnySubnetWithNegatedNotMember() {
		Result<Void> result = IOValidators.validateInAnySubnet("8.8.8.8", Optional.of(Pair.of(Set.of("192.168.0.0/16"), true)));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void validateInAnySubnetWithNegatedMember() {
		Result<Void> result = IOValidators.validateInAnySubnet("192.168.1.1", Optional.of(Pair.of(Set.of("192.168.0.0/16"), true)));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("must not be member of any specified subnet"));
	}
	
	@Test
	void validateInAnySubnetWithInvalidCidr() {
		Result<Void> result = IOValidators.validateInAnySubnet("192.168.1.1", Optional.of(Pair.of(Set.of("invalid"), false)));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Invalid CIDR"));
	}
	
	@Test
	void validateInAnySubnetWithInvalidIp() {
		Result<Void> result = IOValidators.validateInAnySubnet("invalid", Optional.of(Pair.of(Set.of("192.168.0.0/16"), false)));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("not a valid ip address"));
	}
	
	@Test
	void validateInAnySubnetWithMixedSubnets() {
		Result<Void> result = IOValidators.validateInAnySubnet("192.168.1.1", Optional.of(Pair.of(Set.of("192.168.0.0/16", "2001:db8::/32"), false)));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void validateInAnySubnetWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOValidators.validateInAnySubnet(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> IOValidators.validateInAnySubnet("192.168.1.1", null));
	}
	
	@Test
	void validateRootDomainWithEmptyOptional() {
		Result<Void> result = IOValidators.validateRootDomain("example.com", Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void validateRootDomainWithValidRootDomain() {
		Result<Void> result = IOValidators.validateRootDomain("example.com", Optional.of(Unit.INSTANCE));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void validateRootDomainWithSubDomain() {
		Result<Void> result = IOValidators.validateRootDomain("sub.example.com", Optional.of(Unit.INSTANCE));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("must be a root domain"));
	}
	
	@Test
	void validateRootDomainWithNoDot() {
		Result<Void> result = IOValidators.validateRootDomain("localhost", Optional.of(Unit.INSTANCE));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("must be a root domain"));
	}
	
	@Test
	void validateRootDomainWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOValidators.validateRootDomain(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> IOValidators.validateRootDomain("example.com", null));
	}
	
	@Test
	void validateSubDomainWithEmptyOptional() {
		Result<Void> result = IOValidators.validateSubDomain("sub.example.com", Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void validateSubDomainWithValidSubDomain() {
		Result<Void> result = IOValidators.validateSubDomain("sub.example.com", Optional.of(Unit.INSTANCE));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void validateSubDomainWithRootDomain() {
		Result<Void> result = IOValidators.validateSubDomain("example.com", Optional.of(Unit.INSTANCE));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("must be a subdomain"));
	}
	
	@Test
	void validateSubDomainWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOValidators.validateSubDomain(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> IOValidators.validateSubDomain("sub.example.com", null));
	}
	
	@Test
	void validatePathCanonicalWithEmptyOptional() {
		Result<Void> result = IOValidators.validatePathCanonical(Path.of("/some/path"), Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void validatePathCanonicalWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOValidators.validatePathCanonical(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> IOValidators.validatePathCanonical(Path.of("/some/path"), null));
	}
	
	@Test
	void validatePathStringConfigWithEmptyOptional() {
		Result<Void> result = IOValidators.validatePathStringConfig(Path.of("/some/path"), Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchPathStringConfigWithValidate() {
		String separator = File.separator;
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withStartsWith(separator);
		Result<Void> result = IOValidators.validatePathStringConfig(Path.of(separator + "some" + separator + "path"), Optional.of(config));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchPathStringConfigWithNoValidate() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withStartsWith("/usr");
		Result<Void> result = IOValidators.validatePathStringConfig(Path.of("/home/user"), Optional.of(config));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Path constraint failed"));
	}
	
	@Test
	void validatePathStringConfigWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOValidators.validatePathStringConfig(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> IOValidators.validatePathStringConfig(Path.of("/some/path"), null));
	}
	
	@Test
	void validatePathRootConfigWithEmptyOptional() {
		Result<Void> result = IOValidators.validatePathRootConfig(Path.of("/some/path"), Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void validatePathRootConfigWithNoRoot() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withEqualTo("/");
		Result<Void> result = IOValidators.validatePathRootConfig(Path.of("relative/path"), Optional.of(config));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("has no root component"));
	}
	
	@Test
	void validatePathRootConfigWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOValidators.validatePathRootConfig(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> IOValidators.validatePathRootConfig(Path.of("/some/path"), null));
	}
	
	@Test
	void validatePathParentConfigWithEmptyOptional() {
		Result<Void> result = IOValidators.validatePathParentConfig(Path.of("/some/path"), Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void validatePathParentConfigWithNoParent() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withContains("some");
		Result<Void> result = IOValidators.validatePathParentConfig(Path.of("file"), Optional.of(config));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("has no parent component"));
	}
	
	@Test
	void validatePathParentConfigWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOValidators.validatePathParentConfig(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> IOValidators.validatePathParentConfig(Path.of("/some/path"), null));
	}
	
	@Test
	void validatePathSegmentConfigWithEmptyOptional() {
		Result<Void> result = IOValidators.validatePathSegmentConfig(Path.of("/some/path"), Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchPathSegmentConfigWithValidate() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withAlphabetic();
		Result<Void> result = IOValidators.validatePathSegmentConfig(Path.of("some/path"), Optional.of(config));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchPathSegmentConfigWithNoValidate() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withNumeric();
		Result<Void> result = IOValidators.validatePathSegmentConfig(Path.of("some/path"), Optional.of(config));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Segment"));
	}
	
	@Test
	void validatePathSegmentConfigWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOValidators.validatePathSegmentConfig(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> IOValidators.validatePathSegmentConfig(Path.of("/some/path"), null));
	}
	
	@Test
	void validatePathFileNameConfigWithEmptyOptional() {
		Result<Void> result = IOValidators.validatePathFileNameConfig(Path.of("/some/file.txt"), Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchPathFileNameConfigWithValidate() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withEndsWith(".txt");
		Result<Void> result = IOValidators.validatePathFileNameConfig(Path.of("/some/file.txt"), Optional.of(config));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchPathFileNameConfigWithNoValidate() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withEndsWith(".java");
		Result<Void> result = IOValidators.validatePathFileNameConfig(Path.of("/some/file.txt"), Optional.of(config));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("File constraint failed"));
	}
	
	@Test
	void validatePathFileNameConfigWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOValidators.validatePathFileNameConfig(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> IOValidators.validatePathFileNameConfig(Path.of("/some/file.txt"), null));
	}
	
	@Test
	void validatePathExtensionConfigWithEmptyOptional() {
		Result<Void> result = IOValidators.validatePathExtensionConfig(Path.of("/some/file.txt"), Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchPathExtensionConfigWithValidate() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withEqualTo("txt");
		Result<Void> result = IOValidators.validatePathExtensionConfig(Path.of("/some/file.txt"), Optional.of(config));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void validatePathExtensionConfigWithNoExtension() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withEqualTo("txt");
		Result<Void> result = IOValidators.validatePathExtensionConfig(Path.of("/some/file"), Optional.of(config));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("has no extension"));
	}
	
	@Test
	void validatePathExtensionConfigWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOValidators.validatePathExtensionConfig(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> IOValidators.validatePathExtensionConfig(Path.of("/some/file.txt"), null));
	}
	
	@Test
	void validatePathWithoutExtensionWithEmptyOptional() {
		Result<Void> result = IOValidators.validatePathWithoutExtension(Path.of("/some/file.txt"), Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void validatePathWithoutExtensionWithNoExtension() {
		Result<Void> result = IOValidators.validatePathWithoutExtension(Path.of("/some/file"), Optional.of(Unit.INSTANCE));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void validatePathWithoutExtensionWithExtension() {
		Result<Void> result = IOValidators.validatePathWithoutExtension(Path.of("/some/file.txt"), Optional.of(Unit.INSTANCE));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("must not have an extension"));
	}
	
	@Test
	void validatePathWithoutExtensionWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOValidators.validatePathWithoutExtension(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> IOValidators.validatePathWithoutExtension(Path.of("/some/file"), null));
	}
	
	@Test
	void validatePathAncestorOfWithEmptyOptional() {
		Result<Void> result = IOValidators.validatePathAncestorOf(Path.of("/some"), Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void validatePathAncestorOfWithValid() {
		Result<Void> result = IOValidators.validatePathAncestorOf(Path.of("/some"), Optional.of(Set.of("/some/path", "/some/other")));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void validatePathAncestorOfWithInvalid() {
		Result<Void> result = IOValidators.validatePathAncestorOf(Path.of("/other"), Optional.of(Set.of("/some/path")));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("must be ancestor of"));
	}
	
	@Test
	void validatePathAncestorOfWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOValidators.validatePathAncestorOf(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> IOValidators.validatePathAncestorOf(Path.of("/some"), null));
	}
	
	@Test
	void validatePathDescendantOfWithEmptyOptional() {
		Result<Void> result = IOValidators.validatePathDescendantOf(Path.of("/some/path/file"), Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void validatePathDescendantOfWithValid() {
		Result<Void> result = IOValidators.validatePathDescendantOf(Path.of("/some/path/file"), Optional.of(Set.of("/some", "/some/path")));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void validatePathDescendantOfWithInvalid() {
		Result<Void> result = IOValidators.validatePathDescendantOf(Path.of("/other/path"), Optional.of(Set.of("/some")));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("must be descendant of"));
	}
	
	@Test
	void validatePathDescendantOfWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOValidators.validatePathDescendantOf(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> IOValidators.validatePathDescendantOf(Path.of("/some/path"), null));
	}
	
	@Test
	void validateQueryValueConstraintsWithEmptyOptional() {
		Map<String, List<String>> query = Map.of("key", List.of("value"));
		Result<Void> result = IOValidators.validateQueryValueConstraints(query, Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchQueryValueConstraintsWithValidate() {
		Map<String, List<String>> query = Map.of("key", List.of("value"));
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withEqualTo("value");
		Result<Void> result = IOValidators.validateQueryValueConstraints(query, Optional.of(Map.of("key", config)));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchQueryValueConstraintsWithNoValidate() {
		Map<String, List<String>> query = Map.of("key", List.of("wrong"));
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withEqualTo("value");
		Result<Void> result = IOValidators.validateQueryValueConstraints(query, Optional.of(Map.of("key", config)));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Value constraint for key"));
	}
	
	@Test
	void validateQueryValueConstraintsWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOValidators.validateQueryValueConstraints(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> IOValidators.validateQueryValueConstraints(Map.of(), null));
	}
	
	@Test
	void validateQueryPatternValueConstraintsWithEmptyOptional() {
		Map<String, List<String>> query = Map.of("key1", List.of("value"));
		Result<Void> result = IOValidators.validateQueryPatternValueConstraints(query, Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchQueryPatternValueConstraintsWithValidate() {
		Map<String, List<String>> query = Map.of("key1", List.of("value"));
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withAlphabetic();
		Result<Void> result = IOValidators.validateQueryPatternValueConstraints(query, Optional.of(Map.of(Pattern.compile("key\\d+"), config)));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchQueryPatternValueConstraintsWithNoValidate() {
		Map<String, List<String>> query = Map.of("key1", List.of("123"));
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withAlphabetic();
		Result<Void> result = IOValidators.validateQueryPatternValueConstraints(query, Optional.of(Map.of(Pattern.compile("key\\d+"), config)));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Pattern value constraint"));
	}
	
	@Test
	void validateQueryPatternValueConstraintsWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOValidators.validateQueryPatternValueConstraints(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> IOValidators.validateQueryPatternValueConstraints(Map.of(), null));
	}
	
	@Test
	void validateQuerySingleValuedWithEmptyOptional() {
		Map<String, List<String>> query = Map.of("key", List.of("value1", "value2"));
		Result<Void> result = IOValidators.validateQuerySingleValued(query, Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void validateQuerySingleValuedWithSingleValues() {
		Map<String, List<String>> query = Map.of("key1", List.of("value1"), "key2", List.of("value2"));
		Result<Void> result = IOValidators.validateQuerySingleValued(query, Optional.of(Unit.INSTANCE));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void validateQuerySingleValuedWithMultipleValues() {
		Map<String, List<String>> query = Map.of("key", List.of("value1", "value2"));
		Result<Void> result = IOValidators.validateQuerySingleValued(query, Optional.of(Unit.INSTANCE));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("must have exactly one value"));
	}
	
	@Test
	void validateQuerySingleValuedWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOValidators.validateQuerySingleValued(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> IOValidators.validateQuerySingleValued(Map.of(), null));
	}
	
	@Test
	void validateQueryUniqueValuesWithEmptyOptional() {
		Map<String, List<String>> query = Map.of("key1", List.of("value"), "key2", List.of("value"));
		Result<Void> result = IOValidators.validateQueryUniqueValues(query, Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void validateQueryUniqueValuesWithUnique() {
		Map<String, List<String>> query = Map.of("key1", List.of("value1"), "key2", List.of("value2"));
		Result<Void> result = IOValidators.validateQueryUniqueValues(query, Optional.of(Unit.INSTANCE));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void validateQueryUniqueValuesWithDuplicates() {
		Map<String, List<String>> query = Map.of("key1", List.of("value", "other"), "key2", List.of("value"));
		Result<Void> result = IOValidators.validateQueryUniqueValues(query, Optional.of(Unit.INSTANCE));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("must be unique"));
	}
	
	@Test
	void validateQueryUniqueValuesWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOValidators.validateQueryUniqueValues(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> IOValidators.validateQueryUniqueValues(Map.of(), null));
	}
	
	@Test
	void validateQueryMultiValuedConstraintsWithEmptyOptional() {
		Map<String, List<String>> query = Map.of("key", List.of("v1", "v2", "v3"));
		Result<Void> result = IOValidators.validateQueryMultiValuedConstraints(query, Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchQueryMultiValuedConstraintsWithValidate() {
		Map<String, List<String>> query = Map.of("key", List.of("v1", "v2"));
		SizeConstraintConfig config = SizeConstraintConfig.UNCONSTRAINED.withSizeBetween(1, 3);
		Result<Void> result = IOValidators.validateQueryMultiValuedConstraints(query, Optional.of(Map.of("key", config)));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchQueryMultiValuedConstraintsWithNoValidate() {
		Map<String, List<String>> query = Map.of("key", List.of("v1", "v2", "v3", "v4", "v5"));
		SizeConstraintConfig config = SizeConstraintConfig.UNCONSTRAINED.withMaxSize(3);
		Result<Void> result = IOValidators.validateQueryMultiValuedConstraints(query, Optional.of(Map.of("key", config)));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Multi-valued constraint"));
	}
	
	@Test
	void validateQueryMultiValuedConstraintsWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOValidators.validateQueryMultiValuedConstraints(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> IOValidators.validateQueryMultiValuedConstraints(Map.of(), null));
	}
	
	@Test
	void validateUriSchemeConfigWithEmptyOptional() {
		URI uri = URI.create("https://example.com");
		Result<Void> result = IOValidators.validateUriSchemeConfig(uri, Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchUriSchemeConfigWithValidate() {
		URI uri = URI.create("https://example.com");
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withEqualTo("https");
		Result<Void> result = IOValidators.validateUriSchemeConfig(uri, Optional.of(config));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void validateUriSchemeConfigWithNoScheme() {
		URI uri = URI.create("//example.com");
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withEqualTo("https");
		Result<Void> result = IOValidators.validateUriSchemeConfig(uri, Optional.of(config));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("has no scheme"));
	}
	
	@Test
	void validateUriSchemeConfigWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOValidators.validateUriSchemeConfig(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> IOValidators.validateUriSchemeConfig(URI.create("https://example.com"), null));
	}
	
	@Test
	void validateUriHostConfigWithEmptyOptional() {
		URI uri = URI.create("https://example.com");
		Result<Void> result = IOValidators.validateUriHostConfig(uri, Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchUriHostConfigWithValidate() {
		URI uri = URI.create("https://example.com");
		HostConstraintConfig config = HostConstraintConfig.UNCONSTRAINED.withEqualTo("example.com");
		Result<Void> result = IOValidators.validateUriHostConfig(uri, Optional.of(config));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchUriHostConfigWithNoValidate() {
		URI uri = URI.create("https://example.com");
		HostConstraintConfig config = HostConstraintConfig.UNCONSTRAINED.withEqualTo("other.com");
		Result<Void> result = IOValidators.validateUriHostConfig(uri, Optional.of(config));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Host constraint failed"));
	}
	
	@Test
	void validateUriHostConfigWithNoHost() {
		URI uri = URI.create("file:/path/to/file");
		HostConstraintConfig config = HostConstraintConfig.UNCONSTRAINED.withEqualTo("example.com");
		Result<Void> result = IOValidators.validateUriHostConfig(uri, Optional.of(config));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("has no host"));
	}
	
	@Test
	void validateUriHostConfigWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOValidators.validateUriHostConfig(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> IOValidators.validateUriHostConfig(URI.create("https://example.com"), null));
	}
	
	@Test
	void validateUriUserInfoConfigWithEmptyOptional() {
		URI uri = URI.create("https://user:pass@example.com");
		Result<Void> result = IOValidators.validateUriUserInfoConfig(uri, Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchUriUserInfoConfigWithValidate() {
		URI uri = URI.create("https://user:pass@example.com");
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withContains("user");
		Result<Void> result = IOValidators.validateUriUserInfoConfig(uri, Optional.of(config));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void validateUriUserInfoConfigWithNoUserInfo() {
		URI uri = URI.create("https://example.com");
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withContains("user");
		Result<Void> result = IOValidators.validateUriUserInfoConfig(uri, Optional.of(config));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("has no user info"));
	}
	
	@Test
	void validateUriUserInfoConfigWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOValidators.validateUriUserInfoConfig(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> IOValidators.validateUriUserInfoConfig(URI.create("https://example.com"), null));
	}
	
	@Test
	void validateUriPortConfigWithEmptyOptional() {
		URI uri = URI.create("https://example.com:8080");
		Result<Void> result = IOValidators.validateUriPortConfig(uri, Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchUriPortConfigWithValidate() {
		URI uri = URI.create("https://example.com:8080");
		PortConstraintConfig config = PortConstraintConfig.UNCONSTRAINED.withEqualTo(8080);
		Result<Void> result = IOValidators.validateUriPortConfig(uri, Optional.of(config));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchUriPortConfigWithNoValidate() {
		URI uri = URI.create("https://example.com:8080");
		PortConstraintConfig config = PortConstraintConfig.UNCONSTRAINED.withEqualTo(443);
		Result<Void> result = IOValidators.validateUriPortConfig(uri, Optional.of(config));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Port constraint failed"));
	}
	
	@Test
	void validateUriPortConfigWithNoPort() {
		URI uri = URI.create("https://example.com");
		PortConstraintConfig config = PortConstraintConfig.UNCONSTRAINED.withEqualTo(8080);
		Result<Void> result = IOValidators.validateUriPortConfig(uri, Optional.of(config));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("has no port"));
	}
	
	@Test
	void validateUriPortConfigWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOValidators.validateUriPortConfig(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> IOValidators.validateUriPortConfig(URI.create("https://example.com:8080"), null));
	}
	
	@Test
	void validateUriPathConfigWithEmptyOptional() {
		URI uri = URI.create("https://example.com/path/to/resource");
		Result<Void> result = IOValidators.validateUriPathConfig(uri, Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchUriPathConfigWithValidate() {
		URI uri = URI.create("https://example.com/path/to/resource");
		URIPathConstraintConfig config = URIPathConstraintConfig.UNCONSTRAINED.withDepth(
			DepthConstraintConfig.UNCONSTRAINED.withMinDepth(2)
		);
		Result<Void> result = IOValidators.validateUriPathConfig(uri, Optional.of(config));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchUriPathConfigWithNoValidate() {
		URI uri = URI.create("https://example.com/path");
		URIPathConstraintConfig config = URIPathConstraintConfig.UNCONSTRAINED.withDepth(
			DepthConstraintConfig.UNCONSTRAINED.withMinDepth(3)
		);
		Result<Void> result = IOValidators.validateUriPathConfig(uri, Optional.of(config));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Path constraint failed"));
	}
	
	@Test
	void validateUriPathConfigWithNoPath() {
		URI uri = URI.create("https://example.com");
		URIPathConstraintConfig config = URIPathConstraintConfig.UNCONSTRAINED.withDepth(
			DepthConstraintConfig.UNCONSTRAINED.withMinDepth(1)
		);
		Result<Void> result = IOValidators.validateUriPathConfig(uri, Optional.of(config));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("has no path"));
	}
	
	@Test
	void validateUriPathConfigWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOValidators.validateUriPathConfig(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> IOValidators.validateUriPathConfig(URI.create("https://example.com/path"), null));
	}
	
	@Test
	void validateUriQueryConfigWithEmptyOptional() {
		URI uri = URI.create("https://example.com?key=value");
		Result<Void> result = IOValidators.validateUriQueryConfig(uri, Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchUriQueryConfigWithValidate() {
		URI uri = URI.create("https://example.com?key=value");
		QueryConstraintConfig config = QueryConstraintConfig.UNCONSTRAINED.withRequiredKeys(List.of("key"));
		Result<Void> result = IOValidators.validateUriQueryConfig(uri, Optional.of(config));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchUriQueryConfigWithNoValidate() {
		URI uri = URI.create("https://example.com?key=value");
		QueryConstraintConfig config = QueryConstraintConfig.UNCONSTRAINED.withRequiredKeys(List.of("missing"));
		Result<Void> result = IOValidators.validateUriQueryConfig(uri, Optional.of(config));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Query constraint failed"));
	}
	
	@Test
	void validateUriQueryConfigWithNoQuery() {
		URI uri = URI.create("https://example.com");
		QueryConstraintConfig config = QueryConstraintConfig.UNCONSTRAINED.withSize(SizeConstraintConfig.UNCONSTRAINED.withMinSize(1));
		Result<Void> result = IOValidators.validateUriQueryConfig(uri, Optional.of(config));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("has no query"));
	}
	
	@Test
	void validateUriQueryConfigWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOValidators.validateUriQueryConfig(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> IOValidators.validateUriQueryConfig(URI.create("https://example.com?key=value"), null));
	}
	
	@Test
	void validateUriFragmentConfigWithEmptyOptional() {
		URI uri = URI.create("https://example.com#section");
		Result<Void> result = IOValidators.validateUriFragmentConfig(uri, Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchUriFragmentConfigWithValidate() {
		URI uri = URI.create("https://example.com#section");
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withEqualTo("section");
		Result<Void> result = IOValidators.validateUriFragmentConfig(uri, Optional.of(config));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void validateUriFragmentConfigWithNoFragment() {
		URI uri = URI.create("https://example.com");
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withEqualTo("section");
		Result<Void> result = IOValidators.validateUriFragmentConfig(uri, Optional.of(config));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("has no fragment"));
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
		Result<Void> result = IOValidators.validateUriPathStringConfig("/some/path", Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchUriPathStringConfigWithValidate() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withStartsWith("/");
		Result<Void> result = IOValidators.validateUriPathStringConfig("/some/path", Optional.of(config));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchUriPathStringConfigWithNoValidate() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withStartsWith("/usr");
		Result<Void> result = IOValidators.validateUriPathStringConfig("/home/user", Optional.of(config));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Path constraint failed"));
	}
	
	@Test
	void validateUriPathStringConfigWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOValidators.validateUriPathStringConfig(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> IOValidators.validateUriPathStringConfig("/some/path", null));
	}
	
	@Test
	void validateUriPathSegmentConfigWithEmptyOptional() {
		Result<Void> result = IOValidators.validateUriPathSegmentConfig("/some/path", Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchUriPathSegmentConfigWithValidate() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withAlphabetic();
		Result<Void> result = IOValidators.validateUriPathSegmentConfig("/some/path", Optional.of(config));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchUriPathSegmentConfigWithNoValidate() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withNumeric();
		Result<Void> result = IOValidators.validateUriPathSegmentConfig("/some/path", Optional.of(config));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Segment"));
	}
	
	@Test
	void validateUriPathSegmentConfigWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOValidators.validateUriPathSegmentConfig(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> IOValidators.validateUriPathSegmentConfig("/some/path", null));
	}
	
	@Test
	void validateUriPathFileNameConfigWithEmptyOptional() {
		Result<Void> result = IOValidators.validateUriPathFileNameConfig("/some/file.txt", Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchUriPathFileNameConfigWithValidate() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withEndsWith(".txt");
		Result<Void> result = IOValidators.validateUriPathFileNameConfig("/some/file.txt", Optional.of(config));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchUriPathFileNameConfigWithNoValidate() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withEndsWith(".java");
		Result<Void> result = IOValidators.validateUriPathFileNameConfig("/some/file.txt", Optional.of(config));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("File constraint failed"));
	}
	
	@Test
	void validateUriPathFileNameConfigWithNoFileName() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withEqualTo("test");
		Result<Void> result = IOValidators.validateUriPathFileNameConfig("", Optional.of(config));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("has no file name"));
	}
	
	@Test
	void validateUriPathFileNameConfigWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOValidators.validateUriPathFileNameConfig(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> IOValidators.validateUriPathFileNameConfig("/some/file.txt", null));
	}
	
	@Test
	void validateUriPathWithoutExtensionWithEmptyOptional() {
		Result<Void> result = IOValidators.validateUriPathWithoutExtension("/some/file.txt", Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void validateUriPathWithoutExtensionWithNoExtension() {
		Result<Void> result = IOValidators.validateUriPathWithoutExtension("/some/file", Optional.of(Unit.INSTANCE));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void validateUriPathWithoutExtensionWithExtension() {
		Result<Void> result = IOValidators.validateUriPathWithoutExtension("/some/file.txt", Optional.of(Unit.INSTANCE));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("must not have an extension"));
	}
	
	@Test
	void validateUriPathWithoutExtensionWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOValidators.validateUriPathWithoutExtension(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> IOValidators.validateUriPathWithoutExtension("/some/file", null));
	}
	
	@Test
	void validateUriPathExtensionConfigWithEmptyOptional() {
		Result<Void> result = IOValidators.validateUriPathExtensionConfig("/some/file.txt", Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchUriPathExtensionConfigWithValidate() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withEqualTo("txt");
		Result<Void> result = IOValidators.validateUriPathExtensionConfig("/some/file.txt", Optional.of(config));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void validateUriPathExtensionConfigWithNoExtension() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withEqualTo("txt");
		Result<Void> result = IOValidators.validateUriPathExtensionConfig("/some/file", Optional.of(config));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("has no extension"));
	}
	
	@Test
	void validateUriPathExtensionConfigWithNoFileName() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withEqualTo("txt");
		Result<Void> result = IOValidators.validateUriPathExtensionConfig("", Optional.of(config));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("has no file name"));
	}
	
	@Test
	void validateUriPathExtensionConfigWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOValidators.validateUriPathExtensionConfig(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> IOValidators.validateUriPathExtensionConfig("/some/file.txt", null));
	}
	
	@Test
	void validateUriPathAncestorOfWithEmptyOptional() {
		Result<Void> result = IOValidators.validateUriPathAncestorOf("/some", Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void validateUriPathAncestorOfWithValid() {
		Result<Void> result = IOValidators.validateUriPathAncestorOf("/some", Optional.of(Set.of("/some/path", "/some/other")));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void validateUriPathAncestorOfWithInvalid() {
		Result<Void> result = IOValidators.validateUriPathAncestorOf("/other", Optional.of(Set.of("/some/path")));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("must be ancestor of"));
	}
	
	@Test
	void validateUriPathAncestorOfWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOValidators.validateUriPathAncestorOf(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> IOValidators.validateUriPathAncestorOf("/some", null));
	}
	
	@Test
	void validateUriPathDescendantOfWithEmptyOptional() {
		Result<Void> result = IOValidators.validateUriPathDescendantOf("/some/path/file", Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void validateUriPathDescendantOfWithValid() {
		Result<Void> result = IOValidators.validateUriPathDescendantOf("/some/path/file", Optional.of(Set.of("/some", "/some/path")));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void validateUriPathDescendantOfWithInvalid() {
		Result<Void> result = IOValidators.validateUriPathDescendantOf("/other/path", Optional.of(Set.of("/some")));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("must be descendant of"));
	}
	
	@Test
	void validateUriPathDescendantOfWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOValidators.validateUriPathDescendantOf(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> IOValidators.validateUriPathDescendantOf("/some/path", null));
	}
	
	@Test
	void validateInetAddressIpVersionWithEmptyOptional() throws UnknownHostException {
		InetAddress address = InetAddress.getByName("192.168.1.1");
		Result<Void> result = IOValidators.validateInetAddressIpVersion(address, Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void validateInetAddressIpVersionWithIpv4() throws UnknownHostException {
		InetAddress address = InetAddress.getByName("192.168.1.1");
		EnumConstraintConfig<IpVersion> config = EnumConstraintConfig.<IpVersion>unconstrained().withEqualTo(IpVersion.IPV4);
		Result<Void> result = IOValidators.validateInetAddressIpVersion(address, Optional.of(config));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void validateInetAddressIpVersionWithIpv6() throws UnknownHostException {
		InetAddress address = InetAddress.getByName("::1");
		EnumConstraintConfig<IpVersion> config = EnumConstraintConfig.<IpVersion>unconstrained().withEqualTo(IpVersion.IPV6);
		Result<Void> result = IOValidators.validateInetAddressIpVersion(address, Optional.of(config));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void validateInetAddressIpVersionWithMismatch() throws UnknownHostException {
		InetAddress address = InetAddress.getByName("192.168.1.1");
		EnumConstraintConfig<IpVersion> config = EnumConstraintConfig.<IpVersion>unconstrained().withEqualTo(IpVersion.IPV6);
		Result<Void> result = IOValidators.validateInetAddressIpVersion(address, Optional.of(config));
		assertTrue(result.isError());
	}
	
	@Test
	void validateInetAddressIpVersionWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOValidators.validateInetAddressIpVersion(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> IOValidators.validateInetAddressIpVersion(InetAddress.getByName("192.168.1.1"), null));
	}
	
	@Test
	void validateInetAddressIpTypeWithEmptyOptional() throws UnknownHostException {
		InetAddress address = InetAddress.getByName("192.168.1.1");
		Result<Void> result = IOValidators.validateInetAddressIpType(address, Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void validateInetAddressIpTypeWithPrivate() throws UnknownHostException {
		InetAddress address = InetAddress.getByName("192.168.1.1");
		EnumConstraintConfig<IpAddressType> config = EnumConstraintConfig.<IpAddressType>unconstrained().withEqualTo(IpAddressType.PRIVATE);
		Result<Void> result = IOValidators.validateInetAddressIpType(address, Optional.of(config));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void validateInetAddressIpTypeWithLoopback() throws UnknownHostException {
		InetAddress address = InetAddress.getByName("127.0.0.1");
		EnumConstraintConfig<IpAddressType> config = EnumConstraintConfig.<IpAddressType>unconstrained().withEqualTo(IpAddressType.LOOPBACK);
		Result<Void> result = IOValidators.validateInetAddressIpType(address, Optional.of(config));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void validateInetAddressIpTypeWithLoopbackIpv6() throws UnknownHostException {
		InetAddress address = InetAddress.getByName("::1");
		EnumConstraintConfig<IpAddressType> config = EnumConstraintConfig.<IpAddressType>unconstrained().withEqualTo(IpAddressType.LOOPBACK);
		Result<Void> result = IOValidators.validateInetAddressIpType(address, Optional.of(config));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void validateInetAddressIpTypeWithMismatch() throws UnknownHostException {
		InetAddress address = InetAddress.getByName("192.168.1.1");
		EnumConstraintConfig<IpAddressType> config = EnumConstraintConfig.<IpAddressType>unconstrained().withEqualTo(IpAddressType.LOOPBACK);
		Result<Void> result = IOValidators.validateInetAddressIpType(address, Optional.of(config));
		assertTrue(result.isError());
	}
	
	@Test
	void validateInetAddressIpTypeWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOValidators.validateInetAddressIpType(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> IOValidators.validateInetAddressIpType(InetAddress.getByName("192.168.1.1"), null));
	}
	
	@Test
	void validateInetAddressInAnySubnetWithEmptyOptional() throws UnknownHostException {
		InetAddress address = InetAddress.getByName("192.168.1.1");
		Result<Void> result = IOValidators.validateInetAddressInAnySubnet(address, Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchInetAddressInAnySubnetWithValidate() throws UnknownHostException {
		InetAddress address = InetAddress.getByName("192.168.1.50");
		Result<Void> result = IOValidators.validateInetAddressInAnySubnet(address, Optional.of(Pair.of(Set.of("192.168.1.0/24"), false)));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchInetAddressInAnySubnetWithNoValidate() throws UnknownHostException {
		InetAddress address = InetAddress.getByName("10.0.0.1");
		Result<Void> result = IOValidators.validateInetAddressInAnySubnet(address, Optional.of(Pair.of(Set.of("192.168.1.0/24"), false)));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("must be member of at least one specified subnet"));
	}
	
	@Test
	void validateInetAddressInAnySubnetWithNegated() throws UnknownHostException {
		InetAddress address = InetAddress.getByName("10.0.0.1");
		Result<Void> result = IOValidators.validateInetAddressInAnySubnet(address, Optional.of(Pair.of(Set.of("192.168.0.0/16"), true)));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void validateInetAddressInAnySubnetWithNegatedMember() throws UnknownHostException {
		InetAddress address = InetAddress.getByName("192.168.1.1");
		Result<Void> result = IOValidators.validateInetAddressInAnySubnet(address, Optional.of(Pair.of(Set.of("192.168.0.0/16"), true)));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("must not be member of any specified subnet"));
	}
	
	@Test
	void validateInetAddressInAnySubnetWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOValidators.validateInetAddressInAnySubnet(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> IOValidators.validateInetAddressInAnySubnet(InetAddress.getByName("192.168.1.1"), null));
	}
	
	@Test
	void validateInetSocketAddressAddressWithEmptyOptional() throws UnknownHostException {
		InetSocketAddress socketAddress = new InetSocketAddress(InetAddress.getByName("192.168.1.1"), 8080);
		Result<Void> result = IOValidators.validateInetSocketAddressAddress(socketAddress, Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchInetSocketAddressAddressWithValidate() throws UnknownHostException {
		InetSocketAddress socketAddress = new InetSocketAddress(InetAddress.getByName("192.168.1.1"), 8080);
		InetAddressConstraintConfig config = InetAddressConstraintConfig.UNCONSTRAINED.withIpType(
			EnumConstraintConfig.<IpAddressType>unconstrained().withEqualTo(IpAddressType.PRIVATE)
		);
		Result<Void> result = IOValidators.validateInetSocketAddressAddress(socketAddress, Optional.of(config));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchInetSocketAddressAddressWithNoValidate() throws UnknownHostException {
		InetSocketAddress socketAddress = new InetSocketAddress(InetAddress.getByName("127.0.0.1"), 8080);
		InetAddressConstraintConfig config = InetAddressConstraintConfig.UNCONSTRAINED.withIpType(
			EnumConstraintConfig.<IpAddressType>unconstrained().withEqualTo(IpAddressType.PRIVATE)
		);
		Result<Void> result = IOValidators.validateInetSocketAddressAddress(socketAddress, Optional.of(config));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Address constraint failed"));
	}
	
	@Test
	void validateInetSocketAddressAddressWithUnresolved() {
		InetSocketAddress socketAddress = InetSocketAddress.createUnresolved("nonexistent.invalid", 8080);
		InetAddressConstraintConfig config = InetAddressConstraintConfig.UNCONSTRAINED;
		Result<Void> result = IOValidators.validateInetSocketAddressAddress(socketAddress, Optional.of(config));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("has no resolved address"));
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
		Result<Void> result = IOValidators.validateInetSocketAddressPort(socketAddress, Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchInetSocketAddressPortWithValidate() throws UnknownHostException {
		InetSocketAddress socketAddress = new InetSocketAddress(InetAddress.getByName("192.168.1.1"), 8080);
		PortConstraintConfig config = PortConstraintConfig.UNCONSTRAINED.withInRange(8000, 9000);
		Result<Void> result = IOValidators.validateInetSocketAddressPort(socketAddress, Optional.of(config));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchInetSocketAddressPortWithNoValidate() throws UnknownHostException {
		InetSocketAddress socketAddress = new InetSocketAddress(InetAddress.getByName("192.168.1.1"), 80);
		PortConstraintConfig config = PortConstraintConfig.UNCONSTRAINED.withInRange(8000, 9000);
		Result<Void> result = IOValidators.validateInetSocketAddressPort(socketAddress, Optional.of(config));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Port constraint failed"));
	}
	
	@Test
	void matchInetSocketAddressPortWithExactValidate() throws UnknownHostException {
		InetSocketAddress socketAddress = new InetSocketAddress(InetAddress.getByName("192.168.1.1"), 443);
		PortConstraintConfig config = PortConstraintConfig.UNCONSTRAINED.withEqualTo(443);
		Result<Void> result = IOValidators.validateInetSocketAddressPort(socketAddress, Optional.of(config));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void validateInetSocketAddressPortWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOValidators.validateInetSocketAddressPort(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> IOValidators.validateInetSocketAddressPort(
			new InetSocketAddress(InetAddress.getByName("192.168.1.1"), 8080), null
		));
	}
}
