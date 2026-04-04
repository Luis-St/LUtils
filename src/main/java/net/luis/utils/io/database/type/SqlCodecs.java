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

import net.luis.utils.io.codec.Codecs;
import net.luis.utils.io.codec.decoder.DecoderException;

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

public final class SqlCodecs {
	
	public static final SqlCodec<Boolean> BOOLEAN = new WrappedSqlCodec<>("sql boolean", Codecs.BOOLEAN);
	public static final SqlCodec<Byte> BYTE = new WrappedSqlCodec<>("sql byte", Codecs.BYTE);
	public static final SqlCodec<Short> SHORT = new WrappedSqlCodec<>("sql short", Codecs.SHORT);
	public static final SqlCodec<Integer> INTEGER = new WrappedSqlCodec<>("sql integer", Codecs.INTEGER);
	public static final SqlCodec<Long> LONG = new WrappedSqlCodec<>("sql long", Codecs.LONG);
	public static final SqlCodec<Float> FLOAT = new WrappedSqlCodec<>("sql float", Codecs.FLOAT);
	public static final SqlCodec<Double> DOUBLE = new WrappedSqlCodec<>("sql double", Codecs.DOUBLE);
	public static final SqlCodec<BigDecimal> BIG_DECIMAL = new WrappedSqlCodec<>("sql big decimal", Codecs.BIG_DECIMAL);
	
	public static final SqlCodec<String> FIXED_STRING = new WrappedSqlCodec<>("sql fixed string", Codecs.STRING.length(l -> l.maxLength(255)));
	public static final SqlCodec<String> STRING = new WrappedSqlCodec<>("sql string", Codecs.STRING.length(l -> l.maxLength(65535)));
	public static final SqlCodec<String> TEXT = new WrappedSqlCodec<>("sql text", Codecs.STRING);
	public static final SqlCodec<Clob> CLOB = new DefaultSqlCodec<>("sql clob", SqlTypeProvider::getClob, SqlTypeProvider::createClob);
	public static final SqlCodec<Reader> CHARACTER_STREAM = new DefaultSqlCodec<>("sql character stream", SqlTypeProvider::getCharacterStream, SqlTypeProvider::createCharacterStream);
	public static final SqlCodec<NClob> NCLOB = new DefaultSqlCodec<>("sql nclob", SqlTypeProvider::getNClob, SqlTypeProvider::createNClob);
	public static final SqlCodec<Reader> N_CHARACTER_STREAM = new DefaultSqlCodec<>("sql n character stream", SqlTypeProvider::getNCharacterStream, SqlTypeProvider::createNCharacterStream);
	
	public static final SqlCodec<byte[]> BYTES = new DefaultSqlCodec<>("sql bytes", SqlTypeProvider::getBytes, SqlTypeProvider::createBytes);
	public static final SqlCodec<Blob> BLOB = new DefaultSqlCodec<>("sql blob", SqlTypeProvider::getBlob, SqlTypeProvider::createBlob);
	public static final SqlCodec<InputStream> BINARY_STREAM = new DefaultSqlCodec<>("sql binary stream", SqlTypeProvider::getBinaryStream, SqlTypeProvider::createBinaryStream);
	
	public static final SqlCodec<LocalDate> LOCAL_DATE = new WrappedSqlCodec<>("sql local date", Codecs.LOCAL_DATE);
	public static final SqlCodec<LocalTime> LOCAL_TIME = new WrappedSqlCodec<>("sql local time", Codecs.LOCAL_TIME);
	public static final SqlCodec<LocalDateTime> LOCAL_DATE_TIME = new WrappedSqlCodec<>("sql local date time", Codecs.LOCAL_DATE_TIME);
	public static final SqlCodec<OffsetTime> OFFSET_TIME = new WrappedSqlCodec<>("sql offset time", Codecs.OFFSET_TIME);
	public static final SqlCodec<OffsetDateTime> OFFSET_DATE_TIME = new WrappedSqlCodec<>("sql offset date time", Codecs.OFFSET_DATE_TIME);
	public static final SqlCodec<Instant> INSTANT = new WrappedSqlCodec<>("sql instant", OFFSET_DATE_TIME.map(instant -> OffsetDateTime.ofInstant(instant, ZoneOffset.UTC), either -> {
		if (either.isLeft()) {
			return either.leftOrThrow().toInstant();
		} else {
			throw new DecoderException("Decoding mapper (OffsetDateTime -> Instant) could not be applied due to previous error");
		}
	}));
	
	public static final SqlCodec<UUID> UUID = new WrappedSqlCodec<>("sql uuid", Codecs.UUID);
	
	private SqlCodecs() {}
}
