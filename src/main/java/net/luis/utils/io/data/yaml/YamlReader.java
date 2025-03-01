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

import com.google.common.collect.Lists;
import net.luis.utils.io.data.InputProvider;
import net.luis.utils.io.data.yaml.exception.YamlSyntaxException;
import net.luis.utils.io.reader.StringReader;
import net.luis.utils.util.Either;
import net.luis.utils.util.Pair;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

// Not supported features:
//  - anchor definition on anchor reference
//  - tag definition on anchor reference
//  - plain multi-line scalar
//  - tag definition before anchor definition

/**
 *
 * @author Luis-St
 *
 */

// ToDo:
//  - Objects.requireNonNull -> currently missing
//  - Binary and Timestamp and Tag are not implemented in readYamlValue -> conversion & validation
//  - Placing anchor and tags in seperated lines (sequence and mapping -> before actual value)
public class YamlReader implements AutoCloseable {
	
	private static final List<String> SUPPORTED_YAML_TAGS = List.of(
		"null", "bool", "int", "float", "str", "binary", "timestamp", "seq", "map"
	);
	
	private final YamlConfig config;
	private final StringReader reader;
	
	public YamlReader(@NotNull String string) {
		this(string, YamlConfig.DEFAULT);
	}
	
	public YamlReader(@NotNull String string, @NotNull YamlConfig config) {
		this.config = Objects.requireNonNull(config, "Json config must not be null");
		this.reader = new StringReader(Objects.requireNonNull(string, "String must not be null"));
	}
	
	public YamlReader(@NotNull InputProvider input) {
		this(input, YamlConfig.DEFAULT);
	}
	
	public YamlReader(@NotNull InputProvider input, @NotNull YamlConfig config) {
		this.config = Objects.requireNonNull(config, "Json config must not be null");
		this.reader = new StringReader(new InputStreamReader(Objects.requireNonNull(input, "Input must not be null").getStream(), config.charset()));
	}
	
	public @NotNull YamlDocument readYamlDocument() {
		YamlDocument document = new YamlDocument();
		while (this.reader.canRead()) {
			document.add(this.readYamlStruct());
		}
		return document;
	}
	
	public @NotNull YamlDocument readResolvedYamlDocument() {
		YamlDocument document = this.readYamlDocument();
		document.resolveAnchors();
		return document;
	}
	
	public @NotNull YamlStruct readYamlStruct() {
		this.reader.skipWhitespaces();
		YamlStruct struct = this.readYamlStruct(0, this.readLinesOfYamlStruct());
		this.reader.skipWhitespaces();
		return struct;
	}
	
	private @NotNull YamlStruct readYamlStruct(int expectedIndent, @NotNull List<Line> lines) {
		if (lines.size() == 1) {
			Pair<String, Either<String, YamlNode>> pair = this.readYamlKeyValuePair(new StringReader(lines.getFirst().line));
			String key = pair.getFirst();
			return pair.getSecond().mapTo(
				anchor -> new YamlStruct(key, anchor),
				node -> new YamlStruct(key, node)
			);
		}
		StringReader reader = new StringReader(lines.removeFirst().line);
		String key = this.readYamlKey(reader);
		Optional<String> optionalAnchor = this.readOptionalYamlAnchor(reader);
		Optional<YamlTag> optionalTag = this.readOptionalYamlTag(reader);
		if (this.onlyWhitespacesRemain(reader)) {
			Line nextLine = lines.getFirst();
			if (nextLine.indent != 1) {
				throw new YamlSyntaxException("Invalid yaml structure, expected indent level 1 but got " + nextLine.indent + " in line: '" + nextLine.indentedLine(this.config) + "'");
			}
			YamlNode node = null;
			if (nextLine.line.startsWith("-")) {
				node = this.readYamlSequence(expectedIndent + 1, lines);
			} else {
				node = this.readYamlMapping(expectedIndent + 1, lines);
			}
			optionalAnchor.ifPresent(node::setAnchor);
			if (optionalTag.isPresent()) {
				YamlTag tag = optionalTag.get();
				if (node.isYamlSequence()) {
					if (tag != YamlTag.SEQUENCE) {
						throw new YamlSyntaxException("Invalid yaml tag, tag '" + tag + "' is not compatible with sequence '" + key + "'");
					}
				} else if (node.isYamlMapping()) {
					if (tag != YamlTag.MAPPING) {
						throw new YamlSyntaxException("Invalid yaml tag, tag '" + tag + "' is not compatible with mapping '" + key + "'");
					}
				}
			}
			return new YamlStruct(key, node);
		}
		char next = reader.peek();
		YamlScalar scalar = this.readYamlMultiline(reader, expectedIndent + 1, lines);
		if (optionalTag.isPresent()) {
			YamlTag tag = optionalTag.get();
			if (tag != YamlTag.STR && tag != YamlTag.BINARY && tag != YamlTag.TIMESTAMP) {
				throw new YamlSyntaxException("Invalid yaml tag, tag '" + tag + "' is not compatible with multiline scalar: '" + scalar.getAsString() + "'");
			}
		}
		optionalAnchor.ifPresent(scalar::setAnchor);
		return new YamlStruct(key, scalar);
	}
	
