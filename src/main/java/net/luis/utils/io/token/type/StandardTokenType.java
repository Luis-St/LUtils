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

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

/**
 * Enum that represents standard token types for programming languages.<br>
 *
 * @author Luis-St
 */
public enum StandardTokenType implements TokenType {
	
	//@formatter:off
	/**
	 * Syntax elements of a programming language.<br>
	 */
	SYNTAX("Syntax"),
		/**
		 * Keywords are reserved words that have a special meaning in a programming language.<br>
		 * They are used to define the structure and control flow of the code.<br>
		 * Examples include 'if', 'else', 'while', 'for', 'return', etc.<br>
		 */
		KEYWORD("Keyword", SYNTAX),
		/**
		 * Modifiers are keywords that provide additional information about declarations.<br>
		 * They can specify access levels, behavior, or other attributes of classes, methods, or variables.<br>
		 * Examples include 'public', 'private', 'static', 'final', etc.<br>
		 */
		MODIFIER("Modifier", SYNTAX),
	
	/**
	 * Names used to identify variables, functions, classes, etc.<br>
	 * They must follow specific naming conventions and rules defined by the programming language.<br>
	 */
	IDENTIFIER("Identifier"),
		/**
		 * Type identifiers are used to define data types in programming languages.<br>
		 * They specify the kind of data that a variable can hold, such as integers, floats, strings, etc.<br>
		 * Examples include 'int', 'float', 'String', 'boolean', etc.<br>
		 */
		TYPE_IDENTIFIER("TypeIdentifier", IDENTIFIER),
		/**
		 * Variable identifiers are names given to variables in programming languages.<br>
		 * They are used to store and reference data throughout the code.<br>
		 * Examples include 'myVariable', 'count', 'totalSum', etc.<br>
		 */
		VARIABLE_IDENTIFIER("VariableIdentifier", IDENTIFIER),
		/**
		 * Function identifiers are names given to functions or methods in programming languages.<br>
		 * They are used to define and call reusable blocks of code that perform specific tasks.<br>
		 * Examples include 'calculateSum', 'printMessage', 'getUserInput', etc.<br>
		 */
		FUNCTION_IDENTIFIER("FunctionIdentifier", IDENTIFIER),
	
	/**
	 * Literal values represent fixed values in the source code.<br>
	 * They can be of various types, such as numbers, strings, booleans, etc.<br>
	 */
	LITERAL("Literal"),
		/**
		 * Numeric literals represent numerical values in the source code.<br>
		 * They can be integers or floating-point numbers.<br>
		 * Examples include '42', '3.14', '-7', etc.<br>
		 */
		NUMBER("NumericLiteral", LITERAL),
			/**
			 * Integer literals represent whole numbers without a fractional component.<br>
			 * They can be positive, negative, or zero.<br>
			 * Examples include '0', '123', '-456', etc.<br>
			 */
			INTEGER("Integer", NUMBER),
			/**
			 * Float literals represent numbers with a fractional component.<br>
			 * They can be expressed in decimal or scientific notation.<br>
			 * Examples include '3.14', '-0.001', '2.5e3', etc.<br>
			 */
			FLOAT("Float", NUMBER),
		/**
		 * Boolean literals represent truth values in the source code.<br>
		 * They can be either 'true' or 'false'.<br>
		 */
		BOOLEAN("Boolean", LITERAL),
		/**
		 * Null literal represents the absence of a value or a null reference in the source code.<br>
		 * It is often used to indicate that a variable does not point to any object or data.<br>
		 * Examples include 'null' in Java, 'None' in Python, etc.<br>
		 */
		NULL("Null", LITERAL),
		/**
		 * Character literals represent single characters in the source code.<br>
		 * They are typically enclosed in single quotes.<br>
		 * Examples include 'a', 'Z', '1', '@', etc.<br>
		 */
		CHARACTER("Character", LITERAL),
		/**
		 * String literals represent sequences of characters in the source code.<br>
		 * They are typically enclosed in double quotes.<br>
		 * Examples include "Hello, World!", "12345", "This is a string.", etc.<br>
		 */
		STRING("String", LITERAL),
	
