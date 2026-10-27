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

package net.luis.utils.io.data.binary;

/**
 * The compression modes of the binary format.<br>
 * The compression is applied to the encoded data as a whole, it is not applied to single elements.<br>
 * <p>
 *     A compressed document stores the length of the compressed data in front of it,<br>
 *     therefore a compressed document can be read from a stream which holds more data than the document itself.
 * </p>
 * <p>
 *     The compression mode is not written to the output, only the information whether the data is compressed.<br>
 *     A reader is therefore able to read a document independently of the mode which was used to write it.
 * </p>
 *
 * @author Luis-St
 */
public enum BinaryCompression {
	
	/**
	 * The data is not compressed.<br>
	 * This is the only mode which does not require the header to be written.<br>
	 */
	NONE,
	/**
	 * The data is always compressed, even if the compressed data is larger than the uncompressed data.<br>
	 * Compression adds a constant overhead, therefore small documents grow in this mode.<br>
	 */
	DEFLATE,
	/**
	 * The data is compressed if the compressed data is smaller than the uncompressed data.<br>
	 * The output of this mode is never larger than the output of {@link #NONE}, except for the header flag.<br>
	 */
	AUTO
}
