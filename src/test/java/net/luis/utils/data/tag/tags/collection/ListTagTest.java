package net.luis.utils.data.tag.tags.collection;

import net.luis.utils.data.tag.Tag;
import net.luis.utils.data.tag.tags.numeric.IntTag;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Luis-St
 *
 */

class ListTagTest {
	
	@Test
	void save() {
		Path path = new File("D:\\Programmieren\\Git Repositories\\LUtils\\build\\tests\\list_tag_test.txt").toPath();
		ListTag tagSave = new ListTag();
		tagSave.add(IntTag.valueOf(12));
		tagSave.add(IntTag.valueOf(13));
		tagSave.add(IntTag.valueOf(14));
		assertDoesNotThrow(() -> Tag.save(path, tagSave));
		Tag tag = Tag.load(path);
		assertInstanceOf(ListTag.class, tag);
		if (tag instanceof ListTag tagLoad) {
			assertEquals(tagLoad.size(), 3);
			assertEquals(tagLoad.getElementType(), Tag.INT_TAG);
			assertEquals(tagLoad.getInt(0), 12);
			assertEquals(tagLoad.getInt(1), 13);
			assertEquals(tagLoad.getInt(2), 14);
		}
	}
}