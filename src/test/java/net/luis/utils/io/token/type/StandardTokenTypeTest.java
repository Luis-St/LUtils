/*
 * LUtils
 * Copyright (C) 2025 Luis Staudt
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
 * Test class for {@link StandardTokenType}.<br>
 *
 * @author Luis-St
 */
class StandardTokenTypeTest {
	
	@Test
	void syntaxTokens() {
		assertEquals("Syntax", StandardTokenType.SYNTAX.getName());
		assertNull(StandardTokenType.SYNTAX.getSuperType());
		
		assertEquals("Keyword", StandardTokenType.KEYWORD.getName());
		assertEquals(StandardTokenType.SYNTAX, StandardTokenType.KEYWORD.getSuperType());
		
		assertEquals("Modifier", StandardTokenType.MODIFIER.getName());
		assertEquals(StandardTokenType.SYNTAX, StandardTokenType.MODIFIER.getSuperType());
	}
	
	@Test
	void identifierTokens() {
		assertEquals("Identifier", StandardTokenType.IDENTIFIER.getName());
		assertNull(StandardTokenType.IDENTIFIER.getSuperType());
		
		assertEquals("TypeIdentifier", StandardTokenType.TYPE_IDENTIFIER.getName());
		assertEquals(StandardTokenType.IDENTIFIER, StandardTokenType.TYPE_IDENTIFIER.getSuperType());
		
		assertEquals("VariableIdentifier", StandardTokenType.VARIABLE_IDENTIFIER.getName());
		assertEquals(StandardTokenType.IDENTIFIER, StandardTokenType.VARIABLE_IDENTIFIER.getSuperType());
		
		assertEquals("FunctionIdentifier", StandardTokenType.FUNCTION_IDENTIFIER.getName());
		assertEquals(StandardTokenType.IDENTIFIER, StandardTokenType.FUNCTION_IDENTIFIER.getSuperType());
	}
	
	@Test
	void literalTokens() {
		assertEquals("Literal", StandardTokenType.LITERAL.getName());
		assertNull(StandardTokenType.LITERAL.getSuperType());
		
		assertEquals("NumericLiteral", StandardTokenType.NUMBER.getName());
		assertEquals(StandardTokenType.LITERAL, StandardTokenType.NUMBER.getSuperType());
		
		assertEquals("Integer", StandardTokenType.INTEGER.getName());
		assertEquals(StandardTokenType.NUMBER, StandardTokenType.INTEGER.getSuperType());
		
		assertEquals("Float", StandardTokenType.FLOAT.getName());
		assertEquals(StandardTokenType.NUMBER, StandardTokenType.FLOAT.getSuperType());
		
		assertEquals("Boolean", StandardTokenType.BOOLEAN.getName());
		assertEquals(StandardTokenType.LITERAL, StandardTokenType.BOOLEAN.getSuperType());
		
		assertEquals("Null", StandardTokenType.NULL.getName());
		assertEquals(StandardTokenType.LITERAL, StandardTokenType.NULL.getSuperType());
		
		assertEquals("Character", StandardTokenType.CHARACTER.getName());
		assertEquals(StandardTokenType.LITERAL, StandardTokenType.CHARACTER.getSuperType());
		
		assertEquals("String", StandardTokenType.STRING.getName());
		assertEquals(StandardTokenType.LITERAL, StandardTokenType.STRING.getSuperType());
	}
	
	@Test
	void operatorTokens() {
		assertEquals("Operator", StandardTokenType.OPERATOR.getName());
		assertNull(StandardTokenType.OPERATOR.getSuperType());
		
		assertEquals("ArithmeticOperator", StandardTokenType.ARITHMETIC_OPERATOR.getName());
		assertEquals(StandardTokenType.OPERATOR, StandardTokenType.ARITHMETIC_OPERATOR.getSuperType());
		
		assertEquals("AssignmentOperator", StandardTokenType.ASSIGNMENT_OPERATOR.getName());
		assertEquals(StandardTokenType.OPERATOR, StandardTokenType.ASSIGNMENT_OPERATOR.getSuperType());
		
		assertEquals("AccessOperator", StandardTokenType.ACCESS_OPERATOR.getName());
		assertEquals(StandardTokenType.OPERATOR, StandardTokenType.ACCESS_OPERATOR.getSuperType());
		
		assertEquals("ComparisonOperator", StandardTokenType.COMPARISON_OPERATOR.getName());
		assertEquals(StandardTokenType.OPERATOR, StandardTokenType.COMPARISON_OPERATOR.getSuperType());
		
		assertEquals("LogicalOperator", StandardTokenType.LOGICAL_OPERATOR.getName());
		assertEquals(StandardTokenType.OPERATOR, StandardTokenType.LOGICAL_OPERATOR.getSuperType());
		
		assertEquals("BitwiseOperator", StandardTokenType.BITWISE_OPERATOR.getName());
		assertEquals(StandardTokenType.OPERATOR, StandardTokenType.BITWISE_OPERATOR.getSuperType());
		
		assertEquals("UnaryOperator", StandardTokenType.UNARY_OPERATOR.getName());
		assertEquals(StandardTokenType.OPERATOR, StandardTokenType.UNARY_OPERATOR.getSuperType());
		
		assertEquals("TernaryOperator", StandardTokenType.TERNARY_OPERATOR.getName());
		assertEquals(StandardTokenType.OPERATOR, StandardTokenType.TERNARY_OPERATOR.getSuperType());
	}
	
