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

package net.luis.utils.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Represents one of two possible values.<br>
 * An instance of {@link Either} is either an instance of {@link Either.Left} or {@link Either.Right}.<br>
 * <p>
 *     Sealed to prevent external instantiation and extension.<br>
 * </p>
 *
 * @see Either.Left
 * @see Either.Right
 *
 * @author Luis-St
 *
 * @param <L> The type of the left value
 * @param <R> The type of the right value
 */
public abstract sealed class Either<L, R> permits Either.Left, Either.Right {
	
	//region Static factory methods
	
	/**
	 * Creates a new either instance with the given value as the left value.<br>
	 * @param value The left value
	 * @return The created (left) either instance
	 * @param <L> The type of the left value
	 * @param <R> The type of the right value
	 */
	public static <L, R> @NotNull Either<L, R> left(@Nullable L value) {
		return new Left<>(value);
	}
	
	/**
	 * Creates a new either instance with the given value as the right value.<br>
	 * @param value The right value
	 * @return The created (right) either instance
	 * @param <L> The type of the left value
	 * @param <R> The type of the right value
	 */
	public static <L, R> @NotNull Either<L, R> right(@Nullable R value) {
		return new Right<>(value);
	}
	//endregion
	
	/**
	 * @return True if this either instance is a left instance, false otherwise
	 */
	public abstract boolean isLeft();
	
	/**
	 * @return True if this either instance is a right instance, false otherwise
	 */
	public abstract boolean isRight();
	
	/**
	 * Performs the given action if this either instance is a left instance.<br>
	 * @param action The action to perform
	 * @throws NullPointerException If the action is null
	 */
	public void ifLeft(@NotNull Consumer<? super L> action) {}
	
	/**
	 * Performs the given action if this either instance is a right instance.<br>
	 * @param action The action to perform
	 * @throws NullPointerException If the action is null
	 */
	public void ifRight(@NotNull Consumer<? super R> action) {}
	
	/**
	 * @return The left value as an {@link Optional}
	 */
	public abstract @NotNull Optional<L> left();
	
	/**
	 * @return The right value as an {@link Optional}
	 */
	public abstract @NotNull Optional<R> right();
	
	/**
	 * @return The left value or throws an exception if it is a right either
	 * @throws IllegalStateException If the left value is not present
	 */
	public abstract  L leftOrThrow();
	
	/**
	 * @return The right value or throws an exception if it is a left either
	 * @throws IllegalStateException If the right value is not present
	 */
	public abstract R rightOrThrow();
	
	/**
	 * Swaps the left and right values of this either instance.<br>
	 * @return A new either instance with the swapped values
	 */
	public @NotNull Either<R, L> swap() {
		return this.mapTo(Either::right, Either::left);
	}
	
	/**
	 * Maps the left and right values of this either instance to new values.<br>
	 * @param leftMapper The mapper for the left value
	 * @param rightMapper The mapper for the right value
	 * @return A new either instance with the mapped values
	 * @param <C> The new type of the left value
	 * @param <D> The new type of the right value
	 * @throws NullPointerException If the mapper used in this either implementation is null
	 */
	public abstract <C, D> @NotNull Either<C, D> mapBoth(@NotNull Function<? super L, ? extends C> leftMapper, @NotNull Function<? super R, ? extends D> rightMapper);
	
	/**
	 * Maps the left and right values of this either instance to a new value.<br>
	 * @param leftMapper The mapper for the left value
	 * @param rightMapper The mapper for the right value
	 * @return The mapped value
	 * @param <T> The type of the mapped value
	 * @throws NullPointerException If the mapper used in this either implementation is null
	 */
	public abstract <T> T mapTo(@NotNull Function<? super L, ? extends T> leftMapper, @NotNull Function<? super R, ? extends T> rightMapper);
	
	/**
	 * Maps the left value of this either instance to a new value.<br>
	 * @param mapper The mapper for the left value
	 * @return A new either instance with the mapped left value
	 * @param <T> The new type of the left value
	 * @throws NullPointerException If the mapper is null
	 */
	public <T> Either<T, R> mapLeft(@NotNull Function<? super L, ? extends T> mapper) {
		return this.mapTo((value) -> Either.left(Objects.requireNonNull(mapper, "Mapper must not be null").apply(value)), Either::right);
	}
	
	/**
	 * Maps the right value of this either instance to a new value.<br>
	 * @param mapper The mapper for the right value
	 * @return A new either instance with the mapped right value
	 * @param <T> The new type of the right value
	 * @throws NullPointerException If the mapper is null
	 */
	public <T> Either<L, T> mapRight(@NotNull Function<? super R, ? extends T> mapper) {
		return this.mapTo(Either::left, (value) -> Either.right(Objects.requireNonNull(mapper, "Mapper must not be null").apply(value)));
	}
	
