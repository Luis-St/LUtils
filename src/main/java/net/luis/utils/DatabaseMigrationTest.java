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
import net.luis.utils.io.database.Sql;
import net.luis.utils.io.database.SqlDatabase;
import net.luis.utils.io.database.SqlReferentialAction;
import net.luis.utils.io.database.dialect.SqlDialects;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.migration.*;
import net.luis.utils.io.database.rendering.SqlRendered;
import net.luis.utils.io.database.table.*;
import net.luis.utils.io.database.type.SqlTypes;
import net.luis.utils.io.database.type.parameter.SqlParameter;
import net.luis.utils.logging.*;
import net.luis.utils.util.Version;
import org.apache.logging.log4j.*;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.sql.DataSource;
import java.time.Instant;
import java.util.List;

/**
 * Demonstrates the complete migration API with four progressive migrations.<br>
 *
 * @author Luis-St
 */
@SuppressWarnings("unused")
public class DatabaseMigrationTest {

	public record Person(int id, @NonNull String firstName, @NonNull String lastName, @NonNull String email, int age, long version, @Nullable Instant createdAt) {}

	public record User(int id, @NonNull String username, @NonNull String email, int age, @Nullable String bio, long version) {}

	public record Role(int id, @NonNull String name, @Nullable String description, int priority, boolean active) {}

	public record UserRole(int userId, int roleId, @NonNull Instant assignedAt) {}

	public record AuditLog(int id, int userId, @NonNull String action, @Nullable String details, @Nullable String ipAddress, @NonNull Instant timestamp) {}

	private static final Logger LOGGER;
	private static final DataSource DATA_SOURCE;

	// Person table (initial, renamed to user in migration 3)
	public static final SqlTable<Person> PERSON_TABLE;
	public static final SqlColumn<Person, Integer> PERSON_ID;
	public static final SqlColumn<Person, String> FIRST_NAME;
	public static final SqlColumn<Person, String> LAST_NAME;
	public static final SqlColumn<Person, String> PERSON_EMAIL;
	public static final SqlColumn<Person, Integer> PERSON_AGE;
	public static final SqlColumn<Person, Long> PERSON_VERSION;
	public static final SqlColumn<Person, Instant> PERSON_CREATED_AT;

	// User table (renamed from person in migration 3)
	public static final SqlTable<User> USER_TABLE;
	public static final SqlColumn<User, Integer> USER_ID;
	public static final SqlColumn<User, String> USERNAME;
	public static final SqlColumn<User, String> USER_EMAIL;
	public static final SqlColumn<User, Integer> USER_AGE;
	public static final SqlColumn<User, String> USER_BIO;
	public static final SqlColumn<User, Long> USER_VERSION;

	// Role table
	public static final SqlTable<Role> ROLE_TABLE;
	public static final SqlColumn<Role, Integer> ROLE_ID;
	public static final SqlColumn<Role, String> ROLE_NAME;
	public static final SqlColumn<Role, String> ROLE_DESCRIPTION;
	public static final SqlColumn<Role, Integer> ROLE_PRIORITY;
	public static final SqlColumn<Role, Boolean> ROLE_ACTIVE;

	// UserRole junction table
	public static final SqlTable<UserRole> USER_ROLE_TABLE;
	public static final SqlColumn<UserRole, Integer> UR_USER_ID;
	public static final SqlColumn<UserRole, Integer> UR_ROLE_ID;
	public static final SqlColumn<UserRole, Instant> UR_ASSIGNED_AT;

	// AuditLog table (created in migration 2, dropped in migration 4)
	public static final SqlTable<AuditLog> AUDIT_LOG_TABLE;
	public static final SqlColumn<AuditLog, Integer> AUDIT_ID;
	public static final SqlColumn<AuditLog, Integer> AUDIT_USER_ID;
	public static final SqlColumn<AuditLog, String> AUDIT_ACTION;
	public static final SqlColumn<AuditLog, String> AUDIT_DETAILS;
	public static final SqlColumn<AuditLog, String> AUDIT_IP_ADDRESS;
	public static final SqlColumn<AuditLog, Instant> AUDIT_TIMESTAMP;

