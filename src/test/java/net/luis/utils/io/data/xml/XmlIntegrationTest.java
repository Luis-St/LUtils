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

package net.luis.utils.io.data.xml;

import net.luis.utils.io.data.InputProvider;
import net.luis.utils.io.data.OutputProvider;
import net.luis.utils.util.Version;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for XML read/write operations with complex structures.<br>
 *
 * @author Luis-St
 */
class XmlIntegrationTest {

	@Test
	void mavenPomFileRoundTrip() throws Exception {
		XmlContainer project = new XmlContainer("project");
		project.addAttribute("xmlns", "http://maven.apache.org/POM/4.0.0");
		project.addAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");

		project.addValue(new XmlValue("modelVersion", "4.0.0"));
		project.addValue(new XmlValue("groupId", "com.example"));
		project.addValue(new XmlValue("artifactId", "my-application"));
		project.addValue(new XmlValue("version", "1.0.0-SNAPSHOT"));
		project.addValue(new XmlValue("packaging", "jar"));
		project.addValue(new XmlValue("name", "My Application"));
		project.addValue(new XmlValue("description", "A sample Maven project"));

		XmlContainer properties = new XmlContainer("properties");
		properties.addValue(new XmlValue("java-version", "17"));
		properties.addValue(new XmlValue("project-build-sourceEncoding", "UTF-8"));
		properties.addValue(new XmlValue("maven-compiler-source", "17"));
		properties.addValue(new XmlValue("maven-compiler-target", "17"));
		project.addContainer(properties);

		XmlContainer dependencies = new XmlContainer("dependencies");

		XmlContainer dep1 = new XmlContainer("dependency");
		dep1.addValue(new XmlValue("groupId", "org.junit.jupiter"));
		dep1.addValue(new XmlValue("artifactId", "junit-jupiter"));
		dep1.addValue(new XmlValue("version", "5.10.0"));
		dep1.addValue(new XmlValue("scope", "test"));
		dependencies.addContainer(dep1);

		XmlContainer dep2 = new XmlContainer("dependency");
		dep2.addValue(new XmlValue("groupId", "org.slf4j"));
		dep2.addValue(new XmlValue("artifactId", "slf4j-api"));
		dep2.addValue(new XmlValue("version", "2.0.9"));
		dependencies.addContainer(dep2);

		XmlContainer dep3 = new XmlContainer("dependency");
		dep3.addValue(new XmlValue("groupId", "com.google.guava"));
		dep3.addValue(new XmlValue("artifactId", "guava"));
		dep3.addValue(new XmlValue("version", "32.1.3-jre"));
		XmlValue optional = new XmlValue("optional", "true");
		dep3.addValue(optional);
		dependencies.addContainer(dep3);

		project.addContainer(dependencies);

		XmlContainer build = new XmlContainer("build");
		XmlContainer plugins = new XmlContainer("plugins");

		XmlContainer plugin1 = new XmlContainer("plugin");
		plugin1.addValue(new XmlValue("groupId", "org.apache.maven.plugins"));
		plugin1.addValue(new XmlValue("artifactId", "maven-compiler-plugin"));
		plugin1.addValue(new XmlValue("version", "3.11.0"));
		XmlContainer configuration1 = new XmlContainer("configuration");
		configuration1.addValue(new XmlValue("source", "17"));
		configuration1.addValue(new XmlValue("target", "17"));
		plugin1.addContainer(configuration1);
		plugins.addContainer(plugin1);

		XmlContainer plugin2 = new XmlContainer("plugin");
		plugin2.addValue(new XmlValue("groupId", "org.apache.maven.plugins"));
		plugin2.addValue(new XmlValue("artifactId", "maven-surefire-plugin"));
		plugin2.addValue(new XmlValue("version", "3.1.2"));
		plugins.addContainer(plugin2);

		build.addContainer(plugins);
		project.addContainer(build);

		ByteArrayOutputStream output = new ByteArrayOutputStream();
		XmlDeclaration declaration = new XmlDeclaration(Version.of(1, 0), StandardCharsets.UTF_8);
		try (XmlWriter writer = new XmlWriter(new OutputProvider(output))) {
			writer.writeDeclaration(declaration);
			writer.writeXml(project);
		}

		ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
		XmlElement result;
		try (XmlReader reader = new XmlReader(new InputProvider(input))) {
			reader.readDeclaration();
			result = reader.readXmlElement();
		}

		assertEquals(project, result);

		XmlContainer resultContainer = (XmlContainer) result;
		assertEquals("com.example", resultContainer.getAsValue("groupId").getAsString());
		assertEquals("my-application", resultContainer.getAsValue("artifactId").getAsString());
		assertEquals(3, resultContainer.getAsContainer("dependencies").getElements().getAsArray().size());
	}

