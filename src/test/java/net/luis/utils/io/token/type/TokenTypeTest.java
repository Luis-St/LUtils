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

package net.luis.utils.io.token.type;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link TokenType}.<br>
 *
 * @author Luis-St
 */
class TokenTypeTest {
	
	@Test
	void getBaseTypeWithNoSuperType() {
		TokenType baseType = StandardTokenType.SYNTAX;
		
		assertEquals(baseType, baseType.getBaseType());
	}
	
	@Test
	void getBaseTypeWithSuperType() {
		TokenType type = StandardTokenType.INTEGER;
		
		assertEquals(StandardTokenType.LITERAL, type.getBaseType());
	}
	
	@Test
	void getBaseTypeWithMultipleLevels() {
		TokenType type = StandardTokenType.KEYWORD;
		
		assertEquals(StandardTokenType.SYNTAX, type.getBaseType());
	}
	
	@Test
	void isInstanceOfSelf() {
		TokenType type = StandardTokenType.STRING;
		
		assertTrue(type.isInstanceOf(StandardTokenType.STRING));
	}
	
	@Test
	void isInstanceOfSuperType() {
		TokenType type = StandardTokenType.INTEGER;
		
		assertTrue(type.isInstanceOf(StandardTokenType.NUMBER));
		assertTrue(type.isInstanceOf(StandardTokenType.LITERAL));
	}
	
	@Test
	void isInstanceOfUnrelated() {
		TokenType type = StandardTokenType.INTEGER;
		
		assertFalse(type.isInstanceOf(StandardTokenType.SYNTAX));
		assertFalse(type.isInstanceOf(StandardTokenType.OPERATOR));
	}
	
	@Test
	void isInstanceOfWithNullType() {
		TokenType type = StandardTokenType.STRING;
		
		assertThrows(NullPointerException.class, () -> type.isInstanceOf(null));
	}
	
	@Test
	void getHierarchyWithNoSuperType() {
		TokenType type = StandardTokenType.SYNTAX;
		
		TokenType[] hierarchy = type.getHierarchy();
		
		assertEquals(1, hierarchy.length);
		assertEquals(StandardTokenType.SYNTAX, hierarchy[0]);
	}
	
	@Test
	void getHierarchyWithSuperType() {
		TokenType type = StandardTokenType.INTEGER;
		
		TokenType[] hierarchy = type.getHierarchy();
		
		assertEquals(3, hierarchy.length);
		assertEquals(StandardTokenType.LITERAL, hierarchy[0]);
		assertEquals(StandardTokenType.NUMBER, hierarchy[1]);
		assertEquals(StandardTokenType.INTEGER, hierarchy[2]);
	}
	
	@Test
	void getHierarchyWithMultipleLevels() {
		TokenType type = StandardTokenType.KEYWORD;
		
		TokenType[] hierarchy = type.getHierarchy();
		
		assertEquals(2, hierarchy.length);
		assertEquals(StandardTokenType.SYNTAX, hierarchy[0]);
		assertEquals(StandardTokenType.KEYWORD, hierarchy[1]);
	}
	
	@Test
	void getHierarchyPathWithNoSuperType() {
		TokenType type = StandardTokenType.SYNTAX;
		
		String path = type.getHierarchyPath();
		
		assertEquals("Syntax", path);
	}
	
	@Test
	void getHierarchyPathWithSuperType() {
		TokenType type = StandardTokenType.INTEGER;
		
		String path = type.getHierarchyPath();
		
		assertEquals("Literal/NumericLiteral/Integer", path);
	}
	
	@Test
	void getHierarchyPathWithMultipleLevels() {
		TokenType type = StandardTokenType.KEYWORD;
		
		String path = type.getHierarchyPath();
		
		assertEquals("Syntax/Keyword", path);
	}
	
	@Test
	void isSyntaxTokenTrue() {
		assertTrue(StandardTokenType.SYNTAX.isSyntaxToken());
		assertTrue(StandardTokenType.KEYWORD.isSyntaxToken());
		assertTrue(StandardTokenType.MODIFIER.isSyntaxToken());
	}
	
	@Test
	void isSyntaxTokenFalse() {
		assertFalse(StandardTokenType.LITERAL.isSyntaxToken());
		assertFalse(StandardTokenType.STRING.isSyntaxToken());
		assertFalse(StandardTokenType.OPERATOR.isSyntaxToken());
	}
	
	@Test
	void isIdentifierTokenTrue() {
		assertTrue(StandardTokenType.IDENTIFIER.isIdentifierToken());
		assertTrue(StandardTokenType.TYPE_IDENTIFIER.isIdentifierToken());
		assertTrue(StandardTokenType.VARIABLE_IDENTIFIER.isIdentifierToken());
		assertTrue(StandardTokenType.FUNCTION_IDENTIFIER.isIdentifierToken());
	}
	
	@Test
	void isIdentifierTokenFalse() {
		assertFalse(StandardTokenType.LITERAL.isIdentifierToken());
		assertFalse(StandardTokenType.SYNTAX.isIdentifierToken());
		assertFalse(StandardTokenType.OPERATOR.isIdentifierToken());
	}
	