	static {
		System.setProperty("reflection.exceptions.throw", "true");
		LoggingUtils.initialize(LoggerConfiguration.DEFAULT.disableLogging(LoggingType.FILE).addDefaultLogger(LoggingType.CONSOLE, Level.DEBUG));
		LOGGER = LogManager.getLogger(DatabaseMigrationTest.class);

		HikariConfig config = new HikariConfig();
		config.setJdbcUrl("jdbc:postgresql://localhost:3000/test");
		config.setUsername("test");
		config.setPassword("test");
		DATA_SOURCE = new HikariDataSource(config);

		// Person table (old reference for rename)
		SqlTableBuilder<Person> personBuilder = SqlTable.of(Person.class, "person");
		PERSON_ID = personBuilder.column("id", SqlTypes.INTEGER, Person::id, col -> col.primaryKey().notNull().autoIncrement());
		FIRST_NAME = personBuilder.column("first_name", SqlTypes.STRING.configure(SqlParameter.length(64)), Person::firstName, SqlColumnBuilder::notNull);
		LAST_NAME = personBuilder.column("last_name", SqlTypes.STRING.configure(SqlParameter.length(64)), Person::lastName, SqlColumnBuilder::notNull);
		PERSON_EMAIL = personBuilder.column("email", SqlTypes.STRING.configure(SqlParameter.length(320)), Person::email, col -> col.notNull().unique());
		PERSON_AGE = personBuilder.column("age", SqlTypes.INTEGER, Person::age, SqlColumnBuilder::notNull);
		PERSON_VERSION = personBuilder.column("version", SqlTypes.LONG, Person::version, col -> col.notNull().defaultValue(0L));
		PERSON_CREATED_AT = personBuilder.column("created_at", SqlTypes.INSTANT.configure(SqlParameter.fractional(3)), Person::createdAt, SqlColumnBuilder::notNull);
		PERSON_TABLE = personBuilder.build();

		// User table (new reference for rename)
		SqlTableBuilder<User> userBuilder = SqlTable.of(User.class, "user");
		USER_ID = userBuilder.column("id", SqlTypes.INTEGER, User::id, col -> col.primaryKey().notNull().autoIncrement());
		USERNAME = userBuilder.column("username", SqlTypes.STRING.configure(SqlParameter.length(64)), User::username, SqlColumnBuilder::notNull);
		USER_EMAIL = userBuilder.column("email", SqlTypes.STRING.configure(SqlParameter.length(320)), User::email, col -> col.notNull().unique());
		USER_AGE = userBuilder.column("age", SqlTypes.INTEGER, User::age, SqlColumnBuilder::notNull);
		USER_BIO = userBuilder.column("bio", SqlTypes.STRING.configure(SqlParameter.length(1024)), User::bio);
		USER_VERSION = userBuilder.column("version", SqlTypes.LONG, User::version, col -> col.notNull().defaultValue(0L));
		USER_TABLE = userBuilder.build();

		// Role table
		SqlTableBuilder<Role> roleBuilder = SqlTable.of(Role.class, "role");
		ROLE_ID = roleBuilder.column("id", SqlTypes.INTEGER, Role::id, col -> col.primaryKey().notNull().autoIncrement());
		ROLE_NAME = roleBuilder.column("name", SqlTypes.STRING.configure(SqlParameter.length(64)), Role::name, SqlColumnBuilder::notNull);
		ROLE_DESCRIPTION = roleBuilder.column("description", SqlTypes.STRING.configure(SqlParameter.length(512)), Role::description);
		ROLE_PRIORITY = roleBuilder.column("priority", SqlTypes.INTEGER, Role::priority, col -> col.notNull().defaultValue(0));
		ROLE_ACTIVE = roleBuilder.column("active", SqlTypes.BOOLEAN, Role::active, col -> col.notNull().defaultValue(true));
		ROLE_TABLE = roleBuilder.build();

		// UserRole junction table
		SqlTableBuilder<UserRole> userRoleBuilder = SqlTable.of(UserRole.class, "user_role");
		UR_USER_ID = userRoleBuilder.column("user_id", SqlTypes.INTEGER, UserRole::userId, SqlColumnBuilder::notNull);
		UR_ROLE_ID = userRoleBuilder.column("role_id", SqlTypes.INTEGER, UserRole::roleId, SqlColumnBuilder::notNull);
		UR_ASSIGNED_AT = userRoleBuilder.column("assigned_at", SqlTypes.INSTANT.configure(SqlParameter.fractional(3)), UserRole::assignedAt, SqlColumnBuilder::notNull);
		USER_ROLE_TABLE = userRoleBuilder.build();

		// AuditLog table
		SqlTableBuilder<AuditLog> auditLogBuilder = SqlTable.of(AuditLog.class, "audit_log");
		AUDIT_ID = auditLogBuilder.column("id", SqlTypes.INTEGER, AuditLog::id, col -> col.primaryKey().notNull().autoIncrement());
		AUDIT_USER_ID = auditLogBuilder.column("user_id", SqlTypes.INTEGER, AuditLog::userId, SqlColumnBuilder::notNull);
		AUDIT_ACTION = auditLogBuilder.column("action", SqlTypes.STRING.configure(SqlParameter.length(64)), AuditLog::action, SqlColumnBuilder::notNull);
		AUDIT_DETAILS = auditLogBuilder.column("details", SqlTypes.STRING.configure(SqlParameter.length(1024)), AuditLog::details);
		AUDIT_IP_ADDRESS = auditLogBuilder.column("ip_address", SqlTypes.STRING.configure(SqlParameter.length(45)), AuditLog::ipAddress);
		AUDIT_TIMESTAMP = auditLogBuilder.column("timestamp", SqlTypes.INSTANT.configure(SqlParameter.fractional(3)), AuditLog::timestamp, SqlColumnBuilder::notNull);
		AUDIT_LOG_TABLE = auditLogBuilder.build();
	}

