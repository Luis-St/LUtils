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
import net.luis.utils.io.data.OutputProvider;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for YAML read/write operations with complex structures.<br>
 *
 * @author Luis-St
 */
class YamlIntegrationTest {

	private static final YamlConfig DEFAULT_CONFIG = YamlConfig.DEFAULT;
	private static final YamlConfig PRESERVE_ANCHORS = YamlConfig.PRESERVE_ANCHORS;

	@Test
	void kubernetesDeploymentRoundTrip() throws IOException {
		YamlMapping deployment = new YamlMapping();
		deployment.add("apiVersion", "apps/v1");
		deployment.add("kind", "Deployment");

		YamlMapping metadata = new YamlMapping();
		metadata.add("name", "my-application");
		metadata.add("namespace", "production");
		YamlMapping labels = new YamlMapping();
		labels.add("app", "my-application");
		labels.add("version", "v1.0.0");
		labels.add("environment", "production");
		metadata.add("labels", labels);
		deployment.add("metadata", metadata);

		YamlMapping spec = new YamlMapping();
		spec.add("replicas", 3);

		YamlMapping selector = new YamlMapping();
		YamlMapping matchLabels = new YamlMapping();
		matchLabels.add("app", "my-application");
		selector.add("matchLabels", matchLabels);
		spec.add("selector", selector);

		YamlMapping template = new YamlMapping();
		YamlMapping templateMetadata = new YamlMapping();
		YamlMapping templateLabels = new YamlMapping();
		templateLabels.add("app", "my-application");
		templateMetadata.add("labels", templateLabels);
		template.add("metadata", templateMetadata);

		YamlMapping templateSpec = new YamlMapping();
		YamlSequence containers = new YamlSequence();

		YamlMapping container1 = new YamlMapping();
		container1.add("name", "main-container");
		container1.add("image", "my-registry.io/my-application:v1.0.0");
		container1.add("imagePullPolicy", "Always");

		YamlSequence ports = new YamlSequence();
		YamlMapping port1 = new YamlMapping();
		port1.add("containerPort", 8080);
		port1.add("name", "http");
		ports.add(port1);
		YamlMapping port2 = new YamlMapping();
		port2.add("containerPort", 8443);
		port2.add("name", "https");
		ports.add(port2);
		container1.add("ports", ports);

		YamlSequence env = new YamlSequence();
		YamlMapping env1 = new YamlMapping();
		env1.add("name", "JAVA_OPTS");
		env1.add("value", "-Xmx512m -Xms256m");
		env.add(env1);
		YamlMapping env2 = new YamlMapping();
		env2.add("name", "SPRING_PROFILES_ACTIVE");
		env2.add("value", "production");
		env.add(env2);
		YamlMapping env3 = new YamlMapping();
		env3.add("name", "DATABASE_URL");
		YamlMapping valueFrom = new YamlMapping();
		YamlMapping secretKeyRef = new YamlMapping();
		secretKeyRef.add("name", "db-secrets");
		secretKeyRef.add("key", "url");
		valueFrom.add("secretKeyRef", secretKeyRef);
		env3.add("valueFrom", valueFrom);
		env.add(env3);
		container1.add("env", env);

		YamlMapping resources = new YamlMapping();
		YamlMapping limits = new YamlMapping();
		limits.add("cpu", "500m");
		limits.add("memory", "512Mi");
		resources.add("limits", limits);
		YamlMapping requests = new YamlMapping();
		requests.add("cpu", "250m");
		requests.add("memory", "256Mi");
		resources.add("requests", requests);
		container1.add("resources", resources);

		YamlSequence volumeMounts = new YamlSequence();
		YamlMapping mount1 = new YamlMapping();
		mount1.add("name", "config-volume");
		mount1.add("mountPath", "/app/config");
		volumeMounts.add(mount1);
		YamlMapping mount2 = new YamlMapping();
		mount2.add("name", "data-volume");
		mount2.add("mountPath", "/app/data");
		volumeMounts.add(mount2);
		container1.add("volumeMounts", volumeMounts);

		containers.add(container1);
		templateSpec.add("containers", containers);

		YamlSequence volumes = new YamlSequence();
		YamlMapping vol1 = new YamlMapping();
		vol1.add("name", "config-volume");
		YamlMapping configMap = new YamlMapping();
		configMap.add("name", "app-config");
		vol1.add("configMap", configMap);
		volumes.add(vol1);
		YamlMapping vol2 = new YamlMapping();
		vol2.add("name", "data-volume");
		YamlMapping pvc = new YamlMapping();
		pvc.add("claimName", "app-data-pvc");
		vol2.add("persistentVolumeClaim", pvc);
		volumes.add(vol2);
		templateSpec.add("volumes", volumes);

		template.add("spec", templateSpec);
		spec.add("template", template);
		deployment.add("spec", spec);

		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try (YamlWriter writer = new YamlWriter(new OutputProvider(output), DEFAULT_CONFIG)) {
			writer.writeYaml(deployment);
		}

		ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
		YamlElement result;
		try (YamlReader reader = new YamlReader(new InputProvider(input), DEFAULT_CONFIG)) {
			result = reader.readYaml();
		}

		assertEquals(deployment, result);

		YamlMapping resultMapping = result.getAsYamlMapping();
		assertEquals("apps/v1", resultMapping.get("apiVersion").getAsYamlScalar().getAsString());
		assertEquals("Deployment", resultMapping.get("kind").getAsYamlScalar().getAsString());
		assertEquals(3, resultMapping.get("spec").getAsYamlMapping().get("replicas").getAsYamlScalar().getAsInteger());
	}