	//region Line reading
	private @NotNull List<Line> readLinesOfYamlStruct() {
		List<Line> lines = Lists.newArrayList();
		while (this.reader.canRead()) {
			this.reader.mark();
			String line = this.readYamlLine();
			int indent = this.getIndent(line);
			if (line.isBlank() && indent == 0) {
				continue; // Skip empty lines, but keep blank lines with indent
			}
			if (indent == 0) {
				if (lines.isEmpty()) {
					lines.add(new Line(line, 0));
				} else {
					this.reader.reset(); // Line is part of the next struct
					return lines;
				}
			} else {
				if (lines.isEmpty()) {
					throw new YamlSyntaxException("Invalid yaml structure, expected a line with indent level zero but got a line with indent level " + this.getIndent(line));
				}
				String unindentedLine = line.substring(this.config.indent().length() * indent);
				lines.add(new Line(unindentedLine, indent));
			}
		}
		return lines;
	}
	
	private @NotNull String readYamlLine() {
		while (this.reader.canRead()) {
			String line = this.removeComments(this.reader.readLine(false));
			if (line.isEmpty() || line.startsWith("#")) {
				continue;
			}
			return line;
		}
		return "";
	}
	
	private @NotNull String removeComments(@NotNull String line) {
		StringReader lineReader = new StringReader(line);
		StringBuilder lineWithoutComments = new StringBuilder();
		while (lineReader.canRead()) {
			char c = lineReader.read();
			if (c == '#') {
				break;
			}
			lineWithoutComments.append(c);
		}
		return lineWithoutComments.toString();
	}
	
	private @NotNull List<Line> readAndDropLinesWithIndent(@NotNull List<Line> lines, int indent, boolean allowDeeperIndent) {
		int lastLineIndex = -1;
		for (int i = 0; i < lines.size(); i++) {
			Line line = lines.get(i);
			if (allowDeeperIndent ? line.indent >= indent : line.indent == indent) {
				lastLineIndex = i;
			} else {
				break;
			}
		}
		List<Line> linesWithIndent = Lists.newArrayList();
		if (lastLineIndex == -1) {
			return linesWithIndent;
		}
		for (int i = 0; i <= lastLineIndex; i++) {
			linesWithIndent.add(lines.removeFirst());
		}
		return linesWithIndent;
	}
	//endregion
	
	//region Indentation and whitespace handling
	private int getIndent(@NotNull String line) {
		String indent = this.config.indent();
		int indentLevel = 0;
		while (line.startsWith(indent)) {
			indentLevel++;
			line = line.substring(indent.length());
		}
		return indentLevel;
	}
	
	private boolean onlyWhitespacesRemain(@NotNull StringReader lineReader) { // ToDo: Move to StringReader
		lineReader.mark();
		lineReader.skipWhitespaces();
		if (!lineReader.canRead()) {
			return true;
		}
		lineReader.reset();
		return false;
	}
	//endregion
	
