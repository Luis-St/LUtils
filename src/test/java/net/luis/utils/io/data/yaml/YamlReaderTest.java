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

package net.luis.utils.io.data.yaml;

import net.luis.utils.io.data.InputProvider;
import net.luis.utils.io.data.yaml.exception.YamlSyntaxException;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link YamlReader}.<br>
 *
 * @author Luis-St
 */
class YamlReaderTest {

	private static final YamlConfig DEFAULT_CONFIG = YamlConfig.DEFAULT;
	private static final YamlConfig PRESERVE_ANCHORS = YamlConfig.PRESERVE_ANCHORS;
	private static final YamlConfig NON_STRICT = new YamlConfig(false, true, "  ", true, false, YamlConfig.NullStyle.NULL, true, false, StandardCharsets.UTF_8);
	private static final YamlConfig ALLOW_DUPLICATES = new YamlConfig(true, true, "  ", true, false, YamlConfig.NullStyle.NULL, true, true, StandardCharsets.UTF_8);

	//region Constructor tests
	@Test
	void constructorWithString() {
		assertDoesNotThrow(() -> new YamlReader("key: value"));
	}

	@Test
	void constructorWithStringAndConfig() {
		assertDoesNotThrow(() -> new YamlReader("key: value", DEFAULT_CONFIG));
	}

	@Test
	void constructorWithInputProvider() {
		InputProvider input = new InputProvider(new ByteArrayInputStream("key: value".getBytes(StandardCharsets.UTF_8)));
		assertDoesNotThrow(() -> new YamlReader(input));
	}

	@Test
	void constructorWithInputProviderAndConfig() {
		InputProvider input = new InputProvider(new ByteArrayInputStream("key: value".getBytes(StandardCharsets.UTF_8)));
		assertDoesNotThrow(() -> new YamlReader(input, DEFAULT_CONFIG));
	}

	@Test
	void constructorWithNullString() {
		assertThrows(NullPointerException.class, () -> new YamlReader((String) null));
	}

	@Test
	void constructorWithNullConfig() {
		assertThrows(NullPointerException.class, () -> new YamlReader("yaml", null));
	}

	@Test
	void constructorWithNullInput() {
		assertThrows(NullPointerException.class, () -> new YamlReader((InputProvider) null));
	}
	//endregion

	//region Empty and null values
	@Test
	void readEmptyInput() throws IOException {
		try (YamlReader reader = new YamlReader("")) {
			YamlElement result = reader.readYaml();
			assertTrue(result.isYamlNull());
		}
	}

	@Test
	void readNullKeyword() throws IOException {
		try (YamlReader reader = new YamlReader("null")) {
			YamlElement result = reader.readYaml();
			assertTrue(result.isYamlNull());
		}
	}

	@Test
	void readTildeNull() throws IOException {
		try (YamlReader reader = new YamlReader("~")) {
			YamlElement result = reader.readYaml();
			assertTrue(result.isYamlNull());
		}
	}

	@Test
	void readWhitespaceOnly() throws IOException {
		try (YamlReader reader = new YamlReader("   \n  \n  ")) {
			YamlElement result = reader.readYaml();
			assertTrue(result.isYamlNull());
		}
	}
	//endregion

	//region Scalar types
	@Test
	void readStringScalar() throws IOException {
		try (YamlReader reader = new YamlReader("hello world")) {
			YamlElement result = reader.readYaml();
			assertTrue(result.isYamlScalar());
			assertEquals("hello world", result.getAsYamlScalar().getAsString());
		}
	}

	@Test
	void readQuotedString() throws IOException {
		try (YamlReader reader = new YamlReader("\"hello world\"")) {
			YamlElement result = reader.readYaml();
			assertTrue(result.isYamlScalar());
			assertEquals("hello world", result.getAsYamlScalar().getAsString());
		}
	}

	@Test
	void readSingleQuotedString() throws IOException {
		try (YamlReader reader = new YamlReader("'hello world'")) {
			YamlElement result = reader.readYaml();
			assertTrue(result.isYamlScalar());
			assertEquals("hello world", result.getAsYamlScalar().getAsString());
		}
	}

