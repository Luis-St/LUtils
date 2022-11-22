package net.luis.utils.data.tag;

import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.luis.utils.data.DataUtil;
import net.luis.utils.data.tag.tags.EndTag;
import net.luis.utils.data.tag.visitor.StringTagVisitor;
import net.luis.utils.data.tag.visitor.TagVisitor;

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
	byte BYTE_ARRAY_TAG = 8;
	byte INT_ARRAY_TAG = 9;
	byte LONG_ARRAY_TAG = 10;
	byte LIST_TAG = 11;
	byte COMPOUND_TAG = 12;
	byte PRIMITIVE_TAG = 99;
	
	void save(DataOutput output) throws IOException;
	
	String toString();
	
	byte getId();
	
	TagType<?> getType();
	
	Tag copy();
	
	void accept(TagVisitor visitor);
	
	default String getAsString() {
		return new StringTagVisitor().visit(this);
	}
	
	static Tag load(Path path) throws IOException {
		if (!Files.exists(path)) {
			LOGGER.warn("Fail to load tag from {}, since the file does not exists", path);
			return EndTag.INSTANCE;
		}
		DataInputStream input = DataUtil.inputStream(path);
		byte type = input.readByte();
		TagType<?> tagType = TagTypes.getType(type);
		Tag tag = tagType.load(input);
		input.close();
		return tag;
	}
	
	static void save(Path path, Tag tag) throws IOException {
		if (!Files.exists(path)) {
			Files.createDirectories(path.getParent());
			Files.createFile(path);
		}
		DataOutputStream output = DataUtil.outputStream(path);
		output.writeByte(tag.getId());
		tag.save(output);
		output.flush();
		output.close();
	}
	
}