	//region Key, value and tag reading
	private @NotNull String readYamlKey(@NotNull StringReader reader) {
		StringBuilder key = new StringBuilder();
		while (reader.canRead()) {
			char c = reader.read();
			if (key.isEmpty()) {
				if (Character.isWhitespace(c)) {
					throw new YamlSyntaxException("Invalid yaml key, key must not start with a whitespace character");
				}
				if (ArrayUtils.contains(YamlHelper.SPECIAL_CHARACTERS, c)) {
					throw new YamlSyntaxException("Invalid yaml key, key must not start with a special character (*, &, !, >, |, #, @, %, [, {, ' or \")");
				}
			}
			if (c == ':' && Character.isWhitespace(reader.peek())) {
				reader.skip();
				break;
			}
			key.append(c);
		}
		return key.toString().stripTrailing();
	}
	
	private @NotNull Pair<String, Either</*Anchor*/ String, YamlNode>> readYamlKeyValuePair(@NotNull StringReader reader) {
		String key = this.readYamlKey(reader);
		if (this.onlyWhitespacesRemain(reader)) {
			return Pair.of(key, Either.right(YamlNull.INSTANCE));
		}
		
		Optional<String> optionalAnchor = this.readOptionalYamlAnchor(reader);
		if (this.onlyWhitespacesRemain(reader)) {
			return Pair.of(key, Either.right(optionalAnchor.map(YamlNull::createNull).orElse(YamlNull.INSTANCE)));
		}
		
		Either</*Anchor*/ String, YamlNode> either = this.readYamlValue(reader);
		if (either.isLeft()) {
			if (optionalAnchor.isPresent()) {
				throw new YamlSyntaxException("Invalid yaml key-value pair, anchor definition is not supported on a anchor reference (by this reader), line: '" + reader.getString() + "'");
			}
			return Pair.of(key, either);
		} else {
			YamlNode node = either.rightOrThrow();
			if (optionalAnchor.isPresent()) {
				if (node.isYamlNull()) {
					node = YamlNull.createNull(optionalAnchor.get());
				} else {
					node.setAnchor(optionalAnchor.get());
				}
			}
			return Pair.of(key, Either.right(node));
		}
	}
	
	private @NotNull Optional<String> readOptionalYamlAnchor(@NotNull StringReader reader) {
		if (!reader.canRead()) {
			return Optional.empty();
		}
		if (!Character.isWhitespace(reader.peek())) {
			throw new YamlSyntaxException("Invalid yaml anchor, expected at least one whitespace before anchor definition but got '" + reader.peek() + "' in line: '" + reader.getString() + "'");
		}
		reader.mark();
		reader.skipWhitespaces();
		if (reader.peek() != '&') {
			reader.reset(); // Keep the whitespaces since it is not an anchor
			return Optional.empty();
		}
		reader.skip();
		
		StringBuilder anchor = new StringBuilder();
		while (reader.canRead() && !Character.isWhitespace(reader.peek())) {
			char c = reader.read();
			if (c == '{' || c == '[' || c == '}' || c == ']') {
				throw new YamlSyntaxException("Invalid yaml anchor, anchor must not contain special characters ({, [, }, ])");
			}
			anchor.append(c);
		}
		return Optional.of(anchor.toString());
	}
	
	private @NotNull Optional<YamlTag> readOptionalYamlTag(@NotNull StringReader reader) {
		if (!reader.canRead()) {
			return Optional.empty();
		}
		if (!Character.isWhitespace(reader.peek())) {
			throw new YamlSyntaxException("Invalid yaml tag, expected at least one whitespace before tag definition but got '" + reader.peek() + "' in line: '" + reader.getString() + "'");
		}
		reader.mark();
		reader.skipWhitespaces();
		if (reader.peek() != '!') {
			reader.reset(); // Keep the whitespaces since it is not a tag
			return Optional.empty();
		}
		reader.skip();
		if (reader.read() != '!') {
			throw new YamlSyntaxException("Invalid yaml tag, expected two exclamation marks in tag definition but got only one, line: '" + reader.getString() + "'");
		}
		return Optional.of(YamlTag.fromString(reader.readExpected(SUPPORTED_YAML_TAGS, false)));
	}
	//endregion
	
