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

package net.luis.utils.io.database.type;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.Codecs;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.*;
import java.time.*;
import java.util.UUID;

/**
 *
 * @author Luis-St
 *
 */

public final class SqlDataTypes {
	
	public static final Codec<Boolean> BOOLEAN = Codecs.BOOLEAN;
	public static final Codec<Byte> BYTE = Codecs.BYTE;
	public static final Codec<Short> SHORT = Codecs.SHORT;
	public static final Codec<Integer> INTEGER = Codecs.INTEGER;
	public static final Codec<Long> LONG = Codecs.LONG;
	public static final Codec<Float> FLOAT = Codecs.FLOAT;
	public static final Codec<Double> DOUBLE = Codecs.DOUBLE;
	public static final Codec<BigDecimal> BIG_DECIMAL = Codecs.BIG_DECIMAL;
	
	public static final Codec<String> FIXED_STRING = Codecs.STRING.length(l -> l.maxLength(255));
	public static final Codec<String> STRING = Codecs.STRING.length(l -> l.maxLength(65535));
	public static final Codec<String> TEXT = Codecs.STRING;
	public static final Codec<Clob> CLOB = new SqlCodec<>("sql clob", SqlTypeProvider::getClob);
	public static final Codec<Reader> CHARACTER_STREAM = new SqlCodec<>("sql character stream", SqlTypeProvider::getCharacterStream);
	public static final Codec<NClob> NCLOB = new SqlCodec<>("sql nclob", SqlTypeProvider::getNClob);
	public static final Codec<Reader> N_CHARACTER_STREAM = new SqlCodec<>("sql n character stream", SqlTypeProvider::getNCharacterStream);
	
	public static final Codec<byte[]> BYTES = new SqlCodec<>("sql bytes", SqlTypeProvider::getBytes);
	public static final Codec<Blob> BLOB = new SqlCodec<>("sql blob", SqlTypeProvider::getBlob);
	public static final Codec<InputStream> BINARY_STREAM = new SqlCodec<>("sql binary stream", SqlTypeProvider::getBinaryStream);
	
	public static final Codec<LocalDate> LOCAL_DATE = Codecs.LOCAL_DATE;
	public static final Codec<LocalTime> LOCAL_TIME = Codecs.LOCAL_TIME;
	public static final Codec<LocalDateTime> LOCAL_DATE_TIME = Codecs.LOCAL_DATE_TIME;
	public static final Codec<OffsetTime> OFFSET_TIME = Codecs.OFFSET_TIME;
	public static final Codec<OffsetDateTime> OFFSET_DATE_TIME = Codecs.OFFSET_DATE_TIME;
//	public static final Codec<Instant> INSTANT = Codecs.INSTANT;
	
	public static final Codec<UUID> UUID = Codecs.UUID;
	public static final Codec<Character> CHARACTER = Codecs.CHARACTER;
	
	private SqlDataTypes() {}
}
