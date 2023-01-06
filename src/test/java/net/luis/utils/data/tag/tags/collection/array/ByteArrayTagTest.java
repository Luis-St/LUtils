package net.luis.utils.data.tag.tags.collection.array;

import net.luis.utils.data.tag.Tag;
import net.luis.utils.data.tag.tags.numeric.ByteTag;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Luis-st
 *
 */

class ByteArrayTagTest {
	
	@Test
	void save() {
		Path path = new File("D:\\Programmieren\\Git Repositories\\LUtils\\build\\tests\\byte_array_tag_test.txt").toPath();
		ByteArrayTag tagSave = new ByteArrayTag();
		tagSave.add(ByteTag.valueOf((byte) 12));
		tagSave.add(ByteTag.valueOf((byte) 13));
		tagSave.add(ByteTag.valueOf((byte) 14));
		assertDoesNotThrow(() -> Tag.save(path, tagSave));
		Tag tag = Tag.load(path);
		assertInstanceOf(ByteArrayTag.class, tag);
		if (tag instanceof ByteArrayTag tagLoad) {
			assertEquals(tagLoad.size(), 3);
			assertEquals(tagLoad.getElementType(), Tag.BYTE_TAG);
			assertEquals(tagLoad.get(0).getAsByte(), (byte) 12);
			assertEquals(tagLoad.get(1).getAsByte(), (byte) 13);
			assertEquals(tagLoad.get(2).getAsByte(), (byte) 14);
		}
	}
}