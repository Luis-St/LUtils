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
	
	Logger LOGGER = LogManager.getLogger(Tag.class);
	
	byte END_TAG = 0;
	byte INT_TAG = 1;
	byte LONG_TAG = 2;
	@Deprecated byte FLOAT_TAG = 3;
	byte DOUBLE_TAG = 4;
	byte STRING_TAG = 5;
	byte INT_ARRAY_TAG = 6;
	byte LONG_ARRAY_TAG = 7;
	byte LIST_TAG = 8;
	byte COMPOUND_TAG = 9;
	byte PRIMITIVE_TAG = 99;
	
	static @NotNull Tag load(@NotNull Path path) throws LoadTagException {
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
	
	static void save(@NotNull Path path, @NotNull Tag tag) throws SaveTagException {
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
	
	void save(@NotNull DataOutput output) throws SaveTagException;
	
	@NotNull String toString();
	
	byte getId();
	
	@NotNull TagType<?> getType();
	
	@NotNull Tag copy();
	
	void accept(@NotNull TagVisitor visitor);
	
	default @NotNull String getAsString() {
		return new StringTagVisitor().visit(this);
	}
	
}
