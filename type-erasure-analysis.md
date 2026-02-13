# Type Erasure Analysis

Analysis of every location in the database function system where generic type `<T>` is discarded but could be preserved.

## Categories

- **A - Input type lost in output**: A method accepts `SqlExpression<T>` but returns `SqlExpression<?>` or `SqlExpression<Object>` instead of propagating `T`.
- **B - Return type too broad**: The return type is `SqlExpression<Number>` when the function always produces a specific type like `Double` or `Integer`.
- **C - Parameters use raw Object**: Method parameters use `Object` instead of a generic `<T>`, losing type safety on the input side.

---

## SqlMath.java (27 methods)

### Category A - Input type lost (11 methods)

These functions accept a numeric expression and produce a result of the **same numeric type**. The input `<T>` should flow through to the output.

| Line | Method | Current | Should Be |
|------|--------|---------|-----------|
| 37 | `abs(SqlExpression<? extends Number>)` | `SqlExpression<Number>` | `<T extends Number> SqlExpression<T>` |
| 48 | `round(SqlExpression<? extends Number>)` | `SqlExpression<Number>` | `<T extends Number> SqlExpression<T>` |
| 59 | `ceil(SqlExpression<? extends Number>)` | `SqlExpression<Number>` | `<T extends Number> SqlExpression<T>` |
| 70 | `floor(SqlExpression<? extends Number>)` | `SqlExpression<Number>` | `<T extends Number> SqlExpression<T>` |
| 82 | `mod(SqlExpression<? extends Number>, Number)` | `SqlExpression<Number>` | `<T extends Number> SqlExpression<T>` |
| 94 | `power(SqlExpression<? extends Number>, Number)` | `SqlExpression<Number>` | `<T extends Number> SqlExpression<T>` |
| 138 | `round(SqlExpression<? extends Number>, int)` | `SqlExpression<Number>` | `<T extends Number> SqlExpression<T>` |
| 150 | `truncate(SqlExpression<? extends Number>, int)` | `SqlExpression<Number>` | `<T extends Number> SqlExpression<T>` |
| 316 | `bitAnd(SqlExpression<? extends Number>, long)` | `SqlExpression<Number>` | `<T extends Number> SqlExpression<T>` |
| 328 | `bitOr(SqlExpression<? extends Number>, long)` | `SqlExpression<Number>` | `<T extends Number> SqlExpression<T>` |
| 340 | `bitXor(SqlExpression<? extends Number>, long)` | `SqlExpression<Number>` | `<T extends Number> SqlExpression<T>` |
| 351 | `bitNot(SqlExpression<? extends Number>)` | `SqlExpression<Number>` | `<T extends Number> SqlExpression<T>` |

**Why it matters**: `abs(integerColumn)` returns `SqlExpression<Number>` instead of `SqlExpression<Integer>`. Downstream code loses the ability to work with the specific numeric type.

### Category B - Return type too broad (15 methods)

These functions always produce a `Double` (or `Integer` for `sign`) regardless of input type, but are declared as returning `Number`.

| Line | Method | Current | Should Be |
|------|--------|---------|-----------|
| 105 | `sqrt(...)` | `SqlExpression<Number>` | `SqlExpression<Double>` |
| 115 | `random()` | `SqlExpression<Number>` | `SqlExpression<Double>` |
| 126 | `sign(...)` | `SqlExpression<Number>` | `SqlExpression<Integer>` |
| 161 | `exp(...)` | `SqlExpression<Number>` | `SqlExpression<Double>` |
| 172 | `ln(...)` | `SqlExpression<Number>` | `SqlExpression<Double>` |
| 183 | `log10(...)` | `SqlExpression<Number>` | `SqlExpression<Double>` |
| 194 | `log2(...)` | `SqlExpression<Number>` | `SqlExpression<Double>` |
| 205 | `sin(...)` | `SqlExpression<Number>` | `SqlExpression<Double>` |
| 216 | `cos(...)` | `SqlExpression<Number>` | `SqlExpression<Double>` |
| 227 | `tan(...)` | `SqlExpression<Number>` | `SqlExpression<Double>` |
| 238 | `asin(...)` | `SqlExpression<Number>` | `SqlExpression<Double>` |
| 249 | `acos(...)` | `SqlExpression<Number>` | `SqlExpression<Double>` |
| 260 | `atan(...)` | `SqlExpression<Number>` | `SqlExpression<Double>` |
| 272 | `atan2(...)` | `SqlExpression<Number>` | `SqlExpression<Double>` |
| 283 | `radians(...)` | `SqlExpression<Number>` | `SqlExpression<Double>` |
| 294 | `degrees(...)` | `SqlExpression<Number>` | `SqlExpression<Double>` |
| 304 | `pi()` | `SqlExpression<Number>` | `SqlExpression<Double>` |

