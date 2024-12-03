/*
 * LUtils
 * Copyright (C) 2024 Luis Staudt
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

package net.luis.utils.io.data.xml;

import net.luis.utils.annotation.type.MockObject;
import net.luis.utils.io.data.OutputProvider;
import net.luis.utils.util.Version;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link XmlWriter}.<br>
 *
 * @author Luis-St
 */
class XmlWriterTest {
	
	private static final XmlConfig CUSTOM_CONFIG = new XmlConfig(false, false, "\t", false, false, StandardCharsets.UTF_8);
	private static final String DECLARATION = "<?xml version=\"v1.0\" encoding=\"UTF-8\" standalone=\"false\"?>";
	
	private static @NotNull String multiline(@NotNull String lines) {
		return lines.replaceAll("\\$", System.lineSeparator());
	}
	
	@Test
	void constructor() {
		OutputProvider provider = new OutputProvider(OutputStream.nullOutputStream());
		
		assertThrows(NullPointerException.class, () -> new XmlWriter(null));
		assertDoesNotThrow(() -> new XmlWriter(provider));
		
		assertThrows(NullPointerException.class, () -> new XmlWriter(null, XmlConfig.DEFAULT));
		assertThrows(NullPointerException.class, () -> new XmlWriter(provider, null));
		assertDoesNotThrow(() -> new XmlWriter(provider, XmlConfig.DEFAULT));
	}
	
	@Test
	void writeDeclarationDefaultConfig() {
		StringOutputStream stream = new StringOutputStream();
		XmlWriter writer = new XmlWriter(new OutputProvider(stream));
		assertThrows(NullPointerException.class, () -> writer.writeDeclaration(null));
		writer.writeDeclaration(new XmlDeclaration(Version.of(1, 0)));
		assertThrows(IllegalStateException.class, () -> writer.writeDeclaration(new XmlDeclaration(Version.of(1, 0))));
		assertEquals(DECLARATION + System.lineSeparator(), stream.toString());
	}
	
	@Test
	void writeDeclarationCustomConfig() {
		StringOutputStream stream = new StringOutputStream();
		XmlWriter writer = new XmlWriter(new OutputProvider(stream), CUSTOM_CONFIG);
		assertThrows(NullPointerException.class, () -> writer.writeDeclaration(null));
		writer.writeDeclaration(new XmlDeclaration(Version.of(1, 0)));
		assertDoesNotThrow(() -> writer.writeDeclaration(new XmlDeclaration(Version.of(1, 0))));
		assertEquals(DECLARATION, stream.toString());
	}
	
	@Test
	void writeXmlDefaultConfig() {
		StringOutputStream stream = new StringOutputStream();
		XmlWriter writer = new XmlWriter(new OutputProvider(stream));
		assertThrows(NullPointerException.class, () -> writer.writeXml(null));
		assertThrows(IllegalStateException.class, () -> writer.writeXml(new XmlElement("test")));
		writer.writeDeclaration(new XmlDeclaration(Version.of(1, 0)));
		
		XmlContainer root = new XmlContainer("root");
		writer.writeXml(root);
		assertEquals(DECLARATION + multiline("$<root></root>"), stream.toString());
		stream.reset();
		
		XmlContainer users = new XmlContainer("users");
		root.addContainer(users);
		writer.writeXml(root);
		assertEquals(multiline("<root>$\t<users></users>$</root>"), stream.toString());
		stream.reset();
		
		XmlContainer user1 = new XmlContainer("user1");
		user1.addAttribute("id", "1");
		user1.addAttribute("name", "User1");
		user1.addValue(new XmlValue("Password", "****"));
		users.addContainer(user1);
		writer.writeXml(root);
		assertEquals(multiline("<root>$\t<users>$\t\t<user1 id=\"1\" name=\"User1\">$\t\t\t<Password>****</Password>$\t\t</user1>$\t</users>$</root>"), stream.toString());
		stream.reset();
		
		XmlContainer user2 = new XmlContainer("user2");
		user2.addAttribute("id", "2");
		user2.addAttribute("name", "User2");
		user2.addValue(new XmlValue("Password", "****"));
		users.addContainer(user2);
		writer.writeXml(root);
		assertEquals(multiline("<root>$\t<users>$\t\t<user1 id=\"1\" name=\"User1\">$\t\t\t<Password>****</Password>$\t\t</user1>$\t\t<user2 id=\"2\" name=\"User2\">$\t\t\t<Password>****</Password>$\t\t</user2>$\t</users>$</root>"), stream.toString());
	}
	
	@Test
	void writeXmlCustomConfig() {
		StringOutputStream stream = new StringOutputStream();
		XmlWriter writer = new XmlWriter(new OutputProvider(stream), CUSTOM_CONFIG);
		assertThrows(NullPointerException.class, () -> writer.writeXml(null));
		assertDoesNotThrow(() -> writer.writeXml(new XmlElement("test")));
		assertEquals(DECLARATION + "<test/>", stream.toString());
		stream.reset();
		
		XmlContainer root = new XmlContainer("root");
		writer.writeXml(root);
		assertEquals("<root></root>", stream.toString());
		stream.reset();
		
		XmlContainer users = new XmlContainer("users");
		root.addContainer(users);
		writer.writeXml(root);
		assertEquals("<root><users></users></root>", stream.toString());
		stream.reset();
		
		XmlContainer user1 = new XmlContainer("user1");
		user1.addAttribute("id", "1");
		user1.addAttribute("name", "user1");
		user1.addValue(new XmlValue("password", "****"));
		users.addContainer(user1);
		assertThrows(IllegalStateException.class, () -> writer.writeXml(user1));
		
		user1.getAttributes().clear();
		user1.addValue(new XmlValue("id", "1"));
		user1.addValue(new XmlValue("name", "user1"));
		writer.writeXml(root);
		assertEquals("<root><users><user1><password>****</password><id>1</id><name>user1</name></user1></users></root>", stream.toString());
		stream.reset();
		
		XmlContainer user2 = new XmlContainer("user2");
		user2.addValue(new XmlValue("id", "2"));
		user2.addValue(new XmlValue("name", "user2"));
		user2.addValue(new XmlValue("password", "****"));
		users.addContainer(user2);
		writer.writeXml(root);
		assertEquals("<root><users><user1><password>****</password><id>1</id><name>user1</name></user1><user2><id>2</id><name>user2</name><password>****</password></user2></users></root>", stream.toString());
	}
	
	@Test
	void close() {
		assertDoesNotThrow(() -> new XmlWriter(new OutputProvider(OutputStream.nullOutputStream())));
	}
	
	//region Internal classes
	@MockObject(OutputStream.class)
	private static class StringOutputStream extends OutputStream {
		
		private final StringBuilder builder = new StringBuilder();
		
		@Override
		public void write(int b) {
			this.builder.append((char) b);
		}
		
		public void reset() {
			this.builder.setLength(0);
		}
		
		@Override
		public String toString() {
			return this.builder.toString();
		}
	}
	//endregion
}