	@Test
	void dockerComposeRoundTrip() throws IOException {
		YamlMapping compose = new YamlMapping();
		compose.add("version", "3.8");

		YamlMapping services = new YamlMapping();

		YamlMapping web = new YamlMapping();
		web.add("image", "nginx:latest");
		web.add("container_name", "web-server");
		YamlSequence webPorts = new YamlSequence();
		webPorts.add("80:80");
		webPorts.add("443:443");
		web.add("ports", webPorts);
		YamlSequence webVolumes = new YamlSequence();
		webVolumes.add("./nginx.conf:/etc/nginx/nginx.conf:ro");
		webVolumes.add("./html:/usr/share/nginx/html:ro");
		webVolumes.add("./certs:/etc/nginx/certs:ro");
		web.add("volumes", webVolumes);
		YamlSequence webDepends = new YamlSequence();
		webDepends.add("app");
		web.add("depends_on", webDepends);
		YamlSequence webNetworks = new YamlSequence();
		webNetworks.add("frontend");
		web.add("networks", webNetworks);
		web.add("restart", "unless-stopped");
		services.add("web", web);

		YamlMapping app = new YamlMapping();
		app.add("build", ".");
		app.add("container_name", "application");
		YamlSequence appPorts = new YamlSequence();
		appPorts.add("8080:8080");
		app.add("ports", appPorts);
		YamlMapping appEnvironment = new YamlMapping();
		appEnvironment.add("SPRING_PROFILES_ACTIVE", "docker");
		appEnvironment.add("DATABASE_HOST", "db");
		appEnvironment.add("DATABASE_PORT", "5432");
		appEnvironment.add("REDIS_HOST", "cache");
		appEnvironment.add("REDIS_PORT", "6379");
		app.add("environment", appEnvironment);
		YamlSequence appVolumes = new YamlSequence();
		appVolumes.add("./logs:/app/logs");
		app.add("volumes", appVolumes);
		YamlSequence appDepends = new YamlSequence();
		appDepends.add("db");
		appDepends.add("cache");
		app.add("depends_on", appDepends);
		YamlSequence appNetworks = new YamlSequence();
		appNetworks.add("frontend");
		appNetworks.add("backend");
		app.add("networks", appNetworks);
		app.add("restart", "unless-stopped");
		services.add("app", app);

		YamlMapping db = new YamlMapping();
		db.add("image", "postgres:15");
		db.add("container_name", "database");
		YamlMapping dbEnvironment = new YamlMapping();
		dbEnvironment.add("POSTGRES_USER", "myapp");
		dbEnvironment.add("POSTGRES_PASSWORD", "secret");
		dbEnvironment.add("POSTGRES_DB", "myapp_db");
		db.add("environment", dbEnvironment);
		YamlSequence dbVolumes = new YamlSequence();
		dbVolumes.add("postgres_data:/var/lib/postgresql/data");
		db.add("volumes", dbVolumes);
		YamlSequence dbNetworks = new YamlSequence();
		dbNetworks.add("backend");
		db.add("networks", dbNetworks);
		db.add("restart", "unless-stopped");
		services.add("db", db);

		YamlMapping cache = new YamlMapping();
		cache.add("image", "redis:7-alpine");
		cache.add("container_name", "redis-cache");
		YamlSequence cachePorts = new YamlSequence();
		cachePorts.add("6379:6379");
		cache.add("ports", cachePorts);
		YamlSequence cacheNetworks = new YamlSequence();
		cacheNetworks.add("backend");
		cache.add("networks", cacheNetworks);
		cache.add("restart", "unless-stopped");
		services.add("cache", cache);

		compose.add("services", services);

		YamlMapping networks = new YamlMapping();
		YamlMapping frontend = new YamlMapping();
		frontend.add("driver", "bridge");
		networks.add("frontend", frontend);
		YamlMapping backend = new YamlMapping();
		backend.add("driver", "bridge");
		networks.add("backend", backend);
		compose.add("networks", networks);

		YamlMapping volumes = new YamlMapping();
		YamlMapping postgresData = new YamlMapping();
		postgresData.add("driver", "local");
		volumes.add("postgres_data", postgresData);
		compose.add("volumes", volumes);

		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try (YamlWriter writer = new YamlWriter(new OutputProvider(output), DEFAULT_CONFIG)) {
			writer.writeYaml(compose);
		}

		ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
		YamlElement result;
		try (YamlReader reader = new YamlReader(new InputProvider(input), DEFAULT_CONFIG)) {
			result = reader.readYaml();
		}

		assertTrue(result.isYamlMapping());
		YamlMapping resultMapping = result.getAsYamlMapping();

		assertEquals("3.8", resultMapping.get("version").getAsYamlScalar().getAsString());
		assertEquals(4, resultMapping.get("services").getAsYamlMapping().size());

		YamlMapping webService = resultMapping.get("services").getAsYamlMapping().get("web").getAsYamlMapping();
		assertEquals("nginx:latest", webService.get("image").getAsYamlScalar().getAsString());
		assertEquals("unless-stopped", webService.get("restart").getAsYamlScalar().getAsString());
		assertEquals(2, webService.get("ports").getAsYamlSequence().size());

		assertEquals(2, resultMapping.get("networks").getAsYamlMapping().size());
	}

