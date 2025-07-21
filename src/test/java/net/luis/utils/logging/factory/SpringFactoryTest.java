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

package net.luis.utils.logging.factory;

import net.luis.utils.logging.LoggerConfiguration;
import net.luis.utils.logging.LoggingUtils;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * Test class for {@link SpringFactory}.<br>
 *
 * @author Luis-St
 */
class SpringFactoryTest {
	
	@Test
	void constructor() {
		assertDoesNotThrow(SpringFactory::new);
		
		SpringFactory factory = new SpringFactory();
		assertNotNull(factory);
		assertTrue(factory instanceof ConfigurationFactory);
	}
	
	@Test
	void classAnnotations() {
		Class<SpringFactory> clazz = SpringFactory.class;
		
		assertTrue(clazz.isAnnotationPresent(Plugin.class));
		Plugin plugin = clazz.getAnnotation(Plugin.class);
		assertEquals("SpringFactory", plugin.name());
		assertEquals(ConfigurationFactory.CATEGORY, plugin.category());
		
		boolean hasOrderAnnotation = java.util.Arrays.stream(clazz.getAnnotations()).anyMatch(annotation -> annotation.annotationType().getSimpleName().contains("Order"));
		assertTrue(hasOrderAnnotation);
	}
	
	@Test
	void getSupportedTypes() {
		SpringFactory factory = new SpringFactory();
		String[] supportedTypes = factory.getSupportedTypes();
		
		assertNotNull(supportedTypes);
		assertEquals(1, supportedTypes.length);
		assertEquals("*", supportedTypes[0]);
	}
	
	@Test
	void getSupportedTypesImmutability() {
		SpringFactory factory = new SpringFactory();
		String[] supportedTypes1 = factory.getSupportedTypes();
		String[] supportedTypes2 = factory.getSupportedTypes();
		
		assertNotSame(supportedTypes1, supportedTypes2);
		assertArrayEquals(supportedTypes1, supportedTypes2);
		
		supportedTypes1[0] = "modified";
		String[] supportedTypes3 = factory.getSupportedTypes();
		assertEquals("*", supportedTypes3[0]);
	}
	
	@Test
	void getConfigurationWithSource() throws Exception {
		SpringFactory factory = new SpringFactory();
		LoggerContext context = new LoggerContext("TestContext");
		
		InputStream inputStream = new ByteArrayInputStream("test content".getBytes());
		ConfigurationSource source = new ConfigurationSource(inputStream, new File("test-source"));
		
		Configuration result = assertDoesNotThrow(() -> factory.getConfiguration(context, source));
		assertNotNull(result);
		assertEquals("RuntimeConfiguration", result.getName());
	}
	
	@Test
	void getConfigurationWithSourceWhenUtilsConfigurationExists() throws Exception {
		SpringFactory factory = new SpringFactory();
		LoggerContext context = new LoggerContext("TestContext");
		
		LoggerConfiguration loggerConfig = new LoggerConfiguration("*");
		LoggingUtils.reconfigure(loggerConfig);
		
		InputStream inputStream = new ByteArrayInputStream("test content".getBytes());
		ConfigurationSource source = new ConfigurationSource(inputStream, new File("test-source"));
		
		Configuration result = assertDoesNotThrow(() -> factory.getConfiguration(context, source));
		assertNotNull(result);
		assertEquals("RuntimeConfiguration", result.getName());
	}
	
	@Test
	void getConfigurationWithNameAndLocation() throws Exception {
		SpringFactory factory = new SpringFactory();
		LoggerContext context = new LoggerContext("TestContext");
		String name = "TestConfiguration";
		URI location = URI.create("file:///test/config.xml");
		
		Configuration result = assertDoesNotThrow(() -> factory.getConfiguration(context, name, location));
		assertNotNull(result);
		assertEquals("RuntimeConfiguration", result.getName());
	}
	
