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

import net.luis.utils.io.database.SqlDatabase;
import net.luis.utils.io.database.SqlDatabaseConfig;
import net.luis.utils.io.database.condition.SqlCondition;
import net.luis.utils.io.database.dialect.SqlColumnType;
import net.luis.utils.io.database.dialect.postgres.PostgresQueryProvider;
import net.luis.utils.io.database.dialect.postgres.PostgresTable;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.migration.*;
import net.luis.utils.io.database.query.SqlQueryProvider;
import net.luis.utils.io.database.query.row.SqlRow2;
import net.luis.utils.io.database.table.*;
import net.luis.utils.io.database.transaction.SqlTransaction;
import org.jspecify.annotations.NonNull;

import java.time.Instant;
import java.util.List;

/**
 * Demonstrates the complete database API after the refactoring.<br>
 *
 * @author Luis-St
 */
@SuppressWarnings({"unused", "unchecked"})
public class DatabaseTest {

	record Person(int id, String name, String email, long version, Instant createdAt) {}

	// --- Table definition (pure schema, no queries) ---

	static final SqlTable<Person> PERSON_TABLE = SqlTable.of("person", Person.class);
	static final SqlColumn<Integer> ID = PERSON_TABLE.column("id", Integer.class);
	static final SqlColumn<String> NAME = PERSON_TABLE.column("name", String.class);
	static final SqlColumn<String> EMAIL = PERSON_TABLE.column("email", String.class);
	static final SqlVersionColumn<Person, Long> VERSION = PERSON_TABLE.versionColumn("version", Long.class);
	static final SqlCreationColumn<Person, Instant> CREATED_AT = PERSON_TABLE.creationColumn("created_at", Instant.class);

	void databaseLifecycle() throws SqlException {
		// SqlDatabaseConfig.builder()...build() returns SqlDatabase directly
		try (SqlDatabase db = SqlDatabaseConfig.builder().build()) {
			// DDL via db.from(table)
			SqlQueryProvider<Person> persons = db.from(PERSON_TABLE);
			persons.createIfNotExists();

			// Insert with record-based mapping
			persons.insert(new Person(1, "Alice", "alice@example.com", 0, Instant.now()));
			persons.insert(
				new Person(2, "Bob", "bob@example.com", 0, Instant.now()),
				new Person(3, "Charlie", "charlie@example.com", 0, Instant.now())
			);
		}
	}

	void selectQueries() throws SqlException {
		try (SqlDatabase db = SqlDatabaseConfig.builder().build()) {
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
			persons.select().where(CREATED_AT.temporal().withinLast(java.time.Duration.ofHours(24))).fetch();
		}
	}

	void transactions() throws SqlException {
		try (SqlDatabase db = SqlDatabaseConfig.builder().build()) {
			// Explicit transactions only
			SqlTransaction tx = db.beginTransaction();
			try {
				SqlQueryProvider<Person> persons = tx.from(PERSON_TABLE);
				persons.insert(new Person(10, "Dave", "dave@example.com", 0, Instant.now())).execute();
				persons.update()
					.set(NAME, "David")
					.where(ID.equalTo(10))
					.execute();
				tx.commit();
			} catch (SqlException e) {
				tx.rollback();
				throw e;
			}
		}
	}

	void updateAndDelete() throws SqlException {
		try (SqlDatabase db = SqlDatabaseConfig.builder().build()) {
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
		try (SqlDatabase db = SqlDatabaseConfig.builder().build()) {
			SqlTransaction tx = db.beginTransaction();
			try {
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
			} catch (SqlException e) {
				tx.rollback();
				throw e;
			}
		}
	}

	void postgresSpecific() throws SqlException {
		PostgresTable<Person> pgTable = PostgresTable.of("person", Person.class);

		try (SqlDatabase db = SqlDatabaseConfig.builder().build()) {
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

	void migrationExample() throws SqlException {
		SqlMigration migration = new SqlMigration() {
			@Override
			public @NonNull String version() {
				return "001";
			}

			@Override
			public @NonNull String description() {
				return "Create person table";
			}

			@Override
			public void up(@NonNull SqlMigrationBuilder builder) throws SqlException {
				builder.createTable("person", table -> {
					table.column("id", SqlColumnType.BIGINT, col -> col.notNull().autoIncrement());
					table.column("name", SqlColumnType.VARCHAR, col -> col.notNull());
					table.column("email", SqlColumnType.VARCHAR, col -> col.notNull().unique());
					table.column("version", SqlColumnType.BIGINT, col -> col.notNull().defaultValue(0L));
					table.column("created_at", SqlColumnType.TIMESTAMP, col -> col.notNull());
					table.primaryKey("id");
				});
				builder.createIndex("person", "idx_person_email", idx -> idx.columns("email").unique());
			}

			@Override
			public void down(@NonNull SqlMigrationBuilder builder) throws SqlException {
				builder.dropIndex("idx_person_email");
				builder.dropTable("person");
			}
		};

		try (SqlDatabase db = SqlDatabaseConfig.builder().build()) {
			SqlMigrationRunner runner = SqlMigrationRunner.of(db);
			runner.register(migration);

			// Dry run to preview SQL
			List<net.luis.utils.io.database.SqlRendered> preview = runner.dryRun();

			// Apply all pending migrations
			runner.migrate();

			// Check migration status
			List<SqlMigrationInfo> status = runner.status();
		}
	}
}
