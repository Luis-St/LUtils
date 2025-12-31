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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link YamlBuilder}.<br>
 *
 * @author Luis-St
 */
class YamlBuilderTest {

	@Test
	void mappingFactory() {
		YamlBuilder builder = YamlBuilder.mapping();
		assertNotNull(builder);
		assertTrue(builder.isInMappingContext());
		assertFalse(builder.isInSequenceContext());
		assertTrue(builder.isAtRootLevel());
	}

	@Test
	void sequenceFactory() {
		YamlBuilder builder = YamlBuilder.sequence();
		assertNotNull(builder);
		assertTrue(builder.isInSequenceContext());
		assertFalse(builder.isInMappingContext());
		assertTrue(builder.isAtRootLevel());
	}

	@Test
	void addElementToMapping() {
		YamlMapping result = YamlBuilder.mapping()
			.add("key", new YamlScalar("value"))
			.buildMapping();

		assertEquals(new YamlScalar("value"), result.get("key"));
	}

	@Test
	void addNullElementToMapping() {
		YamlMapping result = YamlBuilder.mapping()
			.add("key", (YamlElement) null)
			.buildMapping();

		assertEquals(YamlNull.INSTANCE, result.get("key"));
	}

	@Test
	void addStringToMapping() {
		YamlMapping result = YamlBuilder.mapping()
			.add("name", "John")
			.buildMapping();

		assertEquals(new YamlScalar("John"), result.get("name"));
	}

	@Test
	void addNullStringToMapping() {
		YamlMapping result = YamlBuilder.mapping()
			.add("name", (String) null)
			.buildMapping();

		assertEquals(YamlNull.INSTANCE, result.get("name"));
	}

	@Test
	void addBooleanToMapping() {
		YamlMapping result = YamlBuilder.mapping()
			.add("active", true)
			.add("disabled", false)
			.buildMapping();

		assertEquals(new YamlScalar(true), result.get("active"));
		assertEquals(new YamlScalar(false), result.get("disabled"));
	}

	@Test
	void addNumberToMapping() {
		YamlMapping result = YamlBuilder.mapping()
			.add("int", 42)
			.add("double", 3.14)
			.add("long", 123456789L)
			.buildMapping();

		assertEquals(new YamlScalar(42), result.get("int"));
		assertEquals(new YamlScalar(3.14), result.get("double"));
		assertEquals(new YamlScalar(123456789L), result.get("long"));
	}

	@Test
	void addNullNumberToMapping() {
		YamlMapping result = YamlBuilder.mapping()
			.add("value", (Number) null)
			.buildMapping();

		assertEquals(YamlNull.INSTANCE, result.get("value"));
	}

	@Test
	void addMappingBuilderToMapping() {
		YamlBuilder nested = YamlBuilder.mapping()
			.add("inner", "value");

		YamlMapping result = YamlBuilder.mapping()
			.addMapping("nested", nested)
			.buildMapping();

		assertTrue(result.get("nested").isYamlMapping());
		assertEquals(new YamlScalar("value"), result.get("nested").getAsYamlMapping().get("inner"));
	}

	@Test
	void addSequenceBuilderToMapping() {
		YamlBuilder nested = YamlBuilder.sequence()
			.add("a")
			.add("b");

		YamlMapping result = YamlBuilder.mapping()
			.addSequence("items", nested)
			.buildMapping();

		assertTrue(result.get("items").isYamlSequence());
		assertEquals(2, result.get("items").getAsYamlSequence().size());
	}

	@Test
	void addWithNullKeyThrows() {
		YamlBuilder builder = YamlBuilder.mapping();
		assertThrows(NullPointerException.class, () -> builder.add(null, "value"));
	}

	@Test
	void addMappingWithNullBuilderThrows() {
		YamlBuilder builder = YamlBuilder.mapping();
		assertThrows(NullPointerException.class, () -> builder.addMapping("key", null));
	}

