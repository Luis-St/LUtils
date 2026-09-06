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

package net.luis.utils.io.database.integration;

import net.luis.utils.function.throwable.ThrowableBiConsumer;
import net.luis.utils.io.database.*;
import net.luis.utils.io.database.audit.SqlAuditConfig;
import net.luis.utils.io.database.dialect.SqlFeature;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.exception.client.SqlMigrationConflictException;
import net.luis.utils.io.database.index.SqlIndex;
import net.luis.utils.io.database.index.SqlIndexMethod;
import net.luis.utils.io.database.integration.reflection.SqlEngineFixture;
import net.luis.utils.io.database.integration.reflection.SqlEngineFixture.Engine;
import net.luis.utils.io.database.migration.*;
import net.luis.utils.io.database.migration.store.SqlMigrationStore;
import net.luis.utils.io.database.migration.store.SqlMigrationTableStore;
import net.luis.utils.io.database.rendering.SqlRendered;
import net.luis.utils.io.database.table.*;
import net.luis.utils.io.database.type.SqlTypes;
import net.luis.utils.io.database.type.parameter.SqlParameter;
import net.luis.utils.util.Version;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.testcontainers.DockerClientFactory;

import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

/**
 * Integration tests for the database migration system.<br>
 *
 * @author Luis-St
 */
class SqlMigrationIntegrationTest {
	
	private static final List<Engine> ENGINES = new ArrayList<>();
	private static final SqlTable<MigRow> MIG = SqlTable.create(MigRow.class, "mit_item");
	private static final SqlColumn<MigRow, Integer> M_ID = MIG.column("id", SqlTypes.INTEGER, MigRow::id, col -> col.primaryKey().notNull());
	private static final SqlColumn<MigRow, String> M_NAME = MIG.column("name", SqlTypes.STRING.configure(SqlParameter.length(64)), MigRow::name, SqlColumnBuilder::notNull);
	private static final SqlColumn<MigRow, String> M_DESC = MIG.column("description", SqlTypes.STRING.configure(SqlParameter.length(255)), MigRow::description);
	private static final SqlTable<MigRow> MIG_RENAMED = SqlTable.create(MigRow.class, "mit_item_renamed");
	private static final SqlColumn<MigRow, Integer> MR_ID = MIG_RENAMED.column("id", SqlTypes.INTEGER, MigRow::id, col -> col.primaryKey().notNull());
	private static final SqlColumn<MigRow, String> MR_NAME = MIG_RENAMED.column("name", SqlTypes.STRING.configure(SqlParameter.length(64)), MigRow::name, SqlColumnBuilder::notNull);
	private static final SqlColumn<MigRow, String> MR_DESC = MIG_RENAMED.column("description", SqlTypes.STRING.configure(SqlParameter.length(255)), MigRow::description);
	private static final SqlTable<Extra> EXTRA = SqlTable.create(Extra.class, "mit_extra");
	private static final SqlColumn<Extra, Integer> E_ID = EXTRA.column("id", SqlTypes.INTEGER, Extra::id, col -> col.primaryKey().notNull());
	private static final SqlColumn<Extra, Integer> E_REF = EXTRA.column("ref", SqlTypes.INTEGER, Extra::ref);
	private static final SqlColumn<Extra, Integer> E_AMOUNT = EXTRA.column("amount", SqlTypes.INTEGER, Extra::amount);
	private static final SqlTable<MigExtra> MIG_X = SqlTable.create(MigExtra.class, "mit_item");
	private static final SqlColumn<MigExtra, Integer> MX_ID = MIG_X.column("id", SqlTypes.INTEGER, MigExtra::id, col -> col.primaryKey().notNull());
	private static final SqlColumn<MigExtra, String> MX_NAME = MIG_X.column("name", SqlTypes.STRING.configure(SqlParameter.length(64)), MigExtra::name, SqlColumnBuilder::notNull);
	private static final SqlColumn<MigExtra, String> MX_DESC = MIG_X.column("description", SqlTypes.STRING.configure(SqlParameter.length(255)), MigExtra::description);
	private static final SqlColumn<MigExtra, String> MX_EXTRA = MIG_X.column("extra", SqlTypes.STRING.configure(SqlParameter.length(32)), MigExtra::extra);
	private static final SqlTable<MigCounter> MIG_C = SqlTable.create(MigCounter.class, "mit_item");
	private static final SqlColumn<MigCounter, Integer> MC_ID = MIG_C.column("id", SqlTypes.INTEGER, MigCounter::id, col -> col.primaryKey().notNull());
	private static final SqlColumn<MigCounter, String> MC_NAME = MIG_C.column("name", SqlTypes.STRING.configure(SqlParameter.length(64)), MigCounter::name, SqlColumnBuilder::notNull);
	private static final SqlColumn<MigCounter, String> MC_DESC = MIG_C.column("description", SqlTypes.STRING.configure(SqlParameter.length(255)), MigCounter::description);
	private static final SqlColumn<MigCounter, Integer> MC_COUNTER = MIG_C.column("counter", SqlTypes.INTEGER, MigCounter::counter);
	private static final SqlTable<AuditRow> AUDITED = SqlTable.audited(AuditRow.class, "mit_audited");
	private static final SqlColumn<AuditRow, Integer> AU_ID = AUDITED.column("id", SqlTypes.INTEGER, AuditRow::id, col -> col.primaryKey().notNull());
	private static final SqlColumn<AuditRow, String> AU_NAME = AUDITED.column("name", SqlTypes.STRING.configure(SqlParameter.length(64)), AuditRow::name, SqlColumnBuilder::notNull);
	private static final SqlTable<AuditRow> AUDITED_PLAIN = SqlTable.create(AuditRow.class, "mit_audited"); // enableAuditing adds the audit columns, so the table is created without them
	private static final SqlColumn<AuditRow, Integer> AP_ID = AUDITED_PLAIN.column("id", SqlTypes.INTEGER, AuditRow::id, col -> col.primaryKey().notNull());
	private static final SqlColumn<AuditRow, String> AP_NAME = AUDITED_PLAIN.column("name", SqlTypes.STRING.configure(SqlParameter.length(64)), AuditRow::name, SqlColumnBuilder::notNull);
	private static final SqlAuditConfig CUSTOM_AUDIT = SqlAuditConfig.builder().versionColumn("rev").createdAtColumn("born_at").build();
	
	@BeforeAll
	static void startEngines() throws IOException {
		assumeTrue(DockerClientFactory.instance().isDockerAvailable(), "Docker is required for the container database integration tests");
		ENGINES.addAll(SqlEngineFixture.startEngines());
	}
	
	@AfterAll
	static void stopEngines() {
		SqlEngineFixture.stopEngines(ENGINES);
		ENGINES.clear();
	}
	
	private static @NonNull Stream<Engine> engines() {
		return ENGINES.stream();
	}
	
	private static @NonNull SqlMigration migration(
		int minor,
		@NonNull String description,
		@NonNull ThrowableBiConsumer<SqlMigrationBuilder, SqlMigrationSchema, SqlException> up,
		@NonNull ThrowableBiConsumer<SqlMigrationBuilder, SqlMigrationSchema, SqlException> down
	) {
		return new SqlMigration() {
			
			@Override
			public boolean allowsNonAtomicExecution() {
				return true; // MySQL/MariaDB implicitly commit on DDL, so atomic execution is not possible
			}
			
			@Override
			public @NonNull Version version() {
				return Version.of(0, minor);
			}
			
			@Override
			public @NonNull String description() {
				return description;
			}
			
			@Override
			public void up(@NonNull SqlMigrationBuilder builder, @NonNull SqlMigrationSchema schema) throws SqlException {
				up.accept(builder, schema);
			}
			
			@Override
			public void down(@NonNull SqlMigrationBuilder builder, @NonNull SqlMigrationSchema schema) throws SqlException {
				down.accept(builder, schema);
			}
		};
	}
	
