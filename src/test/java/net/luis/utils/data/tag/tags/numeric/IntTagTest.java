package net.luis.utils.data.tag.tags.numeric;

import net.luis.utils.data.tag.Tag;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Luis-st
 *
 */

class IntTagTest {
	
	@Test
	void save() {
		Path path = new File("D:\\Programmieren\\Git Repositories\\LUtils\\build\\tests\\int_tag_test.txt").toPath();
		IntTag tagSave = IntTag.valueOf(12);
		assertDoesNotThrow(() -> Tag.save(path, tagSave));
		Tag tag = Tag.load(path);
		assertInstanceOf(IntTag.class, tag);
		assertEquals(((IntTag) tag).getAsInt(), 12);
	}
}