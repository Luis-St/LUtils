package net.luis.utils.data.tag.tags.numeric;

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

class DoubleTagTest {
	
	@Test
	void save() {
		Path path = new File(".\\LUtils\\build\\tests\\double_tag_test.txt").toPath();
		DoubleTag tagSave = DoubleTag.valueOf(12.0);
		assertDoesNotThrow(() -> Tag.save(path, tagSave));
		Tag tag = Tag.load(path);
		assertInstanceOf(DoubleTag.class, tag);
		assertEquals(((DoubleTag) tag).getAsDouble(), 12.0);
	}
}