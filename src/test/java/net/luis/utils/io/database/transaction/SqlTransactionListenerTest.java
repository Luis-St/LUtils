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

package net.luis.utils.io.database.transaction;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlTransactionListener}.<br>
 *
 * @author Luis-St
 */
class SqlTransactionListenerTest {
	
	@Test
	void defaultMethodsDoNothing() {
		SqlTransactionListener listener = new SqlTransactionListener() {};
		assertDoesNotThrow(listener::afterCommit);
		assertDoesNotThrow(listener::afterRollback);
		assertDoesNotThrow(listener::afterClose);
	}
	
	@Test
	void overriddenMethodsAreInvoked() {
		AtomicBoolean committed = new AtomicBoolean(false);
		AtomicBoolean rolledBack = new AtomicBoolean(false);
		AtomicBoolean closed = new AtomicBoolean(false);
		SqlTransactionListener listener = new SqlTransactionListener() {
			
			@Override
			public void afterCommit() {
				committed.set(true);
			}
			
			@Override
			public void afterRollback() {
				rolledBack.set(true);
			}
			
			@Override
			public void afterClose() {
				closed.set(true);
			}
		};
		
		listener.afterCommit();
		assertTrue(committed.get());
		assertFalse(rolledBack.get());
		assertFalse(closed.get());
		
		listener.afterRollback();
		assertTrue(rolledBack.get());
		assertFalse(closed.get());
		
		listener.afterClose();
		assertTrue(closed.get());
	}
}