	private static @NonNull SqlMigration createMigTable() {
		return migration(1, "Create item table", (builder, schema) -> builder.createTable(MIG, table -> {
			table.column(M_ID, SqlTypes.INTEGER, SqlMigrationColumnBuilder::notNull);
			table.column(M_NAME, SqlTypes.STRING.configure(SqlParameter.length(64)), SqlMigrationColumnBuilder::notNull);
			table.column(M_DESC, SqlTypes.STRING.configure(SqlParameter.length(255)));
			table.primaryKey(M_ID);
		}), (builder, schema) -> builder.dropTable(MIG));
	}
	
	private static @NonNull SqlMigration createExtraTable(int minor) {
		return migration(minor, "Create extra table", (builder, schema) -> builder.createTable(EXTRA, table -> {
			table.column(E_ID, SqlTypes.INTEGER, SqlMigrationColumnBuilder::notNull);
			table.column(E_REF, SqlTypes.INTEGER);
			table.column(E_AMOUNT, SqlTypes.INTEGER);
			table.primaryKey(E_ID);
		}), (builder, schema) -> builder.dropTable(EXTRA));
	}
	
	private static @NonNull SqlMigrationRunner runnerWith(@NonNull SqlDatabase database, SqlMigration @NonNull ... migrations) throws SqlException {
		SqlMigrationRunner runner = SqlMigrationRunner.of(database);
		runner.register(List.of(migrations));
		return runner;
	}
	
	private static void resetMigrationState(@NonNull SqlDatabase database) throws SqlException {
		database.table(EXTRA).dropIfExists();
		database.table(MIG).dropIfExists();
		database.table(MIG_RENAMED).dropIfExists();
		database.table(AUDITED).dropIfExists();
		database.table(SqlTable.create(Void.class, "_sql_migrations")).dropIfExists();
		database.table(SqlTable.create(Void.class, "_sql_schema_columns")).dropIfExists();
		database.table(SqlTable.create(Void.class, "_sql_schema_check_constraints")).dropIfExists();
	}
	
	private static void seedMigRows(@NonNull SqlDatabase database) throws SqlException {
		database.from(MIG).insert(List.of(
			new MigRow(1, "first", "alpha"),
			new MigRow(2, "second", "beta"),
			new MigRow(3, "third", null)
		)).execute();
	}
	
	private static long appliedCount(@NonNull SqlMigrationRunner runner) throws SqlException {
		return runner.status().stream().filter(info -> info.status() == SqlMigrationStatus.APPLIED).count();
	}
	
	private static boolean hasIndex(@NonNull SqlDatabase database, @NonNull SqlTable<?> table, @NonNull String name) throws SqlException {
		return database.table(table).getIndexes().stream().map(SqlIndex::name).anyMatch(name::equals);
	}
	
	private static @NonNull SqlMigrationStore failingStore(@NonNull SqlDatabase database) {
		SqlMigrationTableStore delegate = new SqlMigrationTableStore(database.getDataSource(), database.getDialect());
		return new SqlMigrationStore() {
			
			@Override
			public void initialize() throws SqlException {
				delegate.initialize();
			}
			
			@Override
			public @NonNull List<SqlMigrationInfo> loadAll() throws SqlException {
				return delegate.loadAll();
			}
			
			@Override
			public void save(@NonNull SqlMigrationInfo info) throws SqlException {
				throw new SqlException("Store save failed on purpose");
			}
			
			@Override
			public void save(@NonNull Connection connection, @NonNull SqlMigrationInfo info) throws SqlException {
				throw new SqlException("Store save failed on purpose");
			}
			
			@Override
			public void update(@NonNull Version version, @NonNull SqlMigrationStatus status) throws SqlException {
				delegate.update(version, status);
			}
		};
	}
	
