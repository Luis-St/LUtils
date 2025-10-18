/*
 * LUtils
 * Copyright (C) 2025 Luis Staudt
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

package net.luis.utils.io.codec;

import net.luis.utils.io.codec.function.*;
import net.luis.utils.io.codec.mapping.CodecAutoMapping;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

/**
 * A utility class that provides methods to build codecs for complex objects.<br>
 *
 * @author Luis-St
 */
public final class CodecBuilder {
	
	/**
	 * Private constructor to prevent instantiation.<br>
	 * This is a static helper class.<br>
	 */
	private CodecBuilder() {}
	
	/**
	 * Creates an automatically mapped codec for the given class.<br>
	 * The underlying auto-mapping system analyzes the class structure and creates a codec that can encode and decode instances of the class.<br>
	 * The auto-mapping supports record classes (preferred), regular classes, and enums.<br>
	 *
	 * @param clazz The class for which to create a codec
	 * @param <O> The type of the class
	 * @return A codec for the given class
	 * @throws NullPointerException If the provided class is null
	 * @throws IllegalArgumentException If the class is an interface, annotation, primitive type, or has invalid structure
	 * @see CodecAutoMapping#createAutoMappedCodec(Class)
	 */
	public static @NotNull <O> Codec<O> autoMapCodec(@NotNull Class<O> clazz) {
		return CodecAutoMapping.createAutoMappedCodec(clazz);
	}
	
	/**
	 * Creates a codec creator containing the provided codec.<br>
	 * The codec creator can be used to create the resulting codec by calling the {@link CodecCreator#create(CodecBuilderFunction)} method of the returned creator.<br>
	 * The {@code create} requires a grouping function as input that constructs the resulting object from the provided components.<br>
	 *
	 * @param codec1 The first codec
	 * @return The created codec grouper
	 * @param <CI1> The type of the first component
	 * @param <O> The type of the resulting object the created codec will produce
	 * @throws NullPointerException If any of the provided codecs is null
	 * @see CodecCreator
	 * @see CodecGroup
	 * @see CodecBuilderFunction1
	 */
	public static <CI1, O> @NotNull CodecCreator<O, CodecBuilderFunction1<CI1, O>> group(
		@NotNull ConfiguredCodec<CI1, O> codec1
	) {
		List<ConfiguredCodec<?, O>> codecs = List.of(
			codec1
		);
		
		ensureNoElementIsNull(codecs);
		return new CodecCreator<>(codecs);
	}
	
	/**
	 * Creates a codec creator containing the provided codec.<br>
	 * The codec creator can be used to create the resulting codec by calling the {@link CodecCreator#create(CodecBuilderFunction)} method of the returned creator.<br>
	 * The {@code create} requires a grouping function as input that constructs the resulting object from the provided components.<br>
	 *
	 * @param codec1 The first codec
	 * @param codec2 The second codec
	 * @return The created codec grouper
	 * @param <CI1> The type of the first component
	 * @param <CI2> The type of the second component
	 * @param <O> The type of the resulting object the created codec will produce
	 * @throws NullPointerException If any of the provided codecs is null
	 * @see CodecCreator
	 * @see CodecGroup
	 * @see CodecBuilderFunction2
	 */
	public static <CI1, CI2, O> @NotNull CodecCreator<O, CodecBuilderFunction2<CI1, CI2, O>> group(
		@NotNull ConfiguredCodec<CI1, O> codec1,
		@NotNull ConfiguredCodec<CI2, O> codec2
	) {
		List<ConfiguredCodec<?, O>> codecs = List.of(
			codec1, codec2
		);
		
		ensureNoElementIsNull(codecs);
		return new CodecCreator<>(codecs);
	}
	
	/**
	 * Creates a codec creator containing the provided codec.<br>
	 * The codec creator can be used to create the resulting codec by calling the {@link CodecCreator#create(CodecBuilderFunction)} method of the returned creator.<br>
	 * The {@code create} requires a grouping function as input that constructs the resulting object from the provided components.<br>
	 *
	 * @param codec1 The first codec
	 * @param codec2 The second codec
	 * @param codec3 The third codec
	 * @return The created codec grouper
	 * @param <CI1> The type of the first component
	 * @param <CI2> The type of the second component
	 * @param <CI3> The type of the third component
	 * @param <O> The type of the resulting object the created codec will produce
	 * @throws NullPointerException If any of the provided codecs is null
	 * @see CodecCreator
	 * @see CodecGroup
	 * @see CodecBuilderFunction3
	 */
	public static <CI1, CI2, CI3, O> @NotNull CodecCreator<O, CodecBuilderFunction3<CI1, CI2, CI3, O>> group(
		@NotNull ConfiguredCodec<CI1, O> codec1,
		@NotNull ConfiguredCodec<CI2, O> codec2,
		@NotNull ConfiguredCodec<CI3, O> codec3
	) {
		List<ConfiguredCodec<?, O>> codecs = List.of(
			codec1, codec2, codec3
		);
		
		ensureNoElementIsNull(codecs);
		return new CodecCreator<>(codecs);
	}
	
