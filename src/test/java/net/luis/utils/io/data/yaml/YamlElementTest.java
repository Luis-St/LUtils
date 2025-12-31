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

import net.luis.utils.io.data.yaml.exception.YamlTypeException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link YamlElement} interface default methods.<br>
 *
 * @author Luis-St
 */
class YamlElementTest {
	
	@Test
	void isYamlNullOnAllTypes() {
		assertTrue(YamlNull.INSTANCE.isYamlNull());
		assertFalse(new YamlScalar("test").isYamlNull());
		assertFalse(new YamlMapping().isYamlNull());
		assertFalse(new YamlSequence().isYamlNull());
		assertFalse(new YamlAnchor("anchor", new YamlScalar("value")).isYamlNull());
		assertFalse(new YamlAlias("alias").isYamlNull());
	}
	
	@Test
	void isYamlMappingOnAllTypes() {
		assertFalse(YamlNull.INSTANCE.isYamlMapping());
		assertFalse(new YamlScalar("test").isYamlMapping());
		assertTrue(new YamlMapping().isYamlMapping());
		assertFalse(new YamlSequence().isYamlMapping());
		assertFalse(new YamlAnchor("anchor", new YamlScalar("value")).isYamlMapping());
		assertFalse(new YamlAlias("alias").isYamlMapping());
	}
	
	@Test
	void isYamlSequenceOnAllTypes() {
		assertFalse(YamlNull.INSTANCE.isYamlSequence());
		assertFalse(new YamlScalar("test").isYamlSequence());
		assertFalse(new YamlMapping().isYamlSequence());
		assertTrue(new YamlSequence().isYamlSequence());
		assertFalse(new YamlAnchor("anchor", new YamlScalar("value")).isYamlSequence());
		assertFalse(new YamlAlias("alias").isYamlSequence());
	}
	
	@Test
	void isYamlScalarOnAllTypes() {
		assertFalse(YamlNull.INSTANCE.isYamlScalar());
		assertTrue(new YamlScalar("test").isYamlScalar());
		assertFalse(new YamlMapping().isYamlScalar());
		assertFalse(new YamlSequence().isYamlScalar());
		assertFalse(new YamlAnchor("anchor", new YamlScalar("value")).isYamlScalar());
		assertFalse(new YamlAlias("alias").isYamlScalar());
	}
	
	@Test
	void isYamlAnchorOnAllTypes() {
		assertFalse(YamlNull.INSTANCE.isYamlAnchor());
		assertFalse(new YamlScalar("test").isYamlAnchor());
		assertFalse(new YamlMapping().isYamlAnchor());
		assertFalse(new YamlSequence().isYamlAnchor());
		assertTrue(new YamlAnchor("anchor", new YamlScalar("value")).isYamlAnchor());
		assertFalse(new YamlAlias("alias").isYamlAnchor());
	}
	
	@Test
	void isYamlAliasOnAllTypes() {
		assertFalse(YamlNull.INSTANCE.isYamlAlias());
		assertFalse(new YamlScalar("test").isYamlAlias());
		assertFalse(new YamlMapping().isYamlAlias());
		assertFalse(new YamlSequence().isYamlAlias());
		assertFalse(new YamlAnchor("anchor", new YamlScalar("value")).isYamlAlias());
		assertTrue(new YamlAlias("alias").isYamlAlias());
	}
	
	@Test
	void getAsYamlMappingSuccess() {
		YamlMapping mapping = new YamlMapping();
		assertSame(mapping, mapping.getAsYamlMapping());
		
		YamlAnchor anchor = new YamlAnchor("anchor", mapping);
		assertEquals(mapping, anchor.getAsYamlMapping());
	}
	
	@Test
	void getAsYamlMappingFailure() {
		assertThrows(YamlTypeException.class, () -> YamlNull.INSTANCE.getAsYamlMapping());
		assertThrows(YamlTypeException.class, () -> new YamlScalar("test").getAsYamlMapping());
		assertThrows(YamlTypeException.class, () -> new YamlSequence().getAsYamlMapping());
		assertThrows(YamlTypeException.class, () -> new YamlAlias("alias").getAsYamlMapping());
		
		YamlAnchor anchor = new YamlAnchor("anchor", new YamlScalar("value"));
		assertThrows(YamlTypeException.class, anchor::getAsYamlMapping);
	}
	
	@Test
	void getAsYamlSequenceSuccess() {
		YamlSequence sequence = new YamlSequence();
		assertSame(sequence, sequence.getAsYamlSequence());
		
		YamlAnchor anchor = new YamlAnchor("anchor", sequence);
		assertEquals(sequence, anchor.getAsYamlSequence());
	}
	
