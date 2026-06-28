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

package net.luis.utils.io.database;

import net.luis.utils.io.database.condition.SqlCondition;
import net.luis.utils.io.database.dialect.SqlDialect;
import net.luis.utils.io.database.dialect.SqlDialects;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.expression.SqlExpression;
import net.luis.utils.io.database.expression.SqlValueExpression;
import net.luis.utils.io.database.query.crud.SqlSelectQuery;
import net.luis.utils.io.database.table.SqlColumn;
import net.luis.utils.io.database.table.SqlTable;
import net.luis.utils.io.database.type.SqlType;
import net.luis.utils.io.database.type.SqlTypes;
import net.luis.utils.io.database.type.parameter.SqlParameter;
import org.jspecify.annotations.NonNull;

import javax.sql.DataSource;
import java.io.*;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.*;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.logging.Logger;

/**
 * Shared fixtures for tests under {@code net.luis.utils.io.database}.<br>
 * <p>
 *     Centralizes the construction of the common building blocks (dialect, types, tables, columns,
 *     conditions and expressions) so the individual test classes do not repeat the boilerplate.
 *     Use {@link #DIALECT} unless a test needs a specific dialect, and default to {@link #STRING_TYPE}
 *     and {@link #INTEGER_TYPE} for value types.
 * </p>
 *
 * @author Luis-St
 */
public final class SqlTestFixtures {
	
	/**
	 * The default dialect used by the database tests.<br>
	 */
	public static final SqlDialect DIALECT = SqlDialects.DEFAULT;
	/**
	 * A ready-to-use {@code VARCHAR(255)} string type.<br>
	 */
	public static final SqlType<String> STRING_TYPE = SqlTypes.STRING.configure(SqlParameter.length(255));
	/**
	 * A ready-to-use integer type.<br>
	 */
	public static final SqlType<Integer> INTEGER_TYPE = SqlTypes.INTEGER;
	/**
	 * A query timeout suitable for tests.<br>
	 */
	public static final Duration TIMEOUT = Duration.ofSeconds(5);
	/**
	 * A connection source that always throws when a connection is requested.<br>
	 * <p>
	 *     The database tests run without a real database, so any code path that opens a connection
	 *     must not be exercised; this source makes such an attempt fail fast instead of hanging.
	 * </p>
	 */
	public static final SqlConnectionSource SOURCE = () -> {
		throw new SqlException("No database available in tests");
	};
	
	/**
	 * Private constructor to prevent instantiation.<br>
	 * This is a static fixture holder.<br>
	 */
	private SqlTestFixtures() {}
	
	/**
	 * Creates a fresh sample table named {@code test_table} backed by {@link Object}.<br>
	 * @return A new sample table
	 */
	public static @NonNull SqlTable<Object> sampleTable() {
		return SqlTable.create(Object.class, "test_table");
	}
	
	/**
	 * Creates a fresh integer column named {@code id} on a fresh sample table.<br>
	 * @return A new integer column
	 */
	public static @NonNull SqlColumn<Object, Integer> integerColumn() {
		return sampleTable().column("id", INTEGER_TYPE, object -> 0);
	}
	
	/**
	 * Creates a fresh string column named {@code name} on a fresh sample table.<br>
	 * @return A new string column
	 */
	public static @NonNull SqlColumn<Object, String> stringColumn() {
		return sampleTable().column("name", STRING_TYPE, object -> "test");
	}
	
	/**
	 * Creates a fresh single-column list suitable for index fixtures.<br>
	 * @return A new list holding one integer column
	 */
	public static @NonNull List<SqlColumn<?, ?>> columns() {
		return List.of(integerColumn());
	}
	
	/**
	 * Returns a condition that is always true.<br>
	 * @return The always condition
	 */
	public static @NonNull SqlCondition alwaysCondition() {
		return SqlCondition.always();
	}
	
	/**
	 * Returns a condition that is always false.<br>
	 * @return The never condition
	 */
	public static @NonNull SqlCondition neverCondition() {
		return SqlCondition.never();
	}
	
	/**
	 * Creates a simple string value expression holding {@code "test"}.<br>
	 * @return A new string value expression
	 */
	public static @NonNull SqlExpression<String> stringExpression() {
		return new SqlValueExpression<>("test", STRING_TYPE);
	}
	
	/**
	 * Creates a simple integer value expression holding {@code 0}.<br>
	 * @return A new integer value expression
	 */
	public static @NonNull SqlExpression<Integer> integerExpression() {
		return new SqlValueExpression<>(0, INTEGER_TYPE);
	}
	
