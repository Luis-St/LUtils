/*
 * LUtils
 * Copyright (C) 2025 Luis Staudt
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

package net.luis.utils.io.data.ini;

import net.luis.utils.io.data.ini.exception.IniTypeException;
import net.luis.utils.io.data.ini.exception.NoSuchIniElementException;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link IniDocument}.<br>
 *
 * @author Luis-St
 */
class IniDocumentTest {
	
	private static final IniConfig CUSTOM_CONFIG = new IniConfig(
		true, true, "\t", Set.of(';', '#'), '=', 1,
		false, false, false,
		Pattern.compile("^[a-zA-Z0-9._-]+$"), Pattern.compile("^[a-zA-Z0-9._-]+$"),
		IniConfig.NullStyle.EMPTY, StandardCharsets.UTF_8
	);
	
	private static final IniConfig SKIP_NULL_CONFIG = new IniConfig(
		true, true, "\t", Set.of(';', '#'), '=', 1,
		false, false, false,
		Pattern.compile("^[a-zA-Z0-9._-]+$"), Pattern.compile("^[a-zA-Z0-9._-]+$"),
		IniConfig.NullStyle.SKIP, StandardCharsets.UTF_8
	);
	
	@Test
	void constructorEmpty() {
		IniDocument document = new IniDocument();
		assertTrue(document.isEmpty());
		assertFalse(document.hasGlobalProperties());
		assertFalse(document.hasSections());
	}
	
	@Test
	void iniElementTypeChecks() {
		IniDocument document = new IniDocument();
		
		assertFalse(document.isIniNull());
		assertFalse(document.isIniValue());
		assertFalse(document.isIniSection());
		assertTrue(document.isIniDocument());
	}
	
	@Test
	void iniElementConversions() {
		IniDocument document = new IniDocument();
		
		assertSame(document, document.getAsIniDocument());
		assertThrows(IniTypeException.class, document::getAsIniValue);
		assertThrows(IniTypeException.class, document::getAsIniSection);
	}
	
	//region Global Properties Tests
	
	@Test
	void globalSizeAndHasGlobalProperties() {
		IniDocument document = new IniDocument();
		assertEquals(0, document.globalSize());
		assertFalse(document.hasGlobalProperties());
		
		document.addGlobal("key1", new IniValue("value1"));
		assertEquals(1, document.globalSize());
		assertTrue(document.hasGlobalProperties());
		
		document.addGlobal("key2", new IniValue(42));
		assertEquals(2, document.globalSize());
		
		document.removeGlobal("key1");
		assertEquals(1, document.globalSize());
		
		document.clearGlobal();
		assertEquals(0, document.globalSize());
		assertFalse(document.hasGlobalProperties());
	}
	
	@Test
	void containsGlobalKey() {
		IniDocument document = new IniDocument();
		
		assertFalse(document.containsGlobalKey("key"));
		assertFalse(document.containsGlobalKey(null));
		
		document.addGlobal("key", new IniValue("value"));
		assertTrue(document.containsGlobalKey("key"));
		assertFalse(document.containsGlobalKey("other"));
	}
	
	@Test
	void globalKeySetAndElements() {
		IniDocument document = new IniDocument();
		
		assertEquals(Set.of(), document.globalKeySet());
		assertTrue(document.globalElements().isEmpty());
		assertTrue(document.globalEntrySet().isEmpty());
		
		document.addGlobal("key1", new IniValue("value1"));
		document.addGlobal("key2", new IniValue(42));
		document.addGlobal("key3", IniNull.INSTANCE);
		
		Set<String> keys = document.globalKeySet();
		assertEquals(Set.of("key1", "key2", "key3"), keys);
		
		assertEquals(3, document.globalElements().size());
		assertTrue(document.globalElements().contains(new IniValue("value1")));
		assertTrue(document.globalElements().contains(new IniValue(42)));
		assertTrue(document.globalElements().contains(IniNull.INSTANCE));
		
		assertEquals(3, document.globalEntrySet().size());
	}
	
	@Test
	void forEachGlobalOperation() {
		IniDocument document = new IniDocument();
		
		assertThrows(NullPointerException.class, () -> document.forEachGlobal(null));
		
		document.forEachGlobal((key, value) -> fail("Should not be called for empty document"));
		
		document.addGlobal("key1", new IniValue("value1"));
		document.addGlobal("key2", new IniValue(42));
		
		AtomicInteger callCount = new AtomicInteger(0);
		document.forEachGlobal((key, value) -> {
			callCount.incrementAndGet();
			assertTrue(Set.of("key1", "key2").contains(key));
			assertNotNull(value);
		});
		
		assertEquals(2, callCount.get());
	}
	