	static void main() {
		try {
			LOGGER.info("Starting database migration test");

			migrationLifecycle();

			LOGGER.info("Finished database migration test");
		} catch (Exception e) {
			LOGGER.error("Database migration test failed", e);
			throw new RuntimeException(e);
		}
	}

	static void migrationLifecycle() throws SqlException {
		try (SqlDatabase db = SqlDatabase.builder(DATA_SOURCE, SqlDialects.POSTGRESQL).build()) {
			SqlMigrationRunner runner = SqlMigrationRunner.of(db);
			runner.register(List.of(
				createInitialSchema(),
				addRelationshipsAndAuditLog(),
				renameAndAlterSchema(),
				restructureAndCleanup()
			));

			List<SqlRendered> preview = runner.dryRun();
			preview.forEach(rendered -> LOGGER.debug("Preview: {}", rendered.sql()));

			runner.migrate();

			List<SqlMigrationInfo> status = runner.status();
			status.forEach(info -> LOGGER.info("Migration {}: {} ({})", info.version(), info.description(), info.status()));

			runner.validate();

			runner.rollback();

			List<SqlMigrationInfo> afterRollback = runner.status();
			afterRollback.forEach(info -> LOGGER.info("After rollback {}: {}", info.version(), info.status()));
		}
	}

	static @NonNull SqlMigration createInitialSchema() {
		return new SqlMigration() {
			@Override
			public @NonNull Version version() {
				return Version.of(0, 1);
			}

			@Override
			public @NonNull String description() {
				return "Create initial schema with person and role tables";
			}

			@Override
			public void up(@NonNull SqlMigrationBuilder builder) throws SqlException {
				builder.createTable(PERSON_TABLE, table -> {
					table.column(PERSON_ID, SqlTypes.INTEGER, col -> col.notNull().autoIncrement());
					table.column(FIRST_NAME, SqlTypes.STRING.configure(SqlParameter.length(64)), SqlMigrationColumnBuilder::notNull);
					table.column(LAST_NAME, SqlTypes.STRING.configure(SqlParameter.length(64)), SqlMigrationColumnBuilder::notNull);
					table.column(PERSON_EMAIL, SqlTypes.STRING.configure(SqlParameter.length(320)), col -> col.notNull().unique());
					table.column(PERSON_AGE, SqlTypes.INTEGER, SqlMigrationColumnBuilder::notNull);
					table.column(PERSON_VERSION, SqlTypes.LONG, col -> col.notNull().defaultValue(0L));
					table.column(PERSON_CREATED_AT, SqlTypes.INSTANT.configure(SqlParameter.fractional(3)), SqlMigrationColumnBuilder::notNull);
					table.primaryKey(PERSON_ID);
				});

				builder.createTable(ROLE_TABLE, table -> {
					table.column(ROLE_ID, SqlTypes.INTEGER, col -> col.notNull().autoIncrement());
					table.column(ROLE_NAME, SqlTypes.STRING.configure(SqlParameter.length(64)), SqlMigrationColumnBuilder::notNull);
					table.column(ROLE_DESCRIPTION, SqlTypes.STRING.configure(SqlParameter.length(512)));
					table.column(ROLE_PRIORITY, SqlTypes.INTEGER, col -> col.notNull().defaultValue(0));
					table.column(ROLE_ACTIVE, SqlTypes.BOOLEAN, col -> col.notNull().defaultValue(true));
					table.primaryKey(ROLE_ID);
				});

				builder.createIndex(PERSON_TABLE, "idx_person_email", idx -> idx.columns(PERSON_EMAIL).unique());
				builder.createIndex(ROLE_TABLE, "idx_role_name", idx -> idx.columns(ROLE_NAME).unique());

				builder.data(PERSON_TABLE, provider -> {
					provider.insert(new Person(1, "John", "Doe", "john.doe@example.com", 30, 1, Instant.now())).execute();
					provider.insert(new Person(2, "Jane", "Smith", "jane.smith@example.com", 25, 1, Instant.now())).execute();
				});

				builder.data(ROLE_TABLE, provider -> {
					provider.insert(new Role(1, "admin", "Full access", 100, true)).execute();
					provider.insert(new Role(2, "user", "Standard access", 0, true)).execute();
				});
			}

			@Override
			public void down(@NonNull SqlMigrationBuilder builder) {
				builder.dropIndex(PERSON_TABLE, "idx_person_email");
				builder.dropIndex(ROLE_TABLE, "idx_role_name");
				builder.dropTable(PERSON_TABLE);
				builder.dropTable(ROLE_TABLE);
			}
		};
	}

