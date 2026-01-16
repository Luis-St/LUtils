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

package net.luis.utils.io.codec.constraint_new.config.network;

import net.luis.utils.io.codec.constraint_new.config.StringConstraintConfig;
import net.luis.utils.io.codec.constraint_new.core.Unit;
import net.luis.utils.util.Pair;
import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link URIConstraintConfig}.<br>
 *
 * @author Luis-St
 */
class URIConstraintConfigTest {

	@Test
	void constructor() {
		assertDoesNotThrow(() -> URIConstraintConfig.UNCONSTRAINED);
		assertDoesNotThrow(() -> new URIConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}

	@Test
	void constructorNullChecks() {
		assertThrows(NullPointerException.class, () -> new URIConstraintConfig(
			null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
		assertThrows(NullPointerException.class, () -> new URIConstraintConfig(
			Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
		assertThrows(NullPointerException.class, () -> new URIConstraintConfig(
			Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
		assertThrows(NullPointerException.class, () -> new URIConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
		assertThrows(NullPointerException.class, () -> new URIConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
		assertThrows(NullPointerException.class, () -> new URIConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
		assertThrows(NullPointerException.class, () -> new URIConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
		assertThrows(NullPointerException.class, () -> new URIConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
		assertThrows(NullPointerException.class, () -> new URIConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
		assertThrows(NullPointerException.class, () -> new URIConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null,
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
		assertThrows(NullPointerException.class, () -> new URIConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
		assertThrows(NullPointerException.class, () -> new URIConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
		assertThrows(NullPointerException.class, () -> new URIConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
		assertThrows(NullPointerException.class, () -> new URIConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
		assertThrows(NullPointerException.class, () -> new URIConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
		assertThrows(NullPointerException.class, () -> new URIConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty()
		));
		assertThrows(NullPointerException.class, () -> new URIConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty()
		));
		assertThrows(NullPointerException.class, () -> new URIConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty()
		));
		assertThrows(NullPointerException.class, () -> new URIConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null
		));
	}

	@Test
	void constructorEmptyInSet() {
		assertThrows(IllegalArgumentException.class, () -> new URIConstraintConfig(
			Optional.empty(), Optional.of(Pair.of(Set.of(), false)), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}

	@Test
	void constructorAbsoluteRelativeMutuallyExclusive() {
		assertThrows(IllegalArgumentException.class, () -> new URIConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(Unit.INSTANCE), Optional.of(Unit.INSTANCE), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}

	@Test
	void constructorOpaqueHierarchicalMutuallyExclusive() {
		assertThrows(IllegalArgumentException.class, () -> new URIConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(Unit.INSTANCE), Optional.of(Unit.INSTANCE), Optional.empty()
		));
	}

	@Test
	void constructorWithoutUserInfoAndUserInfoMutuallyExclusive() {
		assertThrows(IllegalArgumentException.class, () -> new URIConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(Unit.INSTANCE), Optional.of(StringConstraintConfig.UNCONSTRAINED), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}

	@Test
	void constructorWithoutPortAndPortMutuallyExclusive() {
		assertThrows(IllegalArgumentException.class, () -> new URIConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(Unit.INSTANCE), Optional.of(PortConstraintConfig.UNCONSTRAINED), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}

	@Test
	void constructorWithoutPathAndPathMutuallyExclusive() {
		assertThrows(IllegalArgumentException.class, () -> new URIConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(Unit.INSTANCE), Optional.of(PathConstraintConfig.UNCONSTRAINED),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}

	@Test
	void constructorWithoutQueryAndQueryMutuallyExclusive() {
		assertThrows(IllegalArgumentException.class, () -> new URIConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.of(Unit.INSTANCE), Optional.of(QueryConstraintConfig.UNCONSTRAINED), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}

	@Test
	void constructorWithoutFragmentAndFragmentMutuallyExclusive() {
		assertThrows(IllegalArgumentException.class, () -> new URIConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.of(Unit.INSTANCE), Optional.of(StringConstraintConfig.UNCONSTRAINED), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}

	@Test
	void unconstrained() {
		URIConstraintConfig config = URIConstraintConfig.UNCONSTRAINED;
		assertNotNull(config);
		assertTrue(config.equalTo().isEmpty());
		assertTrue(config.in().isEmpty());
		assertTrue(config.scheme().isEmpty());
		assertTrue(config.host().isEmpty());
		assertTrue(config.withoutUserInfo().isEmpty());
		assertTrue(config.userInfo().isEmpty());
		assertTrue(config.withoutPort().isEmpty());
		assertTrue(config.port().isEmpty());
		assertTrue(config.withoutPath().isEmpty());
		assertTrue(config.path().isEmpty());
		assertTrue(config.withoutQuery().isEmpty());
		assertTrue(config.query().isEmpty());
		assertTrue(config.withoutFragment().isEmpty());
		assertTrue(config.fragment().isEmpty());
		assertTrue(config.absolute().isEmpty());
		assertTrue(config.relative().isEmpty());
		assertTrue(config.opaque().isEmpty());
		assertTrue(config.hierarchical().isEmpty());
		assertTrue(config.custom().isEmpty());
	}

	@Test
	void withEqualTo() {
		URI value = URI.create("https://example.com");
		URIConstraintConfig config = URIConstraintConfig.UNCONSTRAINED.withEqualTo(value);

		assertTrue(config.equalTo().isPresent());
		assertEquals(value, config.equalTo().get().getFirst());
		assertFalse(config.equalTo().get().getSecond());
	}

	@Test
	void withEqualToNull() {
		assertThrows(NullPointerException.class, () -> URIConstraintConfig.UNCONSTRAINED.withEqualTo(null));
	}

	@Test
	void withNotEqualTo() {
		URI value = URI.create("https://blocked.com");
		URIConstraintConfig config = URIConstraintConfig.UNCONSTRAINED.withNotEqualTo(value);

		assertTrue(config.equalTo().isPresent());
		assertEquals(value, config.equalTo().get().getFirst());
		assertTrue(config.equalTo().get().getSecond());
	}

	@Test
	void withNotEqualToNull() {
		assertThrows(NullPointerException.class, () -> URIConstraintConfig.UNCONSTRAINED.withNotEqualTo(null));
	}

	@Test
	void withIn() {
		URI uri1 = URI.create("https://example.com");
		URI uri2 = URI.create("https://test.com");
		URIConstraintConfig config = URIConstraintConfig.UNCONSTRAINED.withIn(List.of(uri1, uri2));

		assertTrue(config.in().isPresent());
		assertEquals(2, config.in().get().getFirst().size());
		assertFalse(config.in().get().getSecond());
	}

	@Test
	void withInNull() {
		assertThrows(NullPointerException.class, () -> URIConstraintConfig.UNCONSTRAINED.withIn(null));
	}

	@Test
	void withNotIn() {
		URI uri = URI.create("https://blocked.com");
		URIConstraintConfig config = URIConstraintConfig.UNCONSTRAINED.withNotIn(List.of(uri));

		assertTrue(config.in().isPresent());
		assertTrue(config.in().get().getSecond());
	}

	@Test
	void withNotInNull() {
		assertThrows(NullPointerException.class, () -> URIConstraintConfig.UNCONSTRAINED.withNotIn(null));
	}

	@Test
	void withScheme() {
		StringConstraintConfig schemeConfig = StringConstraintConfig.UNCONSTRAINED.withIn(List.of("http", "https"));
		URIConstraintConfig config = URIConstraintConfig.UNCONSTRAINED.withScheme(schemeConfig);

		assertTrue(config.scheme().isPresent());
	}

	@Test
	void withSchemeNull() {
		assertThrows(NullPointerException.class, () -> URIConstraintConfig.UNCONSTRAINED.withScheme(null));
	}

	@Test
	void withHost() {
		HostConstraintConfig hostConfig = HostConstraintConfig.UNCONSTRAINED.withEqualTo("example.com");
		URIConstraintConfig config = URIConstraintConfig.UNCONSTRAINED.withHost(hostConfig);

		assertTrue(config.host().isPresent());
	}

	@Test
	void withHostNull() {
		assertThrows(NullPointerException.class, () -> URIConstraintConfig.UNCONSTRAINED.withHost(null));
	}

	@Test
	void withWithoutUserInfo() {
		URIConstraintConfig config = URIConstraintConfig.UNCONSTRAINED.withWithoutUserInfo();

		assertTrue(config.withoutUserInfo().isPresent());
		assertTrue(config.userInfo().isEmpty());
	}

	@Test
	void withUserInfo() {
		StringConstraintConfig userInfoConfig = StringConstraintConfig.UNCONSTRAINED.withMinLength(1);
		URIConstraintConfig config = URIConstraintConfig.UNCONSTRAINED.withUserInfo(userInfoConfig);

		assertTrue(config.userInfo().isPresent());
		assertTrue(config.withoutUserInfo().isEmpty());
	}

	@Test
	void withUserInfoNull() {
		assertThrows(NullPointerException.class, () -> URIConstraintConfig.UNCONSTRAINED.withUserInfo(null));
	}

	@Test
	void withWithoutPort() {
		URIConstraintConfig config = URIConstraintConfig.UNCONSTRAINED.withWithoutPort();

		assertTrue(config.withoutPort().isPresent());
		assertTrue(config.port().isEmpty());
	}

	@Test
	void withPort() {
		PortConstraintConfig portConfig = PortConstraintConfig.UNCONSTRAINED.withIn(List.of(80, 443));
		URIConstraintConfig config = URIConstraintConfig.UNCONSTRAINED.withPort(portConfig);

		assertTrue(config.port().isPresent());
		assertTrue(config.withoutPort().isEmpty());
	}

	@Test
	void withPortNull() {
		assertThrows(NullPointerException.class, () -> URIConstraintConfig.UNCONSTRAINED.withPort(null));
	}

	@Test
	void withWithoutPath() {
		URIConstraintConfig config = URIConstraintConfig.UNCONSTRAINED.withWithoutPath();

		assertTrue(config.withoutPath().isPresent());
		assertTrue(config.path().isEmpty());
	}

	@Test
	void withPath() {
		PathConstraintConfig pathConfig = PathConstraintConfig.UNCONSTRAINED.withMinLength(1);
		URIConstraintConfig config = URIConstraintConfig.UNCONSTRAINED.withPath(pathConfig);

		assertTrue(config.path().isPresent());
		assertTrue(config.withoutPath().isEmpty());
	}

	@Test
	void withPathNull() {
		assertThrows(NullPointerException.class, () -> URIConstraintConfig.UNCONSTRAINED.withPath(null));
	}

	@Test
	void withWithoutQuery() {
		URIConstraintConfig config = URIConstraintConfig.UNCONSTRAINED.withWithoutQuery();

		assertTrue(config.withoutQuery().isPresent());
		assertTrue(config.query().isEmpty());
	}

	@Test
	void withQuery() {
		QueryConstraintConfig queryConfig = QueryConstraintConfig.UNCONSTRAINED.withMinSize(1);
		URIConstraintConfig config = URIConstraintConfig.UNCONSTRAINED.withQuery(queryConfig);

		assertTrue(config.query().isPresent());
		assertTrue(config.withoutQuery().isEmpty());
	}

	@Test
	void withQueryNull() {
		assertThrows(NullPointerException.class, () -> URIConstraintConfig.UNCONSTRAINED.withQuery(null));
	}

	@Test
	void withWithoutFragment() {
		URIConstraintConfig config = URIConstraintConfig.UNCONSTRAINED.withWithoutFragment();

		assertTrue(config.withoutFragment().isPresent());
		assertTrue(config.fragment().isEmpty());
	}

	@Test
	void withFragment() {
		StringConstraintConfig fragmentConfig = StringConstraintConfig.UNCONSTRAINED.withMinLength(1);
		URIConstraintConfig config = URIConstraintConfig.UNCONSTRAINED.withFragment(fragmentConfig);

		assertTrue(config.fragment().isPresent());
		assertTrue(config.withoutFragment().isEmpty());
	}

	@Test
	void withFragmentNull() {
		assertThrows(NullPointerException.class, () -> URIConstraintConfig.UNCONSTRAINED.withFragment(null));
	}

	@Test
	void withAbsolute() {
		URIConstraintConfig config = URIConstraintConfig.UNCONSTRAINED.withAbsolute();

		assertTrue(config.absolute().isPresent());
		assertTrue(config.relative().isEmpty());
	}

	@Test
	void withRelative() {
		URIConstraintConfig config = URIConstraintConfig.UNCONSTRAINED.withRelative();

		assertTrue(config.relative().isPresent());
		assertTrue(config.absolute().isEmpty());
	}

	@Test
	void withOpaque() {
		URIConstraintConfig config = URIConstraintConfig.UNCONSTRAINED.withOpaque();

		assertTrue(config.opaque().isPresent());
		assertTrue(config.hierarchical().isEmpty());
	}

	@Test
	void withHierarchical() {
		URIConstraintConfig config = URIConstraintConfig.UNCONSTRAINED.withHierarchical();

		assertTrue(config.hierarchical().isPresent());
		assertTrue(config.opaque().isEmpty());
	}

	@Test
	void withCustom() {
		URIConstraintConfig config = URIConstraintConfig.UNCONSTRAINED.withCustom(value -> Result.success());

		assertTrue(config.custom().isPresent());
	}

	@Test
	void withCustomNull() {
		assertThrows(NullPointerException.class, () -> URIConstraintConfig.UNCONSTRAINED.withCustom(null));
	}

	@Test
	void matchesUnconstrained() {
		URIConstraintConfig config = URIConstraintConfig.UNCONSTRAINED;

		assertTrue(config.matches(URI.create("https://example.com")).isSuccess());
		assertTrue(config.matches(URI.create("http://localhost:8080/path")).isSuccess());
		assertTrue(config.matches(URI.create("ftp://files.example.com/file.txt")).isSuccess());
		assertTrue(config.matches(URI.create("relative/path")).isSuccess());
	}

	@Test
	void matchesWithNull() {
		assertThrows(NullPointerException.class, () -> URIConstraintConfig.UNCONSTRAINED.matches(null));
	}

	@Test
	void matchesEqualTo() {
		URI expected = URI.create("https://example.com");
		URIConstraintConfig config = URIConstraintConfig.UNCONSTRAINED.withEqualTo(expected);

		assertTrue(config.matches(URI.create("https://example.com")).isSuccess());
		assertTrue(config.matches(URI.create("https://other.com")).isError());
	}

	@Test
	void matchesNotEqualTo() {
		URI excluded = URI.create("https://blocked.com");
		URIConstraintConfig config = URIConstraintConfig.UNCONSTRAINED.withNotEqualTo(excluded);

		assertTrue(config.matches(URI.create("https://example.com")).isSuccess());
		assertTrue(config.matches(URI.create("https://blocked.com")).isError());
	}

	@Test
	void matchesIn() {
		URI uri1 = URI.create("https://example.com");
		URI uri2 = URI.create("https://test.com");
		URIConstraintConfig config = URIConstraintConfig.UNCONSTRAINED.withIn(List.of(uri1, uri2));

		assertTrue(config.matches(URI.create("https://example.com")).isSuccess());
		assertTrue(config.matches(URI.create("https://test.com")).isSuccess());
		assertTrue(config.matches(URI.create("https://other.com")).isError());
	}

	@Test
	void matchesNotIn() {
		URI excluded = URI.create("https://blocked.com");
		URIConstraintConfig config = URIConstraintConfig.UNCONSTRAINED.withNotIn(List.of(excluded));

		assertTrue(config.matches(URI.create("https://example.com")).isSuccess());
		assertTrue(config.matches(URI.create("https://blocked.com")).isError());
	}

	@Test
	void matchesAbsolute() {
		URIConstraintConfig config = URIConstraintConfig.UNCONSTRAINED.withAbsolute();

		assertTrue(config.matches(URI.create("https://example.com")).isSuccess());
		assertTrue(config.matches(URI.create("relative/path")).isError());
	}

	@Test
	void matchesRelative() {
		URIConstraintConfig config = URIConstraintConfig.UNCONSTRAINED.withRelative();

		assertTrue(config.matches(URI.create("relative/path")).isSuccess());
		assertTrue(config.matches(URI.create("https://example.com")).isError());
	}

	@Test
	void matchesWithoutFragment() {
		URIConstraintConfig config = URIConstraintConfig.UNCONSTRAINED.withWithoutFragment();

		assertTrue(config.matches(URI.create("https://example.com")).isSuccess());
		assertTrue(config.matches(URI.create("https://example.com#section")).isError());
	}

	@Test
	void matchesWithoutQuery() {
		URIConstraintConfig config = URIConstraintConfig.UNCONSTRAINED.withWithoutQuery();

		assertTrue(config.matches(URI.create("https://example.com")).isSuccess());
		assertTrue(config.matches(URI.create("https://example.com?param=value")).isError());
	}

	@Test
	void matchesWithoutPort() {
		URIConstraintConfig config = URIConstraintConfig.UNCONSTRAINED.withWithoutPort();

		assertTrue(config.matches(URI.create("https://example.com")).isSuccess());
		assertTrue(config.matches(URI.create("https://example.com:8080")).isError());
	}

	@Test
	void matchesWithoutPath() {
		URIConstraintConfig config = URIConstraintConfig.UNCONSTRAINED.withWithoutPath();

		assertTrue(config.matches(URI.create("https://example.com")).isSuccess());
		assertTrue(config.matches(URI.create("https://example.com/path")).isError());
	}

	@Test
	void matchesHierarchical() {
		URIConstraintConfig config = URIConstraintConfig.UNCONSTRAINED.withHierarchical();

		assertTrue(config.matches(URI.create("https://example.com")).isSuccess());
		assertTrue(config.matches(URI.create("mailto:user@example.com")).isError());
	}

	@Test
	void matchesOpaque() {
		URIConstraintConfig config = URIConstraintConfig.UNCONSTRAINED.withOpaque();

		assertTrue(config.matches(URI.create("mailto:user@example.com")).isSuccess());
		assertTrue(config.matches(URI.create("https://example.com")).isError());
	}
}
