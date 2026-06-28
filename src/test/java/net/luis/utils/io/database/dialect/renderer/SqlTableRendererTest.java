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

package net.luis.utils.io.database.dialect.renderer;

import net.luis.utils.io.database.SqlReferentialAction;
import net.luis.utils.io.database.audit.SqlAuditColumn;
import net.luis.utils.io.database.audit.SqlAuditRole;
import net.luis.utils.io.database.condition.SqlCondition;
import net.luis.utils.io.database.condition.conditions.comparison.SqlEqualToCondition;
import net.luis.utils.io.database.dialect.SqlDialects;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.exception.client.dialect.SqlDialectUnsupportedRenderingException;
import net.luis.utils.io.database.expression.SqlValueExpression;
import net.luis.utils.io.database.rendering.SqlRendered;
import net.luis.utils.io.database.rendering.SqlRenderer;
import net.luis.utils.io.database.table.*;
import net.luis.utils.io.database.type.SqlTypes;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlTableRenderer}.<br>
 *
 * @author Luis-St
 */
class SqlTableRendererTest {
	
	private static final SqlTableRenderer RENDERER = new SqlTableRenderer(SqlDialects.DEFAULT);
	
	private static SqlCondition condition() {
		return new SqlEqualToCondition(new SqlValueExpression<>(1, SqlTypes.INTEGER), new SqlValueExpression<>(1, SqlTypes.INTEGER));
	}
	
	private static long countCommas(String sql) {
		return sql.chars().filter(c -> c == ',').count();
	}
	
	@Test
	void constructWithDialect() {
		assertNotNull(new SqlTableRenderer(SqlDialects.DEFAULT));
	}
	
	@Test
	void constructWithNullDialect() {
		assertThrows(NullPointerException.class, () -> new SqlTableRenderer(null));
	}
	
