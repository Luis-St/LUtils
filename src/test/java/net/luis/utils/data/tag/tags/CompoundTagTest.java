package net.luis.utils.data.tag.tags;

import net.luis.utils.data.tag.Tag;
import net.luis.utils.data.tag.tags.collection.ListTag;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Luis-St
 *
 */

class CompoundTagTest {
	
	@Test
	void save() {
		Path path = new File("./build/tests/compound_tag_test.txt").toPath();
		CompoundTag tagSave = new CompoundTag();
		tagSave.putBoolean("Boolean", true);
		tagSave.putInt("Int", 14);
		tagSave.putLong("Long", 15L);
		tagSave.putDouble("Double", 17.0);
		tagSave.putIntArray("IntArray", new int[]{4, 5, 6, 7});
		tagSave.putLongArray("LongArray", new long[]{8, 9, 10, 11});
		tagSave.putString("String", "String");
		tagSave.putList("List", new ListTag());
		tagSave.putCompound("Compound", new CompoundTag());
		assertDoesNotThrow(() -> Tag.save(path, tagSave));
		Tag tag = Tag.load(path);
		assertInstanceOf(CompoundTag.class, tag);
		if (tag instanceof CompoundTag tagLoad) {
			assertEquals(tagLoad.size(), 9);
			assertTrue(tagLoad.getBoolean("Boolean"));
			assertEquals(tagLoad.getInt("Int"), 14);
			assertEquals(tagLoad.getLong("Long"), 15L);
			assertEquals(tagLoad.getDouble("Double"), 17.0);
			assertArrayEquals(tagLoad.getIntArray("IntArray"), new int[]{4, 5, 6, 7});
			assertArrayEquals(tagLoad.getLongArray("LongArray"), new long[]{8, 9, 10, 11});
			assertEquals(tagLoad.getString("String"), "String");
			assertTrue(tagLoad.getList("List").isEmpty());
			assertTrue(tagLoad.getCompound("Compound").isEmpty());
		}
	}
}