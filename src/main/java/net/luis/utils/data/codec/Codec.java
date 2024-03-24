/*
 * LUtils
 * Copyright (C) 2024 Luis Staudt
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

package net.luis.utils.data.codec;

import com.google.common.collect.Table;
import net.luis.utils.data.codec.exception.DecoderException;
import net.luis.utils.data.codec.io.FileCodec;
import net.luis.utils.data.codec.nested.*;
import net.luis.utils.data.codec.number.*;
import net.luis.utils.data.codec.primitive.*;
import net.luis.utils.data.codec.util.RangeCodec;
import net.luis.utils.data.codec.util.UUIDCodec;
import net.luis.utils.util.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

/**
 *
 * @author Luis-St
 *
 */

public interface Codec<T> {
	
	static final Codec<Boolean> BOOLEAN = new BooleanCodec();
	static final Codec<Byte> BYTE = new ByteCodec();
	static final Codec<Short> SHORT = new ShortCodec();
	static final Codec<Integer> INT = new IntegerCodec();
	static final Codec<Long> LONG = new LongCodec();
	static final Codec<BigInteger> BIG_INTEGER = new BigIntegerCodec();
	
	static final Codec<Float> FLOAT = new FloatCodec();
	static final Codec<Double> DOUBLE = new DoubleCodec();
	static final Codec<BigDecimal> BIG_DECIMAL = new BigDecimalCodec();
	
	static final Codec<Character> CHAR = new CharacterCodec();
	static final Codec<String> STRING = new StringCodec();
	
	static final Codec<File> FILE = new FileCodec();
	
	static final Codec<Range> RANGE = new RangeCodec();
	static final Codec<UUID> UUID = new UUIDCodec();
	
	
	//region Factory methods
	static <K, V> @NotNull Codec<Map<K, V>> mapOf(@NotNull Codec<K> key, @NotNull Codec<V> value) {
		return new MapCodec<>(key, value);
	}
	
	static <F, S> @NotNull Codec<Pair<F, S>> pairOf(@NotNull Codec<F> first, @NotNull Codec<S> second) {
		return new PairCodec<>(first, second);
	}
	
	static <L, R> @NotNull Codec<Either<L, R>> eitherOf(@NotNull Codec<L> left, @NotNull Codec<R> right) {
		return new EitherCodec<>(left, right);
	}
	
	static <R, C, V> @NotNull Codec<Table<R, C, V>> tableOf(@NotNull Codec<R> row, @NotNull Codec<C> column, @NotNull Codec<V> value) {
		return new TableCodec<>(row, column, value);
	}
	//endregion
	
	<X> @NotNull ParserCache<X> isValid(@Nullable String value);
	
	default <X> @NotNull T decodeDirect(@Nullable String value) {
		ParserCache<X> cache = this.isValid(value);
		if (cache.isValid()) {
			return this.decode(cache);
		} else {
			throw new DecoderException("Unable to decode value '" + value + "' with codec '" + this.getClass().getSimpleName() + "' because it is invalid: " + cache);
		}
	}
	
	default <X> @NotNull T decode(@NotNull ParserCache<X> cache) {
		Objects.requireNonNull(cache, "Parser cache must not be null");
		if (!cache.isValid()) {
			throw new DecoderException("Invalid cache provided: " + cache);
		}
		return cache.cast();
	}
	
	@NotNull String encode(@NotNull T value);
	
	@NotNull T getDefaultValue();
	
	default boolean isNested() {
		return false;
	}
	
	//region Wrapper methods
	default @NotNull Codec<Optional<T>> optional() {
		return new OptionalCodec<>(this);
	}
	
	default @NotNull Codec<List<T>> asList() {
		return new ListCodec<>(this);
	}
	
	default @NotNull Codec<Set<T>> asSet() {
		return new SetCodec<>(this);
	}
	
	default <V> @NotNull Codec<Map<T, V>> asKeyOfMap(@NotNull Codec<V> codec) {
		return mapOf(this, codec);
	}
	
	default <K> @NotNull Codec<Map<K, T>> asValueOfMap(@NotNull Codec<K> codec) {
		return mapOf(codec, this);
	}
	//endregion
}
