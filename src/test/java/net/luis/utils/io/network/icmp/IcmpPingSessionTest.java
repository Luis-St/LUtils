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

package net.luis.utils.io.network.icmp;

import net.luis.utils.io.network.address.ipv4.Ipv4Address;
import net.luis.utils.io.network.connection.exception.NetworkConnectionException;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BooleanSupplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

/**
 * Test class for {@link IcmpPingSession}.<br>
 *
 * @author Luis-St
 */
class IcmpPingSessionTest {
	
	private static final IcmpEchoReply REPLY = new IcmpEchoReply(new IcmpPacket(Ipv4Address.LOOPBACK, 0, 0, 0x00010001, new byte[0]), Duration.ofMillis(1));
	private static boolean datagramV4;
	
	@BeforeAll
	static void setUp() {
		String os = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
		boolean supported = (os.contains("linux") || os.contains("mac")) && System.getProperty("os.arch", "").contains("64");
		datagramV4 = supported && canOpen();
	}
	
	private static boolean canOpen() {
		try (IcmpSocket socket = new IcmpSocket(IcmpVersion.ICMP_V4)) {
			socket.bind(Ipv4Address.LOOPBACK);
			return true;
		} catch (NetworkConnectionException | UnsupportedOperationException e) {
			return false;
		}
	}
	
	private static @NonNull IcmpSocket openSocket() throws NetworkConnectionException {
		assumeTrue(datagramV4);
		return new IcmpSocket(IcmpVersion.ICMP_V4);
	}
	
	private static boolean await(@NonNull BooleanSupplier condition, @NonNull Duration timeout) throws InterruptedException {
		long deadline = System.nanoTime() + timeout.toNanos();
		while (!condition.getAsBoolean()) {
			if (System.nanoTime() >= deadline) {
				return false;
			}
			Thread.sleep(10);
		}
		return true;
	}
	
	private static @NonNull Runnable spinUntil(@NonNull AtomicBoolean release) {
		return () -> {
			while (!release.get()) {
				Thread.onSpinWait();
			}
		};
	}
	
	@Test
	void constructWithSocket() throws Exception {
		IcmpSocket socket = openSocket();
		try (IcmpPingSession session = new IcmpPingSession(socket, 3)) {
			assertSame(socket, session.socket());
			assertTrue(session.results().isEmpty());
			assertEquals(0, session.sent());
			assertFalse(session.isRunning());
			assertFalse(session.isCancelled());
			assertTrue(session.failure().isEmpty());
		}
	}
	
	@Test
	void constructWithNullSocket() {
		assertThrows(NullPointerException.class, () -> new IcmpPingSession(null, 1));
	}
	
	@Test
	void constructWithZeroMaxResults() throws Exception {
		try (IcmpSocket socket = openSocket()) {
			assertThrows(IllegalArgumentException.class, () -> new IcmpPingSession(socket, 0));
		}
	}
	
	@Test
	void startWithNullTask() throws Exception {
		try (IcmpPingSession session = new IcmpPingSession(openSocket(), 1)) {
			assertThrows(NullPointerException.class, () -> session.start("test", null));
		}
	}
	
	@Test
	void startWithNullName() throws Exception {
		try (IcmpPingSession session = new IcmpPingSession(openSocket(), 1)) {
			assertThrows(NullPointerException.class, () -> session.start(null, () -> {}));
		}
	}
	
	@Test
	void addNullResult() throws Exception {
		try (IcmpPingSession session = new IcmpPingSession(openSocket(), 1)) {
			assertThrows(NullPointerException.class, () -> session.add(null));
			assertEquals(0, session.sent());
		}
	}
	
	@Test
	void constructWithMinMaxResultsBoundary() throws Exception {
		try (IcmpPingSession session = new IcmpPingSession(openSocket(), 1)) {
			session.add(new IcmpPingResult.Lost(1));
			session.add(new IcmpPingResult.Lost(2));
			assertEquals(List.of(new IcmpPingResult.Lost(2)), session.results());
		}
	}
	
	@Test
	void addBelowMaxResultsKeepsAll() throws Exception {
		try (IcmpPingSession session = new IcmpPingSession(openSocket(), 3)) {
			session.add(new IcmpPingResult.Lost(1));
			session.add(new IcmpPingResult.Lost(2));
			assertEquals(List.of(new IcmpPingResult.Lost(1), new IcmpPingResult.Lost(2)), session.results());
			assertEquals(2, session.sent());
		}
	}
	
