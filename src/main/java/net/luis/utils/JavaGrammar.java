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

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.luis.utils.io.token.TokenReader;
import net.luis.utils.io.token.definition.TokenDefinition;
import net.luis.utils.io.token.rule.TokenRuleEngine;
import net.luis.utils.io.token.rule.actions.GroupingTokenAction;
import net.luis.utils.io.token.rule.actions.filters.ExtractTokenAction;
import net.luis.utils.io.token.rule.rules.*;
import net.luis.utils.io.token.rule.rules.combinators.RecursiveTokenRule;
import net.luis.utils.io.token.tokens.*;
import net.luis.utils.logging.*;
import net.luis.utils.util.Lazy;
import org.apache.logging.log4j.*;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static net.luis.utils.Tokens.*;

/**
 *
 * @author Luis-St
 *
 */

public class JavaGrammar {
	
	private static final Logger LOGGER;
	
	@Language("Java")
	private static final String TEST_CODE = """
		/*
		 * Simple license header for testing purposes
		 */
		
		package net.luis.test;
		
		/**
		 * This is a test class to demonstrate token reading
		 */
		public /*Incode comment*/ class Main {
			// This is an inline comment
		}
		""";
	
	private static @NotNull @Unmodifiable Token createToken(@NotNull String value) {
		return SimpleToken.createUnpositioned(word -> true, value);
	}
	
	public static void main(String[] args) {
		TokenReader reader = new TokenReader(getTokenDefinitions(), getAllowedCharacters(), getSeparators());
		
		List<Token> tokens = reader.readTokens(TEST_CODE.replace("\r", ""));
		LOGGER.info("Read {} tokens", tokens.size());
		
		
		TokenRule identifier = TokenRules.pattern("[a-zA-Z_][a-zA-Z0-9_]*");
		TokenRule qualifiedName = TokenRules.sequence(
			identifier,
			TokenRules.sequence(DOT, identifier).repeatInfinitely()
		);
		
		
		TokenRule binaryIntegerRule = TokenRules.pattern("0[bB][01]+[lL]?");
		TokenRule octalIntegerLiteral = TokenRules.pattern("0[oO][0-7]+[lL]?");
		TokenRule hexadecimalIntegerLiteral = TokenRules.pattern("0[xX][0-9a-fA-F]+[lL]?");
		TokenRule decimalIntegerLiteral = TokenRules.pattern("0|[1-9][0-9]*[lL]?");
		TokenRule integerLiteral = TokenRules.any(
			binaryIntegerRule,
			octalIntegerLiteral,
			hexadecimalIntegerLiteral,
			decimalIntegerLiteral
		);
		
		TokenRule hexadecimalFloatingPointLiteral = TokenRules.pattern("0[xX][0-9a-fA-F]+\\.[0-9a-fA-F]+([pP][+-]?[0-9]+)?");
		TokenRule decimalFloatingPointLiteral = TokenRules.pattern("(\\.[0-9]+|[0-9]+(\\.[0-9]*)?)([eE][+-]?[0-9]+)?[fFdD]?");
		TokenRule floatingPointLiteral = TokenRules.any(
			hexadecimalFloatingPointLiteral,
			decimalFloatingPointLiteral
		);
		
		TokenRule numberLiteral = TokenRules.any(
			integerLiteral,
			floatingPointLiteral
		);
		
		TokenRule characterLiteral = TokenRules.sequence(
			SINGLE_QUOTE,
			SINGLE_QUOTE.not(),
			SINGLE_QUOTE
		);
		TokenRule stringLiteral = TokenRules.sequence(
			DOUBLE_QUOTE,
			DOUBLE_QUOTE.not().repeatInfinitely(),
			DOUBLE_QUOTE
		);
		
		TokenRule primitiveValue = TokenRules.any(
			Keywords.TRUE, Keywords.FALSE, Keywords.NULL,
			integerLiteral,
			numberLiteral,
			stringLiteral
		);
		
		
		TokenRule accessModifier = TokenRules.any(
			Keywords.PUBLIC, Keywords.PROTECTED, Keywords.PRIVATE
		);
		
		
		TokenRule packageDeclaration = TokenRules.sequence(
			Keywords.PACKAGE,
			qualifiedName,
			SEMICOLON
		);
		TokenRule importDeclaration = TokenRules.sequence(
			Keywords.IMPORT,
			Keywords.STATIC.optional(),
			qualifiedName,
			TokenRules.sequence(DOT, QUESTION_MARK).optional(),
			SEMICOLON
		);
		
		TokenRule classFileHeader = TokenRules.sequence(
			packageDeclaration.optional(),
			importDeclaration.repeatInfinitely()
		);
		
		LazyTokenRule annotationDeclaration = TokenRules.lazy();
		LazyTokenRule annotationParameter = TokenRules.lazy();
		
		TokenRule annotationValue = new RecursiveTokenRule(self -> {
			return TokenRules.any(
				primitiveValue,
				identifier,
				annotationDeclaration,
				TokenRules.sequence(
					LEFT_CURLY_BRACKET,
					TokenRules.separatedList(
						COMMA,
						self
					),
					RIGHT_CURLY_BRACKET
				)
			);
		});
		
		annotationDeclaration.set(TokenRules.any(
			TokenRules.sequence(
				AT,
				qualifiedName,
				TokenRules.sequence(
					LEFT_BRACKET,
					RIGHT_BRACKET
				).optional()
			),
			TokenRules.sequence(
				AT,
				qualifiedName,
				LEFT_BRACKET,
				RIGHT_BRACKET
			)
		));
		
		annotationParameter.set(TokenRules.sequence(
			identifier,
			EQUALS,
			annotationValue
		));
		
		
		TokenRule singleLineCommentRule = TokenRules.boundary(
			SLASH.repeatExactly(2),
			TokenRules.alwaysMatch(),
			TokenRules.any(
				TokenRules.lookahead(NEW_LINE),
				TokenRules.endDocument()
			)
		);
		TokenRule multiLineCommentRule = TokenRules.boundary(
			TokenRules.sequence(SLASH.repeatBetween(1, 2), ASTERISK),
			TokenRules.alwaysMatch(),
			TokenRules.sequence(ASTERISK, SLASH)
		);
		TokenRule commentRule = TokenRules.any(
			singleLineCommentRule.group(),
			multiLineCommentRule.group()
		);
		
		TokenRuleEngine ruleEngine = new TokenRuleEngine();
		
		ruleEngine.addRule(singleLineCommentRule, new GroupingTokenAction(Custom.SINGLE_LINE_COMMENT));
		ruleEngine.addRule(multiLineCommentRule, new GroupingTokenAction(Custom.MULTI_LINE_COMMENT));
		
		tokens = ruleEngine.process(tokens);
		LOGGER.info("After grouping comments: {} tokens", tokens.size());
		
		List<Token> comments = Lists.newArrayList();
		ruleEngine.addRule(commentRule, new ExtractTokenAction(TokenGroup.class::isInstance, comments::add));
		
		tokens = ruleEngine.process(tokens);
		LOGGER.info("After extracting comments: {} tokens", tokens.size());
		LOGGER.info("Extracted {} comments from tokens", comments.size());
		LOGGER.info("Extracted comments:\n{}", comments.stream().map(Token::value).collect(Collectors.joining("'\n'", "'", "'")));
		
		
		TokenRule packageNamePartRule = TokenRules.sequence(
			identifier,
			DOT
		);
		
		TokenRule packageNameRule = TokenRules.sequence(
			packageNamePartRule.repeatAtLeast(1),
			TokenRules.pattern("[a-zA-Z_][a-zA-Z0-9_]*")
		);
		
		TokenRule fullQualifiedClassNameRule = TokenRules.sequence(
			packageNameRule,
			DOT,
			identifier
		);
		
		TokenRule packageDeclarationRule = TokenRules.sequence(
			Keywords.PACKAGE,
			packageNameRule,
			SEMICOLON
		);
		
		TokenRule importRule = TokenRules.sequence(
			Keywords.IMPORT,
			fullQualifiedClassNameRule,
			SEMICOLON
		);
		
		TokenRule staticImportRule = TokenRules.sequence(
			Keywords.IMPORT,
			Keywords.STATIC,
			fullQualifiedClassNameRule,
			TokenRules.sequence(
				DOT,
				TokenRules.any(
					ASTERISK,
					identifier
				)
			).optional(),
			SEMICOLON
		);
		
		TokenRule importDeclarationRule = TokenRules.any(
			importRule,
			staticImportRule
		);
		
		
		TokenRule classModifierRule = TokenRules.any(
			Keywords.STATIC, Keywords.FINAL, Keywords.ABSTRACT
		);
		
		
		LOGGER.info("Modified example code:\n'{}'", tokens.stream().map(Token::value).collect(Collectors.joining()));
	}
	