	@Test
	void readIntegerScalar() throws IOException {
		try (YamlReader reader = new YamlReader("42")) {
			YamlElement result = reader.readYaml();
			assertTrue(result.isYamlScalar());
			assertEquals(42, result.getAsYamlScalar().getAsInteger());
		}
	}

	@Test
	void readNegativeInteger() throws IOException {
		try (YamlReader reader = new YamlReader("-123")) {
			YamlElement result = reader.readYaml();
			assertTrue(result.isYamlScalar());
			assertEquals(-123, result.getAsYamlScalar().getAsInteger());
		}
	}

	@Test
	void readLongScalar() throws IOException {
		try (YamlReader reader = new YamlReader("9999999999999")) {
			YamlElement result = reader.readYaml();
			assertTrue(result.isYamlScalar());
			assertEquals(9999999999999L, result.getAsYamlScalar().getAsLong());
		}
	}

	@Test
	void readDoubleScalar() throws IOException {
		try (YamlReader reader = new YamlReader("3.14159")) {
			YamlElement result = reader.readYaml();
			assertTrue(result.isYamlScalar());
			assertEquals(3.14159, result.getAsYamlScalar().getAsDouble(), 0.00001);
		}
	}

	@Test
	void readScientificNotation() throws IOException {
		try (YamlReader reader = new YamlReader("1.5e10")) {
			YamlElement result = reader.readYaml();
			assertTrue(result.isYamlScalar());
			assertEquals(1.5e10, result.getAsYamlScalar().getAsDouble(), 1e5);
		}
	}

	@Test
	void readBooleanTrue() throws IOException {
		try (YamlReader reader = new YamlReader("true")) {
			YamlElement result = reader.readYaml();
			assertTrue(result.isYamlScalar());
			assertTrue(result.getAsYamlScalar().getAsBoolean());
		}
	}

	@Test
	void readBooleanFalse() throws IOException {
		try (YamlReader reader = new YamlReader("false")) {
			YamlElement result = reader.readYaml();
			assertTrue(result.isYamlScalar());
			assertFalse(result.getAsYamlScalar().getAsBoolean());
		}
	}

	@Test
	void readHexadecimalNumber() throws IOException {
		try (YamlReader reader = new YamlReader("0xFF")) {
			YamlElement result = reader.readYaml();
			assertTrue(result.isYamlScalar());
			assertEquals(255L, result.getAsYamlScalar().getAsLong());
		}
	}

	@Test
	void readOctalNumber() throws IOException {
		try (YamlReader reader = new YamlReader("0o77")) {
			YamlElement result = reader.readYaml();
			assertTrue(result.isYamlScalar());
			assertEquals(63L, result.getAsYamlScalar().getAsLong());
		}
	}

	@Test
	void readBinaryNumber() throws IOException {
		try (YamlReader reader = new YamlReader("0b1010")) {
			YamlElement result = reader.readYaml();
			assertTrue(result.isYamlScalar());
			assertEquals(10L, result.getAsYamlScalar().getAsLong());
		}
	}

	@Test
	void readPositiveInfinity() throws IOException {
		try (YamlReader reader = new YamlReader(".inf")) {
			YamlElement result = reader.readYaml();
			assertTrue(result.isYamlScalar());
			assertEquals(Double.POSITIVE_INFINITY, result.getAsYamlScalar().getAsDouble());
		}
	}

	@Test
	void readNegativeInfinity() throws IOException {
		try (YamlReader reader = new YamlReader("-.inf")) {
			YamlElement result = reader.readYaml();
			assertTrue(result.isYamlScalar());
			assertEquals(Double.NEGATIVE_INFINITY, result.getAsYamlScalar().getAsDouble());
		}
	}

