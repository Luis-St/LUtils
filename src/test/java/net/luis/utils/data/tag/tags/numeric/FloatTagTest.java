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

class FloatTagTest {
	
	@Test
	void save() {
		Path path = new File("D:\\Programmieren\\Git Repositories\\LUtils\\build\\tests\\float_tag_test.txt").toPath();
		FloatTag tagSave = FloatTag.valueOf(12.0F);
		assertDoesNotThrow(() -> Tag.save(path, tagSave));
		Tag tag = Tag.load(path);
		assertInstanceOf(FloatTag.class, tag);
		assertEquals(((FloatTag) tag).getAsDouble(), 12.0F);
	}
}