	@Test
	void addSequenceWithNullBuilderThrows() {
		YamlBuilder builder = YamlBuilder.mapping();
		assertThrows(NullPointerException.class, () -> builder.addSequence("key", null));
	}

	@Test
	void addWithAnchorElement() {
		YamlMapping result = YamlBuilder.mapping()
			.addWithAnchor("key", new YamlScalar("value"), "myAnchor")
			.buildMapping();

		YamlElement element = result.get("key");
		assertTrue(element.isYamlAnchor());
		assertEquals("myAnchor", element.getAsYamlAnchor().getName());
		assertEquals(new YamlScalar("value"), element.getAsYamlAnchor().getElement());
	}

	@Test
	void addWithAnchorString() {
		YamlMapping result = YamlBuilder.mapping()
			.addWithAnchor("key", "value", "strAnchor")
			.buildMapping();

		YamlElement element = result.get("key");
		assertTrue(element.isYamlAnchor());
		assertEquals("strAnchor", element.getAsYamlAnchor().getName());
	}

	@Test
	void addWithAnchorNullString() {
		YamlMapping result = YamlBuilder.mapping()
			.addWithAnchor("key", (String) null, "nullAnchor")
			.buildMapping();

		YamlElement element = result.get("key");
		assertTrue(element.isYamlAnchor());
		assertEquals(YamlNull.INSTANCE, element.getAsYamlAnchor().getElement());
	}

	@Test
	void addWithAnchorBoolean() {
		YamlMapping result = YamlBuilder.mapping()
			.addWithAnchor("key", true, "boolAnchor")
			.buildMapping();

		YamlElement element = result.get("key");
		assertTrue(element.isYamlAnchor());
		assertEquals(new YamlScalar(true), element.getAsYamlAnchor().getElement());
	}

	@Test
	void addWithAnchorNumber() {
		YamlMapping result = YamlBuilder.mapping()
			.addWithAnchor("key", 42, "numAnchor")
			.buildMapping();

		YamlElement element = result.get("key");
		assertTrue(element.isYamlAnchor());
		assertEquals(new YamlScalar(42), element.getAsYamlAnchor().getElement());
	}

	@Test
	void addWithAnchorNullNumber() {
		YamlMapping result = YamlBuilder.mapping()
			.addWithAnchor("key", (Number) null, "nullNumAnchor")
			.buildMapping();

		YamlElement element = result.get("key");
		assertTrue(element.isYamlAnchor());
		assertEquals(YamlNull.INSTANCE, element.getAsYamlAnchor().getElement());
	}

	@Test
	void addAliasToMapping() {
		YamlMapping result = YamlBuilder.mapping()
			.addAlias("ref", "targetAnchor")
			.buildMapping();

		YamlElement element = result.get("ref");
		assertTrue(element.isYamlAlias());
		assertEquals("targetAnchor", element.getAsYamlAlias().getAnchorName());
	}

	@Test
	void addWithAnchorNullElementThrows() {
		YamlBuilder builder = YamlBuilder.mapping();
		assertThrows(NullPointerException.class, () -> builder.addWithAnchor("key", (YamlElement) null, "anchor"));
	}

	@Test
	void addWithAnchorNullAnchorNameThrows() {
		YamlBuilder builder = YamlBuilder.mapping();
		assertThrows(NullPointerException.class, () -> builder.addWithAnchor("key", new YamlScalar("value"), null));
	}

	@Test
	void addAliasNullAnchorNameThrows() {
		YamlBuilder builder = YamlBuilder.mapping();
		assertThrows(NullPointerException.class, () -> builder.addAlias("key", null));
	}

	@Test
	void addElementToSequence() {
		YamlSequence result = YamlBuilder.sequence()
			.add(new YamlScalar("value"))
			.buildSequence();

		assertEquals(1, result.size());
		assertEquals(new YamlScalar("value"), result.get(0));
	}

