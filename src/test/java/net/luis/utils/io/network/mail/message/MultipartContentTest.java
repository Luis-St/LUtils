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

package net.luis.utils.io.network.mail.message;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link MultipartContent}.<br>
 *
 * @author Luis-St
 */
class MultipartContentTest {
	
	private static final MailContent TEXT_PART = TextContent.of("plain");
	private static final MailContent HTML_PART = HtmlContent.of("<p>x</p>");
	
	@Test
	void constructWithSubtypeAndParts() {
		MultipartContent content = new MultipartContent("mixed", List.of(TextContent.of("a")));
		assertNotNull(content);
		assertEquals("mixed", content.subtype());
		assertEquals(1, content.parts().size());
	}
	
	@Test
	void constructWithMultipleParts() {
		List<MailContent> parts = List.of(TextContent.of("a"), HtmlContent.of("<p>b</p>"), TextContent.of("c"));
		MultipartContent content = new MultipartContent("mixed", parts);
		assertEquals(3, content.parts().size());
		assertEquals(parts, content.parts());
	}
	
	@Test
	void constructWithNullSubtype() {
		assertThrows(NullPointerException.class, () -> new MultipartContent(null, List.of(TextContent.of("a"))));
	}
	
	@Test
	void constructWithNullParts() {
		assertThrows(NullPointerException.class, () -> new MultipartContent("mixed", null));
	}
	
	@Test
	void alternativeCreatesInstance() {
		MultipartContent content = MultipartContent.alternative(TextContent.of("plain"), HtmlContent.of("<p>x</p>"));
		assertEquals("alternative", content.subtype());
		assertEquals(2, content.parts().size());
	}
	
	@Test
	void alternativeWithSinglePart() {
		MultipartContent content = MultipartContent.alternative(TextContent.of("plain"));
		assertEquals("alternative", content.subtype());
		assertEquals(1, content.parts().size());
	}
	
	@Test
	void mixedCreatesInstance() {
		MultipartContent content = MultipartContent.mixed(TextContent.of("body"), HtmlContent.of("<p>y</p>"));
		assertEquals("mixed", content.subtype());
		assertEquals(2, content.parts().size());
	}
	
	@Test
	void mixedWithSinglePart() {
		MultipartContent content = MultipartContent.mixed(TextContent.of("body"));
		assertEquals("mixed", content.subtype());
		assertEquals(1, content.parts().size());
	}
	
	@Test
	void constructWithEmptySubtypeThrows() {
		assertThrows(IllegalArgumentException.class, () -> new MultipartContent("", List.of(TextContent.of("a"))));
	}
	
	@Test
	void constructWithEmptyPartsThrows() {
		assertThrows(IllegalArgumentException.class, () -> new MultipartContent("mixed", List.of()));
	}
	
	@Test
	void constructWithNullElementInPartsThrows() {
		List<MailContent> parts = Arrays.asList(TextContent.of("a"), null);
		assertThrows(NullPointerException.class, () -> new MultipartContent("mixed", parts));
	}
	
	@Test
	void alternativeWithNullArrayThrows() {
		assertThrows(NullPointerException.class, () -> MultipartContent.alternative((MailContent[]) null));
	}
	
	@Test
	void alternativeWithNullElementThrows() {
		assertThrows(NullPointerException.class, () -> MultipartContent.alternative(TextContent.of("a"), null));
	}
	
	@Test
	void alternativeWithNoPartsThrows() {
		assertThrows(IllegalArgumentException.class, MultipartContent::alternative);
	}
	
	@Test
	void mixedWithNullArrayThrows() {
		assertThrows(NullPointerException.class, () -> MultipartContent.mixed((MailContent[]) null));
	}
	
	@Test
	void mixedWithNullElementThrows() {
		assertThrows(NullPointerException.class, () -> MultipartContent.mixed(TextContent.of("a"), null));
	}
	