	@Test
	void ciPipelineConfigRoundTrip() throws IOException {
		YamlMapping pipeline = new YamlMapping();

		YamlSequence stages = new YamlSequence();
		stages.add("build");
		stages.add("test");
		stages.add("deploy");
		pipeline.add("stages", stages);

		YamlMapping variables = new YamlMapping();
		variables.add("MAVEN_OPTS", "-Dmaven.repo.local=.m2/repository");
		variables.add("DOCKER_REGISTRY", "registry.example.com");
		variables.add("APP_NAME", "my-application");
		pipeline.add("variables", variables);

		YamlMapping cache = new YamlMapping();
		YamlSequence cachePaths = new YamlSequence();
		cachePaths.add(".m2/repository");
		cachePaths.add("node_modules");
		cache.add("paths", cachePaths);
		cache.add("key", "${CI_COMMIT_REF_SLUG}");
		pipeline.add("cache", cache);

		YamlMapping buildJob = new YamlMapping();
		buildJob.add("stage", "build");
		buildJob.add("image", "maven:3.9-eclipse-temurin-17");
		YamlSequence buildScript = new YamlSequence();
		buildScript.add("mvn clean package -DskipTests");
		buildScript.add("docker build -t ${DOCKER_REGISTRY}/${APP_NAME}:${CI_COMMIT_SHA} .");
		buildScript.add("docker push ${DOCKER_REGISTRY}/${APP_NAME}:${CI_COMMIT_SHA}");
		buildJob.add("script", buildScript);
		YamlMapping buildArtifacts = new YamlMapping();
		YamlSequence artifactPaths = new YamlSequence();
		artifactPaths.add("target/*.jar");
		buildArtifacts.add("paths", artifactPaths);
		buildArtifacts.add("expire_in", "1 week");
		buildJob.add("artifacts", buildArtifacts);
		pipeline.add("build", buildJob);

		YamlMapping testJob = new YamlMapping();
		testJob.add("stage", "test");
		testJob.add("image", "maven:3.9-eclipse-temurin-17");
		YamlSequence testScript = new YamlSequence();
		testScript.add("mvn test");
		testJob.add("script", testScript);
		YamlSequence testNeeds = new YamlSequence();
		testNeeds.add("build");
		testJob.add("needs", testNeeds);
		YamlMapping testReports = new YamlMapping();
		YamlSequence junitPaths = new YamlSequence();
		junitPaths.add("target/surefire-reports/TEST-*.xml");
		testReports.add("junit", junitPaths);
		YamlSequence coveragePaths = new YamlSequence();
		coveragePaths.add("target/site/jacoco/jacoco.xml");
		testReports.add("coverage_report", coveragePaths);
		testJob.add("reports", testReports);
		pipeline.add("test", testJob);

		YamlMapping deployStaging = new YamlMapping();
		deployStaging.add("stage", "deploy");
		deployStaging.add("image", "bitnami/kubectl:latest");
		YamlSequence deployStagingScript = new YamlSequence();
		deployStagingScript.add("kubectl config use-context staging");
		deployStagingScript.add("kubectl set image deployment/${APP_NAME} main=${DOCKER_REGISTRY}/${APP_NAME}:${CI_COMMIT_SHA}");
		deployStagingScript.add("kubectl rollout status deployment/${APP_NAME}");
		deployStaging.add("script", deployStagingScript);
		YamlMapping stagingEnvironment = new YamlMapping();
		stagingEnvironment.add("name", "staging");
		stagingEnvironment.add("url", "https://staging.example.com");
		deployStaging.add("environment", stagingEnvironment);
		YamlSequence stagingRules = new YamlSequence();
		YamlMapping rule1 = new YamlMapping();
		rule1.add("if", "$CI_COMMIT_BRANCH == 'develop'");
		stagingRules.add(rule1);
		deployStaging.add("rules", stagingRules);
		pipeline.add("deploy-staging", deployStaging);

		YamlMapping deployProd = new YamlMapping();
		deployProd.add("stage", "deploy");
		deployProd.add("image", "bitnami/kubectl:latest");
		YamlSequence deployProdScript = new YamlSequence();
		deployProdScript.add("kubectl config use-context production");
		deployProdScript.add("kubectl set image deployment/${APP_NAME} main=${DOCKER_REGISTRY}/${APP_NAME}:${CI_COMMIT_SHA}");
		deployProdScript.add("kubectl rollout status deployment/${APP_NAME}");
		deployProd.add("script", deployProdScript);
		YamlMapping prodEnvironment = new YamlMapping();
		prodEnvironment.add("name", "production");
		prodEnvironment.add("url", "https://www.example.com");
		deployProd.add("environment", prodEnvironment);
		deployProd.add("when", "manual");
		YamlSequence prodRules = new YamlSequence();
		YamlMapping prodRule = new YamlMapping();
		prodRule.add("if", "$CI_COMMIT_TAG");
		prodRules.add(prodRule);
		deployProd.add("rules", prodRules);
		pipeline.add("deploy-production", deployProd);

		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try (YamlWriter writer = new YamlWriter(new OutputProvider(output), DEFAULT_CONFIG)) {
			writer.writeYaml(pipeline);
		}

		ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
		YamlElement result;
		try (YamlReader reader = new YamlReader(new InputProvider(input), DEFAULT_CONFIG)) {
			result = reader.readYaml();
		}

		assertEquals(pipeline, result);

		YamlMapping resultMapping = result.getAsYamlMapping();
		assertEquals(3, resultMapping.get("stages").getAsYamlSequence().size());
		assertTrue(resultMapping.containsKey("build"));
		assertTrue(resultMapping.containsKey("test"));
	}

