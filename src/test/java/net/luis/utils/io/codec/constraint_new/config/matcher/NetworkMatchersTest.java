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
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link NetworkMatchers}.<br>
 *
 * @author Luis-St
 */
class NetworkMatchersTest {
	
	@Test
	void matchPortRangeWithEmptyOptional() {
		Result<Void> result = NetworkMatchers.matchPortRange(8080, Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchPortRangeWithInRange() {
		Result<Void> result = NetworkMatchers.matchPortRange(8080, Optional.of(Pair.of(Pair.of(8000, 9000), false)));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchPortRangeWithOutOfRange() {
		Result<Void> result = NetworkMatchers.matchPortRange(7000, Optional.of(Pair.of(Pair.of(8000, 9000), false)));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("must be in range"));
	}
	
	@Test
	void matchPortRangeWithNegated() {
		Result<Void> result = NetworkMatchers.matchPortRange(7000, Optional.of(Pair.of(Pair.of(8000, 9000), true)));
		assertTrue(result.isSuccess());
		Result<Void> negatedResult = NetworkMatchers.matchPortRange(8080, Optional.of(Pair.of(Pair.of(8000, 9000), true)));
		assertTrue(negatedResult.isError());
		assertTrue(negatedResult.errorOrThrow().contains("must not be in range"));
	}
	
	@Test
	void matchPortRangeWithNullChecks() {
		assertThrows(NullPointerException.class, () -> NetworkMatchers.matchPortRange(8080, null));
	}
	
	@Test
	void matchPortTypeWithEmptyOptional() {
		Result<Void> result = NetworkMatchers.matchPortType(80, Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchPortTypeWithSystemPort() {
		EnumConstraintConfig<PortRange> config = EnumConstraintConfig.<PortRange>unconstrained().withEqualTo(PortRange.SYSTEM);
		Result<Void> result = NetworkMatchers.matchPortType(80, Optional.of(config));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchPortTypeWithRegisteredPort() {
		EnumConstraintConfig<PortRange> config = EnumConstraintConfig.<PortRange>unconstrained().withEqualTo(PortRange.REGISTERED);
		Result<Void> result = NetworkMatchers.matchPortType(8080, Optional.of(config));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchPortTypeWithDynamicPort() {
		EnumConstraintConfig<PortRange> config = EnumConstraintConfig.<PortRange>unconstrained().withEqualTo(PortRange.DYNAMIC);
		Result<Void> result = NetworkMatchers.matchPortType(50000, Optional.of(config));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchPortTypeWithMismatch() {
		EnumConstraintConfig<PortRange> config = EnumConstraintConfig.<PortRange>unconstrained().withEqualTo(PortRange.SYSTEM);
		Result<Void> result = NetworkMatchers.matchPortType(8080, Optional.of(config));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Port type constraint failed"));
	}
	
	@Test
	void matchPortTypeWithNullChecks() {
		assertThrows(NullPointerException.class, () -> NetworkMatchers.matchPortType(80, null));
	}
	
	@Test
	void matchIpVersionWithEmptyOptional() {
		Result<Void> result = NetworkMatchers.matchIpVersion("192.168.1.1", Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchIpVersionWithIpv4() {
		EnumConstraintConfig<IpVersion> config = EnumConstraintConfig.<IpVersion>unconstrained().withEqualTo(IpVersion.IPV4);
		Result<Void> result = NetworkMatchers.matchIpVersion("192.168.1.1", Optional.of(config));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchIpVersionWithIpv6() {
		EnumConstraintConfig<IpVersion> config = EnumConstraintConfig.<IpVersion>unconstrained().withEqualTo(IpVersion.IPV6);
		Result<Void> result = NetworkMatchers.matchIpVersion("2001:0db8::1", Optional.of(config));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchIpVersionWithMismatch() {
		EnumConstraintConfig<IpVersion> config = EnumConstraintConfig.<IpVersion>unconstrained().withEqualTo(IpVersion.IPV6);
		Result<Void> result = NetworkMatchers.matchIpVersion("192.168.1.1", Optional.of(config));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("IP version constraint failed"));
	}
	
	@Test
	void matchIpVersionWithInvalidIp() {
		EnumConstraintConfig<IpVersion> config = EnumConstraintConfig.<IpVersion>unconstrained().withEqualTo(IpVersion.IPV4);
		Result<Void> result = NetworkMatchers.matchIpVersion("not.an.ip", Optional.of(config));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("not a valid IP address"));
	}
	
	@Test
	void matchIpVersionWithNullChecks() {
		assertThrows(NullPointerException.class, () -> NetworkMatchers.matchIpVersion(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> NetworkMatchers.matchIpVersion("192.168.1.1", null));
	}
	
	@Test
	void matchIpTypeWithEmptyOptional() {
		Result<Void> result = NetworkMatchers.matchIpType("192.168.1.1", Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchIpTypeWithPublicIpv4() {
		EnumConstraintConfig<IpAddressType> config = EnumConstraintConfig.<IpAddressType>unconstrained().withEqualTo(IpAddressType.PUBLIC);
		Result<Void> result = NetworkMatchers.matchIpType("8.8.8.8", Optional.of(config));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchIpTypeWithPrivateIpv4() {
		EnumConstraintConfig<IpAddressType> config = EnumConstraintConfig.<IpAddressType>unconstrained().withEqualTo(IpAddressType.PRIVATE);
		Result<Void> result = NetworkMatchers.matchIpType("192.168.1.1", Optional.of(config));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchIpTypeWithLoopbackIpv4() {
		EnumConstraintConfig<IpAddressType> config = EnumConstraintConfig.<IpAddressType>unconstrained().withEqualTo(IpAddressType.LOOPBACK);
		Result<Void> result = NetworkMatchers.matchIpType("127.0.0.1", Optional.of(config));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchIpTypeWithLinkLocalIpv4() {
		EnumConstraintConfig<IpAddressType> config = EnumConstraintConfig.<IpAddressType>unconstrained().withEqualTo(IpAddressType.LINK_LOCAL);
		Result<Void> result = NetworkMatchers.matchIpType("169.254.1.1", Optional.of(config));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchIpTypeWithMulticastIpv4() {
		EnumConstraintConfig<IpAddressType> config = EnumConstraintConfig.<IpAddressType>unconstrained().withEqualTo(IpAddressType.MULTICAST);
		Result<Void> result = NetworkMatchers.matchIpType("224.0.0.1", Optional.of(config));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchIpTypeWithBroadcastIpv4() {
		EnumConstraintConfig<IpAddressType> config = EnumConstraintConfig.<IpAddressType>unconstrained().withEqualTo(IpAddressType.BROADCAST);
		Result<Void> result = NetworkMatchers.matchIpType("255.255.255.255", Optional.of(config));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchIpTypeWithDocumentationIpv4() {
		EnumConstraintConfig<IpAddressType> config = EnumConstraintConfig.<IpAddressType>unconstrained().withEqualTo(IpAddressType.DOCUMENTATION);
		Result<Void> result = NetworkMatchers.matchIpType("192.0.2.1", Optional.of(config));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchIpTypeWithUnspecifiedIpv4() {
		EnumConstraintConfig<IpAddressType> config = EnumConstraintConfig.<IpAddressType>unconstrained().withEqualTo(IpAddressType.UNSPECIFIED);
		Result<Void> result = NetworkMatchers.matchIpType("0.0.0.0", Optional.of(config));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchIpTypeWithLoopbackIpv6() {
		EnumConstraintConfig<IpAddressType> config = EnumConstraintConfig.<IpAddressType>unconstrained().withEqualTo(IpAddressType.LOOPBACK);
		Result<Void> result = NetworkMatchers.matchIpType("::1", Optional.of(config));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchIpTypeWithDocumentationIpv6() {
		EnumConstraintConfig<IpAddressType> config = EnumConstraintConfig.<IpAddressType>unconstrained().withEqualTo(IpAddressType.DOCUMENTATION);
		Result<Void> result = NetworkMatchers.matchIpType("2001:db8::1", Optional.of(config));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchIpTypeWithMismatch() {
		EnumConstraintConfig<IpAddressType> config = EnumConstraintConfig.<IpAddressType>unconstrained().withEqualTo(IpAddressType.PUBLIC);
		Result<Void> result = NetworkMatchers.matchIpType("192.168.1.1", Optional.of(config));
		assertTrue(result.isError());
	}
	
	@Test
	void matchIpTypeWithInvalidIp() {
		EnumConstraintConfig<IpAddressType> config = EnumConstraintConfig.<IpAddressType>unconstrained().withEqualTo(IpAddressType.PUBLIC);
		Result<Void> result = NetworkMatchers.matchIpType("invalid", Optional.of(config));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("not a valid ip address"));
	}
	
	@Test
	void matchIpTypeWithNullChecks() {
		assertThrows(NullPointerException.class, () -> NetworkMatchers.matchIpType(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> NetworkMatchers.matchIpType("192.168.1.1", null));
	}
	
	@Test
	void matchInAnySubnetWithEmptyOptional() {
		Result<Void> result = NetworkMatchers.matchInAnySubnet("192.168.1.1", Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchInAnySubnetWithIpv4InSubnet() {
		Result<Void> result = NetworkMatchers.matchInAnySubnet("192.168.1.50", Optional.of(Pair.of(Set.of("192.168.1.0/24"), false)));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchInAnySubnetWithIpv4NotInSubnet() {
		Result<Void> result = NetworkMatchers.matchInAnySubnet("10.0.0.1", Optional.of(Pair.of(Set.of("192.168.1.0/24"), false)));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("must be member of at least one specified subnet"));
	}
	
	@Test
	void matchInAnySubnetWithIpv6InSubnet() {
		Result<Void> result = NetworkMatchers.matchInAnySubnet("2001:db8::1", Optional.of(Pair.of(Set.of("2001:db8::/32"), false)));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchInAnySubnetWithIpv6NotInSubnet() {
		Result<Void> result = NetworkMatchers.matchInAnySubnet("2001:db9::1", Optional.of(Pair.of(Set.of("2001:db8::/32"), false)));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("must be member of at least one specified subnet"));
	}
	
	@Test
	void matchInAnySubnetWithMultipleSubnets() {
		Result<Void> result = NetworkMatchers.matchInAnySubnet("10.0.0.1", Optional.of(Pair.of(Set.of("192.168.0.0/16", "10.0.0.0/8"), false)));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchInAnySubnetWithNegatedNotMember() {
		Result<Void> result = NetworkMatchers.matchInAnySubnet("8.8.8.8", Optional.of(Pair.of(Set.of("192.168.0.0/16"), true)));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchInAnySubnetWithNegatedMember() {
		Result<Void> result = NetworkMatchers.matchInAnySubnet("192.168.1.1", Optional.of(Pair.of(Set.of("192.168.0.0/16"), true)));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("must not be member of any specified subnet"));
	}
	
	@Test
	void matchInAnySubnetWithInvalidCidr() {
		Result<Void> result = NetworkMatchers.matchInAnySubnet("192.168.1.1", Optional.of(Pair.of(Set.of("invalid"), false)));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Invalid CIDR"));
	}
	
	@Test
	void matchInAnySubnetWithInvalidIp() {
		Result<Void> result = NetworkMatchers.matchInAnySubnet("invalid", Optional.of(Pair.of(Set.of("192.168.0.0/16"), false)));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("not a valid ip address"));
	}
	
	@Test
	void matchInAnySubnetWithMixedSubnets() {
		Result<Void> result = NetworkMatchers.matchInAnySubnet("192.168.1.1", Optional.of(Pair.of(Set.of("192.168.0.0/16", "2001:db8::/32"), false)));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchInAnySubnetWithNullChecks() {
		assertThrows(NullPointerException.class, () -> NetworkMatchers.matchInAnySubnet(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> NetworkMatchers.matchInAnySubnet("192.168.1.1", null));
	}
	
	@Test
	void matchRootDomainWithEmptyOptional() {
		Result<Void> result = NetworkMatchers.matchRootDomain("example.com", Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchRootDomainWithValidRootDomain() {
		Result<Void> result = NetworkMatchers.matchRootDomain("example.com", Optional.of(Unit.INSTANCE));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchRootDomainWithSubDomain() {
		Result<Void> result = NetworkMatchers.matchRootDomain("sub.example.com", Optional.of(Unit.INSTANCE));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("must be a root domain"));
	}
	
	@Test
	void matchRootDomainWithNoDot() {
		Result<Void> result = NetworkMatchers.matchRootDomain("localhost", Optional.of(Unit.INSTANCE));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("must be a root domain"));
	}
	
	@Test
	void matchRootDomainWithNullChecks() {
		assertThrows(NullPointerException.class, () -> NetworkMatchers.matchRootDomain(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> NetworkMatchers.matchRootDomain("example.com", null));
	}
	
	@Test
	void matchSubDomainWithEmptyOptional() {
		Result<Void> result = NetworkMatchers.matchSubDomain("sub.example.com", Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchSubDomainWithValidSubDomain() {
		Result<Void> result = NetworkMatchers.matchSubDomain("sub.example.com", Optional.of(Unit.INSTANCE));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchSubDomainWithRootDomain() {
		Result<Void> result = NetworkMatchers.matchSubDomain("example.com", Optional.of(Unit.INSTANCE));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("must be a subdomain"));
	}
	
	@Test
	void matchSubDomainWithNullChecks() {
		assertThrows(NullPointerException.class, () -> NetworkMatchers.matchSubDomain(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> NetworkMatchers.matchSubDomain("sub.example.com", null));
	}
	
	@Test
	void matchPathCanonicalWithEmptyOptional() {
		Result<Void> result = NetworkMatchers.matchPathCanonical(Path.of("/some/path"), Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchPathCanonicalWithNullChecks() {
		assertThrows(NullPointerException.class, () -> NetworkMatchers.matchPathCanonical(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> NetworkMatchers.matchPathCanonical(Path.of("/some/path"), null));
	}
	
	@Test
	void matchPathStringConfigWithEmptyOptional() {
		Result<Void> result = NetworkMatchers.matchPathStringConfig(Path.of("/some/path"), Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchPathStringConfigWithMatch() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withStartsWith("/");
		Result<Void> result = NetworkMatchers.matchPathStringConfig(Path.of("/some/path"), Optional.of(config));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchPathStringConfigWithNoMatch() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withStartsWith("/usr");
		Result<Void> result = NetworkMatchers.matchPathStringConfig(Path.of("/home/user"), Optional.of(config));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Path constraint failed"));
	}
	
	@Test
	void matchPathStringConfigWithNullChecks() {
		assertThrows(NullPointerException.class, () -> NetworkMatchers.matchPathStringConfig(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> NetworkMatchers.matchPathStringConfig(Path.of("/some/path"), null));
	}
	
	@Test
	void matchPathRootConfigWithEmptyOptional() {
		Result<Void> result = NetworkMatchers.matchPathRootConfig(Path.of("/some/path"), Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchPathRootConfigWithNoRoot() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withEqualTo("/");
		Result<Void> result = NetworkMatchers.matchPathRootConfig(Path.of("relative/path"), Optional.of(config));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("has no root component"));
	}
	
	@Test
	void matchPathRootConfigWithNullChecks() {
		assertThrows(NullPointerException.class, () -> NetworkMatchers.matchPathRootConfig(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> NetworkMatchers.matchPathRootConfig(Path.of("/some/path"), null));
	}
	
	@Test
	void matchPathParentConfigWithEmptyOptional() {
		Result<Void> result = NetworkMatchers.matchPathParentConfig(Path.of("/some/path"), Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchPathParentConfigWithNoParent() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withContains("some");
		Result<Void> result = NetworkMatchers.matchPathParentConfig(Path.of("file"), Optional.of(config));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("has no parent component"));
	}
	
	@Test
	void matchPathParentConfigWithNullChecks() {
		assertThrows(NullPointerException.class, () -> NetworkMatchers.matchPathParentConfig(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> NetworkMatchers.matchPathParentConfig(Path.of("/some/path"), null));
	}
	
	@Test
	void matchPathSegmentConfigWithEmptyOptional() {
		Result<Void> result = NetworkMatchers.matchPathSegmentConfig(Path.of("/some/path"), Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchPathSegmentConfigWithMatch() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withAlphabetic();
		Result<Void> result = NetworkMatchers.matchPathSegmentConfig(Path.of("some/path"), Optional.of(config));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchPathSegmentConfigWithNoMatch() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withNumeric();
		Result<Void> result = NetworkMatchers.matchPathSegmentConfig(Path.of("some/path"), Optional.of(config));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Segment"));
	}
	
	@Test
	void matchPathSegmentConfigWithNullChecks() {
		assertThrows(NullPointerException.class, () -> NetworkMatchers.matchPathSegmentConfig(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> NetworkMatchers.matchPathSegmentConfig(Path.of("/some/path"), null));
	}
	
	@Test
	void matchPathFileNameConfigWithEmptyOptional() {
		Result<Void> result = NetworkMatchers.matchPathFileNameConfig(Path.of("/some/file.txt"), Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchPathFileNameConfigWithMatch() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withEndsWith(".txt");
		Result<Void> result = NetworkMatchers.matchPathFileNameConfig(Path.of("/some/file.txt"), Optional.of(config));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchPathFileNameConfigWithNoMatch() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withEndsWith(".java");
		Result<Void> result = NetworkMatchers.matchPathFileNameConfig(Path.of("/some/file.txt"), Optional.of(config));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("File constraint failed"));
	}
	
	@Test
	void matchPathFileNameConfigWithNullChecks() {
		assertThrows(NullPointerException.class, () -> NetworkMatchers.matchPathFileNameConfig(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> NetworkMatchers.matchPathFileNameConfig(Path.of("/some/file.txt"), null));
	}
	
	@Test
	void matchPathExtensionConfigWithEmptyOptional() {
		Result<Void> result = NetworkMatchers.matchPathExtensionConfig(Path.of("/some/file.txt"), Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchPathExtensionConfigWithMatch() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withEqualTo("txt");
		Result<Void> result = NetworkMatchers.matchPathExtensionConfig(Path.of("/some/file.txt"), Optional.of(config));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchPathExtensionConfigWithNoExtension() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withEqualTo("txt");
		Result<Void> result = NetworkMatchers.matchPathExtensionConfig(Path.of("/some/file"), Optional.of(config));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("has no extension"));
	}
	
	@Test
	void matchPathExtensionConfigWithNullChecks() {
		assertThrows(NullPointerException.class, () -> NetworkMatchers.matchPathExtensionConfig(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> NetworkMatchers.matchPathExtensionConfig(Path.of("/some/file.txt"), null));
	}
	
	@Test
	void matchPathWithoutExtensionWithEmptyOptional() {
		Result<Void> result = NetworkMatchers.matchPathWithoutExtension(Path.of("/some/file.txt"), Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchPathWithoutExtensionWithNoExtension() {
		Result<Void> result = NetworkMatchers.matchPathWithoutExtension(Path.of("/some/file"), Optional.of(Unit.INSTANCE));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchPathWithoutExtensionWithExtension() {
		Result<Void> result = NetworkMatchers.matchPathWithoutExtension(Path.of("/some/file.txt"), Optional.of(Unit.INSTANCE));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("must not have an extension"));
	}
	
	@Test
	void matchPathWithoutExtensionWithNullChecks() {
		assertThrows(NullPointerException.class, () -> NetworkMatchers.matchPathWithoutExtension(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> NetworkMatchers.matchPathWithoutExtension(Path.of("/some/file"), null));
	}
	
	@Test
	void matchPathAncestorOfWithEmptyOptional() {
		Result<Void> result = NetworkMatchers.matchPathAncestorOf(Path.of("/some"), Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchPathAncestorOfWithValid() {
		Result<Void> result = NetworkMatchers.matchPathAncestorOf(Path.of("/some"), Optional.of(Set.of("/some/path", "/some/other")));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchPathAncestorOfWithInvalid() {
		Result<Void> result = NetworkMatchers.matchPathAncestorOf(Path.of("/other"), Optional.of(Set.of("/some/path")));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("must be ancestor of"));
	}
	
	@Test
	void matchPathAncestorOfWithNullChecks() {
		assertThrows(NullPointerException.class, () -> NetworkMatchers.matchPathAncestorOf(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> NetworkMatchers.matchPathAncestorOf(Path.of("/some"), null));
	}
	
	@Test
	void matchPathDescendantOfWithEmptyOptional() {
		Result<Void> result = NetworkMatchers.matchPathDescendantOf(Path.of("/some/path/file"), Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchPathDescendantOfWithValid() {
		Result<Void> result = NetworkMatchers.matchPathDescendantOf(Path.of("/some/path/file"), Optional.of(Set.of("/some", "/some/path")));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchPathDescendantOfWithInvalid() {
		Result<Void> result = NetworkMatchers.matchPathDescendantOf(Path.of("/other/path"), Optional.of(Set.of("/some")));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("must be descendant of"));
	}
	
	@Test
	void matchPathDescendantOfWithNullChecks() {
		assertThrows(NullPointerException.class, () -> NetworkMatchers.matchPathDescendantOf(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> NetworkMatchers.matchPathDescendantOf(Path.of("/some/path"), null));
	}
	
	@Test
	void matchQueryValueConstraintsWithEmptyOptional() {
		Map<String, List<String>> query = Map.of("key", List.of("value"));
		Result<Void> result = NetworkMatchers.matchQueryValueConstraints(query, Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchQueryValueConstraintsWithMatch() {
		Map<String, List<String>> query = Map.of("key", List.of("value"));
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withEqualTo("value");
		Result<Void> result = NetworkMatchers.matchQueryValueConstraints(query, Optional.of(Map.of("key", config)));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchQueryValueConstraintsWithNoMatch() {
		Map<String, List<String>> query = Map.of("key", List.of("wrong"));
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withEqualTo("value");
		Result<Void> result = NetworkMatchers.matchQueryValueConstraints(query, Optional.of(Map.of("key", config)));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Value constraint for key"));
	}
	
	@Test
	void matchQueryValueConstraintsWithNullChecks() {
		assertThrows(NullPointerException.class, () -> NetworkMatchers.matchQueryValueConstraints(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> NetworkMatchers.matchQueryValueConstraints(Map.of(), null));
	}
	
	@Test
	void matchQueryPatternValueConstraintsWithEmptyOptional() {
		Map<String, List<String>> query = Map.of("key1", List.of("value"));
		Result<Void> result = NetworkMatchers.matchQueryPatternValueConstraints(query, Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchQueryPatternValueConstraintsWithMatch() {
		Map<String, List<String>> query = Map.of("key1", List.of("value"));
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withAlphabetic();
		Result<Void> result = NetworkMatchers.matchQueryPatternValueConstraints(query, Optional.of(Map.of(Pattern.compile("key\\d+"), config)));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchQueryPatternValueConstraintsWithNoMatch() {
		Map<String, List<String>> query = Map.of("key1", List.of("123"));
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withAlphabetic();
		Result<Void> result = NetworkMatchers.matchQueryPatternValueConstraints(query, Optional.of(Map.of(Pattern.compile("key\\d+"), config)));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Pattern value constraint"));
	}
	
	@Test
	void matchQueryPatternValueConstraintsWithNullChecks() {
		assertThrows(NullPointerException.class, () -> NetworkMatchers.matchQueryPatternValueConstraints(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> NetworkMatchers.matchQueryPatternValueConstraints(Map.of(), null));
	}
	
	@Test
	void matchQuerySingleValuedWithEmptyOptional() {
		Map<String, List<String>> query = Map.of("key", List.of("value1", "value2"));
		Result<Void> result = NetworkMatchers.matchQuerySingleValued(query, Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchQuerySingleValuedWithSingleValues() {
		Map<String, List<String>> query = Map.of("key1", List.of("value1"), "key2", List.of("value2"));
		Result<Void> result = NetworkMatchers.matchQuerySingleValued(query, Optional.of(Unit.INSTANCE));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchQuerySingleValuedWithMultipleValues() {
		Map<String, List<String>> query = Map.of("key", List.of("value1", "value2"));
		Result<Void> result = NetworkMatchers.matchQuerySingleValued(query, Optional.of(Unit.INSTANCE));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("must have exactly one value"));
	}
	
	@Test
	void matchQuerySingleValuedWithNullChecks() {
		assertThrows(NullPointerException.class, () -> NetworkMatchers.matchQuerySingleValued(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> NetworkMatchers.matchQuerySingleValued(Map.of(), null));
	}
	
	@Test
	void matchQueryUniqueValuesWithEmptyOptional() {
		Map<String, List<String>> query = Map.of("key1", List.of("value"), "key2", List.of("value"));
		Result<Void> result = NetworkMatchers.matchQueryUniqueValues(query, Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchQueryUniqueValuesWithUnique() {
		Map<String, List<String>> query = Map.of("key1", List.of("value1"), "key2", List.of("value2"));
		Result<Void> result = NetworkMatchers.matchQueryUniqueValues(query, Optional.of(Unit.INSTANCE));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchQueryUniqueValuesWithDuplicates() {
		Map<String, List<String>> query = Map.of("key1", List.of("value", "other"), "key2", List.of("value"));
		Result<Void> result = NetworkMatchers.matchQueryUniqueValues(query, Optional.of(Unit.INSTANCE));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("must be unique"));
	}
	
	@Test
	void matchQueryUniqueValuesWithNullChecks() {
		assertThrows(NullPointerException.class, () -> NetworkMatchers.matchQueryUniqueValues(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> NetworkMatchers.matchQueryUniqueValues(Map.of(), null));
	}
	
	@Test
	void matchQueryMultiValuedConstraintsWithEmptyOptional() {
		Map<String, List<String>> query = Map.of("key", List.of("v1", "v2", "v3"));
		Result<Void> result = NetworkMatchers.matchQueryMultiValuedConstraints(query, Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchQueryMultiValuedConstraintsWithMatch() {
		Map<String, List<String>> query = Map.of("key", List.of("v1", "v2"));
		SizeConstraintConfig config = SizeConstraintConfig.UNCONSTRAINED.withSizeBetween(1, 3);
		Result<Void> result = NetworkMatchers.matchQueryMultiValuedConstraints(query, Optional.of(Map.of("key", config)));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchQueryMultiValuedConstraintsWithNoMatch() {
		Map<String, List<String>> query = Map.of("key", List.of("v1", "v2", "v3", "v4", "v5"));
		SizeConstraintConfig config = SizeConstraintConfig.UNCONSTRAINED.withMaxSize(3);
		Result<Void> result = NetworkMatchers.matchQueryMultiValuedConstraints(query, Optional.of(Map.of("key", config)));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Multi-valued constraint"));
	}
	
	@Test
	void matchQueryMultiValuedConstraintsWithNullChecks() {
		assertThrows(NullPointerException.class, () -> NetworkMatchers.matchQueryMultiValuedConstraints(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> NetworkMatchers.matchQueryMultiValuedConstraints(Map.of(), null));
	}
	
	@Test
	void matchUriSchemeConfigWithEmptyOptional() {
		URI uri = URI.create("https://example.com");
		Result<Void> result = NetworkMatchers.matchUriSchemeConfig(uri, Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchUriSchemeConfigWithMatch() {
		URI uri = URI.create("https://example.com");
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withEqualTo("https");
		Result<Void> result = NetworkMatchers.matchUriSchemeConfig(uri, Optional.of(config));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchUriSchemeConfigWithNoScheme() {
		URI uri = URI.create("//example.com");
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withEqualTo("https");
		Result<Void> result = NetworkMatchers.matchUriSchemeConfig(uri, Optional.of(config));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("has no scheme"));
	}
	
	@Test
	void matchUriSchemeConfigWithNullChecks() {
		assertThrows(NullPointerException.class, () -> NetworkMatchers.matchUriSchemeConfig(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> NetworkMatchers.matchUriSchemeConfig(URI.create("https://example.com"), null));
	}
	
	@Test
	void matchUriHostConfigWithEmptyOptional() {
		URI uri = URI.create("https://example.com");
		Result<Void> result = NetworkMatchers.matchUriHostConfig(uri, Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchUriHostConfigWithMatch() {
		URI uri = URI.create("https://example.com");
		HostConstraintConfig config = HostConstraintConfig.UNCONSTRAINED.withEqualTo("example.com");
		Result<Void> result = NetworkMatchers.matchUriHostConfig(uri, Optional.of(config));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchUriHostConfigWithNoMatch() {
		URI uri = URI.create("https://example.com");
		HostConstraintConfig config = HostConstraintConfig.UNCONSTRAINED.withEqualTo("other.com");
		Result<Void> result = NetworkMatchers.matchUriHostConfig(uri, Optional.of(config));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Host constraint failed"));
	}
	
	@Test
	void matchUriHostConfigWithNoHost() {
		URI uri = URI.create("file:/path/to/file");
		HostConstraintConfig config = HostConstraintConfig.UNCONSTRAINED.withEqualTo("example.com");
		Result<Void> result = NetworkMatchers.matchUriHostConfig(uri, Optional.of(config));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("has no host"));
	}
	
	@Test
	void matchUriHostConfigWithNullChecks() {
		assertThrows(NullPointerException.class, () -> NetworkMatchers.matchUriHostConfig(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> NetworkMatchers.matchUriHostConfig(URI.create("https://example.com"), null));
	}
	
	@Test
	void matchUriUserInfoConfigWithEmptyOptional() {
		URI uri = URI.create("https://user:pass@example.com");
		Result<Void> result = NetworkMatchers.matchUriUserInfoConfig(uri, Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchUriUserInfoConfigWithMatch() {
		URI uri = URI.create("https://user:pass@example.com");
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withContains("user");
		Result<Void> result = NetworkMatchers.matchUriUserInfoConfig(uri, Optional.of(config));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchUriUserInfoConfigWithNoUserInfo() {
		URI uri = URI.create("https://example.com");
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withContains("user");
		Result<Void> result = NetworkMatchers.matchUriUserInfoConfig(uri, Optional.of(config));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("has no user info"));
	}
	
	@Test
	void matchUriUserInfoConfigWithNullChecks() {
		assertThrows(NullPointerException.class, () -> NetworkMatchers.matchUriUserInfoConfig(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> NetworkMatchers.matchUriUserInfoConfig(URI.create("https://example.com"), null));
	}
	
	@Test
	void matchUriPortConfigWithEmptyOptional() {
		URI uri = URI.create("https://example.com:8080");
		Result<Void> result = NetworkMatchers.matchUriPortConfig(uri, Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchUriPortConfigWithMatch() {
		URI uri = URI.create("https://example.com:8080");
		PortConstraintConfig config = PortConstraintConfig.UNCONSTRAINED.withEqualTo(8080);
		Result<Void> result = NetworkMatchers.matchUriPortConfig(uri, Optional.of(config));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchUriPortConfigWithNoMatch() {
		URI uri = URI.create("https://example.com:8080");
		PortConstraintConfig config = PortConstraintConfig.UNCONSTRAINED.withEqualTo(443);
		Result<Void> result = NetworkMatchers.matchUriPortConfig(uri, Optional.of(config));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Port constraint failed"));
	}
	
	@Test
	void matchUriPortConfigWithNoPort() {
		URI uri = URI.create("https://example.com");
		PortConstraintConfig config = PortConstraintConfig.UNCONSTRAINED.withEqualTo(8080);
		Result<Void> result = NetworkMatchers.matchUriPortConfig(uri, Optional.of(config));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("has no port"));
	}
	
	@Test
	void matchUriPortConfigWithNullChecks() {
		assertThrows(NullPointerException.class, () -> NetworkMatchers.matchUriPortConfig(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> NetworkMatchers.matchUriPortConfig(URI.create("https://example.com:8080"), null));
	}
	
	@Test
	void matchUriPathConfigWithEmptyOptional() {
		URI uri = URI.create("https://example.com/path/to/resource");
		Result<Void> result = NetworkMatchers.matchUriPathConfig(uri, Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchUriPathConfigWithMatch() {
		URI uri = URI.create("https://example.com/path/to/resource");
		PathConstraintConfig config = PathConstraintConfig.UNCONSTRAINED.withMinDepth(2);
		Result<Void> result = NetworkMatchers.matchUriPathConfig(uri, Optional.of(config));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchUriPathConfigWithNoMatch() {
		URI uri = URI.create("https://example.com/path");
		PathConstraintConfig config = PathConstraintConfig.UNCONSTRAINED.withMinDepth(3);
		Result<Void> result = NetworkMatchers.matchUriPathConfig(uri, Optional.of(config));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Path constraint failed"));
	}
	
	@Test
	void matchUriPathConfigWithNoPath() {
		URI uri = URI.create("https://example.com");
		PathConstraintConfig config = PathConstraintConfig.UNCONSTRAINED.withMinDepth(1);
		Result<Void> result = NetworkMatchers.matchUriPathConfig(uri, Optional.of(config));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("has no path"));
	}
	
	@Test
	void matchUriPathConfigWithNullChecks() {
		assertThrows(NullPointerException.class, () -> NetworkMatchers.matchUriPathConfig(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> NetworkMatchers.matchUriPathConfig(URI.create("https://example.com/path"), null));
	}
	
	@Test
	void matchUriQueryConfigWithEmptyOptional() {
		URI uri = URI.create("https://example.com?key=value");
		Result<Void> result = NetworkMatchers.matchUriQueryConfig(uri, Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchUriQueryConfigWithMatch() {
		URI uri = URI.create("https://example.com?key=value");
		QueryConstraintConfig config = QueryConstraintConfig.UNCONSTRAINED.withRequiredKeys(List.of("key"));
		Result<Void> result = NetworkMatchers.matchUriQueryConfig(uri, Optional.of(config));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchUriQueryConfigWithNoMatch() {
		URI uri = URI.create("https://example.com?key=value");
		QueryConstraintConfig config = QueryConstraintConfig.UNCONSTRAINED.withRequiredKeys(List.of("missing"));
		Result<Void> result = NetworkMatchers.matchUriQueryConfig(uri, Optional.of(config));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Query constraint failed"));
	}
	
	@Test
	void matchUriQueryConfigWithNoQuery() {
		URI uri = URI.create("https://example.com");
		QueryConstraintConfig config = QueryConstraintConfig.UNCONSTRAINED.withMinSize(1);
		Result<Void> result = NetworkMatchers.matchUriQueryConfig(uri, Optional.of(config));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("has no query"));
	}
	
	@Test
	void matchUriQueryConfigWithNullChecks() {
		assertThrows(NullPointerException.class, () -> NetworkMatchers.matchUriQueryConfig(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> NetworkMatchers.matchUriQueryConfig(URI.create("https://example.com?key=value"), null));
	}
	
	@Test
	void matchUriFragmentConfigWithEmptyOptional() {
		URI uri = URI.create("https://example.com#section");
		Result<Void> result = NetworkMatchers.matchUriFragmentConfig(uri, Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchUriFragmentConfigWithMatch() {
		URI uri = URI.create("https://example.com#section");
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withEqualTo("section");
		Result<Void> result = NetworkMatchers.matchUriFragmentConfig(uri, Optional.of(config));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchUriFragmentConfigWithNoFragment() {
		URI uri = URI.create("https://example.com");
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withEqualTo("section");
		Result<Void> result = NetworkMatchers.matchUriFragmentConfig(uri, Optional.of(config));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("has no fragment"));
	}
	
	@Test
	void matchUriFragmentConfigWithNullChecks() {
		assertThrows(NullPointerException.class, () -> NetworkMatchers.matchUriFragmentConfig(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> NetworkMatchers.matchUriFragmentConfig(URI.create("https://example.com"), null));
	}
	
	@Test
	void parseQueryWithEmptyString() {
		Map<String, List<String>> result = NetworkMatchers.parseQuery("");
		assertTrue(result.isEmpty());
	}
	
	@Test
	void parseQueryWithSingleParam() {
		Map<String, List<String>> result = NetworkMatchers.parseQuery("key=value");
		assertEquals(1, result.size());
		assertEquals(List.of("value"), result.get("key"));
	}
	
	@Test
	void parseQueryWithMultipleParams() {
		Map<String, List<String>> result = NetworkMatchers.parseQuery("key1=value1&key2=value2");
		assertEquals(2, result.size());
		assertEquals(List.of("value1"), result.get("key1"));
		assertEquals(List.of("value2"), result.get("key2"));
	}
	
	@Test
	void parseQueryWithRepeatedKeys() {
		Map<String, List<String>> result = NetworkMatchers.parseQuery("key=value1&key=value2");
		assertEquals(1, result.size());
		assertEquals(List.of("value1", "value2"), result.get("key"));
	}
	
	@Test
	void parseQueryWithEmptyValue() {
		Map<String, List<String>> result = NetworkMatchers.parseQuery("key=");
		assertEquals(1, result.size());
		assertEquals(List.of(""), result.get("key"));
	}
	
	@Test
	void parseQueryWithNoValue() {
		Map<String, List<String>> result = NetworkMatchers.parseQuery("key");
		assertEquals(1, result.size());
		assertEquals(List.of(""), result.get("key"));
	}
	
	@Test
	void parseQueryWithNullChecks() {
		assertThrows(NullPointerException.class, () -> NetworkMatchers.parseQuery(null));
	}
}