	//region Value reading
	private @NotNull Either</*Anchor*/ String, YamlNode> readYamlValue(@NotNull StringReader reader) {
		if (!reader.canRead()) {
			return Either.right(YamlNull.INSTANCE);
		}
		if (!Character.isWhitespace(reader.peek())) {
			throw new YamlSyntaxException("Invalid yaml value, expected at least one whitespace before value definition but got '" + reader.peek() + "' in line: '" + reader.getString() + "'");
		}
		
		Optional<YamlTag> optionalTag = this.readOptionalYamlTag(reader);
		if (this.onlyWhitespacesRemain(reader)) {
			return Either.right(optionalTag.map(YamlTag::getDefaultValue).orElse(YamlNull.INSTANCE));
		}
		return this.readYamlValue(reader, optionalTag.orElse(null));
	}
	
	private @NotNull Either</*Anchor*/ String, YamlNode> readYamlValue(@NotNull StringReader reader, @Nullable YamlTag tag) {
		// Do not invoke this method directly, use readYamlValue(StringReader) instead -> remove and add to javadoc
		reader.skipWhitespaces();
		if (!reader.canRead()) {
			return Either.right(YamlNull.INSTANCE);
		}
		if (reader.peek() == '*') {
			reader.skip();
			if (tag != null) {
				throw new YamlSyntaxException("Invalid yaml value, specification of yaml tag '" + tag + "' is not supported on anchor references (by this reader), line: '" + reader.getString() + "'");
			}
			String anchor = reader.readRemaining().strip();
			if (anchor.isEmpty()) {
				throw new YamlSyntaxException("Invalid yaml anchor reference, anchor must not be empty, line: '" + reader.getString() + "'");
			}
			if (anchor.contains(" ")) {
				throw new YamlSyntaxException("Invalid yaml anchor reference, anchor must not contain whitespaces, line: '" + reader.getString() + "'");
			}
			return Either.left(anchor);
		}
		
		String value = reader.readRemaining().strip();
		if (value.length() >= 2) {
			char first = value.charAt(0);
			char last = value.charAt(value.length() - 1);
			String inner = value.substring(1, value.length() - 1);
			if ((first == '"' && last == '"') || (first == '\'' && last == '\'')) {
				if (tag == null || tag == YamlTag.STR) {
					return Either.right(new YamlScalar(inner));
				}
				return this.readYamlValue(new StringReader(inner), tag);
			} else if (first == '[' && last == ']') {
				if (tag == null || tag == YamlTag.SEQUENCE) {
					return Either.right(this.parseInlineYamlSequence(new StringReader(value)));
				} else if (tag == YamlTag.STR) {
					return Either.right(new YamlScalar(value));
				}
				throw new YamlSyntaxException("Invalid yaml tag, tag '" + tag + "' is not compatible with value '" + value + "'");
			} else if (first == '{' && last == '}') {
				if (tag == null || tag == YamlTag.MAPPING) {
					return Either.right(this.parseInlineYamlMapping(new StringReader(value)));
				} else if (tag == YamlTag.STR) {
					return Either.right(new YamlScalar(value));
				}
				throw new YamlSyntaxException("Invalid yaml tag, tag '" + tag + "' is not compatible with value '" + value + "'");
			}
		}
		if ("null".equalsIgnoreCase(value) || "~".equals(value)) {
			return switch (tag) {
				case BOOL -> Either.right(new YamlScalar(false));
				case INT -> Either.right(new YamlScalar(0));
				case FLOAT -> Either.right(new YamlScalar(0.0));
				case STR -> Either.right(new YamlScalar(""));
				case SEQUENCE -> Either.right(new YamlSequence());
				case MAPPING -> Either.right(new YamlMapping());
				case null, default -> Either.right(YamlNull.INSTANCE);
			};
		} else if ("true".equalsIgnoreCase(value)) {
			return switch (tag) {
				case INT -> Either.right(new YamlScalar(1));
				case FLOAT -> Either.right(new YamlScalar(1.0));
				case STR -> Either.right(new YamlScalar("true"));
				case NULL, SEQUENCE, MAPPING -> throw new YamlSyntaxException("Invalid yaml tag, tag '" + tag + "' is not compatible with value 'true'");
				case null, default -> Either.right(new YamlScalar(true));
			};
		} else if ("false".equalsIgnoreCase(value)) {
			return switch (tag) {
				case INT -> Either.right(new YamlScalar(0));
				case FLOAT -> Either.right(new YamlScalar(0.0));
				case STR -> Either.right(new YamlScalar("false"));
				case NULL, SEQUENCE, MAPPING -> throw new YamlSyntaxException("Invalid yaml tag, tag '" + tag + "' is not compatible with value 'false'");
				case null, default -> Either.right(new YamlScalar(false));
			};
		}
		
		try {
			Number number = new StringReader(value).readNumber();
			if (tag == null) {
				return Either.right(new YamlScalar(number));
			} else if (tag == YamlTag.INT) {
				return Either.right(new YamlScalar(number.longValue()));
			} else if (tag == YamlTag.FLOAT) {
				return Either.right(new YamlScalar(number.doubleValue()));
			}
		} catch (RuntimeException ignored) {}
		return Either.right(new YamlScalar(value));
	}
	
