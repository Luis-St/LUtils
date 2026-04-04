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

package net.luis.utils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.luis.utils.io.database.SqlDatabase;
import net.luis.utils.io.database.condition.SqlCondition;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.function.*;
import net.luis.utils.io.database.query.SqlQueryProvider;
import net.luis.utils.io.database.query.row.SqlRow2;
import net.luis.utils.io.database.table.*;
import net.luis.utils.io.database.transaction.SqlTransaction;
import net.luis.utils.io.database.type.SqlCodecs;

import javax.sql.DataSource;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

/**
 * Demonstrates the complete database API after the refactoring.<br>
 *
 * @author Luis-St
 */
@SuppressWarnings("unused")
public class DatabaseTest {
	
	public record Person(int id, String name, String email, long version, Instant createdAt) {}
	
	public record Role(int id, String name) {}
	
	public record PersonRole(int personId, int roleId) {}
	
	private static final DataSource DATA_SOURCE;
	
	public static final SqlTable<Person> PERSON_TABLE;
	public static final SqlColumn<Person, Integer> ID;
	public static final SqlColumn<Person, String> NAME;
	public static final SqlColumn<Person, String> EMAIL;
	public static final SqlColumn<Person, Long> VERSION;
	public static final SqlColumn<Person, Instant> CREATED_AT;
	
	public static final SqlTable<Role> ROLE_TABLE;
	public static final SqlColumn<Role, Integer> ROLE_ID;
	public static final SqlColumn<Role, String> ROLE_NAME;
	
	public static final SqlTable<PersonRole> PERSON_ROLE_TABLE;
	public static final SqlColumn<PersonRole, Integer> PR_PERSON_ID;
	public static final SqlColumn<PersonRole, Integer> PR_ROLE_ID;
	public static final SqlCompositePrimaryKey<PersonRole> PERSON_ROLE_PK;
	
	static {
		HikariConfig config = new HikariConfig();
		config.setJdbcUrl("jdbc:postgresql://localhost:5432/test");
		config.setUsername("test");
		config.setPassword("test");
		DATA_SOURCE = new HikariDataSource(config);
		
		SqlTableBuilder<Person> personTableBuilder = SqlTable.of(Person.class, "person");
		ID = personTableBuilder.column("id", SqlCodecs.INTEGER, Person::id, col -> col.primaryKey().notNull().autoIncrement());
		NAME = personTableBuilder.column("name", SqlCodecs.STRING, Person::name, SqlColumnBuilder::notNull);
		EMAIL = personTableBuilder.column("email", SqlCodecs.STRING, Person::email, col -> col.notNull().unique());
		VERSION = personTableBuilder.column("version", SqlCodecs.LONG, Person::version, col -> col.notNull().defaultValue(0L));
		CREATED_AT = personTableBuilder.column("created_at", SqlCodecs.INSTANT, Person::createdAt, SqlColumnBuilder::notNull);
		PERSON_TABLE = personTableBuilder.build();
		
		SqlTableBuilder<Role> roleTableBuilder = SqlTable.of(Role.class, "role");
		ROLE_ID = roleTableBuilder.column("id", SqlCodecs.INTEGER, Role::id, col -> col.primaryKey().notNull().autoIncrement());
		ROLE_NAME = roleTableBuilder.column("name", SqlCodecs.STRING, Role::name, SqlColumnBuilder::notNull);
		ROLE_TABLE = roleTableBuilder.build();
		
		SqlTableBuilder<PersonRole> personRoleTableBuilder = SqlTable.of(PersonRole.class, "person_role");
		PR_PERSON_ID = personRoleTableBuilder.column("person_id", SqlCodecs.INTEGER, PersonRole::personId, col -> col.primaryKey().notNull().foreignKey(PERSON_TABLE));
		PR_ROLE_ID = personRoleTableBuilder.column("role_id", SqlCodecs.INTEGER, PersonRole::roleId, col -> col.primaryKey().notNull().foreignKey(ROLE_TABLE));
		PERSON_ROLE_PK = personRoleTableBuilder.compositePrimaryKey(PR_PERSON_ID, PR_ROLE_ID);
		PERSON_ROLE_TABLE = personRoleTableBuilder.build();
	}
	
	void databaseLifecycle() throws SqlException {
		try (SqlDatabase db = SqlDatabase.builder(DATA_SOURCE, null).build()) {
			// DDL via db.table(table)
			db.table(PERSON_TABLE).createIfNotExists();
			
			SqlQueryProvider<Person> query = db.from(PERSON_TABLE);
			query.insert(new Person(1, "Alice", "alice@example.com", 0, Instant.now())).execute();
			query.insert(
				new Person(2, "Bob", "bob@example.com", 0, Instant.now()),
				new Person(3, "Charlie", "charlie@example.com", 0, Instant.now())
			).execute();
		}
	}
	