**Why it matters**: `sin()` always returns a floating-point value. Declaring `Number` forces callers to cast when they need `Double`, and provides no useful type information.

---

## SqlAgg.java (2 methods)

### Category B - Return type too broad

| Line | Method | Current | Should Be |
|------|--------|---------|-----------|
| 48 | `sum(SqlColumn<? extends Number>)` | `SqlExpression<Number>` | `SqlExpression<Double>` |
| 59 | `avg(SqlColumn<? extends Number>)` | `SqlExpression<Number>` | `SqlExpression<Double>` |

**Note**: SQL `SUM` and `AVG` can widen the input type (e.g. `SUM(INT)` returns `BIGINT`, `AVG(INT)` returns `DECIMAL`). Returning `Number` is arguably a safe choice, but `Double` would be more precise for `AVG` which always produces a floating-point value. For `SUM`, preserving the input type with `<T extends Number> SqlExpression<T>` is also a valid option.

---

## SqlWindow.java (1 method)

### Category A - Input type lost

| Line | Method | Current | Should Be |
|------|--------|---------|-----------|
| 104 | `over(SqlExpression<?>)` | `SqlExpression<?>` | `<T> SqlExpression<T> over(SqlExpression<T>)` |

**Why it matters**: This is the most critical case. `rowNumber()` returns `SqlExpression<Long>`, but after passing through `over()` it becomes `SqlExpression<?>`. The entire type of the windowed expression is erased at the point where it needs to be preserved the most.

---

## SqlFunction.java (5 methods)

### Category A - Input type lost

| Line | Method | Current | Should Be |
|------|--------|---------|-----------|
| 38 | `coalesce(SqlExpression<?>...)` | `SqlExpression<Object>` | `<T> SqlExpression<T> coalesce(SqlExpression<T>...)` |
| 75 | `greatest(SqlExpression<?>...)` | `SqlExpression<Object>` | `<T extends Comparable<T>> SqlExpression<T> greatest(SqlExpression<T>...)` |
| 86 | `least(SqlExpression<?>...)` | `SqlExpression<Object>` | `<T extends Comparable<T>> SqlExpression<T> least(SqlExpression<T>...)` |

**Note**: The typed overload enforces that all arguments share the same type. This is correct for typical usage (`coalesce(nameColumn, defaultNameColumn)` where both are `SqlExpression<String>`). A wildcard overload could remain for heterogeneous cases.

### Category A+C - Input type lost and parameters use Object

| Line | Method | Current | Should Be |
|------|--------|---------|-----------|
| 99 | `caseWhen(SqlCondition, Object, Object)` | `SqlExpression<Object>` | `<T> SqlExpression<T> caseWhen(SqlCondition, T, T)` |
| 126 | `ifExpression(SqlCondition, Object, Object)` | `SqlExpression<Object>` | `<T> SqlExpression<T> ifExpression(SqlCondition, T, T)` |

**Why it matters**: Both branches of a CASE/IF should produce the same type. Using `Object` means no compile-time check that `thenValue` and `elseValue` are compatible, and the result is completely untyped.

### Category C - Parameters use raw Object

