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

package net.luis.utils.io.network.connection;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link TaskTracker}.<br>
 *
 * @author Luis-St
 */
@Timeout(value = 30, unit = TimeUnit.SECONDS)
class TaskTrackerTest {
	
	private static void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}
	
	@Test
	void constructHasNoRunningHandlers() {
		TaskTracker tracker = new TaskTracker();
		
		assertEquals(0, tracker.running());
		assertTrue(tracker.awaitIdle(Duration.ZERO));
	}
	
	@Test
	void awaitIdleWithNullTimeout() {
		TaskTracker tracker = new TaskTracker();
		
		assertThrows(NullPointerException.class, () -> tracker.awaitIdle(null));
	}
	
	@Test
	void startedIncrementsRunning() {
		TaskTracker tracker = new TaskTracker();
		
		tracker.started();
		assertEquals(1, tracker.running());
		
		tracker.started();
		tracker.started();
		assertEquals(3, tracker.running());
	}
	
	@Test
	void finishedDecrementsRunning() {
		TaskTracker tracker = new TaskTracker();
		tracker.started();
		tracker.started();
		
		tracker.finished();
		
		assertEquals(1, tracker.running());
	}
	
	@Test
	void finishedWithoutStartedStaysAtZero() {
		TaskTracker tracker = new TaskTracker();
		
		tracker.finished();
		tracker.finished();
		assertEquals(0, tracker.running());
		
		tracker.started();
		assertEquals(1, tracker.running());
	}
	
	@Test
	void awaitIdleWhenIdleReturnsTrueImmediately() {
		TaskTracker tracker = new TaskTracker();
		
		long start = System.nanoTime();
		assertTrue(tracker.awaitIdle(Duration.ofHours(1)));
		long elapsed = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
		
		assertTrue(elapsed < 10000, "Elapsed: " + elapsed + " ms");
	}
	
	@Test
	void awaitIdleWithZeroTimeoutWhileRunning() {
		TaskTracker tracker = new TaskTracker();
		tracker.started();
		
		assertFalse(tracker.awaitIdle(Duration.ZERO));
	}
	
	@Test
	void awaitIdleWithNegativeTimeoutWhileRunning() {
		TaskTracker tracker = new TaskTracker();
		tracker.started();
		
		assertFalse(tracker.awaitIdle(Duration.ofSeconds(-1)));
	}
	
	@Test
	void awaitIdleWithNegativeTimeoutWhenIdle() {
		TaskTracker tracker = new TaskTracker();
		
		assertTrue(tracker.awaitIdle(Duration.ofSeconds(-1)));
	}
	
	@Test
	void awaitIdleTimesOutWhileRunning() {
		TaskTracker tracker = new TaskTracker();
		tracker.started();
		
		long start = System.nanoTime();
		assertFalse(tracker.awaitIdle(Duration.ofMillis(100)));
		long elapsed = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
		
		assertTrue(elapsed >= 100, "Elapsed: " + elapsed + " ms");
	}
	
	@Test
	void awaitIdleReturnsWhenHandlerFinishes() throws Exception {
		TaskTracker tracker = new TaskTracker();
		tracker.started();
		Thread finisher = Thread.ofVirtual().start(() -> {
			sleep(100);
			tracker.finished();
		});
		
		long start = System.nanoTime();
		assertTrue(tracker.awaitIdle(Duration.ofSeconds(20)));
		long elapsed = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
		finisher.join();
		
		assertTrue(elapsed < 10000, "Elapsed: " + elapsed + " ms");
		assertEquals(0, tracker.running());
	}
	
	@Test
	void awaitIdleInterruptedWhileWaiting() throws Exception {
		TaskTracker tracker = new TaskTracker();
		tracker.started();
		AtomicBoolean result = new AtomicBoolean(true);
		AtomicBoolean interrupted = new AtomicBoolean(false);
		Thread waiter = Thread.ofPlatform().start(() -> {
			result.set(tracker.awaitIdle(Duration.ofSeconds(10)));
			interrupted.set(Thread.currentThread().isInterrupted());
		});
		
		sleep(100);
		waiter.interrupt();
		waiter.join(5000);
		
		assertFalse(waiter.isAlive());
		assertFalse(result.get());
		assertTrue(interrupted.get());
	}
	
	@Test
	void awaitIdleWithInterruptFlagWhenIdle() {
		TaskTracker tracker = new TaskTracker();
		
		Thread.currentThread().interrupt();
		boolean idle = tracker.awaitIdle(Duration.ofSeconds(1));
		boolean flagSet = Thread.interrupted();
		
		assertTrue(idle);
		assertTrue(flagSet);
	}
	
	@Test
	void runningReflectsStartAndFinishSequence() {
		TaskTracker tracker = new TaskTracker();
		
		tracker.started();
		assertEquals(1, tracker.running());
		tracker.started();
		assertEquals(2, tracker.running());
		tracker.finished();
		assertEquals(1, tracker.running());
		tracker.started();
		assertEquals(2, tracker.running());
		tracker.finished();
		assertEquals(1, tracker.running());
		tracker.finished();
		assertEquals(0, tracker.running());
	}
	
	@Test
	void awaitIdleWaitsForAllOfSeveralHandlers() throws Exception {
		TaskTracker tracker = new TaskTracker();
		tracker.started();
		tracker.started();
		tracker.started();
		AtomicInteger finishedCount = new AtomicInteger();
		Thread finisher = Thread.ofVirtual().start(() -> {
			for (int i = 0; i < 3; i++) {
				sleep(50);
				finishedCount.incrementAndGet();
				tracker.finished();
			}
		});
		
		assertTrue(tracker.awaitIdle(Duration.ofSeconds(5)));
		assertEquals(3, finishedCount.get());
		assertEquals(0, tracker.running());
		finisher.join();
	}
	
	@Test
	void concurrentStartAndFinishKeepsCountConsistent() throws Exception {
		TaskTracker tracker = new TaskTracker();
		List<Thread> threads = new ArrayList<>();
		for (int i = 0; i < 8; i++) {
			threads.add(Thread.ofPlatform().start(() -> {
				for (int j = 0; j < 1000; j++) {
					tracker.started();
					tracker.finished();
				}
			}));
		}
		for (Thread thread : threads) {
			thread.join();
		}
		
		assertEquals(0, tracker.running());
		assertTrue(tracker.awaitIdle(Duration.ZERO));
	}
}
