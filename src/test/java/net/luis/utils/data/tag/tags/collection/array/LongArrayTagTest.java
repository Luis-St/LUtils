package net.luis.utils.data.tag.tags.collection.array;

import net.luis.utils.data.tag.Tag;
import net.luis.utils.data.tag.tags.numeric.LongTag;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Luis-St
 *
 */

class LongArrayTagTest {
	
	@Test
	void save() {
		Path path = new File("D:\\Programmieren\\Git Repositories\\LUtils\\build\\tests\\long_array_tag_test.txt").toPath();
		LongArrayTag tagSave = new LongArrayTag();
		tagSave.add(LongTag.valueOf(12L));
		tagSave.add(LongTag.valueOf(13L));
		tagSave.add(LongTag.valueOf(14L));
		assertDoesNotThrow(() -> Tag.save(path, tagSave));
		Tag tag = Tag.load(path);
		assertInstanceOf(LongArrayTag.class, tag);
		if (tag instanceof LongArrayTag tagLoad) {
			assertEquals(tagLoad.size(), 3);
			assertEquals(tagLoad.getElementType(), Tag.LONG_TAG);
			assertEquals(tagLoad.get(0).getAsLong(), 12);
			assertEquals(tagLoad.get(1).getAsLong(), 13);
			assertEquals(tagLoad.get(2).getAsLong(), 14);
		}
	}
}