	@Test
	void addGlobalWithIniElement() {
		IniDocument document = new IniDocument();
		
		assertThrows(NullPointerException.class, () -> document.addGlobal(null, new IniValue("value")));
		
		IniElement previous = document.addGlobal("key1", (IniElement) null);
		assertNull(previous);
		assertEquals(IniNull.INSTANCE, document.getGlobal("key1"));
		
		previous = document.addGlobal("key1", new IniValue("newValue"));
		assertEquals(IniNull.INSTANCE, previous);
		assertEquals(new IniValue("newValue"), document.getGlobal("key1"));
	}
	
	@Test
	void addGlobalWithPrimitiveTypes() {
		IniDocument document = new IniDocument();
		
		document.addGlobal("stringKey", "testString");
		assertEquals(new IniValue("testString"), document.getGlobal("stringKey"));
		
		document.addGlobal("nullStringKey", (String) null);
		assertEquals(IniNull.INSTANCE, document.getGlobal("nullStringKey"));
		
		document.addGlobal("booleanKey", true);
		assertEquals(new IniValue(true), document.getGlobal("booleanKey"));
		
		document.addGlobal("numberKey", Integer.valueOf(42));
		assertEquals(new IniValue(42), document.getGlobal("numberKey"));
		
		document.addGlobal("nullNumberKey", (Number) null);
		assertEquals(IniNull.INSTANCE, document.getGlobal("nullNumberKey"));
	}
	
	@Test
	void removeGlobalOperations() {
		IniDocument document = new IniDocument();
		
		assertNull(document.removeGlobal("nonexistent"));
		assertNull(document.removeGlobal(null));
		
		document.addGlobal("key1", new IniValue("value1"));
		document.addGlobal("key2", new IniValue(42));
		
		IniElement removed = document.removeGlobal("key1");
		assertEquals(new IniValue("value1"), removed);
		assertEquals(1, document.globalSize());
		assertFalse(document.containsGlobalKey("key1"));
		
		document.clearGlobal();
		assertEquals(0, document.globalSize());
		assertFalse(document.hasGlobalProperties());
	}
	
	@Test
	void getGlobalOperations() {
		IniDocument document = new IniDocument();
		
		assertThrows(NullPointerException.class, () -> document.getGlobal(null));
		assertNull(document.getGlobal("nonexistent"));
		
		document.addGlobal("stringKey", new IniValue("stringValue"));
		document.addGlobal("numberKey", new IniValue(42));
		document.addGlobal("nullKey", IniNull.INSTANCE);
		
		assertEquals(new IniValue("stringValue"), document.getGlobal("stringKey"));
		assertEquals(new IniValue(42), document.getGlobal("numberKey"));
		assertEquals(IniNull.INSTANCE, document.getGlobal("nullKey"));
	}
	
	@Test
	void getGlobalIniValueOperations() {
		IniDocument document = new IniDocument();
		
		assertThrows(NoSuchIniElementException.class, () -> document.getGlobalIniValue("nonexistent"));
		
		document.addGlobal("valueKey", new IniValue("test"));
		document.addGlobal("nullKey", IniNull.INSTANCE);
		
		assertEquals(new IniValue("test"), document.getGlobalIniValue("valueKey"));
		assertThrows(IniTypeException.class, () -> document.getGlobalIniValue("nullKey"));
	}
	
	@Test
	void getGlobalAsSpecificTypesSuccess() {
		IniDocument document = new IniDocument();
		document.addGlobal("stringKey", new IniValue("stringValue"));
		document.addGlobal("numberKey", new IniValue(42));
		document.addGlobal("booleanKey", new IniValue(true));
		document.addGlobal("doubleKey", new IniValue(3.14));
		
		assertEquals("stringValue", document.getGlobalAsString("stringKey"));
		assertEquals(42, document.getGlobalAsNumber("numberKey"));
		assertEquals(42, document.getGlobalAsInteger("numberKey"));
		assertEquals(42L, document.getGlobalAsLong("numberKey"));
		assertEquals(3.14, document.getGlobalAsDouble("doubleKey"));
		assertTrue(document.getGlobalAsBoolean("booleanKey"));
	}
	
