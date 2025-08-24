package net.luis.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.luis.utils.io.token.TokenReader;
import net.luis.utils.io.token.definition.TokenDefinition;
import net.luis.utils.io.token.rule.TokenRuleEngine;
import net.luis.utils.io.token.rule.actions.GroupingTokenAction;
import net.luis.utils.io.token.rule.actions.filters.ExtractTokenAction;
import net.luis.utils.io.token.rule.rules.TokenRule;
import net.luis.utils.io.token.rule.rules.TokenRules;
import net.luis.utils.io.token.rule.rules.combinators.RecursiveTokenRule;
import net.luis.utils.io.token.tokens.*;
import net.luis.utils.logging.*;
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

public class Main {
	
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
		public class Main {
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
		
		// ToDo:
		//  - Add EmptyTokenRule, always true but does not consume any tokens
		//  - Add missing factory methods for RecursiveTokenRule to TokenRules
		//  - Add support for recursion without opening and closing rule (AnnotationValue ::= PrimitiveValue | Identifier | AnnotationDeclaration | '{' AnnotationValue (',' AnnotationValue)* '}')
		//  - Add support for capturing and referencing groups in TokenRules (TokenRules.capture(String id, TokenRule tokenRule), TokenRules.reference(String id))
		
		TokenRule annotationValue = new RecursiveTokenRule(null, null, (Function<TokenRule, TokenRule>) null);
		
		TokenRule annotationParameter = TokenRules.sequence(
			identifier,
			EQUALS,
			annotationValue
		);
		
		TokenRule annotationDeclaration = TokenRules.any(
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
		);
		
		
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
		LOGGER = LogManager.getLogger(Main.class);
	}
}
