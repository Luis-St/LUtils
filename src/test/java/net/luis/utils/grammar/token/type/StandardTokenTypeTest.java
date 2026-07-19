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

package net.luis.utils.grammar.token.type;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link StandardTokenType}.<br>
 *
 * @author Luis-St
 */
class StandardTokenTypeTest {
	
	@Test
	void enumConstantsConstructedWithNameOnly() {
		assertEquals("Syntax", StandardTokenType.SYNTAX.getName());
		assertNull(StandardTokenType.SYNTAX.getSuperType());
	}
	
	@Test
	void enumConstantsConstructedWithNameAndSuperType() {
		assertEquals("Keyword", StandardTokenType.KEYWORD.getName());
		assertEquals(StandardTokenType.SYNTAX, StandardTokenType.KEYWORD.getSuperType());
	}
	
	@Test
	void getSuperTypeReturnsNullForRootCategories() {
		assertNull(StandardTokenType.SYNTAX.getSuperType());
		assertNull(StandardTokenType.IDENTIFIER.getSuperType());
		assertNull(StandardTokenType.LITERAL.getSuperType());
		assertNull(StandardTokenType.OPERATOR.getSuperType());
		assertNull(StandardTokenType.DELIMITER.getSuperType());
		assertNull(StandardTokenType.WHITESPACE.getSuperType());
		assertNull(StandardTokenType.COMMENT.getSuperType());
		assertNull(StandardTokenType.SPECIAL.getSuperType());
	}
	
	@Test
	void getSuperTypeReturnsParentForSubCategories() {
		assertSame(StandardTokenType.SYNTAX, StandardTokenType.KEYWORD.getSuperType());
		assertSame(StandardTokenType.NUMBER, StandardTokenType.INTEGER.getSuperType());
	}
	
	@Test
	void getNameReturnsExpectedStringForEachConstant() {
		assertEquals("Modifier", StandardTokenType.MODIFIER.getName());
		assertEquals("TypeIdentifier", StandardTokenType.TYPE_IDENTIFIER.getName());
		assertEquals("Boolean", StandardTokenType.BOOLEAN.getName());
		assertEquals("TernaryOperator", StandardTokenType.TERNARY_OPERATOR.getName());
		assertEquals("AngleBracket", StandardTokenType.ANGLE_BRACKET.getName());
		assertEquals("Newline", StandardTokenType.NEWLINE.getName());
		assertEquals("DocumentationComment", StandardTokenType.DOCUMENTATION_COMMENT.getName());
		assertEquals("Unknown", StandardTokenType.UNKNOWN.getName());
	}
	
	@Test
	void valuesContainsAllDefinedConstants() {
		assertEquals(43, StandardTokenType.values().length);
	}
	
	@Test
	void deepHierarchyResolvesToCorrectBaseType() {
		assertEquals(StandardTokenType.LITERAL, StandardTokenType.INTEGER.getBaseType());
		assertEquals("Literal/NumericLiteral/Integer", StandardTokenType.INTEGER.getHierarchyPath());
	}
	
	@Test
	void valueOfRoundTripsForAllConstants() {
		Set<String> names = new HashSet<>();
		for (StandardTokenType constant : StandardTokenType.values()) {
			assertSame(constant, StandardTokenType.valueOf(constant.name()));
			assertTrue(names.add(constant.getName()));
		}
	}
}