	/**
	 * Creates a fresh full-entity select query over a sample table backed by the throwing source.<br>
	 * Suitable for {@code toSql}/rendering assertions and as a subquery fixture.<br>
	 * @return A new select query
	 */
	public static @NonNull SqlSelectQuery<Object> sampleSelect() {
		return new SqlSelectQuery<>(sampleTable(), DIALECT, SOURCE, TIMEOUT, resultSet -> null);
	}
	
	/**
	 * Creates a fresh audited sample table named {@code audited_table} backed by {@link Object}.<br>
	 * @return A new audited sample table
	 */
	public static @NonNull SqlTable<Object> auditedTable() {
		return SqlTable.audited(Object.class, "audited_table");
	}
	
	/**
	 * Creates a {@link DataSource} whose {@link DataSource#getConnection()} always throws a {@link SQLException}.<br>
	 * <p>
	 *     The database tests run without a real database; this source lets exception-wrapping paths
	 *     (which catch {@code SQLException}) be exercised without a live connection.
	 * </p>
	 * @return A new failing data source
	 */
	public static @NonNull DataSource failingDataSource() {
		return new FailingDataSource();
	}
	
	/**
	 * Creates a non-null placeholder {@link Connection} whose methods all throw when invoked.<br>
	 * <p>
	 *     Useful to satisfy a non-null {@code Connection} argument when the method under test is expected
	 *     to fail an earlier argument check before the connection is ever used.
	 * </p>
	 * @return A new placeholder connection
	 */
	public static @NonNull Connection placeholderConnection() {
		return (Connection) Proxy.newProxyInstance(
			Connection.class.getClassLoader(),
			new Class<?>[] { Connection.class },
			(proxy, method, args) -> {
				throw new UnsupportedOperationException("Placeholder connection method '" + method.getName() + "' must not be invoked in tests");
			}
		);
	}
	
	/**
	 * Creates a non-null {@link Connection} that throws {@link SQLException} from every statement-creating
	 * method ({@code createStatement}, {@code prepareStatement}, {@code prepareCall}) while leaving transaction
	 * control and {@code close} as benign no-ops.<br>
	 * <p>
	 *     Unlike {@link #placeholderConnection()} (throws {@link UnsupportedOperationException} on every call),
	 *     this connection fails with the checked {@link SQLException} that the {@code save(Connection, ...)} /
	 *     {@code update(Connection, ...)} / {@code delete(Connection, ...)} overloads actually catch, so their
	 *     {@code catch (SQLException) -> SqlMigrationExecutionException} wrapping branch is exercised. Because
	 *     {@code setAutoCommit}, {@code commit} and {@code rollback} stay benign, it can also be handed out from a
	 *     custom {@link DataSource} to drive the transactional rollback path.
	 * </p>
	 * @return A new connection that fails statement creation with a {@link SQLException}
	 */
	public static @NonNull Connection throwingConnection() {
		return (Connection) Proxy.newProxyInstance(
			Connection.class.getClassLoader(),
			new Class<?>[] { Connection.class },
			(proxy, method, args) -> {
				String name = method.getName();
				switch (name) {
					case "createStatement", "prepareStatement", "prepareCall" -> throw new SQLException("Statement creation failed in tests");
					case "isClosed", "isReadOnly", "getAutoCommit", "isValid", "isWrapperFor" -> {
						return false;
					}
					case "getSchema" -> {
						return "public";
					}
					case "toString" -> {
						return "ThrowingConnection";
					}
					default -> {
						return defaultValue(method.getReturnType());
					}
				}
			}
		);
	}
	
	/**
	 * Creates a {@link RecordingDataSource} that hands out working, no-op {@link Connection}s.<br>
	 * <p>
	 *     Unlike {@link #failingDataSource()} (throws on connect) and {@link #placeholderConnection()} (throws on
	 *     every call), the connections from this source let connection-opening code paths run to completion:
	 *     statements execute as no-ops, transaction control is recorded, and metadata / query look-ups return
	 *     empty {@link ResultSet}s by default. Executed SQL, commits and rollbacks are recorded on the source for
	 *     assertions, and result sets can be pre-queued with {@link RecordingDataSource#enqueueResultSet(ResultSet)}.
	 * </p>
	 * @return A new recording data source
	 */
	public static @NonNull RecordingDataSource recordingDataSource() {
		return new RecordingDataSource();
	}
	
