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

package net.luis.utils.io.database.rendering;

import net.luis.utils.io.database.SqlTestFixtures;
import net.luis.utils.io.database.type.SqlType;
import net.luis.utils.util.Pair;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlRenderer}.<br>
 *
 * @author Luis-St
 */
class SqlRendererTest {
	
	@Test
	void constructWithStatementsAndParameters() {
		Pair<SqlType<?>, Object> pair = Pair.of(SqlTestFixtures.INTEGER_TYPE, 1);
		SqlRenderer renderer = new SqlRenderer(List.of("SELECT"), List.of(pair));
		assertEquals(List.of("SELECT"), renderer.getStatements());
		assertEquals(1, renderer.getParameters().size());
	}
	
	@Test
	void emptyCreatesBlankRenderer() {
		SqlRenderer renderer = SqlRenderer.empty();
		assertTrue(renderer.getStatements().isEmpty());
		assertTrue(renderer.getParameters().isEmpty());
	}
	
	@Test
	void constructWithNullStatements() {
		assertThrows(NullPointerException.class, () -> new SqlRenderer(null, List.of()));
	}
	
	@Test
	void constructWithNullParameters() {
		assertThrows(NullPointerException.class, () -> new SqlRenderer(List.of(), null));
	}
	
	@Test
	void literalWithNull() {
		assertThrows(NullPointerException.class, () -> SqlRenderer.empty().literal(null));
	}
	
	@Test
	void quotedLiteralWithNullQuote() {
		assertThrows(NullPointerException.class, () -> SqlRenderer.empty().quotedLiteral(null, "\"\"", "x"));
	}
	
	@Test
	void quotedLiteralWithNullEscapedQuote() {
		assertThrows(NullPointerException.class, () -> SqlRenderer.empty().quotedLiteral("\"", null, "x"));
	}
	
	@Test
	void quotedLiteralWithNullLiteral() {
		assertThrows(NullPointerException.class, () -> SqlRenderer.empty().quotedLiteral("\"", "\"\"", null));
	}
	
	@Test
	void keywordWithNull() {
		assertThrows(NullPointerException.class, () -> SqlRenderer.empty().keyword(null));
	}
	
	@Test
	void keywordWithBlank() {
		assertThrows(IllegalArgumentException.class, () -> SqlRenderer.empty().keyword("   "));
	}
	
	@Test
	void keywordWithEmpty() {
		assertThrows(IllegalArgumentException.class, () -> SqlRenderer.empty().keyword(""));
	}
	
	@Test
	void keywordWithLowercaseCharacter() {
		assertThrows(IllegalArgumentException.class, () -> SqlRenderer.empty().keyword("Select"));
	}
	
	@Test
	void keywordWithLowercaseFirstCharacter() {
		assertThrows(IllegalArgumentException.class, () -> SqlRenderer.empty().keyword("sELECT"));
	}
	
	@Test
	void keywordWithInvalidSymbol() {
		assertThrows(IllegalArgumentException.class, () -> SqlRenderer.empty().keyword("SELECT-X"));
	}
	
	@Test
	void parameterWithNullType() {
		assertThrows(NullPointerException.class, () -> SqlRenderer.empty().parameter(null, "v"));
	}
	
	@Test
	void renderedWithNull() {
		assertThrows(NullPointerException.class, () -> SqlRenderer.empty().rendered(null));
	}
	
	@Test
	void getStatementsIsUnmodifiable() {
		SqlRenderer renderer = SqlRenderer.empty().select();
		assertThrows(UnsupportedOperationException.class, () -> renderer.getStatements().add("X"));
	}
	
	@Test
	void getParametersIsUnmodifiable() {
		SqlRenderer renderer = SqlRenderer.empty().parameter(SqlTestFixtures.INTEGER_TYPE, 1);
		assertThrows(UnsupportedOperationException.class, () -> renderer.getParameters().add(Pair.of(SqlTestFixtures.INTEGER_TYPE, 2)));
	}
	
	@Test
	void getStatementsIsSnapshotNotLive() {
		SqlRenderer renderer = SqlRenderer.empty().select();
		List<String> snapshot = renderer.getStatements();
		renderer.from();
		assertEquals(List.of("SELECT"), snapshot);
	}
	
	@Test
	void literalAppendsVerbatim() {
		SqlRenderer renderer = SqlRenderer.empty().literal("users");
		assertEquals(List.of("users"), renderer.getStatements());
	}
	
	@Test
	void keywordAppendsValidUppercase() {
		SqlRenderer renderer = SqlRenderer.empty().keyword("PRIMARY_KEY");
		assertEquals(List.of("PRIMARY_KEY"), renderer.getStatements());
	}
	
	@Test
	void quotedLiteralWithoutQuoteCharacter() {
		SqlRenderer renderer = SqlRenderer.empty().quotedLiteral("\"", "\"\"", "users");
		assertEquals(List.of("\"users\""), renderer.getStatements());
	}
	
	@Test
	void quotedLiteralEscapesEmbeddedQuote() {
		SqlRenderer renderer = SqlRenderer.empty().quotedLiteral("\"", "\"\"", "a\"b");
		assertEquals(List.of("\"a\"\"b\""), renderer.getStatements());
	}
	
