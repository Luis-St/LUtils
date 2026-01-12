# Test File Creation Guide

## 1. File Location & Naming

**Location:** Mirror the source package structure under `src/test/java/`
```
Source: src/main/java/net/luis/utils/collection/SortedList.java
Test:   src/test/java/net/luis/utils/collection/SortedListTest.java
```

**Naming Convention:** `<ClassName>Test.java`

---

## 2. File Header Structure

Every test file must include:

```java
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

package net.luis.utils.collection;

import static org.junit.jupiter.api.Assertions.*;

// Other imports...

/**
 * Test class for {@link SortedList}.<br>
 *
 * @author Luis-St
 */
class SortedListTest {
```

---

## 3. Import Organization

```java
// Static assertion imports first
import static org.junit.jupiter.api.Assertions.*;

// JUnit imports
import org.junit.jupiter.api.*;

// Project imports
import net.luis.utils.collection.SortedList;

// Java standard library imports
import java.util.*;
```

---

## 4. Test Method Naming Conventions

**Pattern:** `<action><Subject><WithCondition/When>`

| Category | Pattern | Examples |
|----------|---------|----------|
| Constructor tests | `construct*` | `constructEmptyList()`, `constructWithComparator()` |
| Happy path | `<action><behavior>` | `addElementMaintainsSorting()`, `buildSimpleObject()` |
| Null handling | `*WithNull*` or `*Null*` | `constructWithNullComparator()`, `removeNullElement()` |
| Parameter variants | `*With<Variant>` | `constructWithElements()`, `setComparatorNaturalOrder()` |
| Edge cases | `*Multiple*`, `*Boundary*` | `setComparatorMultipleTimes()`, `deeplyNestedStructure()` |
| Validation | `*Validation*` | `contextValidationInObjectMode()` |
| State management | `*Reuse*`, `*Consistency` | `builderReuseAfterBuild()`, `methodChainingConsistency()` |

---

## 5. Test Class Structure

```java
class ExampleTest {

    private static final Config CUSTOM_CONFIG = new Config(...);

    @BeforeAll
    static void setUp() {
        // One-time setup
    }

    @AfterAll
    static void cleanUp() throws Exception {
        // Cleanup resources
    }

    @Test
    void constructEmptyInstance() { ... }

    @Test
    void constructWithNullParameter() { ... }

    @Test
    void featureHappyPath() { ... }

    @Test
    void featureWithEdgeCase() { ... }

    private record TestObject(@NonNull String name) {}
}
```

---

## 6. Assertion Patterns

**Use static imports:**
```java
import static org.junit.jupiter.api.Assertions.*;
```

**Common assertions by use case:**

```java
// Value equality
assertEquals(expected, actual);

// Boolean conditions
assertTrue(condition);
assertFalse(condition);

// Null checks
assertNull(value);
assertNotNull(value);

// Exception verification (inline for single expressions)
assertThrows(NullPointerException.class, () -> method(null));
assertDoesNotThrow(() -> method(validArg));

// Object identity (for builder patterns)
assertSame(builder, builder.add("key", "value"));

// Type verification
assertInstanceOf(JsonObject.class, result);
```

**Assertion density:** 3-8 assertions per test method is typical.

**Inline rule:** Keep assertions on a single line when the lambda is a single expression.

---

## 7. Test Data Organization

**A. Static Constants** - For reusable configurations:
```java
private static final Config CUSTOM_CONFIG = new Config(...);
```

**B. Inline Data** - For simple test values:
```java
@Test
void buildSimpleObject() {
    List<String> items = List.of("A", "B", "C");
    // use items...
}
```

**C. Local Records/Classes** - For complex test objects:
```java
private record TestObject(@NonNull String name) {}
private record TestObjectWithAge(@NonNull String name, int age) {}
```

**D. Atomic Variables** - For tracking invocations:
```java
AtomicInteger callCount = new AtomicInteger(0);
Supplier<Integer> supplier = FunctionUtils.memorize(() -> {
    callCount.incrementAndGet();
    return 42;
});
```

---

## 8. Common Test Patterns

**Constructor Testing:**
```java
@Test
void constructWithNullComparator() {
    assertThrows(NullPointerException.class, () -> new SortedList<>((Comparator<String>) null));
}

@Test
void constructEmptyList() {
    SortedList<String> list = new SortedList<>();
    assertTrue(list.isEmpty());
}
```

**Round-Trip Testing (encode/decode):**
```java
@Test
void codecRoundTrip() {
    TestRecord original = new TestRecord("Test", 25);
    JsonElement encoded = codec.encode(provider, original);
    TestRecord decoded = codec.decode(provider, encoded);
    assertEquals(original, decoded);
}
```

**Builder Pattern Testing:**
```java
@Test
void methodChainingConsistency() {
    Builder builder = Builder.create();
    Builder returned = builder.add("key", "value");
    assertSame(builder, returned);
}
```

**State Consistency Testing:**
```java
@Test
void builderReuseAfterBuild() {
    Builder builder = Builder.create().add("initial", "value");

    Result first = builder.build();
    assertEquals(1, first.size());

    builder.add("additional", "value");
    Result second = builder.build();
    assertEquals(2, second.size());

    // Verify first result is immutable
    assertEquals(1, first.size());
}
```

---

## 9. File I/O Test Cleanup

Always clean up created files:

```java
@AfterAll
static void cleanUp() throws Exception {
    Files.deleteIfExists(Path.of("test-output.json"));
    FileUtils.deleteRecursively(Path.of("test-directory"));
}
```

---

## 10. Key Principles

1. **No test inheritance** - Each test class is standalone
2. **No mocks** - Test actual behavior, not mocked implementations
3. **Comprehensive null testing** - Every public method with parameters needs null tests
4. **All constructor variants** - Test every constructor overload
5. **Test method length** - Keep tests at 5-25 lines
6. **Test names as documentation** - Names should describe the test scenario
7. **Modern Java features** - Use records for test data objects
8. **Static assertions import** - Always use static import for assertions
9. **Inline assertions** - Keep assertions on a single line when the lambda is a single expression

---

## 11. Complete Example

```java
/*
 * LUtils
 * Copyright (C) 2026 Luis Staudt
 * ... (full license header)
 */

package net.luis.utils.example;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;
import java.util.*;

/**
 * Test class for {@link ExampleClass}.<br>
 *
 * @author Luis-St
 */
class ExampleClassTest {

    @Test
    void constructEmpty() {
        ExampleClass instance = new ExampleClass();
        assertNotNull(instance);
        assertTrue(instance.isEmpty());
    }

    @Test
    void constructWithNullParameter() {
        assertThrows(NullPointerException.class, () -> new ExampleClass(null));
    }

    @Test
    void constructWithValidData() {
        ExampleClass instance = new ExampleClass("data");
        assertFalse(instance.isEmpty());
        assertEquals("data", instance.getData());
    }

    @Test
    void addElementSuccessfully() {
        ExampleClass instance = new ExampleClass();
        instance.add("element");
        assertEquals(1, instance.size());
    }

    @Test
    void addNullElementThrows() {
        ExampleClass instance = new ExampleClass();
        assertThrows(NullPointerException.class, () -> instance.add(null));
    }

    @Test
    void addMultipleElements() {
        ExampleClass instance = new ExampleClass();
        instance.add("A");
        instance.add("B");
        instance.add("C");
        assertEquals(3, instance.size());
    }

    private record TestData(String name, int value) {}
}
```
