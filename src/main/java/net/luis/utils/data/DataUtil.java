package net.luis.utils.data;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;

/**
 *
 * @author Luis-st
 *
 */

public class DataUtil {
	
	public static DataInputStream inputStream(Path path) throws IOException {
		return new DataInputStream(new BufferedInputStream(new FileInputStream(path.toFile())));
	}
	
	public static DataOutputStream outputStream(Path path) throws IOException {
		return new DataOutputStream(new BufferedOutputStream(new FileOutputStream(path.toFile())));
	}
	
}