	@Test
	void getAsYamlSequenceFailure() {
		assertThrows(YamlTypeException.class, () -> YamlNull.INSTANCE.getAsYamlSequence());
		assertThrows(YamlTypeException.class, () -> new YamlScalar("test").getAsYamlSequence());
		assertThrows(YamlTypeException.class, () -> new YamlMapping().getAsYamlSequence());
		assertThrows(YamlTypeException.class, () -> new YamlAlias("alias").getAsYamlSequence());
		
		YamlAnchor anchor = new YamlAnchor("anchor", new YamlScalar("value"));
		assertThrows(YamlTypeException.class, anchor::getAsYamlSequence);
	}
	
	@Test
	void getAsYamlScalarSuccess() {
		YamlScalar scalar = new YamlScalar("test");
		assertSame(scalar, scalar.getAsYamlScalar());
		
		YamlAnchor anchor = new YamlAnchor("anchor", scalar);
		assertEquals(scalar, anchor.getAsYamlScalar());
	}
	
	@Test
	void getAsYamlScalarFailure() {
		assertThrows(YamlTypeException.class, () -> YamlNull.INSTANCE.getAsYamlScalar());
		assertThrows(YamlTypeException.class, () -> new YamlMapping().getAsYamlScalar());
		assertThrows(YamlTypeException.class, () -> new YamlSequence().getAsYamlScalar());
		assertThrows(YamlTypeException.class, () -> new YamlAlias("alias").getAsYamlScalar());
		
		YamlAnchor anchor = new YamlAnchor("anchor", new YamlMapping());
		assertThrows(YamlTypeException.class, anchor::getAsYamlScalar);
	}
	
	@Test
	void getAsYamlAnchorSuccess() {
		YamlAnchor anchor = new YamlAnchor("anchor", new YamlScalar("value"));
		assertSame(anchor, anchor.getAsYamlAnchor());
	}
	
	@Test
	void getAsYamlAnchorFailure() {
		assertThrows(YamlTypeException.class, () -> YamlNull.INSTANCE.getAsYamlAnchor());
		assertThrows(YamlTypeException.class, () -> new YamlScalar("test").getAsYamlAnchor());
		assertThrows(YamlTypeException.class, () -> new YamlMapping().getAsYamlAnchor());
		assertThrows(YamlTypeException.class, () -> new YamlSequence().getAsYamlAnchor());
		assertThrows(YamlTypeException.class, () -> new YamlAlias("alias").getAsYamlAnchor());
	}
	
	@Test
	void getAsYamlAliasSuccess() {
		YamlAlias alias = new YamlAlias("alias");
		assertSame(alias, alias.getAsYamlAlias());
	}
	
	@Test
	void getAsYamlAliasFailure() {
		assertThrows(YamlTypeException.class, () -> YamlNull.INSTANCE.getAsYamlAlias());
		assertThrows(YamlTypeException.class, () -> new YamlScalar("test").getAsYamlAlias());
		assertThrows(YamlTypeException.class, () -> new YamlMapping().getAsYamlAlias());
		assertThrows(YamlTypeException.class, () -> new YamlSequence().getAsYamlAlias());
		assertThrows(YamlTypeException.class, () -> new YamlAnchor("anchor", new YamlScalar("value")).getAsYamlAlias());
	}
	
	@Test
	void unwrapNonAnchor() {
		YamlNull yamlNull = YamlNull.INSTANCE;
		YamlScalar scalar = new YamlScalar("test");
		YamlMapping mapping = new YamlMapping();
		YamlSequence sequence = new YamlSequence();
		YamlAlias alias = new YamlAlias("alias");
		
		assertSame(yamlNull, yamlNull.unwrap());
		assertSame(scalar, scalar.unwrap());
		assertSame(mapping, mapping.unwrap());
		assertSame(sequence, sequence.unwrap());
		assertSame(alias, alias.unwrap());
	}
	
	@Test
	void unwrapAnchor() {
		YamlScalar scalar = new YamlScalar("test");
		YamlAnchor anchor = new YamlAnchor("anchor", scalar);
		
		assertEquals(scalar, anchor.unwrap());
	}
	
	@Test
	void unwrapNestedAnchor() {
		YamlMapping mapping = new YamlMapping();
		YamlAnchor inner = new YamlAnchor("inner", mapping);
		YamlAnchor outer = new YamlAnchor("outer", inner);
		
		assertEquals(mapping, outer.unwrap());
	}
	
	@Test
	void typeExceptionMessagesContainTypeNames() {
		YamlScalar scalar = new YamlScalar("test");
		
		YamlTypeException mappingException = assertThrows(YamlTypeException.class, scalar::getAsYamlMapping);
		assertTrue(mappingException.getMessage().toLowerCase().contains("yaml mapping"));
		assertTrue(mappingException.getMessage().toLowerCase().contains("yaml scalar"));
		
		YamlTypeException sequenceException = assertThrows(YamlTypeException.class, scalar::getAsYamlSequence);
		assertTrue(sequenceException.getMessage().toLowerCase().contains("yaml sequence"));
		assertTrue(sequenceException.getMessage().toLowerCase().contains("yaml scalar"));
	}
}
