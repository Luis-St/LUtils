package net.luis.utils.data.tag.tags.collection.array;

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

class IntArrayTagTest {
	
	@Test
	void save() {
		Path path = new File("D:\\Programmieren\\Git Repositories\\LUtils\\build\\tests\\int_array_tag_test.txt").toPath();
		IntArrayTag tagSave = new IntArrayTag();
		tagSave.add(IntTag.valueOf(12));
		tagSave.add(IntTag.valueOf(13));
		tagSave.add(IntTag.valueOf(14));
		assertDoesNotThrow(() -> Tag.save(path, tagSave));
		Tag tag = Tag.load(path);
		assertInstanceOf(IntArrayTag.class, tag);
		if (tag instanceof IntArrayTag tagLoad) {
			assertEquals(tagLoad.size(), 3);
			assertEquals(tagLoad.getElementType(), Tag.INT_TAG);
			assertEquals(tagLoad.get(0).getAsInt(), 12);
			assertEquals(tagLoad.get(1).getAsInt(), 13);
			assertEquals(tagLoad.get(2).getAsInt(), 14);
		}
	}
}