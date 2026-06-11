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

import net.luis.utils.io.database.SqlSession.SqlEntityKey;
import net.luis.utils.io.database.audit.*;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.exception.client.*;
import net.luis.utils.io.database.exception.client.transaction.SqlTransactionStateException;
import net.luis.utils.io.database.table.SqlTable;
import net.luis.utils.io.database.transaction.SqlTransaction;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.util.*;

import static net.luis.utils.io.database.SqlTestFixtures.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlSession}.<br>
 *
 * @author Luis-St
 */
class SqlSessionTest {
	
	private static final Entity ENTITY = new Entity(1, "name");
	private static final SqlAuditMetadata VERSION_ONE = SqlAuditMetadata.of(1, null, null, null, null);
	
	private static SqlDatabase database(DataSource dataSource) throws SqlException {
		return SqlDatabase.builder(dataSource, DIALECT).build();
	}
	
	private static SqlSession session(DataSource dataSource) throws SqlException {
		return database(dataSource).openSession();
	}
	
	private static SqlTable<Entity> entityTable() {
		SqlTable<Entity> table = SqlTable.create(Entity.class, "entities");
		table.column("id", INTEGER_TYPE, Entity::id, builder -> builder.primaryKey());
		table.column("name", STRING_TYPE, Entity::name);
		return table;
	}
	
	private static SqlTable<Entity> auditedEntityTable() {
		SqlTable<Entity> table = SqlTable.audited(Entity.class, "audited_entities");
		table.column("id", INTEGER_TYPE, Entity::id, builder -> builder.primaryKey());
		table.column("name", STRING_TYPE, Entity::name);
		return table;
	}
	
	private static SqlTable<NullableEntity> nullablePrimaryKeyTable() {
		SqlTable<NullableEntity> table = SqlTable.create(NullableEntity.class, "nullable");
		table.column("id", INTEGER_TYPE, NullableEntity::id, builder -> builder.primaryKey());
		table.column("name", STRING_TYPE, NullableEntity::name);
		return table;
	}
	
	private static SqlTable<CompositeEntity> compositeTable() {
		SqlTable<CompositeEntity> table = SqlTable.create(CompositeEntity.class, "composite");
		table.column("first", INTEGER_TYPE, CompositeEntity::first, builder -> builder.primaryKey());
		table.column("second", INTEGER_TYPE, CompositeEntity::second, builder -> builder.primaryKey());
		table.column("name", STRING_TYPE, CompositeEntity::name);
		return table;
	}
	
	private static SqlTable<CompositeNullEntity> compositeNullTable() {
		SqlTable<CompositeNullEntity> table = SqlTable.create(CompositeNullEntity.class, "composite_null");
		table.column("first", INTEGER_TYPE, CompositeNullEntity::first, builder -> builder.primaryKey());
		table.column("second", INTEGER_TYPE, CompositeNullEntity::second, builder -> builder.primaryKey());
		table.column("name", STRING_TYPE, CompositeNullEntity::name);
		return table;
	}
	
	private static String createSchemaSql(String name, boolean ifNotExists) throws SqlException {
		return DIALECT.schemaRenderer().renderCreateSchema(name, ifNotExists).sql();
	}
	
	private static String dropSchemaSql(String name, boolean cascade) throws SqlException {
		return DIALECT.schemaRenderer().renderDropSchema(name, false, cascade).sql();
	}
	
	//region Constructors
	@Test
	void constructSessionViaOpenSession() throws SqlException {
		SqlSession session = session(recordingDataSource());
		assertNotNull(session);
		assertEquals(0, session.trackedCount());
		assertDoesNotThrow(() -> session.createSchema("s"));
	}
	
	@Test
	void constructSessionWithTransactionRegistersListener() throws SqlException {
		SqlDatabase database = database(recordingDataSource());
		try (SqlTransaction transaction = database.beginTransaction()) {
			assertNotNull(database.openSession(transaction));
		}
	}
	//endregion
	