	@Test
	void getConfigurationWithNameAndLocationWhenUtilsConfigurationExists() throws Exception {
		SpringFactory factory = new SpringFactory();
		LoggerContext context = new LoggerContext("TestContext");
		String name = "TestConfiguration";
		URI location = URI.create("file:///test/config.xml");
		
		LoggerConfiguration loggerConfig = new LoggerConfiguration("com.example");
		LoggingUtils.reconfigure(loggerConfig);
		
		Configuration result = assertDoesNotThrow(() -> factory.getConfiguration(context, name, location));
		assertNotNull(result);
		assertEquals("RuntimeConfiguration", result.getName());
	}
	
	@Test
	void getConfigurationConsistency() throws Exception {
		SpringFactory factory = new SpringFactory();
		LoggerContext context = new LoggerContext("TestContext");
		
		InputStream inputStream = new ByteArrayInputStream("test content".getBytes());
		ConfigurationSource source = new ConfigurationSource(inputStream, new File("test-source"));
		Configuration config1 = factory.getConfiguration(context, source);
		
		URI location = URI.create("file:///test/config.xml");
		Configuration config2 = factory.getConfiguration(context, "TestConfig", location);
		
		assertNotNull(config1);
		assertNotNull(config2);
		assertEquals(config1.getName(), config2.getName());
	}
	
	@Test
	void getConfigurationWithNullContext() throws Exception {
		SpringFactory factory = new SpringFactory();
		
		InputStream inputStream = new ByteArrayInputStream("test content".getBytes());
		ConfigurationSource source = new ConfigurationSource(inputStream, new File("test-source"));
		
		assertThrows(NullPointerException.class, () -> factory.getConfiguration(null, source));
	}
	
	@Test
	void getConfigurationWithNullSource() throws Exception {
		SpringFactory factory = new SpringFactory();
		LoggerContext context = new LoggerContext("TestContext");
		
		assertThrows(NullPointerException.class, () -> factory.getConfiguration(context, (ConfigurationSource) null));
	}
	
	@Test
	void getConfigurationWithNullName() throws Exception {
		SpringFactory factory = new SpringFactory();
		LoggerContext context = new LoggerContext("TestContext");
		URI location = URI.create("file:///test/config.xml");
		
		assertThrows(NullPointerException.class, () -> factory.getConfiguration(context, null, location));
	}
	
	@Test
	void getConfigurationWithNullLocation() throws Exception {
		SpringFactory factory = new SpringFactory();
		LoggerContext context = new LoggerContext("TestContext");
		String name = "TestConfiguration";
		
		assertThrows(NullPointerException.class, () -> factory.getConfiguration(context, name, null));
	}
	
	@Test
	void multipleFactoryInstances() throws Exception {
		SpringFactory factory1 = new SpringFactory();
		SpringFactory factory2 = new SpringFactory();
		
		assertNotSame(factory1, factory2);
		
		LoggerContext context = new LoggerContext("TestContext");
		InputStream inputStream1 = new ByteArrayInputStream("test content 1".getBytes());
		InputStream inputStream2 = new ByteArrayInputStream("test content 2".getBytes());
		ConfigurationSource source1 = new ConfigurationSource(inputStream1, new File("test-source-1"));
		ConfigurationSource source2 = new ConfigurationSource(inputStream2, new File("test-source-2"));
		
		Configuration config1 = factory1.getConfiguration(context, source1);
		Configuration config2 = factory2.getConfiguration(context, source2);
		
		assertNotNull(config1);
		assertNotNull(config2);
		assertEquals(config1.getName(), config2.getName());
	}
	
	@Test
	void factoryInheritance() {
		SpringFactory factory = new SpringFactory();
		assertTrue(factory instanceof ConfigurationFactory);
		
		assertDoesNotThrow(factory::getSupportedTypes);
	}
	
	@Test
	void configurationProperties() throws Exception {
		SpringFactory factory = new SpringFactory();
		LoggerContext context = new LoggerContext("TestContext");
		
		InputStream inputStream = new ByteArrayInputStream("test content".getBytes());
		ConfigurationSource source = new ConfigurationSource(inputStream, new File("test-source"));
		
		Configuration result = factory.getConfiguration(context, source);
		
		assertNotNull(result);
		assertNotNull(result.getName());
		assertNotNull(result.getLoggers());
		assertNotNull(result.getAppenders());
	}
}
