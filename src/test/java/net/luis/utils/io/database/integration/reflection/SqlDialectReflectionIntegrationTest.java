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

package net.luis.utils.io.database.integration.reflection;

import net.luis.utils.io.database.Sql;
import net.luis.utils.io.database.SqlDatabase;
import net.luis.utils.io.database.condition.SqlCondition;
import net.luis.utils.io.database.exception.client.dialect.SqlDialectFeatureException;
import net.luis.utils.io.database.exception.client.dialect.SqlDialectUnsupportedRenderingException;
import net.luis.utils.io.database.expression.SqlExpression;
import net.luis.utils.io.database.integration.reflection.SqlOperatorCase.Determinism;
import net.luis.utils.io.database.rendering.SqlRenderable;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.opentest4j.TestAbortedException;
import org.testcontainers.DockerClientFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.util.stream.Stream;

import static net.luis.utils.io.database.integration.reflection.SqlEngineFixture.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

/**
 * Reflection-driven cross-dialect integration test for the database system.<br>
 *
 * @author Luis-St
 */
@SuppressWarnings("UseOfSystemOutOrSystemErr")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SqlDialectReflectionIntegrationTest {
	
	private static final List<SqlOperatorCase> CASES = SqlOperatorRegistry.cases();
	private static final List<Engine> ENGINES = new ArrayList<>();
	private static final List<Defect> DEFECTS = Collections.synchronizedList(new ArrayList<>());
	private static final Set<String> EVALUATED_CASES = Collections.synchronizedSet(new LinkedHashSet<>());
	
	private static @NonNull Stream<SqlOperatorCase> operatorCases() {
		return CASES.stream();
	}
	
	private static void assertExecutesEverywhere(@NonNull SqlOperatorCase operatorCase, @NonNull Map<String, Outcome> byEngine, @NonNull List<Map.Entry<String, Outcome>> participating) {
		boolean anyError = participating.stream().anyMatch(entry -> entry.getValue().kind() == Outcome.Kind.ERROR);
		boolean anyNull = participating.stream().anyMatch(entry -> entry.getValue().kind() == Outcome.Kind.VALUE && entry.getValue().raw() == null);
		if (anyError || anyNull) {
			Defect defect = new Defect(operatorCase, Defect.Type.EXECUTION_ERROR, byEngine, null);
			DEFECTS.add(defect);
			fail(defect.render());
		}
		if (participating.isEmpty()) {
			throw new TestAbortedException("No engine could evaluate " + operatorCase.label());
		}
	}
	
	private static @NonNull Outcome runOnEngine(@NonNull Engine engine, @NonNull SqlOperatorCase operatorCase) {
		SqlRenderable renderable = operatorCase.build(engine.database());
		String sql = safeRender(engine, renderable);
		try {
			Object value = execute(engine.database(), operatorCase, renderable);
			return Outcome.value(value, normalize(operatorCase, value), sql);
		} catch (Throwable t) {
			if (isDeliberateDecline(t)) {
				return Outcome.declined(sql, rootMessage(t));
			}
			return Outcome.error(sql, rootMessage(t));
		}
	}
	
	@SuppressWarnings("unchecked")
	private static @Nullable Object execute(@NonNull SqlDatabase database, @NonNull SqlOperatorCase operatorCase, @NonNull SqlRenderable renderable) throws Exception {
		switch (operatorCase.kind()) {
			case SCALAR -> {
				SqlExpression<Object> expression = (SqlExpression<Object>) renderable;
				return database.from(REFL).select(expression).where(Sql.equalTo(ID, 1)).fetchOne();
			}
			case AGGREGATE -> {
				SqlExpression<Object> expression = (SqlExpression<Object>) renderable;
				return database.from(REFL).select(expression).fetchOne();
			}
			case WINDOW -> {
				SqlExpression<Object> expression = (SqlExpression<Object>) renderable;
				return database.from(REFL).select(expression).orderBy(ID.ascending()).fetchFirst().orElse(null);
			}
			case CONDITION -> {
				SqlCondition condition = (SqlCondition) renderable;
				return database.from(REFL).select().where(condition).count();
			}
			default -> throw new IllegalStateException("Unknown kind " + operatorCase.kind());
		}
	}
	
	private static @NonNull String safeRender(@NonNull Engine engine, @NonNull SqlRenderable renderable) {
		try {
			return renderable.toSql(engine.database().getDialect()).sql();
		} catch (Throwable t) {
			return "<render failed: " + rootMessage(t) + ">";
		}
	}
	
	private static @NonNull String normalize(@NonNull SqlOperatorCase operatorCase, @Nullable Object value) {
		return switch (value) {
			case null -> "<null>";
			case byte[] bytes -> "0x" + HexFormat.of().formatHex(bytes);
			case Boolean bool -> bool.toString();
			case Number number -> {
				if (operatorCase.determinism() == Determinism.FLOAT) {
					yield String.format(Locale.ROOT, "%.6f", number.doubleValue());
				}
				yield canonicalDecimal(new BigDecimal(number.toString()));
			}
			case Timestamp timestamp -> timestamp.toLocalDateTime().toString();
			case Date date -> date.toLocalDate().toString();
			case Time time -> time.toLocalTime().toString();
			default -> value.toString();
		};
	}
	
	private static @NonNull String canonicalDecimal(@NonNull BigDecimal value) {
		BigDecimal stripped = value.stripTrailingZeros();
		if (stripped.scale() <= 0) {
			return stripped.toBigInteger().toString();
		}
		return stripped.toPlainString();
	}
	
	private static boolean isDeliberateDecline(@NonNull Throwable t) {
		for (Throwable current = t; current != null; current = current.getCause()) {
			if (current instanceof SqlDialectFeatureException || current instanceof SqlDialectUnsupportedRenderingException) {
				return true;
			}
			if (current.getCause() == current) {
				break;
			}
		}
		return false;
	}
	
	private static @NonNull String rootMessage(@NonNull Throwable t) {
		Throwable current = t;
		while (current.getCause() != null && current.getCause() != current) {
			current = current.getCause();
		}
		String message = current.getMessage();
		return current.getClass().getSimpleName() + (message != null ? ": " + message.lines().findFirst().orElse(message) : "");
	}
	
	@BeforeAll
	void startEngines() throws Exception {
		assumeTrue(DockerClientFactory.instance().isDockerAvailable(), "Docker is required for the reflection dialect integration test");
		ENGINES.addAll(SqlEngineFixture.startEngines());
		for (Engine engine : ENGINES) {
			resetAndSeed(engine.database());
		}
	}
	
	@AfterAll
	void stopEnginesAndReport() {
		this.writeInventory();
		this.printSummary();
		SqlEngineFixture.stopEngines(ENGINES);
		ENGINES.clear();
	}
	
	@Test
	@Order(1)
	void coverageGuardEveryLeafIsRepresented() {
		Collection<Class<?>> leaves = SqlOperatorDiscovery.discoverLeaves();
		Set<Class<?>> covered = new HashSet<>();
		for (SqlOperatorCase operatorCase : CASES) {
			covered.add(operatorCase.operatorClass());
		}
		
		List<String> uncovered = leaves.stream()
			.filter(leaf -> !covered.contains(leaf))
			.map(Class::getSimpleName)
			.sorted()
			.toList();
		
		assertTrue(uncovered.isEmpty(), "Discovered operator leaves without a registered case (wire them into SqlOperatorRegistry): " + uncovered);
		assertTrue(leaves.size() >= 100, "Expected at least 100 discovered operator leaves but found " + leaves.size() + " - did discovery break?");
	}
	
	@Test
	@Order(2)
	void everyCaseBuildsAndRendersOnAtLeastOneEngine() {
		assumeFalse(ENGINES.isEmpty(), "engines not started");
		Engine engine = ENGINES.getFirst();
		
		List<String> failures = new ArrayList<>();
		for (SqlOperatorCase operatorCase : CASES) {
			try {
				SqlRenderable renderable = operatorCase.build(engine.database());
				renderable.toSql(engine.database().getDialect());
			} catch (Exception e) {
				if (!isDeliberateDecline(e)) {
					failures.add(operatorCase.label() + " -> " + e.getClass().getSimpleName() + ": " + e.getMessage());
				}
			}
		}
		assertTrue(failures.isEmpty(), "Cases that failed to build/render on " + engine.name() + ":\n" + String.join("\n", failures));
	}
	
	@Order(3)
	@ParameterizedTest(name = "{0}")
	@MethodSource("operatorCases")
	void allDialectsAgree(@NonNull SqlOperatorCase operatorCase) {
		assumeFalse(ENGINES.isEmpty(), "engines not started");
		EVALUATED_CASES.add(operatorCase.label());
		
		Map<String, Outcome> byEngine = new LinkedHashMap<>();
		for (Engine engine : ENGINES) {
			if (operatorCase.requiredFeature() != null && !engine.supports(operatorCase.requiredFeature())) {
				continue;
			}
			byEngine.put(engine.name(), runOnEngine(engine, operatorCase));
		}
		
		List<Map.Entry<String, Outcome>> participating = byEngine.entrySet().stream()
			.filter(entry -> entry.getValue().kind() != Outcome.Kind.DECLINED)
			.toList();
		
		if (operatorCase.determinism() == Determinism.NON_DETERMINISTIC) {
			assertExecutesEverywhere(operatorCase, byEngine, participating);
			return;
		}
		
		boolean anyError = participating.stream().anyMatch(entry -> entry.getValue().kind() == Outcome.Kind.ERROR);
		Set<String> distinctValues = new LinkedHashSet<>();
		participating.stream()
			.filter(entry -> entry.getValue().kind() == Outcome.Kind.VALUE)
			.forEach(entry -> distinctValues.add(entry.getValue().normalized()));
		
		boolean valueMismatch = distinctValues.size() > 1;
		boolean oracleMismatch = false;
		if (operatorCase.hasOracle() && distinctValues.size() == 1) {
			String consensus = distinctValues.iterator().next();
			String expected = normalize(operatorCase, operatorCase.oracle());
			oracleMismatch = !consensus.equals(expected);
		}
		
		if (anyError || valueMismatch || oracleMismatch) {
			Defect.Type type = anyError ? Defect.Type.EXECUTION_ERROR : (valueMismatch ? Defect.Type.VALUE_MISMATCH : Defect.Type.ORACLE_MISMATCH);
			Defect defect = new Defect(operatorCase, type, byEngine, operatorCase.hasOracle() ? normalize(operatorCase, operatorCase.oracle()) : null);
			DEFECTS.add(defect);
			fail(defect.render());
		}
		
		if (participating.size() < 2 && !operatorCase.hasOracle()) {
			throw new TestAbortedException("Only " + participating.size() + " engine(s) could evaluate " + operatorCase.label() + " - cannot establish consensus");
		}
	}
	
	private void printSummary() {
		System.out.println("\n================ Reflection dialect consensus summary ================");
		System.out.println("Operator cases evaluated: " + EVALUATED_CASES.size() + " / " + CASES.size());
		System.out.println("Defects found:            " + DEFECTS.size());
		
		Map<Defect.Type, Long> byType = new EnumMap<>(Defect.Type.class);
		for (Defect defect : DEFECTS) {
			byType.merge(defect.type(), 1L, Long::sum);
		}
		
		byType.forEach((type, count) -> System.out.println("  " + type + ": " + count));
		System.out.println("=====================================================================\n");
	}
	
	private void writeInventory() {
		StringBuilder builder = new StringBuilder();
		builder.append("# SQL Dialect Defect Inventory\n\n");
		builder.append("Generated by `SqlDialectReflectionIntegrationTest` (reflection-driven cross-dialect consensus).\n");
		builder.append("Engines: ").append(ENGINES.stream().map(Engine::name).toList()).append("\n");
		builder.append("Operator cases evaluated: ").append(EVALUATED_CASES.size()).append(" / ").append(CASES.size()).append("\n");
		builder.append("Defects found: ").append(DEFECTS.size()).append("\n\n");
		builder.append("> Root-cause lines marked `(pending investigation)` are to be filled in manually. No source under `main/.../database` has been modified.\n\n");
		
		Map<Defect.Type, Long> byType = new EnumMap<>(Defect.Type.class);
		for (Defect defect : DEFECTS) {
			byType.merge(defect.type(), 1L, Long::sum);
		}
		
		builder.append("## Summary\n\n");
		if (DEFECTS.isEmpty()) {
			builder.append("No defects detected.\n\n");
		} else {
			byType.forEach((type, count) -> builder.append("- ").append(type).append(": ").append(count).append('\n'));
			builder.append('\n');
		}
		
		builder.append("## Defects\n\n");
		List<Defect> sorted = new ArrayList<>(DEFECTS);
		sorted.sort(Comparator.comparing((Defect d) -> d.type().name()).thenComparing(d -> d.operatorCase().label()));
		for (Defect defect : sorted) {
			builder.append(defect.renderMarkdown()).append('\n');
		}
		
		try {
			Files.writeString(Path.of("sql-dialect-defect-inventory.md"), builder.toString(), StandardCharsets.UTF_8);
		} catch (IOException e) {
			System.err.println("Failed to write defect inventory: " + e.getMessage());
		}
	}
	
	private record Outcome(@NonNull Kind kind, @Nullable Object raw, @Nullable String normalized, @NonNull String sql, @Nullable String detail) {
		
		private static @NonNull Outcome value(@Nullable Object raw, @NonNull String normalized, @NonNull String sql) {
			return new Outcome(Kind.VALUE, raw, normalized, sql, null);
		}
		
		private static @NonNull Outcome error(@NonNull String sql, @NonNull String detail) {
			return new Outcome(Kind.ERROR, null, null, sql, detail);
		}
		
		private static @NonNull Outcome declined(@NonNull String sql, @NonNull String detail) {
			return new Outcome(Kind.DECLINED, null, null, sql, detail);
		}
		
		private @NonNull String describe() {
			return switch (this.kind) {
				case VALUE -> "= " + (this.raw instanceof byte[] b ? "0x" + HexFormat.of().formatHex(b) : String.valueOf(this.raw)) + "  (normalized " + this.normalized + ")";
				case ERROR -> "ERROR " + this.detail;
				case DECLINED -> "declined (" + this.detail + ")";
			};
		}
		
		private enum Kind {VALUE, ERROR, DECLINED}
	}
	
	private record Defect(@NonNull SqlOperatorCase operatorCase, @NonNull Type type, @NonNull Map<String, Outcome> byEngine, @Nullable String oracle) {
		
		private @NonNull String render() {
			StringBuilder builder = new StringBuilder();
			builder.append(this.type).append(" - ").append(this.operatorCase.label()).append('\n');
			
			if (this.oracle != null) {
				builder.append("  Java oracle expected: ").append(this.oracle).append('\n');
			}
			
			this.byEngine.forEach((engine, outcome) -> builder.append("  ").append(String.format(Locale.ROOT, "%-11s", engine)).append(' ').append(outcome.describe()).append("\n      SQL: ").append(outcome.sql()).append('\n'));
			return builder.toString();
		}
		
		private @NonNull String renderMarkdown() {
			StringBuilder builder = new StringBuilder();
			String symptom = switch (this.type) {
				case VALUE_MISMATCH -> "dialects disagree on the result";
				case EXECUTION_ERROR -> "an engine rejected the SQL";
				case ORACLE_MISMATCH -> "all dialects agree but disagree with the Java oracle";
			};
			
			builder.append("### ").append(this.operatorCase.label()).append(" - ").append(symptom).append('\n');
			builder.append("- Kind:        ").append(this.type == Type.EXECUTION_ERROR ? "execution-error" : "value-mismatch").append('\n');
			builder.append("- Operator:    `").append(this.operatorCase.operatorClass().getName()).append("`\n");
			
			if (this.oracle != null) {
				builder.append("- Java oracle: ").append(this.oracle).append('\n');
			}
			
			builder.append("- Results:\n");
			this.byEngine.forEach((engine, outcome) -> builder.append("    - ").append(engine).append(": ").append(outcome.describe()).append('\n'));
			builder.append("- Rendered SQL:\n");
			this.byEngine.forEach((engine, outcome) -> builder.append("    - ").append(engine).append(": `").append(outcome.sql()).append("`\n"));
			builder.append("- Root cause:  (pending investigation)\n");
			builder.append("- Status:      NOT FIXED (documented only)\n");
			return builder.toString();
		}
		
		private enum Type {
			VALUE_MISMATCH,
			EXECUTION_ERROR,
			ORACLE_MISMATCH
		}
	}
}
