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

package net.luis.utils.io.database.integration.reflection;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.luis.utils.io.database.SqlDatabase;
import net.luis.utils.io.database.dialect.*;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.table.*;
import net.luis.utils.io.database.type.SqlTypes;
import net.luis.utils.io.database.type.parameter.SqlParameter;
import org.jspecify.annotations.NonNull;
import org.testcontainers.containers.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Luis-St
 *
 */

public final class SqlEngineFixture {
	
	private static final List<Refl> SEED = List.of(
		new Refl(1, "Alice", 30, 1.5, true, LocalDate.of(2024, 3, 15)),
		new Refl(2, "bob", 25, 2.5, true, LocalDate.of(2024, 1, 10)),
		new Refl(3, "Charlie", 40, 4.0, false, LocalDate.of(2024, 6, 20))
	);
	public static final SqlTable<Refl> REFL = SqlTable.create(Refl.class, "it_refl");
	public static final SqlColumn<Refl, Integer> ID = REFL.column("id", SqlTypes.INTEGER, Refl::id, col -> col.primaryKey().notNull());
	public static final SqlColumn<Refl, String> NAME = REFL.column("name", SqlTypes.STRING.configure(SqlParameter.length(64)), Refl::name, SqlColumnBuilder::notNull);
	public static final SqlColumn<Refl, Integer> AGE = REFL.column("age", SqlTypes.INTEGER, Refl::age, SqlColumnBuilder::notNull);
	public static final SqlColumn<Refl, Double> RATIO = REFL.column("ratio", SqlTypes.DOUBLE, Refl::ratio, SqlColumnBuilder::notNull);
	public static final SqlColumn<Refl, Boolean> ACTIVE = REFL.column("active", SqlTypes.BOOLEAN, Refl::active, SqlColumnBuilder::notNull);
	public static final SqlColumn<Refl, LocalDate> AT = REFL.column("at", SqlTypes.LOCAL_DATE, Refl::at, SqlColumnBuilder::notNull);
	public static final long SEED_ROWS = 3L;
	
	private SqlEngineFixture() {}
	
	public static @NonNull List<Engine> startEngines() throws IOException {
		PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");
		MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.4");
		MariaDBContainer<?> mariadb = new MariaDBContainer<>("mariadb:11.4");
		MSSQLServerContainer<?> mssql = new MSSQLServerContainer<>("mcr.microsoft.com/mssql/server:2022-latest").acceptLicense();
		List<JdbcDatabaseContainer<?>> containers = List.of(postgres, mysql, mariadb, mssql);
		containers.parallelStream().forEach(JdbcDatabaseContainer::start);
		
		List<Engine> engines = new ArrayList<>();
		engines.add(new Engine("PostgreSQL", fromContainer(postgres, SqlDialects.POSTGRESQL), postgres, null));
		engines.add(new Engine("MySQL", fromContainer(mysql, SqlDialects.MYSQL), mysql, null));
		engines.add(new Engine("MariaDB", fromContainer(mariadb, SqlDialects.MARIA_DB), mariadb, null));
		engines.add(new Engine("SQLServer", fromContainer(mssql, SqlDialects.SQL_SERVER), mssql, null));
		engines.add(new Engine("H2", fromUrl("jdbc:h2:mem:lutils_refl;DB_CLOSE_DELAY=-1", "sa", "", SqlDialects.H2, 4), null, null));
		
		Path sqliteFile = Files.createTempFile("lutils_refl", ".db");
		engines.add(new Engine("SQLite", fromUrl("jdbc:sqlite:" + sqliteFile, "", "", SqlDialects.SQLITE, 1), null, sqliteFile));
		return engines;
	}
	
	public static void stopEngines(@NonNull List<Engine> engines) {
		for (Engine engine : engines) {
			try {
				engine.database().close();
			} catch (SqlException ignored) {}
			if (engine.container() != null) {
				engine.container().stop();
			}
			if (engine.sqliteFile() != null) {
				try {
					Files.deleteIfExists(engine.sqliteFile());
				} catch (IOException ignored) {}
			}
		}
	}
	
	public static void resetAndSeed(@NonNull SqlDatabase database) throws SqlException {
		database.table(REFL).dropIfExists();
		database.table(REFL).create();
		database.from(REFL).insert(SEED).execute();
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
	
	public record Engine(@NonNull String name, @NonNull SqlDatabase database, JdbcDatabaseContainer<?> container, Path sqliteFile) {
		
		public boolean supports(@NonNull SqlFeature feature) {
			return this.database.getDialect().isFeatureSupported(feature);
		}
		
		@Override
		public @NonNull String toString() {
			return this.name;
		}
	}
	
	public record Refl(int id, @NonNull String name, int age, double ratio, boolean active, @NonNull LocalDate at) {}
}
