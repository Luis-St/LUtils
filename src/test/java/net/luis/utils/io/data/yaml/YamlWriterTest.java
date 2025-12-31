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

import net.luis.utils.io.data.OutputProvider;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link YamlWriter}.<br>
 *
 * @author Luis-St
 */
class YamlWriterTest {

	private static final YamlConfig DEFAULT_CONFIG = YamlConfig.DEFAULT;
	private static final YamlConfig FLOW_CONFIG = new YamlConfig(true, true, "  ", false, false, YamlConfig.NullStyle.NULL, true, false, StandardCharsets.UTF_8);
	private static final YamlConfig WITH_MARKERS = new YamlConfig(true, true, "  ", true, true, YamlConfig.NullStyle.NULL, true, false, StandardCharsets.UTF_8);

	@Test
	void constructorWithOutputProvider() {
		StringOutputStream output = new StringOutputStream();
		assertDoesNotThrow(() -> new YamlWriter(new OutputProvider(output)));
	}

	@Test
	void constructorWithOutputProviderAndConfig() {
		StringOutputStream output = new StringOutputStream();
		assertDoesNotThrow(() -> new YamlWriter(new OutputProvider(output), DEFAULT_CONFIG));
	}

	@Test
	void constructorWithNullOutput() {
		assertThrows(NullPointerException.class, () -> new YamlWriter(null));
		assertThrows(NullPointerException.class, () -> new YamlWriter(null, DEFAULT_CONFIG));
	}

	@Test
	void constructorWithNullConfig() {
		StringOutputStream output = new StringOutputStream();
		assertThrows(NullPointerException.class, () -> new YamlWriter(new OutputProvider(output), null));
	}

	@Test
	void writeYamlNull() throws IOException {
		StringOutputStream output = new StringOutputStream();
		try (YamlWriter writer = new YamlWriter(new OutputProvider(output), DEFAULT_CONFIG)) {
			writer.writeYaml(YamlNull.INSTANCE);
		}
		assertEquals("null", output.toString().trim());
	}

	@Test
	void writeYamlNullWithTildeStyle() throws IOException {
		YamlConfig tildeConfig = new YamlConfig(true, true, "  ", true, false, YamlConfig.NullStyle.TILDE, true, false, StandardCharsets.UTF_8);
		StringOutputStream output = new StringOutputStream();
		try (YamlWriter writer = new YamlWriter(new OutputProvider(output), tildeConfig)) {
			writer.writeYaml(YamlNull.INSTANCE);
		}
		assertEquals("~", output.toString().trim());
	}

	@Test
	void writeYamlScalarString() throws IOException {
		StringOutputStream output = new StringOutputStream();
		try (YamlWriter writer = new YamlWriter(new OutputProvider(output), DEFAULT_CONFIG)) {
			writer.writeYaml(new YamlScalar("hello world"));
		}
		assertEquals("hello world", output.toString().trim());
	}

	@Test
	void writeYamlScalarNumber() throws IOException {
		StringOutputStream output = new StringOutputStream();
		try (YamlWriter writer = new YamlWriter(new OutputProvider(output), DEFAULT_CONFIG)) {
			writer.writeYaml(new YamlScalar(42));
		}
		assertEquals("42", output.toString().trim());
	}

	@Test
	void writeYamlScalarBoolean() throws IOException {
		StringOutputStream output = new StringOutputStream();
		try (YamlWriter writer = new YamlWriter(new OutputProvider(output), DEFAULT_CONFIG)) {
			writer.writeYaml(new YamlScalar(true));
		}
		assertEquals("true", output.toString().trim());
	}

	@Test
	void writeYamlSequenceFlowStyle() throws IOException {
		YamlSequence sequence = new YamlSequence();
		sequence.add("a");
		sequence.add("b");
		sequence.add("c");

		StringOutputStream output = new StringOutputStream();
		try (YamlWriter writer = new YamlWriter(new OutputProvider(output), FLOW_CONFIG)) {
			writer.writeYaml(sequence);
		}
		assertEquals("[a, b, c]", output.toString().trim());
	}

