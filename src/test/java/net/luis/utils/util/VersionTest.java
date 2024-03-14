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

import net.luis.utils.math.Mth;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link Version}.<br>
 *
 * @author Luis-St
 */
class VersionTest {
	
	@Test
	void of() {
		assertNotNull(Version.of(0, 0, 0));
		assertSame(Version.ZERO, Version.of(0, 0, 0));
		assertSame(Version.ZERO, Version.of(-1, -1, -1));
	}
	
	@Test
	void builder() {
		assertNotNull(Version.builder(0, 0, 0));
		assertEquals(Version.ZERO, Version.builder(0, 0, 0).build());
		assertEquals(Version.ZERO, Version.builder(-1, -1, -1).build());
		assertEquals(Version.of(1, 0, 0), Version.builder(1, 0, 0).build());
	}
	
	@Test
	void parse() {
		char[] separators = { '.', '-', 'r' };
		String[] suffixes = { "alpha", "beta", "rc", "release", "final" };
		assertDoesNotThrow(() -> Version.parse(null));
		assertSame(Version.ZERO, Version.parse(null));
		assertSame(Version.ZERO, Version.parse(""));
		assertSame(Version.ZERO, Version.parse(" "));
		assertSame(Version.ZERO, Version.parse("abcde"));
		assertSame(Version.ZERO, Version.parse("-1.-1.-1"));
		assertEquals(Version.of(1, 0, 0), Version.parse("1.0.0"));
		assertEquals(Version.of(1, 2, 3), Version.parse("1.2.3"));
		for (char separator : separators) {
			assertEquals(Version.builder(1, 2, 3).withBuild(separator, 4).build(), Version.parse("1.2.3" + separator + "4"));
		}
		for (String suffix : suffixes) {
			assertEquals(Version.builder(1, 2, 3).withSuffix(suffix).build(), Version.parse("1.2.3-" + suffix));
		}
		assertEquals(Version.builder(1, 2, 3).withSuffixVersion(0).build(), Version.parse("1.2.3+000"));
		for (char separator : separators) {
			for (String suffix : suffixes) {
				assertEquals(Version.builder(1, 2, 3).withBuild(separator, 4).withSuffix(suffix).build(), Version.parse("1.2.3" + separator + "4-" + suffix));
				assertEquals(Version.builder(1, 2, 3).withBuild(separator, 4).withSuffix(suffix).withSuffixVersion(1).build(), Version.parse("1.2.3" + separator + "4-" + suffix + "+001"));
			}
		}
	}
	
	@Test
	void getMajor() {
		assertEquals(0, Version.ZERO.getMajor());
		assertEquals(0, Version.of(-1, 0, 0).getMajor());
		assertEquals(1, Version.of(1, 0, 0).getMajor());
		assertEquals(1, Version.of(1, 2, 3).getMajor());
	}
	
	@Test
	void getMinor() {
		assertEquals(0, Version.ZERO.getMinor());
		assertEquals(0, Version.of(0, -1, 0).getMinor());
		assertEquals(1, Version.of(0, 1, 0).getMinor());
		assertEquals(2, Version.of(1, 2, 3).getMinor());
	}
	
	@Test
	void getPatch() {
		assertEquals(0, Version.ZERO.getPatch());
		assertEquals(0, Version.of(0, 0, -1).getPatch());
		assertEquals(1, Version.of(0, 0, 1).getPatch());
		assertEquals(3, Version.of(1, 2, 3).getPatch());
	}
	
	@Test
	void getBuild() {
		assertEquals(0, Version.ZERO.getBuild());
		for (char separator : new char[] { '.', '-', 'r' }) {
			assertEquals(0, Version.builder(0, 0, 0).withBuild(separator, -1).build().getBuild());
			assertEquals(0, Version.builder(0, 0, 0).withBuild(separator, 0).build().getBuild());
			assertEquals(1, Version.builder(0, 0, 0).withBuild(separator, 1).build().getBuild());
			assertEquals(4, Version.builder(1, 2, 3).withBuild(separator, 4).build().getBuild());
		}
	}
	
	@Test
	void getSuffix() {
		assertEquals("", Version.ZERO.getSuffix());
		for (String suffix : new String[] { "alpha", "beta", "rc", "release", "final" }) {
			assertEquals(suffix, Version.builder(0, 0, 0).withSuffix(suffix).build().getSuffix());
			assertEquals(suffix, Version.builder(1, 2, 3).withSuffix(suffix).build().getSuffix());
		}
	}
	
	@Test
	void getSuffixVersion() {
		assertEquals(0, Version.ZERO.getSuffixVersion());
		assertEquals(0, Version.builder(0, 0, 0).withSuffixVersion(-1).build().getSuffixVersion());
		assertEquals(0, Version.builder(0, 0, 0).withSuffixVersion(0).build().getSuffixVersion());
		assertEquals(1, Version.builder(0, 0, 0).withSuffixVersion(1).build().getSuffixVersion());
		assertEquals(1, Version.builder(1, 2, 3).withSuffixVersion(1).build().getSuffixVersion());
	}
	
