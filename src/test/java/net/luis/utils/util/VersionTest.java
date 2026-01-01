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

package net.luis.utils.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link Version}.<br>
 *
 * @author Luis-St
 */
class VersionTest {
	
	@Test
	void ofTwoParametersCreatesVersion() {
		assertNotNull(Version.of(1, 0));
		assertEquals(1, Version.of(1, 0).getMajor());
		assertEquals(0, Version.of(1, 0).getMinor());
		assertEquals(0, Version.of(1, 0).getPatch());
	}
	
	@Test
	void ofTwoParametersHandlesZeroAndNegative() {
		assertSame(Version.ZERO, Version.of(0, 0));
		assertSame(Version.ZERO, Version.of(-1, -1));
		assertNotSame(Version.ZERO, Version.of(1, 0));
	}
	
	@Test
	void ofThreeParametersCreatesVersion() {
		assertNotNull(Version.of(1, 2, 3));
		assertEquals(1, Version.of(1, 2, 3).getMajor());
		assertEquals(2, Version.of(1, 2, 3).getMinor());
		assertEquals(3, Version.of(1, 2, 3).getPatch());
	}
	
	@Test
	void ofThreeParametersHandlesZeroAndNegative() {
		assertSame(Version.ZERO, Version.of(0, 0, 0));
		assertSame(Version.ZERO, Version.of(-1, -1, -1));
		assertNotSame(Version.ZERO, Version.of(1, 0, 0));
	}
	
	@Test
	void builderTwoParametersCreatesBuilder() {
		Version.Builder builder = Version.builder(1, 2);
		assertNotNull(builder);
		assertEquals(Version.of(1, 2), builder.build());
	}
	
	@Test
	void builderThreeParametersCreatesBuilder() {
		Version.Builder builder = Version.builder(1, 2, 3);
		assertNotNull(builder);
		assertEquals(Version.of(1, 2, 3), builder.build());
	}
	
	@Test
	void parseBasicVersionStrings() {
		assertEquals(Version.of(1, 0), Version.parse("1.0"));
		assertEquals(Version.of(1, 2), Version.parse("1.2"));
		assertEquals(Version.of(1, 0, 0), Version.parse("1.0.0"));
		assertEquals(Version.of(1, 2, 3), Version.parse("1.2.3"));
		assertEquals(Version.of(1, 0), Version.parse("v1.0"));
		assertEquals(Version.of(1, 2, 3), Version.parse("v1.2.3"));
	}
	
	@Test
	void parseVersionsWithBuildNumbers() {
		assertEquals(Version.builder(1, 2, 3).withBuild('.', 4).build(), Version.parse("1.2.3.4"));
		assertEquals(Version.builder(1, 2, 3).withBuild('-', 4).build(), Version.parse("1.2.3-4"));
		assertEquals(Version.builder(1, 2, 3).withBuild('r', 4).build(), Version.parse("1.2.3r4"));
		assertEquals(Version.builder(1, 2).withBuild('-', 4).build(), Version.parse("1.2-4"));
		assertEquals(Version.builder(1, 2).withBuild('r', 4).build(), Version.parse("1.2r4"));
	}
	
	@Test
	void parseVersionsWithSuffixes() {
		String[] suffixes = { "alpha", "beta", "rc", "release", "final" };
		for (String suffix : suffixes) {
			assertEquals(Version.builder(1, 2).withSuffix(suffix).build(), Version.parse("1.2-" + suffix));
			assertEquals(Version.builder(1, 2, 3).withSuffix(suffix).build(), Version.parse("1.2.3-" + suffix));
		}
	}
	
	@Test
	void parseVersionsWithSuffixVersions() {
		assertEquals(Version.builder(1, 2).withSuffixVersion(1).build(), Version.parse("1.2+001"));
		assertEquals(Version.builder(1, 2, 3).withSuffixVersion(42).build(), Version.parse("1.2.3+042"));
		assertEquals(Version.builder(1, 2, 3).withSuffixVersion(999).build(), Version.parse("1.2.3+999"));
	}
	