	/**
	 * Creates a codec creator containing the provided codec.<br>
	 * The codec creator can be used to create the resulting codec by calling the {@link CodecCreator#create(CodecBuilderFunction)} method of the returned creator.<br>
	 * The {@code create} requires a grouping function as input that constructs the resulting object from the provided components.<br>
	 *
	 * @param codec1 The first codec
	 * @param codec2 The second codec
	 * @param codec3 The third codec
	 * @param codec4 The fourth codec
	 * @return The created codec grouper
	 * @param <CI1> The type of the first component
	 * @param <CI2> The type of the second component
	 * @param <CI3> The type of the third component
	 * @param <CI4> The type of the fourth component
	 * @param <O> The type of the resulting object the created codec will produce
	 * @throws NullPointerException If any of the provided codecs is null
	 * @see CodecCreator
	 * @see CodecGroup
	 * @see CodecBuilderFunction4
	 */
	public static <CI1, CI2, CI3, CI4, O> @NotNull CodecCreator<O, CodecBuilderFunction4<CI1, CI2, CI3, CI4, O>> group(
		@NotNull ConfiguredCodec<CI1, O> codec1,
		@NotNull ConfiguredCodec<CI2, O> codec2,
		@NotNull ConfiguredCodec<CI3, O> codec3,
		@NotNull ConfiguredCodec<CI4, O> codec4
	) {
		List<ConfiguredCodec<?, O>> codecs = List.of(
			codec1, codec2, codec3, codec4
		);
		
		ensureNoElementIsNull(codecs);
		return new CodecCreator<>(codecs);
	}
	
	/**
	 * Creates a codec creator containing the provided codec.<br>
	 * The codec creator can be used to create the resulting codec by calling the {@link CodecCreator#create(CodecBuilderFunction)} method of the returned creator.<br>
	 * The {@code create} requires a grouping function as input that constructs the resulting object from the provided components.<br>
	 *
	 * @param codec1 The first codec
	 * @param codec2 The second codec
	 * @param codec3 The third codec
	 * @param codec4 The fourth codec
	 * @param codec5 The fifth codec
	 * @return The created codec grouper
	 * @param <CI1> The type of the first component
	 * @param <CI2> The type of the second component
	 * @param <CI3> The type of the third component
	 * @param <CI4> The type of the fourth component
	 * @param <CI5> The type of the fifth component
	 * @param <O> The type of the resulting object the created codec will produce
	 * @throws NullPointerException If any of the provided codecs is null
	 * @see CodecCreator
	 * @see CodecGroup
	 * @see CodecBuilderFunction5
	 */
	public static <CI1, CI2, CI3, CI4, CI5, O> @NotNull CodecCreator<O, CodecBuilderFunction5<CI1, CI2, CI3, CI4, CI5, O>> group(
		@NotNull ConfiguredCodec<CI1, O> codec1,
		@NotNull ConfiguredCodec<CI2, O> codec2,
		@NotNull ConfiguredCodec<CI3, O> codec3,
		@NotNull ConfiguredCodec<CI4, O> codec4,
		@NotNull ConfiguredCodec<CI5, O> codec5
	) {
		List<ConfiguredCodec<?, O>> codecs = List.of(
			codec1, codec2, codec3, codec4, codec5
		);
		
		ensureNoElementIsNull(codecs);
		return new CodecCreator<>(codecs);
	}
	
	/**
	 * Creates a codec creator containing the provided codec.<br>
	 * The codec creator can be used to create the resulting codec by calling the {@link CodecCreator#create(CodecBuilderFunction)} method of the returned creator.<br>
	 * The {@code create} requires a grouping function as input that constructs the resulting object from the provided components.<br>
	 *
	 * @param codec1 The first codec
	 * @param codec2 The second codec
	 * @param codec3 The third codec
	 * @param codec4 The fourth codec
	 * @param codec5 The fifth codec
	 * @param codec6 The sixth codec
	 * @return The created codec grouper
	 * @param <CI1> The type of the first component
	 * @param <CI2> The type of the second component
	 * @param <CI3> The type of the third component
	 * @param <CI4> The type of the fourth component
	 * @param <CI5> The type of the fifth component
	 * @param <CI6> The type of the sixth component
	 * @param <O> The type of the resulting object the created codec will produce
	 * @throws NullPointerException If any of the provided codecs is null
	 * @see CodecCreator
	 * @see CodecGroup
	 * @see CodecBuilderFunction6
	 */
	public static <CI1, CI2, CI3, CI4, CI5, CI6, O> @NotNull CodecCreator<O, CodecBuilderFunction6<CI1, CI2, CI3, CI4, CI5, CI6, O>> group(
		@NotNull ConfiguredCodec<CI1, O> codec1,
		@NotNull ConfiguredCodec<CI2, O> codec2,
		@NotNull ConfiguredCodec<CI3, O> codec3,
		@NotNull ConfiguredCodec<CI4, O> codec4,
		@NotNull ConfiguredCodec<CI5, O> codec5,
		@NotNull ConfiguredCodec<CI6, O> codec6
	) {
		List<ConfiguredCodec<?, O>> codecs = List.of(
			codec1, codec2, codec3, codec4, codec5, codec6
		);
		
		ensureNoElementIsNull(codecs);
		return new CodecCreator<>(codecs);
	}
	