	@Test
	void soapEnvelopeRoundTrip() throws Exception {
		XmlContainer envelope = new XmlContainer("soap:Envelope");
		envelope.addAttribute("xmlns:soap", "http://schemas.xmlsoap.org/soap/envelope/");
		envelope.addAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
		envelope.addAttribute("xmlns:api", "http://api.example.com/v1");

		XmlContainer header = new XmlContainer("soap:Header");
		XmlContainer auth = new XmlContainer("api:Authentication");
		auth.addValue(new XmlValue("api:Username", "admin"));
		auth.addValue(new XmlValue("api:Token", "abc123xyz789"));
		auth.addValue(new XmlValue("api:Timestamp", "2024-01-25T18:00:00Z"));
		header.addContainer(auth);
		XmlContainer meta = new XmlContainer("api:Metadata");
		meta.addValue(new XmlValue("api:RequestId", "req-001"));
		meta.addValue(new XmlValue("api:Version", "1.0"));
		header.addContainer(meta);
		envelope.addContainer(header);

		XmlContainer body = new XmlContainer("soap:Body");
		XmlContainer request = new XmlContainer("api:GetUserRequest");
		request.addValue(new XmlValue("api:UserId", "USR-12345"));
		XmlContainer options = new XmlContainer("api:Options");
		options.addValue(new XmlValue("api:IncludeProfile", "true"));
		options.addValue(new XmlValue("api:IncludeActivity", "true"));
		options.addValue(new XmlValue("api:IncludePermissions", "false"));
		request.addContainer(options);
		XmlContainer filters = new XmlContainer("api:Filters");
		XmlContainer dateRange = new XmlContainer("api:DateRange");
		dateRange.addValue(new XmlValue("api:Start", "2024-01-01"));
		dateRange.addValue(new XmlValue("api:End", "2024-01-31"));
		filters.addContainer(dateRange);
		request.addContainer(filters);
		body.addContainer(request);
		envelope.addContainer(body);

		ByteArrayOutputStream output = new ByteArrayOutputStream();
		XmlDeclaration declaration = new XmlDeclaration(Version.of(1, 0), StandardCharsets.UTF_8);
		try (XmlWriter writer = new XmlWriter(new OutputProvider(output))) {
			writer.writeDeclaration(declaration);
			writer.writeXml(envelope);
		}

		ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
		XmlElement result;
		try (XmlReader reader = new XmlReader(new InputProvider(input))) {
			reader.readDeclaration();
			result = reader.readXmlElement();
		}

		assertEquals(envelope, result);

		XmlContainer resultContainer = (XmlContainer) result;
		assertEquals("http://schemas.xmlsoap.org/soap/envelope/", resultContainer.getAttributes().get("xmlns:soap").getAsString());
	}