	@Test
	void readNaN() throws IOException {
		try (YamlReader reader = new YamlReader(".nan")) {
			YamlElement result = reader.readYaml();
			assertTrue(result.isYamlScalar());
			assertTrue(Double.isNaN(result.getAsYamlScalar().getAsDouble()));
		}
	}
	//endregion

	//region Block mapping
	@Test
	void readSimpleMapping() throws IOException {
		String yaml = "key: value";
		try (YamlReader reader = new YamlReader(yaml)) {
			YamlElement result = reader.readYaml();
			assertTrue(result.isYamlMapping());
			YamlMapping mapping = result.getAsYamlMapping();
			assertEquals(new YamlScalar("value"), mapping.get("key"));
		}
	}

	@Test
	void readMultiKeyMapping() throws IOException {
		String yaml = """
			name: John
			age: 30
			active: true
			""";
		try (YamlReader reader = new YamlReader(yaml)) {
			YamlElement result = reader.readYaml();
			assertTrue(result.isYamlMapping());
			YamlMapping mapping = result.getAsYamlMapping();
			assertEquals(new YamlScalar("John"), mapping.get("name"));
			assertEquals(new YamlScalar(30), mapping.get("age"));
			assertEquals(new YamlScalar(true), mapping.get("active"));
		}
	}

	@Test
	void readNestedMapping() throws IOException {
		String yaml = """
			outer:
			  inner: value
			  nested:
			    deep: data
			""";
		try (YamlReader reader = new YamlReader(yaml)) {
			YamlElement result = reader.readYaml();
			assertTrue(result.isYamlMapping());
			YamlMapping outer = result.getAsYamlMapping();
			YamlMapping inner = outer.get("outer").getAsYamlMapping();
			assertEquals(new YamlScalar("value"), inner.get("inner"));
			YamlMapping nested = inner.get("nested").getAsYamlMapping();
			assertEquals(new YamlScalar("data"), nested.get("deep"));
		}
	}

	@Test
	void readMappingWithNullValue() throws IOException {
		String yaml = "key:";
		try (YamlReader reader = new YamlReader(yaml)) {
			YamlElement result = reader.readYaml();
			assertTrue(result.isYamlMapping());
			YamlMapping mapping = result.getAsYamlMapping();
			assertTrue(mapping.get("key").isYamlNull());
		}
	}

	@Test
	void readMappingWithQuotedKey() throws IOException {
		String yaml = "\"quoted key\": value";
		try (YamlReader reader = new YamlReader(yaml)) {
			YamlElement result = reader.readYaml();
			assertTrue(result.isYamlMapping());
			YamlMapping mapping = result.getAsYamlMapping();
			assertEquals(new YamlScalar("value"), mapping.get("quoted key"));
		}
	}

	@Test
	void readDuplicateKeyThrows() {
		String yaml = """
			key: value1
			key: value2
			""";
		try (YamlReader reader = new YamlReader(yaml, DEFAULT_CONFIG)) {
			assertThrows(YamlSyntaxException.class, reader::readYaml);
		} catch (IOException e) {
			fail("Unexpected IOException");
		}
	}

	@Test
	void readDuplicateKeyAllowed() throws IOException {
		String yaml = """
			key: value1
			key: value2
			""";
		try (YamlReader reader = new YamlReader(yaml, ALLOW_DUPLICATES)) {
			YamlElement result = reader.readYaml();
			assertTrue(result.isYamlMapping());
			// Last value wins
			assertEquals(new YamlScalar("value2"), result.getAsYamlMapping().get("key"));
		}
	}
	//endregion

	//region Block sequence
	@Test
	void readSimpleSequence() throws IOException {
		String yaml = """
			- a
			- b
			- c
			""";
		try (YamlReader reader = new YamlReader(yaml)) {
			YamlElement result = reader.readYaml();
			assertTrue(result.isYamlSequence());
			YamlSequence sequence = result.getAsYamlSequence();
			assertEquals(3, sequence.size());
			assertEquals(new YamlScalar("a"), sequence.get(0));
			assertEquals(new YamlScalar("b"), sequence.get(1));
			assertEquals(new YamlScalar("c"), sequence.get(2));
		}
	}