	//region Constructor null handling
	@Test
	void constructWithNullDatabase() {
		assertThrows(NullPointerException.class, () -> new SqlSession(null, SOURCE, TIMEOUT, null));
	}
	
	@Test
	void constructWithNullConnectionSource() throws SqlException {
		SqlDatabase database = database(recordingDataSource());
		assertThrows(NullPointerException.class, () -> new SqlSession(database, null, TIMEOUT, null));
	}
	
	@Test
	void constructWithNullQueryTimeout() throws SqlException {
		SqlDatabase database = database(recordingDataSource());
		assertThrows(NullPointerException.class, () -> new SqlSession(database, SOURCE, null, null));
	}
	//endregion
	
	//region Schema null handling
	@Test
	void createSchemaWithNullName() throws SqlException {
		SqlSession session = session(recordingDataSource());
		assertThrows(NullPointerException.class, () -> session.createSchema(null));
	}
	
	@Test
	void createSchemaIfNotExistsWithNullName() throws SqlException {
		SqlSession session = session(recordingDataSource());
		assertThrows(NullPointerException.class, () -> session.createSchemaIfNotExists(null));
	}
	
	@Test
	void existsSchemaWithNullName() throws SqlException {
		SqlSession session = session(recordingDataSource());
		assertThrows(NullPointerException.class, () -> session.existsSchema(null));
	}
	
	@Test
	void dropSchemaWithNullName() throws SqlException {
		SqlSession session = session(recordingDataSource());
		assertThrows(NullPointerException.class, () -> session.dropSchema(null, false));
	}
	
	@Test
	void tableWithNullTable() throws SqlException {
		SqlSession session = session(recordingDataSource());
		assertThrows(NullPointerException.class, () -> session.table(null));
	}
	
	@Test
	void fromWithNullTable() throws SqlException {
		SqlSession session = session(recordingDataSource());
		assertThrows(NullPointerException.class, () -> session.from(null));
	}
	//endregion
	
	//region Tracking null handling
	@Test
	void trackWithNullTable() throws SqlException {
		SqlSession session = session(recordingDataSource());
		assertThrows(NullPointerException.class, () -> session.track(null, ENTITY, VERSION_ONE));
	}
	
	@Test
	void trackWithNullEntity() throws SqlException {
		SqlSession session = session(recordingDataSource());
		SqlTable<Entity> table = entityTable();
		assertThrows(NullPointerException.class, () -> session.track(table, null, VERSION_ONE));
	}
	
	@Test
	void trackWithNullMetadata() throws SqlException {
		SqlSession session = session(recordingDataSource());
		SqlTable<Entity> table = entityTable();
		assertThrows(NullPointerException.class, () -> session.track(table, ENTITY, null));
	}
	
	@Test
	void attachWithNullTable() throws SqlException {
		SqlSession session = session(recordingDataSource());
		assertThrows(NullPointerException.class, () -> session.attach(null, SqlAudited.of(ENTITY, VERSION_ONE)));
	}
	
	@Test
	void attachWithNullAudited() throws SqlException {
		SqlSession session = session(recordingDataSource());
		SqlTable<Entity> table = entityTable();
		assertThrows(NullPointerException.class, () -> session.attach(table, null));
	}
	
	@Test
	void evictWithNullTable() throws SqlException {
		SqlSession session = session(recordingDataSource());
		assertThrows(NullPointerException.class, () -> session.evict(null, ENTITY));
	}
	
	@Test
	void evictWithNullEntity() throws SqlException {
		SqlSession session = session(recordingDataSource());
		SqlTable<Entity> table = entityTable();
		assertThrows(NullPointerException.class, () -> session.evict(table, null));
	}
	
	@Test
	void isTrackedWithNullTable() throws SqlException {
		SqlSession session = session(recordingDataSource());
		assertThrows(NullPointerException.class, () -> session.isTracked(null, ENTITY));
	}
	