	/**
	 * Creates a codec creator containing the provided codec.<br>
	 * The codec creator can be used to create the resulting codec by calling the {@link CodecCreator#create(CodecBuilderFunction)} method of the returned creator.<br>
	 * The {@code create} requires a grouping function as input that constructs the resulting object from the provided components.<br>
	 *
	 * @param codec1 The first codec
	 * @param codec2 The second codec
	 * @param codec3 The third codec
	 * @param codec4 The fourth codec
	 * @param codec5 The fifth codec
	 * @param codec6 The sixth codec
	 * @param codec7 The seventh codec
	 * @return The created codec grouper
	 * @param <CI1> The type of the first component
	 * @param <CI2> The type of the second component
	 * @param <CI3> The type of the third component
	 * @param <CI4> The type of the fourth component
	 * @param <CI5> The type of the fifth component
	 * @param <CI6> The type of the sixth component
	 * @param <CI7> The type of the seventh component
	 * @param <O> The type of the resulting object the created codec will produce
	 * @throws NullPointerException If any of the provided codecs is null
	 * @see CodecCreator
	 * @see CodecGroup
	 * @see CodecBuilderFunction7
	 */
	public static <CI1, CI2, CI3, CI4, CI5, CI6, CI7, O> @NotNull CodecCreator<O, CodecBuilderFunction7<CI1, CI2, CI3, CI4, CI5, CI6, CI7, O>> group(
		@NotNull ConfiguredCodec<CI1, O> codec1,
		@NotNull ConfiguredCodec<CI2, O> codec2,
		@NotNull ConfiguredCodec<CI3, O> codec3,
		@NotNull ConfiguredCodec<CI4, O> codec4,
		@NotNull ConfiguredCodec<CI5, O> codec5,
		@NotNull ConfiguredCodec<CI6, O> codec6,
		@NotNull ConfiguredCodec<CI7, O> codec7
	) {
		List<ConfiguredCodec<?, O>> codecs = List.of(
			codec1, codec2, codec3, codec4, codec5, codec6, codec7
		);
		
		ensureNoElementIsNull(codecs);
		return new CodecCreator<>(codecs);
	}
	
	/**
	 * Creates a codec creator containing the provided codec.<br>
	 * The codec creator can be used to create the resulting codec by calling the {@link CodecCreator#create(CodecBuilderFunction)} method of the returned creator.<br>
	 * The {@code create} requires a grouping function as input that constructs the resulting object from the provided components.<br>
	 *
	 * @param codec1 The first codec
	 * @param codec2 The second codec
	 * @param codec3 The third codec
	 * @param codec4 The fourth codec
	 * @param codec5 The fifth codec
	 * @param codec6 The sixth codec
	 * @param codec7 The seventh codec
	 * @param codec8 The eighth codec
	 * @return The created codec grouper
	 * @param <CI1> The type of the first component
	 * @param <CI2> The type of the second component
	 * @param <CI3> The type of the third component
	 * @param <CI4> The type of the fourth component
	 * @param <CI5> The type of the fifth component
	 * @param <CI6> The type of the sixth component
	 * @param <CI7> The type of the seventh component
	 * @param <CI8> The type of the eighth component
	 * @param <O> The type of the resulting object the created codec will produce
	 * @throws NullPointerException If any of the provided codecs is null
	 * @see CodecCreator
	 * @see CodecGroup
	 * @see CodecBuilderFunction8
	 */
	public static <CI1, CI2, CI3, CI4, CI5, CI6, CI7, CI8, O> @NotNull CodecCreator<O, CodecBuilderFunction8<CI1, CI2, CI3, CI4, CI5, CI6, CI7, CI8, O>> group(
		@NotNull ConfiguredCodec<CI1, O> codec1,
		@NotNull ConfiguredCodec<CI2, O> codec2,
		@NotNull ConfiguredCodec<CI3, O> codec3,
		@NotNull ConfiguredCodec<CI4, O> codec4,
		@NotNull ConfiguredCodec<CI5, O> codec5,
		@NotNull ConfiguredCodec<CI6, O> codec6,
		@NotNull ConfiguredCodec<CI7, O> codec7,
		@NotNull ConfiguredCodec<CI8, O> codec8
	) {
		List<ConfiguredCodec<?, O>> codecs = List.of(
			codec1, codec2, codec3, codec4, codec5, codec6, codec7, codec8
		);
		
		ensureNoElementIsNull(codecs);
		return new CodecCreator<>(codecs);
	}
	
