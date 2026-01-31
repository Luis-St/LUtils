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
 * Test class for {@link IOMatchers}.<br>
 *
 * @author Luis-St
 */
class IOMatchersTest {
	
	@Test
	void matchPortRangeWithEmptyOptional() {
		Result<Void> result = IOMatchers.matchPortRange(8080, Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchPortRangeWithInRange() {
		Result<Void> result = IOMatchers.matchPortRange(8080, Optional.of(Pair.of(Pair.of(8000, 9000), false)));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchPortRangeWithOutOfRange() {
		Result<Void> result = IOMatchers.matchPortRange(7000, Optional.of(Pair.of(Pair.of(8000, 9000), false)));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("must be in range"));
	}
	
	@Test
	void matchPortRangeWithNegated() {
		Result<Void> result = IOMatchers.matchPortRange(7000, Optional.of(Pair.of(Pair.of(8000, 9000), true)));
		assertTrue(result.isSuccess());
		Result<Void> negatedResult = IOMatchers.matchPortRange(8080, Optional.of(Pair.of(Pair.of(8000, 9000), true)));
		assertTrue(negatedResult.isError());
		assertTrue(negatedResult.errorOrThrow().contains("must not be in range"));
	}
	
	@Test
	void matchPortRangeWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOMatchers.matchPortRange(8080, null));
	}
	
