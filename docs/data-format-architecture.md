# Data Format Architecture Recommendation

## Overview

Extract common parts from the current property implementation and provide separate implementations for Properties, INI, and TOML formats.

## Benefits

- Matches existing architecture pattern (JSON, XML, YAML are separate)
- Each format is spec-compliant and interoperable with external tools
- Clear API - users know exactly what format they're working with
- Easier to document and test individually
- Avoids configuration explosion

## Proposed Structure

```
io/data/
├── DataElement (base interface)
├── DataReader<T extends DataElement>
├── DataWriter<T extends DataElement>
├── DataConfig (base record with common options)
│
├── json/     (existing)
├── xml/      (existing)
├── yaml/     (existing)
├── property/ (current - keep as "extended properties")
├── ini/      (new - simple, section-based)
└── toml/     (new - full TOML spec)
```

## Shared Base Components

### DataElement (Base Interface)
- Common element type hierarchy
- Type checking methods (`isNull()`, `isValue()`, `isArray()`, `isObject()`)
- Type conversion methods (`getAsString()`, `getAsNumber()`, etc.)

### Element Types
| Type | Description |
|------|-------------|
| Null | Represents absence of value |
| Value | Primitive values (string, number, boolean) |
| Array | Ordered collection of elements |
| Object/Map | Key-value mapping |

### DataReader / DataWriter Base
- Stream handling utilities
- Resource management (AutoCloseable)
- Common read/write patterns

### DataConfig Base
Common configuration options:
- Charset encoding
- Indentation settings
- Newline handling
- Pretty printing options

### Shared Utilities
- Type conversion (string to boolean/number/etc.)
- Escape sequence handling
- Input validation

## Format-Specific Features

### Properties (Current Implementation)
- Extended properties format with additional features
- Typed value parsing
- Inline arrays `[a, b, c]`
- Hierarchical keys with separators
- Variable resolution `${prop:key:-default}`
- Key compaction

### INI (New)
- Section-based structure `[section]`
- Simple key=value pairs within sections
- One level of nesting only
- Comments with `;` or `#`
- Standard INI spec compliance

### TOML (New)
- Native type support (strings, integers, floats, booleans, dates/times)
- Tables `[table]` and nested tables `[table.subtable]`
- Array of tables `[[items]]`
- Inline tables `{key = "value"}`
- Multi-line strings `"""..."""`
- Full TOML v1.0 spec compliance

## Implementation Priority

1. **Extract shared base** - Create common interfaces and utilities
2. **Refactor Properties** - Update to use shared base
3. **Implement INI** - Simpler format, good starting point
4. **Implement TOML** - More complex, requires date/time handling

## Migration Path

The current Properties implementation can remain as-is initially. The shared base can be extracted incrementally:

1. Create base interfaces without breaking existing code
2. Have existing implementations extend/implement base types
3. Add new format implementations using the shared base
