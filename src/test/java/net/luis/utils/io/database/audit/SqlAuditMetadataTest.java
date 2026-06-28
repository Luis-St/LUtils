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

package net.luis.utils.io.database.audit;

import net.luis.utils.io.database.SqlTestFixtures;
import net.luis.utils.io.database.dialect.SqlDialects;
import org.junit.jupiter.api.Test;

import javax.sql.rowset.RowSetProvider;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.OptionalLong;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlAuditMetadata}.<br>
 *
 * @author Luis-St
 */
class SqlAuditMetadataTest {
	
	private static final LocalDateTime NOW = LocalDateTime.of(2024, 1, 1, 12, 0);
	private static final LocalDateTime LATER = LocalDateTime.of(2024, 6, 6, 18, 30);
	
	@Test
	void constructValidMetadata() {
		SqlAuditMetadata metadata = new SqlAuditMetadata(OptionalLong.of(1L), Optional.of(NOW), Optional.of("alice"), Optional.of(LATER), Optional.of("bob"));
		assertEquals(OptionalLong.of(1L), metadata.version());
		assertEquals(Optional.of(NOW), metadata.createdAt());
		assertEquals(Optional.of("alice"), metadata.createdBy());
		assertEquals(Optional.of(LATER), metadata.updatedAt());
		assertEquals(Optional.of("bob"), metadata.updatedBy());
	}
	
	@Test
	void constructWithNullVersion() {
		assertThrows(NullPointerException.class, () -> new SqlAuditMetadata(null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()));
	}
	
	@Test
	void constructWithNullCreatedAt() {
		assertThrows(NullPointerException.class, () -> new SqlAuditMetadata(OptionalLong.empty(), null, Optional.empty(), Optional.empty(), Optional.empty()));
	}
	
	@Test
	void constructWithNullCreatedBy() {
		assertThrows(NullPointerException.class, () -> new SqlAuditMetadata(OptionalLong.empty(), Optional.empty(), null, Optional.empty(), Optional.empty()));
	}
	
	@Test
	void constructWithNullUpdatedAt() {
		assertThrows(NullPointerException.class, () -> new SqlAuditMetadata(OptionalLong.empty(), Optional.empty(), Optional.empty(), null, Optional.empty()));
	}
	
	@Test
	void constructWithNullUpdatedBy() {
		assertThrows(NullPointerException.class, () -> new SqlAuditMetadata(OptionalLong.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null));
	}
	
	@Test
	void readFromWithNullDialect() {
		assertThrows(NullPointerException.class, () -> SqlAuditMetadata.readFrom(null, null, 0, null));
	}
	
	@Test
	void readFromWithNullResultSet() {
		assertThrows(NullPointerException.class, () -> SqlAuditMetadata.readFrom(SqlDialects.H2, null, 0, null));
	}
	
	@Test
	void readFromWithNullConfig() throws Exception {
		ResultSet resultSet = RowSetProvider.newFactory().createCachedRowSet();
		assertThrows(NullPointerException.class, () -> SqlAuditMetadata.readFrom(SqlDialects.H2, resultSet, 0, null));
	}
	
	@Test
	void readFromParsesAllColumns() throws Exception {
		ResultSet resultSet = SqlTestFixtures.resultRow(5L, NOW, "alice", LATER, "bob");
		SqlAuditMetadata metadata = SqlAuditMetadata.readFrom(SqlTestFixtures.DIALECT, resultSet, 1, SqlAuditConfig.DEFAULT);
		assertEquals(5L, metadata.version().getAsLong());
		assertEquals(Optional.of(NOW), metadata.createdAt());
		assertEquals(Optional.of("alice"), metadata.createdBy());
		assertEquals(Optional.of(LATER), metadata.updatedAt());
		assertEquals(Optional.of("bob"), metadata.updatedBy());
	}
	
	@Test
	void readFromReadsColumnsAtStartIndex() throws Exception {
		ResultSet resultSet = SqlTestFixtures.resultRow("ignored", "ignored", 9L, NOW, "alice", LATER, "bob");
		SqlAuditMetadata metadata = SqlAuditMetadata.readFrom(SqlTestFixtures.DIALECT, resultSet, 3, SqlAuditConfig.DEFAULT);
		assertEquals(9L, metadata.version().getAsLong());
		assertEquals(Optional.of(NOW), metadata.createdAt());
		assertEquals(Optional.of("alice"), metadata.createdBy());
		assertEquals(Optional.of(LATER), metadata.updatedAt());
		assertEquals(Optional.of("bob"), metadata.updatedBy());
	}
	
	@Test
	void readFromWithNullVersionYieldsEmptyVersion() throws Exception {
		ResultSet resultSet = SqlTestFixtures.resultRow(null, NOW, "alice", LATER, "bob");
		SqlAuditMetadata metadata = SqlAuditMetadata.readFrom(SqlTestFixtures.DIALECT, resultSet, 1, SqlAuditConfig.DEFAULT);
		assertTrue(metadata.version().isEmpty());
		assertEquals(Optional.of(NOW), metadata.createdAt());
		assertEquals(Optional.of("bob"), metadata.updatedBy());
	}
	
