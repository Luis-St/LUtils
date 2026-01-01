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

package net.luis.utils.io.data.ini;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link IniBuilder}.<br>
 *
 * @author Luis-St
 */
class IniBuilderTest {
	
	private static final IniConfig CUSTOM_CONFIG = new IniConfig(
		true, true, "\t", Set.of(';', '#'), '=', 1,
		false, false, false,
		Pattern.compile("^[a-zA-Z0-9._-]+$"), Pattern.compile("^[a-zA-Z0-9._-]+$"),
		IniConfig.NullStyle.EMPTY, StandardCharsets.UTF_8
	);
	
	@Test
	void createEmptyDocument() {
		IniDocument document = IniBuilder.document().build();
		assertTrue(document.isEmpty());
		assertFalse(document.hasGlobalProperties());
		assertFalse(document.hasSections());
	}
	
	@Test
	void buildSimpleDocumentWithGlobals() {
		IniDocument document = IniBuilder.document()
			.addGlobal("key1", "value1")
			.addGlobal("key2", 42)
			.addGlobal("key3", true)
			.build();
		
		assertEquals(3, document.globalSize());
		assertEquals("value1", document.getGlobalAsString("key1"));
		assertEquals(42, document.getGlobalAsInteger("key2"));
		assertTrue(document.getGlobalAsBoolean("key3"));
	}
	
	@Test
	void addGlobalAllPrimitiveTypes() {
		IniDocument document = IniBuilder.document()
			.addGlobal("stringKey", "testString")
			.addGlobal("booleanKey", true)
			.addGlobal("intKey", 42)
			.addGlobal("longKey", 100L)
			.addGlobal("doubleKey", 3.14)
			.addGlobal("nullStringKey", (String) null)
			.addGlobal("nullNumberKey", (Number) null)
			.build();
		
		assertEquals(7, document.globalSize());
		assertEquals("testString", document.getGlobalAsString("stringKey"));
		assertTrue(document.getGlobalAsBoolean("booleanKey"));
		assertEquals(42, document.getGlobalAsInteger("intKey"));
		assertEquals(100L, document.getGlobalAsLong("longKey"));
		assertEquals(3.14, document.getGlobalAsDouble("doubleKey"));
		assertEquals(IniNull.INSTANCE, document.getGlobal("nullStringKey"));
		assertEquals(IniNull.INSTANCE, document.getGlobal("nullNumberKey"));
	}
	
	@Test
	void addGlobalWithIniElement() {
		IniDocument document = IniBuilder.document()
			.addGlobal("valueKey", new IniValue("elementValue"))
			.addGlobal("nullKey", (IniElement) null)
			.build();
		
		assertEquals(2, document.globalSize());
		assertEquals(new IniValue("elementValue"), document.getGlobal("valueKey"));
		assertEquals(IniNull.INSTANCE, document.getGlobal("nullKey"));
	}
	
	@Test
	void addGlobalNullKeyValidation() {
		IniBuilder builder = IniBuilder.document();
		assertThrows(NullPointerException.class, () -> builder.addGlobal(null, "value"));
		assertThrows(NullPointerException.class, () -> builder.addGlobal(null, true));
		assertThrows(NullPointerException.class, () -> builder.addGlobal(null, 42));
		assertThrows(NullPointerException.class, () -> builder.addGlobal(null, (IniElement) null));
	}
	
	@Test
	void addNullKeyValidationInSection() {
		IniBuilder builder = IniBuilder.document().startSection("section");
		assertThrows(NullPointerException.class, () -> builder.add(null, "value"));
		assertThrows(NullPointerException.class, () -> builder.add(null, true));
		assertThrows(NullPointerException.class, () -> builder.add(null, 42));
		assertThrows(NullPointerException.class, () -> builder.add(null, (IniElement) null));
	}
	
	@Test
	void conditionalBuildingGlobal() {
		IniDocument document = IniBuilder.document()
			.addGlobalIf(true, "included", "yes")
			.addGlobalIf(false, "excluded", "no")
			.build();
		
		assertEquals(1, document.globalSize());
		assertTrue(document.containsGlobalKey("included"));
		assertFalse(document.containsGlobalKey("excluded"));
	}
	