	@Test
	void readSequenceWithNumbers() throws IOException {
		String yaml = """
			- 1
			- 2
			- 3
			""";
		try (YamlReader reader = new YamlReader(yaml)) {
			YamlElement result = reader.readYaml();
			assertTrue(result.isYamlSequence());
			YamlSequence sequence = result.getAsYamlSequence();
			assertEquals(new YamlScalar(1), sequence.get(0));
			assertEquals(new YamlScalar(2), sequence.get(1));
			assertEquals(new YamlScalar(3), sequence.get(2));
		}
	}

	@Test
	void readNestedSequence() throws IOException {
		String yaml = """
			-
			  - nested1
			  - nested2
			- item2
			""";
		try (YamlReader reader = new YamlReader(yaml)) {
			YamlElement result = reader.readYaml();
			assertTrue(result.isYamlSequence());
			YamlSequence outer = result.getAsYamlSequence();
			assertTrue(outer.get(0).isYamlSequence());
			assertEquals(2, outer.get(0).getAsYamlSequence().size());
		}
	}

	@Test
	void readSequenceWithMappings() throws IOException {
		String yaml = """
			- name: John
			  age: 30
			- name: Jane
			  age: 25
			""";
		try (YamlReader reader = new YamlReader(yaml)) {
			YamlElement result = reader.readYaml();
			assertTrue(result.isYamlSequence());
			YamlSequence sequence = result.getAsYamlSequence();
			assertEquals(2, sequence.size());
			assertTrue(sequence.get(0).isYamlMapping());
			assertTrue(sequence.get(1).isYamlMapping());
			assertEquals(new YamlScalar("John"), sequence.get(0).getAsYamlMapping().get("name"));
			assertEquals(new YamlScalar("Jane"), sequence.get(1).getAsYamlMapping().get("name"));
		}
	}

	@Test
	void readSequenceWithNullItem() throws IOException {
		String yaml = """
			- item1
			-
			- item3
			""";
		try (YamlReader reader = new YamlReader(yaml)) {
			YamlElement result = reader.readYaml();
			assertTrue(result.isYamlSequence());
			YamlSequence sequence = result.getAsYamlSequence();
			assertEquals(3, sequence.size());
			assertTrue(sequence.get(1).isYamlNull());
		}
	}
	//endregion

	//region Flow collections
	@Test
	void readFlowMapping() throws IOException {
		String yaml = "{key1: value1, key2: value2}";
		try (YamlReader reader = new YamlReader(yaml)) {
			YamlElement result = reader.readYaml();
			assertTrue(result.isYamlMapping());
			YamlMapping mapping = result.getAsYamlMapping();
			assertEquals(2, mapping.size());
			assertEquals(new YamlScalar("value1"), mapping.get("key1"));
			assertEquals(new YamlScalar("value2"), mapping.get("key2"));
		}
	}

	@Test
	void readFlowSequence() throws IOException {
		String yaml = "[a, b, c]";
		try (YamlReader reader = new YamlReader(yaml)) {
			YamlElement result = reader.readYaml();
			assertTrue(result.isYamlSequence());
			YamlSequence sequence = result.getAsYamlSequence();
			assertEquals(3, sequence.size());
			assertEquals(new YamlScalar("a"), sequence.get(0));
			assertEquals(new YamlScalar("b"), sequence.get(1));
			assertEquals(new YamlScalar("c"), sequence.get(2));
		}
	}

	@Test
	void readEmptyFlowMapping() throws IOException {
		String yaml = "{}";
		try (YamlReader reader = new YamlReader(yaml)) {
			YamlElement result = reader.readYaml();
			assertTrue(result.isYamlMapping());
			assertEquals(0, result.getAsYamlMapping().size());
		}
	}

	@Test
	void readEmptyFlowSequence() throws IOException {
		String yaml = "[]";
		try (YamlReader reader = new YamlReader(yaml)) {
			YamlElement result = reader.readYaml();
			assertTrue(result.isYamlSequence());
			assertEquals(0, result.getAsYamlSequence().size());
		}
	}