	@Test
	void writeYamlSequenceBlockStyle() throws IOException {
		YamlSequence sequence = new YamlSequence();
		sequence.add("a");
		sequence.add("b");

		StringOutputStream output = new StringOutputStream();
		try (YamlWriter writer = new YamlWriter(new OutputProvider(output), DEFAULT_CONFIG)) {
			writer.writeYaml(sequence);
		}
		String result = output.toString();
		assertTrue(result.contains("- a"));
		assertTrue(result.contains("- b"));
	}

	@Test
	void writeYamlMappingFlowStyle() throws IOException {
		YamlMapping mapping = new YamlMapping();
		mapping.add("key1", "value1");
		mapping.add("key2", "value2");

		StringOutputStream output = new StringOutputStream();
		try (YamlWriter writer = new YamlWriter(new OutputProvider(output), FLOW_CONFIG)) {
			writer.writeYaml(mapping);
		}
		assertEquals("{key1: value1, key2: value2}", output.toString().trim());
	}

	@Test
	void writeYamlMappingBlockStyle() throws IOException {
		YamlMapping mapping = new YamlMapping();
		mapping.add("key1", "value1");
		mapping.add("key2", "value2");

		StringOutputStream output = new StringOutputStream();
		try (YamlWriter writer = new YamlWriter(new OutputProvider(output), DEFAULT_CONFIG)) {
			writer.writeYaml(mapping);
		}
		String result = output.toString();
		assertTrue(result.contains("key1: value1"));
		assertTrue(result.contains("key2: value2"));
	}

	@Test
	void writeYamlWithDocumentMarkers() throws IOException {
		YamlMapping mapping = new YamlMapping();
		mapping.add("key", "value");

		StringOutputStream output = new StringOutputStream();
		try (YamlWriter writer = new YamlWriter(new OutputProvider(output), WITH_MARKERS)) {
			writer.writeYaml(mapping);
		}
		String result = output.toString();
		assertTrue(result.startsWith("---"));
		assertTrue(result.contains("key: value"));
		assertTrue(result.endsWith("..." + System.lineSeparator()) || result.trim().endsWith("..."));
	}

	@Test
	void writeYamlWithoutDocumentMarkers() throws IOException {
		YamlMapping mapping = new YamlMapping();
		mapping.add("key", "value");

		StringOutputStream output = new StringOutputStream();
		try (YamlWriter writer = new YamlWriter(new OutputProvider(output), DEFAULT_CONFIG)) {
			writer.writeYaml(mapping);
		}
		String result = output.toString();
		assertFalse(result.contains("---"));
		assertFalse(result.contains("..."));
	}

	@Test
	void writeYamlNullElement() {
		StringOutputStream output = new StringOutputStream();
		YamlWriter writer = new YamlWriter(new OutputProvider(output), DEFAULT_CONFIG);
		assertThrows(NullPointerException.class, () -> writer.writeYaml(null));
	}

	@Test
	void writeYamlAnchor() throws IOException {
		YamlAnchor anchor = new YamlAnchor("myAnchor", new YamlScalar("value"));

		StringOutputStream output = new StringOutputStream();
		try (YamlWriter writer = new YamlWriter(new OutputProvider(output), DEFAULT_CONFIG)) {
			writer.writeYaml(anchor);
		}
		assertTrue(output.toString().contains("&myAnchor"));
		assertTrue(output.toString().contains("value"));
	}

	@Test
	void writeYamlAlias() throws IOException {
		YamlAlias alias = new YamlAlias("myAlias");

		StringOutputStream output = new StringOutputStream();
		try (YamlWriter writer = new YamlWriter(new OutputProvider(output), DEFAULT_CONFIG)) {
			writer.writeYaml(alias);
		}
		assertEquals("*myAlias", output.toString().trim());
	}