	void selectQueries() throws SqlException {
		try (SqlDatabase db = SqlDatabase.builder(DATA_SOURCE, null).build()) {
			SqlQueryProvider<Person> persons = db.from(PERSON_TABLE);
			
			// Full entity select
			List<Person> all = persons.select().fetch();
			
			// Single column select (typed)
			List<String> names = persons.select(NAME).fetch();
			
			// Two-column typed select returning SqlRow2
			List<SqlRow2<Integer, String>> rows = persons.select(ID, NAME)
				.where(SqlStringFunctions.startsWith(NAME, "A"))
				.fetch();
			
			// Conditions — static combinators or instance chaining
			persons.select()
				.where(SqlCondition.allOf(SqlOrderableFunctions.greaterThan(ID, 5), SqlFunctions.isNull(NAME).not()))
				.orderBy(NAME.ascending())
				.limit(10)
				.fetch();
			
			// Fetch variants
			persons.select().where(SqlFunctions.equalTo(ID, 5)).fetchOne();
			persons.select().where(SqlFunctions.equalTo(ID, 999)).fetchFirst();
			persons.select().where(SqlFunctions.equalTo(ID, 1)).fetchOneOrNull();
			
			// Count and exists
			long count = persons.select().count();
			boolean exists = persons.select().where(SqlFunctions.equalTo(NAME, "Alice")).exists();
			
			// Pagination
			persons.select().fetchPage(0, 20);
			
			// Type-specific column operations
			persons.select().where(SqlStringFunctions.contains(NAME, "li")).fetch();
			persons.select().where(SqlOrderableFunctions.between(ID, 1, 100)).fetch();
			persons.select().where(SqlTemporalFunctions.withinLast(CREATED_AT, Duration.ofHours(24))).fetch();
		}
	}
	
	void transactions() throws SqlException {
		try (SqlDatabase db = SqlDatabase.builder(DATA_SOURCE, null).build()) {
			try (SqlTransaction tx = db.beginTransaction()) {
				SqlQueryProvider<Person> persons = tx.from(PERSON_TABLE);
				persons.insert(new Person(10, "Dave", "dave@example.com", 0, Instant.now())).execute();
				persons.update()
					.set(NAME, "David")
					.where(SqlFunctions.equalTo(ID, 10))
					.execute();
				tx.commit();
			}
		}
	}
	
	void updateAndDelete() throws SqlException {
		try (SqlDatabase db = SqlDatabase.builder(DATA_SOURCE, null).build()) {
			SqlQueryProvider<Person> persons = db.from(PERSON_TABLE);
			
			// Update with SET
			int updated = persons.update()
				.set(NAME, "Alice Updated")
				.where(SqlFunctions.equalTo(ID, 1))
				.execute();
			
			// Increment numeric column
			persons.update()
				.increment(ID, 1)
				.where(SqlFunctions.equalTo(NAME, "Bob"))
				.execute();
			
			// Decrement numeric column
			persons.update()
				.increment(ID, 1)
				.where(SqlFunctions.equalTo(NAME, "Alice"))
				.execute();
			
			// Update with RETURNING
			List<Person> updatedPersons = persons.update()
				.set(EMAIL, "new@example.com")
				.where(SqlFunctions.equalTo(ID, 2))
				.returning();
			
			// Delete
			int deleted = persons.delete()
				.where(SqlOrderableFunctions.lessThan(ID, 0))
				.execute();
			
			/*// Skip version check for bulk operations
			persons.skipVersionCheck().update()
				.set(NAME, "Bulk Updated")
				.where(NAME.string().like("%old%"))
				.execute();*/
		}
	}
	
