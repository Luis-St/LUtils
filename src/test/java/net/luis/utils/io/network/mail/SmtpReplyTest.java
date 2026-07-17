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

package net.luis.utils.io.network.mail;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SmtpReply}.<br>
 *
 * @author Luis-St
 */
class SmtpReplyTest {
	
	@Test
	void constructWithValidCodeAndLines() {
		SmtpReply reply = new SmtpReply(250, List.of("OK"));
		
		assertEquals(250, reply.code());
		assertEquals(List.of("OK"), reply.lines());
	}
	
	@Test
	void constructWithSingleLine() {
		SmtpReply reply = new SmtpReply(220, List.of("mail.example.com ESMTP"));
		
		assertEquals(1, reply.lines().size());
	}
	
	@Test
	void constructCopiesLinesDefensively() {
		List<String> original = new ArrayList<>(List.of("OK"));
		SmtpReply reply = new SmtpReply(250, original);
		
		original.add("mutated");
		
		assertEquals(List.of("OK"), reply.lines());
		assertThrows(UnsupportedOperationException.class, () -> reply.lines().add("x"));
	}
	
	@Test
	void constructWithEmptyLines() {
		SmtpReply reply = assertDoesNotThrow(() -> new SmtpReply(250, List.of()));
		
		assertTrue(reply.lines().isEmpty());
	}
	
	@Test
	void constructWithNullLines() {
		assertThrows(NullPointerException.class, () -> new SmtpReply(250, null));
	}
	
	@Test
	void constructWithCodeBelowMinimumThrows() {
		assertThrows(IllegalArgumentException.class, () -> new SmtpReply(99, List.of("x")));
	}
	
	@Test
	void constructWithCodeAboveMaximumThrows() {
		assertThrows(IllegalArgumentException.class, () -> new SmtpReply(600, List.of("x")));
	}
	
	@Test
	void constructWithZeroCodeThrows() {
		assertThrows(IllegalArgumentException.class, () -> new SmtpReply(0, List.of("x")));
	}
	
	@Test
	void constructWithNegativeCodeThrows() {
		assertThrows(IllegalArgumentException.class, () -> new SmtpReply(-1, List.of("x")));
	}
	
	@Test
	void constructWithNullLinesThrows() {
		assertThrows(NullPointerException.class, () -> new SmtpReply(250, null));
	}
	
	@Test
	void constructWithNullLinesAndInvalidCodeThrowsNullPointer() {
		assertThrows(NullPointerException.class, () -> new SmtpReply(0, null));
	}
	
	@Test
	void constructAtMinimumCodeBoundary() {
		SmtpReply reply = assertDoesNotThrow(() -> new SmtpReply(100, List.of("x")));
		
		assertEquals(100, reply.code());
	}
	
	@Test
	void constructBelowMinimumCodeBoundary() {
		assertThrows(IllegalArgumentException.class, () -> new SmtpReply(99, List.of("x")));
	}
	
	@Test
	void constructAtMaximumCodeBoundary() {
		SmtpReply reply = assertDoesNotThrow(() -> new SmtpReply(599, List.of("x")));
		
		assertEquals(599, reply.code());
	}
	
	@Test
	void constructAboveMaximumCodeBoundary() {
		assertThrows(IllegalArgumentException.class, () -> new SmtpReply(600, List.of("x")));
	}
	
	@Test
	void isPositiveCompletionTrueFor2xx() {
		SmtpReply reply = new SmtpReply(250, List.of("OK"));
		
		assertTrue(reply.isPositiveCompletion());
		assertFalse(reply.isIntermediate());
		assertFalse(reply.isError());
	}
	
	@Test
	void isPositiveCompletionAtLowerBoundary200() {
		SmtpReply reply = new SmtpReply(200, List.of("OK"));
		
		assertTrue(reply.isPositiveCompletion());
	}
	
	@Test
	void isPositiveCompletionAtUpperBoundary299() {
		SmtpReply reply = new SmtpReply(299, List.of("OK"));
		
		assertTrue(reply.isPositiveCompletion());
	}
	
	@Test
	void isPositiveCompletionFalseBelow200() {
		SmtpReply reply = new SmtpReply(199, List.of("x"));
		
		assertFalse(reply.isPositiveCompletion());
	}
	
	@Test
	void isPositiveCompletionFalseAt300() {
		SmtpReply reply = new SmtpReply(300, List.of("x"));
		
		assertFalse(reply.isPositiveCompletion());
	}
	
	@Test
	void isIntermediateTrueFor3xx() {
		SmtpReply reply = new SmtpReply(354, List.of("Start mail input"));
		
		assertTrue(reply.isIntermediate());
		assertFalse(reply.isPositiveCompletion());
		assertFalse(reply.isError());
	}
	