	@Test
	void addNullElementToSequence() {
		YamlSequence result = YamlBuilder.sequence()
			.add((YamlElement) null)
			.buildSequence();

		assertEquals(YamlNull.INSTANCE, result.get(0));
	}

	@Test
	void addStringToSequence() {
		YamlSequence result = YamlBuilder.sequence()
			.add("hello")
			.buildSequence();

		assertEquals(new YamlScalar("hello"), result.get(0));
	}

	@Test
	void addNullStringToSequence() {
		YamlSequence result = YamlBuilder.sequence()
			.add((String) null)
			.buildSequence();

		assertEquals(YamlNull.INSTANCE, result.get(0));
	}

	@Test
	void addBooleanToSequence() {
		YamlSequence result = YamlBuilder.sequence()
			.add(true)
			.add(false)
			.buildSequence();

		assertEquals(new YamlScalar(true), result.get(0));
		assertEquals(new YamlScalar(false), result.get(1));
	}

	@Test
	void addNumberToSequence() {
		YamlSequence result = YamlBuilder.sequence()
			.add(1)
			.add(2.5)
			.add(3L)
			.buildSequence();

		assertEquals(3, result.size());
		assertEquals(new YamlScalar(1), result.get(0));
		assertEquals(new YamlScalar(2.5), result.get(1));
		assertEquals(new YamlScalar(3L), result.get(2));
	}

	@Test
	void addNullNumberToSequence() {
		YamlSequence result = YamlBuilder.sequence()
			.add((Number) null)
			.buildSequence();

		assertEquals(YamlNull.INSTANCE, result.get(0));
	}

	@Test
	void addMappingBuilderToSequence() {
		YamlBuilder nested = YamlBuilder.mapping()
			.add("key", "value");

		YamlSequence result = YamlBuilder.sequence()
			.addMapping(nested)
			.buildSequence();

		assertTrue(result.get(0).isYamlMapping());
	}

	@Test
	void addSequenceBuilderToSequence() {
		YamlBuilder nested = YamlBuilder.sequence()
			.add(1)
			.add(2);

		YamlSequence result = YamlBuilder.sequence()
			.addSequence(nested)
			.buildSequence();

		assertTrue(result.get(0).isYamlSequence());
		assertEquals(2, result.get(0).getAsYamlSequence().size());
	}

	@Test
	void addAllStrings() {
		YamlSequence result = YamlBuilder.sequence()
			.addAll("a", "b", "c")
			.buildSequence();

		assertEquals(3, result.size());
		assertEquals(new YamlScalar("a"), result.get(0));
		assertEquals(new YamlScalar("b"), result.get(1));
		assertEquals(new YamlScalar("c"), result.get(2));
	}

	@Test
	void addAllNumbers() {
		YamlSequence result = YamlBuilder.sequence()
			.addAll(1, 2, 3)
			.buildSequence();

		assertEquals(3, result.size());
		assertEquals(new YamlScalar(1), result.get(0));
		assertEquals(new YamlScalar(2), result.get(1));
		assertEquals(new YamlScalar(3), result.get(2));
	}

	@Test
	void addAllWithNullArrayThrows() {
		YamlBuilder builder = YamlBuilder.sequence();
		assertThrows(NullPointerException.class, () -> builder.addAll((String[]) null));
		assertThrows(NullPointerException.class, () -> builder.addAll((Number[]) null));
	}

	@Test
	void addWithAnchorToSequence() {
		YamlSequence result = YamlBuilder.sequence()
			.addWithAnchor(new YamlScalar("value"), "seqAnchor")
			.buildSequence();

		YamlElement element = result.get(0);
		assertTrue(element.isYamlAnchor());
		assertEquals("seqAnchor", element.getAsYamlAnchor().getName());
	}

	@Test
	void addWithAnchorStringToSequence() {
		YamlSequence result = YamlBuilder.sequence()
			.addWithAnchor("value", "strSeqAnchor")
			.buildSequence();

		YamlElement element = result.get(0);
		assertTrue(element.isYamlAnchor());
	}