	@Test
	void buildSimpleSection() {
		IniDocument document = IniBuilder.document()
			.startSection("mySection")
			.add("key1", "value1")
			.add("key2", 42)
			.add("key3", true)
			.endSection()
			.build();
		
		assertEquals(1, document.sectionCount());
		IniSection section = document.requireSection("mySection");
		assertEquals(3, section.size());
		assertEquals("value1", section.getAsString("key1"));
		assertEquals(42, section.getAsInteger("key2"));
		assertTrue(section.getAsBoolean("key3"));
	}
	
	@Test
	void addAllPrimitiveTypesToSection() {
		IniDocument document = IniBuilder.document()
			.startSection("section")
			.add("stringKey", "testString")
			.add("booleanKey", true)
			.add("intKey", 42)
			.add("longKey", 100L)
			.add("doubleKey", 3.14)
			.add("nullStringKey", (String) null)
			.add("nullNumberKey", (Number) null)
			.endSection()
			.build();
		
		IniSection section = document.requireSection("section");
		assertEquals(7, section.size());
		assertEquals("testString", section.getAsString("stringKey"));
		assertTrue(section.getAsBoolean("booleanKey"));
		assertEquals(42, section.getAsInteger("intKey"));
		assertEquals(100L, section.getAsLong("longKey"));
		assertEquals(3.14, section.getAsDouble("doubleKey"));
		assertEquals(IniNull.INSTANCE, section.get("nullStringKey"));
		assertEquals(IniNull.INSTANCE, section.get("nullNumberKey"));
	}
	
	@Test
	void addIniElementToSection() {
		IniDocument document = IniBuilder.document()
			.startSection("section")
			.add("valueKey", new IniValue("elementValue"))
			.add("nullKey", (IniElement) null)
			.endSection()
			.build();
		
		IniSection section = document.requireSection("section");
		assertEquals(2, section.size());
		assertEquals(new IniValue("elementValue"), section.get("valueKey"));
		assertEquals(IniNull.INSTANCE, section.get("nullKey"));
	}
	
	@Test
	void conditionalBuildingInSection() {
		IniDocument document = IniBuilder.document()
			.startSection("section")
			.addIf(true, "included", "yes")
			.addIf(false, "excluded", "no")
			.addIf(true, "boolIncluded", true)
			.addIf(false, "boolExcluded", false)
			.addIf(true, "numIncluded", 42)
			.addIf(false, "numExcluded", 0)
			.endSection()
			.build();
		
		IniSection section = document.requireSection("section");
		assertEquals(3, section.size());
		assertTrue(section.containsKey("included"));
		assertFalse(section.containsKey("excluded"));
		assertTrue(section.containsKey("boolIncluded"));
		assertFalse(section.containsKey("boolExcluded"));
		assertTrue(section.containsKey("numIncluded"));
		assertFalse(section.containsKey("numExcluded"));
	}
	
	@Test
	void multipleSections() {
		IniDocument document = IniBuilder.document()
			.startSection("section1")
			.add("key1", "value1")
			.endSection()
			.startSection("section2")
			.add("key2", "value2")
			.endSection()
			.startSection("section3")
			.add("key3", "value3")
			.endSection()
			.build();
		
		assertEquals(3, document.sectionCount());
		assertTrue(document.containsSection("section1"));
		assertTrue(document.containsSection("section2"));
		assertTrue(document.containsSection("section3"));
	}
	
	@Test
	void mixedGlobalsAndSections() {
		IniDocument document = IniBuilder.document()
			.addGlobal("globalKey", "globalValue")
			.startSection("section1")
			.add("sectionKey", "sectionValue")
			.endSection()
			.build();
		
		assertTrue(document.hasGlobalProperties());
		assertTrue(document.hasSections());
		assertEquals("globalValue", document.getGlobalAsString("globalKey"));
		assertEquals("sectionValue", document.requireSection("section1").getAsString("sectionKey"));
	}
	