	@Test
	void ansiblePlaybookRoundTrip() throws IOException {
		YamlSequence playbook = new YamlSequence();

		YamlMapping play1 = new YamlMapping();
		play1.add("name", "Configure web servers");
		play1.add("hosts", "webservers");
		play1.add("become", true);

		YamlMapping vars = new YamlMapping();
		vars.add("http_port", 80);
		vars.add("max_clients", 200);
		vars.add("document_root", "/var/www/html");
		YamlSequence packages = new YamlSequence();
		packages.add("nginx");
		packages.add("php-fpm");
		packages.add("certbot");
		vars.add("required_packages", packages);
		play1.add("vars", vars);

		YamlSequence tasks = new YamlSequence();

		YamlMapping task1 = new YamlMapping();
		task1.add("name", "Install required packages");
		YamlMapping apt = new YamlMapping();
		apt.add("name", "{{ item }}");
		apt.add("state", "present");
		apt.add("update_cache", true);
		task1.add("apt", apt);
		task1.add("loop", "{{ required_packages }}");
		tasks.add(task1);

		YamlMapping task2 = new YamlMapping();
		task2.add("name", "Copy nginx configuration");
		YamlMapping template = new YamlMapping();
		template.add("src", "templates/nginx.conf.j2");
		template.add("dest", "/etc/nginx/nginx.conf");
		template.add("mode", "0644");
		task2.add("template", template);
		task2.add("notify", "Restart nginx");
		tasks.add(task2);

		YamlMapping task3 = new YamlMapping();
		task3.add("name", "Ensure nginx is running");
		YamlMapping service = new YamlMapping();
		service.add("name", "nginx");
		service.add("state", "started");
		service.add("enabled", true);
		task3.add("service", service);
		tasks.add(task3);

		YamlMapping task4 = new YamlMapping();
		task4.add("name", "Create document root");
		YamlMapping file = new YamlMapping();
		file.add("path", "{{ document_root }}");
		file.add("state", "directory");
		file.add("owner", "www-data");
		file.add("group", "www-data");
		file.add("mode", "0755");
		task4.add("file", file);
		tasks.add(task4);

		YamlMapping task5 = new YamlMapping();
		task5.add("name", "Configure firewall");
		YamlMapping ufw = new YamlMapping();
		ufw.add("rule", "allow");
		ufw.add("port", "{{ item }}");
		ufw.add("proto", "tcp");
		task5.add("ufw", ufw);
		YamlSequence ports = new YamlSequence();
		ports.add("80");
		ports.add("443");
		task5.add("loop", ports);
		tasks.add(task5);

		play1.add("tasks", tasks);

		YamlSequence handlers = new YamlSequence();
		YamlMapping handler1 = new YamlMapping();
		handler1.add("name", "Restart nginx");
		YamlMapping handlerService = new YamlMapping();
		handlerService.add("name", "nginx");
		handlerService.add("state", "restarted");
		handler1.add("service", handlerService);
		handlers.add(handler1);
		play1.add("handlers", handlers);

		playbook.add(play1);

		YamlMapping play2 = new YamlMapping();
		play2.add("name", "Configure database servers");
		play2.add("hosts", "dbservers");
		play2.add("become", true);

		YamlMapping dbVars = new YamlMapping();
		dbVars.add("db_name", "myapp");
		dbVars.add("db_user", "myapp_user");
		dbVars.add("db_password", "secret123");
		play2.add("vars", dbVars);

		YamlSequence dbTasks = new YamlSequence();
		YamlMapping dbTask1 = new YamlMapping();
		dbTask1.add("name", "Install PostgreSQL");
		YamlMapping dbApt = new YamlMapping();
		dbApt.add("name", "postgresql");
		dbApt.add("state", "present");
		dbTask1.add("apt", dbApt);
		dbTasks.add(dbTask1);
		play2.add("tasks", dbTasks);

		playbook.add(play2);

		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try (YamlWriter writer = new YamlWriter(new OutputProvider(output), DEFAULT_CONFIG)) {
			writer.writeYaml(playbook);
		}

		ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
		YamlElement result;
		try (YamlReader reader = new YamlReader(new InputProvider(input), DEFAULT_CONFIG)) {
			result = reader.readYaml();
		}

		assertTrue(result.isYamlSequence());
		YamlSequence resultSequence = result.getAsYamlSequence();
		assertEquals(2, resultSequence.size());
		assertEquals("Configure web servers", resultSequence.get(0).getAsYamlMapping().get("name").getAsYamlScalar().getAsString());
	}