	@Test
	void isTrackedWithNullEntity() throws SqlException {
		SqlSession session = session(recordingDataSource());
		SqlTable<Entity> table = entityTable();
		assertThrows(NullPointerException.class, () -> session.isTracked(table, null));
	}
	
	@Test
	void reloadWithNullTable() throws SqlException {
		SqlSession session = session(recordingDataSource());
		assertThrows(NullPointerException.class, () -> session.reload(null, ENTITY));
	}
	
	@Test
	void reloadWithNullEntity() throws SqlException {
		SqlSession session = session(recordingDataSource());
		SqlTable<Entity> table = entityTable();
		assertThrows(NullPointerException.class, () -> session.reload(table, null));
	}
	
	@Test
	void updateEntityWithNullTable() throws SqlException {
		SqlSession session = session(recordingDataSource());
		assertThrows(NullPointerException.class, () -> session.update(null, ENTITY));
	}
	
	@Test
	void updateEntityWithNullEntity() throws SqlException {
		SqlSession session = session(recordingDataSource());
		SqlTable<Entity> table = entityTable();
		assertThrows(NullPointerException.class, () -> session.update(table, (Entity) null));
	}
	
	@Test
	void updateAuditedWithNullTable() throws SqlException {
		SqlSession session = session(recordingDataSource());
		assertThrows(NullPointerException.class, () -> session.update((SqlTable<Entity>) null, SqlAudited.of(ENTITY, VERSION_ONE)));
	}
	
	@Test
	void updateAuditedWithNullAudited() throws SqlException {
		SqlSession session = session(recordingDataSource());
		SqlTable<Entity> table = entityTable();
		assertThrows(NullPointerException.class, () -> session.update(table, (SqlAudited<Entity>) null));
	}
	//endregion
	
	//region Schema SQLException wrapping and state
	@Test
	void createSchemaWrapsSqlException() throws SqlException {
		SqlSession session = session(failingDataSource());
		assertThrows(SqlException.class, () -> session.createSchema("s"));
	}
	
	@Test
	void createSchemaIfNotExistsWrapsSqlException() throws SqlException {
		SqlSession session = session(failingDataSource());
		assertThrows(SqlException.class, () -> session.createSchemaIfNotExists("s"));
	}
	
	@Test
	void existsSchemaWrapsSqlException() throws SqlException {
		SqlSession session = session(failingDataSource());
		assertThrows(SqlException.class, () -> session.existsSchema("s"));
	}
	
	@Test
	void dropSchemaWrapsSqlException() throws SqlException {
		SqlSession session = session(failingDataSource());
		assertThrows(SqlException.class, () -> session.dropSchema("s", false));
	}
	
	@Test
	void createSchemaAfterCloseThrowsStateException() throws SqlException {
		SqlSession session = session(recordingDataSource());
		session.close();
		assertThrows(SqlTransactionStateException.class, () -> session.createSchema("s"));
	}
	
	@Test
	void createSchemaWithInactiveTransactionThrowsStateException() throws SqlException {
		SqlDatabase database = database(recordingDataSource());
		try (SqlTransaction transaction = database.beginTransaction()) {
			SqlSession session = database.openSession(transaction);
			transaction.commit();
			assertThrows(SqlTransactionStateException.class, () -> session.createSchema("s"));
		}
	}
	//endregion
	
	//region Tracking validation
	@Test
	void trackWithTableWithoutPrimaryKeyThrows() throws SqlException {
		SqlSession session = session(recordingDataSource());
		assertThrows(SqlIncompletePrimaryKeyException.class, () -> session.track(sampleTable(), new Object(), VERSION_ONE));
	}
	
	@Test
	void trackWithNullPrimaryKeyValueThrows() throws SqlException {
		SqlSession session = session(recordingDataSource());
		SqlTable<NullableEntity> table = nullablePrimaryKeyTable();
		assertThrows(SqlIncompletePrimaryKeyException.class, () -> session.track(table, new NullableEntity(null, "name"), VERSION_ONE));
	}
	
