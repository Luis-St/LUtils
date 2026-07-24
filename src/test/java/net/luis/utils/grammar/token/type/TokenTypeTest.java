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

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link TokenType}.<br>
 *
 * @author Luis-St
 */
class TokenTypeTest {
	
	@Test
	void isInstanceOfWithNullTypeThrows() {
		assertThrows(NullPointerException.class, () -> new CustomTokenType("A", null).isInstanceOf(null));
	}
	
	@Test
	void getBaseTypeWithNoSuperTypeReturnsItself() {
		CustomTokenType root = new CustomTokenType("Root", null);
		assertEquals(root, root.getBaseType());
	}
	
	@Test
	void getBaseTypeWithSuperTypeChainReturnsTopMostType() {
		CustomTokenType root = new CustomTokenType("Root", null);
		CustomTokenType mid = new CustomTokenType("Mid", root);
		CustomTokenType leaf = new CustomTokenType("Leaf", mid);
		assertEquals(root, leaf.getBaseType());
	}
	
	@Test
	void isInstanceOfReturnsTrueForSameType() {
		CustomTokenType type = new CustomTokenType("A", null);
		assertTrue(type.isInstanceOf(type));
	}
	
	@Test
	void isInstanceOfReturnsTrueForAncestorType() {
		CustomTokenType root = new CustomTokenType("Root", null);
		CustomTokenType mid = new CustomTokenType("Mid", root);
		CustomTokenType leaf = new CustomTokenType("Leaf", mid);
		assertTrue(leaf.isInstanceOf(root));
	}
	
	@Test
	void isInstanceOfReturnsFalseForUnrelatedType() {
		CustomTokenType root = new CustomTokenType("Root", null);
		CustomTokenType mid = new CustomTokenType("Mid", root);
		CustomTokenType leaf = new CustomTokenType("Leaf", mid);
		CustomTokenType other = new CustomTokenType("Other", null);
		assertFalse(leaf.isInstanceOf(other));
	}
	
	@Test
	void getHierarchyWithNoSuperTypeReturnsSingleElementArray() {
		CustomTokenType root = new CustomTokenType("Root", null);
		TokenType[] hierarchy = root.getHierarchy();
		assertEquals(1, hierarchy.length);
		assertEquals(root, hierarchy[0]);
	}
	
	@Test
	void getHierarchyWithSuperTypeChainReturnsOrderedArray() {
		CustomTokenType root = new CustomTokenType("Root", null);
		CustomTokenType mid = new CustomTokenType("Mid", root);
		CustomTokenType leaf = new CustomTokenType("Leaf", mid);
		TokenType[] hierarchy = leaf.getHierarchy();
		assertEquals(3, hierarchy.length);
		assertSame(root, hierarchy[0]);
		assertSame(mid, hierarchy[1]);
		assertSame(leaf, hierarchy[2]);
	}
	
	@Test
	void getHierarchyPathWithSingleTypeOmitsSeparator() {
		CustomTokenType root = new CustomTokenType("Root", null);
		assertEquals("Root", root.getHierarchyPath());
	}
	
	@Test
	void getHierarchyPathWithMultipleTypesJoinsWithSlash() {
		CustomTokenType root = new CustomTokenType("Root", null);
		CustomTokenType mid = new CustomTokenType("Mid", root);
		CustomTokenType leaf = new CustomTokenType("Leaf", mid);
		assertEquals("Root/Mid/Leaf", leaf.getHierarchyPath());
	}
	
	@Test
	void isSyntaxTokenReturnsTrueForSyntaxItself() {
		assertTrue(StandardTokenType.SYNTAX.isSyntaxToken());
	}
	
	@Test
	void isSyntaxTokenReturnsTrueForSubtype() {
		assertTrue(StandardTokenType.KEYWORD.isSyntaxToken());
	}
	
	@Test
	void isSyntaxTokenReturnsFalseForUnrelatedType() {
		assertFalse(StandardTokenType.IDENTIFIER.isSyntaxToken());
	}
	
	@Test
	void isIdentifierTokenReturnsTrueForIdentifierItself() {
		assertTrue(StandardTokenType.IDENTIFIER.isIdentifierToken());
	}
	
	@Test
	void isIdentifierTokenReturnsTrueForSubtype() {
		assertTrue(StandardTokenType.VARIABLE_IDENTIFIER.isIdentifierToken());
	}
	