	/**
	 * Creates a {@link RecordingDataSource} that behaves like {@link #recordingDataSource()} but whose connections throw
	 * a {@link SQLException} from {@code commit()}.<br>
	 * <p>
	 *     Statement creation, execution and {@code rollback()} stay benign and recorded, so a transactional operation
	 *     ({@code setAutoCommit(false)} -&gt; work -&gt; {@code commit()}) runs all the way to the commit, fails there, and
	 *     then enters its {@code catch (SQLException) -> rollback() -> rethrow} branch. This is the only way to reach the
	 *     rollback branch: an inner {@code save(Connection, …)} wraps its own {@code SQLException} into a
	 *     {@code SqlMigrationExecutionException} (which is not a {@link SQLException}), so a failing statement alone never
	 *     triggers a rollback. Assert against {@link RecordingDataSource#rollbackCount()} (which still increments here).
	 * </p>
	 * @return A new recording data source whose {@code commit()} fails
	 */
	public static @NonNull RecordingDataSource commitFailingDataSource() {
		return new RecordingDataSource(true, false);
	}
	
	/**
	 * Creates a {@link RecordingDataSource} that behaves like {@link #recordingDataSource()} but whose connections throw
	 * a {@link SQLException} from {@code rollback()}.<br>
	 * <p>
	 *     Lets the rollback-failure paths be exercised: an operation that rolls back while already handling another error
	 *     (for example {@code SqlDatabase.inTransaction(...)} when the action threw) must record the failed rollback as a
	 *     suppressed exception rather than mask the original. Statement creation, execution and {@code commit()} stay
	 *     benign and recorded.
	 * </p>
	 * @return A new recording data source whose {@code rollback()} fails
	 */
	public static @NonNull RecordingDataSource rollbackFailingDataSource() {
		return new RecordingDataSource(false, true);
	}
	
	/**
	 * Creates a {@link DataSource} whose connections are {@link #throwingConnection()} instances.<br>
	 * <p>
	 *     Connecting succeeds, but each connection reports {@code isValid() == false} and throws a {@link SQLException}
	 *     from statement creation. Use it to drive the unhealthy-connection branch of {@code health}/{@code ping}
	 *     (which returns {@code false} <i>without</i> an exception) and the statement-level {@code SQLException}-wrapping
	 *     branch of the schema operations - distinct from {@link #failingDataSource()}, which already fails at connect time.
	 * </p>
	 * @return A new data source handing out throwing connections
	 */
	public static @NonNull DataSource throwingConnectionDataSource() {
		return new ThrowingConnectionDataSource();
	}
	
	/**
	 * Creates a {@link DataSource} that also implements {@link Closeable} and records whether it has been closed.<br>
	 * <p>
	 *     It never opens a connection; it exists to drive the {@code autoCloseDataSource} branch of
	 *     {@link SqlDatabase#close()} that closes a {@link Closeable} source, and - when {@code failOnClose} is set - the
	 *     branch that wraps the close failure into a {@code SqlException}.
	 * </p>
	 * @param failOnClose Whether {@link CloseableDataSource#close()} should throw after recording the close
	 * @return A new closeable data source
	 */
	public static @NonNull CloseableDataSource closeableDataSource(boolean failOnClose) {
		return new CloseableDataSource(failOnClose);
	}
	
	/**
	 * Creates a {@link DataSource} that implements {@link AutoCloseable} (but not {@link Closeable}) and records whether
	 * it has been closed.<br>
	 * <p>
	 *     Mirrors {@link #closeableDataSource(boolean)} for the {@link SqlDatabase#close()} branch that falls through to
	 *     an {@link AutoCloseable} source because the source is not {@link Closeable}.
	 * </p>
	 * @param failOnClose Whether {@link AutoCloseableDataSource#close()} should throw after recording the close
	 * @return A new auto-closeable data source
	 */
	public static @NonNull AutoCloseableDataSource autoCloseableDataSource(boolean failOnClose) {
		return new AutoCloseableDataSource(failOnClose);
	}
	
	/**
	 * Creates a non-null {@link Connection} that serves working no-op statements until the {@code failingStatement}-th
	 * statement-creating call, which throws a {@link SQLException}.<br>
	 * <p>
	 *     Unlike {@link #throwingConnection()} (throws on the <i>first</i> {@code prepareStatement}), this lets the
	 *     earlier statements of a multi-statement method succeed so a <i>later</i> {@code catch (SQLException) -> wrap}
	 *     site can be reached. For example, {@code connectionFailingOnStatement(2)} lets the first prepared statement of
	 *     {@code save(Connection, …)} / {@code delete(Connection, …)} run and makes the second (the check-constraint
	 *     statement) throw, so the "check constraints" wrap branch is exercised. The no-op statements return {@code 0}
	 *     from {@code executeUpdate}, an empty array from {@code executeBatch} and benign defaults elsewhere.
	 * </p>
	 * @param failingStatement The 1-based ordinal of the statement-creating call that throws
	 * @return A new connection that fails on the given statement-creating call
	 * @throws IllegalArgumentException If {@code failingStatement} is less than 1
	 */
	public static @NonNull Connection connectionFailingOnStatement(int failingStatement) {
		if (failingStatement < 1) {
			throw new IllegalArgumentException("Failing statement number must be >= 1, but was " + failingStatement);
		}
		int[] count = { 0 };
		return (Connection) Proxy.newProxyInstance(
			Connection.class.getClassLoader(),
			new Class<?>[] { Connection.class },
			(proxy, method, args) -> {
				String name = method.getName();
				switch (name) {
					case "createStatement", "prepareStatement", "prepareCall" -> {
						if (++count[0] == failingStatement) {
							throw new SQLException("Statement " + failingStatement + " creation failed in tests");
						}
						return noOpStatement();
					}
					case "isClosed", "isReadOnly", "getAutoCommit", "isValid", "isWrapperFor" -> {
						return false;
					}
					case "getSchema" -> {
						return "public";
					}
					case "toString" -> {
						return "FailingOnStatementConnection";
					}
					default -> {
						return defaultValue(method.getReturnType());
					}
				}
			}
		);
	}
	