	@Test
	void trackWithNullPrimaryKeyValueInCompositeKeyThrows() throws SqlException {
		SqlSession session = session(recordingDataSource());
		SqlTable<CompositeNullEntity> table = compositeNullTable();
		assertThrows(SqlIncompletePrimaryKeyException.class, () -> session.track(table, new CompositeNullEntity(null, 2, "name"), VERSION_ONE));
	}
	
	@Test
	void updateUntrackedEntityThrows() throws SqlException {
		SqlSession session = session(recordingDataSource());
		SqlTable<Entity> table = auditedEntityTable();
		assertThrows(SqlUntrackedEntityException.class, () -> session.update(table, ENTITY));
	}
	
	@Test
	void updateTrackedEntityWithoutVersionThrows() throws SqlException {
		SqlSession session = session(recordingDataSource());
		SqlTable<Entity> table = auditedEntityTable();
		session.track(table, ENTITY, SqlAuditMetadata.of(-1, null, null, null, null));
		assertThrows(SqlUntrackedEntityException.class, () -> session.update(table, ENTITY));
	}
	
	@Test
	void updateAuditedWithoutVersionThrows() throws SqlException {
		SqlSession session = session(recordingDataSource());
		SqlTable<Entity> table = auditedEntityTable();
		SqlAudited<Entity> audited = SqlAudited.of(ENTITY, SqlAuditMetadata.of(-1, null, null, null, null));
		assertThrows(SqlUntrackedEntityException.class, () -> session.update(table, audited));
	}
	
	@Test
	void updateNonAuditedTableThrows() throws SqlException {
		SqlSession session = session(recordingDataSource());
		SqlTable<Entity> table = entityTable();
		session.track(table, ENTITY, VERSION_ONE);
		assertThrows(SqlUntrackedEntityException.class, () -> session.update(table, ENTITY));
	}
	
	@Test
	void updateOptimisticLockWhenNoRowsMatched() throws SqlException {
		SqlSession session = session(recordingDataSource());
		SqlTable<Entity> table = auditedEntityTable();
		session.track(table, ENTITY, VERSION_ONE);
		assertThrows(SqlOptimisticLockException.class, () -> session.update(table, ENTITY));
	}
	
	@Test
	void updateWithNegativeExpectedVersionThrows() throws SqlException {
		SqlSession session = session(recordingDataSource());
		SqlTable<Entity> table = auditedEntityTable();
		session.track(table, ENTITY, new SqlAuditMetadata(OptionalLong.of(-1L), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()));
		assertThrows(IllegalArgumentException.class, () -> session.update(table, ENTITY));
	}
	
	@Test
	void entityKeyWithNullEntityType() {
		assertThrows(NullPointerException.class, () -> new SqlEntityKey(null, 1));
	}
	
	@Test
	void entityKeyWithNullPrimaryKey() {
		assertThrows(NullPointerException.class, () -> new SqlEntityKey(Entity.class, null));
	}
	//endregion
	
	//region Schema execution
	@Test
	void createSchemaExecutesRenderedSql() throws SqlException {
		RecordingDataSource source = recordingDataSource();
		database(source).openSession().createSchema("s");
		assertTrue(source.executedSql().contains(createSchemaSql("s", false)));
	}
	
	@Test
	void createSchemaIfNotExistsExecutesRenderedSql() throws SqlException {
		RecordingDataSource source = recordingDataSource();
		database(source).openSession().createSchemaIfNotExists("s");
		assertTrue(source.executedSql().contains(createSchemaSql("s", true)));
	}
	
	@Test
	void existsSchemaReturnsFalseForEmptyResult() throws SqlException {
		assertFalse(session(recordingDataSource()).existsSchema("x"));
	}
	