	private @NotNull YamlSequence parseInlineYamlSequence(@NotNull StringReader reader) {
		return null;
	}
	
	private @NotNull YamlMapping parseInlineYamlMapping(@NotNull StringReader reader) {
		return null;
	}
	
	private @NotNull YamlScalar readYamlMultiline(@NotNull StringReader reader, int expectedIndent, @NotNull List<Line> lines) {
		char next = reader.peek();
		if (next == '|' || next == '>' || next == '\'' || next == '"') {
			reader.skip();
			return this.readYamlMultiline(reader, next, expectedIndent + 1, lines);
		} else {
			return this.readYamlMultiline(reader, '\0', expectedIndent + 1, lines);
		}
	}
	
	private @NotNull YamlScalar readYamlMultiline(@NotNull StringReader reader, char next, int expectedIndent, @NotNull List<Line> lines) {
		List<Line> multiline = this.readAndDropLinesWithIndent(lines, expectedIndent, false);
		if (next == '|' || next == '>') { // Block scalar
			char mode = '\0';
			if (reader.canRead() && !this.onlyWhitespacesRemain(reader)) {
				mode = reader.read();
				if (mode != '+' && mode != '-') {
					throw new YamlSyntaxException("Invalid yaml block scalar, expected '+' or '-' after '|' or '>' but got '" + mode + "'");
				}
			}
			String value = switch (mode) {
				case '+' -> multiline.stream().map(line -> line.line).collect(Collectors.joining(System.lineSeparator()));
				case '-' -> multiline.stream().map(line -> line.line).collect(Collectors.joining(""));
				default -> multiline.stream().map(line -> line.line).collect(Collectors.joining(System.lineSeparator())).stripTrailing();
			};
			return new YamlScalar(value);
		} else if (next == '\'' || next == '"') { // Quoted scalar
			StringBuilder builder = new StringBuilder(reader.readRemaining());
			multiline.forEach(line -> builder.append(System.lineSeparator()).append(line.line));
			String value = builder.toString();
			if (value.charAt(value.length() - 1) != next) {
				throw new YamlSyntaxException("Invalid yaml quoted scalar, expected closing quote '" + next + "' but got invalid end, multiline: '" + value + "'");
			}
			return new YamlScalar(value.substring(0, value.length() - 1));
		} else if (next == '\0') {
			StringBuilder builder = new StringBuilder(reader.readRemaining());
			multiline.forEach(line -> builder.append(System.lineSeparator()).append(line.line));
			return new YamlScalar(builder.toString());
		}
		throw new YamlSyntaxException("Invalid yaml multiline scalar, expected block scalar or quoted scalar but got '" + next + "'");
	}
	
	private @NotNull YamlSequence readYamlSequence(int expectedIndent, @NotNull List<Line> lines) {
		List<List<Line>> items = Lists.newArrayList();
		
		List<Line> currentBlock = null;
		for (Line line : this.readAndDropLinesWithIndent(lines, expectedIndent, true)) {
			if (line.indent() == expectedIndent) {
				if (currentBlock != null) {
					items.add(currentBlock);
				}
				currentBlock = new ArrayList<>();
				currentBlock.add(line);
			} else if (currentBlock != null && line.indent() > expectedIndent) {
				currentBlock.add(line);
			}
		}
		if (currentBlock != null) {
			items.add(currentBlock);
		}
		
		YamlSequence sequence = new YamlSequence();
		for (List<Line> itemLines : items) {
			Either</*Anchor*/ String, YamlNode> item = this.parseYamlSequenceItem(itemLines);
			item.ifLeft(sequence::addAnchor);
			item.ifRight(sequence::add);
		}
		return sequence;
	}
	
