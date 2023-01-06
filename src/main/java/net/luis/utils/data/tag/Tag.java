package net.luis.utils.data.tag;

import net.luis.utils.data.tag.exception.LoadTagException;
import net.luis.utils.data.tag.exception.SaveTagException;
import net.luis.utils.data.tag.tags.EndTag;
import net.luis.utils.data.tag.visitor.StringTagVisitor;
import net.luis.utils.data.tag.visitor.TagVisitor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 *
 * @author Luis-st
 *
 */

public interface Tag {
	
	Logger LOGGER = LogManager.getLogger();
	
	byte END_TAG = 0;
	byte BYTE_TAG = 1;
	byte SHORT_TAG = 2;
	byte INT_TAG = 3;
	byte LONG_TAG = 4;
	byte FLOAT_TAG = 5;
	byte DOUBLE_TAG = 6;
	byte STRING_TAG = 7;
	byte CRYPT_STRING_TAG = 8;
	byte BYTE_ARRAY_TAG = 9;
	byte INT_ARRAY_TAG = 10;
	byte LONG_ARRAY_TAG = 11;
	byte LIST_TAG = 12;
	byte COMPOUND_TAG = 13;
	byte PRIMITIVE_TAG = 99;
	
	static Tag load(Path path) throws LoadTagException {
		if (!Files.exists(path)) {
			LOGGER.warn("Tag from file {} cannot be loaded because the file does not exist", path);
			return EndTag.INSTANCE;
		}
		try (DataInputStream input = new DataInputStream(new BufferedInputStream(new FileInputStream(path.toFile())))) {
			byte type = input.readByte();
			TagType<?> tagType = TagTypes.getType(type);
			return tagType.load(input);
		} catch (IOException e) {
			throw new LoadTagException(path, e);
		}
	}
	
	static void save(Path path, Tag tag) throws SaveTagException {
		if (!Files.exists(path)) {
			try {
				Files.createDirectories(path.getParent());
				Files.createFile(path);
			} catch (IOException e) {
				throw new SaveTagException(tag, path, e);
			}
		}
		try (DataOutputStream output = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(path.toFile())))) {
			output.writeByte(tag.getId());
			tag.save(output);
			output.flush();
		} catch (IOException e) {
			throw new SaveTagException(tag, path, e);
		}
	}
	
	void save(DataOutput output) throws SaveTagException;
	
	String toString();
	
	byte getId();
	
	@NotNull
	TagType<?> getType();
	
	@NotNull
	Tag copy();
	
	void accept(TagVisitor visitor);
	
	@NotNull
	default String getAsString() {
		return new StringTagVisitor().visit(this);
	}
	
}