	@Test
	void renderCreateTableWithNullTable() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderCreateTable(null, false));
	}
	
	@Test
	void renderDropTableWithNullTable() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderDropTable(null, false));
	}
	
	@Test
	void renderTruncateTableWithNullTable() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderTruncateTable(null));
	}
	
	@Test
	void renderTableRebuildThrowsUnsupported() {
		SqlTable<Object> table = SqlTable.create(Object.class, "users");
		SqlDialectUnsupportedRenderingException exception = assertThrows(SqlDialectUnsupportedRenderingException.class, () -> RENDERER.renderTableRebuild(table, List.of(), List.of()));
		assertTrue(exception.getMessage().contains("Default"));
	}
	
	@Test
	void renderColumnForTableWithNullColumn() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderColumnForTable(null, false));
	}
	
	@Test
	void renderColumnForTableWithUnsupportedType() {
		SqlTable<Object> table = SqlTable.create(Object.class, "users");
		SqlColumn<Object, Integer[]> column = table.column("data", SqlTypes.INTEGER.array(), object -> new Integer[0]);
		SqlDialectUnsupportedRenderingException exception = assertThrows(SqlDialectUnsupportedRenderingException.class, () -> RENDERER.renderColumnForTable(column, false));
		assertTrue(exception.getMessage().contains("Column type"));
	}
	
	@Test
	void renderAuditColumnForTableWithNullColumn() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderAuditColumnForTable(null));
	}
	
	@Test
	void renderAuditColumnForTableWithUnsupportedType() {
		SqlAuditColumn column = new SqlAuditColumn("v", SqlTypes.INTEGER.array(), SqlAuditRole.VERSION, false);
		SqlDialectUnsupportedRenderingException exception = assertThrows(SqlDialectUnsupportedRenderingException.class, () -> RENDERER.renderAuditColumnForTable(column));
		assertTrue(exception.getMessage().contains("Audit column type"));
	}
	
	@Test
	void renderAutoIncrementWithNullRenderer() {
		SqlTable<Object> table = SqlTable.create(Object.class, "users");
		SqlColumn<Object, Integer> column = table.column("id", SqlTypes.INTEGER, object -> 0);
		assertThrows(NullPointerException.class, () -> RENDERER.renderAutoIncrement(null, column));
	}
	
	@Test
	void renderAutoIncrementWithNullColumn() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderAutoIncrement(SqlRenderer.empty(), null));
	}
	
	@Test
	void renderForeignKeyWithNullRenderer() {
		SqlTable<Object> other = SqlTable.create(Object.class, "other");
		SqlColumn<Object, Integer> oid = other.column("oid", SqlTypes.INTEGER, object -> 0);
		SqlForeignKey<Object> fk = new SqlForeignKey<>(other, List.of(oid), SqlReferentialAction.NO_ACTION, SqlReferentialAction.NO_ACTION);
		assertThrows(NullPointerException.class, () -> RENDERER.renderForeignKey(null, fk));
	}
	
	@Test
	void renderForeignKeyWithNullForeignKey() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderForeignKey(SqlRenderer.empty(), null));
	}
	
	@Test
	void renderTableConstraintsWithNullTable() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderTableConstraints(null));
	}
	
	@Test
	void renderCreateTableWithoutIfNotExists() throws SqlException {
		SqlTable<Object> table = SqlTable.create(Object.class, "users");
		table.column("id", SqlTypes.INTEGER, object -> 0);
		SqlRendered rendered = RENDERER.renderCreateTable(table, false);
		assertTrue(rendered.sql().contains("CREATE TABLE \"users\"("));
		assertFalse(rendered.sql().contains("IF NOT EXISTS"));
	}
	
	@Test
	void renderCreateTableWithIfNotExists() throws SqlException {
		SqlTable<Object> table = SqlTable.create(Object.class, "users");
		table.column("id", SqlTypes.INTEGER, object -> 0);
		SqlRendered rendered = RENDERER.renderCreateTable(table, true);
		assertTrue(rendered.sql().contains("IF NOT EXISTS"));
	}
	
	@Test
	void renderCreateTableWithSingleColumn() throws SqlException {
		SqlTable<Object> table = SqlTable.create(Object.class, "users");
		table.column("id", SqlTypes.INTEGER, object -> 0);
		SqlRendered rendered = RENDERER.renderCreateTable(table, false);
		assertEquals(0, countCommas(rendered.sql()));
	}
	
	@Test
	void renderCreateTableWithMultipleColumns() throws SqlException {
		SqlTable<Object> table = SqlTable.create(Object.class, "users");
		table.column("id", SqlTypes.INTEGER, object -> 0);
		table.column("name", SqlTypes.TEXT, object -> "");
		SqlRendered rendered = RENDERER.renderCreateTable(table, false);
		assertEquals(1, countCommas(rendered.sql()));
	}
	
	@Test
	void renderCreateTableWithoutCompositeKey() throws SqlException {
		SqlTable<Object> table = SqlTable.create(Object.class, "users");
		table.column("id", SqlTypes.INTEGER, object -> 0, builder -> builder.primaryKey());
		SqlRendered rendered = RENDERER.renderCreateTable(table, false);
		assertTrue(rendered.sql().contains("PRIMARY KEY"));
	}
	
	@Test
	void renderCreateTableWithCompositeKey() throws SqlException {
		SqlTable<Object> table = SqlTable.create(Object.class, "users");
		SqlColumn<Object, Integer> a = table.column("a", SqlTypes.INTEGER, object -> 0, builder -> builder.primaryKey());
		SqlColumn<Object, Integer> b = table.column("b", SqlTypes.INTEGER, object -> 0, builder -> builder.primaryKey());
		table.compositePrimaryKey(List.of(a, b));
		SqlRendered rendered = RENDERER.renderCreateTable(table, false);
		assertTrue(rendered.sql().contains("PRIMARY KEY("));
		assertTrue(rendered.sql().contains("\"a\""));
	}
	
	@Test
	void renderCreateTableWithoutAuditConfig() throws SqlException {
		SqlTable<Object> table = SqlTable.create(Object.class, "users");
		table.column("id", SqlTypes.INTEGER, object -> 0);
		SqlRendered rendered = RENDERER.renderCreateTable(table, false);
		assertFalse(rendered.sql().contains("\"version\""));
	}
	
	@Test
	void renderCreateTableWithAuditConfig() throws SqlException {
		SqlTable<Object> table = SqlTable.audited(Object.class, "users");
		table.column("id", SqlTypes.INTEGER, object -> 0);
		SqlRendered rendered = RENDERER.renderCreateTable(table, false);
		for (String name : table.auditConfig().orElseThrow().columnNames()) {
			assertTrue(rendered.sql().contains("\"" + name + "\""));
		}
	}
	
	@Test
	void renderCreateTableWithoutTableConstraints() throws SqlException {
		SqlTable<Object> table = SqlTable.create(Object.class, "users");
		table.column("id", SqlTypes.INTEGER, object -> 0);
		SqlRendered rendered = RENDERER.renderCreateTable(table, false);
		assertFalse(rendered.sql().contains("UNIQUE"));
		assertFalse(rendered.sql().contains("FOREIGN KEY"));
	}
	
	@Test
	void renderCreateTableWithTableConstraints() throws SqlException {
		SqlTable<Object> table = SqlTable.create(Object.class, "users");
		SqlColumn<Object, Integer> id = table.column("id", SqlTypes.INTEGER, object -> 0);
		table.uniqueConstraint(id);
		SqlRendered rendered = RENDERER.renderCreateTable(table, false);
		assertTrue(rendered.sql().contains("UNIQUE("));
	}
	
	@Test
	void renderDropTableWithoutIfExists() throws SqlException {
		SqlTable<Object> table = SqlTable.create(Object.class, "users");
		SqlRendered rendered = RENDERER.renderDropTable(table, false);
		assertTrue(rendered.sql().contains("DROP TABLE \"users\""));
		assertFalse(rendered.sql().contains("IF EXISTS"));
	}
	
	@Test
	void renderDropTableWithIfExists() throws SqlException {
		SqlTable<Object> table = SqlTable.create(Object.class, "users");
		SqlRendered rendered = RENDERER.renderDropTable(table, true);
		assertTrue(rendered.sql().contains("IF EXISTS"));
	}
	
	@Test
	void renderColumnForTableNotNullColumn() throws SqlException {
		SqlTable<Object> table = SqlTable.create(Object.class, "users");
		SqlColumn<Object, Integer> column = table.column("id", SqlTypes.INTEGER, object -> 0, builder -> builder.notNull());
		SqlRendered rendered = RENDERER.renderColumnForTable(column, false);
		assertTrue(rendered.sql().contains("NOT NULL"));
	}
	
	@Test
	void renderColumnForTableNullableColumn() throws SqlException {
		SqlTable<Object> table = SqlTable.create(Object.class, "users");
		SqlColumn<Object, Integer> column = table.column("id", SqlTypes.INTEGER, object -> 0);
		SqlRendered rendered = RENDERER.renderColumnForTable(column, false);
		assertFalse(rendered.sql().contains("NOT NULL"));
	}
	
	@Test
	void renderColumnForTableWithDefaultValue() throws SqlException {
		SqlTable<Object> table = SqlTable.create(Object.class, "users");
		SqlColumn<Object, Integer> column = table.column("id", SqlTypes.INTEGER, object -> 0, builder -> builder.defaultValue(0));
		SqlRendered rendered = RENDERER.renderColumnForTable(column, false);
		assertTrue(rendered.sql().contains("DEFAULT"));
		assertTrue(rendered.sql().contains("0"));
	}
	
	@Test
	void renderColumnForTableWithAutoIncrement() throws SqlException {
		SqlTable<Object> table = SqlTable.create(Object.class, "users");
		SqlColumn<Object, Integer> column = table.column("id", SqlTypes.INTEGER, object -> 0, builder -> builder.autoIncrement());
		SqlRendered rendered = RENDERER.renderColumnForTable(column, false);
		assertTrue(rendered.sql().contains("GENERATED"));
		assertTrue(rendered.sql().contains("IDENTITY"));
	}
	
	@Test
	void renderColumnForTablePrimaryKeyNotSkipped() throws SqlException {
		SqlTable<Object> table = SqlTable.create(Object.class, "users");
		SqlColumn<Object, Integer> column = table.column("id", SqlTypes.INTEGER, object -> 0, builder -> builder.primaryKey());
		SqlRendered rendered = RENDERER.renderColumnForTable(column, false);
		assertTrue(rendered.sql().contains("PRIMARY KEY"));
	}
	
	@Test
	void renderColumnForTablePrimaryKeySkipped() throws SqlException {
		SqlTable<Object> table = SqlTable.create(Object.class, "users");
		SqlColumn<Object, Integer> column = table.column("id", SqlTypes.INTEGER, object -> 0, builder -> builder.primaryKey());
		SqlRendered rendered = RENDERER.renderColumnForTable(column, true);
		assertFalse(rendered.sql().contains("PRIMARY KEY"));
	}
	
	@Test
	void renderColumnForTableNonPrimaryKeyColumn() throws SqlException {
		SqlTable<Object> table = SqlTable.create(Object.class, "users");
		SqlColumn<Object, Integer> column = table.column("id", SqlTypes.INTEGER, object -> 0);
		SqlRendered rendered = RENDERER.renderColumnForTable(column, false);
		assertFalse(rendered.sql().contains("PRIMARY KEY"));
	}
	
	@Test
	void renderColumnForTableUniqueColumn() throws SqlException {
		SqlTable<Object> table = SqlTable.create(Object.class, "users");
		SqlColumn<Object, Integer> column = table.column("id", SqlTypes.INTEGER, object -> 0, builder -> builder.unique());
		SqlRendered rendered = RENDERER.renderColumnForTable(column, false);
		assertTrue(rendered.sql().contains("UNIQUE"));
	}
	
	@Test
	void renderColumnForTableWithForeignKey() throws SqlException {
		SqlTable<Object> other = SqlTable.create(Object.class, "other");
		SqlColumn<Object, Integer> oid = other.column("oid", SqlTypes.INTEGER, object -> 0);
		SqlTable<Object> table = SqlTable.create(Object.class, "users");
		SqlColumn<Object, Integer> column = table.column("ref", SqlTypes.INTEGER, object -> 0, builder -> builder.foreignKey(other, oid));
		SqlRendered rendered = RENDERER.renderColumnForTable(column, false);
		assertTrue(rendered.sql().contains("REFERENCES"));
		assertTrue(rendered.sql().contains("\"other\""));
	}
	
	@Test
	void renderColumnForTableWithoutForeignKey() throws SqlException {
		SqlTable<Object> table = SqlTable.create(Object.class, "users");
		SqlColumn<Object, Integer> column = table.column("id", SqlTypes.INTEGER, object -> 0);
		SqlRendered rendered = RENDERER.renderColumnForTable(column, false);
		assertFalse(rendered.sql().contains("REFERENCES"));
	}
	
	@Test
	void renderColumnForTableWithChecks() throws SqlException {
		SqlTable<Object> table = SqlTable.create(Object.class, "users");
		SqlColumn<Object, Integer> column = table.column("id", SqlTypes.INTEGER, object -> 0, builder -> builder.addConstraint(condition()));
		SqlRendered rendered = RENDERER.renderColumnForTable(column, false);
		assertTrue(rendered.sql().contains("CHECK("));
	}
	
	@Test
	void renderColumnForTableWithoutChecks() throws SqlException {
		SqlTable<Object> table = SqlTable.create(Object.class, "users");
		SqlColumn<Object, Integer> column = table.column("id", SqlTypes.INTEGER, object -> 0);
		SqlRendered rendered = RENDERER.renderColumnForTable(column, false);
		assertFalse(rendered.sql().contains("CHECK"));
	}
	
	@Test
	void renderAuditColumnForTableNotNull() throws SqlException {
		SqlAuditColumn column = new SqlAuditColumn("v", SqlTypes.LONG, SqlAuditRole.VERSION, false);
		SqlRendered rendered = RENDERER.renderAuditColumnForTable(column);
		assertTrue(rendered.sql().contains("\"v\""));
		assertTrue(rendered.sql().contains(SqlDialects.DEFAULT.getTypeName(SqlTypes.LONG)));
		assertTrue(rendered.sql().contains("NOT NULL"));
	}
	
	@Test
	void renderAuditColumnForTableNullable() throws SqlException {
		SqlAuditColumn column = new SqlAuditColumn("v", SqlTypes.LONG, SqlAuditRole.VERSION, true);
		SqlRendered rendered = RENDERER.renderAuditColumnForTable(column);
		assertFalse(rendered.sql().contains("NOT NULL"));
	}
	
	@Test
	void renderForeignKeyWithSingleReferencedColumn() throws SqlException {
		SqlTable<Object> other = SqlTable.create(Object.class, "other");
		SqlColumn<Object, Integer> oid = other.column("oid", SqlTypes.INTEGER, object -> 0);
		SqlForeignKey<Object> fk = new SqlForeignKey<>(other, List.of(oid), SqlReferentialAction.NO_ACTION, SqlReferentialAction.NO_ACTION);
		SqlRenderer renderer = SqlRenderer.empty();
		RENDERER.renderForeignKey(renderer, fk);
		assertTrue(renderer.toSql().sql().contains("\"oid\""));
		assertEquals(0, countCommas(renderer.toSql().sql()));
	}
	
	@Test
	void renderForeignKeyWithMultipleReferencedColumns() throws SqlException {
		SqlTable<Object> other = SqlTable.create(Object.class, "other");
		List<SqlColumn<Object, ?>> referenced = List.of(other.column("oid", SqlTypes.INTEGER, object -> 0), other.column("oname", SqlTypes.TEXT, object -> ""));
		SqlForeignKey<Object> fk = new SqlForeignKey<>(other, referenced, SqlReferentialAction.NO_ACTION, SqlReferentialAction.NO_ACTION);
		SqlRenderer renderer = SqlRenderer.empty();
		RENDERER.renderForeignKey(renderer, fk);
		assertEquals(1, countCommas(renderer.toSql().sql()));
	}
	
	@Test
	void renderForeignKeyWithNoActionActions() throws SqlException {
		SqlTable<Object> other = SqlTable.create(Object.class, "other");
		SqlColumn<Object, Integer> oid = other.column("oid", SqlTypes.INTEGER, object -> 0);
		SqlForeignKey<Object> fk = new SqlForeignKey<>(other, List.of(oid), SqlReferentialAction.NO_ACTION, SqlReferentialAction.NO_ACTION);
		SqlRenderer renderer = SqlRenderer.empty();
		RENDERER.renderForeignKey(renderer, fk);
		assertFalse(renderer.toSql().sql().contains("ON UPDATE"));
		assertFalse(renderer.toSql().sql().contains("ON DELETE"));
	}
	
	@Test
	void renderForeignKeyWithUpdateAction() throws SqlException {
		SqlTable<Object> other = SqlTable.create(Object.class, "other");
		SqlColumn<Object, Integer> oid = other.column("oid", SqlTypes.INTEGER, object -> 0);
		SqlForeignKey<Object> fk = new SqlForeignKey<>(other, List.of(oid), SqlReferentialAction.CASCADE, SqlReferentialAction.NO_ACTION);
		SqlRenderer renderer = SqlRenderer.empty();
		RENDERER.renderForeignKey(renderer, fk);
		assertTrue(renderer.toSql().sql().contains("ON UPDATE"));
		assertTrue(renderer.toSql().sql().contains("CASCADE"));
		assertFalse(renderer.toSql().sql().contains("ON DELETE"));
	}
	
	@Test
	void renderForeignKeyWithDeleteAction() throws SqlException {
		SqlTable<Object> other = SqlTable.create(Object.class, "other");
		SqlColumn<Object, Integer> oid = other.column("oid", SqlTypes.INTEGER, object -> 0);
		SqlForeignKey<Object> fk = new SqlForeignKey<>(other, List.of(oid), SqlReferentialAction.NO_ACTION, SqlReferentialAction.CASCADE);
		SqlRenderer renderer = SqlRenderer.empty();
		RENDERER.renderForeignKey(renderer, fk);
		assertTrue(renderer.toSql().sql().contains("ON DELETE"));
		assertTrue(renderer.toSql().sql().contains("CASCADE"));
		assertFalse(renderer.toSql().sql().contains("ON UPDATE"));
	}
	
	@Test
	void renderTableConstraintsEmptyTable() throws SqlException {
		SqlTable<Object> table = SqlTable.create(Object.class, "users");
		table.column("id", SqlTypes.INTEGER, object -> 0);
		SqlRendered rendered = RENDERER.renderTableConstraints(table);
		assertTrue(rendered.sql().isEmpty());
	}
	
	@Test
	void renderTableConstraintsWithCompositePrimaryKey() throws SqlException {
		SqlTable<Object> table = SqlTable.create(Object.class, "users");
		SqlColumn<Object, Integer> a = table.column("a", SqlTypes.INTEGER, object -> 0, builder -> builder.primaryKey());
		SqlColumn<Object, Integer> b = table.column("b", SqlTypes.INTEGER, object -> 0, builder -> builder.primaryKey());
		table.compositePrimaryKey(List.of(a, b));
		SqlRendered rendered = RENDERER.renderTableConstraints(table);
		assertTrue(rendered.sql().contains("PRIMARY KEY("));
		assertFalse(rendered.sql().startsWith(","));
	}
	
	@Test
	void renderTableConstraintsWithForeignKey() throws SqlException {
		SqlTable<Object> other = SqlTable.create(Object.class, "other");
		SqlColumn<Object, Integer> oid = other.column("oid", SqlTypes.INTEGER, object -> 0);
		SqlTable<Object> table = SqlTable.create(Object.class, "users");
		SqlColumn<Object, Integer> ref = table.column("ref", SqlTypes.INTEGER, object -> 0);
		table.foreignKey(List.of(ref), other, oid);
		SqlRendered rendered = RENDERER.renderTableConstraints(table);
		assertTrue(rendered.sql().contains("FOREIGN KEY"));
		assertTrue(rendered.sql().contains("REFERENCES"));
		assertFalse(rendered.sql().startsWith(","));
	}
	
	@Test
	void renderTableConstraintsWithUniqueConstraint() throws SqlException {
		SqlTable<Object> table = SqlTable.create(Object.class, "users");
		SqlColumn<Object, Integer> id = table.column("id", SqlTypes.INTEGER, object -> 0);
		table.uniqueConstraint(id);
		SqlRendered rendered = RENDERER.renderTableConstraints(table);
		assertTrue(rendered.sql().contains("UNIQUE("));
	}
	
	@Test
	void renderTableConstraintsWithCheckConstraint() throws SqlException {
		SqlTable<Object> table = SqlTable.create(Object.class, "users");
		table.column("id", SqlTypes.INTEGER, object -> 0);
		table.checkConstraint(condition());
		SqlRendered rendered = RENDERER.renderTableConstraints(table);
		assertTrue(rendered.sql().contains("CHECK("));
	}
	
	@Test
	void renderTableConstraintsWithMultipleConstraintTypes() throws SqlException {
		SqlTable<Object> table = SqlTable.create(Object.class, "users");
		SqlColumn<Object, Integer> id = table.column("id", SqlTypes.INTEGER, object -> 0);
		table.uniqueConstraint(id);
		table.checkConstraint(condition());
		SqlRendered rendered = RENDERER.renderTableConstraints(table);
		assertTrue(rendered.sql().contains("UNIQUE("));
		assertTrue(rendered.sql().contains("CHECK("));
		assertTrue(countCommas(rendered.sql()) >= 1);
	}
	
	@Test
	void renderTruncateTableProducesTruncate() throws SqlException {
		SqlTable<Object> table = SqlTable.create(Object.class, "users");
		SqlRendered rendered = RENDERER.renderTruncateTable(table);
		assertTrue(rendered.sql().contains("TRUNCATE"));
		assertTrue(rendered.sql().contains("TABLE"));
		assertTrue(rendered.sql().contains("\"users\""));
	}
	
	@Test
	void renderAutoIncrementKeywordProducesIdentity() throws SqlException {
		SqlRendered rendered = RENDERER.renderAutoIncrementKeyword();
		assertTrue(rendered.sql().contains("GENERATED"));
		assertTrue(rendered.sql().contains("ALWAYS"));
		assertTrue(rendered.sql().contains("AS"));
		assertTrue(rendered.sql().contains("IDENTITY"));
	}
	
	@Test
	void renderAutoIncrementAppendsIdentityKeywords() throws SqlException {
		SqlTable<Object> table = SqlTable.create(Object.class, "users");
		SqlColumn<Object, Integer> column = table.column("id", SqlTypes.INTEGER, object -> 0);
		SqlRenderer renderer = SqlRenderer.empty();
		RENDERER.renderAutoIncrement(renderer, column);
		assertTrue(renderer.toSql().sql().contains("GENERATED ALWAYS AS IDENTITY"));
	}
	
	@Test
	void renderCreateTableWithColumnsAuditAndConstraints() throws SqlException {
		SqlTable<Object> table = SqlTable.audited(Object.class, "users");
		SqlColumn<Object, Integer> a = table.column("a", SqlTypes.INTEGER, object -> 0, builder -> builder.primaryKey());
		SqlColumn<Object, Integer> b = table.column("b", SqlTypes.INTEGER, object -> 0, builder -> builder.primaryKey());
		table.compositePrimaryKey(List.of(a, b));
		SqlRendered rendered = RENDERER.renderCreateTable(table, false);
		String sql = rendered.sql();
		assertTrue(sql.contains("CREATE TABLE"));
		assertTrue(sql.contains("\"a\""));
		assertTrue(sql.contains("\"b\""));
		assertTrue(sql.contains("PRIMARY KEY("));
		for (String name : table.auditConfig().orElseThrow().columnNames()) {
			assertTrue(sql.contains("\"" + name + "\""));
		}
		assertEquals(sql.chars().filter(c -> c == '(').count(), sql.chars().filter(c -> c == ')').count());
	}
	
	@Test
	void renderColumnForTableWithAllModifiersCombined() throws SqlException {
		SqlTable<Object> other = SqlTable.create(Object.class, "other");
		SqlColumn<Object, Integer> oid = other.column("oid", SqlTypes.INTEGER, object -> 0);
		SqlTable<Object> table = SqlTable.create(Object.class, "users");
		SqlColumn<Object, Integer> column = table.column("ref", SqlTypes.INTEGER, object -> 0, builder -> builder.notNull().defaultValue(0).unique().foreignKey(other, oid).addConstraint(condition()));
		SqlRendered rendered = RENDERER.renderColumnForTable(column, false);
		String sql = rendered.sql();
		assertTrue(sql.contains("NOT NULL"));
		assertTrue(sql.contains("DEFAULT"));
		assertTrue(sql.contains("UNIQUE"));
		assertTrue(sql.contains("REFERENCES"));
		assertTrue(sql.contains("CHECK"));
	}
	
	@Test
	void renderTableConstraintsWithAllConstraintTypes() throws SqlException {
		SqlTable<Object> other = SqlTable.create(Object.class, "other");
		SqlColumn<Object, Integer> oid = other.column("oid", SqlTypes.INTEGER, object -> 0);
		SqlTable<Object> table = SqlTable.create(Object.class, "users");
		SqlColumn<Object, Integer> a = table.column("a", SqlTypes.INTEGER, object -> 0, builder -> builder.primaryKey());
		SqlColumn<Object, Integer> b = table.column("b", SqlTypes.INTEGER, object -> 0, builder -> builder.primaryKey());
		SqlColumn<Object, Integer> ref = table.column("ref", SqlTypes.INTEGER, object -> 0);
		table.foreignKey(List.of(ref), other, oid);
		table.uniqueConstraint(a);
		table.checkConstraint(condition());
		table.compositePrimaryKey(List.of(a, b));
		SqlRendered rendered = RENDERER.renderTableConstraints(table);
		String sql = rendered.sql();
		assertTrue(sql.contains("PRIMARY KEY"));
		assertTrue(sql.contains("FOREIGN KEY"));
		assertTrue(sql.contains("UNIQUE"));
		assertTrue(sql.contains("CHECK"));
		assertFalse(sql.startsWith(","));
		assertFalse(sql.endsWith(","));
	}
}