	@Test
	void addWithAnchorNullStringToSequence() {
		YamlSequence result = YamlBuilder.sequence()
			.addWithAnchor((String) null, "nullSeqAnchor")
			.buildSequence();

		YamlElement element = result.get(0);
		assertTrue(element.isYamlAnchor());
		assertEquals(YamlNull.INSTANCE, element.getAsYamlAnchor().getElement());
	}

	@Test
	void addAliasToSequence() {
		YamlSequence result = YamlBuilder.sequence()
			.addAlias("targetAnchor")
			.buildSequence();

		YamlElement element = result.get(0);
		assertTrue(element.isYamlAlias());
		assertEquals("targetAnchor", element.getAsYamlAlias().getAnchorName());
	}

	@Test
	void addKeyValueToSequenceThrows() {
		YamlBuilder builder = YamlBuilder.sequence();
		assertThrows(IllegalStateException.class, () -> builder.add("key", "value"));
	}

	@Test
	void addValueToMappingThrows() {
		YamlBuilder builder = YamlBuilder.mapping();
		assertThrows(IllegalStateException.class, () -> builder.add("value"));
	}

	@Test
	void addAllToMappingThrows() {
		YamlBuilder builder = YamlBuilder.mapping();
		assertThrows(IllegalStateException.class, () -> builder.addAll("a", "b"));
	}

	@Test
	void startMappingInMapping() {
		YamlMapping result = YamlBuilder.mapping()
			.startMapping("nested")
				.add("inner", "value")
			.endMapping()
			.buildMapping();

		assertTrue(result.get("nested").isYamlMapping());
		assertEquals(new YamlScalar("value"), result.get("nested").getAsYamlMapping().get("inner"));
	}

	@Test
	void startMappingWithAnchorInMapping() {
		YamlMapping result = YamlBuilder.mapping()
			.startMappingWithAnchor("nested", "nestedAnchor")
				.add("key", "value")
			.endMapping()
			.buildMapping();

		YamlElement element = result.get("nested");
		assertTrue(element.isYamlAnchor());
		assertEquals("nestedAnchor", element.getAsYamlAnchor().getName());
	}

	@Test
	void deeplyNestedMappings() {
		YamlMapping result = YamlBuilder.mapping()
			.startMapping("level1")
				.startMapping("level2")
					.startMapping("level3")
						.add("deep", "value")
					.endMapping()
				.endMapping()
			.endMapping()
			.buildMapping();

		YamlMapping level1 = result.get("level1").getAsYamlMapping();
		YamlMapping level2 = level1.get("level2").getAsYamlMapping();
		YamlMapping level3 = level2.get("level3").getAsYamlMapping();
		assertEquals(new YamlScalar("value"), level3.get("deep"));
	}

	@Test
	void startSequenceInMapping() {
		YamlMapping result = YamlBuilder.mapping()
			.startSequence("items")
				.add("a")
				.add("b")
			.endSequence()
			.buildMapping();

		assertTrue(result.get("items").isYamlSequence());
		assertEquals(2, result.get("items").getAsYamlSequence().size());
	}

	@Test
	void startSequenceWithAnchorInMapping() {
		YamlMapping result = YamlBuilder.mapping()
			.startSequenceWithAnchor("items", "itemsAnchor")
				.add(1)
				.add(2)
			.endSequence()
			.buildMapping();

		YamlElement element = result.get("items");
		assertTrue(element.isYamlAnchor());
		assertEquals("itemsAnchor", element.getAsYamlAnchor().getName());
	}

	@Test
	void startMappingInSequence() {
		YamlSequence result = YamlBuilder.sequence()
			.startMapping()
				.add("key", "value")
			.endMapping()
			.buildSequence();

		assertTrue(result.get(0).isYamlMapping());
		assertEquals(new YamlScalar("value"), result.get(0).getAsYamlMapping().get("key"));
	}