	private static @NotNull Set<TokenDefinition> getTokenDefinitions() {
		Set<TokenDefinition> tokens = Sets.newHashSet();
		
		List<Field> fields = Lists.newArrayList(Tokens.class.getFields());
		for (Class<?> clazz : Tokens.class.getClasses()) {
			fields.addAll(Lists.newArrayList(clazz.getFields()));
		}
		
		for (Field field : fields) {
			if (TokenDefinition.class.isAssignableFrom(field.getType())) {
				try {
					tokens.add((TokenDefinition) field.get(null));
				} catch (IllegalAccessException e) {
					LOGGER.error("Failed to access token definition: {}", field.getName(), e);
				}
			}
		}
		return tokens;
	}
	
	private static @NotNull Set<Character> getAllowedCharacters() {
		Set<Character> allowedCharacters = Sets.newHashSet();
		
		// Add all alphanumeric characters
		for (char c = 'a'; c <= 'z'; c++) {
			allowedCharacters.add(c);
		}
		for (char c = 'A'; c <= 'Z'; c++) {
			allowedCharacters.add(c);
		}
		
		// Add digits
		for (char c = '0'; c <= '9'; c++) {
			allowedCharacters.add(c);
		}
		
		allowedCharacters.addAll(getSeparators());
		return allowedCharacters;
	}
	
	private static @NotNull Set<Character> getSeparators() {
		Set<Character> separators = Sets.newHashSet();
		
		for (char c : new char[] {
			'.', ',', ':', ';', '=', '+', '-', '*', '/', '%', '&', '|', '!', '?',
			'~', '`', '"', '\'', '(', ')', '[', ']', '{', '}', '<', '>', '@',
			'\\', '$', '^', '_', '#', ' ', '\t', '\n', '\r'
		}) {
			separators.add(c);
		}
		
		return separators;
	}
	
	static {
		System.setProperty("reflection.exceptions.throw", "true");
		LoggingUtils.initialize(LoggerConfiguration.DEFAULT.disableLogging(LoggingType.FILE).addDefaultLogger(LoggingType.CONSOLE, Level.DEBUG));
		LOGGER = LogManager.getLogger(JavaGrammar.class);
	}
}