	@Test
	void delimiterTokens() {
		assertEquals("Delimiter", StandardTokenType.DELIMITER.getName());
		assertNull(StandardTokenType.DELIMITER.getSuperType());
		
		assertEquals("Separator", StandardTokenType.SEPARATOR.getName());
		assertEquals(StandardTokenType.DELIMITER, StandardTokenType.SEPARATOR.getSuperType());
		
		assertEquals("Parenthesis", StandardTokenType.PARENTHESIS.getName());
		assertEquals(StandardTokenType.DELIMITER, StandardTokenType.PARENTHESIS.getSuperType());
		
		assertEquals("Brace", StandardTokenType.BRACE.getName());
		assertEquals(StandardTokenType.DELIMITER, StandardTokenType.BRACE.getSuperType());
		
		assertEquals("Bracket", StandardTokenType.BRACKET.getName());
		assertEquals(StandardTokenType.DELIMITER, StandardTokenType.BRACKET.getSuperType());
		
		assertEquals("AngleBracket", StandardTokenType.ANGLE_BRACKET.getName());
		assertEquals(StandardTokenType.DELIMITER, StandardTokenType.ANGLE_BRACKET.getSuperType());
	}
	
	@Test
	void whitespaceTokens() {
		assertEquals("Whitespace", StandardTokenType.WHITESPACE.getName());
		assertNull(StandardTokenType.WHITESPACE.getSuperType());
		
		assertEquals("Space", StandardTokenType.SPACE.getName());
		assertEquals(StandardTokenType.WHITESPACE, StandardTokenType.SPACE.getSuperType());
		
		assertEquals("Tab", StandardTokenType.TAB.getName());
		assertEquals(StandardTokenType.WHITESPACE, StandardTokenType.TAB.getSuperType());
		
		assertEquals("Newline", StandardTokenType.NEWLINE.getName());
		assertEquals(StandardTokenType.WHITESPACE, StandardTokenType.NEWLINE.getSuperType());
	}
	
	@Test
	void commentTokens() {
		assertEquals("Comment", StandardTokenType.COMMENT.getName());
		assertNull(StandardTokenType.COMMENT.getSuperType());
		
		assertEquals("SingleLineComment", StandardTokenType.SINGLE_LINE_COMMENT.getName());
		assertEquals(StandardTokenType.COMMENT, StandardTokenType.SINGLE_LINE_COMMENT.getSuperType());
		
		assertEquals("MultiLineComment", StandardTokenType.MULTI_LINE_COMMENT.getName());
		assertEquals(StandardTokenType.COMMENT, StandardTokenType.MULTI_LINE_COMMENT.getSuperType());
		
		assertEquals("DocumentationComment", StandardTokenType.DOCUMENTATION_COMMENT.getName());
		assertEquals(StandardTokenType.COMMENT, StandardTokenType.DOCUMENTATION_COMMENT.getSuperType());
	}
	
	@Test
	void specialTokens() {
		assertEquals("Special", StandardTokenType.SPECIAL.getName());
		assertNull(StandardTokenType.SPECIAL.getSuperType());
		
		assertEquals("Preprocessor", StandardTokenType.PREPROCESSOR.getName());
		assertEquals(StandardTokenType.SPECIAL, StandardTokenType.PREPROCESSOR.getSuperType());
		
		assertEquals("Error", StandardTokenType.ERROR.getName());
		assertEquals(StandardTokenType.SPECIAL, StandardTokenType.ERROR.getSuperType());
		
		assertEquals("EndOfFile", StandardTokenType.EOF.getName());
		assertEquals(StandardTokenType.SPECIAL, StandardTokenType.EOF.getSuperType());
		
		assertEquals("Unknown", StandardTokenType.UNKNOWN.getName());
		assertEquals(StandardTokenType.SPECIAL, StandardTokenType.UNKNOWN.getSuperType());
	}
	
	@Test
	void enumValues() {
		StandardTokenType[] values = StandardTokenType.values();
		
		assertTrue(values.length > 0);
		assertNotNull(StandardTokenType.valueOf("SYNTAX"));
		assertNotNull(StandardTokenType.valueOf("INTEGER"));
		assertNotNull(StandardTokenType.valueOf("UNKNOWN"));
	}
	
	@Test
	void enumValueOf() {
		assertEquals(StandardTokenType.SYNTAX, StandardTokenType.valueOf("SYNTAX"));
		assertEquals(StandardTokenType.STRING, StandardTokenType.valueOf("STRING"));
		assertEquals(StandardTokenType.EOF, StandardTokenType.valueOf("EOF"));
	}
	
	@Test
	void enumValueOfInvalid() {
		assertThrows(IllegalArgumentException.class, () -> StandardTokenType.valueOf("INVALID"));
	}
}