	/**
	 * Creates a codec creator containing the provided codec.<br>
	 * The codec creator can be used to create the resulting codec by calling the {@link CodecCreator#create(CodecBuilderFunction)} method of the returned creator.<br>
	 * The {@code create} requires a grouping function as input that constructs the resulting object from the provided components.<br>
	 *
	 * @param codec1 The first codec
	 * @param codec2 The second codec
	 * @param codec3 The third codec
	 * @param codec4 The fourth codec
	 * @param codec5 The fifth codec
	 * @param codec6 The sixth codec
	 * @param codec7 The seventh codec
	 * @param codec8 The eighth codec
	 * @param codec9 The ninth codec
	 * @return The created codec grouper
	 * @param <CI1> The type of the first component
	 * @param <CI2> The type of the second component
	 * @param <CI3> The type of the third component
	 * @param <CI4> The type of the fourth component
	 * @param <CI5> The type of the fifth component
	 * @param <CI6> The type of the sixth component
	 * @param <CI7> The type of the seventh component
	 * @param <CI8> The type of the eighth component
	 * @param <CI9> The type of the ninth component
	 * @param <O> The type of the resulting object the created codec will produce
	 * @throws NullPointerException If any of the provided codecs is null
	 * @see CodecCreator
	 * @see CodecGroup
	 * @see CodecBuilderFunction9
	 */
	public static <CI1, CI2, CI3, CI4, CI5, CI6, CI7, CI8, CI9, O> @NotNull CodecCreator<O, CodecBuilderFunction9<CI1, CI2, CI3, CI4, CI5, CI6, CI7, CI8, CI9, O>> group(
		@NotNull ConfiguredCodec<CI1, O> codec1,
		@NotNull ConfiguredCodec<CI2, O> codec2,
		@NotNull ConfiguredCodec<CI3, O> codec3,
		@NotNull ConfiguredCodec<CI4, O> codec4,
		@NotNull ConfiguredCodec<CI5, O> codec5,
		@NotNull ConfiguredCodec<CI6, O> codec6,
		@NotNull ConfiguredCodec<CI7, O> codec7,
		@NotNull ConfiguredCodec<CI8, O> codec8,
		@NotNull ConfiguredCodec<CI9, O> codec9
	) {
		List<ConfiguredCodec<?, O>> codecs = List.of(
			codec1, codec2, codec3, codec4, codec5, codec6, codec7, codec8, codec9
		);
		
		ensureNoElementIsNull(codecs);
		return new CodecCreator<>(codecs);
	}
	
	/**
	 * Creates a codec creator containing the provided codec.<br>
	 * The codec creator can be used to create the resulting codec by calling the {@link CodecCreator#create(CodecBuilderFunction)} method of the returned creator.<br>
	 * The {@code create} requires a grouping function as input that constructs the resulting object from the provided components.<br>
	 *
	 * @param codec1 The first codec
	 * @param codec2 The second codec
	 * @param codec3 The third codec
	 * @param codec4 The fourth codec
	 * @param codec5 The fifth codec
	 * @param codec6 The sixth codec
	 * @param codec7 The seventh codec
	 * @param codec8 The eighth codec
	 * @param codec9 The ninth codec
	 * @param codec10 The tenth codec
	 * @return The created codec grouper
	 * @param <CI1> The type of the first component
	 * @param <CI2> The type of the second component
	 * @param <CI3> The type of the third component
	 * @param <CI4> The type of the fourth component
	 * @param <CI5> The type of the fifth component
	 * @param <CI6> The type of the sixth component
	 * @param <CI7> The type of the seventh component
	 * @param <CI8> The type of the eighth component
	 * @param <CI9> The type of the ninth component
	 * @param <CI10> The type of the tenth component
	 * @param <O> The type of the resulting object the created codec will produce
	 * @throws NullPointerException If any of the provided codecs is null
	 * @see CodecCreator
	 * @see CodecGroup
	 * @see CodecBuilderFunction10
	 */
	public static <CI1, CI2, CI3, CI4, CI5, CI6, CI7, CI8, CI9, CI10, O> @NotNull CodecCreator<O, CodecBuilderFunction10<CI1, CI2, CI3, CI4, CI5, CI6, CI7, CI8, CI9, CI10, O>> group(
		@NotNull ConfiguredCodec<CI1, O> codec1,
		@NotNull ConfiguredCodec<CI2, O> codec2,
		@NotNull ConfiguredCodec<CI3, O> codec3,
		@NotNull ConfiguredCodec<CI4, O> codec4,
		@NotNull ConfiguredCodec<CI5, O> codec5,
		@NotNull ConfiguredCodec<CI6, O> codec6,
		@NotNull ConfiguredCodec<CI7, O> codec7,
		@NotNull ConfiguredCodec<CI8, O> codec8,
		@NotNull ConfiguredCodec<CI9, O> codec9,
		@NotNull ConfiguredCodec<CI10, O> codec10
	) {
		List<ConfiguredCodec<?, O>> codecs = List.of(
			codec1, codec2, codec3, codec4, codec5, codec6, codec7, codec8, codec9, codec10
		);
		
		ensureNoElementIsNull(codecs);
		return new CodecCreator<>(codecs);
	}
	
