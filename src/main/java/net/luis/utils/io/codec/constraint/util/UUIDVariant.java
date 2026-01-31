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

package net.luis.utils.io.codec.constraint.util;

import org.jspecify.annotations.NonNull;

import java.util.Objects;
import java.util.UUID;

/**
 * Enumeration of UUID variant types as defined in RFC 4122.<br>
 * <p>
 *     UUID variants determine the layout of the UUID and are encoded in the high bits of the clock_seq_hi_and_reserved field.<br>
 *     Different variants have different bit patterns and are used for different purposes.
 * </p>
 *
 * @author Luis-St
 *
 */

public enum UUIDVariant {
	
	/**
	 * NCS backward compatibility variant.<br>
	 * <p>
	 *     Reserved for NCS (Apollo Network Computing System) backward compatibility.<br>
	 *     The high bit of clock_seq_hi_and_reserved is 0.
	 * </p>
	 */
	NFC,
	
	/**
	 * RFC 4122 variant (Leach-Salz).<br>
	 * <p>
	 *     The standard UUID variant as defined in RFC 4122.<br>
	 *     The two high bits of clock_seq_hi_and_reserved are 10.
	 * </p>
	 */
	RFC_4122,
	
	/**
	 * Microsoft GUID variant.<br>
	 * <p>
	 *     Reserved for Microsoft Corporation backward compatibility.<br>
	 *     The three high bits of clock_seq_hi_and_reserved are 110.
	 * </p>
	 */
	MICROSOFT,
	
	/**
	 * Reserved for future definition.<br>
	 * <p>
	 *     Reserved for future UUID variant definitions.<br>
	 *     The three high bits of clock_seq_hi_and_reserved are 111.
	 * </p>
	 */
	RESERVED;
	
	/**
	 * Gets the variant type from the given UUID instance.<br>
	 *
	 * @param uuid The UUID instance
	 * @return The corresponding UUID variant
	 * @throws NullPointerException If the UUID is null
	 * @throws IllegalArgumentException If the UUID has an unknown variant
	 */
	public static @NonNull UUIDVariant from(@NonNull UUID uuid) {
		Objects.requireNonNull(uuid, "UUID must not be null");
		
		return switch (uuid.variant()) {
			case 0 -> NFC;
			case 2 -> RFC_4122;
			case 6 -> MICROSOFT;
			case 7 -> RESERVED;
			default -> throw new IllegalArgumentException("Unknown UUID variant: " + uuid.variant());
		};
	}
}
