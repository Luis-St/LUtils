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
import net.luis.utils.io.database.*;
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
 *
 * @author Luis-St
 *
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
	
	// User table (current schema)
	public static final SqlTable<User> USER_TABLE;
	public static final SqlColumn<User, Integer> USER_ID;
	public static final SqlColumn<User, String> USERNAME;
	public static final SqlColumn<User, String> USER_EMAIL;
	public static final SqlColumn<User, Integer> USER_AGE;
	public static final SqlColumn<User, String> USER_BIO;
	public static final SqlColumn<User, Long> USER_VERSION;
	
	// Role table (current schema)
	public static final SqlTable<Role> ROLE_TABLE;
	public static final SqlColumn<Role, Integer> ROLE_ID;
	public static final SqlColumn<Role, String> ROLE_NAME;
	public static final SqlColumn<Role, String> ROLE_DESCRIPTION;
	public static final SqlColumn<Role, Integer> ROLE_PRIORITY;
	public static final SqlColumn<Role, Boolean> ROLE_ACTIVE;
	
	// UserRole junction table (current schema)
	public static final SqlTable<UserRole> USER_ROLE_TABLE;
	public static final SqlColumn<UserRole, Integer> UR_USER_ID;
	public static final SqlColumn<UserRole, Integer> UR_ROLE_ID;
	public static final SqlColumn<UserRole, Instant> UR_ASSIGNED_AT;
	
	static {
		System.setProperty("reflection.exceptions.throw", "true");
		LoggingUtils.initialize(LoggerConfiguration.DEFAULT.disableLogging(LoggingType.FILE).addDefaultLogger(LoggingType.CONSOLE, Level.DEBUG));
		LOGGER = LogManager.getLogger(DatabaseMigrationTest.class);
		
		HikariConfig config = new HikariConfig();
		config.setJdbcUrl("jdbc:postgresql://localhost:3000/test");
		config.setUsername("test");
		config.setPassword("test");
		DATA_SOURCE = new HikariDataSource(config);
		
		// User table (current schema)
		USER_TABLE = SqlTable.create(User.class, "user");
		USER_ID = USER_TABLE.column("id", SqlTypes.INTEGER, User::id, col -> col.primaryKey().notNull().autoIncrement());
		USERNAME = USER_TABLE.column("username", SqlTypes.STRING.configure(SqlParameter.length(64)), User::username, SqlColumnBuilder::notNull);
		USER_EMAIL = USER_TABLE.column("email", SqlTypes.STRING.configure(SqlParameter.length(320)), User::email, col -> col.notNull().unique());
		USER_AGE = USER_TABLE.column("age", SqlTypes.INTEGER, User::age, SqlColumnBuilder::notNull);
		USER_BIO = USER_TABLE.column("bio", SqlTypes.STRING.configure(SqlParameter.length(1024)), User::bio);
		USER_VERSION = USER_TABLE.column("version", SqlTypes.LONG, User::version, col -> col.notNull().defaultValue(0L));
		
		// Role table (current schema)
		ROLE_TABLE = SqlTable.create(Role.class, "role");
		ROLE_ID = ROLE_TABLE.column("id", SqlTypes.INTEGER, Role::id, col -> col.primaryKey().notNull().autoIncrement());
		ROLE_NAME = ROLE_TABLE.column("name", SqlTypes.STRING.configure(SqlParameter.length(64)), Role::name, SqlColumnBuilder::notNull);
		ROLE_DESCRIPTION = ROLE_TABLE.column("description", SqlTypes.STRING.configure(SqlParameter.length(512)), Role::description);
		ROLE_PRIORITY = ROLE_TABLE.column("priority", SqlTypes.INTEGER, Role::priority, col -> col.notNull().defaultValue(0));
		ROLE_ACTIVE = ROLE_TABLE.column("active", SqlTypes.BOOLEAN, Role::active, col -> col.notNull().defaultValue(true));
		
		// UserRole junction table (current schema)
		USER_ROLE_TABLE = SqlTable.create(UserRole.class, "user_role");
		UR_USER_ID = USER_ROLE_TABLE.column("user_id", SqlTypes.INTEGER, UserRole::userId, SqlColumnBuilder::notNull);
		UR_ROLE_ID = USER_ROLE_TABLE.column("role_id", SqlTypes.INTEGER, UserRole::roleId, SqlColumnBuilder::notNull);
		UR_ASSIGNED_AT = USER_ROLE_TABLE.column("assigned_at", SqlTypes.INSTANT.configure(SqlParameter.fractional(3)), UserRole::assignedAt, SqlColumnBuilder::notNull);
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
		// ToDo: SqlMigrationSchema not correctly used
		
		SqlTable<Person> personTable = SqlTable.create(Person.class, "person");
		SqlColumn<Person, Integer> personId = personTable.column("id", SqlTypes.INTEGER, Person::id, col -> col.primaryKey().notNull().autoIncrement());
		SqlColumn<Person, String> firstName = personTable.column("first_name", SqlTypes.STRING.configure(SqlParameter.length(64)), Person::firstName, SqlColumnBuilder::notNull);
		SqlColumn<Person, String> lastName = personTable.column("last_name", SqlTypes.STRING.configure(SqlParameter.length(64)), Person::lastName, SqlColumnBuilder::notNull);
		SqlColumn<Person, String> personEmail = personTable.column("email", SqlTypes.STRING.configure(SqlParameter.length(320)), Person::email, col -> col.notNull().unique());
		SqlColumn<Person, Integer> personAge = personTable.column("age", SqlTypes.INTEGER, Person::age, SqlColumnBuilder::notNull);
		SqlColumn<Person, Long> personVersion = personTable.column("version", SqlTypes.LONG, Person::version, col -> col.notNull().defaultValue(0L));
		SqlColumn<Person, Instant> personCreatedAt = personTable.column("created_at", SqlTypes.INSTANT.configure(SqlParameter.fractional(3)), Person::createdAt, SqlColumnBuilder::notNull);
		
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
			public void up(@NonNull SqlMigrationBuilder builder, @NonNull SqlMigrationSchema schema) throws SqlException {
				builder.createTable(personTable, table -> {
					table.column(personId, SqlTypes.INTEGER, col -> col.notNull().autoIncrement());
					table.column(firstName, SqlTypes.STRING.configure(SqlParameter.length(64)), SqlMigrationColumnBuilder::notNull);
					table.column(lastName, SqlTypes.STRING.configure(SqlParameter.length(64)), SqlMigrationColumnBuilder::notNull);
					table.column(personEmail, SqlTypes.STRING.configure(SqlParameter.length(320)), col -> col.notNull().unique());
					table.column(personAge, SqlTypes.INTEGER, SqlMigrationColumnBuilder::notNull);
					table.column(personVersion, SqlTypes.LONG, col -> col.notNull().defaultValue(0L));
					table.column(personCreatedAt, SqlTypes.INSTANT.configure(SqlParameter.fractional(3)), SqlMigrationColumnBuilder::notNull);
					table.primaryKey(personId);
				});
				
				builder.createTable(ROLE_TABLE, table -> {
					table.column(ROLE_ID, SqlTypes.INTEGER, col -> col.notNull().autoIncrement());
					table.column(ROLE_NAME, SqlTypes.STRING.configure(SqlParameter.length(64)), SqlMigrationColumnBuilder::notNull);
					table.column(ROLE_DESCRIPTION, SqlTypes.STRING.configure(SqlParameter.length(512)));
					table.column(ROLE_PRIORITY, SqlTypes.INTEGER, col -> col.notNull().defaultValue(0));
					table.column(ROLE_ACTIVE, SqlTypes.BOOLEAN, col -> col.notNull().defaultValue(true));
					table.primaryKey(ROLE_ID);
				});
				
				builder.createIndex(personTable, "idx_person_email", idx -> idx.columns(personEmail).unique());
				builder.createIndex(ROLE_TABLE, "idx_role_name", idx -> idx.columns(ROLE_NAME).unique());
				
				builder.data(personTable, provider -> {
					provider.insert(new Person(1, "John", "Doe", "john.doe@example.com", 30, 1, Instant.now())).execute();
					provider.insert(new Person(2, "Jane", "Smith", "jane.smith@example.com", 25, 1, Instant.now())).execute();
				});
				
				builder.data(ROLE_TABLE, provider -> {
					provider.insert(new Role(1, "admin", "Full access", 100, true)).execute();
					provider.insert(new Role(2, "user", "Standard access", 0, true)).execute();
				});
			}
			
			@Override
			public void down(@NonNull SqlMigrationBuilder builder, @NonNull SqlMigrationSchema schema) {
				builder.dropIndex(personTable, "idx_person_email");
				builder.dropIndex(ROLE_TABLE, "idx_role_name");
				builder.dropTable(personTable);
				builder.dropTable(ROLE_TABLE);
			}
		};
	}
	
	static @NonNull SqlMigration addRelationshipsAndAuditLog() {
		// ToDo: SqlMigrationSchema not correctly used
		
		SqlTable<Void> auditLogTable = SqlTable.create(Void.class, "audit_log");
		SqlColumn<Void, Integer> auditId = auditLogTable.column("id", SqlTypes.INTEGER, v -> null, col -> col.primaryKey().notNull().autoIncrement());
		SqlColumn<Void, Integer> auditUserId = auditLogTable.column("user_id", SqlTypes.INTEGER, v -> null, SqlColumnBuilder::notNull);
		SqlColumn<Void, String> auditAction = auditLogTable.column("action", SqlTypes.STRING.configure(SqlParameter.length(64)), v -> null, SqlColumnBuilder::notNull);
		SqlColumn<Void, String> auditDetails = auditLogTable.column("details", SqlTypes.STRING.configure(SqlParameter.length(1024)), v -> null);
		SqlColumn<Void, String> auditIpAddress = auditLogTable.column("ip_address", SqlTypes.STRING.configure(SqlParameter.length(45)), v -> null);
		SqlColumn<Void, Instant> auditTimestamp = auditLogTable.column("timestamp", SqlTypes.INSTANT.configure(SqlParameter.fractional(3)), v -> null, SqlColumnBuilder::notNull);
		
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
			public void up(@NonNull SqlMigrationBuilder builder, @NonNull SqlMigrationSchema schema) throws SqlException {
				SqlTable<Void> personTable = schema.table("person");
				SqlColumn<Void, ?> personId = schema.column("person", "id");
				SqlColumn<Void, Integer> personAge = schema.column("person", "age", Integer.class);
				
				builder.createTable(USER_ROLE_TABLE, table -> {
					table.column(UR_USER_ID, SqlTypes.INTEGER, SqlMigrationColumnBuilder::notNull);
					table.column(UR_ROLE_ID, SqlTypes.INTEGER, SqlMigrationColumnBuilder::notNull);
					table.column(UR_ASSIGNED_AT, SqlTypes.INSTANT.configure(SqlParameter.fractional(3)), SqlMigrationColumnBuilder::notNull);
					table.primaryKey(UR_USER_ID, UR_ROLE_ID);
				});
				
				builder.createTable(auditLogTable, table -> {
					table.column(auditId, SqlTypes.INTEGER, col -> col.notNull().autoIncrement());
					table.column(auditUserId, SqlTypes.INTEGER, SqlMigrationColumnBuilder::notNull);
					table.column(auditAction, SqlTypes.STRING.configure(SqlParameter.length(64)), SqlMigrationColumnBuilder::notNull);
					table.column(auditDetails, SqlTypes.STRING.configure(SqlParameter.length(1024)));
					table.column(auditIpAddress, SqlTypes.STRING.configure(SqlParameter.length(45)));
					table.column(auditTimestamp, SqlTypes.INSTANT.configure(SqlParameter.fractional(3)), SqlMigrationColumnBuilder::notNull);
					table.primaryKey(auditId);
				});
				
				builder.addForeignKey(
					USER_ROLE_TABLE, "fk_user_role_person",
					new SqlColumn[] { UR_USER_ID }, personTable, new SqlColumn[] { personId },
					SqlReferentialAction.CASCADE, SqlReferentialAction.CASCADE
				);
				builder.addForeignKey(
					USER_ROLE_TABLE, "fk_user_role_role",
					new SqlColumn[] { UR_ROLE_ID }, ROLE_TABLE, new SqlColumn[] { ROLE_ID },
					SqlReferentialAction.CASCADE, SqlReferentialAction.CASCADE
				);
				
				builder.addUniqueConstraint(ROLE_TABLE, "uq_role_name", ROLE_NAME);
				builder.addCheckConstraint(personTable, "chk_person_age", Sql.greaterThan(personAge, Sql.of(0)));
				
				builder.createIndex(auditLogTable, "idx_audit_timestamp", idx -> idx.columns(auditTimestamp));
				builder.createIndex(USER_ROLE_TABLE, "idx_user_role_user", idx -> idx.columns(UR_USER_ID));
			}
			
			@Override
			public void down(@NonNull SqlMigrationBuilder builder, @NonNull SqlMigrationSchema schema) throws SqlException {
				SqlTable<Void> personTable = schema.table("person");
				SqlTable<Void> auditLog = schema.table("audit_log");
				
				builder.dropIndex(USER_ROLE_TABLE, "idx_user_role_user");
				builder.dropIndex(auditLog, "idx_audit_timestamp");
				builder.dropConstraint(personTable, "chk_person_age");
				builder.dropConstraint(ROLE_TABLE, "uq_role_name");
				builder.dropConstraint(USER_ROLE_TABLE, "fk_user_role_role");
				builder.dropConstraint(USER_ROLE_TABLE, "fk_user_role_person");
				builder.dropTable(auditLog);
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
			public void up(@NonNull SqlMigrationBuilder builder, @NonNull SqlMigrationSchema schema) throws SqlException {
				SqlTable<Void> personTable = schema.table("person");
				SqlColumn<Void, String> firstName = schema.column("person", "first_name", String.class);
				SqlColumn<Void, ?> lastName = schema.column("person", "last_name");
				SqlColumn<Void, String> personEmail = schema.column("person", "email", String.class);
				SqlColumn<Void, Long> personVersion = schema.column("person", "version", Long.class);
				
				builder.renameTable(personTable, USER_TABLE);
				builder.renameColumn(firstName, USERNAME);
				builder.dropColumn(lastName);
				builder.addColumn(USER_BIO, SqlTypes.STRING.configure(SqlParameter.length(1024)));
				builder.alterColumn(personEmail, col -> col.setNullable(true));
				builder.alterColumn(personVersion, col -> col.setDefault(1L));
				builder.renameIndex(USER_TABLE, "idx_person_email", "idx_user_email");
			}
			
			@Override
			public void down(@NonNull SqlMigrationBuilder builder, @NonNull SqlMigrationSchema schema) throws SqlException {
				SqlTable<Void> userTable = schema.table("user");
				SqlColumn<Void, Long> version = schema.column("user", "version", Long.class);
				SqlColumn<Void, String> email = schema.column("user", "email", String.class);
				
				SqlTable<Void> temp = SqlTable.create(Void.class, "user");
				SqlColumn<Void, String> lastName = temp.column("last_name", SqlTypes.STRING.configure(SqlParameter.length(64)), v -> null, SqlColumnBuilder::notNull);
				SqlColumn<Void, String> firstName = temp.column("first_name", SqlTypes.STRING.configure(SqlParameter.length(64)), v -> null);
				
				SqlTable<Void> personTarget = SqlTable.create(Void.class, "person");
				
				builder.renameIndex(userTable, "idx_user_email", "idx_person_email");
				builder.alterColumn(version, col -> col.setDefault(0L));
				builder.alterColumn(email, col -> col.setNullable(false));
				builder.dropColumn(USER_BIO);
				builder.addColumn(lastName, SqlTypes.STRING.configure(SqlParameter.length(64)), SqlMigrationColumnBuilder::notNull);
				builder.renameColumn(USERNAME, firstName);
				builder.renameTable(USER_TABLE, personTarget);
			}
		};
	}
	
	static @NonNull SqlMigration restructureAndCleanup() {
		// ToDo: SqlMigrationSchema not correctly used
		SqlTable<Void> auditLogTable = SqlTable.create(Void.class, "audit_log");
		SqlColumn<Void, Integer> auditId = auditLogTable.column("id", SqlTypes.INTEGER, v -> null, col -> col.primaryKey().notNull().autoIncrement());
		SqlColumn<Void, Integer> auditUserId = auditLogTable.column("user_id", SqlTypes.INTEGER, v -> null, SqlColumnBuilder::notNull);
		SqlColumn<Void, String> auditAction = auditLogTable.column("action", SqlTypes.STRING.configure(SqlParameter.length(64)), v -> null, SqlColumnBuilder::notNull);
		SqlColumn<Void, String> auditDetails = auditLogTable.column("details", SqlTypes.STRING.configure(SqlParameter.length(1024)), v -> null);
		SqlColumn<Void, String> auditIpAddress = auditLogTable.column("ip_address", SqlTypes.STRING.configure(SqlParameter.length(45)), v -> null);
		SqlColumn<Void, Instant> auditTimestamp = auditLogTable.column("timestamp", SqlTypes.INSTANT.configure(SqlParameter.fractional(3)), v -> null, SqlColumnBuilder::notNull);
		
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
			public void up(@NonNull SqlMigrationBuilder builder, @NonNull SqlMigrationSchema schema) throws SqlException {
				SqlTable<Void> auditLog = schema.table("audit_log");
				
				builder.dropConstraint(USER_TABLE, "chk_person_age");
				builder.addCheckConstraint(USER_TABLE, "chk_user_age", Sql.greaterThanOrEqualTo(USER_AGE, Sql.of(18)));
				builder.addCompositePrimaryKey(USER_ROLE_TABLE, "pk_user_role", UR_USER_ID, UR_ROLE_ID);
				builder.alterColumn(USER_VERSION, SqlMigrationColumnAlter::dropDefault);
				builder.alterColumn(USER_VERSION, col -> col.setNullable(true));
				builder.dropIndex(auditLog, "idx_audit_timestamp");
				builder.dropTable(auditLog);
			}
			
			@Override
			public void down(@NonNull SqlMigrationBuilder builder, @NonNull SqlMigrationSchema schema) throws SqlException {
				builder.createTable(auditLogTable, table -> {
					table.column(auditId, SqlTypes.INTEGER, col -> col.notNull().autoIncrement());
					table.column(auditUserId, SqlTypes.INTEGER, SqlMigrationColumnBuilder::notNull);
					table.column(auditAction, SqlTypes.STRING.configure(SqlParameter.length(64)), SqlMigrationColumnBuilder::notNull);
					table.column(auditDetails, SqlTypes.STRING.configure(SqlParameter.length(1024)));
					table.column(auditIpAddress, SqlTypes.STRING.configure(SqlParameter.length(45)));
					table.column(auditTimestamp, SqlTypes.INSTANT.configure(SqlParameter.fractional(3)), SqlMigrationColumnBuilder::notNull);
					table.primaryKey(auditId);
				});
				builder.createIndex(auditLogTable, "idx_audit_timestamp", idx -> idx.columns(auditTimestamp));
				builder.alterColumn(USER_VERSION, col -> col.setNullable(false));
				builder.alterColumn(USER_VERSION, col -> col.setDefault(1L));
				builder.dropConstraint(USER_ROLE_TABLE, "pk_user_role");
				builder.dropConstraint(USER_TABLE, "chk_user_age");
				builder.addCheckConstraint(USER_TABLE, "chk_person_age", Sql.greaterThan(USER_AGE, Sql.of(0)));
			}
		};
	}
}