	/**
	 * Operators are symbols or keywords that perform specific operations on one or more operands.<br>
	 * They are used to manipulate data, perform calculations, and control the flow of execution in code.<br>
	 * Examples include '+', '-', '*', '/', '=', '==', '&amp;&amp;', '||', etc.<br>
	 */
	OPERATOR("Operator"),
		/**
		 * Arithmetic operators are used to perform mathematical calculations on numeric values.<br>
		 * They include addition, subtraction, multiplication, division, and modulus operations.<br>
		 * Examples include '+', '-', '*', '/', '%', etc.<br>
		 */
		ARITHMETIC_OPERATOR("ArithmeticOperator", OPERATOR),
		/**
		 * Assignment operators are used to assign values to variables.<br>
		 * They include simple assignment as well as compound assignment operators that combine assignment with arithmetic operations.<br>
		 * Examples include '=', '+=', '-=', '*=', '/=', '%=', etc.<br>
		 */
		ASSIGNMENT_OPERATOR("AssignmentOperator", OPERATOR),
		/**
		 * Access operators are used to access properties or methods of objects or data structures.<br>
		 * They include operators for member access, array indexing, and pointer dereferencing.<br>
		 * Examples include '.', '->', '[]', etc.<br>
		 */
		ACCESS_OPERATOR("AccessOperator", OPERATOR),
		/**
		 * Comparison operators are used to compare two values and determine their relationship.<br>
		 * They include operators for equality, inequality, and relational comparisons.<br>
		 * Examples include '==', '!=', '&lt;', '&gt;', '&lt;=', '&gt;=', etc.<br>
		 */
		COMPARISON_OPERATOR("ComparisonOperator", OPERATOR),
		/**
		 * Logical operators are used to perform logical operations on boolean values.<br>
		 * They include operators for conjunction, disjunction, and negation.<br>
		 * Examples include '&amp;&amp;', '||', '!', etc.<br>
		 */
		LOGICAL_OPERATOR("LogicalOperator", OPERATOR),
		/**
		 * Bitwise operators are used to perform bit-level operations on integer values.<br>
		 * They include operators for AND, OR, XOR, NOT, and bit shifts.<br>
		 * Examples include '&amp;', '|', '^', '~', '&lt;&lt;', '&gt;&gt;', etc.<br>
		 */
		BITWISE_OPERATOR("BitwiseOperator", OPERATOR),
		/**
		 * Unary operators are used to perform operations on a single operand.<br>
		 * They include operators for negation, increment, decrement, and type casting.<br>
		 * Examples include '-', '++', '--', '(type)', etc.<br>
		 */
		UNARY_OPERATOR("UnaryOperator", OPERATOR),
		/**
		 * Ternary operator is a conditional operator that takes three operands.<br>
		 * It is used to evaluate a condition and return one of two values based on the result of the condition.<br>
		 * The syntax is typically 'condition ? valueIfTrue : valueIfFalse'.<br>
		 */
		TERNARY_OPERATOR("TernaryOperator", OPERATOR),
	
	/**
	 * Delimiters are symbols used to separate or group elements in the source code.<br>
	 * They help define the structure and organization of the code.<br>
	 */
	DELIMITER("Delimiter"),
		/**
		 * Separators are used to separate different elements in the source code.<br>
		 * They include commas, semicolons, and other punctuation marks that help organize code statements and expressions.<br>
		 * Examples include ',', ';', ':', etc.<br>
		 */
		SEPARATOR("Separator", DELIMITER),
		/**
		 * Parentheses are used to group expressions and define the order of operations in the source code.<br>
		 * They are also used in function calls and definitions to enclose parameters.<br>
		 * This includes the opening and closing parentheses: '(' and ')'.<br>
		 */
		PARENTHESIS("Parenthesis", DELIMITER),
		/**
		 * Braces are used to define blocks of code in programming languages.<br>
		 * They are typically used to group statements together, such as in function bodies, loops, and conditionals.<br>
		 * This includes the opening and closing braces: '{' and '}'.<br>
		 */
		BRACE("Brace", DELIMITER),
		/**
		 * Brackets are used to define arrays, lists, and other data structures in programming languages.<br>
		 * They are also used for indexing and accessing elements within these data structures.<br>
		 * This includes the opening and closing brackets: '[' and ']'.<br>
		 */
		BRACKET("Bracket", DELIMITER),
		/**
		 * Angle brackets are used in various programming languages for different purposes.<br>
		 * They are commonly used in generics, templates, and to denote certain types of tags or elements.<br>
		 * This includes the opening and closing angle brackets: '&lt;' and '&gt;'.<br>
		 */
		ANGLE_BRACKET("AngleBracket",  DELIMITER),
	