	@Test
	void applicationYamlConfigRoundTrip() throws IOException {
		YamlMapping config = new YamlMapping();

		YamlMapping spring = new YamlMapping();

		YamlMapping application = new YamlMapping();
		application.add("name", "my-microservice");
		application.add("version", "1.0.0");
		spring.add("application", application);

		YamlMapping profiles = new YamlMapping();
		profiles.add("active", "default");
		YamlSequence profileGroups = new YamlSequence();
		profileGroups.add("local");
		profileGroups.add("dev");
		profileGroups.add("staging");
		profileGroups.add("prod");
		profiles.add("group", profileGroups);
		spring.add("profiles", profiles);

		YamlMapping datasource = new YamlMapping();
		datasource.add("url", "jdbc:postgresql://localhost:5432/myapp");
		datasource.add("username", "myapp");
		datasource.add("password", "${DB_PASSWORD}");
		datasource.add("driver-class-name", "org.postgresql.Driver");
		YamlMapping hikari = new YamlMapping();
		hikari.add("minimum-idle", 5);
		hikari.add("maximum-pool-size", 20);
		hikari.add("idle-timeout", 30000);
		hikari.add("connection-timeout", 30000);
		hikari.add("max-lifetime", 1800000);
		datasource.add("hikari", hikari);
		spring.add("datasource", datasource);

		YamlMapping jpa = new YamlMapping();
		jpa.add("open-in-view", false);
		jpa.add("show-sql", false);
		YamlMapping hibernate = new YamlMapping();
		hibernate.add("ddl-auto", "validate");
		YamlMapping naming = new YamlMapping();
		naming.add("physical-strategy", "org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy");
		hibernate.add("naming", naming);
		jpa.add("hibernate", hibernate);
		YamlMapping properties = new YamlMapping();
		YamlMapping hibernateProps = new YamlMapping();
		hibernateProps.add("dialect", "org.hibernate.dialect.PostgreSQLDialect");
		hibernateProps.add("format_sql", true);
		hibernateProps.add("use_sql_comments", true);
		properties.add("hibernate", hibernateProps);
		jpa.add("properties", properties);
		spring.add("jpa", jpa);

		YamlMapping cache = new YamlMapping();
		cache.add("type", "redis");
		YamlMapping redis = new YamlMapping();
		redis.add("host", "localhost");
		redis.add("port", 6379);
		redis.add("password", "${REDIS_PASSWORD}");
		redis.add("database", 0);
		YamlMapping timeout = new YamlMapping();
		timeout.add("connect", 10000);
		timeout.add("read", 10000);
		redis.add("timeout", timeout);
		cache.add("redis", redis);
		spring.add("cache", cache);

		config.add("spring", spring);

		YamlMapping server = new YamlMapping();
		server.add("port", 8080);
		YamlMapping servlet = new YamlMapping();
		servlet.add("context-path", "/api");
		server.add("servlet", servlet);
		YamlMapping compression = new YamlMapping();
		compression.add("enabled", true);
		compression.add("min-response-size", 1024);
		YamlSequence mimeTypes = new YamlSequence();
		mimeTypes.add("application/json");
		mimeTypes.add("application/xml");
		mimeTypes.add("text/html");
		mimeTypes.add("text/plain");
		compression.add("mime-types", mimeTypes);
		server.add("compression", compression);
		config.add("server", server);

		YamlMapping logging = new YamlMapping();
		YamlMapping level = new YamlMapping();
		level.add("root", "INFO");
		level.add("com.example", "DEBUG");
		level.add("org.hibernate.SQL", "DEBUG");
		level.add("org.springframework.web", "INFO");
		logging.add("level", level);
		YamlMapping pattern = new YamlMapping();
		pattern.add("console", "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n");
		pattern.add("file", "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n");
		logging.add("pattern", pattern);
		YamlMapping fileConfig = new YamlMapping();
		fileConfig.add("name", "/var/log/myapp/application.log");
		fileConfig.add("max-size", "100MB");
		fileConfig.add("max-history", 30);
		logging.add("file", fileConfig);
		config.add("logging", logging);

		YamlMapping management = new YamlMapping();
		YamlMapping endpoints = new YamlMapping();
		YamlMapping web = new YamlMapping();
		YamlMapping exposure = new YamlMapping();
		YamlSequence include = new YamlSequence();
		include.add("health");
		include.add("info");
		include.add("metrics");
		include.add("prometheus");
		exposure.add("include", include);
		web.add("exposure", exposure);
		endpoints.add("web", web);
		management.add("endpoints", endpoints);
		YamlMapping metrics = new YamlMapping();
		YamlMapping export = new YamlMapping();
		YamlMapping prometheus = new YamlMapping();
		prometheus.add("enabled", true);
		export.add("prometheus", prometheus);
		metrics.add("export", export);
		management.add("metrics", metrics);
		config.add("management", management);

		YamlMapping features = new YamlMapping();
		features.add("feature-x-enabled", true);
		features.add("feature-y-enabled", false);
		features.add("rate-limiting-enabled", true);
		features.add("caching-enabled", true);
		config.add("features", features);

		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try (YamlWriter writer = new YamlWriter(new OutputProvider(output), DEFAULT_CONFIG)) {
			writer.writeYaml(config);
		}

		ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
		YamlElement result;
		try (YamlReader reader = new YamlReader(new InputProvider(input), DEFAULT_CONFIG)) {
			result = reader.readYaml();
		}

		assertTrue(result.isYamlMapping());
		YamlMapping resultMapping = result.getAsYamlMapping();

		assertEquals("my-microservice", resultMapping.get("spring").getAsYamlMapping().get("application").getAsYamlMapping().get("name").getAsYamlScalar().getAsString());
		assertEquals(8080, resultMapping.get("server").getAsYamlMapping().get("port").getAsYamlScalar().getAsInteger());

		assertTrue(resultMapping.containsKey("spring"));
		assertTrue(resultMapping.containsKey("server"));
		assertTrue(resultMapping.containsKey("logging"));
		assertTrue(resultMapping.containsKey("management"));
		assertTrue(resultMapping.containsKey("features"));
	}