	@Test
	void addPrebuiltSection() {
		IniSection section = new IniSection("prebuilt");
		section.add("key", new IniValue("value"));
		
		IniDocument document = IniBuilder.document()
			.addSection(section)
			.build();
		
		assertEquals(1, document.sectionCount());
		assertSame(section, document.getSection("prebuilt"));
	}
	
	@Test
	void addSectionNullValidation() {
		IniBuilder builder = IniBuilder.document();
		assertThrows(NullPointerException.class, () -> builder.addSection(null));
	}
	
	@Test
	void startSectionValidation() {
		IniBuilder builder = IniBuilder.document();
		assertThrows(NullPointerException.class, () -> builder.startSection(null));
		assertThrows(IllegalArgumentException.class, () -> builder.startSection(""));
		assertThrows(IllegalArgumentException.class, () -> builder.startSection("   "));
	}
	
	@Test
	void contextValidationNotInSection() {
		IniBuilder builder = IniBuilder.document();
		
		assertThrows(IllegalStateException.class, () -> builder.add("key", "value"));
		assertThrows(IllegalStateException.class, () -> builder.add("key", true));
		assertThrows(IllegalStateException.class, () -> builder.add("key", 42));
		assertThrows(IllegalStateException.class, () -> builder.add("key", (IniElement) null));
		assertThrows(IllegalStateException.class, () -> builder.addIf(true, "key", "value"));
		assertThrows(IllegalStateException.class, builder::endSection);
	}
	
	@Test
	void contextValidationInSection() {
		IniBuilder builder = IniBuilder.document()
			.startSection("section");
		
		assertThrows(IllegalStateException.class, () -> builder.addGlobal("key", "value"));
		assertThrows(IllegalStateException.class, () -> builder.addGlobal("key", true));
		assertThrows(IllegalStateException.class, () -> builder.addGlobal("key", 42));
		assertThrows(IllegalStateException.class, () -> builder.addGlobal("key", (IniElement) null));
		assertThrows(IllegalStateException.class, () -> builder.addGlobalIf(true, "key", "value"));
		assertThrows(IllegalStateException.class, () -> builder.addSection(new IniSection("other")));
	}
	
	@Test
	void nestingDepthOperations() {
		IniBuilder builder = IniBuilder.document();
		
		assertEquals(0, builder.getNestingDepth());
		assertTrue(builder.isAtDocumentLevel());
		assertFalse(builder.isInSection());
		
		builder.startSection("section");
		assertEquals(1, builder.getNestingDepth());
		assertFalse(builder.isAtDocumentLevel());
		assertTrue(builder.isInSection());
		
		builder.endSection();
		assertEquals(0, builder.getNestingDepth());
		assertTrue(builder.isAtDocumentLevel());
		assertFalse(builder.isInSection());
	}
	
	@Test
	void buildAutoClosesSections() {
		IniDocument document = IniBuilder.document()
			.startSection("section")
			.add("key", "value")
			// No endSection() call
			.build();
		
		assertEquals(1, document.sectionCount());
		assertEquals("value", document.requireSection("section").getAsString("key"));
	}
	
	@Test
	void buildMultipleTimesReturnsConsistentResults() {
		IniBuilder builder = IniBuilder.document()
			.addGlobal("key", "value");
		
		IniDocument doc1 = builder.build();
		IniDocument doc2 = builder.build();
		
		assertSame(doc1, doc2);
		assertEquals(doc1.getGlobal("key"), doc2.getGlobal("key"));
	}
	
	@Test
	void toStringWithDefaultConfig() {
		IniBuilder builder = IniBuilder.document()
			.addGlobal("key", "value")
			.startSection("section")
			.add("sectionKey", "sectionValue")
			.endSection();
		
		String result = builder.toString();
		assertTrue(result.contains("key"));
		assertTrue(result.contains("value"));
		assertTrue(result.contains("[section]"));
		assertTrue(result.contains("sectionKey"));
		assertTrue(result.contains("sectionValue"));
	}
	
