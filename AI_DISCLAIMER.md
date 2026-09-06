# AI Disclaimer

This document states how generative AI tools were used in the development of LUtils, so that users of the library can judge the provenance of its code for themselves.

## Where AI was used

AI assistance was used for two parts of this project:

- **Tests**: The unit and integration tests under `src/test/java` were largely drafted with AI assistance, following the conventions in [TestGuidelines.md](TestGuidelines.md).
- **Documentation**: Javadoc comments, Markdown documents and other explanatory text were largely drafted with AI assistance, following the conventions in [DocumentationGuidelines.md](DocumentationGuidelines.md).

## Where AI was not used

The library itself is written by hand.
The public API, the data structures and the algorithms in `src/main/java` are the result of my own design decisions, and AI was not used to author them.

## Review and responsibility

Every AI-assisted contribution was reviewed before it entered the repository.
Generated tests were checked against the behavior they are meant to pin down, and generated documentation was checked against the code it describes.

AI assistance does not shift responsibility.
The maintainer of this project is accountable for all code and documentation in it, regardless of how a given line was drafted.
Mistakes are mine, not the tool's.

## Reporting issues

Documentation is not regenerated automatically and may lag behind the code.
If you find a test that asserts the wrong thing, or documentation that no longer matches the implementation, please open an issue.
