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

package net.luis.utils.io.codec.types.struct;

import net.luis.utils.io.codec.AbstractCodec;
import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.provider.TypeProvider;
import net.luis.utils.util.result.Result;
import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A codec for encoding and decoding recursive data structures.<br>
 * This codec allows defining codecs for types that reference themselves, such as trees or linked lists.<br>
 * <p>
 *     The recursive codec uses a factory function that receives a codec reference and returns the actual codec implementation.<br>
 *     This allows the codec to reference itself during construction, enabling recursive data structure encoding/decoding.
 * </p>
 * <p>
 *     Example usage for a binary tree node:
 * </p>
 * <pre>{@code
 * record TreeNode(int value, TreeNode left, TreeNode right) {}
 *
 * Codec<TreeNode> treeCodec = Codecs.recursive(self ->
 *     CodecBuilder.of(
 *         Codecs.INTEGER.fieldOf("value", TreeNode::value),
 *         self.nullable().fieldOf("left", TreeNode::left),
 *         self.nullable().fieldOf("right", TreeNode::right)
 *     ).create(TreeNode::new)
 * );
 * }</pre>
 *
 * @author Luis-St
 *
 * @param <C> The type of the recursive value
 */
public class RecursiveCodec<C> extends AbstractCodec<C, Object> {

	/**
	 * Supplier that lazily provides the actual codec.<br>
	 * This is used to break the circular dependency during codec construction.
	 */
	private final Supplier<Codec<C>> codecSupplier;

	/**
	 * Constructs a new recursive codec using the given factory function.<br>
	 * <p>
	 *     The factory function receives a reference to this codec and must return the actual codec implementation.<br>
	 *     This allows the returned codec to reference this recursive codec, enabling recursive structures.
	 * </p>
	 *
	 * @param codecFactory The factory function that creates the actual codec
	 * @throws NullPointerException If the codec factory is null
	 */
	public RecursiveCodec(@NonNull Function<Codec<C>, Codec<C>> codecFactory) {
		Objects.requireNonNull(codecFactory, "Codec factory must not be null");
		
		Mutable<Codec<C>> codecHolder = new MutableObject<>();
		codecHolder.setValue(codecFactory.apply(new LazyCodec<>(codecHolder)));
		this.codecSupplier = codecHolder;
	}

	@Override
	public <R> @NonNull Result<R> encodeStart(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable C value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");

		return this.codecSupplier.get().encodeStart(provider, current, value);
	}

	@Override
	public <R> @NonNull Result<C> decodeStart(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable R value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");

		return this.codecSupplier.get().decodeStart(provider, current, value);
	}

	@Override
	public String toString() {
		return "RecursiveCodec[" + this.codecSupplier.get() + "]";
	}

	/**
	 * Internal lazy codec that delegates to the actual codec once it's initialized.<br>
	 * This is used to break the circular dependency during codec construction.
	 *
	 * @param <C> The type of the value
	 */
	private static final class LazyCodec<C> extends AbstractCodec<C, Object> {

		/**
		 * Supplier that provides the actual codec.<br>
		 */
		private final Supplier<Codec<C>> codecSupplier;

		/**
		 * Constructs a new lazy codec.<br>
		 *
		 * @param codecSupplier The supplier that provides the actual codec
		 */
		private LazyCodec(@NonNull Supplier<Codec<C>> codecSupplier) {
			this.codecSupplier = Objects.requireNonNull(codecSupplier, "Codec supplier must not be null");
		}

		@Override
		public <R> @NonNull Result<R> encodeStart(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable C value) {
			return this.codecSupplier.get().encodeStart(provider, current, value);
		}

		@Override
		public <R> @NonNull Result<C> decodeStart(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable R value) {
			return this.codecSupplier.get().decodeStart(provider, current, value);
		}

		@Override
		public String toString() {
			return "LazyCodec";
		}
	}
}