	@Test
	void isIdentifierTokenReturnsFalseForUnrelatedType() {
		assertFalse(StandardTokenType.KEYWORD.isIdentifierToken());
	}
	
	@Test
	void isLiteralTokenReturnsTrueForLiteralItself() {
		assertTrue(StandardTokenType.LITERAL.isLiteralToken());
	}
	
	@Test
	void isLiteralTokenReturnsTrueForSubtype() {
		assertTrue(StandardTokenType.STRING.isLiteralToken());
	}
	
	@Test
	void isLiteralTokenReturnsFalseForUnrelatedType() {
		assertFalse(StandardTokenType.OPERATOR.isLiteralToken());
	}
	
	@Test
	void isOperatorTokenReturnsTrueForOperatorItself() {
		assertTrue(StandardTokenType.OPERATOR.isOperatorToken());
	}
	
	@Test
	void isOperatorTokenReturnsTrueForSubtype() {
		assertTrue(StandardTokenType.ARITHMETIC_OPERATOR.isOperatorToken());
	}
	
	@Test
	void isOperatorTokenReturnsFalseForUnrelatedType() {
		assertFalse(StandardTokenType.LITERAL.isOperatorToken());
	}
	
	@Test
	void isDelimiterTokenReturnsTrueForDelimiterItself() {
		assertTrue(StandardTokenType.DELIMITER.isDelimiterToken());
	}
	
	@Test
	void isDelimiterTokenReturnsTrueForSubtype() {
		assertTrue(StandardTokenType.PARENTHESIS.isDelimiterToken());
	}
	
	@Test
	void isDelimiterTokenReturnsFalseForUnrelatedType() {
		assertFalse(StandardTokenType.OPERATOR.isDelimiterToken());
	}
	
	@Test
	void isWhitespaceTokenReturnsTrueForWhitespaceItself() {
		assertTrue(StandardTokenType.WHITESPACE.isWhitespaceToken());
	}
	
	@Test
	void isWhitespaceTokenReturnsTrueForSubtype() {
		assertTrue(StandardTokenType.SPACE.isWhitespaceToken());
	}
	
	@Test
	void isWhitespaceTokenReturnsFalseForUnrelatedType() {
		assertFalse(StandardTokenType.COMMENT.isWhitespaceToken());
	}
	
	@Test
	void isCommentTokenReturnsTrueForCommentItself() {
		assertTrue(StandardTokenType.COMMENT.isCommentToken());
	}
	
	@Test
	void isCommentTokenReturnsTrueForSubtype() {
		assertTrue(StandardTokenType.SINGLE_LINE_COMMENT.isCommentToken());
	}
	
	@Test
	void isCommentTokenReturnsFalseForUnrelatedType() {
		assertFalse(StandardTokenType.WHITESPACE.isCommentToken());
	}
	
	@Test
	void isSpecialTokenReturnsTrueForSpecialItself() {
		assertTrue(StandardTokenType.SPECIAL.isSpecialToken());
	}
	
	@Test
	void isSpecialTokenReturnsTrueForSubtype() {
		assertTrue(StandardTokenType.ERROR.isSpecialToken());
	}
	
	@Test
	void isSpecialTokenReturnsFalseForUnrelatedType() {
		assertFalse(StandardTokenType.COMMENT.isSpecialToken());
	}
	
	@Test
	void getHierarchyAndHierarchyPathConsistentForDeepChain() {
		CustomTokenType a = new CustomTokenType("A", null);
		CustomTokenType b = new CustomTokenType("B", a);
		CustomTokenType c = new CustomTokenType("C", b);
		CustomTokenType d = new CustomTokenType("D", c);
		assertEquals(4, d.getHierarchy().length);
		assertEquals("A/B/C/D", d.getHierarchyPath());
	}
	
	@Test
	void isInstanceOfAcrossMixedStandardAndCustomHierarchy() {
		CustomTokenType custom = new CustomTokenType("MyKeyword", StandardTokenType.KEYWORD);
		assertTrue(custom.isInstanceOf(StandardTokenType.SYNTAX));
		assertEquals(StandardTokenType.SYNTAX, custom.getBaseType());
		assertTrue(custom.isSyntaxToken());
	}
}