	@Test
	void htmlDocumentRoundTrip() throws Exception {
		XmlContainer html = new XmlContainer("html");
		html.addAttribute("lang", "en");

		XmlContainer head = new XmlContainer("head");
		XmlElement charset = new XmlElement("charset");
		charset.addAttribute("value", "UTF-8");
		head.add(charset);
		head.addValue(new XmlValue("title", "My Web Page"));
		head.addValue(new XmlValue("description", "A sample HTML document"));
		html.addContainer(head);

		XmlContainer body = new XmlContainer("body");
		body.addAttribute("class", "main-content");

		XmlContainer header = new XmlContainer("header");
		header.addAttribute("id", "main-header");
		XmlContainer nav = new XmlContainer("nav");
		nav.addAttribute("class", "navigation");
		nav.addValue(new XmlValue("item1", "Home"));
		nav.addValue(new XmlValue("item2", "About"));
		nav.addValue(new XmlValue("item3", "Services"));
		nav.addValue(new XmlValue("item4", "Contact"));
		header.addContainer(nav);
		body.addContainer(header);

		XmlContainer main = new XmlContainer("main");
		main.addAttribute("id", "content");

		XmlContainer section1 = new XmlContainer("section1");
		section1.addAttribute("class", "hero");
		section1.addValue(new XmlValue("h1", "Welcome to My Website"));
		section1.addValue(new XmlValue("p", "This is a sample paragraph with some content."));
		main.addContainer(section1);

		XmlContainer section2 = new XmlContainer("section2");
		section2.addAttribute("class", "features");
		section2.addValue(new XmlValue("h2", "Features"));
		XmlContainer featureList = new XmlContainer("feature-grid");
		featureList.addValue(new XmlValue("feature1", "Feature 1 description"));
		featureList.addValue(new XmlValue("feature2", "Feature 2 description"));
		featureList.addValue(new XmlValue("feature3", "Feature 3 description"));
		section2.addContainer(featureList);
		main.addContainer(section2);

		XmlContainer form = new XmlContainer("form");
		form.addAttribute("action", "/submit");
		form.addAttribute("method", "POST");
		form.addValue(new XmlValue("name-label", "Name:"));
		form.addValue(new XmlValue("email-label", "Email:"));
		main.addContainer(form);

		body.addContainer(main);

		XmlContainer footer = new XmlContainer("footer");
		footer.addValue(new XmlValue("copyright", "2024 My Website. All rights reserved."));
		body.addContainer(footer);

		html.addContainer(body);

		ByteArrayOutputStream output = new ByteArrayOutputStream();
		XmlDeclaration declaration = new XmlDeclaration(Version.of(1, 0), StandardCharsets.UTF_8);
		try (XmlWriter writer = new XmlWriter(new OutputProvider(output))) {
			writer.writeDeclaration(declaration);
			writer.writeXml(html);
		}

		ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
		XmlElement result;
		try (XmlReader reader = new XmlReader(new InputProvider(input))) {
			reader.readDeclaration();
			result = reader.readXmlElement();
		}

		assertEquals(html, result);

		XmlContainer resultHtml = (XmlContainer) result;
		assertEquals("en", resultHtml.getAttributes().get("lang").getAsString());
		assertNotNull(resultHtml.getAsContainer("head"));
		assertNotNull(resultHtml.getAsContainer("body"));
	}