	private static @NonNull SqlMigrationStore countingStore(@NonNull SqlDatabase database, @NonNull AtomicInteger initializes, @NonNull AtomicInteger saves) {
		SqlMigrationTableStore delegate = new SqlMigrationTableStore(database.getDataSource(), database.getDialect());
		return new SqlMigrationStore() {
			
			@Override
			public void initialize() throws SqlException {
				initializes.incrementAndGet();
				delegate.initialize();
			}
			
			@Override
			public @NonNull List<SqlMigrationInfo> loadAll() throws SqlException {
				return delegate.loadAll();
			}
			
			@Override
			public void save(@NonNull SqlMigrationInfo info) throws SqlException {
				saves.incrementAndGet();
				delegate.save(info);
			}
			
			@Override
			public void save(@NonNull Connection connection, @NonNull SqlMigrationInfo info) throws SqlException {
				saves.incrementAndGet();
				delegate.save(connection, info);
			}
			
			@Override
			public void update(@NonNull Version version, @NonNull SqlMigrationStatus status) throws SqlException {
				delegate.update(version, status);
			}
		};
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void migrateCreatesStoreTablesOnFirstRun(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetMigrationState(database);
		
		SqlMigrationRunner runner = runnerWith(database, createMigTable());
		assertEquals(SqlMigrationStatus.PENDING, runner.status().getFirst().status());
		
		runner.migrate();
		assertTrue(database.table(SqlTable.create(Void.class, "_sql_migrations")).exists());
		assertTrue(database.table(SqlTable.create(Void.class, "_sql_schema_columns")).exists());
		assertEquals(SqlMigrationStatus.APPLIED, runner.status().getFirst().status());
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void migrateWithFreshRunnerReadsExistingStore(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetMigrationState(database);
		runnerWith(database, createMigTable()).migrate();
		
		SqlMigrationRunner second = runnerWith(database, createMigTable());
		assertEquals(SqlMigrationStatus.APPLIED, second.status().getFirst().status());
		assertDoesNotThrow(second::migrate);
		assertEquals(1L, appliedCount(second));
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void storeInitializeIsIdempotentAcrossRunners(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetMigrationState(database);
		
		assertDoesNotThrow(() -> runnerWith(database, createMigTable()).migrate());
		assertDoesNotThrow(() -> runnerWith(database, createMigTable()).migrate());
		SqlMigrationRunner third = runnerWith(database, createMigTable());
		assertDoesNotThrow(third::migrate);
		assertEquals(1L, appliedCount(third));
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void migrateWithNoRegisteredMigrationsIsNoOp(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetMigrationState(database);
		
		SqlMigrationRunner runner = SqlMigrationRunner.of(database);
		assertDoesNotThrow(runner::migrate);
		assertTrue(runner.status().isEmpty());
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void registerListRegistersAllMigrations(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetMigrationState(database);
		
		SqlMigrationRunner runner = SqlMigrationRunner.of(database);
		runner.register(List.of(createMigTable(), createExtraTable(2)));
		runner.migrate();
		
		assertEquals(2L, appliedCount(runner));
		assertTrue(database.table(MIG).exists());
		assertTrue(database.table(EXTRA).exists());
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void migrateWithFailingStatementOnTransactionalDialectLeavesNoPartialSchema(@NonNull Engine engine) throws SqlException {
		assumeTrue(engine.supports(SqlFeature.TRANSACTIONAL_DDL), "Transactional DDL not supported by " + engine.name());
		SqlDatabase database = engine.database();
		resetMigrationState(database);
		
		SqlMigrationRunner runner = runnerWith(database, createMigTable(), migration(2, "Broken", (builder, schema) -> {
			builder.createIndex(MIG, "mit_broken_idx", index -> index.columns(M_NAME));
			builder.dropColumn(MX_EXTRA); // The column does not exist, so the statement fails
		}, (builder, schema) -> builder.dropIndex(MIG, "mit_broken_idx")));
		
		assertThrows(SqlException.class, runner::migrate);
		assertEquals(1L, appliedCount(runner));
		assertFalse(hasIndex(database, MIG, "mit_broken_idx"));
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void migrateWithFailingStoreSaveRollsBackDdl(@NonNull Engine engine) throws SqlException {
		assumeTrue(engine.supports(SqlFeature.TRANSACTIONAL_DDL), "Transactional DDL not supported by " + engine.name());
		SqlDatabase database = engine.database();
		resetMigrationState(database);
		
		SqlMigrationRunner runner = SqlMigrationRunner.of(database, failingStore(database));
		runner.register(createMigTable());
		
		assertThrows(SqlException.class, runner::migrate);
		assertFalse(database.table(MIG).exists());
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void rollbackWithFailingDownLeavesMigrationApplied(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetMigrationState(database);
		
		SqlMigrationRunner runner = runnerWith(database, createMigTable(), migration(2, "Failing down",
			(builder, schema) -> builder.addColumn(MX_EXTRA, SqlTypes.STRING.configure(SqlParameter.length(32))),
			(builder, schema) -> builder.dropColumn(MC_COUNTER) // The column was never added, so the down statement fails
		));
		runner.migrate();
		
		assertThrows(SqlException.class, runner::rollback);
		assertEquals(2L, appliedCount(runner));
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void migrateWithMissingSchemaSnapshotThrowsConflict(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetMigrationState(database);
		runnerWith(database, createMigTable()).migrate();
		
		database.table(SqlTable.create(Void.class, "_sql_schema_columns")).truncate();
		SqlMigrationRunner runner = runnerWith(database, createMigTable(), createExtraTable(2));
		
		assertThrows(SqlMigrationConflictException.class, runner::migrate);
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void renameTablePreservesRowsAndConstraints(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetMigrationState(database);
		
		SqlMigrationRunner runner = runnerWith(database, createMigTable(), migration(2, "Rename",
			(builder, schema) -> builder.renameTable(MIG, MIG_RENAMED),
			(builder, schema) -> builder.renameTable(MIG_RENAMED, MIG)
		));
		runnerWith(database, createMigTable()).migrate();
		seedMigRows(database);
		runner.migrate();
		
		assertEquals(3L, database.from(MIG_RENAMED).select().count());
		runner.rollback();
		assertEquals(3L, database.from(MIG).select().count());
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void createTableWithColumnOptionsAppliesConstraintsInline(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetMigrationState(database);
		
		runnerWith(database, migration(1, "Create with options", (builder, schema) -> builder.createTable(MIG, table -> {
			table.column(M_ID, SqlTypes.INTEGER, SqlMigrationColumnBuilder::notNull);
			table.column(M_NAME, SqlTypes.STRING.configure(SqlParameter.length(64)), col -> col.notNull().unique());
			table.column(M_DESC, SqlTypes.STRING.configure(SqlParameter.length(255)), col -> col.defaultValue("none"));
			table.primaryKey(M_ID);
		}), (builder, schema) -> builder.dropTable(MIG))).migrate();
		
		database.from(MIG).insert(new MigRow(1, "unique", null)).omitting(M_DESC).execute();
		assertEquals("none", database.from(MIG).select(M_DESC).where(Sql.equalTo(M_ID, 1)).fetchOne());
		assertThrows(SqlException.class, () -> database.from(MIG).insert(new MigRow(2, "unique", null)).omitting(M_DESC).execute());
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void rollbackOfDropTableRecreatesTable(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetMigrationState(database);
		
		SqlMigrationRunner runner = runnerWith(database, createMigTable(), migration(2, "Drop table",
			(builder, schema) -> builder.dropTable(MIG),
			(builder, schema) -> builder.createTable(MIG, table -> {
				table.column(M_ID, SqlTypes.INTEGER, SqlMigrationColumnBuilder::notNull);
				table.column(M_NAME, SqlTypes.STRING.configure(SqlParameter.length(64)), SqlMigrationColumnBuilder::notNull);
				table.column(M_DESC, SqlTypes.STRING.configure(SqlParameter.length(255)));
				table.primaryKey(M_ID);
			})
		));
		runner.migrate();
		assertFalse(database.table(MIG).exists());
		
		runner.rollback();
		assertTrue(database.table(MIG).exists());
		database.from(MIG).insert(new MigRow(1, "restored", null)).execute();
		assertThrows(SqlException.class, () -> database.from(MIG).insert(new MigRow(1, "duplicate", null)).execute());
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void addColumnNullableAppliesAndIsQueryable(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetMigrationState(database);
		runnerWith(database, createMigTable()).migrate();
		seedMigRows(database);
		
		SqlMigrationRunner runner = runnerWith(database, createMigTable(), migration(2, "Add column",
			(builder, schema) -> builder.addColumn(MX_EXTRA, SqlTypes.STRING.configure(SqlParameter.length(32))),
			(builder, schema) -> builder.dropColumn(MX_EXTRA)
		));
		runner.migrate();
		
		assertNull(database.from(MIG).select(MX_EXTRA).where(Sql.equalTo(M_ID, 1)).fetchOne());
		runner.rollback();
		assertThrows(SqlException.class, () -> database.from(MIG).select(MX_EXTRA).fetch());
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void addColumnWithNotNullAndDefaultBackfillsExistingRows(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetMigrationState(database);
		runnerWith(database, createMigTable()).migrate();
		seedMigRows(database);
		
		runnerWith(database, createMigTable(), migration(2, "Backfill",
			(builder, schema) -> builder.addColumn(MX_EXTRA, SqlTypes.STRING.configure(SqlParameter.length(32)), col -> col.notNull().defaultValue("x")),
			(builder, schema) -> builder.dropColumn(MX_EXTRA)
		)).migrate();
		
		List<String> values = database.from(MIG).select(MX_EXTRA).fetch();
		assertEquals(3, values.size());
		assertTrue(values.stream().allMatch("x"::equals));
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void addColumnWithUniqueOptionRejectsDuplicates(@NonNull Engine engine) throws SqlException {
		assumeTrue(engine.supports(SqlFeature.ADD_CONSTRAINT), "Adding constraints not supported by " + engine.name());
		SqlDatabase database = engine.database();
		resetMigrationState(database);
		runnerWith(database, createMigTable()).migrate();
		
		runnerWith(database, createMigTable(), migration(2, "Add unique column",
			(builder, schema) -> builder.addColumn(MX_EXTRA, SqlTypes.STRING.configure(SqlParameter.length(32)), SqlMigrationColumnBuilder::unique),
			(builder, schema) -> builder.dropColumn(MX_EXTRA)
		)).migrate();
		
		database.from(MIG_X).insert(new MigExtra(1, "a", null, "same")).execute();
		assertThrows(SqlException.class, () -> database.from(MIG_X).insert(new MigExtra(2, "b", null, "same")).execute());
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void addColumnWithCheckRejectsViolatingRow(@NonNull Engine engine) throws SqlException {
		assumeTrue(engine.supports(SqlFeature.ADD_CONSTRAINT), "Adding constraints not supported by " + engine.name());
		SqlDatabase database = engine.database();
		resetMigrationState(database);
		runnerWith(database, createMigTable()).migrate();
		
		runnerWith(database, createMigTable(), migration(2, "Add checked column",
			(builder, schema) -> builder.addColumn(MC_COUNTER, SqlTypes.INTEGER, col -> col.check(Sql.greaterThanOrEqualTo(MC_COUNTER, 0))),
			(builder, schema) -> builder.dropColumn(MC_COUNTER)
		)).migrate();
		
		assertDoesNotThrow(() -> database.from(MIG_C).insert(new MigCounter(1, "ok", null, 5)).execute());
		assertThrows(SqlException.class, () -> database.from(MIG_C).insert(new MigCounter(2, "bad", null, -1)).execute());
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void dropColumnRemovesColumnAndPreservesRemainingData(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetMigrationState(database);
		runnerWith(database, createMigTable()).migrate();
		seedMigRows(database);
		
		runnerWith(database, createMigTable(), migration(2, "Drop column",
			(builder, schema) -> builder.dropColumn(M_DESC),
			(builder, schema) -> builder.addColumn(M_DESC, SqlTypes.STRING.configure(SqlParameter.length(255)))
		)).migrate();
		
		assertEquals(3L, database.from(MIG).select().count());
		assertEquals("first", database.from(MIG).select(M_NAME).where(Sql.equalTo(M_ID, 1)).fetchOne());
		assertThrows(SqlException.class, () -> database.from(MIG).select(M_DESC).fetch());
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void renameColumnPreservesDataUnderNewName(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetMigrationState(database);
		runnerWith(database, createMigTable()).migrate();
		seedMigRows(database);
		
		runnerWith(database, createMigTable(), migration(2, "Rename column",
			(builder, schema) -> builder.renameColumn(M_DESC, MX_EXTRA),
			(builder, schema) -> builder.renameColumn(MX_EXTRA, M_DESC)
		)).migrate();
		
		assertEquals("alpha", database.from(MIG).select(MX_EXTRA).where(Sql.equalTo(M_ID, 1)).fetchOne());
		assertEquals(3L, database.from(MIG).select().count());
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void alterColumnSetTypeWideningPreservesValues(@NonNull Engine engine) throws SqlException {
		assumeTrue(engine.supports(SqlFeature.ALTER_COLUMN), "Altering columns not supported by " + engine.name());
		SqlDatabase database = engine.database();
		resetMigrationState(database);
		runnerWith(database, createMigTable()).migrate();
		seedMigRows(database);
		
		runnerWith(database, createMigTable(), migration(2, "Widen",
			(builder, schema) -> builder.alterColumn(M_NAME, changes -> changes.setType(SqlTypes.STRING.configure(SqlParameter.length(255)))),
			(builder, schema) -> builder.alterColumn(M_NAME, changes -> changes.setType(SqlTypes.STRING.configure(SqlParameter.length(64))))
		)).migrate();
		
		assertEquals("first", database.from(MIG).select(M_NAME).where(Sql.equalTo(M_ID, 1)).fetchOne());
		assertDoesNotThrow(() -> database.from(MIG).insert(new MigRow(4, "x".repeat(200), null)).execute());
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void alterColumnSetNullableFalseWithExistingNullsFails(@NonNull Engine engine) throws SqlException {
		assumeTrue(engine.supports(SqlFeature.ALTER_COLUMN), "Altering columns not supported by " + engine.name());
		SqlDatabase database = engine.database();
		resetMigrationState(database);
		runnerWith(database, createMigTable()).migrate();
		seedMigRows(database); // The third row holds a null description
		
		SqlMigrationRunner runner = runnerWith(database, createMigTable(), migration(2, "Forbid nulls",
			(builder, schema) -> builder.alterColumn(M_DESC, changes -> changes.setNullable(false)),
			(builder, schema) -> builder.alterColumn(M_DESC, changes -> changes.setNullable(true))
		));
		
		assertThrows(SqlException.class, runner::migrate);
		assertEquals(1L, appliedCount(runner));
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void alterColumnSetNullableTrueAllowsNulls(@NonNull Engine engine) throws SqlException {
		assumeTrue(engine.supports(SqlFeature.ALTER_COLUMN), "Altering columns not supported by " + engine.name());
		SqlDatabase database = engine.database();
		resetMigrationState(database);
		runnerWith(database, createMigTable()).migrate();
		
		assertThrows(SqlException.class, () -> database.from(MIG).insert(new MigRow(1, "x", null)).omitting(M_NAME).execute());
		runnerWith(database, createMigTable(), migration(2, "Allow nulls",
			(builder, schema) -> builder.alterColumn(M_NAME, changes -> changes.setNullable(true)),
			(builder, schema) -> builder.alterColumn(M_NAME, changes -> changes.setNullable(false))
		)).migrate();
		
		assertDoesNotThrow(() -> database.from(MIG).insert(new MigRow(2, "x", null)).omitting(M_NAME).execute());
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void alterColumnSetDefaultAppliesToSubsequentInserts(@NonNull Engine engine) throws SqlException {
		assumeTrue(engine.supports(SqlFeature.ALTER_COLUMN), "Altering columns not supported by " + engine.name());
		SqlDatabase database = engine.database();
		resetMigrationState(database);
		runnerWith(database, createMigTable()).migrate();
		
		runnerWith(database, createMigTable(), migration(2, "Set default",
			(builder, schema) -> builder.alterColumn(M_DESC, changes -> changes.setDefault("fallback")),
			(builder, schema) -> builder.alterColumn(M_DESC, SqlMigrationColumnAlter::dropDefault)
		)).migrate();
		
		database.from(MIG).insert(new MigRow(1, "a", null)).omitting(M_DESC).execute();
		assertEquals("fallback", database.from(MIG).select(M_DESC).where(Sql.equalTo(M_ID, 1)).fetchOne());
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void alterColumnDropDefaultRemovesDefaultBehavior(@NonNull Engine engine) throws SqlException {
		assumeTrue(engine.supports(SqlFeature.ALTER_COLUMN), "Altering columns not supported by " + engine.name());
		SqlDatabase database = engine.database();
		resetMigrationState(database);
		runnerWith(database, createMigTable()).migrate();
		
		runnerWith(database, createMigTable(), migration(2, "Set then drop default",
			(builder, schema) -> {
				builder.alterColumn(M_DESC, changes -> changes.setDefault("fallback"));
				builder.alterColumn(M_DESC, SqlMigrationColumnAlter::dropDefault);
			},
			(builder, schema) -> builder.alterColumn(M_DESC, SqlMigrationColumnAlter::dropDefault)
		)).migrate();
		
		database.from(MIG).insert(new MigRow(1, "a", null)).omitting(M_DESC).execute();
		assertNull(database.from(MIG).select(M_DESC).where(Sql.equalTo(M_ID, 1)).fetchOne());
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void createUniqueIndexRejectsDuplicateValues(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetMigrationState(database);
		runnerWith(database, createMigTable()).migrate();
		
		SqlMigrationRunner runner = runnerWith(database, createMigTable(), migration(2, "Unique index",
			(builder, schema) -> builder.createIndex(MIG, "mit_unique_name", index -> index.columns(M_NAME).unique()),
			(builder, schema) -> builder.dropIndex(MIG, "mit_unique_name")
		));
		runner.migrate();
		
		database.from(MIG).insert(new MigRow(1, "same", null)).execute();
		assertThrows(SqlException.class, () -> database.from(MIG).insert(new MigRow(2, "same", null)).execute());
		
		runner.rollback();
		assertDoesNotThrow(() -> database.from(MIG).insert(new MigRow(2, "same", null)).execute());
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void createIndexWithMethodApplies(@NonNull Engine engine) throws SqlException {
		assumeTrue(!"SQLServer".equals(engine.name()), "SQL Server does not accept an explicit index method");
		SqlDatabase database = engine.database();
		resetMigrationState(database);
		runnerWith(database, createMigTable()).migrate();
		
		runnerWith(database, createMigTable(), migration(2, "Index with method",
			(builder, schema) -> builder.createIndex(MIG, "mit_method_idx", index -> index.columns(M_NAME).method(SqlIndexMethod.BTREE)),
			(builder, schema) -> builder.dropIndex(MIG, "mit_method_idx")
		)).migrate();
		
		assertTrue(hasIndex(database, MIG, "mit_method_idx"));
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void createIndexWithWhereConditionApplies(@NonNull Engine engine) throws SqlException {
		assumeTrue("PostgreSQL".equals(engine.name()) || "SQLite".equals(engine.name()), "Partial indexes not supported by " + engine.name());
		SqlDatabase database = engine.database();
		resetMigrationState(database);
		runnerWith(database, createMigTable()).migrate();
		
		runnerWith(database, createMigTable(), migration(2, "Partial index",
			(builder, schema) -> builder.createIndex(MIG, "mit_partial_idx", index -> index.columns(M_NAME).unique().where(Sql.greaterThan(M_ID, 10))),
			(builder, schema) -> builder.dropIndex(MIG, "mit_partial_idx")
		)).migrate();
		
		assertTrue(hasIndex(database, MIG, "mit_partial_idx"));
		database.from(MIG).insert(List.of(new MigRow(1, "same", null), new MigRow(2, "same", null))).execute();
		database.from(MIG).insert(new MigRow(11, "indexed", null)).execute();
		assertThrows(SqlException.class, () -> database.from(MIG).insert(new MigRow(12, "indexed", null)).execute());
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void createIndexOnMultipleColumnsApplies(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetMigrationState(database);
		runnerWith(database, createMigTable()).migrate();
		
		runnerWith(database, createMigTable(), migration(2, "Composite index",
			(builder, schema) -> builder.createIndex(MIG, "mit_multi_idx", index -> index.columns(M_NAME, M_DESC)),
			(builder, schema) -> builder.dropIndex(MIG, "mit_multi_idx")
		)).migrate();
		
		assertTrue(hasIndex(database, MIG, "mit_multi_idx"));
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void renameIndexKeepsIndexUsable(@NonNull Engine engine) throws SqlException {
		assumeTrue(engine.supports(SqlFeature.RENAME_INDEX), "Renaming indexes not supported by " + engine.name());
		SqlDatabase database = engine.database();
		resetMigrationState(database);
		runnerWith(database, createMigTable()).migrate();
		
		runnerWith(database, createMigTable(), migration(2, "Rename index", (builder, schema) -> {
			builder.createIndex(MIG, "mit_old_idx", index -> index.columns(M_NAME));
			builder.renameIndex(MIG, "mit_old_idx", "mit_new_idx");
		}, (builder, schema) -> builder.dropIndex(MIG, "mit_new_idx"))).migrate();
		
		assertFalse(hasIndex(database, MIG, "mit_old_idx"));
		assertTrue(hasIndex(database, MIG, "mit_new_idx"));
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void addUniqueConstraintRejectsDuplicates(@NonNull Engine engine) throws SqlException {
		assumeTrue(engine.supports(SqlFeature.ADD_CONSTRAINT), "Adding constraints not supported by " + engine.name());
		SqlDatabase database = engine.database();
		resetMigrationState(database);
		runnerWith(database, createMigTable()).migrate();
		
		runnerWith(database, createMigTable(), migration(2, "Unique constraint",
			(builder, schema) -> builder.addUniqueConstraint(MIG, "mit_unique_ct", M_NAME),
			(builder, schema) -> builder.dropConstraint(MIG, "mit_unique_ct")
		)).migrate();
		
		database.from(MIG).insert(new MigRow(1, "same", null)).execute();
		assertThrows(SqlException.class, () -> database.from(MIG).insert(new MigRow(2, "same", null)).execute());
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void addForeignKeyEnforcesReferentialIntegrity(@NonNull Engine engine) throws SqlException {
		assumeTrue(engine.supports(SqlFeature.ADD_CONSTRAINT), "Adding constraints not supported by " + engine.name());
		assumeTrue(!"SQLite".equals(engine.name()), "SQLite enforces foreign keys per connection only");
		SqlDatabase database = engine.database();
		resetMigrationState(database);
		
		runnerWith(database, createMigTable(), createExtraTable(2), migration(3, "Foreign key",
			(builder, schema) -> builder.addForeignKey(EXTRA, "mit_extra_fk", new SqlColumn<?, ?>[] { E_REF }, MIG, new SqlColumn<?, ?>[] { M_ID }, SqlReferentialAction.NO_ACTION, SqlReferentialAction.NO_ACTION),
			(builder, schema) -> builder.dropConstraint(EXTRA, "mit_extra_fk")
		)).migrate();
		
		database.from(MIG).insert(new MigRow(1, "parent", null)).execute();
		assertDoesNotThrow(() -> database.from(EXTRA).insert(new Extra(1, 1, 0)).execute());
		assertThrows(SqlException.class, () -> database.from(EXTRA).insert(new Extra(2, 99, 0)).execute());
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void addForeignKeyWithCascadeDeletesChildren(@NonNull Engine engine) throws SqlException {
		assumeTrue(engine.supports(SqlFeature.ADD_CONSTRAINT), "Adding constraints not supported by " + engine.name());
		assumeTrue(!"SQLite".equals(engine.name()), "SQLite enforces foreign keys per connection only");
		SqlDatabase database = engine.database();
		resetMigrationState(database);
		
		runnerWith(database, createMigTable(), createExtraTable(2), migration(3, "Cascading foreign key",
			(builder, schema) -> builder.addForeignKey(EXTRA, "mit_extra_cascade", new SqlColumn<?, ?>[] { E_REF }, MIG, new SqlColumn<?, ?>[] { M_ID }, SqlReferentialAction.CASCADE, SqlReferentialAction.NO_ACTION),
			(builder, schema) -> builder.dropConstraint(EXTRA, "mit_extra_cascade")
		)).migrate();
		
		database.from(MIG).insert(new MigRow(1, "parent", null)).execute();
		database.from(EXTRA).insert(new Extra(1, 1, 0)).execute();
		database.from(MIG).delete().where(Sql.equalTo(M_ID, 1)).execute();
		assertEquals(0L, database.from(EXTRA).select().count());
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void addCheckConstraintRejectsViolatingRow(@NonNull Engine engine) throws SqlException {
		assumeTrue(engine.supports(SqlFeature.ADD_CONSTRAINT), "Adding constraints not supported by " + engine.name());
		SqlDatabase database = engine.database();
		resetMigrationState(database);
		runnerWith(database, createMigTable(), createExtraTable(2)).migrate();
		
		runnerWith(database, createMigTable(), createExtraTable(2), migration(3, "Check constraint",
			(builder, schema) -> builder.addCheckConstraint(EXTRA, "mit_extra_check", Sql.greaterThanOrEqualTo(E_AMOUNT, 0)),
			(builder, schema) -> builder.dropConstraint(EXTRA, "mit_extra_check")
		)).migrate();
		
		assertDoesNotThrow(() -> database.from(EXTRA).insert(new Extra(1, null, 10)).execute());
		assertThrows(SqlException.class, () -> database.from(EXTRA).insert(new Extra(2, null, -5)).execute());
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void dropConstraintReleasesEnforcement(@NonNull Engine engine) throws SqlException {
		assumeTrue(engine.supports(SqlFeature.ADD_CONSTRAINT) && engine.supports(SqlFeature.DROP_CONSTRAINT), "Constraint alteration not supported by " + engine.name());
		SqlDatabase database = engine.database();
		resetMigrationState(database);
		runnerWith(database, createMigTable()).migrate();
		
		SqlMigrationRunner runner = runnerWith(database, createMigTable(), migration(2, "Unique constraint",
			(builder, schema) -> builder.addUniqueConstraint(MIG, "mit_droppable", M_NAME),
			(builder, schema) -> builder.dropConstraint(MIG, "mit_droppable")
		));
		runner.migrate();
		database.from(MIG).insert(new MigRow(1, "same", null)).execute();
		assertThrows(SqlException.class, () -> database.from(MIG).insert(new MigRow(2, "same", null)).execute());
		
		runner.rollback();
		assertDoesNotThrow(() -> database.from(MIG).insert(new MigRow(2, "same", null)).execute());
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void enableAuditingCreatesAuditColumnsAndPopulatesOnInsert(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetMigrationState(database);
		
		runnerWith(database, migration(1, "Create audited table", (builder, schema) -> {
			builder.createTable(AUDITED_PLAIN, table -> {
				table.column(AP_ID, SqlTypes.INTEGER, SqlMigrationColumnBuilder::notNull);
				table.column(AP_NAME, SqlTypes.STRING.configure(SqlParameter.length(64)), SqlMigrationColumnBuilder::notNull);
				table.primaryKey(AP_ID);
			});
			builder.enableAuditing(AUDITED);
		}, (builder, schema) -> builder.dropTable(AUDITED_PLAIN))).migrate();
		
		SqlMigrationSchema snapshot = SqlMigrationSchema.load(database);
		assertTrue(snapshot.hasColumn(AUDITED.name(), "version"));
		assertTrue(snapshot.hasColumn(AUDITED.name(), "created_at"));
		
		database.from(AUDITED).insert(new AuditRow(1, "audited")).execute();
		assertEquals(1L, database.from(AUDITED).select().where(Sql.equalTo(AU_ID, 1)).withAudit().fetchOne().version().orElse(-1L));
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void enableAuditingWithCustomConfigUsesConfiguredColumns(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetMigrationState(database);
		
		runnerWith(database, migration(1, "Create custom audited table", (builder, schema) -> {
			builder.createTable(AUDITED_PLAIN, table -> {
				table.column(AP_ID, SqlTypes.INTEGER, SqlMigrationColumnBuilder::notNull);
				table.column(AP_NAME, SqlTypes.STRING.configure(SqlParameter.length(64)), SqlMigrationColumnBuilder::notNull);
				table.primaryKey(AP_ID);
			});
			builder.enableAuditing(AUDITED, CUSTOM_AUDIT);
		}, (builder, schema) -> builder.dropTable(AUDITED_PLAIN))).migrate();
		
		SqlMigrationSchema snapshot = SqlMigrationSchema.load(database);
		assertTrue(snapshot.hasColumn(AUDITED.name(), "rev"));
		assertTrue(snapshot.hasColumn(AUDITED.name(), "born_at"));
		assertFalse(snapshot.hasColumn(AUDITED.name(), "version"));
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void disableAuditingRemovesAuditColumns(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetMigrationState(database);
		
		runnerWith(database, migration(1, "Create audited table", (builder, schema) -> {
			builder.createTable(AUDITED_PLAIN, table -> {
				table.column(AP_ID, SqlTypes.INTEGER, SqlMigrationColumnBuilder::notNull);
				table.column(AP_NAME, SqlTypes.STRING.configure(SqlParameter.length(64)), SqlMigrationColumnBuilder::notNull);
				table.primaryKey(AP_ID);
			});
			builder.enableAuditing(AUDITED);
		}, (builder, schema) -> builder.dropTable(AUDITED_PLAIN)), migration(2, "Disable auditing",
			(builder, schema) -> builder.disableAuditing(AUDITED),
			(builder, schema) -> builder.enableAuditing(AUDITED)
		)).migrate();
		
		SqlMigrationSchema snapshot = SqlMigrationSchema.load(database);
		assertFalse(snapshot.hasColumn(AUDITED.name(), "version"));
		assertFalse(snapshot.hasColumn(AUDITED.name(), "created_at"));
		assertTrue(snapshot.hasColumn(AUDITED.name(), "id"));
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void dataMigrationTransformsExistingRows(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetMigrationState(database);
		runnerWith(database, createMigTable()).migrate();
		seedMigRows(database);
		
		runnerWith(database, createMigTable(), migration(2, "Transform data",
			(builder, schema) -> builder.data(MIG, provider -> provider.update().set(M_DESC, "migrated").allowAll().execute()),
			(builder, schema) -> builder.data(MIG, provider -> provider.update().set(M_DESC, "reverted").allowAll().execute())
		)).migrate();
		
		List<String> descriptions = database.from(MIG).select(M_DESC).fetch();
		assertEquals(3, descriptions.size());
		assertTrue(descriptions.stream().allMatch("migrated"::equals));
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void dryRunSkipsDataAction(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetMigrationState(database);
		runnerWith(database, createMigTable()).migrate();
		seedMigRows(database);
		
		SqlMigrationRunner runner = runnerWith(database, createMigTable(), migration(2, "Data action",
			(builder, schema) -> builder.data(MIG, provider -> provider.delete().allowAll().execute()),
			(builder, schema) -> builder.data(MIG, provider -> {})
		));
		
		assertFalse(runner.dryRun().isEmpty());
		assertEquals(3L, database.from(MIG).select().count());
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void migrateToIntermediateVersionLeavesLaterMigrationsPending(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetMigrationState(database);
		
		SqlMigrationRunner runner = runnerWith(database, createMigTable(), createExtraTable(2), migration(3, "Add column",
			(builder, schema) -> builder.addColumn(MX_EXTRA, SqlTypes.STRING.configure(SqlParameter.length(32))),
			(builder, schema) -> builder.dropColumn(MX_EXTRA)
		));
		runner.migrateTo(Version.of(0, 2));
		
		assertEquals(2L, appliedCount(runner));
		assertEquals(1L, runner.status().stream().filter(info -> info.status() == SqlMigrationStatus.PENDING).count());
		assertThrows(SqlException.class, () -> database.from(MIG).select(MX_EXTRA).fetch());
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void migrateToVersionAboveAllRegisteredAppliesEverything(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetMigrationState(database);
		
		SqlMigrationRunner runner = runnerWith(database, createMigTable(), createExtraTable(2));
		runner.migrateTo(Version.of(1, 0));
		
		assertEquals(2L, appliedCount(runner));
		assertEquals(0L, runner.status().stream().filter(info -> info.status() == SqlMigrationStatus.PENDING).count());
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void migrateToVersionBelowAppliedIsNoOp(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetMigrationState(database);
		
		SqlMigrationRunner runner = runnerWith(database, createMigTable(), createExtraTable(2));
		runner.migrate();
		assertDoesNotThrow(() -> runner.migrateTo(Version.of(0, 1)));
		
		assertEquals(2L, appliedCount(runner));
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void migrateAppliesInVersionOrderRegardlessOfRegistrationOrder(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetMigrationState(database);
		
		SqlMigrationRunner runner = SqlMigrationRunner.of(database);
		runner.register(List.of(createExtraTable(3), createMigTable(), migration(2, "Add column",
			(builder, schema) -> builder.addColumn(MX_EXTRA, SqlTypes.STRING.configure(SqlParameter.length(32))),
			(builder, schema) -> builder.dropColumn(MX_EXTRA)
		)));
		
		assertDoesNotThrow(runner::migrate);
		assertEquals(List.of(Version.of(0, 1), Version.of(0, 2), Version.of(0, 3)), runner.status().stream().map(SqlMigrationInfo::version).toList());
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void rollbackAfterAddColumnRemovesColumn(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetMigrationState(database);
		
		SqlMigrationRunner runner = runnerWith(database, createMigTable(), migration(2, "Add column",
			(builder, schema) -> builder.addColumn(MX_EXTRA, SqlTypes.STRING.configure(SqlParameter.length(32))),
			(builder, schema) -> builder.dropColumn(MX_EXTRA)
		));
		runner.migrate();
		assertDoesNotThrow(() -> database.from(MIG).select(MX_EXTRA).fetch());
		
		runner.rollback();
		assertThrows(SqlException.class, () -> database.from(MIG).select(MX_EXTRA).fetch());
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void rollbackAfterRenameTableRestoresOriginalName(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetMigrationState(database);
		
		SqlMigrationRunner runner = runnerWith(database, createMigTable(), migration(2, "Rename",
			(builder, schema) -> builder.renameTable(MIG, MIG_RENAMED),
			(builder, schema) -> builder.renameTable(MIG_RENAMED, MIG)
		));
		runner.migrate();
		assertTrue(database.table(MIG_RENAMED).exists());
		
		runner.rollback();
		assertTrue(database.table(MIG).exists());
		assertFalse(database.table(MIG_RENAMED).exists());
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void rollbackChainAppliesDownInReverseOrder(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetMigrationState(database);
		
		List<Integer> order = new ArrayList<>();
		SqlMigrationRunner runner = runnerWith(database,
			createMigTable(),
			migration(2, "Second", (builder, schema) -> builder.createIndex(MIG, "mit_second_idx", index -> index.columns(M_NAME)), (builder, schema) -> {
				order.add(2);
				builder.dropIndex(MIG, "mit_second_idx");
			}),
			migration(3, "Third", (builder, schema) -> builder.createIndex(MIG, "mit_third_idx", index -> index.columns(M_DESC)), (builder, schema) -> {
				order.add(3);
				builder.dropIndex(MIG, "mit_third_idx");
			})
		);
		runner.migrate();
		runner.rollback(2);
		
		assertEquals(List.of(3, 2), order);
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void rollbackToTargetVersionStopsAtTarget(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetMigrationState(database);
		
		SqlMigrationRunner runner = runnerWith(database, createMigTable(), createExtraTable(2));
		runner.migrate();
		runner.rollbackTo(Version.of(0, 1));
		
		assertEquals(1L, appliedCount(runner));
		assertTrue(database.table(MIG).exists());
		assertFalse(database.table(EXTRA).exists());
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void rollbackToVersionAboveLatestIsNoOp(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetMigrationState(database);
		
		SqlMigrationRunner runner = runnerWith(database, createMigTable(), createExtraTable(2));
		runner.migrate();
		assertDoesNotThrow(() -> runner.rollbackTo(Version.of(9, 0)));
		
		assertEquals(2L, appliedCount(runner));
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void rollbackWithNoAppliedMigrationsIsNoOp(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetMigrationState(database);
		
		SqlMigrationRunner runner = runnerWith(database, createMigTable());
		assertDoesNotThrow(() -> runner.rollback());
		assertEquals(0L, appliedCount(runner));
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void rollbackWithCountGreaterThanAppliedRollsBackAll(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetMigrationState(database);
		
		SqlMigrationRunner runner = runnerWith(database, createMigTable(), createExtraTable(2));
		runner.migrate();
		assertDoesNotThrow(() -> runner.rollback(5));
		
		assertEquals(0L, appliedCount(runner));
		assertFalse(database.table(MIG).exists());
		assertDoesNotThrow(() -> runner.rollback(1));
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void rollbackTwiceDoesNotRunDownTwice(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetMigrationState(database);
		
		AtomicInteger downs = new AtomicInteger(0);
		SqlMigrationRunner runner = runnerWith(database, createMigTable(), migration(2, "Counted",
			(builder, schema) -> builder.createIndex(MIG, "mit_counted_idx", index -> index.columns(M_NAME)),
			(builder, schema) -> {
				downs.incrementAndGet();
				builder.dropIndex(MIG, "mit_counted_idx");
			}
		));
		runner.migrate();
		runner.rollback();
		runner.rollback();
		
		assertEquals(1, downs.get());
		assertEquals(0L, appliedCount(runner));
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void rollbackThenMigrateReappliesSuccessfully(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetMigrationState(database);
		
		SqlMigrationRunner runner = runnerWith(database, createMigTable());
		runner.migrate();
		runner.rollback();
		assertFalse(database.table(MIG).exists());
		
		runner.migrate();
		assertTrue(database.table(MIG).exists());
		assertEquals(1L, appliedCount(runner));
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void statusReflectsMixedPendingAppliedRolledBack(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetMigrationState(database);
		
		runnerWith(database, createMigTable(), createExtraTable(2)).migrate();
		runnerWith(database, createMigTable(), createExtraTable(2)).rollback();
		
		SqlMigrationRunner runner = runnerWith(database, createMigTable(), createExtraTable(2), migration(3, "Pending",
			(builder, schema) -> builder.createIndex(MIG, "mit_pending_idx", index -> index.columns(M_NAME)),
			(builder, schema) -> builder.dropIndex(MIG, "mit_pending_idx")
		));
		List<SqlMigrationInfo> status = runner.status();
		
		assertEquals(1L, status.stream().filter(info -> info.status() == SqlMigrationStatus.APPLIED).count());
		assertEquals(1L, status.stream().filter(info -> info.status() == SqlMigrationStatus.ROLLED_BACK).count());
		assertEquals(1L, status.stream().filter(info -> info.status() == SqlMigrationStatus.PENDING).count());
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void validatePassesAfterMigrate(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetMigrationState(database);
		
		SqlMigrationRunner runner = runnerWith(database, createMigTable(), createExtraTable(2));
		runner.migrate();
		
		assertDoesNotThrow(runner::validate);
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void validateWithNothingAppliedPasses(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetMigrationState(database);
		
		SqlMigrationRunner runner = runnerWith(database, createMigTable(), createExtraTable(2));
		assertDoesNotThrow(runner::validate);
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void validateDetectsModifiedMigrationBody(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetMigrationState(database);
		runnerWith(database, createMigTable()).migrate();
		
		SqlMigrationRunner tampered = runnerWith(database, migration(1, "Create item table",
			(builder, schema) -> builder.createIndex(MIG, "mit_tampered_idx", index -> index.columns(M_NAME)),
			(builder, schema) -> builder.dropIndex(MIG, "mit_tampered_idx")
		));
		
		assertThrows(SqlMigrationConflictException.class, tampered::validate);
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void validateIgnoresRolledBackMigrations(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetMigrationState(database);
		runnerWith(database, createMigTable()).migrate();
		runnerWith(database, createMigTable()).rollback();
		
		SqlMigrationRunner tampered = runnerWith(database, migration(1, "Create item table",
			(builder, schema) -> builder.createTable(EXTRA, table -> {
				table.column(E_ID, SqlTypes.INTEGER, SqlMigrationColumnBuilder::notNull);
				table.primaryKey(E_ID);
			}),
			(builder, schema) -> builder.dropTable(EXTRA)
		));
		
		assertDoesNotThrow(tampered::validate);
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void checksumIsStableAcrossRunnerInstances(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetMigrationState(database);
		runnerWith(database, createMigTable()).migrate();
		
		String checksum = runnerWith(database, createMigTable()).status().getFirst().checksum();
		assertNotNull(checksum);
		assertEquals(checksum, runnerWith(database, createMigTable()).status().getFirst().checksum());
		assertDoesNotThrow(() -> runnerWith(database, createMigTable()).validate());
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void dryRunCreatesNoStoreRows(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetMigrationState(database);
		
		SqlMigrationRunner runner = runnerWith(database, createMigTable());
		List<SqlRendered> preview = runner.dryRun();
		
		assertFalse(preview.isEmpty());
		assertFalse(database.table(MIG).exists());
		assertEquals(0L, appliedCount(runner));
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void dryRunRollbackMatchesExecutedRollbackStatements(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetMigrationState(database);
		
		SqlMigrationRunner runner = runnerWith(database, createMigTable());
		runner.migrate();
		List<SqlRendered> preview = runner.dryRunRollback();
		
		assertFalse(preview.isEmpty());
		assertTrue(database.table(MIG).exists());
		assertEquals(1L, appliedCount(runner));
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void migrateWithCustomStoreUsesProvidedStore(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetMigrationState(database);
		
		AtomicInteger saves = new AtomicInteger(0);
		AtomicInteger initializes = new AtomicInteger(0);
		SqlMigrationRunner runner = SqlMigrationRunner.of(database, countingStore(database, initializes, saves));
		runner.register(createMigTable());
		runner.migrate();
		
		assertTrue(initializes.get() > 0);
		assertEquals(1, saves.get());
		assertEquals(SqlMigrationStatus.APPLIED, runnerWith(database, createMigTable()).status().getFirst().status());
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void schemaPassedToUpIsEmptyForFirstMigration(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetMigrationState(database);
		
		AtomicInteger tables = new AtomicInteger(-1);
		runnerWith(database, migration(1, "Inspect empty schema", (builder, schema) -> {
			tables.set(schema.tableNames().size());
			builder.createTable(MIG, table -> {
				table.column(M_ID, SqlTypes.INTEGER, SqlMigrationColumnBuilder::notNull);
				table.column(M_NAME, SqlTypes.STRING.configure(SqlParameter.length(64)), SqlMigrationColumnBuilder::notNull);
				table.primaryKey(M_ID);
			});
		}, (builder, schema) -> builder.dropTable(MIG))).migrate();
		
		assertEquals(0, tables.get());
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void schemaPassedToUpReflectsPreviousMigrationsTable(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetMigrationState(database);
		
		AtomicInteger seen = new AtomicInteger(0);
		runnerWith(database, createMigTable(), migration(2, "Inspect schema", (builder, schema) -> {
			if (schema.hasTable(MIG.name()) && schema.hasColumn(MIG.name(), M_NAME.name())) {
				seen.incrementAndGet();
			}
			builder.createIndex(MIG, "mit_schema_idx", index -> index.columns(M_NAME));
		}, (builder, schema) -> builder.dropIndex(MIG, "mit_schema_idx"))).migrate();
		
		assertEquals(1, seen.get());
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void schemaSnapshotExcludesSystemTables(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetMigrationState(database);
		runnerWith(database, createMigTable()).migrate();
		
		SqlMigrationSchema snapshot = SqlMigrationSchema.load(database);
		assertTrue(snapshot.hasTable(MIG.name()));
		assertTrue(snapshot.tableNames().stream().noneMatch(name -> name.contains("performance_schema") || name.contains("information_schema") || name.startsWith("pg_")));
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void schemaSnapshotCapturesColumnFlagsPerEngine(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetMigrationState(database);
		runnerWith(database, createMigTable()).migrate();
		
		SqlMigrationSchema snapshot = SqlMigrationSchema.load(database);
		assertTrue(snapshot.hasColumn(MIG.name(), M_ID.name()));
		assertTrue(snapshot.hasColumn(MIG.name(), M_NAME.name()));
		assertTrue(snapshot.hasColumn(MIG.name(), M_DESC.name()));
		assertFalse(snapshot.hasColumn(MIG.name(), "missing"));
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void schemaSnapshotRoundTripsThroughStore(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetMigrationState(database);
		runnerWith(database, createMigTable()).migrate();
		
		SqlMigrationSchema live = SqlMigrationSchema.load(database);
		SqlMigrationSchema restored = SqlMigrationSchema.fromSnapshot(live.extractColumnInfos(), live.extractCheckConstraints());
		
		assertEquals(live.tableNames(), restored.tableNames());
		assertTrue(restored.hasColumn(MIG.name(), M_NAME.name()));
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void rollbackDeletesSchemaSnapshotRowsForVersion(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetMigrationState(database);
		
		SqlMigrationRunner runner = runnerWith(database, createMigTable(), createExtraTable(2));
		runner.migrate();
		runner.rollback();
		
		assertEquals(1L, appliedCount(runner));
		assertFalse(database.table(EXTRA).exists());
		assertDoesNotThrow(runner::migrate);
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void migrationChainOnPopulatedTablePreservesAllData(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetMigrationState(database);
		runnerWith(database, createMigTable()).migrate();
		
		List<MigRow> rows = new ArrayList<>();
		for (int i = 1; i <= 100; i++) {
			rows.add(new MigRow(i, "name" + i, "desc" + i));
		}
		database.from(MIG).insert(rows).execute();
		
		runnerWith(database, createMigTable(), migration(2, "Add column",
			(builder, schema) -> builder.addColumn(MX_EXTRA, SqlTypes.STRING.configure(SqlParameter.length(32))),
			(builder, schema) -> builder.dropColumn(MX_EXTRA)
		), migration(4, "Backfill", // A separate migration, because data actions run before the DDL of their own migration
			(builder, schema) -> builder.data(MIG_X, provider -> provider.update().set(MX_EXTRA, "filled").allowAll().execute()),
			(builder, schema) -> builder.data(MIG_X, provider -> provider.update().setNull(MX_EXTRA).allowAll().execute())
		), migration(3, "Index",
			(builder, schema) -> builder.createIndex(MIG, "mit_chain_idx", index -> index.columns(M_NAME)),
			(builder, schema) -> builder.dropIndex(MIG, "mit_chain_idx")
		)).migrate();
		
		assertEquals(100L, database.from(MIG).select().count());
		assertEquals("desc50", database.from(MIG).select(M_DESC).where(Sql.equalTo(M_ID, 50)).fetchOne());
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void migrationDescriptionWithUnicodeAndQuotesRoundTrips(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetMigrationState(database);
		
		String description = "Grüße: it's a \"test\" – ünïcode";
		SqlMigrationRunner runner = runnerWith(database, migration(1, description,
			(builder, schema) -> builder.createTable(MIG, table -> {
				table.column(M_ID, SqlTypes.INTEGER, SqlMigrationColumnBuilder::notNull);
				table.column(M_NAME, SqlTypes.STRING.configure(SqlParameter.length(64)), SqlMigrationColumnBuilder::notNull);
				table.primaryKey(M_ID);
			}),
			(builder, schema) -> builder.dropTable(MIG)
		));
		runner.migrate();
		
		assertEquals(description, runner.status().getFirst().description());
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void appliedAtInstantRoundTripsWithoutTimezoneShift(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetMigrationState(database);
		
		long before = System.currentTimeMillis();
		SqlMigrationRunner runner = runnerWith(database, createMigTable());
		runner.migrate();
		
		SqlMigrationInfo info = runner.status().getFirst();
		assertNotNull(info.appliedAt());
		assertTrue(Math.abs(info.appliedAt().toEpochMilli() - before) < 300_000L, "Applied-at drifted by more than five minutes on " + engine.name());
	}
	
	private record MigRow(int id, @NonNull String name, String description) {}
	
	private record Extra(int id, Integer ref, Integer amount) {}
	
	private record AuditRow(int id, @NonNull String name) {}
	
	private record MigExtra(int id, @NonNull String name, String description, String extra) {}
	
	private record MigCounter(int id, @NonNull String name, String description, Integer counter) {}
}