	@Test
	void asBuilder() {
		assertEquals(Version.builder(0, 0, 0), Version.ZERO.asBuilder());
		assertEquals(Version.builder(1, 2, 3), Version.of(1, 2, 3).asBuilder());
	}
	
	@Test
	void compareTo() {
		Version version = Version.of(1, 2, 3);
		Version build = Version.builder(1, 2, 3).withBuild('.', 4).build();
		Version suffixVersion = Version.builder(1, 2, 3).withSuffixVersion(1).build();
		Version suffixAlpha = Version.builder(1, 2, 3).withSuffix("alpha").withSuffixVersion(1).build();
		
		Version alpha = Version.builder(1, 2, 3).withSuffix("alpha").build();
		Version beta = Version.builder(1, 2, 3).withSuffix("beta").build();
		Version rc = Version.builder(1, 2, 3).withSuffix("rc").build();
		Version release = Version.builder(1, 2, 3).withSuffix("release").build();
		
		// x.compareTo(y) -> x > y -> 1 | x < y -> -1 | x == y -> 0
		// Different versions
		assertTrue(version.compareTo(Version.ZERO) > 0);
		assertEquals(0, version.compareTo(version));
		assertTrue(version.compareTo(build) < 0);
		
		assertTrue(build.compareTo(Version.ZERO) > 0);
		assertTrue(build.compareTo(version) > 0);
		assertEquals(0, build.compareTo(build));
		
		assertTrue(suffixVersion.compareTo(Version.ZERO) > 0);
		assertTrue(suffixVersion.compareTo(build) < 0);
		assertTrue(suffixVersion.compareTo(version) > 0);
		assertTrue(suffixVersion.compareTo(suffixAlpha) > 0);
		assertEquals(0, suffixVersion.compareTo(suffixVersion));
		
		assertTrue(suffixAlpha.compareTo(Version.ZERO) > 0);
		assertTrue(suffixAlpha.compareTo(version) < 0);
		assertTrue(suffixAlpha.compareTo(alpha) > 0);
		assertEquals(0, suffixAlpha.compareTo(suffixAlpha));
		
		// Different suffix versions
		assertTrue(alpha.compareTo(Version.ZERO) > 0);
		assertTrue(alpha.compareTo(build) < 0);
		assertTrue(alpha.compareTo(beta) < 0);
		assertEquals(0, alpha.compareTo(alpha));
		
		assertTrue(beta.compareTo(Version.ZERO) > 0);
		assertTrue(beta.compareTo(alpha) > 0);
		assertTrue(beta.compareTo(rc) < 0);
		assertEquals(0, beta.compareTo(beta));
		
		assertTrue(rc.compareTo(Version.ZERO) > 0);
		assertTrue(rc.compareTo(beta) > 0);
		assertTrue(rc.compareTo(release) < 0);
		assertEquals(0, rc.compareTo(rc));
		
		assertTrue(release.compareTo(Version.ZERO) > 0);
		assertTrue(release.compareTo(rc) > 0);
		assertTrue(release.compareTo(version) < 0);
		assertEquals(0, release.compareTo(release));
	}
	
	@Test
	void testToString() {
		char[] separators = { '.', '-', 'r' };
		String[] suffixes = { "alpha", "beta", "rc", "release", "final" };
		assertEquals("0.0.0", Version.ZERO.toString(false));
		assertEquals("v0.0.0", Version.ZERO.toString(true));
		
		Random rng = new Random();
		for (char separator : separators) {
			for (String suffix : suffixes) {
				int mayor = Mth.randomInt(rng, 0, 20);
				int minor = Mth.randomInt(rng, 0, 20);
				int patch = Mth.randomInt(rng, 0, 20);
				int build = Mth.randomInt(rng, 0, 20);
				Version version = Version.builder(mayor, minor, patch).withBuild(separator, build).withSuffix(suffix).build();
				String expected = mayor + "." + minor + "." + patch + separator + build + "-" + suffix;
				assertEquals(expected, version.toString(false));
				assertEquals("v" + expected, version.toString(true));
			}
		}
	}
	
	@Nested
	class Builder {
		
		@Test
		void withMajor() {
			assertNotNull(Version.builder(0, 0, 0).withMajor(1));
		}
		
		@Test
		void withMinor() {
			assertNotNull(Version.builder(0, 0, 0).withMinor(1));
		}
		
		@Test
		void withPatch() {
			assertNotNull(Version.builder(0, 0, 0).withPatch(1));
		}
		
		@Test
		void withBuild() {
			assertNotNull(Version.builder(0, 0, 0).withBuild('.', 1));
		}
		
		@Test
		void withSuffix() {
			assertNotNull(Version.builder(0, 0, 0).withSuffix("alpha"));
		}
		
		@Test
		void withSuffixVersion() {
			assertNotNull(Version.builder(0, 0, 0).withSuffixVersion(1));
		}
		
		@Test
		void build() {
			assertNotNull(Version.builder(0, 0, 0).build());
		}
	}
}