	@Test
	void siteMappingRoundTrip() throws Exception {
		XmlContainer urlset = new XmlContainer("urlset");
		urlset.addAttribute("xmlns", "http://www.sitemaps.org/schemas/sitemap/0.9");
		urlset.addAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");

		String[] pages = {"/", "/about", "/services", "/products", "/contact", "/blog", "/faq", "/terms", "/privacy", "/careers"};
		String[] changefreqs = {"daily", "weekly", "monthly", "weekly", "monthly", "daily", "monthly", "yearly", "yearly", "weekly"};
		String[] priorities = {"1.0", "0.8", "0.9", "0.9", "0.7", "0.8", "0.6", "0.3", "0.3", "0.7"};

		for (int i = 0; i < pages.length; i++) {
			XmlContainer url = new XmlContainer("url");
			url.addValue(new XmlValue("loc", "https://www.example.com" + pages[i]));
			url.addValue(new XmlValue("lastmod", "2024-01-" + String.format("%02d", i + 1)));
			url.addValue(new XmlValue("changefreq", changefreqs[i]));
			url.addValue(new XmlValue("priority", priorities[i]));
			urlset.addContainer(url);
		}

		ByteArrayOutputStream output = new ByteArrayOutputStream();
		XmlDeclaration declaration = new XmlDeclaration(Version.of(1, 0), StandardCharsets.UTF_8);
		try (XmlWriter writer = new XmlWriter(new OutputProvider(output))) {
			writer.writeDeclaration(declaration);
			writer.writeXml(urlset);
		}

		ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
		XmlElement result;
		try (XmlReader reader = new XmlReader(new InputProvider(input))) {
			reader.readDeclaration();
			result = reader.readXmlElement();
		}

		assertEquals(urlset, result);

		XmlContainer resultUrlset = (XmlContainer) result;
		assertEquals(10, resultUrlset.getElements().getAsArray().size());
		assertEquals("https://www.example.com/", resultUrlset.getAsContainer(0).getAsValue("loc").getAsString());
	}

	@Test
	void configurationWithNamespacesRoundTrip() throws Exception {
		XmlContainer config = new XmlContainer("cfg:Configuration");
		config.addAttribute("xmlns:cfg", "http://config.example.com/v1");
		config.addAttribute("xmlns:db", "http://database.example.com/v1");
		config.addAttribute("xmlns:cache", "http://cache.example.com/v1");
		config.addAttribute("xmlns:log", "http://logging.example.com/v1");

		XmlContainer database = new XmlContainer("db:Database");
		database.addValue(new XmlValue("db:Host", "localhost"));
		database.addValue(new XmlValue("db:Port", "5432"));
		database.addValue(new XmlValue("db:Name", "myapp"));
		XmlContainer credentials = new XmlContainer("db:Credentials");
		credentials.addValue(new XmlValue("db:Username", "admin"));
		credentials.addValue(new XmlValue("db:Password", "secret"));
		database.addContainer(credentials);
		XmlContainer pool = new XmlContainer("db:Pool");
		pool.addValue(new XmlValue("db:MinConnections", "5"));
		pool.addValue(new XmlValue("db:MaxConnections", "20"));
		pool.addValue(new XmlValue("db:Timeout", "30000"));
		database.addContainer(pool);
		config.addContainer(database);

		XmlContainer cacheConfig = new XmlContainer("cache:Cache");
		cacheConfig.addAttribute("cache:enabled", "true");
		cacheConfig.addValue(new XmlValue("cache:Provider", "redis"));
		cacheConfig.addValue(new XmlValue("cache:Host", "localhost"));
		cacheConfig.addValue(new XmlValue("cache:Port", "6379"));
		XmlContainer ttl = new XmlContainer("cache:TTL");
		ttl.addValue(new XmlValue("cache:Default", "3600"));
		ttl.addValue(new XmlValue("cache:Session", "86400"));
		ttl.addValue(new XmlValue("cache:Static", "604800"));
		cacheConfig.addContainer(ttl);
		config.addContainer(cacheConfig);

		XmlContainer logging = new XmlContainer("log:Logging");
		logging.addAttribute("log:level", "INFO");
		XmlContainer appenders = new XmlContainer("log:Appenders");
		XmlContainer console = new XmlContainer("log:Console");
		console.addAttribute("log:name", "stdout");
		console.addValue(new XmlValue("log:Pattern", "[%d] [%level] %logger - %msg%n"));
		appenders.addContainer(console);
		XmlContainer file = new XmlContainer("log:File");
		file.addAttribute("log:name", "application");
		file.addValue(new XmlValue("log:Path", "/var/log/myapp/app.log"));
		file.addValue(new XmlValue("log:MaxSize", "100MB"));
		file.addValue(new XmlValue("log:MaxHistory", "30"));
		appenders.addContainer(file);
		logging.addContainer(appenders);
		config.addContainer(logging);

		ByteArrayOutputStream output = new ByteArrayOutputStream();
		XmlDeclaration declaration = new XmlDeclaration(Version.of(1, 0), StandardCharsets.UTF_8);
		try (XmlWriter writer = new XmlWriter(new OutputProvider(output))) {
			writer.writeDeclaration(declaration);
			writer.writeXml(config);
		}

		ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
		XmlElement result;
		try (XmlReader reader = new XmlReader(new InputProvider(input))) {
			reader.readDeclaration();
			result = reader.readXmlElement();
		}

		assertEquals(config, result);

		XmlContainer resultConfig = (XmlContainer) result;
		assertEquals("http://config.example.com/v1", resultConfig.getAttributes().get("xmlns:cfg").getAsString());
		assertNotNull(resultConfig.getAsContainer("db:Database"));
		assertNotNull(resultConfig.getAsContainer("cache:Cache"));
		assertNotNull(resultConfig.getAsContainer("log:Logging"));
	}