	@Test
	void startMappingWithAnchorInSequence() {
		YamlSequence result = YamlBuilder.sequence()
			.startMappingWithAnchor("mapAnchor")
				.add("key", "value")
			.endMapping()
			.buildSequence();

		YamlElement element = result.get(0);
		assertTrue(element.isYamlAnchor());
	}

	@Test
	void startSequenceInSequence() {
		YamlSequence result = YamlBuilder.sequence()
			.startSequence()
				.add(1)
				.add(2)
			.endSequence()
			.buildSequence();

		assertTrue(result.get(0).isYamlSequence());
		assertEquals(2, result.get(0).getAsYamlSequence().size());
	}

	@Test
	void startSequenceWithAnchorInSequence() {
		YamlSequence result = YamlBuilder.sequence()
			.startSequenceWithAnchor("seqAnchor")
				.add("item")
			.endSequence()
			.buildSequence();

		YamlElement element = result.get(0);
		assertTrue(element.isYamlAnchor());
	}

	@Test
	void startMappingWithKeyInSequenceThrows() {
		YamlBuilder builder = YamlBuilder.sequence();
		assertThrows(IllegalStateException.class, () -> builder.startMapping("key"));
	}

	@Test
	void startMappingWithoutKeyInMappingThrows() {
		YamlBuilder builder = YamlBuilder.mapping();
		assertThrows(IllegalStateException.class, builder::startMapping);
	}

	@Test
	void startSequenceWithKeyInSequenceThrows() {
		YamlBuilder builder = YamlBuilder.sequence();
		assertThrows(IllegalStateException.class, () -> builder.startSequence("key"));
	}

	@Test
	void startSequenceWithoutKeyInMappingThrows() {
		YamlBuilder builder = YamlBuilder.mapping();
		assertThrows(IllegalStateException.class, builder::startSequence);
	}

	@Test
	void endMappingInSequenceContextThrows() {
		YamlBuilder builder = YamlBuilder.sequence();
		assertThrows(IllegalStateException.class, builder::endMapping);
	}

	@Test
	void endSequenceInMappingContextThrows() {
		YamlBuilder builder = YamlBuilder.mapping();
		assertThrows(IllegalStateException.class, builder::endSequence);
	}

	@Test
	void endMappingAtRootThrows() {
		YamlBuilder builder = YamlBuilder.mapping();
		assertThrows(IllegalStateException.class, builder::endMapping);
	}

	@Test
	void endSequenceAtRootThrows() {
		YamlBuilder builder = YamlBuilder.sequence();
		assertThrows(IllegalStateException.class, builder::endSequence);
	}

	@Test
	void startMappingNullKeyThrows() {
		YamlBuilder builder = YamlBuilder.mapping();
		assertThrows(NullPointerException.class, () -> builder.startMapping(null));
	}

	@Test
	void startSequenceNullKeyThrows() {
		YamlBuilder builder = YamlBuilder.mapping();
		assertThrows(NullPointerException.class, () -> builder.startSequence(null));
	}

	@Test
	void startMappingWithAnchorNullAnchorThrows() {
		YamlBuilder builder = YamlBuilder.mapping();
		assertThrows(NullPointerException.class, () -> builder.startMappingWithAnchor("key", null));
	}

	@Test
	void startSequenceWithAnchorNullAnchorThrows() {
		YamlBuilder builder = YamlBuilder.mapping();
		assertThrows(NullPointerException.class, () -> builder.startSequenceWithAnchor("key", null));
	}

	@Test
	void addIfTrueAdds() {
		YamlMapping result = YamlBuilder.mapping()
			.addIf(true, "key", "value")
			.buildMapping();

		assertEquals(new YamlScalar("value"), result.get("key"));
	}

	@Test
	void addIfFalseDoesNotAdd() {
		YamlMapping result = YamlBuilder.mapping()
			.addIf(false, "key", "value")
			.buildMapping();

		assertFalse(result.containsKey("key"));
	}