	private @NotNull Either</*Anchor*/ String, YamlNode> parseYamlSequenceItem(@NotNull List<Line> itemLines) {
		Line itemDefinition = itemLines.removeFirst();
		StringReader reader = new StringReader(itemDefinition.line);
		if (!reader.canRead() || reader.peek() != '-') {
			throw new YamlSyntaxException("Invalid yaml sequence item, expected '-' at the beginning of the line but got '" + reader.peek() + "', line: '" + itemDefinition.indentedLine(this.config) + "'");
		}
		reader.skip();
		
		if (itemLines.isEmpty()) {
			if (!reader.canRead() || this.onlyWhitespacesRemain(reader)) {
				return Either.right(YamlNull.INSTANCE);
			}
			Optional<String> optionalAnchor = this.readOptionalYamlAnchor(reader);
			if (!itemDefinition.line.contains("-") && !itemDefinition.line.contains(": ")) { // ToDo: Extraction to own method if needed
				Either<String, YamlNode> value = this.readYamlValue(reader);
				if (value.isLeft()) {
					if (optionalAnchor.isPresent()) {
						throw new YamlSyntaxException("Invalid yaml sequence item, anchor definition is not supported on a anchor reference (by this reader), line: '" + itemDefinition.indentedLine(this.config) + "'");
					}
					return value;
				}
				return value.mapRight(node -> {
					optionalAnchor.ifPresent(node::setAnchor);
					return node;
				});
			}
			
			Optional<YamlTag> optionalTag = this.readOptionalYamlTag(reader);
			if (itemDefinition.line.contains(": ")) {
				return Either.right(this.readYamlStruct(0, Lists.newArrayList(new Line(reader.readRemaining().strip(), 0))));
			} else if (itemDefinition.line.contains("-")) {
				return Either.right(this.readYamlSequence(itemDefinition.indent + 1, itemLines));
			}
		}
		
		Optional<String> optionalAnchor = this.readOptionalYamlAnchor(reader);
		Optional<YamlTag> optionalTag = this.readOptionalYamlTag(reader);
		if (!this.onlyWhitespacesRemain(reader)) {
			if (!Character.isWhitespace(reader.peek())) {
				throw new YamlSyntaxException("Invalid yaml sequence item, expected at least one whitespace before value definition but got '" + reader.peek() + "' in line: '" + itemDefinition.indentedLine(this.config) + "'");
			}
			reader.skip();
			char next = reader.peek();
			if (next == '|' || next == '>') {
				return Either.right(this.readYamlMultiline(reader, next, itemDefinition.indent + 1, itemLines));
			}
			itemLines.addFirst(new Line(reader.readRemaining().strip(), itemDefinition.indent + 1)); // Move content to the next line (avoid duplicate code)
		}
		
		YamlNode node = null;
		Line nextLine = itemLines.getFirst();
		if (nextLine.line.startsWith("-")) {
			node = this.readYamlSequence(nextLine.indent + 1, itemLines);
		} else if (nextLine.line.contains(": ")) {
			node = this.readYamlMapping(nextLine.indent + 1, itemLines);
		} else {
			node = this.readYamlMultiline(reader, nextLine.indent + 1, itemLines);
		}
		optionalAnchor.ifPresent(node::setAnchor);
		
		// ToDo: Include yaml tag logic directly in the YamlTag class -> reduces code complexity of the reader
		
		return Either.right(node);
	}
	
	private @NotNull YamlMapping readYamlMapping(int expectedIndent, @NotNull List<Line> lines) {
		List<Line> mapping = this.readAndDropLinesWithIndent(lines, expectedIndent, true);
		return null;
	}
	//endregion
	
	@Override
	public void close() throws IOException {
		this.reader.readRemaining(); // Assert that there is no remaining content
	}
	
	//region Internal
	private record Line(@NotNull String line, int indent) {
		
		public @NotNull String indentedLine(@NotNull YamlConfig config) {
			return config.indent().repeat(this.indent) + this.line;
		}
	}
	//endregion
}