	@Test
	void toStringWithCustomConfig() {
		IniBuilder builder = IniBuilder.document()
			.addGlobal("key", "value");
		
		String result = builder.toString(CUSTOM_CONFIG);
		assertTrue(result.contains("key"));
		assertTrue(result.contains("value"));
	}
	
	@Test
	void toStringNullConfigValidation() {
		IniBuilder builder = IniBuilder.document().addGlobal("key", "value");
		assertThrows(NullPointerException.class, () -> builder.toString(null));
	}
	
	@Test
	void methodChainingConsistency() {
		IniBuilder builder = IniBuilder.document();
		
		assertSame(builder, builder.addGlobal("key1", "value"));
		assertSame(builder, builder.addGlobal("key2", true));
		assertSame(builder, builder.addGlobal("key3", 42));
		assertSame(builder, builder.addGlobal("key4", (IniElement) null));
		assertSame(builder, builder.addGlobalIf(true, "key5", "value"));
		
		assertSame(builder, builder.startSection("section"));
		assertSame(builder, builder.add("sectionKey1", "value"));
		assertSame(builder, builder.add("sectionKey2", true));
		assertSame(builder, builder.add("sectionKey3", 42));
		assertSame(builder, builder.add("sectionKey4", (IniElement) null));
		assertSame(builder, builder.addIf(true, "sectionKey5", "value"));
		assertSame(builder, builder.addIf(true, "sectionKey6", true));
		assertSame(builder, builder.addIf(true, "sectionKey7", 42));
		assertSame(builder, builder.endSection());
		
		assertSame(builder, builder.addSection(new IniSection("otherSection")));
	}
	
	@Test
	void complexRealWorldExample() {
		IniDocument document = IniBuilder.document()
			.addGlobal("appName", "MyApplication")
			.addGlobal("version", 1)
			.addGlobal("debug", false)
			.startSection("database")
			.add("host", "localhost")
			.add("port", 5432)
			.add("name", "mydb")
			.add("username", "admin")
			.addIf(true, "password", "secret")
			.addIf(false, "ssl", false)
			.endSection()
			.startSection("logging")
			.add("level", "INFO")
			.add("file", "/var/log/app.log")
			.add("console", true)
			.endSection()
			.startSection("features")
			.add("feature1", true)
			.add("feature2", false)
			.add("maxRetries", 3)
			.endSection()
			.build();
		
		assertEquals(3, document.globalSize());
		assertEquals(3, document.sectionCount());
		
		assertEquals("MyApplication", document.getGlobalAsString("appName"));
		assertEquals(1, document.getGlobalAsInteger("version"));
		assertFalse(document.getGlobalAsBoolean("debug"));
		
		IniSection dbSection = document.requireSection("database");
		assertEquals("localhost", dbSection.getAsString("host"));
		assertEquals(5432, dbSection.getAsInteger("port"));
		assertTrue(dbSection.containsKey("password"));
		assertFalse(dbSection.containsKey("ssl"));
		
		IniSection loggingSection = document.requireSection("logging");
		assertEquals("INFO", loggingSection.getAsString("level"));
		assertTrue(loggingSection.getAsBoolean("console"));
		
		IniSection featuresSection = document.requireSection("features");
		assertTrue(featuresSection.getAsBoolean("feature1"));
		assertFalse(featuresSection.getAsBoolean("feature2"));
		assertEquals(3, featuresSection.getAsInteger("maxRetries"));
	}
	
	@Test
	void sameNameSectionReturnsExisting() {
		IniDocument document = IniBuilder.document()
			.startSection("section")
			.add("key1", "value1")
			.endSection()
			.startSection("section")
			.add("key2", "value2")
			.endSection()
			.build();
		
		assertEquals(1, document.sectionCount());
		IniSection section = document.requireSection("section");
		assertEquals(2, section.size());
		assertTrue(section.containsKey("key1"));
		assertTrue(section.containsKey("key2"));
	}
}