	/**
	 * Creates a working no-op statement proxy that returns benign results from the execution methods.<br>
	 * @return A new no-op statement
	 */
	private static Object noOpStatement() {
		return Proxy.newProxyInstance(
			CallableStatement.class.getClassLoader(),
			new Class<?>[] { CallableStatement.class },
			(proxy, method, args) -> {
				String name = method.getName();
				switch (name) {
					case "executeBatch" -> {
						return new int[0];
					}
					case "executeLargeBatch" -> {
						return new long[0];
					}
					case "execute" -> {
						return false;
					}
					case "executeQuery", "getResultSet", "getGeneratedKeys" -> {
						return emptyResultSet();
					}
					case "isClosed", "isWrapperFor", "getMoreResults" -> {
						return false;
					}
					case "toString" -> {
						return "NoOpStatement";
					}
					default -> {
						return defaultValue(method.getReturnType());
					}
				}
			}
		);
	}
	
	/**
	 * Creates an empty {@link ResultSet} whose {@code next()} returns {@code false} and whose readers yield defaults.<br>
	 * @return A new empty result set
	 */
	public static @NonNull ResultSet emptyResultSet() {
		return (ResultSet) Proxy.newProxyInstance(
			ResultSet.class.getClassLoader(),
			new Class<?>[] { ResultSet.class },
			(proxy, method, args) -> {
				String name = method.getName();
				if ("next".equals(name) || "wasNull".equals(name) || "isClosed".equals(name) || "isWrapperFor".equals(name)) {
					return false;
				}
				if ("close".equals(name)) {
					return null;
				}
				if ("toString".equals(name)) {
					return "EmptyResultSet";
				}
				return defaultValue(method.getReturnType());
			}
		);
	}
	
	/**
	 * Returns a benign default for the given (possibly primitive) return type, so unhandled proxy methods do not
	 * fail with a {@code null}-unboxing error.<br>
	 * @param type The method return type
	 * @return {@code false}/{@code 0}/{@code '\0'} for primitives, {@code null} otherwise
	 */
	private static Object defaultValue(@NonNull Class<?> type) {
		if (type == boolean.class) {
			return false;
		}
		if (type == byte.class) {
			return (byte) 0;
		}
		if (type == short.class) {
			return (short) 0;
		}
		if (type == int.class) {
			return 0;
		}
		if (type == long.class) {
			return 0L;
		}
		if (type == float.class) {
			return 0.0F;
		}
		if (type == double.class) {
			return 0.0D;
		}
		if (type == char.class) {
			return '\0';
		}
		return null;
	}
	
	/**
	 * Creates a fake single-row {@link ResultSet} that serves the given column values by 1-based index.<br>
	 * <p>
	 *     Only the value-reading methods that {@code SqlType} uses are implemented: {@code getObject(int, Class)}
	 *     (returning the value cast to the requested type, or {@code null}), {@code getObject(int)} and {@code wasNull()}.
	 *     This mirrors the existing hand-written fakes in the database tests; the default dialect reads values through
	 *     {@code getObject(int, Class)}, which the JDK reference {@code CachedRowSet} does not support for non-temporal types.
	 *     Any other invoked method throws {@link UnsupportedOperationException}.
	 * </p>
	 * @param columnValues The values for the single row, one per column (1-based when accessed)
	 * @return A new fake result set positioned on the single row
	 * @throws NullPointerException If the column values array is null
	 */
	public static @NonNull ResultSet resultRow(Object @NonNull ... columnValues) {
		Objects.requireNonNull(columnValues, "Column values must not be null");
		Object[] values = columnValues.clone();
		boolean[] wasNull = { false };
		return (ResultSet) Proxy.newProxyInstance(
			ResultSet.class.getClassLoader(),
			new Class<?>[] { ResultSet.class },
			(proxy, method, args) -> {
				String name = method.getName();
				if ("getObject".equals(name) && args != null && args.length == 2 && args[1] instanceof Class<?> type) {
					Object value = values[(Integer) args[0] - 1];
					wasNull[0] = value == null;
					return value == null ? null : type.cast(value);
				}
				if ("getObject".equals(name) && args != null && args.length == 1) {
					Object value = values[(Integer) args[0] - 1];
					wasNull[0] = value == null;
					return value;
				}
				if ("wasNull".equals(name)) {
					return wasNull[0];
				}
				if ("toString".equals(name)) {
					return "FakeResultSet";
				}
				throw new UnsupportedOperationException("Fake result set method '" + name + "' must not be invoked in tests");
			}
		);
	}
	