	/**
	 * Creates a codec creator containing the provided codec.<br>
	 * The codec creator can be used to create the resulting codec by calling the {@link CodecCreator#create(CodecBuilderFunction)} method of the returned creator.<br>
	 * The {@code create} requires a grouping function as input that constructs the resulting object from the provided components.<br>
	 *
	 * @param codec1 The first codec
	 * @param codec2 The second codec
	 * @param codec3 The third codec
	 * @param codec4 The fourth codec
	 * @param codec5 The fifth codec
	 * @param codec6 The sixth codec
	 * @param codec7 The seventh codec
	 * @param codec8 The eighth codec
	 * @param codec9 The ninth codec
	 * @param codec10 The tenth codec
	 * @param codec11 The eleventh codec
	 * @return The created codec grouper
	 * @param <CI1> The type of the first component
	 * @param <CI2> The type of the second component
	 * @param <CI3> The type of the third component
	 * @param <CI4> The type of the fourth component
	 * @param <CI5> The type of the fifth component
	 * @param <CI6> The type of the sixth component
	 * @param <CI7> The type of the seventh component
	 * @param <CI8> The type of the eighth component
	 * @param <CI9> The type of the ninth component
	 * @param <CI10> The type of the tenth component
	 * @param <CI11> The type of the eleventh component
	 * @param <O> The type of the resulting object the created codec will produce
	 * @throws NullPointerException If any of the provided codecs is null
	 * @see CodecCreator
	 * @see CodecGroup
	 * @see CodecBuilderFunction11
	 */
	public static <CI1, CI2, CI3, CI4, CI5, CI6, CI7, CI8, CI9, CI10, CI11, O> @NotNull CodecCreator<O, CodecBuilderFunction11<CI1, CI2, CI3, CI4, CI5, CI6, CI7, CI8, CI9, CI10, CI11, O>> group(
		@NotNull ConfiguredCodec<CI1, O> codec1,
		@NotNull ConfiguredCodec<CI2, O> codec2,
		@NotNull ConfiguredCodec<CI3, O> codec3,
		@NotNull ConfiguredCodec<CI4, O> codec4,
		@NotNull ConfiguredCodec<CI5, O> codec5,
		@NotNull ConfiguredCodec<CI6, O> codec6,
		@NotNull ConfiguredCodec<CI7, O> codec7,
		@NotNull ConfiguredCodec<CI8, O> codec8,
		@NotNull ConfiguredCodec<CI9, O> codec9,
		@NotNull ConfiguredCodec<CI10, O> codec10,
		@NotNull ConfiguredCodec<CI11, O> codec11
	) {
		List<ConfiguredCodec<?, O>> codecs = List.of(
			codec1, codec2, codec3, codec4, codec5, codec6, codec7, codec8, codec9, codec10, codec11
		);
		
		ensureNoElementIsNull(codecs);
		return new CodecCreator<>(codecs);
	}
	
	/**
	 * Creates a codec creator containing the provided codec.<br>
	 * The codec creator can be used to create the resulting codec by calling the {@link CodecCreator#create(CodecBuilderFunction)} method of the returned creator.<br>
	 * The {@code create} requires a grouping function as input that constructs the resulting object from the provided components.<br>
	 *
	 * @param codec1 The first codec
	 * @param codec2 The second codec
	 * @param codec3 The third codec
	 * @param codec4 The fourth codec
	 * @param codec5 The fifth codec
	 * @param codec6 The sixth codec
	 * @param codec7 The seventh codec
	 * @param codec8 The eighth codec
	 * @param codec9 The ninth codec
	 * @param codec10 The tenth codec
	 * @param codec11 The eleventh codec
	 * @param codec12 The twelfth codec
	 * @return The created codec grouper
	 * @param <CI1> The type of the first component
	 * @param <CI2> The type of the second component
	 * @param <CI3> The type of the third component
	 * @param <CI4> The type of the fourth component
	 * @param <CI5> The type of the fifth component
	 * @param <CI6> The type of the sixth component
	 * @param <CI7> The type of the seventh component
	 * @param <CI8> The type of the eighth component
	 * @param <CI9> The type of the ninth component
	 * @param <CI10> The type of the tenth component
	 * @param <CI11> The type of the eleventh component
	 * @param <CI12> The type of the twelfth component
	 * @param <O> The type of the resulting object the created codec will produce
	 * @throws NullPointerException If any of the provided codecs is null
	 * @see CodecCreator
	 * @see CodecGroup
	 * @see CodecBuilderFunction12
	 */
	public static <CI1, CI2, CI3, CI4, CI5, CI6, CI7, CI8, CI9, CI10, CI11, CI12, O> @NotNull CodecCreator<O, CodecBuilderFunction12<CI1, CI2, CI3, CI4, CI5, CI6, CI7, CI8, CI9, CI10, CI11, CI12, O>> group(
		@NotNull ConfiguredCodec<CI1, O> codec1,
		@NotNull ConfiguredCodec<CI2, O> codec2,
		@NotNull ConfiguredCodec<CI3, O> codec3,
		@NotNull ConfiguredCodec<CI4, O> codec4,
		@NotNull ConfiguredCodec<CI5, O> codec5,
		@NotNull ConfiguredCodec<CI6, O> codec6,
		@NotNull ConfiguredCodec<CI7, O> codec7,
		@NotNull ConfiguredCodec<CI8, O> codec8,
		@NotNull ConfiguredCodec<CI9, O> codec9,
		@NotNull ConfiguredCodec<CI10, O> codec10,
		@NotNull ConfiguredCodec<CI11, O> codec11,
		@NotNull ConfiguredCodec<CI12, O> codec12
	) {
		List<ConfiguredCodec<?, O>> codecs = List.of(
			codec1, codec2, codec3, codec4, codec5, codec6, codec7, codec8, codec9, codec10, codec11, codec12
		);
		
		ensureNoElementIsNull(codecs);
		return new CodecCreator<>(codecs);
	}
	