	@Test
	void deeplyNestedContainersRoundTrip() throws Exception {
		XmlContainer level1 = new XmlContainer("level1");
		level1.addAttribute("depth", "1");
		level1.addValue(new XmlValue("data", "Level 1 data"));

		XmlContainer level2 = new XmlContainer("level2");
		level2.addAttribute("depth", "2");
		level2.addValue(new XmlValue("data", "Level 2 data"));

		XmlContainer level3 = new XmlContainer("level3");
		level3.addAttribute("depth", "3");
		level3.addValue(new XmlValue("data", "Level 3 data"));

		XmlContainer level4 = new XmlContainer("level4");
		level4.addAttribute("depth", "4");
		level4.addValue(new XmlValue("data", "Level 4 data"));

		XmlContainer level5 = new XmlContainer("level5");
		level5.addAttribute("depth", "5");
		level5.addValue(new XmlValue("data", "Level 5 data"));

		XmlContainer level6 = new XmlContainer("level6");
		level6.addAttribute("depth", "6");
		level6.addValue(new XmlValue("value1", "Deep value 1"));
		level6.addValue(new XmlValue("value2", "Deep value 2"));
		level6.addValue(new XmlValue("value3", "Deep value 3"));
		XmlElement marker = new XmlElement("marker");
		marker.addAttribute("reached", "true");
		level6.add(marker);

		level5.addContainer(level6);
		level5.addValue(new XmlValue("extra", "Extra 5"));

		level4.addContainer(level5);
		level4.addValue(new XmlValue("extra", "Extra 4"));

		level3.addContainer(level4);
		level3.addValue(new XmlValue("extra", "Extra 3"));

		level2.addContainer(level3);
		level2.addValue(new XmlValue("extra", "Extra 2"));

		level1.addContainer(level2);
		level1.addValue(new XmlValue("extra", "Extra 1"));

		ByteArrayOutputStream output = new ByteArrayOutputStream();
		XmlDeclaration declaration = new XmlDeclaration(Version.of(1, 0), StandardCharsets.UTF_8);
		try (XmlWriter writer = new XmlWriter(new OutputProvider(output))) {
			writer.writeDeclaration(declaration);
			writer.writeXml(level1);
		}

		ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
		XmlElement result;
		try (XmlReader reader = new XmlReader(new InputProvider(input))) {
			reader.readDeclaration();
			result = reader.readXmlElement();
		}

		assertEquals(level1, result);

		XmlContainer resultL1 = (XmlContainer) result;
		XmlContainer deepest = resultL1
			.getAsContainer("level2")
			.getAsContainer("level3")
			.getAsContainer("level4")
			.getAsContainer("level5")
			.getAsContainer("level6");

		assertEquals("6", deepest.getAttributes().get("depth").getAsString());
		assertEquals("Deep value 1", deepest.getAsValue("value1").getAsString());
		assertEquals("true", deepest.get("marker").getAttributes().get("reached").getAsString());
	}
}