	@Test
	void getGlobalAsSpecificTypesExceptions() {
		IniDocument document = new IniDocument();
		document.addGlobal("stringKey", new IniValue("stringValue"));
		document.addGlobal("nullKey", IniNull.INSTANCE);
		
		assertThrows(NoSuchIniElementException.class, () -> document.getGlobalAsString("nonexistent"));
		assertThrows(NoSuchIniElementException.class, () -> document.getGlobalAsBoolean("nonexistent"));
		assertThrows(NoSuchIniElementException.class, () -> document.getGlobalAsNumber("nonexistent"));
		
		assertThrows(IniTypeException.class, () -> document.getGlobalAsString("nullKey"));
		assertThrows(IniTypeException.class, () -> document.getGlobalAsBoolean("nullKey"));
		assertThrows(IniTypeException.class, () -> document.getGlobalAsNumber("nullKey"));
		
		assertThrows(IniTypeException.class, () -> document.getGlobalAsBoolean("stringKey"));
		assertThrows(IniTypeException.class, () -> document.getGlobalAsNumber("stringKey"));
	}
	
	//endregion
	
	//region Section Tests
	
	@Test
	void sectionCountAndHasSections() {
		IniDocument document = new IniDocument();
		assertEquals(0, document.sectionCount());
		assertFalse(document.hasSections());
		
		IniSection section1 = new IniSection("section1");
		document.addSection(section1);
		assertEquals(1, document.sectionCount());
		assertTrue(document.hasSections());
		
		IniSection section2 = new IniSection("section2");
		document.addSection(section2);
		assertEquals(2, document.sectionCount());
		
		document.removeSection("section1");
		assertEquals(1, document.sectionCount());
		
		document.clearSections();
		assertEquals(0, document.sectionCount());
		assertFalse(document.hasSections());
	}
	
	@Test
	void containsSection() {
		IniDocument document = new IniDocument();
		
		assertFalse(document.containsSection("section"));
		assertFalse(document.containsSection(null));
		
		IniSection section = new IniSection("mySection");
		document.addSection(section);
		assertTrue(document.containsSection("mySection"));
		assertFalse(document.containsSection("other"));
	}
	
	@Test
	void sectionNamesAndSections() {
		IniDocument document = new IniDocument();
		
		assertEquals(Set.of(), document.sectionNames());
		assertTrue(document.sections().isEmpty());
		
		IniSection section1 = new IniSection("section1");
		IniSection section2 = new IniSection("section2");
		document.addSection(section1);
		document.addSection(section2);
		
		Set<String> names = document.sectionNames();
		assertEquals(Set.of("section1", "section2"), names);
		
		assertEquals(2, document.sections().size());
		assertTrue(document.sections().contains(section1));
		assertTrue(document.sections().contains(section2));
	}
	
	@Test
	void forEachSectionOperation() {
		IniDocument document = new IniDocument();
		
		assertThrows(NullPointerException.class, () -> document.forEachSection(null));
		
		document.forEachSection((name, section) -> fail("Should not be called for empty document"));
		
		IniSection section1 = new IniSection("section1");
		IniSection section2 = new IniSection("section2");
		document.addSection(section1);
		document.addSection(section2);
		
		AtomicInteger callCount = new AtomicInteger(0);
		document.forEachSection((name, section) -> {
			callCount.incrementAndGet();
			assertTrue(Set.of("section1", "section2").contains(name));
			assertNotNull(section);
		});
		
		assertEquals(2, callCount.get());
	}
	
	@Test
	void addSectionOperations() {
		IniDocument document = new IniDocument();
		
		assertThrows(NullPointerException.class, () -> document.addSection(null));
		
		IniSection section1 = new IniSection("mySection");
		section1.add("key1", new IniValue("value1"));
		
		IniSection previous = document.addSection(section1);
		assertNull(previous);
		assertEquals(section1, document.getSection("mySection"));
		
		IniSection section2 = new IniSection("mySection");
		section2.add("key2", new IniValue("value2"));
		
		previous = document.addSection(section2);
		assertEquals(section1, previous);
		assertEquals(section2, document.getSection("mySection"));
	}
	
