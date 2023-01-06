package net.luis.utils.data.tag.tags;

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

class CryptStringTagTest {
	
	@Test
	void save() {
		Path path = new File("D:\\Programmieren\\Git Repositories\\LUtils\\build\\tests\\crypt_string_tag_test.txt").toPath();
		CryptStringTag tagSave = CryptStringTag.valueOf("CryptStringTagTest");
		assertDoesNotThrow(() -> Tag.save(path, tagSave));
		Tag tag = Tag.load(path);
		assertInstanceOf(CryptStringTag.class, tag);
		assertEquals(tag.getAsString(), "CryptStringTagTest");
	}
}