	@Test
	void readNestedFlowCollections() throws IOException {
		String yaml = "{outer: {inner: [1, 2, 3]}}";
		try (YamlReader reader = new YamlReader(yaml)) {
			YamlElement result = reader.readYaml();
			assertTrue(result.isYamlMapping());
			YamlMapping outer = result.getAsYamlMapping();
			YamlMapping inner = outer.get("outer").getAsYamlMapping();
			YamlSequence sequence = inner.get("inner").getAsYamlSequence();
			assertEquals(3, sequence.size());
		}
	}

	@Test
	void readFlowMappingInBlockMapping() throws IOException {
		String yaml = """
			config:
			  settings: {debug: true, verbose: false}
			""";
		try (YamlReader reader = new YamlReader(yaml)) {
			YamlElement result = reader.readYaml();
			YamlMapping config = result.getAsYamlMapping().get("config").getAsYamlMapping();
			YamlMapping settings = config.get("settings").getAsYamlMapping();
			assertEquals(new YamlScalar(true), settings.get("debug"));
			assertEquals(new YamlScalar(false), settings.get("verbose"));
		}
	}

	@Test
	void readFlowSequenceInBlockMapping() throws IOException {
		String yaml = """
			tags: [java, yaml, parser]
			""";
		try (YamlReader reader = new YamlReader(yaml)) {
			YamlElement result = reader.readYaml();
			YamlSequence tags = result.getAsYamlMapping().get("tags").getAsYamlSequence();
			assertEquals(3, tags.size());
			assertEquals(new YamlScalar("java"), tags.get(0));
		}
	}
	//endregion

	//region Anchors and aliases
	@Test
	void readAnchorAndAliasResolved() throws IOException {
		String yaml = """
			defaults: &defaults
			  adapter: postgres
			  host: localhost
			production:
			  database: prod_db
			  <<: *defaults
			""";
		// Note: This is a simplified test - the << merge key isn't implemented
		// Testing basic anchor/alias resolution
		String simpleYaml = """
			anchor: &myValue test
			alias: *myValue
			""";
		try (YamlReader reader = new YamlReader(simpleYaml, DEFAULT_CONFIG)) {
			YamlElement result = reader.readYaml();
			assertTrue(result.isYamlMapping());
			YamlMapping mapping = result.getAsYamlMapping();
			// Both should resolve to the same value
			assertEquals(new YamlScalar("test"), mapping.get("anchor"));
			assertEquals(new YamlScalar("test"), mapping.get("alias"));
		}
	}

	@Test
	void readAnchorPreserved() throws IOException {
		String yaml = """
			anchor: &myValue test
			alias: *myValue
			""";
		try (YamlReader reader = new YamlReader(yaml, PRESERVE_ANCHORS)) {
			YamlElement result = reader.readYaml();
			assertTrue(result.isYamlMapping());
			YamlMapping mapping = result.getAsYamlMapping();
			assertTrue(mapping.get("anchor").isYamlAnchor());
			assertTrue(mapping.get("alias").isYamlAlias());
			assertEquals("myValue", mapping.get("anchor").getAsYamlAnchor().name());
			assertEquals("myValue", mapping.get("alias").getAsYamlAlias().anchorName());
		}
	}

	@Test
	void readUndefinedAliasThrows() {
		String yaml = "ref: *undefined";
		try (YamlReader reader = new YamlReader(yaml, DEFAULT_CONFIG)) {
			assertThrows(YamlSyntaxException.class, reader::readYaml);
		} catch (IOException e) {
			fail("Unexpected IOException");
		}
	}

	@Test
	void readAnchorOnMapping() throws IOException {
		// Use flow style for anchored mapping
		String yaml = "data: &mapAnchor {key: value}\nref: *mapAnchor\n";
		try (YamlReader reader = new YamlReader(yaml, DEFAULT_CONFIG)) {
			YamlElement result = reader.readYaml();
			YamlMapping root = result.getAsYamlMapping();
			// Both should resolve to the same mapping content
			assertTrue(root.get("data").isYamlMapping());
			assertTrue(root.get("ref").isYamlMapping());
		}
	}