| Line | Method | Current | Should Be |
|------|--------|---------|-----------|
| 139 | `isDistinctFrom(SqlExpression<?>, Object)` | works, but untyped | `<T> SqlCondition isDistinctFrom(SqlExpression<T>, T)` |
| 152 | `isNotDistinctFrom(SqlExpression<?>, Object)` | works, but untyped | `<T> SqlCondition isNotDistinctFrom(SqlExpression<T>, T)` |

**Why it matters**: Nothing prevents comparing a `SqlExpression<String>` with an `Integer` value. The generic version ensures the value type matches the expression type.

---

## SqlArray.java (7 methods)

### Category A - Input type lost

All array-returning operations accept `SqlExpression<?>` and return `SqlExpression<Object>`. The array element type from the input should be preserved.

| Line | Method | Current | Should Be |
|------|--------|---------|-----------|
| 42 | `append(SqlExpression<?>, T)` | `SqlExpression<Object>` | `<T> SqlExpression<List<T>> append(SqlExpression<List<T>>, T)` |
| 55 | `prepend(SqlExpression<?>, T)` | `SqlExpression<Object>` | `<T> SqlExpression<List<T>> prepend(SqlExpression<List<T>>, T)` |
| 68 | `remove(SqlExpression<?>, T)` | `SqlExpression<Object>` | `<T> SqlExpression<List<T>> remove(SqlExpression<List<T>>, T)` |
| 82 | `replace(SqlExpression<?>, T, T)` | `SqlExpression<Object>` | `<T> SqlExpression<List<T>> replace(SqlExpression<List<T>>, T, T)` |
| 94 | `cat(SqlExpression<?>, SqlExpression<?>)` | `SqlExpression<Object>` | `<T> SqlExpression<List<T>> cat(SqlExpression<List<T>>, SqlExpression<List<T>>)` |
| 181 | `distinct(SqlExpression<?>)` | `SqlExpression<Object>` | `<T> SqlExpression<List<T>> distinct(SqlExpression<List<T>>)` |
| 192 | `sort(SqlExpression<?>)` | `SqlExpression<Object>` | `<T> SqlExpression<List<T>> sort(SqlExpression<List<T>>)` |

**Why it matters**: Array operations should preserve the element type. Chaining `append(column, "x")` followed by `sort()` loses all type information at every step, making the result completely untyped.

---

## SqlColumn.java (1 method)

### Category A - Input type lost

| Line | Method | Current | Should Be |
|------|--------|---------|-----------|
| 204 | `ifNull(T defaultValue)` | `SqlExpression<?>` | `SqlExpression<T>` |

**Why it matters**: `column.ifNull(defaultValue)` provides a value of type `T` but returns `SqlExpression<?>`. The result type is known to be `T` (either the column value or the default, both of which are `T`).

---

## Files with No Type Erasure Issues

These files correctly preserve types throughout:

- **SqlString.java** - All methods return `SqlExpression<String>` or `SqlExpression<Integer>` as appropriate
- **SqlDate.java** - Returns specific types like `SqlExpression<LocalDate>`, `SqlExpression<LocalDateTime>`, `SqlExpression<Integer>`, `SqlExpression<Long>`
- **SqlRegex.java** - Returns `SqlCondition` or `SqlExpression<String>`
- **SqlHash.java** - Returns `SqlExpression<String>` or `SqlExpression<Long>`
- **SqlJson.java** - Returns `SqlExpression<String>` or `SqlExpression<Integer>`
- **SqlExpression.java** - Interface is correctly generic with proper `<T>` preservation

---

## Summary

| File | Cat A (type lost) | Cat B (too broad) | Cat C (raw Object) | Total |
|------|:-:|:-:|:-:|:-:|
| SqlMath.java | 11 | 16 | - | 27 |
| SqlAgg.java | - | 2 | - | 2 |
| SqlWindow.java | 1 | - | - | 1 |
| SqlFunction.java | 3 | - | 4 | 7 |
| SqlArray.java | 7 | - | - | 7 |
| SqlColumn.java | 1 | - | - | 1 |
| **Total** | **23** | **18** | **4** | **45** |