	/**
	 * Creates a multi-row {@link ResultSet} that serves column values <b>by label</b>, as the schema- and
	 * migration-store {@code load} paths read them.<br>
	 * <p>
	 *     Each map in {@code rows} is one row; {@code next()} advances through them in order. The value readers
	 *     ({@code getString}, {@code getInt}, {@code getLong}, {@code getBoolean}, {@code getTimestamp},
	 *     {@code getObject}) take a single column-label argument and return the current row's value for that label.
	 *     A column that is <b>absent from the map (or mapped to {@code null})</b> is treated as SQL {@code NULL}:
	 *     numeric/boolean readers yield {@code 0}/{@code false}, object readers yield {@code null}, and the
	 *     immediately following {@link ResultSet#wasNull()} returns {@code true}. This lets a row use
	 *     {@code Map.of(...)} and simply omit nullable columns (e.g. omit {@code "length"} so {@code getInt("length")}
	 *     plus {@code wasNull()} reconstructs a missing parameter). {@code getTimestamp} accepts a {@link Timestamp}
	 *     or an {@link Instant} value. Any other invoked method returns a benign default.
	 * </p>
	 * @param rows The rows to serve, each a label-to-value map ({@code null}/absent value means SQL {@code NULL})
	 * @return A new result set positioned before the first row
	 * @throws NullPointerException If the rows list or any row is null
	 */
	public static @NonNull ResultSet labeledResultSet(@NonNull List<? extends Map<String, ?>> rows) {
		Objects.requireNonNull(rows, "Rows must not be null");
		List<Map<String, Object>> copy = new ArrayList<>();
		for (Map<String, ?> row : rows) {
			copy.add(new HashMap<>(Objects.requireNonNull(row, "Row must not be null")));
		}
		int[] cursor = { -1 };
		boolean[] wasNull = { false };
		return (ResultSet) Proxy.newProxyInstance(
			ResultSet.class.getClassLoader(),
			new Class<?>[] { ResultSet.class },
			(proxy, method, args) -> {
				String name = method.getName();
				switch (name) {
					case "next" -> {
						cursor[0]++;
						return cursor[0] < copy.size();
					}
					case "wasNull" -> {
						return wasNull[0];
					}
					case "close" -> {
						return null;
					}
					case "isClosed" -> {
						return cursor[0] >= copy.size();
					}
					case "isWrapperFor" -> {
						return false;
					}
					case "toString" -> {
						return "LabeledResultSet";
					}
				}
				if (args != null && args.length == 1 && args[0] instanceof String column) {
					Object value = copy.get(cursor[0]).get(column);
					wasNull[0] = value == null;
					return switch (name) {
						case "getString" -> value == null ? null : (String) value;
						case "getInt" -> value == null ? 0 : ((Number) value).intValue();
						case "getLong" -> value == null ? 0L : ((Number) value).longValue();
						case "getBoolean" -> value != null && (Boolean) value;
						case "getTimestamp" -> toTimestamp(value);
						case "getObject" -> value;
						default -> defaultValue(method.getReturnType());
					};
				}
				return defaultValue(method.getReturnType());
			}
		);
	}
	
	/**
	 * Converts a benign timestamp value (a {@link Timestamp}, an {@link Instant} or {@code null}) into a
	 * {@link Timestamp} for the labeled result set.<br>
	 * @param value The value to convert
	 * @return The timestamp, or {@code null} if the value is null
	 * @throws IllegalArgumentException If the value is neither a timestamp nor an instant
	 */
	private static Timestamp toTimestamp(Object value) {
		if (value == null) {
			return null;
		}
		if (value instanceof Timestamp timestamp) {
			return timestamp;
		}
		if (value instanceof Instant instant) {
			return Timestamp.from(instant);
		}
		throw new IllegalArgumentException("Cannot convert " + value.getClass().getName() + " to a timestamp");
	}
	