	@Test
	void existsSchemaReturnsTrueWhenSchemaPresent() throws SqlException {
		RecordingDataSource source = recordingDataSource();
		source.enqueueResultSet(labeledResultSet(List.of(Map.of("TABLE_SCHEM", "x"))));
		assertTrue(database(source).openSession().existsSchema("x"));
	}
	
	@Test
	void existsSchemaReturnsFalseWhenSchemaAbsent() throws SqlException {
		RecordingDataSource source = recordingDataSource();
		source.enqueueResultSet(labeledResultSet(List.of(Map.of("TABLE_SCHEM", "other"))));
		assertFalse(database(source).openSession().existsSchema("x"));
	}
	
	@Test
	void dropSchemaWithoutCascadeExecutesRenderedSql() throws SqlException {
		RecordingDataSource source = recordingDataSource();
		database(source).openSession().dropSchema("s", false);
		assertTrue(source.executedSql().contains(dropSchemaSql("s", false)));
	}
	
	@Test
	void dropSchemaWithCascadeExecutesRenderedSql() throws SqlException {
		RecordingDataSource source = recordingDataSource();
		database(source).openSession().dropSchema("s", true);
		assertTrue(source.executedSql().contains(dropSchemaSql("s", true)));
	}
	//endregion
	
	//region Providers
	@Test
	void tableReturnsProvider() throws SqlException {
		assertNotNull(session(recordingDataSource()).table(entityTable()));
	}
	
	@Test
	void fromReturnsQueryProviderUsingDatabaseProvider() throws SqlException {
		assertNotNull(session(recordingDataSource()).from(entityTable()));
	}
	
	@Test
	void fromUsesOverrideAuditUserProvider() throws SqlException {
		SqlSession session = database(recordingDataSource()).openSession(SqlAuditUserProvider.of("alice"));
		assertNotNull(session.from(entityTable()));
	}
	//endregion
	
	//region Tracking behaviour
	@Test
	void trackMakesEntityTracked() throws SqlException {
		SqlSession session = session(recordingDataSource());
		SqlTable<Entity> table = entityTable();
		session.track(table, ENTITY, VERSION_ONE);
		assertTrue(session.isTracked(table, ENTITY));
		assertEquals(1, session.trackedCount());
	}
	
	@Test
	void isTrackedReturnsFalseForUntrackedEntity() throws SqlException {
		SqlSession session = session(recordingDataSource());
		SqlTable<Entity> table = entityTable();
		assertFalse(session.isTracked(table, ENTITY));
		assertEquals(0, session.trackedCount());
	}
	
	@Test
	void attachMakesEntityTracked() throws SqlException {
		SqlSession session = session(recordingDataSource());
		SqlTable<Entity> table = entityTable();
		session.attach(table, SqlAudited.of(ENTITY, VERSION_ONE));
		assertTrue(session.isTracked(table, ENTITY));
	}
	
	@Test
	void evictRemovesTrackedEntity() throws SqlException {
		SqlSession session = session(recordingDataSource());
		SqlTable<Entity> table = entityTable();
		session.track(table, ENTITY, VERSION_ONE);
		session.evict(table, ENTITY);
		assertFalse(session.isTracked(table, ENTITY));
		assertEquals(0, session.trackedCount());
	}
	
	@Test
	void evictByKeyRemovesTrackedEntity() throws SqlException {
		SqlSession session = session(recordingDataSource());
		SqlTable<Entity> table = entityTable();
		session.track(table, ENTITY, VERSION_ONE);
		session.evict(new SqlEntityKey(Entity.class, 1));
		assertEquals(0, session.trackedCount());
	}
	
	@Test
	void evictByNullKeyDoesNothing() throws SqlException {
		SqlSession session = session(recordingDataSource());
		SqlTable<Entity> table = entityTable();
		session.track(table, ENTITY, VERSION_ONE);
		assertDoesNotThrow(() -> session.evict(null));
		assertEquals(1, session.trackedCount());
	}
	
