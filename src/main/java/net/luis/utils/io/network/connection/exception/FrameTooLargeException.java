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

package net.luis.utils.io.network.connection.exception;

import java.io.IOException;
import java.io.Serial;

/**
 * Thrown by {@code NetworkUtils.readFrame} when a received frame declares a length that exceeds<br>
 * the maximum number of bytes the receiver is willing to accept.<br>
 *
 * @author Luis-St
 */
public class FrameTooLargeException extends IOException {
	
	@Serial
	private static final long serialVersionUID = 1L;
	
	/**
	 * The declared length of the frame that was rejected.<br>
	 */
	private final int frameLength;
	/**
	 * The maximum payload length that was accepted.<br>
	 */
	private final int maxBytes;
	
	/**
	 * Constructs a new frame too large exception for the given declared and maximum lengths.<br>
	 *
	 * @param frameLength The declared length of the rejected frame
	 * @param maxBytes The maximum payload length that was accepted
	 */
	public FrameTooLargeException(int frameLength, int maxBytes) {
		super("Message size " + frameLength + " exceeds buffer size " + maxBytes);
		this.frameLength = frameLength;
		this.maxBytes = maxBytes;
	}
	
	/**
	 * Returns the declared length of the frame that was rejected.<br>
	 * @return The frame length
	 */
	public int frameLength() {
		return this.frameLength;
	}
	
	/**
	 * Returns the maximum payload length that was accepted.<br>
	 * @return The maximum accepted length
	 */
	public int maxBytes() {
		return this.maxBytes;
	}
}
