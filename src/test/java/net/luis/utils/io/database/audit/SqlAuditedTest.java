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

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlAudited}.<br>
 *
 * @author Luis-St
 */
class SqlAuditedTest {
	
	private static final LocalDateTime NOW = LocalDateTime.of(2024, 1, 1, 12, 0);
	private static final LocalDateTime LATER = LocalDateTime.of(2024, 6, 6, 18, 30);
	
	@Test
	void constructValidAudited() {
		SqlAuditMetadata metadata = SqlAuditMetadata.of(1L, NOW, "alice", null, null);
		SqlAudited<String> audited = new SqlAudited<>("entity", metadata);
		assertEquals("entity", audited.entity());
		assertSame(metadata, audited.audit());
	}
	
	@Test
	void constructWithNullEntity() {
		SqlAuditMetadata metadata = SqlAuditMetadata.of(1L, NOW, "alice", null, null);
		assertThrows(NullPointerException.class, () -> new SqlAudited<>(null, metadata));
	}
	
	@Test
	void constructWithNullAudit() {
		assertThrows(NullPointerException.class, () -> new SqlAudited<>("entity", null));
	}
	
	@Test
	void ofWithNullEntity() {
		SqlAuditMetadata metadata = SqlAuditMetadata.of(1L, NOW, "alice", null, null);
		assertThrows(NullPointerException.class, () -> SqlAudited.of(null, metadata));
	}
	
	@Test
	void ofWithNullAudit() {
		assertThrows(NullPointerException.class, () -> SqlAudited.of("entity", null));
	}
	
	@Test
	void versionDelegatesPresent() {
		SqlAudited<String> audited = new SqlAudited<>("entity", SqlAuditMetadata.of(5L, NOW, "alice", null, null));
		assertEquals(5L, audited.version().getAsLong());
	}
	
	@Test
	void versionDelegatesEmpty() {
		SqlAudited<String> audited = new SqlAudited<>("entity", SqlAuditMetadata.of(-1L, NOW, "alice", null, null));
		assertTrue(audited.version().isEmpty());
	}
	
	@Test
	void createdByDelegatesPresentAndEmpty() {
		SqlAudited<String> present = new SqlAudited<>("entity", SqlAuditMetadata.of(1L, NOW, "alice", null, null));
		assertEquals(Optional.of("alice"), present.createdBy());
		
		SqlAudited<String> empty = new SqlAudited<>("entity", SqlAuditMetadata.of(1L, NOW, null, null, null));
		assertTrue(empty.createdBy().isEmpty());
	}
	
	@Test
	void timestampsDelegateToAudit() {
		SqlAuditMetadata metadata = SqlAuditMetadata.of(1L, NOW, "alice", LATER, "bob");
		SqlAudited<String> audited = new SqlAudited<>("entity", metadata);
		assertEquals(metadata.createdAt(), audited.createdAt());
		assertEquals(metadata.updatedAt(), audited.updatedAt());
	}
	
	@Test
	void updatedByDelegatesToAudit() {
		SqlAuditMetadata metadata = SqlAuditMetadata.of(1L, NOW, "alice", LATER, "bob");
		SqlAudited<String> audited = new SqlAudited<>("entity", metadata);
		assertEquals(metadata.updatedBy(), audited.updatedBy());
	}
	
	@Test
	void ofReturnsEquivalentToConstructor() {
		SqlAuditMetadata metadata = SqlAuditMetadata.of(1L, NOW, "alice", LATER, "bob");
		assertEquals(new SqlAudited<>("entity", metadata), SqlAudited.of("entity", metadata));
	}
	
	@Test
	void accessorsReturnConstructorValues() {
		SqlAuditMetadata metadata = SqlAuditMetadata.of(1L, NOW, "alice", LATER, "bob");
		SqlAudited<String> audited = new SqlAudited<>("entity", metadata);
		assertSame("entity", audited.entity());
		assertSame(metadata, audited.audit());
	}
	
	@Test
	void wrapsComplexEntityType() {
		User user = new User("bob", 30);
		SqlAuditMetadata metadata = SqlAuditMetadata.of(2L, NOW, "alice", LATER, "carol");
		SqlAudited<User> audited = new SqlAudited<>(user, metadata);
		assertEquals(user, audited.entity());
		assertEquals(2L, audited.version().getAsLong());
		assertEquals(Optional.of(NOW), audited.createdAt());
		assertEquals(Optional.of("alice"), audited.createdBy());
		assertEquals(Optional.of(LATER), audited.updatedAt());
		assertEquals(Optional.of("carol"), audited.updatedBy());
	}
	
	@Test
	void equalAuditedAreEqual() {
		SqlAuditMetadata metadata = SqlAuditMetadata.of(1L, NOW, "alice", LATER, "bob");
		SqlAudited<String> first = new SqlAudited<>("entity", metadata);
		SqlAudited<String> second = new SqlAudited<>("entity", SqlAuditMetadata.of(1L, NOW, "alice", LATER, "bob"));
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
	}
	
	private record User(String name, int age) {}
}