	/**
	 * Creates a codec creator containing the provided codec.<br>
	 * The codec creator can be used to create the resulting codec by calling the {@link CodecCreator#create(CodecBuilderFunction)} method of the returned creator.<br>
	 * The {@code create} requires a grouping function as input that constructs the resulting object from the provided components.<br>
	 *
	 * @param codec1 The first codec
	 * @param codec2 The second codec
	 * @param codec3 The third codec
	 * @param codec4 The fourth codec
	 * @param codec5 The fifth codec
	 * @param codec6 The sixth codec
	 * @param codec7 The seventh codec
	 * @param codec8 The eighth codec
	 * @param codec9 The ninth codec
	 * @param codec10 The tenth codec
	 * @param codec11 The eleventh codec
	 * @param codec12 The twelfth codec
	 * @param codec13 The thirteenth codec
	 * @return The created codec grouper
	 * @param <CI1> The type of the first component
	 * @param <CI2> The type of the second component
	 * @param <CI3> The type of the third component
	 * @param <CI4> The type of the fourth component
	 * @param <CI5> The type of the fifth component
	 * @param <CI6> The type of the sixth component
	 * @param <CI7> The type of the seventh component
	 * @param <CI8> The type of the eighth component
	 * @param <CI9> The type of the ninth component
	 * @param <CI10> The type of the tenth component
	 * @param <CI11> The type of the eleventh component
	 * @param <CI12> The type of the twelfth component
	 * @param <CI13> The type of the thirteenth component
	 * @param <O> The type of the resulting object the created codec will produce
	 * @throws NullPointerException If any of the provided codecs is null
	 * @see CodecCreator
	 * @see CodecGroup
	 * @see CodecBuilderFunction13
	 */
	public static <CI1, CI2, CI3, CI4, CI5, CI6, CI7, CI8, CI9, CI10, CI11, CI12, CI13, O> @NotNull CodecCreator<O, CodecBuilderFunction13<CI1, CI2, CI3, CI4, CI5, CI6, CI7, CI8, CI9, CI10, CI11, CI12, CI13, O>> group(
		@NotNull ConfiguredCodec<CI1, O> codec1,
		@NotNull ConfiguredCodec<CI2, O> codec2,
		@NotNull ConfiguredCodec<CI3, O> codec3,
		@NotNull ConfiguredCodec<CI4, O> codec4,
		@NotNull ConfiguredCodec<CI5, O> codec5,
		@NotNull ConfiguredCodec<CI6, O> codec6,
		@NotNull ConfiguredCodec<CI7, O> codec7,
		@NotNull ConfiguredCodec<CI8, O> codec8,
		@NotNull ConfiguredCodec<CI9, O> codec9,
		@NotNull ConfiguredCodec<CI10, O> codec10,
		@NotNull ConfiguredCodec<CI11, O> codec11,
		@NotNull ConfiguredCodec<CI12, O> codec12,
		@NotNull ConfiguredCodec<CI13, O> codec13
	) {
		List<ConfiguredCodec<?, O>> codecs = List.of(
			codec1, codec2, codec3, codec4, codec5, codec6, codec7, codec8, codec9, codec10, codec11, codec12, codec13
		);
		
		ensureNoElementIsNull(codecs);
		return new CodecCreator<>(codecs);
	}
	