	@Test
	void createSectionOperations() {
		IniDocument document = new IniDocument();
		
		assertThrows(NullPointerException.class, () -> document.createSection(null));
		assertThrows(IllegalArgumentException.class, () -> document.createSection(""));
		assertThrows(IllegalArgumentException.class, () -> document.createSection("   "));
		
		IniSection section = document.createSection("newSection");
		assertNotNull(section);
		assertEquals("newSection", section.getName());
		assertTrue(section.isEmpty());
		assertTrue(document.containsSection("newSection"));
		assertSame(section, document.getSection("newSection"));
		
		IniSection anotherSection = document.createSection("newSection");
		assertNotSame(section, anotherSection);
		assertSame(anotherSection, document.getSection("newSection"));
	}
	
	@Test
	void getOrCreateSectionOperations() {
		IniDocument document = new IniDocument();
		
		assertThrows(NullPointerException.class, () -> document.getOrCreateSection(null));
		assertThrows(IllegalArgumentException.class, () -> document.getOrCreateSection(""));
		
		IniSection section = document.getOrCreateSection("mySection");
		assertNotNull(section);
		assertEquals("mySection", section.getName());
		assertTrue(document.containsSection("mySection"));
		
		IniSection sameSection = document.getOrCreateSection("mySection");
		assertSame(section, sameSection);
		
		section.add("key", new IniValue("value"));
		sameSection = document.getOrCreateSection("mySection");
		assertSame(section, sameSection);
		assertEquals(new IniValue("value"), sameSection.get("key"));
	}
	
	@Test
	void removeSectionOperations() {
		IniDocument document = new IniDocument();
		
		assertNull(document.removeSection("nonexistent"));
		assertNull(document.removeSection(null));
		
		IniSection section1 = new IniSection("section1");
		IniSection section2 = new IniSection("section2");
		document.addSection(section1);
		document.addSection(section2);
		
		IniSection removed = document.removeSection("section1");
		assertEquals(section1, removed);
		assertEquals(1, document.sectionCount());
		assertFalse(document.containsSection("section1"));
		
		document.clearSections();
		assertEquals(0, document.sectionCount());
		assertFalse(document.hasSections());
	}
	
	@Test
	void getSectionOperations() {
		IniDocument document = new IniDocument();
		
		assertThrows(NullPointerException.class, () -> document.getSection(null));
		assertNull(document.getSection("nonexistent"));
		
		IniSection section = new IniSection("mySection");
		section.add("key", new IniValue("value"));
		document.addSection(section);
		
		IniSection retrieved = document.getSection("mySection");
		assertEquals(section, retrieved);
		assertEquals(new IniValue("value"), retrieved.get("key"));
	}
	
	@Test
	void requireSectionOperations() {
		IniDocument document = new IniDocument();
		
		assertThrows(NullPointerException.class, () -> document.requireSection(null));
		assertThrows(NoSuchIniElementException.class, () -> document.requireSection("nonexistent"));
		
		IniSection section = new IniSection("mySection");
		document.addSection(section);
		
		IniSection retrieved = document.requireSection("mySection");
		assertSame(section, retrieved);
	}
	
	//endregion
	
	@Test
	void isEmptyAndClearOperations() {
		IniDocument document = new IniDocument();
		assertTrue(document.isEmpty());
		
		document.addGlobal("key", new IniValue("value"));
		assertFalse(document.isEmpty());
		
		document.clearGlobal();
		assertTrue(document.isEmpty());
		
		IniSection section = new IniSection("section");
		document.addSection(section);
		assertFalse(document.isEmpty());
		
		document.clearSections();
		assertTrue(document.isEmpty());
		
		document.addGlobal("key", new IniValue("value"));
		document.addSection(new IniSection("section"));
		assertFalse(document.isEmpty());
		
		document.clear();
		assertTrue(document.isEmpty());
		assertFalse(document.hasGlobalProperties());
		assertFalse(document.hasSections());
	}
	
	@Test
	void equalsAndHashCode() {
		IniDocument doc1 = new IniDocument();
		IniDocument doc2 = new IniDocument();
		IniDocument doc3 = new IniDocument();
		
		assertEquals(doc1, doc2);
		assertEquals(doc1.hashCode(), doc2.hashCode());
		
		doc1.addGlobal("key1", new IniValue("value1"));
		doc1.addSection(new IniSection("section1"));
		
		doc2.addGlobal("key1", new IniValue("value1"));
		doc2.addSection(new IniSection("section1"));
		
		assertEquals(doc1, doc2);
		assertEquals(doc1.hashCode(), doc2.hashCode());
		
		doc3.addGlobal("key1", new IniValue("different"));
		assertNotEquals(doc1, doc3);
		
		assertNotEquals(doc1, null);
		assertNotEquals(doc1, "not a document");
		
		assertEquals(doc1, doc1);
		
		IniDocument doc4 = new IniDocument();
		doc4.addGlobal("key1", new IniValue("value1"));
		assertNotEquals(doc1, doc4);
		
		IniDocument doc5 = new IniDocument();
		doc5.addSection(new IniSection("section1"));
		assertNotEquals(doc1, doc5);
	}
	
