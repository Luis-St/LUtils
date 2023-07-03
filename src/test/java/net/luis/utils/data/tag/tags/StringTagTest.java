package net.luis.utils.data.tag.tags;

import net.luis.utils.data.tag.Tag;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Luis-St
 *
 */

class StringTagTest {
	
	@Test
	void save() {
		Path path = new File(".\\LUtils\\build\\tests\\string_tag_test.txt").toPath();
		StringTag tagSave = StringTag.valueOf("StringTagTest");
		assertDoesNotThrow(() -> Tag.save(path, tagSave));
		Tag tag = Tag.load(path);
		assertInstanceOf(StringTag.class, tag);
		assertEquals(tag.getAsString(), "StringTagTest");
	}
}