	@Test
	void addAtMaxResultsDropsOldest() throws Exception {
		try (IcmpPingSession session = new IcmpPingSession(openSocket(), 2)) {
			session.add(new IcmpPingResult.Lost(1));
			session.add(new IcmpPingResult.Lost(2));
			session.add(new IcmpPingResult.Lost(3));
			assertEquals(List.of(new IcmpPingResult.Lost(2), new IcmpPingResult.Lost(3)), session.results());
			assertEquals(3, session.sent());
		}
	}
	
	@Test
	void isRunningBeforeStart() throws Exception {
		try (IcmpPingSession session = new IcmpPingSession(openSocket(), 1)) {
			assertFalse(session.isRunning());
		}
	}
	
	@Test
	void isRunningWhileTaskAlive() throws Exception {
		AtomicBoolean release = new AtomicBoolean();
		IcmpPingSession session = new IcmpPingSession(openSocket(), 1);
		try {
			session.start("test-running", spinUntil(release));
			assertTrue(session.isRunning());
		} finally {
			release.set(true);
			session.close();
		}
	}
	
	@Test
	void isRunningAfterTaskEnded() throws Exception {
		try (IcmpPingSession session = new IcmpPingSession(openSocket(), 1)) {
			session.start("test-ended", () -> {});
			assertTrue(await(() -> !session.socket().isOpen() && !session.isRunning(), Duration.ofSeconds(2)));
			assertFalse(session.isRunning());
			assertFalse(session.isCancelled());
		}
	}
	
	@Test
	void isRunningAfterCancel() throws Exception {
		AtomicBoolean release = new AtomicBoolean();
		IcmpPingSession session = new IcmpPingSession(openSocket(), 1);
		session.start("test-cancel", () -> {
			while (!release.get() && !Thread.currentThread().isInterrupted()) {
				Thread.onSpinWait();
			}
		});
		session.cancel();
		release.set(true);
		assertFalse(session.isRunning());
	}
	
	@Test
	void startTaskEndsNormally() throws Exception {
		try (IcmpPingSession session = new IcmpPingSession(openSocket(), 1)) {
			session.start("test-normal", () -> {});
			assertTrue(await(() -> !session.socket().isOpen(), Duration.ofSeconds(2)));
			assertTrue(session.failure().isEmpty());
		}
	}
	
	@Test
	void startTaskThrowsRuntimeException() throws Exception {
		RuntimeException exception = new IllegalStateException("boom");
		try (IcmpPingSession session = new IcmpPingSession(openSocket(), 1)) {
			session.start("test-runtime", () -> {
				throw exception;
			});
			assertTrue(await(() -> !session.socket().isOpen(), Duration.ofSeconds(2)));
			assertSame(exception, session.failure().orElseThrow());
		}
	}
	
	@Test
	void startTaskThrowsError() throws Exception {
		AssertionError error = new AssertionError("boom");
		try (IcmpPingSession session = new IcmpPingSession(openSocket(), 1)) {
			session.start("test-error", () -> {
				throw error;
			});
			assertTrue(await(() -> !session.socket().isOpen(), Duration.ofSeconds(2)));
			assertSame(error, session.failure().orElseThrow());
		}
	}
	
	@Test
	void cancelBeforeStart() throws Exception {
		IcmpPingSession session = new IcmpPingSession(openSocket(), 1);
		session.cancel();
		assertTrue(session.isCancelled());
		assertFalse(session.socket().isOpen());
	}
	
	@Test
	void cancelTwice() throws Exception {
		IcmpPingSession session = new IcmpPingSession(openSocket(), 1);
		session.cancel();
		assertDoesNotThrow(session::cancel);
		assertTrue(session.isCancelled());
		assertFalse(session.socket().isOpen());
	}
	
	@Test
	void cancelFromTaskThread() throws Exception {
		CountDownLatch done = new CountDownLatch(1);
		IcmpPingSession session = new IcmpPingSession(openSocket(), 1);
		session.start("test-self-cancel", () -> {
			session.cancel();
			done.countDown();
		});
		
		assertTrue(done.await(1, TimeUnit.SECONDS));
		assertTrue(session.isCancelled());
		assertTrue(await(() -> !session.socket().isOpen(), Duration.ofSeconds(1)));
	}
	
	@Test
	void cancelRunningSessionJoinsThread() throws Exception {
		AtomicBoolean ended = new AtomicBoolean();
		IcmpPingSession session = new IcmpPingSession(openSocket(), 1);
		session.start("test-join", () -> {
			while (!Thread.currentThread().isInterrupted()) {
				Thread.onSpinWait();
			}
			ended.set(true);
		});
		
		session.cancel();
		assertTrue(ended.get());
		assertFalse(session.isRunning());
	}
	