	/**
	 * Creates a {@link ResultSet} whose {@code next()} throws a {@link SQLException} on the first call.<br>
	 * <p>
	 *     Lets a row-iteration {@code while (rs.next()) { … }} loop fail mid-query so the surrounding
	 *     {@code catch (SQLException) -> wrap} branch is reached (e.g. the second, check-constraint query of
	 *     {@code load(Version)} after the column query has returned rows). Enqueue it with
	 *     {@link RecordingDataSource#enqueueResultSet(ResultSet)} so the next {@code executeQuery} serves it.
	 *     {@code close()} is a benign no-op so try-with-resources unwinds cleanly.
	 * </p>
	 * @return A new result set that throws from {@code next()}
	 */
	public static @NonNull ResultSet throwingResultSet() {
		return (ResultSet) Proxy.newProxyInstance(
			ResultSet.class.getClassLoader(),
			new Class<?>[] { ResultSet.class },
			(proxy, method, args) -> {
				String name = method.getName();
				switch (name) {
					case "next" -> {
						throw new SQLException("Result set iteration failed in tests");
					}
					case "isClosed", "wasNull", "isWrapperFor" -> {
						return false;
					}
					case "close" -> {
						return null;
					}
					case "toString" -> {
						return "ThrowingResultSet";
					}
					default -> {
						return defaultValue(method.getReturnType());
					}
				}
			}
		);
	}
	
	/**
	 * A {@link DataSource} whose {@link Connection}s are working no-op recorders.<br>
	 * <p>
	 *     Every connection it hands out shares this source's recording state, so SQL executed across the several
	 *     short-lived connections a single operation opens is collected in one place. Statement execution is a
	 *     no-op; {@code executeQuery}, {@code getGeneratedKeys} and all {@link DatabaseMetaData} {@code get*}
	 *     look-ups return the next pre-queued {@link ResultSet} or an empty one; {@code commit}/{@code rollback}
	 *     are counted. The connection, statements, metadata and the empty result sets are {@link Proxy}-based and
	 *     return benign defaults for any method not explicitly handled.
	 * </p>
	 */
	public static final class RecordingDataSource implements DataSource {
		
		private final List<String> executedSql = new ArrayList<>();
		private final Deque<ResultSet> queuedResultSets = new ArrayDeque<>();
		private final boolean failOnCommit;
		private final boolean failOnRollback;
		private int commitCount;
		private int rollbackCount;
		private int rowsAffected;
		
		private RecordingDataSource() {
			this(false, false);
		}
		
		private RecordingDataSource(boolean failOnCommit, boolean failOnRollback) {
			this.failOnCommit = failOnCommit;
			this.failOnRollback = failOnRollback;
		}
		
		/**
		 * Sets the row count that {@code executeUpdate}/{@code executeLargeUpdate} report on connections from this source.<br>
		 * <p>
		 *     Defaults to {@code 0}, which makes any {@code execute()}-counting path (notably an audited update) see
		 *     "no rows affected" and take its zero-rows branch. Set a positive value to drive the matched-row / success branch.
		 * </p>
		 * @param rowsAffected The number of rows {@code executeUpdate} should report as affected
		 * @return This source, for chaining
		 */
		public @NonNull RecordingDataSource rowsAffected(int rowsAffected) {
			this.rowsAffected = rowsAffected;
			return this;
		}
		
		/**
		 * Returns an immutable copy of every SQL string passed to {@code execute*}, {@code prepareStatement} and
		 * {@code addBatch}, across all connections handed out by this source.<br>
		 * @return The recorded SQL in execution order
		 */
		public @NonNull List<String> executedSql() {
			return List.copyOf(this.executedSql);
		}
		
		/**
		 * Returns how many times {@code commit()} was called on connections from this source.<br>
		 * @return The commit count
		 */
		public int commitCount() {
			return this.commitCount;
		}
		
		/**
		 * Returns how many times {@code rollback()} was called on connections from this source.<br>
		 * @return The rollback count
		 */
		public int rollbackCount() {
			return this.rollbackCount;
		}
		
		/**
		 * Pre-queues a {@link ResultSet} to be returned by the next query / metadata look-up.<br>
		 * @param resultSet The result set to serve next
		 * @throws NullPointerException If the result set is null
		 */
		public void enqueueResultSet(@NonNull ResultSet resultSet) {
			this.queuedResultSets.add(Objects.requireNonNull(resultSet, "Result set must not be null"));
		}
		
		private @NonNull ResultSet nextResultSet() {
			ResultSet queued = this.queuedResultSets.poll();
			return queued != null ? queued : emptyResultSet();
		}
		
		@Override
		public @NonNull Connection getConnection() {
			return (Connection) Proxy.newProxyInstance(
				Connection.class.getClassLoader(),
				new Class<?>[] { Connection.class },
				this::handleConnection
			);
		}
		