	@Test
	void addIfBooleanTrue() {
		YamlMapping result = YamlBuilder.mapping()
			.addIf(true, "flag", true)
			.buildMapping();

		assertEquals(new YamlScalar(true), result.get("flag"));
	}

	@Test
	void addIfBooleanFalse() {
		YamlMapping result = YamlBuilder.mapping()
			.addIf(false, "flag", true)
			.buildMapping();

		assertFalse(result.containsKey("flag"));
	}

	@Test
	void addIfNumberTrue() {
		YamlMapping result = YamlBuilder.mapping()
			.addIf(true, "count", 42)
			.buildMapping();

		assertEquals(new YamlScalar(42), result.get("count"));
	}

	@Test
	void addIfNumberFalse() {
		YamlMapping result = YamlBuilder.mapping()
			.addIf(false, "count", 42)
			.buildMapping();

		assertFalse(result.containsKey("count"));
	}

	@Test
	void addAliasIfTrue() {
		YamlMapping result = YamlBuilder.mapping()
			.addAliasIf(true, "ref", "anchor")
			.buildMapping();

		assertTrue(result.get("ref").isYamlAlias());
	}

	@Test
	void addAliasIfFalse() {
		YamlMapping result = YamlBuilder.mapping()
			.addAliasIf(false, "ref", "anchor")
			.buildMapping();

		assertFalse(result.containsKey("ref"));
	}

	@Test
	void buildReturnsElement() {
		YamlElement result = YamlBuilder.mapping()
			.add("key", "value")
			.build();

		assertTrue(result.isYamlMapping());
	}

	@Test
	void buildMappingSuccess() {
		YamlMapping result = YamlBuilder.mapping()
			.add("key", "value")
			.buildMapping();

		assertNotNull(result);
		assertTrue(result.containsKey("key"));
	}

	@Test
	void buildMappingFromSequenceThrows() {
		YamlBuilder builder = YamlBuilder.sequence().add("item");
		assertThrows(IllegalStateException.class, builder::buildMapping);
	}

	@Test
	void buildSequenceSuccess() {
		YamlSequence result = YamlBuilder.sequence()
			.add("item")
			.buildSequence();

		assertNotNull(result);
		assertEquals(1, result.size());
	}

	@Test
	void buildSequenceFromMappingThrows() {
		YamlBuilder builder = YamlBuilder.mapping().add("key", "value");
		assertThrows(IllegalStateException.class, builder::buildSequence);
	}

	@Test
	void buildWithUnclosedContextThrows() {
		YamlBuilder builder = YamlBuilder.mapping()
			.startMapping("nested")
			.add("key", "value");

		assertThrows(IllegalStateException.class, builder::build);
	}

	@Test
	void getNestingDepthAtRoot() {
		YamlBuilder builder = YamlBuilder.mapping();
		assertEquals(1, builder.getNestingDepth());
	}

	@Test
	void getNestingDepthNested() {
		YamlBuilder builder = YamlBuilder.mapping()
			.startMapping("level1");

		assertEquals(2, builder.getNestingDepth());

		builder.startMapping("level2");
		assertEquals(3, builder.getNestingDepth());
	}

	@Test
	void isInMappingContext() {
		YamlBuilder mappingBuilder = YamlBuilder.mapping();
		assertTrue(mappingBuilder.isInMappingContext());

		YamlBuilder sequenceBuilder = YamlBuilder.sequence();
		assertFalse(sequenceBuilder.isInMappingContext());
	}

	@Test
	void isInSequenceContext() {
		YamlBuilder sequenceBuilder = YamlBuilder.sequence();
		assertTrue(sequenceBuilder.isInSequenceContext());

		YamlBuilder mappingBuilder = YamlBuilder.mapping();
		assertFalse(mappingBuilder.isInSequenceContext());
	}

