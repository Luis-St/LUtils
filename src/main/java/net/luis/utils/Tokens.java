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

package net.luis.utils;

import net.luis.utils.io.token.definition.TokenDefinition;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("InnerClassOfInterface")
public interface Tokens {
	
	TokenDefinition DOT = TokenDefinition.of('.');
	TokenDefinition COMMA = TokenDefinition.of(',');
	TokenDefinition COLON = TokenDefinition.of(':');
	TokenDefinition SEMICOLON = TokenDefinition.of(';');
	TokenDefinition EQUALS = TokenDefinition.of('=');
	TokenDefinition PLUS = TokenDefinition.of('+');
	TokenDefinition MINUS = TokenDefinition.of('-');
	TokenDefinition ASTERISK = TokenDefinition.of('*');
	TokenDefinition SLASH = TokenDefinition.of('/');
	TokenDefinition PERCENT = TokenDefinition.of('%');
	TokenDefinition AMPERSAND = TokenDefinition.of('&');
	TokenDefinition PIPE = TokenDefinition.of('|');
	TokenDefinition EXCLAMATION_MARK = TokenDefinition.of('!');
	TokenDefinition QUESTION_MARK = TokenDefinition.of('?');
	TokenDefinition TILDE = TokenDefinition.of('~');
	TokenDefinition BACKTICK = TokenDefinition.of('`');
	TokenDefinition DOUBLE_QUOTE = TokenDefinition.of('"');
	TokenDefinition SINGLE_QUOTE = TokenDefinition.of('\'');
	TokenDefinition LEFT_BRACKET = TokenDefinition.of('(');
	TokenDefinition RIGHT_BRACKET = TokenDefinition.of(')');
	TokenDefinition LEFT_SQUARE_BRACKET = TokenDefinition.of('[');
	TokenDefinition RIGHT_SQUARE_BRACKET = TokenDefinition.of(']');
	TokenDefinition LEFT_CURLY_BRACKET = TokenDefinition.of('{');
	TokenDefinition RIGHT_CURLY_BRACKET = TokenDefinition.of('}');
	TokenDefinition LEFT_ANGLE_BRACKET = TokenDefinition.of('<');
	TokenDefinition RIGHT_ANGLE_BRACKET = TokenDefinition.of('>');
	TokenDefinition AT = TokenDefinition.of('@');
	TokenDefinition HASH = TokenDefinition.of('#');
	TokenDefinition DOLLAR = TokenDefinition.of('$');
	TokenDefinition BACKSLASH = TokenDefinition.of('\\');
	TokenDefinition UNDERSCORE = TokenDefinition.of('_');
	TokenDefinition SPACE = TokenDefinition.of(' ');
	TokenDefinition NEW_LINE = TokenDefinition.of('\n');
	TokenDefinition TAB = TokenDefinition.of('\t');
	
	interface Keywords {
		
		TokenDefinition NULL = TokenDefinition.of("null", false);
		TokenDefinition TRUE = TokenDefinition.of("true", false);
		TokenDefinition FALSE = TokenDefinition.of("false", false);
		
		TokenDefinition PACKAGE = TokenDefinition.of("package", false);
		TokenDefinition IMPORT = TokenDefinition.of("import", false);
		
		TokenDefinition PUBLIC = TokenDefinition.of("public", false);
		TokenDefinition PROTECTED = TokenDefinition.of("protected", false);
		TokenDefinition PRIVATE = TokenDefinition.of("private", false);
		
		TokenDefinition STATIC = TokenDefinition.of("static", false);
		TokenDefinition FINAL = TokenDefinition.of("final", false);
		TokenDefinition ABSTRACT = TokenDefinition.of("abstract", false);
		
		TokenDefinition CLASS = TokenDefinition.of("class", false);
		TokenDefinition INTERFACE = TokenDefinition.of("interface", false);
		TokenDefinition ENUM = TokenDefinition.of("enum", false);
		TokenDefinition RECORD = TokenDefinition.of("record", false);
	}
	
	interface Custom {
		
		TokenDefinition SINGLE_LINE_COMMENT = new TokenDefinition() {
			@Override
			public boolean matches(@NotNull String word) {
				return word.startsWith("//");
			}
		};
		
		TokenDefinition MULTI_LINE_COMMENT = new TokenDefinition() {
			@Override
			public boolean matches(@NotNull String word) {
				return word.startsWith("/*") && word.endsWith("*/");
			}
		};
	}
}