	@Test
	void anchorAndAliasComplexRoundTrip() throws IOException {
		YamlMapping config = new YamlMapping();

		YamlMapping defaults = new YamlMapping();
		defaults.add("timeout", 30000);
		defaults.add("retries", 3);
		defaults.add("max-connections", 100);
		config.add("defaults", defaults);

		YamlMapping database = new YamlMapping();
		database.add("host", "localhost");
		database.add("port", 5432);
		config.add("database-template", database);

		YamlMapping production = new YamlMapping();
		production.add("name", "production");
		YamlMapping prodConnection = new YamlMapping();
		prodConnection.add("host", "prod-db.example.com");
		prodConnection.add("port", 5432);
		production.add("connection", prodConnection);
		config.add("production", production);

		YamlMapping staging = new YamlMapping();
		staging.add("name", "staging");
		YamlMapping stagingConnection = new YamlMapping();
		stagingConnection.add("host", "staging-db.example.com");
		stagingConnection.add("port", 5432);
		staging.add("connection", stagingConnection);
		config.add("staging", staging);

		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try (YamlWriter writer = new YamlWriter(new OutputProvider(output), DEFAULT_CONFIG)) {
			writer.writeYaml(config);
		}

		ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
		YamlElement result;
		try (YamlReader reader = new YamlReader(new InputProvider(input), DEFAULT_CONFIG)) {
			result = reader.readYaml();
		}

		assertTrue(result.isYamlMapping());
		YamlMapping resultMapping = result.getAsYamlMapping();

		assertEquals(30000, resultMapping.get("defaults").getAsYamlMapping().get("timeout").getAsYamlScalar().getAsInteger());
		assertEquals(3, resultMapping.get("defaults").getAsYamlMapping().get("retries").getAsYamlScalar().getAsInteger());

		assertEquals("localhost", resultMapping.get("database-template").getAsYamlMapping().get("host").getAsYamlScalar().getAsString());
		assertEquals(5432, resultMapping.get("database-template").getAsYamlMapping().get("port").getAsYamlScalar().getAsInteger());

		assertEquals("production", resultMapping.get("production").getAsYamlMapping().get("name").getAsYamlScalar().getAsString());
		assertEquals("prod-db.example.com", resultMapping.get("production").getAsYamlMapping().get("connection").getAsYamlMapping().get("host").getAsYamlScalar().getAsString());

		assertEquals("staging", resultMapping.get("staging").getAsYamlMapping().get("name").getAsYamlScalar().getAsString());
		assertEquals("staging-db.example.com", resultMapping.get("staging").getAsYamlMapping().get("connection").getAsYamlMapping().get("host").getAsYamlScalar().getAsString());
	}
}
