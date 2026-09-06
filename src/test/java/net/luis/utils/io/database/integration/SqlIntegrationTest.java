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

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.luis.utils.io.database.*;
import net.luis.utils.io.database.audit.*;
import net.luis.utils.io.database.condition.SqlCondition;
import net.luis.utils.io.database.dialect.*;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.exception.client.SqlOptimisticLockException;
import net.luis.utils.io.database.exception.client.SqlUntrackedEntityException;
import net.luis.utils.io.database.exception.client.transaction.SqlTransactionPropagationException;
import net.luis.utils.io.database.expression.SqlExpression;
import net.luis.utils.io.database.function.window.SqlWindowClause;
import net.luis.utils.io.database.index.SqlIndex;
import net.luis.utils.io.database.index.SqlIndexMethod;
import net.luis.utils.io.database.migration.*;
import net.luis.utils.io.database.query.SqlAlias;
import net.luis.utils.io.database.query.SqlCommonTableExpression;
import net.luis.utils.io.database.query.crud.SqlInsertQuery;
import net.luis.utils.io.database.query.crud.SqlSelectQuery;
import net.luis.utils.io.database.query.row.SqlRow2;
import net.luis.utils.io.database.query.util.*;
import net.luis.utils.io.database.rendering.SqlRendered;
import net.luis.utils.io.database.table.*;
import net.luis.utils.io.database.transaction.*;
import net.luis.utils.io.database.type.SqlType;
import net.luis.utils.io.database.type.SqlTypes;
import net.luis.utils.io.database.type.parameter.SqlParameter;
import net.luis.utils.util.Version;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

/**
 * Integration tests for the database system.<br>
 *
 * @author Luis-St
 */
class SqlIntegrationTest {
	
	private static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine");
	private static final MySQLContainer<?> MYSQL = new MySQLContainer<>("mysql:8.4");
	private static final MariaDBContainer<?> MARIADB = new MariaDBContainer<>("mariadb:11.4");
	private static final MSSQLServerContainer<?> MSSQL = new MSSQLServerContainer<>("mcr.microsoft.com/mssql/server:2022-latest").acceptLicense();
	
	private static final List<JdbcDatabaseContainer<?>> CONTAINERS = List.of(POSTGRES, MYSQL, MARIADB, MSSQL);
	private static final List<Engine> ENGINES = new ArrayList<>();
	private static final SqlTable<Person> PERSON = SqlTable.create(Person.class, "it_person");
	private static final SqlColumn<Person, Integer> P_ID = PERSON.column("id", SqlTypes.INTEGER, Person::id, col -> col.primaryKey().notNull());
	private static final SqlColumn<Person, String> P_NAME = PERSON.column("name", SqlTypes.STRING.configure(SqlParameter.length(64)), Person::name, SqlColumnBuilder::notNull);
	private static final SqlColumn<Person, String> P_EMAIL = PERSON.column("email", SqlTypes.STRING.configure(SqlParameter.length(320)), Person::email, col -> col.notNull().unique());
	private static final SqlColumn<Person, Integer> P_AGE = PERSON.column("age", SqlTypes.INTEGER, Person::age, SqlColumnBuilder::notNull);
	private static final SqlColumn<Person, Boolean> P_ACTIVE = PERSON.column("active", SqlTypes.BOOLEAN, Person::active, col -> col.notNull().defaultValue(true));
	private static final SqlTable<Role> ROLE = SqlTable.create(Role.class, "it_role");
	private static final SqlColumn<Role, Integer> R_ID = ROLE.column("id", SqlTypes.INTEGER, Role::id, col -> col.primaryKey().notNull());
	private static final SqlColumn<Role, String> R_NAME = ROLE.column("name", SqlTypes.STRING.configure(SqlParameter.length(64)), Role::name, SqlColumnBuilder::notNull);
	private static final SqlTable<PersonRole> PERSON_ROLE = SqlTable.create(PersonRole.class, "it_person_role");
	private static final SqlColumn<PersonRole, Integer> PR_PERSON = PERSON_ROLE.column("person_id", SqlTypes.INTEGER, PersonRole::personId, col -> col.primaryKey().notNull());
	private static final SqlColumn<PersonRole, Integer> PR_ROLE = PERSON_ROLE.column("role_id", SqlTypes.INTEGER, PersonRole::roleId, col -> col.primaryKey().notNull());
	private static final SqlCompositePrimaryKey<PersonRole> PR_PK = PERSON_ROLE.compositePrimaryKey(PR_PERSON, PR_ROLE);
	private static final SqlTable<MigItem> MIG = SqlTable.create(MigItem.class, "it_mig_item");
	private static final SqlColumn<MigItem, Integer> M_ID = MIG.column("id", SqlTypes.INTEGER, MigItem::id, col -> col.primaryKey().notNull());
	private static final SqlColumn<MigItem, String> M_NAME = MIG.column("name", SqlTypes.STRING.configure(SqlParameter.length(64)), MigItem::name, SqlColumnBuilder::notNull);
	private static final SqlColumn<MigItem, String> M_DESC = MIG.column("description", SqlTypes.STRING.configure(SqlParameter.length(255)), MigItem::description);
	private static final SqlTable<Parent> PARENT = SqlTable.create(Parent.class, "it_fk_parent");
	private static final SqlColumn<Parent, Integer> FP_ID = PARENT.column("id", SqlTypes.INTEGER, Parent::id, col -> col.primaryKey().notNull());
	private static final SqlColumn<Parent, String> FP_NAME = PARENT.column("name", SqlTypes.STRING.configure(SqlParameter.length(64)), Parent::name, SqlColumnBuilder::notNull);
	private static final SqlTable<Child> CHILD = SqlTable.create(Child.class, "it_fk_child");
	private static final SqlColumn<Child, Integer> FC_ID = CHILD.column("id", SqlTypes.INTEGER, Child::id, col -> col.primaryKey().notNull());
	private static final SqlColumn<Child, Integer> FC_PARENT = CHILD.column("parent_id", SqlTypes.INTEGER, Child::parentId, SqlColumnBuilder::notNull);
	private static final SqlTableForeignKey<Child, Parent> FC_FK = CHILD.foreignKey(List.of(FC_PARENT), PARENT, List.of(FP_ID), SqlReferentialAction.NO_ACTION, SqlReferentialAction.CASCADE);
	private static final SqlColumn<Child, String> FC_LABEL = CHILD.column("label", SqlTypes.STRING.configure(SqlParameter.length(64)), Child::label, SqlColumnBuilder::notNull);
	private static final SqlTable<TypeRow> TYPES = SqlTable.create(TypeRow.class, "it_types");
	private static final SqlColumn<TypeRow, Integer> T_ID = TYPES.column("id", SqlTypes.INTEGER, TypeRow::id, col -> col.primaryKey().notNull());
	private static final SqlColumn<TypeRow, Long> T_BIG = TYPES.column("big", SqlTypes.LONG, TypeRow::big, SqlColumnBuilder::notNull);
	private static final SqlColumn<TypeRow, Double> T_RATIO = TYPES.column("ratio", SqlTypes.DOUBLE, TypeRow::ratio, SqlColumnBuilder::notNull);
	private static final SqlColumn<TypeRow, BigDecimal> T_AMOUNT = TYPES.column("amount", SqlTypes.DECIMAL.configure(SqlParameter.precision(10, 2)), TypeRow::amount, SqlColumnBuilder::notNull);
	private static final SqlColumn<TypeRow, LocalDate> T_DATE = TYPES.column("at", SqlTypes.LOCAL_DATE, TypeRow::date, SqlColumnBuilder::notNull);
	private static final SqlTable<UuidRow> UUID_ROW = SqlTable.create(UuidRow.class, "it_uuid_row");
	private static final SqlColumn<UuidRow, UUID> U_ID = UUID_ROW.column("id", SqlTypes.UUID, UuidRow::id, col -> col.primaryKey().notNull());
	private static final SqlColumn<UuidRow, String> U_LABEL = UUID_ROW.column("label", SqlTypes.STRING.configure(SqlParameter.length(64)), UuidRow::label, SqlColumnBuilder::notNull);
	private static final SqlTable<BinaryRow> BINARY = SqlTable.create(BinaryRow.class, "it_binary_row");
	private static final SqlColumn<BinaryRow, Integer> B_ID = BINARY.column("id", SqlTypes.INTEGER, BinaryRow::id, col -> col.primaryKey().notNull());
	private static final SqlColumn<BinaryRow, byte[]> B_FIXED = BINARY.column("fixed", SqlTypes.FIXED_BYTES.configure(SqlParameter.length(16)), BinaryRow::fixed, SqlColumnBuilder::notNull);
	private static final SqlColumn<BinaryRow, byte[]> B_DATA = BINARY.column("data", SqlTypes.BYTES.configure(SqlParameter.length(64)), BinaryRow::data, SqlColumnBuilder::notNull);
	private static final SqlColumn<BinaryRow, byte[]> B_PAYLOAD = BINARY.column("payload", SqlTypes.LARGE_BYTES, BinaryRow::payload, SqlColumnBuilder::notNull);
	private static final SqlAuditConfig CUSTOM_AUDIT = SqlAuditConfig.builder()
		.versionColumn("rev")
		.createdAtColumn("born_at")
		.createdByColumn("born_by")
		.updatedAtColumn("touched_at")
		.updatedByColumn("touched_by")
		.build();
	private static final SqlTable<AuditRow> AUDITED = SqlTable.audited(AuditRow.class, "it_audited");
	private static final SqlColumn<AuditRow, Integer> A_ID = AUDITED.column("id", SqlTypes.INTEGER, AuditRow::id, col -> col.primaryKey().notNull());
	private static final SqlColumn<AuditRow, String> A_LABEL = AUDITED.column("label", SqlTypes.STRING.configure(SqlParameter.length(64)), AuditRow::label, SqlColumnBuilder::notNull);
	private static final SqlTable<AuditRow> AUDITED_CUSTOM = SqlTable.audited(AuditRow.class, "it_audited_custom", CUSTOM_AUDIT);
	private static final SqlColumn<AuditRow, Integer> AC_ID = AUDITED_CUSTOM.column("id", SqlTypes.INTEGER, AuditRow::id, col -> col.primaryKey().notNull());
	private static final SqlColumn<AuditRow, String> AC_LABEL = AUDITED_CUSTOM.column("label", SqlTypes.STRING.configure(SqlParameter.length(64)), AuditRow::label, SqlColumnBuilder::notNull);
	private static final SqlTable<IdName> CTE_ACTIVE = SqlTable.create(IdName.class, "it_cte_active");
	private static final SqlColumn<IdName, Integer> CTE_ID = CTE_ACTIVE.column("id", SqlTypes.INTEGER, IdName::id, SqlColumnBuilder::notNull);
	private static final SqlColumn<IdName, String> CTE_NAME = CTE_ACTIVE.column("name", SqlTypes.STRING.configure(SqlParameter.length(64)), IdName::name, SqlColumnBuilder::notNull);
	private static final SqlTable<Node> NODE = SqlTable.create(Node.class, "it_node");
	private static final SqlColumn<Node, Integer> N_ID = NODE.column("id", SqlTypes.INTEGER, Node::id, col -> col.primaryKey().notNull());
	private static final SqlColumn<Node, Integer> N_PARENT = NODE.column("parent_id", SqlTypes.INTEGER, Node::parentId);
	private static final SqlColumn<Node, String> N_NAME = NODE.column("name", SqlTypes.STRING.configure(SqlParameter.length(64)), Node::name, SqlColumnBuilder::notNull);
	private static final SqlTable<IdName> TREE = SqlTable.create(IdName.class, "it_tree");
	private static final SqlColumn<IdName, Integer> TREE_ID = TREE.column("id", SqlTypes.INTEGER, IdName::id, SqlColumnBuilder::notNull);
	private static final SqlColumn<IdName, String> TREE_NAME = TREE.column("name", SqlTypes.STRING.configure(SqlParameter.length(64)), IdName::name, SqlColumnBuilder::notNull);
	private static final SqlTable<TemporalRow> TEMPORAL = SqlTable.create(TemporalRow.class, "it_temporal");
	private static final SqlColumn<TemporalRow, Integer> TP_ID = TEMPORAL.column("id", SqlTypes.INTEGER, TemporalRow::id, col -> col.primaryKey().notNull());
	private static final SqlColumn<TemporalRow, LocalDateTime> TP_AT = TEMPORAL.column("at", SqlTypes.LOCAL_DATE_TIME.configure(SqlParameter.fractional(0)), TemporalRow::at, SqlColumnBuilder::notNull);
	private static final SqlColumn<TemporalRow, Instant> TP_INSTANT = TEMPORAL.column("instant", SqlTypes.INSTANT.configure(SqlParameter.fractional(0)), TemporalRow::instant, SqlColumnBuilder::notNull);
	private static final SqlColumn<TemporalRow, LocalTime> TP_TIME = TEMPORAL.column("time", SqlTypes.LOCAL_TIME.configure(SqlParameter.fractional(0)), TemporalRow::time, SqlColumnBuilder::notNull);
	private static final SqlType<Code> CODE_TYPE = SqlTypes.STRING.configure(SqlParameter.length(16)).map(Code.class, code -> code == null ? null : code.value(), Code::new);
	private static final SqlTable<CodeRow> CODES = SqlTable.create(CodeRow.class, "it_code_row");
	private static final SqlColumn<CodeRow, Integer> C_ID = CODES.column("id", SqlTypes.INTEGER, CodeRow::id, col -> col.primaryKey().notNull());
	private static final SqlColumn<CodeRow, Code> C_CODE = CODES.column("code", CODE_TYPE, CodeRow::code, SqlColumnBuilder::notNull);
	private static final SqlTable<ArrayRow> ARRAYS = SqlTable.create(ArrayRow.class, "it_array_row");
	private static final SqlColumn<ArrayRow, Integer> AR_ID = ARRAYS.column("id", SqlTypes.INTEGER, ArrayRow::id, col -> col.primaryKey().notNull());
	private static final SqlColumn<ArrayRow, String[]> AR_TAGS = ARRAYS.column("tags", SqlTypes.STRING.configure(SqlParameter.length(32)).array(), ArrayRow::tags, SqlColumnBuilder::notNull);
	private static Path sqliteFile;
	