	@Test
	void parseComplexVersionStrings() {
		char[] separators = { '.', '-', 'r' };
		String[] suffixes = { "alpha", "beta", "rc" };
		
		for (char separator : separators) {
			for (String suffix : suffixes) {
				if (separator != '.') {
					String versionStr = "1.2" + separator + "4-" + suffix + "+001";
					Version expected = Version.builder(1, 2).withBuild(separator, 4).withSuffix(suffix).withSuffixVersion(1).build();
					assertEquals(expected, Version.parse(versionStr));
				}
				
				String versionStr = "1.2.3" + separator + "4-" + suffix + "+001";
				Version expected = Version.builder(1, 2, 3).withBuild(separator, 4).withSuffix(suffix).withSuffixVersion(1).build();
				assertEquals(expected, Version.parse(versionStr));
			}
		}
	}
	
	@Test
	void parseInvalidStringsReturnsZero() {
		assertSame(Version.ZERO, Version.parse(null));
		assertSame(Version.ZERO, Version.parse(""));
		assertSame(Version.ZERO, Version.parse(" "));
		assertSame(Version.ZERO, Version.parse("abcde"));
		assertSame(Version.ZERO, Version.parse("-1.-1"));
		assertSame(Version.ZERO, Version.parse("1"));
		assertSame(Version.ZERO, Version.parse("1.2.3.4.5"));
	}
	
	@Test
	void getMajorReturnsCorrectValue() {
		assertEquals(0, Version.ZERO.getMajor());
		assertEquals(1, Version.of(1, 2, 3).getMajor());
		assertEquals(10, Version.of(10, 5, 2).getMajor());
		assertEquals(0, Version.of(-1, 0, 0).getMajor());
	}
	
	@Test
	void getMinorReturnsCorrectValue() {
		assertEquals(0, Version.ZERO.getMinor());
		assertEquals(2, Version.of(1, 2, 3).getMinor());
		assertEquals(15, Version.of(1, 15, 3).getMinor());
		assertEquals(0, Version.of(0, -1, 0).getMinor());
	}
	
	@Test
	void getPatchReturnsCorrectValue() {
		assertEquals(0, Version.ZERO.getPatch());
		assertEquals(0, Version.of(1, 2).getPatch());
		assertEquals(3, Version.of(1, 2, 3).getPatch());
		assertEquals(0, Version.of(0, 0, -1).getPatch());
	}
	
	@Test
	void getBuildReturnsCorrectValue() {
		assertEquals(0, Version.ZERO.getBuild());
		assertEquals(0, Version.of(1, 2, 3).getBuild());
		
		for (char separator : new char[] { '.', '-', 'r' }) {
			assertEquals(4, Version.builder(1, 2).withBuild(separator, 4).build().getBuild());
			assertEquals(0, Version.builder(1, 2).withBuild(separator, -1).build().getBuild());
		}
	}
	
	@Test
	void getSuffixReturnsCorrectValue() {
		assertEquals("", Version.ZERO.getSuffix());
		assertEquals("", Version.of(1, 2, 3).getSuffix());
		
		String[] suffixes = { "alpha", "beta", "rc", "release", "final" };
		for (String suffix : suffixes) {
			assertEquals(suffix, Version.builder(1, 2).withSuffix(suffix).build().getSuffix());
		}
	}
	
	@Test
	void getSuffixIndexReturnsCorrectValue() {
		assertEquals(0, Version.ZERO.getSuffixIndex());
		assertEquals(0, Version.of(1, 2, 3).getSuffixIndex());
		assertEquals(1, Version.builder(1, 2).withSuffix("alpha").build().getSuffixIndex());
		assertEquals(2, Version.builder(1, 2).withSuffix("beta").build().getSuffixIndex());
		assertEquals(3, Version.builder(1, 2).withSuffix("rc").build().getSuffixIndex());
		assertEquals(3, Version.builder(1, 2).withSuffix("release-candidate").build().getSuffixIndex());
		assertEquals(4, Version.builder(1, 2).withSuffix("release").build().getSuffixIndex());
		assertEquals(5, Version.builder(1, 2).withSuffix("final").build().getSuffixIndex());
		assertEquals(0, Version.builder(1, 2).withSuffix("unknown").build().getSuffixIndex());
	}
	
	@Test
	void getSuffixVersionReturnsCorrectValue() {
		assertEquals(0, Version.ZERO.getSuffixVersion());
		assertEquals(0, Version.of(1, 2, 3).getSuffixVersion());
		assertEquals(1, Version.builder(1, 2).withSuffixVersion(1).build().getSuffixVersion());
		assertEquals(0, Version.builder(1, 2).withSuffixVersion(-1).build().getSuffixVersion());
	}
	