	@Test
	void writeNestedStructure() throws IOException {
		YamlMapping root = new YamlMapping();
		YamlMapping nested = new YamlMapping();
		nested.add("inner", "value");
		root.add("outer", nested);

		StringOutputStream output = new StringOutputStream();
		try (YamlWriter writer = new YamlWriter(new OutputProvider(output), FLOW_CONFIG)) {
			writer.writeYaml(root);
		}
		assertEquals("{outer: {inner: value}}", output.toString().trim());
	}

	@Test
	void writeWithDifferentCharset() throws IOException {
		YamlConfig utf16Config = new YamlConfig(true, true, "  ", false, false, YamlConfig.NullStyle.NULL, true, false, StandardCharsets.UTF_16);
		YamlMapping mapping = new YamlMapping();
		mapping.add("key", "value");

		ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
		try (YamlWriter writer = new YamlWriter(new OutputProvider(byteOutput), utf16Config)) {
			writer.writeYaml(mapping);
		}

		String result = byteOutput.toString(StandardCharsets.UTF_16);
		assertTrue(result.contains("key"));
		assertTrue(result.contains("value"));
	}

	@Test
	void writeEmptyMapping() throws IOException {
		YamlMapping mapping = new YamlMapping();

		StringOutputStream output = new StringOutputStream();
		try (YamlWriter writer = new YamlWriter(new OutputProvider(output), DEFAULT_CONFIG)) {
			writer.writeYaml(mapping);
		}
		assertEquals("{}", output.toString().trim());
	}

	@Test
	void writeEmptySequence() throws IOException {
		YamlSequence sequence = new YamlSequence();

		StringOutputStream output = new StringOutputStream();
		try (YamlWriter writer = new YamlWriter(new OutputProvider(output), DEFAULT_CONFIG)) {
			writer.writeYaml(sequence);
		}
		assertEquals("[]", output.toString().trim());
	}

	@Test
	void writeSpecialFloatValues() throws IOException {
		StringOutputStream output = new StringOutputStream();
		try (YamlWriter writer = new YamlWriter(new OutputProvider(output), FLOW_CONFIG)) {
			YamlSequence sequence = new YamlSequence();
			sequence.add(new YamlScalar(Double.POSITIVE_INFINITY));
			sequence.add(new YamlScalar(Double.NEGATIVE_INFINITY));
			sequence.add(new YamlScalar(Double.NaN));
			writer.writeYaml(sequence);
		}
		String result = output.toString();
		assertTrue(result.contains(".inf"));
		assertTrue(result.contains("-.inf"));
		assertTrue(result.contains(".nan"));
	}

	@Test
	void closeWriter() throws IOException {
		StringOutputStream output = new StringOutputStream();
		YamlWriter writer = new YamlWriter(new OutputProvider(output), DEFAULT_CONFIG);
		writer.writeYaml(new YamlScalar("test"));
		writer.close();

		assertDoesNotThrow(writer::close);
	}

	@Test
	void writerFlushes() throws IOException {
		FlushTrackingOutputStream output = new FlushTrackingOutputStream();
		try (YamlWriter writer = new YamlWriter(new OutputProvider(output), DEFAULT_CONFIG)) {
			writer.writeYaml(new YamlScalar("test"));
		}
		assertTrue(output.wasFlushed());
	}

	/**
	 * Helper class to capture string output.
	 */
	private static class StringOutputStream extends OutputStream {
		private final StringBuilder builder = new StringBuilder();

		@Override
		public void write(int b) {
			this.builder.append((char) b);
		}

		@Override
		public String toString() {
			return this.builder.toString();
		}
	}

	/**
	 * Helper class to track flush calls.
	 */
	private static class FlushTrackingOutputStream extends OutputStream {
		private boolean flushed;

		@Override
		public void write(int b) {}

		@Override
		public void flush() {
			this.flushed = true;
		}

		public boolean wasFlushed() {
			return this.flushed;
		}
	}
}
