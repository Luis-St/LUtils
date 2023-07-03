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

class LongTagTest {
	
	@Test
	void save() {
		Path path = new File(".\\LUtils\\build\\tests\\long_tag_test.txt").toPath();
		LongTag tagSave = LongTag.valueOf(12L);
		assertDoesNotThrow(() -> Tag.save(path, tagSave));
		Tag tag = Tag.load(path);
		assertInstanceOf(LongTag.class, tag);
		assertEquals(((LongTag) tag).getAsLong(), 12L);
	}
}