	@BeforeAll
	static void startEngines() throws IOException {
		assumeTrue(DockerClientFactory.instance().isDockerAvailable(), "Docker is required for the container database integration tests");
		
		Logger.getLogger("com.microsoft.sqlserver.jdbc").setLevel(Level.SEVERE);
		
		CONTAINERS.parallelStream().forEach(JdbcDatabaseContainer::start);
		
		ENGINES.add(new Engine("PostgreSQL", fromContainer(POSTGRES, SqlDialects.POSTGRESQL)));
		ENGINES.add(new Engine("MySQL", fromContainer(MYSQL, SqlDialects.MYSQL)));
		ENGINES.add(new Engine("MariaDB", fromContainer(MARIADB, SqlDialects.MARIA_DB)));
		ENGINES.add(new Engine("SQLServer", fromContainer(MSSQL, SqlDialects.SQL_SERVER)));
		
		ENGINES.add(new Engine("H2", fromUrl("jdbc:h2:mem:lutils_it;DB_CLOSE_DELAY=-1", "sa", "", SqlDialects.H2, 4)));
		
		sqliteFile = Files.createTempFile("lutils_it", ".db");
		ENGINES.add(new Engine("SQLite", fromUrl("jdbc:sqlite:" + sqliteFile, "", "", SqlDialects.SQLITE, 1)));
	}
	
	@AfterAll
	static void stopEngines() {
		for (Engine engine : ENGINES) {
			try {
				engine.database().close();
			} catch (SqlException ignored) {}
		}
		
		ENGINES.clear();
		CONTAINERS.forEach(JdbcDatabaseContainer::stop);
		
		if (sqliteFile != null) {
			try {
				Files.deleteIfExists(sqliteFile);
			} catch (IOException ignored) {}
		}
	}
	
	private static @NonNull Stream<Engine> engines() {
		return ENGINES.stream();
	}
	
	private static @NonNull SqlDatabase fromContainer(@NonNull JdbcDatabaseContainer<?> container, @NonNull SqlDialect dialect) {
		return fromUrl(container.getJdbcUrl(), container.getUsername(), container.getPassword(), dialect, 4);
	}
	
	private static @NonNull SqlDatabase fromUrl(@NonNull String jdbcUrl, @NonNull String username, @NonNull String password, @NonNull SqlDialect dialect, int poolSize) {
		HikariConfig config = new HikariConfig();
		config.setJdbcUrl(jdbcUrl);
		if (!username.isEmpty()) {
			config.setUsername(username);
		}
		if (!password.isEmpty()) {
			config.setPassword(password);
		}
		
		config.setMaximumPoolSize(poolSize);
		if (poolSize == 1) {
			config.setMinimumIdle(1);
		}
		
		try {
			return SqlDatabase.builder(new HikariDataSource(config), dialect).build();
		} catch (SqlException e) {
			throw new RuntimeException("Failed to build database for " + dialect, e);
		}
	}
	
	@SuppressWarnings("unchecked")
	private static <E, V> @NonNull SqlSetClause<E, V> excludedValueOf(@NonNull SqlColumn<E, V> column, @NonNull SqlDialect dialect) throws SqlException {
		return new SqlSetClause<>(column, (SqlExpression<V>) dialect.upsertExcludedValue(column), SqlSetType.EXPRESSION);
	}
	
	private static void resetSchema(@NonNull SqlDatabase database) throws SqlException {
		database.table(PERSON_ROLE).dropIfExists();
		database.table(PERSON).dropIfExists();
		database.table(ROLE).dropIfExists();
		database.table(PERSON).create();
		database.table(ROLE).create();
		database.table(PERSON_ROLE).create();
	}
	
	private static void seedPersons(@NonNull SqlDatabase database) throws SqlException {
		database.from(PERSON).insert(List.of(
			new Person(1, "Alice", "alice@example.com", 30, true),
			new Person(2, "Bob", "bob@example.com", 25, true),
			new Person(3, "Charlie", "charlie@example.com", 40, false),
			new Person(4, "Dave", "dave@example.com", 35, true)
		)).execute();
	}
	
	private static void seedRoles(@NonNull SqlDatabase database) throws SqlException {
		database.from(ROLE).insert(List.of(new Role(10, "Admin"), new Role(20, "User"))).execute();
		database.from(PERSON_ROLE).insert(List.of(
			new PersonRole(1, 10),
			new PersonRole(1, 20),
			new PersonRole(2, 20)
		)).execute();
	}
	
	private static void resetMigrationState(@NonNull SqlDatabase database) throws SqlException {
		resetMigrationState(database, MIG);
	}
	
	private static void resetMigrationState(@NonNull SqlDatabase database, @NonNull SqlTable<?> table) throws SqlException {
		database.table(table).dropIfExists();
		database.table(SqlTable.create(Void.class, "_sql_migrations")).dropIfExists();
		database.table(SqlTable.create(Void.class, "_sql_schema_columns")).dropIfExists();
		database.table(SqlTable.create(Void.class, "_sql_schema_check_constraints")).dropIfExists();
	}
	
	private static @NonNull SqlMigration createTableMigration() {
		return new SqlMigration() {
			
			@Override
			public boolean allowsNonAtomicExecution() {
				return true; // MySQL/MariaDB implicitly commit on DDL, so atomic execution is not possible
			}
			
			@Override
			public @NonNull Version version() {
				return Version.of(0, 1);
			}
			
			@Override
			public @NonNull String description() {
				return "Create mig_item table";
			}
			
			@Override
			public void up(@NonNull SqlMigrationBuilder builder, @NonNull SqlMigrationSchema schema) {
				builder.createTable(MIG, table -> {
					table.column(M_ID, SqlTypes.INTEGER, SqlMigrationColumnBuilder::notNull);
					table.column(M_NAME, SqlTypes.STRING.configure(SqlParameter.length(64)), SqlMigrationColumnBuilder::notNull);
					table.column(M_DESC, SqlTypes.STRING.configure(SqlParameter.length(255)));
					table.primaryKey(M_ID);
				});
			}
			
			@Override
			public void down(@NonNull SqlMigrationBuilder builder, @NonNull SqlMigrationSchema schema) {
				builder.dropTable(MIG);
			}
		};
	}
	
	private static @NonNull SqlMigration addIndexMigration() {
		return new SqlMigration() {
			
			@Override
			public boolean allowsNonAtomicExecution() {
				return true;
			}
			
			@Override
			public @NonNull Version version() {
				return Version.of(0, 2);
			}
			
			@Override
			public @NonNull String description() {
				return "Add index on mig_item name";
			}
			
			@Override
			public void up(@NonNull SqlMigrationBuilder builder, @NonNull SqlMigrationSchema schema) {
				builder.createIndex(MIG, "idx_it_mig_name", index -> index.columns(M_NAME));
			}
			
			@Override
			public void down(@NonNull SqlMigrationBuilder builder, @NonNull SqlMigrationSchema schema) {
				builder.dropIndex(MIG, "idx_it_mig_name");
			}
		};
	}
	