	@Test
	void toStringWithDefaultConfig() {
		IniDocument document = new IniDocument();
		assertEquals("", document.toString());
		
		document.addGlobal("globalKey", new IniValue("globalValue"));
		String result = document.toString();
		assertTrue(result.contains("globalKey"));
		assertTrue(result.contains("globalValue"));
		
		IniSection section = new IniSection("mySection");
		section.add("sectionKey", new IniValue("sectionValue"));
		document.addSection(section);
		
		result = document.toString();
		assertTrue(result.contains("globalKey"));
		assertTrue(result.contains("[mySection]"));
		assertTrue(result.contains("sectionKey"));
		assertTrue(result.contains("sectionValue"));
	}
	
	@Test
	void toStringWithCustomConfig() {
		IniDocument document = new IniDocument();
		
		assertThrows(NullPointerException.class, () -> document.toString(null));
		
		assertEquals("", document.toString(CUSTOM_CONFIG));
		
		document.addGlobal("key1", new IniValue("value1"));
		IniSection section = new IniSection("section");
		section.add("key2", new IniValue(42));
		document.addSection(section);
		
		String result = document.toString(CUSTOM_CONFIG);
		assertTrue(result.contains("key1"));
		assertTrue(result.contains("value1"));
		assertTrue(result.contains("[section]"));
		assertTrue(result.contains("key2"));
		assertTrue(result.contains("42"));
	}
	
	@Test
	void toStringSkipsNullValues() {
		IniDocument document = new IniDocument();
		document.addGlobal("key1", new IniValue("value1"));
		document.addGlobal("nullKey", IniNull.INSTANCE);
		document.addGlobal("key2", new IniValue(42));
		
		String result = document.toString(SKIP_NULL_CONFIG);
		assertTrue(result.contains("key1"));
		assertFalse(result.contains("nullKey"));
		assertTrue(result.contains("key2"));
	}
	
	@Test
	void preservesInsertionOrder() {
		IniDocument document = new IniDocument();
		
		document.addGlobal("third", new IniValue(3));
		document.addGlobal("first", new IniValue(1));
		document.addGlobal("second", new IniValue(2));
		
		String[] expectedGlobalOrder = { "third", "first", "second" };
		String[] actualGlobalOrder = document.globalKeySet().toArray(new String[0]);
		assertArrayEquals(expectedGlobalOrder, actualGlobalOrder);
		
		document.addSection(new IniSection("zSection"));
		document.addSection(new IniSection("aSection"));
		document.addSection(new IniSection("mSection"));
		
		String[] expectedSectionOrder = { "zSection", "aSection", "mSection" };
		String[] actualSectionOrder = document.sectionNames().toArray(new String[0]);
		assertArrayEquals(expectedSectionOrder, actualSectionOrder);
	}
	
	@Test
	void complexDocumentScenario() {
		IniDocument document = new IniDocument();
		
		document.addGlobal("appName", "MyApplication");
		document.addGlobal("version", 1);
		document.addGlobal("debug", true);
		
		IniSection databaseSection = document.getOrCreateSection("database");
		databaseSection.add("host", new IniValue("localhost"));
		databaseSection.add("port", new IniValue(5432));
		databaseSection.add("name", new IniValue("mydb"));
		
		IniSection loggingSection = document.createSection("logging");
		loggingSection.add("level", new IniValue("INFO"));
		loggingSection.add("file", new IniValue("/var/log/app.log"));
		
		assertEquals(3, document.globalSize());
		assertEquals(2, document.sectionCount());
		
		assertEquals("MyApplication", document.getGlobalAsString("appName"));
		assertEquals(1, document.getGlobalAsInteger("version"));
		assertTrue(document.getGlobalAsBoolean("debug"));
		
		assertEquals("localhost", document.requireSection("database").getAsString("host"));
		assertEquals(5432, document.requireSection("database").getAsInteger("port"));
		
		String result = document.toString();
		assertTrue(result.contains("appName"));
		assertTrue(result.contains("[database]"));
		assertTrue(result.contains("[logging]"));
	}
}
