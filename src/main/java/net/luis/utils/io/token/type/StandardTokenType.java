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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 *
 * @author Luis-St
 *
 */
public enum StandardTokenType implements TokenType {
	
	//@formatter:off
	SYNTAX("Syntax"),
		KEYWORD("Keyword", SYNTAX),
		MODIFIER("Modifier", SYNTAX),
	
	IDENTIFIER("Identifier"),
		TYPE_IDENTIFIER("TypeIdentifier", IDENTIFIER),
		VARIABLE_IDENTIFIER("VariableIdentifier", IDENTIFIER),
		FUNCTION_IDENTIFIER("FunctionIdentifier", IDENTIFIER),
	
	LITERAL("Literal"),
		NUMBER("NumericLiteral", LITERAL),
			INTEGER("Integer", NUMBER),
			FLOAT("Float", NUMBER),
		BOOLEAN("Boolean", LITERAL),
		NULL("Null", LITERAL),
		CHARACTER("Character", LITERAL),
		STRING("String", LITERAL),
	
	OPERATOR("Operator"),
		ARITHMETIC_OPERATOR("ArithmeticOperator", OPERATOR),
		ASSIGNMENT_OPERATOR("AssignmentOperator", OPERATOR),
		ACCESS_OPERATOR("AccessOperator", OPERATOR),
		COMPARISON_OPERATOR("ComparisonOperator", OPERATOR),
		LOGICAL_OPERATOR("LogicalOperator", OPERATOR),
		BITWISE_OPERATOR("BitwiseOperator", OPERATOR),
		UNARY_OPERATOR("UnaryOperator", OPERATOR),
		TERNARY_OPERATOR("TernaryOperator", OPERATOR),
	
	DELIMITER("Delimiter"),
		SEPARATOR("Separator", DELIMITER),
		PARENTHESIS("Parenthesis", DELIMITER),
		BRACE("Brace", DELIMITER),
		BRACKET("Bracket", DELIMITER),
		ANGLE_BRACKET("AngleBracket",  DELIMITER),
	
	WHITESPACE("Whitespace"),
		SPACE("Space", WHITESPACE),
		TAB("Tab", WHITESPACE),
		NEWLINE("Newline", WHITESPACE),
	
	COMMENT("Comment"),
		SINGLE_LINE_COMMENT("SingleLineComment", COMMENT),
		MULTI_LINE_COMMENT("MultiLineComment", COMMENT),
		DOCUMENTATION_COMMENT("DocumentationComment", COMMENT),
	
	SPECIAL("Special"),
		PREPROCESSOR("Preprocessor", SPECIAL),
		ERROR("Error", SPECIAL),
		EOF("EndOfFile", SPECIAL),
		UNKNOWN("Unknown", SPECIAL);
	//@formatter:on
	
	private final String name;
	private final TokenType superType;
	
	StandardTokenType(@NotNull String name) {
		this(name, null);
	}
	
	StandardTokenType(@NotNull String name, @Nullable TokenType superType) {
		this.name = Objects.requireNonNull(name, "Token type name must not be null");
		this.superType = superType;
	}
	
	@Override
	public @NotNull String getName() {
		return this.name;
	}
	
	@Override
	public @Nullable TokenType getSuperType() {
		return this.superType;
	}
}