	@Test
	void clearRemovesAllTrackedEntities() throws SqlException {
		SqlSession session = session(recordingDataSource());
		SqlTable<Entity> table = entityTable();
		session.track(table, new Entity(1, "a"), VERSION_ONE);
		session.track(table, new Entity(2, "b"), VERSION_ONE);
		session.clear();
		assertEquals(0, session.trackedCount());
	}
	
	@Test
	void trackedCountReflectsTrackedEntities() throws SqlException {
		SqlSession session = session(recordingDataSource());
		SqlTable<Entity> table = entityTable();
		session.track(table, new Entity(1, "a"), VERSION_ONE);
		session.track(table, new Entity(2, "b"), VERSION_ONE);
		assertEquals(2, session.trackedCount());
	}
	
	@Test
	void closeMarksSessionClosedAndClears() throws SqlException {
		SqlSession session = session(recordingDataSource());
		SqlTable<Entity> table = entityTable();
		session.track(table, ENTITY, VERSION_ONE);
		session.close();
		assertEquals(0, session.trackedCount());
		assertThrows(SqlTransactionStateException.class, () -> session.createSchema("s"));
	}
	
	@Test
	void closeIsIdempotent() throws SqlException {
		SqlSession session = session(recordingDataSource());
		assertDoesNotThrow(() -> {
			session.close();
			session.close();
		});
	}
	
	@Test
	void entityKeyEqualityForSameTypeAndKey() {
		SqlEntityKey first = new SqlEntityKey(Entity.class, 1);
		SqlEntityKey second = new SqlEntityKey(Entity.class, 1);
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
	}
	
	@Test
	void entityKeyInequalityForDifferentKey() {
		assertNotEquals(new SqlEntityKey(Entity.class, 1), new SqlEntityKey(Entity.class, 2));
	}
	//endregion
	
	//region Simple inputs
	@Test
	void existsSchemaScansMultipleRowsUntilMatch() throws SqlException {
		RecordingDataSource source = recordingDataSource();
		source.enqueueResultSet(labeledResultSet(List.of(Map.of("TABLE_SCHEM", "a"), Map.of("TABLE_SCHEM", "b"), Map.of("TABLE_SCHEM", "x"))));
		assertTrue(database(source).openSession().existsSchema("x"));
	}
	
	@Test
	void trackOverwritesMetadataForSameKey() throws SqlException {
		SqlSession session = session(recordingDataSource());
		SqlTable<Entity> table = entityTable();
		session.track(table, ENTITY, VERSION_ONE);
		session.track(table, ENTITY, SqlAuditMetadata.of(2, null, null, null, null));
		assertEquals(1, session.trackedCount());
	}
	
	@Test
	void compositePrimaryKeyTracksSuccessfully() throws SqlException {
		SqlSession session = session(recordingDataSource());
		SqlTable<CompositeEntity> table = compositeTable();
		CompositeEntity entity = new CompositeEntity(1, 2, "name");
		session.track(table, entity, VERSION_ONE);
		assertTrue(session.isTracked(table, entity));
	}
	//endregion
	
	//region Transaction listeners and positive update
	@Test
	void transactionRollbackRestoresTrackedSnapshots() throws SqlException {
		RecordingDataSource source = recordingDataSource().rowsAffected(1);
		SqlDatabase database = database(source);
		SqlTable<Entity> table = auditedEntityTable();
		try (SqlTransaction transaction = database.beginTransaction()) {
			SqlSession session = database.openSession(transaction);
			session.track(table, ENTITY, VERSION_ONE);
			session.update(table, ENTITY);
			transaction.rollback();
			assertTrue(session.isTracked(table, ENTITY));
			assertEquals(1, session.trackedCount());
		}
	}
	