	@Test
	void isLiteralTokenTrue() {
		assertTrue(StandardTokenType.LITERAL.isLiteralToken());
		assertTrue(StandardTokenType.STRING.isLiteralToken());
		assertTrue(StandardTokenType.INTEGER.isLiteralToken());
		assertTrue(StandardTokenType.BOOLEAN.isLiteralToken());
	}
	
	@Test
	void isLiteralTokenFalse() {
		assertFalse(StandardTokenType.SYNTAX.isLiteralToken());
		assertFalse(StandardTokenType.OPERATOR.isLiteralToken());
		assertFalse(StandardTokenType.IDENTIFIER.isLiteralToken());
	}
	
	@Test
	void isOperatorTokenTrue() {
		assertTrue(StandardTokenType.OPERATOR.isOperatorToken());
		assertTrue(StandardTokenType.ARITHMETIC_OPERATOR.isOperatorToken());
		assertTrue(StandardTokenType.LOGICAL_OPERATOR.isOperatorToken());
		assertTrue(StandardTokenType.UNARY_OPERATOR.isOperatorToken());
	}
	
	@Test
	void isOperatorTokenFalse() {
		assertFalse(StandardTokenType.LITERAL.isOperatorToken());
		assertFalse(StandardTokenType.SYNTAX.isOperatorToken());
		assertFalse(StandardTokenType.DELIMITER.isOperatorToken());
	}
	
	@Test
	void isDelimiterTokenTrue() {
		assertTrue(StandardTokenType.DELIMITER.isDelimiterToken());
		assertTrue(StandardTokenType.PARENTHESIS.isDelimiterToken());
		assertTrue(StandardTokenType.BRACE.isDelimiterToken());
		assertTrue(StandardTokenType.SEPARATOR.isDelimiterToken());
	}
	
	@Test
	void isDelimiterTokenFalse() {
		assertFalse(StandardTokenType.LITERAL.isDelimiterToken());
		assertFalse(StandardTokenType.OPERATOR.isDelimiterToken());
		assertFalse(StandardTokenType.WHITESPACE.isDelimiterToken());
	}
	
	@Test
	void isWhitespaceTokenTrue() {
		assertTrue(StandardTokenType.WHITESPACE.isWhitespaceToken());
		assertTrue(StandardTokenType.SPACE.isWhitespaceToken());
		assertTrue(StandardTokenType.TAB.isWhitespaceToken());
		assertTrue(StandardTokenType.NEWLINE.isWhitespaceToken());
	}
	
	@Test
	void isWhitespaceTokenFalse() {
		assertFalse(StandardTokenType.LITERAL.isWhitespaceToken());
		assertFalse(StandardTokenType.OPERATOR.isWhitespaceToken());
		assertFalse(StandardTokenType.DELIMITER.isWhitespaceToken());
	}
	
	@Test
	void isCommentTokenTrue() {
		assertTrue(StandardTokenType.COMMENT.isCommentToken());
		assertTrue(StandardTokenType.SINGLE_LINE_COMMENT.isCommentToken());
		assertTrue(StandardTokenType.MULTI_LINE_COMMENT.isCommentToken());
		assertTrue(StandardTokenType.DOCUMENTATION_COMMENT.isCommentToken());
	}
	
	@Test
	void isCommentTokenFalse() {
		assertFalse(StandardTokenType.LITERAL.isCommentToken());
		assertFalse(StandardTokenType.OPERATOR.isCommentToken());
		assertFalse(StandardTokenType.WHITESPACE.isCommentToken());
	}
	
	@Test
	void isSpecialTokenTrue() {
		assertTrue(StandardTokenType.SPECIAL.isSpecialToken());
		assertTrue(StandardTokenType.ERROR.isSpecialToken());
		assertTrue(StandardTokenType.EOF.isSpecialToken());
		assertTrue(StandardTokenType.UNKNOWN.isSpecialToken());
		assertTrue(StandardTokenType.PREPROCESSOR.isSpecialToken());
	}
	
	@Test
	void isSpecialTokenFalse() {
		assertFalse(StandardTokenType.LITERAL.isSpecialToken());
		assertFalse(StandardTokenType.OPERATOR.isSpecialToken());
		assertFalse(StandardTokenType.COMMENT.isSpecialToken());
	}
	
	@Test
	void customTokenTypeHierarchy() {
		CustomTokenType customBase = new CustomTokenType("CustomBase", null);
		CustomTokenType customChild = new CustomTokenType("CustomChild", customBase);
		
		assertTrue(customChild.isInstanceOf(customBase));
		assertFalse(customBase.isInstanceOf(customChild));
		assertEquals(customBase, customChild.getBaseType());
		
		TokenType[] hierarchy = customChild.getHierarchy();
		assertEquals(2, hierarchy.length);
		assertEquals(customBase, hierarchy[0]);
		assertEquals(customChild, hierarchy[1]);
	}
}