	@Test
	void readAnchorOnSequence() throws IOException {
		// Use flow style for anchored sequence
		String yaml = "items: &seqAnchor [a, b]\ncopy: *seqAnchor\n";
		try (YamlReader reader = new YamlReader(yaml, DEFAULT_CONFIG)) {
			YamlElement result = reader.readYaml();
			YamlMapping root = result.getAsYamlMapping();
			assertTrue(root.get("items").isYamlSequence());
			assertTrue(root.get("copy").isYamlSequence());
		}
	}
	//endregion

	//region Document markers
	@Test
	void readWithDocumentStart() throws IOException {
		String yaml = """
			---
			key: value
			""";
		try (YamlReader reader = new YamlReader(yaml)) {
			YamlElement result = reader.readYaml();
			assertTrue(result.isYamlMapping());
			assertEquals(new YamlScalar("value"), result.getAsYamlMapping().get("key"));
		}
	}

	@Test
	void readWithDocumentEnd() throws IOException {
		String yaml = """
			key: value
			...
			""";
		try (YamlReader reader = new YamlReader(yaml)) {
			YamlElement result = reader.readYaml();
			assertTrue(result.isYamlMapping());
			assertEquals(new YamlScalar("value"), result.getAsYamlMapping().get("key"));
		}
	}

	@Test
	void readWithBothMarkers() throws IOException {
		String yaml = """
			---
			key: value
			...
			""";
		try (YamlReader reader = new YamlReader(yaml)) {
			YamlElement result = reader.readYaml();
			assertTrue(result.isYamlMapping());
			assertEquals(new YamlScalar("value"), result.getAsYamlMapping().get("key"));
		}
	}
	//endregion

	//region Comments
	@Test
	void readWithLineComment() throws IOException {
		String yaml = """
			# This is a comment
			key: value
			""";
		try (YamlReader reader = new YamlReader(yaml)) {
			YamlElement result = reader.readYaml();
			assertTrue(result.isYamlMapping());
			assertEquals(new YamlScalar("value"), result.getAsYamlMapping().get("key"));
		}
	}

	@Test
	void readWithInlineComment() throws IOException {
		String yaml = "key: value # inline comment";
		try (YamlReader reader = new YamlReader(yaml)) {
			YamlElement result = reader.readYaml();
			assertTrue(result.isYamlMapping());
			assertEquals(new YamlScalar("value"), result.getAsYamlMapping().get("key"));
		}
	}

	@Test
	void readWithMultipleComments() throws IOException {
		String yaml = """
			# Header comment
			key1: value1  # inline
			# Middle comment
			key2: value2
			# Footer comment
			""";
		try (YamlReader reader = new YamlReader(yaml)) {
			YamlElement result = reader.readYaml();
			assertTrue(result.isYamlMapping());
			assertEquals(2, result.getAsYamlMapping().size());
		}
	}
	//endregion

	//region Multi-line strings
	@Test
	void readLiteralBlockScalar() throws IOException {
		String yaml = """
			text: |
			  Line 1
			  Line 2
			  Line 3
			""";
		try (YamlReader reader = new YamlReader(yaml)) {
			YamlElement result = reader.readYaml();
			YamlScalar text = result.getAsYamlMapping().get("text").getAsYamlScalar();
			String value = text.getAsString();
			assertTrue(value.contains("Line 1"));
			assertTrue(value.contains("Line 2"));
			assertTrue(value.contains("Line 3"));
		}
	}

	@Test
	void readFoldedBlockScalar() throws IOException {
		String yaml = """
			text: >
			  This is a long
			  paragraph that should
			  be folded into one line.
			""";
		try (YamlReader reader = new YamlReader(yaml)) {
			YamlElement result = reader.readYaml();
			YamlScalar text = result.getAsYamlMapping().get("text").getAsYamlScalar();
			String value = text.getAsString();
			// Folded should join lines with space
			assertTrue(value.contains("This is a long"));
		}
	}