	@Test
	void asBuilderCreatesEquivalentBuilder() {
		Version original = Version.of(1, 2, 3);
		assertEquals(original, original.asBuilder().build());
		
		Version complex = Version.builder(1, 2, 3).withBuild('.', 4).withSuffix("alpha").withSuffixVersion(1).build();
		assertEquals(complex, complex.asBuilder().build());
	}
	
	@Test
	void compareToOrdersVersionsCorrectly() {
		Version v100 = Version.of(1, 0, 0);
		Version v110 = Version.of(1, 1, 0);
		Version v111 = Version.of(1, 1, 1);
		Version v200 = Version.of(2, 0, 0);
		
		assertTrue(v100.compareTo(Version.ZERO) > 0);
		assertTrue(v110.compareTo(v100) > 0);
		assertTrue(v111.compareTo(v110) > 0);
		assertTrue(v200.compareTo(v111) > 0);
		
		assertEquals(0, v100.compareTo(v100));
		assertTrue(v100.compareTo(v110) < 0);
	}
	
	@Test
	void compareToHandlesBuildVersions() {
		Version base = Version.of(1, 0, 0);
		Version withBuild = Version.builder(1, 0, 0).withBuild('.', 1).build();
		
		assertTrue(withBuild.compareTo(base) > 0);
		assertTrue(base.compareTo(withBuild) < 0);
	}
	
	@Test
	void compareToHandlesSuffixes() {
		Version release = Version.of(1, 0, 0);
		Version alpha = Version.builder(1, 0, 0).withSuffix("alpha").build();
		Version beta = Version.builder(1, 0, 0).withSuffix("beta").build();
		Version rc = Version.builder(1, 0, 0).withSuffix("rc").build();
		
		assertTrue(alpha.compareTo(beta) < 0);
		assertTrue(beta.compareTo(rc) < 0);
		assertTrue(rc.compareTo(release) < 0);
		assertTrue(release.compareTo(alpha) > 0);
	}
	
	@Test
	void compareToHandlesSuffixVersions() {
		Version alpha1 = Version.builder(1, 0, 0).withSuffix("alpha").withSuffixVersion(1).build();
		Version alpha2 = Version.builder(1, 0, 0).withSuffix("alpha").withSuffixVersion(2).build();
		
		assertTrue(alpha1.compareTo(alpha2) < 0);
		assertTrue(alpha2.compareTo(alpha1) > 0);
		assertEquals(0, alpha1.compareTo(alpha1));
	}
	
	@Test
	void equalsComparesAllComponents() {
		Version v1 = Version.of(1, 2, 3);
		Version v2 = Version.of(1, 2, 3);
		Version different = Version.of(1, 2, 4);
		
		assertEquals(v1, v2);
		assertNotEquals(v1, different);
		assertNotEquals(v1, null);
		assertNotEquals(v1, "not a version");
	}
	
	@Test
	void equalsHandlesComplexVersions() {
		Version complex1 = Version.builder(1, 2, 3).withBuild('.', 4).withSuffix("alpha").withSuffixVersion(1).build();
		Version complex2 = Version.builder(1, 2, 3).withBuild('.', 4).withSuffix("alpha").withSuffixVersion(1).build();
		Version differentBuild = Version.builder(1, 2, 3).withBuild('-', 4).withSuffix("alpha").withSuffixVersion(1).build();
		
		assertEquals(complex1, complex2);
		assertNotEquals(complex1, differentBuild);
	}
	
	@Test
	void hashCodeIsSameForEqualObjects() {
		Version v1 = Version.of(1, 2, 3);
		Version v2 = Version.of(1, 2, 3);
		
		assertEquals(v1.hashCode(), v2.hashCode());
	}
	
	@Test
	void toStringFormatsCorrectly() {
		assertEquals("v1.0", Version.of(1, 0).toString(true));
		assertEquals("1.0", Version.of(1, 0).toString(false));
		assertEquals("v1.2.3", Version.of(1, 2, 3).toString(true));
		assertEquals("1.2.3", Version.of(1, 2, 3).toString(false));
	}
	
