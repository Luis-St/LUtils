/*
 * LUtils
 * Copyright (C) 2026 Luis Staudt
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package net.luis.utils.io.network.mail.message;

import org.jspecify.annotations.NonNull;

import java.util.Arrays;
import java.util.Objects;

/**
 * A file or binary attachment, rendered as a Base64 encoded MIME part with a {@code Content-Disposition: attachment} header.<br>
 * The raw data is stored by reference and not copied, so callers should not mutate it afterwards.<br>
 * <p>
 *     Example usage:
 * </p>
 * <pre>{@code
 * byte[] pdf = Files.readAllBytes(Path.of("report.pdf"));
 * Attachment attachment = Attachment.of("report.pdf", "application/pdf", pdf);
 * }</pre>
 *
 * @see MailContent
 *
 * @author Luis-St
 *
 * @param fileName The file name of the attachment
 * @param contentType The MIME content type of the attachment
 * @param data The raw attachment data
 */
public record MailAttachment(@NonNull String fileName, @NonNull String contentType, byte @NonNull [] data) implements MailContent {
	
	/**
	 * The default content type used when none is specified.<br>
	 */
	public static final String DEFAULT_CONTENT_TYPE = "application/octet-stream";
	
	/**
	 * Constructs a new attachment with the given file name, content type, and data.<br>
	 *
	 * @param fileName The file name of the attachment
	 * @param contentType The MIME content type of the attachment
	 * @param data The raw attachment data
	 * @throws NullPointerException If the file name, content type, or data is null
	 * @throws IllegalArgumentException If the file name or content type is empty, or the file name contains an illegal character
	 */
	public MailAttachment {
		Objects.requireNonNull(fileName, "File name must not be null");
		Objects.requireNonNull(contentType, "Content type must not be null");
		Objects.requireNonNull(data, "Data must not be null");
		
		if (fileName.isEmpty()) {
			throw new IllegalArgumentException("File name must not be empty");
		}
		if (contentType.isEmpty()) {
			throw new IllegalArgumentException("Content type must not be empty");
		}
		if (fileName.indexOf('\r') >= 0 || fileName.indexOf('\n') >= 0 || fileName.indexOf('"') >= 0) {
			throw new IllegalArgumentException("File name contains an illegal character: " + fileName);
		}
	}
	
	/**
	 * Creates a new attachment using the default {@code application/octet-stream} content type.<br>
	 *
	 * @param fileName The file name of the attachment
	 * @param data The raw attachment data
	 * @return A new attachment
	 * @throws NullPointerException If the file name or data is null
	 * @throws IllegalArgumentException If the file name is empty or contains an illegal character
	 */
	public static @NonNull MailAttachment of(@NonNull String fileName, byte @NonNull [] data) {
		return new MailAttachment(fileName, DEFAULT_CONTENT_TYPE, data);
	}
	
	/**
	 * Creates a new attachment with the given file name, content type, and data.<br>
	 *
	 * @param fileName The file name of the attachment
	 * @param contentType The MIME content type of the attachment
	 * @param data The raw attachment data
	 * @return A new attachment
	 * @throws NullPointerException If the file name, content type, or data is null
	 * @throws IllegalArgumentException If the file name or content type is empty, or the file name contains an illegal character
	 */
	public static @NonNull MailAttachment of(@NonNull String fileName, @NonNull String contentType, byte @NonNull [] data) {
		return new MailAttachment(fileName, contentType, data);
	}
	
	//region Object overrides
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof MailAttachment that)) return false;
		
		if (!this.fileName.equals(that.fileName)) return false;
		if (!this.contentType.equals(that.contentType)) return false;
		return Arrays.equals(this.data, that.data);
	}
	
	@Override
	public int hashCode() {
		int result = Objects.hash(this.fileName, this.contentType);
		result = 31 * result + Arrays.hashCode(this.data);
		return result;
	}
	
	@Override
	public @NonNull String toString() {
		return "Attachment[fileName=" + this.fileName + ", contentType=" + this.contentType + ", data=" + this.data.length + " bytes]";
	}
	//endregion
}
