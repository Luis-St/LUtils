# Javadoc Documentation Guide

## 1. General Formatting Rules

### Line Break Rules

**Rule:** All text lines in the documentation end with `<br>` tag, except:
- Lines inside `<ul>`, `<ol>`, `<li>` elements
- Lines inside `<pre>` elements
- The last line before a closing `</p>` tag

```java
// Correct - regular lines end with <br>
/**
 * This is a description line.<br>
 * Another line of description.<br>
 */

// Correct - last line in <p> block has no <br>
/**
 * <p>
 *     This is paragraph content.
 * </p>
 */

// Correct - list items don't use <br>
/**
 * <ul>
 *     <li>First item</li>
 *     <li>Second item</li>
 * </ul>
 */
```

### Empty Line Rules

**Short Javadocs (2 lines or less):** No empty line before tags. The line count includes both description and tags.

```java
// Correct - short javadoc, no empty line
/**
 * Returns the value.<br>
 * @return The value
 */

// Correct - single line description
/**
 * Updates the configuration.<br>
 */
```

**Long Javadocs (more than 2 lines):** Empty line between description and tags.

```java
// Correct - long javadoc with empty line before tags
/**
 * Constructs a new sorted list with the given elements.<br>
 * The elements are wrapped in an internal list.<br>
 * The list is sorted after construction.<br>
 *
 * @param elements The elements to add
 * @param comparator The comparator used to sort
 * @throws NullPointerException If elements is null
 */
```

---

## 2. Block Element Rules

### Paragraph Blocks `<p></p>`

**Usage:** For logical grouping and visual separation in documentation.

**Format:**
- `<p>` on its own comment line
- Content indented inside the block
- Closing `</p>` on its own line
- No `<br>` tag on the last line before `</p>`

```java
// Correct
/**
 * Main description.<br>
 * <p>
 *     Additional explanation here.<br>
 *     This line has no br tag as it's last before closing p.
 * </p>
 */

// Correct - multiple paragraphs
/**
 * Main description.<br>
 * <p>
 *     First paragraph content.<br>
 *     More content here.
 * </p>
 * <p>
 *     Second paragraph content.
 * </p>
 */
```

**Restriction:** No nested block elements inside `<p></p>`:

```java
// WRONG - nested list inside <p>
/**
 * <p>
 *     Description with list:
 *     <ul>
 *         <li>Item</li>
 *     </ul>
 * </p>
 */

// Correct - list outside <p>
/**
 * <p>
 *     Description here.
 * </p>
 * <ul>
 *     <li>Item</li>
 * </ul>
 */

// WRONG - nested <pre> inside <p>
/**
 * <p>
 *     Example:
 *     <pre>{@code code here }</pre>
 * </p>
 */

// Correct - <pre> outside <p>
/**
 * <p>
 *     Example description.
 * </p>
 * <pre>{@code 
 * public class Main {
 * 
 * }
 * }</pre>
 */
```

### List Blocks `<ul>`, `<ol>`, `<li>`

**Format:**
- Opening tag on its own line
- Each `<li>` indented with content and closing tag on same line
- Closing tag on its own line

```java
/**
 * Common radices used:<br>
 * <ul>
 *     <li>{@link #BINARY Binary} (base 2)</li>
 *     <li>{@link #OCTAL Octal} (base 8)</li>
 *     <li>{@link #DECIMAL Decimal} (base 10)</li>
 *     <li>{@link #HEXADECIMAL Hexadecimal} (base 16)</li>
 * </ul>
 */
```

### Code Blocks `<pre>{@code}`

**Format:** Used for code examples and formatted output.

```java
/**
 * Splits the given file into folder and name.<br>
 * <p>
 *     Examples:
 * </p>
 * <pre>{@code
 * split(null) -> ("", "")
 * split("") -> ("", "")
 * split("foo.json") -> ("", "foo.json")
 * split("/bar/foo.json") -> ("/bar/", "foo.json")
 * }</pre>
 *
 * @param file The file to split
 * @return The pair of folder and file name
 */
```

---

## 3. Tag Formatting

### `@param` Tag

**Format:** `@param paramName Description`
- First letter after `@param paramName` is uppercase
- No usage of {@code ...}, links or similar inline tags directly after `@param paramName`

```java
/**
 * @param elements The list of elements to add
 * @param comparator The comparator used to sort the list
 */
```

### `@return` Tag

**Format:** `@return Description of the return value`
- First letter after `@return` is uppercase
- No usage of {@code ...}, links or similar inline tags directly after `@return`

```java
/**
 * @return The name of the file or an empty string if no name
 */
```

### `@throws` Tag

**Format:** `@throws ExceptionType If condition` or `@throws ExceptionType Description`
- First letter after `@throws ExceptionType` is uppercase
- No usage of {@code ...}, links or similar inline tags directly after `@throws ExceptionType`
- Multiple `@throws` tags should be ordered in the order the exceptions can occur in the method
- The pattern "If <condition>" is preferred when applicable

```java
/**
 * @throws NullPointerException If the list is null
 * @throws IllegalArgumentException If the value is negative
 */
```

### `@author` Tag

**Format:** Used in class-level documentation after description.

```java
/**
 * A sorted list implementation.<br>
 *
 * @author Luis-St
 */
```

### `@see` Tag

**Format:** Used for cross-references, placed after description and before `@author`.