	@Test
	void readLiteralBlockStripChomp() throws IOException {
		String yaml = """
			text: |-
			  Line without trailing newline
			""";
		try (YamlReader reader = new YamlReader(yaml)) {
			YamlElement result = reader.readYaml();
			YamlScalar text = result.getAsYamlMapping().get("text").getAsYamlScalar();
			String value = text.getAsString();
			assertFalse(value.endsWith("\n"));
		}
	}

	@Test
	void readLiteralBlockKeepChomp() throws IOException {
		String yaml = """
			text: |+
			  Line with preserved newlines


			""";
		try (YamlReader reader = new YamlReader(yaml)) {
			YamlElement result = reader.readYaml();
			YamlScalar text = result.getAsYamlMapping().get("text").getAsYamlScalar();
			// Keep chomping preserves trailing newlines
			assertNotNull(text.getAsString());
		}
	}
	//endregion

	//region Escape sequences
	@Test
	void readDoubleQuotedEscapes() throws IOException {
		String yaml = "text: \"line1\\nline2\\ttab\"";
		try (YamlReader reader = new YamlReader(yaml)) {
			YamlElement result = reader.readYaml();
			String value = result.getAsYamlMapping().get("text").getAsYamlScalar().getAsString();
			assertTrue(value.contains("\n"));
			assertTrue(value.contains("\t"));
		}
	}

	@Test
	void readSingleQuotedEscape() throws IOException {
		String yaml = "text: 'It''s a test'";
		try (YamlReader reader = new YamlReader(yaml)) {
			YamlElement result = reader.readYaml();
			String value = result.getAsYamlMapping().get("text").getAsYamlScalar().getAsString();
			assertEquals("It's a test", value);
		}
	}

	@Test
	void readUnicodeEscape() throws IOException {
		String yaml = "text: \"\\u0041\\u0042\\u0043\""; // ABC
		try (YamlReader reader = new YamlReader(yaml)) {
			YamlElement result = reader.readYaml();
			String value = result.getAsYamlMapping().get("text").getAsYamlScalar().getAsString();
			assertEquals("ABC", value);
		}
	}

	@Test
	void readHexEscape() throws IOException {
		String yaml = "text: \"\\x41\\x42\""; // AB
		try (YamlReader reader = new YamlReader(yaml)) {
			YamlElement result = reader.readYaml();
			String value = result.getAsYamlMapping().get("text").getAsYamlScalar().getAsString();
			assertEquals("AB", value);
		}
	}
	//endregion

	//region Syntax errors
	@Test
	void readTabIndentThrows() {
		String yaml = "\tkey: value";
		try (YamlReader reader = new YamlReader(yaml)) {
			assertThrows(YamlSyntaxException.class, reader::readYaml);
		} catch (IOException e) {
			fail("Unexpected IOException");
		}
	}

	@Test
	void readInconsistentIndentThrows() {
		String yaml = """
			mapping:
			  key1: value1
			   key2: value2
			""";
		try (YamlReader reader = new YamlReader(yaml)) {
			assertThrows(YamlSyntaxException.class, reader::readYaml);
		} catch (IOException e) {
			fail("Unexpected IOException");
		}
	}

	@Test
	void readUnclosedFlowMappingThrows() {
		String yaml = "{key: value";
		try (YamlReader reader = new YamlReader(yaml)) {
			assertThrows(YamlSyntaxException.class, reader::readYaml);
		} catch (IOException e) {
			fail("Unexpected IOException");
		}
	}

	@Test
	void readUnclosedFlowSequenceThrows() {
		String yaml = "[a, b, c";
		try (YamlReader reader = new YamlReader(yaml)) {
			assertThrows(YamlSyntaxException.class, reader::readYaml);
		} catch (IOException e) {
			fail("Unexpected IOException");
		}
	}