	/**
	 * Creates a codec creator containing the provided codec.<br>
	 * The codec creator can be used to create the resulting codec by calling the {@link CodecCreator#create(CodecBuilderFunction)} method of the returned creator.<br>
	 * The {@code create} requires a grouping function as input that constructs the resulting object from the provided components.<br>
	 *
	 * @param codec1 The first codec
	 * @param codec2 The second codec
	 * @param codec3 The third codec
	 * @param codec4 The fourth codec
	 * @param codec5 The fifth codec
	 * @param codec6 The sixth codec
	 * @param codec7 The seventh codec
	 * @param codec8 The eighth codec
	 * @param codec9 The ninth codec
	 * @param codec10 The tenth codec
	 * @param codec11 The eleventh codec
	 * @param codec12 The twelfth codec
	 * @param codec13 The thirteenth codec
	 * @param codec14 The fourteenth codec
	 * @return The created codec grouper
	 * @param <CI1> The type of the first component
	 * @param <CI2> The type of the second component
	 * @param <CI3> The type of the third component
	 * @param <CI4> The type of the fourth component
	 * @param <CI5> The type of the fifth component
	 * @param <CI6> The type of the sixth component
	 * @param <CI7> The type of the seventh component
	 * @param <CI8> The type of the eighth component
	 * @param <CI9> The type of the ninth component
	 * @param <CI10> The type of the tenth component
	 * @param <CI11> The type of the eleventh component
	 * @param <CI12> The type of the twelfth component
	 * @param <CI13> The type of the thirteenth component
	 * @param <CI14> The type of the fourteenth component
	 * @param <O> The type of the resulting object the created codec will produce
	 * @throws NullPointerException If any of the provided codecs is null
	 * @see CodecCreator
	 * @see CodecGroup
	 * @see CodecBuilderFunction14
	 */
	public static <CI1, CI2, CI3, CI4, CI5, CI6, CI7, CI8, CI9, CI10, CI11, CI12, CI13, CI14, O> @NotNull CodecCreator<O, CodecBuilderFunction14<CI1, CI2, CI3, CI4, CI5, CI6, CI7, CI8, CI9, CI10, CI11, CI12, CI13, CI14, O>> group(
		@NotNull ConfiguredCodec<CI1, O> codec1,
		@NotNull ConfiguredCodec<CI2, O> codec2,
		@NotNull ConfiguredCodec<CI3, O> codec3,
		@NotNull ConfiguredCodec<CI4, O> codec4,
		@NotNull ConfiguredCodec<CI5, O> codec5,
		@NotNull ConfiguredCodec<CI6, O> codec6,
		@NotNull ConfiguredCodec<CI7, O> codec7,
		@NotNull ConfiguredCodec<CI8, O> codec8,
		@NotNull ConfiguredCodec<CI9, O> codec9,
		@NotNull ConfiguredCodec<CI10, O> codec10,
		@NotNull ConfiguredCodec<CI11, O> codec11,
		@NotNull ConfiguredCodec<CI12, O> codec12,
		@NotNull ConfiguredCodec<CI13, O> codec13,
		@NotNull ConfiguredCodec<CI14, O> codec14
	) {
		List<ConfiguredCodec<?, O>> codecs = List.of(
			codec1, codec2, codec3, codec4, codec5, codec6, codec7, codec8, codec9, codec10, codec11, codec12, codec13, codec14
		);
		
		ensureNoElementIsNull(codecs);
		return new CodecCreator<>(codecs);
	}
	
	/**
	 * Creates a codec creator containing the provided codec.<br>
	 * The codec creator can be used to create the resulting codec by calling the {@link CodecCreator#create(CodecBuilderFunction)} method of the returned creator.<br>
	 * The {@code create} requires a grouping function as input that constructs the resulting object from the provided components.<br>
	 *
	 * @param codec1 The first codec
	 * @param codec2 The second codec
	 * @param codec3 The third codec
	 * @param codec4 The fourth codec
	 * @param codec5 The fifth codec
	 * @param codec6 The sixth codec
	 * @param codec7 The seventh codec
	 * @param codec8 The eighth codec
	 * @param codec9 The ninth codec
	 * @param codec10 The tenth codec
	 * @param codec11 The eleventh codec
	 * @param codec12 The twelfth codec
	 * @param codec13 The thirteenth codec
	 * @param codec14 The fourteenth codec
	 * @param codec15 The fifteenth codec
	 * @return The created codec grouper
	 * @param <CI1> The type of the first component
	 * @param <CI2> The type of the second component
	 * @param <CI3> The type of the third component
	 * @param <CI4> The type of the fourth component
	 * @param <CI5> The type of the fifth component
	 * @param <CI6> The type of the sixth component
	 * @param <CI7> The type of the seventh component
	 * @param <CI8> The type of the eighth component
	 * @param <CI9> The type of the ninth component
	 * @param <CI10> The type of the tenth component
	 * @param <CI11> The type of the eleventh component
	 * @param <CI12> The type of the twelfth component
	 * @param <CI13> The type of the thirteenth component
	 * @param <CI14> The type of the fourteenth component
	 * @param <CI15> The type of the fifteenth component
	 * @param <O> The type of the resulting object the created codec will produce
	 * @throws NullPointerException If any of the provided codecs is null
	 * @see CodecCreator
	 * @see CodecGroup
	 * @see CodecBuilderFunction15
	 */
	public static <CI1, CI2, CI3, CI4, CI5, CI6, CI7, CI8, CI9, CI10, CI11, CI12, CI13, CI14, CI15, O> @NotNull CodecCreator<O, CodecBuilderFunction15<CI1, CI2, CI3, CI4, CI5, CI6, CI7, CI8, CI9, CI10, CI11, CI12, CI13, CI14, CI15, O>> group(
		@NotNull ConfiguredCodec<CI1, O> codec1,
		@NotNull ConfiguredCodec<CI2, O> codec2,
		@NotNull ConfiguredCodec<CI3, O> codec3,
		@NotNull ConfiguredCodec<CI4, O> codec4,
		@NotNull ConfiguredCodec<CI5, O> codec5,
		@NotNull ConfiguredCodec<CI6, O> codec6,
		@NotNull ConfiguredCodec<CI7, O> codec7,
		@NotNull ConfiguredCodec<CI8, O> codec8,
		@NotNull ConfiguredCodec<CI9, O> codec9,
		@NotNull ConfiguredCodec<CI10, O> codec10,
		@NotNull ConfiguredCodec<CI11, O> codec11,
		@NotNull ConfiguredCodec<CI12, O> codec12,
		@NotNull ConfiguredCodec<CI13, O> codec13,
		@NotNull ConfiguredCodec<CI14, O> codec14,
		@NotNull ConfiguredCodec<CI15, O> codec15
	) {
		List<ConfiguredCodec<?, O>> codecs = List.of(
			codec1, codec2, codec3, codec4, codec5, codec6, codec7, codec8, codec9, codec10, codec11, codec12, codec13, codec14, codec15
		);
		
		ensureNoElementIsNull(codecs);
		return new CodecCreator<>(codecs);
	}
	