```java
/**
 * A simple entry implementation.<br>
 *
 * @see MutableEntry
 *
 * @author Luis-St
 */
```

### `@param <T>` Type Parameter Tag

**Format:** Used for generic type parameters, placed after `@author`.

```java
/**
 * A sorted list.<br>
 *
 * @author Luis-St
 *
 * @param <E> The type of elements
 */
```

---

## 4. Inline Tags

### `{@link}` Tag

**Usage:** Reference other classes, methods, or fields.

```java
/**
 * Internally uses a {@link ArrayList}.<br>
 * See {@link #getComparator()} for the current comparator.<br>
 * Similar to {@link Collections#sort(List)}.<br>
 */
```

### `{@code}` Tag

**Usage:** Inline code literals and values.

```java
/**
 * The comparator is set to {@code null}.<br>
 * Returns {@code true} if the list is empty.<br>
 */
```

---

## 5. Class-Level Documentation

**Structure:**
1. Description with `<br>` tags
2. Empty line
3. `@see` references (optional)
4. Empty line (if @see present)
5. `@author` tag
6. Empty line
7. `@param <T>` type parameters (for generics)

```java
/**
 * A list which is after every modification sorted<br>
 * using the set comparator or if it is {@code null} the natural order.<br>
 *
 * @see Comparator
 *
 * @author Luis-St
 *
 * @param <E> The type of the elements
 */
public class SortedList<E> extends AbstractList<E> {
```

---

## 6. Method-Level Documentation

### Constructor Documentation

```java
/**
 * Constructs a new sorted list with the given elements.<br>
 * The elements are wrapped in an internal {@link ArrayList}.<br>
 *
 * @param elements The elements to add
 * @param comparator The comparator used to sort the list
 * @throws NullPointerException If the list is null
 */
public SortedList(List<E> elements, Comparator<E> comparator) {
```

### Getter Documentation

```java
/**
 * Returns the current comparator.<br>
 * @return The comparator or {@code null} if natural order
 */
public Comparator<E> getComparator() {
```

### Method with Examples

```java
/**
 * Gets the file extension.<br>
 * <p>
 *     Examples:
 * </p>
 * <pre>{@code
 * getExtension("file.txt") -> "txt"
 * getExtension("file") -> ""
 * }</pre>
 *
 * @param file The file path
 * @return The extension or empty string
 */
public String getExtension(String file) {
```

### Override Documentation

**Note**:
Use `{@inheritDoc}` only when there is no additional information to add.
Otherwise, methods annotated with `@Override` should not be documented at all.

```java
/**
 * {@inheritDoc}<br>
 * <br>
 * The list is sorted after the element is added.
 */
@Override
public boolean add(E element) {
```

---

## 7. Field Documentation

**Format:** Keep brief, typically one line.

```java
/**
 * The internal list.
 */
private final List<E> internalList;

/**
 * The comparator used to sort the list.
 */
private Comparator<E> comparator;
```

---

## 8. Tag Order

**Standard order for tags:**
1. `@param` (in parameter order)
2. `@return`
3. `@throws` (in alphabetical order by exception type)
4. `@see`

For class-level:
1. `@see`
2. `@author`
3. `@param <T>` (type parameters)

---

## 9. Complete Examples

### Class with Generic Type

```java
/**
 * A collection which returns a random element based on weight.<br>
 * The higher the weight, the higher the chance of selection.<br>
 *
 * @author Luis-St
 *
 * @param <E> The type of elements
 */
public class WeightCollection<E> {
```

### Method with Multiple Paragraphs

```java
/**
 * Returns the element type of this codec.<br>
 * <p>
 *     By default, this method tries to infer the type using reflection.<br>
 *     If the type cannot be inferred, an exception is thrown.
 * </p>
 * <p>
 *     Override this method if automatic inference fails.
 * </p>
 *
 * @return The inferred element type
 * @throws IllegalStateException If the type cannot be inferred
 */
public Class<C> getElementType() {
```

### Exception Class Documentation

```java
/**
 * Thrown to indicate that a string is invalid.<br>
 * This exception is thrown when a string is not valid for an operation.<br>
 * For example:<br><br>
 * <ul>
 *     <li>If a string is empty and the operation requires a non-empty string.</li>
 *     <li>If a string is not in the correct format.</li>
 * </ul>
 * The exception message should describe the invalid string.<br>
 *
 * @author Luis-St
 */
public class InvalidStringException extends RuntimeException {
```

---

## 10. Key Principles

1. **Line breaks:** All description lines end with `<br>`, except inside lists, pre blocks, or before `</p>`
2. **Empty lines:** Short javadocs (2 lines or less, including description and tags) have no empty line before tags; longer ones do
3. **No nesting:** Never put `<ul>`, `<ol>`, `<li>`, or `<pre>` inside `<p></p>` blocks
4. **Consistent indentation:** Content inside `<p>`, `<ul>`, `<ol>` is indented with 4 spaces
5. **Self-closing:** `<br>` is used, not `<br/>`
6. **Tag order:** Follow the standard order for tags
7. **Concise fields:** Field documentation is typically one line
8. **Examples in pre blocks:** Use `<p>Examples:</p>` followed by `<pre>{@code ... }</pre>`
9. **Cross-references:** Use `{@link}` for classes/methods, `{@code}` for literals
10. **Author tag:** Always include `@author Luis-St` in class-level documentation