	@Test
	void isAtRootLevel() {
		YamlBuilder builder = YamlBuilder.mapping();
		assertTrue(builder.isAtRootLevel());

		builder.startMapping("nested");
		assertFalse(builder.isAtRootLevel());

		builder.endMapping();
		assertTrue(builder.isAtRootLevel());
	}

	@Test
	void toStringReturnsYaml() {
		YamlBuilder builder = YamlBuilder.mapping()
			.add("key", "value");

		String result = builder.toString();
		assertNotNull(result);
		assertTrue(result.contains("key"));
		assertTrue(result.contains("value"));
	}

	@Test
	void toStringWithConfig() {
		YamlBuilder builder = YamlBuilder.mapping()
			.add("key", "value");

		String result = builder.toString(YamlConfig.DEFAULT);
		assertNotNull(result);
		assertTrue(result.contains("key"));
	}

	@Test
	void toStringWithNullConfigThrows() {
		YamlBuilder builder = YamlBuilder.mapping();
		assertThrows(NullPointerException.class, () -> builder.toString(null));
	}

	@Test
	void complexMappingStructure() {
		YamlMapping result = YamlBuilder.mapping()
			.add("name", "Application Config")
			.add("version", 1.0)
			.add("enabled", true)
			.startMapping("database")
				.add("host", "localhost")
				.add("port", 5432)
				.addWithAnchor("credentials", new YamlMapping(), "dbCreds")
			.endMapping()
			.startSequence("servers")
				.add("server1")
				.add("server2")
			.endSequence()
			.buildMapping();

		assertEquals(new YamlScalar("Application Config"), result.get("name"));
		assertEquals(new YamlScalar(1.0), result.get("version"));
		assertTrue(result.get("database").isYamlMapping());
		assertTrue(result.get("servers").isYamlSequence());
	}

	@Test
	void complexSequenceStructure() {
		YamlSequence result = YamlBuilder.sequence()
			.add("simple")
			.add(42)
			.startMapping()
				.add("type", "object")
				.add("value", 100)
			.endMapping()
			.startSequence()
				.addAll("a", "b", "c")
			.endSequence()
			.buildSequence();

		assertEquals(4, result.size());
		assertEquals(new YamlScalar("simple"), result.get(0));
		assertTrue(result.get(2).isYamlMapping());
		assertTrue(result.get(3).isYamlSequence());
	}

	@Test
	void anchorAndAliasUsage() {
		YamlMapping result = YamlBuilder.mapping()
			.addWithAnchor("defaults", new YamlMapping(), "defaultSettings")
			.startMapping("production")
				.add("name", "prod")
				.addAlias("settings", "defaultSettings")
			.endMapping()
			.startMapping("development")
				.add("name", "dev")
				.addAlias("settings", "defaultSettings")
			.endMapping()
			.buildMapping();

		assertTrue(result.get("defaults").isYamlAnchor());
		YamlMapping prod = result.get("production").getAsYamlMapping();
		assertTrue(prod.get("settings").isYamlAlias());
		assertEquals("defaultSettings", prod.get("settings").getAsYamlAlias().getAnchorName());
	}

	@Test
	void methodChaining() {
		YamlMapping result = YamlBuilder.mapping()
			.add("a", "1")
			.add("b", 2)
			.add("c", true)
			.add("d", (String) null)
			.addIf(true, "e", "5")
			.addIf(false, "f", "6")
			.startMapping("nested")
				.add("g", "7")
			.endMapping()
			.startSequence("list")
				.add("h")
				.addAll("i", "j")
			.endSequence()
			.buildMapping();

		assertEquals(7, result.size());
	}

	@Test
	void emptyMapping() {
		YamlMapping result = YamlBuilder.mapping().buildMapping();
		assertNotNull(result);
		assertEquals(0, result.size());
	}

	@Test
	void emptySequence() {
		YamlSequence result = YamlBuilder.sequence().buildSequence();
		assertNotNull(result);
		assertEquals(0, result.size());
	}
}