	@Test
	void readFromWithNullOptionalColumnsYieldsEmptyOptionals() throws Exception {
		ResultSet resultSet = SqlTestFixtures.resultRow(3L, null, null, null, null);
		SqlAuditMetadata metadata = SqlAuditMetadata.readFrom(SqlTestFixtures.DIALECT, resultSet, 1, SqlAuditConfig.DEFAULT);
		assertEquals(3L, metadata.version().getAsLong());
		assertTrue(metadata.createdAt().isEmpty());
		assertTrue(metadata.createdBy().isEmpty());
		assertTrue(metadata.updatedAt().isEmpty());
		assertTrue(metadata.updatedBy().isEmpty());
	}
	
	@Test
	void ofWithNegativeVersionEmpty() {
		SqlAuditMetadata metadata = SqlAuditMetadata.of(-1L, null, null, null, null);
		assertTrue(metadata.version().isEmpty());
	}
	
	@Test
	void ofWithZeroVersionPresent() {
		SqlAuditMetadata metadata = SqlAuditMetadata.of(0L, null, null, null, null);
		assertEquals(0L, metadata.version().getAsLong());
	}
	
	@Test
	void ofWithPositiveVersionPresent() {
		SqlAuditMetadata metadata = SqlAuditMetadata.of(7L, null, null, null, null);
		assertEquals(7L, metadata.version().getAsLong());
	}
	
	@Test
	void ofWithNullOptionalFieldsEmpty() {
		SqlAuditMetadata metadata = SqlAuditMetadata.of(1L, null, null, null, null);
		assertTrue(metadata.createdAt().isEmpty());
		assertTrue(metadata.createdBy().isEmpty());
		assertTrue(metadata.updatedAt().isEmpty());
		assertTrue(metadata.updatedBy().isEmpty());
	}
	
	@Test
	void ofWithNonNullOptionalFieldsPresent() {
		SqlAuditMetadata metadata = SqlAuditMetadata.of(1L, NOW, "alice", LATER, "bob");
		assertEquals(Optional.of(NOW), metadata.createdAt());
		assertEquals(Optional.of("alice"), metadata.createdBy());
		assertEquals(Optional.of(LATER), metadata.updatedAt());
		assertEquals(Optional.of("bob"), metadata.updatedBy());
	}
	
	@Test
	void bumpedFromEmptyVersionStartsAtOne() {
		SqlAuditMetadata bumped = SqlAuditMetadata.of(-1L, null, null, null, null).bumped(NOW, "bob");
		assertEquals(1L, bumped.version().getAsLong());
	}
	
	@Test
	void bumpedFromPresentVersionIncrements() {
		SqlAuditMetadata bumped = SqlAuditMetadata.of(4L, null, null, null, null).bumped(NOW, "bob");
		assertEquals(5L, bumped.version().getAsLong());
	}
	
	@Test
	void bumpedWithNullUpdatedFieldsEmpty() {
		SqlAuditMetadata bumped = SqlAuditMetadata.of(1L, null, null, null, null).bumped(null, null);
		assertTrue(bumped.updatedAt().isEmpty());
		assertTrue(bumped.updatedBy().isEmpty());
		assertEquals(2L, bumped.version().getAsLong());
	}
	
	@Test
	void ofPopulatesAllFields() {
		SqlAuditMetadata metadata = SqlAuditMetadata.of(5L, NOW, "alice", LATER, "bob");
		assertEquals(5L, metadata.version().getAsLong());
		assertEquals(Optional.of(NOW), metadata.createdAt());
		assertEquals(Optional.of("alice"), metadata.createdBy());
		assertEquals(Optional.of(LATER), metadata.updatedAt());
		assertEquals(Optional.of("bob"), metadata.updatedBy());
	}
	
	@Test
	void accessorsReturnConstructorValues() {
		SqlAuditMetadata metadata = new SqlAuditMetadata(OptionalLong.of(2L), Optional.of(NOW), Optional.of("alice"), Optional.of(LATER), Optional.of("bob"));
		assertEquals(OptionalLong.of(2L), metadata.version());
		assertEquals(Optional.of(NOW), metadata.createdAt());
		assertEquals(Optional.of("alice"), metadata.createdBy());
		assertEquals(Optional.of(LATER), metadata.updatedAt());
		assertEquals(Optional.of("bob"), metadata.updatedBy());
	}
	
	@Test
	void bumpedPreservesCreatedFields() {
		SqlAuditMetadata metadata = SqlAuditMetadata.of(3L, NOW, "alice", NOW, "old");
		SqlAuditMetadata bumped = metadata.bumped(LATER, "carol");
		assertEquals(Optional.of(NOW), bumped.createdAt());
		assertEquals(Optional.of("alice"), bumped.createdBy());
		assertEquals(Optional.of(LATER), bumped.updatedAt());
		assertEquals(Optional.of("carol"), bumped.updatedBy());
		assertEquals(4L, bumped.version().getAsLong());
	}
	
	@Test
	void bumpedMultipleTimesAccumulatesVersion() {
		SqlAuditMetadata original = SqlAuditMetadata.of(0L, null, null, null, null);
		SqlAuditMetadata twice = original.bumped(NOW, "alice").bumped(LATER, "bob");
		assertEquals(2L, twice.version().getAsLong());
		assertEquals(0L, original.version().getAsLong());
	}
	
	@Test
	void equalMetadataAreEqual() {
		SqlAuditMetadata first = SqlAuditMetadata.of(1L, NOW, "alice", LATER, "bob");
		SqlAuditMetadata second = SqlAuditMetadata.of(1L, NOW, "alice", LATER, "bob");
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
	}
}