	@Test
	void cancelWhileCallerInterrupted() throws Exception {
		AtomicBoolean release = new AtomicBoolean();
		AtomicBoolean ended = new AtomicBoolean();
		IcmpPingSession session = new IcmpPingSession(openSocket(), 1);
		session.start("test-interrupted-caller", () -> {
			spinUntil(release).run();
			ended.set(true);
		});
		
		boolean interrupted;
		Thread.currentThread().interrupt();
		try {
			session.cancel();
		} finally {
			interrupted = Thread.interrupted();
			release.set(true);
		}
		assertTrue(interrupted);
		assertTrue(await(ended::get, Duration.ofSeconds(2)));
	}
	
	@Test
	void closeDelegatesToCancel() throws Exception {
		IcmpPingSession session = new IcmpPingSession(openSocket(), 1);
		session.close();
		assertTrue(session.isCancelled());
		assertFalse(session.socket().isOpen());
	}
	
	@Test
	void addDifferentResultKinds() throws Exception {
		List<IcmpPingResult> expected = List.of(new IcmpPingResult.Replied(1, REPLY), new IcmpPingResult.Lost(2), new IcmpPingResult.Failed(3, "error"));
		try (IcmpPingSession session = new IcmpPingSession(openSocket(), 5)) {
			expected.forEach(session::add);
			assertEquals(expected, session.results());
		}
	}
	
	@Test
	void resultsIsUnmodifiableSnapshot() throws Exception {
		try (IcmpPingSession session = new IcmpPingSession(openSocket(), 5)) {
			session.add(new IcmpPingResult.Lost(1));
			List<IcmpPingResult> snapshot = session.results();
			assertThrows(UnsupportedOperationException.class, () -> snapshot.add(new IcmpPingResult.Lost(2)));
			
			session.add(new IcmpPingResult.Lost(2));
			assertEquals(List.of(new IcmpPingResult.Lost(1)), snapshot);
			assertEquals(2, session.results().size());
		}
	}
	
	@Test
	void startUsesThreadName() throws Exception {
		AtomicReference<String> name = new AtomicReference<>();
		AtomicBoolean daemon = new AtomicBoolean();
		CountDownLatch done = new CountDownLatch(1);
		try (IcmpPingSession session = new IcmpPingSession(openSocket(), 1)) {
			session.start("icmp-test-thread", () -> {
				name.set(Thread.currentThread().getName());
				daemon.set(Thread.currentThread().isDaemon());
				done.countDown();
			});
			assertTrue(done.await(2, TimeUnit.SECONDS));
			assertEquals("icmp-test-thread", name.get());
			assertTrue(daemon.get());
		}
	}
	
	@Test
	void addManyResultsKeepsWindow() throws Exception {
		try (IcmpPingSession session = new IcmpPingSession(openSocket(), 5)) {
			for (int i = 1; i <= 100; i++) {
				session.add(new IcmpPingResult.Lost(i));
			}
			assertEquals(100, session.sent());
			assertEquals(List.of(96, 97, 98, 99, 100), session.results().stream().map(IcmpPingResult::sequenceNumber).toList());
		}
	}
	
	@Test
	void concurrentAddKeepsCount() throws Exception {
		try (IcmpPingSession session = new IcmpPingSession(openSocket(), 10)) {
			List<Thread> threads = new ArrayList<>();
			for (int t = 0; t < 4; t++) {
				threads.add(Thread.ofPlatform().start(() -> {
					for (int i = 0; i < 1000; i++) {
						session.add(new IcmpPingResult.Lost(i));
					}
				}));
			}
			for (Thread thread : threads) {
				thread.join();
			}
			assertEquals(4000, session.sent());
			assertEquals(10, session.results().size());
		}
	}
	
	@Test
	void tryWithResourcesCancelsSession() throws Exception {
		AtomicBoolean ended = new AtomicBoolean();
		IcmpPingSession reference;
		try (IcmpPingSession session = new IcmpPingSession(openSocket(), 1)) {
			reference = session;
			session.start("test-resources", () -> {
				while (!Thread.currentThread().isInterrupted()) {
					Thread.onSpinWait();
				}
				ended.set(true);
			});
		}
		assertTrue(reference.isCancelled());
		assertTrue(ended.get());
	}
}