	@Test
	void toStringWithOmitZerosFlag() {
		assertEquals("1.0.0", Version.of(1, 0, 0).toString(false, false));
		assertEquals("1.0", Version.of(1, 0, 0).toString(false, true));
		assertEquals("1.2.3", Version.of(1, 2, 3).toString(false, false));
		assertEquals("1.2.3", Version.of(1, 2, 3).toString(false, true));
	}
	
	@Test
	void toStringWithComplexVersions() {
		Version complex = Version.builder(1, 2, 3).withBuild('.', 4).withSuffix("alpha").withSuffixVersion(1).build();
		assertEquals("v1.2.3.4-alpha+001", complex.toString(true));
		assertEquals("1.2.3.4-alpha+001", complex.toString(false));
		
		Version withOmittablePatch = Version.builder(1, 2, 0).withBuild('-', 4).withSuffix("beta").build();
		assertEquals("1.2.0-4-beta", withOmittablePatch.toString(false, false));
		assertEquals("1.2-4-beta", withOmittablePatch.toString(false, true));
	}
	
	@Test
	void builderWithMajor() {
		Version version = Version.builder(0, 0).withMajor(5).build();
		assertEquals(5, version.getMajor());
		assertEquals(0, version.getMinor());
	}
	
	@Test
	void builderWithMinor() {
		Version version = Version.builder(1, 0).withMinor(7).build();
		assertEquals(1, version.getMajor());
		assertEquals(7, version.getMinor());
	}
	
	@Test
	void builderWithPatch() {
		Version version = Version.builder(1, 2).withPatch(9).build();
		assertEquals(1, version.getMajor());
		assertEquals(2, version.getMinor());
		assertEquals(9, version.getPatch());
	}
	
	@Test
	void builderWithBuild() {
		Version version = Version.builder(1, 2).withBuild('.', 4).build();
		assertEquals(4, version.getBuild());
		
		assertThrows(IllegalArgumentException.class, () -> Version.builder(1, 2).withBuild('x', 4));
	}
	
	@Test
	void builderWithSuffix() {
		Version version = Version.builder(1, 2).withSuffix("alpha").build();
		assertEquals("alpha", version.getSuffix());
		
		Version nullSuffix = Version.builder(1, 2).withSuffix(null).build();
		assertEquals("", nullSuffix.getSuffix());
	}
	
	@Test
	void builderWithSuffixVersion() {
		Version version = Version.builder(1, 2).withSuffixVersion(42).build();
		assertEquals(42, version.getSuffixVersion());
		
		Version negativeSuffix = Version.builder(1, 2).withSuffixVersion(-1).build();
		assertEquals(0, negativeSuffix.getSuffixVersion());
	}
	
	@Test
	void builderBuildReturnsValidVersion() {
		assertNotNull(Version.builder(1, 2).build());
		assertNotNull(Version.builder(1, 2, 3).build());
		
		Version complex = Version.builder(1, 2, 3)
			.withBuild('.', 4)
			.withSuffix("alpha")
			.withSuffixVersion(1)
			.build();
		assertNotNull(complex);
		assertEquals(1, complex.getMajor());
		assertEquals(2, complex.getMinor());
		assertEquals(3, complex.getPatch());
		assertEquals(4, complex.getBuild());
		assertEquals("alpha", complex.getSuffix());
		assertEquals(1, complex.getSuffixVersion());
	}
	
	@Test
	void builderEqualsAndHashCode() {
		Version.Builder builder1 = Version.builder(1, 2, 3).withSuffix("alpha");
		Version.Builder builder2 = Version.builder(1, 2, 3).withSuffix("alpha");
		Version.Builder different = Version.builder(1, 2, 4).withSuffix("alpha");
		
		assertEquals(builder1, builder2);
		assertNotEquals(builder1, different);
		assertEquals(builder1.hashCode(), builder2.hashCode());
	}
	
	@Test
	void zeroConstantProperties() {
		assertEquals(0, Version.ZERO.getMajor());
		assertEquals(0, Version.ZERO.getMinor());
		assertEquals(0, Version.ZERO.getPatch());
		assertEquals(0, Version.ZERO.getBuild());
		assertEquals("", Version.ZERO.getSuffix());
		assertEquals(0, Version.ZERO.getSuffixVersion());
		assertEquals("v0.0", Version.ZERO.toString());
	}
}