	/**
	 * Whitespace characters are used to separate tokens in the source code.<br>
	 * They include spaces, tabs, and newline characters.<br>
	 */
	WHITESPACE("Whitespace"),
		/**
		 * Whitespace characters are used to separate tokens in the source code.<br>
		 */
		SPACE("Space", WHITESPACE),
		/**
		 * Tab characters are used to create horizontal spacing in the source code.<br>
		 * They are often used for indentation to improve code readability.<br>
		 */
		TAB("Tab", WHITESPACE),
		/**
		 * Newline characters are used to indicate the end of a line of code.<br>
		 * They help organize code into separate lines for better readability.<br>
		 */
		NEWLINE("Newline", WHITESPACE),
	
	/**
	 * Comments are non-executable parts of the source code that provide explanations or annotations.<br>
	 * They are ignored by the compiler or interpreter and are meant for human readers.<br>
	 */
	COMMENT("Comment"),
		/**
		 * Single-line comments are used to add brief explanations or notes within a single line of code.<br>
		 * They typically start with specific characters (e.g., // in Java, C++, or # in Python) and continue until the end of the line.<br>
		 */
		SINGLE_LINE_COMMENT("SingleLineComment", COMMENT),
		/**
		 * Multi-line comments are used to add explanations or notes that span multiple lines of code.<br>
		 * They typically start and end with specific character sequences (e.g., /* and *\/ in Java and C/C++).<br>
		 */
		MULTI_LINE_COMMENT("MultiLineComment", COMMENT),
		/**
		 * Documentation comments are special types of comments used to generate documentation for code.<br>
		 * They often follow specific formats (e.g., /** ... *\/ in Java) and include tags for parameters, return values, and other relevant information.<br>
		 */
		DOCUMENTATION_COMMENT("DocumentationComment", COMMENT),
	
	/**
	 * Special token types that do not fit into other categories.<br>
	 */
	SPECIAL("Special"),
		/**
		 * Preprocessor directives are special instructions in the source code that are processed before the actual compilation or interpretation of the code.<br>
		 * They are commonly used in languages like C and C++ to include files, define macros, and conditionally compile code.<br>
		 * Examples include '#include', '#define', '#ifdef', etc.<br>
		 */
		PREPROCESSOR("Preprocessor", SPECIAL),
		/**
		 * Error tokens represent invalid or unrecognized sequences of characters in the source code.<br>
		 * They are used to indicate syntax errors or other issues that prevent the code from being correctly parsed or compiled.<br>
		 */
		ERROR("Error", SPECIAL),
		/**
		 * End-of-file token represents the end of the input source code.<br>
		 * It is used to indicate that there are no more tokens to be read or processed.<br>
		 */
		EOF("EndOfFile", SPECIAL),
		/**
		 * Unknown token type is used to represent tokens that do not match any known or defined token types.<br>
		 * It serves as a fallback for unrecognized sequences of characters in the source code.<br>
		 */
		UNKNOWN("Unknown", SPECIAL);
	//@formatter:on
	
	/**
	 * The name of the token type.<br>
	 */
	private final String name;
	/**
	 * The super type of the token type, or null if it has no super type.<br>
	 */
	private final TokenType superType;
	
	/**
	 * Creates a new token type with the given name and no super type.<br>
	 *
	 * @param name The name of the token type, must not be null
	 * @throws NullPointerException If the name is null
	 */
	StandardTokenType(@NonNull String name) {
		this(name, null);
	}
	
	/**
	 * Creates a new token type with the given name and super type.<br>
	 *
	 * @param name The name of the token type
	 * @param superType The super type of the token type, or null if it has no super type
	 * @throws NullPointerException If the name is null
	 */
	StandardTokenType(@NonNull String name, @Nullable TokenType superType) {
		this.name = Objects.requireNonNull(name, "Token type name must not be null");
		this.superType = superType;
	}
	
	@Override
	public @NonNull String getName() {
		return this.name;
	}
	
	@Override
	public @Nullable TokenType getSuperType() {
		return this.superType;
	}
}