		@Override
		public @NonNull Connection getConnection(String username, String password) {
			return this.getConnection();
		}
		
		private Object handleConnection(Object proxy, Method method, Object[] args) throws SQLException {
			String name = method.getName();
			switch (name) {
				case "createStatement" -> {
					return this.statementProxy(null);
				}
				case "prepareStatement", "prepareCall" -> {
					return this.statementProxy(args != null && args.length > 0 && args[0] instanceof String sql ? sql : null);
				}
				case "getMetaData" -> {
					return this.metaDataProxy();
				}
				case "commit" -> {
					if (this.failOnCommit) {
						throw new SQLException("Commit failed in tests");
					}
					this.commitCount++;
					return null;
				}
				case "rollback" -> {
					if (this.failOnRollback) {
						throw new SQLException("Rollback failed in tests");
					}
					this.rollbackCount++;
					return null;
				}
				case "isValid" -> {
					return true;
				}
				case "getSchema" -> {
					return "public";
				}
				case "getAutoCommit", "isClosed", "isReadOnly", "isWrapperFor" -> {
					return false;
				}
				case "toString" -> {
					return "RecordingConnection";
				}
				default -> {
					return defaultValue(method.getReturnType());
				}
			}
		}
		
		private Object statementProxy(String boundSql) {
			return Proxy.newProxyInstance(
				CallableStatement.class.getClassLoader(),
				new Class<?>[] { CallableStatement.class },
				(proxy, method, args) -> this.handleStatement(boundSql, method, args)
			);
		}
		
		private Object handleStatement(String boundSql, Method method, Object[] args) {
			String name = method.getName();
			switch (name) {
				case "execute" -> {
					this.record(boundSql, args);
					return false;
				}
				case "executeUpdate" -> {
					this.record(boundSql, args);
					return this.rowsAffected;
				}
				case "executeLargeUpdate" -> {
					this.record(boundSql, args);
					return (long) this.rowsAffected;
				}
				case "executeQuery" -> {
					this.record(boundSql, args);
					return this.nextResultSet();
				}
				case "addBatch" -> {
					this.record(boundSql, args);
					return null;
				}
				case "executeBatch" -> {
					return new int[0];
				}
				case "executeLargeBatch" -> {
					return new long[0];
				}
				case "getGeneratedKeys", "getResultSet" -> {
					return this.nextResultSet();
				}
				case "getUpdateCount" -> {
					return -1;
				}
				case "getMoreResults", "isClosed", "isWrapperFor" -> {
					return false;
				}
				case "toString" -> {
					return "RecordingStatement";
				}
				default -> {
					return defaultValue(method.getReturnType());
				}
			}
		}
		
		private void record(String boundSql, Object[] args) {
			if (args != null && args.length > 0 && args[0] instanceof String sql) {
				this.executedSql.add(sql);
			} else if (boundSql != null) {
				this.executedSql.add(boundSql);
			}
		}
		
		private Object metaDataProxy() {
			return Proxy.newProxyInstance(
				DatabaseMetaData.class.getClassLoader(),
				new Class<?>[] { DatabaseMetaData.class },
				(proxy, method, args) -> this.handleMetaData(method)
			);
		}
		
		private Object handleMetaData(Method method) {
			String name = method.getName();
			if (name.startsWith("get") && method.getReturnType() == ResultSet.class) {
				return this.nextResultSet();
			}
			switch (name) {
				case "getConnection" -> {
					return this.getConnection();
				}
				case "getIdentifierQuoteString" -> {
					return "\"";
				}
				case "getDatabaseProductName" -> {
					return "RecordingDB";
				}
				case "getURL" -> {
					return "jdbc:recording:test";
				}
				case "isWrapperFor" -> {
					return false;
				}
				case "toString" -> {
					return "RecordingDatabaseMetaData";
				}
				default -> {
					return defaultValue(method.getReturnType());
				}
			}
		}
		
		@Override
		public PrintWriter getLogWriter() {
			throw new UnsupportedOperationException("Not used in tests");
		}
		
		@Override
		public void setLogWriter(PrintWriter out) {
			throw new UnsupportedOperationException("Not used in tests");
		}
		
		@Override
		public int getLoginTimeout() {
			throw new UnsupportedOperationException("Not used in tests");
		}
		
		@Override
		public void setLoginTimeout(int seconds) {
			throw new UnsupportedOperationException("Not used in tests");
		}
		
		@Override
		public Logger getParentLogger() {
			throw new UnsupportedOperationException("Not used in tests");
		}
		
		@Override
		public <T> T unwrap(Class<T> iface) {
			throw new UnsupportedOperationException("Not used in tests");
		}
		
		@Override
		public boolean isWrapperFor(Class<?> iface) {
			throw new UnsupportedOperationException("Not used in tests");
		}
	}
	