	static @NonNull SqlMigration addRelationshipsAndAuditLog() {
		return new SqlMigration() {
			@Override
			public @NonNull Version version() {
				return Version.of(0, 2);
			}

			@Override
			public @NonNull String description() {
				return "Add user_role junction table, audit log, and constraints";
			}

			@Override
			public void up(@NonNull SqlMigrationBuilder builder) throws SqlException {
				builder.createTable(USER_ROLE_TABLE, table -> {
					table.column(UR_USER_ID, SqlTypes.INTEGER, SqlMigrationColumnBuilder::notNull);
					table.column(UR_ROLE_ID, SqlTypes.INTEGER, SqlMigrationColumnBuilder::notNull);
					table.column(UR_ASSIGNED_AT, SqlTypes.INSTANT.configure(SqlParameter.fractional(3)), SqlMigrationColumnBuilder::notNull);
					table.primaryKey(UR_USER_ID, UR_ROLE_ID);
				});

				builder.createTable(AUDIT_LOG_TABLE, table -> {
					table.column(AUDIT_ID, SqlTypes.INTEGER, col -> col.notNull().autoIncrement());
					table.column(AUDIT_USER_ID, SqlTypes.INTEGER, SqlMigrationColumnBuilder::notNull);
					table.column(AUDIT_ACTION, SqlTypes.STRING.configure(SqlParameter.length(64)), SqlMigrationColumnBuilder::notNull);
					table.column(AUDIT_DETAILS, SqlTypes.STRING.configure(SqlParameter.length(1024)));
					table.column(AUDIT_IP_ADDRESS, SqlTypes.STRING.configure(SqlParameter.length(45)));
					table.column(AUDIT_TIMESTAMP, SqlTypes.INSTANT.configure(SqlParameter.fractional(3)), SqlMigrationColumnBuilder::notNull);
					table.primaryKey(AUDIT_ID);
				});

				builder.addForeignKey(
					USER_ROLE_TABLE, "fk_user_role_person",
					new SqlColumn[] { UR_USER_ID }, PERSON_TABLE, new SqlColumn[] { PERSON_ID },
					SqlReferentialAction.CASCADE, SqlReferentialAction.CASCADE
				);
				builder.addForeignKey(
					USER_ROLE_TABLE, "fk_user_role_role",
					new SqlColumn[] { UR_ROLE_ID }, ROLE_TABLE, new SqlColumn[] { ROLE_ID },
					SqlReferentialAction.CASCADE, SqlReferentialAction.CASCADE
				);

				builder.addUniqueConstraint(ROLE_TABLE, "uq_role_name", ROLE_NAME);
				builder.addCheckConstraint(PERSON_TABLE, "chk_person_age", Sql.greaterThan(PERSON_AGE, Sql.of(0)));

				builder.createIndex(AUDIT_LOG_TABLE, "idx_audit_timestamp", idx -> idx.columns(AUDIT_TIMESTAMP));
				builder.createIndex(USER_ROLE_TABLE, "idx_user_role_user", idx -> idx.columns(UR_USER_ID));
			}

			@Override
			public void down(@NonNull SqlMigrationBuilder builder) {
				builder.dropIndex(USER_ROLE_TABLE, "idx_user_role_user");
				builder.dropIndex(AUDIT_LOG_TABLE, "idx_audit_timestamp");
				builder.dropConstraint(PERSON_TABLE, "chk_person_age");
				builder.dropConstraint(ROLE_TABLE, "uq_role_name");
				builder.dropConstraint(USER_ROLE_TABLE, "fk_user_role_role");
				builder.dropConstraint(USER_ROLE_TABLE, "fk_user_role_person");
				builder.dropTable(AUDIT_LOG_TABLE);
				builder.dropTable(USER_ROLE_TABLE);
			}
		};
	}