	private static @NonNull SqlMigration createUuidTableMigration() {
		return new SqlMigration() {
			
			@Override
			public boolean allowsNonAtomicExecution() {
				return true; // MySQL/MariaDB implicitly commit on DDL, so atomic execution is not possible
			}
			
			@Override
			public @NonNull Version version() {
				return Version.of(0, 1);
			}
			
			@Override
			public @NonNull String description() {
				return "Create uuid_row table";
			}
			
			@Override
			public void up(@NonNull SqlMigrationBuilder builder, @NonNull SqlMigrationSchema schema) {
				builder.createTable(UUID_ROW, table -> {
					table.column(U_ID, SqlTypes.UUID, SqlMigrationColumnBuilder::notNull);
					table.column(U_LABEL, SqlTypes.STRING.configure(SqlParameter.length(64)), SqlMigrationColumnBuilder::notNull);
					table.primaryKey(U_ID);
				});
			}
			
			@Override
			public void down(@NonNull SqlMigrationBuilder builder, @NonNull SqlMigrationSchema schema) {
				builder.dropTable(UUID_ROW);
			}
		};
	}
	
	private static @NonNull SqlMigration createBinaryTableMigration() {
		return new SqlMigration() {
			
			@Override
			public boolean allowsNonAtomicExecution() {
				return true;
			}
			
			@Override
			public @NonNull Version version() {
				return Version.of(0, 1);
			}
			
			@Override
			public @NonNull String description() {
				return "Create binary_row table";
			}
			
			@Override
			public void up(@NonNull SqlMigrationBuilder builder, @NonNull SqlMigrationSchema schema) {
				builder.createTable(BINARY, table -> {
					table.column(B_ID, SqlTypes.INTEGER, SqlMigrationColumnBuilder::notNull);
					table.column(B_FIXED, SqlTypes.FIXED_BYTES.configure(SqlParameter.length(16)), SqlMigrationColumnBuilder::notNull);
					table.column(B_DATA, SqlTypes.BYTES.configure(SqlParameter.length(64)), SqlMigrationColumnBuilder::notNull);
					table.column(B_PAYLOAD, SqlTypes.LARGE_BYTES, SqlMigrationColumnBuilder::notNull);
					table.primaryKey(B_ID);
				});
			}
			
			@Override
			public void down(@NonNull SqlMigrationBuilder builder, @NonNull SqlMigrationSchema schema) {
				builder.dropTable(BINARY);
			}
		};
	}
	
	private static @NonNull SqlTransactionListener listener(@NonNull AtomicInteger commits, @NonNull AtomicInteger rollbacks) {
		return new SqlTransactionListener() {
			
			@Override
			public void afterCommit() {
				commits.incrementAndGet();
			}
			
			@Override
			public void afterRollback() {
				rollbacks.incrementAndGet();
			}
		};
	}
	
	private static void resetAuditState(@NonNull SqlDatabase database) throws SqlException {
		database.table(AUDITED).dropIfExists();
		database.table(AUDITED).create();
	}
	
	private static void seedMigItems(@NonNull SqlDatabase database) throws SqlException {
		database.table(MIG).dropIfExists();
		database.table(MIG).create();
		database.from(MIG).insert(List.of(
			new MigItem(1, "first", "alpha"),
			new MigItem(2, "second", null),
			new MigItem(3, "third", "beta")
		)).execute();
	}
	