	@Test
	void parameterAppendsPlaceholderAndValue() {
		SqlRenderer renderer = SqlRenderer.empty().parameter(SqlTestFixtures.INTEGER_TYPE, 1);
		assertEquals(List.of("?"), renderer.getStatements());
		assertEquals(1, renderer.getParameters().size());
		assertEquals(Pair.of(SqlTestFixtures.INTEGER_TYPE, 1), renderer.getParameters().get(0));
	}
	
	@Test
	void parameterWithNullValueAllowed() {
		SqlRenderer renderer = assertDoesNotThrow(() -> SqlRenderer.empty().parameter(SqlTestFixtures.INTEGER_TYPE, null));
		assertEquals(List.of("?"), renderer.getStatements());
		assertNull(renderer.getParameters().get(0).getSecond());
	}
	
	@Test
	void renderedAppendsStatementsAndParameters() {
		Pair<SqlType<?>, Object> pair = Pair.of(SqlTestFixtures.INTEGER_TYPE, 1);
		SqlRendered rendered = new SqlRendered(List.of("A", "?"), List.of(pair));
		SqlRenderer renderer = SqlRenderer.empty().rendered(rendered);
		assertEquals(List.of("A", "?"), renderer.getStatements());
		assertEquals(1, renderer.getParameters().size());
	}
	
	@Test
	void bracketAndPunctuationTokens() {
		SqlRenderer renderer = SqlRenderer.empty().openingBracket().closingBracket().comma();
		assertEquals(List.of("(", ")", ","), renderer.getStatements());
	}
	
	@Test
	void simpleKeywordMethodsAppendExpectedToken() {
		SqlRenderer renderer = SqlRenderer.empty().select().from().where().insert().values().join().limit().unique().cascade();
		assertEquals(List.of("SELECT", "FROM", "WHERE", "INSERT", "VALUES", "JOIN", "LIMIT", "UNIQUE", "CASCADE"), renderer.getStatements());
	}
	
	@Test
	void reservedWordMethodsEmitBareKeyword() {
		SqlRenderer renderer = SqlRenderer.empty().true_().false_().null_().case_().else_().if_().for_().wait_().default_();
		assertEquals(List.of("TRUE", "FALSE", "NULL", "CASE", "ELSE", "IF", "FOR", "WAIT", "DEFAULT"), renderer.getStatements());
	}
	
	@Test
	void multiWordTokensAppendedAsSingleToken() {
		SqlRenderer renderer = SqlRenderer.empty().noAction().currentRow();
		assertEquals(List.of("NO ACTION", "CURRENT ROW"), renderer.getStatements());
	}
	
	@Test
	void toSqlWrapsCurrentState() {
		SqlRenderer renderer = SqlRenderer.empty().select().literal("1");
		assertEquals(List.of("SELECT", "1"), renderer.toSql().statements());
		assertEquals("SELECT 1", renderer.toSql().sql());
	}
	
	@Test
	void groupByEmitsGroupAndBy() {
		SqlRenderer renderer = SqlRenderer.empty().groupBy();
		assertEquals(List.of("GROUP", "BY"), renderer.getStatements());
	}
	
	@Test
	void orderByEmitsOrderAndBy() {
		SqlRenderer renderer = SqlRenderer.empty().orderBy();
		assertEquals(List.of("ORDER", "BY"), renderer.getStatements());
	}
	
	@Test
	void setNullEmitsSetAndNull() {
		SqlRenderer renderer = SqlRenderer.empty().setNull();
		assertEquals(List.of("SET", "NULL"), renderer.getStatements());
	}
	
	@Test
	void setDefaultEmitsSetAndDefault() {
		SqlRenderer renderer = SqlRenderer.empty().setDefault();
		assertEquals(List.of("SET", "DEFAULT"), renderer.getStatements());
	}
	
	@Test
	void fluentChainingReturnsSameInstance() {
		SqlRenderer renderer = SqlRenderer.empty();
		assertSame(renderer, renderer.select());
		assertSame(renderer, renderer.from());
		assertSame(renderer, renderer.literal("x"));
		assertSame(renderer, renderer.keyword("AS"));
	}
	
	@Test
	void buildCompleteSelectStatement() {
		SqlRenderer renderer = SqlRenderer.empty().select().literal("id").from().literal("users").where().literal("id").literal("=").parameter(SqlTestFixtures.INTEGER_TYPE, 1);
		assertEquals(List.of("SELECT", "id", "FROM", "users", "WHERE", "id", "=", "?"), renderer.getStatements());
		assertEquals(1, renderer.getParameters().size());
		assertEquals("SELECT id FROM users WHERE id = ?", renderer.toSql().sql());
	}
	
	@Test
	void buildInsertWithParametersCollectsAllParameters() {
		SqlRenderer renderer = SqlRenderer.empty().insert().into().literal("users").values().openingBracket()
			.parameter(SqlTestFixtures.INTEGER_TYPE, 1).comma().parameter(SqlTestFixtures.STRING_TYPE, "a").closingBracket();
		assertEquals(2, renderer.getParameters().size());
		assertEquals(2, renderer.getStatements().stream().filter("?"::equals).count());
	}
	
	@Test
	void rendererReuseAfterToSqlContinuesAppending() {
		SqlRenderer renderer = SqlRenderer.empty().select();
		SqlRendered first = renderer.toSql();
		renderer.from();
		SqlRendered second = renderer.toSql();
		assertEquals(List.of("SELECT"), first.statements());
		assertEquals(List.of("SELECT", "FROM"), second.statements());
	}
}