	/**
	 * Creates a codec creator containing the provided codec.<br>
	 * The codec creator can be used to create the resulting codec by calling the {@link CodecCreator#create(CodecBuilderFunction)} method of the returned creator.<br>
	 * The {@code create} requires a grouping function as input that constructs the resulting object from the provided components.<br>
	 *
	 * @param codec1 The first codec
	 * @param codec2 The second codec
	 * @param codec3 The third codec
	 * @param codec4 The fourth codec
	 * @param codec5 The fifth codec
	 * @param codec6 The sixth codec
	 * @param codec7 The seventh codec
	 * @param codec8 The eighth codec
	 * @param codec9 The ninth codec
	 * @param codec10 The tenth codec
	 * @param codec11 The eleventh codec
	 * @param codec12 The twelfth codec
	 * @param codec13 The thirteenth codec
	 * @param codec14 The fourteenth codec
	 * @param codec15 The fifteenth codec
	 * @param codec16 The sixteenth codec
	 * @return The created codec grouper
	 * @param <CI1> The type of the first component
	 * @param <CI2> The type of the second component
	 * @param <CI3> The type of the third component
	 * @param <CI4> The type of the fourth component
	 * @param <CI5> The type of the fifth component
	 * @param <CI6> The type of the sixth component
	 * @param <CI7> The type of the seventh component
	 * @param <CI8> The type of the eighth component
	 * @param <CI9> The type of the ninth component
	 * @param <CI10> The type of the tenth component
	 * @param <CI11> The type of the eleventh component
	 * @param <CI12> The type of the twelfth component
	 * @param <CI13> The type of the thirteenth component
	 * @param <CI14> The type of the fourteenth component
	 * @param <CI15> The type of the fifteenth component
	 * @param <CI16> The type of the sixteenth component
	 * @param <O> The type of the resulting object the created codec will produce
	 * @throws NullPointerException If any of the provided codecs is null
	 * @see CodecCreator
	 * @see CodecGroup
	 * @see CodecBuilderFunction16
	 */
	public static <CI1, CI2, CI3, CI4, CI5, CI6, CI7, CI8, CI9, CI10, CI11, CI12, CI13, CI14, CI15, CI16, O> @NotNull CodecCreator<O, CodecBuilderFunction16<CI1, CI2, CI3, CI4, CI5, CI6, CI7, CI8, CI9, CI10, CI11, CI12, CI13, CI14, CI15, CI16, O>> group(
		@NotNull ConfiguredCodec<CI1, O> codec1,
		@NotNull ConfiguredCodec<CI2, O> codec2,
		@NotNull ConfiguredCodec<CI3, O> codec3,
		@NotNull ConfiguredCodec<CI4, O> codec4,
		@NotNull ConfiguredCodec<CI5, O> codec5,
		@NotNull ConfiguredCodec<CI6, O> codec6,
		@NotNull ConfiguredCodec<CI7, O> codec7,
		@NotNull ConfiguredCodec<CI8, O> codec8,
		@NotNull ConfiguredCodec<CI9, O> codec9,
		@NotNull ConfiguredCodec<CI10, O> codec10,
		@NotNull ConfiguredCodec<CI11, O> codec11,
		@NotNull ConfiguredCodec<CI12, O> codec12,
		@NotNull ConfiguredCodec<CI13, O> codec13,
		@NotNull ConfiguredCodec<CI14, O> codec14,
		@NotNull ConfiguredCodec<CI15, O> codec15,
		@NotNull ConfiguredCodec<CI16, O> codec16
	) {
		List<ConfiguredCodec<?, O>> codecs = List.of(
			codec1, codec2, codec3, codec4, codec5, codec6, codec7, codec8, codec9, codec10, codec11, codec12, codec13, codec14, codec15, codec16
		);
		
		ensureNoElementIsNull(codecs);
		return new CodecCreator<>(codecs);
	}
	
	/**
	 * Ensures that no element in the provided list is null.<br>
	 * Used by the group methods to ensure that all provided codecs are not null.<br>
	 * @param list The list to check for null elements
	 * @throws NullPointerException If any element in the list is null
	 */
	private static void ensureNoElementIsNull(@NotNull List<?> list) {
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i) == null) {
				Objects.requireNonNull(list.get(i), "Codec of component " + i + " must not be null");
			}
		}
	}
}
