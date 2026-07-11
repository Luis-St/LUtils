# Log Formatting — Support Matrix

Two axes, kept separate on purpose:

- **Fields** — *what data* you place into the line. Each maps to a pattern token.
- **Modifiers** — *how* a placed field is rendered (pad, truncate, colour). These are pattern *syntax* applied to a field, not tokens of their own.

Token aliases below follow Logback / Log4j2 convention as reference — they're candidates to adopt in the pattern-string parser (the serialization sugar over the builder), not prescriptions for the core.

## Fields

| Field | Pattern tokens | State |
|---|---|---|
| Message (already interpolated) | `%m`, `%msg`, `%message` | **Must** |
| Level | `%p`, `%le`, `%level` | **Must** |
| Timestamp | `%d`, `%date` | **Must** |
| Logger name | `%c`, `%lo`, `%logger` | **Must** |
| Thread name | `%t`, `%thread` | **Must** |
| Line separator | `%n` | **Must** |
| Throwable / stack trace | `%ex`, `%exception`, `%throwable` | **Must** *(capability; depth is Could)* |
| Throwable, root-cause-first | `%rEx`, `%rootException` | Could |
| Context map (MDC) | `%X`, `%mdc` | Should |
| Marker | `%marker` | Should *(Must for your framework — you already ship `marker:` sugar)* |
| Source: class | `%C`, `%class` | Could *(stack walk — see notes)* |
| Source: method | `%M`, `%method` | Could *(stack walk)* |
| Source: line number | `%L`, `%line` | Could *(stack walk)* |
| Source: file | `%F`, `%file` | Could *(stack walk)* |
| Process ID | `%pid`, `%processId` | Could |
| Thread ID | `%tid`, `%T` | Could |
| Host name | *(no standard; lookup)* | Could |
| Sequence number | `%sn`, `%sequenceNumber` | Could |
| Relative time / uptime | `%r`, `%relative` | Could |
| Nano time | `%N`, `%nano` | Could |

## Modifiers

Different axis — these attach to a field rather than standing alone.

| Modifier | Pattern syntax (example) | State |
|---|---|---|
| Timestamp format | `%d{HH:mm:ss.SSS}` | **Must** *(only field whose default isn't obvious)* |
| Min-width / left-right pad | `%-5level`, `%5level` | Should |
| Max-width / truncate | `%.30logger` | Should |
| Dotted-name abbreviation | `%logger{36}`, `%c{1}` | Could |
| Colour / highlight | `%highlight{...}`, `%style{...}` | Could |
| Case transform | *(no standard token)* | Could |
| Conditional literals (suppress on empty) | `%notEmpty{[%marker] }` | Could *(hardest to defer — couples a field's emptiness to adjacent literals)* |

## Escaping — conditional, not a tier

Not must/should/could in the abstract. **Zero** escaping for plain-text layouts. The moment a structured layout exists (JSON / GELF / CEF), correct escaping inside *that* layout is a hard requirement — unescaped output is invalid, not just ugly. Own it inside each structured layout; never make it a global modifier.

## Notes

- **Throwable — capability vs. depth.** Rendering *a* stack trace is Must (an error line without one is worthless). Common-frame elision, per-frame jar/version packaging, and configurable cause depth are Could.
- **Source location is the expensive field.** It forces a stack walk at capture. Support is Could, and even then it should be off by default.
- **Structured output isn't a field or a modifier** — it's a whole other `Layout` implementation that ignores the component list and field-maps directly. JSON is a pragmatic Should (everything ingests it); other wire formats are Could, pulled up individually when a sink demands them.

## Ruthless first cut

Build: `%d` + `%level` + `%msg` + `%logger` + `%thread` + `%n` + basic `%throwable`, with timestamp-format + left/right pad + truncate. That's the entire Must-and-Should core. Everything else is a bolt-on for when a real line looks wrong or a real sink refuses your bytes.