	private static void seedNodes(@NonNull SqlDatabase database) throws SqlException {
		database.table(NODE).dropIfExists();
		database.table(NODE).create();
		database.from(NODE).insert(List.of(
			new Node(1, null, "root"),
			new Node(2, 1, "child"),
			new Node(3, 2, "grandchild"),
			new Node(4, 1, "sibling")
		)).execute();
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void createAndExistsAndDropTable(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		database.table(PERSON_ROLE).dropIfExists();
		database.table(PERSON).dropIfExists();
		
		assertFalse(database.table(PERSON).exists());
		database.table(PERSON).create();
		
		assertTrue(database.table(PERSON).exists());
		
		database.table(PERSON).drop();
		assertFalse(database.table(PERSON).exists());
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void createIfNotExistsIsIdempotent(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetSchema(database);
		
		assertDoesNotThrow(() -> database.table(PERSON).createIfNotExists());
		assertTrue(database.table(PERSON).exists());
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void truncateEmptiesPopulatedTable(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetSchema(database);
		seedPersons(database);
		
		assertEquals(4L, database.from(PERSON).select().count());
		database.table(PERSON).truncate();
		assertEquals(0L, database.from(PERSON).select().count());
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void compositePrimaryKeyTableIsUsable(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetSchema(database);
		
		database.from(PERSON_ROLE).insert(List.of(new PersonRole(1, 10), new PersonRole(1, 20))).execute();
		assertEquals(2L, database.from(PERSON_ROLE).select().count());
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void insertSingleRowPersists(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetSchema(database);
		
		database.from(PERSON).insert(new Person(1, "Alice", "alice@example.com", 30, true)).execute();
		assertEquals(1L, database.from(PERSON).select().count());
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void insertBatchPersistsAllRows(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetSchema(database);
		seedPersons(database);
		
		assertEquals(4L, database.from(PERSON).select().count());
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void insertReturningKeysReturnsList(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetSchema(database);
		
		List<Long> keys = database.from(PERSON).insert(List.of(
			new Person(1, "Alice", "alice@example.com", 30, true),
			new Person(2, "Bob", "bob@example.com", 25, true)
		)).executeReturningKeys();
		assertNotNull(keys);
		assertEquals(2L, database.from(PERSON).select().count());
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void insertDuplicateUniqueValueThrows(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetSchema(database);
		
		database.from(PERSON).insert(new Person(1, "Alice", "alice@example.com", 30, true)).execute();
		assertThrows(SqlException.class, () -> database.from(PERSON).insert(new Person(2, "Bob", "alice@example.com", 25, true)).execute());
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void selectFullEntityFetch(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetSchema(database);
		seedPersons(database);
		
		List<Person> persons = database.from(PERSON).select().fetch();
		assertEquals(4, persons.size());
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void selectSingleColumnTyped(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetSchema(database);
		seedPersons(database);
		
		List<String> names = database.from(PERSON).select(P_NAME).orderBy(P_ID.ascending()).fetch();
		assertEquals(List.of("Alice", "Bob", "Charlie", "Dave"), names);
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void selectTwoColumnRow(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetSchema(database);
		seedPersons(database);
		
		List<SqlRow2<Integer, String>> rows = database.from(PERSON).select(P_ID, P_NAME)
			.where(Sql.startsWith(P_NAME, "A"))
			.fetch();
		assertEquals(1, rows.size());
		assertEquals(1, rows.getFirst().first());
		assertEquals("Alice", rows.getFirst().second());
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void fetchOneFirstAndOrNull(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetSchema(database);
		seedPersons(database);
		assertEquals("Alice", database.from(PERSON).select(P_NAME).where(Sql.equalTo(P_ID, 1)).fetchOne());
		
		Optional<String> first = database.from(PERSON).select(P_NAME).where(Sql.equalTo(P_ID, 999)).fetchFirst();
		assertTrue(first.isEmpty());
		
		assertNull(database.from(PERSON).select(P_NAME).where(Sql.equalTo(P_ID, 999)).fetchOneOrNull());
		assertNotNull(database.from(PERSON).select(P_NAME).where(Sql.equalTo(P_ID, 1)).fetchOneOrNull());
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void countAndExists(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetSchema(database);
		seedPersons(database);
		
		assertEquals(4L, database.from(PERSON).select().count());
		assertTrue(database.from(PERSON).select().where(Sql.equalTo(P_NAME, "Alice")).exists());
		assertFalse(database.from(PERSON).select().where(Sql.equalTo(P_NAME, "Nobody")).exists());
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void distinctRemovesDuplicates(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetSchema(database);
		seedPersons(database);
		
		List<Boolean> distinct = database.from(PERSON).select(P_ACTIVE).distinct().fetch();
		assertEquals(2, distinct.size());
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void orderByLimitAndOffset(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetSchema(database);
		seedPersons(database);
		
		List<String> descending = database.from(PERSON).select(P_NAME).orderBy(P_AGE.descending()).limit(2).fetch();
		assertEquals(List.of("Charlie", "Dave"), descending);
		
		List<String> offset = database.from(PERSON).select(P_NAME).orderBy(P_AGE.ascending()).offset(1).limit(1).fetch();
		assertEquals(List.of("Alice"), offset);
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void fetchPageReturnsSlice(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetSchema(database);
		seedPersons(database);
		
		var page = database.from(PERSON).select().orderBy(P_ID.ascending()).fetchPage(0, 2);
		assertEquals(2, page.content().size());
		assertTrue(page.hasNext());
		assertFalse(page.hasPrevious());
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void whereComparisonConditions(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetSchema(database);
		seedPersons(database);
		
		assertEquals(2L, database.from(PERSON).select().where(Sql.greaterThan(P_AGE, 30)).count());
		assertEquals(3L, database.from(PERSON).select().where(Sql.between(P_AGE, 25, 35)).count());
		assertEquals(1L, database.from(PERSON).select().where(Sql.lessThan(P_AGE, 30)).count());
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void whereInCondition(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetSchema(database);
		seedPersons(database);
		assertEquals(3L, database.from(PERSON).select().where(Sql.in(P_ID, 1, 2, 3)).count());
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void whereStringConditions(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetSchema(database);
		seedPersons(database);
		
		assertEquals(1L, database.from(PERSON).select().where(Sql.startsWith(P_NAME, "Al")).count());
		assertEquals(1L, database.from(PERSON).select().where(Sql.contains(P_NAME, "har")).count());
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void whereCombinatorsAndNegation(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetSchema(database);
		seedPersons(database);
		
		long activeAdults = database.from(PERSON).select()
			.where(SqlCondition.allOf(Sql.equalTo(P_ACTIVE, true), Sql.greaterThanOrEqualTo(P_AGE, 30)))
			.count();
		assertEquals(2L, activeAdults);
		
		long youngOrInactive = database.from(PERSON).select()
			.where(SqlCondition.anyOf(Sql.lessThan(P_AGE, 26), Sql.equalTo(P_ACTIVE, false)))
			.count();
		assertEquals(2L, youngOrInactive);
		
		long named = database.from(PERSON).select().where(Sql.isNull(P_NAME).not()).count();
		assertEquals(4L, named);
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void aggregateMinMax(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetSchema(database);
		seedPersons(database);
		
		assertEquals(40, database.from(PERSON).select(Sql.max(P_AGE)).fetchOne());
		assertEquals(25, database.from(PERSON).select(Sql.min(P_AGE)).fetchOne());
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void aggregateSum(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetSchema(database);
		seedPersons(database);
		
		assertEquals(130, database.from(PERSON).select(Sql.sum(P_AGE)).fetchOne());
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void groupByAndHaving(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetSchema(database);
		seedPersons(database);
		
		List<SqlRow2<Boolean, Long>> counts = database.from(PERSON).select(P_ACTIVE, Sql.count(P_ID, false))
			.groupBy(P_ACTIVE)
			.having(Sql.greaterThanOrEqualTo(Sql.count(P_ID, false), 2L))
			.fetch();
		assertEquals(1, counts.size());
		assertEquals(Boolean.TRUE, counts.getFirst().first());
		assertEquals(3L, counts.getFirst().second());
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void updateSetReturnsAffectedCount(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetSchema(database);
		seedPersons(database);
		
		int updated = database.from(PERSON).update().set(P_NAME, "Alice Updated").where(Sql.equalTo(P_ID, 1)).execute();
		assertEquals(1, updated);
		assertEquals("Alice Updated", database.from(PERSON).select(P_NAME).where(Sql.equalTo(P_ID, 1)).fetchOne());
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void updateIncrementColumn(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetSchema(database);
		seedPersons(database);
		
		database.from(PERSON).update().increment(P_AGE, 5).where(Sql.equalTo(P_ID, 1)).execute();
		assertEquals(35, database.from(PERSON).select(P_AGE).where(Sql.equalTo(P_ID, 1)).fetchOne());
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void updateReturningRows(@NonNull Engine engine) throws SqlException {
		assumeTrue(engine.supports(SqlFeature.UPDATE_RETURNING), "UPDATE ... RETURNING not supported by " + engine.name());
		SqlDatabase database = engine.database();
		resetSchema(database);
		seedPersons(database);
		
		List<Person> returned = database.from(PERSON).update().set(P_EMAIL, "new@example.com").where(Sql.equalTo(P_ID, 2)).returning();
		assertEquals(1, returned.size());
		assertEquals("new@example.com", returned.getFirst().email());
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void deleteConditional(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetSchema(database);
		seedPersons(database);
		
		int deleted = database.from(PERSON).delete().where(Sql.equalTo(P_ACTIVE, false)).execute();
		assertEquals(1, deleted);
		assertEquals(3L, database.from(PERSON).select().count());
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void deleteAllowAllClearsTable(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetSchema(database);
		seedPersons(database);
		
		database.from(PERSON).delete().allowAll().execute();
		assertEquals(0L, database.from(PERSON).select().count());
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void transactionCommitMakesRowsVisible(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetSchema(database);
		
		try (SqlTransaction transaction = database.beginTransaction()) {
			transaction.from(PERSON).insert(new Person(1, "Alice", "alice@example.com", 30, true)).execute();
			transaction.commit();
		}
		
		assertEquals(1L, database.from(PERSON).select().count());
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void transactionRollbackDiscardsRows(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetSchema(database);
		
		try (SqlTransaction transaction = database.beginTransaction()) {
			transaction.from(PERSON).insert(new Person(1, "Alice", "alice@example.com", 30, true)).execute();
			transaction.rollback();
		}
		
		assertEquals(0L, database.from(PERSON).select().count());
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void savepointRollbackToPartiallyDiscards(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetSchema(database);
		
		try (SqlTransaction transaction = database.beginTransaction()) {
			transaction.from(PERSON).insert(new Person(1, "Alice", "alice@example.com", 30, true)).execute();
			SqlSavepoint savepoint = transaction.savepoint("sp1");
			transaction.from(PERSON).insert(new Person(2, "Bob", "bob@example.com", 25, true)).execute();
			transaction.rollbackTo(savepoint);
			transaction.commit();
		}
		
		assertEquals(1L, database.from(PERSON).select().count());
		assertTrue(database.from(PERSON).select().where(Sql.equalTo(P_ID, 1)).exists());
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void inTransactionCommitsResult(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetSchema(database);
		
		long count = database.inTransaction(transaction -> {
			transaction.from(PERSON).insert(new Person(1, "Alice", "alice@example.com", 30, true)).execute();
			return transaction.from(PERSON).select().count();
		});
		
		assertEquals(1L, count);
		assertEquals(1L, database.from(PERSON).select().count());
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void inTransactionRollsBackOnException(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetSchema(database);
		
		assertThrows(IllegalStateException.class, () -> database.inTransaction(transaction -> {
			transaction.from(PERSON).insert(new Person(1, "Alice", "alice@example.com", 30, true)).execute();
			throw new IllegalStateException("boom");
		}));
		
		assertEquals(0L, database.from(PERSON).select().count());
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void readOnlyTransactionIsReadOnly(@NonNull Engine engine) throws SqlException {
		assumeTrue(!"SQLite".equals(engine.name()), "SQLite JDBC driver cannot change the read-only flag after the connection is established");
		
		SqlDatabase database = engine.database();
		resetSchema(database);
		
		try (SqlTransaction transaction = database.beginReadOnlyTransaction()) {
			assertTrue(transaction.isActive());
			assertTrue(transaction.isReadOnly());
		}
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void forUpdateLocksSelectedRows(@NonNull Engine engine) throws SqlException {
		assumeTrue(engine.supports(SqlFeature.FOR_UPDATE), "FOR UPDATE not supported by " + engine.name());
		
		SqlDatabase database = engine.database();
		resetSchema(database);
		seedPersons(database);
		
		try (SqlTransaction transaction = database.beginTransaction()) {
			List<Person> locked = transaction.from(PERSON).select().where(Sql.equalTo(P_ID, 1)).forUpdate().fetch();
			assertEquals(1, locked.size());
			transaction.commit();
		}
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void forUpdateSkipLockedExecutes(@NonNull Engine engine) throws SqlException {
		assumeTrue(engine.supports(SqlFeature.SKIP_LOCKED), "SKIP LOCKED not supported by " + engine.name());
		
		SqlDatabase database = engine.database();
		resetSchema(database);
		seedPersons(database);
		
		try (SqlTransaction transaction = database.beginTransaction()) {
			List<Person> rows = transaction.from(PERSON).select().forUpdate().skipLocked().limit(2).fetch();
			assertEquals(2, rows.size());
			transaction.commit();
		}
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void migrationChainAppliesValidatesAndRollsBack(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetMigrationState(database);
		
		SqlMigrationRunner runner = SqlMigrationRunner.of(database);
		runner.register(List.of(createTableMigration(), addIndexMigration()));
		
		runner.migrate();
		List<SqlMigrationInfo> applied = runner.status();
		assertTrue(applied.stream().anyMatch(info -> info.status() == SqlMigrationStatus.APPLIED));
		long appliedBefore = applied.stream().filter(info -> info.status() == SqlMigrationStatus.APPLIED).count();
		assertEquals(2L, appliedBefore);
		
		assertDoesNotThrow(runner::validate);
		assertTrue(database.table(MIG).exists());
		
		runner.rollback();
		long appliedAfter = runner.status().stream().filter(info -> info.status() == SqlMigrationStatus.APPLIED).count();
		assertTrue(appliedAfter < appliedBefore, "Rollback should reduce the number of applied migrations");
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void migrationDryRunDoesNotApply(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetMigrationState(database);
		
		SqlMigrationRunner runner = SqlMigrationRunner.of(database);
		runner.register(List.of(createTableMigration()));
		
		List<SqlRendered> preview = runner.dryRun();
		assertFalse(preview.isEmpty());
		assertFalse(database.table(MIG).exists());
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void innerJoinAcrossJunctionTable(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetSchema(database);
		seedPersons(database);
		seedRoles(database);
		
		assertEquals(3L, database.from(PERSON).select(P_ID).innerJoin(PERSON_ROLE, Sql.equalTo(PR_PERSON, P_ID)).count());
		
		List<String> aliceRoles = database.from(PERSON)
			.select(R_NAME)
			.innerJoin(PERSON_ROLE, Sql.equalTo(PR_PERSON, P_ID))
			.innerJoin(ROLE, Sql.equalTo(PR_ROLE, R_ID))
			.where(Sql.equalTo(P_ID, 1))
			.orderBy(R_NAME.ascending())
			.fetch();
		assertEquals(List.of("Admin", "User"), aliceRoles);
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void leftJoinKeepsUnmatchedRows(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetSchema(database);
		seedPersons(database);
		seedRoles(database);
		
		List<Integer> rows = database.from(PERSON)
			.select(P_ID)
			.leftJoin(PERSON_ROLE, Sql.equalTo(PR_PERSON, P_ID))
			.fetch();
		assertEquals(5, rows.size());
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void crossJoinProducesCartesianProduct(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetSchema(database);
		seedPersons(database);
		seedRoles(database);
		
		assertEquals(8L, database.from(PERSON).select(P_ID).crossJoin(ROLE).count());
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void unionRemovesDuplicatesUnionAllKeepsThem(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetSchema(database);
		seedPersons(database);
		
		List<String> union = database.from(PERSON).select(P_NAME).where(Sql.equalTo(P_ACTIVE, true))
			.union(database.from(PERSON).select(P_NAME).where(Sql.greaterThan(P_AGE, 30)))
			.fetch();
		assertEquals(4, union.size());
		
		List<String> unionAll = database.from(PERSON).select(P_NAME).where(Sql.equalTo(P_ACTIVE, true))
			.unionAll(database.from(PERSON).select(P_NAME).where(Sql.greaterThan(P_AGE, 30)))
			.fetch();
		assertEquals(5, unionAll.size());
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void intersectAndExceptCombineResultSets(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetSchema(database);
		seedPersons(database);
		
		List<String> intersect = database.from(PERSON).select(P_NAME).where(Sql.equalTo(P_ACTIVE, true))
			.intersect(database.from(PERSON).select(P_NAME).where(Sql.greaterThan(P_AGE, 30)))
			.fetch();
		assertEquals(List.of("Dave"), intersect);
		
		List<String> except = database.from(PERSON).select(P_NAME).where(Sql.equalTo(P_ACTIVE, true))
			.except(database.from(PERSON).select(P_NAME).where(Sql.greaterThan(P_AGE, 30)))
			.fetch();
		assertEquals(Set.of("Alice", "Bob"), new HashSet<>(except));
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void whereInSubqueryFiltersByRelatedTable(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetSchema(database);
		seedPersons(database);
		seedRoles(database);
		
		SqlSelectQuery<Integer> assigned = database.from(PERSON_ROLE).select(PR_PERSON);
		assertEquals(2L, database.from(PERSON).select(P_ID).where(Sql.in(P_ID, assigned)).count());
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void whereExistsCorrelatedSubquery(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetSchema(database);
		seedPersons(database);
		seedRoles(database);
		
		long withRole = database.from(PERSON).select(P_ID)
			.whereExists(database.from(PERSON_ROLE).select(PR_PERSON).where(Sql.equalTo(PR_PERSON, P_ID)))
			.count();
		assertEquals(2L, withRole);
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void insertOrIgnoreSkipsConflictingRows(@NonNull Engine engine) throws SqlException {
		assumeTrue(engine.supports(SqlFeature.INSERT_OR_IGNORE), "INSERT OR IGNORE not supported by " + engine.name());
		SqlDatabase database = engine.database();
		resetSchema(database);
		
		database.from(PERSON).insert(new Person(1, "Alice", "alice@example.com", 30, true)).execute();
		database.from(PERSON).insert(new Person(1, "Replacement", "replacement@example.com", 99, false), P_ID).execute();
		
		assertEquals(1L, database.from(PERSON).select().count());
		assertEquals("Alice", database.from(PERSON).select(P_NAME).where(Sql.equalTo(P_ID, 1)).fetchOne());
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void upsertInsertsThenUpdatesOnConflict(@NonNull Engine engine) throws SqlException {
		assumeTrue(engine.supports(SqlFeature.UPSERT) || engine.supports(SqlFeature.UPSERT_SUFFIX), "Upsert not supported by " + engine.name());
		SqlDatabase database = engine.database();
		resetSchema(database);
		SqlConnectionSource source = SqlConnectionSource.pooled(database.getDataSource());
		
		SqlInsertQuery.upsert(PERSON, database.getDialect(), source, Duration.ofSeconds(30), resultSet -> null, List.of(new Person(1, "Alice", "alice@example.com", 30, true)), P_ID).execute();
		SqlInsertQuery.upsert(PERSON, database.getDialect(), source, Duration.ofSeconds(30), resultSet -> null, List.of(new Person(1, "Alice Updated", "alice.updated@example.com", 31, true)), P_ID).execute();
		
		assertEquals(1L, database.from(PERSON).select().count());
		assertEquals("Alice Updated", database.from(PERSON).select(P_NAME).where(Sql.equalTo(P_ID, 1)).fetchOne());
		assertEquals(31, database.from(PERSON).select(P_AGE).where(Sql.equalTo(P_ID, 1)).fetchOne());
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void upsertWithCustomUpdateClausesUpdatesOnlyGivenColumns(@NonNull Engine engine) throws SqlException {
		assumeTrue(engine.supports(SqlFeature.UPSERT_SUFFIX), "Custom upsert update clauses require an upsert suffix, not supported by " + engine.name());
		SqlDatabase database = engine.database();
		resetSchema(database);
		SqlDialect dialect = database.getDialect();
		SqlConnectionSource source = SqlConnectionSource.pooled(database.getDataSource());
		
		database.from(PERSON).insert(new Person(1, "Alice", "alice@example.com", 30, true)).execute();
		SqlInsertQuery.upsert(
			PERSON, dialect, source, Duration.ofSeconds(30), resultSet -> null,
			List.of(new Person(1, "Alice Updated", "alice.updated@example.com", 99, false)), List.of(P_ID),
			List.of(excludedValueOf(P_NAME, dialect))
		).execute();
		
		assertEquals(1L, database.from(PERSON).select().count());
		assertEquals("Alice Updated", database.from(PERSON).select(P_NAME).where(Sql.equalTo(P_ID, 1)).fetchOne());
		assertEquals("alice@example.com", database.from(PERSON).select(P_EMAIL).where(Sql.equalTo(P_ID, 1)).fetchOne());
		assertEquals(30, database.from(PERSON).select(P_AGE).where(Sql.equalTo(P_ID, 1)).fetchOne());
		assertEquals(true, database.from(PERSON).select(P_ACTIVE).where(Sql.equalTo(P_ID, 1)).fetchOne());
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void insertOmittingColumnFallsBackToDatabaseDefault(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetSchema(database);
		
		database.from(PERSON).insert(new Person(1, "Alice", "alice@example.com", 30, false)).omitting(P_ACTIVE).execute();
		
		assertEquals(true, database.from(PERSON).select(P_ACTIVE).where(Sql.equalTo(P_ID, 1)).fetchOne());
		assertEquals("Alice", database.from(PERSON).select(P_NAME).where(Sql.equalTo(P_ID, 1)).fetchOne());
		assertEquals("alice@example.com", database.from(PERSON).select(P_EMAIL).where(Sql.equalTo(P_ID, 1)).fetchOne());
		assertEquals(30, database.from(PERSON).select(P_AGE).where(Sql.equalTo(P_ID, 1)).fetchOne());
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void insertOmittingColumnAppliesToEveryRowOfBatch(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetSchema(database);
		
		database.from(PERSON).insert(List.of(
			new Person(1, "Alice", "alice@example.com", 30, false),
			new Person(2, "Bob", "bob@example.com", 25, false),
			new Person(3, "Charlie", "charlie@example.com", 40, false)
		)).omitting(P_ACTIVE).execute();
		
		assertEquals(3L, database.from(PERSON).select().where(Sql.equalTo(P_ACTIVE, true)).count());
		assertEquals("Bob", database.from(PERSON).select(P_NAME).where(Sql.equalTo(P_ID, 2)).fetchOne());
		assertEquals("charlie@example.com", database.from(PERSON).select(P_EMAIL).where(Sql.equalTo(P_ID, 3)).fetchOne());
		assertEquals(40, database.from(PERSON).select(P_AGE).where(Sql.equalTo(P_ID, 3)).fetchOne());
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void insertOverridingColumnUsesExpressionInsteadOfEntityValue(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetSchema(database);
		
		database.from(PERSON).insert(new Person(1, "alice", "alice@example.com", 30, true)).override(P_NAME, Sql.upper(Sql.of("alice"))).execute();
		
		assertEquals("ALICE", database.from(PERSON).select(P_NAME).where(Sql.equalTo(P_ID, 1)).fetchOne());
		assertEquals("alice@example.com", database.from(PERSON).select(P_EMAIL).where(Sql.equalTo(P_ID, 1)).fetchOne());
		assertEquals(30, database.from(PERSON).select(P_AGE).where(Sql.equalTo(P_ID, 1)).fetchOne());
		assertEquals(true, database.from(PERSON).select(P_ACTIVE).where(Sql.equalTo(P_ID, 1)).fetchOne());
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void insertOverridingAndOmittingCombineAcrossBatch(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetSchema(database);
		
		database.from(PERSON).insert(List.of(
			new Person(1, "alice", "alice@example.com", 30, false),
			new Person(2, "bob", "bob@example.com", 25, false)
		)).override(P_NAME, Sql.of("Overridden")).omitting(P_ACTIVE).execute();
		
		assertEquals(2L, database.from(PERSON).select().where(Sql.equalTo(P_NAME, "Overridden")).count());
		assertEquals(2L, database.from(PERSON).select().where(Sql.equalTo(P_ACTIVE, true)).count());
		assertEquals("alice@example.com", database.from(PERSON).select(P_EMAIL).where(Sql.equalTo(P_ID, 1)).fetchOne());
		assertEquals("bob@example.com", database.from(PERSON).select(P_EMAIL).where(Sql.equalTo(P_ID, 2)).fetchOne());
		assertEquals(25, database.from(PERSON).select(P_AGE).where(Sql.equalTo(P_ID, 2)).fetchOne());
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void insertColumnsInsertsPartialRowsWithoutEntity(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetSchema(database);
		SqlConnectionSource source = SqlConnectionSource.pooled(database.getDataSource());
		
		int affected = SqlInsertQuery.columns(PERSON, database.getDialect(), source, Duration.ofSeconds(30), resultSet -> null, P_ID, P_NAME, P_EMAIL, P_AGE)
			.row(1, "Alice", "alice@example.com", 30)
			.row(2, "Bob", "bob@example.com", 25)
			.execute();
		
		assertEquals(2, affected);
		assertEquals(2L, database.from(PERSON).select().count());
		assertEquals("Alice", database.from(PERSON).select(P_NAME).where(Sql.equalTo(P_ID, 1)).fetchOne());
		assertEquals(25, database.from(PERSON).select(P_AGE).where(Sql.equalTo(P_ID, 2)).fetchOne());
		assertEquals(2L, database.from(PERSON).select().where(Sql.equalTo(P_ACTIVE, true)).count());
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void insertColumnsBuilderInsertsUntypedPartialRows(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetSchema(database);
		SqlConnectionSource source = SqlConnectionSource.pooled(database.getDataSource());
		
		int affected = SqlInsertQuery.insertColumns(PERSON, database.getDialect(), source, Duration.ofSeconds(30), resultSet -> null)
			.row(SqlColumnValue.of(P_ID, 1), SqlColumnValue.of(P_NAME, Sql.upper(Sql.of("alice"))), SqlColumnValue.of(P_EMAIL, "alice@example.com"), SqlColumnValue.of(P_AGE, 30))
			.row(SqlColumnValue.of(P_ID, 2), SqlColumnValue.of(P_NAME, Sql.upper(Sql.of("bob"))), SqlColumnValue.of(P_EMAIL, "bob@example.com"), SqlColumnValue.of(P_AGE, 25))
			.execute();
		
		assertEquals(2, affected);
		assertEquals("ALICE", database.from(PERSON).select(P_NAME).where(Sql.equalTo(P_ID, 1)).fetchOne());
		assertEquals("BOB", database.from(PERSON).select(P_NAME).where(Sql.equalTo(P_ID, 2)).fetchOne());
		assertEquals(30, database.from(PERSON).select(P_AGE).where(Sql.equalTo(P_ID, 1)).fetchOne());
		assertEquals(2L, database.from(PERSON).select().where(Sql.equalTo(P_ACTIVE, true)).count());
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void insertReturningRows(@NonNull Engine engine) throws SqlException {
		assumeTrue(engine.supports(SqlFeature.RETURNING), "INSERT ... RETURNING not supported by " + engine.name());
		SqlDatabase database = engine.database();
		resetSchema(database);
		
		List<Person> returned = database.from(PERSON).insert(new Person(1, "Alice", "alice@example.com", 30, true)).returning();
		assertEquals(1, returned.size());
		assertEquals("Alice", returned.getFirst().name());
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void deleteReturningRows(@NonNull Engine engine) throws SqlException {
		assumeTrue(engine.supports(SqlFeature.RETURNING), "DELETE ... RETURNING not supported by " + engine.name());
		SqlDatabase database = engine.database();
		resetSchema(database);
		seedPersons(database);
		
		List<Person> deleted = database.from(PERSON).delete().where(Sql.equalTo(P_ACTIVE, false)).returning();
		assertEquals(1, deleted.size());
		assertEquals("Charlie", deleted.getFirst().name());
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void foreignKeyCascadeDeletesChildren(@NonNull Engine engine) throws SqlException {
		assumeTrue(!"SQLite".equals(engine.name()), "SQLite does not enforce foreign keys unless the pragma is enabled per connection");
		SqlDatabase database = engine.database();
		database.table(CHILD).dropIfExists();
		database.table(PARENT).dropIfExists();
		database.table(PARENT).create();
		database.table(CHILD).create();
		
		database.from(PARENT).insert(new Parent(1, "Root")).execute();
		database.from(CHILD).insert(new Child(1, 1, "Leaf")).execute();
		assertEquals(1L, database.from(CHILD).select().count());
		
		database.from(PARENT).delete().where(Sql.equalTo(FP_ID, 1)).execute();
		assertEquals(0L, database.from(CHILD).select().count());
		
		database.table(CHILD).dropIfExists();
		database.table(PARENT).dropIfExists();
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void foreignKeyViolationOnOrphanInsertThrows(@NonNull Engine engine) throws SqlException {
		assumeTrue(!"SQLite".equals(engine.name()), "SQLite does not enforce foreign keys unless the pragma is enabled per connection");
		SqlDatabase database = engine.database();
		database.table(CHILD).dropIfExists();
		database.table(PARENT).dropIfExists();
		database.table(PARENT).create();
		database.table(CHILD).create();
		
		assertThrows(SqlException.class, () -> database.from(CHILD).insert(new Child(1, 999, "Orphan")).execute());
		
		database.table(CHILD).dropIfExists();
		database.table(PARENT).dropIfExists();
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void scalarExpressionsInProjection(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetSchema(database);
		seedPersons(database);
		
		assertEquals("ALICE", database.from(PERSON).select(Sql.upper(P_NAME)).where(Sql.equalTo(P_ID, 1)).fetchOne());
		assertEquals(5, database.from(PERSON).select(Sql.length(P_NAME)).where(Sql.equalTo(P_ID, 1)).fetchOne());
		assertEquals(40, database.from(PERSON).select(Sql.add(P_AGE, 10)).where(Sql.equalTo(P_ID, 1)).fetchOne());
		assertEquals("inactive", database.from(PERSON).select(Sql.caseWhen(Sql.equalTo(P_ACTIVE, true), "active", "inactive")).where(Sql.equalTo(P_ID, 3)).fetchOne());
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void countDistinctCountsUniqueValues(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetSchema(database);
		seedPersons(database);
		
		assertEquals(2L, database.from(PERSON).select(Sql.count(P_ACTIVE, true)).fetchOne());
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void nullableColumnRoundTripsAndCoalesces(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		database.table(MIG).dropIfExists();
		database.table(MIG).create();
		
		database.from(MIG).insert(List.of(new MigItem(1, "a", null), new MigItem(2, "b", "desc"))).execute();
		
		assertNull(database.from(MIG).select(M_DESC).where(Sql.equalTo(M_ID, 1)).fetchOne());
		assertEquals(1L, database.from(MIG).select().where(Sql.isNull(M_DESC)).count());
		assertEquals("none", database.from(MIG).select(Sql.coalesce(M_DESC, Sql.of("none"))).where(Sql.equalTo(M_ID, 1)).fetchOne());
		
		database.from(MIG).update().setNull(M_DESC).where(Sql.equalTo(M_ID, 2)).execute();
		assertEquals(2L, database.from(MIG).select().where(Sql.isNull(M_DESC)).count());
		
		database.table(MIG).dropIfExists();
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void forShareLocksSelectedRows(@NonNull Engine engine) throws SqlException {
		assumeTrue(engine.supports(SqlFeature.FOR_SHARE), "FOR SHARE not supported by " + engine.name());
		SqlDatabase database = engine.database();
		resetSchema(database);
		seedPersons(database);
		
		try (SqlTransaction transaction = database.beginTransaction()) {
			List<Person> shared = transaction.from(PERSON).select().where(Sql.equalTo(P_ID, 1)).forShare().fetch();
			assertEquals(1, shared.size());
			transaction.commit();
		}
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void forUpdateNoWaitExecutes(@NonNull Engine engine) throws SqlException {
		assumeTrue(engine.supports(SqlFeature.NO_WAIT), "NO WAIT not supported by " + engine.name());
		SqlDatabase database = engine.database();
		resetSchema(database);
		seedPersons(database);
		
		try (SqlTransaction transaction = database.beginTransaction()) {
			List<Person> rows = transaction.from(PERSON).select().where(Sql.equalTo(P_ID, 1)).forUpdate().noWait().fetch();
			assertEquals(1, rows.size());
			transaction.commit();
		}
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void windowFunctionAssignsRowNumbers(@NonNull Engine engine) throws SqlException {
		assumeTrue(engine.supports(SqlFeature.WINDOW_FUNCTIONS), "Window functions not supported by " + engine.name());
		SqlDatabase database = engine.database();
		resetSchema(database);
		seedPersons(database);
		
		List<SqlRow2<String, Long>> ranked = database.from(PERSON)
			.select(P_NAME, Sql.rowNumber(SqlWindowClause.partitionBy(P_ACTIVE).orderBy(P_AGE.ascending())))
			.fetch();
		assertEquals(4, ranked.size());
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void typeRoundTripPreservesValues(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		database.table(TYPES).dropIfExists();
		database.table(TYPES).create();
		
		TypeRow original = new TypeRow(1, 9_000_000_000L, 3.5, new BigDecimal("12.34"), LocalDate.of(2024, 1, 15));
		database.from(TYPES).insert(original).execute();
		
		TypeRow fetched = database.from(TYPES).select().where(Sql.equalTo(T_ID, 1)).fetchOne();
		assertEquals(9_000_000_000L, fetched.big());
		assertEquals(3.5, fetched.ratio());
		assertEquals(0, new BigDecimal("12.34").compareTo(fetched.amount()));
		assertEquals(LocalDate.of(2024, 1, 15), fetched.date());
		
		database.table(TYPES).dropIfExists();
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void projectIntoMapsColumnsToRecord(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetSchema(database);
		seedPersons(database);
		
		List<IdName> projected = database.from(PERSON).select(P_ID, P_NAME).projectInto(IdName.class).orderBy(P_ID.ascending()).fetch();
		assertEquals(4, projected.size());
		assertEquals(1, projected.getFirst().id());
		assertEquals("Alice", projected.getFirst().name());
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void createAndDropIndexThroughProvider(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetSchema(database);
		
		database.table(PERSON).createIndex("idx_it_person_age", List.of(P_AGE), SqlIndexMethod.BTREE);
		assertTrue(database.table(PERSON).getIndexes().stream().map(SqlIndex::name).anyMatch("idx_it_person_age"::equals));
		
		database.table(PERSON).dropIndex("idx_it_person_age");
		assertFalse(database.table(PERSON).getIndexes().stream().map(SqlIndex::name).anyMatch("idx_it_person_age"::equals));
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void schemaLifecycle(@NonNull Engine engine) throws SqlException {
		assumeTrue(engine.supports(SqlFeature.SCHEMAS), "Schemas not supported by " + engine.name());
		assumeTrue(!"MySQL".equals(engine.name()), "The MySQL test container user lacks the CREATE DATABASE privilege required for schema creation");
		SqlDatabase database = engine.database();
		
		database.createSchemaIfNotExists("it_schema");
		assertTrue(database.existsSchema("it_schema"));
		
		database.dropSchema("it_schema", true);
		assertFalse(database.existsSchema("it_schema"));
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void explicitIsolationAndPropagationCommits(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetSchema(database);
		
		try (SqlTransaction transaction = database.beginTransaction(SqlIsolationLevel.READ_COMMITTED, SqlPropagation.REQUIRED)) {
			transaction.from(PERSON).insert(new Person(1, "Alice", "alice@example.com", 30, true)).execute();
			transaction.commit();
		}
		
		assertEquals(1L, database.from(PERSON).select().count());
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void sessionRunsQueries(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetSchema(database);
		seedPersons(database);
		
		try (SqlSession session = database.openSession()) {
			assertEquals(4L, session.from(PERSON).select().count());
			assertEquals(0, session.trackedCount());
		}
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void fetchPageMiddleAndLastSlices(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetSchema(database);
		seedPersons(database);
		
		var middle = database.from(PERSON).select().orderBy(P_ID.ascending()).fetchPage(1, 1);
		assertEquals(1, middle.content().size());
		assertTrue(middle.hasNext());
		assertTrue(middle.hasPrevious());
		
		var last = database.from(PERSON).select().orderBy(P_ID.ascending()).fetchPage(3, 1);
		assertEquals(1, last.content().size());
		assertFalse(last.hasNext());
		assertTrue(last.hasPrevious());
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void migrationReRunIsIdempotent(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetMigrationState(database);
		
		SqlMigrationRunner runner = SqlMigrationRunner.of(database);
		runner.register(List.of(createTableMigration(), addIndexMigration()));
		
		runner.migrate();
		assertDoesNotThrow(runner::migrate);
		long applied = runner.status().stream().filter(info -> info.status() == SqlMigrationStatus.APPLIED).count();
		assertEquals(2L, applied);
		
		resetMigrationState(database);
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void migrationWithUuidColumnApplies(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetMigrationState(database, UUID_ROW);
		
		SqlMigrationRunner runner = SqlMigrationRunner.of(database);
		runner.register(List.of(createUuidTableMigration()));
		
		try {
			assertDoesNotThrow(runner::migrate);
			assertTrue(database.table(UUID_ROW).exists());
			
			UUID id = UUID.fromString("11111111-2222-3333-4444-555555555555");
			database.from(UUID_ROW).insert(new UuidRow(id, "alpha")).execute();
			assertEquals(id, database.from(UUID_ROW).select(U_ID).fetchOne());
		} finally {
			resetMigrationState(database, UUID_ROW);
		}
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void binaryColumnsCreateAndRoundTripValues(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		database.table(BINARY).dropIfExists();
		
		try {
			assertDoesNotThrow(() -> database.table(BINARY).create());
			
			byte[] fixed = new byte[16];
			Arrays.fill(fixed, (byte) 0x7F);
			byte[] data = { 1, 2, 3, 4, 5 };
			byte[] payload = { 9, 8, 7 };
			database.from(BINARY).insert(new BinaryRow(1, fixed, data, payload)).execute();
			
			BinaryRow fetched = database.from(BINARY).select().where(Sql.equalTo(B_ID, 1)).fetchOne();
			assertArrayEquals(fixed, fetched.fixed());
			assertArrayEquals(data, fetched.data());
			assertArrayEquals(payload, fetched.payload());
		} finally {
			database.table(BINARY).dropIfExists();
		}
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void migrationWithBinaryColumnsApplies(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetMigrationState(database, BINARY);
		
		SqlMigrationRunner runner = SqlMigrationRunner.of(database);
		runner.register(List.of(createBinaryTableMigration()));
		
		try {
			assertDoesNotThrow(runner::migrate);
			assertTrue(database.table(BINARY).exists());
		} finally {
			resetMigrationState(database, BINARY);
		}
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void healthAndPingReportLiveConnection(@NonNull Engine engine) {
		SqlDatabase database = engine.database();
		assertTrue(database.health());
		assertTrue(database.ping());
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void rightJoinKeepsUnmatchedRowsOfJoinedTable(@NonNull Engine engine) throws SqlException {
		assumeTrue(!"SQLite".equals(engine.name()), "The bundled SQLite build does not support RIGHT JOIN");
		SqlDatabase database = engine.database();
		resetSchema(database);
		seedPersons(database);
		seedRoles(database);
		
		List<Integer> rows = database.from(PERSON_ROLE).select(PR_ROLE).rightJoin(PERSON, Sql.equalTo(P_ID, PR_PERSON)).fetch();
		assertEquals(5, rows.size());
		assertEquals(2, rows.stream().filter(Objects::isNull).count());
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void fullJoinKeepsUnmatchedRowsOfBothSides(@NonNull Engine engine) throws SqlException {
		assumeTrue("PostgreSQL".equals(engine.name()) || "SQLServer".equals(engine.name()), "FULL JOIN not supported by " + engine.name());
		SqlDatabase database = engine.database();
		resetSchema(database);
		seedPersons(database);
		seedRoles(database);
		
		long joined = database.from(PERSON).select(P_ID).fullJoin(PERSON_ROLE, Sql.equalTo(PR_PERSON, P_ID)).count();
		assertEquals(5L, joined);
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void lateralJoinCorrelatesSubqueryPerRow(@NonNull Engine engine) throws SqlException {
		assumeTrue(engine.supports(SqlFeature.LATERAL_JOIN), "LATERAL joins not supported by " + engine.name());
		SqlDatabase database = engine.database();
		resetSchema(database);
		seedPersons(database);
		seedRoles(database);
		
		SqlSelectQuery<Integer> roles = database.from(PERSON_ROLE).select(PR_ROLE).where(Sql.equalTo(PR_PERSON, P_ID));
		long correlated = database.from(PERSON).select(P_ID).lateralJoin(SqlJoinType.INNER, roles, SqlAlias.of("lat")).count();
		assertEquals(3L, correlated);
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void commonTableExpressionFiltersBeforeMainQuery(@NonNull Engine engine) throws SqlException {
		assumeTrue(engine.supports(SqlFeature.CTE), "Common table expressions not supported by " + engine.name());
		SqlDatabase database = engine.database();
		resetSchema(database);
		seedPersons(database);
		
		SqlCommonTableExpression active = database.from(PERSON).select(P_ID, P_NAME).where(Sql.equalTo(P_ACTIVE, true)).asCommonExpression(SqlAlias.of("it_cte_active"), false);
		List<String> names = database.from(CTE_ACTIVE).select(CTE_NAME).with(active).orderBy(CTE_NAME.ascending()).fetch();
		assertEquals(List.of("Alice", "Bob", "Dave"), names);
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void recursiveCommonTableExpressionWalksHierarchy(@NonNull Engine engine) throws SqlException {
		assumeTrue(engine.supports(SqlFeature.RECURSIVE_CTE), "Recursive common table expressions not supported by " + engine.name());
		SqlDatabase database = engine.database();
		seedNodes(database);
		
		SqlSelectQuery<IdName> roots = database.from(NODE).select(N_ID, N_NAME).where(Sql.isNull(N_PARENT)).projectInto(IdName.class);
		SqlSelectQuery<IdName> descendants = database.from(NODE).select(N_ID, N_NAME).innerJoin(TREE, Sql.equalTo(N_PARENT, TREE_ID)).projectInto(IdName.class);
		List<IdName> tree = database.from(TREE).select(TREE_ID, TREE_NAME).projectInto(IdName.class).with(roots.unionAll(descendants).asCommonExpression(SqlAlias.of("it_tree"), true)).fetch();
		assertEquals(4, tree.size());
		
		database.table(NODE).dropIfExists();
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void updateWithJoinUpdatesMatchedRows(@NonNull Engine engine) throws SqlException {
		assumeTrue(engine.supports(SqlFeature.JOINED_DML), "Joined DML not supported by " + engine.name());
		SqlDatabase database = engine.database();
		resetSchema(database);
		seedPersons(database);
		seedRoles(database);
		
		database.from(PERSON).update().innerJoin(PERSON_ROLE, Sql.equalTo(PR_PERSON, P_ID)).set(P_ACTIVE, false).allowAll().execute();
		
		assertEquals(false, database.from(PERSON).select(P_ACTIVE).where(Sql.equalTo(P_ID, 1)).fetchOne());
		assertEquals(false, database.from(PERSON).select(P_ACTIVE).where(Sql.equalTo(P_ID, 2)).fetchOne());
		assertEquals(true, database.from(PERSON).select(P_ACTIVE).where(Sql.equalTo(P_ID, 4)).fetchOne());
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void deleteWithJoinDeletesMatchedRows(@NonNull Engine engine) throws SqlException {
		assumeTrue(engine.supports(SqlFeature.JOINED_DML), "Joined DML not supported by " + engine.name());
		SqlDatabase database = engine.database();
		resetSchema(database);
		seedPersons(database);
		seedRoles(database);
		
		database.from(PERSON_ROLE).delete().innerJoin(PERSON, Sql.equalTo(P_ID, PR_PERSON)).where(Sql.equalTo(P_ID, 1)).execute();
		assertEquals(1L, database.from(PERSON_ROLE).select().count());
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void updateDecrementColumn(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetSchema(database);
		seedPersons(database);
		
		database.from(PERSON).update().decrement(P_AGE, 5).where(Sql.equalTo(P_ID, 1)).execute();
		assertEquals(25, database.from(PERSON).select(P_AGE).where(Sql.equalTo(P_ID, 1)).fetchOne());
		assertEquals(25, database.from(PERSON).select(P_AGE).where(Sql.equalTo(P_ID, 2)).fetchOne());
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void updateSetToColumnExpression(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetSchema(database);
		seedPersons(database);
		
		database.from(PERSON).update().set(P_NAME, Sql.upper(P_EMAIL)).where(Sql.equalTo(P_ID, 1)).execute();
		assertEquals("ALICE@EXAMPLE.COM", database.from(PERSON).select(P_NAME).where(Sql.equalTo(P_ID, 1)).fetchOne());
		assertEquals("Bob", database.from(PERSON).select(P_NAME).where(Sql.equalTo(P_ID, 2)).fetchOne());
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void orderByNullsFirstAndLast(@NonNull Engine engine) throws SqlException {
		assumeTrue(engine.supports(SqlFeature.NULLS_ORDERING), "Explicit null ordering not supported by " + engine.name());
		SqlDatabase database = engine.database();
		seedMigItems(database);
		
		List<String> nullsFirst = database.from(MIG).select(M_DESC).orderBy(M_DESC.ascending().nullsFirst()).fetch();
		assertNull(nullsFirst.getFirst());
		
		List<String> nullsLast = database.from(MIG).select(M_DESC).orderBy(M_DESC.ascending().nullsLast()).fetch();
		assertNull(nullsLast.getLast());
		
		database.table(MIG).dropIfExists();
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void offsetWithoutLimitSkipsLeadingRows(@NonNull Engine engine) throws SqlException {
		assumeTrue(engine.supports(SqlFeature.OFFSET_WITHOUT_LIMIT), "OFFSET without LIMIT not supported by " + engine.name());
		SqlDatabase database = engine.database();
		resetSchema(database);
		seedPersons(database);
		
		List<Integer> rows = database.from(PERSON).select(P_ID).orderBy(P_ID.ascending()).offset(2).fetch();
		assertEquals(List.of(3, 4), rows);
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void aliasedExpressionIsSelectableByAlias(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetSchema(database);
		seedPersons(database);
		
		SqlExpression<Integer> doubled = Sql.multiply(P_AGE, 2).as(SqlAlias.of("double_age"));
		List<Integer> ages = database.from(PERSON).select(doubled).orderBy(P_ID.ascending()).fetch();
		assertEquals(4, ages.size());
		assertEquals(60, ages.getFirst());
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void arrayColumnRoundTripsValues(@NonNull Engine engine) throws SqlException {
		assumeTrue(engine.supports(SqlFeature.ARRAY_TYPE), "Array columns not supported by " + engine.name());
		SqlDatabase database = engine.database();
		database.table(ARRAYS).dropIfExists();
		
		try {
			database.table(ARRAYS).create();
			database.from(ARRAYS).insert(new ArrayRow(1, new String[] { "red", "green" })).execute();
			
			ArrayRow fetched = database.from(ARRAYS).select().where(Sql.equalTo(AR_ID, 1)).fetchOne();
			assertArrayEquals(new String[] { "red", "green" }, fetched.tags());
		} finally {
			database.table(ARRAYS).dropIfExists();
		}
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void temporalTypesRoundTripDateTimeAndInstant(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		database.table(TEMPORAL).dropIfExists();
		
		try {
			database.table(TEMPORAL).create();
			assumeTrue("PostgreSQL".equals(engine.name()) || "SQLServer".equals(engine.name()) || "H2".equals(engine.name()),
				"The " + engine.name() + " driver rejects TIMESTAMP_WITH_TIMEZONE, which SqlTypes.INSTANT maps onto");
			
			LocalDateTime at = LocalDateTime.of(2024, 5, 17, 13, 45, 30);
			Instant instant = Instant.parse("2024-05-17T11:45:30Z");
			database.from(TEMPORAL).insert(new TemporalRow(1, at, instant, LocalTime.of(6, 15, 0))).execute();
			
			TemporalRow fetched = database.from(TEMPORAL).select().where(Sql.equalTo(TP_ID, 1)).fetchOne();
			assertEquals(at, fetched.at());
			assertEquals(instant, fetched.instant());
			assertEquals(LocalTime.of(6, 15, 0), fetched.time());
		} finally {
			database.table(TEMPORAL).dropIfExists();
		}
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void customMappedTypeRoundTripsValues(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		database.table(CODES).dropIfExists();
		
		try {
			database.table(CODES).create();
			database.from(CODES).insert(new CodeRow(1, new Code("ALPHA"))).execute();
			
			CodeRow fetched = database.from(CODES).select().where(Sql.equalTo(C_ID, 1)).fetchOne();
			assertEquals(new Code("ALPHA"), fetched.code());
			assertEquals(new Code("ALPHA"), database.from(CODES).select(C_CODE).fetchOne());
		} finally {
			database.table(CODES).dropIfExists();
		}
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void auditedTableInsertPopulatesCreatedColumns(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetAuditState(database);
		
		try (SqlSession session = database.openSession(() -> Optional.of("tester"))) {
			session.from(AUDITED).insert(new AuditRow(1, "first")).execute();
			
			SqlAudited<AuditRow> audited = session.from(AUDITED).select().where(Sql.equalTo(A_ID, 1)).withAudit().fetchOne();
			assertEquals(1L, audited.version().orElse(-1L));
			assertTrue(audited.createdAt().isPresent());
			assertEquals("tester", audited.createdBy().orElse(null));
		}
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void auditedTableWithCustomConfigUsesConfiguredColumnNames(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		database.table(AUDITED_CUSTOM).dropIfExists();
		
		try {
			database.table(AUDITED_CUSTOM).create();
			database.from(AUDITED_CUSTOM).insert(new AuditRow(1, "custom")).execute();
			
			SqlAudited<AuditRow> audited = database.from(AUDITED_CUSTOM).select().where(Sql.equalTo(AC_ID, 1)).withAudit().fetchOne();
			assertEquals(1L, audited.version().orElse(-1L));
			assertEquals("custom", audited.entity().label());
		} finally {
			database.table(AUDITED_CUSTOM).dropIfExists();
		}
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void selectWithAuditReturnsMetadata(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetAuditState(database);
		database.from(AUDITED).insert(new AuditRow(1, "plain")).execute();
		
		AuditRow plain = database.from(AUDITED).select().where(Sql.equalTo(A_ID, 1)).fetchOne();
		SqlAudited<AuditRow> audited = database.from(AUDITED).select().where(Sql.equalTo(A_ID, 1)).withAudit().fetchOne();
		assertEquals(plain, audited.entity());
		assertTrue(audited.version().isPresent());
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void sessionUpdateBumpsVersionAndUpdatedAt(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetAuditState(database);
		
		try (SqlSession session = database.openSession(() -> Optional.of("tester"))) {
			session.from(AUDITED).insert(new AuditRow(1, "before")).execute();
			SqlAudited<AuditRow> loaded = session.from(AUDITED).select().where(Sql.equalTo(A_ID, 1)).withAudit().fetchOne();
			session.attach(AUDITED, loaded);
			
			SqlAuditMetadata metadata = session.update(AUDITED, new AuditRow(1, "after"));
			assertEquals(2L, metadata.version().orElse(-1L));
			assertTrue(metadata.updatedAt().isPresent());
			assertEquals("after", session.from(AUDITED).select(A_LABEL).where(Sql.equalTo(A_ID, 1)).fetchOne());
		}
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void sessionUpdateWithStaleVersionThrowsOptimisticLock(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetAuditState(database);
		database.from(AUDITED).insert(new AuditRow(1, "initial")).execute();
		
		try (SqlSession first = database.openSession(); SqlSession second = database.openSession()) {
			SqlAudited<AuditRow> stale = second.from(AUDITED).select().where(Sql.equalTo(A_ID, 1)).withAudit().fetchOne();
			first.attach(AUDITED, first.from(AUDITED).select().where(Sql.equalTo(A_ID, 1)).withAudit().fetchOne());
			first.update(AUDITED, new AuditRow(1, "winner"));
			
			assertThrows(SqlOptimisticLockException.class, () -> second.update(AUDITED, stale));
			assertEquals("winner", database.from(AUDITED).select(A_LABEL).where(Sql.equalTo(A_ID, 1)).fetchOne());
		}
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void sessionUpdateOfUntrackedEntityThrows(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetAuditState(database);
		database.from(AUDITED).insert(new AuditRow(1, "initial")).execute();
		
		try (SqlSession session = database.openSession()) {
			assertThrows(SqlUntrackedEntityException.class, () -> session.update(AUDITED, new AuditRow(1, "never tracked")));
		}
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void sessionAttachAndEvictControlTracking(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetAuditState(database);
		database.from(AUDITED).insert(new AuditRow(1, "tracked")).execute();
		
		try (SqlSession session = database.openSession()) {
			SqlAudited<AuditRow> audited = session.from(AUDITED).select().where(Sql.equalTo(A_ID, 1)).withAudit().fetchOne();
			session.attach(AUDITED, audited);
			assertTrue(session.isTracked(AUDITED, audited.entity()));
			assertEquals(1, session.trackedCount());
			
			session.evict(AUDITED, audited.entity());
			assertFalse(session.isTracked(AUDITED, audited.entity()));
			
			session.attach(AUDITED, audited);
			session.clear();
			assertEquals(0, session.trackedCount());
		}
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void sessionReloadRefreshesAuditMetadata(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetAuditState(database);
		database.from(AUDITED).insert(new AuditRow(1, "initial")).execute();
		
		try (SqlSession session = database.openSession()) {
			AuditRow entity = session.from(AUDITED).select().where(Sql.equalTo(A_ID, 1)).fetchOne();
			SqlAuditMetadata before = session.reload(AUDITED, entity);
			assertEquals(1L, before.version().orElse(-1L));
			
			try (SqlSession other = database.openSession()) {
				other.attach(AUDITED, other.from(AUDITED).select().where(Sql.equalTo(A_ID, 1)).withAudit().fetchOne());
				other.update(AUDITED, new AuditRow(1, "changed"));
			}
			
			assertEquals(2L, session.reload(AUDITED, entity).version().orElse(-1L));
		}
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void transactionListenerReceivesCommitCallback(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetSchema(database);
		
		AtomicInteger commits = new AtomicInteger(0);
		AtomicInteger rollbacks = new AtomicInteger(0);
		try (SqlTransaction transaction = database.beginTransaction()) {
			transaction.addListener(listener(commits, rollbacks));
			transaction.from(PERSON).insert(new Person(1, "Alice", "alice@example.com", 30, true)).execute();
			transaction.commit();
		}
		
		assertEquals(1, commits.get());
		assertEquals(0, rollbacks.get());
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void transactionListenerReceivesRollbackCallback(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetSchema(database);
		
		AtomicInteger commits = new AtomicInteger(0);
		AtomicInteger rollbacks = new AtomicInteger(0);
		try (SqlTransaction transaction = database.beginTransaction()) {
			transaction.addListener(listener(commits, rollbacks));
			transaction.from(PERSON).insert(new Person(1, "Alice", "alice@example.com", 30, true)).execute();
			transaction.rollback();
		}
		
		assertEquals(0, commits.get());
		assertEquals(1, rollbacks.get());
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void requiresNewPropagationCommitsIndependently(@NonNull Engine engine) throws SqlException {
		assumeTrue(!"SQLite".equals(engine.name()), "SQLite locks the database file for the suspended outer transaction");
		SqlDatabase database = engine.database();
		resetSchema(database);
		
		try (SqlTransaction outer = database.beginTransaction()) {
			outer.from(PERSON).insert(new Person(1, "Alice", "alice@example.com", 30, true)).execute();
			
			try (SqlTransaction inner = database.beginTransaction(SqlIsolationLevel.READ_COMMITTED, SqlPropagation.REQUIRES_NEW)) {
				inner.from(PERSON).insert(new Person(2, "Bob", "bob@example.com", 25, true)).execute();
				inner.commit();
			}
			outer.rollback();
		}
		
		assertEquals(1L, database.from(PERSON).select().count());
		assertTrue(database.from(PERSON).select().where(Sql.equalTo(P_ID, 2)).exists());
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void nestedPropagationRollsBackInnerOnly(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetSchema(database);
		
		try (SqlTransaction outer = database.beginTransaction()) {
			outer.from(PERSON).insert(new Person(1, "Alice", "alice@example.com", 30, true)).execute();
			
			try (SqlTransaction nested = database.beginTransaction(SqlIsolationLevel.READ_COMMITTED, SqlPropagation.NESTED)) {
				nested.from(PERSON).insert(new Person(2, "Bob", "bob@example.com", 25, true)).execute();
				nested.rollback();
			}
			outer.commit();
		}
		
		assertEquals(1L, database.from(PERSON).select().count());
		assertTrue(database.from(PERSON).select().where(Sql.equalTo(P_ID, 1)).exists());
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void mandatoryPropagationWithoutTransactionThrows(@NonNull Engine engine) {
		SqlDatabase database = engine.database();
		assertThrows(SqlTransactionPropagationException.class, () -> database.beginTransaction(SqlIsolationLevel.READ_COMMITTED, SqlPropagation.MANDATORY));
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void neverPropagationInsideTransactionThrows(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetSchema(database);
		
		try (SqlTransaction outer = database.beginTransaction()) {
			assertThrows(SqlTransactionPropagationException.class, () -> database.beginTransaction(SqlIsolationLevel.READ_COMMITTED, SqlPropagation.NEVER));
			outer.rollback();
		}
		
		assertDoesNotThrow(() -> database.beginTransaction(SqlIsolationLevel.READ_COMMITTED, SqlPropagation.NEVER).close());
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void everySupportedIsolationLevelExecutes(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetSchema(database);
		
		int inserted = 0;
		for (SqlIsolationLevel level : SqlIsolationLevel.values()) {
			try (SqlTransaction transaction = database.beginTransaction(level, SqlPropagation.REQUIRED)) {
				transaction.from(PERSON).insert(new Person(++inserted, "P" + inserted, inserted + "@example.com", 20, true)).execute();
				transaction.commit();
			} catch (SqlException ignored) {
				inserted--; // The engine rejects this isolation level, which is a documented dialect difference
			}
		}
		
		assertTrue(inserted > 0, "No isolation level was accepted by " + engine.name());
		assertEquals(inserted, database.from(PERSON).select().count());
	}
	
	@MethodSource("engines")
	@ParameterizedTest(name = "{0}")
	void multipleSavepointsRollbackToEarlierDiscardsLater(@NonNull Engine engine) throws SqlException {
		SqlDatabase database = engine.database();
		resetSchema(database);
		
		try (SqlTransaction transaction = database.beginTransaction()) {
			transaction.from(PERSON).insert(new Person(1, "Alice", "alice@example.com", 30, true)).execute();
			SqlSavepoint first = transaction.savepoint("sp1");
			transaction.from(PERSON).insert(new Person(2, "Bob", "bob@example.com", 25, true)).execute();
			transaction.savepoint("sp2");
			transaction.from(PERSON).insert(new Person(3, "Charlie", "charlie@example.com", 40, false)).execute();
			
			transaction.rollbackTo(first);
			transaction.commit();
		}
		
		assertEquals(1L, database.from(PERSON).select().count());
		assertTrue(database.from(PERSON).select().where(Sql.equalTo(P_ID, 1)).exists());
	}
	
	private record Engine(@NonNull String name, @NonNull SqlDatabase database) {
		
		private boolean supports(@NonNull SqlFeature feature) {
			return this.database.getDialect().isFeatureSupported(feature);
		}
		
		@Override
		public @NonNull String toString() {
			return this.name;
		}
	}
	
	private record Person(int id, @NonNull String name, @NonNull String email, int age, boolean active) {}
	
	private record Role(int id, @NonNull String name) {}
	
	private record PersonRole(int personId, int roleId) {}
	
	private record MigItem(int id, @NonNull String name, String description) {}
	
	private record Parent(int id, @NonNull String name) {}
	
	private record Child(int id, int parentId, @NonNull String label) {}
	
	private record TypeRow(int id, long big, double ratio, @NonNull BigDecimal amount, @NonNull LocalDate date) {}
	
	private record IdName(int id, @NonNull String name) {}
	
	private record UuidRow(@NonNull UUID id, @NonNull String label) {}
	
	private record BinaryRow(int id, byte @NonNull [] fixed, byte @NonNull [] data, byte @NonNull [] payload) {}
	
	private record AuditRow(int id, @NonNull String label) {}
	
	private record Node(int id, Integer parentId, @NonNull String name) {}
	
	private record TemporalRow(int id, @NonNull LocalDateTime at, @NonNull Instant instant, @NonNull LocalTime time) {}
	
	private record Code(@NonNull String value) {}
	
	private record CodeRow(int id, @NonNull Code code) {}
	
	private record ArrayRow(int id, String @NonNull [] tags) {}
}