	@Test
	void mixedWithNoPartsThrows() {
		assertThrows(IllegalArgumentException.class, MultipartContent::mixed);
	}
	
	@Test
	void contentTypeForAlternative() {
		MultipartContent content = MultipartContent.alternative(TextContent.of("a"));
		assertEquals("multipart/alternative", content.contentType());
	}
	
	@Test
	void contentTypeForMixed() {
		MultipartContent content = MultipartContent.mixed(TextContent.of("a"));
		assertEquals("multipart/mixed", content.contentType());
	}
	
	@Test
	void contentTypeForCustomSubtype() {
		MultipartContent content = new MultipartContent("related", List.of(TextContent.of("a")));
		assertEquals("multipart/related", content.contentType());
	}
	
	@Test
	void subtypeAccessorReturnsValue() {
		MultipartContent content = new MultipartContent("related", List.of(TEXT_PART));
		assertEquals("related", content.subtype());
	}
	
	@Test
	void partsAccessorReturnsAllParts() {
		List<MailContent> parts = List.of(TEXT_PART, HTML_PART);
		MultipartContent content = new MultipartContent("mixed", parts);
		assertEquals(parts, content.parts());
	}
	
	@Test
	void partsPreserveInsertionOrder() {
		MailContent text2 = TextContent.of("second");
		MultipartContent content = new MultipartContent("mixed", List.of(TEXT_PART, HTML_PART, text2));
		assertEquals(TEXT_PART, content.parts().get(0));
		assertEquals(HTML_PART, content.parts().get(1));
		assertEquals(text2, content.parts().get(2));
	}
	
	@Test
	void partsListIsUnmodifiable() {
		MultipartContent content = new MultipartContent("mixed", List.of(TEXT_PART));
		assertThrows(UnsupportedOperationException.class, () -> content.parts().add(TextContent.of("x")));
		assertThrows(UnsupportedOperationException.class, () -> content.parts().remove(0));
	}
	
	@Test
	void partsCopiedDefensivelyFromSource() {
		List<MailContent> source = new ArrayList<>(List.of(TEXT_PART, HTML_PART));
		MultipartContent content = new MultipartContent("mixed", source);
		source.add(TextContent.of("added"));
		assertEquals(2, content.parts().size());
	}
	
	@Test
	void nestedMultipartContent() {
		MultipartContent inner = MultipartContent.alternative(TextContent.of("text"), HtmlContent.of("<p>html</p>"));
		MultipartContent outer = MultipartContent.mixed(inner, TextContent.of("attachment"));
		assertEquals("mixed", outer.subtype());
		MultipartContent nested = assertInstanceOf(MultipartContent.class, outer.parts().get(0));
		assertEquals("multipart/alternative", nested.contentType());
	}
	
	@Test
	void mixedContentTypesInParts() {
		MultipartContent content = MultipartContent.mixed(TextContent.of("t"), HtmlContent.of("<p>h</p>"));
		assertEquals(2, content.parts().size());
		assertEquals("text/plain", content.parts().get(0).contentType());
		assertEquals("text/html", content.parts().get(1).contentType());
	}
	
	@Test
	void equalsAndHashCodeForEqualContents() {
		MultipartContent first = new MultipartContent("mixed", List.of(TextContent.of("a")));
		MultipartContent second = new MultipartContent("mixed", List.of(TextContent.of("a")));
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
		assertNotEquals(first, new MultipartContent("related", List.of(TextContent.of("a"))));
	}
	
	@Test
	void equalsWithDifferentPartsNotEqual() {
		MultipartContent first = new MultipartContent("mixed", List.of(TextContent.of("a")));
		MultipartContent second = new MultipartContent("mixed", List.of(TextContent.of("b")));
		assertNotEquals(first, second);
	}
	
	@Test
	void toStringContainsSubtypeAndParts() {
		String string = new MultipartContent("mixed", List.of(TextContent.of("a"))).toString();
		assertTrue(string.contains("mixed"));
		assertTrue(string.contains("parts"));
	}
}