	@Test
	void isIntermediateAtLowerBoundary300() {
		SmtpReply reply = new SmtpReply(300, List.of("x"));
		
		assertTrue(reply.isIntermediate());
	}
	
	@Test
	void isIntermediateAtUpperBoundary399() {
		SmtpReply reply = new SmtpReply(399, List.of("x"));
		
		assertTrue(reply.isIntermediate());
	}
	
	@Test
	void isIntermediateFalseAt299() {
		SmtpReply reply = new SmtpReply(299, List.of("x"));
		
		assertFalse(reply.isIntermediate());
	}
	
	@Test
	void isIntermediateFalseAt400() {
		SmtpReply reply = new SmtpReply(400, List.of("x"));
		
		assertFalse(reply.isIntermediate());
	}
	
	@Test
	void isErrorTrueForTransient4xx() {
		SmtpReply reply = new SmtpReply(450, List.of("Mailbox busy"));
		
		assertTrue(reply.isError());
		assertFalse(reply.isPositiveCompletion());
		assertFalse(reply.isIntermediate());
	}
	
	@Test
	void isErrorTrueForPermanent5xx() {
		SmtpReply reply = new SmtpReply(550, List.of("No such user"));
		
		assertTrue(reply.isError());
	}
	
	@Test
	void isErrorAtLowerBoundary400() {
		SmtpReply reply = new SmtpReply(400, List.of("x"));
		
		assertTrue(reply.isError());
	}
	
	@Test
	void isErrorFalseAt399() {
		SmtpReply reply = new SmtpReply(399, List.of("x"));
		
		assertFalse(reply.isError());
	}
	
	@Test
	void isErrorFalseForPositiveCode() {
		SmtpReply reply = new SmtpReply(250, List.of("OK"));
		
		assertFalse(reply.isError());
	}
	
	@Test
	void messageWithSingleLine() {
		SmtpReply reply = new SmtpReply(250, List.of("OK"));
		
		assertEquals("OK", reply.message());
	}
	
	@Test
	void messageWithEmptyLines() {
		SmtpReply reply = new SmtpReply(250, List.of());
		
		assertEquals("", reply.message());
	}
	
	@Test
	void codeAccessorReturnsGivenCode() {
		SmtpReply reply = new SmtpReply(421, List.of("Service not available"));
		
		assertEquals(421, reply.code());
	}
	
	@Test
	void linesAccessorReturnsGivenLines() {
		SmtpReply reply = new SmtpReply(250, List.of("a", "b"));
		
		assertEquals(List.of("a", "b"), reply.lines());
	}
	
	@Test
	void categoryHelpersMutuallyExclusiveForSingleCode() {
		SmtpReply reply = new SmtpReply(500, List.of("Syntax error"));
		
		assertTrue(reply.isError());
		assertFalse(reply.isPositiveCompletion());
		assertFalse(reply.isIntermediate());
	}
	
	@Test
	void categoryHelpersAllFalseForOneXxCode() {
		SmtpReply reply = new SmtpReply(150, List.of("Wait"));
		
		assertEquals(150, reply.code());
		assertFalse(reply.isPositiveCompletion());
		assertFalse(reply.isIntermediate());
		assertFalse(reply.isError());
	}
	
	@Test
	void messageJoinsMultipleLinesWithLineFeed() {
		SmtpReply reply = new SmtpReply(250, List.of("mail.example.com", "PIPELINING", "8BITMIME"));
		
		assertEquals("mail.example.com\nPIPELINING\n8BITMIME", reply.message());
	}
	
	@Test
	void constructMultiLineEhloReply() {
		List<String> lines = List.of("mail.example.com", "PIPELINING", "SIZE 10485760", "STARTTLS");
		SmtpReply reply = new SmtpReply(250, lines);
		
		assertTrue(reply.isPositiveCompletion());
		assertEquals(4, reply.lines().size());
		assertEquals("mail.example.com\nPIPELINING\nSIZE 10485760\nSTARTTLS", reply.message());
	}
	
	@Test
	void messagePreservesEmptyStringLines() {
		SmtpReply reply = new SmtpReply(250, List.of("a", "", "b"));
		
		assertEquals("a\n\nb", reply.message());
	}
	
	@Test
	void equalRepliesAreEqual() {
		SmtpReply first = new SmtpReply(250, List.of("OK"));
		SmtpReply second = new SmtpReply(250, List.of("OK"));
		
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
	}
	
	@Test
	void unequalRepliesDifferByCodeOrLines() {
		SmtpReply base = new SmtpReply(250, List.of("OK"));
		
		assertNotEquals(base, new SmtpReply(251, List.of("OK")));
		assertNotEquals(base, new SmtpReply(250, List.of("FAIL")));
	}
}