	void lockingQueries() throws SqlException {
		try (SqlDatabase db = SqlDatabase.builder(DATA_SOURCE, null).build()) {
			try (SqlTransaction tx = db.beginTransaction()) {
				// FOR UPDATE locking
				tx.from(PERSON_TABLE).select()
					.where(SqlOrderableFunctions.lessThan(ID, 1))
					.forUpdate()
					.fetch();
				
				// SKIP LOCKED for job queue pattern
				tx.from(PERSON_TABLE).select()
					.where(SqlFunctions.isNull(NAME).not())
					.forUpdate()
					.skipLocked()
					.limit(5)
					.fetch();
				
				tx.commit();
			}
		}
	}
	
/*	void postgresSpecific() throws SqlException {
		PostgresTable<Person> pgTable = PostgresTable.of("person", Person.class);
		
		try (SqlDatabase db = SqlDatabase.builder().build()) {
			// Postgres-specific query provider
			PostgresQueryProvider<Person> pgPersons = PostgresQueryProvider.from(db, pgTable);
			
			// Postgres-specific: TRUNCATE CASCADE
			pgPersons.truncateCascade();
			
			// Postgres-specific: ON CONFLICT DO NOTHING
			pgPersons.insert(new Person(1, "Alice", "alice@example.com", 0, Instant.now()))
				.onConflictDoNothing()
				.execute();
			
			// Postgres-specific: DISTINCT ON
			pgPersons.select()
				.distinctOn(NAME)
				.fetch();
		}
	}*/
	
/*	void compositePrimaryKeyExample() throws SqlException {
		SqlMigration migration = new SqlMigration() {
			@Override
			public @NonNull Version version() {
				return Version.of(0, 2);
			}
			
			@Override
			public @NonNull String description() {
				return "Create role and person_role tables";
			}
			
			@Override
			public void up(@NonNull SqlMigrationBuilder builder) throws SqlException {
				builder.createTable(ROLE_TABLE, table -> {
					table.column(ROLE_ID, SqlColumnType.INTEGER, col -> col.notNull().autoIncrement());
					table.column(ROLE_NAME, SqlColumnType.VARCHAR, col -> col.notNull());
					table.primaryKey(ROLE_ID);
				});
				
				// Junction table: composite PK over two FK columns (neither is SqlPrimaryKeyColumn)
				builder.createTable(PERSON_ROLE_TABLE, table -> {
					table.column(PR_PERSON_ID, SqlColumnType.INTEGER, col -> col.notNull());
					table.column(PR_ROLE_ID, SqlColumnType.INTEGER, col -> col.notNull());
					table.compositePrimaryKey(PR_PERSON_ID, PR_ROLE_ID);
				});
			}
			
			@Override
			public void down(@NonNull SqlMigrationBuilder builder) throws SqlException {
				builder.dropTable(PERSON_ROLE_TABLE);
				builder.dropTable(ROLE_TABLE);
			}
		};
		
		// Alternatively, add a composite PK constraint to an existing table via ALTER TABLE
		SqlMigration alterMigration = new SqlMigration() {
			@Override
			public @NonNull Version version() {
				return Version.of(0, 3);
			}
			
			@Override
			public @NonNull String description() {
				return "Add composite primary key constraint to person_role";
			}
			
			@Override
			public void up(@NonNull SqlMigrationBuilder builder) throws SqlException {
				builder.addCompositePrimaryKey(PERSON_ROLE_TABLE, "pk_person_role", PR_PERSON_ID, PR_ROLE_ID);
			}
			
			@Override
			public void down(@NonNull SqlMigrationBuilder builder) throws SqlException {
				builder.dropConstraint(PERSON_ROLE_TABLE, "pk_person_role");
			}
		};
	}*/
	
/*	void migrationExample() throws SqlException {
		SqlMigration migration = new SqlMigration() {
			@Override
			public @NonNull Version version() {
				return Version.of(0, 1);
			}
			
			@Override
			public @NonNull String description() {
				return "Create person table";
			}
			
			@Override
			public void up(@NonNull SqlMigrationBuilder builder) throws SqlException {
				builder.createTable(PERSON_TABLE, table -> {
					table.column(ID, SqlColumnType.BIGINT, col -> col.notNull().autoIncrement());
					table.column(NAME, SqlColumnType.VARCHAR, col -> col.notNull());
					table.column(EMAIL, SqlColumnType.VARCHAR, col -> col.notNull().unique());
					table.column(VERSION, SqlColumnType.BIGINT, col -> col.notNull().defaultValue(0L));
					table.column(CREATED_AT, SqlColumnType.TIMESTAMP, col -> col.notNull());
					table.primaryKey(ID);
				});
				builder.createIndex(PERSON_TABLE, "idx_person_email", idx -> idx.columns(EMAIL).unique());
			}
			
			@Override
			public void down(@NonNull SqlMigrationBuilder builder) throws SqlException {
				builder.dropIndex("idx_person_email");
				builder.dropTable(PERSON_TABLE);
			}
		};
		
		try (SqlDatabase db = SqlDatabase.builder().build()) {
			SqlMigrationRunner runner = SqlMigrationRunner.of(db);
			runner.register(migration);
			
			// Dry run to preview SQL
			List<SqlRendered> preview = runner.dryRun();
			
			// Apply all pending migrations
			runner.migrate();
			
			// Check migration status
			List<SqlMigrationInfo> status = runner.status();
		}
	}*/
}