	/**
	 * Left implementation of the either.<br>
	 *
	 * @author Luis-St
	 *
	 * @param <L> The type of the left value
	 * @param <R> The type of the right value
	 */
	static final class Left<L, R> extends Either<L, R> {
		
		//region Left implementation
		
		/**
		 * The value of the left either.<br>
		 */
		private final L value;
		
		/**
		 * Constructs a new left either instance with the given value.<br>
		 * @param value The value of the left either
		 */
		private Left(@Nullable L value) {
			this.value = value;
		}
		
		@Override
		public boolean isLeft() {
			return true;
		}
		
		@Override
		public boolean isRight() {
			return false;
		}
		
		@Override
		public void ifLeft(@NotNull Consumer<? super L> action) {
			Objects.requireNonNull(action, "Action must not be null").accept(this.value);
		}
		
		@Override
		public @NotNull Optional<L> left() {
			return Optional.ofNullable(this.value);
		}
		
		@Override
		public @NotNull Optional<R> right() {
			return Optional.empty();
		}
		
		@Override
		public L leftOrThrow() {
			return this.value;
		}
		
		@Override
		public @NotNull R rightOrThrow() {
			throw new IllegalStateException("Either is a left instance");
		}
		
		@Override
		public <C, D> @NotNull Either<C, D> mapBoth(@NotNull Function<? super L, ? extends C> leftMapper, @NotNull Function<? super R, ? extends D> rightMapper) {
			return new Left<>(Objects.requireNonNull(leftMapper, "Left mapper must nut be null").apply(this.value));
		}
		
		@Override
		public <T> T mapTo(@NotNull Function<? super L, ? extends T> leftMapper, @NotNull Function<? super R, ? extends T> rightMapper) {
			return Objects.requireNonNull(leftMapper, "Left mapper must nut be null").apply(this.value);
		}
		//endregion
		
		//region Object overrides
		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (!(o instanceof Left<?, ?> left)) return false;
			
			return Objects.equals(this.value, left.value);
		}
		
		@Override
		public int hashCode() {
			return Objects.hash(this.value);
		}
		
		@Override
		public String toString() {
			return "(" + this.value + ",)";
		}
		//endregion
	}
	
	/**
	 * Right implementation of the either.<br>
	 *
	 * @author Luis-St
	 *
	 * @param <L> The type of the left value
	 * @param <R> The type of the right value
	 */
	static final class Right<L, R> extends Either<L, R> {
		
		//region Right implementation
		
		/**
		 * The value of the right either.<br>
		 */
		private final R value;
		
		/**
		 * Constructs a new right either instance with the given value.<br>
		 * @param value The value of the right either
		 */
		private Right(@Nullable R value) {
			this.value = value;
		}
		
		@Override
		public boolean isLeft() {
			return false;
		}
		
		@Override
		public boolean isRight() {
			return true;
		}
		
		@Override
		public void ifRight(@NotNull Consumer<? super R> action) {
			Objects.requireNonNull(action, "Action must not be null").accept(this.value);
		}
		
		@Override
		public @NotNull Optional<L> left() {
			return Optional.empty();
		}
		
		@Override
		public @NotNull Optional<R> right() {
			return Optional.ofNullable(this.value);
		}
		
		@Override
		public @NotNull L leftOrThrow() {
			throw new IllegalStateException("Either is a right instance");
		}
		
		@Override
		public R rightOrThrow() {
			return this.value;
		}
		
		@Override
		public <C, D> @NotNull Either<C, D> mapBoth(@NotNull Function<? super L, ? extends C> leftMapper, @NotNull Function<? super R, ? extends D> rightMapper) {
			return new Right<>(Objects.requireNonNull(rightMapper, "Right mapper must nut be null").apply(this.value));
		}
		
		@Override
		public <T> T mapTo(@NotNull Function<? super L, ? extends T> leftMapper, @NotNull Function<? super R, ? extends T> rightMapper) {
			return Objects.requireNonNull(rightMapper, "Right mapper must nut be null").apply(this.value);
		}
		//endregion
		
		//region Object overrides
		@Override
		public String toString() {
			return "(," + this.value + ")";
		}
		
		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (!(o instanceof Right<?, ?> right)) return false;
			
			return Objects.equals(this.value, right.value);
		}
		
		@Override
		public int hashCode() {
			return Objects.hash(this.value);
		}
		//endregion
	}
}