	/**
	 * Base for the small test {@link DataSource}s: every {@link DataSource} method that the database tests never use
	 * throws {@link UnsupportedOperationException}, leaving subclasses to implement only the {@code getConnection}
	 * overloads (and any closing behaviour).<br>
	 */
	private abstract static class AbstractTestDataSource implements DataSource {
		
		private static @NonNull UnsupportedOperationException unsupported() {
			return new UnsupportedOperationException("Not used in tests");
		}
		
		@Override
		public PrintWriter getLogWriter() {
			throw unsupported();
		}
		
		@Override
		public void setLogWriter(PrintWriter out) {
			throw unsupported();
		}
		
		@Override
		public int getLoginTimeout() {
			throw unsupported();
		}
		
		@Override
		public void setLoginTimeout(int seconds) {
			throw unsupported();
		}
		
		@Override
		public Logger getParentLogger() {
			throw unsupported();
		}
		
		@Override
		public <T> T unwrap(Class<T> iface) {
			throw unsupported();
		}
		
		@Override
		public boolean isWrapperFor(Class<?> iface) {
			throw unsupported();
		}
	}
	
	/**
	 * A {@link DataSource} that fails fast with a {@link SQLException} when a connection is requested.<br>
	 */
	private static final class FailingDataSource extends AbstractTestDataSource {
		
		@Override
		public Connection getConnection() throws SQLException {
			throw new SQLException("No database available in tests");
		}
		
		@Override
		public Connection getConnection(String username, String password) throws SQLException {
			throw new SQLException("No database available in tests");
		}
	}
	
	/**
	 * A {@link DataSource} that hands out {@link #throwingConnection()} connections: connecting succeeds, but the
	 * connection is unhealthy and fails statement creation with a {@link SQLException}.<br>
	 */
	private static final class ThrowingConnectionDataSource extends AbstractTestDataSource {
		
		@Override
		public @NonNull Connection getConnection() {
			return throwingConnection();
		}
		
		@Override
		public @NonNull Connection getConnection(String username, String password) {
			return throwingConnection();
		}
	}
	
	/**
	 * A {@link DataSource} that also implements {@link Closeable} and records whether it has been closed.<br>
	 * <p>
	 *     It never opens a real connection ({@code getConnection} is unused by the close paths); it exists to drive the
	 *     {@code autoCloseDataSource} branch of {@link SqlDatabase#close()} that closes a {@link Closeable} source, and,
	 *     when constructed with {@code failOnClose}, the branch that wraps the close failure.
	 * </p>
	 */
	public static final class CloseableDataSource extends AbstractTestDataSource implements Closeable {
		
		private final boolean failOnClose;
		private boolean closed;
		
		private CloseableDataSource(boolean failOnClose) {
			this.failOnClose = failOnClose;
		}
		
		/**
		 * Returns whether {@link #close()} has been invoked.<br>
		 * @return {@code true} once closed
		 */
		public boolean closed() {
			return this.closed;
		}
		
		@Override
		public void close() throws IOException {
			this.closed = true;
			if (this.failOnClose) {
				throw new IOException("Data source close failed in tests");
			}
		}
		
		@Override
		public @NonNull Connection getConnection() {
			throw new UnsupportedOperationException("Not used in tests");
		}
		
		@Override
		public @NonNull Connection getConnection(String username, String password) {
			throw new UnsupportedOperationException("Not used in tests");
		}
	}
	
	/**
	 * A {@link DataSource} that implements {@link AutoCloseable} (but not {@link Closeable}) and records whether it has
	 * been closed.<br>
	 * <p>
	 *     Mirrors {@link CloseableDataSource} for the {@link SqlDatabase#close()} branch that falls through to an
	 *     {@link AutoCloseable} source because the source is not {@link Closeable}.
	 * </p>
	 */
	public static final class AutoCloseableDataSource extends AbstractTestDataSource implements AutoCloseable {
		
		private final boolean failOnClose;
		private boolean closed;
		
		private AutoCloseableDataSource(boolean failOnClose) {
			this.failOnClose = failOnClose;
		}
		
		/**
		 * Returns whether {@link #close()} has been invoked.<br>
		 * @return {@code true} once closed
		 */
		public boolean closed() {
			return this.closed;
		}
		
		@Override
		public void close() throws Exception {
			this.closed = true;
			if (this.failOnClose) {
				throw new Exception("Data source close failed in tests");
			}
		}
		
		@Override
		public @NonNull Connection getConnection() {
			throw new UnsupportedOperationException("Not used in tests");
		}
		
		@Override
		public @NonNull Connection getConnection(String username, String password) {
			throw new UnsupportedOperationException("Not used in tests");
		}
	}
}