	static @NonNull SqlMigration renameAndAlterSchema() {
		return new SqlMigration() {
			@Override
			public @NonNull Version version() {
				return Version.of(0, 3);
			}

			@Override
			public @NonNull String description() {
				return "Rename person to user, restructure columns";
			}

			@Override
			public void up(@NonNull SqlMigrationBuilder builder) throws SqlException {
				builder.renameTable(PERSON_TABLE, USER_TABLE);
				builder.renameColumn(FIRST_NAME, USERNAME);
				builder.dropColumn(LAST_NAME);
				builder.addColumn(USER_BIO, SqlTypes.STRING.configure(SqlParameter.length(1024)));
				builder.alterColumn(USER_EMAIL, col -> col.setNullable(true));
				builder.alterColumn(USER_VERSION, col -> col.setDefault(1L));
				builder.renameIndex(USER_TABLE, "idx_person_email", "idx_user_email");
			}

			@Override
			public void down(@NonNull SqlMigrationBuilder builder) {
				builder.renameIndex(PERSON_TABLE, "idx_user_email", "idx_person_email");
				builder.alterColumn(PERSON_VERSION, col -> col.setDefault(0L));
				builder.alterColumn(PERSON_EMAIL, col -> col.setNullable(false));
				builder.dropColumn(USER_BIO);
				builder.addColumn(LAST_NAME, SqlTypes.STRING.configure(SqlParameter.length(64)), SqlMigrationColumnBuilder::notNull);
				builder.renameColumn(USERNAME, FIRST_NAME);
				builder.renameTable(USER_TABLE, PERSON_TABLE);
			}
		};
	}

	static @NonNull SqlMigration restructureAndCleanup() {
		return new SqlMigration() {
			@Override
			public @NonNull Version version() {
				return Version.of(0, 4);
			}

			@Override
			public @NonNull String description() {
				return "Restructure constraints and remove audit log";
			}

			@Override
			public void up(@NonNull SqlMigrationBuilder builder) throws SqlException {
				builder.dropConstraint(USER_TABLE, "chk_person_age");
				builder.addCheckConstraint(USER_TABLE, "chk_user_age", Sql.greaterThanOrEqualTo(USER_AGE, Sql.of(18)));
				builder.addCompositePrimaryKey(USER_ROLE_TABLE, "pk_user_role", UR_USER_ID, UR_ROLE_ID);
				builder.alterColumn(USER_VERSION, col -> col.dropDefault());
				builder.alterColumn(USER_VERSION, col -> col.setNullable(true));
				builder.dropIndex(AUDIT_LOG_TABLE, "idx_audit_timestamp");
				builder.dropTable(AUDIT_LOG_TABLE);
			}

			@Override
			public void down(@NonNull SqlMigrationBuilder builder) throws SqlException {
				builder.createTable(AUDIT_LOG_TABLE, table -> {
					table.column(AUDIT_ID, SqlTypes.INTEGER, col -> col.notNull().autoIncrement());
					table.column(AUDIT_USER_ID, SqlTypes.INTEGER, SqlMigrationColumnBuilder::notNull);
					table.column(AUDIT_ACTION, SqlTypes.STRING.configure(SqlParameter.length(64)), SqlMigrationColumnBuilder::notNull);
					table.column(AUDIT_DETAILS, SqlTypes.STRING.configure(SqlParameter.length(1024)));
					table.column(AUDIT_IP_ADDRESS, SqlTypes.STRING.configure(SqlParameter.length(45)));
					table.column(AUDIT_TIMESTAMP, SqlTypes.INSTANT.configure(SqlParameter.fractional(3)), SqlMigrationColumnBuilder::notNull);
					table.primaryKey(AUDIT_ID);
				});
				builder.createIndex(AUDIT_LOG_TABLE, "idx_audit_timestamp", idx -> idx.columns(AUDIT_TIMESTAMP));
				builder.alterColumn(USER_VERSION, col -> col.setNullable(false));
				builder.alterColumn(USER_VERSION, col -> col.setDefault(1L));
				builder.dropConstraint(USER_ROLE_TABLE, "pk_user_role");
				builder.dropConstraint(USER_TABLE, "chk_user_age");
				builder.addCheckConstraint(USER_TABLE, "chk_person_age", Sql.greaterThan(USER_AGE, Sql.of(0)));
			}
		};
	}
}