	@Test
	void transactionCommitClearsSnapshots() throws SqlException {
		RecordingDataSource source = recordingDataSource().rowsAffected(1);
		SqlDatabase database = database(source);
		SqlTable<Entity> table = auditedEntityTable();
		try (SqlTransaction transaction = database.beginTransaction()) {
			SqlSession session = database.openSession(transaction);
			session.track(table, ENTITY, VERSION_ONE);
			session.update(table, ENTITY);
			transaction.commit();
			assertTrue(session.isTracked(table, ENTITY));
			assertEquals(1, session.trackedCount());
		}
	}
	
	@Test
	void updateBumpsVersionWhenRowMatched() throws SqlException {
		RecordingDataSource source = recordingDataSource().rowsAffected(1);
		SqlSession session = database(source).openSession();
		SqlTable<Entity> table = auditedEntityTable();
		session.track(table, ENTITY, VERSION_ONE);
		SqlAuditMetadata refreshed = session.update(table, ENTITY);
		assertEquals(2, refreshed.version().getAsLong());
		assertTrue(session.isTracked(table, ENTITY));
		assertFalse(source.executedSql().isEmpty());
	}
	
	@Test
	void updateAuditedProceedsWhenVersionPresent() throws SqlException {
		RecordingDataSource source = recordingDataSource().rowsAffected(1);
		SqlSession session = database(source).openSession();
		SqlTable<Entity> table = auditedEntityTable();
		SqlAuditMetadata refreshed = session.update(table, SqlAudited.of(ENTITY, VERSION_ONE));
		assertEquals(2, refreshed.version().getAsLong());
		assertFalse(source.executedSql().isEmpty());
	}
	
	@Test
	void transactionRollbackRemovesPreviouslyUntrackedEntity() throws SqlException {
		RecordingDataSource source = recordingDataSource().rowsAffected(1);
		SqlDatabase database = database(source);
		SqlTable<Entity> table = auditedEntityTable();
		try (SqlTransaction transaction = database.beginTransaction()) {
			SqlSession session = database.openSession(transaction);
			session.update(table, SqlAudited.of(ENTITY, VERSION_ONE));
			transaction.rollback();
			assertFalse(session.isTracked(table, ENTITY));
			assertEquals(0, session.trackedCount());
		}
	}
	
	@Test
	void updateSnapshotsKeyOnlyOnceWithinTransaction() throws SqlException {
		RecordingDataSource source = recordingDataSource().rowsAffected(1);
		SqlDatabase database = database(source);
		SqlTable<Entity> table = auditedEntityTable();
		try (SqlTransaction transaction = database.beginTransaction()) {
			SqlSession session = database.openSession(transaction);
			session.track(table, ENTITY, VERSION_ONE);
			assertEquals(2, session.update(table, ENTITY).version().getAsLong());
			assertEquals(3, session.update(table, ENTITY).version().getAsLong());
			transaction.rollback();
			assertTrue(session.isTracked(table, ENTITY));
			assertEquals(1, session.trackedCount());
		}
	}
	
	@Test
	void updateAfterCloseThrowsStateException() throws SqlException {
		RecordingDataSource source = recordingDataSource().rowsAffected(1);
		SqlSession session = database(source).openSession();
		SqlTable<Entity> table = auditedEntityTable();
		session.track(table, ENTITY, VERSION_ONE);
		session.close();
		assertThrows(SqlTransactionStateException.class, () -> session.update(table, ENTITY));
	}
	
	@Test
	void schemaOperationsAccumulateExecutedSqlAcrossSession() throws SqlException {
		RecordingDataSource source = recordingDataSource();
		SqlSession session = database(source).openSession();
		session.createSchema("a");
		session.createSchemaIfNotExists("b");
		session.dropSchema("c", true);
		assertEquals(List.of(createSchemaSql("a", false), createSchemaSql("b", true), dropSchemaSql("c", true)), source.executedSql());
	}
	//endregion
	
	private record Entity(int id, String name) {}
	
	private record NullableEntity(Integer id, String name) {}
	
	private record CompositeEntity(int first, int second, String name) {}
	
	private record CompositeNullEntity(Integer first, int second, String name) {}
}