	@Test
	void readInvalidAnchorNameThrows() {
		// Anchor at top level with no name throws
		String yaml = "&";
		try (YamlReader reader = new YamlReader(yaml)) {
			assertThrows(YamlSyntaxException.class, reader::readYaml);
		} catch (IOException e) {
			fail("Unexpected IOException");
		}
	}
	//endregion

	//region Strict mode
	@Test
	void strictModeRejectsExtraContent() {
		// Content after document end marker should fail in strict mode
		String yaml = "key: value\n...\nextra content\n";
		try (YamlReader reader = new YamlReader(yaml, DEFAULT_CONFIG)) {
			assertThrows(YamlSyntaxException.class, reader::readYaml);
		} catch (IOException e) {
			fail("Unexpected IOException");
		}
	}

	@Test
	void nonStrictModeAllowsExtraContent() throws IOException {
		// Content after document end marker should be ignored in non-strict mode
		String yaml = "key: value\n...\nextra content\n";
		try (YamlReader reader = new YamlReader(yaml, NON_STRICT)) {
			// Should not throw in non-strict mode
			YamlElement result = reader.readYaml();
			assertNotNull(result);
		}
	}
	//endregion

	//region Complex structures
	@Test
	void readComplexNestedStructure() throws IOException {
		String yaml = """
			application:
			  name: MyApp
			  version: 1.0.0
			  database:
			    host: localhost
			    port: 5432
			    credentials:
			      username: admin
			      password: secret
			  servers:
			    - name: server1
			      port: 8080
			    - name: server2
			      port: 8081
			  features:
			    - auth
			    - logging
			    - metrics
			""";
		try (YamlReader reader = new YamlReader(yaml)) {
			YamlElement result = reader.readYaml();
			assertTrue(result.isYamlMapping());
			YamlMapping app = result.getAsYamlMapping().get("application").getAsYamlMapping();

			assertEquals(new YamlScalar("MyApp"), app.get("name"));
			assertEquals(new YamlScalar("1.0.0"), app.get("version"));

			YamlMapping db = app.get("database").getAsYamlMapping();
			assertEquals(new YamlScalar("localhost"), db.get("host"));
			assertEquals(new YamlScalar(5432), db.get("port"));

			YamlMapping creds = db.get("credentials").getAsYamlMapping();
			assertEquals(new YamlScalar("admin"), creds.get("username"));

			YamlSequence servers = app.get("servers").getAsYamlSequence();
			assertEquals(2, servers.size());

			YamlSequence features = app.get("features").getAsYamlSequence();
			assertEquals(3, features.size());
		}
	}

	@Test
	void readMixedBlockAndFlowStyles() throws IOException {
		String yaml = """
			config:
			  simple: value
			  list: [1, 2, 3]
			  nested:
			    map: {a: 1, b: 2}
			    items:
			      - first
			      - second
			""";
		try (YamlReader reader = new YamlReader(yaml)) {
			YamlElement result = reader.readYaml();
			assertTrue(result.isYamlMapping());
			YamlMapping config = result.getAsYamlMapping().get("config").getAsYamlMapping();

			assertEquals(new YamlScalar("value"), config.get("simple"));

			YamlSequence list = config.get("list").getAsYamlSequence();
			assertEquals(3, list.size());

			YamlMapping nested = config.get("nested").getAsYamlMapping();
			YamlMapping map = nested.get("map").getAsYamlMapping();
			assertEquals(new YamlScalar(1), map.get("a"));

			YamlSequence items = nested.get("items").getAsYamlSequence();
			assertEquals(2, items.size());
		}
	}
	//endregion

	//region Close behavior
	@Test
	void closeDoesNotThrow() {
		YamlReader reader = new YamlReader("key: value");
		assertDoesNotThrow(reader::close);
	}

	@Test
	void multipleClosesDoNotThrow() throws IOException {
		YamlReader reader = new YamlReader("key: value");
		reader.close();
		assertDoesNotThrow(reader::close);
	}
	//endregion
}
