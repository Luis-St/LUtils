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

import net.luis.utils.io.database.SqlDataType;
import net.luis.utils.io.database.table.*;
import net.luis.utils.io.database.table.key.SqlCompositePrimaryKey;
import net.luis.utils.io.database.table.key.SqlForeignKey;

import java.time.Instant;

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
	
	public static final SqlTable<Person> PERSON_TABLE;
	public static final SqlColumn<Integer> ID;
	public static final SqlColumn<String> NAME;
	public static final SqlColumn<String> EMAIL;
	public static final SqlColumn<Long> VERSION;
	public static final SqlColumn<Instant> CREATED_AT;
	
	public static final SqlTable<Role> ROLE_TABLE;
	public static final SqlColumn<Integer> ROLE_ID;
	public static final SqlColumn<String> ROLE_NAME;
	
	public static final SqlTable<PersonRole> PERSON_ROLE_TABLE;
	public static final SqlColumn<Integer> PR_PERSON_ID;
	public static final SqlColumn<Integer> PR_ROLE_ID;
	public static final SqlForeignKey FK_PR_PERSON_ID;
	public static final SqlForeignKey FK_PR_ROLE_ID;
	public static final SqlCompositePrimaryKey PERSON_ROLE_PK;
	
	static {
		SqlTableBuilder<Person> personTableBuilder = SqlTable.of(Person.class, "person");
		ID = personTableBuilder.column("id", SqlDataType.INTEGER, col -> col.primaryKey().notNull().autoIncrement());
		NAME = personTableBuilder.column("name", SqlDataType.STRING, SqlColumnBuilder::notNull);
		EMAIL = personTableBuilder.column("email", SqlDataType.STRING, col -> col.notNull().unique());
		VERSION = personTableBuilder.column("version", SqlDataType.LONG, col -> col.notNull().defaultValue(0L));
		CREATED_AT = personTableBuilder.column("created_at", SqlDataType.INSTANT, SqlColumnBuilder::notNull);
		PERSON_TABLE = personTableBuilder.build();
		
		SqlTableBuilder<Role> roleTableBuilder = SqlTable.of(Role.class, "role");
		ROLE_ID = roleTableBuilder.column("id", SqlDataType.INTEGER, col -> col.primaryKey().notNull().autoIncrement());
		ROLE_NAME = roleTableBuilder.column("name", SqlDataType.STRING, SqlColumnBuilder::notNull);
		ROLE_TABLE = roleTableBuilder.build();
		
		SqlTableBuilder<PersonRole> personRoleTableBuilder = SqlTable.of(PersonRole.class, "person_role");
		PR_PERSON_ID = personRoleTableBuilder.column("person_id", SqlDataType.INTEGER, col -> col.primaryKey().notNull());
		PR_ROLE_ID = personRoleTableBuilder.column("role_id", SqlDataType.INTEGER, col -> col.primaryKey().notNull());
		PERSON_ROLE_PK = personRoleTableBuilder.compositePrimaryKey(PR_PERSON_ID, PR_ROLE_ID);
		FK_PR_PERSON_ID = personRoleTableBuilder.foreignKey(PR_PERSON_ID, PERSON_TABLE);
		FK_PR_ROLE_ID = personRoleTableBuilder.foreignKey(PR_ROLE_ID, ROLE_TABLE);
		PERSON_ROLE_TABLE = personRoleTableBuilder.build();
	}
	
	/*record Person(int id, String name, String email, long version, Instant createdAt) {}
	
	record Role(int id, String name) {}
	
	record PersonRole(int personId, int roleId) {}
	
	// --- Table definition (pure schema, no queries) ---
	
	static final SqlTable<Person> PERSON_TABLE = SqlTable.of("person", Person.class);
	static final SqlPrimaryKeyColumn<Person, Integer> ID = PERSON_TABLE.primaryKeyColumn("id", Integer.class, col -> col.notNull().autoIncrement());
	static final SqlColumn<String> NAME = PERSON_TABLE.column("name", String.class, SqlColumnBuilder::notNull);
	static final SqlColumn<String> EMAIL = PERSON_TABLE.column("email", String.class, col -> col.notNull().unique());
	static final SqlVersionColumn<Person, Long> VERSION = PERSON_TABLE.versionColumn("version", Long.class, col -> col.notNull().defaultValue(0L));
	static final SqlCreationColumn<Person, Instant> CREATED_AT = PERSON_TABLE.creationColumn("created_at", Instant.class, SqlColumnBuilder::notNull);
	
	static final SqlTable<Role> ROLE_TABLE = SqlTable.of("role", Role.class);
	static final SqlPrimaryKeyColumn<Role, Integer> ROLE_ID = ROLE_TABLE.primaryKeyColumn("id", Integer.class, col -> col.notNull().autoIncrement());
	static final SqlColumn<String> ROLE_NAME = ROLE_TABLE.column("name", String.class, SqlColumnBuilder::notNull);
	
	// Junction table: composite PK over two FK columns
	static final SqlTable<PersonRole> PERSON_ROLE_TABLE = SqlTable.of("person_role", PersonRole.class);
	static final SqlForeignColumn<Integer, Person> PR_PERSON_ID = PERSON_ROLE_TABLE.foreignColumn("person_id", Integer.class, PERSON_TABLE);
	static final SqlForeignColumn<Integer, Role> PR_ROLE_ID = PERSON_ROLE_TABLE.foreignColumn("role_id", Integer.class, ROLE_TABLE);
	static final SqlCompositePrimaryKey<PersonRole> PERSON_ROLE_PK = PERSON_ROLE_TABLE.compositePrimaryKey(PR_PERSON_ID, PR_ROLE_ID);
	
	void databaseLifecycle() throws SqlException {
		try (SqlDatabase db = SqlDatabase.builder().build()) {
			// DDL via db.from(table)
			SqlQueryProvider<Person> persons = db.from(PERSON_TABLE);
			persons.createIfNotExists();
			
			// Insert with record-based mapping
			persons.insert(new Person(1, "Alice", "alice@example.com", 0, Instant.now())).execute();
			persons.insert(
				new Person(2, "Bob", "bob@example.com", 0, Instant.now()),
				new Person(3, "Charlie", "charlie@example.com", 0, Instant.now())
			).execute();
		}
	}
	
	void selectQueries() throws SqlException {
		try (SqlDatabase db = SqlDatabase.builder().build()) {
			SqlQueryProvider<Person> persons = db.from(PERSON_TABLE);
			
			// Full entity select
			List<Person> all = persons.select().fetch();
			
			// Single column select (typed)
			List<String> names = persons.select(NAME).fetch();
			
			// Two-column typed select returning SqlRow2
			List<SqlRow2<Integer, String>> rows = persons.select(ID, NAME)
				.where(NAME.string().startsWith("A"))
				.fetch();
			
			// Conditions — static combinators or instance chaining
			persons.select()
				.where(SqlCondition.allOf(ID.greaterThan(5), NAME.isNotNull()))
				.orderBy(NAME.asc())
				.limit(10)
				.fetch();
			
			// Fetch variants
			persons.select().where(ID.equalTo(1)).fetchOne();
			persons.select().where(ID.equalTo(999)).fetchFirst();
			persons.select().where(ID.equalTo(1)).fetchOneOrNull();
			
			// Count and exists
			long count = persons.select().count();
			boolean exists = persons.select().where(NAME.equalTo("Alice")).exists();
			
			// Pagination
			persons.select().fetchPage(0, 20);
			
			// Type-specific column operations
			persons.select().where(NAME.string().contains("li")).fetch();
			persons.select().where(ID.between(1, 100)).fetch();
			persons.select().where(CREATED_AT.temporal().withinLast(Duration.ofHours(24))).fetch();
		}
	}
	
	void transactions() throws SqlException {
		try (SqlDatabase db = SqlDatabase.builder().build()) {
			try (SqlTransaction tx = db.beginTransaction()) {
				SqlQueryProvider<Person> persons = tx.from(PERSON_TABLE);
				persons.insert(new Person(10, "Dave", "dave@example.com", 0, Instant.now())).execute();
				persons.update()
					.set(NAME, "David")
					.where(ID.equalTo(10))
					.execute();
				tx.commit();
			}
		}
	}
	
	void updateAndDelete() throws SqlException {
		try (SqlDatabase db = SqlDatabase.builder().build()) {
			SqlQueryProvider<Person> persons = db.from(PERSON_TABLE);
			
			// Update with SET
			int updated = persons.update()
				.set(NAME, "Alice Updated")
				.where(ID.equalTo(1))
				.execute();
			
			// Increment numeric column
			persons.update()
				.increment(ID, 1)
				.where(NAME.equalTo("Bob"))
				.execute();
			
			// Update with RETURNING
			List<Person> updatedPersons = persons.update()
				.set(EMAIL, "new@example.com")
				.where(ID.equalTo(2))
				.returning();
			
			// Delete
			int deleted = persons.delete()
				.where(ID.lessThan(0))
				.execute();
			
			// Skip version check for bulk operations
			persons.skipVersionCheck().update()
				.set(NAME, "Bulk Updated")
				.where(NAME.string().like("%old%"))
				.execute();
		}
	}
	
	void lockingQueries() throws SqlException {
		try (SqlDatabase db = SqlDatabase.builder().build()) {
			try (SqlTransaction tx = db.beginTransaction()) {
				// FOR UPDATE locking
				tx.from(PERSON_TABLE).select()
					.where(ID.equalTo(1))
					.forUpdate()
					.fetch();
				
				// SKIP LOCKED for job queue pattern
				tx.from(PERSON_TABLE).select()
					.where(NAME.isNotNull())
					.forUpdate()
					.skipLocked()
					.limit(5)
					.fetch();
				
				tx.commit();
			}
		}
	}
	
	void postgresSpecific() throws SqlException {
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
	}
	
	void compositePrimaryKeyExample() throws SqlException {
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
	}
	
	void migrationExample() throws SqlException {
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
