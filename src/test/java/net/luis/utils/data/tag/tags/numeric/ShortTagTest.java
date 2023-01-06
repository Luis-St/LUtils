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

class ShortTagTest {
	
	@Test
	void save() {
		Path path = new File("D:\\Programmieren\\Git Repositories\\LUtils\\build\\tests\\short_tag_test.txt").toPath();
		ShortTag tagSave = ShortTag.valueOf((short) 12);
		assertDoesNotThrow(() -> Tag.save(path, tagSave));
		Tag tag = Tag.load(path);
		assertInstanceOf(ShortTag.class, tag);
		assertEquals(((ShortTag) tag).getAsShort(), (short) 12);
	}
}