	@Test
	void matchPortTypeWithEmptyOptional() {
		Result<Void> result = IOMatchers.matchPortType(80, Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchPortTypeWithSystemPort() {
		EnumConstraintConfig<PortRange> config = EnumConstraintConfig.<PortRange>unconstrained().withEqualTo(PortRange.SYSTEM);
		Result<Void> result = IOMatchers.matchPortType(80, Optional.of(config));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchPortTypeWithRegisteredPort() {
		EnumConstraintConfig<PortRange> config = EnumConstraintConfig.<PortRange>unconstrained().withEqualTo(PortRange.REGISTERED);
		Result<Void> result = IOMatchers.matchPortType(8080, Optional.of(config));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchPortTypeWithDynamicPort() {
		EnumConstraintConfig<PortRange> config = EnumConstraintConfig.<PortRange>unconstrained().withEqualTo(PortRange.DYNAMIC);
		Result<Void> result = IOMatchers.matchPortType(50000, Optional.of(config));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchPortTypeWithMismatch() {
		EnumConstraintConfig<PortRange> config = EnumConstraintConfig.<PortRange>unconstrained().withEqualTo(PortRange.SYSTEM);
		Result<Void> result = IOMatchers.matchPortType(8080, Optional.of(config));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Port type constraint failed"));
	}
	
	@Test
	void matchPortTypeWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOMatchers.matchPortType(80, null));
	}
	
	@Test
	void matchIpVersionWithEmptyOptional() {
		Result<Void> result = IOMatchers.matchIpVersion("192.168.1.1", Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchIpVersionWithIpv4() {
		EnumConstraintConfig<IpVersion> config = EnumConstraintConfig.<IpVersion>unconstrained().withEqualTo(IpVersion.IPV4);
		Result<Void> result = IOMatchers.matchIpVersion("192.168.1.1", Optional.of(config));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchIpVersionWithIpv6() {
		EnumConstraintConfig<IpVersion> config = EnumConstraintConfig.<IpVersion>unconstrained().withEqualTo(IpVersion.IPV6);
		Result<Void> result = IOMatchers.matchIpVersion("2001:0db8::1", Optional.of(config));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchIpVersionWithMismatch() {
		EnumConstraintConfig<IpVersion> config = EnumConstraintConfig.<IpVersion>unconstrained().withEqualTo(IpVersion.IPV6);
		Result<Void> result = IOMatchers.matchIpVersion("192.168.1.1", Optional.of(config));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("IP version constraint failed"));
	}
	
	@Test
	void matchIpVersionWithInvalidIp() {
		EnumConstraintConfig<IpVersion> config = EnumConstraintConfig.<IpVersion>unconstrained().withEqualTo(IpVersion.IPV4);
		Result<Void> result = IOMatchers.matchIpVersion("not.an.ip", Optional.of(config));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("not a valid IP address"));
	}
	
	@Test
	void matchIpVersionWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOMatchers.matchIpVersion(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> IOMatchers.matchIpVersion("192.168.1.1", null));
	}
	
	@Test
	void matchIpTypeWithEmptyOptional() {
		Result<Void> result = IOMatchers.matchIpType("192.168.1.1", Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchIpTypeWithPublicIpv4() {
		EnumConstraintConfig<IpAddressType> config = EnumConstraintConfig.<IpAddressType>unconstrained().withEqualTo(IpAddressType.PUBLIC);
		Result<Void> result = IOMatchers.matchIpType("8.8.8.8", Optional.of(config));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchIpTypeWithPrivateIpv4() {
		EnumConstraintConfig<IpAddressType> config = EnumConstraintConfig.<IpAddressType>unconstrained().withEqualTo(IpAddressType.PRIVATE);
		Result<Void> result = IOMatchers.matchIpType("192.168.1.1", Optional.of(config));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchIpTypeWithLoopbackIpv4() {
		EnumConstraintConfig<IpAddressType> config = EnumConstraintConfig.<IpAddressType>unconstrained().withEqualTo(IpAddressType.LOOPBACK);
		Result<Void> result = IOMatchers.matchIpType("127.0.0.1", Optional.of(config));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchIpTypeWithLinkLocalIpv4() {
		EnumConstraintConfig<IpAddressType> config = EnumConstraintConfig.<IpAddressType>unconstrained().withEqualTo(IpAddressType.LINK_LOCAL);
		Result<Void> result = IOMatchers.matchIpType("169.254.1.1", Optional.of(config));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchIpTypeWithMulticastIpv4() {
		EnumConstraintConfig<IpAddressType> config = EnumConstraintConfig.<IpAddressType>unconstrained().withEqualTo(IpAddressType.MULTICAST);
		Result<Void> result = IOMatchers.matchIpType("224.0.0.1", Optional.of(config));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchIpTypeWithBroadcastIpv4() {
		EnumConstraintConfig<IpAddressType> config = EnumConstraintConfig.<IpAddressType>unconstrained().withEqualTo(IpAddressType.BROADCAST);
		Result<Void> result = IOMatchers.matchIpType("255.255.255.255", Optional.of(config));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchIpTypeWithDocumentationIpv4() {
		EnumConstraintConfig<IpAddressType> config = EnumConstraintConfig.<IpAddressType>unconstrained().withEqualTo(IpAddressType.DOCUMENTATION);
		Result<Void> result = IOMatchers.matchIpType("192.0.2.1", Optional.of(config));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchIpTypeWithUnspecifiedIpv4() {
		EnumConstraintConfig<IpAddressType> config = EnumConstraintConfig.<IpAddressType>unconstrained().withEqualTo(IpAddressType.UNSPECIFIED);
		Result<Void> result = IOMatchers.matchIpType("0.0.0.0", Optional.of(config));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchIpTypeWithLoopbackIpv6() {
		EnumConstraintConfig<IpAddressType> config = EnumConstraintConfig.<IpAddressType>unconstrained().withEqualTo(IpAddressType.LOOPBACK);
		Result<Void> result = IOMatchers.matchIpType("::1", Optional.of(config));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchIpTypeWithDocumentationIpv6() {
		EnumConstraintConfig<IpAddressType> config = EnumConstraintConfig.<IpAddressType>unconstrained().withEqualTo(IpAddressType.DOCUMENTATION);
		Result<Void> result = IOMatchers.matchIpType("2001:db8::1", Optional.of(config));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchIpTypeWithMismatch() {
		EnumConstraintConfig<IpAddressType> config = EnumConstraintConfig.<IpAddressType>unconstrained().withEqualTo(IpAddressType.PUBLIC);
		Result<Void> result = IOMatchers.matchIpType("192.168.1.1", Optional.of(config));
		assertTrue(result.isError());
	}
	
	@Test
	void matchIpTypeWithInvalidIp() {
		EnumConstraintConfig<IpAddressType> config = EnumConstraintConfig.<IpAddressType>unconstrained().withEqualTo(IpAddressType.PUBLIC);
		Result<Void> result = IOMatchers.matchIpType("invalid", Optional.of(config));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("not a valid ip address"));
	}
	
	@Test
	void matchIpTypeWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOMatchers.matchIpType(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> IOMatchers.matchIpType("192.168.1.1", null));
	}
	
	@Test
	void matchInAnySubnetWithEmptyOptional() {
		Result<Void> result = IOMatchers.matchInAnySubnet("192.168.1.1", Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchInAnySubnetWithIpv4InSubnet() {
		Result<Void> result = IOMatchers.matchInAnySubnet("192.168.1.50", Optional.of(Pair.of(Set.of("192.168.1.0/24"), false)));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchInAnySubnetWithIpv4NotInSubnet() {
		Result<Void> result = IOMatchers.matchInAnySubnet("10.0.0.1", Optional.of(Pair.of(Set.of("192.168.1.0/24"), false)));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("must be member of at least one specified subnet"));
	}
	
	@Test
	void matchInAnySubnetWithIpv6InSubnet() {
		Result<Void> result = IOMatchers.matchInAnySubnet("2001:db8::1", Optional.of(Pair.of(Set.of("2001:db8::/32"), false)));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchInAnySubnetWithIpv6NotInSubnet() {
		Result<Void> result = IOMatchers.matchInAnySubnet("2001:db9::1", Optional.of(Pair.of(Set.of("2001:db8::/32"), false)));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("must be member of at least one specified subnet"));
	}
	
	@Test
	void matchInAnySubnetWithMultipleSubnets() {
		Result<Void> result = IOMatchers.matchInAnySubnet("10.0.0.1", Optional.of(Pair.of(Set.of("192.168.0.0/16", "10.0.0.0/8"), false)));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchInAnySubnetWithNegatedNotMember() {
		Result<Void> result = IOMatchers.matchInAnySubnet("8.8.8.8", Optional.of(Pair.of(Set.of("192.168.0.0/16"), true)));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchInAnySubnetWithNegatedMember() {
		Result<Void> result = IOMatchers.matchInAnySubnet("192.168.1.1", Optional.of(Pair.of(Set.of("192.168.0.0/16"), true)));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("must not be member of any specified subnet"));
	}
	
	@Test
	void matchInAnySubnetWithInvalidCidr() {
		Result<Void> result = IOMatchers.matchInAnySubnet("192.168.1.1", Optional.of(Pair.of(Set.of("invalid"), false)));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Invalid CIDR"));
	}
	
	@Test
	void matchInAnySubnetWithInvalidIp() {
		Result<Void> result = IOMatchers.matchInAnySubnet("invalid", Optional.of(Pair.of(Set.of("192.168.0.0/16"), false)));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("not a valid ip address"));
	}
	
	@Test
	void matchInAnySubnetWithMixedSubnets() {
		Result<Void> result = IOMatchers.matchInAnySubnet("192.168.1.1", Optional.of(Pair.of(Set.of("192.168.0.0/16", "2001:db8::/32"), false)));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchInAnySubnetWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOMatchers.matchInAnySubnet(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> IOMatchers.matchInAnySubnet("192.168.1.1", null));
	}
	
	@Test
	void matchRootDomainWithEmptyOptional() {
		Result<Void> result = IOMatchers.matchRootDomain("example.com", Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchRootDomainWithValidRootDomain() {
		Result<Void> result = IOMatchers.matchRootDomain("example.com", Optional.of(Unit.INSTANCE));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchRootDomainWithSubDomain() {
		Result<Void> result = IOMatchers.matchRootDomain("sub.example.com", Optional.of(Unit.INSTANCE));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("must be a root domain"));
	}
	
	@Test
	void matchRootDomainWithNoDot() {
		Result<Void> result = IOMatchers.matchRootDomain("localhost", Optional.of(Unit.INSTANCE));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("must be a root domain"));
	}
	
	@Test
	void matchRootDomainWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOMatchers.matchRootDomain(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> IOMatchers.matchRootDomain("example.com", null));
	}
	
	@Test
	void matchSubDomainWithEmptyOptional() {
		Result<Void> result = IOMatchers.matchSubDomain("sub.example.com", Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchSubDomainWithValidSubDomain() {
		Result<Void> result = IOMatchers.matchSubDomain("sub.example.com", Optional.of(Unit.INSTANCE));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchSubDomainWithRootDomain() {
		Result<Void> result = IOMatchers.matchSubDomain("example.com", Optional.of(Unit.INSTANCE));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("must be a subdomain"));
	}
	
	@Test
	void matchSubDomainWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOMatchers.matchSubDomain(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> IOMatchers.matchSubDomain("sub.example.com", null));
	}
	
	@Test
	void matchPathCanonicalWithEmptyOptional() {
		Result<Void> result = IOMatchers.matchPathCanonical(Path.of("/some/path"), Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchPathCanonicalWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOMatchers.matchPathCanonical(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> IOMatchers.matchPathCanonical(Path.of("/some/path"), null));
	}
	
	@Test
	void matchPathStringConfigWithEmptyOptional() {
		Result<Void> result = IOMatchers.matchPathStringConfig(Path.of("/some/path"), Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchPathStringConfigWithMatch() {
		String separator = File.separator;
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withStartsWith(separator);
		Result<Void> result = IOMatchers.matchPathStringConfig(Path.of(separator + "some" + separator + "path"), Optional.of(config));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchPathStringConfigWithNoMatch() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withStartsWith("/usr");
		Result<Void> result = IOMatchers.matchPathStringConfig(Path.of("/home/user"), Optional.of(config));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Path constraint failed"));
	}
	
	@Test
	void matchPathStringConfigWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOMatchers.matchPathStringConfig(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> IOMatchers.matchPathStringConfig(Path.of("/some/path"), null));
	}
	
	@Test
	void matchPathRootConfigWithEmptyOptional() {
		Result<Void> result = IOMatchers.matchPathRootConfig(Path.of("/some/path"), Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchPathRootConfigWithNoRoot() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withEqualTo("/");
		Result<Void> result = IOMatchers.matchPathRootConfig(Path.of("relative/path"), Optional.of(config));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("has no root component"));
	}
	
	@Test
	void matchPathRootConfigWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOMatchers.matchPathRootConfig(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> IOMatchers.matchPathRootConfig(Path.of("/some/path"), null));
	}
	
	@Test
	void matchPathParentConfigWithEmptyOptional() {
		Result<Void> result = IOMatchers.matchPathParentConfig(Path.of("/some/path"), Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchPathParentConfigWithNoParent() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withContains("some");
		Result<Void> result = IOMatchers.matchPathParentConfig(Path.of("file"), Optional.of(config));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("has no parent component"));
	}
	
	@Test
	void matchPathParentConfigWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOMatchers.matchPathParentConfig(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> IOMatchers.matchPathParentConfig(Path.of("/some/path"), null));
	}
	
	@Test
	void matchPathSegmentConfigWithEmptyOptional() {
		Result<Void> result = IOMatchers.matchPathSegmentConfig(Path.of("/some/path"), Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchPathSegmentConfigWithMatch() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withAlphabetic();
		Result<Void> result = IOMatchers.matchPathSegmentConfig(Path.of("some/path"), Optional.of(config));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchPathSegmentConfigWithNoMatch() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withNumeric();
		Result<Void> result = IOMatchers.matchPathSegmentConfig(Path.of("some/path"), Optional.of(config));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Segment"));
	}
	
	@Test
	void matchPathSegmentConfigWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOMatchers.matchPathSegmentConfig(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> IOMatchers.matchPathSegmentConfig(Path.of("/some/path"), null));
	}
	
	@Test
	void matchPathFileNameConfigWithEmptyOptional() {
		Result<Void> result = IOMatchers.matchPathFileNameConfig(Path.of("/some/file.txt"), Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchPathFileNameConfigWithMatch() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withEndsWith(".txt");
		Result<Void> result = IOMatchers.matchPathFileNameConfig(Path.of("/some/file.txt"), Optional.of(config));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchPathFileNameConfigWithNoMatch() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withEndsWith(".java");
		Result<Void> result = IOMatchers.matchPathFileNameConfig(Path.of("/some/file.txt"), Optional.of(config));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("File constraint failed"));
	}
	
	@Test
	void matchPathFileNameConfigWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOMatchers.matchPathFileNameConfig(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> IOMatchers.matchPathFileNameConfig(Path.of("/some/file.txt"), null));
	}
	
	@Test
	void matchPathExtensionConfigWithEmptyOptional() {
		Result<Void> result = IOMatchers.matchPathExtensionConfig(Path.of("/some/file.txt"), Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchPathExtensionConfigWithMatch() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withEqualTo("txt");
		Result<Void> result = IOMatchers.matchPathExtensionConfig(Path.of("/some/file.txt"), Optional.of(config));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchPathExtensionConfigWithNoExtension() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withEqualTo("txt");
		Result<Void> result = IOMatchers.matchPathExtensionConfig(Path.of("/some/file"), Optional.of(config));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("has no extension"));
	}
	
	@Test
	void matchPathExtensionConfigWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOMatchers.matchPathExtensionConfig(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> IOMatchers.matchPathExtensionConfig(Path.of("/some/file.txt"), null));
	}
	
	@Test
	void matchPathWithoutExtensionWithEmptyOptional() {
		Result<Void> result = IOMatchers.matchPathWithoutExtension(Path.of("/some/file.txt"), Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchPathWithoutExtensionWithNoExtension() {
		Result<Void> result = IOMatchers.matchPathWithoutExtension(Path.of("/some/file"), Optional.of(Unit.INSTANCE));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchPathWithoutExtensionWithExtension() {
		Result<Void> result = IOMatchers.matchPathWithoutExtension(Path.of("/some/file.txt"), Optional.of(Unit.INSTANCE));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("must not have an extension"));
	}
	
	@Test
	void matchPathWithoutExtensionWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOMatchers.matchPathWithoutExtension(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> IOMatchers.matchPathWithoutExtension(Path.of("/some/file"), null));
	}
	
	@Test
	void matchPathAncestorOfWithEmptyOptional() {
		Result<Void> result = IOMatchers.matchPathAncestorOf(Path.of("/some"), Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchPathAncestorOfWithValid() {
		Result<Void> result = IOMatchers.matchPathAncestorOf(Path.of("/some"), Optional.of(Set.of("/some/path", "/some/other")));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchPathAncestorOfWithInvalid() {
		Result<Void> result = IOMatchers.matchPathAncestorOf(Path.of("/other"), Optional.of(Set.of("/some/path")));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("must be ancestor of"));
	}
	
	@Test
	void matchPathAncestorOfWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOMatchers.matchPathAncestorOf(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> IOMatchers.matchPathAncestorOf(Path.of("/some"), null));
	}
	
	@Test
	void matchPathDescendantOfWithEmptyOptional() {
		Result<Void> result = IOMatchers.matchPathDescendantOf(Path.of("/some/path/file"), Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchPathDescendantOfWithValid() {
		Result<Void> result = IOMatchers.matchPathDescendantOf(Path.of("/some/path/file"), Optional.of(Set.of("/some", "/some/path")));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchPathDescendantOfWithInvalid() {
		Result<Void> result = IOMatchers.matchPathDescendantOf(Path.of("/other/path"), Optional.of(Set.of("/some")));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("must be descendant of"));
	}
	
	@Test
	void matchPathDescendantOfWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOMatchers.matchPathDescendantOf(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> IOMatchers.matchPathDescendantOf(Path.of("/some/path"), null));
	}
	
	@Test
	void matchQueryValueConstraintsWithEmptyOptional() {
		Map<String, List<String>> query = Map.of("key", List.of("value"));
		Result<Void> result = IOMatchers.matchQueryValueConstraints(query, Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchQueryValueConstraintsWithMatch() {
		Map<String, List<String>> query = Map.of("key", List.of("value"));
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withEqualTo("value");
		Result<Void> result = IOMatchers.matchQueryValueConstraints(query, Optional.of(Map.of("key", config)));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchQueryValueConstraintsWithNoMatch() {
		Map<String, List<String>> query = Map.of("key", List.of("wrong"));
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withEqualTo("value");
		Result<Void> result = IOMatchers.matchQueryValueConstraints(query, Optional.of(Map.of("key", config)));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Value constraint for key"));
	}
	
	@Test
	void matchQueryValueConstraintsWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOMatchers.matchQueryValueConstraints(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> IOMatchers.matchQueryValueConstraints(Map.of(), null));
	}
	
	@Test
	void matchQueryPatternValueConstraintsWithEmptyOptional() {
		Map<String, List<String>> query = Map.of("key1", List.of("value"));
		Result<Void> result = IOMatchers.matchQueryPatternValueConstraints(query, Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchQueryPatternValueConstraintsWithMatch() {
		Map<String, List<String>> query = Map.of("key1", List.of("value"));
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withAlphabetic();
		Result<Void> result = IOMatchers.matchQueryPatternValueConstraints(query, Optional.of(Map.of(Pattern.compile("key\\d+"), config)));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchQueryPatternValueConstraintsWithNoMatch() {
		Map<String, List<String>> query = Map.of("key1", List.of("123"));
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withAlphabetic();
		Result<Void> result = IOMatchers.matchQueryPatternValueConstraints(query, Optional.of(Map.of(Pattern.compile("key\\d+"), config)));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Pattern value constraint"));
	}
	
	@Test
	void matchQueryPatternValueConstraintsWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOMatchers.matchQueryPatternValueConstraints(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> IOMatchers.matchQueryPatternValueConstraints(Map.of(), null));
	}
	
	@Test
	void matchQuerySingleValuedWithEmptyOptional() {
		Map<String, List<String>> query = Map.of("key", List.of("value1", "value2"));
		Result<Void> result = IOMatchers.matchQuerySingleValued(query, Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchQuerySingleValuedWithSingleValues() {
		Map<String, List<String>> query = Map.of("key1", List.of("value1"), "key2", List.of("value2"));
		Result<Void> result = IOMatchers.matchQuerySingleValued(query, Optional.of(Unit.INSTANCE));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchQuerySingleValuedWithMultipleValues() {
		Map<String, List<String>> query = Map.of("key", List.of("value1", "value2"));
		Result<Void> result = IOMatchers.matchQuerySingleValued(query, Optional.of(Unit.INSTANCE));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("must have exactly one value"));
	}
	
	@Test
	void matchQuerySingleValuedWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOMatchers.matchQuerySingleValued(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> IOMatchers.matchQuerySingleValued(Map.of(), null));
	}
	
	@Test
	void matchQueryUniqueValuesWithEmptyOptional() {
		Map<String, List<String>> query = Map.of("key1", List.of("value"), "key2", List.of("value"));
		Result<Void> result = IOMatchers.matchQueryUniqueValues(query, Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchQueryUniqueValuesWithUnique() {
		Map<String, List<String>> query = Map.of("key1", List.of("value1"), "key2", List.of("value2"));
		Result<Void> result = IOMatchers.matchQueryUniqueValues(query, Optional.of(Unit.INSTANCE));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchQueryUniqueValuesWithDuplicates() {
		Map<String, List<String>> query = Map.of("key1", List.of("value", "other"), "key2", List.of("value"));
		Result<Void> result = IOMatchers.matchQueryUniqueValues(query, Optional.of(Unit.INSTANCE));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("must be unique"));
	}
	
	@Test
	void matchQueryUniqueValuesWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOMatchers.matchQueryUniqueValues(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> IOMatchers.matchQueryUniqueValues(Map.of(), null));
	}
	
	@Test
	void matchQueryMultiValuedConstraintsWithEmptyOptional() {
		Map<String, List<String>> query = Map.of("key", List.of("v1", "v2", "v3"));
		Result<Void> result = IOMatchers.matchQueryMultiValuedConstraints(query, Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchQueryMultiValuedConstraintsWithMatch() {
		Map<String, List<String>> query = Map.of("key", List.of("v1", "v2"));
		SizeConstraintConfig config = SizeConstraintConfig.UNCONSTRAINED.withSizeBetween(1, 3);
		Result<Void> result = IOMatchers.matchQueryMultiValuedConstraints(query, Optional.of(Map.of("key", config)));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchQueryMultiValuedConstraintsWithNoMatch() {
		Map<String, List<String>> query = Map.of("key", List.of("v1", "v2", "v3", "v4", "v5"));
		SizeConstraintConfig config = SizeConstraintConfig.UNCONSTRAINED.withMaxSize(3);
		Result<Void> result = IOMatchers.matchQueryMultiValuedConstraints(query, Optional.of(Map.of("key", config)));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Multi-valued constraint"));
	}
	
	@Test
	void matchQueryMultiValuedConstraintsWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOMatchers.matchQueryMultiValuedConstraints(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> IOMatchers.matchQueryMultiValuedConstraints(Map.of(), null));
	}
	
	@Test
	void matchUriSchemeConfigWithEmptyOptional() {
		URI uri = URI.create("https://example.com");
		Result<Void> result = IOMatchers.matchUriSchemeConfig(uri, Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchUriSchemeConfigWithMatch() {
		URI uri = URI.create("https://example.com");
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withEqualTo("https");
		Result<Void> result = IOMatchers.matchUriSchemeConfig(uri, Optional.of(config));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchUriSchemeConfigWithNoScheme() {
		URI uri = URI.create("//example.com");
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withEqualTo("https");
		Result<Void> result = IOMatchers.matchUriSchemeConfig(uri, Optional.of(config));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("has no scheme"));
	}
	
	@Test
	void matchUriSchemeConfigWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOMatchers.matchUriSchemeConfig(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> IOMatchers.matchUriSchemeConfig(URI.create("https://example.com"), null));
	}
	
	@Test
	void matchUriHostConfigWithEmptyOptional() {
		URI uri = URI.create("https://example.com");
		Result<Void> result = IOMatchers.matchUriHostConfig(uri, Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchUriHostConfigWithMatch() {
		URI uri = URI.create("https://example.com");
		HostConstraintConfig config = HostConstraintConfig.UNCONSTRAINED.withEqualTo("example.com");
		Result<Void> result = IOMatchers.matchUriHostConfig(uri, Optional.of(config));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchUriHostConfigWithNoMatch() {
		URI uri = URI.create("https://example.com");
		HostConstraintConfig config = HostConstraintConfig.UNCONSTRAINED.withEqualTo("other.com");
		Result<Void> result = IOMatchers.matchUriHostConfig(uri, Optional.of(config));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Host constraint failed"));
	}
	
	@Test
	void matchUriHostConfigWithNoHost() {
		URI uri = URI.create("file:/path/to/file");
		HostConstraintConfig config = HostConstraintConfig.UNCONSTRAINED.withEqualTo("example.com");
		Result<Void> result = IOMatchers.matchUriHostConfig(uri, Optional.of(config));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("has no host"));
	}
	
	@Test
	void matchUriHostConfigWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOMatchers.matchUriHostConfig(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> IOMatchers.matchUriHostConfig(URI.create("https://example.com"), null));
	}
	
	@Test
	void matchUriUserInfoConfigWithEmptyOptional() {
		URI uri = URI.create("https://user:pass@example.com");
		Result<Void> result = IOMatchers.matchUriUserInfoConfig(uri, Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchUriUserInfoConfigWithMatch() {
		URI uri = URI.create("https://user:pass@example.com");
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withContains("user");
		Result<Void> result = IOMatchers.matchUriUserInfoConfig(uri, Optional.of(config));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchUriUserInfoConfigWithNoUserInfo() {
		URI uri = URI.create("https://example.com");
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withContains("user");
		Result<Void> result = IOMatchers.matchUriUserInfoConfig(uri, Optional.of(config));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("has no user info"));
	}
	
	@Test
	void matchUriUserInfoConfigWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOMatchers.matchUriUserInfoConfig(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> IOMatchers.matchUriUserInfoConfig(URI.create("https://example.com"), null));
	}
	
	@Test
	void matchUriPortConfigWithEmptyOptional() {
		URI uri = URI.create("https://example.com:8080");
		Result<Void> result = IOMatchers.matchUriPortConfig(uri, Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchUriPortConfigWithMatch() {
		URI uri = URI.create("https://example.com:8080");
		PortConstraintConfig config = PortConstraintConfig.UNCONSTRAINED.withEqualTo(8080);
		Result<Void> result = IOMatchers.matchUriPortConfig(uri, Optional.of(config));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchUriPortConfigWithNoMatch() {
		URI uri = URI.create("https://example.com:8080");
		PortConstraintConfig config = PortConstraintConfig.UNCONSTRAINED.withEqualTo(443);
		Result<Void> result = IOMatchers.matchUriPortConfig(uri, Optional.of(config));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Port constraint failed"));
	}
	
	@Test
	void matchUriPortConfigWithNoPort() {
		URI uri = URI.create("https://example.com");
		PortConstraintConfig config = PortConstraintConfig.UNCONSTRAINED.withEqualTo(8080);
		Result<Void> result = IOMatchers.matchUriPortConfig(uri, Optional.of(config));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("has no port"));
	}
	
	@Test
	void matchUriPortConfigWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOMatchers.matchUriPortConfig(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> IOMatchers.matchUriPortConfig(URI.create("https://example.com:8080"), null));
	}
	
	@Test
	void matchUriPathConfigWithEmptyOptional() {
		URI uri = URI.create("https://example.com/path/to/resource");
		Result<Void> result = IOMatchers.matchUriPathConfig(uri, Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchUriPathConfigWithMatch() {
		URI uri = URI.create("https://example.com/path/to/resource");
		URIPathConstraintConfig config = URIPathConstraintConfig.UNCONSTRAINED.withDepth(
			DepthConstraintConfig.UNCONSTRAINED.withMinDepth(2)
		);
		Result<Void> result = IOMatchers.matchUriPathConfig(uri, Optional.of(config));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchUriPathConfigWithNoMatch() {
		URI uri = URI.create("https://example.com/path");
		URIPathConstraintConfig config = URIPathConstraintConfig.UNCONSTRAINED.withDepth(
			DepthConstraintConfig.UNCONSTRAINED.withMinDepth(3)
		);
		Result<Void> result = IOMatchers.matchUriPathConfig(uri, Optional.of(config));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Path constraint failed"));
	}
	
	@Test
	void matchUriPathConfigWithNoPath() {
		URI uri = URI.create("https://example.com");
		URIPathConstraintConfig config = URIPathConstraintConfig.UNCONSTRAINED.withDepth(
			DepthConstraintConfig.UNCONSTRAINED.withMinDepth(1)
		);
		Result<Void> result = IOMatchers.matchUriPathConfig(uri, Optional.of(config));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("has no path"));
	}
	
	@Test
	void matchUriPathConfigWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOMatchers.matchUriPathConfig(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> IOMatchers.matchUriPathConfig(URI.create("https://example.com/path"), null));
	}
	
	@Test
	void matchUriQueryConfigWithEmptyOptional() {
		URI uri = URI.create("https://example.com?key=value");
		Result<Void> result = IOMatchers.matchUriQueryConfig(uri, Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchUriQueryConfigWithMatch() {
		URI uri = URI.create("https://example.com?key=value");
		QueryConstraintConfig config = QueryConstraintConfig.UNCONSTRAINED.withRequiredKeys(List.of("key"));
		Result<Void> result = IOMatchers.matchUriQueryConfig(uri, Optional.of(config));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchUriQueryConfigWithNoMatch() {
		URI uri = URI.create("https://example.com?key=value");
		QueryConstraintConfig config = QueryConstraintConfig.UNCONSTRAINED.withRequiredKeys(List.of("missing"));
		Result<Void> result = IOMatchers.matchUriQueryConfig(uri, Optional.of(config));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Query constraint failed"));
	}
	
	@Test
	void matchUriQueryConfigWithNoQuery() {
		URI uri = URI.create("https://example.com");
		QueryConstraintConfig config = QueryConstraintConfig.UNCONSTRAINED.withSize(SizeConstraintConfig.UNCONSTRAINED.withMinSize(1));
		Result<Void> result = IOMatchers.matchUriQueryConfig(uri, Optional.of(config));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("has no query"));
	}
	
	@Test
	void matchUriQueryConfigWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOMatchers.matchUriQueryConfig(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> IOMatchers.matchUriQueryConfig(URI.create("https://example.com?key=value"), null));
	}
	
	@Test
	void matchUriFragmentConfigWithEmptyOptional() {
		URI uri = URI.create("https://example.com#section");
		Result<Void> result = IOMatchers.matchUriFragmentConfig(uri, Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchUriFragmentConfigWithMatch() {
		URI uri = URI.create("https://example.com#section");
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withEqualTo("section");
		Result<Void> result = IOMatchers.matchUriFragmentConfig(uri, Optional.of(config));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchUriFragmentConfigWithNoFragment() {
		URI uri = URI.create("https://example.com");
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withEqualTo("section");
		Result<Void> result = IOMatchers.matchUriFragmentConfig(uri, Optional.of(config));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("has no fragment"));
	}
	
	@Test
	void matchUriFragmentConfigWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOMatchers.matchUriFragmentConfig(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> IOMatchers.matchUriFragmentConfig(URI.create("https://example.com"), null));
	}
	
	@Test
	void parseQueryWithEmptyString() {
		Map<String, List<String>> result = IOMatchers.parseQuery("");
		assertTrue(result.isEmpty());
	}
	
	@Test
	void parseQueryWithSingleParam() {
		Map<String, List<String>> result = IOMatchers.parseQuery("key=value");
		assertEquals(1, result.size());
		assertEquals(List.of("value"), result.get("key"));
	}
	
	@Test
	void parseQueryWithMultipleParams() {
		Map<String, List<String>> result = IOMatchers.parseQuery("key1=value1&key2=value2");
		assertEquals(2, result.size());
		assertEquals(List.of("value1"), result.get("key1"));
		assertEquals(List.of("value2"), result.get("key2"));
	}
	
	@Test
	void parseQueryWithRepeatedKeys() {
		Map<String, List<String>> result = IOMatchers.parseQuery("key=value1&key=value2");
		assertEquals(1, result.size());
		assertEquals(List.of("value1", "value2"), result.get("key"));
	}
	
	@Test
	void parseQueryWithEmptyValue() {
		Map<String, List<String>> result = IOMatchers.parseQuery("key=");
		assertEquals(1, result.size());
		assertEquals(List.of(""), result.get("key"));
	}
	
	@Test
	void parseQueryWithNoValue() {
		Map<String, List<String>> result = IOMatchers.parseQuery("key");
		assertEquals(1, result.size());
		assertEquals(List.of(""), result.get("key"));
	}
	
	@Test
	void parseQueryWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOMatchers.parseQuery(null));
	}
	
	// URI Path matcher tests
	
	@Test
	void matchUriPathStringConfigWithEmptyOptional() {
		Result<Void> result = IOMatchers.matchUriPathStringConfig("/some/path", Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchUriPathStringConfigWithMatch() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withStartsWith("/");
		Result<Void> result = IOMatchers.matchUriPathStringConfig("/some/path", Optional.of(config));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchUriPathStringConfigWithNoMatch() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withStartsWith("/usr");
		Result<Void> result = IOMatchers.matchUriPathStringConfig("/home/user", Optional.of(config));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Path constraint failed"));
	}
	
	@Test
	void matchUriPathStringConfigWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOMatchers.matchUriPathStringConfig(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> IOMatchers.matchUriPathStringConfig("/some/path", null));
	}
	
	@Test
	void matchUriPathSegmentConfigWithEmptyOptional() {
		Result<Void> result = IOMatchers.matchUriPathSegmentConfig("/some/path", Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchUriPathSegmentConfigWithMatch() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withAlphabetic();
		Result<Void> result = IOMatchers.matchUriPathSegmentConfig("/some/path", Optional.of(config));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchUriPathSegmentConfigWithNoMatch() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withNumeric();
		Result<Void> result = IOMatchers.matchUriPathSegmentConfig("/some/path", Optional.of(config));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Segment"));
	}
	
	@Test
	void matchUriPathSegmentConfigWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOMatchers.matchUriPathSegmentConfig(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> IOMatchers.matchUriPathSegmentConfig("/some/path", null));
	}
	
	@Test
	void matchUriPathFileNameConfigWithEmptyOptional() {
		Result<Void> result = IOMatchers.matchUriPathFileNameConfig("/some/file.txt", Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchUriPathFileNameConfigWithMatch() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withEndsWith(".txt");
		Result<Void> result = IOMatchers.matchUriPathFileNameConfig("/some/file.txt", Optional.of(config));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchUriPathFileNameConfigWithNoMatch() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withEndsWith(".java");
		Result<Void> result = IOMatchers.matchUriPathFileNameConfig("/some/file.txt", Optional.of(config));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("File constraint failed"));
	}
	
	@Test
	void matchUriPathFileNameConfigWithNoFileName() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withEqualTo("test");
		Result<Void> result = IOMatchers.matchUriPathFileNameConfig("", Optional.of(config));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("has no file name"));
	}
	
	@Test
	void matchUriPathFileNameConfigWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOMatchers.matchUriPathFileNameConfig(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> IOMatchers.matchUriPathFileNameConfig("/some/file.txt", null));
	}
	
	@Test
	void matchUriPathWithoutExtensionWithEmptyOptional() {
		Result<Void> result = IOMatchers.matchUriPathWithoutExtension("/some/file.txt", Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchUriPathWithoutExtensionWithNoExtension() {
		Result<Void> result = IOMatchers.matchUriPathWithoutExtension("/some/file", Optional.of(Unit.INSTANCE));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchUriPathWithoutExtensionWithExtension() {
		Result<Void> result = IOMatchers.matchUriPathWithoutExtension("/some/file.txt", Optional.of(Unit.INSTANCE));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("must not have an extension"));
	}
	
	@Test
	void matchUriPathWithoutExtensionWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOMatchers.matchUriPathWithoutExtension(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> IOMatchers.matchUriPathWithoutExtension("/some/file", null));
	}
	
	@Test
	void matchUriPathExtensionConfigWithEmptyOptional() {
		Result<Void> result = IOMatchers.matchUriPathExtensionConfig("/some/file.txt", Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchUriPathExtensionConfigWithMatch() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withEqualTo("txt");
		Result<Void> result = IOMatchers.matchUriPathExtensionConfig("/some/file.txt", Optional.of(config));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchUriPathExtensionConfigWithNoExtension() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withEqualTo("txt");
		Result<Void> result = IOMatchers.matchUriPathExtensionConfig("/some/file", Optional.of(config));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("has no extension"));
	}
	
	@Test
	void matchUriPathExtensionConfigWithNoFileName() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withEqualTo("txt");
		Result<Void> result = IOMatchers.matchUriPathExtensionConfig("", Optional.of(config));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("has no file name"));
	}
	
	@Test
	void matchUriPathExtensionConfigWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOMatchers.matchUriPathExtensionConfig(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> IOMatchers.matchUriPathExtensionConfig("/some/file.txt", null));
	}
	
	@Test
	void matchUriPathAncestorOfWithEmptyOptional() {
		Result<Void> result = IOMatchers.matchUriPathAncestorOf("/some", Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchUriPathAncestorOfWithValid() {
		Result<Void> result = IOMatchers.matchUriPathAncestorOf("/some", Optional.of(Set.of("/some/path", "/some/other")));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchUriPathAncestorOfWithInvalid() {
		Result<Void> result = IOMatchers.matchUriPathAncestorOf("/other", Optional.of(Set.of("/some/path")));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("must be ancestor of"));
	}
	
	@Test
	void matchUriPathAncestorOfWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOMatchers.matchUriPathAncestorOf(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> IOMatchers.matchUriPathAncestorOf("/some", null));
	}
	
	@Test
	void matchUriPathDescendantOfWithEmptyOptional() {
		Result<Void> result = IOMatchers.matchUriPathDescendantOf("/some/path/file", Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchUriPathDescendantOfWithValid() {
		Result<Void> result = IOMatchers.matchUriPathDescendantOf("/some/path/file", Optional.of(Set.of("/some", "/some/path")));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchUriPathDescendantOfWithInvalid() {
		Result<Void> result = IOMatchers.matchUriPathDescendantOf("/other/path", Optional.of(Set.of("/some")));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("must be descendant of"));
	}
	
	@Test
	void matchUriPathDescendantOfWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOMatchers.matchUriPathDescendantOf(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> IOMatchers.matchUriPathDescendantOf("/some/path", null));
	}
	
	@Test
	void matchInetAddressIpVersionWithEmptyOptional() throws UnknownHostException {
		InetAddress address = InetAddress.getByName("192.168.1.1");
		Result<Void> result = IOMatchers.matchInetAddressIpVersion(address, Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchInetAddressIpVersionWithIpv4() throws UnknownHostException {
		InetAddress address = InetAddress.getByName("192.168.1.1");
		EnumConstraintConfig<IpVersion> config = EnumConstraintConfig.<IpVersion>unconstrained().withEqualTo(IpVersion.IPV4);
		Result<Void> result = IOMatchers.matchInetAddressIpVersion(address, Optional.of(config));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchInetAddressIpVersionWithIpv6() throws UnknownHostException {
		InetAddress address = InetAddress.getByName("::1");
		EnumConstraintConfig<IpVersion> config = EnumConstraintConfig.<IpVersion>unconstrained().withEqualTo(IpVersion.IPV6);
		Result<Void> result = IOMatchers.matchInetAddressIpVersion(address, Optional.of(config));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchInetAddressIpVersionWithMismatch() throws UnknownHostException {
		InetAddress address = InetAddress.getByName("192.168.1.1");
		EnumConstraintConfig<IpVersion> config = EnumConstraintConfig.<IpVersion>unconstrained().withEqualTo(IpVersion.IPV6);
		Result<Void> result = IOMatchers.matchInetAddressIpVersion(address, Optional.of(config));
		assertTrue(result.isError());
	}
	
	@Test
	void matchInetAddressIpVersionWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOMatchers.matchInetAddressIpVersion(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> IOMatchers.matchInetAddressIpVersion(InetAddress.getByName("192.168.1.1"), null));
	}
	
	@Test
	void matchInetAddressIpTypeWithEmptyOptional() throws UnknownHostException {
		InetAddress address = InetAddress.getByName("192.168.1.1");
		Result<Void> result = IOMatchers.matchInetAddressIpType(address, Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchInetAddressIpTypeWithPrivate() throws UnknownHostException {
		InetAddress address = InetAddress.getByName("192.168.1.1");
		EnumConstraintConfig<IpAddressType> config = EnumConstraintConfig.<IpAddressType>unconstrained().withEqualTo(IpAddressType.PRIVATE);
		Result<Void> result = IOMatchers.matchInetAddressIpType(address, Optional.of(config));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchInetAddressIpTypeWithLoopback() throws UnknownHostException {
		InetAddress address = InetAddress.getByName("127.0.0.1");
		EnumConstraintConfig<IpAddressType> config = EnumConstraintConfig.<IpAddressType>unconstrained().withEqualTo(IpAddressType.LOOPBACK);
		Result<Void> result = IOMatchers.matchInetAddressIpType(address, Optional.of(config));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchInetAddressIpTypeWithLoopbackIpv6() throws UnknownHostException {
		InetAddress address = InetAddress.getByName("::1");
		EnumConstraintConfig<IpAddressType> config = EnumConstraintConfig.<IpAddressType>unconstrained().withEqualTo(IpAddressType.LOOPBACK);
		Result<Void> result = IOMatchers.matchInetAddressIpType(address, Optional.of(config));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchInetAddressIpTypeWithMismatch() throws UnknownHostException {
		InetAddress address = InetAddress.getByName("192.168.1.1");
		EnumConstraintConfig<IpAddressType> config = EnumConstraintConfig.<IpAddressType>unconstrained().withEqualTo(IpAddressType.LOOPBACK);
		Result<Void> result = IOMatchers.matchInetAddressIpType(address, Optional.of(config));
		assertTrue(result.isError());
	}
	
	@Test
	void matchInetAddressIpTypeWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOMatchers.matchInetAddressIpType(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> IOMatchers.matchInetAddressIpType(InetAddress.getByName("192.168.1.1"), null));
	}
	
	@Test
	void matchInetAddressInAnySubnetWithEmptyOptional() throws UnknownHostException {
		InetAddress address = InetAddress.getByName("192.168.1.1");
		Result<Void> result = IOMatchers.matchInetAddressInAnySubnet(address, Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchInetAddressInAnySubnetWithMatch() throws UnknownHostException {
		InetAddress address = InetAddress.getByName("192.168.1.50");
		Result<Void> result = IOMatchers.matchInetAddressInAnySubnet(address, Optional.of(Pair.of(Set.of("192.168.1.0/24"), false)));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchInetAddressInAnySubnetWithNoMatch() throws UnknownHostException {
		InetAddress address = InetAddress.getByName("10.0.0.1");
		Result<Void> result = IOMatchers.matchInetAddressInAnySubnet(address, Optional.of(Pair.of(Set.of("192.168.1.0/24"), false)));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("must be member of at least one specified subnet"));
	}
	
	@Test
	void matchInetAddressInAnySubnetWithNegated() throws UnknownHostException {
		InetAddress address = InetAddress.getByName("10.0.0.1");
		Result<Void> result = IOMatchers.matchInetAddressInAnySubnet(address, Optional.of(Pair.of(Set.of("192.168.0.0/16"), true)));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchInetAddressInAnySubnetWithNegatedMember() throws UnknownHostException {
		InetAddress address = InetAddress.getByName("192.168.1.1");
		Result<Void> result = IOMatchers.matchInetAddressInAnySubnet(address, Optional.of(Pair.of(Set.of("192.168.0.0/16"), true)));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("must not be member of any specified subnet"));
	}
	
	@Test
	void matchInetAddressInAnySubnetWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOMatchers.matchInetAddressInAnySubnet(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> IOMatchers.matchInetAddressInAnySubnet(InetAddress.getByName("192.168.1.1"), null));
	}
	
	@Test
	void matchInetSocketAddressAddressWithEmptyOptional() throws UnknownHostException {
		InetSocketAddress socketAddress = new InetSocketAddress(InetAddress.getByName("192.168.1.1"), 8080);
		Result<Void> result = IOMatchers.matchInetSocketAddressAddress(socketAddress, Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchInetSocketAddressAddressWithMatch() throws UnknownHostException {
		InetSocketAddress socketAddress = new InetSocketAddress(InetAddress.getByName("192.168.1.1"), 8080);
		InetAddressConstraintConfig config = InetAddressConstraintConfig.UNCONSTRAINED.withIpType(
			EnumConstraintConfig.<IpAddressType>unconstrained().withEqualTo(IpAddressType.PRIVATE)
		);
		Result<Void> result = IOMatchers.matchInetSocketAddressAddress(socketAddress, Optional.of(config));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchInetSocketAddressAddressWithNoMatch() throws UnknownHostException {
		InetSocketAddress socketAddress = new InetSocketAddress(InetAddress.getByName("127.0.0.1"), 8080);
		InetAddressConstraintConfig config = InetAddressConstraintConfig.UNCONSTRAINED.withIpType(
			EnumConstraintConfig.<IpAddressType>unconstrained().withEqualTo(IpAddressType.PRIVATE)
		);
		Result<Void> result = IOMatchers.matchInetSocketAddressAddress(socketAddress, Optional.of(config));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Address constraint failed"));
	}
	
	@Test
	void matchInetSocketAddressAddressWithUnresolved() {
		InetSocketAddress socketAddress = InetSocketAddress.createUnresolved("nonexistent.invalid", 8080);
		InetAddressConstraintConfig config = InetAddressConstraintConfig.UNCONSTRAINED;
		Result<Void> result = IOMatchers.matchInetSocketAddressAddress(socketAddress, Optional.of(config));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("has no resolved address"));
	}
	
	@Test
	void matchInetSocketAddressAddressWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOMatchers.matchInetSocketAddressAddress(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> IOMatchers.matchInetSocketAddressAddress(
			new InetSocketAddress(InetAddress.getByName("192.168.1.1"), 8080), null
		));
	}
	
	@Test
	void matchInetSocketAddressPortWithEmptyOptional() throws UnknownHostException {
		InetSocketAddress socketAddress = new InetSocketAddress(InetAddress.getByName("192.168.1.1"), 8080);
		Result<Void> result = IOMatchers.matchInetSocketAddressPort(socketAddress, Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchInetSocketAddressPortWithMatch() throws UnknownHostException {
		InetSocketAddress socketAddress = new InetSocketAddress(InetAddress.getByName("192.168.1.1"), 8080);
		PortConstraintConfig config = PortConstraintConfig.UNCONSTRAINED.withInRange(8000, 9000);
		Result<Void> result = IOMatchers.matchInetSocketAddressPort(socketAddress, Optional.of(config));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchInetSocketAddressPortWithNoMatch() throws UnknownHostException {
		InetSocketAddress socketAddress = new InetSocketAddress(InetAddress.getByName("192.168.1.1"), 80);
		PortConstraintConfig config = PortConstraintConfig.UNCONSTRAINED.withInRange(8000, 9000);
		Result<Void> result = IOMatchers.matchInetSocketAddressPort(socketAddress, Optional.of(config));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Port constraint failed"));
	}
	
	@Test
	void matchInetSocketAddressPortWithExactMatch() throws UnknownHostException {
		InetSocketAddress socketAddress = new InetSocketAddress(InetAddress.getByName("192.168.1.1"), 443);
		PortConstraintConfig config = PortConstraintConfig.UNCONSTRAINED.withEqualTo(443);
		Result<Void> result = IOMatchers.matchInetSocketAddressPort(socketAddress, Optional.of(config));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchInetSocketAddressPortWithNullChecks() {
		assertThrows(NullPointerException.class, () -> IOMatchers.matchInetSocketAddressPort(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> IOMatchers.matchInetSocketAddressPort(
			new InetSocketAddress(InetAddress.getByName("192.168.1.1"), 8080), null
		));
	}
}
